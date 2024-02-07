// This file is deprecated. Use ModuleData from @facilio/data
<script>
import {
  isEmpty,
  isObject,
  isFileObject,
  areValuesEmpty,
  isNullOrUndefined,
} from '@facilio/utils/validation'
import { isChooserTypeField } from 'util/field-utils'
import Constants from 'util/constant'
import { isDropdownTypeField } from '@facilio/utils/field'

export default {
  data() {
    return {
      isFileTypeFields: ['FILE', 'IMAGE', 'SIGNATURE'],
    }
  },
  methods: {
    serializedData(formObj, formModel) {
      let { sections } = formObj
      let { isFileTypeFields, isV3Api, $getProperty, $setProperty } = this
      let { subFormData } = formModel
      let finalObj = {
        data: {},
      }
      if (!isEmpty(sections)) {
        sections.forEach(section => {
          let { fields } = section
          if (!isEmpty(fields)) {
            fields.forEach(field => {
              let {
                name,
                field: fieldObj,
                displayType,
                displayTypeEnum,
                lookupModuleName,
              } = field
              let { dataType, unitId, dataTypeEnum } = fieldObj || {}
              let value = formModel[name]
              if (isObject(value) && !isFileObject(value)) {
                if (!areValuesEmpty(value)) {
                  if (
                    name !== 'siteId' &&
                    name !== 'assignment' &&
                    (![21, 7].includes(dataType) ||
                      (fieldObj && !fieldObj.default && !isV3Api))
                  ) {
                    // custom lookup fields, value should not be an object
                    value = value.id
                  }
                } else if (
                  (isDropdownTypeField || isChooserTypeField) &&
                  !isV3Api
                ) {
                  if (dataType !== 7 || (fieldObj && !fieldObj.default)) {
                    value = -99
                  } else {
                    value.id = -99
                  }
                }
              } else if (
                !isV3Api &&
                (dataTypeEnum === 'NUMBER' || dataTypeEnum === 'DECIMAL')
              ) {
                if (isEmpty(value)) {
                  value = -99
                }
              }
              if (
                displayTypeEnum === 'NUMBER' ||
                displayTypeEnum === 'DECIMAL'
              ) {
                if (isV3Api ? !isNullOrUndefined(value) : !isEmpty(value)) {
                  if (displayTypeEnum === 'DECIMAL') {
                    value = parseFloat(value)
                  } else {
                    value = parseInt(value)
                  }
                }
                if (!isEmpty(unitId)) {
                  if (isV3Api) {
                    this.$set(finalObj, `${name}Unit`, unitId)
                  } else {
                    this.$set(finalObj.data, `${name}Unit`, unitId)
                  }
                }
              }
              if (displayType === 56) {
                // Recurring fields handling
                let { config } = field
                let { endFieldName, scheduleJsonName, startFieldName } = config
                finalObj[startFieldName] = formModel[startFieldName]
                finalObj[endFieldName] = formModel[endFieldName]
                finalObj[scheduleJsonName] = formModel[scheduleJsonName]
                finalObj.isRecurring = formModel.isRecurring
              } else if (displayTypeEnum === 'QUOTE_ADDRESS') {
                finalObj['shipToAddress'] = formModel.shipToAddress
                finalObj['billToAddress'] = formModel.billToAddress
              } else if (
                ['QUOTE_LINE_ITEMS', 'LINEITEMS'].includes(displayTypeEnum)
              ) {
                finalObj[name] = formModel[name]
                let { currencyCode, exchangeRate } = formModel || {}
                finalObj['currencyCode'] = currencyCode
                finalObj['exchangeRate'] = exchangeRate
                Constants.QUOTE_LINE_ITEMS_ADDITIONAL_FIELDS.forEach(
                  additionalField => {
                    if (additionalField === 'tax') {
                      if (
                        isEmpty(
                          $getProperty(formModel, `${additionalField}.id`)
                        )
                      ) {
                        $setProperty(formModel, `${additionalField}.id`, null)
                      }
                    }
                    finalObj[additionalField] = formModel[additionalField]
                  }
                )
              } else if (displayTypeEnum === 'FACILITY_BOOKING_SLOTS') {
                finalObj[name] = formModel[name]
                finalObj['bookingDate'] = formModel.bookingDate
              } else if (displayTypeEnum === 'MULTI_CURRENCY') {
                let { currencyCode, exchangeRate } = formModel || {}
                finalObj[name] = formModel[name]
                finalObj['currencyCode'] = currencyCode
                finalObj['exchangeRate'] = exchangeRate
              } else if (
                [
                  'BUDGET_AMOUNT',
                  'PERMIT_CHECKLIST',
                  'FACILITY_AVAILABILITY',
                ].includes(displayTypeEnum)
              ) {
                finalObj[name] = formModel[name]
              } else if (displayTypeEnum === 'ATTACHMENT') {
                finalObj[lookupModuleName] = formModel[lookupModuleName]
                let deletedItemKey = `${lookupModuleName}_delete`
                if ($getProperty(formModel, deletedItemKey) && isV3Api) {
                  finalObj[deletedItemKey] = formModel[deletedItemKey]
                }
              } else if (
                displayTypeEnum === 'COMMUNITY_PUBLISHING' &&
                isV3Api
              ) {
                finalObj[name] = value

                let deletedItemKey = `${name}_delete`
                if ($getProperty(formModel, deletedItemKey)) {
                  finalObj[deletedItemKey] = formModel[deletedItemKey]
                }
              } else if (displayTypeEnum === 'TICKETNOTES' && isV3Api) {
                if (!this.$helpers.isLicenseEnabled('NEW_COMMENTS')) {
                  finalObj[name] = [
                    {
                      body: formModel[name],
                      createdTime: Date.now(),
                      createdBy: (this.$account || {}).user,
                    },
                  ]
                } else {
                  let comment = formModel[name] || {}
                  $setProperty(comment, 'createdTime', Date.now())
                  $setProperty(comment, 'createdBy', (this.$account || {}).user)
                  finalObj[name] = [comment]
                }
              } else if (displayTypeEnum === 'URL_FIELD') {
                let urlFieldValue = formModel[name]
                let { href } = urlFieldValue || ''

                if (!isEmpty(href)) {
                  let isValidUrl =
                    href.startsWith('http://', 0) ||
                    href.startsWith('https://', 0)

                  if (isEmpty(href)) {
                    urlFieldValue = isV3Api ? null : {}
                  } else {
                    if (!isValidUrl) {
                      urlFieldValue.href = 'http://' + href
                    }
                  }
                } else {
                  urlFieldValue = null
                }
                if (isV3Api) {
                  finalObj[name] = urlFieldValue
                } else {
                  finalObj.data[name] = urlFieldValue
                }
              } else if (displayTypeEnum === 'JP_TASK') {
                let tasks = formModel[name]
                tasks = this.serializeJobPlanTasks(tasks)
                if (isV3Api) {
                  finalObj[name] = tasks
                }
              } else if (displayTypeEnum === 'JP_PREREQUISITE') {
                let preRequisites = formModel[name]
                preRequisites = this.serializePrerequisites(preRequisites)
                if (isV3Api) {
                  finalObj[name] = preRequisites
                }
              } else {
                if (dataTypeEnum === 'MULTI_LOOKUP') {
                  value = (formModel[name] || []).map(currId => {
                    return { id: currId }
                  })
                  finalObj.data[name] = value
                }
                // For text type fields, in edit case value should be sent even if it is empty
                if (name === 'siteId' && !isEmpty(value)) {
                  finalObj[name] = value.id
                } else if (name === 'assignment' && !isEmpty(value)) {
                  // Team, staff assignment related fields
                  for (let assignmentObj in value) {
                    if (!isEmpty(value[assignmentObj].id)) {
                      finalObj[assignmentObj] = {
                        id: value[assignmentObj].id,
                      }
                    } else {
                      finalObj[assignmentObj] = isV3Api ? null : { id: -99 }
                    }
                  }
                } else if (isFileTypeFields.includes(displayTypeEnum)) {
                  finalObj = this.fileFieldHandler(finalObj, field, value)
                } else if (isV3Api) {
                  finalObj[name] = value
                } else if (fieldObj && !fieldObj.default) {
                  // Custom field handling
                  if (!isEmpty(value)) {
                    finalObj.data[name] = value
                  }
                } else {
                  // System field handling
                  finalObj = this.systemFieldHandler(finalObj, field, value)
                }
              }
            })
          }
        })
      }
      if (!isNullOrUndefined(formModel.notifyRequester)) {
        finalObj.notifyRequester = formModel.notifyRequester
      }
      if (!isEmpty(finalObj) && isV3Api) {
        delete finalObj.data
        if (!isEmpty(subFormData)) {
          finalObj = {
            ...finalObj,
            ...subFormData,
          }
          subFormData = {}
        }
        finalObj = this.v3NullHandler(finalObj)
      }
      if (!isEmpty(subFormData)) {
        finalObj.subFormData = subFormData
      }
      return finalObj
    },
    v3NullHandler(data) {
      if (!isEmpty(data)) {
        Object.entries(data).forEach(([key, value]) => {
          if (isObject(value) && !isEmpty(value) && key !== 'relations') {
            if (areValuesEmpty(value)) {
              data[key] = null
            }
          }
        })
      }
      return data
    },
    systemFieldHandler(finalObj, field, value) {
      let { name } = field
      if (!isEmpty(value)) {
        finalObj[name] = value
      }
      return finalObj
    },
    serializeJobPlanTasks(taskList) {
      let serializedSection = null
      let inputHash = { 2: 'Reading', 3: 'Text', 4: 'Number', 5: 'Option' }
      if (!isEmpty(taskList)) {
        serializedSection = taskList.map((section, index) => {
          let {
            tasks = [],
            jobPlanSectionCategory,
            name,
            inputType,
            inputOptions = [],
          } = section || {}

          section = { ...section, name: `JP-${name}` }
          if (isEmpty(inputType)) {
            section = { ...section, inputType: 1 }
          }
          if (isEmpty(jobPlanSectionCategory)) {
            section = { ...section, jobPlanSectionCategory: 5 }
          }
          if (!isEmpty(inputOptions)) {
            let sequence = 1
            inputOptions.forEach(option => {
              option.sequence = sequence++
            })

            section = { ...section, inputOptions }
          }
          // Option Type
          if (inputHash[inputType] === 'Option') {
            let {
              inputOptions,
              remarkOptionValues,
              attachmentOptionValues,
              defaultValue,
              failureValue,
            } = section || {}

            if (!isEmpty(defaultValue)) {
              defaultValue = (inputOptions[defaultValue] || {}).value
              let selectedDefaultValue = inputOptions[defaultValue] || {}
              failureValue = this.$getProperty(
                selectedDefaultValue,
                'value',
                ''
              )
            }
            if (!isEmpty(failureValue)) {
              let selectedFailureValue = inputOptions[failureValue] || {}
              failureValue = this.$getProperty(
                selectedFailureValue,
                'value',
                ''
              )
            }
            if (!isEmpty(attachmentOptionValues)) {
              attachmentOptionValues = (attachmentOptionValues || []).map(
                option => {
                  let selectedOption = inputOptions[option]
                  let { value } = selectedOption || {}
                  return value
                }
              )
            }
            if (!isEmpty(remarkOptionValues)) {
              remarkOptionValues = (remarkOptionValues || []).map(option => {
                let selectedOption = inputOptions[option]
                let { value } = selectedOption || {}
                return value
              })
            }

            section = {
              ...section,
              remarkOptionValues,
              attachmentOptionValues,
              defaultValue,
              failureValue,
            }
          }

          //Removing unwanted nodes
          let excludeFields = ['tasks', 'sectionId', 'resource']
          excludeFields.forEach(
            excludeField => delete (section || {})[excludeField]
          )

          let serializedTasks = tasks.map(task => {
            let {
              jobPlanTaskCategory,
              inputOptions = [],
              taskId,
              inputType: taskInputType,
            } = task || {}

            task = {
              ...task,
              taskId: Number(taskId),
              sequence: Number(taskId),
            }
            if (isEmpty(jobPlanTaskCategory)) {
              task = { ...task, jobPlanTaskCategory: 5 }
            }
            if (isEmpty(taskInputType)) {
              task = { ...task, inputType: 1 }
            }
            if (!isEmpty(inputOptions)) {
              let sequence = 1
              inputOptions.forEach(option => {
                option.sequence = sequence++
              })
              task = { ...task, inputOptions }
            }

            if (inputHash[taskInputType] === 'Option') {
              let {
                inputOptions: taskInputOptions = [],
                remarkOptionValues: taskRemarkOptions,
                attachmentOptionValues: taskAttachmentOptions,
                defaultValue: taskDefaultValue,
                failureValue: taskFailureValue,
              } = task || {}

              if (!isEmpty(taskDefaultValue)) {
                let selectedDefaultValue =
                  taskInputOptions[taskDefaultValue] || {}
                taskDefaultValue = this.$getProperty(
                  selectedDefaultValue,
                  'value',
                  ''
                )
              }
              if (!isEmpty(taskFailureValue)) {
                let selectedFailureValue =
                  taskInputOptions[taskFailureValue] || {}
                taskFailureValue = this.$getProperty(
                  selectedFailureValue,
                  'value',
                  ''
                )
              }
              if (!isEmpty(taskAttachmentOptions)) {
                taskAttachmentOptions = (taskAttachmentOptions || []).map(
                  option => {
                    let selectedOption = taskInputOptions[option]
                    let { value } = selectedOption || {}
                    return value
                  }
                )
              }
              if (!isEmpty(taskRemarkOptions)) {
                taskRemarkOptions = (taskRemarkOptions || []).map(option => {
                  let selectedOption = taskInputOptions[option]
                  let { value } = selectedOption || {}
                  return value
                })
              }

              task = {
                ...task,
                remarkOptionValues: taskRemarkOptions,
                attachmentOptionValues: taskAttachmentOptions,
                defaultValue: taskDefaultValue,
                failureValue: taskFailureValue,
              }
            }

            //Removing unwanted nodes
            let excludeFields = [
              'resource',
              'sectionId',
              'jobPlanResource',
              'sectionResource',
              'jobPlanCategory',
              'readings',
              'sequenceNumber',
            ]
            excludeFields.forEach(
              excludeField => delete (task || {})[excludeField]
            )

            task = { ...task, statusNew: 1 }
            return task
          })
          section.tasks = serializedTasks
          return { ...section, sequenceNumber: index + 1 }
        })
      }
      return serializedSection
    },
    serializePrerequisites(prerequisiteList = []) {
      let serializedPrereqs = null
      serializedPrereqs = prerequisiteList.map(section => {
        let { enableOption, options, attachmentRequired, tasks, name } =
          section || {}
        let serializedTasks = tasks.map(task => {
          let { enableOption, options, attachmentRequired, subject } =
            task || {}
          return { subject, enableOption, options, attachmentRequired }
        })
        return {
          name,
          enableOption,
          options,
          attachmentRequired,
          tasks: serializedTasks,
        }
      })
      return serializedPrereqs
    },
    fileFieldHandler(finalObj, field, value) {
      let { isV3Api, isEdit } = this
      let { field: fieldObj, name } = field

      if (!isEmpty(value)) {
        if (typeof value === 'string') {
          try {
            value = JSON.parse(value)
          } catch (e) {
            this.$message.warning(this.$t('common._common.error_parsing_value'))
          }
          let defaultFileObj = value[0] || {}
          let { fileId } = defaultFileObj || {}
          value = fileId
        }
      }
      if (isV3Api) {
        finalObj[`${name}Id`] = value
      } else if (fieldObj && !fieldObj.default) {
        // Custom file field handling
        if (isEmpty(value) && isEdit) {
          // To reset the file fields on edit
          finalObj.data[`${name}Id`] = -99
        } else {
          finalObj.data[`${name}Id`] = value
        }
      } else {
        // System file field handling
        if (isEmpty(value) && isEdit) {
          // To reset the file fields on edit
          finalObj[`${name}Id`] = -99
        } else {
          finalObj[`${name}Id`] = value
        }
      }
      return finalObj
    },
  },
}
</script>
