<template>
  <el-dialog
    :title="
      isSection ? $t('jobplan._section_details') : $t('jobplan._task_details')
    "
    :visible="showSettingDialog"
    width="40%"
    class="fc-dialog-center-container jp-task-dialog"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <!-- Section Info -->
    <div class="setting-container">
      <div class="pL16 pT20 pR15 d-flex">
        <div class="name-icon">
          <fc-icon group="default" name="circle-check"></fc-icon>
        </div>
        <div class="d-flex flex-direction-column mL10 width100">
          <div class="name-and-desc">
            <div class="section-name">{{ sectionName }}</div>
            <div
              class="content desc-content"
              v-if="selectedSectionInfo.description"
            >
              {{ selectedSectionInfo.description }}
            </div>
          </div>
          <el-row v-if="!isEmpty(scopeName)" class="d-flex">
            <el-col
              class="scope d-flex flex-direction-column mB10 mT15"
              :span="8"
            >
              <div class="title">{{ $t('jobplan._scope') }}</div>
              <div class="content">{{ scopeName }}</div>
            </el-col>
            <el-col
              v-if="canShowResourceCategory && false"
              class="scope d-flex flex-direction-column mB10  mT15"
              :span="12"
            >
              <div class="title">{{ scopeName }}</div>
              <div class="content">{{ scopeCategoryName }}</div>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="scope-seperator"></div>

      <!-- Input Configuration -->
      <div class="pL16 pT10 pR15 d-flex">
        <div class="name-icon">
          <fc-icon group="default" name="settings"></fc-icon>
        </div>
        <div class="d-flex flex-direction-column mL10 width100">
          <div class="name-and-desc">
            <div class="section-name">{{ $t('jobplan.input_config') }}</div>
            <div class="content">
              {{ inputType }}
            </div>
          </div>
          <div v-if="inputEnabled">
            <div v-if="[5, 6].includes(selectedSectionInfo.inputType)">
              <div class="title mT15">
                {{ $t('jobplan.values') }}
              </div>
              <div
                class="content"
                v-for="(option, index) in optionsList"
                :key="index"
              >
                {{ option.value }}
              </div>
            </div>
            <div v-if="selectedSectionInfo.inputType === 2">
              <div class="title mT20">
                {{ $t('jobplan.reading_field') }}
              </div>
              <div class="content mT10">
                {{ readingFieldName }}
              </div>
            </div>
            <el-row class="mT10 d-flex width100">
              <el-col
                class="scope d-flex flex-direction-column mB10"
                :span="8"
                v-if="!$validation.isEmpty(selectedSectionInfo.defaultValue)"
              >
                <div class="title">
                  {{ $t('common._common.default_value') }}
                </div>
                <div class="content">
                  {{ $getProperty(selectedSectionInfo, 'defaultValue', 'N/A') }}
                </div>
              </el-col>
              <el-col
                v-if="!$validation.isEmpty(selectedSectionInfo.failureValue)"
                class="scope d-flex flex-direction-column mB10"
                :span="12"
              >
                <div v-if="inputType === 'Option'">
                  <div class="title">
                    {{ $t('common.wo_report.deviation_value') }}
                  </div>
                  <div class="content">
                    {{
                      $getProperty(selectedSectionInfo, 'failureValue', 'N/A')
                    }}
                  </div>
                </div>
                <div v-if="inputType === 'Number'">
                  <div class="title">
                    {{ $t('jobplan.deviation_condition') }}
                  </div>
                  <div class="content mL7">
                    {{ deviationOperator }}
                    {{
                      $getProperty(selectedSectionInfo, 'failureValue', 'N/A')
                    }}
                  </div>
                </div>
              </el-col>
            </el-row>
            <div
              v-if="!$validation.isEmpty(selectedSectionInfo.createWoOnFailure)"
            >
              <div class="title mT20">
                {{ $t('jobplan.deviation_Wo') }}
              </div>
              <div v-if="createWoOnFailureConfigured" class="content mT10">
                {{ $t('jobplan.configured_deviation_Wo') }}
              </div>
              <div v-else class="content mT10">
                {{ $t('jobplan.not_configured_deviation_Wo') }}
              </div>
            </div>
          </div>
          <div v-else class="mT15 content">{{ $t('jobplan.not_defined') }}</div>
        </div>
      </div>
      <div class="scope-seperator"></div>

      <!-- Data Validation -->
      <div v-if="inputType === 'Number'" class="pL16 pT10 pR15 d-flex">
        <div class="d-flex flex-direction-column mL30 width100">
          <div class="name-and-desc">
            <div class="section-name">{{ $t('jobplan.data_validation') }}</div>
            <div class="content text-capitalize" v-if="dataValidationEnabled">
              {{ selectedSectionInfo.validation }}
            </div>
          </div>
          <el-row
            v-if="selectedSectionInfo.validation === 'safeLimit'"
            class="d-flex mT15"
          >
            <el-col
              class="scope d-flex flex-direction-column mB10 mT15"
              :span="8"
            >
              <div class="title">{{ $t('jobplan.min_value') }}</div>
              <div class="content">
                {{ $getProperty(selectedSectionInfo, 'minSafeLimit', 'N/A') }}
              </div>
            </el-col>
            <el-col
              class="scope d-flex flex-direction-column mB10  mT15"
              :span="12"
            >
              <div class="title">{{ $t('jobplan.max_value') }}</div>
              <div class="content">
                {{ $getProperty(selectedSectionInfo, 'maxSafeLimit', 'N/A') }}
              </div>
            </el-col>
          </el-row>
          <div v-if="!dataValidationEnabled" class="mT15 content">
            {{ $t('jobplan.not_defined') }}
          </div>
        </div>
      </div>
      <div v-if="inputType === 'Number'" class="scope-seperator"></div>
      <!-- Validations -->
      <div class="pL16 pT20 pR15 d-flex">
        <div class="name-icon">
          <fc-icon group="default" name="verified-check"></fc-icon>
        </div>
        <div class="d-flex flex-direction-column mL10 width100">
          <div class="name-and-desc">
            <div class="section-name">{{ $t('jobplan.validation') }}</div>
            <div class="content">
              {{ validationFields }}
            </div>
          </div>
          <div v-if="inputType === 'Option'">
            <el-row v-if="validationEnabled" class="d-flex">
              <el-col
                class="scope d-flex flex-direction-column mB10 mT15"
                :span="8"
                v-if="attachmentRequired"
              >
                <div class="title">{{ attachmentTitle }}</div>
                <div class="content">{{ attachmentOptions }}</div>
              </el-col>
              <el-col
                v-if="remarksRequired"
                class="scope d-flex flex-direction-column mB10  mT15"
                :span="12"
              >
                <div class="title">{{ remarksTitle }}</div>
                <div class="content">{{ remarksOptions }}</div>
              </el-col>
            </el-row>
            <div v-else class="mT15 content">
              {{ $t('jobplan.not_defined') }}
            </div>
          </div>
          <div v-if="!validationEnabled" class="mT15 content">
            {{ $t('jobplan.not_defined') }}
          </div>
        </div>
      </div>
      <div class="scope-seperator"></div>
    </div>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import {
  JOB_PLAN_SECTION_SCOPE,
  JOB_PLAN_TASK_SCOPE,
} from 'src/newapp/components/tasks-creation/utils/scope-util.js'
import { API } from '@facilio/api'
import { getRelatedFieldName } from 'src/util/relatedFieldUtil'

