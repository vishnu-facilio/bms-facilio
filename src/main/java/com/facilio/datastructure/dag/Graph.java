package com.facilio.datastructure.dag;

import com.facilio.bmsconsole.exception.AllowedLevelExceededException;
import com.facilio.bmsconsole.exception.CircularDependencyException;

public interface Graph<T> {
    public void addVertex(Node node);

    public void addEdge(Node fromNode,Node toNode,Edge edge) throws CircularDependencyException, AllowedLevelExceededException;
}