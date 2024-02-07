<template>
  <div class="height100 overflow-hidden">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('setup.setup.security_policy') }}
        </div>
        <div class="heading-description">
          {{ $t('setup.list.list_security') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="openSecurityCreation()"
        >
          {{ $t('setup.setup.security_policy') }}</el-button
        >
      </div>
    </div>
    <!-- list -->
    <div class="container-scroll clearboth">
      <div class="width100 setting-Rlayout mT30">
        <table class="setting-list-view-table">
          <thead>
            <tr>
              <th class="setting-table-th setting-th-text">
                {{ $t('setup.listTable.security_name') }}
              </th>
              <th class="setting-table-th setting-th-text">
                {{ $t('setup.listTable.configurations') }}
              </th>
              <th></th>
            </tr>
          </thead>
          <tbody v-if="loading">
            <tr>
              <td colspan="100%" class="text-center">
                <spinner :show="loading" size="80"></spinner>
              </td>
            </tr>
          </tbody>
          <tbody v-if="$validation.isEmpty(securityPolicyList) && !loading">
            <tr>
              <td
                colspan="100%"
                class="text-center height50vh width100 white-bg"
              >
                <inline-svg
                  src="svgs/emptystate/security"
                  iconClass="icon text-center icon-xxxlg"
                ></inline-svg>
                <div class="pT10 bold fc-black-16">
                  {{ $t('setup.empty.empty_security') }}
                </div>
              </td>
            </tr>
          </tbody>
          <tbody v-if="!loading && !$validation.isEmpty(securityPolicyList)">
            <tr
              v-for="(security, index) in securityPolicyList"
              :key="index"
              class="visibility-visible-actions"
            >
              <td>
                {{ security.name }}
              </td>
              <td>
                <div
                  v-if="security.isDefault === true"
                  class="fc-pink f14 text-left bold pB10"
                >
                  {{ $t('common._common.default') }}
                </div>
                <div class="flex-middle">
                  <el-tag
                    type="success"
                    class="mR10 fc-security-tag-pwd-policy"
                    v-if="security.isPwdPolicyEnabled === true"
                    >{{ $t('setup.security.pwd_policy') }}</el-tag
                  >
                  <el-tag
                    type="success"
                    class="mR10 fc-security-tag-mfa"
                    v-if="security.isTOTPEnabled === true"
                    >{{ $t('setup.security.mfa_short') }}</el-tag
                  >
                  <el-tag
                    type="success"
                    class="mR10 fc-security-tag-websession"
                    v-if="security.isWebSessManagementEnabled === true"
                    >{{ $t('setup.security.web_session') }}</el-tag
                  >
                </div>
              </td>
              <td>
                <i
                  class="el-icon-edit edit-icon visibility-hide-actions pR10"
                  data-arrow="true"
                  :title="$t('setup.security.edit_policy')"
                  v-tippy
                  @click="editSecuritypolicy(security)"
                ></i>
                <i
                  class="el-icon-delete visibility-hide-actions"
                  data-arrow="true"
                  :title="$t('setup.security.delete_policy')"
                  v-tippy
                  @click="deletePolicy(security)"
                  v-if="security.isDefault === false"
                ></i>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <securtiyForm
      v-if="showSecurityForm"
      @onClose="listReload"
      :isNew="isNew"
      :selectedData="selectedPolicy"
    >
    </securtiyForm>
  </div>
</template>
<script>
import securtiyForm from 'src/pages/setup/securitypolicy/SecurityPolicyCreateUpdate'
import { API } from '@facilio/api'
export default {
  data() {
    return {
      showSecurityForm: false,
      isNew: false,
      securityPolicyList: [],
      loading: false,
      selectedPolicy: null,
    }
  },
  components: {
    securtiyForm,
  },
  title() {
    return 'Security Policy'
  },
  created() {
    this.fetchSecurityPolicy()
  },
  methods: {
    async openSecurityCreation() {
      this.showSecurityForm = true
      this.isNew = true
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
    editSecuritypolicy(security) {
      this.isNew = false
      this.showSecurityForm = true
      this.selectedPolicy = security
    },
    listReload() {
      this.showSecurityForm = false
      this.fetchSecurityPolicy()
    },
    deletePolicy(selectedPolicy) {
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_security_policy'),
          message: this.$t(
            'common.header.are_you_sure_you_want_to_delete_this_security_policy'
          ),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(async value => {
          if (!value) return

          let { error } = await API.post('/v2/deleteSecurityPolicy', {
            id: selectedPolicy.id,
          })

          if (error) {
            this.$message.error(
              error.message ||
                this.$t('common._common.security_policy_deletion_failed')
            )
          } else {
            this.$message.success(
              this.$t('common._common.security_policy_deleted_successfully')
            )
            this.fetchSecurityPolicy()
          }
        })
    },
  },
}
</script>
<style scoped lang="scss">
.fc-security-tag {
  color: #fff !important;
  background: #3478f6 !important;
  border: 1px solid #3478f6 !important;
  font-weight: 500 !important;
  height: 25px;
  line-height: 22px;
  font-size: 11px;
  padding-left: 15px;
  padding-right: 15px;
  border-radius: 3px;
}

.fc-security-tag-pwd-policy {
  font-weight: 500 !important;
  height: 25px;
  line-height: 22px;
  padding-left: 15px;
  padding-right: 15px;
  border-radius: 3px;
  color: #d78d2d !important;
  background: #fffaf3 !important;
  border: 1px solid #d78d2d !important;
  text-transform: uppercase;
  font-size: 10px;
}

.fc-security-tag-mfa {
  font-weight: 500 !important;
  height: 25px;
  line-height: 22px;
  padding-left: 15px;
  padding-right: 15px;
  border-radius: 3px;
  color: #a785f2 !important;
  background: #fcfaff !important;
  border: 1px solid #a785f2 !important;
  text-transform: uppercase;
  font-size: 10px;
}

.fc-security-tag-websession {
  font-weight: 500 !important;
  height: 25px;
  line-height: 22px;
  padding-left: 15px;
  padding-right: 15px;
  border-radius: 3px;
  color: #3ab2c2 !important;
  background: #f7feff !important;
  border: 1px solid #3ab2c2 !important;
  text-transform: uppercase;
  font-size: 10px;
}
</style>
