<template>
  <div>
    <base-dialog-box
      :visibility.sync="visibility"
      :onConfirm="saveModuleName"
      :onCancel="cancelEdit"
      cancelText="Cancel"
      confirmText="Save"
      :title="'Value Based On'"
      width="650px"
    >
      <div class="dialog-content-body" style="margin-bottom:20px;" slot="body">
        <div class="data-config-section">
          <el-row class="config-row" style="padding-bottom:10px;">
            <el-col :span="6">
              <div
                class="config-label"
                v-if="config.moduleType == '2' || config.moduleType == 2"
              >
                {{ $t('pivot.preferences') }}
              </div>
              <div v-else class="config-label">
                {{ $t('pivot.dateFilter') }}
              </div>
            </el-col>
            <el-col
              :span="12"
              v-if="config.moduleType == '1' || config.moduleType == 1"
            >
              <el-switch
                v-model="enableTimelineFilter"
                style="margin-top:10px;"
                :disabled="config.moduleType === '2'"
              ></el-switch>
            </el-col>
          </el-row>
          <el-row
            class="config-row mL15"
            v-if="
              enableTimelineFilter &&
                (config.moduleType === '1' || config.moduleType == 1)
            "
          >
            <el-col :span="12">
              <div class="config-label">
                {{ $t('pivot.dateField') }}
              </div>
            </el-col>
            <el-col :span="12">
              <el-select
                v-model="configLocal.dateFieldId"
                placeholder="Select"
                class="fc-input-full-border-select2"
              >
                <el-option
                  v-for="(dateField, index) in dateFieldOptions"
                  :key="'module-date-field-option' + index"
                  :label="dateField.displayName"
                  :value="dateField.id"
                >
                </el-option>
              </el-select>
            </el-col>
          </el-row>
          <el-row class="config-row mL15" v-if="enableTimelineFilter">
            <el-col :span="12">
              <div class="config-label">
                {{ $t('pivot.period') }}
              </div>
            </el-col>
            <el-col :span="12">
              <el-select
                v-model="configLocal.datePeriod"
                placeholder="Select"
                class="fc-input-full-border-select2"
              >
                <el-option
                  v-for="(datePeriod, index) in datePeriodOptions"
                  :key="'module-date-period' + index"
                  :label="datePeriod.label"
                  :value="datePeriod.dateOperator"
                >
                </el-option>
              </el-select>
            </el-col>
          </el-row>
          <el-row class="config-row mL15" v-if="enableTimelineFilter">
            <el-col :span="12">
              <div class="config-label">
                {{ $t('pivot.baseline') }}
              </div>
            </el-col>
            <el-col :span="12">
              <el-select
                v-model="configLocal.baselineLabel"
                placeholder="Select"
                class="fc-input-full-border-select2"
              >
                <el-option
                  v-for="(datePeriod, index) in baselinePeriodOptions"
                  :key="'module-base-period' + index"
                  :label="datePeriod.name"
                  :value="datePeriod.name"
                >
                </el-option>
              </el-select>
            </el-col>
          </el-row>
          <el-row class="config-row mL15" v-if="configLocal.datePeriod == 20">
            <el-col :span="12">
              <div class="config-label">
                {{ $t('pivot.customRange') }}
              </div>
            </el-col>
            <el-col :span="12">
              <el-date-picker
                v-model="customDateRange"
                type="datetimerange"
                placeholder="Select start date and time"
                class="fc-input-full-border2"
                style="width: 69% !important; border-radius: 5px !important;"
                value-format="timestamp"
              >
              </el-date-picker>
            </el-col>
          </el-row>
          <el-row
            class="config-row mL15"
            v-if="config.moduleType == '2' || config.moduleType == 2"
          >
            <el-col>
              <div>
                <span>{{ $t('pivot.exclude_global_time_filter') }}</span>
                <el-checkbox
                  v-model="configLocal.excludeFromTimelineFilter"
                  style="margin-left:95px;"
                ></el-checkbox>
              </div>
            </el-col>
          </el-row>
        </div>
        <div class="mT35" v-if="showHideCriteria">
          <span>Criteria</span
          ><el-switch
            v-model="criteriaEnabled"
            style="margin-left:95px;"
          ></el-switch>
        </div>
        <div
          class="mT10"
          v-if="criteriaEnabled && showHideCriteria && config.moduleType == 2"
        >
          <el-radio-group
            v-model="showCriteriaType"
            class="fc-input-full-border-select2 mT10 module-select"
            @change="changeShowCriteriaType(showCriteriaType)"
          >
            <el-radio-button :label="1"
              >{{ $t('pivot.ParentCriteria') }}
            </el-radio-button>
            <el-radio-button :label="2"
              >{{ $t('pivot.DataCriteria') }}
            </el-radio-button>
          </el-radio-group>
        </div>
        <div
          :key="showDataFilter"
          v-if="criteriaEnabled && showHideCriteria"
          class="criteria-section"
          @click="criteriaValidMsg = false"
        >
          <CriteriaBuilder
            ref="f-parent-pivot-filter"
            v-if="showParentModuleFilter"
            v-model="configLocal.dataFilter"
            :moduleName="baseModuleName"
          >
          </CriteriaBuilder>
          <CriteriaBuilder
            ref="f-pivot-data-filter"
            v-if="showDataFilter"
            v-model="configLocal.criteria"
            :moduleName="moduleName"
          >
          </CriteriaBuilder>
          <div v-if="criteriaValidMsg">
            <span style="color:#f56c6c;margin-left:9px;"
              >Please add valid criteria</span
            >
          </div>
        </div>
      </div>
    </base-dialog-box>
  </div>
