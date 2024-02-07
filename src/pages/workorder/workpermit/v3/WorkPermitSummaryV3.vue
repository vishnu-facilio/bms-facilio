<template>
  <!-- OverWiriting For Header -->
  <div
    class="custom-modules-overview overflow-y-scroll"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="!isLoading" class="header pT10 pB15 pL20 pR20">
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
              #{{ customModuleData.localId }}
            </div>
          </div>
        </div>
      </div>
      <div
        class="d-flex flex-direction-row flex-middle"
        style="margin-left: auto;"
      >
        <CustomButton
          class="pR10"
          :record="customModuleData"
          :moduleName="moduleName"
          :position="POSITION.SUMMARY"
          @refresh="refreshObj()"
          @onError="() => {}"
        />
        <el-button-group class="fc-group-btn2 flex-middle">
          <el-button type="primary" @click="downloadRecord()">
            <inline-svg
              src="svgs/download2"
              iconClass="icon vertical-middle icon-sm fill-grey2"
            ></inline-svg>
          </el-button>
          <el-button type="primary" @click="openPrintPreview()">
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
          v-if="!isRecordLocked"
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
            <el-dropdown-item :command="'edit'">Edit</el-dropdown-item>
            <el-dropdown-item
              v-if="!(customModuleData || {}).isPreValidationDone"
              :command="1"
              >Review Prerequisites</el-dropdown-item
            >
            <el-dropdown-item
              v-if="
                (customModuleData || {}).isPreValidationDone &&
                  !(customModuleData || {}).isPostValidationDone
              "
              :command="2"
              >Permit Closeout</el-dropdown-item
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
    <ChecklistValidation
      v-if="checklistDialogVisibility"
      :visibility.sync="checklistDialogVisibility"
      :details="customModuleData"
      :validationType="validationType"
      @saved="refreshObj()"
    ></ChecklistValidation>
  </div>
</template>
<script>
import CustomModuleOverview from 'pages/base-module-v2/Overview'
import { mapState, mapGetters } from 'vuex'
import { API } from '@facilio/api'
import { eventBus } from '@/page/widget/utils/eventBus'
import { isEmpty } from '@facilio/utils/validation'
import ChecklistValidation from './ChecklistValidation'

import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
  getApp,
} from '@facilio/router'
export default {
  extends: CustomModuleOverview,
  components: { ChecklistValidation },
  data() {
    return {
      notesModuleName: 'workpermitnotes',
      attachmentsModuleName: 'workpermitattachments',
      primaryFields: ['name'],
      recordDetails: null,
      associateTermsDialogVisibility: false,
      downloadUrl: null,
      validationType: null,
      checklistDialogVisibility: false,
    }
  },
  computed: {
    moduleName() {
      return 'workpermit'
    },
    ...mapGetters(['getTicketStatus']),
    name() {
      return 'subject'
    },
    customModuleData() {
      return this.recordDetails
    },
    ...mapState({
      ticketStatus: state => state.ticketStatus.workpermit,
    }),
    pdfUrl() {
      let { linkName } = getApp() || {}

      return (
        window.location.protocol +
        '//' +
        window.location.host +
        `/${linkName}/pdf/workpermit?id=${this.id}`
      )
    },
    moduleStateId() {
      return this.$getProperty(this, 'customModuleData.moduleState.id')
    },
    canShowRevise() {
      let status = this.getTicketStatus(this.moduleStateId, this.moduleName)
      if (status && status.status === 'Sent') {
        return true
      }
      return false
    },
  },
  title() {
    'Work Permit'
  },
  mounted() {
    eventBus.$on('refesh-parent', this.refreshObj)
  },
  beforeDestroy() {
    eventBus.$off('refesh-parent', this.refreshObj)
  },
  methods: {
    refreshObj(refreshList) {
      this.loadCustomModuleData()
      if (refreshList) {
        this.$emit('refreshSummaryList', true)
      }
    },
    async loadCustomModuleData() {
      this.isLoading = true
      let { workpermit, error } = await API.fetchRecord(
        this.moduleName,
        {
          id: this.id,
        },
        { force: true } // temp call only for update
      )
      if (!isEmpty(error)) {
        let { message = 'Error Occured while fetching Work Permit' } = error
        this.$message.error(message)
      } else {
        this.recordDetails = workpermit
      }
      this.isLoading = false
    },
    editCustomModuleData() {
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
        this.$router.push({
          name: 'edit-workpermit',
          params: { id },
        })
      }
    },
    transformFormData(returnObj, data) {
      returnObj['workpermit'] = { ...returnObj['workpermit'], ...data }
      return returnObj
    },
    summaryDropDownAction(action) {
      if (action === 'edit') {
        this.editCustomModuleData(this.customModuleData)
      } else if ([1, 2].includes(action)) {
        this.checklistDialogVisibility = true
        this.validationType = action
      }
    },
    openPrintPreview() {
      window.open(this.pdfUrl)
    },
    downloadRecord() {
      this.downloadUrl = null
      this.$message({
        message: 'Downloading...',
        showClose: true,
        duration: 0,
      })
      API.post(`/v2/integ/pdf/create`, { url: this.pdfUrl }).then(
        ({ data, error }) => {
          this.$message.closeAll()
          if (error) {
            let { message = 'Unable to fetch download link' } = error
            this.$message.error(message)
          } else {
            let { fileUrl } = data || {}
            this.downloadUrl = fileUrl || null
          }
        }
      )
    },
    sendMail() {
      this.$router.push({
        path: `/app/wo/workpermit/sendmail/${this.id}`,
      })
    },
  },
}
</script>
