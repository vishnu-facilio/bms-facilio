<template>
  <el-dialog
    custom-class="f-kfi-card-builder fc-dialog-center-container kpi-map-setup-dilaog"
    :append-to-body="true"
    :visible.sync="visibility"
    :width="'50%'"
    title="KPI CARD"
    :before-close="closedialog"
  >
    <span slot="title" class="dialog-footer">
      <span class="kpi-card-header">SMART MAP</span>
    </span>
    <div style="height:500px;">
      <el-form :model="data" ref="FCUCARD" :label-position="'top'">
        <el-form-item prop="category">
          <el-col :span="19">
            <p class="grey-text2 kpi-text-3 ">Name</p>
            <el-input
              style="width: 100%"
              :autofocus="true"
              class="addReading-title el-input-textbox-full-border"
              v-model="data.name"
              placeholder="Name"
            ></el-input>
          </el-col>
        </el-form-item>
        <el-form-item prop="moduleName">
          <el-col :span="19">
            <p class="grey-text2 kpi-text-3 ">Module Name</p>
            <el-select
              @change="setmoduleName(data.module)"
              v-model="data.module"
              filterable
              class="fc-input-full-border-select2  p0"
            >
              <el-option
                v-for="(m, mIdx) in getLicenseEnabledmoduleNames"
                :key="mIdx"
                :value="m.moduleName"
                :label="m.label"
              ></el-option>
            </el-select>
          </el-col>
        </el-form-item>
      </el-form>
      <div v-if="loading">
        <spinner
          v-if="loading"
          :show="loading"
          size="80"
          class="analytics-spinner"
        ></spinner>
      </div>
      <template>
        <el-form
          v-for="(expression, index) in data.expressions"
          :model="expression"
          ref="FCUCARD"
          :label-position="'top'"
          :key="index"
          class="criteria-box"
        >
          <el-form-item prop="category">
            <el-col
              :span="2"
              :offset="1"
              v-if="expression.aggregateString !== 'count'"
            >
              <div class="fc-input-div-full-border-f14-1 pointer mT25">
                <el-popover
                  :disabled="!showMetricAggregation(metric)"
                  v-model="toggleMetricAggregation"
                  placement="bottom"
                  width="70"
                  trigger="click"
                  popper-class="metric-popover"
                >
                  <div
                    class="pointer pT5 pB5 fc-label-hover label-txt-black f14 text-center"
                    @click="
                      setMetricAggregation(
                        metricAggr,
                        expression.aggregate,
                        index
                      )
                    "
                    v-for="(metricAggr, metricAggrIdx) in metricAggregation"
                    :value="metricAggr.value"
                    :key="metricAggrIdx"
                  >
                    {{ metricAggr.label }}
                  </div>
                  <img
                    :disabled="!showMetricAggregation(metric)"
                    src="~assets/summation_img.svg"
                    class="p15"
                    slot="reference"
                  />
                </el-popover>
              </div>
            </el-col>
          </el-form-item>
          <el-form-item prop="category">
            <div class="height100">
              <new-criteria-builder
                @moduleObj="getModuleList"
                class="report-criteria-buuilder"
                :exrule="data.expressions[0].criteria"
                @condition="getCriteria"
                :showSiteField="true"
                :module="data.module"
                :title="
                  $t('setup.users_management.specify_rules_for_assigment_rules')
                "
              ></new-criteria-builder>
            </div>
          </el-form-item>
        </el-form>
      </template>
      <map-marker
        :data="data"
        class="criteria-box mT20"
        :marker="data.marker"
        :markerValue="data.markerValue"
        :moduleFieldList="moduleFieldList"
      ></map-marker>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closedialog">Close</el-button>
      <el-button
        type="primary"
        class="modal-btn-save"
        @click="save('update')"
        v-if="kpiCard"
        >Update</el-button
      >
      <el-button type="primary" class="modal-btn-save" @click="save()" v-else
        >Save</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import NewCriteriaBuilder from 'src/components/NewCriteriaBuilder'
