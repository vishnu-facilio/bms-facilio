<template>
  <div>
    <send-notification
      ref="notificationpage"
      :visibility.sync="actionEdit"
      :mode.sync="actionMode"
      :option="actionData"
      @onsave="actionSave"
      class="ruleDialog pm-send-notify"
      :module="module"
    ></send-notification>
    <el-dialog
      v-if="showConfigureNotification"
      title="NOTIFICATION"
      :visible.sync="showConfigureNotification"
      width="50%"
      :append-to-body="true"
      style="z-index: 999999"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog"
    >
      <div class="new-body-modal">
        <div class="positon-relative">
          <el-row class>
            <div class="fc-input-label-txt mb5">Name</div>
            <el-col :span="24">
              <el-input
                v-model="reminderTemplateEdit.name"
                class="fc-input-full-border-select3 width100"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="mT25" :gutter="20">
            <el-col :span="12">
              <div class="fc-input-label-txt mb5">Duration</div>
              <el-select
                v-model="reminderTemplateEdit.duration"
                placeholder="Select"
                class="fc-input-full-border-select3 width100"
              >
                <el-option
                  v-for="item in generateReminderOptions()"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="12">
              <div class="fc-input-label-txt mb5">Type</div>
              <el-select
                v-model="reminderTemplateEdit.type"
                placeholder="Select"
                class="fc-input-full-border-select3 width100"
              >
                <el-option
                  v-for="item in reminderType"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </div>
        <el-row class="pT20">
          <el-col :span="20" class="utility-block">
            <p class="subHeading-pink-txt">ACTIONS</p>
            <p class="small-description-txt">
              Set actions for the corresponding notification type
            </p>
          </el-col>
        </el-row>
        <el-row class="mT20">
          <el-col :span="12">
            <p class="details-Heading">Send Email</p>
            <p class="small-description-txt2">
              The system will send an <br />
              email {{ helpText }}
            </p>
          </el-col>
          <el-col :span="10" class="mT20">
            <el-button
              type="button"
              v-bind:class="
                isEmailConfi ? 'success-green-btn' : 'small-border-btn'
              "
              @click="!isEmailConfi ? addAction('mail') : null"
              >{{ isEmailConfi ? 'Configured' : 'Configure' }}</el-button
            >
            <span v-if="isEmailConfi" class="mL10">
              <i
                class="el-icon-edit pointer"
                @click="editAction(emailContext)"
                title="Edit Mail"
                v-tippy
              ></i>
              <span class="mL10 reset-txt pointer" @click="reset('mail')"
                >Reset</span
              >
            </span>
          </el-col>
        </el-row>
        <el-row class="mT20 border-top-grey pT20">
          <el-col :span="12">
            <p class="details-Heading">Send SMS</p>
            <p class="small-description-txt2">
              The system will send an <br />
              sms {{ helpText }}
            </p>
          </el-col>
          <el-col :span="10" class="mT20">
            <el-button
              type="button"
              v-bind:class="
                isSmsConfi ? 'success-green-btn' : 'small-border-btn'
              "
              @click="!isSmsConfi ? addAction('sms') : null"
              >{{ isSmsConfi ? 'Configured' : 'Configure' }}</el-button
            >
            <span v-if="isSmsConfi" class="mL10">
              <i
                class="el-icon-edit pointer"
                @click="editAction(smsContent)"
                title="Edit Sms"
                v-tippy
              ></i>
              <span class="mL10 reset-txt pointer" @click="reset('sms')"
                >Reset</span
              >
            </span>
          </el-col>
        </el-row>
        <el-row class="mT20 border-top-grey pT20">
          <el-col :span="12">
            <p class="details-Heading">Send Mobile Notification</p>
            <p class="small-description-txt2">
              The system will send <br />
              mobile notification {{ helpText }}
            </p>
          </el-col>
          <el-col :span="10" class="mT20">
            <el-button
              type="button"
              v-bind:class="
                isMobileConfig ? 'success-green-btn' : 'small-border-btn'
              "
              @click="!isMobileConfig ? addAction('mobile') : null"
              >{{ isMobileConfig ? 'Configured' : 'Configure' }}</el-button
            >
            <span v-if="isMobileConfig" class="mL10">
              <i
                class="el-icon-edit pointer"
                @click="editAction(mobileContent)"
                title="Edit Mobile"
                v-tippy
              ></i>
              <span class="mL10 reset-txt pointer" @click="reset('mobile')"
                >Reset</span
              >
            </span>
          </el-col>
        </el-row>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel f13"
          @click="showConfigureNotification = false"
          >Cancel</el-button
        >
        <el-button class="modal-btn-save f13" @click="addReminder"
          >Save</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import SendNotification from 'pages/setup/actions/SendNotification'
