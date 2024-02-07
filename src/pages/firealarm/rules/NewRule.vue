<template>
  <el-dialog
    :visible.sync="visibility"
    width="50%"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog"
    :before-close="closeDialog"
  >
    <el-form :model="rule" :label-position="'top'" ref="rule">
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{ isEdit ? $t('common._common.edit') : $t('common._common._new') }}
            {{ $t('common.header.alarm_rule') }}
          </div>
        </div>
      </div>
      <div class="fc-right-dialog-body">
        <!-- Alarm Basic Details -->
        <div>
          <el-form-item
            :rules="[
              {
                required: true,
                message: this.$t('common._common.name_is_required'),
              },
            ]"
            prop="preRequsite.name"
          >
            <div class="fc-red-txt14 mT10">
              {{ $t('common._common.rule_name') }}
            </div>
            <el-input
              v-model="rule.preRequsite.name"
              autofocus
              :placeholder="$t('common.header.alarm_rule_name')"
              class="fc-input-full-border2"
            ></el-input>
          </el-form-item>
        </div>
        <div>
          <el-form-item :label="'Description'">
            <el-input
              v-model="rule.preRequsite.description"
              type="textarea"
              :autosize="{ minRows: 3, maxRows: 4 }"
              resize="none"
              class="fc-input-full-border-select2"
              :placeholder="$t('common._common.enter_desc')"
            ></el-input>
          </el-form-item>
        </div>
        <div>
          <el-form-item :label="'Alarm Type'">
            <el-radio
              :disabled="isEdit"
              v-model="resourceType"
              label="asset"
              :value="'asset'"
              class="fc-radio-btn"
              >{{ $t('common._common.asset') }}</el-radio
            >
            <el-radio
              :disabled="isEdit"
              v-model="resourceType"
              label="space"
              :value="'space'"
              class="fc-radio-btn"
              >{{ $t('common.space_asset_chooser.space') }}</el-radio
            >
          </el-form-item>
        </div>
        <div>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item
                v-if="resourceType === 'asset'"
                :rules="[
                  {
                    required: resourceType === 'asset',
                    message: 'Category is required',
                  },
                ]"
                prop="preRequsite.assetCategoryId"
              >
                <div class="fc-red-txt14">
                  {{ $t('common._common.asset_category') }}
                </div>
                <el-select
                  :disabled="isEdit"
                  v-model="rule.preRequsite.assetCategoryId"
                  filterable
                  @change="loadThresholdFields(true)"
                  :placeholder="$t('common._common.select')"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="(category, index) in assetCategory"
                    :key="index"
                    :label="category.displayName"
                    :value="parseInt(category.id)"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                :label="resourceType === 'asset' ? 'Assets' : 'Space'"
              >
                <el-input
                  v-model="resourceLabel"
                  disabled
                  class="fc-border-select fc-input-full-border-select2 width100"
                >
                  <i
                    @click="chooserVisibility = true"
                    slot="suffix"
                    style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                    class="el-input__icon el-icon-search"
                  ></i>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12" class="mT10">
              <div class="fc-red-txt14 mT10 mB10">
                {{ $t('common._common.threshold_metric') }}
              </div>
              <el-form-item
                :rules="[
                  {
                    required: true,
                    message: this.$t(
                      'common._common.please_select_threshold_metric'
                    ),
                  },
                ]"
                prop="metricFieldName"
              >
                <el-select
                  :disabled="isEdit"
                  filterable
                  v-model="rule.metricFieldName"
                  @change="
                    setMetricModule(rule.metricFieldName, rule.preRequsite)
                  "
                  :placeholder="$t('common._common.select')"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="field in thresholdFields"
                    :key="field.name + field.id"
                    :label="field.displayName"
                    :value="field.name"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <div class="fc-text-pink mT30">
            {{ $t('common.header.rule_condition') }}
          </div>
          <div class="pT10">
            <el-checkbox v-model="isPrerequest">{{
              $t('common.header.enable_pre_requisite')
            }}</el-checkbox>
          </div>
          <div v-if="isPrerequest">
            <div class="fc-dark-blue2-12 mT10">
              {{ $t('common.header.prerequisite') }}
            </div>
            <div class="fc-grey2-text13 mT5 mB10">
              {{ $t('common.header.condition_statement') }}
            </div>
            <f-condition-type
              :modelData.sync="rule.preRequsite"
              :title="'Prerequiste'"
              :isSeverity="true"
              :thresholdFields="thresholdFields"
              :metricFields="selectedMetricFields"
              :assetCategoryId="rule.preRequsite.assetCategoryId"
              class="p20 border-blue position-relative mT10"
            ></f-condition-type>
          </div>
        </div>
        <div>
          <div class="fc-dark-blue2-12 mT10 pT10 text-uppercase">
            {{ $t('common.header.alarm_condition') }}
          </div>
          <div class="fc-grey2-text13 mT10 mB10">
            {{ $t('common.header.condition_statement') }}
          </div>
          <f-condition-type
            :title="'Alarm Condition'"
            :isSeverity="true"
            :modelData.sync="rule.alarmTriggerRule"
            :metricFields="selectedMetricFields"
            :assetCategoryId="rule.preRequsite.assetCategoryId"
            :thresholdFields="thresholdFields"
          ></f-condition-type>
          <alarm-rule-action
            :emitData.sync="emitaction"
            :showUpdate="false"
            :thresholdFields="thresholdFields"
            v-model="rule.alarmTriggerRule"
            :model.sync="rule.alarmTriggerRule.actions"
            :isSeverity="true"
            @actions="appendAction"
            :category="null"
            :isUpdate="isUpdate"
          ></alarm-rule-action>
        </div>
        <!--- RCA -->
        <div class="mT20">
          <el-checkbox @change="changeClear()" v-model="rule.enableRootCause">{{
            $t('common.header.enable_root_cause_analysis')
          }}</el-checkbox>
        </div>
        <div v-if="rule.enableRootCause">
          <div class="fc-dark-blue2-12 mT30 text-uppercase">
            {{ $t('common.header.root_cause_condition') }}
          </div>
          <div class="fc-grey2-text13 mT10">
            {{ $t('common.header.create_single_multiple') }}
          </div>
          <alarm-trigger
            :title="'Root Cause'"
            :emitData.sync="triggerData"
            :isCondition="true"
            :context.sync="rule.alarmRCARules"
            :metricFields="selectedMetricFields"
            :metric="rule.metricFieldName"
            :category="
              rule.preRequsite.assetCategoryId
                ? getAssetCategory(rule.preRequsite.assetCategoryId)
                : { id: null }
            "
            @triggerRule="data => saveRule(data)"
            :thresholdFields="thresholdFields"
            :isActions="true"
            :showUpdate="false"
            :isUpdate="isUpdate"
          ></alarm-trigger>
        </div>
        <!--- Impacts ---->
        <div class="pT10">
          <el-checkbox @change="changeClear()" v-model="rule.enableimpacts">{{
            $t('common.header.enable_impact')
          }}</el-checkbox>
        </div>
        <div v-if="rule.enableimpacts">
          <div class="fc-dark-blue2-12 mT30 text-uppercase">
            {{ $t('common.header.impact') }}
          </div>
          <div class="fc-grey2-text13 mT10">
            {{ $t('common.header.create_single_multiple') }}
          </div>
          <div class="pT20 pB20">
            <el-checkbox
              v-model="rule.isCondition"
              @change="rule.readingAlarmRuleContexts = []"
              >{{ $t('common.header.condition_based') }}</el-checkbox
            >
          </div>
          <alarm-trigger
            v-if="rule.isCondition"
            :title="'Impact'"
            :emitData="impactData"
            :isFormule="true"
            :isCondition="rule.isCondition"
            :fields="alarmFields"
            :context.sync="rule.readingAlarmRuleContexts"
            :metric="rule.metricFieldName"
            :category="
              rule.preRequsite.assetCategoryId
                ? getAssetCategory(rule.preRequsite.assetCategoryId)
                : { id: null }
            "
            @triggerRule="data => addImpact(data)"
            :thresholdFields="thresholdFields"
            :isActions="true"
            :showUpdate="true"
          ></alarm-trigger>
          <div v-else>
            <f-field-update
              :emitData="fielUpdate"
              :isServerity="false"
              @actions="data => addImpact(data)"
              :fields="alarmFields"
              :model.sync="rule.readingAlarmRuleContexts"
              :editField="
                rule.readingAlarmRuleContexts &&
                rule.readingAlarmRuleContexts[0]
                  ? rule.readingAlarmRuleContexts[0].actions
                  : null
              "
              :category="getAssetCategory(rule.preRequsite.assetCategoryId)"
            ></f-field-update>
          </div>
        </div>
        <!-- Asset Breakdown -->
        <div class="pT10">
          <el-checkbox
            v-if="resourceType === 'asset'"
            v-model="rule.reportBreakdown"
            >{{ $t('common.header.report_downtime') }}</el-checkbox
          >
        </div>

        <div></div>
        <!-- Clear -->
        <div>
          <div class="fc-dark-blue2-12 mT20">
            {{ $t('common.header.alarm_clear_when') }}
          </div>
          <div class="fc-grey2-text13 mT10">
            {{ $t('common.header.condition_to_clear_an_alarm') }}
          </div>
        </div>
        <div class="mT20">
          <el-form-item>
            <el-radio
              @change="changeClear()"
              v-model="rule.isAutoClear"
              :label="true"
              :value="true"
              class="fc-radio-btn"
              >Auto Clear</el-radio
            >
            <el-radio
              @change="changeClear()"
              v-model="rule.isAutoClear"
              :label="false"
              :value="false"
              class="fc-radio-btn"
              >{{ $t('common.header.clear_based_condition') }}</el-radio
            >
          </el-form-item>
          <f-condition-type
            v-show="!rule.isAutoClear"
            :title="'Clear'"
            :modelData.sync="rule.alarmClearRule"
            :thresholdFields="thresholdFields"
            class="p20 border-blue position-relative"
          ></f-condition-type>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialog()">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button type="primary" class="modal-btn-save" @click="addRule()">{{
          $t('common._common._save')
        }}</el-button>
      </div>
    </el-form>
    <space-asset-chooser
      v-if="resourceType === 'space'"
      @associate="associateResource"
      :visibility.sync="chooserVisibility"
      :initialValues="{}"
      :query="resourceQuery"
      :showAsset="false"
      picktype="space"
    ></space-asset-chooser>
    <space-asset-multi-chooser
      v-else
      @associate="associateResource"
      :visibility.sync="chooserVisibility"
      :initialValues="resourceData"
      :query="resourceQuery"
      :showAsset="true"
      :disable="true"
    ></space-asset-multi-chooser>
  </el-dialog>
