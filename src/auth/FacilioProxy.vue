<template>
  <div class="signin-container">
    <div class="gsignin-container">
      <div class="text-center">
        <img
          src="~assets/facilio-blue-logo.svg"
          style="height:30px;"
          aria-hidden="true"
        />
      </div>
      <div v-if="errorMessage">
        {{ errorMessage }}
      </div>
      <div v-else-if="!$route.query.token">
        <div
          id="g_id_onload"
          data-client_id="749943625822-unl69f9ufcga8tkitsoairfbmncam341.apps.googleusercontent.com"
          data-context="signin"
          data-ux_mode="popup"
          :data-login_uri="loginUri"
          data-auto_prompt="false"
        ></div>

        <div
          class="g_id_signin"
          data-type="standard"
          data-shape="rectangular"
          data-theme="outline"
          data-text="signin_with"
          data-size="large"
          data-logo_alignment="left"
          data-width="200"
        ></div>
      </div>
      <div v-else>
        <div class="username-container pT30" v-if="isportalproxy">
          <div>
            <div class="text-left label-txt-black">Portal url</div>
            <el-input
              type="text"
              class="fc-login-input pT15"
              required
              v-model="portalUrl"
            />
          </div>
          <button
            native-type="submit"
            class="btn btn-primary fc-login-btn mT20"
            @click="handleSubmit"
          >
            Submit
          </button>
        </div>
        <div class="username-container pT30" v-else>
          <div class="text-left label-txt-black">
            Enter Customer Email Address
          </div>
          <el-input
            v-model="username"
            type="text"
            class="fc-login-input pT15"
            required
          ></el-input>
          <el-button
            native-type="submit"
            @click="handleSubmit"
            class="btn btn-primary fc-login-btn mT20"
          >
            Submit
          </el-button>
        </div>
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
      username: '',
      loginUri: '',
      isportalproxy: false,
      portalUrl: '',
      errorMessage: null,
    }
  },
  created() {
    Vue.cookie.delete('fc.portalproxy')
    this.isportalproxy = window.location.pathname.includes(
      '/auth/portalproxyuser'
    )
    if (this.isportalproxy) {
      Vue.cookie.set('fc.portalproxy', 'true')
    }
    this.loginUri =
      window.location.protocol +
      '//' +
      window.rebrandInfo.servername +
      '/api/integ/authorizeproxyuser'
    if (this.$route.query.message) {
      this.errorMessage = this.$route.query.message
    }
    let tag = document.createElement('script')
    tag.src = 'https://accounts.google.com/gsi/client'
    tag.defer = true
    document.head.appendChild(tag)
  },
  methods: {
    async handleSubmit() {
      let { data, error } = await API.post('/integ/proxyUser', {
        proxiedUserName: this.username,
        token: this.$route.query.token,
        portalUrl: this.portalUrl,
      })
      if (error) {
        this.errorMessage =
          error.message || this.$t('common._common.error_occured')
      } else {
        if (
          data.jsonresponse.errorcode === '1' ||
          data.jsonresponse.errorcode === '2'
        ) {
          this.errorMessage =
            data.jsonresponse.message || this.$t('common._common.error_occured')
        } else {
          if (this.isportalproxy) {
            window.location.href = data.jsonresponse.redirectUrl
          } else {
            this.$store.commit('LOGIN_REQUIRED', false)
            window.location.href = window.location.origin
          }
        }
      }
    },
  },
}
</script>
<style lang="scss">
.signin-container {
  background-color: rgb(247, 248, 249);
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.gsignin-container {
  background: #fff;
  padding: 80px 40px;
  border-radius: 4px;
}

.username-container {
  width: 350px;
}

.fc-login-input {
  .el-input__inner {
    width: 100%;
    height: 45px;
    padding-left: 16px !important;
    padding-right: 16px !important;
    border-radius: 5px !important;
    border: solid 1px #ccc;
    background-color: #ffffff !important;
    -webkit-transition: border-color ease-in-out 0.15s,
      -webkit-box-shadow ease-in-out 0.15s;
    -o-transition: border-color ease-in-out 0.15s, box-shadow ease-in-out 0.15s;
    transition: border-color ease-in-out 0.15s, box-shadow ease-in-out 0.15s;
    color: #19182e;
    font-size: 13px;
    letter-spacing: 0.5px;

    &::placeholder {
      font-size: 13px !important;
      font-weight: normal;
      line-height: normal;
      letter-spacing: 0.5px;
      color: #9998b3 !important;
    }

    &:active,
    &:focus,
    &:hover {
      box-shadow: 0 0 4px 0 #e1d6ff !important;
      border: solid 1px #b4d0ff !important;
      border-color: #b4d0ff !important;
    }
  }
  .el-input__clear {
    color: #c0c2db;
    font-size: 17px;
    padding-top: 4px;
  }
  .el-input__suffix-inner:before {
    content: '\e906';
  }
}
.fc-login-btn {
  width: 100%;
  height: 50px;
  border-radius: 5px;
  background: rgb(255 42 128 / 0.9) !important;
  border: 1px solid rgb(255 42 128 / 0.9) !important;
  font-size: 14px;
  font-weight: bold;
  line-height: normal;
  letter-spacing: 1px;
  text-align: center;
  color: #ffffff;
  text-transform: uppercase;

  &:hover,
  &:focus,
  &:active {
    background-color: #ff2a80 !important;
    border: 1px solid #ff2a80 !important;
  }
  &:focus,
  &:active {
    box-shadow: 0 0 1px 1px rgb(255 42 128 / 75%);
  }
}
</style>
