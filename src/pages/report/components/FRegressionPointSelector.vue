<template>
  <div class="newregression-analysis" v-if="visibility">
    <div class="sidebar-position-setup regeression0-ananlysis">
      <div
        class="sidebar-close-btn pointer"
        v-if="selectorPanelShow"
        @click="
          cancel()
          $emit('update:enableFloatimngIcon', true)
        "
      >
        <div class="">
          <i class="el-icon-close"></i>
        </div>
      </div>
      <div class="report-main-con">
        <div
          class="building-analysis-sidebar-conatainer regression-analytics-container"
        >
          <div class="new-report-sidebar col-3" style="box-shadow: none;">
            <div class="report-sidebar-inner">
              <div class=" pB20 pL20 pR20">
                <div class="fc-text-pink">
                  {{ $t('home.dashboard.generate_chart') }}
                </div>

                <el-row class="pT20">
                  <div class="label-txt-black pB5">
                    {{ $t('common.header.regression_type') }}
                  </div>
                  <el-select
                    v-model="regressionType"
                    @change="quitReset = false"
                    class="fc-input-full-border2 width100"
                  >
                    <el-option
                      v-for="(type, typeIndex) in regressionTypeList"
                      :key="typeIndex"
                      :label="type.displayName"
                      :value="type.name"
                    ></el-option>
                  </el-select>
                </el-row>
                <el-row class="pT20">
                  <el-col :span="24">
                    <div class="label-txt-black pB10">
                      {{ $t('common.header.dependent_variable') }}
                    </div>

                    <div
                      v-if="!config.result.metrics.selectedDataPoints.length"
                      class="fc-input-div-full-border-f14 position-relative"
                      v-bind:class="{
                        active: config.firstPanel.selectedTab === 'METRICS',
                      }"
                      @click="
                        config.firstPanel.selectedTab = 'METRICS'
                        selectorPanelShow = true
                        selectDefaultTab('METRICS')
                        $emit('update:enableFloatimngIcon', false)
                      "
                    >
                      <span>{{ $t('common._common.select') }}</span>
                      <i
                        class=" pointer mT12 el-icon-arrow-right pull-right regression-right-icon"
                      ></i>
                    </div>
                    <template
                      v-for="(data, index) in config.result.metrics
                        .selectedDataPoints"
                      v-else
                    >
                      <el-row :key="index" class="visibility-visible-actions">
                        <el-col
                          :span="
                            analyticsConfig && analyticsConfig.period ? 20 : 24
                          "
                        >
                          <div
                            class="fc-input-div-full-border-f14"
                            @click="
                              config.firstPanel.selectedTab = 'METRICS'
                              selectDefaultTab('METRICS')
                              selectorPanelShow = true
                              $emit('update:enableFloatimngIcon', false)
                            "
                          >
                            <span>{{ getFormatedData(data, 'metrics') }}</span>
                            <i
                              class=" pointer mT12 el-icon-arrow-right pull-right "
                            ></i>
                          </div>
                        </el-col>
                        <el-col
                          :span="4"
                          class="pL10 "
                          v-if="analyticsConfig && analyticsConfig.period"
                        >
                          <div
                            class="fc-input-div-full-border-f14 pointer pointer position-relative"
                            style="padding-left: 12px;"
                          >
                            <el-popover
                              placement="top-start"
                              width="90"
                              trigger="click"
                              popper-class="metric-popover"
                            >
                              <div
                                class="pointer pT5 pB5 fc-label-hover label-txt-black f14 text-center"
                                v-bind:class="{
                                  active:
                                    config.result['metrics'].dataPoints[
                                      index
                                    ] &&
                                    metricAggr.value ===
                                      config.result['metrics'].dataPoints[index]
                                        .yAxis.aggr,
                                }"
                                v-for="(metricAggr,
                                metricAggrIdx) in metricAggregation"
                                :value="metricAggr.value"
                                :key="metricAggrIdx"
                                @click="
                                  setAggr(index, metricAggr.value, 'metrics')
                                "
                              >
                                {{ metricAggr.label }}
                              </div>
                              <img
                                src="~assets/summation_img.svg"
                                slot="reference"
                              />
                            </el-popover>
                          </div>
                        </el-col>
                      </el-row>
                    </template>
                  </el-col>
                </el-row>

                <el-row class="pT20">
                  <el-col :span="24">
                    <div>
                      <div class="label-txt-black pB10 fL">
                        {{ $t('common.header.independent_variables') }}
                      </div>
                      <div
                        v-if="config.result.dimension.selectedDataPoints.length"
                        class="fc-dark-blue2-12 pointer"
                        style="text-align:right;"
                        @click="
                          config.firstPanel.selectedTab = 'DIMENSION'
                          selectDefaultTab('DIMENSION')
                          selectorPanelShow = true
                          $emit('update:enableFloatimngIcon', false)
                        "
                      >
                        {{ $t('common._common.add') }}
                      </div>
                    </div>

                    <div
                      v-if="!config.result.dimension.selectedDataPoints.length"
                      class="fc-input-div-full-border-f14 position-relative clearboth"
                      @click="
                        config.firstPanel.selectedTab = 'DIMENSION'
                        selectDefaultTab('DIMENSION')
                        selectorPanelShow = true
                        $emit('update:enableFloatimngIcon', false)
                      "
                    >
                      <span>{{ $t('common._common.select') }}</span>
                      <i
                        class=" pointer mT12 el-icon-arrow-right pull-right regression-right-icon"
                      ></i>
                    </div>
                    <template
                      v-for="(data1, index1) in config.result.dimension
                        .selectedDataPoints"
                      v-else
                    >
                      <el-row :key="index1" class="visibility-visible-actions">
                        <el-col
                          :span="
                            config.result.dimension.selectedDataPoints.length &&
                            analyticsConfig &&
                            analyticsConfig.period
                              ? 18
                              : 22
                          "
                          class="pB20"
                        >
                          <div
                            class="fc-input-div-full-border-f14 position-relative"
                          >
                            <span>{{
                              getFormatedData(data1, 'dimension')
                            }}</span>
                          </div>
                        </el-col>
                        <el-col
                          :span="4"
                          class="pL10 pB20"
                          v-if="analyticsConfig && analyticsConfig.period"
                        >
                          <div
                            class="fc-input-div-full-border-f14 pointer pointer position-relative"
                            style="padding-left: 12px;"
                          >
                            <el-popover
                              placement="top-start"
                              width="90"
                              trigger="click"
                              popper-class="metric-popover"
                            >
                              <div
                                class="pointer pT5 pB5 fc-label-hover label-txt-black f14 text-center"
                                v-bind:class="{
                                  active:
                                    config.result['dimension'].dataPoints[
                                      index1
                                    ] &&
                                    metricAggr.value ===
                                      config.result['dimension'].dataPoints[
                                        index1
                                      ].yAxis.aggr,
                                }"
                                v-for="(metricAggr,
                                metricAggrIdx) in metricAggregation"
                                :value="metricAggr.value"
                                :key="metricAggrIdx"
                                @click="
                                  setAggr(index1, metricAggr.value, 'dimension')
                                "
                              >
                                {{ metricAggr.label }}
                              </div>
                              <img
                                src="~assets/summation_img.svg"
                                slot="reference"
                              />
                            </el-popover>
                          </div>
                        </el-col>
                        <el-col
                          class="mT10"
                          style="text-align:right;"
                          :span="2"
                        >
                          <i
                            @click="deleteDataPoint(index1)"
                            class="el-icon-delete pointer fc-red3 f16 visibility-hide-actions"
                            style="vertical-align: sub;"
                          ></i>
                        </el-col>
                      </el-row>
                    </template>
                  </el-col>
                </el-row>

                <el-row class="clearboth pT20">
                  <el-col :span="24">
                    <div class="label-txt-black pB10">
                      {{ $t('common.dashboard.time_period') }}
                    </div>
                    <el-select
                      class="period-select fc-input-full-border2 width100"
                      v-model="analyticsConfig.period"
                      placeholder="Period"
                      v-if="analyticsConfig.mode !== 2"
                      :title="$t('common.dashboard.time_period')"
                      data-arrow="true"
                      v-tippy
                      @change="onPeriodChange"
                    >
                      <el-option
                        v-for="(period, index2) in getAvailablePeriods"
                        :key="index2"
                        :label="period.name"
                        :value="period.value"
                        :disabled="!period.enable"
                      ></el-option>
                    </el-select>
                  </el-col>
                </el-row>
              </div>
            </div>
            <div class="saved-regressions-link" @click="showSavedReport = true">
              {{ $t('common._common.view_saved_regressions') }}
            </div>
          </div>
          <div v-if="!buildingIds || !buildingIds.length">
            {{ $t('common.products.no_building_selected') }}
          </div>
          <div
            class="newanalytics-sidebar col-3"
            v-else-if="selectorPanelShow && loading"
          >
            <spinner :show="loading"></spinner>
          </div>
          <template v-if="selectorPanelShow && !loading">
            <div
              class="newanalytics-sidebar col-3"
              v-if="!loading && config.firstPanel.selectedTab === 'DIMENSION'"
            >
              <div class="data-pointer-selector-popup">
                <analaytics-filter
                  :selectedBuildings.sync="selectedBuildings"
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
                        <title>{{ $t('common._common.close') }}</title>
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
                      @click="weatherPanelToggler"
                    >
                      {{ $t('common._common.weather') }}
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
                        @click="loadWeatherReadings(weatherModule, wIdx)"
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
                        <div class="width80">
                          {{ weatherModule.displayName }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="building-analysis-side-block">
                  <div class="building-hover-actions">
                    <div class="sidebar-asset-H fL" @click="loadSpaceReadings">
                      {{ $t('common.wo_report.spaces') }}
                    </div>
                  </div>
                </div>
                <div class="building-analysis-side-block">
                  <div class="building-hover-actions">
                    <div
                      class="sidebar-asset-H fL"
                      @click="loadAssetCategories"
                    >
                      {{ $t('common.products.assets') }}
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
                            config.mainPanel.selectedAssetCategory !=
                            category.id,
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
                      {{ $t('common.products.no_assets') }}
                    </div>
                  </div>
                </div>

                <div class="building-analysis-side-block">
                  <div class="building-hover-actions">
                    <div class="sidebar-asset-H fL" @click="loadEnPIReadings">
                      {{ $t('common._common.enpi') }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div
              class="newanalytics-sidebar col-3"
              v-if="!loading && config.firstPanel.selectedTab === 'METRICS'"
            >
              <div class="data-pointer-selector-popup">
                <analaytics-filter
                  :selectedBuildings.sync="selectedBuildings"
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
                        :placeholder="$t('common._common.search')"
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
                        <title>{{ $t('common._common.close') }}</title>
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
                    >
                      {{ $t('common.products.assets') }}
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
                            config.mainPanel.selectedAssetCategory !=
                            category.id,
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
                      {{ $t('common.products.no_assets') }}
                    </div>
                  </div>
                </div>

                <div class="building-analysis-side-block">
                  <div class="building-hover-actions">
                    <div class="sidebar-asset-H fL" @click="loadSpaceReadings">
                      {{ $t('common.wo_report.spaces') }}
                    </div>
                  </div>
                </div>

                <div class="building-analysis-side-block">
                  <div class="building-hover-actions">
                    <div class="sidebar-asset-H fL" @click="loadEnPIReadings">
                      {{ $t('common._common.enpi') }}
                    </div>
                  </div>
                </div>

                <div class="building-analysis-side-block">
                  <div class="building-hover-actions">
                    <div
                      class="sidebar-asset-H fL"
                      @click="weatherPanelToggler"
                    >
                      {{ $t('common._common.weather') }}
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
                        @click="loadWeatherReadings(weatherModule, wIdx)"
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
                        <div class="width80">
                          {{ weatherModule.displayName }}
                        </div>
                      </div>
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
                      />{{ $t('common.header.controlpoints') }}</span
                    >
                    <div class="building-points-search">
                      <el-input
                        placeholder="Filter points"
                        v-model="config.assetPointsPanel.filterQuery"
                        autofocus
                        class=""
                      ></el-input>
                    </div>
                    <!-- search component end -->
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
                        {{ $t('common.products.no_points') }}
                      </div>
                      <div
                        class="asset-list-block-txt"
                        v-else
                        v-for="(item, index) in filteredAssetPointsPanel"
                        :key="index"
                        @click="openPoint(item.id)"
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
                      />{{ $t('common.products.assets') }}</span
                    >
                    <div class="building-points-search">
                      <el-input
                        :placeholder="$t('common.header.filter_assets')"
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
                        {{ $t('common.products._no_assets') }}
                      </div>
                      <div
                        class="asset-list-block-txt"
                        v-else
                        v-for="(item, index) in filteredAssetPointsPanel"
                        :key="index"
                        @click="openAsset(item.id)"
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
              v-if="
                config.selectReadingsPanel.show &&
                  config.firstPanel.selectedTab === 'DIMENSION'
              "
            >
              <div>
                <div
                  class="assets-points-inner-container readings-list-container"
                >
                  <div class="select-reading-header">
                    <div class="select-reading-H">
                      {{ $t('common.wo_report.select_readings') }}
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
                  <div class="mT10 readings-scroll asset-list-container">
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
                      {{ $t('common.header.no_data_found') }}
                    </div>

                    <el-checkbox-group
                      v-model="config.result.dimension.selectedDataPoints"
                      v-else
                      class="building-check-list"
                    >
                      <el-checkbox
                        v-for="(item, index) in filteredReadingsPanel"
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

                <div
                  class="building-analysis-point-container reading-apply-container row"
                  v-if="
                    config.result.dimension.selectedDataPoints &&
                      config.result.dimension.selectedDataPoints.length
                  "
                >
                  <div class="building-analysis-points-wrap col-12">
                    <div class="building-analysis-points-btn fL col-4">
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
            </div>
            <div
              class="assets-points-Scontainer assets-points-Scontainer-shadow"
              v-if="
                config.selectReadingsPanel.show &&
                  config.firstPanel.selectedTab === 'METRICS'
              "
            >
              <div>
                <div
                  class="assets-points-inner-container readings-list-container"
                >
                  <div class="select-reading-header">
                    <div class="select-reading-H">
                      {{ $t('common.products.select_readings') }}
                    </div>
                  </div>
                  <div class="building-points-search building-points-height">
                    <el-input
                      :placeholder="$t('common.header.filter_readings')"
                      v-model="config.selectReadingsPanel.filterQuery"
                      autofocus
                      class=""
                    ></el-input>
                  </div>
                  <div class="mT10 readings-scroll asset-list-container">
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
                      {{ $t('common.header.no_data_found') }}
                    </div>

                    <div
                      v-if="
                        config.selectReadingsPanel.show &&
                          config.firstPanel.selectedTab === 'METRICS'
                      "
                    >
                      <div
                        class="asset-list-block-txt"
                        v-for="(item, index) in filteredReadingsPanel"
                        :key="index"
                        @click="
                          selectDataPoint(
                            item.id + '_' + config.selectReadingsPanel.parentId,
                            'metrics'
                          ),
                            apply()
                        "
                      >
                        {{ item.displayName ? item.displayName : item.name }}
                        <i
                          class="el-icon-check regression-icon"
                          v-if="
                            config.result.metrics.selectedDataPoints[0] ===
                              item.id +
                                '_' +
                                config.selectReadingsPanel.parentId
                          "
                        ></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div
              class="assets-points-Scontainer assets-points-Scontainer-shadow"
              v-if="
                config.selectAssetsPanel.show &&
                  config.firstPanel.selectedTab === 'DIMENSION'
              "
            >
              <div>
                <div
                  class="assets-points-inner-container readings-list-container"
                >
                  <div class="select-reading-header">
                    <div class="select-reading-H">
                      {{ $t('common._common.select_assets') }}
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
                      {{ $t('common.header.no_data_found') }}
                    </div>

                    <el-checkbox-group
                      v-model="config.result.dimension.selectedDataPoints"
                      v-else
                      class="building-check-list"
                    >
                      <el-checkbox
                        v-for="(item, index) in filteredAssetsPanel"
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
                <div
                  class="building-analysis-point-container row"
                  v-if="
                    config.result.dimension.selectedDataPoints &&
                      config.result.dimension.selectedDataPoints.length
                  "
                >
                  <div class="building-analysis-points-wrap col-12">
                    <div></div>
                    <div class="building-analysis-points-btn fL col-4">
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
            </div>
            <div
              class="assets-points-Scontainer assets-points-Scontainer-shadow"
              v-if="
                config.selectAssetsPanel.show &&
                  config.firstPanel.selectedTab === 'METRICS'
              "
            >
              <div>
                <div
                  class="assets-points-inner-container readings-list-container"
                >
                  <div class="select-reading-header">
                    <div class="select-reading-H">
                      {{ $t('common._common.select_assets') }}
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
                      {{ $t('common.header.no_data_found') }}
                    </div>

                    <div
                      class="asset-list-block-txt"
                      v-for="(item, index) in filteredAssetsPanel"
                      :key="index"
                      @click="
                        selectDataPoint(
                          config.selectAssetsPanel.readingId + '_' + item.id,
                          'metrics'
                        ),
                          apply()
                      "
                    >
                      {{ item.displayName ? item.displayName : item.name }}
                      <i
                        class="el-icon-check regression-icon"
                        v-if="
                          config.result.metrics.selectedDataPoints[0] ===
                            config.selectAssetsPanel.readingId + '_' + item.id
                        "
                      ></i>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- sidebar3 end -->
          </template>
        </div>
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
          {{ $t('common.wo_report.this_datapoint_is_part_of_range') }}
        </div>
        <div>{{ $t('common.wo_report.sure_want_to_delete_it') }}</div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelRangeDeletion">{{
            $t('common._common.close')
          }}</el-button>
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="deleteRangePoints"
            >{{ $t('common._common.delete') }}</el-button
          >
        </div>
      </div>
    </el-dialog>
    <!-- dataPoint dialog deletion end -->
    <saved-reg-report :visibility.sync="showSavedReport"></saved-reg-report>
  </div>
