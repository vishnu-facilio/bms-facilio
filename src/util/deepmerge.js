export default {
  getTypeOf(input) {
    if (input === null) {
      return 'null'
    } else if (typeof input === 'undefined') {
      return 'undefined'
    } else if (typeof input === 'object') {
      return Array.isArray(input) ? 'array' : 'object'
    }

    return typeof input
  },

  cloneValue(value, self) {
    if (!self) {
      self = this
    }
    // The value is an object so lets clone it.
    if (self.getTypeOf(value) === 'object') {
      return self.quickCloneObject(value)
    }

    // The value is an array so lets clone it.
    else if (self.getTypeOf(value) === 'array') {
      return self.quickCloneArray(value)
    }

    // Any other value can just be copied.
    return value
  },

  quickCloneArray(input) {
    let self = this
    return input.map(function(value) {
      return self.cloneValue(value, self)
    })
  },

  quickCloneObject(input) {
    const output = {}

    for (const key in input) {
      if (!input.hasOwnProperty(key)) {
        continue
      }

      output[key] = this.cloneValue(input[key])
    }

    return output
  },

  executeDeepMerge(target, _objects = [], _options = {}) {
    const options = {
      arrayBehaviour: _options.arrayBehaviour || 'replace', // Can be "merge" or "replace".
    }

    // Ensure we have actual objects for each.
    const objects = _objects.map(object => object || {})
    const output = target || {}

    // Enumerate the objects and their keys.
    for (let oindex = 0; oindex < objects.length; oindex++) {
      const object = objects[oindex]
      const keys = Object.keys(object)

      for (let kindex = 0; kindex < keys.length; kindex++) {
        const key = keys[kindex]
        const value = object[key]
        const type = this.getTypeOf(value)
        const existingValueType = this.getTypeOf(output[key])

        if (type === 'object') {
          if (existingValueType !== 'undefined') {
            const existingValue =
              existingValueType === 'object' ? output[key] : {}
            output[key] = this.executeDeepMerge(
              {},
              [existingValue, this.quickCloneObject(value)],
              options
            )
          } else {
            output[key] = this.quickCloneObject(value)
          }
        } else if (type === 'array') {
          if (existingValueType === 'array') {
            const newValue = this.quickCloneArray(value)
            output[key] =
              options.arrayBehaviour === 'merge'
                ? output[key].concat(newValue)
                : newValue
          } else {
            output[key] = this.quickCloneArray(value)
          }
        } else {
          output[key] = value
        }
      }
    }

    return output
  },

  objectAssignDeep(target, ...objects) {
    return this.executeDeepMerge(target, objects)
  },
}
