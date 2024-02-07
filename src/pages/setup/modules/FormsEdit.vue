<template>
  <div
    class="form-customization-container position-relative"
    :class="isSaving || hasError ? 'noselect' : ''"
  >
    <FFormLoader :isFormLoading="isFormLoading" />
    <div class="header p15">
      <div class="title cursor-pointer" @click="removeActiveField">
        {{ formProperties.displayName }}
        <span v-show="isSaving" class="saving-container">{{
          $t('common.roles.saving')
        }}</span>
      </div>
      <div class="mL-auto">
        <el-button
          v-if="activeTab === 'builder'"
          type="primary"
          class="fc-btn-green-medium-fill shadow-none"
          @click="saveFormRecord({ redirect: true })"
          :disabled="isSaving"
          >{{ $t('common._common.done') }}</el-button
        >
        <portal-target v-else name="builder-btn-portal"></portal-target>
      </div>
    </div>
    <div class="customization-tabs">
      <portal-target
        name="builder-action-config"
        class="builder-action-config-portal"
      ></portal-target>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="BUILDER" name="builder" class="builder-container">
          <!-- Fields Sections -->
          <div class="fields-section">
            <!-- Default fields section -->
            <div class="default-fields">
              <div class="fc-dark-grey-txt fw-bold f12 p15 text-uppercase">
                {{ $t('forms.builder.new_fields') }}
              </div>
              <div class="container">
                <el-row :gutter="20">
                  <draggable
                    v-model="defaultFields"
                    v-bind="defaultFieldsDragOptions"
                    :clone="createNewFieldInstance"
                    :disabled="isSaving || hasError"
                    @start="onStart"
                    @end="onEnd"
                    :move="onFieldsMove"
                    class="dragArea"
                  >
                    <el-col
                      :span="12"
                      v-for="(field, index) in defaultFields"
                      :key="index"
                      class="mB15"
                    >
                      <el-tooltip
                        :disabled="field.displayName.length < 16"
                        effect="dark"
                        :content="field.displayName"
                        placement="top"
                      >
                        <div class="field-block">
                          <div class="field-label fw4 truncate-text">
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
                      </el-tooltip>
                    </el-col>
                  </draggable>
                </el-row>
                <!-- New Section -->
                <el-row :gutter="20" class="mB20">
                  <draggable
                    v-model="newSection"
                    v-bind="newSectionDragOptions"
                    :clone="createNewFieldInstance"
                    :move="onNewSectionFieldsMove"
                    :disabled="isSaving || hasError"
                    @start="onStart"
                    @end="onEnd"
                    class="dragArea"
                  >
                    <el-col :span="24">
                      <div class="field-block">
                        <div class="field-label fw4 truncate-text d-flex">
                          <span class="mR5">
                            <img
                              src="~assets/new-section.svg"
                              class="field-icons"
                            />
                          </span>
                          {{ $t('forms.builder.new_section') }}
                        </div>
                      </div>
                    </el-col>
                  </draggable>
                </el-row>
                <!-- New Subform -->
                <el-row :gutter="20">
                  <draggable
                    v-model="newSubForm"
                    v-bind="newSectionDragOptions"
                    :clone="createNewFieldInstance"
                    :move="onNewSectionFieldsMove"
                    :disabled="isSaving || hasError"
                    @start="onStart"
                    @end="onEnd"
                    class="dragArea"
                  >
                    <el-col :span="24">
                      <div class="field-block">
                        <div class="field-label fw4 truncate-text d-flex">
                          <span class="mR5">
                            <img
                              src="~assets/new-section.svg"
                              class="field-icons"
                            />
                          </span>
                          {{ $t('forms.builder.new_subform') }}
                        </div>
                      </div>
                    </el-col>
                  </draggable>
                </el-row>
              </div>
            </div>
            <!-- System and Custom fields section -->
            <div class="sys-cus-fields">
              <el-tabs v-model="activeFieldsTab">
                <el-tab-pane
                  :label="$t('forms.builder.system_label')"
                  name="systemfields"
                >
                  <div class="container">
                    <el-row :gutter="20">
                      <draggable
                        v-model="systemFields"
                        v-bind="systemFieldsDragOptions"
                        :disabled="isSaving || hasError"
                        @start="onStart"
                        @end="onEnd"
                        :move="onFieldsMove"
                        class="dragArea"
                      >
                        <el-col
                          :span="24"
                          v-for="(field, index) in systemFields"
                          :key="index"
                          class="mB15"
                        >
                          <el-tooltip
                            :disabled="field.displayName.length < 40"
                            effect="dark"
                            :content="field.displayName"
                            placement="top"
                          >
                            <div class="field-block">
                              <div class="field-label fw4 truncate-text">
                                <span class="mR5 vertical-middle">
                                  <inline-svg
                                    :src="getIconSrc(field.name)"
                                    class="vertical-middle"
                                    iconClass="icon icon-sm-md"
                                  ></inline-svg>
                                </span>
                                {{ field.displayName }}
                              </div>
                            </div>
                          </el-tooltip>
                        </el-col>
                      </draggable>
                    </el-row>
                  </div>
                </el-tab-pane>
                <el-tab-pane
                  :label="$t('forms.builder.custom_label')"
                  name="customfields"
                >
                  <div class="container">
                    <el-row :gutter="20">
                      <draggable
                        v-model="customFields"
                        v-bind="systemFieldsDragOptions"
                        :clone="createNewFieldInstance"
                        :disabled="isSaving || hasError"
                        @start="onStart"
                        @end="onEnd"
                        :move="onFieldsMove"
                        class="dragArea"
                      >
                        <el-col
                          :span="24"
                          v-for="(field, index) in customFields"
                          :key="index"
                          class="mB15"
                        >
                          <el-tooltip
                            :disabled="field.displayName.length < 40"
                            effect="dark"
                            :content="field.displayName"
                            placement="top"
                          >
                            <div class="field-block">
                              <div class="field-label fw4 truncate-text">
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
                          </el-tooltip>
                        </el-col>
                      </draggable>
                    </el-row>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </div>
          <!-- Form content section -->
          <div class="builder-prop-wrapper">
            <div class="builder-section">
              <el-form
                ref="formsEdit"
                :model="formProperties"
                :label-position="formProperties.labelPosition"
                label-width="100px"
                class="form-builder-main-form pT20 pb20"
                @click.native="removeActiveField"
              >
                <draggable
                  v-model="sections"
                  v-bind="formSectionDragOptions"
                  :move="onSectionsMove"
                  :disabled="isSaving || hasError"
                  ghost-class="ghost-class-sections"
                  class="dragArea fc-main-drag-area"
                  @change="onSectionChange"
                >
                  <div
                    v-for="(row, rowIndex) in sections"
                    :key="rowIndex"
                    class="section-container position-relative mB15 p10 mL20 mR20"
                    :class="[
                      rowIndex === sections.length - 1 ? 'border-none' : '',
                      isSectionActive(row) ? 'active' : '',
                      isImageSection(row) ? '' : 'form-drag-section',
                    ]"
                    @click.stop="setActiveSection(rowIndex)"
                  >
                    <img
                      v-if="!hasError && canShowSectionDelete(row, rowIndex)"
                      src="~assets/remove-icon.svg"
                      class="delete-icon"
                      @click.stop="removeSection(row, rowIndex)"
                    />
                    <div
                      v-if="canShowSectionHeader(row, rowIndex)"
                      class="section-header"
                    >
                      <p
                        class="fc-dark-grey-txt fw-bold f12 text-uppercase section-name"
                      >
                        {{ row.name }}
                      </p>
                    </div>
                    <div
                      v-if="
                        !$validation.isEmpty(row.subForm) &&
                          row.sectionType === 2
                      "
                    >
                      <f-sub-form
                        :key="row.subFormId"
                        :form="row.subForm"
                        :isLiveSubForm="false"
                        :isSaving="isSaving"
                        :fieldError="hasError"
                        :activeField.sync="activeField"
                        @onDragEnd="onEnd"
                        @onDragStart="onStart"
                        @resetActiveSection="resetActiveSection"
                        @removeActiveField="removeActiveField"
                      ></f-sub-form>
                    </div>
                    <draggable
                      v-else
                      v-model="sections[rowIndex].fields"
                      v-bind="formFieldsDragOptions"
                      :ghost-class="ghostClass"
                      :disabled="isSaving || hasError"
                      :move="onFieldsMove"
                      class="dragArea flex-container"
                      :class="isFieldEmpty(row.fields) ? 'pT20 pB20' : 'pT10'"
                      @start="onStart"
                      @end="onEnd"
                      @change="onFieldChange(rowIndex, row, ...arguments)"
                    >
                      <div
                        v-for="(column, columnIndex) in row.fields"
                        :key="columnIndex"
                        class="form-drag-main-ele-con position-relative mb5"
                        :class="[
                          column.span === 1
                            ? 'form-one-column'
                            : 'form-two-column',
                          formProperties.labelPosition !== 'top'
                            ? 'label-bottom'
                            : '',
                          column.displayTypeEnum !== 'IMAGE'
                            ? 'form-drag-item'
                            : '',
                        ]"
                        @click.stop="
                          setActiveField(
                            column,
                            rowIndex,
                            columnIndex,
                            ...arguments
                          )
                        "
                      >
                        <el-form-item
                          v-if="!isFieldEmpty(column)"
                          :required="column.required"
                          class="form-label-container"
                          :class="[
                            isFieldActive(column) ? 'active' : '',
                            ['photo'].includes(column.name)
                              ? 'photo-field'
                              : '',
                            ['avatar'].includes(column.name)
                              ? 'avatar-field'
                              : '',
                            ['NOTES'].includes(column.displayTypeEnum)
                              ? 'notes-field'
                              : '',
                            !column.hideField ? 'hide-field' : '',
                          ]"
                        >
                          <div
                            v-if="
                              !['photo', 'avatar'].includes(column.name) &&
                                !['NOTES'].includes(column.displayTypeEnum)
                            "
                            slot="label"
                            class="d-flex"
                          >
                            <div>
                              {{ column.displayName }}
                              <inline-svg
                                v-if="column.hideField"
                                src="svgs/hidden-field"
                                iconClass="mL5 icon icon-sm vertical-text-bottom"
                              ></inline-svg>
                            </div>
                          </div>
                          <img
                            v-show="
                              !isSaving &&
                                !hasError &&
                                canShowFieldDelete(column)
                            "
                            src="~assets/remove-icon.svg"
                            class="delete-icon"
                            @click.stop="
                              removeField(column, rowIndex, columnIndex)
                            "
                          />
                          <img
                            v-if="canShowLayoutIcon(column)"
                            :src="layoutIcon(column.span)"
                            class="layout-icon"
                            @click.stop="changeLayout(column)"
                          />
                          <el-input
                            v-if="
                              ['TEXTAREA', 'RICH_TEXT'].includes(
                                column.displayTypeEnum
                              )
                            "
                            type="textarea"
                            :autosize="{ minRows: 3, maxRows: 5 }"
                            resize="none"
                            :placeholder="`Type your  ${column.displayName}`"
                            class="fc-input-full-border-textarea"
                            :key="`${rowIndex}${columnIndex}`"
                            :disabled="isPreview"
                          ></el-input>
                          <el-checkbox
                            v-model="column.value"
                            v-else-if="
                              column.displayTypeEnum === 'DECISION_BOX'
                            "
                            :disabled="isPreview"
                          ></el-checkbox>
                          <el-radio-group
                            v-model="column.value"
                            v-else-if="column.displayTypeEnum === 'RADIO'"
                            :disabled="isPreview"
                          >
                            <el-radio :label="true" class="fc-radio-btn">
                              {{ column.field.trueVal }}
                            </el-radio>
                            <el-radio :label="false" class="fc-radio-btn">
                              {{ column.field.falseVal }}
                            </el-radio>
                          </el-radio-group>
                          <FSiteField
                            v-else-if="isSiteField(column)"
                            :model.sync="column.value"
                            :canDisable="isPreview"
                            :isBuilder="true"
                          ></FSiteField>
                          <FLookupField
                            v-else-if="
                              $fieldUtils.isChooserTypeField(column) &&
                                !isSpaceChooser(column)
                            "
                            :key="`${columnIndex} ${column.id}`"
                            :model.sync="(column.value || {}).id"
                            :field="column"
                            :disabled="isPreview"
                            :hideLookupIcon="true"
                            :siteId="selectedSiteId"
                            :fetchOptionsOnLoad="true"
                          ></FLookupField>
                          <template
                            class="d-flex flex-direction-column"
                            v-else-if="
                              isLookupSimple(column) ||
                                isMultiLookup(column) ||
                                isSpaceChooser(column)
                            "
                          >
                            <FLookupField
                              :key="`${columnIndex} ${column.id}`"
                              :field="column"
                              :disabled="isPreview"
                              :hideLookupIcon="true"
                              :siteId="selectedSiteId"
                              :fetchOptionsOnLoad="true"
                            ></FLookupField>
                          </template>
                          <el-select
                            v-else-if="
                              isDropdownTypeFields(column.displayTypeEnum) &&
                                isMultiple(column)
                            "
                            collapse-tags
                            :multiple="true"
                            filterable
                            placeholder="Select"
                            class="fc-input-full-border-select2 width100 fc-tag"
                            :disabled="isPreview"
                          >
                            <el-option
                              v-for="(option, index) in column.options"
                              :key="index"
                              :label="option.label"
                              :value="option.value"
                            ></el-option>
                          </el-select>
                          <el-select
                            v-else-if="
                              isDropdownTypeFields(column.displayTypeEnum)
                            "
                            v-model="column.value"
                            filterable
                            placeholder="Select"
                            class="fc-input-full-border-select2 width100"
                            :disabled="isPreview"
                          >
                            <el-option
                              v-for="(option, index) in column.options"
                              :key="index"
                              :label="option.label"
                              :value="option.value"
                            ></el-option>
                          </el-select>
                          <div
                            v-else-if="
                              column.displayTypeEnum === 'TEAMSTAFFASSIGNMENT'
                            "
                            class="fc-input-full-border-select2"
                            :disabled="isPreview"
                          >
                            <div class="fc-border-input-div">
                              <span>{{ getTeamStaffLabel(column.value) }}</span>
                              <span style="float: right;">
                                <img
                                  class="svg-icon team-down-icon"
                                  src="~assets/down-arrow.svg"
                                />
                              </span>
                            </div>
                            <FAssignment
                              v-if="!isPreview"
                              :model="column.value"
                              :siteId="selectedSiteId"
                              viewtype="form"
                            ></FAssignment>
                          </div>
                          <div
                            v-else-if="column.displayTypeEnum === 'ATTACHMENT'"
                          >
                            <div class="fc-attachments">
                              <div class="pointer">
                                <form>
                                  <div class="dropbox text-center">
                                    <img
                                      src="~assets/upload-icon.svg"
                                      class="mT10 upload-icon"
                                    />
                                    <input class="input-file" />
                                    <p>
                                      {{
                                        $t(
                                          'common.attachment_form.drag_and_drop_file'
                                        )
                                      }}
                                      <br />
                                      {{
                                        $t(
                                          'common.attachment_form.click_to_browse'
                                        )
                                      }}
                                    </p>
                                  </div>
                                </form>
                              </div>
                            </div>
                          </div>
                          <el-input
                            v-else-if="column.displayTypeEnum === 'NUMBER'"
                            type="number"
                            v-model="column.value"
                            class="fc-input-full-border2"
                            :disabled="isPreview"
                          ></el-input>
                          <el-input
                            v-else-if="
                              [
                                'TASKS',
                                'PERMIT_CHECKLIST',
                                'QUOTE_LINE_ITEMS',
                                'INVREQUEST_LINE_ITEMS',
                                'QUOTE_ADDRESS',
                                'ADDRESS',
                                'SADDRESS',
                                'LINEITEMS',
                                'GEO_LOCATION',
                                'BUDGET_AMOUNT',
                                'FACILITY_AVAILABILITY',
                                'FACILITY_BOOKING_SLOTS',
                              ].includes(column.displayTypeEnum)
                            "
                            class="fc-input-full-border2"
                            :disabled="isPreview"
                          ></el-input>
                          <FTimePicker
                            v-else-if="column.displayTypeEnum === 'TIME'"
                            :key="`time ${columnIndex} ${column.id}`"
                            v-model="column.value"
                            :field="column"
                            :disabled="isPreview"
                            :placeholder="
                              $t('fields.properties.time_picker_placeholder')
                            "
                            class="el-select fc-input-full-border-select2 width100"
                          ></FTimePicker>
                          <FDatePicker
                            v-else-if="column.displayTypeEnum === 'DATE'"
                            :key="`${columnIndex} ${column.id}`"
                            v-model="column.value"
                            :type="'date'"
                            class="fc-input-full-border2 form-date-picker"
                            :disabled="isPreview"
                          ></FDatePicker>
                          <FDatePicker
                            v-else-if="column.displayTypeEnum === 'DATETIME'"
                            :key="`${columnIndex} ${column.id}`"
                            v-model="column.value"
                            :type="'datetime'"
                            class="fc-input-full-border2 form-date-picker"
                            :disabled="isPreview"
                          ></FDatePicker>
                          <FDatePicker
                            v-else-if="column.displayTypeEnum === 'DATERANGE'"
                            :key="`${columnIndex} ${column.id}`"
                            v-model="column.value"
                            :type="'daterange'"
                            class="fc-input-full-border2 form-date-picker"
                            :disabled="isPreview"
                          ></FDatePicker>
                          <div
                            v-else-if="column.displayTypeEnum === 'DURATION'"
                          >
                            <f-duration-field
                              :key="`${columnIndex} ${column.id}`"
                              class="duration-container"
                              :field="column"
                              :isDisabled="isPreview"
                            ></f-duration-field>
                          </div>
                          <div v-else-if="column.displayTypeEnum === 'IMAGE'">
                            <forms-photo-field
                              :field="column"
                              :activeField="activeField"
                              @updatePhotoField="updatePhotoField"
                            ></forms-photo-field>
                          </div>

                          <FFileUpload
                            v-else-if="column.displayTypeEnum === 'FILE'"
                            :isFileType="true"
                            :isDisabled="isPreview"
                            :imgContentUrl.sync="column.imgUrl"
                            :showWebCamPhoto="false"
                          ></FFileUpload>

                          <div
                            v-else-if="column.displayTypeEnum === 'SIGNATURE'"
                          >
                            <FSignaturePad
                              :disabled="isPreview"
                              :canShowColorPalette="
                                (column.config || {}).canShowColorPalette
                              "
                            ></FSignaturePad>
                          </div>
                          <div
                            v-else-if="column.displayTypeEnum === 'NOTES'"
                            class="notes-container"
                          >
                            <InlineSvg
                              src="form-notes"
                              class="self-center pointer d-flex"
                              :iconClass="`icon icon-sm-md`"
                            ></InlineSvg>
                            <div class="mL10">
                              {{ $t('forms.builder.notes_des') }}
                            </div>
                          </div>
                          <FUrlField
                            v-else-if="column.displayTypeEnum === 'URL_FIELD'"
                            :key="`${columnIndex}-${column.id}`"
                            :field="column"
                            :isDisabled="isPreview"
                            :config="column.config"
                          ></FUrlField>
                          <div
                            v-else-if="column.displayTypeEnum === 'CURRENCY'"
                          >
                            <FCurrencyField
                              :key="`${columnIndex}-${column.id}`"
                              :disabled="isPreview"
                              :field="column"
                              :config="column.config"
                              :value="column.value"
                            ></FCurrencyField>
                          </div>
                          <div
                            v-else-if="
                              column.displayTypeEnum === 'MULTI_CURRENCY'
                            "
                          >
                            <FNewCurrencyField
                              :key="`${columnIndex}-${column.id}`"
                              :disabled="isPreview"
                              :field="column"
                              :config="column.config"
                              :value="column.value"
                              :hideCurrency="true"
                              :hideDesc="true"
                            ></FNewCurrencyField>
                          </div>
                          <FEmailField
                            v-else-if="column.displayTypeEnum === 'EMAIL'"
                            :field="column"
                            v-model="column.value"
                            :isDisabled="isPreview"
                          ></FEmailField>
                          <div
                            v-else-if="
                              column.displayTypeEnum === 'GEO_LOCATION'
                            "
                          >
                            <FLocationField
                              :key="`${columnIndex}-${column.id}`"
                              v-model="column.value"
                              :field="column"
                              :disabled="isPreview"
                            ></FLocationField>
                          </div>
                          <el-input
                            v-else
                            class="fc-input-full-border2"
                            v-model="column.value"
                            :disabled="isPreview"
                          ></el-input>
                        </el-form-item>
                        <el-form-item v-else></el-form-item>
                      </div>
                    </draggable>
                  </div>
                </draggable>
                <div class="formbuilder-main-btn">
                  <el-button class="formbuilder-secondary-btn text-uppercase">
                    {{ formProperties.secondaryBtnLabel }}
                  </el-button>
                  <el-button class="formbuilder-primary-btn text-uppercase">
                    {{ formProperties.primaryBtnLabel }}
                  </el-button>
                </div>
              </el-form>
            </div>
            <!-- Form and fields properties section -->
            <div class="property-section">
              <FieldProperties
                :formId="formId"
                :formObj="formObj"
                :moduleName="moduleName"
                :isCustomModule="isCustomModule"
                :field="activeField"
                :fieldsList="formFields"
                :section="activeSection"
                :formRulesMap="formRulesMap"
                :formProperties="formProperties"
                :spaceAssetResourceObj.sync="spaceAssetResourceObj"
                :jobPlansList="jobPlansList"
                :isSaving.sync="isSaving"
                :allowUpdateProp="allowUpdateProp"
                :allowUpdateFormProp="allowUpdateFormProp"
                :isFieldPropertiesLoading="isFieldPropertiesLoading"
                :hasError.sync="hasError"
                :filter.sync="filter"
                :siteId="selectedSiteId"
                :moduleList="moduleList"
                :currentSiteId="currentSiteId"
                :isUpdateForm="isUpdateForm"
                @openConfigurationDialog="canShowSubFormDefaultDialog"
                @loadRulesList="getFormRulesMap"
                @formFieldUpdated="formFieldUpdated"
                @saveDefaultValue="saveDefaultValue"
              ></FieldProperties>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="RULES" name="rules" lazy>
          <FormRulesList
            v-if="activeTab === 'rules'"
            :moduleName="moduleName"
            :formId="formId"
            :redirectToFormsList="redirectToFormsList"
            :formObj="formObj"
            :isUpdateForm="isUpdateForm"
          ></FormRulesList>
        </el-tab-pane>
        <el-tab-pane
          :label="$t('setup.form_builder.validation.validation_rules')"
          name="validation"
          lazy
        >
          <ValidationRulesList
            v-if="activeTab === 'validation'"
            :moduleName="moduleName"
            :formId="formId"
            :redirectToFormsList="redirectToFormsList"
          ></ValidationRulesList>
        </el-tab-pane>
        <el-tab-pane
          v-if="isSubFormPresent"
          label="SUBFORM RULES"
          name="subformrules"
          lazy
        >
          <SubformRulesList
            v-if="activeTab === 'subformrules'"
            :moduleName="moduleName"
            :formId="formId"
            :redirectToFormsList="redirectToFormsList"
            :formObj="formObj"
            :isUpdateForm="isUpdateForm"
          ></SubformRulesList>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!--
      Lookup field add dialogue box
    -->
    <LookupFieldProperties
      v-if="openLookupDialog"
      :openLookupDialog.sync="openLookupDialog"
      :moduleList="moduleList"
      :lookupField.sync="lookupField"
      @addNewDefaultField="addNewDefaultField"
      @removeField="removeField"
      :moduleName="moduleName"
      :isLocationField="isLocationField"
      :isCustomModule="isCustomModule"
    ></LookupFieldProperties>

    <!--
      Multi Line field add dialogue box
    -->
    <MultiLineFieldProperties
      ref="multiLineFieldProperties"
      v-if="openMultiLinePropDialog"
      :openMultiLinePropDialog.sync="openMultiLinePropDialog"
      :multiLineField.sync="multiLineField"
      @addNewDefaultField="addNewDefaultField"
      @removeField="removeField"
      :moduleName="moduleName"
    ></MultiLineFieldProperties>
    <!--
      Subform modules list dialog box
    -->
    <SubFormModulesList
      v-if="openSubformDialog"
      :openSubformDialog.sync="openSubformDialog"
      :subFormsModules="subFormsModules"
      :activeSectionIndex="activeSectionIndex"
      :activeSection="activeSection"
      :formId="(formProperties || {}).id"
      @updateSubFormSection="updateSubFormSection"
      @removeSection="removeSection"
    ></SubFormModulesList>
    <!--
      Subform default values configuration dialog box
    -->
    <SubFormValuesConfigBox
      v-if="openSubformDefaultDialog"
      :openSubformDefaultDialog.sync="openSubformDefaultDialog"
      :allowUpdateProp.sync="allowUpdateProp"
      :section="activeSection"
    >
    </SubFormValuesConfigBox>

    <DefaultFieldProperties
      v-if="openFieldNamePropDialog"
      :canShowDialog.sync="openFieldNamePropDialog"
      @removeField="removeField"
      @addNewDefaultField="addNewDefaultField"
      :fieldObj.sync="defaultField"
    />
  </div>
