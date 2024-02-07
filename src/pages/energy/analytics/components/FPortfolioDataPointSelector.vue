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
        <!-- sidebar1 start  -->
        <div class="newanalytics-sidebar col-3" v-else>
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
                  <div class="width80">
                    {{
                      category.displayName
                        ? category.displayName
                        : category.name
                    }}
                  </div>
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
              v-if="['mv', 4].includes(mode)"
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
            <div
              class="building-analysis-side-block"
              v-if="[6, 7, 8].includes(mode)"
            >
              <div class="building-hover-actions">
                <div
                  class="sidebar-asset-H fL"
                  @click="openModuleFieldsPanel()"
                  style="cursor : pointer;"
                >
                  {{ moduleMap[mode] }} Fields
                </div>
              </div>
            </div>
            <div v-if="newSiteSummary" class="building-analysis-side-block">
              <div class="building-hover-actions">
                <div
                  class="sidebar-asset-H fL"
                  @click="loadWeatherStationsAndFields()"
                  style="cursor : pointer;"
                >
                  Weather
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- Sidebar end -->

        <!-- sidebar2 start -->
        <!-- asset points panel start -->
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
                No points found.
              </div>
              <el-checkbox-group
                v-model="config.mainPanel.selectedDataPoints"
                class="building-check-list"
                v-else-if="!isSinglePointSelector"
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
              <div
                class="asset-list-block-txt"
                v-else
                v-for="(item, index) in filteredAssetPointsPanel"
                :key="index"
                @click="
                  selectDataPoint(
                    config.mainPanel.selectedAssetCategory + '_' + item.id
                  ),
                    apply()
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
        <!-- asset points panel end -->

        <!-- module fields panel start  -->
        <div
          class="assets-points-Scontainer assets-points-Scontainer-lalign"
          v-if="config.moduleFieldsPanel.show && [6, 7, 8].includes(mode)"
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
                v-model="config.moduleFieldsPanel.filterQuery"
                autofocus
                class=""
              ></el-input>
            </div>
            <div class="asset-list-container mT10">
              <spinner
                v-if="config.moduleFieldsPanel.loading"
                :show="config.moduleFieldsPanel.loading"
              />
              <div
                class="asset-list-block-txt text-center"
                v-else-if="
                  !config.moduleFieldsPanel.data ||
                    !config.moduleFieldsPanel.data.length
                "
              >
                No points found.
              </div>
              <el-checkbox-group
                v-model="config.mainPanel.selectedDataPoints"
                class="building-check-list"
                v-else-if="!isSinglePointSelector"
              >
                <el-checkbox
                  v-for="(item, index) in filteredModuleFieldsPanel"
                  :key="index"
                  :label="null + '_' + item.id"
                  >{{
                    item.displayName ? item.displayName : item.name
                  }}</el-checkbox
                >
              </el-checkbox-group>
              <div
                class="asset-list-block-txt"
                v-else
                v-for="(item, index) in filteredModuleFieldsPanel"
                :key="index"
                @click="selectDataPoint(null + '_' + item.id), apply()"
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
        <!-- module fields panel end  -->

        <!-- mv points panel start -->
        <div
          class="assets-points-Scontainer assets-points-Scontainer-lalign"
          v-if="
            (config.mvPointsPanel.show || mode === 'mv') &&
              ![6, 7, 8].includes(mode)
          "
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
                v-else-if="!isSinglePointSelector"
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
              <div
                class="asset-list-block-txt"
                v-else
                v-for="(item, index) in filteredmvPointsPanel"
                :key="index"
                @click="
                  selectDataPoint(
                    config.mainPanel.selectedAssetCategory + '_' + item.id
                  ),
                    apply()
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
        <!-- mv points panel end -->

        <!-- weather stations panel start -->
        <template v-if="newSiteSummary">
          <div
            class="assets-points-Scontainer assets-points-Scontainer-lalign"
            v-if="config.weatherStationsPanel.show"
          >
            <div class="assets-points-inner-container height100">
              <div class="select-reading-header">
                <span>
                  Weather Stations
                </span>
              </div>

              <div class="building-points-search">
                <el-input
                  placeholder="Filter points"
                  v-model="config.weatherStationsPanel.filterQuery"
                  autofocus
                  class=""
                >
                </el-input>
              </div>
              <div class="asset-list-container mT10 overflow-scroll">
                <spinner
                  v-if="config.weatherStationsPanel.loading"
                  :show="config.weatherStationsPanel.loading"
                />
                <div
                  class="asset-list-block-txt text-center"
                  v-else-if="
                    !config.weatherStationsPanel.data ||
                      !config.weatherStationsPanel.data.length
                  "
                >
                  No stations found.
                </div>
                <div
                  class="asset-list-block-txt"
                  v-else
                  v-for="(item, index) in getFilteredData(
                    'weatherStationsPanel'
                  )"
                  :key="index"
                  @click="openWeatherReadings(item.id)"
                  :class="{
                    'asset-list-block-txt-active':
                      parseInt(
                        config.weatherStationsPanel.selectedStationId
                      ) === parseInt(item.id),
                  }"
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
        </template>
        <!-- weather stations panel end -->
        <!-- sidebar2 end -->

        <!-- sidebar3 start -->
        <!-- select weather readings panel start -->
        <div
          class="assets-points-Scontainer assets-points-Scontainer-shadow"
          v-if="config.weatherReadingsPanel.show"
        >
          <div
            class="assets-points-inner-container readings-list-container height100"
          >
            <div class="select-reading-header">
              {{ 'Select Readings' }}
            </div>
            <div class="building-points-search">
              <el-input
                placeholder="Filter readings"
                v-model="config.weatherReadingsPanel.filterQuery"
                autofocus
                class=""
              ></el-input>
            </div>
            <div class="mT20 readings-scroll">
              <spinner
                v-if="config.weatherReadingsPanel.loading"
                :show="config.weatherReadingsPanel.loading"
              ></spinner>
              <div
                class="text-center"
                v-else-if="
                  !config.weatherReadingsPanel.data ||
                    !config.weatherReadingsPanel.data.length
                "
              >
                No data found.
              </div>
              <el-checkbox-group
                v-model="config.mainPanel.selectedDataPoints"
                class="building-check-list"
                v-else
              >
                <el-checkbox
                  v-for="(item, index) in getFilteredData(
                    'weatherReadingsPanel'
                  )"
                  :key="index"
                  :label="config.weatherReadingsPanel.parentId + '_' + item.id"
                  >{{
                    item.displayName ? item.displayName : item.name
                  }}</el-checkbox
                >
              </el-checkbox-group>
            </div>
          </div>
        </div>
        <!-- select weather readings panel end -->
        <!-- sidebar3 end -->

        <div
          class="building-analysis-point-container row "
          v-if="
            (config.assetPointsPanel.show ||
              config.mainPanel.selectedDataPoints.length) &&
              !isSinglePointSelector
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
                          getDataPointName(dataPoint)
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
    'isSinglePointSelector',
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
        weatherStationsPanel: {
          show: false,
          data: null,
          loading: false,
          selectedStationId: null,
          filterQuery: '',
        },
        weatherReadingsPanel: {
          show: false,
          data: null,
          loading: false,
          parentId: null,
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
        moduleFieldsPanel: {
          show: false,
          data: null,
          loading: false,
          parentId: null,
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
      moduleMap: {
        6: 'Site',
        7: 'Building',
        8: 'Asset',
      },
      allowedModuleFields: {
        site: [
          'area',
          'boundaryRadius',
          'cddBaseTemperature',
          'grossFloorArea',
          'hddBaseTemperature',
          'maxOccupancy',
          'noOfBuildings',
          'wddBaseTemperature',
        ],
        building: ['area', 'grossFloorArea', 'maxOccupancy', 'noOfFloors'],
        asset: ['boundaryRadius', 'distanceMoved', 'unitPrice'],
      },
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
    newSiteSummary() {
      return (
        this.$helpers.isLicenseEnabled('NEW_SITE_SUMMARY') &&
        this.$helpers.isLicenseEnabled('WEATHER_INTEGRATION')
      )
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
    filteredModuleFieldsPanel() {
      let self = this
      if (
        this.config.moduleFieldsPanel.data &&
        this.config.moduleFieldsPanel.data.length
      ) {
        if (
          this.config.moduleFieldsPanel.filterQuery &&
          this.config.moduleFieldsPanel.filterQuery.trim().length
        ) {
          let filteredData = this.config.moduleFieldsPanel.data.filter(function(
            d
          ) {
            let name = d.displayName ? d.displayName : d.name
            if (
              name
                .toLowerCase()
                .indexOf(
                  self.config.moduleFieldsPanel.filterQuery.toLowerCase()
                ) >= 0
            ) {
              if (d.dataTypeEnum !== 'BOOLEAN') {
                return d
              }
            }
          })
          return this.$helpers.sortData('name', filteredData)
        } else {
          let filteredData = this.config.moduleFieldsPanel.data.filter(function(
            d
          ) {
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
    mode: function(newVal) {
      if (newVal === 'mv') {
        this.loadMVProjects()
      } else {
        if ([6, 7, 8].includes(newVal)) {
          this.loadModuleFields()
        }
        this.config.mainPanel.currentModule = 'assets'
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
    if (this.mode && [6, 7, 8].includes(this.mode)) {
      this.loadModuleFields()
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
    quickSearch() {},
    selectDataPoint(datapoint) {
      this.config.mainPanel.selectedDataPoints[0] = datapoint
    },
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
          buildingIds: [],
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
          if (self.config['moduleFieldsPanel'].show) {
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
      self.config.mainPanel.selectedAssetCategory = null
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
      if (self.config['moduleFieldsPanel'].show) {
        panels.push('moduleFieldsPanel')
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

    loadModuleFields() {
      let moduleName = null
      switch (this.mode) {
        case 6:
          moduleName = 'site'
          break
        case 7:
          moduleName = 'building'
          break
        case 8:
          moduleName = 'asset'
          break
        default:
          moduleName = 'asset'
          break
      }
      this.$http.get('/module/meta?moduleName=' + moduleName).then(response => {
        this.config.moduleFieldsPanel.data = []
        for (let fieldObj of response.data.meta.fields) {
          if (
            [2, 3].includes(fieldObj.dataType) &&
            (this.allowedModuleFields[moduleName].includes(fieldObj.name) ||
              !fieldObj.default)
          ) {
            this.config.moduleFieldsPanel.data.push(fieldObj)
          }
        }
      })
    },
    openModuleFieldsPanel() {
      this.config.mainPanel.selectedAssetCategory = null
      this.config['assetPointsPanel'].show = false
      this.config['selectReadingsPanel'].show = false
      this.config['selectAssetsPanel'].show = false
      this.config['mvPointsPanel'].show = false
      if (this.config['moduleFieldsPanel'].show) {
        this.config['moduleFieldsPanel'].show = false
      } else {
        this.config['moduleFieldsPanel'].show = true
      }
    },

    getDataPointName(dataPoint) {
      if (this.readings.fields[dataPoint.split('_')[1]]) {
        return this.readings.fields[dataPoint.split('_')[1]].displayName
      } else {
        if (
          this.config.moduleFieldsPanel.data &&
          this.config.moduleFieldsPanel.data.length > 0
        ) {
          let ModuleFieldPoint = this.config.moduleFieldsPanel.data.find(
            dp => dp.fieldId === parseInt(dataPoint.split('_')[1])
          )
          if (ModuleFieldPoint) {
            return ModuleFieldPoint.displayName
          }
          return '---'
        } else if (
          this.config.mvPointsPanel.data &&
          this.config.mvPointsPanel.data.length > 0
        ) {
          let mvPoint = this.config.mvPointsPanel.data.find(
            dp => dp.fieldId === parseInt(dataPoint.split('_')[1])
          )
          if (mvPoint) {
            return mvPoint.displayName
          }
          return '---'
        } else if (
          this.config.weatherReadingsPanel.data &&
          this.config.weatherReadingsPanel.data.length > 0
        ) {
          let weatherReadingPoint = this.config.weatherReadingsPanel.data.find(
            dp => dp.fieldId === parseInt(dataPoint.split('_')[1])
          )
          if (weatherReadingPoint) {
            return weatherReadingPoint.displayName
          }
          return '---'
        } else {
          return '---'
        }
      }
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
        self.config.mainPanel.selectedDataPoints.length > 0
      ) {
        this.isMultipleCategorySelected = true
      } else {
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
            self.$http
              .post('/asset/getReadingsForSpecificAssetCategory', {
                buildingIds: [],
                categoryIds: [categoryId],
              })
              .then(function(response) {
                if (!self.readings.categories.includes(categoryId)) {
                  self.readings.categories.push(categoryId)
                }
                if (response.data.categoryWithFields[categoryId]) {
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
      let currentModule = this.$getProperty(
        this,
        'config.mainPanel.currentModule',
        ''
      )
      if (this.config.moduleFieldsPanel.show) {
        this.config.moduleFieldsPanel.show = false
      }
      if (
        this.config.mainPanel.selectedDataPoints &&
        this.config.mainPanel.selectedDataPoints.length
      ) {
        for (let dp of this.config.mainPanel.selectedDataPoints) {
          let categoryId = dp.split('_')[0] ? parseInt(dp.split('_')[0]) : null
          let pointId = parseInt(dp.split('_')[1])
          let moduleName = null
          let aggr = null

          if (
            (this.mode === 'mv' || !categoryId) &&
            ![6, 7, 8].includes(this.mode)
          ) {
            moduleName = 'mvproject'
            aggr = 3
          }

          let point = {
            categoryId: categoryId,
            name: this.getDataPointName(dp),
            parentId: null,
            moduleName: moduleName,
            yAxis: {
              fieldId: pointId,
              aggr:
                this.readings.fields[pointId] &&
                this.readings.fields[pointId].unit &&
                ['currency', 'kwh', 'co2', 'kg', 'mwh'].includes(
                  this.readings.fields[pointId].unit.trim().toLowerCase()
                )
                  ? 3
                  : aggr
                  ? aggr
                  : 2,
            },
            duplicateDataPoint: false,
          }

          if (currentModule == 'weather') {
            point.parentId = categoryId
            if (
              this.config.weatherStationsPanel.data &&
              this.config.weatherStationsPanel.data.length > 0
            ) {
              let weatherStation = this.config.weatherStationsPanel.data.find(
                station => station.id === point.parentId
              )
              if (weatherStation) {
                point.name = `${weatherStation.name} ( ${this.getDataPointName(
                  dp
                )} )`
              }
            }
          }

          if ([6, 7, 8].includes(this.mode)) {
            if (!this.readings.fields[pointId]) {
              point['type'] = 4
              point.categoryId = null
              point.yAxis.aggr = 0
              point.moduleName = this.moduleMap[this.mode].toLowerCase()
            }
          }

          dataPoints.push(point)
        }
      }
      if (this.isMultipleCategorySelected) {
        this.isMultipleCategorySelected = false
      }
      this.$emit('updateDataPoints', dataPoints)
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
            if (this.$route.query.reportId && dp.categoryId) {
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

    async loadWeatherStationsAndFields() {
      this.config.weatherStationsPanel.loading = true
      this.config.mainPanel.currentModule = 'weather'
      this.togglePanel('weatherStationsPanel')
      let { data, error } = await API.get('v3/weather/allWeatherFields')
      if (error) {
        this.config.weatherStationsPanel.data = []
        this.config.weatherReadingsPanel.data = []
      } else {
        let { stationMap = [], fields = [] } = data
        this.config.weatherStationsPanel.data = stationMap
        this.config.weatherReadingsPanel.data = fields
      }
      this.config.weatherStationsPanel.loading = false
    },

    async openWeatherReadings(stationId) {
      this.config.weatherStationsPanel.selectedStationId = stationId
      this.togglePanel('weatherStationsPanel', 'weatherReadingsPanel')
      this.config.weatherReadingsPanel.parentId = stationId
    },

    getFilteredData(panel) {
      if (this.config[panel].data && this.config[panel].data.length) {
        if (
          this.config[panel].filterQuery &&
          this.config[panel].filterQuery.trim().length
        ) {
          let filteredData = this.config[panel].data.filter(d => {
            let name = d.displayName ? d.displayName : d.name
            if (
              name
                .toLowerCase()
                .indexOf(this.config[panel].filterQuery.toLowerCase()) >= 0
            ) {
              return d
            }
            return false
          })
          return filteredData
        } else {
          return this.config[panel].data || []
        }
      }
      return []
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
.data-pointer-selector-popup {
  padding-left: 14px;
  border-bottom: 1px solid #e0e0e0;
}
</style>
