<template>
  <div v-if="isFieldPropertiesLoading" class="p10">
    <el-row v-for="n in 2" :key="n" :gutter="20" class="mB20">
      <el-col :span="12">
        <div class="fc-animated-background"></div>
      </el-col>
      <el-col :span="12">
        <div class="fc-animated-background"></div>
      </el-col>
    </el-row>
  </div>
  <div
    v-else-if="showFormProperties"
    class="pL30 pR30 pT10 pB30 overflow-scroll"
  >
    <!-- Form properties section -->
    <div class="fc-text-pink mB15 field-title">{{ title }}</div>
    <el-row class="mB20" :class="hasError ? 'error' : ''">
      <el-col :span="24">
        <div class="fc-dark-grey-txt13">Title</div>
        <el-input
          class="fc-input-full-border2 mT10"
          v-model="formProperties.displayName"
          @change.native="onFormInputChange()"
          @input="onLabelChange(...arguments)"
        ></el-input>
        <span v-if="labelErrMsg" class="mL0 error-text">{{ labelErrMsg }}</span>
      </el-col>
    </el-row>
    <el-row class="mB20">
      <el-col :span="24">
        <div class="fc-dark-grey-txt13">Description</div>
        <el-input
          type="textarea"
          v-model="formProperties.description"
          @change.native="onFormInputChange()"
          @input="onInputChange()"
          :autosize="{ minRows: 2, maxRows: 4 }"
          resize="none"
          class="fc-input-txt fc-desc-input fc-input-full-border-select2 mT10 mB0"
        ></el-input>
      </el-col>
    </el-row>
    <FormAlignmentSection
      :labelPosition="formProperties.labelPosition"
      @updatePosition="updatePosition"
    ></FormAlignmentSection>
    <el-row class="mB20">
      <el-col :span="24">
        <div class="fc-dark-grey-txt13">Primary Button</div>
        <el-input
          v-model="formProperties.primaryBtnLabel"
          @change.native="onFormInputChange()"
          @input="onInputChange()"
          class="fc-input-full-border2 mT10 fc-input-disabled"
          :disabled="true"
        ></el-input>
      </el-col>
    </el-row>
    <el-row class="mB20">
      <el-col :span="24">
        <div class="fc-dark-grey-txt13">Secondary Button</div>
        <el-input
          v-model="formProperties.secondaryBtnLabel"
          @change.native="onFormInputChange()"
          @input="onInputChange()"
          class="fc-input-full-border2 mT10 fc-input-disabled"
          :disabled="true"
        ></el-input>
      </el-col>
    </el-row>
  </div>
  <div
    v-else-if="showSectionProperties"
    class="pL30 pR30 pT10 pB30 overflow-scroll"
  >
    <!-- Section properties -->
    <div class="fc-text-pink mB15 field-title">{{ title }}</div>
    <el-row class="mB20" :class="hasError ? 'error' : ''">
      <el-col :span="24">
        <div class="fc-dark-grey-txt13">Label</div>
        <el-input
          class="fc-input-full-border2 mT10"
          v-model="section.name"
          @input="onLabelChange(...arguments)"
          @change.native="onSectionInputChange()"
        ></el-input>
        <span v-if="labelErrMsg" class="mL0 error-text">{{ labelErrMsg }}</span>
      </el-col>
    </el-row>
    <el-row class="mB20">
      <el-col :span="24">
        <el-checkbox v-model="section.showLabel"
          >Show header for this section</el-checkbox
        >
      </el-col>
    </el-row>
    <el-row v-if="isSubFormSection" class="mB20">
      <div>
        <div class="details-Heading">Set default values</div>
        <el-button
          type="button"
          :class="
            isDefaultConfigured ? 'success-green-btn' : 'small-border-btn'
          "
          class="mT10"
          @click="openConfigurationDialog(false)"
          >{{ isDefaultConfigured ? 'Configured' : 'Configure' }}</el-button
        >
        <span v-if="isDefaultConfigured" class="mL10">
          <i
            class="el-icon-edit pointer"
            @click="openConfigurationDialog(true)"
            title="Edit default values"
            v-tippy
          ></i>
          <span
            class="mL10 reset-txt-color f12 letter-spacing0_5 pointer"
            @click="resetDefaultValues()"
            >Reset</span
          >
        </span>
      </div>
    </el-row>
  </div>
  <!-- eslint-disable-next-line vue/valid-template-root -->
  <div v-else class="pL30 pR30 pT10 pB30 overflow-scroll field-prop-section">
    <!-- Field properties section -->
    <div class="fc-text-pink mB15 field-title">{{ title }}</div>
    <el-row class="mB20" :class="hasError ? 'error' : ''">
      <el-col :span="24">
        <div class="fc-dark-grey-txt13">
          {{ $t('fields.properties.field_label') }}
        </div>
        <el-input
          class="fc-input-full-border2 mT10"
          v-model="field.displayName"
          @input="onLabelChange(...arguments)"
          @change.native="onFieldInputChange()"
        ></el-input>
        <span v-if="labelErrMsg" class="mL0 error-text">{{ labelErrMsg }}</span>
      </el-col> </el-row
    ><el-row class="mB20">
      <el-col :span="24">
        <div class="fc-dark-grey-txt13">
          {{ $t('fields.properties.link_name') }}
        </div>
        <el-input
          class="fc-input-full-border2 mT10 fc-input-disabled"
          v-model="field.name"
          :disabled="true"
        ></el-input>
      </el-col>
    </el-row>
    <el-row v-if="showHideMand(field.displayTypeEnum)" class="mB20">
      <el-col :span="24">
        <el-checkbox
          v-model="field.required"
          :disabled="canDisableMand(field.name) && !isCustomModule"
          >Mandatory</el-checkbox
        >
      </el-col>
    </el-row>

    <template v-if="isLookup(field)">
      <el-row class="mB20">
        <el-col :span="24">
          <div class="fc-dark-grey-txt13 mB10">Lookup Module</div>
          <el-select
            placeholder="Select"
            v-model="field.field.lookupModuleId"
            class="fc-input-full-border-select2 width100 fc-input-disabled"
            :disabled="true"
          >
            <el-option
              v-for="module in moduleList"
              :key="(module || {}).name"
              :label="(module || {}).displayName"
              :value="(module || {}).moduleId"
            ></el-option>
          </el-select>
        </el-col>
      </el-row>
      <el-row class="mB20">
        <el-col :span="24">
          <div class="fc-dark-grey-txt13 mB10">
            {{ $t('fields.properties.display_type') }}
          </div>
          <el-select
            :placeholder="$t('fields.properties.display_type_placeholders')"
            v-model="field.displayType"
            class="fc-input-full-border-select2 width100"
            disabled
          >
            <el-option
              :label="$t('fields.properties.single_select')"
              :value="10"
            ></el-option>
            <el-option
              :label="$t('fields.properties.multiple_select')"
              :value="66"
            ></el-option>
          </el-select>
        </el-col>
      </el-row>
      <el-row
        v-if="canShowRelatedListTitle(field)"
        class="mB20"
        :class="[hasError && 'error']"
      >
        <el-col :span="24">
          <div class="fc-dark-grey-txt13">
            {{ $t('common._common.enter_related_list_label') }}
          </div>
          <el-input
            :placeholder="$t('common._common.enter_name')"
            class="fc-input-full-border2 mT10"
            v-model="field.field.relatedListDisplayName"
            @change.native="onFieldInputChange()"
          ></el-input>
          <span v-if="labelErrMsg" class="mL0 error-text">{{
            labelErrMsg
          }}</span>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24" class="mB10">
          <el-checkbox v-model="(field.config || {}).canShowLookupWizard">{{
            $t('fields.properties.show_lookup_wizard')
          }}</el-checkbox>
        </el-col>
        <el-col :span="24" class="mB10">
          <el-checkbox v-model="(field.config || {}).canShowQuickCreate">{{
            $t('fields.properties.show_quick_create')
          }}</el-checkbox>
        </el-col>
        <el-col :span="24" class="mB10">
          <el-checkbox
            v-if="field.showFloorPlanConfig"
            v-model="(field.config || {}).canShowFloorPlanPicker"
            >{{ $t('fields.properties.show_floorpicker') }}</el-checkbox
          >
        </el-col>
      </el-row>
    </template>
    <el-row v-if="isDisplayTypeFieldsHash(field)" class="mB20">
      <el-col :span="24">
        <div class="fc-dark-grey-txt13 mB10">Display Type</div>
        <el-select
          @change="onFieldTypeChange(...arguments)"
          v-model="field.displayType"
          placeholder="Select"
          class="fc-input-full-border-select2 width100"
        >
          <el-option
            v-for="option in $constants.FIELD_TYPE_OPTIONS"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row
      v-if="!isUpdateForm && !$validation.isEmpty(placeHolderMap)"
      class="mB20"
    >
      <el-col :span="24">
        <el-checkbox v-model="enablePlaceHolder" @change="handlePlaceHolder">{{
          placeHolderMap.label
        }}</el-checkbox>
      </el-col>
    </el-row>
    <el-row v-if="!enablePlaceHolder" class="mB10">
      <el-col v-if="field.displayTypeEnum === 'SELECTBOX'" :span="24">
        <div v-if="!hideDefaultValue" class="fc-dark-grey-txt13 mB10">
          {{ defaultValueLabel }}
        </div>
        <el-checkbox-group v-if="isMultiple" v-model="field.value">
          <div v-for="(option, index) in field.options" :key="index">
            <div
              v-if="option.visible"
              class="field-option mB15"
              :class="isOptionInvalid(index) ? 'error' : ''"
            >
              <div class="d-flex">
                <el-checkbox
                  :label="option.value"
                  class="fc-radio-btn mR0"
                  :disabled="isUpdateForm"
                >
                  <el-input
                    v-model="option.label"
                    class="fc-input-full-border2 width-auto"
                    :ref="`input${index}`"
                    @input="onInputChange()"
                    @change.native="onOptionsInputChange(index, option)"
                    @blur="onInputBlur(index, option)"
                    :maxlength="100"
                  ></el-input>
                </el-checkbox>
                <img
                  v-if="canShowAddIcon"
                  src="~assets/add-icon.svg"
                  class="mT10 mL5 add-icon"
                  @mousedown="addOption(index)"
                />
                <img
                  v-if="canShowRemoveIcon(index)"
                  src="~assets/remove-icon.svg"
                  class="mT10 mL5 remove-icon"
                  @mousedown="removeOption(option, index)"
                />
              </div>
              <div v-if="isOptionInvalid(index)" class="error-text">
                {{ optionsErrObj.msg }}
              </div>
            </div>
          </div>
        </el-checkbox-group>
        <el-radio-group v-else v-model="field.value">
          <div v-for="(option, index) in field.options" :key="index">
            <div
              v-if="option.visible"
              class="field-option mB15"
              :class="isOptionInvalid(index) ? 'error' : ''"
            >
              <div class="d-flex">
                <el-radio
                  :label="option.value"
                  class="fc-radio-btn mR0"
                  :disabled="isUpdateForm"
                >
                  <el-input
                    v-model="option.label"
                    class="fc-input-full-border2 width-auto"
                    :ref="`input${index}`"
                    @input="onInputChange()"
                    @change.native="onOptionsInputChange(index, option)"
                    @blur="onInputBlur(index, option)"
                    :maxlength="100"
                  ></el-input>
                </el-radio>
                <img
                  v-if="canShowAddIcon"
                  src="~assets/add-icon.svg"
                  class="mT10 mL5 add-icon"
                  @mousedown="addOption(index)"
                />
                <img
                  v-if="canShowRemoveIcon(index)"
                  src="~assets/remove-icon.svg"
                  class="mT10 mL5 remove-icon"
                  @mousedown="removeOption(option, index)"
                />
              </div>
              <div v-if="isOptionInvalid(index)" class="error-text">
                {{ optionsErrObj.msg }}
              </div>
            </div>
          </div>
        </el-radio-group>
      </el-col>
      <el-radio-group
        v-model="field.value"
        v-else-if="field.displayTypeEnum === 'RADIO'"
      >
        <el-radio
          :label="true"
          :disabled="isUpdateForm"
          class="fc-radio-btn display-block mB15"
        >
          <el-input
            v-model="field.field.trueVal"
            class="fc-input-full-border2 width-auto"
            @input="onInputChange()"
            @change.native="onFieldInputChange()"
          ></el-input>
          <span class="mL5 text-lowercase">(+ve)</span>
        </el-radio>
        <el-radio
          :label="false"
          :disabled="isUpdateForm"
          class="fc-radio-btn display-block mB15 mL0"
        >
          <el-input
            v-model="field.field.falseVal"
            class="fc-input-full-border2 width-auto"
            @input="onInputChange()"
            @change.native="onFieldInputChange()"
          ></el-input>
          <span class="mL5 text-lowercase">(-ve)</span>
        </el-radio>
      </el-radio-group>
      <el-col v-else-if="!isUpdateForm" :span="24">
        <div v-if="!hideDefaultValue" class="fc-dark-grey-txt13 mB10">
          {{ defaultValueLabel }}
        </div>
        <template v-if="canShowConfigureBtn(field)">
          <el-button
            v-bind:class="
              !$validation.isEmpty(field.value)
                ? 'configured-button'
                : 'small-border-btn'
            "
            @click="
              $validation.isEmpty(field.value)
                ? canShowDefaultValueDialog()
                : null
            "
          >
            <span
              v-if="!$validation.isEmpty(field.value)"
              class="el-icon-check mR5 fwBold"
            ></span>
            {{
              !$validation.isEmpty(field.value)
                ? 'Default Value Configured'
                : 'Configure'
            }}
          </el-button>

          <span v-if="!$validation.isEmpty(field.value)" class="mL15">
            <span @click="canShowDefaultValueDialog()" class="pointer">
              <i class="el-icon-edit mR5"></i> {{ $t('common._common.edit') }}
            </span>
            <span class="mL15 reset-txt pointer" @click="resetDefaultValue()"
              ><i class="el-icon-refresh-right mR5"></i
              >{{ $t('common._common.reset') }}</span
            >
          </span>
        </template>
        <template v-else>
          <el-input
            v-if="field.displayTypeEnum === 'TEXTAREA'"
            type="textarea"
            :autosize="{ minRows: 4, maxRows: 6 }"
            v-model="field.value"
            @input="onInputChange()"
            @change.native="onFieldInputChange()"
            :placeholder="`Type your  ${field.displayName}`"
            class="fc-input-full-border-textarea"
          ></el-input>
          <FSiteField
            v-else-if="isSiteField(field)"
            :model.sync="field.value"
            :filter.sync="filterObj"
            :isBuilder="true"
            :isClearable="true"
          ></FSiteField>
          <el-select
            v-else-if="isDropdownTypeFields(field)"
            v-model="field.value"
            filterable
            :clearable="!field.required"
            placeholder="Select"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="(option, index) in field.options"
              :key="index"
              :label="option.label"
              :value="option.value"
            ></el-option>
          </el-select>
          <el-select
            v-else-if="field.displayTypeEnum === 'TASKS'"
            v-model="field.value"
            filterable
            placeholder="Select"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="jobPlan in jobPlansList"
              :key="jobPlan.id"
              :label="jobPlan.name"
              :value="jobPlan.id"
            ></el-option>
          </el-select>
          <el-checkbox
            v-model="field.value"
            v-else-if="field.displayTypeEnum === 'DECISION_BOX'"
            >{{ field.displayName }}</el-checkbox
          >
          <el-select
            v-else-if="
              isResourceTypeFields(field) &&
                (field.config || {}).isFiltersEnabled
            "
            v-model="field.value"
            filterable
            placeholder="Select"
            class="fc-input-full-border-select2 width100"
            :disabled="(field.config || {}).isFiltersEnabled"
          >
            <el-option
              v-for="(option, index) in field.options"
              :key="index"
              :label="option.label"
              :value="option.value"
            ></el-option>
          </el-select>
          <FLookupField
            v-else-if="
              $fieldUtils.isChooserTypeField(field) &&
                field.displayTypeEnum !== 'SPACECHOOSER' &&
                !hideDefaultValue
            "
            :key="`${field.id}`"
            :model.sync="(field.value || {}).id"
            :field="field"
            :disabled="field.isDisabled"
            :hideDropDown="
              $fieldUtils.isChooserTypeField(field) &&
                !(field.config || {}).isFiltersEnabled &&
                field.displayTypeEnum !== 'SPACECHOOSER'
            "
            :siteId="siteId"
            @showLookupWizard="showLookupWizard"
            @recordSelected="onRecordSelected"
          ></FLookupField>
          <div
            v-else-if="field.displayTypeEnum === 'TEAMSTAFFASSIGNMENT'"
            class="fc-input-full-border-select2"
          >
            <div class="fc-border-input-div el-form-item__content">
              <span>{{ getTeamStaffLabel(field.value) }}</span>
              <span style="float: right">
                <img
                  class="svg-icon team-down-icon"
                  src="~assets/down-arrow.svg"
                />
              </span>
            </div>
            <FAssignment
              :model="field.value"
              :siteId="siteId"
              viewtype="form"
            ></FAssignment>
          </div>
          <FDatePicker
            v-else-if="field.displayTypeEnum === 'DATE'"
            v-model="field.value"
            :key="field.name"
            :type="'date'"
            class="fc-input-full-border2 form-date-picker"
          ></FDatePicker>
          <FDatePicker
            v-else-if="field.displayTypeEnum === 'DATETIME'"
            v-model="field.value"
            :key="field.name"
            :type="'datetime'"
            class="fc-input-full-border2 form-date-picker"
          ></FDatePicker>
          <el-input
            v-else-if="
              field.displayTypeEnum === 'NUMBER' ||
                field.displayTypeEnum === 'DECIMAL'
            "
            type="number"
            v-model="field.value"
            @input="onInputChange()"
            @change.native="onFieldInputChange()"
            class="fc-input-full-border2"
            placeholder="Enter a default value"
          ></el-input>
          <div v-else-if="field.displayTypeEnum === 'DURATION'">
            <f-duration-field
              :key="`prop-${field.id}`"
              :field="field"
            ></f-duration-field>
          </div>

          <FTimePicker
            v-else-if="field.displayTypeEnum === 'TIME'"
            v-model="field.value"
            :key="`${field.name}`"
            :field="field"
            :placeholder="$t('fields.properties.time_picker_placeholder')"
            class="el-select fc-input-full-border-select2 width100"
          ></FTimePicker>
          <FLookupField
            v-else-if="isLookupTypeField"
            :key="`lookup-${field.id}`"
            :model.sync="field.value"
            :field="field"
            :disabled="field.isDisabled"
            :siteId="siteId"
            @showLookupWizard="showLookupWizard"
            @recordSelected="onRecordSelected"
          />
          <FCurrencyField
            v-else-if="field.displayTypeEnum === 'CURRENCY'"
            v-model="field.value"
            :key="`${field.name}`"
            :disabled="false"
            :field="field"
          ></FCurrencyField>
          <FNewCurrencyField
            v-else-if="field.displayTypeEnum === 'MULTI_CURRENCY'"
            v-model="field.value"
            :key="`multicurrency-${field.currencyCode}`"
            :disabled="false"
            :field="field"
            :hideCurrency="true"
            :hideDesc="true"
          ></FNewCurrencyField>
          <FLocationField
            v-else-if="field.displayTypeEnum === 'GEO_LOCATION'"
            v-model="field.value"
            :key="`location-${field.name}`"
            :field="field"
            :disabled="false"
            :clearable="true"
            @locationUpdated="locationUpdated"
          ></FLocationField>
          <el-input
            v-else-if="!hideDefaultValue"
            class="fc-input-full-border2"
            v-model="field.value"
            @input="onInputChange()"
            @change.native="onFieldInputChange()"
            placeholder="Enter a default value"
          ></el-input>
        </template>
      </el-col>
    </el-row>
    <template v-if="field.displayTypeEnum === 'URL_FIELD'">
      <el-row class="mB20">
        <el-col :span="24">
          <div class="fc-dark-grey-txt13 mB10">
            {{ $t('fields.properties.target') }}
          </div>
          <el-radio-group v-model="(field.field || {}).target">
            <el-radio
              v-for="(option, index) in urlTargetOptions"
              :key="index"
              :label="option.value"
              class="fc-radio-btn display-block mB10"
            >
              {{ option.label }}
            </el-radio>
          </el-radio-group>
        </el-col>
      </el-row>
    </template>
    <el-row class="mB20" v-if="isNotesField">
      <el-col :span="24">
        <div class="fc-dark-grey-txt13 mB10">
          {{ $t('asset.assets.notes') }}
        </div>
        <el-input
          type="textarea"
          v-model="field.value"
          @click.native="openRichTextArea()"
          :autosize="{ minRows: 2, maxRows: 4 }"
          resize="none"
          class="fc-input-txt fc-desc-input fc-input-full-border-select2 mT10 mB0"
        ></el-input>
      </el-col>
    </el-row>
    <el-row
      class="mB20"
      v-if="handleNumberFieldMetric || field.displayTypeEnum === 'DECIMAL'"
    >
      <div class="fc-dark-grey-txt13 mB10">Metric</div>
      <el-col :span="24">
        <el-select
          v-model="field.metric"
          filterable
          placeholder="Select"
          class="fc-input-full-border-select2 width100"
          @change="resetUnitField"
          :loading="isMetricLoading"
        >
          <el-option
            v-for="option in metricsList"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row
      class="mB20"
      v-if="handleNumberFieldMetric || field.displayTypeEnum === 'DECIMAL'"
    >
      <div class="fc-dark-grey-txt13 mB10">Unit</div>
      <el-col :span="24">
        <el-select
          v-model="field.unitId"
          filterable
          placeholder="Select"
          class="fc-input-full-border-select2 width100"
        >
          <el-option
            v-for="option in unitOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row v-if="!isNotesField" class="mT20 mB10">
      <div class="fc-dark-grey-txt13 mB10">
        {{ $t('fields.properties.choices') }}
      </div>
      <el-col :span="24" class="mB10">
        <el-checkbox
          v-if="showAddOptionsCheckbox(field.displayTypeEnum)"
          v-model="field.allowCreate"
          >{{ $t('fields.properties.allow_other_choices') }}</el-checkbox
        >
      </el-col>
      <el-col :span="24" class="mB10">
        <el-checkbox
          v-model="field.hideField"
          :disabled="!canShowHideField"
          @change="onHideChange"
          >{{ $t('fields.properties.hideFieldTxt') }}</el-checkbox
        >
      </el-col>
      <el-col :span="24" class="mB10">
        <el-checkbox v-model="field.isDisabled" :disabled="!canShowHideField">{{
          $t('fields.properties.disabledFieldTxt')
        }}</el-checkbox>
      </el-col>
      <el-col :span="24">
        <el-checkbox
          v-if="field.displayTypeEnum === 'SIGNATURE'"
          v-model="(field.config || {}).canShowColorPalette"
          >{{ $t('fields.properties.enable_colorpalette') }}</el-checkbox
        >
      </el-col>
      <el-col :span="24">
        <el-checkbox
          v-if="canShowSkipSiteFilter(field)"
          v-model="(field.config || {}).skipSiteFilter"
          @change="setSkipSiteFilter"
        >
          {{ $t('fields.properties.skip_site_filter') }}
        </el-checkbox>
      </el-col>
      <el-col :span="24">
        <el-checkbox
          v-if="field.displayTypeEnum === 'URL_FIELD'"
          v-model="(field.config || {}).showName"
          @change="setShowName"
        >
          {{ $t('fields.properties.show_link_name') }}
        </el-checkbox>
      </el-col>
      <el-col :span="24">
        <el-checkbox
          v-if="
            ['LINEITEMS', 'QUOTE_LINE_ITEMS'].includes(field.displayTypeEnum)
          "
          v-model="(field.config || {}).hideTaxField"
          @change="setHideTaxField"
        >
          {{ $t('fields.properties.hide_tax_field') }}
        </el-checkbox>
      </el-col>
    </el-row>
    <el-row class="mT10 mB20" v-if="canShowLookupFilterOption(field)">
      <el-checkbox
        v-model="(field.config || {}).isFiltersEnabled"
        @change="onCheckFilters"
        >{{ $t('fields.properties.enable_filters') }}</el-checkbox
      >
      <div class="mT20" v-if="(field.config || {}).isFiltersEnabled">
        <el-radio-group
          v-if="lookupFilterOptionArr.length === 2"
          v-model="(field.config || {}).filterValue"
        >
          <el-radio
            v-for="(option, index) in lookupFilterOptionArr"
            :key="index"
            :label="option.value"
            class="fc-radio-btn display-block mB10"
          >
            {{ option.label }}
          </el-radio>
        </el-radio-group>
        <el-select
          v-else
          v-model="(field.config || {}).filterValue"
          filterable
          placeholder="Select"
          class="fc-input-full-border-select2 width100"
          @change="onFilterCheckChange"
        >
          <el-option
            v-for="option in lookupFilterOptionArr"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          ></el-option>
        </el-select>
      </div>
    </el-row>
    <el-row class="mT10 mB20" v-if="canShowCriteriaBuilderForLookup">
      <div class="fc-dark-grey-txt13 mB10">
        {{ $t('fields.properties.set_filter') }}
      </div>

      <el-button
        v-bind:class="
          !$validation.isEmpty(formRulesMap) &&
          !$validation.isEmpty(formRulesMap[field.id])
            ? 'configured-button'
            : 'small-border-btn'
        "
        @click="
          $validation.isEmpty(formRulesMap) ||
          $validation.isEmpty(formRulesMap[field.id])
            ? showCriteriaForField(field)
            : null
        "
      >
        <span
          v-if="
            !$validation.isEmpty(formRulesMap) &&
              !$validation.isEmpty(formRulesMap[field.id])
          "
          class="el-icon-check mR5 fwBold"
        ></span>
        {{
          !$validation.isEmpty(formRulesMap) &&
          !$validation.isEmpty(formRulesMap[field.id])
            ? 'Criteria Configured'
            : 'Configure'
        }}
      </el-button>

      <span
        v-if="
          !$validation.isEmpty(formRulesMap) &&
            !$validation.isEmpty(formRulesMap[field.id])
        "
        class="mL15"
      >
        <span @click="showCriteriaForField(field)" class="pointer">
          <i class="el-icon-edit mR5"></i>Edit
        </span>
        <span class="mL15 reset-txt pointer" @click="resetRule()"
          ><i class="el-icon-refresh-right mR5"></i>Reset</span
        >
      </span>

      <FormFieldsCriteriaBuilder
        v-if="openCriteriaForRule"
        :formId="formId"
        :subFormId="subFormId"
        :moduleName="moduleName"
        :isSubForm="isSubFormField"
        :visibility.sync="openCriteriaForRule"
        :lookupFieldsList.sync="fieldsList"
        :existingRule="
          !$validation.isEmpty(formRulesMap) &&
          !$validation.isEmpty(formRulesMap[field.id])
            ? formRulesMap[field.id]
            : null
        "
        :fieldObject.sync="field"
        @saved="afterSave"
      >
      </FormFieldsCriteriaBuilder>
    </el-row>
    <div v-if="canShowLookupWizard">
      <FLookupFieldWizard
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="selectedLookupField"
        :siteId="siteId"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
    </div>
    <div v-if="canShowRichText">
      <FormNotes
        :canShowRichText.sync="canShowRichText"
        :field="field"
      ></FormNotes>
    </div>

    <DefaultValueSelector
      v-if="showDefaultValueDialog"
      :showDefaultValueDialog="showDefaultValueDialog"
      :selectedField="field"
      @saveDefaultValue="saveDefaultValue"
      @closeDefaultVaueDialog="closeDefaultVaueDialog"
    />
  </div>
