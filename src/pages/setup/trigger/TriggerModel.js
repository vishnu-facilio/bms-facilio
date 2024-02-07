import { SetupData, prop } from '@facilio/data'
import { API } from '@facilio/api'

export class Trigger extends SetupData {
  @prop({ primary: true })
  id = null

  @prop()
  name = null

  @prop()
  type = 2

  @prop()
  eventType = 1073741824

  @prop()
  status = true

  @prop({
    deserialize: trigger => ({
      triggerActions: trigger.triggerActions || [],
    }),
  })
  triggerActions = []

  @prop({ serialize: null })
  isDefault = false

  @prop({ serialize: null })
  moduleName = null

  static async fetchAllRecords(moduleName) {
    let { error, data } = await API.post('v3/trigger/list', {
      moduleName,
    })

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      return { data: data.triggerList || [], moduleName }
    }
  }

  static async deleteRecord({ id }) {
    let { error } = await API.post('v3/trigger/delete', { id })

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    }
  }

  async saveRecord(serilaizedData) {
    let { moduleName } = this
    let url = 'v3/trigger/addOrUpdate'
    let params = { moduleName, trigger: serilaizedData }
    let { error, data } = await API.post(url, params)

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      return data.triggerContext
    }
  }

  async patchUpdate() {
    let { id, status } = this
    let url = 'v3/trigger/changeStatus'
    let params = { status, id }
    let { error } = await API.post(url, params)

    if (error) {
      throw new Error(error.message || 'Failed to change trigger status')
    }
  }

  async saveActionsOrder(triggerActionsList) {
    this.triggerActions = triggerActionsList

    let { moduleName } = this
    let { id, triggerActions } = this.serialize(['triggerActions', 'id']) //return only keys passed as attribute in array
    let params = {
      moduleName,
      id,
      actionList: triggerActions.map(action => ({ id: action.id })),
    }
    let { error } = await API.post('v3/trigger/rearrange', params)

    if (error) {
      throw new Error(error.message || 'Failed to rearrange actions')
    } else {
      this.triggerActions = triggerActionsList.map((triggerAction, idx) => ({
        ...triggerAction,
        executionOrder: idx + 1,
      }))
    }
    return this
  }
}
