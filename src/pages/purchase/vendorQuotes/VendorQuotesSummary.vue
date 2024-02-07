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
                {{ $getProperty(record, 'requestForQuotation.name', '---') }}
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
          <div
            class="marginL-auto flex-middle"
            v-if="!$getProperty(record, 'isDiscarded')"
          >
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
              v-if="checkPermissionForNegotiation"
              type="button"
              :loading="isButtonLoading"
              class="fc__add__btn pL15 pR15 self-center"
              @click="negotiate()"
            >
              {{ $t('common.inventory.negotiate') }}
            </el-button>
            <el-button
              v-if="checkAddQuotePermission"
              type="button"
              class="fc-wo-border-btn pL15 pR15 self-center"
              @click="addQuoteDialog = true"
            >
              {{ addOrUpdateQuoteButtonName }}
            </el-button>
            <el-button
              v-if="checkPermissionToFinalizeQuote"
              type="button"
              :loading="isButtonLoading"
              class="fc__add__btn pL15 pR15 self-center"
              @click="finalizeQuote()"
            >
              {{ $t('common._common.submit') }}
            </el-button>
            <el-tooltip
              effect="dark"
              :content="$t('common.inventory.download_rfq')"
              placement="top"
            >
              <el-button @click="downloadPdf" :loading="isDownloading">
                <inline-svg
                  src="svgs/download2"
                  iconClass="icon vertical-middle icon-sm fill-grey2"
                ></inline-svg>
              </el-button>
            </el-tooltip>
            <el-dropdown
              class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
              trigger="click"
              @command="action => summaryDropDownAction(action)"
              v-if="isNotVendorPortal"
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
                <!-- <el-dropdown-item
                v-if="showEdit"
                :key="1"
                :command="'edit_quote'"
                >{{ $t('common.header.edit_vendor_quotes') }}</el-dropdown-item
              > -->
                <el-dropdown-item :key="2" :command="'go_to_rfq'">{{
                  $t('common.header.go_to_rfq')
                }}</el-dropdown-item>
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
    <portal
      v-if="$getProperty(record, 'isDiscarded', false)"
      to="pagebuilder-fixed-top"
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
          {{ $t('common.inventory.vendor_quote_discarded') }}</span
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
    <AddQuote
      v-if="addQuoteDialog"
      :visibility.sync="addQuoteDialog"
      :record="record"
      :moduleName="moduleName"
      @saved="loadRecord(true)"
    >
    </AddQuote>
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import { API } from '@facilio/api'
import AddQuote from './components/AddQuote'
import moment from 'moment-timezone'
import { getApp } from '@facilio/router'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: CustomModuleSummary,
  props: ['viewname'],
  components: {
    AddQuote,
  },
  data() {
    return {
      notesModuleName: 'vendorQuotesNotes',
      attachmentsModuleName: 'vendorQuotesAttachment',
      primaryFields: [
        'localId',
        'approvalFlowId',
        'approvalStatus',
        'moduleState',
        'stateFlowId',
      ],
      addQuoteDialog: false,
      appLinkName: null,
      isButtonLoading: false,
      downloadUrl: null,
      isDownloading: false,
    }
  },
  watch: {
    record: {
      handler(oldVal, newVal) {
        if (oldVal !== newVal) this.downloadUrl = null
      },
    },
  },
  computed: {
    isNotVendorPortal() {
      return getApp().linkName !== 'vendor'
    },
    showClosedBidBanner() {
      let { record, isClosedBid, isNotVendorPortal } = this || {}
      let { requestForQuotation } = record || {}
      if (isEmpty(requestForQuotation)) {
        return false
      }
      let { isQuoteReceived, isDiscarded, isRfqFinalized } =
        requestForQuotation || {}
      return (
        !isQuoteReceived &&
        isClosedBid &&
        !isDiscarded &&
        isRfqFinalized &&
        isNotVendorPortal
      )
    },
    requestForQuotation() {
      let { record } = this
      let { requestForQuotation } = record || {}
      return requestForQuotation
    },
    vendorQuotesLineItems() {
      let { record } = this
      let { vendorQuotesLineItems } = record || {}
      return vendorQuotesLineItems
    },
    moduleName() {
      return 'vendorQuotes'
    },
    showEdit() {
      let canShowEdit = this.$hasPermission(`requestForQuotation:UPDATE`)
      let isNotLocked = this.isStateFlowEnabled
        ? !this.isRecordLocked && !this.isRequestedState
        : true
      return (
        canShowEdit &&
        isNotLocked &&
        this.$getProperty(this.record, 'requestForQuotation.isRfqFinalized') &&
        !this.$getProperty(this.record, 'requestForQuotation.isQuoteReceived')
      )
    },
    checkAddQuotePermission() {
      let { checkPortalPermission } = this

      let status = this.$getProperty(this.record, 'moduleState.status')
      return (
        checkPortalPermission &&
        ['undernegotiation', 'awaitingvendorquote'].includes(status)
      )
    },
    checkRfqAndApprovalPermission() {
      let { record } = this
      return (
        !record.isApprovalEnabled() &&
        this.$getProperty(record, 'requestForQuotation.isRfqFinalized') &&
        !this.$getProperty(record, 'requestForQuotation.isQuoteReceived')
      )
    },
    checkPermissionForNegotiation() {
      let {
        checkRfqAndApprovalPermission,
        record,
        isNotVendorPortal,
        isClosedBid,
      } = this
      // hardcoded check for icd, will be handled with system button feature in V2
      let hideBtn = this.$org.id === 30

      return (
        checkRfqAndApprovalPermission &&
        this.$getProperty(record, 'isFinalized') &&
        isNotVendorPortal &&
        !hideBtn &&
        !isClosedBid
      )
    },
    isClosedBid() {
      let { record } = this || {}
      let { requestForQuotation } = record || {}
      let { rfqType } = requestForQuotation || {}

      let { RFQ_TYPE } = this.$constants
      return rfqType === RFQ_TYPE.CLOSED_BID
    },
    checkPortalPermission() {
      let { checkRfqAndApprovalPermission, record, isClosedBid } = this
      let checkPermission =
        checkRfqAndApprovalPermission &&
        !this.$getProperty(record, 'isFinalized')
      if (getApp().linkName === 'vendor') {
        return checkPermission
      }
      return (
        checkPermission &&
        !this.$getProperty(this.record, 'vendorPortalAccess') &&
        !isClosedBid
      )
    },
    checkLineItemHasQuotedUnitPrice() {
      let { vendorQuotesLineItems } = this
      if (!isEmpty(vendorQuotesLineItems)) {
        let quotedVendorQuotesLineItems = vendorQuotesLineItems.filter(
          element => {
            let { counterPrice } = element || {}
            return !isEmpty(counterPrice)
          }
        )
        let { length } = quotedVendorQuotesLineItems
        return length > 0
      }
      return false
    },
    checkPermissionToFinalizeQuote() {
      let { checkLineItemHasQuotedUnitPrice, checkPortalPermission } = this
      return checkLineItemHasQuotedUnitPrice && checkPortalPermission
    },
    addOrUpdateQuoteButtonName() {
      let { checkLineItemHasQuotedUnitPrice } = this
      if (checkLineItemHasQuotedUnitPrice) {
        return this.$t('common.products.update_quote')
      } else {
        return this.$t('common.products.add_quote')
      }
    },
  },
  title() {
    'Vendor Quotes'
  },
  methods: {
    downloadPdf() {
      this.isDownloading = true
      this.downloadUrl = null
      let { requestForQuotation } = this || {}
      let { id } = requestForQuotation || {}
      let appName = getApp().linkName
      let url = `${window.location.protocol}//${window.location.host}/${appName}/pdf/vendorQuoteRfqPdf?rfqId=${id}`
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
      this.isDownloading = false
    },

    async negotiate() {
      this.isButtonLoading = true
      let data = {
        id: this.$getProperty(this.record, 'id'),
        params: {
          negotiation: true,
        },
      }
      let { error } = await API.updateRecord(this.moduleName, data)
      if (error) {
        this.$message.error(
          error.message || this.$t('common.inventory.error_occurred')
        )
      } else {
        let successMsg = this.$t('common.inventory.negotiation_initiated')
        this.$message.success(successMsg)
        this.loadRecord(true)
      }
      this.isButtonLoading = false
    },
    async submitQuoteWarningDialog() {
      let value = await this.$dialog.confirm({
        title: this.$t('common.inventory.confirm_submit_quote'),
        message: this.$t('common.inventory.submit_quote_waring_message'),
        lbLabel: this.$t('common._common.cancel'),
        rbDanger: true,
        rbLabel: this.$t('common._common.confirm'),
      })
      return value
    },
    async finalizeQuote() {
      this.isButtonLoading = true
      let { checkLineItemHasQuotedUnitPrice } = this
      let value = await this.submitQuoteWarningDialog()
      if (value) {
        if (!checkLineItemHasQuotedUnitPrice) {
          this.$message.error(
            this.$t('common.inventory.quote_atleast_one_lineitem_warning_msg')
          )
          this.isButtonLoading = false
          return
        } else {
          let currentmillis = moment.tz(this.timeZone).valueOf()
          let data = {
            id: this.$getProperty(this.record, 'id'),
            data: {
              replyDate: currentmillis,
            },
            params: {
              finalizeVendorQuote: true,
            },
          }
          let { error } = await API.updateRecord(this.moduleName, data)
          if (error) {
            this.$message.error(
              error.message ||
                this.$t('common._common.error_while_adding_quote_details')
            )
          } else {
            let { isNotVendorPortal } = this
            let successMsg = !isNotVendorPortal
              ? this.$t(
                  'common.inventory.submit_quote_success_msg_vendor_portal'
                )
              : this.$t('common.inventory.submit_quote_success_msg')
            this.$message.success(successMsg)
            this.loadRecord(true)
          }
        }
      }
      this.isButtonLoading = false
    },
    summaryDropDownAction(action) {
      let id = this.$getProperty(this.record, 'requestForQuotation.id')
      if (action === 'edit_quote') {
        this.editRecord()
      }
      if (action === 'go_to_rfq') {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('requestForQuotation', pageTypes.OVERVIEW) || {}
          name &&
            this.$router.push({
              name,
              params: {
                id,
                viewname: this.viewname,
                query: this.$route.query,
              },
            })
        } else {
          this.$router.push({
            path: `/app/purchase/requestForQuotation/all/${id}/overview`,
          })
        }
      }
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
            name: 'edit-vendorQuotes',
            params: { id },
          })
      }
    },
  },
}
</script>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
</style>