</template>

<script>
import BaseDialogBox from './BaseDialogBox.vue'
// import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import { API } from '@facilio/api'
import { isDateTypeField } from '@facilio/utils/field'
import { datePeriodOptions } from './../PivotDefaults'
import { isEmpty } from '@facilio/utils/validation'
import { CriteriaBuilder } from '@facilio/criteria'

export default {
  props: ['visibility', 'config', 'fields', 'moduleName', 'baseModuleName'],
  components: {
    BaseDialogBox,
    CriteriaBuilder,
  },
  created() {
    this.init()
    this.changeShowCriteriaType(this.showCriteriaType)
  },
  mounted() {
    this.loadBaselineOptions()
    if (this.config) {
      this.configLocal = { ...this.config }
    }

    if (
      this.configLocal?.moduleType != '2' &&
      this.configLocal.dateFieldId < 0
    ) {
      this.configLocal.dateFieldId = null
    }

    this.criteriaEnabled =
      this.configLocal?.criteria || this.configLocal?.dataFilter ? true : false

    if (this.formatter) {
      this.formatterLocal = { ...this.formatter }
    }
    // this.enableTimelineFilter = this.configLocal.excludeFromTimelineFilter
    if (this.config.startTime > 0 && this.endTime > 0) {
      this.customDateRange[0] = this.config.startTime
      this.customDateRange[1] = this.config.endTime
    }
    if (this.configLocal?.moduleType == '2') {
      this.enableTimelineFilter = true
    }
    if (this.configLocal?.moduleType == '1' && this.config.dateFieldId > 0) {
      this.enableTimelineFilter = true
    }
    if (
      this.configLocal?.moduleType == '1' &&
      !isEmpty(this.configLocal?.criteria) &&
      isEmpty(this.configLocal?.dataFilter)
    ) {
      this.configLocal.dataFilter = this.configLocal.criteria
      this.configLocal.criteria = null
    }
  },

  computed: {
    dateFieldOptions() {
      let options = []
      if (this?.config?.moduleType == '2') {
        // options.push({ id: -99, displayName: 'NONE' })
        options.push({ id: -1, displayName: 'TTIME' })
        return options
      }
      if (!this.fields) return options

      options.push({ id: -99, displayName: 'NONE' })
      let dateFields = this.fields.filter(field => {
        return isDateTypeField(field)
      })
      options.push(...dateFields)
      return options
    },
  },

  data() {
    return {
      moduleOptions: {},
      configLocal: {},
      enableTimelineFilter: false,
      formatterLocal: {},
      refreshKey: 0,
      baselinePeriodOptions: [],
      customDateRange: [],
      datePeriodOptions: datePeriodOptions,
      criteriaEnabled: false,
      criteriaValidMsg: false,
      showHideCriteria: true,
      existingOption: [],
      showCriteriaType: 1,
      showDataFilter: false,
      showParentModuleFilter: false,
    }
  },
  methods: {
    init() {
      if (this.config) {
        if (this.config.baselineLabel == null) {
          this.config.baselineLabel = 'None'
        }
        this.configLocal = { ...this.config }
        if (this.configLocal?.moduleType == '2') {
          this.enableTimelineFilter = true
        }
        this.showHideCriteria = isEmpty(this.moduleName) ? false : true
      }
    },
    async loadBaselineOptions() {
      let { data } = await API.get('/baseline/all')
      if (data) {
        this.baselinePeriodOptions = JSON.parse(JSON.stringify(data))
        this.baselinePeriodOptions.splice(0, 0, { id: -1, name: 'None' })
      }
    },
    isValidCriteria(criteria) {
      if (!criteria.conditions) {
        return false
      } //all conditions empty ,invalid criteria
      let areAllConditionsEmpty = false

      areAllConditionsEmpty = Object.keys(criteria.conditions).every(
        conditionKey => {
          let condition = criteria.conditions[conditionKey]
          if (isEmpty(condition)) {
            return true
          }
          if (!(condition.fieldName && condition.operatorId)) {
            return true
          }
        }
      )
      return !areAllConditionsEmpty
    },
    cancelEdit() {
      this.$emit('update:visibility', false)
    },
    saveModuleName() {
      this.configLocal = {
        ...this.configLocal,
        excludeFromTimelineFilter: this.configLocal.excludeFromTimelineFilter,
        startTime: this.customDateRange[0],
        endTime: this.customDateRange[1],
      }
      if (!this.enableTimelineFilter) {
        this.configLocal.dateFieldId = -99
      }
      if (!this.criteriaEnabled) {
        this.configLocal.criteria = null
        this.configLocal.dataFilter = null
      }
      if (this.configLocal.baselineLabel == 'None') {
        this.configLocal.baselineLabel = null
      }
      if (
        this.configLocal.criteria &&
        !this.isValidCriteria(this.configLocal.criteria)
      ) {
        this.criteriaValidMsg = true
        return
      }
      this.$emit('updatePivotMeasureSettings', this.configLocal)
      this.$emit('update:visibility', false)
    },
    changeShowCriteriaType(showFilterType) {
      this.showDataFilter = showFilterType == 2 ? true : false
      this.showParentModuleFilter = showFilterType == 1 ? true : false
    },
  },
}
</script>

<style scoped>
.criteria-section {
  margin-left: -7px;
  margin-bottom: 20px;
}
.config-row {
  padding-top: 15px;
}
.config-label {
  margin-top: 10px;
}
</style>
