<template>
  <div
    class="custom-module-overview"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header p20">
        <div class="custom-module-details">
          <div class="d-flex flex-middle align-center">
            <div class="mL5">
              <div class="custom-module-id">
                <i
                  v-if="$account.portalInfo"
                  class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                  content="back"
                  arrow
                  v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                  @click="back"
                ></i>
                #{{ record && record.id }}
              </div>

              <div class="custom-module-name d-flex mT4">
                <!-- lock icon -->
                <i
                  v-if="record.isRecordLocked()"
                  class="fa fa-lock locked-wo"
                  data-arrow="true"
                  :title="$t('common._common.locked_state')"
                  v-tippy
                ></i>
                <div class="d-flex max-width300px">
                  <el-tooltip
                    placement="bottom"
                    effect="dark"
                    :content="record[mainFieldKey]"
                  >
                    <span class="whitespace-pre-wrap custom-header">{{
                      record[mainFieldKey]
                    }}</span>
                  </el-tooltip>
                </div>
                <div
                  v-if="
                    record.isStateFlowEnabled() && record.currentModuleState()
                  "
                  class="fc-badge text-uppercase inline vertical-middle mL15"
                >
                  {{ record.currentModuleState() }}
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="marginL-auto flex-middle">
          <CustomButton
            class="pR10"
            :record="record"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="refreshData()"
            @onError="() => {}"
          />
          <div class="marginL-auto flex-middle" v-if="!record.isDiscarded">
            <iframe
              v-if="downloadUrl"
              :src="downloadUrl"
              style="display: none;"
            ></iframe>
            <template v-if="record.isStateFlowEnabled()">
              <TransitionButtons
                class="mR10"
                :key="`${record.id}transitions`"
                :moduleName="moduleName"
                :record="record"
                :disabled="record.isApprovalEnabled()"
                buttonClass="asset-el-btn"
                @currentState="() => {}"
                @transitionSuccess="refreshData()"
                @transitionFailure="() => {}"
              ></TransitionButtons>
            </template>
            <portal
              to="pagebuilder-sticky-top"
              v-if="record.isApprovalEnabled()"
            >
              <ApprovalBar
                :moduleName="moduleName"
                :key="record.id + 'approval-bar'"
                :record="record"
                :hideApprovers="shouldHideApprovers"
                @onSuccess="refreshData()"
                @onFailure="() => {}"
                class="approval-bar-shadow"
              ></ApprovalBar>
            </portal>
            <el-button
              class="fc__add__btn mR15"
              :loading="isButtonLoading"
              v-if="!record.isRfqFinalized && !record.isApprovalEnabled()"
              @click="clickAction('publish_rfq')"
            >
              {{ $t('common.inventory.publish_rfq') }}
            </el-button>
            <el-button
              class="fc__add__btn mR15"
              :loading="isButtonLoading"
              v-if="showQuotesReceived"
              @click="clickAction('receive_quote')"
            >
              {{ $t('common.inventory.close_submission') }}
            </el-button>
            <el-button
              class="fc__add__btn mR15"
              :loading="isButtonLoading"
              v-if="checkIsQuoteNotAwarded"
              @click="clickAction('award_vendors')"
            >
              {{ $t('common.inventory.assess_and_award') }}
            </el-button>
            <el-button
              class="fc__add__btn mR15"
              :loading="isButtonLoading"
              v-if="checkIsQuoteAwarded"
              @click="clickAction('award_vendors')"
              >{{ $t('common.inventory.summary') }}</el-button
            >
            <el-button-group class="fc-group-btn2 flex-middle">
              <el-button type="primary" @click="downloadPdf">
                <inline-svg
                  src="svgs/download2"
                  iconClass="icon vertical-middle icon-sm fill-grey2"
                ></inline-svg>
              </el-button>
              <el-button type="primary" @click="openPrintPreview">
                <inline-svg
                  src="svgs/print2"
                  iconClass="icon vertical-middle icon-sm fill-grey2"
                ></inline-svg>
              </el-button>
            </el-button-group>
            <el-dropdown
              v-if="showDropDown"
              class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
              trigger="click"
              @command="action => summaryDropDownAction(action)"
            >
              <span class="el-dropdown-link">
                <inline-svg
                  src="svgs/menu"
                  class="vertical-middle"
                  iconClass="icon icon-md"
                >
                </inline-svg>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  v-if="showEdit"
                  :key="1"
                  :command="'edit_rfq'"
                  >{{ $t('common.header.edit_rfq') }}</el-dropdown-item
                >
                <el-dropdown-item :key="2" :command="'discard_rfq'">
                  {{ $t('common.header.discard_rfq') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </div>

      <Page
        :key="record.id"
        :module="moduleName"
        :id="record.id"
        :details="record"
        :primaryFields="primaryFields"
        :notesModuleName="notesModuleName"
        :isV3Api="true"
        :attachmentsModuleName="attachmentsModuleName"
      ></Page>
    </template>
    <AwardVendors
      v-if="showAwardVendorsDialog"
      :record="record"
      :moduleName="moduleName"
      :onClose="() => (showAwardVendorsDialog = false)"
      @saved="loadRecord(true)"
    >
    </AwardVendors>
    <portal
      to="pagebuilder-fixed-top"
      v-if="$getProperty(record, 'isDiscarded', false)"
    >
      <div
        class="vendorquote-banner-msg-container vendorquote-discarded-banner z-10"
      >
        <fc-icon
          group="dsm"
          name="warning"
          class="vq-warning-icon"
          color="#324056"
        ></fc-icon>
        <span class="vendor-quotes-banner-msg">
          {{ $t('common.inventory.rfq_discarded') }}</span
        >
      </div>
    </portal>
    <portal v-if="showClosedBidBanner" to="pagebuilder-fixed-top">
      <div
        class="vendorquote-banner-msg-container vendorquote-closed-bid-banner z-10"
      >
        <fc-icon
          group="dsm"
          name="warning"
          class="vq-warning-icon"
          color="#324056"
        ></fc-icon>
        <span class="vendor-quotes-banner-msg">
          {{ $t('common.inventory.vendor_quote_closed_bid') }}</span
        >
      </div>
    </portal>
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import { API } from '@facilio/api'
import AwardVendors from './components/AwardVendors'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
  getApp,
} from '@facilio/router'
export default {
  extends: CustomModuleSummary,
  props: ['viewname'],
  components: {
    AwardVendors,
  },
  data() {
    return {
      notesModuleName: 'requestForQuotationNotes',
      attachmentsModuleName: 'requestForQuotationAttachment',
      primaryFields: [
        'localId',
        'approvalFlowId',
        'approvalStatus',
        'moduleState',
        'stateFlowId',
        'billToAddress',
        'shipToAddress',
        'description',
        'requiredDate',
      ],
      showAwardVendorsDialog: false,
      vendorMultiLookupData: {
        displayName: this.$t('common.header.vendors'),
        name: 'vendors',
        lookupModule: {
          displayName: this.$t('common.header.vendors'),
          name: 'vendors',
        },
        multiple: true,
        selectedItems: [],
      },
      downloadUrl: null,
      selectedVendors: [],
      isButtonLoading: false,
    }
  },
  computed: {
    pdfUrl() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(
          this.moduleName,
          pageTypes.MODULE_CUSTOM
        )
        if (name) {
          let resolvedPath = this.$router.resolve({ name }).href || ''
          return this.$router.resolve({
            path: `${resolvedPath}/rfqpdf`,
            query: { rfqId: this.id },
          }).href
        } else return null
      }
      return `${window.location.protocol}//${window.location.host}/app/pdf/rfqpdf?rfqId=${this.id}`
    },
    moduleName() {
      return 'requestForQuotation'
    },
    mainFieldKey() {
      return 'name'
    },
    showClosedBidBanner() {
      let { record, isClosedBid } = this || {}
      if (isEmpty(record)) {
        return false
      }
      let { isQuoteReceived, isDiscarded, isRfqFinalized } = record || {}

      return !isQuoteReceived && isClosedBid && !isDiscarded && isRfqFinalized
    },
    isClosedBid() {
      let { record } = this || {}
      let { rfqType } = record || {}
      let { RFQ_TYPE } = this.$constants
      if (!isEmpty(rfqType)) {
        return rfqType === RFQ_TYPE.CLOSED_BID
      }
      return false
    },
    showEdit() {
      let canShowEdit = this.$hasPermission(`requestForQuotation:UPDATE`)
      return canShowEdit && !this.record.isRfqFinalized
    },
    showQuotesReceived() {
      return (
        this.record.isRfqFinalized &&
        !this.record.isQuoteReceived &&
        !this.record.isApprovalEnabled()
      )
    },
    showDropDown() {
      let { record } = this
      let { isDiscarded, isAwarded } = record
      return !record.isApprovalEnabled() && !isDiscarded && !isAwarded
    },
    checkIsQuoteNotAwarded() {
      let { record } = this
      let { isAwarded, isQuoteReceived } = record || {}
      return !isAwarded && isQuoteReceived
    },
    checkIsQuoteAwarded() {
      let { record } = this
      let { isAwarded } = record || {}
      return isAwarded
    },
  },
  title() {
    this.$t('common.products.rfq')
  },
  watch: {
    record: {
      handler(oldVal, newVal) {
        if (oldVal !== newVal) this.downloadUrl = null
      },
    },
  },
  methods: {
    async closeSubmissionWarningDialog() {
      let value = await this.$dialog.confirm({
        title: this.$t('common.inventory.confirm_close_submission'),
        message: this.$t('common.inventory.close_submission_warning_msg'),
        lbLabel: this.$t('common._common.cancel'),
        rbDanger: true,
        rbLabel: this.$t('common._common.confirm'),
      })
      return value
    },
    async clickAction(action) {
      let data = {}
      this.isButtonLoading = true
      if (action === 'publish_rfq') {
        this.publishRfq()
        return
      } else if (action == 'receive_quote') {
        let value = await this.closeSubmissionWarningDialog()
        if (value) {
          data = {
            id: this.$getProperty(this, 'record.id'),
            params: {
              receiveQuote: true,
            },
          }
        } else {
          this.isButtonLoading = false
          return
        }
      } else if (action == 'discard_quote') {
        data = {
          id: this.$getProperty(this, 'record.id'),
          params: {
            discardQuote: true,
          },
        }
      } else if (action === 'award_vendors') {
        this.showAwardVendorsDialog = true
        this.isButtonLoading = false
        return
      }
      let { error } = await API.updateRecord(this.moduleName, data)
      if (error) {
        let errorMessage =
          action == 'receive_quote'
            ? this.$t('common._common.error_while_reciving_quotes')
            : this.$t('common._common.error_while_discarding_rfq')
        this.$message.error(error.message || errorMessage)
      } else {
        let successMsg =
          action == 'receive_quote'
            ? this.$t('common._common.received_quotes')
            : this.$t('common._common.discarded_rfq')
        this.$message.success(successMsg)
        this.loadRecord(true)
      }
      this.isButtonLoading = false
    },
    async publishRfq() {
      this.setSelectedVendors()
      let selectedVendors = this.selectedVendors.map(vendor => ({
        id: vendor,
      }))
      if (isEmpty(selectedVendors)) {
        await this.$dialog.confirm({
          title: this.$t(`common._common.vendors_not_selected`),
          message: this.$t(
            `common._common.cannot_finalize_rfq_with_no_vendors`
          ),
          lbHide: true,
        })
      } else {
        let data = {
          id: this.$getProperty(this.record, 'id'),
          data: {
            vendor: selectedVendors,
          },
          params: {
            rfqFinalized: true,
          },
        }
        let { error } = await API.updateRecord(this.moduleName, data)
        if (error) {
          this.$message.error(
            error.message ||
              this.$t('common._common.error_while_finalizing_rfq')
          )
        } else {
          let successMsg = this.$t('common._common.finalized_rfq_successfully')
          this.$message.success(successMsg)
          this.loadRecord(true)
        }
      }
      this.isButtonLoading = false
    },
    editRecord() {
      let { id } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
          })
      } else {
        id &&
          this.$router.push({
            name: 'edit-requestForQuotation',
            params: { id },
          })
      }
    },
    async summaryDropDownAction(action) {
      if (action === 'edit_rfq') {
        this.editRecord()
      }
      if (action === 'discard_rfq') {
        this.clickAction('discard_quote')
      }
    },
    openPrintPreview() {
      if (this.pdfUrl) window.open(this.pdfUrl)
    },
    downloadPdf() {
      this.downloadUrl = null
      let appName = getApp().linkName
      let url = `${window.location.protocol}//${window.location.host}/${appName}/pdf/rfqpdf?rfqId=${this.id}`

      this.$message({
        message: this.$t('common._common.downloading'),
        showClose: true,
        duration: 0,
      })
      let additionalInfo = {
        showFooter: false,
        footerStyle: 'p {font-size:12px; margin-left:500px}',
        footerHtml:
          '<p>Page  <span class="pageNumber"></span> / <span class="totalPages"></span></p>',
      }
      API.post(`/v2/integ/pdf/create`, {
        url,
        additionalInfo,
      }).then(({ data, error }) => {
        this.$message.closeAll()
        if (error) {
          let {
            message = this.$t(
              'common.wo_report.unable_to_fetch_rfq_download_link'
            ),
          } = error
          this.$message.error(message)
        } else {
          let { fileUrl } = data || {}
          this.downloadUrl = fileUrl || null
        }
      })
    },
    setSelectedVendors() {
      if (!isEmpty(this.record.vendor)) {
        this.selectedVendors = this.record.vendor.map(vendor => vendor.id)
      }
    },
  },
}
</script>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
</style>
