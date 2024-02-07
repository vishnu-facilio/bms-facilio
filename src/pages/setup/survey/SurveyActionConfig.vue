<template>
  <div class="actions-config mB20 survey-action-config">
    <el-row class="mB10 flex-middle">
      <el-col :span="18">
        <slot name="header"></slot>
      </el-col>
      <el-col :span="6" v-if="!hideAction">
        <el-dropdown
          class="fR"
          @command="addAction"
          hide-on-click
          trigger="click"
        >
          <div class="f13 add-action-color">
            <fc-icon
              group="action"
              name="circle-plus"
              class="pointer vertical-middle add-icon-style"
              color="#23b096"
            ></fc-icon>
            {{ $t('survey.add_action') }}
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(action, index) in supportedActions"
              :key="`actiontype-${index}`"
              :command="getActionParam(action)"
            >
              {{ getActionLabel(action) }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-col>
    </el-row>

    <el-row v-if="$validation.isEmpty(actionsList)" class="action-row no-hover">
      <el-col :span="24">
        <p class="details-heading">{{ $t('survey.no_actions_configured') }}</p>
      </el-col>
    </el-row>

    <el-row v-if="isEmailConfi" class="action-row">
      <el-col :span="12">
        <p class="details-heading">{{ $t('survey.send_email') }}</p>
      </el-col>
      <el-col :span="12" class="pT4 text-right">
        <span class="configured-green">{{ $t('survey.configured') }}</span>
        <span v-if="isEmailConfi" class="mL20 action-edit-section">
          <fc-icon
            group="default"
            name="edit-text"
            class="pointer"
            @click="editAction(emailContext)"
            :title="$t('survey.edit_mail')"
            size="15"
          ></fc-icon>
          <span class="mL20 reset-txt pointer" @click="reset('mail')">{{
            $t('survey.reset')
          }}</span>
        </span>
      </el-col>
    </el-row>
    <el-row class="action-row" v-if="isSmsConfi">
      <el-col :span="12">
        <p class="details-heading">{{ $t('survey.send_sms') }}</p>
      </el-col>
      <el-col :span="12" class="pT4 text-right">
        <span class="configured-green">{{ $t('survey.configured') }}</span>
        <span v-if="isSmsConfi" class="mL20 action-edit-section">
          <fc-icon
            group="default"
            name="edit-text"
            class="pointer"
            @click="editAction(smsContent)"
            :title="$t('survey.edit_Sms')"
            size="15"
          ></fc-icon>
          <span class="mL20 reset-txt pointer" @click="reset('sms')">{{
            $t('survey.reset')
          }}</span>
        </span>
      </el-col>
    </el-row>
    <el-row class="action-row" v-if="isMobileConfig">
      <el-col :span="12">
        <p class="details-heading">
          {{ $t('survey.send_mobile_notification') }}
        </p>
      </el-col>
      <el-col :span="12" class="pT4 text-right">
        <span class="configured-green">{{ $t('survey.configured') }}</span>
        <span v-if="isMobileConfig" class="mL20 action-edit-section">
          <fc-icon
            group="default"
            name="edit-text"
            class="pointer"
            @click="editAction(mobileContent)"
            :title="$t('survey.edit_Mobile')"
            size="15"
          ></fc-icon>
          <span class="mL20 reset-txt pointer" @click="reset('mobile')">{{
            $t('survey.reset')
          }}</span>
        </span>
      </el-col>
    </el-row>
    <el-row class="action-row" v-if="isFieldConfi">
      <el-col :span="12">
        <p class="details-heading">{{ $t('survey.field_update') }}</p>
      </el-col>
      <el-col :span="12" class="pT4 text-right">
        <span class="configured-green">{{ $t('survey.configured') }}</span>
        <span v-if="isFieldConfi" class="mL20 action-edit-section">
          <fc-icon
            group="default"
            name="edit-text"
            class="pointer"
            @click="editAction(fieldContext)"
            :title="$t('survey.edit_Field')"
            size="15"
          ></fc-icon>
          <span class="mL20 reset-txt pointer" @click="reset('fieldChange')">
            {{ $t('survey.reset') }}
          </span>
        </span>
      </el-col>
    </el-row>
    <el-row class="action-row" v-if="isScriptConfi">
      <el-col :span="12">
        <p class="details-Heading">{{ $t('survey.execute_script') }}</p>
      </el-col>
      <el-col :span="12" class="pT4 text-right">
        <span class="configured-green">{{ $t('survey.configured') }}</span>
        <span v-if="isScriptConfi" class="mL20 action-edit-section">
          <fc-icon
            group="default"
            name="edit-text"
            class="pointer"
            @click="editAction(scriptData)"
            :title="$t('survey.edit_script')"
            size="15"
          ></fc-icon>
          <span class="mL20 reset-txt pointer" @click="reset('script')">
            {{ $t('survey.reset') }}
          </span>
        </span>
      </el-col>
    </el-row>
    <el-row class="action-row" v-if="isStatusConfig">
      <el-col :span="12">
        <p class="details-heading">{{ $t('survey.change_status') }}</p>
      </el-col>
      <el-col :span="12" class="pT4 text-right">
        <span class="configured-green">{{ $t('survey.configured') }}</span>
        <span v-if="isStatusConfig" class="mL20 action-edit-section">
          <fc-icon
            group="default"
            name="edit-text"
            class="pointer"
            @click="editAction(changeStatus)"
            :title="$t('survey.edit_status')"
            size="15"
          ></fc-icon>
          <span class="mL20 reset-txt pointer" @click="reset('changeStatus')">
            {{ $t('survey.reset') }}
          </span>
        </span>
      </el-col>
    </el-row>
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
    <change-status-dialog
      v-if="openStatusDialog"
      :changeStatus="changeStatus"
      :module="module"
      :actionSave="actionSave"
      :closeDialog="closeDialog"
    ></change-status-dialog>
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
import ActionsConfig from 'pages/setup/approvals/components/ActionConfig.vue'

const actionTypeHash = {
  EMAIL: 3,
  SMS: 4,
  MOBILE: 7,
  FIELD: 13,
  STATUS: 19,
  SCRIPT: 21,
}

export default {
  extends: ActionsConfig,
  props: [
    'module',
    'moduleFields',
    'configuredActions',
    'actionsHash',
    'supportedActions',
    'hideAction',
  ],
  methods: {
    actionSave(dataObj) {
      let actions = this.getActions()
      let data = this.$helpers.cloneObject(dataObj)

      let index = actions.findIndex(d => d.actionType === data.actionType)
      if (index !== -1) {
        actions.splice(index, 1)
      }

      if (data.actionType === actionTypeHash.EMAIL) {
        this.isEmailConfi = true
        this.emailContext = data
        this.$emit('disableAction', true)
      } else if (data.actionType === actionTypeHash.SMS) {
        this.isSmsConfi = true
        this.smsContent = data
      } else if (data.actionType === actionTypeHash.MOBILE) {
        this.isMobileConfig = true
        this.formatMobileData(data.templateJson)
        this.mobileContent = data
      } else if (data.actionType === actionTypeHash.FIELD) {
        this.fieldUpdateValue = false
        this.fieldContext = data
        this.isFieldConfi = true
      } else if (data.actionType === actionTypeHash.STATUS) {
        this.openStatusDialog = false
        this.changeStatus = data
        this.isStatusConfig = true
      } else if (data.actionType === actionTypeHash.SCRIPT) {
        this.showScriptDialog = false
        this.isScriptConfi = true
        this.scriptData = data || {}
      }

      this.addModuleParam(data)
      actions.push(data)
      this.actionEdit = false
    },
    reset(action) {
      let actions = this.getActions()

      if (action === 'sms') {
        let idx = actions.findIndex(d => d.actionType === actionTypeHash.SMS)
        actions.splice(idx, 1)
        this.smsContent = ''
        this.isSmsConfi = false
      } else if (action === 'mail') {
        let idx = actions.findIndex(d => d.actionType === actionTypeHash.EMAIL)
        actions.splice(idx, 1)
        this.emailContext = ''
        this.isEmailConfi = false
        if (idx >= 0 && this.hideAction) this.$emit('enableAction', true)
      } else if (action === 'mobile') {
        let idx = actions.findIndex(d => d.actionType === actionTypeHash.MOBILE)
        actions.splice(idx, 1)
        this.mobileContent = ''
        this.isMobileConfig = false
      } else if (action === 'fieldChange') {
        let idx = actions.findIndex(d => d.actionType === actionTypeHash.FIELD)
        actions.splice(idx, 1)
        this.isFieldConfi = false
      } else if (action === 'changeStatus') {
        let idx = actions.findIndex(d => d.actionType === actionTypeHash.STATUS)
        actions.splice(idx, 1)
        this.isStatusConfig = false
      } else if (action === 'script') {
        let idx = actions.findIndex(d => d.actionType === actionTypeHash.SCRIPT)
        actions.splice(idx, 1)
        this.isScriptConfi = false
        this.scriptData = null
      }
    },
    getActionParam(action) {
      let { actionsHash } = this
      return this.$getProperty(actionsHash, `${action}.actionParam`, '')
    },
    getActionLabel(action) {
      let { actionsHash } = this
      return (
        actionsHash[action] &&
        this.$getProperty(actionsHash, `${action}.label`, '')
      )
    },
  },
}
</script>
<style lang="scss" scoped>
.survey-action-config {
  .add-action-color {
    color: #23b096;
  }
  .add-icon-style {
    height: 18px;
    width: 18px;
    margin: 0px 4px 2px 0px;
  }
}
</style>
