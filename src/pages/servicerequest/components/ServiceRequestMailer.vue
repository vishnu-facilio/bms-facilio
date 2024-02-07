<template>
  <div>
    <spinner v-if="templateLoading" :show="templateLoading"></spinner>
    <div v-else class="mail-content-editor">
      <div
        v-if="!$helpers.isPortalUser()"
        class="mailer-slot border-bottom-mailer"
      >
        <el-select
          v-model="mailerModel.messageType"
          class="fc-select-suffix-hide fc-select-icon2 border-right1"
          placeholder=""
          @change="resetValue"
          popper-class="sr-email-thread"
        >
          <template slot="prefix">
            <InlineSvg
              :src="
                `svgs/service-requests/${getType(mailerModel.messageType).icon}`
              "
              iconClass="icon icon-md vertical-middle pointer fill-black"
            ></InlineSvg>
            <i class="el-icon-arrow-down mL10 pointer"></i>
          </template>
          <el-option
            v-for="item in types"
            :key="`type-${item.value}`"
            :label="''"
            :value="item.value"
          >
            <div class="flex-middle">
              <InlineSvg
                :src="`svgs/service-requests/${item.icon}`"
                iconClass="icon icon-md vertical-middle fill-black"
              ></InlineSvg>
              <div class="pR40 mL15">
                {{ item.label }}
              </div>
            </div>
          </el-option>
        </el-select>
        <div
          v-if="mailerModel.messageType === 1"
          class="pL10 flex-middle width100"
        >
          <div class="bold-text1 mR10">From:</div>
          <FieldLoader
            v-if="fromAddressloading"
            :isLoading="fromAddressloading"
          ></FieldLoader>
          <el-select
            v-else
            v-model="mailerModel.from"
            filterable
            class="width100 f-select-mailer"
          >
            <el-option
              v-for="(record, index) in fromAddressList"
              :key="`from-${index}`"
              :label="`${record.displayName || ''} (${record.email})`"
              :value="`${record.displayName || ''} <${record.email}>`"
              >{{ `${record.displayName || ''} (${record.email})` }}</el-option
            >
          </el-select>
        </div>
        <div v-else class="pL10">
          <el-select
            v-model="noteType"
            class="fc-select-suffix-hide fc-select-icon2"
            placeholder=""
            popper-class="sr-email-thread"
          >
            <template slot="prefix">
              <InlineSvg
                :src="`svgs/service-requests/${getNotesType()}`"
                iconClass="icon icon-md vertical-middle pointer fill-black"
                :key="`service-requests-${noteType}`"
              ></InlineSvg>
              <i class="el-icon-arrow-down mL10 pointer"></i>
            </template>
            <el-option
              v-for="item in notesType"
              :key="`note-${item.value}`"
              :label="''"
              :value="item.value"
            >
              <div class="flex-middle">
                <InlineSvg
                  :src="`svgs/service-requests/${item.icon}`"
                  iconClass="icon icon-md vertical-middle fill-black"
                ></InlineSvg>
                <div class="pR40 mL15">
                  {{ item.label }}
                </div>
              </div>
            </el-option>
          </el-select>
        </div>
      </div>
      <div
        v-if="!$helpers.isPortalUser()"
        class="mailer-slot border-bottom-mailer service-req-mailer"
      >
        <div
          class="sub-text bold"
          :class="mailerModel.messageType === 1 ? 'width35px' : 'width80px'"
        >
          {{ mailerModel.messageType === 1 ? `To:` : 'Notify To:' }}
        </div>
        <el-select
          v-model="mailerModel.to"
          multiple
          filterable
          default-first-option
          :disabled="mailerModel.messageType === 1"
          class="width100 f-select-mailer"
          @change="setToUser"
          :loading="loadingTo"
          remote
          :remote-method="remoteMethodTo"
        >
          <el-option
            value=""
            label="Type to Search"
            selected
            disabled
            v-if="$validation.isEmpty(userListTo)"
          ></el-option>
          <el-option
            v-else
            v-for="(user, index) in userListTo"
            :key="`to-${user.value}-${index}`"
            :label="user.label"
            :value="user.value"
          ></el-option>
        </el-select>
        <div
          v-if="mailerModel.messageType === 1 && !mailerModel.cc"
          @click="enableCC('cc')"
          class="mR10 cc-text bold"
        >
          Cc
        </div>
        <div
          v-if="mailerModel.messageType === 1 && !mailerModel.bcc"
          @click="enableCC('bcc')"
          class="cc-text bold"
        >
          Bcc
        </div>
      </div>
      <div
        v-if="mailerModel.cc"
        class="mailer-slot border-bottom-mailer service-req-mailer"
      >
        <div class="sub-text width35px bold">
          Cc:
        </div>
        <FieldLoader v-if="CCLoading" :isLoading="CCLoading"></FieldLoader>
        <el-select
          v-else
          v-model="mailerModel.cc"
          multiple
          filterable
          allow-create
          default-first-option
          class="width100 f-select-mailer"
          @change="setCCUser"
          :loading="loadingCC"
          remote
          :remote-method="remoteMethodCc"
        >
          <el-option
            value=""
            label="Type to Search"
            selected
            disabled
            v-if="$validation.isEmpty(userList)"
          ></el-option>
          <el-option
            v-else
            v-for="(user, index) in userList"
            :key="`cc-${user.value}-${index}`"
            :label="user.label"
            :value="user.value"
          ></el-option>
        </el-select>
        <div
          v-if="!$validation.isEmpty(mailerModel.cc)"
          @click="disableCC('cc')"
          class="cc-text bold"
        >
          Clear
        </div>
      </div>
      <div
        v-if="mailerModel.bcc"
        class="mailer-slot border-bottom-mailer service-req-mailer"
      >
        <div class="sub-text width35px bold">
          Bcc:
        </div>
        <FieldLoader v-if="CCLoading" :isLoading="CCLoading"></FieldLoader>
        <el-select
          v-else
          v-model="mailerModel.bcc"
          multiple
          filterable
          allow-create
          default-first-option
          class="width100 f-select-mailer"
          @change="setBCCUser"
          :loading="loadingBcc"
          remote
          :remote-method="remoteMethodBcc"
        >
          <el-option
            value=""
            label="Type to Search"
            selected
            disabled
            v-if="$validation.isEmpty(userListBcc)"
          ></el-option>
          <el-option
            v-else
            v-for="(user, index) in userListBcc"
            :key="`bcc-${user.value}-${index}`"
            :label="user.label"
            :value="user.value"
          ></el-option>
        </el-select>
        <div
          v-if="!$validation.isEmpty(mailerModel.bcc)"
          @click="disableCC('bcc')"
          class="cc-text bold"
        >
          Clear
        </div>
      </div>
      <div
        class="fc-rich-text-editor service-req-rich-text-editor"
        :class="hideFormatter && 'hide-toolbar'"
      >
        <RichTextArea
          v-model="mailerModel.htmlContent"
          :secondaryToolbar="true"
          :istoolbarPositionBottom="true"
          :isMoreOption="false"
          :iconColor="'#2e2e49'"
          :customToolOrder="customToolOrder"
        ></RichTextArea>
        <div v-if="appendContent" class="editor-bq d-flex pT10 pL25 mB15">
          <button @click="setPrevMailContent()" class="facilio-bq"></button>
          <i
            class="el-icon-close pointer mL10"
            @click="appendContent = false"
          ></i>
        </div>
      </div>
      <div
        v-if="fileSaving"
        class="progress-style mailer-attachments-list d-flex mailer-border"
      >
        <div class="spinner">
          <span class="mR5 uploading-txt">{{
            $t('common._common.uploading_file')
          }}</span>
          <div class="bounce1"></div>
          <div class="bounce2"></div>
          <div class="bounce3"></div>
        </div>
      </div>
      <div
        v-if="!$validation.isEmpty(attachments)"
        class="mailer-attachments-list d-flex mailer-border"
      >
        <div
          class="fc-mailer-attachment-row d-flex attachment-border"
          v-for="(attachment, index) in attachmentsDetails"
          :key="`attachment-${index}`"
        >
          <template v-if="!$validation.isEmpty(attachment)">
            <div v-if="!$validation.isEmpty(attachment.className)">
              <div class="icon-document" :class="attachment.className">
                <span class="txt">{{ attachment.displayName }}</span>
              </div>
            </div>
            <div v-else>
              <div class="icon-document gray"><span class="txt">/</span></div>
            </div>
            <div class="attached-files">
              <div
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                  content: attachment.name,
                }"
                class="mB10 attachment-label attached-file-txt"
              >
                {{ attachment.name }}
              </div>
            </div>
            <div class="attached-file-size attached-file-txt">
              {{ attachment.bytes }}
            </div>
            <i
              class="el-icon-delete mailer-attachment-icon fc-delete-icon"
              @click="deleteAttachment(index)"
            ></i>
          </template>
        </div>
      </div>

      <div class="action-container">
        <el-button
          @click="addEMail()"
          :loading="saving"
          :disabled="saving"
          class="email-thread-primary-btn"
        >
          {{ getType(mailerModel.messageType).label }}
        </el-button>
        <el-button
          @click="closeMailer()"
          :disabled="saving"
          class="email-thread-secondary-btn mL10"
        >
          Cancel
        </el-button>
        <!-- <div
          @click="toggleFormatter()"
          class="svg-container mL20"
          :class="!hideFormatter && 'selected'"
        >
          <InlineSvg
            :src="`svgs/service-requests/format`"
            iconClass="icon icon-md vertical-middle"
          ></InlineSvg>
        </div> -->
        <div
          @change="addAttachment"
          v-tippy="{
            placement: 'top',
            animation: 'shift-away',
            arrow: true,
            content: 'Attach files <10MB',
          }"
          class="svg-container mL20 mailer-attachment"
        >
          <div class="pointer">
            <form enctype="multipart/form-data">
              <input
                class="input-file-mailer"
                type="file"
                id="file-attachment"
                multiple
              />
              <InlineSvg
                :src="`svgs/service-requests/attach`"
                iconClass="icon icon-md vertical-middle pointer"
              ></InlineSvg>
            </form>
          </div>
        </div>
      </div>
      <input
        class="hide"
        ref="fcImageInput"
        type="file"
        accept="image/*"
        @change="uploadImage"
      />
    </div>
  </div>
