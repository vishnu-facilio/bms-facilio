<template>
  <!-- OverWiriting For Header -->
  <div
    class="custom-modules-overview overflow-y-scroll"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div
      v-if="!isLoading && customModuleData"
      class="header pT10 pB15 pL20 pR20"
    >
      <div v-if="!$validation.isEmpty(customModuleData)" class="asset-details">
        <div class="d-flex flex-middle">
          <div v-if="showPhotoField">
            <div v-if="customModuleData[photoFieldName]">
              <img
                :src="getImage(customModuleData[photoFieldName])"
                class="img-container"
              />
            </div>
            <div v-else-if="showAvatar">
              <avatar
                size="lg"
                :user="{ name: customModuleData.name }"
              ></avatar>
            </div>
          </div>
          <div class="mL5">
            <div class="asset-name mb5 mT10">
              {{ customModuleData[mainFieldKey] }}
              <div
                v-if="currentModuleState"
                class="fc-badge text-uppercase inline vertical-middle mL15"
              >
                {{ currentModuleState }}
              </div>
            </div>
            <div class="asset-id mT10">
              <i
                class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                content="back"
                arrow
                v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                @click="back"
                v-if="$account.portalInfo"
              ></i>
              #{{ customModuleData.parentId }}
            </div>
          </div>
        </div>
      </div>
      <div class="d-flex flex-direction-row" style="margin-left: auto;">
        <el-button-group class="fc-group-btn2 flex-middle">
          <el-button type="primary" @click="downloadQuotation">
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
          <el-button @click="sendQuotationMail" type="primary">
            <inline-svg
              src="svgs/mail"
              iconClass="icon vertical-middle icon-sm fill-grey2"
            ></inline-svg>
          </el-button>
        </el-button-group>
        <CustomButton
          class="p10"
          :record="customModuleData"
          :moduleName="moduleName"
          :position="POSITION.SUMMARY"
          @refresh="refreshObj(true)"
          @onError="() => {}"
        />
        <iframe
          v-if="downloadUrl"
          :src="downloadUrl"
          style="display: none;"
        ></iframe>
        <template v-if="isStateFlowEnabled">
          <TransitionButtons
            class="mR10"
            :key="customModuleData.id"
            :moduleName="moduleName"
            :record="customModuleData"
            :disabled="isApprovalEnabled"
            buttonClass="asset-el-btn"
            @currentState="() => {}"
            @transitionSuccess="refreshObj(true)"
            @transitionFailure="() => {}"
          ></TransitionButtons>
        </template>

        <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
          <ApprovalBar
            :moduleName="moduleName"
            :key="customModuleData.id + 'approval-bar'"
            :record="customModuleData"
            :hideApprovers="shouldHideApprovers"
            @onSuccess="refreshObj()"
            @onFailure="() => {}"
            class="approval-bar-shadow"
          ></ApprovalBar>
        </portal>
        <el-dropdown
          class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
          v-if="$hasPermission(`${moduleName}:UPDATE`)"
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
            <el-dropdown-item :key="1" :command="'associateterms'"
              >Associate Terms</el-dropdown-item
            >
            <el-dropdown-item
              :key="2"
              v-if="
                !isRecordLocked &&
                  customModuleData.customerType === 4 &&
                  !isParentQuoteExists
              "
              :command="'createCustomerQuote'"
              >Convert to User Quote</el-dropdown-item
            >
            <el-dropdown-item
              :key="3"
              v-if="showOptionViewCustomerQuote"
              :command="'redirecttocustomerquote'"
              >View Customer Quote</el-dropdown-item
            >
            <el-dropdown-item :key="4" v-if="!isRecordLocked" :command="'edit'"
              >Edit</el-dropdown-item
            >
            <el-dropdown-item v-if="canShowRevise" :key="5" :command="'revise'"
              >Revise</el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <page
      v-if="!isLoading && customModuleData && customModuleData.id"
      :key="customModuleData.id"
      :module="moduleName"
      :id="customModuleData.id"
      :details="customModuleData"
      :primaryFields="primaryFields"
      :notesModuleName="notesModuleName"
      :attachmentsModuleName="attachmentsModuleName"
      :isV3Api="true"
    ></page>
    <el-dialog
      :visible.sync="showDeleteDialog"
      class="dialog-d"
      custom-class="setup-dialog45"
      :show-close="false"
    >
      <div class="text-center fc-black-20">
        Do you want to delete or dissociate from
        {{ moduleName ? moduleName : '' }} ?
      </div>
      <span
        slot="footer"
        class="fc-dialog-center-container delete-dialog-footer padding-px18"
      >
        <el-button @click="showDeleteDialog = false">CANCEL</el-button>
        <el-button class="delete-dissociate-buttons" @click="dissociate()"
          >DISSOCIATE</el-button
        >
        <el-button class="delete-dissociate-buttons" @click="deleteRecord()"
          >MOVE TO RECYCLE BIN</el-button
        >
      </span>
    </el-dialog>
    <QuotationTermsDialog
      v-if="associateTermsDialogVisibility"
      :visibility.sync="associateTermsDialogVisibility"
      :record="customModuleData"
      :associateUrl="'v2/quotation/associateTerms'"
      @refreshSummary="refreshObj"
    ></QuotationTermsDialog>
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
import CustomModuleOverview from 'pages/base-module-v2/Overview'
import { mapState, mapGetters } from 'vuex'
import { API } from '@facilio/api'
import QuotationTermsDialog from './TermsAssociationDialog'
import { eventBus } from '@/page/widget/utils/eventBus'
import { isEmpty } from '@facilio/utils/validation'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import cloneDeep from 'lodash/cloneDeep'

