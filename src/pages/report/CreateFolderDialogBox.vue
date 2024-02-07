<template>
  <div>
    <base-dialog-box
      :visibility.sync="visibility"
      :title="'New Folder'"
      :onConfirm="createFolder"
      :onCancel="closeDialog"
      :disableCancel="false"
      cancelText="Cancel"
      confirmText="Save"
      width="490px"
    >
      <div class="dialog-content-body" slot="body">
        <el-row class="input-section">
          <el-col :span="8">
            <div class="module-text">
              Folder Name
            </div>
          </el-col>
          <el-col>
            <el-input
              v-model="folderName"
              class="fc-input-full-border-select2 name-field"
            ></el-input>
            <div v-if="!folderName && validate" class="error-message">
              Please enter Name
            </div>
          </el-col>
        </el-row>
      </div>
    </base-dialog-box>
  </div>
</template>

<script>
import BaseDialogBox from 'src/pages/energy/pivot/Components/BaseDialogBox'

export default {
  props: ['visibility'],
  components: {
    BaseDialogBox,
  },
  data() {
    return {
      folderName: null,
      description: null,
      validate: false,
    }
  },

  methods: {
    closeDialog() {
      this.$emit('dialogClosed')
    },
    createFolder() {
      if (this.folderName) {
        this.$emit('createFolder', this.folderName)
      } else {
        this.validate = true
      }
    },
  },
}
</script>

<style scoped lang="scss">
.dialog-content-body {
  display: flex;
  flex-direction: column;
  padding: 10px 0px;
  .error-message {
    color: #f56c6c;
    font-size: 12px;
    line-height: 1;
    padding-top: 4px;
  }
  .module-text:not(.description)::after {
    content: '*';
    color: #f56c6c;
    margin-right: 4px;
  }
}
.module-text {
  font-size: 14px;
  color: #324056;
  margin-bottom: 8px;
}

.input-section {
  padding: 8px 0;
  display: flex;
  align-items: left;
  flex-direction: column;
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #324056;
  width: 408px;
}

.dialog-footer {
  padding-bottom: 10px !important;
  margin-top: 15px !important;
}
</style>
