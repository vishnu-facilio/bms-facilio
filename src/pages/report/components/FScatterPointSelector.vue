<template>
  <div class="newregression-analysis" v-if="visibility">
    <div>
      <div class="scatter-main-con">
        <div
          class="building-analysis-sidebar-conatainer regression-analytics-container"
        >
          <el-row class="mT20">
            <el-col :span="22">
              <div class="fc-text-pink">SERIES</div>
            </el-col>
            <el-col :span="2" v-if="enableAddSeries">
              <div @click="addSeries()" class="pointer z-20">
                <inline-svg
                  src="svgs/add-circled"
                  class="vertical-middle"
                  iconClass="icon icon-md icon-add mT5"
                ></inline-svg>
              </div>
            </el-col>
          </el-row>
          <div v-for="(group, idx) of config.result" :key="idx">
            <div class="" style="box-shadow: none;">
              <div class="">
                <div class=" pB20 pL10 pR10 series-box">
                  <el-row>
                    <el-col :span="config.result.length > 1 ? 22 : 24">
                      <div class="label-txt-black f11 bold pT10">
                        SERIES {{ idx + 1 }}
                      </div>
                    </el-col>
                    <el-col v-if="config.result.length > 1" :span="2">
                      <div class="mT5" @click="deleteSeries(idx)">
                        <span class="flRight">
                          <i class="el-icon-more pointer f14 fc-grey6"></i>
                        </span>
                      </div>
                    </el-col>
                  </el-row>
                  <el-row class="pT20">
                    <el-col :span="24">
                      <div class="label-txt-black pB10">
                        Dimension
                      </div>

                      <div
                        v-if="!group.metrics.selectedDataPoints.length"
                        class="fc-input-div-full-border-f14 position-relative"
                        v-bind:class="{
                          active: config.firstPanel.selectedTab === 'METRICS',
                        }"
                        @click="
                          config.firstPanel.selectedTab = 'METRICS'
                          selectorPanelShow = true
                          currentIndex = idx
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
                        v-for="(data, index) in group.metrics
                          .selectedDataPoints"
                        v-else
                      >
                        <el-row :key="index" class="visibility-visible-actions">
                          <el-col
                            :span="
                              analyticsConfig && analyticsConfig.period
                                ? 20
                                : 24
                            "
                          >
                            <div
                              class="fc-input-div-full-border-f14"
                              @click="
                                config.firstPanel.selectedTab = 'METRICS'
                                selectDefaultTab('METRICS')
                                selectorPanelShow = true
                                currentIndex = idx
                                $emit('update:enableFloatimngIcon', false)
                              "
                            >
                              <span>{{
                                getFormatedData(data, 'metrics', idx)
                              }}</span>
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
                                      group['metrics'].dataPoints[index] &&
                                      metricAggr.value ===
                                        group['metrics'].dataPoints[index].yAxis
                                          .aggr,
                                  }"
                                  v-for="(metricAggr,
                                  metricAggrIdx) in metricAggregation"
                                  :value="metricAggr.value"
                                  :key="metricAggrIdx"
                                  @click="
                                    setAggr(
                                      index,
                                      metricAggr.value,
                                      'metrics',
                                      idx
                                    )
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
                        <div class="label-txt-black pB10">
                          Metric
                        </div>
                        <div
                          v-if="group.dimension.selectedDataPoints.length"
                          class="fc-dark-blue2-14 pointer pB10"
                          style="text-align:right;"
                          @click="
                            config.firstPanel.selectedTab = 'DIMENSION'
                            selectDefaultTab('DIMENSION')
                            selectorPanelShow = true
                            currentIndex = idx
                            $emit('update:enableFloatimngIcon', false)
                          "
                        >
                          <!-- Add -->
                        </div>
                      </div>
                      <div
                        v-if="!group.dimension.selectedDataPoints.length"
                        class="fc-input-div-full-border-f14 position-relative clearboth"
                        @click="
                          config.firstPanel.selectedTab = 'DIMENSION'
                          selectDefaultTab('DIMENSION')
                          selectorPanelShow = true
                          currentIndex = idx
                          $emit('update:enableFloatimngIcon', false)
                        "
                      >
                        <span>{{ $t('common._common.select') }}</span>
                        <i
                          class=" pointer mT12 el-icon-arrow-right pull-right regression-right-icon"
                        ></i>
                      </div>
                      <template
                        v-for="(data1, index1) in group.dimension
                          .selectedDataPoints"
                        v-else
                      >
                        <el-row
                          :key="index1"
                          class="visibility-visible-actions"
                        >
                          <el-col
                            :span="
                              group.dimension.selectedDataPoints.length &&
                              analyticsConfig &&
                              analyticsConfig.period
                                ? 20
                                : 24
                            "
                          >
                            <div
                              class="fc-input-div-full-border-f14 position-relative"
                            >
                              <span>{{
                                getFormatedData(data1, 'dimension', idx)
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
                                      group['dimension'].dataPoints[index1] &&
                                      metricAggr.value ===
                                        group['dimension'].dataPoints[index1]
                                          .yAxis.aggr,
                                  }"
                                  v-for="(metricAggr,
                                  metricAggrIdx) in metricAggregation"
                                  :value="metricAggr.value"
                                  :key="metricAggrIdx"
                                  @click="
                                    setAggr(
                                      index1,
                                      metricAggr.value,
                                      'dimension',
                                      idx
                                    )
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
                          <!-- <el-col
                            class="mT10"
                            style="text-align:right;"
                            :span="2"
                          >
                            <i
                              @click="deleteDataPoint(index1, idx)"
                              class="el-icon-delete pointer fc-red3 f16 visibility-hide-actions"
                              style="vertical-align: sub;"
                            ></i>
                          </el-col> -->
                        </el-row>
                      </template>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </div>
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
              <div v-if="!buildingIds || !buildingIds.length">
                No buildings selected.
              </div>
              <div
                class="newanalytics-sidebar col-3"
                v-else-if="selectorPanelShow && currentIndex === idx && loading"
              >
                <spinner :show="loading"></spinner>
              </div>
              <template
                v-if="selectorPanelShow && currentIndex === idx && !loading"
              >
                <div
                  class="newanalytics-sidebar col-3"
                  v-if="
                    !loading && config.firstPanel.selectedTab === 'DIMENSION'
                  "
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
                          <!-- <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 32 32" class="search-icon4"><title>search</title><path d="M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"></path></svg> -->
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
                          @click="weatherPanelToggler"
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
                            @click="loadWeatherReadings(weatherModule, wIdx)"
                            v-for="(weatherModule, wIdx) in weatherModules"
                            :key="wIdx"
                          >
                            <i
                              v-bind:class="{
                                'fa fa-circle': true,
                                'dot-blue-icon':
                                  weatherModule.selected === true,
                                'dot-grey-icon':
                                  weatherModule.selected === false,
                              }"
                              aria-hidden="true"
                            ></i>
                            <div class="width80">
                              {{ weatherModule.displayName }}
                            </div>
                            <!-- <span class="active-arrow"><img v-if="weatherPanel.weatherCategory && (weatherModule.displayName === weatherPanel.weatherCategory.displayName)" src="~assets/arrow-pointing-to-right.svg" height="14px" width="14px"></span> -->
                          </div>
                        </div>
                      </div>
                    </div>
                    <div class="building-analysis-side-block">
                      <div class="building-hover-actions">
                        <div
                          class="sidebar-asset-H fL"
                          @click="loadSpaceReadings"
                        >
                          Spaces
                        </div>
                      </div>
                    </div>
                    <div class="building-analysis-side-block">
                      <div class="building-hover-actions">
                        <div
                          class="sidebar-asset-H fL"
                          @click="loadAssetCategories"
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
                                config.mainPanel.selectedAssetCategory !=
                                category.id,
                            }"
                            aria-hidden="true"
                          ></i>
                          <div class="width80">{{ category.displayName }}</div>
                          <!-- <span class="active-arrow"><img v-if="config.mainPanel.selectedAssetCategory === category.id" src="~assets/arrow-pointing-to-right.svg" height="14px" width="14px"></span> -->
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
                          @click="loadEnPIReadings"
                        >
                          EnPI
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
                          <!-- <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 32 32" class="search-icon4"><title>search</title><path d="M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"></path></svg> -->
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
                                config.mainPanel.selectedAssetCategory !=
                                category.id,
                            }"
                            aria-hidden="true"
                          ></i>
                          <div class="width80">{{ category.displayName }}</div>
                          <!-- <span class="active-arrow"><img v-if="config.mainPanel.selectedAssetCategory === category.id" src="~assets/arrow-pointing-to-right.svg" height="14px" width="14px"></span> -->
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
                        >
                          Spaces
                        </div>
                      </div>
                    </div>

                    <div class="building-analysis-side-block">
                      <div class="building-hover-actions">
                        <div
                          class="sidebar-asset-H fL"
                          @click="loadEnPIReadings"
                        >
                          EnPI
                        </div>
                      </div>
                    </div>

                    <div class="building-analysis-side-block">
                      <div class="building-hover-actions">
                        <div
                          class="sidebar-asset-H fL"
                          @click="weatherPanelToggler"
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
                            @click="loadWeatherReadings(weatherModule, wIdx)"
                            v-for="(weatherModule, wIdx) in weatherModules"
                            :key="wIdx"
                          >
                            <i
                              v-bind:class="{
                                'fa fa-circle': true,
                                'dot-blue-icon':
                                  weatherModule.selected === true,
                                'dot-grey-icon':
                                  weatherModule.selected === false,
                              }"
                              aria-hidden="true"
                            ></i>
                            <div class="width80">
                              {{ weatherModule.displayName }}
                            </div>
                            <!-- <span class="active-arrow"><img v-if="weatherPanel.weatherCategory && (weatherModule.displayName === weatherPanel.weatherCategory.displayName)" src="~assets/arrow-pointing-to-right.svg" height="14px" width="14px"></span> -->
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
                        openAssetCategory(
                          config.mainPanel.selectedAssetCategory
                        )
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
                            No points.
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
                              {{
                                item.displayName ? item.displayName : item.name
                              }}
                            </div>
                            <!-- <span class="active-arrow"><img v-if="config.assetPointsPanel.selectedAssetOrPoint === item.id" src="~assets/arrow-pointing-to-right.svg" height="14px" width="14px"></span> -->
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
                            No assets.
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
                            <!-- <span class="active-arrow"><img v-if="config.assetPointsPanel.selectedAssetOrPoint === item.id" src="~assets/arrow-pointing-to-right.svg" height="14px" width="14px"></span> -->
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
                          SELECT READINGS
                        </div>
                      </div>
                      <div
                        class="building-points-search building-points-height"
                      >
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
                          No data found.
                        </div>

                        <el-checkbox-group
                          v-model="group.dimension.selectedDataPoints"
                          @change="prepareSavedPointsSummaryList(idx)"
                          v-else
                          class="building-check-list"
                        >
                          <el-checkbox
                            v-for="(item, index) in filteredReadingsPanel"
                            :key="index"
                            :label="
                              item.id +
                                '_' +
                                config.selectReadingsPanel.parentId
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
                        group.dimension.selectedDataPoints &&
                          group.dimension.selectedDataPoints.length
                      "
                    >
                      <div class="building-analysis-points-wrap col-12">
                        <div class="building-analysis-points-btn fL col-4">
                          <el-button
                            type="button"
                            class="btn-grey-fill pL30 pR30"
                            @click="cancel"
                            >{{
                              $t('setup.users_management.cancel')
                            }}</el-button
                          >
                          <el-button
                            type="button"
                            class="btn-blue-fill pL30 pR30 mL15"
                            @click="apply(idx)"
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
                          SELECT READINGS
                        </div>
                      </div>
                      <div
                        class="building-points-search building-points-height"
                      >
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
                          No data found.
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
                                item.id +
                                  '_' +
                                  config.selectReadingsPanel.parentId,
                                'metrics',
                                idx
                              ),
                                prepareSavedPointsSummaryList(idx),
                                apply(idx)
                            "
                          >
                            {{
                              item.displayName ? item.displayName : item.name
                            }}
                            <i
                              class="el-icon-check regression-icon"
                              v-if="
                                group.metrics.selectedDataPoints[0] ===
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
                          SELECT ASSETS
                        </div>
                      </div>
                      <div
                        class="building-points-search building-points-height"
                      >
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
                          v-model="group.dimension.selectedDataPoints"
                          @change="prepareSavedPointsSummaryList(idx)"
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
                        group.dimension.selectedDataPoints &&
                          group.dimension.selectedDataPoints.length
                      "
                    >
                      <div class="building-analysis-points-wrap col-12">
                        <div></div>
                        <div class="building-analysis-points-btn fL col-4">
                          <el-button
                            type="button"
                            class="btn-grey-fill pL30 pR30"
                            @click="cancel"
                            >{{
                              $t('setup.users_management.cancel')
                            }}</el-button
                          >
                          <el-button
                            type="button"
                            class="btn-blue-fill pL30 pR30 mL15"
                            @click="apply(idx)"
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
                          SELECT ASSETS
                        </div>
                      </div>
                      <div
                        class="building-points-search building-points-height"
                      >
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

                        <div
                          class="asset-list-block-txt"
                          v-for="(item, index) in filteredAssetsPanel"
                          :key="index"
                          @click="
                            selectDataPoint(
                              config.selectAssetsPanel.readingId +
                                '_' +
                                item.id,
                              'metrics',
                              idx
                            ),
                              prepareSavedPointsSummaryList(idx),
                              apply(idx)
                          "
                        >
                          {{ item.displayName ? item.displayName : item.name }}
                          <i
                            class="el-icon-check regression-icon"
                            v-if="
                              group.metrics.selectedDataPoints[0] ===
                                config.selectAssetsPanel.readingId +
                                  '_' +
                                  item.id
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
          <!-- <div
            v-if="enableAddSeries"
            class=" pL20 fc-dark-blue2-12 pointer"
            @click="addSeries()"
          >
            Add Series
          </div> -->
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AnalayticsFilter from 'pages/energy/analytics/components/AnalayticsFilter'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import NewDateHelper from '@/mixins/NewDateHelper'
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
        result: [
          {
            dimension: {
              selectedDataPoints: [],
              dataPoints: [],
              summaryList: [],
              scatterPoints: [],
              addedDataPoints: [],
            },
            metrics: {
              selectedDataPoints: [],
              dataPoints: [],
              summaryList: [],
              scatterPoints: [],
              addedDataPoints: [],
            },
          },
        ],
      },
      selectorPanelShow: false,
      currentIndex: 0,
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
      firstTime: true,
      default_building_id: null,
      selectedBuildingLabel: 'No Buildings',
    }
  },
  components: {
    AnalayticsFilter,
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),

    enableAddSeries() {
      let enable = true
      for (let group of this.config.result) {
        if (
          !group.dimension.selectedDataPoints.length ||
          !group.metrics.selectedDataPoints.length
        ) {
          enable = false
          break
        }
      }
      return enable
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

      // avail.push({
      //   name:'Week of year', // W1, W2
      //   value: 16,
      //   enable: true
      // })

      // avail.push({
      //   name:'Month of year', // Jan, Feb , march
      //   value: 15,
      //   enable: true
      // })

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
          newVal.scatterConfig &&
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
  mounted() {
    this.populateSelector()
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
    prepareSavedPointsSummaryList(resultIndex) {
      if (
        this.savedPoints === null ||
        typeof this.savedPoints === 'undefined'
      ) {
        this.addToSummaryListNew(
          this.config.firstPanel.selectedTab.toLowerCase(),
          resultIndex
        )
      }
    },
    addSeries() {
      this.config.result.push({
        dimension: {
          selectedDataPoints: [],
          dataPoints: [],
          summaryList: [],
          scatterPoints: [],
          addedDataPoints: [],
        },
        metrics: {
          selectedDataPoints: [],
          dataPoints: [],
          summaryList: [],
          scatterPoints: [],
          addedDataPoints: [],
        },
      })
    },
    deleteSeries(idx) {
      this.config.result.splice(idx, 1)
    },
    checkForEmpty() {
      let empty = true
      for (let group of this.config.result) {
        if (
          group.dimension.selectedDataPoints.length ||
          group.metrics.selectedDataPoints.length
        ) {
          empty = false
          break
        }
      }
      return empty
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
      for (let group of this.config.result) {
        if (
          group.dimension.selectedDataPoints.length != 0 &&
          group.metrics.selectedDataPoints.length !== 0
        ) {
          dataPoints.push(...group.metrics.selectedDataPoints)
          dataPoints.push(...group.dimension.selectedDataPoints)
        }
      }
      return dataPoints
    },
    populateSelector() {
      if (
        this.analyticsConfig &&
        this.analyticsConfig.scatterConfig &&
        this.analyticsConfig.scatterConfig.length !== 0
      ) {
        this.quitReset = true
        let metricDataPoints = []
        let dimensionDataPoints = []

        metricDataPoints.push(
          this.analyticsConfig.scatterConfig[0].yAxis.readingId +
            '_' +
            this.analyticsConfig.scatterConfig[0].yAxis.parentId
        )
        let result = []
        for (let configIndex in this.analyticsConfig.scatterConfig) {
          let config = this.analyticsConfig.scatterConfig[configIndex]
          for (let xPoint of config.xAxis) {
            let idenString = ''
            idenString = xPoint.readingId + '_' + xPoint.parentId
            dimensionDataPoints.push(idenString)
          }
          result[configIndex] = {
            dimension: {
              selectedDataPoints: [],
              dataPoints: [],
              summaryList: [],
              scatterPoints: [],
              addedDataPoints: [],
            },
            metrics: {
              selectedDataPoints: [],
              dataPoints: [],
              summaryList: [],
              scatterPoints: [],
              addedDataPoints: [],
            },
          }
          result[configIndex].dimension.selectedDataPoints = dimensionDataPoints
          result[configIndex].metrics.selectedDataPoints = metricDataPoints
        }

        this.config.result = result
        for (let groupIndex in this.config.result) {
          this.populateAddedDataPoints(this.config, groupIndex)
          this.getSelectedPointsMeta(groupIndex)
        }
      } else {
        this.config.result = []
      }
    },
    deleteDataPoint(dataPointIndex, resultIndex) {
      if (
        this.config.result[resultIndex].dimension.selectedDataPoints.length !==
        0
      ) {
        this.config.result[resultIndex].dimension.selectedDataPoints.splice(
          dataPointIndex,
          1
        )
        if (
          this.config.result[resultIndex].dimension.selectedDataPoints
            .length === 0
        ) {
          this.config.result[resultIndex].metrics.selectedDataPoints = []
        }
        this.apply(resultIndex)
      }
    },
    setAggr(index, value, mode, resultIndex) {
      this.config.result[resultIndex][mode].dataPoints[index].yAxis.aggr = value
      this.config.result[resultIndex][mode].scatterPoints[index].aggr = value
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
      if (this.enableAddSeries) {
        for (let group of this.config.result) {
          let yAxis = group.metrics.scatterPoints[0]
          // group.dimension.scatterPoints.forEach(function(xAxis) {
          data.push({
            xAxis: group.dimension.scatterPoints,
            yAxis: yAxis,
          })
          // })
        }
      }

      this.config.scatterPoints = data

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
    getFormatedData(label, mode, resultIndex) {
      let text = ''
      if (this.analyticsConfig.dataPoints.length !== 0) {
        text = this.getLabelFromDataPoints(label)
      }
      if (text === '' || text === null || typeof text === 'undefined') {
        text = ''
        if (label && mode) {
          if (mode === 'metrics') {
            this.config.result[resultIndex][mode].addedDataPoints.forEach(
              function(points) {
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
              }
            )
          } else {
            let parentId = parseInt(label.split('_')[1])
            let readingId = parseInt(label.split('_')[0])

            for (let point of this.config.result[resultIndex].dimension
              .summaryList) {
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
    selectDataPoint(datapoint, mode, resultIndex) {
      this.config.result[resultIndex][mode].selectedDataPoints = []
      this.config.result[resultIndex][mode].selectedDataPoints.push(datapoint)
    },
    convertToList(readings) {
      let readingsObject = {}
      for (let reading of readings) {
        readingsObject[reading.id] = reading
      }
      return readingsObject
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

    addToSummaryListNew(source, resultIndex) {
      let existingReadings = []
      let points = []
      let self = this
      let panel = source
        ? source
        : self.config.firstPanel.selectedTab.toLowerCase()
      if (
        this.config.result[resultIndex][panel].selectedDataPoints &&
        this.config.result[resultIndex][panel].selectedDataPoints.length !== 0
      ) {
        for (let point of this.config.result[resultIndex][panel]
          .selectedDataPoints) {
          let temp = {
            point: point,
            isDone: false,
          }
          points.push(temp)
        }

        if (this.config.result[resultIndex][panel].summaryList.length !== 0) {
          this.currentKey = this.currentKey + 1
          for (let group of this.config.result[resultIndex][panel]
            .summaryList) {
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

        this.removeUnwantedReadings(resultIndex)

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
              for (let group of this.config.result[resultIndex][panel]
                .summaryList) {
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

            let existingGroup = self.config.result[resultIndex][
              panel
            ].summaryList.filter(inGroup => inGroup.label === categoryLabel)
            if (
              self.config.result[resultIndex][panel].summaryList.length === 0 ||
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
              this.config.result[resultIndex][panel].summaryList.push(temp)
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
    removeUnwantedReadings(resultIndex) {
      for (let group of this.config.result[resultIndex][
        this.config.firstPanel.selectedTab.toLowerCase()
      ].summaryList) {
        for (let reading of group.readings) {
          if (reading && reading.marked !== this.currentKey) {
            let readingIndex = group.readings.indexOf(reading)
            group.readings.splice(readingIndex, 1)
          }
          if (group.readings.length === 0) {
            this.config.result[resultIndex][
              this.config.firstPanel.selectedTab.toLowerCase()
            ].summaryList.splice(
              this.config.result[resultIndex][
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
          await self.$http
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
      for (let group of this.config.result) {
        if (
          group.metrics.selectedDataPoints.length >
          group.metrics.dataPoints.length
        ) {
          let length =
            group.metrics.selectedDataPoints.length -
            group.metrics.dataPoints.length
          group.metrics.selectedDataPoints.splice(
            group.metrics.dataPoints.length - 1,
            length
          )
        }
      }
      this.selectorPanelShow = false
    },

    apply(resultIndex) {
      let config = this.$helpers.cloneObject(this.config)
      this.config.result[resultIndex].dimension.dataPoints = []
      this.config.result[resultIndex].metrics.dataPoints = []
      this.config.result[resultIndex].dimension.scatterPoints = []
      this.config.result[resultIndex].metrics.scatterPoints = []

      this.populateAddedDataPoints(config, resultIndex)

      if (
        this.selectedBuildings.length !== 0 &&
        this.selectedBuildingLabel !== ''
      ) {
        this.config.result[resultIndex].meta = {}
        this.config.result[resultIndex].meta[
          'selectedBuildingIds'
        ] = this.selectedBuildings
        this.config.result[resultIndex].meta[
          'selectedBuildingTitle'
        ] = this.selectedBuildingLabel
      }
      this.$emit('update:enableFloatimngIcon', true)
      this.getSelectedPointsMeta(resultIndex)
      this.updateAndEmitData()

      this.selectorPanelShow = false
    },
    populateAddedDataPoints(config, resultIndex) {
      if (
        this.config.result[resultIndex].dimension.selectedDataPoints &&
        this.config.result[resultIndex].dimension.selectedDataPoints.length
      ) {
        for (let dp of this.config.result[resultIndex].dimension
          .selectedDataPoints) {
          let readingFieldId = parseInt(dp.split('_')[0])
          let parentId = parseInt(dp.split('_')[1])

          this.config.result[resultIndex].dimension.dataPoints.push({
            parentId: parentId,
            yAxis: {
              fieldId: readingFieldId,
              aggr: this.setAggrPoint(
                dp,
                'dimension',
                config,
                this.readings.fields[readingFieldId],
                resultIndex
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
          this.config.result[resultIndex].dimension.scatterPoints.push({
            parentId: parentId,
            aggr: this.setAggrPoint(
              dp,
              'dimension',
              config,
              this.readings.fields[readingFieldId],
              resultIndex
            ),
            name: this.getFormatedData(dp, 'dimension', resultIndex),
            readingId: readingFieldId,
            axis: 'x',
            aliases: {},
          })
        }
      }
      if (
        this.config.result[resultIndex].metrics.selectedDataPoints &&
        this.config.result[resultIndex].metrics.selectedDataPoints.length
      ) {
        for (let dp of this.config.result[resultIndex].metrics
          .selectedDataPoints) {
          let readingFieldId = parseInt(dp.split('_')[0])
          let parentId = parseInt(dp.split('_')[1])

          this.config.result[resultIndex].metrics.dataPoints.push({
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
                this.readings.fields[readingFieldId],
                resultIndex
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
          this.config.result[resultIndex].metrics.scatterPoints.push({
            parentId: parentId,
            aggr: this.setAggrPoint(
              dp,
              'metrics',
              config,
              this.readings.fields[readingFieldId],
              resultIndex
            ),
            name: this.getFormatedData(dp, 'metrics', resultIndex),
            readingId: readingFieldId,
            axis: 'y',
            aliases: {},
          })
        }
      }
    },

    getSelectedPointsMeta(resultIndex) {
      let points = []
      let metricPoints = []
      let self = this
      if (this.config.result[resultIndex].dimension.dataPoints.length) {
        this.config.result[resultIndex].dimension.dataPoints.forEach(function(
          point
        ) {
          if (self.config.result[resultIndex].dimension.summaryList.length) {
            self.config.result[resultIndex].dimension.summaryList.forEach(
              function(summary) {
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
              }
            )
          }
        })
      }
      this.config.result[resultIndex].dimension.addedDataPoints = points
      if (this.config.result[resultIndex].metrics.dataPoints.length) {
        this.config.result[resultIndex].metrics.dataPoints.forEach(function(
          point
        ) {
          if (self.config.result[resultIndex].metrics.summaryList.length) {
            self.config.result[resultIndex].metrics.summaryList.forEach(
              function(summary) {
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
              }
            )
          }
        })
      }
      this.config.result[resultIndex].metrics.addedDataPoints = metricPoints
    },
    setAggrPoint(dp, mode, config, readingFields, resultIndex) {
      let aggr =
        readingFields &&
        readingFields.unit &&
        ['currency', 'kwh', 'co2', 'kg', 'mwh'].includes(
          readingFields.unit.trim().toLowerCase()
        )
          ? 3
          : 2
      if (dp && config.result[resultIndex][mode].dataPoints.length) {
        let resourceId = parseInt(dp.split('_')[1])
        let readingId = parseInt(dp.split('_')[0])
        config.result[resultIndex][mode].dataPoints.forEach(point => {
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
  },
}
</script>
<style>
.newregression-analysis .building-analysis-point-container {
  /* bottom: 175px; */
  border-left: none;
}
.newregression-analysis .reading-apply-container {
  /* bottom: 140px; */
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
.scatter-main-con {
  width: 100%;
  position: relative;
  display: block;
}
.series-box {
  border: solid 1px #d0d9e2;
  margin-top: 10px;
}
</style>
