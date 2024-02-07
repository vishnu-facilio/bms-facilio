<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog45 setup-dialog show-right-dialog"
    :before-close="cancel"
    style="z-index: 999999"
  >
    <el-form
      :model="edituser"
      :rules="rules"
      :label-position="'top'"
      ref="ruleForm"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{
              isNew
                ? $t('setup.users_management.new_user_details')
                : $t('setup.users_management.edit_user_details')
            }}
          </div>
        </div>
      </div>

      <div class="new-body-modal">
        <div class="fc-sub-title-container" style="margin-top: 0 !important;">
          <div class="fc-modal-sub-title">
            {{ $t('setup.setup_profile.general_info') }}
          </div>

          <div style="float:right" v-if="isNew">
            <el-switch v-model.number="sendInvitation"></el-switch>
            <span class="fc-input-label-txt pB10">
              {{ $t('setup.users_management.send_invitation') }}
            </span>
          </div>
        </div>

        <el-row :gutter="20">
          <el-col :span="24">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.setup_profile.full_name') }}
              <span class="mandatory-field-color">*</span>
            </p>

            <el-form-item prop="name">
              <el-input
                :autofocus="true"
                v-model="edituser.name"
                :placeholder="$t('setup.setup_profile.enter_the_name')"
                :disabled="disableName"
                class="fc-input-full-border2"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <p class="fc-input-label-txt fc-input-space-vertical pB10">
              {{ $t('setup.setup_profile.email') }}
              <span class="mandatory-field-color">*</span>
            </p>

            <el-form-item prop="email">
              <el-input
                v-model="edituser.email"
                type="text"
                autocomplete="off"
                :disabled="!isNew || disableEmail"
                :placeholder="$t('setup.users_management.enter_email')"
                class="fc-input-full-border2"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row class="mT20" :gutter="20">
          <el-col :span="12">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.setup_profile.timezone') }}
            </p>

            <el-form-item>
              <el-select
                class="fc-input-full-border-select2 width100"
                :placeholder="$t('setup.setup_profile.timezone')"
                v-model="edituser.timezone"
                filterable
                clearable
              >
                <el-option
                  v-for="(timezone, index) in timezoneList"
                  :key="index"
                  :label="timezone.label"
                  :value="timezone.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.setup_profile.language') }}
            </p>

            <el-form-item>
              <el-select
                class="fc-input-full-border-select2 width100"
                :placeholder="$t('setup.setup_profile.language')"
                v-model="edituser.language"
                :options="languageList"
                filterable
                clearable
              >
                <el-option
                  v-for="language in languageList"
                  :key="language.value"
                  :label="language.label"
                  :value="language.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <div v-if="canShowShifts" class="setup-input-block mT20 ">
          <p class="fc-input-label-txt pB10">
            {{ $t('setup.users_management.shift_hours') }}
          </p>

          <el-form-item>
            <el-select
              class="fc-input-full-border-select2 width100"
              v-model="edituser.shiftId"
              filterable
              clearable
            >
              <el-option
                v-for="shift in shifts"
                :key="shift.id"
                :label="shift.name"
                :value="shift.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>

        <div class="fc-sub-title-container">
          <div class="fc-modal-sub-title">
            {{ $t('setup.users_management.roles_accessibility') }}
          </div>
        </div>

        <el-row class="resource-row" :gutter="20">
          <el-col :span="12">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.setup.roles') }}
              <span class="mandatory-field-color">*</span>
            </p>

            <el-form-item prop="roleId">
              <el-select
                v-model="edituser.roleId"
                class="fc-input-full-border-select2 width100"
                filterable
                clearable
              >
                <el-option
                  v-for="role in rolesList"
                  :key="role.roleId"
                  :label="role.name"
                  :value="role.roleId"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.setup.security_policy') }}
            </p>
            <el-form-item>
              <el-select
                v-model="edituser.securityPolicyId"
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
        <el-row :gutter="20" v-if="isNew">
          <el-col :span="12" class="f-element resource-list">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.users_management.accessible_sites') }}
            </p>
            <Lookup
              v-model="edituser.site"
              :field="fields.site"
              :hideDropDown="true"
              @recordSelected="setSelectedValue"
              @showLookupWizard="showLookupWizardSite"
              :disabled="!isNew"
            >
            </Lookup>
          </el-col>
          <el-col :span="12" class="f-element resource-list">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.users_management.accessible_buildings') }}
            </p>

            <FLookupField
              :model.sync="edituser.building"
              :field="fields.building"
              :hideDropDown="true"
              @recordSelected="setSelectedValue"
              @showLookupWizard="showLookupWizardBuilding"
              :disabled="!isNew"
            ></FLookupField>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12" class="mT20">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.users_management.accessible_teams') }}
            </p>

            <el-form-item>
              <el-select
                v-model="edituser.groups"
                multiple
                collapse-tags
                filterable
                :disabled="$validation.isEmpty(groups)"
                class="fc-input-full-border-select2 el-select-block width100"
              >
                <el-option
                  v-for="(group, index) in groups"
                  :key="index"
                  :label="group.name"
                  :value="group.groupId"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <div v-if="canShowScope">
          <el-row v-if="applications.linkName !== 'newapp'">
            <el-col :span="12" class="mT20">
              <p class="fc-input-label-txt pB10">
                {{ $t('setup.userScoping.user_scoping') }}
              </p>
              <el-form-item>
                <el-select
                  v-model="edituser.scopingId"
                  class="fc-input-full-border-select2 el-select-block width100"
                  clearable
                >
                  <el-option
                    v-for="(scope, scopeIdx) in userScopeList"
                    :key="scopeIdx"
                    :label="scope.scopeName"
                    :value="scope.id"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="fc-sub-title-container pT20">
          <div class="fc-modal-sub-title">
            {{ $t('setup.setup_profile.contact_info') }}
          </div>
        </div>

        <el-row :gutter="20">
          <el-col :span="12">
            <label for="current_password" class="fc-input-label-txt">
              {{ $t('setup.setup_profile.phone') }}
            </label>

            <el-form-item prop="phone">
              <el-input
                class="required fc-input-full-border2 mT10 width100"
                v-model="edituser.phone"
                type="text"
                autocomplete="off"
                :placeholder="$t('setup.setup_profile.enter_phone')"
              ></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <label for="password" class="fc-input-label-txt">
              {{ $t('setup.setup_profile.mobile') }}
            </label>

            <el-form-item prop="mobile">
              <el-input
                class="required fc-input-full-border2 mT10"
                v-model="edituser.mobile"
                type="text"
                autocomplete="off"
                :placeholder="$t('setup.setup_profile.enter_mobile')"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="cancel()" class="modal-btn-cancel">
          {{ $t('setup.users_management.cancel') }}
        </el-button>
        <el-button type="primary" class="modal-btn-save" @click="saveUser">
          {{ saveBtn }}
        </el-button>
      </div>

      <FLookupFieldWizard
        v-if="canShowLookupWizardSite"
        :canShowLookupWizard.sync="canShowLookupWizardSite"
        :selectedLookupField="selectedLookupField"
        :withReadings="true"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
      <FLookupFieldWizard
        v-if="canShowLookupWizardBuilding"
        :canShowLookupWizard.sync="canShowLookupWizardBuilding"
        :selectedLookupField="selectedLookupField"
        :withReadings="true"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
    </el-form>
  </el-dialog>
