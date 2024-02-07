<template>
  <!-- OverWiriting For Header -->
  <div class="custom-module-overview" :class="customClass">
    <div
      v-if="!isLoading && !$validation.isEmpty(record)"
      class="header pT10 pB15 pL20 pR20"
    >
      <div v-if="!$validation.isEmpty(record)" class="custom-module-details">
        <div class="custom-module-id  mT10">
          <i
            class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
            content="back"
            arrow
            v-tippy="{ animateFill: false, animation: 'shift-toward' }"
            @click="back"
            v-if="$account.portalInfo"
          ></i>
          #{{ record && record.localId }}
        </div>
        <div class="d-flex flex-middle">
          <div v-if="showPhotoField">
            <div v-if="record[photoFieldName]">
              <img
                :src="record.getImage(photoFieldName)"
                class="img-container"
              />
            </div>
            <div v-else-if="showAvatar">
              <avatar size="lg" :user="{ name: record.name }"></avatar>
            </div>
          </div>
          <div>
            <!-- lock icon -->
            <i
              v-if="record.isRecordLocked()"
              class="fa fa-lock locked-wo mT10"
              data-arrow="true"
              :title="$t('common._common.locked_state')"
              v-tippy
            ></i>

            <div class="custom-module-name  mb5 mT10">
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
      <div class="d-flex flex-direction-row" style="margin-left: auto;">
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
        <CustomButton
          class="p10"
          :record="record"
          :moduleName="moduleName"
          :position="POSITION.SUMMARY"
          @refresh="refreshData()"
          @onError="() => {}"
        />
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

        <portal to="pagebuilder-sticky-top" v-if="record.isApprovalEnabled()">
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
        <el-dropdown
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
              v-if="$hasPermission(`${moduleName}:UPDATE`)"
              :key="1"
              command="associateterms"
              >{{ $t('common.header.associate_terms') }}</el-dropdown-item
            >
            <el-dropdown-item
              :key="2"
              v-if="hasEditPermission"
              command="edit"
              >{{ $t('common._common.edit') }}</el-dropdown-item
            >
            <el-dropdown-item :key="3" command="gotoreceivables">{{
              $t('common.products.go_to_receivables')
            }}</el-dropdown-item>
            <el-dropdown-item
              v-if="checkCompletePoPermissions"
              :key="4"
              command="completepo"
              >{{ $t('common.products.complete_po') }}</el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <page
      v-if="!isLoading && record && record.id"
      :key="record.id"
      :module="moduleName"
      :id="record.id"
      :details="record"
      :primaryFields="primaryFields"
      :notesModuleName="notesModuleName"
      :isV3Api="true"
      :attachmentsModuleName="attachmentsModuleName"
    ></page>
    <TermsAssociationDialog
      v-if="associateTermsDialogVisibility"
      :visibility.sync="associateTermsDialogVisibility"
      :record="record"
      :associateUrl="'v2/purchaseorder/associateTerms'"
      @refreshSummary="refreshData()"
    ></TermsAssociationDialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import { mapState } from 'vuex'
import { eventBus } from '@/page/widget/utils/eventBus'
import { isEmpty } from '@facilio/utils/validation'
import TermsAssociationDialog from 'src/pages/quotation/v1/TermsAssociationDialog'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
  getApp,
} from '@facilio/router'

export default {
  extends: CustomModuleSummary,
  components: {
    TermsAssociationDialog,
  },
  props: ['viewname'],
  data() {
    return {
      notesModuleName: 'purchaseordernotes',
      attachmentsModuleName: 'purchaseorderattachments',
      primaryFields: ['subject', 'billToAddress', 'shipToAddress'],
      associateTermsDialogVisibility: false,
      downloadUrl: null,
    }
  },
  computed: {
    moduleName() {
      return 'purchaseorder'
    },
    ...mapState({
      ticketStatus: state => state.ticketStatus.purchaseorder,
    }),
    pdfUrl() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(
          this.moduleName,
          pageTypes.MODULE_CUSTOM
        )
        if (name) {
          let resolvedPath = this.$router.resolve({ name }).href || ''
          return this.$router.resolve({
            path: `${resolvedPath}/popdf`,
            query: { poId: this.id },
          }).href
        } else return null
      }
      return `${window.location.protocol}//${window.location.host}/app/pdf/popdf?poId=${this.id}`
    },
    checkCompletePoPermissions() {
      let { completedTime, receivableStatusEnum } = this.record
      return (
        this.$hasPermission(`${this.moduleName}:UPDATE`) &&
        !this.record.isRecordLocked() &&
        !this.record.isApprovalEnabled() &&
        isEmpty(completedTime) &&
        receivableStatusEnum !== 'RECEIVED'
      )
    },
    hasEditPermission() {
      let { receivableStatusEnum, completedTime } = this.record || {}
      return (
        this.$hasPermission(`${this.moduleName}:UPDATE`) &&
        this.record.canEdit() &&
        receivableStatusEnum === 'YET_TO_RECEIVE' &&
        isEmpty(completedTime)
      )
    },
  },
  title() {
    'Purchase Order'
  },
  mounted() {
    eventBus.$on('refesh-parent', this.refreshObj)
  },
  beforeDestroy() {
    eventBus.$off('refesh-parent', this.refreshObj)
  },
  watch: {
    record: {
      handler(oldVal, newVal) {
        if (oldVal !== newVal) this.downloadUrl = null
      },
    },
  },
  methods: {
    editRecord() {
      let { id } = this.record
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id,
              viewname: this.viewname,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: `purchaseorder-edit`,
        })
      }
    },
    async summaryDropDownAction(action) {
      if (action === 'edit') {
        this.editRecord(this.record)
      } else if (action === 'associateterms') {
        this.associateTermsDialogVisibility = true
      } else if (
        action === 'gotoreceivables' &&
        this.$getProperty(this.record, 'receivableContext.id')
      ) {
        let id = this.$getProperty(this, 'record.receivableContext.id')

        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('receivable', pageTypes.OVERVIEW) || {}
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
            path: `/app/purchase/rv/all/summary/${id}`,
          })
        }
      } else if (action === 'completepo') {
        let { moduleName, record } = this

        let value = await this.$dialog.confirm({
          title: this.$t(`common._common.complete_purchase_order`),
          message: this.$t(`common._common.complete_confirmation`),
          rbDanger: true,
          rbLabel: this.$t('common._common._complete'),
        })

        if (value) {
          let { error } = await API.updateRecord(moduleName, {
            id: record.id,
            data: record,
            params: {
              completePO: true,
            },
          })

          if (error) {
            this.$message.error(
              error.message || this.$t(`common._common.error_complete_po`)
            )
          } else {
            this.$message.success(this.$t(`common._common.success_complete_po`))
            this.loadRecord()
          }
        }
      }
    },
    openPrintPreview() {
      if (this.pdfUrl) window.open(this.pdfUrl)
    },
    downloadPdf() {
      this.downloadUrl = null
      let appName = getApp().linkName
      let url = `${window.location.protocol}//${window.location.host}/${appName}/pdf/popdf?poId=${this.id}`
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
              'common.wo_report.unable_to_fetch_purchase_order_download_link'
            ),
          } = error
          this.$message.error(message)
        } else {
          let { fileUrl } = data || {}
          this.downloadUrl = fileUrl || null
        }
      })
    },
  },
}
</script>
