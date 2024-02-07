<template>
  <el-dialog
    title="App Launcher"
    width="85%"
    class="app-launcher-dialog-block"
    :visible="true"
    :close-on-click-modal="true"
    :before-close="close"
  >
    <div class="app-launcher-dialog-body d-flex flex-col">
      <spinner v-if="loading" :show="loading"></spinner>
      <div
        v-else-if="
          $validation.isEmpty(systemApps) && $validation.isEmpty(connectedApps)
        "
        class="mT100 self-center"
      >
        No apps or work centers available.
      </div>
      <template v-else>
        <template v-for="(section, key) in typeVsAppListHash">
          <div v-if="!$validation.isEmpty(section.apps)" :key="key">
            <p v-if="!isSystemAppsEmpty" class="fc-blue-label mL5 mB15 mT15">
              {{ section.label }}
            </p>
            <div class="d-flex flex-wrap">
              <a
                v-for="app in section.apps"
                :key="'system' + app.linkName"
                @click="() => goToApp(app, section)"
                :class="[
                  'app-launcher-box d-flex',
                  isCurrentApp(app.linkName) && 'app-active',
                ]"
              >
                <img
                  v-if="app.logoUrl"
                  class="app-launcher-icon"
                  :src="$prependBaseUrl(app.logoUrl)"
                  :style="{ backgroundColor: getAppIconBg(app) }"
                />

                <inline-svg
                  v-else-if="getAppIcon(app)"
                  :src="getAppIcon(app)"
                  class="app-launcher-icon"
                  iconClass="icon icon-60 p10"
                  :style="{ backgroundColor: getAppIconBg(app) }"
                />

                <inline-svg
                  v-else
                  src="product-icons/app-white"
                  class="app-launcher-icon"
                  iconClass="icon icon-60 p5"
                  :style="{ backgroundColor: getAppIconBg({}) }"
                />
                <div>
                  <div class="fc-black3-16 bold">{{ app.name }}</div>
                  <div class="label-txt-black2 mT5 line-height18 app-desc">
                    {{ app.description }}
                  </div>
                </div>
              </a>
            </div>
          </div>
        </template>
      </template>
    </div>
  </el-dialog>
</template>
<script>
import Spinner from '@/Spinner'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

const appDetails = {
  app: { icon: 'svgs/apps/facilio', color: '#4e35a8' },
  iot: { icon: 'svgs/apps/agent', color: '#7881f3' },
  operations: { icon: 'svgs/apps/operations', color: '#f07575' },
  service: { icon: 'svgs/apps/portal', color: '#2ec0b9' },
  tenant: { icon: 'svgs/apps/tenant', color: '#f36ea5' },
  vendor: { icon: 'svgs/apps/vendor', color: '#fdc63b' },
  cafm: { icon: 'svgs/apps/facilio', color: '#4e35a8' },
  workplace: { icon: 'svgs/apps/workplace', color: '#66B8F0' },
  iwms: { icon: 'svgs/apps/workplace', color: '#66B8F0' },
}

