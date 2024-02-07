<template>
  <div style="height: auto;">
    <div v-if="loading" class="text-center width100 pT50 mT50">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else>
      <div
        class="fc__layout__align fc__asset__main__header pT10 pB10 pL15 pR20 fc-visibility-gicon-visible fc-wo-translate-icon"
        style="width: auto; align-items: center !important; border-bottom: none;"
      >
        <div class="d-flex flex-direction-column">
          <div class="flex">
            <div
              class="wos-id mT7"
              @click="back"
              v-tippy
              :title="$t('common.header.back')"
            >
              <inline-svg
                src="arrow-pointing-to-left"
                data-position="top"
                width="14"
                height="14"
                class="vertical-middle mR5 mL3 pointer"
              />
            </div>
            <div class="jp-id">{{ jobPlanTag }}</div>
          </div>
          <div class="jp-name pT10">
            <el-tooltip effect="dark" :content="publishedState" placement="top">
              <fc-icon
                group="files"
                :name="iconTitle"
                :color="iconColor"
                class="pointer"
              ></fc-icon>
            </el-tooltip>
            <div
              class="max-width500px textoverflow-ellipsis show mL10"
              :title="JobPlanSubject"
              v-tippy
              data-arrow="true"
            >
              {{ JobPlanSubject }}
            </div>
            <div
              v-if="isStateFlowEnabled && currentModuleState"
              class="fc-badge text-uppercase inline vertical-middle mL15"
            >
              {{ currentModuleState }}
            </div>
          </div>
        </div>

        <div class="fR display-flex">
          <CustomButton
            class="p10"
            :record="jobPlan"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="loadJobPlan(true)"
            @onError="() => {}"
          />
          <template v-if="isStateFlowEnabled">
            <TransitionButtons
              class="mR10"
              :key="jobPlanId"
              :moduleName="moduleName"
              :record="jobPlan"
              :disabled="isApprovalEnabled"
              buttonClass="asset-el-btn"
              @currentState="() => {}"
              @transitionSuccess="loadJobPlan(true)"
              @transitionFailure="() => {}"
            ></TransitionButtons>
          </template>
          <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
            <ApprovalBar
              :moduleName="moduleName"
              :key="jobPlanId + 'approval-bar'"
              :record="jobPlan"
              :hideApprovers="shouldHideApprovers"
              @onSuccess="loadJobPlan(true)"
              @onFailure="() => {}"
              class="approval-bar-shadow"
            ></ApprovalBar>
          </portal>
          <portal to="pagebuilder-fixed-top" v-if="showJPBanner">
            <JPBanner
              :record="jobPlan"
              :moduleName="moduleName"
              :isLoading="loading"
              :stateUpdating="stateUpdating"
              @saveAction="saveAction"
            />
          </portal>
          <el-dropdown
            class="mL10 fc-btn-ico-lg pointer"
            style="padding-top: 5px; padding-bottom: 5px;"
            trigger="click"
            @command="action => summaryDropDownAction(action)"
            v-if="!isJobPlanDisabled"
          >
            <span class="el-dropdown-link">
              <inline-svg src="menu" width="16" height="16" />
            </span>
            <el-dropdown-menu slot="dropdown" trigger="click" class="p10">
              <el-dropdown-item
                :command="key"
                v-for="(key, label) in summaryButtonHash"
                :key="key"
                >{{ label }}</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <page
        v-if="jobPlan"
        :key="jobPlan.id"
        :module="moduleName"
        :notesModuleName="notesModuleName"
        :attachmentsModuleName="attachmentsModuleName"
        :id="jobPlanId"
        :isV3Api="true"
        :details="jobPlan"
        :primaryFields="[]"
      ></page>
      <JPWarningDialog
        v-if="showDialog"
        :showDialog="showDialog"
        :dialogType="dialogType"
        :moduleName="moduleName"
        :stateUpdating="stateUpdating"
        :recordId="jobPlanId"
        @closeDialog="closeDialog"
        @saveAction="saveAction"
      />
      <JPVersionHistory
        v-if="showVersionHistory"
        :jobPlanId="jobPlanId"
        @closeDialog="closeDialog"
      />
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import Page from '@/page/PageBuilder'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import CustomButton from '@/custombutton/CustomButton'
import TransitionButtons from '@/stateflow/TransitionButtons'
import ApprovalBar from '@/approval/ApprovalBar'
import { mapGetters } from 'vuex'
import JPBanner from './JPBanner'
import JPWarningDialog from './JPWarningDialog'
import JPVersionHistory from './JPVersionHistory'
import {
  PUBLISH_STATUS,
  PUBLISHED_STATUS,
  PUBLISHED_STATUS_ICON_HASH,
  PUBLISHED_STATUS_ICON_COLOR_HASH,
} from 'pages/workorder/pm/create/utils/pm-utils.js'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import router from 'src/router'

