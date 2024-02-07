<template>
  <div
    :class="[
      !removeDefaultStyling && 'f-subform-container',
      !$validation.isEmpty(customClass) ? customClass : '',
      !isLiveSubForm ? 'f-subform-builder' : 'f-subform-live scrollbar-style',
    ]"
  >
    <div
      v-if="canShowRemove"
      class="remove-btn-container"
      :class="formIndex === 0 ? 'first' : ''"
    >
      <img
        src="~assets/remove-icon.svg"
        class="mL5 remove-icon"
        @click="removeLineItem()"
      />
    </div>
    <el-form
      ref="form"
      :model="formModel"
      :rules="rules"
      :label-position="`top`"
      label-width="100px"
      class="position-relative"
    >
      <div
        v-if="isSectionsEmpty && !isLiveSubForm"
        class="empty btn-container"
        @click="
          event => {
            showHideFields(true, event)
          }
        "
      >
        <div class="add-field-btn">
          <inline-svg
            src="add"
            class="vertical-middle"
            iconClass="icon icon-sm-md"
          ></inline-svg>
        </div>
      </div>
      <div
        v-else
        v-for="(section, sectionIndex) in form.sections"
        :key="sectionIndex"
        class="section-container pR300"
        :class="[
          sectionIndex === 0 ? '' : 'mT20',
          isFirstofType ? 'first-subform' : '',
          isLastofType ? 'last-subform ' : '',
        ]"
      >
        <draggable
          v-model="form.sections[sectionIndex].fields"
          v-bind="formFieldsDragOptions"
          :disabled="canDisableDrag()"
          class="dragArea fields-container"
          @start="onStart"
          @end="onEnd"
          @change="onSubFormFieldChange(...arguments)"
        >
          <div
            v-for="(field, fieldIndex) in section.fields"
            :key="`${sectionIndex} - ${fieldIndex}`"
            :class="[
              !isLiveSubForm ? 'form-drag-item' : '',
              isLiveSubForm && field.hideField ? 'hide' : '',
            ]"
            class="subform-field"
            @click.stop="setActiveField(field)"
          >
            <el-form-item
              :label="field.displayName"
              :prop="field.name"
              :required="isLiveSubForm ? field.required : false"
              label-width="150px"
              class="section-items"
              :class="[isFieldActive(field) && !isLiveSubForm ? 'active' : '']"
            >
              <template #label v-if="!isLiveSubForm">
                <div>
                  <label
                    ><span class="forms-field-text-label">{{
                      field.displayName
                    }}</span
                    ><span class="forms-red-dot" v-if="field.required"
                      >*</span
                    ></label
                  >
                  <inline-svg
                    v-if="field.hideField"
                    src="svgs/hidden-field"
                    iconClass="mL5 icon icon-sm vertical-text-bottom"
                  ></inline-svg>
                </div>
              </template>
              <div
                v-if="!isLiveSubForm"
                class="subform-field-delete"
                @click="
                  event => {
                    removeSubFormField(field, fieldIndex, event)
                  }
                "
              >
                <img src="~assets/remove-icon.svg" class="delete-icon" />
              </div>
              <el-input
                v-if="
                  $constants.FORM_TEXTAREA_DISPLAY_TYPES.includes(
                    field.displayTypeEnum
                  )
                "
                type="textarea"
                resize="none"
                v-model="formModel[field.name]"
                :placeholder="`Type your  ${field.displayName}`"
                class="fc-input-full-border-textarea"
                :key="`${sectionIndex}${fieldIndex}`"
                :disabled="canDisableField(field)"
                @change="value => onChangeHandler({ field, isSubForm: true })"
              ></el-input>
              <el-checkbox
                v-model="formModel[field.name]"
                v-else-if="field.displayTypeEnum === 'DECISION_BOX'"
                :disabled="canDisableField(field)"
                @change="value => onChangeHandler({ field, isSubForm: true })"
              ></el-checkbox>
              <el-radio-group
                v-model="formModel[field.name]"
                v-else-if="field.displayTypeEnum === 'RADIO'"
                :disabled="canDisableField(field)"
                @change="value => onChangeHandler({ field, isSubForm: true })"
              >
                <el-radio :label="true" class="fc-radio-btn">{{
                  field.field.trueVal
                }}</el-radio>
                <el-radio :label="false" class="fc-radio-btn">{{
                  field.field.falseVal
                }}</el-radio>
              </el-radio-group>
              <div v-else-if="field.displayTypeEnum === 'MULTI_CURRENCY'">
                <FNewCurrencyField
                  :key="formModel.currencyCode"
                  v-model="formModel[field.name]"
                  :field="field"
                  :isEdit="isEdit"
                  :disabled="!isLiveSubForm"
                  :isSubform="true"
                  :moduleData="formModel"
                  @setCurrencyCodeInSubform="setCurrencyCodeInSubform"
                  @calculateExchangeRate="
                    rateObj => calculateExchangeRate(rateObj, section.fields)
                  "
                ></FNewCurrencyField>
              </div>
              <FSiteField
                v-else-if="
                  isSiteField(field) || $fieldUtils.isSiteLookup(field)
                "
                :data-test-selector="`${field.name}`"
                :key="`${fieldIndex} ${field.id}`"
                :model.sync="(formModel[field.name] || {}).id"
                :canDisable="field.isDisabled"
                :resetFields="true"
                :isEdit="isEdit"
                :filter.sync="filter"
                :field="field"
                @handleSiteSwitch="showConfirmSiteSwitch"
                @handleChange="() => onChangeHandler({ field })"
              ></FSiteField>
              <FLookupField
                v-else-if="field.displayTypeEnum === 'MULTI_LOOKUP_SIMPLE'"
                :data-test-selector="`${field.name}`"
                :key="`${fieldIndex} ${field.id}`"
                :model.sync="formModel[field.name]"
                :field="field"
                :disabled="field.isDisabled"
                :hideDropDown="
                  $fieldUtils.isChooserTypeField(field) &&
                    !(field.config || {}).isFiltersEnabled
                "
                :siteId="selectedSiteId"
                :rulesCriteria="field.rulesCriteria"
                @showLookupWizard="showLookupWizard"
                @recordSelected="
                  (value, field) =>
                    onRecordSelected({ value, field, isSubForm: true })
                "
              ></FLookupField>
              <FLookupFieldWithFloorplan
                v-else-if="
                  isLookupSimpleField(field) &&
                    (field.config || {}).canShowFloorPlanPicker
                "
                :data-test-selector="`${field.name}`"
                :key="`${fieldIndex} ${field.id}`"
                :model.sync="(formModel[field.name] || {}).id"
                :field="field"
                :disabled="field.isDisabled"
                :siteId="selectedSiteId"
                @showLookupWizard="showLookupWizard"
                @recordSelected="
                  (value, field) =>
                    onRecordSelected({ value, field, isSubForm: true })
                "
              ></FLookupFieldWithFloorplan>
              <FLookupField
                v-else-if="checkFieldType(field)"
                :model.sync="(formModel[field.name] || {}).id"
                :field="field"
                :disabled="canDisableField(field)"
                :hideDropDown="
                  $fieldUtils.isChooserTypeField(field) &&
                    !(field.config || {}).isFiltersEnabled
                "
                :siteId="selectedSiteId"
                :fetchOptionsOnLoad="true"
                :skipLoading="true"
                @showLookupWizard="showLookupWizard"
                @recordSelected="
                  (value, field) =>
                    onRecordSelected({ value, field, isSubForm: true })
                "
              ></FLookupField>
              <div v-else-if="field.displayTypeEnum === 'GEO_LOCATION'">
                <FLocationField
                  :key="`${fieldIndex} ${field.id}`"
                  v-model="formModel[field.name]"
                  :field="field"
                  :disabled="field.isDisabled"
                  :clearable="false"
                  @change="value => onChangeHandler({ value, field })"
                ></FLocationField>
              </div>
              <el-select
                v-else-if="
                  isDropdownTypeFields(field.displayTypeEnum) &&
                    isMultiple(field)
                "
                v-model="formModel[field.name]"
                :multiple="true"
                filterable
                :disabled="canDisableField(field)"
                placeholder="Select"
                class="fc-input-full-border-select2 width100"
                @change="value => onChangeHandler({ field, isSubForm: true })"
              >
                <div slot="empty">
                  <div class="el-select-dropdown__empty">
                    {{ $t('common._common.empty_option_text') }}
                  </div>
                  <el-button
                    v-if="field.allowCreate"
                    class="add-option-empty"
                    :loading="field.isOptionsLoading"
                    @click="addOption(field, sectionIndex, fieldIndex)"
                    >{{ $t('maintenance._workorder.add_option') }}</el-button
                  >
                </div>
                <el-option
                  v-for="(option, index) in field.options"
                  :key="index"
                  :label="option.label"
                  :value="option.value"
                ></el-option>
                <div v-if="field.allowCreate" class="add-option-container">
                  <el-button
                    class="add-option-content"
                    @click="addOption(field, sectionIndex, fieldIndex)"
                    >{{ $t('maintenance._workorder.add_option') }}</el-button
                  >
                </div>
              </el-select>
              <el-select
                v-else-if="isDropdownTypeFields(field.displayTypeEnum)"
                v-model="(formModel[field.name] || {}).id"
                filterable
                :disabled="canDisableField(field)"
                placeholder="Select"
                class="fc-input-full-border-select2 width100"
                @change="value => onChangeHandler({ field, isSubForm: true })"
              >
                <div slot="empty">
                  <div class="el-select-dropdown__empty">
                    {{ $t('common._common.empty_option_text') }}
                  </div>
                  <el-button
                    v-if="field.allowCreate"
                    class="add-option-empty"
                    :loading="field.isOptionsLoading"
                    @click="addOption(field, sectionIndex, fieldIndex)"
                    >{{ $t('maintenance._workorder.add_option') }}</el-button
                  >
                </div>
                <el-option
                  v-for="(option, index) in field.options"
                  :key="index"
                  :label="option.label"
                  :value="option.value"
                ></el-option>
                <div v-if="field.allowCreate" class="add-option-container">
                  <el-button
                    class="add-option-content"
                    @click="addOption(field, sectionIndex, fieldIndex)"
                    >{{ $t('maintenance._workorder.add_option') }}</el-button
                  >
                </div>
              </el-select>
              <div
                v-else-if="field.displayTypeEnum === 'TEAMSTAFFASSIGNMENT'"
                class="fc-input-full-border-select2"
                :class="[field.isDisabled ? 'disabled-background' : '']"
                :disabled="canDisableField(field)"
              >
                <div
                  class="fc-border-input-div"
                  @click="event => openFassignment(event, field)"
                >
                  <span>{{ getTeamStaffLabel(formModel[field.name]) }}</span>
                  <span style="float: right;">
                    <img
                      class="svg-icon team-down-icon"
                      src="~assets/down-arrow.svg"
                    />
                  </span>
                </div>
                <FAssignment
                  :model="formModel[field.name]"
                  :siteId="selectedSiteId"
                  viewtype="form"
                  @onChange="() => onChangeHandler({ field, isSubForm: true })"
                ></FAssignment>
              </div>
              <div v-else-if="field.displayTypeEnum === 'DURATION'">
                <f-duration-field
                  :key="field.fieldId"
                  class="duration-container"
                  :field="field"
                  :isDisabled="canDisableField(field)"
                  @updateDurationValue="updateDurationValue"
                ></f-duration-field>
              </div>
              <FTimePicker
                v-else-if="field.displayTypeEnum === 'TIME'"
                v-model="formModel[field.name]"
                :disabled="field.isDisabled"
                :placeholder="$t('fields.properties.time_picker_placeholder')"
                class="el-select fc-input-full-border-select2 width100"
                @onChange="() => onChangeHandler({ field, isSubForm: true })"
              ></FTimePicker>
              <f-date-picker
                v-else-if="field.displayTypeEnum === 'DATE'"
                :key="`${fieldIndex} ${field.id}`"
                v-model="formModel[field.name]"
                :type="'date'"
                :disabled="canDisableField(field)"
                class="fc-input-full-border2 form-date-picker"
                @change="value => onChangeHandler({ field, isSubForm: true })"
              ></f-date-picker>
              <f-date-picker
                v-else-if="field.displayTypeEnum === 'DATETIME'"
                :key="`${fieldIndex} ${field.id}`"
                v-model="formModel[field.name]"
                :type="'datetime'"
                :disabled="canDisableField(field)"
                class="fc-input-full-border2 form-date-picker"
                @change="value => onChangeHandler({ field, isSubForm: true })"
              ></f-date-picker>
              <f-date-picker
                v-else-if="field.displayTypeEnum === 'DATERANGE'"
                :key="`${fieldIndex} ${field.id}`"
                v-model="formModel[field.name]"
                :type="'daterange'"
                :disabled="canDisableField(field)"
                class="fc-input-full-border2 form-date-picker"
                @change="value => onChangeHandler({ field, isSubForm: true })"
              ></f-date-picker>
              <div v-else-if="field.displayTypeEnum === 'FILE'">
                <FFileUpload
                  :key="getRandomNumber()"
                  :field="field"
                  :isFileType="field.displayTypeEnum === 'FILE'"
                  :isDisabled="!isLiveSubForm"
                  :imgContentUrl.sync="field.imgUrl"
                  :fileObj.sync="field.fileObj"
                  :showWebCamPhoto="false"
                  :isSaveBtnDisabled.sync="isSaveBtnDisabled"
                  :isV3Api="isV3Api"
                  :addImgFile="
                    (fileOrId, field) =>
                      addImgFile({ fileOrId, field, isSubForm: true })
                  "
                  @removeImgFile="
                    field => removeImgFile({ field, isSubForm: true })
                  "
                ></FFileUpload>
              </div>
              <div v-else-if="field.displayTypeEnum === 'SIGNATURE'">
                <FSignaturePad
                  :model.sync="formModel[field.name]"
                  :fileName="field.name"
                  :isV3Api="isV3Api"
                  :imgUrl.sync="field.imgUrl"
                  :isSaveBtnDisabled.sync="isSaveBtnDisabled"
                  :canShowColorPalette="
                    (field.config || {}).canShowColorPalette
                  "
                  @onChange="() => onChangeHandler({ field, isSubForm: true })"
                ></FSignaturePad>
              </div>
              <el-input
                v-else-if="
                  field.displayTypeEnum === 'NUMBER' ||
                    field.displayTypeEnum === 'DECIMAL'
                "
                type="number"
                v-model="formModel[field.name]"
                :class="[
                  'fc-input-full-border2',
                  canShowMetric(field) &&
                    (metricSymbolPosition(field) === 'prepend'
                      ? 'fc-slot-input-prepend'
                      : 'fc-slot-input-append'),
                ]"
                :disabled="canDisableField(field)"
                @change="value => onChangeHandler({ field, isSubForm: true })"
              >
                <template
                  v-if="canShowMetric(field)"
                  :slot="metricSymbolPosition(field)"
                >
                  {{ (field.field || {}).unit }}
                </template>
              </el-input>
              <el-input
                v-else-if="field.displayTypeEnum === 'PERCENTAGE'"
                v-model="formModel[field.name]"
                class="fc-input-full-border2 fc-input-icon"
                :disabled="canDisableField(field)"
                @change="value => onChangeHandler({ field, isSubForm: true })"
              >
                <template :slot="field.iconPosition">
                  <inline-svg
                    :src="field.icon"
                    class="vertical-middle"
                    iconClass="icon icon-xs"
                  ></inline-svg>
                </template>
              </el-input>
              <el-input
                v-else
                class="fc-input-full-border2"
                v-model="formModel[field.name]"
                :disabled="canDisableField(field)"
                @change="value => onChangeHandler({ field, isSubForm: true })"
              ></el-input>
            </el-form-item>
          </div>
        </draggable>
        <div
          v-if="!isLiveSubForm"
          class="add-field-btn"
          @click="
            event => {
              showHideFields(true, event)
            }
          "
        >
          <div>
            <inline-svg
              src="add"
              class="vertical-middle"
              iconClass="icon icon-sm-md"
            ></inline-svg>
          </div>
        </div>
      </div>
      <el-popover
        v-if="!isLiveSubForm"
        placement="left"
        width="200"
        trigger="manual"
        :value="canShowFields"
        popper-class="fields-dropdown"
        v-click-outside="showHideFields"
      >
        <div v-for="(field, index) in subFormFields" :key="index">
          <div
            class="letter-spacing0_4 fc-black-color f14 mB20 pointer"
            @click="
              event => {
                addSubFormField(field, index, event)
              }
            "
          >
            <span class="mR5 vertical-middle">
              <inline-svg
                :src="getIconSrc(field.displayTypeEnum)"
                class="vertical-middle"
                iconClass="icon icon-sm-md"
              ></inline-svg>
            </span>
            {{ field.displayName }}
          </div>
        </div>
      </el-popover>
    </el-form>
    <div v-if="canShowLookupWizard">
      <FLookupFieldWizard
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="selectedLookupField"
        :siteId="selectedSiteId"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
    </div>
  </div>
