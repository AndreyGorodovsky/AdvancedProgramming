package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses raw HTTP requests from an InputStream into a RequestInfo object.
 * Functionality preserved: method, URI, protocol, headers, and parameters parsing.
 * Non-blocking for GET requests; reads body only for POST with Content-Length.
 */
public class RequestParser {

    /**
     * Holds parsed HTTP request data.
     */
    public static class RequestInfo {
        private final String method;
        private final String uri;
        private final String protocol;
        private final Map<String, String> headers;
        private final Map<String, String> parameters;
        private final byte[] content;
        private final String httpCommand;
        private final String[] uriSegments;

        public RequestInfo(String method,
                           String uri,
                           String protocol,
                           Map<String, String> headers,
                           Map<String, String> parameters,
                           byte[] content) {
            this.method = method;
            this.uri = uri;
            this.protocol = protocol;
            this.headers = headers;
            this.parameters = parameters;
            this.content = content;
            this.httpCommand = method + " " + uri + " " + protocol;
            this.uriSegments = uri.split("/");
        }

        public String getMethod() { return method; }
        public String getUri() { return uri; }
        public String getProtocol() { return protocol; }
        public Map<String, String> getHeaders() { return headers; }
        public Map<String, String> getParameters() { return parameters; }
        public byte[] getContent() { return content; }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }
    }

    /**
     * Reads and parses the HTTP request from the given InputStream.
     * Only reads a body when the method is POST and a Content-Length header is present.
     */
    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {

        // Parse request line: METHOD URI PROTOCOL
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Empty request line");
        }
        String[] parts = requestLine.split(" ");
        String method = parts[0];
        String uri = parts[1];
        String protocol = parts.length > 2 ? parts[2] : "HTTP/1.1";

        // Read headers
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(':');
            if (idx > 0) {
                String name = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                headers.put(name, value);
            }
        }

        // Separate path and query parameters
        String path = uri;
        Map<String, String> parameters = new HashMap<>();
        int qpos = uri.indexOf('?');
        if (qpos >= 0) {
            path = uri.substring(0, qpos);
            String queryString = uri.substring(qpos + 1);
            for (String pair : queryString.split("&")) {
                int eq = pair.indexOf('=');
                if (eq > 0) {
                    String key = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8.name());
                    String value = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8.name());
                    parameters.put(key, value);
                }
            }
        }

        // Preserve existing functionality: only read body for POST
        byte[] content = new byte[0];
        if ("POST".equalsIgnoreCase(method)) {
            String cl = headers.get("Content-Length");
            if (cl != null) {
                int length = Integer.parseInt(cl);
                char[] buf = new char[length];
                int read = 0;
                while (read < length) {
                    int r = reader.read(buf, read, length - read);
                    if (r == -1) break;
                    read += r;
                }
                String bodyStr = new String(buf);
                content = bodyStr.getBytes(StandardCharsets.UTF_8);

                // If URL-encoded form data, merge into parameters
                String ct = headers.get("Content-Type");
                if (ct != null && ct.startsWith("application/x-www-form-urlencoded")) {
                    for (String pair : bodyStr.split("&")) {
                        int eq = pair.indexOf('=');
                        if (eq > 0) {
                            String key = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8.name());
                            String value = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8.name());
                            parameters.put(key, value);
                        }
                    }
                }
            }
        }

        return new RequestInfo(method, path, protocol, headers, parameters, content);
    }
}
