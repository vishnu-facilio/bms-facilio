<template>
  <div class="rule-basic-info-container d-flex flex-direction-column">
    <div class="header f12 bold mT50 mL70 mR30 d-flex">
      <!-- Header  New Alarm Rule-->
      <div>
        <div class="text-uppercase">{{ $t('rule.create.new_alarm_rule') }}</div>
        <div class="fc-heading-border-width43 mT15"></div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="isLoading" class="loading-container d-flex">
      <Spinner :show="isLoading"></Spinner>
    </div>

    <!-- Basic Info container -->
    <div class="position-relative">
      <div class="rule-basic-info-content">
        <div class="rule-info-form">
          <!--Form Start-->
          <el-form
            ref="ruleInfoForm"
            :model="alarmRule"
            label-position="left"
            label-width="100px"
          >
            <div class="section-container flex-container">
              <!-- Rule Name-->
              <div class="form-one-column fc-label-required">
                <el-form-item
                  :rules="[
                    {
                      required: true,
                      message: this.$t('common.header.please_enter_rule_name'),
                    },
                  ]"
                  :label="$t('common.header.alarm_rule_name')"
                  label-width="150px"
                  prop="preRequsite.name"
                >
                  <el-input
                    v-model="alarmRule.preRequsite.name"
                    class="fc-input-full-border2 width575px"
                    :placeholder="$t('common.header.alarm_rule_name')"
                    :autofocus="true"
                  ></el-input>
                </el-form-item>
              </div>

              <!-- description -->
              <div class="form-one-column">
                <el-form-item
                  :label="$t('common.wo_report.report_description')"
                  label-width="150px"
                >
                  <el-input
                    type="textarea"
                    class="fc-input-full-border-textarea width575px"
                    resize="none"
                    :autosize="{ minRows: 6, maxRows: 4 }"
                    :placeholder="$t('common._common.enter_desc')"
                    v-model="alarmRule.preRequsite.description"
                  ></el-input>
                </el-form-item>
              </div>

              <!-- alarmType -->
              <div class="form-one-column">
                <el-form-item
                  :label="$t('common.header.alarm_type')"
                  label-width="150px"
                >
                  <el-radio-group v-model="resourceType">
                    <el-radio label="asset" class="fc-radio-btn">{{
                      $t('common._common.asset')
                    }}</el-radio>
                    <el-radio label="space" class="fc-radio-btn">{{
                      $t('common.space_asset_chooser.space')
                    }}</el-radio>
                  </el-radio-group>
                </el-form-item>
              </div>

              <!-- <el-row> -->
              <!-- <el-col :span="12"> -->
              <el-row>
                <el-col :span="12">
                  <div class="form-two-column" v-if="resourceType === 'asset'">
                    <el-form-item
                      :label="$t('common._common.asset_category')"
                      label-width="150px"
                      class="section-items"
                    >
                      <el-select
                        :disabled="isEdit"
                        v-model="alarmRule.preRequsite.assetCategoryId"
                        filterable
                        @change="
                          loadThresholdFields(
                            true,
                            alarmRule.preRequsite.assetCategoryId
                          )
                        "
                        :placeholder="$t('common._common.select')"
                        class="fc-input-full-border-select2 width213px"
                      >
                        <el-option
                          v-for="(category, index) in assetCategory"
                          :key="index"
                          :label="category.displayName"
                          :value="parseInt(category.id)"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="form-two-column">
                    <el-form-item
                      :label="resourceType === 'asset' ? 'Assets' : 'Space'"
                      label-width="150px"
                      class="section-items asset-or-space-label"
                      :class="{ labelPL0: resourceType === 'space' }"
                    >
                      <el-input
                        v-model="resourceLabel"
                        disabled
                        class="fc-border-select fc-input-full-border-select2 width213px"
                      >
                        <i
                          @click="chooserVisibility = true"
                          slot="suffix"
                          style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                          class="el-input__icon el-icon-search"
                        ></i>
                      </el-input>
                    </el-form-item>
                  </div>
                </el-col>
              </el-row>

              <!-- </el-col> -->
              <!-- <el-col :span="2"></el-col>
              <el-col :span="12" style="float: right;">-->

              <!-- </el-col> -->
              <!-- </el-row> -->
              <!-- <div class="form-one-column">
                <el-form-item label="Asset" label-width="150px">
                  <el-select
                    v-model="modelData.assetCategory"
                    filterable
                    clearable
                    placeholder="Select"
                    class="fc-input-full-border-select2 width100"
                  >
                  </el-select>
                </el-form-item>
              </div>-->
              <div class="form-one-column fc-label-required">
                <el-form-item
                  :rules="[
                    {
                      required: true,
                      message: this.$t('common.header.please_select_a_field'),
                    },
                  ]"
                  :required="true"
                  :label="$t('common.header.trigger_metric')"
                  prop="preRequsite.readingFieldId"
                  label-width="150px"
                >
                  <el-select
                    :disabled="isEdit"
                    filterable
                    v-model="alarmRule.preRequsite.readingFieldId"
                    @change="
                      setMetricModule(
                        alarmRule.preRequsite.readingFieldId,
                        alarmRule.preRequsite
                      )
                    "
                    :placeholder="$t('common._common.select')"
                    class="fc-input-full-border-select2 width575px"
                  >
                    <el-option
                      v-for="field in thresholdFields"
                      :key="field.name + field.id"
                      :label="field.displayName"
                      :value="field.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </div>

              <!-- Alarm Message -->
              <div class="form-one-column fc-label-required">
                <el-form-item
                  :label="$t('common.header.alarm_message')"
                  :rules="[
                    {
                      required: true,
                      message: this.$t(
                        'common.header.please_enter_alarm_message'
                      ),
                    },
                  ]"
                  prop="action.message"
                  label-width="150px"
                  :required="true"
                >
                  <el-input
                    type="textarea"
                    class="fc-input-full-border-textarea width575px"
                    resize="none"
                    :autosize="{ minRows: 6, maxRows: 4 }"
                    :placeholder="$t('common._common.type_your_alarm_message')"
                    v-model="alarmRule.action.message"
                  ></el-input>
                </el-form-item>
              </div>

              <!-- Severity -->
              <div
                class="form-one-column visibility-visible-actions fc-label-required"
              >
                <el-form-item
                  :label="$t('common._common.severity')"
                  label-width="150px"
                  :rules="[
                    {
                      required: true,
                      message: this.$t('common.header.please_select_severity'),
                    },
                  ]"
                  prop="action.severity"
                  :required="true"
                >
                  <el-select
                    class="fc-input-full-border-select2 width575px"
                    v-model="alarmRule.action.severity"
                    collapse-tags
                  >
                    <el-option
                      v-for="label in alarmseverity"
                      :key="label.id"
                      :label="label.displayName"
                      :value="label.severity"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </div>
              <div class="form-one-column visibility-visible-actions">
                <el-form-item
                  :label="$t('common._common.fault_type')"
                  label-width="150px"
                >
                  <el-select
                    class="fc-input-full-border-select2 width575px"
                    v-model="alarmRule.faultType"
                  >
                    <el-option
                      v-for="(faultTypeData, key) in faultTypes"
                      :key="key"
                      :label="faultTypeData"
                      :value="parseInt(key)"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
              </div>
              <!-- Problem -->
              <div
                v-if="alarmRule.action"
                class="form-one-column visibility-visible-actions"
              >
                <el-form-item
                  :label="$t('common._common.problem')"
                  label-width="150px"
                >
                  <!-- <f-multi-text-box :model.sync="alarmRule.action.problem" name="problem" class="width575px"></f-multi-text-box> -->
                  <el-input
                    class="fc-input-full-border2 mT10 width575px"
                    v-model="alarmRule.action.problem"
                  ></el-input>
                </el-form-item>
              </div>

              <!-- Possible Causes -->
              <div
                v-if="alarmRule.action"
                class="form-one-column visibility-visible-actions"
              >
                <el-form-item
                  :label="$t('common._common.possible_causes')"
                  label-width="150px"
                >
                  <f-multi-text-box
                    :model.sync="alarmRule.action.possibleCauses"
                    name="possibleCauses"
                    class="width575px"
                  ></f-multi-text-box>
                </el-form-item>
              </div>

              <!-- Recommendations -->
              <div
                v-if="alarmRule.action"
                class="form-one-column visibility-visible-actions"
              >
                <el-form-item
                  :label="$t('common._common.recommendation')"
                  label-width="150px"
                >
                  <f-multi-text-box
                    :model.sync="alarmRule.action.recommendation"
                    name="recommendation"
                    class="width575px"
                  ></f-multi-text-box>
                </el-form-item>
              </div>
              <div class="modal-dialog-footer">
                <!-- <el-form-item> -->
                <el-button
                  @click="moveToNext"
                  type="button"
                  class="modal-btn-save width100"
                >
                  {{ $t('common.header.proceed_to_next') }}
                  <img
                    src="~assets/arrow-pointing-white-right.svg"
                    width="17px"
                    class="fR"
                  />
                </el-button>
                <!-- </el-form-item> -->
              </div>
            </div>
          </el-form>
        </div>
      </div>

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
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import RuleMixin from '@/mixins/RuleMixin'
import Spinner from '@/Spinner'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import FMultiTextBox from '@/FMultiTextBox'
import { isEmpty } from '@facilio/utils/validation'

