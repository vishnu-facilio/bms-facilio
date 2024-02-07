<template>
  <spinner v-if="loading"></spinner>
  <!-- <div class="height100" v-else>
    <div class="row">
      <div class="col-12">
        <div class="new-report-header">
          <div class="pull-left" style="width: auto;">
            <el-input class="report-input-title" v-model="reportForm.name" placeholder="New Report"></el-input>
            <el-input class="report-input-desc" v-model="reportForm.description" placeholder="Description"></el-input>
            <el-select class="report-input-folder" v-model="reportForm.parentFolderId" placeholder="Folder">
              <el-option v-for="(folder, index) in reportFolders" :key="index" :label="folder.label" :value="folder.id"></el-option>
            </el-select>
          </div>
          <div class="pull-right">
            <el-button size="medium" @click="cancel" style="color:#39b2c2;border-color:#39b2c2; letter-spacing: 0.7px;">CANCEL</el-button>
            <el-button size="medium" type="primary" @click="save" >SAVE</el-button>
          </div>
        </div>
      </div>
    </div> -->
  <div class="height100" v-else>
    <div class="row">
      <div class="col-12" style="background: #fff;">
        <div class="new-report-header">
          <div class="pull-left report-title-section">
            <div class="report-field-header">NEW MATRIX REPORT</div>
            <div>
              <el-input
                :autofocus="true"
                class="report-input-title"
                v-model="reportForm.name"
                placeholder="New Report"
                prop="reportName"
              ></el-input>
            </div>
            <div>
              <el-input
                class="report-input-desc"
                v-model="reportForm.description"
                placeholder="Description"
              ></el-input>
            </div>
          </div>
          <div class="pull-right report-btn-section">
            <el-button
              size="medium"
              @click="cancel"
              class="btn-modal-border f13"
              >CANCEL</el-button
            >
            <el-button
              v-if="isNew"
              size="medium"
              type="primary"
              @click="save('save')"
              class="btn-modal-fill f13"
              >SAVE</el-button
            >
            <el-button
              v-if="isEdit"
              size="medium"
              type="primary"
              @click="save('edit')"
              class="btn-modal-fill f13"
              >Update</el-button
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
                  <el-col :md="24" :lg="24">
                    <div class="report-field-header pT25">DATE RANGE</div>
                  </el-col>
                  <el-col :md="12" :lg="12">
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
                  <el-col :md="12" :lg="12">
                    <el-form-item label="Default Range">
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
                  <!-- <el-col :md="9" :lg="9">
                    <el-form-item label="Date Filter" v-if="dateFields">
                      <el-select v-model="dateFilter.fieldId" placeholder="Select field">
                        <el-option v-for="(field, index) in dateFields" :key="index" :label="field.displayName" :value="field.id"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :md="9" :lg="9">
                    <el-form-item label="Operator">
                      <el-select v-model="dateFilter.operatorId" placeholder="Select field">
                        <el-option v-for="(operator, index) in dateOperators" :key="index" :label="operator.label" :value="operator.value"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col> -->
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
                  <el-col :md="24" :lg="24">
                    <div class="report-field-header pT25">Row Headings</div>
                  </el-col>
                  <el-col :md="15" :lg="15">
                    <el-form-item label="Row">
                      <!-- <el-select v-model="reportForm.xAxisField.moduleFieldId" placeholder="Select field" prop="xAxisField">
                        <el-option-group
                        v-for="groupX in groupingXAxisFields"
                        :key="groupX.name"
                        :label="groupX.name">
                          <el-option
                            v-for="field in groupX.group"
                            :key="field.value"
                            :label="field.fieldLabel"
                            :value="field.moduleFieldId">
                        </el-option>
                        </el-option-group> -->
                      <!-- <el-option v-for="(field, index) in reportsFields.xAxisFields" :key="index" :label="field.displayName" :value="field.id"></el-option> -->
                      <!-- </el-select> -->
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
                  <el-col
                    :md="9"
                    :lg="9"
                    v-if="reportForm.xAxisField.moduleFieldId"
                  >
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
                          :label="func.stringValue"
                          :value="func.value"
                          v-if="!func.hasOwnProperty('public') || func.public"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                <!-- <el-row :gutter="30" align="middle">
                  <el-col :md="18" :lg="18">
                    <el-form-item label="Row Headings">
                      <el-select v-model="reportForm.xAxisField.moduleFieldId" placeholder="Select field">
                        <el-option v-for="(field, index) in moduleMeta.fields" :key="index" :label="field.displayName" :value="field.id"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :md="6" :lg="6">
                    <el-form-item label="Function">
                      <el-select v-model="reportForm.xAxisaggregateFunction" placeholder="Select">
                        <el-option v-for="(func, funcidx) in getAggregateFunctions(getXAxisDataType)" :key="funcidx" :label="func._name" :value="func.value"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row> -->
                <el-row :gutter="30" align="middle">
                  <el-col :md="24" :lg="24">
                    <div class="report-field-header pT25">Row Headings</div>
                  </el-col>
                  <el-col :md="15" :lg="15">
                    <el-form-item label="Column Headings">
                      <!-- <el-select v-model="reportForm.groupByField.moduleFieldId" placeholder="Select field">
                      <el-option-group
                        v-for="groupX in groupingXAxisFields"
                        :key="groupX.name"
                        :label="groupX.name">
                        <el-option
                          v-for="field in groupX.group"
                          :key="field.value"
                          :label="field.fieldLabel"
                          :value="field.moduleFieldId">
                        </el-option>
                      </el-option-group> -->
                      <!-- <el-option v-for="(field, index) in reportsFields.groupByFields" :key="index" :label="field.fieldLabel" :value="field.moduleFieldId"></el-option> -->
                      <!-- </el-select> -->
                      <el-select
                        v-model="reportForm.groupByField.moduleFieldId"
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
                  <el-col
                    :md="9"
                    :lg="9"
                    v-if="reportForm.groupByField.moduleFieldId"
                  >
                    <el-form-item label="Function">
                      <el-select
                        v-model="reportForm.groupByFieldAggregateFunction"
                        placeholder="Select"
                      >
                        <el-option
                          v-for="(func, funcidx) in getAggregateFunctions(
                            getGroupByDataType
                          )"
                          :key="funcidx"
                          :label="func._name"
                          :value="func.value"
                          v-if="!func.hasOwnProperty('public') || func.public"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                <!-- <el-row :gutter="30" align="middle">
                  <el-col :md="18" :lg="18">
                    <el-form-item label="Column Headings">
                      <el-select v-model="reportForm.groupByField.moduleFieldId" placeholder="Select field">
                        <el-option v-for="(field, index) in moduleMeta.fields" :key="index" :label="field.displayName" :value="field.id"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :md="6" :lg="6">
                    <el-form-item label="Function">
                      <el-select v-model="reportForm.groupByFieldAggregateFunction" placeholder="Select">
                        <el-option v-for="(func, funcidx) in getAggregateFunctions(getGroupByDataType)" :key="funcidx" :label="func._name" :value="func.value"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row> -->
                <el-row :gutter="30" align="middle">
                  <el-col :md="15" :lg="15">
                    <el-form-item label="Columns to Total">
                      <el-select
                        v-model="reportForm.y1AxisField.moduleFieldId"
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
                  <el-col :md="9" :lg="9">
                    <el-form-item label="Function">
                      <el-select
                        v-model="reportForm.y1AxisaggregateFunction"
                        placeholder="Select"
                      >
                        <el-option
                          v-for="(func, funcidx) in getAggregateFunctions(
                            getYAxisDataType
                          )"
                          :key="funcidx"
                          :label="func._name"
                          :value="func.value"
                        ></el-option>
                      </el-select>
                    </el-form-item>
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
  </div>
