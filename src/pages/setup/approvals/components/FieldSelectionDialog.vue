<template>
  <el-dialog
    :visible="true"
    width="50%"
    title="Fields"
    class="fieldchange-Dialog pB15 fc-dialog-center-container"
    custom-class="dialog-header-padding"
    :append-to-body="true"
    :before-close="close"
  >
    <div class="height330 overflow-y-scroll pB50 transition-field-dialog">
      <el-row class="pR30 mB10">
        <el-col :span="16">
          <p class="fc-input-label-txt txt-color">
            <slot name="description"></slot>
          </p>
        </el-col>
        <el-col :span="8">
          <field-picker
            class="fR"
            :list.sync="selectedList"
            :availableFields="availableFields"
          ></field-picker>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="24">
          <draggable
            :list="selectedList"
            v-bind="fieldSectionDragOptions"
            :group="'list'"
          >
            <div v-for="(field, index) in selectedList" :key="index">
              <div
                v-if="field.hasOwnProperty('fieldId')"
                class="pT20 pB20 field-row"
              >
                <div v-if="isInFormConfig" class="task-handle mR10">
                  <img src="~assets/drag-grey.svg" />
                </div>
                <div>{{ field.displayName || '' }}</div>
                <div class="mL-auto">
                  <div
                    v-if="isInFormConfig"
                    class="mR10 pR10 inline mandatory-checkbox field-checkbox"
                  >
                    <el-checkbox v-model="field.required"
                      >Is Mandatory</el-checkbox
                    >
                  </div>
                  <div @click="removeField(field)" class="mR10 inline">
                    <i class="el-icon-delete pointer trash-icon"></i>
                  </div>
                </div>
              </div>
            </div>
          </draggable>
        </el-col>
      </el-row>

      <div class="modal-dialog-footer">
        <el-button @click="close()" class="modal-btn-cancel">CANCEL</el-button>
        <el-button type="primary" class="modal-btn-save" @click="save()"
          >Save</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>
<script>
import findIndex from 'lodash/findIndex'
import draggable from 'vuedraggable'
import FieldPicker from '@/FieldPicker'
import cloneDeep from 'lodash/cloneDeep'

export default {
  props: ['moduleName', 'selectedFields', 'availableFields', 'isInFormConfig'],
  components: {
    FieldPicker,
    draggable,
  },
  created() {
    this.selectedList = cloneDeep(this.selectedFields || [])
  },
  data() {
    return {
      selectedList: [],
      fieldSectionDragOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
    }
  },
  methods: {
    removeField(field) {
      let index = findIndex(this.selectedList, ['name', field.name])
      this.selectedList.splice(index, 1)
    },
    save() {
      this.$emit('save', this.selectedList)
    },
    close() {
      this.$emit('close')
    },
  },
}
</script>
<style lang="scss" scoped>
.transition-field-dialog {
  .field-row {
    padding: 20px 10px;
    border-bottom: 1px solid #f4f5f7;
    display: flex;
  }
  .field-row:hover {
    background-color: #f1f8fa;
  }
  .field-checkbox {
    border-right: 1px solid #eae9e9;
    letter-spacing: 0.4px;
  }
}
.field-row {
  padding: 20px 10px;
  border-bottom: 1px solid #f4f5f7;
  display: flex;
}
.field-row:hover {
  background-color: #f1f8fa;
}
.field-checkbox {
  border-right: 1px solid #eae9e9;
  letter-spacing: 0.4px;
}
</style>
