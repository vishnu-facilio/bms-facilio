<template>
  <el-dialog
    :visible="true"
    class="fc-dialog-center-container"
    maxHeight="300px"
    width="38%"
    :append-to-body="true"
    :title="'Portal Access'"
    :before-close="closeDialog"
  >
    <el-form
      :model="rolesMap"
      :rules="rules"
      ref="portalAccessForm"
      class="pB50"
    >
      <el-row class="mB20">
        <div class="text-uppercase fw6">{{ 'Role' }}</div>
        <div class="fc-heading-border-width43 mT5 width30px"></div>
      </el-row>
      <el-row v-for="linkName in appLinkNames" :key="linkName" class="mB10">
        <el-col class="mB10">
          <el-checkbox
            v-model="userObj[linkNameVsDataKey[linkName]]"
            @change="showRoleSelection(linkName)"
          >
            {{ linkNameVsDisplayName[linkName] }}
          </el-checkbox>
        </el-col>

        <el-col>
          <el-form-item :prop="linkName">
            <el-select
              v-model="rolesMap[linkName]"
              placeholder="Select role"
              filterable
              :loading="rolesLoading[linkName]"
              :disabled="!userObj[linkNameVsDataKey[linkName]]"
              class="fc-input-full-border2 width100"
            >
              <el-option
                v-for="role in rolesList[linkName]"
                :key="role.value"
                :label="role.label"
                :value="role.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row
        v-if="showUserScopingOption || showPermissionSetOption"
        class="mB20 mT20"
      >
        <div class="text-uppercase fw6">{{ 'Data Sharing' }}</div>
        <div class="fc-heading-border-width43 mT5"></div>
      </el-row>
      <template v-if="showUserScopingOption">
        <el-row class="fc-dialog-center-container">
          <el-col :span="24">
            <div>{{ 'User Scoping' }}</div>
          </el-col>
        </el-row>
        <el-row class="mT10">
          <el-col :span="24">
            <el-form-item>
              <el-select
                v-model="userScopingValue"
                placeholder="Select User Scoping"
                filterable
                :disabled="!showDataSharingOptions"
                clearable
                :loading="rolesLoading[selectedApp]"
                class="fc-input-full-border2 width100"
              >
                <el-option
                  v-for="userScoping in userScopingList"
                  :key="userScoping.linkName"
                  :label="userScoping.scopeName"
                  :value="userScoping.id"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </template>
      <template v-if="showPermissionSetOption">
        <el-row class="fc-dialog-center-container">
          <el-col :span="24">
            <div>{{ 'Permission Set' }}</div>
          </el-col>
        </el-row>
        <el-row class="mT10">
          <el-col :span="24">
            <el-form-item>
              <el-select
                v-model="userPermissionSets"
                placeholder="Select Permission Set"
                multiple
                collapse-tags
                :disabled="!showDataSharingOptions"
                filterable
                clearable
                :loading="rolesLoading[selectedApp]"
                class="fc-input-full-border2 width100"
              >
                <el-option
                  v-for="permissionSet in permissionSet"
                  :key="permissionSet.linkName"
                  :label="permissionSet.displayName"
                  :value="permissionSet.id"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </template>
    </el-form>

    <div class="modal-dialog-footer">
      <el-button @click="closeDialog" class="modal-btn-cancel">
        {{ $t('common._common.cancel') }}
      </el-button>
      <el-button
        :loading="saving"
        type="primary"
        class="modal-btn-save"
        @click="save"
      >
        {{ saving ? $t('common._common._saving') : $t('common._common._save') }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['appLinkNames', 'user', 'onSave'],

  data() {
    return {
      rolesLoading: {
        vendor: false,
        tenant: false,
        client: false,
        service: false,
      },
      linkNameVsDisplayName: {
        vendor: 'Vendor Portal Access',
        tenant: 'Tenant Portal Access',
        client: 'Client Portal Access',
        service: 'Occupant Portal Access',
        employee: 'Employee Portal Access',
      },
      linkNameVsDataKey: {
        vendor: 'isVendorPortalAccess',
        tenant: 'isTenantPortalAccess',
        client: 'isClientPortalAccess',
        service: 'isOccupantPortalAccess',
        employee: 'employeePortalAccess',
      },
      rolesList: {},
      rolesMap: {},
      userObj: {},
      rules: {},
      saving: false,
      permissionSet: [],
      userPermissionSets: [],
      userScopingList: [],
      userScopingValue: null,
      selectedApp: null,
    }
  },

  created() {
    this.init()
  },

  computed: {
    showUserScopingOption() {
      let { $helpers } = this
      return (
        $helpers.isLicenseEnabled('NEW_SETUP') &&
        $helpers.isLicenseEnabled('PEOPLE_USER_SCOPING')
      )
    },
    showPermissionSetOption() {
      let { $helpers } = this
      return (
        $helpers.isLicenseEnabled('NEW_SETUP') &&
        $helpers.isLicenseEnabled('PERMISSION_SET')
      )
    },
    showDataSharingOptions() {
      let { userObj, linkNameVsDataKey, appLinkNames } = this
      let showOption = false
      if (!isEmpty(appLinkNames)) {
        appLinkNames.forEach(linkName => {
          showOption = showOption || userObj[linkNameVsDataKey[linkName]]
        })
      }
      return showOption
    },
  },
  methods: {
    init() {
      let { linkNameVsDataKey, user } = this

      this.userObj = { ...user }
      let { scopingId, permissionSets } = user || {}
      this.userPermissionSets = permissionSets
      this.userScopingValue = scopingId
      this.appLinkNames.forEach(linkName => {
        let { rolesMap } = user
        let dataKey = linkNameVsDataKey[linkName]

        this.$set(this.userObj, dataKey, user[dataKey])
        this.rolesMap = rolesMap || {}
        this.loadRoles(linkName)
        this.loadPermissionSets()
        this.loadUserScopingList()
        let rule = {
          validator: function(rule, value, callback) {
            let { userObj, appLinkNames, rolesMap, linkNameVsDataKey } = this
            let portalAccess = userObj[linkNameVsDataKey[linkName]]

            if (
              appLinkNames.includes(linkName) &&
              portalAccess &&
              !rolesMap[linkName]
            ) {
              callback(new Error('Please select role'))
            } else callback()
          }.bind(this),
          trigger: 'change',
        }
        this.$set(this.rules, linkName, rule)
      })
    },
    async loadPermissionSets() {
      let { error, data } = await API.get('/v1/setup/permissionSet/userList', {
        page: 1,
        perPage: 5000,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occcured')
      } else {
        let { result } = data || {}
        let { permissionSet } = result || []
        this.permissionSet = permissionSet
        let { userObj } = this
        let { permissionSets } = userObj || {}
        this.userPermissionSets = permissionSets || []
      }
    },
    async loadUserScopingList() {
      let { error, data } = await API.get('/v1/setup/userScoping/userList', {
        page: 1,
        perPage: 5000,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occcured')
      } else {
        let { result } = data || {}
        let { userScopingList } = result || []
        this.userScopingList = userScopingList
        let { userObj } = this
        let { scopingId } = userObj || {}
        this.userScopingValue = scopingId
      }
    },
    async showRoleSelection(linkName) {
      let { userObj, linkNameVsDataKey } = this
      this.selectedApp = linkName
      if (userObj[linkNameVsDataKey[linkName]]) {
        this.$set(this.rolesMap, linkName, null)
        await this.loadRoles(linkName)
      }
    },
    async loadRoles(linkName) {
      if (!isEmpty(this.rolesList[linkName])) return

      this.rolesLoading[linkName] = true
      let filters = JSON.stringify({ appLinkNames: [linkName] })

      let { error, data } = await API.get('/v3/picklist/role', { filters })

      if (error) {
        this.$message.error(error.message || 'Error Occcured')
      } else {
        let { pickList } = data || {}
        this.rolesList[linkName] = pickList || []
      }
      this.rolesLoading[linkName] = false
    },

    closeDialog() {
      this.$emit('onClose')
    },

    save() {
      this.$refs['portalAccessForm'].validate(valid => {
        if (!valid) return false

        let { rolesMap, userObj } = this
        let userRolesMap = userObj.rolesMap || {}
        if (this.showPermissionSetOption) {
          userObj.permissionSets = this.userPermissionSets
        }
        if (this.showUserScopingOption) {
          userObj.scopingId = this.userScopingValue
        }
        this.saving = true
        this.onSave({
          ...userObj,
          rolesMap: { ...userRolesMap, ...rolesMap },
        })
          .then(() => this.closeDialog())
          .catch(() => {})
          .finally(() => (this.saving = false))
      })
    },
  },
}
</script>
