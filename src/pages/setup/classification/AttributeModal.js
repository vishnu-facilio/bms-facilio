import { SetupData, prop } from '@facilio/data'
import { API } from '@facilio/api'
import getProperty from 'dlv'
import { isEmpty } from '@facilio/utils/validation'

export class AttributeListModel extends SetupData {
  @prop({ primary: true })
  id = null

  @prop()
  name = null

  @prop()
  fieldType = null

  @prop()
  metric = null

  @prop()
  unitId = null

  @prop()
  description = ''

  static moduleName = 'classificationAttribute'
  static totalCount = null

  static async fetchAllRecords(payload) {
    let { error, list, meta } = await API.fetchAll(this.moduleName, payload)

    if (error) {
      throw error
    } else {
      this.totalCount = getProperty(meta, 'pagination.totalCount', null)
      return {
        data: list || [],
      }
    }
  }
  static async fetchRecord(payload) {
    let { classificationAttribute, error } = await API.fetchRecord(
      this.moduleName,
      payload
    )
    if (error) {
      throw error
    } else {
      return { data: classificationAttribute || {} }
    }
  }

  async saveRecord(serializedData) {
    let moduleName = 'classificationAttribute'
    let { id } = serializedData || {}
    let response = []

    if (isEmpty(id)) {
      response = await API.createRecord(moduleName, { data: serializedData })
    } else {
      response = await API.updateRecord(moduleName, {
        id: id,
        data: serializedData,
      })
    }

    let { error } = response
    if (error) {
      throw error
    }
  }
}
