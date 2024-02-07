<!-- -->
<template>
  <div>
    <el-dialog
      :visible.sync="visibilityshow"
      :fullscreen="true"
      :append-to-body="true"
      :before-close="closeDialog"
      custom-class="fc-dialog-form fc-dialog-right custom-rule-dialog setup-dialog50 setup-dialog"
      style="z-index: 1999"
    >
      <div id="new-custom-rule">
        <error-banner
          :error="error"
          :errorMessage="errorMessage"
        ></error-banner>
        <el-form :model="data" ref="newCustomRule" :label-position="'top'">
          <div class="new-header-container">
            <div class="new-header-modal">
              <div class="new-header-text">
                <div class="setup-modal-title">
                  {{ (isNew ? 'New ' : 'Edit ') + title }}
                </div>
              </div>
            </div>
          </div>
          <div class="new-body-modal">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item prop="name">
                  <p class="fc-input-label-txt">Name</p>
                  <el-input
                    :autofocus="true"
                    v-model="data.rule.name"
                    class="fc-input-full-border-select2"
                    :placeholder="$t('setup.setupLabel.action_name')"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="siteId">
                  <p class="fc-input-label-txt">Site</p>
                  <el-select
                    v-model="data.rule.siteId"
                    class="fc-input-full-border-select2 width100"
                    placeholder="Select Site"
                  >
                    <el-option
                      v-for="site in sites"
                      :key="site.id"
                      :label="site.name"
                      :value="site.id"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item prop="description">
                  <p class="fc-input-label-txt">
                    {{ $t('space.sites.site_description') }}
                  </p>
                  <el-input
                    v-model="data.rule.description"
                    :min-rows="1"
                    type="textarea"
                    :autosize="{ minRows: 2, maxRows: 4 }"
                    :placeholder="$t('common._common.enter_desc')"
                    reseize="none"
                    class="fc-input-full-border-select2"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12" class="utility-block">
                <p class="subHeading-pink-txt">
                  {{ $t('maintenance._workorder.execute_on') }}
                </p>
                <p class="small-description-txt mB10">
                  {{ $t('setup.setupLabel.notify_rule_executed') }}
                </p>
                <el-form-item prop="activityType">
                  <el-select
                    v-model="data.rule.event.activityType"
                    placeholder="Select"
                    class="width100 fc-input-full-border-select2"
                    :disabled="isActivityDisabled"
                  >
                    <el-option
                      v-for="(type, index) in activityTypes"
                      :key="index"
                      :label="type.label"
                      :value="type.value"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <div
              class="fc-div-bg-border mB20"
              v-if="
                data.rule.event.activityType === 524288 ||
                  data.rule.event.activityType === 1048576
              "
            >
              <!-- Scheduled block Start-->
              <div v-if="data.rule.event.activityType === 524288">
                <el-row>
                  <el-col :span="12">
                    <p class="fc-input-label-txt">Date Field</p>
                    <el-select
                      v-model="data.rule.dateFieldId"
                      placeholder="Select"
                      class="width300px"
                    >
                      <el-option
                        v-for="(fld, index) in dateFields"
                        :key="index"
                        :label="fld.displayName"
                        :value="fld.id"
                      ></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="12" class="pL40">
                    <p class="fc-input-label-txt mB10">
                      {{ $t('setup.setupLabel.scheduled_type') }}
                    </p>
                    <el-radio-group
                      v-model="data.rule.scheduleType"
                      class="criteria-radio-label"
                    >
                      <el-radio
                        class="fc-radio-btn"
                        :label="1"
                        :key="1"
                        v-if="ruleType === '26'"
                        >Before</el-radio
                      >
                      <el-radio class="fc-radio-btn" :label="2" :key="2"
                        >On</el-radio
                      >
                      <el-radio class="fc-radio-btn" :label="3" :key="3"
                        >After</el-radio
                      >
                    </el-radio-group>
                  </el-col>
                </el-row>
                <el-row
                  class="mT30"
                  v-if="[1, 3].includes(parseInt(data.rule.scheduleType))"
                >
                  <el-col :span="8">
                    <p class="fc-input-label-txt">Days</p>
                    <el-select
                      v-model="dateObject.days"
                      clearable
                      placeholder="Select"
                      class="fc-input-full-border-select2"
                    >
                      <el-option
                        v-for="index in 10"
                        :label="index"
                        :key="index + 1"
                        :value="index"
                      ></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="8" v-if="checkDateTimeField">
                    <p class="fc-input-label-txt">Hours</p>
                    <el-select
                      v-model="dateObject.hours"
                      clearable
                      class="fc-input-full-border-select2"
                    >
                      <el-option
                        v-for="index in $constants.HOURS"
                        :label="index"
                        :key="index + 1"
                        :value="index"
                      >
                      </el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="8" v-if="checkDateTimeField">
                    <p class="fc-input-label-txt">Mins</p>
                    <el-select
                      v-model="dateObject.minute"
                      placeholder="Select"
                      class="fc-input-full-border-select2"
                    >
                      <el-option
                        v-for="index in $constants.MINUTES"
                        :label="index"
                        :key="index + 1"
                        :value="index"
                      >
                      </el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="8" v-else>
                    <p class="fc-input-label-txt">Time</p>
                    <el-select
                      v-model="data.rule.time"
                      collapse-tags
                      class="fc-input-full-border-select2 width100 fc-tag"
                    >
                      <el-option
                        v-for="time in timesOption"
                        :label="time"
                        :value="time"
                        :key="time"
                      ></el-option>
                    </el-select>
                  </el-col>
                </el-row>
              </div>
              <!-- Scheduled block Start-->
              <div v-else-if="data.rule.event.activityType === 1048576">
                <el-row>
                  <!-- Field block Start-->
                  <div
                    class="approve-form-item"
                    v-if="data.rule.event.activityType === 1048576"
                  >
                    <el-form-item
                      prop="activityType"
                      class="fc-tag p0 pL10 pR10"
                    >
                      <p class="fc-input-label-txt">
                        Field
                        {{
                          selectedFieldObject.length > 0
                            ? '(' + selectedFieldObject.length + ')'
                            : ''
                        }}
                      </p>
                      <el-select
                        :multiple-limit="3"
                        v-model="selectedFieldObject"
                        multiple
                        placeholder="Select"
                        class="fc-input-full-border-select2 width50"
                      >
                        <el-option
                          v-for="(fld, index) in moduleFields"
                          :key="index"
                          :label="fld.displayName"
                          :value="fld.id"
                        ></el-option>
                      </el-select>
                      <el-button
                        v-if="selectedFieldObject.length < 3"
                        slot="reference"
                        class="all-rule-btn"
                      >
                        <img src="~assets/add-icon.svg" class="mL10" />
                      </el-button>
                    </el-form-item>
                  </div>
                  <!-- Field block End-->
                </el-row>
              </div>
            </div>
            <new-criteria-builder
              ref="criteriaBuilder"
              v-model="data.rule.criteria"
              :showSiteField="showSiteField"
              :showFormIDField="showFormIDField"
              :exrule="data.rule.criteria"
              @condition="somefnt"
              :module="module"
              :isRendering.sync="criteriaRendered"
              :title="'Specify rules for ' + module + ' action'"
            ></new-criteria-builder>
            <div class="form-input"></div>
            <event-dialog-helper
              v-if="actionType === 'notification'"
              @actions="addAction()"
              ref="eventDialog"
              :ruleType="ruleType"
              :rule="data"
              :module="module"
              :moduleFields="moduleFields"
              :actions="actions"
            >
            </event-dialog-helper>
            <div v-if="actionType === 'severityAction'">
              <el-row>
                <el-col :span="20" class="utility-block">
                  <div class="fc-text-pink text-uppercase mT20">
                    Severity Change
                  </div>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-input
                    disabled
                    v-model="severity"
                    class="fc-input-full-border2 width100"
                  ></el-input>
                </el-col>
                <el-col :span="12">
                  <el-form-item>
                    <el-select
                      v-if="
                        fieldMatchers &&
                          fieldMatchers[0] &&
                          fieldMatchers[0].templateJson
                      "
                      class="fc-input-full-border-select2 width100"
                      v-model="
                        fieldMatchers[0].templateJson.resultWorkflowContext
                          .expressions[0].constant
                      "
                      collapse-tags
                    >
                      <el-option
                        v-for="label in alarmseverity"
                        :key="label.id"
                        :label="label.displayName"
                        :value="String(label.id)"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </div>
            <div v-if="actionType === 'controlLogic'">
              <control-logic :name="'dfd'"></control-logic>
            </div>
          </div>
          <div class="modal-dialog-footer">
            <el-button @click="closeDialog()" class="modal-btn-cancel"
              >CANCEL</el-button
            >
            <el-button
              type="primary"
              class="modal-btn-save"
              @click="save"
              :loading="saving"
            >
              {{ saving ? 'Saving...' : 'SAVE' }}</el-button
            >
          </div>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import ControlLogic from './ControlLogic'
