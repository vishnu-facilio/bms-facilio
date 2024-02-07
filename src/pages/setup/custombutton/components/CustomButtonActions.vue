<template>
  <div>
    <div class="fc-input-label-txt pT5 bold display-flex-between-space">
      Actions
      <el-dropdown trigger="click" @command="handleAddActionClick">
        <div class="flex flex-direction-row pointer">
          <img
            src="~assets/add-icon.svg"
            style="height:14px;width:14px;"
            class="delete-icon"
          />
          <div class="pL5 configured-green f12 ">Add Actions</div>
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="(action, index) in actionHashKeys"
            :key="index"
            :command="actionHash[action].name"
            >{{ actionHash[action].label }}</el-dropdown-item
          >
        </el-dropdown-menu>
      </el-dropdown>
    </div>

    <hr class="separator-line" />

    <ChangeStatusAction
      :showChangeStatusDialog.sync="showChangeStatusDialog"
      @resetAction="resetAction"
      :existingAction="actions"
      :moduleFields="moduleFields"
      :moduleId="moduleId"
      :actionType.sync="actionType"
      @setProperties="setProperties"
      :module="module"
    />

    <FieldUpdateAction
      :moduleFields="moduleFields"
      :module="module"
      :showFieldUpdateDialog.sync="showFieldUpdateDialog"
      @setProperties="setProperties"
      @resetAction="resetAction"
      :existingAction="actions"
      :actionType.sync="actionType"
    />

    <SendNotification
      :module="module"
      :sendNotificationVisible.sync="sendNotificationVisible"
      @setProperties="setProperties"
      @resetAction="resetAction"
      :existingAction="actions"
      :actionType.sync="actionType"
      @serializeNotificationData="
        value => {
          this.notificationSerialize = value
        }
      "
    />
    <ScriptAction
      :module="module"
      :showScriptCodeDialog.sync="showScriptDialog"
      :actionType.sync="actionType"
      @setProperties="setProperties"
      @resetAction="resetAction"
      :existingAction="actions"
    />
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { actionsHash } from './actions/actionHash'
import FieldUpdateAction from './actions/FieldUpdateAction'
import SendNotification from './actions/SendNotifications'
import ScriptAction from './actions/ScriptAction'
import ChangeStatusAction from './actions/ChangeStatusAction'

export default {
  name: 'CustomButtonActions',
  props: ['module', 'existingAction'],
  components: {
    FieldUpdateAction,
    SendNotification,
    ScriptAction,
    ChangeStatusAction,
  },
  data() {
    return {
      actionHash: actionsHash,
      showFieldUpdateDialog: false,
      showChangeStatusDialog: false,
      actionType: '',
      refs: null,
      sendNotificationVisible: false,
      showScriptDialog: false,
      notificationSerialize: null,
      actions: [],
      currentActionType: null,
      actionHashKeys: Object.keys(actionsHash),
      moduleFieldsMeta: [],
    }
  },
  created() {
    this.getModuleFields()
    if (this.existingAction) {
      this.actions = this.existingAction
    }
  },
  computed: {
    moduleFields() {
      return this.moduleFieldsMeta?.fields || null
    },
    moduleId() {
      return this.moduleFieldsMeta?.module?.moduleId || null
    },
  },
  methods: {
    getModuleFields() {
      API.get(`/module/metafields?moduleName=${this.module}`)
        .then(response => {
          this.moduleFieldsMeta = response.data.meta
        })
        .catch(() => {})
    },
    handleAddActionClick(actionType) {
      this.actionData = null
      if (actionType === actionsHash.EMAIL.name) {
        this.actionType = actionsHash.EMAIL.name
        this.sendNotificationVisible = true
      } else if (actionType === actionsHash.SMS.name) {
        this.actionType = actionsHash.SMS.name
        this.sendNotificationVisible = true
      } else if (actionType === actionsHash.MOBILE.name) {
        this.actionType = actionsHash.MOBILE.name
        this.sendNotificationVisible = true
      } else if (actionType === actionsHash.SCRIPT.name) {
        this.actionType = actionsHash.SCRIPT.name
        this.showScriptDialog = true
      } else if (actionType === actionsHash.FIELD_UPDATE.name) {
        this.actionType = actionsHash.FIELD_UPDATE.name
        this.showFieldUpdateDialog = true
      } else if (actionType === actionsHash.CHANGE_STATUS.name) {
        this.actionType = actionsHash.CHANGE_STATUS.name
        this.showChangeStatusDialog = true
      }
    },
    setProperties(data) {
      let { actions } = this
      let index = actions.findIndex(
        action => action.actionType === data.actionType
      )
      if (!isEmpty(index)) {
        actions.splice(index, 1, data)
      } else {
        actions.push(data)
      }

      this.$emit(
        'onAddAction',
        this.updateAction(actions, this.notificationSerialize)
      )
    },
    resetAction(data) {
      let { actions } = this
      let index = this.actions.findIndex(
        action => action.actionType === data.actionType
      )
      actions.splice(index, 1)
    },
    updateAction(actions, notificationSerialize) {
      let newlist = []
      actions
        .filter(action => action.actionType !== 5)
        .map(action => {
          let template
          if (action.actionType === 3 && !isEmpty(notificationSerialize)) {
            template = notificationSerialize.getDataToSave(action, 'mail')
          } else if (
            action.actionType === 4 &&
            !isEmpty(notificationSerialize)
          ) {
            template = notificationSerialize.getDataToSave(action, 'sms')
          } else if (
            action.actionType === 7 &&
            !isEmpty(notificationSerialize)
          ) {
            let data = notificationSerialize.getDataToSave(action, 'mobile')
            this.formatMobileData(data.templateJson)
            template = data
          } else if (action.actionType === 13) {
            template = action
          } else if (action.actionType === 21) {
            template = {
              actionType: action.actionType,
              templateJson: action.templateJson,
            }
          } else if (action.actionType === 19) {
            let new_state =
              action.templateJson?.new_state ||
              this.$getProperty(
                action,
                'template.originalTemplate.new_state',
                null
              )

            let templateJson = { new_state }
            template = {
              actionType: action.actionType,
              templateJson,
            }
          }

          this.addModuleParam(template)
          newlist.push(template || action)
        })
      return newlist
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
    serializeActions() {
      let actions = this.actions || []
      return this.updateAction(actions, this.notificationSerialize)
    },
  },
}
</script>

<style></style>
