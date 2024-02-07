<template>
  <div class="height100 new-report-page">
    <div class="flex-middle justify-content-center" v-if="loading">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <el-form v-else ref="newReportForm" class="fc-form" :label-position="'top'">
      <div class="row">
        <div class="col-12">
          <div class="fc-report-header">
            <div class="pull-left report-title-section">
              <el-input
                :autofocus="true"
                class="fc-report-input-title newreport-title-input pL10"
                v-model="computedReportName"
                :placeholder="$t('common.wo_report.new_report')"
                prop="reportName"
                type="text"
              ></el-input>
            </div>
            <div class="pull-right report-btn-section">
              <!-- fsave as report -->
              <f-report-options
                :savedReport.sync="savedReport"
                :config="configFromReport ? configFromReport : serverConfig"
                :moduleName="moduleName"
                :moduleFromRoute="$attrs.moduleName"
                :iscustomModule="iscustomModule"
                optionClass="analytics-page-options"
                :optionsToEnable="[5, 1, 2]"
                :reportObject="reportObject"
                :resultObject="resultObject"
                :params="reportObject ? reportObject.params : null"
                class="pull-right analytics-page-options-building-analysis newreport-page-options"
                :pdf="false"
              ></f-report-options>
            </div>
          </div>
        </div>
      </div>

      <div class="report-main-con">
        <div
          v-if="!hideSideBar"
          :class="{
            'report-sidebar-con': !hideSideBar,
            'report-sidebar-hide': hideSideBar,
          }"
        >
          <div
            v-if="showInnerPanel"
            :class="{ 'analytics-sidebar-bg': secondPanelToggle }"
          >
            <div class="new-report-sidebar col-3">
              <div class="report-sidebar-inner">
                <div class="pB20 pL20 pR20">
                  <div class="fc-black2-12" v-if="moduleTypes.length === 0">
                    {{ $t('home.dashboard.generate_chart') }}
                  </div>
                  <el-row v-else>
                    <el-col :span="24">
                      <div class="label-txt-black pB5">
                        {{ $t('home.reports.module_name') }}
                      </div>
                      <el-select
                        v-model="config.moduleType"
                        @change="setModuleType"
                        class="fc-input-full-border-select2 report-input-select width100"
                      >
                        <el-option
                          v-for="(moduleType, moduleTypeIdx) in moduleTypes"
                          :key="moduleTypeIdx"
                          :label="moduleType.displayName"
                          :value="moduleType.id"
                        ></el-option>
                      </el-select>
                    </el-col>
                  </el-row>
                  <div
                    v-if="
                      this.$route.meta.switch === 'yes' ||
                        isNewAlarmModule ||
                        isWebTabsEnabled
                    "
                  >
                    <div>
                      <el-row class="pB20 pT20">
                        <el-col :span="24">
                          <div class="label-txt-black pB5">
                            {{ $t('home.reports.module') }}
                          </div>
                        </el-col>
                        <el-select
                          filterable
                          :disabled="this.$route.query.module ? true : false"
                          v-model="moduleswitch"
                          @change="setmodule(moduleswitch)"
                          class="fc-input-full-border-select2 report-input-select width100"
                        >
                          <el-option
                            v-for="moduleObj in getModuleList"
                            :label="moduleObj.displayName"
                            :key="moduleObj.name"
                            :value="moduleObj.name"
                          ></el-option>
                        </el-select>
                      </el-row>
                    </div>
                  </div>
                  <div v-if="subModulesList.length">
                    <div>
                      <el-row class="pB20">
                        <el-col :span="24">
                          <div class="label-txt-black pB5">
                            {{ $t('home.reports.submodule') }}
                          </div>
                        </el-col>
                        <el-select
                          filterable
                          :disabled="this.$route.query.module ? true : false"
                          v-model="submodule"
                          @change="setmodule(submodule, moduleswitch)"
                          class="fc-input-full-border-select2 report-input-select width100"
                          clearable
                        >
                          <el-option
                            v-for="module in subModulesList"
                            :label="module.displayName"
                            :key="module.name"
                            :value="module.name"
                          ></el-option>
                        </el-select>
                      </el-row>
                    </div>
                  </div>
                  <el-row
                    class="pB20"
                    :class="{
                      pT20:
                        this.$route.meta.switch !== 'yes' &&
                        !isNewAlarmModule &&
                        !isWebTabsEnabled,
                    }"
                  >
                    <el-col
                      :span="
                        config.dimension.subDimension &&
                        config.dimension.subDimension.dataTypeEnum === 'LOOKUP'
                          ? 20
                          : 24
                      "
                      :class="
                        config.dimension.subDimension &&
                        config.dimension.subDimension.dataTypeEnum === 'LOOKUP'
                          ? 'pR10'
                          : ''
                      "
                    >
                      <div class="label-txt-black pB5">
                        {{ $t('home.reports.dimension') }}
                      </div>

                      <div
                        class="fc-input-div-full-border-f14"
                        @click="showSecondPanel(activePanelEnum.DIMENSION)"
                      >
                        <span v-if="config.dimension.subDimension">{{
                          config.dimension.subDimension.displayName
                        }}</span>
                        <span v-else>{{ $t('common._common.select') }}</span>
                        <i
                          class="pointer mT12 el-icon-arrow-right pull-right"
                        ></i>
                      </div>
                    </el-col>
                    <el-col
                      v-if="
                        config.dimension.subDimension &&
                          config.dimension.subDimension.dataTypeEnum ===
                            'LOOKUP'
                      "
                      :span="4"
                      class="pT20"
                    >
                      <div
                        class="fc-input-div-full-border-f14 pointer"
                        style="text-overflow: initial !important"
                        title="Dimension Filters"
                        data-position="top"
                        v-tippy="{ arrow: true, animation: 'perspective' }"
                        @click="lookUpFilter()"
                      >
                        <span>
                          <img src="~assets/filter.svg" slot="reference" />
                        </span>
                      </div>
                    </el-col>
                  </el-row>
                  <el-row>
                    <div class="label-txt-black pB5">
                      {{ $t('home.reports.metric') }}
                      <span
                        class="fc-dark-blue2-12 pointer pull-right"
                        title="Add Metric"
                        :disabled="
                          config.groupBy.dimension !== null ||
                            config.dimension.dimension === null
                        "
                        v-tippy="{
                          placement: 'top',
                          arrow: true,
                          animation: 'shift-away',
                        }"
                        :class="{
                          'pointer-events-none':
                            config.dimension.dimension === null ||
                            config.groupBy.dimension !== null,
                        }"
                        @click="addMetric()"
                        >Add</span
                      >
                    </div>
                  </el-row>

                  <!-- dummy metric place holder -->
                  <el-row
                    class="pB20"
                    :disabled="true"
                    v-if="config.metric.length == 0"
                  >
                    <el-col :span="20" class="pR10">
                      <div
                        :class="{
                          'pointer-events-none':
                            config.dimension.dimension == null,
                        }"
                        class="fc-input-div-full-border-f14 pL10 pointer"
                        @click="showSecondPanel(activePanelEnum.METRIC)"
                      >
                        <span
                          >{{ $t('common._common.select')
                          }}<i
                            class="pointer mT12 el-icon-arrow-right pull-right"
                          ></i
                        ></span>
                      </div>
                    </el-col>
                    <el-col :span="4">
                      <div class="fc-input-div-full-border-f14 pointer">
                        <el-popover
                          :disabled="true"
                          v-model="toggleMetricAggregation"
                          placement="top-start"
                          width="90"
                          trigger="hover"
                          popper-class="metric-popover"
                        >
                          <div
                            class="pointer pT5 pB5 fc-label-hover label-txt-black f14 text-center"
                            v-for="(metricAggr,
                            metricAggrIdx) in metricAggregation"
                            :value="metricAggr.value"
                            :key="metricAggrIdx"
                          >
                            {{ metricAggr.label }}
                          </div>
                          <img
                            :disabled="true"
                            src="~assets/summation_img.svg"
                            slot="reference"
                          />
                        </el-popover>
                      </div>
                    </el-col>
                  </el-row>
                  <!-- dummy metric place holder end -->

                  <el-row
                    v-else
                    class="pB20"
                    v-for="(cMetric, cMetricIdx) in config.metric"
                    :key="cMetricIdx"
                    :disabled="config.dimension.dimension == null"
                  >
                    <el-col :span="20" class="pR10">
                      <div
                        :class="{
                          'pointer-events-none':
                            config.dimension.dimension == null,
                        }"
                        class="fc-input-div-full-border-f14 pL10 pointer"
                        @click="
                          showSecondPanel(activePanelEnum.METRIC),
                            (currentMetric = cMetricIdx)
                        "
                      >
                        <span v-if="config.metric.length !== 0">
                          <span class="metric-tag"
                            >{{ cMetric.displayName }}
                            <i
                              class="el-icon-close pointer"
                              v-if="config.metric.length > 1"
                              @click.stop="removeMetric(cMetricIdx)"
                            ></i> </span
                          ><i class="mT12 el-icon-arrow-right pull-right"></i>
                        </span>
                        <span v-else
                          >{{ $t('common._common.select')
                          }}<i
                            class="pointer mT12 el-icon-arrow-right pull-right"
                          ></i
                        ></span>
                      </div>
                    </el-col>
                    <el-col :span="4">
                      <div class="fc-input-div-full-border-f14 pointer">
                        <el-popover
                          :disabled="!showMetricAggregation(cMetric)"
                          v-model="
                            toggleMetricAggregation[
                              cMetric.fieldId + '__' + cMetric.name
                            ]
                          "
                          placement="top-start"
                          width="90"
                          trigger="hover"
                          popper-class="metric-popover"
                        >
                          <div
                            :class="{
                              'selected-field-background':
                                config.metricAggregation &&
                                metricAggr.value ===
                                  config.metricAggregation[
                                    cMetric.fieldId + '__' + cMetric.name
                                  ],
                            }"
                            class="pointer pT5 pB5 fc-label-hover label-txt-black f14 text-center"
                            @click="
                              setMetricAggregation(metricAggr.value, cMetricIdx)
                            "
                            v-for="(metricAggr,
                            metricAggrIdx) in metricAggregation"
                            :value="metricAggr.value"
                            :key="metricAggrIdx"
                          >
                            {{ metricAggr.label }}
                          </div>
                          <img
                            :disabled="!showMetricAggregation(cMetric)"
                            src="~assets/summation_img.svg"
                            slot="reference"
                          />
                        </el-popover>
                      </div>
                    </el-col>
                  </el-row>

                  <el-row
                    v-if="
                      config.metric.length < 2 &&
                        (!config.baseLine || config.baseLine[0] === -1)
                    "
                    class="pB20"
                    :disabled="config.dimension.dimension === null"
                  >
                    <el-col :span="24">
                      <div class="label-txt-black pB5">
                        {{ $t('home.reports.group_by') }}
                      </div>
                      <div
                        :class="{
                          'pointer-events-none':
                            config.dimension.dimension == null,
                        }"
                        class="fc-input-div-full-border-f14"
                        @click="showSecondPanel(activePanelEnum.GROUPBY)"
                      >
                        <span
                          class="metric-tag"
                          v-if="config.groupBy.subDimension"
                          >{{ config.groupBy.subDimension.displayName
                          }}<i
                            class="el-icon-close pointer"
                            @click.stop="removeGroupBy"
                          ></i
                        ></span>
                        <span v-else>{{ $t('common._common.select') }}</span>
                        <i
                          v-if="!config.groupBy.subDimension"
                          class="pointer mT12 el-icon-arrow-right pull-right"
                        ></i>
                      </div>
                    </el-col>
                  </el-row>
                  <el-row
                    class="pB20"
                    v-if="
                      config.dimension.subDimension &&
                        config.dimension.subDimension.dataType !== 5 &&
                        config.dimension.subDimension.dataType !== 6
                    "
                  >
                    <el-col :span="24">
                      <div class="label-txt-black pB5">
                        {{ $t('home.reports.date_filter') }}
                      </div>
                      <el-select
                        v-model="config.customDateField"
                        @change="setCustomDateField"
                        placeholder="Select Time Field"
                        class="fc-input-full-border-select2 report-input-select width100"
                      >
                        <el-option
                          v-for="(timeField, timeFieldIdx) in timeFields"
                          :key="timeFieldIdx"
                          :label="timeField.displayName"
                          :value="timeField.id + '_' + timeField.name"
                        ></el-option>
                      </el-select>
                    </el-col>
                  </el-row>
                  <el-row
                    v-if="
                      baselinelist &&
                        config.groupBy.dimension === null &&
                        ((config.dimension.subDimension &&
                          (config.dimension.subDimension.dataType === 5 ||
                            config.dimension.subDimension.dataType === 6)) ||
                          config.customDateField)
                    "
                    class="pB20"
                  >
                    <el-col :span="24">
                      <div class="label-txt-black pB5">Base Line</div>
                      <el-cascader
                        class="fc-input-full-border-select2 report-input-select width100"
                        placeholder="Compare to"
                        :options="baseLineCasecaderOptions"
                        :value="config.baseLine"
                        @change="onBaseLineChange"
                        title="Compare with Baseline"
                        data-arrow="true"
                        v-tippy
                      ></el-cascader>
                    </el-col>
                  </el-row>
                  <el-row
                    :disabled="
                      config.dimension.dimension === null ||
                        config.dimension.dimension.displayName === 'Time' ||
                        config.groupBy.dimension !== null
                    "
                    class="pB20"
                  >
                    <el-col :span="13">
                      <div class="label-txt-black pB5">
                        {{ $t('home.reports.sorting') }}
                      </div>
                      <el-select
                        :disabled="
                          config.dimension.dimension === null ||
                            config.dimension.dimension.displayName === 'Time' ||
                            config.groupBy.dimension !== null
                        "
                        @change="handleSort"
                        v-model="config.sorting"
                        placeholder="Select"
                        class="fc-input-full-border-select2 report-input-select"
                      >
                        <el-option
                          v-for="(sortOrder, sortOrderIdx) in sortingOrder"
                          :key="sortOrderIdx"
                          :value="sortOrder.value"
                          :label="sortOrder.label"
                        >
                        </el-option>
                      </el-select>
                    </el-col>
                    <el-col :span="10" class="mL10">
                      <div class="label-txt-black pB5">
                        {{ $t('home.reports.range') }}
                      </div>
                      <el-select
                        :disabled="
                          config.dimension.dimension === null ||
                            config.dimension.dimension.displayName === 'Time' ||
                            config.groupBy.dimension !== null
                        "
                        @change="handleRange"
                        v-model="config.range"
                        placeholder="Select"
                        class="fc-input-full-border-select2 report-input-select"
                      >
                        <el-option
                          v-for="(range, rangeIdx) in ranges"
                          :key="rangeIdx"
                          :value="range"
                          :label="range"
                        ></el-option>
                      </el-select>
                    </el-col>
                  </el-row>
                  <el-row>
                    <el-col :span="24">
                      <div class="label-txt-black pB10 pB5">
                        {{ $t('home.reports.criteria') }}
                      </div>
                      <div class="fL">
                        <div v-if="criteriaTrigger">
                          <div class="fc-black2 f14 fw-bold pB5">
                            {{ $t('home.reports.criteria_applied') }}
                          </div>
                          <div class="fc-grey2 f12 pB5"></div>
                        </div>

                        <div v-else>
                          <div class="fc-black2 f14 fw-bold pB5">
                            {{ 'All ' + moduleDisplayName }}
                          </div>
                          <div class="fc-grey2 f12 pB5">
                            {{ $t('home.reports.default') }}
                          </div>
                        </div>
                      </div>
                      <div class="fR">
                        <span
                          v-if="!isCriteriaEmpty() || !isDataFilterEmpty()"
                          @click="resetCriteria"
                          class="pointer label-txt-blue2 pR10"
                          >{{ $t('common._common.reset') }}</span
                        >
                        <i
                          @click="showCriteriaBuilder = true"
                          v-tippy
                          :title="
                            isCriteriaEmpty() && isDataFilterEmpty()
                              ? $t('home.reports.add_criteria')
                              : $t('home.reports.edit_criteria')
                          "
                          class="el-icon-edit report-edit-icon pointer"
                        ></i>
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <div class="border-top5">
                  <div class="pL20 pR20">
                    <el-row class="clearboth pT20">
                      <el-col :span="24">
                        <div class="fL">
                          <div class="fc-black2 f14 fw-bold pB5">
                            {{ $t('home.reports.user_filter') }}
                          </div>
                        </div>
                        <div class="fR">
                          <div
                            class="fc-dark-blue4-12 pointer"
                            :title="$t('common._common.edit')"
                            v-tippy="{
                              placement: 'top',
                              arrow: true,
                              animation: 'shift-away',
                            }"
                            @click="showUserFilter = true"
                          >
                            {{
                              config.userFilters === null ||
                              (config.userFilters &&
                                config.userFilters.length === 0)
                                ? $t('common._common.add')
                                : $t('common._common.edit')
                            }}
                          </div>
                        </div>
                      </el-col>
                    </el-row>
                    <div v-if="config.userFilters !== null">
                      <el-row
                        v-for="(filterConfig,
                        filterConfigIdx) in config.userFilters"
                        :key="filterConfigIdx"
                        class="mT10"
                      >
                        <el-col :span="24">
                          <div class="fc-input-div-full-border width100">
                            <div class="fL">
                              <div class="label-txt-black fw4">
                                {{ filterConfig.name }}
                              </div>
                            </div>
                            <div
                              @click="openFilterConfig(filterConfig)"
                              class="fR border-left1 pL15 pointer"
                            >
                              <img
                                src="~assets/report-configure.svg"
                                width="14"
                                height="14"
                              />
                            </div>
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                  </div>
                </div>
                <!-- DRILLDOWN START -->
                <div class="border-top5 mT20">
                  <div class="pL20 pR20">
                    <el-row class="clearboth pT20">
                      <el-col :span="24">
                        <div class="fL">
                          <div class="fc-black2 f14 fw-bold pB5">
                            {{ $t('home.reports.drilldown') }}
                          </div>
                        </div>
                        <div class="fR">
                          <div
                            class="fc-dark-blue4-12 pointer"
                            :title="$t('common._common.edit')"
                            v-tippy="{
                              placement: 'top',
                              arrow: true,
                              animation: 'shift-away',
                            }"
                            @click="showDrilldownConfig = true"
                          >
                            {{
                              config.reportDrilldownPath === null ||
                              (config.reportDrilldownPath &&
                                config.reportDrilldownPath.length === 0)
                                ? $t('common._common.add')
                                : $t('common._common.edit')
                            }}
                          </div>
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </div>
                <!-- DRILLDOWN END -->
              </div>
            </div>

            <div class="report-sidebar-position-setup">
              <div v-if="secondPanelToggle" class="sidebar-close-btn pointer">
                <div>
                  <i @click="closeActivePanel" class="el-icon-close"></i>
                </div>
              </div>

              <!-- second -->
              <div
                class="report-assets-points-Scontainer assets-points-Scontainer-lalign pL10 pR10"
                v-if="secondPanelToggle"
              >
                <div class="report-sidebar-inner">
                  <div class="report-sidebar-secondpanel-scroll">
                    <div v-if="activePanel === activePanelEnum.DIMENSION">
                      <div
                        class="building-points-search building-points-height"
                      >
                        <el-input
                          placeholder="Search"
                          v-model="filterQuery"
                          autofocus
                          class=""
                        ></el-input>
                      </div>
                      <el-row
                        v-for="(dimension,
                        dimensionIdx) in filteredDimensionConfig"
                        :key="dimensionIdx"
                      >
                        <div
                          :class="[
                            {
                              'fw-bold': config.dimension.dimension
                                ? config.dimension.dimension.displayName ===
                                  dimension.displayName
                                  ? true
                                  : false
                                : false,
                            },
                          ]"
                          class="label-txt-black pB10 f14 pT10 pL10 pR10 fw-bold"
                        >
                          {{ dimension.displayName }}
                          <span
                            v-if="
                              config.dimension.dimension
                                ? config.dimension.dimension.displayName ===
                                  dimension.displayName
                                  ? true
                                  : false
                                : false
                            "
                            class="active-arrow position-relative fR"
                          ></span>
                        </div>
                        <div v-if="dimension.showSubFields">
                          <el-row
                            class="pointer label-txt-black pB10 f14 pT10 pL10 pR10 fc-label-hover"
                            :class="{
                              'selected-field-background': config.dimension
                                .subDimension
                                ? config.dimension.subDimension.displayName ===
                                    field.displayName &&
                                  config.dimension.subDimension.moduleId ===
                                    field.moduleId
                                  ? true
                                  : false
                                : false,
                            }"
                            v-for="(field, fieldIdx) in dimension.subFields"
                            :key="fieldIdx"
                          >
                            <div
                              class="mL5 pointer"
                              @click="setDimension(dimension, field)"
                            >
                              {{ field.displayName }}
                            </div>
                          </el-row>
                        </div>
                      </el-row>
                    </div>

                    <div v-else-if="activePanel === activePanelEnum.METRIC">
                      <div
                        v-for="(metric, metricIdx) in metrics"
                        :key="metricIdx"
                        class="pointer label-txt-black pB10 f14 pT10 pL10 pR10 fc-label-hover"
                        :class="{
                          pointer: true,
                          'asset-list-block-txt': true,
                          'selected-field-background': config.metric
                            ? metricSelected(metric)
                              ? true
                              : false
                            : false,
                        }"
                      >
                        <div @click="setDimension(metric)">
                          {{ metric.displayName }}
                        </div>
                      </div>
                    </div>

                    <div v-else-if="activePanel === activePanelEnum.GROUPBY">
                      <div
                        class="building-points-search building-points-height"
                      >
                        <el-input
                          placeholder="Search"
                          v-model="filterQuery"
                          autofocus
                          class=""
                        ></el-input>
                      </div>
                      <el-row
                        v-for="(group, groupIdx) in filteredGroupByConfig"
                        :key="groupIdx"
                      >
                        <div
                          :class="[
                            { 'asset-list-header-txt': true },
                            {
                              'fw-bold': config.groupBy.dimension
                                ? config.groupBy.dimension.displayName ===
                                  group.displayName
                                  ? true
                                  : false
                                : false,
                            },
                          ]"
                          class="label-txt-black pB10 f14 pT10 pL10 pR10 fw-bold"
                        >
                          {{ group.displayName }}
                          <span
                            v-if="
                              config.groupBy.dimension
                                ? config.groupBy.dimension.displayName ===
                                  group.displayName
                                  ? true
                                  : false
                                : false
                            "
                            style="left: 60%"
                            class="active-arrow position-relative"
                          ></span>
                        </div>
                        <div v-if="group.showSubFields">
                          <el-row
                            v-if="groupByField.show"
                            class="pointer label-txt-black pB10 f14 pT10 pL10 pR10 fc-label-hover pointer asset-list-block-txt"
                            :class="{
                              'asset-list-txt': true,
                              'selected-field-background': config.groupBy
                                .subDimension
                                ? config.groupBy.subDimension.displayName ===
                                    groupByField.displayName &&
                                  config.dimension.subDimension.moduleId ===
                                    groupByField.moduleId
                                  ? true
                                  : false
                                : false,
                            }"
                            v-for="(groupByField,
                            groupByFieldIdx) in group.subFields"
                            :key="groupByFieldIdx"
                          >
                            <div
                              class="mL5 pointer"
                              @click="setDimension(group, groupByField)"
                            >
                              {{ groupByField.displayName }}
                            </div>
                          </el-row>
                        </div>
                      </el-row>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- CHART CUSTOMIZATION start -->
          <div
            class="customize-container building-customize-container col-4"
            v-if="showChartCustomization"
          >
            <div class="customize-header">
              <div class="customize-H">
                {{ $t('home.reports.chart_customization') }}
              </div>
            </div>
            <div class="customize-body">
              <f-chart-customization
                :optionsToEnable="
                  config.groupBy.dimension === null ? [1, 2, 3, 4] : [2, 3, 4]
                "
                :report="reportObject"
                :resultDataPoints="
                  resultObject ? resultObject.report.dataPoints : []
                "
                class="report-chart-customization"
              ></f-chart-customization>
            </div>
          </div>
          <!-- CHART CUSTOMIZATION END -->
        </div>
        <div
          v-if="!hideSideBar"
          :style="{ left: cssLeft }"
          class="report-settings-group"
        >
          <el-button-group>
            <el-button
              type="primary"
              :class="{ 'analytics-report-icon-bg-active': showInnerPanel }"
              @click="
                ;(showInnerPanel = true), (showChartCustomization = false)
              "
              class="report-settings-btn"
            >
              <InlineSvg
                src="statistics"
                iconClass="icon icon-sm-md  vertical-middle report-option-img report-rotate-icons"
              ></InlineSvg>
            </el-button>
            <el-button
              type="primary"
              :class="{
                'analytics-report-icon-bg-active': showChartCustomization,
              }"
              :disabled="
                reportObject === null || typeof reportObject === 'undefined'
              "
              @click="
                ;(showChartCustomization = true), (showInnerPanel = false)
              "
              class="report-settings-btn report-settings-btn2"
            >
              <InlineSvg
                src="settings-grey"
                iconClass="icon icon-sm-md vertical-middle report-option-img"
              ></InlineSvg>
            </el-button>
            <el-button
              type="primary"
              @click="toggleSideBar"
              class="report-settings-btn report-settings-btn3"
            >
              <InlineSvg
                src="arrow-pointing-to-left2"
                iconClass="icon icon-sm-md vertical-middle report-option-img report-rotate-icons"
              ></InlineSvg>
            </el-button>
          </el-button-group>
        </div>
        <div
          v-if="hideSideBar"
          class="report-settings-group settings-group-edge"
        >
          <el-button-group>
            <el-button
              type="primary"
              :class="{ 'analytics-report-icon-bg-active': showInnerPanel }"
              @click="
                ;(hideSideBar = false),
                  (showInnerPanel = true),
                  (showChartCustomization = false)
              "
              class="report-settings-btn"
            >
              <InlineSvg
                src="statistics"
                iconClass="icon icon-sm-md  vertical-middle report-option-img report-rotate-icons"
              ></InlineSvg>
            </el-button>
            <el-button
              type="primary"
              :class="{
                'analytics-report-icon-bg-active': showChartCustomization,
              }"
              :disabled="
                reportObject === null || typeof reportObject === 'undefined'
              "
              @click="
                ;(hideSideBar = false),
                  (showChartCustomization = true),
                  (showInnerPanel = false)
              "
              class="report-settings-btn report-settings-btn2"
            >
              <InlineSvg
                src="settings-grey"
                iconClass="icon icon-sm-md vertical-middle report-option-img"
              ></InlineSvg>
            </el-button>
            <el-button
              type="primary"
              @click="toggleSideBar"
              style="transform: rotate(180deg)"
              class="report-settings-btn report-settings-btn3"
            >
              <InlineSvg
                src="arrow-pointing-to-left2"
                iconClass="icon icon-sm-md vertical-middle report-option-img report-rotate-icons"
              ></InlineSvg>
            </el-button>
          </el-button-group>
        </div>

        <div
          :class="{
            'report-graph-con-chartCustom-apply':
              showChartCustomization === true && hideSideBar === false,
            'report-graph-con':
              !hideSideBar && showChartCustomization === false,
            'report-graph-con-stretch': hideSideBar,
          }"
          class="height100 scrollable"
        >
          <div>
            <div
              class="report-preview text-center pT0 new-report-page-chart"
              style="margin-bottom: 50px"
            >
              <div class="reports-chart">
                <!-- <el-button type="info" v-if="!showChart" @click="build" class="mB30">Generate Report Preview</el-button> -->
                <f-new-analytic-modular-report
                  :savedReport="savedReport"
                  :reportid="reportId"
                  ref="analyticReport"
                  :module="computedModule"
                  :showDatePicker="showDatePicker"
                  v-if="showChart"
                  :serverConfig.sync="serverConfig"
                  @reportLoaded="populateReportObjects"
                ></f-new-analytic-modular-report>
              </div>
            </div>
          </div>
        </div>
      </div>

      <el-dialog
        :visible.sync="showCriteriaBuilder"
        class="fc-dialog-center-container fc-dialog-header-hide"
        width="50%"
        :lock-scroll="false"
      >
        <el-tabs
          v-model="activeTab"
          class="fc-analytics-filter-tab pattern-visible"
        >
          <el-tab-pane
            label="Add Criteria"
            name="criteria"
            class="criteria-scroll"
          >
            <div class="height400">
              <!-- <new-criteria-builder
                v-if="showCriteriaBuilder"
                class="report-criteria-buuilder"
                :exrule="config.criteria"
                @condition="setCriteria"
                :showSiteField="false"
                :module="getSelectedModuleName()"
                :title="
                  $t('setup.users_management.specify_rules_for_assigment_rules')
                "
              ></new-criteria-builder> -->
              <CriteriaBuilder
                v-model="config.criteria"
                :moduleName="moduleName"
              />
            </div>
          </el-tab-pane>
          <el-tab-pane label="Data Filter" name="dataFilter">
            <div class="height400">
              <aggr-data-filter
                v-if="!isMetricsEmpty"
                :having="config.having"
                :metrics="metrics.filter(metric => !metric.defaultMetric)"
                @setDataFilter="setDataFilter"
                class="report-criteria-buuilder"
              ></aggr-data-filter>
            </div>
          </el-tab-pane>
        </el-tabs>
        <div class="modal-dialog-footer">
          <el-button
            class="modal-btn-cancel"
            @click="showCriteriaBuilder = false"
            >{{ $t('common._common.close') }}</el-button
          >
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="addCriteriaAndResconstruct"
            >{{ $t('common.wo_report.apply') }}</el-button
          >
        </div>
      </el-dialog>

      <ModularUserFilters
        v-if="config.userFilters"
        :config="currentUserFilter"
        :moduleName="getSelectedModuleName()"
        :visibility.sync="showFilterConfigDialog"
        @filter="setConfigForUserFilter"
      ></ModularUserFilters>
      <ModularUserFiltersList
        :existingConfig="config.userFilters"
        :moduleName="
          this.$helpers.isLicenseEnabled('NEW_ALARMS') &&
          this.moduleName === 'alarm'
            ? 'newreadingalarm'
            : this.moduleName
        "
        :visibility.sync="showUserFilter"
        @setFilter="setUserFilter"
      ></ModularUserFiltersList>
      <LookUpFilter
        v-if="showLookUpFilter"
        :config="lookUpFilterConfig"
        :visibility.sync="showLookUpFilter"
        @setLookUpFilterConfig="setLookUpFilterConfig"
      ></LookUpFilter>
      <DrilldownConfig
        v-if="showDrilldownConfig"
        :visibility.sync="showDrilldownConfig"
        :reportSettings.sync="config.reportSettings"
        :reportDrilldownPath.sync="config.reportDrilldownPath"
        @save="drilldownConfigSaved"
        :timeAggregators="timeAggregators"
        :isDimensionTime="serverConfig && serverConfig.isTime"
        :dateField="serverConfig && serverConfig.dateField"
        :initialDimensionConfig="initialDimensionConfig"
        :moduleResourceField="moduleResourceField"
        :reportModuleId="moduleId"
      >
      </DrilldownConfig>
    </el-form>
  </div>
