<template>
  <el-dialog
    v-if="dialogVisible"
    title="Actions"
    :visible.sync="dialogVisible"
    width="55%"
    :before-close="handleClose"
    class="fc-dialog-center-container"
    :append-to-body="true"
  >
    <div class="height350 overflow-y-scroll pB50">
      <el-form
        ref="ruleAction-form"
        label-width="180px"
        label-position="top"
        class
        v-if="selectedActionType !== 'script'"
      >
        <div
          v-if="
            !$validation.isEmpty(selectedActionType) &&
              selectedActionType === 'set'
          "
        >
          <div
            v-for="(formRuleAction, index) in actionList[5]
              .formRuleActionFieldsContext"
            :key="index"
          >
            <el-row class="mB10 visibility-visible-actions pT10 pB10 pL10">
              <el-col :span="2">
                <div class="criteria-alphabet-block pT6">
                  <div class="alphabet-circle">{{ index + 1 }}</div>
                </div>
              </el-col>
              <el-col :span="7">
                <el-select
                  v-model="formRuleAction.formFieldId"
                  :placeholder="'Please select the fields'"
                  class="fc-input-full-border2 width85"
                  filterable
                  @change="
                    setSelectedField(formRuleAction, formRuleAction.formFieldId)
                  "
                  collapse
                >
                  <el-option
                    v-for="field in selectedFieldsList"
                    :key="field.id"
                    :label="field.displayName"
                    :value="field.id"
                  ></el-option>
                </el-select>
              </el-col>
              <el-col :span="5">
                <el-select
                  v-if="!$validation.isEmpty(formRuleAction.formFieldId)"
                  v-model="showLookupForSetField[formRuleAction.formFieldId]"
                  @change="
                    toggleLookupFields(
                      formRuleAction.formFieldId,
                      showLookupForSetField[formRuleAction.formFieldId]
                    )
                  "
                  class="fc-input-full-border2 width80"
                >
                  <el-option
                    v-for="(valueType, idx) in valueTypes"
                    :key="idx"
                    :label="valueType"
                    :value="idx"
                  ></el-option>
                </el-select>
              </el-col>
              <el-col :span="6">
                <PlaceHolderComponent
                  v-if="
                    !$validation.isEmpty(formRuleAction.fieldObj) &&
                      !$validation.isEmpty(
                        showLookupForSetField[formRuleAction.formFieldId]
                      ) &&
                      showLookupForSetField[formRuleAction.formFieldId] ===
                        '1' &&
                      canShowPlaceholderComponent(formRuleAction)
                  "
                  v-model="formRuleAction.actionMeta"
                  :formId="selectedFormObj.id"
                  :isSubForm="isSubForm"
                  :selectedFormObj="selectedFormObj"
                  :parentForm="parentForm"
                >
                </PlaceHolderComponent>
                <ValueChooser
                  v-else-if="
                    !$validation.isEmpty(formRuleAction.fieldObj) &&
                      canShowPlaceholderComponent(formRuleAction)
                  "
                  :fieldObj="formRuleAction.fieldObj"
                  :fieldName="formRuleAction.fieldObj.name"
                  :displayTypeEnum="formRuleAction.fieldObj.displayTypeEnum"
                  :dataTypeEnum="
                    !$validation.isEmpty(formRuleAction.fieldObj.field) &&
                    !$validation.isEmpty(
                      formRuleAction.fieldObj.field.dataTypeEnum
                    )
                      ? formRuleAction.fieldObj.field.dataTypeEnum
                      : 'LOOKUP'
                  "
                  :value.sync="formRuleAction.actionMeta"
                ></ValueChooser>
              </el-col>
              <el-col
                :span="2"
                style="padding-top: 9px;padding-left: 10px;padding-right: 0;"
                class="visibility-hide-actions"
              >
                <!-- v-bind:class="index === 0 ? 'delete-icon pointer mL5' : 'delete-icon pointer'" -->
                <img
                  v-if="!$validation.isEmpty(formRuleAction.actionMeta)"
                  src="~assets/add-icon.svg"
                  style="height:17px;width:17px;margin-right:7px;"
                  class="delete-icon pointer"
                  @click="addRow(5)"
                />
                <img
                  v-if="
                    actionList[5].formRuleActionFieldsContext.length > 1 &&
                      !(actionList[5].formRuleActionFieldsContext === 1) &&
                      (!$validation.isEmpty(formRuleAction.actionMeta) ||
                        !$validation.isEmpty(formRuleAction.fieldObj))
                  "
                  src="~assets/remove-icon.svg"
                  style="height:17px;width:17px;"
                  class="delete-icon pointer"
                  @click="deleteRow(index, 5)"
                />
              </el-col>
            </el-row>
          </div>
        </div>

        <div
          v-if="
            !$validation.isEmpty(selectedActionType) &&
              selectedActionType === 'setFilters'
          "
        >
          <div
            v-if="$validation.isEmpty(selectedFieldsList)"
            style="position: absolute;
            left: 40%;
             top: 42%;"
          >
            {{ $t('setup.setup.no_picklist_fields_available') }}
          </div>
          <div v-else>
            <div
              v-for="(formRuleAction, index) in actionList[6]
                .formRuleActionFieldsContext"
              :key="index"
            >
              <el-row class="mB10 visibility-visible-actions pT10 pB10 pL10">
                <el-col :span="2">
                  <div class="criteria-alphabet-block pT6">
                    <div class="alphabet-circle">{{ index + 1 }}</div>
                  </div>
                </el-col>
                <el-col :span="9">
                  <el-select
                    v-model="formRuleAction.formFieldId"
                    :placeholder="'Please select the fields'"
                    class="fc-input-full-border2 width90"
                    filterable
                    @change="
                      setSelectedField(
                        formRuleAction,
                        formRuleAction.formFieldId
                      )
                    "
                    collapse
                  >
                    <el-option
                      v-for="field in selectedFieldsList"
                      :key="field.id"
                      :label="field.displayName"
                      :value="field.id"
                    ></el-option>
                  </el-select>
                </el-col>
                <el-col
                  :span="10"
                  v-if="
                    !$validation.isEmpty(formRuleAction.fieldObj) &&
                      !$validation.isEmpty(formRuleAction.fieldObj.field)
                  "
                >
                  <el-select
                    v-model="formRuleAction.values"
                    multiple
                    collapse-tags
                    filterable
                    placeholder="Select Hide Options"
                    class="fc-input-full-border2 width80"
                  >
                    <el-option
                      v-for="(enumValue, index) in formRuleAction.fieldObj.field
                        .enumMap"
                      :key="index"
                      :label="enumValue"
                      :value="parseInt(index)"
                    ></el-option>
                  </el-select>
                </el-col>
                <el-col
                  :span="3"
                  style="padding-top: 9px;padding-left: 10px;padding-right: 0;"
                  class="visibility-hide-actions"
                >
                  <img
                    v-if="!$validation.isEmpty(formRuleAction.values)"
                    src="~assets/add-icon.svg"
                    style="height:18px;width:18px;margin-left: 32px;"
                    class="delete-icon pointer"
                    @click="addRow(6)"
                  />
                  <img
                    v-if="
                      actionList[6].formRuleActionFieldsContext.length > 1 &&
                        !(actionList[6].formRuleActionFieldsContext === 1) &&
                        (!$validation.isEmpty(formRuleAction.values) ||
                          !$validation.isEmpty(formRuleAction.fieldObj))
                    "
                    src="~assets/remove-icon.svg"
                    style="height:18px;width:18px;margin-right: 3px;"
                    class="delete-icon pointer"
                    @click="deleteRow(index, 6)"
                  />
                </el-col>
              </el-row>
            </div>
          </div>
        </div>

        <el-row
          class="mB10"
          v-else-if="
            !$validation.isEmpty(selectedActionType) &&
              !$validation.isEmpty(selectedFormObj) &&
              !$validation.isEmpty(selectedFormObj.fields) &&
              selectedActionType === 'showHide'
          "
        >
          <el-col :span="20">
            <el-form-item prop="type" label="Show Fields" class="mB10">
              <el-select
                v-model="actionModelValues.showAction"
                :placeholder="'Please select the fields'"
                class="fc-input-full-border2 width100"
                filterable
                multiple
                @change="
                  setSelectedFormFieldObj(
                    actionList,
                    1,
                    actionModelValues.showAction
                  )
                "
                collapse
              >
                <el-option
                  v-for="field in selectedFormObj.fields"
                  :key="field.id"
                  :label="field.displayName"
                  :value="field.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="20">
            <el-form-item prop="type" label="Hide Fields" class="mB10">
              <el-select
                v-model="actionModelValues.hidAction"
                :placeholder="'Please select the fields'"
                class="fc-input-full-border2 width100"
                filterable
                multiple
                @change="
                  setSelectedFormFieldObj(
                    actionList,
                    2,
                    actionModelValues.hidAction
                  )
                "
                collapse
              >
                <el-option
                  v-for="field in selectedFormObj.fields"
                  :key="field.id"
                  :label="field.displayName"
                  :value="field.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row
          class="mB10"
          v-else-if="
            !$validation.isEmpty(selectedActionType) &&
              !$validation.isEmpty(selectedFormObj) &&
              !$validation.isEmpty(selectedFormObj.fields) &&
              selectedActionType === 'enableDisable'
          "
        >
          <el-col :span="20">
            <el-form-item prop="type" label="Enable Fields" class="mB10">
              <el-select
                v-model="actionModelValues.enableAction"
                :placeholder="'Please select the fields'"
                class="fc-input-full-border2 width100"
                filterable
                multiple
                @change="
                  setSelectedFormFieldObj(
                    actionList,
                    3,
                    actionModelValues.enableAction
                  )
                "
                collapse
              >
                <el-option
                  v-for="field in selectedFormObj.fields"
                  :key="field.id"
                  :label="field.displayName"
                  :value="field.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="20">
            <el-form-item prop="type" label="Disble Fields" class="mB10">
              <el-select
                v-model="actionModelValues.disableAction"
                :placeholder="'Please select the fields'"
                class="fc-input-full-border2 width100"
                filterable
                multiple
                @change="
                  setSelectedFormFieldObj(
                    actionList,
                    4,
                    actionModelValues.disableAction
                  )
                "
                collapse
              >
                <el-option
                  v-for="field in selectedFormObj.fields"
                  :key="field.id"
                  :label="field.displayName"
                  :value="field.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row
          class="mB10"
          v-else-if="
            !$validation.isEmpty(selectedActionType) &&
              !$validation.isEmpty(selectedFormObj) &&
              !$validation.isEmpty(selectedFormObj.sections) &&
              selectedActionType === 'showHideSection'
          "
        >
          <el-col :span="20">
            <el-form-item prop="type" label="Show Section" class="mB10">
              <el-select
                v-model="actionModelValues.showSectionAction"
                :placeholder="'Please select the fields'"
                class="fc-input-full-border2 width100"
                filterable
                multiple
                @change="
                  setSelectedFormFieldObj(
                    actionList,
                    9,
                    actionModelValues.showSectionAction,
                    true
                  )
                "
                collapse
              >
                <el-option
                  v-for="section in selectedFormObj.sections"
                  :key="section.id"
                  :label="section.name"
                  :value="section.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="20">
            <el-form-item prop="type" label="Hide Section" class="mB10">
              <el-select
                v-model="actionModelValues.hidSectionAction"
                :placeholder="'Please select the fields'"
                class="fc-input-full-border2 width100"
                filterable
                multiple
                @change="
                  setSelectedFormFieldObj(
                    actionList,
                    10,
                    actionModelValues.hidSectionAction,
                    true
                  )
                "
                collapse
              >
                <el-option
                  v-for="section in selectedFormObj.sections"
                  :key="section.id"
                  :label="section.name"
                  :value="section.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row
          class="mB10"
          v-else-if="
            !$validation.isEmpty(selectedActionType) &&
              !$validation.isEmpty(selectedFormObj) &&
              !$validation.isEmpty(selectedFormObj.fields) &&
              selectedActionType === 'setRemoveMandatory'
          "
        >
          <el-col :span="20">
            <el-form-item prop="type" label="Set Fields Mandatory" class="mB10">
              <el-select
                v-model="actionModelValues.setMandatoryAction"
                :placeholder="'Please select the fields'"
                class="fc-input-full-border2 width100"
                filterable
                multiple
                @change="
                  setSelectedFormFieldObj(
                    actionList,
                    11,
                    actionModelValues.setMandatoryAction
                  )
                "
                collapse
              >
                <el-option
                  v-for="field in selectedFormObj.fields"
                  :key="field.id"
                  :label="field.displayName"
                  :value="field.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="20">
            <el-form-item
              prop="type"
              label="Remove Fields Mandatory"
              class="mB10"
            >
              <el-select
                v-model="actionModelValues.removeMandatoryAction"
                :placeholder="'Please select the fields'"
                class="fc-input-full-border2 width100"
                filterable
                multiple
                @change="
                  setSelectedFormFieldObj(
                    actionList,
                    12,
                    actionModelValues.removeMandatoryAction
                  )
                "
                collapse
              >
                <el-option
                  v-for="field in selectedFormObj.fields"
                  :key="field.id"
                  :label="field.displayName"
                  :value="field.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div v-else>
        <div class="height350 overflow-y-scroll pB50">
          <code-mirror
            :codeeditor="true"
            v-model="action[8].tempWorkflow"
          ></code-mirror>
        </div>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="handleClose" class="modal-btn-cancel">{{
        $t('alarm.alarm.cancel')
      }}</el-button>
      <el-button
        type="primary"
        @click="handleActionSave"
        class="modal-btn-save"
        >{{
          selectedActionType !== 'script' ? 'Add Fields' : 'CONFIGURE'
        }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import { isEmpty, isObject } from '@facilio/utils/validation'
import PlaceHolderComponent from 'pages/setup/form-rules/FormRulePlaceholders'
import ValueChooser from 'pages/setup/form-rules/ValueChooser'
import CodeMirror from '@/CodeMirror'

const excludePlaceHolderHash = ['GEO_LOCATION']
export default {
  name: 'NewAction',
  props: [
    'selectedFormObj',
    'selectedActionType',
    'action',
    'handleClose',
    'handleActionSave',
    'dialogVisible',
    'isSubForm',
    'parentForm',
  ],
  components: {
    ValueChooser,
    CodeMirror,
    PlaceHolderComponent,
  },
  data() {
    return {
      valueTypes: {
        1: 'Field',
        2: 'Value',
      },
      showLookupForSetField: {},
      actions: [
        {
          actionType: null,
          fieldObj: null,
          values: [],
          id: null,
          formRuleId: null,
          criteria: null,
          formRuleActionFieldsContext: [
            {
              formFieldId: null,
              actionMeta: null,
            },
          ],
        },
      ],
      actionTypes: {
        showHide: {
          SHOW_FIELD: {
            label: 'Show',
            value: 1,
          },
          HIDE_FIELD: {
            label: 'Hide',
            value: 2,
          },
        },
        enableDisable: {
          ENABLE_FIELD: {
            label: 'Enable',
            value: 3,
          },
          DISABLE_FIELD: {
            label: 'Disable',
            value: 4,
          },
        },
      },
      values: [],
      actionModelValues: {
        showAction: [],
        hidAction: [],
        enableAction: [],
        disableAction: [],
        showSectionAction: [],
        hidSectionAction: [],
        setMandatoryAction: [],
        removeMandatoryAction: [],
      },
      selectedField: null,
      rules: {},
      excludeDisplayTypes: [
        'TASKS',
        'ATTACHMENT',
        'IMAGE',
        'FILE',
        'WOASSETSPACECHOOSER',
        'SPACECHOOSER',
      ],
    }
  },
  computed: {
    actionList: {
      get() {
        return this.action
      },
      set(val) {
        this.$emit('update:value', val)
      },
    },
    ruleId() {
      return this.$route.params.id
    },
    isNew() {
      return isEmpty(this.$route.params.id)
    },
    module() {
      return this.$route.params.moduleName
    },
    selectedFieldsList() {
      let { selectedActionType, selectedFormObj, excludeDisplayTypes } = this
      if (
        !isEmpty(selectedActionType) &&
        selectedActionType === 'set' &&
        !isEmpty(selectedFormObj)
      ) {
        let fields = []
        fields = selectedFormObj.fields.filter(
          field => !excludeDisplayTypes.includes(field.displayTypeEnum)
        )
        return fields
      }
      if (
        !isEmpty(selectedActionType) &&
        selectedActionType === 'setFilters' &&
        !isEmpty(selectedFormObj)
      ) {
        let fields = []
        fields = selectedFormObj.fields.filter(field =>
          this.isPicklistTypeField(field)
        )
        return fields
      } else if (!isEmpty(selectedFormObj)) {
        return selectedFormObj.fields
      }
      return []
    },
  },
  mounted() {
    this.constructModelValues()
    this.placeHolderFieldHandlings()
  },
  watch: {},
  methods: {
    placeHolderFieldHandlings() {
      let { action } = this
      if (!isEmpty(action)) {
        let formRuleActionList = action[5].formRuleActionFieldsContext
        if (!isEmpty(formRuleActionList)) {
          for (let i = 0; i < formRuleActionList.length; i++) {
            if (!isEmpty(formRuleActionList[i].formFieldId)) {
              if (
                !isEmpty(formRuleActionList[i].actionMeta) &&
                isNaN(formRuleActionList[i].actionMeta) &&
                !isObject(formRuleActionList[i].actionMeta) &&
                formRuleActionList[i].actionMeta.startsWith('${')
              ) {
                this.$set(
                  this.showLookupForSetField,
                  formRuleActionList[i].formFieldId,
                  '1'
                )
              } else {
                this.$set(
                  this.showLookupForSetField,
                  formRuleActionList[i].formFieldId,
                  '2'
                )
              }
            }
          }
        }
      }
    },
    toggleLookupFields(key, value) {
      this.$set(this.showLookupForSetField, key, value)
    },
    isPicklistTypeField(field) {
      let { displayTypeEnum } = field || {}
      return ['SELECTBOX', 'MULTI_SELECTBOX'].includes(displayTypeEnum)
    },
    constructModelValues() {
      let { action } = this
      if (!isEmpty(action)) {
        this.actionModelValues.showAction = action[1].values
        this.actionModelValues.hidAction = action[2].values
        this.actionModelValues.enableAction = action[3].values
        this.actionModelValues.disableAction = action[4].values
        this.actionModelValues.showSectionAction = action[9].values
        this.actionModelValues.hidSectionAction = action[10].values
        this.actionModelValues.setMandatoryAction = action[11].values
        this.actionModelValues.removeMandatoryAction = action[12].values
      }
    },
    setSelectedFormFieldObj(action, actionType, values, isSection) {
      action[actionType].formRuleActionFieldsContext = []
      action[actionType].values = values
      if (!isEmpty(values)) {
        for (let i = 0; i < values.length; i++) {
          let object = {}
          if (isSection) {
            object.formSectionId = values[i]
          } else {
            object.formFieldId = values[i]
          }
          action[actionType].formRuleActionFieldsContext.push(object)
        }
      }
      this.$set(this, 'actionList', action)
      this.$emit('update:value', action)
    },
    setSelectedField(formRuleAction, fieldId) {
      if (!isEmpty(fieldId)) {
        let fieldObject = this.selectedFormObj.fields.find(
          field => field.id === fieldId
        )
        if (!isEmpty(fieldObject)) {
          formRuleAction.fieldObj = fieldObject
          formRuleAction.actionMeta = null
        } else {
          formRuleAction.fieldObj = null
          formRuleAction.actionMeta = null
        }
        // this.showLookupForSetField[fieldId] = '2'
      }
    },
    addRow(actionType) {
      this.actionList[actionType].formRuleActionFieldsContext.push({
        formFieldId: null,
        actionMeta: null,
        fieldObj: null,
        values: [],
      })
    },
    deleteRow(index, actionType) {
      this.actionList[actionType].formRuleActionFieldsContext.splice(index, 1)
    },
    canShowPlaceholderComponent(formRuleAction) {
      let { showLookupForSetField } = this
      let { fieldObj, formFieldId } = formRuleAction || {}
      let actionType = showLookupForSetField[formFieldId] || {}
      let { displayTypeEnum } = fieldObj || {}
      let canShowPlaceHolder =
        actionType === '2'
          ? true
          : !excludePlaceHolderHash.includes(displayTypeEnum)

      return canShowPlaceHolder
    },
  },
}
</script>
<style lang="scss" scoped>
.delete-commitment {
  position: absolute;
  right: 20px;
  top: 20px;
  z-index: 1;
  color: #ff0000;
}
</style>
