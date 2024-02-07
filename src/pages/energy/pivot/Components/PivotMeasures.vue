<template>
  <div class="pivot-measures">
    <div class="title-section pB15">
      <div class="title">
        {{ $t('pivot.measures') }}
      </div>
      <div class="hr-line"></div>
    </div>
    <div class="pivot-dimention-list">
      <skeletonLoader
        v-if="showLoader"
        :rowCount="3"
        width="186.5px"
        height="28px"
      />
      <div class="original-list-row" v-if="pivotBaseModuleName && !showLoader">
        <div v-if="isReadingModule">
          <div class="choose-data-module-type mT5" style="width: 186.5px;">
            <div>
              <i
                class="el-icon-search search-bar-icon"
                style="color:#cacdd4;height: 13px;width: 13px;"
                v-if="!showMeasuresSearch"
                @click="showMeasuresSearch = !showMeasuresSearch"
              ></i>
            </div>
            <div class="vertical-divider"></div>
            <div class="measures-filter-selector" v-if="!showMeasuresSearch">
              <div
                v-if="filterBtn"
                class="measures-filter-btn"
                :class="!isReadingModuleSet ? 'filter-active' : ''"
                style="padding-left:2px;width: 50px;height:20px;padding-bottom:1px;"
                @click="setAllModuleType()"
              >
                <InlineSvg
                  src="svgs/pivot/doubletick"
                  :iconClass="
                    !isReadingModuleSet
                      ? 'icon filter-pivot-svg pivot-filter-active scale3'
                      : isReadingModuleSet
                      ? 'icon filter-pivot-svg pivot-disabled-btn scale3'
                      : ''
                  "
                />
                <div>
                  <span class="fW500">{{ $t('pivot.all') }}</span>
                </div>
              </div>
              <div
                v-if="filterBtn"
                class="measures-filter-btn"
                style="width:90px;padding-left:3px; height:20px;padding-bottom:1px;"
                :class="isReadingModuleSet ? 'filter-active' : ''"
                @click="setReadingModuleType()"
              >
                <InlineSvg
                  src="svgs/pivot/hashsquare"
                  :iconClass="
                    isReadingModuleSet
                      ? 'icon filter-pivot-svg pivot-filter-active'
                      : !isReadingModuleSet
                      ? 'icon filter-pivot-svg pivot-disabled-btn'
                      : ''
                  "
                />
                <div>
                  <span class="fW500">{{ $t('pivot.readings') }}</span>
                </div>
              </div>
            </div>
            <div class="search-bar" v-if="showMeasuresSearch">
              <el-input
                size="small"
                placeholder="Search..."
                class="pivot-search-box"
                v-model="pivotDimentionSearch"
              >
                <template slot="prefix" v-if="showMeasuresSearch">
                  <i
                    class="el-icon-search mT8"
                    @click="
                      !showMeasuresSearch
                        ? (showMeasuresSearch = !showMeasuresSearch)
                        : null
                    "
                    style=""
                  ></i>
                </template>
                <template slot="suffix" v-if="showMeasuresSearch">
                  <i
                    class="el-icon-close "
                    @click="showMeasuresSearch = !showMeasuresSearch"
                    style="margin-top: 6px;cursor: pointer;"
                  ></i>
                </template>
              </el-input>
            </div>
            <div class="vertical-line-split">
              <div class="vertical-line"></div>
            </div>
          </div>
        </div>
        <div class="search-bar" v-if="!isReadingModule">
          <el-input
            size="small"
            placeholder="Search..."
            class="pivot-search-box"
            :prefix-icon="'el-icon-search'"
            v-model="pivotDimentionSearch"
          ></el-input>
        </div>
        <div
          class="modules-and-fields-list"
          v-if="reRender && dataModuleType == PIVOT_DATA_MODULE_TYPE.MODULE"
          v-click-outside="nullifyActiveRow"
        >
          <el-collapse v-model="activeRealtedModuleName" accordion>
            <el-collapse-item :name="pivotBaseModule.name">
              <template slot="title">
                <div class="module-title-icon-section" style="margin-left:2px;">
                  <div class="module-icon-section">
                    <img
                      src="~statics/pivot/module-pink.svg"
                      width="14px"
                      height="16px"
                    />
                  </div>
                  <div class="module-name-section base-module-name">
                    {{ pivotBaseModule.displayName }}
                  </div>
                </div>
              </template>

              <div class="original-list-row-full">
                <div
                  class="task-handle mR10"
                  style="padding-left:27px;"
                  v-if="pivotFilteredDimentions.length == 0"
                >
                  No Data
                </div>
                <draggable
                  class="dragArea list-group"
                  :list="pivotFilteredDimentions"
                  :group="{
                    name: 'pivotMeasures',
                    pull: 'clone',
                    put: false,
                  }"
                  @start="isDraggingActive = true"
                  @end="isDraggingActive = false"
                  :options="draggableOptions"
                  :clone="pivotMeasureAdded"
                  v-else
                >
                  <div
                    class="field-row one"
                    v-for="(row, index) in pivotFilteredDimentions"
                    :style="
                      activeRow == row.primary.uuid
                        ? 'background-color: #ecf0f6;'
                        : ''
                    "
                    @click.stop="
                      activeRow == row.primary.uuid
                        ? (activeRow = null)
                        : (activeRow = row.primary.uuid)
                    "
                    :key="index"
                  >
                    <div class="task-handle pointer">
                      <i class="fa fa-hashtag field-icon"></i>
                    </div>
                    <div class="row-body-section">
                      <div
                        :class="
                          activeRow == row.primary.uuid
                            ? 'mT-auto mB-auto each-row active-row-width'
                            : 'mT-auto mB-auto each-row'
                        "
                      >
                        <el-tooltip
                          class="fld-tooltip"
                          effect="dark"
                          placement="top-start"
                          :content="row.primary.displayName"
                          :open-delay="1000"
                        >
                          <span>{{ row.primary.displayName }}</span>
                        </el-tooltip>
                      </div>
                      <div
                        class="move-row-button"
                        @click="moveRow(row)"
                        :style="
                          activeRow == row.primary.uuid
                            ? 'display:inline;padding-top:6px;padding-right:2px;'
                            : ''
                        "
                      >
                        <img
                          src="~statics/pivot/move-row.svg"
                          width="15px"
                          height="15px"
                        />
                      </div>

                      <div
                        class="aggr-select-option mR10"
                        v-if="showAggr"
                        :style="
                          activeRow == row.primary.uuid
                            ? 'display:inline'
                            : 'padding-top:4px;'
                        "
                      >
                        <el-select
                          filterable
                          size="mini"
                          placeholder="Aggr"
                          popper-class="fc-group-select"
                          value-key="label"
                          v-model="row.selectedAggr"
                          @focus="activeRow = row.primary.uuid"
                          @change="reRenderMeasures()"
                        >
                          <el-option
                            v-for="aggr in row.aggrOptions"
                            :key="aggr.label"
                            :label="aggr.label"
                            :value="aggr.value"
                            @click.stop=""
                            class=""
                          ></el-option>
                        </el-select>
                      </div>
                    </div>
                  </div>
                </draggable>
              </div>
            </el-collapse-item>
          </el-collapse>
          <div class="related-header" v-if="!isRelatedFieldsAvailable">
            {{ $t('pivot.related') }}
          </div>
          <div
            class="related-modules-list"
            v-for="(relatedModuleObj, index1) in filteredRealatedModuleMeasures"
            :key="'related-modules-list' + index1"
            @click.stop="scrollAdjust($event)"
          >
            <el-collapse v-model="activeRealtedModuleName" accordion>
              <el-collapse-item
                :name="relatedModuleObj.moduleName"
                v-if="relatedModuleObj.fields.length > 0"
              >
                <template slot="title">
                  <div
                    class="module-title-icon-section"
                    style="margin-left:2px;"
                  >
                    <div class="module-icon-section">
                      <img
                        src="~statics/pivot/module-pink.svg"
                        width="14px"
                        height="16px"
                      />
                    </div>
                    <div class="module-name-section">
                      {{ relatedModuleObj.moduleObj.displayName }}
                    </div>
                  </div>
                </template>
                <div class="original-list-row-full">
                  <draggable
                    class="dragArea list-group"
                    :list="relatedModuleObj.fields"
                    :group="{
                      name: 'pivotMeasures',
                      pull: 'clone',
                      put: false,
                    }"
                    @start="isDraggingActive = true"
                    @end="isDraggingActive = false"
                    :options="draggableOptions"
                    :clone="
                      field =>
                        pivotSubModuleMeasureAdded(
                          field,
                          relatedModuleObj.moduleObj
                        )
                    "
                  >
                    <div
                      class="field-row two"
                      :id="`rml${index1}-row2-${index}`"
                      :style="
                        activeRow == row.primary.uuid
                          ? 'background-color: #ecf0f6;'
                          : ''
                      "
                      @click.stop="
                        activeRow == row.primary.uuid
                          ? (activeRow = null)
                          : (activeRow = row.primary.uuid)
                      "
                      v-for="(row, index) in relatedModuleObj.fields"
                      :key="index"
                    >
                      <div class="task-handle  pointer">
                        <i class="fa fa-hashtag field-icon"></i>
                      </div>
                      <div class="row-body-section">
                        <div
                          :class="
                            activeRow == row.primary.uuid
                              ? 'mT-auto mB-auto each-row active-row-width'
                              : 'mT-auto mB-auto each-row'
                          "
                        >
                          <el-tooltip
                            class="fld-tooltip"
                            effect="dark"
                            placement="top-start"
                            :content="row.primary.displayName"
                            :open-delay="1000"
                          >
                            <span>{{ row.primary.displayName }}</span>
                          </el-tooltip>
                        </div>
                        <div
                          class="move-row-button"
                          @click="moveRow(row)"
                          :style="
                            activeRow == row.primary.uuid
                              ? 'display:inline;padding-top:6px;padding-right:2px;'
                              : ''
                          "
                        >
                          <img
                            src="~statics/pivot/move-row.svg"
                            width="15px"
                            height="15px"
                          />
                        </div>
                        <div
                          v-if="relatedShowAggr"
                          class="aggr-select-option"
                          :style="
                            activeRow == row.primary.uuid
                              ? 'display:inline'
                              : 'padding-top:4px;'
                          "
                        >
                          <!--two--->
                          <el-select
                            filterable
                            default-first-option
                            size="mini"
                            placeholder="Aggr"
                            popper-class="fc-group-select"
                            value-key="label"
                            v-model="row.selectedAggr"
                            @focus="activeRow = row.primary.uuid"
                            @change="reRenderRelatedMeasures()"
                          >
                            <el-option
                              v-for="aggr in row.aggrOptions"
                              :key="aggr.label"
                              :label="aggr.label"
                              :value="aggr.value"
                              @click.stop=""
                            ></el-option>
                          </el-select>
                        </div>
                      </div>
                    </div>
                  </draggable>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
          <div class="related-header" v-if="isReadingModule">
            {{ $t('pivot.readings') }}
          </div>
          <div
            v-if="isReadingModule"
            class="related-readings-list"
            v-for="(asset_categroy, index1) in searchReadingMeasures"
            :key="'related-readings-list' + index1"
            @click.stop="scrollReadingAdjust($event)"
          >
            <el-collapse
              v-model="activeRelatedReadingCategory"
              @change="loadAssetReadingFields(asset_categroy)"
              accordion
            >
              <el-collapse-item>
                <template slot="title">
                  <div
                    class="module-title-icon-section"
                    style="margin-left:2px;"
                  >
                    <div class="module-icon-section">
                      <fc-icon
                        group="action"
                        name="asset-category"
                        color="#ff3184"
                        style="scale:0.8;"
                      ></fc-icon>
                    </div>
                    <div class="module-name-section">
                      <span v-if="asset_categroy.displayName">
                        {{ asset_categroy.displayName }}
                      </span>
                      <span v-else>
                        {{ asset_categroy.name }}
                      </span>
                    </div>
                  </div>
                </template>
                <div class="original-list-row-full">
                  <div
                    class="task-handle mR10"
                    style="padding-left:27px;"
                    v-if="isReadingsLoading"
                  >
                    <skeletonLoader
                      :rowCount="2"
                      width="186.5px"
                      height="28px"
                    />
                    <!-- {{ $t('pivot.empty_reading') }} -->
                  </div>
                  <div
                    v-else-if="
                      asset_categroy.readings &&
                        asset_categroy.readings.length == 0
                    "
                    class="task-handle mR10"
                    style="padding-left:27px;"
                  >
                    {{ $t('pivot.empty_reading') }}
                  </div>
                  <draggable
                    class="dragArea list-group reading-box-height"
                    :list="asset_categroy.readings"
                    :group="{
                      name: 'pivotMeasures',
                      pull: 'clone',
                      put: false,
                    }"
                    @start="isDraggingActive = true"
                    @end="isDraggingActive = false"
                    :options="draggableOptions"
                    :clone="pivotReadingsAdded"
                  >
                    <div
                      class="field-row two"
                      :id="`rml${index1}-row2-${index}`"
                      :style="
                        activeRow == row.primary.uuid
                          ? 'background-color: #ecf0f6;'
                          : ''
                      "
                      @click.stop="
                        activeRow == row.primary.uuid
                          ? (activeRow = null)
                          : (activeRow = row.primary.uuid)
                      "
                      v-for="(row, index) in asset_categroy.readings"
                      :key="index"
                    >
                      <div class="task-handle  pointer">
                        <InlineSvg
                          src="svgs/pivot/hashsquare"
                          iconClass="icon readings-icon"
                        />
                      </div>
                      <div class="row-body-section">
                        <div
                          :class="
                            activeRow == row.primary.uuid
                              ? 'mT-auto mB-auto each-row active-row-width'
                              : 'mT-auto mB-auto each-row'
                          "
                        >
                          <el-tooltip
                            class="fld-tooltip"
                            effect="dark"
                            placement="top-start"
                            :content="row.primary.displayName"
                            :open-delay="1000"
                          >
                            <span>{{ row.primary.displayName }}</span>
                          </el-tooltip>
                        </div>
                        <div
                          class="move-row-button"
                          @click="moveReadingRow(row)"
                          :style="
                            activeRow == row.primary.uuid
                              ? 'display:inline;padding-top:6px;padding-right:2px;'
                              : ''
                          "
                        >
                          <img
                            src="~statics/pivot/move-row.svg"
                            width="15px"
                            height="15px"
                          />
                        </div>
                        <div
                          v-if="relatedShowAggr"
                          class="aggr-select-option"
                          :style="
                            activeRow == row.primary.uuid
                              ? 'display:inline'
                              : 'padding-top:4px;'
                          "
                        >
                          <!--two--->
                          <el-select
                            filterable
                            default-first-option
                            size="mini"
                            placeholder="Aggr"
                            popper-class="fc-group-select"
                            value-key="label"
                            v-model="row.selectedAggr"
                            @focus="activeRow = row.primary.uuid"
                            @change="reRenderRelatedMeasures()"
                          >
                            <el-option
                              v-for="aggr in row.aggrOptions"
                              :key="aggr.label"
                              :label="aggr.label"
                              :value="aggr.value"
                              @click.stop=""
                            ></el-option>
                          </el-select>
                        </div>
                      </div>
                    </div>
                  </draggable>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </div>
        <div
          class="modules-and-fields-list"
          v-if="reRender && dataModuleType == PIVOT_DATA_MODULE_TYPE.READING"
          v-click-outside="nullifyActiveRow"
        >
          <div class="related-header">
            {{ $t('pivot.readings') }}
          </div>
          <div
            class="related-modules-list"
            v-for="(asset_categroy, index1) in searchReadingMeasures"
            :key="'related-modules-list' + index1"
            @click.stop="scrollAdjust($event)"
          >
            <el-collapse
              v-model="activeRelatedReadingCategory"
              @change="loadAssetReadingFields(asset_categroy)"
              accordion
            >
              <el-collapse-item>
                <template slot="title">
                  <div
                    class="module-title-icon-section"
                    style="margin-left:2px;"
                  >
                    <div class="module-icon-section">
                      <fc-icon
                        group="action"
                        name="asset-category"
                        color="#ff3184"
                        style="scale:0.8;"
                      ></fc-icon>
                    </div>
                    <div class="module-name-section">
                      <span v-if="asset_categroy.displayName">
                        {{ asset_categroy.displayName }}
                      </span>
                      <span v-else>
                        {{ asset_categroy.name }}
                      </span>
                    </div>
                  </div>
                </template>
                <div class="original-list-row-full">
                  <div
                    class="task-handle mR10"
                    style="padding-left:27px;"
                    v-if="isReadingsLoading"
                  >
                    <skeletonLoader
                      :rowCount="2"
                      width="186.5px"
                      height="28px"
                    />
                    <!-- {{ $t('pivot.empty_reading') }} -->
                  </div>
                  <div
                    v-else-if="
                      asset_categroy.readings &&
                        asset_categroy.readings.length == 0
                    "
                    class="task-handle mR10"
                    style="padding-left:27px;"
                  >
                    {{ $t('pivot.empty_reading') }}
                  </div>
                  <draggable
                    class="dragArea list-group reading-box-height"
                    :list="asset_categroy.readings"
                    :group="{
                      name: 'pivotMeasures',
                      pull: 'clone',
                      put: false,
                    }"
                    @start="isDraggingActive = true"
                    @end="isDraggingActive = false"
                    :options="draggableOptions"
                    :clone="pivotReadingsAdded"
                  >
                    <div
                      class="field-row two"
                      :id="`rml${index1}-row2-${index}`"
                      :style="
                        activeRow == row.primary.uuid
                          ? 'background-color: #ecf0f6;'
                          : ''
                      "
                      @click.stop="
                        activeRow == row.primary.uuid
                          ? (activeRow = null)
                          : (activeRow = row.primary.uuid)
                      "
                      v-for="(row, index) in asset_categroy.readings"
                      :key="index"
                    >
                      <div class="task-handle  pointer" style="scale:0.8;">
                        <InlineSvg
                          src="svgs/pivot/hashsquare"
                          iconClass="icon readings-icon"
                        />
                      </div>
                      <div class="row-body-section">
                        <div
                          :class="
                            activeRow == row.primary.uuid
                              ? 'mT-auto mB-auto each-row active-row-width'
                              : 'mT-auto mB-auto each-row'
                          "
                        >
                          <el-tooltip
                            class="fld-tooltip"
                            effect="dark"
                            placement="top-start"
                            :content="row.primary.displayName"
                            :open-delay="1000"
                          >
                            <span>{{ row.primary.displayName }}</span>
                          </el-tooltip>
                        </div>
                        <div
                          class="move-row-button"
                          @click="moveReadingRow(row)"
                          :style="
                            activeRow == row.primary.uuid
                              ? 'display:inline;padding-top:6px;padding-right:2px;'
                              : ''
                          "
                        >
                          <img
                            src="~statics/pivot/move-row.svg"
                            width="15px"
                            height="15px"
                          />
                        </div>
                        <div
                          v-if="relatedShowAggr"
                          class="aggr-select-option"
                          :style="
                            activeRow == row.primary.uuid
                              ? 'display:inline'
                              : 'padding-top:4px;'
                          "
                        >
                          <!--two--->
                          <el-select
                            filterable
                            default-first-option
                            size="mini"
                            placeholder="Aggr"
                            popper-class="fc-group-select"
                            value-key="label"
                            v-model="row.selectedAggr"
                            @focus="activeRow = row.primary.uuid"
                            @change="reRenderRelatedMeasures()"
                          >
                            <el-option
                              v-for="aggr in row.aggrOptions"
                              :key="aggr.label"
                              :label="aggr.label"
                              :value="aggr.value"
                              @click.stop=""
                            ></el-option>
                          </el-select>
                        </div>
                      </div>
                    </div>
                  </draggable>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </div>
      </div>
      <div class="vertical-line-split">
        <div class="vertical-line"></div>
      </div>
      <div class="new-list-row">
        <div class="row-header">
          <div class="title-header">
            <img
              src="~statics/pivot/values-icon.svg"
              width="12px"
              height="12px"
              style="margin-top:5px;transform: scale(1.5);"
            />
            <span
              style="margin-top: 5px;padding-left: 5px;position: absolute;"
              >{{ $t('pivot.values') }}</span
            >
          </div>
          <div class="add-function-btn">
            <div>
              <el-button
                class="el-button function-icon-button setup-el-btn"
                @click="openAddFormulaDialog"
                :disabled="isNoConfig"
              >
                <div class="function-btn-inside-container">
                  {{ $t('pivot.addFunction') }}
                </div>
              </el-button>
            </div>
          </div>
        </div>
        <skeletonLoader
          v-if="showLoader"
          :rowCount="1"
          width="186.5px"
          height="28px"
        />
        <draggable
          class="dragArea list-group new-list"
          :class="{ 'dragging-active': isDraggingActive }"
          :list="pivotConfigMeasures"
          :options="draggableOptions"
          @start="isDraggingActive = true"
          @end="isDraggingActive = false"
          @change="pivotMeasureUpdated"
          v-if="!showLoader"
        >
          <div
            class="field-row-new"
            :class="{ activeMeasure: activeColumn === data.alias }"
            @click.stop="triggerActiveColumn(data)"
            v-for="(data, index) in pivotConfigMeasures"
            :key="index"
            @mouseenter="rowHover = data.alias"
            @mouseleave="rowHover = null"
          >
            <el-tooltip
              class="fld-tooltip"
              effect="dark"
              placement="top-start"
              :open-delay="1000"
            >
              <div slot="content">
                <span v-if="data.moduleMeasure && data.moduleMeasure.aggr">
                  {{
                    aggrMap[data.moduleMeasure.aggr] +
                      ' ( ' +
                      data.fieldDisplayName +
                      ' ) '
                  }}
                </span>
                <span v-else>
                  {{ data.fieldDisplayName }}
                </span>
              </div>
              <div>
                <div
                  v-if="
                    data.valueType === valueType.DATA &&
                      data.moduleMeasure &&
                      data.moduleMeasure.moduleType ==
                        PIVOT_DATA_MODULE_TYPE.READING
                  "
                  style="padding-bottom: 3px;padding-right: 3px;scale: 0.8;"
                >
                  <InlineSvg
                    src="svgs/pivot/hashsquare"
                    iconClass="icon readings-icon"
                  />
                </div>
                <div
                  class="value-icon"
                  v-else-if="data.valueType === valueType.DATA"
                >
                  <i
                    class="fa fa-hashtag field-icon"
                    :style="activeColumn === data.alias ? 'color:white' : ''"
                  ></i>
                </div>
                <div
                  class="function-icon"
                  v-else-if="activeColumn === data.alias"
                >
                  <img
                    src="~statics/formula/formula-white.svg"
                    width="12px"
                    height="12px"
                  />
                </div>
                <div class="function-icon" v-else>
                  <img
                    src="~statics/formula/formula-grey.svg"
                    width="12px"
                    height="12px"
                  />
                </div>
                <div class="mT-auto mB-auto field-label-area">
                  <div
                    class="field-label-container"
                    style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;"
                  >
                    {{ data.fieldDisplayName }}
                  </div>
                </div>

                <div class="mL-auto mT-auto mB-auto">
                  <div class="inline" style="display:flex;align-items:center;">
                    <div
                      class="pointer pivot-icon-hover config-pivot-icon"
                      @click="() => openPivotMeasureSettings(data)"
                      style="padding: 3px 7px;
                       scale: 0.7;"
                    >
                      <InlineSvg
                        src="svgs/pivot/config"
                        iconClass="icon icon-sm15 iconmT3"
                        v-if="data.valueType === valueType.DATA"
                      />
                      <InlineSvg
                        src="svgs/pivot/edit"
                        iconClass="icon edit-pivot-svg"
                        v-else
                      />
                    </div>
                    <i
                      class="el-icon-close pointer field-icon-new pivot-icon-hover"
                      :style="activeColumn === data.alias ? 'color:white' : ''"
                      @click.stop="() => pivotMeasureRemoved(data)"
                    ></i>
                  </div>
                </div>
              </div>
            </el-tooltip>
          </div>
          <div
            class="config-instruction-section"
            v-if="pivotConfigMeasures.length == 0"
            :key="677"
          >
            {{ $t('pivot.dropMeasures') }}
          </div>
        </draggable>
      </div>
      <PivotFormulaDialogNew
        v-if="showAddFormulaDialog"
        :visibility.sync="showAddFormulaDialog"
        :moduleName="pivotBaseModuleName"
        :formatConfig="formatConfig"
        :editConfig="editConfig"
        :label="label"
        :pivotFieldMap="pivotFieldMap"
        @save="pivotFormulaAdded"
        @update="pivotFormulaUpdated"
      ></PivotFormulaDialogNew>
    </div>
    <pivot-measure-settings-dialog-box
      v-if="showMeasureSettings"
      :visibility.sync="showMeasureSettings"
      :moduleName="pivotMeasuresSettings.moduleName"
      :baseModuleName="pivotBaseModuleName"
      :config="pivotMeasuresSettings.config"
      :fields="pivotMeasuresSettings.fields"
      @updatePivotMeasureSettings="updatePivotMeasureSettings"
    ></pivot-measure-settings-dialog-box>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { v4 as uuid } from 'uuid'
