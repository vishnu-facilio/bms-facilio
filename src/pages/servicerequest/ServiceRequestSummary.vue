<template>
  <div
    class="custom-modules-overview overflow-y-scroll width100 service-request-summary"
    data-testid="service-request-summary"
  >
    <div
      v-if="!isLoading && !$validation.isEmpty(customModuleData)"
      class="header pT10 pB15 pL20 pR20"
    >
      <div class="asset-details">
        <div class="asset-id mT10">
          <i
            class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
            content="back"
            arrow
            v-tippy="{ animateFill: false, animation: 'shift-toward' }"
            @click="routeToList"
          ></i>
          #{{ customModuleData.id }}
        </div>
        <div class="d-flex flex-middle mT10">
          <div v-if="modeAllow" class="flex-shrink-0">
            <el-tooltip
              effect="dark"
              :content="modeTypes[customModuleData.mode]"
              placement="top"
              :open-delay="400"
            >
              <div :class="modeClass(customModuleData)"></div>
            </el-tooltip>
          </div>
          <div class="mL5">
            <div class="heading-black22 pT5 mb5 d-flex">
              {{ customModuleData[mainFieldKey] }}
            </div>
          </div>
          <div
            v-if="currentModuleState"
            class="fc-badge text-uppercase inline vertical-middle mL15"
          >
            {{ currentModuleState }}
          </div>
        </div>
      </div>
      <div class="d-flex flex-direction-row" style="margin-left: auto;">
        <CustomButton
          data-testid="custom-button"
          class="mT15"
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

        <portal
          to="pagebuilder-sticky-top"
          data-testid="pagebuilder-sticky-top"
          v-if="isApprovalEnabled"
        >
          <ApprovalBar
            :moduleName="moduleName"
            :key="customModuleData.id + 'approval-bar'"
            :record="customModuleData"
            :hideApprovers="shouldHideApprovers"
            @onSuccess="refreshObj(true)"
            @onFailure="() => {}"
            class="approval-bar-shadow"
          ></ApprovalBar>
        </portal>
        <portal
          to="pagebuilder-fixed-top"
          v-if="isSurveyEnabled && !isApprovalEnabled"
        >
          <SurveyBar
            :surveyDetails="surveyDetails"
            moduleName="serviceRequest"
            :recordId="serviceRequestId"
          />
        </portal>
        <el-dropdown
          class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
          trigger="click"
          @command="action => summaryDropDownAction(action)"
          data-testid="summary-dropdown"
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
            <el-dropdown-item :key="1" v-if="showEdit" :command="'edit'"
              >Edit</el-dropdown-item
            >
            <el-dropdown-item
              :key="2"
              v-if="showViewAllSurvey"
              :command="'showAllSurveyDialog'"
              >{{ $t('survey.take_other_surveys') }}</el-dropdown-item
            >
            <el-dropdown-item
              v-for="(module, index) in subModulesList"
              :key="`convert-${index}`"
              :command="`${module.name}`"
            >
              <div
                v-if="
                  $account.data.orgInfo.hideCreateWOSystemButtonInSR == null ||
                    ($account.data.orgInfo.hideCreateWOSystemButtonInSR &&
                      $account.data.orgInfo.hideCreateWOSystemButtonInSR !=
                        'true')
                "
              >
                {{
                  `Convert to ${
                    $constants.moduleSingularDisplayNameMap[module.name]
                      ? $constants.moduleSingularDisplayNameMap[module.name]
                      : module.displayName
                  }`
                }}
              </div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <ViewAllSurveysDialog
      :showSurveyDialog.sync="showSurveyDialog"
      :recordId="serviceRequestId"
      :moduleName="'serviceRequest'"
    />
    <page
      v-if="!isLoading && customModuleData && customModuleData.id"
      :key="customModuleData.id"
      :module="moduleName"
      :moduleDisplayName="moduleDisplayName"
      :id="customModuleData.id"
      :recordName="recordName"
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
      data-testid="setup-dialog"
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
  </div>
</template>
<script>
import CustomModuleOverview from 'pages/base-module-v2/Overview'
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '../../util/validation'
import { loadSurveyForSurveyBar } from 'pages/setup/survey/SurveyUtil'
import SurveyBar from 'pages/setup/survey/SurveyBar'
import ViewAllSurveysDialog from 'pages/setup/survey/ViewAllSurveys'
import surveyMixin from '../setup/survey/SurveyMixin'

