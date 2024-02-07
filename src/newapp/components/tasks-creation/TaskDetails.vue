<template>
  <el-dialog
    :title="dialogTitle"
    :visible.sync="canShowTaskDetails"
    :append-to-body="true"
    width="45%"
    class="fc-dialog-center-container jp-task-dialog"
    :before-close="closeTaskDialog"
  >
    <div class="task-details jp-section-dialog">
      <div v-if="error" class="jp-banner-space">
        <div class="jp-error-info">
          <i class="fa fa-info-circle" aria-hidden="true"></i>
          {{ errorText }}
        </div>
      </div>
      <template v-if="isPreRequisite">
        <div class="pL20 pT10 pR10 width100">
          <div class="scope-heading mT5">
            {{ $t('jobplan.prereq_type') }}
          </div>
          <el-checkbox
            v-model="task.enableOption"
            class="mT15 mL5 scope-txt14 scope-border"
            >{{ $t('jobplan.enable_option') }}</el-checkbox
          >
        </div>
        <div class="pL20 pR10 mT15" v-if="task.enableOption">
          <div class="scope-txt14">
            {{ $t('maintenance._workorder.options') }}
          </div>
          <el-row
            :key="index"
            v-for="(option, index) in task.options"
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
            v-model="task.attachmentRequired"
            class="mT15 mL5 scope-border"
            >{{ $t('jobplan.photo_mandatory') }}</el-checkbox
          >
        </div>
        <div class="scope-seperator"></div>
      </template>
      <template v-else>
        <div class="scope-text14  pT20  pL20 pR10">
          {{ $t('jobplan.task_name') }}
        </div>
        <el-input
          v-model="task.subject"
          :placeholder="$t('common.placeholders.write_task_name')"
          class="fc-input-full-border2 pL20 pR10 mT10 width75"
        ></el-input>
        <div class="mT20 pL20 pR10">
          <div class="scope-text14 ">
            {{ $t('maintenance._workorder.description') }}
          </div>
          <el-input
            v-model="task.description"
            class="fc-input-full-border-textarea mT10 width75"
            type="textarea"
            auto-complete="off"
            :rows="1"
            resize="none"
            :placeholder="$t('common.placeholders.enter_description')"
            :autosize="{ minRows: 3, maxRows: 5 }"
          />
        </div>
        <div class="scope-seperator mT15"></div>
        <div class="mT10 mB10 d-flex pL20 pR10" v-if="canShowScope">
          <div>
            <div class="scope-heading">
              {{ $t('jobplan.task_scope') }}
            </div>
            <div class="jp-scope-desc mT5">
              {{ $t('jobplan.section_task_desc') }}
            </div>
            <div class="d-flex mT15">
              <div>
                <div class="scope-txt14  fw4">
                  {{ $t('jobplan.category') }}
                </div>
                <el-select
                  class="fc-input-full-border2 mT10  width250px"
                  v-model="task.jobPlanTaskCategory"
                  @change="resetResources()"
                  filterable
                  clearable
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
                <div class="scope-text14  fw4">
                  {{ lookupDisplayName }}
                </div>
                <FLookupField
                  :model.sync="task.resource"
                  ref="task-resource-field"
                  class="width250px mT10"
                  :field="selectedLookupField"
                  @recordSelected="setSelectedLookupValue"
                  @showLookupWizard="showLookupWizard"
                />
              </div>
            </div>
          </div>
        </div>
        <div class="scope-seperator" v-if="canShowScope"></div>
        <div class="mT25 pL20 pR10">
          <div class="scope-heading mT20">
            {{ $t('jobplan.input_config') }}
          </div>
          <el-checkbox
            v-model="task.enableInput"
            class="scope-border mT15 mL5 scope-txt14"
            @change="resetInputs"
            >{{ $t('maintenance._workorder.enable_input') }}</el-checkbox
          >
        </div>
        <div v-if="task.enableInput" class="pL35 pR10">
          <div class="mT25">
            <div class="scope-text14 ">
              {{ $t('maintenance._workorder.task_type') }}
            </div>
            <el-radio
              @change="resetInputs(false)"
              v-model="task.inputType"
              class="fc-radio-btn pT15 scope-border"
              color="secondary"
              v-for="(label, key) in inputOptions"
              :key="key"
              :label="Number(key)"
              >{{ label }}</el-radio
            >
          </div>
          <div v-if="isPreferredInputType('Reading')" class="mT25">
            <div class="scope-text14 ">
              {{ $t('maintenance._workorder.reading_field') }}
            </div>
            <div>
              <el-select
                v-model="task.readingFieldId"
                clearable
                filterable
                style="width:100%"
                class="fc-input-full-border-select2 mT10 width250px"
                :placeholder="$t('jobplan.select_reading_field')"
                :loading="isLoading"
              >
                <el-option
                  v-for="option in task.readings"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                ></el-option>
              </el-select>
            </div>
          </div>
          <div v-if="isPreferredInputType('Option')" class="mT25">
            <div class="mT10 scope-text14 ">
              {{ $t('maintenance._workorder.options') }}
            </div>
            <div
              v-for="(option, index) in task.inputOptions"
              :key="index"
              class="taskoption mT10 d-flex"
            >
              <el-row class="visibility-visible-actions width100">
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
                  <div @click="addTaskOption(task)" class="mT12">
                    <inline-svg src="add-icon" iconClass="icon icon-xxl" />
                  </div>
                </el-col>
                <el-col
                  v-if="isPreferredInputType('Option')"
                  :span="2"
                  class="visibility-hide-actions pointer"
                >
                  <div
                    @click="removeTaskOption(task.inputOptions, index)"
                    v-show="task.inputOptions.length > 2"
                    class="mT12 mL10"
                  >
                    <inline-svg iconClass="icon icon-xxl" src="remove-icon" />
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
          <div
            v-if="!isPreferredInputType('Reading') && task.inputType"
            class="d-flex"
          >
            <div>
              <div class="scope-text14  mT20 mb5">
                {{ $t('common._common.default_value') }}
              </div>
              <el-select
                v-if="
                  ['Option', 'Boolean'].includes(inputTypeHash[task.inputType])
                "
                v-model="task.defaultValue"
                clearable
                filterable
                :placeholder="$t('jobplan.select_default')"
                class="fc-input-full-border-select2 width250px mT10"
              >
                <el-option
                  v-for="(option, index) in task.inputOptions"
                  :label="option.value"
                  :key="index"
                  :value="index"
                ></el-option>
              </el-select>
              <el-input
                v-else
                :type="isPreferredInputType('Number') ? 'number' : 'text'"
                v-model="task.defaultValue"
                :placeholder="$t('jobplan.enter_default')"
                class="fc-input-full-border2 width250px task-option-input mT10"
              ></el-input>
            </div>
            <div v-if="isPreferredInputType('Option')" class="mL15">
              <div class="scope-text14  mT20 mb5">
                {{ $t('common.wo_report.deviation_value') }}
              </div>
              <el-select
                v-model="task.failureValue"
                filterable
                clearable
                @change="resetWoTemplate"
                :placeholder="$t('common.placeholders.select_deviation_value')"
                class="fc-input-full-border-select2 width250px mT10"
              >
                <el-option
                  v-for="(option, index) in task.inputOptions"
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
              <div class="scope-text14  f15 mT20 mb5">
                {{ $t('common.wo_report.deviation_condition') }}
              </div>
              <el-row :gutter="20">
                <el-col :span="11">
                  <div class="scope-text14  mT10 mb5">
                    {{ $t('common.wo_report.Operator') }}
                  </div>
                  <el-select
                    v-model="task.deviationOperatorId"
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
                  <div class="scope-text14  mT10 mb5">
                    {{ $t('asset.readings.value') }}
                  </div>
                  <el-input
                    type="number"
                    v-model="task.failureValue"
                    :disabled="$validation.isEmpty(task.deviationOperatorId)"
                    @input="resetWoTemplate(...arguments, 'number')"
                    :placeholder="$t('common._common.please_enter_value')"
                    class="fc-input-full-border2 width250px task-option-input mT10"
                  ></el-input>
                </el-col>
              </el-row>
            </template>
            <div class="mT10" v-if="canShowConfig">
              <el-checkbox
                v-model="task.createWoOnFailure"
                class="width-full pT20 pB10 scope-border"
                @change="resetTemplate"
                >{{
                  $t('common.wo_report.create_workorder_on_deviation')
                }}</el-checkbox
              >
              <!-- <el-select
                v-if="task.createWoOnFailure"
                v-model="task.woCreateFormId"
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
          <!-- <div v-if="isPreferredInputType('Number')" class="mT25">
            <div class="scope-text14 ">
              {{ $t('jobplan.data_validation') }}
            </div>
            <el-select
              v-model="task.validation"
              clearable
              filterable
              class="fc-input-full-border-select2 width250px mT10"
              :placeholder="$t('jobplan.select_validation')"
            >
              <el-option
                v-for="{ label, value } in validationOptions"
                :key="value"
                :label="label"
                :value="value"
              ></el-option>
            </el-select>
            <div v-if="task.validation === 'safeLimit'" class="mT20 d-flex">
              <div>
                <div class="scope-text14">
                  {{ $t('maintenance.pm_list.minimum_value') }}
                </div>
                <el-input
                  type="number"
                  v-model="task.minSafeLimit"
                  :placeholder="$t('jobplan.enter_min_value')"
                  class="fc-input-full-border2 mT10 width250px"
                ></el-input>
              </div>
              <div class="mL15">
                <div class="scope-text14">
                  {{ $t('maintenance.pm_list.max_value') }}
                </div>
                <el-input
                  type="number"
                  v-model="task.maxSafeLimit"
                  :placeholder="$t('jobplan.enter_max_value')"
                  class="fc-input-full-border2 mT10 width250px"
                ></el-input>
              </div>
            </div>
          </div> -->
        </div>
        <div class="scope-seperator"></div>
        <div class="pL20 pR10">
          <div class="scope-heading mT20 mb5">
            {{ $t('jobplan.validation') }}
          </div>
          <div class="mT15">
            <el-checkbox
              @change="resetOptionValues('attachment')"
              v-model="task.attachmentRequired"
              class="scope-border mL5"
              >{{ $t('maintenance._workorder.photo') }}</el-checkbox
            >
            <div v-if="isAttachmentOptionRequired" class="mB15 pL35 pR10">
              <el-row class="mT20">
                <el-col :span="12">
                  <div class="scope-text14  mb5">
                    {{ $t('jobplan.attachments_options') }}
                  </div>
                  <el-radio-group
                    @change="resetAttachmentValues"
                    v-model="task.attachmentOption"
                    class="mT10 mB10"
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
                    v-if="task.attachmentOption === 'specific'"
                    v-model="task.attachmentOptionValues"
                    multiple
                    collapse-tags
                    clearable
                    filterable
                    :placeholder="$t('common.placeholders.select_values')"
                    class="fc-input-full-border-select2 fc-tag el-select-block width250px mT10"
                  >
                    <el-option
                      v-for="(option, index) in task.inputOptions"
                      :label="option.value"
                      :key="index"
                      :value="index"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
            </div>
          </div>
          <div class="mT20">
            <el-checkbox
              @change="resetOptionValues('remark')"
              v-if="isPreferredInputType('Option')"
              class="scope-border mL5"
              v-model="task.remarksRequired"
              >{{ $t('jobplan.remarks_mandatory') }}</el-checkbox
            >
            <div v-if="isRemarksRequired" class="pL35 pR10">
              <el-row class="mT20">
                <el-col :span="12">
                  <div class="scope-text14  mb5">
                    {{ $t('jobplan.remark_options') }}
                  </div>
                  <el-radio-group
                    @change="resetRemarksValues"
                    v-model="task.remarkOption"
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
                    v-if="task.remarkOption === 'specific'"
                    v-model="task.remarkOptionValues"
                    multiple
                    collapse-tags
                    :placeholder="$t('common.placeholders.select_values')"
                    clearable
                    filterable
                    class="fc-input-full-border-select2 fc-tag el-select-block width250px mT10"
                  >
                    <el-option
                      v-for="(option, index) in task.inputOptions"
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
            @click="saveTaskInfo()"
            >{{ $t('jobplan.save') }}</el-button
          >
          <el-button
            plain
            @click="closeTaskDialog"
            class="jp-task-cancel mL15"
            >{{ $t('agent.agent.cancel') }}</el-button
          >
        </div>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import cloneDeep from 'lodash/cloneDeep'
