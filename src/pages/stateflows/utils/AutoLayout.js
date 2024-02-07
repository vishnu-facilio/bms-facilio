import dagre from 'dagre'
import { breadth } from 'treeverse'

/**
 * @param  [] states
 * @param  [] transitions
 * @returns Ordered nodes and edges based on the order in which they appear in the
 * graph ie. for each edge u -> v, u appears before v in nodes
 */
const graphSort = (nodes, edges, start) => {
  let sortedNodes = []
  let sortedEdges = []

  breadth({
    tree: start,
    visit(node) {
      sortedNodes.push(node)
      sortedEdges.push(...edges.filter(e => e[0] === node))
    },
    getChildren(node) {
      return edges.filter(e => e[0] === node).map(e => e[1])
    },
  })

  return [sortedNodes, sortedEdges]
}

/**
 * @param  [] states
 * @param  [] transitions
 * @returns Graph - a dagre graph object that has nodes() and edges() with respective coordinates
 */
const createGraph = (nodes, edges) => {
  let g = new dagre.graphlib.Graph({
    directed: true,
    multigraph: true,
  })

  g.setGraph({
    ranker: 'network-simplex',
    nodesep: 100,
    ranksep: 100,
    edgesep: 100,
    align: 'UL',
    marginx: 100,
    marginy: 50,
  })

  g.setDefaultEdgeLabel(() => ({
    labelpos: 'c',
    minLen: 1,
    weight: 1,
  }))

  const nodeLabel = () => ({ width: 140, height: 40 })

  // Add nodes and make edges between them to graph
  nodes.forEach(id => g.setNode(id, nodeLabel()))
  edges.forEach(([from, to]) => g.setEdge(from, to))

  // Calculates layout coords for each node and edge
  dagre.layout(g)

  return g
}

/**
 * @param  [] states
 * @param  [] transitions
 * @returns {} Map of stateId and their coordinates based on the dagre graph
 */
export const getLayout = (states, transitions, startStateId) => {
  let [nodes, edges] = graphSort(
    states.map(s => `s${s.id}`),
    transitions.map(t => [`s${t.fromStateId}`, `s${t.toStateId}`]),
    `s${startStateId}`
  )

  // 's' is added because id's that start with numbers dont work with graphlib
  let graph = createGraph(nodes, edges)

  let stateCoords = graph.nodes().reduce((res, id) => {
    // slice is to remove 's' added during graph sort
    res[id.slice(1)] = graph.node(id)
    return res
  }, {})

  return stateCoords
}
