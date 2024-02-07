import * as d3 from 'd3'

export default {
  drawAxis(chartContext) {
    let defaultColors = [
      '#7cb5ec',
      '#ff7f00',
      '#b2df8a',
      '#1f78b4',
      '#fdbf6f',
      '#33a02c',
      '#cab2d6',
      '#6a3d9a',
      '#fb9a99',
      '#e31a1c',
      '#ffff99',
      '#b15928',
    ]
    let series = []
    chartContext.selector = chartContext.$refs['f-timeseries']
    let margin = chartContext.getMargin(),
      width = chartContext.getWidth() - margin.left - margin.right,
      height = chartContext.getHeight() - margin.bottom - margin.top
    let containerHeight = chartContext.getHeight()
    let arrayLenght = chartContext.data.length
    let chartLayerheight = height * (1 / arrayLenght)
    let minLayerHeight = 120
    if (chartLayerheight < minLayerHeight) {
      chartLayerheight = 120
      height = 120 * arrayLenght
      containerHeight = height + margin.bottom + margin.top
    }
    chartContext.svg
      .attr('width', chartContext.getWidth())
      .attr('height', containerHeight)
    chartContext.series = chartContext.svg
      .select('.chart-group')
      .on('mouseover', function(d) {
        chartContext.verticalMarkerLine.classed('bc-is-active', true)
      })
      .on('mouseout', function(d) {
        chartContext.overlay.style('display', 'none')
        chartContext.verticalMarkerLine.classed('bc-is-active', false)
        chartContext.verticalMarkerContainer.attr(
          'transform',
          'translate(' + 9999 + ',' + margin.bottom + ')'
        )
      })
      .on('mousemove', function(d, i) {
        chartContext.verticalMarkerContainer
          .selectAll('.circle-container')
          .remove()

        chartContext.epsilon || setEpsilon(chartContext)
        let [xPosition, yPosition] = d3.mouse(this),
          dataPoint = getNearestDataPoint(xPosition, chartContext),
          dataPointXPosition

        if (dataPoint) {
          dataPoint.filter(function(d, i) {
            dataPointXPosition = chartContext.xScale(d.data[0].label)

            moveVerticalMarker(dataPointXPosition, chartContext)
            let yPostionLayer =
              d.yScale(d.data[0].value) +
              margin.top +
              chartLayerheight * i +
              5 * i
            highlightDataPoints(d.data[0], chartContext, yPostionLayer, d.color)

            d3.selectAll('.value').text(function(d, i) {
              return dataPoint.formatted_value
            })
          })
        }
      })
    chartContext.categoryColorMap = chartContext.getLegends()
    drawHoverOverlay(chartContext)
    drawVerticalMarker(chartContext)
    createChart()
    function setEpsilon(context) {
      let dates = chartContext.data[0].data.map(({ label }) => label)
      context.epsilon =
        (context.xScale(dates[1]) - context.xScale(dates[0])) / 2
    }
    function drawHoverOverlay(context) {
      context.overlay = context.svg
        .select('.metadata-group')
        .append('rect')
        .attr('class', 'overlay')
        .attr('y1', 0)
        .attr('y2', containerHeight - margin.bottom)
        .attr('height', containerHeight - margin.bottom)
        .attr('width', context.getWidth)
        .attr('fill', context.overlayColor)
        .style('display', 'none')
    }
    function drawVerticalMarker(context) {
      context.verticalMarkerContainer = context.svg
        .select('.metadata-group')
        .append('g')
        .attr('class', 'hover-marker vertical-marker-container')
        .attr('transform', 'translate(' + 9999 + ',' + margin.bottom + ')')
      context.verticalMarkerLine = context.verticalMarkerContainer
        .selectAll('path')
        .data([
          {
            x1: 0,
            y1: 0,
            x2: 0,
            y2: 0,
          },
        ])
        .enter()
        .append('line')
        .classed('vertical-marker', true)
        .attr('x1', 0)
        .attr('y1', containerHeight - margin.bottom)
        .attr('x2', 0)
        .attr('y2', 0)
    }
    function getNearestDataPoint(mouseX, context) {
      let points = []
      series.filter(function(d, i) {
        points.push({
          data: d.data.filter(
            ({ label }) =>
              Math.abs(context.xScale(label) - mouseX) <= context.epsilon
          ),
          yScale: d.yScale,
          color: d.color,
        })
      })
      if (points.length) {
        return points
      }
    }
    function moveVerticalMarker(verticalMarkerXPosition, context) {
      context.verticalMarkerContainer.attr(
        'transform',
        `translate(${verticalMarkerXPosition},0)`
      )
    }
    function highlightDataPoints(values, context, yLayerScale, color) {
      let highlightCircleSize = 12,
        highlightCircleRadius = 5,
        highlightCircleStroke = 1.8
      let marker = context.verticalMarkerContainer
        .append('g')
        .classed('circle-container', true)
        .append('circle')
        .classed('data-point-highlighter', true)
        .attr('cx', highlightCircleSize)
        .attr('cy', 0)
        .attr('r', highlightCircleRadius)
        .style('stroke-width', highlightCircleStroke)
        .style('stroke', color)
        .style('cursor', 'pointer')
      marker.attr(
        'transform',
        `translate( ${-highlightCircleSize}, ${yLayerScale} )`
      )
      // }
    }
    function createChart(data) {
      let charts = []
      let maxDataPoint = 0
      for (let i = 0; i <= arrayLenght; i++) {
        let rawData = chartContext.data[i].data
        maxDataPoint = d3.max(rawData, function(d) {
          return d.value
        })
        chartContext.xScale = d3
          .scaleTime()
          .range([0, width])
          .domain(
            d3.extent(rawData, function(d) {
              return new Date(d.label)
            })
          )
        chartContext.yScale = d3
          .scaleLinear()
          .range([chartLayerheight, 0])
          .domain([0, maxDataPoint + maxDataPoint / 2])
        charts.push(
          new Chart({
            context: chartContext,
            data: rawData,
            id: i,
            xScale: chartContext.xScale,
            yScale: chartContext.yScale,
            name: chartContext.data[i].title,
            width: width,
            height: chartLayerheight,
            maxDataPoint: maxDataPoint + maxDataPoint / arrayLenght - 1,
            svg: chartContext.series,
            margin: margin,
            color: defaultColors[i],
            showBottomAxis: i === arrayLenght - 1,
          })
        )
        series.push({
          context: chartContext,
          data: rawData,
          id: i,
          xScale: chartContext.xScale,
          yScale: chartContext.yScale,
          name: chartContext.data[i].title,
          width: width,
          height: chartLayerheight,
          maxDataPoint: maxDataPoint + maxDataPoint / arrayLenght - 1,
          svg: chartContext.series,
          margin: margin,
          color: defaultColors[i],
          showBottomAxis: i === arrayLenght - 1,
        })
      }
    }

    function Chart(options) {
      chartContext.areaOpacity = 0.24

      /* YScale is linear based on the maxData Point we found earlier */
      let xS = chartContext.xScale
      let yS = options.yScale
      options.area = d3
        .area()
        .curve(d3.curveMonotoneX)
        .x(function(d) {
          return xS(new Date(d.label))
        })
        .y0(options.height)
        .y1(function(d) {
          return yS(d.value)
        })
      options.areaOutline = d3
        .line()
        .curve(d3.curveMonotoneX)
        .x(function(d) {
          return xS(new Date(d.label))
        })
        .y(function(d) {
          return yS(d.value)
        })

      let chartContainer = options.svg
        .append('g')
        .attr('class', options.name.toLowerCase())
        .attr(
          'transform',
          'translate(' +
            0 +
            ',' +
            (options.margin.top +
              options.height * options.id +
              5 * options.id) +
            ')'
        )
      /* We've created everything, let's actually add it to the page */

      options.yAxis = d3
        .axisLeft()
        .scale(options.yScale)
        .ticks(5)

      chartContainer
        .append('g')
        .attr('class', 'y axis y-axis-group')
        .attr('transform', 'translate(-0,0)')
        .call(options.yAxis)

      chartContainer
        .selectAll('.y-axis-group .tick text')
        .attr('class', 'yaxis-tick-label')
        .attr('dy', '-0.5em')
      chartContainer
        .selectAll('.y-axis-group .tick line')
        .attr('class', 'yaxis-tick-line')
        .attr('x1', '-20')
        .attr('x2', width)
      chartContainer
        .append('path')
        .data([options.data])
        .attr('class', 'chart')
        .attr('clip-path', 'url(#clip-' + options.id + ')')
        .attr('d', options.area)
        .style('opacity', chartContext.areaOpacity)
        .style('fill', function(d, i) {
          return options.color
        })
      chartContainer
        .append('path')
        .data([options.data])
        .attr('class', 'area-outline')
        .attr('d', options.areaOutline)
        .style('stroke', function(d, i) {
          return options.color
        })
      options.xAxisTop = d3.axisTop().scale(chartContext.xScale)
      options.xAxisBottom = d3.axisBottom().scale(chartContext.xScale)
      /* We only want a top axis if it's the first country */
      if (options.id === 0) {
        chartContainer
          .append('g')
          .attr('class', 'top x-axis-group axis brush-axis')
          .attr('transform', 'translate(0,0)')
          .call(options.xAxisTop)
          .selectAll('line')
          .attr('y2', 0)
        chartContainer
          .select('.x-axis-group.axis path.domain')
          .attr('d', 'M-20,0V0.5H' + width + '.5V0')
          .attr('class', 'x-axis-line-path')
      } else {
        options.svg
          .append('line')
          .attr(
            'transform',
            'translate(' +
              0 +
              ',' +
              (options.margin.top -
                5 +
                options.height * options.id +
                5 * options.id) +
              ')'
          )
          .attr('x2', chartContext.chartWidth)
          .attr('stroke', 'black')
      }
      /* Only want a bottom axis on the last country */
      if (options.showBottomAxis) {
        chartContainer
          .append('g')
          .attr('class', 'x-axis-group axis brush-axis')
          .attr('transform', 'translate(0,' + options.height + ')')
          .call(options.xAxisBottom)
          .selectAll('line')
          .attr('y2', 0)
        chartContainer
          .select('.x-axis-group.axis path.domain')
          .attr('d', 'M-20,0V0.5H' + width + '.5V0')
          .attr('class', 'x-axis-line-path')
      }

      options.context.label = options.svg
        .append('text')
        .attr('class', 'label')
        .attr(
          'transform',
          'translate(' +
            10 +
            ',' +
            (options.margin.top +
              25 +
              options.height * options.id +
              5 * options.id) +
            ')'
        )
        .text(options.name)
      options.context.value = chartContainer
        .append('text')
        .attr('class', 'value')
        .attr(
          'transform',
          'translate(' +
            (options.width - options.margin.left - options.margin.right) +
            ',' +
            25 +
            ')'
        )
    }
  },
}
