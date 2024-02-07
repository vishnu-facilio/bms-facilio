<template>
  <div v-if="visibility" class="analytics-sidebar-bg">
    <div
      v-if="isMultipleCategorySelected && disableMultipleAssetCategories"
      class="analytics-topbar-warning"
    >
      <div class="Only-one-data-point flex-middle">
        <InlineSvg
          src="svgs/info-warning"
          iconClass="icon vertical-middle icon-md fill-orange"
        ></InlineSvg>
        <div class="pL10">
          Please choose only a single asset category to customise! To select
          other data points, please
        </div>
        <div class="text-style-1 pL5">delete the current selection points.</div>
        <div class="mL10">
          <el-switch
            v-model="replaceDataPoints"
            @change="emptySelectedDataPoints()"
            class="Notification-toggle"
            active-color="rgba(57, 178, 194, 0.8)"
            inactive-color="#e5e5e5"
          ></el-switch>
        </div>
      </div>
    </div>
    <div class="sidebar-position-setup">
      <div class="sidebar-close-btn pointer" @click="cancel">
        <div class="">
          <i class="el-icon-close"></i>
        </div>
      </div>
      <div class="building-analysis-sidebar-conatainer">
        <div v-if="!buildingIds || !buildingIds.length">
          No buildings selected.
        </div>
        <div class="newanalytics-sidebar col-3" v-else-if="loading">
          <spinner :show="loading"></spinner>
        </div>
        <div class="newanalytics-sidebar col-3" v-else>
          <div class="data-pointer-selector-popup">
            <analaytics-filter
              :zones="zones"
              :selectedBuildings.sync="selectedBuildings"
              :isSiteAnalysis="isSiteAnalysis"
            ></analaytics-filter>
          </div>
          <div>
            <div v-if="showQuickSearch">
              <div class="fc-list-search">
                <div
                  class="fc-list-search-wrapper relative fc-list-search-wrapperbuilding-search"
                >
                  <input
                    ref="quickSearchQuery"
                    autofocus
                    type="text"
                    v-model="searchKey"
                    @keyup="quickSearch"
                    placeholder="Search"
                    class="quick-search-input4"
                  />
                  <svg
                    @click="closeSearch"
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 32 32"
                    class="close-icon4"
                    aria-hidden="true"
                  >
                    <title>close</title>
                    <path
                      d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"
                    ></path>
                  </svg>
                </div>
              </div>
            </div>

            <div
              class="building-analysis-side-block"
              v-if="!mode || !['mv'].includes(mode)"
            >
              <div class="building-hover-actions">
                <div
                  class="sidebar-asset-H fL"
                  @click="loadAssetCategories"
                  style="cursor : pointer;"
                >
                  Assets
                </div>
              </div>
              <div
                class="building-anlysis-active-block"
                v-if="config.mainPanel.currentModule === 'assets'"
              >
                <div
                  :class="[
                    'building-anlysis-label-txt',
                    'clearboth',
                    {
                      'asset-list-block-txt-active':
                        config.mainPanel.selectedAssetCategory === category.id,
                    },
                  ]"
                  v-if="
                    config.mainPanel.assetCategories &&
                      config.mainPanel.assetCategories.length
                  "
                  v-for="(category, index) in config.mainPanel.assetCategories"
                  :key="index"
                  @click="openAssetCategory(category.id)"
                >
                  <i
                    v-bind:class="{
                      'fa fa-circle': true,
                      'dot-blue-icon':
                        config.mainPanel.selectedAssetCategory === category.id,
                      'dot-grey-icon':
                        config.mainPanel.selectedAssetCategory != category.id,
                    }"
                    aria-hidden="true"
                  ></i>
                  <div class="width80">{{ category.name }}</div>
                  <span class="active-arrow"
                    ><img
                      v-if="
                        config.mainPanel.selectedAssetCategory === category.id
                      "
                      src="~assets/arrow-pointing-to-right.svg"
                      height="14px"
                      width="14px"
                  /></span>
                </div>
                <div
                  v-if="!config.mainPanel.assetCategories.length"
                  class="building-anlysis-label-txt clearboth"
                >
                  -- No assets --
                </div>
              </div>
            </div>
            <div
              class="building-analysis-side-block"
              v-if="[4, 'mv'].includes(mode)"
            >
              <div class="building-hover-actions">
                <div
                  class="sidebar-asset-H fL"
                  @click="loadMVProjects"
                  style="cursor : pointer;"
                >
                  M & V
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- Sidebar end -->

        <!-- sidebar2 start -->
        <div
          class="assets-points-Scontainer assets-points-Scontainer-lalign"
          v-if="config.assetPointsPanel.show"
        >
          <div class="assets-points-inner-container">
            <div
              class="select-reading-header"
              style="padding-bottom: 17px; padding-top: 16px !important;"
            >
              <span
                ><img
                  src="~assets/gauge.svg"
                  width="18px"
                  height="18px"
                  style="position: relative;top: 4px;right: 7px;"
                />Points</span
              >
            </div>

            <div class="building-points-search">
              <el-input
                placeholder="Filter points"
                v-model="config.assetPointsPanel.filterQuery"
                autofocus
                class=""
              ></el-input>
            </div>
            <div class="asset-list-container mT10">
              <spinner
                v-if="config.assetPointsPanel.loading"
                :show="config.assetPointsPanel.loading"
              />
              <div
                class="asset-list-block-txt text-center"
                v-else-if="
                  !config.assetPointsPanel.data ||
                    !config.assetPointsPanel.data.length
                "
              >
                No points.
              </div>
              <el-checkbox-group
                v-model="config.mainPanel.selectedDataPoints"
                class="building-check-list"
                v-else
              >
                <el-checkbox
                  v-for="(item, index) in filteredAssetPointsPanel"
                  :key="index"
                  :label="
                    config.mainPanel.selectedAssetCategory + '_' + item.id
                  "
                  >{{
                    item.displayName ? item.displayName : item.name
                  }}</el-checkbox
                >
              </el-checkbox-group>
            </div>
          </div>
        </div>
        <div
          class="assets-points-Scontainer assets-points-Scontainer-lalign"
          v-if="config.mvPointsPanel.show || mode === 'mv'"
        >
          <div class="assets-points-inner-container">
            <div
              class="select-reading-header"
              style="padding-bottom: 17px; padding-top: 16px !important;"
            >
              <span
                ><img
                  src="~assets/gauge.svg"
                  width="18px"
                  height="18px"
                  style="position: relative;top: 4px;right: 7px;"
                />Points</span
              >
            </div>

            <div class="building-points-search">
              <el-input
                placeholder="Filter points"
                v-model="config.mvPointsPanel.filterQuery"
                autofocus
                class=""
              ></el-input>
            </div>
            <div class="asset-list-container mT10">
              <spinner
                v-if="config.mvPointsPanel.loading"
                :show="config.mvPointsPanel.loading"
              />
              <div
                class="asset-list-block-txt text-center"
                v-else-if="
                  !config.mvPointsPanel.data ||
                    !config.mvPointsPanel.data.length
                "
              >
                No points.
              </div>
              <el-checkbox-group
                v-model="config.mainPanel.selectedDataPoints"
                class="building-check-list"
                v-else
              >
                <el-checkbox
                  v-for="(item, index) in filteredmvPointsPanel"
                  :key="index"
                  :label="
                    config.mainPanel.selectedAssetCategory + '_' + item.id
                  "
                  >{{
                    item.displayName ? item.displayName : item.name
                  }}</el-checkbox
                >
              </el-checkbox-group>
            </div>
          </div>
        </div>
        <!-- sidebar2 end -->

        <div
          class="building-analysis-point-container row"
          v-if="
            config.assetPointsPanel.show ||
              config.mainPanel.selectedDataPoints.length
          "
        >
          <div class="building-analysis-points-wrap col-12">
            <div
              class="col-4"
              v-if="config.mainPanel.selectedDataPoints.length"
            >
              <el-popover
                :width="300"
                :popper-class="'added-datapoints-popover'"
              >
                <div class="added-datapoints-container">
                  <div class="added-datapoints-header">
                    Selected Points
                    <i
                      class="el-icon-delete pointer pL5"
                      style="font-size: 12px;"
                      @click="clearSelectedPoints"
                      title="Remove all"
                      data-arrow="true"
                      v-tippy
                    ></i>
                  </div>
                  <div class="added-datapoints-collection">
                    <ul class="list-group">
                      <li
                        class="list-group-item"
                        v-for="(dataPoint, index) in config.mainPanel
                          .selectedDataPoints"
                        :key="index"
                      >
                        <span class="float: left;">{{
                          readings.fields[dataPoint.split('_')[1]]
                            ? readings.fields[dataPoint.split('_')[1]]
                                .displayName
                            : '---'
                        }}</span>
                        <i
                          class="el-icon-delete pointer"
                          @click="removeSelectedDataPoint(dataPoint, index)"
                          title="Remove"
                          data-arrow="true"
                          v-tippy
                          style="float: right;"
                        ></i>
                        <span style="clear: both;"></span>
                      </li>
                    </ul>
                  </div>
                </div>
                <div slot="reference" class="fc-text-pink fL pointer">
                  {{ config.mainPanel.selectedDataPoints.length }} points
                  selected
                </div>
              </el-popover>
            </div>
            <div class="building-analysis-points-btn fR col-8">
              <el-button type="button" class="btn-grey-fill" @click="cancel">{{
                $t('setup.users_management.cancel')
              }}</el-button>
              <el-button type="button" class="btn-blue-fill" @click="apply">{{
                $t('maintenance.pm_list.apply')
              }}</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AnalayticsFilter from 'pages/energy/analytics/components/AnalayticsFilter'
