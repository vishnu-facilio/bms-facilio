<template>
  <div class="page-container">
    <div class="header-container">
      <div class="header-title">
        <div
          class="fc-black3-13 f16 pointer fwBold vertical-text-super"
          @click="back()"
          :title="$t('setup.translation.back')"
          v-tippy="{
            arrow: true,
            arrowType: 'round',
            animation: 'shift-away',
            placement: 'top',
          }"
        >
          <i class="el-icon-back f16 fwBold "></i>
        </div>
        <el-divider direction="vertical" class="mR15 mL15"></el-divider>
        {{ $t('setup.translation.title') }}
      </div>
      <template>
        <div
          v-if="loadingApps"
          class="loading-shimmer width200px height40 bR5"
        ></div>
        <div v-else class="d-flex">
          <portal-target name="layout-switch"></portal-target>
          <el-select
            v-model="appId"
            placeholder="Select App"
            filterable
            class="fc-input-full-border2 width200px pL15"
          >
            <el-option
              v-for="app in availableApps"
              :key="app.linkName"
              :label="app.name"
              :value="app.id"
            >
            </el-option>
          </el-select>
        </div>
      </template>
    </div>
    <Spinner v-if="loadingApps" :show="loadingApps" size="80"></Spinner>
    <TranslationTable v-else :appId="appId" />
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import TranslationTable from './TranslationTable'
import { getFilteredApps, loadApps } from 'util/appUtil'
import { getApp } from '@facilio/router'
import Spinner from '@/Spinner'

export default {
  data() {
    return {
      loadingApps: false,
      availableApps: [],
    }
  },
  components: {
    TranslationTable,
    Spinner,
  },
  async created() {
    await this.loadAvailableApps()
    if (isEmpty(this.appId)) {
      let { availableApps } = this
      this.appId = (availableApps[0] || {}).id
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
    back() {
      this.$router.go(-1)
    },
    async loadAvailableApps() {
      this.loadingApps = true

      let { error, data } = await loadApps()
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.availableApps = getFilteredApps(data)
      }
      this.loadingApps = false
    },
  },
}
</script>

<style lang="scss" scoped>
.page-container {
  display: flex;
  height: 100%;
  flex-direction: column;
}

.header-container {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
  align-items: center;
  height: 80px;
  padding: 15px;
  background-color: white;

  .header-title {
    font-size: 18px;
    letter-spacing: 0.5px;
    color: #324056;
    display: flex;
  }

  .header-content {
    font-size: 13px;
    letter-spacing: 0.5px;
    color: #808080;
  }
}
</style>
