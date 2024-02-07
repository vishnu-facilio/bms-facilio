<template>
  <div>
    <el-dialog
      :visible="visibility"
      :fullscreen="true"
      :append-to-body="true"
      :before-close="close"
      custom-class="fc-dialog-form fc-dialog-right custom-rule-dialog setup-dialog40 setup-dialog"
      style="z-index: 1999"
    >
      <el-form
        ref="new-sla-escalation"
        :model="levelObj"
        :rules="rules"
        :label-position="'top'"
      >
        <div class="new-header-container mR30 pL30">
          <div class="new-header-modal">
            <div class="new-header-text">
              <div class="setup-modal-title">
                Level {{ position + 1 }} Escalation
              </div>
            </div>
          </div>
        </div>
        <div class="new-body-modal new-escalation-level">
          <el-row class="mB10">
            <el-col :span="20">
              <label class="newescaltion-level-title">
                {{
                  $t('setup.setupLabel.new_edit_escalations_point', {
                    entityName,
                  })
                }}
                <span v-if="supportedTypes.length === 1">{{
                  $t('setup.setupLabel.after')
                }}</span>
              </label>
            </el-col>
          </el-row>
          <el-row class="mB20" :gutter="10">
            <el-col :span="10" v-if="supportedTypes.length > 1">
              <el-form-item prop="type">
                <el-select
                  v-model="levelObj.type"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="item in supportedTypes"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>

            <el-col v-if="levelObj.type !== 2" :span="8">
              <DurationField
                class="mL-auto width300px"
                v-model="levelObj.interval"
                @change="duration => (levelObj.interval = duration)"
              ></DurationField>
            </el-col>
          </el-row>
          <el-row class="mB10">
            <el-col :span="12">
              <label class="newescaltion-level-title">{{
                $t('rule.create.actions')
              }}</label>
              <div class="fc-sub-title-desc">
                {{ $t('setup.setupLabel.escalations_point_actions') }}
              </div>
            </el-col>
            <el-col :span="12">
              <el-dropdown
                class="fR pointer"
                @command="addAction"
                hide-on-click
                trigger="click"
              >
                <div style="color: #3ab2c1" class="f13 mT10">
                  <inline-svg
                    src="svgs/plus-button"
                    style="height:18px;width:18px;margin-right: 4px;"
                    class="pointer vertical-middle fill-greeny-blue"
                  />
                  Add Action
                </div>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item
                    v-for="(action, index) in supportedActions"
                    :key="`actiontype-${index}`"
                    :command="actionsHash[action].actionParam"
                  >
                    {{ actionsHash[action] && actionsHash[action].label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </el-col>
          </el-row>

          <el-row v-if="isEmailConfi" class="action-row">
            <el-col :span="12">
              <p class="details-Heading">Send Email</p>
            </el-col>
            <el-col :span="12" class="pT4 text-right">
              <span class="configured-green">Configured</span>
              <span v-if="isEmailConfi" class="mL20 action-edit-section">
                <i
                  class="el-icon-edit pointer"
                  @click="editAction(emailContext)"
                  title="Edit Mail"
                  v-tippy
                ></i>
                <span class="mL20 reset-txt pointer" @click="reset('mail')">
                  {{ $t('setup.setupLabel.reset') }}
                </span>
              </span>
            </el-col>
          </el-row>
          <el-row class="action-row" v-if="isSmsConfi">
            <el-col :span="12">
              <p class="details-Heading">Send SMS</p>
            </el-col>
            <el-col :span="12" class="pT4 text-right">
              <span class="configured-green">Configured</span>
              <span v-if="isSmsConfi" class="mL20 action-edit-section">
                <i
                  class="el-icon-edit pointer"
                  @click="editAction(smsContent)"
                  title="Edit Sms"
                  v-tippy
                ></i>
                <span class="mL20 reset-txt pointer" @click="reset('sms')">{{
                  $t('setup.setupLabel.reset')
                }}</span>
              </span>
            </el-col>
          </el-row>
          <el-row class="action-row" v-if="isMobileConfig">
            <el-col :span="12">
              <p class="details-Heading">Send Mobile Notification</p>
            </el-col>
            <el-col :span="12" class="pT4 text-right">
              <span class="configured-green">Configured</span>
              <span v-if="isMobileConfig" class="mL20 action-edit-section">
                <i
                  class="el-icon-edit pointer"
                  @click="editAction(mobileContent)"
                  title="Edit Mobile"
                  v-tippy
                ></i>
                <span class="mL20 reset-txt pointer" @click="reset('mobile')">{{
                  $t('setup.setupLabel.reset')
                }}</span>
              </span>
            </el-col>
          </el-row>
          <el-row class="action-row" v-if="isFieldConfi">
            <el-col :span="12">
              <p class="details-Heading">Field Update</p>
            </el-col>
            <el-col :span="12" class="pT4 text-right">
              <span class="configured-green">Configured</span>
              <span v-if="isFieldConfi" class="mL20 action-edit-section">
                <i
                  class="el-icon-edit pointer"
                  @click="editAction(fieldContext)"
                  title="Edit Field"
                  v-tippy
                ></i>
                <span
                  class="mL20 reset-txt pointer"
                  @click="reset('fieldChange')"
                  >{{ $t('setup.setupLabel.reset') }}</span
                >
              </span>
            </el-col>
          </el-row>
          <el-row class="action-row" v-if="isScript">
            <el-col :span="12">
              <p class="details-Heading">
                {{ $t('common.profile.execute_script') }}
              </p>
            </el-col>
            <el-col :span="12" class="pT4 text-right">
              <span class="configured-green">{{
                $t('common._common.configured')
              }}</span>
              <span v-if="isScript" class="mL20 action-edit-section">
                <i
                  class="el-icon-edit pointer"
                  @click="editAction(scriptContent)"
                  :title="$t('agent.edit.edit_script')"
                  v-tippy
                ></i>
                <span class="mL20 reset-txt pointer" @click="reset('script')">{{
                  $t('setup.setupLabel.reset')
                }}</span>
              </span>
            </el-col>
          </el-row>
          <el-row class="action-row" v-if="isStatus">
            <el-col :span="12">
              <p class="details-Heading">
                {{ $t('common.profile.change_status') }}
              </p>
            </el-col>
            <el-col :span="12" class="pT4 text-right">
              <span class="configured-green">{{
                $t('common._common.configured')
              }}</span>
              <span v-if="isStatus" class="mL20 action-edit-section">
                <i
                  class="el-icon-edit pointer"
                  @click="editAction(statusContent)"
                  :title="$t('agent.edit.edit_status')"
                  v-tippy
                ></i>
                <span
                  class="mL20 reset-txt pointer"
                  @click="reset('changeStatus')"
                  >{{ $t('setup.setupLabel.reset') }}</span
                >
              </span>
            </el-col>
          </el-row>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="close" class="modal-btn-cancel text-uppercase">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="save"
            :loading="isSaving"
            >{{
              isSaving
                ? $t('common._common._saving')
                : $t('common._common._save')
            }}</el-button
          >
        </div>
      </el-form>
    </el-dialog>
    <ScriptAction
      v-if="showScriptDialog"
      :existingAction="scriptContent"
      :module="module"
      @onSave="actionSave"
      @onClose="showScriptDialog = false"
    ></ScriptAction>
    <ChangeStatusDialog
      v-if="showChangeStatusDialog"
      :actionObj="statusContent"
      :moduleId="moduleId"
      :moduleName="module"
      @onSave="actionSave"
      @onClose="showChangeStatusDialog = false"
    ></ChangeStatusDialog>
    <send-notification
      ref="notificationpage"
      class="ruleDialog"
      @onsave="actionSave"
      :visibility.sync="actionEdit"
      :mode.sync="actionMode"
      :option.sync="actionData"
      :module="module"
    ></send-notification>
    <field-update-dialog
      v-if="fieldUpdateValue"
      :statusFieldName="statusFieldName"
      :fieldChange="fieldChange"
      :moduleFields="moduleFields"
      :module="module"
      :picklistOptions="picklistOptions"
      :addRow="addRow"
      :actionSave="actionSave"
      :fieldUpdateValue.sync="fieldUpdateValue"
    ></field-update-dialog>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import clone from 'lodash/clone'
import { mapState } from 'vuex'
import DurationField from './SLADurationField'
import FieldUpdateDialog from '@/fields/FieldUpdateDialog'
import SendNotification from 'pages/setup/actions/SendNotification'
import { getFieldOptions } from 'util/picklist'
import ChangeStatusDialog from 'src/newapp/setupActions/components/StatusUpdate.vue'
import ScriptAction from './ScriptAction.vue'

const actionsHash = {
  email: {
    label: 'Send Email',
    actionParam: 'mail',
  },
  sms: {
    label: 'Send SMS',
    actionParam: 'sms',
  },
  mobile: {
    label: 'Send Mobile Notification',
    actionParam: 'mobile',
  },
  fieldUpdate: {
    label: 'Field Update',
    actionParam: 'fieldChange',
  },
  script: {
    label: 'Exceute Script',
    actionParam: 'script',
  },
  changeStatus: {
    label: 'Change Status',
    actionParam: 'changeStatus',
  },
}

export default {
  name: 'NewEscalationLevel',
  props: [
    'visibility',
    'onClose',
    'onSave',
    'position',
    'level',
    'entityId',
    'entityName',
  ],
  components: {
    DurationField,
    SendNotification,
    FieldUpdateDialog,
    ChangeStatusDialog,
    ScriptAction,
  },
  data() {
    return {
      levelObj: {
        type: 1,
        interval: 0,
        actions: [],
      },
      scriptContent: '',
      statusContent: {
        actionType: 19,
        templateJson: {
          new_state: null,
        },
      },
      rules: {},
      showScriptDialog: false,
      showChangeStatusDialog: false,
      actionsHash: actionsHash,
      isSaving: false,
      isEmailConfi: false,
      isSmsConfi: false,
      isMobileConfig: false,
      isFieldConfi: false,
      isScript: false,
      isStatus: false,

      mobileContent: '',
      emailContext: '',
      fieldContext: '',
      moduleId: null,
      smsContent: '',
      actionMode: 'mail',
      actionEdit: false,
      actionIndex: null,
      actionData: null,
      fieldUpdateValue: false,
      fieldChange: {
        actionType: 13,
        templateJson: {
          fieldMatcher: [],
        },
      },
      assets: null,
      categoryMetric: null,
      picklistOptions: {},
      moduleFields: [],
    }
  },
  created() {
    Promise.all([
      this.$store.dispatch('loadTicketCategory'),
      this.$store.dispatch('loadTicketPriority'),
      this.$store.dispatch('loadAssetCategory'),
      this.$store.dispatch('loadGroups'),
      this.$store.dispatch('loadTicketStatus', this.module),
      this.getModuleFields(),
    ]).then(() => this.initData(this.level))
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketcategory: state => state.ticketCategory,
      assetCategory: state => state.assetCategory,
      ticketpriority: state => state.ticketPriority,
    }),
    module() {
      return this.$route.params.moduleName
    },
    types() {
      return [
        {
          id: 1,
          name: 'Before',
        },
        {
          id: 2,
          name: `On ${this.entityName}`,
        },
        {
          id: 3,
          name: 'After',
        },
      ]
    },
    supportedTypes() {
      return this.types.filter(type => {
        let isFirstLevel = this.position === 0
        let isBefore = type.id === 1
        let isOnDue = type.id === 2
        return isFirstLevel ? !isBefore : !(isBefore || isOnDue)
      })
    },
    supportedActions() {
      const actions = [
        'email',
        'mobile',
        'fieldUpdate',
        'script',
        'changeStatus',
      ]
      if (this.$helpers.isLicenseEnabled('SMS')) actions.push('sms')
      return actions
    },
  },
  methods: {
    initData(level) {
      let { type, interval } = level || {}
      let actions = this.$getProperty(level, 'actions', []) || []

      ;[
        'mail',
        'sms',
        'mobile',
        'fieldChange',
        'script',
        'changeStatus',
      ].forEach(this.reset)

      if (!isEmpty(actions)) {
        actions.forEach(action => {
          if (parseInt(action.actionType) === 3) {
            this.isEmailConfi = true
            this.emailContext = action
          } else if (parseInt(action.actionType) === 4) {
            this.isSmsConfi = true
            this.smsContent = action
          } else if (parseInt(action.actionType) === 7) {
            this.isMobileConfig = true
            this.mobileContent = action
          } else if (parseInt(action.actionType) === 13) {
            this.isFieldConfi = true

            let index = 0
            if (!isEmpty(action.template)) {
              let originalTemplate = action.template.originalTemplate

              for (let key in originalTemplate) {
                this.addRow()
                if (originalTemplate.hasOwnProperty(key)) {
                  let fieldMatcher = this.fieldChange.templateJson.fieldMatcher[
                    index
                  ]
                  let field = this.moduleFields.filter(
                    field => field.name === key
                  )

                  !isEmpty(field) &&
                    this.setFieldMatcher(
                      fieldMatcher,
                      field,
                      key,
                      originalTemplate
                    )

                  this.statusFieldName(fieldMatcher, index)

                  index++
                }
              }

              this.fieldContext = action
            }
          } else if (parseInt(action.actionType) === 21) {
            this.isScript = true
            this.scriptContent = action
          } else if (parseInt(action.actionType) === 19) {
            this.isStatus = true
            this.statusContent = action
          }
        })
      }

      this.levelObj = {
        type: type || 3,
        interval: interval || 0,
        actions: isEmpty(actions) ? [] : actions,
      }
    },
    serialize() {
      let { type, interval, actions = [] } = clone(this.levelObj)
      if (type === 2) interval = 0

      let newActions = actions
        .filter(action => !isEmpty(action.templateJson))
        .map(action => ({
          actionType: action.actionType,
          templateJson: action.templateJson,
        }))

      let savedActions = actions.filter(action => isEmpty(action.templateJson))
      let serializedActions = this.updateAction(savedActions || [])

      return {
        type,
        interval,
        actions: [...newActions, ...serializedActions],
      }
    },
    save() {
      this.onSave(this.serialize())
    },
    close() {
      this.onClose()
    },
    addAction(cmd) {
      this.actionMode = cmd
      this.actionData = null

      if (
        cmd !== 'workorder' &&
        cmd !== 'fieldChange' &&
        cmd !== 'controls' &&
        cmd !== 'script' &&
        cmd != 'changeStatus'
      ) {
        this.actionEdit = true
      } else if (cmd === 'fieldChange') {
        this.fieldUpdateValue = true
        if (
          this.$getProperty(
            this.fieldChange,
            'templateJson.fieldMatcher.length',
            0
          ) <= 0
        )
          this.addRow()
      } else if (cmd === 'script') {
        this.showScriptDialog = true
      } else if (cmd === 'changeStatus') {
        this.showChangeStatusDialog = true
      }
    },

    editAction(action) {
      this.actionData = action

      if (action.actionType === 3) {
        this.actionMode = 'mail'
        this.isEmailConfi = true
        this.actionEdit = true
      } else if (action.actionType === 4) {
        this.actionMode = 'sms'
        this.isSmsConfi = true
        this.actionEdit = true
      } else if (action.actionType === 7) {
        this.actionMode = 'mobile'
        this.isMobileConfig = true
        this.actionEdit = true
      } else if (action.actionType === 13) {
        this.isFieldConfi = true
        this.fieldUpdateValue = true
        this.actionMode = 'fieldChange'
      } else if (parseInt(action.actionType) === 21) {
        this.showScriptDialog = true
        this.actionMode = 'script'
      } else if (parseInt(action.actionType) === 19) {
        this.showChangeStatusDialog = true
        this.actionMode = 'changeStatus'
      }
    },
    deleteAction(idx) {
      let actions = this.$getProperty(this, 'levelObj.actions', []) || []
      actions.splice(idx, 1)
      this.$set(this.levelObj, 'actions', actions)
    },
    actionSave(dataObj) {
      let data = this.$helpers.cloneObject(dataObj)
      let actions = this.$getProperty(this, 'levelObj.actions', []) || []

      let index = actions.findIndex(d => d.actionType === data.actionType)
      if (index !== -1) {
        actions.splice(index, 1)
      }

      if (data.actionType === 3) {
        this.isEmailConfi = true
        this.emailContext = data
      } else if (data.actionType === 4) {
        this.isSmsConfi = true
        this.smsContent = data
      } else if (data.actionType === 7) {
        this.isMobileConfig = true
        this.formatMobileData(data.templateJson)
        this.mobileContent = data
      } else if (data.actionType === 13) {
        this.fieldUpdateValue = false
        this.fieldContext = data
        this.isFieldConfi = true
      } else if (data.actionType === 21) {
        this.showScriptDialog = false
        this.scriptContent = data
        this.isScript = true
      } else if (data.actionType === 19) {
        this.showChangeStatusDialog = false
        this.statusContent = data
        this.isStatus = true
      }

      this.addModuleParam(data)
      actions.push(data)
      this.actionEdit = false
    },
    reset(action) {
      let actions = this.$getProperty(this, 'levelObj.actions', []) || []

      if (action === 'sms') {
        let idx = actions.findIndex(d => d.actionType === 4)
        actions.splice(idx, 1)
        this.smsContent = ''
        this.isSmsConfi = false
      } else if (action === 'mail') {
        let idx = actions.findIndex(d => d.actionType === 3)
        actions.splice(idx, 1)
        this.emailContext = ''
        this.isEmailConfi = false
      } else if (action === 'mobile') {
        let idx = actions.findIndex(d => d.actionType === 7)
        actions.splice(idx, 1)
        this.mobileContent = ''
        this.isMobileConfig = false
      } else if (action === 'fieldChange') {
        let idx = actions.findIndex(d => d.actionType === 13)
        actions.splice(idx, 1)
        this.isFieldConfi = false
      } else if (action === 'script') {
        let idx = actions.findIndex(d => d.actionType === 21)
        actions.splice(idx, 1)
        ;(this.scriptContent = ''), (this.isScript = false)
      } else if (action === 'changeStatus') {
        let idx = actions.findIndex(d => d.actionType === 19)
        actions.splice(idx, 1)
        this.statusContent = null
        this.isStatus = false
      }
    },
    formatMobileData(data) {
      let summaryPage = this.module.toUpperCase() + '_SUMMARY'
      let message = {
        content_available: true,
        summary_id: '${' + this.module + '.id}',
        sound: 'default',
        module_name: this.module,
        priority: 'high',
        text: data.message,
        click_action: summaryPage,
        title: data.subject,
      }
      data.body = JSON.stringify({
        name: this.module.toUpperCase() + '_PUSH_NOTIFICATION',
        notification: message,
        data: message,
        id: data.id,
      })
      data.to = data.id
      this.$common.setExpressionFromPlaceHolder(
        data.workflow,
        '${' + this.module + '.id}'
      )
    },
    addModuleParam(template) {
      let workflowParameters = this.$getProperty(
        template,
        'templateJson.workflow.parameters'
      )
      let ftl = this.$getProperty(template, 'templateJson.ftl')
      if (
        ftl &&
        workflowParameters &&
        !workflowParameters.some(param => param.name === this.module)
      ) {
        template.templateJson.workflow.parameters.push({
          name: this.module,
          typeString: 'String',
        })
      }
    },
    updateAction(actions) {
      let newlist = []
      actions
        .filter(action => action.actionType !== 5)
        .forEach(action => {
          let template
          if (action.actionType === 3) {
            template = this.$refs.notificationpage.getDataToSave(
              this.emailContext,
              'mail'
            )
          } else if (action.actionType === 4) {
            template = this.$refs.notificationpage.getDataToSave(
              this.smsContent,
              'sms'
            )
          } else if (action.actionType === 7) {
            let data = this.$refs.notificationpage.getDataToSave(
              this.mobileContent,
              'mobile'
            )
            this.formatMobileData(data.templateJson)
            template = data
          } else if (action.actionType === 13) {
            template = this.fieldChange
          } else if (action.actionType === 21) {
            template = {
              actionType: action.actionType,
              templateJson: action.templateJson,
            }
          } else if (action.actionType === 19) {
            template = action
          }
          this.addModuleParam(template)
          newlist.push(template || action)
        })
      return newlist
    },
    setFieldMatcher(fieldMatcher, field, key, originalTemplate) {
      fieldMatcher.field = key
      fieldMatcher.value = originalTemplate[key]

      if (field[0].dataTypeEnum._name === 'LOOKUP') {
        if (isEmpty(originalTemplate[key].id)) {
          fieldMatcher.valueArray = originalTemplate[key]
          fieldMatcher.value = originalTemplate[key]
        } else {
          fieldMatcher.valueArray = originalTemplate[key].id
          fieldMatcher.value.id = originalTemplate[key].id
        }
      } else if (
        field[0].dataTypeEnum._name === 'DATE_TIME' ||
        field[0].dataTypeEnum._name === 'DATE'
      ) {
        fieldMatcher.dateObject = this.$helpers.secTodaysHoursMinu(
          originalTemplate[key] / 1000
        )
      }
      fieldMatcher.fieldObj = field
    },
    statusFieldName(selectedField, index) {
      let field = this.moduleFields.filter(
        field => field.name === selectedField.field
      )
      let fieldMatcher = this.fieldChange.templateJson.fieldMatcher[index]

      fieldMatcher.fieldObj = field
      fieldMatcher.isSpacePicker = false
      if (!isEmpty(field)) {
        fieldMatcher.columnName = field[0].completeColumnName
        if (field[0].dataTypeEnum._name === 'ENUM') {
          this.$set(this.picklistOptions, field[0].name, field[0].enumMap)
        }
        if (field[0].dataTypeEnum._name === 'LOOKUP' && field[0].specialType) {
          this.loadSpecialTypePickList(field[0].specialType, field[0].name)
        } else if (
          field[0].dataTypeEnum._name === 'LOOKUP' &&
          field[0].lookupModule
        ) {
          if (field[0].lookupModule.name === 'ticketpriority') {
            // handling key value pair to match existing flow pattern
            let priority = {}
            this.ticketpriority.forEach(d => {
              priority[d.id] = d.displayName
            })
            this.$set(this.picklistOptions, field[0].name, priority)
          } else {
            this.loadPickList(field[0].lookupModule.name, field[0].name)
          }
        }
      }
    },
    async loadPickList(moduleName, fieldName) {
      this.picklistOptions = {}
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: moduleName, skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$set(this.picklistOptions, fieldName, options)
      }
    },
    loadSpecialTypePickList(specialType, fieldName) {
      let pickOption = {}
      if (specialType === 'users') {
        let userList = this.$store.state.users
        pickOption['$' + '{LOGGED_USER}'] = 'Current User'
        for (let user of userList) {
          pickOption[user.id] = user.name
        }
        this.$set(this.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'groups') {
        let groupList = this.$store.state.groups
        for (let group of groupList) {
          pickOption[group.groupId] = group.name
        }
        this.$set(this.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'alarmType') {
        this.$set(
          this.picklistOptions,
          fieldName,
          this.$constants.AlarmCategory
        )
      } else if (specialType === 'sourceType') {
        this.$set(this.picklistOptions, fieldName, this.$constants.SourceType)
      }
    },
    addRow() {
      this.$getProperty(this.fieldChange, 'templateJson.fieldMatcher', []).push(
        {
          field: '',
          isSpacePicker: false,
          value: null,
          parseLabel: null,
          valueArray: null,
          dateObject: {},
          fieldObj: null,
        }
      )
    },
    getModuleFields() {
      return this.$http
        .get(`/module/metafields?moduleName=${this.module}`)
        .then(response => {
          this.moduleFields = this.$getProperty(
            response,
            'data.meta.fields',
            []
          )
          this.moduleId = this.$getProperty(
            response,
            'data.meta.module.moduleId',
            null
          )
        })
        .catch(() => {})
    },
  },
}
</script>
<style lang="scss" scoped>
.new-escalation-level {
  .fc-modal-sub-title {
    color: #324056;
  }
  .configured-green {
    color: #5bc293;
  }
  .details-Heading {
    font-weight: normal;
    color: #324056;
  }
  .action-row {
    padding: 20px 10px;
    margin-left: -10px;
  }
  .action-row:hover {
    background-color: #f1f8fa;
  }
  .action-edit-section {
    padding-left: 20px;
    border-left: 1px solid #d9e0e1;
  }
}
</style>
<style>
/* Hacky fix to load styles in correct order
  TODO: Move all these into a css file
*/
.ruleDialog .el-dialog,
.ruleDialog .el-dialog.fc-dialog-form {
  position: relative;
  margin: 0 auto 50px;
  background: #fff;
  border-radius: 2px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  box-sizing: border-box;
  height: 70vh;
  margin-top: 15vh !important;
}
.ruleDialog .mail-message-textarea {
  max-height: 300px;
  height: 300px;
  overflow-x: hidden;
  overflow-y: scroll;
  position: relative;
  padding-bottom: 40px;
  white-space: pre-line;
}
.ruleDialog .mail-message-textarea {
  max-height: 250px;
  height: 250px;
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
  color: #3ab2c1;
}
.new-body-modal .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  border-left: 1px solid transparent !important;
}
.workorder-select .el-input__suffix {
  right: -1px;
}
#new-custom-rule .el-input.is-disabled .el-input__inner {
  background-color: transparent;
}
@media screen and (max-width: 1280px) and (min-width: 800px) {
  .custom-rule-dialog {
    width: 55% !important;
  }
}
.fill-greeny-blue .svg .path {
  fill: #3ab2c1 !important;
}
.newescaltion-level-title {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 1.6px;
  word-break: break-word;
}
</style>
