import http from 'util/http'
import { Message } from 'element-ui'
import * as d3 from 'd3'
import Vue from 'vue'
import store from '../store'
import { isArray, isEmpty } from '@facilio/utils/validation'
import Constants from './constant'
import getProperty from 'dlv'
import { API } from '@facilio/api'

export default {
  /**
   * Returns asset list. If spaceid is given, then the assets associated with that space will be returned
   *
   * @param {Long} spaceId
   * @param {Long} categoryId - filter assets based on category
   * @param {String} searchText - filter assets based on name
   */
  loadAsset({
    spaceId = -1,
    categoryId = -1,
    filters = null,
    filterCriterias = null,
    searchText = null,
    withReadings = false,
    withWritableReadings = false,
    paging = null,
    isService = null,
    getCategoryDetails = false,
    readingId = null,
    inputType = null,
    viewName = null,
    id = null,
    site = null,
    selectFields = null,
    spaceIds = [],
    fetchPrimaryDetails = false,
  }) {
    let filter = null
    if (spaceId > 0 && spaceIds && spaceIds.length > 0) {
      console.log('invalid usage')
      return
    }
    if (spaceId > 0) {
      filter = { space: { operator: 'building_is', value: [spaceId + ''] } }
    }
    if (spaceIds && spaceIds.length > 0) {
      // converting spaceIds to array of spaceId string
      let spaceIdsStrList = []
      if (spaceIds.length > 1) {
        for (let i = 0; i < spaceIds.length; i++) {
          if (spaceIds[i] > 0) {
            spaceIdsStrList.push(String(spaceIds[i]))
          }
        }
      }
      if (spaceIdsStrList) {
        filter = { space: { operator: 'building_is', value: spaceIdsStrList } }
      }
    }
    if (site > 0) {
      filter = { site: { operator: '=', value: [site + ''] } }
    }
    if (filters) {
      if (!filter) {
        filter = {}
      }
      Object.assign(filter, filters)
    }
    let filterCriteria = null
    if (filterCriterias) {
      if (!filterCriterias) {
        filterCriteria = {}
      }
      filterCriteria = filterCriterias
    }
    let url = '/assets/view/'
    if (viewName) {
      url += viewName + '?'
    } else {
      url += 'all?'
    }
    if (withReadings) {
      url += 'withReadings=true'
    }
    if (withWritableReadings) {
      url += 'withWritableReadings=true'
    }
    if (categoryId > 0) {
      if (getCategoryDetails) {
        url += '&categoryId=' + categoryId
      } else {
        if (!filter) {
          filter = {}
        }
        filter.category = { operator: 'is', value: [categoryId + ''] }
      }
    }
    if (readingId) {
      url += '&readingId=' + readingId
    }
    if (inputType) {
      url += '&inputType=' + inputType
    }
    if (fetchPrimaryDetails) {
      url += '&fetchPrimaryDetails=true'
    }
    if (selectFields && selectFields.length) {
      for (let i = 0; i < selectFields.length; i++) {
        url += `&selectFields[${i}]=${selectFields[i]}`
      }
    }
    if (filterCriteria && Object.keys(filterCriteria).length) {
      url +=
        '&filterCriteria=' + encodeURIComponent(JSON.stringify(filterCriteria))
    }
    return v2get(url, { filter, searchText, paging })
  },
  async getFilteredAssetList(filters) {
    let params = { filters: !isEmpty(filters) ? JSON.stringify(filters) : null }
    let { error, data } = await API.get('/asset/all', params)

    if (!error) {
      return data.assets
    }
  },
  getAssetById(id) {
    return get('/asset/summary/' + id + '?fetchHierarchy=true').then(
      response => {
        if (response) {
          return response.asset
        }
      }
    )
  },
  formateSqft(key) {
    return key > 0 ? d3.format(',')(key) + ' sq. ft' : '---'
  },
  /**
   *
   * @param {Number} spaceType - to fetch only a specific type of space like buildings, floors...
   * 1 => site
   * 2 => buildings
   * 3 => floor
   * 4 => space
   * @param {String} searchText
   * @param {Array} spaceFilters - Array(Key-Value pair)... eg: [{key: "siteId", value: "22"}, {key: "floorId", value: "25"}]
   */
  loadSpace(spaceType, searchText, spaceFilters, paging, withReadings, isZone) {
    let filter = {}
    if (spaceType && spaceType === 1) {
      spaceFilters = {}
    } else if (spaceType) {
      filter.spaceType = {
        operator: '=',
        value: Array.isArray(spaceType)
          ? spaceType.map(type => type + '')
          : [spaceType + ''],
      }
    }
    if (spaceFilters) {
      if (isArray(spaceFilters)) {
        spaceFilters.forEach(data => {
          if (data.value || data.operator) {
            filter[data.key] = {
              operator: data.operator ? data.operator : 'is',
            }
            if (Array.isArray(data.value)) {
              filter[data.key].value = data.value.join().split(',')
            } else if (data.value) {
              filter[data.key].value = [data.value + '']
            }
          }
        })
      } else {
        Object.assign(filter, spaceFilters)
      }
    }
    let url = null
    if (Vue.prototype.$account.portalInfo) {
      url = '/basespace'
    } else {
      url = '/basespace'
    }
    if (withReadings) {
      url += '?withReadings=true'
    }
    return get(
      url,
      filter,
      searchText,
      { field: 'spaceType', type: 'asc', isZone: isZone },
      paging
    )
  },
  loadSpacesContext(spaceType, searchText, spaceFilters, paging) {
    let filter = {}
    if (spaceType) {
      filter.spaceType = { operator: '=', value: [spaceType + ''] }
    }
    if (spaceFilters && spaceFilters.length) {
      spaceFilters.forEach(data => {
        if (data.value || data.operator) {
          filter[data.key] = { operator: data.operator ? data.operator : 'is' }
          if (Array.isArray(data.value)) {
            filter[data.key].value = data.value.join().split(',')
          } else if (data.value) {
            filter[data.key].value = [data.value + '']
          }
        }
      })
    }
    return get(
      '/space',
      filter,
      searchText,
      { field: 'spaceType', type: 'asc' },
      paging
    )
  },
  // added this to handle loadSpaces in PMConfigForm
  loadSpacesContextPmConfigForm(spaceType, searchText, spaceFilters, paging) {
    let filter = {}
    if (spaceType) {
      filter.spaceType = { operator: '=', value: [spaceType + ''] }
    }
    if (spaceFilters && spaceFilters.length) {
      spaceFilters.forEach(data => {
        if (data.value || data.operator) {
          filter[data.key] = { operator: data.operator ? data.operator : 'is' }
          if (data.value instanceof Array) {
            filter[data.key].value = data.value
          } else {
            filter[data.key].value = [data.value + '']
          }
        }
      })
    }
    return get(
      '/space',
      filter,
      searchText,
      { field: 'spaceType', type: 'asc' },
      paging
    )
  },
  getDefaultTemplateActions(defaultIds) {
    return v2post('/template/defaultTemplate', { ids: defaultIds }).then(
      result => {
        let actions = {}
        Object.keys(result.default_template).forEach(function(key) {
          let template = result.default_template[key]
          let action = (actions[key] = {
            template: template,
          })
          if (template.name.indexOf('EMAIL') > 0) {
            action.actionType = 3
          } else if (template.name.indexOf('SMS') > 0) {
            action.actionType = 4
          } else if (template.name.indexOf('MOBILE') > 0) {
            action.actionType = 7
          }
        })
        return actions
      }
    )
  },
  getNewAnalyticsQuerryURLfromJSON(obj) {
    if (obj.paramsJson) {
      let url = null
      let paramsJson = obj.paramsJson
      let metaJson = JSON.parse(obj.metaJson)
      let params = {
        name: metaJson.title,
        key: 'BUILDING_ANALYSIS',
        analyticsType: 2,
        type: 'reading',
        period: this.getCardPeriodfromOperatorId(obj.paramsJson.dateOperator),
        mode: 1,
        baseLine: null,
        chartViewOption: 0,
        dataPoints: [
          {
            parentId: paramsJson.parentId,
            type: 1,
            yAxis: {
              fieldId: paramsJson.fieldId,
              aggr: paramsJson.aggregateFunc,
            },
          },
        ],
        dateFilter: paramsJson.dateFilter,
      }
      params.dateFilter.filter = params.dateFilter.option
      params.dateFilter.filterName = params.dateFilter.option
      url =
        '/app/em/analytics/building?filters=' +
        encodeURIComponent(JSON.stringify(params))
      return url
    } else {
      return false
    }
  },
  getCardPeriodfromOperatorId(id) {
    if (id) {
      if (id === 22) {
        return 20
      } else if (id === 25) {
        return 20
      } else if (id === 49) {
        return 12
      } else if (id === 30) {
        return 12
      } else if (id === 31) {
        return 12
      } else if (id === 27) {
        return 12
      } else if (id === 28) {
        return 12
      } else if (id === 44) {
        return 10
      } else {
        return 12
      }
    }
  },
  getCardLabelfromOperatorId(id) {
    if (id) {
      if (id === 22) {
        return 'hourly'
      } else if (id === 25) {
        return 'hourly'
      } else if (id === 49) {
        return 'daily'
      } else if (id === 30) {
        return 'daily'
      } else if (id === 31) {
        return 'daily'
      } else if (id === 27) {
        return 'daily'
      } else if (id === 28) {
        return 'daily'
      } else if (id === 44) {
        return 'monthly'
      } else {
        return 'daily'
      }
    }
  },
  getCardLabelfromAggId(id) {
    if (id) {
      if (id === 20) {
        return 'hourly'
      } else if (id === 12) {
        return 'daily'
      } else if (id === 0) {
        return 'high resolution'
      } else if (id === 10) {
        return 'monthly'
      } else {
        return 'daily'
      }
    }
  },
  loadOccupantValues(ids) {
    return post('v2/reading/occupantdata', {
      resourcesId: ids,
      fieldName: 'currentoccupancy',
      moduleName: 'currentoccupancyreading',
    }).then(response => {
      if (response) {
        if (response.responseCode === 0) {
          let occupantMap = {}
          let occupantdata = response.result.readingValues
          if (response.result.readingValues) {
            Object.keys(occupantdata).forEach(function(key) {
              let id = key.split('_')
              occupantMap[id[0]] = occupantdata[key].value
            })
          }
          return occupantMap
        }
      }
    })
  },
  loadModuleMeta(module) {
    if (module) {
      return v2get(`/modules/meta/${module}`).then(result => result.meta)
    }
  },
  loadResource(ids) {
    return post('v2/resource/getResourcesDetails', { resourceId: ids }).then(
      response => {
        if (response) {
          if (response.responseCode === 0) {
            return response.result.resource
          }
        }
      }
    )
  },
  loadReadingFields(resource, excludeEmptyFields) {
    if (resource && resource.resourceType === 2) {
      return this.loadAssetReadingFields(resource.id, -1, excludeEmptyFields)
    } else if (resource && resource.resourceType === 1) {
      return this.loadSpaceReadingFields(resource.id, excludeEmptyFields)
    } else {
      return Promise.resolve()
    }
  },
  loadAssetReadingFields(
    assetId,
    categoryId,
    excludeEmptyFields,
    type,
    fetchValidationRules
  ) {
    if (excludeEmptyFields !== false) {
      excludeEmptyFields = true
    }
    let url =
      assetId > 0
        ? '/reading/getassetspecificreadings?parentId=' + assetId
        : '/readings/assetcategory?id=' + categoryId
    url += '&excludeEmptyFields=' + excludeEmptyFields
    if (type) {
      url += '&readingType=' + type
    }
    if (fetchValidationRules) {
      url += '&fetchValidationRules=' + fetchValidationRules
    }
    let promise
    if (assetId > 0) {
      promise = get(url)
    } else {
      promise = v2get(url)
    }
    return promise.then(response => {
      let fields = []
      if (response) {
        if (assetId > 0) {
          response.forEach(reading => {
            reading.fields.forEach(field => {
              field.module = { name: reading.name }
              fields.push(field)
            })
          })
        } else {
          response.readings.forEach(field => {
            field.module = { name: field.module.name }
            if (fetchValidationRules && response.fieldVsRules) {
              field.readingRules = response.fieldVsRules[field.fieldId]
            }
            let operators = getProperty(field, 'dataTypeEnum.operators')
            if (operators) {
              for (let operatorKey in operators) {
                let operator = operators[operatorKey]
                if (Constants.excludeOperators.includes(operator.operatorId)) {
                  delete operators[operatorKey]
                }
              }
            }
            fields.push(field)
          })
        }
      }
      return fields
    })
  },
  loadMeterReadingFields(
    meterId,
    categoryId,
    excludeEmptyFields,
    type,
    fetchValidationRules
  ) {
    if (excludeEmptyFields !== false) {
      excludeEmptyFields = true
    }
    let url =
      meterId > 0
        ? '/reading/getmeterspecificreadings?parentId=' + meterId
        : '/readings/utilitytype?id=' + categoryId
    url += '&excludeEmptyFields=' + excludeEmptyFields
    if (type) {
      url += '&readingType=' + type
    }
    if (fetchValidationRules) {
      url += '&fetchValidationRules=' + fetchValidationRules
    }
    let promise
    if (meterId > 0) {
      // API for meter specific readings not added yet
      //promise = get(url)
    } else {
      promise = v2get(url)
      return promise.then(response => {
        let fields = []
        if (response) {
          if (meterId > 0) {
            response.forEach(reading => {
              reading.fields.forEach(field => {
                field.module = { name: reading.name }
                fields.push(field)
              })
            })
          } else {
            response.readings.forEach(field => {
              field.module = { name: field.module.name }
              if (fetchValidationRules && response.fieldVsRules) {
                field.readingRules = response.fieldVsRules[field.fieldId]
              }
              let operators = getProperty(field, 'dataTypeEnum.operators')
              if (operators) {
                for (let operatorKey in operators) {
                  let operator = operators[operatorKey]
                  if (
                    Constants.excludeOperators.includes(operator.operatorId)
                  ) {
                    delete operators[operatorKey]
                  }
                }
              }
              fields.push(field)
            })
          }
        }
        return fields
      })
    }
  },
  loadSpaceReadingFields(
    id,
    excludeEmptyFields,
    categoryId,
    isOnlyReadingFields,
    includeDefaultFields,
    type
  ) {
    if (excludeEmptyFields !== false) {
      excludeEmptyFields = true
    }
    let url =
      id && id > -1
        ? `/reading/getspacespecificreadings?parentId=${id}&excludeEmptyFields=${excludeEmptyFields}`
        : `/reading/getallspacereadings${
            categoryId > 0 ? `?categoryIds=${categoryId}` : ''
          }`
    if (type) {
      url += '&readingType=' + type
    }
    return get(url).then(readings => {
      if (id && id > -1) {
        let fields = []
        if (readings) {
          readings.forEach(reading => {
            if (isOnlyReadingFields) {
              if (reading.type === 3) {
                reading.fields.forEach(field => {
                  field.module = { name: reading.name }
                  fields.push(field)
                })
              }
            } else {
              if (reading.fields) {
                reading.fields.forEach(field => {
                  field.module = { name: reading.name }
                  fields.push(field)
                })
              }
            }
          })
        }
        return fields
      } else {
        let fields = []
        if (readings) {
          if (readings[categoryId]) {
            readings[categoryId].forEach(reading => {
              if (isOnlyReadingFields) {
                if (reading.type === 3) {
                  reading.fields.forEach(field => {
                    field.module = { name: field.name }
                    fields.push(field)
                  })
                }
              } else {
                reading.fields.forEach(field => {
                  field.module = { name: field.name }
                  fields.push(field)
                })
              }
            })
          }

          if (includeDefaultFields) {
            if (readings[-1]) {
              readings[-1].forEach(reading => {
                if (isOnlyReadingFields) {
                  if (reading.type === 3) {
                    reading.fields.forEach(field => {
                      field.module = { name: field.name }
                      fields.push(field)
                    })
                  }
                } else {
                  reading.fields.forEach(field => {
                    field.module = { name: field.name }
                    fields.push(field)
                  })
                }
              })
            }
          }
        }
        return fields
      }
    })
  },
  loadSpaceLatestReadingData(parentId) {
    return get('/reading/getspacespecificlatestdata?parentId=' + parentId).then(
      response => {
        let data = {}
        if (response) {
          data.readingData = response.readingAndValue.readingData
          data.readings = response.readingAndValue.readings
          if (data.readingData) {
            for (let name in data.readingData) {
              let field = data.readingData[name][0]
              if (!isNaN(field.readings[name])) {
                field.readings[name] =
                  Math.round(field.readings[name] * 100) / 100
              }
            }
          }
        }
        return data
      }
    )
  },
  loadBaseSpaceDetails(spaceId) {
    return v2get('/basespaces/' + spaceId)
      .then(result => result.basespace || {})
      .catch(_ => [])
  },
  loadLatestReading(
    id,
    excludeEmptyFields,
    fetchInputValues,
    readingType = null,
    fieldId = null
  ) {
    if (excludeEmptyFields === undefined) {
      excludeEmptyFields = true
    }
    let params = { excludeEmptyFields, fetchInputValues }
    if (readingType) {
      params.readingType = readingType
    }
    if (fieldId) {
      params.fieldId = fieldId
    }
    return post('/v2/reading/latestdata/' + id, params).then(readings => {
      let fields = []
      if (readings.result) {
        if (readings.result.readingValues) {
          readings.result.readingValues.forEach(d => {
            if (!isNaN(parseInt(d.value))) {
              d.value = Math.round(d.value * 100) / 100
            }
          })
          return readings.result.readingValues
        }
      }
      return fields
    })
  },
  executeWorkflow(workflow) {
    let url = null
    if (Vue.prototype.$account.portalInfo) {
      url = '/service/workflow/execute'
    } else {
      url = '/executeworkflow'
    }
    return v2post(url, { workflow: workflow }).then(result => {
      return result.workflowResult
    })
  },
  constructWorkflow(parentId, fielName, moduleName) {
    let workflow = {}
    workflow.expressions = [{}]
    let conditions = {}
    conditions.fieldName = 'parentId'
    conditions.operatorId = '9'
    conditions.sequence = '1'
    conditions.value = parentId
    workflow.expressions[0].aggregateString = 'lastValue'
    workflow.expressions[0].criteria = {}
    workflow.expressions[0].criteria.pattern = '(1)'
    workflow.expressions[0].criteria.conditions = {}
    workflow.expressions[0].criteria.conditions[1] = conditions
    workflow.expressions[0].name = 'Value'
    workflow.expressions[0].sortBy = 'desc'
    workflow.expressions[0].fieldName = fielName
    workflow.expressions[0].moduleName = moduleName
    workflow.expressions[0].orderByFieldName = 'ttime'
    return workflow
  },
  getdefaultWorkFlowResult(defaultIds, parentId) {
    return this.getWorkFlowResult(defaultIds, parentId ? [parentId] : null)
  },
  getWorkFlowResult(defaultIds, paramsList) {
    let url = '/workflow/getDefaultWorkflowResult'
    let params = {
      defaultWorkflowId: defaultIds,
    }
    if (paramsList) {
      params.paramList = paramsList
    }
    return v2post(url, params).then(result => {
      return result.workflow.returnValue
    })
  },
  loadFields(module, isCustomOnly) {
    return get('/module/fields?moduleName=' + module).then(function(response) {
      if (isCustomOnly) {
        return response.fields.filter(field => !field.default)
      }
      return response.fields
    })
  },

  setReadingValue(
    assetId,
    fieldId,
    value,
    assetName,
    fieldName,
    controlGroupId
  ) {
    let params = {}
    if (controlGroupId) {
      params.controlGroupId = controlGroupId
      params.value = value
    } else {
      params.resourceId = assetId
      params.fieldId = fieldId
      params.value = value
    }
    return v2post(
      '/controlAction/setReadingValue',
      params,
      'Reading will be set in a while'
    ).then(response => {
      if (response) {
        let data = response.data
        data.additionalInfo = {
          assetName,
          fieldName,
          assetId,
          fieldId,
          value,
        }
        store.dispatch('publishdata/listen', data)
      }
    })
  },
  loadCategoryAssetsForGraphics(graphics) {
    let param
    if (graphics.applyTo) {
      let applyTo = JSON.parse(graphics.applyTo)
      if (applyTo.applyToType === 2 && applyTo.applyToAssetIds.length > 0) {
        param = {
          filters: {
            id: { operatorId: 36, value: applyTo.applyToAssetIds.map(String) },
          },
        }
      } else if (applyTo.applyToType === 3 && applyTo.criteria) {
        param = { filterCriterias: applyTo.criteria }
      } else {
        if (graphics.assetCategoryId) {
          param = { categoryId: graphics.assetCategoryId }
        }
      }
    } else {
      if (graphics.assetCategoryId) {
        param = { categoryId: graphics.assetCategoryId }
      }
    }
    if (!param || !Object.keys(param).length) {
      return Promise.resolve([])
    }
    return this.loadAsset(param).then(response => response.assets)
  },

  loadAssetReadingCustomNDefaultFields(categoryId) {
    let defaultFields = []
    let customFields = []
    let url = '/reading/getassetreadings?parentCategoryId=' + categoryId
    url += '&excludeEmptyFields=' + false
    return get(url).then(readings => {
      if (readings) {
        readings.forEach(reading => {
          if (reading.fields) {
            reading.fields.forEach(field => {
              if (field.default) {
                defaultFields.push(field)
              } else {
                customFields.push(field)
              }
            })
          }
        })
      }
      return [defaultFields, customFields]
    })
  },
  downloadPdf(url, additionalInfo) {
    Message({
      message: 'Downloading...',
      showClose: true,
      duration: 0,
    })
    API.post(`/v2/integ/pdf/create`, {
      url,
      additionalInfo,
    }).then(({ data, error }) => {
      Message.closeAll()
      if (error) {
        let { message = 'Unable to fetch download link' } = error
        Message.error(message)
      } else {
        return (data || {}).fileUrl
      }
    })
  },
  /** ****** Rules apis Start *********/

  loadRules(module, ruleType) {
    return v2get(
      '/setup/' + module + '/rules?' + (ruleType ? 'ruleType=' + ruleType : '')
    )
      .then(result => result.rules || [])
      .catch(_ => [])
  },

  fetchRule(module, id) {
    return v2get('/setup/' + module + '/rules/' + id).then(
      result => result.rule
    )
  },
  fetchRules(module, id) {
    return v2get('/' + module + '/rules/fetchRule?ruleId=' + id).then(
      result => result
    )
  },

  addOrUpdateSlaOrAssignmentRule(module, rule, isEdit, action) {
    return v2post(
      '/setup/' + module + '/rules/' + (action || (isEdit ? 'update' : 'add')),
      rule,
      'Rule ' + (isEdit ? 'updated' : 'added') + ' succesfully'
    ).then(result => result.rule)
  },
  addOrUpdateRule(module, rule, isEdit, action) {
    rule.rule['actions'] = rule.actions || []
    // rule.rule['activityType'] = rule.rule.event.activityType
    let data = {
      workflowRule: rule.rule,
      moduleName: module,
    }
    return v2post(
      '/modules/rules/' + (isEdit ? 'update' : 'add'),
      data,
      'Rule ' + (isEdit ? 'updated' : 'added') + ' succesfully'
    ).then(result => {
      return result.workflowRule
    })
  },
  addOrUpdateRuleAction(module, rule, isEdit) {
    rule.rule['actions'] = rule.actions || []
    // rule.rule['activityType'] = rule.rule.event.activityType
    let data = {
      workflowRule: rule.rule,
      moduleName: module,
    }
    return v2post(
      '/alarm/rules/' + (isEdit ? 'updateRuleActions' : 'addRuleActions'),
      data,
      'Rule Action added or updated succesfully'
    ).then(result => {
      return result.workflowRule
    })
  },

  deleteRule(module, id, rule) {
    return v2post(
      '/setup/' + module + '/rules/delete',
      { id: [id] },
      (rule || 'Rule') + ' deleted successfully'
    )
  },

  changeRuleStatus(module, id, status) {
    return v2post('/setup/' + module + '/rules/status', {
      status: status,
      ruleId: id,
    })
  },

  newAddRule(module, rule, isEdit) {
    return v2post(
      '/' + module + '/rules/' + (isEdit ? 'updateNew' : 'addNew'),
      rule,
      'Rule ' + (isEdit ? 'updated' : 'added') + ' succesfully',
      true
    ).then(result => result.rule)
  },
  /** ******* Rules apis End *********/
  unixTimeToDaysHours(unixTime) {
    let duration = unixTime
    let ret = { days: 0, hours: 0 }
    if (duration && duration !== -1) {
      let factor = 3600
      let days = Math.floor(duration / (24 * factor))
      duration -= days * 24 * factor
      let hours = Math.floor(duration / factor)
      ret.days = days
      ret.hours = hours
    }
    return ret
  },
  unixTimeToDaysHoursPoint5(unixTime) {
    let duration = unixTime
    let ret = { days: 0, hours: 0 }
    if (duration && duration !== -1) {
      let factor = 3600
      let days = Math.floor(duration / (24 * factor))
      duration -= days * 24 * factor
      let hours = duration / factor
      ret.days = days
      ret.hours = hours
    }
    return ret
  },
  daysHoursToUnixTime({ days, hours }) {
    let factor = 3600
    let duration = days * 24 * factor
    duration += hours * factor
    return duration
  },
}