</template>

<script>
import FormAlignmentSection from '@/form-builder/FormAlignmentSection'
import FormFieldsCriteriaBuilder from '@/form-builder/FormFieldsCriteriaBuilder'
import DefaultValueSelector from '@/form-builder/DefaultValueSelector'
import FormNotes from '@/form-builder/FormNotes'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import FDurationField from '@/FDurationField'
import FSiteField from '@/FSiteField'
import FLookupField from '@/forms/FLookupField'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import FLocationField from '@/forms/FLocationField'
import { API } from '@facilio/api'
import { isEmpty, isObject, areValuesEmpty } from '@facilio/utils/validation'
import {
  cloneArray,
  deepCloneObject,
  cloneObject,
  checkDuplicateInObject,
} from 'util/utility-methods'
import TeamStaffMixin from '@/mixins/TeamStaffMixin'
import FAssignment from '@/FAssignment'
import $helpers from 'util/helpers'
import http from 'util/http'
import { mapGetters, mapActions, mapState } from 'vuex'
import { serializeProps } from '@facilio/utils/utility-methods'
import Constants from 'util/constant'
import { isLookupDropDownField, isChooserTypeField } from 'util/field-utils'
import { isLookupField, isMultiLookup, isSiteField } from '@facilio/utils/field'
import {
  constructFieldOptions,
  constructEnumFieldOptions,
} from '@facilio/utils/utility-methods'
import FTimePicker from '@/FTimePicker'
import max from 'lodash/max'
import FCurrencyField from 'src/components/FCurrencyField.vue'
import FNewCurrencyField from 'src/components/FNewCurrencyField.vue'

