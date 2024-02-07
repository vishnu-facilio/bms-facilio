<template>
  <div style="position: relative;">
    <div v-if="dataPointObject.length > 0">
      <el-tabs v-model="activeTab">
        <el-tab-pane
          label="Data points"
          name="datapoints"
          v-if="checkOptionActivation('datapoints')"
        >
          <div class="fR"></div>
          <div class="customize-tab-body-axis clearboth">
            <div class="customize-scroll-hidden">
              <div class="customize-scroll datapoints-malign">
                <draggable
                  v-model="dataPointObject"
                  @start="drag = true"
                  @end="drag = false"
                >
                  <div
                    v-for="(dataPoint, index) in dataPointObject.filter(
                      dp => dp.axes !== 'x'
                    )"
                    :key="index"
                    class="analytics-data-point"
                    style="display: flex;"
                  >
                    <div
                      v-if="dataPoint.type === 'datapoint'"
                      class="data-points-drag-block"
                    >
                      <div
                        class="data-points-checkbox"
                        :class="{ 'datapoint-hidden': !dataPoint.visible }"
                      >
                        <i
                          v-if="!report.options.isGroupedByTime"
                          @click="visibilityToggle(dataPoint)"
                          class="pointer el-icon-view"
                        ></i>
                        <img src="~assets/drag-blue.svg" class="cursor-drag" />
                        <div
                          v-if="!dataPointTextToggle[dataPoint.key]"
                          @dblclick="
                            ;(dataPointTextToggle[dataPoint.key] =
                              dataPoint.pointType !== 2),
                              $forceUpdate()
                          "
                          class="label-txt-black pL10 pR10"
                        >
                          {{ dataPoint.label }}
                        </div>
                        <el-input
                          ref="dataPointEditName"
                          v-if="dataPointTextToggle[dataPoint.key]"
                          v-model="dataPoint.label"
                          @blur="checkForEmpty(dataPoint)"
                          @keyup.native.enter="checkForEmpty(dataPoint)"
                          class="mL10"
                          style="width: 100%;margin-top: -3px;"
                        >
                        </el-input>
                      </div>
                      <div class="data-points-settings-icon-set">
                        <div
                          v-if="dataPoint.pointType === 2"
                          @click="editDerivation(dataPoint)"
                        >
                          <i
                            class="el-icon-edit pointer mR10"
                            style="color:#319aa8"
                          ></i>
                        </div>
                        <el-radio-group
                          class="dp-yaxis"
                          v-if="
                            report &&
                              dataPoint.dataTypeId !== 4 &&
                              dataPoint.dataTypeId !== 8
                          "
                          v-model="dataPoint.axes"
                          size="mini"
                          @change="changeYAxis"
                        >
                          <el-radio-button label="y">Y1</el-radio-button>
                          <el-radio-button label="y2">Y2</el-radio-button>
                        </el-radio-group>

                        <el-popover
                          v-if="
                            !dataPoint.isBaseLine &&
                              (report.params.xAggr !== 0 ||
                                (report &&
                                  (report.options.common.mode === 6 ||
                                    report.options.common.mode === 7 ||
                                    report.options.common.mode === 8))) &&
                              dataPoint.pointType !== 2 &&
                              dataPoint.kpiType !== 'DYNAMIC'
                          "
                          width="200"
                          :popper-class="'chart-type-popover'"
                          v-model="popover_aggr_toggle[dataPoint.key]"
                        >
                          <div class="chart-type-selector">
                            <ul>
                              <li
                                v-for="aggr in aggregations"
                                :key="aggr.value"
                                @click="changeAggr(dataPoint, aggr.value)"
                                :class="{
                                  active: dataPoint.aggr === aggr.value,
                                }"
                              >
                                <span class="dp-axis-label">{{
                                  aggr.label
                                }}</span>
                              </li>
                            </ul>
                          </div>
                          <div
                            slot="reference"
                            class="mR10 pointer"
                            title="Change Aggregation"
                            data-arrow="true"
                            v-tippy
                          >
                            <img
                              class="aggr-icon"
                              src="~statics/report/aggr.svg"
                            />
                          </div>
                        </el-popover>
                        <div
                          v-if="dataPoint.dataType === 'ENUM'"
                          @click.stop="openEnumDialog(dataPoint)"
                          class="flex-middle mR5 pointer"
                        >
                          <InlineSvg
                            src="svgs/painter-palette"
                            class="vertical-middle pointer color-pallete"
                            iconClass="icon icon-xs mL5"
                            style="top: 2px;"
                          ></InlineSvg>
                        </div>
                        <div v-else-if="!report.options.isGroupedByTime">
                          <el-color-picker
                            v-model="dataPoint.color"
                            size="mini"
                            class="mL5 mR5"
                            :popper-class="'chart-custom-color-picker'"
                          ></el-color-picker>
                        </div>

                        <el-popover
                          v-model="popover_toggle[dataPoint.key]"
                          placement="right"
                          trigger="click"
                          class="pointer addnew-group pT4 fcPopover fc-popover-p0"
                          width="290"
                        >
                          <outside-click
                            @onOutsideClick="
                              popover_toggle[dataPoint.key] = false
                            "
                            :visibility="popover_toggle[dataPoint.key]"
                          >
                            <ul
                              class="addgroup-toggle-ul"
                              v-if="!report.options.isGroupedByTime"
                            >
                              <div
                                style="display:flex;"
                                v-if="
                                  config &&
                                    (config.mode === 1 || config.mode === 4 ||
                                      config.mode === 'reading') &&
                                    config.period !== 0
                                "
                              >
                                <div>
                                  <el-checkbox
                                    v-model="dataPoint.rightInclusive"
                                    @change="
                                      changeRightInclusive(
                                        dataPoint,
                                        dataPoint.rightInclusive
                                      )
                                    "
                                    class="mT15 mL10"
                                    >{{
                                      $t('common.reports.right_inclusive')
                                    }}</el-checkbox
                                  >
                                </div>
                                <div
                                  class="help-icon-position pointer mT12"
                                  style="scale: 0.7;padding-left: 65px;"
                                >
                                  <el-tooltip
                                    popper-class="tooltip_help_msg_width"
                                    class="fld-tooltip"
                                    effect="dark"
                                    placement="top-start"
                                    :content="
                                      $t(
                                        'common.reports.right_inclusive_help_msg'
                                      )
                                    "
                                    :open-delay="1000"
                                  >
                                    <inline-svg
                                      src="svgs/question"
                                      class="vertical-middle"
                                      iconClass="icon icon-xxlll"
                                    ></inline-svg>
                                  </el-tooltip>
                                </div>
                              </div>
                              <div
                                v-if="groupToggle"
                                class="fc-analytics-chart-group"
                              >
                                <input
                                  v-model="groupName"
                                  placeholder="Enter group name"
                                  class="add-new-group-input mT10 mB10"
                                />&nbsp;<button
                                  @click="addNewGroup(dataPoint)"
                                  class=" pointer group-add-btn mT10 mB10"
                                >
                                  Add
                                </button>
                              </div>
                              <div
                                @click="addNewGroupToggle()"
                                v-if="!groupToggle"
                                class="add-new-group-txt label-txt-blue pointer"
                              >
                                <i
                                  class="el-icon-plus label-txt-blue fwbold"
                                ></i>
                                Add New Group
                              </div>
                              <div v-if="isNewDashboardEnabled">
                                <div
                                  @click="openActionAggr()"
                                  class="add-new-group-txt label-txt-blue pointer"
                                >
                                  <i
                                    class="el-icon-plus label-txt-blue fwbold"
                                  ></i
                                  >Actions Aggr
                                </div>
                                <div v-if="actions_aggr">
                                  <el-select
                                    filterable
                                    placeholder="select"
                                    v-model="dataPoint.rule_aggr_type"
                                    @change="
                                      setActionAggr(
                                        dataPoint,
                                        dataPoint.rule_aggr_type
                                      )
                                    "
                                  >
                                    <el-option value="SUM" label="SUM">
                                    </el-option>
                                    <el-option value="SPLIT" label="SPLIT">
                                    </el-option>
                                  </el-select>
                                </div>
                              </div>
                              <li
                                @click="addToGroup(dataPoint, value)"
                                v-for="(value,
                                filterindex) in dataPointObject.filter(
                                  dp => dp.type === 'group'
                                )"
                                :key="filterindex"
                                class="label-txt-black3 fc-add-new-group-name pointer"
                              >
                                {{ value.label }}
                              </li>
                            </ul>
                            <div
                              class="label-txt-blue pT15 pB10 border-top3 pointer pL10 pR20"
                              v-if="
                                !dataPoint.isBaseLine &&
                                  (report.params.xAggr !== 0 ||
                                    (report &&
                                      (report.options.common.mode === 6 ||
                                        report.options.common.mode === 7 ||
                                        report.options.common.mode === 8))) &&
                                  dataPoint.pointType !== 2 &&
                                  !report.options.isGroupedByTime
                              "
                              @click="showAggrForDuplication = true"
                            >
                              <i class="fa fa-clone pR5"></i> Duplicate
                              Datapoint
                            </div>
                            <!-- duplicatePoint(index,dataPoint) -->
                            <el-radio-group
                              v-if="showAggrForDuplication"
                              v-model="duplicateAggr"
                              @change="
                                duplicatePoint(index, dataPoint, duplicateAggr)
                              "
                              size="mini"
                              class="pT15 pB10 pointer pL20 pR10"
                            >
                              <el-radio-button
                                label="SUM"
                                :value="3"
                              ></el-radio-button>
                              <el-radio-button
                                label="AVG"
                                :value="2"
                              ></el-radio-button>
                              <el-radio-button
                                label="MIN"
                                :value="4"
                              ></el-radio-button>
                              <el-radio-button
                                label="MAX"
                                :value="5"
                              ></el-radio-button>
                            </el-radio-group>
                            <div
                              class="label-txt-red pT15 pB10 border-top3 pointer pL10 pR20"
                              @click="deletePoint(index, dataPoint)"
                            >
                              <i class="el-icon-delete pR5"></i> Remove
                              Datapoint
                            </div>
                          </outside-click>
                          <div slot="reference">
                            <i
                              @click.stop="popover_toggle[dataPoint.key] = true"
                              class="q-icon material-icons cursor-pointer"
                              style="float: right; font-size: 20px; color: rgb(216, 216, 216);"
                              >more_vert</i
                            >
                          </div>
                        </el-popover>
                      </div>
                    </div>

                    <div v-else-if="dataPoint.type === 'rangeGroup'">
                      <div class="mT2">
                        <div
                          v-if="!groupNameTextToggle[dataPoint.groupKey]"
                          @dblclick="
                            $set(groupNameTextToggle, dataPoint.groupKey, true)
                          "
                          class="label-txt-black3 fL"
                        >
                          {{ dataPoint.label }}
                        </div>
                        <el-input
                          v-if="groupNameTextToggle[dataPoint.groupKey]"
                          @keyup.native.enter="groupNameEditToggler(dataPoint)"
                          @blur="groupNameEditToggler(dataPoint)"
                          v-model="dataPoint.label"
                          class="fc-input-full-border2"
                          >{{ dataPoint.label }}</el-input
                        >
                      </div>

                      <div v-if="dataPoint.children.length === 0">
                        <span>No datapoints are available in this group</span>
                      </div>
                      <div
                        v-if="dataPoint.dataType === 'ENUM'"
                        @click.stop="openEnumDialog(dataPoint)"
                        class="flex-middle mR5 pointer"
                      >
                        <InlineSvg
                          src="svgs/painter-palette"
                          class="vertical-middle pointer color-pallete"
                          iconClass="icon icon-xs mL5"
                          style="top: 2px;"
                        ></InlineSvg>
                      </div>
                      <el-color-picker
                        v-else
                        v-model="dataPoint.color"
                        size="mini"
                        class="mL5 mR5 float-right"
                        :popper-class="'chart-custom-color-picker'"
                      ></el-color-picker>
                      <i
                        class="el-icon-delete pointer delete-color mL5 float-right"
                        v-if="!dataPoint.isBaseLine"
                        @click="deletePoint(index, dataPoint)"
                      ></i>
                      <i
                        @click="visibilityToggle(dataPoint)"
                        class=" pointer el-icon-view"
                      ></i>

                      <el-radio-group
                        v-if="report && report.options.common.mode === 1"
                        class="dp-yaxis"
                        v-model="dataPoint.axes"
                        size="mini"
                        @change="changeYAxis"
                      >
                        <el-radio-button label="y">Y1</el-radio-button>
                        <el-radio-button label="y2">Y2</el-radio-button>
                      </el-radio-group>

                      <div
                        v-for="(points, chIndex) in dataPoint.children"
                        :key="chIndex"
                        class="data-points-child"
                        style="width:100%"
                      >
                        <div class="data-points-drag-block mT10">
                          <div
                            class="data-points-checkbox"
                            :class="{ 'datapoint-hidden': !points.visible }"
                          >
                            <div
                              v-if="!dataPointTextToggle[points.key]"
                              @dblclick="
                                ;(dataPointTextToggle[points.key] =
                                  points.pointType !== 2),
                                  $forceUpdate()
                              "
                              class="datapoints-txt"
                            >
                              {{ points.label }}
                            </div>
                            <el-input
                              @blur="checkForEmpty(points)"
                              @keyup.native.enter="checkForEmpty(points)"
                              v-model="points.label"
                              v-if="dataPointTextToggle[points.key]"
                              class="mL10"
                              >{{ points.label }}</el-input
                            >
                          </div>
                          <div class="data-points-settings-icon-set">
                            <div
                              v-if="points.pointType === 2"
                              @click="editDerivation(points)"
                            >
                              <i
                                class="el-icon-edit pointer mR10"
                                style="color:#319aa8"
                              ></i>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div v-else class="data-points-group">
                      <div class="mT2">
                        <div
                          v-if="!groupNameTextToggle[dataPoint.groupKey]"
                          @dblclick="
                            $set(groupNameTextToggle, dataPoint.groupKey, true)
                          "
                          class="label-txt-black3 fL"
                        >
                          {{ dataPoint.label }}
                        </div>
                        <el-input
                          v-if="groupNameTextToggle[dataPoint.groupKey]"
                          @keyup.native.enter="groupNameEditToggler(dataPoint)"
                          @blur="groupNameEditToggler(dataPoint)"
                          v-model="dataPoint.label"
                          class="fc-input-full-border2"
                          >{{ dataPoint.label }}</el-input
                        >
                      </div>
                      <div v-if="dataPoint.children.length === 0">
                        <span>No datapoints are available in this group</span>
                      </div>
                      <div v-else>
                        <draggable
                          v-model="dataPoint.children"
                          @start="drag = true"
                          @end="drag = false"
                        >
                          <div
                            v-for="(points, chIndex) in dataPoint.children"
                            :key="chIndex"
                            class="data-points-child"
                            style="width:100%"
                          >
                            <div
                              class="data-points-drag-block new-data-points-drag-block mT10"
                            >
                              <div
                                class="data-points-checkbox"
                                :class="{ 'datapoint-hidden': !points.visible }"
                              >
                                <img
                                  src="~assets/drag-blue.svg"
                                  class="cursor-drag"
                                />
                                <div
                                  v-if="!dataPointTextToggle[points.key]"
                                  @dblclick="
                                    ;(dataPointTextToggle[points.key] =
                                      points.pointType !== 2),
                                      $forceUpdate()
                                  "
                                  class="label-txt-black pL10 pR10"
                                >
                                  {{ points.label }}
                                </div>
                                <el-input
                                  @blur="checkForEmpty(points)"
                                  @keyup.native.enter="checkForEmpty(points)"
                                  v-model="points.label"
                                  v-if="dataPointTextToggle[points.key]"
                                  class="fc-input-full-border2"
                                  >{{ points.label }}</el-input
                                >
                              </div>
                              <div class="data-points-settings-icon-set">
                                <div
                                  v-if="points.pointType === 2"
                                  @click="editDerivation(points)"
                                >
                                  <i
                                    class="el-icon-edit pointer mR10"
                                    style="color:#319aa8"
                                  ></i>
                                </div>
                                <el-radio-group
                                  v-if="
                                    report &&
                                      report.options.common.mode === 1 &&
                                      points.dataTypeId !== 4 &&
                                      points.dataTypeId !== 8
                                  "
                                  class="dp-yaxis"
                                  v-model="points.axes"
                                  size="mini"
                                  @change="changeYAxis"
                                >
                                  <el-radio-button label="y"
                                    >Y1</el-radio-button
                                  >
                                  <el-radio-button label="y2"
                                    >Y2</el-radio-button
                                  >
                                </el-radio-group>

                                <el-popover
                                  v-if="
                                    !points.isBaseLine &&
                                      report.params.xAggr !== 0 &&
                                      points.pointType !== 2
                                  "
                                  width="200"
                                  :popper-class="'chart-type-popover'"
                                  v-model="popover_aggr_toggle[points.key]"
                                >
                                  <div class="chart-type-selector">
                                    <ul>
                                      <li
                                        v-for="aggr in aggregations"
                                        :key="aggr.value"
                                        @click="changeAggr(points, aggr.value)"
                                        :class="{
                                          active: points.aggr === aggr.value,
                                        }"
                                      >
                                        <span class="dp-axis-label">{{
                                          aggr.label
                                        }}</span>
                                      </li>
                                    </ul>
                                  </div>
                                  <div
                                    slot="reference"
                                    class="mR10 pointer"
                                    title="Change Aggregation"
                                    data-arrow="true"
                                    v-tippy
                                  >
                                    <img
                                      class="aggr-icon"
                                      src="~statics/report/aggr.svg"
                                    />
                                  </div>
                                </el-popover>

                                <div
                                  v-if="dataPoint.dataType === 'ENUM'"
                                  @click.stop="openEnumDialog(dataPoint)"
                                  class="flex-middle mR5 pointer"
                                >
                                  <InlineSvg
                                    src="svgs/painter-palette"
                                    class="vertical-middle pointer color-pallete"
                                    iconClass="icon icon-xs mL5"
                                    style="top: 2px;"
                                  ></InlineSvg>
                                </div>
                                <div
                                  v-else-if="!report.options.isGroupedByTime"
                                >
                                  <el-color-picker
                                    v-model="points.color"
                                    size="mini"
                                    class="mL5 mR5"
                                    :popper-class="'chart-custom-color-picker'"
                                  ></el-color-picker>
                                </div>

                                <el-popover
                                  v-model="popover_toggle[points.key]"
                                  placement="right"
                                  trigger="click"
                                  class="pointer addnew-group pT4 fcPopover fc-popover-p0"
                                  width="290"
                                >
                                  <outside-click
                                    @onOutsideClick="
                                      popover_toggle[points.key] = false
                                    "
                                    :visibility="popover_toggle[points.key]"
                                  >
                                    <ul class="addgroup-toggle-ul">
                                      <div
                                        style="display:flex;"
                                        v-if="
                                          config &&
                                            (config.mode === 1 || config.mode === 4 ||
                                              config.mode === 'reading') &&
                                            config.period !== 0
                                        "
                                      >
                                        <div>
                                          <el-checkbox
                                            v-model="dataPoint.rightInclusive"
                                            @change="
                                              changeRightInclusive(
                                                dataPoint,
                                                dataPoint.rightInclusive
                                              )
                                            "
                                            class="mT15 mL10"
                                            >{{
                                              $t(
                                                'common.reports.right_inclusive'
                                              )
                                            }}</el-checkbox
                                          >
                                        </div>
                                        <div
                                          class="help-icon-position pointer mT12"
                                          style="scale: 0.7;padding-left: 65px;"
                                        >
                                          <el-tooltip
                                            popper-class="tooltip_help_msg_width"
                                            class="fld-tooltip"
                                            effect="dark"
                                            placement="top-start"
                                            :content="
                                              $t(
                                                'common.reports.right_inclusive_help_msg'
                                              )
                                            "
                                            :open-delay="1000"
                                          >
                                            <inline-svg
                                              src="svgs/question"
                                              class="vertical-middle"
                                              iconClass="icon icon-xxlll"
                                            ></inline-svg>
                                          </el-tooltip>
                                        </div>
                                      </div>
                                      <div
                                        @click="addNewGroupToggle()"
                                        v-if="!groupToggle"
                                        class="add-new-group-txt"
                                      >
                                        <i class="el-icon-plus fwbold"></i> Add
                                        New Group
                                      </div>
                                      <div
                                        v-if="groupToggle"
                                        class="fc-analytics-chart-group mL10"
                                      >
                                        <input
                                          v-model="groupName"
                                          placeholder="Enter group name"
                                          class="add-new-group-input mT10 mB10"
                                          style="width: 165px;"
                                        />&nbsp;<button
                                          @click="addNewGroup(dataPoint)"
                                          class="group-add-btn mT10 mB10"
                                        >
                                          Add
                                        </button>
                                      </div>
                                      <div v-if="isNewDashboardEnabled">
                                        <div
                                          @click="openActionAggr()"
                                          class="add-new-group-txt label-txt-blue pointer"
                                        >
                                          <i
                                            class="el-icon-plus label-txt-blue fwbold"
                                          ></i
                                          >Actions Aggr
                                        </div>
                                        <div v-if="actions_aggr">
                                          <el-select
                                            filterable
                                            placeholder="Select"
                                            v-model="dataPoint.rule_aggr_type"
                                            @change="
                                              setActionAggr(
                                                dataPoint,
                                                dataPoint.rule_aggr_type
                                              )
                                            "
                                          >
                                            <el-option value="SUM" label="SUM">
                                            </el-option>
                                            <el-option
                                              value="SPLIT"
                                              label="SPLIT"
                                            >
                                            </el-option>
                                          </el-select>
                                        </div>
                                      </div>
                                      <li
                                        @click="addToGroup(dataPoint, value)"
                                        v-for="(value,
                                        filterindex) in dataPointObject.filter(
                                          dp =>
                                            dp.type === 'group' &&
                                            dp.name !== dataPoint.name
                                        )"
                                        :key="filterindex"
                                        class="label-txt-black3 fc-add-new-group-name pointer"
                                      >
                                        {{ value.label }}
                                      </li>
                                    </ul>
                                    <div
                                      class="label-txt-red pT15 pB10 border-top3 pointer pL10 pR20"
                                      @click="
                                        removeFromGroup(dataPoint, points)
                                      "
                                    >
                                      <i class="el-icon-delete pR5"></i> Remove
                                      From Group
                                    </div>
                                  </outside-click>
                                  <div slot="reference">
                                    <i
                                      class="q-icon material-icons cursor-pointer"
                                      style="float: right; font-size: 20px; color: rgb(216, 216, 216);"
                                      @click.stop="
                                        popover_toggle[points.key] = true
                                      "
                                      >more_vert</i
                                    >
                                  </div>
                                </el-popover>
                              </div>
                            </div>
                          </div>
                        </draggable>
                      </div>
                    </div>
                  </div>
                </draggable>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane
          label="Axis"
          name="axis"
          v-if="checkOptionActivation('axis')"
        >
          <div class="customize-tab-body-axis">
            <div class="customize-scroll-hidden">
              <div
                class="customize-scroll pR5"
                v-if="report.options.settings.chartMode === 'multi'"
              >
                <el-collapse v-model="activeMultichartItem" accordion>
                  <template
                    v-for="(dataPoint, index) in dataPointObject.filter(
                      dp => dp.axes !== 'x'
                    )"
                  >
                    <el-collapse-item
                      :key="index"
                      :title="dataPoint.label"
                      :name="
                        dataPoint.type +
                          '_' +
                          (dataPoint.key ? dataPoint.key : dataPoint.label)
                      "
                      v-if="
                        report.options.multichart[
                          dataPoint.type +
                            '_' +
                            (dataPoint.key ? dataPoint.key : dataPoint.label)
                        ]
                      "
                      class="fc-analytics-legend-collapse"
                    >
                      <div class="pL20 pR20 pT20">
                        <div class="fc-text-pink uppercase line-height24 fL">
                          X-Axis
                        </div>
                        <div class="fR">
                          <span class="customize-label pR10">Show</span>
                          <el-checkbox
                            v-model="
                              report.options.multichart[
                                dataPoint.type +
                                  '_' +
                                  (dataPoint.key
                                    ? dataPoint.key
                                    : dataPoint.label)
                              ].axis.x.show
                            "
                          />
                        </div>

                        <div class="customize-input-block clearboth">
                          <p class="customize-label">X - Axis Label</p>
                          <el-row>
                            <el-col :span="24">
                              <el-input
                                v-model="
                                  report.options.multichart[
                                    dataPoint.type +
                                      '_' +
                                      (dataPoint.key
                                        ? dataPoint.key
                                        : dataPoint.label)
                                  ].axis.x.label.text
                                "
                                placeholder="X Label"
                                class="fc-input-full-border2 width100"
                              ></el-input>
                            </el-col>
                          </el-row>
                        </div>

                        <div
                          class="customize-input-block"
                          style="margin-top: 20px;"
                        >
                          <el-row>
                            <el-col :span="18">
                              <el-checkbox
                                v-model="
                                  report.options.multichart[
                                    dataPoint.type +
                                      '_' +
                                      (dataPoint.key
                                        ? dataPoint.key
                                        : dataPoint.label)
                                  ].axis.rotated
                                "
                              />
                              <span class="customize-label pR10"
                                >Rotate Axis</span
                              >
                            </el-col>
                          </el-row>
                        </div>

                        <div
                          class="customize-input-block customize-radio-block new-customize-radio-block"
                          style="margin-top: 30px;"
                        >
                          <p class="customize-label">Tick Direction</p>
                          <div>
                            <el-radio-group
                              v-model="
                                report.options.multichart[
                                  dataPoint.type +
                                    '_' +
                                    (dataPoint.key
                                      ? dataPoint.key
                                      : dataPoint.label)
                                ].axis.x.tick.direction
                              "
                            >
                              <el-radio-button label="auto"
                                >Auto</el-radio-button
                              >
                              <el-radio-button label="horizontal"
                                >Horizontal</el-radio-button
                              >
                              <el-radio-button label="vertical"
                                >Vertical</el-radio-button
                              >
                              <el-radio-button label="slanting"
                                >Slanting</el-radio-button
                              >
                            </el-radio-group>
                          </div>
                        </div>
                      </div>

                      <div class="y-axis-container fc-y-axis-container">
                        <div
                          class="fc-text-pink uppercase line-height24 fL mL20"
                        >
                          Y-Axis
                        </div>
                        <div
                          class="yaxis-tab-block fc-yaxis-tab-block pT20 clearboth"
                        >
                          <el-tabs type="card">
                            <el-tab-pane label="Y1 Axis">
                              <div
                                class="customize-input-block pR20 pL20"
                                style="margin-top: 20px;"
                              >
                                <el-row>
                                  <el-col :span="18">
                                    <span class="customize-label">Show</span>
                                    <el-checkbox
                                      v-model="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y.show
                                      "
                                    />
                                  </el-col>
                                </el-row>
                              </div>
                              <div
                                class="customize-input-block customize-radio-block new-customize-radio-block new-customize-radio-block new-customize-radio-block mL20 mR20"
                                style="margin-top: 20px;"
                              >
                                <p class="customize-label">Y1 - Axis Label</p>
                                <el-row>
                                  <el-col :span="24">
                                    <el-input
                                      class="pT10 fc-input-full-border2 width100"
                                      placeholder="Y Label"
                                      v-model="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y.label.text
                                      "
                                      @change="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y.label.type = 'custom'
                                      "
                                    ></el-input>
                                  </el-col>
                                  <el-col :span="6" style="text-align: left;">
                                    <i
                                      v-if="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y.label.type === 'custom'
                                      "
                                      class="el-icon-refresh pointer axislabel-resettodefault"
                                      @click="
                                        onYLabelTypeChange(
                                          'auto',
                                          report.options.multichart[
                                            dataPoint.type +
                                              '_' +
                                              (dataPoint.key
                                                ? dataPoint.key
                                                : dataPoint.label)
                                          ].axis.y.label
                                        )
                                      "
                                      title="Reset to default"
                                      v-tippy
                                    ></i>
                                  </el-col>
                                </el-row>
                              </div>
                              <div
                                class="customize-input-block pR20 pL20"
                                style="margin-top: 20px;"
                              >
                                <el-row class="mT20" :gutter="20">
                                  <el-col :span="12">
                                    <p class="customize-label">Y-Min</p>
                                    <el-input
                                      v-model.number="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y.range.min
                                      "
                                      placeholder="auto"
                                      class="fc-input-full-border2 width100"
                                    ></el-input>
                                  </el-col>
                                  <el-col :span="12">
                                    <p class="customize-label">Y-Max</p>
                                    <el-input
                                      v-model.number="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y.range.max
                                      "
                                      placeholder="auto"
                                      class="fc-input-full-border2 width100"
                                    ></el-input>
                                  </el-col>
                                </el-row>
                                <el-row class="mT20" :gutter="20">
                                  <el-col :span="12">
                                    <p class="customize-label">Ticks Count</p>
                                    <el-input
                                      v-model.number="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y.ticks.count
                                      "
                                      class="fc-input-full-border2 width100"
                                    ></el-input>
                                  </el-col>
                                  <el-col :span="12">
                                    <p class="customize-label">Decimals</p>
                                    <el-input
                                      v-model.number="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y.format.decimals
                                      "
                                      class="fc-input-full-border2 width100"
                                    ></el-input>
                                  </el-col>
                                </el-row>
                              </div>
                              <div
                                class="customize-input-block pR20 pL20"
                                style="margin-top: 20px;"
                              >
                                <p class="customize-label">Format Label</p>
                                <el-row>
                                  <el-col :span="18">
                                    <el-select
                                      v-model="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y.formatUnit
                                      "
                                      placeholder="Select"
                                      class="fc-input-full-border2"
                                    >
                                      <el-option
                                        v-for="item in formatLabels"
                                        :key="item.value"
                                        :value="item.value"
                                        :label="item.label"
                                      >
                                        <span style="float: left">{{
                                          item.label
                                        }}</span>
                                        <span
                                          style="float: right; color: #8492a6; font-size: 13px"
                                          >{{ item.value }}</span
                                        >
                                      </el-option>
                                    </el-select>
                                  </el-col>
                                </el-row>
                              </div>
                            </el-tab-pane>
                            <el-tab-pane label="Y2 Axis">
                              <div
                                class="customize-input-block pR20 pL20"
                                style="margin-top: 20px;"
                              >
                                <el-row>
                                  <el-col :span="18">
                                    <span class="customize-label">Show</span>
                                    <el-checkbox
                                      v-model="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.showy2axis
                                      "
                                    />
                                  </el-col>
                                </el-row>
                              </div>
                              <div
                                class="customize-input-block customize-radio-block new-customize-radio-block pR20 pL20"
                                style="margin-top: 20px;"
                              >
                                <p class="customize-label">Y2 - Axis Label</p>
                                <el-row>
                                  <el-col :span="24">
                                    <el-input
                                      class="pT15 fc-input-full-border2"
                                      placeholder="Y2 Label"
                                      v-model="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y2.label.text
                                      "
                                      @change="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y2.label.type = 'custom'
                                      "
                                    ></el-input>
                                  </el-col>
                                  <el-col :span="6" style="text-align: left;">
                                    <i
                                      v-if="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y2.label.type === 'custom'
                                      "
                                      class="el-icon-refresh pointer axislabel-resettodefault"
                                      @click="
                                        onYLabelTypeChange(
                                          'auto',
                                          report.options.multichart[
                                            dataPoint.type +
                                              '_' +
                                              (dataPoint.key
                                                ? dataPoint.key
                                                : dataPoint.label)
                                          ].axis.y2.label
                                        )
                                      "
                                      title="Reset to default"
                                      v-tippy
                                    ></i>
                                  </el-col>
                                </el-row>
                              </div>
                              <div
                                class="customize-input-block pR20 pL20"
                                style="margin-bottom: 80px;margin-top: 20px;"
                              >
                                <el-row class="mT20" :gutter="20">
                                  <el-col :span="12">
                                    <p class="customize-label">Y-Min</p>
                                    <el-input
                                      v-model.number="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y2.range.min
                                      "
                                      placeholder="auto"
                                      class="fc-input-full-border2"
                                    ></el-input>
                                  </el-col>
                                  <el-col :span="12">
                                    <p class="customize-label">Y-Max</p>
                                    <el-input
                                      v-model.number="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y2.range.max
                                      "
                                      placeholder="auto"
                                      class="fc-input-full-border2"
                                    ></el-input>
                                  </el-col>
                                </el-row>
                                <el-row class="mT20" :gutter="20">
                                  <el-col :span="12">
                                    <p class="customize-label">Ticks Count</p>
                                    <el-input
                                      v-model.number="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y2.ticks.count
                                      "
                                      class="fc-input-full-border2"
                                    ></el-input>
                                  </el-col>
                                  <el-col :span="12">
                                    <p class="customize-label">Decimals</p>
                                    <el-input
                                      v-model.number="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y2.format.decimals
                                      "
                                      class="fc-input-full-border2"
                                    ></el-input>
                                  </el-col>
                                </el-row>
                              </div>
                              <div
                                class="customize-input-block pR20 pL20"
                                style="margin-top: 20px;"
                              >
                                <p class="customize-label">Format Label</p>
                                <el-row>
                                  <el-col :span="18">
                                    <el-select
                                      v-model="
                                        report.options.multichart[
                                          dataPoint.type +
                                            '_' +
                                            (dataPoint.key
                                              ? dataPoint.key
                                              : dataPoint.label)
                                        ].axis.y2.formatUnit
                                      "
                                      placeholder="Select"
                                      class="fc-input-full-border2"
                                    >
                                      <el-option
                                        v-for="item in formatLabels"
                                        :key="item.value"
                                        :value="item.value"
                                        :label="item.label"
                                      >
                                        <span style="float: left">{{
                                          item.label
                                        }}</span>
                                        <span
                                          style="float: right; color: #8492a6; font-size: 13px"
                                          >{{ item.value }}</span
                                        >
                                      </el-option>
                                    </el-select>
                                  </el-col>
                                </el-row>
                              </div>
                            </el-tab-pane>
                          </el-tabs>
                        </div>
                      </div>
                    </el-collapse-item>
                  </template>
                </el-collapse>
              </div>
              <div class="customize-scroll pR5" v-else>
                <div class="pL20 pR20">
                  <div class="fc-text-pink uppercase">X-Axis</div>
                  <div class="customize-input-block">
                    <p class="customize-label">X - Axis Label</p>
                    <el-row>
                      <el-col :span="24">
                        <el-input
                          v-model="report.options.axis.x.label.text"
                          placeholder="X Label"
                          class="fc-input-full-border2 width100"
                        ></el-input>
                      </el-col>
                    </el-row>
                  </div>

                  <div class="customize-input-block" style="margin-top: 20px;">
                    <el-row>
                      <el-col :span="18">
                        <span class="customize-label pR10">Rotate Axis</span>
                        <el-checkbox v-model="report.options.axis.rotated" />
                      </el-col>
                    </el-row>
                  </div>

                  <div
                    class="customize-input-block customize-radio-block new-customize-radio-block new-customize-radio-block new-customize-radio-block"
                    style="margin-top: 30px;"
                  >
                    <p class="customize-label">Tick Direction</p>
                    <div>
                      <el-radio-group
                        v-model="report.options.axis.x.tick.direction"
                      >
                        <el-radio-button label="auto">Auto</el-radio-button>
                        <el-radio-button label="horizontal"
                          >Horizontal</el-radio-button
                        >
                        <el-radio-button label="vertical"
                          >Vertical</el-radio-button
                        >
                        <el-radio-button label="slanting"
                          >Slanting</el-radio-button
                        >
                      </el-radio-group>
                    </div>
                  </div>
                </div>

                <div
                  class="customize-input-block pR20 pL20"
                  style="margin-top: 20px;"
                  v-if="
                    report.options.axis.x.datatype === 'number' ||
                      report.options.axis.x.datatype === 'decimal'
                  "
                >
                  <el-row class="mT20" :gutter="20">
                    <el-col :span="12">
                      <p class="customize-label">X-Min</p>
                      <el-input
                        v-model.number="report.options.axis.x.range.min"
                        placeholder="auto"
                        class="fc-input-full-border2"
                      ></el-input>
                    </el-col>
                    <el-col :span="12">
                      <p class="customize-label">X-Max</p>
                      <el-input
                        v-model.number="report.options.axis.x.range.max"
                        placeholder="auto"
                        class="fc-input-full-border2"
                      ></el-input>
                    </el-col>
                  </el-row>
                </div>

                <div class="y-axis-container fc-y-axis-container pT20">
                  <div
                    class="fc-text-pink uppercase pR20 pL20 fL line-height24"
                  >
                    Y-Axis
                  </div>
                  <div
                    class="yaxis-tab-block clearboth pT20 fc-yaxis-tab-block "
                  >
                    <el-tabs type="card">
                      <el-tab-pane label="Y1 Axis">
                        <div
                          class="customize-input-block pR20 pL20"
                          style="margin-top: 20px;"
                        >
                          <p class="customize-label">Show</p>
                          <el-row>
                            <el-col :span="18">
                              <el-checkbox
                                v-model="report.options.axis.y.show"
                              />
                            </el-col>
                          </el-row>
                        </div>
                        <div
                          class="customize-input-block customize-radio-block new-customize-radio-block pR20 pL20"
                          style="margin-top: 20px;"
                        >
                          <p class="customize-label">Y1 - Axis Label</p>
                          <el-row>
                            <el-col :span="24">
                              <el-input
                                class="pT10 fc-input-full-border2 width100"
                                type="text"
                                placeholder="Y Label"
                                v-model="report.options.axis.y.label.text"
                                @change="
                                  report.options.axis.y.label.type = 'custom'
                                "
                              ></el-input>
                            </el-col>
                            <el-col :span="6" style="text-align: left;">
                              <i
                                v-if="
                                  report.options.axis.y.label.type === 'custom'
                                "
                                class="el-icon-refresh pointer axislabel-resettodefault"
                                @click="
                                  onYLabelTypeChange(
                                    'auto',
                                    report.options.axis.y.label
                                  )
                                "
                                title="Reset to default"
                                v-tippy
                              ></i>
                            </el-col>
                          </el-row>
                        </div>
                        <div
                          class="customize-input-block pR20 pL20"
                          style="margin-top: 20px;"
                        >
                          <el-row class="mT20" :gutter="20">
                            <el-col :span="12">
                              <p class="customize-label">Y-Min</p>
                              <el-input
                                v-model.number="report.options.axis.y.range.min"
                                placeholder="auto"
                                class="fc-input-full-border2"
                              ></el-input>
                            </el-col>
                            <el-col :span="12">
                              <p class="customize-label">Y-Max</p>
                              <el-input
                                v-model.number="report.options.axis.y.range.max"
                                placeholder="auto"
                                class="fc-input-full-border2"
                              ></el-input>
                            </el-col>
                          </el-row>
                          <el-row class="mT20" :gutter="20">
                            <el-col :span="12">
                              <p class="customize-label">Ticks Count</p>
                              <el-input
                                v-model.number="
                                  report.options.axis.y.ticks.count
                                "
                                class="fc-input-full-border2"
                              ></el-input>
                            </el-col>
                            <el-col :span="12">
                              <p class="customize-label">Decimals</p>
                              <el-input
                                v-model.number="
                                  report.options.axis.y.format.decimals
                                "
                                class="fc-input-full-border2"
                              ></el-input>
                            </el-col>
                          </el-row>
                        </div>
                        <div
                          class="customize-input-block pR20 pL20"
                          style="margin-top: 20px;"
                        >
                          <p class="customize-label">Format Label</p>
                          <el-row>
                            <el-col :span="18">
                              <el-select
                                v-model="report.options.axis.y.formatUnit"
                                placeholder="Select"
                                class="fc-input-full-border2"
                              >
                                <el-option
                                  v-for="item in formatLabels"
                                  :key="item.value"
                                  :value="item.value"
                                  :label="item.label"
                                >
                                  <span style="float: left">{{
                                    item.label
                                  }}</span>
                                  <span
                                    style="float: right; color: #8492a6; font-size: 13px"
                                    >{{ item.value }}</span
                                  >
                                </el-option>
                              </el-select>
                            </el-col>
                          </el-row>
                        </div>
                      </el-tab-pane>
                      <el-tab-pane label="Y2 Axis">
                        <div
                          class="customize-input-block pR20 pL20"
                          style="margin-top: 20px;"
                        >
                          <p class="customize-label">Show</p>
                          <el-row>
                            <el-col :span="18">
                              <el-checkbox
                                v-model="report.options.axis.showy2axis"
                              />
                            </el-col>
                          </el-row>
                        </div>
                        <div
                          class="customize-input-block customize-radio-block new-customize-radio-block pR20 pL20"
                          style="margin-top: 20px;"
                        >
                          <p class="customize-label">Y2 - Axis Label</p>
                          <el-row>
                            <el-col :span="24">
                              <el-input
                                class="pT15 fc-input-full-border2"
                                placeholder="Y2 Label"
                                v-model="report.options.axis.y2.label.text"
                                @change="
                                  report.options.axis.y2.label.type = 'custom'
                                "
                              ></el-input>
                            </el-col>
                            <el-col :span="6" style="text-align: left;">
                              <i
                                v-if="
                                  report.options.axis.y2.label.type === 'custom'
                                "
                                class="el-icon-refresh pointer axislabel-resettodefault"
                                @click="
                                  onYLabelTypeChange(
                                    'auto',
                                    report.options.axis.y2.label
                                  )
                                "
                                title="Reset to default"
                                v-tippy
                              ></i>
                            </el-col>
                          </el-row>
                        </div>
                        <div
                          class="customize-input-block pR20 pL20"
                          style="margin-top: 20px;"
                        >
                          <el-row class="mT20" :gutter="20">
                            <el-col :span="12">
                              <p class="customize-label">Y-Min</p>
                              <el-input
                                v-model.number="
                                  report.options.axis.y2.range.min
                                "
                                placeholder="auto"
                                class="fc-input-full-border2"
                              ></el-input>
                            </el-col>
                            <el-col :span="12">
                              <p class="customize-label">Y-Max</p>
                              <el-input
                                v-model.number="
                                  report.options.axis.y2.range.max
                                "
                                placeholder="auto"
                                class="fc-input-full-border2"
                              ></el-input>
                            </el-col>
                          </el-row>
                          <el-row class="mT20" :gutter="20">
                            <el-col :span="12">
                              <p class="customize-label">Ticks Count</p>
                              <el-input
                                v-model.number="
                                  report.options.axis.y2.ticks.count
                                "
                                class="fc-input-full-border2"
                              ></el-input>
                            </el-col>
                            <el-col :span="12">
                              <p class="customize-label">Decimals</p>
                              <el-input
                                v-model.number="
                                  report.options.axis.y2.format.decimals
                                "
                                class="fc-input-full-border2"
                              ></el-input>
                            </el-col>
                          </el-row>
                        </div>
                        <div
                          class="customize-input-block pR20 pL20"
                          style="margin-top: 20px;"
                        >
                          <p class="customize-label">Format Label</p>
                          <el-row>
                            <el-col :span="18">
                              <el-select
                                v-model="report.options.axis.y2.formatUnit"
                                placeholder="Select"
                                class="fc-input-full-border2"
                              >
                                <el-option
                                  v-for="item in formatLabels"
                                  :key="item.value"
                                  :value="item.value"
                                  :label="item.label"
                                >
                                  <span style="float: left">{{
                                    item.label
                                  }}</span>
                                  <span
                                    style="float: right; color: #8492a6; font-size: 13px"
                                    >{{ item.value }}</span
                                  >
                                </el-option>
                              </el-select>
                            </el-col>
                          </el-row>
                        </div>
                      </el-tab-pane>
                    </el-tabs>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane
          label="Legend"
          name="legend"
          v-if="checkOptionActivation('legend')"
        >
          <div class="customize-tab-body-axis">
            <div class="customize-scroll-hidden">
              <div class="customize-scroll pR5">
                <div class="customize-input-block pR20 pL20 pT20">
                  <el-row>
                    <el-col :span="19">
                      <div class="fc-text-pink uppercase line-height24">
                        Chart Legend
                      </div>
                    </el-col>
                    <el-col :span="5">
                      <span class="customize-label pR10">Show</span>
                      <el-checkbox v-model="report.options.legend.show" />
                    </el-col>
                  </el-row>
                </div>

                <div
                  v-if="report.options.settings.chartMode !== 'multi'"
                  class="customize-input-block customize-radio-block new-customize-radio-block pT10"
                >
                  <p class="customize-label pR20 pL20">Position</p>
                  <div class="pR20 pL20">
                    <el-radio-group v-model="report.options.legend.position">
                      <el-radio-button label="top">Top</el-radio-button>
                      <el-radio-button label="bottom">Bottom</el-radio-button>
                      <el-radio-button label="right">Right</el-radio-button>
                    </el-radio-group>
                  </div>
                </div>

                <template v-if="report.options.common.mode !== 2">
                  <div class="customize-input-block pR20 pL20 pT20">
                    <el-row>
                      <el-col :span="19">
                        <div class="fc-text-pink uppercase line-height24">
                          Widget Legend
                        </div>
                      </el-col>
                      <el-col :span="5" style="padding-top: 2px;">
                        <span class="customize-label pR10">Show</span>
                        <el-checkbox
                          v-model="report.options.widgetLegend.show"
                        />
                      </el-col>
                    </el-row>
                  </div>

                  <el-collapse
                    v-if="report.options.widgetLegend.show"
                    v-model="activeWidgetLegendItem"
                    accordion
                    style="padding: 20 15px;"
                    class="customize-widget-legends mT20"
                  >
                    <template
                      v-for="(point, index) in allPoints.filter(
                        dp => dp.axes !== 'x'
                      )"
                    >
                      <el-collapse-item
                        v-if="!point.isBaseLine"
                        :key="index"
                        :title="prefix(point) + point.label"
                        :name="point.alias"
                        class="fc-analytics-legend-collapse"
                      >
                        <el-checkbox-group
                          class="pT20"
                          v-model="
                            report.options.widgetLegend.variances[point.alias]
                          "
                        >
                          <template v-if="[4, 8].includes(point.dataTypeId)">
                            <el-checkbox
                              v-for="(value, key) in point.enumMap"
                              :label="key"
                              :key="key"
                              class="width40 pL20 pB20"
                              >{{ value }}</el-checkbox
                            >
                          </template>
                          <el-checkbox
                            v-else
                            v-for="(legend, index) in [
                              'min',
                              'max',
                              'sum',
                              'avg',
                              'lastValue',
                            ]"
                            :key="index"
                            :label="legend"
                            class="width40 pL20 pB20"
                            >{{
                              $constants.VarianceLabels[legend]
                            }}</el-checkbox
                          >
                        </el-checkbox-group>
                        <template v-for="(bldp, index) in allPoints">
                          <div
                            :key="index"
                            v-if="
                              bldp.isBaseLine && bldp.dpAlias === point.alias
                            "
                            class="pT20"
                          >
                            <div
                              style="font-size: 14px;color: #333333;font-weight: 500;"
                            >
                              {{ bldp.baseLineName }}
                            </div>
                            <el-checkbox-group
                              class="pT10 pB10"
                              v-model="
                                report.options.widgetLegend.variances[
                                  bldp.alias
                                ]
                              "
                            >
                              <template
                                v-if="[4, 8].includes(point.dataTypeId)"
                              >
                                <el-checkbox
                                  v-for="(value, key) in point.enumMap"
                                  :label="key"
                                  :key="key"
                                  class="width40 pL20 pB20"
                                  >{{ value }}</el-checkbox
                                >
                              </template>
                              <el-checkbox
                                v-if="[4, 8].includes(point.dataTypeId)"
                                label="duration"
                                class="width40 pL20 pB20"
                                >{{
                                  $constants.VarianceLabels.duration
                                }}</el-checkbox
                              >
                              <el-checkbox
                                v-else
                                v-for="(legend, index) in [
                                  'min',
                                  'max',
                                  'sum',
                                  'avg',
                                  'lastValue',
                                ]"
                                :key="index"
                                :label="legend"
                                class="width40 pL20 pB20"
                                >{{
                                  $constants.VarianceLabels[legend]
                                }}</el-checkbox
                              >
                            </el-checkbox-group>
                          </div>
                        </template>
                      </el-collapse-item>
                    </template>
                  </el-collapse>
                </template>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane
          label="Style"
          name="style"
          v-if="checkOptionActivation('style')"
        >
          <div class="customize-tab-body-axis">
            <div class="customize-scroll-hidden">
              <div class="customize-scroll pR5">
                <div class="label-txt-black pR20 pL20">General</div>

                <div class="customize-input-block pR20 pL20 pT20">
                  <el-row>
                    <el-col :span="12">
                      <el-checkbox
                        v-model="report.options.general.point.show"
                        class="mR10"
                      />
                      <span class="customize-label">Points</span>
                    </el-col>
                    <el-col :span="12">
                      <el-checkbox
                        v-model="report.options.general.labels"
                        class="mR10"
                      />
                      <span class="customize-label">Data Labels</span>
                    </el-col>
                  </el-row>
                  <el-row class="mT10">
                    <el-col :span="16">
                      <el-checkbox
                        v-model="report.options.line.connectNull"
                        class="mR10"
                      />
                      <span class="customize-label">Connect Missing Data</span>
                    </el-col>
                  </el-row>
                </div>

                <div class="customize-input-block pR20 pL20 pT20">
                  <p class="label-txt-black">Grid Line</p>
                  <el-row class="mT10">
                    <el-col :span="12">
                      <el-checkbox
                        v-model="report.options.general.grid.x"
                      />&nbsp;&nbsp;X Grid
                    </el-col>
                    <el-col :span="12">
                      <el-checkbox
                        v-model="report.options.general.grid.y"
                      />&nbsp;&nbsp;Y Grid
                    </el-col>
                  </el-row>
                </div>

                <div class="label-txt-black pR20 pL20 mT20">Tooltip</div>

                <div class="customize-input-block pR20 pL20 pT20">
                  <el-row>
                    <el-col :span="24">
                      <p class="label-txt-black">Tooltip Order</p>
                      <el-select
                        v-model="report.options.tooltip.sortOrder"
                        placeholder="Select"
                        class="fc-input-full-border2 width100"
                      >
                        <el-option label="None" value="none"></el-option>
                        <el-option label="Ascending" value="asc"></el-option>
                        <el-option label="Descending" value="desc"></el-option>
                      </el-select>
                    </el-col>
                  </el-row>
                </div>

                <div class="fc-text-pink uppercase pR20 pL20 mT20">
                  Pie / Donut
                </div>

                <div class="customize-input-block pR20 pL20">
                  <el-row>
                    <el-col :span="24">
                      <p class="label-txt-black pB5">Show slice label as</p>
                      <el-select
                        v-model="report.options.donut.labelType"
                        placeholder="Select"
                        class="fc-input-full-border2 width100"
                      >
                        <el-option
                          label="Percentage"
                          value="percentage"
                        ></el-option>
                        <el-option
                          label="Actual Value"
                          value="actual"
                        ></el-option>
                        <el-option label="None" value="none"></el-option>
                      </el-select>
                    </el-col>
                  </el-row>
                  <el-row class="mT20">
                    <el-col :span="24">
                      <p class="label-txt-black pB5">Primary Center Text</p>
                      <el-select
                        v-model="report.options.donut.centerText.primaryText"
                        :allow-create="true"
                        :filterable="true"
                        clearable
                        placeholder="Select or type"
                        class="fc-input-full-border2 width100"
                      >
                        <el-option label="Total" value="_sum"></el-option>
                        <el-option label="Avg" value="_avg"></el-option>
                        <el-option label="Min" value="_min"></el-option>
                        <el-option label="Max" value="_max"></el-option>
                      </el-select>
                    </el-col>
                  </el-row>
                  <el-row class="mT20">
                    <el-col :span="12">
                      <p class="customize-label">
                        {{ $t('common.header.unit') }}
                      </p>
                      <el-input
                        v-model.number="
                          report.options.donut.centerText.primaryUnit
                        "
                        class="fc-input-full-border2"
                      ></el-input>
                    </el-col>
                    <el-col :span="10" class="mL10 mT30">
                      <el-checkbox
                        v-model="
                          report.options.donut.centerText.primaryRoundOff
                        "
                      />
                      <span class="customize-label">
                        {{ $t('common._common.round_off') }}
                      </span>
                    </el-col>
                  </el-row>
                  <el-row class="mT20">
                    <el-col :span="24">
                      <p class="label-txt-black pB5">Secondary Center Text</p>
                      <el-select
                        v-model="report.options.donut.centerText.secondaryText"
                        :allow-create="true"
                        :filterable="true"
                        clearable
                        placeholder="Select or type"
                        class="fc-input-full-border2 width100"
                      >
                        <el-option label="Total" value="_sum"></el-option>
                        <el-option label="Avg" value="_avg"></el-option>
                        <el-option label="Min" value="_min"></el-option>
                        <el-option label="Max" value="_max"></el-option>
                      </el-select>
                    </el-col>
                  </el-row>
                  <el-row class="mT20">
                    <el-col :span="12">
                      <p class="customize-label">
                        {{ $t('common.header.unit') }}
                      </p>
                      <el-input
                        v-model.number="
                          report.options.donut.centerText.secondaryUnit
                        "
                        class="fc-input-full-border2"
                      ></el-input>
                    </el-col>
                    <el-col :span="10" class="mL10 mT30">
                      <el-checkbox
                        v-model="
                          report.options.donut.centerText.secondaryRoundOff
                        "
                      />
                      <span class="customize-label">
                        {{ $t('common._common.round_off') }}
                      </span>
                    </el-col>
                  </el-row>
                </div>

                <div class="fc-text-pink uppercase pR20 pL20 mT20">
                  <el-checkbox
                    v-model="report.options.general.normalizeStack"
                    class="mR10"
                  />
                  STACKED BAR / AREA
                </div>

                <div class="customize-input-block pR20 pL20">
                  <el-row>
                    <el-col :span="24">
                      <p class="label-txt-black pB5">Data Order</p>
                      <el-select
                        v-model="report.options.general.dataOrder"
                        placeholder="Select order"
                        class="fc-input-full-border2 width100"
                      >
                        <el-option label="None" value="none"></el-option>
                        <el-option label="Ascending" value="asc"></el-option>
                        <el-option label="Descending" value="desc"></el-option>
                      </el-select>
                    </el-col>
                  </el-row>
                </div>

                <div class="fc-text-pink uppercase pR20 pL20 mT20">AREA</div>

                <div class="customize-input-block pR20 pL20">
                  <el-row>
                    <el-col :span="11">
                      <el-checkbox
                        v-model="report.options.area.linearGradient"
                        class="mT10 mR10"
                      />
                      <span class="label-txt-black">Linear Gradient</span>
                    </el-col>
                    <el-col :span="11" class="mL20">
                      <el-checkbox
                        v-model="report.options.area.above"
                        class="mT10 mR10"
                      />
                      <span class="label-txt-black">Above</span>
                    </el-col>
                  </el-row>
                </div>

                <div
                  class="fc-text-pink uppercase pR20 pL20 mT20"
                  v-if="report.options.bar"
                >
                  BAR
                </div>

                <div
                  class="customize-input-block pR20 pL20"
                  v-if="report.options.bar"
                >
                  <el-row>
                    <el-col :span="11">
                      <p class="customize-label">Radius</p>
                      <el-input
                        v-model.number="report.options.bar.radius"
                        placeholder="auto"
                        class="fc-input-full-border2"
                      ></el-input>
                    </el-col>
                    <el-col :span="11" class="mL20">
                      <p class="customize-label">Padding</p>
                      <el-input
                        v-model.number="report.options.bar.padding"
                        placeholder="auto"
                        class="fc-input-full-border2"
                      ></el-input>
                    </el-col>
                  </el-row>
                  <div class="customize-input-block pR20 mT10">
                    <el-row>
                      <el-col :span="11">
                        <el-checkbox
                          v-model="report.options.benchmark.lines[0].show"
                          class="mT10 mR10"
                        />
                        <span class="label-txt-black">Benchmark</span>
                      </el-col>
                    </el-row>
                  </div>
                  <div v-if="report.options.benchmark.lines[0].show">
                    <el-row class="mT10">
                      <el-col :span="11">
                        <p class="customize-label">Constant</p>
                        <el-input
                          v-model="
                            report.options.benchmark.lines[0].value
                          "
                          @input="validateDecimalInput"
                          placeholder="auto"
                          class="fc-input-full-border2"
                        ></el-input>
                      </el-col>
                      <el-col :span="11" class="mL20">
                        <p class="customize-label">Color</p>
                        <el-input
                          v-model="report.options.benchmark.lines[0].color"
                          placeholder="auto"
                          class="fc-input-full-border2"
                        ></el-input>
                      </el-col>
                    </el-row>
                    <el-row>
                      <el-col :span="11">
                        <p class="customize-label mT10">Label</p>
                        <el-input
                          v-model="report.options.benchmark.lines[0].label"
                          placeholder="auto"
                          class="fc-input-full-border2"
                        ></el-input>
                      </el-col>
                    </el-row>
                  </div>
                </div>

                <div class="customize-input-block pR20 pL20 pT20">
                  <p class="label-txt-black">Table</p>
                  <el-row class="mT10">
                    <el-col :span="12">
                      <el-checkbox
                        v-model="report.options.table.hideIndex"
                      />&nbsp;&nbsp;Hide Index
                    </el-col>
                  </el-row>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane
          label="Userfilters"
          name="userfilters"
          v-if="checkOptionActivation('userfilters')"
        >
        </el-tab-pane>
        <el-tab-pane
          label="Safelimit"
          name="safelimit"
          v-if="checkOptionActivation('safelimit')"
        >
          <div class="customize-tab-body-axis clearboth">
            <div class="customize-scroll-hidden">
              <div class="customize-scroll datapoints-malign mL0 mR0">
                <div
                  v-for="(datapoint, index) in allPoints.filter(
                    dp => dp.axes !== 'x'
                  )"
                  :key="index"
                  class="analytics-data-point"
                  style="display: flex;"
                >
                  <div class="flex-middle fc-cus-safe-limit-txt">
                    <el-checkbox
                      v-model="datapoint.safelimit"
                      class="mR15"
                    ></el-checkbox>
                    <div
                      class="line-height20 label-txt-black pL10"
                      :title="datapoint.label"
                      v-tippy
                    >
                      {{ datapoint.label }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
      <el-dialog
        title="Color palettes"
        :append-to-body="true"
        width="25%"
        :visible.sync="showEnumColorDialog"
        class="enum-color-dialog"
        :lock-scroll="true"
      >
        <div
          v-if="
            selectedEnumDataPoint && selectedEnumDataPoint.dataType === 'ENUM'
          "
          class="mB50"
        >
          <el-row
            v-for="(enumLabel, idx) in selectedEnumDataPoint.enumMap"
            :key="idx"
          >
            <el-col :span="3">
              <el-color-picker
                v-model="selectedEnumDataPoint.enumColorMap[idx]"
                size="mini"
                class="mL5 mR5"
                :popper-class="'chart-custom-color-picker'"
              ></el-color-picker>
            </el-col>
            <el-col class="mT5" :span="21">{{ enumLabel }}</el-col>
          </el-row>
        </div>
        <div class="modal-dialog-footer">
          <el-button
            class="modal-btn-cancel width100"
            @click.stop="closeEnumDialog()"
            >CLOSE</el-button
          >
        </div>
      </el-dialog>
      <div v-if="!hideFooter" class="modal-dialog-footer" style="bottom: 15px;">
        <el-button class="modal-btn-cancel" @click="done">CANCEL</el-button>
        <el-button type="primary" class="modal-btn-save" @click="done"
          >DONE</el-button
        >
      </div>
    </div>
    <div v-else>
      <div class="mT15 label-txt-black" style="text-align: center;">
        Please select datapoints to customize
      </div>
    </div>
    <derivation
      v-if="derivation.show"
      :visibility.sync="derivation.show"
      :report="report"
      :config="config"
      :selectedPoint="derivation.point"
    ></derivation>
  </div>
</template>

<script>
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import Derivation from 'pages/energy/analytics/components/FDerivation'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import draggable from 'vuedraggable'
import NumberFormatHelper from 'src/components/mixins/NumberFormatHelper'
import deepmerge from 'util/deepmerge'
import OutsideClick from '@/OutsideClick'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: [
    'report',
    'resultDataPoints',
    'config',
    'optionsToEnable',
    'hideFooter',
  ],
  created() {
    this.initBenchMark()
  },
  mounted() {
    this.init()
  },
  computed: {
    validateDecimalInput(){
      const regex = /^-?\d*(\.\d{0,6})?$/; // Allow up to two decimal places

      if (!isEmpty(this.report.options.benchmark.lines[0].value) && !regex.test(this.report.options.benchmark.lines[0].value)) {
        this.report.options.benchmark.lines[0].value = this.report.options.benchmark.lines[0].value.replace(/[^0-9.]/g, "");
      }
    },
    isNewDashboardEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_DASHBOARD_FLOW')
    },
    computedOptions() {
      if (typeof this.optionsToEnable === 'undefined') {
        let options = []
        for (let option of Object.keys(this.options)) {
          if (option !== 'USERFILTERS') {
            options.push(this.options[option])
          }
        }
        return options
      } else {
        return this.optionsToEnable
      }
    },
    dataPointObject: {
      get: function() {
        return this.report
          ? this.$helpers.getDataPoints(
              this.report.options.dataPoints,
              [1, 2, 4],
              true
            )
          : []
      },
      set: function(newDataPointObject) {
        this.report.options.dataPoints = newDataPointObject
      },
    },
    allPoints() {
      let points = []
      if (this.dataPointObject) {
        this.dataPointObject.forEach(dp => {
          if (dp.type === 'group') {
            points.push(...dp.children)
          } else {
            points.push(dp)
          }
        })
      }
      return points.filter(dp => dp.axes !== 'x')
    },
  },
  mixins: [NewDataFormatHelper, AnalyticsMixin, NumberFormatHelper],
  data() {
    return {
      options: {
        DATAPOINTS: 1,
        AXIS: 2,
        LEGEND: 3,
        STYLE: 4,
        USERFILTERS: 5,
        SAFELIMIT: 6,
      },
      aggregations: [
        {
          label: 'Sum',
          value: 3,
          name: 'sum',
        },
        {
          label: 'Avg',
          value: 2,
          name: 'avg',
        },
        {
          label: 'Min',
          value: 4,
          name: 'min',
        },
        {
          label: 'Max',
          value: 5,
          name: 'max',
        },
      ],
      lastGroupKey: 0,
      dataPointTextToggle: {},
      groupNameTextToggle: {},
      popover_toggle: {},
      popover_chart_toggle: {},
      popover_aggr_toggle: {},
      originalReportOptions: null,
      groupName: '',
      groupToggle: false,
      activeTab: 'datapoints',
      activeMultichartItem: null,
      activeWidgetLegendItem: null,
      duplicateAggr: null,
      derivation: {
        show: false,
        point: null,
      },
      checked: '',
      scales: [
        {
          label: 'linear',
          value: 'linear',
        },
      ],
      units: [
        {
          label: 'kWh',
          value: 'kWh',
        },
      ],
      showAggrForDuplication: false,
      aggrMap: {
        AVG: 2,
        SUM: 3,
        MIN: 4,
        MAX: 5,
      },
      showEnumColorDialog: false,
      selectedEnumDataPoint: null,
      formatLabels: [
        { label: 'None', value: 'None' },
        { label: 'Thousand', value: 'K' },
        { label: 'Lacs', value: 'L' },
        { label: 'Millions', value: 'M' },
        { label: 'Crores', value: 'C' },
      ],
      actions_aggr: false,
    }
  },
  components: {
    Derivation,
    draggable,
    OutsideClick,
  },
  methods: {
    openActionAggr() {
      this.actions_aggr = true
    },
    setActionAggr(datapoint, action_aggr) {
      let dp = this.getConfigDataPointFromOptionDP(this.config, datapoint)
      this.$set(dp, 'rule_aggr_type', action_aggr)
      // this.$set(duplicateDp, 'duplicateDataPoint', true)
    },
    checkOptionActivation(key) {
      key = key.toUpperCase()
      if (this.computedOptions) {
        return this.computedOptions.includes(this.options[key])
      }
      return false
    },
    openEnumDialog(dp) {
      this.selectedEnumDataPoint = dp
      this.showEnumColorDialog = true
    },
    closeEnumDialog() {
      ;(this.showEnumColorDialog = false), (this.selectedEnumDataPoint = null)
    },
    setTab() {
      let tab = this.computedOptions[0]
      for (let option of Object.keys(this.options)) {
        if (this.options[option] === tab) {
          this.tab = option.toLowerCase()
          break
        }
      }
    },
    init() {
      this.actions_aggr = false
      this.setTab()
      if (this.tab) {
        this.activeTab = this.tab
      }
      for (let i = 0; i < this.dataPointObject.length; i++) {
        this.popover_toggle[this.dataPointObject[i].key] = false
        this.popover_chart_toggle[this.dataPointObject[i].key] = false
        this.popover_aggr_toggle[this.dataPointObject[i].key] = false
        this.dataPointTextToggle[this.dataPointObject[i].key] = false
      }

      if (this.report.options) {
        this.originalReportOptions = this.$helpers.cloneObject(
          this.report.options
        )
      }
    },
    visibilityToggle(element) {
      if (element.visible) {
        element.visible = false
      } else {
        element.visible = true
      }
    },
    groupNameEditToggler(dataPoint) {
      if (dataPoint.label === '') {
        alert('GroupName cannot be empty')
      } else {
        this.$set(this.groupNameTextToggle, dataPoint.groupKey, false)
      }
    },
    addNewGroup(datapoint) {
      let groupNames = this.dataPointObject.filter(db => db.type === 'group')
      if (groupNames.length !== 0) {
        for (let group in groupNames) {
          if (groupNames[group].label === this.groupName) {
            alert('Group name exists. Try a new one')
            return
          }
        }
      }
      let temp = {
        label: this.groupName,
        children: [],
        type: 'group',
        chartType: '',
        groupKey: this.lastGroupKey + 1,
        unit: '', // auto group key
        dataType: '', // auto group key
      }
      this.lastGroupKey = this.lastGroupKey + 1
      let dataPointIndex = this.dataPointObject.indexOf(datapoint)
      this.report.options.dataPoints.splice(dataPointIndex, 1)
      temp.children.push(datapoint)
      this.report.options.dataPoints.push(temp)
      this.groupNameTextToggle['#' + this.lastGroupKey] = false
      this.groupName = ''
      this.groupToggle = false
      this.popover_toggle[datapoint.key] = false
      this.prepareMultiChart()
    },
    removeFromGroup(dataPoint, points) {
      let dataPointIndex = dataPoint.children.indexOf(points)
      dataPoint.children.splice(dataPointIndex, 1)
      this.report.options.dataPoints.push(points)
      this.$set(this.popover_toggle, points.key, false)
      if (dataPoint.children.length === 0) {
        this.deleteGroup(dataPoint)
      }
      this.prepareMultiChart()
    },
    addToGroup(dataPoint, group) {
      if (group.children.includes(dataPoint)) {
        return false
      } else {
        for (let dp of this.dataPointObject) {
          if (dp.label === group.label) {
            dp.children.push(dataPoint)
            let dataPointIndex = this.report.options.dataPoints.indexOf(
              dataPoint
            )
            this.report.options.dataPoints.splice(dataPointIndex, 1)
            this.$set(this.popover_toggle, dataPoint.key, false)
          } else {
            continue
          }
        }
      }
      this.prepareMultiChart()
    },
    addNewGroupToggle() {
      if (this.groupToggle) {
        this.groupToggle = false
      } else {
        this.groupToggle = true
      }
    },
    deleteGroup(dataPoint) {
      if (dataPoint.children.length > 0) {
        for (let e = 0; e < dataPoint.children.length; e++) {
          this.report.options.dataPoints.push(dataPoint.children[e])
          dataPoint.children.splice(e, 1)
        }
      }
      let dataPointindex = this.report.options.dataPoints.indexOf(dataPoint)
      this.report.options.dataPoints.splice(dataPointindex, 1)
      this.prepareMultiChart()
    },
    resetToDefault() {
      if (this.originalReportOptions) {
        this.report.options = this.originalReportOptions
      }
    },
    done() {
      this.$emit('close')
    },
    checkForEmpty(dataPoint) {
      if (dataPoint.label === '') {
        alert('Datapoint name cannot be empty')
      } else {
        this.$set(this.dataPointTextToggle, dataPoint.key, false)
        this.$forceUpdate()
      }
    },
    prepareMultiChart() {
      this.report.options.multichart = this.getMultichartOptions(
        this.report.options.axis,
        this.dataPointObject,
        this.resultDataPoints
      )
    },
    changeGroupAxisOptions() {
      if (this.report) {
        let rangeGroups = this.report.options.dataPoints.filter(
          dp => dp.type === 'rangeGroup'
        )
        if (rangeGroups.length !== 0) {
          for (let rGroup of rangeGroups) {
            let parentAxes = rGroup.axes
            for (let child of rGroup.children) {
              child.axes = parentAxes
            }
          }
          return
        } else {
          return
        }
      }
    },
    changeYAxis() {
      if (this.report.options.common.mode !== 2) {
        this.changeGroupAxisOptions()
        this.prepareMultiChart()
        this.prepareYAxes(
          this.report.options.axis,
          this.dataPointObject,
          this.resultDataPoints
        )
      }
    },
    onYLabelTypeChange(val, labelObj) {
      if (val === 'auto') {
        if (labelObj) {
          labelObj.type = 'auto'
        }
        this.changeYAxis()
      }
    },
    deletePoint(idx, optionDataPoint, parentIdx, parentDp) {
      let pointIdx = this.getConfigDataPointFromOptionDP(
        this.config,
        optionDataPoint,
        true
      )
      if (pointIdx > -1) {
        if (
          !this.checkAndDeletePointsRel(
            this.report.options,
            optionDataPoint.alias
          )
        ) {
          this.$message.error(
            'This point has been used in other derivation(s). Please delete it before removing this point'
          )
          return
        }
        this.config.dataPoints.splice(pointIdx, 1)
      } else if (optionDataPoint.type === 'rangeGroup') {
        if (this.config.derviations) {
          for (let dp of optionDataPoint.children) {
            for (let derivation of Object.keys(this.config.derivations)) {
              if (
                this.config.derivations[derivation].usedAliases.includes(
                  dp.alias
                )
              ) {
                this.$message.error(
                  'This point has been used in other derivation(s). Please delete it before removing this point'
                )
                return false
              }
            }
          }
        }

        optionDataPoint.children.forEach(dp => {
          for (let conf of this.config.dataPoints) {
            if (conf.aliases.actual === dp.alias) {
              this.config.dataPoints.splice(
                this.config.dataPoints.indexOf(conf),
                1
              )
            }
          }
        })
        this.report.options.dataPoints.splice(idx, 1)
        this.resetPredictionTimings(this.config)
        if (!this.report.options.dataPoints.length) {
          this.$emit('resetPoints')
        }
        return
      } else if (
        optionDataPoint.type === 'group' &&
        optionDataPoint.pointType &&
        optionDataPoint.pointType === 'regression'
      ) {
        // delete the whole group and the config
        let alias = optionDataPoint.alias
          ? optionDataPoint.alias
          : optionDataPoint.key
        let regressionPoint = optionDataPoint.children.find(
          dP => dP.alias === alias
        )
        let regressionConfigIdx = null
        if (regressionPoint) {
          regressionConfigIdx = this.report.options.regressionConfig.findIndex(
            rConf =>
              rConf.xAxis.readingId === regressionPoint.xAxis.readingId &&
              rConf.xAxis.parentId === regressionPoint.xAxis.parentId &&
              rConf.yAxis.readingId === regressionPoint.yAxis.readingId &&
              rConf.yAxis.parentId === regressionPoint.yAxis.parentId
          )
          if (regressionConfigIdx > -1) {
            this.report.options.regressionConfig.splice(regressionConfigIdx, 1)
            this.report.options.dataPoints.splice(idx, 1)
          }
        }
        this.resetPredictionTimings(this.config)
        if (!this.report.options.dataPoints.length) {
          this.$emit('resetPoints')
        }
        return
      }

      if (parentIdx || parentIdx === 0) {
        parentDp.children.splice(idx, 1)
        if (!parentDp.children.length) {
          this.report.options.dataPoints.splice(parentIdx, 1)
        }
      } else {
        this.report.options.dataPoints.splice(idx, 1)
      }

      this.resetPredictionTimings(this.config)
      if (!this.report.options.dataPoints.length) {
        this.$emit('resetPoints')
      }
    },
    duplicatePoint(idx, optionDataPoint, aggr) {
      this.$set(this.popover_toggle, optionDataPoint.key, false)
      let pointIdx = this.getConfigDataPointFromOptionDP(
        this.config,
        optionDataPoint,
        true
      )
      let duplicateDp = deepmerge.objectAssignDeep(
        {},
        this.config.dataPoints[pointIdx]
      )
      this.$delete(duplicateDp, 'aliases')
      this.$set(duplicateDp.yAxis, 'aggr', this.aggrMap[aggr])
      this.$set(duplicateDp, 'duplicateDataPoint', true)
      this.config.dataPoints.push(duplicateDp)
      this.duplicateAggr = null
      this.showAggrForDuplication = false
    },
    changeAggr(optionDataPoint, aggr) {
      optionDataPoint.aggr = aggr
      this.popover_aggr_toggle[optionDataPoint.key] = false
      let dp = this.getConfigDataPointFromOptionDP(this.config, optionDataPoint)
      this.$set(dp.yAxis, 'aggr', aggr)
    },
    changeRightInclusive(optionDataPoint, rightInclusive) {
      optionDataPoint.rightInclusive = rightInclusive
      let dp = this.getConfigDataPointFromOptionDP(this.config, optionDataPoint)
      this.$set(dp, 'rightInclusive', rightInclusive)
    },
    editDerivation(dp) {
      this.derivation.point = dp
      this.derivation.show = true
    },
    prefix(point) {
      if (point.duplicateDataPoint) {
        if (
          this.report.params.xAggr !== 0 ||
          (this.report &&
            ![1, 4, 'reading'].includes(this.report.options.common.mode))
        ) {
          switch (point.aggr) {
            case 2:
              return 'Avg of '
            case 3:
              return 'Sum of '
            case 4:
              return 'Min of '
            case 5:
              return 'Max of '
            default:
              return ''
          }
        }
      }
      return ''
    },
    initBenchMark() {
      if (!this.report.options.benchmark?.lines) {
        this.$set(this.report.options.benchmark, 'lines', [{
            label: null,
            value: null,
            color: '#fff',
            show: false,
          },
        ])
      }
    },
  },
}
</script>

