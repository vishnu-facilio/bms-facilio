package com.facilio.datastructure.dag;

public class Edge implements EdgeInterface<Object>{
    Object object;
    Node startNode,endNode;

    public Edge(Object object) {
        this.object = object;
    }
    @Override
    public void addObject(Object object) {
        this.object = object;
    }

    @Override
    public Object retriveObject() {
        return object;
    }

    @Override
    public Node getStartNode() {
        return startNode;
    }

    @Override
    public Node getEndNode() {
        return endNode;
    }

    @Override
    public void setStartNode(Node node) {
        this.startNode = node;
    }

    @Override
    public void setEndNode(Node node) {
        this.endNode = node;
    }
}
