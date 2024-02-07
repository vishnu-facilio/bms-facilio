<template>
  <div>
    <div v-if="visibility" class="analytics-sidebar-bg">
      <!-- info band -->
      <div
        v-if="applyDisable && templateFilterToggle"
        class="analytics-topbar-warning"
      >
        <div class="Only-one-data-point flex-middle">
          <InlineSvg
            src="svgs/info-warning"
            iconClass="icon vertical-middle icon-md fill-orange"
          ></InlineSvg>
          <div class="pL10">
            Only one datapoint allowed for customization. If you want to select
            other datapoints please
          </div>
          <div class="text-style-1 pL5">Disable the customization.</div>
          <div class="mL10">
            <el-switch
              v-model="templateFilterToggle"
              @change="updateTemplateFilterToggle"
              class="Notification-toggle"
              active-color="rgba(57, 178, 194, 0.8)"
              inactive-color="#e5e5e5"
            ></el-switch>
          </div>
        </div>
      </div>
      <div
        class="sidebar-position-setup"
        :style="{
          top:
            applyDisable === true && templateFilterToggle === true
              ? '45px'
              : '0px',
        }"
      >
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
          <!-- Sidebar1 start -->
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
                          config.mainPanel.selectedAssetCategory ===
                          category.id,
                      },
                    ]"
                    v-if="
                      config.mainPanel.assetCategories &&
                        config.mainPanel.assetCategories.length
                    "
                    v-for="(category, index) in config.mainPanel
                      .assetCategories"
                    :key="index"
                    @click="openAssetCategory(category.id)"
                  >
                    <i
                      v-bind:class="{
                        'fa fa-circle': true,
                        'dot-blue-icon':
                          config.mainPanel.selectedAssetCategory ===
                          category.id,
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
              <!-- //kpi -->

              <div class="building-analysis-side-block">
                <div class="building-hover-actions">
                  <div
                    class="sidebar-asset-H fL"
                    @click="loadKPIAssetCategories"
                    style="cursor : pointer;"
                  >
                    KPI'S
                  </div>
                </div>
                <div
                  class="building-anlysis-active-block"
                  v-if="config.mainPanel.currentModule === 'kpis'"
                >
                  <div
                    v-if="
                      config.mainPanel.kpiAssetCategories &&
                        config.mainPanel.kpiAssetCategories.length
                    "
                  >
                    <div
                      :class="[
                        'building-anlysis-label-txt',
                        'clearboth',
                        {
                          'asset-list-block-txt-active':
                            config.mainPanel.selectedKPICategory ===
                            category.id,
                        },
                      ]"
                      v-for="(category, index) in config.mainPanel
                        .kpiAssetCategories"
                      :key="index"
                      @click="openKPIAssetCategory(category.id)"
                    >
                      <i
                        v-bind:class="{
                          'fa fa-circle': true,
                          'dot-blue-icon':
                            config.mainPanel.selectedKPICategory ===
                            category.id,
                          'dot-grey-icon':
                            config.mainPanel.selectedKPICategory != category.id,
                        }"
                        aria-hidden="true"
                      ></i>
                      <div class="width80">{{ category.displayName }}</div>
                    </div>
                  </div>
                  <spinner
                    v-if="config.mainPanel.kpiAssetCategoriesLoading"
                    :show="config.mainPanel.kpiAssetCategoriesLoading"
                    class="clearboth"
                  ></spinner>
                  <div
                    v-else-if="
                      config.mainPanel.kpiAssetCategories &&
                        !config.mainPanel.kpiAssetCategories.length
                    "
                    class="building-anlysis-label-txt clearboth"
                  >
                    -- No KPIS --
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

              <div class="building-analysis-side-block">
                <div class="building-hover-actions">
                  <div
                    class="sidebar-asset-H fL"
                    @click="loadRuleAssetCategories"
                    style="cursor : pointer;"
                  >
                    Rules
                  </div>
                </div>
                <div
                  class="building-anlysis-active-block"
                  v-if="config.mainPanel.currentModule === 'rules'"
                >
                  <div
                    :class="[
                      'building-anlysis-label-txt',
                      'clearboth',
                      {
                        'asset-list-block-txt-active':
                          config.mainPanel.selectedRuleAssetCategory ===
                          category.id,
                      },
                    ]"
                    v-if="
                      config.mainPanel.ruleAssetCategories &&
                        config.mainPanel.ruleAssetCategories.length
                    "
                    v-for="(category, index) in config.mainPanel
                      .ruleAssetCategories"
                    :key="index"
                    @click="openRuleAssetCategory(category.id)"
                  >
                    <i
                      v-bind:class="{
                        'fa fa-circle': true,
                        'dot-blue-icon':
                          config.mainPanel.selectedRuleAssetCategory ===
                          category.id,
                        'dot-grey-icon':
                          config.mainPanel.selectedRuleAssetCategory !=
                          category.id,
                      }"
                      aria-hidden="true"
                    ></i>
                    <div class="width80">{{ category.displayName }}</div>
                  </div>
                  <spinner
                    v-if="config.mainPanel.ruleAssetCategoriesLoading"
                    :show="config.mainPanel.ruleAssetCategoriesLoading"
                    class="clearboth"
                  ></spinner>
                  <div
                    v-else-if="!config.mainPanel.ruleAssetCategories"
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
                    @click="loadMVProjects"
                    style="cursor : pointer;"
                  >
                    M & V
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- Sidebar1 end -->

          <!-- sidebar2 start -->
          <!-- points and assets panel start -->
          <div
            class="assets-points-Scontainer assets-points-Scontainer-lalign"
            v-if="config.assetPointsPanel.show"
          >
            <div class="assets-points-inner-container">
              <el-tabs
                v-model="config.assetPointsPanel.activeSubTab"
                @tab-click="
                  config.mainPanel.currentModule === 'rules'
                    ? openRuleAssetCategory(
                        config.mainPanel.selectedRuleAssetCategory
                      )
                    : openAssetCategory(config.mainPanel.selectedAssetCategory)
                "
              >
                <el-tab-pane label="Points" name="points">
                  <span slot="label"
                    ><img
                      src="~assets/gauge.svg"
                      width="18px"
                      height="18px"
                      style="position: relative;top: 4px;right: 7px;"
                    />{{
                      config.mainPanel.currentModule === 'rules'
                        ? 'Alarms'
                        : 'Points'
                    }}</span
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
                      v-for="(item, index) in sortedAssetPointsPanel"
                      :key="index"
                      @click="openPoint(item.id)"
                      :class="{
                        'asset-list-block-txt-active':
                          parseInt(
                            config.assetPointsPanel.selectedAssetOrPoint
                          ) === parseInt(item.id),
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
                      v-for="(item, index) in sortedAssetPointsPanel"
                      :key="index"
                      @click="openAsset(item.id)"
                      :class="{
                        'asset-list-block-txt-active':
                          parseInt(
                            config.assetPointsPanel.selectedAssetOrPoint
                          ) === parseInt(item.id),
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
          <div
            class="assets-points-Scontainer assets-points-Scontainer-lalign"
            v-if="config.kpiPointsPanel.show"
          >
            <div class="assets-points-inner-container">
              <div
                class="select-reading-header"
                style="padding-bottom: 17px; padding-top: 16px !important;"
              >
                <span slot="label"
                  ><img
                    src="~assets/gauge.svg"
                    width="18px"
                    height="18px"
                    style="position: relative;top: 4px;right: 7px;"
                  />{{ "KPI'S" }}</span
                >
              </div>
              <div class="building-points-search">
                <el-input
                  placeholder="Filter Kpis"
                  v-model="config.kpiPointsPanel.filterQuery"
                  autofocus
                  class=""
                ></el-input>
              </div>
              <div class="asset-list-container mT10">
                <spinner
                  v-if="config.kpiPointsPanel.loading"
                  :show="config.kpiPointsPanel.loading"
                />
                <div
                  class="asset-list-block-txt text-center"
                  v-else-if="
                    !config.kpiPointsPanel.data ||
                      !config.kpiPointsPanel.data.length
                  "
                >
                  No Kpi's found.
                </div>
                <div
                  class="asset-list-block-txt"
                  v-else
                  v-for="(item, index) in filterKpisPanel"
                  :key="index"
                  @click="openKpiTypePoint(item)"
                  :class="{
                    'asset-list-block-txt-active':
                      parseInt(config.kpiPointsPanel.selectedKpi) ===
                      parseInt(item.id),
                  }"
                >
                  <div class="asset-active-select-item">
                    {{ item.displayName ? item.displayName : item.name }}
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- points and assets panel end -->
          <!-- points and mvs panel start-->
          <div
            class="assets-points-Scontainer assets-points-Scontainer-lalign"
            v-if="config.mvPointsPanel.show"
          >
            <div class="assets-points-inner-container">
              <el-tabs
                v-model="config.mvPointsPanel.activeSubTab"
                @tab-click="
                  openmvproject(config.mainPanel.selectedAssetCategory)
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
                      No points found.
                    </div>

                    <div
                      class="asset-list-block-txt"
                      v-else
                      v-for="(item, index) in filteredMvPointsPanel"
                      :key="index"
                      @click="openMvPoint(item.id)"
                      :class="{
                        'asset-list-block-txt-active':
                          config.mvPointsPanel.selectedMvOrPoint === item.id,
                      }"
                    >
                      <div class="asset-active-select-item">
                        {{ item.displayName ? item.displayName : item.name }}
                      </div>
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="M&V" name="mvs">
                  <span slot="label"
                    ><img
                      src="~assets/one-box-black.svg"
                      width="16px"
                      height="16px"
                      style="position: relative;top: 4px;right: 8px;"
                    />M&V</span
                  >
                  <div class="building-points-search">
                    <el-input
                      placeholder="Filter M&Vs"
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
                      No M&Vs.
                    </div>

                    <div
                      class="asset-list-block-txt"
                      v-else
                      v-for="(item, index) in filteredMvPointsPanel"
                      :key="index"
                      @click="openMv(item.id)"
                      :class="{
                        'asset-list-block-txt-active':
                          config.mvPointsPanel.selectedMvOrPoint === item.id,
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
          <!-- points and mvs panel end-->
          <!-- sidebar2 end -->

          <!-- sidebar3 start -->
          <!-- select readings panel start -->
          <div
            class="assets-points-Scontainer assets-points-Scontainer-shadow"
            v-if="config.selectReadingsPanel.show"
          >
            <div>
              <div
                class="assets-points-inner-container readings-list-container"
              >
                <div class="select-reading-header">
                  <div class="select-reading-H">
                    {{
                      config.mainPanel.currentModule === 'rules'
                        ? 'SELECT ALARMS'
                        : 'SELECT READINGS'
                    }}
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
                <div class="mT20 readings-scroll">
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
                  <el-checkbox-group
                    @change="addToBuildingMap"
                    v-model="config.mainPanel.selectedDataPoints"
                    class="building-check-list"
                    v-else
                  >
                    <el-checkbox
                      v-for="(item, index) in sorteddReadingsPanel"
                      :key="index"
                      :label="
                        item.id + '_' + config.selectReadingsPanel.parentId
                      "
                      >{{
                        item.displayName ? item.displayName : item.name
                      }}</el-checkbox
                    >
                  </el-checkbox-group>
                </div>
              </div>
            </div>
          </div>
          <!-- select readings panel end -->

          <!-- select assets panel start -->
          <div
            class="assets-points-Scontainer assets-points-Scontainer-shadow"
            v-if="config.selectAssetsPanel.show"
          >
            <div>
              <div
                class="assets-points-inner-container readings-list-container"
              >
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
                    v-else-if="
                      !config.selectAssetsPanel.data ||
                        !config.selectAssetsPanel.data.length
                    "
                  >
                    No data found.
                  </div>
                  <el-checkbox-group
                    @change="checkForRangePoints"
                    v-model="config.mainPanel.selectedDataPoints"
                    class="building-check-list asset-checklist"
                    v-else
                  >
                    <el-checkbox
                      v-for="(item, index) in sortedAssetsPanel"
                      :key="index"
                      :label="
                        config.selectAssetsPanel.readingId + '_' + item.id
                      "
                      >{{
                        item.displayName ? item.displayName : item.name
                      }}</el-checkbox
                    >
                  </el-checkbox-group>
                </div>
              </div>
            </div>
          </div>
          <!-- select assets panel end -->

          <!-- select kpi panel start-->
          <div
            class="assets-points-Scontainer assets-points-Scontainer-shadow"
            v-if="config.selectKPIPanel.show"
          >
            <div>
              <div
                class="assets-points-inner-container readings-list-container"
              >
                <div class="select-reading-header">
                  <div class="select-reading-H">
                    SELECT ASSETS
                  </div>
                </div>
                <div class="building-points-search building-points-height">
                  <el-input
                    placeholder="Filter assets"
                    v-model="config.selectKPIPanel.filterQuery"
                    autofocus
                    class=""
                  ></el-input>
                </div>
                <div class="asset-list-container mT10">
                  <spinner
                    v-if="config.selectKPIPanel.loading"
                    :show="config.selectKPIPanel.loading"
                  ></spinner>
                  <div
                    class="text-center"
                    v-else-if="
                      !config.selectKPIPanel.data ||
                        !config.selectKPIPanel.data.length
                    "
                  >
                    No data found.
                  </div>
                  <el-checkbox-group
                    @change="checkForRangePoints"
                    v-model="config.mainPanel.selectedDataPoints"
                    class="building-check-list asset-checklist"
                    v-else
                  >
                    <el-checkbox
                      v-for="(item, index) in sortedKpiAssetsPanel"
                      :key="index"
                      :label="
                        config.selectKPIPanel.readingId + '_' + item.id + '_kpi'
                      "
                      >{{
                        item.displayName ? item.displayName : item.name
                      }}</el-checkbox
                    >
                  </el-checkbox-group>
                </div>
              </div>
            </div>
          </div>
          <!-- select kpi panel end-->

          <!-- select mv readings panel start -->
          <div
            class="assets-points-Scontainer assets-points-Scontainer-shadow"
            v-if="config.selectMvReadingsPanel.show"
          >
            <div>
              <div
                class="assets-points-inner-container readings-list-container"
              >
                <div class="select-reading-header">
                  <div class="select-reading-H">
                    SELECT READINGS
                  </div>
                </div>
                <div class="building-points-search building-points-height">
                  <el-input
                    placeholder="Filter readings"
                    v-model="config.selectMvReadingsPanel.filterQuery"
                    autofocus
                    class=""
                  ></el-input>
                </div>
                <div class="mT20 readings-scroll">
                  <spinner
                    v-if="config.selectMvReadingsPanel.loading"
                    :show="config.selectMvReadingsPanel.loading"
                  ></spinner>
                  <div
                    class="text-center"
                    v-else-if="
                      !config.selectMvReadingsPanel.data ||
                        !config.selectMvReadingsPanel.data.length
                    "
                  >
                    No data found.
                  </div>
                  <el-checkbox-group
                    @change="addToBuildingMap"
                    v-model="config.mainPanel.selectedDataPoints"
                    class="building-check-list"
                    v-else
                  >
                    <el-checkbox
                      v-for="(item, index) in filteredMvReadingsPanel"
                      :key="index"
                      :label="
                        item.id + '_' + config.selectMvReadingsPanel.parentId
                      "
                      >{{
                        item.displayName ? item.displayName : item.name
                      }}</el-checkbox
                    >
                  </el-checkbox-group>
                </div>
              </div>
            </div>
          </div>
          <!-- select mv readings panel end -->

          <!-- select mvs panel start -->
          <div
            class="assets-points-Scontainer assets-points-Scontainer-shadow"
            v-if="config.selectMvsPanel.show"
          >
            <div>
              <div
                class="assets-points-inner-container readings-list-container"
              >
                <div class="select-reading-header">
                  <div class="select-reading-H">
                    SELECT M&Vs
                  </div>
                </div>
                <div class="building-points-search building-points-height">
                  <el-input
                    placeholder="Filter M&Vs"
                    v-model="config.selectMvsPanel.filterQuery"
                    autofocus
                    class=""
                  ></el-input>
                </div>
                <div class="asset-list-container mT10">
                  <spinner
                    v-if="config.selectMvsPanel.loading"
                    :show="config.selectMvsPanel.loading"
                  ></spinner>
                  <div
                    v-else-if="
                      !config.selectMvsPanel.data ||
                        !config.selectMvsPanel.data.length
                    "
                  >
                    No data found.
                  </div>
                  <el-checkbox-group
                    @change="checkForRangePoints"
                    v-model="config.mainPanel.selectedDataPoints"
                    class="building-check-list asset-checklist"
                    v-else
                  >
                    <el-checkbox
                      v-for="(item, index) in filteredmvsPanel"
                      :key="index"
                      :label="config.selectMvsPanel.readingId + '_' + item.id"
                      >{{
                        item.displayName ? item.displayName : item.name
                      }}</el-checkbox
                    >
                  </el-checkbox-group>
                </div>
              </div>
            </div>
          </div>
          <!-- select mvs panel end -->
          <!-- sidebar3 end -->

          <!-- sidebar4 start -->
          <div
            v-if="showFourthPanel"
            class="assets-points-Scontainer assets-points-Scontainer-shadow overflow-y"
          >
            <div class="height100 overflow-y-scroll pB200">
              <div
                class="fc-text-pink11 text-uppercase pT23 pB20 pL20 mB10 position-sticky top0 z-index1 white-bg"
              >
                SELECTED DATA POINTS
                <span class="fc-grey-text">{{ selectedDataPoints }}</span>
              </div>
              <div class="analtics-point-selector4 mB30">
                <div
                  v-for="(readingGroup, readingGroupIndex) in summaryList"
                  :key="readingGroupIndex"
                  class="mB20"
                >
                  <div class="sidebar-asset-H pL20 mB30">
                    {{ readingGroup.label }}
                  </div>
                  <el-row
                    v-for="(readings, readingIndex) in readingGroup.readings"
                    :key="readingIndex"
                    class="pL20 pR20 analtics-point-selector4-row"
                  >
                    <el-col :span="1">
                      <div class="dot-dark-grey5 mT8"></div>
                    </el-col>
                    <el-col :span="21" class="mL5">
                      <div class="label-txt-black line-height20">
                        {{ readings.readingLabel }}
                      </div>
                      <div class="fc-grey2-text12">
                        {{
                          readings.resourceLabel ? readings.resourceLabel : ''
                        }}
                      </div>
                      <div class="fc-grey2-text12">
                        {{
                          moreThanOneLocation
                            ? readings.location
                              ? readings.location
                              : ''
                            : ''
                        }}
                      </div>
                    </el-col>
                    <el-col :span="1">
                      <i
                        @click="
                          removeDataPointFromGroup(readings, readingGroupIndex)
                        "
                        class="el-icon-close analtics-point-selector4-close"
                      ></i>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </div>
            <!-- bottom sections -->
            <div
              class="building-analysis-point-container row"
              v-if="
                config.mainPanel.selectedDataPoints &&
                  config.mainPanel.selectedDataPoints.length
              "
              :style="{
                bottom:
                  applyDisable === true && templateFilterToggle === true
                    ? '160px'
                    : '100px',
              }"
            >
              <div
                class="building-analysis-points-wrap flex-middle flex-direction-column col-12"
              >
                <div></div>
                <div class="building-analysis-points-btn flex-middle col-4">
                  <el-button
                    type="button"
                    class="btn-grey-fill pL30 pR30"
                    @click="cancel"
                    >{{ $t('setup.users_management.cancel') }}</el-button
                  >
                  <el-button
                    type="button"
                    class="btn-blue-fill pL30 pR30 mL15"
                    @click="apply"
                    >{{ $t('maintenance.pm_list.apply') }}</el-button
                  >
                </div>
              </div>
            </div>
          </div>
          <!-- sidebar4 end -->
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
  </div>