</template>
<script>
import FReport from 'pages/report/components/FReport'
import FCriteriaBuilder from '@/criteria/FCriteriaBuilder'
import ReportHelper from 'pages/report/mixins/ReportHelper'
export default {
  mixins: [ReportHelper],
  components: {
    FCriteriaBuilder,
    FReport,
  },
  data() {
    return {
      loading: true,
      module: '',
      moduleMeta: null,
      isNew: true,
      isEdit: false,
      reportFolders: [],
      reportForm: {
        name: '',
        description: '',
        parentFolderId: null,
        chartType: 13,
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
      }
      // else if (!this.dateFilter.fieldId || this.dateFilter.fieldId === '') {
      //   this.$message({
      //     message: 'Date filter is mandatory',
      //     type: 'warning'
      //   })
      //   return
      // }
      else if (
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
      console.log('############# ', self.reportForm)
      self.$http
        .post(
          'dashboard/addReport?moduleName=' + this.getCurrentModule().module,
          { reportContext: this.reportForm }
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
  },
}
</script>
<style>
.report-builder {
  background: white;
  border-right: 1px solid #6666661a;
  overflow-y: scroll;
  padding-bottom: 40px;
  /* min-width: 450px; */
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
/* new report */
.report-field-header {
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.8px;
  text-align: left;
  color: #ef4f8f;
  text-transform: uppercase;
}
.report-input-title {
  width: 300px;
  font-size: 18px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
}
.report-input-title .el-input__inner {
  color: #000000;
}
.report-input-desc {
  line-height: 20px;
  width: 300px;
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: #666666;
}
.report-input-desc {
  color: #666666;
}
.report-input-title.el-input .el-input__inner {
  height: 26px;
}
.report-input-desc.el-input .el-input__inner,
.report-input-title.el-input .el-input__inner {
  border-bottom: 0px;
}
.report-input-desc.el-input .el-input__inner {
  height: 22px;
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: #666666;
}
.report-btn-section {
  margin-top: 40px;
  margin-bottom: 40px;
  margin-right: 20px;
}
.new-report-header {
  height: 113px;
  border-bottom: 1px solid #6666661a;
  display: flex;
  justify-content: space-between;
  flex-wrap: nowrap;
}
.report-title-section {
  display: block;
  padding: 20px 32px;
}
.report-builder .el-tabs__content {
  padding-left: 15px;
  padding-right: 15px;
  padding-bottom: 70px;
}
.report-builder .el-tabs__header {
  margin: 0px;
  background: #f7f8f9;
  padding-top: 15px;
}
.report-builder .el-tabs__item {
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  letter-spacing: 1.1px;
  text-align: left;
  color: #333333;
}
.report-builder .el-tabs__active-bar {
  background-color: #fd4b92;
}
.report-builder .el-tabs__nav-prev,
.report-builder .el-tabs__nav-next {
  display: none;
}
.report-builder .el-tabs__header:hover .el-tabs__nav-prev,
.report-builder .el-tabs__header:hover .el-tabs__nav-next {
  display: block !important;
}
.report-builder .el-form-item__content,
.report-builder .el-form-item__label {
  line-height: 35px;
}
.pT25 {
  padding-top: 20px;
}
.report-builder .el-form-item .el-form-item__label {
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  letter-spacing: 0.4px;
  text-align: left;
  color: #6b7e91;
}
.report-builder .el-tabs__content .el-input__inner {
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
}
.report-builder .el-tabs__nav-wrap.is-scrollable {
  padding: 0 20px;
  box-sizing: border-box;
}
.report-builder .el-tabs__nav-scroll {
  margin-left: 20px;
}
.fc-report {
  background: #fff !important;
}
</style>
