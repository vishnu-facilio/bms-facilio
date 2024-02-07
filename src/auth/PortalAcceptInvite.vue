<template>
  <div>
    <div v-if="pageInfoLoading">{{ $t('panel.loading_load') }}</div>

    <el-container v-else class="service-portal layout-page sp-main block">
      <portal-header></portal-header>

      <el-main class="service-portal-main">
        <div
          class="tcenter col-sm-6 col-sm-offset-3"
          style="width: 350px;margin: 0 auto;"
        >
          <div
            v-if="userMismatchError"
            class="tcenter col-sm-6 col-sm-offset-3"
            style="width: 350px;margin: 0 auto; padding:20px;"
          >
            {{ $t('panel.signup.diff') }}
            <a href="/auth/logout">{{ $t('panel.signup.logout') }}</a>
            {{ $t('panel.signup.curr_user') }}
          </div>

          <div v-else-if="invitation.error" class="mT20">
            {{ $t('panel.signup.link') }}
          </div>

          <div v-else-if="loading">{{ $t('panel.loading_load') }}</div>

          <div v-else-if="invitation.account_exists" class="width100">
            <h2>{{ $t('panel.signup.join') }}</h2>
            <p>
              {{ $t('panel.signup.join_org') }} -
              <b>{{ invitation.orgname }}</b>
            </p>
            <button
              class="btn btn-primary"
              v-if="!saving"
              @click="acceptInvite()"
            >
              {{ $t('panel.signup.join') }}
            </button>
            <button class="btn btn-primary" disabled v-else>
              {{ $t('panel.profile.please_wait') }}
            </button>
          </div>

          <div class="width100">
            <h2>{{ $t('panel.signup.join') }}</h2>
            <p>
              {{ $t('panel.signup.join_org') }} -
              <b>{{ invitation.orgname }}</b>
            </p>
            <form @submit.prevent="acceptInvite">
              <div class="form-group">
                <input
                  type="text"
                  disabled
                  class="form-control"
                  placeholder="Enter your email address"
                  v-model="invitation.email"
                  required
                />
              </div>
              <div class="form-group">
                <input
                  type="password"
                  class="form-control"
                  placeholder="Create password"
                  v-model="model.pass"
                  required
                  autocomplete="new-password"
                />
              </div>
              <button class="btn btn-primary" v-if="!saving">
                {{ $t('panel.signup.act') }}
              </button>
              <button class="btn btn-primary" disabled v-else>
                {{ $t('panel.profile.please_wait') }}
              </button>
            </form>

            <div class="footer">
              <small class="text-muted">
                {{ $t('panel.profile.facilio') }}
                <br />© 2022
              </small>
            </div>
          </div>
        </div>
      </el-main>
      <portal-footer></portal-footer>
    </el-container>
  </div>
</template>

<script>
import AcceptInvite from './FacilioAcceptInvite'
import PortalHeader from 'src/PortalTenant/auth/PortalHeader'
import PortalFooter from 'src/PortalTenant/auth/PortalFooter'
import homeMixin from 'src/PortalTenant/auth/portalHomeMixin'

export default {
  extends: AcceptInvite,
  mixins: [homeMixin],
  components: { PortalHeader, PortalFooter },
  async created() {
    this.pageInfoLoading = true
    await this.loadPublicInfo()
    this.pageInfoLoading = false
  },
  data() {
    return {
      pageInfoLoading: false,
    }
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
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
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
