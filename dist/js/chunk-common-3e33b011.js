(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[95953],{
/***/870681:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/*
Copyright (c) 2012-2014 Chris Pettitt

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
module.exports={graphlib:__webpack_require__(570574),layout:__webpack_require__(698123),debug:__webpack_require__(727570),util:{time:__webpack_require__(811138).time,notime:__webpack_require__(811138).notime},version:__webpack_require__(988177)};
/***/},
/***/892188:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";var _=__webpack_require__(38436),greedyFAS=__webpack_require__(774079);function run(g){var fas="greedy"===g.graph().acyclicer?greedyFAS(g,weightFn(g)):dfsFAS(g);function weightFn(g){return function(e){return g.edge(e).weight}}_.forEach(fas,(function(e){var label=g.edge(e);g.removeEdge(e),label.forwardName=e.name,label.reversed=!0,g.setEdge(e.w,e.v,label,_.uniqueId("rev"))}))}function dfsFAS(g){var fas=[],stack={},visited={};function dfs(v){_.has(visited,v)||(visited[v]=!0,stack[v]=!0,_.forEach(g.outEdges(v),(function(e){_.has(stack,e.w)?fas.push(e):dfs(e.w)})),delete stack[v])}return _.forEach(g.nodes(),dfs),fas}function undo(g){_.forEach(g.edges(),(function(e){var label=g.edge(e);if(label.reversed){g.removeEdge(e);var forwardName=label.forwardName;delete label.reversed,delete label.forwardName,g.setEdge(e.w,e.v,label,forwardName)}}))}
/***/module.exports={run:run,undo:undo}},
/***/961133:
/***/function(module,__unused_webpack_exports,__webpack_require__){var _=__webpack_require__(38436),util=__webpack_require__(811138);function addBorderSegments(g){function dfs(v){var children=g.children(v),node=g.node(v);if(children.length&&_.forEach(children,dfs),_.has(node,"minRank")){node.borderLeft=[],node.borderRight=[];for(var rank=node.minRank,maxRank=node.maxRank+1;rank<maxRank;++rank)addBorderNode(g,"borderLeft","_bl",v,node,rank),addBorderNode(g,"borderRight","_br",v,node,rank)}}_.forEach(g.children(),dfs)}function addBorderNode(g,prop,prefix,sg,sgNode,rank){var label={width:0,height:0,rank:rank,borderType:prop},prev=sgNode[prop][rank-1],curr=util.addDummyNode(g,"border",label,prefix);sgNode[prop][rank]=curr,g.setParent(curr,sg),prev&&g.setEdge(prev,curr,{weight:1})}
/***/module.exports=addBorderSegments},
/***/653258:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";var _=__webpack_require__(38436);function adjust(g){var rankDir=g.graph().rankdir.toLowerCase();"lr"!==rankDir&&"rl"!==rankDir||swapWidthHeight(g)}function undo(g){var rankDir=g.graph().rankdir.toLowerCase();"bt"!==rankDir&&"rl"!==rankDir||reverseY(g),"lr"!==rankDir&&"rl"!==rankDir||(swapXY(g),swapWidthHeight(g))}function swapWidthHeight(g){_.forEach(g.nodes(),(function(v){swapWidthHeightOne(g.node(v))})),_.forEach(g.edges(),(function(e){swapWidthHeightOne(g.edge(e))}))}function swapWidthHeightOne(attrs){var w=attrs.width;attrs.width=attrs.height,attrs.height=w}function reverseY(g){_.forEach(g.nodes(),(function(v){reverseYOne(g.node(v))})),_.forEach(g.edges(),(function(e){var edge=g.edge(e);_.forEach(edge.points,reverseYOne),_.has(edge,"y")&&reverseYOne(edge)}))}function reverseYOne(attrs){attrs.y=-attrs.y}function swapXY(g){_.forEach(g.nodes(),(function(v){swapXYOne(g.node(v))})),_.forEach(g.edges(),(function(e){var edge=g.edge(e);_.forEach(edge.points,swapXYOne),_.has(edge,"x")&&swapXYOne(edge)}))}function swapXYOne(attrs){var x=attrs.x;attrs.x=attrs.y,attrs.y=x}
/***/module.exports={adjust:adjust,undo:undo}},
/***/877822:
/***/function(module){function List(){var sentinel={};sentinel._next=sentinel._prev=sentinel,this._sentinel=sentinel}function unlink(entry){entry._prev._next=entry._next,entry._next._prev=entry._prev,delete entry._next,delete entry._prev}function filterOutLinks(k,v){if("_next"!==k&&"_prev"!==k)return v}
/***/
/*
 * Simple doubly linked list implementation derived from Cormen, et al.,
 * "Introduction to Algorithms".
 */
module.exports=List,List.prototype.dequeue=function(){var sentinel=this._sentinel,entry=sentinel._prev;if(entry!==sentinel)return unlink(entry),entry},List.prototype.enqueue=function(entry){var sentinel=this._sentinel;entry._prev&&entry._next&&unlink(entry),entry._next=sentinel._next,sentinel._next._prev=entry,sentinel._next=entry,entry._prev=sentinel},List.prototype.toString=function(){var strs=[],sentinel=this._sentinel,curr=sentinel._prev;while(curr!==sentinel)strs.push(JSON.stringify(curr,filterOutLinks)),curr=curr._prev;return"["+strs.join(", ")+"]"}},
/***/727570:
/***/function(module,__unused_webpack_exports,__webpack_require__){var _=__webpack_require__(38436),util=__webpack_require__(811138),Graph=__webpack_require__(570574).Graph;
/* istanbul ignore next */
function debugOrdering(g){var layerMatrix=util.buildLayerMatrix(g),h=new Graph({compound:!0,multigraph:!0}).setGraph({});return _.forEach(g.nodes(),(function(v){h.setNode(v,{label:v}),h.setParent(v,"layer"+g.node(v).rank)})),_.forEach(g.edges(),(function(e){h.setEdge(e.v,e.w,{},e.name)})),_.forEach(layerMatrix,(function(layer,i){var layerV="layer"+i;h.setNode(layerV,{rank:"same"}),_.reduce(layer,(function(u,v){return h.setEdge(u,v,{style:"invis"}),v}))})),h}
/***/module.exports={debugOrdering:debugOrdering}},
/***/570574:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/* global window */
var graphlib;try{graphlib=__webpack_require__(628282)}catch(e){
// continue regardless of error
}graphlib||(graphlib=window.graphlib),module.exports=graphlib},
/***/774079:
/***/function(module,__unused_webpack_exports,__webpack_require__){var _=__webpack_require__(38436),Graph=__webpack_require__(570574).Graph,List=__webpack_require__(877822);
/*
 * A greedy heuristic for finding a feedback arc set for a graph. A feedback
 * arc set is a set of edges that can be removed to make a graph acyclic.
 * The algorithm comes from: P. Eades, X. Lin, and W. F. Smyth, "A fast and
 * effective heuristic for the feedback arc set problem." This implementation
 * adjusts that from the paper to allow for weighted edges.
 */
module.exports=greedyFAS;var DEFAULT_WEIGHT_FN=_.constant(1);function greedyFAS(g,weightFn){if(g.nodeCount()<=1)return[];var state=buildState(g,weightFn||DEFAULT_WEIGHT_FN),results=doGreedyFAS(state.graph,state.buckets,state.zeroIdx);
// Expand multi-edges
return _.flatten(_.map(results,(function(e){return g.outEdges(e.v,e.w)})),!0)}function doGreedyFAS(g,buckets,zeroIdx){var entry,results=[],sources=buckets[buckets.length-1],sinks=buckets[0];while(g.nodeCount()){while(entry=sinks.dequeue())removeNode(g,buckets,zeroIdx,entry);while(entry=sources.dequeue())removeNode(g,buckets,zeroIdx,entry);if(g.nodeCount())for(var i=buckets.length-2;i>0;--i)if(entry=buckets[i].dequeue(),entry){results=results.concat(removeNode(g,buckets,zeroIdx,entry,!0));break}}return results}function removeNode(g,buckets,zeroIdx,entry,collectPredecessors){var results=collectPredecessors?[]:void 0;return _.forEach(g.inEdges(entry.v),(function(edge){var weight=g.edge(edge),uEntry=g.node(edge.v);collectPredecessors&&results.push({v:edge.v,w:edge.w}),uEntry.out-=weight,assignBucket(buckets,zeroIdx,uEntry)})),_.forEach(g.outEdges(entry.v),(function(edge){var weight=g.edge(edge),w=edge.w,wEntry=g.node(w);wEntry["in"]-=weight,assignBucket(buckets,zeroIdx,wEntry)})),g.removeNode(entry.v),results}function buildState(g,weightFn){var fasGraph=new Graph,maxIn=0,maxOut=0;_.forEach(g.nodes(),(function(v){fasGraph.setNode(v,{v:v,in:0,out:0})})),
// Aggregate weights on nodes, but also sum the weights across multi-edges
// into a single edge for the fasGraph.
_.forEach(g.edges(),(function(e){var prevWeight=fasGraph.edge(e.v,e.w)||0,weight=weightFn(e),edgeWeight=prevWeight+weight;fasGraph.setEdge(e.v,e.w,edgeWeight),maxOut=Math.max(maxOut,fasGraph.node(e.v).out+=weight),maxIn=Math.max(maxIn,fasGraph.node(e.w)["in"]+=weight)}));var buckets=_.range(maxOut+maxIn+3).map((function(){return new List})),zeroIdx=maxIn+1;return _.forEach(fasGraph.nodes(),(function(v){assignBucket(buckets,zeroIdx,fasGraph.node(v))})),{graph:fasGraph,buckets:buckets,zeroIdx:zeroIdx}}function assignBucket(buckets,zeroIdx,entry){entry.out?entry["in"]?buckets[entry.out-entry["in"]+zeroIdx].enqueue(entry):buckets[buckets.length-1].enqueue(entry):buckets[0].enqueue(entry)}
/***/},
/***/698123:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";var _=__webpack_require__(38436),acyclic=__webpack_require__(892188),normalize=__webpack_require__(245995),rank=__webpack_require__(578093),normalizeRanks=__webpack_require__(811138).normalizeRanks,parentDummyChains=__webpack_require__(124219),removeEmptyRanks=__webpack_require__(811138).removeEmptyRanks,nestingGraph=__webpack_require__(972981),addBorderSegments=__webpack_require__(961133),coordinateSystem=__webpack_require__(653258),order=__webpack_require__(953408),position=__webpack_require__(617873),util=__webpack_require__(811138),Graph=__webpack_require__(570574).Graph;function layout(g,opts){var time=opts&&opts.debugTiming?util.time:util.notime;time("layout",(function(){var layoutGraph=time("  buildLayoutGraph",(function(){return buildLayoutGraph(g)}));time("  runLayout",(function(){runLayout(layoutGraph,time)})),time("  updateInputGraph",(function(){updateInputGraph(g,layoutGraph)}))}))}function runLayout(g,time){time("    makeSpaceForEdgeLabels",(function(){makeSpaceForEdgeLabels(g)})),time("    removeSelfEdges",(function(){removeSelfEdges(g)})),time("    acyclic",(function(){acyclic.run(g)})),time("    nestingGraph.run",(function(){nestingGraph.run(g)})),time("    rank",(function(){rank(util.asNonCompoundGraph(g))})),time("    injectEdgeLabelProxies",(function(){injectEdgeLabelProxies(g)})),time("    removeEmptyRanks",(function(){removeEmptyRanks(g)})),time("    nestingGraph.cleanup",(function(){nestingGraph.cleanup(g)})),time("    normalizeRanks",(function(){normalizeRanks(g)})),time("    assignRankMinMax",(function(){assignRankMinMax(g)})),time("    removeEdgeLabelProxies",(function(){removeEdgeLabelProxies(g)})),time("    normalize.run",(function(){normalize.run(g)})),time("    parentDummyChains",(function(){parentDummyChains(g)})),time("    addBorderSegments",(function(){addBorderSegments(g)})),time("    order",(function(){order(g)})),time("    insertSelfEdges",(function(){insertSelfEdges(g)})),time("    adjustCoordinateSystem",(function(){coordinateSystem.adjust(g)})),time("    position",(function(){position(g)})),time("    positionSelfEdges",(function(){positionSelfEdges(g)})),time("    removeBorderNodes",(function(){removeBorderNodes(g)})),time("    normalize.undo",(function(){normalize.undo(g)})),time("    fixupEdgeLabelCoords",(function(){fixupEdgeLabelCoords(g)})),time("    undoCoordinateSystem",(function(){coordinateSystem.undo(g)})),time("    translateGraph",(function(){translateGraph(g)})),time("    assignNodeIntersects",(function(){assignNodeIntersects(g)})),time("    reversePoints",(function(){reversePointsForReversedEdges(g)})),time("    acyclic.undo",(function(){acyclic.undo(g)}))}
/*
 * Copies final layout information from the layout graph back to the input
 * graph. This process only copies whitelisted attributes from the layout graph
 * to the input graph, so it serves as a good place to determine what
 * attributes can influence layout.
 */function updateInputGraph(inputGraph,layoutGraph){_.forEach(inputGraph.nodes(),(function(v){var inputLabel=inputGraph.node(v),layoutLabel=layoutGraph.node(v);inputLabel&&(inputLabel.x=layoutLabel.x,inputLabel.y=layoutLabel.y,layoutGraph.children(v).length&&(inputLabel.width=layoutLabel.width,inputLabel.height=layoutLabel.height))})),_.forEach(inputGraph.edges(),(function(e){var inputLabel=inputGraph.edge(e),layoutLabel=layoutGraph.edge(e);inputLabel.points=layoutLabel.points,_.has(layoutLabel,"x")&&(inputLabel.x=layoutLabel.x,inputLabel.y=layoutLabel.y)})),inputGraph.graph().width=layoutGraph.graph().width,inputGraph.graph().height=layoutGraph.graph().height}module.exports=layout;var graphNumAttrs=["nodesep","edgesep","ranksep","marginx","marginy"],graphDefaults={ranksep:50,edgesep:20,nodesep:50,rankdir:"tb"},graphAttrs=["acyclicer","ranker","rankdir","align"],nodeNumAttrs=["width","height"],nodeDefaults={width:0,height:0},edgeNumAttrs=["minlen","weight","width","height","labeloffset"],edgeDefaults={minlen:1,weight:1,width:0,height:0,labeloffset:10,labelpos:"r"},edgeAttrs=["labelpos"];