export default {
  name: 'JobPlanSummary',
  data() {
    return {
      jobPlan: {},
      loading: false,
      notesModuleName: 'jobplannotes',
      attachmentsModuleName: 'jobplanattachments',
      POSITION: POSITION_TYPE,
      primaryFields: [
        'jobPlanCategory',
        'sysCreatedTime',
        'sysCreatedBy',
        'associatedPm',
      ],
      showDialog: false,
      dialogType: null,
      stateUpdating: false,
      showVersionHistory: false,
      jobPlanId: null,
    }
  },
  components: {
    Page,
    CustomButton,
    TransitionButtons,
    ApprovalBar,
    JPBanner,
    JPWarningDialog,
    JPVersionHistory,
  },
  computed: {
    ...mapGetters(['getTicketStatus']),
    moduleName() {
      return 'jobplan'
    },
    shouldHideApprovers() {
      return false
    },
    showJPBanner() {
      let { jobPlan } = this
      let { jpStatus } = jobPlan || {}

      return ['Disabled', 'Unpublished', 'Pending Revision'].includes(
        PUBLISHED_STATUS[jpStatus]
      )
    },
    publishText() {
      let { isJobPlanPublished } = this || {}
      if (!isJobPlanPublished) {
        return this.$t('maintenance.pm.publish')
      } else {
        return this.$t('maintenance.pm.unpublish')
      }
    },
    publishedState() {
      let { jobPlan } = this
      let { jpStatus } = jobPlan || {}

      return this.$getProperty(PUBLISHED_STATUS, `${jpStatus}`, 'published')
    },
    iconTitle() {
      let { jobPlan } = this
      let { jpStatus } = jobPlan || {}

      return this.$getProperty(
        PUBLISHED_STATUS_ICON_HASH,
        `${jpStatus}`,
        'published'
      )
    },
    iconColor() {
      let { iconTitle } = this
      return this.$getProperty(
        PUBLISHED_STATUS_ICON_COLOR_HASH,
        `${iconTitle}`,
        'published'
      )
    },
    currentModuleState() {
      let { moduleName, jobPlan } = this
      let currentStateId = this.$getProperty(jobPlan, 'moduleState.id')
      let currentState = this.getTicketStatus(currentStateId, moduleName)
      let { displayName, status } = currentState || {}

      if (!isEmpty(displayName)) {
        return displayName
      }
      return status || null
    },
    groupId() {
      let { $route } = this
      let { params } = $route || {}
      let { id } = params || {}

      return parseInt(id)
    },
    version() {
      let { $route } = this
      let { query } = $route || {}
      let version = this.$getProperty(query, 'version', '')

      version = version.slice(1)
      return parseInt(version)
    },
    jobPlanTag() {
      let { groupId, version } = this
      return `#${groupId} / v${version}`
    },
    JobPlanSubject() {
      let { jobPlan } = this
      if (!isEmpty(jobPlan)) {
        let { name } = jobPlan || {}
        return name
      }
      return '---'
    },
    moduleDisplayName() {
      return 'Job Plan'
    },
    isJobPlanPublished() {
      let { jobPlan } = this
      if (!isEmpty(jobPlan)) {
        let { jpStatus } = jobPlan || {}
        return PUBLISHED_STATUS[jpStatus] === 'Published'
      }
      return false
    },
    canPublishJP() {
      let { jobPlan } = this
      if (!isEmpty(jobPlan)) {
        let { jpStatus } = jobPlan || {}
        return !['Published', 'Revised'].includes(PUBLISHED_STATUS[jpStatus])
      }
      return false
    },
    isJobPlanDisabled() {
      let { jobPlan } = this
      if (!isEmpty(jobPlan)) {
        let { jpStatus } = jobPlan || {}
        return PUBLISHED_STATUS[jpStatus] === 'Disabled'
      }
      return false
    },
    isStateFlowEnabled() {
      let { jobPlan } = this
      let hasState = this.$getProperty(jobPlan, 'moduleState.id')
      let isEnabled = this.$getProperty(
        this.moduleMeta,
        'module.stateFlowEnabled'
      )
      return hasState && isEnabled
    },
    isApprovalEnabled() {
      let { jobPlan } = this
      let { approvalFlowId, approvalStatus } = jobPlan || {}
      return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
    },
    summaryButtonHash() {
      let { canPublishJP, isJobPlanPublished, publishText } = this
      let editJP = this.$t('common._common.edit')
      let reviseJP = this.$t('jobplan.revise_jp')
      let cloneJP = this.$t('jobplan._clone_jp')
      let disableJP = this.$t('jobplan.disable')
      let versionHistory = this.$t('jobplan.version_history')
      let buttons = {}

      if (canPublishJP) {
        buttons = {
          ...buttons,
          [`${editJP}`]: 'edit',
          [`${publishText}`]: publishText,
        }
      }
      if (isJobPlanPublished) {
        buttons = {
          ...buttons,
          [`${reviseJP}`]: reviseJP,
          [`${disableJP}`]: disableJP,
        }
      }
      buttons = {
        ...buttons,
        [`${cloneJP}`]: cloneJP,
        [`${versionHistory}`]: versionHistory,
      }

      return buttons
    },
  },
  created() {
    this.init()
  },
  watch: {
    jobPlanId: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          this.loadJobPlan()
        }
      },
      immediate: true,
    },
  },
  methods: {
    async init() {
      this.loading = true
      let { groupId, version } = this
      let params = { groupId, jobPlanVersion: version }
      let { error, data } = await API.get('v3/jobPlan/getJobPlanId', params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { result } = data || {}
        this.jobPlanId = result
        this.loadJobPlan()
      }
      this.loading = false
    },
    async loadJobPlan(force = false, groupId) {
      this.loading = true
      let { moduleName, jobPlanId } = this
      let url = 'v3/modules/data/summary'
      let params = {
        moduleName,
        id: !isEmpty(groupId) ? groupId : jobPlanId,
        force,
      }
      let { error, data } = await API.get(url, params)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        if (!isEmpty(data)) {
          let { jobplan } = data || {}
          this.jobPlan = jobplan
        }
      }
      if (!isEmpty(groupId)) {
        this.jobPlanId = groupId
      }
      this.loading = false
    },
    back() {
      let { moduleName, viewname } = this
      let params = { viewname }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params,
          })
      } else {
        this.$router.push({ name: 'jobPlanList', params })
      }
    },
    summaryDropDownAction(action) {
      if (action === 'edit') {
        let { moduleName, groupId, version } = this
        version = `v${version}`
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
          name &&
            this.$router.push({
              name,
              params: { id: groupId },
              query: { version },
            })
        } else {
          this.$router.push({
            name: 'edit-jobplan',
            params: { id: groupId },
            query: { version },
          })
        }
      } else if (['Publish', 'Unpublish', 'Disable'].includes(action)) {
        this.showJPStatusUpdateDialog(action)
      } else if (action === 'Revise') {
        this.reviseJP()
      } else if (action === 'Version History') {
        this.openVersionHistory()
      } else if (action === 'Clone') {
        this.cloneJP()
      }
    },
    showJPStatusUpdateDialog(status) {
      this.showDialog = true
      this.dialogType = status
    },
    closeDialog() {
      this.showDialog = false
      this.showVersionHistory = false
      this.dialogType = null
    },
    async saveAction(status) {
      if (status === 'Publish') {
        await this.publishJP()
      } else if (status === 'Clone') {
        await this.cloneJP()
      } else {
        await this.updateStatus(status)
      }
    },
    async publishJP() {
      let { jobPlanId } = this
      let { error, data } = await API.post('v3/jobPlan/publish', {
        jobPlanId,
      })

      if (!error) {
        let { result } = data || {}
        let { groupId } = result || {}
        this.$message.success(this.$t('jobplan.jp_published'))
        this.stateUpdating = false
        this.loadPublishedJobPlan(groupId)
      } else {
        this.stateUpdating = false
        this.$message.error(error.message || 'Error Occurred')
      }
    },
    loadPublishedJobPlan(groupId) {
      this.closeDialog()
      this.loadJobPlan(true, groupId)
    },
    async updateStatus(status) {
      this.stateUpdating = true
      let { jobPlan } = this
      this.$set(jobPlan, 'jpStatus', PUBLISH_STATUS[`${status}`])
      let data = { ...jobPlan }
      let successMessage = `${this.moduleDisplayName} ${status}ed successfully`

      await this.updateRecord(data, successMessage)
    },
    async updateRecord(data, successMessage) {
      let { moduleName, jobPlanId } = this

      let { error } = await API.updateRecord(moduleName, {
        id: jobPlanId,
        data,
      })
      if (!error) {
        this.$message.success(successMessage)
        this.stateUpdating = false
        await this.loadJobPlan(true)
        this.closeDialog()
      } else {
        this.stateUpdating = false
        this.$message.error(error.message)
      }
    },
    async reviseJP() {
      let { moduleName, jobPlanId } = this
      let routeName = 'edit-jobplan'

      let { data, error } = await API.get('v3/jobPlan/revise', {
        jobPlanId,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
          routeName = name
        }
        let { result } = data || {}
        let { groupId, jobPlanVersion: version } = result || {}
        version = `v${version}`
        let params = { id: groupId }
        let query = { version }
        let { href } =
          router.resolve({
            name: routeName,
            params,
            query,
          }) || {}

        if (!isEmpty(href)) {
          window.open(href, '_blank', 'noopener,noreferrer')
        }
      }
    },
    async cloneJP() {
      let { moduleName, jobPlanId } = this
      let routeName = 'edit-jobplan'

      let { data, error } = await API.get('v3/jobPlan/cloneJobPlan', {
        jobPlanId,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
          routeName = name
        }
        let { result } = data || {}
        let { jobPlanId: id } = result || {}
        let params = { id }
        let query = { version: 'v1' }
        let { href } =
          router.resolve({
            name: routeName,
            params,
            query,
          }) || {}

        if (!isEmpty(href)) {
          window.open(href, '_blank', 'noopener,noreferrer')
        }
      }
    },
    openVersionHistory() {
      this.showVersionHistory = true
    },
  },
}
</script>
<style scoped>
.jp-name {
  font-size: 16px;
  color: #324056;
  font-weight: 500;
  display: flex;
  align-items: center;
}
.jp-id {
  color: #324056;
  height: 30px;
  background-color: #eeeff8;
  min-width: 50px;
  border-radius: 10px;
  padding: 8px;
  font-weight: bold;
  font-size: 12px;
}
</style>
