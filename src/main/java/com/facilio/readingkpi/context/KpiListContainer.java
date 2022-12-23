package com.facilio.readingkpi.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

@Getter
@AllArgsConstructor
public class KpiListContainer {
    private final List<ReadingKPIContext> independentKpis;
    private final List<ReadingKPIContext> dependentKpis;
    private final List<Graph<Long, DefaultEdge>> dependencyGraphs;
}
