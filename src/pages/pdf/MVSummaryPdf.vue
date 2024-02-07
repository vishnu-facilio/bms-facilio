<template>
  <div class="height100 scrollable mv-summary-pdf-page">
    <div class="mv-summary-page" v-if="!isLoading">
      <!-- first page -->
      <div class="mv-pdf-container">
        <div class="">
          <div class="mv-content-con">
            <div class="mv-header">
              <div v-show="$org.logoUrl">
                <img :src="$org.logoUrl" style="width: 100px;" />
              </div>
              <div class="fc-black2-18 text-center bold">
                {{ projectName }}
              </div>
              <div class="wo-download-fac-logo">
                <img src="~assets/facilio-logo-black.svg" />
              </div>
            </div>
            <!-- body -->
            <div v-if="mvProject" class="mv-body-container">
              <div class="mv-sum-desc">
                {{ mvProject.description }}
              </div>
              <div class="mT30">
                <el-row>
                  <el-col :span="6">
                    <div class="black-pdf-txt9 bold">Site</div>
                  </el-col>
                  <el-col :span="16">
                    <div class="black-pdf-txt9">
                      {{ getSiteName(mvProject.siteId) }}
                    </div>
                  </el-col>
                </el-row>
                <el-row class="pT20">
                  <el-col :span="6">
                    <div class="black-pdf-txt9 bold">
                      ECM Option Measurement
                    </div>
                  </el-col>
                  <el-col :span="16">
                    <div class="black-pdf-txt9">
                      {{ getECMOptionLabel(mvProject.emcOptions) }}
                    </div>
                  </el-col>
                </el-row>
                <el-row class="pT20">
                  <el-col :span="6">
                    <div class="black-pdf-txt9 bold">Reporting Period</div>
                  </el-col>
                  <el-col :span="16">
                    <div class="black-pdf-txt9">
                      <div class="flex-middle pT5">
                        <div class="black-pdf-txt9">
                          {{
                            mvProject.reportingPeriodStartTime
                              | formatDate(true)
                          }}
                          <span class="mL15 mR15">-</span>
                        </div>
                        <div class="black-pdf-txt9">
                          {{
                            mvProject.reportingPeriodEndTime | formatDate(true)
                          }}
                        </div>
                      </div>
                    </div>
                  </el-col>
                </el-row>
                <el-row class="pT20">
                  <el-col :span="6">
                    <div class="black-pdf-txt9 bold">
                      {{ $t('mv.summary.baselineperiod') }}
                    </div>
                  </el-col>
                  <el-col :span="16">
                    <div class="black-pdf-txt9">
                      <div class="flex-middle pT5">
                        <div class="black-pdf-txt9">
                          {{ mvBaseLine.startTime | formatDate(true) }}
                          <span class="mL15 mR15">-</span>
                        </div>
                        <div class="black-pdf-txt9">
                          {{ mvBaseLine.endTime | formatDate(true) }}
                        </div>
                      </div>
                    </div>
                  </el-col>
                </el-row>
                <el-row class="pT20">
                  <el-col :span="6">
                    <div class="black-pdf-txt9 bold">ECM period</div>
                  </el-col>
                  <el-col :span="16">
                    <div class="flex-middle pT5">
                      <div class="black-pdf-txt9">
                        {{ mvProject.startTime | formatDate(true) }}
                        <span class="mL15 mR15">-</span>
                      </div>
                      <div class="black-pdf-txt9">
                        {{ mvProject.endTime | formatDate(true) }}
                      </div>
                    </div>
                  </el-col>
                </el-row>
                <el-row class="pT20">
                  <el-col :span="6">
                    <div class="black-pdf-txt9 bold">Asset Name</div>
                  </el-col>
                  <el-col :span="16">
                    <div class="black-pdf-txt9">
                      {{ (mvProject.meter || {}).name }}
                    </div>
                  </el-col>
                </el-row>
                <el-row class="pT20">
                  <el-col :span="6">
                    <div class="black-pdf-txt9 bold">Frequency</div>
                  </el-col>
                  <el-col :span="16">
                    <div class="black-pdf-txt9">
                      {{
                        this.$constants.FACILIO_FREQUENCY[mvProject.frequency]
                      }}
                    </div>
                  </el-col>
                </el-row>
                <el-row class="pT20">
                  <el-col :span="6">
                    <div class="black-pdf-txt9 bold">Target Savings</div>
                  </el-col>
                  <el-col :span="16">
                    <div class="black-pdf-txt9">{{ mvProject.saveGoal }}%</div>
                  </el-col>
                </el-row>
                <el-row class="pT20">
                  <el-col :span="6">
                    <div class="black-pdf-txt9 bold">Project owner</div>
                  </el-col>
                  <el-col :span="16">
                    <div class="black-pdf-txt9">
                      {{ getUserName(mvProject.owner) }}
                    </div>
                  </el-col>
                </el-row>

                <!-- baseline equation -->
                <div class="pT30">
                  <div class="black-pdf-txt10 fwBold text-uppercase">
                    {{ $t('mv.summary.baseline_equation') }}

                    <span class="mL15 mR15">-</span>
                    <span
                      v-if="!$validation.isEmpty(mvBaseLine)"
                      class="f11 formula-expression letter-spacing1"
                      >{{
                        mvBaseLine.formulaField.workflow.resultEvaluator
                      }}</span
                    >
                  </div>

                  <div class="">
                    <f-formula-builder
                      v-model="mvBaseLine.formulaField.workflow"
                      module="formulaField"
                      :title="''"
                      :renderInLeft="true"
                      :showOnlyVarible="true"
                    ></f-formula-builder>
                  </div>
                </div>
                <!-- adjustment detailes -->
                <div class="pT30" v-if="adjustment">
                  <div>
                    <el-row class="pB20">
                      <el-col :span="12">
                        <el-row class="flex-middle">
                          <el-col :span="8">
                            <div class="black-pdf-txt10 fwBold">
                              {{ $t('mv.summary.name') }}
                            </div>
                          </el-col>
                          <el-col :span="1">
                            <span class="f9 bold letter-spacing1 energy"
                              >-</span
                            >
                          </el-col>
                          <el-col :span="14">
                            <div class="black-pdf-txt9">
                              {{ adjustment.name }}
                            </div>
                          </el-col>
                        </el-row>
                        <el-row class="pT10 flex-middle">
                          <el-col :span="8">
                            <div class="black-pdf-txt10 fwBold">
                              {{ $t('mv.summary.adjustments_equation') }}
                            </div>
                          </el-col>
                          <el-col :span="1">
                            <span class="f9 bold letter-spacing1 energy"
                              >-</span
                            >
                          </el-col>
                          <el-col :span="14">
                            <div class="black-pdf-txt9">
                              {{
                                adjustment.formulaField.workflow.resultEvaluator
                              }}
                            </div>
                          </el-col>
                        </el-row>
                        <div
                          class="pT10 pB10"
                          v-if="!$validation.isEmpty(adjustment)"
                        >
                          <f-formula-builder
                            v-model="adjustment.formulaField.workflow"
                            module="formulaField"
                            :title="''"
                            :renderInLeft="true"
                            :showOnlyVarible="true"
                          ></f-formula-builder>
                        </div>
                        <el-row class="pT10 flex-middle">
                          <el-col :span="8">
                            <div class="black-pdf-txt10 fwBold">
                              {{ $t('mv.summary.date_range') }}
                            </div>
                          </el-col>
                          <el-col :span="1">
                            <span class="f9 bold letter-spacing1 energy"
                              >-</span
                            >
                          </el-col>
                          <el-col :span="14">
                            <span class="black-pdf-txt9">
                              {{ adjustment.startTime | formatDate(true) }}
                              <span class="mL15 mR15">-</span>
                              {{ adjustment.endTime | formatDate(true) }}
                            </span>
                          </el-col>
                        </el-row>
                      </el-col>
                      <!-- contstants -->
                      <el-col :span="12" class="pL20">
                        <el-row class="flex-middle">
                          <el-col :span="8">
                            <div class="black-pdf-txt10 fwBold">
                              {{ $t('mv.summary.adjustments_constant') }}
                            </div>
                          </el-col>
                          <el-col :span="1">
                            <span class="f9 bold letter-spacing1 energy"
                              >-</span
                            >
                          </el-col>
                          <el-col :span="14">
                            <div class="black-pdf-txt9">
                              {{ adjustment.constant }}
                            </div>
                          </el-col>
                        </el-row>

                        <el-row class="pT10 flex-middle">
                          <el-col :span="8">
                            <div class="black-pdf-txt10 fwBold">
                              {{ $t('mv.summary.name') }}
                            </div>
                          </el-col>
                          <el-col :span="1">
                            <span class="f9 bold letter-spacing1 energy"
                              >-</span
                            >
                          </el-col>
                          <el-col :span="14">
                            <div class="black-pdf-txt9">
                              {{ adjustment.name }}
                            </div>
                          </el-col>
                        </el-row>
                        <el-row class="pT10 flex-middle">
                          <el-col :span="8">
                            <div class="black-pdf-txt10 fwBold">
                              {{ $t('mv.summary.date_range') }}
                            </div>
                          </el-col>
                          <el-col :span="1">
                            <span class="f9 bold letter-spacing1 energy"
                              >-</span
                            >
                          </el-col>
                          <el-col :span="14">
                            <span class="black-pdf-txt9">
                              {{ adjustment.startTime | formatDate(true) }}
                              <span class="mL15 mR15">-</span>
                              {{ adjustment.endTime | formatDate(true) }}
                            </span>
                          </el-col>
                        </el-row>
                      </el-col>
                    </el-row>
                  </div>
                  <!-- <MVAdjustmentsCard :details="mvProjectObj"></MVAdjustmentsCard> -->
                </div>
                <!-- Energy Details -->
                <div class="pT30">
                  <div class="black-pdf-txt10 fwBold text-uppercase">
                    Metrics (Reporting period)
                  </div>
                  <div class="mT10">
                    <el-row class="border-bottom4 border-top1 pT10 pB10">
                      <el-col :span="6">
                        <div class="black-pdf-txt8 bold">Actual Energy</div>
                      </el-col>
                      <el-col :span="6">
                        <div class="black-pdf-txt9">
                          {{
                            mvProjectObj.returnValue.thisMonthActualConsumption
                          }}
                        </div>
                      </el-col>
                      <el-col :span="6" class="border-leftpdf">
                        <div class="black-pdf-txt8 bold">Savings</div>
                      </el-col>
                      <el-col :span="6">
                        <div class="black-pdf-txt9">
                          {{ mvProjectObj.returnValue.costSaved }}
                        </div>
                      </el-col>
                    </el-row>
                    <el-row class="border-bottom4 pT10 pB10">
                      <el-col :span="6">
                        <div class="black-pdf-txt8 bold">
                          {{ $t('mv.summary.baseline_energy') }}
                        </div>
                      </el-col>
                      <el-col :span="6">
                        <div class="black-pdf-txt9">
                          {{
                            mvProjectObj.returnValue
                              .thisMonthBaselineConsumption
                          }}
                        </div>
                      </el-col>
                      <el-col :span="6" class="border-leftpdf">
                        <div class="black-pdf-txt8 bold">Target Savings</div>
                      </el-col>
                      <el-col :span="6">
                        <div class="black-pdf-txt9">
                          {{ mvProjectObj.returnValue.targetCost }}
                        </div>
                      </el-col>
                    </el-row>
                    <el-row class="border-bottom4 pT10 pB10">
                      <el-col :span="6">
                        <div class="black-pdf-txt8 bold">Carbon Savings</div>
                      </el-col>
                      <el-col :span="6">
                        <div class="black-pdf-txt9">
                          {{ mvProjectObj.returnValue.carbonEmissionSaved }}
                          Tons
                        </div>
                      </el-col>
                      <el-col :span="6" class="border-leftpdf">
                        <div class="black-pdf-txt8 bold"></div>
                      </el-col>
                      <el-col :span="6">
                        <div class="black-pdf-txt9"></div>
                      </el-col>
                    </el-row>
                  </div>
                </div>
              </div>
            </div>
            <!-- footer -->
            <!-- <div class="mv-footer">
          <div class="black-pdf-txt9">
            {{projectName}}
          </div>
          <div class="black-pdf-txt9">

          </div>
        </div> -->
          </div>
        </div>
      </div>

      <!-- second page -->
      <div class="mv-pdf-container">
        <div class="">
          <div class="mv-content-con">
            <div class="mv-body-container" style="padding-top: 0;">
              <MVBaselineVsActual
                :details="mvProjectObj"
                class="graph-border chart-container baseline-chart-container"
                @reportLoaded="reportLoaded.push(true)"
              ></MVBaselineVsActual>
              <MVCumulativeSavings
                :details="mvProjectObj"
                class="graph-border chart-container cumulativecard"
                @reportLoaded="reportLoaded.push(true)"
              ></MVCumulativeSavings>
              <MVCostTrend
                :details="mvProjectObj"
                class="graph-border chart-container saving-trend"
                @reportLoaded="reportLoaded.push(true)"
              ></MVCostTrend>
              <MVPercentageSavings
                :details="mvProjectObj"
                class="graph-border chart-container percentage-savings"
                @reportLoaded="reportLoaded.push(true)"
              ></MVPercentageSavings>
            </div>
            <!-- <div class="mv-footer">
          <div class="black-pdf-txt9">
            {{projectName}}
          </div>
          <div class="black-pdf-txt9">

          </div>
        </div> -->
          </div>
        </div>
      </div>

      <!-- third page -->
      <div class="mv-pdf-container baseline-table">
        <div>
          <div class="mv-content-con">
            <div class="mv-body-container" style="padding-top: 0;">
              <MVBaselineEquation
                :details="mvProjectObj"
                class="mv-summary-baseline"
                :hideFilter="true"
                @reportLoaded="reportLoaded.push(true)"
              ></MVBaselineEquation>
            </div>
            <!--
        <div class="mv-footer">
          <div class="black-pdf-txt9">
            {{projectName}}
          </div>
          <div class="black-pdf-txt9">

          </div>
        </div> -->
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
// import Page from '@/page/PageBuilder'
// import Spinner from '@/Spinner'

