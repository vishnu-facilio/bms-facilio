import { SetupData, prop } from '@facilio/data'
import { getFieldOptions } from 'util/picklist'
import $getProperty from 'dlv'
import { API } from '@facilio/api'

export class ChangeStatusModel extends SetupData {
  @prop()
  actionType = 19
  @prop({
    deserialize: actionObj => {
      let new_state =
        $getProperty(actionObj, 'template.originalTemplate.new_state', null) ||
        $getProperty(actionObj, 'templateJson.new_state', null) ||
        null
      return { new_state }
    },
    serialize: new_stateData => {
      return { templateJson: { new_state: new_stateData } }
    },
  })
  new_state = null

  static async getTicketStatus(moduleId) {
    let { error, options } = await getFieldOptions({
      field: {
        lookupModuleName: 'ticketstatus',
        skipDeserialize: true,
        filters: {
          parentModuleId: { operatorId: 9, value: [`${moduleId}`] },
        },
      },
    })

    if (error) {
      throw error
    } else {
      return options || {}
    }
  }
  static async fetchMetaModuleId(moduleName) {
    let { error, data } = await API.get('/module/metafields', {
      moduleName,
    })

    if (error) {
      throw error
    } else {
      return $getProperty(data, 'meta.module.moduleId', null)
    }
  }
}
