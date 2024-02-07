<template>
  <div class="height100">
    <SurveyLiveForm
      style="height: 100vh;"
      :templateModuleName="templateModuleName"
      :templateDisplayName="templateDisplayName"
      :responseModuleName="responseModuleName"
      :responseId="responseId"
      @onSubmitSuccess="redirectToSummary"
      @onGoBack="goBack"
      @dirtyQuestionIds="isDirtyQuestions"
    >
    </SurveyLiveForm>
  </div>
</template>
<script>
import { SurveyLiveForm } from '@facilio/survey'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  components: {
    SurveyLiveForm,
  },
  props: {
    isV2: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    responseId() {
      let { $route } = this
      let { params } = $route || {}
      let { id } = params || {}
      return Number(id)
    },
  },
  data() {
    return {
      templateModuleName: 'inspectionTemplate',
      templateDisplayName: 'Inspection',
      responseModuleName: 'inspectionResponse',
      canLeave: true,
    }
  },
  beforeRouteLeave(to, from, next) {
    if (!this.canLeave) {
      const confirmMessage = this.$t('qanda.response.confirm_message')
      if (confirm(confirmMessage)) {
        this.canLeave = true
        next()
      } else {
        next(false)
      }
    } else {
      next()
    }
  },
  methods: {
    isDirtyQuestions(value) {
      this.canLeave = value
    },
    redirectToSummary(data) {
      this.canLeave = true
      let { viewname } = this.$route.params
      if (this.isV2) {
        this.$emit('ruleEvent', {
          moduleName: this.responseModuleName,
          id: this.responseId,
          viewname,
        })
      } else {
        let { id } = data || {}
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule(this.responseModuleName, pageTypes.OVERVIEW) ||
            {}
          name &&
            this.$router.push({
              name,
              params: {
                viewname: 'all',
                id,
              },
            })
        } else {
          this.$router.push({
            name: 'individualInspectionSummary',
            params: { id, viewname: 'all' },
          })
        }
      }
    },
    goBack(props) {
      let { data = {} } = props || {}
      this.redirectToSummary(data)
    },
  },
}
</script>
