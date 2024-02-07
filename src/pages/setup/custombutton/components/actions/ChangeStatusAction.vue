<template>
  <div>
    <ActionConfiguredHelper
      :isActionConfigured="isChangeStatusConfigured"
      helperText="Update Status after button execution"
      @edit="editAction()"
      @reset="reset(changeStatus)"
    />
    <ChangeStatusDialog
      v-if="showStatusDialog"
      :actionObj="changeStatus"
      :moduleName="module"
      :moduleId="moduleId"
      @onSave="actionSave"
      @onClose="showStatusDialog = false"
    ></ChangeStatusDialog>
  </div>
</template>

<script>
import { actionsHash } from './actionHash'
import ChangeStatusDialog from 'src/newapp/setupActions/components/StatusUpdate.vue'
import ActionConfiguredHelper from './ActionConfiguredHelper'

export default {
  name: 'ChangeStatusAction',
  props: [
    'showChangeStatusDialog',
    'existingAction',
    'actionType',
    'module',
    'moduleFields',
    'moduleId',
  ],
  components: { ChangeStatusDialog, ActionConfiguredHelper },
  data() {
    return {
      isChangeStatusConfigured: false,
      actionHash: actionsHash,
      picklistOptions: {},
      changeStatus: {
        actionType: 19,
        templateJson: { new_state: null },
      },
      showStatusDialog: this.showChangeStatusDialog,
    }
  },
  async created() {
    if (this.existingAction) {
      this.deserialize()
    }
  },
  watch: {
    showChangeStatusDialog: {
      handler: function(newVal) {
        this.showStatusDialog = newVal
      },
    },
    showStatusDialog: function() {
      if (!this.showStatusDialog) {
        this.$emit('update:showChangeStatusDialog', false)
      }
    },
  },
  methods: {
    deserialize() {
      let actions = this.existingAction
      if (actions && actions.length !== 0) {
        actions.forEach(action => {
          if (parseInt(action.actionType) === 19) {
            this.isChangeStatusConfigured = true
            let new_state = this.$getProperty(
              action,
              'template.originalTemplate.new_state',
              null
            )
            let templateJson = {
              new_state,
            }
            this.changeStatus = { ...this.changeStatus, templateJson }
          }
        })
      }
    },

    closeDialog() {
      this.showStatusDialog = false
    },

    actionSave(data) {
      this.$emit('update:actionType', actionsHash.CHANGE_STATUS.name)
      this.isChangeStatusConfigured = true
      this.showStatusDialog = false
      this.changeStatus = data
      this.$emit('setProperties', data)
    },
    editAction() {
      this.isChangeStatusConfigured = true
      this.showStatusDialog = true
    },
    reset(action) {
      this.isChangeStatusConfigured = false
      this.$emit('resetAction', action)
    },
  },
}
</script>

<style></style>
