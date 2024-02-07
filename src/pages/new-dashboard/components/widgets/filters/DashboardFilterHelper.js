import { isEmpty } from '@facilio/utils/validation'
import { serializeProps } from '@facilio/utils/utility-methods'
import getProperty from 'dlv'

let CONSTANTS = {
  componentTypes: {
    LOOKUP: {
      SINGLE_SELECT: 1,
      MULTI_SELECT: 2,
    },
  },

  optionType: {
    ALL: 1,
    SOME: 2,
  },

  PICKLIST_OPERATOR: {
    IS: 36,
    ISN_T: 37,
  },

  BUILDING_OPERATOR: {
    BUILDING_IS: 38,
  },
  DATETIME_OPERATOR: {
    HOUR_OF_DAY: 103,
    DAY_OF_MONTH: 101,
    DAY_OF_WEEK: 85,
  },
}

function serializeUserFilters(userFilterObjs) {
  return userFilterObjs.map(userFilterObj => {
    let userFilter = serializeProps(userFilterObj, [
      'isOthersOptionEnabled',
      'isAllOptionEnabled',
      'showOnlyRelevantValues',
      'id',
      'parentModuleName',
      'filterOrder',
      'optionType',
      'componentType',
      'label',
      'fieldId',
      'moduleName',
      'defaultValues',
      'selectedOptions',
      'criteria',
      'criteriaId',
      'selectedSliderRangeValues',
      'selectedDayOrHourValues',
      'filterDisplayType',
    ])

    let { moduleName, module } = userFilterObj || {}
    let { parentModule } = module || {}
    if (moduleName === 'ticketstatus') {
      userFilter.parentModuleName = parentModule
    }
    let criteria = userFilter.criteria
    if (criteria && criteria.conditions) {
      //dont send operator as there is no enum setter for operator,causes struts error,operatorId is sent which will suffice

      for (const conditionId in criteria.conditions) {
        delete criteria.conditions[conditionId].operator
      }
    }
    return userFilter
  })
}
function getUserFilterObj(filterOption) {
  // filterOption is either-> Field/Module Object indicated by filterOption.filterType

  // construct a db user filter context obj
  let filterObj = {
    optionType: 1,
    componentType: 1,
    label: filterOption.displayName,
    defaultValues: [],
    isAllOptionEnabled: true,
    isOthersOptionEnabled: true,
    showOnlyRelevantValues: false,
    filterDisplayType: 1,
  }
  if (filterOption.filterType == 'FIELD') {
    filterObj.fieldId = filterOption.fieldId
    filterObj.field = filterOption
  } else if (filterOption.filterType == 'MODULE') {
    filterObj.moduleName = filterOption.name
    filterObj.module = filterOption
  }

  return filterObj
}

function getUserFilterModel(userFilters) {
  let model = {}
  userFilters.forEach(filter => {
    model[filter.id] = filter.defaultValues.filter(rt => {
      if (rt !== 'all') {
        return rt
      }
    })
  })
  return model
}

// cascadingFilters -> map of UserFilterId:applicableFieldObj
// userFilters Array of filter meta, userFilterModel {filterId:valueArray} , object of applied dropdown values for all filters
function getCascadingFilterModel(userFilters, userFilterModel) {
  // MAP userFilterId:FilterJson(that applies to columns in that Filter)
  let cascadingFilterModel = {}

  for (const userFilter of userFilters) {
    if (userFilter.showOnlyRelevantValues && userFilter.cascadingFilters) {
      let filterJSON = getCascadingFilterJsonForFilter(
        userFilter.cascadingFilters,
        userFilterModel
      )
      if (filterJSON) {
        cascadingFilterModel[userFilter.id] = filterJSON
      }
    }
  }

  return cascadingFilterModel
}

