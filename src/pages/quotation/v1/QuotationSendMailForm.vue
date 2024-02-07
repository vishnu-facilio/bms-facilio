<template>
  <div class="fc-form-single-wrapper">
    <div @click="goToSummary()" class="active-txt bold pointer f12">
      <i class="el-icon-back pR5 f12"></i>Go to Summary
    </div>
    <div class="fc-black-com f20 fw4 pT5">
      {{ $t('setup.setupLabel.send_email') }}
    </div>
    <div v-if="isLoading" class="flex-middle ">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div v-else class="fc-single-form-con mT10">
      <el-row>
        <el-col :span="22" class="pB80">
          <el-form
            ref="sendMailForm"
            :model="mailJson"
            label-width="120px"
            :label-position="labelPosition"
            :rules="rules"
          >
            <el-form-item
              label="Send to"
              class="el-select-block form-input fc-input-full-border-select2"
              prop="to"
            >
              <el-select
                v-model="mailJson.to"
                multiple
                filterable
                default-first-option
                collapse-tags
                class="width100"
              >
                <el-option
                  v-for="contact in contacts"
                  :key="contact.id"
                  :label="contact.name"
                  :value="contact.email"
                  >{{ contact.name + ' (' + contact.email + ')' }}</el-option
                >
              </el-select>
            </el-form-item>
            <div class="position-relative width100">
              <el-form-item
                label="CC"
                class="el-select-block form-input fc-input-full-border-select2"
                prop="cc"
              >
                <el-select
                  v-model="mailJson.cc"
                  multiple
                  filterable
                  default-first-option
                  collapse-tags
                  class="width100"
                >
                  <el-option
                    v-for="user in usersList"
                    :key="user.id"
                    :label="user.name"
                    :value="user.id"
                    >{{ user.name + ' (' + user.email + ')' }}</el-option
                  >
                </el-select>
              </el-form-item>
              <div
                @click="showBcc = true"
                v-show="!showBcc"
                class="active-txt f14 fc-bcc-txt pointer"
              >
                Bcc
              </div>
            </div>
            <div v-if="showBcc" class="position-relative width100">
              <el-form-item
                label="BCC"
                class="el-select-block form-input fc-input-full-border-select2"
                prop="bcc"
              >
                <el-select
                  v-model="mailJson.bcc"
                  multiple
                  filterable
                  default-first-option
                  collapse-tags
                  class="width100"
                >
                  <el-option
                    v-for="user in usersList"
                    :key="user.id"
                    :label="user.name"
                    :value="user.id"
                    >{{ user.name + ' (' + user.email + ')' }}</el-option
                  >
                </el-select>
              </el-form-item>
            </div>
            <el-form-item label="Subject" prop="subject">
              <el-input
                v-model="mailJson.subject"
                class="fc-input-full-border width100"
              ></el-input>
            </el-form-item>
            <el-form-item label="Message" prop="message">
              <div class="notification-editor-block pB0">
                <div
                  class="html-editor-icon pointer fc-link-blue14 bold mT20"
                  @click="codeEditorVisible = true"
                >
                  Insert HTML
                </div>
                <f-editor
                  v-model="mailJson.message"
                  ref="templateEditor"
                  :editorModules="quillEditorConfig"
                  class="height400"
                ></f-editor>
              </div>
              <code-editor
                v-if="codeEditorVisible"
                v-model="mailJson.message"
                :options="{ mode: 'text/html' }"
                :visibility.sync="codeEditorVisible"
              ></code-editor>
            </el-form-item>
            <el-form-item class="mB10">
              <div class="d-flex width100 justify-content-space">
                <div class="">
                  <el-checkbox v-model="sendPdf">Attach Quote PDF</el-checkbox>
                </div>
              </div>
            </el-form-item>
            <el-form-item v-if="sendPdf">
              <el-row :gutter="20">
                <el-col :span="12" class="pB20 pointer">
                  <div @click="downloadPrintQuotation" class="fc-attch-block">
                    <el-row :gutter="20" class="flex-middle">
                      <el-col :span="2">
                        <i class="el-icon-document fc-grey2-text12 f18"></i>
                      </el-col>
                      <el-col :span="22" class="pointer p210">
                        <div class="fc-black-13 line-height20 text-left">
                          {{ (quotationDetails || {}).subject }}
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </el-col>
              </el-row>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
      <div class="modal-dialog-footer">
        <el-button @click="goToSummary" class="modal-btn-cancel">
          {{ $t('setup.users_management.cancel') }}</el-button
        >
        <el-button
          @click="validateMail"
          type="primary"
          :loading="isSaving"
          class="modal-btn-save"
          >{{ isSaving ? 'Sending...' : 'Send' }}
        </el-button>
      </div>
    </div>
  </div>
