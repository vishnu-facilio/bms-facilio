<template>
  <div style="height: auto">
    <div v-if="loading" class="text-center width100 pT50 mT50">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else>
      <div
        class="
          fc__layout__align fc__asset__main__header
          pT20
          pB10
          pL20
          pR20
          fc-visibility-gicon-visible fc-wo-translate-icon
        "
        style="width: auto; align-items: center !important; border-bottom: none"
      >
        <div class="fL pointer">
          <!-- id & back icon -->
          <div class="wos-id" @click="back" :title="$t('common.header.back')">
            <img
              src="~assets/arrow-pointing-to-left.svg"
              data-position="top"
              v-tippy="{ arrow: true, animation: 'perspective' }"
              width="12"
              height="12"
              class="vertical-middle mR5 mL3 pointer"
            />
            <span class="fc-id"
              >#{{ workorder ? workorder.serialNumber : '---' }}</span
            >
          </div>

          <!-- subject & state -->
          <div class="heading-black22 pT5 flex-middle">
            <!-- lock icon -->
            <i
              v-if="
                workorder &&
                  workorder.moduleState &&
                  workorder.moduleState.id &&
                  isStatusLocked(workorder.moduleState.id, 'workorder')
              "
              class="fa fa-lock locked-wo"
              data-arrow="true"
              :title="$t('maintenance.approval.waiting_for_approval')"
              v-tippy
            ></i>

            <!-- subject -->
            <div
              class="max-width500px textoverflow-ellipsis show"
              :title="workorder.subject"
              v-tippy
              data-arrow="true"
            >
              {{ workorder && workorder.subject ? workorder.subject : '---' }}
            </div>
            <!-- state -->
            <span class="fc-newsummary-chip">
              {{ moduleStateName }}
            </span>
          </div>
        </div>

        <div class="fR display-flex">
          <div v-if="$helpers.isLicenseEnabled('GOOGLE_TRANSLATION')">
            <div
              @click="handleTranslate"
              class="gTranslate-icon-place fc-gtransalte"
              v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
              content="Show translation for text in subject,description, comments & tasks"
              :class="{ translateActive: isActive }"
            >
              <inline-svg
                src="svgs/google-translate"
                iconClass="icon text-center icon-md vertical-middle"
              ></inline-svg>
            </div>
          </div>

          <CustomButton
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            :record="workorder"
            @refresh="onTransitionSuccess"
            :transformFn="transformFormData"
            :updateUrl="updateUrlString"
          />

          <TransitionButtons
            ref="TransitionButtons"
            :moduleName="moduleName"
            :record="workorder"
            :transformFn="transformFormData"
            :updateUrl="updateUrlString"
            :transitionFilter="transitionFilter"
            :disabled="isApprovalEnabled"
            buttonClass="asset-el-btn"
            @currentState="id => setCurrentState(id)"
            @transitionSuccess="onTransitionSuccess"
            @transitionFailure="() => {}"
          ></TransitionButtons>

          <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
            <ApprovalBar
              v-if="isApprovalEnabled"
              :transformFn="transformFormData"
              :moduleName="moduleName"
              :record="workorder"
              canShowEdit="true"
              @onSuccess="onTransitionSuccess"
              @onFailure="() => {}"
              @onEdit="
                () => {
                  this.$root.$emit('editWorkorder')
                }
              "
            ></ApprovalBar>
          </portal>
          <portal
            to="pagebuilder-fixed-top"
            v-if="
              checkIfSurveyLicenseEnabled &&
                isSurveyEnabled &&
                !isApprovalEnabled
            "
          >
            <SurveyBar
              :surveyDetails="surveyDetails"
              moduleName="workorder"
              :recordId="woId"
            />
          </portal>
          <!-- Note: these banner messages can be moved to respective tabs,
                once vendor portal summary pages is moved to use page builder
                and for plans/actuals additional widget entry might require to
                add banner-message on top. -->
          <!-- TODO: WORK_ORDER_FEATURE_SETTINGS license check -->
          <portal
            to="pagebuilder-fixed-top"
            v-if="
              isWorkOrderFeatureSettingsLicenseEnabled && canShowMessageBanner
            "
          >
            <div class="wo_task_view_banner pT5">
              <span class="wo_task_view_banner_subject">
                <fc-icon group="action" name="info"></fc-icon>
                <span class="pL5">{{ bannerMessage }}</span></span
              >
            </div>
          </portal>

          <!-- TODO: WORK_ORDER_FEATURE_SETTINGS license check -->
          <!-- below banner message can be removed when WorkOrderFeature license is fully-enabled to all orgs -->
          <!-- -- can be removed later -- -->
          <portal
            to="pagebuilder-fixed-top"
            v-else-if="
              !isWorkOrderFeatureSettingsLicenseEnabled &&
                isTasksTab &&
                !hasExecuteTaskPermission
            "
          >
            <div class="wo_task_view_banner pT5">
              <span class="wo_task_view_banner_subject">
                <fc-icon group="action" name="info"></fc-icon>
                <span class="pL5">{{
                  $t('common.products.workorder_no_update_task')
                }}</span></span
              >
            </div>
          </portal>
          <!-- -- can be removed later -- -->

          <!-- elipsis dropdown -->
          <el-dropdown
            class="mL10 fc-btn-ico-lg pointer"
            style="padding-top: 5px; padding-bottom: 5px"
            trigger="click"
          >
            <span class="el-dropdown-link">
              <img src="~assets/menu.svg" width="16" height="16" />
            </span>
            <el-dropdown-menu slot="dropdown" trigger="click" class="p10">
              <div
                class="
                  pT5
                  pB5
                  fc-label-hover
                  label-txt-black
                  f14
                  pL10
                  pR10
                  pointer
                "
                @click="openPrintOptionDialog()"
              >
                {{ $t('common._common.print') }}
              </div>
              <div
                class="
                  pT5
                  pB5
                  fc-label-hover
                  label-txt-black
                  f14
                  pL10
                  pR10
                  pointer
                "
                @click="openDownloadOptionDialog()"
              >
                {{ $t('common._common.download') }}
              </div>
              <div
                v-if="canShowReportDownTime"
                class="
                  pT5
                  pB5
                  fc-label-hover
                  label-txt-black
                  f14
                  pL10
                  pR10
                  pointer
                "
                @click="
                  openAssetBreakDown(workorder.resource.id, workorder.subject)
                "
              >
                {{ $t('maintenance._workorder.report_downtime') }}
              </div>
              <div
                v-if="enablePrerequisiteApprove"
                class="
                  pT5
                  pB5
                  fc-label-hover
                  label-txt-black
                  f14
                  pL10
                  pR10
                  pointer
                "
                @click="preRequisiteApprove()"
              >
                {{ $t('maintenance._workorder.prerequisite_approve') }}
              </div>
              <div
                v-if="isTutenLabs && isWorkOrderEditable()"
                class="
                  pT5
                  pB5
                  fc-label-hover
                  label-txt-black
                  f14
                  pL10
                  pR10
                  pointer
                "
                @click="openEditWODialog()"
              >
                {{ $t('common._common.edit') }}
              </div>

              <WoV3EditForm
                v-if="editFormVisibilty"
                moduleName="workorder"
                :visibility="editFormVisibilty"
                :wo="workorder"
                @visibilityUpdate="handleUpdateVisibility"
              >
              </WoV3EditForm>
              <div
                v-if="showViewAllSurvey"
                class="
                  pT5
                  pB5
                  fc-label-hover
                  label-txt-black
                  f14
                  pL10
                  pR10
                  pointer"
                @click="showSurveyDialog = true"
              >
                {{ $t('survey.take_other_surveys') }}
              </div>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <ViewAllSurveysDialog
        :showSurveyDialog.sync="showSurveyDialog"
        :recordId="woId"
        :moduleName="'workorder'"
        :recordName="workOrderName"
      />
      <page
        v-if="!loading && woBundle && woBundle.workorder"
        :key="woBundle ? woBundle.workorder.id : -1"
        :module="moduleName"
        :moduleDisplayName="moduleDisplayName"
        :recordName="workOrderName"
        :id="woBundle ? woBundle.workorder.id : -1"
        :details="woBundle"
        :primaryFields="primaryFields"
        notesModuleName="ticketnotes"
        attachmentsModuleName="ticketattachments"
        :isV3Api="true"
      ></page>

      <workorder-options
        v-if="openTaskOptions"
        :workorders="[woBundle && woBundle.workorder.id]"
        :visibility.sync="openTaskOptions"
        :viewName="viewname"
        :isDownloadOption="isDownloadOption"
        :pdfUrl.sync="pdfUrl"
        :isPdfDownload="isPdfDownload"
      ></workorder-options>

      <pdf-download
        :url="pdfUrl"
        :isDownload.sync="isPdfDownload"
      ></pdf-download>

      <asset-breakdown
        v-if="showAssetBreakdown"
        :assetBDSourceDetails="assetBDSourceDetails"
        :visibility.sync="showAssetBreakdown"
      ></asset-breakdown>
    </div>
  </div>
