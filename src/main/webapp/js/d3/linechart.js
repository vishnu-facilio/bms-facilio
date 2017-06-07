
line_chart = function(parent_elm, width, height) {

	var margin = {top: 30, right: 30, bottom: 40, left: 50},
    width = width - margin.left - margin.right,
    height = height - margin.top - margin.bottom;

var formatPercent = d3.format("+.0%"),
    formatChange = function(x) { return formatPercent(x - 1); },
    parseDate = d3.time.format("%d-%b-%y").parse;

var x = d3.time.scale()
    .range([0, width]);

var y = d3.scale.log()
    .range([height, 0]);

var xAxis = d3.svg.axis()
    .scale(x)
    .orient("bottom");

var yAxis = d3.svg.axis()
    .scale(y)
    .orient("left")
    .tickSize(-width, 0)
    .tickFormat(formatChange);

var line = d3.svg.line()
    .x(function(d) { return x(d.date); })
    .y(function(d) { return y(d.ratio); });

var svg = d3.select(parent_elm).append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
  .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

var gX = svg.append("g")
    .attr("class", "axis axis--x")
    .attr("transform", "translate(0," + height + ")");

var gY = svg.append("g")
    .attr("class", "axis axis--y");

gY.append("text")
    .attr("class", "axis-title")
    .attr("transform", "rotate(-90)")
    .attr("y", 6)
    .attr("dy", ".71em")
    .text("Change in Price");

d3.tsv("../js/d3/sample-data.tsv", function(error, data) {
  if (error) throw error;

  // Compute price relative to base value (hypothetical purchase price).
  var baseValue = +data[0].close;
  data.forEach(function(d) {
    d.date = parseDate(d.date);
    d.ratio = d.close / baseValue;
  });

  x.domain(d3.extent(data, function(d) { return d.date; }));
  y.domain(d3.extent(data, function(d) { return d.ratio; }));

  // Use a second linear scale for ticks.
  yAxis.tickValues(d3.scale.linear()
      .domain(y.domain())
      .ticks(20));

  gX.call(xAxis);

  gY.call(yAxis)
    .selectAll(".tick")
      .classed("tick--one", function(d) { return Math.abs(d - 1) < 1e-6; });

  svg.append("path")
      .datum(data)
      .attr("class", "line")
      .attr("d", line);
});
};