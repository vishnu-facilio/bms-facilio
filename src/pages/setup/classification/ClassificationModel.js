import { SetupData, prop } from '@facilio/data'
import { API } from '@facilio/api'
import getProperty from 'dlv'
import { isEmpty } from '@facilio/utils/validation'

export class ClassificationListModel extends SetupData {
  @prop({ primary: true })
  id = null

  @prop()
  name = ''

  @prop()
  description = ''

  @prop()
  appliedModuleIds = []

  @prop({ serialize: null })
  hasChild = false

  @prop()
  parentClassification = null

  @prop({ serialize: null })
  classificationPath = null

  @prop()
  attributes = []

  static totalCount = null
  static moduleName = 'classification'

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
    let { classification, error } = await API.fetchRecord(
      this.moduleName,
      payload
    )
    if (error) {
      throw error
    } else {
      return { data: classification || {} }
    }
  }

  async saveRecord(serializedData) {
    let { id } = serializedData || {}
    let moduleName = 'classification'
    let response = []

    if (isEmpty(id)) {
      response = await API.createRecord(moduleName, {
        data: serializedData,
      })
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
