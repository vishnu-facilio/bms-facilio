import { SetupData, prop } from '@facilio/data'
import $getProperty from 'dlv'
import { isEmpty, isNullOrUndefined } from '@facilio/utils/validation'

export class ScriptModel extends SetupData {
  @prop()
  actionType = 21

  @prop({ serialize: null })
  moduleName = null

  @prop({
    deserialize: actionObj => {
      let { moduleName } = actionObj || {}
      let serverWorkflowString =
        $getProperty(
          actionObj,
          'template.originalTemplate.workflowContext.workflowV2String',
          ''
        ) || ''
      let clientWorkflowString =
        $getProperty(
          actionObj,
          'templateJson.resultWorkflowContext.workflowV2String',
          ''
        ) || ''
      let workflowString = serverWorkflowString || clientWorkflowString || ''

      let serverIsV2Script = $getProperty(
        actionObj,
        'template.originalTemplate.workflowContext.isV2Script',
        null
      )
      let clientIsV2Script = $getProperty(
        actionObj,
        'templateJson.resultWorkflowContext.isV2Script',
        null
      )
      let isV2Script = !isNullOrUndefined(serverIsV2Script)
        ? serverIsV2Script
        : !isNullOrUndefined(clientIsV2Script)
        ? clientIsV2Script
        : true

      let workflowV2String = !isEmpty(moduleName)
        ? workflowString
            .replace('void scriptFunc(Map ' + moduleName + ') {\n', '')
            .replace(new RegExp('\n}' + '$'), '')
        : workflowString.replace('void test()' + ' { ', '').slice(0, -2)

      let workflowContextId = $getProperty(
        actionObj,
        'templateJson.resultWorkflowContext.id',
        null
      )
      let workflowContext = { workflowV2String, isV2Script }
      if (!isEmpty(workflowContextId)) {
        workflowContext = { ...workflowContext, id: workflowContextId }
      }

      return { workflowContext }
    },
    validate: workflowContext => {
      let { workflowV2String } = workflowContext || {}
      return !isEmpty(workflowV2String)
    },
    serialize: (workflowContext, instance) => {
      let { workflowV2String, isV2Script, id } = workflowContext || {}
      let workflowString = !isEmpty(instance.moduleName)
        ? 'void scriptFunc(Map ' +
          instance.moduleName +
          ') {\n' +
          workflowV2String +
          '\n}'
        : 'void test()' + ' { ' + workflowV2String + ' }'
      let resultWorkflowContext = {
        workflowV2String: workflowString,
        isV2Script: isV2Script || true,
      }
      if (!isEmpty(id)) {
        resultWorkflowContext = { ...resultWorkflowContext, id }
      }
      let templateJson = { resultWorkflowContext }

      return { templateJson }
    },
  })
  workflowContext = { workflowV2String: '', isV2Script: true }
}
