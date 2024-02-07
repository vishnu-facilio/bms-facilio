<template>
  <el-header :class="['sp-toolbar toolbar']">
    <router-link :to="loginPath" class="pointer">
      <img
        class="sp-logo"
        style="width: 200px;height: 60px;object-fit: contain;"
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
        class="fc-custom-logo fc-moro-logo"
        v-else-if="this.brandName == 'Moro'"
        src="~assets/facilio-moro.png"
      />
      <img
        class="sp-logo"
        v-else
        src="~assets/facilio-blue-logo.svg"
        style="width:100px; height:40px;"
      />
    </router-link>

    <div class="sp-header-container">
      <ul class="product-menu uppercase pT5" v-if="isPublicRequestAllowed">
        <router-link tag="li" :to="{ name: 'submitRequest' }">
          <a>{{ $t('tenant.announcement.submit_request') }}</a>
          <div class="sp-header-indicator"></div>
        </router-link>
      </ul>

      <ul class="header-tabs pull-right log-in-signup mT10">
        <router-link v-if="canShowLogin" tag="li" :to="loginPath">
          <a class="portal-link">{{ $t('tenant.announcement.login') }}</a>
          <div class="sp-header-indicator"></div>
        </router-link>

        <router-link v-if="isSignupAllowed" tag="li" :to="{ name: 'signup' }">
          <a class="portal-link">{{ $t('tenant.announcement.signup') }}</a>
          <div class="sp-header-indicator"></div>
        </router-link>
      </ul>
    </div>
  </el-header>
</template>
<script>
import homeMixin from './portalHomeMixin'
import getProperty from 'dlv'
import { isEmpty } from '@facilio/utils/validation'
import RebrandMixin from 'util/rebrandMixin'
export default {
  data() {
    return {
      brandName: '',
    }
  },
  mixins: [homeMixin, RebrandMixin],
  computed: {
    isServicePortal() {
      return (
        getProperty(this.$appDomain, 'appDomainTypeEnum') === 'SERVICE_PORTAL'
      )
    },
    isPublicRequestAllowed() {
      return (
        this.isServicePortal &&
        getProperty(this, '$portalInfo.is_public_create_allowed')
      )
    },
    isSignupAllowed() {
      return (
        this.isServicePortal && getProperty(this, '$portalInfo.signup_allowed')
      )
    },
    canShowLogin() {
      let isNotAuth = isEmpty(this.$portaluser)
      let isInLogin = this.$route.path.includes('/login')

      return isNotAuth && !isInLogin
    },
  },
  mounted() {
    // if (window.rebrandInfo && window.rebrandInfo.brandName) {
    //   this.brandName = window.rebrandInfo.brandName
    // }
    if (window.brandConfig && window.brandConfig.name) {
      this.brandName = window.brandConfig.name
    }
  },
  methods: {
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
body {
  background-color: #f0f3f4;
  color: #58666e;
}

.footer {
  padding-top: 30px;
}
</style>
<style>
@import '../../assets/styles/portal.css';
@import '../../assets/styles/portalform.css';
@import '../../assets/styles/white-theme.css';
@import '../../assets/styles/black-theme.scss';
@import '../../assets/styles/helper.scss';
@import '../../assets/styles/c3.css';
</style>
