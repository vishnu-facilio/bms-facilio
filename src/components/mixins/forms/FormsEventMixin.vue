<script>
import { isEmpty, isFunction } from '@facilio/utils/validation'
import isString from 'lodash/isString'
import Constants from 'util/constant'
import { API } from '@facilio/api'
import {
  constructEnumFieldOptions,
  deepClean,
} from '@facilio/utils/utility-methods'
import cloneDeep from 'lodash/cloneDeep'
import DataCreationMixin from '@/mixins/DataCreationMixin'
import { isChooserTypeField } from 'util/field-utils'
import { isDropdownTypeField, isMultiLookup } from '@facilio/utils/field'
export default {
  mixins: [DataCreationMixin],

  methods: {
    fetchOnDataActionsList(triggerType = 1, params = {}) {
      let {
        form,
        formModel,
        moduleDataId,
        subFormModels,
        isEditForm,
        isEdit,
      } = this
      let { id: formId } = form || {}
      let executeType =
        isEdit || isEditForm
          ? Constants.EXECUTE_TYPE_HASH['Edit']
          : Constants.EXECUTE_TYPE_HASH['Create']
      let url = `/v2/form/rule/executeFormActionRules`
      let clonedFormModel = cloneDeep(formModel)
      let formData = this.serializeFormModel(clonedFormModel, form)
      subFormModels = this.getModifiedSubForm()
      formData = { ...formData, relations: subFormModels }
      let payload = {
        formId,
        triggerType,
        formData,
        executeType,
        ...params,
      }
      if (!isEmpty(moduleDataId) && !isEmpty(payload.formData)) {
        payload.formData.id = moduleDataId
      }
      if (formId > 0) {
        API.post(url, payload).then(({ data, error }) => {
          if (error) {
            let { message } = error
            this.$message.error(message)
          } else {
            let { formRuleResultJSON, subFormRuleResultJSON } = data
            if (!isEmpty(formRuleResultJSON)) {
              this.formRulesHandler(formRuleResultJSON)
            }

            if (!isEmpty(subFormRuleResultJSON)) {
              this.subFormRulesHandler(subFormRuleResultJSON)
            }
          }
        })
      }
    },
    fetchActionsList(field) {
      let { form, formModel, moduleDataId, subFormModels } = this
      let { id: formId } = form || {}
      let { id: formFieldId } = field
      let url = `/v2/form/rule/executeFormActionRules`
      let clonedFormModel = cloneDeep(formModel)
      let formData = this.serializeFormModel(clonedFormModel, form)
      let { isEdit, isEditForm } = this
      let executeType =
        isEdit || isEditForm
          ? Constants.EXECUTE_TYPE_HASH['Edit']
          : Constants.EXECUTE_TYPE_HASH['Create']
      subFormModels = this.getModifiedSubForm()
      formData = { ...formData, relations: subFormModels }
      let payload = {
        formId,
        executeType,
        formFieldId,
        triggerType: 2,
        formData,
      }
      if (!isEmpty(moduleDataId) && !isEmpty(payload.formData)) {
        payload.formData.id = moduleDataId
      }
      API.post(url, payload).then(({ data, error }) => {
        if (error) {
          let { message } = error
          this.$message.error(message)
        } else {
          let { formRuleResultJSON, subFormRuleResultJSON } = data
          if (!isEmpty(formRuleResultJSON)) {
            this.formRulesHandler(formRuleResultJSON)
          }

          if (!isEmpty(subFormRuleResultJSON)) {
            this.subFormRulesHandler(subFormRuleResultJSON)
          }
        }
      })
    },
    getModifiedSubForm() {
      let { subFormModels } = this || {}
      let { form } = this || {}
      let { sections } = form || {}
      let modifiedSubForm = {}
      Object.keys(subFormModels).forEach(subFormName => {
        let model = subFormModels[subFormName]
        let clonedSubFormModel = cloneDeep(model)

        let subFormSection = sections.find(section => {
          let { sectionTypeEnum, subForm } = section || {}
          let { name: sectionSubformName } = subForm || {}
          return (
            sectionTypeEnum == 'SUB_FORM' && sectionSubformName == subFormName
          )
        })
        let { subForm } = subFormSection || {}
        let subFormModel = clonedSubFormModel.map(model => {
          let modifiedModel = this.serializeFormModel(model, subForm)
          let { sub_form_action } = model || {}
          if (!isEmpty(sub_form_action)) {
            modifiedModel.sub_form_action = model['sub_form_action']
          }
          return modifiedModel
        })
        modifiedSubForm = {
          ...modifiedSubForm,
          [subFormName]: { data: subFormModel },
        }
      })
      return modifiedSubForm
    },
    serializeFormModel(formModel, form) {
      let serializedModel = {}
      if (!isEmpty(formModel)) {
        serializedModel = this.serializedData(form, formModel)
        let { subFormData } = serializedModel
        if (!isEmpty(subFormData)) {
          delete serializedModel.subFormData
        }
      }
      return deepClean(serializedModel)
    },
    formRulesHandler(rulesArr) {
      rulesArr.forEach(rule => {
        this.executeRules({ rule })
      })
    },
    subFormRulesHandler(rules) {
      // Here we get the subform sections and access the sub form wrapper
      // component to call the executeSubFormRules method inside that component.
      let { form, $refs } = this
      let { sections } = form
      if (!isEmpty(sections)) {
        sections.forEach((section, index) => {
          let { subForm, subFormId } = section || {}
          let subFormElement = $refs[`${index} ${subFormId}`]
          let { name } = subForm || {}
          if (!isEmpty(subFormElement)) {
            subFormElement[0].executeSubFormRules(rules[name])
          }
        })
      }
    },
    executeViewRelatedActions(props) {
      let { field } = props
      let { id } = field
      let { form } = this
      let { onLoadActionsFieldIds = [], initialActionsList = [] } = form
      let viewRelatedRules = []
      if (onLoadActionsFieldIds.includes(id)) {
        let rules = this.getRulesById({ initialActionsList, id })
        viewRelatedRules = this.getViewRelatedActionRules({ rules })
      }
      if (!isEmpty(viewRelatedRules)) {
        viewRelatedRules.forEach(rule => {
          this.executeRules({
            rule,
            selectedField: field,
          })
        })
      }
    },
    executeFilterRelatedActions(props) {
      let { field } = props
      let { id } = field
      let { form } = this
      let { onLoadActionsFieldIds = [], initialActionsList = [] } = form
      let rule = {}
      if (onLoadActionsFieldIds.includes(id)) {
        let rules = this.getRulesById({ initialActionsList, id })
        rule = this.getFilterActionRules({ rules })
      }
      if (!isEmpty(rule)) {
        this.executeRules({
          rule,
          selectedField: field,
          criteriaKey: 'clientCriteria',
        })
      }
    },
    executeRules(props) {
      let {
        rule,
        selectedField,
        stopRecursiveRuleExecution = false,
        criteriaKey = 'clientCriteria',
      } = props
      let { actionsMap } = this
      let { action } = rule
      let { actionName } = action || {}
      let actionHandler = (actionsMap[actionName] || {}).handler
      if (!isEmpty(actionHandler) && isFunction(actionHandler)) {
        actionHandler({
          ...rule,
          selectedField,
          stopRecursiveRuleExecution,
          criteriaKey,
        })
      }
    },
    onBlurHandler(props) {
      let { field } = props
      let { formModel } = this

      this.$emit('onBlur', {
        field,
        value: formModel[field.name],
        formModel,
      })
    },
    onChangeHandler(props) {
      let { field, isSubForm } = props
      if (isSubForm) {
        let { form, formModel, isEdit } = this
        if (isEdit) {
          let id = this.$getProperty(form, 'recordId')
          this.$emit('subFormDirtyChange', { ...formModel, id })
        }
        this.$emit('onModelChange', { formModel, field })
      } else {
        // To send events on form or field change
        this.$nextTick(() => {
          this.sendFieldChangeEvent(field)
          let { isTriggerField } = field || {}
          if (isTriggerField) {
            this.fetchActionsList(field)
          }
        })
      }
    },
    async getCurrentFormData() {
      let { form, $refs, formModel } = this
      let modelObj = formModel || {}
      let { sections = [] } = form || {}
      if (!isEmpty(sections)) {
        for (let index = 0; index < sections.length; index++) {
          let section = sections[index]
          let { subForm, subFormId } = section
          let subFormElement = $refs[`${index} ${subFormId}`]
          if (!isEmpty(subForm) && !isEmpty(subFormElement)) {
            let subFormData = await subFormElement[0].saveRecord({
              skipDeserialize: true,
              skipSubFormValidation: true,
            })
            let { moduleName, data } = subFormData
            modelObj[moduleName] = data
          }
        }
      }
      return modelObj
    },
    async sendFieldChangeEvent(field) {
      let { formModel, module } = this
      let { name } = field

      let modelObj = await this.getCurrentFormData()
      let data = {
        formData: modelObj,
        module,
      }

      let fieldData = {
        value: formModel[name],
        module,
      }
      this.sendEvent({
        eventName: 'form.changed',
        eventData: data,
      })
      this.sendEvent({
        eventName: `form.${name}.changed`,
        eventData: fieldData,
      })
    },
    async sendEvent(props) {
      let { eventName, eventData } = props
      let { creationWidgets, $refs } = this
      if (!isEmpty(creationWidgets)) {
        creationWidgets.forEach(widget => {
          let { id } = widget
          let [refElement] = $refs[`ref-${id}`]
          if (!isEmpty(refElement)) {
            refElement.sendEvent(eventName, eventData)
          }
        })
      }
    },
    handleShowHideAction(prop) {
      let { action, fieldId, selectedField } = prop
      let { actionName } = action || {}
      if (isEmpty(selectedField)) {
        selectedField = this.getSelectedField(fieldId)
      }
      if (!isEmpty(selectedField)) {
        let { hideField } = selectedField
        hideField = actionName === 'hide'
        this.$set(selectedField, 'hideField', hideField)
      }
    },
    handleEnableDisableAction(prop) {
      let { action, fieldId, selectedField } = prop
      let { actionName } = action || {}
      if (isEmpty(selectedField)) {
        selectedField = this.getSelectedField(fieldId)
      }
      if (!isEmpty(selectedField)) {
        let { isDisabled } = selectedField
        isDisabled = actionName === 'disable'
        this.$set(selectedField, 'isDisabled', isDisabled)
      }
    },
    handleSetRemoveMandatory(prop) {
      let { action, fieldId, selectedField } = prop
      let { actionName } = action || {}
      if (isEmpty(selectedField)) {
        selectedField = this.getSelectedField(fieldId)
      }
      if (!isEmpty(selectedField)) {
        let { required } = selectedField
        required = actionName === 'setMandatory'
        if (required) {
          this.setupValidation(selectedField)
        } else {
          this.removeValidation(selectedField)
        }
        this.$set(selectedField, 'required', required)
      }
    },
    removeValidation(field) {
      let { rules = [] } = this
      let { name } = field
      if (!isEmpty(rules[name])) {
        delete rules[name]
      }
    },
    handleShowHideSection(prop) {
      let { action, sectionId, selectedSection } = prop
      let { actionName } = action || {}
      if (!isEmpty(sectionId)) {
        selectedSection = this.getSelectedSection(sectionId)
      }
      if (!isEmpty(selectedSection)) {
        let { hideSection } = selectedSection
        hideSection = actionName === 'hideSection'
        this.$set(selectedSection, 'hideSection', hideSection)
      }
    },
    handleSetValueAction(prop) {
      let { action, fieldId, selectedField, stopRecursiveRuleExecution } = prop
      let { value } = action || {}
      if (isEmpty(selectedField)) {
        selectedField = this.getSelectedField(fieldId)
      }

      if (!isEmpty(selectedField)) {
        let { name, displayTypeEnum } = selectedField
        if (
          Constants.PARSE_FIELDS_HASH.includes(displayTypeEnum) &&
          !isEmpty(value) &&
          isString(value)
        ) {
          value = JSON.parse(value)
        }

        if (isMultiLookup(selectedField)) {
          value = value.map(val => val.id)
          this.$set(this.formModel, name, value)
        } else if (
          isChooserTypeField(selectedField) ||
          isDropdownTypeField(selectedField)
        ) {
          this.$set(this.formModel, name, { id: value })
        } else if (selectedField.displayTypeEnum === 'MULTI_CURRENCY') {
          let { exchangeRate } = this.formModel || {}
          value = !isEmpty(exchangeRate) ? value * exchangeRate : value
          this.$set(this.formModel, name, value)
        } else {
          this.$set(this.formModel, name, value)
        }
        if (!stopRecursiveRuleExecution) {
          this.onChangeHandler({ field: selectedField })
        }
      }
    },
    handleSetFilter(prop) {
      let { action, fieldId, selectedField, criteriaKey } = prop
      let { value, isEnum } = action || {}
      if (isEmpty(selectedField)) {
        selectedField = this.getSelectedField(fieldId)
      }
      if (!isEmpty(selectedField)) {
        if (isEnum) {
          let { formModel } = this
          let { values = [] } = value
          let { field: fieldObj, name } = selectedField
          let { values: optionsMap = [] } = fieldObj
          let options = constructEnumFieldOptions(optionsMap)
          let isMultipleEnum = this.isMultiple(selectedField)
          // To reset already selected values
          if (isMultipleEnum) {
            let selectedValues = formModel[name] || []
            let finalValues =
              selectedValues.filter(value => !values.includes(value)) || []
            this.$set(this.formModel, name, finalValues)
          } else {
            let { id } = formModel[name] || {}
            let canResetValue = values.includes(id)
            if (canResetValue) {
              this.$set(this.formModel, name, { id: null })
            }
          }
          let filteredOptions =
            (options || []).filter(option => !values.includes(option.value)) ||
            []
          this.$set(selectedField, 'options', filteredOptions)
        } else {
          let { conditions, pattern } = value
          let criteria = {
            conditions,
            pattern,
          }
          this.$set(selectedField, criteriaKey, criteria)
        }
      }
    },
    getSelectedField(fieldId) {
      let { form } = this
      let { sections = [] } = form || {}
      let selectedField = sections
        .reduce((res, { fields }) => [...res, ...fields], [])
        .find(field => field.id === fieldId)
      return selectedField || {}
    },
    getSelectedSection(sectionId) {
      let { form } = this
      let { sections = [] } = form || {}
      let selectedSection = sections.find(section => section.id === sectionId)
      return selectedSection || {}
    },
    getActionName(rule) {
      let { action } = rule || {}
      let { actionName } = action || {}
      return actionName
    },
    getRulesById(props) {
      let { initialActionsList, id } = props
      let selectedRules = (initialActionsList || []).filter(
        rule => rule.fieldId === id
      )
      return selectedRules
    },
    getSetActionRule(props) {
      let { rules } = props
      let selectedRule = (rules || []).find(rule => {
        let actionName = this.getActionName(rule)
        return actionName === 'set'
      })
      return selectedRule
    },
    getViewRelatedActionRules(props) {
      let { rules } = props
      let selectedRules = (rules || []).filter(rule => {
        let actionName = this.getActionName(rule)
        return [
          'enable',
          'disable',
          'show',
          'hide',
          'hideSection',
          'showSection',
          'setMandatory',
          'removeMandatory',
        ].includes(actionName)
      })
      return selectedRules
    },
    getFilterActionRules(props) {
      let { rules } = props
      let selectedRule = (rules || []).find(rule => {
        let actionName = this.getActionName(rule)
        return actionName === 'filter'
      })
      return selectedRule
    },
  },
}
</script>
