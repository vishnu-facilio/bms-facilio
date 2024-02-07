<template>
  <!-- OverWiriting For Header -->
  <div
    class="custom-modules-overview overflow-y-scroll"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="!isLoading" class="header pT10 pB15 pL20 pR20">
      <div v-if="!$validation.isEmpty(customModuleData)" class="asset-details">
        <div class="d-flex flex-middle">
          <div class="mL5">
            <div class="asset-id mT10">#{{ customModuleData.id }}</div>
            <div class="asset-name mb5 mT5">
              {{ customModuleData[mainFieldKey] }}
              <div
                v-if="currentModuleState"
                class="fc-badge text-uppercase inline vertical-middle mL15"
              >
                {{ currentModuleState }}
              </div>
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
        </el-button-group>
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
            :updateUrl="updateUrl"
            :transformFn="transformFormData"
            :disabled="isApprovalEnabled"
            buttonClass="asset-el-btn"
            @currentState="() => {}"
            @transitionSuccess="refreshObj()"
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
    ></page>
  </div>
</template>
<script>
import CustomModuleOverview from 'pages/base-module-v2/Overview'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: CustomModuleOverview,
  data() {
    return {
      updateUrl: `/v2/module/data/update`,
      downloadUrl: null,
    }
  },
  computed: {
    pdfUrl() {
      return (
        window.location.protocol +
        '//' +
        window.location.host +
        `/app/pdf/paymentmemopdf?paymentId=${this.id}`
      )
    },
    currentModuleState() {
      let { moduleName, customModuleData } = this
      let currentStateId = this.$getProperty(customModuleData, 'moduleState.id')
      let currentState = this.$store.getters.getTicketStatus(
        currentStateId,
        moduleName
      )
      let { displayName, status } = currentState || {}

      if (!isEmpty(displayName)) {
        return displayName
      }
      return status || null
    },
  },
  methods: {
    openPrintPreview() {
      window.open(this.pdfUrl)
    },
    downloadQuotation() {
      this.downloadUrl = null
      this.$message({
        message: 'Downloading...',
        showClose: true,
        duration: 0,
      })
      let additionalInfo = {
        showFooter: false,
        footerStyle: 'h1 {font-size:12px; color: #324056;text-align: right;}',
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
      })
    },
  },
}
</script>
