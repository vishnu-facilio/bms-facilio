<template>
  <div v-if="modelData">
    <el-row class="mT10">
      <el-col :span="8" v-if="!isSeverity" class="pR10">
        <div class="fc-input-label-txt">
          {{ title }} {{ $t('common.tabs.metric') }}
        </div>
        <el-select
          v-model="modelData.readingFieldId"
          filterable
          @change="setMetricModule(modelData.readingFieldId, modelData)"
          :placeholder="$t('common._common.select')"
          class="fc-input-full-border-select2 width100"
        >
          <el-option
            v-for="field in categoryFields"
            :key="field.id"
            :label="field.displayName"
            :value="field.id"
          ></el-option>
        </el-select>
      </el-col>
      <el-col :span="8">
        <div class="fc-input-label-txt">
          {{ $t('common._common.condition_type') }}
        </div>
        <el-select
          @change="loadType"
          class="fc-input-full-border-select2 width100 pR10"
          v-model="modelData.thresholdType"
          collapse-tags
        >
          <el-option
            v-for="(label, value) in $constants.CONDITION_TYPE"
            :key="value"
            :label="label"
            :value="parseInt(value)"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row>
      <el-col
        v-if="modelData.thresholdType === 2 || modelData.thresholdType === 3"
      >
        <!-- <div class="fc-sub-title-container">
            <div class="fc-modal-sub-title">CONDITIONS</div>
        </div>-->

        <el-row
          v-if="modelData.thresholdType === 3"
          align="middle"
          class="mT20"
          :gutter="20"
        >
          <el-col :span="8">
            <div class="fc-input-label-txt">
              {{ $t('common.header.baseline') }}
            </div>
            <div class="add">
              <el-select
                v-model="modelData.baselineId"
                class="form-item fc-input-full-border-select2 width100"
                :placeholder="$t('common.products.choose_baseline')"
              >
                <el-option
                  v-for="baseline in baselines"
                  :key="baseline.id"
                  :label="baseline.name"
                  :value="baseline.id"
                ></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
        <el-row align="middle" class="position-relative pT20 pB20" :gutter="20">
          <el-col :span="8">
            <div class="fc-input-label-txt">
              {{ $t('common._common.aggregation') }}
            </div>
            <div class="add">
              <el-select
                v-model="modelData.aggregation"
                class="form-item fc-input-full-border-select2 width100"
                :placeholder="$t('common.products.choose_aggregation')"
              >
                <el-option
                  key="sum"
                  :label="$t('common._common.sum')"
                  value="sum"
                ></el-option>
                <el-option
                  key="avg"
                  :label="$t('common._common.average')"
                  value="avg"
                ></el-option>
                <el-option
                  key="min"
                  :label="$t('common.wo_report.minimum')"
                  value="min"
                ></el-option>
                <el-option
                  key="max"
                  :label="$t('common.wo_report.maximum')"
                  value="max"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="12">
            <duration
              v-model="modelData.dateRange"
              :label="$t('common.date_picker.date_range')"
              labelClass="fc-input-label-txt"
              format="h"
              size="l"
              :minHour="true"
            >
              <el-col :span="3" class="pT10" slot="prefix">
                <span class="textcolor vertical-sub">{{
                  $t('common.products.last')
                }}</span>
              </el-col>
            </duration>
          </el-col>
          <el-col :span="8" class="mT20">
            <div class="fc-input-label-txt">
              {{ $t('common.wo_report.Operator') }}
            </div>
            <div class="add">
              <el-select
                v-model="modelData.operatorId"
                class="form-item fc-input-full-border-select2 width100"
                :placeholder="$t('common.products.choose_operator')"
              >
                <el-option
                  :key="9"
                  :label="$t('common.products.equals')"
                  :value="9"
                ></el-option>
                <el-option
                  :key="10"
                  :label="$t('common._common.not_equals')"
                  :value="10"
                ></el-option>
                <el-option
                  :key="13"
                  :label="$t('common._common.greater_than')"
                  :value="13"
                ></el-option>
                <el-option
                  :key="14"
                  :label="$t('common._common.greater_than_equals')"
                  :value="14"
                ></el-option>
                <el-option
                  :key="11"
                  :label="$t('common._common.less_then')"
                  :value="11"
                ></el-option>
                <el-option
                  :key="12"
                  :label="$t('common._common.less_than_equals')"
                  :value="12"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col
            v-if="modelData.thresholdType === 3"
            :span="8"
            class="mT20 pL10"
          >
            <div class="fc-input-label-txt">
              {{ $t('common._common.percentage') }}
            </div>
            <div class="add">
              <el-input
                v-model="modelData.percentage"
                type="number"
                placeholder="0%"
                class="fc-input-select fc-input-full-border2 width100"
              ></el-input>
            </div>
          </el-col>
          <el-col
            v-if="modelData.thresholdType === 2"
            :span="8"
            class="mT20 pL10"
          >
            <div class="fc-input-label-txt">
              {{ $t('common.header.value') }}
            </div>
            <div class="add">
              <el-input
                v-model="modelData.percentage"
                :placeholder="$t('common.placeholders.enter_value')"
                class="fc-input-full-border2 width100"
              ></el-input>
            </div>
          </el-col>
        </el-row>
      </el-col>
      <el-col v-else-if="modelData.thresholdType === 4">
        <!-- <div class="fc-sub-title-container">
            <div class="fc-modal-sub-title">CONDITIONS</div>
        </div>-->
        <el-row
          align="middle"
          v-if="!isBooleanMetric"
          :gutter="20"
          class="mT20"
        >
          <el-col :span="8">
            <div class="fc-input-label-txt">
              {{ $t('common._common.lower_trigger') }}
            </div>
            <div class="add">
              <el-input
                v-model="modelData.minFlapValue"
                :placeholder="$t('common.placeholders.lower_trigger_value')"
                class="fc-input-full-border2 width100"
              ></el-input>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="fc-input-label-txt">
              {{ $t('common._common.upper_trigger') }}
            </div>
            <div class="add">
              <el-input
                v-model="modelData.maxFlapValue"
                :placeholder="$t('common.placeholders.upper_trigger_value')"
                class="fc-input-full-border2 width100"
              ></el-input>
            </div>
          </el-col>
        </el-row>
        <el-row align="middle" class="mT20 mB20" :gutter="20">
          <el-col :span="8">
            <div class="fc-input-label-txt">
              {{ $t('common.dialog.flapping_frequency_n_times') }}
            </div>
            <div class="add">
              <el-input
                v-model="modelData.flapFrequency"
                :placeholder="
                  $t('common.placeholders.enter_flapping_frequency')
                "
                class="fc-input-full-border2 width100"
              ></el-input>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="fc-input-label-txt">
              {{ $t('common.dialog.flapping_interval_period_in_minutes') }}
            </div>
            <div class="add">
              <el-input
                v-model="modelData.flapInterval"
                :placeholder="$t('common.placeholders.enter_flapping_interval')"
                class="fc-input-full-border2 width100"
              ></el-input>
            </div>
          </el-col>
        </el-row>
      </el-col>
      <el-col v-else-if="modelData.thresholdType === 1">
        <!-- <div class="fc-sub-title-container">
            <div class="fc-modal-sub-title">CONDITIONS</div>
        </div>-->

        <el-row align="middle" class="mT20 mB20">
          <el-col :span="8">
            <div class="fc-input-label-txt">
              {{ $t('common.wo_report.Operator') }}
            </div>
            <div class="add">
              <el-select
                v-model="modelData.operatorId"
                class="form-item fc-input-full-border2 width100 pR10"
                :placeholder="$t('common.products.choose_operator')"
              >
                <el-option
                  v-for="operator in operators"
                  :key="operator.operator"
                  :label="operator.label"
                  :value="operator.operator"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="fc-input-label-txt">
              {{ $t('common.header.value') }}
            </div>
            <div class="add">
              <!-- <el-popover
                v-if="isNumberMetric"
                v-model="showPrevPopover"
                :width="
                  $refs.previnput && $refs.previnput.$el
                    ? $refs.previnput.$el.clientWidth
                    : ''
                "
                trigger="click"
                popper-class="prev-popper"
              >
                <div class="prev-value" style="cursor:pointer;padding: 10px;" @click="setPrevValue">
                  {{
                  (this.selectedMetric
                  ? this.selectedMetric.displayName + "'s "
                  : '') + 'Previous Value'
                  }}
                </div>
                <div slot="reference">
                  <el-input
                    ref="previnput"
                    @keydown.native="showPrevPopover = false"
                    v-model="modelData.percentage"
                    placeholder="Enter Value"
                    class="fc-input-full-border2"
                  >
                    <i
                      slot="suffix"
                      :class="[
                        'prev-icon el-select__caret el-input__icon el-icon-arrow-down',
                        { active: showPrevPopover },
                      ]"
                      style="line-height: 16px;"
                    ></i>
                  </el-input>
                </div>
              </el-popover> -->
              <el-select
                v-if="isBooleanMetric"
                v-model="modelData.percentage"
                :placeholder="$t('common._common.select')"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  :label="selectedMetric.trueVal || 'True'"
                  value="TRUE"
                ></el-option>
                <el-option
                  :label="selectedMetric.falseVal || 'False'"
                  value="FALSE"
                ></el-option>
              </el-select>
              <el-input
                v-else
                v-model="modelData.percentage"
                :placeholder="$t('common.placeholders.enter_value')"
                class="fc-input-full-border2"
              ></el-input>
            </div>
          </el-col>
          <el-col :span="8" class="pL10">
            <div class="fc-input-label-txt">
              {{ $t('common._common.occurrences') }}
            </div>
            <div class="add">
              <el-input
                type="number"
                v-model="modelData.occurences"
                :placeholder="$t('common.placeholders.enter_occurrences')"
                class="fc-input-select fc-input-full-border2"
              ></el-input>
            </div>
          </el-col>
        </el-row>
        <el-row
          v-if="modelData.occurences > 0"
          align="middle"
          style="margin:0px;"
          :gutter="20"
        >
          <el-col :span="24" style="padding-left:0px;">
            <div class="textcolor pT0">
              <el-radio-group
                v-model="modelData.consecutiveoroverperiod"
                @change="changeOccurence()"
              >
                <el-radio :label="1" class="fc-radio-btn">{{
                  $t('common.header.over_period')
                }}</el-radio>
                <el-radio :label="2" class="fc-radio-btn">{{
                  $t('common.products.consecutive')
                }}</el-radio>
              </el-radio-group>

              <!-- <el-radio
                @change="changeOccurence()"
                v-model="modelData.consecutiveoroverperiod"
                :label="1"
                class="fc-radio-btn"
              >Over Period</el-radio>
              <el-radio
                @change="changeOccurence()"
                v-model="modelData.consecutiveoroverperiod"
                :label="2"
                class="fc-radio-btn"
              >Consecutive</el-radio>-->
            </div>
          </el-col>
        </el-row>
        <el-row
          v-if="
            modelData.occurences > 0 && modelData.consecutiveoroverperiod === 1
          "
          align="middle"
          class="mT20"
        >
          <el-col :span="12">
            <div class="fc-input-label-txt">
              {{ $t('common.wo_report.period') }}
            </div>
            <div class="add">
              <el-select
                v-model="modelData.overPeriod"
                @change="changeOccurence()"
                class="width100 fc-input-full-border-select2"
                :placeholder="$t('common.placeholders.choose_period')"
              >
                <el-option
                  :key="1"
                  :label="$t('common.header.last_1_hour')"
                  :value="1"
                ></el-option>
                <el-option
                  :key="3"
                  :label="$t('common.header.last_3_hour')"
                  :value="3"
                ></el-option>
                <el-option
                  :key="6"
                  :label="$t('common.header.last_6_hour')"
                  :value="6"
                ></el-option>
                <el-option
                  :key="12"
                  :label="$t('common.header.last_12_hour')"
                  :value="12"
                ></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
      </el-col>
      <el-col
        v-else-if="modelData.thresholdType === 5"
        class="mB20 rule-condition-formula"
      >
        <f-formula-builder
          style="padding-top: 20px;"
          title
          module="thresholdrule"
          v-model="modelData.workflow"
          :metric="selectedMetric"
          :restrictInit="true"
          :assetCategory="{
            id: assetCategoryId ? assetCategoryId : modelData.assetCategoryId,
          }"
        ></f-formula-builder>
      </el-col>
      <el-col v-else-if="modelData.thresholdType === 6" :span="16" class="mB20">
        <!-- <div class="fc-sub-title-container">
            <div class="fc-modal-sub-title">CONDITIONS</div>
        </div>-->
        <function
          v-model="modelData.workflow"
          :metric="metricFields"
          :resource="selectedResource"
        ></function>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
