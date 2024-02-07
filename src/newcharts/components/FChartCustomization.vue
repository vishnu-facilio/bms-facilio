<template>
  <div style="position: relative;">
    <el-tabs v-model="activeTab">
      <el-tab-pane
        label="Data points"
        name="datapoints"
        v-if="checkOptionActivation('datapoints')"
      >
        <div class="customize-tab-body-axis clearboth">
          <div class="customize-scroll-hidden">
            <div class="customize-scroll datapoints-malign">
              <draggable
                v-model="dataPointObject"
                @start="drag = true"
                @end="drag = false"
              >
                <div
                  v-for="(dataPoint, index) in dataPointObject"
                  :key="index"
                  class="analytics-data-point"
                  style="display: flex;"
                >
                  <div
                    v-if="dataPoint.type === 'datapoint'"
                    class="data-points-drag-block"
                    :class="{ 'mR20 pR10': report.reportType === 2 }"
                  >
                    <div
                      class="data-points-checkbox"
                      :class="{ 'datapoint-hidden': !dataPoint.visible }"
                    >
                      <i
                        @click="visibilityToggle(dataPoint)"
                        class="pointer el-icon-view"
                      ></i>
                      <div
                        v-if="!dataPointTextToggle[dataPoint.key]"
                        @dblclick="
                          ;(dataPointTextToggle[dataPoint.key] =
                            dataPoint.pointType !== 2),
                            $forceUpdate()
                        "
                        class="datapoints-txt"
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
                            (report.options.common.mode === 1 ||
                              report.reportType === 2) &&
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
                      <div>
                        <el-color-picker
                          v-model="dataPoint.color"
                          size="mini"
                          class="mL5 mR5"
                          :popper-class="'chart-custom-color-picker'"
                        ></el-color-picker>
                      </div>
                      <el-popover
                        v-if="
                          (dataPoint.metricEnum &&
                            dataPoint.metricEnum.toLowerCase() ===
                              'duration') ||
                            specialTimeFields.includes(dataPoint.fieldName)
                        "
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
                          <div
                            class="label-txt-blue pT10 pB10 pointer pL10 pR20"
                            v-if="
                              (dataPoint.metricEnum &&
                                dataPoint.metricEnum.toLowerCase() ===
                                  'duration') ||
                                specialTimeFields.includes(dataPoint.fieldName)
                            "
                          >
                            Display Unit
                          </div>
                          <el-radio-group
                            v-model="dataPoint.convertTounit"
                            @change="popover_toggle[dataPoint.key] = false"
                            size="mini"
                            class="pT15 pB10 pointer pL20 pR10"
                          >
                            <el-radio
                              label="None"
                              :value="null"
                              class="fc-radio-btn block mB10"
                            ></el-radio>
                            <el-radio
                              label="Days"
                              class="fc-radio-btn  block mB10"
                            ></el-radio>
                            <el-radio
                              label="Hours"
                              class="fc-radio-btn  block mB10"
                            ></el-radio>
                            <el-radio
                              label="Minutes"
                              class="fc-radio-btn  block mB10"
                            ></el-radio>
                            <el-radio
                              label="Seconds"
                              class="fc-radio-btn  block mB10"
                            ></el-radio>
                            <el-radio
                              label="Milliseconds"
                              class="fc-radio-btn  block mB10"
                            ></el-radio>
                          </el-radio-group>
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
                    <el-color-picker
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
                      <i
                        class="el-icon-delete pointer delete-color mL5"
                        v-if="
                          dataPoint.pointType &&
                            dataPoint.pointType === 'regression'
                        "
                        @click="deletePoint(index, dataPoint)"
                      ></i>
                    </div>
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
                          v-if="points.axes !== 'x'"
                          class="data-points-drag-block mT10"
                        >
                          <div
                            class="data-points-checkbox"
                            :class="{ 'datapoint-hidden': !points.visible }"
                          >
                            <i
                              @click="visibilityToggle(points)"
                              class=" pointer el-icon-view"
                            ></i>
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
                                  points.dataTypeId !== 8 &&
                                  !dataPoint.pointType &&
                                  dataPoint.pointType !== 'regression'
                              "
                              class="dp-yaxis"
                              v-model="points.axes"
                              size="mini"
                              @change="changeYAxis"
                            >
                              <el-radio-button label="y">Y1</el-radio-button>
                              <el-radio-button label="y2">Y2</el-radio-button>
                            </el-radio-group>

                            <el-popover
                              v-if="
                                !points.isBaseLine &&
                                  report.params.xAggr !== 0 &&
                                  points.pointType !== 2 &&
                                  !dataPoint.pointType &&
                                  dataPoint.pointType !== 'regression'
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
                                  class="chart-icon-new"
                                  src="~statics/report/aggr.svg"
                                />
                              </div>
                            </el-popover>

                            <div>
                              <el-color-picker
                                v-model="points.color"
                                size="mini"
                                class="mL5 mR5"
                                :popper-class="'chart-custom-color-picker'"
                              ></el-color-picker>
                            </div>

                            <el-popover
                              v-if="report.reportType !== 2"
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
                                    @click="addNewGroupToggle()"
                                    v-if="!groupToggle"
                                    class="add-new-group-txt"
                                  >
                                    <i class="el-icon-plus fwbold"></i> Add New
                                    Group
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
                                  @click="removeFromGroup(dataPoint, points)"
                                >
                                  <i class="el-icon-delete pR5"></i> Remove From
                                  Group
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

                        <i
                          class="el-icon-delete pointer delete-color dp-delete mL5"
                          v-if="
                            points.axes !== 'x' &&
                              !points.isBaseLine &&
                              !dataPoint.pointType &&
                              dataPoint.pointType !== 'regression'
                          "
                          @click="
                            deletePoint(chIndex, points, index, dataPoint)
                          "
                        ></i>
                      </div>
                    </draggable>
                  </div>
                  <i
                    v-if="
                      report.reportType !== 2 &&
                        dataPoint.type === 'datapoint' &&
                        !dataPoint.isBaseLine
                    "
                    class="el-icon-delete pointer delete-color dp-delete mL5"
                    @click="deletePoint(index, dataPoint)"
                  ></i>
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
              class="customize-scroll"
              v-if="report.options.settings.chartMode === 'multi'"
            >
              <el-collapse v-model="activeMultichartItem" accordion>
                <el-collapse-item
                  v-for="(dataPoint, index) in dataPointObject"
                  :key="index"
                  :title="dataPoint.label"
                  :name="
                    dataPoint.type +
                      '_' +
                      (dataPoint.key ? dataPoint.key : dataPoint.label)
                  "
                  :v-if="
                    report.options.multichart[
                      dataPoint.type +
                        '_' +
                        (dataPoint.key ? dataPoint.key : dataPoint.label)
                    ]
                  "
                  class="new-analytics-collapse"
                >
                  <div class="fc-text-pink uppercase pl27">X-Axis</div>
                  <div class="customize-input-block pl27">
                    <p class="customize-label">X - Axis Label</p>
                    <el-row>
                      <el-col :span="18">
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
                          class="fc-input-full-border2"
                        ></el-input>
                      </el-col>
                      <el-col :span="6">
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
                        Show
                      </el-col>
                    </el-row>
                  </div>

                  <div
                    class="customize-input-block pl27"
                    style="margin-top: 20px;"
                  >
                    <p class="customize-label">Rotate Axis</p>
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
                      </el-col>
                    </el-row>
                  </div>

                  <div
                    class="customize-input-block customize-radio-block"
                    style="margin-top: 30px;"
                  >
                    <p class="customize-label pl27">Tick Direction</p>
                    <div class="pl27">
                      <el-radio-group
                        v-model="
                          report.options.multichart[
                            dataPoint.type +
                              '_' +
                              (dataPoint.key ? dataPoint.key : dataPoint.label)
                          ].axis.x.tick.direction
                        "
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

                  <div class="y-axis-container">
                    <div class="fc-text-pink uppercase pl27">Y-Axis</div>
                    <div class="yaxis-tab-block">
                      <el-tabs type="card">
                        <el-tab-pane label="Y1 Axis">
                          <div
                            class="customize-input-block pl27"
                            style="margin-top: 20px;"
                          >
                            <p class="customize-label">Show</p>
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
                                    ].axis.y.show
                                  "
                                />
                              </el-col>
                            </el-row>
                          </div>
                          <div
                            class="customize-input-block customize-radio-block pl27"
                            style="margin-top: 20px;"
                          >
                            <p class="customize-label">Y1 - Axis Label</p>
                            <el-row>
                              <el-col :span="22">
                                <el-input
                                  class="pT15 fc-input-full-border2"
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
                            class="customize-input-block pl27"
                            style="margin-top: 20px;"
                          >
                            <el-row class="mT20">
                              <el-col :span="10" class="mR30">
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
                                  class="fc-input-full-border2"
                                ></el-input>
                              </el-col>
                              <el-col :span="10" class="mL5">
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
                                  class="fc-input-full-border2"
                                ></el-input>
                              </el-col>
                            </el-row>
                            <el-row class="mT20">
                              <el-col :span="10" class="mR30">
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
                                  class="fc-input-full-border2"
                                ></el-input>
                              </el-col>
                              <el-col :span="10" class="mL5">
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
                                  class="fc-input-full-border2"
                                ></el-input>
                              </el-col>
                            </el-row>
                          </div>
                          <div
                            class="customize-input-block pl27"
                            style="margin-bottom: 80px;margin-top: 20px;"
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
                            class="customize-input-block pl27"
                            style="margin-top: 20px;"
                          >
                            <p class="customize-label">Show</p>
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
                                    ].axis.y2.show
                                  "
                                />
                              </el-col>
                            </el-row>
                          </div>
                          <div
                            class="customize-input-block customize-radio-block pl27"
                            style="margin-top: 20px;"
                          >
                            <p class="customize-label">Y2 - Axis Label</p>
                            <el-row>
                              <el-col :span="18">
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
                            class="customize-input-block pl27"
                            style="margin-bottom: 80px;margin-top: 20px;"
                          >
                            <el-row class="mT20">
                              <el-col :span="10" class="mR30">
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
                              <el-col :span="10" class="mL5">
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
                            <el-row class="mT20">
                              <el-col :span="10" class="mR30">
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
                              <el-col :span="10" class="mL5">
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
                            class="customize-input-block pl27"
                            style="margin-bottom: 80px;margin-top: 20px;"
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
              </el-collapse>
            </div>
            <div class="customize-scroll" v-else>
              <div class="fc-text-pink uppercase pl27">X-Axis</div>
              <div class="customize-input-block pl27">
                <p class="customize-label">X - Axis Label</p>
                <el-row>
                  <el-col :span="18">
                    <el-input
                      v-model="report.options.axis.x.label.text"
                      placeholder="X Label"
                      class="fc-input-full-border2"
                    ></el-input>
                  </el-col>
                </el-row>
              </div>

              <div class="customize-input-block pl27" style="margin-top: 20px;">
                <p class="customize-label">Rotate Axis</p>
                <el-row>
                  <el-col :span="18">
                    <el-checkbox v-model="report.options.axis.rotated" />
                  </el-col>
                </el-row>
              </div>

              <div
                class="customize-input-block customize-radio-block"
                style="margin-top: 30px;"
              >
                <p class="customize-label pl27">Tick Direction</p>
                <div class="pl27">
                  <el-radio-group
                    v-model="report.options.axis.x.tick.direction"
                  >
                    <el-radio-button label="auto">Auto</el-radio-button>
                    <el-radio-button label="horizontal"
                      >Horizontal</el-radio-button
                    >
                    <el-radio-button label="vertical">Vertical</el-radio-button>
                    <el-radio-button label="slanting">Slanting</el-radio-button>
                  </el-radio-group>
                </div>
              </div>

              <div
                class="customize-input-block pl27"
                style="margin-top: 20px;"
                v-if="
                  report.options.axis.x.datatype === 'number' ||
                    report.options.axis.x.datatype === 'decimal'
                "
              >
                <el-row class="mT20">
                  <el-col :span="10" class="mR30">
                    <p class="customize-label">X-Min</p>
                    <el-input
                      v-model.number="report.options.axis.x.range.min"
                      placeholder="auto"
                      class="fc-input-full-border2"
                    ></el-input>
                  </el-col>
                  <el-col :span="10" class="mL5">
                    <p class="customize-label">X-Max</p>
                    <el-input
                      v-model.number="report.options.axis.x.range.max"
                      placeholder="auto"
                      class="fc-input-full-border2"
                    ></el-input>
                  </el-col>
                </el-row>
              </div>

              <div class="y-axis-container">
                <div class="fc-text-pink uppercase pl27">Y-Axis</div>
                <div class="yaxis-tab-block">
                  <el-tabs type="card">
                    <el-tab-pane label="Y1 Axis">
                      <div
                        class="customize-input-block pl27"
                        style="margin-top: 20px;"
                      >
                        <p class="customize-label">Show</p>
                        <el-row>
                          <el-col :span="18">
                            <el-checkbox v-model="report.options.axis.y.show" />
                          </el-col>
                        </el-row>
                      </div>
                      <div
                        class="customize-input-block customize-radio-block pl27"
                        style="margin-top: 20px;"
                      >
                        <p class="customize-label">Y1 - Axis Label</p>
                        <el-row>
                          <el-col :span="22">
                            <el-input
                              class="pT15 fc-input-full-border2"
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
                        class="customize-input-block pl27"
                        style="margin-top: 20px;"
                      >
                        <el-row class="mT20">
                          <el-col :span="10" class="mR30">
                            <p class="customize-label">Y-Min</p>
                            <el-input
                              v-model.number="report.options.axis.y.range.min"
                              placeholder="auto"
                              class="fc-input-full-border2"
                            ></el-input>
                          </el-col>
                          <el-col :span="10" class="mL5">
                            <p class="customize-label">Y-Max</p>
                            <el-input
                              v-model.number="report.options.axis.y.range.max"
                              placeholder="auto"
                              class="fc-input-full-border2"
                            ></el-input>
                          </el-col>
                        </el-row>
                        <el-row class="mT20">
                          <el-col :span="10" class="mR30">
                            <p class="customize-label">Ticks Count</p>
                            <el-input
                              v-model.number="report.options.axis.y.ticks.count"
                              class="fc-input-full-border2"
                            ></el-input>
                          </el-col>
                          <el-col :span="10" class="mL5">
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
                        class="customize-input-block pl27"
                        style="margin-bottom: 80px;margin-top: 20px;"
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
                        class="customize-input-block pl27"
                        style="margin-top: 20px;"
                      >
                        <p class="customize-label">Show</p>
                        <el-row>
                          <el-col :span="18">
                            <el-checkbox
                              v-model="report.options.axis.y2.show"
                            />
                          </el-col>
                        </el-row>
                      </div>
                      <div
                        class="customize-input-block customize-radio-block pl27"
                        style="margin-top: 20px;"
                      >
                        <p class="customize-label">Y2 - Axis Label</p>
                        <el-row>
                          <el-col :span="18">
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
                        class="customize-input-block pl27"
                        style="margin-top: 20px;"
                      >
                        <el-row class="mT20">
                          <el-col :span="10" class="mR30">
                            <p class="customize-label">Y-Min</p>
                            <el-input
                              v-model.number="report.options.axis.y2.range.min"
                              placeholder="auto"
                              class="fc-input-full-border2"
                            ></el-input>
                          </el-col>
                          <el-col :span="10" class="mL5">
                            <p class="customize-label">Y-Max</p>
                            <el-input
                              v-model.number="report.options.axis.y2.range.max"
                              placeholder="auto"
                              class="fc-input-full-border2"
                            ></el-input>
                          </el-col>
                        </el-row>
                        <el-row class="mT20">
                          <el-col :span="10" class="mR30">
                            <p class="customize-label">Ticks Count</p>
                            <el-input
                              v-model.number="
                                report.options.axis.y2.ticks.count
                              "
                              class="fc-input-full-border2"
                            ></el-input>
                          </el-col>
                          <el-col :span="10" class="mL5">
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
                        class="customize-input-block pl27"
                        style="margin-bottom: 80px; margin-top: 20px;"
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
            <div class="customize-scroll">
              <div class="fc-text-pink uppercase pl27">Chart Legend</div>
              <div class="customize-input-block pl27">
                <p class="customize-label">Show</p>
                <el-row>
                  <el-col :span="18">
                    <el-checkbox v-model="report.options.legend.show" />
                  </el-col>
                </el-row>
              </div>

              <div
                class="customize-input-block customize-radio-block"
                style="margin-top: 30px;"
              >
                <p class="customize-label pl27">Position</p>
                <div class="pl27">
                  <el-radio-group v-model="report.options.legend.position">
                    <el-radio-button label="top">Top</el-radio-button>
                    <el-radio-button label="bottom">Bottom</el-radio-button>
                    <el-radio-button label="right">Right</el-radio-button>
                  </el-radio-group>
                </div>
              </div>

              <template v-if="report.options.common.mode !== 2">
                <div class="fc-text-pink uppercase pl27 pT20">
                  Widget Legend
                </div>
                <div class="customize-input-block pl27">
                  <p class="customize-label">Show</p>
                  <el-row>
                    <el-col :span="18">
                      <el-checkbox v-model="report.options.widgetLegend.show" />
                    </el-col>
                  </el-row>
                </div>

                <el-collapse
                  v-if="report.options.widgetLegend.show"
                  v-model="activeWidgetLegendItem"
                  accordion
                  style="padding: 0 15px;"
                  class="customize-widget-legends"
                >
                  <el-collapse-item
                    v-for="(point, index) in allPoints"
                    :v-if="!point.isBaseLine"
                    :key="index"
                    :title="point.label"
                    :name="point.alias || point.key"
                    class="new-analytics-collapse"
                  >
                    <el-checkbox-group
                      class="pT20"
                      v-model="
                        report.options.widgetLegend.variances[
                          point.alias || point.key
                        ]
                      "
                    >
                      <template v-if="[4, 8].includes(point.dataTypeId)">
                        <el-checkbox
                          v-for="(value, key) in point.enumMap"
                          :label="key"
                          :key="key"
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
                        >{{ $constants.VarianceLabels[legend] }}</el-checkbox
                      >
                    </el-checkbox-group>
                    <!-- <div
                      v-for="(bldp, index) in allPoints"
                      :key="index"
                      :v-if="bldp.isBaseLine && bldp.dpAlias === point.alias"
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
                          report.options.widgetLegend.variances[bldp.alias]
                        "
                      >
                        <template v-if="[4, 8].includes(point.dataTypeId)">
                          <el-checkbox
                            v-for="(value, key) in point.enumMap"
                            :label="key"
                            :key="key"
                            >{{ value }}</el-checkbox
                          >
                        </template>
                        <el-checkbox
                          v-if="[4, 8].includes(point.dataTypeId)"
                          label="duration"
                          >{{ $constants.VarianceLabels.duration }}</el-checkbox
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
                          >{{ $constants.VarianceLabels[legend] }}</el-checkbox
                        >
                      </el-checkbox-group>
                    </div> -->
                  </el-collapse-item>
                </el-collapse>
              </template>

              <template v-if="report.options.common.mode !== 2">
                <div class="fc-text-pink uppercase pl27 pT20">
                  Benchmark
                </div>
                <div class="customize-input-block pl27">
                  <p class="customize-label">Show</p>
                  <el-row>
                    <el-col :span="18">
                      <el-checkbox v-model="report.options.benchmark.show" />
                    </el-col>
                  </el-row>
                </div>
                <el-row v-if="report.options.benchmark.show">
                  <el-col :span="22">
                    <el-input
                      class="pT15 fc-input-full-border2 pl27"
                      type="text"
                      placeholder="Label"
                      v-model="report.options.benchmark.label"
                    ></el-input>
                  </el-col>
                </el-row>

                <el-collapse
                  v-if="report.options.benchmark.show"
                  v-model="activeBenchMarkItem"
                  accordion
                  style="padding: 0 15px;"
                  class="customize-widget-legends"
                >
                  <el-collapse-item
                    v-for="(point, index) in allPoints"
                    :v-if="!point.isBaseLine"
                    :key="index"
                    :title="point.label"
                    :name="point.alias"
                    class="new-analytics-collapse"
                  >
                    <el-checkbox-group
                      class="pT20"
                      v-model="report.options.benchmark.variances[point.alias]"
                    >
                      <template v-if="[4, 8].includes(point.dataTypeId)">
                        <el-checkbox
                          v-for="(value, key) in point.enumMap"
                          :label="key"
                          :key="key"
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
                        >{{ $constants.VarianceLabels[legend] }}</el-checkbox
                      >
                    </el-checkbox-group>
                  </el-collapse-item>
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
            <div class="customize-scroll">
              <div class="fc-text-pink uppercase pl27">General</div>

              <div class="customize-input-block pl27">
                <el-row>
                  <el-col :span="6">
                    <p class="customize-label">Points</p>
                    <el-checkbox v-model="report.options.general.point.show" />
                  </el-col>
                  <el-col :span="6">
                    <p class="customize-label">Data Labels</p>
                    <el-checkbox v-model="report.options.general.labels" />
                  </el-col>
                  <el-col :span="12">
                    <p class="customize-label">Exclude zero values</p>
                    <el-checkbox v-model="report.options.general.hideZeroes" />
                  </el-col>
                </el-row>
              </div>

              <div class="customize-input-block pl27 pT10">
                <p class="customize-label">Grid Line</p>
                <el-row class="mT10">
                  <el-col :span="6">
                    <el-checkbox
                      v-model="report.options.general.grid.x"
                    />&nbsp;&nbsp;X Grid
                  </el-col>
                  <el-col :span="6">
                    <el-checkbox
                      v-model="report.options.general.grid.y"
                    />&nbsp;&nbsp;Y Grid
                  </el-col>
                </el-row>
              </div>

              <div class="fc-text-pink uppercase pl27 mT20">Tooltip</div>

              <div class="customize-input-block pl27">
                <el-row>
                  <el-col :span="11">
                    <p class="customize-label">Tooltip Order</p>
                    <el-select
                      v-model="report.options.tooltip.sortOrder"
                      placeholder="Select"
                      class="fc-input-full-border2"
                    >
                      <el-option label="None" value="none"></el-option>
                      <el-option label="Ascending" value="asc"></el-option>
                      <el-option label="Descending" value="desc"></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="10" class="mL20">
                    <p class="customize-label">Show null values</p>
                    <el-checkbox
                      v-model="report.options.tooltip.showNullValues"
                      class="mT10"
                    />
                  </el-col>
                </el-row>
              </div>

              <div class="fc-text-pink uppercase pl27 mT20">Pie / Donut</div>

              <div class="customize-input-block pl27">
                <el-row>
                  <el-col :span="11">
                    <p class="customize-label">Show slice label as</p>
                    <el-select
                      v-model="report.options.donut.labelType"
                      placeholder="Select"
                      class="fc-input-full-border2"
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
                  <el-col :span="11">
                    <p class="customize-label">Primary Center Text</p>
                    <el-select
                      v-model="report.options.donut.centerText.primaryText"
                      :allow-create="true"
                      :filterable="true"
                      clearable
                      placeholder="Select or type"
                      class="fc-input-full-border2"
                    >
                      <el-option label="Total" value="_sum"></el-option>
                      <el-option label="Avg" value="_avg"></el-option>
                      <el-option label="Min" value="_min"></el-option>
                      <el-option label="Max" value="_max"></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="11" class="mL20">
                    <p class="customize-label">Secondary Center Text</p>
                    <el-select
                      v-model="report.options.donut.centerText.secondaryText"
                      :allow-create="true"
                      :filterable="true"
                      clearable
                      placeholder="Select or type"
                      class="fc-input-full-border2"
                    >
                      <el-option label="Total" value="_sum"></el-option>
                      <el-option label="Avg" value="_avg"></el-option>
                      <el-option label="Min" value="_min"></el-option>
                      <el-option label="Max" value="_max"></el-option>
                    </el-select>
                  </el-col>
                </el-row>
              </div>

              <div class="fc-text-pink uppercase pl27 mT20">
                STACKED BAR / AREA
              </div>

              <div class="customize-input-block pl27">
                <el-row>
                  <el-col :span="11">
                    <p class="customize-label">Data Order</p>
                    <el-select
                      v-model="report.options.general.dataOrder"
                      placeholder="Select order"
                      class="fc-input-full-border2"
                    >
                      <el-option label="None" value="none"></el-option>
                      <el-option label="Ascending" value="asc"></el-option>
                      <el-option label="Descending" value="desc"></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="11" class="mL20">
                    <p class="customize-label">Normalize Stack</p>
                    <el-checkbox
                      v-model="report.options.general.normalizeStack"
                      class="mT10"
                    />
                  </el-col>
                </el-row>
              </div>

              <div
                class="fc-text-pink uppercase pR20 pL20 mT20"
                v-if="report.options.bar"
              >
                BAR / HISTOGRAM
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
                <el-row class="mT10">
                  <el-col :span="11">
                    <p class="customize-label">Show Group Total</p>
                    <el-checkbox
                      v-model="report.options.bar.showGroupTotal"
                      class="mT10"
                    />
                  </el-col>
                  <el-col :span="11" class="mL20">
                    <p class="customize-label">Group Total Label</p>
                    <el-input
                      v-model="report.options.bar.groupTotalLabel"
                      placeholder="Group Total"
                      class="fc-input-full-border2"
                    ></el-input>
                  </el-col>
                </el-row>
              </div>
              <div class="fc-text-pink uppercase pl27 mT20">AREA</div>

              <div class="customize-input-block pl27">
                <el-row>
                  <el-col :span="11">
                    <p class="customize-label">Linear Gradient</p>
                    <el-checkbox
                      v-model="report.options.area.linearGradient"
                      class="mT10"
                    />
                  </el-col>
                  <el-col :span="11" class="mL20">
                    <p class="customize-label">Above</p>
                    <el-checkbox
                      v-model="report.options.area.above"
                      class="mT10"
                    />
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
            <div
              class="customize-scroll datapoints-malign"
              style="margin-right:22px;"
            >
              <div
                v-for="(datapoint, index) in allPoints"
                :key="index"
                class="analytics-data-point"
                style="display: flex;"
              >
                <div class="data-points-drag-block">
                  <div class="datapoints-txt">{{ datapoint.label }}</div>
                  <div class="data-points-settings-icon-set">
                    <el-radio-group
                      v-model="datapoint.safelimit"
                      class="pl27 chart-setting-group"
                      style="padding-right: 10px;padding-left: 10px;"
                    >
                      <el-radio-button :label="true">Show</el-radio-button>
                      <el-radio-button :label="false">Hide</el-radio-button>
                    </el-radio-group>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    <div class="modal-dialog-footer" style="bottom: 0px;">
      <el-button class="modal-btn-cancel" @click="done">CANCEL</el-button>
      <el-button type="primary" class="modal-btn-save" @click="done"
        >DONE</el-button
      >
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
import OutsideClick from '@/OutsideClick'

