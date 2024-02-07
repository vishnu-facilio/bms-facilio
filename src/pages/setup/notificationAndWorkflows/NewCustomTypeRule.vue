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
                <div class="setup-modal-title">{{ title }}</div>
              </div>
            </div>
          </div>
          <div class="new-body-modal" v-if="loading">
            <el-row>
              <el-col :span="12" class="pR10">
                <span class="line1 loading-shimmer width50"></span>
                <span class="line2 loading-shimmer width100"></span>
              </el-col>
              <el-col :span="12" class="pL10">
                <span class="line1 loading-shimmer width50"></span>
                <span class="line2 loading-shimmer width100"></span>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <span class="line3 loading-shimmer width50"></span>
                <span class="line4 loading-shimmer width100"></span>
              </el-col>
            </el-row>
          </div>
          <div class="new-body-modal" v-else>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item prop="name">
                  <p class="fc-input-label-txt">
                    {{ $t('common.roles.name') }}
                  </p>
                  <el-input
                    :autofocus="true"
                    v-model="data.rule.name"
                    class="fc-input-full-border-select2"
                    :placeholder="$t('setup.setupLabel.action_name')"
                  >
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="siteId">
                  <p class="fc-input-label-txt">
                    {{ $t('space.sites._site') }}
                  </p>
                  <Lookup
                    v-model="data.rule.siteId"
                    :field="fields.site"
                    :hideLookupIcon="true"
                    @recordSelected="setSelectedValue"
                    @showLookupWizard="showLookupWizardSite"
                  ></Lookup>
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
                  >
                    <el-option
                      v-for="(type, index) in activityTypes"
                      :key="index"
                      :label="type.label"
                      :value="type.value"
                    >
                    </el-option>
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
                    <p class="fc-input-label-txt">
                      {{ $t('setup.setupLabel.dateField') }}
                    </p>
                    <el-select
                      v-model="data.rule.dateFieldId"
                      placeholder="Select"
                      class="width300px"
                    >
                      <el-option
                        v-for="(fld, index) in filteredDateFields"
                        :key="index"
                        :label="fld.displayName"
                        :value="fld.id"
                      >
                      </el-option>
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
                        v-if="!beforeFieldIds.includes(data.rule.dateFieldId)"
                        >Before</el-radio
                      >
                      <el-radio class="fc-radio-btn" :label="2" :key="2"
                        >On
                      </el-radio>
                      <el-radio class="fc-radio-btn" :label="3" :key="3"
                        >After
                      </el-radio>
                    </el-radio-group>
                  </el-col>
                </el-row>
                <el-row class="mT30">
                  <template
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
                          v-for="index in 60"
                          :label="index"
                          :key="`days-${index}`"
                          :value="index"
                        ></el-option>
                      </el-select>
                    </el-col>
                    <template v-if="checkDateTimeField">
                      <el-col :span="8">
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
                          ></el-option>
                        </el-select>
                      </el-col>
                      <el-col :span="8">
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
                          ></el-option>
                        </el-select>
                      </el-col>
                    </template>
                  </template>
                  <el-col :span="8" v-if="!checkDateTimeField">
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
                        collapse-tags
                        placeholder="Select"
                        class="fc-input-full-border-select2 width50"
                      >
                        <el-option
                          v-for="(fld, index) in moduleFields"
                          :key="index"
                          :label="fld.displayName"
                          :value="fld.id"
                        >
                        </el-option>
                      </el-select>
                      <el-button
                        v-if="selectedFieldObject.length < 3"
                        slot="reference"
                        class="all-rule-btn"
                        ><img src="~assets/add-icon.svg" class="mL10"
                      /></el-button>
                    </el-form-item>
                  </div>
                  <!-- Field block End-->
                </el-row>
              </div>
            </div>
            <div class="form-input">
              <div class="wo-save-subH">
                {{ $t('common._common.criteria') }}
              </div>
              <div class="fc-sub-title-desc">
                {{ $t('common._common.specify_rule_custom', { module }) }}
              </div>
              <CriteriaBuilder
                v-model="data.rule.criteria"
                :moduleName="module"
                :isOneLevelEnabled="true"
              />
            </div>
            <event-dialog-helper
              @actions="addAction"
              ref="eventDialog"
              :ruleType="ruleType"
              :rule="data"
              :module="module"
              :moduleFields="moduleFields"
              :moduleId="moduleId"
              :actions="ruleActions"
            ></event-dialog-helper>
          </div>
          <div class="modal-dialog-footer">
            <el-button @click="closeDialog()" class="modal-btn-cancel">{{
              $t('setup.users_management.cancel')
            }}</el-button>
            <el-button
              type="primary"
              class="modal-btn-save"
              @click="save"
              :loading="saving"
              >{{
                saving
                  ? $t('maintenance._workorder.saving')
                  : $t('maintenance._workorder.save')
              }}</el-button
            >
          </div>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import EventDialogHelper from '@/EventDialogHelper'