</template>

<script>
import FFormLoader from '@/FFormLoader'
import draggable from 'vuedraggable'
import http from 'util/http'
import { isEmpty, isFunction } from '@facilio/utils/validation'
import { cloneArray, cloneObject } from 'util/utility-methods'
import FieldProperties from '@/form-builder/FieldProperties'
import FormsPhotoField from '@/form-builder/FormsPhotoField'
import SubFormValuesConfigBox from '@/form-builder/SubFormValuesConfigBox'
import FFileUpload from '@/FFileUpload'
import LookupFieldProperties from '@/form-builder/LookupFieldProperties'
import DefaultFieldProperties from '@/form-builder/DefaultFieldProperties'
import MultiLineFieldProperties from '@/form-builder/MultiLineFieldProperties'
import SubFormModulesList from '@/form-builder/SubFormModulesList'
import WebFormMixin from '@/mixins/forms/WebFormMixin'
import FormBuilderMixin from '@/mixins/forms/FormBuilderMixin'
import Constants from 'util/constant'
import FDurationField from '@/FDurationField'
import FSiteField from '@/FSiteField'
import FSubForm from '@/FSubForm'
import FSignaturePad from '@/FSignaturePad'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import FTimePicker from '@/FTimePicker'
import FEmailField from '@/FEmailField'
import FLocationField from '@/forms/FLocationField'
import { isChooserTypeField } from 'util/field-utils'
import {
  isLookupSimple,
  isMultiLookup,
  isSiteField,
} from '@facilio/utils/field'
import FormRulesList from 'src/pages/setup/form-rules/FormRulesList'
import FUrlField from 'newapp/components/FUrlField'
import SubformRulesList from 'src/pages/setup/form-rules/SubformRulesList'
import ValidationRulesList from 'src/pages/setup/form-validation/FormValidationList'
import FCurrencyField from 'src/components/FCurrencyField.vue'
import TeamStaffMixin from '@/mixins/TeamStaffMixin'
import FNewCurrencyField from 'src/components/FNewCurrencyField.vue'

