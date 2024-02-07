<template>
  <div
    class="rule-basic-info-container rule-basic-info-container d-flex flex-direction-column"
  >
    <div class="position-relative">
      <div class="header f12 bold mT30 mL70 mR30 d-flex">
        <div>
          <div class="text-uppercase">{{ $t('rule.create.impact') }}</div>
          <div class="fc-heading-border-width43 mT15"></div>
        </div>
      </div>
      <div class="rule-condition-from-content rule-basic-info-content">
        <div class="rule-info-form">
          <div class="rca-rule-table" v-if="enableimpacts">
            <div class="row">
              <div class="fc-text-pink text-uppercase">
                {{ $t('common._common.cost_impact') }}
              </div>
              <div class="close-icon-fbuilder">
                <i
                  @click="enableimpacts = false"
                  class="el-dialog__close el-icon el-icon-close"
                ></i>
              </div>
            </div>
            <div class="flex-middle">
              <f-formula-builder
                class="pB20 rule-alarm-trigger width80"
                style="padding-top: 20px;"
                title=""
                module="impact"
                v-model="workflow"
                :assetCategory="{
                  id: categoryId,
                }"
              ></f-formula-builder>
            </div>
          </div>
          <div class="rule-basic-info-content" v-else>
            <div class="d-flex flex-direction-column text-center">
              <inline-svg
                src="svgs/emptystate/data-empty"
                iconClass="icon text-center icon-xxxxlg"
              ></inline-svg>
              <div class="mT10 empty-text f15 bold">
                {{ $t('common.products.no_cost_impact_available_here') }}
              </div>
              <div class="mT5 empty-text-desc f13">
                {{ $t('common.wo_report.you_havent_created_cost_impact_yet') }}
              </div>
              <div class="inline-block mT20">
                <el-button
                  class="pT10 pB10 small-border-btn text-uppercase pL15 pR15"
                  @click="enableimpacts = true"
                  >{{ $t('common.header.enable_cost_impact') }}</el-button
                >
              </div>
            </div>
          </div>
          <div class="modal-dialog-footer">
            <el-button
              @click="goToPrevious"
              type="button"
              class="modal-btn-cancel"
              >{{ $t('common._common.previous') }}</el-button
            >
            <el-button
              @click="addImpact"
              type="button"
              :loading="isSaving"
              class="modal-btn-save"
            >
              {{
                isEdit
                  ? $t('common.header.update_rule')
                  : $t('common.header.proceed_to_next')
              }}
              <img
                v-if="!isEdit"
                src="~assets/arrow-pointing-white-right.svg"
                width="17px"
                class="fR"
              />
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import FFormulaBuilder from '@/workflow/FFormulaBuilder'

export default {
  components: {
    FFormulaBuilder,
  },
  props: {
    sharedData: {
      type: Object,
    },
  },
  created() {},
  data() {
    return {
      enableimpacts: false,
      isSaving: false,
      workflow: null,
      trigger: {
        name: 'Cost Impact',
        ruleType: 8,
        activityType: 3,
        actions: [
          {
            actionType: 28,
            templateJson: {
              metaJson: {
                resultWorkflowContext: null,
                fields: 'cost',
              },
            },
          },
        ],
      },
    }
  },
  computed: {
    isEdit() {
      return this.$route.name === 'rule-creation-edit'
    },
    categoryId() {
      return this.sharedData.preRequsite
        ? this.sharedData.preRequsite.assetCategoryId
        : null
    },
  },
  mounted() {
    if (this.sharedData.enableimpacts) {
      this.enableimpacts = this.sharedData.enableimpacts
      if (
        this.sharedData.impactsContext &&
        this.sharedData.impactsContext.id > 0
      ) {
        this.loadWorflowRule(this.sharedData.impactsContext.id)
      }
    }
  },
  methods: {
    loadWorflowRule(id) {
      this.$http.get(`/v2/setup/asset/rules/${id}`).then(response => {
        this.workflow =
          response.data.result.rule.actions[0].template.originalTemplate.workflowContext
      })
    },
    addImpact() {
      if (
        this.sharedData.preRequsite &&
        this.sharedData.preRequsite.event.moduleId
      ) {
        this.trigger.moduleId = this.sharedData.preRequsite.event.moduleId
      }
      if (this.enableimpacts) {
        this.trigger.actions[0].templateJson.resultWorkflowContext = this.workflow
        this.sharedData.enableimpacts = this.enableimpacts
        if (
          this.sharedData.impactsContext &&
          this.sharedData.impactsContext.id
        ) {
          this.trigger.id = this.sharedData.impactsContext.id
        }
        this.sharedData.impactsContext = this.trigger
        let data = {
          workflowRule: this.sharedData.impactsContext,
        }
        let url = `/v2/modules/rules/`
        if (this.sharedData.impactsContext.id > 0) {
          url += `update`
        } else {
          url += `add`
        }
        this.$http.post(url, data).then(response => {
          if (response.data.responseCode === 0) {
            if (response.data.result.workflowRule.id) {
              this.sharedData.impactsContext.id =
                response.data.result.workflowRule.id
              this.moveToNext()
            }
          } else if (response.data.responseCode === 1) {
            this.$message({
              message: response.data.message,
              type: 'error',
            })
          }
        })
      } else {
        this.moveToNext()
      }
    },
    moveToNext() {
      if (this.$route.name === 'rule-creation-edit') {
        this.isSaving = true
        this.$emit('generateFinalSharedData', this.sharedData)
      } else {
        this.$emit('nextStep', this.sharedData.impactsContext)
      }
    },
    goToPrevious() {
      this.$emit('goToPreviousStep', null)
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
.impact-form-table table {
  width: 100% !important;
}
.close-icon-fbuilder {
  cursor: pointer;
  position: absolute;
  right: 25%;
}
</style>
