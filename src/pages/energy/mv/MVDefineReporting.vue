<template>
  <div class="live-form">
    <div v-if="isLoading" class="loading-container d-flex">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <f-webform
      v-else
      :form.sync="formObj"
      :isSaving="isSaving"
      :isEdit="!$validation.isEmpty((sharedData || {}).projectId)"
      :module="module"
      customClass="reporting-container"
      @save="saveMVProject"
      @cancel="goToPreviousStep"
      :canShowPrimaryBtn="true"
      :canShowSecondaryBtn="true"
    ></f-webform>
  </div>
</template>

<script>
import FWebform from '@/FWebform'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import { deepCloneObject } from 'util/utility-methods'
import { constructFieldOptions } from '@facilio/utils/utility-methods'
import Constants from 'util/constant'

// M & V form object
const MV_DEFINE_REPORTING_OBJ = {
  primaryBtnLabel: 'Save',
  secondaryBtnLabel: 'Previous',
  labelPosition: 2,
  sections: [
    {
      fields: [
        {
          displayName: 'Reporting Period',
          displayTypeEnum: 'DATERANGE',
          name: 'reportingperiod',
          span: 1,
          required: true,
        },
        {
          displayName: 'Frequency',
          displayTypeEnum: 'SELECTBOX',
          name: 'frequency',
          span: 1,
          required: false,
          options: constructFieldOptions(Constants.MV_FREQUENCY),
          value: 3,
        },
        {
          displayName: 'Target Savings',
          displayTypeInt: 5,
          displayType: 5,
          displayTypeEnum: 'RADIO_TEMP',
          dataType: 4,
          required: false,
          span: 1,
          value: 1,
          name: 'saveGoalType',
          isDefault: true,
          field: {
            trueVal: 'Constant',
            falseVal: 'Formula',
          },
          formulaFieldHack: true,
        },
        {
          displayName: '',
          displayTypeInt: 1,
          displayType: 1,
          displayTypeEnum: 'TEXTBOX_TEMP',
          dataType: 1,
          name: 'saveGoal',
          required: true,
          span: 1,
          value: '',
          isDefault: true,
          formulaFieldHack: true,
        },
        {
          displayName: '',
          displayTypeEnum: 'FORMULA_FIELD_TEMP',
          name: 'saveGoalFormulaField',
          span: 1,
          required: true,
          hideModeChange: true,
          renderInLeft: true,
          formulaFieldHack: true,
        },
      ],
      name: 'DEFINE REPORTING PERIOD',
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
    let { sharedData = {} } = this
    let { defineReportObj, projectId } = sharedData
    if (!isEmpty(projectId)) {
      this.isLoading = true
      this.deserializeObj(defineReportObj)
    }
  },
  data() {
    return {
      module: 'm&v',
      formObj: deepCloneObject(MV_DEFINE_REPORTING_OBJ),
      isSaving: false,
      isLoading: false,
    }
  },
  methods: {
    saveMVProject(formModel) {
      let { reportingperiod, frequency, saveGoalType } = formModel
      if (!isEmpty(frequency) && !isEmpty(frequency.id)) {
        formModel.frequency = frequency.id
      }
      if (!isEmpty(reportingperiod)) {
        formModel.reportingPeriodStartTime = reportingperiod[0]
        formModel.reportingPeriodEndTime = reportingperiod[1]
        delete formModel.reportingperiod
      }
      if (saveGoalType === 0) {
        delete formModel.saveGoal
      } else {
        delete formModel.saveGoalFormulaField
      }
      this.$emit('generateFinalSharedData', formModel)
    },
    goToPreviousStep() {
      this.$emit('goToPreviousStep', {})
    },
    deserializeObj(obj) {
      let { formObj } = this
      let { sections } = formObj
      sections.forEach(section => {
        let { fields } = section
        fields.forEach(field => {
          let { name } = field
          if (name === 'saveGoalType') {
            field.value = isEmpty(obj.saveGoal) ? 0 : 1
          } else {
            field.value = obj[name]
          }
        })
      })
      this.isLoading = false
    },
  },
}
</script>

<style lang="scss">
.f-webform-container {
  &.reporting-container {
    border: 1px solid #ebedf4;
    .section-container {
      padding: 0 100px 30px 50px;
      border: none;
    }
  }
}
</style>
