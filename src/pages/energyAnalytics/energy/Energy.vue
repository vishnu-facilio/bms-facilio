<template>
  <div>
    <div class="fc-energy-con fc-energy-main-summary">
      <el-header class="fc-energy-header" height="150">
        <div class="flex-middle justify-content-center">
          <div class="mR20">
            <img
              src="~assets/customer-logo/energy-star.jpg"
              width="84"
              height="100"
            />
          </div>
          <div class="flex-grow">
            <div class="fc-black3-20">
              Portfolio - ENERGY STAR <span class="">&#174;</span> Score
            </div>
            <div class="flex-middle pT20">
              <div class="pR30">
                <div class="fc-black-22 line-height30">
                  {{ Math.round(countData.returnValue) }}
                </div>
                <div class="fc-black-13">Average Score</div>
              </div>
              <div class="pL30 border-left3">
                <div class="fc-black-22 line-height30">
                  {{ energySummaryData.length }} of {{ buildingCount }}
                </div>
                <div class="fc-black-13">Building Reporting a Score</div>
              </div>
            </div>
          </div>
          <div class="text-right">
            <el-dropdown v-if="canShowSetupActions" @command="EnergySetup">
              <el-button type="primary" class="setup-el-btn">
                Configure <i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="setupDialog"
                  >Sync from EnergySTAR</el-dropdown-item
                >
                <el-dropdown-item command="pushingDialog"
                  >Push Missing data</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <!-- main container -->
      <el-container>
        <div class="fc-energy-main-con">
          <div class="fc-energy-main-white-bg pB20">
            <el-row>
              <el-col :span="6" class="border-right6">
                <el-header class="pL0 border-bottom12" height="92">
                  <div class="p20">
                    <div class="fc-black-11 bold text-uppercase">
                      BUILDINGS ( {{ energySummaryData.length }} )
                    </div>
                    <div class="fc-grey2 f10 line-height15 pT8">
                      Buildings must have 12 months of Data to receive Energy
                      Star Score.
                    </div>
                  </div>
                </el-header>
                <div v-if="refresh">
                  <el-col :span="24">
                    <div
                      v-if="showLoading"
                      class="flex-middle fc-empty-white justify-content-center scrollable250-y100"
                    >
                      <spinner :show="showLoading" size="80"></spinner>
                    </div>
                    <div
                      v-if="
                        $validation.isEmpty(energySummaryData) && !showLoading
                      "
                      class="flex-middle flex-direction-column fc-empty-white justify-content-center nowo-label scrollable250-y100"
                    >
                      <inline-svg
                        src="svgs/emptystate/reportlist"
                        iconClass="icon text-center icon-xxxlg "
                      ></inline-svg>
                      <div class="nowo-label">
                        No Buildings
                      </div>
                    </div>
                    <div
                      v-if="
                        !showLoading && !$validation.isEmpty(energySummaryData)
                      "
                    >
                      <div
                        class="pointer flex-middle justify-content-space border-bottom12 p20"
                        v-for="building in energySummaryData"
                        :key="building.name"
                        style="height: 80px;"
                      >
                        <div
                          @click="openEnegySummary(building)"
                          class="width100"
                        >
                          <div class="flex-middle justify-content-space pB10">
                            <div class="label-txt-black bold">
                              {{ building.data.building.name }}
                            </div>
                            <div
                              class="flex-middle justify-content-space"
                              v-if="
                                building.data.values[tabActive] &&
                                  building.data.values[tabActive].current
                              "
                            >
                              <div class="fc-blue-txt2-14 text-left">
                                <div v-if="tabActive === 'energyCost'">
                                  <currency
                                    :value="
                                      building.data.values[tabActive].current
                                    "
                                  ></currency>
                                </div>
                                <div v-else>
                                  {{
                                    numberWithCommas(
                                      Math.round(
                                        building.data.values[tabActive].current
                                      )
                                    )
                                  }}
                                  {{ tabunitmap[tabActive] }}
                                </div>
                              </div>
                            </div>
                          </div>

                          <div>
                            <div
                              class="flex-middle justify-content-space"
                              v-if="
                                building.data.values[tabActive] &&
                                  building.data.values[tabActive].baseline
                              "
                            >
                              <div class="fc-black-12 text-left">
                                Baseline
                              </div>
                              <div class="fc-black-12 text-left">
                                <div v-if="tabActive === 'energyCost'">
                                  <currency
                                    :value="
                                      building.data.values[tabActive].baseline
                                    "
                                  ></currency>
                                </div>
                                <div v-else>
                                  {{
                                    numberWithCommas(
                                      Math.round(
                                        building.data.values[tabActive].baseline
                                      )
                                    )
                                  }}
                                  {{ tabunitmap[tabActive] }}
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </el-col>
                </div>
              </el-col>
              <el-col :span="18">
                <div class="">
                  <el-header class="pR0 pL0" height="80">
                    <el-tabs
                      v-model="tabActive"
                      @tab-click="energyTabSwitch"
                      class="pT10 fc-energy-tab"
                      lazy
                    >
                      <el-tab-pane
                        v-for="tab in tabLists"
                        :key="tab.name"
                        :label="tab.displayName"
                        :name="tab.name"
                      >
                        <div class="fc-energy-timeline">
                          <div
                            class="fc-grey6-11"
                            v-for="(xval, ind) of xvalues"
                            :key="ind"
                          >
                            {{ xval }}
                          </div>
                        </div>
                        <div
                          v-if="showLoading"
                          class="flex-middle fc-empty-white justify-content-center scrollable250-y100"
                        >
                          <spinner :show="showLoading" size="80"></spinner>
                        </div>
                        <div
                          v-if="
                            $validation.isEmpty(energySummaryData) &&
                              !showLoading
                          "
                          class="flex-middle nowo-label flex-direction-column scrollable250-y100 fc-empty-white justify-content-center"
                        >
                          <inline-svg
                            src="svgs/emptystate/reportlist"
                            iconClass="icon text-center icon-xxxlg "
                          ></inline-svg>
                          <div class="nowo-label">
                            No Buildings Readings
                          </div>
                        </div>
                        <div
                          v-if="
                            !showLoading &&
                              !$validation.isEmpty(energySummaryData)
                          "
                        >
                          <div
                            class="border-bottom12 flex-middle justify-content-center fc-energy-bar-chart"
                            style="height: 80px;"
                            v-for="building in energySummaryData"
                            :key="building.id"
                          >
                            <f-progress-bar-chart
                              v-if="
                                tabActive === tab.name &&
                                  widgets[building.id + '_' + tabActive]
                              "
                              :fixedChartHeight="null"
                              :isWidget="true"
                              :data="
                                widgets[building.id + '_' + tabActive]
                                  ? widgets[building.id + '_' + tabActive]
                                  : null
                              "
                              :options="widgetOptions"
                              :colors="defaultcolors"
                              :unit="tabunitmap[tabActive]"
                              class="height100"
                            ></f-progress-bar-chart>
                          </div>
                        </div>
                      </el-tab-pane>
                    </el-tabs>
                  </el-header>
                </div>
              </el-col>
            </el-row>
            <div
              class="fc-energy-dot flex-middle justify-content-end width100 pT20 pR20"
            >
              <div class="flex-middle">
                <div class="fc-dot fc-dot-blue"></div>
                <div class="fc-black-12 pL10 bold">
                  Current
                </div>
              </div>
              <div class="flex-middle mL20">
                <div class="fc-dot-dark-blue fc-dot"></div>
                <div class="fc-black-12 pL10 bold">
                  Baseline
                </div>
              </div>
              <div class="flex-middle mL20">
                <div class="fc-dot-red fc-dot"></div>
                <div class="fc-black-12 pL10 bold">
                  National Median
                </div>
              </div>
              <div class="flex-middle mL20">
                <div class="fc-dot-green fc-dot"></div>
                <div class="fc-black-12 pL10 bold">
                  Target
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-container>
    </div>
    <EnergyPushingData
      v-if="showPushingData"
      :showPushingData.sync="showPushingData"
    ></EnergyPushingData>
    <EnergySetup v-if="showDialog" :visibility.sync="showDialog"></EnergySetup>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import FProgressBarChart from 'newcharts/components/FProgressBarChart'