const modeTypes = {
  1: 'New',
  2: 'Customer Replied',
  3: 'Admin Replied',
  '-1': 'Created',
}

export default {
  props: ['viewname'],
  extends: CustomModuleOverview,
  mixins: [surveyMixin],
  components: { SurveyBar, ViewAllSurveysDialog },
  data() {
    return {
      notesModuleName: 'servicerequestsnotes',
      attachmentsModuleName: 'servicerequestsattachments',
      primaryFields: ['subject'],
      serviceRequest: null,
      subModulesDataList: [],
      modeTypes,
    }
  },
  async created() {
    this.fetchSubModuleList()
    this.surveyDetails = await loadSurveyForSurveyBar(
      false,
      'serviceRequest',
      this.serviceRequestId,
      this.surveyDetails
    )
  },
  computed: {
    modeAllow() {
      let hideSRMode = this.$getProperty(
        this.$account,
        'data.orgInfo.hideSRMode',
        {}
      )
      return isEmpty(hideSRMode) || hideSRMode != 'true'
    },
    showEdit() {
      return (
        this.$hasPermission(`${this.moduleName}:UPDATE`) && !this.isRecordLocked
      )
    },
    customModuleData() {
      return this.serviceRequest
    },
    subModulesList() {
      return this.subModulesDataList
    },
    mainFieldKey() {
      return 'subject'
    },
    ...mapState({
      ticketStatus: state => state.ticketStatus.serviceRequest,
      moduleMeta: state => state.view.metaInfo,
    }),
    moduleDisplayName() {
      let { moduleMeta } = this
      let { displayName } = moduleMeta || {}
      return displayName
    },
    serviceRequestId() {
      return this.$route.params.id
    },
    recordName() {
      let { customModuleData } = this
      let { subject } = customModuleData || {}
      return subject
    },
  },

  methods: {
    async refreshObj(force = false) {
      this.loadCustomModuleData(force)
      this.surveyDetails = await loadSurveyForSurveyBar(
        true,
        this.moduleName,
        this.serviceRequestId,
        this.surveyDetails
      )
    },
    modeClass(customModuleData) {
      const modeClass = {
        1: 'is-new',
        2: 'customer-replied',
        3: 'admin-replied',
        '-1': 'created',
      }
      let { mode } = customModuleData || {}

      if (mode) {
        return `service-request-mode-in-summary-page ${modeClass[mode]}`
      }
    },
    async loadCustomModuleData(force = false) {
      this.isLoading = true

      let { id, moduleName } = this
      let { error, serviceRequest } = await API.fetchRecord(
        moduleName,
        { id },
        { force }
      )

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.serviceRequest = serviceRequest
      }
      this.isLoading = false
    },
    editCustomModuleData() {
      let { id, moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        name && this.$router.push({ name, params: { id } })
      } else {
        this.$router.push({
          path: `/app/sr/serviceRequest/edit/${id}`,
        })
      }
    },
    openNewForm(moduleName) {
      if (moduleName === 'workorder') {
        let { id, subject, description, siteId, resource = {} } =
          this.customModuleData || {}
        this.$router.push({
          path: `/app/wo/create`,
          query: {
            serviceRequest: id,
            subject,
            description,
            siteId,
            resource: (resource || {}).id,
            resourceLabel: (resource || {}).name,
          },
        })
      }
    },
    async fetchSubModuleList() {
      let { error, data } = await API.get('v2/servicerequests/fetchSubModules')

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.subModulesDataList = data.submodules || []
      }
    },
    routeToList() {
      let {
        viewname,
        $route: { query },
      } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: { viewname },
            query,
          })
      } else {
        this.$router.push({
          path: `/app/sr/serviceRequest/${viewname}`,
          query,
        })
      }
    },
    summaryDropDownAction(action) {
      if (action === 'edit') {
        this.editCustomModuleData()
      } else if (action === 'showAllSurveyDialog') {
        this.setshowSurveyDialog()
      } else if (action) {
        this.openNewForm(action)
      }
    },
  },
}
</script>

<style lang="scss">
.service-request-mode-in-summary-page {
  padding: 4px;
  border-radius: 50%;

  &.is-new {
    background-color: #6cbd85;
  }
  &.customer-replied {
    background-color: #7fa5ff;
  }
  &.admin-replied {
    background-color: #7fa5ff;
  }
  &.created {
    background-color: #6cbd85;
  }
}
</style>
