<template>
  <div :class="!$validation.isEmpty(creationWidgets) ? 'd-flex' : 'height-100'">
    <div
      :class="[
        !removeDefaultStyling && 'f-webform-container',
        !$validation.isEmpty(customClass) ? customClass : '',
      ]"
    >
      <el-form
        ref="form"
        :model="formModel"
        :rules="rules"
        :label-position="labelPosition"
      >
        <div
          v-for="(section, sectionIndex) in form.sections"
          :key="sectionIndex"
          class="section-container flex-container"
          :class="[
            labelPosition === 'top' ? 'pR300' : '',
            sectionIndex === 0 ? '' : 'mT20',
            $getProperty(section, ['fields', 0, 'displayTypeEnum']) ===
              'BUDGET_AMOUNT' && 'budget-section-container',
          ]"
          :style="
            section.hideSection || (isSubForm(section) && !showSubform(section))
              ? 'display: none;'
              : ''
          "
        >
          <div class="section-header form-one-column">
            <div v-if="section.showLabel" class="pT50 text-uppercase">
              {{ section.name }}
              <div class="fc-heading-border-width43 mT15"></div>
            </div>
          </div>
          <div v-if="isSubForm(section)" class="form-one-column">
            <FSubFormWrapper
              class="position-relative"
              :ref="`${sectionIndex} ${section.subFormId}`"
              :key="`${sectionIndex} ${section.subFormId}`"
              :initialSubForm="section.subForm"
              :siteId="selectedSiteId"
              :subFormsArr.sync="section.subFormsArr"
              :isV3Api="isV3Api"
              :isEdit="isEdit"
              :isLoading="isLoading"
              @onSubFormModelChange="onSubFormModelChange"
            ></FSubFormWrapper>
          </div>
          <div
            v-else
            v-for="(field, fieldIndex) in section.fields"
            :key="`${sectionIndex} - ${fieldIndex}`"
            :class="[
              field.span === 1 ? 'form-one-column' : 'form-two-column',
              field.displayTypeEnum === 'TASKS' ? 'task-section' : '',
              field.hideField ? 'hide' : '',
            ]"
          >
            <!-- TODO: Have to remove this once rules feature is done -->
            <el-form-item
              v-if="isMvSection(field)"
              :label="field.displayName"
              :prop="field.name"
              :required="field.required"
              class="section-items"
            >
              <el-radio-group
                v-model="formModel[field.name]"
                v-if="field.displayTypeEnum === 'RADIO_TEMP'"
              >
                <el-radio
                  :data-test-selector="`${field.field.name}_true`"
                  :label="1"
                  class="fc-radio-btn"
                  >{{ field.field.trueVal }}</el-radio
                >
                <el-radio
                  :data-test-selector="`${field.field.name}_false`"
                  :label="0"
                  class="fc-radio-btn"
                  >{{ field.field.falseVal }}</el-radio
                >
              </el-radio-group>
              <f-formula-builder
                v-if="field.displayTypeEnum === 'FORMULA_FIELD_TEMP'"
                v-model="formModel[field.name].workflow"
                module="formulaField"
                :title="''"
                :renderInLeft="field.renderInLeft"
                :hideModeChange="field.hideModeChange"
              ></f-formula-builder>
              <el-input
                v-if="field.displayTypeEnum === 'TEXTBOX_TEMP'"
                :data-test-selector="`${field.name}`"
                class="fc-input-full-border2"
                v-model="formModel[field.name]"
                :disabled="field.isDisabled"
              ></el-input>
            </el-form-item>
            <!-- End -->
            <el-form-item
              v-else-if="
                !field.formulaFieldHack &&
                  ![
                    'LINEITEMS',
                    'INVREQUEST_LINE_ITEMS',
                    'TASKS',
                    'SADDRESS',
                    'ADDRESS',
                    'QUOTE_ADDRESS',
                    'QUOTE_LINE_ITEMS',
                    'PERMIT_CHECKLIST',
                    'COMMUNITY_PUBLISHING',
                    'ATTACHMENT',
                    'BUDGET_AMOUNT',
                    'FACILITY_AVAILABILITY',
                    'FACILITY_BOOKING_SLOTS',
                    'NOTES',
                  ].includes(field.displayTypeEnum)
              "
              :label="field.displayName"
              :prop="field.name"
              :required="field.required"
              class="section-items"
            >
              <template
                v-if="
                  ($constants.FORM_TEXTAREA_DISPLAY_TYPES.includes(
                    field.displayTypeEnum
                  ) &&
                    $getProperty(field, 'configJSON.richText')) ||
                    field.displayTypeEnum === 'RICH_TEXT'
                "
              >
                <RichTextArea
                  v-model="formModel[field.name]"
                  :field="field"
                  :isEdit="isEdit"
                  :disabled="field.isDisabled"
                />
              </template>
              <form-comments
                v-else-if="
                  field.displayTypeEnum === 'TICKETNOTES' &&
                    isNewCommentsEnabled
                "
                v-model="formModel[field.name]"
                :field="field"
                :parentModule="module"
                :recordId="moduleDataId"
                @clearError="clearError(field.name)"
              >
              </form-comments>

              <div
                v-else-if="
                  $constants.FORM_TEXTAREA_DISPLAY_TYPES.includes(
                    field.displayTypeEnum
                  )
                "
              >
                <el-input
                  :data-test-selector="`${field.name}`"
                  type="textarea"
                  :autosize="{ minRows: 4, maxRows: 6 }"
                  resize="none"
                  v-model="formModel[field.name]"
                  :placeholder="`Type your  ${field.displayName}`"
                  class="fc-input-full-border-textarea"
                  :key="`${sectionIndex}${fieldIndex}`"
                  :disabled="field.isDisabled"
                  @change="value => onChangeHandler({ value, field })"
                ></el-input>
                <el-checkbox
                  v-model="formModel['notifyRequester']"
                  v-if="
                    field.displayTypeEnum === 'TICKETNOTES' &&
                      canShowNotifyRequester
                  "
                >
                  {{ $t('common._common.notify') }}
                </el-checkbox>
              </div>
              <el-checkbox
                :data-test-selector="`${field.name}`"
                v-model="formModel[field.name]"
                v-else-if="field.displayTypeEnum === 'DECISION_BOX'"
                :disabled="field.isDisabled"
                @change="value => onChangeHandler({ value, field })"
              ></el-checkbox>
              <el-radio-group
                v-model="formModel[field.name]"
                v-else-if="field.displayTypeEnum === 'RADIO'"
                :disabled="field.isDisabled"
                @change="value => onChangeHandler({ value, field })"
              >
                <el-radio
                  :data-test-selector="`${field.field.name}_true`"
                  :label="true"
                  class="fc-radio-btn"
                  >{{ field.field.trueVal }}</el-radio
                >
                <el-radio
                  :data-test-selector="`${field.field.name}_false`"
                  :label="false"
                  class="fc-radio-btn"
                  >{{ field.field.falseVal }}</el-radio
                >
              </el-radio-group>
              <f-site-field
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
              ></f-site-field>
              <template
                v-else-if="field.displayTypeEnum === 'MULTI_LOOKUP_SIMPLE'"
              >
                <Lookup
                  v-if="checkNewLookupWizardEnabled"
                  :key="`${fieldIndex} ${field.id}`"
                  :field="field"
                  v-model="formModel[field.name]"
                  :disabled="field.isDisabled"
                  :hideDropDown="
                    $fieldUtils.isChooserTypeField(field) &&
                      !(field.config || {}).isFiltersEnabled
                  "
                  :siteId="selectedSiteId"
                  @showLookupWizard="showLookupWizard"
                  @recordSelected="
                    (value, field) => onRecordSelected({ value, field })
                  "
                  @setLookupFieldValue="setLookupFieldValue"
                ></Lookup>
                <FLookupField
                  v-else
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
                  @showLookupWizard="showLookupWizard"
                  @setLookupFieldValue="setLookupFieldValue"
                  @recordSelected="
                    (value, field) => onRecordSelected({ value, field })
                  "
                ></FLookupField>
              </template>

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
                  (value, field) => onRecordSelected({ value, field })
                "
              ></FLookupFieldWithFloorplan>
              <template
                v-else-if="
                  isLookupSimpleOrChooserType(field) ||
                    field.displayTypeEnum === 'SPACECHOOSER'
                "
              >
                <Lookup
                  v-if="checkNewLookupWizardEnabled"
                  v-model="(formModel[field.name] || {}).id"
                  :key="`${fieldIndex} ${field.id}`"
                  :field="field"
                  :disabled="field.isDisabled"
                  :hideDropDown="
                    $fieldUtils.isChooserTypeField(field) &&
                      !(field.config || {}).isFiltersEnabled
                  "
                  :siteId="selectedSiteId"
                  @showLookupWizard="showLookupWizard"
                  @recordSelected="
                    (value, field) => onRecordSelected({ value, field })
                  "
                  @setLookupFieldValue="setLookupFieldValue"
                ></Lookup>
                <FLookupField
                  v-else
                  :data-test-selector="`${field.name}`"
                  :key="`${fieldIndex} ${field.id}`"
                  :model.sync="(formModel[field.name] || {}).id"
                  :field="field"
                  :disabled="field.isDisabled"
                  :hideDropDown="
                    $fieldUtils.isChooserTypeField(field) &&
                      !(field.config || {}).isFiltersEnabled
                  "
                  :siteId="selectedSiteId"
                  @showLookupWizard="showLookupWizard"
                  @setLookupFieldValue="setLookupFieldValue"
                  @recordSelected="
                    (value, field) => onRecordSelected({ value, field })
                  "
                ></FLookupField>
              </template>
              <el-select
                v-else-if="
                  ['SELECTBOX'].includes(field.displayTypeEnum) &&
                    isMultiple(field)
                "
                :data-test-selector="`${field.name}`"
                :ref="`${fieldIndex} ${field.id}`"
                :key="`${fieldIndex} ${field.id}`"
                v-model="formModel[field.name]"
                :multiple="true"
                filterable
                collapse-tags
                :clearable="!field.required"
                :disabled="field.isDisabled"
                :placeholder="$t('common._common.select')"
                class="fc-input-full-border-select2 width100 fc-tag"
                @change="value => onChangeHandler({ value, field })"
              >
                <div slot="empty">
                  <div class="el-select-dropdown__empty">
                    {{ $t('common._common.empty_option_text') }}
                  </div>
                  <el-button
                    v-if="field.allowCreate"
                    :data-test-selector="
                      `${$t('maintenance._workorder.add_option')}`
                    "
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
                  :data-test-selector="`${field.name}_${option.label}`"
                ></el-option>
                <div v-if="field.allowCreate" class="add-option-container">
                  <el-button
                    class="add-option-content"
                    :data-test-selector="
                      `${$t('maintenance._workorder.add_option')}`
                    "
                    @click="addOption(field, sectionIndex, fieldIndex)"
                    >{{ $t('maintenance._workorder.add_option') }}</el-button
                  >
                </div>
              </el-select>
              <el-select
                v-else-if="
                  ['SELECTBOX', 'URGENCY', 'REQUESTER'].includes(
                    field.displayTypeEnum
                  )
                "
                :data-test-selector="`${field.name}`"
                :ref="`${fieldIndex} ${field.id}`"
                v-model="(formModel[field.name] || {}).id"
                filterable
                :clearable="!field.required"
                :disabled="field.isDisabled"
                :placeholder="$t('common._common.select')"
                class="fc-input-full-border-select2 width100"
                @change="value => onChangeHandler({ value, field })"
              >
                <div slot="empty">
                  <div class="el-select-dropdown__empty">
                    {{ $t('common._common.empty_option_text') }}
                  </div>
                  <el-button
                    v-if="field.allowCreate"
                    :data-test-selector="
                      `${$t('maintenance._workorder.add_option')}`
                    "
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
                  :data-test-selector="`${field.name}_${option.label}`"
                ></el-option>
                <div v-if="field.allowCreate" class="add-option-container">
                  <el-button
                    class="add-option-content"
                    :data-test-selector="
                      `${$t('maintenance._workorder.add_option')}`
                    "
                    @click="addOption(field, sectionIndex, fieldIndex)"
                    >{{ $t('maintenance._workorder.add_option') }}</el-button
                  >
                </div>
              </el-select>
              <div
                v-else-if="field.displayTypeEnum === 'TEAMSTAFFASSIGNMENT'"
                class="fc-input-full-border-select2"
                :class="[field.isDisabled ? 'disabled-background' : '']"
                data-test-selector="Team/Staff"
                :disabled="field.isDisabled"
              >
                <div
                  class="fc-border-input-div2"
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
                  :siteId="teamStaffSiteId"
                  viewtype="form"
                  @onChange="(...args) => updateAssignedTo(args, field)"
                ></FAssignment>
              </div>
              <f-formula-builder
                v-else-if="field.displayTypeEnum === 'FORMULA_FIELD'"
                v-model="formModel[field.name].workflow"
                module="formulaField"
                :title="''"
                :renderInLeft="field.renderInLeft"
                :hideModeChange="field.hideModeChange"
              ></f-formula-builder>
              <div
                v-else-if="
                  field.displayTypeEnum === 'IMAGE' ||
                    field.displayTypeEnum === 'FILE'
                "
              >
                <FFileUpload
                  :field="field"
                  :isFileType="field.displayTypeEnum === 'FILE'"
                  :isDisabled="field.isDisabled"
                  :imgContentUrl.sync="field.imgUrl"
                  :fileObj.sync="field.fileObj"
                  :showWebCamPhoto="true"
                  :isV3Api="isV3Api"
                  :isSaveBtnDisabled.sync="isSaveBtnDisabled"
                  :addImgFile="
                    (fileOrId, field) => addImgFile({ fileOrId, field })
                  "
                  @removeImgFile="field => removeImgFile({ field })"
                ></FFileUpload>
              </div>
              <div v-else-if="field.displayTypeEnum === 'SIGNATURE'">
                <FSignaturePad
                  v-if="!field.hideField"
                  :model.sync="formModel[field.name]"
                  :fileName="field.name"
                  :isV3Api="isV3Api"
                  :imgUrl.sync="field.imgUrl"
                  :isSaveBtnDisabled.sync="isSaveBtnDisabled"
                  :canShowColorPalette="
                    (field.config || {}).canShowColorPalette
                  "
                ></FSignaturePad>
              </div>
              <div v-else-if="field.displayTypeEnum === 'WEEK_MATRIX'">
                <WeekMatrix
                  :weekendData.sync="formModel[field.name]"
                ></WeekMatrix>
              </div>
              <div v-else-if="field.displayTypeEnum === 'COLOR_PICKER'">
                <el-color-picker
                  v-model="formModel[field.name]"
                  :predefine="predefineColors(field)"
                ></el-color-picker>
              </div>
              <div v-else-if="field.displayTypeEnum === 'DURATION'">
                <f-duration-field
                  v-model="formModel[field.name]"
                  :key="field.fieldId"
                  class="duration-container"
                  :field="field"
                  :isDisabled="field.isDisabled"
                  @updateDurationValue="updateDurationValue"
                ></f-duration-field>
              </div>
              <FTimePicker
                v-else-if="field.displayTypeEnum === 'TIME'"
                :config="field.config"
                v-model="formModel[field.name]"
                :disabled="field.isDisabled"
                :field="field"
                :placeholder="$t('fields.properties.time_picker_placeholder')"
                class="el-select fc-input-full-border-select2 width100"
                @change="value => onChangeHandler({ value, field })"
              ></FTimePicker>
              <FDatePicker
                v-else-if="field.displayTypeEnum === 'DATE'"
                :key="`${fieldIndex} ${field.id}`"
                v-model="formModel[field.name]"
                :type="'date'"
                class="fc-input-full-border2 form-date-picker"
                :disabled="field.isDisabled"
                @change="() => onChangeHandler({ field })"
              ></FDatePicker>
              <FDatePicker
                v-else-if="field.displayTypeEnum === 'DATETIME'"
                :key="`${fieldIndex} ${field.id}`"
                v-model="formModel[field.name]"
                :type="'datetime'"
                class="fc-input-full-border2 form-date-picker"
                :disabled="field.isDisabled"
                @change="() => onChangeHandler({ field })"
              ></FDatePicker>
              <FDatePicker
                v-else-if="field.displayTypeEnum === 'DATERANGE'"
                :key="`${fieldIndex} ${field.id}`"
                v-model="formModel[field.name]"
                :type="'daterange'"
                class="fc-input-full-border2 form-date-picker"
                :disabled="field.isDisabled"
                @change="() => onChangeHandler({ field })"
              ></FDatePicker>
              <div v-else-if="field.displayTypeEnum === 'DAILY_START_END'">
                <FScheduleField
                  :key="`${fieldIndex} ${field.id}`"
                  :model="formModel"
                  :config="field.config"
                ></FScheduleField>
              </div>
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
              <div v-else-if="field.displayTypeEnum === 'TIMEZONE'">
                <FTimeZoneField
                  :key="`${fieldIndex} ${field.id}`"
                  v-model="formModel[field.name]"
                  :field="field"
                ></FTimeZoneField>
              </div>
              <div v-else-if="field.displayTypeEnum === 'LANGUAGE'">
                <FLanguageFiled
                  :key="`${fieldIndex} ${field.id}`"
                  v-model="formModel[field.name]"
                  :field="field"
                ></FLanguageFiled>
              </div>
              <el-input
                :data-test-selector="`${field.name}`"
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
                :disabled="field.isDisabled"
                @change="() => onChangeHandler({ field })"
              >
                <template
                  v-if="canShowMetric(field)"
                  :slot="metricSymbolPosition(field)"
                >
                  {{ (field.field || {}).unit }}
                </template>
              </el-input>
              <el-input
                :data-test-selector="`${field.name}`"
                v-else-if="field.displayTypeEnum === 'PERCENTAGE'"
                v-model="formModel[field.name]"
                class="fc-input-full-border2 fc-input-icon"
                @change="() => onChangeHandler({ field })"
              >
                <template :slot="field.iconPosition">
                  <inline-svg
                    :src="field.icon"
                    class="vertical-middle"
                    iconClass="icon icon-xs"
                  ></inline-svg>
                </template>
              </el-input>

              <FUrlField
                v-else-if="field.displayTypeEnum === 'URL_FIELD'"
                :field="field"
                :isEdit="isEdit"
                :config="field.config"
                v-model="formModel[field.name]"
              ></FUrlField>
              <template
                v-else-if="
                  ['JP_TASK', 'JP_PREREQUISITE'].includes(field.displayTypeEnum)
                "
              >
                <JobPlanTasks
                  :formModel="formModel"
                  :type="field.displayTypeEnum"
                  :isJobPlan="true"
                  :field="field"
                  :isEdit="isEdit"
                  @updateFormModel="updateFormModel"
                  v-model="formModel[field.name]"
                />
              </template>
              <div v-else-if="field.displayTypeEnum === 'CURRENCY'">
                <FCurrencyField
                  :field="field"
                  :isEdit="isEdit"
                  :disabled="field.isDisabled"
                  v-model="formModel[field.name]"
                  @handleChange="value => onChangeHandler({ value, field })"
                ></FCurrencyField>
              </div>
              <div v-else-if="field.displayTypeEnum === 'MULTI_CURRENCY'">
                <FNewCurrencyField
                  :key="`${currencyDetails.currencyCode}`"
                  :field="field"
                  :isEdit="isEdit"
                  :disabled="field.isDisabled"
                  :moduleData="currencyDetails"
                  :initialCurrency="initialCurrency"
                  v-model="formModel[field.name]"
                  @handleChange="value => onChangeHandler({ value, field })"
                  @calculateExchangeRate="
                    rateObj => calculateExchangeRate(rateObj)
                  "
                  @setCurrencyCode="setCurrencyCodeOnChange"
                ></FNewCurrencyField>
              </div>
              <FEmailField
                v-else-if="field.displayTypeEnum === 'EMAIL'"
                :field="field"
                v-model="formModel[field.name]"
                :isDisabled="field.isDisabled"
                @change="value => onChangeHandler({ value, field })"
              ></FEmailField>
              <el-input
                v-else
                :data-test-selector="`${field.name}`"
                class="fc-input-full-border2"
                v-model="formModel[field.name]"
                :disabled="field.isDisabled"
                @blur.passive="onBlurHandler({ field })"
                @change="value => onChangeHandler({ value, field })"
              ></el-input>
            </el-form-item>
            <template v-else-if="field.displayTypeEnum === 'ATTACHMENT'">
              <FormAttachments
                :formModel="formModel"
                :attachments="attachments"
                :field="field"
                :labelPosition="labelPosition"
                :isV3Api="isV3Api"
                :clearError="clearError"
                @updateAttachment="updateAttachment"
                @updateFormModel="updateFormModel"
                :disableSaveBtn="value => (isSaveBtnDisabled = value)"
              />
            </template>
            <div
              v-else-if="
                field.displayTypeEnum === 'SADDRESS' ||
                  field.displayTypeEnum === 'ADDRESS'
              "
            >
              <f-address-field
                :model="formModel[field.name]"
                :field="field"
                :storeRoomId="(formModel['storeRoom'] || {}).id"
                :vendorId="(formModel['vendor'] || {}).id"
              ></f-address-field>
            </div>
            <div v-else-if="field.displayTypeEnum === 'QUOTE_ADDRESS'">
              <FQuoteAddress :model="formModel"></FQuoteAddress>
            </div>
            <div v-else-if="field.displayTypeEnum === 'LINEITEMS'">
              <FLineItem
                :key="`${currencyDetails.currencyCode}`"
                :model.sync="formModel"
                :lineItems.sync="formModel[field.name]"
                :currencyObj="currencyDetails"
                :initialCurrency="initialCurrency"
                :hasMultiCurrencyFieldInModel="hasMultiCurrencyFieldInModel"
                :vendorId="(formModel['vendor'] || {}).id"
                :field="field"
                :isEdit="isEdit"
                :module="module"
                :ref="`line-item`"
                @calculateExchangeRate="
                  rateObj => calculateExchangeRate(rateObj)
                "
                @setCurrencyCode="setCurrencyCodeOnChange"
              ></FLineItem>
            </div>
            <div v-else-if="field.displayTypeEnum === 'INVREQUEST_LINE_ITEMS'">
              <FTransferRequestLineItem
                :model="formModel"
                :lineItems="formModel[field.name]"
                :field="field"
                :isEdit="isEdit"
                :module="module"
                :ref="`line-item`"
              ></FTransferRequestLineItem>
            </div>
            <div v-else-if="field.displayTypeEnum === 'QUOTE_LINE_ITEMS'">
              <FQuoteLineItem
                :key="`${currencyDetails.currencyCode}`"
                :model.sync="formModel"
                :lineItems.sync="formModel[field.name]"
                :initialCurrency="initialCurrency"
                :currencyObj="currencyDetails"
                :hasMultiCurrencyFieldInModel="hasMultiCurrencyFieldInModel"
                :field="field"
                :isEdit="isEdit"
                :module="module"
                :ref="`line-item`"
                @calculateExchangeRate="
                  rateObj => calculateExchangeRate(rateObj)
                "
                @setCurrencyCode="setCurrencyCodeOnChange"
              ></FQuoteLineItem>
            </div>
            <div v-else-if="field.displayTypeEnum === 'BUDGET_AMOUNT'">
              <FBudgetAmount
                :model.sync="formModel"
                :budgetamount="formModel.budgetamount"
                :initialCurrency="initialCurrency"
                :currencyObj="currencyDetails"
                :field="field"
                :ref="`budget-amount`"
                @calculateExchangeRate="
                  rateObj => calculateExchangeRate(rateObj)
                "
                @setCurrencyCode="setCurrencyCodeOnChange"
              ></FBudgetAmount>
            </div>
            <div v-else-if="field.displayTypeEnum === 'FACILITY_AVAILABILITY'">
              <FFacilityAvailability
                :model="formModel"
                :moduleData="moduleData"
                :field="field"
              ></FFacilityAvailability>
            </div>
            <div v-else-if="field.displayTypeEnum === 'FACILITY_BOOKING_SLOTS'">
              <FFacilityBookingSlots
                :model="formModel"
                :bookingslot.sync="formModel[field.name]"
                :field="field"
                :facilityId="(formModel['facility'] || {}).id"
              ></FFacilityBookingSlots>
            </div>
            <div
              v-else-if="field.displayTypeEnum === 'PERMIT_CHECKLIST'"
              class="permit-checklist"
            >
              <FFormPermitChecklist
                :model="formModel"
                :field="field"
              ></FFormPermitChecklist>
            </div>
            <template
              v-else-if="field.displayTypeEnum === 'COMMUNITY_PUBLISHING'"
            >
              <FPublishTo
                v-model="formModel[field.name]"
                :formModel.sync="formModel"
                :field="field"
                :isEdit="isEdit"
              ></FPublishTo>
            </template>
            <template v-else-if="field.displayTypeEnum === 'NOTES'">
              <div
                class="pL10 notes-content"
                :class="getNotesClass(field)"
                v-html="sanitize(field.value)"
              ></div>
            </template>
            <div v-else-if="!field.formulaFieldHack">
              <TasksList
                :tasksList.sync="tasksList"
                :spaceAssetResourceObj="spaceAssetResourceObj"
                :hasError.sync="hasError"
                :filter="filter"
              ></TasksList>
            </div>
          </div>
        </div>
      </el-form>
      <!-- To fill the extra space on form with minimum fields -->
      <div class="flex-grow flex-shrink white-background"></div>
      <div class="d-flex mT-auto fc-web-form-action-btn flex-row-reverse">
        <el-button
          v-if="canShowPrimaryBtn"
          data-test-submit
          type="primary"
          class="form-btn f13 bold primary m0 text-center text-uppercase save-btn-focus"
          :loading="isSaving"
          :disabled="isSaveBtnDisabled"
          @click="saveRecord()"
        >
          {{ form.primaryBtnLabel }}
          <!-- <inline-svg src="add-icon" class="vertical-middle" iconClass="icon icon-md mR5"></inline-svg> -->
        </el-button>
        <el-button
          data-test-cancel
          v-if="canShowSecondaryBtn"
          class="form-btn f13 bold secondary text-center text-uppercase cancel-btn-focus"
          @click="cancel()"
          >{{ form.secondaryBtnLabel }}</el-button
        >
      </div>
      <div v-if="showResourceWizard">
        <LookupWizard
          :canShowLookupWizard.sync="showResourceWizard"
          :field="selectedLookupField"
          @setLookupFieldValue="setLookupFieldValue"
          :siteId="selectedSiteId"
        ></LookupWizard>
      </div>
      <div v-else-if="canShowLookupWizard">
        <LookupWizard
          v-if="checkNewLookupWizardEnabled"
          :canShowLookupWizard.sync="canShowLookupWizard"
          :field="selectedLookupField"
          @setLookupFieldValue="setLookupFieldValue"
          :siteId="selectedSiteId"
        ></LookupWizard>
        <FLookupFieldWizard
          v-else
          :canShowLookupWizard.sync="canShowLookupWizard"
          :selectedLookupField="selectedLookupField"
          :siteId="selectedSiteId"
          @setLookupFieldValue="setLookupFieldValue"
        ></FLookupFieldWizard>
      </div>
    </div>
    <portal to="side-bar-widgets" v-if="canShowSidebarWidgets">
      <el-collapse
        :class="isSingleWidgetConfigured ? 'height100' : ''"
        v-model="activeWidgets"
      >
        <el-collapse-item
          v-for="(widget, index) in sideBarWidgets"
          :key="index"
          :title="widget.widgetName"
          :name="widget.widgetName"
          :class="isSingleWidgetConfigured ? 'expand-widget' : ''"
        >
          <ConnectedAppViewWidget
            :ref="`ref-${widget.id}`"
            :key="widget.id"
            :widgetId="widget.id"
            :recordId="moduleDataId"
            :context="moduleData"
            :handlers="handlers"
          ></ConnectedAppViewWidget>
        </el-collapse-item>
      </el-collapse>
    </portal>
    <!-- Have to hide background widgets always -->
    <div class="hide">
      <div v-for="(widget, index) in backgroundWidgets" :key="index">
        <ConnectedAppViewWidget
          :ref="`ref-${widget.id}`"
          :key="widget.id"
          :widgetId="widget.id"
          :recordId="moduleDataId"
          :context="moduleData"
          :handlers="handlers"
        ></ConnectedAppViewWidget>
      </div>
    </div>
  </div>
