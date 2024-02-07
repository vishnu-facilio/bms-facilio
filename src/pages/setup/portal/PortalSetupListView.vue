<template>
  <div class="portal-setup">
    <div class="header-content">
      <div class="header-name">
        {{
          $t('common.header.portal_workcenter', {
            workcenter: !$validation.isEmpty(workCenters)
              ? $t('common.header.work_centers')
              : '',
          })
        }}
      </div>
      <div class="header-descrip">
        {{
          $t('common._common.list_of_all_portals_workcenters', {
            workcenter: !$validation.isEmpty(workCenters)
              ? $t('common.header.work_centers')
              : '',
          })
        }}
      </div>
    </div>
    <el-tabs v-model="selectedtab">
      <el-tab-pane :label="$t('setup.setup_profile.portal')" name="portal">
        <SetupLoader v-if="loading" class="m10 mT0">
          <template #setupLoading>
            <spinner :show="loading" size="80"></spinner>
          </template>
        </SetupLoader>
        <div
          v-else
          v-for="portal in portalApp"
          :key="portal.id"
          class="app-list visibility-visible-actions"
        >
          <div class="flex-middle">
            <div>
              <div class="app-name" @click="openSummary(portal)">
                {{ portal.name }}
              </div>
              <div class="app-link-name flex-middle">
                <div class="bold">{{ $t('common._common.link') }}:</div>
                <div>
                  <a
                    :href="`https://${portal.appDomain.domain}`"
                    target="_blank"
                    class="app-link f14 pL5"
                  >
                    {{ `https://${portal.appDomain.domain}` }}
                  </a>
                </div>
                <div
                  @click="copyLinkName(`https://${portal.appDomain.domain}`)"
                  class="pointer pL10 visibility-hide-actions"
                >
                  <inline-svg
                    src="svgs/link-copy"
                    iconClass="icon icon-sm-md vertical-bottom op5"
                  ></inline-svg>
                </div>
              </div>
            </div>
          </div>
          <button @click="openSummary(portal)" class="app-summary-btn">
            {{ $t('common._common.view_summary') }}
          </button>
        </div>
      </el-tab-pane>
      <el-tab-pane
        v-if="!$validation.isEmpty(workCenters)"
        :label="$t('common.header.work_centers')"
        name="woCenters"
      >
        <div
          v-for="app in workCenters"
          :key="app.id"
          class="app-list visibility-visible-actions"
        >
          <div>
            <div class="app-name" @click="openSummary(app)">
              {{ app.name }}
            </div>
            <div class="app-link-name flex-middle">
              <div class="bold">{{ $t('common._common.link') }}:</div>
              <div>
                <a
                  :href="`https://${app.appDomain.domain}/${app.linkName}`"
                  target="_blank"
                  class="app-link f14 pL5"
                >
                  {{ `https://${app.appDomain.domain}/${app.linkName}` }}
                </a>
              </div>
              <div
                @click="
                  copyLinkName(
                    `https://${app.appDomain.domain}/${app.linkName}`
                  )
                "
                class="pointer pL10 visibility-hide-actions"
              >
                <inline-svg
                  src="svgs/link-copy"
                  iconClass="icon icon-sm-md vertical-bottom op5"
                ></inline-svg>
              </div>
            </div>
          </div>
          <button @click="openSummary(app)" class="app-summary-btn">
            {{ $t('common._common.view_summary') }}
          </button>
        </div>
      </el-tab-pane>
      <el-tab-pane
        v-if="!$validation.isEmpty(tools)"
        :label="$t('common.header.tools')"
        name="tools"
      >
        <el-row
          v-for="app in tools"
          :key="app.id"
          class="app-list dataloader-padding"
        >
          <el-col :span="21">
            <el-row class="flex-middle">
              <el-col :span="2">
                <inline-svg
                  src="svgs/dataloader-icon"
                  iconClass="icon icon-80"
                ></inline-svg>
              </el-col>
              <el-col :span="22">
                <div class="app-name" @click="openSummary(app)">
                  {{ app.name }}
                </div>
                <div class="app-link-name flex-middle">
                  <div>
                    {{ $t('setup.dataloader.dataloader_desc') }}
                  </div>
                </div>
              </el-col>
            </el-row>
          </el-col>
          <el-col :span="3">
            <button @click="openSummary(app)" class="app-summary-btn">
              {{ $t('common._common.view_summary') }}
            </button>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import { loadApps, getFilteredApps } from 'util/appUtil'
import SetupLoader from 'pages/setup/components/SetupLoader'
export default {
  title() {
    return 'Portal & Work Center'
  },
  data() {
    return {
      loading: false,
      applications: [],
      selectedtab: 'portal',
    }
  },
  components: {
    SetupLoader,
  },
  async created() {
    this.loading = true

    let { error, data } = await loadApps()
    if (error) {
      this.$message.error(error.message || 'Error Occured')
    } else {
      this.applications = getFilteredApps(data)
    }
    this.loading = false
  },
  computed: {
    workCenters() {
      let {
        appCategory: { WORK_CENTERS },
      } = this.$constants

      let workCenters = this.filterApps(WORK_CENTERS) || []
      return workCenters.filter(i => i.domainType !== 6)
    },
    portalApp() {
      let {
        appCategory: { PORTALS },
      } = this.$constants

      return this.filterApps(PORTALS)
    },
    tools() {
      let {
        appCategory: { TOOLS },
      } = this.$constants

      return this.filterApps(TOOLS)
    },
  },
  methods: {
    filterApps(appCategoryVal) {
      return this.applications.filter(app => {
        let { appCategory } = app
        return appCategory === appCategoryVal
      })
    },
    openSummary({ id }) {
      this.$router.push({ path: `portal/summary/${id}` })
    },
    async copyLinkName(copy) {
      await navigator.clipboard.writeText(copy)
      this.$message({
        message: 'Copied - ' + copy,
        type: 'success',
      })
    },
  },
}
</script>
<style lang="scss">
.portal-setup {
  .header-content {
    padding: 25px 20px;
    background: #fff;

    .header-name {
      font-size: 18px;
      color: #000;
      letter-spacing: 0.7px;
      padding-bottom: 5px;
    }
    .header-descrip {
      font-size: 13px;
      color: grey;
      letter-spacing: 0.3px;
    }
  }
  .el-tabs__header {
    padding-left: 20px;
    background: #fff;
  }
  .app-list {
    padding: 25px;
    background: #fff;
    box-shadow: 0 4px 15px 0 rgba(14, 15, 43, 0.03);
    border: 1px solid #edf1f2;
    transition: all 0.6s cubic-bezier(0.165, 0.84, 0.44, 1);
    margin: 0px 20px 10px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .app-name {
      color: #324056;
      letter-spacing: 0.5px;
      font-size: 16px;
      font-weight: 500;
      line-height: 20px;
      cursor: pointer;
    }
    .app-summary-btn {
      font-weight: 500;
      padding: 10px;
      line-height: normal;
      border: 1px solid #d0d9e2;
      border-radius: 3px;
      background-color: #fff;
      letter-spacing: 0.3px;
      color: #324056;
      cursor: pointer;
    }
    .app-summary-btn:hover {
      background-color: #39b2c2;
      color: #fff;
      border: 1px solid #39b2c2;
      border-color: #39b2c2;
    }
  }
  .app-list:hover {
    border: 1px solid rgb(57 178 194 / 50%);
    background-color: #f5feff;
    transform: none;
  }
  .dataloader-padding {
    padding: 10px !important;
  }
}
</style>
