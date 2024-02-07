<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog"
      :before-close="closeDialog"
      style="z-index: 999999"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            <div id="policy-header" class="section-header">
              {{ isNew ? 'New Rule' : `Edit ${ruleTitle}` }}
            </div>
          </div>
        </div>
      </div>
      <div class="new-body-modal">
        <el-form
          :model="formRuleContext"
          :rules="rules"
          ref="slaPolicy-form"
          label-width="180px"
          label-position="top"
          class=""
        >
          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item prop="name" label="Rule Name" class="mB10">
                <el-input
                  v-model="formRuleContext.name"
                  @change="name => $emit('updateTitle', name)"
                  class="fc-input-full-border2"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item
                prop="description"
                :label="$t('space.sites.site_description')"
                class="mB10"
              >
                <el-input
                  type="textarea"
                  :autosize="{ minRows: 4, maxRows: 4 }"
                  class="fc-input-full-border-textarea"
                  :placeholder="$t('setup.setupLabel.add_a_decs')"
                  v-model="formRuleContext.description"
                  resize="none"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <p class="fc-modal-sub-title">
            {{ $t('maintenance._workorder.execute_on') }}
          </p>
          <p class="small-description-txt mB10">
            {{ $t('setup.setupLabel.notify_rule_executed') }}
          </p>
          <el-row class="mb10">
            <el-col :span="14">
              <el-form-item
                prop="executeType"
                :label="$t('forms.form_rule.execute')"
                class="mb10"
              >
                <el-radio-group
                  :disabled="isUpdateForm"
                  v-model="formRuleContext.executeType"
                >
                  <el-radio
                    v-for="(value, label) in executeOptions"
                    :label="value"
                    :key="value"
                    class="fc-radio-btn"
                  >
                    {{ label }}
                  </el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row class="mb10">
            <el-col :span="14">
              <el-form-item
                prop="triggerType"
                label="Trigger Type"
                class="mb10"
              >
                <el-select
                  v-model="formRuleContext.triggerType"
                  :disabled="!isNew"
                  placeholder="On Field Update"
                  class="fc-input-full-border2 width100"
                  filterable
                >
                  <el-option
                    v-for="type in triggerTypeList"
                    :key="type.value"
                    :label="type.label"
                    :value="type.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row v-if="canShowSubformField">
            <el-col :span="14">
              <el-form-item label="Subform" class="mB10">
                <el-select
                  v-model="formRuleContext.subFormId"
                  :disabled="!isNew"
                  placeholder="Select a sub form"
                  class="fc-input-full-border2 width100 fc-tag"
                  filterable
                  collapse-tags
                >
                  <el-option
                    v-for="forms in subForms"
                    :key="forms.id"
                    :label="(forms || {}).displayName"
                    :value="(forms || {}).id"
                  ></el-option> </el-select></el-form-item
            ></el-col>
          </el-row>
          <el-row class="mB10" v-if="formRuleContext.triggerType === 2">
            <el-col :span="14">
              <el-form-item prop="fieldId" label="Trigger Field" class="mB10">
                <el-select
                  v-model="formRuleContext.fieldIds"
                  :disabled="!isNew"
                  placeholder="Select the field"
                  class="fc-input-full-border2 width100 fc-tag"
                  filterable
                  multiple
                  collapse-tags
                  ><el-option-group
                    v-for="group in triggerFieldsList"
                    :key="group.label"
                    :label="group.label"
                  >
                    <el-option
                      v-for="field in group.options"
                      :key="field.id"
                      :label="(field || {}).displayName"
                      :value="(field || {}).id"
                    >
                    </el-option>
                  </el-option-group>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row class="mT30">
            <el-col :span="24">
              <div>
                <label class="fc-modal-sub-title">{{
                  $t('forms.rules.criteria')
                }}</label>
                <div class="fc-sub-title-desc">
                  {{ $t('forms.rules.specify_criteria') }}
                </div>
              </div>
              <CriteriaBuilder
                v-model="formRuleContext.criteria"
                :moduleName="module"
              />
            </el-col>
          </el-row>
          <el-row v-if="!isSubFormsEmpty">
            <el-col :span="24">
              <div>
                <label class="fc-modal-sub-title">{{
                  $t('forms.rules.subform_criteria')
                }}</label>
                <div class="fc-sub-title-desc">
                  {{ $t('forms.rules.specify_subform_criteria') }}
                </div>
              </div>
              <CriteriaBuilder
                v-model="formRuleContext.subFormCriteria"
                :moduleName="$getProperty(selectedSubForm, 'module.name')"
              />
            </el-col>
          </el-row>
        </el-form>
        <!-- new actions -->
        <div>
          <p class="subHeading-pink-txt">
            {{ $t('setup.approvalprocess.action') }}
          </p>
          <p class="small-description-txt">
            {{ $t('setup.setupLabel.set_corresponding_rule') }}
          </p>

          <ActionsConfiguredHelper
            v-if="isSubFormsEmpty"
            :name="$t('forms.rules.show_hide_fields')"
            :description="$t('forms.rules.specify_show_hide')"
            :actions="actions"
            type="SHOW_HIDE"
            :reset="() => reset('showHide')"
            :configureAction="() => showConfigure('showHide')"
          />
          <ActionsConfiguredHelper
            :name="$t('forms.rules.enable_disable_field')"
            :description="$t('forms.rules.specify_enable_disable')"
            :actions="actions"
            type="ENABLE_DISABLE"
            :reset="() => reset('enableDisable')"
            :configureAction="() => showConfigure('enableDisable')"
          />
          <ActionsConfiguredHelper
            v-if="isSubFormsEmpty"
            :name="$t('forms.rules.show_hide_section')"
            :description="$t('forms.rules.specify_show_hide_section')"
            :actions="actions"
            type="SHOW_HIDE_SECTION"
            :reset="() => reset('showHideSection')"
            :configureAction="() => showConfigure('showHideSection')"
          />
          <ActionsConfiguredHelper
            :name="$t('forms.rules.set_remove_mandatory')"
            :description="$t('forms.rules.specify_mandatory')"
            :actions="actions"
            type="SET_MANDATORY"
            :reset="() => reset('setRemoveMandatory')"
            :configureAction="() => showConfigure('setRemoveMandatory')"
          />
          <ActionsConfiguredHelper
            :name="$t('forms.rules.set_value')"
            :description="$t('forms.rules.specify_set_value')"
            :actions="actions"
            type="SET_VALUE"
            :reset="() => reset('set')"
            :configureAction="() => showConfigure('set')"
          />
          <ActionsConfiguredHelper
            :name="$t('setup.setup.set_picklist_filters')"
            :description="$t('forms.rules.specify_picklist_filter')"
            :actions="actions"
            type="SET_FILTER"
            :reset="() => reset('setFilters')"
            :configureAction="() => showConfigure('setFilters')"
          />
          <ActionsConfiguredHelper
            v-if="showWorkflow"
            :name="$t('forms.rules.execute_script')"
            :description="$t('forms.rules.specify_execute_script')"
            :actions="actions"
            type="EXECUTE_SCRIPT"
            :reset="() => reset('script')"
            :configureAction="() => showConfigure('script')"
          />
        </div>
        <!-- actions dialog -->

        <NewAction
          v-if="dialogVisible"
          :selectedFormObj="selectedSubForm || selectedForm"
          :selectedActionType="selectedActionType"
          :action.sync="actions"
          class="mB20 form-rule-actions-comp"
          :dialogVisible.sync="dialogVisible"
          :handleActionSave="handleActionSave"
          :handleClose="handleClose"
          :isSubForm="isSubForm"
          :parentForm="selectedForm"
        ></NewAction>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">{{
          $t('forms.rules.cancel')
        }}</el-button>
        <el-button
          type="primary"
          @click="save"
          :loading="saving"
          class="modal-btn-save"
          >{{ saving ? 'Saving...' : 'SAVE' }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty, isObject } from '@facilio/utils/validation'
