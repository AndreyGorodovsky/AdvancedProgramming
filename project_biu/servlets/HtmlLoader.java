package servlets;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import server.*;

// Servlet that serves static HTML files from a configured directory.
public class HtmlLoader implements Servlet {
    private final String htmlDir;
    public HtmlLoader(String htmlDir) {
        this.htmlDir = htmlDir;
    }
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        // Determine which file was requested (default to index.html).
        String[] segs = ri.getUriSegments();
        String fileName = segs.length > 1 ? segs[1] : "index.html";
        String filePath = htmlDir + "/" + fileName;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            byte[] content = Files.readAllBytes(Paths.get(filePath));
            // Build and send a minimal 200 OK response header followed by the file contents.
            String header = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\nContent-Length: "
                    + content.length + "\r\n\r\n";
            toClient.write(header.getBytes(StandardCharsets.UTF_8));
            toClient.write(content);
        } else {
            // File not found – return a simple 404 page.
            String html = "<html><body><h2>File not found: " + fileName + "</h2></body></html>";
            byte[] response = ("HTTP/1.1 404 Not Found\r\nContent-Type: text/html; charset=UTF-8\r\nContent-Length: "
                    + html.length() + "\r\n\r\n" + html).getBytes(StandardCharsets.UTF_8);
            toClient.write(response);
        }
    }
    @Override
    public void close() throws IOException {}
} 