/*
 * Constructs a new graph from the input graph, which can be used for layout.
 * This process copies only whitelisted attributes from the input graph to the
 * layout graph. Thus this function serves as a good place to determine what
 * attributes can influence layout.
 */
function buildLayoutGraph(inputGraph){var g=new Graph({multigraph:!0,compound:!0}),graph=canonicalize(inputGraph.graph());return g.setGraph(_.merge({},graphDefaults,selectNumberAttrs(graph,graphNumAttrs),_.pick(graph,graphAttrs))),_.forEach(inputGraph.nodes(),(function(v){var node=canonicalize(inputGraph.node(v));g.setNode(v,_.defaults(selectNumberAttrs(node,nodeNumAttrs),nodeDefaults)),g.setParent(v,inputGraph.parent(v))})),_.forEach(inputGraph.edges(),(function(e){var edge=canonicalize(inputGraph.edge(e));g.setEdge(e,_.merge({},edgeDefaults,selectNumberAttrs(edge,edgeNumAttrs),_.pick(edge,edgeAttrs)))})),g}
/*
 * This idea comes from the Gansner paper: to account for edge labels in our
 * layout we split each rank in half by doubling minlen and halving ranksep.
 * Then we can place labels at these mid-points between nodes.
 *
 * We also add some minimal padding to the width to push the label for the edge
 * away from the edge itself a bit.
 */function makeSpaceForEdgeLabels(g){var graph=g.graph();graph.ranksep/=2,_.forEach(g.edges(),(function(e){var edge=g.edge(e);edge.minlen*=2,"c"!==edge.labelpos.toLowerCase()&&("TB"===graph.rankdir||"BT"===graph.rankdir?edge.width+=edge.labeloffset:edge.height+=edge.labeloffset)}))}
/*
 * Creates temporary dummy nodes that capture the rank in which each edge's
 * label is going to, if it has one of non-zero width and height. We do this
 * so that we can safely remove empty ranks while preserving balance for the
 * label's position.
 */function injectEdgeLabelProxies(g){_.forEach(g.edges(),(function(e){var edge=g.edge(e);if(edge.width&&edge.height){var v=g.node(e.v),w=g.node(e.w),label={rank:(w.rank-v.rank)/2+v.rank,e:e};util.addDummyNode(g,"edge-proxy",label,"_ep")}}))}function assignRankMinMax(g){var maxRank=0;_.forEach(g.nodes(),(function(v){var node=g.node(v);node.borderTop&&(node.minRank=g.node(node.borderTop).rank,node.maxRank=g.node(node.borderBottom).rank,maxRank=_.max(maxRank,node.maxRank))})),g.graph().maxRank=maxRank}function removeEdgeLabelProxies(g){_.forEach(g.nodes(),(function(v){var node=g.node(v);"edge-proxy"===node.dummy&&(g.edge(node.e).labelRank=node.rank,g.removeNode(v))}))}function translateGraph(g){var minX=Number.POSITIVE_INFINITY,maxX=0,minY=Number.POSITIVE_INFINITY,maxY=0,graphLabel=g.graph(),marginX=graphLabel.marginx||0,marginY=graphLabel.marginy||0;function getExtremes(attrs){var x=attrs.x,y=attrs.y,w=attrs.width,h=attrs.height;minX=Math.min(minX,x-w/2),maxX=Math.max(maxX,x+w/2),minY=Math.min(minY,y-h/2),maxY=Math.max(maxY,y+h/2)}_.forEach(g.nodes(),(function(v){getExtremes(g.node(v))})),_.forEach(g.edges(),(function(e){var edge=g.edge(e);_.has(edge,"x")&&getExtremes(edge)})),minX-=marginX,minY-=marginY,_.forEach(g.nodes(),(function(v){var node=g.node(v);node.x-=minX,node.y-=minY})),_.forEach(g.edges(),(function(e){var edge=g.edge(e);_.forEach(edge.points,(function(p){p.x-=minX,p.y-=minY})),_.has(edge,"x")&&(edge.x-=minX),_.has(edge,"y")&&(edge.y-=minY)})),graphLabel.width=maxX-minX+marginX,graphLabel.height=maxY-minY+marginY}function assignNodeIntersects(g){_.forEach(g.edges(),(function(e){var p1,p2,edge=g.edge(e),nodeV=g.node(e.v),nodeW=g.node(e.w);edge.points?(p1=edge.points[0],p2=edge.points[edge.points.length-1]):(edge.points=[],p1=nodeW,p2=nodeV),edge.points.unshift(util.intersectRect(nodeV,p1)),edge.points.push(util.intersectRect(nodeW,p2))}))}function fixupEdgeLabelCoords(g){_.forEach(g.edges(),(function(e){var edge=g.edge(e);if(_.has(edge,"x"))switch("l"!==edge.labelpos&&"r"!==edge.labelpos||(edge.width-=edge.labeloffset),edge.labelpos){case"l":edge.x-=edge.width/2+edge.labeloffset;break;case"r":edge.x+=edge.width/2+edge.labeloffset;break}}))}function reversePointsForReversedEdges(g){_.forEach(g.edges(),(function(e){var edge=g.edge(e);edge.reversed&&edge.points.reverse()}))}function removeBorderNodes(g){_.forEach(g.nodes(),(function(v){if(g.children(v).length){var node=g.node(v),t=g.node(node.borderTop),b=g.node(node.borderBottom),l=g.node(_.last(node.borderLeft)),r=g.node(_.last(node.borderRight));node.width=Math.abs(r.x-l.x),node.height=Math.abs(b.y-t.y),node.x=l.x+node.width/2,node.y=t.y+node.height/2}})),_.forEach(g.nodes(),(function(v){"border"===g.node(v).dummy&&g.removeNode(v)}))}function removeSelfEdges(g){_.forEach(g.edges(),(function(e){if(e.v===e.w){var node=g.node(e.v);node.selfEdges||(node.selfEdges=[]),node.selfEdges.push({e:e,label:g.edge(e)}),g.removeEdge(e)}}))}function insertSelfEdges(g){var layers=util.buildLayerMatrix(g);_.forEach(layers,(function(layer){var orderShift=0;_.forEach(layer,(function(v,i){var node=g.node(v);node.order=i+orderShift,_.forEach(node.selfEdges,(function(selfEdge){util.addDummyNode(g,"selfedge",{width:selfEdge.label.width,height:selfEdge.label.height,rank:node.rank,order:i+ ++orderShift,e:selfEdge.e,label:selfEdge.label},"_se")})),delete node.selfEdges}))}))}function positionSelfEdges(g){_.forEach(g.nodes(),(function(v){var node=g.node(v);if("selfedge"===node.dummy){var selfNode=g.node(node.e.v),x=selfNode.x+selfNode.width/2,y=selfNode.y,dx=node.x-x,dy=selfNode.height/2;g.setEdge(node.e,node.label),g.removeNode(v),node.label.points=[{x:x+2*dx/3,y:y-dy},{x:x+5*dx/6,y:y-dy},{x:x+dx,y:y},{x:x+5*dx/6,y:y+dy},{x:x+2*dx/3,y:y+dy}],node.label.x=node.x,node.label.y=node.y}}))}function selectNumberAttrs(obj,attrs){return _.mapValues(_.pick(obj,attrs),Number)}function canonicalize(attrs){var newAttrs={};return _.forEach(attrs,(function(v,k){newAttrs[k.toLowerCase()]=v})),newAttrs}
