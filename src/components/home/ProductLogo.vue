<template>
  <img v-if="orgCheckforLogoHide" src="" />
  <img
    class="fc-custom-logo"
    v-else-if="$org.id === 129 && $account.org.logoUrl"
    :src="$account.org.logoUrl"
    alt="Logo"
  />
  <img
    class="fc-custom-logo"
    v-else-if="$org.id === 146"
    src="~assets/facilio-pd7-technology-logo.svg"
    alt="Logo"
    style="width: 183px;object-fit: cover;"
  />
  <img
    class="fc-custom-logo"
    v-else-if="$org.id === 567"
    src="~assets/dalkia-logo.svg"
  />
  <img
    class="fc-custom-logo"
    v-else-if="$org.id === 360"
    src="~assets/facilio-netix-controls-logo.svg"
    alt="Logo"
  />
  <img
    class="fc-custom-logo"
    v-else-if="
      ($org.id === 1 && $org.domain === 'heeren') ||
        ($org.id === 321 && $account.user.email === 'jack@echoltech.com')
    "
    src="~assets/echo.png"
    alt="Logo"
  />
  <img
    class="fc-custom-logo"
    v-else-if="$org.id === 321 && $account.user.email === 'moro@facilio.com'"
    src="~assets/moro.png"
    alt="Logo"
  />
  <img
    v-else-if="
      $account.user.email.toLowerCase() ===
        'desiree.gonzalez+tutendemo@tutenlabs.com'
    "
    alt="Logo"
    style="height: 17px; position: relative; top: 0;"
    src="~assets/tutenlabs-poweredby-facilio.svg"
  />

  <img
    class="fc-custom-logo"
    v-else-if="$account.user.email === 'utcdemo'"
    src="~assets/utc-logo.png"
    alt="Logo"
  />
  <img
    class="fc-custom-logo"
    v-else-if="$account.user.email === 'tjacob@nxn.ae'"
    src="~assets/trans-1px.png"
    alt="Logo"
  />
  <img
    class="fc-custom-logo"
    style="max-height:28px"
    v-else-if="
      $org.id === 351 ||
        $org.id === 343 ||
        (($org.id === 210 || $org.id === 321) &&
          ($account.user.email === 'jasonsmith@sutherland.com' ||
            $account.user.email === 'lucascage@gmail.com' ||
            $account.user.email.toLowerCase() === 'sutherland@facilio.com'))
    "
    src="~assets/sutherland-white.svg"
    alt="Logo"
  />
  <img
    class="fc-custom-logo"
    v-else-if="
      isHoneywell && $account.data.orgInfo.toolbar_theme === 'honeywell'
    "
    src="~assets/honeywell-logo-white.svg"
    style=" max-height: 20px;"
    alt="Logo"
  />
  <img
    class="fc-custom-logo"
    v-else-if="isHoneywell"
    src="~assets/honeywell-logo.svg"
    style=" max-height: 20px;"
    alt="Logo"
  />
  <img
    v-else-if="isBuildingsTalk"
    src="~assets/buildingstalk_home_logo.png"
    style="height:28px;"
    alt="Logo"
  />

  <img
    v-else-if="isMoro"
    src="~assets/moro.png"
    class="moro-log-nav"
    alt="Logo"
  />

  <img
    class="fc-custom-logo"
    v-else-if="$org.id === 396"
    src="~assets/cit-facilio-logo.png"
    alt="Logo"
  />
  <img
    class="fc-logo fc-emi-view-logo"
    v-else-if="$org.id === 24 && $org.domain === 'emicool'"
    src="~assets/emi-view.png"
    alt="Logo"
  />
  <img class="fc-rebrand-logo" v-else :src="brandConfig.logoLight" alt="Logo" />
</template>
<script>
export default {
  props: ['isHoneywell', 'isBuildingsTalk', 'isMoro'],
  computed: {
    orgCheckforLogoHide() {
      const orgUid = this.$account?.user?.uid
      const userEmail = this.$account?.user?.email
      return (
        (orgUid === 22886 && userEmail === 'shane+cb@facilio.com') ||
        (orgUid === 22767 && userEmail === 'ashokkumar+cb@facilio.com') ||
        (orgUid === 22901 && userEmail === 'megan+NHS@facilio.com')
      )
    },
    brandConfig()
    {
      return window.brandConfig
    },
  },
  methods: {
    getWebPackPublicPath() {
      return window.webpackPublicPath
    },
  },
}
</script>
<style lang="scss">
.moro-log-nav {
  height: 30px;
  vertical-align: middle;
}
.fc-emi-view-logo {
  width: 40px;
  height: 45px;
  object-fit: contain;
}
</style>
