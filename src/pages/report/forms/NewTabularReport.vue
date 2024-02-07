<template>
  <spinner v-if="loading"></spinner>
  <div class="height100" v-else>
    <div class="row">
      <div class="col-12">
        <div class="new-report-header">
          <div class="pull-left" style="width: auto;">
            <el-input
              class="report-input-title"
              v-model="reportForm.name"
              placeholder="New Report"
            ></el-input>
            <el-input
              class="report-input-desc"
              v-model="reportForm.description"
              placeholder="Description"
            ></el-input>
            <el-select
              class="report-input-folder"
              v-model="reportForm.parentFolderId"
              placeholder="Folder"
            >
              <el-option
                v-for="(folder, index) in reportFolders"
                :key="index"
                :label="folder.label"
                :value="folder.id"
              ></el-option>
            </el-select>
          </div>
          <div class="pull-right">
            <el-button
              size="medium"
              @click="cancel"
              style="color:#39b2c2;border-color:#39b2c2; letter-spacing: 0.7px;"
              >CANCEL</el-button
            >
            <el-button size="medium" type="primary" @click="save"
              >SAVE</el-button
            >
          </div>
        </div>
      </div>
    </div>
    <div class="row height100 scrollable">
      <div class="col-4">
        <div class="report-builder">
          <el-form
            ref="newReportForm"
            @submit.prevent="save"
            class="fc-form"
            :label-position="'top'"
            :model="reportForm"
          >
            <el-tabs :tab-position="'top'">
              <el-tab-pane label="BUILD">
                <el-row :gutter="30" align="middle">
                  <el-col :md="9" :lg="9">
                    <el-form-item label="Date Filter" v-if="dateFields">
                      <el-select
                        v-model="dateFilter.fieldId"
                        placeholder="Select field"
                      >
                        <el-option
                          v-for="(field, index) in dateFields"
                          :key="index"
                          :label="field.displayName"
                          :value="field.id"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :md="9" :lg="9">
                    <el-form-item label="Operator">
                      <el-select
                        v-model="dateFilter.operatorId"
                        placeholder="Select field"
                      >
                        <el-option
                          v-for="(operator, index) in dateOperators"
                          :key="index"
                          :label="operator.label"
                          :value="operator.value"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :md="6" :lg="6">
                    <el-form-item
                      label="Custom"
                      v-show="dateFilter.operatorId === 20"
                    >
                      <el-date-picker
                        v-model="dateFilter.dateRange"
                        :type="'datetimerange'"
                      ></el-date-picker>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="30" align="middle">
                  <el-col :md="18" :lg="18">
                    <el-form-item label="Row Headings">
                      <el-select
                        v-model="reportForm.xAxisField.moduleFieldId"
                        placeholder="Select field"
                      >
                        <el-option
                          v-for="(field, index) in moduleMeta.fields"
                          :key="index"
                          :label="field.displayName"
                          :value="field.id"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :md="6" :lg="6">
                    <el-form-item label="Function">
                      <el-select
                        v-model="reportForm.xAxisaggregateFunction"
                        placeholder="Select"
                      >
                        <el-option
                          v-for="(func, funcidx) in getAggregateFunctions(
                            getXAxisDataType
                          )"
                          :key="funcidx"
                          :label="func._name"
                          :value="func.value"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="30" align="middle">
                  <el-col
                    :md="24"
                    :lg="24"
                    style="margin-top: 30px; font-weight: 450; color: #6b7e91;"
                  >
                    Columns
                  </el-col>
                  <el-col :md="24" :lg="24">
                    <table width="100%" class="tabular-columns-list">
                      <tr v-for="(column, index) in columns" :key="index">
                        <td>
                          <el-input
                            v-model="column.data.name"
                            placeholder="Column Name"
                          ></el-input>
                        </td>
                        <td>
                          <el-select
                            v-model="column.data.aggregateFunc"
                            placeholder="Aggregate"
                          >
                            <el-option
                              v-for="(func, funcidx) in aggregateFunctions"
                              :key="funcidx"
                              :label="func.label"
                              :value="func.value"
                            >
                            </el-option>
                          </el-select>
                        </td>
                        <td>
                          <span class="pointer"
                            ><i class="el-icon-close"></i
                          ></span>
                        </td>
                      </tr>
                    </table>
                  </el-col>
                  <el-col :md="24" :lg="24" class="mT30">
                    <a>Add Column</a> /
                    <a @click="addReadingColumn">Add Reading Column</a>
                  </el-col>
                </el-row>
              </el-tab-pane>
              <el-tab-pane label="FILTERS">
                <f-criteria-builder
                  v-model="reportForm.criteria"
                  :module="moduleMeta.module"
                ></f-criteria-builder>
              </el-tab-pane>
            </el-tabs>
          </el-form>
        </div>
      </div>
      <div class="col-8">
        <div class="report-preview text-center">
          <el-button
            type="info"
            v-if="showGeneratePreview"
            @click="generatePreview"
            >Generate Report Preview</el-button
          >
          <f-report
            v-if="previewReportContext"
            :config="{ id: -1, report: previewReportContext }"
          ></f-report>
        </div>
      </div>
    </div>
    <el-dialog
      width="300px"
      class="compare-dialog"
      title="Add Reading Column"
      :visible.sync="addReadingColumnDialogVisibility"
    >
      <f-compare-report
        ref="addReadingColumnReportForm"
        @save="readingColumnAdded"
        @cancel="readingColumnClosed"
      ></f-compare-report>
    </el-dialog>
  </div>