/***/},
/***/38436:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/* global window */
var lodash;try{lodash={cloneDeep:__webpack_require__(150361),constant:__webpack_require__(575703),defaults:__webpack_require__(791747),each:__webpack_require__(966073),filter:__webpack_require__(763105),find:__webpack_require__(313311),flatten:__webpack_require__(385564),forEach:__webpack_require__(784486),forIn:__webpack_require__(962620),has:__webpack_require__(218721),isUndefined:__webpack_require__(352353),last:__webpack_require__(610928),map:__webpack_require__(435161),mapValues:__webpack_require__(66604),max:__webpack_require__(606162),merge:__webpack_require__(682492),min:__webpack_require__(253632),minBy:__webpack_require__(322762),now:__webpack_require__(707771),pick:__webpack_require__(478718),range:__webpack_require__(396026),reduce:__webpack_require__(354061),sortBy:__webpack_require__(189734),uniqueId:__webpack_require__(873955),values:__webpack_require__(252628),zipObject:__webpack_require__(907287)}}catch(e){
// continue regardless of error
}lodash||(lodash=window._),module.exports=lodash},
/***/972981:
/***/function(module,__unused_webpack_exports,__webpack_require__){var _=__webpack_require__(38436),util=__webpack_require__(811138);
/*
 * A nesting graph creates dummy nodes for the tops and bottoms of subgraphs,
 * adds appropriate edges to ensure that all cluster nodes are placed between
 * these boundries, and ensures that the graph is connected.
 *
 * In addition we ensure, through the use of the minlen property, that nodes
 * and subgraph border nodes to not end up on the same rank.
 *
 * Preconditions:
 *
 *    1. Input graph is a DAG
 *    2. Nodes in the input graph has a minlen attribute
 *
 * Postconditions:
 *
 *    1. Input graph is connected.
 *    2. Dummy nodes are added for the tops and bottoms of subgraphs.
 *    3. The minlen attribute for nodes is adjusted to ensure nodes do not
 *       get placed on the same rank as subgraph border nodes.
 *
 * The nesting graph idea comes from Sander, "Layout of Compound Directed
 * Graphs."
 */
function run(g){var root=util.addDummyNode(g,"root",{},"_root"),depths=treeDepths(g),height=_.max(_.values(depths))-1,nodeSep=2*height+1;g.graph().nestingRoot=root,
// Multiply minlen by nodeSep to align nodes on non-border ranks.
_.forEach(g.edges(),(function(e){g.edge(e).minlen*=nodeSep}));
// Calculate a weight that is sufficient to keep subgraphs vertically compact
var weight=sumWeights(g)+1;
// Create border nodes and link them up
_.forEach(g.children(),(function(child){dfs(g,root,nodeSep,weight,height,depths,child)})),
// Save the multiplier for node layers for later removal of empty border
// layers.
g.graph().nodeRankFactor=nodeSep}function dfs(g,root,nodeSep,weight,height,depths,v){var children=g.children(v);if(children.length){var top=util.addBorderNode(g,"_bt"),bottom=util.addBorderNode(g,"_bb"),label=g.node(v);g.setParent(top,v),label.borderTop=top,g.setParent(bottom,v),label.borderBottom=bottom,_.forEach(children,(function(child){dfs(g,root,nodeSep,weight,height,depths,child);var childNode=g.node(child),childTop=childNode.borderTop?childNode.borderTop:child,childBottom=childNode.borderBottom?childNode.borderBottom:child,thisWeight=childNode.borderTop?weight:2*weight,minlen=childTop!==childBottom?1:height-depths[v]+1;g.setEdge(top,childTop,{weight:thisWeight,minlen:minlen,nestingEdge:!0}),g.setEdge(childBottom,bottom,{weight:thisWeight,minlen:minlen,nestingEdge:!0})})),g.parent(v)||g.setEdge(root,top,{weight:0,minlen:height+depths[v]})}else v!==root&&g.setEdge(root,v,{weight:0,minlen:nodeSep})}function treeDepths(g){var depths={};function dfs(v,depth){var children=g.children(v);children&&children.length&&_.forEach(children,(function(child){dfs(child,depth+1)})),depths[v]=depth}return _.forEach(g.children(),(function(v){dfs(v,1)})),depths}function sumWeights(g){return _.reduce(g.edges(),(function(acc,e){return acc+g.edge(e).weight}),0)}function cleanup(g){var graphLabel=g.graph();g.removeNode(graphLabel.nestingRoot),delete graphLabel.nestingRoot,_.forEach(g.edges(),(function(e){var edge=g.edge(e);edge.nestingEdge&&g.removeEdge(e)}))}
/***/module.exports={run:run,cleanup:cleanup}},
/***/245995:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";var _=__webpack_require__(38436),util=__webpack_require__(811138);
/*
 * Breaks any long edges in the graph into short segments that span 1 layer
 * each. This operation is undoable with the denormalize function.
 *
 * Pre-conditions:
 *
 *    1. The input graph is a DAG.
 *    2. Each node in the graph has a "rank" property.
 *
 * Post-condition:
 *
 *    1. All edges in the graph have a length of 1.
 *    2. Dummy nodes are added where edges have been split into segments.
 *    3. The graph is augmented with a "dummyChains" attribute which contains
 *       the first dummy in each chain of dummy nodes produced.
 */
function run(g){g.graph().dummyChains=[],_.forEach(g.edges(),(function(edge){normalizeEdge(g,edge)}))}function normalizeEdge(g,e){var v=e.v,vRank=g.node(v).rank,w=e.w,wRank=g.node(w).rank,name=e.name,edgeLabel=g.edge(e),labelRank=edgeLabel.labelRank;if(wRank!==vRank+1){var dummy,attrs,i;for(g.removeEdge(e),i=0,++vRank;vRank<wRank;++i,++vRank)edgeLabel.points=[],attrs={width:0,height:0,edgeLabel:edgeLabel,edgeObj:e,rank:vRank},dummy=util.addDummyNode(g,"edge",attrs,"_d"),vRank===labelRank&&(attrs.width=edgeLabel.width,attrs.height=edgeLabel.height,attrs.dummy="edge-label",attrs.labelpos=edgeLabel.labelpos),g.setEdge(v,dummy,{weight:edgeLabel.weight},name),0===i&&g.graph().dummyChains.push(dummy),v=dummy;g.setEdge(v,w,{weight:edgeLabel.weight},name)}}function undo(g){_.forEach(g.graph().dummyChains,(function(v){var w,node=g.node(v),origLabel=node.edgeLabel;g.setEdge(node.edgeObj,origLabel);while(node.dummy)w=g.successors(v)[0],g.removeNode(v),origLabel.points.push({x:node.x,y:node.y}),"edge-label"===node.dummy&&(origLabel.x=node.x,origLabel.y=node.y,origLabel.width=node.width,origLabel.height=node.height),v=w,node=g.node(v)}))}
/***/module.exports={run:run,undo:undo}},
/***/55093:
/***/function(module,__unused_webpack_exports,__webpack_require__){var _=__webpack_require__(38436);function addSubgraphConstraints(g,cg,vs){var rootPrev,prev={};_.forEach(vs,(function(v){var parent,prevChild,child=g.parent(v);while(child){if(parent=g.parent(child),parent?(prevChild=prev[parent],prev[parent]=child):(prevChild=rootPrev,rootPrev=child),prevChild&&prevChild!==child)return void cg.setEdge(prevChild,child);child=parent}}))}
/***/module.exports=addSubgraphConstraints},
/***/35439:
/***/function(module,__unused_webpack_exports,__webpack_require__){var _=__webpack_require__(38436);function barycenter(g,movable){return _.map(movable,(function(v){var inV=g.inEdges(v);if(inV.length){var result=_.reduce(inV,(function(acc,e){var edge=g.edge(e),nodeU=g.node(e.v);return{sum:acc.sum+edge.weight*nodeU.order,weight:acc.weight+edge.weight}}),{sum:0,weight:0});return{v:v,barycenter:result.sum/result.weight,weight:result.weight}}return{v:v}}))}
/***/module.exports=barycenter},
/***/23128:
/***/function(module,__unused_webpack_exports,__webpack_require__){var _=__webpack_require__(38436),Graph=__webpack_require__(570574).Graph;
/*
 * Constructs a graph that can be used to sort a layer of nodes. The graph will
 * contain all base and subgraph nodes from the request layer in their original
 * hierarchy and any edges that are incident on these nodes and are of the type
 * requested by the "relationship" parameter.
 *
 * Nodes from the requested rank that do not have parents are assigned a root
 * node in the output graph, which is set in the root graph attribute. This
 * makes it easy to walk the hierarchy of movable nodes during ordering.
 *
 * Pre-conditions:
 *
 *    1. Input graph is a DAG
 *    2. Base nodes in the input graph have a rank attribute
 *    3. Subgraph nodes in the input graph has minRank and maxRank attributes
 *    4. Edges have an assigned weight
 *
 * Post-conditions:
 *
 *    1. Output graph has all nodes in the movable rank with preserved
 *       hierarchy.
 *    2. Root nodes in the movable layer are made children of the node
 *       indicated by the root attribute of the graph.
 *    3. Non-movable nodes incident on movable nodes, selected by the
 *       relationship parameter, are included in the graph (without hierarchy).
 *    4. Edges incident on movable nodes, selected by the relationship
 *       parameter, are added to the output graph.
 *    5. The weights for copied edges are aggregated as need, since the output
 *       graph is not a multi-graph.
 */
function buildLayerGraph(g,rank,relationship){var root=createRootNode(g),result=new Graph({compound:!0}).setGraph({root:root}).setDefaultNodeLabel((function(v){return g.node(v)}));return _.forEach(g.nodes(),(function(v){var node=g.node(v),parent=g.parent(v);(node.rank===rank||node.minRank<=rank&&rank<=node.maxRank)&&(result.setNode(v),result.setParent(v,parent||root),
// This assumes we have only short edges!
_.forEach(g[relationship](v),(function(e){var u=e.v===v?e.w:e.v,edge=result.edge(u,v),weight=_.isUndefined(edge)?0:edge.weight;result.setEdge(u,v,{weight:g.edge(e).weight+weight})})),_.has(node,"minRank")&&result.setNode(v,{borderLeft:node.borderLeft[rank],borderRight:node.borderRight[rank]}))})),result}function createRootNode(g){var v;while(g.hasNode(v=_.uniqueId("_root")));return v}
/***/module.exports=buildLayerGraph},
/***/756630:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";var _=__webpack_require__(38436);
/*
 * A function that takes a layering (an array of layers, each with an array of
 * ordererd nodes) and a graph and returns a weighted crossing count.
 *
 * Pre-conditions:
 *
 *    1. Input graph must be simple (not a multigraph), directed, and include
 *       only simple edges.
 *    2. Edges in the input graph must have assigned weights.
 *
 * Post-conditions:
 *
 *    1. The graph and layering matrix are left unchanged.
 *
 * This algorithm is derived from Barth, et al., "Bilayer Cross Counting."
 */
