<template>
  <el-container v-if="!loading" class="service-portal layout-page sp-main">
    <el-header class="sp-toolbar toolbar">
      <router-link to="/" class="pointer">
        <img
          class="sp-logo"
          style="width: auto; max-height: 50px; max-width: 120px; height: auto;"
          v-if="$portalOrg.logoUrl"
          :src="getConvertedUrl($portalOrg.logoUrl)"
        />
        <img
          class="sp-logo"
          v-else-if="$portalOrg.id === 146"
          src="~assets/facilio-pd7-technology-logo-black.svg"
          style="width:250px; height:40px;object-fit: cover;"
        />
        <img
          class="sp-logo"
          v-else
          src="~assets/facilio-blue-logo.svg"
          style="width:100px; height:40px;"
        />
      </router-link>

      <div class="sp-header-container">
        <template>
          <ul class="header-tabs pull-right log-in-signup mT10">
            <router-link tag="li" :to="{ name: 'login' }">
              <a class="portal-link">{{ $t('panel.welcome.login') }}</a>
              <div class="sp-header-indicator"></div>
            </router-link>

            <router-link
              v-if="isSignupAllowed"
              :to="{ name: 'signup' }"
              tag="li"
            >
              <a class="portal-link">{{ $t('panel.welcome.signup') }}</a>
              <div class="sp-header-indicator"></div>
            </router-link>
          </ul>
        </template>
      </div>
    </el-header>
    <el-main class="service-portal-main">
      <router-view />
    </el-main>
    <el-footer v-if="canShowFooter">
      <div class="footer-power">
        <span class="powered-txt">{{ $t('panel.welcome.power') }}</span
        ><span
          ><img
            class="sp-logo"
            src="~assets/facilio-blue-logo.svg"
            style="width:60px; height:25px;margin-right: 5px;"
        /></span>
      </div>
    </el-footer>
  </el-container>
</template>

<script>
import homeMixin from 'src/PortalTenant/auth/portalHomeMixin'
import getProperty from 'dlv'
import { isEmpty } from '@facilio/utils/validation'
import http from 'util/http'
import Util from 'util/index'
import Vue from 'vue'

export default {
  mixins: [homeMixin],

  beforeRouteEnter(to, from, next) {
    let baseUrl = new URL(http.defaults.baseURL)
    http.defaults.baseURL = baseUrl + '/service'
    next(vm => {
      vm.loadPublicInfo()
        .then(account => {
          Vue.use(Util, {
            account: account || {},
          })
        })
        .finally(() => {
          next()
          vm.loading = false
        })
    })
  },
  computed: {
    canShowFooter() {
      return (
        getProperty(this, '$portalOrg.logoUrl') &&
        getProperty(this, '$portalOrg.id') !== 75
      )
    },
    isServicePortal() {
      return (
        getProperty(this.$appDomain, 'appDomainTypeEnum') === 'SERVICE_PORTAL'
      )
    },
    isSignupAllowed() {
      return (
        this.isServicePortal && getProperty(this, '$portalInfo.signup_allowed')
      )
    },
    canShowLogin() {
      let isNotAuth = isEmpty(this.$portaluser)
      let isInLogin = this.$route.path.includes('/auth/login')

      return isNotAuth && !isInLogin
    },
  },

  methods: {
    myprofile() {
      this.$router.push({
        path: '/service/myProfile',
      })
    },
    getConvertedUrl(url) {
      if (url) {
        let link = url.split('/')
        if (link[1] === 'api' && link[2] === 'v2' && link[3] === 'service') {
          let temp = []
          temp = link[2]
          link[2] = link[3]
          link[3] = temp
          return link.join('/')
        } else {
          return url
        }
      }
    },
  },
}
</script>

<style>
.el-main.service-portal-main {
  padding: 0px;
  background: rgb(244, 244, 244);
  height: 100vh;
  padding-bottom: 50px;
  overflow-y: hidden;
}
@import '../../../assets/styles/portal.css';
</style>
<style>
@import '../../../assets/styles/portalform.css';
</style>
<style>
@import '../../../assets/styles/white-theme.css';
</style>
<style>
@import '../../../assets/styles/black-theme.scss';
</style>
<style>
@import '../../../assets/styles/helper.scss';
</style>
<style>
@import '../../../assets/styles/c3.css';
</style>