</template>
<script>
const editorFontSize = ['10px', '16px', '20px', '32px']
import { API } from '@facilio/api'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import { prettyBytes } from '@facilio/utils/filters'
import FieldLoader from '@/forms/FieldLoader'
import ImageResize from 'quill-image-resize'
import { sanitize } from '@facilio/utils/sanitize'
import debounce from 'lodash/debounce'
import { getApp } from '@facilio/router'
import RichTextArea from '@/forms/RichTextArea'

const emailRegex = /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i
const ATTACHMENT_DETAILS = [
  { name: 'pdf', displayName: 'PDF', className: 'red' },
  { name: 'docx', displayName: 'DOC', className: 'blue' },
  { name: 'xl', displayName: 'XLS', className: 'green' },
  { name: 'ppt', displayName: 'PPT', className: 'orange' },
  { name: 'img', displayName: 'IMG', className: 'gray' },
]

const customToolOrder = [
  'undo',
  'redo',
  'separator',
  'fontfamily',
  'fontsize',
  'separator',
  'bold',
  'italic',
  'underline',
  'separator',
  'setColor',
  'highlight',
  'strike',
  'separator',
  'orderedList',
  'bulletList',
  'separator',
  'textalign',
  'indent-less',
  'indent-more',
  'separator',
  'blockquote',
  'line',
  'separator',
  'link',
  'table',
  'separator',
  'addimage',
]

