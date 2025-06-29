import server.HTTPServer;
import server.MyHTTPServer;
import servlets.*;

public class Main {
    public static void main(String[] args) throws Exception {
        
        HTTPServer server=new MyHTTPServer(8080,5);
        server.addServlet("GET", "/publish", new TopicDisplayer());
        server.addServlet("POST", "/upload", new ConfLoader());
        server.addServlet("GET", "/app/", new HtmlLoader("html_files"));
        server.addServlet("GET", "/topics", new TopicsJsonServlet());
        server.start();
        System.out.println("Open http://localhost:8080/app/index.html in your browser.");
        System.in.read();
        server.close();
        System.out.println("done");
    }
} 