import EventDialogHelper from '@/EventDialogHelper'
import ErrorBanner from '@/ErrorBanner'
import NotificationHelper from 'pages/setup/actions/NotificationHelper'
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import { isDateTimeField } from '@facilio/utils/field'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  props: [
    'isNew',
    'isSummary',
    'visibilityshow',
    'mailWidth',
    'ruleType',
    'parentRuleId',
    'title',
    'ruleActions',
    'actionType',
    'details',
  ],
  mixins: [NotificationHelper],
  data() {
    return {
      severity: 'Severity',
      fieldMatchers: [],
      activityTypes: [
        {
          label: 'Create',
          value: 1,
        },
        {
          label: 'Severity Change',
          value: 1024,
        },
        {
          label: 'Create and Severity Change',
          value: 1025,
        },
        {
          label: 'Clear',
          value: 2048,
        },
        {
          label: 'On Date',
          value: 524288,
        },
        {
          label: 'Field Change',
          value: 1048576,
        },
        {
          label: 'Delete',
          value: 4,
        },
      ],
      timesOption: [],
      actions: ['email', 'sms', 'mobile'],
      error: false,
      isUpdate: false,
      errorMessage: '',
      selectedFieldObject: [],
      dateFields: [],
      criteriaRendered: false,
      moduleMeta: null,
      saving: false,
      data: {
        rule: {
          name: '',
          scheduleType: 3,
          description: '',
          ruleType: null,
          event: {
            moduleName: 'newreadingalarm',
            activityType: 1,
          },
          dateFieldId: null,
          time: null,
          fields: [],
          criteria: null,
        },
        siteId: '',
        actions: [],
      },
      dateObject: {
        days: null,
        minute: 0,
        hours: 0,
        minu: null,
      },
      closeWo: {
        actionType: 12,
      },
      ruleDialog: true,
    }
  },
  components: {
    ErrorBanner,
    EventDialogHelper,
    ControlLogic,
    NewCriteriaBuilder,
  },
  created() {
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('loadSite')
  },
  computed: {
    ...mapState({
      alarmseverity: state => state.alarmSeverity,
      sites: state => state.site,
    }),
    moduleFields() {
      if (this.criteriaRendered) {
        return
      }
      if (
        this.$refs.criteriaBuilder &&
        this.$refs.criteriaBuilder.moduleMetaObject &&
        this.$refs.criteriaBuilder.moduleMetaObject.fields
      ) {
        return this.$refs.criteriaBuilder.moduleMetaObject.fields
      }
      return null
    },
    showSiteField() {
      let workFlowActionHash = this.$constants.WorkFlowAction.module[
        this.module
      ]
      return workFlowActionHash && workFlowActionHash.showSiteField
    },
    showFormIDField() {
      let workFlowActionHash = this.$constants.WorkFlowAction.module[
        this.module
      ]
      return workFlowActionHash && workFlowActionHash.showFormIDField
    },
    checkDateTimeField() {
      let { dateFields, data } = this
      let { rule } = data || {}
      let { dateFieldId } = rule || {}
      let field =
        (dateFields || []).find(fld => fld.fieldId === dateFieldId) || {}

      return !isEmpty(field) ? isDateTimeField(field) : false
    },
    isActivityDisabled() {
      let { data } = this || {}
      let { actions } = data || {}
      let firstActionType = this.$getProperty(actions, '0.actionType', null)

      return !isEmpty(actions.length) && [11, 12].includes(firstActionType)
    },
  },
  watch: {
    moduleFields(newVal) {
      if (newVal != null) {
        let dateFld = []
        newVal.filter(d => {
          if (
            d.dataTypeEnum._name === 'DATE' ||
            d.dataTypeEnum._name === 'DATE_TIME'
          ) {
            dateFld.push(d)
          }
        })
        this.dateFields = dateFld
      }
    },
  },
  mounted() {
    for (let i = 0; i <= 23; i++) {
      let time = (i < 10 ? '0' + i : i) + ':'
      this.timesOption.push(time + '00')
      this.timesOption.push(time + '30')
    }
    this.fieldMatchers.push({
      actionType: 17,
      fieldName: 'severity',
      templateJson: {
        resultWorkflowContext: {
          expressions: [
            {
              name: 'severity',
              constant: null,
            },
          ],
        },
        metaJson: {
          fields: [],
        },
      },
    })
    if (!this.isNew) {
      this.rule = this.ruleActions
      this.data.rule.actions = this.rule.actions ? this.rule.actions : []
      this.rule.actions = this.ruleActions ? this.ruleActions.actions : []
      this.data = {
        rule: {
          name: this.rule.name,
          siteId: this.rule.siteId,
          description: this.rule.description,
          ruleType: this.rule.ruleType,
          interval: this.rule.interval,
          event: {
            activityType: parseInt(this.rule.event.activityType),
            moduleName: this.rule.event.moduleName,
          },
          id: this.rule.id,
          scheduleType: this.rule.scheduleType,
          dateFieldId: this.rule.dateFieldId,
          time: this.rule.time,
          criteria: this.rule.criteria,
        },
        actions: this.rule.actions ? this.rule.actions : [],
      }
      if (this.rule.fields) {
        this.selectedFieldObject = []
        this.rule.fields.forEach(d => {
          this.selectedFieldObject.push(parseInt(d.fieldId))
        })
      }
      if (this.rule.interval) {
        this.dateObject = this.$helpers.secTodaysHoursMinu(this.rule.interval)
      }
      if (this.actionType === 'severityAction') {
        if (this.rule.actions) {
          let severitytemplate = this.rule.actions[0].template.originalTemplate
            .workflowContext.expressions[0]
          this.fieldMatchers[0].templateJson.resultWorkflowContext.expressions[0].constant =
            severitytemplate.constant
        }
      }
      this.$forceUpdate()
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibilityshow', false)
    },
    addAction(action) {
      this.data.rule.actions = action
    },
    somefnt(newVal) {
      this.data.rule.criteria = newVal
    },
    async save() {
      let self = this

      let actions = this.data.actions
      let woActionIdx = actions.findIndex(action => action.actionType === 11)
      let closeWoIdx = actions.findIndex(action => action.actionType === 12)

      if (parseInt(this.data.rule.event.activityType) === 2048) {
        if (woActionIdx !== -1) {
          actions.splice(woActionIdx, 1)
        }
        if (closeWoIdx !== -1) {
          delete actions[closeWoIdx].id
        }
      } else {
        if (woActionIdx !== -1) {
          this.handleWoAction(actions[woActionIdx])
        }
        if (closeWoIdx !== -1) {
          actions.splice(closeWoIdx, 1)
        }
      }
      Object.keys(self.data.rule.criteria.conditions).forEach(function(kessy) {
        if (
          !self.data.rule.criteria.conditions[kessy].hasOwnProperty('fieldName')
        ) {
          self.data.rule.criteria = null
        } else {
          if (self.data.rule.criteria) {
            if (self.data.rule.criteria.conditions[kessy].fieldName) {
              delete self.data.rule.criteria.conditions[kessy].valueArray
              delete self.data.rule.criteria.conditions[kessy].operatorsDataType
              delete self.data.rule.criteria.conditions[kessy].operatorLabel
            } else {
              self.data.rule.criteria = null
            }
          }
        }
      })
      if (self.selectedFieldObject.length > 0) {
        self.data.rule.fields = []
        self.selectedFieldObject.forEach(d => {
          self.data.rule.fields.push({
            fieldId: d,
          })
        })
      } else {
        delete self.data.rule.fields
      }
      if (parseInt(this.data.rule.event.activityType) === 524288) {
        this.data.rule.interval = null
        if (this.dateObject) {
          if (this.data.rule.dateFieldId) {
            if (!this.checkDateTimeField) {
              this.dateObject.hours = 0
              this.dateObject.minute = 0
            }
          }
          this.data.rule.interval = this.$helpers.daysHoursMinuToSec(
            this.dateObject
          )
        }
      }
      if (!this.data.rule.dateFieldId) {
        delete this.data.rule.dateFieldId
      }
      if (this.data.rule.dateFieldId && this.checkDateTimeField) {
        this.data.rule.time = null
      }
      let data = this.$helpers.cloneObject(this.data)
      data.rule.actions = actions
      if (!this.validateForm(data)) {
        return false
      }
      this.saving = true
      if (self.actionType === 'severityAction') {
        data.actions = []
        data.actions[0] = Object.assign(this.fieldMatchers[0])
        data.rule.actions[0] = Object.assign(this.fieldMatchers[0])
      }
      if (!self.isSummary) {
        self.$emit('actionSaved', { ...data.rule, type: this.actionType })
        self.closeDialog()
      } else {
        let apiaction
        if (this.isNew) {
          apiaction = 'addcustom'
        } else {
          apiaction = 'updatecustom'
          if (this.actionType !== 'severityAction') {
            let newAction = this.$refs.eventDialog.updateAction(data.actions)
            data.actions = Object.assign(newAction)
          }
        }
        data.rule.ruleId = this.parentRuleId
        data.rule.ruleType = 41
        let { details } = this
        let { module } = details || {}
        if (module === 'newreadingrule') {
          let workflowRule = data.rule
          let url = 'v3/readingrule/actions/add'
          let params = { workflowRule, moduleName: 'newreadingalarm' }
          let { error, data: actionData } = await API.post(url, params)

          if (error) {
            this.$message.error('Error Occurred')
          } else {
            this.saving = false
            this.$emit('actionSaved', actionData)
            this.closeDialog()
          }
        } else {
          this.$util
            .addOrUpdateRuleAction(
              'newreadingalarm',
              data,
              !this.isNew,
              apiaction
            )
            .then(() => {
              this.saving = false
              self.$emit('actionSaved', data.rule)
              this.closeDialog()
            })
            .catch(function() {
              self.saving = false
            })
        }
      }
    },
    handleWoAction(action) {
      if (action.actionType === 11) {
        action.templateJson = action.template.originalTemplate
        delete action.template
        delete action.id
        delete action.templateId
      }
    },
    validateForm(ruleData) {
      this.error = false
      if (!ruleData.rule.name) {
        this.error = true
        this.errorMessage = 'Enter Name '
        return false
      } else if (ruleData.rule.event.activityType === 524288) {
        if (!ruleData.rule.dateFieldId) {
          this.error = true
          this.errorMessage = 'Please select field for on date activity'
          return false
        }
        if (ruleData.rule.scheduleType === 3) {
          if (!ruleData.rule.interval) {
            this.error = true
            this.errorMessage = 'Please select duration'
            return false
          }
        }
      } else if (ruleData.rule.event.activityType === 1048576) {
        if (!ruleData.rule.fields || !ruleData.rule.fields.length > 0) {
          this.error = true
          this.errorMessage =
            'Please select atleast on field for field change activity'
          return false
        }
      }
      return true
    },
  },
}
</script>
<style>
#new-custom-rule .setting-list-view-table tbody tr {
  border-bottom: none !important;
}

