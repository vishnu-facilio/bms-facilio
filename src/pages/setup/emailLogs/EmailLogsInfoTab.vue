<template>
  <div class="info-tab-page">
    <setup-loader v-if="isLoading">
      <template #setupLoading>
        <spinner :show="isLoading" size="80"></spinner>
      </template>
    </setup-loader>
    <div v-else class="info-tab mB100">
      <div class="fc-grey3-text14 bold">
        {{ $t('emailLogs.info_tab.basic_information') }}
      </div>
      <el-card
        shadow="never"
        class="fc-setup-summary-card heightInitial mB20 mT15"
      >
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="fc-grey2-text12 bold line-height20">
              {{ $t('common._common.from') }}
            </div>
            <div class="fc-black-14 text-left bold">
              {{ emailFromAddress || '---' }}
            </div>
          </el-col>
          <el-col :span="8">
            <div class="fc-grey2-text12 bold line-height20">
              {{ $t('common.products.to') }}
            </div>
            <div
              v-if="!canShowToolTip(receiverList)"
              class="fc-black-14 text-left bold .truncate-text"
            >
              {{ getFirstItemInList(receiverList) }}
            </div>
            <div v-else class="fc-black-14 text-left bold .truncate-text">
              {{ getFirstItemInList(receiverList) }}
              <el-tooltip placement="bottom">
                <div slot="content">
                  <div v-for="(receiver, index) in receiverList" :key="index">
                    <div v-if="index">{{ receiver || '---' }}</div>
                  </div>
                </div>
                <span class="plus-more">
                  {{ getListToolTipText(receiverList) }}
                </span>
              </el-tooltip>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="fc-grey2-text12 bold line-height20">
              {{ $t('emailLogs.info_tab.cc') }}
            </div>
            <div
              v-if="!canShowToolTip(ccList)"
              class="fc-black-14 text-left bold .truncate-text"
            >
              {{ getFirstItemInList(ccList) }}
            </div>
            <div v-else class="fc-black-14 text-left bold .truncate-text">
              {{ getFirstItemInList(ccList) }}
              <el-tooltip placement="bottom">
                <div slot="content">
                  <div v-for="(receiver, index) in ccList" :key="index">
                    <div v-if="index">{{ receiver || '---' }}</div>
                  </div>
                </div>
                <span class="plus-more">
                  {{ getListToolTipText(ccList) }}
                </span>
              </el-tooltip>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="fc-grey2-text12 bold line-height20">
              {{ $t('emailLogs.info_tab.bcc') }}
            </div>
            <div
              v-if="!canShowToolTip(bccList)"
              class="fc-black-14 text-left bold truncate-text"
            >
              {{ getFirstItemInList(bccList) }}
            </div>
            <div v-else class="fc-black-14 text-left bold truncate-text">
              {{ getFirstItemInList(bccList) }}
              <el-tooltip placement="bottom">
                <div slot="content">
                  <div v-for="(receiver, index) in ccList" :key="index">
                    <div v-if="index">{{ receiver || '---' }}</div>
                  </div>
                </div>
                <span class="plus-more">
                  {{ getListToolTipText(bccList) }}
                </span>
              </el-tooltip>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="fc-grey2-text12 bold line-height20">
              {{ $t('emailLogs.info_tab.created_time') }}
            </div>
            <div class="fc-black-14 text-left bold">
              {{ getFormatter(emailLog.sysCreatedTime) || '---' }}
            </div>
          </el-col>
        </el-row>
      </el-card>
      <div v-if="!isEmailBodyEmpty" class="fc-grey3-text14 bold">
        {{ $t('emailLogs.info_tab.email_body') }}
      </div>
      <el-card
        v-if="!isEmailBodyEmpty"
        shadow="never"
        class="fc-setup-summary-card heightInitial mB20 mT15"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <div v-if="isEmailBodyEmpty">
              <setup-empty>
                <template #emptyImage>
                  <inline-svg
                    src="svgs/copy2"
                    iconClass="icon icon-sm-md"
                  ></inline-svg>
                </template>
                <template #emptyHeading>
                  {{ $t('emailLogs.no_records_found') }}
                </template>
                <template #emptyDescription> </template>
              </setup-empty>
            </div>
            <div v-else class="email-logs-email-body">
              <FHtml :content="getHtmlTemplate()" />
            </div>
          </el-col>
        </el-row>
        <el-row v-if="canShowAttachments" class="attachments-row">
          <p class="files-attached">
            <el-tooltip placement="top-start">
              <div slot="content">
                <div
                  v-for="(attachment, index) in attachmentsList"
                  :key="index"
                >
                  {{ attachment.fileName || '---' }}
                </div>
              </div>
              <span>{{ getFilesCount() }}</span>
            </el-tooltip>
          </p>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { sanitize } from '@facilio/utils/sanitize'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import SetupLoader from 'pages/setup/components/SetupLoader'
import { isEmpty } from '@facilio/utils/validation'
import moment from 'moment-timezone'
import { FHtml } from '@facilio/ui/app'

