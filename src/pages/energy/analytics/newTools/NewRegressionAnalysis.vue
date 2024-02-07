<template>
  <div class="row">
    <div class="col-12 analytic-summary">
      <iframe
        v-if="exportDownloadUrl"
        :src="exportDownloadUrl"
        style="display: none;"
      ></iframe>
      <div class="analytics-page-header new-heatmap-analytics">
        <div class="row">
          <div class="col-7">
            <div class="analytics-page-header-filters">
              <div class="data-points-container">
                <div class="data-point">
                  <div
                    @click="
                      openEditDataPoint('X-Axis', xAxisDataPoint, 'reading')
                    "
                    title="X-Axis"
                    v-tippy
                  >
                    {{ xAxisDataPoint.name ? xAxisDataPoint.name : 'X-Axis' }}
                  </div>
                </div>
                <span style="opacity: 0.5; margin-right: 8px;">vs</span>
                <div class="data-point">
                  <div
                    @click="
                      openEditDataPoint('Y-Axis', yAxisDataPoint, 'reading')
                    "
                    title="Y-Axis"
                    v-tippy
                  >
                    {{ yAxisDataPoint.name ? yAxisDataPoint.name : 'Y-Axis' }}
                  </div>
                </div>
              </div>
              <f-data-point-adder
                v-show="false"
                ref="readingAdder"
                reading="TOTAL_ENERGY_CONSUMPTION_DELTA"
                type="reading"
              ></f-data-point-adder>
              <el-dialog
                width="300px"
                class="add-data-point-dialog"
                :title="dialogTitle"
                :visible.sync="addDataPointFormVisibility"
              >
                <f-add-data-point
                  :type="dataPointType"
                  ref="addDataPointForm"
                  @save="readingColumnAdded"
                  @cancel="readingColumnClosed"
                ></f-add-data-point>
              </el-dialog>
            </div>
          </div>
          <div class="col-5" style="text-align: right;">
            <el-button
              class="freport-btn analytics-save-as-report new-analytics-Rbtn"
              :disabled="isActionsDisabled"
              @click="visibility.saveAsDialog = true"
              >{{ $t('common.wo_report.save_as_report') }}</el-button
            >
            <div class="analytics-page-options new-analytics-Rbtn">
              <el-dropdown
                @command="exportData($event, analyticsConfig, iframeLoader)"
                :class="{ 'action-disabled': isActionsDisabled }"
              >
                <el-button size="small" type="text" class="fc-chart-btn">
                  <img class="report-icon" src="~statics/report/export.svg" />
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="1" :disabled="isActionsDisabled">{{
                    $t('common.wo_report.export_csv')
                  }}</el-dropdown-item>
                  <el-dropdown-item command="2" :disabled="isActionsDisabled">{{
                    $t('common.wo_report.export_xcl')
                  }}</el-dropdown-item>
                  <!-- <el-dropdown-item command="3">As PDF</el-dropdown-item>
              <el-dropdown-item command="4">As Image</el-dropdown-item> -->
                </el-dropdown-menu>
              </el-dropdown>
              <el-button
                size="small"
                :disabled="isActionsDisabled"
                type="text"
                :title="$t('common.wo_report.email_this_report')"
                data-position="bottom"
                data-arrow="true"
                v-tippy
                class="fc-chart-btn"
                @click="visibility.emailReport = true"
              >
                <img class="report-icon" src="~statics/report/email.svg" />
              </el-button>
              <el-button
                size="small"
                :disabled="isActionsDisabled"
                type="text"
                @click="printReport"
                title="Print"
                data-position="bottom"
                data-arrow="true"
                v-tippy
                class="fc-chart-btn"
              >
                <img class="report-icon" src="~statics/report/printer.svg" />
              </el-button>
              <email-report
                :visibility.sync="visibility.emailReport"
                :analyticsConfig="analyticsConfig"
              ></email-report>
              <f-save-as-report
                :visibility.sync="visibility.saveAsDialog"
                :config="analyticsConfig"
              ></f-save-as-report>
            </div>
          </div>
        </div>
      </div>
      <div
        ref="chartSection"
        class="self-center fchart-section scrollable regression-graph-container"
      >
        <div v-if="!analyticsConfig.dataPoints.length" class="text-center">
          <div class="p15">
            Please select an X-Axis and Y-Axis reading to analyze.
          </div>
        </div>
        <f-analytic-report
          :config.sync="analyticsConfig"
          v-else
        ></f-analytic-report>
      </div>
    </div>
  </div>
</template>
<script>
import FDataPointAdder from 'pages/energy/analytics/components/FDataPointAdder'
import FAnalyticReport from 'pages/energy/analytics/components/FAnalyticReport'
import FAddDataPoint from 'pages/energy/analytics/components/FAddDataPoint'
import FSaveAsReport from 'pages/report/components/FSaveAsReport'
import EmailReport from 'pages/report/forms/EmailReport'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'