.execute-block {
  height: 70px;
}

.execute-block .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  color: #39b2c2;
  background-color: #f1fdff !important;
  border-color: #39b2c2 !important;
  box-shadow: -1px 0 0 0 #39b2c2 !important;
  letter-spacing: 0.5px;
  font-size: 14px !important;
}

.execute-block .el-radio-button__inner {
  border-color: #e2e8ee !important;
}

.execute-block .el-radio-button__inner:hover {
  color: #39b2c2;
}

.execute-block .el-radio-button__inner {
  color: #333333;
  font-size: 14px !important;
  letter-spacing: 0.5px !important;
}

.execute-block .el-radio-button {
  padding-bottom: 10px;
}

.creteawo-Dialog .el-dialog__header {
  display: none !important;
}

.creteawo-Dialog .el-dialog__body {
  padding: 40px;
  height: 450px !important;
}

.ruleDialog .el-dialog {
  position: relative !important;
  margin: 0 auto 50px;
  background: #fff;
  border-radius: 2px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  box-sizing: border-box;
  height: auto;
  padding-bottom: 50px;
  margin-top: 15vh !important;
}

.ruleDialog .mail-message-textarea {
  overflow-x: hidden;
  overflow-y: scroll;
  position: relative;
  padding-bottom: 40px;
  white-space: pre-line;
}

.ruleDialog .mail-message-textarea {
  overflow-y: scroll;
  padding-bottom: 40px;
}

.ruleDialog .subject .el-textarea {
  min-height: 200px;
  border: 0px solid;
  overflow-y: scroll;
  height: 240px;
}

.ruleDialog .setup-dialog-lay {
  overflow-x: hidden;
  overflow-y: scroll;
}

.reset-txt {
  font-size: 12px;
  letter-spacing: 0.5px;
  color: #30a0af;
}

.new-body-modal .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  border-left: 1px solid transparent !important;
}

#new-custom-rule .el-input.is-disabled .el-input__inner {
  background-color: transparent;
}

@media screen and (max-width: 1280px) and (min-width: 800px) {
  .custom-rule-dialog {
    width: 55% !important;
  }
}
</style>
