<template>
  <div class="analytics-section-new row">
    <!-- analytics page header start -->
    <div class="newanalytics-page-header">
      <div class="row">
        <div class="col-8">
          <div class="analytics-page-header-filters">
            <div class="col-2">
              <div class="building-avatar-block fL">
                <div class="building-avatar">
                  <img
                    src="~statics/space/building.svg"
                    height="40px"
                    width="40px"
                  />
                </div>
              </div>
            </div>
            <div class="col-2">
              <div class="fL">
                <div class="selected-portfolio-analytics">
                  <analaytics-filter
                    ref="buildings-filter"
                    v-if="selectedBuildings"
                    :multiple="true"
                    :selectedBuildings.sync="selectedBuildings"
                    class="portfolio-analysis-add-points-btn"
                  ></analaytics-filter>
                </div>
              </div>
            </div>
            <div class="col-4">
              <div class="mL20">
                <el-select
                  class="period-select"
                  v-model="analyticsConfig.mode"
                  @change="onModeChange"
                  :placeholder="$t('common._common.mode')"
                  :title="$t('common._common.mode')"
                  data-arrow="true"
                  v-tippy
                >
                  <el-option
                    :label="$t('common.wo_report.time')"
                    :value="1"
                  ></el-option>
                  <el-option
                    :label="$t('common.products.series')"
                    :value="2"
                  ></el-option>
                  <el-option
                    :label="$t('common.products.conslidated')"
                    :value="3"
                    v-if="isSameDataPoint"
                  ></el-option>
                </el-select>
                <el-cascader
                  class="period-select"
                  :placeholder="$t('common.wo_report.compare_to')"
                  :options="baseLineCasecaderOptions"
                  :value="analyticsConfig.baseLine"
                  v-if="baseLineList && !isActionsDisabled"
                  @change="onBaseLineChange"
                  :title="$t('common.wo_report.compare_with_baseline')"
                  data-arrow="true"
                  v-tippy
                >
                </el-cascader>
                <el-select
                  class="period-select"
                  v-model="analyticsConfig.period"
                  :placeholder="$t('common.wo_report.period')"
                  v-if="analyticsConfig.mode !== 2"
                  :title="$t('common.dashboard.time_period')"
                  data-arrow="true"
                  v-tippy
                  @change="onPeriodChange"
                >
                  <el-option
                    v-for="(period, index) in getAvailablePeriods"
                    :key="index"
                    :label="period.name"
                    :value="period.value"
                    :disabled="!period.enable"
                  ></el-option>
                </el-select>
              </div>
            </div>
          </div>
        </div>
        <div class="col-4" style="text-align: right;">
          <f-report-options
            :isActionsDisabled="isActionsDisabled"
            optionClass="analytics-page-options"
            :optionsToEnable="[1, 2, 5]"
            :reportObject="reportObject"
            :params="reportObject ? reportObject.params : null"
            :savedReport="analyticsConfig.savedReport"
            class="analytics-page-options-building-analysis"
          ></f-report-options>
        </div>
      </div>
    </div>

    <!-- CHART CUSTOMIZATION start -->
    <div
      class="customize-container col-4 portfolio-customize-container"
      v-if="reportObject"
      v-show="showChartCustomization"
    >
      <div class="customize-header">
        <div class="customize-H">
          {{ $t('home.reports.chart_customization') }}
          <i class="el-icon-close" @click="hideChartCustomizationDialog"></i>
        </div>
      </div>
      <div class="customize-body">
        <f-chart-customization
          v-if="reportObject"
          v-show="showChartCustomization"
          :tab="chartCustomizationActiveTab"
          @close="showChartCustomization = false"
          :report="reportObject"
          :resultDataPoints="resultObject ? resultObject.report.dataPoints : []"
          :config="analyticsConfig"
        ></f-chart-customization>
      </div>
    </div>
    <!-- CHART CUSTOMIZATION end -->

    <div class="portfolio-sidebar col-2">
      <spinner v-if="loading" :show="loading"></spinner>
      <div v-else>
        <div class="na-parent">
          <div
            class="na-header mT20"
            @click="
              collapseState.buildingEnergy = !collapseState.buildingEnergy
            "
          >
            {{ $t('common._common.building_energy') }}
          </div>
          <div class="na-content" v-if="collapseState.buildingEnergy">
            <div class="na-child" @click="showBuildingDataPoints('Energy')">
              <div
                class="na-child-content"
                v-bind:class="{ active: selectedTab === 'Energy' }"
              >
                {{ $t('common.products.energy') }}
              </div>
            </div>
            <div class="na-child" @click="showBuildingDataPoints('Power', 2)">
              <div
                class="na-child-content"
                v-bind:class="{ active: selectedTab === 'Power' }"
              >
                {{ $t('common.wo_report.power') }}
              </div>
            </div>
          </div>
        </div>

        <div
          class="na-parent"
          v-if="serviceWithMeters && Object.keys(serviceWithMeters).length"
        >
          <div
            class="na-header"
            @click="collapseState.endUseEnergy = !collapseState.endUseEnergy"
          >
            {{ $t('common.wo_report.end_use_energy') }}
          </div>
          <div class="na-content" v-if="collapseState.endUseEnergy">
            <div
              class="na-child"
              v-for="(label, id) in serviceWithMeters"
              :key="id"
              @click="showServiceDataPoints(id)"
            >
              <div
                class="na-child-content"
                v-bind:class="{ active: selectedTab === 'id' }"
              >
                {{ label }}
              </div>
            </div>
          </div>
        </div>

        <div class="na-parent">
          <div
            class="na-header"
            @click="collapseState.cost = !collapseState.cost"
          >
            {{ $t('common.tabs.cost') }}
          </div>
          <div class="na-content" v-if="collapseState.cost">
            <div class="na-child" @click="showBuildingDataPoints('Cost')">
              <div
                class="na-child-content"
                v-bind:class="{ active: selectedTab === 'Cost' }"
              >
                {{ $t('common._common.building_energy_cost') }}
              </div>
            </div>
            <!-- <div class="na-child">
              <div class="na-child-content">End Use Energy Cost</div>
            </div> -->
          </div>
        </div>

        <!-- <div class="na-parent" v-for="(item, index) in  sidebarData" :key="index">
        <div class="na-header" @click="sidebarcollapse(item)">{{item.title}}</div>
        <div class="na-content">
        <div class="na-child" v-for="(child, idx) in item.value" :key="idx" v-if="item.collapse">
          <div class="na-child-content-cost" v-bind:class="{active: thischild === child}" v-if="item.title === 'COST'">
            <div class="na-child-title2" @click="getParentActive(item.type, idx, child)" >{{child.label}}</div>
            <div class="na-child-content2" v-for="(list, idex) in child.value"  v-bind:class="{active: thischild2 === list}" v-if="child.collapse" :key="idex">
              <div @click="getActive(item.type, idex, list, list)">{{list}}</div></div>
            </div>
          <div class="na-child-content" @click="getActive(item.type, idx, child)" v-bind:class="{active: thischild === child}" v-else>{{child}}</div>
        </div>
      </div>
      </div> -->
      </div>
    </div>

    <!-- Graph section start -->
    <div
      class="portfolio-analytic-summary col-10 scrollable"
      :style="
        showChartCustomization
          ? 'max-width: calc(100% - 450px) !important;'
          : ''
      "
      :class="{
        'col-12, mleft0': !showChartCustomization,
        'col-8, mleft217': showChartCustomization,
      }"
    >
      <div
        class="add-points-block portfolio-add-points-block"
        v-if="reportObject"
      >
        <el-button-group v-if="analyticsConfig.mode === 1">
          <el-button
            type="primary"
            class="add-points-btn mL10"
            @click="showDerivation = true"
            ><img
              src="~statics/formula/formula-grey.svg"
              width="17px"
              height="17px"
          /></el-button>
        </el-button-group>
        <el-button-group>
          <el-button
            type="primary"
            class="add-points-btn"
            @click="showChartCustomizationDialog('datapoints')"
            ><img src="~assets/settings-grey.svg" width="17px" height="17px"
          /></el-button>
        </el-button-group>
      </div>
      <div
        ref="chartSection"
        class="self-center fchart-section building-graph-container portfolio-building-graph-container"
      >
        <div v-if="!analyticsConfig.dataPoints.length" class="text-center">
          <div class="p15" v-if="reportId || alarmId || cardId">
            <spinner :show="true" />
          </div>
          <div
            class="p15 flex-middle height80vh justify-content-center flex-direction-column"
            v-else
          >
            <div>
              <inline-svg
                src="svgs/emptystate/reportlist"
                iconClass="icon text-center icon-xxxlg"
              ></inline-svg>
            </div>
            <div class="nowo-label">
              {{ $t('common._common.please_select_data_points_analyze') }}
            </div>
          </div>
        </div>
        <f-new-analytic-report
          ref="newAnalyticReport"
          :config.sync="analyticsConfig"
          :baseLines="baseLineList"
          @reportLoaded="reportLoaded"
          v-else
        ></f-new-analytic-report>
      </div>
    </div>
    <!-- Graph section end -->
    <derivation
      v-if="showDerivation"
      :visibility.sync="showDerivation"
      :report="reportObject"
      :config="analyticsConfig"
    ></derivation>
  </div>
