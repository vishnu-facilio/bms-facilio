<template>
  <div class="fc-energy-regression-page fc-energy-regression">
    <div class="fc__layout__flexes fc__layout__has__row fc__layout__box">
      <div class="fc__layout__flexes fc__layout__has__row fc__layout__box">
        <!-- main -->
        <div
          class="fc__layout__flexes fc__layout__has__row fc__layout__asset_main p20 height100"
        >
          <div
            v-if="
              (typeof resultObj !== 'undefined' && resultObj) ||
                regressionInfo.length !== 0
            "
            class="fc__layout__flexes fc__main__con__width height100"
          >
            <div
              v-for="(regressionResult, idx) in regressionInfo"
              :key="idx"
              class="fc__asset__main__scroll"
            >
              <div class="white-bg p20 fc__layout__flexes position-relative">
                <div class="fc-black-13 bold text-left">
                  <span class="regression-eq-title">Regression equation -</span
                  ><span class="fc-black-14 fw4">{{
                    regressionResult.expressionString
                      ? regressionResult.expressionString
                      : ''
                  }}</span>
                </div>
                <div>
                  <table class="regression-table">
                    <tr>
                      <td class="regression-table-first-td">
                        {{ 'Y' }}
                      </td>
                      <td>
                        {{ regressionResult.yPoint.name }}
                      </td>
                    </tr>
                    <tr
                      v-for="(dataPoint,
                      dataPointIdx) in regressionResult.dataPoints"
                      :key="dataPointIdx"
                    >
                      <td class="regression-table-first-td">
                        {{ dataPoint.aliases.actual }}
                      </td>
                      <td>
                        {{ dataPoint.name }}
                      </td>
                    </tr>
                  </table>
                </div>
              </div>

              <div class="mT20">
                <div class="fc-grey2 f11 text-uppercase fwBold">
                  Regression Statistics
                </div>
                <div class="white-bg fc__layout__flexes position-relative mT10">
                  <el-row class="p20 border-bottom4">
                    <el-col :span="5">
                      <div class="fc-black-13 bold text-left">
                        Multiple R
                      </div>
                    </el-col>
                    <el-col :span="7">
                      <div class="fc-black-13 text-left">
                        {{
                          Math.sqrt(regressionResult.rsquared).toPrecision(2)
                        }}
                      </div>
                    </el-col>
                    <el-col :span="5">
                      <div class="fc-black-13 bold text-left">
                        Standard Error
                      </div>
                    </el-col>
                    <el-col :span="7">
                      <div class="fc-black-13 text-left">
                        {{ regressionResult.standardError }}
                      </div>
                    </el-col>
                  </el-row>
                  <el-row class="p20 border-bottom4">
                    <el-col :span="5">
                      <div class="fc-black-13 bold text-left">
                        R Square
                      </div>
                    </el-col>
                    <el-col :span="7">
                      <div class="fc-black-13 text-left">
                        {{ regressionResult.rsquared }}
                      </div>
                    </el-col>
                    <el-col :span="5">
                      <div class="fc-black-13 bold text-left">
                        Observations
                      </div>
                    </el-col>
                    <el-col :span="7">
                      <div class="fc-black-13 text-left">
                        {{ regressionResult.observations }}
                      </div>
                    </el-col>
                  </el-row>
                  <el-row class="p20">
                    <el-col :span="5">
                      <div class="fc-black-13 bold text-left">
                        Adjusted R Square
                      </div>
                    </el-col>
                    <el-col :span="7">
                      <div class="fc-black-13 text-left">
                        {{ regressionResult.adjustedrSquared }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </div>

              <div class="mT20">
                <div class="fc-grey2 f11 text-uppercase fwBold">
                  ANOVA
                </div>
                <div
                  class="white-bg fc__layout__flexes position-relative mT10 settings-table-hover-bg-remove"
                >
                  <table class="setting-list-view-table width100">
                    <thead>
                      <tr>
                        <th class="setting-table-th setting-th-text"></th>
                        <th
                          v-for="(anovaColumn, idx2) in anovaResultKeys"
                          :key="idx2"
                          class="setting-table-th setting-th-text"
                        >
                          {{ anovaColumn.displayName }}
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr
                        v-for="(anovaRecord,
                        metricIdx) in regressionResult.anovaMetrics"
                        :key="metricIdx"
                      >
                        <td>
                          <div class="fc-black-13 text-left">
                            {{ stringWithCaps(anovaRecord.resultVariable) }}
                          </div>
                        </td>
                        <td
                          v-for="(metricColumn, columnIdx) in anovaResultKeys"
                          :key="columnIdx"
                        >
                          <div class="fc-black-13 text-left">
                            {{
                              anovaRecord[metricColumn.name] === -1
                                ? ' '
                                : anovaRecord[metricColumn.name]
                            }}
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <div class="mT20">
                <div class="fc-grey2 f11 text-uppercase fwBold">
                  Regression metrics
                </div>
                <div
                  class="white-bg fc__layout__flexes position-relative mT10 settings-table-hover-bg-remove"
                >
                  <table class="setting-list-view-table width100">
                    <thead>
                      <tr>
                        <th class="setting-table-th setting-th-text"></th>
                        <th
                          v-for="(regressionStat,
                          regressionStatIdx) in regressionStatKeys"
                          :key="regressionStatIdx"
                          class="setting-table-th setting-th-text"
                        >
                          {{ regressionStat.displayName }}
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr
                        v-for="(metricRecord,
                        metricRecordIdx) in regressionResult.regressionMetrics"
                        :key="metricRecordIdx"
                      >
                        <td>
                          <div class="fc-black-13 text-left">
                            {{ metricRecord.name }}
                          </div>
                        </td>
                        <td
                          v-for="(stat, statIdx) in regressionStatKeys"
                          :key="statIdx"
                        >
                          <div class="fc-black-13 text-left">
                            {{
                              metricRecord[stat.name] === -1
                                ? 'N/A'
                                : metricRecord[stat.name]
                            }}
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import FNewAnalyticsModelHelper from 'src/pages/report/mixins/FNewAnalyticsModelHelper'
export default {
  watch: {
    resultObj: {
      handler: function(newVal, oldVal) {
        if (typeof this.resultObj !== 'undefined' && this.resultObj) {
          this.prepareRegressionInfo()
        }
      },
      deep: true,
    },
    regressionMetrics: {
      handler: function(newVal, oldVal) {
        if (
          typeof this.regressionMetrics !== 'undefined' &&
          this.regressionMetrics
        ) {
          this.prepareRegressionInfo()
        }
      },
      deep: true,
    },
  },
  data() {
    return {
      regressionMetricKeys: null,
      regressionStatKeys: null,
      anovaResultKeys: null,
      regressionInfo: [],
    }
  },
  props: ['resultObj', 'regressionMetrics', 'analyticsConfig'],
  created() {
    this.regressionMetricKeys = FNewAnalyticsModelHelper.regressionMetrics()
    this.regressionStatKeys = FNewAnalyticsModelHelper.regressionStatistics()
    this.anovaResultKeys = FNewAnalyticsModelHelper.anovaResultKeys()
    if (
      typeof this.regressionMetrics !== 'undefined' &&
      this.regressionMetrics !== null
    ) {
      this.populateRegressionInfo()
    } else if (
      typeof this.resultObj !== 'undefined' &&
      this.resultObj !== null
    ) {
      this.prepareRegressionInfo()
    }
  },
  methods: {
    populateRegressionInfo() {
      this.regressionInfo = this.regressionMetrics
    },
    stringWithCaps(string) {
      let firstChar = string.charAt(0)
      let remaining = string.slice(1)
      return firstChar.toUpperCase() + remaining.toLowerCase()
    },
    prepareRegressionInfo() {
      if (
        typeof this.resultObj !== 'undefined' &&
        this.resultObj !== null &&
        this.resultObj.reportData.data &&
        this.resultObj.reportData.data.length != 0
      ) {
        if (
          this.resultObj.reportData.regressionResult &&
          this.resultObj.reportData.regressionResult.length !== 0
        ) {
          this.regressionInfo = []
          for (let config of this.resultObj.regressionConfig) {
            let key = FNewAnalyticsModelHelper.computeRegressionAlias(config)
            let temp = {}
            temp['alias'] = key
            let result = this.resultObj.reportData.regressionResult[key]
            let regressionDataPoint = this.resultObj.report.dataPoints.filter(
              dp => dp.aliases.actual === key
            )
            if (regressionDataPoint.length) {
              temp['expressionString'] = regressionDataPoint[0].expressionString
            }

            let dataPoints = []
            for (let alias in result.coefficientMap) {
              if (alias !== 'constant') {
                let dataPoint = this.resultObj.report.dataPoints.find(
                  dp => dp.aliases.actual === alias
                )
                if (dataPoint) {
                  dataPoints.push(dataPoint)
                }
              }
            }

            let yDataPoint = this.resultObj.report.dataPoints.find(
              dp => dp.aliases.actual === config.yAxis.alias
            )
            if (yDataPoint) {
              temp['yPoint'] = yDataPoint
            }

            temp['dataPoints'] = dataPoints

            for (let heading of this.regressionMetricKeys) {
              temp[heading.name] = result[heading.name]
            }

            temp['anovaMetrics'] = result['anovaMetrics']

            for (let variableName in result.regressionMetrics) {
              let row = result.regressionMetrics[variableName]
              row['name'] = variableName
            }

            temp['regressionMetrics'] = result.regressionMetrics

            this.regressionInfo.push(temp)
          }
        }
      }
    },
  },
  computed: {},
}
</script>
<style lang="scss">
.fc-energy-regression-page {
  .regression-analysis-chart {
    width: 81% !important;
  }
  .fc__submenu__left {
    width: 272px;
  }
  .fc__layout__asset_main {
    width: 100%;
    left: 10px;
    top: -29px;
  }
  .fc__main__con__width {
    width: 100%;
  }
  .fc__asset__main__scroll {
    height: calc(100vh - 150px);
    padding-right: 0;
    padding-bottom: 80px;
    padding-left: 0 !important;
  }
  .regression-table {
    width: 100%;
    border: solid 1px #cae8ec;
    margin-top: 20px;
    tr {
      border-bottom: solid 1px #cae8ec;
    }
    tr:last-child {
      border-bottom: none;
    }
    td {
      padding-top: 15px;
      padding-bottom: 15px;
      font-size: 14px;
      font-weight: normal;
      line-height: normal;
      letter-spacing: 0.48px;
      color: #333333;
      padding-left: 10px;
      padding-right: 10px;
    }
    .regression-table-first-td {
      background-color: #f0f9fa;
      font-size: 14px;
      font-weight: 500;
      line-height: normal;
      letter-spacing: 0.32px;
      text-align: center;
      border-right: solid 1px #cae8ec;
      color: #39b2c2;
    }
  }
}
.fc-energy-regression .white-bg {
  box-shadow: 0 3px 7px 0 rgba(233, 233, 226, 0.5);
}
.regression-eq-title {
  text-transform: uppercase;
  letter-spacing: 1px;
  font-size: 12px;
  padding-right: 8px;
}
.fc-energy-regression .fc-grey2 {
  letter-spacing: 0.8px !important;
}
</style>
