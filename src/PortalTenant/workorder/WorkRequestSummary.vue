<template>
  <div class="custom-module-overview overflow-y-scroll" :class="customClass">
    <div v-if="isLoading" class="text-center width100 pT50 mT50">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else>
      <div class="header pT10 pB15 pL20 pR20">
        <div
          v-if="!$validation.isEmpty(customModuleData)"
          class="custom-module-details"
        >
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
              <div class="custom-module-id mT10">
                <i
                  v-if="$account.portalInfo"
                  class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                  content="back"
                  arrow
                  v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                  @click="back"
                ></i>
                #{{ customModuleData[idFieldKey] }}
              </div>
              <div class="custom-module-name mb5 d-flex">
                {{ $getProperty(customModuleData, mainFieldKey) }}
                <div
                  v-if="isStateFlowEnabled && currentModuleState"
                  class="fc-badge text-uppercase inline vertical-middle mL15"
                >
                  {{ currentModuleState }}
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="d-flex flex-direction-row" style="margin-left: auto;">
          <CustomButton
            class="p10"
            :record="customModuleData"
            :moduleName="moduleName"
            :updateUrl="updateUrlString"
            :transformFn="transformFormData"
            :position="POSITION.SUMMARY"
            @refresh="refreshData()"
            @onError="() => {}"
          />
          <template v-if="isStateFlowEnabled">
            <TransitionButtons
              :key="customModuleData.id"
              :moduleName="moduleName"
              :record="customModuleData"
              :disabled="isApprovalEnabled"
              :transitionFilter="transitionFilter"
              buttonClass="asset-el-btn"
              :updateUrl="updateUrlString"
              :transformFn="transformFormData"
              @currentState="() => {}"
              @transitionSuccess="refreshData()"
              @transitionFailure="() => {}"
              class="mR10"
            ></TransitionButtons>
          </template>

          <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
            <ApprovalBar
              :moduleName="moduleName"
              :key="customModuleData.id + 'approval-bar'"
              :record="customModuleData"
              :hideApprovers="shouldHideApprovers"
              @onSuccess="refreshData()"
              @onFailure="() => {}"
              class="approval-bar-shadow"
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

          <el-dropdown
            v-if="canShowActions"
            class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
            trigger="click"
            @command="dropDownActions"
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
              <el-dropdown-item v-if="canShowEdit" :command="'edit'">{{
                $t('common._common.edit')
              }}</el-dropdown-item>
              <el-dropdown-item v-if="canShowDelete" :command="'delete'">{{
                $t('common._common.delete')
              }}</el-dropdown-item>
              <el-dropdown-item
                v-if="checkIfSurveyLicenseEnabled && isViewAnswersEnabled"
                @click.native="showAnswers()"
              >
                {{ $t('survey.view_survey_answers') }}
              </el-dropdown-item>
              <el-dropdown-item
                v-if="checkIfSurveyLicenseEnabled && showViewAllSurvey"
                @click.native="setshowSurveyDialog()"
              >
                {{ $t('survey.take_other_surveys') }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <ViewAllSurveysDialog
        :showSurveyDialog.sync="showSurveyDialog"
        :showAllResponses.sync="showAllResponses"
        :recordId="woId"
        :moduleName="'workorder'"
        :recordName="workOrderName"
      />
      <IndividualSurveyResult
        :showResultDialog.sync="showIndividualResponse"
        :details="selectedSurvey"
        :surveyLoading.sync="surveyLoading"
        :recordName="workOrderName"
        :module="'workorder'"
      />
      <page
        v-if="customModuleData && customModuleData.id"
        :key="customModuleData.id"
        :module="moduleName"
        :id="customModuleData.id"
        :details="customModuleData"
        :primaryFields="primaryFields"
        :notesModuleName="notesModuleName"
        :attachmentsModuleName="attachmentsModuleName"
        :isV3Api="isV3Api"
      ></page>
    </template>
  </div>
</template>

<script>
import Overview from '../components/ModuleSummary'
import SurveyBar from 'pages/setup/survey/SurveyBar'
import ViewAllSurveysDialog from 'pages/setup/survey/ViewAllSurveys'
import IndividualSurveyResult from 'pages/setup/survey/IndividualSurveyResult'
import { isEmpty } from '@facilio/utils/validation'
import { loadSurveyForSurveyBar } from 'pages/setup/survey/SurveyUtil'
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import { getApp } from '@facilio/router'
import { eventBus } from '@/page/widget/utils/eventBus'
import debounce from 'lodash/debounce'
import surveyMixin from 'pages/setup/survey/SurveyMixin.js'

export default {
  extends: Overview,
  components: {
    SurveyBar,
    ViewAllSurveysDialog,
    IndividualSurveyResult,
  },
  mixins: [workorderMixin, surveyMixin],
  async created() {
    this.$store.dispatch('loadApprovalStatus')
    let { moduleName } = this || {}
    this.$store.dispatch('loadTicketStatus', moduleName)
    this.loadMeta()
    eventBus.$on('refreshWorkOrderPage', () =>
      this.debounceLoadWorkOrderSummary()
    )
    this.surveyDetails = await loadSurveyForSurveyBar(
      false,
      this.moduleName,
      this.woId,
      this.surveyDetails
    )
  },
  mounted() {
    eventBus.$on('postLoadRecordCallback', this.postLoadRecordCallback)
  },
  data() {
    return {
      notesModuleName: 'ticketnotes',
      attachmentsModuleName: 'ticketattachments',
      primaryFields: [
        'subject',
        'description',
        'site',
        'siteId',
        'resource',
        'urgency',
        'parentWO',
        'sendForApproval',
      ],
      workOrderSettingsRelatedData: {},
    }
  },
  computed: {
    moduleName() {
      return 'workorder'
    },
    mainFieldKey() {
      return 'subject'
    },
    idFieldKey() {
      return 'serialNumber'
    },
    canShowActions() {
      let { isNotLocked } = this
      return isNotLocked
    },
    woId() {
      let { $route } = this || {}
      let { params } = $route || {}
      let { id } = params || {}
      return id
    },
    workOrderName() {
      let { record } = this
      let { subject } = record || {}
      if (!isEmpty(subject)) {
        return subject
      }
      return '---'
    },
    isLockedState() {
      let { record } = this
      let { moduleState } = record || {}
      return this.$getProperty(moduleState, 'recordLocked', false)
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
    isWorkOrderFeatureSettingsLicenseEnabled() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers || {}
      return isLicenseEnabled('WORK_ORDER_FEATURE_SETTINGS')
    },
    canShowMessageBanner() {
      return !isEmpty(this.bannerMessage)
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
        let { record } = this
        let { noOfTasks } = record || {}
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
  },
  methods: {
    postLoadRecordCallback(record) {
      let { isWorkOrderFeatureSettingsLicenseEnabled } = this
      //TODO: WORK_ORDER_FEATURE_SETTINGS license check
      if (isWorkOrderFeatureSettingsLicenseEnabled) {
        // if check can be removed later, once WORK_ORDER_FEATURE_SETTINGS license is fully enabled
        this.loadWorkOrderFeatureSettings(record)
      }
    },
    async refreshData() {
      this.loadRecord(true)
      this.surveyDetails = await loadSurveyForSurveyBar(
        true,
        this.moduleName,
        this.woId,
        this.surveyDetails
      )
    },
    updateUrl(transition) {
      if (!isEmpty(transition)) {
        let stateObj = this.$store.getters.getTicketStatus(
          transition.toStateId,
          'workorder'
        )

        if (this.$getProperty(stateObj, 'type') === 'CLOSED') {
          return '/v2/workorders/close'
        } else return '/v2/workorders/update'
      }
    },
    debounceLoadWorkOrderSummary: debounce(function() {
      this.loadRecord(true, false)
    }, 1000),
    async loadWorkOrderFeatureSettings(record) {
      if (!isEmpty(this.woId)) {
        let { woId } = this
        let route = 'v3/workorders/features/' + woId
        let { data, error } = await API.get(route)
        if (!isEmpty(data)) {
          let workOrderSettingsRelatedData = {
            ...data,
          }
          record = {
            ...record,
            ...data,
          }

          // calling modifyRecordData so that customModuleData gets updated, and
          // workorderSettings data will be available in other components(like TasksMonolith.vue),
          // which gets added via PageBuilder
          this.modifyRecordData(record)

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
      }
    },
  },
  destroyed() {
    eventBus.$off('refreshWorkOrderPage')
  },
}
</script>
<style>
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
  overflow: hidden;
  text-overflow: ellipsis;
  color: #324056;
  justify-content: center;
  align-items: center;
}
</style>