import MVBaselineVsActual from '@/page/widget/mv/MVBaselineVsActual'
import MVCumulativeSavings from '@/page/widget/mv/MVCumulativeSavings'
import MVCostTrend from '@/page/widget/mv/MVCostTrend'
import MVPercentageSavings from '@/page/widget/mv/MVPercentageSavings'
import MVBaselineEquation from '@/page/widget/mv/MVBaselineEquation'
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
import http from 'util/http'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import { isEmpty } from '@facilio/utils/validation'
import Constants from 'util/constant'
const frequencyHash = {
  1: 'Till yesterday',
  3: 'Till last month',
}
export default {
  props: ['details', 'widget'],
  mixins: [AnalyticsMixin],
  data() {
    return {
      loading: true,
      fields: null,
      moduleName: 'mvproject',
      mvId: this.$route.params.id,
      isLoading: false,
      mvProjectObj: {},
      baseLineList: null,
      analyticsConfig: {
        name: 'Building Analysis',
        key: 'BUILDING_ANALYSIS',
        analyticsType: this.isSiteAnalysis ? 6 : 2,
        hidedatepicker: true,
        hidechartoptions: true,
        point: {
          show: true,
        },
        hidetabular: false,
        hidechart: true,
        hidecharttypechanger: true,
        showWidgetLegends: false,
        axis: {
          y: {
            label: 'SAVINGS',
          },
        },
        type: 'reading',
        period: 12,
        mode: 4,
        baseLine: null,
        dateFilter: {},
        chartViewOption: 0,
        dataPoints: [],
        chartType: 'line',
        predictionTimings: [],
        tableConfig: {
          hideIndex: true,
          removeNegativeValue: true,
        },
        transformWorkflow: {
          parameters: [
            {
              name: 'data',
              typeString: 'list',
            },
            {
              name: 'aggr',
              typeString: 'map',
            },
          ],
          expressions: [
            {
              workflowExpressionType: 2,
              iteratableVariable: 'data',
              loopVariableIndexName: 'index',
              loopVariableValueName: 'value',
              expressions: [],
            },
          ],
        },
      },
      reportLoaded: [],
      detailsLoaded: false,
    }
  },
  components: {
    // Page
    // Spinner
    FFormulaBuilder,
    MVBaselineEquation,
    MVBaselineVsActual,
    MVCumulativeSavings,
    MVCostTrend,
    MVPercentageSavings,
  },
  watch: {
    reportLoaded() {
      this.checkAndPrint()
    },
    detailsLoaded() {
      this.checkAndPrint()
    },
  },
  computed: {
    projectName() {
      let { mvProjectObj } = this
      let { mvProject = {} } = mvProjectObj
      return isEmpty(mvProject) ? '' : mvProject.name
    },
    mvProject() {
      return this.mvProjectObj.mvProject
    },
    mvBaseLine() {
      return this.mvProjectObj.baselines[0]
    },
    workFlow() {
      // let { baseline } = this
      // let { formulaField = {} } = baseline
      // return (formulaField || {}).workflow || {}
      return this.mvProjectObj.adjustments.workFlow
    },
    adjustment() {
      return this.mvProjectObj.adjustments[0]
    },
  },
  created() {
    this.$store.dispatch('loadSite')
    let promises = []
    let { mvId } = this
    let fetchMVDetailsUrl = `/v2/mv/getMVProject?mvProjectId=${mvId}`
    let fetchMVCardDetailsUrl = `/v2/workflow/getDefaultWorkflowResult`
    let fetchMVSubmoduleDetailsUrl =
      '/v2/readings/getSubModuleRel?moduleName=mvproject'
    let params = {
      defaultWorkflowId: 3,
      paramList: [mvId],
    }
    this.isLoading = true
    promises.push(http.get(fetchMVDetailsUrl))
    promises.push(http.post(fetchMVCardDetailsUrl, params))
    promises.push(http.get(fetchMVSubmoduleDetailsUrl))
    Promise.all(promises)
      .then(([mvData, mvCardData, mvSubmodules]) => {
        let {
          data: {
            message: mvDataMessage,
            responseCode: mvDataResponseCode,
            result: mvDataResult,
          },
        } = mvData
        let {
          data: {
            message: mvCardDataMessage,
            responseCode: mvCardDataResponseCode,
            result: mvCardDataResult,
          },
        } = mvCardData
        let {
          data: {
            message: mvSubmodulesMessage,
            responseCode: mvSubmodulesResponseCode,
            result: mvSubmodulesResult,
          },
        } = mvSubmodules
        if (mvDataResponseCode === 0) {
          let { mvProjectWrapper = {} } = mvDataResult
          let {
            mvProject: { frequency },
          } = mvProjectWrapper
          this.mvProjectObj = mvProjectWrapper
          this.mvProjectObj.frequencyLabel = frequencyHash[frequency]
        } else {
          throw new Error(mvDataMessage)
        }
        if (mvCardDataResponseCode === 0) {
          let { workflow = {} } = mvCardDataResult
          let { returnValue = {} } = workflow
          this.mvProjectObj.returnValue = returnValue

          let unit = ' kWh'
          if (returnValue.thisMonthActualConsumption > 10000) {
            returnValue.thisMonthActualConsumption =
              returnValue.thisMonthActualConsumption / 1000
            unit = ' MWh'
          }
          returnValue.thisMonthActualConsumption = this.$d3.format(',.2f')(
            Math.round(returnValue.thisMonthActualConsumption)
          )
          returnValue.thisMonthActualConsumption += unit

          unit = ' kWh'
          if (returnValue.thisMonthBaselineConsumption > 10000) {
            returnValue.thisMonthBaselineConsumption =
              returnValue.thisMonthBaselineConsumption / 1000
            unit = ' MWh'
          }
          returnValue.thisMonthBaselineConsumption = this.$d3.format(',.2f')(
            Math.round(returnValue.thisMonthBaselineConsumption)
          )
          returnValue.thisMonthBaselineConsumption += unit

          returnValue.costSaved = this.$d3.format(',.2f')(
            Math.round(returnValue.costSaved)
          )
          if (this.$currency === '$') {
            returnValue.costSaved = '$ ' + returnValue.costSaved
          } else {
            returnValue.costSaved += ' ' + this.$currency
          }
          returnValue.targetCost = this.$d3.format(',.2f')(
            Math.round(returnValue.targetCost)
          )
          if (this.$currency === '$') {
            returnValue.targetCost = '$ ' + returnValue.targetCost
          } else {
            returnValue.targetCost += ' ' + this.$currency
          }
          returnValue.carbonEmissionSaved = this.$d3.format(',.2f')(
            Math.round(returnValue.carbonEmissionSaved) / 1000
          )
          this.detailsLoaded = true
        } else {
          throw new Error(mvCardDataMessage)
        }
        if (mvSubmodulesResponseCode === 0) {
          this.mvProjectObj.submodules = {}
          mvSubmodulesResult.submodules.forEach(submodule => {
            this.mvProjectObj.submodules[submodule.name] = submodule
          })
        } else {
          throw new Error(mvSubmodulesMessage)
        }
      })
      .catch(({ message }) => {
        this.$message.error(message)
      })
      .finally(() => (this.isLoading = false))
  },
  methods: {
    getECMOptionLabel(value) {
      let ecmMeasurementOptions = Constants.ECM_MEASUREMENT_OPTIONS
      return ecmMeasurementOptions.find(option => option.value === value).label
    },
    getUserName(userObj) {
      if (!isEmpty(userObj)) {
        let { id } = userObj
        let currentUser = this.$store.getters.getUser(id)
        return currentUser.name === 'Unknown' ? '---' : currentUser.name
      }
      return '---'
    },
    getSiteName(siteId) {
      let site = this.$store.getters.getSite(siteId)
      return site.name ? site.name : '---'
    },
    print() {
      if (!this.$route.query.download) {
        setTimeout(() => {
          this.$nextTick(() => {
            window.print()
          })
        }, 1000)
      }
    },
    checkAndPrint() {
      if (this.reportLoaded.length > 4 && this.detailsLoaded) {
        this.print()
      }
    },
  },
}
</script>