import draggable from 'vuedraggable'
import './../pivot.scss'
import { getUniqueCharAlias } from 'util/utility-methods'
import { defaultColFormat, aggrOptions } from './../PivotDefaults'
import PivotFormulaDialogNew from './PivotFormulaDialogNew.vue'
import skeletonLoader from './skeletonLoader.vue'
import PivotMeasureSettingsDialogBox from './PivotMeasureSettingsDialogBox.vue'

import { mapActions, mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
const readingSupportedModules = ['asset', 'site', 'building', 'floor', 'space']
const PIVOT_DATA_MODULE_TYPE = {
  MODULE: 1,
  READING: 2,
}
const valueType = {
  DATA: 'DATA',
  FORMULA: 'FORMULA',
}

export default {
  name: 'PivotMeasures',
  props: [
    'pivotBaseModuleName',
    'builderConfig',
    'pivotResponse',
    'initaialValues',
    'activeColumn',
  ],
  components: {
    draggable,
    PivotMeasureSettingsDialogBox,
    PivotFormulaDialogNew,
    skeletonLoader,
  },
  watch: {
    pivotBaseModuleName(newVal) {
      if (newVal) {
        this.initMeasures()
      }
    },
    initaialValues(newVal) {
      if (newVal) {
        this.pivotConfigMeasures = newVal
      }
    },
    dataModuleType(newVal) {
      if (newVal) {
        this.filterBtn = false
        this.$nextTick(() => {
          this.filterBtn = true
        })
      }
    },
    showMeasuresSearch(newVal) {
      this.pivotDimentionSearch = newVal ? this.pivotDimentionSearch : null
    },
  },
  created() {
    this.setAggrMap()
    this.isShowReadingDataOptions()
  },
  computed: {
    ...mapState({ assetCategoryOptions: 'assetCategory' }),
    ...mapState({ spaceCategoryOptions: 'spaceCategory' }),
    pivotFilteredDimentions() {
      if (!this.pivotDimentionSearch) return this.transformedPivotMeasures
      let measures = this.transformedPivotMeasures.filter(rowObj =>
        rowObj.primary.displayName
          .toLowerCase()
          .includes(this.pivotDimentionSearch.toLowerCase())
      )
      return measures
    },
    isReadingModuleSet() {
      return this.dataModuleType == this.PIVOT_DATA_MODULE_TYPE.READING
    },
    transformedPivotMeasures() {
      let transformedPivotMeasures = this.transformMeasures(this.pivotMeasures)
      return transformedPivotMeasures
    },

    transformedRelatedModuleMeasures() {
      let transformArray = this.relatedModulesList.map(relatedMod => {
        relatedMod.fields = this.transformMeasures(relatedMod.fields)
        return relatedMod
      })
      return transformArray
    },
    filteredRealatedModuleMeasures() {
      if (!this.pivotDimentionSearch || this.pivotDimentionSearch == '')
        return this.transformedRelatedModuleMeasures

      let filteredValues = JSON.parse(
        JSON.stringify(this.transformedRelatedModuleMeasures)
      )
      filteredValues = filteredValues.map(relatedMod => {
        relatedMod.fields = relatedMod.fields.filter(
          rowObj =>
            rowObj.primary?.displayName &&
            rowObj.primary.displayName
              .toLowerCase()
              .startsWith(this.pivotDimentionSearch.toLowerCase())
        )

        return relatedMod
      })
      return filteredValues
    },
    isRelatedFieldsAvailable() {
      return this.filteredRealatedModuleMeasures.every(
        rm => rm.fields.length == 0
      )
    },
    searchReadingMeasures() {
      if (!this.pivotDimentionSearch || this.pivotDimentionSearch == '') {
        if (this.pivotBaseModuleName == 'asset') {
          return this.assetCategoryOptions
        } else if (this.pivotBaseModuleName == 'space') {
          return this.spaceCategoryOptions
        } else if (
          ['site', 'building', 'floor'].includes(this.pivotBaseModuleName)
        ) {
          return this.allSpaceReadings[this.pivotBaseModuleName]
        }
      }
      let asset_category_cloned = null
      if (this.pivotBaseModuleName == 'asset') {
        asset_category_cloned = cloneDeep(this.assetCategoryOptions)
      } else if (this.pivotBaseModuleName == 'space') {
        asset_category_cloned = cloneDeep(this.spaceCategoryOptions)
      } else if (
        ['site', 'building', 'floor'].includes(this.pivotBaseModuleName)
      ) {
        asset_category_cloned = cloneDeep(
          this.allSpaceReadings[this.pivotBaseModuleName]
        )
      }

      let filteredValues = JSON.parse(JSON.stringify(asset_category_cloned))
      let self = this
      filteredValues = filteredValues?.map(relatedMod => {
        relatedMod.readings = relatedMod?.readings?.filter(
          rowObj =>
            rowObj.primary?.displayName &&
            rowObj.primary.displayName
              .toLowerCase()
              .includes(self.pivotDimentionSearch.toLowerCase())
        )
        return relatedMod
      })
      return filteredValues ?? []
    },
    pivotFieldMap() {
      if (!this.pivotResponse || !this.builderConfig) return []

      let fielsAliases = Object.keys(this.pivotResponse.pivotAliasVsField)
      let formulaValidFieldAliases = []
      let pivotFieldMapLocal = []
      fielsAliases.forEach(key => {
        if (!key.startsWith('formula')) {
          formulaValidFieldAliases.push(key)
        }
      })
      formulaValidFieldAliases.forEach(alias => {
        pivotFieldMapLocal.push({
          alias: alias,
          paramAlias: this.getCharAlias(alias),
          label: this.builderConfig.templateJSON.columnFormatting[alias].label,
        })
      })

      pivotFieldMapLocal.sort((a, b) => {
        if (a.paramAlias > b.paramAlias) {
          return 1
        }
        return -1
      })
      return pivotFieldMapLocal
    },
    isNoConfig() {
      return !(
        this.builderConfig &&
        this.builderConfig.rows.length > 0 &&
        this.builderConfig.values.length > 0
      )
    },
  },
  mounted() {
    if (this.pivotBaseModuleName) {
      this.initMeasures()
    }
  },
  data() {
    return {
      PIVOT_DATA_MODULE_TYPE: PIVOT_DATA_MODULE_TYPE,
      dataModuleType: PIVOT_DATA_MODULE_TYPE.MODULE,
      pivotMeasures: [],
      showMeasuresSearch: false,
      filterBtn: true,
      reRender: true,
      showLoader: true,
      showAggr: true,
      relatedShowAggr: true,
      pivotConfigMeasures: [],
      draggableOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'pivotMeasures',
        sort: true,
      },
      showAddFormulaDialog: false,
      aggrMap: { 28: 'Last Value' },
      aggrOptions: aggrOptions,
      rowHover: null,
      charAliasMap: {},
      activeRow: null,
      label: '',
      activeSelectBox: false,
      meta: {},
      activeRealtedModuleName: null,
      activeRelatedReadingCategory: null,
      pivotMeasuresSettings: {},
      pivotColumnFormat: {},
      lookupFieldOptions: [],
      baseModuelFields: [],
      relatedModulesList: [],
      editConfig: null,
      formatConfig: null,
      pivotDimentionSearch: '',
      showMeasureSettings: false,
      defaultColFormat: defaultColFormat,
      pivotBaseModule: {},
      isDraggingActive: false,
      valueType: valueType,
      //reading variables
      assetCategory: {},
      all_category: { id: -1, name: 'All Category' },
      allSpaceReadings: {
        building: [{ name: 'Building Readings', id: -1 }],
        floor: [{ name: 'Floor Readings', id: -1 }],
        site: [{ name: 'Site Readings', id: -1 }],
      },
      isReadingModule: false,
      isReadingsLoading: false,
    }
  },
  methods: {
    ...mapActions(['loadAssetCategory', 'loadSpaceCategory']),
    setAggrMap() {
      this.aggrOptions.forEach(obj => {
        this.aggrMap[obj.value] = obj.label
      })
    },
    isShowReadingDataOptions() {
      this.isReadingModule = readingSupportedModules.includes(
        this.pivotBaseModuleName
      )
    },

    scrollAdjust(event) {
      const elmTop = event.target
        .closest('.related-modules-list')
        .getBoundingClientRect().top
      const parentTop = event.target
        .closest('.related-modules-list')
        .parentElement.getBoundingClientRect().top
      let temp = event.target.closest('.modules-and-fields-list').scrollTop
      this.$nextTick(() => {
        event.target.closest('.modules-and-fields-list').scrollTop =
          elmTop - parentTop + temp
      })
    },
    scrollReadingAdjust(event) {
      const elmTop = event.target
        .closest('.related-readings-list')
        .getBoundingClientRect().top
      const parentTop = event.target
        .closest('.related-readings-list')
        .parentElement.getBoundingClientRect().top
      let temp = event.target.closest('.modules-and-fields-list').scrollTop
      this.$nextTick(() => {
        event.target.closest('.modules-and-fields-list').scrollTop =
          elmTop - parentTop + temp
      })
    },
    reRenderMeasures() {
      const list = document.querySelector('.modules-and-fields-list')
      const scrollPos = list.scrollTop
      this.showAggr = false
      this.$nextTick(() => {
        this.reRender = true
        this.showAggr = true
        this.scrollTop(scrollPos, list)
      })
    },
    reRenderRelatedMeasures() {
      const list = document.querySelector('.modules-and-fields-list')
      const scrollPos = list.scrollTop
      console.log('sp : ', scrollPos)
      this.relatedShowAggr = false
      this.$nextTick(() => {
        this.reRender = true
        this.relatedShowAggr = true
        this.scrollTop(scrollPos, list)
      })
    },
    scrollTop(pos, list) {
      console.log('pos : ', pos)
      console.log('list : ', list)
      list.scrollTop = pos
    },
    nullifyActiveRow() {
      this.activeRow = null
    },

    triggerActiveColumn(data) {
      this.$emit('activeColumnChanged', data.alias)
      this.nullifyActiveRow()
    },
    async initMeasures() {
      let self = this
      this.isShowReadingDataOptions()
      this.fetchMetricOptions(this.pivotBaseModuleName).then(res => {
        this.pivotMeasures = res
      })
      this.loadNonReadingModuleFields()
      this.loadReleatedModuleOptions()
      this.loadPivotBaseModuleMeta()
      if (this.pivotBaseModuleName == 'asset') {
        await this.loadAssetCategory()
      } else if (this.pivotBaseModuleName == 'space') {
        await this.loadSpaceCategory()
        this.spaceCategoryOptions.splice(0, 0, this.all_category)
      }

      if (this.initialValues)
        this.pivotConfigMeasures = JSON.parse(
          JSON.stringify(this.initialValues)
        )

      // if (this.initialFormula)
      //   this.pivotConfigMeasures = [...this.initialValues]
      this.$emit('measuresLoaded')
      this.showLoader = false
    },

    openAddFormulaDialog() {
      this.showAddFormulaDialog = true
    },
    getCharAlias(alias) {
      let index = Object.keys(this.charAliasMap).findIndex(
        e => this.charAliasMap[e] == alias
      )
      let keys = Object.keys(this.charAliasMap)
      if (index != -1) return keys[index]
      keys = keys.map(e => e.toLowerCase())
      let varAlias = getUniqueCharAlias(keys)
      let result = varAlias.toUpperCase()
      this.charAliasMap[result] = alias
      return result
    },
    transformMeasures(fields) {
      let transformedPivotMeasures = fields.map(field => {
        let rowObject = {}
        rowObject.primary = { ...field }
        rowObject.aggrOptions = [
          {
            label: 'count',
            value: 1,
          },
          {
            label: 'avg',
            value: 2,
          },
          {
            label: 'sum',
            value: 3,
          },
          {
            label: 'min',
            value: 4,
          },
          {
            label: 'max',
            value: 5,
          },
        ]

        rowObject.selectedAggr = 1

        return rowObject
      })

      return transformedPivotMeasures
    },
    transformReadingMeasures(readings) {
      let self = this
      let transformedReadingMeasures = readings.map(field => {
        field.uuid = uuid()
        let rowObject = {}
        rowObject.primary = { ...field }
        if (field && !field.hasOwnProperty('displayName')) {
          rowObject.primary.displayName = field.name
        }
        if (field && field.hasOwnProperty('kpiType')) {
          rowObject.primary.id = field.readingFieldId
          rowObject.primary.moduleId = field.readingModuleId
        }
        rowObject.aggrOptions = [
          {
            label: 'avg',
            value: 2,
          },
          {
            label: 'sum',
            value: 3,
          },
          {
            label: 'min',
            value: 4,
          },
          {
            label: 'max',
            value: 5,
          },
          {
            label: 'last value',
            value: 28,
          },
        ]
        rowObject.selectedAggr = 3
        // if (
        //   typeof field.dataTypeEnum == 'object' &&
        //   field.dataTypeEnum.typeAsString == 'Boolean'
        // ) {
        //   rowObject.aggrOptions = [{ label: 'last value', value: 28 }]
        //   rowObject.selectedAggr = 28
        // }

        return rowObject
      })

      return transformedReadingMeasures ?? []
    },
    async loadNonReadingModuleFields() {
      let { data, error } = await API.get(
        `v2/modules/meta/${this.pivotBaseModuleName}`
      )

      if (error) {
        this.$message.error('Error loading all fields for module')
      } else {
        this.baseModuelFields = data.meta.fields.map(field => {
          field.uuid = uuid()
          return field
        })
      }
    },

    async loadReleatedModuleOptions() {
      let resp = await API.get('/v2/report/getDataModuleList', {
        moduleName: this.pivotBaseModuleName,
      })

      let { data, error } = resp
      if (error) {
        this.$message.error('Error loading  module list')
      } else {
        let submodules = data.modules
        //module submodule up to itself case, wo -parent wo , remove the submodule here

        this.relatedModulesList = await Promise.all(
          submodules
            .filter(module => module.name != this.pivotBaseModuleName)
            .map(async moduleObj => {
              let submoduleObj = {
                moduleName: moduleObj.name,
                moduleObj: moduleObj,
                fields: await this.fetchMetricOptions(moduleObj.name),
              }
              return submoduleObj
            })
        )
      }
      this.loading = false
    },

    async fetchMetricOptions(nonReadingModule) {
      let resp = await API.get('v2/report/getMetricsList', {
        moduleName: nonReadingModule,
      })
      let { data, error } = resp
      if (error) {
        this.$message.error('Error loading metric list')
      } else {
        let no_of_measures = []
        let measure_fields = data.Fields.filter(field => {
          field.uuid = uuid()
          if (field.columnName && field.columnName === 'ID') {
            no_of_measures.push(field)
          } else {
            return field
          }
        })
        if (no_of_measures.length) {
          measure_fields.splice(0, 0, no_of_measures[0])
        }
        return measure_fields
      }
    },
    async loadPivotBaseModuleMeta() {
      let moduleMetaResp = await API.get(
        `v2/modules/meta/${this.pivotBaseModuleName}`
      )
      let { data, error } = moduleMetaResp
      if (error) {
        this.$message.error('Error fetching base module meta')
      } else {
        this.pivotBaseModule = JSON.parse(JSON.stringify(data.meta))
        this.activeRealtedModuleName = this.pivotBaseModule.name
      }
    },

    pivotMeasureUpdated({ added }) {
      if (added) {
        let element = added.element
        if (element.valueType === valueType.DATA) {
          let formatConfig = this.defaultColFormat.dataColumn
          if (element?.moduleMeasure?.moduleType == 2) {
            formatConfig.label = element.moduleMeasure.readingField.displayName
          } else if (
            !isEmpty(element.moduleMeasure?.readingField?.displayName)
          ) {
            element.moduleMeasure.moduleType = 2
            formatConfig.label = element.moduleMeasure.readingField.displayName
          } else {
            formatConfig.label = element.moduleMeasure.field.displayName
          }
          this.pivotColumnFormat[element.alias] = { ...formatConfig }
        }
      }
      this.$emit('pivotMeasureUpdated', {
        measures: this.pivotConfigMeasures,
        formats: this.pivotColumnFormat,
      })
    },
    moveRow(row) {
      let addRow = this.pivotMeasureAdded(row)
      this.pivotConfigMeasures.push(addRow)
      this.pivotMeasureUpdated({ added: { element: addRow } })
    },
    moveReadingRow(row) {
      let addRow = this.pivotReadingsAdded(row)
      this.pivotConfigMeasures.push(addRow)
      this.pivotMeasureUpdated({ added: { element: addRow } })
    },
    pivotMeasureAdded(row) {
      let alias = `data_${getUniqueCharAlias(
        this.pivotConfigMeasures.map(row => row.alias.split('_')[1])
      )}`
      let rowObj = {
        moduleType: 1,
        aggr: row.selectedAggr,
        baselineLabel: null,
        excludeFromTimelineFilter: false,
        field: row.primary,
        alias: alias,
        moduleName: row.primary.module.name,
        dateFieldId: -99,
        datePeriod: 22,
        criteria: null,
        dataFilter:null,
      }

      return {
        moduleMeasure: rowObj,
        valueType: valueType.DATA,
        alias: alias,
        fieldDisplayName: row.primary.displayName,
      }
    },
    pivotSubModuleMeasureAdded(row) {
      let alias = `data_${getUniqueCharAlias(
        this.pivotConfigMeasures.map(row => row.alias.split('_')[1])
      )}`
      let rowObj = {
        valueType: valueType.DATA,
        moduleType: 1,
        aggr: row.selectedAggr,
        baselineLabel: null,
        excludeFromTimelineFilter: false,
        field: row.primary,
        alias: alias,
        moduleName: row.primary.module.name,
        dateFieldId: -99,
        datePeriod: 22,
        criteria: null,
        subModuleFieldId: row.primary.id,
        dataFilter:null,
      }

      this.pivotConfigMeasures.push({
        moduleMeasure: rowObj,
        valueType: valueType.DATA,
        alias: alias,
        fieldDisplayName: row.primary.displayName,
      })
      let formatConfig = this.defaultColFormat.dataColumn
      formatConfig.label = row.primary.displayName
      this.pivotColumnFormat[alias] = { ...formatConfig }

      this.$emit('pivotMeasureUpdated', {
        measures: this.pivotConfigMeasures,
        formats: this.pivotColumnFormat,
      })
    },
    pivotFormulaAdded({ formula, formatting }) {
      formula.alias = `formula_${getUniqueCharAlias(
        this.pivotConfigMeasures.map(row => row.alias.split('_')[1])
      )}`
      formula = { ...formula }
      this.pivotColumnFormat[formula.alias] = formatting
      this.pivotConfigMeasures.push({
        customMeasure: formula,
        alias: formula.alias,
        valueType: valueType.FORMULA,
        fieldDisplayName: formatting.label,
      })
      this.showAddFormulaDialog = false

      this.$emit('pivotMeasureUpdated', {
        measures: this.pivotConfigMeasures,
        formats: this.pivotColumnFormat,
      })
    },
    pivotFormulaUpdated({ params }) {
      let { formula, formatting } = params
      let index = this.pivotConfigMeasures.findIndex(
        val => val.alias === formula.alias
      )
      if (index !== -1) {
        this.pivotConfigMeasures[index] = {
          customMeasure: formula,
          alias: formula.alias,
          valueType: valueType.FORMULA,
          fieldDisplayName: formatting.label,
        }
      }
      this.showAddFormulaDialog = false
      this.$emit('pivotMeasureUpdated', {
        measures: this.pivotConfigMeasures,
        formats: this.pivotColumnFormat,
      })
    },
    pivotMeasureRemoved(measure) {
      this.$emit('deleteRowValue', measure.alias)
    },
    mouseEnterRow(uuid) {
      if (!this.mouseEnterRow) this.activeRow = uuid
    },
    mouseLeaveRow() {},
    openPivotMeasureSettings(measure) {
      if (measure.alias.startsWith('data')) {
        this.pivotMeasuresSettings['moduleName'] =
          measure.moduleMeasure.moduleName
        this.pivotMeasuresSettings['config'] = { ...measure.moduleMeasure }
        this.pivotMeasuresSettings['fields'] = measure.moduleMeasure?.field
          ?.module?.fields
          ? [...measure.moduleMeasure.field.module.fields]
          : [...this.baseModuelFields]
        this.showMeasureSettings = true
      } else {
        this.editConfig = measure.customMeasure
        this.label = measure.fieldDisplayName
        this.formatConfig = this.pivotColumnFormat[measure.alias]
        this.showAddFormulaDialog = true
      }
    },
    updatePivotMeasureSettings(config) {
      this.pivotMeasuresSettings = {}
      let index = this.pivotConfigMeasures.findIndex(
        measure => measure.alias == config.alias
      )
      if (index == -1) return

      this.pivotConfigMeasures[index] = {
        ...this.pivotConfigMeasures[index],
        ...config,
      }
      if (this.pivotConfigMeasures[index].alias.startsWith('data')) {
        this.pivotConfigMeasures[index].moduleMeasure = {
          ...this.pivotConfigMeasures[index].moduleMeasure,
          ...config,
        }
      } else {
        this.pivotConfigMeasures[index].customMeasure = {
          ...this.pivotConfigMeasures[index].customMeasure,
          ...config,
        }
      }

      this.$emit('pivotMeasureUpdated', {
        measures: this.pivotConfigMeasures,
        formats: this.pivotColumnFormat,
      })
    },
    changeMeasuresDataType() {
      this.nullifyActiveRow()
      // if (!this.isEditing) this.resetDefauts()
      if (this.dataModuleType == PIVOT_DATA_MODULE_TYPE.MODULE) {
        this.loadNonReadingModuleFields()
      }
    },
    async loadSiteBuildingFloorReadings(moduleName, space_category) {
      let { data, error } = await API.post('reading/getallspacetypereadings', {
        spaceType: this.getSpaceType(moduleName),
      })
      if (error) {
        this.$message.error(`error loading reading fields for ${moduleName}`)
      } else {
        let readings = this.getReadingFieldForSpaceModules(data)
        if (!isEmpty(readings)) {
          this.$set(
            space_category,
            'readings',
            this.transformReadingMeasures(
              this.getReadingFieldForSpaceModules(data)
            )
          )
        }
      }
    },
    getReadingFieldForSpaceModules(data) {
      let fields = []
      let keys = Object.keys(data.moduleMap)
      keys.forEach(key => {
        let fields_map = data.moduleMap[key]
        if (!isEmpty(fields_map) && !isEmpty(fields_map.length)) {
          fields_map.forEach(field_obj => {
            if (
              !isEmpty(field_obj.fields) &&
              !isEmpty(field_obj.fields.length)
            ) {
              fields.push(...field_obj.fields)
            }
          })
        }
      })
      return fields
    },
    getSpaceType(moduleName) {
      switch (moduleName) {
        case 'site':
          return 'Sites'

        case 'building':
          return 'Buildings'

        case 'floor':
          return 'Floors'
      }
    },

    pivotReadingsAdded(row) {
      let alias = `data_${getUniqueCharAlias(
        this.pivotConfigMeasures.map(row => row.alias.split('_')[1])
      )}`
      let rowObj = {
        valueType: valueType.DATA,
        moduleType: this.dataModuleType,
        aggr: row.selectedAggr,
        baselineLabel: null,
        excludeFromTimelineFilter: false,
        readingField: row.primary,
        alias: alias,
        dateFieldId: -1,
        datePeriod: 22,
        moduleName: row?.primary?.module?.name,
        criteria:null,
        dataFilter:null,
      }

      return {
        moduleMeasure: rowObj,
        valueType: valueType.DATA,
        alias: alias,
        fieldDisplayName: row.primary.displayName,
      }
      // let formatConfig = this.defaultColFormat.dataColumn
      // formatConfig.label = row.primary.displayName
      // this.pivotColumnFormat[alias] = { ...formatConfig }

      // this.$emit('pivotMeasureUpdated', {
      //   measures: this.pivotConfigMeasures,
      //   formats: this.pivotColumnFormat,
      // })
    },
    async loadAssetReadingFields(asset_category) {
      try {
        this.isReadingsLoading = true
        const reading_url =
          this.pivotBaseModuleName === 'space'
            ? 'v2/readings/spacecategory'
            : 'v2/readings/assetcategory'

        if (['site', 'building', 'floor'].includes(this.pivotBaseModuleName)) {
          return await this.loadSiteBuildingFloorReadings(
            this.pivotBaseModuleName,
            asset_category
          )
        }

        let kpiFilter = JSON.stringify({
          assetCategory: { operatorId: 36, value: [asset_category.id + ''] },
          kpiType: { operatorId: 54, value: ['1'] },
          oneLevelLookup: {},
        })
        let [data1, data2, data3] = await Promise.all([
          await API.get(
            reading_url,
            {
              id: asset_category.id,
              excludeEmptyFields: false,
              fetchValidationRules: false,
              readingType: 'connected',
            },
            { force: true }
          ),
          await API.get(
            reading_url,
            {
              id: asset_category.id,
              excludeEmptyFields: false,
              fetchValidationRules: false,
              readingType: 'available',
            },
            { force: true }
          ),
          await API.get(
            'v1/setup/readingkpi',
            { filters: kpiFilter },
            { force: true }
          ),
        ])

        if (data1.data.error || data2.data.error || data3.data.error) {
          throw new Error('Error loading reading fields for asset category')
        }

        let readings = [
          ...data1.data.readings,
          ...data2.data.readings,
          ...data3.data.data.readingkpi,
        ]
        if (readings.length > 0 && !isEmpty(this.pivotDimentionSearch)) {
          const searchKeyword = this.pivotDimentionSearch.toLowerCase()
          readings = readings.filter(rowObj => {
            if (rowObj?.displayName?.toLowerCase().includes(searchKeyword)) {
              return rowObj
            } else if (
              rowObj?.name &&
              rowObj.name.toLowerCase().includes(searchKeyword)
            ) {
              return rowObj
            }
          })
        }

        this.$set(
          asset_category,
          'readings',
          this.transformReadingMeasures(readings)
        )
        this.isReadingsLoading = false
      } catch (error) {
        this.isReadingsLoading = false
        this.$message.error(error.message)
      }
    },
    setAllModuleType() {
      this.dataModuleType = this.PIVOT_DATA_MODULE_TYPE.MODULE
    },
    setReadingModuleType() {
      this.dataModuleType = this.PIVOT_DATA_MODULE_TYPE.READING
    },
    // getParentModuleId() {
    //   if (this.pivotBaseModuleName == 'asset') {
    //     return this.assetCategory.assetModuleID
    //   } else if (this.pivotBaseModuleName == 'space') {
    //     return this.spaceCategoryId
    //   }
    // },
  },
}
</script>