export default {
  mixins: [AnalyticsMixin],
  components: {
    FDataPointAdder,
    FAnalyticReport,
    FSaveAsReport,
    EmailReport,
    FAddDataPoint,
  },
  data() {
    return {
      analyticsConfig: {
        name: 'Regression Analysis',
        period: 12,
        regression: true,
        dateFilter: this.getDefaultDateFilter('M'),
        dataPoints: [],
        chartType: 'regression',
        chartTypeChangable: false,
      },
      xAxisDataPoint: {
        buildingId: '',
        readingFieldId: '',
        readingField: null,
        parentId: '',
        parent: null,
        parentType: 'space',
        name: '',
        yAggr: 3,
      },
      yAxisDataPoint: {
        buildingId: '',
        readingFieldId: '',
        readingField: null,
        parentId: '',
        parent: null,
        parentType: 'space',
        name: '',
        yAggr: 3,
      },
      addDataPointFormVisibility: false,
      dialogTitle: '',
      editDataPoint: '',
      dataPointType: 'reading',
      visibility: {
        saveAsDialog: false,
        emailReport: false,
      },
      exportDownloadUrl: null,
    }
  },
  mounted() {},
  methods: {
    openEditDataPoint(name, dataPoint, dataPointType) {
      let self = this
      self.addDataPointFormVisibility = true
      self.dialogTitle = 'Edit ' + name
      self.dataPointType = dataPointType
      self.editDataPoint = name.toLowerCase()
      this.$nextTick(function() {
        self.$refs.addDataPointForm.open(dataPoint)
      })
    },
    readingColumnAdded(dataPoint) {
      let readingReport = {
        buildingId: dataPoint.buildingId,
        readingFieldId: dataPoint.readingField.id,
        readingField: dataPoint.readingField,
        parentId: dataPoint.space ? dataPoint.space.id : dataPoint.asset.id,
        parent: dataPoint.space ? dataPoint.space : dataPoint.asset,
        parentType: dataPoint.space ? 'space' : 'asset',
        name: dataPoint.name,
        yAggr: dataPoint.aggregateFunc,
      }

      if (this.editDataPoint === 'x-axis') {
        this.xAxisDataPoint = readingReport
      } else if (this.editDataPoint === 'y-axis') {
        this.yAxisDataPoint = readingReport
      }
      if (
        this.xAxisDataPoint.readingFieldId &&
        this.yAxisDataPoint.readingFieldId
      ) {
        this.analyticsConfig.dataPoints = [
          this.xAxisDataPoint,
          this.yAxisDataPoint,
        ]
      }
      this.addDataPointFormVisibility = false
    },
    readingColumnClosed() {
      this.addDataPointFormVisibility = false
    },
    iframeLoader(url) {
      this.exportDownloadUrl = url
    },
  },
}
</script>
<style>
.charttype-options {
  /* padding: 4px 10px;
    margin: 0;
    list-style: none;
    float: left; */
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
  /* padding-top: 15px; */
  /* padding-right: 23px; */
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
/**/
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
}
.analytics-page-header-filters .el-select input {
  border-radius: 3px;
  border: solid 1px #8fd2db;
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #31a4b4;
  padding: 8px;
  height: 40px;
  line-height: 40px;
}
.analytics-page-header-filters .period-select {
  width: 90px;
}
.analytics-page-options {
  margin: 10px;
  background-color: #ffffff;
  box-shadow: 0 2px 4px 0 rgba(230, 230, 230, 0.5);
  border: solid 1px #d9e0e7;
  border-radius: 5px;
}
.analytics-page-options {
  display: inline-block;
  height: 40px;
  margin-left: 20px;
  position: relative;
  top: 4px !important;
}
.analytics-page-options button,
.analytics-page-options button:hover,
.analytics-page-options button:focus {
  border-left: 1px solid rgb(217, 224, 231);
}
.analytics-page-options .el-button + .el-button {
  margin-left: 0px;
}
.analytics-page-options button:first-child {
  border: none !important;
}
.analytics-page-options .report-icon {
  width: 17px;
  height: 17px;
}
.analytics-page-options .fc-chart-btn {
  color: #333333;
  font-size: 17px;
  padding: 9px;
  padding-left: 15px;
  padding-right: 12px;
  margin-left: 0px;
}
.freport-btn:hover {
  background-color: #f5f7fa;
}
.analytics-save-as-report,
.analytics-save-as-report:hover,
.analytics-save-as-report:focus {
  font-weight: 500;
}
.regression-graph-container {
  margin: 20px;
}
</style>
