package servlets;

import java.io.IOException;
import java.io.OutputStream;

import server.RequestParser.RequestInfo;

/**
 * A servlet is a stateless object that knows how to handle a single
 * HTTP request and write the response back to the client.
 *
 * <h2>Implementing a servlet</h2>
 * <pre>
 * public class HelloServlet implements Servlet {
 *     public void handle(RequestInfo ri, OutputStream out) throws IOException {
 *         String body = "Hello " + ri.getUri();
 *         out.write(("HTTP/1.1 200 OK\r\nContent-Length: " + body.length() + "\r\n\r\n" + body)
 *                  .getBytes(StandardCharsets.UTF_8));
 *     }
 *     public void close() {}
 * }
 * </pre>
 */
public interface Servlet {

    /**
     * Handle a single HTTP request.
     *
     * @param ri       parsed request information
     * @param toClient response stream – write the full HTTP response (status line, headers, body)
     * @throws IOException if writing fails
     */
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;

    /**
     * Free any resources held by this servlet (optional). Called once when the server shuts down.
     */
    void close() throws IOException;
}