export default {
  props: ['report', 'resultDataPoints', 'config', 'optionsToEnable'],
  mounted() {
    this.init()
  },
  computed: {
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
        return this.report ? this.report.options.dataPoints : []
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
      return points
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
      specialTimeFields: ['firstresponsetime', 'estimatedduration'],
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
      activeBenchMarkItem: null,
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
      formatLabels: [
        { label: 'None', value: 'None' },
        { label: 'Thousands', value: 'K' },
        { label: 'Lacs', value: 'L' },
        { label: 'Millions', value: 'M' },
        { label: 'Crores', value: 'C' },
      ],
    }
  },
  components: {
    Derivation,
    draggable,
    OutsideClick,
  },
  methods: {
    checkOptionActivation(key) {
      key = key.toUpperCase()
      if (this.computedOptions) {
        return this.computedOptions.includes(this.options[key])
      }
      return false
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
    changeAggr(optionDataPoint, aggr) {
      optionDataPoint.aggr = aggr
      this.popover_aggr_toggle[optionDataPoint.key] = false
      let dp = this.getConfigDataPointFromOptionDP(this.config, optionDataPoint)
      this.$set(dp.yAxis, 'aggr', aggr)
    },
    editDerivation(dp) {
      this.derivation.point = dp
      this.derivation.show = true
    },
  },
}
</script>

<style>
.new-analytics-collapse .el-collapse-item__header {
  padding-left: 25px;
  font-size: 14px;
  letter-spacing: 0.6px;
  color: #333333;
  font-weight: normal;
  border-bottom: 1px solid #f2f2f2;
}
.customize-scroll .el-collapse {
  border-top: none;
  border-bottom: none;
}
.new-analytics-collapse .el-collapse-item__header.is-active {
  background-color: #fafafa !important;
  font-weight: 500;
  box-shadow: 0 3px 1px 0 rgba(229, 229, 229, 0.34);
}
.new-analytics-collapse .el-collapse-item__header .el-collapse-item__arrow {
  display: block !important;
  font-weight: 600;
  transform: rotate(90deg);
  font-size: 16px;
  margin-right: 20px;
}
.new-analytics-collapse .el-collapse-item__arrow.is-active {
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
</style>