</template>
<script>
import WoV3EditForm from 'src/pages/workorder/workorders/v3/WoV3EditForm.vue'
import { mapState, mapGetters } from 'vuex'
import { eventBus } from '@/page/widget/utils/eventBus'
import { API } from '@facilio/api'
import Page from 'src/beta/summary/PageBuilder.vue'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import workorderOptions from 'pages/workorder/workorders/v1/WorkOrderSummaryPdfOptions'
import Spinner from '@/Spinner'
import CustomButton from '@/custombutton/CustomButton'
import TransitionButtons from '@/stateflow/TransitionButtons'
import { isEmpty } from '@facilio/utils/validation'
import transformMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper.js'
import ApprovalBar from '@/approval/ApprovalBar'
import SurveyBar from 'pages/setup/survey/SurveyBar'
import ViewAllSurveysDialog from 'pages/setup/survey/ViewAllSurveys'
import { loadSurveyForSurveyBar } from 'pages/setup/survey/SurveyUtil'
import AssetBreakdown from '@/AssetBreakdown'
import utils from 'pages/workorder/workorders/v3/mixins/utils'
import { getApp, isWebTabsEnabled } from '@facilio/router'
import PdfDownload from 'src/components/PDFDownload'
import debounce from 'lodash/debounce'
import surveyMixin from '../../../setup/survey/SurveyMixin'

