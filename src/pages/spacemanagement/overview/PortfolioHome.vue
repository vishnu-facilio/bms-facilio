<template>
  <div class="fc-sites-v1-overview-page" style="height: 100vh">
    <div v-if="homeLoading" class="flex-middle fc-empty-white">
      <spinner :show="homeLoading" size="80"></spinner>
    </div>
    <template v-else>
      <div class="fc-white-bg p0" style="height: 100vh">
        <multipane
          @paneResizeStop="doLayout"
          class="vertical-panes"
          layout="vertical"
        >
          <div
            @mousedown.stop
            class="site-list-container"
            :class="paddingClass"
          >
            <div class="fc-white-bg site-header-v1 display-flex-between-space">
              <div>
                <div class="fc-black-22 bold">
                  {{ $t('common.header.spacemanagement') }}
                </div>
              </div>
              <div>
                <div class="d-flex flex-direction-column">
                  <div v-if="$hasPermission('space:CREATE')">
                    <el-dropdown
                      class="mL30 pointer site-header-button"
                      trigger="click"
                      @command="currentModule => openNewForm(currentModule)"
                    >
                      <el-button type="primary">
                        {{ $t('common._common.add')
                        }}<i class="el-icon-arrow-down pL10 font-black"></i>
                      </el-button>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item :key="1" :command="'site'">{{
                          $t('space.sites.new_site')
                        }}</el-dropdown-item>
                        <el-dropdown-item :key="2" :command="'building'">{{
                          $t('space.sites.new_building')
                        }}</el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </div>
                </div>
              </div>
            </div>
            <div>
              <div
                class="display-flex-between-space p10 position-relative border-bottom1px"
              >
                <div class="display-flex height40">
                  <div class="pR10"></div>
                  <div
                    v-on:click="changeCurrentModule('site')"
                    :class="[
                      'portfolio-list-view-select-container',
                      currentModule === 'site' && 'select-bold',
                    ]"
                  >
                    {{ $t('common._common.sites') }}
                  </div>
                  <div
                    :class="[
                      'portfolio-list-view-select',
                      currentModule === 'site' && 'site-active-tab',
                    ]"
                  ></div>
                  <span class="separator pL20">|</span>
                  <div
                    v-on:click="changeCurrentModule('building')"
                    :class="[
                      'portfolio-list-view-select-container',
                      'pL20',
                      currentModule === 'building' && 'select-bold',
                    ]"
                  >
                    {{ $t('common._common.buildings') }}
                  </div>
                  <div
                    :class="[
                      'portfolio-list-view-select',
                      currentModule === 'building' &&
                        'site-active-tab-building',
                    ]"
                  ></div>
                </div>
                <div class="text-right pR20">
                  <div class="fc-black-13 text-right bold">
                    {{ $t('space.sites.total_area') }}
                  </div>
                  <div class="fc-black-15 text-right pT5">
                    {{
                      !$validation.isEmpty(totalArea) && totalArea > 0
                        ? numberWithCommas(totalArea + ' ' + totalAreaUnit)
                        : '---'
                    }}
                  </div>
                </div>
              </div>
            </div>
            <div style="padding-bottom:200px">
              <SiteList
                style="left : 0;top : 0"
                :moduleName="currentModule"
                :viewname="viewname"
                ref="site-list"
                @refreshCount="loadCount"
                @mapData="mapData => setMapData(mapData)"
                :key="currentModule + '----index'"
                :isMapEnabled="isMapEnabled"
              ></SiteList>
            </div>
          </div>
          <multipane-resizer
            v-if="!$helpers.isEtisalat() && isMapEnabled"
            class="multiplane-resizer-site"
          ></multipane-resizer>
          <div
            v-if="!$helpers.isEtisalat() && isMapEnabled"
            class="pane site-map-conainer"
            style="border-left: 1px solid #c7ced4;"
            :style="{ flexGrow: 1 }"
          >
            <f-map-widget
              :data="mapData"
              :markerType="'profile'"
              :ref="'portFolioMap'"
              height="calc(100vh - 50px)"
              width="100%"
              class="new-site-map"
            >
              <template v-slot="markerScope">
                <div class="info-close-icon">
                  <i class="el-icon-close" @click="closeInfoWindow()"></i>
                </div>
                <div class="flex-middle">
                  <space-avatar
                    :name="false"
                    size="xxlg"
                    :space="markerScope.currentMarker"
                  ></space-avatar>
                  <div class="fw5 fc-black3-16 ellipsis inline pointer mL10">
                    <div @click="openOverview(markerScope)">
                      <div class="fc-id">
                        #{{ markerScope.currentMarker.id }}
                      </div>
                      <div class="fw5 fc-black3-16 ellipsis">
                        {{ markerScope.currentMarker.name }}
                      </div>
                    </div>
                  </div>
                </div>
              </template>
            </f-map-widget>
          </div>
        </multipane>
      </div>
      <div class="height100">
        <portal to="view-manager-link">
          <router-link
            tag="div"
            :to="`/app/home/${currentModule}/viewmanager`"
            class="site-list-viewmanager"
          >
            <inline-svg
              src="svgs/hamburger-menu"
              class="d-flex"
              iconClass="icon icon-sm"
            ></inline-svg>
            <span class="label mL10 text-uppercase">
              {{ $t('viewsmanager.list.views_manager') }}
            </span>
          </router-link>
        </portal>
      </div>
    </template>
  </div>