export default {
  props: ['close'],
  components: { Spinner },
  data() {
    return {
      connectedApps: [],
      systemApps: [],
      loading: true,
    }
  },
  async created() {
    this.loading = true
    await Promise.all([this.loadConnectedApps(), this.loadSystemApps()])
    this.loading = false
  },
  computed: {
    typeVsAppListHash() {
      return {
        workcenters: {
          label: 'Work Centers',
          apps: this.workcenterApps,
          getRoute(app) {
            return { path: `/${app.linkName}` }
          },
        },
        portals: {
          label: 'Portals',
          apps: this.portalApps,
          getRoute(app) {
            let { linkName, appDomain } = app
            let { domain } = appDomain || {}

            return appDetails[linkName]
              ? { href: `https://${domain}`, external: true }
              : { path: `/${linkName}` }
          },
        },
        connected: {
          label: 'Connected Apps',
          apps: this.connectedApps,
          getRoute(app) {
            return { path: '/app/connectedapp/' + app.linkName }
          },
        },
      }
    },
    workcenterApps() {
      let {
        systemApps,
        $org: { id: orgId },
        $constants: {
          appCategory: { WORK_CENTERS, FEATURE_GROUPING },
        },
      } = this

      return systemApps.filter(app => {
        if ([WORK_CENTERS, FEATURE_GROUPING].includes(app.appCategory)) {
          if (app.linkName === 'app' && orgId === 429) {
            return false
          } else {
            return true
          }
        }
        return false
      })
    },
    portalApps() {
      let {
        appCategory: { PORTALS },
      } = this.$constants

      return this.systemApps.filter(
        app => app.appCategory === PORTALS && app.linkName !== 'client'
      )
    },
    isSystemAppsEmpty() {
      return isEmpty(this.portalApps) && isEmpty(this.workcenterApps)
    },
  },
  methods: {
    async loadConnectedApps() {
      let filters = {
        showInLauncher: { operatorId: 15, value: ['true'] },
        isActive: { operatorId: 15, value: ['true'] },
      }

      let { data, error } = await API.get('v2/connectedApps/all', {
        filters: JSON.stringify(filters),
      })
      if (!error) {
        this.connectedApps = data.connectedApps || []
      }
    },
    async loadSystemApps() {
      let { data, error } = await API.get(
        `v2/application/list?fetchMyApps=true`
      )
      if (!error) {
        this.systemApps = data.application || []

        // temp till newapp is not required
        this.systemApps.forEach(app => {
          if ('newapp' === app.linkName) app.linkName = 'app'
        })
      }
    },
    getAppIcon({ linkName }) {
      return appDetails[linkName] ? appDetails[linkName].icon : null
    },
    getAppIconBg({ linkName }) {
      return appDetails[linkName] ? appDetails[linkName].color : '#a3cc85'
    },
    openAppLink(route) {
      if (route.external) {
        window.open(route.href, '_blank')
      } else {
        let { href } = this.$router.resolve(route)
        window.open(href, '_blank')
      }
    },
    isCurrentApp(linkName) {
      let appNameFromUrl =
        window.location.pathname.slice(1).split('/')[0] || null
      return appNameFromUrl === linkName
    },
    goToApp(app, section) {
      this.isCurrentApp(app.linkName)
        ? this.close()
        : this.openAppLink(section.getRoute(app))
    },
  },
}
</script>
<style lang="scss" scoped>
.app-launcher-dialog-body {
  min-height: 350px;
  max-height: 530px;
  overflow: scroll;
  background: #fff;

  .app-launcher-box {
    width: 360px;
    height: 100px;
    margin: 8px;
    padding: 20px;
    background-color: #ffffff;
    border: 1px solid #ebedf4;
    border-radius: 5px;
    box-shadow: 0 2px 7px 0 rgba(223, 226, 232, 0.47);
    position: relative;
  }
  .app-launcher-box:hover {
    border: 1px solid #ef508f;
    box-shadow: 0 2px 7px 0 rgba(223, 226, 232, 0.47);
    cursor: pointer;
  }
  .app-launcher-box.app-active:after {
    position: absolute;
    top: 0;
    right: 0;
    content: '\2713';
    width: 40px;
    height: 40px;
    background: linear-gradient(to top right, transparent 50%, #6bca24 0) top
        right/40px 40px no-repeat,
      #fff;
    border-top-right-radius: 4px;
    text-align: end;
    color: #fff;
    font-weight: 600;
    font-size: 14px;
    padding: 4px 7px;
  }

  img.app-launcher-icon {
    width: 58px;
  }
  .app-launcher-icon {
    border: none;
    border-radius: 5px;
    margin-right: 15px;
    svg {
      fill: #fff;
    }
  }

  .app-desc {
    -webkit-line-clamp: 2;
    overflow: hidden;
    -webkit-box-orient: vertical;
    display: -webkit-box;
    word-break: break-word;
  }
}
</style>
<style lang="scss">
.app-launcher-dialog-block {
  .el-dialog {
    max-width: 1200px;
  }
  .el-dialog__header {
    padding: 13px 20px 12px;
    border-bottom: 1px solid #ebedf4;
  }
  .el-dialog__headerbtn {
    top: 15px;
  }
  .el-dialog__title {
    color: #324056;
  }
  .el-dialog__body {
    padding: 0 20px 20px;
  }
  .app-launcher-icon svg {
    fill: #fff;
  }
}
</style>
