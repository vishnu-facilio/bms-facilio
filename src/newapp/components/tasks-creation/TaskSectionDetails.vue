<template>
  <el-dialog
    :visible.sync="showSectionSettings"
    :append-to-body="true"
    :title="dialogTitle"
    width="45%"
    class="fc-dialog-center-container jp-task-dialog"
    :before-close="cancelSectionSettings"
  >
    <div class="task-section-dialog jp-section-dialog ">
      <div class="jp-banner-space">
        <div :class="bannerClass">
          <i class="fa" :class="bannerIcon" aria-hidden="true"></i>
          {{ bannerText }}
        </div>
      </div>
      <template v-if="isPreRequisite">
        <div class="pL20 pT10 pR10 width100">
          <div class="scope-heading mT5">
            {{ $t('jobplan.prereq_type') }}
          </div>
          <el-checkbox
            v-model="sectionSettingsObj.enableOption"
            class="mT15 mL5 scope-txt14 scope-border"
            >{{ $t('jobplan.enable_option') }}</el-checkbox
          >
        </div>
        <div class="pL20 pR10 mT15">
          <div class="scope-txt14">
            {{ $t('maintenance._workorder.options') }}
          </div>
          <el-row
            :key="index"
            v-for="(option, index) in sectionSettingsObj.options"
            class="mT20"
          >
            <el-col :span="20">
              <el-input
                type="text"
                v-model="option.value"
                :placeholder="`Option ${index + 1}`"
                class="fc-input-full-border2 width500 task-option-input"
              >
                <template slot="prepend">{{ index + 1 }}</template>
              </el-input>
            </el-col>
          </el-row>
        </div>
        <div class="scope-seperator"></div>
        <div class="pL20 pR10 mT10">
          <div class="scope-heading">
            {{ $t('jobplan.validation') }}
          </div>
          <el-checkbox
            v-model="sectionSettingsObj.attachmentRequired"
            class="mT15 mL5 scope-border"
            >{{ $t('jobplan.photo_mandatory') }}</el-checkbox
          >
        </div>
        <div class="scope-seperator"></div>
      </template>
      <template v-else>
        <div class="mT10 mB10 pL20 pR10">
          <div class="scope-txt14 mT30">
            {{ $t('common.wo_report.section_name') }}
          </div>
          <el-input
            v-model="sectionSettingsObj.name"
            :placeholder="$t('common.placeholders.write_section_name')"
            class="fc-input-full-border2 mT10 width75"
          ></el-input>
        </div>
        <div class="scope-seperator"></div>
        <div class="mT10 mB10 pL20 pR10" v-if="canShowScope">
          <div class="scope-heading">
            {{ $t('jobplan.section_scope') }}
          </div>
          <div class="jp-scope-desc mT5">
            {{ $t('jobplan.section_scope_desc') }}
          </div>
          <div class="d-flex mT15">
            <div>
              <div class="scope-txt14  fw4">
                {{ $t('jobplan.category') }}
              </div>
              <el-select
                class="fc-input-full-border2 mT10  width250px"
                clearable
                filterable
                v-model="sectionSettingsObj.jobPlanSectionCategory"
                @change="resetResources()"
                :placeholder="$t('jobplan.select_scope')"
              >
                <el-option
                  v-for="(key, label) in scopeOptions"
                  :key="key"
                  :label="label"
                  :value="Number(key)"
                ></el-option>
              </el-select>
            </div>
            <div class="mL15" v-if="canShowLookup">
              <div class="scope-txt14  fw4">
                {{ lookupDisplayName }}
              </div>
              <FLookupField
                class="width250px mT10"
                ref="section-resource-field"
                :model.sync="sectionSettingsObj.resource"
                :field="selectedLookupField"
                @recordSelected="setSelectedLookupValue"
                @showLookupWizard="showLookupWizard"
              />
            </div>
          </div>
        </div>
        <div class="scope-seperator" v-if="canShowScope"></div>
        <div class="mT20 mB10 pL20 pR10">
          <div class="width100">
            <div class="scope-heading mT20">
              {{ $t('jobplan.input_config') }}
            </div>
            <el-checkbox
              @change="resetInputs"
              v-model="sectionSettingsObj.enableInput"
              class="mT15 mL5 scope-txt14 scope-border"
              >{{ $t('common.wo_report.enable_input') }}</el-checkbox
            >
          </div>
        </div>
        <div v-if="sectionSettingsObj.enableInput" class="pL55 pR10">
          <div class>
            <div
              v-if="sectionSettingsObj.enableInput"
              class="mT20 form-task-radio"
            >
              <div class="scope-txt14 mT30">
                {{ $t('jobplan.input_type') }}
              </div>
              <el-radio
                @change="onSelectInput"
                v-model="sectionSettingsObj.inputType"
                class="fc-radio-btn mT20 scope-border"
                color="secondary"
                v-for="(label, key) in inputOptions"
                :key="key"
                :label="Number(key)"
                >{{ label }}</el-radio
              >
            </div>
            <div
              v-if="
                isPreferredInputType('Option') ||
                  isPreferredInputType('Boolean')
              "
              class="mT30"
            >
              <div class="scope-txt14">
                {{ $t('maintenance._workorder.options') }}
              </div>
              <div
                :key="index"
                v-for="(option, index) in sectionSettingsObj.inputOptions"
                class="mT20"
              >
                <el-row class="visibility-visible-actions">
                  <el-col :span="20">
                    <el-input
                      type="text"
                      v-model="option.value"
                      :placeholder="`Option ${index + 1}`"
                      class="fc-input-full-border2 width500 task-option-input"
                    >
                      <template slot="prepend">{{ index + 1 }}</template>
                    </el-input>
                  </el-col>
                  <el-col
                    v-if="isPreferredInputType('Option')"
                    :span="1"
                    class="visibility-hide-actions pointer mL10"
                  >
                    <div
                      @click="addTaskOption(sectionSettingsObj)"
                      class="mT12"
                    >
                      <inline-svg src="add-icon" iconClass="icon icon-xxl" />
                    </div>
                  </el-col>
                  <el-col
                    v-if="isPreferredInputType('Option')"
                    :span="2"
                    class="visibility-hide-actions pointer"
                  >
                    <div
                      @click="
                        removeOption(sectionSettingsObj.inputOptions, index)
                      "
                      v-show="sectionSettingsObj.inputOptions.length > 2"
                      class="mT12 mL10"
                    >
                      <inline-svg iconClass="icon icon-xxl" src="remove-icon" />
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>
          </div>
          <div
            v-if="
              !isPreferredInputType('Reading') && sectionSettingsObj.inputType
            "
            class="d-flex"
          >
            <div>
              <div class="scope-txt14 mT20 mb5">
                {{ $t('common._common.default_value') }}
              </div>
              <el-select
                v-if="
                  isPreferredInputType('Option') ||
                    isPreferredInputType('Boolean')
                "
                v-model="sectionSettingsObj.defaultValue"
                clearable
                filterable
                :placeholder="$t('jobplan.select_default')"
                class="fc-input-full-border-select2 width250px mT10"
              >
                <el-option
                  v-for="(option, index) in sectionSettingsObj.inputOptions"
                  :label="option.value"
                  :key="index"
                  :value="index"
                ></el-option>
              </el-select>
              <el-input
                v-else
                :type="isPreferredInputType('Number') ? 'number' : 'text'"
                v-model="sectionSettingsObj.defaultValue"
                :placeholder="$t('jobplan.enter_default')"
                class="fc-input-full-border2 width250px mT10 task-option-input"
              ></el-input>
            </div>
            <div v-if="isPreferredInputType('Option')" class="mL15">
              <div class="scope-txt14 mT20 mb5">
                {{ $t('common.wo_report.deviation_value') }}
              </div>
              <el-select
                v-model="sectionSettingsObj.failureValue"
                filterable
                clearable
                @change="resetWoTemplate"
                :placeholder="$t('common.placeholders.select_deviation_value')"
                class="fc-input-full-border-select2 width250px mT10"
              >
                <el-option
                  v-for="(option, index) in sectionSettingsObj.inputOptions"
                  :label="option.value"
                  :key="index"
                  :value="index"
                ></el-option>
              </el-select>
            </div>
          </div>
          <div
            v-if="
              !(isPreferredInputType('Text') || isPreferredInputType('Reading'))
            "
          >
            <template v-if="isPreferredInputType('Number')">
              <div class="scope-txt14 f15 mT20 mb5">
                {{ $t('common.wo_report.deviation_condition') }}
              </div>
              <el-row :gutter="20">
                <el-col :span="11">
                  <div class="scope-txt14 mT10 mb5">
                    {{ $t('common.wo_report.Operator') }}
                  </div>
                  <el-select
                    v-model="sectionSettingsObj.deviationOperatorId"
                    clearable
                    filterable
                    :placeholder="$t('dashboardfilters.select_operator')"
                    class="fc-input-full-border-select2 width250px mT10"
                  >
                    <el-option
                      v-for="(operator, index) in operatorsList"
                      :key="`section-operator-${index}`"
                      :label="operator.label"
                      :value="operator.value"
                    />
                  </el-select>
                </el-col>
                <el-col :span="11">
                  <div class="scope-txt14 mT10 mb5">
                    {{ $t('asset.readings.value') }}
                  </div>
                  <el-input
                    type="number"
                    v-model="sectionSettingsObj.failureValue"
                    :disabled="
                      $validation.isEmpty(
                        sectionSettingsObj.deviationOperatorId
                      )
                    "
                    @input="resetWoTemplate(...arguments, 'number')"
                    placeholder="Enter value"
                    class="fc-input-full-border2 width250px task-option-input mT10"
                  ></el-input>
                </el-col>
              </el-row>
            </template>
            <div class="mT10" v-if="canShowConfig">
              <el-checkbox
                v-model="sectionSettingsObj.createWoOnFailure"
                class="width-full pT20 pB10 scope-border"
                @change="resetTemplate"
                >{{
                  $t('common.wo_report.create_workorder_on_deviation')
                }}</el-checkbox
              >
              <!-- <el-select
                v-if="sectionSettingsObj.createWoOnFailure"
                v-model="sectionSettingsObj.woCreateFormId"
                clearable
                filterable
                :placeholder="$t('common.placeholders.select_template')"
                class="fc-input-full-border-select2 width250px mT10"
              >
                <el-option
                  v-for="(template, index) in woForms"
                  :label="template.displayName"
                  :key="index"
                  :value="template.id"
                ></el-option>
              </el-select> -->
            </div>
          </div>
          <!-- <div v-if="isPreferredInputType('Number')" class="mT30">
            <div class="scope-txt14 mb5">
              {{ $t('jobplan.data_validation') }}
            </div>
            <el-select
              v-model="sectionSettingsObj.validation"
              clearable
              filterable
              :placeholder="$t('jobplan.select_validation')"
              class="fc-input-full-border-select2 width250px mT10"
            >
              <el-option
                v-for="option in validationOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              >
              </el-option>
            </el-select>
            <div v-if="safeLimitSelected">
              <el-row class="mT20">
                <el-col :span="11">
                  <div class="scope-txt14 mb5">
                    {{ $t('jobplan.min_value') }}
                  </div>
                  <el-input
                    type="number"
                    v-model="sectionSettingsObj.minSafeLimit"
                    :placeholder="$t('jobplan.enter_min_value')"
                    class="fc-input-full-border-select2 width250px mT10"
                  ></el-input>
                </el-col>
                <el-col :span="11">
                  <div class="scope-txt14 mb5">
                    {{ $t('jobplan.max_value') }}
                  </div>
                  <el-input
                    type="number"
                    v-model="sectionSettingsObj.maxSafeLimit"
                    :placeholder="$t('jobplan.enter_max_value')"
                    class="fc-input-full-border-select2 width250px mT10"
                  ></el-input>
                </el-col>
              </el-row>
            </div>
          </div> -->
        </div>
        <div class="scope-seperator" :class="emptyInput"></div>
        <div class="pL20 pR10 mT20">
          <div class="scope-heading">
            {{ $t('jobplan.validation') }}
          </div>
          <el-checkbox
            @change="resetOptionValues('attachment')"
            v-model="sectionSettingsObj.attachmentRequired"
            class="mT15 mL5 scope-border"
            >{{ $t('jobplan.photo_mandatory') }}</el-checkbox
          >
          <div v-if="isAttachmentOptionRequired" class="pL35 pR10">
            <el-row class="mT20">
              <el-col :span="12">
                <div class="scope-txt14 mb5">
                  {{ $t('jobplan.attachments_options') }}
                </div>
                <el-radio-group
                  v-model="sectionSettingsObj.attachmentOption"
                  @change="resetAttachmentValues"
                  class="mT10"
                >
                  <el-radio label="all" class="fc-radio-btn scope-border">{{
                    $t('jobplan.all')
                  }}</el-radio>
                  <el-radio
                    label="specific"
                    class="fc-radio-btn scope-border"
                    >{{ $t('jobplan.specific') }}</el-radio
                  >
                </el-radio-group>
                <el-select
                  v-if="sectionSettingsObj.attachmentOption === 'specific'"
                  v-model="sectionSettingsObj.attachmentOptionValues"
                  multiple
                  collapse-tags
                  clearable
                  filterable
                  :placeholder="$t('common.placeholders.select_values')"
                  class="fc-input-full-border-select2 fc-tag el-select-block width250px mT10"
                >
                  <el-option
                    v-for="(option, index) in sectionSettingsObj.inputOptions"
                    :label="option.value"
                    :key="index"
                    :value="index"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
          </div>
          <div class="mT20" v-if="isPreferredInputType('Option')">
            <el-checkbox
              @change="resetOptionValues('remark')"
              class="scope-border mL5"
              v-model="sectionSettingsObj.remarksRequired"
            >
              {{ $t('jobplan.remarks_mandatory') }}
            </el-checkbox>
            <div v-if="isRemarksRequired" class="pL35 pR10">
              <el-row class="mT20">
                <el-col :span="12">
                  <div class="scope-txt14 mb5">
                    {{ $t('jobplan.remark_options') }}
                  </div>
                  <el-radio-group
                    v-model="sectionSettingsObj.remarkOption"
                    @change="resetRemarksValues"
                    class="mT10"
                  >
                    <el-radio label="all" class="fc-radio-btn scope-border">{{
                      $t('jobplan.all')
                    }}</el-radio>
                    <el-radio
                      label="specific"
                      class="fc-radio-btn scope-border"
                      >{{ $t('jobplan.specific') }}</el-radio
                    >
                  </el-radio-group>
                  <el-select
                    v-if="sectionSettingsObj.remarkOption === 'specific'"
                    v-model="sectionSettingsObj.remarkOptionValues"
                    multiple
                    collapse-tags
                    clearable
                    filterable
                    :placeholder="$t('common.placeholders.select_values')"
                    class="fc-input-full-border-select2 fc-tag el-select-block width250px mT10"
                  >
                    <el-option
                      v-for="(option, index) in sectionSettingsObj.inputOptions"
                      :label="option.value"
                      :key="index"
                      :value="index"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>

        <FLookupFieldWizard
          v-if="canShowLookupWizard"
          :canShowLookupWizard.sync="canShowLookupWizard"
          :selectedLookupField="selectedLookupField"
          @setLookupFieldValue="setWizardValue"
        />
        <div class="scope-seperator"></div>
      </template>
      <div class="modal-dialog-footer jp-task-dialog-footer">
        <div class="pR20 pB20 d-flex">
          <el-button
            type="primary"
            class="jp-task-save"
            @click="saveSectionSettings"
            >{{ $t('jobplan.save') }}</el-button
          >
          <el-button
            plain
            @click="cancelSectionSettings"
            class="jp-task-cancel mL15"
            >{{ $t('agent.agent.cancel') }}</el-button
          >
        </div>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { isEmpty, isNumber } from '@facilio/utils/validation'