</template>
<script>
import AnalayticsFilter from 'pages/energy/analytics/components/AnalayticsFilter'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import FNewAnalyticReport from 'pages/energy/analytics/components/FNewAnalyticReport'
import FChartCustomization from 'newcharts/components/FChartCustomization'
import FReportOptions from 'pages/report/components/FReportOptions'
import Derivation from 'pages/energy/analytics/components/FDerivation'
import NewDateHelper from '@/mixins/NewDateHelper'
import { mapState } from 'vuex'

export default {
  mixins: [AnalyticsMixin],
  components: {
    AnalayticsFilter,
    FChartCustomization,
    FNewAnalyticReport,
    FReportOptions,
    Derivation,
  },
  data() {
    return {
      loading: true,
      showChartCustomization: false,
      chartCustomizationActiveTab: 'datapoints',
      selectedBaseLineId: '',
      showDerivation: false,
      baseLineList: null,
      oldBaseLine: null,
      baseLineCasecaderOptions: [],
      analyticsConfig: {
        name: 'Portfolio Analysis',
        key: 'PORTFOLIO_ANALYSIS',
        analyticsType: 1,
        type: 'reading',
        period: 12,
        mode: 1,
        baseLine: null,
        dateFilter: NewDateHelper.getDatePickerObject(28),
        chartViewOption: 0,
        dataPoints: [],
      },
      collapseState: {
        buildingEnergy: true,
        endUseEnergy: false,
        cost: false,
        water: false,
      },
      reportObject: null,
      resultObject: null,
      selectedBuildings: this.$store.getters.getBuildingsPickList()
        ? Object.keys(this.$store.getters.getBuildingsPickList())
        : null,
      readingFields: null,
      buildingWithMeters: [],
      selectedTab: null,
    }
  },
  watch: {
    getAvailablePeriods: {
      handler(newData, oldData) {
        let avail = this.getAvailablePeriods
        let selected = avail.find(
          a => a.value === this.analyticsConfig.period && a.enable
        )
        if (!selected) {
          let filterName = this.analyticsConfig.dateFilter.operationOn

          let defaultPeriod = avail.filter(a => a.enable)[0].value
          if (filterName === 'week') {
            defaultPeriod = 12
          } else if (filterName === 'month') {
            defaultPeriod = 12
          } else if (filterName === 'year') {
            defaultPeriod = 10
          }
          this.analyticsConfig.period = defaultPeriod
        }
      },
      immediate: true,
    },
    selectedBuildings() {
      if (
        this.reportObject &&
        this.reportObject.options &&
        this.reportObject.options.common
      ) {
        this.reportObject.options.common.buildingIds = this.selectedBuildings
      }
    },
  },
  created() {
    this.$store.dispatch('loadServiceList')
    this.$store.dispatch('loadBuildings')
  },
  mounted() {
    this.loadBuildingWithMeters()
    this.loadBaselineList()

    if (this.reportId) {
      this.loadReport()
    }
  },
  computed: {
    ...mapState({
      serviceList: state => state.serviceList,
    }),

    isSameDataPoint() {
      if (this.analyticsConfig && this.analyticsConfig.dataPoints.length) {
        let dp = {}
        for (let d of this.analyticsConfig.dataPoints) {
          dp[d.yAxis.fieldId] = true
        }
        if (Object.keys(dp).length === 1) {
          return true
        }
      }
      return false
    },

    getAvailablePeriods() {
      let operationOnId = this.analyticsConfig.dateFilter.operationOnId
      let avail = []

      avail.push({
        name: this.$t('common.wo_report.high_res'),
        value: 0,
        enable: operationOnId !== 6 ? operationOnId !== 4 : true,
      })

      avail.push({
        name: this.$t('common.wo_report.hourly'),
        value: 20,
        enable: operationOnId !== 6 ? operationOnId !== 4 : true,
      })

      avail.push({
        name: this.$t('common._common.daily'),
        value: 12,
        enable: true,
      })

      avail.push({
        name: this.$t('common._common.weekly'),
        value: 11,
        enable:
          operationOnId !== 6
            ? operationOnId === 3 || operationOnId === 4
            : true,
      })

      avail.push({
        name: this.$t('common._common.monthly'),
        value: 10,
        enable: true,
      })

      avail.push({
        name: this.$t('common.products.quarterly'),
        value: 25,
        enable: operationOnId === 4 || operationOnId === 5,
      })

      avail.push({
        name: this.$t('common._common.yearly'),
        value: 8,
        enable: operationOnId === 4,
      })

      avail.push({
        name: this.$t('common.header.hour_of_day'), // 12, 1, 2, 3
        value: 19,
        enable: true,
      })

      avail.push({
        name: this.$t('common.header.day_of_week'), // sun, mon
        value: 17,
        enable: true,
      })

      avail.push({
        name: this.$t('common.header.day_of_month'), // 1,2,3
        value: 18,
        enable: true,
      })

      // avail.push({
      //   name:'Week of year', // W1, W2
      //   value: 16,
      //   enable: true
      // })

      // avail.push({
      //   name:'Month of year', // Jan, Feb , march
      //   value: 15,
      //   enable: true
      // })

      return avail
    },

    serviceWithMeters() {
      if (this.serviceList) {
        let filteredServiceList = {}
        for (let serviceId in this.serviceList) {
          let serviceName = this.serviceList[serviceId]

          for (let building of this.buildingWithMeters) {
            if (
              this.selectedBuildings.indexOf(building.id) !== -1 ||
              this.selectedBuildings.indexOf(building.id + '') !== -1
            ) {
              let rootServiceMeter = building.rootServiceMeters
                ? building.rootServiceMeters.find(
                    sm => sm.purpose.id === parseInt(serviceId)
                  )
                : null
              if (rootServiceMeter) {
                filteredServiceList[serviceId] = serviceName
              }
            }
          }
        }
        return filteredServiceList
      }
      return {}
    },
  },
  methods: {
    loadBuildingWithMeters() {
      let self = this
      self.loading = true
      self.$http
        .get('/report/energy/portfolio/getAllBuildings')
        .then(response => {
          let reportData = response.data.reportData
          if (reportData) {
            self.buildingWithMeters = reportData.buildingDetails

            if (self.buildingWithMeters && self.buildingWithMeters.length) {
              for (let b of self.buildingWithMeters) {
                if (b.rootMeter) {
                  self.$util
                    .loadAssetReadingFields(b.rootMeter.id)
                    .then(readingFields => {
                      self.readingFields = readingFields
                      self.loading = false
                    })
                  break
                }
              }
            } else {
              self.loading = false
            }
          }
        })
    },

    loadBaselineList() {
      let self = this
      self.$http.get('/baseline/all').then(function(response) {
        if (response.status === 200) {
          self.baseLineList = response.data ? response.data : []

          self.baseLineCasecaderOptions = []
          self.baseLineCasecaderOptions.push({
            label: 'None',
            value: -1,
          })

          for (let b of self.baseLineList) {
            let children = null
            if (
              b.rangeTypeEnum === 'PREVIOUS_MONTH' ||
              b.rangeTypeEnum === 'ANY_MONTH'
            ) {
              children = [
                {
                  label: 'Same date',
                  value: 5,
                },
                {
                  label: 'Same week',
                  value: 2,
                },
              ]
            } else if (
              b.rangeTypeEnum === 'PREVIOUS_YEAR' ||
              b.rangeTypeEnum === 'ANY_YEAR'
            ) {
              children = [
                {
                  label: 'Same date',
                  value: 4,
                },
                {
                  label: 'Same week',
                  value: 2,
                },
              ]
            }

            if (b.rangeTypeEnum !== 'PREVIOUS') {
              self.baseLineCasecaderOptions.push({
                label: b.name,
                value: b.id,
                children: children,
              })
            }
          }
        }
      })
    },

    showBuildingDataPoints(pointName, aggregation) {
      let self = this

      let dataPoints = []

      if (!self.readingFields) {
        this.$message.error(
          this.$t('common.products.no_root_meters_configured')
        )
        return
      }
      let readingField = self.readingFields.find(
        rt => rt.displayName === pointName
      )
      if (!readingField) {
        this.$message.error(
          this.$t('common.products.no') +
            pointName +
            this.$t('common.header.reading_available')
        )
        return
      }
      if (
        readingField &&
        this.buildingWithMeters &&
        this.buildingWithMeters.length
      ) {
        for (let building of this.buildingWithMeters) {
          if (
            (this.selectedBuildings.indexOf(building.id) !== -1 ||
              this.selectedBuildings.indexOf(building.id + '') !== -1) &&
            building.rootMeter
          ) {
            dataPoints.push({
              yAxis: {
                fieldId: readingField.id,
                aggr: aggregation || 3,
              },
              parentId: building.rootMeter.id,
            })
          }
        }
      }

      this.analyticsConfig.dataPoints = dataPoints
      this.selectedTab = pointName
    },

    showServiceDataPoints(serviceId, pointName, aggregation) {
      let self = this

      let dataPoints = []

      let readingField = self.readingFields.find(
        rt => rt.displayName === (pointName || 'Energy')
      )
      if (
        readingField &&
        this.buildingWithMeters &&
        this.buildingWithMeters.length
      ) {
        for (let building of this.buildingWithMeters) {
          if (
            this.selectedBuildings.indexOf(building.id) !== -1 ||
            this.selectedBuildings.indexOf(building.id + '') !== -1
          ) {
            let rootServiceMeter = building.rootServiceMeters
              ? building.rootServiceMeters.find(
                  sm => sm.purpose.id === parseInt(serviceId)
                )
              : null
            if (rootServiceMeter) {
              dataPoints.push({
                yAxis: {
                  fieldId: readingField.id,
                  aggr: aggregation || 3,
                },
                parentId: rootServiceMeter.id,
              })
            }
          }
        }
      }

      if (!dataPoints || !dataPoints.length) {
        this.$message.error(
          this.$t('common.products.no_root_meter_configured_end_use')
        )
      } else {
        this.analyticsConfig.dataPoints = dataPoints
      }
    },

    showChartCustomizationDialog(defaultTab) {
      this.showChartCustomization = true
      if (defaultTab) {
        this.chartCustomizationActiveTab = defaultTab
      }
    },

    hideChartCustomizationDialog() {
      this.showChartCustomization = false
    },

    reportLoaded(report, result) {
      this.reportObject = report
      this.resultObject = result
      if (report.options.common && report.options.common.buildingIds) {
        if (
          !report.options.common.buildingIds.length &&
          this.selectedBuildings
        ) {
          this.reportObject.options.common.buildingIds = this.selectedBuildings
        } else {
          this.selectedBuildings = report.options.common.buildingIds
          this.$refs['buildings-filter'].updateSelectedBuilding(
            report.options.common.buildingIds
          )
        }
      }
    },
    onPeriodChange() {
      if (!this.analyticsConfig.dataPoints.length) {
        let filter = 'M'
        if (this.analyticsConfig.period === 0) {
          filter = 'D'
        }
        if (this.analyticsConfig.period === 20) {
          filter = 'W'
        } else if (this.analyticsConfig.period === 10) {
          filter = 'Y'
        }
        this.analyticsConfig.dateFilter = this.getDefaultDateFilter(filter)
      }
    },

    onModeChange() {
      if (this.analyticsConfig.mode !== 1) {
        this.analyticsConfig.dataPoints = this.analyticsConfig.dataPoints.filter(
          dp => dp.type !== 2
        )
      }
    },

    onBaseLineChange(newBaseLine) {
      if (this.oldBaseLine && this.oldBaseLine[0] === newBaseLine[0]) {
        return
      }
      if (this.oldBaseLine && this.oldBaseLine[0] !== -1) {
        let oldBlName = this.baseLineList.find(
          bl => bl.id === this.oldBaseLine[0]
        ).name
        let dp = this.reportObject.options.dataPoints.find(
          dp => dp.baseLineName === oldBlName
        )
        if (
          !this.checkAndDeletePointsRel(this.reportObject.options, dp.alias)
        ) {
          this.$message.error(
            dp.baseLineName +
              this.$t(
                'common.header.has_been_derivation_please_delete_derivation'
              )
          )
          this.$set(this.analyticsConfig, 'baseLine', this.oldBaseLine)
          return
        }
      }
      this.oldBaseLine = [...newBaseLine]
      this.$set(this.analyticsConfig, 'baseLine', newBaseLine)
    },
  },
}
</script>
<style>
.charttype-options {
  border-bottom: 1px solid #6666660d;
  padding: 8px 13px;
}
.charttype-options ul.fchart-icon li {
  float: left;
  cursor: pointer;
  width: 45px;
  height: 40px;
  padding: 20px 10px 10px 10px;
}