export default {
  name: 'JPSettingDialog',
  props: ['selectedSectionInfo', 'showSettingDialog', 'isSection'],
  created() {
    this.isEmpty = isEmpty
  },
  data() {
    return {
      optionsList: [],
      //woForms: [],
      operatorHash: { 9: '=', 10: '!=', 11: '<', 12: '<=', 13: '>', 14: '>=' },
    }
  },
  mounted() {
    let { inputType } = this
    if (inputType === 'Option') this.loadOptions()
    //this.loadWoForms()
  },
  computed: {
    inputEnabled() {
      let { selectedSectionInfo } = this
      let { inputType } = selectedSectionInfo || {}

      return !isEmpty(inputType) && inputType !== 1
    },
    inputType() {
      let { selectedSectionInfo } = this
      let { inputType } = selectedSectionInfo || {}
      let inputOption = ''

      if (!isEmpty(inputType)) {
        let inputHash = {
          2: 'Reading',
          3: 'Text',
          4: 'Number',
          5: 'Option',
        }
        inputOption = !isEmpty(inputHash[inputType]) ? inputHash[inputType] : ''
      }
      return inputOption
    },
    createWoOnFailureConfigured() {
      let { selectedSectionInfo } = this
      let { createWoOnFailure = false } = selectedSectionInfo || {}
      return createWoOnFailure
    },
    attachmentRequired() {
      let { selectedSectionInfo } = this
      let { attachmentRequired } = selectedSectionInfo || {}

      return attachmentRequired
    },
    remarksRequired() {
      let { selectedSectionInfo } = this
      let { remarksRequired } = selectedSectionInfo || {}

      return remarksRequired
    },
    deviationOperator() {
      let { selectedSectionInfo, operatorHash } = this
      let { deviationOperatorId } = selectedSectionInfo || {}

      return `${operatorHash[deviationOperatorId]} ` || ''
    },
    validationFields() {
      let { attachmentRequired, remarksRequired } = this
      let validation = ''

      if (attachmentRequired) validation = 'Photo'
      if (remarksRequired) validation = `${validation}, Remarks`
      return validation
    },
    attachmentTitle() {
      let { selectedSectionInfo } = this
      let { attachmentOption } = selectedSectionInfo || {}
      if (attachmentOption === 'all') return this.$t('jobplan.photo_all')
      else return this.$t('jobplan.photo_specific')
    },
    remarksTitle() {
      let { selectedSectionInfo } = this
      let { remarkOption } = selectedSectionInfo || {}
      if (remarkOption === 'all') return this.$t('jobplan.remarks_all')
      else return this.$t('jobplan.remarks_specific')
    },
    attachmentOptions() {
      let { selectedSectionInfo } = this
      let { attachmentOption, attachmentOptionValues } =
        selectedSectionInfo || {}

      if (attachmentOption === 'specific')
        return attachmentOptionValues.toString()
      else return ``
    },
    remarksOptions() {
      let { selectedSectionInfo } = this
      let { remarkOption, remarkOptionValues } = selectedSectionInfo || {}

      if (remarkOption === 'specific') return remarkOptionValues.toString()
      else return ``
    },
    dataValidationEnabled() {
      let { selectedSectionInfo } = this
      let { validation } = selectedSectionInfo || {}

      return !isEmpty(validation)
    },
    validationEnabled() {
      let { attachmentRequired, remarksRequired } = this
      return attachmentRequired || remarksRequired
    },
    scopeName() {
      let { selectedSectionInfo, isSection } = this
      let scopeName = ''
      if (isSection) scopeName = this.getSectionScope(selectedSectionInfo)
      else scopeName = this.getTaskScope(selectedSectionInfo)
      return scopeName
    },
    canShowResourceCategory() {
      let { scopeName } = this
      return ['Asset Category', 'Space Category'].includes(scopeName)
    },
    scopeCategoryName() {
      let { scopeName, selectedSectionInfo } = this
      if (scopeName === 'Asset Category') {
        let { assetCategory } = selectedSectionInfo || {}
        return this.$getProperty(assetCategory, 'displayName', '---')
      } else {
        let { spaceCategory } = selectedSectionInfo || {}
        return this.$getProperty(spaceCategory, 'displayName', '---')
      }
    },
    readingFieldName() {
      let { selectedSectionInfo } = this
      let { readingField } = selectedSectionInfo || {}
      return this.$getProperty(readingField, 'displayName', '---')
    },
    sectionName() {
      let { selectedSectionInfo, isSection } = this

      if (isSection)
        return this.$getProperty(selectedSectionInfo, 'name', 'N/A')
      else return this.$getProperty(selectedSectionInfo, 'subject', 'N/A')
    },
  },
  methods: {
    closeDialog() {
      this.$emit('update:showSettingDialog', false)
    },
    async loadOptions() {
      let { isSection, selectedSectionInfo } = this
      let { id } = selectedSectionInfo || {}

      let optionsModuleName
      let currentModuleName
      if (isSection) {
        optionsModuleName = 'jobPlanSectionInputOptions'
        currentModuleName = 'jobplansection'
      } else {
        optionsModuleName = 'jobPlanTaskInputOptions'
        currentModuleName = 'jobplantask'
      }
      let relatedFieldName = getRelatedFieldName(
        currentModuleName,
        optionsModuleName
      )
      let relatedConfig = {
        moduleName: currentModuleName,
        id,
        relatedModuleName: optionsModuleName,
        relatedFieldName,
      }
      let { error, list } = await API.fetchAllRelatedList(relatedConfig)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$set(this, 'optionsList', list)
      }
    },
    getSectionScope(section) {
      let { jobPlanSectionCategory, jobPlan } = section || {}
      let { jobPlanCategory } = jobPlan || {}
      let sectionScopeOptions = JOB_PLAN_SECTION_SCOPE[jobPlanCategory] || []
      let sectionScope = Object.keys(sectionScopeOptions).find(
        key => sectionScopeOptions[key] === jobPlanSectionCategory
      )

      return !isEmpty(sectionScope) ? sectionScope : '---'
    },
    // async loadWoForms() {
    //   let url = `/v2/forms?moduleName=workorder`
    //   let { error, data } = await API.get(url)

    //   if (error) {
    //     this.$message.error('Error Occured')
    //   } else {
    //     let { forms = [] } = data || {}
    //     this.woForms = forms.filter(
    //       form => form.id > 0 && form.name !== 'default_workorder_web'
    //     )
    //   }
    // },
    getTaskScope(task) {
      let { taskSection, jobPlanTaskCategory, jobPlan } = task || {}
      let { jobPlanCategory } = jobPlan || {}
      let { jobPlanSectionCategory } = taskSection || {}
      let currentSectionScopeOptions = JOB_PLAN_SECTION_SCOPE[jobPlanCategory]
      let taskScopeKey = Object.keys(currentSectionScopeOptions).find(
        key => currentSectionScopeOptions[key] === jobPlanSectionCategory
      )
      let taskScopeOptions = JOB_PLAN_TASK_SCOPE[taskScopeKey] || {}
      let taskScope = Object.keys(taskScopeOptions).find(
        key => taskScopeOptions[key] === jobPlanTaskCategory
      )

      return !isEmpty(taskScope) ? taskScope : '---'
    },
  },
}
</script>
<style scoped lang="scss">
.setting-container {
  min-height: 300px;
  max-height: 450px;
  overflow-y: scroll;
  letter-spacing: 0.2px;
  .scope-seperator {
    width: 100%;
    margin-top: 10px;
    margin-bottom: 10px;
    border-bottom: 1.5px solid #f0f0f0;
  }
  .fill-light {
    color: #324056 !important;
  }
  .section-name {
    font-size: 14px;
    font-weight: bold;
    letter-spacing: 0.3px;
    color: #324056;
  }
  .title {
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 0.2px;
    color: #324056;
  }

  .content {
    margin-top: 7px;
    font-size: 14px;
    line-height: 1.29;
    letter-spacing: 0.2px;
    color: #737376;
  }
  .desc-content {
    word-break: break-word;
    white-space: pre-line;
  }
}
</style>
<style lang="scss">
.jp-setting-dialog {
  .el-dialog {
    padding-bottom: 55px !important;
  }
}
</style>
