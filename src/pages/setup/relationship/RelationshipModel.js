import { SetupData, prop } from '@facilio/data'
import { API } from '@facilio/api'

export class RelationShip extends SetupData {
  @prop({ primary: true })
  id = null

  @prop()
  name = null

  @prop()
  description = null

  @prop()
  relationName = null

  @prop()
  relationType = null

  @prop()
  fromModuleId = null

  @prop()
  toModuleId = null

  @prop()
  reverseRelationName = null

  @prop({
    deserialize: relation => ({
      toModuleDisplayName:
        relation?.toModule?.displayName || relation.toModuleDisplayName || '',
    }),
    serialize: null,
  })
  toModuleDisplayName = null

  @prop({
    deserialize: relation => ({
      fromModuleDisplayName:
        relation?.fromModule?.displayName ||
        relation.fromModuleDisplayName ||
        '',
    }),
    serialize: null,
  })
  fromModuleDisplayName = null

  @prop({
    deserialize: relation => ({
      relationModuleName: relation?.relationModule?.name || '',
    }),
    serialize: null,
  })
  relationModuleName = null

  @prop({ serialize: null })
  moduleName = null

  static async fetchAllRecords(params) {
    let { moduleName } = params || {}
    let { data, error } = await API.get('/v2/relation/list', params)

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      return { data: data.relationList || [], moduleName }
    }
  }

  static async fetchRecord({ id, moduleName }) {
    let { error, data } = await API.post('/v2/relation/view', {
      id,
      moduleName,
    })

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      return { data: data.relation || {}, moduleName }
    }
  }

  static async deleteRecord({ id }) {
    let { error } = await API.post('/v2/relation/delete', { id })

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    }
  }

  async saveRecord(serilaizedData) {
    let { moduleName } = this
    let url = 'v2/relation/addOrUpdate'
    let params = { moduleName, relation: serilaizedData }
    let { error, data } = await API.post(url, params)

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      return data
    }
  }
}
