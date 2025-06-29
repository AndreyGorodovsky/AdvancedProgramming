package server;

import servlets.*;

/**
 * Minimal HTTP/1.1 server abstraction.
 * <p>
 * Implementation note: {@link server.MyHTTPServer} is the default implementation that ships
 * with this project.  Nothing prevents you from creating an alternative implementation (for
 * example based on NIO) as long as it fulfils this contract.
 * <h2>Typical usage</h2>
 * <pre>{@code
 * HTTPServer server = new MyHTTPServer(8080, 4);
 * server.addServlet("GET", "/static/", new HtmlLoader("html_files"));
 * server.start();
 * // ... later ...
 * server.close();
 * }</pre>
 */
public interface HTTPServer extends Runnable {

    /**
     * Register a servlet for requests that start with the given URI prefix.
     * If multiple servlets match one URI, the longest prefix wins.
     *
     * @param httpCommanmd HTTP method ("GET", "POST", ...)
     * @param uri          leading part of the path, e.g. "/api/" (trailing slash recommended)
     * @param s            servlet instance responsible for those requests
     */
    void addServlet(String httpCommanmd, String uri, Servlet s);

    /**
     * Remove a previously-registered servlet.
     *
     * @param httpCommanmd HTTP method
     * @param uri          URI prefix used during registration
     */
    void removeServlet(String httpCommanmd, String uri);

    /**
     * Start listening for incoming connections.  Implementations are expected to spawn their
     * own thread(s) so this call usually returns immediately.
     */
    void start();

    /**
     * Stop the server and release all underlying resources.
     */
    void close();
}
