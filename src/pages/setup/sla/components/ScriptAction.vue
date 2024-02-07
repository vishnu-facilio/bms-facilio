<template>
  <el-dialog
    :visible="true"
    width="60%"
    class="fieldchange-Dialog pB15 fc-dialog-center-container fc-dialog-center-body-p0"
    :title="$t('agent.edit.script')"
    :append-to-body="true"
    :before-close="close"
  >
    <div class="height350 overflow-y-scroll pB50">
      <code-mirror :codeeditor="true" v-model="workflowString"></code-mirror>
    </div>
    <div class="modal-dialog-footer" style="z-index: 900;">
      <el-button @click="close" class="modal-btn-cancel">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button type="primary" class="modal-btn-save" @click="save">
        {{ $t('common._common._save') }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import CodeMirror from '@/CodeMirror'

export default {
  props: ['module', 'existingAction'],
  components: { CodeMirror },
  data() {
    return {
      workflowString: '',
    }
  },
  created() {
    if (!isEmpty(this.existingAction)) {
      this.init()
    }
  },
  methods: {
    init() {
      let { templateJson } = this.existingAction || {}
      let { resultWorkflowContext } = templateJson || {}

      let { workflowV2String } = resultWorkflowContext || {}

      this.workflowString = this.getScriptWorkflow(workflowV2String)
    },
    getScriptWorkflow(workflowV2String) {
      if (workflowV2String) {
        return workflowV2String
          .replace('void scriptFunc(Map ' + this.module + ') {\n', '')
          .replace(new RegExp('\n}' + '$'), '')
      }
      return null
    },
    close() {
      this.$emit('onClose')
    },
    save() {
      let scriptData = {
        actionType: 21,
        templateJson: {
          resultWorkflowContext: {
            workflowV2String: null,
            isV2Script: true,
          },
        },
      }
      let workflowV2String =
        'void scriptFunc(Map ' +
        this.module +
        ') {\n' +
        this.workflowString +
        '\n}'

      this.$set(
        scriptData.templateJson.resultWorkflowContext,
        'workflowV2String',
        workflowV2String
      )
      this.$emit('onSave', scriptData)
    },
  },
}
</script>
