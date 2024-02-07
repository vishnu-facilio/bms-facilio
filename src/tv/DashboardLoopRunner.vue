<template>
  <div
    class="no-screens-casted"
    v-if="!remoteScreen.screenId || remoteScreen.screenId < 0"
  >
    {{ $t('panel.no_screen') }}
  </div>
  <div
    class="no-screens-casted"
    v-else-if="
      !remoteScreen.screenContext.screenDashboards ||
        !remoteScreen.screenContext.screenDashboards.length
    "
  >
    {{ $t('panel.no_dashboard') }}
  </div>
  <div v-else-if="slideShow.currentDashboard" @mousemove="showControlsToolbar">
    <transition name="toolbarfade">
      <div class="screen-controls-toolbar" v-show="controlsToolbar.show">
        <i
          class="fa fa-backward"
          @click="previous"
          :class="{ disabled: !previousDashboardName }"
          :title="
            previousDashboardName
              ? 'Previous: ' + previousDashboardName
              : 'Previous'
          "
          v-tippy
          data-arrow="true"
        />
        <i
          class="fa fa-pause-circle playpause"
          @click="slideShow.pause = true"
          v-if="!slideShow.pause"
          title="Pause"
          v-tippy
          data-arrow="true"
        />
        <i
          class="fa fa-play-circle playpause"
          @click="slideShow.pause = false"
          v-else
          title="Play"
          v-tippy
          data-arrow="true"
        />
        <i
          class="fa fa-forward"
          @click="next"
          :class="{ disabled: !nextDashboardName }"
          :title="nextDashboardName ? 'Next: ' + nextDashboardName : 'Next'"
          v-tippy
          data-arrow="true"
        />
      </div>
    </transition>
    <div v-if="showDashboard" @click="openFullScreen">
      <component
        :is="whichDashboardViewer"
        :currentDashboard="slideShow.currentDashboard"
        :refresh="refresh"
        :isTv="true"
        @dashboardLoaded="getDashboardobj"
      >
        <el-popover
          slot="dashboardNameList"
          placement="bottom"
          width="300"
          v-model="toggle"
          popper-class="dashboard-switcher"
          trigger="click"
        >
          <ul>
            <li
              @click="openDashBoard(index)"
              v-for="(Dashboard, index) in this.remoteScreen.screenContext
                .screenDashboards"
              :key="index"
              :class="{ active: slideShow.currentDashboardIndex === index }"
            >
              {{ Dashboard.dashboard.dashboardName }}
            </li>
          </ul>
          <span slot="reference"
            >{{ slideShow.currentDashboard.dashboardName }}
            <i
              class="el-icon-arrow-down el-icon-arrow-down-tv"
            ></i></span></el-popover
      ></component>
    </div>
  </div>
</template>

