import * as d3 from 'd3'
import formatter from 'charts/helpers/formatter'
import Vue from 'vue'

export default {
  showTooltip(tooltipConfig, context) {
    let tooltipElm = this._getTooltipElement()

    let tooltipContent = ''
    if (tooltipConfig.header) {
      tooltipContent +=
        '<div class="tooltip-type-title">' + tooltipConfig.header + '</div>'
    }
    if (tooltipConfig.title) {
      tooltipContent +=
        '<div class="tooltip-title">' +
        formatter.formatValue(tooltipConfig.title, context.getOptions().xaxis) +
        '</div>'
    }
    if (tooltipConfig.title1) {
      tooltipContent +=
        '<div class="tooltip-title">' + tooltipConfig.title1 + '</div>'
    }

    if (tooltipConfig.mode === 1) {
      let data = tooltipConfig.data.value
      tooltipContent += '<table>'
      for (let i = 0; i < data.length; i++) {
        let axisRow = data[i]
        tooltipContent += '<tr>'
        tooltipContent += '<td>'
        tooltipContent +=
          '<div class="circlesmall axis-color" style="background: ' +
          context.categoryColorMap[axisRow.label] +
          '"></div>'
        tooltipContent +=
          '<div class="axis-label">' +
          formatter.formatValue(axisRow.label, context.getOptions().groupby) +
          '</div>'
        tooltipContent += '</td>'
        tooltipContent += '<td>'
        tooltipContent +=
          '<div class="axis-value">' +
          formatter.formatValue(axisRow.value, context.getOptions().y1axis) +
          '</div>'
        if (axisRow.unit) {
          tooltipContent +=
            '<span class="axis-unit">' + axisRow.unit + '</span>'
        }
        tooltipContent += '</div>'
        tooltipContent += '</td>'
        tooltipContent += '</tr>'
      }
      tooltipContent += '</table>'
    } else if (tooltipConfig.mode === 'boolean') {
      for (let i = 0; i < tooltipConfig.data.length; i++) {
        let axisRow = tooltipConfig.data[i]
        tooltipContent += '<div class="row axis-row">'
        tooltipContent += '<div class="axis-label">' + axisRow.label + ':</div>'
        if (axisRow.axis) {
          if (Array.isArray(axisRow.value)) {
            tooltipContent += '<div class="axis-value">' + axisRow.value[0]
            tooltipContent +=
              ' to ' + formatter.formatValue(axisRow.value[1], axisRow.axis)
          } else {
            tooltipContent += '<div class="axis-value">' + axisRow.value
          }
        } else {
          tooltipContent += '<div class="axis-value">' + axisRow.value
        }
        if (axisRow.unit) {
          tooltipContent +=
            '<span class="axis-unit">' + axisRow.unit + '</span>'
        }
        if (axisRow.description) {
          tooltipContent +=
            '<span class="axis-value"> (' + axisRow.description + ' )</span>'
        }
        tooltipContent += '</div>'
        tooltipContent += '</div>'
      }
    } else {
      for (let i = 0; i < tooltipConfig.data.length; i++) {
        let axisRow = tooltipConfig.data[i]
        tooltipContent += '<div class="row axis-row">'
        tooltipContent += '<div class="axis-label">' + axisRow.label + ':</div>'
        if (axisRow.axis) {
          if (Array.isArray(axisRow.value)) {
            tooltipContent +=
              '<div class="axis-value">' +
              formatter.formatValue(axisRow.value[0], axisRow.axis)
            tooltipContent +=
              ' to ' + formatter.formatValue(axisRow.value[1], axisRow.axis)
          } else {
            tooltipContent +=
              '<div class="axis-value">' +
              formatter.formatValue(axisRow.value, axisRow.axis)
          }
        } else {
          tooltipContent += '<div class="axis-value">' + axisRow.value
        }
        if (axisRow.unit) {
          tooltipContent +=
            '<span class="axis-unit">' + axisRow.unit + '</span>'
        }
        tooltipContent += '</div>'
        tooltipContent += '</div>'
      }
    }
    if (tooltipConfig.drilldown) {
      tooltipContent +=
        '<div class="row axis-row"><div class="axis-tip">Click to Drill Down</div></div>'
    }
    if (tooltipConfig.clickToOpen) {
      tooltipContent +=
        '<div class="row axis-row"><div class="axis-tip">Click to open alarm</div></div>'
    }
    tooltipElm.html(tooltipContent)
    if (tooltipConfig.color) {
      tooltipElm.style('border-bottom', '5px solid ' + tooltipConfig.color)
    } else {
      tooltipElm.style('border-bottom', '5px solid transparent')
    }
    tooltipElm.style('visibility', 'visible')
    tooltipElm.style('opacity', '1')

    let rightSpace = window.innerWidth - tooltipConfig.position.left
    let tooltipDivWidth = parseInt(tooltipElm.style('width')) + 25
    if (tooltipDivWidth > rightSpace) {
      tooltipElm.style(
        'left',
        tooltipConfig.position.left - tooltipDivWidth + 'px'
      )
    } else {
      tooltipElm.style('left', tooltipConfig.position.left + 25 + 'px')
    }
    tooltipElm.style('top', tooltipConfig.position.top - 25 + 'px')
  },
  showTooltip1(tooltipConfig) {
    let tooltipElm = this._getTooltipElement(tooltipConfig)

    let tooltipContent = ''
    if (tooltipConfig.header) {
      tooltipContent +=
        '<div class="tooltip-type-title">' + tooltipConfig.header + '</div>'
    }
    if (tooltipConfig.title) {
      tooltipContent +=
        '<div class="tooltip-title">' + tooltipConfig.title + '</div>'
    }

    if (tooltipConfig.mode && tooltipConfig.mode === 1) {
      tooltipContent += '<table>'
      for (let i = 0; i < tooltipConfig.data.length; i++) {
        let axisRow = tooltipConfig.data[i]
        tooltipContent += '<tr>'
        tooltipContent += '<td>'
        if (axisRow.type && axisRow.type === 'line') {
          tooltipContent +=
            '<div class="linesmall axis-color" style="border: 1px ' +
            (axisRow.lineStyle ? axisRow.lineStyle : 'solid') +
            ' ' +
            axisRow.color +
            '"></div>'
        } else {
          tooltipContent +=
            '<div class="circlesmall axis-color" style="background: ' +
            axisRow.color +
            '"></div>'
        }
        tooltipContent += '<div class="axis-label">' + axisRow.label + '</div>'
        tooltipContent += '</td>'
        tooltipContent += '<td>'
        tooltipContent += '<div class="axis-value">' + axisRow.value + '</div>'
        if (axisRow.unit) {
          tooltipContent +=
            '<span class="axis-unit">' + axisRow.unit + '</span>'
        }
        tooltipContent += '</td>'
        tooltipContent += '</div>'
        tooltipContent += '</tr>'
      }
      if (tooltipConfig.alarm && tooltipConfig.alarm[0]) {
        tooltipContent += '<tr>'
        tooltipContent += '<td>'
        tooltipContent +=
          '<div class="imgcls" style="border-top: 1px solid #f0f0f0; margin-top: 10px;">'

        tooltipContent +=
          '<div class="icon"><img style="width: 15px; margin-left: 3px;" src="' +
          require('statics/icons/alarm.svg') +
          '"/></div>'

        tooltipContent +=
          '<div class="axis-label" style="color: #333333; padding-left: 3px;">' +
          tooltipConfig.alarm[0].value +
          '</div>'
        tooltipContent += '</div>'
        tooltipContent += '</td>'
        tooltipContent += '</tr>'
      }
      tooltipContent += '</table>'
    } else {
      for (let i = 0; i < tooltipConfig.data.length; i++) {
        let axisRow = tooltipConfig.data[i]

        tooltipContent += '<div class="row axis-row">'

        tooltipContent += '<div class="axis-label">' + axisRow.label + ':</div>'
        tooltipContent += '<div class="axis-value">' + axisRow.value
        if (axisRow.unit) {
          tooltipContent +=
            '<span class="axis-unit">' + axisRow.unit + '</span>'
        }
        tooltipContent += '</div>'

        tooltipContent += '</div>'
      }
    }
    if (tooltipConfig.alarms && tooltipConfig.alarms.length) {
      let alarmTitle =
        tooltipConfig.alarms.length === 1
          ? '1 Alarm'
          : (tooltipConfig.count || tooltipConfig.alarms.length) + ' Alarms'
      tooltipContent += '<div class="tooltip-alarm-container">'
      tooltipContent +=
        '<div class="tooltip-alarm-title"><i class="fa fa-bell-o"></i> ' +
        alarmTitle +
        '</div>'

      tooltipContent += '<table>'
      let moreAlarms = 0
      for (let i = 0; i < tooltipConfig.alarms.length; i++) {
        let alrm = tooltipConfig.alarms[i]
        if (i < 3) {
          let severityTag = ''
          let isCleared = false
          if (alrm.severity) {
            severityTag =
              '<div class="tooltip-alarm-severity" style="background: ' +
              alrm.severity.color +
              ';">' +
              alrm.severity.severity +
              '</div>'
            isCleared = alrm.isCleared
          }

          tooltipContent += '<tr>'
          tooltipContent += '<tr>'
          if (isCleared) {
            tooltipContent += '<td style="opacity: 0.7;">'
          } else {
            tooltipContent += '<td>'
          }
          tooltipContent +=
            '<div class="tooltip-alarm-datapoint">' +
            alrm.name +
            severityTag +
            '</div>'
          tooltipContent +=
            '<div class="tooltip-alarm-message" style="color: #333333;">' +
              alrm.message !==
              null && alrm.message !== undefined
              ? alrm.message
              : '' + '</div>'
          tooltipContent += '</td>'
          tooltipContent += '</tr>'
        } else {
          moreAlarms = moreAlarms + 1
        }
      }
      if (moreAlarms) {
        tooltipContent += '<tr>'
        tooltipContent += '<td>'
        tooltipContent +=
          '<div class="tooltip-alarm-datapoint">' +
          ('+' + moreAlarms + ' more alarms') +
          '</div>'
        tooltipContent += '</td>'
        tooltipContent += '</tr>'
      }
      tooltipContent += '</table>'

      tooltipContent += '</div>'
      tooltipContent += '</div>'
    }
    if (tooltipConfig.drilldown) {
      tooltipContent +=
        '<div class="row axis-row"><div class="axis-tip">Click to Drill Down</div></div>'
    }
    if (tooltipConfig.clickToOpen) {
      tooltipContent +=
        '<div class="row axis-row"><div class="axis-tip">Click to open the alarm</div></div>'
    }
    tooltipElm.html(tooltipContent)
    if (tooltipConfig.color) {
      tooltipElm.style('border-bottom', '5px solid ' + tooltipConfig.color)
    } else {
      tooltipElm.style('border-bottom', '5px solid transparent')
    }
    tooltipElm.style('visibility', 'visible')
    tooltipElm.style('opacity', '1')
    // tooltipElm.style('position', 'relative')

    if (tooltipConfig.position) {
      let rightSpace = window.innerWidth - tooltipConfig.position.left
      let tooltipDivWidth = parseInt(tooltipElm.style('width')) + 25
      if (tooltipDivWidth > rightSpace) {
        tooltipElm.style(
          'left',
          tooltipConfig.position.left - tooltipDivWidth + 'px'
        )
      } else {
        tooltipElm.style('left', tooltipConfig.position.left + 25 + 'px')
      }
      tooltipElm.style('top', tooltipConfig.position.top - 25 + 'px')
    } else {
      return tooltipElm._groups[0][0].outerHTML
    }
  },
  showTooltip2(tooltipConfig) {
    let tooltipElm = this._getTooltipElement(tooltipConfig)

    let tooltipContent = ''
    if (tooltipConfig.header) {
      tooltipContent +=
        '<div class="tooltip-type-title">' + tooltipConfig.header + '</div>'
    }
    // if (tooltipConfig.titleObj) {
    //   tooltipContent += '<div class="tooltip-new-title">' + tooltipConfig.titleObj.start + '</div>'
    // }
    if (tooltipConfig.titleObj && tooltipConfig.subtitle) {
      tooltipContent +=
        '<div class="tooltip-alarm-header"><div><div></div><div class="tooltip-alarm-newtitle">' +
        tooltipConfig.titleObj.start +
        '</div><div class="tooltip-alarm-subtitle">' +
        tooltipConfig.subtitle.start +
        '</div></div><div class="alarm-spereator">-</div><div><div class="tooltip-alarm-newtitle">' +
        tooltipConfig.titleObj.end +
        '</div><div class="tooltip-alarm-subtitle">' +
        tooltipConfig.subtitle.end +
        '</div></div></div>'
    }

    if (tooltipConfig.mode && tooltipConfig.mode === 1) {
      tooltipContent += '<table>'
      for (let i = 0; i < tooltipConfig.data.length; i++) {
        let axisRow = tooltipConfig.data[i]
        tooltipContent += '<tr>'
        tooltipContent += '<td>'
        if (axisRow.type && axisRow.type === 'line') {
          tooltipContent +=
            '<div class="linesmall axis-color" style="border: 1px ' +
            (axisRow.lineStyle ? axisRow.lineStyle : 'solid') +
            ' ' +
            axisRow.color +
            '"></div>'
        } else {
          tooltipContent +=
            '<div class="circlesmall axis-color" style="background: ' +
            axisRow.color +
            '"></div>'
        }
        tooltipContent += '<div class="axis-label">' + axisRow.label + '</div>'
        tooltipContent += '</td>'
        tooltipContent += '<td>'
        tooltipContent += '<div class="axis-value">' + axisRow.value + '</div>'
        if (axisRow.unit) {
          tooltipContent +=
            '<span class="axis-unit">' + axisRow.unit + '</span>'
        }
        tooltipContent += '</td>'
        tooltipContent += '</div>'
        tooltipContent += '</tr>'
      }
      if (tooltipConfig.alarm && tooltipConfig.alarm[0]) {
        tooltipContent += '<tr>'
        tooltipContent += '<td>'
        tooltipContent +=
          '<div class="imgcls" style="border-top: 1px solid #f0f0f0; margin-top: 10px;">'

        tooltipContent +=
          '<div class="icon"><img style="width: 15px; margin-left: 3px;" src="' +
          require('statics/icons/alarm.svg') +
          '"/></div>'

        tooltipContent +=
          '<div class="axis-label" style="color: #333333; padding-left: 3px;">' +
          tooltipConfig.alarm[0].value +
          '</div>'
        tooltipContent += '</div>'
        tooltipContent += '</td>'
        tooltipContent += '</tr>'
      }
      tooltipContent += '</table>'
    } else {
      for (let i = 0; i < tooltipConfig.data.length; i++) {
        let axisRow = tooltipConfig.data[i]

        tooltipContent += '<div class="row axis-row">'

        tooltipContent += '<div class="axis-label">' + axisRow.label + ':</div>'
        tooltipContent += '<div class="axis-value">' + axisRow.value
        if (axisRow.unit) {
          tooltipContent +=
            '<span class="axis-unit">' + axisRow.unit + '</span>'
        }
        tooltipContent += '</div>'

        tooltipContent += '</div>'
      }
    }
    if (tooltipConfig.alarms && tooltipConfig.alarms.length) {
      let alarmTitle =
        tooltipConfig.alarms.length === 1
          ? '1 Alarm'
          : (tooltipConfig.count || tooltipConfig.alarms.length) + ' Alarms'
      tooltipContent += '<div class="tooltip-boolean-alarm-container">'
      tooltipContent +=
        '<div class="tooltip-alarm-title"><i class="fa fa-bell-o pR5"></i> ' +
        alarmTitle +
        '</div>'
      tooltipContent += '</div>'
      tooltipContent += '</div>'
    }
    if (tooltipConfig.drilldown) {
      tooltipContent +=
        '<div class="row axis-row"><div class="axis-tip">Click to Drill Down</div></div>'
    }
    if (tooltipConfig.clickToOpen) {
      tooltipContent +=
        '<div class="row axis-row"><div class="axis-tip">Click to open the alarm</div></div>'
    }
    tooltipElm.html(tooltipContent)
    if (tooltipConfig.color) {
      tooltipElm.style('border-bottom', '5px solid ' + tooltipConfig.color)
    } else {
      tooltipElm.style('border-bottom', '5px solid transparent')
    }
    tooltipElm.style('visibility', 'visible')
    tooltipElm.style('opacity', '1')
    // tooltipElm.style('position', 'relative')

    if (tooltipConfig.position) {
      let rightSpace = window.innerWidth - tooltipConfig.position.left
      let tooltipDivWidth = parseInt(tooltipElm.style('width')) + 25
      if (tooltipDivWidth > rightSpace) {
        tooltipElm.style(
          'left',
          tooltipConfig.position.left - tooltipDivWidth + 'px'
        )
      } else {
        tooltipElm.style('left', tooltipConfig.position.left + 25 + 'px')
      }
      tooltipElm.style('top', tooltipConfig.position.top - 25 + 'px')
    } else {
      return tooltipElm._groups[0][0].outerHTML
    }
  },
  showTooltipForNewChart(tooltipConfig) {
    let tooltipContent = ''
    if (tooltipConfig.header) {
      tooltipContent +=
        '<div class="tooltip-type-title">' + tooltipConfig.header + '</div>'
    }
    if (tooltipConfig.title) {
      tooltipContent +=
        '<div class="tooltip-title">' + tooltipConfig.title + '</div>'
    }

    if (tooltipConfig.mode && tooltipConfig.mode === 1) {
      tooltipContent += '<table>'
      for (let i = 0; i < tooltipConfig.data.length; i++) {
        let axisRow = tooltipConfig.data[i]
        if (axisRow.is_group) {
          tooltipContent += '<tr>'
          tooltipContent +=
            '<td colspan="2" style="text-align: left; padding-left: 6px;"><div class="axis-group">' +
            axisRow.label +
            '</div></td>'
          tooltipContent += '</tr>'
          for (let child of axisRow.children) {
            tooltipContent += '<tr>'
            tooltipContent += '<td>'
            if (child.type && child.color && child.type === 'line') {
              tooltipContent +=
                '<div class="linesmall axis-color" style="border: 1px ' +
                (child.lineStyle ? child.lineStyle : 'solid') +
                ' ' +
                child.color +
                '"></div>'
            } else if (child.color) {
              tooltipContent +=
                '<div class="circlesmall axis-color" style="background: ' +
                child.color +
                '"></div>'
            } else {
              tooltipContent +=
                '<div class="whitecirclesmall axis-color"' + '"></div>'
            }
            tooltipContent +=
              '<div class="axis-label">' + child.label + '</div>'
            tooltipContent += '</td>'
            tooltipContent += '<td>'
            tooltipContent +=
              '<div class="axis-value">' + child.value + '</div>'
            if (child.unit) {
              tooltipContent +=
                '<span class="axis-unit">' + child.unit + '</span>'
            }
            tooltipContent += '</td>'
            tooltipContent += '</div>'
            tooltipContent += '</tr>'
          }
          tooltipContent += '<tr>'
          tooltipContent +=
            '<td colspan="2"><div class="axis-break"></div></td>'
          tooltipContent += '</tr>'
        } else {
          tooltipContent += '<tr>'
          tooltipContent += '<td>'
          if (axisRow.type && axisRow.color && axisRow.type === 'line') {
            tooltipContent +=
              '<div class="linesmall axis-color" style="border: 1px ' +
              (axisRow.lineStyle ? axisRow.lineStyle : 'solid') +
              ' ' +
              axisRow.color +
              '"></div>'
          } else if (axisRow.color) {
            tooltipContent +=
              '<div class="circlesmall axis-color" style="background: ' +
              axisRow.color +
              '"></div>'
          } else {
            tooltipContent +=
              '<div class="whitecirclesmall axis-color"' + '"></div>'
          }
          tooltipContent +=
            '<div class="axis-label">' + axisRow.label + '</div>'
          tooltipContent += '</td>'
          tooltipContent += '<td>'
          if (axisRow.unit && ['$', '€', '₹'].includes(axisRow.unit)) {
            tooltipContent +=
              '<span class="axis-unit">' + axisRow.unit + '</span>'
            tooltipContent +=
              '<div class="axis-value">' + axisRow.value + '</div>'
          } else if (axisRow.unit) {
            tooltipContent +=
              '<div class="axis-value">' + axisRow.value + '</div>'
            tooltipContent +=
              '<span class="axis-unit">' + axisRow.unit + '</span>'
          } else {
            tooltipContent +=
              '<div class="axis-value">' + axisRow.value + '</div>'
          }
          tooltipContent += '</td>'
          if (axisRow.percent) {
            tooltipContent += '<td>'
            let diffPercent = axisRow.percent.toFixed(2)
            let diffPercentVal =
              (diffPercent < 0 ? -diffPercent : diffPercent) + '%'
            tooltipContent +=
              '<div class="axis-percent">' + diffPercentVal + '</div>'
            tooltipContent += '</td>'
          }
          if (axisRow.diff) {
            tooltipContent += '<td>'
            let arrowClass =
              axisRow.diff < 0 && tooltipConfig.compare_indicator
                ? 'el-icon-caret-bottom'
                : 'el-icon-caret-top'
            let textClass =
              axisRow.diff < 0 ? 'axis-diff-bottom' : 'axis-diff-top'
            if (tooltipConfig.compare_indicator == 1) {
              textClass =
                axisRow.diff >= 0 ? 'axis-diff-bottom' : 'axis-diff-top'
            }
            let diffPercent = axisRow.diff.toFixed(2)
            let diffPercentVal =
              (diffPercent < 0 ? -diffPercent : diffPercent) + '%'
            tooltipContent +=
              '<div class="axis-diff ' +
              textClass +
              '"><i class="' +
              arrowClass +
              '"></i>' +
              diffPercentVal +
              '</div>'
            tooltipContent += '</td>'
          } else {
            tooltipContent += '<td></td>'
          }
          tooltipContent += '</tr>'
        }
      }
      tooltipContent += '</table>'
    } else {
      for (let i = 0; i < tooltipConfig.data.length; i++) {
        let axisRow = tooltipConfig.data[i]

        tooltipContent += '<div class="row axis-row">'

        tooltipContent += '<div class="axis-label">' + axisRow.label + ':</div>'
        if (axisRow.unit && ['$', '€', '₹'].includes(axisRow.unit)) {
          tooltipContent +=
            '<span class="axis-unit">' + axisRow.unit + '</span>'
          tooltipContent +=
            '<div class="axis-value">' + axisRow.value + '</div>'
        } else if (axisRow.unit) {
          tooltipContent +=
            '<div class="axis-value">' + axisRow.value + '</div>'
          tooltipContent +=
            '<span class="axis-unit">' + axisRow.unit + '</span>'
        } else {
          tooltipContent +=
            '<div class="axis-value">' + axisRow.value + '</div>'
        }
        if (axisRow.diff) {
          let arrowClass =
            axisRow.diff >= 0 ? 'el-icon-caret-bottom' : 'el-icon-caret-top'
          let diffPercent = axisRow.diff.toFixed(2) + '%'
          tooltipContent +=
            '<span class="axis-diff"><i class="' +
            arrowClass +
            '">' +
            diffPercent +
            '</i></span>'
        }
        tooltipContent += '</div>'

        tooltipContent += '</div>'
      }
    }
    if (tooltipConfig.alarms && tooltipConfig.alarms.length) {
      let alarmTitle =
        tooltipConfig.alarms.length === 1
          ? '1 Alarm'
          : (tooltipConfig.count || tooltipConfig.alarms.length) + ' Alarms'
      tooltipContent += '<div class="tooltip-alarm-container">'
      tooltipContent +=
        '<div class="tooltip-alarm-title"><i class="fa fa-bell-o"></i> ' +
        alarmTitle +
        '</div>'

      tooltipContent += '<table>'
      let moreAlarms = 0
      for (let i = 0; i < tooltipConfig.alarms.length; i++) {
        let alrm = tooltipConfig.alarms[i]
        if (i < 3) {
          let severityTag = ''
          let isCleared = false
          if (alrm.severity) {
            severityTag =
              '<div class="tooltip-alarm-severity" style="background: ' +
              alrm.severity.color +
              ';">' +
              alrm.severity.severity +
              '</div>'
            isCleared = alrm.isCleared
          }

          tooltipContent += '<tr>'
          if (isCleared) {
            tooltipContent += '<td style="opacity: 0.7;">'
          } else {
            tooltipContent += '<td>'
          }
          tooltipContent +=
            '<div class="tooltip-alarm-datapoint">' +
            alrm.name +
            severityTag +
            '</div>'
          tooltipContent +=
            '<div class="tooltip-alarm-message" style="color: #333333;">' +
              alrm.message !==
              null && alrm.message !== undefined
              ? alrm.message
              : '' + '</div>'
          tooltipContent += '</td>'
          tooltipContent += '</tr>'
        } else {
          moreAlarms = moreAlarms + 1
        }
      }
      if (moreAlarms) {
        tooltipContent += '<tr>'
        tooltipContent += '<td>'
        tooltipContent +=
          '<div class="tooltip-alarm-datapoint">' +
          ('+' + moreAlarms + ' more alarms') +
          '</div>'
        tooltipContent += '</td>'
        tooltipContent += '</tr>'
      }
      tooltipContent += '</table>'

      tooltipContent += '</div>'
      tooltipContent += '</div>'
    }
    if (tooltipConfig.drilldown) {
      tooltipContent +=
        '<div class="row axis-row"><div class="axis-tip">Click to Drill Down</div></div>'
    }
    if (tooltipConfig.clickToOpen) {
      tooltipContent +=
        '<div class="row axis-row"><div class="axis-tip">Click to open the alarm</div></div>'
    }

    if (tooltipConfig.position) {
      let tooltipElm = this._getTooltipElement(tooltipConfig)
      tooltipElm.html(tooltipContent)

      if (tooltipConfig.color) {
        tooltipElm.style('border-bottom', '5px solid ' + tooltipConfig.color)
      } else {
        tooltipElm.style('border-bottom', '5px solid transparent')
      }
      tooltipElm.style('visibility', 'visible')
      tooltipElm.style('opacity', '1')

      let rightSpace = window.innerWidth - tooltipConfig.position.left
      let bottomSpace = window.innerHeight - tooltipConfig.position.top
      let tooltipDivWidth = parseInt(tooltipElm.style('width')) + 25
      let tooltipDivHeight = parseInt(tooltipElm.style('height')) - 25
      if (tooltipDivWidth > rightSpace) {
        tooltipElm.style(
          'left',
          tooltipConfig.position.left - tooltipDivWidth + 'px'
        )
      } else {
        tooltipElm.style('left', tooltipConfig.position.left + 25 + 'px')
      }
      if (tooltipDivHeight > bottomSpace) {
        tooltipElm.style(
          'top',
          tooltipConfig.position.top - (tooltipDivHeight - bottomSpace) + 'px'
        )
      } else {
        tooltipElm.style('top', tooltipConfig.position.top - 25 + 'px')
      }
    } else {
      let tooltipElm =
        '<div class="' +
        tooltipConfig.className +
        '" style="border-bottom: 5px solid ' +
        (tooltipConfig.color ? tooltipConfig.color : 'transparent') +
        ';">'
      tooltipElm += tooltipContent
      tooltipElm += '</div>'

      return tooltipElm
    }
  },
  _getTooltipElement(tooltipConfig) {
    let className =
      tooltipConfig && tooltipConfig.className
        ? tooltipConfig.className
        : 'chart-tooltip'
    let customClass =
      tooltipConfig && tooltipConfig.class ? tooltipConfig.class : ''
    let tooltipElm = document.querySelector('.' + className)
    if (!tooltipElm) {
      tooltipElm = document.createElement('div')
      tooltipElm.setAttribute('class', className + ' ' + customClass)
      tooltipElm.style.visibility = 'hidden'
      tooltipElm.style.opacity = '0'
      document.querySelector('body').appendChild(tooltipElm)
    }

    let tooltipDiv = d3.select('.' + className)
    return tooltipDiv
  },
  showTooltipForCard(tooltipConfig, className) {
    if (className) {
      tooltipConfig.class = className
    }
    let tooltipElm = this._getTooltipElement(tooltipConfig)
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
        tooltipContent += '<div class="axis-label">' + axisRow.label + ':</div>'
        tooltipContent += '<div class="axis-value">' + axisRow.value
        if (axisRow.unit) {
          tooltipContent +=
            '<span class="axis-unit">' + axisRow.unit + '</span>'
        }
        tooltipContent += '</div>'
      } else {
        tooltipContent += '<div class="axis-label">' + axisRow.label + ':</div>'
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
  hideTooltip() {
    let tooltipElm = this._getTooltipElement()
    tooltipElm.style('visibility', 'hidden')
    tooltipElm.style('opacity', '0')
  },
  methods: {
    _getTooltipElement() {
      let tooltipElm = document.querySelector('.chart-tooltip ')
      if (!tooltipElm) {
        tooltipElm = document.createElement('div')
        tooltipElm.setAttribute('class', 'chart-tooltip')
        tooltipElm.style.visibility = 'hidden'
        tooltipElm.style.opacity = '0'
        document.querySelector('body').appendChild(tooltipElm)
      }

      let tooltipDiv = d3.select('.chart-tooltip')
      return tooltipDiv
    },
    showTooltip(tooltipConfig) {
      let tooltipElm = this._getTooltipElement(tooltipConfig)
      tooltipElm.style('left', tooltipConfig.position.left + 10 + 'px')
      tooltipElm.style('top', tooltipConfig.position.top - 25 + 'px')

      let tooltipContent = ''
      if (tooltipConfig.header) {
        tooltipContent +=
          '<div class="tooltip-type-title">' + tooltipConfig.header + '</div>'
      }
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
    showTooltipForCard(tooltipConfig) {
      let tooltipElm = this._getTooltipElement(tooltipConfig)
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
    hideTooltip() {
      let tooltipElm = this._getTooltipElement()
      tooltipElm.style('visibility', 'hidden')
      tooltipElm.style('opacity', '0')
    },
  },
}
