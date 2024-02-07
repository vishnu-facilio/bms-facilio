<template>
  <div class="d-flex flex-direction-row p30 display-flex-between-space">
    <div class="d-flex flex-direction-column" style="flex-grow: 1;">
      <div>
        <InlineSvg
          src="svgs/integrations/wattsense"
          iconClass="icon text-center"
          iconStyle="width: 200px; height: 40px; margin-left: -8px;"
        ></InlineSvg>
        <div
          v-if="!$validation.isEmpty(accountList)"
          class="fc-badge text-uppercase inline vertical-top mL15 mT10"
        >
          Configured
        </div>
      </div>
      <p class="desc mB0">
        Wattsense is an on-demand, non-intrusive building connectivity service.
      </p>
      <div
        v-if="!$validation.isEmpty(accountList)"
        class="d-flex flex-direction-column mT25"
      >
        <div class="f13 fc-blue-label mT10 text-capitalize">
          Configured Accounts
        </div>
        <table class="wattsense-table mT20 width100">
          <tbody>
            <tr
              v-for="(account, index) in accountList"
              :key="index"
              class="tablerow-edit-delete-icon"
            >
              <td class="f14">{{ account.userName }}</td>
              <td class="f13">({{ account.clientId }})</td>
              <td>
                <div class="hover-actions-view fR">
                  <i
                    class="el-icon-delete fc-delete pointer"
                    @click="deleteAccount(account)"
                    data-arrow="true"
                  ></i>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div class="mT5">
      <el-button @click="addAccount" class="fc-wo-fill-btn"
        >Add Account</el-button
      >
    </div>

    <!-- Configuration Popup -->
    <f-dialog
      :title="dialogTitle"
      @save="isCreateMode ? configure() : remove()"
      @close="resetValues()"
      :visible.sync="canShowPopup"
      :loading="isSaving"
      :stayOnSave="true"
      :confirmTitle="isDeleteMode ? 'Remove' : null"
      :loadingTitle="isDeleteMode ? 'Remove' : null"
      width="30%"
      class="agents-dialog"
    >
      <el-form class :model="activeAccount">
        <el-row :gutter="20">
          <el-col :span="24">
            <div v-if="isDeleteMode" style="word-break: break-word;">
              To remove the integration enter the password for the account
              <span class="bold">{{ activeAccount.userName }}</span
              >:
            </div>
            <template v-else>
              <p class="fc-input-label-txt pB10">Username</p>
              <el-input
                v-model="activeAccount.userName"
                placeholder="Enter your username"
                class="fc-input-full-border-select2"
              ></el-input>
            </template>
          </el-col>
        </el-row>
        <el-row :gutter="20" class="mT20 mB25">
          <el-col :span="24">
            <p v-if="!isDeleteMode" class="fc-input-label-txt pB10">Password</p>
            <el-input
              v-model="activeAccount.password"
              type="password"
              placeholder="Enter your password"
              class="fc-input-full-border-select2"
              autocomplete="new-password"
            ></el-input>
          </el-col>
        </el-row>
      </el-form>
    </f-dialog>
    <!-- Configuration Popup -->
  </div>
</template>
<script>
import FDialog from '@/FDialogNew'
import { isEmpty } from '@facilio/utils/validation'

export default {
  components: { FDialog },
  data() {
    return {
      canShowPopup: false,
      isSaving: false,
      accountList: null,
      activeAccount: {
        userName: '',
        password: '',
        clientId: null,
      },
      isCreateMode: false,
      isDeleteMode: false,
    }
  },
  mounted() {
    this.loadAccountList()
  },
  computed: {
    dialogTitle() {
      return this.isCreateMode
        ? 'Configure Wattsense Account'
        : 'Remove Wattsense Account'
    },
  },
  methods: {
    loadAccountList() {
      this.$http(`/v2/setup/agent/integration/wattsense/list`)
        .then(response => {
          if (
            response.data.responseCode === 0 &&
            !isEmpty(response.data.result.wattsenseList)
          ) {
            this.accountList = response.data.result.wattsenseList.filter(
              account => account.integrationStatus === 200
            )
          } else {
            this.accountList = null
          }
        })
        .catch(() => {
          this.accountList = null
        })
    },

    addAccount() {
      this.resetValues()

      this.isCreateMode = true
      this.canShowPopup = true
    },

    configure() {
      this.isSaving = true
      let data = {
        userName: this.activeAccount.userName,
        password: this.activeAccount.password,
      }
      this.$http
        .post(`/v2/setup/agent/integration/wattsense/add`, data)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success('Account integrated successfully')
            this.loadAccountList()
            this.resetValues()
          } else {
            this.isSaving = false
            this.$message({
              showClose: true,
              message: 'Error occurred while integrating account',
              type: 'error',
            })
          }
        })
        .catch(error => {
          error &&
            this.$message({
              showClose: true,
              message: error.message,
              type: 'error',
            })
          this.isSaving = false
        })
    },

    deleteAccount(account) {
      this.resetValues()

      this.isDeleteMode = true
      this.canShowPopup = true

      this.$set(this, 'activeAccount', {
        userName: account.userName,
        password: '',
        clientId: account.clientId,
      })
    },

    remove() {
      this.isSaving = true
      this.$http
        .post(
          `/v2/setup/agent/integration/wattsense/delete`,
          this.activeAccount
        )
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success('Account removed successfully')
            this.loadAccountList()
            this.resetValues()
          } else {
            this.$message({
              showClose: true,
              message: 'Error while deleting account',
              type: 'error',
            })
          }
          this.isSaving = false
        })
        .catch(error => {
          this.$message({
            showClose: true,
            message: error.message || 'Error while deleting account',
            type: 'error',
          })
          this.isSaving = false
        })
    },

    resetValues() {
      this.activeAccount = { userName: '', password: '', clientId: null }
      this.canShowPopup = false
      this.isCreateMode = false
      this.isDeleteMode = false
      this.isSaving = false
    },
  },
}
</script>
<style lang="scss" scoped>
.desc {
  color: #666666;
  margin-top: 4px;
}
.wattsense-table {
  border-top: 1px solid #f3f6f9;

  tr {
    display: table-row;
    vertical-align: inherit;
    cursor: pointer;
    background-color: #ffffff;
    z-index: 1;
    &:hover {
      background-color: #f9f9f9;
    }
  }
  td {
    border-top: none;
    border-left: none;
    border-right: none;
    color: #333;
    font-size: 14px;
    border-collapse: separate;
    padding: 15px 30px;
    letter-spacing: 0.6px;
    font-weight: 400;
    border-bottom: 1px solid #f3f6f9;
  }
}
</style>