</template>
<script>
import FWebform from '@/FWebform'
import FormBuilderMixin from '@/mixins/forms/FormBuilderMixin'
import draggable from 'vuedraggable'
import isEqual from 'lodash/isEqual'
import { isEmpty, isObject, areValuesEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
import { v4 as uuid } from 'uuid'
import { isLookupSimple } from '@facilio/utils/field'
import TeamStaffMixin from '@/mixins/TeamStaffMixin'
import FNewCurrencyField from 'src/components/FNewCurrencyField.vue'
import { getCalculatedCurrencyValue } from '../pages/setup/organizationSetting/currency/CurrencyUtil'

const defaultDragOptions = {
  animation: 150,
  easing: 'cubic-bezier(1, 0, 0, 1)',
}

export default {
  name: 'f-sub-form',
  extends: FWebform,
  mixins: [FormBuilderMixin, TeamStaffMixin],
  components: {
    draggable,
    FNewCurrencyField,
  },
  props: [
    'isLiveSubForm',
    'removeDefaultStyling',
    'customClass',
    'fieldError',
    'activeField',
    'isLastofType',
    'isFirstofType',
    'canShowRemove',
    'formIndex',
    'isV3Api',
    'isEdit',
    'subFormModuleData',
  ],
  data() {
    return {
      removeFieldsHash: ['photo', 'attachedFiles'],
      subFormFields: [],
      canShowFields: false,
    }
  },

  computed: {
    formFieldsDragOptions() {
      return {
        ...defaultDragOptions,
        draggable: '.form-drag-item',
        group: 'subform-fields',
        sort: true,
      }
    },
    moduleName() {
      let { form } = this
      if (!isEmpty(form)) {
        let { module } = form
        let { name } = module || {}
        return name
      }
      return null
    },
    formId() {
      let { form } = this
      if (!isEmpty(form)) {
        let { id } = form
        return id
      }
      return null
    },
    subFormActiveField: {
      get() {
        return this.activeField
      },
      set(value) {
        this.$emit('update:activeField', value)
      },
    },
    isSectionsEmpty() {
      let { form } = this
      let { sections = [] } = form || {}
      let { fields } = sections[0] || {}
      return isEmpty(fields)
    },
  },
  watch: {
    form: {
      handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          this.deserialize()
        }
      },
      deep: true,
    },
  },
  methods: {
    /* Data serialize and deserialize methods */
    deserializeFormData() {
      let { subFormActiveField } = this
      let {
        form: { sections = [] },
      } = this
      this.deserialize()
      if (!isEmpty(subFormActiveField)) {
        this.subFormActiveField = this.findSelectedField(
          sections,
          subFormActiveField
        )
      }
    },
    init() {
      let { moduleName, formId, isLiveSubForm } = this
      this.isLookupSimpleField = isLookupSimple
      this.initActionHandlers()
      this.deserialize()

      let { currencyCode, exchangeRate } = this.subFormModuleData || {}
      this.setCurrencyCodeInSubform(currencyCode, exchangeRate)

      if (!isLiveSubForm && !isEmpty(moduleName) && !isEmpty(formId)) {
        this.loadUnusedFields(moduleName, formId).then(fields =>
          this.setSubFormFields(fields)
        )
      }
    },
    async saveRecord(props = {}) {
      let { skipSubFormValidation = false } = props || {}
      let isValid = true
      if (!skipSubFormValidation && this.canValidateSubForm()) {
        isValid = await new Promise(res =>
          this.$refs['form'].validate(valid => res(valid))
        )
      } else {
        this.$refs['form'].clearValidate()
      }
      return new Promise((resolve, reject) => {
        let { formModel } = this
        if (isValid) {
          let _formModel = cloneDeep(formModel)
          resolve(_formModel)
        } else {
          reject()
        }
      })
    },
    /* Drag and drop related methods */
    onStart() {
      this.$emit('onDragStart')
    },
    onEnd() {
      this.$emit('onDragEnd')
    },
    onSubFormFieldChange() {
      let { form: subFormObj } = this
      this.debounceSaveRecord({ subFormObj, skipSettingSave: true })
    },
    /* Show/Hide/Disable related Actions */
    canDisableDrag() {
      let { fieldError, isSaving } = this
      return fieldError || isSaving
    },
    canDisableField(field) {
      let { isLiveSubForm } = this
      let { isDisabled } = field
      if (isLiveSubForm) {
        return isDisabled
      }
      return true
    },
    showHideFields(canShow = false, event) {
      let { isLiveSubForm } = this
      if (!isLiveSubForm) {
        if (event) {
          event.stopPropagation()
        }
        this.$set(this, 'canShowFields', canShow)
      }
    },
    setSubFormFields(fields) {
      let { isLiveSubForm } = this
      if (!isLiveSubForm) {
        let { systemFields, customFields } = fields
        let subFormFields = [...systemFields, ...customFields]
        this.$set(this, 'subFormFields', subFormFields)
      }
    },
    setActiveField(field) {
      let { isLiveSubForm } = this
      if (!isLiveSubForm) {
        this.subFormActiveField = field
        this.$emit('resetActiveSection')
      }
    },
    addSubFormField(field, index, event) {
      let { isLiveSubForm } = this
      if (!isLiveSubForm) {
        if (event) {
          event.stopPropagation()
        }
        let { subFormFields, form } = this
        let { sections } = form || {}
        let section = sections[0] || {}
        let { fields } = section
        subFormFields.splice(index, 1)
        if (isEmpty(fields)) {
          let _fields = []
          let _sections = []
          _fields.push(field)
          section.fields = _fields
          _sections.push(section)
          form.sections = _sections
        } else {
          fields.push(field)
        }
        this.setActiveField(field)
        this.$set(this, 'subFormFields', subFormFields)
        this.$set(this, 'canShowFields', false)
        this.saveFormRecord({ subFormObj: form, skipSettingSave: true })
      }
    },
    removeSubFormField(field, index, event) {
      let { isLiveSubForm } = this
      if (!isLiveSubForm) {
        if (event) {
          event.stopPropagation()
        }
        let { subFormFields, form: subFormObj } = this
        let { sections } = subFormObj || {}
        let section = sections[0] || {}
        let { fields } = section
        fields.splice(index, 1)
        this.$set(section.fields, 'fields', fields)
        subFormFields.push(field)
        if (this.isFieldActive(field)) {
          this.$emit('removeActiveField')
        }
        this.debounceSaveRecord({ subFormObj, skipSettingSave: true })
      }
    },

    /* live subform related methods */
    removeLineItem() {
      let { formIndex } = this
      this.$emit('removeLineItem', formIndex)
    },
    getRandomNumber() {
      return uuid()
    },
    canValidateSubForm() {
      let { formModel } = this
      let shouldSkipValidation
      Object.keys(formModel).forEach(field => {
        if (shouldSkipValidation) {
          return
        }
        if (!isObject(formModel[field]) && !isEmpty(formModel[field])) {
          shouldSkipValidation = true
        } else if (
          isObject(formModel[field]) &&
          !areValuesEmpty(formModel[field])
        ) {
          shouldSkipValidation = true
        }
      })
      return shouldSkipValidation
    },
    checkFieldType(field) {
      return !isEmpty(field)
        ? isLookupSimple(field) || this.$fieldUtils.isChooserTypeField(field)
        : false
    },
    setCurrencyCodeInSubform(currencyCode, exchangeRate) {
      let { formModel } = this
      this.formModel = { ...(formModel || {}), currencyCode, exchangeRate }
    },
    calculateExchangeRate(rateObj, fields) {
      this.formModel = (fields || []).reduce((formModel, field) => {
        let { name, displayTypeEnum } = field || {}
        let value = formModel[name]

        if (displayTypeEnum === 'MULTI_CURRENCY' && !isEmpty(value)) {
          formModel[name] = getCalculatedCurrencyValue(rateObj, value)
        }
        return formModel
      }, this.formModel)
    },
  },
}
</script>
<style lang="scss" scoped>
.forms-red-dot {
  color: red;
  margin-left: 3px;
  font-size: 15px;
}
.forms-field-text-label {
  color: #385571;
  letter-spacing: 0.7px;
  font-size: 14px !important;
  text-align: right;
  vertical-align: middle;
}
</style>