import {
  getApp,
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

const orgIdVsRevisionStatus = {
  274: 'rejectedbycustomer',
  407: 'quoterevision',
}
export default {
  extends: CustomModuleOverview,
  components: {
    QuotationTermsDialog,
    CustomButton,
  },
  data() {
    return {
      childQuotes: [],
      isParentQuoteExists: false,
      notesModuleName: 'quotenotes',
      attachmentsModuleName: 'quoteattachments',
      primaryFields: [
        'subject',
        'billToAddress',
        'billDate',
        'description',
        'expiryDate',
        'contact',
      ],
      quotationDetails: null,
      associateTermsDialogVisibility: false,
      downloadUrl: null,
      POSITION: POSITION_TYPE,
      quoteFormsList: [],
      withMarkup: true,
      showMarkupDialog: false,
      selectedRecord: null,
    }
  },
  computed: {
    childQuote() {
      if (this.childQuotes.length) {
        return this.childQuotes[0]
      }
      return null
    },
    currentView() {
      if (this.viewName) {
        return this.viewName
      }
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    showOptionViewCustomerQuote() {
      let { customerType } = this.quotationDetails
      return !!(
        this.isParentQuoteExists &&
        this.childQuote !== null &&
        customerType === 4
      )
    },
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
    getCustomerTypeFromParent() {
      if (this.quotationDetails?.workorder?.tenant?.id) {
        return 1
      } else {
        return 2
      }
    },
    getWokorderTenant() {
      if (this.quotationDetails?.workorder?.tenant?.id) {
        return this.quotationDetails.workorder.tenant.id
      }
      return null
    },
    getWokorder() {
      if (this.quotationDetails?.workorder?.id) {
        return this.quotationDetails.workorder.id
      }
      return null
    },
    getWokorderClinet() {
      if (this.quotationDetails?.workorder?.client?.id) {
        return this.quotationDetails.workorder.client.id
      }
      return null
    },
    moduleName() {
      return 'quote'
    },
    ...mapGetters(['getTicketStatus']),
    mainFieldKey() {
      return 'subject'
    },
    customModuleData() {
      return this.quotationDetails
    },
    ...mapState({
      ticketStatus: state => state.ticketStatus.quote,
    }),
    pdfUrl() {
      let { id } = this
      let url
      let appName = getApp().linkName
      let record = this.customModuleData
      if (isWebTabsEnabled()) {
        if (this.canShowMarkup(record) && this.withMarkup) {
          url = `/${appName}/pdf/quotationpdf?quotationId=${id}&withmarkup=true`
        } else if (this.canShowMarkup(record) && !this.withMarkup) {
          url = `/${appName}/pdf/quotationpdf?quotationId=${id}&withmarkup=false`
        } else {
          url = `/${appName}/pdf/quotationpdf?quotationId=${id}`
        }
      } else {
        url = `/app/pdf/quotationpdf?quotationId=${id}`
      }

      return window.location.protocol + '//' + window.location.host + url
    },
    moduleStateId() {
      return this.$getProperty(this, 'customModuleData.moduleState.id')
    },
    canShowRevise() {
      let status = this.getTicketStatus(this.moduleStateId, 'quote')
      if (
        (orgIdVsRevisionStatus[this.$org.id] || 'Sent') ===
        (status || {}).status
      ) {
        return true
      }
      return false
    },
  },
  title() {
    'Quote'
  },
  mounted() {
    eventBus.$on('refesh-parent', this.refreshObj)
    this.loadCustomerQuote()
    this.loadQuoteDependencies()
  },
  beforeDestroy() {
    eventBus.$off('refesh-parent', this.refreshObj)
  },
  methods: {
    downloadQuotation() {
      this.selectedRecord = null
      this.showMarkupDialog = false
      let record = this.customModuleData
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
    downloadQuote() {
      this.closemarkupDialog()
      this.downloadUrl = null
      this.$message({
        message: 'Downloading...',
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
        url: this.pdfUrl,
        additionalInfo,
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
    canShowMarkup() {
      return this.quotationDetails?.showMarkupValue || false
    },
    getRedirectFormId(cusType) {
      let customerType = this.quotationDetails.customerType
      if (cusType) {
        customerType = cusType
      }
      if (customerType > -1) {
        let customerTypeToFormsMap = {
          1: 'tenantquoteform',
          2: 'clientquoteform',
          3: 'default_quote_web_maintenance',
          4: 'vendorquoteform',
        }
        if (customerType) {
          let form = this.quoteFormsList.find(rt => {
            if (rt.name === customerTypeToFormsMap[customerType]) {
              return rt
            } else {
              return null
            }
          })
          if (form?.id) {
            return form.id
          }
        }
      } else {
        if (
          this.quoteFormsList.length &&
          this.quoteFormsList[0] &&
          this.quoteFormsList[0].id
        ) {
          return this.quoteFormsList[0].id
        }
      }
      return null
    },
    async loadQuoteDependencies() {
      await this.loadQuoteForms()
    },
    async loadQuoteForms() {
      let url = `/v2/forms?moduleName=${'quote'}`

      let { data, error } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { forms = [] } = data || {}
        this.quoteFormsList = forms
      }
    },
    getFormIdfromQuote() {
      return this.getRedirectFormId(this.getCustomerTypeFromParent)
    },
    async loadCustomerQuote() {
      let { page = 1, perPage = 1, moduleName } = this || {}
      let { id } = this
      let filters = {
        parentQuotationId: {
          operatorId: 36,
          value: [id + ''],
        },
        oneLevelLookup: {},
      }

      if (!isEmpty(filters)) {
        filters = JSON.stringify(filters)
      }

      let params = {
        includeParentFilter: true,
        page,
        perPage,
        moduleName: moduleName,
        withCount: true,
        filters,
      }

      let url = '/v3/modules/data/list'
      let { data } = await API.get(url, params)

      if (data?.quote && data.quote.length) {
        this.childQuotes = data.quote
        this.isParentQuoteExists = true
      }
    },
    refreshObj(refreshList) {
      this.loadCustomModuleData()
      if (refreshList) {
        this.$emit('refreshSummaryList', true)
      }
    },
    async loadCustomModuleData() {
      this.isLoading = true
      let { quote, error } = await API.fetchRecord(
        'quote',
        {
          id: this.id,
        },
        { force: true } // temp call only for update
      )
      if (!isEmpty(error)) {
        let { message = 'Error Occured while fetching Quote' } = error
        this.$message.error(message)
      } else {
        this.quotationDetails = quote
      }
      this.isLoading = false
    },
    editCustomModuleData() {
      let { id, moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
          })
      } else {
        this.$router.push({
          path: `/app/tm/quotation/edit/${id}`,
        })
      }
    },
    transformFormData(returnObj, data) {
      returnObj['quote'] = { ...returnObj['quote'], ...data }
      return returnObj
    },
    summaryDropDownAction(action) {
      if (action === 'edit') {
        this.editCustomModuleData(this.customModuleData)
      } else if (action === 'associateterms') {
        this.associateTermsDialogVisibility = true
      } else if (action === 'revise') {
        this.reviseQuotation()
      } else if (action === 'createCustomerQuote') {
        this.createCustomerQuoteAction()
      } else if (action === 'redirecttocustomerquote') {
        this.redirectToCustomerQuote()
      }
    },
    async redirectToCustomerQuote() {
      if (isWebTabsEnabled()) {
        let { currentView, moduleName } = this
        let id = this.childQuote.id
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        console.log('---->', name, currentView, id, moduleName)
        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
              id: id,
            },
            query: this.$route.query,
          })
      }
    },
    createCustomerQuoteAction() {
      let quote = cloneDeep(this.quotationDetails)

      let formId = this.getFormIdfromQuote(quote)

      let query = {
        customerType: 2,
        subject: quote.subject,
        description: quote.description || '',
        lineItems: quote.lineItems || [],
        siteId: quote.siteId,
        parentQuotationId: quote.id,
      }

      if (this.getWokorder !== null) {
        query['workorder'] = this.getWokorder
      }
      if (this.getWokorderTenant !== null) {
        query['tenant'] = this.getWokorderTenant
      }
      if (this.getWokorderClinet !== null) {
        query['client'] = this.getWokorderClinet
      }
      if (quote?.billDate) {
        query['billDate'] = quote.billDate
      }

      if (quote?.expiryDate) {
        query['expiryDate'] = quote.expiryDate
      }

      if (quote?.markup) {
        query['markup'] = quote.markup
      }

      if (formId) {
        query['formId'] = formId
      }

      query['customerType'] = this.getCustomerTypeFromParent

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('quote', pageTypes.CREATE) || {}

        name &&
          this.$router.push({
            name,
            query,
          })
      }
    },
    reviseQuotation() {
      let { id, moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
            query: { revise: true },
          })
      } else {
        this.$router.push({
          path: `/app/tm/quotation/edit/${id}?revise=true`,
        })
      }
    },
    openPrintPreview() {
      window.open(this.pdfUrl)
    },
    sendQuotationMail() {
      if (!isEmpty(this.quotationDetails.contact)) {
        this.$router.push({
          path: `sendmail`,
        })
      } else {
        this.$message.error(
          'There is no contact information associated for the quotation'
        )
      }
    },
    back() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
          })
      } else {
        this.$router.go(-1)
      }
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
</style>
