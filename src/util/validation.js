/* Validation Methods */

export const isEmpty = value => {
  return (
    value === undefined ||
    value === null ||
    Number(value) === -1 ||
    (typeof value === 'object' &&
      !(value instanceof Blob) &&
      Object.keys(value).length === 0) ||
    (typeof value === 'string' && value.trim().length === 0)
  )
}

/*

{
  name:'',
  age:-1,
  host:{
    id:null
  }
}
is considered empty
*/

//TO DO , replace 'every' with 'some' operator for efficiency and Include array suuport
export const areValuesEmpty = obj => {
  return Object.values(obj).every(value => {
    if (isNullOrUndefined(value)) {
      return true
    } else if (Array.isArray(value)) {
      return value.length === 0 || value.every(e => areValuesEmpty(e))
    } else if (typeof value === 'object') {
      return areValuesEmpty(value)
    } else {
      return isEmpty(value)
    }
  })
}

// keyname use to determine object equality.Later version may allow passing a custom comparator function
export const removeDuplicatesFromArrayOfObj = (sourceArray, fieldToCompare) => {
  const uniqueArray = Array.from(
    new Set(sourceArray.map(e => e[fieldToCompare]))
  ).map(valueToCompare => {
    return sourceArray.find(e => e[fieldToCompare] === valueToCompare)
  })

  return uniqueArray
}

export const isNullOrUndefined = value => {
  return value === undefined || value === null
}

export const isNull = value => {
  return value === null
}

export const isArray = value => {
  return value && value.constructor === Array
}

export const isObject = obj => {
  return obj !== null && typeof obj === 'object' && Array.isArray(obj) === false
}

export const isFunction = value => {
  return typeof value === 'function'
}

export const isNumber = value => (typeof value === 'number' ? true : false)

export const isFileObject = object => {
  return object instanceof File || object instanceof Blob
}
export const isFloat = value => {
  return !Number.isInteger(value)
}
export const isInteger = value => {
  return Number.isInteger(value)
}
export const isUndefined = value => value === undefined

export const isBoolean = value => value === true || value === false
