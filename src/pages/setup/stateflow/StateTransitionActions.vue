<template>
  <div>
    <el-row class="pR30">
      <el-col :span="24">
        <p class="fc-input-label-txt">
          Configure actions to execute when this transiton occurs.
        </p>
        <el-dropdown @command="addAction" hide-on-click trigger="click">
          <div class="pointer new-statetransition-config configure-blue">
            Configure
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(action, index) in actions"
              :key="index"
              :command="actionsHash[action].actionParam"
              >{{
                actionsHash[action] && actionsHash[action].label
              }}</el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
      </el-col>
    </el-row>

    <hr class="separator-line mR40 mT20" />
    <div class="mR40">
      <el-row v-if="isEmailConfi" class="action-row">
        <el-col :span="12">
          <p class="details-Heading">Send Email</p>
        </el-col>
        <el-col :span="12" class="pT4">
          <span class="configured-green">Configured</span>
          <span v-if="isEmailConfi" class="mL20 action-edit-section">
            <i
              class="el-icon-edit pointer"
              @click="editAction(emailContext)"
              title="Edit Mail"
              v-tippy
            ></i>
            <span class="mL20 reset-txt pointer" @click="reset('mail')"
              >Reset</span
            >
          </span>
        </el-col>
      </el-row>
      <el-row class="action-row" v-if="isSmsConfi">
        <el-col :span="12">
          <p class="details-Heading">Send SMS</p>
        </el-col>
        <el-col :span="12" class="pT4">
          <span class="configured-green">Configured</span>
          <span v-if="isSmsConfi" class="mL20 action-edit-section">
            <i
              class="el-icon-edit pointer"
              @click="editAction(smsContent)"
              title="Edit Sms"
              v-tippy
            ></i>
            <span class="mL20 reset-txt pointer" @click="reset('sms')"
              >Reset</span
            >
          </span>
        </el-col>
      </el-row>
      <el-row class="action-row" v-if="isMobileConfig">
        <el-col :span="12">
          <p class="details-Heading">Send Mobile Notification</p>
        </el-col>
        <el-col :span="12" class="pT4">
          <span class="configured-green">Configured</span>
          <span v-if="isMobileConfig" class="mL20 action-edit-section">
            <i
              class="el-icon-edit pointer"
              @click="editAction(mobileContent)"
              title="Edit Mobile"
              v-tippy
            ></i>
            <span class="mL20 reset-txt pointer" @click="reset('mobile')"
              >Reset</span
            >
          </span>
        </el-col>
      </el-row>
      <el-row class="action-row" v-if="isFieldConfi">
        <el-col :span="12">
          <p class="details-Heading">Field Update</p>
        </el-col>
        <el-col :span="12" class="pT4">
          <span class="configured-green">Configured</span>
          <span v-if="isFieldConfi" class="mL20 action-edit-section">
            <i
              class="el-icon-edit pointer"
              @click="editAction(fieldContext)"
              title="Edit Field"
              v-tippy
            ></i>
            <span class="mL20 reset-txt pointer" @click="reset('fieldChange')"
              >Reset</span
            >
          </span>
        </el-col>
      </el-row>
      <el-row class="action-row" v-if="isScriptConfi">
        <el-col :span="12">
          <p class="details-Heading">Execute Script</p>
        </el-col>
        <el-col :span="12" class="pT4">
          <span class="configured-green">Configured</span>
          <span v-if="isScriptConfi" class="mL20 action-edit-section">
            <i
              class="el-icon-edit pointer"
              @click="editAction(scriptData)"
              title="Edit Script"
              v-tippy
            ></i>
            <span class="mL20 reset-txt pointer" @click="reset('script')"
              >Reset</span
            >
          </span>
        </el-col>
      </el-row>
      <el-row class="action-row" v-if="isStatusConfi">
        <el-col :span="12">
          <p class="details-Heading">Change Status</p>
        </el-col>
        <el-col :span="12" class="pT4">
          <span class="configured-green">Configured</span>
          <span v-if="isStatusConfi" class="mL20 action-edit-section">
            <i
              class="el-icon-edit pointer"
              @click="editAction(changeStatus)"
              title="Edit Script"
              v-tippy
            ></i>
            <span class="mL20 reset-txt pointer" @click="reset('changeStatus')"
              >Reset</span
            >
          </span>
        </el-col>
      </el-row>
    </div>

    <send-notification
      ref="notificationpage"
      class="ruleDialog"
      @onsave="actionSave"
      :visibility.sync="actionEdit"
      :mode.sync="actionMode"
      :option.sync="actionData"
      :module="module"
    ></send-notification>

    <!-- Update Field Value dialog start -->
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
    <ChangeStatusDialog
      v-if="openStatusDialog"
      :actionObj="changeStatus"
      :moduleName="module"
      :moduleId="moduleId"
      @onSave="actionSave"
      @onClose="closeDialog"
    >
    </ChangeStatusDialog>
    <!-- Update Field Value dialog end -->

    <!-- Script Edit Dialog -->
    <ScriptDialog
      v-if="showScriptDialog"
      :actionObj="scriptData"
      :moduleName="module"
      @onSave="actionSave"
      @onClose="showScriptDialog = false"
    ></ScriptDialog>
  </div>
