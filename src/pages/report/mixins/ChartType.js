const chartTypes = [
  {
    label: 'Pie',
    ct: 'pie',
    icon:
      '<svg width="20px" height="16px" viewBox="0 0 20 16" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"><title>pie chart</title><desc>Created with Sketch.</desc><defs><circle id="path-1" cx="10" cy="8" r="5"></circle><mask id="mask-2" maskContentUnits="userSpaceOnUse" maskUnits="objectBoundingBox" x="0" y="0" width="10" height="10" fill="white"><use xlink:href="#path-1"></use></mask><circle id="path-3" cx="10" cy="8" r="5"></circle><mask id="mask-4" maskContentUnits="userSpaceOnUse" maskUnits="objectBoundingBox" x="0" y="0" width="10" height="10" fill="white"><use xlink:href="#path-3"></use></mask</defs><g id="Artboard" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd" transform="translate(-633.000000, -98.000000)"><g id="pie-chart" transform="translate(633.000000, 98.000000)"><rect id="Rectangle-8" stroke="#EF508F" x="0.5" y="0.5" width="19" height="15" rx="1.20000005"></rect><circle id="Oval" fill="#EF508F" transform="translate(10.000000, 8.000000) rotate(-15.000000) translate(-10.000000, -8.000000) " cx="10" cy="8" r="5"></circle><use id="#FFC53A:40%" stroke="#FA90BA" mask="url(#mask-2)" stroke-width="10" stroke-dasharray="12.57142857142857,50" transform="translate(10.000000, 8.000000) rotate(-15.000000) translate(-10.000000, -8.000000) " xlink:href="#path-1"></use><use id="#FAFF81:0.2" stroke="#FFC4DB" mask="url(#mask-4)" stroke-width="10" stroke-dasharray="6.285714285714286,50" transform="translate(10.000000, 8.000000) rotate(-15.000000) translate(-10.000000, -8.000000) " xlink:href="#path-3"></use></g></g></svg>',
    rule: function(report) {
      if (report.data.length > 1) {
        // multi series data
        return false
      } else {
        let data = report.data[0].data
        if (data.length && Array.isArray(data[0].value)) {
          return false
        } else if (report.options.xaxis.datatype.indexOf('date') !== -1) {
          return false
        }
      }
      return true
    },
  },
  {
    label: 'Doughnut',
    ct: 'doughnut',
    icon:
      '<svg id="icon-donut" viewBox="0 0 32 32" width="100%" height="100%"><path fill="#49bfda" style="fill: var(--color5, #49bfda)" d="M24.4 16.2v.8l5 .5c.4-3.3-.4-7-2.8-9.7-2-2.7-5-4.4-8.2-4.8l-.6 5.4c3.8.6 6.6 4 6.6 7.8z"></path><path fill="#ef7c80" style="fill: var(--color33, #ef7c80)" d="M16.4 8.2h1L18 3c-3.4-.4-7 .4-10 2.8-5.6 4.4-6.5 12.8-2 18.5.2 0 0 0 .2.2l4-3.2c-1-1.4-1.7-3-1.7-5 0-4.5 3.5-8 8-8z"></path><path fill="#b8d15c" style="fill: var(--color3, #b8d15c)" d="M16.4 24.2c-2.4 0-4.5-1-6-2.7l-4 3.2c4.7 5.3 12.6 6 18 1.5 2.8-2 4.4-5 5-8.2l-5.2-.6c-.4 4-3.7 6.8-7.8 6.8z"></path><path fill="#fff" style="fill: var(--color8, #fff)" d="M18 3h.5L18 8.4h-.6L18 3zm11.4 14.5v.5l-5-.6V17l5 .5zm-19.2 3.7l.3.3-4 3.3-.3-.4 4-3.2z"></path></svg>',
    rule: function(report) {
      if (report.data.length > 1) {
        // multi series data
        return false
      } else {
        let data = report.data[0].data
        if (data.length && Array.isArray(data[0].value)) {
          return false
        } else if (report.options.xaxis.datatype.indexOf('date') !== -1) {
          return false
        }
      }
      return true
    },
  },
  {
    label: 'Bar',
    ct: 'bar',
    icon:
      '<svg id="icon-bar-flat" viewBox="0 0 32 32" width="100%" height="100%"><path fill="#ef7c80" style="fill: var(--color33, #ef7c80)" d="M4.6 5.3H11v22H4.6v-22z"></path><path fill="#49bfda" style="fill: var(--color5, #49bfda)" d="M13.4 10.2h6.4v17.2h-6.4V10.2z"></path><path fill="#b8d15c" style="fill: var(--color3, #b8d15c)" d="M22.3 12.8h6.4v14.6h-6.4V12.8z"></path></svg>',
    rule: function(report) {
      if (report.data.length > 1) {
        // multi series data
        return false
      } else {
        let data = report.data[0].data
        let options = report.data[0].options
        if (data.length && Array.isArray(data[0].value)) {
          return false
        } else if (options.is_highres_data) {
          return false
        }
      }
      return true
    },
  },
  {
    label: 'Stacked Bar',
    ct: 'stackedbar',
    icon:
      '<svg id="icon-stacked" viewBox="0 0 32 32" width="100%" height="100%"><path fill="#ef7c80" style="fill: var(--color41, #ef7c80)" d="M4.6 4.5H11v7.2H4.6V4.5z"></path><path fill="#b8d15c" style="fill: var(--color5, #b8d15c)" d="M4.6 11.7H11V19H4.6v-7.3z"></path><path fill="#49bfda" style="fill: var(--color7, #49bfda)" d="M4.6 19H11v8H4.6v-8z"></path><path fill="#ef7c80" style="fill: var(--color41, #ef7c80)" d="M13.4 9.3h6.4V15h-6.4V9.2z"></path><path fill="#b8d15c" style="fill: var(--color5, #b8d15c)" d="M13.4 15h6.4v5.5h-6.4V15z"></path><path fill="#49bfda" style="fill: var(--color7, #49bfda)" d="M13.4 20.5h6.4V27h-6.4v-6.5z"></path><path fill="#ef7c80" style="fill: var(--color41, #ef7c80)" d="M22.2 13.3h6.4V18h-6.4v-4.7z"></path><path fill="#b8d15c" style="fill: var(--color5, #b8d15c)" d="M22.2 18h6.4v4h-6.4v-4z"></path><path fill="#49bfda" style="fill: var(--color7, #49bfda)" d="M22.2 22h6.4v5h-6.4v-5z"></path></svg>',
    rule: function(report) {
      if (report.data.length > 1) {
        // multi series data
        if (!report.data[0].options.is_highres_data) {
          return true
        }
        return false
      } else {
        let data = report.data[0].data
        let options = report.data[0].options
        if (data.length && !Array.isArray(data[0].value)) {
          return false
        } else if (options.is_highres_data) {
          return false
        }
      }
      return true
    },
  },
  {
    label: 'Grouped Bar',
    ct: 'groupedbar',
    icon:
      '<svg id="icon-stacked" viewBox="0 0 32 32" width="100%" height="100%"><path fill="#ef7c80" style="fill: var(--color41, #ef7c80)" d="M4.6 4.5H11v7.2H4.6V4.5z"></path><path fill="#b8d15c" style="fill: var(--color5, #b8d15c)" d="M4.6 11.7H11V19H4.6v-7.3z"></path><path fill="#49bfda" style="fill: var(--color7, #49bfda)" d="M4.6 19H11v8H4.6v-8z"></path><path fill="#ef7c80" style="fill: var(--color41, #ef7c80)" d="M13.4 9.3h6.4V15h-6.4V9.2z"></path><path fill="#b8d15c" style="fill: var(--color5, #b8d15c)" d="M13.4 15h6.4v5.5h-6.4V15z"></path><path fill="#49bfda" style="fill: var(--color7, #49bfda)" d="M13.4 20.5h6.4V27h-6.4v-6.5z"></path><path fill="#ef7c80" style="fill: var(--color41, #ef7c80)" d="M22.2 13.3h6.4V18h-6.4v-4.7z"></path><path fill="#b8d15c" style="fill: var(--color5, #b8d15c)" d="M22.2 18h6.4v4h-6.4v-4z"></path><path fill="#49bfda" style="fill: var(--color7, #49bfda)" d="M22.2 22h6.4v5h-6.4v-5z"></path></svg>',
    rule: function(report) {
      if (report.data.length > 1) {
        // multi series data
        if (!report.data[0].options.is_highres_data) {
          return true
        }
        return false
      } else {
        let data = report.data[0].data
        let options = report.data[0].options
        if (data.length && !Array.isArray(data[0].value)) {
          return false
        } else if (options.is_highres_data) {
          return false
        }
      }
      return true
    },
  },
  {
    label: 'Progress',
    ct: 'progress',
    icon:
      '<svg id="icon-bubble" viewBox="0 0 32 32" width="100%" height="100%"><path fill="#999" style="fill: var(--color40, #999)" d="M.3 27.8h32v.8H.3v-.8z"></path><path fill="#999" style="fill: var(--color40, #999)" d="M.3 4H1v24H.4V4z"></path><path fill="#35c7e1" stroke="#136776" stroke-width=".4" stroke-miterlimit="10" style="stroke: var(--color42, #136776), fill: var(--color41, #35c7e1)" d="M8.3 23.7C8.3 25 7.3 26 6 26s-2-1-2-2.4c0-1.3 1-2.3 2.3-2.3s2.3 1 2.3 2.3z"></path><path fill="#f57c7f" stroke="#cd4c4e" stroke-width=".4" stroke-miterlimit="10" style="stroke: var(--color44, #cd4c4e), fill: var(--color43, #f57c7f)" d="M22 15c0 3-2.5 5.4-5.5 5.4S11 18 11 15c0-3.2 2.5-5.6 5.5-5.6S22 11.8 22 15z"></path><path fill="#b8e659" stroke="#688332" stroke-width=".5" stroke-miterlimit="10" style="stroke: var(--color46, #688332), fill: var(--color45, #b8e659)" d="M29.8 22c0 1.8-1.3 3-3 3s-3-1.2-3-3c0-1.6 1.4-3 3-3s3 1.4 3 3z"></path><path fill="#cecece" stroke="#999" stroke-width=".4" stroke-miterlimit="10" style="stroke: var(--color40, #999), fill: var(--color39, #cecece)" d="M7.5 11.7C7.5 12.5 7 13 6 13s-1.4-.5-1.4-1.3.7-1.5 1.5-1.5 1.5.7 1.5 1.5z"></path></svg>',
    rule: function(report) {
      if (report.data.length > 1) {
        // multi series data
        return false
      } else {
        let data = report.data[0].data
        let options = report.data[0].options
        if (data.length && Array.isArray(data[0].value)) {
          return false
        } else if (options.is_highres_data) {
          return false
        }
      }
      return true
    },
  },
  {
    label: 'Line',
    ct: 'line',
    icon:
      '<svg id="icon-line3" viewBox="0 0 32 32" width="100%" height="100%"><path fill="none" stroke="#49bfda" stroke-width="2.4" stroke-miterlimit="10" style="stroke: var(--color5, #49bfda)" d="M4.2 25l6.8-8.3 5 5.3 6-7.6 6-2.5"></path><path fill="#fff" stroke="#49bfda" stroke-width="1.6" stroke-miterlimit="10" style="stroke: var(--color5, #49bfda)" d="M6.7 24c0 1-.7 2-1.7 2s-1.8-1-1.8-2S4 22.4 5 22.4s1.7.8 1.7 1.8zm6.3-7.2c0 1-1 1.8-2 1.8s-1.6-.8-1.6-1.8S10 15 11 15s2 .8 2 1.8zm5 5c0 1-1 1.8-1.8 1.8s-1.8-.8-1.8-1.8.8-1.7 1.8-1.7S18 21 18 22zm6.3-7.3c0 1-.8 1.7-1.7 1.7s-1.8-.7-1.8-1.7.8-1.8 1.8-1.8 1.7.8 1.7 1.8z"></path><path fill="#9a9999" style="fill: var(--color7, #9a9999)" d="M.8 27h30v.8H.7V27zm0-24h.8v24H.8V3z"></path></svg>',
    rule: function(report) {
      if (report.data.length > 1) {
        // multi series data
        if (!report.data[0].options.is_highres_data) {
          return true
        }
        return false
      } else {
        let options = report.data[0].options
        if (options.xaxis.datatype.indexOf('date') !== -1) {
          return true
        }
      }
      return false
    },
  },
  {
    label: 'Area',
    ct: 'area',
    icon:
      '<svg id="icon-area1" viewBox="0 0 32 32" width="100%" height="100%"><path fill="#b8d15c" style="fill: var(--color3, #b8d15c)" d="M.8 27l14-13.8 13 10.2 3.4-.2v4z"></path><path fill="none" stroke="#7e9649" stroke-width=".8" stroke-miterlimit="10" style="stroke: var(--color4, #7e9649)" d="M.6 27l14-13.8 12.8 10.2 3.4-.2v4"></path><path fill="#49bfda" opacity=".8" style="fill: var(--color5, #49bfda)" d="M.8 12.2l9.2 11 5.4-5.2 12 9.4L.7 27z"></path><path fill="none" stroke="#1f7d8e" stroke-width=".8" stroke-miterlimit="10" style="stroke: var(--color6, #1f7d8e)" d="M.8 12.2l9.4 11.3 5.4-5.4 11.7 9.3"></path><path fill="#9a9999" style="fill: var(--color7, #9a9999)" d="M0 27h32v.8H0V27zM0 3h.8v24H0V3z"></path></svg>',
    rule: function(report) {
      if (report.data.length > 1) {
        // multi series data
        if (!report.data[0].options.is_highres_data) {
          return true
        }
        return false
      } else {
        let options = report.data[0].options
        if (options.xaxis.datatype.indexOf('date') !== -1) {
          return true
        }
      }
      return false
    },
  },
  {
    label: 'Scatter',
    ct: 'scatter',
    icon:
      '<svg id="icon-area1" viewBox="0 0 32 32" width="100%" height="100%"><path fill="#b8d15c" style="fill: var(--color3, #b8d15c)" d="M.8 27l14-13.8 13 10.2 3.4-.2v4z"></path><path fill="none" stroke="#7e9649" stroke-width=".8" stroke-miterlimit="10" style="stroke: var(--color4, #7e9649)" d="M.6 27l14-13.8 12.8 10.2 3.4-.2v4"></path><path fill="#49bfda" opacity=".8" style="fill: var(--color5, #49bfda)" d="M.8 12.2l9.2 11 5.4-5.2 12 9.4L.7 27z"></path><path fill="none" stroke="#1f7d8e" stroke-width=".8" stroke-miterlimit="10" style="stroke: var(--color6, #1f7d8e)" d="M.8 12.2l9.4 11.3 5.4-5.4 11.7 9.3"></path><path fill="#9a9999" style="fill: var(--color7, #9a9999)" d="M0 27h32v.8H0V27zM0 3h.8v24H0V3z"></path></svg>',
    rule: function(report) {
      if (report.data.length > 1) {
        // multi series data
        if (!report.data[0].options.is_highres_data) {
          return true
        }
        return false
      } else {
        let options = report.data[0].options
        if (options.xaxis.datatype.indexOf('date') !== -1) {
          return true
        }
      }
      return false
    },
  },
  {
    label: 'Boolean',
    ct: 'boolean',
    icon:
      '<svg id="icon-area1" viewBox="0 0 32 32" width="100%" height="100%"><path fill="#b8d15c" style="fill: var(--color3, #b8d15c)" d="M.8 27l14-13.8 13 10.2 3.4-.2v4z"></path><path fill="none" stroke="#7e9649" stroke-width=".8" stroke-miterlimit="10" style="stroke: var(--color4, #7e9649)" d="M.6 27l14-13.8 12.8 10.2 3.4-.2v4"></path><path fill="#49bfda" opacity=".8" style="fill: var(--color5, #49bfda)" d="M.8 12.2l9.2 11 5.4-5.2 12 9.4L.7 27z"></path><path fill="none" stroke="#1f7d8e" stroke-width=".8" stroke-miterlimit="10" style="stroke: var(--color6, #1f7d8e)" d="M.8 12.2l9.4 11.3 5.4-5.4 11.7 9.3"></path><path fill="#9a9999" style="fill: var(--color7, #9a9999)" d="M0 27h32v.8H0V27zM0 3h.8v24H0V3z"></path></svg>',
    rule: function(report) {
      let options = report.data[0].options
      if (
        options.is_highres_data &&
        options.xaxis.datatype.indexOf('date') !== -1 &&
        options.y1axis.datatype === 'boolean'
      ) {
        return true
      }
      return false
    },
  },
  {
    label: 'Timeseries',
    ct: 'timeseries',
    icon:
      '<svg id="icon-line3" viewBox="0 0 32 32" width="100%" height="100%"><path fill="none" stroke="#49bfda" stroke-width="2.4" stroke-miterlimit="10" style="stroke: var(--color5, #49bfda)" d="M4.2 25l6.8-8.3 5 5.3 6-7.6 6-2.5"></path><path fill="#fff" stroke="#49bfda" stroke-width="1.6" stroke-miterlimit="10" style="stroke: var(--color5, #49bfda)" d="M6.7 24c0 1-.7 2-1.7 2s-1.8-1-1.8-2S4 22.4 5 22.4s1.7.8 1.7 1.8zm6.3-7.2c0 1-1 1.8-2 1.8s-1.6-.8-1.6-1.8S10 15 11 15s2 .8 2 1.8zm5 5c0 1-1 1.8-1.8 1.8s-1.8-.8-1.8-1.8.8-1.7 1.8-1.7S18 21 18 22zm6.3-7.3c0 1-.8 1.7-1.7 1.7s-1.8-.7-1.8-1.7.8-1.8 1.8-1.8 1.7.8 1.7 1.8z"></path><path fill="#9a9999" style="fill: var(--color7, #9a9999)" d="M.8 27h30v.8H.7V27zm0-24h.8v24H.8V3z"></path></svg>',
    rule: function(report) {
      let options = report.data[0].options
      if (options.xaxis.datatype.indexOf('date') !== -1) {
        return true
      }
      return false
    },
  },
  {
    label: 'Heatmap',
    ct: 'heatMap',
    icon:
      '<svg id="icon-bubble" viewBox="0 0 32 32" width="100%" height="100%"><path fill="#999" style="fill: var(--color40, #999)" d="M.3 27.8h32v.8H.3v-.8z"></path><path fill="#999" style="fill: var(--color40, #999)" d="M.3 4H1v24H.4V4z"></path><path fill="#35c7e1" stroke="#136776" stroke-width=".4" stroke-miterlimit="10" style="stroke: var(--color42, #136776), fill: var(--color41, #35c7e1)" d="M8.3 23.7C8.3 25 7.3 26 6 26s-2-1-2-2.4c0-1.3 1-2.3 2.3-2.3s2.3 1 2.3 2.3z"></path><path fill="#f57c7f" stroke="#cd4c4e" stroke-width=".4" stroke-miterlimit="10" style="stroke: var(--color44, #cd4c4e), fill: var(--color43, #f57c7f)" d="M22 15c0 3-2.5 5.4-5.5 5.4S11 18 11 15c0-3.2 2.5-5.6 5.5-5.6S22 11.8 22 15z"></path><path fill="#b8e659" stroke="#688332" stroke-width=".5" stroke-miterlimit="10" style="stroke: var(--color46, #688332), fill: var(--color45, #b8e659)" d="M29.8 22c0 1.8-1.3 3-3 3s-3-1.2-3-3c0-1.6 1.4-3 3-3s3 1.4 3 3z"></path><path fill="#cecece" stroke="#999" stroke-width=".4" stroke-miterlimit="10" style="stroke: var(--color40, #999), fill: var(--color39, #cecece)" d="M7.5 11.7C7.5 12.5 7 13 6 13s-1.4-.5-1.4-1.3.7-1.5 1.5-1.5 1.5.7 1.5 1.5z"></path></svg>',
    rule: function(report) {
      let options = report.data[0].options
      if (
        options.xaxis.datatype.indexOf('date') !== -1 &&
        options.xaxis.operation === 'hoursofdayonly'
      ) {
        return true
      }
      return false
    },
  },
  {
    label: 'Treemap',
    ct: 'treemap',
    icon:
      '<svg id="icon-bubble" viewBox="0 0 32 32" width="100%" height="100%"><path fill="#999" style="fill: var(--color40, #999)" d="M.3 27.8h32v.8H.3v-.8z"></path><path fill="#999" style="fill: var(--color40, #999)" d="M.3 4H1v24H.4V4z"></path><path fill="#35c7e1" stroke="#136776" stroke-width=".4" stroke-miterlimit="10" style="stroke: var(--color42, #136776), fill: var(--color41, #35c7e1)" d="M8.3 23.7C8.3 25 7.3 26 6 26s-2-1-2-2.4c0-1.3 1-2.3 2.3-2.3s2.3 1 2.3 2.3z"></path><path fill="#f57c7f" stroke="#cd4c4e" stroke-width=".4" stroke-miterlimit="10" style="stroke: var(--color44, #cd4c4e), fill: var(--color43, #f57c7f)" d="M22 15c0 3-2.5 5.4-5.5 5.4S11 18 11 15c0-3.2 2.5-5.6 5.5-5.6S22 11.8 22 15z"></path><path fill="#b8e659" stroke="#688332" stroke-width=".5" stroke-miterlimit="10" style="stroke: var(--color46, #688332), fill: var(--color45, #b8e659)" d="M29.8 22c0 1.8-1.3 3-3 3s-3-1.2-3-3c0-1.6 1.4-3 3-3s3 1.4 3 3z"></path><path fill="#cecece" stroke="#999" stroke-width=".4" stroke-miterlimit="10" style="stroke: var(--color40, #999), fill: var(--color39, #cecece)" d="M7.5 11.7C7.5 12.5 7 13 6 13s-1.4-.5-1.4-1.3.7-1.5 1.5-1.5 1.5.7 1.5 1.5z"></path></svg>',
    disabled: true,
    rule: function(report) {
      if (report.data.length > 1) {
        // multi series data
        return false
      } else {
        let data = report.data[0].data
        if (data.length && Array.isArray(data[0].value)) {
          return false
        } else if (report.options.xaxis.datatype.indexOf('date') !== -1) {
          return false
        }
      }
      return true
    },
  },
  {
    label: 'Regression',
    ct: 'regression',
    icon:
      '<svg id="icon-bubble" viewBox="0 0 32 32" width="100%" height="100%"><path fill="#999" style="fill: var(--color40, #999)" d="M.3 27.8h32v.8H.3v-.8z"></path><path fill="#999" style="fill: var(--color40, #999)" d="M.3 4H1v24H.4V4z"></path><path fill="#35c7e1" stroke="#136776" stroke-width=".4" stroke-miterlimit="10" style="stroke: var(--color42, #136776), fill: var(--color41, #35c7e1)" d="M8.3 23.7C8.3 25 7.3 26 6 26s-2-1-2-2.4c0-1.3 1-2.3 2.3-2.3s2.3 1 2.3 2.3z"></path><path fill="#f57c7f" stroke="#cd4c4e" stroke-width=".4" stroke-miterlimit="10" style="stroke: var(--color44, #cd4c4e), fill: var(--color43, #f57c7f)" d="M22 15c0 3-2.5 5.4-5.5 5.4S11 18 11 15c0-3.2 2.5-5.6 5.5-5.6S22 11.8 22 15z"></path><path fill="#b8e659" stroke="#688332" stroke-width=".5" stroke-miterlimit="10" style="stroke: var(--color46, #688332), fill: var(--color45, #b8e659)" d="M29.8 22c0 1.8-1.3 3-3 3s-3-1.2-3-3c0-1.6 1.4-3 3-3s3 1.4 3 3z"></path><path fill="#cecece" stroke="#999" stroke-width=".4" stroke-miterlimit="10" style="stroke: var(--color40, #999), fill: var(--color39, #cecece)" d="M7.5 11.7C7.5 12.5 7 13 6 13s-1.4-.5-1.4-1.3.7-1.5 1.5-1.5 1.5.7 1.5 1.5z"></path></svg>',
    rule: function(report) {
      let options = report.data[0].options
      if (
        options.trendline &&
        (options.xaxis.datatype.indexOf('number') !== -1 ||
          options.xaxis.datatype.indexOf('decimal') !== -1)
      ) {
        return true
      }
      return false
    },
  },
  {
    label: 'Runtime',
    ct: 'runtime',
    disabled: true,
    icon:
      '<svg id="icon-bar-flat" viewBox="0 0 32 32"  width="100%" height="100%"><path fill="#ef7c80" style="fill: var(--color33, #ef7c80)" d="M4.6 5.3H11v22H4.6v-22z"></path><path fill="#49bfda" style="fill: var(--color5, #49bfda)" d="M13.4 10.2h6.4v17.2h-6.4V10.2z"></path><path fill="#b8d15c" style="fill: var(--color3, #b8d15c)" d="M22.3 12.8h6.4v14.6h-6.4V12.8z"></path></svg>',
    rule: function(report) {
      let options = report.data[0].options
      if (
        options.xaxis.datatype.indexOf('date') !== -1 &&
        options.xaxis.operation === 'hoursofdayonly'
      ) {
        return true
      }
      return false
    },
  },
  {
    label: 'Combo Chart',
    ct: 'combo',
    disabled: false,
    icon:
      '<svg id="icon-bar-flat" viewBox="0 0 32 32"  width="100%" height="100%"><path fill="#ef7c80" style="fill: var(--color33, #ef7c80)" d="M4.6 5.3H11v22H4.6v-22z"></path><path fill="#49bfda" style="fill: var(--color5, #49bfda)" d="M13.4 10.2h6.4v17.2h-6.4V10.2z"></path><path fill="#b8d15c" style="fill: var(--color3, #b8d15c)" d="M22.3 12.8h6.4v14.6h-6.4V12.8z"></path></svg>',
    rule: function(report) {
      if (report.data.length > 1) {
        if (!report.data[0].options.is_highres_data) {
          return true
        }
      } else if (report.combined) {
        return true
      }
      return false
    },
  },
]

