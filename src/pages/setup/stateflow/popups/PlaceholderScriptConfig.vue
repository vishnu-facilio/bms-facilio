<template>
  <el-dialog
    :visible="true"
    width="60%"
    class="fieldchange-Dialog pB15 fc-dialog-center-container fc-dialog-center-body-p0"
    title="Placeholder Script Configuration"
    :append-to-body="true"
    :before-close="close"
  >
    <div class="height350 overflow-y-scroll pB50">
      <code-mirror :codeeditor="true" v-model="workFlowString"></code-mirror>
    </div>

    <div class="modal-dialog-footer" style="z-index: 900;">
      <el-button @click="close" class="modal-btn-cancel">CANCEL</el-button>
      <el-button type="primary" class="modal-btn-save" @click="save()"
        >Save</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import CodeMirror from '@/CodeMirror'

export default {
  props: ['scriptTemplate', 'module'],

  components: { CodeMirror },

  data() {
    return {
      workFlowString: null,
    }
  },

  created() {
    this.workFlowString = this.getScriptWorkflow()
  },

  methods: {
    getScriptWorkflow() {
      let { workflowV2String } = this.scriptTemplate || {}
      if (!isEmpty(workflowV2String)) {
        return workflowV2String
          .replace('Map scriptFunc(Map ' + this.module + ') {\n', '')
          .replace(new RegExp('\n}' + '$'), '')
      }
      return null
    },
    close() {
      this.$emit('onClose')
    },
    save() {
      let { module: moduleName, workFlowString, scriptTemplate } = this
      let { id } = scriptTemplate || {}
      let userWorkflow = null

      if (!isEmpty(workFlowString)) {
        userWorkflow = {
          isV2Script: true,
          workflowV2String:
            'Map scriptFunc(Map ' +
            moduleName +
            ') {\n' +
            workFlowString +
            '\n}',
        }
      }
      if (!isEmpty(id)) {
        userWorkflow = { ...userWorkflow, id }
      }

      this.$emit('update:scriptTemplate', userWorkflow)
      this.close()
    },
  },
}
</script>