export default {
  name: 'WorkOrderOverview',
  props: ['viewname'],
  components: {
    Page,
    Spinner,
    workorderOptions,
    CustomButton,
    TransitionButtons,
    ApprovalBar,
    SurveyBar,
    ViewAllSurveysDialog,
    AssetBreakdown,
    WoV3EditForm,
    PdfDownload,
  },

  mixins: [transformMixin, utils, surveyMixin],

  mounted() {
    this.$root.$on('reloadWO', async res => {
      if (res) {
        this.woBundle.workorder = res.wo
      } else {
        this.loadWorkorderSummary()
        this.surveyDetails = await loadSurveyForSurveyBar(
          false,
          this.moduleName,
          this.woId,
          this.surveyDetails
        )
      }
    })
  },
  async created() {
    this.loadWorkorderSummary()
    this.surveyDetails = await loadSurveyForSurveyBar(
      false,
      this.moduleName,
      this.woId,
      this.surveyDetails
    )
    this.$store.dispatch('loadTicketStatus', 'asset')
    this.$store.dispatch('loadTicketStatus', 'assetmovement')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAssetType')
    this.$store.dispatch('loadApprovalStatus')

    eventBus.$on('refesh-parent', () => this.refreshObj())
    eventBus.$on('refreshWorkOrderPage', () =>
      this.debounceLoadWorkOrderSummary()
    )
    eventBus.$on('refresh-overview', this.refreshObj)
    eventBus.$on('stateflows', this.loadStateflows)
    eventBus.$on('woDescriptiontranslate', woDescriptiontranslate => {
      this.woDescriptiontranslate = woDescriptiontranslate
    })
  },
  beforeDestroy() {
    eventBus.$off('refresh-overview', this.refreshObj)
  },
  destroyed() {
    eventBus.$off('woDescriptiontranslate', () => {
      this.woDescriptiontranslate = false
    })
    eventBus.$off('refreshWorkOrderPage')
  },
  data() {
    return {
      moduleName: 'workorder',
      woBundle: null,
      loading: true,

      isActive: false,
      stateflows: [],
      primaryFields: [
        'name',
        'description',
        'category',
        'type',
        'siteId',
        'space',
      ],
      POSITION: POSITION_TYPE,
      openTaskOptions: false,

      // asset breakdown reporting specific
      showAssetBreakdown: false,
      assetBDSourceDetails: {},

      editFormVisibilty: false,
      isDownloadOption: false,
      pdfUrl: '',
      workOrderSettingsRelatedData: {},
    }
  },
  watch: {
    woId: {
      async handler(newVal) {
        if (newVal) this.refreshObj()
      },
      immediate: true,
    },
  },
  computed: {
    ...mapState({
      ticketStates: state => state.ticketStatus.asset,
      moduleMeta: state => state.view.metaInfo,
      hasWorkOrderClosePermission() {
        if (isWebTabsEnabled()) {
          return this.$hasPermission(
            `${this.moduleName}:UPDATE_CLOSE_WORKORDER`
          )
        }
        return this.$hasPermission(`${this.moduleName}:CLOSE_WORK_ORDER`)
      },
    }),
    moduleDisplayName() {
      let { moduleMeta } = this
      let { displayName } = moduleMeta || {}
      return displayName
    },
    isTutenLabs() {
      const TUTEN_LABS = 592
      const currentOrgID = this.$org.id
      return currentOrgID && currentOrgID === TUTEN_LABS
    },
    ...mapGetters(['getAssetCategory', 'isStatusLocked']),
    currentModuleName() {
      return 'workorder'
    },
    isWoStateTransitionLicenseEnabled() {
      return this.$helpers.isLicenseEnabled('WO_STATE_TRANSITION_V3')
    },
    updateUrlString() {
      if (!this.isWoStateTransitionLicenseEnabled) {
        return 'v2/workorders/update'
      }
      return null
    },
    woId() {
      return this.$route.params.id
    },
    isStateFlowEnabled() {
      return Boolean(
        this.woBundle &&
          this.woBundle.workorder.moduleState &&
          this.woBundle.workorder.moduleState.id
      )
    },
    // currentModuleState() {
    //   let currentStateId =
    //     this.woBundle &&
    //     this.woBundle.workorder &&
    //     this.woBundle.workorder.moduleState.id

    //   let currentState =
    //     this.ticketStates &&
    //     this.ticketStates.find(state => state.id === currentStateId)

    //   return currentState ? currentState.displayName : null
    // },
    workorderState() {
      return 'wos'
    },
    moreOptions() {
      return (
        this.$hasPermission('asset:UPDATE') ||
        this.$hasPermission('asset:CREATE')
      )
    },
    workorder() {
      const wo = this?.woBundle?.workorder
      return wo ? wo : {}
    },
    canShowReportDownTime() {
      let { workorder, isNotPortal } = this
      let { resource } = workorder || {}
      let { resourceType, id } = resource || {}
      const ASSET = 2
      return isNotPortal && resourceType === ASSET && id !== -1
    },
    workOrderName() {
      let { woBundle } = this
      let { workorder } = woBundle || {}
      let { subject } = workorder || {}
      if (!isEmpty(subject)) {
        return subject
      }
      return '---'
    },
    enablePrerequisiteApprove() {
      return (
        this.woBundle &&
        this.woBundle.workorder.prerequisiteEnabled &&
        this.woBundle.workorder.allowNegativePreRequisite &&
        this.woBundle.workorder.allowNegativePreRequisite === 3 &&
        this.woBundle.workorder.prerequisiteApprover &&
        !this.woBundle.workorder.preRequisiteApproved &&
        this.woBundle.workorder.preRequestStatus === 2
      )
    },
    isApprovalEnabled() {
      let { approvalFlowId, approvalStatus } = this.woBundle.workorder || {}
      return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
    },
    hasExecuteTaskPermission() {
      // hasUpdateTaskPermission changed to hasExecuteTaskPermission
      let { linkName } = getApp() || {}
      if (linkName === 'newapp') {
        return true
      } else {
        return this.$hasPermission('workorder:UPDATE_WORKORDER_TASK')
      }
    },
    hasManageTaskPermission() {
      let { linkName } = getApp() || {}
      if (linkName === 'newapp') {
        return true
      } else {
        return this.$hasPermission('workorder:UPDATE_TASK')
      }
    },
    isTasksTab() {
      let tabName = this.$getProperty(this, '$route.query.tab')
      return tabName === 'tasks'
    },
    isPlansTab() {
      let tabName = this.$getProperty(this, '$route.query.tab')
      return tabName === 'plans'
    },
    isActualsTab() {
      let tabName = this.$getProperty(this, '$route.query.tab')
      return tabName === 'Actuals'
    },
    bannerMessage() {
      let message = null
      let {
        isTasksTab,
        isPlansTab,
        isActualsTab,
        workOrderSettingsRelatedData,
      } = this

      let { workOrderSettings } = workOrderSettingsRelatedData || {}

      // Tasks tab conditional messages
      if (isTasksTab) {
        let { executeTask } = workOrderSettings || {}
        let { reason, allowed } = executeTask || {}
        let { workorder } = this
        let { noOfTasks } = workorder || {}
        if (
          !allowed &&
          !isEmpty(reason) &&
          (isEmpty(noOfTasks) || noOfTasks <= 0)
        ) {
          /*
            -> True when executeTask is false, and there is no task in workorder
            -> It's been done to not-to show "Task Execution not allowed" bannerMessage when there are no tasks.
           */
          message = null
        } else if (!isEmpty(reason)) {
          message = reason
        }
      } else if (isPlansTab) {
        // Plans tab conditional messages
        let { inventoryPlaning } = workOrderSettings || {}
        let { reason } = inventoryPlaning || {}

        if (!isEmpty(reason)) {
          message = reason
        }
      } else if (isActualsTab) {
        // Actuals tab conditional messages
        let { inventoryActuals } = workOrderSettings || {}
        let { reason } = inventoryActuals || {}

        if (!isEmpty(reason)) {
          message = reason
        }
      }
      return message
    },
    canShowMessageBanner() {
      return !isEmpty(this.bannerMessage)
    },
    isLockedState() {
      let { workorder } = this
      let { moduleState } = workorder || {}
      return this.$getProperty(moduleState, 'recordLocked', false)
    },
    isPdfDownload: {
      get() {
        let { openTaskOptions, isDownloadOption, pdfUrl } = this
        return openTaskOptions && isDownloadOption && !isEmpty(pdfUrl)
      },
      set(newValue) {
        if (!newValue) {
          this.$set(this, 'pdfUrl', '')
        }
        this.$set(this, 'openTaskOptions', newValue)
      },
    },
    moduleStateName() {
      let { woBundle } = this
      let { workorder } = woBundle || {}
      let { moduleState } = workorder || {}
      let { displayName = '---' } = moduleState || {}
      return displayName
    },
    isWorkOrderFeatureSettingsLicenseEnabled() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers || {}
      return isLicenseEnabled('WORK_ORDER_FEATURE_SETTINGS')
    },
  },
  methods: {
    handleViewResults() {
      this.$router.push({
        path: '',
        query: { ...this.$route.query, tab: 'timelog and metrics' },
      })
    },
    handleUpdateVisibility(data) {
      this.editFormVisibilty = data
    },
    openEditWODialog() {
      this.handleUpdateVisibility(true)
    },
    openAllSurveyDialog() {
      this.showSurveyDialog = true
    },
    loadWorkorderSummary(force = false, loadPage = true) {
      this.loading = loadPage
      //TODO: WORK_ORDER_FEATURE_SETTINGS license check
      if (this.isWorkOrderFeatureSettingsLicenseEnabled) {
        this.loadWorkorderSummaryWithWorkOrderFeatureSettingAPI(force)
      } else {
        // -- can be removed later --
        this.loadWorkorderSummaryOld(force)
        // -- can be removed later --
      }
    },
    loadWorkorderSummaryOld(force) {
      API.fetchRecord(
        'workorder',
        {
          id: this.$route.params.id,
        },
        { force }
      )
        .then(res => {
          this.$set(this, 'woBundle', res)
          this.woBundle.id = this.woBundle.workorder.id
          this.$store.dispatch(
            'workorder/setCurrentWO',
            this.woBundle.workorder
          )
        })
        .then(() => {
          // filtering prerequisite sections
          this.woBundle.workorder.preReqSections = {}
          for (const sectionID in this.woBundle.workorder.taskSections) {
            const taskSection = this.woBundle.workorder.taskSections[sectionID]
            if (taskSection.preRequest) {
              this.woBundle.workorder.preReqSections[+sectionID] = taskSection
            }
          }

          let preReqSectionIDs = Object.keys(
            this.woBundle.workorder.preReqSections
          ).map(k => +k)

          // filtering prerequisite tasks
          this.woBundle.workorder.preReqTasks = {}
          for (const sectionID in this.woBundle.workorder.tasks) {
            if (preReqSectionIDs.includes(+sectionID)) {
              this.woBundle.workorder.preReqTasks[
                sectionID
              ] = this.woBundle.workorder.tasks[sectionID]
            }
          }
          this.loading = false
        })
        .catch(error => {
          let { message = 'Error occured while fetching workorkder' } = error
          this.$message.error(message)
        })
    },
    loadWorkorderSummaryWithWorkOrderFeatureSettingAPI(force) {
      API.fetchRecord(
        'workorder',
        {
          id: this.$route.params.id,
        },
        { force }
      )
        .then(res => {
          this.$set(this, 'woBundle', res)
          this.woBundle.id = this.woBundle.workorder.id
          this.$store.dispatch(
            'workorder/setCurrentWO',
            this.woBundle.workorder
          )
        })
        .then(() => {
          // filtering prerequisite sections
          this.woBundle.workorder.preReqSections = {}
          for (const sectionID in this.woBundle.workorder.taskSections) {
            const taskSection = this.woBundle.workorder.taskSections[sectionID]
            if (taskSection.preRequest) {
              this.woBundle.workorder.preReqSections[+sectionID] = taskSection
            }
          }

          let preReqSectionIDs = Object.keys(
            this.woBundle.workorder.preReqSections
          ).map(k => +k)

          // filtering prerequisite tasks
          this.woBundle.workorder.preReqTasks = {}
          for (const sectionID in this.woBundle.workorder.tasks) {
            if (preReqSectionIDs.includes(+sectionID)) {
              this.woBundle.workorder.preReqTasks[
                sectionID
              ] = this.woBundle.workorder.tasks[sectionID]
            }
          }
          this.loading = false
        })
        .then(async () => {
          if (!isEmpty(this.workorder)) {
            let { workorder } = this
            let route = 'v3/workorders/features/' + workorder.id
            return await API.get(route)
          }
        })
        .then(({ data, error }) => {
          if (!isEmpty(data)) {
            let { woBundle, workOrderSettingsRelatedData } = this

            workOrderSettingsRelatedData = {
              ...data,
            }
            woBundle = {
              ...woBundle,
              ...data,
            }

            this.$set(this, 'woBundle', woBundle)
            this.$set(
              this,
              'workOrderSettingsRelatedData',
              workOrderSettingsRelatedData
            )
          } else if (!isEmpty(error)) {
            this.$message.error(
              error.message ||
                this.$t(
                  common.workorder
                    .error_occurred_while_fetching_work_order_feature_settings_message
                )
            )
          }
        })
        .catch(error => {
          let { message = 'Error occured while fetching workorkder' } = error
          this.$message.error(message)
        })
    },
    getCurrentModuleState() {
      let { woBundle } = this
      let { workorder } = woBundle || {}
      let { moduleState } = workorder || {}
      return moduleState
    },
    async refreshObj() {
      this.loadWorkorderSummary(true)
      this.surveyDetails = await loadSurveyForSurveyBar(
        true,
        this.moduleName,
        this.woId,
        this.surveyDetails
      )
    },
    loadStateflows(value) {
      this.stateflows = value
    },
    back() {
      this.$router.go(-1)
    },
    openPrintOptionDialog() {
      this.openTaskOptions = true
      this.isDownloadOption = false
    },
    openDownloadOptionDialog() {
      this.openTaskOptions = true
      this.isDownloadOption = true
    },
    async refreshWoDetails() {
      this.loadWorkorderSummary()
      this.surveyDetails = await loadSurveyForSurveyBar(
        false,
        this.moduleName,
        this.woId,
        this.surveyDetails
      )
    },
    onTransitionSuccess() {
      this.woBundle.workorder.loadTimer = false
      this.refreshWoDetails()
      this.reloadRelatedRecords()
      this.loadTasks()
    },
    reloadRelatedRecords() {
      eventBus.$emit('refresh-related-list')
    },
    transitionFilter(transition) {
      let stateObj = this.$store.getters.getTicketStatus(
        transition.toStateId,
        'workorder'
      )
      let isDisallowedState =
        stateObj.type === 'CLOSED' && !hasWorkOrderClosePermission
      return !isDisallowedState
    },
    handleTranslate() {
      this.woDescriptiontranslate = !this.woDescriptiontranslate
      eventBus.$emit('woDescriptiontranslate', this.woDescriptiontranslate)
      this.isActive = !this.isActive
    },
    setCurrentState(id) {
      this.$setProperty(this, 'woBundle.workorder.moduleState.id', id)
    },
    debounceLoadWorkOrderSummary: debounce(function() {
      this.loadWorkorderSummary(true, false)
    }, 1000),
  },
}
</script>
<style scoped>
.translateActive {
  border: 1px solid #518ef8;
  background: rgba(81, 142, 248, 0.1);
}
.wo_task_view_banner {
  width: 100%;
  height: 36px;
  margin: 0px;
  background-color: #e7eff3;
  border-bottom: 1px solid #0078b9;
  text-align: center;
}

.wo_task_view_banner_subject {
  display: flex !important;
  align-items: center;
  height: 100%;
  font-size: 14px;
  font-weight: normal;
  line-height: 1.14;
  letter-spacing: 0.5px;
  overflow: hidden;
  word-break: break-all;
  -webkit-line-clamp: 1;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #324056;
  justify-content: center;
  align-items: center;
}
</style>
