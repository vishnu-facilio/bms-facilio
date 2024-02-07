<template>
  <div class="row chart-option" :class="{ sl: isSmall }">
    <div
      :class="['ch-option', { 'diff-layout': key === 'diff' }]"
      v-for="(option, key) in chartContext.widgetLegends"
      :key="key"
      :style="{
        'max-width': maxWidth + 'px',
        'font-size': fontSize,
        'padding-right': isSmall && option.type !== 'baseLine' ? '5px' : '20px',
      }"
    >
      <div
        class="c-sublabel variance ellipsis"
        :title="option.varianceLabel"
        data-placement="right"
        data-arrow="true"
        v-tippy
      >
        {{ option.varianceLabel || '&nbsp;' }}
      </div>
      <div
        v-if="option.type === 'baseLine'"
        class="baseline"
        :style="getBaseLineStyle(key, option)"
      ></div>
      <svg
        v-else-if="option.legendColor"
        class="legendcircle flLeft ch-circle"
        :style="{ 'margin-top': isSmall ? '3px' : '5px' }"
      >
        <circle
          :cx="isSmall ? 7 : 9"
          :cy="isSmall ? 7 : 9"
          :r="isSmall ? 7 : 9"
          :fill="option.legendColor"
        />
      </svg>
      <template v-else-if="key === 'diff' && option.value">
        <img
          class="diff-icon flLeft"
          src="~statics/energy/arrow-red.svg"
          v-if="option.increment"
        />
        <img
          class="diff-icon flLeft rotate180"
          src="~statics/energy/arrow-green.svg"
          v-else
        />
      </template>
      <div
        :class="{
          'legend-data':
            (option.legendColor && option.type !== 'baseLine') ||
            key === 'diff',
        }"
      >
        <div
          class="ch-value ellipsis"
          :title="option.value + ' ' + option.unit"
          data-placement="right"
          data-arrow="true"
          v-tippy
        >
          {{ option.value }}&nbsp;<span
            class="ch-unit"
            v-html="option.unit"
          ></span>
        </div>
        <div class="c-sublabel" v-if="option.subLabel">
          {{ option.subLabel }}
        </div>
        <div class="c-sublabel" v-else-if="option.type === 'baseLine'">
          {{ getDateLabel(option.baseLineDiff) }}
        </div>
        <div class="ch-description uppercase">{{ option.label }}</div>
      </div>
    </div>
  </div>
</template>
<script>
import moment from 'moment-timezone'
export default {
  props: ['chartContext', 'width', 'height'],
  data() {
    return {
      options: [],
      chartDescribe: [],
    }
  },
  computed: {
    isSmall() {
      return this.width < 560 || this.height <= 340
    },
    fontSize() {
      let val = (this.width - 300) / 100
      val = val <= 1 ? 100 : val * 50 + 30
      if (this.chartContext && this.chartContext.widgetLegends) {
        let maxWid =
          this.width / Object.keys(this.chartContext.widgetLegends).length
        let size =
          val < maxWid ? val / 8.333333333333334 : maxWid / 8.333333333333334
        return size > 17 ? '1.1vw' : size + 'px'
      }
      return val / 8.333333333333334 > 17
        ? '1.1vw'
        : val / 8.333333333333334 + 'px'
    },
    maxWidth() {
      let val = (this.width - 300) / 100
      val = val <= 1 ? 100 : val * 50 + 30
      if (this.chartContext && this.chartContext.widgetLegends) {
        let maxWid =
          this.width / Object.keys(this.chartContext.widgetLegends).length
        return val < maxWid ? val : maxWid
      }
      return val
    },
  },
  methods: {
    // Temp
    getDateLabel(diff) {
      let option = this.chartContext.timeObject.field
      let date = this.chartContext.date_range
      let time = []
      if (Array.isArray(date)) {
        time[0] = date[0]
        time[1] = date[1]
      } else {
        time[0] = date
      }
      if (diff) {
        time[0] -= diff
        if (time[1]) {
          time[1] -= diff
        }
      }
      if (option !== 'D') {
        if (option === 'M') {
          return moment(time[0]).format('MMM YYYY')
        } else if (option === 'Y') {
          return moment(time[0]).format('YYYY')
        } else {
          if (option === 'W') {
            let array = moment(time[0]).toArray()
            time[0] = moment([array[0], array[1], array[2]])
              .startOf('week')
              .valueOf()
            time[1] = moment([array[0], array[1], array[2]])
              .endOf('week')
              .valueOf()
          }
          return (
            this.$options.filters.formatDate(time[0], true, false) +
            '  -  ' +
            this.$options.filters.formatDate(time[1], true, false)
          )
        }
      } else {
        return this.$options.filters.formatDate(time[0], true, false)
      }
    },
    getBaseLineStyle(key, option) {
      return {
        border:
          (key === 'baseLine' && this.chartContext.type === 'timeseries'
            ? 'dashed'
            : 'solid') +
          ' 1.5px ' +
          option.legendColor,
      }
    },
  },
}
</script>
<style>
.ch-option {
  padding: 25px 5px 30px 20px;
  text-align: left;
  /* line-height: 1.4rem; */
  position: relative;
}

.ch-description {
  font-size: 0.8vw;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1px;
  text-align: left;
  color: #ef508f;
  padding-left: 1px;
}

.c-sublabel {
  color: #9e9e9e;
  font-size: 0.8vw;
  font-weight: 500;
}

.c-sublabel.variance {
  padding-left: 1px;
  padding-bottom: 1px;
}

.ch-value {
  /* font-size: 1.7vw; */
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.1px;
  text-align: left;
  color: #3d3d3d;
}
.ch-unit {
  font-size: 1vw;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.1px;
  text-align: left;
  color: #000000;
  margin-left: -3px;
}
.c-alert {
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0px;
  text-align: left;
  color: #ed6e6e;
  position: relative;
  bottom: 5px;
  left: 5px;
}
.chart-option {
  width: 100%;
  padding-left: 20px;
}
.c-icon {
  width: 20px;
  margin-right: -8px;
}
.diff-icon {
  width: 20px;
}

.baseline {
  width: 50px;
  height: 1.5px;
  border: dashed 1.5px #a96fae;
  margin-bottom: 5px;
}
.baseline.normal {
  border: solid 1.5px #a96fae;
}

.diff-layout {
  margin-top: 8px;
}

.ch-circle {
  width: 18px;
  height: 18px;
}

.legend-data {
  padding-left: 24px;
}

/***********   Small Container  **************/
/* .sl .ch-value {
  font-size: 17px;
} */

.sl .ch-unit,
.sl .ch-description,
.sl .c-sublabel {
  font-size: 11px;
}

.sl .ch-circle {
  width: 14px;
  height: 14px;
}

.sl .legend-data {
  padding-left: 20px;
}
</style>
