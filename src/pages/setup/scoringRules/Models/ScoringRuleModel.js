import { SetupData, prop, hasMany } from '@facilio/data'
import { API } from '@facilio/api'
import { ScoreContext } from './ScoreContextModel'

export class ScoringRule extends SetupData {
  @prop({
    deserialize: rule => {
      let { name, description, siteId, scoreType, scoreRange, id } = rule || {}
      return {
        scoreDetails: {
          name,
          description,
          siteId,
          scoreType,
          scoreRange,
          id,
        },
      }
    },
  })
  scoreDetails = {
    name: null,
    description: null,
    siteId: -1,
    scoreType: 1,
    scoreRange: 100,
  }

  @prop({
    deserialize: rule => ({
      triggers: (rule.triggers || []).map(trigger => {
        let { name, id, internal, eventType } = trigger
        return { name, id, internal, eventType }
      }),
    }),
    serialize: selectedTriggers => ({
      triggers: selectedTriggers
        .filter(t => !t.internal)
        .map(trigger => {
          let { id } = trigger
          return { id }
        }),
    }),
  })
  triggers = []

  @hasMany(ScoreContext, {
    deserialize: rule => ({ data: rule.scoringCommitmentContexts || [{}] }),
  })
  scoringContexts = []

  @prop()
  status = true

  @prop({ serialize: null })
  moduleName = null

  static async fetchAllRecords(moduleName) {
    let { error, data } = await API.post('v2/scoringRule/list', {
      moduleName,
    })

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      return { data: data.workflowRuleList || [], moduleName }
    }
  }

  static async fetchRecord(moduleName, id) {
    let { error, data } = await API.get('v2/scoringRule/view', {
      ruleId: id,
    })

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      return { data: data.workflowRule || {}, moduleName }
    }
  }

  static async deleteRecord(id) {
    let { error } = await API.post('v2/scoringRule/delete', {
      ruleId: id,
    })

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    }
  }

  async saveRecord(serilaizedData) {
    let { moduleName } = this
    let { scoreDetails, triggers, scoringContexts } = serilaizedData
    let params = {
      moduleName,
      scoringRule: { ...scoreDetails, triggers },
      scoringContexts,
    }
    let { error } = await API.post('v2/scoringRule/addOrUpdate', params)

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    }
  }

  async patchUpdate() {
    let {
      scoreDetails: { id },
      status,
    } = this
    let url = status ? 'setup/turnonrule' : 'setup/turnoffrule'
    let params = { workflowId: id }
    let { error, data } = await API.post(url, params)

    if (error && data.result !== 'success') {
      this.status = !this.status
      throw new Error(error.message)
    }
  }

  addGroup() {
    let currentGroupNum = this.scoringContexts.length + 1
    let name = `Group ${currentGroupNum}`

    this.scoringContexts.push(
      new ScoreContext({ name, order: currentGroupNum })
    )
  }

  removeGrp(index) {
    this.scoringContexts.splice(index, 1)
    this.scoringContexts.forEach((context, idx) => {
      context.order = idx + 1
    })
  }
}
