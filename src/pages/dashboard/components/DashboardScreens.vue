<template>
  <div class="height100">
    <div class="setting-header2 dashboard-screen-header">
      <div class="setting-title-block">
        <div class="setting-form-title pT10">
          {{ $t('panel.remote.screens') }}
        </div>
      </div>
      <template v-if="activeName && activeName === 'screens'">
        <portal to="screens">
          <div class="action-btn setting-page-btn" v-if="hasCreatePermission">
            <add-screen
              :defaultIntervals="defaultIntervals"
              :dashboards="dashboardTree"
              @save="loadScreens"
            >
              <el-button slot="reference" type="primary" class="pink-el-btn">{{
                $t('panel.remote.add')
              }}</el-button>
            </add-screen>
          </div>
        </portal>
      </template>
      <template v-else>
        <div class="action-btn setting-page-btn">
          <add-screen
            :defaultIntervals="defaultIntervals"
            :dashboards="dashboardTree"
            @save="loadScreens"
          >
            <el-button slot="reference" type="primary" class="setup-el-btn">{{
              $t('panel.remote.add')
            }}</el-button>
          </add-screen>
        </div>
      </template>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout dashboard-screens-scroll">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table screen-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text"></th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('panel.remote.screen_name') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('panel.remote.dashboard') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('panel.remote.interval') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('panel.remote.remote_connect') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="failed">
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('panel.remote.failed') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="!screens || !screens.length">
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('panel.remote.none_created') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(screen, index) in screens"
                :key="index"
              >
                <td style="width: 5%;">
                  <i
                    class="launch-screen fa fa-play-circle"
                    @click="launchScreen(screen)"
                    title="Launch"
                    data-arrow="true"
                    v-tippy
                  ></i>
                </td>
                <td>
                  <div class="screen-name" v-if="!screen.editName">
                    {{ screen.name }}
                    <i
                      v-if="hasDashboardEditPermission"
                      class="el-icon-edit pointer"
                      @click="screen.editName = true"
                      style="padding: 0 5px;"
                    ></i>
                  </div>
                  <div class="screen-name" v-else>
                    <textarea
                      @change="updateScreen(screen)"
                      @blur="screen.editName = false"
                      @keydown.enter.exact.prevent
                      @keyup.enter.exact="screen.editName = false"
                      autofocus="true"
                      autocomplete="off"
                      v-model="screen.name"
                      class="screen-editable-name"
                    ></textarea>
                  </div>
                </td>
                <td>
                  <dashboard-selector
                    :screen="screen"
                    :dashboards="dashboardTree"
                    @update="updateScreen(screen)"
                  >
                    <el-tag
                      size="small"
                      class="fc-tag-pink"
                      slot="reference"
                      v-if="screen.screenDashboards"
                      >{{ screenDashboard(screen) }}</el-tag
                    >
                    <el-tag
                      size="small"
                      class="fc-tag-pink"
                      slot="reference"
                      v-else
                      >0 {{ $t('panel.remote.dash') }}</el-tag
                    >
                  </dashboard-selector>
                </td>
                <td>
                  <div class="screen-interval" v-if="!screen.editInterval">
                    {{ screen.intervalMinutes }}
                    {{ $t('panel.remote.minutes') }}
                    <i
                      v-if="hasDashboardEditPermission"
                      class="el-icon-edit pointer"
                      @click="screen.editInterval = true"
                      style="padding: 0 5px;"
                    ></i>
                  </div>
                  <div class="screen-interval" v-else>
                    <el-select
                      @change="
                        updateScreen(screen)
                        screen.editInterval = false
                      "
                      v-model="screen.intervalMinutes"
                      :filterable="true"
                      placeholder="Select Interval"
                      style="width: 80px;"
                    >
                      <el-option
                        v-for="(ival, idx) in defaultIntervals"
                        :key="idx"
                        :label="ival"
                        :value="ival"
                      ></el-option>
                    </el-select>
                  </div>
                </td>
                <td>
                  <remote-screen-caster :screen="screen" :screens="screens">
                    <el-tag
                      slot="reference"
                      class="fc-tag-green"
                      size="small"
                      v-if="!screen.remoteScreens"
                      >0 {{ $t('panel.remote.remote_screen') }}</el-tag
                    >
                    <el-tag
                      slot="reference"
                      class="fc-tag-green"
                      size="small"
                      v-else
                      >{{ remoteScreens(screen) }}</el-tag
                    >
                  </remote-screen-caster>
                </td>
                <td style="width: 15%; padding-left: 0;">
                  <div
                    class="text-left actions screen-actions"
                    style="margin-top:0px;margin-right: 15px;text-align:center; cursor: pointer;display: flex;"
                  >
                    <el-popover
                      placement="bottom"
                      trigger="click"
                      style="border-radius=5px;"
                      v-if="hasDashboardEditPermission"
                    >
                      <div class="p15">
                        <el-checkbox
                          style="margin:6px;"
                          v-model="screenSettings.titleVisibility"
                          @change="
                            updateScreen(screen, screenSettings.titleVisibility)
                          "
                        >
                          <center>{{ $t('panel.remote.title') }}</center>
                        </el-checkbox>
                        <div class="mT15">
                          <p class="grey-text2">
                            {{ $t('panel.remote.site') }}
                          </p>
                          <el-select
                            @change="updateScreen(screen)"
                            v-model="screen.siteId"
                            :filterable="true"
                            :clearable="true"
                            placeholder="Select site"
                            style="width: 100%"
                            class="fc-input-full-border2"
                          >
                            <el-option label="None" :value="-99"></el-option>
                            <el-option
                              v-for="(site, idx) in sites"
                              :key="idx"
                              :label="site.name"
                              :value="site.id"
                            ></el-option>
                          </el-select>
                        </div>
                      </div>
                      <i
                        class="el-icon-setting pointer"
                        @click="
                          screenSettings.titleVisibility =
                            screen.screenSetting &&
                            typeof screen.screenSetting['titleVisibility'] !==
                              'undefined'
                              ? screen.screenSetting['titleVisibility']
                              : true
                        "
                        slot="reference"
                        data-arrow="true"
                      ></i>
                    </el-popover>
                    <i
                      v-if="hasDeletePermission"
                      class="el-icon-delete pointer screen-delete-icon"
                      title="Delete"
                      data-arrow="true"
                      v-tippy
                      @click="deleteScreen(index, screen)"
                    ></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <div
      ref="launchScreenContainer"
      class="launch-screen-container"
      style="background:white"
      v-if="currentLaunchedScreen"
    >
      <dashboard-loop-runner
        :remoteScreen="currentLaunchedScreen"
      ></dashboard-loop-runner>
      <div class="chart-tooltip" style="visibility: hidden; opacity: 0;"></div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import AddScreen from './AddScreen'