/** ************* Get And Post Methods  ************************/

let getUrl = (url, filter, searchText, orderBy, paging) => {
  if (!url.includes('?')) {
    url += '?'
  }
  if (filter && Object.keys(filter).length) {
    url += '&filters=' + encodeURIComponent(JSON.stringify(filter))
  }
  if (searchText) {
    url += '&search=' + searchText
  }
  if (orderBy) {
    url += '&orderBy=' + orderBy.field
    if (orderBy.type) {
      url += '&orderType=' + orderBy.type
    }
    if (orderBy.isZone) {
      url += '&isZone=' + orderBy.isZone
    }
  }
  if (paging) {
    url += '&page=' + paging.page + '&perPage=' + paging.perPage
  }
  return url
}

let get = function(url, filter, searchText, orderBy, paging) {
  url = getUrl(url, filter, searchText, orderBy, paging)
  return new Promise((resolve, reject) => {
    http
      .get(url)
      .then(response => {
        if (response.data) {
          resolve(response.data)
        } else {
          reject(Error('Internal Error'))
        }
      })
      .catch(error => {
        reject(error)
      })
  })
}

let post = function(url, params) {
  return new Promise((resolve, reject) => {
    http
      .post(url, params)
      .then(response => {
        if (response.data) {
          resolve(response.data)
        } else {
          reject(Error('Internal Error'))
        }
      })
      .catch(error => {
        reject(error)
      })
  })
}

