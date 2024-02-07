import Subheader from '@/Subheader'
import NewDashboard from './../forms/NewDashboard'
import share from './../components/DashboardShare'
import DashboardManager from './../components/DashboardManager'
import OutsideClick from '@/OutsideClick'
import { mapState, mapGetters } from 'vuex'

export default {
  data() {
    return {
      showDashboardComments: false,
      subheaderMenu: [],
      isMobileDashboard: false,
      portfoliolinkName: {
        energyportfolio: 'energyportfolio',
        alarmportfolio: 'alarmportfolio',
        maintenanceportfolio: 'maintenanceportfolio',
        portfolio: 'portfolio',
      },
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
  watch: {
    $route: function(from, to) {
      console.log('********', this.dashboard)
      if (
        (this.dashboard && this.$route.path.endsWith('/dashboard')) ||
        (this.dashboard && this.$route.path.endsWith('/newdashboard'))
      ) {
        if (this.dashboard.dashboardFolderId) {
          this.handleCommand(this.dashboard.dashboardFolderId)
        } else {
          this.handleCommand(this.dashboard.id)
        }
      }
    },
  },
  created() {
    this.$store.dispatch('loadEnergyMeters')
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadBuildings')
    this.$store.dispatch('loadUsers')
  },
  computed: {
    ...mapState({
      groups: state => state.groups,
      users: state => state.users,
      roles: state => state.roles,
    }),

    ...mapGetters(['getCurrentUser']),

    getDashboardId() {
      let self = this
      if (self.rawdata) {
        let dashboard = self.rawdata.find(
          rt =>
            rt.linkName ===
            (this.$route.params ? this.$route.params.dashboardlink : null)
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
    dashboardModules() {
      return {
        workorder: {
          modulePath: '/app/wo',
          buildingDashboards: this.getBuildingDashboards(
            '/app/wo',
            'newdashboard'
          ),
          module: !this.loadAll
            ? this.getCurrentModule()
            : {
                module: 'workorder',
              },
          dashboardLink: 'dashboard',
        },
        alarm: {
          modulePath: '/app/fa',
          buildingDashboards: this.getBuildingDashboards(
            '/app/fa',
            'dashboard'
          ),
          module: !this.loadAll
            ? this.getCurrentModule()
            : {
                module: 'alarm',
              },
          dashboardLink: 'dashboard',
        },
        energydata: {
          modulePath: '/app/em',
          buildingDashboards: this.getBuildingDashboards(
            '/app/em',
            'dashboard'
          ),
          module: !this.loadAll
            ? this.getCurrentModule()
            : {
                module: 'energydata',
              },
          dashboardLink: 'newdashboard',
        },
      }
    },
    dashboardLink() {
      if (
        this.$route.params.dashboardlink === 'residentialbuildingdashboard' ||
        this.$route.params.dashboardlink === 'commercialbuildingdashboard'
      ) {
        return 'buildingdashboard'
      } else {
        return this.$route.params.dashboardlink
      }
    },
    buildingList() {
      return Object.keys(this.$store.getters.getBuildingsPickList()).map(
        key => {
          return {
            id: parseInt(key),
            name: this.$store.getters.getBuildingsPickList()[key],
          }
        }
      )
    },
    buildingId() {
      return this.$route.params.buildingid
        ? parseInt(this.$route.params.buildingid)
        : null
    },
    dashBoardViewPermission() {
      let currentModule = this.getCurrentModule()
      let moduleName = currentModule.module
      if (currentModule.module === 'energydata') {
        moduleName = 'energy'
      }
      return moduleName + ':VIEW_DASHBOARDS'
    },
    dashBoardCEPermission() {
      let currentModule = this.getCurrentModule()
      let moduleName = currentModule.module
      if (currentModule.module === 'energydata') {
        moduleName = 'energy'
      }
      return moduleName + ':CREATE_EDIT_DASHBOARD'
    },
  },
  methods: {
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
    goInFullscreen: function() {
      let element = document.getElementsByClassName('dashboardmainlayout')[0]
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
      document.addEventListener('fullscreenchange', function() {
        if (document.fullscreenElement) {
          if (
            (document.getElementsByClassName('vue-grid-layout')[0] &&
              document.getElementsByClassName('vue-grid-layout')[0].style) ||
            (document.getElementsByClassName('dashboardmainlayout')[0] &&
              document.getElementsByClassName('dashboardmainlayout')[0].style)
          ) {
            document.getElementsByClassName(
              'vue-grid-layout'
            )[0].style.background = 'white'
            document.getElementsByClassName(
              'dashboardmainlayout'
            )[0].style.background = 'white'
          }
          document.getElementsByClassName(
            'dashboard-container'
          )[0].style.paddingBottom = '0px'
          if (
            document.getElementsByClassName('headingFlSc') &&
            document.getElementsByClassName('headingFlSc')[0]
          ) {
            document.getElementsByClassName('headingFlSc')[0].style.display =
              'block'
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
          document.getElementsByClassName(
            'vue-grid-layout'
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
            self.dashboardlist[self.querryModule].dashboards[
              index
            ].childrens.splice(idx, 1)
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
            self.dashboardlist[self.querryModule].dashboards.splice(index, 1)
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
    dashboardCommand(modeule) {
      if (modeule === 'dashboardmanager') {
        let url = '/app/dashboardmanager'
        if (this.getCurrentModule().module) {
          url += '?module=' + this.getCurrentModule().module
        }
        this.$router.push(url)
      } else if (modeule === 'edit') {
        this.geteditdashboardLink(modeule)
      } else if (modeule === 'mobile') {
        this.EnableMobileDashboard()
      } else if (modeule === 'share') {
        this.sharedialogVisible = true
      } else if (modeule === 'publish') {
        this.publishdialogVisible = true
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
        if (
          this.subheaderMenu[0].label === 'Portfolio' &&
          this.buildings.length === 1 &&
          this.subheaderMenu[0].childrens.length
        ) {
          if (!cancelRedirect) {
            if (this.$route.path.includes('dashboard')) {
              this.$router.push({
                path: this.subheaderMenu[0].childrens[0].path.path,
                query: this.query,
              })
            }
          }
        } else {
          if (!cancelRedirect) {
            if (this.$route.path.includes('dashboard')) {
              this.$router.push({
                path: this.subheaderMenu[0].path.path,
                query: this.query,
              })
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
        if (this.$route.path.indexOf('dashboard') > -1) {
          this.$router.push({
            path: path,
            query: this.query,
          })
        }
      }
    },
    handleCommand2(command, path) {
      this.activeFolderNames = command
      this.subheaderMenu =
        this.headerList.find(rt => rt.id === command) &&
        this.headerList.find(rt => rt.id === command).childrens
          ? this.headerList.find(rt => rt.id === command).childrens
          : []
      if (this.subheaderMenu.length) {
        if (this.$route.path.indexOf('dashboard') > -1) {
          this.$router.push({
            path: path,
            query: this.query,
          })
        }
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
      // if (this.buildingId) {
      //   params.dashboard.buildingId = this.buildingId
      // }
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
    loadBuildings(moduleData) {
      let self = this
      this.linkName = 'buildingdashboard'
      let params = {}
      this.loading = true
      return new Promise(resolve => {
        let currentModule = moduleData ? moduleData : this.getCurrentModule()
        let url = '/report/energy/portfolio/getAllBuildingsWithRootMeter'
        if (
          currentModule.module === 'workorder' ||
          currentModule.module === 'alarm'
        ) {
          self.buildings = self.buildingList
          if (self.loadAll) {
            self.dashboardlist[currentModule.module].buildings =
              self.buildingList
          }
          resolve()
          return
        }
        self.$http
          .post(url, params)
          .then(function(response) {
            if (currentModule.module === 'energydata') {
              self.buildings = response.data.reportData.buildingsWithRootMeter
              if (self.loadAll) {
                self.dashboardlist[currentModule.module].buildings =
                  response.data.reportData.buildingsWithRootMeter
              }
              resolve(response)
            } else {
              self.buildings = response.data
              if (self.loadAll) {
                self.dashboardlist[currentModule.module].buildings =
                  response.data
              }
              resolve(response)
            }
          })
          .catch(function(error) {
            console.log(error)
          })
      })
    },
    loadSite() {
      let self = this
      self.$http.get('/campus').then(response => {
        self.sites = response.data.records
      })
    },
    loadChillers() {
      let self = this
      if (self.$account.org.id === 133) {
        self.$http
          .get('/report/energy/portfolio/getAllChiller')
          .then(function(response) {
            if (response.data) {
              self.chillers = response.data.chillerPlantsJson
            }
          })
          .catch(function(error) {
            console.log(error)
          })
      }
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
      let routeObj = this.$route
      let module = null
      let rootPath = null
      if (routeObj.meta.module) {
        module = routeObj.meta.module
        rootPath = routeObj.path
      } else {
        if (routeObj.matched) {
          for (let matchedRoute of routeObj.matched) {
            if (matchedRoute.meta.module) {
              module = matchedRoute.meta.module
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
    geteditdashboardLink(mode) {
      if (mode) {
        if (mode === 'new') {
          let url = this.constractUrl('new') + '?create=new'
          this.$router.push(url)
        } else {
          let url =
            this.constractUrl() +
            '?create=edit' +
            (this.$route.query.tab ? '&tab=' + this.$route.query.tab : '')
          this.$router.push(url)
        }
      }
    },
    constractUrl(mode) {
      let module = this.getCurrentModule().module
      let link = this.$router.currentRoute.params.dashboardlink
      if (link === 'buildingdashboard') {
        link = link + '/' + this.$router.currentRoute.params.buildingid
      }
      if (mode === 'new') {
        if (module === 'workorder') {
          return '/app/wo/dbediter'
        } else if (module === 'alarm') {
          return '/app/fa/dbediter'
        } else if (module === 'energydata') {
          return '/app/em/dbediter'
        } else {
          return '/app/wo/dbediter'
        }
      } else {
        if (module === 'workorder') {
          return '/app/wo/dbediter/' + link
        } else if (module === 'alarm') {
          return '/app/fa/dbediter/' + link
        } else if (module === 'energydata') {
          return '/app/em/dbediter/' + link
        } else {
          return '/app/wo/dbediter/' + link
        }
      }
    },
    getBuildingDashboards(basePath, dashboardLink, siteType) {
      let menuList = []
      let buildingList =
        siteType === 'commercial'
          ? this.commercial
          : siteType === 'residential'
          ? this.residential
          : this.buildings
      let type =
        siteType === 'commercial'
          ? '/commercialbuildingdashboard/'
          : siteType === 'residential'
          ? '/residentialbuildingdashboard/'
          : '/buildingdashboard/'
      for (let building of buildingList) {
        if (building.space) {
          building.id = building.space
        }
        let buildingId = parseInt(building.id)
        if (basePath.indexOf('/em') !== -1) {
          if (this.$store.state.energyMeters) {
            let energyMeter = this.$store.state.energyMeters.find(
              em => em.purposeSpace.id === buildingId
            )
            if (!energyMeter) {
              continue
            }
          }
        }
        menuList.push({
          label: building.name,
          path: {
            path: basePath + '/' + dashboardLink + type + building.id,
            editPath:
              basePath +
              '/' +
              'dbediter/' +
              type +
              building.id +
              '?create=edit',
          },
          permission: this.dashBoardViewPermission,
          id: building.id,
          rawdata: building,
        })
      }
      return menuList
    },
    getSiteDashboards(basePath, dashboardLink) {
      let menuList = []
      let siteList = this.sites
      let type = '/sitedashboard/'
      for (let site of siteList) {
        menuList.push({
          label: site.name,
          path: {
            path: basePath + '/' + dashboardLink + type + site.id,
            editPath:
              basePath + '/' + 'dbediter/' + type + site.id + '?create=edit',
          },
          permission: this.dashBoardViewPermission,
          id: site.id,
          rawdata: site,
        })
      }
      return menuList
    },
    loadSiteDashboard(dashboards) {
      let self = this
      this.linkName = 'sitedashboard'
      let currentModule = this.getCurrentModule()
      let modulePath = '/app/wo'
      let buildingDashboards = null
      let dashboardLink = 'dashboard'
      if (currentModule.module === 'alarm') {
        modulePath = '/app/fa'
        buildingDashboards = this.getSiteDashboards(modulePath, dashboardLink)
        self.setTitle('Alarms Dashboard')
      } else if (currentModule.module === 'workorder') {
        modulePath = '/app/wo'
        buildingDashboards = this.getSiteDashboards(modulePath, dashboardLink)
      } else if (currentModule.module === 'energydata') {
        modulePath = '/app/em'
        buildingDashboards = this.getSiteDashboards(modulePath, dashboardLink)
      }
      if (buildingDashboards.length === 1) {
        dashboards = dashboards.filter(el => {
          if (el.linkName !== 'portfolio') {
            return el
          }
        })
      }
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
        if (element.displayOrder == null) {
          unOrderArray.push(element)
        } else {
          orderArray.push(element)
        }
      })
      for (let dashboard of [...orderArray, ...unOrderArray]) {
        if (
          ['portfolio'].includes(dashboard.linkName) &&
          [...orderArray, ...unOrderArray].find(el => {
            return el.linkName === 'sitedashboard'
          })
        ) {
          let dashboards = buildingDashboards
          dashboards.unshift({
            label: dashboard.dashboardName,
            path: {
              path: modulePath + '/' + dashboardLink + '/' + dashboard.linkName,
            },
            permission: self.dashBoardViewPermission,
            id: dashboard.id ? dashboards.id : -1,
          })
          self.subheaderMenu.push({
            label: dashboard.dashboardName,
            path: {
              path: modulePath + '/' + dashboardLink + '/' + dashboard.linkName,
            },
            permission: self.dashBoardViewPermission,
            childrens: dashboards,
            id: dashboard.id ? dashboards.id : -1,
          })
        } else if (
          dashboard.linkName === 'sitedashboard' &&
          ![...orderArray, ...unOrderArray].find(el => {
            return el.linkName === 'portfolio'
          })
        ) {
          self.subheaderMenu = self.subheaderMenu.concat(buildingDashboards)
        } else if (dashboard.linkName !== 'sitedashboard') {
          self.subheaderMenu.push({
            label: dashboard.dashboardName,
            path: {
              path: modulePath + '/' + dashboardLink + '/' + dashboard.linkName,
            },
            permission: self.dashBoardViewPermission,
          })
        }
      }
      if (
        !self.subheaderMenu.find(menu => menu.path.path === self.$route.path)
      ) {
        let portfolio = self.subheaderMenu.find(
          menu => menu.label === 'Portfolio'
        )
        if (portfolio) {
          let currentBuilding = portfolio.childrens.find(
            menu => menu.path.path === self.$route.path
          )
          if (currentBuilding) {
            currentBuilding.childrens = portfolio.childrens
            self.subheaderMenu = self.subheaderMenu.map(ele => {
              if (ele.path.path === portfolio.path.path) {
                return currentBuilding
              } else {
                return ele
              }
            })
          } else {
            if (self.$route.path.startsWith(modulePath + '/' + dashboardLink)) {
              self.$router.replace(self.subheaderMenu[0].path)
            }
          }
        } else {
          if (self.$route.path.startsWith(modulePath + '/' + dashboardLink)) {
            self.$router.replace(self.subheaderMenu[0].path)
          }
        }
      }
    },
    getCustomeDashboardList(dashboard, modulePath, dashboardLink, subhedaer) {
      if (dashboard && dashboard.length > 1) {
        let sortedDashboards = dashboard.sort((a, b) => {
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
          if (element.displayOrder == null) {
            unOrderArray.push(element)
          } else {
            orderArray.push(element)
          }
        })
        dashboard = [...orderArray, ...unOrderArray]
      }
      let customeDashboard = []
      for (let d of dashboard) {
        if (customeDashboard.indexOf(subhedaer) === -1) {
          customeDashboard.unshift(subhedaer)
        }
        customeDashboard.push({
          label: d.dashboardName,
          id: d.id,
          path: {
            path: modulePath + '/' + dashboardLink + '/' + d.linkName,
          },
          permission: this.dashBoardViewPermission,
        })
      }
      return customeDashboard
    },
    getchillerdashboard(modulePath, dashboardLink) {
      let dashboard = []
      let self = this
      let chillers = self.chillers
      for (let i = 0; i < chillers.length; i++) {
        let data = {
          label: chillers[i].name,
          path: {
            path:
              modulePath +
              '/' +
              dashboardLink +
              '/chillerplant/' +
              chillers[i].id,
          },
          permission: self.dashBoardViewPermission,
          id: chillers[i].id ? chillers[i].id : -1,
          value:
            modulePath +
            '/' +
            dashboardLink +
            '/chillerplant/' +
            chillers[i].id,
        }
        if (chillers[i].childrens && chillers[i].childrens.length) {
          let child = chillers[i].childrens
          let childer = []
          for (let i = 0; i < child.length; i++) {
            childer.push({
              label: child[i].name,
              path: {
                path:
                  modulePath + '/' + dashboardLink + '/chillers/' + child[i].id,
              },
              permission: self.dashBoardViewPermission,
              id: child[i].id ? child[i].id : -1,
              value:
                modulePath + '/' + dashboardLink + '/chillers/' + child[i].id,
            })
          }
          data.childrens = childer
          data.children = childer
        }
        dashboard.push(data)
      }
      console.log('********** chiller', dashboard)
      return dashboard
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
      console.log('****** dashboard list called')
    },
    loadDashboardList(loadAll) {
      let self = this
      let dashboardLink = 'dashboard'
      let promises = []
      if (loadAll) {
        Object.entries(this.dashboardModules).forEach(entry => {
          promises.push(self.loadDashboards(self.dashboardModules[entry[0]]))
        })
        // promises = [
        //   this.loadDashboards({module: {module: 'energydata'}})
        // ]
      } else {
        promises = [
          this.loadDashboards(
            this.dashboardModules[this.getCurrentModule().module]
          ),
        ]
      }
      let self2 = self
      self2.headerList = []
      self.managerloading = true
      let p = Promise.all(promises)
      p.then(response => {
        self.managerloading = false
        if (this.loadAll) {
          self.dashboardlist['workorder'].dashboards = response[0][1]
          self.dashboardlist['alarm'].dashboards = response[1][1]
          self.dashboardlist['energydata'].dashboards = response[2][1]
          self.activeScreen = [
            self.dashboardlist[self.querryModule].dashboards[0].id,
          ]
        } else {
          self2.headerList = response[0][1] //get the subheader data
          self2.setDefaultdashboard(self2.headerList)
        }
      })
    },
    getSubheaderFormater(response, moduleData) {
      let dashboardlist = response.data.dashboardFolders
      let self = this
      return self.getsubheaderData(
        dashboardlist,
        moduleData.modulePath,
        moduleData.dashboardLink
      )
    },
    loadDashboards(moduleData) {
      let subheaderdata = []
      return new Promise(resolve => {
        let self = this
        if (moduleData.module) {
          self.$http
            .get('/dashboardWithFolder?moduleName=' + moduleData.module.module)
            .then(function(response) {
              subheaderdata = self.getSubheaderFormater(response, moduleData)
              resolve([response, subheaderdata])
            })
            .catch(function(error) {
              console.log(error)
            })
        }
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
              if (
                rl.label === 'Portfolio' &&
                rl.childrens &&
                rl.childrens.length &&
                rl.childrens.find(rx => rx.path.path === self.$route.path)
              ) {
                return rt
              } else if (rl.path.path === self.$route.path) {
                return rt
              }
            })
          ) {
            return rt
          }
        })
        if (filterdDashboard) {
          this.activeFolderNames = filterdDashboard.id
          this.handleCommand2(this.activeFolderNames, self.$route.path)
        } else {
          //  this.activeFolderNames = dashboard.find(rt => rt.label === 'default').id || -1
          let filteredDashboard = []
          filteredDashboard = dashboard.filter(
            rt => rt.childrens && rt.childrens.length
          )
          this.activeFolderNames = filteredDashboard[0].id || -1
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
    getsubheaderchildren(
      list,
      modulePath,
      dashboardLink,
      dashboardObj,
      listData
    ) {
      let data = []
      let self = this
      list = this.displayOrder(list)
      if (list.length) {
        list.forEach(rt => {
          let listobj = {
            label: rt.dashboardName,
            path: {
              path: modulePath + '/' + dashboardLink + '/' + rt.linkName,
              editPath:
                modulePath + '/' + 'dbediter/' + rt.linkName + '?create=edit',
            },
            permission: this.dashBoardViewPermission,
            id: rt.id ? rt.id : -1,
            rawdata: rt,
            collapse: false,
          }
          let portfolio = {
            label: rt.dashboardName,
            path: {
              path: modulePath + '/' + dashboardLink + '/' + rt.linkName,
              editPath:
                modulePath + '/' + 'dbediter/' + rt.linkName + '?create=edit',
            },
            permission: this.dashBoardViewPermission,
            id: rt.id ? rt.id : -1,
            rawdata: rt,
          }
          if (
            self.portfoliolinkName[rt.linkName] &&
            listData.find(function(rt) {
              if (
                rt &&
                rt.dashboards &&
                rt.dashboards.find(rl => rl.linkName === 'buildingdashboard')
              ) {
                return rt
              }
            })
          ) {
            listobj.childrens = [
              ...[portfolio],
              ...this.getBuildingDashboards(modulePath, dashboardLink),
            ]
          } else if (
            self.portfoliolinkName[rt.linkName] &&
            listData.find(function(rt) {
              if (
                rt &&
                rt.dashboards &&
                rt.dashboards.find(rl => rl.linkName === 'sitedashboard')
              ) {
                return rt
              }
            })
          ) {
            listobj.childrens = [
              ...[portfolio],
              ...this.getSiteDashboards(modulePath, dashboardLink),
            ]
          } else if (
            self.portfoliolinkName[rt.linkName] &&
            list.find(rt => rt.linkName === 'sitedashboard')
          ) {
            listobj.childrens = [
              ...[portfolio],
              ...this.getSiteDashboards(modulePath, dashboardLink),
            ]
          }
          // else if (listData.find(function (rt) {
          //   if (rt && rt.dashboards && rt.dashboards.find(rl => rl.linkName === 'buildingdashboard') && rt.dashboards.find(rl => rl.linkName !== 'portfolio')) {
          //     return rt
          //   }
          // })) {
          //   if (this.getBuildingDashboards(modulePath, dashboardLink).length === 1) {
          //     listobj = this.getBuildingDashboards(modulePath, dashboardLink)
          //   }
          // }
          if (
            listobj.label.toLowerCase() === 'fab' &&
            self.$account.org.id === 133
          ) {
            listobj.path = {
              path: '/app/em/newdashboard/buildingdashboard/907496',
              editPath: '/app/em/dbediter/buildingdashboard/907496?create=edit',
            }
          }
          data.push(listobj)
        })
        return data
      }
    },
    removeSpaceDashboards(list, dashboardloist) {
      let self = this
      if (self.$account.org.id === 133) {
        return list
      } else {
        return list.filter(function(rt) {
          if (rt && rt.childrens) {
            rt.childrens = rt.childrens.filter(
              rl => rl.rawdata.linkName !== 'buildingdashboard'
            )
            rt.childrens = rt.childrens.filter(
              rl => rl.rawdata.linkName !== 'sitedashboard'
            )
            return rt
          } else {
            return rt
          }
        })
      }
    },
    getsubheaderData(list, modulePath, dashboardLink) {
      let data = []
      list = this.displayOrder(list)
      list.forEach(rt => {
        let dashboardlist = {
          label: rt.name !== 'default' ? rt.name : 'Default',
          path: {
            path: modulePath + '/' + dashboardLink + '/' + rt.linkName,
            editPath:
              modulePath + '/' + 'dbediter/' + rt.linkName + '?create=edit',
          },
          permission: this.dashBoardViewPermission,
          id: rt.id ? rt.id : -1,
          rawdata: rt,
        }
        if (
          this.getsubheaderchildren(
            rt.dashboards,
            modulePath,
            dashboardLink,
            rt,
            list
          ) !== null &&
          rt.dashboards
        ) {
          dashboardlist.childrens = this.getsubheaderchildren(
            rt.dashboards,
            modulePath,
            dashboardLink,
            rt,
            list
          )
        } else {
          dashboardlist.childrens = []
        }
        data.push(dashboardlist)
      })
      // return data
      return this.removeSpaceDashboards(data, list) // temp only
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
  },
}