</template>
<script>
import timezones from 'util/data/timezones'
import countries from 'util/data/countries'
import Constants from 'util/constant'
import { mapState } from 'vuex'
import FLookupField from '@/forms/FLookupField'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'
import { Lookup } from '@facilio/ui/forms'
import { isValidPhoneNumber } from 'src/util/helpers'
const fields = {
  site: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'site',
    field: {
      lookupModule: {
        name: 'site',
        displayName: 'Sites',
      },
    },

    multiple: true,
    additionalParams: {
      orderBy: 'spaceType',
      orderType: 'asc',
    },
  },
  building: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'building',
    field: {
      lookupModule: {
        name: 'building',
        displayName: 'Buildings',
      },
    },
    multiple: true,
  },
}
const peopleLookup = {
  isDataLoading: false,
  options: [],
  lookupModuleName: 'people',
  field: {
    lookupModule: {
      name: 'people',
      displayName: 'Peoples',
    },
  },
  forceFetchAlways: true,
  filters: {},
  multiple: false,
  isDisabled: false,
}
export default {
  props: ['user', 'isNew', 'appId', 'applications', 'fromPeople'],
  data() {
    return {
      sendInvitation: true,
      saving: false,
      canShowLookupWizardSite: false,
      canShowLookupWizardBuilding: false,
      selectedLookupField: null,
      edituser: { site: [], building: [], language: 'en' },
      timezoneList: timezones,
      countryList: countries,
      languageList: Constants.languages,
      securityPolicyList: [],
      userScopeList: [],
      fields,
      rolesList: [],
      peopleLookup,
      rules: {
        name: {
          required: true,
          message: this.$t('setup.users_management.please_enter_name'),
          trigger: 'blur',
        },
        email: {
          required: true,
          validator: (rule, email, callback) => {
            if (isEmpty(email)) {
              callback(
                new Error(
                  this.$t(
                    'setup.users_management.please_enter_valid_email_only'
                  )
                )
              )
            } else {
              email = email.trim()
              let emailRegx = /^(([^<>()\\[\]\\.,;:\s@"]+(\.[^<>()\\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
              let mobileRegx = /^[\\+\d]?(?:[\d-.\s()]*)$/
              if (
                emailRegx.test(email) === false &&
                mobileRegx.test(email) === false &&
                this.isNew
              ) {
                callback(
                  new Error(
                    this.$t('setup.users_management.please_enter_valid_email')
                  )
                )
              } else {
                callback()
              }
            }
          },
          trigger: 'blur',
        },
        roleId: {
          required: true,
          validator: (rule, roleId, callback) => {
            if (isEmpty(roleId)) {
              callback(
                new Error(
                  this.$t('setup.users_management.please_specify_roles')
                )
              )
            } else {
              callback()
            }
          },
          trigger: 'change',
        },
        mobile: [
          {
            validator: function(rule, mobile, callback) {
              if (!isEmpty(mobile) && !isValidPhoneNumber(mobile)) {
                callback(new Error('Please enter valid mobile number'))
              } else {
                callback()
              }
            },
            trigger: 'blur',
          },
        ],
        phone: [
          {
            validator: function(rule, phone, callback) {
              if (!isEmpty(phone) && !isValidPhoneNumber(phone)) {
                callback(new Error('Please enter valid phone number'))
              } else {
                callback()
              }
            },
            trigger: 'blur',
          },
        ],
      },
    }
  },
  components: {
    Lookup,
    FLookupField,
    FLookupFieldWizard,
  },
  async created() {
    this.$store.dispatch('loadShifts')
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
    await this.loadRolesForApp()
    this.initUser()
    this.fetchSecurityPolicy()
    this.userScopesListData()
  },
  computed: {
    ...mapState({
      groups: state => state.groups,
      shifts: state => state.shifts,
    }),
    canShowShifts() {
      return !isEmpty(this.shifts) && this.$helpers.isLicenseEnabled('PEOPLE')
    },
    saveBtn() {
      let { isNew, saving } = this

      if (isNew) {
        return saving
          ? this.$t('common._common._saving')
          : this.$t('setup.users_management.add')
      } else {
        return saving ? 'Updating' : 'Update'
      }
    },
    canShowScope() {
      let { query } = this.$route
      let { scope: showAdd } = query || {}
      return process.env.NODE_ENV === 'development' || showAdd
    },
    disableEmail() {
      let { fromPeople, user } = this
      let { email } = user || {}
      return fromPeople && !isEmpty(email)
    },
    disableName() {
      let { fromPeople, user } = this
      let { name } = user || {}
      return fromPeople && !isEmpty(name)
    },
  },
  methods: {
    async loadRolesForApp() {
      let { appId } = this

      this.loading = true
      let { error, data } = await API.get('/setup/roles', { appId })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.rolesList = (data.roles || []).filter(
          role => role.name !== 'Super Administrator'
        )
      }
      this.loading = false
    },
    showLookupWizardSite(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizardSite', canShow)
    },
    showLookupWizardBuilding(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizardBuilding', canShow)
    },
    setSelectedValue(selectedValues, field) {
      selectedValues
      field
    },
    setLookupFieldValue({ field }) {
      let name = this.$getProperty(field, 'field.lookupModule.name', null)
      this.edituser[name] = field.selectedItems.map(item => item.value)
      let { options } = this.fields[name]
      let selectedItemsInOptions = options.filter(option =>
        this.edituser[name].includes(option.value)
      )
      let selectedItemIdsInOptions = selectedItemsInOptions.map(
        item => item.value
      )
      let selectedItemsNotInOptions = field.selectedItems.filter(
        item => !selectedItemIdsInOptions.includes(item.value)
      )

      this.$set(this.fields[name], 'options', [
        ...options,
        ...selectedItemsNotInOptions,
      ])
    },

    cancel() {
      this.$emit('close')
    },
    initUser() {
      let { isNew, fromPeople } = this
      if (!isNew || fromPeople) {
        this.edituser = cloneDeep(this.user)
      }
    },
    async fetchSecurityPolicy() {
      this.loading = true
      let { error, data } = await API.get('v2/getAllSecurityPolicies')
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.securityPolicyList = data.securityPolicies || []
      }
      this.loading = false
    },
    saveUser() {
      this.$refs['ruleForm'].validate(valid => {
        if (!valid) return false
        let user = cloneDeep(this.edituser)

        let email = this.$getProperty(user, 'email', '')
        let validatedEmail = email.trim()
        this.$set(user, 'email', validatedEmail)
        if (isEmpty(user.securityPolicyId)) {
          delete user.securityPolicyId
        }
        if (this.isNew) {
          let { edituser } = this
          let { site, building } = edituser || {}
          if (!isEmpty(site)) {
            user.accessibleSpace = [...site]
          }
          if (!isEmpty(building)) {
            user.accessibleSpace = [...building]
          }
          delete user.site
          delete user.building
          let { groups } = user || {}
          if (isEmpty(groups)) {
            user.groups = []
          }
        }

        let params = {
          emailVerificationNeeded: this.sendInvitation,
          appId: this.appId,
          user,
        }
        let url = ''

        if (this.isNew) {
          url = '/setup/adduser'
        } else {
          url = '/setup/updateuser'
        }

        this.saving = true
        API.post(url, params).then(({ error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            let message = this.isNew
              ? this.sendInvitation
                ? this.$t('setup.users_management.invitation_send_success')
                : this.$t('common._common.user_added_successfully')
              : this.$t('setup.users_management.user_details_success')

            this.$message.success(message)
            this.$store.dispatch('loadUsers', true)
            this.$emit('save')
            this.$emit('close')
          }
          this.saving = false
        })
      })
    },
    async userScopesListData(force = true) {
      this.loading = true
      let {
        error,
        data,
      } = await API.get(`v2/scoping/scopingList?appId=${this.appId}`, { force })
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
<style>
.resource-row .el-input__inner {
  min-height: 40px;
}
.resource-list .multi .el-select .el-tag__close.el-icon-close,
.resource-list .multi .el-input__suffix {
  display: none;
}
</style>
<style scoped>
.mandatory-field-color {
  color: #d54141;
}
</style>