function getCascadingFilterJsonForFilter(cascadingFilters, userFilterModel) {
  let filterJsonForUserFilter = {}

  for (const userFilterId in cascadingFilters) {
    //ALL handling etc being done here , to do ,user the generated widget->filterModel for this
    if (
      userFilterModel[userFilterId] &&
      !userFilterModel[userFilterId].includes('all') &&
      !userFilterModel[userFilterId].includes('others')
    ) {
      // if that filter has a criteria , ie its dropdown set to something oTHER THAN all etc
      let condition = getSimpleCondition(
        cascadingFilters[userFilterId].name,
        userFilterModel[userFilterId],
        cascadingFilters[userFilterId].module.name
      )

      filterJsonForUserFilter = { ...filterJsonForUserFilter, ...condition }
    }
  }

  if (!isEmpty(filterJsonForUserFilter)) {
    return filterJsonForUserFilter
  }

  return null
}

function getSimpleCondition(fieldName, values, fieldModuleName) {
  return {
    [fieldName]: {
      operatorId: isSpaceRelatedModuleName(fieldModuleName)
        ? CONSTANTS.BUILDING_OPERATOR.BUILDING_IS
        : CONSTANTS.PICKLIST_OPERATOR.IS,
      value: values,
    },
  }
}

function generateFilterJSON(filterModel, userFilterList, widgetFilterRel) {
  let userFilterObjMap = userFilterList.reduce((obj, ele) => {
    obj[ele.id] = ele
    return obj
  }, {})
  //generate condition for each filterID{widgetID:{"category":"operatorId",WidgetID2:{"differentModCategory:..."}}
  //generate condition for each widget for each filter-> due to field mapping ,
  let filterConditions = {}

  //init map of widgetID:{filterID:{condition},filterID2Condition},widgetId2:{filterId1:c}...
  for (const widgetId in widgetFilterRel) {
    filterConditions[widgetId] = {}
  }

  for (const widgetId in widgetFilterRel) {
    //widget filter rel=> {widgetID:[applicableFilter1,applicableFilter2],widgetId2:[..]}
    //widget id key present only if atleast one applicable filter present
    for (const applicableFilterId of widgetFilterRel[widgetId]) {
      let condition = getCondition(
        applicableFilterId,
        filterModel[applicableFilterId],
        userFilterObjMap[applicableFilterId],
        widgetId
      )
      if (condition) {
        if (
          !isEmpty(condition) &&
          condition.ttime &&
          (condition?.ttime?.operatorId ===
            CONSTANTS.DATETIME_OPERATOR.HOUR_OF_DAY ||
            condition?.ttime?.operatorId ===
              CONSTANTS.DATETIME_OPERATOR.DAY_OF_WEEK ||
            condition?.ttime?.operatorId ===
              CONSTANTS.DATETIME_OPERATOR.DAY_OF_MONTH)
        ) {
          let dttimeFilterKey = condition.ttime.operatorId
          let dtTimeFilterJson = {}
          dtTimeFilterJson['ttime_' + dttimeFilterKey] = condition.ttime
          filterConditions[widgetId][applicableFilterId] = dtTimeFilterJson
        } else {
          filterConditions[widgetId][applicableFilterId] = condition
        }
      }
    }
  }

  let widgetFilterJSON = {}

  for (const widgetId in widgetFilterRel) {
    let filterJson = {}
    widgetFilterRel[widgetId].forEach(filterId => {
      filterJson = { ...filterJson, ...filterConditions[widgetId][filterId] }
    })

    //generate json only if atleast of the applicable filters needs a condition  , ie workorder-Widget -> category,priority filter  ,if all three are set to 'ALL' don't need any filter to be applied
    if (!isEmpty(filterJson)) {
      widgetFilterJSON[widgetId] = filterJson
    }
  }

  return widgetFilterJSON
}
function getCondition(filterId, values, userFilterObj, widgetId) {
  console.log(
    `generating filter for Filter: ${userFilterObj.label} =>Widget ${widgetId}. Value=${values}`
  )
  let fieldName = null
  let { field, optionType, selectedOptions } = userFilterObj

  //filter always contains column name for each widget to generate filter json
  let mappedField = getProperty(userFilterObj, `widgetFieldMap.${widgetId}`)
  fieldName = mappedField?.name ? mappedField.name : userFilterObj.moduleName
  if (optionType == 1) {
    if (
      values.includes('all') ||
      values.includes('') ||
      values.includes(null) ||
      !values.length
    ) {
      return {}
    } else if (
      mappedField?.name &&
      mappedField.dataTypeEnum === 'DATE_TIME' &&
      values &&
      values[0] != 'ALL'
    ) {
      let dateTimeValues = []
      if (!(userFilterObj?.dateTimeValues?.length > 0)) {
        if (userFilterObj?.selectedSliderRangeValues?.length > 0) {
          let start = userFilterObj.selectedSliderRangeValues[0]
          let end = userFilterObj.selectedSliderRangeValues[1]
          for (start; start <= end; start++) {
            dateTimeValues.push(start.toString())
          }
        } else if (userFilterObj?.selectedDayOrHourValues?.length > 0) {
          dateTimeValues = userFilterObj.selectedDayOrHourValues
        }
      }
      return {
        [fieldName]: {
          operatorId: Number(values[0]),
          value:
            userFilterObj?.dateTimeValues?.length > 0
              ? userFilterObj.dateTimeValues
              : dateTimeValues,
        },
      }
    } else {
      return {
        [fieldName]: {
          operatorId: isSpaceRelatedModule(userFilterObj)
            ? CONSTANTS.BUILDING_OPERATOR.BUILDING_IS
            : CONSTANTS.PICKLIST_OPERATOR.IS,
          value: values,
        },
      }
    }
  } else if (optionType == 2) {
    //in multiselect if both all and others are chosen . no need for filters Irrespective of option type
    if (values.includes('all') && values.includes('others')) {
      return null
    } else if (values.includes('') || values.includes(null) || !values.length) {
      return null
    } else if (values.includes('all')) {
      if (!isPickListLookup(field || mappedField)) {
        //for lookups like building,asset etc. Resolve the criteria to IDS and send when 'all' is chosen and LIMIT options is configured
        if (userFilterObj.criteria) {
          return {
            [fieldName]: {
              operatorId: isSpaceRelatedModule(userFilterObj)
                ? CONSTANTS.BUILDING_OPERATOR.BUILDING_IS
                : CONSTANTS.PICKLIST_OPERATOR.IS,
              value: userFilterObj.selectedOptionsRecordIds,
            },
          }
        } else {
          return null
        }
      } else {
        //enum and picklist lookup have selected options id list
        return {
          [fieldName]: {
            operatorId: CONSTANTS.PICKLIST_OPERATOR.IS,
            value: selectedOptions,
          },
        }
      }
    } else if (values.includes('others')) {
      if (!isPickListLookup(field || mappedField)) {
        if (userFilterObj.criteria) {
          return {
            [fieldName]: {
              operatorId: CONSTANTS.PICKLIST_OPERATOR.ISN_T,
              value: userFilterObj.selectedOptionsRecordIds,
            },
          }
        } else {
          return null
        }
      } else {
        return {
          [fieldName]: {
            operatorId: CONSTANTS.PICKLIST_OPERATOR.ISN_T,
            value: selectedOptions,
          },
        }
      }
    } else {
      return {
        [fieldName]: {
          operatorId: isSpaceRelatedModule(userFilterObj)
            ? CONSTANTS.BUILDING_OPERATOR.BUILDING_IS
            : CONSTANTS.PICKLIST_OPERATOR.IS,
          value: values,
        },
      }
    }
  }
}

function isPickListLookup(field) {
  return (
    field.dataTypeEnum == 'LOOKUP' && field.lookupModule.typeEnum == 'PICK_LIST'
  )
}

function isSpaceRelatedModule(userFilterObj) {
  if (
    userFilterObj.moduleName &&
    ['site', 'building', 'floor', 'space'].includes(userFilterObj.moduleName)
  ) {
    return true
  }
  return false
}
// to do both methods in one
function isSpaceRelatedModuleName(moduleName) {
  if (['site', 'building', 'floor', 'space', 'resource'].includes(moduleName)) {
    return true
  }
  return false
}

export {
  CONSTANTS,
  getCondition,
  generateFilterJSON,
  getUserFilterModel,
  getUserFilterObj,
  serializeUserFilters,
  getCascadingFilterModel,
}