</template>
<script>
import FEditor from '@/FEditor'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import CodeEditor from 'pages/setup/actions/EmailCodeEditor'
import pick from 'lodash/pick'
import { mapState } from 'vuex'
import { deepClean } from '@facilio/utils/utility-methods'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
  getApp,
} from '@facilio/router'
export default {
  props: ['id', 'viewname'],
  components: {
    FEditor,
    CodeEditor,
  },
  data() {
    return {
      isLoading: false,
      quotationDetails: {},
      labelPosition: 'left',
      isSaving: false,
      sendPdf: true,
      showBcc: false,
      codeEditorVisible: false,
      contacts: [],
      mailJson: {
        to: [],
        cc: [],
        bcc: [],
        subject: '',
        message: '',
        html: true,
      },
      rules: {
        subject: [
          { required: true, message: 'Please Input Subject', trigger: 'blur' },
        ],
        message: [
          { required: true, message: 'Please Input Message', trigger: 'blur' },
        ],
        to: [
          {
            type: 'array',
            required: true,
            message: 'Please select at least one to address',
            trigger: 'change',
          },
        ],
      },
      quillEditorConfig: {
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
      },
    }
  },
  created() {
    this.loadRecordDetails()
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
    usersList() {
      return (this.users || []).filter(user => user.inviteAcceptStatus)
    },
    pdfUrl() {
      let appName = getApp().linkName
      return (
        window.location.protocol +
        '//' +
        window.location.host +
        `/${appName}/pdf/quotationpdf?quotationId=${this.id}`
      )
    },
    moduleName() {
      return 'quote'
    },
  },
  methods: {
    async loadRecordDetails() {
      this.isLoading = true
      let { quote, error } = await API.fetchRecord('quote', {
        id: this.id,
        fetchContacts: true,
        force: true,
      })
      if (!isEmpty(error)) {
        let { message = 'Error Occured while fetching Quote' } = error
        this.$message.error(message)
      } else {
        this.quotationDetails = quote
        this.setInitialHTMLContent()
        if (quote.customerType) {
          if (quote.customerType == 1) {
            if (
              !isEmpty(this.$getProperty(quote, 'tenant.peopleTenantContacts'))
            ) {
              this.contacts = this.$getProperty(
                quote,
                'tenant.peopleTenantContacts',
                []
              ).filter(
                contact =>
                  !isEmpty(contact.email) &&
                  quote.contact &&
                  quote.contact.email &&
                  contact.email !== quote.contact.email
              )
            }
          } else if (quote.customerType == 2) {
            if (
              !isEmpty(this.$getProperty(quote, 'client.peopleClientContacts'))
            ) {
              this.contacts = this.$getProperty(
                quote,
                'client.peopleClientContacts',
                []
              ).filter(
                contact =>
                  !isEmpty(contact.email) &&
                  quote.contact &&
                  quote.contact.email &&
                  contact.email !== quote.contact.email
              )
            }
          }
        }
        if (quote.contact && quote.contact.email) {
          let qContact = {
            name: 'Quote Contact - ' + quote.contact.name,
            email: quote.contact.email,
          }
          this.contacts.splice(0, 0, qContact)
        }
      }
      this.isLoading = false
    },
    goToSummary() {
      let { viewname, moduleName, id } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
              id,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          path: `/app/tm/quotation/${viewname}/${id}/overview`,
          query: this.$route.query,
        })
      }
    },
    validateMail() {
      this.$refs['sendMailForm'].validate(valid => {
        if (valid) {
          this.sendMail()
        } else {
          return false
        }
      })
    },
    async sendMail() {
      this.isSaving = true
      let { quotationDetails, pdfUrl } = this
      let mailTemplate = this.$helpers.cloneObject(this.mailJson)
      this.$common.setUserMailWorkflow(
        mailTemplate.to,
        mailTemplate,
        'mail',
        null,
        null,
        !isEmpty(mailTemplate.cc)
          ? {
              ids: mailTemplate.cc,
              key: 'cc',
            }
          : null,
        !isEmpty(mailTemplate.bcc)
          ? {
              ids: mailTemplate.bcc,
              key: 'bcc',
            }
          : null
      )
      let quotation = pick(quotationDetails, ['id', 'subject'])
      mailTemplate = deepClean(mailTemplate)
      let params = {
        emailTemplate: mailTemplate,
        quotation,
      }
      if (this.sendPdf) {
        params['pdfUrl'] = pdfUrl
      }
      let { error } = await API.post('/v2/quotation/sendMail', params)
      if (error) {
        let { message = 'Error Occured while sending mail' } = error
        this.$message.error(message)
      } else {
        this.$message.success('Mail Sent Successfully')
        this.goToSummary()
      }
      this.isSaving = false
    },
    downloadPrintQuotation() {
      window.open(this.pdfUrl)
    },
    setInitialHTMLContent() {
      let { quotationDetails: quotation } = this
      let toName =
        quotation.contact && quotation.contact.name
          ? quotation.contact.name
          : ' '
      let message = `<div style="max-width: 600px; margin-left: auto; margin-right: auto;border-top: 2px solid #584a8f; border-left: 2px solid #584a8f; border-right: 2px solid #584a8f; border-top-left-radius: 10px; border-top-right-radius: 10px;padding-left: 20px;padding-top: 20px;padding-right: 20px;">QUOTE #${
        (quotation || {}).id
      }</div>`
      if (!isEmpty((quotation || {}).billDate)) {
        message += `<div style="max-width: 600px; margin-left: auto; margin-right: auto;border-left: 2px solid #584a8f;padding-left: 20px; padding-right: 20px;border-right: 2px solid #584a8f;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;text-align: left;font-size: 12px; font-weight: normal; font-stretch: normal; font-style: normal; line-height: normal; letter-spacing: 0.86px; text-align: left; color: #584a8f !important;">Bill Date: ${this.$options.filters.formatDate(
          (quotation || {}).billDate
        )}</div>`
      }
      message += `<div style="max-width: 600px; margin-left: auto; margin-right: auto;border-left: 2px solid #584a8f;padding-left: 20px; padding-right: 20px;border-right: 2px solid #584a8f;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#19182e;font-size: 13px; font-weight: normal; font-stretch: normal; font-style: normal; line-height: 1.54; letter-spacing: 0.5px;font-weight: bold;padding-top: 10px;">Dear ${toName}, </div><div style="max-width: 600px; margin-left: auto; margin-right: auto;border-left: 2px solid #584a8f;padding-left: 20px; padding-right: 20px;border-right: 2px solid #584a8f;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#19182e;font-size: 13px; font-weight: normal; font-stretch: normal; font-style: normal; line-height: 1.54; letter-spacing: 0.5px;padding-bottom: 20px;">This our Quotation for your Workorder: ${this.$getProperty(
        quotation,
        'workorder.subject',
        ' '
      )}. Please check your Quote attached as PDF.</div><div style="max-width: 600px; margin-left: auto; margin-right: auto;border-left: 2px solid #584a8f;padding-left: 20px; padding-right: 20px;border-right: 2px solid #584a8f;font-family: 'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important; text-align: center; font-size: 12px; font-weight: normal; font-stretch: normal; font-style: normal; line-height: normal; padding-top: 20px; text-align: center; color: #584a8f; background: #f9f7ff;">QUOTATION AMOUNT</div><div style="max-width: 600px; margin-left: auto; margin-right: auto;border-left: 2px solid #584a8f;padding-left: 20px; padding-right: 20px;border-right: 2px solid #584a8f;font-family: 'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important; text-align: center; font-size: 24px; font-weight: bold; padding-top: 10px; font-stretch: normal; font-style: normal; line-height: normal; letter-spacing: 0.75px; text-align: center; color: #584a8f;background: #f9f7ff;padding-bottom: 20px;">${this.getFormattedCurrency(
        (quotation || {}).totalCost
      )}</div><div style="max-width: 600px; margin-left: auto; margin-right: auto;border-left: 2px solid #584a8f;padding-left: 20px; padding-right: 20px;border-right: 2px solid #584a8f;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#19182e;font-size: 13px; font-weight: normal; font-stretch: normal; font-style: normal; line-height: 1.54; letter-spacing: 0.5px;padding-top: 20px;"> Regards, </div><div style="max-width: 600px; margin-left: auto; margin-right: auto;padding-bottom: 20px;padding-left: 20px; padding-right: 20px;border-left: 2px solid #584a8f; border-right: 2px solid #584a8f; border-bottom: 2px solid #584a8f; border-bottom-right-radius: 10px; border-bottom-left-radius: 10px;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#19182e;font-size: 13px; font-weight: normal; font-stretch: normal; font-style: normal; line-height: 1.54; letter-spacing: 0.5px;"> ${
        this.$org.name
      } </div>`
      this.mailJson.message = message
      if (!isEmpty(this.$getProperty(quotation, 'workorder.subject'))) {
        this.mailJson.subject = `Quotation for ${this.$getProperty(
          quotation,
          'workorder.subject'
        )}`
      } else if (!isEmpty(quotation.subject)) {
        this.mailJson.subject = `${quotation.subject}`
      }
    },
    getFormattedCurrency(val) {
      val = this.$d3.format(',.2f')(val)
      let amount =
        this.$currency === '$' || this.$currency === '₹'
          ? `${this.$currency} ${val}`
          : `${val} ${this.$currency}`
      return amount
    },
  },
}
</script>
