<template>
  <div>
    <div
      id="surveyCustomization-header"
      class="section-header survey-header-color"
    >
      {{ $t('survey.survey_customization') }}
    </div>
    <div class="pL50 customization-block">
      <el-form
        ref="customizationForm"
        :model="customization"
        label-width="350px"
        label-position="left"
        class="pT10 pB30"
      >
        <div class="section-container flex-container">
          <div class="form-one-column">
            <el-form-item
              :label="$t('survey.unanswered_survey_expiry')"
              prop="expiryDay"
            >
              <el-select
                class="fc-input-full-border2 width50"
                v-model="customization.expiryDay"
                placeholder="Choose Days"
              >
                <el-option
                  v-for="(key, label) in dayOptions"
                  :key="key"
                  :label="label"
                  :value="key"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
          <div class="form-one-column">
            <el-form-item
              :label="$t('survey.configure_whether_retake_survey')"
              prop="isRetakeAllowed"
            >
              <el-radio-group
                v-model="customization.isRetakeAllowed"
                class="fc-visitor-radio"
              >
                <el-radio :label="true" class="fc-radio-btn">
                  {{ $t('common.products.yes') }}</el-radio
                >
                <el-radio :label="false" class="fc-radio-btn">
                  {{ $t('common.products._no') }}</el-radio
                >
              </el-radio-group>
            </el-form-item>
          </div>
          <div class="form-one-column" v-if="customization.isRetakeAllowed">
            <el-form-item
              :label="$t('survey.display_retake_option')"
              prop="retakeExpiryDay"
            >
              <el-select
                class="fc-input-full-border2 width50"
                v-model="customization.retakeExpiryDay"
                placeholder="Choose Days"
              >
                <el-option
                  v-for="(key, label) in dayOptions"
                  :key="key"
                  :label="label"
                  :value="key"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  name: 'Survey-Customization',
  props: ['selectedSurvey'],
  data() {
    return {
      customization: {
        isRetakeAllowed: false,
        retakeExpiryDay: 3,
        expiryDay: 3,
      },
      dayOptions: {
        '1 Day': 1,
        '2 Days': 2,
        '3 Days': 3,
        '4 Days': 4,
        '5 Days': 5,
        '6 Days': 6,
        '7 Days': 7,
        '8 Days': 8,
        '9 Days': 9,
        '10 Days': 10,
      },
    }
  },
  watch: {
    selectedSurvey: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.prefillSurveyCustomization()
        }
      },
      immediate: true,
    },
  },
  methods: {
    prefillSurveyCustomization() {
      let { selectedSurvey } = this
      if (!isEmpty(selectedSurvey)) {
        let { workflowRule } = selectedSurvey || {}
        let { actions } = workflowRule || {}
        let isRetakeAllowed = this.$getProperty(
          actions,
          '0.template.isRetakeAllowed',
          null
        )
        let retakeExpiryDay = this.$getProperty(
          actions,
          '0.template.retakeExpiryDay',
          null
        )
        let expiryDay = this.$getProperty(actions, '0.template.expiryDay', null)
        let surveyDetails = {
          isRetakeAllowed,
          retakeExpiryDay,
          expiryDay,
        }

        this.$set(this, 'customization', surveyDetails)
      }
    },
    serialize() {
      let { customization } = this
      return customization
    },
  },
}
</script>
<style scoped>
.button-preview-region {
  display: flex;
  border: solid 1px #3ab2c1;
  background-color: #ebfdff;
}
.button-region {
  display: flex;
  width: 450px;
  margin-left: 250px;
  padding: 30px 30px 30px 30px;
  align-items: center;
  flex-direction: column;
}
.qna-btn-preview {
  background-color: #39b2c2;
}
.customization-block {
  min-height: 250px;
  margin-top: -10px;
}
</style>