<style lang="scss">
.header-sidebar-hide .layout-header {
  display: none;
}

.header-sidebar-hide {
  width: 100%;
  height: 100vh;
  padding-left: 0 !important;
  background: #fff;
  padding-bottom: 50px;
  overflow-y: scroll;
  overflow-x: hidden;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999999;
}

.fc-white-theme .layout-page-container {
  background: #fff;
}

.fc-layout-aside,
.layout-header {
  display: none;
}

.label-txt-black {
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #324056;
  margin: 0;
}

table {
  page-break-after: auto;
}

tr {
  page-break-inside: avoid;
  page-break-after: auto;
}

td {
  page-break-inside: avoid;
  page-break-after: auto;
}

thead {
  display: table-header-group;
}

tfoot {
  display: table-footer-group;
}

body {
  overflow: scroll;
}

.mv-pdf-container {
  width: 100%;
  margin: 50px auto;
  max-width: 980px;
  margin-top: 50px;
  overflow: visible;
  position: relative;
}

.mv-header {
  width: 100%;
  height: 100px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e5e8e9;
  height: 75px;
  padding: 20px;
}

.mv-footer {
  width: 100%;
  position: fixed;
  bottom: 0;
  height: 20px;
  border-top: 1px solid #e5e8e9;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  max-width: 980px;
  padding: 10px 20px 10px;
}

