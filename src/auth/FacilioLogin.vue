<template>
  <div>
    <loader v-if="ssoLoading"></loader>
    <div v-else class="fc-login-con">
      <div class="fc-login-align" :class="{ mT0: isWebServicePortal }">
        <portal-header v-if="isWebServicePortal"></portal-header>
        <div
          class="fc-login-block"
          :class="{ mT50: isWebServicePortal }"
          role="presentation"
        >
          <div
            v-if="!isWebServicePortal && !isMobilePortal"
            class="fc-login-logo-block"
          >
            <!-- TEMP FIX , Brand LOGO for iungoCitGroup , TO DO , Why logo cropped in Profile settings not applied to brand info ? fix-->
            <img
              v-if="rebrandInfo && rebrandInfo.name == 'iungoCitGroup'"
              :src="getWebPackPublicPath() + '/statics/logo/citgroup.jpeg'"
              style="max-height:40px;"
              :alt="rebrandInfo.brandName"
              aria-hidden="true"
            />
            <!--  -->

            <img
              v-else-if="logo_url"
              :src="logo_url"
              style="max-height:40px;"
              :alt="rebrandInfo.brandName"
              aria-hidden="true"
            />
            <img
              v-else-if="brandName == 'BuildingsTalk'"
              src="~assets/buildingstalklogo.png"
              style="height:60px;"
              :alt="rebrandInfo.brandName"
              aria-hidden="true"
            />
            <img
              v-else-if="brandName == 'Moro'"
              src="~assets/facilio-moro.png"
              class="fc-moro-logo"
              :alt="rebrandInfo.brandName"
              aria-hidden="true"
            />

            <img
              v-else-if="!logo_url"
              src="~assets/facilio-blue-logo.svg"
              style="height:25px;"
              :alt="rebrandInfo.brandName"
              aria-hidden="true"
            />
          </div>
          <h1 class="fc-login-txt pT15 m0">
            {{ $t('common._common.sign_into') }}
          </h1>
          <div class="alert alert-danger" v-if="error">
            <div
              v-if="error"
              class="error text-center pT10 f14 nowrap"
              role="alert"
            >
              {{ error.message }}
            </div>
          </div>
          <div v-if="loading">Please wait...</div>
          <div v-if="(isCustomDomain || isWebServicePortal) && !totpToken">
            <form
              @submit.prevent="loginUserNamePwd"
              class="fc-form-slide-in-left fc-animate-login-form position-relative"
            >
              <div class="form-group position-relative pT20">
                <el-input
                  id="inputUsername"
                  ref="inputUsername"
                  type="text"
                  class="fc-login-input"
                  placeholder="Enter your email address"
                  aria-label="Enter your email address"
                  tabindex="0"
                  autofocus="true"
                  v-model="username"
                  required
                  :class="{ errorBorder: userPasswordError }"
                  :autocomplete="'on'"
                  :autocapitalize="'off'"
                />
                <div
                  v-if="userPasswordError"
                  role="alert"
                  class="error text-left pT10 f12"
                >
                  {{ userPasswordError.message }}
                </div>
              </div>
              <div class="position-relative pT20">
                <el-input
                  id="inputPassword"
                  :type="showVisibilityIcon"
                  class="fc-login-input"
                  placeholder="Enter your password"
                  v-model="pass"
                  required
                  :class="{ errorBorder: userPasswordError }"
                >
                </el-input>
                <div
                  v-if="userPasswordError"
                  role="alert"
                  class="error text-left pT10 f12"
                >
                  {{ userPasswordError.message }}
                </div>
                <div
                  v-if="showVisibilityIcon == 'text'"
                  @click="hidePassword"
                  :class="{ 'position-relative': isWebServicePortal }"
                >
                  <InlineSvg
                    src="svgs/eye-visible"
                    class="fc-login-icons pointer"
                    iconClass="icon icon-md fill-lite-grey"
                    style="top: 33px;"
                    :class="{ fcPasswordPortalEyevisible: isWebServicePortal }"
                  ></InlineSvg>
                </div>
                <div
                  v-if="showVisibilityIcon == 'password'"
                  @click="showPassword"
                  :class="{ 'position-relative': isWebServicePortal }"
                >
                  <InlineSvg
                    src="svgs/not-visible"
                    class="fc-login-icons pointer"
                    iconClass="icon icon-md fill-lite-grey"
                    style="top: 33px;"
                    :class="{ fcPasswordPortal: isWebServicePortal }"
                  ></InlineSvg>
                </div>
              </div>
              <button
                class="btn btn-primary fc-login-btn mT20"
                :disabled="logging_in"
              >
                Sign in
              </button>
              <div v-if="ssoLink">
                <div class="fc-login-txt-or">
                  (OR)
                </div>
                <div>
                  <a :href="ssoLink" class="fc-sso-link-btn">Login With SSO</a>
                </div>
              </div>
              <div class="pT20 text-center">
                <router-link
                  :to="{ name: 'passwordreset' }"
                  class="fc-forgot-password-txt text-center"
                  >{{ $t('common._common.forgot_password') }}
                </router-link>
              </div>
            </form>
          </div>
          <div
            v-if="!isCustomDomain && !isWebServicePortal && !digest"
            class="pT27 fade-in-lightbox"
          >
            <form
              @submit.prevent="faciliologin"
              class="fc-form-slide-in-left fc-animate-login-form position-relative"
            >
              <div class="form-group">
                <el-input
                  id="inputUsername"
                  ref="inputUsername"
                  type="text"
                  class="fc-login-input"
                  placeholder="Enter your email address"
                  title="Enter your email address"
                  tabindex="0"
                  autofocus="true"
                  v-model="username"
                  required
                  :class="{ errorBorder: userPasswordError }"
                  :autocomplete="'on'"
                />
                <div
                  v-if="userPasswordError"
                  role="alert"
                  class="error text-left pT10 f12"
                >
                  {{ userPasswordError.message }}
                </div>
              </div>
              <el-button
                class="btn btn-primary fc-login-btn mT20"
                :disabled="logging_in"
                :loading="logging_in"
                native-type="submit"
              >
                {{ $t('common._common.next') }}
              </el-button>
              <div class="pT20 text-center">
                <router-link
                  :to="{ name: 'passwordreset' }"
                  class="fc-forgot-password-txt text-center"
                  >{{ $t('common._common.forgot_password') }}
                </router-link>
              </div>
            </form>
          </div>
          <div
            v-if="domainLookupRequired"
            class="fade-in-lightbox fc-portal-login-block"
          >
            <form
              @submit.prevent="loginUserNamePwd"
              class="fc-form-slide-in-next fc-animate-login-form position-relative"
              autocomplete="on"
            >
              <el-input
                id="inputDomain"
                class="fc-login-input fc-login-append-in pT20"
                placeholder="Enter Subdomain"
                v-model="probableDomain"
                :autocomplete="'nope'"
                required
              >
                <template v-if="isMobileTenantPortal" slot="append"
                  >.faciliotenants.com</template
                >
                <template v-if="isMobileServicePortal" slot="append"
                  >.facilioportal.com</template
                >
              </el-input>
              <div class="pT15">
                <el-input
                  id="inputDomain"
                  class="fc-login-input"
                  placeholder="Username"
                  v-model="username"
                  required
                >
                </el-input>
              </div>
              <div class="form-group position-relative">
                <el-input
                  id="inputPassword"
                  :type="showVisibilityIcon"
                  class="fc-login-input pT15"
                  placeholder="Enter your password"
                  aria-label="Enter your password"
                  v-model="pass"
                  required
                  :class="{ errorBorder: userPasswordError }"
                >
                </el-input>
                <div
                  v-if="userPasswordError"
                  role="alert"
                  class="error text-left pT10 f12"
                >
                  {{ userPasswordError.message }}
                </div>
                <div v-if="showVisibilityIcon == 'text'" @click="hidePassword">
                  <InlineSvg
                    src="svgs/eye-visible"
                    class="fc-login-icons pointer"
                    iconClass="icon icon-md fill-lite-grey"
                  ></InlineSvg>
                </div>
                <div
                  v-if="showVisibilityIcon == 'password'"
                  @click="showPassword"
                >
                  <InlineSvg
                    src="svgs/not-visible"
                    class="fc-login-icons pointer"
                    iconClass="icon icon-md fill-lite-grey"
                  ></InlineSvg>
                </div>
              </div>
              <div class="pT10 flex-middle justify-content-space">
                <div>
                  <router-link
                    :to="{ name: 'passwordreset' }"
                    class="fc-forgot-password-txt text-center"
                    >{{ $t('common._common.forgot_password') }}
                  </router-link>
                </div>
              </div>
              <button
                class="btn btn-primary fc-login-btn mT20"
                :disabled="logging_in"
              >
                Sign in
              </button>
            </form>
          </div>
          <div v-else-if="digest && !totpToken" class="fade-in-lightbox">
            <form
              @submit.prevent="loginWithDigest"
              class="fc-form-slide-in-next fc-animate-login-form position-relative"
              autocomplete="on"
            >
              <div class="pT20">
                <div class="fc-login-txt14 pB20">
                  {{ username }}
                  <a
                    @click="changeUserName"
                    href="javascript:void(0)"
                    aria-label="Change email address"
                    class="fc-login-txt-blue13 fc-change-email-txt"
                    >{{ $t('common._common.change') }}</a
                  >
                </div>
              </div>
              <div class="form-group position-relative">
                <el-input
                  id="inputPassword"
                  ref="digestInputPassword"
                  :type="showVisibilityIcon"
                  class="fc-login-input"
                  placeholder="Enter your password"
                  aria-label="Enter your password"
                  v-model="pass"
                  required
                  :class="{ errorBorder: userPasswordError }"
                >
                </el-input>
                <div
                  v-if="userPasswordError"
                  role="alert"
                  class="error text-left pT10 f12"
                >
                  {{ userPasswordError.message }}
                </div>
                <div
                  v-if="showVisibilityIcon == 'text'"
                  @click="hidePassword"
                  role="button"
                >
                  <InlineSvg
                    src="svgs/eye-visible"
                    class="fc-login-icons pointer"
                    iconClass="icon icon-md fill-lite-grey"
                  ></InlineSvg>
                </div>

                <div
                  v-if="showVisibilityIcon == 'password'"
                  @click="showPassword"
                  role="button"
                >
                  <InlineSvg
                    src="svgs/not-visible"
                    class="fc-login-icons pointer"
                    iconClass="icon icon-md fill-lite-grey"
                  ></InlineSvg>
                </div>
              </div>
              <div class="pT10 flex-middle justify-content-space">
                <div>
                  <a
                    v-if="ssourl"
                    :href="ssourl"
                    class="fc-forgot-password-txt"
                  >
                    {{ $t('common._common.signin_saml') }}</a
                  >
                </div>
                <div>
                  <router-link
                    :to="{ name: 'passwordreset' }"
                    class="fc-forgot-password-txt text-center"
                    >{{ $t('common._common.forgot_password') }}
                  </router-link>
                </div>
              </div>
              <el-button
                class="btn btn-primary fc-login-btn mT20"
                :disabled="logging_in"
                :loading="logging_in"
                native-type="submit"
              >
                Sign in
              </el-button>
            </form>
          </div>
          <div v-else-if="totpToken" class="fade-in-lightbox">
            <form
              @submit.prevent="loginWithTotpAndTotpToken"
              class="fc-form-slide-in-next fc-animate-login-form position-relative"
              autocomplete="on"
            >
              <div class="fc-login-txt13 pB20 pT15">
                {{ $t('common._common.enter_otp_text') }}
              </div>
              <el-input
                id="inputTotpToken"
                class="fc-login-input"
                :placeholder="$t('common._common.enter_totp_placeholder')"
                v-model="totp"
                required
                :class="{ errorBorder: totpError }"
              >
              </el-input>
              <div v-if="totpError" class="error text-left pT10 f12">
                {{ $t('common._common.invalid_totp') }}
              </div>
              <div class="pT10 flex-middle justify-content-space">
                <div>
                  <div class="fc-forgot-password-txt text-center">
                    <a
                      href="mailto:support@facilio.com?subject=Need assistance Signing in"
                    >
                      {{ $t('common._common.need_assistance') }}
                    </a>
                  </div>
                </div>
              </div>
              <button
                class="btn btn-primary fc-login-btn mT20"
                :disabled="verifying_totp"
              >
                Verify
              </button>
            </form>
          </div>
          <div
            v-if="showGoogleLogin"
            class="signin-using fc-login-google mT20"
            :class="googleAuth.hide ? 'googleauth-hide' : ''"
          >
            <GoogleSignInButton
              v-if="googleAuth.enable"
              :clientId="googleAuth.clientId"
              @success="onGoogleAuthSuccess"
              @failure="onGoogleAuthError"
              @initSuccess="onGoogleAuthInitSuccess"
            >
            </GoogleSignInButton>
          </div>
        </div>
        <div
          v-if="!isCustomDomain"
          class="fc-copy-right pT30"
          role="contentinfo"
        >
          {{ rebrandInfo.copyright.name }} © {{ rebrandInfo.copyright.year }}
        </div>
        <portal-footer
          v-if="isWebServicePortal && !isCustomDomain"
        ></portal-footer>
        <div class="fc-login-footer">
          <div class="fc-login-footer-img">
            <img
              aria-hidden="true"
              src="~assets/svgs/fc-login-footer-bg.svg"
              :alt="rebrandInfo.brandName"
              class="fc-login-footer-img"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import http from 'util/http'
