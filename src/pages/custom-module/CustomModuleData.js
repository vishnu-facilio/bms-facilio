import { ModuleData, displayTypeProp } from '@facilio/data'
import { API } from '@facilio/api'
import getProperty from 'dlv'
import setProperty from 'dset'
import cloneDeep from 'lodash/cloneDeep'
import { getBaseURL } from 'util/baseUrl'
import store from 'src/store/index.js'
import Constants from 'util/constant'
import Vue from 'vue'
import {
  isEmpty,
  isObject,
  areValuesEmpty,
  isNullOrUndefined,
  isFunction,
} from '@facilio/utils/validation'

const state = store.state
const getters = store.getters

export class CustomModuleData extends ModuleData {
  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { assignedTo, assignmentGroup } = instance
      let fieldValue = {
        assignedTo: { id: assignedTo || '' },
        assignmentGroup: { id: assignmentGroup || '' },
      }

      setProperty(fieldObj, 'value', fieldValue)
      return fieldObj
    },
    serialize: (finalObj, field, formModel) => {
      let { assignment } = formModel || {}
      let { assignedTo } = assignment || {}
      let { assignmentGroup } = assignment || {}

      if (assignedTo.id == null && assignmentGroup.id == null) {
        finalObj['assignedTo'] = null
        finalObj['assignmentGroup'] = null
        return finalObj
      } else if (assignedTo.id == null && assignmentGroup.id != null) {
        finalObj['assignedTo'] = null
        finalObj['assignmentGroup'] = assignmentGroup
        return finalObj
      } else if (assignedTo.id != null && assignmentGroup.id == null) {
        finalObj['assignedTo'] = assignedTo
        finalObj['assignmentGroup'] = null
        return finalObj
      } else {
        finalObj['assignedTo'] = assignedTo
        finalObj['assignmentGroup'] = assignmentGroup
        return finalObj
      }
    },
  })
  TEAMSTAFFASSIGNMENT

  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { name } = fieldObj || {}
      let value
      if (name === 'siteId') {
        value = instance[name] || ''
      } else {
        value = (instance[name] || {}).id
      }
      setProperty(fieldObj, 'value', value)
      return fieldObj
    },
  })
  LOOKUP_SIMPLE

  @displayTypeProp({
    serialize: (finalObj, field, formModel) => {
      let { name } = field || {}
      finalObj[name] = (formModel[name] || []).map(currId => {
        return { id: currId }
      })
      return finalObj
    },
  })
  MULTI_LOOKUP_SIMPLE

  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { name } = fieldObj || {}
      setProperty(fieldObj, 'value', (instance[name] || {}).id)
      return fieldObj
    },
  })
  REQUESTER

  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { name } = fieldObj || {}
      let fieldValue = instance[name] || {
        ...Constants.ADDRESS_FIELD_DEFAULTS,
      }
      setProperty(fieldObj, 'value', fieldValue)
      return fieldObj
    },
  })
  SADDRESS

  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { name } = fieldObj || {}
      let fieldValue = instance[name] || {
        ...Constants.ADDRESS_FIELD_DEFAULTS,
      }
      setProperty(fieldObj, 'value', fieldValue)
      return fieldObj
    },
  })
  ADDRESS

  @displayTypeProp({
    serialize: (finalObj, field, formModel) => {
      let { name, field: fieldObj } = field || {}
      let { unitId } = fieldObj || {}
      let value = formModel[name]

      if (!isEmpty(unitId)) {
        setProperty(finalObj, `${name}Unit`, unitId)
      }
      finalObj[name] = parseInt(value)

      return finalObj
    },
  })
  NUMBER

  @displayTypeProp({
    serialize: (finalObj, field, formModel) => {
      let { name, field: fieldObj } = field || {}
      let { unitId } = fieldObj || {}
      let value = formModel[name]

      if (!isEmpty(unitId)) {
        setProperty(finalObj, `${name}Unit`, unitId)
      }
      finalObj[name] = parseFloat(value)

      return finalObj
    },
  })
  DECIMAL

  @displayTypeProp({
    serialize: (finalObj, field, formModel) => {
      let { lookupModuleName } = field || {}
      let deletedItemKey = `${lookupModuleName}_delete`

      finalObj[lookupModuleName] = formModel[lookupModuleName]
      if (getProperty(formModel, deletedItemKey)) {
        finalObj[deletedItemKey] = formModel[deletedItemKey]
      }
      return finalObj
    },
  })
  ATTACHMENT

  @displayTypeProp({
    serialize: (finalObj, field, formModel) => {
      let { name } = field || {}
      if (!this.$helpers.isLicenseEnabled('NEW_COMMENTS')) {
        finalObj[name] = [
          {
            body: formModel[name],
            createdTime: Date.now(),
            createdBy: (Vue.prototype.$account || {}).user,
          },
        ]
      } else {
        let comment = formModel[name] || {}
        this.$setProperty(comment, 'createdTime', Date.now())
        this.$setProperty(comment, 'createdBy', (this.$account || {}).user)
        finalObj[name] = [comment]
      }
      return finalObj
    },
  })
  TICKETNOTES

  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { name } = fieldObj
      let photoFieldName = `${name}Id`
      let imageId = instance[photoFieldName]
      let imgUrl = instance.getImage(photoFieldName)

      setProperty(fieldObj, 'imgUrl', imageId ? imgUrl : null)
      setProperty(fieldObj, 'value', imageId)
      return fieldObj
    },
    serialize: (finalObj, field, formModel) => {
      let { name } = field || {}
      finalObj[`${name}Id`] = formModel[name]
      return finalObj
    },
  })
  IMAGE

  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { name } = fieldObj
      let photoFieldName = `${name}Id`
      let imageId = instance[photoFieldName]
      let imgUrl = instance.getImage(photoFieldName)
      setProperty(fieldObj, 'imgUrl', imageId ? imgUrl : null)
      setProperty(fieldObj, 'value', imageId)
      return fieldObj
    },
    serialize: (finalObj, field, formModel) => {
      let { name } = field || {}
      finalObj[`${name}Id`] = formModel[name]
      return finalObj
    },
  })
  SIGNATURE

  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { name } = fieldObj || {}
      let fileId = instance[`${name}Id`]
      let fileObj = { name: instance[`${name}FileName`] }
      setProperty(fieldObj, 'fileObj', fileId ? fileObj : null)
      setProperty(fieldObj, 'value', fileId)
      return fieldObj
    },
    serialize: (finalObj, field, formModel) => {
      let { name } = field || {}
      let value = formModel[name]
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
      finalObj[`${name}Id`] = value
      return finalObj
    },
  })
  FILE

  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { name: fieldName } = fieldObj || {}
      let fieldValue = instance[fieldName]
      let value = { href: '', name: '' }

      if (!isEmpty(fieldValue)) {
        let { href, name } = fieldValue || {}
        value = { href, name }
      }
      setProperty(fieldObj, 'value', value)
      return fieldObj
    },
    serialize: (finalObj, field, formModel) => {
      let { name } = field || {}
      let value = formModel[name] || {}

      let { href } = value || {}

      if (isEmpty(href)) {
        formModel[name] = null
      } else {
        formModel[name] = value
        let isValidUrl =
          href.startsWith('http://', 0) || href.startsWith('https://', 0)

        if (!isValidUrl) {
          formModel[name].href = 'http://' + href
        }
      }
      finalObj[name] = formModel[name]
      return finalObj
    },
  })
  URL_FIELD

  @displayTypeProp({
    deserialize: finalObj => {
      return finalObj
    },
  })
  NOTES

  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { name: fieldName } = fieldObj || {}
      let fieldValue = instance[fieldName]
      let value = {
        currencyCode: null,
        currencyValue: null,
      }
      if (!isEmpty(fieldValue)) {
        let { currencyCode, currencyValue } = fieldValue || {}
        value = { currencyCode, currencyValue }
      }
      setProperty(fieldObj, 'value', value)
      return fieldObj
    },
    serialize: (finalObj, field, formModel) => {
      let { name } = field || {}
      finalObj[name] = formModel[name]
      return finalObj
    },
  })
  CURRENCY

  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { name: fieldName } = fieldObj || {}
      let fieldValue = instance[fieldName]
      setProperty(fieldObj, 'value', fieldValue)
      return fieldObj
    },
    serialize: (finalObj, field, formModel, _this) => {
      let { name } = field || {}
      finalObj[name] = formModel[name]
      finalObj.currencyCode = formModel.currencyCode || null
      finalObj.exchangeRate = formModel.exchangeRate || null

      isEmpty(_this.id) &&
        Vue.cookie.set('userCurrency', formModel.currencyCode)

      return finalObj
    },
  })
  MULTI_CURRENCY

  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let fieldValue = null
      let { name } = fieldObj || {}
      let value = instance[name] || {}
      if (!isEmpty(value)) {
        let isStringField =
          fieldObj?.field?.dataTypeEnum === 'STRING' &&
          fieldObj?.displayTypeEnum === 'GEO_LOCATION'
        if (!isObject(value)) {
          if (!isStringField) {
            value = JSON.parse(value)
          } else {
            let [lat, lng] = value.split(',') || []
            lat = !isEmpty(lat) ? parseFloat(lat) : null
            lng = !isEmpty(lng) ? parseFloat(lng) : null
            value = { lat, lng }
          }
        }

        fieldValue = { id: value.id || -99 }
        fieldValue.lat = value?.lat
        fieldValue.lng = value?.lng
      }
      setProperty(fieldObj, 'value', fieldValue)

      return fieldObj
    },
    serialize: (finalObj, field, formModel) => {
      let { name } = field || {}
      finalObj[name] = formModel[name]
      let isStringField =
        field?.displayTypeEnum === 'GEO_LOCATION' &&
        field?.field?.dataTypeEnum === 'STRING'
      if (isStringField) {
        let lat = formModel[name]?.lat
        let lng = formModel[name]?.lng
        let stringLocationValue = ''
        if (!isEmpty(lat) && !isEmpty(lng)) {
          stringLocationValue = `${lat},` + `${lng}`
        }
        finalObj[name] = stringLocationValue
      }
      return finalObj
    },
  })
  GEO_LOCATION

  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { name } = fieldObj || {}
      let value = (instance[name] || {}).id
      setProperty(fieldObj, 'value', value)
      return fieldObj
    },
  })
  SPACECHOOSER

  static recordListCount = null
  static customButtonList = []

  static async fetchAllRecords(payload) {
    let { moduleName, viewname, filters, perPage, force = false } =
      payload || {}
    let params = {
      fetchOnlyViewGroupColumn: true,
      ...(payload || {}),
      viewName: viewname,
      filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
      perPage: perPage || 50,
      withoutCustomButtons: true,
      force: null,
    }

    let config = { force, uniqueKey: `${moduleName}_LIST` }
    let { list, error, meta } = await API.fetchAll(moduleName, params, config)

    if (error) {
      console.warn(`${moduleName} fetchRecord API failed or cancelled`)
      let { isCancelled } = error || {}
      if (!isCancelled) throw error
    } else {
      this.recordListCount = getProperty(meta, 'pagination.totalCount', null)
      return { data: list || [], moduleName }
    }
  }
  static async loadCustomButtons({ moduleName, record, position, force }) {
    let { id = null } = record || {}
    let params = { moduleName, id, positionType: position }
    let config = {
      force,
    }
    let url = '/v2/custombutton/getAvailableButtons'

    let { data } = await API.post(url, params, config)
    let { workflowRuleList } = data || {}

    return workflowRuleList || []
  }
  static async loadCustomButtonsForRecords({
    moduleName,
    recordIds = [],
    position,
    force,
  }) {
    let params = {
      moduleName,
      recordIds,
      positionTypes: position,
    }
    let url = '/v2/custombutton/getAvailableButtonsForRecords'
    let config = { force }

    if (!isEmpty(recordIds)) {
      let { error, data } = await API.get(url, params, config)

      if (!isEmpty(error)) {
        let { isCancelled } = error || {}
        if (!isCancelled) throw error
      } else {
        let { customButtons = [], customButtonsforRecords = [] } = data || {}
        return { customButtons, customButtonsforRecords }
      }
    } else {
      return { customButtons: [], customButtonsforRecords: [] }
    }
  }
  static async fetchRecordsCount({
    moduleName,
    viewname,
    filters,
    search,
    additionalParams,
    force = false,
  }) {
    let config = { force, uniqueKey: `${moduleName}_LIST_COUNT` }
    let params = {
      viewName: viewname,
      filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
      includeParentFilter: !isEmpty(filters),
      search,
      moduleName,
      ...(additionalParams || {}),
    }
    let url = 'v3/modules/data/count'
    let { data, error } = await API.get(url, params, config)

    if (error) {
      console.warn(`${moduleName} count API failed or cancelled`)
      let { isCancelled } = error || {}
      if (!isCancelled) throw error
    } else {
      return data.count || null
    }
  }
  static async fetchRecord({ moduleName, id, force = false }) {
    let { [moduleName]: data, error } = await API.fetchRecord(
      moduleName,
      { id },
      { force }
    )

    if (error) {
      console.warn(`${moduleName} fetchRecord API failed or cancelled`)
      let { isCancelled } = error || {}
      if (!isCancelled) throw error
    } else {
      return { data: data || {}, moduleName }
    }
  }

  static async deleteRecord(moduleName, idList) {
    let { error } = await API.deleteRecord(moduleName, idList)

    if (error) {
      console.warn(`${moduleName} deleteRecord API failed or cancelled`)
      let { isCancelled } = error || {}
      if (!isCancelled) throw error
    }
  }

  getImage(photoFieldName) {
    return `${getBaseURL()}/v2/files/preview/${this[photoFieldName]}`
  }

  getImageUrl(photoUrlFieldName) {
    return this[photoUrlFieldName]
  }

  isStateFlowEnabled() {
    let moduleMeta = state.view.metaInfo
    let hasState = getProperty(this.moduleState, 'id')
    let isCustomModule = getProperty(moduleMeta, 'module.custom')
    let isEnabled = getProperty(moduleMeta, 'module.stateFlowEnabled')

    return hasState && (!isCustomModule || (isCustomModule && isEnabled))
  }

  canEdit() {
    return !this.isRecordLocked() && !this.isRequestedState()
  }

  isRecordLocked() {
    let { moduleState, moduleName } = this

    if (this.isStateFlowEnabled) {
      let hasState = getProperty(moduleState, 'id')
      return hasState && getters.isStatusLocked(hasState, moduleName)
    }
    return false
  }

  isRequestedState() {
    let { approvalStatus } = this

    if (!isEmpty(approvalStatus)) {
      let statusObj = getters.getApprovalStatus(approvalStatus.id)
      return getProperty(statusObj, 'requestedState', false)
    }
    return false
  }

  isApprovalEnabled() {
    let { approvalFlowId, approvalStatus } = this
    return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
  }

  currentModuleState() {
    let { moduleName, moduleState } = this
    let currentStateId = getProperty(moduleState, 'id')
    let currentState = getters.getTicketStatus(currentStateId, moduleName)
    let { displayName, status } = currentState || {}
    return !isEmpty(displayName) ? displayName : status || null
  }

  static async loadFormsList(moduleName) {
    let { data, error } = await API.get('/v2/forms', { moduleName })

    if (error) {
      console.warn(`${moduleName} /v2/forms API failed or cancelled`)
      let { isCancelled } = error || {}
      if (!isCancelled) throw error
    } else {
      let { forms } = data || {}
      return forms || []
    }
  }

  static async loadFormData({ formId, moduleName, selectedForm }) {
    let { id, name } = selectedForm
    let params = { fetchFormRuleFields: true, forCreate: true }

    if (id === -1) {
      params = { ...params, formName: name }
    } else {
      params = { ...params, formId: formId || id }
    }

    let { data, error } = await API.get(`/v2/forms/${moduleName}`, params)
    if (error) {
      console.warn(`/v2/forms/${moduleName} formDetail API failed or cancelled`)
      let { isCancelled } = error || {}
      if (!isCancelled) throw error
    } else {
      let { form } = data || {}
      return form
    }
  }

  static async loadWidgets(filters) {
    let params = { filters: JSON.stringify(filters) }

    let { data, error } = await API.get('/v2/connectedApps/widgetList', params)

    if (error) {
      console.warn(`/v2/connectedApps/widgetList API failed or cancelled`)
      let { isCancelled } = error || {}
      if (!isCancelled) throw error
    } else {
      return data || {}
    }
  }

  async saveRecord(serilaizedData) {
    let { moduleName } = this
    let { id } = serilaizedData || {}

    try {
      if (isEmpty(id)) {
        return await API.createRecord(moduleName, {
          data: serilaizedData,
        })
      } else {
        return await API.updateRecord(moduleName, {
          id: id,
          data: serilaizedData,
        })
      }
    } catch (error) {
      console.warn(`${moduleName} create/update record API failed or cancelled`)
    }
  }

  deserialize(formObj) {
    let copiedFormObj = cloneDeep(formObj || {})
    let { sections } = copiedFormObj || {}

    if (!isEmpty(sections)) {
      sections.forEach(section => {
        let { fields } = section || {}

        if (!isEmpty(fields)) {
          let deserializedFields = fields.map(field => {
            return this.deserializeField(field)
          })
          setProperty(section, 'fields', deserializedFields)
        }
      })
    }
    return copiedFormObj
  }

  serialize(formObj, formModel, afterSerializeHook) {
    let { sections } = formObj
    let { subFormData, formId } = formModel
    let finalObj = {}

    if (!isEmpty(sections)) {
      sections.forEach(section => {
        let { fields } = section

        if (!isEmpty(fields)) {
          fields.forEach(field => {
            finalObj = this.serializeField(finalObj, field, formModel)
          })
        }
      })
    }

    if (!isNullOrUndefined(formModel.notifyRequester)) {
      finalObj.notifyRequester = formModel.notifyRequester
    }

    if (!isEmpty(finalObj)) {
      if (!isEmpty(subFormData)) {
        finalObj = { ...finalObj, ...subFormData }
      }
      finalObj = this.v3NullHandler(finalObj)
    }

    if (!isEmpty(formId)) {
      finalObj.formId = formId
    }
    if (!isEmpty(this.id)) {
      finalObj.id = this.id
    }

    if (!isEmpty(afterSerializeHook) && isFunction(afterSerializeHook)) {
      finalObj = afterSerializeHook({
        data: finalObj,
        formModel,
        formObj,
      })
    }
    return finalObj
  }

  v3NullHandler(data) {
    Object.entries(data || {}).forEach(([key, value]) => {
      let isValueEmpty =
        isObject(value) &&
        !isEmpty(value) &&
        key !== 'relations' &&
        areValuesEmpty(value)

      if (isValueEmpty) {
        data[key] = null
      }
    })
    return data
  }
}
