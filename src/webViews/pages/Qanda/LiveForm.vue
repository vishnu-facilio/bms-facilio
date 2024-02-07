<template>
  <div class="height100vh">
    <SurveyLiveForm
      v-if="isModulePresent"
      class="height100"
      :templateModuleName="moduleNames.templateModuleName"
      :templateDisplayName="moduleNames.templateDisplayName"
      :responseModuleName="moduleNames.responseModuleName"
      :responseId="responseId"
      @onSubmitSuccess="onSubmit"
      @onGoBack="onGoBack"
    >
    </SurveyLiveForm>
  </div>
</template>
<script>
import { SurveyLiveForm } from '@facilio/survey'
import { isEmpty } from '@facilio/utils/validation'
import { emitEvent } from 'src/webViews/utils/mobileapps'

const MODULE_HASH = {
  inspection: {
    templateModuleName: 'inspectionTemplate',
    templateDisplayName: 'Inspection',
    responseModuleName: 'inspectionResponse',
  },
  induction: {
    templateModuleName: 'inductionTemplate',
    templateDisplayName: 'Induction',
    responseModuleName: 'inductionResponse',
  },
  survey: {
    templateModuleName: 'surveyTemplate',
    templateDisplayName: 'Survey',
    responseModuleName: 'surveyResponse',
  },
}

export default {
  props: ['module'],
  name: 'WebviewLiveForm',
  components: {
    SurveyLiveForm,
  },
  computed: {
    responseId() {
      let { $route } = this
      let { params } = $route || {}
      let { id } = params || {}
      return Number(id)
    },
    isModulePresent() {
      let { module } = this
      return !isEmpty(MODULE_HASH[module])
    },
    moduleNames() {
      let { module } = this
      return MODULE_HASH[module]
    },
  },
  methods: {
    onGoBack() {
      emitEvent('close', { submit: false })
    },
    onSubmit() {
      emitEvent('close', { submit: true })
    },
  },
}
</script>
