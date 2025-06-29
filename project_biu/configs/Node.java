package configs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import graph.*;


public class Node {
    private final String name;
    private final String displayName;
    private final List<Node> edges = new ArrayList<>();
    private Message msg;
    private String equation="";


    public Node(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public Node(String name) {
        this(name, name);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<Node> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public Message getMsg() {
        return msg;
    }

    public void addEdge(Node to) {
        edges.add(to);
    }

    public boolean hasCycles() {
        return hasCycles(new HashSet<>());
    }

    private boolean hasCycles(Set<Node> path) {
        if (!path.add(this)) {
            return true;
        }
        for (Node neighbor : edges) {
            if (neighbor.hasCycles(new HashSet<>(path))) {
                return true;
            }
        }
        return false;
    }

    public void setEquation(String eq){ this.equation=eq; }
    public String getEquation(){ return equation; }
}
