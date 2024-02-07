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
              <div class="d-flex max-width300px custom-header">
                <el-tooltip
                  placement="bottom"
                  effect="dark"
                  :content="record[mainFieldKey]"
                >
                  <span class="whitespace-pre-wrap">{{
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
        <el-button
          v-if="convertPrToRfqVisibility"
          type="button"
          class="fc-wo-border-btn pL15 pR15 mR10 self-center"
          @click="convertPrToRfq()"
        >
          {{ $t('common.products.convert_to_rfq') }}
        </el-button>
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
          v-if="$hasPermission(`${moduleName}:UPDATE`)"
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
            <el-dropdown-item :key="1" :command="'associateterms'">{{
              $t('common.header.associate_terms')
            }}</el-dropdown-item>
            <el-dropdown-item
              :key="2"
              v-if="hasEditPermission"
              :command="'edit'"
              >{{ $t('common._common.edit') }}</el-dropdown-item
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
      :attachmentsModuleName="attachmentsModuleName"
      :isV3Api="true"
    ></page>
    <TermsAssociationDialog
      v-if="associateTermsDialogVisibility"
      :visibility.sync="associateTermsDialogVisibility"
      :record="record"
      :associateUrl="'v2/purchaserequest/associateTerms'"
      @refreshSummary="refreshData()"
    ></TermsAssociationDialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import { mapState } from 'vuex'
import { eventBus } from '@/page/widget/utils/eventBus'
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
  data() {
    return {
      notesModuleName: 'purchaserequestnotes',
      attachmentsModuleName: 'purchaserequestattachments',
      primaryFields: ['subject', 'billToAddress', 'shipToAddress'],
      associateTermsDialogVisibility: false,
      downloadUrl: null,
    }
  },
  computed: {
    moduleName() {
      return 'purchaserequest'
    },
    ...mapState({
      ticketStatus: state => state.ticketStatus.purchaserequest,
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
            path: `${resolvedPath}/prpdf`,
            query: { prId: this.id },
          }).href
        } else return null
      } else {
        return `${window.location.protocol}//${window.location.host}/app/pdf/prpdf?prId=${this.id}`
      }
    },
    hasEditPermission() {
      return (
        this.$hasPermission(`${this.moduleName}:UPDATE`) &&
        this.record.canEdit()
      )
    },
    convertPrToRfqVisibility() {
      return (
        this.$hasPermission(`requestForQuotation:CREATE`) &&
        this.$helpers.isLicenseEnabled('REQUEST_FOR_QUOTATION')
      )
    },
  },
  title() {
    'Purchase Request'
  },
  mounted() {
    eventBus.$on('refesh-parent', this.refreshData)
  },
  beforeDestroy() {
    eventBus.$off('refesh-parent', this.refreshData)
  },
  watch: {
    record: {
      handler(oldVal, newVal) {
        if (oldVal !== newVal) this.downloadUrl = null
      },
    },
  },
  methods: {
    convertPrToRfq() {
      if (isWebTabsEnabled()) {
        let { viewname } = this
        let { name } = findRouteForModule(
          'requestForQuotation',
          pageTypes.CREATE
        )
        name &&
          this.$router.push({
            name,
            params: {
              viewname: viewname,
            },
            query: {
              isConvertPr: true,
              recordIds: JSON.stringify([this.id]),
            },
          })
      } else {
        this.$router.push({
          name: `new-requestForQuotation`,
          params: {
            viewname: 'all',
          },
          query: {
            isConvertPr: true,
            recordIds: JSON.stringify([this.id]),
          },
        })
      }
    },
    redirectToRfqList() {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('requestForQuotation', pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'requestForQuotation',
        })
      }
    },
    editRecord() {
      let { id, viewname } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.EDIT) || {}
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
          name: `purchaserequest-edit`,
          params: {
            viewname,
            id,
          },
          query: this.$route.query,
        })
      }
    },
    summaryDropDownAction(action) {
      if (action === 'edit') {
        this.editRecord(this.record)
      } else if (action === 'associateterms') {
        this.associateTermsDialogVisibility = true
      }
    },
    openPrintPreview() {
      if (this.pdfUrl) window.open(this.pdfUrl)
    },
    downloadPdf() {
      this.downloadUrl = null
      let appName = getApp().linkName
      let url = `${window.location.protocol}//${window.location.host}/${appName}/pdf/prpdf?prId=${this.id}`

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
              'common.wo_report.unable_to_fetch_purchase_request_download_link'
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
