import { isEmpty, areValuesEmpty, isNumber } from '@facilio/utils/validation'
const requiredFieldValidator = props => {
  let { rowsData, cellValue, skipRowValidation = false, cellType } = props || {}
  if (!skipRowValidation && areValuesEmpty(rowsData) && isEmpty(cellValue)) {
    return true
  } else if (['lookup', 'select'].includes(cellType)) {
    return !isEmpty(cellValue) && isNumber(cellValue)
  } else {
    return !isEmpty(cellValue)
  }
}

const selectLookupFieldValidator = props => {
  let { cellValue } = props || {}
  return isEmpty(cellValue) || isNumber(cellValue)
}

export { requiredFieldValidator, selectLookupFieldValidator }
