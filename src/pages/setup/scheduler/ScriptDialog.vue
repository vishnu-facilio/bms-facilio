<template>
  <el-dialog
    :visible="true"
    width="60%"
    class="pB15 fc-dialog-center-container fc-dialog-center-body-p0"
    title="Script"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="height350 overflow-y-scroll pB50">
      <CodeMirror v-model="scriptData.workflow" :codeeditor="true"></CodeMirror>
    </div>

    <div class="modal-dialog-footer" style="z-index: 900;">
      <el-button @click="closeDialog" class="modal-btn-cancel">
        CANCEL
      </el-button>
      <el-button type="primary" class="modal-btn-save" @click="saveScript">
        Save
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import CodeMirror from '@/CodeMirror'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['script', 'name', 'header'],
  components: { CodeMirror },

  data() {
    return {
      scriptData: { workflow: null },
    }
  },

  created() {
    this.deserialize()
  },
  computed: {
    headerString() {
      if (isEmpty(this.header)) {
        return 'void test()'
      }
      return this.header
    },
  },
  methods: {
    deserialize() {
      if (!isEmpty(this.script)) {
        let { workflowV2String, id } = this.script[this.name]

        this.scriptData = {
          workflow: this.getScriptWorkflow(workflowV2String),
          id,
        }
      }
    },
    getScriptWorkflow(workflowString) {
      if (workflowString) {
        return workflowString
          .replace(this.headerString + ' { ', '')
          .slice(0, -2)
      }

      return null
    },
    serialize() {
      let { scriptData } = this
      let { workflow, id } = scriptData || {}
      let workflowV2String = this.headerString + ' { ' + workflow + ' }'
      let serializeData = {
        [this.name]: { isV2Script: true, workflowV2String },
      }
      let { [this.name]: workflowContext } = serializeData || {}
      if (!isEmpty(id)) {
        workflowContext = { ...workflowContext, id }
      }
      serializeData = { [this.name]: workflowContext }
      return serializeData
    },
    saveScript() {
      let { scriptData } = this
      let { workflow } = scriptData

      if (isEmpty(workflow)) {
        this.$message.error('Script is Empty')
        return
      }
      this.$emit('onSave', this.serialize())
      this.closeDialog()
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
