<template>
  <div class="live-form">
    <portal to="mv-project-title" slim>{{ projectName }}</portal>
    <div v-if="isLoading" class="loading-container d-flex">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <f-webform
      v-else
      ref="mv-form-creation"
      :form.sync="formObj"
      :isSaving="isSaving"
      :isEdit="!$validation.isEmpty(projectId)"
      :module="module"
      customClass="project-new-container"
      @save="proceedToBaseLine"
      @onFormModelChange="setProjectName"
      :canShowSecondaryBtn="false"
      :canShowPrimaryBtn="true"
    ></f-webform>
  </div>
</template>

<script>
import FWebform from '@/FWebform'
import Constants from 'util/constant'
import { isEmpty } from '@facilio/utils/validation'
import { deepCloneObject } from 'util/utility-methods'
import Spinner from '@/Spinner'

import http from 'util/http'

// M & V form object
const MV_NEW_PROJECT_OBJ = {
  primaryBtnLabel: 'Proceed to next',
  secondaryBtnLabel: '',
  labelPosition: 2,
  sections: [
    {
      fields: [
        {
          displayName: 'Site',
          displayTypeEnum: 'LOOKUP_SIMPLE',
          name: 'siteId',
          span: 1,
          required: true,
          lookupModuleName: 'site',
        },
        {
          displayName: 'Name',
          displayTypeEnum: 'TEXTBOX',
          name: 'name',
          span: 1,
          required: true,
          value: '',
        },
        {
          displayName: 'Description',
          displayTypeEnum: 'TEXTAREA',
          name: 'description',
          span: 1,
          required: false,
        },
        {
          displayName: 'ECM Period',
          displayTypeEnum: 'DATERANGE',
          name: 'emcperiod',
          span: 1,
          required: true,
        },
        {
          displayName: 'ECM Option',
          displayTypeEnum: 'SELECTBOX',
          name: 'emcOptions',
          span: 1,
          required: false,
          options: Constants.ECM_MEASUREMENT_OPTIONS,
          value: 1,
        },
        {
          displayName: 'Asset',
          displayTypeEnum: 'WOASSETSPACECHOOSER',
          config: { filterValue: 2, isFiltersEnabled: true },
          field: {
            dataType: 7,
            default: true,
          },
          name: 'meter',
          span: 2,
          required: true,
          lookupModuleName: 'resource',
        },
        {
          displayName: 'Project Owner',
          displayTypeEnum: 'LOOKUP_SIMPLE',
          name: 'owner',
          span: 2,
          required: false,
          lookupModule: {
            type: -1,
          },
          lookupModuleName: 'users',
        },
      ],
      name: 'PROJECT',
      showLabel: true,
    },
  ],
}

export default {
  components: {
    FWebform,
    Spinner,
  },
  props: {
    sharedData: {
      type: Object,
    },
  },
  created() {
    let { projectId } = this
    if (!isEmpty(projectId)) {
      this.isLoading = true
      let url = `/v2/mv/getMVProject?mvProjectId=${projectId}`
      let promise = http
        .get(url)
        .then(({ data: { message, responseCode, result } }) => {
          if (responseCode === 0) {
            let { mvProjectWrapper = {} } = result
            let {
              mvProject = {},
              adjustments = [],
              baselines = [],
            } = mvProjectWrapper
            this.adjustmentsResObj = adjustments
            this.baselinesResObj = baselines
            this.defineReportObj = {
              saveGoal: mvProject.saveGoal,
              saveGoalFormulaField: mvProject.saveGoalFormulaField,
              reportingperiod: [
                mvProject.reportingPeriodStartTime,
                mvProject.reportingPeriodEndTime,
              ],
              frequency: mvProject.frequency,
            }
            this.deserializeProjObj(mvProject)
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
      Promise.all([promise]).finally(() => (this.isLoading = false))
    }
  },
  data() {
    return {
      module: 'm&v',
      projectId: this.$route.params.id,
      formObj: deepCloneObject(MV_NEW_PROJECT_OBJ),
      isSaving: false,
      isLoading: false,
      projectName: '',
      adjustmentsResObj: {},
      baselinesResObj: {},
      defineReportObj: {},
    }
  },
  methods: {
    deserializeProjObj(projObj) {
      let {
        formObj: { sections },
      } = this
      sections.forEach(section => {
        let { fields } = section
        fields.forEach(field => {
          let { name } = field
          if (name === 'emcperiod') {
            this.$set(field, 'value', [projObj.startTime, projObj.endTime])
          } else if (name === 'owner') {
            this.$set(field, 'value', (projObj.owner || {}).id)
          } else {
            this.$set(field, 'value', projObj[name])
          }
        })
      })
    },
    proceedToBaseLine(formModel) {
      let {
        projectId,
        adjustmentsResObj,
        baselinesResObj,
        defineReportObj,
      } = this
      let { emcperiod, emcOptions, siteId } = formModel
      if (!isEmpty(emcOptions) && !isEmpty(emcOptions.id)) {
        formModel.emcOptions = emcOptions.id
      }
      if (!isEmpty(emcperiod)) {
        formModel.startTime = emcperiod[0]
        formModel.endTime = emcperiod[1]
        delete formModel.emcperiod
      }
      if (!isEmpty(siteId) && !isEmpty(siteId.id)) {
        formModel.siteId = siteId.id
      }
      Object.entries(formModel).forEach(([key, value]) => {
        if (isEmpty(value)) {
          delete formModel[key]
        }
      })
      // Handling for edit cases
      if (!isEmpty(projectId)) {
        formModel.adjustmentsResObj = adjustmentsResObj
        formModel.baselinesResObj = baselinesResObj
        formModel.defineReportObj = defineReportObj
        formModel.projectId = projectId
      }
      this.$emit('nextStep', formModel)
    },
    setProjectName(formModel) {
      let { name } = formModel
      this.projectName = name
    },
  },
}
</script>

<style lang="scss">
.f-webform-container {
  &.project-new-container {
    .section-container {
      padding: 0 120px 30px 50px;
    }
  }
}
</style>
