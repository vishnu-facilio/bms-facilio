<template>
  <div>
    <el-dialog
      :title="$t('home.dashboard.dashboard_add')"
      :visible.sync="enableMoveDialog"
      :before-close="handleclose"
      width="450px"
      custom-class="add-to-dashboard-dialog fc-dialog-center-container"
      append-to-body
    >
      <div class="p20" style="max-height: 450px;">
        <div>
          <p class="grey-text2">
            {{
              $t(
                'common.products.select_the_existing_dashboard__enter_new_dashboard_name'
              )
            }}
          </p>
          <el-select
            @change="loadWidgets"
            v-model="choosenDashboard"
            filterable
            class="fc-input-full-border2 mT10 width100"
          >
            <el-option-group
              v-for="(dashboardFolder, index) in dashboardFolders"
              :key="index"
              :label="dashboardFolder.name"
            >
              <el-option
                v-for="(dashboard, index1) in dashboardFolder.dashboards"
                :key="index1"
                :label="dashboard.dashboardName"
                :value="dashboard.linkName"
              >
              </el-option>
            </el-option-group>
          </el-select>
          <div
            style="color:#FF0000; font-size: 12px; padding-top: 8px;"
            v-if="warningMessage"
          >
            {{
              $t(
                'common.dialog.this_report_already_exists_the_selected_dashboard'
              )
            }}
          </div>
        </div>

        <div class="mT20" v-if="isTabsEnabled" :isTabsEnabled="isTabsEnabled">
          <p class="grey-text2">
            {{ $t('common.products.select_tab_for_dashboard') }}
          </p>
          <el-select
            v-model="choosenDashboardTab"
            filterable
            class="fc-input-full-border2 mT10 width100"
          >
            <el-option
              v-for="(dashboardTab, dashboardTabIndex) in dashboardTabs"
              :key="dashboardTabIndex"
              :label="dashboardTab.name"
              :value="dashboardTab"
            >
            </el-option>
          </el-select>
        </div>
        <div class="mT20">
          <p class="grey-text2">{{ $t('common.products.widget_title') }}</p>
          <el-input
            v-model="widgetTitle"
            :placeholder="
              $t('common.dialog.type_a_name_that_describes_your_widget')
            "
            class="fc-input-full-border2"
          ></el-input>
        </div>
        <div class="mT20">
          <p class="grey-text2">{{ $t('common.products.widget_size') }}</p>
          <el-radio-group v-model="widgetSize">
            <el-radio-button
              :label="$t('common.products.half')"
            ></el-radio-button>
            <el-radio-button
              :label="$t('common.products.full')"
            ></el-radio-button>
          </el-radio-group>
        </div>
        <div v-if="widgetToggle" class="mT10">
          <div>{{ $t('common.products.widget_position') }}</div>
          <el-cascader
            :options="widgetPositioningList"
            v-model="widgetPositioning"
          ></el-cascader>
        </div>
        <div class="pT20 pB50 f12 op6">
          <i class="el-icon-info"></i>
          {{
            $t('common.dialog.widget_will_be_added_end_the_selected dashboard')
          }}
        </div>
      </div>
      <div class="f-footer row" style="height:46px;">
        <slot name="footer">
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="cancel">{{
              $t('common._common.cancel')
            }}</el-button>
            <el-button
              :disabled="choosenDashboard === null"
              class="modal-btn-save"
              type="primary"
              @click="moveToDashBoard"
              :loading="adding"
              >{{
                adding
                  ? $t('common.products.adding')
                  : $t('common._common._add')
              }}
            </el-button>
          </div>
        </slot>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {pageTypes, findRouteForTab,isWebTabsEnabled } from '@facilio/router'