function crossCount(g,layering){for(var cc=0,i=1;i<layering.length;++i)cc+=twoLayerCrossCount(g,layering[i-1],layering[i]);return cc}function twoLayerCrossCount(g,northLayer,southLayer){
// Sort all of the edges between the north and south layers by their position
// in the north layer and then the south. Map these edges to the position of
// their head in the south layer.
var southPos=_.zipObject(southLayer,_.map(southLayer,(function(v,i){return i}))),southEntries=_.flatten(_.map(northLayer,(function(v){return _.sortBy(_.map(g.outEdges(v),(function(e){return{pos:southPos[e.w],weight:g.edge(e).weight}})),"pos")})),!0),firstIndex=1;while(firstIndex<southLayer.length)firstIndex<<=1;var treeSize=2*firstIndex-1;firstIndex-=1;var tree=_.map(new Array(treeSize),(function(){return 0})),cc=0;
// Calculate the weighted crossings
return _.forEach(southEntries.forEach((function(entry){var index=entry.pos+firstIndex;tree[index]+=entry.weight;var weightSum=0;while(index>0)index%2&&(weightSum+=tree[index+1]),index=index-1>>1,tree[index]+=entry.weight;cc+=entry.weight*weightSum}))),cc}
/***/module.exports=crossCount},
/***/953408:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";var _=__webpack_require__(38436),initOrder=__webpack_require__(902588),crossCount=__webpack_require__(756630),sortSubgraph=__webpack_require__(361026),buildLayerGraph=__webpack_require__(23128),addSubgraphConstraints=__webpack_require__(55093),Graph=__webpack_require__(570574).Graph,util=__webpack_require__(811138);
/*
 * Applies heuristics to minimize edge crossings in the graph and sets the best
 * order solution as an order attribute on each node.
 *
 * Pre-conditions:
 *
 *    1. Graph must be DAG
 *    2. Graph nodes must be objects with a "rank" attribute
 *    3. Graph edges must have the "weight" attribute
 *
 * Post-conditions:
 *
 *    1. Graph nodes will have an "order" attribute based on the results of the
 *       algorithm.
 */
function order(g){var maxRank=util.maxRank(g),downLayerGraphs=buildLayerGraphs(g,_.range(1,maxRank+1),"inEdges"),upLayerGraphs=buildLayerGraphs(g,_.range(maxRank-1,-1,-1),"outEdges"),layering=initOrder(g);assignOrder(g,layering);for(var best,bestCC=Number.POSITIVE_INFINITY,i=0,lastBest=0;lastBest<4;++i,++lastBest){sweepLayerGraphs(i%2?downLayerGraphs:upLayerGraphs,i%4>=2),layering=util.buildLayerMatrix(g);var cc=crossCount(g,layering);cc<bestCC&&(lastBest=0,best=_.cloneDeep(layering),bestCC=cc)}assignOrder(g,best)}function buildLayerGraphs(g,ranks,relationship){return _.map(ranks,(function(rank){return buildLayerGraph(g,rank,relationship)}))}function sweepLayerGraphs(layerGraphs,biasRight){var cg=new Graph;_.forEach(layerGraphs,(function(lg){var root=lg.graph().root,sorted=sortSubgraph(lg,root,cg,biasRight);_.forEach(sorted.vs,(function(v,i){lg.node(v).order=i})),addSubgraphConstraints(lg,cg,sorted.vs)}))}function assignOrder(g,layering){_.forEach(layering,(function(layer){_.forEach(layer,(function(v,i){g.node(v).order=i}))}))}
/***/module.exports=order},
/***/902588:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";var _=__webpack_require__(38436);
/*
 * Assigns an initial order value for each node by performing a DFS search
 * starting from nodes in the first rank. Nodes are assigned an order in their
 * rank as they are first visited.
 *
 * This approach comes from Gansner, et al., "A Technique for Drawing Directed
 * Graphs."
 *
 * Returns a layering matrix with an array per layer and each layer sorted by
 * the order of its nodes.
 */
function initOrder(g){var visited={},simpleNodes=_.filter(g.nodes(),(function(v){return!g.children(v).length})),maxRank=_.max(_.map(simpleNodes,(function(v){return g.node(v).rank}))),layers=_.map(_.range(maxRank+1),(function(){return[]}));function dfs(v){if(!_.has(visited,v)){visited[v]=!0;var node=g.node(v);layers[node.rank].push(v),_.forEach(g.successors(v),dfs)}}var orderedVs=_.sortBy(simpleNodes,(function(v){return g.node(v).rank}));return _.forEach(orderedVs,dfs),layers}
/***/module.exports=initOrder},
/***/683678:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";var _=__webpack_require__(38436);
/*
 * Given a list of entries of the form {v, barycenter, weight} and a
 * constraint graph this function will resolve any conflicts between the
 * constraint graph and the barycenters for the entries. If the barycenters for
 * an entry would violate a constraint in the constraint graph then we coalesce
 * the nodes in the conflict into a new node that respects the contraint and
 * aggregates barycenter and weight information.
 *
 * This implementation is based on the description in Forster, "A Fast and
 * Simple Hueristic for Constrained Two-Level Crossing Reduction," thought it
 * differs in some specific details.
 *
 * Pre-conditions:
 *
 *    1. Each entry has the form {v, barycenter, weight}, or if the node has
 *       no barycenter, then {v}.
 *
 * Returns:
 *
 *    A new list of entries of the form {vs, i, barycenter, weight}. The list
 *    `vs` may either be a singleton or it may be an aggregation of nodes
 *    ordered such that they do not violate constraints from the constraint
 *    graph. The property `i` is the lowest original index of any of the
 *    elements in `vs`.
 */
function resolveConflicts(entries,cg){var mappedEntries={};_.forEach(entries,(function(entry,i){var tmp=mappedEntries[entry.v]={indegree:0,in:[],out:[],vs:[entry.v],i:i};_.isUndefined(entry.barycenter)||(tmp.barycenter=entry.barycenter,tmp.weight=entry.weight)})),_.forEach(cg.edges(),(function(e){var entryV=mappedEntries[e.v],entryW=mappedEntries[e.w];_.isUndefined(entryV)||_.isUndefined(entryW)||(entryW.indegree++,entryV.out.push(mappedEntries[e.w]))}));var sourceSet=_.filter(mappedEntries,(function(entry){return!entry.indegree}));return doResolveConflicts(sourceSet)}function doResolveConflicts(sourceSet){var entries=[];function handleIn(vEntry){return function(uEntry){uEntry.merged||(_.isUndefined(uEntry.barycenter)||_.isUndefined(vEntry.barycenter)||uEntry.barycenter>=vEntry.barycenter)&&mergeEntries(vEntry,uEntry)}}function handleOut(vEntry){return function(wEntry){wEntry["in"].push(vEntry),0===--wEntry.indegree&&sourceSet.push(wEntry)}}while(sourceSet.length){var entry=sourceSet.pop();entries.push(entry),_.forEach(entry["in"].reverse(),handleIn(entry)),_.forEach(entry.out,handleOut(entry))}return _.map(_.filter(entries,(function(entry){return!entry.merged})),(function(entry){return _.pick(entry,["vs","i","barycenter","weight"])}))}function mergeEntries(target,source){var sum=0,weight=0;target.weight&&(sum+=target.barycenter*target.weight,weight+=target.weight),source.weight&&(sum+=source.barycenter*source.weight,weight+=source.weight),target.vs=source.vs.concat(target.vs),target.barycenter=sum/weight,target.weight=weight,target.i=Math.min(source.i,target.i),source.merged=!0}
/***/module.exports=resolveConflicts},
/***/361026:
/***/function(module,__unused_webpack_exports,__webpack_require__){var _=__webpack_require__(38436),barycenter=__webpack_require__(35439),resolveConflicts=__webpack_require__(683678),sort=__webpack_require__(487304);function sortSubgraph(g,v,cg,biasRight){var movable=g.children(v),node=g.node(v),bl=node?node.borderLeft:void 0,br=node?node.borderRight:void 0,subgraphs={};bl&&(movable=_.filter(movable,(function(w){return w!==bl&&w!==br})));var barycenters=barycenter(g,movable);_.forEach(barycenters,(function(entry){if(g.children(entry.v).length){var subgraphResult=sortSubgraph(g,entry.v,cg,biasRight);subgraphs[entry.v]=subgraphResult,_.has(subgraphResult,"barycenter")&&mergeBarycenters(entry,subgraphResult)}}));var entries=resolveConflicts(barycenters,cg);expandSubgraphs(entries,subgraphs);var result=sort(entries,biasRight);if(bl&&(result.vs=_.flatten([bl,result.vs,br],!0),g.predecessors(bl).length)){var blPred=g.node(g.predecessors(bl)[0]),brPred=g.node(g.predecessors(br)[0]);_.has(result,"barycenter")||(result.barycenter=0,result.weight=0),result.barycenter=(result.barycenter*result.weight+blPred.order+brPred.order)/(result.weight+2),result.weight+=2}return result}function expandSubgraphs(entries,subgraphs){_.forEach(entries,(function(entry){entry.vs=_.flatten(entry.vs.map((function(v){return subgraphs[v]?subgraphs[v].vs:v})),!0)}))}function mergeBarycenters(target,other){_.isUndefined(target.barycenter)?(target.barycenter=other.barycenter,target.weight=other.weight):(target.barycenter=(target.barycenter*target.weight+other.barycenter*other.weight)/(target.weight+other.weight),target.weight+=other.weight)}
/***/module.exports=sortSubgraph},
/***/487304:
/***/function(module,__unused_webpack_exports,__webpack_require__){var _=__webpack_require__(38436),util=__webpack_require__(811138);function sort(entries,biasRight){var parts=util.partition(entries,(function(entry){return _.has(entry,"barycenter")})),sortable=parts.lhs,unsortable=_.sortBy(parts.rhs,(function(entry){return-entry.i})),vs=[],sum=0,weight=0,vsIndex=0;sortable.sort(compareWithBias(!!biasRight)),vsIndex=consumeUnsortable(vs,unsortable,vsIndex),_.forEach(sortable,(function(entry){vsIndex+=entry.vs.length,vs.push(entry.vs),sum+=entry.barycenter*entry.weight,weight+=entry.weight,vsIndex=consumeUnsortable(vs,unsortable,vsIndex)}));var result={vs:_.flatten(vs,!0)};return weight&&(result.barycenter=sum/weight,result.weight=weight),result}function consumeUnsortable(vs,unsortable,index){var last;while(unsortable.length&&(last=_.last(unsortable)).i<=index)unsortable.pop(),vs.push(last.vs),index++;return index}function compareWithBias(bias){return function(entryV,entryW){return entryV.barycenter<entryW.barycenter?-1:entryV.barycenter>entryW.barycenter?1:bias?entryW.i-entryV.i:entryV.i-entryW.i}}
/***/module.exports=sort},
/***/124219:
/***/function(module,__unused_webpack_exports,__webpack_require__){var _=__webpack_require__(38436);function parentDummyChains(g){var postorderNums=postorder(g);_.forEach(g.graph().dummyChains,(function(v){var node=g.node(v),edgeObj=node.edgeObj,pathData=findPath(g,postorderNums,edgeObj.v,edgeObj.w),path=pathData.path,lca=pathData.lca,pathIdx=0,pathV=path[pathIdx],ascending=!0;while(v!==edgeObj.w){if(node=g.node(v),ascending){while((pathV=path[pathIdx])!==lca&&g.node(pathV).maxRank<node.rank)pathIdx++;pathV===lca&&(ascending=!1)}if(!ascending){while(pathIdx<path.length-1&&g.node(pathV=path[pathIdx+1]).minRank<=node.rank)pathIdx++;pathV=path[pathIdx]}g.setParent(v,pathV),v=g.successors(v)[0]}}))}
// Find a path from v to w through the lowest common ancestor (LCA). Return the
// full path and the LCA.
function findPath(g,postorderNums,v,w){var parent,lca,vPath=[],wPath=[],low=Math.min(postorderNums[v].low,postorderNums[w].low),lim=Math.max(postorderNums[v].lim,postorderNums[w].lim);
// Traverse up from v to find the LCA
parent=v;do{parent=g.parent(parent),vPath.push(parent)}while(parent&&(postorderNums[parent].low>low||lim>postorderNums[parent].lim));lca=parent,
// Traverse from w to LCA
parent=w;while((parent=g.parent(parent))!==lca)wPath.push(parent);return{path:vPath.concat(wPath.reverse()),lca:lca}}function postorder(g){var result={},lim=0;function dfs(v){var low=lim;_.forEach(g.children(v),dfs),result[v]={low:low,lim:lim++}}return _.forEach(g.children(),dfs),result}
/***/module.exports=parentDummyChains},
/***/503573:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";var _=__webpack_require__(38436),Graph=__webpack_require__(570574).Graph,util=__webpack_require__(811138);
/*
 * Marks all edges in the graph with a type-1 conflict with the "type1Conflict"
 * property. A type-1 conflict is one where a non-inner segment crosses an
 * inner segment. An inner segment is an edge with both incident nodes marked
 * with the "dummy" property.
 *
 * This algorithm scans layer by layer, starting with the second, for type-1
 * conflicts between the current layer and the previous layer. For each layer
 * it scans the nodes from left to right until it reaches one that is incident
 * on an inner segment. It then scans predecessors to determine if they have
 * edges that cross that inner segment. At the end a final scan is done for all
 * nodes on the current rank to see if they cross the last visited inner
 * segment.
 *
 * This algorithm (safely) assumes that a dummy node will only be incident on a
 * single node in the layers being scanned.
 */