export default {
  mixins: [RuleMixin],
  components: {
    Spinner,
    FMultiTextBox,
    SpaceAssetChooser,
    SpaceAssetMultiChooser,
  },
  props: {
    sharedData: {
      type: Object,
    },
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('view/loadModuleMeta', 'readingrule')
  },
  computed: {
    isEdit() {
      return this.$route.name === 'rule-creation-edit'
    },
    customFields() {
      return this.alarmFields
    },
    faultTypes() {
      let enumMap = null
      if (this.metaInfo && this.metaInfo.fields != null) {
        let faultFields = this.metaInfo.fields.filter(
          d => d.displayName === 'faultType'
        )
        enumMap = faultFields[0].enumMap
      }
      return enumMap
      //  else {
      //   return null
      // }
    },
    ...mapState({
      assetCategory: state => state.assetCategory,
      alarmseverity: state => state.alarmSeverity,
      metaInfo: state => state.view.metaInfo,
    }),
    isAsset() {
      return this.resourceType === 'asset'
    },
    isMultiResource() {
      return (
        this.isAsset &&
        (!this.alarmRule.preRequsite ||
          this.alarmRule.preRequsite.assetCategoryId > 0)
      )
    },
    resourceData() {
      // if (isEmpty(this.alarmRule.selectedResourceList)) {
      //   return null
      // }
      return {
        assetCategory: this.alarmRule.preRequsite.assetCategoryId,
        isIncludeResource: this.alarmRule.isIncludeResource,
        selectedResources: Array.isArray(this.alarmRule.selectedResourceList)
          ? this.alarmRule.selectedResourceList.map(resource => ({
              id:
                resource && typeof resource === 'object'
                  ? resource.id
                  : resource,
            }))
          : this.alarmRule.selectedResourceList.id,
      }
    },
    resourceLabel() {
      if (this.alarmRule.preRequsite.assetCategoryId > 0) {
        let category = this.assetCategory.filter(
          d => d.id === this.alarmRule.preRequsite.assetCategoryId
        )
        let message
        let selectedCount = this.alarmRule.selectedResourceList.length
        if (selectedCount) {
          let includeMsg = this.alarmRule.isIncludeResource
            ? 'included'
            : 'excluded'
          // if (selectedCount === 1) {
          //   return this.alarmRule.alarmTriggerRule.selectedResourceList[0].name
          // }
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
      } else if (this.alarmRule.selectedResourceList.id > 0) {
        return this.alarmRule.selectedResourceList.name
      }
      return null
    },
  },
  data() {
    return {
      dataFromTemplate: null,
      metricFieldName: null,
      templateId: this.$route.params.templateid,
      ruleId: this.$route.params.id,
      modelData: {},
      resourceQuery: null,
      resourceType: 'asset',
      faultTypeData: null,
      alarmRule: {
        preRequsite: {
          name: null,
          description: null,
          assetCategoryId: null,
          thresholdType: 1,
          event: {
            moduleId: 37,
            activityType: 1,
          },
        },
        action: {
          problem: null,
          possibleCauses: [],
          recommendation: [],
          severity: null,
          message: null,
        },
        // alarmTriggerRule: {
        //   faultType: null,
        // },
        faultType: null,
        module: 'readingRule',
        isIncludeResource: false,
        alarmFields: [],
        selectedResourceList: [],
      },
      thresholdFields: [],
      isLoading: false,
      isSaving: false,
      chooserVisibility: false,
      temp: null,
    }
  },
  mounted() {
    this.fetchRules()
  },
  methods: {
    moveToNext() {
      this.$refs['ruleInfoForm'].validate(valid => {
        if (valid) {
          if (this.alarmRule.action && this.alarmRule.action.severity) {
            this.alarmRule.preRequsite.alarmSeverityId = this.alarmseverity.find(
              d => this.alarmRule.action.severity === d.displayName
            ).id
          }
          this.$emit('nextStep', this.alarmRule)
        }
      })
    },
    fetchRules() {
      let self = this
      if (this.templateId) {
        this.dataFromTemplate = this.$helpers.cloneObject(
          this.$store.state.ruleTemplate.ruleContext
        )
        this.fetchRuleNew(this.dataFromTemplate)
      } else if (this.ruleId) {
        self.$util.fetchRules('alarm', this.ruleId).then(data => {
          this.fetchRuleNew(data)
        })
      }
    },
    fetchRuleNew(data) {
      this.isLoading = false
      let ruleObj = this.$helpers.cloneObject(data.alarmRule)
      Object.assign(this.alarmRule, ruleObj)
      this.alarmRule.preRequsite.action = {
        problem: [],
        possibleCauses: [],
        recommendation: [],
        message: null,
        severity: null,
      }
      if (this.alarmRule.preRequsite.assetCategoryId > 0) {
        this.resourceType = 'asset'
        if (
          this.alarmRule.preRequsite.includedResources &&
          this.alarmRule.preRequsite.includedResources.length
        ) {
          this.alarmRule.isIncludeResource = true
          this.alarmRule.selectedResourceList = this.alarmRule.preRequsite.includedResources.map(
            id => ({ id: id })
          )
        } else if (this.alarmRule.preRequsite.excludedResources) {
          this.alarmRule.selectedResourceList = this.alarmRule.preRequsite.excludedResources.map(
            id => ({ id: id })
          )
        }
        this.loadThresholdFields(
          true,
          this.alarmRule.preRequsite.assetCategoryId
        )
        // this.parseCondition(this.alarmRule.preRequsite)
      } else {
        this.resourceType = 'space'
        this.alarmRule.selectedResourceList = this.alarmRule.preRequsite.matchedResources[
          this.alarmRule.preRequsite.resourceId
        ]
        this.loadThresholdFields(false, -1, this.alarmRule.selectedResourceList)
      }
      if (this.alarmRule.preRequsite.workflow) {
        this.alarmRule.isPrerequest = true
      }
      // this.parseCondition(this.alarmRule.alarmTriggerRule)
      this.alarmRule.faultType = !isEmpty(
        this.alarmRule.alarmTriggerRule.faultType
      )
        ? this.alarmRule.alarmTriggerRule.faultType
        : null
      if (this.alarmRule.alarmTriggerRule.actions.length > 0) {
        if (this.alarmRule.alarmTriggerRule.actions[0]) {
          if (this.alarmRule.alarmTriggerRule.actions[0].template) {
            let template = this.alarmRule.alarmTriggerRule.actions[0].template
              .originalTemplate
            this.$set(this.alarmRule.action, `message`, template.message)
            this.$set(this.alarmRule.action, `problem`, template.problem)
            this.alarmRule.action.possibleCauses = template.possibleCauses
              ? template.possibleCauses.split(' \n ')
              : []
            this.alarmRule.action.recommendation = template.recommendation
              ? template.recommendation.split(' \n ')
              : []
            this.alarmRule.action.severity = template.severity
            if (template.impact) {
              this.alarmRule.impactsContext = {}
              this.alarmRule.impactsContext.id = template.impact.cost[0]
              // template.impact.cost = [ruleObject.impactsContext.id]
              this.alarmRule.enableimpacts = true
            }
          } else {
            this.alarmRule.alarmTriggerRule.actions[0].templateJson.fieldMatcher.forEach(
              context => {
                this.$set(
                  this.alarmRule.action,
                  context.field,
                  context.value ? context.value.split('\n') : []
                )
              }
            )
            this.alarmRule.action.severity = this.alarmRule.alarmTriggerRule.actions[0].templateJson.fieldMatcher[3].value
          }
        }
      }
      if (this.alarmRule.alarmRCARules.length > 0) {
        this.alarmRule.alarmRCARules = this.alarmRule.alarmRCARules
      }
      if (this.alarmRule.readingAlarmRuleContexts) {
        this.alarmRule.readingAlarmRuleContexts.forEach(context => {
          if (!isEmpty(context.actions)) {
            let actions = context.actions
            actions.forEach(d => {
              if (d.actionType === 22) {
                this.alarmRule.reportBreakdown = true
              }
            })
          }
        })
      }
    },
    loadAssets() {
      let self = this
      let url = '/asset/all'
      this.$http.get(url).then(function(response) {
        if (response.data) {
          // self.assetList = response.data.assets
          if (!self.isnew || self.isDependant) {
            if (!self.isMultiResource) {
              self.alarmRule.preRequsite.selectedResource = response.data.assets.find(
                asset => asset.id === self.selectedResource.id
              )
            } else {
              self.alarmRule.preRequsite.selectedResourceList = response.data.assets.filter(
                asset =>
                  self.alarmRule.preRequsite.selectedResourceList.some(
                    resource => resource.id === asset.id
                  )
              )
            }
            self.loadThresholdFields(self.isMultiResource)
          }
        }
      })
    },
    ruleCondition() {
      if (this.ruleId) {
        this.parseCondition(this.alarmRule.preRequsite)
        this.parseCondition(this.alarmRule.alarmTriggerRule)
      }
    },
    associateResource(selectedObj) {
      if (this.resourceType === 'asset') {
        if (selectedObj.resourceList && selectedObj.resourceList.length) {
          this.alarmRule.selectedResourceList = selectedObj.resourceList
          this.alarmRule.isIncludeResource = selectedObj.isInclude
          // this.parseLabel = this.resourceLabel(this.alarmRule.selectedResourceList)
          this.loadThresholdFields(
            this.isMultiResource,
            this.alarmRule.preRequsite.assetCategoryId
          )
        }
      } else {
        if (this.resourceType === 'space') {
          this.alarmRule.isIncludeResource = true
        }
        this.alarmRule.selectedResourceList = selectedObj
        this.loadThresholdFields(false, null, selectedObj)
        // this.parseLabel = this.resourceLabel(this.alarmRule.selectedResourceList)
      }
      this.chooserVisibility = false
      this.resourceQuery = null
    },
  },
}
</script>

<style lang="scss">
.asset-or-space-label .el-form-item__label {
  padding-left: 70px;
}
.labelPL0 .el-form-item__label {
  padding-left: 0 !important;
}
.labelRemovePadding .el-form-item__label {
  padding-left: 0 !important;
}
</style>