</template>

<script>
import AnalayticsFilter from 'pages/energy/analytics/components/AnalayticsFilter'

import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import { getFieldValue } from 'util/picklist'
import cloneDeep from 'lodash/cloneDeep'
export default {
  props: [
    'visibility',
    'zones',
    'isSiteAnalysis',
    'savedPoints',
    'reportObject',
    'templateFilterToggle',
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
          ruleAssetCategories: null,
          ruleAssetCategoriesLoading: null,
          selectedRuleAssetCategory: null,
          selectedKPICategory: null,
          kpiAssetCategories: null,
          kpiAssetCategoriesLoading: false,
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
        mvPointsPanel: {
          show: false,
          activeSubTab: 'mvs',
          data: null,
          loading: true,
          selectedMvOrPoint: null,
          filterQuery: '',
        },
        selectMvReadingsPanel: {
          show: false,
          data: null,
          loading: false,
          parentId: null,
          filterQuery: '',
        },
        selectMvsPanel: {
          show: false,
          data: null,
          loading: false,
          readingId: null,
          filterQuery: '',
        },
        kpiPointsPanel: {
          show: false,
          activeSubTab: 'kpis',
          data: null,
          loading: true,
          selectedKpi: null,
          filterQuery: '',
        },
        selectKPIPanel: {
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
      loading: true,
      readings: {
        categories: [],
        assets: {},
        categoryWithAssets: {},
        fields: {},
        categoryWithFields: {},
      },
      mvprojects: null,
      mvreadings: [],
      mvAssetMap: null,
      mvprojectNameMap: null,
      showQuickSearch: false,
      quickSearchQuery: null,
      selectedBuildings: [],
      newWeatherModule: {
        displayName: 'New Weather',
        moduleName: 'newWeather',
        selected: false,
      },
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
        rules: [],
        kpis: [],
      },
      currentKey: 0,
      siteId: null,
      applyDisable: false,
      rulereadings: {
        categories: [],
        assets: {},
        categoryWithAssets: {},
        fields: {},
        categoryWithFields: {},
      },
      kpireadings: {
        categories: [],
        assets: {},
        categoryWithAssets: {},
        fields: {},
        categoryWithFields: {},
      },
      field_vs_Kpi_map: {},
      id_vs_asset: {},
      hideUserFilter: false,
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
    newSiteSummary() {
      return (
        this.$helpers.isLicenseEnabled('NEW_SITE_SUMMARY') &&
        this.$helpers.isLicenseEnabled('WEATHER_INTEGRATION')
      )
    },
    selectedDataPoints() {
      if (this.config.mainPanel.selectedDataPoints.length < 10)
        return '0' + this.config.mainPanel.selectedDataPoints.length
      else return this.config.mainPanel.selectedDataPoints.length
    },

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
              return d
            }
          })
        } else {
          return this.config.assetPointsPanel.data
        }
      }
      return []
    },
    filteredMvPointsPanel() {
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
              return d
            }
          })
          return this.$helpers.sortData('name', filteredData)
        } else {
          return this.$helpers.sortData('name', this.config.mvPointsPanel.data)
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
    filteredMvReadingsPanel() {
      let self = this
      if (
        this.config.selectMvReadingsPanel.data &&
        this.config.selectMvReadingsPanel.data.length
      ) {
        if (
          this.config.selectMvReadingsPanel.filterQuery &&
          this.config.selectMvReadingsPanel.filterQuery.trim().length
        ) {
          let filteredData = this.config.selectMvReadingsPanel.data.filter(
            function(d) {
              let name = d.displayName ? d.displayName : d.name
              if (
                name
                  .toLowerCase()
                  .indexOf(
                    self.config.selectMvReadingsPanel.filterQuery.toLowerCase()
                  ) >= 0
              ) {
                return d
              }
            }
          )
          return this.$helpers.sortData('name', filteredData)
        } else {
          return this.$helpers.sortData(
            'name',
            this.config.selectMvReadingsPanel.data
          )
        }
      }
      return []
    },
    filteredmvsPanel() {
      let self = this
      if (
        this.config.selectMvsPanel.data &&
        this.config.selectMvsPanel.data.length
      ) {
        if (
          this.config.selectMvsPanel.filterQuery &&
          this.config.selectMvsPanel.filterQuery.trim().length
        ) {
          let filteredData = this.config.selectMvsPanel.data.filter(function(
            d
          ) {
            let name = d.displayName ? d.displayName : d.name
            if (
              name
                .toLowerCase()
                .indexOf(
                  self.config.selectMvsPanel.filterQuery.toLowerCase()
                ) >= 0
            ) {
              return d
            }
          })
          return this.$helpers.sortData('name', filteredData)
        } else {
          return this.$helpers.sortData('name', this.config.selectMvsPanel.data)
        }
      }
      return []
    },
    filterKpisPanel() {
      let self = this
      if (
        this.config.kpiPointsPanel.data &&
        this.config.kpiPointsPanel.data.length
      ) {
        if (
          this.config.kpiPointsPanel.filterQuery &&
          this.config.kpiPointsPanel.filterQuery.trim().length
        ) {
          return this.config.kpiPointsPanel.data.filter(function(d) {
            let name = d.displayName ? d.displayName : d.name
            if (
              name
                .toLowerCase()
                .indexOf(
                  self.config.kpiPointsPanel.filterQuery.toLowerCase()
                ) >= 0
            ) {
              return d
            }
          })
        } else {
          return this.config.kpiPointsPanel.data
        }
      }
      return []
    },
    filteredKpiAssetsPanel() {
      let self = this
      if (
        this.config.selectKPIPanel.data &&
        this.config.selectKPIPanel.data.length
      ) {
        if (
          this.config.selectKPIPanel.filterQuery &&
          this.config.selectKPIPanel.filterQuery.trim().length
        ) {
          return this.config.selectKPIPanel.data.filter(function(d) {
            let name = d.displayName ? d.displayName : d.name
            if (
              name
                .toLowerCase()
                .indexOf(
                  self.config.selectKPIPanel.filterQuery.toLowerCase()
                ) >= 0
            ) {
              return d
            }
          })
        } else {
          return this.config.selectKPIPanel.data
        }
      }
      return []
    },
    sortedKpiAssetsPanel() {
      return this.$helpers.sortData('name', this.filteredKpiAssetsPanel)
    },
    sortedAssetPointsPanel() {
      return this.$helpers.sortData('name', this.filteredAssetPointsPanel)
    },
    sorteddReadingsPanel() {
      return this.$helpers.sortData('name', this.filteredReadingsPanel)
    },
    sortedAssetsPanel() {
      return this.$helpers.sortData('name', this.filteredAssetsPanel)
    },
  },
  watch: {
    buildingIds: function(newVal, oldVal) {
      if (JSON.stringify(newVal) !== JSON.stringify(oldVal)) {
        this.config.mainPanel.currentModule = ''
        this.loadKPIAssetCategories()
        this.loadAssetCategories()
        this.resetReadings()
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
      if (newValue) {
        this.prepareSavedDataPoints()
      }
    },
  },
  mounted() {},
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
    updateTemplateFilterToggle() {
      console.log('template filter', this.templateFilterToggle)
      this.$emit('update:templateFilterToggle', this.templateFilterToggle)
    },
    addToBuildingMap(newVal) {
      let temp = {}
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
    async prepareSavedDataPoints() {
      if (!isEmpty(this.savedPoints)) {
        let points = [...this.savedPoints]
        this.currentKey = this.currentKey + 1
        for (let group of this.savedPoints) {
          for (let reading of group.readings) {
            reading.marked = this.currentKey
          }
        }

        let groups = cloneDeep(points)
        this.summaryList = []
        for await (let group of groups) {
          let kpiCategoryId = null
          if (!isEmpty(group) && group.label === 'kpi') {
            let kpiGroupsIndex = points.findIndex(
              point => point.label === group.label
            )
            await this.loadKPIAssetCategories()
            for await (let reading of group.readings) {
              kpiCategoryId = reading.categoryId ? reading.categoryId : -1
              let assetCategories =
                this.config.mainPanel.kpiAssetCategories || []
              let category = assetCategories.filter(
                category => category.id === kpiCategoryId
              )
              if (!isEmpty(category[0])) {
                let label = category[0].displayName
                await this.openKPIAssetCategory(kpiCategoryId)
                if (reading.kpiType !== 'DYNAMIC') {
                  let kpiObj = this.field_vs_Kpi_map[reading.readingId]
                  let selectedDP =
                    this.$getProperty(
                      this,
                      'config.mainPanel.selectedDataPoints'
                    ) || []
                  let dataPoints = selectedDP.map(point => {
                    let pointSplit = point.split('_')
                    let fieldId = pointSplit[0]
                    if (parseInt(fieldId) === reading['readingId']) {
                      point = kpiObj?.id + '_' + pointSplit[1] + '_kpi'
                    }
                    return point
                  })
                  this.config.mainPanel.selectedDataPoints = dataPoints
                  reading['readingId'] = parseInt(kpiObj?.id)
                }
                this.addAssetToGroup(label, reading, kpiCategoryId)
                this.config.kpiPointsPanel.activeSubTab = 'kpis'
                reading['readingLabel'] = this.kpireadings.fields[
                  reading.readingId
                ]?.name
                let kpiReading =
                  this.kpireadings.fields[reading.readingId] || {}
                this.openKpiTypePoint(kpiReading)
              }
            }
            if (kpiGroupsIndex !== -1) {
              points.splice(kpiGroupsIndex, 1)
            }
          }
          if (!isEmpty(group) && group.label === 'Asset') {
            let assetGroupIndex = points.findIndex(
              point => point.label === group.label
            )
            this.loadAssetCategories()
            this.config.mainPanel.currentModule = 'assets'
            for await (let inReading of group.readings) {
              let assetCategory = inReading.categoryId
                ? inReading.categoryId
                : -1
              let assetCategoryLabel = this.assetCategory.filter(
                category => category.id === parseInt(assetCategory)
              )
              if (assetCategoryLabel.length !== 0) {
                assetCategoryLabel = assetCategoryLabel[0].displayName
                this.addAssetToGroup(
                  assetCategoryLabel,
                  inReading,
                  assetCategory
                )
              }
              if (this.config.mainPanel.assetCategories) {
                let category = this.config.mainPanel.assetCategories.filter(
                  categories => categories.id === parseInt(assetCategory)
                )
                if (category.length !== 0) {
                  this.config.assetPointsPanel.activeSubTab = 'assets'
                  await this.openAssetCategory(assetCategory)
                  let self = this
                  this.$nextTick().then(function() {
                    if (
                      typeof self.readings.categoryWithAssets[assetCategory] !==
                      'undefined'
                    ) {
                      let field =
                        self.readings.categoryWithFields[assetCategory][
                          inReading.readingId
                        ]
                      let asset =
                        self.readings.categoryWithAssets[assetCategory][
                          inReading.resourceId
                        ]
                      if (asset) {
                        self.openAsset(inReading.resourceId)
                      }
                    }
                  })
                }
              }
            }
            if (assetGroupIndex !== -1) {
              points.splice(assetGroupIndex, 1)
            }
          }
        }
        this.summaryList.push(...points)

        //code to display the datapoints as per the selected order while edit
        let dataPoints = this.$getProperty(
          this,
          'config.mainPanel.selectedDataPoints'
        )
        let summaryListNew = dataPoints.reduce((groups, dataPoint) => {
          let fieldId = parseInt(dataPoint.split('_')[0])
          for (let group of this.summaryList) {
            let { readings } = group || {}
            let reading = (readings || []).filter(
              ele => ele.readingId === fieldId
            )[0]

            if (!isEmpty(reading)) {
              let readingIndex = (readings || []).findIndex(
                ele => ele.readingId === reading.readingId
              )

              let existingGroup = groups.filter(
                ele => ele.label === group.label
              )[0]

              if (!isEmpty(existingGroup)) {
                let groupIndex = groups.findIndex(
                  ele => ele.label === existingGroup.label
                )
                if (readingIndex != -1 && groupIndex != -1) {
                  let { readings: fields } = groups[groupIndex]
                  fields.push(reading)
                  groups[groupIndex].readings = fields
                  group.readings.splice(readingIndex, 1)
                }
                break
              } else {
                let fieldObj = {}
                fieldObj['label'] = group.label
                fieldObj['categoryId'] = group.label
                fieldObj['readings'] = [reading]
                groups.push(fieldObj)
                group.readings.splice(readingIndex, 1)
                break
              }
            }
          }
          return groups
        }, [])

        this.summaryList = summaryListNew
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
      let kpiFieldObj = this.$getProperty(this, 'kpireadings.fields') || {}
      let kpiObj = Object.keys(kpiFieldObj).filter(
        key => parseInt(key) === reading.readingId
      )[0]
      let dataPointKey = reading.readingId + '_' + reading.resourceId
      let readingSumId = !isEmpty(kpiObj) ? dataPointKey + '_kpi' : dataPointKey
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
      let mvObject = this.mvAssetMap
        ? this.mvAssetMap[parseInt(readingSplit[readingSplit.length - 1])]
        : null
      if (this.kpireadings) {
        if (
          this.kpireadings.assets &&
          this.kpireadings.assets[parseInt(readingSplit[1])] &&
          this.kpireadings.fields &&
          this.kpireadings.fields[parseInt(readingSplit[0])] &&
          readingSplit[2] === 'kpi'
        ) {
          return 'kpis'
        }
      }
      if (this.rulereadings) {
        if (
          this.rulereadings.assets &&
          this.rulereadings.assets[
            parseInt(readingSplit[readingSplit.length - 1])
          ] &&
          this.rulereadings.fields &&
          this.rulereadings.fields[parseInt(readingSplit[0])]
        ) {
          return 'rules'
        }
      }

      if (this.buildingIds[0] === parseInt(readingSplit[1])) {
        return ''
      } else if (assetObject) {
        return 'assets'
      } else if (mvObject) {
        return 'mvs'
      } else {
        return 'weather'
      }
    },

    addToSummaryListNew() {
      let self = this
      let { mainPanel } = this.config
      this.applyDisable = false
      let existingReadings = []
      let points = []
      if (
        this.config.mainPanel.selectedDataPoints &&
        this.config.mainPanel.selectedDataPoints.length !== 0
      ) {
        console.log('this.reportObject', this.reportObject)
        if (this.reportObject && this.reportObject.reportType === 4) {
          let existingAsset = this.reportObject.options.reportTemplate
            .defaultValue
          console.log('existingAsset', existingAsset)
          for (let point of this.config.mainPanel.selectedDataPoints) {
            if (existingAsset === parseInt(point.split('_')[1])) {
              let temp = {
                point: point,
                isDone: false,
              }
              points.push(temp)
            } else {
              if (
                mainPanel.currentModule === 'assets' ||
                mainPanel.currentModule === 'space'
              ) {
                this.applyDisable = true
              }
              break
            }
          }
        } else {
          for (let point of this.config.mainPanel.selectedDataPoints) {
            let temp = {
              point: point,
              isDone: false,
            }
            points.push(temp)
          }
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
            let resourceId = parseInt(readingSplit[1])

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
            if (category === 'kpis') {
              let readings = this.allFields[category].filter(
                reading => reading?.id == readingSplit[0]
              )[0]
              readingObject = !isEmpty(readings) ? readings : {}
            } else if (
              category &&
              category !== '' &&
              this.allFields[category] &&
              this.allFields[category][parseInt(readingSplit[0])]
            ) {
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
            } else if (category === 'rules') {
              for (let cat of Object.keys(
                this.rulereadings.categoryWithAssets
              )) {
                if (
                  Object.keys(
                    this.rulereadings.categoryWithAssets[cat]
                  ).includes(readingSplit[readingSplit.length - 1])
                ) {
                  categoryId = parseInt(cat)
                  break
                } else {
                  categoryId = -1
                }
              }
              categoryLabel = this.config.mainPanel.ruleAssetCategories.filter(
                categories => categories.id === categoryId
              )[0].name
            } else if (category === 'kpis') {
              for (let cat of Object.keys(
                this.kpireadings.categoryWithAssets
              )) {
                if (
                  Object.keys(
                    this.kpireadings.categoryWithAssets[cat]
                  ).includes(readingSplit[1])
                ) {
                  categoryId = parseInt(cat)
                  break
                } else {
                  categoryId = -1
                }
              }
              categoryLabel = this.config.mainPanel.kpiAssetCategories.filter(
                categories => categories.id === categoryId
              )[0].name
            } else if (category === 'weather' && readingObject) {
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
            } else if (category === 'mvs') {
              console.log('this.summaryList', this.summaryList, readingObject)
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
                categoryLabel = 'M&Vs'
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
                ? readingObject.displayName
                : readingObject.name
              reading['resourceId'] = parseInt(readingSplit[1])
              reading['resourceLabel'] =
                category === 'assets'
                  ? this.readings.assets[
                      parseInt(readingSplit[readingSplit.length - 1])
                    ]
                  : category === 'rules' &&
                    this.rulereadings.assets[
                      parseInt(readingSplit[readingSplit.length - 1])
                    ]
                  ? this.rulereadings.assets[
                      parseInt(readingSplit[readingSplit.length - 1])
                    ]
                  : category === 'kpis' &&
                    this.kpireadings.assets[parseInt(readingSplit[1])]
                  ? this.kpireadings.assets[parseInt(readingSplit[1])]
                  : category === 'mvs' && this.mvprojectNameMap
                  ? this.mvprojectNameMap[
                      parseInt(readingSplit[readingSplit.length - 1])
                    ]
                  : null
              reading['location'] = this.selectedBuildingLabel //this.selectedBuildingName
              reading['marked'] = this.currentKey

              let temp = {}
              temp['label'] = categoryLabel
              temp['readings'] = []
              if (category === 'assets' || category === 'kpis') {
                temp['categoryId'] = categoryId
              }
              temp.readings.push(reading)
              this.summaryList.push(temp)
            } else {
              let reading = {}

              reading['readingId'] = parseInt(readingSplit[0])
              reading['readingLabel'] = readingObject.displayName
                ? readingObject.displayName
                : readingObject.name
              reading['resourceId'] = parseInt(readingSplit[1])
              reading['resourceLabel'] =
                category === 'assets'
                  ? this.readings.assets[
                      parseInt(readingSplit[readingSplit.length - 1])
                    ]
                  : category === 'kpis' &&
                    this.kpireadings.assets[parseInt(readingSplit[1])]
                  ? this.kpireadings.assets[parseInt(readingSplit[1])]
                  : category === 'mvs' && this.mvprojectNameMap
                  ? this.mvprojectNameMap[
                      parseInt(readingSplit[readingSplit.length - 1])
                    ]
                  : null
              reading['location'] = this.selectedBuildingLabel //this.selectedBuildingName
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
      if (!this.newSiteSummary) {
        if (this.weatherPanel.show) {
          this.weatherPanel.show = false
        } else {
          this.weatherPanel.show = true
        }
      } else {
        this.loadNewWeatherReadings(this.newWeatherModule)
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
      if (this.buildingList) {
        let filters = {}
        if (this.$route.query.filters) {
          filters = JSON.parse(this.$route.query.filters)
        }
        if (filters.buildingId && filters.buildingId !== -1) {
          this.selectedBuildings = [filters.buildingId]
        } else {
          this.selectedBuildings = [this.default_building_id] //[Object.keys(this.buildingList)[0]]
        }
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
      if (self.config['mvPointsPanel'].show) {
        panels.push('selectAssetsPanel')
      }
      if (self.config['selectMvsPanel'].show) {
        panels.push('selectAssetsPanel')
      }
      if (self.config['selectMvReadingsPanel'].show) {
        panels.push('selectReadingsPanel')
      }
      if (self.config['kpiPointsPanel'].show) {
        panels.push('kpiPointsPanel')
      }
      if (self.config['selectKPIPanel'].show) {
        panels.push('selectKPIPanel')
      }
      self.togglePanel(panels)

      self.config.mainPanel.assetCategoriesLoading = true
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
          self.config.mainPanel.assetCategoriesLoading = false
          self.loading = false
        })
    },
    async loadKPIAssetCategories() {
      let self = this
      let panels = []
      let currentModule =
        this.$getProperty(self, 'config.mainPanel.currentModule') || null
      if (currentModule === 'kpis') {
        self.config.mainPanel.currentModule = ''
        return
      }
      if (self.config['assetPointsPanel'].show) {
        panels.push('assetPointsPanel')
      }
      if (self.config['selectReadingsPanel'].show) {
        panels.push('selectReadingsPanel')
      }
      if (self.config['selectAssetsPanel'].show) {
        panels.push('selectAssetsPanel')
      }
      if (self.config['mvPointsPanel'].show) {
        panels.push('selectAssetsPanel')
      }
      if (self.config['selectMvsPanel'].show) {
        panels.push('selectAssetsPanel')
      }
      if (self.config['selectMvReadingsPanel'].show) {
        panels.push('selectReadingsPanel')
      }
      if (self.config['kpiPointsPanel'].show) {
        panels.push('kpiPointsPanel')
      }
      if (self.config['selectKPIPanel'].show) {
        panels.push('selectKPIPanel')
      }
      self.togglePanel(panels)
      self.config.mainPanel.selectedKPICategory = null
      self.config.mainPanel.currentModule = 'kpis'
      self.config.mainPanel.kpiAssetCategoriesLoading = true
      let params = { buildingIds: self.buildingIds }
      let { data, error } = await API.get(
        'v3/readingKpi/analytics/fetchAssetCategoriesContainingKpis',
        params
      )
      if (!error) {
        let categoryIds = Object.keys(data)
        self.config.mainPanel.kpiAssetCategories = self.assetCategory.filter(
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
      self.config.mainPanel.kpiAssetCategoriesLoading = false
    },
    async openKPIAssetCategory(categoryId) {
      let self = this
      self.togglePanel('kpiPointsPanel')
      self.config.mainPanel.selectedKPICategory = categoryId
      self.config.kpiPointsPanel.loading = true
      self.config.kpiPointsPanel.selectedKpi = null
      this.config.mainPanel.currentModule = 'kpis'
      if (
        self.kpireadings.categories.includes(categoryId) &&
        self.kpireadings.categoryWithAssets[categoryId]
      ) {
        this.setAssetPointsPanelForKpi(categoryId)
      } else {
        let params = { categoryId: categoryId }
        if (!isEmpty(self.buildingIds)) {
          params['buildingIds'] = self.buildingIds
        }
        let { data, error } = await API.get(
          'v3/readingKpi/analytics/getReadingsForSpecificAssetCategory',
          params
        )
        if (!error) {
          this.setAssetReadingsForKpi(categoryId, data)
          this.setAssetPointsPanelForKpi(categoryId)
        }
      }
    },
    setAssetReadingsForKpi(categoryId, data) {
      let self = this
      if (!self.kpireadings.categories.includes(categoryId)) {
        self.kpireadings.categories.push(categoryId)
      }
      if (data.categoryWithAssets[categoryId] !== undefined) {
        self.kpireadings.categoryWithAssets[categoryId] =
          data.categoryWithAssets[categoryId]
      }
      if (data.categoryWithFields[categoryId]) {
        self.kpireadings.categoryWithFields[categoryId] =
          data.categoryWithFields[categoryId]
      }

      for (let kpiId of Object.keys(data.fields)) {
        let fieldObj = data.fields[kpiId]
        let { readingField } = fieldObj
        if (!self.kpireadings.fields[kpiId]) {
          fieldObj['id'] = kpiId
          self.kpireadings.fields[kpiId] = this.$helpers.cloneObject(fieldObj)
        }
        let fields = self.allFields['kpis'].filter(field => field.id === kpiId)
        if (self.allFields['kpis'] && isEmpty(fields)) {
          self.allFields['kpis'].push(fieldObj)
        }
        this.field_vs_Kpi_map[readingField?.fieldId] = fieldObj
      }
      if (data.assets) {
        for (let assetId of Object.keys(data.assets)) {
          if (!self.kpireadings.assets[assetId]) {
            this.id_vs_asset[assetId] = data.assets[assetId]
            self.kpireadings.assets[assetId] = data.assets[assetId]
          }
        }
      }
    },
    setAssetPointsPanelForKpi(categoryId) {
      let self = this
      if (self.config.kpiPointsPanel.activeSubTab === '') {
        let assetIds = Object.keys(
          self.kpireadings.categoryWithAssets[categoryId]
        )
        self.config.kpiPointsPanel.data = assetIds.map(el => {
          return { id: el, name: self.kpireadings.assets[el] }
        })
        self.config.kpiPointsPanel.loading = false
      } else {
        let readingIds =
          Object.keys(self.kpireadings.categoryWithFields[categoryId]) || []
        self.config.kpiPointsPanel.data = readingIds.map(
          el => self.kpireadings.fields[el]
        )
        self.config.kpiPointsPanel.loading = false
      }
    },
    loadRuleAssetCategories() {
      let self = this
      this.weatherPanel.show = false
      this.weatherPanel.weatherCategory = null
      for (let module of self.weatherModules) {
        module.selected = false
      }
      if (this.config.mainPanel.currentModule === 'rules') {
        this.config.mainPanel.currentModule = ''
        return
      }
      this.config.mainPanel.currentModule = 'rules'
      this.config.mainPanel.selectedRuleAssetCategory = null
      this.config.mainPanel.ruleAssetCategories = null
      this.config.mainPanel.ruleAssetCategoriesLoading = true
      let panels = []
      if (this.config['assetPointsPanel'].show) {
        panels.push('assetPointsPanel')
      }
      if (this.config['selectReadingsPanel'].show) {
        panels.push('selectReadingsPanel')
      }
      if (this.config['selectAssetsPanel'].show) {
        panels.push('selectAssetsPanel')
      }
      if (this.config['mvPointsPanel'].show) {
        panels.push('selectAssetsPanel')
      }
      if (this.config['selectMvsPanel'].show) {
        panels.push('selectAssetsPanel')
      }
      if (this.config['selectMvReadingsPanel'].show) {
        panels.push('selectReadingsPanel')
      }
      this.togglePanel(panels)

      self.$http
        .post('/asset/getAssetCategoryWithReadings', {
          buildingIds: self.buildingIds,
          fetchOnlyAlarmPoints: true,
        })
        .then(function(response) {
          let categoryIds = Object.keys(response.data)
          self.config.mainPanel.ruleAssetCategories = self.assetCategory.filter(
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
          self.config.mainPanel.ruleAssetCategoriesLoading = false
          self.loading = false
        })
    },

    resetReadings() {
      this.readings = {
        categories: [],
        assets: {},
        categoryWithAssets: {},
        fields: {},
        categoryWithFields: {},
      }
      this.kpireadings = {
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

    openmvproject(mvId) {
      let self = this
      self.togglePanel('mvPointsPanel')
      self.config.mvPointsPanel.loading = true
      self.config.mvPointsPanel.selectedMvOrPoint = null
      if (self.config.mvPointsPanel.activeSubTab === 'mvs') {
        self.config.mvPointsPanel.data = self.mvprojects
          ? self.mvprojects.map(el => {
              return { id: el.id, name: el.name }
            })
          : []
        self.config.mvPointsPanel.loading = false
      } else {
        self.config.mvPointsPanel.data = self.mvreadings
          ? self.mvreadings.map(el => {
              return { id: el.id, name: el.displayName }
            })
          : []
        self.allFields['mvs'] = self.convertToList(
          self.config.mvPointsPanel.data
        )
        self.config.mvPointsPanel.loading = false
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
              if (response.data.categoryWithAssets[categoryId] !== undefined) {
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

    async openRuleAssetCategory(categoryId) {
      let self = this
      this.togglePanel('assetPointsPanel')
      this.config.mainPanel.selectedRuleAssetCategory = categoryId
      this.config.assetPointsPanel.loading = true
      this.config.assetPointsPanel.selectedAssetOrPoint = null
      if (
        this.rulereadings.categories.includes(categoryId) &&
        isEmpty(this.buildingIds)
      ) {
        if (this.config.assetPointsPanel.activeSubTab === 'assets') {
          let assetIds = Object.keys(
            this.rulereadings.categoryWithAssets[categoryId]
          )
          this.config.assetPointsPanel.data = assetIds.map(el => {
            return { id: el, name: self.rulereadings.assets[el] }
          })
          this.config.assetPointsPanel.loading = false
        } else {
          let readingIds = Object.keys(
            this.rulereadings.categoryWithFields[categoryId]
          )
          this.config.assetPointsPanel.data = readingIds.map(el => {
            return this.rulereadings.fields[el]
          })
          this.config.assetPointsPanel.loading = false
        }
      } else {
        if (this.buildingIds) {
          await self.$http
            .post('/asset/getReadingsForSpecificAssetCategory', {
              buildingIds: self.buildingIds,
              categoryIds: [categoryId],
              fetchOnlyAlarmPoints: true,
            })
            .then(function(response) {
              if (!self.rulereadings.categories.includes(categoryId)) {
                self.rulereadings.categories.push(categoryId)
              }
              self.rulereadings.categoryWithAssets[categoryId] =
                response.data.categoryWithAssets[categoryId]
              self.rulereadings.categoryWithFields[categoryId] =
                response.data.categoryWithFields[categoryId]
              if (response.data.fields) {
                for (let fieldId of Object.keys(response.data.fields)) {
                  if (!self.rulereadings.fields[fieldId]) {
                    self.rulereadings.fields[fieldId] =
                      response.data.fields[fieldId]
                  }
                  if (
                    self.allFields['rules'] &&
                    !self.allFields['rules'][fieldId]
                  ) {
                    self.allFields['rules'][fieldId] =
                      response.data.fields[fieldId]
                  }
                }
              }
              if (response.data.assets) {
                for (let assetId of Object.keys(response.data.assets)) {
                  if (!self.rulereadings.assets[assetId]) {
                    self.rulereadings.assets[assetId] =
                      response.data.assets[assetId]
                  }
                }
              }
              if (self.config.assetPointsPanel.activeSubTab === 'assets') {
                let assetIds = Object.keys(
                  self.rulereadings.categoryWithAssets[categoryId]
                )
                self.config.assetPointsPanel.data = assetIds.map(el => {
                  return { id: el, name: self.rulereadings.assets[el] }
                })
                self.config.assetPointsPanel.loading = false
              } else {
                let readingIds = Object.keys(
                  self.rulereadings.categoryWithFields[categoryId]
                )
                self.config.assetPointsPanel.data = readingIds.map(el => {
                  return self.rulereadings.fields[el]
                })
                self.config.assetPointsPanel.loading = false
              }
            })
        }
      }
    },

    openAsset(assetId) {
      this.config.assetPointsPanel.selectedAssetOrPoint = assetId

      this.togglePanel('assetPointsPanel', 'selectReadingsPanel')
      this.config.selectReadingsPanel.loading = true
      this.config.selectReadingsPanel.parentId = assetId

      let fieldIds =
        this.config.mainPanel.currentModule === 'rules'
          ? this.rulereadings.categoryWithAssets[
              this.config.mainPanel.selectedRuleAssetCategory
            ][assetId]
          : this.readings.categoryWithAssets[
              this.config.mainPanel.selectedAssetCategory
            ][assetId]
      this.config.selectReadingsPanel.data = fieldIds?.map(el => {
        return this.config.mainPanel.currentModule === 'rules'
          ? this.rulereadings.fields[el]
          : this.readings.fields[el]
      })
      this.config.selectReadingsPanel.loading = false
    },
    openKpiTypePoint(item) {
      this.config.kpiPointsPanel.selectedKpi = item.id
      this.togglePanel('kpiPointsPanel', 'selectKPIPanel')
      this.config.selectKPIPanel.loading = true
      this.config.selectKPIPanel.readingId = item.id

      let assetIds =
        this.kpireadings.categoryWithFields[
          this.config.mainPanel.selectedKPICategory
        ][item.id] || []
      this.config.selectKPIPanel.data = assetIds.map(el => {
        return {
          id: el,
          name: this.kpireadings.assets[el],
        }
      })
      this.config.selectKPIPanel.loading = false
    },
    openPoint(pointId) {
      this.config.assetPointsPanel.selectedAssetOrPoint = pointId

      this.togglePanel('assetPointsPanel', 'selectAssetsPanel')
      this.config.selectAssetsPanel.loading = true
      this.config.selectAssetsPanel.readingId = pointId

      let assetIds =
        this.config.mainPanel.currentModule === 'rules'
          ? this.rulereadings.categoryWithFields[
              this.config.mainPanel.selectedRuleAssetCategory
            ][pointId]
          : this.readings.categoryWithFields[
              this.config.mainPanel.selectedAssetCategory
            ][pointId]
      this.config.selectAssetsPanel.data = assetIds.map(el => {
        return {
          id: el,
          name:
            this.config.mainPanel.currentModule === 'rules'
              ? this.rulereadings.assets[el]
              : this.readings.assets[el],
        }
      })
      this.config.selectAssetsPanel.loading = false
    },

    openMvPoint(pointId) {
      let self = this
      self.config.assetPointsPanel.selectedMvOrPoint = pointId

      self.togglePanel('mvPointsPanel', 'selectMvsPanel')
      self.config.selectMvsPanel.loading = true
      self.config.selectMvsPanel.readingId = pointId

      self.config.selectMvsPanel.data = self.mvprojects
        ? self.mvprojects.map(el => {
            return { id: el.id, name: el.name }
          })
        : []
      self.config.selectMvsPanel.loading = false
    },

    openMv(mvId) {
      let self = this
      self.config.mvPointsPanel.selectedMvOrPoint = mvId

      self.togglePanel('mvPointsPanel', 'selectMvReadingsPanel')
      self.config.selectMvReadingsPanel.loading = true
      self.config.selectMvReadingsPanel.parentId = mvId

      self.config.selectMvReadingsPanel.data = self.mvreadings
        ? self.mvreadings.map(el => {
            return { id: el.id, name: el.displayName }
          })
        : []
      self.allFields['mvs'] = self.convertToList(
        self.config.selectMvReadingsPanel.data
      )
      self.config.selectMvReadingsPanel.loading = false
    },

    getReadingFields(readings) {
      let data = []
      for (let reading of readings) {
        if (reading.fields) {
          for (let field of reading.fields) {
            field.module = reading.name
            data.push(field)
          }
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

    async loadNewWeatherReadings(weatherModule) {
      this.config.selectReadingsPanel.loading = true
      this.config.mainPanel.currentModule = weatherModule.moduleName
      this.togglePanel('selectReadingsPanel')
      let { buildingIds = 0 } = this
      let buildingId = buildingIds[0]
      if (!isEmpty(buildingId)) {
        let { data, error } = await API.get(
          'v3/weather/buildingSpecificWeatherFields?',
          {
            buildingId,
          }
        )
        if (error) {
          this.config.selectReadingsPanel.data = []
          this.config.selectReadingsPanel.parentId = null
        } else {
          let { fields = [], stationId } = data || {}
          this.config.selectReadingsPanel.data = fields
          this.config.selectReadingsPanel.parentId = stationId
          this.allFields['weather'] = this.convertToList(fields)
        }
      }
      this.config.selectReadingsPanel.loading = false
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

    loadMVProjects() {
      let self = this
      self.config.mainPanel.currentModule = 'mv'
      self.config.mvPointsPanel.loading = true
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
      if (self.config['mvPointsPanel'].show) {
        panels.push('selectAssetsPanel')
      }
      if (self.config['selectMvsPanel'].show) {
        panels.push('selectAssetsPanel')
      }
      if (self.config['selectMvReadingsPanel'].show) {
        panels.push('selectReadingsPanel')
      }
      self.togglePanel(panels)
      self.togglePanel('mvPointsPanel')
      let fetchMvDetails = siteId => {
        let mvprojects = self.$http
          .get(`/v2/mv/getMVProjectList`)
          .then(function(response) {
            self.mvprojects = response.data.result.mvprojects.filter(
              project => project.siteId === siteId
            )
          })
        let mvreadings = self.$http
          .get('/v2/readings/getSubModuleRel?moduleName=mvproject')
          .then(function(response) {
            let submodules = response.data.result.submodules
            self.mvreadings = []
            for (let module in submodules) {
              self.mvreadings = self.mvreadings.concat(
                submodules[module].fields
              )
            }
            if (self.mvreadings !== []) {
              self.mvreadings = self.mvreadings.filter(el => el.dataType === 3)
            }
          })
        Promise.all([mvprojects, mvreadings]).finally(() => {
          if (self.config.mvPointsPanel.activeSubTab === 'mvs') {
            self.config.mvPointsPanel.data = self.mvprojects
              ? self.mvprojects.map(el => {
                  return { id: el.id, name: el.name }
                })
              : []
            self.config.mvPointsPanel.loading = false
          } else {
            self.config.mvPointsPanel.data = self.mvreadings
              ? self.mvreadings.map(el => {
                  return { id: el.id, name: el.displayName }
                })
              : []
            self.config.mvPointsPanel.loading = false
          }
          self.mvAssetMap = {}
          self.mvprojectNameMap = {}
          if (self.mvprojects) {
            for (let project in self.mvprojects) {
              self.mvAssetMap[self.mvprojects[project].id] =
                self.mvprojects[project].meter.id
              self.mvprojectNameMap[self.mvprojects[project].id] =
                self.mvprojects[project].name
            }
          }
        })
      }

      self.$http
        .get('/building/' + self.buildingIds[0])
        .then(function(response) {
          if (response.data.building) {
            let siteId = response.data.building.siteId
            fetchMvDetails(siteId)
          } else {
            self.$http
              .get('/zone/' + self.buildingIds[0])
              .then(function(response) {
                if (response.data.zone) {
                  let siteId = response.data.zone.siteId
                  fetchMvDetails(siteId)
                }
              })
          }
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
      if (this.applyDisable === false) {
        let dataPoints = []
        if (
          this.config.mainPanel.selectedDataPoints &&
          this.config.mainPanel.selectedDataPoints.length
        ) {
          for (let dp of this.config.mainPanel.selectedDataPoints) {
            let buildingId = this.getBuildingId(dp)
            let readingFieldId = parseInt(dp.split('_')[0])
            let parentId = parseInt(dp.split('_')[1])
            let aggr = null
            let name = null
            let dataType = null
            let moduleName = null
            if (
              this.readings &&
              this.readings.assets[parentId] &&
              this.allFields.assets &&
              this.allFields.assets[readingFieldId]
            ) {
              name =
                this.readings.assets[parentId] +
                ' ( ' +
                this.allFields.assets[readingFieldId].displayName +
                ' )'
              dataType = this.readings.fields[readingFieldId].dataType
            }
            if (
              this.rulereadings &&
              this.rulereadings.assets[parentId] &&
              this.allFields.rules &&
              this.allFields.rules[readingFieldId]
            ) {
              name =
                this.rulereadings.assets[parentId] +
                ' ( ' +
                this.allFields.rules[readingFieldId].displayName +
                ' )'
              dataType = this.rulereadings.fields[readingFieldId].dataType
            } else if (
              this.mvprojectNameMap &&
              this.mvprojectNameMap[parentId] &&
              this.allFields.mvs &&
              this.allFields.mvs[readingFieldId]
            ) {
              name =
                this.mvprojectNameMap[parentId] +
                ' ( ' +
                this.allFields.mvs[readingFieldId].name +
                ' )'
            } else if (!this.readingName && !this.assetName) {
              if (
                this.allFields.space &&
                this.allFields.space[readingFieldId]
              ) {
                name = this.allFields.space[readingFieldId].displayName
                dataType = this.allFields.space[readingFieldId].dataType
              } else if (
                this.allFields.weather &&
                this.allFields.weather[readingFieldId]
              ) {
                name = this.allFields.weather[readingFieldId].displayName
                dataType = this.allFields.weather[readingFieldId].dataType
              } else if (
                this.allFields.enpi &&
                this.allFields.enpi[readingFieldId]
              ) {
                name = this.allFields.enpi[readingFieldId].displayName
                dataType = this.allFields.enpi[readingFieldId].dataType
              }
            }
            if (
              this.mvAssetMap &&
              this.mvAssetMap[parentId] &&
              this.allFields.mvs &&
              this.allFields.mvs[readingFieldId]
            ) {
              buildingId = -1
              aggr = 3
              moduleName = 'mvproject'
            }
            let dataPoint = {
              parentId: parentId,
              name: name,
              moduleName: moduleName,
              yAxis: {
                dataType: dataType,
                fieldId: readingFieldId,
                aggr:
                  this.readings.fields &&
                  this.readings.fields[readingFieldId] &&
                  this.readings.fields[readingFieldId].unit &&
                  ['currency', 'kwh', 'co2', 'kg', 'mwh'].includes(
                    this.readings.fields[readingFieldId].unit
                      .trim()
                      .toLowerCase()
                  )
                    ? 3
                    : aggr
                    ? aggr
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
            }
            if (this.allFields.kpis && dp.split('_')[2] === 'kpi') {
              let readings = this.allFields['kpis'].filter(
                reading => reading?.id == readingFieldId
              )[0]
              if (!isEmpty(readings)) {
                if (readings.kpiType === 'DYNAMIC') {
                  this.hideUserFilter = true
                  name = !isEmpty(this.id_vs_asset[parentId])
                    ? this.id_vs_asset[parentId] + ' ( ' + readings.name + ' )'
                    : name
                  dataPoint.name = name
                  dataPoint.kpiType = readings.kpiType
                  dataPoint.yAxis.dynamicKpi = readingFieldId
                  dataPoint.yAxis.fieldId = null
                  dataPoint.yAxis.dataType = null
                  dataPoints.push(dataPoint)
                } else {
                  let { readingField = {} } = readings
                  name = !isEmpty(this.id_vs_asset[parentId])
                    ? this.id_vs_asset[parentId] +
                      ' ( ' +
                      readingField.displayName +
                      ' )'
                    : name
                  dataPoint.kpiType = readings.kpiType
                  dataPoint.prediction = readingField?.moduleType === 11
                  dataPoint.yAxis.fieldId = readingField?.fieldId
                  dataPoint.name = name
                  dataPoint.yAxis.dataType = readingField?.dataType
                  dataPoints.push(dataPoint)
                }
              }
            } else {
              dataPoints.push(dataPoint)
            }
          }
        }
        let selectedAssetCategory = this.config.mainPanel.selectedAssetCategory
        if (selectedAssetCategory) {
          this.$parent.assetCategoryName = this.config.mainPanel.assetCategories.filter(
            categories => categories.id === selectedAssetCategory
          )[0].name
        }
        this.$emit('hideFilter', this.hideUserFilter)
        this.$emit('updateDataPoints', dataPoints)
      }
    },
    checkAndReduceRangeTraces(dataPoints) {
      let finalDataPoints = []
      let keyAndDataPoints = {}
      for (let dP of dataPoints) {
        let fieldId = !isEmpty(dP.yAxis.dynamicKpi)
          ? dP.yAxis.dynamicKpi
          : dP.yAxis.fieldId
        if (keyAndDataPoints.hasOwnProperty(fieldId + '.' + dP.parentId)) {
          keyAndDataPoints[fieldId + '.' + dP.parentId].push(dP)
        } else {
          keyAndDataPoints[fieldId + '.' + dP.parentId] = []
          keyAndDataPoints[fieldId + '.' + dP.parentId].push(dP)
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
            if (!isEmpty(dp.yAxis.dynamicKpi)) {
              this.config.mainPanel.selectedDataPoints.push(
                dp.yAxis.dynamicKpi + '_' + dp.parentId + '_kpi'
              )
            } else {
              this.config.mainPanel.selectedDataPoints.push(
                dp.yAxis.fieldId + '_' + dp.parentId
              )
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
</style>
