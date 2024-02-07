import { isEmpty, isUndefined } from '@facilio/utils/validation'
import getProperty from 'dlv'
import router from 'src/router'
import { isLookupDropDownField } from 'util/field-utils'
import cloneDeep from 'lodash/cloneDeep'
import Constants from 'util/constant'
import { fetchSearchableFields } from 'newapp/components/search/util'
import { getFieldOptions } from 'util/picklist'

const initData = ({ state, commit, rootState }, payload) => {
  return new Promise(resolve => {
    let filterObjectsData = (state.configData || {}).data
    let { moduleName, canAddCustomFields, includeAllMetaFields } = payload

    if (canAddCustomFields && filterObjectsData) {
      let fields = getProperty(payload.metaInfo, 'fields', null)
      if (isEmpty(fields)) return

      fields.forEach(field => {
        let {
          name,
          dataTypeEnum,
          trueVal,
          falseVal,
          specialType,
          enumMap,
          displayName,
          lookupModule,
        } = field
        let filterData

        let canShowNonDefaultFields =
          field.default != true && isUndefined(includeAllMetaFields)

        let isFieldInModuleFields = (
          Constants.moduleSearchFieldsMap[moduleName] || []
        ).includes(name)

        if (
          canShowNonDefaultFields ||
          includeAllMetaFields ||
          isFieldInModuleFields
        ) {
          if (
            dataTypeEnum._name == 'DATE' ||
            dataTypeEnum._name == 'DATE_TIME'
          ) {
            filterData = {
              label: '',
              displayType: 'select',
              dateRange: [],
              type: 'date',
              customdate: true,
              value: '',
              options: {
                '22': 'Today',
                '26': 'Till Yesterday',
                '31': 'This Week',
                '30': 'Last Week',
                '2_50': 'Last 2 Weeks',
                '32': 'Next Week',
                '2_61': 'Next 2 Days',
                '7_61': 'Next 7 Days',
                '28': 'This Month',
                '27': 'Last Month',
              },
            }
          } else {
            filterData = {
              label: '',
              displayType: 'string',
              value: [],
            }

            if (dataTypeEnum._name == 'STRING') {
              filterData.displayType = 'string'
              filterData.operatorId = 5
            } else if (dataTypeEnum._name == 'BOOLEAN') {
              filterData.displayType = 'select'
              filterData.type = 'Boolean'
              filterData.options = {
                true: trueVal || 'Yes',
                false: falseVal || 'No',
              }
              filterData.value = []
            } else if (dataTypeEnum._name == 'NUMBER') {
              if (field.name === 'siteId') {
                filterData.displayType = 'select'
                filterData.options = {}
              } else {
                filterData.displayType = 'number'
                filterData.operatorId = 9
              }
            } else if (dataTypeEnum._name == 'DECIMAL') {
              filterData.displayType = 'decimal'
              filterData.operatorId = 9
            } else if (dataTypeEnum._name == 'LOOKUP') {
              if (isLookupDropDownField(field)) {
                filterData.displayType = 'lookup'
              } else {
                filterData.displayType = 'select'
                filterData.options = {}
                filterData.selectedLabel = ''
              }
              filterData.isCustomFieldLookup =
                (lookupModule || {}).custom || false

              if (specialType && specialType === 'users') {
                let userOptions = {}

                rootState.users.forEach(user => {
                  userOptions[user.id] = user.name
                })

                filterData.specialType === true
                filterData.displayType = 'select'
                filterData.options = userOptions
              }
            } else if (dataTypeEnum._name == 'ENUM') {
              filterData.displayType = 'select'
              filterData.type = 'enum'
              filterData.value = []
              filterData.options = enumMap
            }
          }

          if (
            ['DATE', 'DATE_TIME', 'BOOLEAN', 'ENUM'].includes(
              dataTypeEnum._name
            )
          ) {
            filterData['key'] = name
          }

          filterData.label = displayName
          filterData.default = field.default
          filterData.operator = dataTypeEnum._name

          if (
            [
              'LOOKUP',
              'NUMBER',
              'DECIMAL',
              'ENUM',
              'BOOLEAN',
              'STRING',
              'DATE',
              'DATE_TIME',
            ].includes(dataTypeEnum._name)
          ) {
            // Set only allowed field types
            filterObjectsData[name] = filterData
          }
        }
      })

      commit('UPDATE_FILTER_CONFIG', {
        ...state.configData,
        data: filterObjectsData,
      })
      resolve()
    } else {
      resolve()
    }
  })
}