import chartModel from 'newcharts/model/chart-model'
import deepmerge from 'util/deepmerge'
import EnergySetup from 'src/pages/energyAnalytics/energy/EnergySetup'
import EnergyPushingData from 'src/pages/energyAnalytics/energy/EnergyPushingData'
import * as d3 from 'd3'
import {
  isWebTabsEnabled,
  getApp,
  findRouteForTab,
  tabTypes,
} from '@facilio/router'
import { mapState } from 'vuex'

export default {
  data() {
    return {
      tabActive: null,
      tabunitmap: {},
      countData: [],
      energySummaryData: [],
      tabLists: [],
      showLoading: true,
      tab: null,
      buildingCount: 0,
      refresh: true,
      showPushingData: false,
      building: null,
      widgetOptions: {},
      showDialog: false,
      defaultcolors: {
        current: '#a6e1ff',
        baseline: '#1f8bdd',
        median: '#f98484',
        target: '#55d685',
      },
      widgets: {},
      xvalues: [],
    }
  },
  components: {
    FProgressBarChart,
    EnergySetup,
    EnergyPushingData,
  },
  mounted() {
    this.energyCountData()
    this.tabListData()
    this.prepareChartOptions()
  },
  created() {
    this.buildingLoadCount()
  },
  computed: {
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    canShowSetupActions() {
      if (isWebTabsEnabled()) {
        let { linkName } = getApp()
        return linkName !== 'operations'
      } else {
        return true
      }
    },
  },
  methods: {
    async energyCountData() {
      const response = await this.$http.get(
        '/v2/workflow/getDefaultWorkflowResult?defaultWorkflowId=54'
      )
      this.countData = response.data.result.workflow
    },
    async energyListData() {
      this.showLoading = true
      const response = await this.$http.get(
        `/v2/energystar/fetchMainSummaryData?fieldName=${this.tabActive}`
      )
      this.energySummaryData = response.data.result.energyStarPropertiesContext
      this.showLoading = false
      this.widgets = {}
      this.widgetsData = this.prepareWidgetData()
    },
    async tabListData() {
      const response = await this.$http.get('/v2/energystar/getMetricsList')
      this.tabLists = response.data.result.metrics
      this.tabActive = this.tabLists[0].name

      this.tabunitmap = {}
      for (let tab of this.tabLists) {
        this.tabunitmap[tab.name] = tab.field.unit
      }
      this.energyListData()
    },
    energyTabSwitch() {
      this.energyListData()
    },
    findRoute() {
      let { currentTab = {}, $router } = this
      let tabType = tabTypes.CUSTOM
      let config = { type: 'energyStar' }
      let route =
        findRouteForTab(currentTab.id, {
          tabType,
          config,
        }) || {}

      return $router.resolve({ name: route.name }).href
    },
    openEnegySummary(building) {
      if (isWebTabsEnabled()) {
        let path = this.findRoute()
        this.$router.push({
          path: path + '/energy/summary/' + building.id,
        })
      } else {
        this.$router.push({
          path: '/app/en/energy/summary/' + building.id,
        })
      }
    },
    buildingLoadCount() {
      let promises = []
      let buildingsTotalCountUrl =
        'v2/building/list?fetchCount=true&viewName=all'
      promises.push(this.$http.get(buildingsTotalCountUrl))
      Promise.all(promises)
        .then(([buildingCount]) => {
          if (!isEmpty(buildingCount)) {
            let {
              data: { message, responseCode, result = {} },
            } = buildingCount
            if (responseCode === 0) {
              let { recordCount } = result
              this.$set(this, 'buildingCount', recordCount)
            } else {
              throw new Error(message)
            }
          }
        })
        .catch(({ message = 'Error Occurred while fetching Count' }) => {
          this.$message.error(message)
        })
        .finally(() => {
          this.isLoading = false
          return
        })
    },
    prepareWidgetData() {
      let widgetObj = {}
      let maxValArray = []
      this.xvalues = []
      if (this.energySummaryData) {
        for (let value of this.energySummaryData) {
          let details = {}
          if (
            value.data &&
            value.data.values &&
            value.data.values[this.tabActive]
          ) {
            Object.keys(value.data.values[this.tabActive]).forEach(key => {
              if (!['max', 'maxValue'].includes(key)) {
                details[key] = [
                  parseFloat(value.data.values[this.tabActive][key]),
                ]
              } else if (key === 'maxValue') {
                maxValArray.push(
                  parseFloat(value.data.values[this.tabActive][key])
                )
              }
            })
          }
          widgetObj[value.id + '_' + this.tabActive] = details
        }
      }
      if (maxValArray.length) {
        let maxVal = d3.max(maxValArray)

        for (let objKey of Object.keys(widgetObj)) {
          widgetObj[objKey]['x'] = this.findNiceDelta(parseFloat(maxVal), 5)
        }
        this.xvalues = this.findNiceDelta(parseFloat(maxVal), 5)
      }
      this.widgets = widgetObj
    },
    findNiceDelta(maxVal, count) {
      let step = Math.ceil(maxVal) / (count - 1)
      let order = Math.pow(10, Math.floor(Math.log10(step)))
      let delta = step / order

      let stepVal = delta * order

      let stepList = []
      stepList.push(0)
      for (let j = 1; j < count; j++) {
        stepList.push(Math.ceil(j * stepVal))
      }
      return stepList
    },
    prepareChartOptions() {
      let defaultOptions = {}
      let widgetObj = {}
      let customOptions = {
        axis: {
          x: {
            datatype: 'number',
            show: false,
          },
        },
        dataPoints: [],
        regionMultiplier: 0.0015,
        customizeC3: {
          size: {
            height: 20,
          },
        },
      }
      let mergedOptions = deepmerge.objectAssignDeep(
        defaultOptions,
        chartModel.options,
        { style: chartModel.style },
        customOptions
      )
      this.widgetOptions = mergedOptions
    },
    EnergySetup(cmd) {
      if (cmd === 'setupDialog') {
        this.setupDialogOpen()
      } else if (cmd === 'pushingDialog') {
        this.pushingDataOpen()
      }
    },
    setupDialogOpen() {
      this.showDialog = true
    },
    pushingDataOpen() {
      this.showPushingData = true
    },
    numberWithCommas(value) {
      return value.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ',')
    },
  },
}
</script>
