<template>
  <!-- <div class="fc-formlayout"> -->
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :before-close="closeDialog"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50 no-header fc-el-msgBox fc-dialog-center-container fc-dialog-center-body-p0"
    :append-to-body="true"
    style="z-index: 999999"
  >
    <error-banner :error="error" :errorMessage="errorMessage"></error-banner>
    <div
      class="setup-dialog-lay new-body-modal mT0 pB100 p0"
      style="height: calc(100vh - 200px);"
    >
      <div
        slot="title"
        class="flex-middle width100 justify-content-space p20 border-bottom1px fc-email-template-notify-header"
      >
        <div class="setup-modal-title">
          {{ config[mode] ? config[mode].label : '' }}
        </div>
        <div v-if="!showNewTemplate">
          <div
            class="fc-pdf-blue-txt-13 pointer pL10 bold fc-underline-hover"
            @click="switchToNewTemplate()"
            v-if="mode === 'mail'"
          >
            {{ $t('setup.emailTemplates.switch_to_new_template') }}
          </div>
        </div>
      </div>

      <el-form :model="template" ref="numberValidateForm" class="p20">
        <el-row v-if="mode === 'mobile'">
          <el-col :span="24" class="el-select-block">
            <div class="flex-middle justify-content-space width100">
              <div class="grey-text2 pB10">Application</div>
            </div>
            <el-select
              v-model="template.application"
              class="fc-input-full-border-select2"
              filterable
              default-first-option
              @change="showPushOption(template.application)"
              style="width: 100%;"
            >
              <el-option
                v-for="app in appList"
                :key="app.id"
                :label="app.name"
                :value="app.id"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>

        <!-- This is new view  send notification page-->

        <div v-if="showNewTemplate">
          <div>
            <!-- from input -->
            <el-row v-if="mode === 'mail'" class="border-bottom24 pB30">
              <el-col :span="24" class="el-select-block">
                <div>
                  <div>
                    <p class="grey-text2 pB10">From</p>
                  </div>
                </div>

                <el-select
                  v-model="fromAddr"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="addr in notificationMailAddresses"
                    :key="addr.id"
                    :label="addr.email"
                    :value="addr.id"
                    class="subject"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>

            <!-- To input -->
            <el-row
              class="mT20"
              v-if="
                mode === 'mail' || mode === 'mobile' || mode === 'phonecall'
              "
            >
              <el-col :span="24" class="el-select-block">
                <div class="flex-middle justify-content-space width100 pB10">
                  <div class="grey-text2 pB10">To</div>
                  <div class="flex-middle" v-if="mode === 'mail'">
                    <div
                      class="fc-black2-label14 fc-text-underline pointer"
                      v-if="ccTextHide"
                      @click="toShowCcInput()"
                    >
                      Cc
                    </div>
                    <div
                      class="fc-black2-label14 fc-text-underline pL10 pointer"
                      @click="toShowBccinput()"
                      v-if="bccTextHide"
                    >
                      Bcc
                    </div>
                  </div>
                </div>
                <el-select
                  v-model="toAddr"
                  class="fc-input-full-border-select2"
                  multiple
                  filterable
                  collapse-tags
                  :allow-create="true"
                  default-first-option
                  style="width: 100%;"
                  :disabled="isUserNonEditable"
                  @change="handlePlaceHolders()"
                >
                  <template v-for="(user, index) in userList">
                    <el-option-group
                      :label="user.isGroup ? user.label : ''"
                      :key="`${user.label}-${index}`"
                    >
                      <el-option
                        v-for="item in user.options"
                        :key="item.value"
                        :label="
                          item.displayLabel ? item.displayLabel : item.label
                        "
                        :value="item.value"
                        class="subject"
                      >
                        {{ item.displayLabel ? item.displayLabel : item.label }}
                      </el-option>
                    </el-option-group>
                  </template>
                </el-select>
              </el-col>
            </el-row>

            <el-row class="mT20" v-if="mode === 'mobile'">
              <el-col :span="24">
                <p class="grey-text2 pB10">Subject</p>
                <el-input
                  placeholder="Please Enter the subject"
                  v-model="template.subject"
                  type="input"
                  class="subject fc-input-full-border2"
                ></el-input>
              </el-col>
            </el-row>

            <!-- cc input record -->
            <div v-if="showEmailInputCc">
              <el-row class="mT20" v-if="mode === 'mail'">
                <el-col :span="24" class="el-select-block">
                  <p class="grey-text2 pB10">Cc</p>
                  <el-select
                    v-model="ccAddr"
                    class="fc-input-full-border-select2 width100"
                    multiple
                    filterable
                    :allow-create="true"
                    default-first-option
                    collapse-tags
                    :disabled="isUserNonEditable"
                    @change="handlePlaceHolders()"
                  >
                    <template v-for="(user, index) in userList">
                      <el-option-group
                        :label="user.isGroup ? user.label : ''"
                        :key="`${user.label}-${index}`"
                      >
                        <el-option
                          v-for="item in user.options"
                          :key="item.value"
                          :label="
                            item.displayLabel ? item.displayLabel : item.label
                          "
                          :value="item.value"
                          class="subject"
                        >
                          {{
                            item.displayLabel ? item.displayLabel : item.label
                          }}
                        </el-option>
                      </el-option-group>
                    </template>
                  </el-select>
                </el-col>
              </el-row>
            </div>

            <!-- Bcc input record -->

            <div v-if="showEmailInputBc">
              <el-row v-if="mode === 'mail'" class="mT20">
                <el-col :span="24" class="el-select-block">
                  <p class="grey-text2 pB10">Bcc</p>
                  <el-select
                    v-model="bccAddr"
                    class="fc-input-full-border-select2 width100"
                    multiple
                    filterable
                    :allow-create="true"
                    default-first-option
                    collapse-tags
                    :disabled="isUserNonEditable"
                    @change="handlePlaceHolders()"
                  >
                    <template v-for="(user, index) in userList">
                      <el-option-group
                        :label="user.isGroup ? user.label : ''"
                        :key="`${user.label}-${index}`"
                      >
                        <el-option
                          v-for="item in user.options"
                          :key="item.value"
                          :label="
                            item.displayLabel ? item.displayLabel : item.label
                          "
                          :value="item.value"
                          class="subject"
                        >
                          {{
                            item.displayLabel ? item.displayLabel : item.label
                          }}
                        </el-option>
                      </el-option-group>
                    </template>
                  </el-select>
                </el-col>
              </el-row>
            </div>

            <div
              v-if="mode === 'mail'"
              class="pT10 flex-center-row-space border-bottom24 pB30"
            >
              <div>
                <el-checkbox
                  v-model="sendAsSeparateMail"
                  class="mT10"
                  :disabled="!toAddr || toAddr.length < 2"
                  >{{
                    $t('setup.notification.send_as_separate_mail')
                  }}</el-checkbox
                >
              </div>
              <div
                class="fc-green4-label14 pointer"
                @click="openScriptDialog()"
              >
                {{ $t('setup.emailTemplates.advanced_merge_fields') }}
              </div>
            </div>

            <!-- email templates -->
            <el-row class="mT20 mB20" v-if="mode === 'mail'">
              <el-col :span="24">
                <div class="flex-middle justify-content-space">
                  <div class="grey-text2">
                    {{ $t('setup.emailTemplates.email_templates') }}
                  </div>
                  <div
                    @click="getEmailTemplateList"
                    class="pointer"
                    v-tippy="{ arrow: true, arrowType: 'round' }"
                    content="Refresh"
                  >
                    <i class="el-icon-refresh fc-black2-14 fwBold f16"></i>
                  </div>
                </div>
                <el-select
                  v-if="rerender"
                  v-model="template.emailStructureId"
                  class="fc-input-full-border2 mT10 width100"
                  @change="setEmailStructure(template.emailStructureId)"
                  required
                  filterable
                  popper-class="template-popover"
                >
                  <template slot="empty">
                    <div v-if="$validation.isEmpty(templateListData)">
                      <div class="fc-black2-14 text-center fc-no-data-txt">
                        No data available
                      </div>
                      <el-button
                        class="btn-green-full filter-footer-btn fc-btn-position-absolute fw-bold send-notification-template-btn"
                        @click="gotoEmailTemplateCreation()"
                        >Add new</el-button
                      >
                    </div>
                  </template>
                  <el-option
                    v-for="(template, keyTemplate) in templateListData"
                    :key="keyTemplate"
                    :label="template.name"
                    :value="template.id"
                  ></el-option>
                  <el-button
                    class="btn-green-full filter-footer-btn fc-btn-position-absolute fw-bold send-notification-template-btn"
                    @click="gotoEmailTemplateCreation()"
                    >Add new</el-button
                  >
                </el-select>
              </el-col>
            </el-row>
          </div>
        </div>

        <div v-else-if="!showNewTemplate">
          <el-row v-if="mode === 'mail'">
            <el-col :span="24" class="el-select-block">
              <div>
                <div>
                  <p class="grey-text2 pB10">From</p>
                </div>
              </div>

              <el-select
                v-model="fromAddr"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="addr in notificationMailAddresses"
                  :key="addr.id"
                  :label="addr.email"
                  :value="addr.id"
                  class="subject"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>

          <el-row class="mT20">
            <el-col :span="24" class="el-select-block">
              <div class="flex-middle justify-content-space width100">
                <div class="grey-text2 pB10">To</div>
                <div v-if="mode === 'mail'" class="flex-middle pB10">
                  <div>
                    <el-checkbox
                      v-model="sendAsSeparateMail"
                      class="mT10"
                      :disabled="!toAddr || toAddr.length < 2"
                      >{{
                        $t('setup.notification.send_as_separate_mail')
                      }}</el-checkbox
                    >
                  </div>
                </div>
              </div>
              <el-select
                v-model="toAddr"
                class="fc-input-full-border-select2"
                multiple
                collapse-tags
                filterable
                :allow-create="true"
                default-first-option
                style="width: 100%;"
                :disabled="isUserNonEditable"
                @change="handlePlaceHolders()"
              >
                <template v-for="(user, index) in userList">
                  <el-option-group
                    :label="user.isGroup ? user.label : ''"
                    :key="`${user.label}-${index}`"
                  >
                    <el-option
                      v-for="item in user.options"
                      :key="item.value"
                      :label="
                        item.displayLabel ? item.displayLabel : item.label
                      "
                      :value="item.value"
                      class="subject"
                    >
                      {{ item.displayLabel ? item.displayLabel : item.label }}
                    </el-option>
                  </el-option-group>
                </template>
              </el-select>
            </el-col>
          </el-row>

          <el-row class="mT20" v-if="mode === 'mail'">
            <el-col :span="24" class="el-select-block">
              <p class="grey-text2 pB10">CC</p>
              <el-select
                v-model="ccAddr"
                class="fc-input-full-border-select2 width100"
                multiple
                filterable
                collapse-tags
                :allow-create="true"
                default-first-option
                :disabled="isUserNonEditable"
                @change="handlePlaceHolders()"
              >
                <template v-for="(user, index) in userList">
                  <el-option-group
                    :label="user.isGroup ? user.label : ''"
                    :key="`${user.label}-${index}`"
                  >
                    <el-option
                      v-for="item in user.options"
                      :key="item.value"
                      :label="
                        item.displayLabel ? item.displayLabel : item.label
                      "
                      :value="item.value"
                      class="subject"
                    >
                      {{ item.displayLabel ? item.displayLabel : item.label }}
                    </el-option>
                  </el-option-group>
                </template>
              </el-select>
            </el-col>
          </el-row>

          <el-row v-if="mode === 'mail'" class="mT30">
            <el-col :span="24" class="el-select-block">
              <p class="grey-text2 pB10">BCC</p>
              <el-select
                v-model="bccAddr"
                class="fc-input-full-border-select2 width100"
                multiple
                collapse-tags
                filterable
                :allow-create="true"
                default-first-option
                :disabled="isUserNonEditable"
                @change="handlePlaceHolders()"
              >
                <template v-for="(user, index) in userList">
                  <el-option-group
                    :label="user.isGroup ? user.label : ''"
                    :key="`${user.label}-${index}`"
                  >
                    <el-option
                      v-for="item in user.options"
                      :key="item.value"
                      :label="
                        item.displayLabel ? item.displayLabel : item.label
                      "
                      :value="item.value"
                      class="subject"
                    >
                      {{ item.displayLabel ? item.displayLabel : item.label }}
                    </el-option>
                  </el-option-group>
                </template>
              </el-select>
            </el-col>
          </el-row>
          <div>
            <el-row class="mT20" v-if="mode === 'mobile' || mode === 'mail'">
              <el-col :span="24">
                <p class="grey-text2 pB10">Subject</p>
                <el-input
                  placeholder="Please Enter the subject"
                  v-model="template.subject"
                  type="input"
                  class="subject fc-input-full-border2"
                ></el-input>
              </el-col>
            </el-row>
            <el-row
              class="mT30 mail-message-textarea pB0"
              v-if="mode === 'mail'"
            >
              <el-col :span="24">
                <div class="pB20">
                  <div class="flex-middle justify-content-space pB10">
                    <div class="flex-middle justify-content-space width100">
                      <div class="grey-text2 pB5">Message</div>
                      <div class="flex-middle">
                        <div>
                          <div class="flex-middle">
                            <div
                              class="fc-pdf-blue-txt-13 pointer pL10 bold fc-underline-hover"
                              @click="openScriptDialog()"
                            >
                              {{ $t('setup.notification.configure_script') }}
                            </div>
                            <div
                              class="fc-separator-lg mL10 mR10 self-center"
                            ></div>

                            <div
                              class="fc-pdf-blue-txt-13 pointer pL10 bold fc-underline-hover"
                              @click="showFileSupportDialog()"
                            >
                              {{ $t('setup.notification.attach_files') }}
                            </div>
                            <div
                              class="fc-separator-lg mL10 mR10 self-center"
                            ></div>
                            <div
                              class="fc-pdf-blue-txt-13 fc-underline-hover pointer bold"
                              v-if="showHtmlEditor"
                              @click="showPlain()"
                            >
                              {{ $t('setup.notification.plain_text') }}
                            </div>
                            <div
                              class="fc-pdf-blue-txt-13 pointer pL10 bold fc-underline-hover"
                              v-else
                              @click="showRichEditor()"
                            >
                              {{ $t('setup.notification.rich_text_editor') }}
                            </div>
                            <div
                              class="fc-separator-lg mL10 mR10 self-center"
                            ></div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="notification-editor-block" v-if="showHtmlEditor">
                    <div
                      class="html-editor-icon pointer fc-link-blue14 bold"
                      @click="showCodeEditor()"
                    >
                      {{ $t('setup.notification.insert_html') }}
                    </div>
                    <f-editor
                      v-model="template.message"
                      ref="templateEditor"
                      :editorModules="quillEditorConfig"
                      class="height350"
                    ></f-editor>
                  </div>

                  <el-input
                    v-model="template.message"
                    type="textarea"
                    class="subject fc-input-full-border-select2 notification-txt-area"
                    placeholder="Type your message"
                    :autosize="{ minRows: 6, maxRows: 6 }"
                    v-else
                  ></el-input>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
        <el-row class="mT20">
          <el-col
            :span="24"
            v-if="
              mode === 'mobile' ||
                mode === 'phonecall' ||
                mode === 'sms' ||
                mode === 'whatsapp'
            "
          >
            <div class="grey-text2 pB10">Message</div>
            <el-input
              v-model="template.message"
              type="textarea"
              class="subject fc-input-full-border-select2 notification-txt-area"
              placeholder="Type your message"
              :autosize="{ minRows: 6, maxRows: 6 }"
            ></el-input>
          </el-col>
        </el-row>
        <el-row>
          <div
            v-if="mode === 'mobile' && showCheckIsPush"
            class="flex-middle pB10"
          >
            <div>
              <el-checkbox v-model="isPushNotification" class="mT10">{{
                $t('setup.notification.send_push_notifications')
              }}</el-checkbox>
            </div>
          </div>
        </el-row>
      </el-form>
    </div>

    <div class="modal-dialog-footer">
      <button type="button" class="modal-btn-cancel" @click="closeDialog">
        <span>Cancel</span>
      </button>
      <button type="button" class="modal-btn-save" @click="save">
        <span>Save</span>
      </button>
    </div>

    <code-editor
      v-if="codeEditorVisible"
      v-model="template.message"
      :options="{ mode: 'text/html' }"
      :visibility.sync="codeEditorVisible"
    ></code-editor>

    <FilesAttachment
      v-if="showFileAttachDialog"
      :visibility.sync="showFileAttachDialog"
      :templateId="templateId"
      :urlStringList="template.templateUrlStrings"
      :fileIdsList="template.templateFileIds"
      :fileFieldIdsList="template.templateFileFileIds"
      :attachmentsList="attachmentsList"
      @onSave="onSaveAttachments"
    >
    </FilesAttachment>

    <el-dialog
      :visible.sync="showScriptDialog"
      width="60%"
      class="fieldchange-Dialog pB15 fc-dialog-center-container fc-dialog-center-body-p0"
      title="script"
      :append-to-body="true"
    >
      <div class="height350 overflow-y-scroll pB50">
        <code-mirror
          :codeeditor="true"
          v-model="template.userWorkflow"
          v-if="showScriptDialog"
        ></code-mirror>
      </div>
      <div class="modal-dialog-footer" style="z-index: 900;">
        <el-button @click="actionSave(template)" class="modal-btn-cancel"
          >CANCEL</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="actionSave(template)"
          >Save</el-button
        >
      </div>
    </el-dialog>
  </el-dialog>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'
