/**
 * Contains the {@link servlets.Servlet} interface and a few ready-made servlet implementations.
 * <h2>Creating your own servlet</h2>
 * Implement the two methods:
 * <pre>
 * public class EchoServlet implements Servlet {
 *     public void handle(RequestInfo ri, OutputStream out) throws IOException {
 *         String body = "You requested " + ri.getUri();
 *         out.write(("HTTP/1.1 200 OK\r\nContent-Length: " + body.length() + "\r\n\r\n" + body)
 *                  .getBytes(StandardCharsets.UTF_8));
 *     }
 *     public void close() {}
 * }
 * </pre>
 * Then register it with {@link server.HTTPServer#addServlet(String, String, Servlet)}.
 * <h2>Provided servlets</h2>
 * <ul>
 *   <li>{@link servlets.HtmlLoader} – serves static files from a directory.</li>
 *   <li>{@link servlets.TopicsJsonServlet} – dumps the current topics as JSON.</li>
 *   <li>Several demo servlets used by the course exercises – feel free to study or remove.</li>
 * </ul>
 */
package servlets; 