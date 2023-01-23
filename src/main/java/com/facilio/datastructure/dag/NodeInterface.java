package com.facilio.datastructure.dag;

import java.util.List;

public interface NodeInterface<T> {
    public void addObject(T object);

    public T retriveObject();

    public List<Edge> getForwardingEdges();

    public void addForwardingEdge(Edge edge);
}
