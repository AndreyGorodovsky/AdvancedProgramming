package servlets;

import server.RequestParser.RequestInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class SubServlet implements Servlet {
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        Map<String,String> params = ri.getParameters();
        double a = Double.parseDouble(params.get("a"));
        double b = Double.parseDouble(params.get("b"));
        String response = "Sum: " + (a + b) + "\n";
        toClient.write(response.getBytes());
    }

    @Override
    public void close() throws IOException {
        // no resources to free
    }
}