</template>
<script>
import NewDataFormatHelper from 'src/pages/report/mixins/NewDataFormatHelper'
import FNewAnalyticModularReport from 'src/pages/energy/analytics/components/FNewAnalyticModularReport'
import { CriteriaBuilder } from '@facilio/criteria'
import FReportOptions from 'src/pages/report/components/FReportOptions'
import FChartCustomization from 'src/newcharts/components/FChartCustomization'
import NewDateHelper from 'src/components/mixins/NewDateHelper'
import ModularUserFilters from 'src/pages/report/components/ModularUserFilters'
import ModularUserFiltersList from 'src/pages/report/components/ModularUserFilterList'
import ModularUserFilterMixin from 'src/pages/report/mixins/modularUserFilter'
import LookUpFilter from 'src/pages/report/components/LookupFilter'
import DrilldownConfig from 'src/pages/new-dashboard/components/reports/DrilldownConfig.vue'
import AggrDataFilter from 'src/pages/report/AggregatedDataFilter'
import { mapState, mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import isString from 'lodash/isString'
import { API } from '@facilio/api'
import { isWebTabsEnabled } from '@facilio/router'
import { getFieldOptions } from 'util/picklist'

export default {
  components: {
    CriteriaBuilder,
    FNewAnalyticModularReport,
    FReportOptions,
    FChartCustomization,
    ModularUserFilters,
    ModularUserFiltersList,
    LookUpFilter,
    AggrDataFilter,
    DrilldownConfig,
  },

  mixins: [NewDataFormatHelper, ModularUserFilterMixin],
  data() {
    return {
      loading: false,
      dialogVisible: false,
      toggleMetricAggregation: {},
      newReportFolderName: null,
      reportObject: null,
      resultObject: null,
      filterQuery: null,
      sortingOrder: [
        {
          label: 'Ascending',
          value: 2,
        },
        {
          label: 'Descending',
          value: 3,
        },
      ],
      activeTab: 'criteria',
      ranges: [...Array(21).keys()].slice(1),
      reportName: 'New Report',
      reportDescription: 'description',
      showInnerPanel: true,
      showChartCustomization: false,
      hideSideBar: false,
      savedReport: null,
      configFromReport: null,
      currentUserFilter: null,
      showCriteriaBuilder: false,
      showUserFilter: false,
      showDrilldownConfig: false,
      showFilterConfigDialog: false,
      showChart: false,
      serverConfig: {},
      criteria: null,
      currentMetric: null,
      moduleswitch: null,
      moduleName: null,
      customModuleList: null,
      etisalatModuleList: [],
      subModulesList: [],
      parentModule: null,
      submodule: null,
      moduleDisplayName: null,
      moduleField: null,
      moduleList: [
        {
          displayName: 'WorkOrders',
          name: 'workorder',
          module: 'workorder',
          license: 'MAINTENANCE',
        },
        {
          displayName: 'Asset',
          name: 'asset',
          module: 'asset',
          license: 'SPACE_ASSET',
        },
        {
          displayName: 'Items',
          name: 'item',
          module: 'item',
          license: 'INVENTORY',
        },
        {
          displayName: 'Tools',
          name: 'tool',
          module: 'item',
          license: 'INVENTORY',
        },
        {
          displayName: 'Item transactions',
          name: 'itemTransactions',
          module: 'item',
          license: 'INVENTORY',
        },
        {
          displayName: 'Tool transactions',
          name: 'toolTransactions',
          module: 'item',
          license: 'INVENTORY',
        },
        {
          displayName: 'Item types',
          name: 'itemTypes',
          module: 'item',
          license: 'INVENTORY',
        },
        {
          displayName: 'Tool types',
          name: 'toolTypes',
          module: 'item',
          license: 'INVENTORY',
        },
        {
          displayName: 'Storerooms',
          name: 'storeRoom',
          module: 'item',
          license: 'INVENTORY',
        },
        {
          displayName: 'Transfer Request',
          name: 'transferrequest',
          module: 'item',
          license: 'TRANSFER_REQUEST',
        },
        {
          displayName: 'Shipments',
          name: 'shipment',
          module: 'item',
          license: 'INVENTORY',
        },
        {
          displayName: 'Purchase contracts',
          name: 'purchasecontracts',
          module: 'contracts',
          license: 'CONTRACT',
        },
        {
          displayName: 'Labour contracts',
          name: 'labourcontracts',
          module: 'contracts',
          license: 'CONTRACT',
        },
        {
          displayName: 'Warranty contracts',
          name: 'warrantycontracts',
          module: 'contracts',
          license: 'CONTRACT',
        },
        {
          displayName: 'Rental lease contracts',
          name: 'rentalleasecontracts',
          module: 'contracts',
          license: 'CONTRACT',
        },
        {
          displayName: 'Visits',
          name: 'visitorlog',
          module: 'visitorlog',
          license: 'VISITOR',
        },
        {
          displayName: 'Visitors',
          name: 'visitor',
          module: 'visitorlog',
          license: 'VISITOR',
        },
        {
          displayName: 'Watchlist',
          name: 'watchlist',
          module: 'visitorlog',
          license: 'VISITOR',
        },
        {
          displayName: 'Tenants',
          name: 'tenant',
          module: 'tenant',
          license: 'TENANTS',
        },
        {
          displayName: 'Vendors',
          name: 'vendors',
          module: 'asset',
          license: 'VENDOR',
        },
        {
          displayName: 'Tenant Contacts',
          name: 'tenantcontact',
          module: 'tenant',
          license: 'TENANTS',
        },
        {
          displayName: 'Tenant Units',
          name: 'tenantunit',
          module: 'tenant',
          license: 'TENANTS',
        },
        {
          displayName: 'Quotes',
          name: 'quote',
          module: 'tenant',
          license: 'QUOTATION',
        },
        {
          displayName: 'Faults',
          name: 'newreadingalarm',
          module: 'alarm',
          license: 'NEW_ALARMS',
        },
        {
          displayName: 'Fault Occurrences',
          name: 'readingalarmoccurrence',
          module: 'alarm',
          license: 'NEW_ALARMS',
        },
        {
          displayName: 'BMS Alarms',
          name: 'bmsalarm',
          module: 'alarm',
          license: 'NEW_ALARMS',
        },
        {
          displayName: 'ML Alarms',
          name: 'mlAnomalyAlarm',
          module: 'alarm',
          license: 'ANOMALY',
        },
        {
          displayName: 'ML Alarm Occurrences',
          name: 'anomalyalarmoccurrence',
          module: 'alarm',
          license: 'ANOMALY',
        },
        {
          displayName: 'Violation Alarms',
          name: 'violationalarm',
          module: 'alarm',
          license: 'NEW_ALARMS',
        },
        {
          displayName: 'Violation Alarm Occurrences',
          name: 'violationalarmoccurrence',
          module: 'alarm',
          license: 'NEW_ALARMS',
        },
        {
          displayName: 'Out of Schedules',
          name: 'operationalarm',
          module: 'alarm',
          license: 'OPERATIONAL_ALARM',
        },
        {
          displayName: 'Out of Schedules Occurrences',
          name: 'operationalarmoccurrence',
          module: 'alarm',
          license: 'OPERATIONAL_ALARM',
        },
        {
          displayName: 'Sensor/Meter Faults',
          name: 'sensorrollupalarm',
          module: 'alarm',
          license: 'NEW_ALARMS',
        },
        {
          displayName: 'Sensor/Meter Fault Occurrences',
          name: 'sensorrollupalarmoccurrence',
          module: 'alarm',
          license: 'NEW_ALARMS',
        },
        {
          displayName: 'Base Alarms',
          name: 'basealarm',
          module: 'alarm',
          license: 'NEW_ALARMS',
        },
        {
          displayName: 'Base Alarm Occurrences',
          name: 'alarmoccurrence',
          module: 'alarm',
          license: 'NEW_ALARMS',
        },
        {
          displayName: 'Fault Events',
          name: 'readingevent',
          module: 'alarm',
          license: 'NEW_ALARMS',
        },
        {
          displayName: 'BMS Alarm Events',
          name: 'bmsevent',
          module: 'alarm',
          license: 'NEW_ALARMS',
        },
        {
          displayName: 'ML Alarm Events',
          name: 'mlAnomalyEvent',
          module: 'alarm',
          license: 'ANOMALY',
        },
        {
          displayName: 'Violation Alarm Events',
          name: 'violationevent',
          module: 'alarm',
          license: 'NEW_ALARMS',
        },
        {
          displayName: 'Out of Schedule Events',
          name: 'operationevent',
          module: 'alarm',
          license: 'OPERATIONAL_ALARM',
        },
        {
          displayName: 'Sensor/Meter Fault Events',
          name: 'sensorevent',
          module: 'alarm',
          license: 'NEW_ALARMS',
        },
        {
          displayName: 'Base Events',
          name: 'baseevent',
          module: 'alarm',
          license: 'NEW_ALARMS',
        },
        {
          displayName: 'Service Requests',
          name: 'serviceRequest',
          module: 'serviceRequest',
          license: 'SERVICE_REQUEST',
        },
        {
          displayName: 'Purchase Order',
          name: 'purchaseorder',
          module: 'purchaseorder',
          license: 'PURCHASE',
        },
        {
          displayName: 'Purchase Request',
          name: 'purchaserequest',
          module: 'purchaseorder',
          license: 'PURCHASE',
        },
        {
          displayName: 'Budget',
          name: 'budget',
          module: 'budget',
          license: 'BUDGET_MONITORING',
        },
        {
          displayName: 'Inspection Template',
          name: 'inspectionTemplate',
          module: 'inspectionTemplate',
          license: 'INSPECTION',
        },
        {
          displayName: 'Inspection',
          name: 'inspectionResponse',
          module: 'inspectionTemplate',
          license: 'INSPECTION',
        },
        {
          displayName: 'Planned Maintenance',
          name: 'plannedmaintenance',
          module: 'workorder',
          license: 'PM_PLANNER',
        },
      ],
      aggr: {
        YEARLY: 8,
        MONTHLY: 10,
        WEEKLY: 11,
        DAILY: 12,
        HOURLY: 20,
        QUARTERLY: 25,
        HIGHRES: 0,
      },
      moduleResourceField: null,
      metrics: [],
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
      spaceAggregators: [
        {
          displayName: 'Site',
          name: 'site',
          value: 21,
          field_Id: 5,
          dataType: 7,
          show: true,
        },
        {
          displayName: 'Building',
          name: 'building',
          value: 22,
          field_Id: 6,
          dataType: 7,
          show: true,
        },
        {
          displayName: 'Floor',
          name: 'floor',
          value: 23,
          field_Id: 7,
          dataType: 7,
          show: true,
        },
        {
          displayName: 'Space',
          name: 'space',
          value: 26,
          field_Id: 8,
          dataType: 7,
          show: true,
        },
      ],
      moduleTypes: [],
      moduleMap: null,
      parentlookupFileds: null,
      timeAggregators: [
        {
          displayName: 'High-res',
          label: 'highres',
          value: 0,
          selected: false,
          field_Id: 0,
        },
        {
          displayName: 'Hourly',
          label: 'hourly',
          value: 20,
          selected: false,
          field_Id: 1,
        },
        {
          displayName: 'Daily',
          label: 'daily',
          value: 12,
          selected: false,
          field_Id: 2,
        },
        {
          displayName: 'Weekly',
          label: 'weekly',
          value: 11,
          selected: false,
          field_Id: 3,
        },
        {
          displayName: 'Monthly',
          label: 'monthly',
          value: 10,
          selected: false,
          field_Id: 4,
        },
        {
          displayName: 'Quarterly',
          label: 'quarterly',
          value: 25,
          selected: false,
          field_Id: 9,
        },
      ],
      showReportFolderList: false,
      reportFolders: [],
      activePanel: 1,
      activePanelEnum: {
        DIMENSION: 1,
        METRIC: 2,
        GROUPBY: 3,
      },
      groupBy: [],
      initialDimensionConfig: [],
      secondPanelToggle: false,
      thirdPanelLoadingToggle: false,
      timeFields: [],
      chartType: null,
      config: {
        moduleType: 1,
        dimension: {
          dimension: null,
          subDimension: null,
        },
        metric: [],
        metricAggregation: null,
        groupBy: {
          dimension: null,
          subDimension: null,
        },
        range: null,
        sorting: null,
        customDateField: null,
        criteria: null,
        userFilters: null,
        having: null,
        baseLine: null,
        reportSettings: {
          clickAction: 2,
          isShowListAtDrillEnd: true,
        },
        reportDrilldownPath: null,
      },
      criteriaTrigger: false,
      showLookUpFilter: false,
      lookUpFilterConfig: {
        allValues: {},
        chooseValues: [],
      },
      baselinelist: [],
      baseLineCasecaderOptions: [],
      oldBaseLine: null,
    }
  },
  computed: {
    getModuleList() {
      if (isWebTabsEnabled() && this.$attrs.modules) {
        return this.$attrs.modules
      } else if (this.isEtisalat) {
        return this.etisalatModuleList
      } else if (this.iscustomModule) {
        return this.customModuleList
      } else {
        return this.moduleList.filter(
          module =>
            module.module === this.$route.meta.module &&
            (this.$helpers.isLicenseEnabled(module.license) ||
              module.license === 'workorder')
        )
      }
    },
    filteredDimensionConfig() {
      if (!this.filterQuery || this.filterQuery === '')
        return this.initialDimensionConfig
      let config = []
      for (let record of this.initialDimensionConfig) {
        let temp = Object.assign({}, record)
        if (temp.subFields != null) {
          temp.subFields = temp.subFields.filter(
            field =>
              field.displayName
                .toLowerCase()
                .indexOf(this.filterQuery.toLowerCase()) >= 0
          )
          if (temp.subFields.length > 0) config.push(temp)
        }
      }
      return config
    },
    filteredGroupByConfig() {
      if (!this.filterQuery || this.filterQuery === '') return this.groupBy
      let config = []
      for (let record of this.groupBy) {
        let temp = Object.assign({}, record)
        if (temp.subFields != null) {
          temp.subFields = temp.subFields.filter(
            field =>
              field.displayName
                .toLowerCase()
                .indexOf(this.filterQuery.toLowerCase()) >= 0
          )
          if (temp.subFields.length > 0) config.push(temp)
        }
      }
      return config
    },
    isMetricsEmpty() {
      let ActualMetrics = this.metrics.filter(metric => !metric.defaultMetric)
      if (ActualMetrics.length > 0) {
        return false
      }
      return true
    },
    isNewAlarmModule() {
      if (
        this.$route.meta.module === 'alarm' &&
        this.$helpers.isLicenseEnabled('NEW_ALARMS')
      ) {
        return true
      }
      return false
    },
    isWebTabsEnabled() {
      return isWebTabsEnabled()
    },
    computedModule() {
      let module = {}
      module['moduleName'] = this.moduleName
      module['moduleDisplayName'] = this.moduleDisplayName
      module['moduleId'] = this.moduleId
      if (this.moduleResourceField) {
        module['resourceField'] = this.moduleResourceField
      }
      module['meta'] = { fieldMeta: {} }
      return module
    },
    iscustomModule() {
      if (
        this.$route.meta.moduleType &&
        this.$route.meta.moduleType === 'custom'
      ) {
        return true
      }
      return false
    },
    isEtisalat() {
      return this.$helpers.isEtisalat()
    },
    cssLeft() {
      if (this.showChartCustomization) {
        return 409 + 'px !important'
      } else {
        return 309 + 'px !important'
      }
    },
    computedCriteriaDescription() {
      if (this.isCriteriaEmpty() && this.isDataFilterEmpty()) {
        return 'All ' + this.moduleDisplayName
      } else {
        return 'Criteria Applied'
      }
    },
    computedReportName: {
      get: function() {
        if (this.savedReport) {
          return this.savedReport.name
        } else {
          if (this.resultObject) {
            if (this.resultObject.report.name) {
              return this.resultObject.report.name
            } else {
              return this.reportName
            }
          } else {
            return this.reportName
          }
        }
      },
      set: function(newValue) {
        if (this.savedReport) {
          this.savedReport.name = newValue
        } else {
          if (this.reportObject) {
            this.reportName = newValue
            this.reportObject.name = newValue
          } else {
            this.reportName = newValue
          }
        }
      },
    },
    reportId() {
      if (this.$route.query.reportId && this.$route.query.module) {
        this.moduleName = this.$route.query.module
        return this.$route.query.reportId
      }
      return null
    },
    showDatePicker() {
      if (this.serverConfig) {
        if (this.serverConfig.isTime) {
          return true
        } else {
          return false
        }
      } else {
        return false
      }
    },
    ...mapGetters(['getTicketStatusPickList']),
    ...mapState({
      ticketstatus: state => state.ticketStatus.workorder,
    }),
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      this.loading = true
      this.loadBaseline()
      this.moduleName = this.$attrs.moduleName || this.$route.meta.module
      if (this.$route.query && this.$route.query.module) {
        this.moduleName = this.$route.query.module
      }
      if (this.moduleName === 'workorder') {
        this.$store.dispatch('loadTicketStatus', 'workorder')
      }
      if (
        this.moduleName === 'alarm' &&
        this.$helpers.isLicenseEnabled('NEW_ALARMS')
      ) {
        this.moduleName = 'newreadingalarm'
      }
      if (this.isEtisalat) {
        this.etisalatModuleList = []
        API.get('v2/module/lists?defaultModules=true').then(
          ({ error, data }) => {
            if (!error) {
              if (data.modules) {
                if (
                  data.modules.systemModules &&
                  data.modules.systemModules.length
                ) {
                  let filteredModules = data.modules.systemModules.filter(
                    module =>
                      ['workorder', 'asset', 'vendors'].includes(module.name)
                  )
                  this.etisalatModuleList = this.etisalatModuleList.concat(
                    filteredModules
                  )
                }
                if (
                  data.modules.customModules &&
                  data.modules.customModules.length
                ) {
                  this.etisalatModuleList = this.etisalatModuleList.concat(
                    data.modules.customModules
                  )
                }
                if (this.etisalatModuleList) {
                  if (this.$route.query.module) {
                    let selectedModule = this.etisalatModuleList.find(
                      module => module.name === this.$route.query.module
                    )
                    if (selectedModule) {
                      this.moduleName = selectedModule.name
                      this.moduleId = selectedModule.moduleId
                    } else {
                      this.moduleName = this.etisalatModuleList[0].name
                      this.moduleId = this.etisalatModuleList[0].moduleId
                    }
                  } else {
                    this.moduleName = this.etisalatModuleList[0].name
                    this.moduleId = this.etisalatModuleList[0].moduleId
                  }
                }
                this.moduleswitch = `${this.moduleName}`
                this.$http('/module/meta?moduleName=' + this.moduleName).then(
                  response => {
                    this.moduleId = response.data.meta.module.moduleId
                    API.get(
                      '/v3/report/fields',
                      {
                        moduleName: this.moduleName,
                      },
                      { force: true }
                    ).then(response => {
                      if (response.error) {
                        this.$message.error('Error while getting fields')
                      } else {
                        this.prepareData(response.data.meta)
                      }
                      this.loading = false
                    })
                  }
                )
              }
            } else {
              let { message } = error
              this.$message.error(message)
            }
          }
        )
      } else if (
        this.moduleName === 'custom' ||
        (this.$route.meta && this.$route.meta.moduleType === 'custom')
      ) {
        let { data, error } = await API.get('/v3/report/modules/list', {})
        if (!error) {
          this.customModuleList = data.customModules
          if (!isEmpty(this.customModuleList)) {
            if (this.$route.query.module) {
              let customModule = this.customModuleList.find(
                module => module.name === this.$route.query.module
              )
              if (customModule) {
                this.moduleName = customModule.name
                this.moduleId = customModule.moduleId
              } else {
                this.moduleName = this.customModuleList[0].name
                this.moduleId = this.customModuleList[0].moduleId
              }
            } else {
              this.moduleName = this.customModuleList[0].name
              this.moduleId = this.customModuleList[0].moduleId
            }
            this.moduleswitch = `${this.moduleName}`
            API.get(
              '/v3/report/fields',
              {
                moduleName: this.moduleName,
              },
              { force: true }
            ).then(response => {
              if (response.error) {
                this.$message.error('Error while getting fields')
              } else {
                this.prepareData(response.data.meta)
              }
              this.loading = false
            })
          }
        }
      } else {
        if (this.moduleName === 'contracts') {
          this.moduleName = 'purchasecontracts'
        }
        this.moduleswitch = `${this.moduleName}`
        this.$http('/module/meta?moduleName=' + this.moduleName).then(
          response => {
            this.moduleId = response.data.meta.module.moduleId
            API.get(
              '/v3/report/fields',
              {
                moduleName: this.moduleName,
              },
              { force: true }
            ).then(response => {
              if (response.error) {
                this.$message.error('Error while getting fields')
              } else {
                this.prepareData(response.data.meta)
              }
              this.loading = false
            })
          }
        )
      }
      if (this.moduleName !== 'custom') {
        API.get(
          '/v3/report/sub_modules/list',
          {
            moduleName: this.moduleName,
          },
          { force: true }
        ).then(response => {
          if (response.error) {
            this.$message.error('Error while fetching SubModules')
          } else if (
            response.data.modules &&
            response.data.modules.length > 0
          ) {
            let mainModules = this.moduleList.map(module => module.name)
            this.subModulesList = response.data.modules.filter(
              module => !mainModules.includes(module.name)
            )
          }
        })
      }
      if (this.reportId) {
        this.showChart = true
      }
      this.$store.dispatch('loadTicketStatus', 'workorder')
    },
    loadBaseline() {
      API.get('/baseline/all').then(({ error, data }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.baselinelist = data ? data : []
          this.baseLineCasecaderOptions = []
          this.baseLineCasecaderOptions.push({
            label: 'None',
            value: -1,
          })

          for (let b of this.baselinelist) {
            let children = null
            if (
              b.rangeTypeEnum === 'PREVIOUS_MONTH' ||
              b.rangeTypeEnum === 'ANY_MONTH'
            ) {
              children = [
                {
                  label: 'Same date',
                  value: 4,
                },
                {
                  label: 'Same week',
                  value: 1,
                },
              ]
            } else if (
              b.rangeTypeEnum === 'PREVIOUS_YEAR' ||
              b.rangeTypeEnum === 'ANY_YEAR'
            ) {
              children = [
                {
                  label: 'Same date',
                  value: 3,
                },
                {
                  label: 'Same week',
                  value: 1,
                },
              ]
            }

            if (b.rangeTypeEnum !== 'PREVIOUS') {
              this.baseLineCasecaderOptions.push({
                label: b.name,
                value: b.id,
                children: children,
              })
            }
          }
        }
      })
    },
    onBaseLineChange(newBaseLine) {
      if (this.oldBaseLine && this.oldBaseLine[0] === newBaseLine[0]) {
        return
      }
      this.oldBaseLine = [...newBaseLine]
      this.$set(this.config, 'baseLine', newBaseLine)
      this.$set(this.serverConfig, 'baseLine', newBaseLine)
    },
    heatmapToggle() {
      if (
        this.chartType === 'heatmap' ||
        (this.reportObject &&
          this.reportObject.options &&
          this.reportObject.options.type === 'heatmap')
      ) {
        this.chartType = null
      } else {
        this.chartType = 'heatmap'
      }
    },
    getModuleName(module) {
      switch (module) {
        case 'asset':
          return 'Assets'
        case 'item':
          return 'Items'
        case 'tool':
          return 'Tools'
        case 'itemtransactions':
          return 'Item Transactions'
        case 'tooltransactions':
          return 'Tool Transactions'
        case 'assetbreakdown':
          return 'Asset Downtime'
      }
    },
    getSelectedModuleName() {
      if (
        this.$helpers.isLicenseEnabled('NEW_ALARMS') &&
        this.moduleName === 'alarm'
      ) {
        return 'newreadingalarm'
      } else {
        return this.moduleName
      }
    },
    setmodule(moduleswitch, parentModule) {
      if (parentModule) {
        if (moduleswitch === '') {
          moduleswitch = parentModule
          this.parentModule = null
        } else {
          this.parentModule = parentModule
        }
      }
      this.metrics = []
      this.currentUserFilter = null
      ;(this.lookUpFilterConfig = {
        allValues: {},
        chooseValues: [],
      }),
        (this.config = {
          moduleType: 1,
          dimension: {
            dimension: null,
            subDimension: null,
          },
          metric: [],
          metricAggregation: null,
          groupBy: {
            dimension: null,
            subDimension: null,
          },
          range: null,
          sorting: null,
          customDateField: null,
          criteria: null,
          userFilters: null,
          having: null,
          reportSettings: {
            clickAction: 2,
            isShowListAtDrillEnd: true,
          },
          reportDrilldownPath: null,
        })
      this.moduleName = `${moduleswitch}`
      this.$http('/module/meta?moduleName=' + this.moduleName).then(
        response => {
          this.moduleId = response.data.meta.module.moduleId
          API.post(
            '/v3/report/fields',
            { moduleName: this.moduleName },
            { force: true }
          ).then(response => {
            if (response.error) {
              this.prepareData(response.data.meta)
            } else {
              this.prepareData(response.data.meta)
            }
          })
          this.showChart = false
          this.serverConfig = {}
          this.moduleResourceField = null
        }
      )
      if (!parentModule)
        API.get(
          '/v3/report/sub_modules/list',
          {
            moduleName: this.moduleName,
          },
          { force: true }
        ).then(response => {
          if (response.error) {
            this.$message.error('Error while fetching SubModules')
          } else if (
            response.data.modules &&
            response.data.modules.length > 0
          ) {
            let mainModules = this.moduleList.map(module => module.name)
            this.subModulesList = response.data.modules.filter(
              module => !mainModules.includes(module.name)
            )
          }
        })
      if (this.reportId) {
        this.showChart = true
      }
    },
    setConfigForUserFilter(changedConfig) {
      this.showFilterConfigDialog = false
      let temp = []
      for (let config of this.config.userFilters) {
        if (config.fieldId === changedConfig.fieldId) {
          temp.push(changedConfig)
        } else {
          temp.push(config)
        }
      }
      this.$set(this.config, 'userFilters', temp)
      this.$set(this.serverConfig, 'userFilters', temp)
    },
    drilldownConfigSaved() {
      this.$set(this.serverConfig, 'reportSettings', this.config.reportSettings)
      this.$set(
        this.serverConfig,
        'reportDrilldownPath',
        this.config.reportDrilldownPath
      )
      this.showDrilldownConfig = false
    },
    openFilterConfig(filterConfig) {
      this.currentUserFilter = filterConfig
      this.showFilterConfigDialog = true
    },
    metricSelected(metric) {
      for (let m of this.config.metric) {
        if (metric.fieldId === m.fieldId && metric.name === m.name) {
          return true
        }
      }
      return false
    },
    showMetricAggregation(metric) {
      if (metric.defaultMetric) {
        this.toggleMetricAggregation[
          metric.fieldId + '__' + metric.name
        ] = false
        return false
      } else {
        return true
      }
    },
    addMetric() {
      this.currentMetric = null
      this.activePanel = this.activePanelEnum.METRIC
      this.secondPanelToggle = true
    },
    resetUserFilters() {
      this.config.userFilters = null
      this.currentUserFilter = null
      this.$set(this.serverConfig, 'userFilters', null)
    },
    setUserFilter(val) {
      this.config.userFilters = val
      this.showUserFilter = false
      if (val && val.length == 0) {
        this.currentUserFilter = null
      }
      this.$set(this.serverConfig, 'userFilters', val)
    },
    setModuleType(val) {
      this.config.moduleType = val
      if (this.serverConfig.moduleType) {
        this.serverConfig.moduleType = val
      } else {
        this.$set(this.serverConfig, 'moduleType', val)
      }
    },
    isCriteriaEmpty() {
      if (this.config.criteria) {
        let numberOfConditions = 0
        for (let condition of Object.keys(this.config.criteria.conditions)) {
          if (
            Object.keys(this.config.criteria.conditions[parseInt(condition)])
              .length !== 0 &&
            typeof this.config.criteria.conditions[parseInt(condition)]
              .fieldName !== 'undefined'
          ) {
            numberOfConditions = numberOfConditions + 1
          }
        }
        if (numberOfConditions > 0) {
          return false
        } else {
          return true
        }
      } else if (this.config.criteria === null) {
        return true
      }
      return false
    },
    isDataFilterEmpty() {
      if (this.config.having === null) {
        return true
      } else if (this.config.having.length > 0) {
        let numberOfConditions = 0
        for (let condition of this.config.having) {
          if (
            typeof condition.fieldName !== 'undefined' ||
            typeof condition.fieldId !== 'undefined'
          ) {
            numberOfConditions = numberOfConditions + 1
          }
        }
        if (numberOfConditions > 0) {
          return false
        } else {
          return true
        }
      }
      return true
    },
    resetGroupBy() {
      for (let group of this.groupBy) {
        for (let subField of group.subFields) {
          subField.show = true
        }
      }
    },
    setCustomDateField(customDateFieldId) {
      let dateFieldSplit = customDateFieldId.split(/_(.+)/)
      this.config.customDateField =
        dateFieldSplit.length && parseInt(dateFieldSplit[0]) === 0
          ? null
          : customDateFieldId
      this.updateReportObject()
      let oldValue = this.serverConfig
      if (parseInt(dateFieldSplit[0]) === 0) {
        oldValue['isCustomDateField'] = false
        if (oldValue.baseLine) {
          oldValue.baseLine = null
        }
      } else {
        let customDateField = this.timeFields.filter(
          field =>
            field.fieldId &&
            field.fieldId === parseInt(dateFieldSplit[0]) &&
            field.name &&
            field.name === dateFieldSplit[1]
        )
        this.computedModule['meta']['fieldMeta']['customDateField'] =
          customDateField.length > 0 ? customDateField[0].moduleId : null
        oldValue['isCustomDateField'] = true
        if (
          this.serverConfig.dateField === null ||
          typeof this.serverConfig.dateField === 'undefined'
        ) {
          let dateObject = NewDateHelper.getDatePickerObject(22)
          oldValue['dateFilter'] = dateObject
          let dateField = {}
          dateField['operator'] = dateObject.operatorId
          dateField['field_id'] = parseInt(dateFieldSplit[0])
          dateField['fieldName'] = dateFieldSplit[1]
          dateField['module_id'] = this.moduleId
          dateField['date_value'] =
            dateObject && dateObject.value ? dateObject.value.join(',') : ''
          oldValue['dateField'] = dateField
          this.serverConfig = oldValue
        } else {
          let dateField = this.serverConfig.dateField
          dateField.field_id = parseInt(dateFieldSplit[0])
          dateField['fieldName'] = dateFieldSplit[1]
          dateField['module_id'] = this.moduleId
          this.$set(this.serverConfig, 'dateField', dateField)
        }
      }
    },
    filtergroupBy() {
      for (let dimension of this.groupBy) {
        if (
          dimension.displayName === this.config.dimension.dimension.displayName
        ) {
          for (let field of dimension.subFields) {
            if (field.hasOwnProperty('fieldId')) {
              if (field.fieldId === -1) {
                if (
                  field.displayName ===
                  this.config.dimension.subDimension.displayName
                ) {
                  field.show = false
                } else {
                  field.show = true
                }
              } else if (
                field.fieldId === this.config.dimension.subDimension.fieldId
              ) {
                field.show = false
              } else {
                field.show = true
              }
            } else if (
              field.hasOwnProperty('value') &&
              field.value === this.config.dimension.subDimension.value
            ) {
              field.show = true
            } else {
              field.show = true
            }
          }
        } else {
          for (let field of dimension.subFields) {
            field.show = true
          }
        }
      }
    },
    resetCriteria() {
      this.config.criteria = {
        conditions: {
          1: {
            columnName: undefined,
            fieldName: undefined,
            operatorId: undefined,
            value: undefined,
          },
        },
        pattern: '(1)',
        resourceOperator: false,
      }
      this.config.having = [
        {
          fieldId: undefined,
          fieldName: undefined,
          aggregateOperator: undefined,
          operatorId: undefined,
          value: undefined,
        },
      ]
      this.criteriaTrigger = false
      this.updateReportObject()
      this.$set(this.serverConfig, 'criteria', null)
      this.$set(this.serverConfig, 'having', null)
    },
    getAllDefaults(data) {
      // Load all metrics
      let defaultMetric = {}
      defaultMetric['displayName'] = 'Number of ' + this.moduleDisplayName
      defaultMetric['defaultMetric'] = true
      defaultMetric['fieldId'] = -1
      defaultMetric['name'] = 'default'
      this.moduleMap = data.moduleMap
      this.parentlookupFileds = data.parentlookupFileds
        ? data.parentlookupFileds
        : {}
      this.metrics.push(defaultMetric)
      for (let metric of data.metrics) {
        this.metrics.push(metric)
        this.toggleMetricAggregation[
          metric.fieldId + '__' + metric.name
        ] = false
      }
      // Laod all dimensions
      for (let dimension of data.dimensionListOrder) {
        if (dimension !== 'resource_fields') {
          let dimensionData = data.dimension[dimension]
          if (dimensionData) {
            let baseconfig = {
              displayName: this.getStringWithCaps(dimension),
              subFields: [],
              showSubFields: true,
            }
            for (let field of dimensionData) {
              field['selected'] = true
              baseconfig.subFields.push(field)
            }
            this.initialDimensionConfig.push(baseconfig)
            // load all Group BY fields
            if (dimension.toLowerCase() !== 'time') {
              let fields = []
              for (let field of baseconfig.subFields) {
                if (field.dataType !== 5 && field.dataType !== 6) {
                  fields.push(field)
                }
              }
              let groupBaseField = this.$helpers.cloneObject(baseconfig)
              groupBaseField.subFields = fields
              this.groupBy.push(groupBaseField)
            }
          }
        }
      }

      // Load space in dimension
      if (data.dimension.resource_fields) {
        this.moduleResourceField = data.dimension['resource_fields'][0]
        let spaceConfig = {
          displayName: 'Space',
          subFields: this.spaceAggregators,
          showSubFields: true,
        }
        if (data.dimension.space) {
          for (let spaceField of data.dimension.space) {
            spaceField['show'] = true
            spaceConfig.subFields.push(spaceField)
          }
        }
        this.initialDimensionConfig.push(spaceConfig)
        this.groupBy.push(spaceConfig)
      } else if (data.dimension.siteId) {
        let spaceConfig = {
          displayName: 'Space',
          subFields: [this.spaceAggregators[0]],
          showSubFields: true,
        }

        if (data.dimension.space) {
          for (let spaceField of data.dimension.space) {
            spaceField['show'] = true
            spaceConfig.subFields.push(spaceField)
          }
        }
        this.initialDimensionConfig.push(spaceConfig)
        this.groupBy.push(spaceConfig)
      }

      this.timeFields = []
      this.timeFields.push({
        name: 'none',
        displayName: 'None',
        id: 0,
      })

      if (
        !isEmpty(
          this.initialDimensionConfig.filter(
            config => config.displayName === 'Time'
          )
        )
      ) {
        this.timeFields.push(
          ...this.initialDimensionConfig.filter(
            config => config.displayName === 'Time'
          )[0].subFields
        )
      }

      // Load ModuleTypes
      if (data.moduleType && data.moduleType.length !== 0) {
        for (let type of data.moduleType) {
          let temp = {}
          ;(temp['displayName'] = type.displayName), (temp['id'] = type.type)
          temp['selected'] = this.moduleTypes.length === 0 ? true : false
          temp['field_id'] = type.type

          this.moduleTypes.push(temp)
        }
      }
    },
    prepareData(data) {
      this.initialDimensionConfig = []
      this.groupBy = []
      if (data.displayName) {
        this.moduleDisplayName = data.displayName
      } else {
        this.moduleDisplayName = this.moduleName
      }
      // metrics
      if (this.moduleName !== 'workorder') {
        this.getAllDefaults(data)
      } else {
        this.moduleMap = data.moduleMap
        this.parentlookupFileds = data.parentlookupFileds
          ? data.parentlookupFileds
          : {}
        this.timeFields = []
        this.timeFields.push({
          name: 'none',
          displayName: 'None',
          id: 0,
        })
        for (let dimension of data.dimension.time) {
          if (
            dimension.name !== 'sysModifiedTime' &&
            dimension.name !== 'sysCreatedTime'
          ) {
            this.timeFields.push(dimension)
          }
        }
        for (let metric of data.metrics) {
          if (metric.name === 'actualWorkDuration') {
            metric.displayName = 'Work Duration'
          }
          this.metrics.push(metric)
        }
        let defaultMetric = {}
        defaultMetric['displayName'] = 'Number of ' + this.moduleDisplayName
        defaultMetric['defaultMetric'] = true
        defaultMetric['fieldId'] = -1
        defaultMetric['name'] = 'default'
        this.metrics.push(defaultMetric)
        for (let dimension of data.dimensionListOrder) {
          if (dimension.toLowerCase() !== 'resource_fields') {
            let dimensionData = data.dimension[dimension]
            let baseconfig = {
              displayName: this.getStringWithCaps(dimension),
              subFields: [],
              showSubFields: true,
            }

            for (let field of dimensionData) {
              if (field.name.toLowerCase() === 'actualworkstart') {
                field.displayName = 'Start Time'
              }
              field['selected'] = true
              baseconfig.subFields.push(field)
            }
            this.initialDimensionConfig.push(baseconfig)
            if (dimension !== 'time') {
              let fields = []
              for (let field of baseconfig.subFields) {
                if (field.dataType !== 5 && field.dataType !== 6) {
                  fields.push(field)
                }
              }
              let groupBaseField = this.$helpers.cloneObject(baseconfig)
              groupBaseField.subFields = fields
              this.groupBy.push(groupBaseField)
            }
          }
        }
        // load space in initial Dimension config and group by

        this.moduleResourceField = data.dimension['resource_fields'][0]
        let spaceConfig = {
          displayName: 'Space',
          subFields: this.spaceAggregators,
          showSubFields: true,
        }

        if (data.dimension.space) {
          for (let spaceField of data.dimension.space) {
            spaceField['show'] = true
            spaceConfig.subFields.push(spaceField)
          }
        }

        this.initialDimensionConfig.push(spaceConfig)
        this.groupBy.push(spaceConfig)

        // // Load ModuleTypes
        if (data.moduleType && data.moduleType.length !== 0) {
          for (let type of data.moduleType) {
            let temp = {}
            ;(temp['displayName'] = type.displayName), (temp['id'] = type.type)
            temp['selected'] = this.moduleTypes.length === 0 ? true : false
            temp['field_id'] = type.type

            this.moduleTypes.push(temp)
          }
        }
      }
      this.addGroupRequirements()
    },
    updateReportObject() {
      if (this.reportObject) {
        this.reportObject.options.initialConfig = this.config
      }
    },
    addGroupRequirements() {
      for (let groupBy of this.groupBy) {
        for (let field of groupBy.subFields) {
          field['show'] = true
        }
      }
    },
    removeGroupBy() {
      this.config.groupBy.subDimension = null
      this.config.groupBy.dimension = null
      this.updateReportObject()
      this.build(this.config, this.moduleResourceField)
    },
    removeMetric(metricIdx) {
      // only for one Metric
      let metric = this.config.metric[metricIdx]
      delete this.config.metricAggregation[metric.fieldId + '__' + metric.name]
      this.config.metric.splice(metricIdx, 1)
      this.config.sorting = null
      this.config.range = null
      this.build(this.config, this.moduleResourceField)
    },
    handleSort(sortVal) {
      this.config.sorting = sortVal
      this.$set(this.serverConfig, 'sortOrder', sortVal)
    },
    handleRange(rangeVal) {
      this.config.range = rangeVal
      this.$set(this.serverConfig, 'limit', rangeVal)
    },
    setMetricAggregation(metricAggr, metricIdx) {
      let metric = this.config.metric[metricIdx]
      this.toggleMetricAggregation[metric.fieldId + '__' + metric.name] = false
      this.config.metricAggregation[
        metric.fieldId + '__' + metric.name
      ] = metricAggr
      this.updateReportObject()
      if (this.serverConfig.yField) {
        this.serverConfig.yField[metricIdx].aggr = metricAggr
      }
    },
    showReportFolders() {
      let url = '/v3/report/folders?moduleName=asset'
      if (['workorder', 'alarm', 'alarmoccurence'].includes(this.moduleName)) {
        url = '/v3/report/folders?moduleName=' + this.moduleName
      } else if (this.$route.meta.module === 'visitor') {
        url = '/v3/report/folders?moduleName=visitor'
      }

      API.get(url)
        .then(response => {
          if (!response.error) {
            this.reportFolders = response.data.reportFolders
            this.showReportFolderList = true
          }
        })
        .catch(error => {
          this.showReportFolderList = false
          this.reportFolders = []
        })
    },
    closeActivePanel() {
      if (this.secondPanelToggle) {
        this.secondPanelToggle = false
      }
    },
    showActiveArrow(name) {
      this.$nextTick(() => {
        if (this.config.dimension.dimension) {
          if (name === this.config.dimension.dimension.displayName) {
            return true
          } else {
            return false
          }
        } else {
          return false
        }
      })
    },
    showSecondPanel(activator) {
      this.activePanel = activator
      if (activator === this.activePanelEnum.DIMENSION) {
        this.secondPanelToggle = true
      } else if (activator === this.activePanelEnum.METRIC) {
        this.secondPanelToggle = true
      } else {
        // group by case
        this.secondPanelToggle = true
      }
    },
    cancel() {
      this.openPanel = false
      this.$emit('update:openPanel', this.openPanel)
    },
    setDimension(dimension, child) {
      if (this.activePanel === this.activePanelEnum.DIMENSION) {
        if (dimension.displayName === 'Time') {
          this.resetGroupBy()
        }
        this.config.dimension.dimension = dimension
        this.config.dimension.subDimension = child
        this.filtergroupBy()
        this.build(this.config, this.moduleResourceField)
      } else if (this.activePanel === this.activePanelEnum.METRIC) {
        if (this.currentMetric !== null) {
          this.config.metric[this.currentMetric] = dimension
          this.build(this.config, this.moduleResourceField)
        } else if (this.currentMetric == null) {
          this.config.metric.push(dimension)
          this.build(this.config, this.moduleResourceField)
        }
      } else {
        this.config.groupBy.dimension = dimension
        this.config.groupBy.subDimension = child
        this.build(this.config, this.moduleResourceField)
        //  apply group by
      }
      this.filterQuery = null
      this.updateReportObject()
    },
    toggleSideBar() {
      if (this.hideSideBar) {
        this.hideSideBar = false
        if (this.$refs['analyticReport']) {
          this.$refs['analyticReport'].resize()
        }
      } else {
        this.hideSideBar = true
        if (this.$refs['analyticReport']) {
          this.$refs['analyticReport'].resize()
        }
      }
    },
    populateReportObjects(report, result) {
      this.reportObject = report
      this.resultObject = result
      if (this.reportObject !== null && !this.reportObject.name) {
        this.reportObject['name'] = this.reportName
      }
      if (this.reportId && this.savedReport === null) {
        this.savedReport = result.report
      }
      if (
        this.config.dimension.dimension !== null &&
        this.reportObject !== null
      ) {
        this.reportObject.options['initialConfig'] = this.config
      }
      if (
        this.$route.query.duplicate &&
        this.$route.query.reportId &&
        this.reportObject !== null
      ) {
        this.reportObject['name'] = this.computedReportName
        this.reportObject['description'] = this.computedReportDescription
      }
      if (this.reportId && this.config.dimension.dimension === null) {
        let config = this.reconstructConfig(result)
        if (config.userFilters) {
          this.loadAllValues(config)
        } else {
          this.config = config
          this.build(this.config, this.moduleResourceField)
        }
        this.reconstructLookUpFilterConfig(result)
      }
    },
    setCriteria(criteria) {
      this.config.criteria = criteria
      this.updateReportObject()
    },
    setDataFilter(dataFilter) {
      this.config.having = dataFilter
      this.updateReportObject()
    },
    reconstructConfig(result) {
      let config = null
      if (result.report.chartState) {
        if (typeof result.report.chartState === 'object') {
          config = result.report.chartState.initialConfig
        } else {
          let chartState = JSON.parse(result.report.chartState)
          config = chartState.initialConfig
        }

        //initialize reportSettings(drilldown settings)
        if (result.report.reportSettings) {
          //new report has no reportsettings in resp
          config.reportSettings = result.report.reportSettings
        }

        config.reportDrilldownPath = result.report.reportDrilldownPath
      }

      // Done for backward compatibility for existing new reports (metric structure and user Filters)
      if (!Array.isArray(config.metric)) {
        let temp = config.metric
        if (config.metric.defaultMetric) {
          temp['fieldId'] = -1
          temp['name'] = 'default'
          config.metric = []
          config.metric.push(temp)
        } else {
          config.metric.push(temp)
        }

        if (config.metricAggregation) {
          let aggr = config.metricAggregation
          config.metricAggregation = {}
          config.metricAggregation[temp.fieldId + '__' + temp.name] = aggr
        }
        if (!config.hasOwnProperty('userFilters')) {
          config['userFilters'] = null
        }
      }
      if (!config.hasOwnProperty('having')) {
        config['having'] = null
      }
      return config
    },
    getDimensionModuleId(config) {
      if (
        this.moduleMap &&
        Object.keys(this.moduleMap).includes(
          config.dimension.dimension.displayName.toLowerCase()
        )
      ) {
        return this.moduleMap[
          config.dimension.dimension.displayName.toLowerCase()
        ]
      }
      return this.moduleId
    },
    getGroupByModuleId(config) {
      if (
        this.moduleMap &&
        Object.keys(this.moduleMap).includes(
          config.groupBy.dimension.displayName.toLowerCase()
        )
      ) {
        return this.moduleMap[
          config.groupBy.dimension.displayName.toLowerCase()
        ]
      }
      return this.moduleId
    },
    getDimensionLookupFieldId(config) {
      if (
        this.parentlookupFileds &&
        Object.keys(this.parentlookupFileds).includes(
          config.dimension.dimension.displayName.toLowerCase()
        )
      ) {
        return this.parentlookupFileds[
          config.dimension.dimension.displayName.toLowerCase()
        ].fieldId
      }
      return null
    },
    getGroupByLookupFieldId(config) {
      if (
        this.parentlookupFileds &&
        Object.keys(this.parentlookupFileds).includes(
          config.groupBy.dimension.displayName.toLowerCase()
        )
      ) {
        return this.parentlookupFileds[
          config.groupBy.dimension.displayName.toLowerCase()
        ].fieldId
      }
      return null
    },
    buildDimensionObjectForServer(config, moduleResourceField) {
      let serverConfig = {}
      let xField = {}
      if (config.moduleType && this.moduleTypes.length !== 0) {
        serverConfig['moduleType'] = config.moduleType
      } else if (this.moduleTypes.length !== 0) {
        // backward compatability for reports without module type
        config.moduleType = 1
        this.config['moduleType'] = 1
        serverConfig['moduleType'] = 1
      }
      if (config.dimension.dimension.displayName === 'Time') {
        if (config.dimension.subDimension.fieldId === -1) {
          xField['field_id'] = config.dimension.subDimension.name
          xField['nullFieldId'] = true
        } else {
          xField['field_id'] = config.dimension.subDimension.fieldId
          xField['nullFieldId'] = false
        }
        xField['aggr'] =
          this.resultObject &&
          this.resultObject.report.xAggr &&
          (this.resultObject.report.dataPoints[0].xAxis.dataType === 5 ||
            this.resultObject.report.dataPoints[0].xAxis.dataType === 6)
            ? this.resultObject.report.xAggr
            : 12
        xField['module_id'] = this.moduleId
        this.computedModule['meta']['fieldMeta']['xField'] = xField.module_id
        serverConfig['xField'] = xField
        serverConfig['isTime'] = true
      } else if (config.dimension.dimension.displayName === 'Space') {
        if (
          config.dimension.subDimension.displayName === 'Site' &&
          !moduleResourceField
        ) {
          xField['field_id'] = 'siteId'
          xField['module_id'] = this.moduleId
        } else if (
          config.dimension.subDimension.fieldId &&
          config.dimension.subDimension.fieldId !== -1
        ) {
          xField['field_id'] = config.dimension.subDimension.fieldId
          xField['module_id'] = this.moduleMap['space']
        } else {
          xField['field_id'] = moduleResourceField.fieldId
          xField['aggr'] = config.dimension.subDimension.value
          xField['module_id'] = this.moduleId
        }

        this.computedModule['meta']['fieldMeta']['xField'] = xField.module_id
        serverConfig['xField'] = xField
        serverConfig['isTime'] = false
      } else {
        // covered for asset and the module itself
        if (config.dimension.subDimension.fieldId === -1) {
          xField['field_id'] = config.dimension.subDimension.name
        } else {
          xField['field_id'] = config.dimension.subDimension.fieldId
        }
        xField['module_id'] = this.getDimensionModuleId(config)
        let dimensionLookupFieldId = this.getDimensionLookupFieldId(config)
        if (dimensionLookupFieldId) {
          xField['lookupFieldId'] = dimensionLookupFieldId
        }
        xField['selectedIds'] = this.getLookUpFilterIds(config)
        this.computedModule['meta']['fieldMeta']['xField'] = xField.module_id
        if (
          config.dimension.subDimension.dataType === 5 ||
          (config.dimension.subDimension.dataType === 6 &&
            config.dimension.subDimension.moduleId !== this.moduleId)
        ) {
          serverConfig['isTime'] = true
          xField['aggr'] =
            this.resultObject &&
            this.resultObject.report.xAggr &&
            (this.resultObject.report.dataPoints[0].xAxis.dataType === 5 ||
              this.resultObject.report.dataPoints[0].xAxis.dataType === 6)
              ? this.resultObject.report.xAggr
              : 12
        } else {
          serverConfig['isTime'] = false
        }
        serverConfig['xField'] = xField
      }
      if (config.metric.length !== 0) {
        serverConfig['yField'] = []
        for (let metric of config.metric) {
          if (metric.defaultMetric) {
            serverConfig['yField'].push(null)
          } else {
            let yField = {}
            if (metric.fieldId === -1) {
              yField['field_id'] = metric.name
              yField['module_id'] = this.moduleId
              this.computedModule['meta']['fieldMeta']['yField'] =
                yField.module_id
            } else {
              yField['field_id'] = metric.fieldId
              yField['module_id'] = this.moduleId
              this.computedModule['meta']['fieldMeta']['yField'] =
                yField.module_id
            }
            if (config.metricAggregation === null && metric.fieldId) {
              this.config.metricAggregation = {}
              this.config.metricAggregation[
                metric.fieldId + '__' + metric.name
              ] = this.metricAggregation[1].value
              yField['aggr'] = this.config.metricAggregation[
                metric.fieldId + '__' + metric.name
              ]
            } else {
              yField['aggr'] =
                config.metricAggregation[metric.fieldId + '__' + metric.name]
            }
            if (typeof yField['aggr'] === 'undefined') {
              this.config.metricAggregation[
                metric.fieldId + '__' + metric.name
              ] = this.metricAggregation[1].value
              yField['aggr'] = this.config.metricAggregation[
                metric.fieldId + '__' + metric.name
              ]
            }
            serverConfig['yField'].push(yField)
          }
        }
      } else {
        serverConfig['yField'] = null
      }
      if (config.groupBy.dimension && config.groupBy.subDimension) {
        let groupBy = {}
        if (config.groupBy.dimension.displayName === 'Time') {
          if (config.groupBy.subDimension.fieldId === -1) {
            groupBy['field_id'] = config.groupBy.subDimension.name
            groupBy['module_id'] = this.moduleId
            this.computedModule['meta']['fieldMeta']['groupBy'] =
              groupBy.module_id
          } else {
            groupBy['field_id'] = config.groupBy.subDimension.fieldId
            groupBy['module_id'] = this.moduleId
            this.computedModule['meta']['fieldMeta']['groupBy'] =
              groupBy.module_id
          }
          groupBy['aggr'] =
            this.serverConfig.groupBy && this.serverConfig.groupBy.aggr
              ? this.serverConfig.groupBy.aggr
              : 12
          serverConfig['groupBy'] = [groupBy]
        } else if (config.groupBy.dimension.displayName === 'Space') {
          if (config.groupBy.subDimension.displayName === 'Site') {
            groupBy['field_id'] = 'siteId'
            groupBy['module_id'] = this.moduleId
          } else if (
            config.groupBy.subDimension.fieldId &&
            config.groupBy.subDimension.fieldId !== -1
          ) {
            groupBy['field_id'] = config.groupBy.subDimension.fieldId
            groupBy['module_id'] = this.moduleMap['space']
          } else {
            groupBy['field_id'] = moduleResourceField.fieldId
            groupBy['aggr'] = config.groupBy.subDimension.value
            groupBy['module_id'] = this.moduleId
          }

          this.computedModule['meta']['fieldMeta']['groupBy'] =
            groupBy.module_id

          serverConfig['groupBy'] = [groupBy]
        } else {
          // covered for asset and the module itself
          if (config.groupBy.subDimension.fieldId === -1) {
            groupBy['field_id'] = config.groupBy.subDimension.name
          } else {
            groupBy['field_id'] = config.groupBy.subDimension.fieldId
          }
          groupBy['module_id'] = this.getGroupByModuleId(config)
          let groupByLookupFieldId = this.getGroupByLookupFieldId(config)
          if (groupByLookupFieldId) {
            groupBy['lookupFieldId'] = groupByLookupFieldId
          }
          this.computedModule['meta']['fieldMeta']['groupBy'] =
            groupBy.module_id
          serverConfig['groupBy'] = [groupBy]
        }
      } else {
        serverConfig['groupBy'] = null
      }

      // apply criteria
      if (!this.isCriteriaEmpty()) {
        serverConfig['criteria'] = this.config.criteria
      } else {
        serverConfig['criteria'] = null
      }
      // apply criteria
      if (!this.isDataFilterEmpty()) {
        serverConfig['having'] = this.config.having
      } else {
        serverConfig['having'] = null
      }
      // sorting
      if (
        config.dimension.dimension.displayName !== 'Time' &&
        config.groupBy.dimension === null
      ) {
        let sortFields = []
        let sortOrder = 0
        if (
          serverConfig['yField'] &&
          serverConfig['yField'].length > 1 &&
          serverConfig['yField'][0] !== null
        ) {
          let firstYField = serverConfig['yField'][0]
          sortFields.push(firstYField)
        } else {
          sortFields.push({ field_id: 'y-field' })
        }

        if (this.config.sorting) {
          sortOrder = this.config.sorting
        } else {
          this.config.sorting = 3
          sortOrder = this.config.sorting
        }
        serverConfig['sortOrder'] = sortOrder
        serverConfig['sortFields'] = sortFields

        if (config.range) {
          serverConfig['limit'] = config.range
        } else {
          this.config.range = 15
          serverConfig['limit'] = this.config.range
        }
      } else {
        serverConfig['sortFields'] = null
        serverConfig['sortOrder'] = null
        serverConfig['limit'] = null
      }
      if (
        config.customDateField &&
        config.dimension.dimension.displayName !== 'Time'
      ) {
        serverConfig['isCustomDateField'] = true
      } else {
        serverConfig['isCustomDateField'] = false
      }
      if (this.config.userFilters) {
        serverConfig['userFilters'] = this.config.userFilters
      }
      if (this.config.reportSettings) {
        serverConfig['reportSettings'] = this.config.reportSettings
      }
      if (this.config.reportDrilldownPath) {
        serverConfig['reportDrilldownPath'] = this.config.reportDrilldownPath
      }
      // baseline
      if (
        (this.config.customDateField ||
          this.config.dimension.dimension.displayName === 'Time') &&
        this.config.baseLine
      ) {
        serverConfig['baseLine'] = this.config.baseLine
      }
      return serverConfig
    },
    build(config, moduleResourceField) {
      // handle for fsave as report
      this.secondPanelToggle = false

      this.configFromReport = null
      let element = this.buildDimensionObjectForServer(
        config,
        moduleResourceField
      )
      element['mode'] = 4
      element['hideChart'] = false
      if (
        element.xField.aggr &&
        config.dimension.dimension.displayName === 'Time' &&
        this.activePanel === this.activePanelEnum.DIMENSION &&
        !this.serverConfig.dateField
      ) {
        if (!this.savedReport) {
          switch (element.xField.aggr) {
            case this.aggr.YEARLY:
              element['dateFilter'] = NewDateHelper.getDatePickerObject(44)
              break
            case this.aggr.MONTHLY:
            case this.aggr.QUARTERLY:
              element['dateFilter'] = NewDateHelper.getDatePickerObject(44)
              break
            case this.aggr.WEEKLY:
              element['dateFilter'] = NewDateHelper.getDatePickerObject(28)
              break
            case this.aggr.DAILY:
              element['dateFilter'] = NewDateHelper.getDatePickerObject(31)
              break
            case this.aggr.HOURLY:
              element['dateFilter'] = NewDateHelper.getDatePickerObject(22)
              break
          }
        } else {
          element['dateFilter'] = NewDateHelper.getDatePickerObject(
            this.savedReport.dateOperator,
            [
              this.savedReport.dateRange.startTime,
              this.savedReport.dateRange.endTime,
            ]
          )
        }

        let dateField = {}
        dateField['operator'] = element.dateFilter.operatorId
        dateField['field_id'] = this.config.dimension.subDimension.fieldId
        dateField['fieldName'] = this.config.dimension.subDimension.name
        dateField['module_id'] = this.moduleId
        if (
          element.dateFilter.operatorId === 49 ||
          element.dateFilter.operatorId === 50 ||
          element.dateFilter.operatorId === 51
        ) {
          dateField['date_value'] = Math.abs(element.dateFilter.offset) + ''
        } else if (element.dateFilter.operatorId === 20) {
          dateField['date_value'] = element.dateFilter.value.join(',')
        } else {
          dateField['date_value'] = element.dateFilter.value[0] + ''
        }
        element['dateField'] = dateField
      } else {
        if (
          this.savedReport &&
          !this.serverConfig.dateField &&
          this.resultObject.report.dateOperator !== -1
        ) {
          element['dateFilter'] = this.resultObject.dateRange
          element['dateField'] = this.config.customDateField
            ? this.getDateFieldParam(
                this.resultObject.dateRange,
                this.config.customDateField,
                this.moduleId
              )
            : this.getDateFieldParam(
                this.resultObject.dateRange,
                element.xField.field_id + '_' + element.xField.fieldName,
                this.moduleId
              )
        } else {
          if (this.serverConfig.dateField && element.isTime === true) {
            this.serverConfig.dateField[
              'field_id'
            ] = this.config.dimension.subDimension.fieldId
            this.serverConfig.dateField[
              'fieldName'
            ] = this.config.dimension.subDimension.name
            this.serverConfig.dateField['module_id'] = this.moduleId
            element['dateField'] = this.serverConfig.dateField
          } else {
            element['dateField'] = this.serverConfig.dateField
              ? this.computeDateFieldParamFromExisting(
                  this.serverConfig.dateField,
                  element
                )
              : this.getDateFieldParam(
                  NewDateHelper.getDatePickerObject(22),
                  element.xField.field_id + '_' + element.xField.fieldName,
                  this.moduleId
                )
          }
          element['dateFilter'] = this.serverConfig.dateFilter
            ? this.serverConfig.dateFilter
            : NewDateHelper.getDatePickerObject(22)
        }
      }
      if (this.config.metric && this.config.metric.length === 0) {
        this.currentMetric = 0
        let defaultMetric = {}
        if (element.moduleType === 2) {
          defaultMetric['displayName'] = 'Number of ' + 'workrequest' + 's'
        } else {
          defaultMetric['displayName'] = 'Number of ' + this.moduleDisplayName
        }
        defaultMetric['fieldId'] = -1
        defaultMetric['name'] = 'default'
        defaultMetric['defaultMetric'] = true
        this.config.metric.push(defaultMetric)
      }
      this.showChart = true
      this.serverConfig = element
    },
    computeDateFieldParamFromExisting(existingDateField, newServerConfig) {
      let dateField = {}
      let customDateFieldSplit = this.config.customDateField
        ? this.config.customDateField.split(/_(.+)/)
        : [null, null]
      dateField['operator'] = existingDateField.operator
      dateField['field_id'] =
        newServerConfig.isTime === true
          ? newServerConfig.xField.field_id
          : customDateFieldSplit[0]
          ? parseInt(customDateFieldSplit[0])
          : null
      dateField['fieldName'] =
        newServerConfig.isTime === true
          ? newServerConfig.xField.fieldName
          : customDateFieldSplit[1]
      if (newServerConfig.isTime === true) {
        dateField['module_id'] = newServerConfig.xField.module_id
      } else {
        dateField['module_id'] = this.moduleId
      }
      dateField['date_value'] = existingDateField.date_value
      return dateField
    },
    getDateFieldParam(dateObj, element, moduleId) {
      let dateField = {}
      if (isString(element)) {
        let customDateFieldSplit = element.split(/_(.+)/)
        dateField['field_id'] = parseInt(customDateFieldSplit[0])
        dateField['fieldName'] = customDateFieldSplit[1]
      } else {
        dateField['field_id'] = element
        dateField['fieldName'] = null
      }
      dateField['operator'] = dateObj.operatorId
      dateField['module_id'] = moduleId
      if (
        dateObj.operatorId === 49 ||
        dateObj.operatorId === 50 ||
        dateObj.operatorId === 51
      ) {
        dateField['date_value'] = Math.abs(dateObj.offset) + ''
      } else if (dateObj.operatorId === 20) {
        dateField['date_value'] = dateObj.value.join(',')
      } else {
        dateField['date_value'] = dateObj.value[0] + ''
      }
      return dateField
    },
    addCriteriaAndResconstruct() {
      if (!this.isCriteriaEmpty()) {
        if (this.validateOperatorId()) {
          this.criteriaTrigger = true
          this.$set(this.serverConfig, 'criteria', this.config.criteria)
        } else {
          return
        }
      }
      this.$set(this.serverConfig, 'having', this.config.having)
      this.createTrigger = false
      this.showCriteriaBuilder = false
    },
    validateOperatorId() {
      if (this.config.criteria) {
        for (let condition of Object.keys(this.config.criteria.conditions)) {
          if (
            this.config.criteria.conditions[parseInt(condition)].operatorId ===
              '' ||
            this.config.criteria.conditions[parseInt(condition)].operatorId ===
              null
          ) {
            this.$message.error('Operator cannot be null')
            return false
          } else {
            continue
          }
        }
        return true
      }
    },
    lookUpFilter() {
      let self = this
      let field = self.config.dimension.subDimension
      if (field.displayName != this.lookUpFilterConfig.name) {
        if (field.lookupModule.name === 'ticketstatus') {
          let tstatus = this.$store.state.ticketStatus['workorder']
          let tstatusMap = {}
          for (let st of tstatus) {
            tstatusMap[st.id] = st.displayName
          }
          self.lookUpFilterConfig.allValues = tstatusMap
        } else {
          self.lookUpFilterConfig.allValues = {}
          getFieldOptions({
            field: {
              lookupModuleName: field.lookupModule.name,
              skipDeserialize: true,
            },
          }).then(({ error, options }) => {
            if (error) {
              self.$message.error(error.message || 'Error Occured')
            } else {
              self.lookUpFilterConfig.allValues = options
            }
          })
        }
        self.lookUpFilterConfig.name = field.displayName
        self.lookUpFilterConfig.chooseValues = []
        self.showLookUpFilter = true
      } else {
        self.showLookUpFilter = true
      }
    },
    setLookUpFilterConfig(config) {
      this.showLookUpFilter = false
      this.lookUpFilterConfig = config
      this.config.dimension.subDimension.selectedIds = config.chooseValues
      this.serverConfig.xField.selectedIds = config.chooseValues
    },
    getLookUpFilterIds(config) {
      return config.dimension.subDimension.selectedIds
    },
    async reconstructLookUpFilterConfig(result) {
      if (result.report.dataPoints[0].xAxis.selectValuesOnly) {
        let field = this.config.dimension.subDimension
        this.lookUpFilterConfig.chooseValues =
          result.report.dataPoints[0].xAxis.selectValuesOnly
        this.setLookUpFilterConfig(this.lookUpFilterConfig)
        this.lookUpFilterConfig.name = field.displayName
        if (field.lookupModule.name === 'ticketstatus') {
          let tstatus = this.$store.state.ticketStatus['workorder']
          let tstatusMap = {}
          for (let st of tstatus) {
            tstatusMap[st.id] = st.displayName
          }
          this.lookUpFilterConfig.allValues = tstatusMap
        } else {
          let { error, options } = await getFieldOptions({
            field: {
              lookupModuleName: field.lookupModule.name,
              skipDeserialize: true,
            },
          })
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.lookUpFilterConfig.allValues = options
          }
        }
      }
    },
  },
}
</script>
<style lang="scss">
.criteria-scroll {
  overflow: auto;
}
.pattern-visible {
  padding-bottom: 55px;
}
.new-report-page {
  .report-settings-group {
    top: 39px;
    .report-settings-btn {
      padding: 14px 13px;
    }
  }
}
.new-report-page-chart .fc-new-chart-type-single {
  margin-right: -10px !important;
}
</style>
