<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    :close-on-click-modal="false"
    :show-close="false"
    class="ea-container"
  >
    <template #title>
      <span>{{ title }}</span>
      <div
        v-if="!showNewTemplate"
        class="fc-pdf-blue-txt-13 pointer pL10 bold fc-underline-hover"
        @click="switchToNewTemplate()"
      >
        {{ $t('setup.emailTemplates.switch_to_new_template') }}
      </div>
    </template>

    <div v-if="loadingForm" class="mT10">
      <div v-for="index in [1, 2, 3]" :key="index">
        <el-row class="mB20">
          <el-col :span="24">
            <div class="lines loading-shimmer width40 mB10"></div>
            <div class="lines loading-shimmer width90 mB15 line-h40"></div>
          </el-col>
        </el-row>
      </div>
    </div>
    <el-form
      v-else
      ref="email-notifcation"
      :rules="rules"
      :model="this.emailNotification.templateJson"
    >
      <el-form-item :label="$t('asset.performance.from')">
        <el-select
          v-model="emailNotification.templateJson.fromAddr"
          class="fc-input-full-border-select2 width100"
        >
          <el-option
            v-for="addr in senderAddr"
            :key="addr.id"
            :label="addr.email"
            :value="addr.id"
            class="subject"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item class="reciever-form-container" prop="to">
        <template #label>
          <div class="grey-text2 pB10">{{ $t('asset.performance.to') }}</div>
          <div class="flex-middle">
            <div
              class="fc-black2-label14 fc-text-underline pointer z-index1"
              v-if="!showEmailCC"
              @click="openCCField()"
            >
              {{ $t('asset.performance.cc') }}
            </div>
            <div
              class="fc-black2-label14 fc-text-underline pL10 pointer z-index1"
              @click="openBCCField()"
              v-if="!showEmailBCC"
            >
              {{ $t('asset.performance.bcc') }}
            </div>
          </div>
        </template>
        <el-select
          v-model="emailNotification.templateJson.to"
          class="fc-input-full-border-select2 width100 "
          multiple
          filterable
          collapse-tags
          remote
          :loading="loadingToReceiver"
          :remote-method="search => setReceiverOptions('to', search)"
          :allow-create="true"
          :disabled="emailNotification.isUserNonEditable"
          @change="handleReceiverAddrChange('to')"
        >
          <el-option-group
            :label="label"
            v-for="(user, label, index) in toList"
            :key="`${user.label}-${index}`"
          >
            <el-option
              v-for="item in user"
              :key="item.value"
              :label="item.displayLabel || item.label"
              :value="item.value"
              class="subject"
            >
              {{ item.displayLabel || item.label }}
            </el-option>
          </el-option-group>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('asset.performance.cc')" v-if="showEmailCC">
        <el-select
          v-model="emailNotification.templateJson.cc"
          class="fc-input-full-border-select2 width100"
          multiple
          filterable
          collapse-tags
          remote
          :loading="loadingCcReceiver"
          :remote-method="search => setReceiverOptions('cc', search)"
          :allow-create="true"
          :disabled="emailNotification.isUserNonEditable"
          @change="handleReceiverAddrChange('cc')"
        >
          <el-option-group
            :label="label"
            v-for="(user, label, index) in ccList"
            :key="`${user.label}-${index}`"
          >
            <el-option
              v-for="item in user"
              :key="item.value"
              :label="item.displayLabel || item.label"
              :value="item.value"
              class="subject"
            >
              {{ item.displayLabel || item.label }}
            </el-option>
          </el-option-group>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('asset.performance.bcc')" v-if="showEmailBCC">
        <el-select
          v-model="emailNotification.templateJson.bcc"
          class="fc-input-full-border-select2 width100"
          multiple
          filterable
          collapse-tags
          remote
          :loading="loadingBccReceiver"
          :remote-method="search => setReceiverOptions('bcc', search)"
          :allow-create="true"
          :disabled="emailNotification.isUserNonEditable"
          @change="handleReceiverAddrChange('bcc')"
        >
          <el-option-group
            :label="label"
            v-for="(user, label, index) in bccList"
            :key="`${user.label}-${index}`"
          >
            <el-option
              v-for="item in user"
              :key="item.value"
              :label="item.displayLabel || item.label"
              :value="item.value"
              class="subject"
            >
            </el-option>
          </el-option-group>
        </el-select>
      </el-form-item>
      <el-form-item>
        <div class="flex-center-row-space border-bottom24 pB20">
          <el-checkbox
            v-model="emailNotification.templateJson.sendAsSeparateMail"
            :disabled="disableSeperateMail"
            >{{ $t('setup.notification.send_as_separate_mail') }}</el-checkbox
          >
          <div
            v-if="emailNotification.isNewTemplate"
            class="fc-green4-label14 pointer"
            @click="openPlaceholderDialog()"
          >
            {{ $t('setup.emailTemplates.advanced_merge_fields') }}
          </div>
        </div>
      </el-form-item>
      <template v-if="!emailNotification.isNewTemplate">
        <el-form-item prop="subject" :label="$t('common._common.subject')">
          <el-input
            placeholder="Please Enter the subject"
            v-model="emailNotification.templateJson.subject"
            type="input"
            class="subject fc-input-full-border2"
          ></el-input>
        </el-form-item>
        <div class="flex-middle justify-content-space mB10">
          <p class="grey-text2 ">{{ $t('common._common.message') }}</p>
          <div class="flex-middle">
            <div
              class="fc-pdf-blue-txt-13 pointer pL10 bold fc-underline-hover  z-index1"
              @click="openPlaceholderDialog()"
            >
              {{ $t('setup.notification.configure_script') }}
            </div>
            <div class="fc-separator-lg mL10 mR10 self-center"></div>

            <div
              class="fc-pdf-blue-txt-13 pointer pL10 bold fc-underline-hover  z-index1"
              @click="showFileAttachDialog = true"
            >
              {{ $t('setup.notification.attach_files') }}
            </div>
            <div class="fc-separator-lg mL10 mR10 self-center"></div>
            <div
              class="fc-pdf-blue-txt-13 fc-underline-hover pointer bold  z-index1"
              v-if="emailNotification.templateJson.html"
              @click="showPlain()"
            >
              {{ $t('setup.notification.plain_text') }}
            </div>
            <div
              class="fc-pdf-blue-txt-13 pointer pL10 bold fc-underline-hover  "
              v-else
              @click="showRichEditor()"
            >
              {{ $t('setup.notification.rich_text_editor') }}
            </div>
          </div>
        </div>

        <div
          class="notification-editor-block"
          v-if="emailNotification.templateJson.html"
        >
          <div
            class="pointer fc-link-blue14 bold insert-html-pos"
            @click="codeEditorVisible = true"
          >
            {{ $t('setup.notification.insert_html') }}
          </div>
          <f-editor
            v-model="emailNotification.templateJson.message"
            ref="templateEditor"
            :editorModules="quillEditorConfig"
            class="height350"
          ></f-editor>
        </div>
        <el-input
          v-else
          v-model="emailNotification.templateJson.message"
          type="textarea"
          class="subject fc-input-full-border-select2 notification-txt-area "
          :placeholder="$t('common.placeholders.type_your_message')"
          :autosize="{ minRows: 6, maxRows: 6 }"
        ></el-input>
      </template>
      <el-form-item v-if="emailNotification.isNewTemplate">
        <template #label>
          <div class="grey-text2">
            {{ $t('setup.emailTemplates.email_templates') }}
          </div>
          <div
            @click="setEmailTemplateList"
            class="pointer position-relative z-index1"
            v-tippy="{ arrow: true, arrowType: 'round' }"
            content="Refresh"
          >
            <i class="el-icon-refresh fc-black2-14 fwBold f16"></i>
          </div>
        </template>
        <el-select
          v-model="emailNotification.templateJson.emailStructureId"
          class="fc-input-full-border2 mT10 width100"
          required
          filterable
          :loading="emailTemplateLoading"
          popper-class="template-popover"
        >
          <template #empty>
            <div v-if="$validation.isEmpty(emailTemplateList)">
              <div class="fc-black2-14 text-center fc-no-data-txt">
                {{ $t('common._common.no_data_available') }}
              </div>
              <el-button
                class="btn-green-full filter-footer-btn fc-btn-position-absolute fw-bold send-notification-template-btn"
                @click="gotoEmailTemplateCreation()"
                >{{ $t('common.header.add_new') }}</el-button
              >
            </div>
          </template>
          <el-option
            v-for="(template, keyTemplate) in emailTemplateList"
            :key="keyTemplate"
            :label="template.name"
            :value="template.id"
          ></el-option>
          <el-button
            class="btn-green-full filter-footer-btn fc-btn-position-absolute fw-bold send-notification-template-btn"
            @click="gotoEmailTemplateCreation()"
            >{{ $t('common.header.add_new') }}</el-button
          >
        </el-select>
      </el-form-item>
    </el-form>
    <div class="modal-dialog-footer">
      <el-button type="button" class="modal-btn-cancel" @click="close">
        {{ $t('common._common.cancel') }}
      </el-button>
      <el-button
        type="button"
        class="modal-btn-save"
        @click="save"
        :disabled="loadingForm"
      >
        {{ $t('common._common._save') }}
      </el-button>
    </div>
    <el-dialog
      :visible="showScriptDialog"
      width="60%"
      class="fieldchange-Dialog pB15 fc-dialog-center-container fc-dialog-center-body-p0"
      :title="$t('common.products.script')"
      :append-to-body="true"
      :show-close="false"
      :close-on-click-modal="false"
    >
      <div class="height350 overflow-y-scroll pB50">
        <CodeMirror :codeeditor="true" v-model="placeholderString"></CodeMirror>
      </div>
      <div class="modal-dialog-footer" style="z-index: 900;">
        <el-button @click="closePlaceholder" class="modal-btn-cancel">{{
          $t('common._common.cancel').toUpperCase()
        }}</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="savePlaceholder"
        >
          {{ $t('common._common.save').toUpperCase() }}
        </el-button>
      </div>
    </el-dialog>
    <FilesAttachment
      v-if="showFileAttachDialog"
      :visibility.sync="showFileAttachDialog"
      :templateId="emailNotification.templateId"
      :urlStringList="emailNotification.templateJson.templateUrlStrings"
      :fileIdsList="emailNotification.templateJson.templateFileIds"
      :fileFieldIdsList="emailNotification.templateJson.templateFileFileIds"
      :attachmentsList="emailNotification.templateJson.attachmentList"
      @onSave="onSaveAttachments"
    >
    </FilesAttachment>
    <code-editor
      v-if="codeEditorVisible"
      :visibility.sync="codeEditorVisible"
      v-model="emailNotification.templateJson.message"
      :options="{ mode: 'text/html' }"
    ></code-editor>
  </el-dialog>