import { isEmpty } from '@facilio/utils/validation'

import { mapState } from 'vuex'
import { API } from '@facilio/api'
import { getFieldValue } from 'util/picklist'

export default {
  props: [
    'visibility',
    'zones',
    'isSiteAnalysis',
    'mode',
    'disableMultipleAssetCategories',
  ],
  data() {
    return {
      config: {
        mainPanel: {
          currentModule: '',
          assetCategories: null,
          selectedAssetCategory: null,
          selectedDataPoints: [],
        },
        assetPointsPanel: {
          show: false,
          activeSubTab: 'points',
          data: null,
          loading: true,
          selectedAssetOrPoint: null,
          filterQuery: '',
        },
        mvPointsPanel: {
          show: false,
          activeSubTab: 'points',
          data: null,
          loading: true,
          selectedAssetOrPoint: null,
          filterQuery: '',
        },
        selectReadingsPanel: {
          show: false,
          data: null,
          loading: false,
          parentId: null,
          filterQuery: '',
        },
        selectAssetsPanel: {
          show: false,
          data: null,
          loading: false,
          readingId: null,
          filterQuery: '',
        },
      },
      loading: true,
      readings: {
        categories: [],
        assets: {},
        categoryWithAssets: {},
        fields: {},
        categoryWithFields: {},
      },
      mvreadings: null,
      showQuickSearch: false,
      quickSearchQuery: null,
      selectedBuildings: [],
      parentIds: null,
      replaceDataPoints: false,
      isMultipleCategorySelected: false,
      urlParams: {
        perPage: 50,
        page: 1,
        viewName: 'hidden-all',
      },
      default_building_id: null,
      selectedBuildingLabel: this.isSiteAnalysis ? 'No Sites' : 'No Buildings',
    }
  },
  components: {
    AnalayticsFilter,
  },
  created() {
    Promise.all([
      this.$store.dispatch('loadAssetCategory'),
      this.loadDefaultBuilding(),
    ]).then(() => this.setDefaultSelection())
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),

    buildingIds() {
      if (this.selectedBuildings && this.selectedBuildings.length) {
        let buildingIds = []
        for (let sb of this.selectedBuildings) {
          buildingIds.push(parseInt(sb))
        }
        return buildingIds
      }
      return null
    },
    buildingList() {
      return {}
    },
    async selectedBuildingName() {
      if (this.selectedBuildings && this.selectedBuildings.length) {
        if (this.selectedBuildings.length === 1) {
          if (this.selectedBuildings[0] && !this.isSiteAnalysis) {
            let { data, error } = await getFieldValue({
              lookupModuleName: 'building',
              selectedOptionId: [this.selectedBuildings[0]],
            })
            if (!error && data) {
              let value = this.$getProperty(data, '0.label')
              this.selectedBuildingLabel = value
            }
          } else if (this.selectedBuildings[0] && this.isSiteAnalysis) {
            let { data, error } = await getFieldValue({
              lookupModuleName: 'site',
              selectedOptionId: [this.selectedBuildings[0]],
            })
            if (!error && data) {
              let value = this.$getProperty(data, '0.label')
              this.selectedBuildingLabel = value
            }
          }
        }
      }
    },
    filteredAssetPointsPanel() {
      let self = this
      if (
        this.config.assetPointsPanel.data &&
        this.config.assetPointsPanel.data.length
      ) {
        if (
          this.config.assetPointsPanel.filterQuery &&
          this.config.assetPointsPanel.filterQuery.trim().length
        ) {
          return this.config.assetPointsPanel.data.filter(function(d) {
            let name = d.displayName ? d.displayName : d.name
            if (
              name
                .toLowerCase()
                .indexOf(
                  self.config.assetPointsPanel.filterQuery.toLowerCase()
                ) >= 0
            ) {
              if (d.dataTypeEnum !== 'BOOLEAN') {
                return d
              }
            }
          })
        } else {
          return this.config.assetPointsPanel.data.filter(function(d) {
            if (d.dataTypeEnum !== 'BOOLEAN') {
              return d
            }
          })
        }
      }
      return []
    },
    filteredmvPointsPanel() {
      let self = this
      if (
        this.config.mvPointsPanel.data &&
        this.config.mvPointsPanel.data.length
      ) {
        if (
          this.config.mvPointsPanel.filterQuery &&
          this.config.mvPointsPanel.filterQuery.trim().length
        ) {
          let filteredData = this.config.mvPointsPanel.data.filter(function(d) {
            let name = d.displayName ? d.displayName : d.name
            if (
              name
                .toLowerCase()
                .indexOf(self.config.mvPointsPanel.filterQuery.toLowerCase()) >=
              0
            ) {
              if (d.dataTypeEnum !== 'BOOLEAN') {
                return d
              }
            }
          })
          return this.$helpers.sortData('name', filteredData)
        } else {
          let filteredData = this.config.mvPointsPanel.data.filter(function(d) {
            if (d.dataTypeEnum !== 'BOOLEAN') {
              return d
            }
          })
          return this.$helpers.sortData('name', filteredData)
        }
      }
      return []
    },
    filteredReadingsPanel() {
      let self = this
      if (
        this.config.selectReadingsPanel.data &&
        this.config.selectReadingsPanel.data.length
      ) {
        if (
          this.config.selectReadingsPanel.filterQuery &&
          this.config.selectReadingsPanel.filterQuery.trim().length
        ) {
          return this.config.selectReadingsPanel.data.filter(function(d) {
            let name = d.displayName ? d.displayName : d.name
            if (
              name
                .toLowerCase()
                .indexOf(
                  self.config.selectReadingsPanel.filterQuery.toLowerCase()
                ) >= 0
            ) {
              return d
            }
          })
        } else {
          return this.config.selectReadingsPanel.data
        }
      }
      return []
    },
    filteredAssetsPanel() {
      let self = this
      if (
        this.config.selectAssetsPanel.data &&
        this.config.selectAssetsPanel.data.length
      ) {
        if (
          this.config.selectAssetsPanel.filterQuery &&
          this.config.selectAssetsPanel.filterQuery.trim().length
        ) {
          return this.config.selectAssetsPanel.data.filter(function(d) {
            let name = d.displayName ? d.displayName : d.name
            if (
              name
                .toLowerCase()
                .indexOf(
                  self.config.selectAssetsPanel.filterQuery.toLowerCase()
                ) >= 0
            ) {
              return d
            }
          })
        } else {
          return this.config.selectAssetsPanel.data
        }
      }
      return []
    },
  },
  watch: {
    buildingIds: function(newVal, oldVal) {
      if (JSON.stringify(newVal) !== JSON.stringify(oldVal)) {
        this.loadAssetCategories()
        this.resetResetReadings()
      }
    },
    selectedBuildingName: async function(newVal) {
      await this.selectedBuildingName
      this.$emit('selectBuildings', {
        name: this.selectedBuildingLabel,
        ids: this.selectedBuildings,
      })
    },
    buildingList: function(newval) {
      this.setDefaultSelection()
    },
  },
  mounted() {
    if (this.mode === 'mv') {
      this.loadMVProjects()
    }
  },
  methods: {
    async loadDefaultBuilding() {
      if (!this.isSiteAnalysis) {
        let { data, error } = await API.get(
          '/v3/picklist/building',
          this.urlParams,
          { force: true }
        )
        if (!error && data?.pickList.length > 0) {
          this.default_building_id = data.pickList[0].value
        }
      } else {
        let { data, error } = await API.get(
          '/v3/picklist/site',
          this.urlParams,
          { force: true }
        )
        if (!error && data?.pickList.length > 0) {
          this.default_building_id = data.pickList[0].value
        }
      }
    },
    setDefaultSelection() {
      this.selectedBuildings = []
      if (this.default_building_id) {
        // this.selectedBuildings = [Object.keys(this.buildingList)[0]]
        this.selectedBuildings = [this.default_building_id] //[Object.keys(this.buildingList)[0]]
      }
    },
    setSelectedBuildings(selectedBuildings) {
      this.selectedBuildings = selectedBuildings
    },
    toggleQuickSearch() {
      this.showQuickSearch = !this.showQuickSearch
      if (this.showQuickSearch) {
        this.$nextTick(() => {
          this.$refs.quickSearchQuery.focus()
        })
      }
    },
    closeSearch() {
      this.toggleQuickSearch()
      this.quickSearchQuery = null
      this.quickSearch()
    },
    quickSearch() {},
    loadAssetCategories() {
      let self = this
      if (self.config.mainPanel.currentModule === 'assets') {
        // toggle
        self.config.mainPanel.currentModule = ''
        return
      }
      self.config.mainPanel.currentModule = 'assets'
      self.config.mainPanel.selectedAssetCategory = null
      self.config.mainPanel.assetCategories = null

      self.$http
        .post('/asset/getAssetCategoryWithReadings', {
          buildingIds: self.buildingIds,
        })
        .then(function(response) {
          let categoryIds = Object.keys(response.data)
          self.config.mainPanel.assetCategories = self.assetCategory.filter(
            ele => {
              for (let index = 0; index < categoryIds.length; index++) {
                const element = categoryIds[index]
                if (parseInt(element) === ele.id) {
                  ele.toggle = false
                  return ele
                }
              }
            }
          )
          self.loading = false
          let panels = []
          if (self.config['assetPointsPanel'].show) {
            panels.push('assetPointsPanel')
          }
          if (self.config['selectReadingsPanel'].show) {
            panels.push('selectReadingsPanel')
          }
          if (self.config['selectAssetsPanel'].show) {
            panels.push('selectAssetsPanel')
          }
          if (
            self.config['moduleFieldsPanel'] &&
            self.config['moduleFieldsPanel'].show
          ) {
            panels.push('moduleFieldsPanel')
          }
          self.togglePanel(panels)
          if (
            self.savedPoints &&
            typeof self.savedPoints !== 'undefined' &&
            self.savedPoints.length !== 0
          ) {
            self.prepareSavedDataPoints()
          }
        })
    },

    resetResetReadings() {
      this.readings = {
        categories: [],
        assets: {},
        categoryWithAssets: {},
        fields: {},
        categoryWithFields: {},
      }
    },

    loadMVProjects() {
      let self = this
      if (self.config.mainPanel.currentModule === 'mvs') {
        // toggle
        self.config.mainPanel.currentModule = ''
        return
      }
      self.config.mainPanel.currentModule = 'mvs'

      let panels = []
      if (self.config['assetPointsPanel'].show) {
        panels.push('assetPointsPanel')
      }
      if (self.config['selectReadingsPanel'].show) {
        panels.push('selectReadingsPanel')
      }
      if (self.config['selectAssetsPanel'].show) {
        panels.push('selectAssetsPanel')
      }
      self.togglePanel(panels)
      self.togglePanel('mvPointsPanel')
      self.$http
        .get('/v2/readings/getSubModuleRel?moduleName=mvproject')
        .then(function(response) {
          let submodules = response.data.result.submodules
          self.mvreadings = []
          for (let module in submodules) {
            self.mvreadings = self.mvreadings.concat(submodules[module].fields)
          }
          if (self.mvreadings !== []) {
            self.mvreadings = self.mvreadings.filter(el => el.dataType === 3)
          }
          self.config.mvPointsPanel.data = self.mvreadings.map(el => {
            return { id: el.id, name: el.displayName }
          })
          self.config.mvPointsPanel.loading = false
        })
    },

    clearSelectedPoints() {
      this.config.mainPanel.selectedDataPoints = []
    },

    removeSelectedDataPoint(dataPoint, index) {
      this.config.mainPanel.selectedDataPoints.splice(index, 1)
    },

    togglePanel(...panels) {
      let self = this
      let configKeys = Object.keys(self.config)
      for (let configKey of configKeys) {
        if (panels.indexOf(configKey) >= 0) {
          self.config[configKey].show = true
        } else {
          self.config[configKey].show = false
          self.config[configKey].selectedItem = null
        }
      }
    },

    openAssetCategory(categoryId) {
      let self = this
      if (
        self.disableMultipleAssetCategories &&
        self.config.mainPanel.selectedAssetCategory &&
        self.config.mainPanel.selectedAssetCategory !== categoryId &&
        self.config.mainPanel.selectedDataPoints.length > 0
      ) {
        this.isMultipleCategorySelected = true
      } else {
        self.togglePanel('assetPointsPanel')
        self.config.mainPanel.selectedAssetCategory = categoryId
        self.config.assetPointsPanel.loading = true
        self.config.assetPointsPanel.selectedAssetOrPoint = null

        if (self.readings.categories.includes(categoryId)) {
          if (self.config.assetPointsPanel.activeSubTab === 'assets') {
            let assetIds = Object.keys(
              self.readings.categoryWithAssets[categoryId]
            )
            self.config.assetPointsPanel.data = assetIds.map(el => {
              return { id: el, name: self.readings.assets[el] }
            })
            self.config.assetPointsPanel.loading = false
          } else {
            let readingIds = Object.keys(
              self.readings.categoryWithFields[categoryId]
            )
            self.config.assetPointsPanel.data = readingIds.map(el => {
              return self.readings.fields[el]
            })
            self.config.assetPointsPanel.loading = false
          }
        } else {
          if (self.buildingIds) {
            self.$http
              .post('/asset/getReadingsForSpecificAssetCategory', {
                buildingIds: self.buildingIds,
                categoryIds: [categoryId],
              })
              .then(function(response) {
                if (!self.readings.categories.includes(categoryId)) {
                  self.readings.categories.push(categoryId)
                }
                self.readings.categoryWithAssets[categoryId] =
                  response.data.categoryWithAssets[categoryId]
                self.readings.categoryWithFields[categoryId] =
                  response.data.categoryWithFields[categoryId]
                if (response.data.fields) {
                  for (let fieldId of Object.keys(response.data.fields)) {
                    if (!self.readings.fields[fieldId]) {
                      self.readings.fields[fieldId] =
                        response.data.fields[fieldId]
                    }
                  }
                }
                if (response.data.assets) {
                  for (let assetId of Object.keys(response.data.assets)) {
                    if (!self.readings.assets[assetId]) {
                      self.readings.assets[assetId] =
                        response.data.assets[assetId]
                    }
                  }
                }
                if (self.config.assetPointsPanel.activeSubTab === 'assets') {
                  let assetIds = Object.keys(
                    self.readings.categoryWithAssets[categoryId]
                  )
                  self.config.assetPointsPanel.data = assetIds.map(el => {
                    return { id: el, name: self.readings.assets[el] }
                  })
                  self.config.assetPointsPanel.loading = false
                } else {
                  let readingIds = Object.keys(
                    self.readings.categoryWithFields[categoryId]
                  )
                  self.config.assetPointsPanel.data = readingIds.map(el => {
                    return self.readings.fields[el]
                  })
                  self.config.assetPointsPanel.loading = false
                }
              })
          }
        }
      }
    },

    openAsset(assetId) {
      let self = this
      self.config.assetPointsPanel.selectedAssetOrPoint = assetId

      self.togglePanel('assetPointsPanel', 'selectReadingsPanel')
      self.config.selectReadingsPanel.loading = true
      self.config.selectReadingsPanel.parentId = assetId

      let fieldIds =
        self.readings.categoryWithAssets[
          self.config.mainPanel.selectedAssetCategory
        ][assetId]
      self.config.selectReadingsPanel.data = fieldIds.map(el => {
        return self.readings.fields[el]
      })
      self.config.selectReadingsPanel.loading = false
    },

    openPoint(pointId) {
      let self = this
      self.config.assetPointsPanel.selectedAssetOrPoint = pointId

      let assetIds =
        self.readings.categoryWithFields[
          self.config.mainPanel.selectedAssetCategory
        ][pointId]
      let dataPoints = assetIds.map(el => {
        return {
          parentId: el,
          yAxis: {
            fieldId: pointId,
            aggr:
              this.readings.fields[pointId] &&
              this.readings.fields[pointId].unit &&
              ['currency', 'kwh', 'co2', 'kg', 'mwh'].includes(
                this.readings.fields[pointId].unit.trim().toLowerCase()
              )
                ? 3
                : 2,
          },
        }
      })

      this.$emit(
        'updateDataPoints',
        dataPoints,
        self.config.mainPanel.selectedAssetCategory,
        pointId
      )
    },

    emptySelectedDataPoints() {
      if (this.replaceDataPoints) {
        this.isMultipleCategorySelected = false
        this.replaceDataPoints = false
        this.config.mainPanel.selectedAssetCategory = null
        this.setInitialValues([])
      }
    },

    getReadingFields(readings) {
      let data = []
      for (let reading of readings) {
        for (let field of reading.fields) {
          data.push(field)
        }
      }
      return data
    },

    loadSpaceReadings() {
      let self = this
      self.config.mainPanel.currentModule = 'spaces'

      self.togglePanel('selectReadingsPanel')
      self.config.selectReadingsPanel.loading = true
      self.config.selectReadingsPanel.parentId = self.buildingIds[0]

      self.$http
        .get(
          '/reading/getspacespecificreadings?parentId=' + self.buildingIds[0]
        )
        .then(function(response) {
          self.config.selectReadingsPanel.data = self.getReadingFields(
            response.data.filter(el => {
              // TODO check whether to show for building analysis also, all readings except enpi
              if (self.isSiteAnalysis ? el.type !== 7 : el.type === 3) {
                return el
              }
            })
          )
          self.config.selectReadingsPanel.loading = false
        })
    },

    loadWeatherReadings() {
      let self = this
      self.config.mainPanel.currentModule = 'weather'
      self.togglePanel('selectReadingsPanel')
      self.config.selectReadingsPanel.loading = true

      let fetchWeatherReadings = siteId => {
        self.$http
          .get(
            '/reading/getspacespecificreadings?excludeForecastReadings=false&parentId=' +
              siteId
          )
          .then(function(response) {
            self.config.selectReadingsPanel.data = self.getReadingFields(
              response.data
            )
            self.config.selectReadingsPanel.loading = false
          })
      }
      self.$http
        .get('/building/' + self.buildingIds[0])
        .then(function(response) {
          if (response.data.building) {
            let siteId = response.data.building.siteId
            self.config.selectReadingsPanel.parentId = siteId
            fetchWeatherReadings(siteId)
          } else {
            self.$http
              .get('/zone/' + self.buildingIds[0])
              .then(function(response) {
                if (response.data.zone) {
                  let siteId = response.data.zone.siteId
                  self.config.selectReadingsPanel.parentId = siteId
                  fetchWeatherReadings(siteId)
                }
              })
          }
        })
    },

    loadEnPIReadings() {
      let self = this
      self.config.mainPanel.currentModule = 'enpi'
      self.togglePanel('selectReadingsPanel')
      self.config.selectReadingsPanel.loading = true
      self.config.selectReadingsPanel.parentId = self.buildingIds[0]

      self.$http
        .get(
          '/reading/getspacespecificreadings?parentId=' + self.buildingIds[0]
        )
        .then(function(response) {
          self.config.selectReadingsPanel.data = self.getReadingFields(
            response.data.filter(el => {
              if (el.type === 7) {
                return el
              }
            })
          )
          self.config.selectReadingsPanel.loading = false
        })
    },

    cancel() {
      if (this.isMultipleCategorySelected) {
        this.isMultipleCategorySelected = false
      }
      this.$emit('update:visibility', false)
    },

    apply() {
      let dataPoints = []
      let parentId = null
      if (this.isMultipleCategorySelected) {
        this.isMultipleCategorySelected = false
      }
      if (
        this.config.mainPanel.selectedDataPoints &&
        this.config.mainPanel.selectedDataPoints.length
      ) {
        if (this.mode === 'mv') {
          let self = this
          self.$http.get(`/v2/mv/getMVProjectList`).then(function(response) {
            let mvprojects = response.data.result.mvprojects.filter(
              project =>
                project.siteId ===
                self.$store.state.buildings.filter(
                  building =>
                    building.id === parseInt(self.selectedBuildings[0])
                )[0].siteId
            )
            if (mvprojects) {
              parentId = mvprojects.map(project => {
                return project.id
              })
            }
            for (let dp of self.config.mainPanel.selectedDataPoints) {
              let categoryId = parseInt(dp.split('_')[0])
              let pointId = parseInt(dp.split('_')[1])
              dataPoints.push({
                categoryId: categoryId,
                parentId: parentId,
                moduleName: 'mvproject',
                yAxis: {
                  fieldId: pointId,
                  aggr:
                    self.readings.fields[pointId] &&
                    self.readings.fields[pointId].unit &&
                    ['currency', 'kwh', 'co2', 'kg', 'mwh'].includes(
                      self.readings.fields[pointId].unit.trim().toLowerCase()
                    )
                      ? 3
                      : 2,
                },
                duplicateDataPoint: false,
              })
            }
            self.$emit('updateDataPoints', dataPoints)
          })
        } else {
          for (let dp of this.config.mainPanel.selectedDataPoints) {
            let categoryId = parseInt(dp.split('_')[0])
            let pointId = parseInt(dp.split('_')[1])
            let moduleName = null

            dataPoints.push({
              categoryId: categoryId,
              parentId: null,
              moduleName: null,
              yAxis: {
                fieldId: pointId,
                aggr:
                  this.readings.fields[pointId] &&
                  this.readings.fields[pointId].unit &&
                  ['currency', 'kwh', 'co2', 'kg', 'mwh'].includes(
                    this.readings.fields[pointId].unit.trim().toLowerCase()
                  )
                    ? 3
                    : 2,
              },
            })
          }
          this.$emit('updateDataPoints', dataPoints)
        }
      }
    },

    setInitialValues(dataPoints, buildings) {
      if (
        dataPoints.length !== this.config.mainPanel.selectedDataPoints.length
      ) {
        this.config.mainPanel.selectedDataPoints = []
        dataPoints.forEach(dp => {
          if (dp.type !== 2 && !dp.duplicateDataPoint) {
            this.config.mainPanel.selectedDataPoints.push(
              dp.categoryId + '_' + dp.yAxis.fieldId
            )
            if (this.$route.query.reportId) {
              this.openAssetCategory(dp.categoryId)
            }
          }
        })
      }
      if (
        buildings &&
        buildings.length &&
        !this.selectedBuildings.includes(buildings[0])
      ) {
        this.setSelectedBuildings(buildings)
      }
    },
  },
}
</script>
<style>
.search-icon4 {
  width: 15px;
  fill: #6f7c87;
  height: 20px;
  top: 12px;
  left: 0;
  position: absolute;
}
.close-icon4 {
  width: 15px;
  fill: #6f7c87;
  height: 20px;
  position: absolute;
  right: 15px;
  top: 10px;
  cursor: pointer;
}
.quick-search-input4 {
  transition: 0.2s linear;
  padding: 10px 40px 8px 20px !important;
  line-height: 1.8;
  width: 100%;
  margin-bottom: 5px;
  border: none !important;
  outline: none;
  background: transparent;
  border-bottom: 1px solid #6f7c87 !important;
  border-radius: 0 !important;
}
.close-icon5 {
  width: 15px;
  fill: #6f7c87;
  height: 20px;
  position: absolute;
  right: 15px;
  top: 10px;
  cursor: pointer;
}
.quick-search-input5 {
  transition: 0.2s linear;
  padding: 10px 40px 8px 20px !important;
  line-height: 1.8;
  width: 100%;
  margin-bottom: 5px;
  border: none !important;
  outline: none;
  background: transparent;
  border-bottom: 1px solid #6f7c87 !important;
  border-radius: 0 !important;
}
.building-search-icon {
  font-size: 16px;
  position: absolute;
  right: 20px;
  color: #333333;
  z-index: 1;
}
.fc-list-search-wrapperbuilding-search2 {
  position: absolute;
  width: 100%;
  z-index: 1;
  top: 11px;
  left: 0;
  right: 0;
  transition: 0.3s;
  -webkit-transition: 0.3s;
  -moz-transition: 0.3s;
  z-index: 2;
}
.fc-list-search-wrapperbuilding-search3 {
  position: absolute;
  width: 100%;
  z-index: 1;
  top: 50px;
  left: 0;
  right: 0;
  transition: 0.3s;
  -webkit-transition: 0.3s;
  -moz-transition: 0.3s;
  z-index: 2;
}
.fc-list-search-wrapperbuilding-search {
  position: absolute;
  width: 100%;
  z-index: 1;
  top: 65px;
  left: 0;
  right: 0;
  transition: 0.3s;
  -webkit-transition: 0.3s;
  -moz-transition: 0.3s;
  z-index: 2;
}
.data-pointer-selector-popup {
  padding-left: 14px;
  border-bottom: 1px solid #e0e0e0;
}
.building-points-search .el-input__inner {
  height: 40px;
  line-height: 40px;
  padding-left: 24px;
  padding-right: 24px;
  border-bottom: 1px solid #e0e0e0;
}
.building-points-height .el-input__inner {
  height: 39px !important;
}

.added-datapoints-popover {
  border: 1px solid rgba(56, 85, 113, 0.3);
  border-radius: 0px;
}

.added-datapoints-header {
  padding: 15px;
  font-weight: 500;
  color: rgb(56, 85, 113);
  border-bottom: 1px solid rgba(56, 85, 113, 0.3);
}

.added-datapoints-collection {
  max-height: 300px;
  overflow: scroll;
}

.added-datapoints-collection ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.added-datapoints-collection ul li {
  padding: 15px;
  border-bottom: 1px solid #f4f4f4;
}
.Only-one-data-point .text-style-1 {
  color: #0088d4;
}
.analytics-topbar-warning {
  width: 100%;
  height: 45px;
  border: solid 1px #fc7300;
  background-color: #fff2e3;
  position: relative;
  text-align: center;
  font-size: 14px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: 0.54px;
  color: #324056;
  z-index: 900;
  display: flex;
  align-items: center;
  padding-left: 45px;
  padding-right: 45px;
  -webkit-transition: background 0.3s ease-in-out;
  -moz-transition: background 0.3s ease-in-out;
  -ms-transition: background 0.3s ease-in-out;
  -o-transition: background 0.3s ease-in-out;
  transition: background 0.3s ease-in-out;
}
</style>
