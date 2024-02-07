<template>
  <div>
    <Spinner
      v-if="isLoading || isFormDataLoading"
      size="80"
      :show="true"
    ></Spinner>
    <template v-else>
      <f-webform
        ref="state-transition-f-form"
        :form.sync="formObj"
        :module="moduleName"
        :isSaving="isSaving"
        :canShowPrimaryBtn="false"
        :canShowSecondaryBtn="false"
        :canShowNotifyRequester="canShowNotifyRequester"
        formLabelPosition="top"
        :removeDefaultStyling="true"
        :isEdit="false"
        @save="submitForm"
        @cancel="closeAction"
      ></f-webform>
    </template>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import StateTransitionForm from './TransitionForm'
export default {
  extends: StateTransitionForm,
  watch: {
    formId: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.selectedForm.id = newVal
          this.loadFormData()
        }
      },
    },
  },
  methods: {
    saveRecord() {
      let { $refs } = this
      if (!isEmpty($refs)) {
        let fWebFormComponent = $refs['state-transition-f-form']
        if (!isEmpty(fWebFormComponent)) {
          fWebFormComponent.saveRecord()
        }
      }
    },
  },
}
</script>
