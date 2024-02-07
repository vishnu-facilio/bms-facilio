<template>
  <el-dialog
    :visible="true"
    width="60%"
    class="pB15 fc-dialog-center-container fc-dialog-center-body-p0"
    title="script"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="height550 overflow-y-scroll pB30 pT20 pR30 pL30">
      <el-form ref="script-dialog" :rules="rules" :model="scriptData">
        <el-form-item label="Name" prop="name" :required="true">
          <el-input
            class="width100 fc-input-full-border2"
            autofocus
            v-model="scriptData.name"
            placeholder="Enter Script Name"
          />
        </el-form-item>
      </el-form>
      <p class="fc-input-label-txt">Script</p>
      <code-mirror
        v-model="scriptData.workflow"
        :codeeditor="true"
        class="condition-manager-script"
      ></code-mirror>
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
  props: ['selectedScript', 'moduleName', 'expressionIdx'],
  components: { CodeMirror },

  data() {
    return {
      scriptData: { name: null, workflow: null },
      rules: {
        name: {
          required: true,
          message: 'Please enter a name',
          trigger: 'change',
        },
      },
    }
  },

  created() {
    let { workflowContext, name } = this.selectedScript || {}

    if (!isEmpty(workflowContext)) {
      let { workflowV2String, id: workflowId } = workflowContext || {}

      this.scriptData = {
        name,
        workflow: this.getScriptWorkflow(workflowV2String),
        workflowId,
      }
    } else {
      this.scriptData.name = `Expression ${this.expressionIdx}`
    }
  },

  methods: {
    getScriptWorkflow(workflowString) {
      if (workflowString) {
        return workflowString
          .replace('boolean scriptFunc(Map ' + this.moduleName + ') {\n', '')
          .replace('void scriptFunc(Map ' + this.moduleName + ') {\n', '')
          .replace('Boolean scriptFunc(Map ' + this.moduleName + ') {\n', '')
          .replace(new RegExp('\n}' + '$'), '')
      }

      return null
    },

    saveScript() {
      let { scriptData, moduleName } = this
      let { workflow, name, workflowId } = scriptData || {}

      this.$refs['script-dialog'].validate(async valid => {
        if (!valid) return false

        if (isEmpty(workflow)) {
          this.$message.error('Script is Empty')
          return
        }

        let workflowV2String =
          'Boolean scriptFunc(Map ' + moduleName + ') {\n' + workflow + '\n}'
        let workflowContext = { isV2Script: true, workflowV2String }
        if (!isEmpty(workflowId)) {
          workflowContext = { ...workflowContext, id: workflowId }
        }
        let scriptDataToSave = {
          name,
          workflowContext,
        }

        this.$emit('onSave', scriptDataToSave)
        this.closeDialog()
      })
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss">
.condition-manager-script {
  .CodeMirror {
    height: 300px;
    border: 1px solid #ddd;
  }
}
</style>
