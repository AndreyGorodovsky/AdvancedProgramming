package server;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

// Simple utility to parse an HTTP request stream into a RequestInfo object.
public class RequestParser {

    // Convert the raw HTTP request from the given stream into a RequestInfo object.
    public static RequestInfo parseRequest(InputStream in) throws IOException {
        // Manually read headers one character at a time until we hit a blank line (CRLF CRLF).
        List<String> headerLines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        int curr;
        while ((curr = in.read()) != -1) {
            if (curr == '\r') continue;
            if (curr == '\n') {
                if (line.length() == 0) {
                    break;
                }
                headerLines.add(line.toString());
                line.setLength(0);
            } else {
                line.append((char) curr);
            }
        }

        if (headerLines.isEmpty()) throw new IOException("Empty request");
        String startLine = headerLines.get(0);
        String[] parts = startLine.split(" ", 3);
        String httpCommand = parts[0];
        String uri = parts[1];

        String path = uri;
        Map<String, String> parameters = new HashMap<>();
        int qIdx = uri.indexOf('?');
        if (qIdx >= 0) {
            path = uri.substring(0, qIdx);
            String query = uri.substring(qIdx + 1);
            for (String kv : query.split("&")) {
                String[] kvp = kv.split("=", 2);
                if (kvp.length == 2) {
                    parameters.put(kvp[0], kvp[1]);
                }
            }
        }

        String[] raw = path.split("/");
        List<String> segs = new ArrayList<>();
        for (String s : raw) {
            if (!s.isEmpty()) segs.add(s);
        }
        String[] uriSegments = segs.toArray(new String[0]);

        // Parse headers
        int contentLength = 0;
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < headerLines.size(); i++) {
            String h = headerLines.get(i);
            int idx = h.indexOf(':');
            if (idx > 0) {
                String key = h.substring(0, idx).trim();
                String value = h.substring(idx + 1).trim();
                headers.put(key, value);
                if (key.equalsIgnoreCase("Content-Length")) {
                    try {
                        contentLength = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        // Read body (only for methods that send one) – exactly contentLength bytes.
        byte[] content = new byte[0];
        if (!httpCommand.equals("GET") && contentLength > 0) {
            content = new byte[contentLength];
            int totalRead = 0;
            while (totalRead < contentLength) {
                int read = in.read(content, totalRead, contentLength - totalRead);
                if (read == -1) break;
                totalRead += read;
            }
        }

        return new RequestInfo(httpCommand, uri, uriSegments, parameters, content);
    }

    // Data holder for the parsed request fields.
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand,
                           String uri,
                           String[] uriSegments,
                           Map<String, String> parameters,
                           byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