function findType1Conflicts(g,layering){var conflicts={};function visitLayer(prevLayer,layer){var
// last visited node in the previous layer that is incident on an inner
// segment.
k0=0,
// Tracks the last node in this layer scanned for crossings with a type-1
// segment.
scanPos=0,prevLayerLength=prevLayer.length,lastNode=_.last(layer);return _.forEach(layer,(function(v,i){var w=findOtherInnerSegmentNode(g,v),k1=w?g.node(w).order:prevLayerLength;(w||v===lastNode)&&(_.forEach(layer.slice(scanPos,i+1),(function(scanNode){_.forEach(g.predecessors(scanNode),(function(u){var uLabel=g.node(u),uPos=uLabel.order;!(uPos<k0||k1<uPos)||uLabel.dummy&&g.node(scanNode).dummy||addConflict(conflicts,u,scanNode)}))})),scanPos=i+1,k0=k1)})),layer}return _.reduce(layering,visitLayer),conflicts}function findType2Conflicts(g,layering){var conflicts={};function scan(south,southPos,southEnd,prevNorthBorder,nextNorthBorder){var v;_.forEach(_.range(southPos,southEnd),(function(i){v=south[i],g.node(v).dummy&&_.forEach(g.predecessors(v),(function(u){var uNode=g.node(u);uNode.dummy&&(uNode.order<prevNorthBorder||uNode.order>nextNorthBorder)&&addConflict(conflicts,u,v)}))}))}function visitLayer(north,south){var nextNorthPos,prevNorthPos=-1,southPos=0;return _.forEach(south,(function(v,southLookahead){if("border"===g.node(v).dummy){var predecessors=g.predecessors(v);predecessors.length&&(nextNorthPos=g.node(predecessors[0]).order,scan(south,southPos,southLookahead,prevNorthPos,nextNorthPos),southPos=southLookahead,prevNorthPos=nextNorthPos)}scan(south,southPos,south.length,nextNorthPos,north.length)})),south}return _.reduce(layering,visitLayer),conflicts}function findOtherInnerSegmentNode(g,v){if(g.node(v).dummy)return _.find(g.predecessors(v),(function(u){return g.node(u).dummy}))}function addConflict(conflicts,v,w){if(v>w){var tmp=v;v=w,w=tmp}var conflictsV=conflicts[v];conflictsV||(conflicts[v]=conflictsV={}),conflictsV[w]=!0}function hasConflict(conflicts,v,w){if(v>w){var tmp=v;v=w,w=tmp}return _.has(conflicts[v],w)}
/*
 * Try to align nodes into vertical "blocks" where possible. This algorithm
 * attempts to align a node with one of its median neighbors. If the edge
 * connecting a neighbor is a type-1 conflict then we ignore that possibility.
 * If a previous node has already formed a block with a node after the node
 * we're trying to form a block with, we also ignore that possibility - our
 * blocks would be split in that scenario.
 */function verticalAlignment(g,layering,conflicts,neighborFn){var root={},align={},pos={};
// We cache the position here based on the layering because the graph and
// layering may be out of sync. The layering matrix is manipulated to
// generate different extreme alignments.
return _.forEach(layering,(function(layer){_.forEach(layer,(function(v,order){root[v]=v,align[v]=v,pos[v]=order}))})),_.forEach(layering,(function(layer){var prevIdx=-1;_.forEach(layer,(function(v){var ws=neighborFn(v);if(ws.length){ws=_.sortBy(ws,(function(w){return pos[w]}));for(var mp=(ws.length-1)/2,i=Math.floor(mp),il=Math.ceil(mp);i<=il;++i){var w=ws[i];align[v]===v&&prevIdx<pos[w]&&!hasConflict(conflicts,v,w)&&(align[w]=v,align[v]=root[v]=root[w],prevIdx=pos[w])}}}))})),{root:root,align:align}}function horizontalCompaction(g,layering,root,align,reverseSep){
// This portion of the algorithm differs from BK due to a number of problems.
// Instead of their algorithm we construct a new block graph and do two
// sweeps. The first sweep places blocks with the smallest possible
// coordinates. The second sweep removes unused space by moving blocks to the
// greatest coordinates without violating separation.
var xs={},blockG=buildBlockGraph(g,layering,root,reverseSep),borderType=reverseSep?"borderLeft":"borderRight";function iterate(setXsFunc,nextNodesFunc){var stack=blockG.nodes(),elem=stack.pop(),visited={};while(elem)visited[elem]?setXsFunc(elem):(visited[elem]=!0,stack.push(elem),stack=stack.concat(nextNodesFunc(elem))),elem=stack.pop()}
// First pass, assign smallest coordinates
function pass1(elem){xs[elem]=blockG.inEdges(elem).reduce((function(acc,e){return Math.max(acc,xs[e.v]+blockG.edge(e))}),0)}
// Second pass, assign greatest coordinates
function pass2(elem){var min=blockG.outEdges(elem).reduce((function(acc,e){return Math.min(acc,xs[e.w]-blockG.edge(e))}),Number.POSITIVE_INFINITY),node=g.node(elem);min!==Number.POSITIVE_INFINITY&&node.borderType!==borderType&&(xs[elem]=Math.max(xs[elem],min))}return iterate(pass1,blockG.predecessors.bind(blockG)),iterate(pass2,blockG.successors.bind(blockG)),
// Assign x coordinates to all nodes
_.forEach(align,(function(v){xs[v]=xs[root[v]]})),xs}function buildBlockGraph(g,layering,root,reverseSep){var blockGraph=new Graph,graphLabel=g.graph(),sepFn=sep(graphLabel.nodesep,graphLabel.edgesep,reverseSep);return _.forEach(layering,(function(layer){var u;_.forEach(layer,(function(v){var vRoot=root[v];if(blockGraph.setNode(vRoot),u){var uRoot=root[u],prevMax=blockGraph.edge(uRoot,vRoot);blockGraph.setEdge(uRoot,vRoot,Math.max(sepFn(g,v,u),prevMax||0))}u=v}))})),blockGraph}
/*
 * Returns the alignment that has the smallest width of the given alignments.
 */function findSmallestWidthAlignment(g,xss){return _.minBy(_.values(xss),(function(xs){var max=Number.NEGATIVE_INFINITY,min=Number.POSITIVE_INFINITY;return _.forIn(xs,(function(x,v){var halfWidth=width(g,v)/2;max=Math.max(x+halfWidth,max),min=Math.min(x-halfWidth,min)})),max-min}))}
/*
 * Align the coordinates of each of the layout alignments such that
 * left-biased alignments have their minimum coordinate at the same point as
 * the minimum coordinate of the smallest width alignment and right-biased
 * alignments have their maximum coordinate at the same point as the maximum
 * coordinate of the smallest width alignment.
 */function alignCoordinates(xss,alignTo){var alignToVals=_.values(alignTo),alignToMin=_.min(alignToVals),alignToMax=_.max(alignToVals);_.forEach(["u","d"],(function(vert){_.forEach(["l","r"],(function(horiz){var delta,alignment=vert+horiz,xs=xss[alignment];if(xs!==alignTo){var xsVals=_.values(xs);delta="l"===horiz?alignToMin-_.min(xsVals):alignToMax-_.max(xsVals),delta&&(xss[alignment]=_.mapValues(xs,(function(x){return x+delta})))}}))}))}function balance(xss,align){return _.mapValues(xss.ul,(function(ignore,v){if(align)return xss[align.toLowerCase()][v];var xs=_.sortBy(_.map(xss,v));return(xs[1]+xs[2])/2}))}function positionX(g){var adjustedLayering,layering=util.buildLayerMatrix(g),conflicts=_.merge(findType1Conflicts(g,layering),findType2Conflicts(g,layering)),xss={};_.forEach(["u","d"],(function(vert){adjustedLayering="u"===vert?layering:_.values(layering).reverse(),_.forEach(["l","r"],(function(horiz){"r"===horiz&&(adjustedLayering=_.map(adjustedLayering,(function(inner){return _.values(inner).reverse()})));var neighborFn=("u"===vert?g.predecessors:g.successors).bind(g),align=verticalAlignment(g,adjustedLayering,conflicts,neighborFn),xs=horizontalCompaction(g,adjustedLayering,align.root,align.align,"r"===horiz);"r"===horiz&&(xs=_.mapValues(xs,(function(x){return-x}))),xss[vert+horiz]=xs}))}));var smallestWidth=findSmallestWidthAlignment(g,xss);return alignCoordinates(xss,smallestWidth),balance(xss,g.graph().align)}function sep(nodeSep,edgeSep,reverseSep){return function(g,v,w){var delta,vLabel=g.node(v),wLabel=g.node(w),sum=0;if(sum+=vLabel.width/2,_.has(vLabel,"labelpos"))switch(vLabel.labelpos.toLowerCase()){case"l":delta=-vLabel.width/2;break;case"r":delta=vLabel.width/2;break}if(delta&&(sum+=reverseSep?delta:-delta),delta=0,sum+=(vLabel.dummy?edgeSep:nodeSep)/2,sum+=(wLabel.dummy?edgeSep:nodeSep)/2,sum+=wLabel.width/2,_.has(wLabel,"labelpos"))switch(wLabel.labelpos.toLowerCase()){case"l":delta=wLabel.width/2;break;case"r":delta=-wLabel.width/2;break}return delta&&(sum+=reverseSep?delta:-delta),delta=0,sum}}function width(g,v){return g.node(v).width}
