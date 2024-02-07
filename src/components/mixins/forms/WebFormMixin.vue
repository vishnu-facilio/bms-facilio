<script>
import {
  isEmpty,
  isArray,
  areValuesEmpty,
  isNullOrUndefined,
  isFunction,
  isObject,
} from '@facilio/utils/validation'
import { deepCloneObject } from 'util/utility-methods'
import { constructEnumFieldOptions } from '@facilio/utils/utility-methods'
import Constants from 'util/constant'
import FAssignment from '@/FAssignment'
import FLookupField from '@/forms/FLookupField'
import http from 'util/http'
import {
  isLookupDropDownField,
  isChooserTypeField,
  isSiteLookup,
} from 'util/field-utils'

import isString from 'lodash/isString'
import FormFieldHandlerMixin from '@/mixins/forms/FormFieldHandlerMixin'
import SubFormSetupMixin from '@/mixins/forms/SubFormSetupMixin'
import FormsEventMixin from './FormsEventMixin'
import { API } from '@facilio/api'
import { prettyBytes } from '@facilio/utils/filters'
import { getFieldOptions } from 'util/picklist'
import {
  isDateTypeField,
  isMultiLookup,
  isLookupSimple,
  isDropdownTypeField,
} from '@facilio/utils/field'
import { mapState } from 'vuex'
import common from 'src/util/common.js'
import { getCalculatedCurrencyValue } from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

const addressFieldsList = ['billToAddress', 'shipToAddress', 'location']

