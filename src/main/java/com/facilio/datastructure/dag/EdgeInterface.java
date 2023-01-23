package com.facilio.datastructure.dag;

public interface EdgeInterface<T> {
    public void addObject(T object);

    public T retriveObject();

    public Node getStartNode();

    public Node getEndNode();

    public void setStartNode(Node node);

    public void setEndNode(Node node);
}
