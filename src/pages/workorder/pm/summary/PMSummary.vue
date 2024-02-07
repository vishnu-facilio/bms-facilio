<template>
  <div class="custom-module-overview" :class="customClass">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header p20">
        <div class="custom-module-details">
          <div class="d-flex flex-middle align-center">
            <div v-if="showPhotoField" class="mR5">
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
            <div class="mL5">
              <div class="custom-module-id">
                <i
                  class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                  content="back"
                  arrow
                  v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                  @click="back"
                ></i>
                #{{ record && record.id }}
              </div>
              <div class="custom-module-name ">
                <div class="d-flex max-width300px">
                  <span class="whitespace-pre-wrap">{{
                    record[mainFieldKey]
                  }}</span>
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
          <portal to="pagebuilder-fixed-top" v-if="showPMBanner">
            <PMBanner
              :record="record"
              :moduleName="moduleName"
              :isLoading="isLoading"
              :stateUpdating="stateUpdating"
              @saveAction="saveAction"
            />
          </portal>
          <el-dropdown
            class="mL10 fc-btn-ico-lg pointer pT5 pB5"
            trigger="click"
            @command="handleActionClick"
          >
            <span class="el-dropdown-link">
              <img src="~assets/menu.svg" width="16" height="16" />
            </span>
            <el-dropdown-menu slot="dropdown" trigger="click">
              <el-dropdown-item command="Edit">{{
                $t('maintenance.pm.edit_record')
              }}</el-dropdown-item>
              <el-dropdown-item :command="publishText">{{
                publishText
              }}</el-dropdown-item>
              <el-dropdown-item command="navWorkorders">{{
                $t('maintenance.pm.nav_generated_workorders')
              }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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
      <PMWarningDialog
        v-if="showDialog"
        :showDialog="showDialog"
        :dialogType="dialogType"
        :moduleName="moduleName"
        :stateUpdating="stateUpdating"
        @closeDialog="closeDialog"
        @saveAction="saveAction"
      />
    </template>
  </div>
</template>

<script>
import Summary from 'pages/custom-module/CustomModuleSummary'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import PMBanner from 'pages/workorder/jobplan/JPBanner'
import PMWarningDialog from 'pages/workorder/jobplan/JPWarningDialog'
import { PUBLISHED_STATUS } from 'pages/workorder/pm/create/utils/pm-utils.js'

export default {
  name: 'PMSummary',
  extends: Summary,
  data: () => ({
    dialogType: null,
    showDialog: false,
    stateUpdating: false,
  }),
  computed: {
    publishText() {
      let { isPmActive } = this || {}
      if (!isPmActive) {
        return this.$t('maintenance.pm.publish')
      } else {
        return this.$t('maintenance.pm.unpublish')
      }
    },
    isPmActive() {
      let { record } = this || {}
      let { pmStatus } = record || {}
      return PUBLISHED_STATUS[pmStatus] === 'Published'
    },
    showPMBanner() {
      let { record } = this
      let { pmStatus } = record || {}

      return ['Disabled', 'Unpublished'].includes(PUBLISHED_STATUS[pmStatus])
    },
  },
  components: { PMBanner, PMWarningDialog },
  methods: {
    back() {
      let { moduleName, viewname, $route } = this
      let { search, page } = $route?.query || {}
      let query = { search, page }
      let params = { viewname }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        name && this.$router.push({ name, params, query })
      } else {
        this.$router.push({ name: 'pm-list', params, query })
      }
    },
    handleActionClick(command) {
      let { isPmActive } = this
      if (['Edit', 'Publish', 'Unpublish'].includes(command)) {
        if (command === 'Edit') {
          if (isPmActive) {
            this.showWarningDialog(command)
          } else {
            this.editRecord()
          }
        } else {
          this.showWarningDialog(command)
        }
      } else if (command === 'navWorkorders') {
        this.navigateToWorkorders()
      }
    },
    async editRecord() {
      let { moduleName, id } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        name && this.$router.push({ name, query: { id } })
      } else {
        this.$router.push({ name: 'pm-create', query: { id } })
      }
    },
    showWarningDialog(event) {
      this.showDialog = true
      this.dialogType = event
    },
    closeDialog() {
      this.showDialog = false
      this.dialogType = null
    },
    async publishPM() {
      this.stateUpdating = true
      let { id: pmId, isPmActive } = this
      let url = '/v3/plannedmaintenance/publish'
      let message = this.$t('maintenance.pm.published_successfully')

      if (isPmActive) {
        url = '/v3/plannedmaintenance/deactivate'
        message = this.$t('maintenance.pm.unpublished_successfully')
      }
      let { error } = await API.post(url, { pmId })
      if (isEmpty(error)) {
        this.$message.success(message)
        this.stateUpdating = false
        this.loadRecord(true)
        this.closeDialog()
      } else {
        this.stateUpdating = false
        this.$message.error(error.message || 'Error occured')
      }
    },
    saveAction(action) {
      if (['Publish', 'Unpublish'].includes(action)) this.publishPM()
      else if (action === 'Edit') this.editRecord()
    },
    navigateToWorkorders() {
      let { id } = this || {}
      let search = JSON.stringify({ pmV2: { operatorId: 9, value: [`${id}`] } })
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: { viewname: 'all' },
            query: { search },
          })
      } else {
        this.$router.push({
          name: 'workorderhomev1',
          params: { viewname: 'all' },
          query: { search },
        })
      }
    },
  },
}
</script>

<style lang="scss">
.pmv2-edit-dialog {
  .f-dialog-header {
    display: none;
  }
  .f-dialog-content {
    padding-top: 30px;
    padding-left: 30px;
    padding-right: 10px;
    min-height: 185px;
    width: 650px;
    text-align: justify;
  }
  .f-dialog-body {
    padding: 0px;
  }
  .del-cancel-btn {
    width: 50%;
  }
  .pmv2-edit-dialog-btn {
    width: 50%;
    background-color: #39b2c2 !important;
    border: transparent;
    margin-left: 0;
    padding-top: 20px;
    padding-bottom: 20px;
    border-radius: 0;
    font-size: 13px;
    font-weight: bold;
    letter-spacing: 1.1px;
    text-align: center;
    color: #ffffff;
    &:hover {
      background-color: #3cbfd0 !important;
    }
  }
}
</style>
