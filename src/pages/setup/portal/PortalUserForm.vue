<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <el-form
      :model="userData"
      :label-position="'top'"
      ref="user-form"
      :rules="rules"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{
              isNew ? $t('tenant.announcement.new') : $t('common._common.edit')
            }}
            {{ $t('setup.users_management.users') }}
          </div>
        </div>
      </div>
      <div class="new-body-modal">
        <div class="fc-sub-title-container" style="margin-top: 0 !important;">
          <div class="fc-modal-sub-title">
            {{ $t('setup.setup_profile.user_info') }}
          </div>
          <div style="float:right" v-if="canShowSendInvitationToggle">
            <el-switch v-model.number="sendInvitation"></el-switch>
            <span class="fc-input-label-txt pB10">
              {{ $t('setup.users_management.send_invitation') }}
            </span>
          </div>
        </div>
        <template v-if="isLoading">
          <template v-for="index in [1, 2]">
            <el-row class="mB10" :key="index">
              <el-col :span="24">
                <span class="lines loading-shimmer width50 mB10"></span>

                <span
                  class="lines loading-shimmer width100 mB10 height40"
                ></span>
              </el-col>
            </el-row>
          </template>

          <el-row class="mB10">
            <el-col :span="24">
              <span class="lines loading-shimmer width50 mB10"></span>
            </el-col>
            <el-col :span="24">
              <span class="lines loading-shimmer width50 mB10 height40"></span>
            </el-col>
          </el-row>
        </template>
        <template v-else>
          <el-form-item :label="$t('common._common.name')" prop="name">
            <el-input
              type="text"
              v-model="userData.name"
              class="fc-input-full-border2"
              :placeholder="$t('common._common.enter_name')"
            ></el-input>
          </el-form-item>
          <el-form-item :label="$t('common.header.email')" prop="email">
            <el-input
              v-model="userData.email"
              type="email"
              class="fc-input-full-border2"
              :disabled="!isNew"
              :placeholder="$t('common._common.enter_e_mail')"
            ></el-input>
          </el-form-item>
          <el-form-item :label="$t('common.header.phone')" prop="phone">
            <el-input
              v-model="userData.phone"
              class="fc-input-full-border2"
              type="number"
              :placeholder="$t('common._common.enter_phone')"
            ></el-input>
          </el-form-item>

          <el-form-item
            :label="$t('setup.userScoping.user_scoping')"
            :prop="scopeProp"
            v-if="canShowScope"
          >
            <el-select
              v-model="currentScopeId"
              class="fc-input-full-border-select2 el-select-block width100"
              clearable
            >
              <el-option
                v-for="scope in userScopeList"
                :key="scope.id"
                :label="scope.scopeName"
                :value="scope.id"
              >
              </el-option>
            </el-select>
          </el-form-item>

          <template v-if="formType === 'tenant'">
            <el-form-item :label="$t('common.header.tenant')" prop="tenant">
              <f-lookup-field-wrapper
                v-model="userData.tenant.id"
                :label="userData.tenant.name"
                moduleName="tenantcontact"
                fieldName="tenant"
              ></f-lookup-field-wrapper>
            </el-form-item>
          </template>
          <template v-if="formType === 'vendor'">
            <el-form-item :label="$t('common.products.vendor')" prop="vendor">
              <FLookupFieldWrapper
                v-model="userData.vendor.id"
                :label="userData.vendor.name"
                moduleName="vendorcontact"
                fieldName="vendor"
              ></FLookupFieldWrapper>
            </el-form-item>
          </template>
          <template v-if="formType === 'client'">
            <el-form-item :label="$t('common._common.client')" prop="client">
              <FLookupFieldWrapper
                v-model="userData.client.id"
                :label="userData.client.name"
                moduleName="clientcontact"
                fieldName="client"
              ></FLookupFieldWrapper>
            </el-form-item>
          </template>

          <el-row :gutter="15">
            <el-col :span="12">
              <el-form-item
                :label="$t('common.wo_report.role')"
                :prop="roleProp"
              >
                <el-select
                  v-model="currentAppRoleId"
                  :placeholder="$t('common.products.select_role')"
                  filterable
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="role in rolesList"
                    :key="role.id"
                    :label="role.name"
                    :value="role.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="12" v-if="formType !== 'dev'">
              <el-form-item
                :label="$t('setup.setup_profile.language')"
                prop="language"
              >
                <el-select
                  v-model="userData.language"
                  :placeholder="$t('setup.setup.select_language')"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="lang in languageList"
                    :key="lang.value"
                    :label="lang.label"
                    :value="lang.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="15">
            <el-col :span="12">
              <el-form-item
                :label="$t('setup.setup.security_policy')"
                prop="security_policy"
              >
                <el-select
                  v-model="currentSecurityPolicyId"
                  class="fc-input-full-border-select2 el-select-block width100"
                  clearable
                >
                  <el-option
                    v-for="(security, securityIdx) in securityPolicyList"
                    :key="securityIdx"
                    :label="security.name"
                    :value="security.id"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </template>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">
          {{ $t('common._common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="submitForm()"
          :loading="saving"
          class="modal-btn-save"
        >
          {{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}
        </el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import Constants from 'util/constant'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  props: ['isNew', 'formType', 'user', 'app', 'save'],
  components: { FLookupFieldWrapper },
  data() {
    return {
      sendInvitation: true,
      userScopeList: [],
      userData: {
        name: null,
        email: null,
        phone: null,
      },
      securityPolicyList: [],
      saving: false,
      languageList: Constants.languages,
      rolesList: [],
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_input_user_name'),
          trigger: 'blur',
        },
        email: {
          required: true,
          message: this.$t('common._common.please_input_e_mail'),
          trigger: 'blur',
        },
      },
      isLoading: true,
    }
  },
  async created() {
    this.isLoading = true
    await this.fetchSecurityPolicy()
    await this.loadRolesForApp()
    await this.userScopesListData()
    this.deserialize()
    this.setValidationRules()
    this.isLoading = false
  },
  computed: {
    currentSecurityPolicyId: {
      get() {
        let {
          app: { linkName },
          userData: { securityPolicyMap = {} },
        } = this
        return (securityPolicyMap || {})[linkName] || null
      },
      set(value) {
        let {
          app: { linkName },
        } = this

        let secMap = this.userData.securityPolicyMap
        secMap = {
          ...secMap,
          [linkName]: value,
        }

        this.$set(this.userData, 'securityPolicyMap', secMap)
      },
    },
    currentAppRoleId: {
      get() {
        let {
          app: { linkName },
          userData: { rolesMap = {}, roleId },
          formType,
        } = this

        if (formType === 'workCenter' || formType === 'dev') {
          return roleId || null
        } else {
          return (rolesMap || {})[linkName] || null
        }
      },
      set(value) {
        let {
          app: { linkName },
          formType,
        } = this

        if (formType === 'workCenter' || formType === 'dev') {
          this.userData.roleId = value
        } else {
          this.userData.rolesMap = {
            ...this.userData.rolesMap,
            [linkName]: value,
          }
        }
      },
    },
    currentScopeId: {
      get() {
        let {
          app: { linkName },
          userData: { scopingsMap = {}, roleId },
          formType,
        } = this

        if (formType === 'workCenter') {
          return roleId || null
        } else {
          return (scopingsMap || {})[linkName] || null
        }
      },
      set(value) {
        let {
          app: { linkName },
          formType,
        } = this

        if (formType === 'workCenter') {
          this.userData.scopingId = value
        } else {
          this.userData.scopingsMap = {
            ...this.userData.scopingsMap,
            [linkName]: value,
          }
        }
      },
    },
    roleProp() {
      let { formType } = this
      return formType === 'workCenter' || formType === 'dev'
        ? 'roleId'
        : 'rolesMap'
    },
    scopeProp() {
      let { formType } = this
      return formType === 'workCenter' ? 'scopingId' : 'scopingsMap'
    },
    canShowScope() {
      let { query } = this.$route
      let showAdd = query.hasOwnProperty('scope') && query.scope
      return process.env.NODE_ENV === 'development' || showAdd
    },
    canShowSendInvitationToggle() {
      return this.isNew && this.app.linkName == 'maintenance'
    },
  },
  methods: {
    async fetchSecurityPolicy() {
      let { error, data } = await API.get('v2/getAllSecurityPolicies')
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.securityPolicyList = data.securityPolicies || []
      }
    },
    async loadRolesForApp() {
      let { id: appId } = this.app

      this.loading = true
      let { error, data } = await API.get('/setup/roles', { appId })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.rolesList = data.roles || []
      }
      this.loading = false
    },
    deserialize() {
      let {
        formType,
        user,
        isNew,
        rolesList,
        app,
        userScopeList,
        $account,
      } = this

      if (formType === 'tenant') {
        if (isNew) {
          this.$set(this.userData, 'tenant', { id: null })
        } else {
          this.userData = { ...user }
        }
      } else if (formType === 'vendor') {
        if (isNew) {
          this.$set(this.userData, 'vendor', { id: null })
        } else {
          this.userData = { ...user }
        }
      } else if (formType === 'client') {
        if (isNew) {
          this.$set(this.userData, 'client', { id: null })
        } else {
          this.userData = { ...user }
        }
      } else if (formType === 'workCenter') {
        if (!isNew) {
          let { phone } = user

          this.userData = {
            ...user,
            phone: parseInt(phone),
          }
        }
      } else if (
        formType === 'occupant' ||
        formType === 'dev' ||
        formType === 'employee'
      ) {
        if (!isNew) {
          this.userData = { ...user }
        }
      }

      if (isNew) {
        let [defaultRole] = rolesList
        let { org: { language } = {} } = $account || {}
        let [defaultScoping] = userScopeList

        if (formType === 'workCenter' || formType === 'dev') {
          this.$set(this.userData, 'roleId', defaultRole.id)
          this.$set(this.userData, 'scopingId', defaultScoping.id)
        } else {
          let { linkName } = app
          this.$set(this.userData, 'rolesMap', { [linkName]: defaultRole.id })
          this.$set(this.userData, 'scopingsMap', {
            [linkName]: defaultScoping.id,
          })
        }
        this.$set(this.userData, 'language', language || 'en')
      }
    },
    setValidationRules() {
      let { formType } = this

      if (formType === 'tenant') {
        this.rules.tenant = {
          validator: function(rule, value, callback) {
            if (isEmpty(this.userData.tenant.id)) {
              callback(
                new Error(this.$t('common._common.please_select_tenant'))
              )
            } else callback()
          }.bind(this),
          trigger: 'change',
          required: true,
        }
      } else if (formType === 'vendor') {
        this.rules.vendor = {
          validator: function(rule, value, callback) {
            if (isEmpty(this.userData.vendor.id)) {
              callback(
                new Error(this.$t('common._common.please_select_vendor'))
              )
            } else callback()
          }.bind(this),
          trigger: 'change',
          required: true,
        }
      } else if (formType === 'client') {
        this.rules.client = {
          validator: function(rule, value, callback) {
            if (isEmpty(this.userData.client.id)) {
              callback(
                new Error(this.$t('common._common.please_select_client'))
              )
            } else callback()
          }.bind(this),
          trigger: 'change',
          required: true,
        }
      }

      if (formType === 'workCenter' || formType === 'dev') {
        this.rules.roleId = {
          validator: function(rule, value, callback) {
            let { roleId } = this.userData

            if (isEmpty(roleId)) {
              callback(new Error(this.$t('common._common.please_select_role')))
            } else callback()
          }.bind(this),
          trigger: 'change',
        }
        this.rules.scopingId = {
          validator: function(rule, value, callback) {
            let { scopingId } = this.userData

            if (isEmpty(scopingId)) {
              callback(new Error(this.$t('common._common.please_select_scope')))
            } else callback()
          }.bind(this),
          trigger: 'change',
        }
      } else {
        this.rules.rolesMap = {
          validator: function(rule, value, callback) {
            let { linkName } = this.app
            let { rolesMap = {} } = this.userData

            if (isEmpty(rolesMap[linkName])) {
              callback(new Error(this.$t('common._common.please_select_role')))
            } else callback()
          }.bind(this),
          trigger: 'change',
        }
        this.rules.scopingsMap = {
          validator: function(rule, value, callback) {
            let { linkName } = this.app
            let { scopingsMap = {} } = this.userData

            if (isEmpty(scopingsMap[linkName])) {
              callback(new Error(this.$t('common._common.please_select_scope')))
            } else callback()
          }.bind(this),
          trigger: 'change',
        }
      }
    },
    submitForm() {
      this.$refs['user-form'].validate(valid => {
        if (!valid) return

        let { userData } = this
        this.save(userData, this.sendInvitation)
          .then(() => this.closeDialog())
          .catch(() => {})
          .finally(() => (this.saving = false))
      })
    },
    closeDialog() {
      this.$emit('onClose')
    },
    async userScopesListData(force = true) {
      this.loading = true
      let { id: appId } = this.app
      let {
        error,
        data,
      } = await API.get(`v2/scoping/scopingList?appId=${appId}`, { force })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.userScopeList = data.scopingContextList
      }
      this.loading = false
    },
  },
}
</script>
<style scoped>
.lines {
  height: 15px;
  border-radius: 5px;
}
.height40 {
  height: 40px !important;
}
</style>