let v2get = function(
  url,
  { filter, searchText, orderBy, paging } = {},
  showAlert
) {
  if (filter || searchText || orderBy || paging) {
    url = getUrl(url, filter, searchText, orderBy, paging)
  }
  return new Promise((resolve, reject) => {
    http
      .get('/v2' + url)
      .then(response => {
        if (response.data.responseCode === 0) {
          resolve(response.data.result)
        } else {
          if (showAlert) {
            Message.error(response.data.message)
          }
          reject(Error(response.data.message))
        }
      })
      .catch(error => {
        reject(error)
      })
  })
}

let v2post = function(url, params, successMsg, hideAlert) {
  return new Promise((resolve, reject) => {
    http
      .post('/v2' + url, params)
      .then(response => {
        if (response.data) {
          if (response.data.responseCode === 0) {
            if (successMsg) {
              Message.success(successMsg)
            }
            resolve(response.data.result)
          } else {
            if (!hideAlert) {
              Message.error(response.data.message)
            }
            reject(Error(response.data.message))
          }
        } else {
          if (!hideAlert) {
            Message.error('Error Occured')
          }
          reject(Error('Error Occured'))
        }
      })
      .catch(error => {
        if (!hideAlert) {
          Message.error('Error Occured')
        }
        reject(error)
      })
  })
}
