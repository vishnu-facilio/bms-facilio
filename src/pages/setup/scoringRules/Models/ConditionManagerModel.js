import { SetupData, prop } from '@facilio/data'

export class ScoreConditionManager extends SetupData {
  @prop({ primary: true })
  id = null
  @prop()
  namedCriteriaId = null
  @prop()
  namedCriteria = {}
  @prop()
  type = 1
  @prop()
  weightage = null
}