import NotificationHelper from './NotificationHelper'
import { isEmpty } from 'util/validation'
import FilesAttachment from 'pages/setup/actions/email-attachment/FilesAttachment'
import CodeEditor from 'pages/setup/actions/EmailCodeEditor'
import FEditor from '@/FEditor'
import { API } from '@facilio/api'
import CodeMirror from '@/CodeMirror'
import { getApp } from '@facilio/router'

let config = {
  mail: {
    type: 1, // Template type
    actionType: 3,
    label: 'Send Email Action',
    name: 'New WorkOrder Email Template',
  },
  sms: {
    type: 2, // Template type
    actionType: 4,
    label: 'Send SMS Action',
    name: 'New WorkOrder SMS Template',
  },
  mobile: {
    type: 8, // Template type
    actionType: 7,
    label: 'Send Notification',
    name: 'New WorkOrder Push Notification Template',
  },
  web: {
    type: 9, // Template type
    label: 'Send Web Notification',
    name: 'New WorkOrder Push Notification Template',
  },
  whatsapp: {
    type: 26, // Template type
    actionType: 26,
    label: 'Send Whatsapp Action',
    name: 'New WorkOrder Whatsapp Template',
  },
  phonecall: {
    type: 27, // Template type
    actionType: 27,
    label: 'Make a Phone Call Action',
    name: 'New WorkOrder Phone Call Template',
  },
}

