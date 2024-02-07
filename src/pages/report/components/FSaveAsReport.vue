<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    custom-class="fc-dialog-right"
    class="trend-dialog headernew"
    :before-close="close"
  >
    <div>
      <div class="dialog-title title">
        {{ $t('common.wo_report.save_as_report') }}
      </div>
      <div v-if="mainReport">
        <div class="heading pT15">{{ $t('common.products.name') }}</div>
        <div>
          <el-input v-model="mainReport.name"></el-input>
        </div>

        <div class="heading pT15">
          {{ $t('common.wo_report.report_description') }}
        </div>
        <div>
          <el-input
            type="textarea"
            :rows="5"
            v-model="mainReport.description"
          ></el-input>
        </div>

        <div class="heading pT15">{{ $t('common._common.x_axislabel') }}</div>
        <div>
          <el-input v-model="mainReport.xAxisLabel"></el-input>
        </div>

        <div class="heading pT15">{{ $t('common._common.y_axis_label') }}</div>
        <div>
          <el-input v-model="mainReport.y1AxisLabel"></el-input>
        </div>

        <div class="heading pT15">{{ $t('common.products.folder') }}</div>
        <div>
          <el-select
            class="report-dropDown"
            v-model="mainReport.parentFolderId"
            :placeholder="$t('common.products.folder')"
          >
            <!-- <el-option v-for="(folder, index) in reportFolders" :key="index" :label="folder.label" :value="folder.id"></el-option> -->
            <el-option
              v-for="(folder, index) in reportFolders"
              :key="index"
              :label="folder.label"
              :value="folder.id"
              v-if="folder.label !== 'Default'"
            ></el-option>
          </el-select>
        </div>

        <template v-if="isDerivationAvailable">
          <div class="heading pT15">
            {{ $t('common.header.start_time_to_calculate') }}
          </div>
          <div class="dervn-starttime">
            <el-date-picker
              v-model="startTime"
              :placeholder="$t('common.wo_report.start_time')"
              :picker-options="dateOptions"
              type="datetime"
              :default-value="'day' | weekly"
            />
          </div>
        </template>

        <div class="heading pT15"></div>
        <div
          v-if="
            config.dataPoints.length > 1 && config.dataPoints[0].period !== 0
          "
        >
          <el-checkbox v-model="saveAsTabularReport">{{
            $t('common._common.save_as_tabular_report')
          }}</el-checkbox>
        </div>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <button type="button" class="modal-btn-cancel" @click="close">
        <!----><!----><span>{{ $t('common._common.cancel') }}</span>
      </button>
      <button type="button" class="modal-btn-save" @click="save">
        <!----><!----><span>{{ $t('common._common._save') }}</span>
      </button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  props: ['visibility', 'config'],
  data() {
    return {
      reportFolders: [],
      mainReport: null,
      subReportList: [],
      saveAsTabularReport: false,
      startTime: null, // For derivation
      dateOptions: {
        disabledDate(time) {
          let today = new Date()
          today.setHours(23, 59, 0, 0)
          return time.getTime() > today.getTime()
        },
      },
    }
  },
  computed: {
    isDerivationAvailable() {
      return (
        this.config &&
        this.config.dataPoints.filter(d => d.type === 'derivation')
      )
    },
  },
  mounted() {
    this.loadReportFolders()
    this.init()
  },
  watch: {
    visibility: function() {
      if (this.visibility) {
        this.init()
      }
    },
  },
  methods: {
    close() {
      this.$emit('update:visibility', false)
    },
    loadReportFolders() {
      let self = this
      self.$http
        .get('/report/workorder/getAllWorkOrderReports?moduleName=energydata')
        .then(function(response) {
          if (response.data.allWorkOrderJsonReports) {
            self.reportFolders = response.data.allWorkOrderJsonReports
          }
        })
    },
    reset() {
      this.mainReport = null
      this.subReportList = []
      this.saveAsTabularReport = false
    },
    init() {
      if (this.config) {
        this.reset()
        for (let i = 0; i < this.config.dataPoints.length; i++) {
          let dataPoint = this.config.dataPoints[i]

          let report = {
            name: dataPoint.name,
            description: dataPoint.name + ' Report',
            parentFolderId: '',
            chartTypeString:
              this.config.chartType === 'combo'
                ? dataPoint.settings.chartType
                : this.config.chartType,
            reportColor: dataPoint.settings
              ? dataPoint.settings.reportColor
              : null,
            isCombinationReport: this.config.chartType === 'combo',
            xAxisLabel: 'Timestamp',
            xAxisField: {
              moduleField: {
                name: 'ttime',
              },
            },
            xAxisaggregateFunction: this.config.period,
            y1AxisaggregateFunction: dataPoint.yAggr,
            groupByField: {
              moduleFieldId: null,
            },
            groupByFieldAggregateFunction: null,
            isHighResolutionReport:
              this.config.period === 0 && dataPoint.yAggr === 0,
            reportUserFilters: [],
            reportThresholds: [],
            dateFilter: {
              operatorId: this.config.dateFilter.operatorId
                ? this.config.dateFilter.operatorId
                : 31,
              value: this.config.dateFilter.value
                ? this.config.dateFilter.value
                : null,
            },
          }

          if (dataPoint.type === 'derivation') {
            let formula = this.$common.getFormulaFieldFromWorkflow() // TODO pass workflow
            formula.name = 'Report_' + dataPoint.name
            let dateRange = this.$helpers.getDateRange('day')
            let derivation = {
              name: dataPoint.name,
              workflowId: dataPoint.workflowId,
              id: dataPoint.id,
              formulaField: formula,
              dateRange: {
                startTime: this.startTime
                  ? this.$helpers.getTimeInOrg(this.startTime)
                  : dateRange[0],
                endTime: dateRange[1],
              },
            }
            report.derivation = derivation
            report.y1AxisLabel = dataPoint.name
          } else {
            report.moduleId =
              dataPoint.readingField.module &&
              dataPoint.readingField.module.moduleId
                ? dataPoint.readingField.module.moduleId
                : dataPoint.readingField.moduleId
            report.criteria = {
              pattern: '(1)',
              conditions: {
                '1': {
                  fieldName: 'parentId',
                  moduleName:
                    dataPoint.readingField.module &&
                    dataPoint.readingField.module.name
                      ? dataPoint.readingField.module.name
                      : 'energydata',
                  operatorId: 36,
                  value: dataPoint.parentId + '',
                },
              },
            }
            report.y1AxisLabel = dataPoint.readingField.displayName
            report.y1AxisField = {
              moduleFieldId: dataPoint.readingField.id,
            }
            report.dateFilter.field = {
              name: 'ttime',
              module: {
                name:
                  dataPoint.readingField.module &&
                  dataPoint.readingField.module.name
                    ? dataPoint.readingField.module.name
                    : 'energydata',
              },
            }
          }

          if (i === 0) {
            this.mainReport = report
            if (this.config.baseLine) {
              this.mainReport.baseLineId = this.config.baseLine.id
            }
          } else {
            this.subReportList.push(report)
          }
        }
      }
    },
    save() {
      let self = this
      if (this.mainReport && !this.mainReport.parentFolderId) {
        this.$message.error(
          this.$t('common._common.please_select_report_folder')
        )
        return
      }

      if (this.subReportList && this.subReportList.length) {
        for (let subReport of this.subReportList) {
          subReport.parentFolderId = null
        }
      }
      if (this.saveAsTabularReport) {
        let reports = []
        this.mainReport.chartType = 14
        this.mainReport.isComparisionReport = true
        reports.push(this.mainReport)

        let cloned = this.$helpers.cloneObject(this.mainReport)
        cloned.parentFolderId = null
        reports.push(cloned)

        for (let subReport of this.subReportList) {
          reports.push(subReport)
        }
        self.$http
          .post('dashboard/addTabularReport?moduleName=energydata', {
            reports: reports,
          })
          .then(function(response) {
            if (response.data.reportContext) {
              let notifyInstance = self.$notify({
                title: this.$t('common._common.report_saved_successfully'),
                message: this.$t(
                  'common._common.click_here_to_open_saved_record'
                ),
                type: 'success',
                duration: 0,
                customClass: 'report-save-success',
                onClick: function() {
                  if (notifyInstance) {
                    notifyInstance.close()
                  }
                  let reportURL =
                    '/app/em/reports/view/' + response.data.reportContext.id
                  self.$router.replace({ path: reportURL })
                },
              })
              self.close()
            }
          })
          .catch(function(error) {
            if (error) {
              self.$message.error(
                this.$t('common._common.oops_this_is_a_error_saving')
              )
            }
          })
      } else {
        this.mainReport.comparingReportContexts = this.subReportList
        self.$http
          .post('dashboard/addReport?moduleName=energydata', {
            reportContext: this.mainReport,
          })
          .then(function(response) {
            if (response.data.reportContext) {
              let notifyInstance = self.$notify({
                title: this.$t('common._common.report_saved_successfully'),
                message: this.$t(
                  'common._common.click_here_to_open_saved_record'
                ),
                type: 'success',
                duration: 0,
                customClass: 'report-save-success',
                onClick: function() {
                  if (notifyInstance) {
                    notifyInstance.close()
                  }
                  let reportURL =
                    '/app/em/reports/view/' + response.data.reportContext.id
                  self.$router.replace({ path: reportURL })
                },
              })
              self.close()
            }
          })
          .catch(function(error) {
            if (error) {
              self.$message.error(
                this.$t('common._common.oops_this_is_a_error_saving')
              )
            }
          })
      }
    },
    cancel() {
      this.$emit('cancel')
    },
  },
}
</script>

<style>
.fc-report-popover-conetnt .header {
  font-size: 12px;
  opacity: 0.8;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.4px;
  text-align: left;
  color: #6b7e91;
}
.saveas-dialog .el-dialog__body {
  padding: 0px;
}

.saveas-dialog .p10 {
  padding: 10px;
}

.saveas-dialog .p5 {
  padding: 5px;
}

.report-save-success {
  cursor: pointer;
}

.report-save-success .el-notification__icon {
  font-size: 20px;
}

.report-save-success .el-notification__group {
  margin-left: 8px;
}

.report-save-success .el-notification__title {
  font-weight: 500;
  font-size: 15px;
}

.report-save-success .el-notification__content {
  text-align: left;
}

.report-save-success .el-notification__content p {
  font-size: 13px;
}
.dervn-starttime .el-date-editor.el-input .el-input__inner {
  padding-left: 30px;
  min-height: 40px;
}
</style>