</template>

<script>
import FLookupFieldWizard from '@/FLookupFieldWizard'
import FLookupField from '@/forms/FLookupField'
import WebFormMixin from '@/mixins/forms/WebFormMixin'
import Tasks from '@/mixins/tasks/TasksMixin'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
import FFileUpload from '@/FFileUpload'
import FDurationField from '@/FDurationField'
import FAddressField from '@/FAddressField'
import FSiteField from '@/FSiteField'
import FLineItem from '@/forms/FPoPrLineItem'
import FTransferRequestLineItem from 'src/components/forms/FTransferRequestLineItem'
import FQuoteLineItem from '@/forms/FQuoteLineItem'
import FQuoteAddress from '@/forms/FQuoteAddress'
import FSubFormWrapper from '@/FSubFormWrapper'
import FScheduleField from '@/FScheduleField'
import FSignaturePad from '@/FSignaturePad'
import TasksList from '@/TasksList'
import Constants from 'util/constant'
import FFormPermitChecklist from '@/forms/FFormPermitChecklist'
import FPublishTo from '@/forms/FPublishTo'
import RichTextArea from '@/forms/RichTextArea'
import FLocationField from '@/forms/FLocationField'
import FTimeZoneField from '@/forms/FFormTimeZoneField'
import FormAttachments from '@/forms/FFormAttachments'
import ConnectedAppViewWidget from 'pages/connectedapps/ConnectedAppViewWidget'
import FBudgetAmount from '@/forms/FBudgetAmountsTable'
import { API } from '@facilio/api'
import FFacilityAvailability from '@/forms/FFacilityAvailability'
import FFacilityBookingSlots from '@/forms/FFacilityBookingSlots'
import FTimePicker from '@/FTimePicker'
import FLookupFieldWithFloorplan from '@/forms/FLookupFieldWithFloorplan'
import { sanitize } from '@facilio/utils/sanitize'
import { isLookupSimple, isSiteField } from '@facilio/utils/field'
import { deepCloneObject } from 'util/utility-methods'
import { constructEnumFieldOptions } from '@facilio/utils/utility-methods'
import cloneDeep from 'lodash/cloneDeep'
import { areValuesEmpty, isEmpty, isFunction } from '@facilio/utils/validation'
import FUrlField from 'newapp/components/FUrlField'
import JobPlanTasks from 'newapp/components/tasks-creation/TasksLayout'
import WeekMatrix from 'components/WeekMatrix/WeekMatrix'
import { Lookup, LookupWizard } from '@facilio/ui/forms'
import FCurrencyField from 'src/components/FCurrencyField.vue'
import FEmailField from '@/FEmailField'
import TeamStaffMixin from '@/mixins/TeamStaffMixin'
import FormComments from './notes/FormComments.vue'
import FLanguageFiled from '@/forms/FFormLanguageField'
import FNewCurrencyField from 'src/components/FNewCurrencyField.vue'
import { mapState } from 'vuex'

