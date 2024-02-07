<template>
  <div class="dashboard-main-section">
    <div class="row" v-if="mode === 'new'">
      <div class="col-12">
        <div class="new-create-dashboard-header">
          <div class="pull-left" style="width: 15%;padding-left:5px;">
            <div class="create-dashboard-field-header uppercase">
              NEW DASHBOARD
            </div>
            <div>
              <el-input
                :autofocus="true"
                class="create-dashboard-input-title"
                v-model="newdashboard.label"
                placeholder="Enter New Dashboard Name"
              ></el-input>
            </div>
          </div>
          <div
            class="pull-left mobiledashboard-checkbox pT10"
            style="width: 20%;padding-left:5px;"
          >
            <el-checkbox v-model="isMobileDashboard" @change="switchLayout()"
              >Enable mobile dashboard</el-checkbox
            >
          </div>
          <div class="pull-right create-dashboard-btn-section">
            <el-button size="medium" class="plain f13" @click="cancel()"
              >CANCEL</el-button
            >
            <el-button
              size="medium"
              type="primary"
              class="setup-el-btn f13"
              @click="loadSupportedModules"
              >SAVE</el-button
            >
          </div>
          <div class="pull-right create-dashboard-btn-section dashboard-folder">
            <el-autocomplete
              popper-class="my-autocomplete"
              v-model="dashbaordFolderName"
              :fetch-suggestions="querySearch"
              placeholder="Enter the folder name"
              @select="handleSelect"
            >
              <i class="" slot="suffix" @click="handleIconClick"> </i>
              <template v-slot="{ item }">
                <div class="dashboard-folder-name">{{ item.name }}</div>
                <!-- <span class="link"></span> -->
              </template>
            </el-autocomplete>
          </div>
        </div>
      </div>
    </div>
    <div class="row" v-else-if="dashboard && !loading">
      <div class="col-12">
        <div class="new-create-dashboard-header">
          <div class="pull-left" style="width: 15%;">
            <div class="create-dashboard-field-header uppercase">
              EDIT DASHBOARD
            </div>
            <div>
              <el-input
                :autofocus="true"
                class="create-dashboard-input-title"
                v-model="dashboard.label"
                placeholder="Enter Alternative Dashboard Name"
              ></el-input>
            </div>
          </div>
          <div
            class="pull-left mobiledashboard-checkbox pT10"
            style="width: 20%;padding-left:5px;"
          >
            <el-checkbox v-model="isMobileDashboard" @change="switchLayout()"
              >Enable mobile dashboard</el-checkbox
            >
          </div>
          <div class="pull-right create-dashboard-btn-section">
            <div
              class="chart-delete-icon"
              style=""
              v-tippy
              title="Delete Dashboard"
            >
              <i class="el-icon-delete f18 pT10" @click="deletedashboard()"></i>
            </div>
            <el-button size="medium" class="plain f13" @click="cancel()"
              >CANCEL</el-button
            >
            <el-button
              size="medium"
              type="primary"
              class="setup-el-btn f13"
              @click="saveEdit"
              >SAVE</el-button
            >
          </div>
          <div class="pull-right create-dashboard-btn-section dashboard-folder">
            <el-autocomplete
              popper-class="my-autocomplete"
              v-model="dashbaordFolderName"
              :fetch-suggestions="querySearch"
              placeholder="Enter the folder name"
              @select="handleSelect"
            >
              <i class="" slot="suffix" @click="handleIconClick"> </i>
              <template v-slot="{ item }">
                <div class="dashboard-folder-name">{{ item.name }}</div>
                <!-- <span class="link"></span> -->
              </template>
            </el-autocomplete>
          </div>
        </div>
      </div>
    </div>
    <div class="row" style="position:relative;">
      <div
        class="collapse-btn col-12"
        @click="toggle()"
        v-bind:class="{ collapsed: collapseSidebar === true }"
      ></div>
      <div class="col-3" v-if="!collapseSidebar">
        <div class="dashboard-sidebar">
          <div class="dashboard-sidebar-header editdashboard-sidebar-header">
            <el-tabs v-model="option">
              <el-tab-pane label="REPORTS" name="report">
                <div class="rtreenew" v-if="!loading" :style="getHeight">
                  <div v-for="(folder, index) in reportTree" :key="index">
                    <div class="rfolder-name uppercase" @click="expand(folder)">
                      <div class="rfolder-icon">
                        <i class="fa fa-folder-open-o" v-if="folder.expand"></i>
                        <i class="fa fa-folder-o" v-else></i>
                      </div>
                      {{ folder.label }}
                    </div>
                    <div class="editer-rfolder-children" v-show="folder.expand">
                      <div class="rempty" v-if="!folder.children.length">
                        -- No reports --
                      </div>
                      <div
                        class="drag-children"
                        v-for="(report, ridx) in folder.children"
                        :key="ridx"
                        v-else
                        :draggable="true"
                        @drag="drag"
                        @dragstart="start(report)"
                        @dragend="dragend"
                      >
                        <chart-icon-hover
                          class="edit-leftbar-icon chart-icon-hover"
                          :icon="
                            report.widget.dataOptions
                              ? report.widget.dataOptions.type
                                ? report.widget.dataOptions.type
                                : 'line'
                              : 'line'
                          "
                        ></chart-icon-hover>
                        <span class="drag-label ellipsis">{{
                          report.label
                        }}</span>
                        <div class="chart-delete-icon chart-drag-icon" style="">
                          <i class="el-icon-more drag-icon"></i>
                          <i class="el-icon-more drag-icon right"></i>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div
                    v-if="newReportTree"
                    v-for="(folder, index) in newReportTree"
                    :key="folder.id + '_' + index"
                  >
                    <div class="rfolder-name uppercase" @click="expand(folder)">
                      <div class="rfolder-icon">
                        <i class="fa fa-folder-open-o" v-if="folder.expand"></i>
                        <i class="fa fa-folder-o" v-else></i>
                      </div>
                      {{ folder.name }}
                    </div>
                    <div class="editer-rfolder-children" v-show="folder.expand">
                      <div class="rempty" v-if="!folder.reports.length">
                        -- No reports --
                      </div>
                      <div
                        class="drag-children"
                        v-for="(report, ridx) in folder.reports"
                        :key="ridx"
                        v-else
                        :draggable="true"
                        @drag="drag"
                        @dragstart="start(report)"
                        @dragend="dragend"
                      >
                        <chart-icon-hover
                          class="edit-leftbar-icon chart-icon-hover"
                          :icon="
                            report.options && report.options.type
                              ? report.options.type
                              : 'line'
                          "
                        ></chart-icon-hover>
                        <span class="drag-label ellipsis">{{
                          report.name
                        }}</span>
                        <div class="chart-delete-icon chart-drag-icon" style="">
                          <i class="el-icon-more drag-icon"></i>
                          <i class="el-icon-more drag-icon right"></i>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="VIEWS" name="view">
                <div class="rtreenew" v-if="!loading" :style="getHeight">
                  <div v-for="(folder, index) in supportedModules" :key="index">
                    <div class="rfolder-name uppercase" @click="expand(folder)">
                      <div class="rfolder-icon">
                        <i class="fa fa-folder-open-o" v-if="folder.expand"></i>
                        <i class="fa fa-folder-o" v-else></i>
                      </div>
                      {{ folder.label }}
                    </div>
                    <div class="editer-rfolder-children" v-show="folder.expand">
                      <div
                        class="rempty"
                        v-if="folder.list && !folder.list.length"
                      >
                        -- No Views --
                      </div>
                      <div
                        class="drag-children"
                        v-for="(report, ridx) in folder.list"
                        :key="ridx"
                        v-else
                        :draggable="true"
                        @drag="drag"
                        @dragstart="
                          start(report, supportedModules[index].value)
                        "
                        @dragend="dragend"
                      >
                        <div class="edit-leftbar-icon">
                          <img
                            class="chart-icon-new table-main"
                            src="~statics/report/table.svg"
                          />
                          <img
                            class="chart-icon-new table-hover"
                            src="~statics/report/table-white.svg"
                          />
                        </div>
                        <span class="drag-label ellipsis">{{
                          report.displayName
                        }}</span>
                        <div class="chart-delete-icon chart-drag-icon" style="">
                          <i class="el-icon-more drag-icon"></i>
                          <i class="el-icon-more drag-icon right"></i>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane
                label="STATIC WIDGET"
                name="static"
                v-if="$route.query.static"
              >
                <div class="rtreenew" v-if="!loading" :style="getHeight">
                  <div>
                    <div class="rfolder-name uppercase">
                      <div class="rfolder-icon">
                        <i class="fa fa-folder-open-o"></i>
                      </div>
                      Static Widgets
                    </div>
                    <div class="editer-rfolder-children">
                      <div
                        class="drag-children"
                        v-for="(folder, index) in getStaticWidgetList(
                          staticWidgets
                        )"
                        :key="index"
                        :draggable="true"
                        @drag="drag"
                        @dragstart="start(folder)"
                        @dragend="dragend"
                      >
                        <span class="drag-label ellipsis">
                          {{ folder.label }}</span
                        >
                        <div class="chart-delete-icon chart-drag-icon" style="">
                          <i class="el-icon-more drag-icon"></i>
                          <i class="el-icon-more drag-icon right"></i>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="CARDS" name="cards">
                <div class="rtreenew" v-if="!loading" :style="getHeight">
                  <div>
                    <div class="rfolder-name uppercase">
                      <div class="rfolder-icon">
                        <i class="fa fa-folder-open-o"></i>
                      </div>
                      Static Widgets
                    </div>
                    <div class="editer-rfolder-children">
                      <div
                        class="drag-children"
                        v-for="(folder, index) in getCardsList(cards)"
                        :key="index"
                        :draggable="true"
                        @drag="drag"
                        @dragstart="start(folder)"
                        @dragend="dragend"
                        v-show="!folder.disbale"
                      >
                        <span class="drag-label ellipsis">
                          {{ folder.label }}</span
                        >
                        <div class="chart-delete-icon chart-drag-icon" style="">
                          <i class="el-icon-more drag-icon"></i>
                          <i class="el-icon-more drag-icon right"></i>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </div>
      <div
        class="col-9 container-section"
        :style="getHeight"
        v-bind:class="{ fullWidth: collapseSidebar === true }"
        v-if="mode === 'new' && !dashboardLayout.length"
        @dragenter="containerEntry"
      >
        <div
          class="height100 scrollable dashboardmainlayout newdashboardmainlayout"
        >
          <div
            class="dashboard-container"
            :class="{ editmode: editMode }"
            @dragenter="containerEntry"
            ref="editdashboard"
          >
            <dashboard-filter
              v-if="typeof $route.query.filters !== 'undefined'"
            ></dashboard-filter>
            <div
              @drop="drop"
              @dragover="allowDrop"
              v-bind:class="{ dragzone: isDraging == true }"
              v-if="isDraging"
            >
              <div class="dropArea">
                Drop Here !!!!!
                <el-dialog
                  title="Select Building"
                  :modal-append-to-body="false"
                  custom-class="select-building-dialog"
                  :visible.sync="dialogVisible"
                  width="20%"
                  :before-close="cancelBuilding"
                >
                  <el-select
                    v-model="selectedBuilding"
                    placeholder="Please select a building"
                  >
                    <el-option
                      :label="building.name"
                      :value="building.id"
                      v-for="(building, index) in buildings"
                      :key="index"
                    ></el-option>
                  </el-select>
                  <span slot="footer" class="dialog-footer row">
                    <el-button @click="cancelBuilding()" class="col-6"
                      >Cancel</el-button
                    >
                    <el-button
                      type="primary"
                      @click="selectBuildings()"
                      class="col-6"
                      >Confirm</el-button
                    >
                  </span>
                </el-dialog>
                <el-dialog
                  title="Select Building"
                  :modal-append-to-body="false"
                  custom-class="select-building-dialog"
                  :visible.sync="dialogVisible"
                  width="20%"
                  :before-close="cancelBuilding"
                >
                  <el-select
                    v-model="selectedBuilding"
                    placeholder="Please select a building"
                  >
                    <el-option
                      :label="building.name"
                      :value="building.id"
                      v-for="(building, index) in buildings"
                      :key="index"
                    ></el-option>
                  </el-select>
                  <span slot="footer" class="dialog-footer row">
                    <el-button @click="cancelBuilding()" class="col-6"
                      >Cancel</el-button
                    >
                    <el-button
                      type="primary"
                      @click="selectBuildings()"
                      class="col-6"
                      >Confirm</el-button
                    >
                  </span>
                </el-dialog>
                <el-dialog
                  title="WEB CARD"
                  :modal-append-to-body="false"
                  custom-class="select-building-dialog webcard"
                  :visible.sync="webcardVisible"
                  width="20%"
                  :before-close="cancelBuilding"
                >
                  <el-input
                    v-model="webUrl"
                    placeholder="Enter the URL"
                  ></el-input>
                  <span slot="footer" class="dialog-footer row">
                    <el-button @click="cancelBuilding()" class="col-6"
                      >Cancel</el-button
                    >
                    <el-button
                      type="primary"
                      @click="selectwebcard()"
                      class="col-6"
                      >Confirm</el-button
                    >
                  </span>
                </el-dialog>
                <el-dialog
                  custom-class="select-building-dialog select-reading-dialog"
                  v-bind:class="{ readingmode4: readingCard.mode === 4 }"
                  :visible.sync="readingpopup"
                  :modal-append-to-body="false"
                  width="60%"
                  :before-close="cancelBuilding"
                >
                  <div class="new-header-container">
                    <div class="new-header-text">
                      <div class="fc-setup-modal-title">Add Reading Card</div>
                    </div>
                  </div>
                  <div class="mT20 mL40 mR40">
                    <div class="row">
                      <div class="reading-card-layout-changer col-5">
                        <div class="row">
                          <div
                            class="card-layout-big pointer col-6"
                            v-bind:class="{ active: readingCard.mode === 1 }"
                            @click="readingCard.mode = 1"
                          >
                            <div class="rd-conatiner">
                              <div class="sample-card-content">
                                <div class="card-small-heading">
                                  ENERGY USAGE
                                </div>
                                <div class="card-static-reading">
                                  999
                                  <span class="card-reading-value">kWh</span>
                                </div>
                                <div class="f12 bold">THIS MONTH</div>
                              </div>
                            </div>
                          </div>
                          <div
                            class="card-layout-big pointer layout-3 col-6"
                            v-bind:class="{ active: readingCard.mode === 3 }"
                            @click="readingCard.mode = 3"
                          >
                            <div class="rd-conatiner text-center">
                              <img
                                class="svg-icon"
                                src="~assets/dashboard-icons/readingGraph.png"
                              />
                            </div>
                          </div>
                          <div
                            class="card-layout-small pointer col-6"
                            v-bind:class="{ active: readingCard.mode === 2 }"
                            @click="readingCard.mode = 2"
                          >
                            <div class="rd-conatiner">
                              <div class="sample-card-content">
                                <div class="card-small-heading">
                                  ENERGY USAGE
                                </div>
                                <div class="card-static-reading">
                                  999
                                  <span class="card-reading-value">kWh</span>
                                </div>
                                <div class="f12 bold">THIS MONTH</div>
                              </div>
                            </div>
                          </div>
                          <div
                            class="card-layout-big pointer layout-3 col-6"
                            v-bind:class="{ active: readingCard.mode === 4 }"
                            @click="readingCard.mode = 4"
                          >
                            <div class="rd-conatiner text-center">
                              <img
                                class="svg-icon"
                                src="~assets/dashboard-icons/readingGauge.png"
                              />
                            </div>
                          </div>
                        </div>
                      </div>
                      <div
                        class="reading-card-data-selecter col-7"
                        v-if="readingCard.mode === 4"
                      >
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Add Reading
                          </div>
                          <el-popover
                            placement="right"
                            width="300"
                            popper-class="select-card-reading"
                            v-model="addreadingVisible2"
                          >
                            <f-add-data-point
                              :showLastValue="true"
                              ref="addDataPointForm"
                              @save="getDatapoint"
                              @cancel="cancelDataPointAdder"
                              :source="'dashboardedit'"
                            ></f-add-data-point>
                            <el-input
                              slot="reference"
                              :autofocus="true"
                              class="addReading-title el-input-textbox-full-border"
                              v-model="readingCard.readingName"
                              :placeholder="
                                readingCard.mode === 3
                                  ? 'Click to add Reading Graph data'
                                  : 'Click to add Reading'
                              "
                              :disabled="true"
                            ></el-input>
                          </el-popover>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Title
                          </div>
                          <el-input
                            :autofocus="true"
                            class="addReading-title el-input-textbox-full-border"
                            v-model="readingCard.title"
                            placeholder="Enter the title"
                          ></el-input>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Add Target Reading
                          </div>
                          <div class="row readingcard-graph-container">
                            <div class="col-6">
                              <el-select
                                v-model="readingCard.targetreading.targetmode"
                                placeholder="Select the option"
                                class="el-input-textbox-full-border col-9"
                              >
                                <el-option
                                  label="Set constant"
                                  value="constant"
                                ></el-option>
                                <el-option
                                  label="Reading"
                                  value="reading"
                                ></el-option>
                              </el-select>
                            </div>
                            <div class="col-6">
                              <el-popover
                                placement="right"
                                width="300"
                                popper-class="select-card-reading "
                                v-model="addreadingVisible3"
                                v-if="
                                  readingCard.targetreading.targetmode ===
                                    'reading'
                                "
                              >
                                <f-add-data-point
                                  :showLastValue="true"
                                  ref="addDataPointForm1"
                                  @save="getTragetDatapoint"
                                  @cancel="cancelDataPointAdder"
                                  :source="'dashboardedit'"
                                ></f-add-data-point>
                                <el-input
                                  slot="reference"
                                  :autofocus="true"
                                  class="BL0 addReading-title el-input-textbox-full-border"
                                  v-model="
                                    readingCard.targetreading.readingName
                                  "
                                  :placeholder="'Click to add target reading'"
                                  :disabled="true"
                                ></el-input>
                              </el-popover>
                              <el-input
                                v-if="
                                  readingCard.targetreading.targetmode ===
                                    'constant'
                                "
                                :autofocus="true"
                                class="BL0 addReading-title el-input-textbox-full-border"
                                v-model="readingCard.targetreading.count"
                                :placeholder="'Enter only traget value'"
                              ></el-input>
                            </div>
                          </div>
                        </div>
                        <!-- <div class="reading-card-container">
                            <el-checkbox v-model="readingCard.targetreading.needMax">Enable max value</el-checkbox>
                            </div> -->
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Maximum
                          </div>
                          <div class="row readingcard-graph-container">
                            <div class="col-8">
                              <el-select
                                v-model="readingCard.targetreading.maxmode"
                                placeholder="Select the option"
                                class="el-input-textbox-full-border col-12"
                              >
                                <el-option
                                  label="Same as target Reading"
                                  value="usetarget"
                                ></el-option>
                                <el-option
                                  label="Set constant"
                                  value="constant"
                                ></el-option>
                                <el-option
                                  label="Percentage of target reading"
                                  value="percentage"
                                ></el-option>
                              </el-select>
                            </div>
                            <el-input
                              v-if="
                                readingCard.targetreading.maxmode === 'constant'
                              "
                              :autofocus="true"
                              class="BL0 addReading-title el-input-textbox-full-border col-4"
                              v-model="readingCard.targetreading.maxValue"
                              :placeholder="'Enter only Max value'"
                            ></el-input>
                            <el-input
                              v-else-if="
                                readingCard.targetreading.maxmode ===
                                  'usetarget'
                              "
                              :autofocus="true"
                              class="BL0 addReading-title el-input-textbox-full-border col-4"
                              :disabled="true"
                              :placeholder="''"
                            ></el-input>
                            <el-input
                              v-else
                              :autofocus="true"
                              class="BL0 addReading-title el-input-textbox-full-border col-4"
                              v-model="readingCard.targetreading.maxpercentage"
                              :placeholder="'Enter only max %'"
                            ></el-input>
                          </div>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Period
                          </div>
                          <el-select
                            v-model="readingCard.operatorId"
                            placeholder="Please select a building"
                            class="el-input-textbox-full-border"
                          >
                            <el-option
                              :label="dateRange.label"
                              :value="dateRange.value"
                              v-for="(dateRange, index) in getdateOperators()"
                              :key="index"
                              v-if="dateRange.label !== 'Range'"
                            ></el-option>
                          </el-select>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Aggregation
                          </div>
                          <el-select
                            v-model="readingCard.aggregationFunc"
                            placeholder="Please select a building"
                            class="el-input-textbox-full-border"
                          >
                            <el-option
                              :label="legend.label"
                              :value="legend.value"
                              v-for="(legend, index) in aggregateFunctions"
                              :key="index"
                            ></el-option>
                          </el-select>
                        </div>
                      </div>
                      <div class="reading-card-data-selecter col-7" v-else>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Select Reading
                          </div>
                          <el-popover
                            placement="right"
                            width="300"
                            popper-class="select-card-reading"
                            v-model="addreadingVisible2"
                          >
                            <f-add-data-point
                              :showLastValue="true"
                              ref="addDataPointForm"
                              @save="getDatapoint"
                              @cancel="cancelDataPointAdder"
                              :source="'dashboardedit'"
                            ></f-add-data-point>
                            <el-input
                              slot="reference"
                              :autofocus="true"
                              class="addReading-title el-input-textbox-full-border"
                              v-model="readingCard.readingName"
                              :placeholder="
                                readingCard.mode === 3
                                  ? 'Click to add Reading Graph data'
                                  : 'Click to add Reading'
                              "
                              :disabled="true"
                            ></el-input>
                          </el-popover>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Title
                          </div>
                          <el-input
                            :autofocus="true"
                            class="addReading-title el-input-textbox-full-border"
                            v-model="readingCard.title"
                            placeholder="Enter the title"
                          ></el-input>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Period
                          </div>
                          <el-select
                            v-model="readingCard.operatorId"
                            placeholder="Please select a building"
                            class="el-input-textbox-full-border"
                          >
                            <el-option
                              :label="dateRange.label"
                              :value="dateRange.value"
                              v-for="(dateRange, index) in getdateOperators()"
                              :key="index"
                              v-if="dateRange.label !== 'Range'"
                            ></el-option>
                          </el-select>
                        </div>
                        <div
                          class="reading-card-container"
                          v-if="readingCard.mode === 3"
                        >
                          <div class="reading-card-header fc-input-label-txt">
                            Aggregation
                          </div>
                          <el-select
                            v-model="readingCard.legend"
                            placeholder="Please select a building"
                            class="el-input-textbox-full-border"
                          >
                            <el-option
                              :label="legend.label"
                              :value="legend.name"
                              v-for="(legend, index) in aggregateFunctions"
                              :key="index"
                            ></el-option>
                          </el-select>
                        </div>
                        <div class="reading-card-container" v-else>
                          <div class="reading-card-header fc-input-label-txt">
                            Aggregation
                          </div>
                          <el-select
                            v-model="readingCard.aggregationFunc"
                            placeholder="Please select a building"
                            class="el-input-textbox-full-border"
                          >
                            <el-option
                              :label="legend.label"
                              :value="legend.value"
                              v-for="(legend, index) in aggregateFunctions"
                              :key="index"
                            ></el-option>
                          </el-select>
                        </div>
                        <div
                          class="reading-card-container"
                          v-if="readingCard.mode === 3"
                        >
                          <div class="reading-card-header fc-input-label-txt">
                            Graph Aggregation
                          </div>
                          <div class="row readingcard-graph-container">
                            <div class="col-3 readingcard-graph-agg">
                              {{
                                $util.getCardLabelfromOperatorId(
                                  readingCard.operatorId
                                )
                              }}
                            </div>
                            <el-select
                              v-model="readingCard.aggregationFunc"
                              placeholder="Please select a building"
                              class="el-input-textbox-full-border col-9"
                            >
                              <el-option
                                :label="legend.label"
                                :value="legend.value"
                                v-for="(legend, index) in aggregateFunctions2"
                                :key="index"
                              ></el-option>
                            </el-select>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <span slot="footer" class="modal-dialog-footer">
                    <el-button
                      @click="cancelBuilding()"
                      class="modal-btn-cancel"
                      >Cancel</el-button
                    >
                    <el-button
                      type="primary"
                      @click="addreadings()"
                      class="modal-btn-save"
                      >Confirm</el-button
                    >
                  </span>
                </el-dialog>

                <el-dialog
                  title="AHU Status Card"
                  :modal-append-to-body="false"
                  custom-class="select-building-dialog select-reading-dialog select-ahu-dialog "
                  :visible.sync="ahupopup"
                  width="20%"
                  :before-close="cancelBuilding"
                >
                  <div>
                    <div class="reading-card-container">
                      <div class="reading-card-header">
                        Select AHU
                      </div>
                      <el-select
                        v-model="AHUcard.catogry"
                        placeholder="Please select the asset"
                        @change="loadAHUAssets(AHUcard.catogry)"
                      >
                        <el-option label="AHU" value="AHU"></el-option>
                        <el-option
                          label="FAHU"
                          value="FAHU"
                          v-if="this.$account.org.id === 135"
                        ></el-option>
                        <el-option
                          label="FAHU"
                          value="FAHU"
                          v-if="
                            $route.query.showallcards && $account.org.id !== 135
                          "
                        ></el-option>
                      </el-select>
                    </div>
                    <div class="reading-card-container">
                      <div class="reading-card-header">
                        Select Asset
                      </div>
                      <el-select
                        v-model="AHUcard.id"
                        placeholder="Please select the asset"
                      >
                        <el-option
                          :label="asset.name"
                          :value="asset.id"
                          v-for="(asset, index) in assets.assets"
                          :key="index"
                        ></el-option>
                      </el-select>
                    </div>
                  </div>
                  <span slot="footer" class="dialog-footer row">
                    <el-button @click="cancelBuilding()" class="col-6"
                      >Cancel</el-button
                    >
                    <el-button
                      type="primary"
                      @click="addAHUData()"
                      class="col-6"
                      >Confirm</el-button
                    >
                  </span>
                </el-dialog>

                <f-photoUploader
                  :module="currentModuleName ? currentModuleName.module : ''"
                  :dialogVisible.sync="showAvatarUpload"
                  @image-updated="avatarCropSuccess"
                  @input="getInput"
                  @upload-done="avatarCropUploadSuccess"
                  @upload-failed="avatarCropUploadFail"
                >
                </f-photoUploader>
              </div>
            </div>
            <div class="row">
              <div class="col-6">
                <div class="self-center empty-drop-box">
                  <div class="header">DRAG AND DROP REPORTS</div>
                  <div class="subheader">
                    Height and width of the chart can also be adjusted
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div
        class="col-9 container-section"
        :style="getHeight"
        v-bind:class="{ fullWidth: collapseSidebar === true }"
        v-else
      >
        <div class="height100 scrollable dashboardmainlayout">
          <div v-if="loading || !dashboardLayout">
            <div class="sk-cube-grid">
              <div class="sk-cube sk-cube1"></div>
              <div class="sk-cube sk-cube2"></div>
              <div class="sk-cube sk-cube3"></div>
              <div class="sk-cube sk-cube4"></div>
              <div class="sk-cube sk-cube5"></div>
              <div class="sk-cube sk-cube6"></div>
              <div class="sk-cube sk-cube7"></div>
              <div class="sk-cube sk-cube8"></div>
              <div class="sk-cube sk-cube9"></div>
            </div>
          </div>
          <div
            class="dashboard-container"
            :class="{ editmode: editMode }"
            v-else
            @dragenter="containerEntry"
          >
            <dashboard-filter
              v-if="typeof $route.query.filters !== 'undefined'"
            ></dashboard-filter>
            <div
              @drop="drop"
              @dragover="allowDrop"
              v-bind:class="{ dragzone: isDraging == true }"
              v-if="isDraging"
            >
              <div class="dropArea">
                Drop Here !!!!!
                <el-dialog
                  title="Select Building"
                  :modal-append-to-body="false"
                  custom-class="select-building-dialog"
                  :visible.sync="dialogVisible"
                  width="20%"
                  :before-close="cancelBuilding"
                >
                  <el-select
                    v-model="selectedBuilding"
                    placeholder="Please select a building"
                  >
                    <el-option
                      :label="building.name"
                      :value="building.id"
                      v-for="(building, index) in buildings"
                      :key="index"
                    ></el-option>
                  </el-select>
                  <span slot="footer" class="dialog-footer row">
                    <el-button @click="cancelBuilding()" class="col-6"
                      >Cancel</el-button
                    >
                    <el-button
                      type="primary"
                      @click="selectBuildings()"
                      class="col-6"
                      >Confirm</el-button
                    >
                  </span>
                </el-dialog>
                <el-dialog
                  title="WEB CARD"
                  :modal-append-to-body="false"
                  custom-class="select-building-dialog webcard"
                  :visible.sync="webcardVisible"
                  width="20%"
                  :before-close="cancelBuilding"
                >
                  <el-input
                    v-model="webUrl"
                    placeholder="Enter the URL"
                  ></el-input>
                  <span slot="footer" class="dialog-footer row">
                    <el-button @click="cancelBuilding()" class="col-6"
                      >Cancel</el-button
                    >
                    <el-button
                      type="primary"
                      @click="selectwebcard()"
                      class="col-6"
                      >Confirm</el-button
                    >
                  </span>
                </el-dialog>
                <el-dialog
                  custom-class="select-building-dialog select-reading-dialog"
                  v-bind:class="{ readingmode4: readingCard.mode === 4 }"
                  :modal-append-to-body="false"
                  :visible.sync="readingpopup"
                  width="60%"
                  :before-close="cancelBuilding"
                >
                  <div class="new-header-container">
                    <div class="new-header-text">
                      <div class="fc-setup-modal-title">Add Reading Card</div>
                    </div>
                  </div>
                  <div class="mT20 mL40 mR40">
                    <div class="row">
                      <div class="reading-card-layout-changer col-5">
                        <div class="row">
                          <div
                            class="card-layout-big pointer col-6"
                            v-bind:class="{ active: readingCard.mode === 1 }"
                            @click="readingCard.mode = 1"
                          >
                            <div class="rd-conatiner">
                              <div class="sample-card-content">
                                <div class="card-small-heading">
                                  ENERGY USAGE
                                </div>
                                <div class="card-static-reading">
                                  999
                                  <span class="card-reading-value">kWh</span>
                                </div>
                                <div class="f12 bold">THIS MONTH</div>
                              </div>
                            </div>
                          </div>
                          <div
                            class="card-layout-big pointer layout-3 col-6"
                            v-bind:class="{ active: readingCard.mode === 3 }"
                            @click="readingCard.mode = 3"
                          >
                            <div class="rd-conatiner text-center">
                              <img
                                class="svg-icon"
                                src="~assets/dashboard-icons/readingGraph.png"
                              />
                            </div>
                          </div>
                          <div
                            class="card-layout-small pointer col-6"
                            v-bind:class="{ active: readingCard.mode === 2 }"
                            @click="readingCard.mode = 2"
                          >
                            <div class="rd-conatiner">
                              <div class="sample-card-content">
                                <div class="card-small-heading">
                                  ENERGY USAGE
                                </div>
                                <div class="card-static-reading">
                                  999
                                  <span class="card-reading-value">kWh</span>
                                </div>
                                <div class="f12 bold">THIS MONTH</div>
                              </div>
                            </div>
                          </div>
                          <div
                            class="card-layout-big pointer layout-3 col-6"
                            v-bind:class="{ active: readingCard.mode === 4 }"
                            @click="readingCard.mode = 4"
                          >
                            <div class="rd-conatiner text-center">
                              <img
                                class="svg-icon"
                                src="~assets/dashboard-icons/readingGauge.png"
                              />
                            </div>
                          </div>
                        </div>
                      </div>
                      <div
                        class="reading-card-data-selecter col-7"
                        v-if="readingCard.mode === 4"
                      >
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Add Reading
                          </div>
                          <el-popover
                            placement="right"
                            width="300"
                            popper-class="select-card-reading"
                            v-model="addreadingVisible2"
                          >
                            <f-add-data-point
                              :showLastValue="true"
                              ref="addDataPointForm"
                              @save="getDatapoint"
                              @cancel="cancelDataPointAdder"
                              :source="'dashboardedit'"
                            ></f-add-data-point>
                            <el-input
                              slot="reference"
                              :autofocus="true"
                              class="addReading-title el-input-textbox-full-border"
                              v-model="readingCard.readingName"
                              :placeholder="
                                readingCard.mode === 3
                                  ? 'Click to add Reading Graph data'
                                  : 'Click to add Reading'
                              "
                              :disabled="true"
                            ></el-input>
                          </el-popover>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Title
                          </div>
                          <el-input
                            :autofocus="true"
                            class="addReading-title el-input-textbox-full-border"
                            v-model="readingCard.title"
                            placeholder="Enter the title"
                          ></el-input>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Add Target Reading
                          </div>
                          <div class="row readingcard-graph-container">
                            <div class="col-6">
                              <el-select
                                v-model="readingCard.targetreading.targetmode"
                                placeholder="Select the option"
                                class="el-input-textbox-full-border col-9"
                              >
                                <el-option
                                  label="Set constant"
                                  value="constant"
                                ></el-option>
                                <el-option
                                  label="Reading"
                                  value="reading"
                                ></el-option>
                              </el-select>
                            </div>
                            <div class="col-6">
                              <el-popover
                                placement="right"
                                width="300"
                                popper-class="select-card-reading "
                                v-model="addreadingVisible3"
                                v-if="
                                  readingCard.targetreading.targetmode ===
                                    'reading'
                                "
                              >
                                <f-add-data-point
                                  :showLastValue="true"
                                  ref="addDataPointForm1"
                                  @save="getTragetDatapoint"
                                  @cancel="cancelDataPointAdder"
                                  :source="'dashboardedit'"
                                ></f-add-data-point>
                                <el-input
                                  slot="reference"
                                  :autofocus="true"
                                  class="BL0 addReading-title el-input-textbox-full-border"
                                  v-model="
                                    readingCard.targetreading.readingName
                                  "
                                  :placeholder="'Click to add target reading'"
                                  :disabled="true"
                                ></el-input>
                              </el-popover>
                              <el-input
                                v-if="
                                  readingCard.targetreading.targetmode ===
                                    'constant'
                                "
                                :autofocus="true"
                                class="BL0 addReading-title el-input-textbox-full-border"
                                v-model="readingCard.targetreading.count"
                                :placeholder="'Enter only traget value'"
                              ></el-input>
                            </div>
                          </div>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Maximum
                          </div>
                          <div class="row readingcard-graph-container">
                            <div class="col-8">
                              <el-select
                                v-model="readingCard.targetreading.maxmode"
                                placeholder="Select the option"
                                class="el-input-textbox-full-border col-12"
                              >
                                <el-option
                                  label="Same as target Reading"
                                  value="usetarget"
                                ></el-option>
                                <el-option
                                  label="Set constant"
                                  value="constant"
                                ></el-option>
                                <el-option
                                  label="Percentage of target reading"
                                  value="percentage"
                                ></el-option>
                              </el-select>
                            </div>
                            <el-input
                              v-if="
                                readingCard.targetreading.maxmode === 'constant'
                              "
                              :autofocus="true"
                              class="BL0 addReading-title el-input-textbox-full-border col-4"
                              v-model="readingCard.targetreading.maxValue"
                              :placeholder="'Enter only Max value'"
                            ></el-input>
                            <el-input
                              v-else-if="
                                readingCard.targetreading.maxmode ===
                                  'usetarget'
                              "
                              :autofocus="true"
                              class="BL0 addReading-title el-input-textbox-full-border col-4"
                              :disabled="true"
                              :placeholder="''"
                            ></el-input>
                            <el-input
                              v-else
                              :autofocus="true"
                              class="BL0 addReading-title el-input-textbox-full-border col-4"
                              v-model="readingCard.targetreading.maxpercentage"
                              :placeholder="'Enter only max %'"
                            ></el-input>
                          </div>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Period
                          </div>
                          <el-select
                            v-model="readingCard.operatorId"
                            placeholder="Please select a building"
                            class="el-input-textbox-full-border"
                          >
                            <el-option
                              :label="dateRange.label"
                              :value="dateRange.value"
                              v-for="(dateRange, index) in getdateOperators()"
                              :key="index"
                              v-if="dateRange.label !== 'Range'"
                            ></el-option>
                          </el-select>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Aggregation
                          </div>
                          <el-select
                            v-model="readingCard.aggregationFunc"
                            placeholder="Please select a building"
                            class="el-input-textbox-full-border"
                          >
                            <el-option
                              :label="legend.label"
                              :value="legend.value"
                              v-for="(legend, index) in aggregateFunctions"
                              :key="index"
                            ></el-option>
                          </el-select>
                        </div>
                      </div>
                      <div class="reading-card-data-selecter col-7" v-else>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Select Reading
                          </div>
                          <el-popover
                            placement="right"
                            width="300"
                            popper-class="select-card-reading"
                            v-model="addreadingVisible2"
                          >
                            <f-add-data-point
                              :showLastValue="true"
                              ref="addDataPointForm"
                              @save="getDatapoint"
                              @cancel="cancelDataPointAdder"
                              :source="'dashboardedit'"
                            ></f-add-data-point>
                            <el-input
                              slot="reference"
                              :autofocus="true"
                              class="addReading-title el-input-textbox-full-border"
                              v-model="readingCard.readingName"
                              :placeholder="
                                readingCard.mode === 3
                                  ? 'Click to add Reading Graph data'
                                  : 'Click to add Reading'
                              "
                              :disabled="true"
                            ></el-input>
                          </el-popover>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Title
                          </div>
                          <el-input
                            :autofocus="true"
                            class="addReading-title el-input-textbox-full-border"
                            v-model="readingCard.title"
                            placeholder="Enter the title"
                          ></el-input>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            Period
                          </div>
                          <el-select
                            v-model="readingCard.operatorId"
                            placeholder="Please select a building"
                            class="el-input-textbox-full-border"
                          >
                            <el-option
                              :label="dateRange.label"
                              :value="dateRange.value"
                              v-for="(dateRange, index) in getdateOperators()"
                              :key="index"
                              v-if="dateRange.label !== 'Range'"
                            ></el-option>
                          </el-select>
                        </div>
                        <div
                          class="reading-card-container"
                          v-if="readingCard.mode === 3"
                        >
                          <div class="reading-card-header fc-input-label-txt">
                            Aggregation
                          </div>
                          <el-select
                            v-model="readingCard.legend"
                            placeholder="Please select a building"
                            class="el-input-textbox-full-border"
                          >
                            <el-option
                              :label="legend.label"
                              :value="legend.name"
                              v-for="(legend, index) in aggregateFunctions"
                              :key="index"
                            ></el-option>
                          </el-select>
                        </div>
                        <div class="reading-card-container" v-else>
                          <div class="reading-card-header fc-input-label-txt">
                            Aggregation
                          </div>
                          <el-select
                            v-model="readingCard.aggregationFunc"
                            placeholder="Please select a building"
                            class="el-input-textbox-full-border"
                          >
                            <el-option
                              :label="legend.label"
                              :value="legend.value"
                              v-for="(legend, index) in aggregateFunctions"
                              :key="index"
                            ></el-option>
                          </el-select>
                        </div>
                        <div
                          class="reading-card-container"
                          v-if="readingCard.mode === 3"
                        >
                          <div class="reading-card-header fc-input-label-txt">
                            Graph Aggregation
                          </div>
                          <div class="row readingcard-graph-container">
                            <div class="col-3 readingcard-graph-agg">
                              {{
                                $util.getCardLabelfromOperatorId(
                                  readingCard.operatorId
                                )
                              }}
                            </div>
                            <el-select
                              v-model="readingCard.aggregationFunc"
                              placeholder="Please select a building"
                              class="el-input-textbox-full-border col-9"
                            >
                              <el-option
                                :label="legend.label"
                                :value="legend.value"
                                v-for="(legend, index) in aggregateFunctions2"
                                :key="index"
                              ></el-option>
                            </el-select>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <span slot="footer" class="modal-dialog-footer">
                    <el-button
                      @click="cancelBuilding()"
                      class="modal-btn-cancel"
                      >Cancel</el-button
                    >
                    <el-button
                      type="primary"
                      @click="addreadings()"
                      class="modal-btn-save"
                      >Confirm</el-button
                    >
                  </span>
                </el-dialog>
                <el-dialog
                  title="AHU Status Card"
                  :modal-append-to-body="false"
                  custom-class="select-building-dialog select-reading-dialog select-ahu-dialog "
                  :visible.sync="ahupopup"
                  width="20%"
                  :before-close="cancelBuilding"
                >
                  <div>
                    <div class="reading-card-container">
                      <div class="reading-card-header">
                        Select AHU
                      </div>
                      <el-select
                        v-model="AHUcard.catogry"
                        placeholder="Please select the asset"
                        @change="loadAHUAssets(AHUcard.catogry)"
                      >
                        <el-option label="AHU" value="AHU"></el-option>
                        <el-option
                          label="FAHU"
                          value="FAHU"
                          v-if="this.$account.org.id === 135"
                        ></el-option>
                        <el-option
                          label="FAHU"
                          value="FAHU"
                          v-if="
                            $route.query.showallcards && $account.org.id !== 135
                          "
                        ></el-option>
                      </el-select>
                    </div>
                    <div class="reading-card-container">
                      <div class="reading-card-header">
                        Select Asset
                      </div>
                      <el-select
                        v-model="AHUcard.id"
                        placeholder="Please select the asset"
                      >
                        <el-option
                          :label="asset.name"
                          :value="asset.id"
                          v-for="(asset, index) in assets.assets"
                          :key="index"
                        ></el-option>
                      </el-select>
                    </div>
                  </div>
                  <span slot="footer" class="modal-dialog-footer">
                    <el-button
                      @click="cancelBuilding()"
                      class="modal-btn-cancel"
                      >Cancel</el-button
                    >
                    <el-button
                      type="primary"
                      @click="addAHUData()"
                      class="modal-btn-save"
                      >Confirm</el-button
                    >
                  </span>
                </el-dialog>
                <f-photoUploader
                  :module="currentModuleName ? currentModuleName.module : ''"
                  :dialogVisible.sync="showAvatarUpload"
                  @image-updated="avatarCropSuccess"
                  @input="getInput"
                  @upload-done="avatarCropUploadSuccess"
                  @upload-failed="avatarCropUploadFail"
                >
                </f-photoUploader>
              </div>
            </div>
            <div class="row" v-if="!dashboardLayout.length">
              <div class="col-6">
                <div class="self-center empty-drop-box">
                  <div class="header">DRAG AND DROP REPORTS</div>
                  <div class="subheader">
                    Height and width of the chart can also be adjusted
                  </div>
                </div>
              </div>
            </div>
            <div v-else>
              <company-listslider
                v-if="
                  $route.path === '/app/em/dbediter/portfolio' ||
                    $route.path === '/app/em/dbediter/portfolio'
                "
                style="cursor:pointer; padding: 15px 15px 0px 15px;"
              ></company-listslider>
              <alarm-company-listslider
                v-if="
                  $route.path === '/app/fa/dbediter/portfolio' ||
                    $route.path === '/app/fa/dbediter/portfolio'
                "
                style="cursor:pointer; padding: 15px 15px 0px 15px;"
              ></alarm-company-listslider>
              <grid-layout
                :layout.sync="dashboardLayout"
                :col-num="24"
                :rowHeight="height"
                :is-draggable="editMode"
                :is-resizable="editMode"
                :use-css-transforms="true"
                :margin="[15, 15]"
              >
                <grid-item
                  class="dashboard-f-widget"
                  v-for="(item, index) in dashboardLayout"
                  :is-mirrored="false"
                  :x="item.x"
                  :y="item.y"
                  :w="item.w"
                  :h="item.h"
                  :i="item.i"
                  :key="index"
                  drag-allow-from=".fc-widget-header, .dragabale-card, .map-drag-area, .shimmer, .ql-toolbar"
                  :drag-ignore-from="getDragIgnoreClass(item.widget)"
                  :is-resizable="resizecontrol(item)"
                  v-bind:class="{ gridempty: item && item.w === 0 }"
                >
                  <f-widget
                    :type="item.widget.type"
                    :widget="item.widget"
                    :grid="item"
                    :key="index"
                    :rowHeight="height"
                    :dashboard="dashboard ? dashboard.id : null"
                    @deletechart="deleteChart(index)"
                    @widget="updatewidget, getIndex(index)"
                    :mode="editMode"
                  ></f-widget>
                </grid-item>
              </grid-layout>
            </div>
          </div>
          <div v-if="editMode">
            <new-chart-widget
              :dashboardId="dashboard ? dashboard.id : null"
              ref="newChartWidget"
            ></new-chart-widget>
            <new-list-widget
              :dashboardId="dashboard ? dashboard.id : null"
              ref="newListWidget"
            ></new-list-widget>
            <new-map-widget
              :dashboardId="dashboard ? dashboard.id : null"
              ref="newMapWidget"
            ></new-map-widget>
            <new-prebuilt-widget
              :dashboardId="dashboard ? dashboard.id : null"
              ref="newStaticWidget"
            ></new-prebuilt-widget>
            <new-web-widget
              :dashboardId="dashboard ? dashboard.id : null"
              ref="newWebWidget"
            ></new-web-widget>
            <el-dialog
              title="Dashboard Sharing"
              :visible.sync="sharingDialogVisible"
              width="50%"
            >
              <el-row align="middle" style="margin:0px;" :gutter="20">
                <el-col
                  :span="24"
                  style="padding-right: 35px;padding-left:0px;"
                >
                  <div class="add">
                    <el-radio v-model="shareTo" :label="1">Only Me</el-radio>
                    <el-radio v-model="shareTo" :label="2">Everyone</el-radio>
                    <el-radio v-model="shareTo" :label="3">Specific</el-radio>
                  </div>
                </el-col>
              </el-row>
              <el-row
                v-if="shareTo === 3"
                align="middle"
                style="margin:0px;padding-top:20px;"
                :gutter="20"
              >
                <el-col :span="24" style="padding-right: 0px;padding-left:0px;">
                  <div class="textcolor">Users</div>
                  <div class="add">
                    <el-select
                      v-model="sharedUsers"
                      multiple
                      style="width:100%"
                      class="form-item "
                      :placeholder="$t('common.wo_report.choose_users')"
                    >
                      <el-option
                        v-for="user in users"
                        :key="user.id"
                        :label="user.name"
                        :value="user.id"
                      ></el-option>
                    </el-select>
                  </div>
                </el-col>
              </el-row>
              <el-row
                v-if="shareTo === 3"
                align="middle"
                style="margin:0px;padding-top:20px;"
                :gutter="20"
              >
                <el-col :span="24" style="padding-right:0px;padding-left:0px;">
                  <div class="textcolor">Roles</div>
                  <div class="add">
                    <el-select
                      v-model="sharedRoles"
                      multiple
                      style="width:100%"
                      class="form-item "
                      :placeholder="$t('common.wo_report.choose_roles')"
                    >
                      <el-option
                        v-for="role in roles"
                        :key="role.id"
                        :label="role.name"
                        :value="role.id"
                      ></el-option>
                    </el-select>
                  </div>
                </el-col>
              </el-row>
              <el-row
                v-if="shareTo === 3"
                align="middle"
                style="margin:0px;padding-top:20px;"
                :gutter="20"
              >
                <el-col :span="24" style="padding-right: 0px;padding-left:0px;">
                  <div class="textcolor">Teams</div>
                  <div class="add">
                    <el-select
                      v-model="sharedGroups"
                      multiple
                      style="width:100%"
                      class="form-item "
                      :placeholder="$t('common.wo_report.choose_teams')"
                    >
                      <el-option
                        v-for="group in groups"
                        :key="group.id"
                        :label="group.name"
                        :value="group.id"
                      ></el-option>
                    </el-select>
                  </div>
                </el-col>
              </el-row>
              <span slot="footer" class="dialog-footer">
                <el-button @click="sharingDialogVisible = false"
                  >Cancel</el-button
                >
                <el-button type="primary" @click="applySharing"
                  >Confirm</el-button
                >
              </span>
            </el-dialog>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import FWidget from './widget/FWidget'
