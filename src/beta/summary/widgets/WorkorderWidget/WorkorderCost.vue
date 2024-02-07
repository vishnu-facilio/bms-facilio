<template>
  <FContainer>
    <FContainer>
      <FContainer
        padding="containerXxLarge containerXLarge"
        gap="containerLarge"
      >
        <FContainer v-if="!showMaintenanceCost">
          <FContainer padding="containerMedium containerNone">
            <FText
              appearance="headingMed14"
              textAlign="center"
              color="textMain"
              fontWeight="bold"
              >Maintenance cost</FText
            >
          </FContainer>
          <FContainer padding="containerMedium containerNone">
            <FText line-height="18.2px" appearance="bodyReg14" color="textMain">
              <currency
                v-if="!showMaintenanceCost"
                :value="
                  $validation.isEmpty(this.details.totalCost)
                    ? 0
                    : this.details.totalCost
                "
              ></currency>
            </FText>
          </FContainer>
        </FContainer>
        <WOMaintenanceCostCard
          v-if="showMaintenanceCost"
          :workorder="workorder"
          :plannedMaintenance="plannedMaintenance"
          :actualMaintenance="actualMaintenance"
          :isLoading="isLoading"
          @updateActualGrossTotal="updateActualGrossTotal"
        />
        <FContainer
          v-if="
            $validation.isEmpty(quotationList) && workorder.isQuotationNeeded
          "
        >
          <FContainer
            borderBottom="solid 1px"
            borderColor="borderNeutralBaseSubtler"
          >
          </FContainer>
          <FContainer
            padding="containerMedium"
            display="flex"
            justifyContent="flex-end"
          >
            <FButton
              @click="redirectToQuotationForm"
              appearance="secondary"
              size="large"
            >
              {{ $t('maintenance.wr_list.create_quote') }}
            </FButton>
          </FContainer>
        </FContainer>

        <!-- <iframe v-if="downloadUrl" :src="downloadUrl" class="hide"></iframe> -->
        <el-dialog
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
        <FContainer v-if="!$validation.isEmpty(quotationList)">
          <FContainer marginTop="containerXLarge">
            <FText appearance="headingMed14" color="textMain" fontWeight="bold">
              Quote
            </FText>
          </FContainer>
          <FContainer
            v-for="(record, index) in quotationList"
            :key="index"
            class="visibility-visible-actions"
          >
            <FContainer
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
            </FContainer>
            <FContainer display="flex" justifyContent="space-between">
              <FContainer
                marginTop="containerLarge"
                display="flex"
                alignItems="end"
                gap="containerLarge"
              >
                <FText color="textMain" appearance="bodyReg14">
                  {{ record.subject }}
                </FText>
                <FContainer>
                  <el-tag
                    class="fc-tag-status text-uppercase"
                    :class="getTicketStatusColor(record)"
                    >{{ getTicketStatusDetails(record).displayName }}</el-tag
                  >
                </FContainer>
              </FContainer>
              <FContainer class="visibility-hide-actions">
                <FContainer
                  alignItems="end"
                  gap="containerXLarge"
                  display="flex"
                >
                  <FContainer @click="openQuotationPreview(record)">
                    <InlineSvg src="svgs/dsm-view"></InlineSvg>
                  </FContainer>
                  <FContainer @click="downloadQuotation(record)">
                    <InlineSvg src="svgs/dsm-download"></InlineSvg>
                  </FContainer>
                  <FContainer @click="openQuotationSummary(record)">
                    <InlineSvg src="svgs/dsm-summary"></InlineSvg>
                  </FContainer>
                </FContainer>
              </FContainer>
            </FContainer>
            <FContainer
              display="flex"
              flexDirection="column"
              gap="containerLarge"
            >
              <FContainer
                display="flex"
                marginTop="containerXLarge"
                justifyContent="space-between"
              >
                <FText appearance="bodyReg14" color="textMain">
                  Total amount
                </FText>
                <FContainer>
                  <FText
                    appearance="headingMed14"
                    color="textMain"
                    fontWeight="bold"
                  >
                    <currency :value="record.totalCost"></currency>
                  </FText>
                </FContainer>
              </FContainer>

              <FContainer display="flex" justifyContent="space-between">
                <FText appearance="bodyReg14" color="textMain">
                  Bill date
                </FText>
                <FContainer v-if="!$validation.isEmpty(record.billDate)">
                  <FText appearance="headingMed14" color="textMain">
                    {{ record.billDate | formatDate(true) }}
                  </FText>
                </FContainer>
              </FContainer>
            </FContainer>
          </FContainer>
        </FContainer>
      </FContainer>
      <FContainer
        v-if="
          !$validation.isEmpty(quotationList) && workorder.isQuotationNeeded
        "
      >
        <FContainer
          borderBottom="solid 1px"
          borderColor="borderNeutralBaseSubtler"
        >
        </FContainer>
        <FContainer
          v-if="workorder.isQuotationNeeded"
          padding="containerMedium"
          display="flex"
          justifyContent="flex-end"
        >
          <FButton
            @click="redirectToQuotationForm"
            appearance="secondary"
            size="large"
          >
            {{ $t('maintenance.wr_list.create_quote') }}
          </FButton>
        </FContainer>
      </FContainer>
    </FContainer>
  </FContainer>
</template>
<script>
import {
  FContainer,
  FText,
  FButton,
  FTags,
  FDivider,
} from '@facilio/design-system'
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
import WOMaintenanceCostCard from 'src/beta/summary/widgets/WorkorderWidget/NewWOMaintenanceCostCard.vue'