let quillEditorConfig = {
  toolbar: [
    [
      'bold',
      'italic',
      'underline',
      'link',
      {
        list: 'ordered',
      },
      {
        list: 'bullet',
      },
      {
        indent: '-1',
      },
      {
        indent: '+1',
      },
      {
        size: ['small', false, 'large', 'huge'],
      },
      {
        align: ['', 'right', 'center', 'justify'],
      },
      {
        header: [1, 2, 3, 4, 5, 6, false],
      },
    ],
  ],
}

export default {
  props: ['visibility', 'option', 'mode', 'module'],
  components: {
    ErrorBanner,
    CodeEditor,
    FEditor,
    CodeMirror,
    FilesAttachment,
  },
  mixins: [NotificationHelper],
  data() {
    return {
      rerender: true,
      showNewTemplate: false,
      template: {
        subject: '',
        message: '',
        originalTemplate: {
          to: '',
        },
        workflow: {},
        userWorkflow: '',
        application: null,
        templateFileIds: [],
        templateUrlStrings: [],
        templateFileFileIds: [],
        attachmentList: [],
        emailStructureId: null,
      },
      showScriptDialog: false,
      showFileAttachDialog: false,
      showCheckIsPush: false,
      templateId: -1,
      toAddr: [],
      ccAddr: [],
      bccAddr: [],
      notificationMailAddresses: [],
      fromAddr: null,
      isPushNotification: false,
      sendAsSeparateMail: true,
      attachmentsList: [],
      templateFiles: [],
      templateUrls: [],
      error: false,
      errorMessage: '',
      codeEditorVisible: false,
      config,
      enableFormMode: 'Plain text',
      vueEditorData: null,
      quillEditorConfig,
      showHtmlEditor: false,
      appList: [],
      userScriptHeader: 'Map scriptFunc(Map ' + this.module + ') {\n',
      userScriptFooter: '\n}',
      templateListData: [],
      switchToEmailTemplate: true,
      hideOldEditor: true,
      hideOldViewTempalte: true,
      showEmailInputCc: false,
      showEmailInputBc: false,
      showSwitchTemplateTxt: true,
      ccTextHide: true,
      bccTextHide: true,
    }
  },
  async created() {
    await Promise.all([this.loadApps(), this.loadTemplate()])
    API.get('/v3/modules/data/list', {
      moduleName: 'emailFromAddress',
    }).then(({ data, error }) => {
      if (!error) {
        /* sourceType=1 filters all addresses added for notifications */
        data.emailFromAddress = data.emailFromAddress.filter(
          em => em.sourceType === 1
        )
        this.notificationMailAddresses = [...data.emailFromAddress]
        if (this.notificationMailAddresses.length > 0) {
          this.fromAddr = data.emailFromAddress.filter(em =>
            em.email.startsWith('noreply@')
          )[0].id
        }
      }
    })
    this.$store.dispatch('view/loadModuleMeta', this.module)
    this.getEmailTemplateList()
  },
  computed: {
    appLinkName() {
      let appName = getApp().linkName
      return appName
    },
    isFtl() {
      let { templateId, template, templateJson } = this.option || {}
      let isFTLQuery = this.$route.query.ftl === 'true'

      if (templateId && template) {
        return template.ftl
      } else if (templateJson) {
        return templateJson.ftl || false
      } else {
        return isFTLQuery
      }
    },
    isNewTemplate() {
      let { option } = this
      if (option === null) {
        return true
      }
      if (option?.template?.emailStructureId > 0) {
        return true
      }
      return false
    },
  },
  watch: {
    option() {
      this.loadTemplate()
      this.handleTemplete()
    },
    visibility(val) {
      if (val) {
        this.loadTemplate()
        this.handleTemplete()
      }
    },
  },
  methods: {
    setEmailStructure(id) {
      this.rerender = false
      this.$set(this.template, 'emailStructureId', id)
      this.$nextTick(() => {
        this.rerender = true
      })
    },
    handleTemplete() {
      if (this.isNewTemplate) {
        this.showNewTemplate = true
      }
    },
    loadAttachments() {
      let { option } = this
      let { template } = option || {}
      if (template.isAttachmentAdded) {
        API.get('/v2/template/attachment/getList', {
          templateId: option.templateId,
        }).then(({ data, error }) => {
          if (!error) {
            if (!isEmpty(data)) {
              this.deserialize(data)
            }
          }
        })
      }
    },
    deserialize(data) {
      let { attachmentUrlList, attachments, attachmentFileFieldslList } =
        data || {}
      if (!isEmpty(attachmentUrlList)) {
        let urls = []
        for (let i = 0; i < attachmentUrlList.length; i++) {
          urls.push(attachmentUrlList[i].urlString)
        }
        this.template.templateUrlStrings = urls
      }
      if (!isEmpty(attachmentFileFieldslList)) {
        let fileFields = []
        for (let i = 0; i < attachmentFileFieldslList.length; i++) {
          fileFields.push(attachmentFileFieldslList[i].fieldId)
        }
        this.template.templateFileFileIds = fileFields
      }
      if (!isEmpty(attachments)) {
        let ids = []
        for (let i = 0; i < attachments.length; i++) {
          ids.push(attachments[i].fileId)
        }
        this.attachmentsList = attachments
        this.template.templateFileIds = ids
      }
    },
    constructAttachmentList() {
      let { template } = this || {}
      this.template.attachmentList = []
      let attachmentsList = []
      let {
        templateFileIds,
        templateUrlStrings,
        templateFileFileIds,
      } = template
      if (!isEmpty(templateFileIds)) {
        for (let i = 0; i < templateFileIds.length; i++) {
          let obj = {
            fileId: templateFileIds[i],
            type: 1,
          }
          attachmentsList.push(obj)
        }
      }
      if (!isEmpty(templateFileFileIds)) {
        for (let i = 0; i < templateFileFileIds.length; i++) {
          let obj = {
            fieldId: templateFileFileIds[i],
            type: 2,
          }
          attachmentsList.push(obj)
        }
      }
      if (!isEmpty(templateUrlStrings)) {
        for (let i = 0; i < templateUrlStrings.length; i++) {
          if (!isEmpty(templateUrlStrings[i])) {
            let obj = {
              urlString: templateUrlStrings[i],
              type: 3,
            }
            attachmentsList.push(obj)
          }
        }
      }
      if (!isEmpty(attachmentsList)) {
        this.template.attachmentList = attachmentsList
      }
      return attachmentsList
    },
    onSaveAttachments(urlArray, fileIds, attachments, fileFieldIds) {
      this.template.templateUrlStrings = urlArray
      this.template.templateFileIds = fileIds
      this.template.templateFileFileIds = fileFieldIds
      this.attachmentsList = attachments
    },
    handlePlaceHolders() {
      let { toAddr, ccAddr, bccAddr } = this
      let addArr = [...toAddr, ...ccAddr, ...bccAddr]
      addArr = [...new Set(addArr)]
      let placeholders = addArr.filter(add => {
        return isNaN(add) && add.startsWith('${cs.')
      })
      if (!isEmpty(placeholders)) {
        let placeHoldersGroup = this.constructPlaceHoldersOption(
          placeholders,
          'PlaceHolders'
        )
        let placeHoldersGroupArr = this.userList.find(
          list => list.label === 'PlaceHolders'
        )
        if (!isEmpty(placeHoldersGroupArr)) {
          let index = this.userList.indexOf(placeHoldersGroupArr)
          this.userList.splice(index, 1)
        }
        this.userList.push(placeHoldersGroup)
      }
    },
    constructPlaceHoldersOption(placeholders, groupLabel) {
      let optionsArr = []
      placeholders.forEach(placeholder => {
        let options = {
          label: placeholder,
          value: placeholder,
        }
        optionsArr.push(options)
      })
      return {
        label: groupLabel,
        options: optionsArr,
        isGroup: true,
      }
    },
    openScriptDialog() {
      this.showScriptDialog = true
    },
    showFileSupportDialog() {
      this.showFileAttachDialog = true
    },
    getScriptWorkflow(workflow) {
      if (!isEmpty(workflow) && !isEmpty(workflow.workflowV2String)) {
        return workflow.workflowV2String
          .replace(this.userScriptHeader, '')
          .replace(new RegExp(this.userScriptFooter + '$'), '')
      }
      return null
    },
    actionSave() {
      this.showScriptDialog = false
    },
    async loadApps() {
      let { data, error } = await API.get(
        `v2/application/fetchList?moduleName=${this.module}`
      )
      if (!error) {
        const {
          $constants: {
            appCategory: { FEATURE_GROUPING, WORK_CENTERS, PORTALS },
          },
        } = this

        this.appList = data.application.filter(app =>
          [FEATURE_GROUPING, WORK_CENTERS, PORTALS].includes(app.appCategory)
        )
      }
    },
    showPushOption(appId) {
      this.showCheckIsPush = false
      let app = this.appList.find(app => app.id === appId) || this.appList[0]
      let { linkName } = app || {}
      if (
        !isEmpty(app) &&
        ['newapp', 'maintenance', 'service', 'tenant', 'vendor'].includes(
          linkName
        )
      ) {
        this.showCheckIsPush = true
      }
    },
    loadTemplate() {
      this.reset()
      if (this.option) {
        // On editing after adding
        if (this.option.templateJson) {
          this.template.message = this.option.templateJson.message
          this.template.subject = this.option.templateJson.subject
          this.template.originalTemplate.to = this.option.templateJson.to
          this.template.originalTemplate.cc = this.option.templateJson.cc
          this.template.originalTemplate.bcc = this.option.templateJson.bcc
          this.template.workflow = this.option.templateJson.workflow
          let applink =
            this.appList.find(app => app.linkName === 'newapp') || {}
          let id = applink.id
          let { option } = this
          let { templateJson } = option || {}
          let { application } = templateJson || {}
          this.template.application = application || id
          this.isPushNotification = this.option.templateJson.isPushNotification
          this.sendAsSeparateMail = this.option.templateJson.sendAsSeparateMail
          this.templateFiles = this.option.templateJson.templateFiles
          this.templateUrls = this.option.templateJson.templateUrls
          this.template.templateFileIds = this.option.templateJson.templateFileIds
          this.template.templateUrlStrings = this.option.templateJson.templateUrlStrings
          this.template.templateFileFileIds = this.option.templateJson.templateFileFileIds
          this.toAddr = this.$common.getUsersFromTemplate(this.template)
          this.ccAddr = this.$common.getUsersFromTemplate(this.template, 'cc')
          this.bccAddr = this.$common.getUsersFromTemplate(this.template, 'bcc')
          this.template.toType = this.option.templateJson.toType
          this.showHtmlEditor = this.option.templateJson.html
          this.$set(
            this.template,
            'emailStructureId',
            this.option.templateJson.emailStructureId
          )
          if (
            !isEmpty(this.template.templateFileIds) ||
            !isEmpty(this.template.templateUrlStrings) ||
            !isEmpty(this.template.templateFileFileIds)
          ) {
            this.template.isAttachmentAdded = true
          } else {
            this.template.isAttachmentAdded = false
          }
          if (
            !isEmpty(this.option.templateJson) &&
            !isEmpty(this.option.templateJson.userWorkflow)
          ) {
            this.template.userWorkflow = this.getScriptWorkflow(
              this.option.templateJson.userWorkflow
            )
          }
          this.template.attachmentList = this.constructAttachmentList()
        } else {
          // On editing
          if (this.option.templateId > 0) {
            this.loadAttachments()
            Object.assign(this.template, this.option.template)
            this.templateId = this.option.templateId
            if (this.option.template && this.option.template.from) {
              this.fromAddr = this.notificationMailAddresses.filter(
                addr => addr.id === this.option.template.fromID
              )[0].id
            }
            if (
              !isEmpty(this.option.template) &&
              !isEmpty(this.option.template.userWorkflow)
            ) {
              this.template.userWorkflow = this.getScriptWorkflow(
                this.option.template.userWorkflow
              )
            }
          } else {
            // Default template
            this.template = this.option.template.originalTemplate
            if (
              this.option.template.originalTemplate &&
              this.option.template.originalTemplate.sender
            ) {
              // this.fromAddr = this.option.template.originalTemplate.sender
            }
          }

          this.toAddr =
            this.mode !== 'web'
              ? this.$common.getUsersFromTemplate(this.option.template)
              : this.option.template.originalTemplate
              ? this.option.template.originalTemplate.id.split()
              : []
          this.ccAddr = this.$common.getUsersFromTemplate(
            this.option.template,
            'cc'
          )
          this.bccAddr = this.$common.getUsersFromTemplate(
            this.option.template,
            'bcc'
          )
          this.template.toType = this.option.template.toType
          this.sendAsSeparateMail =
            this.option.template.originalTemplate.sendAsSeparateMail === false
              ? false
              : true
          this.isPushNotification = this.option.template.originalTemplate.isSendNotification
          this.showPushOption()
          if (
            this.mode === 'mobile' &&
            this.option.template.originalTemplate &&
            this.option.template.originalTemplate.data
          ) {
            this.template.message = this.option.template.originalTemplate.data.text
            this.template.subject = this.option.template.originalTemplate.data.title
          }
          this.showHtmlEditor = this.option.template.originalTemplate.html
        }
      }
      if (this.mode === 'mobile') {
        let defaultApp =
          this.appList.find(app => app.linkName === 'newapp') || {}

        if (this.template.application !== defaultApp.id) {
          // this.isPushNotification = false
          this.showCheckIsPush = true
        } else if (isEmpty(this.template.application)) {
          this.template.application = defaultApp.id
          // this.isPushNotification = true
          this.showCheckIsPush = true
        }
      }
      this.handlePlaceHolders()
    },
    closeDialog() {
      this.showNewTemplate = false
      this.$emit('update:visibility', false)
    },
    validateFields() {
      if (!this.template.message && this.toAddr.length <= 0) {
        this.error = true
        this.errorMessage = ' Please fill field'
        return false
      } else if (this.toAddr.length <= 0) {
        this.error = true
        this.errorMessage = 'Add receiving address'
        return false
      } else if (this.toAddr.length >= 1) {
        this.error = false
      }
      // else if (!this.template.message) {
      //   this.error = true
      //   this.errorMessage = 'Please give message'
      //   return false
      // }
      // else if (this.toAddr && this.template.message) {
      //   this.error = false
      //   return true
      // }
    },
    save() {
      // this.showNewTemplate = false
      this.validateFields()
      if (!this.error) {
        let dataToSave = this.getDataToSave()
        this.$emit('onsave', dataToSave)
      }
    },
    getDataToSave(option, mode) {
      if (option) {
        this.option = option
        this.mode = mode
        this.loadTemplate()
      }
      let data = {
        type: this.config[this.mode].type,
        message: this.template.message,
        name: this.config[this.mode].name,
        workflow: {},
        ftl: this.isFtl,
        html: this.showHtmlEditor,
        toType: this.template.toType,
        sendAsSeparateMail: this.sendAsSeparateMail,
        templateFiles: this.templateFiles,
        templateUrls: this.templateUrls,
        isPushNotification: this.isPushNotification,
        fromAddr: this.fromAddr,
        templateFileIds: this.template.templateFileIds,
        templateUrlStrings: this.template.templateUrlStrings,
        templateFileFileIds: this.template.templateFileFileIds,
      }

      if (this.template.emailStructureId) {
        this.$set(data, 'emailStructureId', this.template.emailStructureId)
      }

      if (!isEmpty(this.template.userWorkflow)) {
        data.userWorkflow = {
          isV2Script: true,
          workflowV2String:
            this.userScriptHeader +
            this.template.userWorkflow +
            this.userScriptFooter,
        }
      }
      if (this.mode === 'mobile') {
        data.isSendNotification = this.isPushNotification
        data.application = this.template.application
      }
      this.$common.setUserMailWorkflow(
        this.toAddr,
        data,
        this.mode,
        this.module,
        this.isFtl,
        !isEmpty(this.ccAddr)
          ? {
              ids: this.ccAddr,
              key: 'cc',
            }
          : null,
        !isEmpty(this.bccAddr)
          ? {
              ids: this.bccAddr,
              key: 'bcc',
            }
          : null
      )
      if (!this.isFtl) {
        this.$common.setExpressionFromPlaceHolder(
          data.workflow,
          this.template.message,
          this.module
        )
      }

      if (this.mode === 'mail' || this.mode === 'mobile') {
        data.subject = this.template.subject
        if (!this.isFtl) {
          this.$common.setExpressionFromPlaceHolder(
            data.workflow,
            this.template.subject
          )
        }
      }

      if (!isEmpty(data.templateUrlStrings)) {
        this.$common.setExpressionFromPlaceHolder(
          data.workflow,
          data.templateUrlStrings.join()
        )
      }

      if (
        this.mode === 'sms' ||
        this.mode === 'phonecall' ||
        this.mode === 'whatsapp'
      ) {
        data.body = this.template.message
      }
      data.attachmentList = this.constructAttachmentList()
      return {
        actionType: this.config[this.mode].actionType,
        templateJson: data,
      }
    },
    reset() {
      this.template = {
        subject: '',
        message: '',
        originalTemplate: {
          to: '',
        },
        workflow: {},
        userWorkflow: '',
        application: null,
      }
      this.templateId = -1
      this.template.fromAddr = null
      this.toAddr = []
      this.ccAddr = []
      this.bccAddr = []
      this.sendAsSeparateMail = true
      this.templateFiles = []
      this.templateUrls = []
      this.showHtmlEditor = false
    },
    showCodeEditor() {
      this.codeEditorVisible = true
    },
    showPlain() {
      let self = this
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
          self.$refs['templateEditor'].removeFormat()
          self.template.message = self.template.message
            .replace(/<div([^>]*)>/gi, '')
            .replace(/<br>/gi, '\n')
            .replace(/<\/div>/gi, '')
            .trim()
          self.showHtmlEditor = false
        })
        .catch(() => {})
    },
    showRichEditor() {
      this.showHtmlEditor = true
      if (this.template.message) {
        this.template.message =
          "<div style='white-space:pre'>" + this.template.message + '</div>'
      }
    },
    async getEmailTemplateList() {
      this.loading = true
      let { error, data } = await API.get(
        `v2/template/email/list?moduleName=${this.module}`
      )
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.templateListData = data.emailStructures
      }
      this.loading = false
    },
    switchToNewTemplate() {
      this.showNewTemplate = true
      this.showSwitchTemplateTxt = false
      this.hideOldViewTempalte = false
      this.existingSubjectShowHide = false
    },
    toShowCcInput() {
      this.showEmailInputCc = !this.showEmailInputCc
      this.ccTextHide = false
    },
    toShowBccinput() {
      this.showEmailInputBc = !this.showEmailInputBc
      this.bccTextHide = false
    },
    gotoEmailTemplateCreation() {
      let routeData = this.$router.resolve({
        path: `/${this.appLinkName}/setup/customization/emailtemplates/${this.module}/new`,
      })
      return window.open(routeData.href, '_blank')
    },
  },
}
</script>

<style lang="scss" scoped>
.subject {
  letter-spacing: 0.4px;
  text-align: left;
  color: #6b7e91;
}

.titlefont {
  font-size: 18px;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
}
</style>
<style lang="scss">
.q-input-target,
.q-input-shadow {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
}

.no-header .el-dialog__header {
  display: none;
}
.fc-email-template-notify-header {
  position: sticky;
  top: 0;
  background: #fff;
  z-index: 100;
}
.fc-btn-position-absolute {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  &:hover,
  &:active,
  &:focus {
    background-color: #39b2c2;
    color: #fff;
  }
}
.fc-no-data-txt {
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-bottom: 40px;
  font-weight: 500;
}
.template-popover {
  .el-select-dropdown__list {
    padding-bottom: 60px;
  }
}
.send-notification-template-btn {
  background: #fff !important;
  color: #39b2c2 !important;
  padding-top: 10px;
  padding-bottom: 10px;
  border-top: 1px solid #efefef !important;
  &:hover {
    background: #fff !important;
    color: #39b2c2 !important;
    border-top: 1px solid #efefef !important;
  }
}
</style>
