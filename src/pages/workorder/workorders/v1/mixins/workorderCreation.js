import { isEmpty, isObject } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import helpers from 'src/util/helpers.js'
export default {
  methods: {
    serializePayloadForV3(data) {
      let model = {
        subject: data.subject,
        siteId: data.siteId.id,
      }

      // user assignmemt
      const assignment = data.assignment
      if (
        assignment &&
        assignment.assignedTo &&
        !isEmpty(assignment.assignedTo.id)
      ) {
        model.assignedTo = assignment.assignedTo
      }

      // group assignmemt
      if (
        assignment &&
        assignment.assignmentGroup &&
        !isEmpty(assignment.assignmentGroup.id)
      ) {
        model.assignmentGroup = assignment.assignmentGroup
      }
      // console.log({data})
      // console.log({model})
      return model
    },
    serializedData(formData, _formModel) {
      let {
        formObj: { sections },
        moduleDataId,
        preFillValueForCreationObj,
        isFileTypeFields,
      } = this
      let { subFormData } = _formModel || {}
      let isParentWoFieldAvailable = false
      sections.forEach(section => {
        let { fields } = section
        if (!isEmpty(fields)) {
          fields.forEach(field => {
            let { name, field: fieldObj, displayTypeEnum } = field
            let { dataTypeEnum } = fieldObj || {}
            let value = _formModel[name]
            if (dataTypeEnum === 'NUMBER' || dataTypeEnum === 'DECIMAL') {
              if (isEmpty(value)) {
                value = -99
              }
            }
            if (dataTypeEnum === 'MULTI_LOOKUP') {
              value = (value || []).map(currId => {
                return { id: currId }
              })
            }
            if (!isEmpty(value) && displayTypeEnum !== 'TASKS') {
              // To differentiate between system and custom fields
              if (fieldObj && !fieldObj.default) {
                if (isObject(value)) {
                  // For dropdown custom fields have to fetch the value from id
                  if (!isEmpty(value.id)) {
                    // Field data type 7 is lookup field
                    formData.workorder.data[name] = value.id
                  } else if (displayTypeEnum === 'URL_FIELD') {
                    let value = _formModel[name]
                    let { href } = value || {}
                    if (!isEmpty(href)) {
                      let isValidUrl =
                        href.startsWith('http://', 0) ||
                        href.startsWith('https://', 0)

                      if (!isValidUrl) {
                        value.href = 'http://' + href
                      }
                    } else {
                      value = null
                    }
                    formData.workorder.data[name] = value
                  }
                } else if (isFileTypeFields.includes(displayTypeEnum)) {
                  formData.workorder = this.fileFieldHandler(
                    formData.workorder,
                    field,
                    value
                  )
                } else {
                  formData.workorder.data[name] = value
                }
              } else {
                if (name === 'siteId' || displayTypeEnum === 'URGENCY') {
                  formData.workorder[name] = value.id
                } else if (name === 'assignment') {
                  for (let assignmentObj in value) {
                    if (!isEmpty(value[assignmentObj].id)) {
                      formData.workorder[assignmentObj] = {
                        id: value[assignmentObj].id,
                      }
                    }
                  }
                } else if (isObject(value) && isEmpty(value.id)) {
                  formData.workorder[name] = null
                } else {
                  formData.workorder[name] = value
                }
              }
            }
            if (name === 'parentWO') {
              isParentWoFieldAvailable = true
            }
          })
        }
      })
      if (!isParentWoFieldAvailable && !isEmpty(moduleDataId)) {
        formData.workorder['parentWO.id'] = moduleDataId
      }
      if (!isEmpty(subFormData)) {
        formData.workorder['subFormData'] = subFormData
      }
      if (!isEmpty(preFillValueForCreationObj)) {
        for (let [key] of Object.entries(preFillValueForCreationObj)) {
          let keyValue = formData.workorder[key]
          if (!isEmpty(keyValue)) {
            if (['tenant', 'resource', 'serviceRequest'].includes(key)) {
              formData.workorder[`${key}.id`] = parseInt(
                preFillValueForCreationObj[`${key}`]
              )
            }
          }
        }
      }
      return formData
    },
    removeBlanks(model) {
      for (const key in model) {
        const value = model[key]
        if (value === null || value.id === null) {
          delete model[key]
        }
      }
      if (model.resource && model.resource.id === undefined) {
        delete model.resource
      }
      return model
    },
    saveRecord(formModel) {
      const { isLicenseEnabled } = helpers
      const woV3BetaLicense = isLicenseEnabled('WOV3_BETA')

      this.isSaving = true

      if (woV3BetaLicense) {
        let { tasksString, ticketattachments } = formModel

        let model = this.serializePayloadForV3(formModel)

        if (!isEmpty(ticketattachments)) {
          model['ticketattachments'] = ticketattachments
        }

        if (!isEmpty(tasksString)) {
          model['tasksString'] = tasksString
        }

        let params = {
          data: model,
          moduleName: 'workorder',
        }

        API.post('v3/modules/data/create', params)
          .then(response => {
            this.successHandler(response)
          })
          .finally(() => (this.isSaving = false))
      } else {
        //console.log('creating wo with v2')
        let { tasksString, ticketattachments } = formModel
        delete formModel.ticketattachments
        delete formModel.tasksString

        let { selectedForm } = this
        let formId = (selectedForm || {}).id

        let formData = {
          workorder: {
            data: {},
            formId: formId || -1,
          },
        }

        this.isSaving = true

        let serializedData = this.serializedData(formData, formModel)

        if (!isEmpty(ticketattachments)) {
          serializedData['ticketattachments'] = ticketattachments
        }
        if (!isEmpty(tasksString)) {
          serializedData['tasksString'] = JSON.stringify(tasksString)
        }

        API.post('v2/workorders/add', serializedData)
          .then(response => {
            this.successHandler(response)
          })
          .finally(() => (this.isSaving = false))
      }
    },
  },
}
