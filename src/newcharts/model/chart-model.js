export default {
  constant: {
    padding: {
      top: 40,
      right: 100,
      bottom: 10,
      left: 100,
    },
    size: {
      height: 450,
    },
  },

  options: {
    common: {
      mode: 1,
      buildingIds: [],
    },
    general: {
      grid: {
        y: true,
        x: false,
      },
      point: {
        show: false,
      },
      labels: false,
      normalizeStack: false,
      dataOrder: null,
      hideZeroes: false,
    },
    settings: {
      chartMode: 'single',
      alarm: false,
      chart: true,
      table: true,
      safelimit: true,
      enableAlarm: true,
      autoGroup: false,
      timeperiod: false,
      filterBar: false,
    },
    tooltip: {
      grouped: false,
      sortOrder: 'none',
      showNullValues: false,
      compare_indicator: 2,
    },
    donut: {
      labelType: 'percentage',
      centerText: {
        primaryText: null,
        primaryUnit: null,
        primaryRoundOff: false,
        secondaryText: null,
        secondaryUnit: null,
        secondaryRoundOff: false,
      },
    },
    area: {
      above: false,
      linearGradient: true,
    },
    line: {
      connectNull: true,
    },
    bar: {
      radius: null,
      padding: null,
      showGroupTotal: false,
      groupTotalLabel: 'Group Total',
    },
    treemap: {
      size: 'A',
      color: 'A',
    },
    table: {
      hideIndex: false,
      removeNegativeValue: false,
    },
    axis: {
      rotated: false,
      showy2axis: true,
      x: {
        show: true,
        label: {
          text: null,
          position: 'outer-center',
        },
        tick: {
          direction: 'auto',
        },
        range: {
          min: null,
          max: null,
        },
        ticks: {
          count: null,
        },
        format: {
          decimals: 0,
        },
      },
      y: {
        show: true,
        label: {
          text: null,
          position: 'outer-middle',
        },
        unit: null,
        scale: 'linear',
        range: {
          min: null,
          max: null,
        },
        ticks: {
          count: 5,
        },
        format: {
          decimals: 0,
        },
        padding: {
          bottom: 0,
        },
        formatUnit: 'None',
      },
      y2: {
        show: false,
        label: {
          text: null,
          position: 'outer-middle',
        },
        unit: null,
        scale: 'linear',
        range: {
          min: null,
          max: null,
        },
        ticks: {
          count: 5,
        },
        format: {
          decimals: 0,
        },
        padding: {
          top: 0,
          bottom: 0,
        },
        formatUnit: 'None',
      },
    },
    legend: {
      show: true,
      position: 'top',
      width: 180,
    },
    trendLine: {
      enable: false,
      type: '1',
      degree: '2',
      selectedPoints: [],
      showr2: false,
      decimal: 2,
    },
    scatter: {
      shape: 'circle',
      color: {
        mode: null,
        pallete: 1,
        colors: ['#1d7f01', '#6cb302', '#e9f501', '#fda504', '#fb5905'],
        reading: null,
      },
      size: {
        mode: null,
        reading: null,
      },
    },
    widgetLegend: {
      show: false,
    },
    benchmark: {
      show: false,
      label: 'Benchmark',
    },
    colorPalette: 'auto',
  },

  style: {
    pie: {
      label: {
        show: true,
      },
    },
    donut: {
      width: null,
      label: {
        show: true,
      },
    },
    gauge: {
      width: null,
      label: {
        show: true,
      },
      min: 0,
      max: 100,
      unit: ' %',
    },
    line: {
      point: {
        show: true,
        radius: 5,
      },
      lineMode: 'default', // 'spline', 'step'
      stepType: 'step', // 'step-after', 'step-before'
      stroke: {
        width: 1,
        opacity: 1,
        dashed: {
          length: 2,
          space: 2,
        },
      },
      connectNull: false,
    },
    area: {
      point: {
        show: true,
        radius: 5,
      },
      lineMode: 'default', // 'spline', 'step'
      stepType: 'step', // 'step-after', 'step-before'
      fillOpacity: null,
      stroke: {
        width: 1,
        opacity: 1,
        dashed: {
          length: 2,
          space: 2,
        },
      },
      connectNull: false,
    },
    bar: {
      width: null /* bar width in pixels or { ratio: 0.5 } */,
    },
    scatter: {
      point: {
        radius: 5,
      },
    },
  },
}
