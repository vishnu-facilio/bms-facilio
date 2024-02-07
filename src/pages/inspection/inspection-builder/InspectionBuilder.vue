<template>
  <div
    class="formbuilder-fullscreen-popup height100vh fc-inspection-builder-container"
  >
    <SurveyBuilder
      class="height100"
      ref="survey-builder"
      :templateModuleName="templateModuleName"
      :templateDisplayName="templateDisplayName"
      :responseModuleName="responseModuleName"
      :id="id"
      @onScoringEdit="onScoringEdit"
    >
      <template v-slot:survey-button>
        <div class="done-btn" @click="redirectToSummary">Done</div>
      </template>
    </SurveyBuilder>
  </div>
</template>
<script>
import { SurveyBuilder } from '@facilio/survey'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  components: {
    SurveyBuilder,
  },
  props: {
    isV2: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      templateModuleName: 'inspectionTemplate',
      templateDisplayName: 'Inspection',
      responseModuleName: 'inspectionResponse',
      isEditScore: false,
    }
  },
  computed: {
    id() {
      let { $route } = this
      let { params } = $route || {}
      let { id } = params || {}
      return Number(id)
    },
  },
  methods: {
    onScoringEdit(props) {
      if (props) {
        this.isEditScore = true
      } else {
        this.isEditScore = false
      }
    },
    async redirectToSummary() {
      let { viewname } = this.$route.params
      if (this.isV2) {
        this.$emit('ruleEvent', {
          moduleName: this.templateModuleName,
          id: this.id,
          viewname,
        })
      } else {
        let value = false
        if (this.isEditScore) {
          value = await this.$dialog.confirm({
            title: this.$t(`common.header.unsaved_changes`),
            message: this.$t(
              `common._common.All_the_unsaved_changes_will_be_lost_Are_you_sure_you_want_to_continue`
            ),
            rbDanger: true,
            rbLabel: this.$t('common.header.proceed'),
          })
        }
        if (value || !this.isEditScore) {
          let { $route } = this
          let { params } = $route || {}
          let { id } = params || {}
          this.$emit('reloadlist')
          if (isWebTabsEnabled()) {
            let { name } =
              findRouteForModule('inspectionTemplate', pageTypes.OVERVIEW) || {}
            name &&
              this.$router.push({
                name,
                params: {
                  viewname: 'all',
                  id,
                },
              })
          } else {
            this.$router
              .push({
                name: 'inspectionTemplateSummary',
                params: {
                  viewname: 'all',
                  id,
                },
              })
              .catch(() => {})
          }
        }
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.formbuilder-fullscreen-popup {
  border: 0;
  background: #fff;
  .done-btn {
    margin-left: 10px;
    text-align: center;
    border: solid 1px #39b2c2;
    background-color: #39b2c2;
    color: #fff;
    cursor: pointer;
    border-radius: 3px;
    padding: 5px 20px;
    height: 32px;
    display: flex;
    align-items: center;
  }
}
</style>
