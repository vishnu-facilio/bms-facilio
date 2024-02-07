<template>
  <el-dialog
    title="Enable Dashboard Tab"
    :visible.sync="showModal"
    width="30%"
    :close-on-click-modal="false"
    :show-close="false"
  >
    <div class="height150">
      <el-row class="add-dashboard-popper">
        <el-col :span="24" class="mB10">{{ 'Tab Name' }}</el-col>
        <el-col :span="24">
          <el-input
            v-model="newTabName"
            class="fc-input-full-border2"
            placeholder="Enter Tab Name"
          ></el-input>
        </el-col>
      </el-row>
      <div class="pT15">
        <el-checkbox v-model="copyAllWidgets"
          >Copy all the widgets to new tab</el-checkbox
        >
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button
        class="modal-btn-cancel"
        @click="
          () => {
            showModal = false
          }
        "
        >Cancel</el-button
      >
      <el-button class="modal-btn-save" type="primary" @click="save">{{
        'Save'
      }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
export default {
  data() {
    return {
      isModalVisible: true,
      newTabName: '',
      copyAllWidgets: false,
    }
  },
  methods: {
    save() {
      if (this.newTabName == '') {
        this.$message.error('Tab name should not be empty!')
      } else {
        this.$emit('save', {
          tabName: this.newTabName,
          copyAllWidgets:
            this.copyAllWidgets == '' ? false : this.copyAllWidgets,
        })
      }
    },
  },
  computed: {
    showModal: {
      get() {
        return this.isModalVisible
      },
      set(value) {
        if (!value) {
          this.isModalVisible = false
          this.$nextTick(() => {
            this.$emit('close')
          })
        }
      },
    },
  },
}
</script>
<style lang="scss"></style>
