<template>
  <div>
    <div v-if="pageInfoLoading">{{ $t('panel.profile.loading') }}</div>

    <el-container v-else class="service-portal layout-page sp-main block">
      <portal-header></portal-header>

      <el-main class="service-portal-main">
        <div
          class="tcenter col-sm-6 col-sm-offset-3"
          style="width: 350px;margin: 0 auto;"
        >
          <div v-if="loading">
            {{ $t('panel.profile.loading') }}
          </div>
          <div v-else-if="invitation.error">
            {{ $t('panel.signup.link_exp') }}
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
      loading: true,
      invitation: {},
      model: {
        username: '',
        email: '',
        pass: '',
        companyname: '',
      },
      saving: false,
      token: -1,
      currentUser: '',
      userMismatchError: false,
    }
  },
  created() {
    this.pageInfoLoading = true
    this.loadPublicInfo().finally(() => (this.pageInfoLoading = false))
  },
  mounted() {
    this.loadInvitation()
  },
  methods: {
    loadInvitation() {
      this.loading = true
      http
        .get(
          'validateRegistration?inviteToken=' + this.$route.params.invitetoken
        )
        .then(response => {
          if (response.data.invitation) {
            let invitation = response.data.invitation
            if (invitation && invitation.accepted) {
              this.$router.replace({
                name: 'login',
                query: { username: invitation.email },
              })
            }
          }
          this.loading = false
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
  text-align: center;
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
