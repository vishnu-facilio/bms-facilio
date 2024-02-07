<template>
  <div class="change-password-container">
    <div class="fc-form">
      <el-form
        :model="changepassword"
        :rules="rules"
        :label-position="'top'"
        class="change-pass-form"
        ref="ruleForm"
      >
        <div>
          <div>
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
          <div class="d-flex mT30">
            <el-button
              @click="changePassword"
              :loading="saving"
              class="btn change-el-btn mR20"
            >
              {{ 'Change' }}
            </el-button>
            <div class="fc-ep-cancel-btn pointer" @click="cancelPassword">
              {{ 'Cancel' }}
            </div>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>
<script>
import ChangePassword from 'src/PortalTenant/profile/ChangePassword.vue'

export default {
  extends: ChangePassword,
  methods: {
    cancelPassword() {
      this.changepassword.oldpassword = null
      this.changepassword.newpassword = null
      this.changepassword.confirmpassword = null
    },
  },
}
</script>
<style scoped>
.change-password-container {
  background-color: #ffffff;
  height: inherit;
  width: 600px;
  padding: 20px;
  display: flex;
  margin: auto;
  border-radius: 8px;
}
.change-el-btn {
  height: 32px !important;
  width: 100px;
  padding: 8px 24px;
  border-radius: 4px;
  background-color: #0053cc;
  line-height: 1;
  display: inline-block;
  border: none !important;
  font-size: 12px;
  letter-spacing: 0.7px !important;
  font-weight: 500;
  cursor: pointer !important;
  color: #ffffff;
}
.fc-ep-cancel-btn {
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: 32px;
  letter-spacing: normal;
  color: #0053cc;
}
.fc-ep-cancel-btn:hover {
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: 32px;
  letter-spacing: normal;
  color: #0053cc;
  text-decoration: underline;
}
.fc-form {
  overflow-y: scroll;
  padding: 5px;
}
</style>
<style>
.change-pass-form .el-form-item__label {
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #324056;
  margin-bottom: 10px;
}
.change-pass-form .el-form-item__content {
  width: 500px;
}
.change-pass-form .el-form-item {
  margin-bottom: 25px !important;
}
</style>
