/**
 * Core HTTP server classes.
 * <p>
 * The package contains:
 * <ul>
 *   <li>{@link server.HTTPServer} – an interface representing a minimal HTTP/1.1 server.</li>
 *   <li>{@link server.MyHTTPServer} – the default implementation that uses a blocking {@link java.net.ServerSocket}</li>
 *   <li>{@link server.RequestParser} – utility for turning a raw socket stream into a convenient request object.</li>
 * </ul>
 * <h2>Quick start</h2>
 * <pre>{@code
 * // 1. Create a server listening on port 8080 with a thread-pool
 * HTTPServer server = new MyHTTPServer(8080, 5);
 *
 * // 2. Register a servlet (see {@link servlets.Servlet})
 * server.addServlet("GET", "/static/", new servlets.HtmlLoader("html_files"));
 * server.addServlet("GET", "/topics.json", new servlets.TopicsJsonServlet());
 *
 * // 3. Start the server (non-blocking)
 * server.start();
 *
 * // ... later ...
 * server.close();
 * }
 * </pre>
 * <p>
 * The implementation depends only on the Java Standard Library and requires Java&nbsp;8 or later.
 */
package server; 