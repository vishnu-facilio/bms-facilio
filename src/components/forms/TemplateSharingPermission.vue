<template>
  <div
    v-if="isLoading"
    class="flex-middle fc-empty-white m10 fc-agent-empty-state"
  >
    <Spinner :show="isLoading" size="80"></Spinner>
  </div>
  <div v-else>
    <el-dialog
      :title="$t('setup.setup.template_permissions')"
      :visible="true"
      :fullscreen="false"
      :append-to-body="true"
      custom-class="fc-dialog-center-container module-template-permission"
      :before-close="closeDialog"
      class="d-flex"
    >
      <span>
        {{ $t('setup.setup.select_role_info') }}
      </span>
      <el-form
        ref="templateSharingPermissionForm"
        :model="templatePermissionObj"
        :rules="rules"
      >
        <el-form-item prop="role">
          <el-radio-group
            v-model="templatePermissionObj.role"
            class="mT25"
            @change="showRoleLookUp"
          >
            <el-radio
              v-for="(value, label) in buttonOptions"
              :label="value"
              :key="value"
              class="fc-radio-btn"
            >
              {{ label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="showRoleSelector" prop="sharedRoles">
          <el-select
            multiple
            :placeholder="$t('setup.setup.all_roles')"
            filterable
            v-model="templatePermissionObj.sharedRoles"
            class="fc-input-full-border  template-permission-select width100"
          >
            <el-option
              v-for="role in roleList"
              :key="role.roleId"
              :label="role.name"
              :value="role.roleId"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div class="modal-dialog-footer">
        <el-button
          @click="closeDialog"
          class="modal-btn-cancel border-bottom-left-radius"
        >
          {{ $t('agent.agent.cancel') }}</el-button
        >
        <el-button
          type="primary"
          @click="updateSharingPermission"
          :loading="saving"
          class="modal-btn-save border-bottom-right-radius"
          >{{ handleSaving }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
export default {
  props: ['selectedForm'],
  components: {
    Spinner,
  },
  data() {
    return {
      roleList: [],
      saving: false,
      isLoading: false,
      buttonOptions: { 'All Roles': 'allRoles', 'Selected role': 'shared' },
      templatePermissionObj: {
        id: null,
        role: null,
        sharedRoles: [],
      },
      rules: {
        sharedRoles: {
          trigger: 'change',
          validator: function(rule, value, callback) {
            let { role, sharedRoles } = this.templatePermissionObj || {}
            if (role === 'shared' && isEmpty(sharedRoles)) {
              callback(
                new Error(
                  this.$t('common._common.please_select_alteast_one_roles')
                )
              )
            } else {
              callback()
            }
          }.bind(this),
        },
      },
    }
  },
  computed: {
    handleSaving() {
      return this.saving
        ? this.$t('common._common._saving')
        : this.$t('common._common._save')
    },
    showRoleSelector() {
      let { role } = this.templatePermissionObj || {}
      return role === 'shared'
    },
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      this.isLoading = true
      await this.setFilledRoles()
      await this.getRoles()
      this.isLoading = false
    },

    async setFilledRoles() {
      let { id } = this.selectedForm || {}
      if (isEmpty(id)) return

      let { data, error } = await API.get(
        'v2/forms/formSharing/getSharingList',
        { formId: id }
      )
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { formSharing } = data || {}
        let role = !isEmpty(formSharing) ? 'shared' : 'allRoles'
        let sharedRoles = (formSharing || [])
          .map(template => template?.roleId)
          .filter(roleId => !isEmpty(roleId))

        this.templatePermissionObj = { role, sharedRoles, id }
      }
    },

    async getRoles() {
      let { appId } = this.selectedForm || {}
      let params = { appId }
      let { data, error } = await API.get('/setup/roles', params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.roleList = data?.roles || []
      }
    },

    closeDialog() {
      this.$emit('close')
    },

    showRoleLookUp() {
      this.$set(this.templatePermissionObj, 'sharedRoles', [])
    },
    async validate() {
      try {
        return await this.$refs['templateSharingPermissionForm'].validate()
      } catch (error) {
        return false
      }
    },

    async updateSharingPermission() {
      let isValid = await this.validate()

      if (!isValid) return

      this.saving = true
      let { sharedRoles, id } = this.templatePermissionObj || {}
      let formSharing = (sharedRoles || []).map(roleId => ({
        type: 2,
        roleId,
      }))
      let params = { form: { id, formSharing } }
      let url = 'v2/forms/formSharing/addOrUpdate'
      let { error } = await API.post(url, params)

      if (error) {
        this.$message.error(error.message || 'Error occured')
      } else {
        this.$emit('saved')
        this.$message.success(
          this.$t('common.products.role_updated_successfully')
        )
        this.closeDialog()
      }
      this.saving = false
    },
  },
}
</script>
<style scoped>
.border-bottom-left-radius {
  border-bottom-left-radius: 4px;
}
.border-bottom-right-radius {
  border-bottom-right-radius: 4px;
}
</style>
<style lang="scss">
.module-template-permission.el-dialog {
  padding-bottom: 60px;
  border-radius: 4px;
  width: 532px;
  margin: auto !important;
  margin-top: 15vh !important;
}
.template-permission-select {
  .el-select__tags {
    flex-wrap: wrap !important;
  }
  .el-tag {
    border-radius: 50px;
    border: solid 1px #39b2c2;
    color: #39b2c2;
    background: #fff;
  }
  .el-icon-close {
    color: #39b2c2 !important;
    background-color: #fff !important;
  }
}
</style>