/***/
/*
 * This module provides coordinate assignment based on Brandes and Köpf, "Fast
 * and Simple Horizontal Coordinate Assignment."
 */
module.exports={positionX:positionX,findType1Conflicts:findType1Conflicts,findType2Conflicts:findType2Conflicts,addConflict:addConflict,hasConflict:hasConflict,verticalAlignment:verticalAlignment,horizontalCompaction:horizontalCompaction,alignCoordinates:alignCoordinates,findSmallestWidthAlignment:findSmallestWidthAlignment,balance:balance}},
/***/617873:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";var _=__webpack_require__(38436),util=__webpack_require__(811138),positionX=__webpack_require__(503573).positionX;function position(g){g=util.asNonCompoundGraph(g),positionY(g),_.forEach(positionX(g),(function(x,v){g.node(v).x=x}))}function positionY(g){var layering=util.buildLayerMatrix(g),rankSep=g.graph().ranksep,prevY=0;_.forEach(layering,(function(layer){var maxHeight=_.max(_.map(layer,(function(v){return g.node(v).height})));_.forEach(layer,(function(v){g.node(v).y=prevY+maxHeight/2})),prevY+=maxHeight+rankSep}))}
/***/module.exports=position},
/***/720300:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";var _=__webpack_require__(38436),Graph=__webpack_require__(570574).Graph,slack=__webpack_require__(876681).slack;
/*
 * Constructs a spanning tree with tight edges and adjusted the input node's
 * ranks to achieve this. A tight edge is one that is has a length that matches
 * its "minlen" attribute.
 *
 * The basic structure for this function is derived from Gansner, et al., "A
 * Technique for Drawing Directed Graphs."
 *
 * Pre-conditions:
 *
 *    1. Graph must be a DAG.
 *    2. Graph must be connected.
 *    3. Graph must have at least one node.
 *    5. Graph nodes must have been previously assigned a "rank" property that
 *       respects the "minlen" property of incident edges.
 *    6. Graph edges must have a "minlen" property.
 *
 * Post-conditions:
 *
 *    - Graph nodes will have their rank adjusted to ensure that all edges are
 *      tight.
 *
 * Returns a tree (undirected graph) that is constructed using only "tight"
 * edges.
 */
function feasibleTree(g){var edge,delta,t=new Graph({directed:!1}),start=g.nodes()[0],size=g.nodeCount();
// Choose arbitrary node from which to start our tree
t.setNode(start,{});while(tightTree(t,g)<size)edge=findMinSlackEdge(t,g),delta=t.hasNode(edge.v)?slack(g,edge):-slack(g,edge),shiftRanks(t,g,delta);return t}
/*
 * Finds a maximal tree of tight edges and returns the number of nodes in the
 * tree.
 */function tightTree(t,g){function dfs(v){_.forEach(g.nodeEdges(v),(function(e){var edgeV=e.v,w=v===edgeV?e.w:edgeV;t.hasNode(w)||slack(g,e)||(t.setNode(w,{}),t.setEdge(v,w,{}),dfs(w))}))}return _.forEach(t.nodes(),dfs),t.nodeCount()}
/*
 * Finds the edge with the smallest slack that is incident on tree and returns
 * it.
 */function findMinSlackEdge(t,g){return _.minBy(g.edges(),(function(e){if(t.hasNode(e.v)!==t.hasNode(e.w))return slack(g,e)}))}function shiftRanks(t,g,delta){_.forEach(t.nodes(),(function(v){g.node(v).rank+=delta}))}
/***/module.exports=feasibleTree},
/***/578093:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";var rankUtil=__webpack_require__(876681),longestPath=rankUtil.longestPath,feasibleTree=__webpack_require__(720300),networkSimplex=__webpack_require__(442472);
/*
 * Assigns a rank to each node in the input graph that respects the "minlen"
 * constraint specified on edges between nodes.
 *
 * This basic structure is derived from Gansner, et al., "A Technique for
 * Drawing Directed Graphs."
 *
 * Pre-conditions:
 *
 *    1. Graph must be a connected DAG
 *    2. Graph nodes must be objects
 *    3. Graph edges must have "weight" and "minlen" attributes
 *
 * Post-conditions:
 *
 *    1. Graph nodes will have a "rank" attribute based on the results of the
 *       algorithm. Ranks can start at any index (including negative), we'll
 *       fix them up later.
 */
function rank(g){switch(g.graph().ranker){case"network-simplex":networkSimplexRanker(g);break;case"tight-tree":tightTreeRanker(g);break;case"longest-path":longestPathRanker(g);break;default:networkSimplexRanker(g)}}
// A fast and simple ranker, but results are far from optimal.
module.exports=rank;var longestPathRanker=longestPath;function tightTreeRanker(g){longestPath(g),feasibleTree(g)}function networkSimplexRanker(g){networkSimplex(g)}
/***/},
/***/442472:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";var _=__webpack_require__(38436),feasibleTree=__webpack_require__(720300),slack=__webpack_require__(876681).slack,initRank=__webpack_require__(876681).longestPath,preorder=__webpack_require__(570574).alg.preorder,postorder=__webpack_require__(570574).alg.postorder,simplify=__webpack_require__(811138).simplify;
/*
 * The network simplex algorithm assigns ranks to each node in the input graph
 * and iteratively improves the ranking to reduce the length of edges.
 *
 * Preconditions:
 *
 *    1. The input graph must be a DAG.
 *    2. All nodes in the graph must have an object value.
 *    3. All edges in the graph must have "minlen" and "weight" attributes.
 *
 * Postconditions:
 *
 *    1. All nodes in the graph will have an assigned "rank" attribute that has
 *       been optimized by the network simplex algorithm. Ranks start at 0.
 *
 *
 * A rough sketch of the algorithm is as follows:
 *
 *    1. Assign initial ranks to each node. We use the longest path algorithm,
 *       which assigns ranks to the lowest position possible. In general this
 *       leads to very wide bottom ranks and unnecessarily long edges.
 *    2. Construct a feasible tight tree. A tight tree is one such that all
 *       edges in the tree have no slack (difference between length of edge
 *       and minlen for the edge). This by itself greatly improves the assigned
 *       rankings by shorting edges.
 *    3. Iteratively find edges that have negative cut values. Generally a
 *       negative cut value indicates that the edge could be removed and a new
 *       tree edge could be added to produce a more compact graph.
 *
 * Much of the algorithms here are derived from Gansner, et al., "A Technique
 * for Drawing Directed Graphs." The structure of the file roughly follows the
 * structure of the overall algorithm.
 */
function networkSimplex(g){g=simplify(g),initRank(g);var e,f,t=feasibleTree(g);initLowLimValues(t),initCutValues(t,g);while(e=leaveEdge(t))f=enterEdge(t,g,e),exchangeEdges(t,g,e,f)}
/*
 * Initializes cut values for all edges in the tree.
 */function initCutValues(t,g){var vs=postorder(t,t.nodes());vs=vs.slice(0,vs.length-1),_.forEach(vs,(function(v){assignCutValue(t,g,v)}))}function assignCutValue(t,g,child){var childLab=t.node(child),parent=childLab.parent;t.edge(child,parent).cutvalue=calcCutValue(t,g,child)}
/*
 * Given the tight tree, its graph, and a child in the graph calculate and
 * return the cut value for the edge between the child and its parent.
 */function calcCutValue(t,g,child){var childLab=t.node(child),parent=childLab.parent,childIsTail=!0,graphEdge=g.edge(child,parent),cutValue=0;return graphEdge||(childIsTail=!1,graphEdge=g.edge(parent,child)),cutValue=graphEdge.weight,_.forEach(g.nodeEdges(child),(function(e){var isOutEdge=e.v===child,other=isOutEdge?e.w:e.v;if(other!==parent){var pointsToHead=isOutEdge===childIsTail,otherWeight=g.edge(e).weight;if(cutValue+=pointsToHead?otherWeight:-otherWeight,isTreeEdge(t,child,other)){var otherCutValue=t.edge(child,other).cutvalue;cutValue+=pointsToHead?-otherCutValue:otherCutValue}}})),cutValue}function initLowLimValues(tree,root){arguments.length<2&&(root=tree.nodes()[0]),dfsAssignLowLim(tree,{},1,root)}function dfsAssignLowLim(tree,visited,nextLim,v,parent){var low=nextLim,label=tree.node(v);return visited[v]=!0,_.forEach(tree.neighbors(v),(function(w){_.has(visited,w)||(nextLim=dfsAssignLowLim(tree,visited,nextLim,w,v))})),label.low=low,label.lim=nextLim++,parent?label.parent=parent:
// TODO should be able to remove this when we incrementally update low lim
delete label.parent,nextLim}function leaveEdge(tree){return _.find(tree.edges(),(function(e){return tree.edge(e).cutvalue<0}))}function enterEdge(t,g,edge){var v=edge.v,w=edge.w;
// For the rest of this function we assume that v is the tail and w is the
// head, so if we don't have this edge in the graph we should flip it to
// match the correct orientation.
g.hasEdge(v,w)||(v=edge.w,w=edge.v);var vLabel=t.node(v),wLabel=t.node(w),tailLabel=vLabel,flip=!1;
// If the root is in the tail of the edge then we need to flip the logic that
// checks for the head and tail nodes in the candidates function below.
vLabel.lim>wLabel.lim&&(tailLabel=wLabel,flip=!0);var candidates=_.filter(g.edges(),(function(edge){return flip===isDescendant(t,t.node(edge.v),tailLabel)&&flip!==isDescendant(t,t.node(edge.w),tailLabel)}));return _.minBy(candidates,(function(edge){return slack(g,edge)}))}function exchangeEdges(t,g,e,f){var v=e.v,w=e.w;t.removeEdge(v,w),t.setEdge(f.v,f.w,{}),initLowLimValues(t),initCutValues(t,g),updateRanks(t,g)}function updateRanks(t,g){var root=_.find(t.nodes(),(function(v){return!g.node(v).parent})),vs=preorder(t,root);vs=vs.slice(1),_.forEach(vs,(function(v){var parent=t.node(v).parent,edge=g.edge(v,parent),flipped=!1;edge||(edge=g.edge(parent,v),flipped=!0),g.node(v).rank=g.node(parent).rank+(flipped?edge.minlen:-edge.minlen)}))}
/*
 * Returns true if the edge is in the tree.
 */function isTreeEdge(tree,u,v){return tree.hasEdge(u,v)}
