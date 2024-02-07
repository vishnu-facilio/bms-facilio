<template>
  <div>
    <el-dialog
      :title="
        isNew
          ? $t('setup.create.create_user_scope')
          : $t('setup.create.edit_user_scope')
      "
      :visible="true"
      width="30%"
      class="fc-dialog-center-container"
      :before-close="closeDialog"
    >
      <el-form ref="scopeForm" :model="scopingContext" :label-position="'top'">
        <div class="height330 overflow-y-scroll pB50">
          <el-form-item label="Scope name">
            <el-input
              v-model="scopingContext.scopeName"
              placeholder="Enter the scope name"
              class="fc-input-full-border2 fc-input-user-scope"
            ></el-input>
          </el-form-item>

          <el-form-item :label="$t('common._common.enter_desc')">
            <el-input
              type="textarea"
              :autosize="{ minRows: 2, maxRows: 2 }"
              resize="none"
              class="fc-input-full-border-select2"
              :placeholder="$t('common._common.enter_desc')"
              v-model="scopingContext.description"
            >
            </el-input>
          </el-form-item>
        </div>
      </el-form>
      <div class="modal-dialog-footer" style="z-index: 900;">
        <el-button @click="closeDialog" class="modal-btn-cancel">{{
          $t('setup.users_management.cancel')
        }}</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          :loading="saving"
          @click="addScope"
          >{{ $t('panel.dashboard.confirm') }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
export default {
  props: ['applicationId', 'userScopeData', 'isNew'],
  data() {
    return {
      saving: false,
      scopingContext: {
        scopeName: '',
        description: '',
      },
    }
  },
  created() {
    if (!this.isNew) {
      this.scopingContext = {
        ...this.userScopeData,
      }
    }
  },
  methods: {
    addScope() {
      this.$refs['scopeForm'].validate(async valid => {
        if (!valid) return false
        this.laoding = true
        let url = 'v2/scoping/addOrUpdate'
        let params = {}
        if (this.isNew) {
          params.scopingContext = {
            scopeName: this.scopingContext.scopeName,
            applicationId: this.applicationId,
            description: this.scopingContext.description,
          }
        } else {
          params.scopingContext = {
            scopeName: this.scopingContext.scopeName,
            applicationId: this.applicationId,
            description: this.scopingContext.description,
            id: this.userScopeData.id,
          }
        }
        let { error, data } = await API.post(url, params)

        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.$message.success(
            this.$t('common._common.user_scope_saved_successfully')
          )
          this.$emit('onSave', data.scopingContext)
          this.closeDialog()
        }
      })
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
