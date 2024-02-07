<template>
  <div class="height100">
    <subheader :menu="subheaderMenu" newbtn="true" :parent="parent"></subheader>

    <div class="height100">
      <div class="fc-settings-page-content">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>
<script>
import Subheader from '@/Subheader'
import { mapGetters } from 'vuex'
import { isWebTabsEnabled } from '@facilio/router'
import { isUndefined } from '@facilio/utils/validation'

export default {
  components: {
    Subheader,
  },
  computed: {
    ...mapGetters('webtabs', [
      'getTabGroups',
      'isAppPrefEnabled',
      'tabHasPermission',
    ]),
    currentAccount() {
      return this.$account
    },
    appName() {
      return window.location.pathname.slice(1).split('/')[0]
    },
    parent() {
      return `/${this.appName}/personalsettings`
    },
    canShowProfile() {
      if (isWebTabsEnabled()) {
        return this.isAppPrefEnabled('canShowProfile')
      }
      return true
    },
    canShowNotifications() {
      if (isWebTabsEnabled()) {
        return this.isAppPrefEnabled('canShowNotifications')
      }
      return true
    },
    canShowAPIClient() {
      return (
        this.$helpers.isLicenseEnabled('DEVELOPER_SPACE') &&
        this.currentAccount.isDev
      )
    },
    canShowMfa() {
      if (this.$isMFAEnabled) {
        return true
      }
      return false
    },

    headerTabs() {
      let params = { app: this.appName }
      let tabs = [
        {
          label: this.$t('setup.setup_profile.edit_profile'),
          path: { name: 'editprofile', params },
          canShow: this.canShowProfile,
        },
        {
          label: this.$t('setup.changepassword.change_pw'),
          path: { name: 'userchangepassword', params },
          canShow: this.canShowProfile,
        },
        {
          label: this.$t('setup.setup.notifications'),
          path: { name: 'notifications', params },
          canShow: this.canShowNotifications,
        },
        {
          label: 'Multi Factor Authentication',
          path: { name: 'MFA', params },
          canShow: this.canShowMfa,
        },
        {
          label: this.$t('setup.setup.delegates'),
          path: { name: 'delegate', params },
        },
        {
          label: this.$t('profile.signature.signature'),
          path: { name: 'signature', params },
        },
        {
          label: this.$t('setup.setup.apiclient'),
          path: { name: 'apiclient', params },
          canShow: this.canShowAPIClient,
        },
        {
          label: this.$t('setup.setup.myapps'),
          path: { name: 'myapps', params },
          canShow: this.canShowProfile,
        },
      ]

      return tabs
    },
    subheaderMenu() {
      return this.headerTabs.filter(
        tab => isUndefined(tab.canShow) || tab.canShow
      )
    },
  },
}
</script>
<style>
.fc-form-title {
  font-weight: 500;
  padding-bottom: 20px;
}

.fc-settings-page-content {
  margin: 10px;
  background: #fff;
  height: 100%;
}
</style>
