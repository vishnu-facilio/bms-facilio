<template>
  <TabsAndLayouts
    :appId="appId"
    :loading="loadingApps"
    :currentAppId="currentAppId"
    :isActive="true"
    @reloadApp="showReloadApp = true"
  >
    <div class="header-container">
      <div>
        <div class="header-title">
          {{ $t('common._common.tabs_and_layouts') }}
        </div>
        <div class="header-content">
          {{ $t('common._common.list_of_tabs_and_layout') }}
        </div>
      </div>

      <div class="d-flex">
        <template>
          <div
            v-if="loadingApps"
            class="loading-shimmer width200px height40 bR5 mR15"
          ></div>

          <el-select
            v-else
            v-model="appId"
            placeholder="Select App"
            filterable
            class="fc-input-full-border2 width200px pR15"
          >
            <el-option
              v-for="app in availableApps"
              :key="app.linkName"
              :label="app.name"
              :value="app.id"
            >
            </el-option>
          </el-select>
        </template>

        <portal-target name="header-buttons"></portal-target>
      </div>
    </div>
  </TabsAndLayouts>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import TabsAndLayouts from './TabsAndLayouts'
import { getApp } from '@facilio/router'
import { getFilteredApps, loadApps } from 'util/appUtil'

export default {
  components: { TabsAndLayouts },
  data() {
    return {
      loadingApps: false,
      availableApps: [],
      showReloadApp: false,
    }
  },

  async created() {
    await this.loadAvailableApps()
    if (isEmpty(this.appId)) {
      let { availableApps } = this
      this.appId = (availableApps[0] || {}).id
    }
  },

  beforeRouteLeave(to, from, next) {
    if (this.showReloadApp) {
      let path = this.$router.resolve({ path: to.path }).href
      window.location.href = path
    } else {
      next()
    }
  },

  computed: {
    appId: {
      get() {
        let { params } = this.$route
        let { appId } = params || {}
        return appId ? Number(appId) : null
      },
      set(value) {
        let { query } = this.$route
        this.$router
          .replace({ params: { appId: value }, query })
          .catch(() => {})
      },
    },
    currentAppId() {
      return (getApp() || {}).id
    },
  },

  methods: {
    async loadAvailableApps() {
      this.loadingApps = true

      let { error, data } = await loadApps()
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let {
          appCategory: { FEATURE_GROUPING },
        } = this.$constants
        this.availableApps = getFilteredApps(data).filter(
          app =>
            app.linkName !== 'newapp' && app.appCategory === FEATURE_GROUPING
        )
      }

      this.loadingApps = false
    },
  },
}
</script>
