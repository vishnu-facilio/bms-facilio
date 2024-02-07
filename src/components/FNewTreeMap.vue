<template>
  <div :id="'TreeMap_' + getRandomInt" class="F-New-Treemap"></div>
</template>
<script>
import * as d3 from 'd3'
import tooltip from '@/graph/mixins/tooltip'
import { v4 as uuid } from 'uuid'

export default {
  props: ['data', 'width', 'height', 'options'],
  mounted() {
    this.draw()
    this.$emit('rendred')
  },
  computed: {
    getRandomInt() {
      return uuid()
    },
    sizeBasedSort() {
      if (this.options?.sort?.size) {
        return true
      }
      return false
    },
    showHeader() {
      if (this.options?.hideHeader) {
        return false
      }
      return true
    },
    tileName() {
      if (this.options?.tileName) {
        return this.options.tileName
      }
      return `treemapResquarify`
    },
    showTooltip() {
      return this.options.showTooltip
    },
  },
  methods: {
    getContrastYIQ(hexcolor) {
      if (hexcolor.indexOf('#') < 0) {
        return '#324049'
      }
      hexcolor = hexcolor.replace('#', '')
      let r = parseInt(hexcolor.substr(0, 2), 16)
      let g = parseInt(hexcolor.substr(2, 2), 16)
      let b = parseInt(hexcolor.substr(4, 2), 16)
      let yiq = (r * 299 + g * 587 + b * 114) / 1000
      return yiq >= 128 ? '#324049' : '#fff'
    },
    parentMouseOver() {},
    onclickHandle(d) {
      if (d?.parent?.data?.key) {
        this.$emit('floor', d.parent.data.key)
      }
    },
    childMouseOver(d) {
      let tooltipData = [
        { label: d.data.mode, value: d.id },
        { label: d.data.sizePoint, value: d.value },
      ]
      let tooltipConfig = {
        position: {
          left: d3.event.pageX,
          top: d3.event.pageY,
        },
        data: tooltipData,
        color: d.data?.color ? d.data.color : 'gray',
      }
      if (this.showTooltip) {
        tooltip.showTooltip(tooltipConfig)
      }
    },
    specialHandle(data) {
      // empty data should come in last so we ingnored the size is alway in last
      let newChildren = []
      let emptyChildren = {}
      if (data?.children) {
        data.children.forEach(rt => {
          if (rt.key !== -1) {
            newChildren.push(rt)
          } else {
            emptyChildren = rt
          }
        })
        this.$set(this.data, 'children', [...newChildren, ...[emptyChildren]])
      }
      return data
    },
    draw() {
      let { data, height, width, tileName } = this
      data = this.specialHandle(data)
      let self = this
      let margin = {
          top: 0,
          right: 0,
          bottom: 0,
          left: 0,
        },
        formatNumber = d3.format(',d'),
        transitioning

      let x = d3
        .scaleLinear()
        .domain([0, width])
        .range([0, width])

      let y = d3
        .scaleLinear()
        .domain([0, height - margin.top - margin.bottom])
        .range([0, height - margin.top - margin.bottom])

      // let color = d3.scaleOrdinal().range(
      //   d3.schemeCategory10.map(function(c) {
      //     c = d3.rgb(c)
      //     c.opacity = 0.6
      //     return c
      //   })
      // )
      let treemap
      let svg, grandparent

      updateDrillDown()

      function updateDrillDown() {
        if (svg) {
          svg.selectAll('*').remove()
        } else {
          let svgWidth = width - margin.left - margin.right
          svg = d3
            .select(`#TreeMap_${self.getRandomInt}`)
            .append('svg')
            .attr('width', svgWidth)
            .attr('height', height - margin.bottom - margin.top)
            .style('margin-left', -margin.left + 'px')
            .style('margin.right', -margin.right + 'px')
            .append('g')
            .attr(
              'transform',
              'translate(' + margin.left + ',' + margin.top + ')'
            )
            .style('shape-rendering', 'crispEdges')

          if (self.showHeader) {
            grandparent = svg.append('g').attr('class', 'fc-grandparent')

            grandparent
              .append('rect')
              .attr('y', -margin.top)
              .attr('width', width)
              .attr('height', margin.top)

            grandparent
              .append('text')
              .attr('x', 6)
              .attr('y', 6 - margin.top)
              .attr('dy', '.75em')
          }

          treemap = d3.treemap()
          if (tileName === 'treemapResquarify') {
            treemap.tile(
              d3[tileName].ratio((height / width) * 0.5 * (1 + Math.sqrt(5)))
            )
          } else {
            treemap.tile(d3[tileName])
          }
          treemap
            .size([width, height])
            .round(false)
            .paddingInner(1)
        }

        let root = d3
          .hierarchy(data)
          .eachBefore(function(d) {
            // d.id = (d.parent ? d.parent.id + ' > ' : '') + d.data.name
            d.id = d.data.name
          })
          .sum(d => d.value)
        if (self.sizeBasedSort) {
          root.sort(function(a, b) {
            return b.height - a.height || b.value - a.value
          })
        }

        initialize(root)
        accumulate(root)
        layout(root)
        treemap(root)
        display(root)
      }

      function initialize(root) {
        root.x = root.y = 0
        root.x1 = width
        root.y1 = height
        root.depth = 0
      }

      // Aggregate the values for internal nodes. This is normally done by the
      // treemap layout, but not here because of our custom implementation.
      // We also take a snapshot of the original children (_children) to avoid
      // the children being overwritten when when layout is computed.
      function accumulate(d) {
        // eslint-disable-next-line no-cond-assign
        return (d._children = d.children)
          ? (d.value = d.children.reduce(function(p, v) {
              return p + accumulate(v)
            }, 0))
          : d.value
      }

      // Compute the treemap layout recursively such that each group of siblings
      // uses the same size (1×1) rather than the dimensions of the parent cell.
      // This optimizes the layout for the current zoom state. Note that a wrapper
      // object is created for the parent node for each group of siblings so that
      // the parent’s dimensions are not discarded as we recurse. Since each group
      // of sibling was laid out in 1×1, we must rescale to fit using absolute
      // coordinates. This lets us use a viewport to zoom.
      function layout(d) {
        if (d._children) {
          d._children.forEach(function(c) {
            c.x0 = d.x0 + c.x0 * d.x1
            c.y0 = d.y0 + c.y0 * d.y1
            c.x1 *= d.x1 - d.x0
            c.y1 *= d.y1 - d.y0
            c.parent = d
            layout(c)
          })
        }
      }

      function display(d) {
        if (self.showHeader) {
          grandparent
            .datum(d.parent)
            .on('click', transition)
            .on('mouseover', data1 => {
              self.parentMouseOver(data1)
            })
            .select('text')
            .text(name(d))
        }

        let g1 = svg
          .insert('g', '.grandparent')
          .datum(d)
          .attr('class', 'depth')

        let g = g1
          .selectAll('g')
          .data(d._children)
          .enter()
          .append('g')

        g.filter(function(d) {
          return d._children
        })
          .classed('children', true)
          .on('click', transition)
          .on('mouseover', child => {
            self.parentMouseOver(child)
          })

        let children = g
          .selectAll('.child')
          .data(function(d) {
            return d._children || [d]
          })
          .enter()
          .append('g')

        children
          .append('rect')
          .attr('class', 'child')
          .call(rect)
          .append('title')
          .text(function(d) {
            return d.data.name + ' (' + formatNumber(d.value) + ')'
          })

        children
          .append('text')
          .attr('class', 'ctext')
          .text(function(d) {
            return d.data.name
          })
          .call(text2)

        g.append('rect')
          .attr('class', 'parent')
          .call(rect)

        let t = g
          .append('text')
          .attr('class', 'ptext')
          .attr('dy', '.75em')
          .style('fill', function(d) {
            return d.data?.color ? self.getContrastYIQ(d.data.color) : '#d9d9d9'
          })
        t.append('tspan').text(function(d) {
          return d.data.name
        })

        t.append('tspan')
          .attr('dy', '1.2em')
          .text(function(d) {
            return formatNumber(d.value)
          })

        t.call(text)

        g.selectAll('rect')
          .style('fill', function(d) {
            return d.data?.color ? d.data.color : '#d9d9d9'
          })
          .on('mouseover', childs => {
            self.childMouseOver(childs)
          })
          .on('click', childs => {
            self.onclickHandle(childs)
          })
          .on('mouseout', function() {
            tooltip.hideTooltip()
          })

        function transition(d) {
          if (transitioning || !d) return
          transitioning = true
          let g2 = display(d),
            t1 = g1.transition().duration(750),
            t2 = g2.transition().duration(750)

          // Update the domain only after entering new elements.
          //x.domain([d.x0, d.x0 + d.x1]);
          //y.domain([d.y0, d.y0 + d.y1]);
          x.domain([d.x0, d.x0 + (d.x1 - d.x0)])
          y.domain([d.y0, d.y0 + (d.y1 - d.y0)])

          // Enable anti-aliasing during the transition.
          svg.style('shape-rendering', null)

          // Draw child nodes on top of parent nodes.
          svg.selectAll('.depth').sort(function(a, b) {
            return a.depth - b.depth
          })

          // Fade-in entering text.
          g2.selectAll('text').style('fill-opacity', 0)

          // Transition to the new view.
          t1.selectAll('.ptext')
            .call(text)
            .style('fill-opacity', 0)
            .style('fill', function(d) {
              return d.data?.color ? d.data.color : '#d9d9d9'
            })
          t2.selectAll('.ptext')
            .call(text)
            .style('fill-opacity', 1)
            .style('fill', function(d) {
              return d.data?.color ? d.data.color : '#d9d9d9'
            })
          t1.selectAll('.ctext')
            .call(text2)
            .style('fill-opacity', 0)
            .style('fill', function(d) {
              return d.data?.color ? d.data.color : '#d9d9d9'
            })
          t2.selectAll('.ctext')
            .call(text2)
            .style('fill-opacity', 1)
            .style('fill', function(d) {
              return d.data?.color ? d.data.color : '#d9d9d9'
            })
          t1.selectAll('rect').call(rect)
          t2.selectAll('rect').call(rect)

          // Remove the old node when the transition is finished.
          t1.remove().on('end', function() {
            svg.style('shape-rendering', 'crispEdges')
            transitioning = false
          })
        }
        return g
      }

      function text(text) {
        text.selectAll('tspan').attr('x', function(d) {
          return x(d.x0) + 6
        })
        text
          .attr('x', function(d) {
            return x(d.x0) + 6
          })
          .attr('y', function(d) {
            return y(d.y0) + 3
          })
          .style('opacity', function(d) {
            let w = x(d.x1) - x(d.x0)

            return this.getComputedTextLength() < w - 6 ? 1 : 0
          })
      }

      function text2(text) {
        text
          .attr('x', function(d) {
            return x(d.x1) - this.getComputedTextLength() - 6
          })
          .attr('y', function(d) {
            return y(d.y1) - 6
          })
          .style('opacity', function(d) {
            let w = x(d.x1) - x(d.x0)

            return this.getComputedTextLength() < w - 6 ? 1 : 0
          })
      }

      function rect(rect) {
        rect
          .attr('x', function(d) {
            return x(d.x0)
          })
          .attr('y', function(d) {
            return y(d.y0)
          })
          .attr('width', function(d) {
            let w = x(d.x1) - x(d.x0)
            return w
          })
          .attr('height', function(d) {
            let h = y(d.y1) - y(d.y0)
            return h
          })
      }

      function name(d) {
        return d.parent
          ? name(d.parent) +
              ' / ' +
              (d.data?.name ? d.data.name : '') +
              ' (' +
              formatNumber(d.value) +
              ')'
          : (d.data?.name ? d.data.name : '') +
              ' (' +
              formatNumber(d.value) +
              ')'
      }
    },
  },
}
</script>
<style lang="scss">
.fc-grandparent rect {
  fill: #fff;
  font-size: 0.8rem;
  font-weight: 500;
  text-overflow: ellipsis;
}

.fc-grandparent {
  text {
    font-size: 0.8rem;
    font-weight: 500;
  }
}

.ptext {
  font-size: 13px;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  // color: #324049;
  line-height: 10px;
}
</style>
