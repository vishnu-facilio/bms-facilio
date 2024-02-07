import { SetupData, prop } from '@facilio/data'
import { API } from '@facilio/api'
import { isEmpty } from 'util/validation'

const nodeTypes = {
  MODULE: 2,
  SUB_MODULE: 1,
}

export class ScoreDependency extends SetupData {
  @prop()
  name = null
  @prop()
  type = 2
  @prop()
  weightage = null
  @prop()
  nodeType = nodeTypes.MODULE
  @prop()
  scoreRuleId = null

  @prop({
    serialize: (value, instance) =>
      instance.nodeType === nodeTypes.SUB_MODULE ? { fieldId: value } : {},
  })
  fieldId = null

  @prop({
    serialize: (value, instance) =>
      instance.nodeType === nodeTypes.SUB_MODULE
        ? { fieldModuleId: value }
        : {},
  })
  fieldModuleId = null

  @prop({
    serialize: (value, instance) =>
      instance.nodeType === nodeTypes.SUB_MODULE
        ? { shouldBePropagated: value }
        : {},
  })
  shouldBePropagated = false

  @prop({
    serialize: (value, instance) =>
      instance.nodeType === nodeTypes.SUB_MODULE
        ? { criteria: instance.serializeCriteria(value) }
        : {},
  })
  criteria = null

  static async loadSubModuleFieldOptions(moduleName, extendedModuleIds) {
    let { error, data } = await API.get('/module/meta', { moduleName })

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      let { fields } = data.meta || {}
      let lookupFields = fields.filter(f => f.dataType === 7) //lookup type
      let currentModuleLookups = lookupFields.filter(field =>
        extendedModuleIds.includes(field.lookupModuleId)
      )
      let firstLookup = !isEmpty(currentModuleLookups)
        ? currentModuleLookups[0].id
        : null

      this.fieldId = firstLookup
      return currentModuleLookups
    }
  }

  static async getSubModulesList(moduleName) {
    let { error, data } = await API.get('/v2/forms/subFormModules', {
      moduleName,
    })

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      return data.modules || []
    }
  }

  serializeCriteria(criteria) {
    for (let condition of Object.keys(criteria.conditions)) {
      let hasValidFieldName =
        Object.prototype.hasOwnProperty.call(
          criteria.conditions[condition],
          'fieldName'
        ) && !isEmpty(criteria.conditions[condition].fieldName)
      if (!hasValidFieldName) {
        delete criteria.conditions[condition]
      } else {
        let discardKeys = [
          'valueArray',
          'operatorsDataType',
          'operatorLabel',
          'operator',
        ]

        discardKeys.forEach(key => {
          delete criteria.conditions[condition][key]
        })
      }
    }
    if (criteria && criteria.conditions) {
      if (Object.keys(criteria.conditions).length === 0) {
        criteria = null
      }
    }
    return criteria
  }
}