const setOptions = ({ state, rootState, commit }, moduleName) => {
  let filterObjectsData = state.configData.data
  let ticketStates = rootState.ticketStatus[moduleName]
  let filterObjects = Object.keys(filterObjectsData)

  let userOptions = {}
  rootState.users.forEach(user => {
    userOptions[user.id] = user.name
  })
  let filterOptions = [
    'assignedTo',
    'executedBy',
    'issuedTo',
    'issuedBy',
    'requestedBy',
    'registeredBy',
    'requestedFor',
    'host',
    'inviteHost',
    'acknowledgedBy',
    'clearedBy',
  ]
  let formatFilterOptions = {
    category: { data: rootState.ticketCategory, setValue: 'name' },
    readingAlarmCategory: {
      data: rootState.readingAlarmCategory,
      setValue: 'name',
    },
    assetCategoryId: { data: rootState.assetCategory, setValue: 'displayName' },
    assetCategory: { data: rootState.assetCategory, setValue: 'displayName' },
    assetDepartment: { data: rootState.assetDepartment, setValue: 'name' },
    assetType: { data: rootState.assetType, setValue: 'name' },
    inventoryCategory: { data: rootState.inventoryCategory, setValue: 'name' },
    //siteId: { data: rootState.sites, setValue: 'name' },
    status: { data: ticketStates, setValue: 'displayName' },
    moduleState: { data: ticketStates, setValue: 'displayName' },
    priority: { data: rootState.ticketPriority, setValue: 'displayName' },
    ticketType: { data: rootState.ticketType, setValue: 'name' },
  }
  let filterOptionModule = {
    tenant: 'tenant',
    client: 'client',
    itemType: 'itemTypes',
    toolType: 'toolTypes',
    storeRoom: 'storeRoom',
    fromStore: 'storeRoom',
    toStore: 'storeRoom',
    vendor: 'vendors',
    visitor: 'visitor',
    visitorType: 'visitorType',
    rule: 'readingrule',
    siteId: 'site',
    resourceIdSpace: 'resource',
    resourceIdAsset: 'resource',
  }

  filterObjects.forEach(async key => {
    if (isEmpty(filterObjectsData[key].options)) {
      if (filterOptions.includes(key)) {
        filterObjectsData[key].options = userOptions

        if (
          ['assignedTo', 'executedBy'].includes(key) &&
          !isEmpty(rootState.groups)
        ) {
          rootState.groups.forEach(group => {
            filterObjectsData[key].options[group.id + '_group'] = group.name
          })
        }
      } else if (
        ['severity', 'previousSeverity'].includes(key) &&
        !isEmpty(rootState.alarmSeverity)
      ) {
        rootState.alarmSeverity.forEach(severity => {
          filterObjectsData[key].options[severity.id] = severity.severity
        })
      } else if (Object.keys(formatFilterOptions).includes(key)) {
        let { data, setValue } = formatFilterOptions[key]

        data.forEach(listObj => {
          filterObjectsData[key]['options'][listObj.id] = listObj[setValue]
        })
      } else if (key === 'frequency') {
        filterObjectsData[key].options = cloneDeep(Constants.FACILIO_FREQUENCY)
        filterObjectsData[key].options['0'] = 'Once'
      } else if (Object.keys(filterOptionModule).includes(key)) {
        let moduleName = filterOptionModule[key]
        let field = {
          lookupModuleName: moduleName,
          skipDeserialize: true,
        }
        let { error, options } = await getFieldOptions({
          field,
          page: 1,
          perPage: 50,
        })

        if (!error) {
          filterObjectsData[key].options = options
        }
      }
    }
  })

  commit('UPDATE_FILTER_CONFIG', {
    ...state.configData,
    data: filterObjectsData,
  })
}

const constructStatusField = (
  { state, rootState, commit },
  meta,
  moduleName
) => {
  let moduleObj = getProperty(meta, 'module', null)
  let filterData = state.configData.data

  if (!isEmpty(moduleObj) && isEmpty(filterData.moduleState)) {
    let stateOptions = {}
    let ticketStates = rootState.ticketStatus[moduleName]

    if (!isEmpty(ticketStates)) {
      ticketStates.forEach(ticketState => {
        stateOptions[ticketState.id] = ticketState.displayName
      })
    }

    if (moduleObj.stateFlowEnabled) {
      let stateObj = {
        label: 'Status',
        displayType: 'select',
        options: stateOptions,
        value: [],
        key: 'moduleState',
      }

      filterData.moduleState = stateObj
      commit('UPDATE_FILTER_CONFIG', { ...state.configData, data: filterData })
    }
  }
}

const addRelatedFields = ({ state, commit }) => {
  if (state.configData.relatedFields) {
    let filterData = state.configData.data
    state.configData.relatedFields.forEach(field => {
      let { name, relatedFieldName, label } = field
      let config = {
        displayType: 'lookup',
        operatorId: 88,
        label,
        relatedFieldName,
      }
      filterData[name] = config
    })
    commit('UPDATE_FILTER_CONFIG', { ...state.configData, data: filterData })
  }
}

