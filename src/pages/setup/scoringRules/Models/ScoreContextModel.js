import { SetupData, prop } from '@facilio/data'
import { isEmpty } from 'util/validation'
import { ScoreConditionManager } from './ConditionManagerModel'
import { ScoreDependency } from './DependencyModel'

const scoreTypes = {
  CONDITION: 1,
  DEPENDENCY: 2,
}

export class ScoreContext extends SetupData {
  @prop()
  name = null

  @prop({
    deserialize: scoreContext => ({
      baseScoringContexts: scoreContext.baseScoringContexts.map(baseContext => {
        let { type } = baseContext

        if (type === scoreTypes.CONDITION)
          return new ScoreConditionManager(baseContext)
        else return new ScoreDependency(baseContext)
      }),
    }),
    serialize: baseScoringContexts => ({
      baseScoringContexts: baseScoringContexts.map(baseContext => {
        return baseContext.serialize()
      }),
    }),
  })
  baseScoringContexts = []

  @prop()
  order = 1

  @prop({
    deserialize: context => ({
      namedCriteriaId: context.namedCriteriaId,
      iscriteriaEnable: !isEmpty(context.namedCriteriaId),
    }),
    serialize: value => ({
      namedCriteriaId: !isEmpty(value) ? value : -1,
    }),
  })
  namedCriteriaId = null

  getGroupTotalWeightage() {
    let totalWeightage = (this.baseScoringContexts || []).reduce(
      (total, scoreContext) => {
        let { weightage = 0 } = scoreContext || {}
        total += parseInt(weightage)
        return total
      },
      0
    )

    return totalWeightage
  }

  saveDependency(dependency, dependencyIdx) {
    if (isEmpty(dependencyIdx)) {
      this.baseScoringContexts.push(dependency)
    } else {
      this.baseScoringContexts.splice(dependencyIdx, 1, dependency)
    }
  }

  removeDependency(dependencyIdx) {
    this.baseScoringContexts.splice(dependencyIdx, 1)
  }

  saveCondition(condition, criteriaIdx) {
    if (isEmpty(criteriaIdx)) {
      this.baseScoringContexts.push(condition)
    } else {
      this.baseScoringContexts.splice(criteriaIdx, 1, condition)
    }
  }

  removeCondition(criteriaIdx) {
    this.baseScoringContexts.splice(criteriaIdx, 1)
  }
}
