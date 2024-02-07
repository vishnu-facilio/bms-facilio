<template>
  <div class="fc-form-container change-password-container">
    <div class="fc-form">
      <el-form
        :model="changepassword"
        :rules="rules"
        :label-position="'top'"
        ref="ruleForm"
      >
        <div class="row mT10">
          <div class="col-lg-4 col-md-4">
            <el-form-item
              prop="oldpassword"
              :label="$t('setup.changepassword.current_pw')"
            >
              <el-input
                v-model="changepassword.oldpassword"
                :placeholder="$t('setup.changepassword.enter_your_pw')"
                type="password"
                class="fc-input-full-border2"
                autocomplete="new-password"
              ></el-input>
            </el-form-item>
            <el-form-item
              prop="newpassword"
              :label="$t('setup.changepassword.new_pw')"
            >
              <el-input
                v-model="changepassword.newpassword"
                :placeholder="$t('setup.changepassword.enter_your_pw')"
                type="password"
                class="fc-input-full-border2"
                autocomplete="new-password"
              ></el-input>
            </el-form-item>
            <el-form-item
              prop="confirmpassword"
              :label="$t('setup.changepassword.confirm_pw')"
            >
              <el-input
                v-model="changepassword.confirmpassword"
                :placeholder="$t('setup.changepassword.enter_your_pw')"
                type="password"
                class="fc-input-full-border2"
                autocomplete="new-password"
              ></el-input>
            </el-form-item>
          </div>
          <div class="col-lg-12 col-md-12">
            <el-button
              @click="changePassword"
              :loading="saving"
              class="btn btn--primary setup-el-btn pL60 pR60 mT20"
            >
              {{ $t('setup.changepassword.change_pw') }}
            </el-button>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>
<script>
import ChangePassword from 'pages/personalsettings/ChangePassword'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
export default {
  extends: ChangePassword,
  methods: {
    async changePassword() {
      let {
        changepassword: { oldpassword, newpassword, confirmpassword },
      } = this
      this.$refs['ruleForm'].validate(valid => {
        if (!valid) {
          return false
        }
      })
      if (isEmpty(oldpassword)) {
        this.$message.error('Please enter your current password')
        return
      }
      if (isEmpty(newpassword)) {
        this.$message.error('Please enter new password')
        return
      }
      if (isEmpty(confirmpassword)) {
        this.$message.error('Please enter confirm password')
        return
      }
      if (newpassword !== confirmpassword) {
        this.$message.error('New password does not match')
        return
      }

      this.saving = true
      let { data, error } = await API.post('/integ/changepassword', {
        password: oldpassword,
        rawPassword: newpassword,
        emailaddress: this.$account.user.email,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
        this.reset()
      } else if (
        data.jsonresponse &&
        data.jsonresponse.status === 'failure' &&
        data.jsonresponse.messages
      ) {
        this.$message.error(data.jsonresponse.messages)
      } else {
        this.$message.success('Password updated sucessfully')
        this.reset()
      }
      this.saving = false
    },
  },
}
</script>
<style>
.change-password-container {
  background-color: #ffffff;
  padding: 20px;
  height: calc(100vh - 90px);
}
</style>