import DashboardSelector from './DashboardSelector'
import RemoteScreenCaster from './RemoteScreenCaster'
import DashboardLoopRunner from 'tv/DashboardLoopRunner'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'
import DashboardPermission from 'src/pages/new-dashboard/utils/DashboardPermissions.js'
export default {
  props: ['module', 'activeName'],
  mixins:[DashboardPermission],
  components: {
    AddScreen,
    DashboardSelector,
    RemoteScreenCaster,
    DashboardLoopRunner,
  },
  data() {
    return {
      loading: true,
      failed: false,
      screens: null,
      showDeleteDialog: false,
      dashboardTree: null,
      addDashboard: null,
      defaultIntervals: [2, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60],
      currentLaunchedScreen: null,
      screenSettings: {
        titleVisibility: true,
      },
    }
  },
  mounted() {
    this.loadScreens()
    this.loadDashboards()
  },
  computed: {
    ...mapState({
      sites: state => state.site,
    }),
  },
  methods: {
    screenDashboard(screen) {
      return screen.screenDashboards.length <= 1
        ? screen.screenDashboards.length + ' Dashboard'
        : screen.screenDashboards.length + ' Dashboards'
    },
    remoteScreens(screen) {
      return screen.remoteScreens.length <= 1
        ? `${screen.remoteScreens.length} remote screen`
        : `${screen.remoteScreens.length} remote screens`
    },
    loadDashboards() {
      let self = this
      // let url ='/dashboardWithFolder'
      let url = 'v3/dashboard/list'
      let params = { appId: getApp().id, withSharing: true }
      API.get(url, params).then(function(response) {
        self.dashboardTree = response.data.dashboardFolders.filter(
          rt => rt.dashboards !== null
        )
      })
    },
    loadScreens() {
      let self = this
      self.loading = true
      self.failed = false
      self.$http
        .get('/screen/getAllScreens')
        .then(function(response) {
          let screenList = response.data.screenContexts
          if (screenList && screenList.length) {
            for (let screen of screenList) {
              screen.editName = false
              screen.editInterval = false
              screen.intervalMinutes = screen.interval / 60
            }
          }
          self.screens = screenList
          self.loading = false
        })
        .catch(function(error) {
          self.loading = false
          if (error) {
            self.failed = true
          }
        })
    },
    deleteScreen(index, screen) {
      let self = this
      let cstatus = confirm(
        'Are you sure want to delete "' + screen.name + '" screen?'
      )
      if (cstatus) {
        self.$http
          .post('/screen/deleteScreen', {
            screenContext: {
              id: screen.id,
            },
          })
          .then(function(response) {
            self.screens.splice(index, 1)
          })
      }
    },
    updateScreen(screen, titleVisibility) {
      let screenSetting = screen.screenSetting ? screen.screenSetting : {}
      if (typeof titleVisibility !== 'undefined') {
        screenSetting.titleVisibility = titleVisibility
      }
      let self = this
      let updateData = {
        screenContext: {
          id: screen.id,
          orgId: screen.orgId,
          name: screen.name,
          interval: screen.intervalMinutes * 60,
          screenDashboards: [],
          screenSettingString: JSON.stringify(screenSetting),
          siteId: screen.siteId || -99,
        },
      }
      let sequence = 1
      for (let db of screen.screenDashboards) {
        updateData.screenContext.screenDashboards.push({
          dashboard: null,
          dashboardId: db.dashboardId,
          id: db.id,
          screenId: db.screenId,
          sequence: sequence,
          spaceId: db.spaceId,
        })
        sequence = sequence + 1
      }
      self.$http.post('/screen/updateScreen', updateData)
    },
    launchScreen(screen) {
      let self = this
      self.currentLaunchedScreen = {
        id: 1,
        name: screen.name,
        screenId: screen.id,
        screenContext: screen,
      }

      this.$nextTick(() => {
        let screenElement = self.$refs['launchScreenContainer']
        if (screenElement) {
          if (screenElement.requestFullscreen) {
            screenElement.requestFullscreen()
          } else if (screenElement.mozRequestFullScreen) {
            screenElement.mozRequestFullScreen()
          } else if (screenElement.webkitRequestFullscreen) {
            screenElement.webkitRequestFullscreen()
          } else if (screenElement.msRequestFullscreen) {
            screenElement.msRequestFullscreen()
          }
          this.applyDashboardFullScreenChanges()
          let fullscreenExitHandler = function() {
            if (
              !document.webkitIsFullScreen &&
              !document.mozFullScreen &&
              !document.msFullscreenElement
            ) {
              self.currentLaunchedScreen = null

              document.removeEventListener(
                'webkitfullscreenchange',
                fullscreenExitHandler
              )
              document.removeEventListener(
                'mozfullscreenchange',
                fullscreenExitHandler
              )
              document.removeEventListener(
                'fullscreenchange',
                fullscreenExitHandler
              )
              document.removeEventListener(
                'MSFullscreenChange',
                fullscreenExitHandler
              )
            }
          }
          if (document.addEventListener) {
            document.addEventListener(
              'webkitfullscreenchange',
              fullscreenExitHandler,
              false
            )
            document.addEventListener(
              'mozfullscreenchange',
              fullscreenExitHandler,
              false
            )
            document.addEventListener(
              'fullscreenchange',
              fullscreenExitHandler,
              false
            )
            document.addEventListener(
              'MSFullscreenChange',
              fullscreenExitHandler,
              false
            )
          }
        }
      })
    },
    applyDashboardFullScreenChanges() {
      document.body.classList.add('dashboard-widget-view')
      if (document.getElementsByClassName('single-dashboard').length === 1) {
        if (document.getElementsByClassName('fc-widget-header').length) {
          document.getElementsByClassName('fc-widget-header')[0].style.display =
            'none'
        }
      }
    },
  },
}
</script>