const defaultDragOptions = {
  animation: 150,
  easing: 'cubic-bezier(1, 0, 0, 1)',
}

const fullWidthFieldsHash = [
  'TEXTAREA',
  'ATTACHMENT',
  'TEAMSTAFFASSIGNMENT',
  'TASKS',
  'IMAGE',
  'FILE',
  'DURATION',
  'SIGNATURE',
  'NOTES',
  'GEO_LOCATION',
]
const hideDeleteIconFieldsHash = ['subject', 'siteId']
const excludeFieldsHash = ['stateFlowId', 'moduleState']

export default {
  name: 'forms-edit',
  props: ['moduleName', 'id', 'onSave', 'showDeleteForField', 'isUpdateForm'],
  mixins: [WebFormMixin, FormBuilderMixin, TeamStaffMixin],
  components: {
    FFormLoader,
    draggable,
    FieldProperties,
    LookupFieldProperties,
    MultiLineFieldProperties,
    FDurationField,
    FormsPhotoField,
    FSiteField,
    FSubForm,
    SubFormModulesList,
    SubFormValuesConfigBox,
    FFileUpload,
    FSignaturePad,
    FDatePicker,
    FTimePicker,
    FEmailField,
    FLocationField,
    FormRulesList,
    FUrlField,
    DefaultFieldProperties,
    SubformRulesList,
    ValidationRulesList,
    FCurrencyField,
    FNewCurrencyField,
  },
  data() {
    return {
      module: 'formBuilder',
      formRulesMap: {},
      appId: null,
      isFormLoading: false,
      isPreview: true,
      defaultFields: [],
      systemFields: [],
      customFields: [],
      newSection: [
        {
          name: 'New Section',
          showLabel: true,
          fields: [],
        },
      ],
      newSubForm: [
        {
          name: 'Subform',
          fields: [],
          subForm: {},
          subFormId: -1,
          showLabel: true,
          isSubForm: true,
        },
      ],
      formProperties: {
        displayName: 'Untitled form',
        id: null,
        description: null,
        labelPosition: 'top',
        primaryBtnLabel: 'Submit',
        secondaryBtnLabel: 'Cancel',
      },
      sections: [],
      activeField: {},
      activeSection: {},
      activeSectionIndex: null,
      activeFieldsTab: 'systemfields',
      activeTab: 'builder',
      draggedIntoNewSection: false,
      allowUpdateProp: true,
      allowUpdateFormProp: false,
      isSaving: false,
      isFieldPropertiesLoading: false,
      debounceSaveRecord: null,
      hasError: false,
      filter: {},
      jobPlansList: [],
      taskDetails: [],
      spaceAssetResourceObj: {},
      taskTemplateName: '',
      isCustomModule: false,
      openMultiLinePropDialog: false,
      openLookupDialog: false,
      openFieldNamePropDialog: false,
      openSubformDialog: false,
      openSubformDefaultDialog: false,
      moduleList: [],
      lookupField: null,
      defaultField: null,
      multiLineField: null,
      subFormsModules: [],
      formObj: {},
      isLocationField: false,
    }
  },
  beforeDestroy() {
    window.removeEventListener('beforeunload', this.beforeWindowUnload)
  },
  created() {
    let { moduleName, formId } = this
    let { FORM_SOURCE } = Constants
    this.isLookupSimple = isLookupSimple
    this.isMultiLookup = isMultiLookup
    this.isSiteField = isSiteField
    this.$store.dispatch('loadGroups')
    this.getFormRulesMap()
    this.isFormLoading = true
    let promises = []
    let fetchUnusedFieldsUrl = `/v2/forms/fields/unusedlist?moduleName=${moduleName}&formId=${formId}`
    let formsDetails = `/v2/forms/${moduleName}?formId=${formId}&formSourceType=${FORM_SOURCE.BUILDER}`
    let fetchModulesList = `/v2/module/list?defaultModules=true`
    let fetchSubFormsModulesList = `v2/forms/subFormModules?moduleName=${moduleName}`
    promises.push(http.get(formsDetails))
    promises.push(http.get(fetchUnusedFieldsUrl))
    promises.push(http.get(fetchModulesList))
    promises.push(http.get(fetchSubFormsModulesList))
    Promise.all(promises)
      .then(
        ([
          formsData = {},
          unusedFieldsData = {},
          modulesData = {},
          subFormsModulesData = {},
        ]) => {
          let {
            result: { fields },
          } = unusedFieldsData.data
          let {
            result: {
              form,
              form: { sections, module },
            },
          } = formsData.data
          let { custom, name } = module
          let {
            result: { moduleList },
          } = modulesData.data
          let {
            result: { modules: subFormModules },
          } = subFormsModulesData.data
          let { systemFields, customFields } = fields
          let filteredCustomFields = []
          // Have to remove these fields before loading form builder
          if (!isEmpty(customFields)) {
            filteredCustomFields = customFields.filter(field => {
              return !excludeFieldsHash.includes((field || {}).name)
            })
          }
          this.sections = this.deserializeSectionData(sections)
          this.defaultFields = Constants.FORMS_DEFAULT_FIELDS.filter(
            field =>
              !field.hasOwnProperty('license') ||
              (field.hasOwnProperty('license') &&
                this.$helpers.isLicenseEnabled(field.license))
          )

          this.removeCurrencyFieldForSystemModule(custom)

          this.customFields = this.deserializeData(filteredCustomFields)
          this.systemFields = this.deserializeData(systemFields)
          moduleList = moduleList.filter(module => module.name !== 'users')
          this.moduleList = moduleList
          this.subFormsModules = subFormModules
          this.isCustomModule = custom
          this.activeFieldsTab = this.isCustomModule
            ? 'customfields'
            : 'systemfields'
          this.allowUpdateFormProp = false
          this.formProperties.labelPosition =
            Constants.FORMS_LABELALIGNMENT_ENUMHASH[form.labelPosition]
          this.formProperties.id = formId
          this.formProperties.description = form.description
          this.formProperties.displayName = form.displayName
          this.isFormLoading = false
          this.allowUpdateFormProp = true
          this.appId = form.appId
          this.formObj = form
        }
      )
      .catch(({ message }) => {
        this.$message.error(message)
        this.isFormLoading = false
        this.allowUpdateFormProp = true
      })
    // .finally(() => {
    //   this.isFormLoading = false;
    //   this.allowUpdateFormProp = true;
    // });
  },
  watch: {
    isSaving(newVal) {
      if (newVal) {
        window.addEventListener('beforeunload', this.beforeWindowUnload)
      } else {
        window.removeEventListener('beforeunload', this.beforeWindowUnload)
      }
    },
  },
  computed: {
    defaultFieldsDragOptions() {
      return {
        ...defaultDragOptions,
        group: {
          name: 'fields',
          pull: 'clone',
          put: false,
        },
        sort: false,
      }
    },
    systemFieldsDragOptions() {
      return {
        ...defaultDragOptions,
        group: 'fields',
        sort: false,
      }
    },
    formFields() {
      let { sections } = this
      let fields = []
      if (!isEmpty(sections)) {
        sections.forEach(sec => {
          if (!isEmpty(sec.fields)) {
            sec.fields.forEach(field => {
              fields.push(field)
            })
          }
        })
      }
      return fields
    },
    newSectionDragOptions() {
      return {
        ...defaultDragOptions,
        group: {
          name: 'sections',
          pull: 'clone',
          put: false,
        },
      }
    },
    newSubFormDragOptions() {
      return {
        ...defaultDragOptions,
        group: {
          name: 'sections',
          pull: 'clone',
          put: false,
        },
        // sort: false,
      }
    },
    formSectionDragOptions() {
      return {
        ...defaultDragOptions,
        draggable: '.form-drag-section',
        group: 'sections',
        sort: true,
      }
    },
    formFieldsDragOptions() {
      return {
        ...defaultDragOptions,
        draggable: '.form-drag-item',
        group: 'fields',
        sort: true,
      }
    },
    ghostClass() {
      let {
        formProperties: { labelPosition },
      } = this
      return labelPosition === 'top' ? 'ghost-class-top' : 'ghost-class'
    },
    formId() {
      let { id } = this
      return parseInt(id)
    },
    isSubFormPresent() {
      let { formObj } = this || {}
      let { sections = [] } = formObj || {}
      let isSubFormPresent = sections.some(
        section => !isEmpty((section || {}).subForm)
      )
      return isSubFormPresent
    },
  },
  methods: {
    //Temp hack should be removed in revamp
    formFieldUpdated(field) {
      let { formObj } = this
      let { id: selectedFieldId } = field || {}
      let { sections } = formObj || {}

      let updatedSections = sections.map(section => {
        let { fields } = section || {}
        let fieldIndex = fields.findIndex(field => field.id === selectedFieldId)

        if (fieldIndex !== -1) {
          fields[fieldIndex] = field
          section.fields = fields
        }
        return section
      })
      this.$set(formObj, 'sections', updatedSections)
    },
    beforeWindowUnload(e) {
      e.preventDefault()
      e.returnValue = ''
    },
    getFormRulesMap() {
      let { formId } = this
      this.$http
        .post(`v2/form/rule/getRulesMap`, {
          formRuleContext: {
            formId: formId,
          },
        })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.formRulesMap = data.result.formRuleResultJSON
          } else {
            throw new Error()
          }
        })
        .catch(() => {
          this.$message.error('Could not load form rule')
        })
    },
    redirectToFormsList() {
      let { moduleName, appId, onSave, $route } = this
      let isSetupPage = this.$getProperty($route.query, 'setup', false)
      if (isSetupPage) {
        this.sendMessage()
      } else {
        if (isFunction(onSave)) {
          onSave()
        } else {
          let currentPath = this.$router.resolve({
            name: 'modules-details',
            params: { moduleName },
          }).href

          this.$router.push({
            path: `${currentPath}/layouts`,
            query: { appId },
          })
        }
      }
    },
    setActiveSection(rowIndex) {
      let { hasError, sections } = this
      let isNotLastSection = rowIndex !== sections.length - 1
      if (!hasError) {
        // If empty section is clicked, then show form properties
        this.activeSection = isNotLastSection ? this.sections[rowIndex] : {}
        this.activeField = {}
        this.allowUpdateProp = true
      }
    },
    setActiveField(field, rowIndex, columnIndex) {
      let { hasError } = this
      let { hideField, displayTypeEnum } = field
      if (!hasError) {
        if (displayTypeEnum !== 'IMAGE' || !hideField) {
          this.activeSection = {}
          let activeField = (this.sections[rowIndex] || {}).fields[columnIndex]
          if (displayTypeEnum === 'MULTI_LOOKUP_SIMPLE') {
            let { value } = activeField || {}
            if (typeof value === 'string') value = JSON.parse(value)

            this.activeField = { ...activeField, value }
          } else {
            this.activeField = activeField
          }
          this.allowUpdateProp = true
        }
      }
    },
    removeActiveField() {
      let { hasError } = this
      if (!hasError) {
        this.activeSection = {}
        this.activeField = {}
        this.allowUpdateProp = false
      }
    },
    layoutIcon(span) {
      return span === 1
        ? require('assets/contract-layout.svg')
        : require('assets/expand-layout.svg')
    },
    changeLayout(element) {
      element.span = element.span === 1 ? 2 : 1
    },
    isFieldEmpty(field) {
      return isEmpty(field)
    },
    isSectionActive({ id }) {
      let { activeSection } = this
      return (activeSection || {}).id === id
    },
    isImageSection(section) {
      let { fields } = section
      if (!isEmpty(fields)) {
        return fields.some(
          field => field.name === 'photo' || field.displayTypeEnum === 'IMAGE'
        )
      }
      return false
    },
    canShowLayoutIcon(field) {
      let { displayTypeEnum, hideField } = field
      return !fullWidthFieldsHash.includes(displayTypeEnum) && !hideField
    },
    canShowSectionHeader(element, rowIndex) {
      let { sections } = this
      let isNotLastSection = rowIndex !== sections.length - 1
      return isNotLastSection && element.showLabel
    },
    canShowSectionDelete(section, rowIndex) {
      let { sections } = this
      let { fields } = section
      let isNotLastSection = rowIndex !== sections.length - 1
      return isEmpty(fields) && isNotLastSection
    },
    canShowFieldDelete({ name }) {
      let { isCustomModule, showDeleteForField } = this

      if (isFunction(showDeleteForField)) {
        return showDeleteForField({ name })
      } else if (isCustomModule) {
        return !['name', 'photo'].includes(name)
      } else {
        return !hideDeleteIconFieldsHash.includes(name)
      }
    },
    createNewFieldInstance(field) {
      let newFieldInstance = cloneObject(field)
      return newFieldInstance
    },
    removeSection(removedSection, rowIndex) {
      let sections = this.sections
      let { activeSection } = this

      if (!isEmpty(activeSection) && removedSection.id === activeSection.id) {
        this.activeSection = {}
      }
      sections.splice(rowIndex, 1)
      this.debounceSaveRecord()
    },
    removeField(removedField, row, column) {
      let { activeField, formFields } = this
      let systemFields = this.systemFields
      let customFields = this.customFields

      if (formFields.length === 1) {
        this.$message.error('There must be atleast one field')
        return
      }
      // To check whether the removed field is system field or custom field
      let { field, isDefault, displayTypeEnum } = removedField
      let { default: isSystemField } = field || {}
      let isNotesField = displayTypeEnum === 'NOTES'

      if (!isEmpty(activeField) && removedField.id === activeField.id) {
        this.removeActiveField()
      }

      if (!isNotesField) {
        if (!isEmpty(field) && !isSystemField) {
          this.resetFieldValues(removedField)
          customFields.push(removedField)
        } else if (!isDefault) {
          this.resetFieldValues(removedField)
          systemFields.push(removedField)
        }
      }
      if (!isEmpty(this.sections[row])) {
        this.sections[row].fields.splice(column, 1)
      }
      this.debounceSaveRecord()
    },
    resetFieldValues(field) {
      if (field.displayTypeEnum === 'TEAMSTAFFASSIGNMENT') {
        field.value = {
          assignedTo: {
            id: '',
          },
          assignmentGroup: {
            id: '',
          },
        }
      } else if (isChooserTypeField(field)) {
        field.value = { id: null }
      } else {
        field.value = null
      }
    },
    resetActiveSection() {
      this.activeSection = {}
      this.activeSectionIndex = null
    },
    isSpaceChooser(column) {
      let { displayTypeEnum } = column
      return displayTypeEnum === 'SPACECHOOSER'
    },
    resetActiveFields() {
      this.activeField = {}
    },
    formatSectionsArray() {
      let { sections } = this
      let sectionsLength = Object.keys(sections).length
      let _sections = cloneArray(sections)

      Object.keys(sections).forEach(row => {
        let fields = sections[row].fields
        if (Number(row) === sectionsLength - 1) {
          if (!isEmpty(fields)) {
            _sections.push({
              name: `section_${sectionsLength}`,
              id: -1,
              fields: [],
            })
          }
        }
      })
      this.sections = _sections
    },
    /* Drag and Drop related methods */
    onEnd() {
      let formElement = (this.$refs['formsEdit'] || {}).$el
      formElement.classList.remove('disable-hover')
    },
    onStart() {
      let formElement = (this.$refs['formsEdit'] || {}).$el
      formElement.classList.add('disable-hover')
    },
    onSectionsMove({ draggedContext, relatedContext }) {
      let { element: draggedElement = {} } = draggedContext
      let { element: relatedElement = {} } = relatedContext
      /*
        Cancel drag if empty section is dragged
        Cancel drag if section dragged over empty section
      */
      let isEmptyElementDragged = draggedElement.id !== -1
      let isSectionDraggedOverEmptyDiv = relatedElement.id !== -1

      return isEmptyElementDragged && isSectionDraggedOverEmptyDiv
    },
    onFieldsMove({ draggedContext, relatedContext }) {
      /*
        Cancel drag if empty section dragged over fields.
      */
      let { element: draggedElement } = draggedContext
      let { element: relatedElement } = relatedContext
      let { isSaving } = this

      if (isSaving) {
        return false
      } else if (isEmpty(draggedElement)) {
        return false
      } else if (!isEmpty(relatedElement)) {
        this.draggedIntoNewSection = false
      } else {
        this.draggedIntoNewSection = true
      }
      return true
    },
    onNewSectionFieldsMove() {
      let { isSaving } = this
      return !isSaving
    },
    onFieldChange(rowIndex, section, { added = {}, moved = {} }) {
      let { sections } = this
      let isLastSection = rowIndex === sections.length - 1
      let { element: addedElement = {}, newIndex: columnIndex } = added
      let { element: movedElement = {} } = moved
      if (!isEmpty(addedElement)) {
        let { displayTypeEnum, isDefault: isDefaultFields } = addedElement
        let { draggedIntoNewSection } = this
        let isNotesField = displayTypeEnum === 'NOTES'

        if (!fullWidthFieldsHash.includes(displayTypeEnum)) {
          addedElement.span = draggedIntoNewSection ? 1 : 2
        }
        this.allowUpdateProp = true
        if (isDefaultFields) {
          addedElement.rowIndex = rowIndex
          addedElement.columnIndex = columnIndex
          if (['GEO_LOCATION', 'LOOKUP'].includes(displayTypeEnum)) {
            let isLocationField = displayTypeEnum === 'GEO_LOCATION'
            this.addLookupField(addedElement, isLocationField)
          }
          // else if (displayTypeEnum === 'GEO_LOCATION') {
          //   this.addLocationField(addedElement)
          // }
          else if (displayTypeEnum === 'TEXTAREA') {
            this.addMultiLineField(addedElement)
          } else {
            if (isNotesField) {
              this.addNotesField(addedElement)
            } else {
              this.addDefaultField(addedElement)
            }
          }
        } else if (isLastSection) {
          let { sections } = this
          let sectionsLength = sections.length + 1
          // Pushing empty section object, to support adding new section.
          sections.push({
            name: `section_${sectionsLength}`,
            id: -1,
            showLabel: false,
            fields: [],
          })
          this.addNewSection().then(sections => {
            let activeElement = this.findSelectedField(sections, addedElement)
            this.resetActiveSection()
            this.activeField = activeElement
          })
        } else {
          this.activeField = addedElement
          this.resetActiveSection()
          this.saveFormRecord({})
        }
      }
      if (!isEmpty(movedElement)) {
        this.activeField = movedElement
        this.resetActiveSection()
        this.allowUpdateProp = true
        this.saveFormRecord({})
      }
      this.formatSectionsArray()
    },
    onSectionChange({ added = {}, moved = {} }) {
      let { element: addedElement = {}, newIndex: elementIndex } = added
      let { element: movedElement = {}, newIndex: movedElementIndex } = moved

      if (!isEmpty(addedElement)) {
        let { isSubForm } = addedElement
        this.activeSection = addedElement
        this.activeSectionIndex = elementIndex
        this.resetActiveFields()
        if (isSubForm) {
          delete addedElement.isSubForm
          this.openSubformDialog = true
        } else {
          this.addNewSection()
        }
      }
      if (!isEmpty(movedElement)) {
        this.activeSectionIndex = movedElementIndex
        this.activeSection = movedElement
        this.resetActiveFields()
        this.allowUpdateProp = true
        this.debounceSaveRecord()
      }
    },
    updateSubFormSection(form) {
      let { id } = form
      let { activeSection } = this
      if (!isEmpty(activeSection)) {
        this.allowUpdateProp = false
        activeSection.subFormId = id
        activeSection.sectionType = 2
      }
      this.addNewSection().then(() => {
        this.allowUpdateProp = true
      })
    },
    saveDefaultValue(fieldObj) {
      this.$set(this, 'activeField', fieldObj)
    },
    addNewSection() {
      this.isFieldPropertiesLoading = true
      return this.saveFormRecord({})
        .then(() => (this.isFieldPropertiesLoading = false))
        .catch(() => (this.isFieldPropertiesLoading = false))
    },
    addLookupField(element, isLocationField = false) {
      this.isLocationField = isLocationField
      this.openLookupDialog = true
      this.lookupField = element
    },
    // addLocationField(element){
    //   this.isLocationField = true
    //   this.openLookupDialog = true
    //   this.lookupField = element
    // },
    addDefaultField(element) {
      this.openFieldNamePropDialog = true
      this.defaultField = element
    },
    addMultiLineField(element) {
      this.openMultiLinePropDialog = true
      this.multiLineField = element
    },
    addNotesField(element) {
      let { columnIndex, rowIndex } = element
      this.isFieldPropertiesLoading = true
      this.saveFormRecord({})
        .then(sections => {
          if (
            !isEmpty(sections) &&
            !isEmpty(columnIndex) &&
            !isEmpty(rowIndex)
          ) {
            let selectedSection = sections[rowIndex] || {}
            let { fields } = selectedSection
            let selectedField = fields[columnIndex] || {}
            this.resetActiveSection()
            this.activeField = selectedField
          }
        })
        .finally(() => (this.isFieldPropertiesLoading = false))
    },
    addNewDefaultField(element) {
      let { moduleName } = this
      let url = '/v2/modules/fields/add'
      let data = {
        fieldJson: this.serializeFields(
          element,
          Constants.ADD_FIELDS_RESOURCE_PROPS
        ),
        moduleName,
      }
      this.isFieldPropertiesLoading = true
      this.isSaving = true
      http
        .post(url, data)
        .then(({ data: { message, responseCode, result = {} } }) => {
          if (responseCode === 0) {
            let { field = {} } = result
            element.name = field.name
            element.displayName = field.displayName
            element.fieldId = field.fieldId
            return this.saveFormRecord({}).then(sections => {
              let activeElement = this.findSelectedField(sections, element)
              this.resetActiveSection()
              this.activeField = activeElement
              this.isFieldPropertiesLoading = false
              this.isSaving = false
              this.openLookupDialog = false
              this.openMultiLinePropDialog = false
              this.openFieldNamePropDialog = false
            })
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
          this.isFieldPropertiesLoading = false
          this.isSaving = false
        })
    },
    deserializeFormData(result) {
      let { activeField, activeSection, activeSectionIndex } = this
      let {
        form: { sections = [] },
      } = result
      let deserializedSection = this.$set(
        this,
        'sections',
        this.deserializeSectionData(sections)
      )
      if (!isEmpty(activeField)) {
        this.activeField = this.findSelectedField(
          deserializedSection,
          activeField
        )
      } else if (!isEmpty(activeSection) && !isEmpty(activeSectionIndex)) {
        this.activeSection = deserializedSection[activeSectionIndex]
      }
      return deserializedSection
    },
    updatePhotoField(field) {
      let { id, hideField } = field
      let url = 'v2/forms/fields/update'
      let data = {
        formField: {
          id,
          hideField,
        },
      }
      this.$http
        .post(url, data)
        .then(({ data: { message, responseCode } }) => {
          if (responseCode !== 0) {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
    },
    canShowSubFormDefaultDialog() {
      this.$set(this, 'openSubformDefaultDialog', true)
      this.$set(this, 'allowUpdateProp', false)
    },
    closeFieldNamePropDialog(element) {
      this.openFieldNamePropDialog = false
      let { rowIndex, columnIndex } = element
      if (!isEmpty(rowIndex) && !isEmpty(columnIndex)) {
        this.removeField(element, rowIndex, columnIndex)
      }
    },
    removeCurrencyFieldForSystemModule(custom) {
      let index = (this.defaultFields || []).findIndex(
        fld => fld.displayTypeEnum === 'MULTI_CURRENCY'
      )

      let currencySupportedModules = [
        'workorder',
        'purchaserequest',
        'purchaseorder',
        'quote',
        'budget',
      ]
      let removeCurrencyField =
        !currencySupportedModules.includes(this.moduleName) && !custom
      if (index != -1 && removeCurrencyField) {
        this.defaultFields.splice(index, 1)
      }
    },
  },
}
</script>
<style scoped>
/* Drag drop placeholder styles for fields, sections */
.ghost-class-top,
.ghost-class,
.ghost-class-sections {
  margin: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 0 1 50%;
}
.ghost-class:before,
.ghost-class-top:before,
.ghost-class-sections:before {
  content: '';
  border: 1px dashed #9ed7de;
  height: 40px;
  display: block;
  background: #f9feff;
}
.ghost-class-sections:before {
  position: relative;
  margin: 0 10px 5px 10px;
}
.ghost-class-top:before {
  margin-top: 20px;
}
.ghost-class *,
.ghost-class-top *,
.ghost-class-sections * {
  display: none;
}
.el-col.el-col-24.ghost-class-sections {
  float: none;
}
.section-container.position-relative.ghost-class-sections {
  border: none;
}
.disable-hover .form-label-container {
  pointer-events: none !important;
}
.layout-icon,
.delete-icon {
  position: absolute;
  z-index: 1000;
  cursor: pointer;
  width: 15px;
}
.delete-icon {
  top: 17px;
  right: -18px;
  width: 15px;
}
.layout-icon {
  top: 15px;
  right: 12px;
  width: 35px;
}
.field-icons {
  width: 15px;
  height: 15px;
}
.form-builder-main-form {
  flex: 1;
  overflow: scroll;
}
.fc-input-full-border-textarea .el-textarea__inner {
  min-height: 40px;
}
.setting-header2 {
  box-shadow: 0 2px 12px 0 rgba(155, 157, 180, 0.1);
  background-color: #fff;
  border-color: #e6ecf3;
  border-width: 0 0 1px 0px;
  border-style: solid;
}
.setting-header2 >>> .el-tabs .el-tabs__header {
  margin: 0px;
}
.section-container {
  border: 1px dashed #9ed7de;
}
.section-container.border-none {
  border: none;
}
.form-label-container .delete-icon,
.form-label-container .layout-icon {
  display: none;
}
.form-label-container:hover .delete-icon,
.form-label-container:hover .layout-icon {
  display: block;
}
.section-header {
  flex: 1 1 100%;
  border-bottom: 1px dashed #9ed7de;
  margin-left: -10px;
  margin-right: -10px;
}
.section-header .section-icon {
  vertical-align: middle;
  margin-left: 20px;
  width: 17px;
  height: 17px;
}
.section-header .section-name {
  display: inline-flex;
  margin-left: 15px;
  margin-bottom: 10px;
}
.setting-form-title:hover {
  cursor: pointer;
}
.photo-field {
  margin: -21px -11px -16px;
  padding: 20px;
  border: 1px dashed #dbe0e1;
}
.photo-field.hide-field {
  border: 1px dashed #9ed7de;
}
.avatar-field >>> .el-form-item__content,
.photo-field >>> .el-form-item__content,
.notes-field >>> .el-form-item__content {
  margin-left: 0px !important;
}
.builder-action-config-portal {
  position: absolute;
  top: 75px;
  right: 15px;
  z-index: 10;
}
</style>
