import { isEmpty } from 'lodash'

class Factory {
  _recurse(factory, override) {
    if (typeof override !== 'object') {
      return override
    }
    if (typeof override === 'object') {
      for (const key of Object.keys(override)) {
        if (factory.hasOwnProperty(key)) {
          factory[key] = this._recurse(factory[key], override[key])
        }
      }
    }
    return factory
  }

  create(factory, overrides = {}) {
    if (isEmpty(overrides)) return factory
    const obj = this._recurse(factory, overrides)
    return obj
  }
}

export default Object.freeze(new Factory())