export default {
  name: 'EmailLogsInfoTab',
  data() {
    return {
      isLoading: false,
      emailLog: {},
    }
  },
  props: ['loggerId'],
  computed: {
    moduleName() {
      return 'outgoingMailLogger'
    },
    attachmentsList() {
      let { emailLog } = this
      let { attachmentsList = [] } = emailLog || {}
      return attachmentsList
    },
    canShowAttachments() {
      let { isEmailBodyEmpty, attachmentsList } = this
      return !isEmailBodyEmpty && attachmentsList.length > 0
    },
    receiverList() {
      let { emailLog } = this
      let { receiverList } = emailLog || {}
      if (!isEmpty(receiverList)) {
        return receiverList
      }
      return []
    },
    ccList() {
      let { emailLog } = this
      let { ccList } = emailLog || {}
      if (!isEmpty(ccList)) {
        return ccList
      }
      return []
    },
    bccList() {
      let { emailLog } = this
      let { bccList } = emailLog || {}
      if (!isEmpty(bccList)) {
        return bccList
      }
      return []
    },
    isEmailBodyEmpty() {
      let { emailLog } = this
      let { htmlContent, textContent } = emailLog || {}
      return isEmpty(htmlContent) && isEmpty(textContent)
    },
    emailFromAddress() {
      let { emailLog } = this
      let { senderName, senderMail } = emailLog || {}
      if (!isEmpty(senderName)) {
        return `${senderName} <${senderMail}>`
      }
      return senderMail
    },
  },
  components: { SetupLoader, SetupEmpty, FHtml },
  created() {
    this.fetchEmailInfo()
  },
  methods: {
    canShowToolTip(dataList) {
      if (!isEmpty(dataList)) {
        return dataList.length > 1 ? true : false
      }
      return false
    },
    getFirstItemInList(dataList) {
      if (!isEmpty(dataList)) {
        return dataList[0]
      }
      return '---'
    },
    getListToolTipText(dataList) {
      if (!isEmpty(dataList) && dataList.length > 1) {
        return `+ ${dataList.length - 1} ${this.$t('common._common.more')}`
      }
      return '---'
    },
    getListFromString(dataList) {
      if (!isEmpty(dataList)) {
        return dataList.split(',')
      }
      return []
    },
    getFormatter(timeStamp) {
      return moment(timeStamp)
        .tz(this.$timezone)
        .format('DD MMM YYYY')
    },
    getFilesCount() {
      let { attachmentsList = [] } = this
      if (attachmentsList.length > 1) {
        return `${attachmentsList.length} ${this.$t(
          'emailLogs.files_attached'
        )}`
      }
      return `${attachmentsList.length} ${this.$t('emailLogs.file_attached')}`
    },
    getHtmlTemplate() {
      let { emailLog } = this
      let { htmlContent, textContent } = emailLog || {}
      if (!isEmpty(htmlContent)) {
        return sanitize(htmlContent)
      } else if (!isEmpty(textContent)) {
        return `<p>${textContent}</p>`
      } else {
        return ''
      }
    },
    deserializeData() {
      let { emailLog } = this || {}
      if (!isEmpty(emailLog)) {
        let { from = '', to = '', cc, bcc } = emailLog || {}
        let senderName = from.substring(
          from.indexOf('"') + 1,
          from.lastIndexOf('"')
        )
        let senderMail = from.substring(
          from.indexOf('<') + 1,
          from.lastIndexOf('>')
        )
        let receiverList = this.getListFromString(to)
        let ccList = this.getListFromString(cc)
        let bccList = this.getListFromString(bcc)
        this.$set(emailLog, 'senderName', senderName)
        this.$set(emailLog, 'senderMail', senderMail)
        this.$set(emailLog, 'receiverList', receiverList)
        this.$set(emailLog, 'ccList', ccList)
        this.$set(emailLog, 'bccList', bccList)
      }
    },
    async fetchEmailInfo() {
      this.isLoading = true
      let { moduleName, loggerId: id } = this
      let { outgoingMailLogger, error } = await API.fetchRecord(moduleName, {
        id,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.emailLog = outgoingMailLogger
        let { emailLog } = this
        let { subject } = emailLog || {}
        this.$emit('emailLogFetched', subject)
        this.deserializeData()
      }
      this.isLoading = false
    },
  },
}
</script>

<style lang="scss" scoped>
.info-tab {
  .files-attached {
    margin-bottom: 0px;
    font-size: 15px;
    color: #39b2c2;
  }
  .attachments-row {
    border-top: 1px dashed rgb(173, 173, 173);
  }
  .plus-more {
    font-size: 13px;
    color: #39b2c2;
    padding-left: 5px;
  }
  .attachments-container {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    .attachment {
      height: 30%;
      margin-right: 15px;
      padding: 10px 35px;
      background: #f7f8f9;
      border-radius: 5px;
      min-width: max-content;
      margin-bottom: 10px;
      margin-top: 10px;
      border: 1px solid #e1dfdf;
    }
  }
}
</style>
<style lang="scss">
.info-tab-page {
  .fc-setup-loader {
    height: calc(100vh - 280px);
  }
  .info-tab {
    .fc-setup-empty {
      height: 40vh;
      box-shadow: none;
    }
    .el-card {
      border: 0px;
    }
    .el-row {
      padding: 10px;
    }
    .email-logs-email-body {
      font-size: 15px;
      line-height: 2;
      a.facilio-masked-url-email-log {
        color: #dbcdff !important;
        text-shadow: 0 0 9px #280a7a;
      }
      table {
        border-collapse: separate;
      }
    }
  }
}
</style>
