<template>
  <div>
    <ActionConfiguredHelper
      :isActionConfigured="isEmailConfigured"
      helperText="Send Email after button execution"
      @edit="editAction(emailContext)"
      @reset="reset(emailContext)"
    />
    <ActionConfiguredHelper
      :isActionConfigured="isSMSConfigured"
      helperText="Send SMS after button execution"
      @edit="editAction(smsContent)"
      @reset="reset(smsContent)"
    />
    <ActionConfiguredHelper
      :isActionConfigured="isMobileConfigured"
      helperText="Send Mobile Notification after button execution"
      @edit="editAction(mobileContent)"
      @reset="reset(mobileContent)"
    />

    <send-notification
      ref="notificationpage"
      @update:visibility="() => (showNotificationDialog = false)"
      class="ruleDialog"
      @onsave="actionSave"
      :visibility.sync="showNotificationDialog"
      :mode.sync="actionType"
      :option.sync="actionData"
      :module="module"
    ></send-notification>
  </div>
</template>

<script>
import { actionsHash } from './actionHash'
import SendNotification from 'pages/setup/actions/SendNotification'
import ActionConfiguredHelper from './ActionConfiguredHelper'
import { isEmpty } from '@facilio/utils/validation'
export default {
  components: { SendNotification, ActionConfiguredHelper },
  props: ['module', 'sendNotificationVisible', 'existingAction', 'actionType'],
  data() {
    return {
      isEmailConfigured: false,
      emailContext: '',
      isMobileConfigured: false,
      mobileContent: '',
      isSMSConfigured: false,
      smsContent: '',
      actionData: null,
      showNotificationDialog: this.sendNotificationVisible,
      actions: [],
    }
  },
  watch: {
    sendNotificationVisible: {
      handler: function(newVal) {
        this.showNotificationDialog = newVal
      },
    },
    showNotificationDialog: function() {
      if (!this.showNotificationDialog) {
        this.$emit('update:sendNotificationVisible', false)
      }
    },
  },
  created() {
    if (this.existingAction) {
      this.deserialize()
    }
  },
  mounted() {
    this.$emit('serializeNotificationData', this.notificationSerialize())
  },
  methods: {
    deserialize() {
      let actions = this.existingAction
      this.actions = this.existingAction
      if (actions && actions.length !== 0) {
        actions.forEach(action => {
          if (parseInt(action.actionType) === 3) {
            this.isEmailConfigured = true
            this.emailContext = action
          } else if (parseInt(action.actionType) === 4) {
            this.isSMSConfigured = true
            this.smsContent = action
          } else if (parseInt(action.actionType) === 7) {
            this.isMobileConfigured = true
            this.mobileContent = action
          }
        })
      }
    },
    actionSave(data) {
      this.showNotificationDialog = false
      if (data.actionType === actionsHash.EMAIL.value) {
        this.isEmailConfigured = true
        this.emailContext = data
      } else if (data.actionType === actionsHash.SMS.value) {
        this.isSMSConfigured = true
        this.smsContent = data
      } else if (data.actionType === actionsHash.MOBILE.value) {
        this.isMobileConfigured = true
        this.formatMobileData(data.templateJson)
        this.mobileContent = data
      }

      this.$emit('setProperties', data)
    },

    editAction(action) {
      this.actionData = action
      if (action.actionType === actionsHash.EMAIL.value) {
        this.$emit('update:actionType', actionsHash.EMAIL.name)
        this.isEmailConfigured = true
      } else if (action.actionType === actionsHash.SMS.value) {
        this.$emit('update:actionType', actionsHash.SMS.name)
        this.isSMSConfigured = true
      } else if (action.actionType === actionsHash.MOBILE.value) {
        this.$emit('update:actionType', actionsHash.MOBILE.name)
        this.isMobileConfigured = true
      }

      this.showNotificationDialog = true
    },
    reset(action) {
      if (action.actionType === actionsHash.EMAIL.value) {
        this.isEmailConfigured = false
        this.emailContext = ''
      } else if (action.actionType === actionsHash.SMS.value) {
        this.isSMSConfigured = false
        this.smsContent = ''
      } else if (action.actionType === actionsHash.MOBILE.value) {
        this.isMobileConfigured = false
        this.mobileContent = ''
      }
      let index = this.actions.findIndex(
        actionItem => action.actionType === actionItem.actionType
      )
      if (!isEmpty(index)) {
        this.actions.splice(index, 1, action)
      }
      this.$emit('resetAction', action)
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
    notificationSerialize() {
      return this.$refs.notificationpage
    },
  },
}
</script>

<style>
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
</style>
