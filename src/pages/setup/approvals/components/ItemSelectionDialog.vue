<template>
  <el-dialog
    :visible="true"
    width="40%"
    :title="title"
    class="fieldchange-Dialog pB15 fc-dialog-center-container"
    custom-class="dialog-header-padding"
    :append-to-body="true"
    :before-close="close"
  >
    <div class="height330 overflow-y-scroll pB50 transition-field-dialog">
      <el-row>
        <el-col :span="24">
          <el-checkbox-group v-model="selectedItemIds" class="checkbox-group">
            <el-checkbox
              v-for="(type, index) in availableItems"
              :key="index"
              :label="type[itemKey]"
              class="checkbox field-row"
            >
              {{ type.displayName || type.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-col>
      </el-row>
      <div class="modal-dialog-footer">
        <el-button @click="close()" class="modal-btn-cancel">CANCEL</el-button>
        <el-button type="primary" class="modal-btn-save" @click="save()">
          Save
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
export default {
  props: ['value', 'availableItems', 'itemKey', 'title'],
  data() {
    return {
      selectedItemIds: [],
    }
  },
  watch: {
    value: {
      handler() {
        this.selectedItemIds = (this.value || []).map(
          item => item[this.itemKey]
        )
      },
      immediate: true,
    },
  },
  methods: {
    close() {
      this.$emit('close')
    },
    save() {
      let selectedTypes = (this.selectedItemIds || []).map(id => {
        return (this.availableItems || []).find(
          item => item[this.itemKey] === id
        )
      })
      this.$emit('input', selectedTypes)
      this.close()
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
    margin-right: 0;
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