/*
 * Returns true if the specified node is descendant of the root node per the
 * assigned low and lim attributes in the tree.
 */function isDescendant(tree,vLabel,rootLabel){return rootLabel.low<=vLabel.lim&&vLabel.lim<=rootLabel.lim}
/***/module.exports=networkSimplex,
// Expose some internals for testing purposes
networkSimplex.initLowLimValues=initLowLimValues,networkSimplex.initCutValues=initCutValues,networkSimplex.calcCutValue=calcCutValue,networkSimplex.leaveEdge=leaveEdge,networkSimplex.enterEdge=enterEdge,networkSimplex.exchangeEdges=exchangeEdges},
/***/876681:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";var _=__webpack_require__(38436);
/*
 * Initializes ranks for the input graph using the longest path algorithm. This
 * algorithm scales well and is fast in practice, it yields rather poor
 * solutions. Nodes are pushed to the lowest layer possible, leaving the bottom
 * ranks wide and leaving edges longer than necessary. However, due to its
 * speed, this algorithm is good for getting an initial ranking that can be fed
 * into other algorithms.
 *
 * This algorithm does not normalize layers because it will be used by other
 * algorithms in most cases. If using this algorithm directly, be sure to
 * run normalize at the end.
 *
 * Pre-conditions:
 *
 *    1. Input graph is a DAG.
 *    2. Input graph node labels can be assigned properties.
 *
 * Post-conditions:
 *
 *    1. Each node will be assign an (unnormalized) "rank" property.
 */
function longestPath(g){var visited={};function dfs(v){var label=g.node(v);if(_.has(visited,v))return label.rank;visited[v]=!0;var rank=_.min(_.map(g.outEdges(v),(function(e){return dfs(e.w)-g.edge(e).minlen})));return rank!==Number.POSITIVE_INFINITY&&// return value of _.map([]) for Lodash 3
void 0!==rank&&// return value of _.map([]) for Lodash 4
null!==rank||(// return value of _.map([null])
rank=0),label.rank=rank}_.forEach(g.sources(),dfs)}
/*
 * Returns the amount of slack for the given edge. The slack is defined as the
 * difference between the length of the edge and its minimum length.
 */function slack(g,e){return g.node(e.w).rank-g.node(e.v).rank-g.edge(e).minlen}
/***/module.exports={longestPath:longestPath,slack:slack}},
/***/811138:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";
/* eslint "no-console": off */var _=__webpack_require__(38436),Graph=__webpack_require__(570574).Graph;
/*
 * Adds a dummy node to the graph and return v.
 */
function addDummyNode(g,type,attrs,name){var v;do{v=_.uniqueId(name)}while(g.hasNode(v));return attrs.dummy=type,g.setNode(v,attrs),v}
/*
 * Returns a new graph with only simple edges. Handles aggregation of data
 * associated with multi-edges.
 */function simplify(g){var simplified=(new Graph).setGraph(g.graph());return _.forEach(g.nodes(),(function(v){simplified.setNode(v,g.node(v))})),_.forEach(g.edges(),(function(e){var simpleLabel=simplified.edge(e.v,e.w)||{weight:0,minlen:1},label=g.edge(e);simplified.setEdge(e.v,e.w,{weight:simpleLabel.weight+label.weight,minlen:Math.max(simpleLabel.minlen,label.minlen)})})),simplified}function asNonCompoundGraph(g){var simplified=new Graph({multigraph:g.isMultigraph()}).setGraph(g.graph());return _.forEach(g.nodes(),(function(v){g.children(v).length||simplified.setNode(v,g.node(v))})),_.forEach(g.edges(),(function(e){simplified.setEdge(e,g.edge(e))})),simplified}function successorWeights(g){var weightMap=_.map(g.nodes(),(function(v){var sucs={};return _.forEach(g.outEdges(v),(function(e){sucs[e.w]=(sucs[e.w]||0)+g.edge(e).weight})),sucs}));return _.zipObject(g.nodes(),weightMap)}function predecessorWeights(g){var weightMap=_.map(g.nodes(),(function(v){var preds={};return _.forEach(g.inEdges(v),(function(e){preds[e.v]=(preds[e.v]||0)+g.edge(e).weight})),preds}));return _.zipObject(g.nodes(),weightMap)}
/*
 * Finds where a line starting at point ({x, y}) would intersect a rectangle
 * ({x, y, width, height}) if it were pointing at the rectangle's center.
 */function intersectRect(rect,point){var sx,sy,x=rect.x,y=rect.y,dx=point.x-x,dy=point.y-y,w=rect.width/2,h=rect.height/2;if(!dx&&!dy)throw new Error("Not possible to find intersection inside of the rectangle");return Math.abs(dy)*w>Math.abs(dx)*h?(
// Intersection is top or bottom of rect.
dy<0&&(h=-h),sx=h*dx/dy,sy=h):(
// Intersection is left or right of rect.
dx<0&&(w=-w),sx=w,sy=w*dy/dx),{x:x+sx,y:y+sy}}
/*
 * Given a DAG with each node assigned "rank" and "order" properties, this
 * function will produce a matrix with the ids of each node.
 */function buildLayerMatrix(g){var layering=_.map(_.range(maxRank(g)+1),(function(){return[]}));return _.forEach(g.nodes(),(function(v){var node=g.node(v),rank=node.rank;_.isUndefined(rank)||(layering[rank][node.order]=v)})),layering}
/*
 * Adjusts the ranks for all nodes in the graph such that all nodes v have
 * rank(v) >= 0 and at least one node w has rank(w) = 0.
 */function normalizeRanks(g){var min=_.min(_.map(g.nodes(),(function(v){return g.node(v).rank})));_.forEach(g.nodes(),(function(v){var node=g.node(v);_.has(node,"rank")&&(node.rank-=min)}))}function removeEmptyRanks(g){
// Ranks may not start at 0, so we need to offset them
var offset=_.min(_.map(g.nodes(),(function(v){return g.node(v).rank}))),layers=[];_.forEach(g.nodes(),(function(v){var rank=g.node(v).rank-offset;layers[rank]||(layers[rank]=[]),layers[rank].push(v)}));var delta=0,nodeRankFactor=g.graph().nodeRankFactor;_.forEach(layers,(function(vs,i){_.isUndefined(vs)&&i%nodeRankFactor!==0?--delta:delta&&_.forEach(vs,(function(v){g.node(v).rank+=delta}))}))}function addBorderNode(g,prefix,rank,order){var node={width:0,height:0};return arguments.length>=4&&(node.rank=rank,node.order=order),addDummyNode(g,"border",node,prefix)}function maxRank(g){return _.max(_.map(g.nodes(),(function(v){var rank=g.node(v).rank;if(!_.isUndefined(rank))return rank})))}
/*
 * Partition a collection into two groups: `lhs` and `rhs`. If the supplied
 * function returns true for an entry it goes into `lhs`. Otherwise it goes
 * into `rhs.
 */function partition(collection,fn){var result={lhs:[],rhs:[]};return _.forEach(collection,(function(value){fn(value)?result.lhs.push(value):result.rhs.push(value)})),result}
/*
 * Returns a new function that wraps `fn` with a timer. The wrapper logs the
 * time it takes to execute the function.
 */function time(name,fn){_.now();try{return fn()}finally{}}function notime(name,fn){return fn()}
