<template>
  <div class="fc-login-con">
    <loader v-if="loading"></loader>
    <div v-else class="fc-login-align" :class="{ mT0: isWebServicePortal }">
      <portal-header v-if="isWebServicePortal"></portal-header>
      <div
        class="fc-login-block"
        :class="{ mT50: isWebServicePortal }"
        v-if="!resetDone"
      >
        <div>
          <div
            v-if="!isWebServicePortal"
            class="fc-login-logo-block"
            aria-hidden="true"
          >
            <img
              v-if="brandName == 'BuildingsTalk'"
              src="~assets/buildingstalklogo.png"
              style="height:60px;"
              :alt="brandName"
            />
            <img
              v-else-if="brandName == 'Sutherland'"
              src="~assets/sutherland.png"
              style="height:60px;"
              :alt="brandName"
            />
            <img
              v-else-if="brandName == 'Moro'"
              src="~assets/facilio-moro.png"
              class="fc-moro-logo"
              :alt="brandName"
            />
            <img
              v-else-if="orgLogoUrl"
              :src="orgLogoUrl"
              style="height:60px;"
              :alt="brandName"
            />
            <img
              v-else
              src="~assets/facilio-blue-logo.svg"
              style="height:25px;"
              :alt="brandName"
            />
          </div>
          <div class="fc-login-txt2 pT20">
            {{ $t('panel.panel.reset_pwd_content') }}
          </div>

          <div class="alert alert-danger" v-if="error" role="alert">
            <p v-if="error" class="error">{{ error.message }}</p>
          </div>

          <form @submit.prevent="passwordReset" class="pT27 position-relative">
            <div class="form-group">
              <el-input
                type="text"
                class="form-control fc-login-input"
                ref="inputUsername"
                :placeholder="
                  $t('common.placeholders.enter_your_email_address')
                "
                v-model="username"
                :autocomplete="'on'"
                required
              >
              </el-input>
            </div>
            <button class="btn btn-primary fc-login-btn mT20" v-if="!resetting">
              {{ $t('common._common.reset_pass') }}
            </button>
            <button class="btn btn-primary fc-login-btn mT20" disabled v-else>
              {{ $t('common.header.please_wait') }}
            </button>
          </form>
          <div class="forgot-links">
            <div class="text-center pT20">
              <a
                href="javascript:void(0)"
                @click="$router.go(-1)"
                class="fc-forgot-password-txt text-center"
                >{{ $t('panel.panel.back_to_login') }}</a
              >
            </div>
          </div>
        </div>
      </div>
      <div
        v-else
        class="pB85 fc-login-block"
        style="width: 500px;padding-bottom: 60px;"
      >
        <div v-if="!isWebServicePortal" class="text-center">
          <img
            v-if="brandName == 'BuildingsTalk'"
            src="~assets/buildingstalklogo.png"
            style="height:60px;"
          />
          <img
            v-else-if="brandName == 'Sutherland'"
            src="~assets/sutherland.png"
            style="height:60px;"
          />
          <img
            class="fc-custom-logo fc-moro-logo"
            v-else-if="brandName == 'Moro'"
            src="~assets/facilio-moro.png"
          />
          <img
            v-else
            src="~assets/facilio-blue-logo.svg"
            style="height:25px;"
          />
        </div>
        <div class="text-center pT30">
          <img
            src="~assets/svgs/emptystate/reset-email-sent.svg"
            style="width: 101px; height: 83px;"
          />
        </div>
        <div class="fc-login-txt2 pT20">
          {{ $t('common.dialog.password_reset_link_has_been') }}
        </div>
      </div>

      <div v-if="!isCustomDomain" class="fc-copy-right pT30">
        {{ rebrandInfo.copyright.name }} © {{ rebrandInfo.copyright.year }}
      </div>
      <portal-footer
        v-if="isWebServicePortal && !isCustomDomain"
      ></portal-footer>
      <div class="fc-login-footer">
        <div class="fc-login-footer-img">
          <img
            src="~assets/svgs/fc-login-footer-bg.svg"
            class="fc-login-footer-img"
            aria-hidden="true"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import http from 'util/http'
import RebrandMixin from 'util/rebrandMixin.js'
import PortalHeader from 'src/PortalTenant/auth/PortalHeader'
import PortalFooter from 'src/PortalTenant/auth/PortalFooter'
import Loader from '@/Loader'

export default {
  mixins: [RebrandMixin],
  components: { PortalHeader, PortalFooter, Loader },
  data() {
    return {
      loading: false,
      resetting: false,
      username: '',
      error: null,
      resetDone: false,
      brandName: '',
      userPasswordError: null,
    }
  },
  mounted() {
    if (
      window.rebrandInfo &&
      window.rebrandInfo.brandName === 'BuildingsTalk'
    ) {
      // alert(this.brandName)
      this.brandName = window.rebrandInfo.brandName
      // alert(this.brandName)
    } else {
      this.brandName = window.rebrandInfo.brandName
    }

    this.setAutofocus('inputUsername')
  },
  methods: {
    passwordReset() {
      this.resetting = true
      http
        .post('/fresetPassword', {
          emailaddress: this.username,
        })
        .then(({ data, status }) => {
          if (status === 200) {
            let { invitation } = data || {}
            let { status, message } = invitation || {}
            if (status == 'success') {
              this.resetDone = true
            } else {
              throw new Error(message)
            }
          }
        })
        .catch(
          ({
            message = this.$t('setup.users_management.pw_reset_not_send'),
          }) => {
            this.$message.error(message)
            this.resetting = false
          }
        )
    },
  },
}
</script>

<style>
body {
  background-color: #f8f8fb;
}

.error {
  color: red;
}
</style>