.charttype-options ul li svg {
  width: 18px;
  height: 18px;
  opacity: 0.3;
}

.charttype-options ul li svg:hover,
.charttype-options ul li.active svg {
  opacity: 1;
}

.charttype-options-select {
  padding-top: 0px;
  padding-left: 10px;
  padding-right: 10px;
}
.chart-category-dropdown {
  font-size: 12px;
}
.datefilter-name {
  padding-right: 8px;
  padding-top: 7px !important;
}
.chart-created-info {
  text-align: left;
  line-height: 1.5;
  color: #333333;
  font-size: 12px;
  justify-content: center;
}
.chart-category-dropdown input.el-input__inner {
  font-size: 12px;
}
.datefilter-name {
  white-space: nowrap;
  align-items: center;
  justify-content: center;
  padding-top: 10px;
  padding-right: 10px;
}
.charttype-options-select {
  font-size: 12px;
}
.building-filter {
  text-align: left;
}
.building-filter .filter-entry {
  display: inline-block;
  font-size: 12px;
  color: #666;
  padding-left: 10px;
}

.building-filter .filter-entry .q-select {
  font-size: 12px;
  margin-top: 0px;
  padding-bottom: 0px;
  margin-right: 10px;
}

.building-filter .filter-entry .q-select i {
  font-size: 12px;
  opacity: 0.5;
  padding-right: 4px;
}