<style>
.fc-analytics-legend-collapse .el-collapse-item__header {
  padding-left: 20px;
  font-size: 14px;
  font-weight: normal;
  border: 1px solid transparent;
  border-bottom: 1px solid #ecf3fa;
  letter-spacing: 0.5px;
  color: #324056;
  padding-right: 3px;
  line-height: 20px;
}
.fc-analytics-legend-collapse .el-collapse-item__header:hover {
  border: 1px solid #9ed6dd;
  background-color: #f9feff;
}
.customize-scroll .el-collapse {
  border-top: none;
  border-bottom: none;
}
.fc-analytics-legend-collapse .el-collapse-item__header.is-active {
  background-color: #fafafa !important;
  font-weight: 500;
  box-shadow: 0 3px 1px 0 rgba(229, 229, 229, 0.34);
}
.fc-analytics-legend-collapse
  .el-collapse-item__header
  .el-collapse-item__arrow {
  display: block !important;
  font-weight: 600;
  transform: rotate(90deg);
  font-size: 16px;
  margin-right: 20px;
}
.fc-analytics-legend-collapse .el-collapse-item__arrow.is-active {
  font-weight: 600;
  transform: rotate(-90deg);
  color: #ef508f;
  font-size: 16px;
}
.axislabel-resettodefault {
  margin-top: 18px;
  margin-left: 12px;
  opacity: 0.6;
}
.axislabel-resettodefault:hover {
  opacity: 1;
}
.customize-widget-legends .el-checkbox {
  margin: 5px 16px 0 0 !important;
}
.aggr-icon {
  width: 14px;
  height: 14px;
  margin-top: 7px;
}
.enum-color-dialog .el-color-picker__trigger {
  border: none !important;
}
.enum-color-dialog .el-color-picker__color {
  border: none !important;
}
.enum-color-dialog .el-icon-arrow-down {
  display: none;
}
.enum-color-dialog .el-dialog__header {
  display: block;
}
.tooltip_help_msg_width {
  max-width: 340px;
}
</style>