export default {
  props: ['showConfigureNotification', 'reminderTemplateEdit', 'reminderType'],
  components: {
    SendNotification,
  },
  data() {
    return {
      module: 'workorder',
      isEmailConfi: false,
      isSmsConfi: false,
      isMobileConfig: false,
      actionMode: 'mail',
      actionData: null,
      actionEdit: false,
      emailContext: '',
      mobileContent: '',
      smsContent: '',
    }
  },
  mounted() {
    if (this.reminderTemplateEdit) {
      this.initData(this.reminderTemplateEdit)
    }
  },
  watch: {
    reminderTemplateEdit: function(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.initData(newVal)
      }
    },
  },
  computed: {
    helpText() {
      let type
      for (let i = 0; i < this.reminderType.length; i++) {
        if (this.reminderType[i].value === this.reminderTemplateEdit.type) {
          type = this.reminderType[i]
          break
        }
      }
      if (!type) {
        return ''
      }
      return type.helpText
    },
  },
  methods: {
    generateReminderOptions() {
      let options = []
      options.push({
        label: '30 Min',
        value: 1800,
      })
      for (let i = 1; i < 24; i++) {
        options.push({
          label: i + (i === 1 ? ' Hour' : ' Hours'),
          value: i * 3600,
        })
      }
      for (let i = 1; i < 32; i++) {
        options.push({
          label: i + (i === 1 ? ' Day' : ' Days'),
          value: i * 24 * 3600,
        })
      }
      return options
    },
    addReminder() {
      let newRemActions = this.updateAction(this.reminderTemplateEdit)
      this.reminderTemplateEdit.reminderActions = newRemActions
      this.$emit('addReminder', this.reminderTemplateEdit)
    },
    closeDialog() {
      this.$emit('closeDialog')
    },
    addAction(cmd) {
      this.actionMode = cmd
      this.actionData = null
      this.actionEdit = true
    },
    actionSave(datas) {
      let data = this.$helpers.cloneObject(datas)
      let idx = this.reminderTemplateEdit.reminderActions.findIndex(
        d => d.action.actionType === data.actionType
      )
      if (idx !== -1) {
        this.reminderTemplateEdit.reminderActions.splice(idx, 1)
      }
      if (data.actionType === 3) {
        this.isEmailConfi = true
        this.emailContext = data
        this.reminderTemplateEdit.isEmail = true
      } else if (data.actionType === 4) {
        this.isSmsConfi = true
        this.smsContent = data
        this.reminderTemplateEdit.isSms = true
      } else if (data.actionType === 7) {
        this.isMobileConfig = true
        this.formatMobileData(data.templateJson)
        this.mobileContent = data
        this.reminderTemplateEdit.isMobile = true
      }
      this.addModuleParam(data)
      this.reminderTemplateEdit.reminderActions.push({ action: data })
      this.actionEdit = false
    },
    addModuleParam(template) {
      if (
        template &&
        template.templateJson &&
        template.templateJson.ftl &&
        template.templateJson.workflow &&
        !template.templateJson.workflow.parameters.some(
          param => param.name === this.module
        )
      ) {
        template.templateJson.workflow.parameters.push({
          name: this.module,
          typeString: 'String',
        })
      }
    },
    // Temporary...TODO need to handle in server
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
    initData(rule) {
      this.isEmailConfi = false
      this.isSmsConfi = false
      this.isMobileConfig = false
      if (!rule.reminderActions) {
        return
      }
      rule.reminderActions.forEach(d => {
        if (parseInt(d.action.actionType) === 3) {
          this.isEmailConfi = true
          this.emailContext = d.action
        } else if (parseInt(d.action.actionType) === 4) {
          this.isSmsConfi = true
          this.smsContent = d.action
        } else if (parseInt(d.action.actionType) === 7) {
          this.isMobileConfig = true
          this.mobileContent = d.action
        }
      })
    },
    reset(action) {
      if (action === 'sms') {
        let idx = this.reminderTemplateEdit.reminderActions.findIndex(
          d => d.action.actionType === 4
        )
        this.reminderTemplateEdit.reminderActions.splice(idx, 1)
        this.smsContent = ''
        this.isSmsConfi = false
        this.reminderTemplateEdit.isSms = false
      } else if (action === 'mail') {
        let idx = this.reminderTemplateEdit.reminderActions.findIndex(
          d => d.action.actionType === 3
        )
        this.reminderTemplateEdit.reminderActions.splice(idx, 1)
        this.emailContext = ''
        this.isEmailConfi = false
        this.reminderTemplateEdit.isEmail = false
      } else if (action === 'mobile') {
        let idx = this.reminderTemplateEdit.reminderActions.findIndex(
          d => d.action.actionType === 7
        )
        this.reminderTemplateEdit.reminderActions.splice(idx, 1)
        this.mobileContent = ''
        this.isMobileConfig = false
        this.reminderTemplateEdit.isMobile = false
      }
      this.$forceUpdate()
    },
    updateAction(reminderTemplateEdit) {
      // Removing web notifications for now
      let newlist = []
      if (
        !reminderTemplateEdit.reminderActions ||
        !reminderTemplateEdit.reminderActions.length
      ) {
        return
      }
      reminderTemplateEdit.reminderActions.map((remaction, i) => {
        let template
        if (remaction.action.actionType === 3) {
          template = this.$refs.notificationpage.getDataToSave(
            this.emailContext,
            'mail'
          )
        } else if (remaction.action.actionType === 4) {
          template = this.$refs.notificationpage.getDataToSave(
            this.smsContent,
            'sms'
          )
        } else if (remaction.action.actionType === 7) {
          let data = this.$refs.notificationpage.getDataToSave(
            this.mobileContent,
            'mobile'
          )
          this.formatMobileData(data.templateJson)
          template = data
        }
        this.addModuleParam(template)
        newlist.push({ action: template })
      })
      return newlist
    },
    editAction(action) {
      if (action.actionType === 3) {
        this.actionData = action
        this.actionMode = 'mail'
        this.actionEdit = true
        this.isEmailConfi = true
      } else if (action.actionType === 4) {
        this.actionData = action
        this.actionMode = 'sms'
        this.actionEdit = true
        this.isSmsConfi = true
      } else if (action.actionType === 7) {
        this.actionData = action
        this.actionMode = 'mobile'
        this.actionEdit = true
        this.isMobileConfig = true
      }
    },
  },
}
</script>
<style lang="scss">
.pm-send-notify {
  .html-editor-icon {
    top: 13px;
  }
}
</style>