<style>
.launch-screen {
  font-size: 28px;
  color: #39b2c2;
  opacity: 0.5;
}

.tablerow:hover .launch-screen {
  opacity: 1;
}

.tablerow:hover .el-icon-edit {
  visibility: visible;
}

.screen-name .el-icon-edit,
.screen-interval .el-icon-edit {
  opacity: 0;
}

.screen-name:hover .el-icon-edit,
.screen-interval:hover .el-icon-edit {
  opacity: 1;
}

.screen-name,
.screen-interval {
  white-space: nowrap;
}

.dashboard-selector .left-panel,
.dashboard-selector .right-panel {
  padding: 15px;
}

.dashboard-selector .left-panel {
  border-right: 1px solid #f4f4f4;
}

.flip-list-move {
  transition: transform 0.5s;
}

.no-move {
  transition: transform 0s;
}

.ghost {
  opacity: 0.5;
  background: #c8ebfb;
}

.list-group {
  min-height: 20px;
}

.list-group-item {
  cursor: move;
}

.list-group-item i {
  cursor: pointer;
}

.dashboard-selector ul.list-group {
  list-style: none;
  padding: 0;
  margin: 0;
}

.dashboard-selector ul.list-group li {
  margin: 5px 0;
  padding: 10px;
  background: #f4f3f3;
  border-radius: 2px;
}

.dashboard-selector {
  padding: 10px;
}

.dashboard-selector .add-dashboard {
  margin-top: 30px;
}

.launch-screen-container:-webkit-full-screen {
  width: 100%;
  height: 100%;
  margin: 0;
  background: #fff;
}

.fc-white-theme .launch-screen-container:-webkit-full-screen {
  background: #fff;
}

.fc-black-theme .launch-screen-container:-webkit-full-screen {
  background: transparent;
}

.launch-screen-container:-moz-full-screen {
  margin: 0;
}

.launch-screen-container:-ms-fullscreen {
  margin: 0;
  height: 100%;
}

.launch-screen-container:fullscreen {
  margin: 0;
  height: 100%;
}

.dashboard-screens-scroll {
  height: calc(100vh - 275px);
  overflow-y: scroll;
  padding: 0 !important;
  padding-bottom: 100px;
}

.screen-actions .el-icon-setting {
  font-size: 18px;
}

.screen-delete-icon {
  margin-left: 10px;
}

.dashboard-screen-header {
  padding: 10px 0px 20px !important;
}
.pink-el-btn {
  background: #ef508f;
  border-color: #ef508f;
  font-size: 12px;
  text-transform: uppercase;
  font-weight: bold;
  letter-spacing: 0.4px;
}
</style>
