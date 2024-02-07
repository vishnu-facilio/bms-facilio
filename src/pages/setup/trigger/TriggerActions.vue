<template>
  <el-dialog
    :visible="true"
    width="50%"
    :title="`${trigger.name} Actions`"
    class="fieldchange-Dialog pB15 fc-dialog-center-container trigger-summary"
    custom-class="dialog-header-padding"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="height350 pB50">
      <div class="trigger-descrip">
        List of rules to which this trigger is associated.
      </div>

      <div class="height250">
        <div
          v-if="$validation.isEmpty(triggerActions)"
          class="d-flex height-100"
        >
          <div class="empty-state">
            <inline-svg
              src="svgs/emptystate/readings-empty"
              iconClass="icon text-center icon-60"
              class="margin-auto"
            >
            </inline-svg>
            <div class="margin-auto" style="color: #909399;">
              No actions available
            </div>
          </div>
        </div>

        <draggable
          v-else
          :list="triggerActions"
          v-bind="fieldSectionDragOptions"
          handle=".task-handle"
        >
          <div
            v-for="(action, index) in triggerActions"
            :key="`${action.id}_${index}`"
            class="trigger-actions"
          >
            <div class="task-handle mR10">
              <img src="~assets/drag-grey.svg" />
            </div>
            <div class="action-details">
              <div class="action-name">
                {{ action.name || action.typeRefPrimaryId }}
              </div>
              <div class="action-type">
                {{ actionTypes[action.actionType] }}
              </div>
            </div>
          </div>
        </draggable>
      </div>
    </div>

    <div class="modal-dialog-footer">
      <el-button @click="closeDialog()" class="modal-btn-cancel">
        CANCEL
      </el-button>
      <el-button
        type="primary"
        class="modal-btn-save"
        @click="save()"
        :loading="saving"
      >
        Save
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import draggable from 'vuedraggable'
import sortBy from 'lodash/sortBy'

const actionTypes = {
  2: 'Rule Execution',
}

export default {
  props: ['trigger'],

  components: { draggable },

  data() {
    return {
      triggerActions: [],
      saving: false,
      fieldSectionDragOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
      actionTypes,
    }
  },

  created() {
    let { triggerActions } = this.trigger
    this.triggerActions = sortBy(triggerActions || [], ['executionOrder'])
  },

  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    async save() {
      this.saving = true
      try {
        await this.trigger.saveActionsOrder(this.triggerActions)
        this.$message.success('Actions rearranged successfully')
        this.closeDialog()
      } catch (errorMsg) {
        this.$message.error(errorMsg)
        this.saving = false
      }
    },
  },
}
</script>
<style lang="scss">
.trigger-summary {
  .trigger-descrip {
    font-size: 13px;
    color: #808080;
    letter-spacing: 0.3px;
    padding-bottom: 10px;
    word-break: break-word;
  }
  .empty-state {
    display: flex;
    margin: auto;
    flex-direction: column;
  }
  .trigger-actions {
    padding: 10px 10px;
    border: 1px solid #f4f5f7;
    box-shadow: 0px 3px 5px #f4f5f7;
    display: flex;
    font-size: 13px;
    margin: 15px 0px 10px;
    align-items: center;
  }
  .task-handle {
    padding: 10px;
    cursor: move;
  }
  .action-details {
    display: flex;
    flex-direction: column;
    margin: auto 0px;
  }
  .action-name {
    font-size: 14px;
    letter-spacing: 0.5px;
    color: #324056;
  }
  .action-type {
    font-size: 12px;
    letter-spacing: 0.5px;
    color: #8ca0ad;
    margin-top: 5px;
  }
}
</style>