</template>
<script>
import FConditionType from '@/FConditionType'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import AlarmTrigger from '@/AlarmTrigger'
import { mapState, mapGetters } from 'vuex'
import FFieldUpdate from '@/workflow/FFieldUpdate'
import WorkFlowMixin from '@/mixins/WorkFlowMixin'
import AlarmRuleAction from '@/workflow/AlarmRuleAction'

export default {
  mixins: [WorkFlowMixin],
  props: ['visibility', 'ruleObj', 'ruleId', 'rules', 'isEditLoading'],
  components: {
    FConditionType,
    AlarmTrigger,
    AlarmRuleAction,
    SpaceAssetChooser,
    FFieldUpdate,
    SpaceAssetMultiChooser,
  },
  watch: {
    'rule.alarmTriggerRule': {
      handler() {
        if (this.isUpdate && this.rule.alarmTriggerRule) {
          this.rule.alarmTriggerRule.isChanged = true
        }
      },
      deep: true,
    },
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
  },
  computed: {
    ...mapGetters(['getAssetCategory', 'getAlarmSeverityByDisplayName']),
    customFields() {
      return this.alarmFields
    },
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
    isAsset() {
      return this.resourceType === 'asset'
    },
    isMultiResource() {
      return (
        this.isAsset &&
        (!this.rule.preRequsite || this.rule.preRequsite.assetCategoryId > 0)
      )
    },
    resourceData() {
      return {
        assetCategory: this.rule.preRequsite.assetCategoryId,
        isIncludeResource: this.isIncludeResource,
        selectedResources: Array.isArray(this.selectedResourceList)
          ? this.selectedResourceList.map(resource => ({
              id:
                resource && typeof resource === 'object'
                  ? resource.id
                  : resource,
            }))
          : this.selectedResourceList.id,
      }
    },
    resourceLabel() {
      if (this.rule.preRequsite.assetCategoryId > 0) {
        let category = this.assetCategory.filter(
          d => d.id === this.rule.preRequsite.assetCategoryId
        )
        let message
        let selectedCount = this.selectedResourceList.length
        if (selectedCount) {
          let includeMsg = this.isIncludeResource ? 'included' : 'excluded'
          if (selectedCount === 1) {
            return this.selectedResourceList[0].name
          }
          message =
            selectedCount +
            ' ' +
            category[0].name +
            (selectedCount > 1 ? 's' : '') +
            ' ' +
            includeMsg
        } else {
          message = 'All ' + category[0].name + 's selected'
        }
        return message
      } else if (this.selectedResourceList.id > 0) {
        return this.selectedResourceList.name
      }
      return null
    },
  },
  data() {
    return {
      orgId: this.$account.user.orgId,
      isUpdate: false,
      isEdit: false,
      emitaction: false,
      thresholdFields: [],
      triggerData: false,
      isPrerequest: false,
      impactData: false,
      fielUpdate: false,
      selectedMetricFields: null,
      rule: {
        enableRootCause: false,
        enableimpacts: false,
        isCondition: false,
        preRequsite: {
          name: null,
          consecutiveoroverperiod: 1,
          dateRange: 1,
          operatorId: null,
          percentage: null,
          overperiod: 1,
          occurences: null,
          description: null,
          assetCategoryId: null,
          thresholdType: 1,
          event: {
            moduleId: 37,
            activityType: 1,
          },
        },
        isAutoClear: true,
        autoClear: true,
        reportBreakdown: false,
        alarmTriggerRule: {
          isChanged: true,
          consecutiveoroverperiod: 1,
          dateRange: 1,
          operatorId: null,
          percentage: null,
          overperiod: 1,
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
        },
      },
      resourceQuery: null,
      isIncludeResource: false,
      metricFieldName: null,
      alarmFields: [],
      selectedResourceList: [],
      chooserVisibility: false,
      resourceType: 'asset',
    }
  },
  updated() {
    this.isUpdate = true
  },

  mounted() {
    this.loadAlarmFields()
    if (this.rules) {
      this.isEdit = true
      this.initData(this.ruleId)
    } else {
      this.isEdit = false
      self.rule = null
    }
  },
  methods: {
    changeClear() {
      this.$forceUpdate()
    },
    initData(ruleId) {
      this.rule = this.rules.alarmRule
      this.parseRule(this.rules.alarmRule)
    },
    parseRule(rule) {
      if (rule.preRequsite) {
        this.rule.metricFieldName = rule.alarmTriggerRule.readingField.name
      }
      if (rule.preRequsite.assetCategoryId > 0) {
        this.resourceType = 'asset'
        if (
          rule.preRequsite.includedResources &&
          rule.preRequsite.includedResources.length
        ) {
          this.isIncludeResource = true
          this.selectedResourceList = rule.preRequsite.includedResources.map(
            id => ({ id: id })
          )
        } else if (rule.preRequsite.excludedResources) {
          this.selectedResourceList = rule.preRequsite.excludedResources.map(
            id => ({ id: id })
          )
        }
        this.loadAssets()
      } else {
        rule.preRequsite.assetCategoryId = null
        this.selectedResourceList =
          rule.preRequsite.matchedResources[
            Object.keys(rule.preRequsite.matchedResources)[0]
          ]
        if (this.selectedResourceList.resourceType === 2) {
          this.resourceType = 'asset'
          this.loadAssets()
          this.loadThresholdFields()
        } else {
          this.resourceType = 'space'
          this.loadThresholdFields()
        }
      }
      this.parseCondition(rule.preRequsite)
      if (rule.preRequsite.workflow) {
        this.isPrerequest = true
      }
      if (rule.alarmClearRule) {
        rule.isAutoClear = false
        rule.autoClear = false
        this.parseCondition(rule.alarmClearRule)
      } else {
        rule.autoClear = true
        rule.isAutoClear = true
        rule.alarmClearRule = {
          consecutiveoroverperiod: 1,
          dateRange: 1,
          operatorId: null,
          percentage: null,
          overperiod: null,
          occurences: null,
          name: 'alarmClear',
          thresholdType: 1,
          workflow: null,
          criteria: null,
        }
      }
      this.parseCondition(rule.alarmTriggerRule)
      if (rule.alarmRCARules) {
        rule.enableRootCause = true
        rule.alarmRCARules.forEach(d => {
          if (d.parentRuleId > 0) {
            Object.keys(rule.alarmRCARules).forEach(ds => {
              if (rule.alarmRCARules[ds].id === d.parentRuleId) {
                let preRule = rule.alarmRCARules[ds]
                d.parentRuleName = preRule.name
              }
            })
          }
          this.parseCondition(d)
        })
      }
      if (rule.readingAlarmRuleContexts) {
        let enableimpacts = false
        let found = false
        for (let i = 0; i < rule.readingAlarmRuleContexts.length; i++) {
          if (rule.readingAlarmRuleContexts[i].actions != null) {
            for (
              let j = 0;
              j < rule.readingAlarmRuleContexts[i].actions.length;
              j++
            ) {
              if (
                rule.readingAlarmRuleContexts[i].actions[j].actionType == 22
              ) {
                found = true
                break
              }
            }
          }
        }
        rule.reportBreakdown = found
        if (!rule.reportBreakdown) {
          enableimpacts = true
        } else {
          if (rule.readingAlarmRuleContexts.length - 1 > 0) {
            enableimpacts = true
          }
        }
        if (enableimpacts) {
          rule.enableimpacts = true
          rule.readingAlarmRuleContexts.forEach(d => {
            d.thresholdType = 5
            if (d.workflow) {
              rule.isCondition = true
            }
            this.parseCondition(d)
          })
        }
      }
    },
    parseCondition(rule) {
      rule.readingFieldId = rule.readingFieldId < 0 ? null : rule.readingFieldId
      if (rule.thresholdType === 4) {
        rule.minFlapValue = rule.minFlapValue > 0 ? rule.minFlapValue : null
        rule.maxFlapValue = rule.maxFlapValue > 0 ? rule.maxFlapValue : null
        rule.flapInterval = rule.flapInterval / (60 * 1000)
        rule.flapFrequency = rule.flapFrequency > 0 ? rule.flapFrequency : null
      } else if (rule.thresholdType === 1) {
        rule.occurences = rule.occurences <= 0 ? null : rule.occurences
        rule.operatorId = rule.operatorId <= 0 ? null : rule.operatorId
        if (rule.workflow) {
          if (rule.occurences) {
            rule.occurences = rule.percentage < 0 ? null : rule.percentage
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
          } else {
            rule.occurences = null
            rule.overPeriod = 1
            rule.consecutiveoroverperiod = 1
          }
        }
      } else if (rule.thresholdType === 5 || rule.thresholdType === 6) {
        rule.dateRange =
          rule.dateRange && rule.dateRange !== -1 ? rule.dateRange : -1
        if (!rule.workflow) {
          rule.workflow = { occurences: {} }
        }
        rule.workflow.occurences = {
          occurences: rule.occurences,
          overPeriod: rule.overPeriod,
          consecutive: rule.consecutive,
        }
      } else if (rule.thresholdType === -1) {
        rule.thresholdType = 1
        rule.occurences = null
        rule.operatorId = null
      }
      delete rule.matchedResources
      delete rule.operator
    },
    associateResource(selectedObj) {
      if (this.resourceType === 'asset') {
        if (selectedObj.resourceList && selectedObj.resourceList.length) {
          this.selectedResourceList = selectedObj.resourceList
          this.isIncludeResource = selectedObj.isInclude
          this.loadThresholdFields(this.isMultiResource)
        }
      } else {
        if (this.resourceType === 'space') {
          this.isIncludeResource = true
        }
        this.selectedResourceList = selectedObj
        this.loadThresholdFields(false)
      }
      this.chooserVisibility = false
      this.resourceQuery = null
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    addRule() {
      this.$refs['rule'].validate(valid => {
        this.isUpdate = false
        if (!valid) {
          return
        } else {
          this.emitaction = true
        }
      })
    },
    appendAction(data) {
      if (this.rule.alarmTriggerRule.isChanged || data[0].isChanged) {
        this.rule.alarmTriggerRule.actions = data
      }
      if (this.rule.enableRootCause) {
        if (this.rule.enableimpacts) {
          if (this.rule.isCondition) {
            this.impactData = true
          } else {
            this.fielUpdate = true
          }
        } else {
          this.addImpact(null)
        }
      } else {
        if (this.rule.enableimpacts) {
          if (this.rule.isCondition) {
            this.impactData = true
          } else {
            this.fielUpdate = true
          }
        } else {
          this.rule.readingAlarmRuleContexts = null
          this.saveRule(null)
        }
      }
    },
    addImpact(data) {
      if (data) {
        if (this.rule.isCondition) {
          this.rule.readingAlarmRuleContexts = data
          this.rule.readingAlarmRuleContexts.forEach(d => {
            d.event = { moduleName: 'alarm', activityType: 3 }
          })
        } else {
          this.rule.readingAlarmRuleContexts = { actions: data }
          this.rule.readingAlarmRuleContexts.name = 'Impact 1'
          this.rule.readingAlarmRuleContexts.event = {
            moduleName: 'alarm',
            activityType: 3,
          }
        }
      }
      if (this.rule.enableRootCause) {
        this.triggerData = true
      } else {
        this.saveRule(null)
      }
    },
    saveRule(data) {
      if (data) {
        this.rule.alarmRCARules = data
      }
      let ruleObject = this.$helpers.cloneObject(this.rule)
      let metricId = this.thresholdFields.find(
        field => field.name === ruleObject.metricFieldName
      ).id
      ruleObject.preRequsite.readingFieldId = metricId
      if (!(ruleObject.preRequsite.assetCategoryId > 0)) {
        ruleObject.preRequsite.assetCategoryId = -1
        ruleObject.preRequsite.resourceId = this.selectedResourceList.id
      }
      if (
        !(
          ruleObject.preRequsite.thresholdType === 1 &&
          ruleObject.preRequsite.operatorId === null
        )
      ) {
        this.parseThersholdType(
          ruleObject.preRequsite,
          ruleObject.metricFieldName
        )
      }
      if (!this.isPrerequest) {
        ruleObject.preRequsite.workflow = null
        ruleObject.preRequsite.workflowId = -99
        ruleObject.preRequsite.operatorId = -99
        ruleObject.preRequsite.percentage = ''
      }
      if (this.selectedResourceList) {
        let fieldName = this.isIncludeResource
          ? 'includedResources'
          : 'excludedResources'
        let emptyFields =
          fieldName === 'includedResources'
            ? 'excludedResources'
            : 'includedResources'
        ruleObject.preRequsite[fieldName] = Array.isArray(
          this.selectedResourceList
        )
          ? this.selectedResourceList.map(resource => resource.id)
          : null
        ruleObject.preRequsite[emptyFields] = null
      }
      if (ruleObject.isAutoClear) {
        ruleObject.autoClear = true
        ruleObject.alarmClearRule = null
      } else {
        ruleObject.autoClear = false
        ruleObject.isAutoClear = false
      }
      if (
        ruleObject.alarmClearRule &&
        ruleObject.alarmClearRule.readingFieldId > 0
      ) {
        this.parseThersholdType(
          ruleObject.alarmClearRule,
          ruleObject.metricFieldName
        )
      } else {
        if (ruleObject.isAutoClear) {
          ruleObject.autoClear = true
          ruleObject.alarmClearRule = null
        } else {
          ruleObject.autoClear = false
          ruleObject.isAutoClear = false
        }
      }
      if (
        ruleObject.alarmTriggerRule &&
        (ruleObject.alarmTriggerRule.isChanged ||
          ruleObject.alarmTriggerRule.actions[0].isChanged)
      ) {
        ruleObject.alarmTriggerRule.readingFieldId = metricId
        this.parseThersholdType(
          ruleObject.alarmTriggerRule,
          ruleObject.metricFieldName
        )
        ruleObject.alarmTriggerRule.resourceId = this.selectedResourceList.id
      } else {
        ruleObject.alarmTriggerRule = null
      }
      if (ruleObject.alarmRCARules) {
        let spliceRule = []
        let deleteAlarm = []
        ruleObject.alarmRCARules.forEach((d, i) => {
          if (d.isChanged || d.actions[0].isChanged) {
            let problem = d.actions[0].templateJson.fieldMatcher.filter(
              d => d.field === 'problem'
            )
            if (problem.length > 0) {
              d.name = d.actions[0].templateJson.fieldMatcher.filter(
                d => d.field === 'problem'
              )[0].value
            }
            d.readingFieldId = metricId
            this.parseThersholdType(d, ruleObject.metricFieldName)
            delete d.alarmType
          } else if (d.isDeleted) {
            spliceRule.push(i)
            deleteAlarm.push(d)
          } else if (!ruleObject.enableRootCause) {
            spliceRule.push(i)
            deleteAlarm.push(d)
          } else {
            spliceRule.push(i)
          }
        })
        ruleObject.deletedAlarmRCARules = deleteAlarm
        spliceRule = spliceRule.reverse()
        spliceRule.forEach(d => {
          ruleObject.alarmRCARules.splice(d, 1)
        })
        if (!ruleObject.enableRootCause) {
          ruleObject.alarmRCARules = null
        }
      }
      if (ruleObject.enableimpacts) {
        if (ruleObject.isCondition) {
          ruleObject.readingAlarmRuleContexts.forEach(d => {
            d.thresholdType = 5
            d.readingFieldId = metricId
            this.parseThersholdType(d, ruleObject.metricFieldName)
          })
        } else {
          ruleObject.readingAlarmRuleContexts = [
            ruleObject.readingAlarmRuleContexts,
          ]
        }
      }
      if (ruleObject.reportBreakdown) {
        let reportDowntime = {
          actions: [
            {
              actionType: 22,
              templateJson: {},
            },
          ],
          name: 'Report Downtime',
          event: { moduleName: 'alarm', activityType: 1 },
        }
        if (ruleObject.readingAlarmRuleContexts) {
          let found = false
          for (let i = 0; i < ruleObject.readingAlarmRuleContexts.length; i++) {
            for (
              let j = 0;
              j < ruleObject.readingAlarmRuleContexts[i].actions.length;
              j++
            ) {
              if (
                ruleObject.readingAlarmRuleContexts[i].actions[j].actionType ==
                22
              ) {
                found = true
                break
              }
            }
          }
          if (!found) {
            ruleObject.readingAlarmRuleContexts.push(reportDowntime)
          }
        } else {
          ruleObject.readingAlarmRuleContexts = [reportDowntime]
        }
      } else {
        if (
          !ruleObject.readingAlarmRuleContexts &&
          ruleObject.readingAlarmRuleContexts != null
        ) {
          for (let k = 0; k < ruleObject.readingAlarmRuleContexts.length; k++) {
            for (
              let l = 0;
              l < ruleObject.readingAlarmRuleContexts[k].actions.length;
              l++
            ) {
              if (
                ruleObject.readingAlarmRuleContexts[k].actions[l].actionType ==
                22
              ) {
                ruleObject.readingAlarmRuleContexts.splice(k, 1)
              }
            }
          }
        }
      }
      if (this.isEdit) {
        delete ruleObject.allRuleList
        if (ruleObject.preRequsite && ruleObject.preRequsite.matchedResources) {
          delete ruleObject.preRequsite.matchedResources
        }
        if (
          ruleObject.alarmClearRule &&
          ruleObject.alarmClearRule.matchedResources
        ) {
          delete ruleObject.alarmClearRule.matchedResources
        }
        if (ruleObject.alarmRCARules) {
          ruleObject.alarmRCARules.forEach(d => {
            delete d.matchedResources
          })
        }
        if (ruleObject.readingAlarmRuleContexts) {
          ruleObject.readingAlarmRuleContexts.forEach(d => {
            delete d.matchedResources
          })
        }
      }
      if (ruleObject.alarmTriggerRule && ruleObject.alarmTriggerRule.actions) {
        let alarmSeverity = ruleObject.alarmTriggerRule.actions[0].templateJson.fieldMatcher.find(
          d => d.field === 'severity'
        ).value
        if (alarmSeverity) {
          ruleObject.preRequsite.alarmSeverityId = this.getAlarmSeverityByDisplayName(
            alarmSeverity
          ).id
        }
      }
      this.$util
        .newAddRule('alarm', { alarmRule: ruleObject }, this.isEdit)
        .then(rule => {
          this.$emit('saved', true)
          this.$emit('onSaveRule', rule)
          this.closeDialog()
        })
        .catch(error => {
          if (error) {
            this.triggerData = false
            this.impactData = false
            this.emitaction = false
            this.fielUpdate = false
            this.$message({
              message: error,
              type: 'error',
            })
          }
        })
    },
    loadAssets() {
      let self = this
      let url = '/asset/all'
      this.$http.get(url).then(function(response) {
        if (response.data) {
          if (!self.isnew || self.isDependant) {
            if (!self.isMultiResource) {
              self.selectedResource = response.data.assets.find(
                asset => asset.id === self.selectedResource.id
              )
            } else {
              self.selectedResourceList = response.data.assets.filter(asset =>
                self.selectedResourceList.some(
                  resource => resource.id === asset.id
                )
              )
            }
            self.loadThresholdFields(self.isMultiResource)
          }
        }
      })
    },
    addParam(name, type, data) {
      if (!data.workflow.parameters) {
        data.workflow.parameters = []
      }
      let params = data.workflow.parameters
      if (!params.find(param => param.name === name)) {
        params.push({ name: name, typeString: type || 'String' })
      }
    },
    parseThersholdType(readingRule, metricFieldName) {
      if (readingRule.thresholdType === 1) {
        let isExpressionValue =
          readingRule.percentage === '$' + '{previousValue}'

        if (
          !readingRule.hasOwnProperty('occurences') ||
          readingRule.occurences <= 0
        ) {
          readingRule.occurences =
            readingRule.occurences > 0 ? readingRule.occurences : null
          readingRule.workflow = {
            expressions: [],
            resultEvaluator: '',
            parameters: [{ name: 'resourceId', typeString: 'Number' }],
          }
          readingRule.workflow.expressions.push({
            name: 'a',
            aggregateString: 'lastValue',
            fieldName: metricFieldName,
            moduleName: this.thresholdFields.find(
              field => field.name === metricFieldName
            ).module.name,
            aggregateCondition: [],
            criteria: {
              pattern: '(1)',
              conditions: {
                1: {
                  fieldName: 'parentId',
                  operatorId: 9,
                  sequence: '1',
                  value: this.isMultiResource
                    ? '${resourceId' + '}'
                    : this.selectedResourceList.id,
                },
              },
            },
          })
          delete readingRule.criteria
          readingRule.workflow.resultEvaluator =
            'a' +
            this.getOperatorSymbol(readingRule.operatorId) +
            (isExpressionValue ? 'previousValue' : readingRule.percentage)
        } else {
          delete readingRule.criteria
          readingRule.workflow = {
            expressions: [],
            resultEvaluator: '',
            parameters: [{ name: 'resourceId', typeString: 'Number' }],
          }
          readingRule.workflow.expressions.push({
            name: 'a',
            aggregateString: 'count',
            fieldName: metricFieldName,
            moduleName: this.thresholdFields.find(
              field => field.name === metricFieldName
            ).module.name,
            aggregateCondition: [],
            criteria: {
              pattern: '(1)',
              conditions: {
                1: {
                  fieldName: 'parentId',
                  operatorId: 9,
                  sequence: '1',
                  value: this.isMultiResource
                    ? '${resourceId' + '}'
                    : this.selectedResourceList.id,
                },
              },
            },
          })
          readingRule.workflow.expressions[0].aggregateCondition.push({
            fieldName: metricFieldName,
            operatorId: readingRule.operatorId,
            sequence: '1',
            value: readingRule.percentage,
          })
          if (readingRule.consecutiveoroverperiod === 2) {
            readingRule.workflow.expressions[0].aggregateString = 'count'
            readingRule.workflow.expressions[0].orderByFieldName = 'ttime'
            readingRule.workflow.expressions[0].sortBy = 'desc'
            readingRule.workflow.expressions[0].limit = readingRule.occurences
          } else {
            readingRule.workflow.expressions[0].criteria.pattern = '(1 and 2)'
            readingRule.workflow.expressions[0].criteria.conditions['2'] = {
              fieldName: 'ttime',
              operatorId: 42,
              sequence: '2',
              value: readingRule.overperiod + ',${ttime}', // eslint-disable-line no-template-curly-in-string
            }
            this.addParam('ttime', 'Number', readingRule)
          }
          readingRule.percentage = readingRule.occurences
          readingRule.workflow.resultEvaluator = 'a==' + readingRule.occurences
        }
      } else if (readingRule.thresholdType === 2) {
        readingRule.workflow = {
          expressions: [],
          resultEvaluator: '',
          parameters: [{ name: 'resourceId', typeString: 'Number' }],
        }
        readingRule.workflow.expressions.push({
          name: 'a',
          aggregateString: readingRule.aggregation,
          fieldName: this.thresholdFields.find(
            field => field.name === metricFieldName
          ).name,
          moduleName: this.thresholdFields.find(
            field => field.name === metricFieldName
          ).module.name,
          criteria: {
            pattern: '(1 and 2)',
            conditions: {
              1: {
                fieldName: 'parentId',
                operatorId: 9,
                sequence: '1',
                value: this.isMultiResource
                  ? '${resourceId' + '}'
                  : this.selectedResourceList.id,
              },
              2: {
                fieldName: 'ttime',
                operatorId: 42,
                sequence: '2',
                value: readingRule.dateRange + ',${ttime}', // eslint-disable-line no-template-curly-in-string
              },
            },
          },
          aggregateCondition: [],
        })
        this.addParam('ttime', 'Number', readingRule)
        if (readingRule.aggregation === 'ccount') {
          readingRule.workflow.expressions[0].aggregateString = 'count'
          readingRule.workflow.expressions[0].orderByFieldName = 'ttime'
          readingRule.workflow.expressions[0].sortBy = 'desc'
          readingRule.workflow.expressions[0].limit = readingRule.percentage
        }
        readingRule.workflow.resultEvaluator =
          'a' +
          this.getOperatorSymbol(readingRule.operatorId) +
          readingRule.percentage
      } else if (readingRule.thresholdType === 3) {
        readingRule.workflow = {
          expressions: [],
          resultEvaluator: '',
          parameters: [{ name: 'resourceId', typeString: 'Number' }],
        }
        let baselineExp = {
          name: 'a',
          aggregateString: readingRule.aggregation,
          fieldName: this.thresholdFields.find(
            field => field.name === metricFieldName
          ).name,
          moduleName: this.thresholdFields.find(
            field => field.name === metricFieldName
          ).module.name,
          criteria: {
            pattern: '(1 and 2)',
            conditions: {
              1: {
                fieldName: 'parentId',
                operatorId: 9,
                sequence: '1',
                value: this.isMultiResource
                  ? '${resourceId' + '}'
                  : this.selectedResourceList.id,
              },
              2: {
                fieldName: 'ttime',
                operatorId: 42,
                sequence: '2',
                value: readingRule.dateRange + ',${ttime}', // eslint-disable-line no-template-curly-in-string
              },
            },
          },
          conditionSeqVsBaselineId: { 2: readingRule.baselineId },
        }
        this.addParam('ttime', 'Number', readingRule)
        readingRule.workflow.expressions.push(baselineExp)

        let baselineExpB = this.$helpers.cloneObject(baselineExp)
        baselineExpB.name = 'b'
        baselineExpB.conditionSeqVsBaselineId = null
        readingRule.workflow.expressions.push(baselineExpB)

        readingRule.workflow.resultEvaluator =
          '((b-a)/a)*100' +
          this.getOperatorSymbol(readingRule.operatorId) +
          readingRule.percentage
      } else if (readingRule.thresholdType === 4) {
        readingRule.flapInterval = readingRule.flapInterval * (60 * 1000)
      } else if (
        readingRule.thresholdType === 5 ||
        readingRule.thresholdType === 6
      ) {
        if (readingRule.workflow && readingRule.workflow.occurences) {
          Object.assign(readingRule, readingRule.workflow.occurences)
        }
      }
    },
  },
}
</script>