.building-filter .filter-entry .q-select:before {
  height: 0px;
}

.building-filter .filter-entry .q-select .q-if-control[slot='after'] {
  display: none;
}

.fc-analysis-filter {
  padding: 20px;
}

.fc-analysis-filter .pull-left {
  padding-top: 4px;
}

.fc-analysis-filter .filter-field {
  font-size: 12px;
  margin-top: 0px;
  padding-bottom: 0px;
  margin-right: 15px;
  display: inline-block;
}

.fc-analysis-filter .filter-field i {
  opacity: 0.4;
  padding-right: 4px;
}

.fc-analysis-filter .filter-field .plholder {
  opacity: 0.5;
  font-size: 11px;
}

.chart-icon svg {
  width: 18px;
  height: 18px;
}
.chart-label {
  margin-top: -4px;
  margin-left: 6px;
}
.fchart-overlay {
  position: absolute;
  width: 100%;
  height: 100%;
  background: #ffffff91;
  cursor: progress;
}
.report-col {
  padding: 5px;
}
.report-col.active {
  color: red !important;
}
.date-icon-right,
.date-icon-left {
  font-size: 15px;
  position: relative;
  top: 2px;
}
.fchart-overlay {
  position: absolute;
  width: 100%;
  height: 100%;
  background: #ffffff91;
  cursor: progress;
}
.fc-analysis-filter {
  box-shadow: 0px 5px 10px rgba(0, 0, 0, 0.03);
  border-bottom: 1px solid #cccccc61;
}
.en-dropDown {
  padding: 4px 10px;
  border: 1px solid #ccccccd6;
  border-radius: 5px;
  margin-right: 15px;
}
.en-dropDown .el-input .el-input__inner {
  border: none !important;
}
.en-dropDown .el-input__inner {
  max-width: 75%;
}
.en-dropDown input {
  font-size: 13px;
  color: #333;
}
.en-icon {
  position: relative;
  left: 30px;
  top: 5px;
}
.en-icon svg,
.en-icon img {
  width: 20px;
}
.fc-analysis-filter .el-select-dropdown.el-popper {
  margin-left: -20px;
}
.an-sidebar.header {
  box-shadow: 0px 5px 10px rgba(0, 0, 0, 0.03);
}
.en-fchart {
  padding-left: 10px;
  padding-right: 50px;
}
.reading-analysi .fchart-section {
  height: calc(100vh - 150px);
  overflow: auto;
}
.an-spin {
  align-items: center;
  width: 80px;
  margin: 0 auto;
  height: 80px;
  margin-top: 10%;
}
.en-dropDown.el-cascader,
.en-dropDown.el-cascader .el-input__icon {
  line-height: 0px;
}
.reading-fileds-header .en-dropDown {
  margin-right: 5px;
}
.reading-analysis .fc-analysis-filter {
  display: inline-flex;
  width: 100%;
}
.reading-analysis
  .fc-analysis-filter
  .pull-right
  input.el-input
  .el-input__inner {
  border-bottom: 0px solid #d8dce5;
  margin-left: 40px;
}
.date-filter-day input.el-input__inner {
  border: 0px;
  margin-left: 30px;
}
.fc-el-report-pop {
  padding: 0px !important;
}
.fc-el-btn {
  text-align: center;
  padding: 10px;
  font-size: 12px;
  align-items: center;
  text-transform: uppercase;
  padding-bottom: 15px;
  cursor: pointer;
  font-weight: 500;
  padding-top: 15px;
}
.el-report-cancel-btn {
  background-color: #f4f4f4;
  color: #5f5f5f;
}
.el-report-save-btn {
  color: white;
  background-color: #39b2c2;
}
.analytics-page-header-title {
  letter-spacing: 0.6px;
  color: #000000;
  font-weight: 500;
  font-size: 18px;
}
.analytics-page-header-filters {
  padding-top: 12px;
}
.analytics-page-header-filters .el-select {
  margin-right: 10px;
  margin-left: 10px;
}
.analytics-page-header-filters .el-select input,
.analytics-page-header-filters .period-select input {
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #333333;
  padding: 8px;
  height: 42px;
  border-radius: 3px;
  background-color: #ffffff;
  border: solid 1px #d0d9e2;
  padding-left: 13px;
}
.analytics-section-new .analytics-page-header-filters .period-select {
  width: 140px;
}

