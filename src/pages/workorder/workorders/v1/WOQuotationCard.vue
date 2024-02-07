<template>
  <div>
    <div v-if="isLoading" class="white-bg-block mT20 position-relative">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div v-else class="white-bg-block mT20 position-relative">
      <div>
        <div class="p20">
          <div class="fc-id11 text-uppercase">
            MAINTENANCE COST
          </div>
          <currency
            :value="
              $validation.isEmpty(workorder.totalCost) ? 0 : workorder.totalCost
            "
            class="pT10 fc-black2-20"
          ></currency>
        </div>
        <div
          v-if="
            $validation.isEmpty(quotationList) && workorder.isQuotationNeeded
          "
          class="fc-wo-create-quotation-link fc-border-top1"
        >
          <div @click="redirectToQuotationForm" class="fc-dark-blue3-13 bold">
            <i class="el-icon-plus pR5 fwBold"></i>Create Quote
          </div>
        </div>
      </div>
      <iframe v-if="downloadUrl" :src="downloadUrl" class="hide"></iframe>
      <el-dialog
        v-if="quotationSummaryDetails"
        :visible.sync="dialogVisible"
        width="80%"
        class="fc-dialog-header-hide"
      >
        <div class="fc-quotation-wo-action ">
          <div
            @click="openQuotationSummary(quotationSummaryDetails)"
            class="bold fc-white-14 pR20 pointer"
          >
            <i class="fa fa-external-link pR5" aria-hidden="true"></i>
            Go To Summary
          </div>
          <div
            @click="downloadQuotation(quotationSummaryDetails)"
            class="bold fc-white-14 pR20 pointer"
          >
            <inline-svg
              src="svgs/download2"
              iconClass="icon vertical-text-top icon-xs fc-fill-path fill-path-white mR5"
            >
            </inline-svg>
            Download
          </div>
          <div @click="dialogVisible = false" class="fc-white-14 f20 pointer">
            <i class="el-icon-close"></i>
          </div>
        </div>
        <div v-if="summaryLoading">
          <spinner :show="summaryLoading" :size="80"></spinner>
        </div>
        <div v-else>
          <QuotationPreview
            :details="quotationSummaryDetails"
          ></QuotationPreview>
        </div>
      </el-dialog>
    </div>
    <div
      v-if="!$validation.isEmpty(quotationList)"
      class="white-bg-block mT20 position-relative"
    >
      <div class="p20">
        <div class="fc-id11 text-uppercase pB10">
          {{ quotationList.length > 1 ? `Quotes` : `Quote` }}
        </div>
        <div
          v-for="(record, index) in quotationList"
          :key="index"
          class="fc-wo-quote-card visibility-visible-actions pointer"
        >
          <div
            v-if="isApprovalEnabled(record)"
            class="mT20 mB10 wo-quotation-approval-bar"
          >
            <ApprovalBar
              :moduleName="'quote'"
              :key="record.id + 'approval-bar'"
              :record="record"
              :hideApprovers="true"
              @onSuccess="loadWorkorderQuotations(true)"
              @onFailure="() => {}"
              class="approval-bar-shadow"
            >
            </ApprovalBar>
          </div>
          <div class="items-baseline flex pT15 justify-content-space">
            <div class="items-baseline flex width70">
              <div class="fc-black2-14 bold pR10 pB5">
                {{ record.subject }}
              </div>
              <el-tag
                class="fc-tag-status text-uppercase"
                :class="[
                  getTicketStatusDetails(record).status === 'Sent'
                    ? 'fc-tag-status-bg-green'
                    : 'fc-tag-status-bg-grey',
                ]"
                >{{ getTicketStatusDetails(record).displayName }}</el-tag
              >
            </div>
            <div
              class="flex justify-content-end wo-quote-round-btn visibility-hide-actions width30"
            >
              <el-button
                round
                icon="el-icon-view"
                @click="openQuotationPreview(record)"
              ></el-button>
              <el-button
                round
                icon="el-icon-download"
                @click="downloadQuotation(record)"
              ></el-button>
              <el-button round @click="openQuotationSummary(record)"
                ><i class="fa fa-external-link" aria-hidden="true"></i
              ></el-button>
            </div>
          </div>
          <div class="flex-middle justify-content-space pT10">
            <div class="fc-blue-label">
              TOTAL AMOUNT
            </div>
            <div>
              <currency
                :value="record.totalCost"
                class="fc-black-14 bold"
              ></currency>
            </div>
          </div>

          <div class="flex-middle justify-content-space pT10">
            <div class="fc-blue-label">
              BILL DATE
            </div>
            <div
              v-if="!$validation.isEmpty(record.billDate)"
              class="fc-black-14 bold"
            >
              {{ record.billDate | formatDate(true) }}
            </div>
          </div>
        </div>
      </div>
      <div
        v-if="workorder.isQuotationNeeded"
        class="fc-wo-create-quotation-link"
      >
        <div @click="redirectToQuotationForm" class="fc-dark-blue3-13 bold">
          <i class="el-icon-plus pR5 fwBold"></i>Create Quote
        </div>
      </div>
    </div>
    <el-dialog
      v-if="showMarkupDialog"
      title="Download Preferences"
      :visible.sync="showMarkupDialog"
      width="40%"
      :append-to-body="true"
      style="z-index: 9999999999;"
      :before-close="closemarkupDialog"
      class="agents-dialog fc-dialog-center-container fc-dialog-center-container-2"
    >
      <div class="label-txt-black line-height24">
        <el-checkbox v-model="withMarkup">Download with markup</el-checkbox>
        <div class="modal-dialog-footer">
          <el-button
            class="modal-btn-save f13 w100"
            @click="downloadQuote(selectedRecord)"
            >{{ 'Download' }}</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'
