<script>
import ScriptDialog from 'src/pages/setup/scheduler/ScriptDialog'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: ScriptDialog,
  methods: {
    deserialize() {
      let { typeRefObj } = this.selectedScript || {}

      if (!isEmpty(typeRefObj)) {
        let { workflowV2String } = typeRefObj

        this.scriptData = {
          workflow: this.getScriptWorkflow(workflowV2String),
        }
      }
    },
    serialize() {
      let { scriptData } = this
      let { workflow } = scriptData
      let workflowV2String = 'void scriptFunc(Map asset) { ' + workflow + ' }'
      let serializeData = {
        typeRefObj: { isV2Script: true, workflowV2String },
      }
      return serializeData
    },
    getScriptWorkflow(workflowString) {
      if (workflowString) {
        return workflowString
          .replace('void scriptFunc(Map asset) {', '')
          .replace(new RegExp('}' + '$'), '')
          .trim()
      }

      return null
    },
  },
}
</script>
