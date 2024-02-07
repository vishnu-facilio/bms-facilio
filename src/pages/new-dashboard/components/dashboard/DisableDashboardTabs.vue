<template>
  <el-dialog
    title="Disable Dashboard Tab"
    :visible.sync="showModal"
    :close-on-click-modal="false"
    :show-close="false"
    width="30%"
  >
    <div class="height150">
      <el-checkbox v-model="copyAllWidgets">
        Copy tab widgets to main dashboard</el-checkbox
      >
      <div class="pT15" v-if="copyAllWidgets">
        <el-row class="add-dashboard-popper">
          <el-col :span="24" class="mB10">{{ 'Tab Name' }}</el-col>
          <el-col :span="24">
            <el-select
              class="el-input-textbox-full-border"
              v-model="toTab"
              placeholder="Please select a Tab"
            >
              <el-option
                :label="tab.name"
                :value="tab.id"
                v-for="(tab, ix) in tabs"
                :key="ix"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="() => $emit('close')"
        >Cancel</el-button
      >
      <el-button class="modal-btn-save" type="primary" @click="save"
        >Confirm</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
export default {
  data() {
    return {
      toTab: '',
      copyAllWidgets: false,
      isModalVisible: true,
    }
  },
  props: {
    tabs: {
      type: Array,
      required: true,
    },
  },
  methods: {
    save() {
      if (this.copyAllWidgets) {
        if (this.toTab != '') {
          this.$emit('save', {
            toTab: this.toTab,
            copyAllWidgets: this.copyAllWidgets,
          })
        } else {
          this.$message.error('Please select a tab!')
        }
      } else {
        this.$emit('save', {
          copyAllWidgets: false,
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
