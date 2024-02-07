<template>
  <el-dialog
    :visible="true"
    :title="'Application Access'"
    :append-to-body="true"
    :before-close="close"
    maxHeight="300px"
    width="38%"
    class="fc-dialog-center-container"
  >
    <el-row :gutter="20" class="pB50">
      <el-col class="mB20">
        <el-checkbox
          :disabled="isSuperAdmin"
          v-model="userData.isAppAccess"
          @change="selectedRoleId = null"
          >Application Access</el-checkbox
        >
      </el-col>
      <el-col class="mB20">
        <el-select
          v-model="selectedRoleId"
          :disabled="isSuperAdmin"
          :loading="rolesLoading"
          class="fc-input-full-border-select2 width100"
        >
          <el-option
            v-for="role in rolesList"
            :key="role.value"
            :label="role.label"
            :value="role.value"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>

    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="close()">Cancel</el-button>
      <el-button
        :loading="saving"
        :disabled="isDisabled"
        class="modal-btn-save"
        @click="saveAppAccess()"
        >Ok</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['user', 'save'],

  data() {
    return {
      rolesList: [],
      rolesLoading: false,
      saving: false,
      userData: {},
      selectedRoleId: null,
    }
  },

  async created() {
    let { rolesMap } = this.user
    let roleId = (rolesMap || {})['newapp']

    this.userData = { ...this.user }
    this.selectedRoleId = roleId
    await this.loadRoles()
  },

  computed: {
    ...mapGetters(['getRoleNameById']),

    isSuperAdmin() {
      let { rolesMap = {} } = this.userData || {}
      let roleId = (rolesMap || {})['newapp']

      return this.getRoleNameById(roleId) === 'Super Administrator'
    },
    isDisabled() {
      let {
        userData: { isAppAccess },
        selectedRoleId,
      } = this
      return isAppAccess && isEmpty(selectedRoleId)
    },
  },

  methods: {
    async loadRoles(linkName = 'newapp') {
      let filters = JSON.stringify({ appLinkNames: [linkName] })

      this.rolesLoading = true
      let { error, data } = await API.get('/v3/picklist/role', { filters })

      if (error) {
        this.$message.error(error.message || 'Error Occcured')
      } else {
        let { pickList } = data || {}
        this.rolesList = pickList || []
      }
      this.rolesLoading = false
    },
    saveAppAccess() {
      let { userData, selectedRoleId } = this
      let userRolesMap = userData.rolesMap || {}
      let userObj = {
        ...userData,
        rolesMap: { ...userRolesMap, newapp: selectedRoleId },
      }

      this.saving = true
      this.save(userObj)
        .then(() => this.close())
        .catch(() => {})
        .finally(() => (this.saving = false))
    },
    close() {
      this.$emit('onClose')
    },
  },
}
</script>