import TaskInfo from '@/TaskDetails'
import { isEmpty, isNumber } from '@facilio/utils/validation'
import {
  JOB_PLAN_TASK_SCOPE,
  JOB_PLAN_SECTION_SCOPE,
  JOB_PLAN_SCOPE,
  SCOPE_LOOKUP_FIELDS,
} from './utils/scope-util.js'
import FLookupField from 'src/components/forms/FLookupField.vue'
import FLookupFieldWizard from 'src/components/FLookupFieldWizard.vue'
import { API } from '@facilio/api'

const taskSettingConfig = {
  description: '',
  inputOptions: [{ value: 'Choice 1' }, { value: 'Choice 2' }],
  inputType: null,
  enableInput: false,
  defaultValue: '',
  failureValue: '',
  deviationOperatorId: null,
  createWoOnFailure: null,
  // woCreateFormId: null,
  remarkOption: 'all',
  remarkOptionValues: [],
  remarksRequired: false,
  attachmentOptionValues: [],
  attachmentOption: 'all',
  statusNew: 1,
  taskId: 1,
  jobPlanTaskCategory: null,
  resource: null,
  readings: [],
  readingFieldId: null,
}

const taskPrerequisiteConfig = {
  enableOption: true,
  options: [{ value: 'Yes' }, { value: 'No' }],
  attachmentRequired: false,
}

