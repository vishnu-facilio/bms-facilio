<template>
  <div
    ref="legend"
    class="legend"
    :style="LegendTransulate()"
    style="max-height: 100px;overflow-y: auto;overflow-x:hidden;"
  >
    <div
      class="fLegendContainer"
      ref="fLegendContainer"
      v-if="chartContext.options.type === 'boolean'"
    >
      <div
        v-for="(d, index) in chartContext.data[0].options.booleanKey"
        class="legendBoxNew pointer"
        :key="index"
        v-if="
          chartContext.data.length &&
            chartContext.data[0].options.booleanKey &&
            chartContext.data[0].options.booleanKey.length
        "
      >
        <svg width="14" height="14" class="legendcircle">
          <circle cx="7" cy="7" r="7" :fill="d.color" /></svg
        ><span style="padding-left:10px;">{{ d.value }}</span>
      </div>
    </div>
    <div
      class="fLegendContainer"
      ref="fLegendContainer"
      v-else-if="!chartContext.isSeriesData()"
    >
      <div
        v-for="(data, key, index) in chartContext.getLegends()"
        v-if="LegendsCount != 0 && index < LegendsCount"
        :key="key"
        class="legendBoxNew"
        @click="clickLengend(key, data)"
        :title="legendText(key)"
      >
        <svg width="14" height="14" class="legendcircle">
          <circle cx="7" cy="7" r="7" :fill="data" /></svg
        ><span style="padding-left:10px;">{{ legendText(key) }}</span>
      </div>
      <div
        v-if="
          LegendsCount != 0 &&
            Object.keys(this.chartContext.getLegends()).length > LegendsCount
        "
        v-popover:LegendsPopover
        class="pointer more-leg"
      >
        <el-popover ref="LegendsPopover" width="300" trigger="hover">
          <q-item
            class="q-item"
            v-for="(data, key, index) in chartContext.getLegends()"
            v-if="index >= LegendsCount"
            @click="clickLengend(key, data)"
            :key="key"
          >
            <svg width="14" height="14" class="legendcircle">
              <circle cx="7" cy="7" r="7" :fill="data" /></svg
            ><span style="padding-left:10px;">{{ legendText(key) }}</span>
          </q-item> </el-popover
        >+
        {{ Object.keys(this.chartContext.getLegends()).length - LegendsCount }}
      </div>
    </div>
    <div
      class="fLegendContainer"
      ref="fLegendContainer"
      v-else-if="chartContext.options.id === 1015"
    >
      <template v-for="(d, index) in chartContext.data">
        <template v-if="d.data.length && Array.isArray(d.data[0].value)">
          <div
            v-for="(data, key) in chartContext.getLegends()"
            :key="index + '' + key"
            class="legendBoxNew"
            :title="legendText(key)"
          >
            <svg width="14" height="14" class="legendcircle">
              <circle cx="7" cy="7" r="7" :fill="data" /></svg
            ><span style="padding-left:10px;">{{ legendText(key) }}</span>
          </div>
        </template>
        <div
          v-else
          class="legendBoxNew pointer"
          :class="{ inactive: d.options.show === false }"
          @click="toggleLegend(d)"
          :key="index"
        >
          <svg width="14" height="14" class="legendcircle">
            <circle cx="7" cy="7" r="7" :fill="d.options.color" /></svg
          ><span style="padding-left:10px;">{{ d.title }}</span>
        </div>
      </template>
    </div>
    <div class="fLegendContainer" ref="fLegendContainer" v-else>
      <div
        v-for="(d, index) in chartContext.data"
        class="legendBoxNew pointer"
        v-if="d.options && d.title"
        :class="{ inactive: d.options.show === false }"
        :key="index"
        @click="toggleLegend(d)"
      >
        <svg width="14" height="14" class="legendcircle">
          <circle cx="7" cy="7" r="7" :fill="d.options.color" /></svg
        ><span style="padding-left:10px;">{{ d.title }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import * as d3 from 'd3'
import common from 'charts/helpers/common'
import formatter from 'charts/helpers/formatter'
import { QItem } from 'quasar'
// const NUMBER_FORMAT = ',f'

export default {
  components: {
    QItem,
  },
  props: ['chartContext'],
  data() {
    return {
      Legendswidth: 0,
      margin: {
        top: 20,
        right: 20,
        bottom: 30,
        left: 60,
      },
      data: {},
      defaultOptions: {
        isHorizontal: false,
        yAxisPaddingBetweenChart: 10,
        isAnimated: true,
        ease: d3.easeQuadInOut,
        animationDuration: 800,
        animationStepRatio: 70,
        // interBarDelay: (d, i) => this.defaultOptions.animationStepRatio * i
      },
      LegendsCount: 0,
    }
  },
  mounted() {
    let legends = Object.keys(this.chartContext.getLegends())
    let firstRowCount = 0
    let secoundRowCount = 0
    let firstRowLength = 0
    let secoundRowLength = 0
    let firstRowComplete = false
    for (let i = 0; i < legends.length; i++) {
      let len = this.getTextWidth(legends[i]) + 44
      if (
        firstRowLength + len < this.chartContext.getWidth() - 30 &&
        !firstRowComplete
      ) {
        firstRowCount = firstRowCount + 1
        firstRowLength = firstRowLength + len
      } else {
        firstRowComplete = true
      }
      if (firstRowComplete) {
        let len2 = this.getTextWidth(legends[i]) + 44
        if (secoundRowLength + len2 < this.chartContext.getWidth() - 70) {
          secoundRowCount = secoundRowCount + 1
          secoundRowLength = secoundRowLength + len2
        }
      }
    }
    this.LegendsCount = firstRowCount + secoundRowCount
    this.Legendswidth = 'width:' + this.chartContext.getWidth() + 'px;'
    // this.LegendsCount = Math.floor(this.chartContext.getWidth() / 150)
  },
  methods: {
    getTextWidth(text) {
      // re-use canvas object for better performance
      let textWidthCanvas = null
      let canvas =
        textWidthCanvas || (textWidthCanvas = document.createElement('canvas'))
      let context = canvas.getContext('2d')
      let metrics = context.measureText(text)
      return metrics.width
    },
    legendText(key) {
      let xaxisConfig = this.chartContext.getOptions().xaxis
        ? this.chartContext.getOptions().xaxis
        : {
            datatype: 'string',
          }
      let groupbyConfig = this.chartContext.getOptions().groupby
        ? this.chartContext.getOptions().groupby
        : null
      let datatype =
        groupbyConfig && groupbyConfig.datatype
          ? groupbyConfig.datatype
          : xaxisConfig.datatype
      if (datatype) {
        if (datatype === 'date' || datatype === 'date_time') {
          return formatter.formatValue(new Date(key), xaxisConfig)
        } else if (datatype === 'number') {
          return key
        } else {
          return key
        }
      }
    },
    clickLengend(key, data) {},
    colorPicker(color) {
      if (color) {
        let background = 'background-color:' + color + ';'
        return background
      } else {
        return 'background-color: black;'
      }
    },
    LegendRowWidth() {
      if (this.chartContext.getWidth()) {
        return {
          width: this.chartContext.getWidth() + 'px;' + this.Legendswidth,
        }
      } else {
        return {
          width: 'auto',
        }
      }
    },
    LegendTransulate() {
      if (this.chartContext.getMargin().left) {
        return {
          transform:
            'translate(' +
            this.chartContext.getMargin().left +
            'px);' +
            this.Legendswidth,
        }
      } else {
        return {
          transform: 'translate(0px)',
        }
      }
    },
    toggleLegend(d) {
      let currentState = typeof d.options.show === 'undefined' || d.options.show
      d.options.show = !currentState
      this.chartContext.rerender()
    },
    legendAction(data) {
      if (common.isValueArray(this.chartContext.getData())) {
        for (let d of this.chartContext.getData()) {
          let axisList = d.value.filter(row => row.label === data)
        }
      } else {
        let axisList = this.chartContext
          .getData()
          .filter(row => row.label === data)
        let copyData = []
        copyData = this.chartContext.getData()
      }
    },
  },
}
</script>

<style>
.legendBox {
  width: 10px;
  height: 10px;
  background: green;
  align-items: center;
  align-self: center;
  border-radius: 10px;
}

.legendText {
  padding-left: 10px;
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  text-align: left;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

/* .legend.legendsAll {
  max-width: 450px;
  margin: 0;
  margin-top: 20px;
  margin-left: -20px;
} */

.legendcircle {
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  -ms-flex-item-align: center;
  -ms-grid-row-align: center;
  align-self: center;
}

.fLegendContainer {
  display: inline-flex;
  max-width: 700px;
  overflow-y: auto;
}

.fc-report .fLegendContainer {
  flex-wrap: wrap;
  max-width: 100% !important;
  justify-content: center;
}

.legend.legendsAll {
  text-align: left;
  /* max-width: 700px; */
  text-align: center;
}

.legendBoxNew {
  font-size: 1vw;
  padding-left: 10px;
  padding-right: 10px;
  white-space: nowrap;
  max-width: 350px;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 26px;
}

.icon-dropdown {
  font-size: 12px;
  margin-left: 2px;
  display: inline-block;
}

.more-leg {
  width: 20px;
  height: 20px;
  font-size: 11px;
  text-align: center;
  align-items: center;
  padding-top: 3px;
}

.legendBoxNew.inactive {
  opacity: 0.4;
}
</style>
