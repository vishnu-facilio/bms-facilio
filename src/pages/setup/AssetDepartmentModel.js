import { SetupData, prop } from '@facilio/data'
import { API } from '@facilio/api'

export class AssetDepartment extends SetupData {
  @prop({ primary: true })
  id = null

  @prop()
  name = null

  @prop({ serialize: null })
  moduleName = 'assetdepartment'

  static async deleteRecord({ id }) {
    let { error } = await API.deleteRecord('assetdepartment', id)

    if (error) {
      throw new Error(error.message || this.$t('error_occured'))
    }
  }

  static async fetchAllRecords() {
    let moduleName = 'assetdepartment'
    let { list, error } = await API.fetchAll(moduleName, {
      page: 1,
      perPage: 5000,
      withCount: true,
    })
    if (!error) {
      return { data: list || [], moduleName }
    } else {
      throw new Error(error.message || this.$t('error_occured'))
    }
  }

  async saveRecord(serilaizedData) {
    let { moduleName } = this
    let { error, [moduleName]: data } = await API.createRecord(moduleName, {
      data: serilaizedData,
    })
    if (error) {
      throw new Error(error.message || this.$t('error_occured'))
    } else {
      return data
    }
  }

  async patchUpdate(serilaizedData) {
    let { moduleName, id } = this
    let { error, [moduleName]: data } = await API.updateRecord(moduleName, {
      id,
      data: serilaizedData,
    })
    if (error) {
      throw new Error(error.message || this.$t('error_occured'))
    } else return data
  }
}