</template>
<script>
import { EmailModel } from 'src/newapp/setupActions/models/EmailModel.js'
import { getApp } from '@facilio/router'
import { cloneDeep } from 'lodash'
import { isEmpty, isNullOrUndefined } from '@facilio/utils/validation'
import CodeMirror from '@/CodeMirror'
import FilesAttachment from 'pages/setup/actions/email-attachment/FilesAttachment'
import { quillEditorConfig } from '../utils'
import CodeEditor from 'src/pages/setup/actions/EmailCodeEditor'
import FEditor from '@/FEditor'

export default {
  props: ['moduleName', 'actionObj', 'rule', 'configName'],
  components: { CodeMirror, FilesAttachment, CodeEditor, FEditor },
  data() {
    return {
      showEmailCC: false,
      showEmailBCC: false,
      notificationMailAddresses: null,
      quillEditorConfig,
      loadingForm: null,
      emailNotification: null,
      showScriptDialog: false,
      placeholderString: '',
      toList: {},
      ccList: {},
      bccList: {},
      loadingToReceiver: false,
      loadingCcReceiver: false,
      loadingBccReceiver: false,
      senderAddr: null,
      receivingUsers: [],
      receivingFields: [],
      emailTemplateList: [],
      emailTemplateLoading: false,
      showFileAttachDialog: false,
      codeEditorVisible: false,
      rules: {
        to: {
          validator: function(rule, value, callback) {
            if (isEmpty(value)) {
              callback(new Error(this.$t('common._common.please_enter_a_name')))
            } else {
              callback()
            }
          }.bind(this),
          trigger: 'blur',
          message: 'TO field cannot be empty',
        },
      },
    }
  },
  created() {
    this.init()
  },
  computed: {
    title() {
      return this.$t('setup.emailSettings.send_email_action')
    },
    disableSeperateMail() {
      let { templateJson } = this.emailNotification || {}
      let { to } = templateJson || {}

      return !to || (to || []).length < 2
    },
    appId() {
      return (getApp() || {}).id
    },
    appLinkName() {
      return (getApp() || {}).linkName
    },
    showNewTemplate() {
      let val = this.emailNotification?.isNewTemplate

      return isNullOrUndefined(val) ? true : val
    },
  },
  methods: {
    async init() {
      this.quillEditorConfig = quillEditorConfig
      this.loadingForm = true
      let {
        moduleName = null,
        actionObj = {},
        rule = {},
        configName = null,
      } = this
      let emailObj = {
        ...actionObj,
        moduleName,
        rule,
        configName,
      }
      this.emailNotification = new EmailModel(emailObj)
      let { isAttachmentAdded } = this.emailNotification.templateJson
      isAttachmentAdded && this.emailNotification.addAttachmentList()
      this.placeholderString = this.$getProperty(
        this.emailNotification,
        'templateJson.userWorkflow.workflowV2String',
        ''
      )
      let { cc, bcc } = this.emailNotification.templateJson
      !isEmpty(cc) && this.openCCField()
      !isEmpty(bcc) && this.openBCCField()
      this.setEmailTemplateList()
      await this.setInitialReceiverDropDowns()
      this.loadingForm = false
    },
    async setInitialReceiverDropDowns() {
      let receivingUsers = [],
        receivingFields = []
      this.senderAddr = await EmailModel.getSenderList()
      let typeList = ['to', 'cc', 'bcc']
      let { data, error } = await this.emailNotification.getReceivingUsers(
        null,
        this.appId,
        typeList
      )
      if (!error) {
        receivingUsers = data
      } else {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      }
      receivingFields = await this.emailNotification.getReceivingFields()

      let receiverCategoricalList = Object.assign(
        { Users: receivingUsers },
        receivingFields
      )
      this.toList = cloneDeep(receiverCategoricalList)
      this.ccList = cloneDeep(this.toList)
      this.bccList = cloneDeep(this.toList)
      typeList.forEach(type => {
        let fieldList = this.$getProperty(this, `${type}List`)
        this.handleReceiverAddrChange(type, fieldList)
      })
      if (!isEmpty(this.senderAddr) && this.emailNotification.isNew) {
        this.emailNotification.templateJson.fromAddr = this.senderAddr.filter(
          em => em.email.startsWith('noreply@')
        )[0].id
      }
    },
    async setEmailTemplateList() {
      this.emailTemplateLoading = true
      let { data, error } = await this.emailNotification.getEmailTemplateList()
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.emailTemplateList = data
      }
      this.emailTemplateLoading = false
    },
    onSaveAttachments(urlArray, fileIds, attachments, fileFieldIds) {
      this.emailNotification.onSaveAttachments(
        urlArray,
        fileIds,
        attachments,
        fileFieldIds
      )
    },
    async handleReceiverAddrChange(receiverType, alternateList) {
      let receiverList = alternateList || {}
      if (isEmpty(alternateList)) {
        receiverList = {
          Users: (this.$getProperty(this, `${receiverType}List`) || {}).Users,
        }
        let otherFields = await this.emailNotification.getReceivingFields()
        receiverList = Object.assign(receiverList, otherFields)
      }
      let list = this.emailNotification.handleAddrChange(
        receiverType,
        receiverList
      )
      this.setListValues(receiverType, list)
    },
    setListValues(receiverType, list) {
      let filteredList = this.removeEmptyGroup(list)
      receiverType === 'to' && (this.toList = filteredList)
      receiverType === 'cc' && (this.ccList = filteredList)
      receiverType === 'bcc' && (this.bccList = filteredList)
    },
    async setReceiverOptions(receiverType, search) {
      this.toggleReceiverLoading(receiverType, true)
      let receivingFields = []
      let {
        data,
        error,
      } = await this.emailNotification.getReceivingUsers(search, this.appId, [
        receiverType,
      ])

      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      }
      let theList = this.$getProperty(this, `${receiverType}List`, {})
      receivingFields = await this.emailNotification.getReceivingFields(search)
      receivingFields = Object.assign({ Users: data }, receivingFields)
      if (isEmpty(search)) {
        receivingFields = this.emailNotification.handleAddrChange(
          receiverType,
          receivingFields
        )
      } else {
        if (!isEmpty(theList?.PlaceHolders)) {
          receivingFields.PlaceHolders = this.searchForValues(
            search,
            theList.PlaceHolders
          )
        }
        if (!isEmpty(theList?.EmailIds)) {
          receivingFields.EmailIds = this.searchForValues(
            search,
            theList.EmailIds
          )
        }
      }
      let listObj = Object.assign({ Users: data }, receivingFields)
      this.setListValues(receiverType, listObj)
      this.toggleReceiverLoading(receiverType, false)
    },
    toggleReceiverLoading(receiverType, loadingState) {
      if (receiverType === 'to') {
        this.loadingToReceiver = loadingState
      } else if (receiverType === 'cc') {
        this.loadingCcReceiver = loadingState
      } else if (receiverType === 'bcc') {
        this.loadingBccReceiver = loadingState
      }
    },
    searchForValues(searchTerm = '', searchableList) {
      let filteredData = (searchableList || []).filter(item => {
        let lcLabel = (item.label || '').toLowerCase()
        let lcSearchTerm = searchTerm.toLowerCase()

        return lcLabel.match(lcSearchTerm)
      })
      return filteredData
    },
    gotoEmailTemplateCreation() {
      let routeData = this.$router.resolve({
        path: `/${this.appLinkName}/setup/customization/emailtemplates/${this.moduleName}/new`,
      })

      return window.open(routeData.href, '_blank')
    },
    removeEmptyGroup(listObj) {
      let arrayedList = Object.entries(listObj || {})
      let filteredList = arrayedList.reduce((prev, [key, arr]) => {
        if (!isEmpty(arr)) {
          prev[key] = arr
        }
        return prev
      }, {})

      return filteredList || {}
    },
    savePlaceholder() {
      this.$set(
        this.emailNotification.templateJson.userWorkflow,
        'workflowV2String',
        this.placeholderString
      )
      this.closePlaceholder()
    },
    closePlaceholder() {
      this.placeholderString = this.$getProperty(
        this.emailNotification,
        'templateJson.userWorkflow.workflowV2String',
        ''
      )
      this.showScriptDialog = false
    },
    openPlaceholderDialog() {
      this.showScriptDialog = true
    },
    openCCField() {
      this.showEmailCC = true
    },
    openBCCField() {
      this.showEmailBCC = true
    },
    async save() {
      let isValid = false

      try {
        isValid = await this.$refs['email-notifcation'].validate()
      } catch {
        isValid = false
      }
      if (!isValid) return
      this.$emit('onSave', this.serializeData())
    },
    serializeData() {
      return this.emailNotification.serialize()
    },
    close() {
      this.$emit('onClose')
    },
    switchToNewTemplate() {
      this.emailNotification.isNewTemplate = true
    },
    showPlain() {
      this.$confirm(
        'Some formatting will be lost when converting to plain text.\n\n Are you sure want to continue?',
        'Change to plain text',
        {
          confirmButtonText: 'Change Format',
          cancelButtonText: 'Cancel',
          cancelButtonClass: 'msg-cancel-btn',
          confirmButtonClass: 'msg-confirm-btn',
          customClass: 'fc-el-msgBox',
          type: 'warning',
        }
      )
        .then(() => {
          this.$refs['templateEditor'].removeFormat()
          this.emailNotification.templateJson.message = this.emailNotification.templateJson.message
            .replace(/<div([^>]*)>/gi, '')
            .replace(/<br>/gi, '\n')
            .replace(/<\/div>/gi, '')
            .trim()
          this.emailNotification.templateJson.html = false
        })
        .catch(() => {})
    },
    showRichEditor() {
      this.emailNotification.templateJson.html = true
      this.emailNotification.prependDivInMessage()
    },
  },
}
</script>
<style lang="scss">
.ea-container {
  margin: 15vh auto !important;
  width: 50% !important;
  height: 70vh !important;
  .insert-html-pos {
    position: absolute;
    right: 20px;
    top: 13px;
  }
  .el-dialog__body {
    padding-top: 0px !important;
    padding-bottom: 0px !important;
    height: 52vh;
    overflow: scroll;
  }
  .el-dialog__header {
    padding: 20px;
    border-bottom: 1px solid #e2e8ee !important;
    position: sticky;
    top: 0;
    font-weight: bold;
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }
  .lines {
    height: 13px;
  }
  .height90 {
    height: 90px;
  }
  .line-h40 {
    height: 40px;
  }
  .el-form-item__label {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding-right: 0px !important;
  }
}
</style>
