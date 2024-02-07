import { formatDate } from './filters'
import { isEmpty } from '@facilio/utils/validation'
import Constants from './constant'
import moment from 'moment-timezone'
import Vue from 'vue'

export const isBooleanField = field => {
  return (
    field.dataTypeEnum === 'BOOLEAN' ||
    (field.dataTypeEnum || {})._name === 'BOOLEAN'
  )
}

export const isNumberField = field => {
  return (
    field.dataTypeEnum === 'NUMBER' ||
    (field.dataTypeEnum || {})._name === 'NUMBER'
  )
}

export const isDecimalField = field => {
  return (
    field.dataTypeEnum === 'DECIMAL' ||
    (field.dataTypeEnum || {})._name === 'DECIMAL'
  )
}

export const isEnumField = field => {
  return (
    field.dataTypeEnum === 'ENUM' || (field.dataTypeEnum || {})._name === 'ENUM'
  )
}
export const isSystemEnumField = field => {
  return (
    field.dataTypeEnum === 'SYSTEM_ENUM' ||
    (field.dataTypeEnum || {})._name === 'SYSTEM_ENUM'
  )
}

export const isSystemStringEnum = field => {
  return (
    field.dataTypeEnum === 'STRING_SYSTEM_ENUM' ||
    (field.dataTypeEnum || {})._name === 'STRING_SYSTEM_ENUM'
  )
}

export const isMultiEnumField = field => {
  return (
    field.dataTypeEnum === 'MULTI_ENUM' ||
    (field.dataTypeEnum || {})._name === 'MULTI_ENUM'
  )
}

export const isDateField = field => {
  return (
    field.dataTypeEnum === 'DATE' || (field.dataTypeEnum || {})._name === 'DATE'
  )
}

export const isDateTimeField = field => {
  return (
    field.dataTypeEnum === 'DATE_TIME' ||
    (field.dataTypeEnum || {})._name === 'DATE_TIME'
  )
}

export const isDateTypeField = field => {
  return isDateTimeField(field) || isDateField(field)
}

export const isLookupField = field => {
  return (
    field.dataTypeEnum === 'LOOKUP' ||
    (field.dataTypeEnum || {})._name === 'LOOKUP'
  )
}

export const isMultiLookup = fieldObj => {
  let { displayTypeEnum } = fieldObj || {}
  return !isEmpty(displayTypeEnum) && displayTypeEnum === 'MULTI_LOOKUP_SIMPLE'
}

export const isMultiLookupField = fieldObj => {
  let { dataTypeEnum } = fieldObj || {}
  return (
    dataTypeEnum === 'MULTI_LOOKUP' ||
    (dataTypeEnum || {})._name === 'MULTI_LOOKUP'
  )
}

export const isLookupSimple = fieldObj => {
  return fieldObj.displayTypeEnum === 'LOOKUP_SIMPLE'
}
export const isLookupPopup = fieldObj => {
  return fieldObj.displayTypeEnum === 'LOOKUP_POPUP'
}

export const isSiteField = field => {
  let { name } = field || {}
  return name === 'siteId'
}

export const isSiteLookup = field => {
  let { lookupModuleName, field: fieldObj } = field || {}
  return (
    !isEmpty(fieldObj) &&
    fieldObj.default &&
    lookupModuleName === 'site' &&
    !isMultiLookup(field)
  )
}

export const isFileTypeField = fieldObj => {
  return !isEmpty(fieldObj) && fieldObj.displayType === 'FILE'
}
export const isTimeField = fieldObj => {
  return !isEmpty(fieldObj) && fieldObj.displayType === 'TIME'
}

export const getDisplayValue = (field, value) => {
  if (isDecimalField(field)) {
    let unit = field.unit ? field.unit : ''
    return Number(value).toFixed(2) + ' ' + unit
  } else if (isDateTimeField(field)) {
    return value > 0 ? formatDate(value) : '---'
  } else if (isDateField(field)) {
    return value > 0 ? formatDate(value, true) : '---'
  } else if (isBooleanField(field)) {
    return value ? field.trueVal || 'True' : field.falseVal || 'False'
  } else if (isSpecialEnumField(field)) {
    return Constants.specialEnumFieldsMap[field.name][parseInt(value)] || '---'
  } else if (isEnumField(field)) {
    return field.enumMap[parseInt(value)] || '---'
  } else if (isSystemEnumField(field)) {
    return field.enumMap[parseInt(value)] || '---'
  } else if (isSystemStringEnum(field)) {
    return field.enumMap[value] || '---'
  } else if (isMultiEnumField(field)) {
    let { enumMap } = field

    let valueStr = (value || []).reduce((accStr, value) => {
      let str = enumMap[value] || ''
      return isEmpty(accStr) ? `${str}` : `${accStr}, ${str}`
    }, '')

    return isEmpty(valueStr) ? '---' : valueStr
  } else if (isGeoLocationField(field)) {
    return !isEmpty(value)
      ? `${parseFloat(value.lat).toFixed(6)},${parseFloat(value.lng).toFixed(
          6
        )}`
      : ''
  } else if (isTimeField(field)) {
    return formatTimeField(value) || '---'
  } else {
    return value
  }
}
export const formatTimeField = value => {
  let timeFormatHash = {
    '24Hour': 1,
    '12Hour': 2,
  }
  let { timeFormat } = Vue.prototype.$org
  let format = timeFormat === timeFormatHash['12Hour'] ? 'h:mm A' : 'HH:mm'
  return moment()
    .startOf('day')
    .milliseconds(value)
    .format(format)
}

export const isLookupDropDownField = field => {
  let { displayTypeEnum, field: fieldObj, dataTypeEnum } = field
  if (
    (!isEmpty(fieldObj) || !isEmpty(field)) &&
    (displayTypeEnum === 'LOOKUP_SIMPLE' ||
      (dataTypeEnum && dataTypeEnum._name === 'LOOKUP'))
  ) {
    let { lookupModule } = fieldObj || field
    if (!isEmpty(lookupModule)) {
      let { type } = lookupModule
      return Constants.FIELD_LOOKUP_ENTITY_HASH.includes(type)
    }
  }
  return false
}

export const isChooserTypeField = field => {
  let { displayTypeEnum } = field
  return Constants.FIELD_TYPE_CHOOSERHASH.includes(displayTypeEnum)
}

export const isDropdownTypeField = field => {
  let { displayTypeEnum } = field
  return Constants.FIELD_TYPE_DROPDOWNHASH.includes(displayTypeEnum)
}

export const isSpecialEnumField = field => {
  let { name, module = {} } = field
  let { name: moduleName } = module
  return (Constants.SPECIAL_ENUM_FIELDS_HASH[moduleName] || []).includes(name)
}

export const isLocationField = field => {
  let { name } = field || {}
  const locationFieldsList = ['location'] // Should remove this name check
  return locationFieldsList.includes(name)
}
export const isGeoLocationField = field => {
  let { displayTypeEnum = '', displayType = '' } = field || {}
  let isStringLocationField = field?.field?.dataTypeEnum === 'STRING'
  return (
    [displayTypeEnum, displayType].includes('GEO_LOCATION') &&
    !isStringLocationField
  )
}

export const isIdField = field => {
  return field.dataTypeEnum == 'ID'
}
