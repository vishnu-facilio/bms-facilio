<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	Peak Energy Analysis
       	</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
	<div class="col-lg-12">
		<div id="chart"></div>
	</div>
</div>
<script>

var chartWidth = $("#chart").width();
var context1 = cubism.context()
	.step(1e4)
	.size(chartWidth);

$(document).ready(function() {
	var meters = ["Ground Floor", "Floor 1", "Floor 2", "Floor 3", "Floor 4", "Floor 5", "Floor 6", "Floor 7", "Floor 8", "Floor 9", "Floor 10"];

	d3.select("#chart").selectAll(".axis")
		.data(["top", "bottom"])
		.enter().append("div")
		.attr("class", function(d) { return d + " axis"; })
		.each(function(d) { d3.select(this).call(context1.axis().ticks(12).orient(d)); });
	
	d3.select("#chart").append("div")
		.attr("class", "rule")
		.call(context1.rule());
	
	d3.select("#chart").selectAll(".horizon")
		.data(d3.range(0, meters.length).map(random))
		.enter().insert("div", ".bottom")
		.attr("class", "horizon")
		.call(context1.horizon()
				.height(120)
				.mode("mirror")
				.colors(["#bdd7e7","#bdd7e7"])
				.title(function(d) { return meters[d]; })
				.extent([-10, 10]));
	
	context1.on("focus", function(i) {
		d3.selectAll(".value").style("right", i == null ? null : context1.size() - i + "px");
	});
});

//Replace this with context.graphite and graphite.metric!
function random(x) {
  var value = 0,
      values = [],
      i = 0,
      last;
  return context1.metric(function(start, stop, step, callback) {
    start = +start, stop = +stop;
    if (isNaN(last)) last = start;
    while (last < stop) {
      last += step;
      value = Math.max(-10, Math.min(10, value + .8 * Math.random() - .4 + .2 * Math.cos(i += x * .02)));
      values.push(value);
    }
    callback(null, values = values.slice((start - stop) / step));
  }, x);
}

</script>
<style>

.group {
  margin-bottom: 1em;
}

.axis {
  font: 10px sans-serif;
  position: fixed;
  pointer-events: none;
  z-index: 2;
}

.axis text {
  -webkit-transition: fill-opacity 250ms linear;
}

.axis path {
  display: none;
}

.axis line {
  stroke: #000;
  shape-rendering: crispEdges;
}

.axis.top {
  background-image: linear-gradient(top, #fff 0%, rgba(255,255,255,0) 100%);
  background-image: -o-linear-gradient(top, #fff 0%, rgba(255,255,255,0) 100%);
  background-image: -moz-linear-gradient(top, #fff 0%, rgba(255,255,255,0) 100%);
  background-image: -webkit-linear-gradient(top, #fff 0%, rgba(255,255,255,0) 100%);
  background-image: -ms-linear-gradient(top, #fff 0%, rgba(255,255,255,0) 100%);
  top: 0px;
  padding: 0 0 24px 0;
}

.axis.bottom {
  background-image: linear-gradient(bottom, #fff 0%, rgba(255,255,255,0) 100%);
  background-image: -o-linear-gradient(bottom, #fff 0%, rgba(255,255,255,0) 100%);
  background-image: -moz-linear-gradient(bottom, #fff 0%, rgba(255,255,255,0) 100%);
  background-image: -webkit-linear-gradient(bottom, #fff 0%, rgba(255,255,255,0) 100%);
  background-image: -ms-linear-gradient(bottom, #fff 0%, rgba(255,255,255,0) 100%);
  bottom: 0px;
  padding: 24px 0 0 0;
}

.horizon {
  border-bottom: solid 1px #000;
  overflow: hidden;
  position: relative;
}

.horizon {
  border-top: solid 1px #000;
  border-bottom: solid 1px #000;
}

.horizon + .horizon {
  border-top: none;
}

.horizon canvas {
  display: block;
}

.horizon .title,
.horizon .value {
  bottom: 0;
  line-height: 30px;
  margin: 0 6px;
  position: absolute;
  text-shadow: 0 1px 0 rgba(255,255,255,.5);
  white-space: nowrap;
}

.horizon .title {
  left: 0;
}

.horizon .value {
  right: 0;
}

.line {
  background: #000;
  opacity: .2;
  z-index: 2;
}

@media all and (max-width: 1439px) {
  body { margin: 0px auto; }
  .axis { position: static; }
  .axis.top, .axis.bottom { padding: 0; }
}

</style>