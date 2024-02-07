<template>
  <div>
    <ActionConfiguredHelper
      :isActionConfigured="isExecuteScriptConfigured"
      helperText="Execute Script after button execution"
      @edit="editAction()"
      @reset="reset(scriptData)"
    />

    <ScriptDialog
      v-if="showScriptDialog"
      :actionObj="scriptData"
      :moduleName="module"
      @onSave="actionSave"
      @onClose="showScriptDialog = false"
    ></ScriptDialog>
  </div>
</template>

<script>
import ScriptDialog from 'src/newapp/setupActions/components/ScriptEditor.vue'
import { actionsHash } from './actionHash'
import ActionConfiguredHelper from './ActionConfiguredHelper'
export default {
  props: ['module', 'showScriptCodeDialog', 'actionType', 'existingAction'],
  components: { ScriptDialog, ActionConfiguredHelper },
  data() {
    return {
      isExecuteScriptConfigured: false,
      showScriptDialog: this.showScriptCodeDialog,
      scriptData: null,
    }
  },
  created() {
    if (this.existingAction) {
      this.deserialize()
    }
  },
  watch: {
    showScriptCodeDialog: {
      handler: function(newVal) {
        if (this.actionType === actionsHash.SCRIPT.name)
          this.showScriptDialog = newVal
      },
    },
    showScriptDialog: function() {
      if (!this.showScriptDialog) {
        this.$emit('update:showScriptCodeDialog', false)
      }
    },
  },
  methods: {
    deserialize() {
      let actions = this.existingAction
      if (actions && actions.length !== 0) {
        actions.forEach(action => {
          if (parseInt(action.actionType) === 21) {
            this.isExecuteScriptConfigured = true
            this.scriptData = action
            let templateJson = {
              resultWorkflowContext:
                action.template.originalTemplate.workflowContext,
            }
            this.scriptData = { ...this.scriptData, templateJson }
            this.$emit('setProperties', this.scriptData)
          }
        })
      }
    },
    actionSave(data) {
      this.$emit('update:actionType', actionsHash.SCRIPT.name)
      this.showScriptDialog = false
      this.isExecuteScriptConfigured = true
      this.scriptData = data || {}
      this.$emit('setProperties', data)
    },
    editAction() {
      this.showScriptDialog = true
      this.isExecuteScriptConfigured = true
    },
    reset(action) {
      this.isExecuteScriptConfigured = false
      this.scriptData = null
      this.$emit('resetAction', action)
    },
  },
}
</script>

<style></style>
