import Subheader from '@/Subheader'
import { eventBus } from '@/page/widget/utils/eventBus'
import NewDashboard from 'src/pages/new-dashboard/components/dashboard/NewDashboard.vue'
import share from 'src/pages/new-dashboard/components/dashboard/DashboardShare.vue'
import DashboardManager from 'src/pages/dashboard/components/DashboardManager.vue'
import OutsideClick from '@/OutsideClick'
import { mapState, mapGetters } from 'vuex'
import {
  isWebTabsEnabled,
  getApp,
  findRouteForTab,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  data() {
    return {
      showDashboardComments: false,
      subheaderMenu: [],
      isMobileDashboard: false,
      loadAll: false,
      managerloading: false,
      activeScreen: [],
      rawdata: [],
      dashboard: null,
      headerList: [],
      buildings: [],
      Mobilebuildings: [],
      chillers: [],
      Mobilechillers: [],
      sites: [],
      Mobilesites: [],
      subheaderdata: [],
      linkName: 'buildingdashboard',
      commercial: [],
      customDashboard: [],
      chillerDashboard: null,
      loading: false,
      residential: [],
      showDashboardManager: false,
      showListWorkorder: false,
      activeFolderNames: '',
    }
  },
  components: {
    Subheader,
    NewDashboard,
    share,
    DashboardManager,
    OutsideClick,
  },
  computed: {
    ...mapState({
      groups: state => state.groups,
      users: state => state.users,
      roles: state => state.roles,
    }),

    ...mapGetters(['getCurrentUser', 'dashboard/dbFilterQueryParam']),
    currentUser() {
      return this.getCurrentUser()
    },
    getDashboardId() {
      if (this.rawdata) {
        let dashboard = this.rawdata.find(
          rt => rt.linkName === this.dashboardlink || null
        )
        if (dashboard) {
          return dashboard.id
        }
      }
      return null
    },
    query() {
      return this.$route.query
    },
    dashboardLink() {
      let dashboardlink =
        this.$attrs.dashboardlink || this.$route.params.dashboardlink
      if (
        [
          'residentialbuildingdashboard',
          'commercialbuildingdashboard',
        ].includes(dashboardlink)
      ) {
        return 'buildingdashboard'
      } else {
        return dashboardlink
      }
    },
    buildingId() {
      let { buildingid } = this.$attrs || this.$route.params || {}
      return buildingid ? parseInt(buildingid) : null
    },
    dashBoardCEPermission() {
      return `dashboard:UPDATE,CREATE`
    },
    dashBoardSharePermission() {
      return `dashboard:SHARE_DASHBOARD`
    },
    tabId() {
      if (this.$route?.query?.tab) {
        return this.$route.query.tab
      }
      return null
    },
    pdfUrl() {
      let printUrl
      let customAppUrl

      if (isWebTabsEnabled()) {
        let query = { printing: true }

        if (this.tabId) {
          this.$set(query, 'tab', this.tabId)
        }

        customAppUrl = this.$router.resolve({ query }).href
        if (this['dashboard/dbFilterQueryParam']) {
          query['dbFilters'] = this['dashboard/dbFilterQueryParam']
          customAppUrl += '&dbFilters=' + this['dashboard/dbFilterQueryParam']
        }
        customAppUrl = customAppUrl.replace('/?', '?')
        printUrl = `${window.location.protocol}//${window.location.host}${customAppUrl}`
      } else {
        //check for db filters, if any filters applied ,append to print
        let linkname = this.dashboardLink

        printUrl = `${window.location.protocol}//${window.location.host}/app/home/dashboard/${linkname}`
        if (this.tabId) {
          printUrl += `?tab=${this.tabId}&printing=true`
        } else {
          printUrl += `?printing=true`
        }

        if (this['dashboard/dbFilterQueryParam']) {
          printUrl += '&dbFilters=' + this['dashboard/dbFilterQueryParam']
        }
      }
      return printUrl
    },
  },
  created() {
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
    if (this.$account.org.orgId !== 418) this.$store.dispatch('loadBuildings')
    this.$store.dispatch('loadUsers')
  },
  methods: {
    hasEditPermission(dashboard) {
      let previlegedRole = this.$getProperty(
        this,
        'currentUser.role.isPrevileged',
        null
      )
      if (previlegedRole) {
        return true
      } else {
        let locked = false,
          shareContext = [],
          createdByUserId = null,
          createdBy = null
        if (!isEmpty(dashboard?.rawdata)) {
          locked = this.$getProperty(dashboard, 'rawdata.locked', false)
          shareContext = this.$getProperty(
            dashboard,
            'rawdata.dashboardSharingContext',
            []
          )
          createdByUserId = this.$getProperty(
            dashboard,
            'rawdata.createdByUserId',
            null
          )
          createdBy = this.$getProperty(dashboard, 'rawdata.createdBy', null)
        } else {
          locked = dashboard?.locked ?? false
          shareContext = dashboard?.dashboardSharingContext ?? []
          createdByUserId = dashboard?.createdByUserId ?? null
          createdBy = dashboard?.createdBy ?? null
        }
        if (locked) {
          let { peopleId, id, roleId: currentRoleId } =
            this.getCurrentUser() || {}
          const isCreatedUser =
            createdBy === null ? createdByUserId === id : createdBy === peopleId
          if (isCreatedUser) {
            return isCreatedUser
          }
          for (let context of shareContext) {
            let { sharingType, orgUserId, roleId, groupId } = context || {}
            if (sharingType === 1 && id === orgUserId) {
              return false
            } else if (sharingType === 2 && currentRoleId === roleId) {
              return false
            } else if (sharingType === 3) {
              let { groups = [] } = this || {}
              const groupDetail = groups.filter(group => group.id === groupId)
              let { members = [] } = groupDetail || {}
              for (let user of members) {
                if (user.id == id) {
                  return false
                }
              }
            }
          }
          return false
        } else {
          return true
        }
      }
    },
    setBuildingMobileIcon() {
      if (this.buildingId) {
        this.Mobilebuildings.find(
          rt => rt.baseSpaceId === this.buildingId
        ).mobileEnabled = this.isMobileDashboard
      }
    },
    goBack() {
      window.history.go(-1)
    },
    deletedashboardFolder(dashboard, index) {
      let self = this
      let data = {
        dashboardFolderContext: {
          id: dashboard.id,
        },
      }
      self.$dialog
        .confirm({
          title: 'Delete Dashboard Folder',
          message: 'Are you sure you want to delete this Dashboard folder?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(function(value) {
          if (value) {
            self.headerList.splice(index, 1)
            self.$http
              .post('/dashboard/deleteDashboardFolder', data)
              .then(function(response) {
                self.$message({
                  message: 'Dashboard Folder deleted successfully!',
                  type: 'success',
                })
              })
              .catch(function(error) {
                let { errorString } = error.response.data
                self.$message({
                  message: errorString,
                  type: 'error',
                })
              })
          }
        })
    },
    deletedashboard(dashboard, index, idx) {
      let self = this
      let data = {
        dashboardId: dashboard.id,
      }
      self.$dialog
        .confirm({
          title: 'Delete Dashboard',
          message: 'Are you sure you want to delete this Dashboard?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(function(value) {
          if (value) {
            self.headerList[index].childrens.splice(idx, 1)
            self.$http
              .post('/dashboard/deleteDashboard', data)
              .then(function(response) {
                self.$message({
                  message: 'Dashboard deleted successfully!',
                  type: 'success',
                })
              })
              .catch(function(error) {
                let { errorString } = error.response.data
                self.$message({
                  message: errorString,
                  type: 'error',
                })
              })
          }
        })
    },
    dashboardCommand(moduleName) {
      if (moduleName == 'download') {
        this.isPdfDownload = true
      } else if (moduleName === 'dashboardmanager') {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForTab(pageTypes.DASHBOARD_MANAGER) || {}
          name && this.$router.push({ name })
        } else {
          let url = '/app/newdashboardmanager'
          this.$router.push(url)
        }
      } else if (moduleName === 'edit') {
        this.geteditdashboardLink(moduleName)
      } else if (moduleName === 'mobile') {
        this.EnableMobileDashboard()
      } else if (moduleName === 'share') {
        this.sharedialogVisible = true
      } else if (moduleName == 'addDashboardRule') {
        this.gotoDashboardRules('new')
      } else if (moduleName === 'publish') {
        this.publishdialogVisible = true
      } else if (moduleName == 'editFilters') {
        if (this.$refs['dashboardViewer']) {
          this.$refs['dashboardViewer'].editFromDropDown()
        } else {
          eventBus.$emit('editFilterClicked')
        }
      } else if (moduleName === 'openClonePopup') {
        this.isClone = true
        this.dashboardNameObj.name = this.dashboard.label
      } else if (moduleName === 'clone') {
        if (
          this?.dashboardNameObj?.name &&
          this?.dashboardNameObj?.name?.trim() !== ''
        ) {
          this.isClone = false
          this.saving = true
          this.cloneDashboard()
        }
      } else if (moduleName === 'enableDarkMode') {
        this.switchTheme()
      }
    },
    handleCommand(command, thisContext, cancelRedirect) {
      this.activeFolderNames = command
      this.subheaderMenu =
        this.headerList.find(rt => rt.id === command) &&
        this.headerList.find(rt => rt.id === command).childrens
          ? this.headerList.find(rt => rt.id === command).childrens
          : []
      if (this.subheaderMenu.length) {
        let query = this.$helpers.cloneObject(this.query)
        if ((query || {}).tab) {
          delete query.tab
        }
        if (
          this.subheaderMenu[0].label === 'Portfolio' &&
          this.buildings.length === 1 &&
          this.subheaderMenu[0].childrens.length
        ) {
          if (!cancelRedirect) {
            if (isWebTabsEnabled()) {
              let { linkName } = this.subheaderMenu[0].childrens[0]
              let { name } = findRouteForTab(pageTypes.DASHBOARD_VIEWER)

              if (name) {
                this.$router
                  .push({
                    name,
                    params: { dashboardlink: linkName },
                    query,
                  })
                  .catch(() => {})
              }
            } else {
              this.$router
                .push({
                  path: this.subheaderMenu[0].childrens[0].path.path,
                  query: query,
                })
                .catch(() => {})
            }
          }
        } else {
          if (!cancelRedirect) {
            if (isWebTabsEnabled()) {
              let { linkName } = this.subheaderMenu[0]
              let { name } = findRouteForTab(pageTypes.DASHBOARD_VIEWER)

              if (name) {
                this.$router
                  .push({
                    name,
                    params: { dashboardlink: linkName },
                    query,
                  })
                  .catch(() => {})
              }
            } else {
              this.$router
                .push({
                  path: this.subheaderMenu[0].path.path,
                  query: query,
                })
                .catch(() => {})
            }
          }
        }
      }
    },
    handleCommand3(command, path) {
      this.activeFolderNames = command
      this.subheaderMenu =
        this.headerList.find(rt => rt.id === command) &&
        this.headerList.find(rt => rt.id === command).childrens
          ? this.headerList.find(rt => rt.id === command).childrens
          : []
      if (this.subheaderMenu.length) {
        this.$router.push({
          path: path,
          query: this.query,
        })
      }
    },
    handleCommand2(command) {
      this.activeFolderNames = command
      this.subheaderMenu =
        this.headerList.find(rt => rt.id === command) &&
        this.headerList.find(rt => rt.id === command).childrens
          ? this.headerList.find(rt => rt.id === command).childrens
          : []
      if (this.subheaderMenu.length) {
        this.$router
          .push({
            query: this.query,
          })
          .catch(() => {})
      }
    },
    getdashboardObj(data) {
      if (data) {
        this.dashboard = data
      }
    },
    EnableMobileDashboard() {
      let self = this
      self.isMobileDashboard = !self.isMobileDashboard
      let params = {
        dashboard: {
          id: this.dashboard.id,
          mobileEnabled: self.isMobileDashboard,
        },
      }
      if (this.buildingId) {
        params.dashboard.buildingId = this.buildingId
      }
      if (this.Mobilebuildings.length) {
        self.setBuildingMobileIcon()
      }
      self.$http.post('/dashboard/updateDashboard', params).then(function() {
        if (self.isMobileDashboard) {
          self.$message({
            message: 'Mobile dashboard enabled successfully!',
            type: 'success',
          })
        } else {
          self.$message({
            message: 'Mobile dashboard disabled successfully!',
            type: 'success',
          })
        }
      })
    },
    EnableMobileDashboardInManager(dashboard, modeule) {
      let self = this
      dashboard.rawdata.mobileEnabled = !dashboard.rawdata.mobileEnabled
      self.$forceUpdate()
      let params = {
        dashboard: {
          id: dashboard.id,
          mobileEnabled: dashboard.rawdata.mobileEnabled,
        },
      }
      self.$http.post('/dashboard/updateDashboard', params).then(function() {
        if (dashboard.rawdata.mobileEnabled) {
          self.$message({
            message: 'Mobile dashboard enabled successfully!',
            type: 'success',
          })
        } else {
          self.$message({
            message: 'Mobile dashboard disabled successfully!',
            type: 'success',
          })
        }
      })
    },
    mobiledashboardstatus(data) {
      if (this.buildingId) {
        if (this.Mobilebuildings.length) {
          this.isMobileDashboard = this.Mobilebuildings.find(
            rt => rt.baseSpaceId === this.buildingId
          )
            ? this.Mobilebuildings.find(
                rt => rt.baseSpaceId === this.buildingId
              ).mobileEnabled
            : false
        } else {
          this.isMobileDashboard = false
        }
      } else {
        this.isMobileDashboard = data
      }
    },
    enableRule() {
      let orgDetails = [190, 151, 75, 78, 176, 210]
      if (this.getCurrentModule().module === 'energydata') {
        return true
      } else if (
        this.getCurrentModule().module === 'workorder' &&
        orgDetails.find(rt => rt === this.$account.org.id)
      ) {
        return true
      } else {
        return false
      }
    },
    exitfullscreen() {
      document.addEventListener('fullscreenchange', function() {
        if (
          document.getElementsByClassName('headingFlSc') &&
          document.getElementsByClassName('headingFlSc')[0]
        ) {
          document.getElementsByClassName('headingFlSc')[0].style.display =
            'none'
        }
      })
    },
    goInFullscreen() {
      // let fullscreen = document.getElementById('q-app').clientHeight
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
    goInFullscreenBody: function() {
      // let element = document.getElementsByClassName('dashboardmainlayout')[0]
      // This method needs serious refactoring, accessing the DOM this heavily is
      // not recommended. Use $el or $parent etc, instead of document.
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
      document.addEventListener('fullscreenchange', function() {
        if (document.fullscreenElement) {
          document.getElementsByClassName(
            'gridstack-container'
          )[0].style.background = 'white'
          document.getElementsByClassName(
            'dashboardmainlayout'
          )[0].style.background = 'white'
          document.getElementsByClassName(
            'dashboard-container'
          )[0].style.paddingBottom = '0px'
          if (
            document.getElementsByClassName('headingFlSc') &&
            document.getElementsByClassName('headingFlSc')[0]
          ) {
            let body = document.body

            body.classList.add('dashboard-widget-view')

            document.getElementsByClassName('headingFlSc')[0].style.display =
              'block'
          }
          if (
            document.getElementsByClassName('single-dashboard').length === 1
          ) {
            document.getElementsByClassName(
              'fc-widget-header'
            )[0].style.display = 'none'
          }
          if (
            document.getElementsByClassName('dashboardmainlayout') &&
            document.getElementsByClassName('dashboardmainlayout')[0]
          ) {
            document.getElementsByClassName(
              'dashboardmainlayout'
            )[0].style.paddingBottom = '0px'
          }
        } else {
          let body = document.body
          if (
            document.getElementsByClassName('single-dashboard').length === 1
          ) {
            document.getElementsByClassName(
              'fc-widget-header'
            )[0].style.display = 'flex'
          }
          body.classList.remove('dashboard-widget-view')
          document.getElementsByClassName(
            'gridstack-container'
          )[0].style.background = 'none'
          document.getElementsByClassName(
            'dashboardmainlayout'
          )[0].style.background = 'none'
          document.getElementsByClassName(
            'dashboard-container'
          )[0].style.paddingBottom = '50px'
          if (
            document.getElementsByClassName('headingFlSc') &&
            document.getElementsByClassName('headingFlSc')[0]
          ) {
            document.getElementsByClassName('headingFlSc')[0].style.display =
              'none'
          }
          if (
            document.getElementsByClassName('single-dashboard').length === 1
          ) {
            document.getElementsByClassName(
              'fc-widget-header'
            )[0].style.display = 'block'
          }
          if (
            document.getElementsByClassName('dashboardmainlayout') &&
            document.getElementsByClassName('dashboardmainlayout')[0]
          ) {
            document.getElementsByClassName(
              'dashboardmainlayout'
            )[0].style.paddingBottom = '110px'
          }
        }
      })
    },
    toggleFilters() {
      if (this.$route.query.filters) {
        let filters = this.$route.query.filters
        this.$router.replace({
          query: {
            showFilter: true,
            filters: filters,
          },
        })
      } else {
        this.$router.replace({
          query: {
            showFilter: true,
          },
        })
      }
    },
    toggleTemplate() {
      if (this.$route.query.templateParams) {
        let filters = this.$route.query.templateParams
        this.$router.replace({
          query: {
            showTemplate: true,
            filters: filters,
          },
        })
      } else {
        this.$router.replace({
          query: {
            showTemplate: true,
          },
        })
      }
    },
    getCurrentModule(moduleData) {
      if (!this.$route.params.moduleName) {
        return null
      }
      let routeObj = this.$route
      let module = null
      let rootPath = null
      if (routeObj.params.moduleName) {
        this.$route.meta.module = routeObj.params.moduleName
        module = routeObj.params.moduleName
        rootPath = routeObj.path
      } else {
        if (routeObj.matched) {
          for (let matchedRoute of routeObj.matched) {
            if (matchedRoute.params.moduleName) {
              this.$route.meta.module = matchedRoute.params.moduleName
              module = matchedRoute.params.moduleName
              rootPath = matchedRoute.path
              break
            }
          }
        }
      }
      if (moduleData) {
        return moduleData
      } else {
        return {
          module: module,
          rootPath: rootPath,
        }
      }
    },
    gotoDashboardRules(mode) {
      const linkname = this.dashboardLink
      let dashboardTabId = null
      if(!isEmpty(this.$route.query) && !isEmpty(this.$route.query.tab))
      {
        dashboardTabId = this.$route.query.tab
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.DASHBOARD_RULES) || {}
        if (name) {
          let query = {create: mode == 'new' ? 'new' : 'edit'}
          if(dashboardTabId){
            query.tabId= dashboardTabId
          }
          this.$router
            .push({
              name,
              params: { dashboardlink: linkname },
              query: query,
            })
            .catch(() => {})
        }
      } else {
        let url = '/app/home/dashboardrules/' + linkname
        if (mode === 'new') {
          this.$router.push(url + '?create=new')
        } else {
          this.$router.push(url + '?create=edit')
        }
      }
    },
    geteditdashboardLink(mode) {
      if (mode) {
        if (isWebTabsEnabled()) {
          if (mode === 'new') {
            let { name } = findRouteForTab(pageTypes.DASHBOARD_CREATION) || {}

            if (name) {
              this.$router.push({ name, query: { create: 'new' } })
            }
          } else {
            let { name } = findRouteForTab(pageTypes.DASHBOARD_EDITOR) || {}

            if (name) {
              let params = {
                dashboardlink: this.dashboardLink,
              }

              if (
                this.dashboardLink === 'buildingdashboard' &&
                this.buildingId
              ) {
                params[buildingid] = this.buildingId
              }

              this.$router.push({
                name,
                params,
                query: { create: 'edit' },
              })
            }
          }
        } else {
          let linkname = this.dashboardLink
          if (linkname === 'buildingdashboard' && this.buildingId) {
            linkname += '/' + this.buildingId
          }
          let url = '/app/home/editdashboard/' + linkname
          if (mode === 'new') {
            this.$router.push(url + '?create=new')
          } else {
            this.$router.push(url + '?create=edit')
          }
        }
      }
    },
    getMobiledashboardList(dashboard) {
      let list = dashboard.find(rt => rt.linkName === 'buildingdashboard')
      if (
        list &&
        list.spaceFilteredDashboardSettings &&
        list.spaceFilteredDashboardSettings.length
      ) {
        this.Mobilebuildings = list.spaceFilteredDashboardSettings
        this.mobiledashboardstatus(false)
      }
    },
    getSubheaderFormater(response) {
      let dashboardlist = response.data.dashboardFolders
      let self = this
      return self.getsubheaderData(dashboardlist)
    },
    loadDashboardFolders(appId) {
      let url = '/dashboardWithFolder'
      if (!isEmpty(appId)) {
        url += '?appId=' + appId
      }
      let self = this
      self.$http
        .get(url)
        .then(function(response) {
          self.dashboardCloneObj.folder = null
          self.moveDashboardObj.folder = null
          self.dashboardFoldersList = response?.data?.dashboardFolders
            ? response.data.dashboardFolders
            : []
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    isPortalApp() {
      let {
        appCategory: { PORTALS },
      } = this.$constants
      if (getApp() && getApp().appCategory === PORTALS) {
        return true
      }
      return false
    },
    loadDashboards(disableRedirect, appId) {
      this.subheaderdata = []
      if (this?.dashboardCloneObj) {
        this.dashboardCloneObj.selectedAppId = appId
      }
      if (this?.moveDashboardObj) {
        this.moveDashboardObj.selectedAppId = appId
      }
      // let url = '/dashboardWithFolder'
      // if (!isEmpty(appId)) {
      //   url += '?appId=' + appId
      // }
      let url = 'v3/dashboard/list'
      let params = {
        appId: !isEmpty(appId) ? appId : getApp().id,
        withSharing: true,
        withEmptyFolders: true,
      }
      let self = this
      self.loading = true

      API.get(url, params)
        .then(function(response) {
          self.subheaderdata = self.getSubheaderFormater(response)
          self.dashboardFoldersList = response?.data?.dashboardFolders
            ? response.data.dashboardFolders
            : []
          self.headerList = self.subheaderdata
          if (!disableRedirect) {
            self.setDefaultdashboard(self.subheaderdata)
          }
          self.loading = false
        })
        .catch(function(error) {
          self.loading = false
          console.log(error)
        })
    },
    setDefaultdashboard(dashboard) {
      if (dashboard && dashboard.length) {
        let self = this
        let filterdDashboard = null
        filterdDashboard = dashboard.find(function(rt) {
          if (
            self.$route.path &&
            rt.childrens.find(function(rl) {
              let { linkName } = rl

              if (
                rl.label === 'Portfolio' &&
                rl.childrens &&
                rl.childrens.length &&
                rl.childrens.find(
                  rx => rx.linkName === self.$route.params.dashboardlink
                )
              ) {
                return rt
              } else if (linkName === self.$route.params.dashboardlink) {
                return rt
              }
            })
          ) {
            return rt
          }
        })
        if (filterdDashboard && this.dashboardLink) {
          this.activeFolderNames = filterdDashboard.id
          this.handleCommand2(this.activeFolderNames)
        } else {
          //  this.activeFolderNames = dashboard.find(rt => rt.label === 'default').id || -1
          if (dashboard[0].childrens) {
            this.activeFolderNames = dashboard.find(rt => {
              if (rt.childrens !== null && rt.childrens.length) {
                return rt
              }
            }).id
          }
          this.handleCommand(this.activeFolderNames)
        }
      }
    },
    displayOrder(dashboards) {
      if (dashboards !== null) {
        let sortedDashboards = dashboards.sort((a, b) => {
          if (a.displayOrder < b.displayOrder) {
            return -1
          }
          if (a.displayOrder > b.displayOrder) {
            return 1
          }
          return 0
        })
        let unOrderArray = []
        let orderArray = []
        sortedDashboards.forEach(element => {
          if (element.displayOrder === -1) {
            unOrderArray.push(element)
          } else if (element.displayOrder === null) {
            unOrderArray.push(element)
          } else {
            orderArray.push(element)
          }
        })
        return [...orderArray, ...unOrderArray]
      } else {
        return []
      }
    },
    getsubheaderchildren(list) {
      let data = []
      let self = this
      list = this.displayOrder(list)
      if (list.length) {
        list.forEach(rt => {
          let listobj = {
            label: rt.dashboardName,
            path: {
              path: '/app/home/dashboard/' + rt.linkName,
              editPath:
                '/app/home/editdashboard/' + rt.linkName + '?create=edit',
            },
            // permission: this.dashboardpermission(rt.moduleName),
            id: rt.id ? rt.id : -1,
            rawdata: rt,
            linkName: rt.linkName,
            collapse: false,
          }
          let copyObj = self.$helpers.cloneObject(listobj)
          if (
            rt.linkName.toLowerCase() === 'energyportfolio' &&
            list.filter(
              rt => rt.linkName.toLowerCase().indexOf('buildingdashboard/') > -1
            ).length
          ) {
            listobj['childrens'] = self.groupBuildingDashboard(list, copyObj)
          } else if (
            rt.linkName.toLowerCase() === 'alarmportfolio' &&
            list.filter(
              rt => rt.linkName.toLowerCase().indexOf('buildingdashboard/') > -1
            ).length
          ) {
            listobj['childrens'] = self.groupBuildingDashboard(list, copyObj)
          } else if (
            rt.linkName.toLowerCase() === 'maintenanceportfolio' &&
            list.filter(
              rt => rt.linkName.toLowerCase().indexOf('buildingdashboard/') > -1
            ).length
          ) {
            listobj['childrens'] = self.groupBuildingDashboard(list, copyObj)
          } else if (
            rt.linkName.toLowerCase() === 'portfolio' &&
            list.filter(
              rt => rt.linkName.toLowerCase().indexOf('buildingdashboard/') > -1
            ).length
          ) {
            listobj['childrens'] = self.groupBuildingDashboard(list, copyObj)
          }
          data.push(listobj)
        })
        if (
          list.find(rt => rt.linkName.toLowerCase() === 'energyportfolio') &&
          list.filter(
            rt => rt.linkName.toLowerCase().indexOf('buildingdashboard/') > -1
          ).length
        ) {
          return self.removeGroupedDashboardFromMainList(data)
        } else if (
          list.find(rt => rt.linkName.toLowerCase() === 'alarmportfolio') &&
          list.filter(
            rt => rt.linkName.toLowerCase().indexOf('buildingdashboard/') > -1
          ).length
        ) {
          return self.removeGroupedDashboardFromMainList(data)
        } else if (
          list.find(
            rt => rt.linkName.toLowerCase() === 'maintenanceportfolio'
          ) &&
          list.filter(
            rt => rt.linkName.toLowerCase().indexOf('buildingdashboard/') > -1
          ).length
        ) {
          return self.removeGroupedDashboardFromMainList(data)
        } else if (
          list.find(rt => rt.linkName.toLowerCase() === 'portfolio') &&
          list.filter(
            rt => rt.linkName.toLowerCase().indexOf('buildingdashboard/') > -1
          ).length
        ) {
          return self.removeGroupedDashboardFromMainList(data)
        }
        return data
      }
    },
    removeGroupedDashboardFromMainList(listobj) {
      return listobj.filter(
        rt => rt.linkName.toLowerCase().indexOf('buildingdashboard/') < 0
      )
    },
    groupBuildingDashboard(list, obj) {
      if (list.length && obj) {
        return [
          ...[obj],
          ...this.subHeaderFormat(
            this.displayOrder(
              list.filter(
                rt =>
                  rt.linkName.toLowerCase().indexOf('buildingdashboard/') > -1
              )
            )
          ),
        ]
      }
      return []
    },
    subHeaderFormat(list) {
      let data = []
      list = this.displayOrder(list)
      if (list.length) {
        list.forEach(rt => {
          let listobj = {
            label: rt.dashboardName,
            path: {
              path: '/app/home/dashboard/' + rt.linkName,
              editPath:
                '/app/home/editdashboard/' + rt.linkName + '?create=edit',
            },
            // permission: this.dashboardpermission(rt.moduleName),
            id: rt.id ? rt.id : -1,
            rawdata: rt,
            collapse: false,
          }
          data.push(listobj)
        })
        return data
      }
      return data
    },
    dashboardpermission(moduleName) {
      if (moduleName === 'energydata') {
        moduleName = 'energy'
      }
      return moduleName + ':VIEW_DASHBOARDS'
    },
    getsubheaderData(list) {
      let data = []
      list = this.displayOrder(list)
      list.forEach(rt => {
        let dashboardlist = {
          label: rt.name !== 'default' ? rt.name : 'Default',
          path: {
            path: '/app/home/dashboard/' + rt.linkName,
            editPath: '/app/home/editdashboard/' + rt.linkName + '?create=edit',
          },
          // permission: this.dashboardpermission(rt.moduleName),
          id: rt.id ? rt.id : -1,
          rawdata: rt,
        }
        dashboardlist.childrens = []
        if (rt.dashboards) {
          dashboardlist.childrens = this.getsubheaderchildren(rt.dashboards)
        }
        data.push(dashboardlist)
      })
      return data
    },
    getDashboardSharedInfo(sharedList) {
      if (sharedList) {
        let data = {
          users: [],
          roles: [],
          groups: [],
        }
        let shareTo = ''
        let self = this
        sharedList.forEach(function(rt) {
          if (rt.sharingType === 1) {
            data.users.push(self.users.find(rl => rl.id === rt.orgUserId))
          } else if (rt.sharingType === 2) {
            data.roles.push(self.roles.find(rl => rl.id === rt.roleId))
          } else if (rt.sharingType === 3) {
            data.groups.push(self.groups.find(rl => rl.id === rt.groupId))
          }
        })
        data.users = data.users.filter(element => {
          return element !== undefined
        })
        if (!sharedList.length) {
          shareTo = 'Everyone'
        } else if (
          sharedList.length === 1 &&
          data.users.length === 1 &&
          data.users[0].id === this.getCurrentUser().ouid
        ) {
          shareTo = 'Only Me'
        } else {
          shareTo = 'Specific'
        }
        return {
          message: '',
          data: data,
          shareTo: shareTo,
        }
      } else {
        return {
          message: '',
          data: {},
        }
      }
    },
    async cloneDashboard() {
      let linkName = this.dashboardLink
      if (linkName && linkName.trim() !== '') {
        let params = {
          dashboard_link_name: linkName,
          cloned_dashboard_name: this.dashboardNameObj.name,
        }
        let { data, error } = await API.post('v3/dashboard/clone', {
          data: params,
        })
        this.saving = false
        if (!error && data?.dashboard_link_name) {
          this.redirectClonedDashboardToEdit(data.dashboard_link_name)
        } else {
          this.$message.error('Error while cloning dashboard')
        }
      }
    },
    redirectClonedDashboardToEdit(dashboard_link_name) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.DASHBOARD_EDITOR) || {}
        if (name) {
          let params = {
            dashboardlink: dashboard_link_name,
          }
          if (this.dashboardLink === 'buildingdashboard' && this.buildingId) {
            params[buildingid] = this.buildingId
          }
          this.$router.push({ name, params, query: { create: 'edit' } })
        }
      } else {
        let link =
          '/app/home/neweditdashboard/' + dashboard_link_name + '?create=edit'
        this.$router.push(link)
      }
    },
    async cloneDashboardToPortal(isCloneToAnotherPortal) {
      let dashboardObj = this.dashboardCloneObj
      let params = null
      if (
        dashboardObj?.folder > 0 &&
        dashboardObj?.name &&
        dashboardObj?.name.trim() !== '' &&
        dashboardObj?.selectedAppId > 0 &&
        dashboardObj?.dashboard?.id > 0
      ) {
        params = {
          dashboard_link_name: dashboardObj.dashboard.linkName,
          cloned_dashboard_name: dashboardObj.name,
          folder_id: dashboardObj.folder,
        }
      } else if (
        dashboardObj?.name &&
        dashboardObj?.name.trim() !== '' &&
        dashboardObj?.dashboard?.id > 0
      ) {
        params = {
          dashboard_link_name: dashboardObj.dashboard.linkName,
          cloned_dashboard_name: dashboardObj.name,
        }
      }
      if (params) {
        this.dashboardSaving = true
        let { data, error } = await API.post('v3/dashboard/clone', {
          data: params,
        })
        this.isClone = false
        if (!error && data?.dashboard_link_name) {
          this.$message.success('Dashboard Cloned Successfully')
        } else {
          this.$message.error('Error while cloning dashboard')
        }
      }
      this.dashboardSaving = false
    },
    async moveDashboardToPortal() {
      let dashboardObj = this.moveDashboardObj
      if (
        dashboardObj?.folder > 0 &&
        dashboardObj?.selectedAppId > 0 &&
        dashboardObj?.dashboard?.id > 0
      ) {
        this.dashboardSaving = true
        let params = {
          dashboard_link: dashboardObj.dashboard.linkName,
          folder_id: dashboardObj.folder,
        }
        let { data, error } = await API.post('v3/dashboard/moveto', {
          data: params,
        })
        this.isMoveTo = false
        if (!error && data?.result === 'success') {
          this.loadDashboards(true, this.appId)
          this.$message.success('Dashboard Moved Successfully')
        } else {
          this.$message.error('Error while moving dashboard')
        }
      }
      this.dashboardSaving = false
    },
  },
}