import ApprovalBar from '@/approval/ApprovalBar'
import QuotationPreview from 'src/pages/quotation/v1/QuotationPreviewParent'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
  getApp,
} from '@facilio/router'
import { getRelatedFieldName } from 'src/util/relatedFieldUtil'

export default {
  props: ['workorder'],
  components: {
    ApprovalBar,
    QuotationPreview,
  },
  mixins: [FetchViewsMixin],
  data() {
    return {
      quotationList: {},
      downloadUrl: null,
      isLoading: false,
      summaryLoading: true,
      dialogVisible: false,
      quotationSummaryDetails: {},
      withMarkup: true,
      showMarkupDialog: false,
      selectedRecord: null,
    }
  },
  created() {
    this.loadWorkorderQuotations()
    this.$store.dispatch('loadTicketStatus', 'quote')
  },
  computed: {
    ...mapGetters(['getTicketStatus']),
    getCustomerTypeByApp() {
      if (getApp().linkName === 'tenant') {
        return '1'
      } else if (getApp().linkName === 'client') {
        return '2'
      } else if (getApp().linkName === 'vendor') {
        return '4'
      } else if (getApp().linkName === 'occupant') {
        return '3'
      } else {
        return null
      }
    },
  },
  methods: {
    canShowMarkup(record) {
      return record?.showMarkupValue || false
    },
    pdfUrl(record) {
      let appName = getApp().linkName
      if (this.canShowMarkup(record) && this.withMarkup) {
        return `${window.location.protocol}//${window.location.host}/${appName}/pdf/quotationpdf?quotationId=${record.id}&withmarkup=true`
      } else if (this.canShowMarkup(record) && !this.withMarkup) {
        return `${window.location.protocol}//${window.location.host}/${appName}/pdf/quotationpdf?quotationId=${record.id}&withmarkup=false`
      }
      return `${window.location.protocol}//${window.location.host}/${appName}/pdf/quotationpdf?quotationId=${record.id}`
    },
    isApprovalEnabled(record) {
      let { approvalFlowId, approvalStatus } = record || {}
      return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
    },
    updateWorkorder(val) {
      let updateObj = {
        id: [this.workorder.id],
        fields: {
          isQuotationNeeded: val,
        },
      }
      this.isLoading = true
      this.$store
        .dispatch('workorder/updateWorkOrder', updateObj)
        .then(() => {
          this.$message.success(
            this.$t('maintenance._workorder.wo_update_success')
          )
          this.isLoading = false
        })
        .catch(() => {
          this.$message.error(
            this.$t('maintenance._workorder.wo_update_failed')
          )
        })
    },

    getTicketStatusDetails(record) {
      return (
        this.getTicketStatus(
          this.$getProperty(record, 'moduleState.id', -1),
          'quote'
        ) || {}
      )
    },
    async loadWorkorderQuotations(force = false) {
      this.isLoading = true
      let { workorder } = this
      let { id } = workorder || {}
      let relatedFieldName = getRelatedFieldName('workorder', 'quote')
      let relatedConfig = {
        moduleName: 'workorder',
        id,
        relatedModuleName: 'quote',
        relatedFieldName,
      }
      let clientCriteria = {
        conditions: {
          1: {
            fieldName: 'customerType',
            operatorId: 9,
            value: this.getCustomerTypeByApp,
          },
          2: {
            fieldName: 'customerType',
            operatorId: 1,
            value: null,
          },
        },
        pattern: '(1 or 2)',
      }
      // let filters = {
      //   customerType: { operatorId: 9, value: [this.getCustomerTypeByApp] },
      // }

      let params = {}
      // if (!isEmpty(filters) && this.getCustomerTypeByApp !== null) {
      //   params = { filters: JSON.stringify(filters) }
      // }

      if (!isEmpty(clientCriteria)) {
        params = { clientCriteria: JSON.stringify(clientCriteria) }
      }
      let { error, list } = await API.fetchAllRelatedList(
        relatedConfig,
        params,
        {
          force,
        }
      )

      if (!error) {
        if (!isEmpty(list)) {
          this.quotationList = list
        }
      } else {
        this.$error.message(error.message || 'Error Occured')
      }
      this.isLoading = false
    },
    redirectToQuotationForm() {
      let url = `/app/tm/quotation/new`
      let query = {
        workorder: this.workorder.id,
        workorderLabel: this.workorder.subject,
      }
      if (!isEmpty(this.$getProperty(this.workorder, 'tenant.id', -1))) {
        query['customerType'] = 1
        query['tenant'] = this.workorder.tenant.id
        query['tenantLabel'] = this.workorder.tenant.name
      }

      if (!isEmpty(this.$getProperty(this.workorder, 'client.id', -1))) {
        query['customerType'] = 2
        query['client'] = this.workorder.client.id
        query['clientLabel'] = this.workorder.client.name
      }
      if (!isEmpty(this.workorder.siteId)) {
        query['siteId'] = this.workorder.siteId
      }
      if (this.$account.org.id === 320) {
        if (
          !isEmpty(this.workorder.data) &&
          !isEmpty(this.workorder.data.unit)
        ) {
          query['unit'] = this.workorder.data.unit.id
          query['unitLabel'] = this.workorder.data.unit.name
        }
        if (
          !isEmpty(this.workorder.resource) &&
          !isEmpty(this.workorder.resource.building)
        ) {
          query['building'] = this.workorder.resource.building.id
          query['buildingLabel'] = this.workorder.resource.name
        }
      } else {
        query['subject'] = this.workorder.subject
        query['description'] = this.workorder.description
      }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('quote', pageTypes.CREATE) || {}

        name &&
          this.$router.push({
            name,
            query,
          })
      } else {
        this.$router.push({
          path: url,
          query,
        })
      }
    },
    async openQuotationPreview(record) {
      this.quotationSummaryDetails = null
      this.dialogVisible = true
      this.summaryLoading = true
      let { quote, error } = await API.fetchRecord('quote', {
        id: (record || {}).id,
      })
      if (!isEmpty(error)) {
        let { message = 'Error Occured while fetching Quote' } = error
        this.$message.error(message)
      } else {
        this.quotationSummaryDetails = quote
      }
      this.summaryLoading = false
    },
    downloadQuotation(record) {
      this.selectedRecord = null
      this.showMarkupDialog = false
      if (this.canShowMarkup(record)) {
        this.selectedRecord = record
        this.showMarkupDialog = true
      } else {
        this.downloadQuote(record)
      }
    },
    closemarkupDialog() {
      this.showMarkupDialog = false
      this.selectedRecord = false
    },
    downloadQuote(record) {
      this.closemarkupDialog()
      this.downloadUrl = null
      this.$message({
        message: 'Downloading...',
        showClose: true,
        duration: 0,
      })
      API.post(`/v2/integ/pdf/create`, {
        url: this.pdfUrl(record),
      }).then(({ data, error }) => {
        this.$message.closeAll()
        if (error) {
          let { message = 'Unable to fetch quote download link' } = error
          this.$message.error(message)
        } else {
          let { fileUrl } = data || {}
          this.downloadUrl = fileUrl || null
        }
        this.withMarkup = true
      })
    },
    async openQuotationSummary({ id }) {
      let routerPath = ''

      if (isWebTabsEnabled()) {
        let viewname = await this.fetchView('quote')
        let { name } = findRouteForModule('quote', pageTypes.OVERVIEW) || {}

        if (name) {
          let { href } =
            this.$router.resolve({ name, params: { viewname, id } }) || {}
          routerPath = href
        }
      } else {
        routerPath = this.$router.resolve({
          path: `/app/tm/quotation/all/${id}/overview`,
        }).href
      }
      window.open(routerPath, '_blank')
    },
  },
}
</script>
<style lang="scss">
.fc-dialog-center-container-2 {
  .modal-dialog-footer {
    bottom: -50px;
  }
  .modal-btn-save {
    width: 100%;
  }
}

.wo-quotation-approval-bar {
  width: 100%;
  padding-bottom: 10px;
  padding-top: 10px;
  border-radius: 5px;

  .w100 {
    width: 100%;
  }

  .approval-bar {
    flex-direction: column;
    border-bottom: none;
    border-radius: 5px;
    border: solid 1px #c8dfe4;
    background-color: #f5fdff;
    font-size: 13px;
    padding: 10px 5px 10px 5px;
    letter-spacing: 0.54px;
    line-height: 20px;
    text-align: center;
    color: #324056;

    .approval-desc {
      margin-right: 0px;
    }
  }
}

.fc-wo-quote-card {
  &:not(:last-child) {
    border-bottom: 1px solid #eef3f4;
    padding-bottom: 20px;
  }
}

.wo-quote-round-btn {
  .el-button.is-round {
    padding: 5px;
    border: 1px solid rgba(72, 158, 220, 10%);
    background: rgba(72, 158, 220, 0.8);
    color: #fff;
    font-size: 12px;
    font-weight: bold;
    i {
      vertical-align: middle;
    }
  }
  .el-button + .el-button {
    margin-left: 5px;
  }
}
</style>
