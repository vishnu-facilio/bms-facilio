<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div class="kpi-modal">
      <el-form
        :model="newkpi"
        :rules="rules"
        :label-position="'top'"
        ref="categoryForm"
        class="fc-form"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ isNew ? $t('common._common.new') : $t('common._common._edit')
              }}{{ $t('common.header.kpi_definition') }}
            </div>
          </div>
        </div>
        <div class="new-body-modal enpi-body-modal">
          <div class="body-scroll pR10">
            <el-row class="mB10">
              <el-col :span="12">
                <p class="fc-input-label-txt">
                  {{ $t('common.wo_report.module') }}
                </p>
                <el-form-item prop="moduleObj">
                  <el-select
                    v-model="newkpi.moduleObj"
                    :disabled="!isNew"
                    :placeholder="$t('common.header.please_select_the_module')"
                    class="fc-input-full-border2 width100"
                    filterable
                    @change="loadFields()"
                  >
                    <el-option-group
                      v-for="(moduleObj, index) in modulesObjList"
                      :key="index"
                      :label="moduleObj.label"
                    >
                      <el-option
                        v-for="list in moduleObj.list"
                        :key="list.moduleName"
                        :label="list.label"
                        :value="list.moduleName"
                      >
                      </el-option>
                    </el-option-group>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20" class="mB10">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common._common.kpi_name') }}
                </p>
                <el-form-item prop="name">
                  <el-input
                    class="width50 fc-input-full-border2"
                    autofocus
                    v-model="newkpi.name"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common._common.enter_kpi_name')"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20" class="mB10">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common.wo_report.report_description') }}
                </p>
                <el-form-item>
                  <el-input
                    type="textarea"
                    :autosize="{ minRows: 4, maxRows: 4 }"
                    class="width100 fc-input-full-border-textarea"
                    :placeholder="$t('common._common.enter_desc')"
                    v-model="newkpi.description"
                    resize="none"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row class="mB10">
              <el-col :span="12">
                <p class="fc-input-label-txt">
                  {{ $t('common.products.site') }}
                </p>
                <Lookup
                  v-model="newkpi.siteId"
                  :field="siteField"
                  :hideLookupIcon="true"
                  :disabled="!isNew"
                >
                </Lookup>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="24">
                <p class="mT40 mB10 fbTitle">
                  {{ $t('common._common._criteria') }}
                </p>
                <el-form-item prop="criteria">
                  <criteria-builder
                    v-if="showCriteria"
                    class="stateflow-criteria"
                    ref="criteriaBuilder"
                    v-model="newkpi.criteria"
                    :moduleName="newkpi.moduleObj"
                    :disable="$validation.isEmpty(newkpi.moduleObj)"
                    :isOneLevelEnabled="true"
                  ></criteria-builder>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row class="mB10">
              <el-col :span="12">
                <p class="mT20 mB10 fbTitle">{{ $t('common.tabs.metric') }}</p>
                <el-form-item prop="metric">
                  <el-select
                    v-model="newkpi.metric"
                    :disabled="$validation.isEmpty(newkpi.moduleObj)"
                    :placeholder="$t('common.header.please_select_the_metric')"
                    class="fc-input-full-border2 width100"
                    filterable
                  >
                    <el-option
                      v-for="(metric, index) in metrics"
                      :key="index"
                      :label="metric.displayName"
                      :value="metric.name"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>

              <el-col
                :span="2"
                v-if="newkpi.metric && newkpi.metric !== 'count'"
                class="mL20 mT9"
              >
                <div class="fc-input-div-full-border-f14-1 pointer mT25">
                  <el-popover
                    :disabled="!showMetricAggregation(newkpi.metric)"
                    v-model="toggleMetricAggregation"
                    placement="bottom"
                    width="70"
                    trigger="click"
                    popper-class="metric-popover"
                  >
                    <div
                      class="pointer pT5 pB5 fc-label-hover label-txt-black f14 text-center"
                      @click="setMetricAggregation(metricAggr.value)"
                      v-for="metricAggr in metricAggregation"
                      :value="metricAggr.value"
                      :key="metricAggr.value"
                      v-bind:class="{
                        active: newkpi.aggr === metricAggr.value,
                      }"
                    >
                      {{ metricAggr.label }}
                    </div>
                    <img
                      :disabled="!showMetricAggregation(newkpi.metric)"
                      src="~assets/summation_img.svg"
                      class="p15"
                      slot="reference"
                    />
                  </el-popover>
                </div>
              </el-col>
            </el-row>

            <el-row class="mB10">
              <el-col :span="24">
                <p class="mT20 mB10 fbTitle">
                  {{ $t('common.header.date_filter') }}
                </p>
                <el-col :span="12">
                  <p class="fc-input-label-txt">
                    {{ $t('common.date_picker.date_field') }}
                  </p>
                  <el-form-item prop="selectedTimeField">
                    <el-select
                      v-model="newkpi.selectedTimeField"
                      :disabled="$validation.isEmpty(newkpi.moduleObj)"
                      :placeholder="
                        $t('common.header.please_select_the_date_field')
                      "
                      class="fc-input-full-border2 width100"
                      filterable
                      @change="newkpi.dateOperator = 22"
                    >
                      <el-option
                        v-for="(time, index) in dateFields"
                        :key="index"
                        :label="time.displayName"
                        :value="time.name"
                      ></el-option>
                      <el-option
                        :label="$t('common.wo_report.none')"
                        :value="-1"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-col>
            </el-row>

            <el-row
              class="mB10"
              v-if="!$validation.isEmpty(newkpi.selectedTimeField)"
            >
              <el-col :span="12">
                <p class="fc-input-label-txt">
                  {{ $t('common.wo_report.period') }}
                </p>
                <el-select
                  v-model="newkpi.dateOperator"
                  :disabled="$validation.isEmpty(newkpi.selectedTimeField)"
                  :placeholder="$t('common.header.please_select_the_period')"
                  class="fc-input-full-border2 width100"
                  filterable
                >
                  <el-option
                    :label="dateRange.label"
                    :value="dateRange.value"
                    v-for="(dateRange, index) in dateOperators"
                    :key="index"
                  ></el-option>
                </el-select>
              </el-col>

              <el-col :span="12" class="pL20" v-if="newkpi.dateOperator === 20">
                <p class="fc-input-label-txt">
                  {{ $t('common.date_picker.date_range') }}
                </p>
                <el-form-item prop="dateRange" class="kpi-date-picker-width">
                  <f-date-picker
                    :start-placeholder="$t('common.wo_report.start_date')"
                    :end-placeholder="$t('common.wo_report.end_date')"
                    :picker-options="dateOptions"
                    v-model="newkpi.dateRange"
                    type="datetimerange"
                  ></f-date-picker>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row class="mB20">
              <el-col :span="24">
                <div class="mB20 mT40 fbTitle">
                  {{ $t('common._common.safe_limit') }}
                </div>
                <el-radio
                  v-model="safeLimitType"
                  :label="safeLimitTypes.MIN"
                  :value="safeLimitTypes.MIN"
                  class="fc-radio-btn"
                  >{{ $t('common._common.less_then') }}</el-radio
                >

                <el-radio
                  v-model="safeLimitType"
                  :label="safeLimitTypes.MAX"
                  :value="safeLimitTypes.MAX"
                  class="fc-radio-btn"
                  >{{ $t('common._common.greater_than') }}</el-radio
                >

                <el-radio
                  v-model="safeLimitType"
                  :label="safeLimitTypes.BETWEEN"
                  :value="safeLimitTypes.BETWEEN"
                  class="fc-radio-btn"
                  >{{ $t('common._common.between') }}</el-radio
                >

                <el-radio
                  v-model="safeLimitType"
                  :label="safeLimitTypes.NONE"
                  :value="safeLimitTypes.NONE"
                  class="fc-radio-btn"
                  >{{ $t('common.wo_report.none') }}</el-radio
                >
              </el-col>
            </el-row>

            <el-row>
              <el-col v-if="safeLimitType === safeLimitTypes.MIN" :span="12">
                <el-form-item prop="target">
                  <el-input
                    class="fc-input-full-border2"
                    v-model="newkpi.target"
                    type="number"
                    :placeholder="
                      $t('common._common.enter_maximum_target_value')
                    "
                  />
                </el-form-item>
              </el-col>

              <el-col
                v-else-if="safeLimitType === safeLimitTypes.MAX"
                :span="12"
              >
                <el-form-item prop="minTarget">
                  <el-input
                    class="fc-input-full-border2"
                    v-model="newkpi.minTarget"
                    type="number"
                    :placeholder="
                      $t('common._common.enter_minimum_target_value')
                    "
                  />
                </el-form-item>
              </el-col>

              <el-col
                v-else-if="safeLimitType === safeLimitTypes.BETWEEN"
                :span="24"
              >
                <el-col :span="12" class="pR5">
                  <el-form-item prop="minTarget">
                    <el-input
                      class="fc-input-full-border2"
                      v-model="newkpi.minTarget"
                      type="number"
                      :placeholder="
                        $t('common._common.enter_minimum_target_value')
                      "
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12" class="pL5">
                  <el-form-item prop="target">
                    <el-input
                      class="fc-input-full-border2"
                      v-model="newkpi.target"
                      type="number"
                      :placeholder="
                        $t('common._common.enter_maximum_target_value')
                      "
                    />
                  </el-form-item>
                </el-col>
              </el-col>

              <el-col v-else :span="24"></el-col>
            </el-row>
          </div>
        </div>

        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">{{
            $t('common._common.cancel')
          }}</el-button>

          <el-button
            class="modal-btn-save"
            type="primary"
            @click="submitForm()"
            :loading="saving"
            >{{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}</el-button
          >
        </div>
      </el-form>
    </div>
  </el-dialog>
</template>

<script>
import AddKpi from './AddKpi'
import { isEmpty } from '@facilio/utils/validation'
import DateHelper from '@/mixins/DateHelper'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import { getModules } from 'pages/energy/kpi/helpers/kpiConstants'
import { API } from '@facilio/api'
import { CriteriaBuilder } from '@facilio/criteria'

export default {
  name: 'AddModuleKpi',
  extends: AddKpi,
  components: { CriteriaBuilder, FDatePicker },
  mixins: [DateHelper],
  props: ['kpi'],

  data() {
    return {
      showCriteria: true,
      modulesObjList: [],
      dateOptions: {
        disabledDate(time) {
          let today = new Date()
          return time.getTime() > today.getTime()
        },
      },
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
          label: 'This Week Until Now',
          value: 47,
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
          label: 'This Month Till Yesterday',
          value: 66,
        },
        {
          label: 'This Month Until Now',
          value: 48,
        },
        {
          label: 'Last Month',
          value: 27,
        },
        {
          label: 'Range',
          value: 20,
        },
        {
          label: 'This Year',
          value: 44,
        },
        {
          label: 'This Year upto Now',
          value: 46,
        },
        {
          label: 'Last Year',
          value: 45,
        },
      ],
      dateFields: null,
      metrics: null,
      newkpi: {
        name: '',
        description: '',
        siteId: null,
        triggerType: 1,
        formulaFieldType: 1,
        resultDataType: 3,
        moduleObj: 'workorder',
        metric: null,
        aggr: -1,
        selectedTimeField: -1,
        dateOperator: null,
        dateRange: null,
        criteria: null,
        target: null,
        minTarget: null,
      },
      metricAggregation: [
        {
          label: 'SUM',
          value: 3,
          name: 'sum',
        },
        {
          label: 'AVG',
          value: 2,
          name: 'avg',
        },
        {
          label: 'MIN',
          value: 4,
          name: 'min',
        },
        {
          label: 'MAX',
          value: 5,
          name: 'max',
        },
      ],
      toggleMetricAggregation: false,
      criteriaRendered: false,
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
        moduleObj: {
          required: true,
          message: this.$t('common.header.please_select_a_module'),
          trigger: 'change',
        },
        metric: {
          required: true,
          message: this.$t('common.header.please_select_metric'),
          trigger: 'change',
        },
        criteria: {
          validator: function(rule, value, callback) {
            let { conditions } = this.newkpi.criteria
            let criteriaObjValues = Object.values(conditions)

            let throwError = criteriaObjValues.every(field => {
              if (!isEmpty(field)) {
                let { active = false, fieldName, operatorId, value } = field
                if (!isEmpty(fieldName) && !isEmpty(operatorId)) {
                  if (active) {
                    if (!isEmpty(value)) return false
                  } else return false
                }
              }
              return true
            })
            throwError
              ? callback(
                  new Error(
                    this.$t(
                      'common.header.please_configure_atleast_one_criteria'
                    )
                  )
                )
              : callback()
          }.bind(this),
          trigger: 'change',
        },
        target: {
          validator: function(rule, value, callback) {
            if (
              this.safeLimitType === this.safeLimitTypes.MIN ||
              this.safeLimitType === this.safeLimitTypes.BETWEEN
            ) {
              if (isEmpty(this.newkpi.target)) {
                callback(
                  new Error(this.$t('common._common.fields_cannot_be_empty'))
                )
              } else callback()
            }
          }.bind(this),
          trigger: 'change',
        },
        minTarget: {
          validator: function(rule, value, callback) {
            if (
              this.safeLimitType === this.safeLimitTypes.MAX ||
              this.safeLimitType === this.safeLimitTypes.BETWEEN
            ) {
              if (isEmpty(this.newkpi.minTarget)) {
                callback(
                  new Error(this.$t('common._common.fields_cannot_be_empty'))
                )
              } else callback()
            }
          }.bind(this),
          trigger: 'change',
        },
      },
    }
  },

  async created() {
    await this.loadModules()
    this.init()
    this.loadFields()
  },

  methods: {
    init() {
      this.newkpi = { ...this.newkpi, ...this.$helpers.cloneObject(this.kpi) }

      if (!this.isNew) {
        let { moduleName } = this.newkpi
        this.newkpi.moduleObj = moduleName
        delete this.newkpi.moduleName

        let { metric } = this.newkpi
        this.newkpi.metric = metric.name

        let { dateFieldId } = this.newkpi
        if (!isEmpty(dateFieldId))
          this.loadDateFields().then(dateFields => {
            let time = dateFields.find(time => time.id === dateFieldId)
            this.newkpi.selectedTimeField = time.name
          })

        let { dateValue } = this.newkpi
        if (!isEmpty(dateValue)) {
          dateValue = dateValue.split(',')
          this.newkpi.dateRange = []
          dateValue.forEach(date => {
            this.newkpi.dateRange.push(parseInt(date))
          })
        }

        let { minTarget, target } = this.newkpi
        minTarget = !isEmpty(minTarget) ? minTarget : null
        target = !isEmpty(target) ? target : null
        if (isEmpty(minTarget) && isEmpty(target))
          this.safeLimitType = this.safeLimitTypes.NONE
        else {
          if (!isEmpty(minTarget) && !isEmpty(target))
            this.safeLimitType = this.safeLimitTypes.BETWEEN
          else if (!isEmpty(target))
            this.safeLimitType = this.safeLimitTypes.MIN
          else this.safeLimitType = this.safeLimitTypes.MAX
        }
        this.$set(this.newkpi, 'minTarget', minTarget)
        this.$set(this.newkpi, 'target', target)
      }
    },

    submitForm() {
      this.$refs['categoryForm'].validate(valid => {
        if (!valid) return false

        if (this.newkpi.siteId === null) {
          this.newkpi.siteId = -1
        }

        let url = '/v2/kpi/module/addOrUpdate'
        let kpiObj = this.$helpers.cloneObject(this.newkpi)

        let { criteria } = kpiObj
        let isValidateCriteria = this.$refs['criteriaBuilder'].validate()
        if (!isValidateCriteria) {
          return
        }

        let {
          id,
          name,
          description,
          siteId,
          moduleObj: moduleName,
          metric,
          aggr,
          dateOperator,
          minTarget,
          target,
          dateRange,
        } = kpiObj

        let dateFieldId = this.newkpi.selectedTimeField
        if (!isEmpty(this.newkpi.selectedTimeField)) {
          let { id } = this.dateFields.find(
            time => time.name === this.newkpi.selectedTimeField
          )
          dateFieldId = id
        }

        let kpi = {
          name,
          description,
          siteId,
          moduleName,
          criteria,
          metricName: metric,
        }

        let metricObj = this.metrics.find(
          metric => metric.name === this.newkpi.metric
        )
        if (!isEmpty(metricObj.id)) kpi.metricId = metricObj.id

        minTarget ? (kpi.minTarget = minTarget) : null
        target ? (kpi.target = target) : null
        metric !== 'count' ? (kpi.aggr = !isEmpty(aggr) ? aggr : 3) : null
        !isEmpty(dateFieldId)
          ? (kpi.dateFieldId = dateFieldId)
          : (kpi.dateFieldId = -99)
        !isEmpty(dateFieldId)
          ? dateOperator
            ? (kpi.dateOperator = dateOperator)
            : null
          : (kpi.dateOperator = -99)

        id ? (kpi.id = id) : null

        if (!isEmpty(dateRange)) {
          let dateValue = `${dateRange[0]},${dateRange[1]}`
          kpi.dateValue = dateValue
        }

        let param = { kpi }

        let showError = () => {
          this.$message.error(
            this.isNew
              ? this.$t('common.header.kpi_addition_failed')
              : this.$t('common.header.kpi_updation_failed')
          )
        }

        this.saving = true
        this.$http
          .post(url, param)
          .then(response => {
            this.saving = false

            if (response.data.responseCode === 0) {
              this.$message.success(
                this.isNew
                  ? this.$t('common.products.new_kpi_added_successfully')
                  : this.$t('common.header.kpi_updated_successfully')
              )
              this.$emit('onSave', response.data.result.kpi || kpi)
              this.$emit('onClose')
            } else {
              showError()
            }
          })
          .catch(() => {
            this.saving = false
            showError()
          })
      })
    },

    async loadModules() {
      let { error, data } = await API.get('/v3/modules/list/all')

      if (error) {
        this.$message.error(error.message)
      } else {
        let modulesListKey = {
          systemModules: 'System Modules',
          customModules: 'Custom Modules',
        }

        Object.entries(modulesListKey).forEach(([key, value]) => {
          let list = data[key].map(list => {
            return { moduleName: list.name, label: list.displayName }
          })

          if (key === 'systemModules') {
            list = [
              ...list,
              ...getModules().filter(
                module =>
                  this.$helpers.isLicenseEnabled(module.license) === true
              ),
            ]
          }

          this.modulesObjList.push({
            list,
            label: value,
          })
        })
      }
    },

    loadFields() {
      this.showCriteria = false

      let promise = []

      promise.push(this.loadDateFields())
      promise.push(this.loadMetrics())

      this.$nextTick(() => {
        this.showCriteria = true

        if (this.$refs['criteriaBuilder'])
          this.$refs['criteriaBuilder'].forceUpdate()
      })

      return promise
    },

    loadDateFields() {
      let url = `/v3/report/fields?moduleName=${this.newkpi.moduleObj}`

      return API.get(url)
        .then(response => {
          if (response.error) {
            this.$message.error('Error while getting fields')
          } else {
            let { meta } = response.data
            let dateField = meta.dimension.time || []
            let filteredDateField = dateField.filter(field => field.id > 0)
            this.dateFields = filteredDateField
            return this.dateFields
          }
        })
        .catch(() => {
          this.dateFields = []
        })
    },

    loadMetrics() {
      let url = `v2/kpi/module/metrics?moduleName=${this.newkpi.moduleObj}`

      return this.$http
        .get(url)
        .then(({ data }) => {
          if (data.responseCode === 0) {
            let { metrics } = data.result
            this.metrics = metrics
            this.isNew ? (this.newkpi.metric = 'count') : null
            return metrics
          }
        })
        .catch(() => {
          this.metrics = []
        })
    },

    showMetricAggregation(metric) {
      if (metric && metric.defaultMetric) {
        this.toggleMetricAggregation = false
        return false
      } else {
        return true
      }
    },

    setMetricAggregation(metricAggrValue) {
      this.newkpi.aggr = metricAggrValue
      this.toggleMetricAggregation = false
    },
  },
}
</script>

<style scoped>
.mT9 {
  margin-top: 9px;
}
</style>
<style lang="scss">
.kpi-date-picker-width {
  .el-date-editor.el-range-editor.el-input__inner.el-date-editor--datetimerange {
    width: 100% !important;
  }
}
</style>
