<script>
import DsmForm from 'src/pages/custom-module/DSMFormCreation.vue'
import { API } from '@facilio/api'
import { isEmpty, isFunction ,isNullOrUndefined} from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { getBaseURL } from 'util/baseUrl'
import { deepCloneObject } from 'util/utility-methods'
export default {
  extends: DsmForm,
  methods: {
    async loadModuleData({ moduleDataId, moduleName }) {
      let { error, [moduleName]: record } = await API.fetchRecord(moduleName, { id: moduleDataId })
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { name: recordName, description } = record || {}
        record = {
          name: recordName,
          description,
          eventConfiguration: { ...record },
        }
        this.$set(this, 'moduleData', record)
      }
    },
    async loadFormData(formId) {
      let formObj = {}
      let { selectedForm, moduleName, moduleData, isEdit } = this
      let { id, name, module } = selectedForm
      let { name: formModuleName } = module || {}
      moduleName = isEmpty(formModuleName) ? moduleName : formModuleName
      let formUrl =
        id === -1
          ? `/v2/forms/${moduleName}?formName=${name}&fetchFormRuleFields=true`
          : `/v2/forms/${moduleName}?formId=${formId ||
              id}&fetchFormRuleFields=true`
      if (!isEdit) {
        formUrl = `${formUrl}&forCreate=true`
      }

      this.isLoading = true
      let { data, error } = await API.get(formUrl)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { form } = data
        if (!isEmpty(form)) {
          formObj = form
          formObj.secondaryBtnLabel = this.$t('common._common.cancel')
          formObj.primaryBtnLabel = this.$t('common._common._save')
          this.$set(this, 'formObj', formObj)
          if (!isEmpty(moduleData)) {
            this.deserializeData(moduleData)
          }
        }
      }
      return formObj
    },
    deserializeData(moduleData) {
      let { data } = moduleData
      let { isV3Api } = this
      let { formObj } = this
      if (!isEmpty(formObj)) {
        let { sections } = formObj
        if (!isEmpty(sections)) {
          sections.forEach(section => {
            let { fields } = section
            if (!isEmpty(fields)) {
              fields.forEach(field => {
                let {
                  field: fieldObj,
                  name,
                  displayTypeEnum,
                  displayType,
                } = field

                // Custom fields data extraction
                if (
                  !isEmpty(fieldObj) &&
                  !fieldObj.default &&
                  !isEmpty(data) &&
                  !isV3Api
                ) {
                  if (
                    displayTypeEnum === 'IMAGE' ||
                    displayTypeEnum === 'SIGNATURE'
                  ) {
                    let imageId = moduleData.data[`${name}Id`]
                    let imgUrl = `${getBaseURL()}/v2/files/preview/${
                      moduleData.data[`${name}Id`]
                    }`
                    this.$set(field, 'imgUrl', imageId ? imgUrl : null)
                    this.$set(field, 'value', imageId)
                  } else if (displayTypeEnum === 'FILE') {
                    let fileId = moduleData.data[`${name}Id`]
                    let fileObj = { name: moduleData.data[`${name}FileName`] }
                    this.$set(field, 'fileObj', fileId ? fileObj : null)
                    this.$set(field, 'value', fileId)
                  } else if (displayTypeEnum === 'LOOKUP_SIMPLE') {
                    this.$set(field, 'value', (data[name] || {}).id)
                  } else if (displayTypeEnum === 'DURATION') {
                    this.$set(
                      field,
                      'value',
                      this.$helpers.getDurationInSecs(
                        data[name],
                        !isEmpty((fieldObj || {}).unit) ? fieldObj.unit : 's'
                      )
                    )
                  } else if (
                    displayTypeEnum === 'SADDRESS' ||
                    displayTypeEnum === 'ADDRESS'
                  ) {
                    this.$set(
                      field,
                      'value',
                      this.$getProperty(
                        this,
                        `moduleData.data.${field.name}`,
                        deepCloneObject(Constants.ADDRESS_FIELD_DEFAULTS)
                      )
                    )
                  } else {
                    this.$set(field, 'value', data[name])
                  }
                } else if (displayTypeEnum === 'TEAMSTAFFASSIGNMENT') {
                  let { assignedTo, assignmentGroup } = moduleData
                  let fieldValue = {
                    assignedTo: {
                      id: '',
                    },
                    assignmentGroup: {
                      id: '',
                    },
                  }
                  if (!isEmpty(assignedTo)) {
                    fieldValue.assignedTo = assignedTo
                  }
                  if (!isEmpty(assignmentGroup)) {
                    fieldValue.assignmentGroup = assignmentGroup
                  }
                  this.$set(field, 'value', fieldValue)
                }
                else {
                  if (
                    displayTypeEnum === 'IMAGE' ||
                    displayTypeEnum === 'SIGNATURE'
                  ) {
                    let imageId = moduleData[`${name}Id`]
                    let imgUrl = `${getBaseURL()}/v2/files/preview/${
                      moduleData[`${name}Id`]
                    }`
                    this.$set(field, 'imgUrl', imageId ? imgUrl : null)
                    this.$set(field, 'value', imageId)
                  } else if (displayTypeEnum === 'FILE') {
                    let fileId = moduleData[`${name}Id`]
                    let fileObj = { name: moduleData[`${name}FileName`] }
                    this.$set(field, 'fileObj', fileId ? fileObj : null)
                    this.$set(field, 'value', fileId)
                  } else if (
                    (displayTypeEnum === 'LOOKUP_SIMPLE' ||
                      displayTypeEnum === 'REQUESTER' ||
                      displayTypeEnum === 'SPACECHOOSER') &&
                    field.name !== 'siteId'
                  ) {
                    this.$set(field, 'value', (moduleData[name] || {}).id)
                  } else if (displayType === 56) {
                    let { config } = field
                    let {
                      endFieldName,
                      startFieldName,
                      scheduleJsonName,
                    } = config
                    let scheduleValueObj = {
                      startFieldValue: moduleData[startFieldName],
                      endFieldValue: moduleData[endFieldName],
                      scheduleJsonValue: moduleData[scheduleJsonName],
                      isRecurring: moduleData.isRecurring,
                    }
                    this.$set(field, 'scheduleValueObj', scheduleValueObj)
                  } else if (
                    displayTypeEnum === 'SADDRESS' ||
                    displayTypeEnum === 'ADDRESS'
                  ) {
                    this.$set(
                      field,
                      'value',
                      this.$getProperty(
                        this,
                        `moduleData.${field.name}`,
                        deepCloneObject(Constants.ADDRESS_FIELD_DEFAULTS)
                      )
                    )
                  }
                  else if(displayTypeEnum === 'EVENT_CONFIGURATION'){
                    this.$set(
                      field,
                      'value',
                      this.$getProperty(this, `moduleData.${name}`, {})
                    )
                  }
                   else if (displayTypeEnum === 'NOTES') {
                    this.$set(field, 'value', field.value)
                  } else {
                    if (
                      ['NUMBER', 'DECIMAL'].includes(displayTypeEnum) &&
                      !isNullOrUndefined(moduleData[name])
                    ) {
                      this.$set(field, 'value', moduleData[name])
                    } else if (isEmpty(moduleData[name])) {
                      this.$set(field, 'value', null)
                    } else {
                      this.$set(field, 'value', moduleData[name])
                    }
                  }
                }
              })
            }
          })
        }
      }
    },
    modifyFieldPropsHook(field) {
      // Overriding field object in consuming component

      //For Calender Event
      let { displayTypeEnum } = field || {}
      if (displayTypeEnum === 'CALENDAR_EVENT_CONFIGURATION') {
        return { ...field, displayName: '' }
      }
    },
    serializedData(formObj, formModel) {
      let { description } = formModel || {}
      let { name } = formModel || {}
      let { eventConfiguration } = formModel || {}
      let { timeSlotList } = eventConfiguration || {}
      timeSlotList = (timeSlotList || []).map(e => {
        let { startMin, endMin } = e || {}
        startMin = startMin.toString()
        endMin = endMin.toString()
        let startTimehr = startMin.slice(16, 18)
        let startTimemin = startMin.slice(19, 21)
        let endTimehr = endMin.slice(16, 18)
        let endTimemin = endMin.slice(19, 21)

        let timeObj = {
          startMin: parseInt(startTimehr) * 60 + parseInt(startTimemin),
          endMin: parseInt(endTimehr) * 60 + parseInt(endTimemin),
        }
        return timeObj
      })
      eventConfiguration = {
        ...eventConfiguration,
        name,
        description,
        timeSlotList,
        eventSequence: 1,
      }
      return eventConfiguration
    },
   async saveRecord(formModel) {
      let { formObj, afterSerializeHook, moduleName, moduleDataId } = this
      let { formId } = formModel
      let response = {}
      let data = this.serializedData(formObj, formModel)
      if (!isEmpty(formId)) {
        data.formId = formId
      }
      if (!isEmpty(moduleDataId)) {
        data.id = moduleDataId
      }
      // Hook to overwrite the serialized data, before performing crud operation
      if (!isEmpty(afterSerializeHook) && isFunction(afterSerializeHook)) {
        data = this.afterSerializeHook({
          data,
          formModel,
          formObj,
        })
      }
      this.isSaving = true
      if (isEmpty(moduleDataId)) {
        response = await API.createRecord(moduleName, {
          data,
        })
      } else {
        response = await API.updateRecord(moduleName, {
          id: moduleDataId,
          data,
        })
      }
      this.isSaving = false
      // Hook to handle notification after crud operation
      let { moduleDisplayName } = this
      let { error } = response
      if (error) {
        let { message = 'Error occured' } = error
        this.$message.error(message)
      } else {
        let successMsg = moduleDataId
          ? `${moduleDisplayName} updated successfully`
          : `${moduleDisplayName} created successfully`
        this.$message.success(successMsg)
        this.redirectToList()
      }
    },
    redirectToList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
          })
      } else {
        this.$router.push({
          name: 'calendarEvent',
        })
      }
    },
  },
}
</script>