</template>

<script>
import AnalayticsFilter from 'pages/energy/analytics/components/AnalayticsFilter'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import NewDateHelper from '@/mixins/NewDateHelper'
import FNewAnalyticModelHelper from 'src/pages/report/mixins/FNewAnalyticsModelHelper'
import SavedRegReport from 'pages/energy/analytics/components/SavedRegReport'
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import { getFieldValue } from 'util/picklist'

export default {
  props: [
    'reportObject',
    'reportConfig',
    'visibility',
    'enableFloatimngIcon',
    'analyticsConfig',
  ],
  mixins: [AnalyticsMixin],
  data() {
    return {
      regressionTypeList: [
        {
          name: 'single',
          displayName: this.$t('common._common.simple_linear_regression'),
        },
        {
          name: 'multiple',
          displayName: this.$t('common._common.multi_linear_regression'),
        },
      ],
      regressionType: 'single',
      weatherReadings: [],
      config: {
        firstPanel: {
          selectedTab: 'DIMENSION',
        },
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
        result: {
          dimension: {
            selectedDataPoints: [],
            dataPoints: [],
            summaryList: [],
            regressionPoints: [],
            addedDataPoints: [],
          },
          metrics: {
            selectedDataPoints: [],
            dataPoints: [],
            summaryList: [],
            regressionPoints: [],
            addedDataPoints: [],
          },
        },
      },
      selectorPanelShow: false,
      showSavedReport: false,
      weatherPanel: {
        show: false,
        weatherCategory: null,
      },
      quitReset: false,
      metricAggregation: [
        {
          label: 'AVG',
          value: 2,
        },
        {
          label: 'SUM',
          value: 3,
        },
        {
          label: 'MIN',
          value: 4,
        },
        {
          label: 'MAX',
          value: 5,
        },
      ],
      loading: true,
      readings: {
        categories: [],
        assets: {},
        categoryWithAssets: {},
        fields: {},
        categoryWithFields: {},
      },
      showQuickSearch: false,
      toggleMetricAggregation: false,
      quickSearchQuery: null,
      selectedBuildings: [],
      weatherModules: [
        {
          displayName: this.$t('common._common.live_weather'),
          moduleName: 'weather',
          selected: false,
        },
        {
          displayName: this.$t('common._common.daily_weather'),
          moduleName: 'weatherDaily',
          selected: false,
        },
        {
          displayName: this.$t('common._common.degree_days'),
          selected: false,
        },
        {
          displayName: this.$t('common._common.psychrometric'),
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
      firstTime: true,
      urlParams: {
        perPage: 50,
        page: 1,
        viewName: 'hidden-all',
      },
      default_building_id: null,
      selectedBuildingLabel: 'No Buildings',
    }
  },
  components: {
    AnalayticsFilter,
    SavedRegReport,
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),

    moreThanOneLocation() {
      let locationSet = new Set()
      if (
        this.config.result[this.config.firstPanel.selectedTab.toLowerCase()]
          .summaryList.length !== 0
      ) {
        for (let group of this.config.result[
          this.config.firstPanel.selectedTab.toLowerCase()
        ].summaryList) {
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
    getAvailablePeriods() {
      let operationOnId = this.analyticsConfig.dateFilter.operationOnId
      let avail = []
      let isRangeEnabled = true

      avail.push({
        name: 'High-res',
        value: 0,
        enable:
          operationOnId !== 6
            ? operationOnId !== 4 && isRangeEnabled
            : true && isRangeEnabled,
      })

      avail.push({
        name: 'Hourly',
        value: 20,
        enable: operationOnId !== 6 ? operationOnId !== 4 : true,
      })

      avail.push({
        name: 'Daily',
        value: 12,
        enable: true,
      })

      avail.push({
        name: 'Weekly',
        value: 11,
        enable: true,
      })

      avail.push({
        name: 'Monthly',
        value: 10,
        enable: true,
      })

      avail.push({
        name: 'Quarterly',
        value: 25,
        enable: operationOnId === 4 || operationOnId === 5,
      })

      avail.push({
        name: 'Yearly',
        value: 8,
        enable: operationOnId === 4,
      })

      avail.push({
        name: 'Hour of day', // 12, 1, 2, 3
        value: 19,
        enable: true,
      })

      avail.push({
        name: 'Day of week', // sun, mon
        value: 17,
        enable: true,
      })

      avail.push({
        name: 'Day of month', // 1,2,3
        value: 18,
        enable: true,
      })

      return avail
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
    showFourthPanel() {
      if (
        this.config.result[this.config.firstPanel.selectedTab.toLowerCase()]
          .summaryList.length === 0
      ) {
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
          let { data, error } = await getFieldValue({
            lookupModuleName: 'building',
            selectedOptionId: [this.selectedBuildings[0]],
          })
          if (!error && data) {
            let value = this.$getProperty(data, '0.label')
            this.selectedBuildingLabel = value
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
    analyticsConfig: {
      handler: function(newVal, oldVal) {
        if (
          newVal.regressionConfig &&
          this.checkForEmpty() &&
          this.readings &&
          this.allFields.weather &&
          this.allFields.assets
        ) {
          this.populateSelector()
        }
      },
      deep: true,
    },
    regressionType: function(newVal, oldVal) {
      if (this.quitReset === false && oldVal) {
        if (newVal != oldVal) {
          this.closeAllPanels()
          delete this.config.regressionPoints
          Object.assign(
            this.$data.config.mainPanel,
            this.$options.data().config.mainPanel
          )
          Object.assign(
            this.$data.config.result,
            this.$options.data().config.result
          )
          this.regressionType = newVal
          this.$emit('resetState', true)
        }
      }
    },
    buildingIds: function(newVal, oldVal) {
      if (JSON.stringify(newVal) !== JSON.stringify(oldVal)) {
        this.loadAssetCategories()
        this.resetResetReadings()
      }
    },
    selectedBuildings: function(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.config.selectReadingsPanel.spaceObj = null
        this.weatherModules.forEach(points => {
          points.selected = false
        })
        if (this.weatherReadings.length) {
          this.weatherReadings = []
        }
        this.selectDefaultTab()
      }
    },
    selectedBuildingName: async function(newVal) {
      await this.selectedBuildingName
      this.$emit('selectBuildings', {
        name: this.selectedBuildingLabel,
        ids: this.selectedBuildings,
      })
    },
    getAvailablePeriods: {
      handler(newData, oldData) {
        let avail = this.getAvailablePeriods
        let selected = avail.find(
          a => a.value === this.analyticsConfig.period && a.enable
        )
        if (!selected) {
          let filterName = this.analyticsConfig.dateFilter.operationOn

          let defaultPeriod = avail.filter(a => a.enable)[0].value
          if (filterName === 'week') {
            defaultPeriod = 12
          } else if (filterName === 'month') {
            defaultPeriod = 12
          } else if (filterName === 'year') {
            defaultPeriod = 10
          }
          this.analyticsConfig.period = defaultPeriod
        }
      },
      immediate: true,
    },
    'config.result.dimension.selectedDataPoints': {
      handler: function(newVal, oldVal) {
        if (
          newVal.length !== 0 &&
          (this.savedPoints === null || typeof this.savedPoints === 'undefined')
        ) {
          this.addToSummaryListNew('dimension')
        }
      },
      deep: true,
    },
    'config.result.metrics.selectedDataPoints': {
      handler: function(newVal, oldVal) {
        if (
          newVal.length !== 0 &&
          (this.savedPoints === null || typeof this.savedPoints === 'undefined')
        ) {
          this.addToSummaryListNew('metrics')
          this.getSelectedPointsMeta()
        }
      },
      deep: true,
    },
  },
  created() {
    Promise.all([
      this.$store.dispatch('loadAssetCategory'),
      this.loadDefaultBuilding(),
    ]).then(() => {
      this.setDefaultSelection()
      this.preLoadWeatherReadings()
    })
  },
  methods: {
    async loadDefaultBuilding() {
      let { data, error } = await API.get(
        '/v3/picklist/building',
        this.urlParams,
        { force: true }
      )
      if (!error && data?.pickList?.length > 0) {
        this.default_building_id = data.pickList[0].value
      }
    },
    checkForEmpty() {
      if (
        this.config.result.dimension.selectedDataPoints.length === 0 &&
        this.config.result.metrics.selectedDataPoints.length === 0
      ) {
        return true
      }
      return false
    },
    checkForChangedDataPoints(newAnalyticConfig) {
      let diff = false
      if (
        newAnalyticConfig &&
        newAnalyticConfig.dataPoints &&
        newAnalyticConfig.dataPoints.length !== 0
      ) {
        let dataPointList = this.getAllDataPoints()

        if (dataPointList.length === 0) {
          return true
        } else if (dataPointList.length !== 0) {
          for (let dp of this.analyticsConfig.dataPoints) {
            if (!dataPointList.includes(dp.yAxis.fieldId + '_' + dp.parentId)) {
              diff = true
              break
            } else {
              continue
            }
          }
        }
      }
      return diff
    },
    getAllDataPoints() {
      let dataPoints = []
      if (
        this.config.result.dimension.selectedDataPoints.length != 0 &&
        this.config.result.metrics.selectedDataPoints.length !== 0
      ) {
        dataPoints.push(...this.config.result.metrics.selectedDataPoints)
        dataPoints.push(...this.config.result.dimension.selectedDataPoints)
      }
      return dataPoints
    },
    populateSelector() {
      if (
        this.analyticsConfig &&
        this.analyticsConfig.regressionConfig &&
        this.analyticsConfig.regressionConfig.length !== 0
      ) {
        this.quitReset = true
        this.regressionType = this.analyticsConfig.regressionType
        let metricDataPoints = []
        let dimensionDataPoints = []

        metricDataPoints.push(
          this.analyticsConfig.regressionConfig[0].yAxis.readingId +
            '_' +
            this.analyticsConfig.regressionConfig[0].yAxis.parentId
        )
        for (let config of this.analyticsConfig.regressionConfig) {
          for (let xPoint of config.xAxis) {
            let idenString = ''
            idenString = xPoint.readingId + '_' + xPoint.parentId
            dimensionDataPoints.push(idenString)
          }
        }

        let result = {
          dimension: {
            selectedDataPoints: [],
            dataPoints: [],
            summaryList: [],
            regressionPoints: [],
            addedDataPoints: [],
          },
          metrics: {
            selectedDataPoints: [],
            dataPoints: [],
            summaryList: [],
            regressionPoints: [],
            addedDataPoints: [],
          },
        }

        result.dimension.selectedDataPoints = dimensionDataPoints
        result.metrics.selectedDataPoints = metricDataPoints

        this.config.result = result
        this.populateAddedDataPoints(this.config)
        this.getSelectedPointsMeta()
      }
    },
    deleteDataPoint(dataPointIndex) {
      if (this.config.result.dimension.selectedDataPoints.length !== 0) {
        this.config.result.dimension.selectedDataPoints.splice(
          dataPointIndex,
          1
        )
        if (this.config.result.dimension.selectedDataPoints.length === 0) {
          this.config.result.metrics.selectedDataPoints = []
        }
        this.apply()
      }
    },
    setAggr(index, value, mode) {
      this.config.result[mode].dataPoints[index].yAxis.aggr = value
      this.config.result[mode].regressionPoints[index].aggr = value
      this.updateAndEmitData()
    },
    preLoadWeatherReadings() {
      this.loadWeatherReadings()
    },
    selectDefaultTab(mode) {
      this.config.assetPointsPanel = {
        show: false,
        activeSubTab: 'points',
        data: null,
        loading: true,
        selectedAssetOrPoint: null,
        filterQuery: '',
      }
      this.weatherPanel.weatherCategory = null
      this.config.selectAssetsPanel.show = false
      this.config.selectReadingsPanel.show = false
      this.weatherModules.forEach(points => {
        points.selected = false
      })
      if (mode === 'DIMENSION') {
        this.weatherPanel.show = true
        this.config.mainPanel.currentModule = ''
      } else {
        this.weatherPanel.show = false
        this.loadAssetCategories('METRICS')
      }
    },
    removedataPoint(index) {
      let points = this.config.result.metrics.selectedDataPoints[index]
      this.config.result.metrics.dataPoints.splice(index, 1)
      this.config.result.metrics.selectedDataPoints.splice(index, 1)
      this.config.result.metrics.regressionPoints.splice(index, 1)
      this.updateAndEmitData()
    },
    onPeriodChange() {
      if (!this.analyticsConfig.dataPoints.length) {
        if (this.analyticsConfig.period === 20) {
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            31
          )
        } else if (this.analyticsConfig.period === 12) {
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            28
          )
        } else if (this.analyticsConfig.period === 10) {
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            44
          )
        } else {
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            22
          )
        }
      }
    },
    closeAllPanels() {
      this.config.assetPointsPanel.show = false
      this.config.selectAssetsPanel.show = true
      this.config.selectReadingsPanel.show = false
    },
    updateAndEmitData() {
      let data = []
      if (
        this.config.result.dimension.regressionPoints.length != 0 &&
        this.config.result.metrics.regressionPoints.length != 0
      ) {
        if (
          this.regressionType ===
          FNewAnalyticModelHelper.regressionTypes().SINGLE
        ) {
          this.config[
            'regressionType'
          ] = FNewAnalyticModelHelper.regressionTypes().SINGLE
          let yAxis = this.config.result.metrics.regressionPoints[0]
          this.config.result.dimension.regressionPoints.forEach(function(
            xAxis
          ) {
            data.push({
              xAxis: [xAxis],
              yAxis: yAxis,
              groupAlias: null,
              isMultiple: false,
            })
          })
        } else {
          this.config[
            'regressionType'
          ] = FNewAnalyticModelHelper.regressionTypes().MULTIPLE
          let xAxis = []
          let yAxis = this.config.result.metrics.regressionPoints[0]
          for (let point of this.config.result.dimension.regressionPoints) {
            xAxis.push(point)
          }
          data.push({
            xAxis: xAxis.length ? xAxis : null,
            yAxis: yAxis,
            groupAlias: null,
            isMultiple: true,
          })
        }
      }

      this.config.regressionPoints = data

      this.$emit('updateDataPoints', this.config)
    },
    getLabelFromDataPoints(label) {
      let split = label.split('_')
      let parentId = parseInt(split[split.length - 1])
      let readingId = parseInt(split[0])

      for (let dataPoint of this.analyticsConfig.dataPoints) {
        if (
          dataPoint.parentId === parentId &&
          dataPoint.yAxis.fieldId === readingId &&
          dataPoint.name
        ) {
          return dataPoint.name
        } else {
          continue
        }
      }
      return null
    },
    getFormatedData(label, mode) {
      let text = ''
      if (this.analyticsConfig.dataPoints.length !== 0) {
        text = this.getLabelFromDataPoints(label)
      }
      if (text === '' || text === null || typeof text === 'undefined') {
        text = ''
        if (label && mode) {
          if (mode === 'metrics') {
            this.config.result[mode].addedDataPoints.forEach(function(points) {
              if (
                points.resourceId === parseInt(label.split('_')[1]) &&
                points.readingId === parseInt(label.split('_')[0])
              ) {
                let readingLabel = points.readingLabel
                  ? points.readingLabel
                  : points.label
                let resourceLabel = points.resourceLabel
                  ? points.resourceLabel
                  : points.location
                if (resourceLabel) {
                  text = resourceLabel + ' ( ' + readingLabel + ' )'
                } else {
                  text = readingLabel
                }
              }
            })
          } else {
            let parentId = parseInt(label.split('_')[1])
            let readingId = parseInt(label.split('_')[0])

            for (let point of this.config.result.dimension.summaryList) {
              for (let reading of point.readings) {
                if (
                  reading.readingId === readingId &&
                  reading.resourceId === parentId
                ) {
                  let resourceLabel = reading.resourceLabel
                    ? reading.resourceLabel
                    : reading.location
                    ? reading.location
                    : ''
                  let readingLabel = reading.readingLabel
                    ? reading.readingLabel
                    : reading.label
                  if (resourceLabel) {
                    text = text + resourceLabel + '( ' + readingLabel + ' )'
                  } else {
                    text = readingLabel
                  }
                }
              }
            }
          }
        }
      }

      return text
    },
    showMetricAggregation(metric) {
      return true
    },
    selectDataPoint(datapoint, mode) {
      this.config.result[mode].selectedDataPoints = []
      this.config.result[mode].selectedDataPoints.push(datapoint)
    },
    checkForRangePoints(newVal) {
      if (
        this.reportObject &&
        this.config.result[this.config.firstPanel.selectedTab.toLowerCase()]
          .summaryList.length !== 0
      ) {
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
    getAllSummaryDataPoints(mode) {
      let allDataPoints = []
      let selector = mode || this.config.firstPanel.selectedTab.toLowerCase()
      if (this.config.result[selector].summaryList) {
        for (let key in this.config.result[selector].summaryList) {
          allDataPoints.push(
            ...this.config.result[selector].summaryList[key].readings
          )
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
      let existingDataPoints = [
        ...this.config.result.dimension.selectedDataPoints,
      ]
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
        this.config.result.dimension.selectedDataPoints = existingDataPoints
        this.addToSummaryListNew(
          this.config.firstPanel.selectedTab.toLowerCase()
        )
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
    addAssetToGroup(label, reading, assetCategory) {
      if (
        this.config.result[this.config.firstPanel.selectedTab.toLowerCase()]
          .summaryList &&
        this.config.result[this.config.firstPanel.selectedTab.toLowerCase()]
          .summaryList.length === 0
      ) {
        let temp = {}
        temp['label'] = label
        temp['categoryId'] = assetCategory
        temp['readings'] = []
        temp.readings.push(reading)
        this.config.result[
          this.config.firstPanel.selectedTab.toLowerCase()
        ].summaryList.push(temp)
      } else if (
        this.config.result[this.config.firstPanel.selectedTab.toLowerCase()]
          .summaryList &&
        this.config.result[this.config.firstPanel.selectedTab.toLowerCase()]
          .summaryList.length > 0
      ) {
        let readingGroup = this.config.result[
          this.config.firstPanel.selectedTab.toLowerCase()
        ].summaryList.filter(group => group.label === label)
        if (readingGroup.length !== 0) {
          readingGroup[0].readings.push(reading)
        } else {
          let temp = {}
          temp['label'] = label
          temp['categoryId'] = assetCategory
          temp['readings'] = []
          temp.readings.push(reading)
          this.config.result[
            this.config.firstPanel.selectedTab.toLowerCase()
          ].summaryList.push(temp)
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
      let readingGroup = this.config.result[
        this.config.firstPanel.selectedTab.toLowerCase()
      ].summaryList[readingGroupIndex]
      let readingSumId = reading.readingId + '_' + reading.resourceId
      if (this.config.firstPanel.selectedTab === 'DIMESION') {
        this.config.result.dimension.selectedDataPoints.splice(
          this.config.result.dimension.selectedDataPoints.indexOf(readingSumId),
          1
        )
      } else {
        this.config.result.metrics.selectedDataPoints.splice(
          this.config.result.metrics.selectedDataPoints.indexOf(readingSumId),
          1
        )
      }
      let readingIndex = readingGroup.readings.indexOf(reading)
      readingGroup.readings.splice(readingIndex, 1)
      if (readingGroup.readings.length === 0) {
        this.config.result[
          this.config.firstPanel.selectedTab.toLowerCase()
        ].summaryList.splice(readingGroupIndex, 1)
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

    addToSummaryListNew(source) {
      let existingReadings = []
      let points = []
      let self = this
      let panel = source
        ? source
        : self.config.firstPanel.selectedTab.toLowerCase()
      if (
        this.config.result[panel].selectedDataPoints &&
        this.config.result[panel].selectedDataPoints.length !== 0
      ) {
        for (let point of this.config.result[panel].selectedDataPoints) {
          let temp = {
            point: point,
            isDone: false,
          }
          points.push(temp)
        }

        if (this.config.result[panel].summaryList.length !== 0) {
          this.currentKey = this.currentKey + 1
          for (let group of this.config.result[panel].summaryList) {
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
            if (category && category !== '' && this.allFields[category]) {
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
              for (let group of this.config.result[panel].summaryList) {
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
                if (readingObject) {
                  categoryLabel = this.weatherModules.filter(
                    wModule =>
                      wModule && wModule.moduleName === readingObject.module
                  )
                }
                if (!categoryLabel || categoryLabel.length === 0) {
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

            let existingGroup = self.config.result[panel].summaryList.filter(
              inGroup => inGroup.label === categoryLabel
            )
            if (
              self.config.result[panel].summaryList.length === 0 ||
              existingGroup.length === 0
            ) {
              let reading = {}

              reading['readingId'] = parseInt(readingSplit[0])
              reading['readingLabel'] = readingObject
                ? readingObject.displayName
                : ''
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
              this.config.result[panel].summaryList.push(temp)
            } else {
              let reading = {}

              reading['readingId'] = parseInt(readingSplit[0])
              reading['readingLabel'] = readingObject
                ? readingObject.displayName
                : ''
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
      for (let group of this.config.result[
        this.config.firstPanel.selectedTab.toLowerCase()
      ].summaryList) {
        for (let reading of group.readings) {
          if (reading && reading.marked !== this.currentKey) {
            let readingIndex = group.readings.indexOf(reading)
            group.readings.splice(readingIndex, 1)
          }
          if (group.readings.length === 0) {
            this.config.result[
              this.config.firstPanel.selectedTab.toLowerCase()
            ].summaryList.splice(
              this.config.result[
                this.config.firstPanel.selectedTab.toLowerCase()
              ].summaryList.indexOf(group),
              1
            )
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
      this.config.result[
        this.config.firstPanel.selectedTab.toLowerCase()
      ].selectedDataPoints.splice(index, 1)
    },

    togglePanel(...panels) {
      let self = this
      let configKeys = Object.keys(self.config)
      for (let configKey of configKeys) {
        if (configKey !== 'regressionType') {
          if (panels.indexOf(configKey) >= 0) {
            self.config[configKey].show = true
          } else {
            self.config[configKey].show = false
            self.config[configKey].selectedItem = null
          }
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

      self.togglePanel('assetPointsPanel', 'selectAssetsPanel')
      self.config.selectAssetsPanel.loading = true
      self.config.selectAssetsPanel.readingId = pointId

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
              if (el.type === 3) {
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

    async loadWeatherReadings(weatherModule) {
      let self = this
      self.weatherPanel.weatherCategory = weatherModule
        ? weatherModule
        : self.weatherModules[0]
      for (let module of self.weatherModules) {
        module.selected = false
      }
      if (this.weatherReadings.length) {
        self.config.selectReadingsPanel.loading = false
      } else {
        self.config.selectReadingsPanel.loading = true
      }
      self.config.mainPanel.currentModule = weatherModule
        ? weatherModule.moduleName
        : self.weatherModules[0].moduleName
      self.togglePanel('selectReadingsPanel')
      if (weatherModule) {
        weatherModule.selected = true
      }

      let fetchWeatherReadings = async siteId => {
        let response = await self.$http.get(
          '/reading/getspacespecificreadings?excludeForecastReadings=false&parentId=' +
            siteId
        )

        self.weatherReadings = response.data
        if (weatherModule) {
          self.config.selectReadingsPanel.data = self.weatherModuleHandler(
            weatherModule,
            self.getReadingFields(response.data)
          )
        } else {
          self.config.selectReadingsPanel.data = self.weatherModuleHandler(
            self.weatherModules[0],
            self.getReadingFields(response.data)
          )
        }
        self.allFields['weather'] = self.convertToList(
          self.config.selectReadingsPanel.data
        )
        self.config.selectReadingsPanel.loading = false
      }
      if (
        self.config.selectReadingsPanel.spaceObj &&
        self.config.selectReadingsPanel.spaceObj.siteId
      ) {
        self.config.selectReadingsPanel.parentId =
          self.config.selectReadingsPanel.spaceObj.siteId
        if (self.weatherReadings.length) {
          if (weatherModule) {
            self.config.selectReadingsPanel.data = self.weatherModuleHandler(
              weatherModule,
              self.getReadingFields(self.weatherReadings)
            )
          } else {
            self.config.selectReadingsPanel.data = self.weatherModuleHandler(
              self.weatherModules[0],
              self.getReadingFields(self.weatherReadings)
            )
          }
          self.allFields['weather'] = self.convertToList(
            self.config.selectReadingsPanel.data
          )
          self.config.selectReadingsPanel.loading = false
        } else {
          fetchWeatherReadings(self.config.selectReadingsPanel.spaceObj.siteId)
        }
      } else {
        let response = await self.$http.get('/building/' + self.buildingIds[0])

        if (response.data.building) {
          let siteId = response.data.building.siteId
          self.config.selectReadingsPanel.parentId = siteId
          self.config.selectReadingsPanel.spaceObj = response.data.building
          fetchWeatherReadings(siteId)
        } else {
          self.$http
            .get('/zone/' + self.buildingIds[0])
            .then(function(response) {
              if (response.data.zone) {
                let siteId = response.data.zone.siteId
                self.config.selectReadingsPanel.parentId = siteId
                self.config.selectReadingsPanel.spaceObj =
                  response.data.building
                fetchWeatherReadings(siteId)
              }
            })
        }
      }
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
      // remove not added data points
      if (
        this.config.result.metrics.selectedDataPoints.length >
        this.config.result.metrics.dataPoints.length
      ) {
        let length =
          this.config.result.metrics.selectedDataPoints.length -
          this.config.result.metrics.dataPoints.length
        this.config.result.metrics.selectedDataPoints.splice(
          this.config.result.metrics.dataPoints.length - 1,
          length
        )
      }
      this.selectorPanelShow = false
    },

    apply() {
      let config = this.$helpers.cloneObject(this.config)
      this.config.result.dimension.dataPoints = []
      this.config.result.metrics.dataPoints = []
      this.config.result.dimension.regressionPoints = []
      this.config.result.metrics.regressionPoints = []

      this.populateAddedDataPoints(config)

      if (
        this.selectedBuildings.length !== 0 &&
        this.selectedBuildingLabel !== ''
      ) {
        this.config.result.meta = {}
        this.config.result.meta['selectedBuildingIds'] = this.selectedBuildings
        this.config.result.meta[
          'selectedBuildingTitle'
        ] = this.selectedBuildingLabel
      }
      this.$emit('update:enableFloatimngIcon', true)
      this.getSelectedPointsMeta()
      this.updateAndEmitData()

      this.selectorPanelShow = false
    },
    populateAddedDataPoints(config) {
      if (
        this.config.result.dimension.selectedDataPoints &&
        this.config.result.dimension.selectedDataPoints.length
      ) {
        for (let dp of this.config.result.dimension.selectedDataPoints) {
          let readingFieldId = parseInt(dp.split('_')[0])
          let parentId = parseInt(dp.split('_')[1])

          this.config.result.dimension.dataPoints.push({
            parentId: parentId,
            yAxis: {
              fieldId: readingFieldId,
              aggr: this.setAggrPoint(
                dp,
                'dimension',
                config,
                this.readings.fields[readingFieldId]
              ),
            },
            prediction:
              this.readings &&
              this.readings.fields &&
              this.readings.fields[readingFieldId] &&
              this.readings.fields[readingFieldId].module
                ? this.readings.fields[readingFieldId].module.type === 11
                : false,
            duplicateDataPoint: false,
          })
          this.config.result.dimension.regressionPoints.push({
            parentId: parentId,
            aggr: this.setAggrPoint(
              dp,
              'dimension',
              config,
              this.readings.fields[readingFieldId]
            ),
            readingId: readingFieldId,
            axis: 'x',
          })
        }
      }
      if (
        this.config.result.metrics.selectedDataPoints &&
        this.config.result.metrics.selectedDataPoints.length
      ) {
        for (let dp of this.config.result.metrics.selectedDataPoints) {
          let readingFieldId = parseInt(dp.split('_')[0])
          let parentId = parseInt(dp.split('_')[1])

          this.config.result.metrics.dataPoints.push({
            parentId: parentId,
            displayName: this.readings.fields[readingFieldId]
              ? this.readings.fields[readingFieldId].displayName
              : '',
            yAxis: {
              fieldId: readingFieldId,
              aggr: this.setAggrPoint(
                dp,
                'metrics',
                config,
                this.readings.fields[readingFieldId]
              ),
            },
            prediction:
              this.readings &&
              this.readings.fields &&
              this.readings.fields[readingFieldId] &&
              this.readings.fields[readingFieldId].module
                ? this.readings.fields[readingFieldId].module.type === 11
                : false,
          })
          this.config.result.metrics.regressionPoints.push({
            parentId: parentId,
            aggr: this.setAggrPoint(
              dp,
              'metrics',
              config,
              this.readings.fields[readingFieldId]
            ),
            readingId: readingFieldId,
            axis: 'y',
          })
        }
      }
    },

    getSelectedPointsMeta() {
      let points = []
      let metricPoints = []
      let self = this
      if (this.config.result.dimension.dataPoints.length) {
        this.config.result.dimension.dataPoints.forEach(function(point) {
          if (self.config.result.dimension.summaryList.length) {
            self.config.result.dimension.summaryList.forEach(function(summary) {
              if (summary.readings.length) {
                summary.readings.forEach(function(data) {
                  if (
                    data.resourceId === point.parentId &&
                    data.readingId === point.yAxis.fieldId
                  ) {
                    data.label = summary.label
                    points.push(data)
                  }
                })
              }
            })
          }
        })
      }
      this.config.result.dimension.addedDataPoints = points
      if (this.config.result.metrics.dataPoints.length) {
        this.config.result.metrics.dataPoints.forEach(function(point) {
          if (self.config.result.metrics.summaryList.length) {
            self.config.result.metrics.summaryList.forEach(function(summary) {
              if (summary.readings.length) {
                summary.readings.forEach(function(data) {
                  if (
                    data.resourceId === point.parentId &&
                    data.readingId === point.yAxis.fieldId
                  ) {
                    data.label = summary.label
                    metricPoints.push(data)
                  }
                })
              }
            })
          }
        })
      }
      this.config.result.metrics.addedDataPoints = metricPoints
    },
    checkAndReduceRangeTraces(dataPoints) {
      let finalDataPoints = []
      let keyAndDataPoints = {}
      for (let dP of dataPoints) {
        if (keyAndDataPoints.hasOwnProperty(dP.fieldId + '.' + dP.parentId)) {
          keyAndDataPoints[dP.fieldId + '.' + dP.parentId].push(dP)
        } else {
          keyAndDataPoints[dP.fieldId + '.' + dP.parentId] = []
          keyAndDataPoints[dP.fieldId + '.' + dP.parentId].push(dP)
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
    setAggrPoint(dp, mode, config, readingFields) {
      let aggr =
        readingFields &&
        readingFields.unit &&
        ['currency', 'kwh', 'co2', 'kg', 'mwh'].includes(
          readingFields.unit.trim().toLowerCase()
        )
          ? 3
          : 2
      if (dp && config.result[mode].dataPoints.length) {
        let resourceId = parseInt(dp.split('_')[1])
        let readingId = parseInt(dp.split('_')[0])
        config.result[mode].dataPoints.forEach(point => {
          if (
            point.parentId === resourceId &&
            point.yAxis &&
            point.yAxis.fieldId === readingId
          ) {
            aggr = point.yAxis.aggr
          }
        })
      }
      return aggr
    },
    setInitialValues(dataPoints, buildings) {
      let finalDataPoints = this.checkAndReduceRangeTraces(dataPoints)
      if (
        finalDataPoints.length !==
        this.config.result.dimension.selectedDataPoints.length
      ) {
        this.config.result.dimension.selectedDataPoints = []
        finalDataPoints.forEach(dp => {
          if (dp.type !== 2 && !dp.duplicateDataPoint) {
            this.config.result.dimension.selectedDataPoints.push(
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
.newregression-analysis .building-analysis-point-container {
  border-left: none;
}
.newregression-analysis .reading-apply-container {
}
.regression-icon {
  padding-top: 5px;
  color: #39b2c2;
  font-size: 15px;
  font-weight: 900;
  position: absolute;
  right: 20px;
}
.newregression-analysis .asset-list-container .asset-list-block-txt {
  display: inherit;
  position: relative;
}
.regeression0-ananlysis {
  top: 0;
}
.regression-analytics-container .newanalytics-sidebar:nth-child(2),
.regression-analytics-container .newanalytics-sidebar:nth-child(4) {
  border-left: 1px solid transparent;
}
.regression-right-icon {
  position: absolute;
  right: 10px;
  top: 3px;
}
.data-point-delete {
  position: absolute;
  right: 3px;
  top: 15px;
  float: right;
}
.regression-metric-equation {
  width: 32px;
  padding-left: 10px;
  padding-right: 10px;
}
.datapoint-selector-active {
  border-color: #39b2c2;
}
.regeression0-ananlysis .sidebar-close-btn.pointer {
  right: -36px !important;
}
.saved-regressions-link {
  width: 100%;
  left: 0;
  right: 0;
  border-top: 1px solid #f4f4f4;
  position: absolute;
  bottom: 20px;
  background-color: #fff;
  font-size: 13px;
  letter-spacing: 0.4px;
  color: #46a2bf;
  cursor: pointer;
  text-align: center;
  padding: 15px;
}
</style>
