<template>
  <div class="fc-email-template-page">
    <div
      class="form-customization-container fc-email-template-loading"
      v-if="loading"
    >
      <spinner v-if="loading" :show="loading"></spinner>
    </div>
    <template v-else>
      <div class="form-customization-container">
        <el-form :model="emailStructure" :rules="rules" ref="emailForm">
          <div class="fc-email-breadcrumb-header">
            <div>
              <div class="flex-middle fc-setup-breadcrumb">
                <div
                  class="fc-setup-breadcrumb-inner pointer"
                  @click="setupHomeRoute"
                >
                  {{ $t('common.products.home') }}
                </div>
                <div class="fc-setup-breadcrumb-inner pL10 pR10">
                  <i class="el-icon-arrow-right f14 fwBold"></i>
                </div>
                <div
                  class="fc-setup-breadcrumb-inner pointer"
                  @click="setupTemplateRoute"
                >
                  {{ $t('setup.setup.templates') }}
                </div>
                <div class="fc-setup-breadcrumb-inner pL10 pR10">
                  <i class="el-icon-arrow-right f14 fwBold"></i>
                </div>
                <div class="fc-breadcrumbBold-active">
                  {{ moduleMeta.displayName }}
                </div>
              </div>
              <div class="pT10">
                <el-input
                  placeholder="Template name goes here...."
                  class="fc-template-email-input fc-email-top-input-width"
                  v-model="emailStructure.name"
                ></el-input>
              </div>
            </div>
            <div>
              <div class="flex-middle">
                <el-dropdown
                  v-if="isNew"
                  size="medium"
                  split-button
                  type="primary"
                  class="fc-dropdown-menu-template mR20"
                  @command="saveAsDrafts($event)"
                >
                  <el-button
                    @click="emailTemplateAddOrUpdate"
                    class="fc-save-btn-template"
                  >
                    Save
                  </el-button>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="draft"
                      >Save as draft</el-dropdown-item
                    >
                  </el-dropdown-menu>
                </el-dropdown>
                <el-button
                  v-else
                  class="fc-dropdown-menu-template2 mR10"
                  size="medium"
                  @click="emailTemplateAddOrUpdate"
                  style="width: 84px;"
                >
                  Save
                </el-button>

                <el-button
                  class="fc-wo-border-btn height34 pL0 pR0 text-capitalize"
                  @click="goBack"
                  style="line-height: 1px;width: 84px;"
                >
                  {{ $t('setup.users_management.cancel') }}
                </el-button>
              </div>
            </div>
          </div>

          <div class="d-flex m15 mL0 mR0">
            <div class="fc-template-editor-area">
              <div>
                <div>
                  <RichTextArea
                    ref="emailTemplateRichtext"
                    v-if="showPlainTextEditor"
                    v-model="emailStructure.message"
                    :isRichTextMode.sync="isRichTextMode"
                    :disabled="disabled"
                    :isEdit="true"
                    :rows="19"
                    :maxRows="19"
                    :hideImgTool="false"
                    :hideOrderedList="false"
                    :hideUnorderedList="false"
                    :canShowModeToggle="false"
                    :hideToolBar="true"
                    class="fc-email-template-editor-content fc-richtext-message-template"
                  >
                    <template #plainTextModeRichText>
                      <div class="flex-middle">
                        <div
                          @click="showHtmlEditor"
                          class="fc-html-editor-btn"
                          v-if="showPlainTextEditor"
                          v-tippy="{ arrow: true, arrowType: 'round' }"
                          content="HTML"
                        >
                          <inline-svg
                            src="richtext-editor/html"
                            class="toolbar-icon"
                            iconClass="icon icon-lg vertical-middle"
                          />
                        </div>

                        <div class="toolbar-separator mL10"></div>

                        <el-dropdown
                          @command="showPlainTextMode($event)"
                          v-if="showPlainTextEditor"
                        >
                          <span class="el-dropdown-link">
                            <i
                              class="el-icon-more fc-plain-text-14 mL30 mR15"
                            ></i>
                          </span>
                          <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item command="plainTextMode"
                              >Switch to plain text</el-dropdown-item
                            >
                          </el-dropdown-menu>
                        </el-dropdown>
                        <div
                          class="showPlainTextComp"
                          @click="changeMode"
                          v-else
                        >
                          Switch to richtext editor
                        </div>
                      </div>
                    </template>
                    <template #emailSubjectMain>
                      <RichTextArea
                        ref="emailTemplateRichtext"
                        v-if="showPlainTextEditor"
                        v-model="emailStructure.subject"
                        :placeholder="'Subject'"
                        :isRichTextMode.sync="isRichTextMode"
                        :disabled="disabled"
                        :isEdit="true"
                        :rows="1"
                        :maxRows="1"
                        :hideImgTool="false"
                        :hideOrderedList="false"
                        :hideUnorderedList="false"
                        :canShowModeToggle="false"
                        :hideToolBar="false"
                        class="mT20 fc-email-subject-template-header"
                      >
                      </RichTextArea>
                      <el-input
                        placeholder="Subject"
                        class="fc-template-email-input fc-template-subject-email mT10"
                        v-model="emailStructure.subject"
                        v-else
                      ></el-input>
                    </template>
                  </RichTextArea>
                  <div v-else>
                    <div class="fc-changeto-richtext-mode">
                      <div class="label-txt-black bold">
                        Plain text mode
                      </div>
                      <div
                        class="text-right fc-url-blue-txt pointer"
                        @click="changeMode"
                      >
                        <i class="el-icon-back pR10 f16 f18"></i> Change to rich
                        text mode
                      </div>
                    </div>
                    <div>
                      <el-input
                        placeholder="Subject"
                        class="fc-template-email-input fc-template-subject-email mT10"
                        v-model="emailStructure.subject"
                      ></el-input>
                    </div>
                    <el-input
                      v-model="emailStructure.message"
                      type="textarea"
                      class="subject fc-input-full-border-select2 template-editor-area"
                      placeholder="Enter the text here"
                      :autosize="{ minRows: 14, maxRows: 14 }"
                      resize="none"
                    ></el-input>
                  </div>
                </div>
              </div>
            </div>
            <div class="fc-upload-data-right-side">
              <div class="fc-upload-template-tools d-flex">
                <div
                  @click="showAttahchmentBlock"
                  class="pointer p4"
                  :class="{ activeAttach: viewAttachmentBlock }"
                  v-tippy="{ arrow: true, arrowType: 'round' }"
                  content="Attachment"
                >
                  <inline-svg
                    src="svgs/template-attachment"
                    iconClass="icon text-center icon-md fill-black-g"
                  ></inline-svg>
                </div>
                <div
                  @click="showScriptBlock"
                  class="pointer p4"
                  :class="{ activeAttach: viewScriptBlock }"
                  v-tippy="{ arrow: true, arrowType: 'round' }"
                  content="Placeholder"
                >
                  <inline-svg
                    src="svgs/code"
                    iconClass="icon text-center icon-md"
                  ></inline-svg>
                </div>
                <div
                  @click="ShowKeyboardShortcuts"
                  class="pointer p4"
                  :class="{ activeAttach: viewShortcutBlock }"
                  v-tippy="{ arrow: true, arrowType: 'round' }"
                  content="Shortcuts"
                >
                  <inline-svg
                    src="svgs/keyboard-shortcuts"
                    iconClass="icon text-center icon-md"
                  ></inline-svg>
                </div>
              </div>
              <div class="fc-uploaded-right-bg">
                <div
                  class="fc-email-attachment-con relative"
                  v-if="viewAttachmentBlock"
                >
                  <div
                    class="drop-zone"
                    v-if="fileUploader"
                    @drop="handleFileDrop"
                  ></div>
                  <div
                    class="fc-black3-16 pL20 pR20 pT20 pB10 fc-right-side-header"
                  >
                    Upload Properties
                  </div>
                  <div class="fc-right-side-scroll">
                    <div class="fc-attach-using-block">
                      <div class="fc-black2-14 bold">
                        Attach files
                      </div>
                      <div>
                        <DirectFileAttachment
                          module="templatefileattachment"
                          record=""
                          :templateId="templateId"
                          :fileAttachments="fileAttachments"
                          @removeFileId="removeFileId"
                          @addAttachment="addAttachment"
                        ></DirectFileAttachment>
                      </div>
                    </div>

                    <!-- url attachment -->
                    <urlAttachment :urlAttachments="urlAttachments">
                    </urlAttachment>
                    <div class="fc-file-field">
                      <div class="fc-black2-14 bold">
                        Attach file field
                      </div>
                      <div class="fc-file-field-bg">
                        <FileFieldAttachment
                          v-if="viewAttachmentBlock"
                          :fileIdsList.sync="templateFileIds"
                        >
                        </FileFieldAttachment>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="script-block" v-if="viewScriptBlock">
                  <div
                    class="fc-black3-16 pL20 pR20 pT20 pB10 fc-right-side-header"
                  >
                    Placeholder Properties
                  </div>
                  <div class="fc-template-placeholder-tab">
                    <el-tabs
                      v-model="activePlaceholderName"
                      @tab-click="handleClick"
                      class="fc-template-placeholder-tab"
                    >
                      <!-- <el-tab-pane label="Modules" name="module">
                        <div class="pL20 pR20">
                          content
                        </div>
                      </el-tab-pane> -->
                      <el-tab-pane label="Advanced" name="advanced">
                        <div>
                          <div class="position-relative">
                            <code-mirror
                              :codeeditor="true"
                              v-model="
                                emailStructure.userWorkflow.workflowV2String
                              "
                              class="code-editor-template"
                              :placeholder="'//Type your script here'"
                            ></code-mirror>
                            <div>
                              <div
                                class="fc-code-editor-backdrop"
                                v-if="hideBackdrop"
                              >
                                <div class="fc-code-edit">
                                  <div
                                    class="fc-code-edit-txt"
                                    @click="clickToHideBackDrop"
                                  >
                                    Edit
                                  </div>
                                </div>
                              </div>
                              <el-button
                                class="btn-green-full fc-code-edit-btn"
                                @click="showBackdropReturn"
                                v-if="editBtnHideAndShow"
                              >
                                Done
                              </el-button>
                            </div>
                          </div>
                        </div>
                      </el-tab-pane>
                    </el-tabs>
                  </div>
                </div>
                <div class="shortcut-block" v-if="viewShortcutBlock">
                  <div
                    class="fc-black3-16 pL20 pR20 pT20 pB10 fc-right-side-header"
                  >
                    Shortcuts
                  </div>
                  <div>
                    <div class="pT10">
                      <Shortcuts></Shortcuts>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="fc-email-template-bottom-notify" v-if="notifyEnable">
            <i class="el-icon-info"></i> <span class="bold">HINT:</span> To
            insert a merge field, type <b>$</b> and choose one from the list.
            <div class="fc-email-notify-close pointer" @click="closeNotify">
              <i class="el-icon-close bold"></i>
            </div>
          </div>
        </el-form>
      </div>
      <PreviewEmail
        v-if="showTestMail"
        @onClose="showTestMail = false"
        :messageText="emailStructure.name"
        :emailMessage="emailStructure.message"
      >
      </PreviewEmail>
      <HtmlEditor
        v-model="emailStructure.message"
        :options="{ mode: 'text/html' }"
        v-if="showHtmlEditorDialog"
        @onClose="showHtmlEditorDialog = false"
        @onSave="onHtmlEditorClose"
      ></HtmlEditor>
    </template>

    <!-- confiramation dialog back -->
    <el-dialog
      title="Confirmation"
      :visible.sync="confirmationDialogVisible"
      width="35%"
      :before-close="handleClose"
      class="fc-dialog-center-container"
    >
      <div class="fc-confirmation-dialog-body">
        <div class="heading-black16 break-word f14 fw4 line-height20">
          Your changes are not saved. please save your changes if not saved your
          changes will be loose.
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          @click="confirmationDialogVisible = false"
          class="modal-btn-cancel"
        >
          {{ $t('setup.users_management.cancel') }}
        </el-button>
        <el-button
          type="primary"
          class="modal-btn-save fc-error-btn-bg"
          @click="confirmationDialogShow()"
        >
          Confirm
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import RichTextArea from 'pages/setup/EmailTemplates/EmailTemplateRichTextArea'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import CodeMirror from '@/CodeMirror'
import PreviewEmail from 'pages/setup/EmailTemplates/EmailPreview'
import Shortcuts from 'pages/setup/EmailTemplates/EditorShortcuts'
import HtmlEditor from 'pages/setup/EmailTemplates/HtmlEditor'
import { htmlToText } from '@facilio/utils/filters'
import { mapState } from 'vuex'
import FileFieldAttachment from 'src/pages/setup/EmailTemplates/EmailTemplateFileFields'
import DirectFileAttachment from 'pages/setup/EmailTemplates/EmailTemplateDirectAttachment'
import urlAttachment from 'pages/setup/EmailTemplates/EmailTemplateUrlAttachment'
import { getApp } from '@facilio/router'
export default {
  props: ['content', 'disabled'],
  title() {
    return 'Email template'
  },
  data() {
    return {
      draftsModeSelection: null,
      fileUploader: false,
      attachmentTypes: {
        FILE: 1,
        FIELD: 2,
        URL: 3,
      },
      loading: true,
      emailStructure: {
        name: '',
        subject: '',
        message: '',
        userWorkflow: {
          workflowV2String: '',
        },
        workflow: {},
      },
      attachmentsList: [],
      fileAttachments: [],
      urlAttachments: [],
      templateData: [],
      showScriptDialog: false,
      showFileAttachDialog: false,
      templateId: -1,
      templateUrlStrings: [],
      templateFileIds: [],
      templateFileFileIds: [],
      showAttachmentDialog: false,
      showTestMail: false,
      moduleData: [],
      notifyEnable: true,
      showKeyboardShortcuts: false,
      rules: {},
      saving: false,
      showOrHidePlainText: true,
      showPlainTextEditor: true,
      isRichTextMode: true,
      showHtmlEditorDialog: false,
      attachmentDialogVisible: false,
      showUrlFiledAttachment: false,
      showFileUpload: false,
      userScriptHeader: 'Map scriptFunc(Map ' + this.selectedModule + ') {\n',
      userScriptFooter: '\n}',
      attachmentListEdit: [],
      moduleFieldsData: [],
      moduleFieldsTemplateData: [],
      moduleFilterDatas: {},
      viewAttachmentBlock: true,
      viewScriptBlock: false,
      viewShortcutBlock: false,
      activePlaceholderName: 'advanced',
      fileList: [],
      fileFieldsList: [],
      fileFieldIds: [],
      editedUrlField: null,
      hideBackdrop: false,
      editBtnHideAndShow: false,
      fileIds: [],
      isValid: false,
      inActiveUrlClass: false,
      confirmationDialogVisible: false,
      urlFieldDataOnEdit: [],
      attachmentsDataSetEdit: [],
    }
  },
  components: {
    RichTextArea,
    CodeMirror,
    PreviewEmail,
    Shortcuts,
    HtmlEditor,
    FileFieldAttachment,
    DirectFileAttachment,
    urlAttachment,
  },
  async created() {
    this.getModuleFields()
    if (this.content) {
      this.message = this.content
    }
    this.$store.dispatch('view/loadModuleMeta', this.selectedModule)
    this.loadModuleMetaDatas()
    this.moduleDataFilter()
    // this.$store.dispatch('emailTemplate/getMentionsList')
  },
  async mounted() {
    this.loading = true
    await this.fileFieldIdData()
    if (!this.isNew) {
      await this.templateSummaryData()
      if (
        this.templateData.userWorkflow &&
        this.templateData.userWorkflow.workflowV2String
      ) {
        this.hideBackdrop = true
      }
    }
    this.loading = false
    await this.showAttahchmentBlock()
    this.viewAttachmentBlock = true
  },
  computed: {
    selectedModule: {
      get() {
        return this.$route.params.moduleName
      },
      set(moduleName) {
        let { name } = this.$route
        this.$router.replace({
          name,
          params: { moduleName },
        })
      },
    },
    appLinkName() {
      let appName = getApp().linkName
      return appName
    },
    summaryId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    isNew() {
      return isEmpty(this.summaryId)
    },
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
    isNewEditCode() {
      let { emailStructure } = this
      if (emailStructure.userWorkflow === null) {
        return true
      }
      if (emailStructure.userWorkflow > 0) {
        return true
      }
      return false
    },
  },
  methods: {
    handleDragOver() {
      console.log('drop over')
      this.fileUploader = true
    },
    handleDropLeave() {
      console.log('drop leave')
      this.fileUploader = false
    },
    handleFileDrop() {
      console.log('drop')

      this.fileUploader = false
    },
    handleTemplete() {
      if (this.isNewEditCode) {
        this.hideBackdrop = true
      }
    },
    htmlTagsRemove(originalString) {
      return originalString.replace(/(<([^>]+)>)/gi, '')
    },
    async emailTemplateAddOrUpdate() {
      this.$refs['emailForm'].validate(async valid => {
        if (!valid) return false
        this.saving = true
        let url = `v2/template/email/addOrUpdate?moduleName=${this.selectedModule}`
        let params = {}
        let workflowData = {}
        let subjectWorkflowData = {}

        if (this.isNew) {
          if (this.emailStructure.message) {
            this.$common.setExpressionFromPlaceHolder(
              workflowData,
              this.emailStructure.message,
              this.selectedModule
            )
          }
          if (this.emailStructure.subject) {
            this.$common.setExpressionFromPlaceHolder(
              workflowData,
              this.emailStructure.subject
              // this.selectedModule
            )
          }
          params.emailStructure = {
            subject: this.htmlTagsRemove(this.emailStructure.subject),
            name: this.emailStructure.name,
            message: this.emailStructure.message,
            workflow: workflowData,
          }
          if (
            !isEmpty(this.emailStructure.userWorkflow) &&
            !isEmpty(this.emailStructure.userWorkflow.workflowV2String)
          ) {
            if (this.emailStructure.message) {
              this.$common.setExpressionFromPlaceHolder(
                workflowData,
                this.emailStructure.message,
                this.selectedModule
              )
            }
            if (this.emailStructure.subject) {
              this.$common.setExpressionFromPlaceHolder(
                workflowData,
                this.emailStructure.subject
                // this.selectedModule
              )
            }
            params.emailStructure = {
              subject: this.htmlTagsRemove(this.emailStructure.subject),
              name: this.emailStructure.name,
              message: this.emailStructure.message,
              workflow: workflowData,
              userWorkflow: {
                isV2Script: true,
                workflowV2String: `Map scriptFunc(Map ${this.selectedModule}) {\n ${this.emailStructure.userWorkflow.workflowV2String} \n}`,
              },
            }
          }
        }

        if (!this.isNew) {
          if (this.emailStructure.message) {
            this.$common.setExpressionFromPlaceHolder(
              workflowData,
              this.emailStructure.message,
              this.selectedModule
            )
          }
          if (this.emailStructure.subject) {
            this.$common.setExpressionFromPlaceHolder(
              workflowData,
              this.htmlTagsRemove(this.emailStructure.subject)
              // this.selectedModule
            )
          }
          params.emailStructure = {
            id: this.summaryId,
            subject: this.htmlTagsRemove(this.emailStructure.subject),
            name: this.emailStructure.name,
            message: this.emailStructure.message,
            workflow: workflowData,
          }
          if (
            !isEmpty(this.emailStructure.userWorkflow) &&
            !isEmpty(this.emailStructure.userWorkflow.workflowV2String)
          ) {
            this.$common.setExpressionFromPlaceHolder(
              workflowData,
              this.emailStructure.message,
              this.selectedModule
            )
            this.$common.setExpressionFromPlaceHolder(
              subjectWorkflowData,
              this.htmlTagsRemove(this.emailStructure.subject),
              this.selectedModule
            )
            params.emailStructure = {
              id: this.summaryId,
              subject: this.htmlTagsRemove(this.emailStructure.subject),
              name: this.emailStructure.name,
              message: this.emailStructure.message,
              workflow: workflowData,
              userWorkflow: {
                isV2Script: true,
                workflowV2String: `Map scriptFunc(Map ${this.selectedModule}) {\n ${this.emailStructure.userWorkflow.workflowV2String} \n}`,
              },
            }
          }
        }

        if (this.showPlainTextEditor === true) {
          params.emailStructure.html = true
        } else {
          params.emailStructure.html = false
        }

        if (this.draftsModeSelection) {
          params.emailStructure.draft = true
        } else {
          params.emailStructure.draft = false
        }

        if (this.isNew) {
          params.attachmentList = this.constructAttachmentList()
        } else {
          params.attachmentList = this.EditconstructAttachmentList()
        }
        if (this.templateUrlStrings) {
          this.$common.setExpressionFromPlaceHolder(
            this.emailStructure.workflow,
            this.templateUrlStrings.join()
          )
        }
        let { error, data } = await API.post(url, params)
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.$message.success(
            this.$t('setup.emailTemplates.email_saved_successfully')
          )
          this.$emit('onSave', data.emailStructures)
          this.goBack()
        }
        this.saving = false
      })
    },
    async templateSummaryData() {
      let { error, data } = await API.get(
        `v2/template/email/view?id=${this.summaryId}`
      )
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.templateData = data.emailStructure
        this.emailStructure = {
          ...this.templateData,
          name: this.templateData.name,
          subject: this.templateData.subject,
          message: this.templateData.message,
          attachments: this.templateData.attachments,
          userWorkflow: this.templateData.userWorkflow
            ? {
                workflowV2String: this.getScriptWorkflow(
                  this.templateData.userWorkflow
                ),
              }
            : { workflowV2String: '' },
        }
        this.attachmentsList = this.templateData.attachments

        if (this.attachmentsList) {
          this.attachmentsDataSetEdit = []
          this.fileAttachments = []
          this.urlAttachments = []
          this.attachmentsList.forEach(file => {
            if (file.typeInteger === 2) {
              this.attachmentsDataSetEdit.push(file)
            } else if (file.typeInteger === 1) {
              this.fileAttachments.push(file)
            } else if (file.typeInteger === 3) {
              this.urlAttachments.push(file)
            }
          })
        }

        this.setAttachmentfileIds()
      }
    },
    setAttachmentfileIds() {
      if (this.attachmentsList) {
        this.attachmentsList.forEach(file => {
          if (file.typeInteger === 1) {
            this.attachmentsList.push(file.fileId)
          } else if (file.typeInteger === 2) {
            this.templateFileIds.push(file.fieldId)
          } else if (file.typeInteger === 3) {
            this.urlFieldDataOnEdit.push(file.urlString)
          }
        })
      }
    },
    configureScript() {
      this.showScriptDialog = true
    },
    actionSave() {
      this.showScriptDialog = false
    },
    goBack() {
      let { selectedModule } = this
      this.$router.push({ name: 'emailtemplates.list', selectedModule })
    },
    showFileSupportDialog() {
      this.showFileAttachDialog = true
      if (this.showFileAttachment) {
        this.showFileAttachment = true
      }
      if (this.showUrlFiledAttachment) {
        this.showUrlFiledAttachment = true
      }
      if (this.showFileUpload) {
        this.showFileUpload = true
      }
    },
    showAttachmentSupportDialog() {
      this.showFileAttachDialog = true
      this.showFileAttachment = true
      this.showUrlFiledAttachment = false
      this.showFileUpload = false
    },
    showFileUploadSupportDialog() {
      this.showFileAttachDialog = true
      this.showUrlFiledAttachment = true
      this.showFileAttachment = false
      this.showFileUpload = false
    },
    showFileFieldsSupportDialog() {
      this.showFileAttachDialog = true
      this.showFileUpload = true
      this.showUrlFiledAttachment = false
      this.showFileAttachment = false
    },
    EditconstructAttachmentList() {
      this.attachments = []
      let attachments = []
      let { templateFileIds, urlAttachments, fileAttachments } = this
      if (fileAttachments.length) {
        fileAttachments.forEach(rt => {
          let obj = {
            fileId: rt.fileId,
            type: 1,
          }
          attachments.push(obj)
        })
      }
      if (templateFileIds && templateFileIds.length) {
        templateFileIds.forEach(rt => {
          let obj = {
            fieldId: rt,
            type: 2,
          }
          attachments.push(obj)
        })
      }
      if (urlAttachments && urlAttachments.length) {
        urlAttachments.forEach(rt => {
          let obj = {
            urlString: rt.urlString,
            type: 3,
          }
          attachments.push(obj)
        })
      }
      if (attachments.length) {
        this.attachmentList = attachments
      }
      return attachments
    },
    constructAttachmentList() {
      this.attachments = []
      let attachments = []
      let { templateFileIds, urlAttachments, fileAttachments } = this
      if (fileAttachments) {
        fileAttachments.forEach(rt => {
          let obj = {
            fileId: rt.fileId,
            type: 1,
          }
          attachments.push(obj)
        })
      }
      if (templateFileIds && templateFileIds.length) {
        templateFileIds.forEach(rt => {
          let obj = {
            fieldId: rt,
            type: 2,
          }
          attachments.push(obj)
        })
      }
      if (urlAttachments && urlAttachments.length) {
        urlAttachments.forEach(rt => {
          let obj = {
            urlString: rt.urlString,
            type: 3,
          }
          attachments.push(obj)
        })
      }
      if (attachments.length) {
        this.attachmentList = attachments
      }
      return attachments
    },
    onSaveAttachments(urlArray, fileIds, attachments, fileFieldIds) {
      this.templateUrlStrings = urlArray
      this.templateFileIds = fileIds
      this.templateFileFileIds = fileFieldIds
      this.emailStructure.attachmentsList = attachments
    },
    getScriptWorkflow(workflow) {
      if (!isEmpty(workflow) && !isEmpty(workflow.workflowV2String)) {
        return workflow.workflowV2String
          .replace(`Map scriptFunc(Map ${this.selectedModule}) {\n`, '')
          .replace(new RegExp(`\n}` + '$'), '')
      }
      return null
    },
    emailTemplateAction() {
      this.showTestMail = true
    },
    async getModuleFields() {
      let { error, data } = await API.get(
        `v2/modules/fields/fields?moduleName=${this.selectedModule}`
      )
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.moduleData = data.fields
      }
    },
    closeNotify() {
      this.notifyEnable = false
    },
    showKeyboardShortcutsOpen() {
      this.showKeyboardShortcuts = true
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
          this.emailStructure.message = this.emailStructure.message
            .replace(/<div([^>]*)>/gi, '')
            .replace(/<br>/gi, '\n')
            .replace(/<\/div>/gi, '')
            .trim()
          this.showPlainTextEditor = false
        })
        .catch(() => {})
    },
    showPlainText() {
      this.showPlainTextEditor = !this.showPlainTextEditor
    },
    showHtmlEditor() {
      this.showHtmlEditorDialog = true
    },
    changeMode() {
      let { showPlainTextEditor } = this
      if (showPlainTextEditor) {
        this.showConfirmModeSwitch()
      } else {
        let htmlContent = `<p>${this.emailStructure.message}</p>`
        this.emailStructure.message = htmlContent
        this.proceedChangingModes()
      }
    },
    deleteFileFieldsAttachment(key) {
      this.templateFileFileIds.splice(key, 1)
    },
    showConfirmModeSwitch() {
      let dialogObj = {
        htmlMessage: `${this.$t('forms.builder.switch_alert')}`,
        rbDanger: true,
        rbLabel: 'Confirm',
      }
      this.$dialog.confirm(dialogObj).then(canProceed => {
        if (canProceed) {
          let textContent = htmlToText(this.emailStructure.message)
          this.emailStructure.message = textContent
          this.proceedChangingModes()
        }
      })
    },
    proceedChangingModes() {
      let { showPlainTextEditor } = this
      this.showPlainTextEditor = !showPlainTextEditor
    },
    showListOfAttachment() {
      this.attachmentDialogVisible = true
    },
    deleteUrlAttachment(key) {
      this.templateUrlStrings.splice(key, 1)
    },
    getFileIcon(attachment) {
      let { contentType, type } = attachment
      let { FILE_TYPE_ICONS } = this.$constants

      if (!contentType || !type) {
        return FILE_TYPE_ICONS[0].path
      }

      let selectedIndex = FILE_TYPE_ICONS.findIndex(icons => {
        let { fileTypes } = icons
        return fileTypes.some(type => contentType.includes(type))
      })

      if (isEmpty(selectedIndex)) return FILE_TYPE_ICONS[0].path
      else return FILE_TYPE_ICONS[selectedIndex].path
    },
    onHtmlEditorClose(editor) {
      let editorContext = this.$refs['emailTemplateRichtext']
      editorContext.setContent(editor)
      this.$set(this.emailStructure, 'message', editor)
      this.showHtmlEditorDialog = false
    },
    onOptionsSelect(command) {
      if (command === 'file') {
        this.showFileAttachDialog = true
        this.showFileAttachment = true
        this.showUrlFiledAttachment = false
        this.showFileUpload = false
      } else if (command === 'url') {
        this.showFileAttachDialog = true
        this.showUrlFiledAttachment = true
        this.showFileAttachment = false
        this.showFileUpload = false
      } else if (command === 'fileFields') {
        this.showFileAttachDialog = true
        this.showFileUpload = true
        this.showUrlFiledAttachment = false
        this.showFileAttachment = false
      }
    },
    fileFieldIdData() {
      let { moduleMeta } = this
      if (!isEmpty(moduleMeta)) {
        let { fields } = moduleMeta
        let fileFields = []
        if (!isEmpty(fields)) {
          fields.forEach(field => {
            let { dataTypeEnum, displayType } = field || {}
            if (
              !isEmpty(displayType) &&
              displayType._name === 'FILE' &&
              !isEmpty(dataTypeEnum) &&
              dataTypeEnum._name === 'FILE'
            ) {
              fileFields.push(field)
            }
          })
          this.fileFieldsList = fileFields
        }
      }
    },
    deleteFileAttachment(event) {
      this.attachmentsList.splice(event, 1)
    },
    async loadModuleMetaDatas() {
      let { error, data } = await API.get(
        `/module/meta?moduleName=${this.selectedModule}`
      )
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.moduleFieldsData = data.meta && data.meta.fields
      }
    },
    moduleDataFilter() {
      this.moduleFilterDatas = this.attachmentsList.filter(el => {
        return this.moduleFieldsData.some(f => {
          return f.fieldId === el.fieldId && f.fieldId === el.fieldId
        })
      })
    },
    setupHomeRoute() {
      if (this.isNew) {
        return this.$router.replace({
          path: `/${this.appLinkName}/setup/home`,
        })
      } else {
        this.confirmationDialogVisible = true
        return this.$router.replace({
          path: `/${this.appLinkName}/setup/home`,
        })
      }
    },
    setupTemplateRoute() {
      if (this.isNew) {
        return this.$router.replace({
          path: `/${this.appLinkName}/setup/customization/emailtemplates`,
        })
      } else {
        this.confirmationDialogVisible = true
      }
    },
    confirmationDialogShow() {
      if (this.isNew) {
        return this.$router.replace({
          path: `/${this.appLinkName}/setup/customization/emailtemplates`,
        })
      } else {
        this.confirmationDialogVisible = true
        return this.$router.replace({
          path: `/${this.appLinkName}/setup/customization/emailtemplates`,
        })
      }
    },
    showAttahchmentBlock() {
      this.viewAttachmentBlock = true
      this.viewScriptBlock = false
      this.viewShortcutBlock = false
    },
    showScriptBlock() {
      this.viewAttachmentBlock = false
      this.viewScriptBlock = true
      this.viewShortcutBlock = false
    },
    ShowKeyboardShortcuts() {
      this.viewAttachmentBlock = false
      this.viewScriptBlock = false
      this.viewShortcutBlock = true
    },
    showPlainTextMode(command) {
      if (command === 'plainTextMode') {
        this.changeMode()
      }
    },
    handleClick() {
      //tabs clicked
    },
    clickToHideBackDrop() {
      this.hideBackdrop = false
      this.editBtnHideAndShow = true
    },
    showBackdropReturn() {
      this.hideBackdrop = true
      this.editBtnHideAndShow = false
    },
    addAttachment(file) {
      this.addFileId(file.fileId)
    },
    addFileId(fileId) {
      this.fileIds.push(fileId)
    },
    removeFileId(fileId, index) {
      this.fileIds.splice(index, 1)
    },
    handleClose() {
      this.dialogVisible = false
    },
    filesChange(fileList) {
      if (!fileList.length) return

      const formData = new FormData()
      formData.append('fileContent', fileList[0])

      API.post(`/v2/files/add`, formData).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let attachmentIndexMap = {}
          let fileEntry = {
            attachmentId: -1,
            fileId: data.fileInfo.fileId,
            fileName: fileList[0].name,
            fileSize: fileList[0].size,
            contentType: fileList[0].type,
            status: this.status.UPLOADING,
            error: null,
            previewUrl: null,
          }
          this.attachments.push(fileEntry)
          attachmentIndexMap[0 + ''] = this.attachments.length - 1

          this.$emit('addAttachment', data.fileInfo)
        }
      })
    },
    saveAsDrafts(command) {
      if (command === 'draft') {
        this.draftsModeSelection = command
        this.emailTemplateAddOrUpdate()
      }
    },
  },
}
</script>
<style lang="scss">
.drop-zone {
  height: 100%;
  width: 100%;
  position: absolute;
  z-index: 203;
  background: #53ffe26e;
  top: 0;
  left: 0;
}
.showPlainTextComp {
  color: #3478f6;
  font-weight: 500;
  cursor: pointer;
  text-align: right;
  padding-bottom: 10px;
  position: absolute;
  z-index: 100;
  top: 15px;
  right: 15px;
}
.template-editor-area {
  .el-textarea__inner {
    min-height: calc(100vh - 240px) !important;
    border: none !important;
    border-radius: 0 !important;
    border-bottom-left-radius: 8px !important;
    border-bottom-right-radius: 8px !important;
  }
}
.fc-html-editor-btn {
  cursor: pointer;
  z-index: 300;
  padding: 4px;
  border-radius: 4px;
  &:hover {
    background-color: #efefef;
  }
}
.multipane-resizer::after {
  content: '';
  background: url('~assets/svgs/multiresizer.svg') no-repeat;
  width: 50px;
  height: 50px;
  position: absolute;
  top: 40%;
  left: -19px;
  cursor: col-resize;
}
.fc-email-file-icon {
  height: 66px;
  margin-bottom: 10px;
  padding: 10px 15px;
  line-height: 22px;
  border: solid 1px #dae2e9;
  border-radius: 4px;
  display: flex;
  align-items: center;
  position: relative;
  &:hover {
    background: #f5fdff;
  }
  .el-icon-remove-outline {
    position: absolute;
    right: -10px;
    top: 24px;
    background: #fff;
    font-size: 19px;
    transition: 0.2s all;
  }
}
.attachment-empty-card {
  border: 2px solid #eee;
  margin-top: 20px;
  margin-bottom: 20px;
  padding: 20px;
  border-radius: 4px;
  border-style: dotted;
}
.setup-el-btn-fields {
  height: 30px;
  background-color: rgb(57, 178, 194);
  display: inline-block;
  border: none !important;
  text-transform: uppercase;
  font-size: 12px;
  letter-spacing: 0.7px !important;
  font-weight: bold;
  cursor: pointer !important;
  color: #ffffff;
  border-radius: 3px;
  line-height: inherit;
  padding: 0 10px;
  &:hover {
    color: #ffffff;
    background-color: rgb(57, 178, 194);
  }
}
.fc-template-attachment-empty {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
.fc-email-template-page {
  .fc-email-template-loading {
    background: #fff;
  }
}
.template-label-badge {
  font-size: 11px;
  background: #38b2c2;
  color: #fff;
  padding: 3px 10px;
  border-radius: 3px;
}
.fc-email-breadcrumb-header {
  height: 90px;
  padding: 13px 24px;
  background-color: #fff;
  border-bottom: 1px solid #f1f1f1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.activeAttach {
  height: 26px;
  border-radius: 4px;
  background-color: #3ab2c1;
  padding: 4px;
  path {
    fill: #fff;
  }
  g path {
    fill: #fff !important;
  }
}
.fc-template-edit-input {
  .el-input__inner {
    padding-left: 10px;
    padding-right: 10px;
    border-bottom: none;
    height: 35px;
    border-radius: 4px;
  }
}
.fc-template-grey12 {
  font-size: 12px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: 0.43px;
  color: #2f4058;
}
.fc-plain-text-14 {
  color: #2e2e49;
  font-size: 16px;
}
.fc-changeto-richtext-mode {
  width: 81%;
  position: sticky;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-radius: 4px;
  top: 0;
  padding: 16px 15px;
  z-index: 1;
  background: #fff;
  -webkit-box-shadow: 0 2px 6px 0 rgb(87 85 85 / 50%);
  box-shadow: 0 2px 6px 0 rgb(87 85 85 / 50%);
}
.fc-email-top-input-width {
  .el-input__inner {
    width: 300px !important;
  }
}
.fc-code-edit-btn {
  position: absolute;
  bottom: 38px;
  z-index: 500;
  left: 0;
  right: 0;
}
.activeEnableBtn {
  background: #3ab2c1;
  border: 1px solid #3ab2c1;
  color: #fff;
  &:hover,
  &:active,
  &focus {
    background: #3ab2c1;
    border: 1px solid #3ab2c1;
    color: #fff;
  }
}
.fc-email-subject-template-header {
  .richtext-editor {
    overflow-y: hidden !important;
    min-height: 3rem !important;
    max-height: 2rem !important;
  }
  .richtext-content {
    height: 40px;
    padding: 0 !important;
    padding-left: 20px !important;
    padding-right: 20px !important;
    padding-top: 13px !important;
    border-top-left-radius: 8px !important;
    border-top-right-radius: 8px !important;
    border-bottom-left-radius: 0 !important;
    border-bottom-right-radius: 0 !important;
    border-bottom: 1px solid #e6e6e6;
    p {
      font-weight: 500;
    }
  }
}
.fc-email-template-editor-content {
  .richtext-content {
    overflow-y: scroll;
  }
}
.fc-richtext-message-template {
  .richtext-editor {
    height: calc(100vh - 130px);
    min-height: calc(100vh - 130px) !important;
    max-height: calc(100vh - 130px) !important;
  }
}
.fc-email-subject-template-header {
  .richtext-editor {
    height: auto !important;
    min-height: 45px !important;
    max-height: 45px !important;
  }
}
.fc-upload-template-tools {
  .activeAttach {
    &:hover {
      background-color: #3ab2c1 !important;
    }
  }
  .p4 {
    padding: 4px;
    border-radius: 4px;
    &:hover {
      background-color: #efefef;
    }
  }
}
.fc-confirmation-dialog-body {
  height: 150px;
}
.fc-email-template-page {
  .fc-error-btn-bg {
    &:hover,
    &:focus,
    &:active {
      color: #fff !important;
      background: #e47676 !important;
    }
  }
}
.inActiveUrlClass {
  .el-input__inner,
  &:active,
  &:focus {
    border: 1px solid #d60000 !important;
    border-color: #d60000;
    border-radius: 4px;
  }
  &:hover {
    border-color: #d60000;
  }
}
.fc-url-alert-txt {
  font-size: 12px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: 0.43px;
  color: #d60000;
}

.fc-url-template-block {
  .is-disabled {
    font-size: 14px;
    font-weight: 500;
    line-height: normal;
    letter-spacing: 0.5px;
    text-align: center;
    color: #fff;
    border-radius: 5px;
    background-color: #d0d4db;
    opacity: 1 !important;
  }
}
.fill-black-g {
  g path {
    fill: #000;
  }
}
.fc-save-btn-template {
  border: none;
  background: none;
  padding: 0;
  line-height: inherit;
  color: #fff;
  &:hover,
  &:active,
  &:focus {
    color: #fff;
    background: none !important;
  }
}
.code-editor-template {
  .CodeMirror-scroll {
    padding-top: 10px;
  }
  .CodeMirror-sizer {
    border-right-width: 0 !important;
  }
  .CodeMirror-gutters {
    background: none;
  }
}
.fc-dropdown-menu-template {
  .el-button--primary {
    &:active,
    &:focus,
    &:hover {
      color: #fff;
      background: #3ab2c1 !important;
    }
  }
}
</style>
