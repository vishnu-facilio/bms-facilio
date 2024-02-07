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
              <div class="custom-module-name mT5 d-flex align-center">
                <div class="d-flex align-center max-width300px">
                  <el-tooltip
                    placement="bottom"
                    effect="dark"
                    :content="$getProperty(customModuleData, mainFieldKey)"
                  >
                    <span class="whitespace-pre-wrap custom-header">{{
                      $getProperty(customModuleData, mainFieldKey)
                    }}</span>
                  </el-tooltip>
                </div>
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
        <div
          class="d-flex flex-direction-row align-center"
          style="margin-left: auto;"
        >
          <CustomButton
            class="p10"
            :record="customModuleData"
            :moduleName="moduleName"
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
              :recordId="id"
            />
          </portal>

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
        :recordId="id"
        :moduleName="'serviceRequest'"
        :recordName="getSubject"
      />
      <IndividualSurveyResult
        :showResultDialog.sync="showIndividualResponse"
        :details="selectedSurvey"
        :surveyLoading.sync="surveyLoading"
        :recordName="getSubject"
        :module="'serviceRequest'"
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
import ModuleOverview from 'PortalTenant/custom-module/ModuleOverview'
import surveyMixin from 'pages/setup/survey/SurveyMixin.js'
import { loadSurveyForSurveyBar } from 'pages/setup/survey/SurveyUtil'
import SurveyBar from 'pages/setup/survey/SurveyBar'
import ViewAllSurveysDialog from 'pages/setup/survey/ViewAllSurveys'
import IndividualSurveyResult from 'pages/setup/survey/IndividualSurveyResult'

export default {
  extends: ModuleOverview,
  components: { SurveyBar, ViewAllSurveysDialog, IndividualSurveyResult },
  mixins: [surveyMixin],
  async created() {
    this.surveyDetails = await loadSurveyForSurveyBar(
      false,
      this.moduleName,
      this.id,
      this.surveyDetails
    )
  },
  data() {
    return {
      notesModuleName: 'servicerequestsnotes',
      attachmentsModuleName: 'servicerequestsattachments',
      customClass: 'service-request-summary',
    }
  },
  computed: {
    mainFieldKey() {
      return 'subject'
    },
    getSubject() {
      let { customModuleData } = this
      let { subject = null } = customModuleData || {}
      return subject
    },
  },
  methods: {
    async refreshData() {
      this.loadRecord(true)
      this.surveyDetails = await loadSurveyForSurveyBar(
        true,
        this.moduleName,
        this.id,
        this.surveyDetails
      )
    },
  },
}
</script>