import {
  JOB_PLAN_SECTION_SCOPE,
  SCOPE_LOOKUP_FIELDS,
} from './utils/scope-util.js'
import FLookupField from 'src/components/forms/FLookupField.vue'
import cloneDeep from 'lodash/cloneDeep'
import FLookupFieldWizard from 'src/components/FLookupFieldWizard.vue'

const sectionSettingConfig = {
  attachmentRequired: false,
  enableInput: false,
  validation: null,
  // minSafeLimit: null,
  // maxSafeLimit: null,
  inputType: null,
  inputOptions: [],
  defaultValue: '',
  failureValue: '',
  deviationOperatorId: null,
  createWoOnFailure: null,
  //woCreateFormId: null,
  remarkOption: 'all',
  remarkOptionValues: [],
  remarksRequired: false,
  attachmentOptionValues: [],
  attachmentOption: 'all',
  resource: null,
}

const sectionPrerequisiteConfig = {
  enableOption: true,
  options: [{ value: 'Yes' }, { value: 'No' }],
  attachmentRequired: false,
}

export default {
  name: 'TaskSectionDetails',
  props: [
    'showSectionSettings',
    'selectedSection',
    //'woForms',
    'formModel',
    'isPreRequisite',
  ],
  components: { FLookupField, FLookupFieldWizard },
  data() {
    return {
      sectionSettingsObj: {},
      canShowLookupWizard: false,
      sectionOption: 1,
      inputOptions: {
        3: 'Text',
        4: 'Number',
        5: 'Option',
      },
      inputTypeHash: {
        2: 'Reading',
        3: 'Text',
        4: 'Number',
        5: 'Option',
        6: 'Boolean',
      },
      // validationOptions: [
      //   { label: 'None', value: 'none' },
      //   { label: 'Incremental', value: 'incremental' },
      //   { label: 'Decremental', value: 'decremental' },
      //   { label: 'Safe Limit', value: 'safeLimit' },
      // ],
      operatorsList: [
        { label: '=', value: 9 },
        { label: '!=', value: 10 },
        { label: '<', value: 11 },
        { label: '<=', value: 12 },
        { label: '>', value: 13 },
        { label: '>=', value: 14 },
      ],
      error: false,
      errorText: '',
      canShowLookup: false,
    }
  },
  computed: {
    dialogTitle() {
      let { isPreRequisite } = this
      return isPreRequisite
        ? this.$t('jobplan.prereq_details')
        : this.$t('jobplan.configure_task_section')
    },
    bannerText() {
      let { error, errorText } = this
      return error ? errorText : this.$t('jobplan.alert_info')
    },
    bannerClass() {
      let { error } = this
      return error ? 'jp-error-info' : 'fc-alert-msg-info'
    },
    bannerIcon() {
      let { error } = this
      return error ? 'fa-info-circle' : 'fa-exclamation-triangle'
    },
    scopeOptions() {
      let { formModel } = this
      let { jobPlanCategory } = formModel || {}
      let { id: jobPlanScope } = jobPlanCategory || {}

      let sectionScopeOptions = JOB_PLAN_SECTION_SCOPE[jobPlanScope] || []
      return sectionScopeOptions
    },
    canShowScope() {
      let { formModel } = this
      let { jobPlanCategory } = formModel || {}
      let { id: jobPlanScope } = jobPlanCategory || {}

      return !isEmpty(jobPlanScope)
    },
    emptyInput() {
      let { sectionSettingsObj } = this
      let { enableInput } = sectionSettingsObj || {}
      return !enableInput ? 'mT50' : ''
    },
    lookupDisplayName() {
      let { selectedLookupField } = this
      let { field } = selectedLookupField || {}
      let { lookupModule } = field || {}

      return this.$getProperty(lookupModule, 'displayName', '')
    },
    // safeLimitSelected() {
    //   let { sectionSettingsObj } = this
    //   let { enableInput, validation } = sectionSettingsObj || {}

    //   return enableInput && validation === 'safeLimit'
    // },
    isAttachmentOptionRequired() {
      let { sectionSettingsObj, inputTypeHash } = this
      let { attachmentRequired, inputType } = sectionSettingsObj || {}

      return (
        attachmentRequired &&
        ['Option', 'Boolean'].includes(inputTypeHash[inputType])
      )
    },
    isRemarksRequired() {
      let { sectionSettingsObj, inputTypeHash } = this
      let { remarksRequired, inputType } = sectionSettingsObj || {}

      return (
        remarksRequired &&
        ['Option', 'Boolean'].includes(inputTypeHash[inputType])
      )
    },
    canShowConfig() {
      let { sectionSettingsObj, inputTypeHash } = this
      let { enableInput, inputType, failureValue } = sectionSettingsObj || {}
      let isFailureValueSelected = false

      if (inputTypeHash[inputType] === 'Option') {
        isFailureValueSelected = isNumber(failureValue)
          ? failureValue >= 0
          : false
      } else {
        isFailureValueSelected = !isEmpty(failureValue)
      }
      return (
        enableInput &&
        isFailureValueSelected &&
        ['Option', 'Boolean', 'Number'].includes(inputTypeHash[inputType])
      )
    },
    attachmentRequired() {
      let { sectionSettingsObj } = this
      let { attachmentRequired } = sectionSettingsObj || {}

      return attachmentRequired && this.isPreferredInputType('Option')
    },
    selectedLookupField() {
      let { sectionSettingsObj } = this
      let { jobPlanSectionCategory } = sectionSettingsObj || {}

      return SCOPE_LOOKUP_FIELDS[jobPlanSectionCategory] || {}
    },
  },
  created() {
    let { selectedSection, isPreRequisite } = this
    let sectionObj = isPreRequisite
      ? cloneDeep(sectionPrerequisiteConfig)
      : cloneDeep(sectionSettingConfig)
    let sectionData = { ...sectionObj, ...selectedSection }

    this.sectionSettingsObj = sectionData
    if (!isPreRequisite) this.fillDefaultScope()
  },
  watch: {
    selectedLookupField: {
      async handler(newVal) {
        this.canShowLookup = false
        if (!isEmpty(newVal)) {
          this.$nextTick(() => {
            this.canShowLookup = true
          })
        }
      },
      deep: true,
    },
    sectionSettingsObj: {
      async handler(newVal) {
        this.validateSection(newVal)
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    fillDefaultScope() {
      let { sectionSettingsObj, canShowScope } = this
      let { jobPlanSectionCategory } = sectionSettingsObj || {}
      if (isEmpty(jobPlanSectionCategory) && canShowScope) {
        this.$set(this.sectionSettingsObj, 'jobPlanSectionCategory', 5)
      }
    },
    isPreferredInputType(input) {
      let { sectionSettingsObj, inputTypeHash } = this
      let { enableInput } = sectionSettingsObj || {}
      let inputType = -1
      if (enableInput) {
        inputType = this.$getProperty(sectionSettingsObj, 'inputType', '-1')
      }

      return !isEmpty(inputType) ? inputTypeHash[inputType] === input : false
    },
    setSelectedLookupValue() {
      let { selectedLookupField, sectionSettingsObj } = this
      let { lookupModuleName } = selectedLookupField || {}
      let { resource = null } = sectionSettingsObj || {}

      if (lookupModuleName === 'assetcategory') {
        this.$set(this.sectionSettingsObj, 'assetCategory', {
          id: resource,
        })
      } else {
        this.$set(this.sectionSettingsObj, 'spaceCategory', {
          id: resource,
        })
      }
    },
    setWizardValue(props) {
      let { field } = props
      let { selectedItems = [] } = field
      let selectedItemIds = []

      if (!isEmpty(selectedItems)) {
        selectedItemIds = selectedItems.find(item => item.value).value || {}
      }
      this.$set(this.sectionSettingsObj, 'resource', selectedItemIds)
      this.setSelectedLookupValue()
    },
    showLookupWizard(_, canShow) {
      this.canShowLookupWizard = canShow
    },
    resetInputs(val) {
      if (!val) {
        let { sectionSettingsObj } = this
        sectionSettingsObj = {
          ...sectionSettingsObj,
          remarksRequired: false,
          remarkOptionValues: null,
          inputOptions: [{ value: 'Choice1' }, { value: 'Choice 2' }],
          remarkOption: 'all',
          defaultValue: null,
          attachmentRequired: false,
          attachmentOptionValues: null,
          attachmentOption: 'all',
          failureValue: null,
          deviationOperatorId: null,
          createWoOnFailure: false,
          //woCreateFormId: null,
        }
        this.$set(this, 'sectionSettingsObj', sectionSettingsObj)
      }
    },
    //Reset WO Template Selection
    resetTemplate(reset) {
      if (reset) {
        let { sectionSettingsObj } = this
        this.sectionSettingsObj = {
          ...sectionSettingsObj,
          //woCreateFormId: null,
        }
      }
    },
    //Reset WO Template Selection if deviation value is reset
    resetWoTemplate(failureValue, type) {
      if (type === 'number') failureValue = Number(failureValue)
      let isFailureValueSelected =
        type === 'number' ? failureValue > 0 : isNumber(failureValue)
      if (!isFailureValueSelected) {
        let { sectionSettingsObj } = this
        this.sectionSettingsObj = {
          ...sectionSettingsObj,
          createWoOnFailure: false,
          woCreateFormId: null,
        }
      }
    },
    resetResources() {
      let { sectionSettingsObj } = this
      this.sectionSettingsObj = {
        ...sectionSettingsObj,
        resource: null,
        assetCategory: null,
        spaceCategory: null,
      }
    },
    addTaskOption(selectedTask) {
      let { inputOptions } = selectedTask || {}
      let optionValue = `Choice${inputOptions.length + 1}`
      selectedTask.inputOptions.push({ value: optionValue })
      this.sectionOption += 1
    },
    removeOption(list, index) {
      //Removing the deleted option if its selected in configuration
      let { sectionSettingsObj } = this
      let {
        remarkOptionValues,
        attachmentOptionValues,
        defaultValue,
        failureValue,
      } = sectionSettingsObj || {}

      if (defaultValue === index) defaultValue = null
      if (failureValue === index) failureValue = null

      //Removing remarkoption selected
      if (!isEmpty(remarkOptionValues)) {
        let remarkIndex = remarkOptionValues.findIndex(
          option => option === index
        )
        remarkOptionValues.splice(remarkIndex, 1)
      }

      //Removing attachmentoption selected
      if (!isEmpty(attachmentOptionValues)) {
        let attachmentIndex = attachmentOptionValues.findIndex(
          option => option === index
        )
        attachmentOptionValues.splice(attachmentIndex, 1)
      }

      this.sectionSettingsObj = {
        ...sectionSettingsObj,
        remarkOptionValues,
        attachmentOptionValues,
        defaultValue,
        failureValue,
      }

      list.splice(index, 1)
    },
    onSelectInput() {
      let { sectionSettingsObj } = this
      let { inputType } = sectionSettingsObj || {}
      if (inputType === 5) {
        this.sectionSettingsObj.inputOptions = []
        this.addTaskOption(this.sectionSettingsObj)
        this.addTaskOption(this.sectionSettingsObj)
      } else if (inputType === 6) {
        this.taskSettingsEdit.inputOptions = [
          { label: '', value: '' },
          { label: '', value: '' },
        ]
        let options = [
          { label: 'YES', value: 'YES' },
          { label: 'NO', value: 'NO' },
        ]
        this.$set(this.sectionSettingsObj, 'inputOptions', options)
      }
      this.resetInputs(false)
    },
    resetAttachmentValues(val) {
      if (val === 'all') {
        this.sectionSettingsObj.attachmentOptionValues = null
      }
    },
    resetRemarksValues(val) {
      if (val === 'all') {
        this.sectionSettingsObj.remarkOptionValues = null
      }
    },
    resetOptionValues(type) {
      if (type === 'remark') {
        this.sectionSettingsObj.remarkOptionValues = null
        this.sectionSettingsObj.remarkOption = null
      } else {
        this.sectionSettingsObj.attachmentOptionValues = null
        this.sectionSettingsObj.attachmentOption = null
      }
    },
    validateSection(section) {
      let { inputTypeHash } = this
      let {
        inputType,
        attachmentOption,
        attachmentOptionValues,
        remarkOption,
        remarkOptionValues,
        inputOptions,
        jobPlanSectionCategory,
        // createWoOnFailure,
        // woCreateFormId,
        deviationOperatorId,
        failureValue,
        // validation,
        // minSafeLimit,
        // maxSafeLimit,
        resource,
        defaultValue,
      } = section || {}

      let categoryHash = { 3: 'Space Category', 4: 'Asset Category' }
      if (
        ['Asset Category', 'Space Category'].includes(
          categoryHash[jobPlanSectionCategory]
        ) &&
        isEmpty(resource)
      ) {
        this.error = true
        if (categoryHash[jobPlanSectionCategory] === 'Asset Category') {
          this.errorText = this.$t('jobplan.select_asset_category')
        } else {
          this.errorText = this.$t('jobplan.select_space_category')
        }
        return !isEmpty(resource)
      }

      if (inputTypeHash[inputType] === 'Number') {
        let numberConfigValid = true
        if (deviationOperatorId && isEmpty(failureValue)) {
          numberConfigValid = false
          this.error = true
          this.errorText = this.$t('jobplan.input_deviation')
        }

        if (
          deviationOperatorId &&
          !isEmpty(failureValue) &&
          failureValue === defaultValue &&
          deviationOperatorId === 9
        ) {
          // OperatorID of = is 9
          numberConfigValid = false
          this.error = true
          this.errorText = this.$t('jobplan.same_def_deviation')
        }
        // if (validation === 'safeLimit') {
        //   let minLimitValid = true
        //   let maxLimitValid = true
        //   if (isEmpty(minSafeLimit)) {
        //     this.error = true
        //     this.errorText = this.$t('jobplan.minlimit_needed')
        //     minLimitValid = !isEmpty(minSafeLimit)
        //   }
        //   if (isEmpty(maxSafeLimit)) {
        //     this.error = true
        //     this.errorText = this.$t('jobplan.maxlimit_needed')
        //     maxLimitValid = !isEmpty(maxSafeLimit)
        //   }
        //   numberConfigValid = minLimitValid && maxLimitValid
        // }
        if (numberConfigValid) {
          this.error = false
          this.errorText = ''
        }
        return numberConfigValid
      }
      // if (createWoOnFailure && isEmpty(woCreateFormId)) {
      //   this.error = true
      //   this.errorText = this.$t('jobplan.select_wo_template')

      //   return !isEmpty(woCreateFormId)
      // }
      if (inputTypeHash[inputType] === 'Option') {
        let OptionNotEmpty = true
        let isAttachmentOptionValid = true
        let isRemarkOptionValid = true
        let deviationValueValid = true

        inputOptions.forEach(option => {
          let { value } = option || {}
          OptionNotEmpty = OptionNotEmpty && !isEmpty(value)
        })
        if (!OptionNotEmpty) {
          this.error = true
          this.errorText = this.$t('jobplan.option_needed')
        }
        if (!isEmpty(failureValue) && failureValue === defaultValue) {
          deviationValueValid = false
          this.error = true
          this.errorText = this.$t('jobplan.same_def_deviation')
        }

        if (
          attachmentOption === 'specific' &&
          isEmpty(attachmentOptionValues)
        ) {
          this.error = true
          this.errorText = this.$t('jobplan.select_attachment')
          isAttachmentOptionValid = !isEmpty(attachmentOptionValues)
        }

        if (remarkOption === 'specific' && isEmpty(remarkOptionValues)) {
          this.error = true
          this.errorText = this.$t('jobplan.select_remark')
          isRemarkOptionValid = !isEmpty(remarkOptionValues)
        }

        if (
          OptionNotEmpty &&
          isAttachmentOptionValid &&
          isRemarkOptionValid &&
          deviationValueValid
        ) {
          this.error = false
          this.errorText = ''
        }

        return (
          OptionNotEmpty &&
          isAttachmentOptionValid &&
          isRemarkOptionValid &&
          deviationValueValid
        )
      }
      this.error = false
      this.errorText = ''
      return true
    },
    serialize(section) {
      Object.keys(section).forEach(property => {
        if (isEmpty(section[property])) {
          delete section[property]
        }
      })
      return section
    },
    saveSectionSettings() {
      let { sectionSettingsObj } = this
      let isValid = this.validateSection(sectionSettingsObj)
      if (isValid) {
        let serializedSection = this.serialize(sectionSettingsObj)
        this.$emit('sectionUpdated', serializedSection)
      }
    },
    cancelSectionSettings() {
      this.$emit('update:showSectionSettings', false)
    },
  },
}
</script>
<style lang="scss" scoped>
.jp-section-dialog {
  height: 450px;
  overflow: scroll;

  .setting-desc {
    font-weight: 300;
  }
  .scope-heading {
    font-size: 14px;
    font-weight: bold;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.14;
    letter-spacing: 0.3px;
    color: #324056;
  }
  .scope-seperator {
    width: 100%;
    margin-top: 30px;
    margin-bottom: 15px;
    border-bottom: 1.5px solid #f0f0f0;
  }

  .fc-alert-msg-info .fa-exclamation-triangle {
    color: #324056;
    font-size: 16px;
    margin-right: 10px;
  }

  .jp-scope-desc {
    font-size: 14px;
    font-weight: normal;
    letter-spacing: 0.3px;
    color: #324056;
    opacity: 0.6;
  }
  .scope-txt14 {
    font-size: 14px;
    font-weight: 400;
    letter-spacing: 0.3px;
    color: #324056;
  }

  .jp-task-dialog-footer {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;
  }
  .jp-task-cancel {
    width: 90px;
    height: 42px;
    border-radius: 4px;
    border: solid 1px #38b2c2;
    text-transform: capitalize;
    color: #324056;
    font-weight: 500;
    font-size: 14px;
    margin-left: 15px !important;
    &:hover {
      background-color: #38b2c2;
      color: #ffffff;
    }
    &:active {
      color: #324056;
      background-color: #ffffff;
      border: solid 1px #38b2c2;
    }
  }
  .jp-task-save {
    width: 90px;
    height: 42px;
    border-radius: 4px;
    border-color: transparent;
    background-color: #3ab2c2;
    color: #fff;
    font-weight: 500;
    font-size: 14px;
    text-transform: capitalize;
    &:hover {
      background-color: #3ab2c2;
      color: #ffffff;
    }
    &:active {
      color: #fff;
      background-color: #3ab2c2;
      border: transparent;
    }
  }
  .fc-alert-msg-info {
    width: 100%;
    padding: 8px 16px;
    display: flex;
    align-items: center;
    box-sizing: border-box;
    font-size: 14px;
    letter-spacing: 0.3px;
    color: #324056;
    background-color: #eaf3ff;
    padding-left: 15% !important;
  }
  .jp-error-info {
    width: 100%;
    padding: 8px 16px;
    display: flex;
    align-items: center;
    box-sizing: border-box;
    font-size: 14px;
    letter-spacing: 0.3px;
    color: #324056;
    background-color: #f9ecec;
    padding-left: 25% !important;
    .fa-info-circle {
      font-size: 16px;
      margin-right: 10px;
    }
  }
  .jp-banner-space {
    position: sticky;
    z-index: 13;
    top: 0;
  }
}
</style>
<style lang="scss">
.jp-task-dialog {
  .el-dialog {
    padding-bottom: 65px !important;
    .el-dialog__header {
      padding: 10px 20px 5px;
      height: 45px;
    }
    .el-dialog__headerbtn {
      top: 13px !important;
    }
    .el-dialog__title {
      text-transform: capitalize;
      color: #324056;
      font-size: 16px;
      font-weight: bold;
      letter-spacing: 0.4px;
    }
    .el-dialog__body {
      padding: 0 !important;
    }
  }
  .scope-border {
    .el-radio__inner,
    .el-checkbox__inner {
      border: 1px solid #39b2c2 !important;
    }
  }
}
</style>