<style lang="scss">
.pivot-measures {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 46vh;

  .choose-data-module-type {
    align-items: center;
    justify-content: space-between;
    position: relative;
    .search-bar-icon {
      position: absolute;
      left: 4.5px;
      top: 5.5px;
      scale: 0.95;
      cursor: pointer;
    }
    .vertical-divider {
      width: 1px;
      height: 24px;
      border: solid 0.5px #dbdee5;
      margin-left: 30px;
      position: absolute;
    }
    .fW500 {
      font-weight: 500;
      font-size: 12px;
    }
  }
  .measures-filter-selector {
    display: flex;
    height: 25px;
    width: 100%;
    margin-left: 34px;
    align-items: center;
    .measures-filter-btn {
      border-radius: 10px;
      border: solid 1px rgba(152, 159, 170, 0.5);
      color: rgba(152, 159, 170, 0.5);
      margin-left: 10px;
      display: flex;
      align-items: center;
      justify-content: space-evenly;
      cursor: pointer;
      padding: 0 3px;
    }
    .filter-active {
      border: solid 1px rgba(152, 159, 170, 0.5) !important;
      border: solid 1px #324056 !important;
      color: #324056 !important;
      background-color: rgba(200, 210, 224, 0.4) !important;
    }
    .mT8 {
      margin-top: 8px;
    }
  }
  .pivot-icon-hover {
    &:hover {
      .cls-1,
      .cls-2,
      .cls-3 {
        fill: black;
      }
      color: black !important;
      border-radius: 50%;
    }
  }
  .active-row-width {
    text-overflow: ellipsis;
    overflow: hidden;
    width: 135px;
  }
  .pivot-search-box {
    .el-input__prefix {
      top: -3px;
    }
    .el-input__icon {
      padding-right: 14px;
    }
    .el-input__inner {
      padding-left: 22px !important;
    }
  }

  .modules-and-fields-list {
    padding-top: 5px;
    overflow-y: scroll;
  }

  .module-title-icon-section {
    display: flex;
    align-content: space-between;
    align-items: center;
    justify-content: space-around;
  }

  .config-instruction-section {
    height: 100%;
    width: 100%;
    background: #fbfdff;
    color: #a2a9b3;
    display: flex;
    align-content: center;
    align-items: center;
    justify-content: space-around;
  }

  .module-icon-section {
    padding-right: 10px;
  }

  .el-collapse-item__header {
    line-height: 0px !important;
    height: 30px !important;
  }

  .field-hover {
    background: #ecf0f6 !important;
    cursor: move;
  }

  .field-icon-new {
    padding-left: 3.5px;
    color: #324056;
    font-size: 13px;
  }

  .field-icon {
    padding-left: 5px;
    color: #8d96a4;
    font-size: 10px;
  }

  .resize-line-item {
    width: 135px;
    text-overflow: ellipsis;
    overflow: hidden;
  }

  .field-label-area {
    overflow: hidden;
  }

  .field-row {
    padding: 2px 2px !important;
    display: flex;
    box-shadow: none !important;
    border: none !important;
    width: 100%;
    text-overflow: ellipsis;
    font-weight: 300;
    height: 28px;
    white-space: nowrap;
    -webkit-box-pack: end;
    -ms-flex-pack: end;
    font-size: 13px;
    color: #324056;
    align-items: center;
    letter-spacing: 0.5px;
  }
  .field-row {
    &:hover {
      background: #ecf0f6 !important;
      border-radius: 3px;
      cursor: move;
      .each-row {
        text-overflow: ellipsis;
        overflow: hidden;
        width: 135px;
      }
      .aggr-select-option {
        display: inline;
        padding-top: 4px;
      }
    }
  }

  .field-row-new {
    background: #ecf0f6;
    color: #324056;
    padding: 0px 5px;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    margin: 5px 5px;
    font-size: 13px;
    /* width: 204px; */
    border-radius: 3px;
    cursor: move;
    height: 28px;
    align-items: center;
    letter-spacing: 0.5px;
    &:hover {
      .config-pivot-icon {
        display: inline;
      }
    }
    .fld-tooltip {
      display: flex;
      width: 186.5px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
  .config-pivot-icon {
    display: none;
  }

  .row-header {
    display: flex;
    justify-content: space-between;
    padding-right: 10px;
    padding-bottom: 5px;
    padding-top: 10px;
  }

  .original-list-row {
    display: flex;
    width: 186.5px;
    flex-direction: column;
  }

  .value-icon {
    height: 12px;
    width: 8px;
    padding-right: 20px;
    color: #324056;
    font-weight: normal;
    font-size: 10px;
    align-self: center;
  }
  .function-icon {
    color: #324056;
    font-weight: normal;
    font-size: 10px;
    height: 12px;
    width: 8px;
    padding-right: 20px;
    padding-left: 4px;
    align-self: center;
  }
  .original-list-row-full {
    overflow-y: scroll;
    overflow-x: scroll;
    text-overflow: ellipsis;
    .el-input .el-input__inner,
    .el-textarea .el-textarea__inner {
      border: 1px solid #d8dce5;
      padding: 3px;
      background: #c8d2e0 !important;
      border-radius: 4px;
      height: 20px !important;
      line-height: 20px !important;
      border-radius: 3px !important;
      letter-spacing: 0.3px;
      font-size: 10px;
      color: #324056;
      text-overflow: ellipsis;
      font-weight: 400;
      white-space: nowrap;
      width: 100px;
    }
  }

  .pivot-dimention-list {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    padding-bottom: 2px;
    overflow: hidden;
  }

  .dragArea.list-group {
    height: 100%;
  }

  .dragArea.list-group.new-list {
    height: 32vh;
    width: 186.5px;
    border: 1px solid #ebedf5;
    overflow: scroll;
    border-radius: 3px;
  }

  .dragArea.list-group.new-list.dragging-active {
    border: 1px dashed #ff6116 !important;
  }

  .new-list-row {
    height: 350px;
    text-overflow: ellipsis;
    font-weight: 400;
    white-space: nowrap;
    overflow: hidden;
  }

  .vertical-line-split {
    width: 27px;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-around;
  }

  .vertical-line {
    width: 1px;
    height: 100%;
    border-radius: 3px;
    border-right: solid 0.5px #dbdee5;
  }

  .row-body-section {
    display: flex;
    justify-content: space-between;
    width: calc(100% - 20px);
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
  }

  .el-input .el-input__inner,
  .el-textarea .el-textarea__inner {
    border: 1px solid #d8dce5;
    padding: 10px;
    border-radius: 4px;
    height: 25px !important;
    line-height: 25px !important;
    border-radius: 3px !important;
    letter-spacing: 0.3px;
    color: #324056;
    text-overflow: ellipsis;
    font-weight: 400;
    white-space: nowrap;
  }
  .title {
    font-size: 13px;
    padding-bottom: 7px;
    font-weight: 500;
    letter-spacing: 0.5px;
  }
  .title-header {
    font-size: 12px;
  }
  .hr-line {
    width: 40px;
    border-bottom: 2px solid #ff3184;
  }

  .module-name-section {
    color: #ff3184;
    font-size: 12px;
    font-weight: 500;
  }

  .el-collapse-item__header {
    border: none !important;
  }

  .el-collapse {
    border: none !important;
  }

  .el-collapse-item__arrow {
    color: #ff3184;
  }

  .related-header {
    padding-top: 15px;
    padding-bottom: 10px;
    font-size: 13px;
    font-weight: 500;
    letter-spacing: 0.5px;
  }

  .task-handle {
    padding-right: 10px;
  }
  .move-row-button {
    display: none;
    padding-top: 6px;
  }
  .field-row {
    &:hover {
      .move-row-button {
        display: inline;
        padding-top: 6px;
        padding-right: 2px;
        &:hover {
          cursor: pointer;
        }
      }
    }
  }

  .function-icon-button {
    height: inherit !important;
    padding: 3px 10px !important;
    font-size: 13px;
    font-weight: normal;
    text-transform: none;
  }
  .function-btn-inside-container {
    letter-spacing: 1.1px !important;
  }
  .activeMeasure {
    background: #647faa !important;
    color: #fff !important;
    .value-icon {
      color: #fff;
    }
    .field-icon-new {
      color: #fff;
    }
    .cls-2 {
      color: white;
    }
  }

  .module-name-section {
    width: 130px;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .module-title-icon-section {
    overflow: hidden;
  }
  .el-collapse-item__header {
    &:hover {
      background-color: #ecf0f6;
      border-radius: 3px;
    }
  }
  .each-row {
    text-overflow: ellipsis;
    overflow: hidden;
  }
  .aggr-select-option {
    font-size: 11px !important;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: normal;
    letter-spacing: 0.5px;
    text-decoration: lowercase !important;
    border-radius: 3px;
    display: none;
    padding-top: 4px;

    color: #324056;
    .el-input__inner {
      width: 45px !important;
      text-transform: lowercase;
      font-size: 10px !important;
    }
    .el-input__suffix {
      top: 5px;
      right: -4px;
      font-size: 10px !important;
    }
    .el-input__inner::placeholder {
      font-size: 10px;
    }
    /* google */
    .el-input__inner::-webkit-input-placeholder {
      font-size: 10px;
    }
    /* firefox */
    .el-input__inner:-moz-placeholder {
      font-size: 10px;
    }
  }
}
</style>
