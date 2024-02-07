<template>
  <div class="height100vh">
    <SurveyLiveForm
      ref="survey-live-form"
      :templateModuleName="templateModuleName"
      :responseModuleName="responseModuleName"
      :templateDisplayName="templateDisplayName"
      :responseId="responseId"
      :canShowFooter="true"
      @onSubmitSuccess="redirectToThankYouScreen"
      @recordDetails="recordDetails"
    >
      <template #header>
        <div class="survey-header">
          <div class="record-context mT10">
            <div class="template-name mB10">{{ surveyName }}</div>
            <div class="record-id d-flex">
              {{ `${moduleDisplayName} ID` }} : {{ `#${recordId}` }}
            </div>
            <div class="record-name mB10">
              {{ `${moduleDisplayName}` }} : {{ recordName }}
            </div>
          </div>
          <div v-if="!isEmpty(companyLogoUrl)" class="company-logo mT10 mB10">
            <img :src="companyLogoUrl" class="logo-style" />
          </div>
          <div class="btn-section mT20">
            <el-button
              type="primary"
              class="footer-action-btn"
              @click="saveSurvey()"
              :loading="isSaving"
            >
              {{ $t('common._common.submit') }}
            </el-button>
          </div>
        </div>
      </template>
    </SurveyLiveForm>
  </div>
</template>
<script>
import { SurveyLiveForm } from '@facilio/survey'
import { getApp } from '@facilio/router'
import InspectionLiveForm from 'pages/inspection/individual-inspection/InspectionLiveForm'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: InspectionLiveForm,
  props: ['companyLogoUrl', 'templateId'],
  data() {
    return {
      templateModuleName: 'surveyTemplate',
      templateDisplayName: 'Survey',
      responseModuleName: 'surveyResponse',
      surveyName: '',
      recordName: '',
      moduleDisplayName: '',
      recordId: '',
    }
  },

  components: { SurveyLiveForm },
  computed: {
    responseId() {
      return parseInt(this.$route.params.id)
    },
    isSaving() {
      return this.$refs['survey-live-form'].isSubmitTriggered
    },
  },
  created() {
    this.isEmpty = isEmpty
  },
  methods: {
    getRecordId(record) {
      let { id } = record
      return id
    },
    getRecordName(record) {
      let { subject } = record
      return subject
    },
    getSurveyName(record) {
      let { name } = record
      this.surveyName = name
    },
    recordDetails(record) {
      if (!isEmpty(record)) {
        this.getSurveyName(record)
        let {
          workOrderId: workorder,
          serviceRequestId: serviceRequest,
        } = record
        if (!isEmpty(workorder)) {
          this.moduleDisplayName = 'WorkOrder'
          this.recordId = this.getRecordId(workorder)
          this.recordName = this.getRecordName(workorder)
        } else if (!isEmpty(serviceRequest)) {
          this.moduleDisplayName = 'Service Request'
          this.recordId = this.getRecordId(serviceRequest)
          this.recordName = this.getRecordName(serviceRequest)
        }
      }
    },
    saveSurvey() {
      this.$refs['survey-live-form'].triggerSubmitSurvey()
    },
    redirectToThankYouScreen(data) {
      if (!isEmpty(data)) {
        let { responseId, $route } = this
        let { query } = $route || {}
        let { name, recordId } = query || {}
        let { linkName: appName } = getApp() || {}
        let params = { moduleName: name, recordId, id: responseId, appName }

        this.$router.push({ name: 'thankYouScreen', params })
      } else {
        this.$message.error('Survey Expired')
      }
      this.canSubmitSurvey = false
    },
  },
}
</script>
<style lang="scss" scoped>
.survey-header {
  height: 100px;
  background-color: #fff;
  display: flex;
  justify-content: space-between;

  .record-context {
    display: flex;
    flex-direction: column;
    margin-left: 15px;
    .template-name {
      font-weight: bold;
      font-size: 16px;
      letter-spacing: 0.5px;
    }
    .record-id {
      margin-bottom: 7px;
      font-size: 12px;
      letter-spacing: 0.3px;
    }
    .record-name {
      font-size: 12px;
      letter-spacing: 0.3px;
    }
  }
  .btn-section {
    margin-right: 20px;
  }
  .company-logo {
    margin-left: -50px;
    .logo-style {
      width: 100px;
      height: 60px;
    }
  }
}
</style>
