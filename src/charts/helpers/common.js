import * as d3 from 'd3'
const d3Format = require('d3-format')

export default {
  isValueArray(data) {
    if (data && data.length) {
      if (Array.isArray(data[0].value)) {
        return true
      }
    }
    return false
  },

  getGroup(data) {
    if (this.isValueArray(data)) {
      return data[0].value.map(function(d) {
        return d.label
      })
    } else {
      return data.map(function(d) {
        return d.label
      })
    }
  },
  getDescendingGroup(data, options) {
    if (this.isValueArray(data)) {
      if (options && options.widgetLegends) {
        let array = []
        for (let d in options.widgetLegends) {
          array.push(options.widgetLegends[d])
        }
        array.sort(function(a, b) {
          return a.convertedValue < b.convertedValue
            ? 1
            : b.convertedValue < a.convertedValue
            ? -1
            : 0
        })
        if (array && array.length && data && data.length) {
          if (data[0].value.length === array.length) {
            return array.map(function(d, i) {
              return d.stack
                ? data[0].value[i].label
                : d.name
                ? d.name
                : d.label
            })
          } else {
            return data[0].value.map(function(d) {
              return d.label
            })
          }
        }
      } else {
        return data[0].value.map(function(d) {
          return d.label
        })
      }
    } else {
      return data.map(function(d) {
        return d.label
      })
    }
  },

  getMinMax(data) {
    let min = d3.min(data, function(d) {
      if (!d.violated_value) {
        if (Array.isArray(d.value)) {
          return d3.min(d.value, function(v) {
            return v.value
          })
        } else {
          return d.value
        }
      }
    })

    let max = d3.max(data, function(d) {
      if (Array.isArray(d.value)) {
        return d3.max(d.value, function(v) {
          return v.value
        })
      } else {
        return d.value
      }
    })

    return {
      min: min,
      max: max,
    }
  },

  // only for number and time based label values //
  getMinMaxLabel(data) {
    let min = d3.min(data, function(d) {
      if (Array.isArray(d.value)) {
        return d3.min(d.value, function(v) {
          return v.orgLabel
        })
      } else {
        return d.orgLabel
      }
    })

    let max = d3.max(data, function(d) {
      if (Array.isArray(d.value)) {
        return d3.max(d.value, function(v) {
          return v.orgLabel
        })
      } else {
        return d.orgLabel
      }
    })

    return {
      min: min,
      max: max,
    }
  },

  getMinMaxNumberLabel(data) {
    let min = d3.min(data, function(d) {
      if (Array.isArray(d.value)) {
        return d3.min(d.value, function(v) {
          return v.label
        })
      } else {
        return d.label
      }
    })

    let max = d3.max(data, function(d) {
      if (Array.isArray(d.value)) {
        return d3.max(d.value, function(v) {
          return v.label
        })
      } else {
        return d.label
      }
    })

    return {
      min: min,
      max: max,
    }
  },

  getTotalMinMax(data) {
    let min = d3.min(data, function(d) {
      if (Array.isArray(d.value)) {
        return d3.sum(d.value, function(v) {
          return v.value
        })
      } else {
        return d.value
      }
    })

    let max = d3.max(data, function(d) {
      if (Array.isArray(d.value)) {
        return d3.sum(d.value, function(v) {
          return v.value
        })
      } else {
        return d.value
      }
    })

    return {
      min: min,
      max: max,
    }
  },

  // getMillsToFormater (chartContext,  date) {
  //   let xaxisConfig = chartContext.getOptions().xaxis ? chartContext.getOptions().xaxis : {datatype: 'text'}
  // }

  addDays(date, days) {
    const result = new Date(date)
    result.setDate(result.getDate() + days)
    return String(result)
  },

  calculatePercent(value, data) {
    let total = d3.sum(data, function(v) {
      return v.value
    })
    const percent = total ? (value / total) * 100 : 0
    return d3Format.format('.1f')(percent)
  },

  diffDays(date1, date2) {
    const oneDay = 24 * 60 * 60 * 1000
    return Math.ceil(
      Math.abs((new Date(date1).getTime() - new Date(date2).getTime()) / oneDay)
    )
  },

  isInteger(value) {
    return value % 1 === 0
  },

  cleanData(originalData) {
    let dataZeroed = originalData.map(d => ({
      value: 0,
      label: String(d['label']),
    }))

    return dataZeroed
  },
  sortAscending(objectArray) {
    let sortedArray = objectArray.sort(function(a, b) {
      return a['value'] - b['value']
    })
    return sortedArray
  },
  sortAscending1(objectArray) {
    objectArray.sort(function(a, b) {
      return a.label > b.label ? -1 : a.label < b.label ? 1 : 0
    })
    return objectArray.reverse()
  },
  sortDescending(objectArray) {
    let sortedArray = objectArray.sort(function(a, b) {
      return a['value'] - b['value']
    })
    return sortedArray.reverse()
  },
  sortDateAscending(objectArray) {
    objectArray.sort(function(a, b) {
      a = new Date(a.label)
      b = new Date(b.label)
      return a > b ? -1 : a < b ? 1 : 0
    })
    return objectArray.reverse()
  },
  convertToMins(millis) {
    if (millis) {
      let formatted =
        millis / 1000 / 60 > 99
          ? (millis / 1000 / 60 / 60).toFixed(2)
          : (millis / 1000 / 60).toFixed(2)
      let hours = Math.floor(Math.abs(formatted))
      let min = Math.floor((Math.abs(formatted) * 60) % 60)
      let val = (hours > 0 ? hours + 'h ' : '') + min
      return val + 'min'
    } else {
      return '--'
    }
  },
  // getThisContext () {
  //   if (window.__VUE_DEVTOOLS_GLOBAL_HOOK__.store && window.__VUE_DEVTOOLS_GLOBAL_HOOK__.store.state) {
  //     return window.__VUE_DEVTOOLS_GLOBAL_HOOK__.store.state
  //   }
  //   else {
  //     return null
  //   }
  // }
}
