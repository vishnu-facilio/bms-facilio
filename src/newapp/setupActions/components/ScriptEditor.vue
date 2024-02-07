<template>
  <el-dialog
    :visible="true"
    width="60%"
    class="fieldchange-Dialog pB15 fc-dialog-center-container fc-dialog-center-body-p0"
    :title="$t('common.products.script')"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="height350 overflow-y-scroll pB50">
      <CodeMirror
        :codeeditor="true"
        v-model="scriptData.workflowContext.workflowV2String"
      ></CodeMirror>
    </div>
    <div class="modal-dialog-footer" style="z-index: 900;">
      <el-button @click="closeDialog" class="modal-btn-cancel"
        >CANCEL</el-button
      >
      <el-button type="primary" class="modal-btn-save" @click="actionSave">
        Save
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import CodeMirror from '@/CodeMirror'
import { ScriptModel } from '../models/ScriptModel'

export default {
  props: ['actionObj', 'moduleName'],
  components: { CodeMirror },
  data() {
    return {
      scriptData: null,
    }
  },
  created() {
    let { actionObj, moduleName } = this
    let scriptObj = { ...(actionObj || {}), moduleName }

    this.scriptData = new ScriptModel(scriptObj)
  },
  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    actionSave() {
      let isValid = this.scriptData.validate()

      if (!isValid) {
        this.$message.error('Script is Empty')
        return
      }
      this.$emit('onSave', this.scriptData.serialize())
      this.closeDialog()
    },
  },
}
</script>