import Duration from '@/FDuration'
import Function from '@/workflow/FFormulaFunction'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'

import RuleMixin from '@/mixins/RuleMixin'

export default {
  mixins: [RuleMixin],
  props: [
    'modelData',
    'thresholdMetric',
    'isSeverity',
    'thresholdFields',
    'title',
    'assetCategoryId',
    'metricFields',
  ],
  components: {
    FFormulaBuilder,
    Duration,
    Function,
  },
  watch: {
    modelData(newVal, oldVal) {
      if (!(newVal === oldVal)) {
        // this.modelData = this.$helpers.cloneObject(newVal)
        if (this.modelData && this.modelData.thresholdType === 3) {
          this.loadBaselines()
        } else if (this.modelData && this.modelData.thresholdType === 1) {
          this.parseconsecutiveoroverperiod(this.modelData)
        }
      }
    },
    thresholdMetric(val) {
      this.selectedMetric = val
    },
  },
  computed: {
    ...mapState({
      alarmseverity: state => state.alarmSeverity,
    }),
    categoryFields() {
      return this.thresholdFields
    },
    isBooleanMetric() {
      return (
        this.selectedMetric &&
        (this.selectedMetric.dataTypeEnum === 'BOOLEAN' ||
          this.selectedMetric.dataTypeEnum._name === 'BOOLEAN')
      )
    },
    isNumberMetric() {
      return (
        this.selectedMetric &&
        (['NUMBER', 'DECIMAL'].includes(this.selectedMetric.dataTypeEnum) ||
          ['NUMBER', 'DECIMAL'].includes(
            this.selectedMetric.dataTypeEnum._name
          ))
      )
    },
    operators() {
      if (this.isNumberMetric) {
        return this.numberOperators
      } else if (this.isBooleanMetric) {
        return this.booleanOperators
      } else {
        return null
      }
    },
  },
  data() {
    return {
      thresholdType: 1,
      showPrevPopover: false,
      metricFieldName: null,
      baselines: [],
      model: {
        alarmSeverity: null,
        consecutiveoroverperiod: 1,
        overPeriod: null,
        occurences: null,
        workflow: null,
        readingRule: {
          module: '',
          name: '',
          description: '',
          executionOrder: 0,
          resourceId: -1,
          readingFieldId: -1,
          assetCategoryId: null,
          event: {
            moduleId: null,
            activityType: 1,
          },
          criteria: null,
          baselineId: null,
          aggregation: null,
          overPeriod: null,
          dateRange: 1,
          operatorId: null,
          percentage: null,
          onSuccess: false,
          parentRuleId: null,
        },
      },
    }
  },
  mounted() {
    if (this.modelData.thresholdType === 3) {
      this.loadBaselines()
    } else if (this.modelData.thresholdType === 1) {
      this.parseconsecutiveoroverperiod(this.modelData)
    }
    if (this.modelData.readingFieldId > 0) {
      this.setMetricModule(this.modelData.readingFieldId, this.modelData)
      this.selectedMetric = this.modelData.selectedMetric
      // this.modelData = this.$helpers.cloneObject(this.modelData)
      // this.parseconsecutiveoroverperiod(this.modelData)
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
  },
  methods: {
    setPrevValue() {
      this.model.readingRule.percentage = '$' + '{previousValue}'
      this.showPrevPopover = false
    },
    parseconsecutiveoroverperiod(rule) {
      if (rule.workflow) {
        if (rule.occurences) {
          // rule.occurences = rule.percentage < 0 ? null : rule.percentage
          rule.percentage =
            rule.workflow.expressions[0].aggregateCondition &&
            rule.workflow.expressions[0].aggregateCondition[0]
              ? rule.workflow.expressions[0].aggregateCondition[0].value
              : null
          if (rule.workflow.expressions[0].criteria.conditions['2']) {
            rule.consecutiveoroverperiod = 1
            rule.overperiod = parseInt(
              rule.workflow.expressions[0].criteria.conditions['2'].value
            )
          } else {
            rule.consecutiveoroverperiod = 2
          }
        }
      }
    },
    changeOccurence() {
      this.$forceUpdate()
    },
    loadType() {
      if (this.modelData.thresholdType === 3) {
        this.loadBaselines()
      }
    },
    loadBaselines() {
      let self = this
      self.$http.get('/baseline/all').then(function(response) {
        if (response.status === 200) {
          self.baselines = response.data
        }
      })
    },
  },
}
</script>
<style>
.rule-condition-formula .fbVariableLegendContainer {
  padding-right: 1px;
}
</style>