</template>
<script>
import FMapWidget from '@/FMapWidget'
import SpaceAvatar from '@/avatar/Space'
import { Multipane, MultipaneResizer } from 'vue-multipane'
import { eventBus } from '@/page/widget/utils/eventBus'
import SiteList from './components/SiteList'
import { isEmpty } from '@facilio/utils/validation'
import { findRouteForTab, isWebTabsEnabled, tabTypes } from '@facilio/router'
import { API } from '@facilio/api'
import Spinner from '@/Spinner'

export default {
  components: {
    FMapWidget,
    SpaceAvatar,
    Multipane,
    MultipaneResizer,
    SiteList,
    Spinner,
  },
  props: ['moduleName', 'viewname'],
  data() {
    return {
      isLoading: false,
      allSite: false,
      mapData: [],
      listMapData: [],
      sitesCount: 0,
      buildingCount: 0,
      totalArea: 0,
      totalAreaUnit: 'sq. ft',
      allSiteData: [],
      allsitesMapdata: [],
      buildingView: 'all',
      siteView: 'all',
      currentModule: '',
      isMapEnabled: true,
      homeLoading: false,
    }
  },
  created() {
    this.loadCount()
    this.init()
  },

  title() {
    return 'Space Management'
  },
  computed: {
    paddingClass() {
      return !this.isMapEnabled ? 'map-view-disabled' : ''
    },
  },
  methods: {
    async init() {
      this.homeLoading = true
      this.currentModule = this.moduleName
      this.buildingView = await this.getWebTabView('building')
      this.siteView = await this.getWebTabView('site')
      this.isMapEnabled = await this.isMapViewDisabled('site')
      this.homeLoading = false
    },
    async redirect(moduleName) {
      let viewname = this.viewname
      if (!viewname || viewname === '') {
        viewname = 'all'
      }
      let parentPath = isWebTabsEnabled()
        ? this.findRoute()
        : '/app/home/portfolio'

      let path =
        parentPath +
        (moduleName == 'site' ? '/sites' : '/buildings') +
        '/' +
        (moduleName == 'site' ? this.siteView : this.buildingView)

      this.$router.push({
        path: path,
      })
    },
    changeCurrentModule(moduleName) {
      this.currentModule = moduleName
      this.redirect(moduleName)
    },
    numberWithCommas(value) {
      return value.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ',')
    },
    closeInfoWindow() {
      this.$refs['portFolioMap'].mapclick()
    },
    openNewForm(moduleName) {
      if (moduleName === 'site') {
        eventBus.$emit('openSpaceManagementForm', {
          isNew: true,
          visibility: true,
          module: 'site',
        })
      } else if (moduleName === 'building') {
        eventBus.$emit('openSpaceManagementForm', {
          isNew: true,
          visibility: true,
          module: 'building',
        })
      }
    },
    async isMapViewDisabled(moduleName) {
      let { data, error } = await API.get(
        `/v2/siteSettings/list?moduleName=${moduleName}`
      )
      if (data) {
        let setting = this.$getProperty(data, 'setting', [])
        let siteMapSetting = (setting || []).find(
          config => config.configurationName === 'siteMapView'
        )
        let isMapEnabled = this.$getProperty(siteMapSetting, 'status', true)
        return isMapEnabled
      } else {
        this.$message.error(error.message || 'Error Occurred')
      }
      return true
    },
    async getWebTabView(moduleName) {
      let { data } = await API.get(
        '/v2/views/viewList?moduleName=' + moduleName
      )
      if (data) {
        let { groupViews } = data
        let { views } = groupViews[0] || {}
        let { name } = views[0] || {}
        return name
      }
      return 'all'
    },
    loadCount() {
      let promises = []
      let siteTotalCountUrl = 'v2/site/list?fetchCount=true&viewName=all'
      let buildingsTotalCountUrl =
        'v2/building/list?fetchCount=true&viewName=all'
      let totalAreaUrl = 'v2/site/totalArea'
      promises.push(this.$http.get(siteTotalCountUrl))
      promises.push(this.$http.get(buildingsTotalCountUrl))
      promises.push(this.$http.get(totalAreaUrl))
      this.isLoading = true
      Promise.all(promises)
        .then(([siteCount, buildingCount, totalAreaResult]) => {
          if (!isEmpty(siteCount)) {
            let {
              data: { message, responseCode, result = {} },
            } = siteCount
            if (responseCode === 0) {
              let { recordCount } = result
              this.$set(this, 'sitesCount', recordCount)
            } else {
              throw new Error(message)
            }
          }
          if (!isEmpty(buildingCount)) {
            let {
              data: { message, responseCode, result = {} },
            } = buildingCount
            if (responseCode === 0) {
              let { recordCount } = result
              this.$set(this, 'buildingCount', recordCount)
            } else {
              throw new Error(message)
            }
          }
          if (!isEmpty(totalAreaResult)) {
            let {
              data: { message, responseCode, result = {} },
            } = totalAreaResult
            if (responseCode === 0) {
              let { totalArea, unit } = result
              this.$set(this, 'totalArea', totalArea)
              if (!isEmpty(unit)) {
                this.$set(this, 'totalAreaUnit', unit)
              }
            } else {
              throw new Error(message)
            }
          }
        })
        .catch(({ message = 'Error Occurred while fetching Count' }) => {
          this.$message.error(message)
        })
        .finally(() => {
          this.isLoading = false
          return
        })
    },
    doLayout() {
      this.$refs['site-list'].doLayout()
    },
    setMapData(mapData) {
      this.$set(this, 'mapData', this.filterMapData(mapData))
      this.$set(this, 'listMapData', this.mapData)
    },
    switchMapData() {
      if (this.allSite) {
        this.mapData = this.allsitesMapdata
      } else {
        this.$set(this, 'mapData', this.listMapData)
      }
    },
    filterMapData(mapData) {
      if (!isEmpty(mapData)) {
        let map = []
        let data = null
        mapData.forEach(rt => {
          if (rt.location && rt.location.lat && rt.location.lng) {
            data = {
              avatarUrl: rt.avatarUrl,
              id: rt.id,
              location: {
                lat: rt.location.lat,
                lng: rt.location.lng,
              },
              name: rt.name,
            }
            map.push(data)
          }
        })
        return map
      }
    },
    findRoute() {
      let tabType = tabTypes.CUSTOM
      let config = { type: 'portfolio' }
      let { name } = findRouteForTab(tabType, { config }) || {}

      return name ? this.$router.resolve({ name }).href : null
    },
    async openOverview(data) {
      let { moduleName } = this
      let { currentMarker } = data || {}
      let { id } = currentMarker || {}
      let parentPath = isWebTabsEnabled()
        ? this.findRoute()
        : '/app/home/portfolio'

      if (parentPath) {
        let route = { path: `${parentPath}/site/${id}/overview` }

        if (moduleName === 'building') {
          let { error, building } = await API.fetchRecord(moduleName, { id })
          if (!error) {
            let buildingSiteId = this.$getProperty(building, 'siteId', null)
            route = !isEmpty(buildingSiteId)
              ? {
                  path: `${parentPath}/buildings/all/site/${buildingSiteId}/building/${id}`,
                }
              : route
          }
        }
        this.$router.push(route)
      }
    },
  },
}
</script>
<style scoped>
.new-site-map button.gm-ui-hover-effect {
  display: none !important;
}
.map-load-all-sites {
  position: absolute;
  z-index: 1;
}
.info-close-icon {
  cursor: pointer;
  display: flex;
  float: right;
  font-size: 14px;
}
.map-view-disabled {
  width: 100% !important;
  padding: 0 !important;
}
</style>
