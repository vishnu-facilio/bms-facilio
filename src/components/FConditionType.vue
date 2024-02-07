<template>
  <div>
    <el-row :gutter="20" class="mT10">
      <el-col align="left" :span="12" v-if="!isSeverity">
        <div class="fc-input-label-txt">{{ title }} Metric</div>
        <el-select
          v-model="modelData.readingFieldId"
          filterable
          @change="setMetricModule(metricFieldName, modelData)"
          placeholder="Select"
          class="fc-input-full-border-select2 width100"
        >
          <el-option
            v-for="field in categoryFields"
            :key="field.name + title"
            :label="field.displayName"
            :value="field.id"
          ></el-option>
        </el-select>
      </el-col>
      <el-col :span="12" class="condition-type-width">
        <div class="fc-input-label-txt">Condition Type</div>
        <el-select
          @change="loadType"
          class="fc-input-full-border-select2 width100"
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
            </div> -->

        <el-row
          v-if="modelData.thresholdType === 3"
          align="middle"
          class="mT20"
          :gutter="20"
        >
          <el-col :span="12">
            <div class="fc-input-label-txt">Baseline</div>
            <div class="add">
              <el-select
                v-model="modelData.baselineId"
                class="form-item fc-input-full-border-select2 width100"
                placeholder="Choose Baseline"
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
          <el-col :span="12">
            <div class="fc-input-label-txt">Aggregation</div>
            <div class="add">
              <el-select
                v-model="modelData.aggregation"
                class="form-item fc-input-full-border-select2 width100"
                placeholder="Choose Aggregation"
              >
                <el-option key="sum" label="Sum" value="sum"></el-option>
                <el-option key="avg" label="Average" value="avg"></el-option>
                <el-option key="min" label="Minimum" value="min"></el-option>
                <el-option key="max" label="Maximum" value="max"></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="12">
            <duration
              v-model="modelData.dateRange"
              label="Date Range"
              labelClass="fc-input-label-txt"
              format="h"
              size="l"
              :minHour="true"
            >
              <el-col :span="3" class="pT10" slot="prefix"
                ><span class="textcolor vertical-sub">Last</span></el-col
              >
            </duration>
          </el-col>
          <el-col :span="12" class="mT20">
            <div class="fc-input-label-txt">Operator</div>
            <div class="add">
              <el-select
                v-model="modelData.operatorId"
                class="form-item fc-input-full-border-select2 width100"
                placeholder="Choose Operator"
              >
                <el-option :key="9" label="Equals" :value="9"></el-option>
                <el-option :key="10" label="Not Equals" :value="10"></el-option>
                <el-option
                  :key="13"
                  label="Greater Than"
                  :value="13"
                ></el-option>
                <el-option
                  :key="14"
                  label="Greater Than Equals"
                  :value="14"
                ></el-option>
                <el-option :key="11" label="Less Than" :value="11"></el-option>
                <el-option
                  :key="12"
                  label="Less Than Equals"
                  :value="12"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col
            v-if="modelData.thresholdType === 3"
            :span="12"
            class="mT20 pL10"
          >
            <div class="fc-input-label-txt">Percentage</div>
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
            :span="12"
            class="mT20 pL10"
          >
            <div class="fc-input-label-txt">Value</div>
            <div class="add">
              <el-input
                v-model="modelData.percentage"
                placeholder="Enter Value"
                class="fc-input-full-border2 width100"
              ></el-input>
            </div>
          </el-col>
        </el-row>
      </el-col>
      <el-col v-else-if="modelData.thresholdType === 4">
        <!-- <div class="fc-sub-title-container">
            <div class="fc-modal-sub-title">CONDITIONS</div>
            </div> -->
        <el-row
          align="middle"
          v-if="!isBooleanMetric"
          :gutter="20"
          class="mT20"
        >
          <el-col :span="12">
            <div class="fc-input-label-txt">Lower Trigger</div>
            <div class="add">
              <el-input
                v-model="modelData.minFlapValue"
                placeholder="Lower Trigger Value"
                class="fc-input-full-border2 width100"
              ></el-input>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="fc-input-label-txt">Upper Trigger</div>
            <div class="add">
              <el-input
                v-model="modelData.maxFlapValue"
                placeholder="Upper Trigger Value"
                class="fc-input-full-border2 width100"
              ></el-input>
            </div>
          </el-col>
        </el-row>
        <el-row align="middle" class="mT20 mB20" :gutter="20">
          <el-col :span="12">
            <div class="fc-input-label-txt">Flapping Frequency (n times)</div>
            <div class="add">
              <el-input
                v-model="modelData.flapFrequency"
                placeholder="Enter Flapping Frequency"
                class="fc-input-full-border2 width100"
              ></el-input>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="fc-input-label-txt">
              Flapping Interval (period in minutes)
            </div>
            <div class="add">
              <el-input
                v-model="modelData.flapInterval"
                placeholder="Enter Flapping Interval"
                class="fc-input-full-border2 width100"
              ></el-input>
            </div>
          </el-col>
        </el-row>
      </el-col>
      <el-col v-else-if="modelData.thresholdType === 1">
        <!-- <div class="fc-sub-title-container">
            <div class="fc-modal-sub-title">CONDITIONS</div>
            </div> -->

        <el-row align="middle" class="mT20 mB20">
          <el-col :span="8">
            <div class="fc-input-label-txt">Operator</div>
            <div class="add">
              <el-select
                v-model="modelData.operatorId"
                class="form-item fc-input-full-border2 width100 pR10"
                placeholder="Choose Operator"
              >
                <el-option :key="9" label="Equals" :value="9"></el-option>
                <el-option :key="10" label="Not Equals" :value="10"></el-option>
                <el-option
                  :key="13"
                  label="Greater Than"
                  :value="13"
                ></el-option>
                <el-option
                  :key="14"
                  label="Greater Than Equals"
                  :value="14"
                ></el-option>
                <el-option :key="11" label="Less Than" :value="11"></el-option>
                <el-option
                  :key="12"
                  label="Less Than Equals"
                  :value="12"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="fc-input-label-txt">Value</div>
            <div class="add">
              <el-popover
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
                <div
                  class="prev-value"
                  style="cursor:pointer;padding: 10px;"
                  @click="setPrevValue"
                >
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
              </el-popover>
              <el-input
                v-else
                v-model="modelData.percentage"
                placeholder="Enter Value"
                class="fc-input-full-border2"
              >
              </el-input>
            </div>
          </el-col>
          <el-col :span="8" class="pL10">
            <div class="fc-input-label-txt">Occurrences</div>
            <div class="add">
              <el-input
                type="number"
                v-model="modelData.occurences"
                placeholder="Enter Occurrences"
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
              <el-radio
                @change="changeOccurence()"
                v-model="modelData.consecutiveoroverperiod"
                :label="1"
                class="fc-radio-btn"
                >Over Period</el-radio
              >
              <el-radio
                @change="changeOccurence()"
                v-model="modelData.consecutiveoroverperiod"
                :label="2"
                class="fc-radio-btn"
                >Consecutive</el-radio
              >
            </div>
          </el-col>
        </el-row>
        <el-row
          v-if="
            modelData.occurences > 0 && modelData.consecutiveoroverperiod === 1
          "
          align="middle"
          class="mB20 mT20"
        >
          <el-col :span="12">
            <div class="fc-input-label-txt">Period</div>
            <div class="add">
              <el-select
                v-model="modelData.overperiod"
                @change="changeOccurence()"
                class="width100 fc-input-full-border-select2"
                placeholder="Choose Period"
              >
                <el-option :key="1" label="Last 1 Hour" :value="1"></el-option>
                <el-option :key="3" label="Last 3 Hour" :value="3"></el-option>
                <el-option :key="6" label="Last 6 Hour" :value="6"></el-option>
                <el-option
                  :key="12"
                  label="Last 12 Hour"
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
          title=""
          module="thresholdrule"
          v-model="modelData.workflow"
          :metric="selectedMetric"
          :assetCategory="{
            id: assetCategoryId ? assetCategoryId : modelData.assetCategoryId,
          }"
        ></f-formula-builder>
      </el-col>
      <el-col v-else-if="modelData.thresholdType === 6" :span="24" class="mB20">
        <!-- <div class="fc-sub-title-container">
            <div class="fc-modal-sub-title">CONDITIONS</div>
            </div> -->
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
import WorkFlowMixin from '@/mixins/WorkFlowMixin'

export default {
  mixins: [WorkFlowMixin],
  props: [
    'modelData',
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
        this.modelData = newVal
        if (this.modelData && this.modelData.thresholdType === 3) {
          this.loadBaselines()
        }
      }
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
        overperiod: null,
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
          overperiod: null,
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
    }
    if (this.modelData.readingFieldId > 0) {
      this.selectedMetric = this.modelData.readingField
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
