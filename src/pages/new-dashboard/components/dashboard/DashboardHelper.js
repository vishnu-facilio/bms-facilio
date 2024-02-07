/* eslint-disable no-console */
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { cloneDeep } from 'lodash'
import { API } from '@facilio/api'
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
    reset_uid(widgets) {
      const list = []
      for (const widget of widgets) {
        const newId = this.get_uid()
        widget.id = newId
        if (widget?.widget?.id) {
          widget.widget.id = newId
        }
        if (widget?.type == 'section') {
          const { children } = widget
          for (const child of children) {
            const newChildId = this.get_uid()
            child.id = newChildId
            if (child?.widget?.id) {
              child.widget.id = newChildId
            }
          }
        }
        list.push(widget)
      }
      return list
    },
    async changeWidgetsAndSave() {
      const self = this
      const { data, error } = await this.fetchDashboardUsingLinkName()
      if (!error) {
        const widgets = self.deserializeWidgets(
          data?.dashboardJson[0]?.children ?? []
        )
        await self.saveTab({ redirect: false, widgets })
        await self.loadDashboard()
        self.dashboardLayouts = {} // Clear tabs cache.
      } else {
        self.$message.error(error)
      }
    },
    async disableTabs(data) {
      const self = this
      const { toTab: tabId, copyAllWidgets } = data ?? {}
      // Name of the tab the contents of which needs to be copied to the
      // main dashboard since we are disabling the tabs... This is lame
      // feature since we only are able to select 1 dashboard tab...
      this.widgetsToCopy = []
      if (copyAllWidgets) {
        if (tabId == this.dashboardTabId) {
          // current tab id == selected tab id?
          this.widgetsToCopy = this.reset_uid(cloneDeep(this.dashboardLayout))
          console.log('current tab -> ', this.widgetsToCopy)
        } else if (tabId in this.dashboardLayouts) {
          // User might have switch between multiple tabs, the tab data might be in the cache!
          this.widgetsToCopy = this.reset_uid(
            cloneDeep(this.dashboardLayouts[tabId])
          )
          console.log('taken from cache -> ', this.widgetsToCopy)
        } else {
          // Tab data not available locally!
          const { data, error } = await this.fetchDashboardTab(tabId)
          if (!error) {
            const { clientWidgetJson = [] } = data?.dashboardTabContext ?? {}
            self.widgetsToCopy = self.reset_uid(
              self.deserializeWidgets(cloneDeep(clientWidgetJson))
            )
          } else {
            self.$message.error(error.message ?? 'Error Occurred')
          }
          console.log('fetched from server -> ', self.widgetsToCopy)
        }
      }
      await self.changeWidgetsAndSave()
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
    removeWidgets(dasthboardlayout) {
      return dasthboardlayout.filter(widget => {
        if (widget && widget.w && widget.h) {
          return widget
        }
      })
    },
    prepareDashboardTab(dashboardLayout, dashboardTabId, dashboardTabName) {
      let self = this
      this.dashboardSave = true
      let dashboardWidgets = self.getDashboardWidgets(dashboardLayout)
      let dashboardObj = {
        tabId: dashboardTabId,
        tabEnabled: self.dashboardOptions.showTabs,
        dashboardName: dashboardTabName,
        linkName: self.dashboardLink,
        dashboardWidgets: dashboardWidgets,
        dashboardFolderId: self.dashboard.dashboardFolderId
          ? self.dashboard.dashboardFolderId
          : null,
      }
      return dashboardObj
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
    async addNewDashboardTab(data) {
      // There were no tabs, so we showed the user a modal,
      // asked him to create a tab...
      const self = this
      const { tabName, copyAllWidgets } = data ?? {}
      const { error } = await API.post('dashboard/addDashboardTab', {
        dashboardTabContext: {
          name: tabName,
          sequence: 1,
          dashboardId: this.dashboard.id,
        },
      })
      if (isEmpty(error)) {
        // Now that a single tab has been created, enable dashboard tabs...
        const mainDashboardWidgets = cloneDeep(self.dashboardLayout)
        await this.saveTab({ redirect: false })
        // Since this is the first time we are adding a tab to the dashboard
        // the component will automatically take the first tab and assign it as
        // the current tab...
        await this.loadDashboard()
        self.widgetsToCopy = []
        if (copyAllWidgets) {
          // If copyAllWidgets is true, then copy all the widgets present in the dashboard
          // to the dashboard tab...
          // Push the widgets stored in mainDashboardWidgets
          // into  dashboardLayout (the current tabs's layout).
          self.widgetsToCopy = self.reset_uid(mainDashboardWidgets)
        }
        this.$message.success('Dashboard Tab Added Successfully')
      } else {
        this.$message.error(error.message ?? 'Error Occured')
      }
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

    setIdNull() {
      this.dashboardLayout.forEach(rt => {
        if (rt.widget && rt.widget.id) {
          rt.widget.id = -1
        }
      })
    },
    beforeCloseDashboardOption() {
      // this.dashboardOptions.showTabs = !this.dashboardOptions.showTabs
    },
    toggleTabsVisibility() {
      const { showTabs } = this.dashboardOptions
      if (showTabs) {
        this.setDefaultEnableDashboardTabOptions()
      } else {
        // Tabs is enabled, we are disabling it, show a modal...
        this.showDisableTabsModal = true
      }
    },
    setDefaultDisableDashboardTabOptions() {
      this.copyallWidgets = false
      if (this.dashboardTabsList && this.dashboardTabsList.length) {
        this.dashboardTabforWidgets = this.dashboardTabsList[0].id
      }
    },
    async setDefaultEnableDashboardTabOptions() {
      const self = this
      const { data, error } = await API.put('/dashboard/enableDashboardTabs', {
        dashboardId: this.dashboard.id,
      })
      if (isEmpty(error)) {
        if (data?.dashboard) {
          // Tabs enabled -> Tabs disabled -> Tabs enabled back again!
          // Which means tabs already exists so just enable it...
          await self.loadDashboard()
          self.$message.success('Dashboard Tab Enabled Successfully')
        } else {
          // -> Tabs enabled
          self.showEnableDashboardModal = true
        }
      } else {
        self.$message.error(error ?? 'Error Occured')
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
    handleButton(command, sectionIndex = -1) {
      this.sectionIndex = sectionIndex // Note down the index, we will push the widget to the correct index later...
      if (command === 'addCard') {
        this.showCardBuilderSetup = true
      } else if (command === 'addText') {
        this.showtextcard = true
      } else if (command === 'addImage') {
        this.showImageEditer = true
      } else if (command === 'addComponent') {
        this.showReportChooser = true
      } else if (command == 'addSection') {
        // Show the section editer first, so that the user can customize the section
        // before adding it to the dashboard.
        this.sectionSelectedForEditing = undefined
        this.showSectionEditor = true
      }
    },
    updateSection(section) {
      const { id } = section
      const index = this.dashboardLayout.findIndex(s => s.id == id)
      this.$set(this.dashboardLayout, index, cloneDeep(section))
    },
    cloneSection(sectionId) {
      const self = this
      const sectionCopy = cloneDeep(
        this.dashboardLayout.find(s => s.id == sectionId)
      )
      const { children } = sectionCopy
      children.forEach(widget => {
        widget['id'] = self.get_uid()
      })
      sectionCopy['id'] = this.get_uid()
      this.dashboardLayout.push(sectionCopy)
    },
    editSection(id) {
      const index = this.dashboardLayout.findIndex(s => s.id == id)
      this.sectionSelectedForEditing = this.dashboardLayout[index]
      this.showSectionEditor = true
    },
    createSection(section) {
      const { id } = section ?? {}
      if (!id) {
        section['id'] = this.get_uid()
        this.dashboardLayout.push(section)
      }
    },
    addWidgetToSection(id, option) {
      const index = this.dashboardLayout.findIndex(w => w.id == id)
      this.handleButton(option, index)
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
        const uid = this.get_uid()
        layout = {
          id: uid,
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
            id: uid,
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
          id: String(data.id),
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
        const uid = this.get_uid()
        layout = {
          id: uid,
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
            id: uid,
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
        const uid = this.get_uid()
        layout = {
          id: uid,
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
            id: uid,
            newReportId: data.id,
            type: 'chart',
            name: '',
          },
          minW: this.getMinW(ctype),
          minH: this.getMinH(ctype),
          moved: false,
        }
        if (layout.widget.type == 'chart') {
          layout.widget['widgetSettings'] = {
            showHelpText: false,
            excludeDbFilters: false,
          }
          layout.widget['helpText'] = ''
        }
        if (type === 4) {
          layout.widget.dataOptions['reportTemplate'] = data.reportTemplate
        }
      }
      return layout
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