export default {
  name: 'f-web-form',
  mixins: [WebFormMixin, Tasks, TeamStaffMixin],
  components: {
    FLookupFieldWizard,
    FLookupField,
    Lookup,
    LookupWizard,
    TasksList,
    FDatePicker,
    FFormulaBuilder,
    FFileUpload,
    FDurationField,
    FAddressField,
    FLineItem,
    FTransferRequestLineItem,
    FSiteField,
    FSubFormWrapper,
    FScheduleField,
    FQuoteLineItem,
    FQuoteAddress,
    FFormPermitChecklist,
    FSignaturePad,
    FPublishTo,
    RichTextArea,
    FLocationField,
    FormAttachments,
    ConnectedAppViewWidget,
    FBudgetAmount,
    FFacilityAvailability,
    FFacilityBookingSlots,
    FTimePicker,
    FTimeZoneField,
    FLookupFieldWithFloorplan,
    FUrlField,
    JobPlanTasks,
    WeekMatrix,
    FCurrencyField,
    FEmailField,
    FormComments,
    FLanguageFiled,
    FNewCurrencyField,
  },
  props: {
    form: {
      type: Object,
      required: true,
    },
    module: {
      type: String,
    },
    moduleDisplayName: {
      type: String,
    },
    isSaving: {
      type: Boolean,
    },
    canShowPrimaryBtn: {
      type: Boolean,
    },
    canShowSecondaryBtn: {
      type: Boolean,
    },
    customClass: {
      type: String,
    },
    isEdit: {
      type: Boolean,
    },
    removeDefaultStyling: {
      type: Boolean,
    },
    formLabelPosition: {
      type: String,
    },
    fieldTransitionFn: {
      type: Function,
    },
    canShowNotifyRequester: {
      type: Boolean,
    },
    isV3Api: {
      type: Boolean,
    },
    moduleData: {
      type: Object,
      default: () => ({}),
    },
    moduleDataId: {
      type: Number,
    },
    isWidgetsSupported: {
      type: Boolean,
      default: false,
    },
    parentSiteId: {
      type: Number,
    },
    subFormRecords: {
      type: Object,
    },
    modifyFieldPropsHook: {
      type: Function,
      default: () => ({}),
    },
    modifySectionPropsHook: {
      type: Function,
      default: () => ({}),
    },
    isEditForm: {
      type: Boolean,
    },
  },
  created() {
    this.init()
    this.setInitialCurrency()
    this.sanitize = sanitize
    this.isSiteField = isSiteField
  },
  data() {
    return {
      tasksList: [],
      spaceAssetResourceObj: {},
      attachments: [],
      activeWidgets: [],
      rules: {},
      formModel: {},
      filter: {},
      ruleFieldIds: [],
      hasError: false,
      handlers: {},
      canShowLookupWizard: false,
      showResourceWizard: false,
      selectedLookupField: null,
      isSaveBtnDisabled: false,
      creationWidgets: [],
      isLoading: true,
      subFormModels: [],
      currencyData: null,
      oldCurrencyCode: null,
      initialCurrency: {},
      hasMultiCurrencyFieldInModel: false,
    }
  },
  watch: {
    isSaveBtnDisabled(value) {
      this.$emit('disableSaveBtn', value)
    },
    formModel: {
      handler(newVal) {
        this.$emit('onFormModelChange', newVal)
      },
      deep: true,
    },
    sideBarWidgets: {
      handler(newVal) {
        this.$emit('onWidgetChange', newVal)
      },
    },
  },
  computed: {
    ...mapState({
      currencyList: state => state.activeCurrencies,
    }),
    checkNewLookupWizardEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_LOOKUP_WIZARD')
    },
    isNewCommentsEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_COMMENTS')
    },
    labelPosition() {
      let { formLabelPosition, form } = this
      let { labelPosition } = form
      if (!isEmpty(formLabelPosition)) {
        return (
          Constants.FORMS_LABELALIGNMENT_ENUMHASH[formLabelPosition] || 'top'
        )
      }
      return Constants.FORMS_LABELALIGNMENT_ENUMHASH[labelPosition] || 'left'
    },
    sideBarWidgets() {
      let { creationWidgets } = this
      if (!isEmpty(creationWidgets)) {
        return creationWidgets.filter(
          widget => widget.entityTypeEnum === 'CREATE_RECORD_SIDEBAR'
        )
      }
      return []
    },
    backgroundWidgets() {
      let { creationWidgets } = this
      if (!isEmpty(creationWidgets)) {
        return creationWidgets.filter(
          widget => widget.entityTypeEnum === 'FORM_BACKGROUND'
        )
      }
      return []
    },
    canShowSidebarWidgets() {
      let { sideBarWidgets } = this
      return !isEmpty(sideBarWidgets)
    },
    isSingleWidgetConfigured() {
      let { sideBarWidgets } = this
      if (!isEmpty(sideBarWidgets)) {
        return sideBarWidgets.length === 1
      }
      return false
    },
    isSubFormPresent() {
      let sections = this.$getProperty(this, 'form.sections', [])
      return sections.some(section => !isEmpty((section || {}).subForm))
    },
    teamStaffSiteId() {
      let { selectedSiteId, moduleData } = this
      let { siteId } = moduleData || {}
      return selectedSiteId || siteId
    },
    currencyDetails: {
      get() {
        let {
          currencyCode,
          exchangeRate,
          oldCurrencyCode,
          initialCurrencyCode,
        } = this.currencyData || this.moduleData || {}
        return {
          currencyCode,
          exchangeRate,
          oldCurrencyCode,
          initialCurrencyCode,
        }
      },
      set(value) {
        this.currencyData = { ...(this.moduleData || {}), ...value }
      },
    },
  },
  mounted() {
    this.handleResourceSelectAttribute()
  },
  methods: {
    updateAssignedTo(args, field) {
      let user = args[0]
      if (!isEmpty(user)) {
        this.$set(this.formModel['assignment'], 'assignedTo', user)
      }
      this.onChangeHandler({ field })
    },
    async init() {
      this.isLookupSimpleField = isLookupSimple
      this.initActionHandlers()
      await this.deserialize()
      let creationWidgets = await this.getRecordWidgets()
      this.creationWidgets = creationWidgets || []
      if (!isEmpty(creationWidgets)) {
        this.initWidgets()
        this.initWidgetHandlers()
      }
    },
    onSubFormModelChange({
      model,
      subFormName,
      field,
      triggerAddDeleteRules,
      params = {},
      triggerOnLoad,
    }) {
      // This is used to track the changes in the sub forms, on change handler will be called
      // with changed field as trigger.
      let { subFormModels } = this
      this.subFormModels = { ...subFormModels, [subFormName]: model }
      this.onChangeHandler({ field })
      if (triggerAddDeleteRules) this.fetchOnDataActionsList(4, params)
      if (triggerOnLoad) this.fetchOnDataActionsList()
    },
    async getRecordWidgets() {
      let { isWidgetsSupported, form } = this
      let { id } = form || {}
      let operatorValue = 10
      // have to load background widgets always, operatorValue for fetching background widget is 10
      let filters = {
        entityType: {
          operatorId: 9,
          value: [`${operatorValue}`],
        },
      }
      if (!isEmpty(id)) {
        filters = {
          ...filters,
          entityId: {
            operatorId: 9,
            value: [`${id}`],
          },
        }
        // This boolean is to show side bar widget,
        if (isWidgetsSupported) {
          filters.entityType.value = [`${operatorValue}`, '8']
        }
        let { error, data } = await this.loadWidgets(filters)
        if (error) {
          let { message = 'Error occured' } = error
          this.$message.error(message)
        } else {
          let { connectedAppWidgets } = data || {}
          return connectedAppWidgets
        }
      }
      return []
    },
    async loadWidgets(filters) {
      let encodedFilters = encodeURIComponent(JSON.stringify(filters))
      let url = `/v2/connectedApps/widgetList?filters=${encodedFilters}`
      let response = await API.get(url)
      return response
    },
    initWidgets() {
      let { creationWidgets } = this
      let [firstWidget] = creationWidgets
      let { widgetName } = firstWidget || {}
      let activeWidgets = [widgetName]
      this.$set(this, 'activeWidgets', activeWidgets)
    },
    predefineColors(field) {
      let color = this.$getProperty(field, 'config.predefineColors')
      return color
    },
    initWidgetHandlers() {
      let setValue = options => {
        let { fieldName, value } = options
        let { formModel } = this

        let field = this.getFieldByName({ name: fieldName }) || {}
        if (!isEmpty(field)) {
          let { displayTypeEnum } = field
          if (displayTypeEnum === 'FILE') {
            let { name } = value || {}
            this.$set(field, 'fileObj', { name })
          }
        }
        this.$set(formModel, fieldName, value)
      }
      let setSubFormData = options => {
        let { form, ruleFieldIds } = this
        let { sections } = form || {}
        let { subFormId, subFormValue } = options
        if (!isEmpty(subFormId) && !areValuesEmpty(subFormValue)) {
          let selectedSection = sections.find(
            section => section.subFormId === subFormId
          )

          let { subForm } = selectedSection || {}
          let subFormsArr =
            this.constructSubFormValues({
              subForm,
              subFormValue,
              ruleFieldIds,
            }) || []
          this.$set(selectedSection, 'subFormsArr', subFormsArr)
        }
      }
      let getValue = options => {
        let { fieldName } = options
        let { formModel } = this
        return formModel[fieldName]
      }
      let getFormData = () => {
        return this.getCurrentFormData()
      }
      let getFormMeta = () => {
        let { form } = this
        return form
      }
      let getCurrentRecord = () => {
        let { moduleData } = this
        return moduleData || {}
      }
      this.handlers = {
        setValue,
        getValue,
        getFormData,
        getFormMeta,
        getCurrentRecord,
        setSubFormData,
      }
    },
    initActionHandlers() {
      let actionsMap = {
        show: {
          handler: prop => this.handleShowHideAction(prop),
        },
        hide: {
          handler: prop => this.handleShowHideAction(prop),
        },
        enable: {
          handler: prop => this.handleEnableDisableAction(prop),
        },
        disable: {
          handler: prop => this.handleEnableDisableAction(prop),
        },
        set: {
          handler: prop => this.handleSetValueAction(prop),
        },
        filter: {
          handler: prop => this.handleSetFilter(prop),
        },
        hideSection: {
          handler: prop => this.handleShowHideSection(prop),
        },
        showSection: {
          handler: prop => this.handleShowHideSection(prop),
        },
        setMandatory: {
          handler: prop => this.handleSetRemoveMandatory(prop),
        },
        removeMandatory: {
          handler: prop => this.handleSetRemoveMandatory(prop),
        },
      }
      this.actionsMap = actionsMap
    },
    getNotesClass(field) {
      let { config } = field || {}
      let { isRichTextMode } = config || {}
      return isRichTextMode ? '' : 'plain-text'
    },
    async deserialize() {
      this.isLoading = true
      let {
        form,
        removeFieldsHash,
        isEdit,
        moduleDataId,
        modifySectionPropsHook,
        isLiveSubForm,
        isSubFormPresent,
      } = this
      let { sections, ruleFieldIds } = form
      if (!isEmpty(ruleFieldIds)) {
        this.$set(this, 'ruleFieldIds', ruleFieldIds)
      }
      let filteredSections = (sections || []).filter(section => {
        let { fields, subFormId } = section

        let filteredFields = (fields || []).filter(field => {
          if (!isEmpty(removeFieldsHash)) {
            return !(removeFieldsHash || []).includes(field.name)
          } else if (field.name === 'photo') {
            return field.hideField !== true
          }
          return true
        })
        section.fields = filteredFields
        return !isEmpty(subFormId) || !isEmpty(filteredFields)
      })
      let deserializedSections = await Promise.all(
        filteredSections.map(async section => {
          let { fields, subFormId, subForm, subFormValue } = section
          let subFormsArr = []
          if (!isEmpty(subFormId)) {
            if (isEdit) {
              let moduleName = this.$getProperty(form, 'module.name')
              let records = await this.loadSubFormRecords(
                section,
                moduleDataId,
                moduleName
              )
              subFormValue = { data: records }
            }
            subFormsArr = this.constructSubFormValues({
              subForm,
              subFormValue,
              isEdit,
              ruleFieldIds,
            })
            this.$set(section, 'subFormsArr', subFormsArr)
          }
          if (!isEmpty(fields)) {
            this.$set(section, 'fields', this.deserializeData(fields))
          }

          if (!isEmpty(section)) this.$set(section, 'hideSection', false)

          if (
            !isEmpty(modifySectionPropsHook) &&
            isFunction(modifySectionPropsHook)
          ) {
            let modifiedSection = modifySectionPropsHook(section)
            if (!isEmpty(modifiedSection))
              section = { ...section, ...modifiedSection }
          }
          return section
        })
      )
      if (isLiveSubForm) this.onChangeHandler({ field: {}, isSubForm: true })
      else if (!isSubFormPresent) this.fetchOnDataActionsList()
      this.$set(form, 'sections', deserializedSections)
      this.isLoading = false
    },
    clearError(name) {
      this.$refs['form'].clearValidate(name)
    },
    cancel() {
      this.$emit('cancel')
    },
    saveRecord(props = {}) {
      let { skipSubFormValidation = false } = props
      let { form, $refs, isV3Api } = this
      let { sections } = form
      let subFormData = []
      let promises = []
      let subFormV3Data = {}
      let isSubSectionsValid = true
      if (!isEmpty(sections)) {
        sections.forEach((section, index) => {
          let { subForm, subFormId } = section
          let subFormElement = $refs[`${index} ${subFormId}`]
          if (!isEmpty(subForm) && !isEmpty(subFormElement)) {
            let subFormModuleName = this.$getProperty(
              section,
              'subForm.module.name'
            )
            promises.push(
              subFormElement[0]
                .saveRecord({ skipSubFormValidation })
                .then(dataObj => {
                  if (!isEmpty(dataObj)) {
                    if (isV3Api) {
                      if (isEmpty(subFormV3Data[subFormModuleName])) {
                        subFormV3Data = {
                          ...subFormV3Data,
                          ...dataObj,
                        }
                      } else {
                        subFormV3Data[subFormModuleName].push(dataObj)
                      }
                    } else {
                      let { data } = dataObj
                      if (!isEmpty(data)) {
                        subFormData.push(dataObj)
                      }
                    }
                  }
                })
                .catch(() => {
                  throw new Error()
                })
            )
          }
          if (
            ['QUOTE_LINE_ITEMS', 'LINEITEMS'].includes(
              this.$getProperty(section, ['fields', 0, 'displayTypeEnum'])
            )
          ) {
            let quoteForm = this.$refs['line-item']
            if (!isEmpty(quoteForm)) {
              isSubSectionsValid = quoteForm[0].validateForm()
            }
          }
          if (
            ['BUDGET_AMOUNT'].includes(
              this.$getProperty(section, ['fields', 0, 'displayTypeEnum'])
            )
          ) {
            let budgetForm = this.$refs['budget-amount']
            if (!isEmpty(budgetForm)) {
              isSubSectionsValid = budgetForm[0].validateForm()
            }
          }
        })
      }
      Promise.all(promises)
        .then(() => {
          this.$refs['form'].validate(valid => {
            let {
              hasError,
              formModel,
              module,
              form,
              canShowNotifyRequester,
            } = this
            if (hasError) {
              this.$message.error('Please specify the task subject')
            } else if (isSubSectionsValid && valid) {
              let { id } = form || {}
              let _formModel = cloneDeep(formModel)
              if (!isEmpty(id)) {
                _formModel.formId = id
              }
              if (!isEmpty(subFormV3Data)) {
                _formModel.subFormData = { relations: subFormV3Data }
              } else if (!isEmpty(subFormData)) {
                _formModel.subFormData = subFormData
              }
              let serializedFormData = _formModel
              if (canShowNotifyRequester) {
                serializedFormData.notifyRequester = formModel.notifyRequester
              }
              this.$emit('save', serializedFormData)
            }
          })
        })
        .catch(err => err)
    },
    // TODO: Have to remove this once rules feature is done
    isMvSection(field) {
      let { formModel } = this
      let { formulaFieldHack, displayTypeEnum } = field
      if (formulaFieldHack && displayTypeEnum !== 'RADIO_TEMP') {
        if (displayTypeEnum === 'FORMULA_FIELD_TEMP') {
          return formModel['saveGoalType'] === 0
        }
        return formModel['saveGoalType'] === 1
      }
      return !!formulaFieldHack
    },
    handleResourceSelectAttribute() {
      /*
       * Handling remote select for mobile. el-select doesn't hanldle this
       * Refer https://github.com/ElemeFE/element/issues/12563
       */
      if (this.$refs.resourceSelect) {
        this.$refs.resourceSelect.forEach(node => {
          let child = node.$el.querySelector('.el-input input.el-input__inner')
          if (child) {
            child.removeAttribute('readonly')
          }
        })
      }
    },
    showLookupWizard(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      let lookupModuleName = this.$getProperty(field, 'field.lookupModule.name')
      let showNewResourceWizard = this.$helpers.isLicenseEnabled(
        'WIZARD_RESOURCE_FILTER'
      )
      if (lookupModuleName === 'resource' && showNewResourceWizard) {
        this.$set(this, 'showResourceWizard', canShow)
      } else {
        this.$set(this, 'canShowLookupWizard', canShow)
      }
    },
    setLookupFieldValue(props) {
      let { selectedLookupField } = this
      let { field } = props
      if (isEmpty(selectedLookupField)) this.selectedLookupField = field

      let { selectedItems, name: fieldName, options = [], multiple } =
        field || {}
      let [selectedItem] = selectedItems || []
      let selectedItemIds = []

      if (!isEmpty(selectedItems)) {
        selectedItemIds = selectedItems.map(item => item.value)
        if (!isEmpty(options)) {
          let ids = options.map(item => item.value)
          let newOptions = selectedItems.filter(
            item => !ids.includes(item.value)
          )
          options.unshift(...newOptions)
        } else {
          options = [...selectedItems]
        }
      }

      this.$set(this.selectedLookupField, 'options', options)

      if (multiple) {
        this.$set(this.formModel, `${fieldName}`, selectedItemIds)
      } else {
        this.$set(this.formModel[fieldName], 'id', selectedItemIds[0])
      }

      if (fieldName === 'resource') {
        this.onRecordSelected({ value: selectedItem, field })
      } else {
        this.onChangeHandler({ field })
      }
    },
    updateDurationValue(field, value) {
      let { name } = field
      this.onChangeHandler({ field })
      this.$set(this.formModel, name, value)
    },
    canShowMetric(field) {
      let { field: fieldObj } = field
      let { metric, unit } = fieldObj

      return !isEmpty(metric) && !isEmpty(unit)
    },
    metricSymbolPosition(field) {
      let { field: fieldObj } = field
      let { metricEnum } = fieldObj || {}

      if (metricEnum === 'CURRENCY') return 'prepend'
      else return 'append'
    },
    // Adding new option for picklist fields in live form
    addOption(field, sectionIndex, fieldIndex) {
      let {
        id,
        fieldId,
        displayName,
        options = [],
        name,
        field: fieldObj,
      } = field
      let { form } = this
      let { sections } = form
      let { dataType, values } = fieldObj || {}
      let selectElement = this.$refs[`${fieldIndex} ${id}`] || []
      if (!isEmpty(selectElement[0])) {
        let { query } = selectElement[0]
        if (!isEmpty(query)) {
          let clonedOptions = [...options]
          let newOption = {
            label: query,
            visible: true,
            value: values.length + 1,
          }
          clonedOptions.push(newOption)
          this.$set(field, 'isOptionsLoading', true)
          let url = 'v2/modules/fields/update'
          let data = {
            fieldJson: {
              fieldId,
              dataType,
              displayName,
              values: clonedOptions.map(option => {
                let clonedOption = deepCloneObject(option)
                clonedOption.index = option.value
                clonedOption.value = option.label
                delete clonedOption.label
                return clonedOption
              }),
            },
          }
          let promise = this.$http
            .post(url, data)
            .then(({ data: { message, responseCode, result } }) => {
              if (responseCode === 0) {
                let { field } = result
                let { values } = field || {}
                if (!isEmpty(values)) {
                  let deserializedOptions = constructEnumFieldOptions(values)
                  let recentlyAddedOption = values.find(
                    value => value.index === newOption.value
                  )
                  // Focus the newly created option
                  this.$nextTick(() => {
                    if (!isEmpty(selectElement[0])) {
                      selectElement[0].blur()
                    }
                  })
                  this.$set(
                    sections[sectionIndex].fields[fieldIndex],
                    'options',
                    deserializedOptions
                  )
                  let isMultiple = this.isMultiple({ field })
                  if (isMultiple) {
                    this.$set(
                      this.formModel[name],
                      '0',
                      recentlyAddedOption.index
                    )
                  } else {
                    this.$set(
                      this.formModel[name],
                      'id',
                      recentlyAddedOption.index
                    )
                  }
                }
              } else {
                throw new Error(message)
              }
            })
            .catch(({ message }) => {
              this.$message.error(message)
            })
          Promise.all([promise]).finally(() => {
            this.$set(field, 'isOptionsLoading', false)
          })
        }
      }
    },
    isSubForm(section) {
      let isSubFormPresent =
        !isEmpty(section.subForm) &&
        section.sectionType === 2 &&
        !isEmpty(section.subFormsArr)

      return isSubFormPresent
    },
    showSubform(section) {
      let { isV3Api, isEdit } = this
      let { isSubForm } = this
      let isSubFormPresent = isSubForm(section)
      if (isV3Api) {
        return isSubFormPresent
      } else {
        return !isEdit && isSubFormPresent
      }
    },
    isLookupSimpleOrChooserType(field) {
      return !isEmpty(field)
        ? isLookupSimple(field) || this.$fieldUtils.isChooserTypeField(field)
        : false
    },
    getTasksList() {
      return this.tasksList
    },
    updateAttachment(attachments) {
      this.$set(this, 'attachments', attachments)
    },
    updateFormModel(formModel, field) {
      this.$set(this, 'formModel', formModel)
      if (!isEmpty(field)) {
        this.onChangeHandler({ field })
      }
    },
  },
}
</script>