export default {
  extends: TaskInfo,
  props: [
    'woForms',
    'selectedTaskInfo',
    'canShowTaskDetails',
    'formModel',
    'isPreRequisite',
  ],
  components: { FLookupField, FLookupFieldWizard },
  data() {
    return {
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
      isLoading: false,
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
      canShowLookupWizard: false,
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
        : this.$t('jobplan.task_details')
    },
    scopeOptions() {
      let { selectedTaskInfo, formModel } = this
      let { task } = selectedTaskInfo || {}
      let { jobPlanSectionCategory } = task || {}
      let { jobPlanCategory } = formModel || {}
      let { id: jobPlanScope } = jobPlanCategory || {}
      let currentSectionScopeOptions =
        JOB_PLAN_SECTION_SCOPE[jobPlanScope] || {}
      let taskScopeKey = Object.keys(currentSectionScopeOptions).find(
        key => currentSectionScopeOptions[key] === jobPlanSectionCategory
      )
      let taskScopeOptions = JOB_PLAN_TASK_SCOPE[taskScopeKey] || {}

      return taskScopeOptions
    },
    canShowScope() {
      let { formModel, task } = this
      let { jobPlanSectionCategory } = task || {}
      let { jobPlanCategory } = formModel || {}
      let { id: jobPlanScope } = jobPlanCategory || {}

      return !isEmpty(jobPlanScope) && !isEmpty(jobPlanSectionCategory)
    },
    lookupDisplayName() {
      let { selectedLookupField } = this
      let { field } = selectedLookupField || {}
      let { lookupModule } = field || {}

      return this.$getProperty(lookupModule, 'displayName', '')
    },
    selectedLookupField() {
      let { task } = this
      let { jobPlanTaskCategory } = task || {}

      return SCOPE_LOOKUP_FIELDS[jobPlanTaskCategory] || {}
    },
    isAttachmentOptionRequired() {
      let { task, inputTypeHash } = this
      let { attachmentRequired, inputType } = task || {}

      return (
        attachmentRequired &&
        ['Option', 'Boolean'].includes(inputTypeHash[inputType])
      )
    },
    isRemarksRequired() {
      let { task, inputTypeHash } = this
      let { remarksRequired, inputType } = task || {}

      return (
        remarksRequired &&
        ['Option', 'Boolean'].includes(inputTypeHash[inputType])
      )
    },
    canShowConfig() {
      let { task, inputTypeHash } = this
      let { enableInput, inputType, failureValue } = task || {}
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
  },
  mounted() {
    let { selectedTaskInfo, isPreRequisite } = this
    let { task } = selectedTaskInfo || {}
    let taskObj = isPreRequisite
      ? cloneDeep(taskPrerequisiteConfig)
      : cloneDeep(taskSettingConfig)
    let taskData = { ...taskObj, ...task }

    this.$set(this, 'task', taskData)
    if (!isPreRequisite) {
      this.fillDefaultScope()
      this.initReadings()
    }
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
    task: {
      async handler(newVal) {
        this.validateTask(newVal)
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    isCategoryScope(scope) {
      return ['ASSETCATEGORY', 'SPACECATEGORY'].includes(JOB_PLAN_SCOPE[scope])
    },
    initReadings() {
      let { formModel, task } = this
      let { jobPlanCategory } = formModel || {}
      let { jobPlanSectionCategory, jobPlanTaskCategory } = task || {}
      let { id: mainScope = null } = jobPlanCategory || {}

      let scope
      if (!isEmpty(jobPlanTaskCategory)) {
        scope = jobPlanTaskCategory
        if (!this.isCategoryScope(scope)) {
          scope = jobPlanSectionCategory
          if (!this.isCategoryScope(scope)) {
            scope = mainScope
          }
        }
      }

      if (this.isCategoryScope(scope)) {
        let { inputOptions } = this
        this.inputOptions = { 2: 'Reading', ...inputOptions }
        this.loadReadings()
      } else {
        let { inputType } = this
        if (inputType === 2) this.$set(this.task, 'inputType', null)
      }
    },
    fillDefaultScope() {
      let { task, canShowScope } = this
      let { jobPlanTaskCategory, jobPlanSectionCategory } = task || {}
      if (
        !isEmpty(jobPlanSectionCategory) &&
        isEmpty(jobPlanTaskCategory) &&
        canShowScope
      ) {
        this.$set(this.task, 'jobPlanTaskCategory', 5)
      }
    },
    resetTemplate(reset) {
      if (reset) {
        let { task } = this
        this.task = {
          ...task,
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
        let { task } = this
        this.task = {
          ...task,
          createWoOnFailure: false,
          // woCreateFormId: null,
        }
      }
    },
    async loadReadings() {
      this.isLoading = true
      let { task } = this
      let {
        resource: resourceId,
        sectionResource: sectionResourceId,
        jobPlanResource,
        jobPlanCategory,
        jobPlanSectionCategory,
        jobPlanTaskCategory,
      } = task || {}
      let { id: jobPlanResourceId } = jobPlanResource || {}
      let options = []
      let taskResourceId
      let isAssetCategory = true

      //Will Change logic while general release ***
      if (!isEmpty(resourceId)) {
        taskResourceId = resourceId
        isAssetCategory = jobPlanTaskCategory === 4
      } else if (!isEmpty(sectionResourceId)) {
        taskResourceId = sectionResourceId
        isAssetCategory = jobPlanSectionCategory === 4
      } else {
        taskResourceId = jobPlanResourceId
        isAssetCategory = jobPlanCategory === 3
      }

      if (!isEmpty(taskResourceId)) {
        let url
        if (isAssetCategory) {
          url = `v2/readings/assetcategory?id=${taskResourceId}&excludeEmptyFields=false&readingType=true`
        } else {
          url = `reading/getallspacereadings?categoryIds=${taskResourceId}`
        }
        let { data, error } = await API.get(url)
        if (error) {
          let { message } = error || {}
          this.$message.error(message || {})
        } else {
          if (isAssetCategory) {
            let { readings = [] } = data || {}
            if (!isEmpty(readings)) {
              readings.forEach(reading => {
                let { displayName: label, id: value } = reading || {}
                if (!isEmpty(label) && !isEmpty(value)) {
                  options.push({
                    label,
                    value,
                  })
                }
              })
            }
          } else {
            let { [`${taskResourceId}`]: readings } = data || {}
            if (!isEmpty(readings)) {
              readings.forEach(reading => {
                let { fields } = reading || {}
                ;(fields || []).forEach(field => {
                  let { displayName: label, id: value } = field || {}
                  if (!isEmpty(label) && !isEmpty(value)) {
                    options.push({
                      label,
                      value,
                    })
                  }
                })
              })
            }
          }
        }
      }
      this.$set(this.task, 'readings', options)
      this.isLoading = false
    },
    isPreferredInputType(input) {
      let { task, inputTypeHash } = this
      let { enableInput } = task || {}
      let inputType = -1
      if (enableInput) {
        inputType = this.$getProperty(task, 'inputType', '-1')
      }

      return !isEmpty(inputType) ? inputTypeHash[inputType] === input : false
    },
    validateTask(task) {
      let { inputTypeHash } = this
      let {
        inputType,
        readingFieldId,
        inputOptions,
        jobPlanTaskCategory,
        attachmentOption,
        attachmentOptionValues,
        remarkOption,
        remarkOptionValues,
        // createWoOnFailure,
        // woCreateFormId,
        deviationOperatorId,
        failureValue,
        // validation,
        // minSafeLimit,
        // maxSafeLimit,
        resource,
        defaultValue,
      } = task || {}

      let categoryHash = { 3: 'Space Category', 4: 'Asset Category' }
      if (
        ['Asset Category', 'Space Category'].includes(
          categoryHash[jobPlanTaskCategory]
        ) &&
        isEmpty(resource)
      ) {
        this.error = true
        if (categoryHash[jobPlanTaskCategory] === 'Asset Category') {
          this.errorText = this.$t('jobplan.select_asset_category')
        } else {
          this.errorText = this.$t('jobplan.select_space_category')
        }
        return !isEmpty(resource)
      }

      if (inputTypeHash[inputType] === 'Reading' && isEmpty(readingFieldId)) {
        this.error = true
        this.errorText = this.$t('jobplan.reading_needed')
        return false
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
    serialize(task) {
      let { subject } = task || {}
      Object.keys(task).forEach(property => {
        if (isEmpty(task[property])) {
          delete task[property]
        }
      })

      return { ...task, subject }
    },
    setSelectedLookupValue() {
      let { selectedLookupField, task } = this
      let { lookupModuleName } = selectedLookupField || {}
      let { resource = null } = task || {}

      if (lookupModuleName === 'assetcategory') {
        this.$set(this.task, 'assetCategory', {
          id: resource,
        })
      } else {
        this.$set(this.task, 'spaceCategory', {
          id: resource,
        })
      }

      if (!isEmpty(resource)) {
        this.initReadings()
      }
    },
    showLookupWizard(_, canShow) {
      this.canShowLookupWizard = canShow
    },
    setWizardValue(props) {
      let { field } = props
      let { selectedItems = [] } = field
      let selectedItemIds = []

      if (!isEmpty(selectedItems)) {
        selectedItemIds = selectedItems.find(item => item.value).value || {}
      }
      this.$set(this.task, 'resource', selectedItemIds)
      this.setSelectedLookupValue()
      this.initReadings()
    },
    resetResources() {
      let { task } = this
      this.task = {
        ...task,
        resource: null,
        assetCategory: null,
        spaceCategory: null,
      }
    },
    addTaskOption(selectedTask) {
      let { inputOptions } = selectedTask || {}
      let optionValue = `Choice${inputOptions.length + 1}`
      selectedTask.inputOptions.push({ value: optionValue })
    },
    removeTaskOption(list, index) {
      //Removing the deleted option if its selected in configuration
      let { task } = this
      let {
        remarkOptionValues,
        attachmentOptionValues,
        defaultValue,
        failureValue,
      } = task || {}

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

      this.task = {
        ...task,
        remarkOptionValues,
        attachmentOptionValues,
        defaultValue,
        failureValue,
      }

      list.splice(index, 1)
    },
    resetInputs(val) {
      if (!val) {
        let { task, inputTypeHash } = this
        let { inputType } = task || {}
        let readingContext = {}
        if (inputTypeHash[inputType] !== 'Reading') {
          readingContext.readings = []
          readingContext.readingFieldId = null
        }
        this.task = {
          ...task,
          ...readingContext,
          remarksRequired: false,
          inputOptions: [{ value: 'Choice1' }, { value: 'Choice 2' }],
          remarkOptionValues: null,
          remarkOption: 'all',
          defaultValue: null,
          attachmentRequired: false,
          attachmentOptionValues: null,
          attachmentOption: 'all',
          failureValue: null,
          deviationOperatorId: null,
          createWoOnFailure: false,
          // woCreateFormId: null,
        }
      }
    },
    resetAttachmentValues(val) {
      if (val === 'all') {
        this.task.attachmentOptionValues = null
      }
    },
    resetRemarksValues(val) {
      if (val === 'all') {
        this.task.remarkOptionValues = null
      }
    },
    resetOptionValues(type) {
      if (type === 'remark') {
        this.task.remarkOptionValues = null
        this.task.remarkOption = null
      } else {
        this.task.attachmentOptionValues = null
        this.task.attachmentOption = null
      }
    },
    saveTaskInfo() {
      let updatedTask = {}
      let { task, selectedTaskInfo } = this
      let isValid = this.validateTask(task)

      if (isValid) {
        task = this.serialize(task)
        updatedTask = { ...selectedTaskInfo, task }

        this.$emit('taskUpdated', updatedTask)
      }
    },
    closeTaskDialog() {
      this.$emit('update:canShowTaskDetails', false)
    },
  },
}
</script>
<style scoped lang="scss">
.task-details {
  height: 450px;
  overflow: scroll;

  .setting-desc {
    font-weight: 300;
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
  .jp-scope-desc {
    font-size: 14px;
    font-weight: normal;
    letter-spacing: 0.3px;
    color: #324056;
    opacity: 0.6;
  }
  .scope-text14 {
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
}
</style>