.black-pdf-txt8 {
  font-size: 8px;
  line-height: normal;
  letter-spacing: 0.2px;
  color: #324056;
}

.black-pdf-txt9 {
  font-size: 9px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: 0.26px;
  color: #324056;
}

.black-pdf-txt10 {
  font-size: 10px;
  line-height: normal;
  letter-spacing: 0.26px;
  color: #324056;
}

.mv-body-container {
  padding: 35px 35px 0;
}

.mv-sum-desc {
  font-size: 9px;
  font-weight: normal;
  line-height: 1.78;
  letter-spacing: 0.23px;
  color: #324056;
}

.mv-content-con {
  position: relative;
  height: 100%;
}

.mv-summary-page {
  height: 100%;
  padding-bottom: 100px;
  overflow-y: scroll;
}

.border-leftpdf {
  border-left: 1px solid #f2f5f6;
}
/* .mv-summary-graph-con{
    width: 100%;
    max-width: 980px;
    height: 200px;
    border: solid 1px #eceef1;
    background-color: #ffffff;
    margin-bottom: 50px;
  }
  .mv-summary-graph-con .fc-newchart-container svg{
      width: 980px;
      height: 200px;
  } */
.graph-border .analytics-section {
  max-width: 980px;
  width: 100%;
  border: solid 1px #eceef1;
}
.graph-border .widget-header-2 {
  border-bottom: none;
  padding-bottom: 0;
  padding-left: 0;
}
.mv-summary-pdf-page {
  .mv-energ-cost-comparison .tabular-report-table .tabular-data-td {
    width: 120px !important;
    min-width: 120px !important;
    max-width: 120px !important;
    padding-left: 20px !important;
    padding-right: 20px !important;
    font-size: 8px;
    font-weight: normal;
    line-height: 22px;
    letter-spacing: 0.2px;
    color: #324056 !important;
    white-space: normal !important;
    border-right: none !important;
    border-left: none !important;
    text-align: left !important;
  }
  .mv-energ-cost-comparison .new-analytics-table .tabular-report-table th {
    height: 50px;
    color: #324056 !important;
    font-size: 8px !important;
    border: 1px solid #eceef1 !important;
    border-top: none !important;
    color: #324056 !important;
    font-weight: bold !important;
    white-space: normal !important;
    border-left: none !important;
    border-right: none !important;
    text-align: left !important;
  }
  .mv-energ-cost-comparison
    .new-analytics-table
    .tabular-report-table
    th
    .inline {
    padding-top: 10px !important;
    padding-bottom: 10px !important;
  }
  .tabular-report-table {
    border: none !important;
  }
  .f-singlechart .fc-newchart-container svg {
    height: 450px;
  }
  .fbVariableLegendContainer td.fbVariableLegendInfo {
    font-size: 9px;
    letter-spacing: 0.23px;
    color: #324056;
  }
  .fbVariableLegendContainer td.fbVariableLegend {
    font-size: 12px;
    font-weight: 500;
    line-height: normal;
    letter-spacing: 0.27px;
  }
  .widget-header-2 {
    display: flex;
    align-items: center;
    height: inherit;
    font-size: 10px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.25px;
    color: #324056;
    padding-left: 0;
    padding-right: 0;
  }
  .fbVariableLegendContainer {
    padding-top: 20px;
  }
  .mv-energ-cost-comparison .tablular-container {
    padding-bottom: 0;
    overflow: inherit;
  }
  .mv-energ-cost-comparison .tabular-report-table {
    margin-bottom: 0;
    padding-bottom: 0;
  }
  .mv-energ-cost-comparison .new-analytics-table .scrollable {
    overflow-y: inherit;
  }
  .mv-energ-cost-comparison .new-analytics-table {
    margin-bottom: 0px !important;
    height: 100%;
    overflow-y: inherit;
    padding-bottom: 100px;
  }
  .graph-border {
    height: 100%;
    min-height: 450px;
    position: relative;
  }
}

