<template>
  <div
    class="rule-condition-from-container rule-basic-info-container d-flex flex-direction-column"
  >
    <div v-if="isLoading" class="loading-container d-flex">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div class="rule-condition-from-content rule-basic-info-content">
      <div class="rule-info-form">
        <div class="position-relative">
          <!-- PREREQUESt Condition -->
          <div class="header f12 bold mT30 mR30 d-flex">
            <div>
              <div class="text-uppercase">
                {{ $t('rule.create.prerequisite_condition') }}
              </div>
              <div class="fc-heading-border-width43 mT15"></div>
              <div class="fc-grey2 f13 pT10">
                {{ $t('common.header.create_single_multiple') }}
              </div>
            </div>
          </div>

          <el-form>
            <div class="pT10">
              <el-checkbox v-model="rule.isPrerequest">{{
                $t('common.header.enable_pre_requisite')
              }}</el-checkbox>
            </div>
            <div v-if="rule.isPrerequest">
              <div class="rule-condition-from">
                <f-threshold-condition
                  :modelData.sync="rule.preRequsite"
                  :title="'Prerequiste'"
                  :isSeverity="true"
                  :thresholdFields="thresholdFields"
                  :metricFields="selectedMetricFields"
                  :assetCategoryId="rule.preRequsite.assetCategoryId"
                  class="position-relative mT30 rule-condition"
                ></f-threshold-condition>
              </div>
            </div>
            <!-- ALARM Condition -->
            <div class="header f12 bold mT30 d-flex">
              <div>
                <div class="text-uppercase">
                  {{ $t('rule.create.alarm_condition') }}
                </div>
                <div class="fc-heading-border-width43 mT15"></div>
                <div class="fc-grey2 f13 pT10">
                  {{ $t('common.header.create_single_multiple') }}
                </div>
              </div>
            </div>
            <div class="pT20">
              <div class="rule-condition-from">
                <f-threshold-condition
                  :title="$t('common.header.alarm_condition')"
                  :isSeverity="true"
                  :modelData.sync="rule.alarmTriggerRule"
                  :thresholdMetric="rule.preRequsite.selectedMetric"
                  :metricFields="selectedMetricFields"
                  :assetCategoryId="rule.preRequsite.assetCategoryId"
                  :thresholdFields="thresholdFields"
                  class="rule-condition"
                ></f-threshold-condition>
              </div>
            </div>
            <!-- Report down time -->
            <div class="pT20">
              <el-checkbox v-model="rule.reportBreakdown">{{
                $t('rule.create.report_for_downtime')
              }}</el-checkbox>
            </div>
            <!-- Auto Clear -->
            <div class="header f12 bold mT30 d-flex">
              <div>
                <div class="text-uppercase">
                  {{ $t('rule.create.alarm_clear_condition') }}
                </div>
                <div class="fc-heading-border-width43 mT15"></div>
                <div class="fc-grey2 f13 pT10">
                  {{ $t('common.header.create_single_multiple') }}
                </div>
              </div>
            </div>
            <div class="mT20">
              <el-form-item>
                <el-radio
                  v-model="rule.autoClear"
                  :label="true"
                  :value="true"
                  class="fc-radio-btn"
                  >{{ $t('common._common.auto_clear') }}</el-radio
                >
                <el-radio
                  v-model="rule.autoClear"
                  :label="false"
                  :value="false"
                  class="fc-radio-btn"
                >
                  {{ $t('common.header.clear_based_condition') }}
                </el-radio>
              </el-form-item>
              <div class="rule-condition-from">
                <f-threshold-condition
                  v-show="!rule.autoClear"
                  :title="$t('common.header.clear')"
                  :modelData.sync="rule.alarmClearRule"
                  :thresholdFields="thresholdFields"
                  class="position-relative rule-condition"
                ></f-threshold-condition>
              </div>
            </div>
          </el-form>
        </div>
      </div>
    </div>
    <div class="modal-dialog-footer" style="position: relative;">
      <el-button @click="goToPrevious" type="button" class="modal-btn-cancel">{{
        $t('common._common.previous')
      }}</el-button>
      <el-button @click="moveToNext" type="button" class="modal-btn-save">
        {{ $t('common.header.proceed_to_next') }}
        <img
          src="~assets/arrow-pointing-white-right.svg"
          width="17px"
          class="fR"
        />
      </el-button>
    </div>
  </div>
</template>

<script>
import Spinner from '@/Spinner'
import FThresholdCondition from 'pages/alarm/rule/component/FThresholdCondition'
import { isEmpty } from '@facilio/utils/validation'

export default {
  components: {
    Spinner,
    FThresholdCondition,
  },
  props: {
    sharedData: {
      type: Object,
    },
  },
  computed: {
    thresholdFields() {
      return this.$store.getters['formulabuilder/getAssetReadings'](
        this.rule.preRequsite.assetCategoryId,
        true
      )
    },
  },
  watch: {
    'rule.autoClear': function(newVal) {
      if (!newVal && !this.rule.alarmClearRule) {
        this.rule.alarmClearRule = {
          consecutiveoroverperiod: 1,
          dateRange: 1,
          operatorId: null,
          percentage: null,
          overperiod: 1,
          occurences: null,
          name: 'alarmClear',
          thresholdType: 1,
          workflow: null,
          criteria: null,
        }
      }
    },
    'rule.alarmTriggerRule': function() {
      if (!this.rule.alarmTriggerRule.isChanged) {
        this.rule.alarmTriggerRule.isChanged = true
      }
    },
    sharedData: {
      handler: function() {
        this.$forceUpdate()
      },
      deep: true,
    },
  },
  created() {},
  data() {
    return {
      selectedMetricFields: null,
      rule: {
        isPrerequest: false,
        enableRootCause: false,
        enableimpacts: false,
        isCondition: false,
        autoClear: true,
        reportBreakdown: false,
        preRequsite: {},
        alarmTriggerRule: {
          isChanged: true,
          consecutiveoroverperiod: null,
          dateRange: 1,
          operatorId: null,
          percentage: null,
          overperiod: null,
          occurences: null,
          name: 'alarmTriggerRule',
          actions: [],
          thresholdType: 1,
          workflow: null,
          criteria: null,
        },
        readingAlarmRuleContexts: [],
        alarmRCARules: [],
        deletedAlarmRCARules: [],
        alarmClearRule: {
          consecutiveoroverperiod: null,
          dateRange: 1,
          operatorId: null,
          percentage: null,
          overperiod: null,
          occurences: null,
          name: 'alarmClear',
          thresholdType: 1,
          workflow: null,
          criteria: null,
        },
      },
      modelData: null,
      module: 'readingRule',
      isSaving: false,
      isLoading: false,
    }
  },
  mounted() {
    Object.assign(this.rule, this.sharedData)
    if (isEmpty(this.rule.alarmClearRule)) {
      this.rule.autoClear = true
    }
  },
  methods: {
    moveToNext() {
      this.$emit('nextStep', this.rule)
    },

    goToPrevious() {
      this.$emit('goToPreviousStep', null)
    },
  },
}
</script>

<style lang="scss">
.rule-condition .condition-type-width {
  width: 33.3% !important;
}
</style>
