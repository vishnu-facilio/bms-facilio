<template>
  <div v-if="!hasMetaLoaded">
    <loader></loader>
  </div>
  <div v-else class="formbuilder-fullscreen-popup">
    <div class="setting-header2 position-relative">
      <div class="setting-title-block">
        <div class="setting-form-title mT10 mL15">
          <img class="fc-rebrand-logo" :src="brandConfig.logoLight" />
        </div>
      </div>
      <div
        class="d-flex flex-direction-column justify-content-center mR30"
        @click="goBack"
      >
        <div class="cursor-pointer">
          <inline-svg
            src="left-arrow"
            iconClass="icon fill-white vertical-bottom mR10"
          ></inline-svg>
          <span class="back-text">Back</span>
        </div>
      </div>
    </div>
    <router-view :isApp="isApp"></router-view>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { isWebTabsEnabled } from '@facilio/router'
import HomeMixin from 'pages/HomeMixin.js'

export default {
  mixins: [HomeMixin],
  async created() {
    await this.$store.dispatch('getCurrentAccount')
    await this.$store.dispatch('getFeatureLicenses')
    await this.initializeMeta()
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadOrgs')
  },
  mounted() {
    try {
      if (this.cssVariables) {
        for (let cssvar in this.cssVariables) {
          if (document.documentElement && document.documentElement.style) {
            document.documentElement.style.setProperty(
              cssvar,
              this.cssVariables[cssvar]
            )
          }
        }
      }
      // eslint-disable-next-line no-empty
    } catch (err) {}
  },
  computed: {
    isApp() {
      let { $route } = this
      let { meta } = $route
      if (!isEmpty(meta)) {
        return !!meta.isApp
      }
      return false
    },
    brandConfig()
    {
      return window.brandConfig
    },
  },
  methods: {
    goBack() {
      if (isWebTabsEnabled()) {
        let {
          params: { app },
        } = this.$route
        this.$router.push({ path: `/${app}` })
      } else {
        this.$router.push({
          path: '/app/home/dashboard',
        })
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.setting-header2 {
  height: 50px;
  padding: 0;
  box-shadow: 0 2px 12px 0 rgba(155, 157, 180, 0.1);
  background-color: var(--fc-toolbar-bg);
  border-color: #e6ecf3;
  border-width: 0 0 1px 0px;
  border-style: solid;
  .back-text {
    font-size: 14px;
    letter-spacing: 0.3px;
    color: #fff;
  }
}
</style>