import NewChartWidget from './forms/NewChartWidget'
import NewListWidget from './forms/NewListWidget'
import NewMapWidget from './forms/NewMapWidget'
import NewPrebuiltWidget from './forms/NewPrebuiltWidget'
import NewWebWidget from './forms/NewWebWidget'
import CompanyListslider from '@/CompanyListslider'
import AlarmCompanyListslider from '@/CompanySlider'
import FAddDataPoint from 'pages/energy/analytics/components/FAddDataPoint'
import { GridLayout, GridItem } from 'vue-grid-layout'
import DashboardFilter from './DashboardFilter'
import ChartIconHover from 'charts/components/chartIconHover'
import ReportHelper from 'pages/report/mixins/ReportHelper'
import DateUtil from '@/mixins/DateHelper'
import FPhotoUploader from '@/FPhotoUploader'
import { mapState, mapGetters } from 'vuex'
import { getBaseURL } from 'util/baseUrl'
import { API } from '@facilio/api'

export default {
  mixins: [ReportHelper, DateUtil],
  props: ['sites', 'currentDashboard'],
  components: {
    NewChartWidget,
    NewListWidget,
    NewMapWidget,
    NewPrebuiltWidget,
    NewWebWidget,
    FWidget,
    GridLayout,
    GridItem,
    CompanyListslider,
    DashboardFilter,
    AlarmCompanyListslider,
    ChartIconHover,
    FAddDataPoint,
    FPhotoUploader,
  },
  data() {
    return {
      loading: true,
      dialogVisible: false,
      readingpopup: false,
      webcardVisible: false,
      isMobileDashboard: false,
      dashboardLoad: false,
      ahupopup: false,
      addreadingVisible: false,
      addreadingVisible2: false,
      addreadingVisible3: false,
      selectedBuilding: null,
      dashboard: null,
      assets: [],
      AHUcardCatogry: '',
      AHUcard: {
        catogry: '',
        id: null,
      },
      newdashboard: {
        label: null,
        linkName: '',
      },
      readingCard: {
        title: '',
        mode: 1,
        parentId: -1,
        legend: 'sum',
        parentName1: '',
        targetreading: {
          max: 0,
          maxmode: 'usetarget',
          readingName: '',
          targetmode: 'constant',
          count: 0,
          maxValue: 0,
          maxPercentage: 0,
          needMax: false,
          parentName2: '',
        },
        aggregationFunc: 3,
        readingName: '',
        operatorId: 28,
        layout: {
          color: '',
          fontColor: '',
        },
      },
      webUrl: null,
      rowHeight: 100,
      dashboardLayout: [],
      deletlayout: [],
      webView: [],
      sharingDialogVisible: false,
      removeChartList: [],
      dashbaordFolderName: '',
      selectedDashoardFolder: null,
      dashboardFolderList: [],
      convertedlayout: null,
      chartDeleted: false,
      newDashboardData: [],
      buildings: [],
      aggregateFunctions: [
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
        {
          label: 'Current Value',
          value: 6,
          name: 'lastValue',
        },
      ],
      aggregateFunctions2: [
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
      chartypes: [
        {
          type: 'static',
          value: 0,
        },
        {
          type: 'chart',
          value: 1,
        },
        {
          type: 'list',
          value: 2,
        },
        {
          type: 'view',
          value: 2,
        },
        {
          type: 'map',
          value: 3,
        },
        {
          type: 'web',
          value: 4,
        },
      ],
      readingcardlegend: ['Sum', 'Avg', 'Min', 'Max'],
      supportedModules: [
        {
          label: 'Work Orders',
          value: 'workorder',
          list: [],
          expand: false,
        },
        {
          label: 'Planned Maintenance',
          value: 'preventivemaintenance',
          list: [],
          expand: false,
        },
        {
          label: 'Alarms',
          value: 'alarm',
          list: [],
          expand: false,
        },
        {
          label: 'Assets',
          value: 'asset',
          list: [],
          expand: false,
        },
      ],
      cards: [
        {
          key: 'readingcard',
          label: 'Reading card',
          w: 4,
          h: 3,
        },
        {
          key: 'textcard',
          label: 'Text card',
          w: 12,
          h: 6,
        },
        {
          key: 'imagecard',
          label: 'Image card',
          w: 5,
          h: 5,
        },
        {
          key: 'web',
          label: 'Web Card',
          w: 12,
          h: 6,
        },
        {
          disbale: this.$account.org.id !== 135 ? 'true' : false,
          key: 'fahuStatusCard',
          label: 'AHU Status Card',
          w: 3,
          h: 3,
        },
        {
          disbale:
            this.$route.query &&
            this.$route.query.showallcards &&
            this.$account.org.id !== 135
              ? false
              : 'true',
          key: 'fahuStatusCard3',
          label: 'AHU Status Card',
          w: 3,
          h: 3,
        },
        {
          disbale: this.$account.org.id !== 133 ? 'true' : false,
          key: 'fahuStatusCard1',
          label: 'AHU Status Card',
          w: 3,
          h: 3,
        },
        {
          key: 'weathermini',
          label: 'Weather card',
          w: 6,
          h: 3,
        },
        {
          key: 'carbonmini',
          label: 'Carbon emission  card',
          w: 6,
          h: 3,
        },
        {
          key: 'energycostmini',
          label: 'Energy cost card',
          w: 6,
          h: 3,
        },
        {
          key: 'profilemini',
          label: 'Building profile',
          w: 6,
          h: 3,
        },
      ],
      staticWidgets: [
        {
          key: 'profilecard',
          label: 'Profile Card',
          w: 6,
          h: 6,
        },
        {
          key: 'energycard',
          label: 'Energy Card',
          w: 6,
          h: 6,
        },
        {
          key: 'energycost',
          label: 'Energy Cost',
          w: 6,
          h: 6,
        },
        {
          key: 'energycostaltayer',
          label: 'Energy Saving',
          w: 6,
          h: 6,
        },
        {
          key: 'weathercard',
          label: 'Weather Card',
          w: 6,
          h: 6,
        },
        {
          key: 'weathercardaltayer',
          label: 'Weather saving Card',
          w: 6,
          h: 6,
        },
        {
          key: 'workordersummary',
          label: 'Workorder Summary',
          w: 6,
          h: 8,
        },
        {
          key: 'categories',
          label: 'Categories',
          w: 6,
          h: 8,
        },
        {
          key: 'technicians',
          label: 'Technicians',
          w: 6,
          h: 8,
        },
        {
          key: 'closedwotrend',
          label: 'Closed WO Trend',
          w: 6,
          h: 8,
        },
        {
          key: 'mywosummary',
          label: 'My Workorder Summary',
          w: 6,
          h: 8,
        },
        {
          key: 'openalarms',
          label: 'Open Alarms',
          w: 6,
          h: 8,
        },
        {
          key: 'buildingopenalarms',
          label: 'Building Open Alarms',
          w: 6,
          h: 8,
        },
        {
          key: 'mapwidget',
          label: 'Site Map Widget',
          w: 16,
          h: 8,
        },
        {
          key: 'buildingmapwidget',
          label: 'Building Map Widget',
          w: 16,
          h: 8,
        },
        {
          key: 'sampleppm',
          label: 'PPM Low to Moderate Risk Scoresheet',
          w: 24,
          h: 8,
        },
        {
          key: 'utsdata',
          label: 'uts',
          w: 24,
          h: 10,
        },
      ],
      emptywidget: false,
      isDraging: false,
      shareTo: 2,
      sharedUsers: [],
      sharedRoles: [],
      sharedGroups: [],
      viewList: [],
      collapseSidebar: false,
      reportTree: [],
      newReportTree: [],
      dashboardlength: 0,
      option: 'report',
      temp: {
        dis: '',
        label: '',
      },
      list2: [
        {
          widget: {
            layout: {
              width: null,
              position: 1,
              height: 650,
            },
            header: {
              subtitle: 'today',
              title: 'Open Workorders by Team',
              export: true,
            },
            dataOptions: {
              dataurl: '/dashboard/getData?reportId=231',
              refresh_interval: 100,
              reportId: 231,
              name: 'dummy',
              type: 'stackedbar',
            },
            type: 'chart',
          },
          label: 'Open Workorders by Team',
        },
      ],
      dragedElement: 'test',
      showAvatarUpload: false,
      avatarDataUrl: null,
      imagecardData: null,
      params: {
        userId: 0,
      },
      headers: {
        Authorization: '',
      },
      imageUploadUrl: null,
      currentModuleName: null,
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadBuildings')
    this.$store.dispatch('loadUsers')
  },
  computed: {
    ...mapState({
      groups: state => state.groups,
      users: state => state.users,
      roles: state => state.roles,
    }),

    ...mapGetters(['getAssetCategoryPickList', 'getCurrentUser']),
    assetCategory() {
      return this.getAssetCategoryPickList()
    },
    dashboardLink() {
      if (
        this.$route.params.dashboardlink === 'residentialbuildingdashboard' ||
        this.$route.params.dashboardlink === 'commercialbuildingdashboard'
      ) {
        return 'buildingdashboard'
      } else {
        return this.$route.params.dashboardlink
      }
    },
    buildingId() {
      return this.$route.params.buildingid
    },
    editMode() {
      return true
    },
    getHeight() {
      return (
        'height:' +
        (window.appConfig && window.appConfig.isTvMode
          ? window.appConfig.height + 'px;'
          : '80vh;')
      )
    },
    height() {
      if (this.collapseSidebar) {
        return (
          document.getElementsByClassName('layout-page')[0].offsetWidth / 24 -
          20
        )
      } else {
        let rowHeight =
          document.getElementsByClassName('layout-page')[0].offsetWidth / 24 -
          20
        return rowHeight - rowHeight / 8
      }
    },
    mode() {
      if (this.$route.query) {
        if (this.$route.query.create === 'new') {
          return 'new'
        } else {
          return 'edit'
        }
      } else {
        return 'edit'
      }
    },
  },
  mounted() {
    this.initData()
  },
  watch: {
    dashboardLink: function(newVal) {
      this.loadFolder('watch')
    },
    buildingId: function(newVal) {
      this.loadFolder('watch')
    },
    $route: function(from, to) {
      this.openFirstReport()
    },
  },
  methods: {
    initData() {
      let self = this
      self.loading = true
      let baseURL = getBaseURL()
        ? getBaseURL()
        : document.location.origin + '/api'
      if (baseURL.indexOf('http') === -1) {
        baseURL = document.location.protocol + '//' + baseURL
      }
      this.imageUploadUrl = baseURL + '/widget/addPhoto'
      this.currentModuleName = this.getCurrentModule()
      Promise.all([
        this.loadReportTree(),
        this.loadNewReportTree(),
        this.getAllbuildings(),
      ]).then(() => {
        self.loadFolder()
        self.loading = false
      })
      this.moduleViews()
      this.dashbaordScreen()
    },
    resizecontrol(item) {
      if (this.editMode) {
        if (
          item.widget.type === 'static' &&
          !this.allowResizewidget(item.widget.dataOptions.staticKey)
        ) {
          return false
        } else if (
          item.widget.type === 'cards' &&
          !this.allowResizewidget(item.widget.dataOptions.staticKey)
        ) {
          return false
        } else {
          return true
        }
      }
      // if (item) {
      //   return true
      // }
    },
    switchLayout() {
      if (!this.dashboardLoad) {
        if (this.dashboardLayout.length) {
          let layout = []
          for (let i = 0; i < this.dashboardLayout.length; i++) {
            layout.push(
              this.convertLayout(
                this.setlayout(
                  this.$helpers.cloneObject(this.dashboardLayout[i])
                )
              )
            )
          }
          this.dashboardLayout = layout
          this.EnableMobileDashboard()
        }
      } else {
        this.dashboardLoad = false
        this.EnableMobileDashboard()
      }
    },
    convertLayout(layout) {
      let templayout = this.$helpers.cloneObject(layout)
      if (this.isMobileDashboard) {
        templayout.w = templayout.mw
        templayout.h = templayout.mh
        templayout.x = templayout.mx
        templayout.y = templayout.my
        templayout.minW = templayout.mminW
        templayout.minH = templayout.mminH
      } else {
        templayout.w = templayout.ww
        templayout.h = templayout.wh
        templayout.x = templayout.wx
        templayout.y = templayout.wy
        templayout.minW = templayout.wminW
        templayout.minH = templayout.wminH
      }
      return templayout
    },
    setupLayout(layout) {
      let templayout = this.$helpers.cloneObject(layout)
      if (this.isMobileDashboard) {
        templayout.mw = this.$helpers.cloneObject(templayout.w)
        templayout.mh = this.$helpers.cloneObject(templayout.h)
        templayout.mx = this.$helpers.cloneObject(templayout.x)
        templayout.my = this.$helpers.cloneObject(templayout.y)
        templayout.mminW = this.$helpers.cloneObject(templayout.minW)
        templayout.mminH = this.$helpers.cloneObject(templayout.minH)
      } else {
        templayout.ww = this.$helpers.cloneObject(templayout.w)
        templayout.wh = this.$helpers.cloneObject(templayout.h)
        templayout.wx = this.$helpers.cloneObject(templayout.x)
        templayout.wy = this.$helpers.cloneObject(templayout.y)
        templayout.wminW = this.$helpers.cloneObject(templayout.minW)
        templayout.wminH = this.$helpers.cloneObject(templayout.minH)
      }
      return templayout
    },
    setlayout(layout) {
      let templayout = this.$helpers.cloneObject(layout)
      if (this.isMobileDashboard) {
        templayout.ww = this.$helpers.cloneObject(templayout.w)
        templayout.wh = this.$helpers.cloneObject(templayout.h)
        templayout.wx = this.$helpers.cloneObject(templayout.x)
        templayout.wy = this.$helpers.cloneObject(templayout.y)
        templayout.wminW = this.$helpers.cloneObject(templayout.minW)
        templayout.wminH = this.$helpers.cloneObject(templayout.minH)
      } else {
        templayout.mw = this.$helpers.cloneObject(templayout.w)
        templayout.mh = this.$helpers.cloneObject(templayout.h)
        templayout.mx = this.$helpers.cloneObject(templayout.x)
        templayout.my = this.$helpers.cloneObject(templayout.y)
        templayout.mminW = this.$helpers.cloneObject(templayout.minW)
        templayout.mminH = this.$helpers.cloneObject(templayout.minH)
      }
      return templayout
    },
    allowResizewidget(key) {
      if (key === 'textcard') {
        return true
      } else if (key === 'imagecard') {
        return true
      } else if (key === 'web') {
        return true
      } else {
        return false
      }
    },
    loadAHUAssets(catagoryName) {
      if (this.assetCategory) {
        let self = this
        let catagory = this.assetCategory
        let catagoryId = null
        Object.keys(this.assetCategory).forEach(function(id) {
          if (catagory[parseInt(id)] === catagoryName) {
            catagoryId = parseInt(id)
          }
        })
        if (catagoryId && this.AHUcardCatogry !== 'catagoryName') {
          this.$util.loadAsset({ categoryId: catagoryId }).then(response => {
            self.assets = response
            self.AHUcardCatogry = catagoryName
          })
        }
      }
    },
    getAllbuildings(siteType) {
      let self = this
      let params = {}
      return new Promise((resolve, reject) => {
        let currentModule = self.getCurrentModule()
        let url = '/report/energy/portfolio/getAllBuildings'
        if (
          currentModule.module === 'workorder' ||
          currentModule.module === 'alarm'
        ) {
          url = '/report/alarms/getAllBuildings'
        }
        self.$http
          .post(url, params)
          .then(function(response) {
            if (currentModule.module === 'energydata') {
              self.buildings = response.data.reportData.buildingDetails
              resolve(response)
            } else {
              self.buildings = response.data
            }
          })
          .catch(function(error) {
            console.log(error)
          })
      })
    },
    loadReportTree() {
      let self = this
      let moduleName = this.getCurrentModule().module
      if (moduleName === 'energy') {
        moduleName = 'energyData'
      }
      self.$http
        .get(
          '/report/workorder/getAllWorkOrderReports?moduleName=' + moduleName
        )
        .then(function(response) {
          let data = response.data.allWorkOrderJsonReports
          let treeData = data.map(function(d) {
            d.expand = false
            if (self.$route.params.reportid) {
              let reportId = parseInt(self.$route.params.reportid)
              let report = d.children.find(
                rt => rt.widget.dataOptions.reportId === reportId
              )
              if (report) {
                d.expand = true
              }
            }
            return d
          })
          self.reportTree = treeData.filter(
            row => row.label !== 'Default' && row.label !== 'Old Reports'
          )
          self.openFirstReport()
          self.loading = false
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
          }
        })
      if (this.mode !== 'new') {
        this.loadFolder('watch')
      }
    },
    loadNewReportTree() {
      let self = this
      let moduleName = this.getCurrentModule().module
      if (moduleName === 'energy') {
        moduleName = 'energyData'
      }
      API.get('/v3/report/folders?moduleName=' + moduleName)
        .then(response => {
          if (!response.error) {
            let data = response.data.reportFolders
            let treeData = data.map(function(d) {
              d.expand = false
              if (self.$route.params.reportid) {
                let reportId = parseInt(self.$route.params.reportid)
                let report = d.reports.find(rt => rt.id === reportId)
                if (report) {
                  d.expand = true
                }
              }
              return d
            })
            self.newReportTree = treeData
          }
        })
        .catch(function(error) {
          if (error) {
            console.log(error)
          }
        })
    },
    moduleViews() {
      let self = this
      for (let i = 0; i < self.supportedModules.length; i++) {
        if (
          self.supportedModules[i].value &&
          self.supportedModules[i].value !== 'custom'
        ) {
          self.$http
            .get('/view?moduleName=' + self.supportedModules[i].value)
            .then(function(response) {
              self.supportedModules[i].list = response.data.views
            })
        }
      }
    },
    deletedashboard() {
      let self = this
      let data = {
        dashboardId: this.dashboard.id,
      }
      self.$dialog
        .confirm({
          title: 'Delete Dashboard',
          message: 'Are you sure you want to delete this Dashboard?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(function(value) {
          if (value) {
            self.$http
              .post('/dashboard/deleteDashboard', data)
              .then(function(response) {
                self.$message({
                  message: 'Dashboard deleted successfully!',
                  type: 'success',
                })
                self.$router.replace({
                  path: self.constractUrl(),
                })
              })
          }
        })
    },
    loadSharing: function() {
      let self = this
      if (self.dashboard) {
        self.$http
          .get('/dashboardsharing/' + self.dashboard.id)
          .then(function(response) {
            if (response.data.dashboardSharing) {
              if (response.data.dashboardSharing.length === 0) {
                self.shareTo = 2
              } else {
                self.sharedUsers = []
                self.sharedRoles = []
                self.sharedGroups = []
                for (
                  let i = 0;
                  i < response.data.dashboardSharing.length;
                  i++
                ) {
                  let dashboardSharing = response.data.dashboardSharing[i]
                  if (dashboardSharing.sharingType === 1) {
                    self.sharedUsers.push(dashboardSharing.orgUserId)
                  } else if (dashboardSharing.sharingType === 2) {
                    self.sharedRoles.push(dashboardSharing.roleId)
                  } else if (dashboardSharing.sharingType === 3) {
                    self.sharedGroups.push(dashboardSharing.groupId)
                  }
                }
                if (
                  response.data.dashboardSharing.length === 1 &&
                  self.sharedUsers.length === 1 &&
                  self.sharedUsers[0] === self.getCurrentUser().ouid
                ) {
                  self.shareTo = 1
                } else {
                  self.shareTo = 3
                }
              }
            }
            self.sharingDialogVisible = true
          })
      } else {
        self.$message({
          message: 'Please Save The Dashboard',
          type: 'error',
        })
      }
    },
    updatewidget(widget) {
      console.log(
        'update widget called',
        widget,
        this.dashboardLayout[this.getIndex()].widget,
        this.dashboardLayout
      )
    },
    getIndex(index) {
      return index
    },
    applySharing: function() {
      let self = this
      let dashboardSharing = []
      if (self.shareTo === 1) {
        dashboardSharing.push({
          dashboardId: self.dashboard.id,
          sharingType: 1,
          orgUserId: self.getCurrentUser().ouid,
        })
      } else if (self.shareTo === 3) {
        if (self.sharedUsers.length > 0) {
          dashboardSharing.push({
            dashboardId: self.dashboard.id,
            sharingType: 1,
            orgUserId: self.getCurrentUser().ouid,
          })
          for (let i = 0; i < self.sharedUsers.length; i++) {
            if (self.sharedUsers[i] !== self.getCurrentUser().ouid) {
              dashboardSharing.push({
                dashboardId: self.dashboard.id,
                sharingType: 1,
                orgUserId: self.sharedUsers[i],
              })
            }
          }
        }
        if (self.sharedRoles.length > 0) {
          for (let i = 0; i < self.sharedRoles.length; i++) {
            dashboardSharing.push({
              dashboardId: self.dashboard.id,
              sharingType: 2,
              roleId: self.sharedRoles[i],
            })
          }
        }
        if (self.sharedGroups.length > 0) {
          for (let i = 0; i < self.sharedGroups.length; i++) {
            dashboardSharing.push({
              dashboardId: self.dashboard.id,
              sharingType: 3,
              groupId: self.sharedGroups[i],
            })
          }
        }
      }
      self.$http
        .post('/dashboardsharing/apply', {
          dashboardId: self.dashboard.id,
          dashboardSharing: dashboardSharing,
        })
        .then(function(response) {
          self.$message({
            message: 'Sharing applied successfully!',
            type: 'success',
          })
          self.sharingDialogVisible = false
        })
    },
    loadDashboard() {
      let self = this
      self.loading = true
      self.dashboardLayout = null
      let url =
        '/dashboard/' +
        this.dashboardLink +
        '?moduleName=' +
        this.getCurrentModule().module
      if (
        self.$route.params.buildingid &&
        this.dashboardLink === 'sitedashboard'
      ) {
        url += '&siteId=' + self.$route.params.buildingid
      } else if (
        self.$route.params.buildingid &&
        this.dashboardLink === 'buildingdashboard'
      ) {
        url += '&buildingId=' + self.$route.params.buildingid
      }
      self.$http.get(url).then(function(response) {
        self.dashboard = response.data.dashboardJson[0]
        self.prepareDashboardLayout()
        if (self.mode === 'edit' && self.dashboardFolderList.length) {
          self.selectedDashoardFolder = self.dashboardFolderList.find(
            rt => rt.id === self.dashboard.dashboardFolderId
          )
          self.dashbaordFolderName =
            self.selectedDashoardFolder && self.selectedDashoardFolder.name
              ? self.selectedDashoardFolder.name
              : ''
        }
        if (self.dashboard.mobileEnabled) {
          self.isMobileDashboard = true
          self.dashboardLoad = true
        }
        self.loading = false
      })
    },
    prepareDashboardLayout() {
      let self = this
      let layout = []
      let tx = 0
      for (let i = 0; i < self.dashboard.children.length; i++) {
        if (tx + self.dashboard.children[i].widget.layout.width > 24) {
          tx = 0
        }
        let key =
          self.dashboard.children[i].widget.dataOptions &&
          self.dashboard.children[i].widget.dataOptions.staticKey
            ? self.dashboard.children[i].widget.dataOptions.staticKey
            : self.dashboard.children[i].widget.type
            ? self.dashboard.children[i].widget.type
            : ''
        let x = self.dashboard.children[i].widget.layout.x
          ? self.dashboard.children[i].widget.layout.x
          : 0
        let y = self.dashboard.children[i].widget.layout.y
          ? self.dashboard.children[i].widget.layout.y
          : 0
        let mx = self.dashboard.children[i].widget.mLayout.x
          ? self.dashboard.children[i].widget.mLayout.x
          : 0
        let my = self.dashboard.children[i].widget.mLayout.y
          ? self.dashboard.children[i].widget.mLayout.y
          : 0
        layout.push({
          i: self.dashboard.children[i].widget.id + '',
          x: self.isMobileDashboard ? mx : x,
          y: self.isMobileDashboard ? my : y,
          w: self.dashboard.children[i].widget.layout.width,
          h: self.dashboard.children[i].widget.layout.height,
          widget: self.dashboard.children[i].widget,
          minW: self.getMinW(
            self.dashboard.children[i].widget.type,
            self.dashboard.children[i].widget
          ),
          minH: self.getMinH(
            self.dashboard.children[i].widget.type,
            self.dashboard.children[i].widget
          ),
          mx: mx,
          my: my,
          wx: x,
          wy: y,
          mw: self.getMobileLayout(key).width,
          mh: self.getMobileLayout(key).height,
          ww: self.dashboard.children[i].widget.layout.width,
          wh: self.dashboard.children[i].widget.layout.height,
          mminW: 1,
          mminH: 1,
          wminW: self.getMinW(
            self.dashboard.children[i].widget.type,
            self.dashboard.children[i].widget
          ),
          wminH: self.getMinH(
            self.dashboard.children[i].widget.type,
            self.dashboard.children[i].widget
          ),
        })
        tx += self.dashboard.children[i].widget.layout.width
      }
      self.dashboardLayout = layout
      self.dashboardlength =
        self.dashboardLayout.length > 0 ? self.dashboardLayout.length : 0
    },
    getMinW(type, widget) {
      if (
        this.newDashboardData &&
        this.newDashboardData.widget &&
        this.newDashboardData.widget.dataOptions &&
        this.newDashboardData.widget.dataOptions.type &&
        this.newDashboardData.widget.dataOptions.type === 'boolean'
      ) {
        return 1
      }
      if (widget && widget.dataOptions && widget.dataOptions.staticKey) {
        if (widget.dataOptions.staticKey === 'textcard') {
          return 1
        }
        if (widget.dataOptions.staticKey === 'imagecard') {
          return 1
        }
        if (widget.dataOptions.staticKey === 'web') {
          return 1
        }
      }
      if (type) {
        let width = 6
        switch (type) {
          case 'chart':
            width = 6
            break
          case 'static':
            width = 6
            break
          case 'view':
            width = 10
            break
          case 'list':
            width = 10
            break
          case 'map':
            width = 14
            break
          case 'web':
            width = 8
            break
          default:
            width = 6
            break
        }
        return width
      } else {
        return 6
      }
    },
    getMinH(type, widget) {
      if (
        this.newDashboardData &&
        this.newDashboardData.widget &&
        this.newDashboardData.widget.dataOptions &&
        this.newDashboardData.widget.dataOptions.type &&
        this.newDashboardData.widget.dataOptions.type === 'boolean'
      ) {
        return 1
      }
      if (widget && widget.dataOptions && widget.dataOptions.staticKey) {
        if (widget.dataOptions.staticKey === 'textcard') {
          return 1
        }
        if (widget.dataOptions.staticKey === 'imagecard') {
          return 1
        }
        if (widget.dataOptions.staticKey === 'web') {
          return 1
        }
      }
      if (type) {
        let height = 6
        switch (type) {
          case 'chart':
            height = 6
            break
          case 'static':
            height = 8
            break
          case 'view':
            height = 8
            break
          case 'list':
            height = 8
            break
          case 'map':
            height = 8
            break
          case 'buildingcard':
            height = 6
            break
          case 'web':
            height = 8
            break
          default:
            height = 8
            break
        }
        return height
      } else {
        return 8
      }
    },
    saveEditDashboard() {
      let self = this
      if (self.supportedModules.length !== 0) {
        let data = {
          dashboard: {
            dashboardName: this.newdashboard.label,
            moduleId: this.supportedModules.find(
              row => row.name === this.getCurrentModule().module
            ).moduleId,
            dashboardFolderId:
              this.selectedDashoardFolder && this.selectedDashoardFolder.id
                ? this.selectedDashoardFolder.id
                : null,
          },
        }
        this.addDashboardFolder(data)
      }
    },
    savefolderandDashboard(data, mode, dashboardObj, dashboard) {
      let self = this
      let reportFolderObj = {
        name: this.dashbaordFolderName,
      }
      self.$http
        .post(
          'dashboard/addDashboardFolder?moduleName=' +
            self.getCurrentModule().module,
          {
            dashboardFolderContext: reportFolderObj,
          }
        )
        .then(function(response) {
          data.dashboard.dashboardFolderId =
            response.data.dashboardFolderContext.id
          if (mode === 'edit' && dashboardObj && dashboard) {
            dashboardObj.dashboardMeta.dashboardFolderId =
              response.data.dashboardFolderContext.id
            self.updateDashboardapi(dashboardObj, dashboard)
          } else {
            self.saveDashboard(data)
          }
        })
        .catch(function(error) {
          self.$message({
            message: 'Dashboard Folder not saved',
            type: 'error',
          })
          console.log(error)
        })
    },
    addDashboardFolder(data) {
      let dashboardname = []
      let self = this
      dashboardname.push(this.dashbaordFolderName)
      // if ((dashboardname[0].replace(/\s/g, '').length > 0) && this.dashboardFolderList.filter(rt => rt.name === self.dashbaordFolderName).length === 0) {
      //   delete data.dashboardFolderId
      //   this.selectedDashoardFolder = null
      // }
      if (
        this.selectedDashoardFolder === null &&
        dashboardname[0].replace(/\s/g, '').length > 0
      ) {
        if (data.dashboardFolderId) {
          delete data.dashboardFolderId
        }
        this.savefolderandDashboard(data)
      } else if (
        this.selectedDashoardFolder === null &&
        dashboardname[0].replace(/\s/g, '').length === 0
      ) {
        if (data.dashboardFolderId) {
          delete data.dashboardFolderId
        }
        this.saveDashboard(data)
      } else if (
        this.selectedDashoardFolder &&
        dashboardname[0].replace(/\s/g, '').length > 0 &&
        this.dashboardFolderList.filter(
          rt => rt.name === self.dashbaordFolderName
        ).length > 0
      ) {
        data.dashboard.dashboardFolderId = this.selectedDashoardFolder.id
        this.saveDashboard(data)
      }
    },
    loadSupportedModules() {
      let self = this
      let url = '/dashboard/supportedmodules'
      this.$http.get(url).then(function(response) {
        if (response.data.modules) {
          self.supportedModules = response.data.modules
          self.saveEditDashboard()
        }
      })
    },
    saveDashboard(data) {
      let self = this
      if (
        data.dashboard.dashboardName !== null &&
        data.dashboard.dashboardName !== null
      ) {
        self.$http.post('/dashboard/add', data).then(function(response) {
          self.$message({
            message: 'Dashboard created successfully!',
            type: 'success',
          })
          self.newdashboard.linkName = response.data.dashboard.linkName
          self.loadNewDb(response.data.dashboard)
        })
      } else {
        self.$message({
          message: 'Please Enter The Dashboard Name!',
          type: 'error',
        })
      }
    },
    loadNewDb(dashboard) {
      let self = this
      // self.prepareDashboardLayout()
      self.saveEdit(dashboard, 'load')
    },
    loadNewDashboard(link) {
      let self = this
      self.loading = true
      self.$http.get('/dashboard/' + link).then(function(response) {
        self.dashboard = response.data.dashboardJson[0]
        self.prepareDashboardLayout()
        self.saveEdit()
        self.loading = false
      })
    },
    updateLayout() {
      if (this.dashboardLayout.length) {
        let layout = []
        for (let i = 0; i < this.dashboardLayout.length; i++) {
          layout.push(
            this.setupLayout(this.$helpers.cloneObject(this.dashboardLayout[i]))
          )
        }
        this.dashboardLayout = layout
      }
    },
    saveEdit(dashboard, mode) {
      let self = this
      this.updateLayout()
      let dashboardWidgets = []
      let widgetData = null
      if (self.dashboardLayout !== null) {
        for (let i = 0; i < self.dashboardLayout.length; i++) {
          let gridItem = self.dashboardLayout[i]
          widgetData = {
            id: gridItem.widget.id,
            type: gridItem.widget.type,
            layoutWidth: gridItem.ww,
            layoutHeight: gridItem.wh,
            mLayoutHeight: gridItem.mh,
            mLayoutWidth: gridItem.mw,
            order: i + 1,
            xPosition: gridItem.wx,
            yPosition: gridItem.wy,
            mXPosition: gridItem.mx,
            mYPosition: gridItem.my,
            headerText: gridItem.widget.header.title,
            reportId: gridItem.widget.dataOptions.reportId,
            newReportId: gridItem.widget.dataOptions.newReportId,
            staticKey: gridItem.widget.dataOptions.staticKey
              ? gridItem.widget.dataOptions.staticKey
              : null,
            viewName: gridItem.widget.dataOptions.viewName
              ? gridItem.widget.dataOptions.viewName
              : null,
            moduleName: gridItem.widget.dataOptions.moduleName
              ? gridItem.widget.dataOptions.moduleName
              : null,
          }
          // widgetData = {
          //   id: gridItem.widget.id,
          //   type: gridItem.widget.type,
          //   layoutWidth: gridItem.w,
          //   layoutHeight: gridItem.h,
          //   order: (i + 1),
          //   xPosition: gridItem.x,
          //   yPosition: gridItem.y,
          //   headerText: gridItem.widget.header.title,
          //   reportId: gridItem.widget.dataOptions.reportId,
          //   newReportId: gridItem.widget.dataOptions.newReportId,
          //   staticKey: gridItem.widget.dataOptions.staticKey ? gridItem.widget.dataOptions.staticKey : null,
          //   viewName: gridItem.widget.dataOptions.viewName ? gridItem.widget.dataOptions.viewName : null,
          //   moduleName: gridItem.widget.dataOptions.moduleName ? gridItem.widget.dataOptions.moduleName : null
          // }
          if (gridItem.widget.dataOptions.building) {
            widgetData.baseSpaceId = gridItem.widget.dataOptions.building.id
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'readingcard' &&
            gridItem.widget.dataOptions.params
          ) {
            widgetData.metaJson = JSON.stringify(
              gridItem.widget.dataOptions.params.metaJson
            )
            widgetData.paramsJson =
              gridItem.widget.dataOptions.params.paramsJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'readingWithGraphCard' &&
            gridItem.widget.dataOptions.params
          ) {
            widgetData.metaJson = JSON.stringify(
              gridItem.widget.dataOptions.params.metaJson
            )
            widgetData.paramsJson =
              gridItem.widget.dataOptions.params.paramsJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'readingGaugeCard' &&
            gridItem.widget.dataOptions.params
          ) {
            widgetData.metaJson = JSON.stringify(
              gridItem.widget.dataOptions.params.metaJson
            )
            widgetData.paramsJson =
              gridItem.widget.dataOptions.params.paramsJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'textcard' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson + ''
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'imagecard' &&
            gridItem.widget.dataOptions.imagecardData
          ) {
            widgetData.paramsJson = {
              photoId: gridItem.widget.dataOptions.imagecardData.photoId,
              url: gridItem.widget.dataOptions.imagecardData.url,
            }
            widgetData.metaJson = JSON.stringify(
              gridItem.widget.dataOptions.imagecardData
            )
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'web' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'fahuStatusCard' &&
            gridItem.widget.dataOptions.AHUcard
          ) {
            widgetData.paramsJson = {
              parentId: gridItem.widget.dataOptions.AHUcard.id,
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'fahuStatusCard1' &&
            gridItem.widget.dataOptions.AHUcard
          ) {
            widgetData.paramsJson = {
              parentId: gridItem.widget.dataOptions.AHUcard.id,
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'fahuStatusCard2' &&
            gridItem.widget.dataOptions.AHUcard
          ) {
            widgetData.paramsJson = {
              parentId: gridItem.widget.dataOptions.AHUcard.id,
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'fahuStatusCard3' &&
            gridItem.widget.dataOptions.AHUcard
          ) {
            widgetData.paramsJson = {
              parentId: gridItem.widget.dataOptions.AHUcard.id,
            }
          }
          dashboardWidgets.push(widgetData)
        }
      }
      let dashboardObj = {}
      if (self.mode === 'new') {
        dashboardObj = {
          dashboardMeta: {
            id: dashboard.id,
            dashboardName: self.newdashboard.label,
            dashboardWidgets: dashboardWidgets,
            dashboardFolderId: dashboard.dashboardFolderId
              ? dashboard.dashboardFolderId
              : null,
          },
        }
        if (self.selectedDashoardFolder) {
          dashboardObj.dashboardMeta.dashboardFolderId =
            self.selectedDashoardFolder && self.selectedDashoardFolder.id
              ? self.selectedDashoardFolder.id
              : null
        }
      } else {
        dashboardObj = {
          dashboardMeta: {
            id: self.dashboard.id,
            dashboardName: self.dashboard.label,
            linkName: self.dashboardLink,
            dashboardWidgets: dashboardWidgets,
            dashboardFolderId: dashboard.dashboardFolderId
              ? dashboard.dashboardFolderId
              : null,
          },
        }
        if (self.selectedDashoardFolder) {
          dashboardObj.dashboardMeta.dashboardFolderId =
            self.selectedDashoardFolder && self.selectedDashoardFolder.id
              ? self.selectedDashoardFolder.id
              : null
        }
      }
      if (
        self.$route.params.buildingid &&
        this.dashboardLink === 'sitedashboard'
      ) {
        dashboardObj.siteId = self.$route.params.buildingid
      } else if (
        self.$route.params.buildingid &&
        this.dashboardLink === 'buildingdashboard'
      ) {
        dashboardObj.buildingId = self.$route.params.buildingid
      }
      if (
        dashboardObj.dashboardMeta.dashboardName !== null &&
        dashboardObj.dashboardMeta.dashboardName !== ''
      ) {
        if (mode !== 'load') {
          let dashboardname = []
          dashboardname.push(self.dashbaordFolderName)
          if (
            self.dashbaordFolderName.replace(/\s/g, '').length > 0 &&
            self.supportedModules.length &&
            self.dashboardFolderList.filter(
              rt => rt.name === self.dashbaordFolderName
            ).length === 0
          ) {
            // self.selectedDashoardFolder = null
            let data = {
              dashboard: {
                dashboardName:
                  self.dashboard && self.dashboard.label
                    ? self.dashboard.label
                    : null,
                dashboardFolderId:
                  self.selectedDashoardFolder && self.selectedDashoardFolder.id
                    ? self.selectedDashoardFolder.id
                    : null,
              },
            }
            if (
              self.supportedModules.find(
                row => row.value === self.getCurrentModule().module
              )
            ) {
              data.dashboard.moduleId = self.supportedModules.find(
                row => row.value === self.getCurrentModule().module.moduleId
              )
            }
            self.savefolderandDashboard(data, 'edit', dashboardObj, dashboard)
          } else if (
            self.dashbaordFolderName.replace(/\s/g, '').length > 0 &&
            self.supportedModules.length &&
            self.dashboardFolderList.find(
              rt => rt.name === self.dashbaordFolderName
            )
          ) {
            self.updateDashboardapi(dashboardObj, dashboard)
          } else if (dashboardname[0].replace(/\s/g, '').length === 0) {
            self.selectedDashoardFolder = null
            delete dashboardObj.dashboardMeta.dashboardFolderId
            self.updateDashboardapi(dashboardObj, dashboard)
          }
        } else {
          self.updateDashboardapi(dashboardObj, dashboard)
        }
      } else {
        self.$message({
          message: 'Please Enter The Dashboard Name!',
          type: 'error',
        })
      }
    },
    updateDashboardapi(dashboardObj, dashboard) {
      let self = this
      console.log('************', dashboardObj, dashboard)
      if (
        dashboardObj.dashboardMeta &&
        dashboardObj.dashboardMeta.dashboardWidgets
      ) {
        let data = []
        data = dashboardObj.dashboardMeta.dashboardWidgets.filter(
          rt => rt.layoutHeight !== 0
        )
        dashboardObj.dashboardMeta.dashboardWidgets = data
        console.log('****************', data)
      }
      self.$http
        .post('/dashboard/update', dashboardObj)
        .then(function(response) {
          self.$message({
            message: 'Dashboard updated successfully!',
            type: 'success',
          })
          self.$router.push(self.constractUrl(dashboard))
        })
    },
    EnableMobileDashboard(dashboardObj, dashboard) {
      let self = this
      let params = {
        linkName: this.dashboardLink,
        moduleName: this.getCurrentModule().module,
      }
      if (this.buildingId) {
        params.buildingid = this.buildingId
      }
      self.$http
        .post('/dashboard/toggleMobileDashboard', params)
        .then(function(response) {
          if (self.isMobileDashboard) {
            self.$message({
              message: 'Mobile dashboard enabled successfully!',
              type: 'success',
            })
          } else {
            self.$message({
              message: 'Mobile dashboard disabled successfully!',
              type: 'success',
            })
          }
          self.$message({
            message: 'Mobile dashboard disabled successfully!',
            type: 'success',
          })
        })
    },
    constractUrl() {
      let module = this.getCurrentModule().module
      let link =
        this.mode === 'new' ? this.newdashboard.linkName : this.dashboardLink
      if (link === 'buildingdashboard') {
        link = link + '/' + this.$router.currentRoute.params.buildingid
      }
      if (module === 'workorder') {
        return '/app/wo/dashboard/' + link
      } else if (module === 'alarm') {
        return '/app/fa/dashboard/' + link
      } else if (module === 'energydata') {
        return '/app/em/newdashboard/' + link
      } else {
        return '/app/wo/newdashboard/' + link
      }
    },
    cancelEdit() {
      location.reload()
      this.$router.push(this.$route.path)
    },
    cancel() {
      this.$router.push(this.constractUrl())
    },
    handleAddWidget(widgetType) {
      if (widgetType === 'chart') {
        this.$refs.newChartWidget.open()
      } else if (widgetType === 'list') {
        this.$refs.newListWidget.open()
      } else if (widgetType === 'map') {
        this.$refs.newMapWidget.open()
      } else if (widgetType === 'static') {
        this.$refs.newStaticWidget.open()
      } else if (widgetType === 'web') {
        this.$refs.newWebWidget.open()
      }
    },
    deleteChart(index) {
      let self = this
      self.dashboardLayout[index].x = 0
      self.dashboardLayout[index].y = 0
      self.dashboardLayout[index].w = 0
      self.dashboardLayout[index].h = 0
      self.dashboardLayout[index].minH = 0
      self.dashboardLayout[index].minW = 0
      self.dashboardLayout[index].mw = 0
      self.dashboardLayout[index].mh = 0
      self.dashboardLayout[index].mminH = 0
      self.dashboardLayout[index].mminW = 0
      self.dashboardLayout[index].mx = 0
      self.dashboardLayout[index].my = 0
      self.dashboardLayout[index].ww = 0
      self.dashboardLayout[index].wh = 0
      self.dashboardLayout[index].wminH = 0
      self.dashboardLayout[index].wminW = 0
      self.dashboardLayout[index].wx = 0
      self.dashboardLayout[index].wy = 0
      self.dashboardLayout[index].deleted = true
    },
    openFirstReport() {
      let self = this
      if (
        !self.$route.params.reportid &&
        !this.$route.path.includes('scheduled')
      ) {
        if (self.reportTree.length && self.reportTree[0].children.length) {
          self.reportTree[0].expand = true
          // let reportLink = self.getReportLink(self.reportTree[0].children[0].widget.dataOptions.reportId)
          // self.$router.push({path: reportLink})
        }
      }
    },
    expand(folder) {
      folder.expand = !folder.expand
    },
    getReportLink(reportId) {
      return this.getCurrentModule().rootPath + '/view/' + reportId
    },
    handleNewCommand(cmd) {
      if (cmd === 'reportfolder') {
        let self = this
        let promptObj = {
          title: 'New Report Folder',
          promptPlaceholder: 'Folder name',
          rbLabel: 'Save',
        }
        self.$dialog.prompt(promptObj).then(function(value) {
          if (value !== null) {
            let reportFolderObj = {
              name: value,
            }

            self.$http
              .post(
                'dashboard/addReportFolder?moduleName=' +
                  self.getCurrentModule().module,
                {
                  reportFolderContext: reportFolderObj,
                }
              )
              .then(function(response) {
                if (response.data.reportFolderContext) {
                  self.$message({
                    message: 'Folder created successfully.',
                    type: 'success',
                  })
                  self.reportTree.push(response.data.reportFolderContext)
                } else {
                  self.$message({
                    message: 'Folder creation failed.',
                    type: 'warning',
                  })
                }
              })
          }
        })
      } else if (cmd === 'tabular') {
        this.$router.replace({
          path: this.getCurrentModule().rootPath + '/newtabular',
        })
      } else if (cmd === 'matrix') {
        this.$router.replace({
          path: this.getCurrentModule().rootPath + '/newmatrix',
        })
      } else {
        this.$router.replace({
          path: this.getCurrentModule().rootPath + '/new',
        })
      }
    },
    dashbaordScreen() {
      let stage = window.localStorage.getItem('dashboard')
      if (stage !== null) {
        if (stage === 'true') {
          this.collapseSidebar = true
        } else {
          this.collapseSidebar = false
        }
      } else {
        this.collapseSidebar = false
      }
    },
    toggle() {
      let stage = !this.collapseSidebar
      this.collapseSidebar = stage
      window.localStorage.setItem('dashboard', stage)
    },
    drag(e) {},
    allowDrop(e) {
      e.preventDefault()
    },
    start(data, parent) {
      this.selectedBuilding = null
      if (parent) {
        data.moduleName = parent
      }
      this.newDashboardData = data
      this.isDraging = true
    },
    dragEnter(e) {},
    drop(e) {
      this.emptywidget = true
      if (
        this.newDashboardData.key === 'energycostmini' ||
        this.newDashboardData.key === 'profilemini' ||
        this.newDashboardData.key === 'carbonmini' ||
        this.newDashboardData.key === 'weathermini'
      ) {
        this.addDataPopUp()
      } else if (this.newDashboardData.key === 'readingcard') {
        this.addReadingPopUp()
      } else if (this.newDashboardData.key === 'imagecard') {
        this.showImageCardPopUp()
      } else if (this.newDashboardData.key === 'web') {
        this.webcardVisible = true
      } else if (this.newDashboardData.key === 'fahuStatusCard') {
        this.ahupopup = true
      } else if (this.newDashboardData.key === 'fahuStatusCard1') {
        this.ahupopup = true
      } else if (this.newDashboardData.key === 'fahuStatusCard3') {
        this.ahupopup = true
      } else {
        this.addNewelemnt()
      }
    },
    avatarCropUploadSuccess(jsonData, field) {
      this.imagecardData = null
      console.log(jsonData)
      this.imagecardData = jsonData
      this.addNewelemnt()
      this.isDraging = false
      this.showAvatarUpload = false
    },
    avatarCropSuccess(avatarDataUrl, field) {
      this.avatarDataUrl = null
      this.avatarDataUrl = avatarDataUrl
    },
    avatarCropUploadFail(status, field) {
      this.isDraging = false
      this.showAvatarUpload = false
      this.avatarDataUrl = null
    },
    addreadings() {
      if (this.readingPopupValidation().valid) {
        this.addNewelemnt()
        this.isDraging = false
        this.readingpopup = false
      } else {
        this.$message({
          message: this.readingPopupValidation().message,
          type: 'error',
        })
      }
    },
    addAHUData() {
      this.addNewelemnt()
      this.isDraging = false
      this.ahupopup = false
    },
    readingPopupValidation() {
      let data = this.readingCard
      let valid = true
      let message = ''
      if (data.operatorId === null || data.operatorId === -1) {
        valid = false
        message = 'please select date range'
      }
      if (data.readingName === null || data.readingName === '') {
        valid = false
        message = 'please select reading'
      }
      if (data.title === null || data.title === '') {
        valid = false
        message = 'please select title'
      }
      if (this.readingCard.mode === 4 && this.readingCard.targetreading) {
        let target = this.readingCard.targetreading
        if (target.targetmode === 'reading') {
          if (target.readingName === null || target.readingName === '') {
            valid = false
            message = 'please select target reading'
          }
        } else if (target.targetmode === 'constant') {
          if (
            isNaN(Number(target.count)) ||
            Number(target.count) === Infinity
          ) {
            valid = false
            message = 'please enter the valid target value'
          }
        }
        if (target.maxmode !== 'usetarget') {
          if (isNaN(Number(target.maxValue)) && target.maxmode === 'constant') {
            valid = false
            message = 'please enter the valid max value'
          } else if (
            isNaN(Number(target.maxpercentage)) &&
            target.maxmode === 'percentage'
          ) {
            valid = false
            message = 'please enter the valid max percentage'
          }
        }
      }
      return {
        valid: valid,
        message: message,
      }
    },
    selectBuildings() {
      if (this.selectedBuilding) {
        this.addNewelemnt(false, this.selectedBuilding)
        this.dialogVisible = false
        this.isDraging = false
      } else {
        this.$message({
          message: 'Please select building',
          type: 'error',
        })
      }
    },
    selectwebcard() {
      if (
        this.webUrl.indexOf('http') === -1 ||
        this.webUrl.indexOf('http') === -1
      ) {
        this.webUrl = 'https://' + this.webUrl
      }
      if (this.webcardValidation()) {
        this.addNewelemnt()
        this.webcardVisible = false
        this.isDraging = false
      } else {
        this.$message({
          message: 'Please provide a valid url',
          type: 'error',
        })
      }
    },
    webcardValidation() {
      if (this.webUrl) {
        if (!this.webUrl.length) {
          return false
        } else {
          return true
        }
      } else {
        return false
      }
    },
    cancelBuilding() {
      this.dialogVisible = false
      this.isDraging = false
      this.readingpopup = false
      this.showAvatarUpload = false
      this.ahupopup = false
      this.webcardVisible = false
    },
    addDataPopUp() {
      this.dialogVisible = true
    },
    addReadingPopUp() {
      this.readingpopup = true
    },
    showImageCardPopUp() {
      this.showAvatarUpload = true
    },
    getDatapoint(data) {
      if (data) {
        this.addreadingVisible2 = false
        this.readingCard.readings = data
        this.readingCard.readingName = data.name
        this.readingCard.parentId =
          data.type === 'space'
            ? data.space && data.space.id
              ? data.space.id
              : -1
            : data.asset && data.asset.id
            ? data.asset.id
            : -1
        this.readingCard.title = this.readingCard.title.length
          ? this.readingCard.title
          : this.readingCard.readingName
        this.readingCard.parentName1 = this.readingCard.readingName || ''
      }
    },
    getTragetDatapoint(data) {
      if (data) {
        this.addreadingVisible3 = false
        this.readingCard.targetreading.readings = data
        this.readingCard.targetreading.readingName = data.name
        this.readingCard.targetreading.parentId =
          data.type === 'space'
            ? data.space && data.space.id
              ? data.space.id
              : -1
            : data.asset && data.asset.id
            ? data.asset.id
            : -1
        // this.readingCard.title = this.readingCard.title.length ? ((this.readingCard.title === this.readingCard.readingName) ? (this.readingCard.readingName + ' vs ' + this.readingCard.targetreading.readingName) : this.readingCard.title) : (this.readingCard.readingName + 'vs' + this.readingCard.targetreading.readingName)
        this.readingCard.targetreading.parentName2 =
          this.readingCard.targetreading &&
          this.readingCard.targetreading.readingName
            ? this.readingCard.targetreading.readingName
            : ''
      }
    },
    cancelDataPointAdder(data) {
      this.addreadingVisible2 = false
      this.addreadingVisible3 = false
    },
    containerover(e) {
      this.emptywidget = true
    },
    dropContainer(e) {
      this.emptywidget = true
    },
    dragend() {
      if (
        !this.dialogVisible &&
        !this.readingpopup &&
        !this.showAvatarUpload &&
        !this.ahupopup &&
        !this.webcardVisible
      ) {
        this.isDraging = false
      }
    },
    triggerEvent(element, eventName) {
      if ('createEvent' in document) {
        let evt = document.createEvent('HTMLEvents')
        evt.initEvent(eventName, false, true)
        element.dispatchEvent(evt)
      } else {
        element.fireEvent('on' + eventName)
      }
    },
    containerEntry(e) {
      // let elemnt = document.getElementsByClassName('fc-widget-header')[0]
    },
    getMobileLayout(key) {
      let mobileLayoutMapper = {
        readingcard: { height: 8, width: 12 },
        fahuStatusCard: { height: 8, width: 12 },
        fahuStatusCard1: { height: 8, width: 12 },
        fahuStatusCard2: { height: 8, width: 12 },
        fahuStatusCard3: { height: 8, width: 12 },
        readingWithGraphCard: { height: 8, width: 12 },
        textcard: { height: 8, width: 12 },
        readingGaugeCard: { height: 8, width: 12 },
        imagecard: { height: 8, width: 12 },
        weathermini: { height: 6, width: 12 },
        energycostmini: { height: 6, width: 12 },
        profilemini: { height: 6, width: 24 },
        carbonmini: { height: 6, width: 12 },
        web: { height: 12, width: 24 },
        view: { height: 12, width: 24 },
        chart: { height: 16, width: 24 },
      }
      return mobileLayoutMapper[key]
        ? mobileLayoutMapper[key]
        : { height: 16, width: 24 }
    },
    addNewelemnt(mode, id) {
      this.dashboardlength = this.dashboardlength + 1
      let data = this.newDashboardData
      let layout = {}
      if (this.option === 'view') {
        layout = {
          i: this.dashboardlength + '',
          x: 0,
          y: 0,
          mx: 0,
          my: 0,
          wx: 0,
          wy: 0,
          w: this.getMinW('view'),
          h: this.getMinH('view'),
          ww: this.getMinW('view'),
          wh: this.getMinH('view'),
          mw: this.getMobileLayout(this.option).width,
          mh: this.getMobileLayout(this.option).height,
          widget: {
            layout: {
              width: this.getMinW('view'),
              x: 0,
              y: Infinity,
              position: this.dashboardlength,
              height: this.getMinH('view'),
            },
            header: {
              subtitle: 'today',
              title: data.displayName,
              export: false,
            },
            dataOptions: {
              dataurl: '',
              refresh_interval: 100,
              viewName: data.name,
              name: 'dummy',
              moduleName: data.moduleName,
            },
            type: 'view',
            id: -1,
          },
          label: data.displayName,
          minW: this.getMinW('view'),
          minH: this.getMinH('view'),
          wminW: this.getMinW('view'),
          wminH: this.getMinH('view'),
          mminW: this.getMobileLayout(this.option).width,
          mminH: this.getMobileLayout(this.option).height,
        }
      } else if (this.option === 'static') {
        layout = {
          i: this.dashboardlength + '',
          x: 0,
          y: 0,
          mx: 0,
          my: 0,
          wx: 0,
          wy: 0,
          w: data.w,
          h: data.h,
          ww: data.w,
          wh: data.h,
          mw: this.getMobileLayout(data.key).width,
          mh: this.getMobileLayout(data.key).height,
          widget: {
            layout: {
              width: data.w,
              x: 0,
              y: Infinity,
              position: this.dashboardlength,
              height: data.h,
            },
            header: {
              subtitle: 'today',
              title: this.getTitle(data),
              export: false,
            },
            dataOptions: {
              dataurl: '',
              refresh_interval: 100,
              name: 'dummy',
              staticKey: data.key,
            },
            type: 'static',
            id: -1,
            name: '',
          },
          minW: data.w,
          minH: data.h,
          wminW: data.w,
          wminH: data.h,
          mminW: this.getMobileLayout(data.key).width,
          mminH: this.getMobileLayout(data.key).height,
        }
      } else if (this.option === 'cards') {
        layout = {
          i: this.dashboardlength + '',
          x: 0,
          y: 0,
          mx: 0,
          my: 0,
          wx: 0,
          wy: 0,
          w: data.w,
          h: data.h,
          wh: data.h,
          ww: data.w,
          mw: this.getMobileLayout(data.key).width,
          mh: this.getMobileLayout(data.key).height,
          widget: {
            layout: {
              width: data.w,
              x: 0,
              y: Infinity,
              position: this.dashboardlength,
              height: data.h,
            },
            header: {
              subtitle: 'today',
              title: '',
              export: false,
            },
            dataOptions: {
              dataurl: '',
              refresh_interval: 100,
              name: 'dummy',
              staticKey: data.key,
            },
            type: 'static',
            id: -1,
            name: '',
          },
          minW: data.w,
          minH: data.h,
          wminW: data.w,
          wminH: data.h,
          mminW: this.getMobileLayout(data.key).width,
          mminH: this.getMobileLayout(data.key).height,
        }
        if (this.readingCard.mode === 2) {
          layout.h = 2
          layout.widget.height = 2
          layout.minH = 2
          layout.mh = 8
          layout.mminH = 8
          layout.wh = 2
          layout.wminH = 2
        }
        if (this.readingCard.mode === 3) {
          layout.h = 4
          layout.widget.height = 4
          layout.minH = 4
          layout.mh = 8
          layout.mminH = 8
          layout.wh = 4
          layout.wminH = 4
        }
        if (this.readingCard.mode === 4) {
          layout.h = 4
          layout.widget.height = 4
          layout.minH = 4
          layout.mh = 8
          layout.mminH = 8
          layout.wh = 4
          layout.wminH = 4
          layout.widget.dataOptions.staticKey = 'readingGaugeCard'
        }
        if (layout.widget.dataOptions.staticKey === 'imagecard') {
          layout.minH = 1
          layout.minW = 1
          layout.wminH = 1
          layout.mminW = 1
          layout.mminH = 1
          layout.wminW = 1
        }
        if (layout.widget.dataOptions.staticKey === 'textcard') {
          layout.minH = 1
          layout.minW = 1
          layout.wminH = 1
          layout.wminW = 1
          layout.mminH = 1
          layout.mminW = 1
        }
        if (layout.widget.dataOptions.staticKey === 'web') {
          layout.minH = 1
          layout.minW = 1
          layout.wminH = 1
          layout.wminW = 1
          layout.mminH = 1
          layout.mminW = 1
        }
        if (
          this.AHUcard.catogry === 'FAHU' &&
          layout.widget.dataOptions.staticKey === 'fahuStatusCard'
        ) {
          layout.widget.dataOptions.staticKey = 'fahuStatusCard2'
        }
      } else if (this.option === 'web') {
        layout = {
          i: this.dashboardlength + '',
          x: 0,
          y: 0,
          mx: 0,
          my: 0,
          wx: 0,
          wy: 0,
          w: this.getMinW('web'),
          h: this.getMinH('web'),
          wh: this.getMinH('web'),
          ww: this.getMinW('web'),
          mw: this.getMobileLayout('web').width,
          mh: this.getMobileLayout('web').height,
          widget: {
            layout: {
              width: this.getMinW('web'),
              x: 0,
              y: Infinity,
              position: this.dashboardlength,
              height: this.getMinH('web'),
            },
            header: {
              subtitle: 'today',
              title: data.name,
              export: false,
            },
            dataOptions: {
              dataurl: '',
              refresh_interval: 100,
              webUrl: data.url,
              name: 'dummy',
            },
            type: 'web',
            id: -1,
          },
          minW: this.getMinW('web'),
          minH: this.getMinH('web'),
          wminW: this.getMinW('web'),
          wminH: this.getMinH('web'),
          mminW: this.getMobileLayout(this.option).width,
          mminH: this.getMobileLayout(this.option).height,
          label: data.label,
        }
      } else {
        if (data.widget) {
          layout = {
            i: this.dashboardlength + '',
            x: 0,
            y: 0,
            mx: 0,
            my: 0,
            wx: 0,
            wy: 0,
            w: this.getMinW(data.widget.type),
            h: this.getMinH(data.widget.type),
            ww: this.getMinW(data.widget.type),
            wh: this.getMinH(data.widget.type),
            mw: this.getMobileLayout('').width,
            mh: this.getMobileLayout('').height,
            widget: {
              layout: {
                width: this.getMinW(data.widget.type),
                x: 0,
                y: Infinity,
                position: this.dashboardlength,
                height: this.getMinW(data.widget.type),
              },
              header: data.widget.header,
              dataOptions: data.widget.dataOptions,
              id: -1,
              reportId: data.widget.dataOptions.reportId,
              type: data.widget.type,
              name: '',
            },
            minW: this.getMinW(data.widget.type),
            minH: this.getMinH(data.widget.type),
            wminW: this.getMinW(data.widget.type),
            wminH: this.getMinH(data.widget.type),
            mminW: this.getMobileLayout(this.option).width,
            mminH: this.getMobileLayout(this.option).height,
            moved: false,
          }
        } else {
          console.log('############ data: ', data)
          let ctype = 'line'
          layout = {
            i: this.dashboardlength + '',
            x: 0,
            y: 0,
            mx: 0,
            my: 0,
            wx: 0,
            wy: 0,
            w: this.getMinW(ctype),
            h: this.getMinH(ctype),
            ww: this.getMinW(ctype),
            wh: this.getMinH(ctype),
            mw: this.getMobileLayout('').width,
            mh: this.getMobileLayout('').height,
            widget: {
              layout: {
                width: this.getMinW(ctype),
                x: 0,
                y: Infinity,
                position: this.dashboardlength,
                height: this.getMinW(ctype),
              },
              header: {
                title: data.name,
              },
              dataOptions: {
                newReportId: data.id,
              },
              id: -1,
              newReportId: data.id,
              type: 'chart',
              name: '',
            },
            minW: this.getMinW(ctype),
            minH: this.getMinH(ctype),
            wminW: this.getMinW(ctype),
            wminH: this.getMinH(ctype),
            mminW: this.getMobileLayout('').width,
            mminH: this.getMobileLayout('').height,
            moved: false,
          }
        }
      }
      if (this.selectedBuilding) {
        layout.widget.dataOptions.building = this.buildings.find(
          rt => rt.id === this.selectedBuilding
        )
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'readingcard'
      ) {
        layout.widget.dataOptions.reading = this.readingCard
        this.readingCard = {}
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'readingGaugeCard'
      ) {
        layout.widget.dataOptions.reading = this.readingCard
        this.readingCard = {}
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'imagecard'
      ) {
        layout.widget.dataOptions.imagecardDataUrl = this.avatarDataUrl
        layout.widget.dataOptions.imagecardData = this.imagecardData
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'web'
      ) {
        layout.widget.dataOptions.metaJson = this.webUrl
        this.webUrl = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'fahuStatusCard'
      ) {
        layout.widget.dataOptions.AHUcard = this.AHUcard
        this.AHUcard = {
          catogry: '',
          id: null,
        }
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'fahuStatusCard1'
      ) {
        layout.widget.dataOptions.AHUcard = this.AHUcard
        this.AHUcard = {
          catogry: '',
          id: null,
        }
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'fahuStatusCard2'
      ) {
        layout.widget.dataOptions.AHUcard = this.AHUcard
        this.AHUcard = {
          catogry: '',
          id: null,
        }
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'fahuStatusCard3'
      ) {
        layout.widget.dataOptions.AHUcard = this.AHUcard
        this.AHUcard = {
          catogry: '',
          id: null,
        }
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'buildingmapwidget'
      ) {
        this.isDraging = false
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'mapwidget'
      ) {
        this.isDraging = false
      }
      if (!mode) {
        this.prepareDeviceLayout(layout)
        this.convertedlayout = layout
      }
      this.readingCard = {
        title: '',
        mode: 1,
        parentId: -1,
        legend: 'sum',
        parentName1: '',
        targetreading: {
          max: 0,
          maxmode: 'usetarget',
          readingName: '',
          targetmode: 'constant',
          count: 0,
          maxValue: 0,
          maxPercentage: 0,
          needMax: false,
          parentName2: '',
        },
        aggregationFunc: 3,
        readingName: '',
        operatorId: 28,
        layout: {
          color: '',
          fontColor: '',
        },
      }
    },
    getTitle(data) {
      let key = data.key
      if (key === 'profilecard') {
        return ''
      } else if (key === 'energycard') {
        return ''
      } else if (key === 'energycost') {
        return ''
      } else if (key === 'weathercard') {
        return ''
      } else if (key === 'energycostaltayer') {
        return ''
      } else if (key === 'weathercardaltayer') {
        return ''
      } else if (key === 'mapwidget') {
        return ''
      } else if (key === 'buildingmapwidget') {
        return ''
      } else if (key === 'weathermini') {
        return ''
      } else if (key === 'carbonmini') {
        return ''
      } else if (key === 'energycostmini') {
        return ''
      } else if (key === 'profilemini') {
        return ''
      } else if (key === 'readingcard') {
        return ''
      } else {
        return data.label
      }
    },
    prepareDeviceLayout(layout) {
      layout = this.convertLayout(layout)
      this.dashboardLayout.push(layout)
    },
    getDragIgnoreClass(widget) {
      if (
        widget &&
        widget.dataOptions &&
        widget.dataOptions.staticKey === 'mapwidget'
      ) {
        return ''
      } else if (
        widget &&
        widget.dataOptions &&
        widget.dataOptions.staticKey === 'buildingmapwidget'
      ) {
        return ''
      } else {
        return ''
      }
    },
    getCardsList(widgets) {
      let self = this
      if (widgets) {
        if (self.getCurrentModule().module === 'workorder') {
          let data = []
          for (let i = 0; i < widgets.length; i++) {
            data.push(widgets[i])
          }
          return data
        } else {
          return widgets
        }
      }
    },
    getStaticWidgetList(widgets) {
      let self = this
      if (widgets) {
        if (this.$account.org.id !== 108 && this.$account.org.id !== 113) {
          widgets = widgets.filter(widget => widget.key !== 'sampleppm')
        }
        if (self.getCurrentModule().module === 'workorder') {
          let data = []
          for (let i = 0; i < widgets.length; i++) {
            if (
              widgets[i].key === 'profilecard' ||
              widgets[i].key === 'energycard' ||
              widgets[i].key === 'energycost' ||
              widgets[i].key === 'weathercard'
            ) {
              continue
            } else {
              data.push(widgets[i])
            }
          }
          return data
        } else {
          return widgets
        }
      }
    },
    querySearch(queryString, cb) {
      let dashboardFolderList = this.dashboardFolderList
      let results = queryString
        ? dashboardFolderList.filter(this.createFilter(queryString))
        : dashboardFolderList
      // call callback function to return suggestion objects
      cb(results)
    },
    createFilter(queryString) {
      return link => {
        return link.name.toLowerCase().indexOf(queryString.toLowerCase()) === 0
      }
    },
    loadFolder(source) {
      let self = this
      let moduleName = this.getCurrentModule().module
      if (moduleName === 'energy') {
        moduleName = 'energyData'
      }
      self.$http
        .get('/dashboard/getDashboardFolder?moduleName=' + moduleName)
        .then(function(response) {
          self.dashboardFolderList = response.data.dashboardFolders
          if (source === 'watch') {
            self.loadDashboard()
          }
        })
    },
    handleSelect(item) {
      this.dashbaordFolderName = item.name
      this.selectedDashoardFolder = item
      console.log(item)
    },
    handleIconClick(ev) {
      console.log(ev)
    },
    getSourceFile(name, type, size) {
      console.log('************ get source file', name, type, size)
    },
    getInput(data) {
      this.cancelBuilding()
    },
  },
}
</script>

<style>
.dashboard-container {
  height: 100%;
  overflow-y: scroll;
  width: 100%;
  padding-bottom: 70px;
}

.vue-grid-item {
  padding: 0px !important;
}

.sk-cube-grid {
  width: 40px;
  height: 40px;
  margin: 200px auto;
}

.sk-cube-grid .sk-cube {
  width: 33%;
  height: 33%;
  float: left;
  -webkit-animation: sk-cubeGridScaleDelay 1.3s infinite ease-in-out;
  animation: sk-cubeGridScaleDelay 1.3s infinite ease-in-out;
}

.sk-cube-grid .sk-cube1 {
  background-color: #2f2e49;
  -webkit-animation-delay: 0.2s;
  animation-delay: 0.2s;
}

.sk-cube-grid .sk-cube2 {
  background-color: #fd4b92;
  -webkit-animation-delay: 0.3s;
  animation-delay: 0.3s;
}

.sk-cube-grid .sk-cube3 {
  background-color: #2f2e49;
  -webkit-animation-delay: 0.4s;
  animation-delay: 0.4s;
}

.sk-cube-grid .sk-cube4 {
  background-color: #fd4b92;
  -webkit-animation-delay: 0.1s;
  animation-delay: 0.1s;
}

.sk-cube-grid .sk-cube5 {
  background-color: #2f2e49;
  -webkit-animation-delay: 0.2s;
  animation-delay: 0.2s;
}

.sk-cube-grid .sk-cube6 {
  background-color: #fd4b92;
  -webkit-animation-delay: 0.3s;
  animation-delay: 0.3s;
}

.sk-cube-grid .sk-cube7 {
  background-color: #2f2e49;
  -webkit-animation-delay: 0s;
  animation-delay: 0s;
}

.sk-cube-grid .sk-cube8 {
  background-color: #fd4b92;
  -webkit-animation-delay: 0.1s;
  animation-delay: 0.1s;
}

.sk-cube-grid .sk-cube9 {
  background-color: #2f2e49;
  -webkit-animation-delay: 0.2s;
  animation-delay: 0.2s;
}

@-webkit-keyframes sk-cubeGridScaleDelay {
  0%,
  70%,
  100% {
    -webkit-transform: scale3D(1, 1, 1);
    transform: scale3D(1, 1, 1);
  }
  35% {
    -webkit-transform: scale3D(0, 0, 1);
    transform: scale3D(0, 0, 1);
  }
}

@keyframes sk-cubeGridScaleDelay {
  0%,
  70%,
  100% {
    -webkit-transform: scale3D(1, 1, 1);
    transform: scale3D(1, 1, 1);
  }
  35% {
    -webkit-transform: scale3D(0, 0, 1);
    transform: scale3D(0, 0, 1);
  }
}

.dashboard-name-input {
  width: 300px;
  font-size: 18px;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000;
}

.dashboard-container.editmode .fc-widget-header,
.dashboard-container.editmode .dragabale-card,
.ql-toolbar {
  cursor: move;
}

.dashboard-container .fchart-section {
  padding-left: 0px !important;
  padding-right: 0px !important;
}

.dashboard-container .baselineoption span {
  display: none;
}

.dashboard-container .fc-report-filter .header-content,
.dashboard-container .fc-report-building,
.dashboard-container .fc-underline,
.dashboard-container .fc-report-building {
  display: none;
}

.dashboard-container .fc-report-filter .fc-report-building {
  display: none !important;
}

.dashboard-container .fc-report-pop-btn-row,
.dashboard-container .compare-row {
  display: none !important;
}

.dashboard-container .chart-option .c-option-1,
.dashboard-container .chart-option .c-option-2,
.dashboard-container .chart-option .c-diff {
  padding-top: 10px;
  padding-bottom: 10px;
}

.dashboard-container .chart-option {
  border: none;
  margin: 0px;
  padding-left: 2%;
  padding-right: 15px;
  white-space: nowrap;
  position: absolute;
  bottom: 0;
}

/* dashboard new css */

.dashboard-container .energy_background {
  background-image: linear-gradient(to left, #ec637f, #843f78);
}

.dashboard-container .energy-cost {
  background-image: linear-gradient(to left, #7039a9, #4a2973);
  color: #fff;
  height: 100%;
}

.fc-black-theme .dashboard-container .fc-widget {
  border: 0 !important;
  box-shadow: none;
}

.fc-black-theme .dashboard-container .fc-widget-label {
  color: #fff;
}

.fc-black-theme rect.tile_empty {
  fill: #7976764a !important;
}

.fc-black-theme .heatMap line,
.fc-black-theme .heatMap .y.axis path {
  stroke: #393b59 !important;
}

.dashboard-container .fc-widget-label {
  font-size: 1.1em;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: #2f2e49;
  padding-bottom: 0px;
}

.dashboard-container .primaryfill-color {
  fill: #fff !important;
}

.dashboard-container .carbon-weather {
  background-image: linear-gradient(to left, #2f2e49, #2d436e);
}

.dashboard-container .fc-widget {
  border: solid 1px #eae8e8;
  box-shadow: 0 7px 6px 0 rgba(233, 233, 233, 0.5);
}

.dashboard-container .db-container {
  border: none;
}

/* dashboard resonsive changes */

text.Yaxis-label {
  font-size: 1em !important;
}

.dashboard-container .c-unit {
  font-size: 1.8vh;
}

.dashboard-container .c-value {
  font-size: 3.4vh;
}

.dashboard-container .c-description {
  font-size: 1.65vh;
}

.dashboard-container .axis text {
  font-size: 10px;
}

.dashboard-container .fc-widget {
  position: relative;
}

@media only screen and (min-width: 1440px) and (max-width: 1920px) {
  .dashboard-container .c-unit {
    font-size: 1.8vh;
  }
  .dashboard-container .c-value {
    font-size: 3.4vh;
  }
  .dashboard-container .c-description {
    font-size: 1.65vh;
  }
  .dashboard-container .axis text {
    font-size: 1.45vh;
  }
}

@media only screen and (min-width: 1920px) and (max-width: 2560px) {
  .selfcenter {
    font-size: 3vh !important;
  }
  .pofilecard img {
    height: 17vh !important;
  }
  .f14 {
    font-size: 1.8vh !important;
  }
  .f13 {
    font-size: 1.4vh !important;
  }
  .f12 {
    font-size: 1.4vh;
    opacity: 1 !important;
  }
  .thismonth {
    font-size: 1.9vh !important;
    opacity: 1 !important;
    letter-spacing: 0.1vh !important;
  }
  .lastmonth {
    font-size: 1.9vh !important;
    opacity: 1 !important;
    letter-spacing: 0.1vh !important;
  }
  .varience {
    letter-spacing: 0.4px;
    font-size: 2vh !important;
  }
  .varience-class {
    padding-top: 15px;
    opacity: 0.7;
    font-size: 15px;
  }
  .cost-lastmonth {
    padding-bottom: 35px;
  }
  .varience-class {
    font-size: 2.5vh;
  }
}

@media only screen and (min-width: 2560px) and (max-width: 3840px) {
  .selfcenter {
    font-size: 2vh !important;
  }
  .pofilecard img {
    height: 17vw !important;
  }
  .f14 {
    font-size: 1.8vh !important;
  }
  .f13 {
    font-size: 1.4vh !important;
  }
  .f12 {
    font-size: 1.4vh;
    opacity: 1 !important;
  }
  .thismonth {
    font-size: 1.2vh !important;
    opacity: 1 !important;
    letter-spacing: 0.1vh !important;
  }
  .lastmonth {
    font-size: 1.2vh !important;
    opacity: 1 !important;
    letter-spacing: 0.1vh !important;
  }
  .varience {
    letter-spacing: 0.4px;
    font-size: 1.5vh !important;
  }
  .varience-class {
    padding-top: 15px;
    opacity: 0.7;
    font-size: 15px;
  }
  .cost-lastmonth {
    padding-bottom: 35px;
  }
}
.dashboard-container .fc-widget-header {
  padding: 15px;
  padding-top: 15px;
  padding-bottom: 15px;
}

.dashboard-container .fc-widget-sublabel {
  padding-top: 2px;
  font-size: 12px;
}

text.Yaxis-label.timeseries {
  font-size: 0.69em !important;
}

.dashboard-container .fc-list-view-table td {
  white-space: nowrap;
  padding-left: 14px;
}

.dashboard-container .fc-list-view-table tbody tr.tablerow td:first-child {
  border-left: 3px solid transparent !important;
  font-size: 13px;
  font-weight: 400;
  font-style: normal;
  font-stretch: normal;
  line-height: 1;
  letter-spacing: 0.3px;
  text-align: left;
  color: #333333;
  white-space: nowrap;
  max-width: 230px;
}

.fc-black-theme
  .dashboard-container
  .fc-list-view-table
  tbody
  tr.tablerow
  td:first-child {
  color: #fff !important;
}

.dashboard-container .fc-list-view-table tbody tr.tablerow td:first-child div {
  max-width: 280px;
  text-overflow: ellipsis;
  overflow: hidden;
}

.dashboard-container .fc-widget-header .fc-widget-sublabel {
  display: none;
}

.dashboard-container .date-filter-comp button {
  right: 0;
  top: -30px;
  font-size: 13px;
  padding: 8px;
  border: none;
}

.dashboard-container .chartSlt {
  position: absolute;
  /* top: 28px; */
}

.dashboard-container .legend.legendsAll {
  padding-top: 15px;
}

.dashboard-container .emptyLegends {
  padding-top: 55px;
}

/*  chart change icon postion css*/

.dashboard-container .fc-widget:hover .externalLink {
  display: block !important;
  cursor: pointer;
  height: auto !important;
}

.dashboard-container.editmode .fc-widget:hover .chart-delete-icon {
  display: block !important;
  cursor: pointer;
  height: auto !important;
}

.dashboard-container .externalLink {
  display: none;
  position: absolute;
  right: 15px;
  top: 15px;
}

/* edit mode css*/

.dashboard-container.editmode .externalLink {
  display: none;
  position: absolute;
  right: 35px !important;
  top: 15px;
}

.dashboard-container .chart-delete-icon {
  display: none;
  position: absolute;
  color: #000;
  opacity: 0.6;
  right: 8px;
  top: 13px;
  z-index: 2;
}

/* dashbaord */

.new-create-dashboard-header {
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.8px;
  text-align: left;
  color: #ef4f8f;
  text-transform: uppercase;
  padding: 20px 20px 55px 20px;
  border-bottom: 1px solid #6666661a;
  background: white;
  box-shadow: 0 2px 4px 0 rgba(223, 223, 223, 0.5);
}

.create-dashboard-input-title.el-input .el-input__inner {
  border: none;
  color: #000000;
  letter-spacing: 0.6px;
}

.fullWidth {
  -webkit-box-flex: 0;
  -ms-flex: 0 0 100%;
  flex: 0 0 100%;
  max-width: 100% !important;
}

.collapse-btn {
  position: absolute;
  left: 24.2%;
  z-index: 10;
  width: 10px;
  height: 10px;
  border-left: 15px solid transparent;
  border-right: 15px solid transparent;
  border-bottom: 15px solid #39b2c2;
  transform: rotate(315deg);
  top: -3px;
  cursor: pointer;
}

.collapsed {
  left: -0.55rem;
}

.empty-box {
  width: 50px;
  height: 50px;
}

.row.reports-layout {
  height: 100%;
}

.dashboard-sidebar {
  background: white;
  height: 100%;
  overflow-y: scroll;
  border-right: 1px solid #6666662f;
}

.dashboard-sidebar-header {
  border-bottom: 1px solid #6666662f;
  position: absolute;
  background: #fff;
  width: 25%;
  box-shadow: 2px 1px 5px 0 rgba(219, 219, 219, 0.5);
}

.dashboard-sidebar .rtreenew {
  padding-bottom: 70px;
  height: 80vh;
  overflow: scroll;
}

.editdashboard-sidebar-header .rfolder-name {
  padding: 10px 25px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
  margin-top: 10px;
}

.rfolder-name i {
  margin-right: 6px;
  font-size: 16px;
  color: #66666696;
}

.editer-rfolder-children div {
  padding: 10px 10px;
}

.editdashboard-sidebar-header .editer-rfolder-children div:not(.rempty) {
  cursor: pointer;
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 2.08;
  letter-spacing: 0.4px;
  text-align: left;
  color: #333333;
  display: -webkit-inline-box;
  display: -ms-inline-flexbox;
  display: inline-flex;
  margin-right: 1px;
  white-space: nowrap;
  border-radius: 3px;
  margin-top: 10px;
  font-weight: 500px;
}

.editdashboard-sidebar-header .editer-rfolder-children div:not(.rempty):hover,
.editer-rfolder-children div.active,
.scheduled-viewall:hover,
.scheduled-viewall div.active {
  background: #ee518f;
  color: #fff;
  font-weight: 500;
}

.editdashboard-sidebar-header .scheduled-viewall div.active {
  background: #ee518f;
  color: #fff;
  font-weight: 500;
}

.rfolder-icon {
  display: inline-block;
  width: 20px;
}

.rempty {
  font-size: 13px;
  color: rgba(102, 102, 102, 0.57);
}

.fc-chart-side-btn {
  padding: 0;
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1px;
  text-align: left;
  /* color: #717b85; */
}

.fc-chart-side-btn .el-button-group button.el-button {
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.9px;
  text-align: left;
  /* color: #000000; */
}

.m20 {
  margin: 20px;
}

.pR10 {
  padding-right: 10px;
}

.r-sidebar-btn {
  padding-left: 10px;
  color: #615f89;
}

.r-sidebar-btn .el-button {
  color: #615f89;
  border-left: none;
}

.r-sidebar-btn .el-button-group .el-button:last-child {
  border-left: none;
}

.scheduled-viewall {
  width: 23.9%;
  border-top: solid 1px #f4f4f4;
  position: absolute;
  bottom: 0px;
  background-color: #ffffff;
  font-size: 12px;
  letter-spacing: 0.4px;
  color: #46a2bf;
  cursor: pointer;
}

.scheduled-viewall div {
  padding: 13px 20px;
}

.dashboard-sidebar-header .el-tabs__item {
  font-size: 12px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.1px;
  text-align: left;
  color: #333333;
}

.dashboard-sidebar-header .el-tabs__nav {
  height: 30px;
  margin-left: 10px;
}

.dashboard-sidebar-header .el-tabs__nav-wrap {
  padding-top: 25px;
  padding-left: 15px;
  padding-right: 15px;
}

.dashboard-sidebar-header .el-tabs__active-bar {
  background-color: #fd4b92;
  height: 0.157rem;
}

.dragzone {
  background: #39b2c233;
  position: fixed;
  height: 100%;
  z-index: 100;
  width: 100%;
}

.dropArea {
  position: relative;
  top: 50%;
  left: 30%;
  font-size: 3em;
}

.create-dashboard-btn-section .chart-delete-icon {
  color: #868686;
  padding-right: 15px;
  padding-top: 6px;
  cursor: pointer;
}

.create-dashboard-btn-section {
  display: inline-flex;
}

.editdashboard-sidebar-header span.el-tabs__nav-prev {
  position: absolute;
  top: 11px;
  padding-left: 3px;
  font-size: 15px;
  font-weight: 600;
}

.editdashboard-sidebar-header .el-tabs__nav-next {
  position: absolute;
  top: 11px;
  padding-right: 3px;
  font-size: 15px;
  font-weight: 600;
}

.editdashboard-sidebar-header .drag-children {
  box-shadow: 0 3px 2px 0 rgba(217, 217, 217, 0.5);
}

.editdashboard-sidebar-header .editer-rfolder-children {
  margin-right: 10px;
  margin-left: 20px;
}

.editdashboard-sidebar-header .chart-drag-icon {
  width: 30px;
  position: absolute;
  padding: 0;
  right: 0;
  top: 10px;
}

.drag-children .drag-icon {
  -webkit-transform: rotate(90deg);
  -moz-transform: rotate(90deg);
  -o-transform: rotate(90deg);
  -ms-transform: rotate(90deg);
  transform: rotate(90deg);
  font-size: 14px;
  position: relative;
  top: -5px;
  right: 0;
  color: #d8d8d8;
}

.drag-children .drag-icon.right {
  right: 8px;
}

.editdashboard-sidebar-header .drag-children:hover .drag-icon {
  color: #fff;
  width: 100%;
  position: relative;
}

.editdashboard-sidebar-header .drag-children {
  position: relative;
  width: 100%;
  border: solid 1px #ececec;
  border-left: 3px solid #ee518f;
}

.empty-drop-box {
  align-self: center;
  justify-content: center;
  align-items: center;
  padding-top: 30%;
  padding-bottom: 30%;
  background-color: #ffffff;
  border: dashed 1px #39b2c2;
  margin: 15px;
}

.empty-drop-box .header {
  margin-top: auto;
  font-size: 14px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 2.3px;
  color: #39b2c2;
  margin: 10px;
  text-align: center;
}

.empty-drop-box .subheader {
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.38;
  letter-spacing: 0.5px;
  text-align: center;
  color: #cdcdcd;
  width: 200px;
  margin: auto;
}

.edit-leftbar-icon {
  padding: 0 !important;
  padding-right: 10px !important;
  margin-top: 5px !important;
  padding-left: 10px !important;
}

.drag-label {
  width: 75%;
}

.table-hover {
  display: none;
}

.drag-children:hover .table-hover {
  display: inline !important;
}

.drag-children:hover .table-main {
  display: none !important;
}

.editdashboard-sidebar-header
  .el-tabs__nav-wrap.is-scrollable
  span.el-tabs__nav-prev,
.editdashboard-sidebar-header
  .el-tabs__nav-wrap.is-scrollable
  span.el-tabs__nav-next {
  display: none;
}

.editdashboard-sidebar-header
  .el-tabs__nav-wrap.is-scrollable:hover
  span.el-tabs__nav-prev,
.editdashboard-sidebar-header
  .el-tabs__nav-wrap.is-scrollable:hover
  span.el-tabs__nav-next {
  display: block !important;
}

.dashboard-sidebar .el-tabs__nav-wrap::after {
  border-bottom: 1px solid #e4e7ed;
  height: 0px !important;
}

.editdashboard-sidebar .el-tabs__header {
  margin-bottom: 5px !important;
}

.dashboard-sidebar-header .el-tabs__item:first-child {
  padding-left: 15px;
}

.dashboard-sidebar-header .el-tabs__item:last-child {
  padding-right: 15px !important;
}

.dashboard-container.editmode .fc-b-card .fc-avatar {
  width: 100% !important;
  height: 100px !important;
}
.dashboard-folder {
  margin-right: 25px;
  padding-top: 10px;
}
.dashboard-container.editmode .v-modal {
  z-index: 10;
  background: transparent;
  visibility: hidden;
}
.select-building-dialog .el-dialog__header {
  padding-top: 0;
  padding-bottom: 10px;
}
.select-building-dialog .el-dialog__body {
  height: 130px;
}
.select-reading-dialog .el-dialog__body {
  height: 550px;
  padding: 0;
}
.select-ahu-dialog .el-dialog__body {
  height: 250px;
}
.select-building-dialog .el-dialog__footer {
  padding: 0px !important;
}
.select-building-dialog .el-dialog__footer .el-button {
  border-radius: 0px !important;
  padding: 18px !important;
  font-size: 13px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  border: 0px;
}
.select-building-dialog .el-dialog__footer .el-button--primary {
  background: #39b2c2;
  border-color: #39b2c2;
}
.select-building-dialog .el-select {
  width: 100%;
}
.card-shimmer section {
  height: 250px !important;
}
.fullWidth .card-shimmer section {
  height: 280px !important;
}
.select-card-reading .p5 {
  padding-top: 15px;
  padding-bottom: 15px;
}
.select-card-reading .p5 div {
  width: 100%;
}
.select-building-dialog .reading-card-container {
  padding-top: 10px;
  padding-bottom: 10px;
}
.select-card-reading .fc-el-btn {
  padding: 18px !important;
  background: #fff;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  font-weight: 500;
  text-align: center;
  cursor: pointer;
}
.select-card-reading .fc-el-btn.el-report-save-btn {
  background: #39b2c2;
  color: #fff;
}
.reading-card-header {
  padding-bottom: 5px;
}
.gridempty .vue-resizable-handle {
  display: none;
}
.dashboard-container .vue-image-crop-upload .vicp-wrap {
  width: 300px !important;
  height: 350px !important;
}
.dashboard-container .vue-image-crop-upload .vicp-crop-right {
  display: none !important;
}
.dashboard-container .vue-image-crop-upload .vicp-wrap .vicp-operate {
  position: absolute;
  left: 0;
  bottom: 0;
}
.dashboard-container .vicp-operate a {
  border-radius: 0 !important;
  font-size: 13px !important;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  border: 0;
  color: #606266 !important;
  width: 50% !important;
  margin: 0 !important;
  height: 50px !important;
  padding-top: 8px;
}
.dashboard-container .vicp-operate a.vicp-operate-btn {
  background: #39b2c2 !important;
  border-color: #39b2c2;
  color: #fff !important;
}
.dashboard-container .vicp-operate {
  width: 100%;
}
.chart-icon-hide,
.select-building-dialog .el-dialog__headerbtn {
  display: none;
}
.readingmode4 .select-reading-dialog .el-dialog__body {
  height: 670px;
  padding: 0;
}
</style>