export default {
  props: ['visibility', 'messageType', 'details', 'previousMail'],
  components: {
    FieldLoader,
    RichTextArea,
  },
  data() {
    return {
      types: [
        {
          icon: 'reply',
          label: 'Reply',
          value: 1,
        },
        {
          icon: 'notes',
          label: 'Comment',
          value: 3,
        },
      ],
      notesType: [
        {
          icon: 'public',
          label: 'Public Note',
          value: 2,
        },
        {
          icon: 'locked',
          label: 'Private Note',
          value: 3,
        },
      ],
      noteType: 3,
      quillEditorConfig: {
        toolbar: [
          [
            'bold',
            'italic',
            'underline',
            'link',
            { color: [] },
            { background: [] },
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
              size: editorFontSize,
            },
            {
              align: ['', 'right', 'center', 'justify'],
            },
            {
              header: [1, 2, 3, 4, 5, 6, false],
            },
          ],
          ['image', 'blockquote', 'code-block', 'clean'],
        ],
        imageResize: ['Resize'],
      },
      mailerModel: {},
      saving: false,
      fileSaving: false,
      hideFormatter: false,
      appendContent: false,
      attachments: [],
      uploadedAttachments: [],
      imageUploads: [],
      fromAddressloading: false,
      fromAddressList: [],
      quillInstance: null,
      signatureContent: null,
      users: [],
      bccUsers: [],
      toUsers: [],
      loadingCC: false,
      loadingBcc: false,
      loadingTo: false,
      CCLoading: false,
      ccSelected: false,
      bccSelected: false,
      toSelected: false,
      templateLoading: false,
      customToolOrder,
    }
  },
  computed: {
    ...mapState({
      account: state => state.account || {},
    }),
    appId() {
      let { id } = getApp() || {}
      return id
    },
    userList() {
      let { users, ccSelected } = this
      if (!ccSelected) {
        return users.map(user => {
          let { name, email } = user || {}
          let label = `${name || ''} (${email})`.trim()
          let value = `${isEmpty(name) ? '' : name} ${
            isEmpty(name) ? '' : '<'
          }${email}${isEmpty(name) ? '' : '>'}`.trim()

          return { label, value }
        })
      }
      return []
    },
    attachmentsDetails() {
      let { attachments } = this || {}
      return attachments.map(attachment => {
        let extension = this.getExtension(attachment.name)
        let properties = ATTACHMENT_DETAILS.find(
          details => details.name == extension
        )
        let { displayName, className } = properties || {}
        return { ...attachment, displayName, className }
      })
    },
    userListBcc() {
      let { bccUsers, bccSelected } = this

      if (!bccSelected) {
        return bccUsers.map(user => {
          let { name, email } = user || {}
          let label = `${name || ''} (${email})`.trim()
          let value = `${isEmpty(name) ? '' : name} ${
            isEmpty(name) ? '' : '<'
          }${email}${isEmpty(name) ? '' : '>'}`.trim()

          return { label, value }
        })
      }
      return []
    },
    userListTo() {
      let { toUsers, toSelected } = this

      if (!toSelected) {
        return toUsers.map(user => {
          let { name, email } = user || {}
          let label = `${name || ''} (${email})`.trim()
          let value = `${isEmpty(name) ? '' : name} ${
            isEmpty(name) ? '' : '<'
          }${email}${isEmpty(name) ? '' : '>'}`.trim()

          return { label, value }
        })
      }
      return []
    },
    isSignatureFileEmpty() {
      let { user } = this.account || {}
      let { signatureFileId } = user || {}
      return isEmpty(signatureFileId)
    },
  },
  async created() {
    this.templateLoading = true
    await this.getSignatureContent()
    this.initModel()
    this.remoteMethodTo = debounce(this.getPeopleTo, 500)
    this.remoteMethodCc = debounce(this.getPeople, 500)
    this.remoteMethodBcc = debounce(this.getPeopleBcc, 500)
    this.templateLoading = false
  },
  methods: {
    initModel() {
      this.mailerModel = this.$helpers.cloneObject(
        this.$constants.mailerDefaults
      )
      this.mailerModel.messageType = this.messageType
      if (this.messageType === 1) {
        this.setForReply(this.mailerModel)
        if (!isEmpty(this.previousMail)) {
          this.appendContent = true
        }
        if (isEmpty(this.mailerModel.from)) {
          this.loadFromAddressList()
        }
        if (isEmpty(this.mailerModel.cc)) {
          this.EmailAddressList()
        }
      } else {
        this.setFromForUser()
        if (this.$helpers.isPortalUser()) {
          this.noteType = 2
        }
      }
      this.mailerModel.subject = this.details?.subject
      if (!isEmpty(this.signatureContent)) {
        this.mailerModel.htmlContent = sanitize(
          '<br><br><br>' + this.signatureContent
        )
      }
    },
    async getSignatureContent() {
      if (!this.isSignatureFileEmpty && this.messageType == 1) {
        let { error, data } = await API.get(
          '/v2/application/users/getSignature'
        )

        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.signatureContent = this.$getProperty(
            data,
            'signature.signatureContent',
            ''
          )
        }
      }
    },
    async getPeopleTo(searchText) {
      let { details, appId } = this
      let { requester } = details || {}
      let requesterMail = this.$getProperty(requester, 'email', '')

      this.loadingTo = true
      if (!isEmpty(searchText)) {
        this.toSelected = true
        let params = {
          search: !isEmpty(searchText) ? searchText : null,
          inviteAcceptStatus: true,
          appId,
        }
        let { data, error } = await API.get('v2/application/users/list', params)

        if (error) {
          let { message } = error
          this.$message.error(
            message ||
              this.$t(`home.service_request.error_occured_from_address_list`)
          )
        } else {
          let { users } = data || {}
          this.toUsers = []
          this.toUsers = (users || []).map(user => ({
            name: user.name,
            email: user.email,
          }))
        }
        let requesterAvailable = this.toUsers.some(
          user => user.email === requesterMail
        )
        if (!requesterAvailable) {
          let tempUserEmail = this.$getProperty(requester, 'email', '')
          if (tempUserEmail.includes(searchText)) {
            this.toUsers = [
              ...this.toUsers,
              { name: requester.name, email: requester.email },
            ]
          }
        }
        this.toSelected = false
      }
      this.loadingTo = false
    },
    async getPeopleBcc(searchText) {
      this.loadingBcc = true
      if (!isEmpty(searchText)) {
        this.bccSelected = true
        let params = {
          genericSearch: !isEmpty(searchText) ? searchText : null,
        }
        let { data, error } = await API.get('/v2/people/list', params)

        if (error) {
          let { message } = error
          this.$message.error(
            message ||
              this.$t(`home.service_request.error_occured_from_address_list`)
          )
        } else {
          let { people } = data || {}
          this.bccUsers = []
          this.bccUsers = (people || []).map(user => ({
            name: user.name,
            email: user.email,
          }))
        }
        this.bccSelected = false
      }
      this.loadingBcc = false
    },
    async getPeople(searchText) {
      this.loadingCC = true
      if (!isEmpty(searchText)) {
        this.ccSelected = true
        let params = {
          genericSearch: !isEmpty(searchText) ? searchText : null,
        }
        let { data, error } = await API.get('/v2/people/list', params)

        if (error) {
          let { message } = error
          this.$message.error(
            message ||
              this.$t(`home.service_request.error_occured_from_address_list`)
          )
        } else {
          let { people } = data || {}
          this.users = []
          this.users = (people || []).map(user => ({
            name: user.name,
            email: user.email,
          }))
        }
        this.ccSelected = false
      }
      this.loadingCC = false
    },
    getEmailSubString(email) {
      return (
        email.substring(email.indexOf('<') + 1, email.lastIndexOf('>')) || email
      )
    },
    removeDuplicates(emailArray) {
      return emailArray.filter((email, index, copyEmailArray) => {
        let emailSubstring = this.getEmailSubString(email)
        let firstIndex = copyEmailArray.findIndex(copyEmail => {
          let copyEmailSubstring = this.getEmailSubString(copyEmail)
          return emailSubstring === copyEmailSubstring
        })
        return firstIndex === index
      })
    },
    validateUsers(userArray) {
      userArray.forEach((email, index) => {
        email = email.toLowerCase()
        let emailSubstring = this.getEmailSubString(email)

        if (!emailRegex.test(emailSubstring)) {
          this.$message.warning(this.$t(`home.service_request.invalid_email`))
          userArray.splice(index, 1)
        }
      })
      return userArray
    },
    setCCUser(cc) {
      this.mailerModel.cc = this.validateUsers(this.removeDuplicates(cc))
      this.ccSelected = true
    },
    setBCCUser(bcc) {
      this.mailerModel.bcc = this.validateUsers(this.removeDuplicates(bcc))
      this.bccSelected = true
    },
    setToUser(to) {
      this.mailerModel.to = this.validateUsers(this.removeDuplicates(to))
      this.toSelected = true
    },
    async EmailAddressList() {
      this.CCLoading = true
      let emailToOmit = []
      let { data, error } = await API.get(`/setup/emailsettings`)

      if (error) {
        let { message } = error
        this.$message.error(
          message || 'Error Occured while fetching From address list'
        )
      } else {
        let supportEmails = this.$getProperty(data, 'supportEmails', [])

        emailToOmit = (supportEmails || []).reduce((emails, curEmail) => {
          let { actualEmail, fwdEmail } = curEmail || {}
          emails.push(actualEmail)
          emails.push(fwdEmail)
          return emails
        }, [])
      }

      let mailCC = this.$getProperty(this.previousMail, 'cc', '')
        .split(',')
        .filter(mail => !isEmpty(mail))
      let mailBCC = this.$getProperty(this.previousMail, 'bcc', '')
        .split(',')
        .filter(mail => !isEmpty(mail))
      let emailToModuleDataRecord = this.$getProperty(
        this.details,
        'emailToModuleDataRecord.to',
        ''
      )
        .split(',')
        .filter(mail => !isEmpty(mail))

      mailCC = (mailCC || []).filter(
        mail => !emailToModuleDataRecord.includes(mail)
      )
      mailBCC = (mailBCC || []).filter(
        mail => !emailToModuleDataRecord.includes(mail)
      )

      const emailFilter = mail => {
        let matchedemail = mail.match(/<(.+?)>/gm)
        matchedemail =
          matchedemail !== null ? matchedemail[0].replaceAll(/<|>/gm, '') : mail
        return !emailToOmit.some(omittedEmail => omittedEmail === matchedemail)
      }
      this.mailerModel.cc = mailCC.filter(emailFilter)
      this.mailerModel.bcc = mailBCC.filter(emailFilter)
      this.CCLoading = false
    },
    setFromForUser() {
      let user = this.$account?.user || this.$portaluser
      this.mailerModel.from = `${user?.name || ''} <${user?.email}>`
    },
    getType(type) {
      return this.types.find(t => t.value === type)
    },
    getNotesType() {
      let { noteType: type, notesType = [] } = this
      let typeObj = notesType.find(notes => notes.value === type)
      let { icon } = typeObj || {}
      return icon
    },
    async loadFromAddressList() {
      this.fromAddressloading = true
      let params = {
        recordId: this.details?.id,
        moduleId: this.details?.moduleId,
      }
      let { data = {}, error } = await API.post(
        `/v3/mailmessage/getfromEmailForEmailThreadingReply`,
        params
      )
      if (error) {
        let { message } = error
        this.$message.error(
          message || 'Error Occured while fetching From address list'
        )
      } else {
        let { emailFromAddress = [] } = data || {}
        this.fromAddressList = emailFromAddress || []
        this.setFromAddress()
      }
      this.fromAddressloading = false
    },
    setFromAddress() {
      if (!isEmpty(this.fromAddressList) && isArray(this.fromAddressList)) {
        let record = this.fromAddressList[0]
        this.mailerModel.from = `${record.displayName || ''} <${record.email}>`
      } else {
        this.mailerModel.from = 'noreply@facilio.com'
      }
    },
    async closeMailer() {
      let value = await this.$dialog.confirm({
        title: 'Discard Changes',
        message:
          'You have unsaved changes and your data will be lost. Are you sure you want to discard?',
        rbDanger: true,
        rbLabel: 'DISCARD',
      })

      if (value) {
        this.$emit('close')
      }
    },
    async addEMail() {
      this.saving = true
      let mailerModel = this.getFormattedMailerModel()
      // eslint-disable-next-line @facilio/no-http
      await this.$http
        .post('/v3/modules/emailConversationThreading', { data: mailerModel })
        .then(response => {
          let { emailConversationThreading } = response.data.data || {}
          this.$emit('saved', emailConversationThreading)
        })
        .catch(error => {
          if (error.status === 403) {
            this.$message.error(this.$t('serviceRequest.error.403'))
          } else {
            this.$message.error('Error occurred')
          }
        })
      this.saving = false
    },
    getFormattedMailerModel() {
      let data = this.$helpers.cloneObject(this.mailerModel)
      if (!isEmpty(data.to) && isArray(data.to)) {
        data.to = data.to.join()
      } else {
        data.to = null
      }
      if (!isEmpty(data.cc) && isArray(data.cc)) {
        data.cc = data.cc.join()
      } else {
        data.cc = null
      }
      if (!isEmpty(data.bcc) && isArray(data.bcc)) {
        data.bcc = data.bcc.join()
      } else {
        data.bcc = null
      }
      if (data.messageType === 3) {
        data.messageType = this.noteType
      } else if (data.messageType === 1 && this.appendContent) {
        data.htmlContent = this.appendPreviousMail(data.htmlContent, true)
      }
      // Dont change the order
      this.replaceAllImgSrcWithCid(data)
      if (!isEmpty(this.uploadedAttachments)) {
        this.$setProperty(
          data,
          'emailConversationThreadingattachments',
          this.uploadedAttachments
        )
      }

      data.dataModuleId = this.details?.moduleId
      data.recordId = this.details?.id
      data.siteId = this.details?.siteId
      data.fromPeople = {
        id: this.$account?.user?.peopleId || this.$portaluser?.peopleId || null,
      }
      if (this.$helpers.isPortalUser()) {
        data.fromType = 1
      } else {
        data.fromType = 2
      }
      return data
    },
    replaceAllImgSrcWithCid(data) {
      let mailRegEx = /src=['"](?:[^"'\/]*\/)*([^'"]+)['"] data-id=['"]([^'"]+)['"]/
      // adding g for global search
      let regExExecValues = this.$helpers.execRegex(
        data.htmlContent,
        new RegExp(mailRegEx, 'g')
      )
      regExExecValues.forEach(value => {
        let cid = value[2]
        let attachment = this.imageUploads.find(
          at => at.contentId === String(cid)
        )
        if (isEmpty(attachment)) {
          console.warn('No matching attachments found for content id:', cid)
        } else {
          // replace first occurrence
          data.htmlContent = data.htmlContent.replace(
            mailRegEx,
            `src="cid:${cid}"`
          )
          this.uploadedAttachments.push(attachment)
        }
      })
    },
    enableCC(type = 'cc') {
      this.mailerModel[type] = []
    },
    disableCC(type = 'cc') {
      this.mailerModel[type] = null
    },
    resetValue(value) {
      this.mailerModel = this.$helpers.cloneObject(
        this.$constants.mailerDefaults
      )
      this.mailerModel.messageType = value
      if (value === 1) {
        this.setForReply(this.mailerModel)
        if (!isEmpty(this.previousMail)) {
          this.appendContent = true
        }
        if (isEmpty(this.fromAddressList)) {
          this.loadFromAddressList()
        } else {
          this.setFromAddress()
        }
      } else {
        this.setFromForUser()
        this.appendContent = false
      }
    },
    setForReply(model) {
      model.to = [
        `${this.details?.requester?.name || ''} <${
          this.details?.requester?.email
        }>` || 'support@facilio.com',
      ]
      model.cc = []
    },
    toggleFormatter() {
      this.hideFormatter = !this.hideFormatter
    },
    setPrevMailContent() {
      this.mailerModel.htmlContent = this.appendPreviousMail(
        this.mailerModel.htmlContent,
        true
      )
      this.appendContent = false
    },
    appendPreviousMail(htmlContent, attachInlineImgs = false) {
      let { previousMail } = this
      let from = previousMail?.from || 'support@facilio.com'

      let { name, email } = this.$helpers.getNameAndEMail(from)
      let content = previousMail?.htmlContent || ''
      htmlContent += `<br><br><p>`
      htmlContent += `<blockquote style="margin:0px 0px 0px 6px;border-left:1px solid rgb(204,204,204);padding-left:8px;font-size: 15px">`
      htmlContent += `<p>${this.$helpers
        .getOrgMoment(previousMail.sysCreatedTime)
        .format('ddd, MMMM Do YYYY, h:mm:ss a')} ${
        name ? name : ''
      } &lt;<a href="mailto:${email}" rel="noreferrer" target="_blank">${email}</a>&gt; wrote:</p>`
      htmlContent += `${content}</blockquote></p>`
      let inlineAttachemnts =
        previousMail?.attachmentsList &&
        previousMail?.attachmentsList.filter(at => at.type === 2)
      if (attachInlineImgs && !isEmpty(inlineAttachemnts)) {
        let attachments = inlineAttachemnts.map(record => {
          return {
            fileId: record?.fileId,
            type: record?.type,
            contentId: record?.contentId,
          }
        })
        this.imageUploads.push(...attachments)
      }
      return sanitize(htmlContent)
    },

    async addAttachment(event) {
      let filesList = event?.target?.files || []
      let files =
        [...filesList].filter(file => {
          let { size, name } = file
          if (size > 10485760) {
            this.$message.error(
              name + ' exceeded size limit (Max File Size is 10MB)'
            )
            return false
          }
          return true
        }) || []
      this.saving = true
      this.fileSaving = true
      if (!isEmpty(files)) {
        await this.uploadFiles(files)
      }
      this.saving = false
      this.fileSaving = false
    },
    async uploadFiles(files) {
      let { error, ids } = await API.uploadFiles(files)
      if (error) {
        let { message } = error
        this.$message.error(message || 'Error occured')
      } else if (ids) {
        ids.forEach(id => {
          this.uploadedAttachments.push({
            fileId: id,
            type: 1,
            createdTime: Date.now(),
          })
        })
        files.forEach(file => {
          let { name, size, type } = file
          let bytes = prettyBytes(size)
          // Adding only external attachments
          this.attachments.push({
            name,
            size,
            bytes,
            type,
          })
        })
      }
    },
    deleteAttachment(index) {
      let { uploadedAttachments, attachments } = this
      uploadedAttachments.splice(index, 1)
      attachments.splice(index, 1)
    },
    imageHandler() {
      this.$refs.fcImageInput.click()
    },
    async uploadImage(event) {
      let file = event.target.files[0]
      if (file?.size > 10485760) {
        this.$message.error(
          file?.name + ' exceeded size limit (Max File Size is 10MB)'
        )
        return
      }
      let { error, ids } = await API.uploadFiles([file])
      if (error) {
        let { message } = error
        this.$message.error(message || 'Error occured')
      } else if (ids) {
        let [id] = ids
        this.imageUploads.push({
          fileId: id,
          type: 2,
          contentId: String(id),
          createdTime: Date.now(),
        })
        let imagePreviewUrl = this.$helpers.getImagePreviewUrl(id)
        if (this.quillInstance) {
          let currentIndex = this.quillInstance.selection.lastRange.index
          this.quillInstance.insertEmbed(currentIndex, 'imageBlot', {
            src: imagePreviewUrl,
            dataId: id,
            alt: file?.name,
          })
          this.quillInstance.setSelection(currentIndex + 1, 0)
        }
      }
      event.target.value = ''
    },
    registerCustomModules(Quill) {
      let Size = Quill.import('attributors/style/size')
      Size.whitelist = editorFontSize
      Quill.register(Size, true)
      Quill.register('modules/imageResize', ImageResize)
      let InlineBlot = Quill.import('blots/block')
      class ImageBlot extends InlineBlot {
        static create(data) {
          let node = super.create(data)
          node.setAttribute('src', data.src)
          node.setAttribute('data-id', data.dataId)
          node.setAttribute('alt', data.alt)
          return node
        }
        static value(domNode) {
          let { src, dataId, alt } = domNode.dataset
          return { src, dataId, alt }
        }
      }
      ImageBlot.blotName = 'imageBlot'
      ImageBlot.tagName = 'img'
      Quill.register({ 'formats/imageBlot': ImageBlot })
    },
    setQuillInstance(quill) {
      this.quillInstance = quill
      let toolbar = this.quillInstance.getModule('toolbar')
      toolbar && toolbar.addHandler('image', this.imageHandler)
    },
    getExtension(fileName) {
      let name = fileName.split('.')
      let format = name[1]
      if (format == 'pdf') {
        return 'pdf'
      }
      if (['jpg', 'jpeg', 'png', 'svg'].includes(format)) {
        return 'img'
      }
      if (['docx', 'doc', 'docm', 'dot', 'dotx'].includes(format)) {
        return 'docx'
      }
      if (
        ['xls', 'xlsx', 'xl', 'xll', 'xlm', 'xlsm', 'xlsx'].includes(format)
      ) {
        return 'xl'
      }
      if (['ppt', 'pptx'].includes(format)) {
        return 'ppt'
      } else {
        return 'none'
      }
    },
  },
}
</script>
<style lang="scss">
.progress-style {
  padding: 10px;
  height: 50px;
  background-color: 0 2px 4px 0 rgba(238, 236, 236, 0.5);
  .spinner {
    text-align: center;
    .uploading-txt {
      font-size: 18px;
      font-weight: 700;
    }
  }

  .spinner > div {
    height: 10px;
    width: 10px;
    margin-right: 5px;
    background-color: #3ab2c1;
    border-radius: 100%;
    display: inline-block;
    -webkit-animation: sk-bouncedelay 1.4s infinite ease-in-out both;
    animation: sk-bouncedelay 1.4s infinite ease-in-out both;
  }

  .spinner .bounce1 {
    -webkit-animation-delay: -0.32s;
    animation-delay: -0.32s;
  }

  .spinner .bounce2 {
    -webkit-animation-delay: -0.16s;
    animation-delay: -0.16s;
  }

  @-webkit-keyframes sk-bouncedelay {
    0%,
    80%,
    100% {
      -webkit-transform: scale(0);
    }
    40% {
      -webkit-transform: scale(1);
    }
  }

  @keyframes sk-bouncedelay {
    0%,
    80%,
    100% {
      -webkit-transform: scale(0);
      transform: scale(0);
    }
    40% {
      -webkit-transform: scale(1);
      transform: scale(1);
    }
  }
}
.icon-document {
  height: 16px;
  width: 16px;
  border-radius: 3px;
  border-start-end-radius: 8px;
  padding-bottom: 18px;
  margin: -2px 5px 0px 5px;
}
.red {
  background-color: orangered;
}
.blue {
  background-color: rgb(0, 110, 255);
}
.green {
  background-color: rgb(3, 105, 3);
}
.orange {
  background-color: rgb(213, 139, 0);
}
.gray {
  background-color: #6c6d6e;
}

.txt {
  color: #fff !important;
  font-size: 6px;
  font-weight: bold;
  text-align: center;
  align-items: center;
  top: 50%;
  left: 50%;
  padding-left: 2px;
  padding-bottom: 10px;
}
.attachment-border {
  border-radius: 14px !important;
  height: 40px;
  .attached-file-txt {
    font-size: 14px !important;
    color: #686b70;
  }
}
.service-req-rich-text-editor {
  .richtext-editor-ui {
    border: none;
    .richtext-toolbar {
      border-radius: 5px;
      height: 50px;
    }
  }
  .richtext-toolbar-secondray {
    box-shadow: 0 2px 6px 0 rgba(87, 85, 85, 0.5);
    margin: 0px 40px 20px 20px !important;
  }
  .richtext-table-btn-popover {
    .rc-font-family {
      font-size: 14px;
      color: #2e2e49;
    }
  }
  .popover-richtext-extra-options {
    min-width: 100px;
  }
}
.service-req-mailer {
  .f-select-mailer {
    .el-select__tags {
      .el-select__input {
        margin-left: 0 !important;
      }
    }
  }
}
</style>
