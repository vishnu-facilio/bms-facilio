<template>
  <div>
    <div v-if="pageInfoLoading">Loading...</div>

    <el-container v-else class="service-portal layout-page sp-main block">
      <portal-header></portal-header>

      <el-main class="service-portal-main">
        <div>
          <div
            style="width: 300px; margin: 50px auto; text-align: center;"
            v-if="!signupDone"
          >
            <p>Signup for your {{ portalName }} account</p>
            <div class="alert alert-danger" v-if="error">
              <p v-if="error" class="error">{{ error.message }}</p>
            </div>
            <form @submit.prevent="login">
              <div class="form-group">
                <input
                  id="inputUsername"
                  type="text"
                  class="form-control"
                  placeholder="Email address"
                  v-model="signupform.username"
                  required
                />
              </div>
              <div class="form-group">
                <input
                  id="inputPassword"
                  type="password"
                  class="form-control"
                  placeholder="Password"
                  v-model="signupform.password"
                  required
                  autocomplete="new-password"
                />
              </div>
              <div class="form-group">
                <input
                  id="inputConfirmPassword"
                  type="password"
                  class="form-control"
                  placeholder="Confirm password"
                  v-model="signupform.confirmpassword"
                  required
                  autocomplete="new-password"
                />
              </div>
              <button class="btn btn-primary" v-if="!accountcreating">
                {{ $t('panel.signup.register') }}
              </button>
              <button class="btn btn-primary" disabled v-else>
                {{ $t('panel.signup.wait') }}
              </button>
            </form>
            <div class="forgot-links">
              <div>
                <router-link :to="loginPath">{{
                  $t('panel.signup.already')
                }}</router-link>
              </div>
            </div>
          </div>
          <div
            class="tcenter col-sm-6 col-sm-offset-3"
            style="width: 350px;margin: 0 auto; padding:20px;"
            v-else
          >
            <h2>
              <img src="~assets/facilio-logo-black.svg" style="height:40px;" />
            </h2>
            <p>
              {{ $t('panel.signup.confirmation') }}
            </p>
          </div>
        </div>
      </el-main>
      <portal-footer></portal-footer>
    </el-container>
  </div>
</template>
<script>
import http from 'util/http'
import PortalHeader from 'src/PortalTenant/auth/PortalHeader'
import PortalFooter from 'src/PortalTenant/auth/PortalFooter'
import homeMixin from 'src/PortalTenant/auth/portalHomeMixin'

export default {
  mixins: [homeMixin],
  components: { PortalHeader, PortalFooter },
  data() {
    return {
      pageInfoLoading: false,
      error: null,
      accountcreating: false,
      signupform: {
        username: '',
        password: '',
        confirmpassword: '',
      },
      signupDone: false,
    }
  },
  created() {
    this.pageInfoLoading = true
    this.loadPublicInfo().finally(() => (this.pageInfoLoading = false))
  },
  computed: {
    portalName() {
      return document.domain
    },
  },
  methods: {
    reset() {
      this.signupform.username = ''
      this.signupform.password = ''
      this.signupform.confirmpassword = ''
    },
    login() {
      if (this.signupform.password !== this.signupform.confirmpassword) {
        this.error = {
          message: 'Password does not match.',
        }
        return
      }

      http
        .post('/service/apisignup', this.signupform)
        .then(response => {
          if (response.data.jsonresponse.message === 'success') {
            this.signupDone = true
          } else if (
            response.data.jsonresponse.message ===
            'Only whitelisted domains allowed'
          ) {
            this.error = {
              message: 'Signup restricted for ' + this.signupform.username,
            }
          } else if (
            response.data.jsonresponse.message === 'Email Already Registered' ||
            response.data.jsonresponse.message ===
              'Signup not allowed for this portal'
          ) {
            this.error = {
              message: response.data.jsonresponse.message,
            }
          } else {
            this.signupDone = true
          }
        })
        .catch(error => {
          if (error) {
            console.error(error)
          }
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
.error {
  color: red;
}
.tcenter {
  text-align: center;
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
  -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
  -webkit-transition: border-color ease-in-out 0.15s,
    -webkit-box-shadow ease-in-out 0.15s;
  -o-transition: border-color ease-in-out 0.15s, box-shadow ease-in-out 0.15s;
  transition: border-color ease-in-out 0.15s, box-shadow ease-in-out 0.15s;
  margin-top: 8px;
}

button.btn.btn-primary {
  background-color: #7266ba;
  border-color: #7266ba;
  color: #ffffff !important;
  width: 100%;
  font-weight: 400;
  border-radius: 2px;
  outline: 0 !important;
  padding: 10px 16px;
  font-size: 18px;
  line-height: 1.3333333;
  border: 1px solid #7266ba;
  margin-top: 10px;
  cursor: pointer;
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
<style>
@import '../assets/styles/portal.css';
@import '../assets/styles/portalform.css';
@import '../assets/styles/white-theme.css';
@import '../assets/styles/black-theme.scss';
@import '../assets/styles/helper.scss';
@import '../assets/styles/c3.css';
</style>
