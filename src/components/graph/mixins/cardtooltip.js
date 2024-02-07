import * as d3 from 'd3'
export default {
  methods: {
    _getTooltipElement(className) {
      let tooltipElm = className
        ? document.querySelector('.' + className)
        : document.querySelector('.card-tooltip ')
      if (!tooltipElm) {
        tooltipElm = document.createElement('div')
        tooltipElm.setAttribute('class', className || 'card-tooltip')
        tooltipElm.style.visibility = 'hidden'
        tooltipElm.style.opacity = '0'
        document.querySelector('body').appendChild(tooltipElm)
      }

      let tooltipDiv = className
        ? d3.select('.' + className)
        : d3.select('.card-tooltip ')
      return tooltipDiv
    },
    showTooltipForCard(tooltipConfig, className) {
      let tooltipElm = this._getTooltipElement(className)
      tooltipElm.style('left', tooltipConfig.position.left + 10 + 'px')
      tooltipElm.style('top', tooltipConfig.position.top - 25 + 'px')

      let tooltipContent = ''
      if (tooltipConfig.title) {
        tooltipContent +=
          '<div class="tooltip-title">' + tooltipConfig.title + '</div>'
      }

      for (let i = 0; i < tooltipConfig.data.length; i++) {
        let axisRow = tooltipConfig.data[i]

        tooltipContent += '<div class="row axis-row">'

        if (tooltipConfig.mode && tooltipConfig.mode === 1) {
          if (axisRow.color) {
            tooltipContent +=
              '<div class="circlesmall axis-color" style="background: ' +
              axisRow.color +
              '"></div>'
          }
          tooltipContent +=
            '<div class="axis-label">' + axisRow.label + ':</div>'
          tooltipContent += '<div class="axis-value">' + axisRow.value
          if (axisRow.unit) {
            tooltipContent +=
              '<span class="axis-unit">' + axisRow.unit + '</span>'
          }
          tooltipContent += '</div>'
        } else {
          tooltipContent +=
            '<div class="axis-label">' + axisRow.label + ':</div>'
          tooltipContent += '<div class="axis-value">' + axisRow.value
          if (axisRow.unit) {
            tooltipContent +=
              '<span class="axis-unit">' + axisRow.unit + '</span>'
          }
          tooltipContent += '</div>'
        }
        tooltipContent += '</div>'
      }
      if (tooltipConfig.drilldown) {
        tooltipContent +=
          '<div class="row axis-row"><div class="axis-tip">Click to Drill Down</div></div>'
      }
      if (tooltipConfig.clickToOpen) {
        tooltipContent +=
          '<div class="row axis-row"><div class="axis-tip">Click to open alarms</div></div>'
      }
      tooltipElm.html(tooltipContent)
      if (tooltipConfig.color) {
        tooltipElm.style('border-bottom', '5px solid ' + tooltipConfig.color)
      } else {
        tooltipElm.style('border-bottom', '5px solid transparent')
      }
      tooltipElm.style('visibility', 'visible')
      tooltipElm.style('opacity', '1')
    },
    showTooltipForCard1(tooltipConfig, className) {
      let tooltipElm = this._getTooltipElement(className)
      tooltipElm.style('left', tooltipConfig.position.left + 10 + 'px')
      tooltipElm.style('top', tooltipConfig.position.top - 25 + 'px')

      let tooltipContent = ''
      if (tooltipConfig.title) {
        tooltipContent +=
          '<div class="tooltip-title">' + tooltipConfig.title + '</div>'
      }

      for (let i = 0; i < tooltipConfig.data.length; i++) {
        let axisRow = tooltipConfig.data[i]

        tooltipContent += '<div class="row axis-row">'

        if (tooltipConfig.mode && tooltipConfig.mode === 1) {
          if (axisRow.color) {
            tooltipContent +=
              '<div class="circlesmall axis-color" style="background: ' +
              axisRow.color +
              '"></div>'
          }
          tooltipContent +=
            '<div class="axis-label">' + axisRow.label + ':</div>'
          tooltipContent += '<div class="axis-value">' + axisRow.value
          if (axisRow.unit) {
            tooltipContent +=
              '<span class="axis-unit">' + axisRow.unit + '</span>'
          }
          tooltipContent += '</div>'
        } else {
          tooltipContent +=
            '<div class="axis-label">' + axisRow.label + ':</div>'
          tooltipContent += '<div class="axis-value">' + axisRow.value
          if (axisRow.unit) {
            tooltipContent +=
              '<span class="axis-unit">' + axisRow.unit + '</span>'
          }
          tooltipContent += '</div>'
        }
        tooltipContent += '</div>'
      }
      if (tooltipConfig.drilldown) {
        tooltipContent +=
          '<div class="row axis-row"><div class="axis-tip">Click to Drill Down</div></div>'
      }
      if (tooltipConfig.clickToOpen) {
        tooltipContent +=
          '<div class="row axis-row"><div class="axis-tip">Click to open alarms</div></div>'
      }
      tooltipElm.html(tooltipContent)
      if (tooltipConfig.color) {
        tooltipElm.style('border-bottom', '5px solid ' + tooltipConfig.color)
      } else {
        tooltipElm.style('border-bottom', '5px solid transparent')
      }
      tooltipElm.style('visibility', 'visible')
      tooltipElm.style('opacity', '1')
    },
    hideTooltip(className) {
      let tooltipElm = this._getTooltipElement(className)
      tooltipElm.style('visibility', 'hidden')
      tooltipElm.style('opacity', '0')
    },
  },
}