/***/module.exports={addDummyNode:addDummyNode,simplify:simplify,asNonCompoundGraph:asNonCompoundGraph,successorWeights:successorWeights,predecessorWeights:predecessorWeights,intersectRect:intersectRect,buildLayerMatrix:buildLayerMatrix,normalizeRanks:normalizeRanks,removeEmptyRanks:removeEmptyRanks,addBorderNode:addBorderNode,maxRank:maxRank,partition:partition,time:time,notime:notime}},
/***/988177:
/***/function(module){module.exports="0.8.5";
/***/},
/***/242504:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
// CONCATENATED MODULE: ./node_modules/date-format-parse/es/util.js
function isDate(value){return value instanceof Date||"[object Date]"===Object.prototype.toString.call(value)}function toDate(value){return isDate(value)?new Date(value.getTime()):null==value?new Date(NaN):new Date(value)}function isValidDate(value){return isDate(value)&&!isNaN(value.getTime())}function startOfWeek(value){var firstDayOfWeek=arguments.length>1&&void 0!==arguments[1]?arguments[1]:0;if(!(firstDayOfWeek>=0&&firstDayOfWeek<=6))throw new RangeError("weekStartsOn must be between 0 and 6");var date=toDate(value),day=date.getDay(),diff=(day+7-firstDayOfWeek)%7;return date.setDate(date.getDate()-diff),date.setHours(0,0,0,0),date}function util_startOfWeekYear(value){var _ref=arguments.length>1&&void 0!==arguments[1]?arguments[1]:{},_ref$firstDayOfWeek=_ref.firstDayOfWeek,firstDayOfWeek=void 0===_ref$firstDayOfWeek?0:_ref$firstDayOfWeek,_ref$firstWeekContain=_ref.firstWeekContainsDate,firstWeekContainsDate=void 0===_ref$firstWeekContain?1:_ref$firstWeekContain;if(!(firstWeekContainsDate>=1&&firstWeekContainsDate<=7))throw new RangeError("firstWeekContainsDate must be between 1 and 7");for(var date=toDate(value),year=date.getFullYear(),firstDateOfFirstWeek=new Date(0),i=year+1;i>=year-1;i--)if(firstDateOfFirstWeek.setFullYear(i,0,firstWeekContainsDate),firstDateOfFirstWeek.setHours(0,0,0,0),firstDateOfFirstWeek=startOfWeek(firstDateOfFirstWeek,firstDayOfWeek),date.getTime()>=firstDateOfFirstWeek.getTime())break;return firstDateOfFirstWeek}function getWeek(value){var _ref2=arguments.length>1&&void 0!==arguments[1]?arguments[1]:{},_ref2$firstDayOfWeek=_ref2.firstDayOfWeek,firstDayOfWeek=void 0===_ref2$firstDayOfWeek?0:_ref2$firstDayOfWeek,_ref2$firstWeekContai=_ref2.firstWeekContainsDate,firstWeekContainsDate=void 0===_ref2$firstWeekContai?1:_ref2$firstWeekContai,date=toDate(value),firstDateOfThisWeek=startOfWeek(date,firstDayOfWeek),firstDateOfFirstWeek=util_startOfWeekYear(date,{firstDayOfWeek:firstDayOfWeek,firstWeekContainsDate:firstWeekContainsDate}),diff=firstDateOfThisWeek.getTime()-firstDateOfFirstWeek.getTime();return Math.round(diff/6048e5)+1}__webpack_require__.d(__webpack_exports__,{WU:function(){/* reexport */return format}});// CONCATENATED MODULE: ./node_modules/date-format-parse/es/locale/en.js
var locale={months:["January","February","March","April","May","June","July","August","September","October","November","December"],monthsShort:["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],weekdays:["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"],weekdaysShort:["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],weekdaysMin:["Su","Mo","Tu","We","Th","Fr","Sa"],firstDayOfWeek:0,firstWeekContainsDate:1},en=locale,REGEX_FORMAT=/\[([^\]]+)]|YYYY|YY?|M{1,4}|D{1,2}|d{1,4}|H{1,2}|h{1,2}|m{1,2}|s{1,2}|Z{1,2}|S{1,3}|w{1,2}|x|X|a|A/g;
/* harmony default export */function pad(val){var len=arguments.length>1&&void 0!==arguments[1]?arguments[1]:2,output="".concat(Math.abs(val)),sign=val<0?"-":"";while(output.length<len)output="0".concat(output);return sign+output}function getOffset(date){return 15*Math.round(date.getTimezoneOffset()/15)}function formatTimezone(offset){var delimeter=arguments.length>1&&void 0!==arguments[1]?arguments[1]:"",sign=offset>0?"-":"+",absOffset=Math.abs(offset),hours=Math.floor(absOffset/60),minutes=absOffset%60;return sign+pad(hours,2)+delimeter+pad(minutes,2)}var meridiem=function(h,_,isLowercase){var word=h<12?"AM":"PM";return isLowercase?word.toLocaleLowerCase():word},formatFlags={Y:function(date){var y=date.getFullYear();return y<=9999?"".concat(y):"+".concat(y)},
// Year: 00, 01, ..., 99
YY:function(date){return pad(date.getFullYear(),4).substr(2)},
// Year: 1900, 1901, ..., 2099
YYYY:function(date){return pad(date.getFullYear(),4)},
// Month: 1, 2, ..., 12
M:function(date){return date.getMonth()+1},
// Month: 01, 02, ..., 12
MM:function(date){return pad(date.getMonth()+1,2)},MMM:function(date,locale){return locale.monthsShort[date.getMonth()]},MMMM:function(date,locale){return locale.months[date.getMonth()]},
// Day of month: 1, 2, ..., 31
D:function(date){return date.getDate()},
// Day of month: 01, 02, ..., 31
DD:function(date){return pad(date.getDate(),2)},
// Hour: 0, 1, ... 23
H:function(date){return date.getHours()},
// Hour: 00, 01, ..., 23
HH:function(date){return pad(date.getHours(),2)},
// Hour: 1, 2, ..., 12
h:function(date){var hours=date.getHours();return 0===hours?12:hours>12?hours%12:hours},
// Hour: 01, 02, ..., 12
hh:function(){var hours=formatFlags.h.apply(formatFlags,arguments);return pad(hours,2)},
// Minute: 0, 1, ..., 59
m:function(date){return date.getMinutes()},
// Minute: 00, 01, ..., 59
mm:function(date){return pad(date.getMinutes(),2)},
// Second: 0, 1, ..., 59
s:function(date){return date.getSeconds()},
// Second: 00, 01, ..., 59
ss:function(date){return pad(date.getSeconds(),2)},
// 1/10 of second: 0, 1, ..., 9
S:function(date){return Math.floor(date.getMilliseconds()/100)},
// 1/100 of second: 00, 01, ..., 99
SS:function(date){return pad(Math.floor(date.getMilliseconds()/10),2)},
// Millisecond: 000, 001, ..., 999
SSS:function(date){return pad(date.getMilliseconds(),3)},
// Day of week: 0, 1, ..., 6
d:function(date){return date.getDay()},
// Day of week: 'Su', 'Mo', ..., 'Sa'
dd:function(date,locale){return locale.weekdaysMin[date.getDay()]},
// Day of week: 'Sun', 'Mon',..., 'Sat'
ddd:function(date,locale){return locale.weekdaysShort[date.getDay()]},
// Day of week: 'Sunday', 'Monday', ...,'Saturday'
dddd:function(date,locale){return locale.weekdays[date.getDay()]},
// AM, PM
A:function(date,locale){var meridiemFunc=locale.meridiem||meridiem;return meridiemFunc(date.getHours(),date.getMinutes(),!1)},
// am, pm
a:function(date,locale){var meridiemFunc=locale.meridiem||meridiem;return meridiemFunc(date.getHours(),date.getMinutes(),!0)},
// Timezone: -01:00, +00:00, ... +12:00
Z:function(date){return formatTimezone(getOffset(date),":")},
// Timezone: -0100, +0000, ... +1200
ZZ:function(date){return formatTimezone(getOffset(date))},
// Seconds timestamp: 512969520
X:function(date){return Math.floor(date.getTime()/1e3)},
// Milliseconds timestamp: 512969520900
x:function(date){return date.getTime()},w:function(date,locale){return getWeek(date,{firstDayOfWeek:locale.firstDayOfWeek,firstWeekContainsDate:locale.firstWeekContainsDate})},ww:function(date,locale){return pad(formatFlags.w(date,locale),2)}};function format(val,str){var options=arguments.length>2&&void 0!==arguments[2]?arguments[2]:{},formatStr=str?String(str):"YYYY-MM-DDTHH:mm:ss.SSSZ",date=toDate(val);if(!isValidDate(date))return"Invalid Date";var locale=options.locale||en;return formatStr.replace(REGEX_FORMAT,(function(match,p1){return p1||("function"===typeof formatFlags[match]?"".concat(formatFlags[match](date,locale)):match)}))}function _slicedToArray(arr,i){return _arrayWithHoles(arr)||_iterableToArrayLimit(arr,i)||_nonIterableRest()}function _nonIterableRest(){throw new TypeError("Invalid attempt to destructure non-iterable instance")}function _iterableToArrayLimit(arr,i){if(Symbol.iterator in Object(arr)||"[object Arguments]"===Object.prototype.toString.call(arr)){var _arr=[],_n=!0,_d=!1,_e=void 0;try{for(var _s,_i=arr[Symbol.iterator]();!(_n=(_s=_i.next()).done);_n=!0)if(_arr.push(_s.value),i&&_arr.length===i)break}catch(err){_d=!0,_e=err}finally{try{_n||null==_i["return"]||_i["return"]()}finally{if(_d)throw _e}}return _arr}}function _arrayWithHoles(arr){if(Array.isArray(arr))return arr}function _defineProperty(obj,key,value){return key in obj?Object.defineProperty(obj,key,{value:value,enumerable:!0,configurable:!0,writable:!0}):obj[key]=value,obj}var match1=/\d/,match2=/\d\d/,match3=/\d{3}/,match4=/\d{4}/,match1to2=/\d\d?/,matchShortOffset=/[+-]\d\d:?\d\d/,matchSigned=/[+-]?\d+/,matchTimestamp=/[+-]?\d+(\.\d{1,3})?/,YEAR="year",MONTH="month",DAY="day",HOUR="hour",MINUTE="minute",SECOND="second",MILLISECOND="millisecond",parseFlags={},addParseFlag=function(token,regex,callback){var func,tokens=Array.isArray(token)?token:[token];func="string"===typeof callback?function(input){var value=parseInt(input,10);return _defineProperty({},callback,value)}:callback,tokens.forEach((function(key){parseFlags[key]=[regex,func]}))},escapeStringRegExp=function(str){return str.replace(/[|\\{}()[\]^$+*?.]/g,"\\$&")},matchWordRegExp=function(localeKey){return function(locale){var array=locale[localeKey];if(!Array.isArray(array))throw new Error("Locale[".concat(localeKey,"] need an array"));return new RegExp(array.map(escapeStringRegExp).join("|"))}},matchWordCallback=function(localeKey,key){return function(input,locale){var array=locale[localeKey];if(!Array.isArray(array))throw new Error("Locale[".concat(localeKey,"] need an array"));var index=array.indexOf(input);if(index<0)throw new Error("Invalid Word");return _defineProperty({},key,index)}};function matchMeridiem(locale){return locale.meridiemParse||/[ap]\.?m?\.?/i}function defaultIsPM(input){return"p"==="".concat(input).toLowerCase().charAt(0)}function offsetFromString(str){var _ref8=str.match(/([+-]|\d\d)/g)||["-","0","0"],_ref9=_slicedToArray(_ref8,3),symbol=_ref9[0],hour=_ref9[1],minute=_ref9[2],minutes=60*parseInt(hour,10)+parseInt(minute,10);return 0===minutes?0:"+"===symbol?-minutes:+minutes}addParseFlag("Y",matchSigned,YEAR),addParseFlag("YY",match2,(function(input){var year=(new Date).getFullYear(),cent=Math.floor(year/100),value=parseInt(input,10);return value=100*(value>68?cent-1:cent)+value,_defineProperty({},YEAR,value)})),addParseFlag("YYYY",match4,YEAR),addParseFlag("M",match1to2,(function(input){return _defineProperty({},MONTH,parseInt(input,10)-1)})),addParseFlag("MM",match2,(function(input){return _defineProperty({},MONTH,parseInt(input,10)-1)})),addParseFlag("MMM",matchWordRegExp("monthsShort"),matchWordCallback("monthsShort",MONTH)),addParseFlag("MMMM",matchWordRegExp("months"),matchWordCallback("months",MONTH)),addParseFlag("D",match1to2,DAY),addParseFlag("DD",match2,DAY),addParseFlag(["H","h"],match1to2,HOUR),addParseFlag(["HH","hh"],match2,HOUR),addParseFlag("m",match1to2,MINUTE),addParseFlag("mm",match2,MINUTE),addParseFlag("s",match1to2,SECOND),addParseFlag("ss",match2,SECOND),addParseFlag("S",match1,(function(input){return _defineProperty({},MILLISECOND,100*parseInt(input,10))})),addParseFlag("SS",match2,(function(input){return _defineProperty({},MILLISECOND,10*parseInt(input,10))})),addParseFlag("SSS",match3,MILLISECOND),addParseFlag(["A","a"],matchMeridiem,(function(input,locale){var isPM="function"===typeof locale.isPM?locale.isPM(input):defaultIsPM(input);return{isPM:isPM}})),addParseFlag(["Z","ZZ"],matchShortOffset,(function(input){return{offset:offsetFromString(input)}})),addParseFlag("x",matchSigned,(function(input){return{date:new Date(parseInt(input,10))}})),addParseFlag("X",matchTimestamp,(function(input){return{date:new Date(1e3*parseFloat(input))}})),addParseFlag("d",match1,"weekday"),addParseFlag("dd",matchWordRegExp("weekdaysMin"),matchWordCallback("weekdaysMin","weekday")),addParseFlag("ddd",matchWordRegExp("weekdaysShort"),matchWordCallback("weekdaysShort","weekday")),addParseFlag("dddd",matchWordRegExp("weekdays"),matchWordCallback("weekdays","weekday")),addParseFlag("w",match1to2,"week"),addParseFlag("ww",match2,"week")},
/***/944878:
/***/function(module,exports){"use strict";Object.defineProperty(exports,"__esModule",{value:!0}),exports["default"]=void 0;var locale={months:["January","February","March","April","May","June","July","August","September","October","November","December"],monthsShort:["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],weekdays:["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"],weekdaysShort:["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],weekdaysMin:["Su","Mo","Tu","We","Th","Fr","Sa"],firstDayOfWeek:0,firstWeekContainsDate:1},_default=locale;exports["default"]=_default,module.exports=exports.default}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/chunk-common-3e33b011.js.map