</template>
<script>
import FReport from 'pages/report/components/FReport'
import FCriteriaBuilder from '@/criteria/FCriteriaBuilder'
import ReportHelper from 'pages/report/mixins/ReportHelper'
import FCompareReport from 'pages/report/components/FCompareReport'
export default {
  mixins: [ReportHelper],
  components: {
    FCriteriaBuilder,
    FReport,
    FCompareReport,
  },
  data() {
    return {
      loading: true,
      module: '',
      moduleMeta: null,
      reportFolders: [],
      reportForm: {
        name: '',
        description: '',
        parentFolderId: null,
        chartType: 14,
        xAxisField: {
          moduleFieldId: null,
        },
        xAxisaggregateFunction: null,
        y1AxisField: {
          moduleFieldId: null,
        },
        y1AxisaggregateFunction: null,
        groupByField: {
          moduleFieldId: null,
        },
        groupByFieldAggregateFunction: null,
        criteria: null,
        reportUserFilters: [],
        reportThresholds: [],
      },
      columns: [],
      addReadingColumnDialogVisibility: false,
      dateFilter: {
        fieldId: '',
        operatorId: '',
        dateRange: null,
      },
      energyMeterFilter: {
        buildingId: '',
        serviceId: '',
        subMeterId: '',
        groupBy: '',
      },
      subMeters: [],
      energyGroupBy: [
        {
          label: 'By Building',
          value: 'building',
        },
        {
          label: 'By Service',
          value: 'service',
        },
      ],
      dateOperators: [
        {
          label: 'Today',
          value: 22,
        },
        {
          label: 'Yesterday',
          value: 25,
        },
        {
          label: 'This Week',
          value: 31,
        },
        {
          label: 'Last Week',
          value: 30,
        },
        {
          label: 'This Month',
          value: 28,
        },
        {
          label: 'Last Month',
          value: 27,
        },
        {
          label: 'This Year',
          value: 44,
        },
        {
          label: 'Last Year',
          value: 45,
        },
        {
          label: 'Custom',
          value: 20,
        },
      ],
      thresholdLineStyles: [
        {
          label: 'Default',
          value: 3,
        },
        {
          label: 'Dotted',
          value: 2,
        },
        {
          label: 'Dashed',
          value: 1,
        },
      ],
      aggregateFunctions: [
        {
          label: 'Actual',
          value: 0,
        },
        {
          label: 'Sum',
          value: 3,
        },
        {
          label: 'Avg',
          value: 2,
        },
        {
          label: 'Min',
          value: 4,
        },
        {
          label: 'Max',
          value: 5,
        },
      ],
      showGeneratePreview: true,
      previewReportContext: null,
    }
  },
  created() {
    this.$store.dispatch('loadEnergyMeters')
  },
  mounted() {
    this.loadModuleMeta()
  },
  computed: {
    getXAxisDataType() {
      if (
        this.reportForm.xAxisField.moduleFieldId &&
        this.reportForm.xAxisField.moduleFieldId !== ''
      ) {
        let field = this.moduleMeta.fields.find(
          field =>
            field.id === parseInt(this.reportForm.xAxisField.moduleFieldId)
        )
        return field.dataTypeEnum._name
      }
      return null
    },
    getYAxisDataType() {
      if (
        this.reportForm.y1AxisField.moduleFieldId &&
        this.reportForm.y1AxisField.moduleFieldId !== ''
      ) {
        let field = this.moduleMeta.fields.find(
          field =>
            field.id === parseInt(this.reportForm.y1AxisField.moduleFieldId)
        )
        return field.dataTypeEnum._name
      }
      return null
    },
    getGroupByDataType() {
      if (
        this.reportForm.groupByField.moduleFieldId &&
        this.reportForm.groupByField.moduleFieldId !== ''
      ) {
        let field = this.moduleMeta.fields.find(
          field =>
            field.id === parseInt(this.reportForm.groupByField.moduleFieldId)
        )
        return field.dataTypeEnum._name
      }
      return null
    },
    dateFields() {
      if (this.moduleMeta) {
        return this.moduleMeta.fields.filter(
          fld => fld.dataTypeEnum._name.indexOf('DATE') >= 0
        )
      }
      return null
    },
  },
  methods: {
    loadModuleMeta() {
      let self = this
      if (this.getCurrentModule().module) {
        self.loading = true
        let model = this.getCurrentModule().module
        console.log('model', model)
        self.$http
          .get('/module/meta?moduleName=' + this.getCurrentModule().module)
          .then(function(response) {
            self.moduleMeta = response.data.meta
            self.moduleMeta.module = model
            self.loading = false
          })
        self.loadReportFolders()
      }
    },
    loadReportFolders() {
      let self = this
      self.$http
        .get(
          '/report/workorder/getAllWorkOrderReports?moduleName=' +
            this.getCurrentModule().module
        )
        .then(function(response) {
          if (response.data.allWorkOrderJsonReports) {
            self.reportFolders = response.data.allWorkOrderJsonReports
          }
        })
    },
    loadSubMeters() {
      let self = this
      let url =
        '/dashboard/getSubMeters?buildingId=' +
        this.energyMeterFilter.buildingId
      if (
        this.energyMeterFilter.serviceId &&
        this.energyMeterFilter.serviceId !== ''
      ) {
        url += '&serviceId=' + this.energyMeterFilter.serviceId
      }
      self.$http.get(url).then(function(response) {
        self.subMeters = response.data.energyMeters
      })
    },
    generatePreview() {
      if (
        this.reportForm.xAxisField.moduleFieldId &&
        this.reportForm.xAxisField.moduleFieldId !== ''
      ) {
        this.previewReportContext = this.$helpers.cloneObject(this.reportForm)
        this.showGeneratePreview = false
      } else {
        this.$message({
          message: 'X-Axis field is required to preview chart.',
          type: 'warning',
        })
      }
    },
    getAggregateFunctions(dataType) {
      if (dataType) {
        if (dataType === 'DATE' || dataType === 'DATE_TIME') {
          return this.moduleMeta.reportOperators.DateAggregateOperator
        } else if (dataType === 'NUMBER' || dataType === 'DECIMAL') {
          return this.moduleMeta.reportOperators.NumberAggregateOperator
        } else {
          return this.moduleMeta.reportOperators.StringAggregateOperator
        }
      }
      return []
    },
    addThreshold() {
      this.reportForm.reportThresholds.push({
        name: '',
        value: '',
        color: '',
        lineStyle: 3,
      })
    },
    removeThreshold(index) {
      this.reportForm.reportThresholds.splice(index, 1)
    },
    cancel() {
      this.$router.push({ path: this.getCurrentModule().rootPath })
    },
    save() {
      if (this.reportForm.name.trim() === '') {
        this.$message({
          message: "Report name shoudn't be empty",
          type: 'warning',
        })
        return
      } else if (
        !this.reportForm.xAxisField.moduleFieldId ||
        this.reportForm.xAxisField.moduleFieldId === ''
      ) {
        this.$message({
          message: 'X-Axis is mandatory',
          type: 'warning',
        })
        return
      }

      let self = this
      let formattedUserFilters = []
      for (let filterFldId of this.reportForm.reportUserFilters) {
        formattedUserFilters.push({
          reportFieldContext: {
            moduleFieldId: filterFldId,
          },
        })
      }
      this.reportForm.reportUserFilters = formattedUserFilters
      if (this.dateFilter.fieldId && this.dateFilter.fieldId !== '') {
        this.reportForm.dateFilter = {
          fieldId: this.dateFilter.fieldId,
          operatorId: this.dateFilter.operatorId,
        }
        if (this.dateFilter.operatorId === 20) {
          this.reportForm.dateFilter.val =
            this.dateFilter.dateRange[0].getTime() +
            ',' +
            this.dateFilter.dateRange[1].getTime()
        }
      }
      let energyMeter = {}
      if (
        this.energyMeterFilter.buildingId &&
        this.energyMeterFilter.buildingId !== ''
      ) {
        energyMeter.buildingId = this.energyMeterFilter.buildingId
      }
      if (
        this.energyMeterFilter.serviceId &&
        this.energyMeterFilter.serviceId !== ''
      ) {
        energyMeter.serviceId = this.energyMeterFilter.serviceId
      }
      if (
        this.energyMeterFilter.subMeterId &&
        this.energyMeterFilter.subMeterId !== ''
      ) {
        energyMeter.subMeterId = this.energyMeterFilter.subMeterId
      }
      if (
        this.energyMeterFilter.groupBy &&
        this.energyMeterFilter.groupBy !== ''
      ) {
        energyMeter.groupBy = this.energyMeterFilter.groupBy
      }
      if (Object.keys(energyMeter).length > 0) {
        this.reportForm.energyMeter = energyMeter
      }

      let reports = []
      self.reportForm.moduleName = this.getCurrentModule().module
      reports.push(self.reportForm)
      for (let column of self.columns) {
        let reportObj = {
          name: column.data.name,
          description: column.data.name,
          parentFolderId: null,
          moduleId: column.data.readingField.moduleId,
          chartType: 1,
          xAxisField: {
            moduleField: {
              name: 'ttime',
            },
          },
          xAxisaggregateFunction: self.reportForm.xAxisaggregateFunction,
          y1AxisField: {
            moduleFieldId: column.data.readingField.id,
          },
          y1AxisaggregateFunction: column.data.aggregateFunc,
          groupByField: {
            moduleFieldId: null,
          },
          groupByFieldAggregateFunction: null,
          criteria: {
            pattern: '(1)',
            conditions: {
              '1': {
                fieldName: 'parentId',
                moduleName: column.data.readingField.module.name,
                operatorId: 36,
                value:
                  (column.data.space
                    ? column.data.space.id
                    : column.data.asset.id) + '',
              },
            },
          },
          reportUserFilters: [],
          reportThresholds: [],
        }
        if (column.data.baseLineId) {
          reportObj.baseLineId = column.data.baseLineId
        }
        reports.push(reportObj)
      }

      self.$http
        .post(
          'dashboard/addTabularReport?moduleName=' +
            this.getCurrentModule().module,
          { reports: reports }
        )
        .then(function(response) {
          if (response.data.reportContext) {
            self.$message({
              message: 'Report saved successfully.',
              type: 'success',
            })

            let reportURL =
              self.getCurrentModule().rootPath +
              '/view/' +
              response.data.reportContext.id
            self.$router.replace({ path: reportURL })
          } else {
            self.$message({
              message: 'Report creation failed',
              type: 'warning',
            })
          }
        })
    },
    saveReadingColumn(reportContext, data) {
      let params = {
        readingFieldId: data.readingField.id,
        parentId: data.space ? data.space.id : data.asset.id,
        reportName: data.name,
        xAggr: reportContext.XAxisAggregateOpperator.value,
        yAggr: data.aggregateFunc,
        reportEntityId: reportContext.reportEntityId,
      }

      let self = this
      self.$http
        .post('dashboard/addComparisionReport?moduleName=energydata', params)
        .then(function(response) {})
    },
    addReadingColumn() {
      this.addReadingColumnDialogVisibility = true
      if (this.$refs['addReadingColumnReportForm']) {
        this.$refs['addReadingColumnReportForm'].open()
      }
    },
    readingColumnAdded(data) {
      this.columns.push({
        type: 'reading',
        data: data,
      })
      this.addReadingColumnDialogVisibility = false
    },
    readingColumnClosed() {
      this.addReadingColumnDialogVisibility = false
    },
  },
}
</script>
<style>
.new-report-header {
  padding: 20px 20px 54px 20px;
  border-bottom: 1px solid #6666661a;
  background: white;
}

.report-input-title {
  width: 300px;
  font-size: 18px;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
}

.report-input-desc {
  width: 300px;
  margin: 0px 20px;
}

.report-input-folder {
  width: 200px !important;
  margin: 0px 20px;
}

.report-builder {
  background: white;
  border-right: 1px solid #6666661a;
  padding: 5px 20px 20px 20px;
  height: 100%;
  overflow-y: scroll;
}

.el-form--label-top .el-form-item__label {
  padding-bottom: 0px !important;
}

.el-select {
  width: 100%;
}

.report-preview {
  padding: 30px;
}

.report-builder .f-criteria-builder .box-card {
  width: 100%;
}

.tabular-columns-list td {
  padding: 10px;
}
</style>