export default {
  methods: {
    getChartType(report) {
      let availableChartTypes = []
      if (report.data && report.data.length) {
        for (let chartType of chartTypes) {
          if (chartType.rule(report)) {
            availableChartTypes.push(chartType)
          }
        }
      } else {
        availableChartTypes = chartTypes
      }

      let chartType = null
      if (report.options.is_combo) {
        chartType = chartTypes.find(ctype => ctype.ct === report.options.type)
        report.options.type = chartType.ct
      } else {
        chartType = availableChartTypes.find(
          ctype => ctype.ct === report.options.type
        )
        if (!chartType) {
          if (
            report.options.type === availableChartTypes[0].ct &&
            availableChartTypes.length > 1
          ) {
            chartType = availableChartTypes[0]
          } else {
            chartType = availableChartTypes[1]
          }
        }
        report.options.type = chartType.ct
      }
      return {
        chartType: chartType,
        availableChartTypes: availableChartTypes,
      }
    },

    getCombinationChartTypes() {
      let ctypeList = ['bar', 'line', 'area']
      let availableChartTypes = []
      for (let chartType of chartTypes) {
        if (ctypeList.indexOf(chartType.ct) !== -1) {
          availableChartTypes.push(chartType)
        }
      }
      return availableChartTypes
    },
  },
}
