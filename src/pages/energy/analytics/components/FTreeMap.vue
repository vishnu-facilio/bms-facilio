<template>
  <div class="ftree-map-page">
    <div v-if="treeMapStore.length !== 0" class="height100 treemap">
      <div ref="treemapDiv" class="height100"></div>
    </div>
    <f-widget-legends
      v-if="
        !$mobile &&
          reportObject &&
          reportObject.options &&
          reportObject.options.heatMapOptions &&
          reportObject.options.heatMapOptions.showWidgetLegends
      "
      ref="newWidget"
      class="widget-legends"
    ></f-widget-legends>
  </div>
</template>

<script>
import * as d3 from 'd3'
import ReportDataUtil from 'src/pages/report/mixins/ReportDataUtil'
import FWidgetLegends from 'newcharts/components/FWidgetLegendsOptimize'
import basechart from 'newcharts/mixins/basechart'
import ckmeans from 'third_party/ckmeans.min.js'
import * as core from 'charts/core'
import tooltip from '@/graph/mixins/tooltip'
import moment from 'moment-timezone'
import Vue from 'vue'
import { Script } from 'vm'

export default {
  components: { FWidgetLegends },
  mixins: [ReportDataUtil],
  data() {
    return {
      constantMargins: {
        top: 10,
        right: 35,
        bottom: 70,
        left: 35,
      },
      clientWidth: 980,
      clientHeight: 500,
      cellWidth: 50,
      cellHeight: 30,
      treeMapStore: [],
      xAlias: null,
      yAliasList: [],
      defaultColors: ['#1d7f01', '#6cb302', '#e9f501', '#fda504', '#fb5905'],
      strokeWidth: 1,
      strokeColor: 'white',
      activeParentStrokeWidth: 3,
      activeParentStrokeColor: 'white',
      svg: null,
      xScale: null,
      yScale: null,
      offset: 0,
      svgwidth: 0,
      svgheigth: 0,
      yOffset: 0,
      xOffset: 0,
      tTime: 1000,
      colorScale: null,
      myColor: null,
      ancestorClicked: false,
      parentXScale: null,
      parentYScale: null,
      grandparent: null,
      legendSection: null,
      yMax: null,
      yMin: null,
    }
  },
  mounted() {
    this.render()
  },
  computed: {
    chosenColors() {
      if (this.reportObject.options.heatMapOptions) {
        let key = this.reportObject.options.heatMapOptions.chosenColors
        if (this.reportObject.options.heatMapOptions.reversePallete) {
          let reversePallete = this.reportObject.options.heatMapOptions.Colors[
            key
          ].slice()
          return reversePallete.reverse()
        }
        return this.reportObject.options.heatMapOptions.Colors[key]
      }
      return this.defaultColors
    },
    textColor() {
      switch (this.reportObject.options.heatMapOptions.chosenColors) {
        case 'Default':
        case 'Palette 7':
        case 'Palette 12':
        case 'Palette 14':
        case 'Palette 15':
          return '#000'
        default:
          return '#fff'
      }
    },
    textMode() {
      if (this.reportObject.options.heatMapOptions) {
        if (this.reportObject.options.heatMapOptions.treemapTextMode) {
          return this.reportObject.options.heatMapOptions.treemapTextMode
        }
        return 1
      }
      return 1
    },
    isModuleReport() {
      if (
        this.resultObject &&
        this.resultObject.report &&
        this.resultObject.report.type === 2
      ) {
        return true
      }
      return false
    },
  },
  props: ['resultObject', 'reportObject', 'config', 'width', 'height'],
  methods: {
    render() {
      if (this.reportObject) {
        this.reportObject.options.type = 'treemap'
        this.yAliasList = []
        this.loadTreeMapStore()
        this.$nextTick(() => {
          if (this.resultObject) {
            for (let dataPoint of this.resultObject.report.dataPoints) {
              this.yAliasList.push(dataPoint.aliases.actual)
            }
            this.xAlias = 'x'
          }
          this.drawTreeMap()
          if (
            this.reportObject.options &&
            this.reportObject.options.heatMapOptions &&
            this.reportObject.options.heatMapOptions.showWidgetLegends &&
            this.$refs['newWidget']
          ) {
            this.$refs['newWidget'].value(
              null,
              this.reportObject.data,
              this.reportObject.options,
              this.resultObject ? this.resultObject.reportData.aggr : null
            )
          }
        })
      }
    },
    loadTreeMapStore() {
      this.treeMapStore = this.reportObject.options.dataPoints
    },
    resize() {
      this.rerender()
    },
    rerender() {
      if (this.updateTimeout) {
        clearTimeout(this.updateTimeout)
      }
      this.updateTimeout = setTimeout(() => this.render(), 500)
    },
    drawTreeMap() {
      if (this.$mobile) {
        this.constantMargins.left = 10
        this.constantMargins.right = 10
        if (this.reportObject.options.heatMapOptions.showColorScale) {
          this.constantMargins.bottom = 50
        } else {
          this.constantMargins.bottom = 10
        }
        if (!this.reportObject.options.heatMapOptions.showGrandParent) {
          this.constantMargins.top = 10
        } else {
          this.constantMargins.top = 30
        }
      } else if (
        this.reportObject.options.heatMapOptions &&
        this.height !== null &&
        this.height > 0
      ) {
        if (this.reportObject.options.heatMapOptions.showWidgetLegends) {
          if (this.reportObject.options.heatMapOptions.showColorScale) {
            this.constantMargins.bottom = 30
          } else {
            this.constantMargins.bottom = 0
          }
        } else {
          if (this.reportObject.options.heatMapOptions.showColorScale) {
            this.constantMargins.bottom = 50
          } else {
            this.constantMargins.bottom = 20
          }
        }
      }
      this.svgwidth =
        (this.width !== null && this.width > 0
          ? this.width
          : this.clientWidth) -
        (this.constantMargins.left + this.constantMargins.right)
      this.svgheight =
        (this.height !== null && this.height > 0
          ? this.height
          : this.clientHeight) -
        (this.constantMargins.top + this.constantMargins.bottom)
      if (
        this.reportObject.options.heatMapOptions &&
        this.reportObject.options.heatMapOptions.showWidgetLegends &&
        !this.$mobile
      ) {
        this.svgheight = this.svgheight - 100
      }
      let dataPoints = this.resultObject.report.dataPoints
      let sdp = this.resultObject.report.dataPoints[0]
      let cdp =
        this.resultObject.report.dataPoints.length > 1
          ? this.resultObject.report.dataPoints[1]
          : this.resultObject.report.dataPoints[0]
      let mode = this.getMode(this.resultObject.mode)
      let unit = sdp.yAxis.unitStr !== null ? sdp.yAxis.unitStr : ' '

      let isContinous =
        this.resultObject.report.dataPoints[0].xAxis.dataType === 5 ||
        this.resultObject.report.dataPoints[0].xAxis.dataType === 6
          ? true
          : false
      let dataAlias = sdp.aliases.actual
      let colorAlias = cdp.aliases.actual
      this.yMin =
        this.reportObject.options.heatMapOptions &&
        this.reportObject.options.heatMapOptions.minValue !== null
          ? this.reportObject.options.heatMapOptions.minValue !== ''
            ? this.reportObject.options.heatMapOptions.minValue
            : null
          : this.yMin
      this.yMax =
        this.reportObject.options.heatMapOptions &&
        this.reportObject.options.heatMapOptions.maxValue !== null
          ? this.reportObject.options.heatMapOptions.maxValue !== ''
            ? this.reportObject.options.heatMapOptions.maxValue
            : null
          : this.yMax

      let data =
        this.resultObject.report.type === 2
          ? this.transformWorkorderDataforTreeMap(
              this.reportObject,
              this.resultObject,
              this.resultObject.reportData.data,
              'X',
              dataAlias,
              colorAlias,
              sdp,
              cdp,
              mode
            )
          : this.transformDataforTreeMap(
              this.reportObject,
              this.resultObject,
              this.resultObject.reportData.data,
              'X',
              dataAlias,
              colorAlias,
              sdp,
              cdp,
              mode
            )

      this.myColor = d3.scaleLinear().range(this.chosenColors)
      this.svg = null
      this.grandparent = null
      this.legendSection = null
      d3.select(this.$refs['treemapDiv'])
        .select('svg')
        .remove()
      this.svg = d3
        .select(this.$refs['treemapDiv'])
        .append('svg')
        .attr(
          'width',
          this.svgwidth + this.constantMargins.right + this.constantMargins.left
        )
        .attr(
          'height',
          this.svgheight +
            this.constantMargins.top +
            this.constantMargins.bottom
        )
        .attr('class', this.$mobile ? '' : 'widget-treemap')
        .style('font', '10px sans-serif')
        .append('g')
        .attr(
          'transform',
          'translate(' +
            this.constantMargins.left +
            ',' +
            this.constantMargins.top +
            ')'
        )

      let self = this
      this.svg
        .append('filter')
        .append('feDropShadow')
        .attr('flood-opacity', 0.3)
        .attr('dx', 0)
        .attr('stdDeviation', 3)

      if (
        this.reportObject.options.heatMapOptions &&
        this.reportObject.options.heatMapOptions.showGrandParent
      ) {
        this.grandparent = this.svg.append('g').attr('class', 'grandparent')

        this.grandparent
          .append('rect')
          .attr('x', this.constantMargins.left)
          .attr('y', -10)
          .attr(
            'width',
            this.svgwidth -
              (this.constantMargins.right + this.constantMargins.left)
          )
          .attr('height', this.constantMargins.top)

        this.grandparent
          .append('text')
          .attr('x', this.constantMargins.left + 6)
          .attr('y', -10)
          .attr('dy', '.75em')
      } else if (this.grandparent) {
        this.grandparent.remove()
      }

      this.ancestorClicked = false

      this.xScale = d3
        .scaleLinear()
        .domain([0, this.svgwidth])
        .range([
          this.constantMargins.left,
          this.svgwidth - this.constantMargins.right,
        ])
        .clamp(false)
      this.yScale = d3
        .scaleLinear()
        .domain([0, this.svgheight])
        .range([
          this.constantMargins.top,
          this.svgheight - this.constantMargins.bottom,
        ])
        .clamp(false)

      this.parentXScale = d3
        .scaleLinear()
        .domain([0, this.svgwidth / 2])
        .range([
          this.constantMargins.left,
          this.svgwidth / 2 - this.constantMargins.right,
        ])
        .clamp(false)

      this.parentYScale = d3
        .scaleLinear()
        .domain([0, this.svgheight / 2])
        .range([
          this.constantMargins.top,
          this.svgheight - this.constantMargins.bottom,
        ])
        .clamp(false)

      this.yOffset = this.yScale.invert(
        this.yScale.range()[0] +
          this.yScale.range()[1] -
          this.yScale(this.yScale.domain()[1] - this.offset)
      )
      this.xOffset = this.xScale.invert(
        this.xScale.range()[0] +
          this.xScale.range()[1] -
          this.xScale(this.xScale.domain()[1] - this.offset)
      )

      const root = this.treemap(data)
      const ext = d3.extent(root.descendants(), d => d.value)
      const med = d3.mean(root.descendants(), d => d.value)
      this.colorScale = d3.scaleLinear(
        [ext[0], med, ext[1]],
        ['#ee2656', '#f5c512', '#4c97d2']
      )

      this.tTime = 1000

      if (
        this.reportObject.options.heatMapOptions &&
        this.reportObject.options.heatMapOptions.showColorScale
      ) {
        if (
          this.reportObject.options.heatMapOptions &&
          this.reportObject.options.heatMapOptions.colorCriteria
        ) {
          let colorCriteria = this.reportObject.options.heatMapOptions
            .colorCriteria
          let svgDefs = this.svg.append('defs')
          let legendref = 'legendGradient' + Date.now()
          let mainGradient = svgDefs
            .append('linearGradient')
            .attr('id', legendref)
          let legendContainer = this.svg.append('g')
          let legend = legendContainer
            .selectAll('.heatLegend')
            .data(self.chosenColors)

          this.legendSection = legend
            .enter()
            .append('g')
            .attr('class', 'heatLegend')

          this.legendSection
            .append('text')
            .attr('class', 'legendHeading')
            .attr('x', function(d) {
              return self.$mobile ? 0 : self.svgwidth / 4
            })
            .attr('y', function(d) {
              return self.svgheight - (self.$mobile ? 20 : 12)
            })
            .text(function(d) {
              return cdp.name
            })

          for (let critIdx in colorCriteria) {
            this.legendSection
              .append('rect')
              .attr('x', function(d, i) {
                return self.$mobile
                  ? 0 + (self.svgwidth * critIdx) / colorCriteria.length
                  : self.svgwidth / 4 +
                      (self.svgwidth * critIdx) / 2 / colorCriteria.length
              })
              .attr('y', function(d) {
                return self.$mobile ? self.svgheight - 5 : self.svgheight
              })
              .attr(
                'width',
                self.$mobile
                  ? self.svgwidth / colorCriteria.length
                  : self.svgwidth / 2 / colorCriteria.length
              )
              .attr('height', 10)
              .style('fill', colorCriteria[critIdx].color)
            this.legendSection
              .append('text')
              .text(function(d) {
                return colorCriteria[critIdx].min
              })
              .attr('class', 'legendLabel')
              .attr('x', function(d) {
                return self.$mobile
                  ? 0 +
                      (self.svgwidth * critIdx) / colorCriteria.length -
                      (parseInt(critIdx) > 0
                        ? this.getComputedTextLength() / 2
                        : 0)
                  : self.svgwidth / 4 +
                      (self.svgwidth * critIdx) / 2 / colorCriteria.length -
                      (parseInt(critIdx) > 0
                        ? this.getComputedTextLength() / 2
                        : 0)
              })
              .attr('y', function(d) {
                return self.svgheight + 20
              })
            if (parseInt(critIdx) === colorCriteria.length - 1) {
              this.legendSection
                .append('text')
                .text(function(d) {
                  return colorCriteria[critIdx].max
                })
                .attr('class', 'legendLabel')
                .attr('x', function(d) {
                  return self.$mobile
                    ? self.svgwidth - this.getComputedTextLength()
                    : self.svgwidth / 4 +
                        self.svgwidth / 2 -
                        this.getComputedTextLength()
                })
                .attr('y', function(d) {
                  return self.svgheight + 20
                })
            }
          }
        } else {
          let svgDefs = this.svg.append('defs')

          let legendref = 'legendGradient' + Date.now()

          let mainGradient = svgDefs
            .append('linearGradient')
            .attr('id', legendref)

          mainGradient
            .append('stop')
            .style('stop-color', function(d) {
              return self.chosenColors[0]
            })
            .attr('offset', '0%')
          mainGradient
            .append('stop')
            .style('stop-color', function(d) {
              return self.chosenColors[1]
            })
            .attr('offset', '25%')
          mainGradient
            .append('stop')
            .style('stop-color', function(d) {
              return self.chosenColors[2]
            })
            .attr('offset', '50%')
          mainGradient
            .append('stop')
            .style('stop-color', function(d) {
              return self.chosenColors[3]
            })
            .attr('offset', '75%')
          mainGradient
            .append('stop')
            .style('stop-color', function(d) {
              return self.chosenColors[4]
            })
            .attr('offset', '100%')

          let legendContainer = this.svg.append('g')
          let legend = legendContainer
            .selectAll('.heatLegend')
            .data(self.chosenColors)

          this.legendSection = legend
            .enter()
            .append('g')
            .attr('class', 'heatLegend')

          this.legendSection
            .append('text')
            .attr('class', 'legendHeading')
            .attr('x', function(d) {
              return self.$mobile ? 0 : self.svgwidth / 4
            })
            .attr('y', function(d) {
              return self.svgheight - (self.$mobile ? 20 : 12)
            })
            .text(function(d) {
              return cdp.name
            })

          this.legendSection
            .append('rect')
            .attr('x', function(d, i) {
              return self.$mobile ? 0 : self.svgwidth / 4
            })
            .attr('y', function(d) {
              return self.$mobile ? self.svgheight - 5 : self.svgheight
            })
            .attr('width', self.$mobile ? self.svgwidth : self.svgwidth / 2)
            .attr('height', 10)
            .style('fill', 'url(#' + legendref + ')')
        }
      } else if (this.svg) {
        this.svg.selectAll('defs').remove()
      }
      this.update(root)
    },
    getTooltipColor(value, myColor, yMin, yMax, clusters) {
      let c = value !== null ? myColor(value) : '#FFFFFF'
      if (value !== null && value < yMin) {
        c = myColor(clusters[0])
      } else if (value !== null && value > yMax) {
        c = myColor(clusters[4])
      }
      return c
    },
    getTooltipFormat() {
      switch (this.xformat) {
        case 'hours':
          return 'dddd, MMM D, YYYY h a'
        case 'days':
          return 'dddd, MMM D, YYYY'
        case 'weeks':
          return 'dddd, MMM D, [W]ww gggg'
        default:
          return 'LLLL'
      }
    },
    getNestedCloseRelatives(node) {
      node.ancestors().forEach(d => (d.isAncestor = true))
      let relativesSet = new Set(node.ancestors().reverse())

      if (node.children) {
        node.children.forEach(d => {
          d.isAncestor = false
          relativesSet.add(d)
        })
      }

      let depthNest = d3
        .nest()
        .key(d => d.depth)
        .entries([...relativesSet])

      return depthNest
    },
    rectifyDimensions(d) {
      let x0Temp = d.isAncestor ? d.ancestorX0 : d.x0
      let y0Temp = d.isAncestor ? d.ancestorY0 : d.y0
      let x1Temp = d.isAncestor ? d.ancestorX1 : d.x1
      let y1Temp = d.isAncestor ? d.ancestorY1 : d.y1

      return {
        x0: x0Temp,
        y0: y0Temp,
        x1: x1Temp,
        y1: y1Temp,
      }
    },
    cascade(root, offset) {
      return root.eachAfter(d => {
        let ancWidth = this.svgwidth
        let ancHeight = this.svgheight

        d.ancestorX0 = d.depth * offset
        d.ancestorY0 = d.depth * offset
        d.ancestorX1 = d.ancestorX0 + ancWidth
        d.ancestorY1 = d.ancestorY0 + ancHeight

        d.childMinX = d.ancestorX0 + offset
        d.childMinY = d.ancestorY0 + offset
      })
    },
    treemap(data) {
      return this.cascade(
        d3
          .treemap()
          .size([this.svgwidth, this.svgheight])
          .paddingTop(20)
          .paddingInner(0)
          .round(true)
          .tile(d3.treemapResquarify)(
          d3
            .hierarchy(data)
            .sum(d => parseFloat(d.value))
            .sort((a, b) => b.height - a.height)
        ),
        0 //offset
      )
    },
    update(current_node) {
      let self = this
      if (this.grandparent) {
        this.grandparent
          .datum(current_node)
          .on('click', function(d) {
            if (d.parent) {
              self.update(d.parent)
            }
          })
          .select('text')
          .text(self.getGrandParentText(current_node))

        this.grandparent.select('text').text(function(d) {
          let grandParentWidth =
            self.svgwidth -
            (self.constantMargins.right + self.constantMargins.left)
          let textWidth = this.getComputedTextLength()
          let grandParenttext = self.getGrandParentText(d)
          let maxchar =
            textWidth > 0
              ? (grandParentWidth - 12) /
                (textWidth / (grandParenttext + '').length)
              : 0
          if (grandParentWidth - 12 > textWidth) {
            return grandParenttext + ''
          }
          // else if (textWidth > 0) {
          //   return (grandParenttext + '').substring(0, maxchar - 4) + '..'
          // }
          return ''
        })
      }
      self.parentXScale
        .domain([
          d3.min(current_node.children, d => d.x0),
          d3.max(current_node.children, d => d.x1),
        ])
        .range([
          current_node.ancestorX0 + this.xOffset,
          current_node.ancestorX0 + this.svgwidth + this.xOffset,
        ])

      self.parentYScale
        .domain([
          d3.min(current_node.children, d => d.y0),
          d3.max(current_node.children, d => d.y1),
        ])
        .range([
          current_node.ancestorY0 + this.yOffset,
          current_node.ancestorY0 + this.svgheight + this.yOffset,
        ])

      this.xScale.domain([0, self.parentXScale.range()[1]])
      this.yScale.domain([0, self.parentYScale.range()[1]])

      let valArr = current_node.children.map(d =>
        d.data.colorValue ? parseFloat(d.data.colorValue) : null
      )
      let clust = this.getClusters(valArr)
      this.myColor.domain(clust)

      let layers = this.svg
        .selectAll('.layer')
        .data(this.getNestedCloseRelatives(current_node))
        .join('g')
        .classed('layer', true)

      layers.exit().remove()

      let children = layers.selectAll('.child').data(
        d => d.values,
        d =>
          d
            .ancestors()
            .reverse()
            .map(x => x.data.name)
            .join('/')
      )

      children
        .exit()
        .transition()
        .duration(this.tTime)
        .remove()

      children
        .exit()
        .selectAll('*')
        .remove()

      children
        .transition()
        .duration(this.tTime)
        .attr('transform', d => {
          let coords = self.rectifyDimensions(d)
          if (d.isAncestor) {
            return `translate(${self.xScale(coords.x0)},${self.yScale(
              coords.y0
            )})`
          } else {
            return `translate(${self.xScale(
              self.parentXScale(coords.x0)
            )},${self.yScale(self.parentYScale(coords.y0))})`
          }
        })

      children
        .on('click', function(d) {
          let newParent = d3.select(this)
          if (d.children) {
            self.handleNewParentClick(newParent)
            self.update(d)
          }
        })
        .on('mouseover', function(d) {
          let tooltipData = [
            {
              label: d.data.mode,
              value: d.data.name,
            },
            {
              label: d.data.sizePoint,
              value:
                d.value !== null
                  ? d3.format(',')(d['value']) +
                    ' ' +
                    (d.data.unit ? d.data.unit : '')
                  : 'No data',
            },
          ]
          if (d.data.colorPoint) {
            tooltipData.push({
              label: d.data.colorPoint,
              value:
                d.data.colorValue !== null
                  ? d.data.colorValue +
                    ' ' +
                    (d.data.colorUnit ? d.data.colorUnit : '')
                  : 'No data',
            })
          }
          let tooltipConfig = {
            position: {
              left: d3.event.pageX,
              top: d3.event.pageY,
            },
            data: tooltipData,
            color: self.getColor(
              d.data.colorValue ? parseFloat(d.data.colorValue) : null,
              valArr,
              clust,
              self.myColor
            ),
          }
          tooltip.showTooltip(tooltipConfig)
          let tempParent = d3.select(this)
          if (!d.children) return
          self.handleNewParentMouseOver(tempParent)
        })
        .on('mouseout', function(d) {
          tooltip.hideTooltip()
          let notNewParent = d3.select(this)
          self.handleNewParentMouseOut(notNewParent)
        })

      children
        .selectAll('rect')
        .transition()
        .delay(function(d) {
          // if (self.ancestorClicked) return 0

          // return d === current_node || d.depth < current_node.depth
          //   ? 0
          //   : self.tTime
          return 0
        })
        .on('end', () =>
          self.svg
            .selectAll('.layer')
            .selectAll('*')
            .attr('pointer-events', 'auto')
        )
        .attr('width', d => {
          let coords = self.rectifyDimensions(d)
          if (d.isAncestor) {
            return self.xScale(coords.x1) - self.xScale(coords.x0)
          } else {
            return (
              self.xScale(self.parentXScale(coords.x1)) -
              self.xScale(self.parentXScale(coords.x0))
            )
          }
        })
        .attr('height', d => {
          let coords = self.rectifyDimensions(d)
          if (d.isAncestor) {
            return self.yScale(coords.y1) - self.yScale(coords.y0)
          } else {
            return (
              self.yScale(self.parentYScale(coords.y1)) -
              self.yScale(self.parentYScale(coords.y0))
            )
          }
        })
        .attr('fill', d =>
          self.getColor(
            d.data.colorValue ? parseFloat(d.data.colorValue) : null,
            valArr,
            clust,
            this.myColor
          )
        )

      let childrenToAddText = children.filter(function(d) {
        return d3
          .select(this)
          .select('text')
          .empty()
      })

      childrenToAddText
        .append('text')
        .attr('clip-path', d => d.clipUid)
        .selectAll('tspan')
        .data(function(d) {
          let coords = self.rectifyDimensions(d)
          let textToDisplay = []
          switch (self.textMode) {
            case 1:
              textToDisplay = [
                d3.format(',')(parseFloat(d.value)) + '',
                //  + ' ' + (d.data.unit ? d.data.unit : ''),
              ]
                // .split(/(?=[A-Z][^A-Z])/g)
                .concat(d.data.name + '')
              // .concat(d.data.unit)
              break
            case 2:
              textToDisplay = [
                (d.data.colorValue
                  ? d3.format(',')(parseFloat(d.data.colorValue))
                  : ' ') + '',
              ].concat(d.data.name + '')
              break
            case 3:
              textToDisplay = [d.data.name + '']
          }
          let height = d.y1 - d.y0
          let width = d.x1 - d.x0
          d.width = width
          d.height = height
          if (d.isAncestor) {
            height = self.yScale(coords.y1) - self.xScale(coords.y0)
            width = self.xScale(coords.x1) - self.xScale(coords.x0)
          } else {
            height =
              self.yScale(self.parentYScale(coords.y1)) -
              self.yScale(self.parentYScale(coords.y0))
            width =
              self.xScale(self.parentXScale(coords.x1)) -
              self.xScale(self.parentXScale(coords.x0))
          }

          if (textToDisplay.length * 12.5 + 15 < height) {
            return width > 0 ? textToDisplay : ['']
          } else {
            return ['']
          }
        })
        .join('tspan')
        .attr('fill-opacity', (d, i, nodes) =>
          i === nodes.length - 1 ? 0.7 : null
        )
        .attr('fill', self.textColor)
        .attr('opacity', 0)
        .transition()
        .delay(this.tTime)
        .attr('opacity', 1)
        .text(d => d)

      childrenToAddText
        .filter(d => d.children)
        .selectAll('tspan')
        .attr('y', 10)
        .attr('dx', 10)

      childrenToAddText
        .filter(d => !d.children)
        .selectAll('tspan')
        .attr('y', function(d, i, node) {
          return `${1.1 + 2.5 * i}em`
        })
        .attr('x', 3)
        .text(function(d) {
          return d
        })

      let childrenEnter = children
        .enter()
        .append('g')
        .classed('child', true)
        .on('click', function(d) {
          let newParent = d3.select(this)
          if (d.children) {
            self.handleNewParentClick(newParent)
            self.update(d)
          }
        })
        .on('mouseover', function(d) {
          let tooltipData = [
            {
              label: d.data.mode,
              value: d.data.name,
            },
            {
              label: d.data.sizePoint,
              value:
                d.value !== null
                  ? d3.format(',')(d['value']) +
                    ' ' +
                    (d.data.unit ? d.data.unit : '')
                  : 'No data',
            },
          ]
          if (d.data.colorPoint) {
            tooltipData.push({
              label: d.data.colorPoint,
              value:
                d.data.colorValue !== null
                  ? d.data.colorValue +
                    ' ' +
                    (d.data.colorUnit ? d.data.colorUnit : '')
                  : 'No data',
            })
          }
          let tooltipConfig = {
            position: {
              left: d3.event.pageX,
              top: d3.event.pageY,
            },
            data: tooltipData,
            color: self.getColor(
              d.data.colorValue ? parseFloat(d.data.colorValue) : null,
              valArr,
              clust,
              self.myColor
            ),
          }
          tooltip.showTooltip(tooltipConfig)
          let tempParent = d3.select(this)
          if (!d.children) {
            return
          }
          self.handleNewParentMouseOver(tempParent)
        })
        .on('mouseout', function(d) {
          tooltip.hideTooltip()
          let notNewParent = d3.select(this)
          notNewParent.transition()
          self.handleNewParentMouseOut(notNewParent)
        })

      childrenEnter.attr('transform', d => {
        let coords = self.rectifyDimensions(d)
        if (d.isAncestor) {
          return `translate(${self.xScale(coords.x0)},${self.yScale(
            coords.y0
          )})`
        } else {
          return `translate(${self.xScale(
            self.parentXScale(coords.x0)
          )},${self.yScale(self.parentYScale(coords.y0))})`
        }
      })

      // childrenEnter.append('title').text(
      //   d =>
      //     `${d
      //       .ancestors()
      //       .reverse()
      //       .map(d => d.data.name)
      //       .join('/')}\n${parseFloat(d.value)}\n${d.data.unit}`
      // )

      childrenEnter
        .append('rect')
        .attr('stroke-width', 1)
        .attr('stroke', 'white')
        .attr('opacity', 0)
        .attr('fill', d =>
          self.getColor(
            d.data.colorValue ? parseFloat(d.data.colorValue) : null,
            valArr,
            clust,
            this.myColor
          )
        )
        .attr('width', d => {
          let coords = self.rectifyDimensions(d)
          if (d.isAncestor) {
            return self.xScale(coords.x1) - self.xScale(coords.x0)
          } else {
            return (
              self.xScale(self.parentXScale(coords.x1)) -
              self.xScale(self.parentXScale(coords.x0))
            )
          }
        })
        .attr('height', d => {
          let coords = self.rectifyDimensions(d)
          if (d.isAncestor) {
            return self.yScale(coords.y1) - self.xScale(coords.y0)
          } else {
            return (
              self.yScale(self.parentYScale(coords.y1)) -
              self.yScale(self.parentYScale(coords.y0))
            )
          }
        })
        .transition()
        .duration(this.tTime)
        .on('interrupt', function(d) {
          let selection = d3.select(this)
          selection.attr('fill', d =>
            // self.getColor(parseFloat(d.value), valArr, clust, this.myColor)
            self.getColor(
              d.data.colorValue ? parseFloat(d.data.colorValue) : null,
              valArr,
              clust,
              this.myColor
            )
          )
        })
        .attr('opacity', 1)

      childrenEnter
        .append('text')
        .attr('clip-path', d => d.clipUid)
        .selectAll('tspan')
        .data(function(d) {
          let coords = self.rectifyDimensions(d)
          let textToDisplay = []
          switch (self.textMode) {
            case 1:
              textToDisplay = [d3.format(',')(parseFloat(d.value)) + ''].concat(
                d.data.name + ''
              )
              break
            case 2:
              textToDisplay = [
                (d.data.colorValue
                  ? d3.format(',')(parseFloat(d.data.colorValue))
                  : ' ') + '',
              ].concat(d.data.name + '')
              break
            case 3:
              textToDisplay = [d.data.name + '']
          }
          let height = d.y1 - d.y0
          let width = d.x1 - d.x0
          if (d.isAncestor) {
            height = self.yScale(coords.y1) - self.xScale(coords.y0)
            width = self.xScale(coords.x1) - self.xScale(coords.x0)
          } else {
            height =
              self.yScale(self.parentYScale(coords.y1)) -
              self.yScale(self.parentYScale(coords.y0))
            width =
              self.xScale(self.parentXScale(coords.x1)) -
              self.xScale(self.parentXScale(coords.x0))
          }
          d.width = width
          d.height = height

          if (textToDisplay.length * 12.5 + 15 < height) {
            let text = textToDisplay.map(text => {
              return [text].concat(width)
            })
            return width > 0 ? text : ['']
          } else {
            return ['']
          }
        })
        .join('tspan')
        .attr('fill-opacity', (d, i, nodes) =>
          i === nodes.length - 1 ? 0.7 : null
        )
        .attr('fill', self.textColor)
        .attr('y', 10)
        .attr('dx', 10)
        .text(function(d) {
          return d[0]
        })
        .attr('opacity', 0)
        .transition()
        .duration(this.tTime)
        .attr('opacity', 1)
        .text(function(d, i, nodes) {
          if (i === 1) {
            if (nodes[0].textContent === '') {
              return ''
            }
            let firstStringWidth = nodes[0].getComputedTextLength()
            if (d[1] - 15 <= firstStringWidth) {
              return ''
            }
          } else if (i === 0 && nodes[1]) {
            if (nodes[1].textContent === '') {
              return ''
            }
            let firstStringWidth = nodes[1].getComputedTextLength()
            if (d[1] - 15 <= firstStringWidth) {
              return ''
            }
          }
          let textWidth = this.getComputedTextLength()
          // let maxchar =
          //   textWidth > 0 ? (d[1] - 12) / (textWidth / (d[0] + '').length) : 0
          if (d[1] - 15 > textWidth) {
            return d[0] + ''
          }
          // else if (textWidth > 0) {
          //   return (d[0] + '').substring(0, maxchar - 4) + '..'
          // }
          return ''
        })

      childrenEnter
        .filter(d => d.children)
        .selectAll('text')
        .attr('opacity', function(d) {
          if (d.width - 15 < this.getComputedTextLength()) {
            return 0
          }
          return 1
        })

      childrenEnter
        .filter(d => d.children)
        .selectAll('tspan')
        .attr('y', 10)
        .attr('dx', 10)

      childrenEnter
        .filter(d => !d.children)
        .selectAll('tspan')
        .attr('y', function(d, i, node) {
          return `${1.1 + 2.5 * i}em`
        })
        .attr('x', 3)
        .attr('opacity', function(d, i, node) {
          return 1
        })
        .data(function(d) {
          return d
        })
        .text(function(d, i, node) {
          return d
        })

      let tempLayer = this.svg.append('g').attr('id', 'tempLayer')
      tempLayer = this.svg.append('g').attr('pointer-events', 'none')

      if (
        this.legendSection &&
        !(
          this.reportObject.options.heatMapOptions &&
          this.reportObject.options.heatMapOptions.colorCriteria
        )
      ) {
        this.legendSection.selectAll('text.legendLabel').remove()
        let min = this.yMin
          ? this.yMin
          : d3.min(valArr)
          ? this.getRoundedNumber(d3.min(valArr), 'floor')
          : ''
        let max = this.yMax
          ? this.yMax
          : d3.max(valArr)
          ? this.getRoundedNumber(d3.max(valArr), 'ceil')
          : ''
        let unit =
          this.resultObject &&
          this.resultObject.report &&
          this.resultObject.report.dataPoints
            ? this.resultObject.report.dataPoints[1].yAxis.unitStr
            : ''
        this.legendSection
          .append('text')
          .attr('class', 'legendLabel')
          .attr('x', function(d) {
            return self.$mobile ? 0 : self.svgwidth / 4
          })
          .attr('y', function(d) {
            return self.svgheight + 20
          })
          .text(function(d) {
            let m = min < 0 ? min : 0
            return m + (unit ? ' ' + unit : '')
          })
        this.legendSection
          .append('text')
          .text(function(d) {
            return max + (unit ? ' ' + unit : '')
          })
          .attr('class', 'legendLabel')
          .attr('x', function(d) {
            return self.$mobile
              ? self.svgwidth - this.getComputedTextLength()
              : self.svgwidth / 4 +
                  self.svgwidth / 2 -
                  this.getComputedTextLength()
          })
          .attr('y', function(d) {
            return self.svgheight + 20
          })
      }
    },
    handleNewParentClick(selection) {
      this.ancestorClicked = selection.datum().isAncestor
      selection
        .selectAll('rect')
        .attr('stroke', this.strokeColor)
        .attr('stroke-width', this.strokeWidth)

      let tempLayer = this.svg.select('#tempLayer')
      tempLayer.attr('id', null)
      tempLayer.classed('layer', true)
    },
    handleNewParentMouseOver(selection) {
      let self = this
      selection
        .select('rect')
        .attr('stroke', this.activeParentStrokeColor)
        .attr('stroke-width', this.activeParentStrokeWidth)
      if (selection.datum().isAncestor) return

      let datum = selection.datum()

      let tempLayer = this.svg
        .select('#tempLayer')
        .attr('width', datum.x1 - datum.x0)
        .attr('height', datum.y1 - datum.y0)

      let innerNodes = tempLayer
        .selectAll('.child')
        .data(datum.children, function(d) {
          return d
            .ancestors()
            .reverse()
            .map(x => x.data.name)
            .join('/')
        })
        .join('g')
        .classed('child', true)
        .attr('transform', d => {
          return `translate(${self.xScale(
            self.parentXScale(d.x0)
          )},${self.yScale(self.parentYScale(d.y0))})`
        })
        .attr('pointer-events', 'none')

      // innerNodes.append('title').text(
      //   d =>
      //     `${d
      //       .ancestors()
      //       .reverse()
      //       .map(d => d.data.name)
      //       .join('/')}\n${parseFloat(d.value)}\n${d.data.unit}`
      // )

      let data = datum.children.map(d => parseFloat(d.data.colorValue))
      let clusts = this.getClusters(data)
      this.myColor.domain(clusts)

      innerNodes
        .append('rect')
        .attr('fill', d =>
          // self.getColor(parseFloat(d.value), data, clusts, this.myColor)
          self.getColor(
            d.data.colorValue ? parseFloat(d.data.colorValue) : null,
            data,
            clusts,
            this.myColor
          )
        )
        .attr(
          'width',
          d =>
            self.xScale(self.parentXScale(d.x1)) -
            self.xScale(self.parentXScale(d.x0))
        )
        .attr(
          'height',
          d =>
            self.yScale(self.parentYScale(d.y1)) -
            self.yScale(self.parentYScale(d.y0))
        )
        .transition()
        .duration(this.tTime)
        .on('interrupt', function(d) {
          let selection = d3.select(this)
          selection.attr('fill-opacity', 1)
        })
        .attr('fill-opacity', 1)
        .attr('stroke-width', 1)
        .attr('stroke', 'white')
    },
    handleNewParentMouseOut(selection) {
      selection
        .select('rect')
        .attr('stroke', this.strokeColor)
        .attr('stroke-width', this.strokeWidth)

      if (!selection.datum().isAncestor) {
        let tempChildren = this.svg.select('#tempLayer').selectAll('*')
        tempChildren.interrupt()
        tempChildren.remove()
      }
    },
    getClusters(clusterData) {
      let clusters = null
      clusterData.sort()
      let yMax = parseFloat(d3.max(clusterData))
      if (!yMax) {
        return []
      }
      let min =
        this.yMin != null
          ? this.yMin
          : d3.min(clusterData)
          ? this.getRoundedNumber(d3.min(clusterData), 'floor')
          : ''
      let max =
        this.yMax != null
          ? this.yMax
          : d3.max(clusterData)
          ? this.getRoundedNumber(d3.max(clusterData), 'ceil')
          : ''
      if (clusterData.length > 5) {
        clusters = ckmeans(clusterData, 5)
      } else if (min != null && max != null) {
        let diff = max - min
        let step = diff / 5
        let prevVal = min
        clusters = []
        for (let itr = 0; itr < 5; itr++) {
          let val = prevVal + step * itr
          clusters.push(val)
          prevVal = val
        }
      } else {
        clusters = clusterData
      }

      if (clusters && clusters[clusters.length - 1] < yMax) {
        if (clusters.length >= 5) {
          clusters[clusters.length - 1] = yMax
        } else {
          clusters.push(yMax)
        }
      }
      if (clusters.length < 5) {
        while (clusters.length < 5) {
          clusters.push(clusters[clusters.length - 1] + 1)
        }
      }
      return clusters
    },
    getColor(value, data, clusters, colorScale) {
      if (
        this.reportObject.options.heatMapOptions &&
        this.reportObject.options.heatMapOptions.colorCriteria
      ) {
        let min, max, mincolor, maxcolor, resultcolor
        let colorCriteria = this.reportObject.options.heatMapOptions
          .colorCriteria
        for (let crit of colorCriteria) {
          if (typeof min === 'undefined' || min > crit.min) {
            min = crit.min
            mincolor = crit.color
          }
          if (typeof max === 'undefined' || max < crit.max) {
            max = crit.max
            maxcolor = crit.color
          }

          if (value >= crit.min && value < crit.max) resultcolor = crit.color
          else if (value < min) resultcolor = mincolor
          else if (value > max) resultcolor = maxcolor
        }
        return resultcolor
      }
      let min = this.yMin
        ? this.yMin
        : Number(d3.min(data)) < 0
        ? Number(d3.min(data))
        : 0
      let max = this.yMax ? this.yMax : parseFloat(d3.max(data))
      if (value < min) {
        return colorScale(clusters[0])
      } else if (value > max) {
        return colorScale(clusters[clusters.length - 1])
      } else if (!value) {
        return this.chosenColors[0]
      } else {
        return colorScale(value)
      }
    },
    getGrandParentText(d) {
      let value = parseFloat(d.value)
      let unit = d.data.unit ? d.data.unit : ''
      return d.parent
        ? this.getGrandParentText(d.parent) +
            ' / ' +
            d.data.name +
            ' (' +
            d3.format(',')(this.DecimalRoundOff(parseFloat(value))) +
            ' ' +
            unit +
            ')'
        : d.data.name +
            ' (' +
            d3.format(',')(this.DecimalRoundOff(parseFloat(value))) +
            ' ' +
            unit +
            ')'
    },
    getMode(mode) {
      switch (parseInt(mode)) {
        case 4:
          return 'Time'
        case 6:
          return 'Site'
        case 7:
          return 'Building'
        case 8:
          return 'Asset'
        default:
          return ''
      }
    },
  },
}
</script>
<style lang="scss">
.fc-widget .widget-treemap {
  margin-top: 30px;
}

.treemap .fc-new-chart-type-single {
  top: -42px;
  right: 70px !important;
}

.grandparent rect {
  fill: #fff;
  font-size: 1rem;
  font-weight: 500;
  text-overflow: ellipsis;
}

.grandparent {
  text {
    font-size: 1rem;
    font-weight: 600;
  }
}

.child {
  overflow: hidden;
  text-overflow: ellipsis;
}

.child rect {
  text-overflow: ellipsis;
}

.child text tspan {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
}
.ftree-map-page {
  svg {
    tspan:first-child {
      font-size: 1rem;
      font-weight: 300;
      // fill: #fff;
    }
    tspan {
      font-size: 0.6rem;
      font-weight: 600;
      // fill: #fff;
    }
  }
  text {
    font-size: 12px;
    text-overflow: ellipsis;
    text-align: left;
    white-space: nowrap;
    dominant-baseline: mathematical;
  }
}
.heatLegend .legendHeading {
  fill: #868686;
  font-size: 12px;
}
</style>
