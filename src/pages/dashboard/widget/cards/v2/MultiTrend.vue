<template>
  <div class="multi-trend pT5 dragabale-card fc-widget height-100">
    <div class="pT15 pB15 pL25 title-border">
      <div class="fc-black-color f16 bold letter-spacing0_4 text-left">
        {{ $t('panel.layout.trends') }}
      </div>
    </div>
    <div class="d-flex pL25 pR25 pT15 pB15 title-border">
      <div
        class="f11 fw6 fc-black-color letter-spacing1 column-width-name text-uppercase text-left"
      >
        {{ $t('panel.layout.metrics') }}
      </div>
      <div
        class="f11 fw6 fc-black-color letter-spacing1 column-width-value text-uppercase text-left"
      >
        {{ $t('panel.layout.max') }}
      </div>
      <div
        class="f11 fw6 fc-black-color letter-spacing1 sparkline-trend text-uppercase text-left"
      >
        {{ $t('panel.layout.today') }}
      </div>
    </div>
    <div v-if="isLoading" class="loading-container">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div
      v-else
      v-for="(data, index) in dataParams"
      :key="index"
      class="d-flex pT15 pL25 pR25"
    >
      <div
        class="f13 bold leading-normal letter-spacing0_4 fc-black-color column-width-name text-left d-flex flex-direction-column justify-center"
      >
        {{ data.name }}
      </div>
      <div
        class="f13 fc-black-color column-width-value text-left d-flex flex-direction-column justify-center"
      >
        {{ data.maxValue }}
      </div>
      <div class="sparkline-trend" ref="sparkline-trend">
        <sparkline
          :height="sparkLineStyleObj.height"
          :width="sparkLineStyleObj.width"
          :tooltipProps="data.sparkline.label"
          :aliases="data.unitStr"
        >
          <sparklineCurve
            :data="data.sparkline.dataObj"
            :limit="data.sparkline.dataObj.length"
            :styles="sparkLineStyleObj.style"
            :textStyles="data.sparkline.label"
          />
        </sparkline>
      </div>
    </div>
  </div>
</template>
<script>
import Sparkline from 'newcharts/sparklines/Sparkline'
import formatter from 'charts/helpers/formatter'
import Spinner from '@/Spinner'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  props: ['widget'],
  components: {
    Sparkline,
    Spinner,
  },
  data() {
    return {
      dataParams: [],
      sparkline: {
        labelArray: [],
        unitArray: [],
      },
      isLoading: false,
    }
  },
  computed: {
    sparkLineStyleObj() {
      let { $refs } = this
      let width = 300
      if (!isEmpty($refs)) {
        width = $refs['sparkline-trend'].scrollWidth
      }
      return {
        width,
        height: 35,
        style: {
          stroke: '#886cff',
          fill: '#886cff',
        },
      }
    },
    reportId() {
      let { widget } = this
      let { dataOptions } = widget
      let id = ''
      if (!isEmpty(dataOptions)) {
        let { metaJson } = dataOptions
        let parsedMetaJson = JSON.parse(metaJson)
        let { reportId } = parsedMetaJson
        id = reportId
      }
      return id
    },
  },
  created() {
    let { reportId } = this
    let url = `v3/report/reading/view?reportId=${reportId}&newFormat=true`
    this.isLoading = true

    API.get(url)
      .then(resp => {
        if (resp.error) {
          this.isLoading = false
          this.$message.error('Error while fetching Report Data')
        } else {
          let { reportData, report } = resp.data
          this.isLoading = false
          if (!isEmpty(reportData)) {
            let dataParams = this.constructDataParams(reportData, report)
            this.$set(this, 'dataParams', dataParams)
          }
        }
      })
      .catch(({ message }) => {
        this.isLoading = false
        this.$message.error(message)
      })
  },
  methods: {
    constructDataParams(reportData, report) {
      let self = this
      let dataParams = []
      let { data, aggr } = reportData
      let { dataPoints } = report
      let stateDataPoints = JSON.parse(report.chartState).dataPoints
      if (!isEmpty(data) && !isEmpty(dataPoints)) {
        dataPoints.forEach(dataPoint => {
          let {
            aliases: { actual },
            yAxis: { unitStr },
          } = dataPoint
          let dataObj = {
            sparkline: {},
          }
          unitStr = isEmpty(unitStr) ? '' : unitStr
          dataObj.name = stateDataPoints.find(rt => rt.alias === actual)
            ? stateDataPoints.find(rt => rt.alias === actual).label
            : ''
          dataObj.actual = actual
          dataObj.maxValue = `${aggr[`${actual}.max`]} ${unitStr}`
          dataObj.unitStr = unitStr
          dataObj.sparkline.dataObj = data.map(sparkData =>
            Number(sparkData[actual])
          )
          this.sparkline.labelArray = data.map(sparkData =>
            Number(sparkData['X'])
          )
          dataObj.sparkline.label = {
            formatter(val) {
              let data = `<div style="padding:3px;"><div><label>${formatter.formatCardTime(
                self.sparkline.labelArray[val.index],
                20,
                22
              )}</label></div>`
              data = `${data}<div><label style="color:#fff;font-weight:bold;">${val.value}</label>&nbsp;<label>
                ${val.aliases}</label></div></div>`
              return data
            },
          }
          dataParams.push(dataObj)
        })
      }
      return dataParams
    },
  },
}
</script>
<style lang="scss">
.multi-trend {
  .header,
  .max-value {
    font-size: 1vw;
    letter-spacing: 1.3px;
    text-align: left;
    color: #171619;
    padding-left: 10px;
    text-transform: uppercase;
    text-overflow: ellipsis;
    line-height: 1.1rem;
    padding-top: 10px;
    flex: 0 0 25%;
  }
  .max-value {
    font-weight: bold;
    flex: 0 0 25%;
  }
  .sparkline-trend {
    flex: 0 0 50%;
    overflow: hidden;
  }
  .title {
    font-size: 0.74vw;
    color: #67646c;
    font-weight: 500;
    text-transform: uppercase;
  }
  .column-width-name {
    flex: 0 0 30%;
  }
  .column-width-value {
    flex: 0 0 20%;
  }
  .title-border {
    border-bottom: solid 1px #f0f4f6;
  }
}
</style>