@media print {
  * {
    background: #ffffff;
  }
  html {
    min-height: 100%;
    text-rendering: optimizeLegibility;
  }
  body {
    overflow: scroll;
    background: #fff;
    display: block;
    width: auto;
    height: auto;
    overflow: visible;
    min-height: 100%;
    text-rendering: optimizeLegibility;
    -webkit-print-color-adjust: exact !important;
  }
  @page {
    size: A4 portrait;
    max-height: 100%;
    max-width: 100%;
  }
  .mv-header {
    padding-top: 20px;
  }
  .mv-summary-pdf-page .graph-border {
    min-height: 100%;
  }
  .mv-summary-pdf-page .f-singlechart .fc-newchart-container svg {
    height: 100%;
  }
  // .cumulativecard{
  //   padding-bottom: 80px;
  // }
  // .saving-trend .widget-header-2{
  //   margin-top: 50px;
  //   padding-top: 50px;
  // }
  .saving-trend {
    padding-top: 50px;
    margin-top: 50px;
  }

  // .baseline-chart-container{
  //   padding-top: 50px;
  // }
  .mv-tabular-chart {
    margin-top: 50px;
    padding-top: 50px;
  }
  .mv-summary-pdf-page .mv-energ-cost-comparison .tablular-container::before {
    content: 'ENERGY CONSUMPTION VS COST';
    display: block;
    position: relative;
    font-size: 10px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.25px;
    color: #324056;
    padding-bottom: 10px;
    padding-top: 10px;
  }
  .mv-pdf-container {
    margin-top: 0;
    page-break-after: always;
    page-break-before: always;
    page-break-inside: always;
    margin-bottom: 0;
  }
  .mv-content-con {
    overflow-y: inherit;
  }
  .mv-pdf-container {
    margin: 0 auto;
  }
  .mv-summary-page {
    height: 100% !important;
    overflow-y: scroll !important;
  }
  // .chart-container{
  //   page-break-before: avoid;
  //    page-break-after: avoid;
  // }
  .baseline-table {
    page-break-inside: avoid;
  }
  .mv-energ-cost-comparison
    .new-analytics-table
    .tabular-report-table
    th
    .inline {
    padding-left: 0 !important;
    padding-right: 0 !important;
  }
  .mv-summary-pdf-page
    .mv-energ-cost-comparison
    .tabular-report-table
    .tabular-data-td {
    padding-left: 10px !important;
    padding-right: 10px !important;
    width: 100px !important;
    min-width: 100px !important;
    max-width: 100px !important;
  }
  .mv-summary-pdf-page .f-singlechart .fc-newchart-container svg {
    zoom: 65%;
  }
  .graph-border .analytics-section {
    border: none !important;
  }
  .mv-summary-baseline .widget-header-2 {
    display: none;
  }
  .mv-summary-pdf-page .legendBoxNew.f-legendnew.active {
    opacity: 1;
    display: block !important;
  }
  .mv-summary-pdf-page .f-chart-mandv-title {
    font-size: 8px;
    padding-top: 0;
  }
  .mv-summary-pdf-page .mv-baselineVsActual .fLegendContainer {
    top: 0;
  }
  .mv-summary-pdf-page .f-new-chart-title {
    // top: -15px;
    top: 10px;
  }
  .fc-newchart-container {
    position: relative;
  }
  .mv-summary-pdf-page .widget-header-2 {
    padding-top: 0;
  }
  .mv-summary-pdf-page .legendBoxNew.f-legendnew,
  .mv-summary-pdf-page .datapoint-leabel,
  .mv-chart-date-picker {
    font-size: 8px !important;
  }
  .mv-summary-pdf-page .fLegendContainer .el-color-picker--mini {
    position: relative;
    top: 12px;
  }
  .mv-summary-pdf-page .fLegendContainer .el-color-picker__color-inner {
    width: 6px !important;
    height: 6px !important;
  }
  .mv-summary-pdf-page .hideLegend {
    display: none !important;
  }
  .percentage-savings,
  .mv-summary-baseline {
    page-break-after: auto;
  }
  .mv-summary-pdf-page .widget-legends {
    padding: 0 !important;
  }
  .mv-summary-pdf-page .baseline-chart-container,
  .mv-summary-pdf-page .saving-trend {
    padding-top: 20px;
  }
  .mv-summary-page {
    padding-bottom: 0 !important;
  }
  // .saving-trend{
  //   margin-top: 20px;
  // }
  .mv-footer {
    display: none;
  }
  .cumulativecard,
  .percentage-savings {
    padding-bottom: 30px;
  }
}
</style>
