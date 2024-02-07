import * as d3 from 'd3'
export default {
  drawTrapezoids(chartObj) {
    chartObj.series = chartObj.fchart.svg.select('.chart-group')
    let color = [
      '#255aee',
      '#3a6fff',
      '#4f84ff',
      'rgb(101,154,302)',
      'rgb(122,175,323)',
      'rgb(144,197,345)',
      'rgb(165,218,366)',
    ]
    chartObj.series.append('g')
    let max1 = 454,
      max2 = 70,
      may = 200

    let yx = d3
      .scaleLinear()
      .domain([0, may])
      .range([0, may * 1.15])
    let px = function(d, i) {
      return d + 10 * i
    }
    let nx = function(d, i) {
      return d - 10 * i
    }
    let layers = d3.range(8).map(function(val, ind) {
      return {
        values: [
          { x: px(max2, val), y: yx(may) },
          { x: nx(max1, val), y: yx(may) },
          { x: nx(px(max1, 1), val), y: may },
          { x: px(nx(max2, 1), val), y: may },
        ],
      }
    })

    let line = d3
      .line()
      .curve('linear-closed')
      .x(function(d, i) {
        return d.x
      })
      .y(function(d, i) {
        return d.y
      })
    let stack = d3
      .stack()
      .keys(d3.range(8))
      .offset(d3.stackOffsetWiggle)

    let g = chartObj.series
      .selectAll('.funnel-group')
      .data(stack(layers))
      .enter()
      .append('g')
      .attr('class', 'funnel-group')

    g.append('path')
      .attr('d', function(d) {
        return line(d.coordinates)
      })
      .style('fill', function(d) {
        return color(d.sales_process)
      })

    g.append('text')
      .attr({
        y: function(d, i) {
          if (d.coordinates.length === 4) {
            return (
              (d.coordinates[0].y - d.coordinates[1].y) / 2 +
              d.coordinates[1].y +
              5
            )
          } else {
            return (d.coordinates[0].y + d.coordinates[1].y) / 2 + 10
          }
        },
        x: function(d, i) {
          return chartObj.fchart.getWidth() / 2
        },
      })
      .style('text-anchor', 'middle')
      .text(function(d) {
        return d.sales_process
      })

    d3.select('body')
      .append('table')
      .attr({
        id: 'footer',
        width: chartObj.fchart.getWidth() + 'px',
      })

    d3.select('body #footer')
      .append('tr')
      .attr({
        class: 'PykCharts-credits',
        id: 'credit-datasource',
      })
      .append('td')
      .style('text-align', 'left')
      .html(
        "<span style='pointer-events:none;'>Credits: </span><a href='http://pykcharts.com' target='_blank'>" +
          'Pykcharts' +
          '</a>'
      )
  },
}