const state = {
  active: false,
  configData: null,
  openViewDialog: false,
  saveWithExistingViewFilter: false,
  newView: null,
  viewToEdit: null,
  searchableFields: [],
  currentModuleName: null,
}

const getters = {
  getFilterConfig(state) {
    return state.configData
  },

  getSearchActiveState(state) {
    return state.active
  },

  getOpenViewDialog(state) {
    return state.openViewDialog
  },

  getSaveExistingView(state) {
    return state.saveWithExistingViewFilter
  },

  getViewFilterSave(state) {
    return state.newView
  },

  getViewToEdit(state) {
    return state.viewToEdit
  },
}

const mutations = {
  CHANGE_ACTIVE_STATE(state, payload) {
    state.active = payload.active
  },

  UPDATE_FILTER_CONFIG(state, payload) {
    state.configData = payload
  },

  UPDATE_VIEW_DIALOG(state, payload) {
    state.openViewDialog = payload.viewDialogState
  },

  UPDATE_SAVE_EXISTING_VIEW(state, payload) {
    state.saveWithExistingViewFilter = payload.saveExistingView
  },

  UPDATE_NEW_VIEW_FILTER(state, payload) {
    state.newView = payload.newView
  },

  UPDATE_VIEW_TO_EDIT(state, payload) {
    state.viewToEdit = payload.view
  },

  UPDATE_SEARCHABLE_FIELDS(state, payload) {
    state.searchableFields = payload.searchableFields
  },
  UPDATE_CURRENT_MODULE(state, payload) {
    state.currentModuleName = payload
  },
}

const actions = {
  toggleActiveStatus({ commit }, active) {
    commit('CHANGE_ACTIVE_STATE', { active })
  },

  updateFilterConfig({ commit }, configData) {
    commit('UPDATE_FILTER_CONFIG', configData)
  },

  updateViewDialogState({ commit }, viewDialogState) {
    commit('UPDATE_VIEW_DIALOG', { viewDialogState })
  },

  updateSaveExistingView({ commit }, saveExistingView) {
    commit('UPDATE_SAVE_EXISTING_VIEW', { saveExistingView })
  },

  updateNewViewFilter({ commit }, newView) {
    commit('UPDATE_NEW_VIEW_FILTER', { newView })
  },

  updateViewToEdit({ commit }, view) {
    commit('UPDATE_VIEW_TO_EDIT', { view })
  },

  updateCurrentModuleName({ commit }, payload) {
    commit('UPDATE_CURRENT_MODULE', payload)
  },

  resetFilters() {
    let currentRoute = router.currentRoute
    let existingFilters = getProperty(currentRoute, 'query', null)
    let query = {}

    if (!isEmpty(existingFilters)) {
      query = { ...existingFilters }
      delete query['search']
      delete query['includeParentFilter']
    }

    router
      .replace({
        path: currentRoute.path,
        query: query,
      })
      .catch(() => {})
  },

  applyFilters(params, filters) {
    let currentRoute = router.currentRoute
    let existingFilters = getProperty(currentRoute, 'query', null)
    let query = existingFilters ? { ...existingFilters } : {}

    if (!isEmpty(filters)) {
      query.search = JSON.stringify(filters)
      query.includeParentFilter = true
      delete query.page
    }

    router
      .replace({
        query,
      })
      .catch(() => {})
  },

  init({ state, dispatch, rootState, rootGetters, commit }, payload) {
    return new Promise(resolve => {
      initData({ state, commit, rootState }, payload).then(() => {
        setOptions(
          { state, dispatch, rootState, rootGetters, commit },
          payload.moduleName
        )
        constructStatusField(
          { state, rootState, commit },
          payload.metaInfo,
          payload.moduleName
        )

        addRelatedFields({ state, commit })

        resolve()
      })
    })
  },

  // New search and tag implementations

  async fetchSearchableFields({ commit }, payload) {
    let { moduleName } = payload
    let response = await fetchSearchableFields(moduleName)
    if (response.error) {
      return response
    } else {
      let lookUps = ['MULTI_LOOKUP_SIMPLE', 'LOOKUP_SIMPLE', 'LOOKUP_POPUP']
      const RESOURCE_MODULES = [
        'site',
        'building',
        'floor',
        'space',
        'asset',
        'basespace',
      ]
      let { data } = response
      let { fields: searchableFields } = data || {}
      searchableFields = (searchableFields || []).map(field => {
        let { displayType, lookupModule } = field || {}
        let { name } = lookupModule || {}
        if (lookUps.includes(displayType) && RESOURCE_MODULES.includes(name)) {
          field = {
            ...field,
            additionalParams: {
              isToFetchDecommissionedResource: true,
            },
          }
        }
        return field
      })
      commit('UPDATE_SEARCHABLE_FIELDS', { searchableFields })
    }
  },
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
}