</template>
<script>
import NotificationHelper from 'pages/setup/actions/NotificationHelper'
import SendNotification from 'pages/setup/actions/SendNotification'
import FieldUpdateDialog from '@/fields/FieldUpdateDialog'
import ChangeStatusDialog from 'src/newapp/setupActions/components/StatusUpdate.vue'
import ScriptDialog from 'src/newapp/setupActions/components/ScriptEditor.vue'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { getFieldOptions } from 'util/picklist'

export default {
  props: ['module', 'stateflow', 'actions', 'moduleFields', 'moduleId'],
  mixins: [NotificationHelper],
  components: {
    SendNotification,
    FieldUpdateDialog,
    ScriptDialog,
    ChangeStatusDialog,
  },
  data() {
    return {
      assets: null,
      categoryMetric: null,
      isEmailConfi: false,
      isFieldConfi: false,
      isStatusConfi: false,
      isScriptConfi: false,
      isControlConfi: false,
      showScriptDialog: false,
      emailContext: '',
      smsContent: '',
      fieldContext: '',
      controlContext: '',
      mobileContent: '',
      woContext: '',
      isSmsConfi: false,
      isMobileConfig: false,
      actionMode: 'mail',
      actionEdit: false,
      picklistOptions: {},
      actionIndex: null,
      fieldUpdateValue: false,
      openStatusDialog: false,
      actionData: null,
      fieldChange: {
        actionType: 13,
        templateJson: {
          fieldMatcher: [],
        },
      },
      changeStatus: {
        actionType: 19,
        templateJson: {
          new_state: null,
        },
      },
      actionsHash: {
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
          label: 'Execute Script',
          actionParam: 'script',
        },
        changeStatus: {
          label: 'Change Status',
          actionParam: 'changeStatus',
        },
      },
      scriptData: null,
    }
  },
  mounted() {
    if (this.stateflow) {
      this.initData(this.stateflow)
    }
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadGroups')
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketcategory: state => state.ticketCategory,
      assetCategory: state => state.assetCategory,
      ticketpriority: state => state.ticketPriority,
    }),
  },
  watch: {
    moduleFields() {
      this.initData(this.stateflow)
    },
  },
  methods: {
    initData(stateflow) {
      let actions = this.$getProperty(stateflow, 'actions', []) || []

      if (actions && actions.length === 0) {
        this.addRow()
      } else
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
            this.isScriptConfi = true
            this.scriptData = action
            let templateJson = {
              resultWorkflowContext:
                action.template.originalTemplate.workflowContext,
            }
            this.scriptData = { ...this.scriptData, templateJson }
          } else if (parseInt(action.actionType) === 19) {
            this.isStatusConfi = true
            let new_state = this.$getProperty(
              action,
              'template.originalTemplate.new_state'
            )
            let { changeStatus } = this
            let templateJson = { new_state }
            this.changeStatus = { ...changeStatus, templateJson }
          }
        })
    },

    editAction(action) {
      this.actionData = action

      if (action.actionType === 3) {
        this.actionMode = 'mail'
        this.actionEdit = true
        this.isEmailConfi = true
      } else if (action.actionType === 4) {
        this.actionMode = 'sms'
        this.actionEdit = true
        this.isSmsConfi = true
      } else if (action.actionType === 7) {
        this.actionMode = 'mobile'
        this.actionEdit = true
        this.isMobileConfig = true
      } else if (action.actionType === 13) {
        this.isFieldConfi = true
        this.fieldUpdateValue = true
        this.actionMode = 'fieldChange'
        this.$forceUpdate()
      } else if (action.actionType === 21) {
        this.actionMode = 'script'
        this.showScriptDialog = true
        this.isScriptConfi = true
      } else if (action.actionType === 19) {
        this.isStatusConfi = true
        this.openStatusDialog = true
        this.actionMode = 'changeStatus'
      }
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
        this.$forceUpdate()
      }
    },

    loadCategoryMetric(id) {
      this.$util.loadAssetReadingFields(-1, id).then(fields => {
        this.categoryMetric = fields
      })
      this.$util
        .loadAsset({ withReadings: true, categoryId: id })
        .then(response => {
          this.assets = []
          this.assets.push({
            id: '${workorder.resource.id}',
            name: 'Current Asset',
          })
          this.assets.push(...response.assets)
        })
    },

    deleteAction(idx) {
      this.stateflow.actions && this.stateflow.actions.splice(idx, 1)
    },

    actionSave(dataObj) {
      let data = this.$helpers.cloneObject(dataObj)
      let actions = this.stateflow.actions || []
      let idx = actions.findIndex(d => d.actionType === data.actionType)

      if (idx !== -1) {
        actions.splice(idx, 1)
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
      } else if (data.actionType === 19) {
        this.openStatusDialog = false
        this.changeStatus = data
        this.isStatusConfi = true
      } else if (data.actionType === 21) {
        this.showScriptDialog = false
        this.isScriptConfi = true
        this.scriptData = data || {}
      }

      this.addModuleParam(data)
      actions.push(data)
      this.actionEdit = false

      this.$emit('onUpdate', this.updateAction(actions))
      this.$emit('autoSave')
    },

    addAction(cmd) {
      this.actionMode = cmd
      this.actionData = null

      if (cmd === 'script') {
        this.showScriptDialog = true
      } else if (
        cmd !== 'workorder' &&
        cmd !== 'fieldChange' &&
        cmd !== 'changeStatus' &&
        cmd !== 'controls'
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
      } else if (cmd === 'changeStatus') {
        this.openStatusDialog = true
      }
    },

    reset(action) {
      let actions = this.stateflow.actions || []

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
      } else if (action === 'changeStatus') {
        let idx = actions.findIndex(d => d.actionType === 19)
        actions.splice(idx, 1)
        this.changeStatus = null
        this.isStatusConfi = false
      } else if (action === 'script') {
        let idx = actions.findIndex(d => d.actionType === 21)
        actions.splice(idx, 1)
        this.isScriptConfi = false
        this.scriptData = null
      }

      this.$emit('autoSave')
      this.$forceUpdate()
    },

    updateAction(actions) {
      let newlist = []
      actions
        .filter(action => action.actionType !== 5)
        .map(action => {
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
              actionType: this.scriptData.actionType,
              templateJson: this.scriptData.templateJson,
            }
          } else if (action.actionType === 19) {
            template = this.changeStatus
          }

          this.addModuleParam(template)
          newlist.push(template || action)
        })
      this.$emit('update:actions', newlist)
      return newlist
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
        template.templateJson.workflow = template.templateJson.workflow || {}
        template.templateJson.workflow.parameters.push({
          name: this.module,
          typeString: 'String',
        })
      }
    },

    serializeActions() {
      let actions = this.stateflow.actions || []
      return this.updateAction(actions)
    },
    closeDialog() {
      this.openStatusDialog = false
    },
  },
}
</script>
<style scoped>
.details-Heading {
  font-weight: normal;
  color: #324056;
}
.action-row {
  padding: 20px 10px;
}
.action-row:hover {
  background-color: #f1f8fa;
}
.action-edit-section {
  padding-left: 20px;
  border-left: 1px solid #d9e0e1;
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
  color: #6171db;
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
</style>