import GoogleSignInButton from './GoogleSignInButton'
import Loader from '@/Loader'
import RebrandMixin from 'util/rebrandMixin'
import PortalHeader from 'src/PortalTenant/auth/PortalHeader'
import PortalFooter from 'src/PortalTenant/auth/PortalFooter'

export default {
  components: {
    GoogleSignInButton,
    Loader,
    PortalHeader,
    PortalFooter,
  },
  mixins: [RebrandMixin],
  data() {
    return {
      ssoLink: null,
      verifying_totp: false,
      totp: '',
      totpError: false,
      probableDomain: '',
      domainError: null,
      domainLookupRequired: false,
      isMobileView: false,
      hideGoogleLogin: false,
      ssoLoading: false,
      loading: false,
      logging_in: false,
      username: this.$route.query.username ? this.$route.query.username : '',
      pass: '',
      ssourl: null,
      hasGoogleLogin: true,
      error: null,
      userPasswordError: null,
      newPasswordRequired: false,
      newPasswordRequiredContext: null,
      newpass: '',
      confirmnewpass: '',
      pwdchanging: false,
      brandName: '',
      digest: null,
      logo_url: null,
      googleAuth: {
        enable: false,
        clientId: null,
        hide: true,
      },
      ssoLogin: {
        show: false,
        domain: null,
        ssoError: null,
        domain_verifying: false,
        ssoURL: null,
        SAMLRequest: null,
        RelayState: null,
      },
      showVisibilityIcon: 'password',
      domains: null,
      domain: null,
      totpToken: null,
    }
  },
  created() {
    this.loading = false
    let {
      location,
      domainInfo,
      rebrandInfo,
      googleAuthEnable,
      googleAuthClientId,
    } = window

    let hostname = location.hostname
    if (hostname === 'sg.facilio.com') {
      let url = new URL(
        'https://app-sg.facilio.com' + location.pathname + location.search
      )
      location.replace(url.href)
    }

    this.isMobileView =
      location.pathname.includes('/mobile/login') || this.isMobilePortal

    if (this.isWebServicePortal) {
      this.hideGoogleLogin = true
    }
    if (this.isMobileView) {
      this.$cookie.delete('fc.mobile.idToken.facilio')
      this.$cookie.delete('fc.mobile.scheme')
      let date = new Date()
      date.setTime(date.getTime() + 2 * 3600 * 1000)
      this.$cookie.set('fc.isWebView', 'true', {
        expires: date,
        path: '/',
      })
    }
    if (
      domainInfo &&
      domainInfo.isCustomDomain &&
      domainInfo.isSSOEnabled &&
      domainInfo.ssoEndPoint &&
      !(this.$route.query && this.$route.query.ssoError)
    ) {
      this.ssoLoading = true
      let ssoEndPoint = this.appendRelayToSSOEndPoint(domainInfo.ssoEndPoint)
      location.replace(ssoEndPoint)
    }

    //no query -> false
    //?q=x -> false
    //?disableSSO=false -> false
    //?disableSSO=x -> false
    //?disableSSO=true -> true
    let disableSSO =
      this.$route.query &&
      this.$route.query.disableSSO &&
      this.$route.query.disableSSO === 'true'

    if (
      !disableSSO &&
      domainInfo &&
      domainInfo.isPortal &&
      domainInfo.isSSOEnabled &&
      domainInfo.ssoEndPoint &&
      !domainInfo.showSSOLink &&
      !(this.$route.query && this.$route.query.ssoError)
    ) {
      this.ssoLoading = true
      let ssoEndPoint = this.appendRelayToSSOEndPoint(domainInfo.ssoEndPoint)
      location.replace(ssoEndPoint)
    }

    if (domainInfo.showSSOLink) {
      this.ssoLink = this.appendRelayToSSOEndPoint(domainInfo.ssoEndPoint)
    }

    // this.setBuildingsTalkLogin()

    if (rebrandInfo && rebrandInfo.brandName) {
      this.brandName = rebrandInfo.brandName
    }

    this.googleAuth.enable = googleAuthEnable
    this.googleAuth.clientId = googleAuthClientId

    if (!this.isMobileView && !this.isWebServicePortal) {
      this.validateLogin()
    }

    if (domainInfo) {
      this.logo_url = domainInfo.logo_url
    }

    if (this.isMobileView) {
      this.hideGoogleLogin = true
    }

    if (this.$route.query && this.$route.query.ssoError) {
      this.error = {
        message: this.$route.query.ssoError || 'Single Sign-On failed.',
      }
    }
    if (this.username) {
      this.faciliologin()
    }
  },
  mounted() {
    this.setAutofocus('inputUsername')
  },
  computed: {
    showGoogleLogin() {
      if (this.hideGoogleLogin) {
        return false
      }

      if (this.isMobilePortal) {
        return false
      }

      if (this.isCustomDomain) {
        return false
      }

      //when login method is not looked up
      if (!this.digest) {
        return true
      }
      return this.digest && this.hasGoogleLogin
    },
  },
  methods: {
    loginWithTotpAndTotpToken() {
      let data = {
        totp: this.totp,
        digest: this.totpToken,
      }
      this.totpError = false
      this.verifying_totp = true

      if (this.isMobileServicePortal) {
        data.lookUpType = 'service'
      } else if (this.isMobileTenantPortal) {
        data.lookUpType = 'tenant'
      } else if (this.isMobileVendorPortal) {
        data.lookUpType = 'vendor'
      } else if (this.isMobileWorkQ) {
        data.lookUpType = 'workQ'
      }

      let options = {
        headers: {
          'X-Device-Type': 'web',
        },
      }

      if (this.isMobileView) {
        options.headers['X-Device-Type'] = 'webview'
      }

      let confirmPasswordPayload = {
        name: 'confirmpassword',
        query: { isPasswordExpired: true },
      }

      if (data.lookUpType) {
        confirmPasswordPayload.query.lookUpType = data.lookUpType
        console.log('lookup type set')
      }

      http.post('integ/verifyTotp', data, options).then(response => {
        if (response.data.jsonresponse.pwdPolicyResetToken) {
          let pwdPolicyResetToken =
            response.data.jsonresponse.pwdPolicyResetToken
          confirmPasswordPayload.params = { invitetoken: pwdPolicyResetToken }
          this.$router.push(confirmPasswordPayload)
        } else if (response.data.jsonresponse.username) {
          this.handleLoginSuccess()
        } else {
          this.totpError = true
        }
        this.verifying_totp = false
      })
    },
    getWebPackPublicPath() {
      return window.webpackPublicPath
    },
    appendRelayToSSOEndPoint(ssoEndPoint) {
      if (this.isMobileView) {
        let relayString = 'mobile-facilio'
        if (this.isMobileServicePortal) {
          relayString = 'mobile-serviceportal'
        } else if (this.isMobileTenantPortal) {
          relayString = 'mobile-tenantportal'
        } else if (this.isMobileVendorPortal) {
          relayString = 'mobile-vendorportal'
        } else if (this.isMobileWorkQ) {
          relayString = 'mobile-workQ'
        }
        let url = new URL(ssoEndPoint)
        url.searchParams.append('relay', relayString)
        ssoEndPoint = url.href
      }
      return ssoEndPoint
    },
    showPassword() {
      this.showVisibilityIcon = 'text'
    },
    hidePassword() {
      this.showVisibilityIcon = 'password'
    },
    changeUserName() {
      this.digest = null
      this.userPasswordError = null
      this.domainError = null
      this.domainLookupRequired = false
      this.probableDomain = null
      this.domains = []
      this.domain = null
      this.pass = null
      this.logo_url = null

      this.setAutofocus('inputUsername')
    },
    onGoogleAuthSuccess(result) {
      if (result && result.username) {
        this.handleLoginSuccess()
      } else {
        this.error = {
          message:
            'Your account is not active or not registered with ' +
            (this.brandName ? this.brandName : 'Facilio') +
            '.',
        }
      }
    },
    onGoogleAuthError() {
      this.error = {
        message: 'Google Sign In failed!',
      }
    },
    onGoogleAuthInitSuccess() {
      this.googleAuth.hide = false
    },
    // setBuildingsTalkLogin() {
    //   if (window.location.hostname.includes('buildingsoncloud')) {
    //     if (!window.rebrandInfo) {
    //       window.rebrandInfo = {}
    //     }
    //     window.rebrandInfo.brandName = 'BuildingsTalk'
    //     if (!window.rebrandInfo.copyright) {
    //       window.rebrandInfo.copyright = {}
    //     }
    //     window.rebrandInfo.copyright.name = 'Machinestalk Inc'
    //     window.rebrandInfo.copyright.year = '2020'
    //   }
    // },
    validateLogin() {
      let isLoggedIn = Vue.cookie.get('fc.loggedIn') === 'true'
      if (isLoggedIn) {
        let redirect = this.$route.query.redirect
        if (!redirect) {
          redirect = '/app'
        }
        this.$router.replace(redirect)
      }
    },
    faciliologin() {
      let logindata = {
        username: this.username.trim(),
      }
      if (this.isMobileServicePortal) {
        logindata.lookUpType = 'service'
      } else if (this.isMobileTenantPortal) {
        logindata.lookUpType = 'tenant'
      } else if (this.isMobileVendorPortal) {
        logindata.lookUpType = 'vendor'
      } else if (this.isMobileWorkQ) {
        logindata.lookUpType = 'workQ'
      }
      this.logging_in = true
      this.hasGoogleLogin = false
      this.userPasswordError = null
      this.ssourl = null
      this.digest = null
      this.logo_url = null
      http
        .post('integ/lookup', logindata)
        .then(response => {
          if (
            response.data.jsonresponse.code === 2 &&
            response.data.jsonresponse.baseUrl
          ) {
            let baseUrl = response.data.jsonresponse.baseUrl
            let url = baseUrl + this.$route.fullPath
            url += `${
              !url.includes('?') ? '?' : '&'
            }username=${encodeURIComponent(this.username)}`
            window.location.href = url
          } else if (response.data.jsonresponse.code === 1) {
            this.domainLookupRequired =
              response.data.jsonresponse.domainLookupRequired &&
              this.isMobilePortal
            this.domains = response.data.jsonresponse.domains
            if (this.domains && this.domains.length === 1) {
              this.domain = this.domains[0]
            }
            if (!response.data.jsonresponse.loginModes.includes('password')) {
              if (response.data.jsonresponse.loginModes.includes('SAML')) {
                let ssoEndPoint = this.appendRelayToSSOEndPoint(
                  response.data.jsonresponse.SSOURL
                )
                window.location.href = ssoEndPoint
                return
              }
            }
            this.logging_in = false
            if (response.data.jsonresponse.loginModes.includes('SAML')) {
              this.ssourl = this.appendRelayToSSOEndPoint(
                response.data.jsonresponse.SSOURL
              )
            }
            if (response.data.jsonresponse.loginModes.includes('google')) {
              this.hasGoogleLogin = true
            }
            this.logo_url = response.data.jsonresponse.logo_url
            this.$nextTick(() => {
              this.digest = response.data.jsonresponse.digest

              this.setAutofocus('digestInputPassword')
            })
          } else {
            this.logging_in = false
            this.userPasswordError = {
              message: 'Invalid Username',
            }
          }
        })
        .catch(error => {
          this.logging_in = false
        })
    },
    loginUserNamePwd() {
      let logindata = {
        username: this.username,
        password: this.pass,
      }

      let options = {
        headers: {
          'X-Device-Type': 'web',
        },
      }

      if (this.isMobileView) {
        options.headers['X-Device-Type'] = 'webview'
      }

      if (this.domainLookupRequired) {
        logindata.domain = this.probableDomain
      }

      if (this.isMobileServicePortal) {
        logindata.lookUpType = 'service'
      } else if (this.isMobileTenantPortal) {
        logindata.lookUpType = 'tenant'
      } else if (this.isMobileVendorPortal) {
        logindata.lookUpType = 'vendor'
      } else if (this.isMobileWorkQ) {
        logindata.lookUpType = 'workQ'
      }

      this.logging_in = true
      http
        .post('integ/loginWithUserNameAndPassword', logindata, options)
        .then(response => {
          this.logging_in = false
          if (response.data.jsonresponse.username) {
            this.handleLoginSuccess()
          } else {
            this.error = {
              message: 'Invalid Username/password',
            }
          }
        })
    },
    handleLoginSuccess() {
      this.$store.commit('LOGIN_REQUIRED', false)
      if (this.isMobileView || this.isMobilePortal) {
        this.$router.replace('/auth/loginsuccess')
      } else {
        const { href } =
          this.$router.resolve({ path: this.$route.query.redirect || '/' }) ||
          {}
        // Can't use route.push or repace here because we need the store to be cleared
        window.location.href = href
      }
    },
    assertDomainWithDigest() {
      let claim = {
        digest: this.digest,
        domain: this.probableDomain,
      }

      let options = {
        headers: {
          'X-Device-Type': 'web',
        },
      }

      if (this.isMobileView) {
        options.headers['X-Device-Type'] = 'webview'
      }

      if (this.isMobileServicePortal) {
        claim.lookUpType = 'service'
      } else if (this.isMobileTenantPortal) {
        claim.lookUpType = 'tenant'
      }

      this.logging_in = true
      http
        .post('integ/assertDomainWithDigest', claim, options)
        .then(response => {
          this.logging_in = false
          if (response.data.jsonresponse.code === 1) {
            this.domain = this.probableDomain
            this.domainLookupRequired = false
          } else {
            this.domainError = {
              message: 'Invalid Domain',
            }
          }
        })
    },
    loginWithDigest() {
      let logindata = {
        password: this.pass,
        digest: this.digest,
      }

      if (this.isMobilePortal) {
        logindata.domain = this.probableDomain
      }

      if (this.isMobileServicePortal) {
        logindata.lookUpType = 'service'
      } else if (this.isMobileTenantPortal) {
        logindata.lookUpType = 'tenant'
      } else if (this.isMobileVendorPortal) {
        logindata.lookUpType = 'vendor'
      } else if (this.isMobileWorkQ) {
        logindata.lookUpType = 'workQ'
      }

      this.logging_in = true
      this.userPasswordError = null

      let options = {
        headers: {
          'X-Device-Type': 'web',
        },
      }

      if (this.isMobileView) {
        options.headers['X-Device-Type'] = 'webview'
      }

      http
        .post('integ/loginWithPasswordAndDigest', logindata, options)
        .then(response => {
          if (response.data.jsonresponse.isOrgTotpEnabled) {
            if (response.data.jsonresponse.isMFASetupRequired) {
              this.$router.push({
                path: '/auth/mfasetup',
                query: {
                  mfaConfigToken: response.data.jsonresponse.mfaConfigToken,
                  lookUpType: logindata.lookUpType,
                },
              })
            } else {
              this.totpToken = response.data.jsonresponse.totpToken
            }
          } else {
            if (response.data.jsonresponse.pwdPolicyResetToken) {
              let pwdPolicyResetToken =
                response.data.jsonresponse.pwdPolicyResetToken
              this.$router.push({
                name: 'confirmpassword',
                params: { invitetoken: pwdPolicyResetToken },
                query: {
                  isPasswordExpired: true,
                  lookUpType: logindata.lookUpType,
                },
              })
            } else if (response.data.jsonresponse.username) {
              this.handleLoginSuccess()
            } else {
              this.logging_in = false
              this.userPasswordError = {
                message: 'Invalid Password',
              }
            }
          }
        })
    },
  },
}
</script>

<style lang="scss">
body {
  background-color: #f8f8fb;
}
.error {
  color: red;
}
.googleauth-hide {
  opacity: 0;
}
.fc-sso-link-btn {
  width: 100%;
  display: inline-block;
  font-size: 14px;
  font-weight: 500;
  background: rgb(243 243 243 / 60%);
  color: #19182e;
  padding: 16.5px 20px;
  border-radius: 4px;
  text-align: center;
  &:hover {
    color: #ff3184;
  }
}
.fc-login-txt-or {
  text-align: center;
  padding-top: 15px;
  padding-bottom: 15px;
  color: #8ca1ad;
  letter-spacing: 0.7px;
  font-weight: 500;
}
</style>