.analytics-sidebar-menu .inner-child {
  padding-left: 20px;
  padding-top: 12px;
  padding-bottom: 12px;
}
.analytics-sidebar-menu .inner-child:hover {
  background-color: #f0f7f8;
}
.double-inner-child {
  padding: 15px 0 15px 10px;
  font-size: 13px;
  letter-spacing: 0.6px;
  cursor: pointer;
}
.double-inner-child:hover {
  padding: 15px 0 15px 10px;
  background-color: #f0f7f8;
}
.building-na-child-content:hover {
  display: block;
}
.double-inner-child:hover,
.asset-img-hov:hover {
  display: block;
  background-color: #f0f7f8;
  cursor: pointer;
}
.double-inner-child .field-txt {
  width: 170px;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}
.building-na-child-content {
  cursor: pointer;
  font-size: 14px;
}
.building-dialog .el-dialog__header {
  padding: 0;
}
.title-building {
  letter-spacing: 1.1px;
  color: rgb(0, 0, 0);
  font-weight: 500;
  font-size: 14px;
  text-transform: uppercase;
  padding-left: 30px;
}
.bulidng-search-block {
  margin-top: 10px;
}
.bulidng-search-block .search-element {
  border: 0px solid #e6ecf3 !important;
  border-bottom: 1px solid #e6ecf3 !important;
  padding: 0 30px 0 !important;
  border-radius: 0px !important;
  height: 28px;
  width: 100%;
  border-top: none !important;
  border-left: none !important;
  border-right: none !important;
  font-size: 14px;
  margin: 0;
}
.building-check-list .el-checkbox + .el-checkbox {
  margin-left: 0;
}
.building-check-list .el-checkbox,
.building-check-list .el-checkbox__input {
  display: flex;
}
.building-check-list .el-checkbox {
  padding-bottom: 10px;
  padding-top: 10px;
  padding-left: 30px;
}
.building-check-list .el-checkbox:hover {
  background: #f1f8fa;
  transition: 0.2s ease;
}
/* .building-check-list .el-checkbox:nth-child(odd){
background-color: #fbfbfb;
} */
.building-check-list .el-checkbox:nth-child(even) {
  background-color: #fff;
}
.building-check-list .el-checkbox__label {
  font-size: 14px;
  letter-spacing: 0.4px;
  color: #333333;
  font-weight: 400;
  width: 210px;
  max-width: 210px;
  overflow-x: hidden;
  word-break: break-all;
  white-space: normal;
}
.building-dialog .el-dialog__body {
  padding: 0;
}
.building-dialog-body {
  height: 60vh;
  padding: 0 0 60px;
  overflow-y: scroll;
  overflow-x: hidden;
}
.building-dialog-footer-btn {
  width: 50%;
}
.building-dialog-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
}
.building-header-dialog {
  position: sticky;
  padding: 25px 0 0;
}
.bulidng-search-block .el-icon-search {
  position: absolute;
  top: 64px;
  right: 29px;
  z-index: 1;
}
.portfolio-building-graph-container {
  padding: 0 20px 100px 20px !important;
  margin-top: -25px;
}
.selectedBuildingPopup {
  position: relative;
  cursor: pointer;
  display: inline-block;
}
.data-length-btn {
  border-radius: 3px;
  border: solid 1px #8fd2db;
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #31a4b4;
  padding: 7px;
  position: relative;
  top: 1px;
}
.asset-deselect-dialog-body {
  padding: 15px 0 10px;
  border: solid 1px #e8e8e8;
}
.asset-result-txt {
  width: 100%;
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
}
.asset-result-txt:nth-child(odd) {
  background-color: #fbfbfb;
}
.asset-result-txt:nth-child(even) {
  background-color: #fff;
}
.building-dialog .el-dialog__header .el-dialog__headerbtn {
  display: none;
}
.analysis-search-input {
  width: 100%;
  height: 37px;
  border-right: none;
  border-left: none;
  border-top: 1px solid transparent;
  border-bottom: 1px solid #e8e8e8;
  padding-left: 22px;
  font-size: 16px;
  padding-right: 20px;
}
.building-analysis-side-block {
  margin-top: 20px;
  padding-bottom: 30px;
  position: relative;
  display: block;
  clear: both;
}
.blue-txt-10 {
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 0.7px;
  text-align: right;
  color: #38b3c2;
  visibility: hidden;
  text-transform: uppercase;
}
.building-hover-actions {
  cursor: pointer;
}
.building-hover-actions:hover .blue-txt-10 {
  visibility: visible;
  padding-right: 20px;
}
.building-anlysis-label-txt {
  font-size: 14px;
  font-weight: 400;
  letter-spacing: 0.4px;
  color: #333333;
  padding-left: 30px;
  display: flex;
  align-items: center;
  padding-top: 10px;
  padding-bottom: 10px;
  cursor: pointer;
  padding-right: 10px;
  line-height: 20px;
  border-top: 1px solid transparent;
  border-bottom: 1px solid transparent;
}
.building-anlysis-label-txt:hover {
  background: #f1f8fa;
  transition: 0.2s ease;
}
.asset-grey-txt {
  font-size: 10px;
  font-weight: bold;
  letter-spacing: 1.1px;
  color: #a5acb4;
  padding-top: 20px;
  padding-left: 35px;
}
.assets-list-block {
  margin-top: 10px;
  cursor: pointer;
}
.assets-list-txt {
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #333333;
  margin: 0;
  padding-left: 35px;
  padding-top: 12px;
  padding-bottom: 12px;
}
.assets-list-txt:hover {
  background-color: #f1f8fa;
}
/* .building-anlysis-active-block{
  margin-top: 30px;
} */
.building-search-block {
  position: relative;
}
.building-search-block .el-icon-search {
  position: absolute;
  right: 18px;
  top: 9px;
  color: #bec9d6;
  font-size: 18px;
  vertical-align: middle;
}
.customize-container {
  width: 450px;
  max-width: 450px;
  box-shadow: 5px 3px 10px 0 rgba(181, 181, 181, 0.15);
  background-color: #ffffff;
  border-bottom: 1px solid transparent;
  border: 1px solid #e6e6e6;
  z-index: 1;
  transition: all ease-in-out 0.5s;
  -webkit-transition: all ease-in-out 0.5s;
  -moz-transition: all ease-in-out 0.5s;
  position: absolute;
  bottom: 0;
  left: 0px;
  z-index: 1234;
  height: 100vh;
}
.customize-header {
  padding: 20px 20px 20px 26px;
}
.customize-header .customize-H {
  font-size: 12px;
  font-weight: bold;
  letter-spacing: 0.9px;
  color: #25243e;
}
.customize-H .el-icon-close {
  color: 000000;
  font-size: 20px;
  font-weight: 500;
  float: right;
  position: relative;
  cursor: pointer;
}
.customize-body .el-tabs__nav-scroll {
  padding-left: 27px;
}
.customize-body .el-tabs__active-bar {
  background-color: #ef508f;
  width: 21px !important;
}
.customize-body .el-tabs__item.is-active {
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.4px;
  color: #25243e;
}
.customize-body .el-tabs__item {
  font-size: 13px;
  font-weight: 400;
  letter-spacing: 0.4px;
  color: #50506c;
}
.customize-body .el-tabs__nav-wrap::after {
  height: 1px;
  background-color: 1px solid #e0e0e0;
}
.customize-tab-body-axis {
  padding-top: 14px;
  padding-bottom: 14px;
}
.customize-body .el-tabs__header {
  margin: 0;
}
.customize-input-block {
  margin-top: 10px;
}
.customize-input-block .customize-label {
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.4px;
  color: #6b7e91;
  margin: 0;
}
.customize-input-block .el-input .el-input__inner {
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #333333;
}
.customize-radio-block .el-radio-button__inner {
  padding: 11px 21px;
  font-size: 14px;
  border-radius: 0;
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #333333;
  font-weight: 400;
}
.customize-radio-block .el-radio-button:first-child .el-radio-button__inner {
  box-shadow: 0 2px 4px 0 rgba(232, 229, 229, 0.5) !important;
  border: solid 1px #e2e8ee;
}
.customize-radio-block
  .el-radio-button__orig-radio:checked
  + .el-radio-button__inner {
  background-color: #f1fdff;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.5px;
  text-align: center;
  color: #30a0af;
  box-shadow: -1px 0 0 0 #39b2c2 !important;
  border-color: #39b2c2 !important;
}
.customize-radio-block .el-radio-button__inner:hover {
  color: #39b2c2 !important;
}
.customize-radio-block
  .el-radio-button__orig-radio:checked
  + .el-radio-button__inner {
  border-left: 1px solid transparent !important;
}
.y-axis-container {
  margin-top: 30px;
}
.y-axis-container .el-tabs__nav-scroll {
  padding-left: 0;
}
.y-axis-container .el-tabs--card > .el-tabs__header .el-tabs__item {
  border: none;
}
.y-axis-container .el-tabs--card > .el-tabs__header .el-tabs__item.is-active {
  border-color: transparent;
  background-color: #ef508f;
  border-radius: 50px;
  color: #fff;
  font-weight: 600;
}
.y-axis-container .el-tabs--card > .el-tabs__header .el-tabs__nav,
.y-axis-container .el-tabs--card > .el-tabs__header {
  border: none;
}
.y-axis-container .el-tabs__item {
  height: 35px;
  line-height: 35px;
}
.pl27 {
  padding-left: 27px;
}
.yaxis-tab-block .el-tabs__header {
  background-color: #fbfbfb;
  padding-top: 10px;
  padding-bottom: 10px;
  border-top: 1px solid #e6e6e6 !important;
  border-bottom: 1px solid #e6e6e6 !important;
  padding-left: 27px;
}
.customize-scroll {
  overflow-x: hidden;
  overflow-y: scroll;
  height: calc(100vh - 310px);
  padding-bottom: 50px;
}
.customize-scroll-hidden {
  overflow: hidden;
}
.customize-select .el-input--suffix {
  width: 250px;
  min-width: 250px;
}
.customize-select2 .el-input__inner {
  width: 180px;
  min-width: 180px;
}
.width68 {
  width: 68%;
  flex: 0 0 68%;
}
.width80 {
  width: 80%;
  flex: 0 0 80%;
}
.quick-search-input3 {
  transition: 0.2s linear;
  padding: 10px 40px 8px 20px !important;
  line-height: 1.8;
  width: 100%;
  margin-bottom: 5px;
  border: none !important;
  outline: none;
  background: transparent;
  border-bottom: 1px solid #6f7c87 !important;
}
.search-icon3 {
  width: 15px;
  fill: #6f7c87;
  height: 20px;
  top: 12px;
  left: 0;
  position: absolute;
}
.close-icon3 {
  width: 15px;
  fill: #6f7c87;
  height: 20px;
  position: absolute;
  right: 5px;
  top: 13px;
  cursor: pointer;
}
.input-search-analysis {
  width: 100%;
  margin-top: -31px;
  background: #fff;
}
.fc-list-search-wrapper {
  background: #fff;
}
.rule-list-txt {
  color: #606266;
  line-height: 2.4;
  text-align: justify;
  font-size: 14px;
  padding-left: 20px;
}
.rule-list-txt:hover {
  background: #fbfbfb;
}
.all-rule-btn {
  background: none;
  border: none;
  border-bottom: none;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.5px;
  color: #333333;
  cursor: pointer;
  padding: 0;
}
.all-rule-btn:hover,
.all-rule-btn:focus {
  color: #333333;
  border-color: transparent;
  background-color: transparent;
}
.all-rule-btn .el-icon-arrow-down {
  font-size: 16px;
  color: #333333;
  font-weight: bold;
  padding-left: 7px;
  position: relative;
  top: 2px;
}
.el-popover {
  cursor: pointer;
  padding-left: 0;
  padding-right: 0;
  padding-top: 0;
  padding-bottom: 0;
}
.chart-color-selection {
  width: 20px;
  height: 20px;
  background-color: #aa70ae;
}
.mleft0 {
  margin-left: 0;
}
.mleft447 {
  margin-left: 447px;
}
.selected-portfolio-analytics {
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.4px;
  color: #333333;
  padding-left: 10px;
  padding-right: 10px;
}
.selected-portfolio-analytics .building-popover {
  padding-top: 0;
  padding-bottom: 0;
}
.mleft0 {
  margin-left: 0;
}
.mleft217 {
  margin-left: 217px;
}
.portfolio-customize-container {
  top: 72px !important;
}
.portfolio-analytic-summary .portfolio-add-points-block {
  left: inherit !important;
  position: inherit !important;
}
.portfolio-building-graph-container {
  z-index: 3 !important;
}
.portfolio-analysis-add-points-btn .btn-buildong-selected {
  padding-top: 10px;
  padding-bottom: 0;
  padding-left: 10px;
}
</style>
