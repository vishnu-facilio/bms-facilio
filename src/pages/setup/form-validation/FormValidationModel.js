import { SetupData, prop } from '@facilio/data'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export class FormValidation extends SetupData {
  @prop({ primary: true })
  id = null
  @prop()
  name = null
  @prop()
  errorMessage = ''
  @prop()
  namedCriteriaId = null
  @prop()
  parentId = null
  @prop({
    deserialize: validation => ({
      errorMessagePlaceHolderScript: {
        isV2Script:
          validation?.errorMessagePlaceHolderScript?.isV2Script || false,
        workflowV2String:
          validation?.errorMessagePlaceHolderScript?.workflowV2String || '',
        id: validation?.errorMessagePlaceHolderScript?.id || null,
      },
    }),
    serialize: placeHolderScript => {
      let { workflowV2String } = placeHolderScript || {}
      return isEmpty(workflowV2String)
        ? {}
        : { errorMessagePlaceHolderScript: placeHolderScript }
    },
  })
  errorMessagePlaceHolderScript = {
    isV2Script: true,
    workflowV2String: '',
  }
  @prop({
    deserialize: validation => ({
      namedCriteriaDisplayName: validation?.namedCriteria?.name || '',
    }),
    serialize: null,
  })
  namedCriteriaDisplayName = null

  static async fetchAllRecords(payload) {
    let { data, error } = await API.get(`v2/form/validationrule/list`, payload)
    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      return { data: data.formValidationRuleContexts || [] }
    }
  }
  static async fetchRecord(payload) {
    let { data, error } = await API.get(`v2/form/validationrule/get`, payload)
    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      return { data: data.formValidationRuleContext || {} }
    }
  }
  static async deleteRecord({ id }) {
    let { error } = await API.delete('/v2/form/validationrule/delete', {
      id,
    })

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    }
  }
  async saveRecord() {
    let serializedData = this.serialize()
    let { data, error } = await API.post('v2/form/validationrule/add', {
      validationRule: serializedData,
    })

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      return data.formValidationRuleResult || {}
    }
  }
  async patchUpdate() {
    let serializedData = this.serialize()
    let { data, error } = await API.put('/v2/form/validationrule/update', {
      validationRule: serializedData,
    })
    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      return data.formValidationRuleResult || {}
    }
  }
}
