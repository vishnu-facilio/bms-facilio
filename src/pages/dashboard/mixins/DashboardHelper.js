/* eslint-disable no-console */
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'

export default {
  data() {
    return {
      backupDashboard: {},
      dashboardLayouts: {},
      dashboardTabforWidgets: null,
      newDashboardTabName: null,
      EnableDashboardOptions: false,
      disableDashboardOptions: false,
      copyallWidgets: false,
      dashboardOptions: {
        showTabs: false,
        dashboardTabPlacement: 2,
      },
      reportLoading: false,
      chooseFolderPopover: false,
      showAddDashboardfolder: false,
      dashboardSave: false,
      newDashboardName: '',
      assetCardData: null,
      buildingCardData: null,
      dashboardFolderQuery: '',
      kpiCard: null,
      assetcardEdit: false,
      showFcuCardDialog: false,
      showAlarmbarCardDialog: false,
      showBooleanCardDialog: false,
      fcuCardData: null,
      booleancardData: null,
      alarmcardData: null,
      cardToLayoutMap: {
        AHU: {
          w: 12,
          h: 20,
        },
        LIGHT: {
          w: 12,
          h: 12,
        },
        PUMP: {
          w: 12,
          h: 12,
        },
        PUMP1: {
          w: 12,
          h: 12,
        },
        WATER_TANK: {
          w: 12,
          h: 12,
        },
        HVAC: {
          w: 16,
          h: 20,
        },
        HVAC2: {
          w: 12,
          h: 12,
        },
        HVAC3: {
          w: 12,
          h: 12,
        },
        HVAC4: {
          w: 20,
          h: 24,
        },
        HVAC5: {
          w: 20,
          h: 24,
        },
        HVAC6: {
          w: 20,
          h: 24,
        },
        HVAC7: {
          w: 20,
          h: 24,
        },
        HVAC8: {
          w: 20,
          h: 24,
        },
        HVAC9: {
          w: 20,
          h: 24,
        },
        HVAC10: {
          w: 20,
          h: 24,
        },
      },
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },

  computed: {
    ...mapGetters(['getAssetCategoryPickList']),
    tabsEnabled() {
      let { dashboardTabContexts, dashboardOptions } = this
      if (
        dashboardTabContexts &&
        dashboardTabContexts.length &&
        dashboardOptions.dashboardTabPlacement === 2
      ) {
        return true
      }
      return false
    },
    bglayout() {
      let layout = []
      let prevX = 0
      let prevY = 1
      for (let i = 0; i <= 240; i++) {
        let l = { x: prevX, y: prevY, w: 1, h: 1, i: i.toString() }
        prevX = prevX + 1
        if (prevX > 24) {
          prevX = 0
          prevY = prevY + 1
        }
        layout.push(l)
      }
      return layout
    },
    filteredDashboardList() {
      if (this.dashboardFolderQuery && this.dashboardFolderQuery !== '') {
        return this.dashboardFolderList.filter(rt =>
          rt.name
            .toLowerCase()
            .includes(this.dashboardFolderQuery.toLowerCase())
        )
      }
      return this.dashboardFolderList
    },
  },
  methods: {
    beforeDestroyHook() {
      // before destroy hooks
    },
    validateFolder() {
      if (!this.dashboardFolderList.length) {
        if (this.selectedDashoardFolder.name === null) {
          this.$message({
            message: 'Enter Dashboard folder',
            type: 'error',
          })
          return false
        } else {
          return true
        }
      } else {
        if (this.selectedDashoardFolder.name === null) {
          this.$message({
            message: 'Enter Dashboard folder',
            type: 'error',
          })
          return false
        }
      }
      return true
    },
    updateMultiTab(params) {
      this.$http.post('/dashboard/updateTabsList', params).then(() => {
        this.$message({
          message: 'Dashboard updated successfully!',
          type: 'success',
        })
        this.$router.push(this.newconstractUrl(this.dashboard))
      })
    },
    removeWidgets(dasthboardlayout) {
      return dasthboardlayout.filter(widget => {
        if (widget && widget.w && widget.h) {
          return widget
        }
      })
    },
    prepareWidgetToSave(dasthboardlayout, dashboardTabId, dashboardTabName) {
      let self = this
      this.dashboardSave = true
      let dashboardWidgets = []
      let widgetData = null
      if (dasthboardlayout !== null) {
        for (let i = 0; i < dasthboardlayout.length; i++) {
          let gridItem = dasthboardlayout[i]

          let reportTemplate =
            gridItem.widget.dataOptions.newReport &&
            gridItem.widget.dataOptions.newReport.type &&
            gridItem.widget.dataOptions.newReport.type === 4
              ? gridItem.widget.dataOptions.reportTemplate
                ? gridItem.widget.dataOptions.reportTemplate
                : gridItem.widget.dataOptions.newReport.reportTemplate
              : null
          if (typeof reportTemplate === 'object') {
            reportTemplate = JSON.stringify(reportTemplate)
          }

          if (gridItem.widget.type === 'card') {
            widgetData = {
              id: gridItem.widget.id,
              type: gridItem.widget.type,
              layoutWidth: gridItem.w,
              layoutHeight: gridItem.h,
              order: i + 1,
              xPosition: gridItem.x,
              yPosition: gridItem.y,
              ...gridItem.widget.dataOptions,
            }
          } else {
            widgetData = {
              id: gridItem.widget.id,
              type: gridItem.widget.type,
              layoutWidth: gridItem.w,
              layoutHeight: gridItem.h,
              order: i + 1,
              xPosition: gridItem.x,
              yPosition: gridItem.y,
              headerText: gridItem.widget.header.title,
              reportId: gridItem.widget.dataOptions.reportId,
              newReportId: gridItem.widget.dataOptions.newReportId,
              staticKey: gridItem.widget.dataOptions.staticKey
                ? gridItem.widget.dataOptions.staticKey
                : null,
              viewName: gridItem.widget.dataOptions.viewName
                ? gridItem.widget.dataOptions.viewName
                : null,
              moduleName: gridItem.widget.dataOptions.moduleName
                ? gridItem.widget.dataOptions.moduleName
                : null,
              reportTemplate: reportTemplate,
            }
          }

          if (widgetData.type === 'graphics') {
            widgetData.graphicsId = gridItem.widget.graphicsId
            if (
              gridItem.widget.dataOptions &&
              gridItem.widget.dataOptions.graphicsOptions
            ) {
              widgetData.graphicsOptions =
                typeof gridItem.widget.dataOptions.graphicsOptions === 'object'
                  ? JSON.stringify(gridItem.widget.dataOptions.graphicsOptions)
                  : gridItem.widget.dataOptions.graphicsOptions
            }
          }
          if (gridItem.widget.dataOptions.building) {
            widgetData.baseSpaceId = gridItem.widget.dataOptions.building.id
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'readingcard' &&
            gridItem.widget.dataOptions.params
          ) {
            widgetData.metaJson = JSON.stringify(
              gridItem.widget.dataOptions.params.metaJson
            )
            widgetData.paramsJson =
              gridItem.widget.dataOptions.params.paramsJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'readingWithGraphCard' &&
            gridItem.widget.dataOptions.params
          ) {
            widgetData.metaJson = JSON.stringify(
              gridItem.widget.dataOptions.params.metaJson
            )
            widgetData.paramsJson =
              gridItem.widget.dataOptions.params.paramsJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'readingGaugeCard' &&
            gridItem.widget.dataOptions.params
          ) {
            widgetData.metaJson = JSON.stringify(
              gridItem.widget.dataOptions.params.metaJson
            )
            widgetData.paramsJson =
              gridItem.widget.dataOptions.params.paramsJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'textcard' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson + ''
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'imagecard' &&
            gridItem.widget.dataOptions.imagecardData
          ) {
            widgetData.paramsJson = {
              photoId: gridItem.widget.dataOptions.imagecardData.photoId,
              url: gridItem.widget.dataOptions.imagecardData.url,
            }
            widgetData.metaJson = JSON.stringify(
              gridItem.widget.dataOptions.imagecardData
            )
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'web' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'fahuStatusCard' &&
            gridItem.widget.dataOptions.AHUcard
          ) {
            widgetData.paramsJson = {
              parentId: gridItem.widget.dataOptions.AHUcard.id,
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'fahuStatusCard1' &&
            gridItem.widget.dataOptions.AHUcard
          ) {
            widgetData.paramsJson = {
              parentId: gridItem.widget.dataOptions.AHUcard.id,
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'fahuStatusCard2' &&
            gridItem.widget.dataOptions.AHUcard
          ) {
            widgetData.paramsJson = {
              parentId: gridItem.widget.dataOptions.AHUcard.id,
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'fahuStatusCard3' &&
            gridItem.widget.dataOptions.AHUcard
          ) {
            widgetData.paramsJson = {
              parentId: gridItem.widget.dataOptions.AHUcard.id,
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'fahuStatusCardNew' &&
            gridItem.widget.dataOptions.AHUcard
          ) {
            widgetData.paramsJson = {
              parentId: gridItem.widget.dataOptions.AHUcard.id,
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'readingComboCard' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
            let data1 = JSON.parse(gridItem.widget.dataOptions.metaJson)
            if (
              data1.expressions &&
              data1.hasOwnProperty('v2Script') &&
              data1.hasOwnProperty('workflowV2String')
            ) {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  expressions: data1.expressions,
                  workflowUIMode: 1,
                  isV2Script: data1.v2Script,
                  workflowV2String: data1.workflowV2String,
                },
              })
            } else if (data1 && data1.expressions) {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  expressions: data1.expressions,
                  workflowUIMode: 1,
                },
              })
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'kpiCard' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
            let data1 = JSON.parse(gridItem.widget.dataOptions.metaJson)
            if (
              data1.expressions &&
              data1.hasOwnProperty('v2Script') &&
              data1.hasOwnProperty('workflowV2String')
            ) {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  expressions: data1.expressions,
                  workflowUIMode: 1,
                  isV2Script: data1.v2Script,
                  workflowV2String: data1.workflowV2String,
                },
              })
            } else if (data1.workflowV2String) {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  isV2Script: true,
                  workflowV2String: data1.workflowV2String,
                },
              })
            } else if (data1 && data1.expressions) {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  expressions: data1.expressions,
                  workflowUIMode: 1,
                },
              })
            } else {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  v2Script: true,
                  workflowV2String: 'void test(){↵    ↵    ↵}',
                },
              })
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'kpiCard' &&
            gridItem.widget.dataOptions.paramsJson
          ) {
            widgetData.paramsJson = gridItem.widget.dataOptions.paramsJson || {}
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'kpiMultiResultCard' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
            let data1 = JSON.parse(gridItem.widget.dataOptions.metaJson)
            if (
              data1.expressions &&
              data1.hasOwnProperty('v2Script') &&
              data1.hasOwnProperty('workflowV2String')
            ) {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  expressions: data1.expressions,
                  workflowUIMode: 1,
                  isV2Script: data1.v2Script,
                  workflowV2String: data1.workflowV2String,
                },
              })
            } else if (data1 && data1.expressions) {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  expressions: data1.expressions,
                  workflowUIMode: 1,
                },
              })
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'fcucard' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrillFcu' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrillFcuList' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrilllevel1' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrilllevel2' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrilllevel3' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrilllevel' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrilllevel1List' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrilllevel2List' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrilllevel3List' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'resourceAlarmBar' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
            widgetData.paramsJson = JSON.parse(
              gridItem.widget.dataOptions.metaJson
            ).params
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'alarmbarwidget' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
            widgetData.paramsJson = JSON.parse(
              gridItem.widget.dataOptions.metaJson
            ).params
          }
          if (
            gridItem.widget.type === 'chart' &&
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.hasOwnProperty('chartTypeInt')
          ) {
            widgetData.chartType = gridItem.widget.dataOptions.chartTypeInt
          }
          dashboardWidgets.push(widgetData)
        }
      }
      let dashboardObj = {}
      if (self.mode === 'new') {
        dashboardObj = {
          dashboardMeta: {
            id: self.dashboard.id,
            tabEnabled: self.dashboardOptions.showTabs,
            dashboardName: self.newdashboard.label,
            dashboardWidgets: dashboardWidgets,
            dashboardFolderId: self.dashboard.dashboardFolderId
              ? self.dashboard.dashboardFolderId
              : null,
          },
        }
        if (self.selectedDashoardFolder) {
          dashboardObj.dashboardMeta.dashboardFolderId =
            self.selectedDashoardFolder && self.selectedDashoardFolder.id
              ? self.selectedDashoardFolder.id
              : null
        }
      } else {
        dashboardObj = {
          dashboardMeta: {
            id: self.dashboard.id,
            tabEnabled: self.dashboardOptions.showTabs,
            dashboardName: self.dashboard.label,
            linkName: self.dashboardLink,
            dashboardWidgets: dashboardWidgets,
            dashboardFolderId: self.dashboard.dashboardFolderId
              ? self.dashboard.dashboardFolderId
              : null,
          },
        }
        if (self.selectedDashoardFolder) {
          dashboardObj.dashboardMeta.dashboardFolderId =
            self.selectedDashoardFolder && self.selectedDashoardFolder.id
              ? self.selectedDashoardFolder.id
              : null
        }
      }
      if (this.buildingId && this.dashboardLink === 'sitedashboard') {
        dashboardObj.siteId = this.buildingId
      } else if (
        this.buildingId &&
        this.dashboardLink === 'buildingdashboard'
      ) {
        dashboardObj.buildingId = this.buildingId
      }
      return {
        dashboardName: dashboardObj.dashboardMeta.dashboardName,
        dashboardId: dashboardObj.dashboardMeta.id,
        dashboardFolderId: dashboardObj.dashboardMeta.dashboardFolderId,
        tabId: dashboardTabId,
        dashboardWidgets: this.updateDashboardTabId(
          dashboardObj.dashboardMeta.dashboardWidgets,
          dashboardTabId
        ),
        dashboardTabName: dashboardTabName,
      }
    },
    updateDashboardTabId(dashboards, dashboardTabId) {
      if (dashboardTabId) {
        dashboards.forEach(rt => {
          this.$set(rt, 'dashboardTabId', dashboardTabId)
        })
        return dashboards
      }
      dashboards.forEach(rt => {
        this.$set(rt, 'dashboardTabId', this.dashboardTabId)
      })
      return dashboards
    },
    addNewDashboardTab() {
      let param = {
        dashboardTabContext: {
          name: this.newDashboardTabName,
          sequence: 1,
          dashboardId: this.dashboard.id,
        },
      }
      this.$http
        .post('dashboard/addDashboardTab', param)
        .then(response => {
          if (response.data) {
            this.saveEdit(this.dashboard)
            this.$message.success('Dashboard Tab Added Successfully')
          }
        })
        .catch(error => {
          this.$message.error('Error Occured')
        })
    },
    loadEmptyDashboard() {
      let layout = this.dashboardLayout
      this.$http
        .get(`/dashboard/${this.dashboardLink}`)
        .then(response => {
          if (response.data) {
            this.dashboardTabContexts = response.data.dashboardJson[0].tabs
            this.dashboard = response.data.dashboardJson[0]
            this.dashboardTabsList = []
            let tabId = -1
            if (this.dashboardTabContexts) {
              for (let tab of this.dashboardTabContexts) {
                if (
                  this.newDashboardTabName &&
                  this.newDashboardTabName === tab.name &&
                  tab.sequence === 1
                ) {
                  tabId = tab.id
                  this.selectedTabId = tabId
                }
                this.dashboardTabsList.push({
                  id: tab.id,
                  name: tab.name,
                  expand: false,
                })
                if (tab.childTabs && tab.childTabs.length > 0) {
                  for (let childTab of tab.childTabs) {
                    if (
                      this.newDashboardTabName &&
                      this.newDashboardTabName === childTab.name &&
                      childTab.sequence === 1
                    ) {
                      tabId = tab.id
                      this.selectedTabId = tabId
                    }
                    this.dashboardTabsList.push({
                      id: childTab.id,
                      name: childTab.name,
                    })
                  }
                }
              }
              if (this.newDashboardTabName) {
                this.$router.push({
                  query: {
                    create: this.$route.query.create,
                    tab: tabId,
                  },
                })
                layout.forEach(rt => {
                  rt.dashboardTabId = tabId
                })
                this.dashboardLayout = layout
              } else if (this.dashboardTabsList.length) {
                let tabId = this.dashboardTabsList[0].id
                this.$router.push({
                  query: {
                    create: this.$route.query.create,
                    tab: tabId,
                  },
                })
              }
            } else if (tabId === -1) {
              let { query } = this.$route
              let { appId } = query || {}
              if (!isEmpty(appId)) {
                let url = '/app/newdashboardmanager'
                this.$router.push(url)
              } else {
                this.$router.push(this.newconstractUrl())
              }
            }
          }
        })
        .catch(error => {
          this.$message.error('Error Occured')
        })
    },
    saveEnabledDashboardOptions() {
      if (
        (this.copyallWidgets && this.newDashboardTabName === null) ||
        this.newDashboardTabName === '' ||
        this.newDashboardTabName.trim() === ''
      ) {
        this.$message.error('Tab name cannot be empty')
        return
      }
      let { copyallWidgets, newDashboardTabName } = this
      if (newDashboardTabName) {
        this.addNewDashboardTab()
      } else {
        this.saveEdit(this.dashboard)
      }
      this.closeDashboardOption(true)
    },
    saveDisabledDashboardOptions() {
      let { copyallWidgets, dashboardTabforWidgets, newDashboardTabName } = this
      let dashboardlayout = null
      newDashboardTabName = null
      if (copyallWidgets && dashboardTabforWidgets) {
        if (Object.keys(this.dashboardLayouts).length) {
          if (
            dashboardTabforWidgets !== this.dashboardTabId &&
            this.dashboardLayouts[dashboardTabforWidgets]
          ) {
            dashboardlayout = this.dashboardLayouts[dashboardTabforWidgets]
            this.dashboardlayout = dashboardlayout
          } else if (dashboardTabforWidgets !== this.dashboardTabId) {
            this.getDashboardTabWidgets(dashboardTabforWidgets)
          }
        } else if (dashboardTabforWidgets !== this.dashboardTabId) {
          this.getDashboardTabWidgets(dashboardTabforWidgets)
        }

        this.dashboardLayouts = {}
        this.setIdNull()
        this.dashboardTabContexts = null
      } else {
        this.getDashboardDefualtWidget()
        this.removeTabQuerry()
        this.setEmptyTab()
      }

      this.closeDashboardOption(true)
    },
    removeTabQuerry() {
      this.$router.push({
        query: {
          create: this.$route.query.create,
        },
      })
    },
    setEmptyTab() {
      this.dashboardLayouts = {}
      this.dashboardLayout = []
      this.dashboardTabContexts = []
    },
    getDashboardDefualtWidget() {
      this.dashboardTabLoading = true
      this.$http
        .get('/dashboard/' + this.dashboard.linkName)
        .then(response => {
          if (response.data) {
            let dashboard = response.data.dashboardJson[0].children
            this.$set(this.dashboard, 'children', dashboard)
            this.prepareDashboardLayout()
          }
          this.dashboardTabLoading = false
        })
        .catch(() => {
          this.dashboardTabLoading = false
        })
    },
    getDashboardTabWidgets(dashboardTabId) {
      this.dashboardTabLoading = true
      this.$http
        .get('/dashboard/getTabWidgets?dashboardTabId=' + dashboardTabId)
        .then(response => {
          if (response.data.dashboardTabContext) {
            let dashboard = response.data.dashboardTabContext.clientWidgetJson
            this.$set(this.dashboard, 'children', dashboard)
            this.prepareDashboardLayout()
            this.setIdNull()
          }
          this.dashboardTabLoading = false
        })
        .catch(() => {
          this.dashboardTabLoading = false
        })
    },
    setIdNull() {
      this.dashboardLayout.forEach(rt => {
        if (rt.widget && rt.widget.id) {
          rt.widget.id = -1
        }
      })
    },
    closeDashboardOption(disbaled) {
      if (!disbaled) {
        let { showTabs } = this.dashboardOptions
        this.$set(this.dashboardOptions, 'showTabs', !showTabs)
      }
      this.EnableDashboardOptions = false
      this.disableDashboardOptions = false
    },
    beforeCloseDashboardOption() {
      // this.dashboardOptions.showTabs = !this.dashboardOptions.showTabs
    },
    showconfirmationDilog() {
      let { showTabs } = this.dashboardOptions
      this.dashboard.tabEnabled = showTabs
      if (showTabs) {
        this.setDefaultEnableDashboardTabOptions()
        // this.disableDashboardOptions = true
      } else {
        this.setDefaultDisableDashboardTabOptions()
        this.EnableDashboardOptions = true
      }
    },
    setDefaultDisableDashboardTabOptions() {
      this.copyallWidgets = false
      if (this.dashboardTabsList && this.dashboardTabsList.length) {
        this.dashboardTabforWidgets = this.dashboardTabsList[0].id
      }
    },
    setDefaultEnableDashboardTabOptions() {
      this.copyallWidgets = true
      this.newDashboardTabName = ''
      let params = { dashboardId: this.dashboard.id }
      this.$http
        .put('/dashboard/enableDashboardTabs', params)
        .then(response => {
          if (response.data.dashboard) {
            this.$router.push(this.newconstractUrl())
            this.closeDashboardOption(true)
            this.$message.success('Dashboard Tab Enabled Successfully')
          } else {
            this.disableDashboardOptions = true
          }
        })
    },
    setDefaultDashboard() {
      let {
        filteredDashboardList,
        selectedDashoardFolder,
        dashbaordFolderName,
      } = this
      if (filteredDashboardList && filteredDashboardList.length) {
        let folder = filteredDashboardList.find(
          rt => rt.moduleName === 'workorder'
        )
        if (folder) {
          selectedDashoardFolder.id = folder.id
          selectedDashoardFolder.name = folder.name
          dashbaordFolderName = folder.name
        } else {
          selectedDashoardFolder.id = filteredDashboardList[0].id
          selectedDashoardFolder.name = filteredDashboardList[0].name
          dashbaordFolderName = filteredDashboardList[0].name
        }
      } else {
        selectedDashoardFolder.name = null
      }
    },
    closeAddDashboard() {
      this.showAddDashboardfolder = false
    },
    addDashbopardFolderNew() {
      let { newDashboardName } = this
      this.closeAddDashboard()
      let reportFolderObj = {
        name: newDashboardName,
      }
      let { query } = this.$route
      let { appId } = query || {}
      if (!isEmpty(appId) && appId > 0) {
        reportFolderObj.appId = appId
      }
      this.$http
        .post(`dashboard/addDashboardFolder?moduleName=workorder`, {
          dashboardFolderContext: reportFolderObj,
        })
        .then(response => {
          let dashboardFolderContext = response.data.dashboardFolderContext
          this.selectedDashoardFolder.name = newDashboardName
          this.selectedDashoardFolder.id = dashboardFolderContext.id
          this.pushDashboardFolder(dashboardFolderContext)
          this.newDashboardName = null
          // this.$message({
          //   message: 'Dashboard Folder added',
          //   type: 'success',
          // })
        })
        .catch(() => {
          // this.$message({
          //   message: 'Dashboard Folder not saved',
          //   type: 'error',
          // })
        })
    },
    pushDashboardFolder(folder) {
      folder.displayOrder = this.dashboardFolderList.length + 1
      this.dashboardFolderList.push(folder)
    },
    addDashbopardFolder() {
      this.addDashbopardFolderNew()
      this.dashbaordFolderName = this.newDashboardName
      this.selectedDashoardFolder.name = this.dashbaordFolderName
      this.showAddDashboardfolder = false
    },
    handleButton(command) {
      if (command === 'addCard') {
        this.openNewCardBuilder()
      } else if (command === 'addText') {
        this.addText()
      } else if (command === 'addImage') {
        this.addImage()
      } else if (command === 'addComponent') {
        this.openDashboardCompoents()
      }
    },
    setoption(option) {
      this.option = option
    },
    getWidgetNextPosition(w, h, i) {
      let maxY = 0
      let maxX = 0
      this.dashboardLayout.forEach(layout => {
        let { y } = layout
        if (y > maxY) {
          maxY = y
        }
      })
      let ymaxLayouts = this.dashboardLayout.filter(layout => {
        let { y } = layout
        if (y === maxY) {
          return layout
        }
      })
      ymaxLayouts.forEach(layout => {
        let { x } = layout
        if (x > maxX) {
          maxX = x
        }
      })
      let layout = ymaxLayouts.find(layout => {
        let { x } = layout
        if (x === maxX) {
          return layout
        }
      })
      let l = { x: 0, y: Infinity, i: i }
      if (this.dashboardLayout.length === 0) {
        l = { x: 0, y: 0, i: i }
      }
      if (layout) {
        let nextX = layout.x + layout.w
        if (nextX > 96) {
          l.x = 0
          l.y = layout.y + layout.h
        } else {
          if (nextX + w > 96) {
            l.x = 0
            l.y = layout.y + layout.h
          } else {
            l.x = nextX
            l.y = layout.y + w
          }
        }
      }
      return l
    },
    getReportWidget(data, layout, cardName) {
      let position = {}
      if (this.option === 'cards' && cardName === 'textcard') {
        position = this.getWidgetNextPosition(4, 4, this.dashboardlength + '')
        layout.x = position.x
        layout.y = position.y
        layout.widget.layout.x = position.x
        layout.widget.layout.y = position.y
        layout.widget.dataOptions.metaJson = this.textcardData
      } else if (this.option === 'cards' && cardName === 'imagecard') {
        position = this.getWidgetNextPosition(4, 4, this.dashboardlength + '')
        layout.x = position.x
        layout.y = position.y
        layout.widget.layout.x = position.x
        layout.widget.layout.y = position.y
        // layout.widget.dataOptions.metaJson = this.textcardData
      } else if (this.option === 'view') {
        let ctype = 'view'
        position = this.getWidgetNextPosition(
          this.getMinW(ctype),
          this.getMinH(ctype),
          this.dashboardlength + ''
        )
        layout = {
          i: this.dashboardlength + '',
          x: position.x,
          y: position.y,
          w: this.getMinW('view'),
          h: this.getMinH('view'),
          widget: {
            layout: {
              width: this.getMinW('view'),
              x: position.x,
              y: position.y,
              position: this.dashboardlength,
              height: this.getMinH('view'),
            },
            header: {
              subtitle: 'today',
              title: data.displayName,
              export: false,
            },
            dataOptions: {
              dataurl: '',
              refresh_interval: 100,
              viewName: data.name,
              name: 'dummy',
              moduleName: data.moduleName,
            },
            type: 'view',
            id: -1,
          },
          label: data.displayName,
          minW: this.getMinW('view'),
          minH: this.getMinH('view'),
        }
      } else if (this.option === 'graphics') {
        let ctype = 'graphics'
        position = this.getWidgetNextPosition(
          this.getMinW(ctype),
          this.getMinH(ctype),
          this.dashboardlength + ''
        )
        layout = {
          i: this.dashboardlength + '',
          x: position.x,
          y: position.y,
          w: this.getMinW('graphics'),
          h: this.getMinH('graphics'),
          widget: {
            layout: {
              width: this.getMinW('graphics'),
              x: 0,
              y: Infinity,
              position: this.dashboardlength,
              height: this.getMinH('graphics'),
            },
            header: {
              subtitle: 'today',
              title: data.name,
              export: false,
            },
            dataOptions: {
              dataurl: '',
              refresh_interval: 100,
              webUrl: data.url,
              name: 'dummy',
            },
            type: 'graphics',
            graphicsId: data.id,
          },
          minW: this.getMinW('graphics'),
          minH: this.getMinH('graphics'),
          label: data.label,
        }
      } else if (data.widget) {
        position = this.getWidgetNextPosition(
          this.getMinW(data.widget.type),
          this.getMinH(data.widget.type),
          this.dashboardlength + ''
        )
        layout = {
          i: this.dashboardlength + '',
          x: position.x,
          y: position.y,
          w: this.getMinW(data.widget.type),
          h: this.getMinH(data.widget.type),
          widget: {
            layout: {
              width: this.getMinW(data.widget.type),
              x: position.x,
              y: position.y,
              position: this.dashboardlength,
              height: this.getMinW(data.widget.type),
            },
            header: data.widget.header,
            dataOptions: data.widget.dataOptions,
            id: -1,
            reportId: data.widget.dataOptions.reportId,
            type: data.widget.type,
            name: '',
          },
          minW: this.getMinW(data.widget.type),
          minH: this.getMinH(data.widget.type),
          moved: false,
        }
      } else {
        let type = data.type
        let ctype = 'line'
        position = this.getWidgetNextPosition(
          this.getMinW(ctype),
          this.getMinH(ctype),
          this.dashboardlength + ''
        )
        layout = {
          i: this.dashboardlength + '',
          x: position.x,
          y: position.y,
          w: this.getMinW(ctype),
          h: this.getMinH(ctype),
          widget: {
            layout: {
              width: this.getMinW(ctype),
              x: position.x,
              y: position.y,
              position: this.dashboardlength,
              height: this.getMinW(ctype),
            },
            header: {
              title: data.name,
            },
            dataOptions: {
              newReportId: data.id,
              newReport: this.newLayout
                ? data
                : (this.getCurrentModule() &&
                    this.getCurrentModule().module === 'workorder') ||
                  type === 4
                ? {
                    type: type,
                  }
                : null,
            },
            id: -1,
            newReportId: data.id,
            type: 'chart',
            name: '',
          },
          minW: this.getMinW(ctype),
          minH: this.getMinH(ctype),
          moved: false,
        }

        if (type === 4) {
          layout.widget.dataOptions['reportTemplate'] = data.reportTemplate
        }
      }
      return layout
    },
    updateBuildingCard(data) {},
    setColorMapProperties(readingCard) {
      if (
        readingCard.colorMap &&
        readingCard.readings &&
        readingCard.readings.readingField &&
        this.colorMapSupported[readingCard.readings.readingField.dataTypeEnum]
      ) {
        if (readingCard.readings.readingField.dataTypeEnum === 'BOOLEAN') {
          this.readingCard.colorMapConfig = {
            BOOLEAN: {
              min: {
                label: null,
                textColor: '#000',
                bgColor: '#fff',
              },
              max: {
                label: null,
                textColor: '#000',
                bgColor: '#fff',
              },
            },
          }
        }
        if (readingCard.readings.readingField.dataTypeEnum === 'ENUM') {
          let d = {}
          this.readingCard.colorMapConfig = []
          let data = readingCard.readings.readingField.enumMap
          for (let key in data) {
            if (data.hasOwnProperty(key)) {
              d[key] = {
                value: key,
                label: data[key],
                textColor: '#000',
                bgColor: '#fff',
              }
            }
          }
          this.readingCard.colorMapConfig = {
            ENUM: this.$helpers.cloneObject(d),
          }
        }
        if (readingCard.readings.readingField.dataTypeEnum === 'NUMBER') {
          this.readingCard.colorMapConfig = {
            NUMBER: [
              {
                label: null,
                min: null,
                max: null,
                textColor: '#000',
                bgColor: '#fff',
              },
            ],
          }
        }
        if (readingCard.readings.readingField.dataTypeEnum === 'DECIMAL') {
          this.readingCard.colorMapConfig = {
            DECIMAL: [
              {
                label: null,
                min: null,
                max: null,
                textColor: '#000',
                bgColor: '#fff',
              },
            ],
          }
        }
      } else {
        this.readingCard.colorMapConfig = {}
      }
    },
    addColorMapElemnt() {
      this.readingCard.colorMapConfig['NUMBER'].push({
        label: null,
        min: null,
        max: null,
        textColor: '#000',
        bgColor: '#fff',
      })
    },
    removeColorMapElemnt(index) {
      this.readingCard.colorMapConfig['NUMBER'].splice(index, 1)
    },
    addColorMapElemntforDecimal() {
      this.readingCard.colorMapConfig['DECIMAL'].push({
        label: null,
        min: null,
        max: null,
        textColor: '#000',
        bgColor: '#fff',
      })
    },
    removeColorMapElemntDecimal(index) {
      this.readingCard.colorMapConfig['DECIMAL'].splice(index, 1)
    },
    saveBooleancard(data) {
      this.booleancardData = data
      this.addNewelemnt()
      this.isDraging = false
    },
    saveAlarmcard(data) {
      this.alarmcardData = data
      this.addNewelemnt()
      this.isDraging = false
    },
    addAssetCard(data) {
      this.assetCardData = data
      this.addNewelemnt()
      this.isDraging = false
      this.showCardBuilder = false
    },
    cardBuilder(data) {
      this.kpiCard = data
      this.addNewelemnt()
      this.isDraging = false
      this.showkpiCard = false
      this.showkpitargetCard = false
    },
    setBuildingCardData(data) {
      this.buildingCardData = data
      this.addNewelemnt()
      this.isDraging = false
      this.dialogVisible = false
    },
    updateKpiCard(data) {
      this.kpiCard = data
      this.dashboardLayout[
        this.editwidgetData.index
      ].widget.dataOptions.data = data
      this.dashboardLayout[
        this.editwidgetData.index
      ].widget.dataOptions.metaJson = JSON.stringify(data)
      this.isDraging = false
      this.showkpiCard = false
      this.showkpitargetCard = false
      this.dialogVisible = false
      if (
        this.$refs['widget[' + this.editwidgetData.index + ']'] &&
        this.$refs['widget[' + this.editwidgetData.index + ']'].length
      ) {
        this.$refs['widget[' + this.editwidgetData.index + ']'][0].refresh()
      }
    },
    saveFcuCard(data) {
      this.fcuCardData = data
      this.addNewelemnt()
      this.isDraging = false
    },
    updatFacuCard(data) {
      this.isDraging = false
    },
    updateAssetCardData(data) {
      this.dashboardLayout[
        this.editwidgetData.index
      ].widget.dataOptions.metaJson = JSON.stringify(data)
      this.dashboardLayout[
        this.editwidgetData.index
      ].widget.dataOptions.readingComboCard = data
      let widget = this.$helpers.cloneObject(
        this.dashboardLayout[this.editwidgetData.index]
      )
      widget.h = this.assetCardLayoutChanger(data)
      this.dashboardLayout.splice(this.editwidgetData.index, 1, widget)
      this.isDraging = false
      this.showCardBuilder = false
      this.assetCardData = null
      this.assetcardEdit = false
    },
    editAssetCard(data) {
      this.isDraging = true
      this.showCardBuilder = true
      this.assetCardData = data
    },
    loadAHUAssets(catagoryName) {
      if (this.getAssetCategoryPickList()) {
        let self = this
        let catagory = this.getAssetCategoryPickList()
        let catagoryId = null
        Object.keys(this.getAssetCategoryPickList()).forEach(function(id) {
          if (catagory[parseInt(id)] === catagoryName) {
            catagoryId = parseInt(id)
          }
        })
        if (catagoryId && this.AHUcardCatogry !== 'catagoryName') {
          this.$util
            .loadAsset({
              categoryId: catagoryId,
            })
            .then(response => {
              self.assets = response
              self.AHUcardCatogry = catagoryName
            })
        }
      }
    },
    getCardSpacificLayout(h) {
      let layout = {
        1: 4,
        2: 6,
        3: 9,
        4: 12,
        5: 18,
        6: 20,
        7: 22,
      }
      return layout[h] || h * 4
    },
    assetCardLayoutChanger(assetCardData) {
      if (assetCardData) {
        let h = 0
        if (assetCardData.cardType === 'AHU' && assetCardData.templates) {
          if (assetCardData.templates.name.value) {
            h += 1
          }
          if (
            assetCardData.templates.runStatus.value ||
            assetCardData.templates.tripStatus.value
          ) {
            h += 1
          }
          if (assetCardData.templates.autoStatus.value) {
            h += 1
          }
          if (assetCardData.templates.temperature.value) {
            h += 1
          }
          if (assetCardData.templates.valveFeedback.value) {
            h += 1
          }
          if (
            assetCardData.templates.scheduledControl.value ||
            assetCardData.templates.staticControl.value
          ) {
            h += 1
          }
          return h > 1 ? this.getCardSpacificLayout(h) : 4
        }
        if (assetCardData.cardType === 'LIGHT' && assetCardData.templates) {
          if (assetCardData.templates.name.value) {
            h += 1
          }
          if (
            assetCardData.templates.lightStatus.value ||
            assetCardData.templates.tripStatus.value
          ) {
            h += 1
          }
          if (assetCardData.templates.autoStatus.value) {
            h += 1
          }
          if (
            assetCardData.templates.scheduledControl.value ||
            assetCardData.templates.staticControl.value
          ) {
            h += 1
          }
          return h > 1 ? this.getCardSpacificLayout(h) : 4
        }
        if (assetCardData.cardType === 'PUMP' && assetCardData.templates) {
          if (assetCardData.templates.name.value) {
            h += 1
          }
          if (assetCardData.templates.runStatusPercent.value) {
            h += 1
          }
          if (
            assetCardData.templates.autoStatus.value ||
            assetCardData.templates.tripStatus.value
          ) {
            h += 1
          }
          return h > 1 ? this.getCardSpacificLayout(h) : 4
        }
        if (assetCardData.cardType === 'PUMP1' && assetCardData.templates) {
          if (assetCardData.templates.name.value) {
            h += 1
          }
          if (
            assetCardData.templates.runStatus.value ||
            assetCardData.templates.percentage.value
          ) {
            h += 1
          }
          if (
            assetCardData.templates.autoStatus.value ||
            assetCardData.templates.tripStatus.value
          ) {
            h += 1
          }
          return h > 1 ? this.getCardSpacificLayout(h) : 4
        }
        if (
          assetCardData.cardType === 'WATER_TANK' &&
          assetCardData.templates
        ) {
          if (assetCardData.templates.name.value) {
            h += 1
          }
          if (assetCardData.templates.levelPercent.value) {
            h += 1
          }
          if (assetCardData.templates.levelPercent.value) {
            h += 1
          }
          return h > 1 ? this.getCardSpacificLayout(h) : 4
        }
        let cardsvalue = [
          'HVAC',
          'HVAC4',
          'HVAC5',
          'HVAC6',
          'HVAC7',
          'HVAC8',
          'HVAC9',
          'HVAC10',
        ]
        if (
          cardsvalue.includes(assetCardData.cardType) &&
          assetCardData.templates
        ) {
          Object.keys(assetCardData.templates).forEach(function(key) {
            if (assetCardData.templates[key].value) {
              h += 1
            }
          })
          return h > 1 ? this.getCardSpacificLayout(h) : 4
        }
        if (assetCardData.cardType === 'HVAC2' && assetCardData.templates) {
          if (assetCardData.templates.name.value) {
            h += 1
          }
          if (assetCardData.templates.runStatusValue.value) {
            h += 1
          }
          if (assetCardData.templates.temperature.value) {
            h += 1
          }
          return h > 1 ? this.getCardSpacificLayout(h) : 4
        }
        if (assetCardData.cardType === 'HVAC3' && assetCardData.templates) {
          if (assetCardData.templates.name.value) {
            h += 1
          }
          if (assetCardData.templates.runStatusValue.value) {
            h += 1
          }
          if (assetCardData.templates.autoStatus.value) {
            h += 1
          }
          if (assetCardData.templates.valveFeedback.value) {
            h += 1
          }
          return h > 1 ? this.getCardSpacificLayout(h) : 4
        }
      } else {
        return 12
      }
    },
  },
}