import ErrorBanner from '@/ErrorBanner'
import NotificationHelper from 'pages/setup/actions/NotificationHelper'
import { isEmpty } from '@facilio/utils/validation'
import { isDateTimeField } from '@facilio/utils/field'
import { Lookup } from '@facilio/ui/forms'
import { CriteriaBuilder } from '@facilio/criteria'
import { mapState } from 'vuex'
import { API } from '@facilio/api'
const fields = {
  site: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'site',
    field: {
      lookupModule: {
        name: 'site',
        displayName: 'Sites',
      },
    },

    multiple: false,
    additionalParams: {
      orderBy: 'spaceType',
      orderType: 'asc',
    },
  },
}

export default {
  props: [
    'isNew',
    'visibilityshow',
    'mailWidth',
    'module',
    'moduleDisplayName',
    'activityTypes',
    'ruleType',
    'ruleActions',
    'currentAction',
  ],
  mixins: [NotificationHelper],
  data() {
    return {
      fields,
      timesOption: [],
      error: false,
      isUpdate: false,
      errorMessage: '',
      selectedFieldObject: [],
      moduleMeta: null,
      saving: false,
      data: {
        rule: {
          name: '',
          scheduleType: 3,
          description: '',
          ruleType: null,
          event: {
            moduleName: 'alarm',
            activityType: 1,
          },
          dateFieldId: null,
          time: null,
          fields: [],
          criteria: null,
        },
        siteId: null,
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
      dateFieldToExclude: [
        'sysCreatedTime',
        'sysModifiedTime',
        'sysDeletedTime',
      ],
      beforeDateFieldsToExclude: ['createdTime', 'modifiedTime'],
      dateFieldName: ['DATE', 'DATE_TIME'],
      loading: false,
    }
  },
  components: {
    Lookup,
    ErrorBanner,
    EventDialogHelper,
    CriteriaBuilder,
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
    moduleFields() {
      return this.$getProperty(this.metaInfo, 'fields', null)
    },
    moduleId() {
      return this.$getProperty(this.metaInfo, 'module.moduleId', null)
    },
    checkDateTimeField() {
      let { dateFields, data } = this
      let { rule } = data || {}
      let { dateFieldId } = rule || {}
      let field =
        (dateFields || []).find(fld => fld.fieldId === dateFieldId) || {}

      return !isEmpty(field) ? isDateTimeField(field) : false
    },

    title() {
      let { isNew, module, currentAction, moduleDisplayName } = this
      let title = isNew ? `New` : `Edit`
      moduleDisplayName =
        module === 'newreadingalarm' ? 'Alarm' : moduleDisplayName
      currentAction = isEmpty(currentAction) ? 'Action' : currentAction
      title = `${title} ${moduleDisplayName} ${currentAction}`
      return title
    },
    filteredDateFields() {
      let fields = [...this.dateFieldToExclude]
      if (this.data.rule.scheduleType === 1) {
        fields = [...fields, ...this.beforeDateFieldsToExclude]
      }
      return (this.dateFields || []).filter(
        field => !fields.includes(field.name)
      )
    },
    dateFields() {
      let { moduleFields, dateFieldName } = this
      return (moduleFields || []).filter(d =>
        (dateFieldName || []).includes(d.dataTypeEnum._name)
      )
    },
    beforeFieldIds() {
      return (this.dateFields || [])
        .filter(field =>
          (this.beforeDateFieldsToExclude || []).includes(field.name)
        )
        .map(field => field.id)
    },
  },
  mounted() {
    for (let i = 0; i <= 23; i++) {
      let time = (i < 10 ? '0' + i : i) + ':'
      this.timesOption.push(time + '00')
      this.timesOption.push(time + '30')
    }
  },
  async created() {
    let selectedRuleId = this.$attrs.selectedRuleId || {}
    if (selectedRuleId && !this.isNew) {
      this.loading = true
      try {
        let params = { ruleId: selectedRuleId }
        let { data, error } = await API.get('v2/modules/rules/getRule', params)
        if (error) {
          this.$message.error(error.message || 'Something went wrong')
        } else {
          let siteId =
            this.$getProperty(data, 'workflowRule.siteId') > -1
              ? this.$getProperty(data, 'workflowRule.siteId')
              : ''
          let actions = this.$getProperty(data, 'workflowRule.actions')
          let rule = this.$getProperty(data, 'workflowRule')
          let fields = this.$getProperty(data, 'workflowRule.fields')
          let dateObj = this.$getProperty(data, 'workflowRule.interval')

          rule.dateFieldId = this.dateFieldsCheck(rule, dateObj)
          this.data = {
            rule: {
              ...rule,
            },
            siteId,
            actions,
          }
          this.$setProperty(this.data, 'rule.siteId', siteId)

          if (fields) {
            this.selectedFieldObject = []
            fields.map(d => {
              this.selectedFieldObject.push(parseInt(d.fieldId))
            })
          }
        }
      } catch {
        this.data.rule = {}
      } finally {
        this.loading = false
      }
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibilityshow', false)
    },
    showLookupWizardSite(field, canShow) {
      canShow = false
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizardSite', canShow)
    },
    setSelectedValue(selectedValues, field) {
      selectedValues
      field
    },
    addAction(action) {
      this.data.rule.actions = action
    },
    save() {
      let self = this
      let actions = this.data.actions

      if (parseInt(this.data.rule.event.activityType) === 2048) {
        let woActionIdx = actions.findIndex(action => action.actionType === 11)
        if (woActionIdx !== -1) {
          actions.splice(woActionIdx, 1)
        }
      } else if (actions) {
        let idx = actions.findIndex(action => action.actionType === 12)
        if (idx !== -1) {
          actions.splice(idx, 1)
        }
      }
      if (self.data.rule.criteria) {
        Object.keys(self.data.rule.criteria.conditions).forEach(function(
          kessy
        ) {
          if (
            !self.data.rule.criteria.conditions[kessy].hasOwnProperty(
              'fieldName'
            )
          ) {
            self.data.rule.criteria = null
          } else {
            if (self.data.rule.criteria) {
              if (self.data.rule.criteria.conditions[kessy].fieldName) {
                delete self.data.rule.criteria.conditions[kessy].valueArray
                delete self.data.rule.criteria.conditions[kessy]
                  .operatorsDataType
                delete self.data.rule.criteria.conditions[kessy].operatorLabel
              } else {
                self.data.rule.criteria = null
              }
            }
          }
        })
      } else {
        self.data.rule.criteriaId = -99
      }
      if (self.selectedFieldObject.length > 0) {
        self.data.rule.fields = []
        self.selectedFieldObject.forEach(d => {
          self.data.rule.fields.push({ fieldId: d })
        })
      } else {
        delete self.data.rule.fields
      }
      this.data.rule.interval = null
      if (parseInt(this.data.rule.event.activityType) === 524288) {
        if (this.dateObject) {
          if (this.data.rule.dateFieldId) {
            if (!this.checkDateTimeField) {
              this.dateObject.hours = 0
              this.dateObject.minute = 0
            }
          }
          if (
            this.data.rule.scheduleType === 1 ||
            this.data.rule.scheduleType === 3
          ) {
            this.data.rule.interval = this.$helpers.daysHoursMinuToSec(
              this.dateObject
            )
          } else {
            this.data.rule.interval = null
          }
        }
      }

      if (!this.data.rule.dateFieldId) {
        delete this.data.rule.dateFieldId
        this.data.rule.time = null
      }
      if (this.data.rule.dateFieldId && this.checkDateTimeField) {
        this.data.rule.time = null
      }
      let data = this.$helpers.cloneObject(this.data)
      data.actions = actions
      if (!this.validateForm(data)) {
        return false
      }
      let apiaction
      if (self.isNew) {
        data.rule.ruleType = this.ruleType
        data.rule.event.moduleName = this.module
        apiaction = 'addcustom'
      } else {
        let newAction = this.$refs.eventDialog.updateAction(data.actions)
        if (newAction) {
          this.data.actions = []
          data.actions = newAction
        }
        if (isEmpty(data.rule.siteId)) {
          data.rule.siteId = -99
        } else if (!data.rule.siteId) {
          delete data.rule.siteId
        }
        apiaction = 'updatecustom'
      }
      this.saving = true
      this.$util
        .addOrUpdateRule(this.module, data, !this.isNew, apiaction)
        .then(rule => {
          self.saving = false
          self.$emit('actionSaved', rule)
          self.closeDialog()
        })
        .catch(function(error) {
          self.saving = false
          console.log(error)
        })
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
      if (!ruleData.actions || !ruleData.actions.length > 0) {
        this.error = true
        this.errorMessage = 'Please select atleast one a action'
        return false
      }
      return true
    },
    dateFieldsCheck(rule, dateObj) {
      if (rule.event.activityType === 524288 && !isEmpty(rule.dateFieldId)) {
        if (dateObj && [1, 3].includes(parseInt(rule.scheduleType))) {
          let dateObject = this.$helpers.secTodaysHoursMinu(dateObj)
          this.dateObject = {
            ...dateObject,
          }
        }
        return rule.dateFieldId
      }
      this.dateObject = {
        days: null,
        minute: 0,
        hours: 0,
        minu: null,
      }
      return null
    },
  },
}
</script>
<style>
.line1 {
  height: 15px;
  border-radius: 5px;
  margin: 8px 0px 10px;
}
.line2 {
  height: 40px;
  border-radius: 5px;
  margin-bottom: 24px;
}
.line3 {
  height: 15px;
  border-radius: 5px;
  margin-bottom: 13px;
}
.line4 {
  height: 80px;
  border-radius: 5px;
}
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
  height: 70vh;
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
  padding-bottom: 100px;
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