import clone from 'lodash/clone'
import { CriteriaBuilder } from '@facilio/criteria'
import NewAction from 'pages/setup/form-rules/NewAction'
// eslint-disable-next-line @facilio/no-http
import http from 'util/http'
import ActionsConfiguredHelper from './ActionsConfiguredHelper'
import cloneDeep from 'lodash/cloneDeep'
import Constants from 'util/constant'

const scriptHeader = 'List getActions(Map formData) {\n'

export default {
  name: 'NewRule',
  props: [
    'visibility',
    'isNew',
    'formRuleId',
    'formId',
    'isSubForm',
    'isUpdateForm',
  ],

  components: {
    CriteriaBuilder,
    NewAction,
    ActionsConfiguredHelper,
  },
  data() {
    return {
      executeOptions: Constants.EXECUTE_TYPE_HASH,
      formRuleContext: {
        name: '',
        description: '',
        formId: null,
        triggerType: 2,
        executeType: Constants.EXECUTE_TYPE_HASH['Create Or Edit'],
        fieldIds: [],
        ruleType: 1,
        type: 1,
        criteria: null,
        subFormCriteria: null,
      },
      formRuleContextObj: {},
      actions: {
        1: {
          actionType: 1,
          fieldObj: null,
          values: [],
          id: null,
          formRuleId: null,
          criteria: null,
          subFormCriteria: null,
          formRuleActionFieldsContext: [
            {
              formFieldId: null,
            },
          ],
        },
        2: {
          actionType: 2,
          fieldObj: null,
          values: [],
          id: null,
          formRuleId: null,
          criteria: null,
          subFormCriteria: null,
          formRuleActionFieldsContext: [
            {
              formFieldId: null,
            },
          ],
        },
        3: {
          actionType: 3,
          fieldObj: null,
          values: [],
          id: null,
          formRuleId: null,
          criteria: null,
          subFormCriteria: null,
          formRuleActionFieldsContext: [
            {
              formFieldId: null,
            },
          ],
        },
        4: {
          actionType: 4,
          fieldObj: null,
          values: [],
          id: null,
          formRuleId: null,
          criteria: null,
          subFormCriteria: null,
          formRuleActionFieldsContext: [
            {
              formFieldId: null,
            },
          ],
        },
        5: {
          actionType: 5,
          fieldObj: null,
          values: [],
          id: null,
          formRuleId: null,
          criteria: null,
          subFormCriteria: null,
          formRuleActionFieldsContext: [
            {
              formFieldId: null,
              actionMeta: null,
              fieldObj: null,
            },
          ],
        },
        8: {
          actionType: 8,
          workflow: {
            workflowV2String: null,
            isV2Script: true,
          },
          tempWorkflow: '',
        },
        9: {
          actionType: 9,
          fieldObj: null,
          values: [],
          id: null,
          formRuleId: null,
          criteria: null,
          subFormCriteria: null,
          formRuleActionFieldsContext: [
            {
              formSectionId: null,
            },
          ],
        },
        10: {
          actionType: 10,
          fieldObj: null,
          values: [],
          id: null,
          formRuleId: null,
          criteria: null,
          subFormCriteria: null,
          formRuleActionFieldsContext: [
            {
              formSectionId: null,
            },
          ],
        },
        11: {
          actionType: 11,
          fieldObj: null,
          values: [],
          id: null,
          formRuleId: null,
          criteria: null,
          subFormCriteria: null,
          formRuleActionFieldsContext: [
            {
              formFieldId: null,
            },
          ],
        },
        12: {
          actionType: 12,
          fieldObj: null,
          values: [],
          id: null,
          formRuleId: null,
          criteria: null,
          subFormCriteria: null,
          formRuleActionFieldsContext: [
            {
              formFieldId: null,
            },
          ],
        },
        6: {
          actionType: 6,
          fieldObj: null,
          values: [],
          id: null,
          formRuleId: null,
          criteria: null,
          subFormCriteria: null,
          formRuleActionFieldsContext: [
            {
              formFieldId: null,
              actionMeta: null,
              values: [],
            },
          ],
        },
      },
      comment:
        '// `formData` will be passed to the script. Return actions as list\n\n',
      forms: [],
      rules: {},
      saving: false,
      isCriteriaRendering: false,
      selectedForm: null,
      ruleTitle: 'Form Rule',
      dialogVisible: false,
      selectedActionType: null,
      apps: [],
      isLoading: false,
    }
  },
  async created() {
    let { $store } = this
    Promise.all([$store.dispatch('loadSites'), this.fetchFormRule()])
      .then(() => {
        if (!this.isNew) this.ruleTitle = this.formRuleContext.name
      })
      .finally(() => {
        this.$nextTick(this.registerScrollHandler)
      })
  },
  mounted() {
    if (this.isUpdateForm) {
      this.formRuleContext.executeType = Constants.EXECUTE_TYPE_HASH['Edit']
    }
  },
  computed: {
    ...mapState({
      sites: state => state.sites,
    }),
    module() {
      return this.$route.params.moduleName
    },
    triggerTypeList() {
      let { isSubForm } = this || {}
      let types = cloneDeep(this.$constants.FormRuleTriggerType)
      if (isSubForm) {
        delete types['FORM_ON_LOAD']
        return types
      } else {
        delete types['ADD_DELETE_SUBFORM']
        return types
      }
    },
    showWorkflow() {
      if (this.$route.query.showScript) {
        return true
      }
      if (!this.isNew && this.formRuleContext && this.formRuleContext.actions) {
        return this.formRuleContext.actions.some(
          action => action.actionType === 8
        )
      }
      return false
    },
    canShowSubformField() {
      let { subForms, isNew, formRuleContext, isSubForm } = this
      if (!isNew) {
        return !isEmpty(formRuleContext.subFormId) && isSubForm
      } else {
        return !isEmpty(subForms) && isSubForm
      }
    },
    subForms() {
      let { selectedForm } = this
      let { sections } = selectedForm || {}
      let subForms = (sections || []).filter(
        section => !isEmpty(section.subFormId)
      )
      subForms = (subForms || []).map(form => form.subForm)
      return subForms
    },
    selectedSubForm() {
      let { formRuleContext, subForms } = this
      let { subFormId } = formRuleContext || {}
      if (!isEmpty(subFormId) && !isEmpty(subForms)) {
        let currentSubForm = subForms.find(form => form.id === subFormId)
        return currentSubForm
      } else {
        return null
      }
    },
    isSubFormsEmpty() {
      let { selectedSubForm } = this
      return isEmpty(selectedSubForm)
    },
    triggerFieldsList() {
      let { selectedForm, selectedSubForm, isSubFormsEmpty } = this
      let { fields } = selectedForm || {}
      let fieldsList = [{ label: 'Form fields', options: fields }]

      if (!isSubFormsEmpty) {
        let { fields: subFormFields = [] } = selectedSubForm || {}
        fieldsList = [
          ...fieldsList,
          { label: 'Subform fields', options: subFormFields },
        ]
      }

      return fieldsList
    },
  },
  methods: {
    reset(actionType) {
      let action = {
        actionType: null,
        fieldObj: null,
        values: [],
        id: null,
        formRuleId: null,
        criteria: null,
        formRuleActionFieldsContext: [
          {
            formFieldId: null,
          },
        ],
      }
      if (actionType === 'showHide') {
        let showAction = clone(action)
        showAction.actionType = 1
        this.$set(this.actions, '1', showAction)
        let hideAction = clone(action)
        hideAction.actionType = 2
        this.$set(this.actions, '2', hideAction)
      } else if (actionType === 'enableDisable') {
        let enableAction = clone(action)
        enableAction.actionType = 3
        this.$set(this.actions, '3', enableAction)
        let disableAction = clone(action)
        disableAction.actionType = 4
        this.$set(this.actions, '4', disableAction)
      } else if (actionType === 'showHideSection') {
        let showSectionAction = clone(action)
        showSectionAction.actionType = 9
        this.$set(this.actions, '9', showSectionAction)
        let hideSectionAction = clone(action)
        hideSectionAction.actionType = 10
        this.$set(this.actions, '10', hideSectionAction)
      } else if (actionType === 'setRemoveMandatory') {
        let setMandatoryAction = clone(action)
        setMandatoryAction.actionType = 11
        this.$set(this.actions, '11', setMandatoryAction)
        let removeMandatoryAction = clone(action)
        removeMandatoryAction.actionType = 12
        this.$set(this.actions, '12', removeMandatoryAction)
      } else if (actionType === 'setFilters') {
        let setAction = [
          {
            formFieldId: null,
            actionMeta: null,
            values: [],
          },
        ]
        this.actions[6].formRuleActionFieldsContext = setAction
        this.$set(this.actions[6], 'formRuleActionFieldsContext', setAction)
      } else if (actionType === 'set') {
        let setAction = [
          {
            formFieldId: null,
            actionMeta: null,
            fieldObj: null,
          },
        ]
        this.actions[5].formRuleActionFieldsContext = setAction
        this.$set(this.actions[5], 'formRuleActionFieldsContext', setAction)
      } else if (actionType === 'script') {
        this.actions[8].workflow.workflowV2String = null
        this.actions[8].tempWorkflow = ''
      }
    },
    fetchFormRule() {
      let { isNew, formId } = this
      if (isNew) {
        this.formRuleContext.formId = Number(formId)
        this.fetchFormDetails()
      }
      if (this.formRuleId)
        return http
          .post(`v2/form/rule/get`, {
            formRuleContext: {
              id: parseInt(this.formRuleId),
            },
          })
          .then(({ data }) => {
            if (data.responseCode === 0) {
              this.formRuleContextObj = data.result.formRuleResultJSON
              this.formRuleContext = data.result.formRuleResultJSON
              this.formRuleContext.fieldIds = data.result.formRuleResultJSON
                .triggerFields
                ? data.result.formRuleResultJSON.triggerFields.map(
                    triggerField => {
                      const { fieldId } = triggerField
                      return fieldId
                    }
                  )
                : []

              this.fetchFormDetails()
            } else {
              throw new Error()
            }
          })
          .catch(() => {
            this.$message.error('Could not load form rule')
          })
      else return Promise.resolve()
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    fetchFormDetails() {
      let { formRuleContext, forms } = this
      let selectedForm = forms.find(form => form.id === formRuleContext.formId)
      let { formId } = formRuleContext || {}
      let { name } = selectedForm || {}
      let url =
        formId === -1
          ? `/v2/forms/workorder?formName=${name}`
          : `/v2/forms/workorder?formId=${formId}`
      // eslint-disable-next-line @facilio/no-http
      this.$http
        .get(url)
        .then(({ data: { message, responseCode, result = {} } }) => {
          if (responseCode === 0) {
            let { form } = result
            if (!isEmpty(form)) {
              this.selectedForm = form
              let { sections = [] } = form || {}
              let subFormSection = sections.find(
                section => !isEmpty((section || {}).subForm)
              )
              if (!isEmpty(subFormSection)) {
                let { formRuleContext, isSubForm } = this || {}
                if (isSubForm) {
                  let subFormId = this.$getProperty(
                    subFormSection,
                    'subForm.id'
                  )
                  this.formRuleContext = { ...formRuleContext, subFormId }
                }
              }
              if (!this.isNew) {
                this.deserializeRuleData()
              }
            }
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
    },
    deserializeRuleData() {
      let values = []
      let { selectedForm, selectedSubForm } = this
      if (!isEmpty(selectedSubForm)) selectedForm = selectedSubForm
      if (!isEmpty(this.formRuleContext.actions) && !isEmpty(selectedForm)) {
        for (let i = 0; i < this.formRuleContext.actions.length; i++) {
          values = []
          if (this.formRuleContext.actions[i].formRuleActionFieldsContext) {
            for (
              let j = 0;
              j <
              this.formRuleContext.actions[i].formRuleActionFieldsContext
                .length;
              j++
            ) {
              let fieldObject = {}
              if ([5, 6].includes(this.formRuleContext.actions[i].actionType)) {
                fieldObject = selectedForm.fields.find(
                  field =>
                    field.id ===
                    this.formRuleContext.actions[i].formRuleActionFieldsContext[
                      j
                    ].formFieldId
                )
                this.$set(
                  this.formRuleContext.actions[i].formRuleActionFieldsContext[
                    j
                  ],
                  'fieldObj',
                  fieldObject
                )
              }
              if (this.formRuleContext.actions[i].actionType === 5) {
                let dataTypeEnum =
                  !isEmpty(fieldObject.field) &&
                  !isEmpty(fieldObject.field.dataTypeEnum)
                    ? fieldObject.field.dataTypeEnum
                    : 'LOOKUP'

                let displayTypeEnum = fieldObject.displayTypeEnum
                let valueObj = JSON.parse(
                  this.formRuleContext.actions[i].formRuleActionFieldsContext[j]
                    .actionMeta
                )
                let value
                if (
                  ['STRING', 'BOOLEAN', 'DECIMAL'].includes(dataTypeEnum) ||
                  (!isEmpty(valueObj) &&
                    !isEmpty(valueObj.setValue) &&
                    !isObject(valueObj.setValue) &&
                    valueObj.setValue.startsWith('${'))
                ) {
                  value = null
                  value = valueObj.setValue
                } else if (['DATE', 'DATETIME'].includes(displayTypeEnum)) {
                  value = null
                  value = valueObj.setValue
                } else if (displayTypeEnum === 'DATERANGE') {
                  value = []
                  value = valueObj.setValue.split(',')
                } else if (['TIME'].includes(displayTypeEnum)) {
                  value = valueObj.setValue
                } else if (displayTypeEnum === 'TEAMSTAFFASSIGNMENT') {
                  value = {}
                  value = valueObj.setValue
                } else if (['CURRENCY'].includes(displayTypeEnum)) {
                  value = valueObj.setValue
                } else if (['MULTI_CURRENCY'].includes(displayTypeEnum)) {
                  value = valueObj.setValue
                } else if (['GEO_LOCATION'].includes(displayTypeEnum)) {
                  value = valueObj.setValue
                } else {
                  value = parseInt(valueObj.setValue)
                }
                this.formRuleContext.actions[i].formRuleActionFieldsContext[
                  j
                ].actionMeta = value
              } else if (this.formRuleContext.actions[i].actionType === 6) {
                let valueObj = JSON.parse(
                  this.formRuleContext.actions[i].formRuleActionFieldsContext[j]
                    .actionMeta
                )
                let values = valueObj.values
                this.$set(
                  this.formRuleContext.actions[i].formRuleActionFieldsContext[
                    j
                  ],
                  'values',
                  values
                )
              } else if (
                [9, 10].includes(this.formRuleContext.actions[i].actionType)
              ) {
                values.push(
                  this.formRuleContext.actions[i].formRuleActionFieldsContext[j]
                    .formSectionId
                )
              } else {
                values.push(
                  this.formRuleContext.actions[i].formRuleActionFieldsContext[j]
                    .formFieldId
                )
              }
            }
          }

          if (this.formRuleContext.actions[i].actionType !== 5) {
            this.formRuleContext.actions[i].values = values
          }

          this.actions[
            this.formRuleContext.actions[i].actionType
          ] = this.formRuleContext.actions[i]
        }
      }
    },
    updateCriteria(value, criteriaType) {
      this.$set(this.formRuleContext, `${criteriaType}`, value)
    },
    save() {
      let { actions } = this
      let formRuleContext = this.serialize()
      let actionsList = []
      for (let key in actions) {
        let action = actions[key]
        let { actionType, formRuleActionFieldsContext, workflow, id } = action
        if (!this.isNew) {
          this.formRuleContextObj.actions.forEach(action => {
            if (action.actionType === actionType) {
              id = action.id
            }
          })
        }

        if (actionType === 8) {
          if (workflow.workflowV2String) {
            actionsList.push({ actionType, workflow })
          }
        } else {
          if (actionType === 5) {
            formRuleActionFieldsContext = formRuleActionFieldsContext.filter(
              action => !isEmpty(action.actionMeta)
            )
          }
          if (actionType === 6) {
            formRuleActionFieldsContext = formRuleActionFieldsContext.filter(
              action => !isEmpty(action.values)
            )
          }
          if (
            formRuleActionFieldsContext.length > 0 &&
            (!isEmpty(formRuleActionFieldsContext[0].formFieldId) ||
              !isEmpty(formRuleActionFieldsContext[0].formSectionId))
          ) {
            let data = {
              actionType,
            }
            let actionFormRuleList = []
            for (let i = 0; i < formRuleActionFieldsContext.length; i++) {
              if (actionType === 5) {
                let actionMetaJson
                if (
                  formRuleActionFieldsContext[i].fieldObj.displayTypeEnum ===
                  'TEAMSTAFFASSIGNMENT'
                ) {
                  let teamStaffValue = {
                    setValue: formRuleActionFieldsContext[i].actionMeta,
                  }
                  actionMetaJson = JSON.stringify(teamStaffValue)
                } else if (
                  formRuleActionFieldsContext[i].fieldObj.displayTypeEnum ===
                  'CURRENCY'
                ) {
                  let currency = {
                    setValue: formRuleActionFieldsContext[i].actionMeta,
                  }
                  actionMetaJson = JSON.stringify(currency)
                } else if (
                  formRuleActionFieldsContext[i].fieldObj.displayTypeEnum ===
                  'GEO_LOCATION'
                ) {
                  let location = {
                    setValue: formRuleActionFieldsContext[i].actionMeta,
                  }
                  actionMetaJson = JSON.stringify(location)
                } else {
                  actionMetaJson = `{"setValue": "${formRuleActionFieldsContext[
                    i
                  ].actionMeta.toString()}"}`
                }
                formRuleActionFieldsContext[i].actionMeta = actionMetaJson
                delete formRuleActionFieldsContext[i]['fieldObj']
              } else if (actionType === 6) {
                let actionMetaJson = `{"show": false,"values": [${formRuleActionFieldsContext[i].values}]}`
                formRuleActionFieldsContext[i].actionMeta = actionMetaJson
                delete formRuleActionFieldsContext[i]['values']
              } else {
                if (!this.isNew) {
                  let {
                    formFieldId,
                    formSectionId,
                    formRuleActionId,
                    id,
                  } = formRuleActionFieldsContext[i]

                  let dataAction = {
                    formFieldId,
                    formSectionId,
                    formRuleActionId,
                    id,
                  }
                  actionFormRuleList.push(dataAction)
                }
              }
            }
            if (!this.isNew && ![5, 6].includes(actionType)) {
              formRuleActionFieldsContext = actionFormRuleList
            }
            data.formRuleActionFieldsContext = formRuleActionFieldsContext
            if (!this.isNew) data.id = id
            if (!this.isNew) data.formRuleId = this.formRuleId
            if (!isEmpty(formRuleActionFieldsContext)) actionsList.push(data)
          }
        }
      }
      if (!isEmpty(actionsList)) {
        this.$set(formRuleContext, 'actions', actionsList)
      }
      let url = this.isNew ? `v2/form/rule/add` : `v2/form/rule/update`
      this.saving = true
      return http
        .post(url, {
          formRuleContext,
        })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.$message.success('Form Rule saved')
            this.$emit('saved', data.result.formRuleResultJSON)
            this.closeDialog()
            this.$nextTick(this.goBack)
          } else {
            throw new Error()
          }
        })
        .catch(() => {
          this.$message.error('Could not save form rule')
        })
        .finally(() => (this.saving = false))
    },
    serialize() {
      let { formRuleId } = this
      let {
        name,
        description,
        formId,
        triggerType,
        executeType,
        fieldIds,
        ruleType,
        type,
        criteria,
        criteriaId = null,
        subFormId,
        subFormCriteria,
      } = clone(this.formRuleContext)
      let data = {
        name,
        description,
        formId,
        triggerType,
        executeType,
        ruleType,
        type,
        subFormId,
      }
      if (triggerType === 2) {
        data.triggerFields = fieldIds.map(fieldId => {
          return {
            fieldId,
          }
        })
      }
      if (!isEmpty(criteria)) data.criteria = this.serializeCriteria(criteria)
      if (!isEmpty(subFormCriteria))
        data.subFormCriteria = this.serializeCriteria(subFormCriteria)
      if (!this.isNew) data.id = formRuleId
      if (!isEmpty(criteria) && !isEmpty(criteriaId))
        data.criteriaId = criteriaId

      return data
    },
    showConfigure(actionType) {
      this.selectedActionType = actionType
      this.dialogVisible = true

      if (actionType === 'script' && !this.actions[8].tempWorkflow) {
        if (this.actions[8].workflow.workflowV2String) {
          this.actions[8].tempWorkflow = this.actions[8].workflow.workflowV2String
            .replace(scriptHeader, '')
            .slice(0, -1)
        } else {
          this.actions[8].tempWorkflow = this.comment
        }
      }
    },
    handleActionSave() {
      // TODO change the data only if saved. If clicked cancel, reset the data
      let { workflow, tempWorkflow } = this.actions[8]
      if (this.selectedActionType === 'script' && tempWorkflow) {
        {
          workflow.workflowV2String = scriptHeader + tempWorkflow + '\n}'
        }
      }

      this.handleClose()
    },
    handleClose() {
      this.dialogVisible = false
    },
    serializeCriteria(criteria) {
      for (let condition of Object.keys(criteria.conditions)) {
        let hasValidFieldName =
          // eslint-disable-next-line no-prototype-builtins
          criteria.conditions[condition].hasOwnProperty('fieldName') &&
          !isEmpty(criteria.conditions[condition].fieldName)
        if (!hasValidFieldName) {
          delete criteria.conditions[condition]
        } else {
          let discardKeys = [
            'valueArray',
            'operatorsDataType',
            'operatorLabel',
            'operator',
          ]

          discardKeys.forEach(key => {
            delete criteria.conditions[condition][key]
          })
        }
      }
      if (criteria && criteria.conditions) {
        if (Object.keys(criteria.conditions).length === 0) {
          criteria = null
        }
      }
      return criteria
    },
  },
}
</script>
<style>
.configured-green {
  color: #5bc293;
}
</style>
