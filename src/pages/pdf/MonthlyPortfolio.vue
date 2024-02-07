<template>
  <div v-if="isValidatingPermalink">
    Loading...
  </div>
  <div v-else-if="isInvalidPermalink">
    Link expired or invalid.
  </div>
  <div
    v-else
    class="height100 scrollable monthly-portfolio-wrap"
    :class="{
      'monthly-portfolio-wrap': $route.path === 'pdf/monthlyportfolio',
    }"
  >
    <div class="fc-monthly-portfolio-con">
      <div class="fc-monthly-portfolio-header">
        <div class="fc-monthly-portfolio-header-desktop">
          <div class="fL" style="width: 10%;">
            <img src="~assets/spi-logo.svg" />
          </div>

          <div
            class="fc-monthly-portfolio-heading fc-monthly-portfolio-heading-main"
          >
            MONTHLY PORTFOLIO UPDATE
          </div>
          <div class="fR" style="width: 10%;">
            <img src="~assets/facilio-blue-logo.svg" />
          </div>
        </div>
      </div>

      <div class="fc-monthly-portfolio-header">
        <div class="fc-monthly-portfolio-header-mobile">
          <div class="fL">
            <img src="~assets/spi-logo.svg" />
          </div>
          <div class="fR">
            <img src="~assets/facilio-blue-logo.svg" />
          </div>
          <div class="fc-monthly-portfolio-heading clear">
            MONTHLY PORTFOLIO UPDATE
          </div>
        </div>
      </div>
      <div>
        <div
          class="fc-monthly-portfolio-desc fL fc-monthly-portfolio-desc-mobile"
        >
          Here’s your summary of your portfolios for {{ currentMonthName }}. See
          how your organization has performed so far.
        </div>
        <div class="fR mT40 flex mobile-date">
          <new-date-picker
            v-if="dateFilter"
            class="filter-field date-filter-comp-new-report monthly-portfolio-date"
            :dateObj="dateFilter"
            @date="setDateFilter"
            :hidePopover="true"
            :zone="$timezone"
          ></new-date-picker>
          <span class="separator-print">|</span>
          <div class="print-border">
            <img
              src="~assets/printer.svg"
              width="18"
              height="18"
              onclick="window.print()"
              style="cursor: pointer;"
            />
          </div>
        </div>
      </div>

      <div class="fc-monthly-table-con clear">
        <div class="fL chart-heading pB30">
          SPI Overview
        </div>
        <div style="width: 100%; overflow-x: scroll;">
          <table>
            <thead>
              <tr>
                <th style="width: 25%;">Site Name</th>
                <th style="width: 10%;">No of Screens</th>
                <th style="width: 30%;">Team</th>
                <th style="width: 30%;">
                  Jobs<span style="color:#ff3989;padding-left: 5px;"
                    >(Closed/Total)</span
                  >
                </th>
              </tr>
            </thead>
            <tbody v-if="loadingState.workorderCountLoading">
              <tr>
                <td colspan="5" class="tcenter">
                  <spinner :show="loadingState.workorderCountLoading"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                v-for="siteInfo in workOrderCountBySiteArray"
                :key="siteInfo.siteId"
              >
                <td style="width: 25%;">{{ siteInfo.siteName }}</td>
                <td v-if="siteInfo.screenCount == 0" style="width: 10%;">
                  <span class="yet-to-add">No Screens Added</span>
                </td>
                <td v-else style="width: 10%;">{{ siteInfo.screenCount }}</td>
                <td
                  v-if="
                    siteInfo.executiveCount == 0 &&
                      siteInfo.tlCount == 0 &&
                      siteInfo.technicianCount == 0
                  "
                  style="width: 25%;"
                  class="grey-td"
                >
                  <div class="grey-text12">
                    <span class="yet-to-add">No Team Onboarded</span>
                  </div>
                </td>

                <td v-else style="width: 25%;" class="grey-td">
                  <div class="grey-text12">
                    <span v-if="siteInfo.executiveCount > 0">
                      {{ siteInfo.executiveCount }}
                      <span v-if="siteInfo.executiveCount > 1">Executives</span
                      ><span v-else>Executive</span></span
                    >
                    <span v-if="siteInfo.tlCount > 0">
                      / {{ siteInfo.tlCount }}
                      <span v-if="siteInfo.tlCount > 1">TLs</span
                      ><span v-else>TL</span></span
                    >
                    <span v-if="siteInfo.technicianCount > 0">
                      / {{ siteInfo.technicianCount }}
                      <span v-if="siteInfo.technicianCount > 0"
                        >Technicians</span
                      ><span v-else>Technician</span>
                    </span>
                  </div>
                </td>
                <td v-if="siteInfo.totalCount == 0" style="width: 25%;">
                  <span class="yet-to-add">No Jobs Created</span>
                </td>
                <td v-else style="width: 25%;">
                  <div style="float: left;width: 100px;">
                    {{ siteInfo.closedCount }}/{{ siteInfo.totalCount }}
                  </div>
                  <div style="float: left: width: 100px;">
                    <span
                      v-if="
                        calculatePercentage(
                          siteInfo.closedCount,
                          siteInfo.totalCount
                        ) > 50
                      "
                      style="margin-left: 20px;color:green;"
                      >{{
                        calculatePercentage(
                          siteInfo.closedCount,
                          siteInfo.totalCount
                        )
                      }}
                      %</span
                    >
                    <span v-else style="margin-left: 20px;color:red"
                      >{{
                        calculatePercentage(
                          siteInfo.closedCount,
                          siteInfo.totalCount
                        )
                      }}
                      %</span
                    >
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <!-- table end-->
    </div>
    <div class="fc-monthly-portfolio-con pT0">
      <div class="fc-monthly-chart-con clear">
        <div class="fL chart-heading">
          SLA Performance Across Sites
        </div>
        <div class="fR"></div>
        <div
          class="chart-container tcenter"
          v-if="loadingState.workorderStatusLoading"
        >
          <spinner :show="loadingState.workorderStatusLoading"></spinner>
        </div>
        <div class="chart-container" v-else>
          <div>
            <f-new-chart
              ref="newChart"
              width="1200"
              :fixedChartHeight="
                innerWidth < 768 ? graphDataObj.x.length * 50 : null
              "
              :isWidget="true"
              :clientWidth="true"
              :data="graphDataObj"
              :options="graphOptionsObj"
              hidecharttypechanger="true"
              class="monthly-portfolio-legends"
            ></f-new-chart>
          </div>
        </div>
      </div>
    </div>
    <!-- third container -->
    <div class="fc-monthly-bg2 pB0 pT0">
      <div class="fc-monthly-portfolio-con pT40">
        <div class="fL chart-heading">
          Average Work Completion Time
        </div>
        <div class="fR">
          <el-dropdown @command="openResolutionTimeForSite" class="pointer">
            <span class="el-dropdown-link">
              {{ siteName }}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item :command="null">All Sites</el-dropdown-item>
              <el-dropdown-item
                v-for="siteInfo in avgResponseResolutionBySiteArray"
                :key="siteInfo.siteId"
                :command="siteInfo"
                >{{ siteInfo.siteName }}</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
        </div>
        <!-- chart container -->
        <div
          class="fc-monthly-portfolio-header clear mT50 tcenter"
          v-if="loadingState.avgResTimeCountLoading"
        >
          <div
            style="    min-height: 415px; background: rgb(255, 255, 255); width: 100%; padding: 10%;"
            class="tcenter"
          >
            <spinner :show="loadingState.avgResTimeCountLoading"></spinner>
          </div>
        </div>
        <div class="fc-monthly-portfolio-header clear mT50" v-else>
          <f-new-chart
            ref="newChart"
            :clientWidth="true"
            class="spi-categorywise-chart"
            :width="350"
            :height="415"
            :isWidget="true"
            :data="graphByCategoryArrayObj.data"
            :options="graphByCategoryArrayObj.options"
            hidecharttypechanger="true"
          ></f-new-chart>
        </div>
      </div>
    </div>
    <!-- fourth container -->
    <div class="fc-monthly-bg2 pT0">
      <div class="fc-monthly-portfolio-con pT0 pB0">
        <div class="fL chart-heading pB30">
          Technicians with highest SLA Compliance
        </div>
        <!-- chart container -->
        <div
          class="fc-monthly-portfolio-header clear mT50 tcenter"
          v-if="loadingState.topNLoading"
        >
          <div
            style="    min-height: 415px; background: rgb(255, 255, 255); width: 100%; padding: 10%;"
            class="tcenter"
          >
            <spinner :show="loadingState.topNLoading"></spinner>
          </div>
        </div>
        <div
          class="fc-monthly-portfolio-header clear mT50"
          v-else-if="graphByTechniciansArrayObj"
        >
          <f-new-chart
            ref="newChart"
            :clientWidth="true"
            class="spi-categorywise-chart top10-tech-report"
            :width="350"
            :height="415"
            :isWidget="true"
            :data="graphByTechniciansArrayObj.data"
            :options="graphByTechniciansArrayObj.options"
            hidecharttypechanger="true"
          ></f-new-chart>
        </div>
      </div>
    </div>

    <!-- fifth container -->
    <div class="fc-monthly-portfolio-con pT0 pB0">
      <div class="fc-monthly-table-con pB30 mT0">
        <div class="fL chart-heading pB30">
          Operational Performance Across sites
        </div>
        <div style="width: 100%; overflow-x: scroll;">
          <table>
            <thead>
              <tr>
                <th style="width: 20%;">Site Name</th>
                <th style="width: 10%;">Technicians Count</th>
                <th style="width: 20%;">Job Status</th>
                <th style="width: 20%;">Avg Work Completion Time</th>
                <th style="width: 20%;">Avg Response Time</th>
              </tr>
            </thead>
            <tbody v-if="loadingState.avgResponseTimeCountLoading">
              <tr>
                <td colspan="5" class="tcenter">
                  <spinner
                    :show="loadingState.avgResponseTimeCountLoading"
                  ></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                v-for="siteInfo in avgResponseResolutionBySiteArray"
                :key="siteInfo.siteId"
              >
                <td style="width: 20%;">{{ siteInfo.siteName }}</td>
                <td style="width: 10%;">{{ siteInfo.technicianCount }}</td>
                <!-- <td style="width: 20%;">Open - {{ siteInfo.open }} OnTime - {{ siteInfo.onTime }} OverDue - {{ siteInfo.overDue }}</td> -->
                <td style="width: 20%;">
                  <el-popover placement="top-start" width="200" trigger="hover">
                    <div
                      style="font-size: 14px; font-weight: bold;letter-spacing: 0.4px; text-align: left; color: #324056;"
                    >
                      {{ siteInfo.siteName }}
                    </div>
                    <div
                      style="display: flex;align-items: center;justify-content: space-between;padding-top: 10px;"
                    >
                      <div
                        style="font-size: 13px;font-weight: normal; text-align: left; letter-spacing: 0.3px; color: #486372;"
                      >
                        On Time
                      </div>
                      <div
                        style="font-size: 20px;text-align: left; font-weight: normal;letter-spacing: 0.2px; text-align: right; color: #39c2b0;"
                      >
                        {{ siteInfo.onTime }}
                      </div>
                    </div>
                    <div
                      style="display: flex;align-items: center;justify-content: space-between;"
                    >
                      <div
                        style="font-size: 13px;font-weight: normal; text-align: left; letter-spacing: 0.3px; color: #486372;"
                      >
                        Overdue
                      </div>
                      <div
                        style="font-size: 20px;text-align: left; font-weight: normal;letter-spacing: 0.2px; text-align: right; color: #f08532;"
                      >
                        {{ siteInfo.overDue }}
                      </div>
                    </div>
                    <div
                      style="display: flex;align-items: center;justify-content: space-between;"
                    >
                      <div
                        style="font-size: 13px;font-weight: normal; text-align: left; letter-spacing: 0.3px; color: #486372;"
                      >
                        Open
                      </div>
                      <div
                        style="font-size: 20px;text-align: left; font-weight: normal;letter-spacing: 0.2px; text-align: right; color: #6974FA;"
                      >
                        {{ siteInfo.open }}
                      </div>
                    </div>
                    <div class="progress progress-stacked" slot="reference">
                      <span
                        :style="
                          'width:' +
                            [
                              (siteInfo.onTime * 100) /
                                (siteInfo.onTime +
                                  siteInfo.open +
                                  siteInfo.overDue) +
                                '%',
                            ]
                        "
                        class="bg-green"
                      ></span>
                      <span
                        :style="
                          'width:' +
                            [
                              (siteInfo.overDue * 100) /
                                (siteInfo.onTime +
                                  siteInfo.open +
                                  siteInfo.overDue) +
                                '%',
                            ]
                        "
                        class="bg-orange"
                      ></span>
                    </div>
                  </el-popover>
                </td>
                <td>
                  <div>
                    <img
                      v-if="
                        siteInfo.avgResolutionTime <
                          siteInfo.avgResolutionTimeTillLastMonth
                      "
                      src="~assets/download2-green.svg"
                      style="width: 12px;height: 12px;"
                    /><img
                      v-if="
                        siteInfo.avgResolutionTime >
                          siteInfo.avgResolutionTimeTillLastMonth
                      "
                      src="~assets/download2-red.svg"
                      style="width: 12px;height: 12px;-ms-transform: rotate(-180deg); -webkit-transform: rotate(-180deg);transform: rotate(-180deg);"
                    /><span
                      v-if="siteInfo.avgResolutionTime < 60"
                      class="hour-text-13"
                      >{{ siteInfo.avgResolutionTime }} Minutes</span
                    ><span v-else class="hour-text-13"
                      >{{
                        (siteInfo.avgResolutionTime / 60).toFixed(2)
                      }}
                      Hours</span
                    >
                  </div>
                  <div
                    v-if="siteInfo.avgResolutionTimeTillLastMonth < 60"
                    class="grey-11"
                  >
                    {{ siteInfo.avgResolutionTimeTillLastMonth }} mins in
                    {{ previousMonthName }}
                  </div>
                  <div v-else class="grey-11">
                    {{
                      (siteInfo.avgResolutionTimeTillLastMonth / 60).toFixed(2)
                    }}
                    Hours in {{ previousMonthName }}
                  </div>
                </td>
                <td style="width: 20%;">
                  <div>
                    <img
                      v-if="
                        siteInfo.avgResponseTime <
                          siteInfo.avgResponseTimeTillLastMonth
                      "
                      src="~assets/download2-green.svg"
                      style="width: 12px;height: 12px;"
                    /><img
                      v-if="
                        siteInfo.avgResponseTime >
                          siteInfo.avgResponseTimeTillLastMonth
                      "
                      src="~assets/download2-red.svg"
                      style="width: 12px;height: 12px;-ms-transform: rotate(-180deg); -webkit-transform: rotate(-180deg);transform: rotate(-180deg);"
                    />
                    <span
                      v-if="siteInfo.avgResponseTime < 60"
                      class="hour-text-13"
                      >{{ siteInfo.avgResponseTime }} Minutes</span
                    ><span v-else class="hour-text-13"
                      >{{
                        (siteInfo.avgResponseTime / 60).toFixed(2)
                      }}
                      Hours</span
                    >
                  </div>
                  <div
                    v-if="siteInfo.avgResponseTimeTillLastMonth < 60"
                    class="grey-11"
                  >
                    {{ siteInfo.avgResponseTimeTillLastMonth }} mins in
                    {{ previousMonthName }}
                  </div>
                  <div v-else class="grey-11">
                    {{
                      (siteInfo.avgResponseTimeTillLastMonth / 60).toFixed(2)
                    }}
                    Hours in {{ previousMonthName }}
                  </div>
                </td>
                <!-- <td style="width: 20%;">{{ siteInfo.avgResponseTime }}/{{ siteInfo.avgResponseTimeTillLastMonth }} till last month</td> -->
                <!-- <td style="width: 20%;">{{ siteInfo.avgResolutionTime }}/{{ siteInfo.avgResolutionTimeTillLastMonth }} till last month</td> -->
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <!-- table end-->

    <!-- sixth container -->
    <div class="fc-monthly-portfolio-con fc-monthly-portfolio-card-con pT0">
      <div class="fL chart-heading pT0 pB30">
        Consumption metrics
      </div>
      <div
        class="fc-card-container fc-monthly-portfolio-header clear fc-energy-container"
      >
        <div
          v-for="(data, index) in totalConsumptionBySiteArray"
          :key="index"
          class="fc-card-block"
          v-if="data.siteName"
        >
          <div class="fL">
            <img src="~statics/space/campus.svg" class="card-img" />
          </div>
          <div class="fR">
            <div class="card-heading" :title="data.siteName">
              {{ data.siteName }}
            </div>
            <div class="card-day-txt">{{ currentMonthName }}</div>
            <div class="card-reading-txt">
              <span class="reading-cons-txt">Energy </span
              ><span v-if="data.energyConsumption > 0"
                ><span v-if="data.energyConsumption > 1000">{{
                  (data.energyConsumption * 0.001).toFixed(2)
                }}</span
                ><span v-else>{{ data.energyConsumption }}</span></span
              ><span v-else> -- </span>
              <span v-if="data.energyConsumption > 0" class="card-reading-txt2"
                ><span v-if="data.energyConsumption > 1000">MWh</span
                ><span v-else>kWh</span></span
              >
            </div>
            <div class="card-reading-txt">
              <span class="reading-cons-txt">Water </span
              ><span
                v-if="data.waterConsumption > 0"
                style="margin-left: 7px;"
                >{{ data.waterConsumption }}</span
              ><span v-else style="margin-left: 7px;"> -- </span>
              <span
                v-if="data.waterConsumption > 0"
                class="card-reading-txt2"
                >{{ totalWaterConsumptionUnit }}</span
              >
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import http from 'util/http'
import Util from 'util'
import Vue from 'vue'
import FNewChart from 'newcharts/components/FNewChart'
import chartModel from 'newcharts/model/chart-model'
import deepmerge from 'util/deepmerge'
import NewDatePicker from '@/NewDatePicker'
import NewDateHelper from '@/mixins/NewDateHelper'
import moment from 'moment-timezone'
export default {
  components: {
    FNewChart,
    NewDatePicker,
  },
  data() {
    return {
      isValidatingPermalink: true,
      isInvalidPermalink: false,
      workOrderStatusPercentageResp: '',
      tabularDataArray: [],
      workOrderCountBySiteArray: [],
      resolutionTimeByCategoryArray: [],
      reportObj: null,
      resultObj: null,
      graphDataObj: null,
      graphOptionsObj: null,
      graphByCategoryArrayObj: null,
      graphByTechniciansArrayObj: null,
      categoryGraphRowCount: 0,
      graphRenderCountIndex: -1,
      totalEnergyConsumptionUnit: '',
      totalConsumptionBySiteArray: [],
      avgResponseResolutionBySiteArray: [],
      totalWaterConsumptionUnit: '',
      dateFilter: null,
      userFilterApplied: null,
      startTime: null,
      siteId: null,
      siteName: 'All Sites',
      endTime: null,
      dateFilterString: '',
      loadingState: {
        totalConsumptionLoading: true,
        workorderStatusLoading: true,
        workorderCountLoading: true,
        avgResTimeCountLoading: true,
        avgResponseTimeCountLoading: true,
        topNLoading: true,
      },
    }
  },
  methods: {
    validatePermalink() {
      this.isValidatingPermalink = true
      if (!this.permalink) {
        this.isValidatingPermalink = false
        this.isInvalidPermalink = true
      }

      let self = this
      this.isValidatingPermalink = true
      http
        .get('/validatePermalink', { params: { permalink: this.permalink } })
        .then(function(response) {
          self.isValidatingPermalink = false
          if (response.status === 200 && response.data.account) {
            self.isInvalidPermalink = false
            http.defaults.headers.common['X-Permalink-Token'] = self.permalink
            axios.defaults.headers.common['X-Permalink-Token'] = self.permalink

            Vue.use(Util, { account: response.data.account })

            self.initChart()
          } else {
            self.isInvalidPermalink = true
          }
        })
        .catch(function(error) {
          console.log('######### error', error)
          if (error) {
            self.isValidatingPermalink = false
            self.isInvalidPermalink = true
          }
        })
    },
    getWorkOrderStatusPercentage() {
      let self = this
      self.loadingState.workorderStatusLoading = true
      http
        .get(
          '/v2/workorderrequests/workOrderStatusPercentage?startTime=' +
            this.startTime +
            '&endTime=' +
            this.endTime
        )
        .then(function(response) {
          if (response.status === 200) {
            self.tabularDataArray = response.data.woStatusPercentage.tabularData
            self.graphDataObj = response.data.woStatusPercentage.graphData.data
            let optionsFromServer =
              response.data.woStatusPercentage.graphData.options
            optionsFromServer.general = {
              normalizeStack: true,
            }
            let defaultOptions = {}
            let mergedOptions = deepmerge.objectAssignDeep(
              defaultOptions,
              chartModel.options,
              { style: chartModel.style },
              optionsFromServer
            )
            mergedOptions.axis.x.label.text = ''
            mergedOptions.axis.y.label.text = ''
            mergedOptions.axis.y.ticks.count = 6
            mergedOptions.axis.y.format.unit = '%'
            mergedOptions.padding = {
              top: 0,
              left: 160,
              right: 15,
              bottom: 0,
            }
            mergedOptions.customizeC3 = {
              data: {
                order: function(d1, d2) {
                  let indexMap = { ontime: 1, overdue: 2, open: 3 }
                  if (indexMap[d1.id] < indexMap[d2.id]) {
                    return -1
                  }
                  if (indexMap[d1.id] > indexMap[d2.id]) {
                    return 1
                  }
                  return 0
                },
              },
              axis: {
                x: {
                  tick: {
                    multiline: false,
                  },
                },
              },
            }
            if (window.innerWidth < 768) {
              mergedOptions.padding.left = 20
              mergedOptions.customizeC3.axis.x.clipPath = false
              mergedOptions.customizeC3.axis.x.inner = false
              mergedOptions.customizeC3.bar = {
                width: {
                  ratio: 0.4,
                  max: 50,
                },
              }
              mergedOptions.customizeC3.axis.x.tick.text = {
                position: {
                  x: 10,
                  y: -18,
                },
              }
            }
            self.graphOptionsObj = mergedOptions
            self.loadingState.workorderStatusLoading = false
          } else {
            alert(JSON.stringify(response))
          }
        })
        .catch(function(error) {
          self.loadingState.workorderStatusLoading = false
          console.log(error)
        })
    },
    setDateFilter(dateFilter) {
      console.log(dateFilter)
      this.dateFilter = dateFilter

      this.initChart()
    },

    getAvgResolutionTimeByCategory() {
      let self = this
      console.log(self.siteId)
      self.loadingState.avgResTimeCountLoading = true
      let url =
        '/v2/workorderrequests/avgWorkCompletionByCategory?startTime=' +
        this.startTime +
        '&endTime=' +
        this.endTime
      if (self.siteId != null) {
        console.log('hi')
        url = url + '&siteId=' + self.siteId
      }
      http
        .get(url)
        .then(function(response) {
          if (response.status === 200) {
            self.$emit('close', true)
            let graphData = response.data.avgResolutionTimeByCategory.graphData
            JSON.stringify(response)
            let inputCategory = {}
            inputCategory.data = graphData.data
            let defaultOptions = {}
            inputCategory.options = deepmerge.objectAssignDeep(
              defaultOptions,
              chartModel.options,
              { style: chartModel.style },
              graphData.options
            )
            inputCategory.options.axis.x.label.text = 'Category'
            inputCategory.options.axis.y.label.text = 'MINUTES'
            inputCategory.options.dataPoints[0].key = 'y'
            inputCategory.options.dataPoints[0].label =
              'Avg Work Completion Time'
            inputCategory.options.dataPoints[0].unitStr = 'Mins'
            inputCategory.options.dataPoints[0].color = '#cc6bda'
            inputCategory.options.padding = {
              left: 70,
              right: 10,
              top: 30,
            }
            inputCategory.options.legend.show = false
            inputCategory.options.customizeC3 = {
              axis: {
                x: {
                  tick: {
                    multiline: true,
                  },
                },
              },
            }
            self.graphByCategoryArrayObj = self.applyMobileSetting(
              inputCategory
            )
            self.loadingState.avgResTimeCountLoading = false
          } else {
            alert(JSON.stringify(response))
          }
          self.$emit('close', true)
        })
        .catch(function(error) {
          self.loadingState.avgResTimeCountLoading = false
          console.log(error)
        })
    },
    getTopNTechnicians() {
      let self = this
      self.loadingState.topNLoading = true
      http
        .get(
          '/v2/workorderrequests/topNTechnicians?startTime=' +
            this.startTime +
            '&endTime=' +
            this.endTime +
            '&recordCount=10'
        )
        .then(function(response) {
          if (response.status === 200) {
            self.$emit('close', true)
            let graphData = response.data.topTechnicians.graphData
            JSON.stringify(response)
            let inputCategory = {}
            inputCategory.data = graphData.data
            let defaultOptions = {}
            inputCategory.options = deepmerge.objectAssignDeep(
              defaultOptions,
              chartModel.options,
              { style: chartModel.style },
              graphData.options
            )
            inputCategory.options.dataPoints[0].key = 'y'
            inputCategory.options.dataPoints[0].label =
              'Ontime Closed WorkOrders'
            inputCategory.options.dataPoints[0].unitStr = ''
            inputCategory.options.dataPoints[0].color = '#6A74F9'
            inputCategory.options.axis.y.label.text = 'ONTIME CLOSED WORKORDERS'
            inputCategory.options.padding = {
              left: 70,
              right: 10,
              top: 30,
            }
            inputCategory.options.legend.show = false
            inputCategory.options.customizeC3 = {
              axis: {
                x: {
                  tick: {
                    multiline: true,
                  },
                },
              },
            }
            self.graphByTechniciansArrayObj = self.applyMobileSetting(
              inputCategory
            )
            self.loadingState.topNLoading = false
          } else {
            alert(JSON.stringify(response))
          }
          self.$emit('close', true)
        })
        .catch(function(error) {
          self.loadingState.topNLoading = false
          console.log(error)
        })
    },

    initChart() {
      if (!this.dateFilter) {
        if (this.$route.query.startDate && this.$route.query.endDate) {
          let dateFilterString =
            this.$route.query.startDate + ',' + this.$route.query.endDate
          this.dateFilter = NewDateHelper.getDatePickerObject(
            64,
            dateFilterString
          )
        } else {
          this.dateFilter = NewDateHelper.getDatePickerObject(28)
        }
      }

      this.startTime = this.dateFilter.value[0]
      this.endTime = this.dateFilter.value[1]

      this.getTotalConsumptionBySite()
      this.getWorkOrderStatusPercentage()
      this.getWorkOrderCountBySite()
      this.getAvgResolutionTimeByCategory()
      this.getAvgResponseResolutionTimeBySite()
      this.getTopNTechnicians()
    },
    getTotalConsumptionBySite() {
      let self = this
      self.loadingState.totalConsumptionLoading = true
      http
        .get(
          '/v2/consumptionData/totalConsumptionBySite?startTime=' +
            this.startTime +
            '&endTime=' +
            this.endTime
        )
        .then(function(response) {
          if (response.status === 200) {
            self.$emit('close', true)
            self.totalConsumptionBySiteArray =
              response.data.totalConsumptionBySite.total_consumption
            self.totalEnergyConsumptionUnit =
              response.data.totalConsumptionBySite.energyUnit
            self.totalWaterConsumptionUnit = response.data
              .totalConsumptionBySite.waterUnit
              ? response.data.totalConsumptionBySite.waterUnit
              : 'Litres'
            JSON.stringify(response)
            self.loadingState.totalConsumptionLoading = false
          } else {
            alert(JSON.stringify(response))
          }
          self.$emit('close', true)
        })
        .catch(function(error) {
          self.loadingState.totalConsumptionLoading = false
          console.log(error)
        })
    },
    getWorkOrderCountBySite() {
      let self = this
      self.loadingState.workorderCountLoading = true
      http
        .get(
          '/v2/workorderrequests/woCountBySite?startTime=' +
            this.startTime +
            '&endTime=' +
            this.endTime
        )
        .then(function(response) {
          if (response.status === 200) {
            self.$emit('close', true)
            self.workOrderCountBySiteArray = response.data.workOrderBySite
            JSON.stringify(response)
            self.loadingState.workorderCountLoading = false
          } else {
            alert(JSON.stringify(response))
          }
          self.$emit('close', true)
        })
        .catch(function(error) {
          self.loadingState.workorderCountLoading = false
          console.log(error)
        })
    },
    getAvgResponseResolutionTimeBySite() {
      let self = this
      self.loadingState.avgResponseTimeCountLoading = true
      http
        .get(
          '/v2/workorderrequests/avgResolutionResponseTimeBySite?startTime=' +
            this.startTime +
            '&endTime=' +
            this.endTime
        )
        .then(function(response) {
          if (response.status === 200) {
            self.$emit('close', true)
            self.avgResponseResolutionBySiteArray =
              response.data.avgResponseResolution
            JSON.stringify(response)
            self.loadingState.avgResponseTimeCountLoading = false
          } else {
            alert(JSON.stringify(response))
          }
          self.$emit('close', true)
        })
        .catch(function(error) {
          self.loadingState.avgResponseTimeCountLoading = false
          console.log(error)
        })
    },
    calculatePercentage(closed, total) {
      return ((closed * 100) / total).toFixed(1)
    },
    openResolutionTimeForSite(obj) {
      if (obj == null) {
        this.siteId = null
        this.siteName = 'All Sites'
      } else {
        this.siteId = obj.siteId
        this.siteName = obj.siteName
      }
      this.getAvgResolutionTimeByCategory()
    },
    getTotalWaterConsumptionByBuilding() {
      let self = this
      http
        .get(
          '/v2/consumptionData/totalConsumptionByBuilding?moduleName=waterreading&fieldName=waterConsumptionDelta&startTime=' +
            this.startTime +
            '&endTime=' +
            this.endTime
        )
        .then(function(response) {
          if (response.status === 200) {
            self.$emit('close', true)
            self.totalWaterConsumptionByBuildingArray =
              response.data.totalConsumptionByBuilding.totalConsumptionData
            if (response.data.totalConsumptionByBuilding.unit == null) {
              self.totalWaterConsumptionUnit = 'Litres'
            } else {
              self.totalWaterConsumptionUnit =
                response.data.totalConsumptionByBuilding.unit
            }
            JSON.stringify(response)
          } else {
            alert(JSON.stringify(response))
          }
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    applyMobileSetting(chartOptions) {
      if (window.innerWidth < 768) {
        chartOptions.options.axis.x.tick.direction = 'vertical'
        chartOptions.options.axis.x.label.text = ''
        chartOptions.options.axis.x.tick.tooltip = true
        chartOptions.options.padding.bottom = 40
        chartOptions.options.customizeC3.axis.x.tick.multiline = false
      }
      return chartOptions
    },
  },
  computed: {
    innerWidth() {
      return window.innerWidth
    },
    permalink() {
      return this.$route.query.token
    },
    currentMonthName() {
      if (this.dateFilter) {
        return moment(this.dateFilter.value[0])
          .tz(this.$timezone)
          .format('MMMM YYYY')
      }
      return ''
    },
    previousMonthName() {
      if (this.dateFilter) {
        let monthMinusOneName = moment(this.dateFilter.value[0])
          .tz(this.$timezone)
          .subtract(1, 'month')
          .startOf('month')
          .format('MMMM')
        return monthMinusOneName
      }
      return ''
    },
    noOfDays() {
      if (this.dateFilter) {
        return moment(this.dateFilter.value[0])
          .tz(this.$timezone)
          .daysInMonth()
      }
      return '30'
    },
    appliedDateRange() {
      if (this.$route.query.daterange) {
        return JSON.parse(this.$route.query.daterange)
      }
    },
    appliedChartType() {
      // Temp...will be changed for pdf once temp report support is implemented
      return this.$route.query.charttype
    },
  },
  mounted() {
    this.validatePermalink()
  },
}
</script>

<style>
body {
  position: relative;
  height: 100%;
  overflow: hidden;
  font-size: 14px;
}
#q-app {
  height: 100vh;
  overflow: scroll;
}
.fL {
  float: left;
}
.fR {
  float: right;
}
.clear {
  clear: both;
}
.mT0 {
  margin-top: 0;
}
.mT50 {
  margin-top: 50px;
}
.pT40 {
  padding-top: 40px !important;
}
.mT40 {
  margin-top: 40px;
}
.pB30 {
  padding-bottom: 30px !important;
}
.pT0 {
  padding-top: 0 !important;
}
.pB0 {
  padding-bottom: 0 !important;
}
.pT50 {
  padding-top: 50px;
}
.tcenter {
  text-align: center;
}
.pointer {
  cursor: pointer;
}
.fc-monthly-portfolio-con {
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
  padding-top: 52px;
  padding-bottom: 52px;
  background: white;
  position: relative;
}
.fc-monthly-bg2 {
  width: 100%;
  padding-top: 40px;
  padding-bottom: 40px;
  background: #f8f9fc;
  position: relative;
}
.fc-monthly-portfolio-header {
  display: flex;
  flex-direction: row;
  align-items: center;
  flex-wrap: wrap;
  justify-content: space-between;
}
.fc-monthly-portfolio-heading {
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 1px;
  text-align: center;
  color: #324056;
  text-transform: uppercase;
}
.fc-monthly-portfolio-desc {
  margin-top: 50px;
  margin-bottom: 58px;
  font-size: 14px;
  letter-spacing: 0.3px;
  color: #486372;
}
.chart-heading {
  font-size: 18px;
  font-weight: 500;
  letter-spacing: 0.5px;
  color: #324056;
}
.chart-label {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.4px;
  color: #324056;
}
.chart-container {
  clear: both;
  padding-top: 20px;
  padding-bottom: 20px;
}
.fc-monthly-table-con {
  margin-top: 50px;
}
.fc-monthly-table-con table {
  width: 100%;
  table-layout: auto;
  border-collapse: collapse;
  border: 1px solid #e6ecf3;
}
.fc-monthly-table-con thead {
  display: table-header-group;
  vertical-align: middle;
  border-color: inherit;
}
.fc-monthly-table-con tr {
  display: table-row;
  vertical-align: inherit;
  cursor: pointer;
}
.fc-monthly-table-con th {
  color: #333;
  font-size: 11px;
  letter-spacing: 1px;
  font-weight: bold;
  white-space: nowrap;
  padding: 23px 30px;
  text-align: left;
  display: table-cell;
  text-transform: uppercase;
  border-bottom: 1px solid #e6ecf3;
}
.fc-monthly-table-con td {
  color: #333;
  font-size: 14px;
  border-collapse: separate;
  padding: 15px 30px;
  letter-spacing: 0.6px;
  font-weight: 400;
}
.fc-monthly-table-con tbody tr:nth-child(odd) {
  background-color: #fbfbfb;
}
.category-chart {
  width: 100%;
  max-width: 390px;
  padding: 20px;
  height: 350px;
  border-radius: 5px;
  box-shadow: 0 2px 16px 0 rgba(153, 152, 159, 0.08);
  background-color: #ffffff;
  margin-bottom: 20px;
}
.fc-monthly-table-con2 table {
  background: #ffffff;
  margin-bottom: 20px;
}
/* .fc-monthly-table-con2 table tbody td:first-child{
    background: #ffffff;
  } */
.fc-card-block {
  width: 100%;
  max-width: 280px;
  padding: 22px 24px;
  border-radius: 5px;
  box-shadow: -1px 1px 11px 0 rgba(225, 224, 232, 0.5);
  background-color: #ffffff;
  margin-bottom: 20px;
  border: 1px solid #f6f6f9;
  margin-right: 20px;
}
.fc-card-block .fL {
  width: 22%;
}
.fc-card-block .fR {
  width: 78%;
}
.card-img {
  width: 40px;
  height: 40px;
  border-radius: 100%;
  overflow: hidden;
  display: block;
  border: 1px solid #eee;
}
.card-heading {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.4px;
  color: #324056;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.card-day-txt {
  font-size: 11px;
  letter-spacing: 0.3px;
  color: #8ca1ad;
  padding-top: 5px;
  padding-bottom: 10px;
}
.card-reading-txt {
  padding-top: 1px;
  font-size: 22px;
  font-weight: normal;
  color: #ff3184;
  padding-bottom: 5px;
}
.card-reading-txt2 {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.4px;
  color: #8ca1ad;
}
.fc-monthly-portfolio-card-con {
  padding-bottom: 50px;
  border-bottom: 1px solid #f2f1f6;
}
.fc-card-container {
  margin-top: 50px;
}
.fc-energy-container {
  justify-content: inherit !important;
}
.monthly-portfolio-wrap .layout-header {
  display: none;
}
.monthly-portfolio-wrap {
  background: #fff;
  padding-bottom: 50px;
  overflow-y: scroll;
}
.monthly-portfolio-wrap .layout-header,
.monthly-portfolio-wrap .fc-layout-aside {
  display: none;
}
.print-border {
  padding-left: 15px;
}
.separator-print {
  color: #e5e5e5;
  font-size: 16px;
}
.fc-monthly-bg2 .fc-monthly-portfolio-con {
  background: none !important;
}
.flex {
  display: flex;
  align-items: center;
}
.chart-heading {
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 0.4px;
  color: #324056;
}
.monthly-portfolio-legends .fLegendContainer-right {
  width: 300px !important;
  margin-top: -42px !important;
  justify-content: flex-end !important;
  text-align: right !important;
  float: right !important;
}
.fc-newchart-container {
  clear: both !important;
  position: relative;
}

.spi-categorywise-chart .bb-axis tspan {
  fill: #324056 !important;
}

.spi-categorywise-chart .bb-axis-y-label {
  fill: rgb(238, 81, 143) !important;
}

.spi-categorywise-chart .bb .bb-axis-x path.domain {
  stroke: none !important;
}

.monthly-portfolio-legends .bb-axis tspan {
  fill: #324056 !important;
  font-weight: 500;
}

.monthly-portfolio-legends .bb .bb-axis-x path.domain {
  stroke: none !important;
}

.monthly-portfolio-legends .x-axis-zero-line {
  opacity: 0 !important;
}
.reading-cons-txt {
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.3px;
  color: #324056;
  margin-right: 10px;
}
@media screen and (max-width: 768px) {
  .monthly-portfolio-legends .bb-axis-x .tick text {
    text-anchor: start !important;
  }
  .fc-monthly-chart-con,
  .fc-monthly-table-con {
    width: 100% !important;
    overflow-y: scroll !important;
  }
  .category-chart {
    width: 100% !important;
    margin-left: 20px;
    margin-right: 20px;
    max-width: 100% !important;
  }
  .fc-monthly-portfolio-card-con {
    margin-left: 20px;
    margin-right: 20px;
  }
  .fc-card-block {
    width: 100% !important;
    max-width: 100% !important;
  }
}
.grey-td {
  color: #8d8c93;
  font-size: 12px;
  font-weight: normal;
  line-height: 1.5;
  letter-spacing: 0.5px;
}
.monthly-portfolio-date .el-icon-arrow-left,
.monthly-portfolio-date .el-button,
.monthly-portfolio-date .el-icon-arrow-right {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.3px;
  text-align: center;
  color: #ff3989 !important;
}
.monthly-portfolio-legends .fLegendContainer .el-color-picker--mini {
  margin-right: 5px;
}
.monthly-portfolio-legends .button-row {
  padding-bottom: 0 !important;
}
.progress {
  background-color: #f8f8f8;
  color: #fff;
  box-sizing: initial;
  color: #fff;
  font-size: 11px;
  font-size: 0.6875rem;
  height: 12px;
  line-height: 1.182;
  margin: 6px 0;
  position: relative;
  text-align: center;
  width: 100%;
  max-width: 200px;
  border-radius: 50px;
}

.progress > span {
  background-color: #2bc253;
  display: block;
  height: 100%;
  overflow: hidden;
  position: relative;
  width: auto;
  border-radius: 50px;
  margin-left: -10px;
}
.progress.progress-stacked > span {
  display: inline-block;
  float: left;
}
.bg-green {
  background: #39c2b0 !important;
}
.bg-orange {
  background: #f08532 !important;
}
.hour-text-13 {
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.6px;
  color: #333333;
  margin-left: 5px;
}
.grey-11 {
  font-size: 12px;
  letter-spacing: 0.4px;
  color: #95959a;
  margin-left: 18px;
  margin-top: 5px;
}
.grey-text12 {
  font-size: 12px;
  line-height: 1.5;
  letter-spacing: 0.5px;
  color: #8d8c93;
  padding-bottom: 5px;
}
.yet-to-add {
  font-size: 12px;
}
.top10-tech-report .bb-axis tspan:nth-child(2) {
  font-size: 8px !important;
  opacity: 0.8;
}
.fc-monthly-portfolio-heading-main {
  width: 80%;
  float: left;
}
.fc-monthly-portfolio-header-desktop {
  width: 100%;
}
@media print {
  body {
    overflow: scroll;
  }
  #q-app {
    height: auto;
  }
  .fc-monthly-portfolio-header-mobile {
    display: none !important;
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
  .separator-print,
  .print-border {
    display: none;
  }
  .fc-monthly-portfolio-heading-main {
    width: 80%;
    float: left;
  }
}
@media only screen and (max-width: 768px) {
  .fc-monthly-portfolio-header-desktop {
    display: none;
  }
  .fc-monthly-portfolio-header-mobile {
    display: block;
    width: 100%;
    padding-left: 20px;
    padding-right: 20px;
  }
  .fc-monthly-portfolio-desc {
    padding-left: 20px;
    padding-right: 20px;
  }
  .fc-monthly-portfolio-desc-mobile {
    margin-bottom: 0 !important;
    margin-top: 25px !important;
    text-align: center;
    float: none;
  }
  .mobile-date {
    float: none;
    margin-top: 20px;
  }
  .fc-monthly-table-con {
    margin-top: 30px;
  }
  .filter-field.date-filter-comp-new-report.monthly-portfolio-date {
    margin: 0 auto;
  }
  .fc-monthly-portfolio-header-mobile .fc-monthly-portfolio-heading {
    padding-top: 30px;
  }
  .chart-heading {
    padding-left: 20px;
    padding-right: 20px;
  }
  .chart-container {
    margin-top: 60px;
  }
  .el-dropdown {
    margin-top: 20px;
    margin-right: 20px;
    margin-bottom: 20px;
  }
  .separator-print,
  .print-border {
    display: none;
  }
  .fc-card-block {
    margin-right: 0 !important;
  }
}
.el-dropdown-menu__item:focus,
.el-dropdown-menu__item:not(.is-disabled):hover {
  background-color: #f5f7fa;
  color: #606266;
}
@media only screen and (min-width: 768px) {
  .fc-monthly-portfolio-header-desktop {
    display: block;
    width: 100%;
  }
  .fc-monthly-portfolio-header-mobile {
    display: none;
  }
}
</style>
<style>
@import './../../assets/styles/common.css';
</style>
<style>
@import './../../charts/styles/chart.css';
</style>
<style>
@import './../../assets/styles/energy.css';
</style>
<style>
@import './../../assets/styles/c3.css';
</style>
