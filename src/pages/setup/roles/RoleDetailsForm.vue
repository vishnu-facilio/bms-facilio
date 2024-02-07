<template>
  <el-dialog
    :title="`${isNew ? 'New' : 'Edit'} Role`"
    :visible="true"
    width="30%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <el-form
      :model="role"
      :rules="rules"
      :label-position="'top'"
      ref="roleForm"
      class="pB50"
    >
      <el-form-item :label="$t('common.products.name')" prop="name">
        <el-input
          v-model="role.name"
          :placeholder="$t('common._common.enter_role_name')"
          class="width100 fc-input-full-border2"
          autofocus
        ></el-input>
      </el-form-item>
      <el-form-item
        :label="$t('common.wo_report.report_description')"
        prop="description"
      >
        <el-input
          type="textarea"
          :autosize="{ minRows: 4, maxRows: 4 }"
          class="fc-input-full-border-textarea"
          :placeholder="$t('setup.setupLabel.add_a_decs')"
          v-model="role.description"
          resize="none"
        ></el-input>
      </el-form-item>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog" class="modal-btn-cancel">
          {{ $t('common._common.cancel') }}
        </el-button>
        <el-button type="primary" class="modal-btn-save" @click="saveRole">
          {{ $t('common._common.confirm') }}
        </el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['isNew', 'role', 'appId', 'isWebTabRole'],
  data() {
    return {
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
      },
    }
  },

  methods: {
    async saveRole() {
      let { appId } = this

      let valid = await this.$refs['roleForm'].validate()
      if (!valid) return

      let {
        name,
        description,
        roleId,
        newPermissions = [],
        permissions = [],
      } = this.role

      let params = {}
      let successMsg = ''
      let url = ''

      if (this.isNew) {
        url = '/setup/addRole'
        successMsg = this.$t('common.products.role_created_successfully')
        params = {
          role: { name, description },
          roleApp: [{ applicationId: appId }],
          permissions: [],
        }
      } else {
        successMsg = this.$t('common.products.role_updated_successfully')

        if (this.isWebTabRole) {
          url = '/setup/updateWebTabRole'
          let newPerm = []

          if (!isEmpty(newPermissions)) {
            newPerm = newPermissions.map(perm => {
              let { tabId, permission } = perm
              return { tabId, permission }
            })
          }
          params = {
            role: { name, description, roleId },
            roleApp: [{ applicationId: appId }],
            newPermissions: newPerm,
          }
        } else {
          url = '/setup/updaterole'
          params = {
            role: { name, description, roleId },
            roleApp: [{ applicationId: appId }],
            permissions,
          }
        }
      }

      API.post(url, params).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success(successMsg)
          this.$emit('onSave')
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
