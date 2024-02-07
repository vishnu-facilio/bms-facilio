<template>
  <div class="fc-form-container change-password-container">
    <el-form
      :model="changepassword"
      :rules="rules"
      :label-position="'top'"
      ref="ruleForm"
    >
      <div class="setup-modal-title">
        {{ $t('setup.changepassword.change_pw') }}
      </div>
      <div class="row mT20">
        <div class="col-lg-3 col-md-3">
          <div class="form-input">
            <el-form-item prop="oldpassword">
              <p class="fc-input-label-txt m0">
                {{ $t('setup.changepassword.current_pw') }}
              </p>
              <el-input
                v-model="changepassword.oldpassword"
                :placeholder="$t('setup.changepassword.enter_your_pw')"
                type="password"
                class="fc-input-full-border2"
                show-password
                autocomplete="new-password"
              ></el-input>
            </el-form-item>
          </div>
          <div class="form-input">
            <el-form-item prop="newpassword">
              <p class="fc-input-label-txt m0">
                {{ $t('setup.changepassword.new_pw') }}
              </p>
              <el-input
                v-model="changepassword.newpassword"
                :placeholder="$t('setup.changepassword.enter_your_pw')"
                type="password"
                autocomplete="new-password"
                show-password
                class="fc-input-full-border2"
              ></el-input>
            </el-form-item>
          </div>
          <div class="form-input">
            <el-form-item prop="confirmpassword">
              <p class="fc-input-label-txt m0">
                {{ $t('setup.changepassword.confirm_pw') }}
              </p>
              <el-input
                v-model="changepassword.confirmpassword"
                :placeholder="$t('setup.changepassword.enter_your_pw')"
                type="password"
                show-password
                class="fc-input-full-border2"
              ></el-input>
            </el-form-item>
          </div>
        </div>
        <div class="col-lg-12 col-md-12">
          <el-button
            @click="changePassword"
            :loading="saving"
            class="btn btn--primary setup-el-btn fwBold mT20"
          >
            {{ $t('setup.changepassword.change_pw') }}
          </el-button>
        </div>
      </div>
    </el-form>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
export default {
  title() {
    return 'Change Password'
  },
  data() {
    return {
      changepassword: {
        oldpassword: '',
        newpassword: '',
        confirmpassword: '',
      },
      rules: {
        oldpassword: [
          {
            required: true,
            message: 'Old password is required',
            trigger: 'blur',
          },
        ],
        newpassword: [
          {
            required: true,
            message: 'New password is required',
            trigger: 'blur',
          },
        ],
        confirmpassword: [
          {
            required: true,
            message: 'Confirm your new password',
            trigger: 'blur',
          },
        ],
      },
      saving: false,
    }
  },
  methods: {
    reset() {
      this.changepassword.oldpassword = null
      this.changepassword.newpassword = null
      this.changepassword.confirmpassword = null
    },
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
      } else {
        let jsonResponse = data.jsonresponse
        let message = jsonResponse ? jsonResponse.messages : ''
        let status = jsonResponse ? jsonResponse.status : ''

        if (status === 'success') {
          this.$message.success(message || 'Password changed successfully.')
          this.reset()
        } else {
          this.$message.error(message || 'Error Occured')
        }
      }
      this.saving = false
    },
  },
}
</script>
<style>
.change-password-container {
  padding: 40px;
}
</style>
