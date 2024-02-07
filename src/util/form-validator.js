import { isEmpty, isNumber } from '@facilio/utils/validation'

export const validateForDropDownField = (rule = {}, value, callback) => {
  if (isEmpty((value || {}).id)) {
    callback(new Error(`Invalid`))
  } else {
    callback()
  }
}

export const validateForPositiveNumber = (rule = {}, value, callback) => {
  let val = Number(value)
  if (!isNumber(val) || val < 0) {
    callback(new Error(`Invalid`))
  } else {
    callback()
  }
}
export const validateForNumber = (rule = {}, value, callback) => {
  let val = Number(value)
  if (!isEmpty(value) && !isNumber(val)) {
    callback(new Error(`Invalid`))
  } else {
    callback()
  }
}