import DateHelper from '@/mixins/DateHelper'
import MapMarker from '@/MapMarkerSettings'
export default {
  props: ['visibility', 'kpiCard'],
  mixins: [DateHelper],
  data() {
    return {
      moduleFieldList: [],
      moduleNames: [
        {
          label: 'Asset',
          moduleName: 'asset',
          license: 'SPACE_ASSET',
        },
        {
          label: 'Site',
          moduleName: 'site',
          license: 'ENERGY',
        },
        {
          label: 'Building',
          module: 'building',
          license: 'ENERGY',
        },
      ],
      metricAggregation: [
        {
          label: 'AVG',
          value: 'avg',
        },
        {
          label: 'SUM',
          value: 'sum',
        },
        {
          label: 'MIN',
          value: 'min',
        },
        {
          label: 'MAX',
          value: 'max',
        },
      ],
      loading: false,
      toggleMetricAggregation: false,
      data: {
        name: '',
        locationField: 'location',
        module: 'asset',
        metrics: [],
        timeFields: [],
        selectedTimeField: null,
        operatorId: null,
        metricAgg: null,
        marker: {
          style: {
            color: '#EC598C',
          },
          action: {
            type: 1,
            fuction: null,
            url: null,
            popover: null,
          },
          value: null,
          conditionalFormatting: [
            {
              output: 'OUTPUT',
              operatorId: 9,
              value: null,
              style: {
                icon: '',
                color: '#933F95',
              },
            },
          ],
        },
        readingObj: {
          categoryId: null,
          field: null,
          period: 'Today',
          aggregation: 'sum',
          moduleName: 'energydata',
        },
        userFilter: {
          field: [],
        },
        markerValue: {
          type: null,
          id: 21323,
        },
        expressions: [
          {
            name: null,
            actualName: null,
            fieldName: 'id',
            aggregateString: 'count',
            aggregate: null,
            moduleName: 'asset',
            criteria: {
              pattern: '(1)',
              conditions: {
                '1': {
                  fieldName: '__vue_devtool_undefined__',
                  value: '__vue_devtool_undefined__',
                  columnName: '__vue_devtool_undefined__',
                  operatorId: '__vue_devtool_undefined__',
                },
              },
              resourceOperator: false,
            },
          },
        ],
      },
      showCriteriaBuilder: false,
      selectedIndex: 0,
    }
  },
  components: {
    NewCriteriaBuilder,
    MapMarker,
  },
  mounted() {
    this.loadData()
  },
  computed: {
    getLicenseEnabledmoduleNames() {
      return this.moduleNames.filter(
        moduleName =>
          this.$helpers.isLicenseEnabled(moduleName.license) === true
      )
    },
    metric() {
      if (this.data && this.data.aggregateString) {
        return this.data.metrics.find(
          rt => rt.name === this.data.aggregateString
        )
      } else {
        return this.data.metrics.length ? this.data.metrics[0] : null
      }
    },
  },
  methods: {
    getModuleList(data) {
      this.moduleFieldList = data
    },
    deforamtData(data) {
      if (data && data.selectedTimeField) {
        if (Object.keys(data.expressions[0].criteria.conditions).length) {
          let serachValue =
            ' and ' +
            Object.keys(data.expressions[0].criteria.conditions)[
              Object.keys(data.expressions[0].criteria.conditions).length - 2
            ] +
            ' and ' +
            Object.keys(data.expressions[0].criteria.conditions)[
              Object.keys(data.expressions[0].criteria.conditions).length - 1
            ] +
            ')'
          let newPattern = data.expressions[0].criteria.pattern.replace(
            serachValue,
            ')'
          )
          data.expressions[0].criteria.pattern = newPattern
          Object.keys(data.expressions[0].criteria.conditions).forEach(key => {
            let lastValue = Object.keys(
              data.expressions[0].criteria.conditions
            )[Object.keys(data.expressions[0].criteria.conditions).length - 1]
            let lastBeforeValue = Object.keys(
              data.expressions[0].criteria.conditions
            )[Object.keys(data.expressions[0].criteria.conditions).length - 2]
            if (key === lastValue || key === lastBeforeValue) {
              delete data.expressions[0].criteria.conditions[key]
            }
          })
        }
        return data
      }
      return data
    },
    loadData() {
      if (this.kpiCard) {
        this.data = this.deforamtData(this.kpiCard)
        if (!this.data.hasOwnProperty('moduleNameName')) {
          this.data.module = 'workorder'
        }
      }
      this.loadFields()
    },
    closedialog() {
      this.$emit('update:visibility', false)
      this.$emit('close', false)
    },
    setMertic() {},
    setmoduleName(moduleName) {
      if (
        this.$helpers.isLicenseEnabled('NEWALARM') &&
        this.data.module === 'alarm'
      ) {
        moduleName = 'newreadingalarm'
      }
      this.loadDefaultData(moduleName)
      this.data.expressions[0].moduleName = moduleName
      this.loadFields()
    },
    loadDefaultData(moduleName) {
      this.data = {
        name: '',
        moduleName: moduleName ? moduleName : 'workorder',
        metrics: [],
        timeFields: [],
        selectedTimeField: null,
        operatorId: null,
        metricAgg: null,
        expressions: [
          {
            name: null,
            actualName: null,
            fieldName: 'id',
            aggregateString: 'count',
            aggregate: null,
            moduleName: 'workorder',
            criteria: {
              pattern: '(1)',
              conditions: {
                '1': {
                  fieldName: '__vue_devtool_undefined__',
                  value: '__vue_devtool_undefined__',
                  columnName: '__vue_devtool_undefined__',
                  operatorId: '__vue_devtool_undefined__',
                },
              },
              resourceOperator: false,
            },
          },
        ],
      }
    },
    showMetricAggregation(metric) {
      if (metric && metric.defaultMetric) {
        this.toggleMetricAggregation = false
        return false
      } else {
        return true
      }
    },
    validateData() {
      if (this.data && this.data.expressions.length) {
        if (this.data.expressions[0].actualName === null) {
          return false
        } else if (
          this.data.expressions[0].criteria &&
          this.data.expressions[0].criteria.conditions &&
          Object.keys(this.data.expressions[0].criteria.conditions).length &&
          this.data.expressions[0].criteria.conditions[1].columnName !==
            undefined
        ) {
          return true
        } else {
          return false
        }
      } else {
        return false
      }
    },
    setMetricAggregation(agg, data, index) {
      this.data.expressions[0].actualName = this.data.name
      this.data.expressions[index].fieldName = agg.value
      this.toggleMetricAggregation = false
    },
    save(update) {
      this.$emit('update:visibility', false)
      this.convertData()
      if (update) {
        this.$emit('update', this.data)
      } else {
        this.$emit('save', this.data)
      }
    },
    convertData() {
      this.data.expressions.forEach(expression => {
        if (expression.aggregateString !== 'count') {
          let temp = expression.aggregateString
          expression.aggregateString = expression.fieldName
          expression.fieldName = temp
        }
        expression.name = this.formatName(this.data.name)
      })
    },
    formatName(name) {
      return name
        .toLocaleLowerCase()
        .split(' ')
        .join('')
    },
    applyTimefields() {
      if (
        this.data &&
        this.data.expressions.length &&
        this.data.expressions[0].criteria.conditions
      ) {
        let defaultExpresion = {
          fieldName: 'createdTime',
          value: null,
          columnName: this.data.selectedTimeField,
          operatorId: this.data.operatorId,
          isResourceOperator: false,
          parseLabel: null,
          valueArray: [],
          operatorLabel: 'Today',
          active: false,
          operatorsDataType: {
            dataType: 'DATE_TIME',
            displayType: 'DATETIME',
          },
          isSpacePicker: false,
        }
        this.data.expressions[0].criteria.conditions[
          Object.keys(this.data.expressions[0].criteria.conditions).length + 1
        ] = defaultExpresion
        this.data.expressions[0].criteria.pattern =
          this.data.expressions[0].criteria.pattern.split(')')[0] +
          ' and ' +
          Object.keys(this.data.expressions[0].criteria.conditions).length +
          ')'
      }
    },
    applyPeriod() {
      if (
        this.data &&
        this.data.expressions.length &&
        this.data.expressions[0].criteria.conditions
      ) {
        let defaultExpresion = {
          fieldName: 'dueDate',
          value: null,
          columnName: 'Tickets.DUE_DATE',
          operatorId: this.data.operatorId,
          isResourceOperator: false,
          parseLabel: null,
          valueArray: [],
          operatorLabel: 'Today',
          active: false,
          operatorsDataType: {
            dataType: 'DATE_TIME',
            displayType: 'DATETIME',
          },
          isSpacePicker: false,
        }
        this.data.expressions[0].criteria.conditions[
          Object.keys(this.data.expressions[0].criteria.conditions).length + 1
        ] = defaultExpresion
        this.data.expressions[0].criteria.pattern =
          this.data.expressions[0].criteria.pattern.split(')')[0] +
          ' and ' +
          Object.keys(this.data.expressions[0].criteria.conditions).length +
          ')'
      }
    },
    constractData(rawData) {
      let data = this.$helpers.cloneObject(rawData)
      return data
    },
    addCriteriaAndResconstruct() {
      this.showCriteriaBuilder = false
    },
    showcriteriabuilder(index) {
      this.selectedIndex = index
      this.showCriteriaBuilder = true
    },
    getCriteria(criteria) {
      this.data.expressions[this.selectedIndex].criteria = criteria
    },
    loadFields() {
      this.loading = true
      this.$http
        .post('/v2/report/getReportFields', {
          moduleName: this.data.module,
        })
        .then(response => {
          this.prepareMetrics(response.data.result)
          this.getTimeFields(response.data.result)
          this.loading = false
        })
    },
    getTimeFields(result) {
      if (result.meta && result.meta.dimension && result.meta.dimension.time) {
        this.data.timeFields = result.meta.dimension.time
      }
    },
    prepareMetrics(result) {
      this.data.metrics = []
      if (result && result.meta && result.meta.metrics) {
        for (let metric of result.meta.metrics) {
          if (metric.name === 'actualWorkDuration') {
            metric.displayName = 'Work Duration'
          }
          this.data.metrics.push(metric)
          // if (metric.id === -1 || metric.name === 'totalCost') {
          //   this.data.metrics.push(metric)
          // }
        }
        let defaultMetric = {}
        defaultMetric['displayName'] = 'Number of ' + this.data.module + 's'
        defaultMetric['defaultMetric'] = true
        defaultMetric['fieldId'] = -1
        defaultMetric['name'] = 'count'
        this.data.metrics.push(defaultMetric)
      }
    },
  },
}
</script>