export default {
  mixins: [FormsEventMixin, SubFormSetupMixin, FormFieldHandlerMixin],
  components: {
    FAssignment,
    FLookupField,
  },
  computed: {
    ...mapState({
      account: state => state.account,
    }),
    currentSiteId() {
      let siteId = Number(this.$cookie.get('fc.currentSite'))
      let currentSiteId = siteId > 0 ? siteId : -1
      return currentSiteId
    },
    selectedSiteId: {
      get() {
        let { sections, formModel, module, form } = this
        let { siteId } = formModel || {}
        if (!isEmpty(siteId)) {
          return !isEmpty(siteId) ? siteId.id : null
        } else if (module === 'formBuilder' && !isEmpty(sections)) {
          let site = sections
            .reduce((res, { fields }) => [...res, ...fields], [])
            .filter(field => field.name === 'siteId')
          return !isEmpty(site) ? site[0].value : null
        } else {
          let { sections: currSection = [] } = form || {}
          let siteField = currSection
            .reduce((res, { fields }) => {
              if (!isEmpty(fields)) {
                return [...res, ...fields]
              } else {
                return res
              }
            }, [])
            .find(field => isSiteLookup(field))

          if (!isEmpty(siteField)) {
            let { name = '' } = siteField
            return !isEmpty(formModel[name]) ? formModel[name].id : null
          } else {
            return null
          }
        }
      },
      deep: true,
    },
    preFillValueForCreationObj() {
      let { $route } = this
      let { query } = $route || {}
      let { formDetails } = query || {}
      let queryModel = {}

      if (!isEmpty(formDetails)) {
        queryModel = JSON.parse(formDetails)
      } else {
        queryModel = query
      }

      if (!isEmpty(queryModel)) {
        let { assignedTo, assignmentGroup } = queryModel || {}
        if (!isEmpty(assignedTo) || !isEmpty(assignmentGroup)) {
          assignedTo = !isEmpty(assignedTo) ? parseInt(assignedTo) : ''
          assignmentGroup = !isEmpty(assignmentGroup)
            ? parseInt(assignmentGroup)
            : ''
          let assignment = {
            assignedTo: { id: assignedTo },
            assignmentGroup: { id: assignmentGroup },
          }
          queryModel['assignment'] = JSON.stringify(assignment)
        }
        return queryModel
      }

      return {}
    },
  },
  watch: {
    selectedSiteId: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.$set(this, 'filter', { site: newVal })
        }
      },
    },
  },
  created() {
    this.$store.dispatch('loadBuildings')
  },
  mounted() {
    let { selectedSiteId } = this
    if (!isEmpty(selectedSiteId)) {
      this.$set(this, 'filter', { site: selectedSiteId })
    }
  },
  methods: {
    /*
      1. Have to fetch options for system fields of type lookup_simple through picklist factory.
      2. Have to convert `value` to the desired format, since all the `value` for the fields is of type string
        for eg:
        Team/Staff field `value` from the server is json string
        "{"assignedTo":{"id":848545},"assignmentGroup":{"id":56}}"
        Have to convert this to normal object before rendering.
      3. Fetch files for 'ATTACHMENT' type field and insert into both formModel and attachments[]
    */
    deserializeData(fields, sections = []) {
      let {
        module,
        isEdit,
        fieldTransitionFn,
        form,
        ruleFieldIds,
        isV3Api,
        preFillValueForCreationObj,
        currentSiteId,
        modifyFieldPropsHook,
      } = this
      let formSections = []
      if (module === 'formBuilder') {
        formSections = sections
      } else {
        let { sections } = form || {}
        formSections = sections
      }
      let tasksAllowedModules = Constants.TASKS_ALLOWED_MODULES_HASH
      return fields.map(field => {
        let {
          displayTypeEnum,
          value,
          required,
          lookupModuleName,
          name,
          config,
          hideField,
          id,
          validations,
        } = field

        if (
          !isEmpty(modifyFieldPropsHook) &&
          isFunction(modifyFieldPropsHook)
        ) {
          let modifiedField = modifyFieldPropsHook(field)
          if (!isEmpty(modifiedField)) field = { ...field, ...modifiedField }
        }

        let isLookupSimpleField =
          isLookupSimple(field) || field.displayTypeEnum === 'SPACECHOOSER'
        let isLookupMulti = isMultiLookup(field)
        let isLookupField =
          isLookupDropDownField(field) ||
          field.displayTypeEnum === 'SPACECHOOSER'
        let isResourceField = isChooserTypeField(field)

        // To set filters and criteria values that are configured onLoad form rules
        if (module !== 'formBuilder') {
          this.executeFilterRelatedActions({ field })
        }

        if (
          Constants.PARSE_FIELDS_HASH.includes(displayTypeEnum) &&
          !isEmpty(value) &&
          isString(value)
        ) {
          // Special hanlding for deviation type forms
          if (value.startsWith('${')) {
            field.value = value
          } else {
            if (displayTypeEnum === 'NUMBER') {
              field.value = value
            } else if (this.isStringSystemEnumField(field)) {
              field.value = value
            } else {
              field.value = this.parseDefaultValue(value)
            }
          }
        }
        if (
          (isLookupSimpleField || isResourceField || isLookupMulti) &&
          !isEmpty(lookupModuleName)
        ) {
          if (name === 'siteId') {
            this.siteFieldHandler({ field, currentSiteId })
          } else {
            this.lookupFieldHandler({
              formSections,
              field,
              isEdit,
              preFillValueForCreationObj,
              isLookupField,
              isResourceField,
              isLookupMulti,
            })
          }
        }
        if (displayTypeEnum === 'SIGNATURE') {
          if (isEmpty(config)) {
            this.$set(field, 'config', {
              canShowColorPalette: false,
            })
          }
        }
        if (displayTypeEnum === 'REQUESTER') {
          this.fetchRequesterList(field)
        }
        if (displayTypeEnum === 'TEAMSTAFFASSIGNMENT') {
          if (isEmpty(field.value)) {
            field.value = {
              assignedTo: {
                id: '',
              },
              assignmentGroup: {
                id: '',
              },
            }
          }
        }
        if (displayTypeEnum === 'DURATION') {
          this.durationFieldHandler({ field })
        }
        if (
          (displayTypeEnum === 'NUMBER' || displayTypeEnum === 'DECIMAL') &&
          module === 'formBuilder'
        ) {
          this.numberDecimalFieldHandler({ field })
        }
        if (displayTypeEnum === 'SELECTBOX') {
          let { field: fieldObj, value } = field
          if (this.isMultiple(field) && isEmpty(value)) {
            field.value = []
          }
          if (!isEmpty(fieldObj) && !isEmpty(fieldObj.enumMap)) {
            let { values = [] } = fieldObj
            let options = constructEnumFieldOptions(values)
            this.$set(field, 'options', options)
          }
        }
        if (displayTypeEnum === 'URGENCY') {
          let options = Constants.URGENCY_FIELD_OPTIONS
          this.$set(field, 'options', options)
        }
        if (
          (displayTypeEnum === 'SADDRESS' || displayTypeEnum === 'ADDRESS') &&
          !isEdit
        ) {
          this.$set(
            field,
            'value',
            deepCloneObject(Constants.ADDRESS_FIELD_DEFAULTS)
          )
        } else if (displayTypeEnum === 'QUOTE_ADDRESS' && !isEdit) {
          this.$set(
            field,
            'value',
            deepCloneObject(Constants.ADDRESS_FIELD_DEFAULTS)
          )
          if (this.formModel) {
            this.$set(
              this.formModel,
              `billToAddress`,
              deepCloneObject(Constants.ADDRESS_FIELD_DEFAULTS)
            )
            this.$set(
              this.formModel,
              `shipToAddress`,
              deepCloneObject(Constants.ADDRESS_FIELD_DEFAULTS)
            )
          }
        } else if (displayTypeEnum === 'GEO_LOCATION' && !isEdit) {
          let { value } = field || {}
          if (!isEmpty(value)) {
            if (!isObject(value)) {
              value = JSON.parse(value)
            }
            this.$set(field, 'value', value)
          }
        }
        if (displayTypeEnum === 'LINEITEMS' && !isEdit) {
          let defaultItem = []
          defaultItem.push(deepCloneObject(Constants.LINE_ITEM_DEFAULTS))
          this.$set(field, 'value', defaultItem)
          Constants.QUOTE_LINE_ITEMS_ADDITIONAL_FIELDS.forEach(
            additionalField => {
              if (this.formModel) {
                if (additionalField === 'tax') {
                  this.$set(this.formModel, additionalField, { id: null })
                } else {
                  this.$set(this.formModel, additionalField, null)
                }
              }
            }
          )
        }
        if (displayTypeEnum === 'INVREQUEST_LINE_ITEMS' && !isEdit) {
          let defaultItem = []
          defaultItem.push(deepCloneObject(Constants.INV_LINE_ITEM_DEFAULTS))
          this.$set(field, 'value', defaultItem)
        }
        if (
          displayTypeEnum === 'BUDGET_AMOUNT' &&
          !isEdit &&
          module !== 'formBuilder'
        ) {
          let defaultItem = {
            incomes: [deepCloneObject(Constants.budgetIncomeDefaults)],
            expenses: [deepCloneObject(Constants.budgetExpenseDefaults)],
          }
          this.$set(field, 'value', defaultItem)
        }
        if (
          displayTypeEnum === 'FACILITY_AVAILABILITY' &&
          !isEdit &&
          module !== 'formBuilder'
        ) {
          this.$set(
            field,
            'value',
            deepCloneObject(Constants.facilityAvailabilityDefaults)
          )
        }
        if (
          displayTypeEnum === 'FACILITY_BOOKING_SLOTS' &&
          (isEmpty(value) || !isArray(value))
        ) {
          // TODO Default value is getting set as '[]' when we open form builder Check with ahielan and fix this
          this.$set(field, 'value', [])
          if (this.formModel) {
            this.$set(this.formModel, 'bookingDate', null)
          }
        }
        if (displayTypeEnum === 'QUOTE_LINE_ITEMS' && !isEdit) {
          let defaultItem = []
          defaultItem.push(deepCloneObject(Constants.QUOTE_LINE_ITEM_DEFAULTS))
          this.$set(field, 'value', defaultItem)
          Constants.QUOTE_LINE_ITEMS_ADDITIONAL_FIELDS.forEach(
            additionalField => {
              if (this.formModel) {
                if (additionalField === 'tax') {
                  this.$set(this.formModel, additionalField, { id: null })
                } else {
                  this.$set(this.formModel, additionalField, null)
                }
              }
            }
          )
        }
        if (displayTypeEnum === 'ATTACHMENT' && isV3Api && isEdit) {
          this.fetchAttachments(field)
        }

        if (!isEmpty(fieldTransitionFn)) {
          field = fieldTransitionFn(field)
        }
        if (module !== 'formBuilder' && displayTypeEnum !== 'NOTES') {
          this.constructFormModel(field)
          // To hide or show or disable or enable fields that are configured for onLoad form rules
          this.executeViewRelatedActions({ field })
          // Skip validtion for hidden fields, have to revisit while implementing form rules
          if ((required && !hideField) || !isEmpty(validations)) {
            // Setup validation for mandatory fields
            this.setupValidation(field)
          }
        }
        // Setting trigger fields
        field.isTriggerField = (ruleFieldIds || []).includes(id)
        return field
      })
    },

    fetchRequesterList(field) {
      let url = '/setup/portalusers'
      http
        .get(url)
        .then(({ data = {} }) => {
          let { users = [] } = data
          let options = []
          if (!isEmpty(users)) {
            options = users.map(user => {
              return {
                label: user.email,
                value: user.id,
              }
            })
          }
          this.$set(field, 'options', options)
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
    },

    async fetchAttachments({ lookupModuleName }) {
      let { parentId, id } = this.moduleData || {}
      if (isEmpty(id)) return

      let { error, data } = await API.get('/attachment', {
        module: lookupModuleName,
        recordId: parentId ? parentId : id,
      })

      if (!error) {
        let { attachments } = data || []
        this.formModel[lookupModuleName] = attachments
        this.attachments = attachments.map(file => {
          let { fileName: name, fileSize: size, contentType: type } = file
          let bytes = prettyBytes(size)
          return {
            name,
            size,
            bytes,
            type,
            status: null,
            error: null,
          }
        })
      }
    },

    constructFormModel(field) {
      let {
        name,
        value,
        displayTypeEnum,
        displayType,
        config,
        scheduleValueObj = {},
        id,
        lookupModuleName,
      } = field
      let { preFillValueForCreationObj, isEdit, form, isLiveSubForm } = this
      let { onLoadActionsFieldIds = [], initialActionsList = [] } = form
      let rule = {}
      // To set values that are configured for onLoad form rules
      if (onLoadActionsFieldIds.includes(id)) {
        let rules = this.getRulesById({ initialActionsList, id })
        rule = this.getSetActionRule({ rules })
      }
      if (!isEmpty(rule)) {
        this.executeRules({
          rule,
          selectedField: field,
          stopRecursiveRuleExecution: true,
        })
      } else {
        if (
          !isEmpty(preFillValueForCreationObj[name]) &&
          !isEdit &&
          !isLiveSubForm
        ) {
          // To prefill values in the query param
          let paramValue = preFillValueForCreationObj[name]
          if (isChooserTypeField(field)) {
            value = {
              id: this.parseDefaultValue(paramValue),
            }
          } else if (Constants.PARSE_FIELDS_HASH.includes(displayTypeEnum)) {
            value = this.parseDefaultValue(paramValue)
          } else {
            value = paramValue
          }
        }
        if (displayType === 56 && !isEmpty(config)) {
          let { endFieldName, startFieldName, scheduleJsonName } = config
          let {
            startFieldValue = null,
            endFieldValue = null,
            scheduleJsonValue = {},
            isRecurring = false,
          } = scheduleValueObj
          this.$set(this.formModel, `${endFieldName}`, endFieldValue)
          this.$set(this.formModel, `${startFieldName}`, startFieldValue)
          this.$set(this.formModel, `${scheduleJsonName}`, scheduleJsonValue)
          this.$set(this.formModel, 'isRecurring', isRecurring)
        } else if (displayTypeEnum === 'QUOTE_ADDRESS') {
          let { billToAddress, shipToAddress } = field
          if (!isEmpty(billToAddress)) {
            this.$set(this.formModel, `billToAddress`, billToAddress)
          } else {
            this.$set(
              this.formModel,
              `billToAddress`,
              deepCloneObject(Constants.ADDRESS_FIELD_DEFAULTS)
            )
          }
          if (!isEmpty(shipToAddress)) {
            this.$set(this.formModel, `shipToAddress`, shipToAddress)
          } else {
            this.$set(
              this.formModel,
              `shipToAddress`,
              deepCloneObject(Constants.ADDRESS_FIELD_DEFAULTS)
            )
          }
        } else if (
          ['LINEITEMS', 'QUOTE_LINE_ITEMS'].includes(displayTypeEnum)
        ) {
          this.$set(this.formModel, `${name}`, value)
          Constants.QUOTE_LINE_ITEMS_ADDITIONAL_FIELDS.forEach(
            additionalField => {
              let value = this.$getProperty(field, `${additionalField}`, null)

              if (additionalField === 'tax') {
                if (isEmpty(value)) {
                  value = { id: null }
                }
              }
              this.$set(this.formModel, additionalField, value)
            }
          )
        } else if (displayTypeEnum === 'FACILITY_BOOKING_SLOTS') {
          this.$set(this.formModel, `${name}`, value || [])
          this.$set(this.formModel, `bookingDate`, field.bookingDate)
        } else if (displayTypeEnum === 'URL_FIELD') {
          let { showName } = config || {}
          let value = { href: '' }

          if (showName) {
            value = { ...value, name: '' }
          }
          this.$set(this.formModel, `${name}`, value || {})
        } else if (!isEmpty(name)) {
          if (
            isDropdownTypeField(field) ||
            displayTypeEnum === 'SPACECHOOSER' ||
            (isChooserTypeField(field) && isEmpty(value)) ||
            isMultiLookup(field)
          ) {
            if (this.isMultiple(field)) {
              this.$set(this.formModel, `${name}`, value || [])
            } else if (isMultiLookup(field)) {
              if (!isEmpty(value)) {
                if (typeof value === 'string') value = JSON.parse(value)
                value = value.map(value => {
                  let { id } = value || {}
                  if (isEmpty(id)) return { id: value }
                  return value
                })
              }

              this.$set(
                this.formModel,
                `${name}`,
                !isEmpty(value) ? value.map(value => (value || {}).id) : []
              )
            } else {
              this.$set(this.formModel, `${name}`, { id: value })
            }
          } else {
            let { field: fieldObj } = field || {}

            if (displayTypeEnum === 'ATTACHMENT') {
              this.$set(this.formModel, `${lookupModuleName}`, [])
            } else if (
              displayTypeEnum === 'FORMULA_FIELD' ||
              displayTypeEnum === 'FORMULA_FIELD_TEMP'
            ) {
              if (isEmpty(value)) {
                value = {
                  workflow: null,
                }
              }
              this.$set(this.formModel, `${name}`, value)
            } else if (
              !isEmpty(fieldObj) &&
              isDateTypeField(fieldObj) &&
              value <= 0
            ) {
              this.$set(this.formModel, `${name}`, null)
            } else {
              this.$set(this.formModel, `${name}`, value)
            }
          }
        }
      }
    },
    parseDefaultValue(value) {
      return JSON.parse(value)
    },
    setupValidation(field) {
      let { name } = field
      if (!isEmpty(name)) {
        if (addressFieldsList.includes(name)) {
          let addressProps = ['street', 'city', 'state', 'zip', 'country']
          addressProps.forEach(prop => {
            this.pushValidationRules(`${field.name}.${prop}`, field)
          })
        } else {
          this.pushValidationRules(`${field.name}`, field)
        }
      }
    },
    isEmailField(displayTypeEnum) {
      return displayTypeEnum === 'EMAIL'
    },
    pushValidationRules(fieldName, field) {
      let { displayTypeEnum, lookupModuleName } = field
      let trigger = Constants.FIELD_TYPE_DROPDOWNHASH.includes(displayTypeEnum)
        ? 'change'
        : 'blur'
      let isAttachmentField = displayTypeEnum === 'ATTACHMENT'
      trigger = displayTypeEnum === 'TEAMSTAFFASSIGNMENT' ? 'change' : trigger
      fieldName = isAttachmentField ? lookupModuleName : fieldName
      this.rules[fieldName] = []
      this.rules[fieldName].push({
        validator: (rule, value, callback) => {
          this.validateFields({ rule, field, value, callback })
        },
        trigger,
      })
    },
    validateFields({ rule, field, value, callback }) {
      let { field: ruleField } = rule
      let {
        displayName,
        displayTypeEnum,
        value: fieldValue = {},
        name,
        validations,
        required,
        lookupModuleName,
      } = field
      let isResourceField = isChooserTypeField(field)
      let isMultipleField = this.isMultiple(field)
      let { formModel, parentSiteId, isLiveSubForm, isV3Api } = this
      if (!isEmpty(parentSiteId) && isLiveSubForm && name === 'siteId') {
        callback()
      } else if (ruleField.includes('address.')) {
        let charIndex = ruleField.indexOf('.')
        let addressProp = ruleField.substring(charIndex + 1)
        if (isEmpty(fieldValue[addressProp])) {
          callback(new Error(`Please input ${addressProp}`))
        } else {
          callback()
        }
      } else if (isResourceField) {
        if (areValuesEmpty(value)) {
          callback(new Error(`Please input ${displayName}`))
        } else {
          callback()
        }
      } else if (displayTypeEnum === 'TEAMSTAFFASSIGNMENT') {
        let { assignedTo, assignmentGroup } = formModel[name] || {}
        let { id: assignedToId } = assignedTo || {}
        let { id: assignmentGroupId } = assignmentGroup || {}
        if (isEmpty(assignedToId) && isEmpty(assignmentGroupId)) {
          callback(new Error(`Please input ${displayName}`))
        } else {
          callback()
        }
      } else if (displayTypeEnum === 'CURRENCY') {
        let { multiCurrencyEnabled } =
          this.$getProperty(this, 'account.data.currencyInfo') || {}
        let { currencyCode, currencyValue } = formModel[name] || {}
        if (
          multiCurrencyEnabled &&
          (isEmpty(currencyCode) || isEmpty(currencyValue))
        ) {
          callback(new Error(`Please input ${displayName}`))
        } else if (isEmpty(currencyValue)) {
          callback(new Error(`Please input ${displayName}`))
        } else {
          callback()
        }
      } else if (displayTypeEnum === 'MULTI_CURRENCY') {
        let value = formModel[name] || {}
        if (isEmpty(value)) {
          callback(new Error(`Please input ${displayName}`))
        } else {
          callback()
        }
      } else if (
        displayTypeEnum === 'FORMULA_FIELD_TEMP' ||
        displayTypeEnum === 'FORMULA_FIELD'
      ) {
        let formulaFieldValue = formModel[name]
        if (!isEmpty(formulaFieldValue)) {
          let { workflow } = formulaFieldValue
          if (!isEmpty(workflow)) {
            let { workflowUIMode, workflowString, resultEvaluator } = workflow
            if (
              (workflowUIMode === 2 && !isEmpty(workflowString)) ||
              workflowUIMode === 3 ||
              (workflowUIMode === 1 && !isEmpty(resultEvaluator))
            ) {
              callback()
            }
          }
        }
        callback(new Error(`Please input ${displayName}`))
      } else if (
        // TODO: Have to check fdatepicker component, as it is returning NaN for empty value
        (displayTypeEnum === 'DATE' || displayTypeEnum === 'DATETIME') &&
        isNaN(value)
      ) {
        callback(new Error(`Please input ${displayName}`))
      } else if (displayTypeEnum === 'ATTACHMENT') {
        let attachments = this.$getProperty(
          formModel,
          `${lookupModuleName}`,
          []
        )
        if (isEmpty(attachments)) {
          callback(new Error(`Please input ${displayName}`))
        } else {
          callback()
        }
      } else if (displayTypeEnum === 'URL_FIELD') {
        if (required && !isEmpty(value)) {
          let { href } = value || {}

          if (isEmpty(href)) {
            callback(new Error(`Please input a url`))
          } else {
            callback()
          }
        }
      } else if (isMultipleField) {
        if (isEmpty(value)) {
          callback(new Error(`Please input ${displayName}`))
        } else {
          callback()
        }
      } else if (
        Constants.FIELD_TYPE_DROPDOWNHASH.includes(displayTypeEnum) &&
        isEmpty(value.id)
      ) {
        callback(new Error(`Please input ${displayName}`))
      } else if (
        displayTypeEnum === 'daterange' &&
        (!isArray(value) || isEmpty(value))
      ) {
        callback(new Error(`Please input ${displayName}`))
      } else if (displayTypeEnum === 'DECISION_BOX' && !value) {
        callback(new Error(`Please input ${displayName}`))
      } else if (['NUMBER', 'DECIMAL'].includes(displayTypeEnum) && isV3Api) {
        if (isNullOrUndefined(value))
          callback(new Error(`Please input ${displayName}`))
        else callback()
      } else if (isEmpty(value) && required) {
        callback(new Error(`Please enter ${displayName}`))
      } else if (!isEmpty(validations)) {
        let { maxLength } = validations
        if (!isNullOrUndefined(value) && value.length > maxLength) {
          callback(new Error(`Character limit of ${maxLength} exceeded`))
        } else if (
          !isEmpty(value) &&
          this.isEmailField(displayTypeEnum) &&
          !common.validateEmail(value)
        ) {
          callback(
            new Error(this.$t('forms.field_permission.email_validation'))
          )
        } else {
          callback()
        }
      } else {
        callback()
      }
    },
    isDropdownTypeFields(displayTypeEnum) {
      return Constants.FIELD_TYPE_DROPDOWNHASH.includes(displayTypeEnum)
    },
    isResourceTypeFields(field) {
      let { displayTypeEnum, lookupModuleName } = field
      return (
        displayTypeEnum === 'WOASSETSPACECHOOSER' &&
        lookupModuleName === 'resource'
      )
    },
    isStringSystemEnumField(field) {
      let { field: fieldObj } = field || {}
      let { dataTypeEnum } = fieldObj || {}
      return ['STRING_SYSTEM_ENUM'].includes(dataTypeEnum)
    },
    isMultiple(field) {
      let { field: fieldObj } = field || {}
      let { dataTypeEnum } = fieldObj || {}
      return dataTypeEnum === 'MULTI_ENUM'
    },
    showConfirmSiteSwitch(previousSiteId) {
      let { form, formModel, tasksList, isLiveSubForm } = this
      if (!isEmpty(form)) {
        let { sections } = form
        if (!isEmpty(sections)) {
          let lookupDropDownFields = sections
            .reduce((res, { fields }) => [...res, ...fields], [])
            .filter(field => {
              let { config } = field || {}
              let { skipSiteFilter = false } = config || {}
              return (
                (!skipSiteFilter &&
                  !isSiteLookup(field) &&
                  (field.displayTypeEnum === 'TEAMSTAFFASSIGNMENT' ||
                    isLookupDropDownField(field) ||
                    isChooserTypeField(field))) ||
                field.displayTypeEnum === 'SPACECHOOSER'
              )
            })

          let isAnyLookupFieldFilled = lookupDropDownFields.some(field => {
            let { displayTypeEnum } = field
            if (displayTypeEnum === 'TEAMSTAFFASSIGNMENT') {
              let { assignedTo, assignmentGroup } = formModel[field.name] || {}
              let { id: assignedToId } = assignedTo || {}
              let { id: assignmentGroupId } = assignmentGroup || {}
              return !isEmpty(assignedToId) || !isEmpty(assignmentGroupId)
            }
            return !isEmpty((formModel[field.name] || {}).id)
          })
          let isAnyTasksWithResource = (tasksList || []).some(taskObj => {
            let { tasks } = taskObj
            if (!isEmpty(tasks)) {
              return tasks.some(task => !isEmpty(task.resource.id))
            }
            return false
          })
          if (isAnyLookupFieldFilled || isAnyTasksWithResource) {
            let htmlMessage = this.$t('forms.live_form.change_site')
            if (isLiveSubForm)
              htmlMessage = this.$t('forms.live_form.change_site_subform')
            let dialogObj = {
              title: this.$t('forms.live_form.change_site_heading'),
              htmlMessage,
              rbDanger: true,
              lbLabel: this.$t('common.products.no'),
              rbLabel: this.$t('common.products.yes'),
            }
            this.$dialog.confirm(dialogObj).then(value => {
              if (value) {
                this.resetLookupFields(lookupDropDownFields)
                if (isAnyTasksWithResource) {
                  tasksList.forEach(taskObj => {
                    let { tasks } = taskObj
                    if (!isEmpty(tasks)) {
                      tasks.forEach(task => this.resetTaskResourceField(task))
                    }
                  })
                }
              } else {
                this.$set(this.formModel, 'siteId', { id: previousSiteId })
              }
            })
          }
        }
      }
    },
    resetLookupFields(lookupDropDownFields) {
      if (!isEmpty(lookupDropDownFields)) {
        lookupDropDownFields.forEach(field => {
          let { displayTypeEnum } = field
          let value = { id: null }
          if (displayTypeEnum === 'TEAMSTAFFASSIGNMENT') {
            value = {
              assignedTo: {
                id: '',
              },
              assignmentGroup: {
                id: '',
              },
            }
          }
          this.$set(field, 'selectedItems', [])
          this.$set(this.formModel, `${field.name}`, value)
        })
      }
    },
    resetTaskResourceField(selectedTask) {
      selectedTask.resource = {}
      selectedTask.readingFieldId = null
      selectedTask.inputType = 1
      selectedTask.selectedLookupField.value = {
        id: null,
      }
      selectedTask.selectedLookupField.selectedItems = []
    },
    addImgFile(props) {
      let { fileOrId: data, field, isSubForm } = props || {}
      let { name } = field
      this.formModel[name] = data
      this.clearError(name)
      this.onChangeHandler({ field, isSubForm })
    },
    removeImgFile(props) {
      let { field, isSubForm } = props || {}
      let { name } = field
      this.formModel[name] = null
      this.onChangeHandler({ field, isSubForm })
    },
    openFassignment(event, field) {
      let { isDisabled } = field
      if (isDisabled) {
        event.stopPropagation()
      }
    },
    async onRecordSelected(prop) {
      let { value, field, isSubForm } = prop || {}
      let { name, field: fieldObj, selectedItems, config } = field
      let { formModel } = this

      this.onChangeHandler({ field, isSubForm })

      if (name === 'resource' && !isEmpty(fieldObj)) {
        if (isEmpty(value)) {
          // Reset space asset resource obj, when user clears the selected resource field
          this.spaceAssetResourceObj = {}
        } else {
          let [selectedItem] = selectedItems
          let { value, label: resourceName } = selectedItem
          this.spaceAssetResourceObj = {
            name: resourceName,
            id: value,
            config,
          }
        }
        let { module } = fieldObj
        if (!isEmpty(module) && module.name === 'ticket') {
          let { form } = this
          let { resource } = formModel || {}
          let { id } = resource || {}
          if (!isEmpty(form)) {
            let { sections } = form
            let tenantField
            let params = {
              page: 1,
              perPage: 50,
            }
            if (!isEmpty(sections)) {
              sections.forEach(section =>
                section.fields.forEach(field => {
                  if (field.name === 'tenant') {
                    tenantField = field
                  }
                })
              )
            }
            if (this.$org.id !== 320 && !isEmpty(id) && !isEmpty(tenantField)) {
              let { filters = {} } = tenantField || {}
              tenantField.spaceId = id

              let tenantFilters = {
                tenantspaces: {
                  operatorId: 88,
                  relatedOperatorId: 38,
                  relatedFieldName: 'tenant',
                  filterFieldName: 'space',
                  value: [String(id)],
                },
              }
              tenantField.filters = {
                ...filters,
                ...tenantFilters,
              }

              let { error, options } = await getFieldOptions({
                field: tenantField,
                ...params,
              })

              if (error) {
                this.$message.error(error.message || 'Error Occured')
              } else {
                tenantField.options = options
              }
            }
          }
        }
      }
    },
    getSiteId(sections) {
      let filteredSections = sections.filter(
        section => !isEmpty(section.fields)
      )
      let site = filteredSections
        .reduce((res, { fields }) => [...res, ...fields], [])
        .filter(field => field.name === 'siteId')
      return !isEmpty(site) ? site[0].value : null
    },
    getResourceId() {
      let { formModel } = this
      if (!isEmpty(formModel)) {
        let resourceValue = formModel['resource']
        let { id } = resourceValue || {}
        return id
      }
      return null
    },
    getFieldByName(props) {
      let { name } = props
      let { form } = this
      let { sections } = form || {}
      let field = {}
      sections.forEach(section => {
        if (!isEmpty(section)) {
          let { fields } = section
          if (!isEmpty(fields) && isEmpty(field)) {
            field = fields.find(field => field.name === name)
          }
        }
      })
      return field
    },
    getStringEnumMap(field) {
      let { field: fieldObj } = field || {}
      let { enumMap } = fieldObj || {}
      let options = []
      let enumArr = Object.entries(enumMap || {})
      for (let [key, value] of enumArr) {
        let option = { label: key, value: value }
        options.push(option)
      }
      return options
    },
    calculateExchangeRate(rateObj) {
      let { oldExchangeRate, currencyCode, lineItemObj, exchangeRate } =
        rateObj || {}
      let sections = this.$getProperty(this, 'form.sections', [])
      let { currencyCode: initCurrencyCode, exchangeRate: initExchangeRate } =
        this.initialCurrency || {}

      if (isEmpty(oldExchangeRate)) {
        let oldCurrency = (this.currencyList || []).find(
          cur => cur.currencyCode === this.oldCurrencyCode
        )

        if (oldCurrency?.currencyCode === initCurrencyCode) {
          oldExchangeRate = initExchangeRate
        } else oldExchangeRate = oldCurrency?.exchangeRate
      }

      rateObj.oldExchangeRate = oldExchangeRate

      sections.forEach(section => {
        this.formModel = (section.fields || []).reduce((formModel, field) => {
          let { name, displayTypeEnum } = field || {}
          let value = formModel[name]
          let lineItemList = ['QUOTE_LINE_ITEMS', 'LINEITEMS']
          let allowCalculateCurrency = !isEmpty(value) && isEmpty(lineItemObj)
          let lineItems = formModel.lineItems

          if (displayTypeEnum === 'MULTI_CURRENCY' && allowCalculateCurrency) {
            lineItems = (lineItems || []).map(item => {
              return { ...item, currencyCalculated: false }
            })
            formModel[name] = getCalculatedCurrencyValue(rateObj, value)
          }

          if (lineItemList.includes(displayTypeEnum)) {
            let {
              adjustmentsCost,
              discountAmount,
              miscellaneousCharges,
              shippingCharges,
            } = formModel

            let { type, typeId } = lineItemObj || {}
            if (!isEmpty(typeId)) {
              formModel.lineItems = (lineItems || []).map(item => {
                let { unitPrice, currencyCalculated } = item || {}

                let { id } = item[type] || {}
                if (typeId === id && !currencyCalculated) {
                  unitPrice = getCalculatedCurrencyValue(rateObj, unitPrice)
                  currencyCalculated = !isEmpty(unitPrice)
                }
                return {
                  ...item,
                  unitPrice: unitPrice || null,
                  currencyCalculated,
                }
              })
            } else {
              formModel.lineItems = (lineItems || []).map(item => {
                let { unitPrice, currencyCalculated } = item || {}
                unitPrice = getCalculatedCurrencyValue(rateObj, unitPrice)
                currencyCalculated = !isEmpty(unitPrice)
                return {
                  ...item,
                  unitPrice: unitPrice || null,
                  currencyCalculated,
                }
              })
            }

            adjustmentsCost = getCalculatedCurrencyValue(
              rateObj,
              adjustmentsCost
            )
            discountAmount = getCalculatedCurrencyValue(rateObj, discountAmount)
            miscellaneousCharges = getCalculatedCurrencyValue(
              rateObj,
              miscellaneousCharges
            )
            shippingCharges = getCalculatedCurrencyValue(
              rateObj,
              shippingCharges
            )
            formModel = {
              ...formModel,
              adjustmentsCost,
              discountAmount,
              miscellaneousCharges,
              shippingCharges,
            }
          }

          if (displayTypeEnum === 'BUDGET_AMOUNT') {
            let { budgetamount } = this.formModel || {}
            let { expenses, incomes } = budgetamount || {}
            expenses = (expenses || []).map(expense => {
              let { yearlyAmount } = expense || {}
              yearlyAmount = getCalculatedCurrencyValue(rateObj, yearlyAmount)
              let { monthlyAmountSplitUp } = expense || {}
              monthlyAmountSplitUp = (monthlyAmountSplitUp || []).map(
                monthlyAmt => {
                  let { monthlyAmount } = monthlyAmt || {}
                  return {
                    monthlyAmount: getCalculatedCurrencyValue(
                      rateObj,
                      monthlyAmount
                    ),
                    currencyCode,
                    exchangeRate,
                  }
                }
              )
              return { ...(expense || {}), yearlyAmount, monthlyAmountSplitUp }
            })

            incomes = (incomes || []).map(income => {
              let { yearlyAmount } = income || {}
              yearlyAmount = getCalculatedCurrencyValue(rateObj, yearlyAmount)
              let { monthlyAmountSplitUp } = income || {}
              monthlyAmountSplitUp = (monthlyAmountSplitUp || []).map(
                monthlyAmt => {
                  let { monthlyAmount } = monthlyAmt || {}
                  return {
                    monthlyAmount: getCalculatedCurrencyValue(
                      rateObj,
                      monthlyAmount
                    ),
                    currencyCode,
                    exchangeRate,
                  }
                }
              )
              return { ...(income || {}), yearlyAmount, monthlyAmountSplitUp }
            })

            formModel = {
              ...(formModel || {}),
              budgetamount: {
                expenses,
                incomes,
              },
            }
          }

          return formModel
        }, this.formModel)
      })

      if (
        this.oldCurrencyCode !== currencyCode ||
        isEmpty(this.oldCurrencyCode)
      ) {
        this.oldCurrencyCode = currencyCode
      }
    },
    setCurrencyCodeOnChange(currencyCode, exchangeRate) {
      if (!isEmpty(currencyCode)) {
        let { initialCurrencyCode } = this.currencyData || {}
        let currencyData = {
          currencyCode,
          exchangeRate,
          oldCurrencyCode: this.oldCurrencyCode,
          initialCurrencyCode,
        }

        if (isEmpty(initialCurrencyCode))
          currencyData = { ...currencyData, initialCurrencyCode: currencyCode }

        this.$set(this, 'currencyData', currencyData)
        this.$set(this.formModel, 'currencyCode', currencyCode)
        this.$set(this.formModel, 'exchangeRate', exchangeRate)
      }
    },
    setInitialCurrency() {
      if (isEmpty(this.initialCurrency)) {
        let { currencyCode, exchangeRate } = this.moduleData || {}
        this.initialCurrency = { currencyCode, exchangeRate }
      }
      this.setHasMultiCurrencyFieldInModel()
    },
    setHasMultiCurrencyFieldInModel() {
      let sections = this.$getProperty(this, 'form.sections', [])
      let filteredFields = []
      let fields = sections.reduce((fields, section) => {
        fields = [...fields, ...(section.fields || [])]
        return fields
      }, [])
      filteredFields = (fields || []).filter(
        field => field.displayTypeEnum === 'MULTI_CURRENCY'
      )

      this.hasMultiCurrencyFieldInModel = !isEmpty(filteredFields)
    },
  },
}
</script>