export default {
  created() {
    this.$http.get('/dashboardWithFolder').then(response => {
      this.dashboardFolders = response.data.dashboardFolders
      if (
        this.dashboardFolders &&
        this.dashboardFolders.length &&
        this.dashboardFolders[0].dashboardFolderId
      ) {
        this.defaultFolderId = this.dashboardFolders[0].dashboards[0].dashboardFolderId
      }
    })
  },
  data() {
    return {
      dashboardAction: 1,
      dashboardFolders: null,
      defaultFolderId: null,
      dashboardWidgets: [],
      choosenDashboard: null,
      widgetSize: 'Half',
      widgetTitle: '',
      widgetPositioning: ['end'],
      widgetToggle: false,
      warningMessage: false,
      adding: false,
      isTabsEnabled: false,
      dashboardTabs: null,
      choosenDashboardTab: null,
      widgetPositioningList: [
        {
          value: 'beginning',
          label: 'Beginning',
        },
        {
          value: 'end',
          label: 'End',
        },
        {
          value: 'beforeTo',
          label: 'Before To',
          children: [],
        },
        {
          value: 'afterTo',
          label: 'After',
          children: [],
        },
      ],
      positions: {
        START: 1,
        END: 2,
        BEFORE: 3,
        AFTER: 4,
      },
    }
  },
  props: ['enableMoveDialog', 'reportObj', 'moduleName'],
  computed: {
    dashBoards() {
      let dashboards = {}
      if (this.dashboardFolders) {
        let dashboardList = []
        this.dashboardFolders.forEach(folder => {
          if (folder.dashboards) {
            dashboardList.push(folder.dashboards)
          }
        })
        let merged = [].concat.apply([], dashboardList)
        merged.forEach(dashboard => {
          dashboards[dashboard.linkName] = dashboard
        })
      }
      return dashboards
    },
    nonExistingDashBoard() {
      let isNew = true
      if (this.choosenDashboard && this.dashBoards) {
        for (let dashboard of Object.keys(this.dashBoards)) {
          if (dashboard === this.choosenDashboard.trim().toLowerCase()) {
            isNew = false
          }
        }
      }
      return isNew
    },
    isDashboardEmpty() {
      if (this.choosenDashboard) {
        if (this.dashboardWidgets.length === 0) {
          return true
        } else {
          return false
        }
      }
    },
  },
  watch: {
    reportObj: function() {
      if (this.reportObj && !this.widgetTitle) {
        this.widgetTitle = this.reportObj.report.name
      }
    },
  },
  methods: {
    getWidgetWithMaximumHeight() {
      let maxHeightWidget = this.dashboardWidgets[0]
      let maxHeight =
        this.dashboardWidgets[0].widget.layout.y +
        this.dashboardWidgets[0].widget.layout.height
      for (let widget of this.dashboardWidgets) {
        let height = widget.widget.layout.y + widget.widget.layout.height
        if (maxHeight < height) {
          maxHeight = height
        } else {
          continue
        }
      }
      return maxHeightWidget
    },
    clearToDefaults() {
      this.dashboardWidgets = []
      this.choosenDashboard = null
      if (this.dashboardName !== null) {
        this.dashboardName = null
      }
      this.widgetSize = 'Half'
      this.widgetTitle = ''
      this.widgetPositioning = ['end']
      this.warningMessage = false
    },
    cancel() {
      this.clearToDefaults()
      this.$emit('update:enableMoveDialog', false)
    },
    handleclose(done) {
      this.clearToDefaults()
      this.$emit('update:enableMoveDialog', false)
      done()
    },
    sortedWidgetsBasedOnCoordinates() {
      let orderedWidgets = this.computeEucledianDistance(this.dashboardWidgets)
      console.log('orderedWidgets')
      console.log(orderedWidgets)
      return orderedWidgets
    },
    computeEucledianDistance(widgets) {
      console.log(widgets)
      let final = []
      let distances = {}
      let x1 = 0
      let y1 = 0
      for (let widgetIndex of widgets) {
        console.log(widgetIndex.widget)
        let xSquare = Math.pow(widgetIndex.widget.layout.x - x1, 2)
        let ySquare = Math.pow(widgetIndex.widget.layout.y - y1, 2)
        let distance = Math.sqrt(xSquare + ySquare)
        console.log('computed Distance')
        console.log(distance)
        distances[distance] = widgetIndex
      }
      let keys = Object.keys(distances)
      console.log(keys)
      let temp = keys.map(element => parseFloat(element))
      console.log('/lll')
      console.log(temp)
      temp = temp.sort(function(a, b) {
        return a - b
      })
      console.log(temp)
      for (let widgetDistance of temp) {
        console.log('widgetDist')
        console.log(widgetDistance)
        final.push(distances[widgetDistance])
      }
      console.log('distances')
      console.log(final)
      return final
    },
    addNewWidget(dashboardObj) {
      console.log('adding new widget')
      console.log(dashboardObj)
      if (this.nonExistingDashBoard || this.isDashboardEmpty) {
        console.log('New Dashboard with one widget')
        let newWidget = this.getNewWidgetDefaults()
        newWidget['order'] = 1
        newWidget['xPosition'] = 0
        newWidget['yPosition'] = 0
        if (!dashboardObj.dashboardWidgets) {
          dashboardObj['dashboardWidgets'] = []
          dashboardObj.dashboardWidgets.push(newWidget)
        } else {
          dashboardObj.dashboardWidgets.push(newWidget)
        }
      } else {
        if (
          this.widgetPositioning[0] === 'beforeTo' ||
          this.widgetPositioning[0] === 'afterTo'
          // eslint-disable-next-line no-empty
        ) {
        } else if (this.widgetPositioning[0] === 'beginning') {
          let newWidget = this.getNewWidgetDefaults()
          newWidget['order'] = newWidget['xPosition'] = 0
          newWidget['yPosition'] = 0
          let sortedWidgets = this.sortedWidgetsBasedOnCoordinates()
          let adjustedWidgets = this.adjustedWidgets(newWidget, sortedWidgets)
          dashboardObj.dashboardWidgets = adjustedWidgets
        } else {
          let newWidget = this.getNewWidgetDefaults()
          let length = dashboardObj.dashboardWidgets.length - 1
          let temp = dashboardObj.dashboardWidgets[length]
          newWidget['order'] = temp.order + 1
          let sortedWidgets = this.sortedWidgetsBasedOnCoordinates()
          let lastWidget = sortedWidgets[sortedWidgets.length - 1]
          console.log('last Widget')
          console.log(lastWidget)
          newWidget['xPosition'] =
            96 -
              (lastWidget.widget.layout.x + lastWidget.widget.layout.width) >=
            newWidget.layoutWidth
              ? lastWidget.widget.layout.x + lastWidget.widget.layout.width
              : 0
          newWidget['yPosition'] =
            96 -
              (lastWidget.widget.layout.x + lastWidget.widget.layout.width) >=
            newWidget.layoutWidth
              ? lastWidget.widget.layout.y
              : lastWidget.widget.layout.y + lastWidget.widget.layout.height
          dashboardObj.dashboardWidgets.push(newWidget)
        }
      }
      return dashboardObj
    },
    getNewWidgetDefaults() {
      let newWidget = {}
      newWidget['headerText'] = this.widgetTitle
        ? this.widgetTitle
        : this.reportObj.report.name
      newWidget['id'] = -1
      newWidget['layoutHeight'] = 30
      newWidget['layoutWidth'] = this.widgetSize === 'Half' ? 48 : 96
      newWidget['moduleName'] = null
      newWidget['newReportId'] = this.reportObj.report.id
        ? this.reportObj.report.id
        : null
      newWidget['reportId'] = null
      newWidget['staticKey'] = null
      newWidget['type'] = 'chart'
      newWidget['viewName'] = null
      return newWidget
    },
    getDashboardIdAndName() {
      let temp = {}
      if (this.choosenDashboard.includes('/')) {
        let dashboardNameAndId = this.choosenDashboard.split('/')
        temp['linkName'] = dashboardNameAndId[0]
        switch (dashboardNameAndId[0]) {
          case 'sitedashboard': {
            temp['type'] = 'siteId'
            break
          }
          case 'buildingdashboard': {
            temp['type'] = 'buildingId'
            break
          }
          case 'chillerdashboard': {
            temp['type'] = 'chillerId'
            break
          }
        }
        temp['id'] = dashboardNameAndId[1]
      } else {
        temp['linkName'] = this.choosenDashboard
      }
      return temp
    },
    moveToDashBoard() {
      this.adding = true
      if (this.isTabsEnabled && !this.choosenDashboardTab) {
        this.$message.error('Please select the dashboard tab')
        this.adding = false
        return
      }
      let dashboardObj = this.addNewWidget(
        this.loadDashboardWidgets(this.prepareDashBoardObject())
      )
      let params = {}
      let dashBoardLinkSplit = this.getDashboardIdAndName()
      if (dashBoardLinkSplit.id && dashBoardLinkSplit.type) {
        params[dashBoardLinkSplit.type] = dashBoardLinkSplit.id
      }
      let updateUrl = '/dashboard/update'
      if (this.isTabsEnabled) {
        dashboardObj.dashboardTabName = this.choosenDashboardTab.name
        dashboardObj.tabId = this.choosenDashboardTab.id
        dashboardObj.fromType = 'report_page'
        updateUrl = '/dashboard/updateTab'
      }
      dashboardObj['isFromReport']=true
      params['dashboardMeta'] = dashboardObj
      this.$http
        .post(updateUrl, params)
        .then(response => {
          this.adding = false
          let dashboardLinkName = null
          if (updateUrl.includes('updateTab')) {
            dashboardLinkName = dashboardObj.linkName
          } else if (response.data.dashboard.linkName) {
            dashboardLinkName = response.data.dashboard.linkName
          }
          if (dashboardLinkName) {
            let self = this
            let notifyInstance = this.$notify({
              title: self.$t('common._common.widget_added_successfully'),
              message: self.$t('common._common.click_here_to_open_dashboard'),
              type: 'success',
              duration: 0,
              customClass: 'report-save-success',
              onClick: function() {
                if (notifyInstance) {
                  notifyInstance.close()
                }
                let dashboardURL = '/app/home/dashboard/' + dashboardLinkName
                if(isWebTabsEnabled())
                {
                  let { name } = findRouteForTab(pageTypes.DASHBOARD_VIEWER) || {}
                  if (name) {
                    dashboardURL = self.$router.resolve({name }).href + '/' + dashboardLinkName
                  }
                }
                if (dashBoardLinkSplit && dashBoardLinkSplit.id) {
                  dashboardURL += '/' + dashBoardLinkSplit.id
                }
                self.$router.replace({ path: dashboardURL })
              },
            })
            this.clearToDefaults()
            this.$emit('update:enableMoveDialog', false)
          }
        })
        .catch(error => {
          if (error) {
            this.$message.error(
              this.$t('common._common.error_in_adding_new_widget')
            )
          }
        })
    },
    loadDashboardWidgets(dashboardObj) {
      dashboardObj['dashboardWidgets'] = []
      for (let i = 0; i < this.dashboardWidgets.length; i++) {
        let temp = {}
        temp['headerText'] = this.dashboardWidgets[i].label
        temp['id'] = this.dashboardWidgets[i].widget.id
        temp['layoutHeight'] = this.dashboardWidgets[i].widget.layout.height
        temp['layoutWidth'] = this.dashboardWidgets[i].widget.layout.width
        temp['moduleName'] = null
        temp['newReportId'] = this.dashboardWidgets[
          i
        ].widget.dataOptions.newReportId
        temp['order'] = this.dashboardWidgets[i].widget.layout.position
        temp['staticKey'] = this.dashboardWidgets[
          i
        ].widget.dataOptions.staticKey
        temp['type'] = this.dashboardWidgets[i].widget.type
        temp['viewName'] = this.dashboardWidgets[i].widget.dataOptions.viewName
        temp['xPosition'] = this.dashboardWidgets[i].widget.layout.x
        temp['yPosition'] = this.dashboardWidgets[i].widget.layout.y
        dashboardObj.dashboardWidgets.push(temp)
      }
      return dashboardObj
    },
    prepareDashBoardObject() {
      let temp = {}
      if (this.nonExistingDashBoard) {
        temp['dashboardName'] = this.choosenDashboard
        temp['id'] = this.newdashboardId
      } else {
        let dashboard = this.dashBoards[this.choosenDashboard]
        temp['dashboardName'] = dashboard.dashboardName
        temp['id'] = dashboard.id
        temp['dashboardId'] = dashboard.id
        temp['dashboardFolderId'] = dashboard.dashboardFolderId
        let dashboardLinkSplit = this.getDashboardIdAndName()
        temp['linkName'] = dashboardLinkSplit.linkName
      }
      return temp
    },
    handleResponse(dashboardJson) {
      this.isTabsEnabled = false
      this.dashboardWidgets = []
      this.dashboardTabs = null
      this.choosenDashboardTab = null
      for (let index in dashboardJson) {
        let children = dashboardJson[index].children
        for (let index2 in children) {
          let temp = {}
          if (
            children[index2]?.widget?.dataOptions?.newReportId ===
            this.reportObj.report.id
          ) {
            this.warningMessage = true
          }
          this.dashboardWidgets.push(children[index2])
          if (
            children[index2].label !== null &&
            children[index2].widget.header.title !== null
          ) {
            temp['label'] =
              children[index2].label !== null
                ? children[index2].label
                : children[index2].widget.header.title
            temp['value'] = children[index2].widget.id
            this.widgetPositioningList[2].children.push(temp)
          }
        }
        if (dashboardJson[index].tabs && dashboardJson[index].tabs.length > 0) {
          this.isTabsEnabled = true
          this.dashboardTabs = dashboardJson[index].tabs
        }
      }
      this.widgetPositioningList[3].children = this.widgetPositioningList[2].children
      // this.widgetToggle = true
    },
    loadWidgets(choosenDashboard) {
      if (this.nonExistingDashBoard !== true) {
        this.choosenDashboard = choosenDashboard
        this.warningMessage = false
        if (!this.choosenDashboard.includes('/')) {
          let url = '/dashboard/' + this.choosenDashboard
          // '?moduleName=' +
          // this.moduleName
          this.$http.get(url).then(response => {
            let dashboardResponse = response.data.dashboardJson

            this.handleResponse(dashboardResponse)
          })
        } else {
          // fetch and load widgets
          let dashboardPlusId = this.choosenDashboard.split('/')
          let url = '/dashboard/'
          if (dashboardPlusId.length === 1) {
            url = url + dashboardPlusId[0]
          } else {
            switch (dashboardPlusId[0]) {
              case 'buildingdashboard': {
                url = url + 'buildingdashboard'
                break
              }
              case 'sitedashboard': {
                url = url + 'sitedashboard'
                break
              }
              case 'chillerdashboard': {
                url = url + 'chillerdashboard'
                break
              }
            }
            url = url + '?moduleName=' + this.moduleName
            if (dashboardPlusId[1]) {
              url = url + '&id=' + dashboardPlusId[1]
            }
          }
          this.$http.get(url).then(response => {
            let dashboardJson = response.data.dashboardJson
            this.handleResponse(dashboardJson)
          })
        }
      }
    },
  },
}
</script>

<style>
.report-save-success {
  cursor: pointer;
}

.report-save-success .el-notification__icon {
  font-size: 20px;
}

.report-save-success .el-notification__group {
  margin-left: 8px;
}

.report-save-success .el-notification__title {
  font-weight: 500;
  font-size: 15px;
}

.report-save-success .el-notification__content {
  text-align: left;
}

.report-save-success .el-notification__content p {
  font-size: 13px;
}

.add-to-dashboard-dialog .el-dialog__body {
  padding: 0px !important;
}
</style>
