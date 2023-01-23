package com.facilio.datastructure.dag;
import com.facilio.annotations.ImmutableChildClass;
import com.facilio.bmsconsole.exception.AllowedLevelExceededException;
import com.facilio.bmsconsole.exception.CircularDependencyException;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@ImmutableChildClass(className = "DAGCache")
public class DAG implements Graph{

    List<Node> vertices;
    List<Edge> edges;
    int MAX_FORWARDING_PATH;

    public DAG(DAG dag) {
        this.MAX_FORWARDING_PATH = dag.MAX_FORWARDING_PATH;
        this.vertices = dag.vertices;
        this.edges = dag.getEdges();
    }
    public DAG(int maxForwardingPath) {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        this.MAX_FORWARDING_PATH = maxForwardingPath;
    }

    @Override
    public void addVertex(Node node) {
        vertices.add(node);
    }

    @Override
    public void addEdge(Node fromNode, Node toNode, Edge edge) throws CircularDependencyException,AllowedLevelExceededException {
        fromNode.addForwardingEdge(edge);
        edge.setStartNode(fromNode);
        edge.setEndNode(toNode);
        edges.add(edge);
        try {
            checkCyclic();
        } catch (CircularDependencyException e) {
            throw e;
        } catch (AllowedLevelExceededException e) {
            throw e;
        }
    }

    public boolean checkCyclic() throws CircularDependencyException,AllowedLevelExceededException {
        if(CollectionUtils.isNotEmpty(vertices)) {
            for(Node node : vertices) {
                List<Node> visitedNodes = new ArrayList<>();
                if(hasCycle(node, visitedNodes,0)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasCycle(Node node,List<Node> visitedNodes,int hop) throws CircularDependencyException,AllowedLevelExceededException {
        if(hop > MAX_FORWARDING_PATH) {
            throw new AllowedLevelExceededException("Allowed Level Exceeded");
        }
        List<Edge> edges = node.getForwardingEdges();
        if(CollectionUtils.isNotEmpty(edges)) {
            for(Edge edge : edges) {
                Node endNode = edge.getEndNode();
                if(visitedNodes.contains(endNode)) {
                    throw new CircularDependencyException("Circular dependency detected");
                }
                visitedNodes.add(node);
                hasCycle(endNode,visitedNodes,++hop);
            }
        }
        return false;
    }
}