const dropDownTypeHash = ['SELECTBOX', 'URGENCY']
const hideDefaultValueFieldsHash = [
  'IMAGE',
  'SIGNATURE',
  'PERMIT_CHECKLIST',
  'QUOTE_LINE_ITEMS',
  'QUOTE_ADDRESS',
  'ADDRESS',
  'SADDRESS',
  'LINEITEMS',
  'NOTES',
  'BUDGET_AMOUNT',
  'FACILITY_AVAILABILITY',
  'FACILITY_BOOKING_SLOTS',
]
const hideMandatoryFieldsHash = ['TASKS', 'DECISION_BOX', 'IMAGE', 'NOTES']
const displayTypeFieldsHash = ['DECISION_BOX', 'RADIO']
const disabledMandFieldHash = ['siteId', 'subject']
const DELAY = 500

export default {
  components: {
    FormAlignmentSection,
    FLookupField,
    FLookupFieldWizard,
    FAssignment,
    FDatePicker,
    FDurationField,
    FSiteField,
    FormFieldsCriteriaBuilder,
    FormNotes,
    FTimePicker,
    DefaultValueSelector,
    FCurrencyField,
    FLocationField,
    FNewCurrencyField,
  },
  props: {
    formId: {
      type: Number,
      required: true,
    },
    field: {
      type: Object,
      required: true,
    },
    formRulesMap: {
      type: Object,
    },
    section: {
      type: Object,
      required: true,
    },
    formProperties: {
      type: Object,
      required: true,
    },
    formObj: {
      type: Object,
      required: true,
    },
    fieldsList: {
      type: Array,
    },
    jobPlansList: {
      type: Array,
    },
    allowUpdateFormProp: {
      type: Boolean,
      required: true,
    },
    allowUpdateProp: {
      type: Boolean,
      required: true,
    },
    isSaving: {
      type: Boolean,
      required: true,
    },
    isFieldPropertiesLoading: {
      type: Boolean,
      required: true,
    },
    hasError: {
      type: Boolean,
      required: true,
    },
    filter: {
      type: Object,
    },
    moduleList: {
      type: Array,
    },
    siteId: {
      type: Number,
    },
    spaceAssetResourceObj: {
      type: Object,
    },
    moduleName: {
      type: String,
    },
    currentSiteId: {
      type: Number,
    },
    isCustomModule: {
      type: Boolean,
    },
    isUpdateForm: {
      type: Boolean,
      default: () => false,
    },
  },
  mixins: [TeamStaffMixin],
  created() {
    this.$store.dispatch('loadGroups')
    this.isSiteField = isSiteField
  },
  mounted() {
    this.debounceFormUpdate = $helpers.debounce(() => {
      let { formProperties } = this
      let url = '/v2/forms/update'
      let data = {
        form: this.serializeFormData(formProperties),
      }
      this.updateProperties(url, data)
    }, DELAY)
    this.debounceFieldUpdate = $helpers.debounce(() => {
      let {
        field,
        field: {
          options = [],
          fieldId,
          displayTypeEnum,
          metric,
          unitId,
          displayName,
        },
        field: { field: fieldObj },
      } = this
      let filteredOptions = options.filter(option => {
        return option.visible
      })
      let isAnyOptionEmpty = (filteredOptions || []).some(option => {
        return isEmpty(option.label)
      })
      let isNotesField = displayTypeEnum === 'NOTES'
      if (!isAnyOptionEmpty) {
        let url = 'v2/forms/fields/update'
        let serializedField = this.serializeField(field)
        let data = {
          formField: serializedField,
        }
        this.updateProperties(url, data)
        if (!isEmpty(fieldObj) && !isNotesField) {
          let url = 'v2/modules/fields/update'
          let data = {
            fieldJson: {
              fieldId,
              dataType: fieldObj.dataType,
            },
          }
          if (!fieldObj.default) {
            // Update displayname only for custom fields
            data.fieldJson.displayName = displayName
          }
          if (fieldObj.relatedListDisplayName) {
            data.fieldJson.relatedListDisplayName =
              fieldObj.relatedListDisplayName
          }
          if (displayTypeEnum === 'SELECTBOX' && !isEmpty(options)) {
            let clonedOptions = cloneArray(options)
            clonedOptions = clonedOptions.map(option => {
              let clonedOption = deepCloneObject(option)
              clonedOption.index = option.value
              clonedOption.value = option.label
              delete clonedOption.label
              if (clonedOption.isNew) {
                delete clonedOption.isNew
              }
              return clonedOption
            })
            let nonVisibleOptions = (fieldObj.values || []).filter(value => {
              return !value.visible
            })
            clonedOptions = clonedOptions.concat(nonVisibleOptions)
            data.fieldJson.values = clonedOptions.map((option, index) => {
              option.sequence = index + 1
              return option
            })
          } else if (
            displayTypeEnum === 'NUMBER' ||
            displayTypeEnum === 'DECIMAL'
          ) {
            data.fieldJson.metric = metric || -99
            data.fieldJson.unitId = unitId || -99
          } else if (displayTypeEnum === 'RADIO') {
            data.fieldJson.trueVal = fieldObj.trueVal
            data.fieldJson.falseVal = fieldObj.falseVal
          } else if (displayTypeEnum === 'URL_FIELD') {
            data.fieldJson.target = fieldObj.target
          } else if (displayTypeEnum === 'MULTI_CURRENCY') {
            data.fieldJson.currencyCode = field.currencyCode
          }
          this.updateProperties(url, data)
        }
      }
    }, DELAY)
    this.debounceSectionUpdate = $helpers.debounce(() => {
      let url = '/v2/forms/sections/update'
      let resourceProps = Constants.SECTION_RESOURCE_PROPS
      let data = {
        formId: this.formProperties.id,
        formSection: serializeProps(this.section, resourceProps, true),
      }
      this.updateProperties(url, data)
    }, DELAY)
  },
  data() {
    return {
      newOptionIndex: null,
      openCriteriaForRule: false,
      debounceFormUpdate: null,
      debounceFieldUpdate: null,
      debounceSectionUpdate: null,
      optionsErrObj: {
        index: null,
        msg: '',
      },
      labelErrMsg: '',
      allowUpdateFieldProp: true,
      isInputFieldFocused: false,
      canShowLookupWizard: false,
      canShowQuickCreate: false,
      selectedLookupField: null,
      canShowRichText: false,
      enablePlaceHolder: false,
      showDefaultValueDialog: false,
      urlTargetOptions: [
        { label: this.$t('fields.properties.same_window'), value: '_self' },
        { label: this.$t('fields.properties.new_window'), value: '_blank' },
      ],
    }
  },
  watch: {
    formProperties: {
      handler() {
        if (this.allowUpdateFormProp && !this.isInputFieldFocused) {
          this.debounceFormUpdate()
        }
      },
      deep: true,
    },
    field: {
      /*
        * Have to skip update during the first render of all field properties.
        * Have to skip update while switching between fields.

        PS: Everytime newval and oldval will be same for the active field property even after changing
          refer: http://vuejs.org/api/#vm-watch
      */
      handler(newVal, oldVal) {
        if (!isEmpty(newVal)) {
          let { displayTypeEnum } = newVal
          let isNumberOrDecimal = ['NUMBER', 'DECIMAL'].includes(
            displayTypeEnum
          )
          if (isNumberOrDecimal) {
            this.loadMetricUnits()
          }
          this.setPlaceHolders()
        }
        if (
          this.allowUpdateProp &&
          this.allowUpdateFieldProp &&
          !this.isInputFieldFocused
        ) {
          if (!isEmpty(oldVal)) {
            if ($helpers.isObjectEqual(newVal, oldVal)) {
              this.debounceFieldUpdate()
            }
          }
        }
      },
      deep: true,
    },
    section: {
      handler(newVal, oldVal) {
        if (this.allowUpdateProp && !this.isInputFieldFocused) {
          if (!isEmpty(oldVal)) {
            if ($helpers.isObjectEqual(newVal, oldVal)) {
              this.debounceSectionUpdate()
            }
          }
        }
      },
      deep: true,
    },
    canShowHideField: {
      handler(newVal) {
        if (!newVal) {
          let { field } = this
          let { hideField, isDisabled } = field || {}
          if (hideField) {
            this.$set(field, 'hideField', false)
          }
          if (isDisabled) {
            this.$set(field, 'isDisabled', false)
          }
        }
      },
    },
  },
  computed: {
    ...mapGetters({
      getMetrics: 'metricUnits/getMetrics',
      getMetricsUnit: 'metricUnits/getMetricsUnit',
    }),
    ...mapState({
      isMetricLoading: state => state.metricUnits.isMetricLoading,
    }),
    metricsList() {
      let metrics = this.getMetrics
      return metrics || []
    },
    showFormProperties() {
      let { field, section } = this
      return isEmpty(field) && isEmpty(section)
    },
    showSectionProperties() {
      let { field, section } = this
      return !isEmpty(section) && isEmpty(field)
    },
    isLookupTypeField() {
      let { field } = this
      let { displayTypeEnum } = field || {}
      return [
        'LOOKUP_POPUP',
        'LOOKUP_SIMPLE',
        'MULTI_LOOKUP_SIMPLE',
        'SPACECHOOSER',
      ].includes(displayTypeEnum)
    },
    title() {
      let field = this.field
      let section = this.section
      if (isEmpty(field) && isEmpty(section)) {
        return 'FORM PROPERTIES'
      } else if (isEmpty(field)) {
        return 'SECTION PROPERTIES'
      } else {
        return 'FIELD PROPERTIES'
      }
    },
    defaultValueLabel() {
      if (!isEmpty(this.field)) {
        let { displayTypeEnum } = this.field
        if (dropDownTypeHash.includes(displayTypeEnum)) {
          return 'Drop down values'
        }
        return 'Default value'
      }
      return ''
    },
    /* Show add option
       - only if no error exists
       - after new option added successfully
    */
    canShowAddIcon() {
      let {
        optionsErrObj: { msg },
        newOptionIndex,
      } = this
      return isEmpty(newOptionIndex) && isEmpty(msg)
    },
    hideDefaultValue() {
      if (!isEmpty(this.field)) {
        let { displayTypeEnum } = this.field
        return hideDefaultValueFieldsHash.includes(displayTypeEnum)
      }
      return true
    },
    unitOptions() {
      let {
        field: { metric },
      } = this
      let options = this.getMetricsUnit({ metricId: metric })
      return options || []
    },
    filterObj: {
      get() {
        return this.filter
      },
      set(value) {
        this.$emit('update:filter', value)
      },
    },
    canShowHideField() {
      let { field } = this
      let { name } = field
      return name !== 'tasks'
    },
    lookupFilterOptionArr() {
      let { field } = this
      let filterOption = constructFieldOptions(Constants.LOOKUP_FILTERS_MAP)
      let isResourceTypeFields = this.isResourceTypeFields(field)
      if (isResourceTypeFields) {
        filterOption = filterOption.slice(0, 2)
      } else {
        filterOption = filterOption.slice(2, 6)
      }
      return filterOption.map(option => {
        let { label, value } = option
        return {
          label: this.$t(`fields.properties.show_only_${label}`),
          value,
        }
      })
    },
    isSubFormSection() {
      let { section } = this
      let { sectionType } = section || {}
      return sectionType === 2
    },
    selectedFieldSection() {
      let { section, formObj, field } = this || {}
      if (!isEmpty(section)) {
        return section
      } else {
        let { sections } = formObj || {}
        let { id: fieldId } = field || {}
        let fieldSection = (sections || []).find(section => {
          let { fields, subForm } = section || {}
          if (!isEmpty(subForm)) {
            fields = this.$getProperty(section, 'subForm.sections.0.fields', [])
          }
          return (fields || []).find(fieldObj => fieldObj.id === fieldId)
        })
        return fieldSection
      }
    },
    subFormId() {
      let { selectedFieldSection } = this || {}
      return this.$getProperty(selectedFieldSection, 'subForm.id')
    },
    isSubFormField() {
      let { selectedFieldSection } = this || {}
      let { subForm } = selectedFieldSection || {}
      return !isEmpty(subForm)
    },
    isDefaultConfigured() {
      let { isSubFormSection, section } = this
      if (isSubFormSection) {
        let { subFormValue } = section || {}
        return !isEmpty(subFormValue)
      }
      return false
    },
    isMultiple() {
      let { field } = this
      let { field: fieldObj } = field || {}
      let { dataTypeEnum } = fieldObj || {}
      return dataTypeEnum === 'MULTI_ENUM'
    },
    isNotesField() {
      let { field } = this
      let { displayTypeEnum } = field
      return displayTypeEnum === 'NOTES'
    },
    placeHolderMap() {
      let { field } = this
      let { displayTypeEnum, lookupModuleName } = field || {}
      let placeHolderObj =
        Constants.FIELD_PLACEHOLDER_MAP[displayTypeEnum] || {}
      if (
        ['LOOKUP_SIMPLE'].includes(displayTypeEnum) ||
        displayTypeEnum === 'SPACECHOOSER'
      ) {
        return placeHolderObj[lookupModuleName] || {}
      }
      return placeHolderObj || {}
    },
    handleNumberFieldMetric() {
      let { field } = this
      let { displayTypeEnum } = field || {}
      return (
        displayTypeEnum === 'NUMBER' &&
        (!isEmpty(field.metric) || !isEmpty(field.unitId))
      )
    },
    canShowCriteriaBuilderForLookup() {
      let { field } = this
      let { displayTypeEnum, field: fieldObj, dataTypeEnum } = field
      let isEnumType = [
        'LOOKUP_SIMPLE',
        'MULTI_LOOKUP_SIMPLE',
        'WOASSETSPACECHOOSER',
        'SPACECHOOSER',
      ]

      if (
        (!isEmpty(fieldObj) || !isEmpty(field)) &&
        (isEnumType.includes(displayTypeEnum) ||
          (dataTypeEnum && dataTypeEnum._name === 'LOOKUP'))
      ) {
        let { lookupModule } = fieldObj || field || {}
        if (!isEmpty(lookupModule)) {
          let { type } = lookupModule || {}
          return Constants.FIELD_LOOKUP_ENTITY_HASH.includes(type)
        }
      }
      return false
    },
  },
  methods: {
    canShowRelatedListTitle(field) {
      let { isLocationField } = this || {}
      return !isMultiLookup(field) && !isLocationField
    },
    ...mapActions({
      loadMetricUnits: 'metricUnits/loadMetricUnits',
    }),
    setPlaceHolders() {
      let { field } = this
      let { value = '' } = field || {}
      this.enablePlaceHolder = Constants.FIELD_PLACEHOLDERS.includes(value)
    },
    openConfigurationDialog(isEdit) {
      let { isDefaultConfigured } = this
      if (isEdit) {
        this.$emit('openConfigurationDialog')
      } else if (!isDefaultConfigured) {
        this.$emit('openConfigurationDialog')
      }
    },
    showCriteriaForField() {
      this.openCriteriaForRule = true
    },
    afterSave() {
      this.$emit('loadRulesList')
    },
    resetRule() {
      let { formRulesMap, field } = this
      let dialogObj = {
        title: 'Delete Form Rule',
        message: 'Are you sure you want to delete this rule?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          let rule = formRulesMap[field.id]
          for (let i = 0; i < rule.actions.length; i++) {
            let action = rule.actions[i]
            action.formRuleActionFieldsContext.forEach(formRuleActionField => {
              if (!isEmpty(formRuleActionField.criteria)) {
                for (let key in formRuleActionField.criteria.conditions) {
                  delete formRuleActionField.criteria.conditions[key].operator
                }
              }
            })
          }
          let url = `v2/form/rule/delete`
          let data = {
            formRuleContext: formRulesMap[field.id],
          }
          http
            .post(url, data)
            .then(({ data: { message, responseCode } }) => {
              if (responseCode === 0) {
                delete this.formRulesMap[field.id]
                this.$emit('loadRulesList')
                this.$message.success('Successfully Deleted')
              } else {
                throw new Error(message)
              }
            })
            .catch(({ message }) => {
              this.$message.error(message)
            })
        }
      })
    },

    updatePosition(value) {
      this.formProperties.labelPosition = value
    },
    /* Dont Show remove option
       - if it is the only option left
    */
    canShowRemoveIcon(index) {
      let { options = [] } = this.field
      let {
        optionsErrObj: { index: errIndex },
        newOptionIndex,
      } = this
      let { length } = options
      if (!isEmpty(newOptionIndex)) {
        return index === newOptionIndex
      }
      if (length < 1 && !isEmpty(errIndex)) {
        return index === errIndex
      }
      let visibleOptionsCount = options.reduce((acc, option) => {
        if (option.visible) {
          acc += 1
        }
        return acc
      }, 0)
      return visibleOptionsCount > 1
    },
    isOptionInvalid(index) {
      let {
        optionsErrObj: { index: errIndex },
      } = this
      return index === errIndex
    },
    isDropdownTypeFields(field) {
      let { displayTypeEnum } = field
      let isLookupModuleField = isLookupDropDownField(this.field)
      return (
        (displayTypeEnum === 'LOOKUP_SIMPLE' && !isLookupModuleField) ||
        ['REQUESTER', 'URGENCY'].includes(displayTypeEnum)
      )
    },
    isDisplayTypeFieldsHash(field) {
      let { displayTypeEnum } = field
      return displayTypeFieldsHash.includes(displayTypeEnum)
    },
    addOption(index) {
      // check both max index in field.field.values and field.options, because if users add options
      // continously then the max index of field.field.values is incorrect, so check if field.options has max
      // greater than max index in field.field.values (index for field.options is value)
      let { options = [], field: fieldObj } = this.field
      let { values = [] } = fieldObj || {}
      let indices = values.map(value => {
        return value.index
      })
      let maxIndex = max(indices || []) || 0
      let clonedOptions = [...options]
      let unsavedIndices = clonedOptions.map(option => option.value)
      let unsavedMaxIndex = max(unsavedIndices || []) || 0
      if (unsavedMaxIndex > maxIndex) {
        maxIndex = unsavedMaxIndex
      }
      let newOption = {
        label: '',
        visible: true,
        isNew: true,
        value: maxIndex + 1,
      }

      index += 1
      clonedOptions.splice(index, 0, newOption)
      clonedOptions = clonedOptions.map(option => {
        return option
      })
      this.$set(this.field, 'options', clonedOptions)
      // Focus the newly created option
      this.$nextTick(() => {
        this.newOptionIndex = index
        let ref = this.$refs[`input${index}`]
        if (!isEmpty(ref)) {
          ref[0].focus()
        }
      })
    },
    removeOption(option, index) {
      let {
        optionsErrObj: { index: errIndex },
        isMultiple,
        field: { value, options },
      } = this
      let { isNew } = option || {}
      if (!isEmpty(errIndex) && errIndex === index) {
        this.optionsErrObj = { msg: '', index: null }
        this.$emit('update:hasError', false)
      }
      if (isNew) {
        options.splice(index, 1)
      } else {
        option.visible = false
      }
      // Reset value if default value is deleted
      if (isMultiple) {
        value = isEmpty(value) ? [] : value
        let optionsValue = (option || {}).value
        let valueIndex = value.indexOf(optionsValue)
        value.splice(valueIndex, 1)
        this.field.value = value
      } else if (value === (option || {}).value) {
        this.field.value = null
      }
      this.newOptionIndex = null
    },
    onInputChange() {
      this.$set(this, 'isInputFieldFocused', true)
    },
    onLabelChange(value) {
      let { hasError } = this
      if (isEmpty(value)) {
        this.labelErrMsg = 'Label cannot be empty'
        this.$emit('update:hasError', true)
      } else if (hasError) {
        this.labelErrMsg = ''
        this.$emit('update:hasError', false)
      }
      this.$set(this, 'isInputFieldFocused', true)
    },
    onOptionsInputChange(index, { label }) {
      let { options = [] } = this.field
      let filteredOptions = options.filter(option => {
        return option.visible
      })
      if (isEmpty(label)) {
        let errMsg = 'Field option cannot be empty'
        this.setFieldInvalid(index, errMsg)
      } else {
        let isDuplicateLabel = checkDuplicateInObject('label', filteredOptions)
        if (isDuplicateLabel) {
          let errMsg = 'Choices cannot contain duplicates'
          this.setFieldInvalid(index, errMsg)
        } else {
          this.setFieldValid()
        }
      }
      this.debounceFieldUpdate()
    },
    onInputBlur(index, { label }) {
      let {
        optionsErrObj: { msg },
        newOptionIndex,
      } = this
      if (!isEmpty(newOptionIndex) && isEmpty(label) && isEmpty(msg)) {
        let errMsg = 'Field option cannot be empty'
        this.setFieldInvalid(index, errMsg)
      }
      this.newOptionIndex = null
    },
    onFormInputChange() {
      this.debounceFormUpdate()
    },
    onSectionInputChange() {
      this.debounceSectionUpdate()
    },
    onFieldInputChange() {
      this.debounceFieldUpdate()
    },
    onFieldTypeChange(value) {
      let { field: fieldObj } = this
      let { value: fieldValue } = fieldObj || {}
      if (Number(value) === 4) {
        this.field.displayTypeEnum = 'RADIO'
        this.field.displayTypeInt = 4
        this.field.value = fieldValue ? 1 : 0
        if (!isEmpty(fieldObj)) {
          this.field.field.trueVal = fieldObj.trueVal || 'True'
          this.field.field.falseVal = fieldObj.falseVal || 'False'
        }
      } else {
        this.field.displayTypeEnum = 'DECISION_BOX'
        this.field.displayTypeInt = 5
        this.field.value = fieldValue !== 0
      }
    },
    onHideChange(value) {
      let { field } = this
      if (value) {
        this.$set(field, 'span', 1)
      }
    },
    onRecordSelected(value, field) {
      let { name, displayTypeEnum } = field

      if (name === 'resource') {
        if (isEmpty(value)) {
          // Reset space asset resource obj, when user clears the selected resource field
          this.$emit('update:spaceAssetResourceObj', {})
          this.$set(field, 'value', null)
        }
      }
      //Temp Hack fix need to removed in revamp
      if (displayTypeEnum === 'MULTI_LOOKUP_SIMPLE') {
        this.$emit('formFieldUpdated', field)
      }
    },
    setLookupFieldValue(props) {
      let { field } = props
      let {
        selectedItems,
        options = [],
        name: fieldName,
        config,
        displayTypeEnum,
        multiple,
      } = field || {}
      let selectedValue = []

      selectedItems.forEach(selectedItem => {
        let { value, label } = selectedItem
        if (!isEmpty(options)) {
          let isAlreadyExists = !isEmpty(
            options.find(option => option.value === value)
          )
          if (!isAlreadyExists) {
            options.unshift(selectedItem)
          }
        } else {
          options.push(selectedItem)
        }
        this.$set(field, 'options', options)
        selectedValue.push(value)

        if (fieldName === 'resource') {
          this.$emit('update:spaceAssetResourceObj', {
            name: label,
            config,
            id: value,
          })
        }
      })
      if (multiple) {
        this.$set(field, 'value', selectedValue)
      } else {
        this.$set(field, 'value', selectedValue[0])
      }
      //Temp Hack fix need to removed in revamp
      if (displayTypeEnum === 'MULTI_LOOKUP_SIMPLE') {
        this.$emit('formFieldUpdated', field)
      }
    },
    onFilterCheckChange(value) {
      let { field } = this
      if (value) {
        this.$set(field, 'value', null)
      }
    },
    onCheckFilters(isFiltersEnabled) {
      let { field: { config = {} } = {} } = this
      this.field.config = { ...config, isFiltersEnabled }
    },
    setFieldInvalid(index, errMsg) {
      this.optionsErrObj = { msg: errMsg, index: index }
      this.$emit('update:hasError', true)
    },
    setFieldValid() {
      this.optionsErrObj = { msg: '', index: null }
      this.$emit('update:hasError', false)
      this.newOptionIndex = null
    },
    resetUnitField() {
      let { field } = this
      this.$set(field, 'unitId', '')
    },
    updateProperties(url, data) {
      let { hasError } = this
      if (!hasError) {
        this.$emit('update:isSaving', true)
        let promise = http
          .post(url, data)
          .then(({ data: { message, responseCode, result } }) => {
            if (responseCode === 0) {
              let { field = {} } = result
              let { values, dataType } = field
              if (!isEmpty(field)) {
                // Handling for loading options after auto save
                this.allowUpdateFieldProp = false
                this.$set(this.field.field, 'values', values)
                // Handling for both single and multi pickist datatype fields
                // Prefill the options with the constructed options only for the valid ones
                if (dataType === 8 || dataType === 14) {
                  let { options: actualOptions = [] } = this.field || {}
                  let modifiedOptions = constructEnumFieldOptions(values)
                  actualOptions = (actualOptions || []).map(option => {
                    let currOptionValue = modifiedOptions.find(
                      val => val.value === option.value
                    )
                    if (!isEmpty(currOptionValue)) return currOptionValue
                    else return option
                  })
                  this.$set(this.field, 'options', actualOptions)
                }
              }
            } else {
              throw new Error(message)
            }
          })
          .catch(({ message }) => {
            if (message === 'Field Display Name Duplication Is Not Allowed') {
              this.labelErrMsg = 'Field display name duplication is not allowed'
              this.$emit('update:hasError', true)
            } else {
              this.$message.error(message)
            }
          })
        Promise.all([promise]).finally(() => {
          this.allowUpdateFieldProp = true
          this.$emit('update:isSaving', false)
          this.$set(this, 'isInputFieldFocused', false)
        })
      }
    },
    serializeFormData(form = {}) {
      let formObj = cloneObject(form)
      let labelPosition = ''
      let labelAlignmentHash = Constants.FORMS_LABELALIGNMENT_ENUMHASH
      Object.entries(labelAlignmentHash).forEach(([key, value]) => {
        if (formObj.labelPosition === value) {
          labelPosition = Number(key)
        }
      })
      formObj.labelPosition = labelPosition
      return formObj
    },
    serializeField(field) {
      let { currentSiteId } = this
      let { name, value } = field

      let resourceProps = Constants.FIELDS_RESOURCE_PROPS
      let serializedField = serializeProps(field, resourceProps)
      let isChooserTypeFields = isChooserTypeField(field)

      let skipStringify = value === null || typeof value === 'string'
      if (
        isChooserTypeFields &&
        field.displayTypeEnum !== 'SPACECHOOSER' &&
        isObject(value) &&
        areValuesEmpty(value)
      ) {
        serializedField.value = ''
      } else if (
        field.displayTypeEnum === 'MULTI_CURRENCY' &&
        !isEmpty(field.currencyCode)
      ) {
        serializedField.currencyCode = field.currencyCode
        serializedField.exchangeRate = field.exchangeRate
      } else {
        serializedField.value = skipStringify
          ? !isEmpty(value)
            ? value
            : ''
          : JSON.stringify(value)
      }

      // Skip site field value update, if global site is not 'All'
      if (name === 'siteId' && !isEmpty(currentSiteId)) {
        delete serializedField['value']
      }
      return serializedField
    },
    showLookupWizard(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizard', canShow)
    },
    showHideMand(displayTypeEnum) {
      return !hideMandatoryFieldsHash.includes(displayTypeEnum)
    },
    showAddOptionsCheckbox(displayTypeEnum) {
      return displayTypeEnum === 'SELECTBOX'
    },
    canDisableMand(name) {
      return disabledMandFieldHash.includes(name) && !this.isCustomModule
    },
    isResourceTypeFields(field) {
      let { displayTypeEnum, lookupModuleName } = field
      return (
        displayTypeEnum === 'WOASSETSPACECHOOSER' &&
        lookupModuleName === 'resource'
      )
    },
    canShowLookupFilterOption(field) {
      let { lookupModuleName } = field
      let isResourceTypeFields = this.isResourceTypeFields(field)
      let isFilterAllowedField = Constants.LOOKUP_FILTER_ENABLED_FIELDS.includes(
        lookupModuleName
      )
      return isResourceTypeFields || isFilterAllowedField
    },
    resetDefaultValues() {
      let { section } = this
      this.$set(section, 'subFormValue', null)
      this.$set(section, 'subFormValueStr', null)
    },
    openRichTextArea() {
      this.canShowRichText = true
    },
    isLookup(field) {
      return isLookupDropDownField(field) || isMultiLookup(field)
    },
    handlePlaceHolder(value) {
      let placeHolderValue = ''
      let { field } = this
      if (value) {
        let { placeHolderMap } = this
        if (!isEmpty(placeHolderMap)) {
          placeHolderValue = placeHolderMap.value || ''
        }
      }
      this.$set(field, 'value', placeHolderValue)
    },
    canShowSkipSiteFilter(fieldObj) {
      let { field } = fieldObj || {}
      let { lookupModule } = field || {}
      let { type } = lookupModule || {}
      return (
        (isLookupField(field || {}) || isMultiLookup(fieldObj)) &&
        Constants.FIELD_LOOKUP_ENTITY_HASH.includes(type)
      )
    },
    setSkipSiteFilter(skipSiteFilter) {
      let { config } = this.field || {}

      this.field.config = !isEmpty(config)
        ? { ...config, skipSiteFilter }
        : { skipSiteFilter }
    },
    setShowName(showName) {
      let config = { skipSiteFilter: false }

      this.field.config = { ...config, showName }
    },
    setHideTaxField(hideTaxField) {
      let { config } = this.field || {}
      this.field.config = { ...config, hideTaxField }
    },
    canShowConfigureBtn(field) {
      let { isSubFormField } = this
      let { displayTypeEnum, configJSON } = field || {}
      let { richText } = configJSON || {}

      if (
        ['FILE', 'ATTACHMENT', 'RICH_TEXT'].includes(displayTypeEnum) ||
        (displayTypeEnum === 'TEXTAREA' && richText)
      ) {
        return true && !isSubFormField
      }
      return false
    },
    canShowDefaultValueDialog() {
      this.showDefaultValueDialog = true
    },
    closeDefaultVaueDialog() {
      this.showDefaultValueDialog = false
    },
    saveDefaultValue(fieldObj) {
      this.$emit('saveDefaultValue', fieldObj)
      this.showDefaultValueDialog = false
    },
    resetDefaultValue() {
      let { field } = this
      let { displayTypeEnum } = field || {}
      let fieldObj = field
      if (displayTypeEnum === 'FILE') {
        fieldObj = { ...field, fileId: null, fileObj: null }
      }
      fieldObj.value = null
      this.$emit('saveDefaultValue', fieldObj)

      //Temp Hack fix need to removed in revamp
      if (displayTypeEnum === 'FILE') {
        this.$emit('formFieldUpdated', fieldObj)
      }
    },
    async locationUpdated(val) {
      let { name } = val || {}
      let { field } = this

      if (!isObject(val)) val = JSON.parse(val)
      if (isEmpty(name)) {
        val.name = field.name
      }

      if (isEmpty(val.locationId)) {
        try {
          let { data, error } = await API.post(`location/add`, {
            location: val,
          })
          if (!error) {
            let { locationId } = data || {}
            let { value } = field || {}

            field.value = { ...value, locationId }
            this.$emit('saveDefaultValue', field)
          }
        } catch (error) {
          console.error('Error occurd:', error)
        }
      }
    },
  },
}
</script>

