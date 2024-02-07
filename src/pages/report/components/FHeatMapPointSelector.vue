<template>
  <div v-if="visibility" class="analytics-sidebar-bg">
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

            <div class="building-analysis-side-block">
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
                  <div class="width80">{{ category.displayName }}</div>
                </div>
                <spinner
                  v-if="config.mainPanel.assetCategoriesLoading"
                  :show="config.mainPanel.assetCategoriesLoading"
                  class="clearboth"
                ></spinner>
                <div
                  v-else-if="!config.mainPanel.assetCategories.length"
                  class="building-anlysis-label-txt clearboth"
                >
                  -- No assets --
                </div>
              </div>
            </div>

            <div class="building-analysis-side-block">
              <div class="building-hover-actions">
                <div
                  class="sidebar-asset-H fL"
                  @click="loadSpaceReadings"
                  style="cursor : pointer;"
                >
                  Spaces
                </div>
              </div>
            </div>

            <div class="building-analysis-side-block" v-if="!isSiteAnalysis">
              <div class="building-hover-actions">
                <div
                  class="sidebar-asset-H fL"
                  @click="weatherPanelToggler"
                  style="cursor : pointer;"
                >
                  Weather
                </div>
                <div
                  lass="building-anlysis-active-block"
                  v-if="weatherPanel.show"
                >
                  <div
                    :class="[
                      'pointer',
                      'building-anlysis-label-txt',
                      'clearboth',
                      {
                        'asset-list-block-txt-active':
                          weatherPanel.weatherCategory &&
                          weatherModule.displayName ===
                            weatherPanel.weatherCategory.displayName,
                      },
                    ]"
                    @click="loadWeatherReadings(weatherModule)"
                    v-for="(weatherModule, wIdx) in weatherModules"
                    :key="wIdx"
                  >
                    <i
                      v-bind:class="{
                        'fa fa-circle': true,
                        'dot-blue-icon': weatherModule.selected === true,
                        'dot-grey-icon': weatherModule.selected === false,
                      }"
                      aria-hidden="true"
                    ></i>
                    <div class="width80">{{ weatherModule.displayName }}</div>
                  </div>
                </div>
              </div>
            </div>

            <div class="building-analysis-side-block">
              <div class="building-hover-actions">
                <div
                  class="sidebar-asset-H fL"
                  @click="loadEnPIReadings"
                  style="cursor : pointer;"
                >
                  EnPI
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
            <el-tabs
              v-model="config.assetPointsPanel.activeSubTab"
              @tab-click="
                openAssetCategory(config.mainPanel.selectedAssetCategory)
              "
            >
              <el-tab-pane label="Points" name="points">
                <span slot="label"
                  ><img
                    src="~assets/gauge.svg"
                    width="18px"
                    height="18px"
                    style="position: relative;top: 4px;right: 7px;"
                  />Points</span
                >
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
                    No points found.
                  </div>
                  <div
                    class="asset-list-block-txt"
                    v-else
                    v-for="(item, index) in filteredAssetPointsPanel"
                    :key="index"
                    @click="openPoint(item.id, item.displayName)"
                    :class="{
                      'asset-list-block-txt-active':
                        config.assetPointsPanel.selectedAssetOrPoint ===
                        item.id,
                    }"
                  >
                    <div class="asset-active-select-item">
                      {{ item.displayName ? item.displayName : item.name }}
                    </div>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="Assets" name="assets">
                <span slot="label"
                  ><img
                    src="~assets/one-box-black.svg"
                    width="16px"
                    height="16px"
                    style="position: relative;top: 4px;right: 8px;"
                  />Assets</span
                >
                <div class="building-points-search">
                  <el-input
                    placeholder="Filter assets"
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
                    No assets found.
                  </div>
                  <div
                    class="asset-list-block-txt"
                    v-else
                    v-for="(item, index) in filteredAssetPointsPanel"
                    :key="index"
                    @click="openAsset(item.id, item.name)"
                    :class="{
                      'asset-list-block-txt-active':
                        config.assetPointsPanel.selectedAssetOrPoint ===
                        item.id,
                    }"
                  >
                    <div class="asset-active-select-item">
                      {{ item.name ? item.name : item.displayName }}
                    </div>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
        <!-- sidebar2 end -->

        <!-- sidebar3 start -->
        <div
          class="assets-points-Scontainer assets-points-Scontainer-shadow"
          v-if="config.selectReadingsPanel.show"
        >
          <div>
            <div class="assets-points-inner-container readings-list-container">
              <div class="select-reading-header">
                <div class="select-reading-H">
                  SELECT READINGS
                </div>
              </div>
              <div class="building-points-search building-points-height">
                <el-input
                  placeholder="Filter readings"
                  v-model="config.selectReadingsPanel.filterQuery"
                  autofocus
                  class=""
                ></el-input>
              </div>
              <div class="asset-list-container mT10">
                <spinner
                  v-if="config.selectReadingsPanel.loading"
                  :show="config.selectReadingsPanel.loading"
                ></spinner>
                <div
                  class="text-center"
                  v-else-if="
                    !config.selectReadingsPanel.data ||
                      !config.selectReadingsPanel.data.length
                  "
                >
                  No data found.
                </div>
                <div
                  class="asset-list-block-txt"
                  v-for="(item, index) in filteredReadingsPanel"
                  :key="index"
                  @click="
                    selectDataPoint(
                      item.id + '_' + config.selectReadingsPanel.parentId
                    ),
                      apply(item.displayName ? item.displayName : item.name)
                  "
                >
                  {{ item.displayName ? item.displayName : item.name }}
                  <i
                    class="el-icon-check heatmap-icon"
                    v-if="
                      config.mainPanel.selectedDataPoints[0] ===
                        item.id + '_' + config.selectReadingsPanel.parentId
                    "
                  ></i>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div
          class="assets-points-Scontainer assets-points-Scontainer-shadow"
          v-if="config.selectAssetsPanel.show"
        >
          <div>
            <div class="assets-points-inner-container readings-list-container">
              <div class="select-reading-header">
                <div class="select-reading-H">
                  SELECT ASSETS
                </div>
              </div>
              <div class="building-points-search building-points-height">
                <el-input
                  placeholder="Filter assets"
                  v-model="config.selectAssetsPanel.filterQuery"
                  autofocus
                  class=""
                ></el-input>
              </div>
              <div class="asset-list-container mT10">
                <spinner
                  v-if="config.selectAssetsPanel.loading"
                  :show="config.selectAssetsPanel.loading"
                ></spinner>
                <div
                  class="text-center"
                  v-else-if="
                    !config.selectAssetsPanel.data ||
                      !config.selectAssetsPanel.data.length
                  "
                >
                  No data found.
                </div>
                <div
                  class="asset-list-block-txt"
                  v-for="(item, index) in filteredAssetsPanel"
                  :key="index"
                  @click="
                    selectDataPoint(
                      config.selectAssetsPanel.readingId + '_' + item.id
                    ),
                      apply(item.displayName ? item.displayName : item.name)
                  "
                >
                  {{ item.displayName ? item.displayName : item.name }}
                  <i
                    class="el-icon-check heatmap-icon"
                    v-if="
                      config.mainPanel.selectedDataPoints[0] ===
                        config.selectAssetsPanel.readingId + '_' + item.id
                    "
                  ></i>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- sidebar3 end -->
      </div>
    </div>
    <!-- dataPoint dialog deletion start-->
    <el-dialog
      :visible.sync="deleteRangePoint"
      title="Delete Range point"
      class="fc-dialog-center-container"
      width="40%"
      :append-to-body="true"
      :show-close="false"
      @keydown.esc="deleteRangePoint = false"
      :close-on-click-modal="false"
    >
      <div class="height200">
        <div>
          This datapoint is part of a range group. Deleting it, removes the
          range group.
        </div>
        <div>Are you sure you want to delete it?</div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelRangeDeletion"
            >Close</el-button
          >
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="deleteRangePoints"
            >Delete</el-button
          >
        </div>
      </div>
    </el-dialog>
    <!-- dataPoint dialog deletion end -->
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
    'savedPoints',
    'reportObject',
  ],
  data() {
    return {
      config: {
        mainPanel: {
          currentModule: '',
          assetCategories: null,
          assetCategoriesLoading: false,
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
      weatherPanel: {
        show: false,
        weatherCategory: null,
      },
      selectedDataPoints: [],
      loading: true,
      readings: {
        categories: [],
        assets: {},
        categoryWithAssets: {},
        fields: {},
        categoryWithFields: {},
      },
      showQuickSearch: false,
      quickSearchQuery: null,
      selectedBuildings: [],
      weatherModules: [
        {
          displayName: 'Live Weather',
          moduleName: 'weather',
          selected: false,
        },
        {
          displayName: 'Daily Weather',
          moduleName: 'weatherDaily',
          selected: false,
        },
        {
          displayName: 'Degree Days',
          selected: false,
        },
        {
          displayName: 'Psychrometric',
          moduleName: 'psychrometric',
          selected: false,
        },
      ],
      deleteRangePoint: false,
      rangePointConfirmation: false,
      pointDeletionStore: {},
      summaryList: [],
      allFields: {
        assets: [],
      },
      currentKey: 0,
      siteId: null,
      readingName: '',
      assetName: '',
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

  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),

    isRangeModeEnabled() {
      if (
        typeof this.reportObject !== 'undefined' &&
        this.reportObject !== null
      ) {
        let dP = this.reportObject.options.dataPoints.filter(
          point => point.type === 'rangeGroup'
        )
        if (dP.length !== 0) {
          return true
        }
        return false
      }
      return false
    },
    moreThanOneLocation() {
      let locationSet = new Set()
      if (this.summaryList.length !== 0) {
        for (let group of this.summaryList) {
          for (let reading of group.readings) {
            locationSet.add(reading.location)
          }
        }
        if (locationSet.size > 1) {
          return true
        } else {
          return false
        }
      } else {
        return false
      }
    },
    showFourthPanel() {
      if (this.summaryList.length === 0) {
        return false
      } else if (
        this.config.mainPanel.selectedDataPoints.length === 0 &&
        this.summaryList.length !== 0
      ) {
        this.addToSummaryListNew()
        return false
      } else {
        return true
      }
    },
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
              if (!['BOOLEAN', 'ENUM'].includes(d.dataTypeEnum)) {
                return d
              }
            }
          })
        } else {
          return this.config.assetPointsPanel.data.filter(function(d) {
            if (!['BOOLEAN', 'ENUM'].includes(d.dataTypeEnum)) {
              return d
            }
          })
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
              if (!['BOOLEAN', 'ENUM'].includes(d.dataTypeEnum)) {
                return d
              }
            }
          })
        } else {
          return this.config.selectReadingsPanel.data.filter(function(d) {
            if (!['BOOLEAN', 'ENUM'].includes(d.dataTypeEnum)) {
              return d
            }
          })
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
    'config.mainPanel.selectedDataPoints': {
      handler: function(newVal, oldVal) {
        if (
          newVal.length !== 0 &&
          (this.savedPoints === null || typeof this.savedPoints === 'undefined')
        ) {
          this.addToSummaryListNew()
        }
      },
      deep: true,
    },
    savedPoints: function(newValue, oldVal) {
      if (newValue && JSON.stringify(newValue) !== JSON.stringify(oldVal)) {
        if (this.config.mainPanel.assetCategories) {
          this.prepareSavedDataPoints()
        }
      }
    },
  },
  mounted() {
    this.setDefaultSelection()
  },
  created() {
    Promise.all([
      this.$store.dispatch('loadAssetCategory'),
      this.loadDefaultBuilding(),
    ]).then(() => {
      this.setDefaultSelection()
    })
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
    selectDataPoint(datapoint) {
      this.config.mainPanel.selectedDataPoints[0] = datapoint
      this.addToBuildingMap(this.config.mainPanel.selectedDataPoints)
    },
    addToBuildingMap(newVal) {
      let buildingId = this.buildingIds[0]

      if (!this.config.buildingToReadingMap) {
        this.config['buildingToReadingMap'] = {}
      }
      this.config.buildingToReadingMap[buildingId] = newVal
    },
    checkForRangePoints(newVal) {
      this.addToBuildingMap(newVal)
      if (this.reportObject && this.summaryList.length !== 0) {
        let rangeGroups = this.reportObject.options.dataPoints.filter(
          dP => dP.type === 'rangeGroup'
        )
        if (rangeGroups.length !== 0) {
          let inRangeGroup = new Set()
          for (let rGroup of rangeGroups) {
            for (let child of rGroup.children) {
              inRangeGroup.add(child.fieldId + '_' + child.parentId)
              this.pointDeletionStore['readingId'] = child.fieldId
              this.pointDeletionStore['resourceId'] = child.parentId
            }
          }
          for (let inPoint of inRangeGroup) {
            if (!newVal.includes(inPoint)) {
              this.deleteRangePoint = true
              return
            }
          }
        }
      }
    },
    getAllSummaryDataPoints() {
      let allDataPoints = []
      if (this.summaryList) {
        for (let key in this.summaryList) {
          allDataPoints.push(...this.summaryList[key].readings)
        }
      }
      return allDataPoints
    },
    convertToList(readings) {
      let readingsObject = {}
      for (let reading of readings) {
        readingsObject[reading.id] = reading
      }
      return readingsObject
    },
    cancelRangeDeletion() {
      this.pointDeletionStore = {}
      let dataPoints = []
      for (let dp of this.reportObject.options.dataPoints) {
        if (dp.children && dp.children.length !== 0) {
          dataPoints.push(...JSON.parse(JSON.stringify(dp.children)))
        } else {
          dataPoints.push(JSON.parse(JSON.stringify(dp)))
        }
      }

      for (let dataPoint of dataPoints) {
        dataPoint['aliases'] = {}
        dataPoint.aliases['actual'] = dataPoint.alias
        dataPoint['yAxis'] = {}
        dataPoint.yAxis['fieldId'] = dataPoint.fieldId
      }
      this.setInitialValues(dataPoints, null, 'restoreRange')
      this.deleteRangePoint = false
    },
    deleteRangePoints() {
      // obtain range points
      let existingDataPoints = [...this.config.mainPanel.selectedDataPoints]
      let readingId = this.pointDeletionStore['readingId']
      let resourceId = this.pointDeletionStore['resourceId']
      let tobeDeleted = []
      if (this.reportObject) {
        for (let dP of this.reportObject.options.dataPoints) {
          if (dP.type === 'rangeGroup') {
            let inDp = dP.children.filter(
              rDp => rDp.fieldId === readingId && rDp.parentId === resourceId
            )
            if (inDp.length !== 0) {
              tobeDeleted.push(...dP.children)
              break
            }
          }
        }

        // to reduce dataPoints
        let toBeDeleted = this.reduceDataPoints(tobeDeleted)
        for (let dataPoint of toBeDeleted) {
          let index = existingDataPoints.indexOf(
            dataPoint.fieldId + '_' + dataPoint.parentId
          )
          if (index !== -1) {
            existingDataPoints.splice(index, 1)
          }
        }
        this.config.mainPanel.selectedDataPoints = existingDataPoints
        this.addToSummaryListNew()
        this.deleteRangePoint = false
      }
    },
    reduceDataPoints(toBeDeletedDataPoints) {
      let uniqueKeys = new Set()
      toBeDeletedDataPoints.map(dp =>
        uniqueKeys.add(dp.fieldId + '.' + dp.parentId)
      )

      if (uniqueKeys.size === 1) {
        return [toBeDeletedDataPoints[0]]
      } else {
        return toBeDeletedDataPoints
      }
    },
    prepareSavedDataPoints() {
      if (this.savedPoints && typeof this.savedPoints !== 'undefined') {
        let points = [...this.savedPoints]
        this.currentKey = this.currentKey + 1
        for (let group of this.savedPoints) {
          for (let reading of group.readings) {
            reading.marked = this.currentKey
          }
        }
        this.summaryList = []

        let assetReadings = points.filter(group => group.label === 'Asset')
        let assetGroupIndex = points.indexOf(assetReadings[0])
        if (assetReadings.length !== 0) {
          for (let inReading of assetReadings[0].readings) {
            let assetCategory = inReading.categoryId ? inReading.categoryId : -1
            let assetCategoryLabel = this.assetCategory.filter(
              category => category.id === parseInt(assetCategory)
            )
            if (assetCategoryLabel.length !== 0) {
              assetCategoryLabel = assetCategoryLabel[0].displayName
              this.addAssetToGroup(assetCategoryLabel, inReading, assetCategory)
            }
            if (this.config.mainPanel.assetCategories) {
              let category = this.config.mainPanel.assetCategories.filter(
                categories => categories.id === parseInt(assetCategory)
              )
              if (category.length !== 0) {
                this.openAssetCategory(assetCategory)
                let self = this
                this.$nextTick().then(function() {
                  if (
                    typeof self.readings.categoryWithFields[assetCategory] !==
                    'undefined'
                  ) {
                    let asset =
                      self.readings.categoryWithFields[assetCategory][
                        inReading.readingId
                      ]
                    if (asset) {
                      self.openPoint(inReading.readingId)
                    }
                  }
                })
              }
            }
          }
        }
        if (assetGroupIndex !== -1) {
          points.splice(assetGroupIndex, 1)
        }
        this.summaryList.push(...points)
        this.savedPoints = null
      }
    },
    addAssetToGroup(label, reading, assetCategory) {
      if (this.summaryList && this.summaryList.length === 0) {
        let temp = {}
        temp['label'] = label
        temp['categoryId'] = assetCategory
        temp['readings'] = []
        temp.readings.push(reading)
        this.summaryList.push(temp)
      } else if (this.summaryList && this.summaryList.length > 0) {
        let readingGroup = this.summaryList.filter(
          group => group.label === label
        )
        if (readingGroup.length !== 0) {
          readingGroup[0].readings.push(reading)
        } else {
          let temp = {}
          temp['label'] = label
          temp['categoryId'] = assetCategory
          temp['readings'] = []
          temp.readings.push(reading)
          this.summaryList.push(temp)
        }
      }
    },
    getRangeGroup(reading) {
      if (this.reportObject) {
        for (let dataPoint of this.reportObject.options.dataPoints) {
          if (dataPoint.type === 'rangeGroup' && dataPoint.children) {
            for (let child of dataPoint.children) {
              if (
                child.fieldId === reading.readingId &&
                child.parentId === reading.resourceId
              ) {
                return true
              }
            }
          }
        }
      }
    },
    removeDataPointFromGroup(reading, readingGroupIndex) {
      if (this.isRangeModeEnabled) {
        // to find range point
        let isRangePoint = this.getRangeGroup(reading)

        if (isRangePoint) {
          this.deleteRangePoint = true
          this.pointDeletionStore['readingId'] = reading.readingId
          this.pointDeletionStore['resourceId'] = reading.resourceId
        } else {
          this.removePoint(reading, readingGroupIndex)
        }
      } else {
        this.removePoint(reading, readingGroupIndex)
      }
    },
    removePoint(reading, readingGroupIndex) {
      let readingGroup = this.summaryList[readingGroupIndex]
      let readingSumId = reading.readingId + '_' + reading.resourceId
      this.config.mainPanel.selectedDataPoints.splice(
        this.config.mainPanel.selectedDataPoints.indexOf(readingSumId),
        1
      )
      let readingIndex = readingGroup.readings.indexOf(reading)
      readingGroup.readings.splice(readingIndex, 1)
      if (readingGroup.readings.length === 0) {
        this.summaryList.splice(readingGroupIndex, 1)
      }
    },
    isSummaryEmpty(id, isAsset) {
      if (this.summaryList.length === 0) {
        return true
      } else if (isAsset) {
        let filteredGroup = this.summaryList.filter(
          pointGroup => pointGroup.categoryId && pointGroup.categoryId === id
        )
        if (filteredGroup.length !== 0) {
          return false
        }
        return true
      } else {
        let filteredGroup = this.summaryList.filter(
          pointGroup => pointGroup.label === id
        )
        if (filteredGroup.length !== 0) {
          return false
        }
        return true
      }
    },
    getCategoryFromId(reading) {
      let readingSplit = reading.split('_')
      let assetObject = this.readings.assets[
        parseInt(readingSplit[readingSplit.length - 1])
      ]
      if (this.buildingIds[0] === parseInt(readingSplit[1])) {
        return ''
      } else if (assetObject) {
        return 'assets'
      } else {
        return 'weather'
      }
    },

    addToSummaryListNew() {
      let existingReadings = []
      let points = []
      if (
        this.config.mainPanel.selectedDataPoints &&
        this.config.mainPanel.selectedDataPoints.length !== 0
      ) {
        console.log('selectedpoints', this.config.mainPanel.selectedDataPoints)
        for (let point of this.config.mainPanel.selectedDataPoints) {
          let temp = {
            point: point,
            isDone: false,
          }
          points.push(temp)
        }

        if (this.summaryList.length !== 0) {
          this.currentKey = this.currentKey + 1
          for (let group of this.summaryList) {
            for (let reading of group.readings) {
              existingReadings.push(reading)
            }
          }
          for (let dataPoint of points) {
            let readingSplit = dataPoint.point.split('_')
            let readingId = parseInt(readingSplit[0])
            let resourceId = parseInt(readingSplit[readingSplit.length - 1])

            let reading = existingReadings.filter(
              element =>
                element.readingId + '_' + element.resourceId ===
                readingId + '_' + resourceId
            )
            if (reading.length !== 0) {
              reading[0].marked = this.currentKey
              dataPoint.isDone = true
            } else {
              continue
            }
          }
        }

        this.removeUnwantedReadings()

        let notInserted = points.filter(point => point.isDone === false)
        if (notInserted.length !== 0) {
          for (let nInsert of notInserted) {
            let readingSplit = nInsert.point.split('_')
            let category = this.getCategoryFromId(nInsert.point)
            let readingObject = null
            if (category && category !== '') {
              readingObject = this.allFields[category][
                parseInt(readingSplit[0])
              ]
            }

            let categoryLabel = null
            let categoryId = null

            if (category === 'assets') {
              for (let cat of Object.keys(this.readings.categoryWithAssets)) {
                if (
                  Object.keys(this.readings.categoryWithAssets[cat]).includes(
                    readingSplit[readingSplit.length - 1]
                  )
                ) {
                  categoryId = parseInt(cat)
                  break
                } else {
                  categoryId = -1
                }
              }
              categoryLabel = this.config.mainPanel.assetCategories.filter(
                categories => categories.id === categoryId
              )[0].name
            } else if (category === 'weather') {
              for (let group of this.summaryList) {
                for (let reading of group.readings) {
                  if (
                    reading.readingId + '_' + reading.resourceId ===
                    readingSplit[0] + '_' + readingSplit[1]
                  ) {
                    categoryLabel = group.label
                    break
                  }
                }
              }

              if (!categoryLabel) {
                categoryLabel = this.weatherModules.filter(
                  wModule =>
                    wModule && wModule.moduleName === readingObject.module
                )
                if (categoryLabel.length === 0) {
                  categoryLabel = 'Degree Days'
                } else {
                  categoryLabel = categoryLabel[0].displayName
                }
              }
            } else {
              if (this.allFields.length !== 0) {
                if (
                  this.allFields['space'] &&
                  typeof this.allFields['space'][parseInt(readingSplit[0])] !==
                    'undefined'
                ) {
                  readingObject = this.allFields['space'][
                    parseInt(readingSplit[0])
                  ]
                  categoryLabel = 'Space'
                } else {
                  readingObject = this.allFields['enpi'][
                    parseInt(readingSplit[0])
                  ]
                  categoryLabel = 'Enpi'
                }
              }
            }

            let existingGroup = this.summaryList.filter(
              inGroup => inGroup.label === categoryLabel
            )
            if (this.summaryList.length === 0 || existingGroup.length === 0) {
              let reading = {}

              reading['readingId'] = parseInt(readingSplit[0])
              reading['readingLabel'] = readingObject.displayName
              reading['resourceId'] = parseInt(
                readingSplit[readingSplit.length - 1]
              )
              reading['resourceLabel'] =
                category === 'assets'
                  ? this.readings.assets[
                      parseInt(readingSplit[readingSplit.length - 1])
                    ]
                  : null
              reading['location'] = this.selectedBuildingLabel
              reading['marked'] = this.currentKey

              let temp = {}
              temp['label'] = categoryLabel
              temp['readings'] = []
              if (category === 'assets') {
                temp['categoryId'] = categoryId
              }
              temp.readings.push(reading)
              this.summaryList.push(temp)
            } else {
              let reading = {}

              reading['readingId'] = parseInt(readingSplit[0])
              reading['readingLabel'] = readingObject.displayName
              reading['resourceId'] = parseInt(
                readingSplit[readingSplit.length - 1]
              )
              reading['resourceLabel'] =
                category === 'assets'
                  ? this.readings.assets[
                      parseInt(readingSplit[readingSplit.length - 1])
                    ]
                  : null
              reading['location'] = this.selectedBuildingLabel
              reading['marked'] = this.currentKey

              existingGroup[0].readings.push(reading)
            }
            nInsert.isDone = true
          }
        }
      }
    },
    removeUnwantedReadings() {
      for (let group of this.summaryList) {
        for (let reading of group.readings) {
          if (reading && reading.marked !== this.currentKey) {
            let readingIndex = group.readings.indexOf(reading)
            group.readings.splice(readingIndex, 1)
          }
          if (group.readings.length === 0) {
            this.summaryList.splice(this.summaryList.indexOf(group), 1)
          }
        }
      }
    },
    weatherPanelToggler() {
      if (this.weatherPanel.show) {
        this.weatherPanel.show = false
      } else {
        this.weatherPanel.show = true
      }
    },
    weatherModuleHandler(weatherModule, readings) {
      if (weatherModule.moduleName) {
        let filtered = []
        for (let reading of readings) {
          if (reading.module === weatherModule.moduleName) {
            filtered.push(reading)
          }
        }
        return filtered
      } else {
        let filtered = []
        for (let reading of readings) {
          if (
            reading.module === 'cdd' ||
            reading.module === 'hdd' ||
            reading.module === 'wdd'
          ) {
            filtered.push(reading)
          }
        }
        return filtered
      }
    },
    setDefaultSelection() {
      this.selectedBuildings = []
      if (this.default_building_id) {
        this.selectedBuildings = [this.default_building_id]
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
    loadAssetCategories() {
      let self = this
      self.weatherPanel.show = false
      self.weatherPanel.weatherCategory = null
      for (let module of self.weatherModules) {
        module.selected = false
      }
      if (self.config.mainPanel.currentModule === 'assets') {
        self.config.mainPanel.currentModule = ''
        return
      }
      self.config.mainPanel.currentModule = 'assets'
      self.config.mainPanel.selectedAssetCategory = null
      self.config.mainPanel.assetCategories = null
      self.config.mainPanel.assetCategoriesLoading = true
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

      self.$http
        .post('/asset/getAssetCategoryWithReadings', {
          buildingIds: self.buildingIds,
        })
        .then(function(response) {
          let categoryIds = Object.keys(response.data)
          if (self.assetCategory) {
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
          }
          self.config.mainPanel.assetCategoriesLoading = false
          self.loading = false
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

    async openAssetCategory(categoryId) {
      let self = this
      self.togglePanel('assetPointsPanel')
      self.config.mainPanel.selectedAssetCategory = categoryId
      self.config.assetPointsPanel.loading = true
      self.config.assetPointsPanel.selectedAssetOrPoint = null

      if (
        self.readings.categories.includes(categoryId) &&
        self.readings.categoryWithAssets[categoryId]
      ) {
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
          await self.$http
            .post('/asset/getReadingsForSpecificAssetCategory', {
              buildingIds: self.buildingIds,
              categoryIds: [categoryId],
            })
            .then(function(response) {
              if (!self.readings.categories.includes(categoryId)) {
                self.readings.categories.push(categoryId)
              }
              if (response.data.categoryWithAssets[categoryId]) {
                self.readings.categoryWithAssets[categoryId] =
                  response.data.categoryWithAssets[categoryId]
              }
              if (response.data.categoryWithFields[categoryId]) {
                self.readings.categoryWithFields[categoryId] =
                  response.data.categoryWithFields[categoryId]
              }
              if (response.data.fields) {
                for (let fieldId of Object.keys(response.data.fields)) {
                  if (!self.readings.fields[fieldId]) {
                    self.readings.fields[fieldId] =
                      response.data.fields[fieldId]
                  }
                  if (
                    self.allFields['assets'] &&
                    !self.allFields['assets'][fieldId]
                  ) {
                    self.allFields['assets'][fieldId] =
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
              if (
                self.config.assetPointsPanel.activeSubTab === 'assets' &&
                self.readings.categoryWithAssets[categoryId]
              ) {
                let assetIds = Object.keys(
                  self.readings.categoryWithAssets[categoryId]
                )
                self.config.assetPointsPanel.data = assetIds.map(el => {
                  return { id: el, name: self.readings.assets[el] }
                })
                self.config.assetPointsPanel.loading = false
              } else if (self.readings.categoryWithFields[categoryId]) {
                let readingIds = Object.keys(
                  self.readings.categoryWithFields[categoryId]
                )
                self.config.assetPointsPanel.data = readingIds.map(el => {
                  return self.readings.fields[el]
                })
                self.config.assetPointsPanel.loading = false
              } else {
                self.config.assetPointsPanel.loading = false
              }
            })
        }
      }
    },

    openAsset(assetId, assetName) {
      let self = this
      self.config.assetPointsPanel.selectedAssetOrPoint = assetId

      self.togglePanel('assetPointsPanel', 'selectReadingsPanel')
      self.config.selectReadingsPanel.loading = true
      self.config.selectReadingsPanel.parentId = assetId
      if (assetName) {
        self.assetName = assetName
      }

      let fieldIds =
        self.readings.categoryWithAssets[
          self.config.mainPanel.selectedAssetCategory
        ][assetId]
      self.config.selectReadingsPanel.data = fieldIds.map(el => {
        return self.readings.fields[el]
      })
      self.config.selectReadingsPanel.loading = false
    },

    openPoint(pointId, pointName) {
      let self = this
      self.config.assetPointsPanel.selectedAssetOrPoint = pointId

      self.togglePanel('assetPointsPanel', 'selectAssetsPanel')
      self.config.selectAssetsPanel.loading = true
      self.config.selectAssetsPanel.readingId = pointId
      if (pointName) {
        self.readingName = pointName
      }

      let assetIds =
        self.readings.categoryWithFields[
          self.config.mainPanel.selectedAssetCategory
        ][pointId]
      self.config.selectAssetsPanel.data = assetIds.map(el => {
        return { id: el, name: self.readings.assets[el] }
      })
      self.config.selectAssetsPanel.loading = false
    },

    getReadingFields(readings) {
      let data = []
      for (let reading of readings) {
        for (let field of reading.fields) {
          field.module = reading.name
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
          self.allFields['space'] = self.convertToList(
            self.config.selectReadingsPanel.data
          )
          self.config.selectReadingsPanel.loading = false
        })
    },

    loadWeatherReadings(weatherModule) {
      let self = this
      self.weatherPanel.weatherCategory = weatherModule
      for (let module of self.weatherModules) {
        module.selected = false
      }
      self.config.selectReadingsPanel.loading = true
      self.config.mainPanel.currentModule = weatherModule.moduleName
      self.togglePanel('selectReadingsPanel')
      weatherModule.selected = true

      let fetchWeatherReadings = siteId => {
        self.$http
          .get(
            '/reading/getspacespecificreadings?excludeForecastReadings=false&parentId=' +
              siteId
          )
          .then(function(response) {
            self.config.selectReadingsPanel.data = self.weatherModuleHandler(
              weatherModule,
              self.getReadingFields(response.data)
            )
            self.allFields['weather'] = self.convertToList(
              self.config.selectReadingsPanel.data
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
          self.allFields['enpi'] = self.convertToList(
            self.config.selectReadingsPanel.data
          )
          self.config.selectReadingsPanel.loading = false
        })
    },

    cancel() {
      this.$emit('update:visibility', false)
    },

    getBuildingId(dp) {
      for (let building in this.config.buildingToReadingMap) {
        let dataPoint = this.config.buildingToReadingMap[building].find(
          d => d === dp
        )
        if (dataPoint) {
          return building
        }
      }
      return null
    },

    apply() {
      let dataPoints = []
      let tempName = ''
      if (
        this.config.mainPanel.selectedDataPoints &&
        this.config.mainPanel.selectedDataPoints.length
      ) {
        for (let dp of this.config.mainPanel.selectedDataPoints) {
          let buildingId = this.getBuildingId(dp)
          let readingFieldId = parseInt(dp.split('_')[0])
          let parentId = parseInt(dp.split('_')[1])
          let name = ' '
          if (
            this.readings.assets[parentId] &&
            this.allFields.assets &&
            this.allFields.assets[readingFieldId]
          ) {
            name =
              this.readings.assets[parentId] +
              ' ( ' +
              this.allFields.assets[readingFieldId].displayName +
              ' )'
          } else if (!this.readingName && !this.assetName) {
            if (this.allFields.space && this.allFields.space[readingFieldId]) {
              name = this.allFields.space[readingFieldId].displayName
            } else if (
              this.allFields.weather &&
              this.allFields.weather[readingFieldId]
            ) {
              name = this.allFields.weather[readingFieldId].displayName
            } else if (
              this.allFields.enpi &&
              this.allFields.enpi[readingFieldId]
            ) {
              name = this.allFields.enpi[readingFieldId].displayName
            }
          }
          dataPoints.push({
            parentId: parentId,
            name: name,
            yAxis: {
              fieldId: readingFieldId,
              aggr:
                this.readings.fields[readingFieldId] &&
                this.readings.fields[readingFieldId].unit &&
                ['currency', 'kwh', 'co2', 'kg', 'mwh'].includes(
                  this.readings.fields[readingFieldId].unit.trim().toLowerCase()
                )
                  ? 3
                  : 2,
            },
            buildingId: parseInt(buildingId),
            prediction:
              this.readings &&
              this.readings.fields &&
              this.readings.fields[readingFieldId] &&
              this.readings.fields[readingFieldId].module
                ? this.readings.fields[readingFieldId].module.type === 11
                : false,
            duplicateDataPoint: false,
          })
          this.assetName = ''
          this.readingName = ''
          tempName = ''
        }
      }
      if (this.config.mainPanel.selectedAssetCategory) {
        this.$parent.assetCategoryName = this.config.mainPanel.assetCategories.filter(
          categories =>
            categories.id === this.config.mainPanel.selectedAssetCategory
        )[0].name
      }
      this.$emit('updateDataPoints', dataPoints)
    },
    checkAndReduceRangeTraces(dataPoints) {
      let finalDataPoints = []
      let keyAndDataPoints = {}
      for (let dP of dataPoints) {
        if (
          keyAndDataPoints.hasOwnProperty(dP.yAxis.fieldId + '.' + dP.parentId)
        ) {
          keyAndDataPoints[dP.yAxis.fieldId + '.' + dP.parentId].push(dP)
        } else {
          keyAndDataPoints[dP.yAxis.fieldId + '.' + dP.parentId] = []
          keyAndDataPoints[dP.yAxis.fieldId + '.' + dP.parentId].push(dP)
        }
      }
      //reducing data point
      for (let key in keyAndDataPoints) {
        if (keyAndDataPoints[key].length === 3) {
          let aliases = keyAndDataPoints[key][0].aliases.actual.split('.')
          let dataPoint = this.$helpers.cloneObject(keyAndDataPoints[key][0])

          dataPoint.aliases.actual = aliases[0]
          finalDataPoints.push(dataPoint)
        } else {
          finalDataPoints.push(keyAndDataPoints[key][0])
        }
      }
      return finalDataPoints
    },
    setInitialValues(dataPoints, buildings) {
      let finalDataPoints = this.checkAndReduceRangeTraces(dataPoints)
      if (
        finalDataPoints.length !==
        this.config.mainPanel.selectedDataPoints.length
      ) {
        this.config.mainPanel.selectedDataPoints = []
        finalDataPoints.forEach(dp => {
          if (dp.type !== 2 && !dp.duplicateDataPoint) {
            this.config.mainPanel.selectedDataPoints.push(
              dp.yAxis.fieldId + '_' + dp.parentId
            )
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
.heatmap-icon {
  padding-top: 5px;
  color: #39b2c2;
  font-size: 15px;
  font-weight: 900;
  position: absolute;
  right: 20px;
}
.heatmap-right-icon {
  position: absolute;
  right: 10px;
  top: 3px;
}
</style>