<style>
.kpi-criteria .el-dialog__body {
  max-height: 400px;
  overflow: auto;
  padding-bottom: 50px;
}

.f-kfi-card-builder .el-dialog__body {
  padding-bottom: 20px;
  max-height: 80vh;
  overflow: auto;
}

.f-kfi-card-builder .fc-modal-sub-title {
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.6px;
  color: #324056;
}

.f-kfi-card-builder .criteria-inner-container {
  padding: 10px;
  width: 100%;
  overflow: hidden;
  position: relative;
  padding-left: 0px;
}

.f-kfi-card-builder .report-criteria-buuilder {
  /* border: 1px dashed #cfd9e2; */
}

.f-kfi-card-builder .criteria-condition-block {
  padding-top: 10px;
}

.kpi-text-3 {
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #324056;
  padding-bottom: 7px;
}

.f-kfi-card-builder .fc-sub-title-desc {
  line-height: 0px;
  padding-bottom: 10px;
}

.f-kfi-card-builder .criteria-inner-container .mT20 {
  line-height: 25px;
}

.f-kfi-card-builder .criteria-condition-block:nth-child(1) {
  padding-top: 25px;
}

.f-kfi-card-builder .report-criteria-buuilder .criteria-container {
  height: auto;
  padding-bottom: 0px;
}

.f-kfi-card-builder .fc-dialog-center-container .el-dialog__header {
  padding: 0;
  padding-left: 30px;
}

.kpi-card-header {
  padding: 10px 12px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.4px;
  text-align: left;
  color: #000;
  margin-top: 10px;
}

.criteria-box {
  border: 0.5px dashed #d0d9e2;
  padding: 10px;
}
.kpi-map-setup-dilaog {
  height: 100%;
  margin-top: 0vh !important;
}
.kpi-map-setup-dilaog .el-dialog__body {
  height: 100%;
}
</style>