<style scoped>
.field-title {
  letter-spacing: 0.6px;
}
.el-date-editor.form-date-picker.el-input {
  width: 100%;
}
.el-radio.display-block {
  display: block;
  width: 100%;
}
.el-checkbox >>> .el-checkbox__label {
  font-size: 0.8125rem;
}
.width-auto {
  width: auto;
}
.error .error-text {
  display: block;
  color: #f56c6c;
}
.error-text {
  font-size: 12px;
  line-height: normal;
  letter-spacing: 0.4px;
  color: #8ca1ad;
  margin-left: 27px;
  margin-top: 5px;
  text-transform: none;
}
.error-text {
  display: none;
}
.field-option:hover .add-icon,
.field-option:hover .remove-icon {
  display: block;
}
.add-icon,
.remove-icon {
  display: none;
  width: 18px;
  height: 18px;
}
.add-temp-btn {
  padding: 10px;
  border: 1px solid transparent;
}
.add-temp-btn img {
  width: 13px;
  height: 11px;
}
.add-temp-btn:hover {
  background-color: #f7feff;
  border: 1px solid #39b2c2;
}
.add-temp-btn .btn-label {
  font-size: 12px;
  font-weight: 500;
  color: #39b2c2;
  letter-spacing: 0.5px;
}
.reset-txt-color {
  color: #30a0af;
}
.configured-button {
  border-radius: 3px;
  border: solid 1px #39b2c2;
  background-color: #f1fdff;
  padding: 7px 8px;
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.5px;
  color: #30a0af;
  cursor: pointer;
}
</style>
<style lang="scss">
.field-prop-section {
  .el-radio__input.is-disabled + span.el-radio__label {
    opacity: 1;
  }
}
</style>
