import moment from 'moment-timezone'
import { isEmpty, isArray, isObject } from '@facilio/utils/validation'
import transform from 'lodash/transform'
import cloneDeep from 'lodash/cloneDeep'
import getProperty from 'dlv'
import sanitize from './sanitize'

export const checkDuplicateInObject = (propName, inputArray) => {
  let isDuplicateFound = false
  let tempObj = {}

  inputArray.map(item => {
    let propertyName = item[propName].toLowerCase()
    if (propertyName in tempObj) {
      return (isDuplicateFound = true)
    } else {
      tempObj[propertyName] = item
    }
  })
  return isDuplicateFound
}

export const cloneObject = obj => {
  return Object.assign({}, obj)
}

export const deepCloneObject = obj => {
  return JSON.parse(JSON.stringify(obj))
}

export const cloneArray = arr => {
  return arr.slice(0)
}

export const makeObjectNonReactive = (object, deep = true) => {
  Object.freeze(object)
  if (deep) {
    Object.getOwnPropertyNames(object).forEach(function(prop) {
      if (
        object.hasOwnProperty(prop) &&
        object[prop] !== null &&
        (typeof object[prop] === 'object' ||
          typeof object[prop] === 'function') &&
        !Object.isFrozen(object[prop])
      ) {
        makeObjectNonReactive(object[prop], deep)
      }
    })
  }

  return object
}

export const constructFieldOptions = obj => {
  let options = []
  Object.entries(obj).forEach(([key, value]) => {
    options.push({
      label: value,
      value: Number(key),
    })
  })
  return options
}

export const prettyBytes = size => {
  let UNITS = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
  let absSize = Math.abs(size)
  let prettyBytes = ''

  if (Number.isFinite(size)) {
    if (absSize >= 1) {
      let exponent = Math.min(
        Math.floor(Math.log10(absSize) / 3),
        UNITS.length - 1
      )
      let numStr = Number((absSize / Math.pow(1000, exponent)).toPrecision(3))
      let unit = UNITS[exponent]
      prettyBytes = `${numStr} ${unit}`
    } else {
      prettyBytes = `${absSize} B`
    }
  }

  return size < 0 ? `-${prettyBytes}` : `${prettyBytes}`
}

export const removeNullValues = obj => {
  return Object.entries(obj).reduce(([key, value], res) => {
    if (value) {
      res[key] = value
    }
    return res
  }, {})
}

export const durationToSeconds = obj => {
  let durationObj = moment.duration(obj)
  let totalSec = durationObj.asSeconds()
  return totalSec
}

/*
      Constructing options for picklist from values array inside field object
      by the server is of the format,
      values: [
        {
          id: 728,
          value: 'Choice 1',
          index: 1
        },
        {
          id: 729,
          value: 'Choice 2',
          index: 2
        }
      ]
      Have to transform this into format supported by el-select
      options: [
        {
          label: 'Choice 1',
          value: 1
        },
        {
          label: 'Choice 2',
          value: 2
        },
      ]
    */
export const constructEnumFieldOptions = values => {
  let optionsArr = []
  if (!isEmpty(values)) {
    let filteredValues = values.filter(value => value.visible)
    optionsArr = filteredValues.map(value => {
      let obj = deepCloneObject(value)
      obj.label = value.value
      obj.value = value.index
      delete obj.index
      delete value.sequence
      return obj
    })
  }
  return optionsArr
}

export const constructOptionsToMap = valueArr => {
  let optionsObj = valueArr.reduce((optionsObj, option) => {
    let { label, value } = option || {}
    optionsObj[value] = label
    return optionsObj
  }, {})

  return optionsObj
}

export const deepClean = objValue => {
  /**
    Deep cleans the object based on isEmpty in @facilio/utils/validation.
    @param {Object,Array}
    @result {Object,Array} Deep cleaned
    Sample Input => {
          a: { b: null, c: 1 },
          d: null,
          e: -1,
          f: [],
          l: [{ a: null }, { b: 2 }, 3],
          h: 100,
          i: [1, 2, '', -1],
          j: [{}, -1, ''],
          k: [{ a: null, b: [[[]]] }, -1, ''],
        }
    Sample Output => {
          a: { c: 1 },
          h: 100,
          i: [1, 2],
          l: [{ b: 2 }, 3],
        }
  */
  let obj = cloneDeep(objValue)
  return transform(obj, (result, value, key) => {
    if (isArray(value) || isObject(value)) {
      value = deepClean(value)
    }
    if (isEmpty(value)) {
      return
    }
    if (isArray(result)) {
      return result.push(value)
    }
    result[key] = value
  })
}

export function prependBaseUrl(url) {
  if (process.env.NODE_ENV === 'development') {
    return process.env.VUE_APP_FACILIO_BASE_URL + url
  } else {
    return url
  }
}

export function htmlToText(html) {
  let doc = new DOMParser().parseFromString(sanitize(html), 'text/html')
  let node = doc.firstChild

  if (getProperty(node, 'innerText')) {
    return getProperty(node, 'innerText')
  } else {
    return ''
  }
}

export function isPortalDomain() {
  const domain = document.domain
  const isLocalFacilioPortal = domain.includes('localfacilportal.com')
  const { servicePortalDomain = false } = window

  return servicePortalDomain || isLocalFacilioPortal
}

export function nextChar(c) {
  //returns nexgt  ASCII character, so passing z will return '{'
  return String.fromCharCode(c.charCodeAt(0) + 1)
}
export function charFromIndex(index) {
  let firstAlaphabet = 'a'

  return String.fromCharCode(firstAlaphabet.charCodeAt() + index)
}

export function getUniqueCharAlias(existingAliases = []) {
  let charAlias = 'a'

  while (existingAliases.includes(charAlias)) {
    charAlias = nextChar(charAlias)
  }

  return charAlias
}

export const serializeProps = (obj, resourceProps, skipNullCheck = false) => {
  let _serializedFields = {}
  resourceProps.forEach(prop => {
    let value = obj[prop]
    if (skipNullCheck || (value !== undefined && value !== null)) {
      _serializedFields[prop] = obj[prop]
    }
  })
  return _serializedFields
}

export const getKeyByValue = (object, value) => {
  return Object.keys(object).find(key => object[key] === value)
}
