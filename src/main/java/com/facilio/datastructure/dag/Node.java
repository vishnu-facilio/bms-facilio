package com.facilio.datastructure.dag;

import java.util.ArrayList;
import java.util.List;

public class Node implements NodeInterface<Object> {

    Object nodeObject;
    List<Edge> forwardingEdges;

    public Node(Object nodeObject) {
        this.nodeObject = nodeObject;
    }
    @Override
    public void addObject(Object object) {
        this.nodeObject = nodeObject;
    }

    @Override
    public Object retriveObject() {
        return nodeObject;
    }

    @Override
    public List<Edge> getForwardingEdges() {
        return forwardingEdges;
    }

    @Override
    public void addForwardingEdge(Edge edge) {
        if(forwardingEdges == null) {
            forwardingEdges = new ArrayList<>();
        }
        forwardingEdges.add(edge);
    }
}
