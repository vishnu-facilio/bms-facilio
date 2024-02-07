<template>
  <el-dialog
    :visible="true"
    width="50%"
    title="Reorder Weightage Groups"
    class="fieldchange-Dialog pB15 fc-dialog-center-container"
    custom-class="dialog-header-padding"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="height350 pB50 overflow-scroll">
      <draggable
        :list="weightageGrps"
        v-bind="fieldSectionDragOptions"
        draggable=".is-draggable"
        handle=".task-handle"
      >
        <div
          v-for="(grp, index) in weightageGrps"
          :key="`${grp.name}_${index}`"
          :class="[
            'reorder-weightage-grp',
            !$validation.isEmpty(grp.namedCriteriaId)
              ? 'is-draggable'
              : 'is-default',
          ]"
        >
          <div class="task-handle mR10 p10">
            <img src="~assets/drag-grey.svg" />
          </div>
          <div class="grp-name">
            {{ grp.name }}
          </div>
        </div>
      </draggable>
    </div>

    <div class="modal-dialog-footer">
      <el-button @click="closeDialog()" class="modal-btn-cancel">
        CANCEL
      </el-button>
      <el-button type="primary" class="modal-btn-save" @click="save()">
        Save
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import draggable from 'vuedraggable'
import clone from 'lodash/clone'

export default {
  props: ['scoreWeightage'],

  components: { draggable },

  data() {
    return {
      weightageGrps: [],
      fieldSectionDragOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
    }
  },

  created() {
    this.weightageGrps = clone(this.scoreWeightage)
  },

  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    save() {
      this.$emit('onSave', this.weightageGrps)
      this.closeDialog()
    },
  },
}
</script>
<style lang="scss">
.reorder-weightage-grp {
  border: 1px solid #f2f5f6;
  background-color: #fff;
  display: flex;
  align-items: center;
  padding: 5px 20px;
  margin: 10px 0px 15px;

  &:hover {
    background-color: #fff;
    border: 1px solid #b0dbe1;
    z-index: 1;
  }
  &.is-draggable .task-handle {
    cursor: move;
  }
  &:not(.is-draggable),
  &:hover:not(.is-draggable) {
    border-color: #d5efff;
    background-color: #eff5f9;
  }
  .grp-name {
    font-size: 14px;
    letter-spacing: 0.5px;
    color: #324056;
  }
}
</style>