<script>
import OldDashboardViewer from 'src/pages/dashboard/DashboardViewer.vue'
import NewDashboardViewer from 'src/pages/new-dashboard/components/dashboard/DashboardViewer.vue'
export default {
  props: ['remoteScreen'],
  components: {
    NewDashboardViewer,
    OldDashboardViewer,
  },
  data() {
    return {
      showDashboard: true,
      refresh: true,
      toggle: false,
      slideShow: {
        currentDashboardIndex: 0,
        currentDashboard: null,
        loopInterval: null,
        pause: false,
      },
      controlsToolbar: {
        show: false,
        autoHideTimeout: 5000,
        autoHideTimeoutObj: null,
      },
    }
  },
  mounted() {
    this.initLoopRunner()
  },
  destroyed() {
    this.clearLoopRunner()
  },
  computed: {
    whichDashboardViewer() {
      const isNewDashboardEnabled = this.$helpers.isLicenseEnabled(
        'NEW_DASHBOARD_FLOW'
      )
      return isNewDashboardEnabled ? 'NewDashboardViewer' : 'OldDashboardViewer'
    },
    previousDashboardName() {
      let prevIndex = this.slideShow.currentDashboardIndex - 1
      if (prevIndex >= 0) {
        let dbProps = this.getDashboardProps(prevIndex)
        return dbProps.dashboardName
      }
      return null
    },
    nextDashboardName() {
      let nextIndex = this.slideShow.currentDashboardIndex + 1
      if (nextIndex < this.remoteScreen.screenContext.screenDashboards.length) {
        let dbProps = this.getDashboardProps(nextIndex)
        return dbProps.dashboardName
      }
      return null
    },
  },
  watch: {
    remoteScreen: {
      handler: function() {
        this.reload()
      },
      deep: true,
    },
  },
  methods: {
    getDashboardobj(dashboard) {
      if (dashboard && dashboard.children && dashboard.children.length) {
        this.applyDashboardFullScreenChanges()
      }
    },
    reload() {
      this.initLoopRunner()
    },
    openDashBoard(currentDashboardIndex) {
      this.slideShow.currentDashboard = this.getDashboardProps(
        currentDashboardIndex
      )
      this.slideShow.currentDashboardIndex = currentDashboardIndex
      this.toggle = false
    },
    initLoopRunner() {
      let self = this
      self.clearLoopRunner()

      if (
        this.remoteScreen.screenId &&
        this.remoteScreen.screenId > 0 &&
        this.remoteScreen.screenContext.screenDashboards &&
        this.remoteScreen.screenContext.screenDashboards.length
      ) {
        this.slideShow.currentDashboard = this.getDashboardProps(0)
        let interval = this.remoteScreen.screenContext.interval * 1000
        this.slideShow.loopInterval = setInterval(function() {
          try {
            self.applyDashboardFullScreenChanges()
            if (!self.slideShow.pause) {
              self.next()
            }
            // eslint-disable-next-line no-empty
          } catch (err) {}
        }, interval)
      }
    },
    applyDashboardFullScreenChanges() {
      if (document.getElementsByClassName('single-dashboard').length === 1) {
        if (!document.getElementsByClassName('dashboard-widget-view').length) {
          document.body.classList.add('dashboard-widget-view')
        }
        if (document.getElementsByClassName('fc-widget-header').length) {
          document.getElementsByClassName('fc-widget-header')[0].style.display =
            'none'
        }
      }
    },
    removeDashboardFullScreenChanges() {
      document.body.classList.remove('dashboard-widget-view')
      if (document.getElementsByClassName('single-dashboard').length === 1) {
        if (document.getElementsByClassName('fc-widget-header').length) {
          document.getElementsByClassName('fc-widget-header')[0].style.display =
            'flex'
        }
      }
    },
    clearLoopRunner() {
      if (this.slideShow.loopInterval) {
        clearInterval(this.slideShow.loopInterval)
        this.removeDashboardFullScreenChanges()
      }

      this.slideShow.currentDashboardIndex = 0
      this.slideShow.currentDashboard = null
      this.slideShow.pause = false
    },
    next() {
      this.showDashboard = false
      const self = this
      this.$nextTick(() => {
        self.slideShow.currentDashboardIndex =
          self.slideShow.currentDashboardIndex + 1
        if (
          self.slideShow.currentDashboardIndex >=
          self.remoteScreen.screenContext.screenDashboards.length
        ) {
          self.slideShow.currentDashboardIndex = 0
        }
        self.slideShow.currentDashboard = self.getDashboardProps(
          self.slideShow.currentDashboardIndex
        )
        if (self.remoteScreen.screenContext.screenDashboards.length == 1) {
          self.refresh = !self.refresh
        }
        self.showDashboard = true
      })
    },
    previous() {
      let self = this
      self.slideShow.currentDashboardIndex =
        self.slideShow.currentDashboardIndex - 1

      if (self.slideShow.currentDashboardIndex < 0) {
        self.slideShow.currentDashboardIndex = 0
      }
      self.slideShow.currentDashboard = self.getDashboardProps(
        self.slideShow.currentDashboardIndex
      )
    },
    showControlsToolbar() {
      let self = this
      clearTimeout(self.autoHideTimeoutObj)
      self.controlsToolbar.show = true

      self.autoHideTimeoutObj = setTimeout(function() {
        self.controlsToolbar.show = false
      }, self.controlsToolbar.autoHideTimeout)
    },
    getDashboardProps(index) {
      let screenDashboard = this.remoteScreen.screenContext.screenDashboards[
        index
      ]
      let linkName = screenDashboard.dashboard.linkName
      let buildingId = null
      let siteId = null
      if (
        linkName === 'residentialbuildingdashboard' ||
        linkName === 'commercialbuildingdashboard'
      ) {
        linkName = 'buildingdashboard'
      }
      if (linkName === 'buildingdashboard') {
        buildingId = screenDashboard.spaceId
      } else if (linkName === 'sitedashboard') {
        siteId = screenDashboard.spaceId
      }
      return {
        id: screenDashboard.dashboardId,
        dashboardName: screenDashboard.dashboard.dashboardName,
        linkName: linkName,
        moduleName: screenDashboard.dashboard.moduleName,
        buildingId: buildingId,
        siteId: siteId,
        readOnly: true,
        screenSetting: this.remoteScreen.screenContext.screenSetting,
      }
    },
    openFullScreen: function() {
      let element = document.getElementsByTagName('body')[0]
      if (element.requestFullscreen) {
        element.requestFullscreen()
      } else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen()
      } else if (element.webkitRequestFullscreen) {
        element.webkitRequestFullscreen()
      } else if (element.msRequestFullscreen) {
        element.msRequestFullscreen()
      }
    },
  },
}
</script>

<style>
.no-screens-casted {
  text-align: center;
  font-size: 40px;
  margin-top: 60px;
}

.screen-controls-toolbar {
  position: fixed;
  top: 30px;
  left: 0;
  background: #ee518f;
  padding: 8px 20px;
  border-radius: 5px;
  font-size: 20px;
  border: 0px;
  margin-left: 45%;
  z-index: 1000;
}

.screen-controls-toolbar i {
  margin: 10px;
  color: #fff;
  cursor: pointer;
}

.screen-controls-toolbar i.playpause {
  font-size: 40px;
  vertical-align: middle;
}

.toolbarfade-enter-active,
.toolbarfade-leave-active {
  transition: opacity 0.8s;
}
.toolbarfade-enter,
.toolbarfade-leave-to {
  opacity: 0;
}

.dashboard-switcher {
  padding: 0px;
  max-height: 500px;
  overflow: scroll;
}

.dashboard-switcher ul {
  list-style: none;
  padding: 0;
  margin: 0;
  margin-top: 10px;
  margin-bottom: 10px;
}

.dashboard-switcher ul li {
  padding: 12px 30px;
  cursor: pointer;
  font-weight: normal !important;
}
.dashboard-switcher ul li:hover {
  background: #f1f8fa;
}
.dashboard-switcher ul li:hover,
.dashboard-switcher ul li.active {
  background: #f1f8fa;
  color: #2c9baa;
}
.el-icon-arrow-down-tv {
  padding-left: 7px;
  position: relative;
  top: 2px;
  font-size: 20px;
  color: #333333;
  font-weight: 400;
}
.remoteScreenFlSc span .el-popover__reference .el-icon-arrow-down-tv {
  visibility: hidden;
}

.remoteScreenFlSc span .el-popover__reference:hover .el-icon-arrow-down-tv {
  visibility: visible;
}
</style>