export default {
  name: 'Quotation',
  components: {
    ApprovalBar,
    QuotationPreview,
    WOMaintenanceCostCard,
    FContainer,
    FText,
    FTags,
    FButton,
    FDivider,
  },
  mixins: [FetchViewsMixin],
  props: ['moduleName', 'details'],
  data() {
    return {
      quotationList: {},
      downloadUrl: null,
      isLoading: false,
      summaryLoading: true,
      dialogVisible: false,
      quotationSummaryDetails: {},
      plannedMaintenance: {
        plannedToolsCost: null,
        plannedItemsCost: null,
        plannedServicesCost: null,
        plannedLabourCost: null,
        grossAmount: null,
      },
      actualMaintenance: {
        itemCost: null,
        toolCost: null,
        serviceCost: null,
        grossAmount: null,
      },
    }
  },
  created() {
    this.workorder = this.details
    this.init()
    this.$store.dispatch('loadTicketStatus', 'quote')
  },
  computed: {
    ...mapGetters(['getTicketStatus']),
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Quotation'
    },
    workorder() {
      return this.details
    },
    showMaintenanceCost() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers || {}

      return (
        isLicenseEnabled('PLANNED_INVENTORY') && isLicenseEnabled('INVENTORY')
      )
    },
    showQuote() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers || {}

      return isLicenseEnabled('QUOTATION')
    },
    quoteTitle() {
      let { quotationList } = this
      return (quotationList || []).length > 1
        ? this.$t('maintenance.wr_list.quotes')
        : this.$t('maintenance.wr_list.quote')
    },
  },
  methods: {
    async init() {
      this.isLoading = true
      let { showMaintenanceCost, showQuote } = this
      if (showQuote) {
        await this.loadWorkorderQuotations()
      }
      if (showMaintenanceCost) {
        await this.loadPlansCost()
        await this.loadActualsCost()
      }
      this.isLoading = false
    },
    pdfUrl(record) {
      let appName = getApp().linkName
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
      console.log(this)
      let { workorder } = this
      let { id } = workorder || {}
      let relatedFieldName = getRelatedFieldName('workorder', 'quote')
      let relatedConfig = {
        moduleName: 'workorder',
        id,
        relatedModuleName: 'quote',
        relatedFieldName,
      }
      let { error, list } = await API.fetchAllRelatedList(
        relatedConfig,
        {},
        {
          force,
        }
      )

      if (!error) {
        if (!isEmpty(list)) {
          this.quotationList = list
        }
      } else {
        this.$message.error(error.message || 'Error Occured')
      }
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
    async loadPlansCost() {
      let { workorder } = this
      let { id } = workorder || {}
      let url = '/v3/workOrderPlansCost/cost'
      let params = {
        workOrderId: id,
      }
      let { data, error } = await API.get(url, params)

      if (error) {
        let { message } = error
        this.$message.error(message || this.$t('common._common.error_cost'))
      } else {
        let {
          plannedToolsCost,
          plannedItemsCost,
          plannedServicesCost,
          plannedLabourCost,
        } = data || {}
        let grossAmount =
          plannedItemsCost +
          plannedToolsCost +
          plannedServicesCost +
          plannedLabourCost

        this.plannedMaintenance = {
          Labour: plannedLabourCost,
          Items: plannedItemsCost,
          Tools: plannedToolsCost,
          Services: plannedServicesCost,
          grossAmount,
        }
      }
    },
    async loadActualsCost(force) {
      let { workorder } = this
      let { id } = workorder || {}
      let relatedFieldName = getRelatedFieldName('workorder', 'workorderCost')
      let relatedConfig = {
        moduleName: 'workorder',
        id,
        relatedModuleName: 'workorderCost',
        relatedFieldName,
      }
      let { error, list } = await API.fetchAllRelatedList(
        relatedConfig,
        {},
        {
          force,
        }
      )
      if (!isEmpty(error)) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        let customCostList = list.filter(
          element => element.costTypeEnum === 'custom'
        )
        let laboursCostObj = list.find(element => element.costName === 'Labour')
        let itemsCostObj = list.find(element => element.costName === 'Items')
        let toolsCostObj = list.find(element => element.costName === 'Tools')
        let servicesCostObj = list.find(
          element => element.costName === 'Service'
        )
        let labourCost = this.$getProperty(laboursCostObj, 'cost', 0)
        let itemCost = this.$getProperty(itemsCostObj, 'cost', 0)
        let toolCost = this.$getProperty(toolsCostObj, 'cost', 0)
        let serviceCost = this.$getProperty(servicesCostObj, 'cost', 0)

        let actualMaintenance = {
          Labour: labourCost,
          Items: itemCost,
          Tools: toolCost,
          Services: serviceCost,
          customCostList,
        }
        this.updateActualGrossTotal(actualMaintenance)
      }
    },
    updateActualGrossTotal(actualObj) {
      let { customCostList, Labour, Items, Tools, Services } = actualObj || {}
      let grossAmount = Labour + Items + Tools + Services
      let totalCostOfCustomCost = 0

      if (!isEmpty(customCostList)) {
        customCostList.forEach(element => {
          let { cost } = element || {}
          totalCostOfCustomCost += Number(cost)
        })
        grossAmount += totalCostOfCustomCost
      }
      actualObj = { ...actualObj, grossAmount }
      this.actualMaintenance = actualObj
    },
    getTicketStatusColor(record) {
      let statusObj = this.getTicketStatusDetails(record) || {}
      let { status } = statusObj || {}

      return status === 'Sent'
        ? 'fc-tag-status-bg-green'
        : 'fc-tag-status-bg-grey'
    },
  },
}
</script>
