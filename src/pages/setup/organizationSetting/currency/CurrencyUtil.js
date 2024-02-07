import { isEmpty } from '@facilio/utils/validation'
import store from 'src/store/index.js'
import { API } from '@facilio/api'

const metaFieldMap = {}

export const getCurrencyInDecimalValue = (value, currentCurrency) => {
  let { decimalPlaces } = currentCurrency || {}
  if (decimalPlaces === 0 || value % 1 === 0) return parseInt(value)
  else if (decimalPlaces > 0 && value % 1) {
    let decimalCount = value.toString().split('.')[1].length
    return decimalCount > decimalPlaces
      ? parseFloat(value).toFixed(decimalPlaces)
      : value
  }
  return value
}

export const getCalculatedCurrencyValue = (rateObj, value) => {
  let {
    exchangeRate,
    oldExchangeRate,
    isBaseCurrencyCode,
    isPrevBaseCurrencyCode,
  } = rateObj || {}
  let isValid = !isEmpty(oldExchangeRate)

  if (isEmpty(value)) return value

  if (isBaseCurrencyCode) {
    value = isValid ? value / oldExchangeRate : value
  } else if (isPrevBaseCurrencyCode) {
    value = exchangeRate * value
  } else if (isValid) {
    value = value / oldExchangeRate
    value = exchangeRate * value
  }
  return value
}

export const getCurrencyForCurrencyCode = currencyCode => {
  let currencies = store.state.activeCurrencies

  let currency = (currencies || []).find(
    currency => currencyCode === currency.currencyCode
  )
  return currency?.displaySymbol
}

export const getMetaFieldMapForModules = async moduleName => {
  if (!isEmpty(metaFieldMap[moduleName])) {
    return metaFieldMap[moduleName]
  }

  let { error, data } = await API.get(`/module/metafields`, { moduleName })
  if (!error) {
    let { meta } = data || {}
    let { fields } = meta || {}
    let fieldMap = (fields || []).reduce((fieldMap, fld) => {
      let { displayType, name } = fld || {}
      let { _name } = displayType || {}
      fieldMap[name] = _name
      return fieldMap
    }, {})

    metaFieldMap[moduleName] = fieldMap
    return fieldMap
  }
  return {}
}

export const checkForMultiCurrency = (fieldName, metaFieldTypeMap) => {
  return metaFieldTypeMap?.[fieldName] === 'MULTI_CURRENCY'
}
