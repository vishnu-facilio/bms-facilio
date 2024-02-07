<template>
  <div class="fc-login-user-bg accept-invite">
    <div class="tcenter col-sm-6 col-sm-offset-3">
      <div
        v-if="invitation.error"
        class="fc-invite-msg-white height100vh flex-middle f20 justify-content-center flex-direction-column z-30"
      >
        <div>
          Invalid link. Please contact your administrator.
        </div>
        <div>
          <img class="fc-logo pT20" src="~assets/facilio-logo-white.svg" />
        </div>
      </div>
      <div
        v-else-if="userMismatchError"
        class="fc-invite-msg-white height100vh flex-middle f20 justify-content-center flex-direction-column z-30"
      >
        <div>
          Kindly login using different browser or
          <router-link
            tag="a"
            class="fc-pink f20 pL3 pR3"
            :to="{ name: 'logout' }"
            >logout
          </router-link>
          the current user and then use the link
        </div>
        <div>
          <img class="fc-logo pT20" src="~assets/facilio-logo-white.svg" />
        </div>
      </div>

      <div v-else-if="loading" class="fc-white-12 pT10 text-center">
        Loading...
      </div>

      <div v-else class="text-center z-30">
        <img class="fc-logo pT20" src="~assets/facilio-logo-white.svg" />
        <form @submit.prevent="acceptInvite" class="fc-invite-form-bg mT20">
          <div class="form-group">
            <div class="fc-invite-black20 text-center">
              Welcome to {{ invitation.orgname }}
            </div>
            <div class="fc-invite-black15">
              Just a few more steps to get started
            </div>
            <div class="fc-invite-input-lable pT30">
              Email
            </div>
            <div class="position-relative">
              <el-input
                type="text"
                disabled
                class="form-control fc-input-full-border2"
                placeholder="Enter your email address"
                v-model="invitation.email"
                required
              ></el-input>
              <img src="~assets/svgs/lock.svg" class="lock-input-icon" />
            </div>
          </div>
          <div class="form-group" v-show="showPassword">
            <div class="fc-invite-input-lable pT20">Password</div>
            <el-input
              type="password"
              class="form-control mT0 fc-input-full-border2 invite-show-password user-select-none"
              placeholder="Enter password"
              v-model="model.pass"
              :required="showPassword"
              ref="password"
              show-password
              autocomplete="new-password"
            >
            </el-input>
          </div>
          <div class="form-group" v-show="showPassword">
            <div class="fc-invite-input-lable pT20 pass">Confirm Password</div>
            <el-input
              type="password"
              class="form-control mT0 fc-input-full-border2 invite-show-password user-select-none"
              placeholder="Confirm password"
              v-model.lazy="model.checkPass"
              :required="showPassword"
              :disabled="!model.pass"
              show-password
              autocomplete="new-password"
            ></el-input>
            <div class="password-error" v-if="notSamePasswords">
              Passwords do not match
            </div>
          </div>
          <button
            class="btn btn-primary fc-invite-form-btn"
            v-if="!saving"
            :disabled="
              showPassword && (notSamePasswords || !this.model.checkPass)
            "
          >
            ACTIVATE
          </button>
          <button class="btn btn-primary fc-invite-form-btn" disabled v-else>
            Please wait...
          </button>
        </form>

        <div class="fc-invite-copyright">
          Facilio Inc © 2022
        </div>
      </div>
    </div>
    <div class="fc-login-building-img z-10">
      <div class="fc-img-align-con">
        <img
          src="~assets/svgs/login-building-bg.svg"
          style="width: 90%;height: auto; "
        />
      </div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import { API } from '@facilio/api'
import { Message } from 'element-ui'

export default {
  data() {
    return {
      loading: true,
      invitation: {},
      model: {
        username: '',
        email: '',
        pass: '',
        checkPass: null,
        companyname: '',
      },
      saving: false,
      token: -1,
      currentUser: '',
      userMismatchError: false,
    }
  },
  mounted() {
    this.loadInvitation()
  },
  computed: {
    notSamePasswords() {
      return this.model.checkPass && this.model.pass !== this.model.checkPass
    },
    showPassword() {
      return !this.invitation.isVerified
    },
  },
  methods: {
    hasUser() {
      let isLoggedIn = Vue.cookie.get('fc.loggedIn') === 'true'
      return isLoggedIn
    },
    async loadInvitation() {
      let hasUser = this.hasUser()

      if (hasUser) {
        this.userMismatchError = true
        return
      }

      this.loading = true
      API.get('/v2/validateInviteLink', {
        inviteToken: this.$route.params.invitetoken,
      }).then(({ error, data }) => {
        if (!error) {
          this.invitation = data.invitation
        } else {
          this.invitation.error = true
        }
        this.loading = false
      })
    },
    acceptInvite() {
      this.saving = true
      API.post('/integ/acceptOpInvite', {
        inviteToken: this.$route.params.invitetoken,
        username: this.model.username,
        emailaddress: this.invitation.email.trim().toLowerCase(),
        rawPassword: this.model.pass,
      }).then(({ error, data }) => {
        if (
          data &&
          data.jsonresponse &&
          data.jsonresponse.status === 'failure'
        ) {
          Message.error(data.jsonresponse.messages || 'Error Occurred')
        } else {
          if (!error) this.redirectToLogin()
          else Message.error('Error Occurred')
        }
        this.saving = false
      })
    },
    redirectToLogin() {
      this.$router.replace({
        name: 'login',
      })
    },
  },
}
</script>
<style>
body {
  background-color: #f0f3f4;
  color: #58666e;
}

.tcenter {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

.error {
  color: red;
}

.form-group input {
  outline: none;
  width: 100%;
  height: 48px;
  padding: 10px 15px;
  font-size: 13px;
  line-height: 1.42857143;
  color: #555;
  background-color: #fff;
  background-image: none;
  border: 1px solid #d2d2d2;
  border-radius: 4px;
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
  transition: border-color ease-in-out 0.15s, box-shadow ease-in-out 0.15s;
  margin-top: 8px;
}

.forgot-links div {
  margin-top: 12px;
}

.forgot-links div a {
  color: #000;
  font-size: 13px;
}

.footer {
  padding-top: 30px;
}
</style>
