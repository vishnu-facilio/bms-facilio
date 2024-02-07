<template>
  <div class="dashboard-main-section">
    <div class="row" v-if="mode === 'new'">
      <div class="col-12">
        <div
          class="new-create-dashboard-header fc-theme-color flex-middle justify-between"
        >
          <div style="width: 40%;padding-left:5px;display:inline-flex">
            <div>
              <el-input
                :autofocus="true"
                class="create-dashboard-input-title"
                v-model="newdashboard.label"
                placeholder="Enter the Dashboard name"
              ></el-input>
            </div>
            <div class="pointer" v-if="!folderLoading">
              <el-popover
                placement="bottom"
                width="300"
                trigger="click"
                popper-class="p0 dashboard-folder-chooser"
                v-model="chooseFolderPopover"
              >
                <el-row class="p10 border-bottom1px fol-header">
                  <el-col :span="16"
                    ><div class="bold">Choose Folder</div></el-col
                  >
                  <el-col :span="8">
                    <div class="pull-right">
                      <div @click="showAddDashboardfolder = true">
                        <i class="el-icon-folder-add f18 pointer"></i>
                      </div>
                      <!-- <el-button
                          size="mini"
                          type="primary"
                          icon="el-icon-plus"
                        ></el-button> -->
                    </div>
                  </el-col>
                </el-row>
                <el-row class="">
                  <el-col :span="24" class="p10"
                    ><div class="">
                      <el-input
                        size="small"
                        class="fc-input-full-border4 fc-small-btn"
                        v-model="dashboardFolderQuery"
                        suffix-icon="el-icon-search"
                        placeholder="Search Folder"
                      ></el-input></div
                  ></el-col>
                  <el-col :span="24" class="dashboard-folder-height p10">
                    <div
                      class="pointer p10 dashboard-folder f13"
                      v-bind:class="{
                        active: selectedDashoardFolder.id === folder.id,
                        bold: selectedDashoardFolder.id === folder.id,
                      }"
                      v-for="(folder, index) in filteredDashboardList"
                      :key="index"
                      @click="
                        handleSelect(folder), (chooseFolderPopover = false)
                      "
                    >
                      <i
                        class="el-icon-folder pR3"
                        style="color:#ef508f"
                        v-if="selectedDashoardFolder.id === folder.id"
                      ></i>
                      <i class="el-icon-folder pR3" v-else></i>
                      {{ folder.name }}
                      <div
                        class="pull-right"
                        v-if="selectedDashoardFolder.id === folder.id"
                      >
                        <i style="color:green" class="el-icon-check"></i>
                      </div>
                    </div>
                  </el-col>
                </el-row>
                <div slot="reference">
                  <div class="display-flex text-t-none">
                    <div class="f14 pL10">{{ ` in ` }}</div>
                    <div class="display-flex btn-effect-1">
                      <i class="el-icon-folder f18 pR5 primary-font"></i>
                      <div class="primary-font f14 ellipsis">
                        {{ selectedDashoardFolder.name }}
                      </div>
                    </div>
                  </div>
                </div>
              </el-popover>
            </div>
          </div>
          <div class="create-dashboard-btn-section">
            <!-- <el-autocomplete
              popper-class="my-autocomplete"
              v-model="dashbaordFolderName"
              :fetch-suggestions="querySearch"
              placeholder="Enter the folder name"
              @select="handleSelect"
              class="fc-autocomplete-select mR10"
            >
              <i class="el-icon-folder f18 pT8 pR5" slot="prefix"></i>
              <i class slot="suffix" @click="handleIconClick"></i>
              <template v-slot="{ item }">
                <div class="dashboard-folder-name">{{ item.name }}</div>
              </template>
            </el-autocomplete> -->
            <el-dropdown @command="handleButton">
              <el-button
                type="primary"
                class="pT10 PR20 fc-create-btn mL10 mR10 f13"
              >
                <i class="el-icon-plus"></i> Add<i
                  class="el-icon-arrow-down el-icon--right"
                ></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="addText">Add Text</el-dropdown-item>
                <el-dropdown-item command="addImage"
                  >Add Image</el-dropdown-item
                >
                <el-dropdown-item command="addComponent"
                  >Add Component</el-dropdown-item
                >
                <el-dropdown-item command="addCard" v-if="canShowNewCardBuilder"
                  >Add Card</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
            <el-button
              size="medium"
              class="plain f13"
              @click="cancel()"
              style="height: 35px !important;"
            >
              {{ $t('panel.dashboard.cancel') }}
            </el-button>
            <el-button
              size="medium"
              :disabled="dashboardSave"
              type="primary"
              class="setup-el-btn f13 mR10"
              @click="loadSupportedModules"
              :loading="dashboardSave"
              style="height: 35px !important;"
            >
              {{
                dashboardSave ? 'Saving' : $t('panel.dashboard.save')
              }}</el-button
            >
          </div>
        </div>
      </div>
    </div>
    <div class="row" v-else-if="dashboard && !loading">
      <div class="col-12">
        <div
          class="new-create-dashboard-header fc-theme-color flex-middle justify-between visibility-visible-actions"
        >
          <div class="pull-left width30">
            <!-- <div class="create-dashboard-field-header uppercase">EDIT DASHBOARD</div> -->
            <div class="flex-middle width100 justify-content-end">
              <el-input
                :autofocus="true"
                class="create-dashboard-input-title newreport-title-input dashboard-title-input"
                v-model="dashboard.label"
                placeholder="Enter Alternative Dashboard Name"
              ></el-input>
              <div class="pointer">
                <el-popover
                  placement="bottom"
                  width="300"
                  trigger="click"
                  popper-class="p0 dashboard-folder-chooser"
                  v-model="chooseFolderPopover"
                >
                  <el-row class="p10 border-bottom1px fol-header">
                    <el-col :span="16"
                      ><div class="bold">Choose Folder</div></el-col
                    >
                    <el-col :span="8">
                      <div class="pull-right">
                        <div @click="showAddDashboardfolder = true">
                          <i class="el-icon-folder-add f18 pointer"></i>
                        </div>
                        <!-- <el-button
                          size="mini"
                          type="primary"
                          icon="el-icon-plus"
                        ></el-button> -->
                      </div>
                    </el-col>
                  </el-row>
                  <el-row class="">
                    <el-col :span="24" class="p10"
                      ><div class="">
                        <el-input
                          size="small"
                          class="fc-input-full-border4 fc-small-btn"
                          v-model="dashboardFolderQuery"
                          suffix-icon="el-icon-search"
                          placeholder="Search Folder"
                        ></el-input></div
                    ></el-col>
                    <el-col :span="24" class="dashboard-folder-height p10">
                      <div
                        class="pointer p10 dashboard-folder f13"
                        v-bind:class="{
                          active: selectedDashoardFolder.id === folder.id,
                          bold: selectedDashoardFolder.id === folder.id,
                        }"
                        v-for="(folder, index) in filteredDashboardList"
                        :key="index"
                        @click="
                          handleSelect(folder), (chooseFolderPopover = false)
                        "
                      >
                        <i
                          class="el-icon-folder pR3"
                          style="color:#ef508f"
                          v-if="selectedDashoardFolder.id === folder.id"
                        ></i>
                        <i class="el-icon-folder pR3" v-else></i>
                        {{ folder.name }}
                        <div
                          class="pull-right"
                          v-if="selectedDashoardFolder.id === folder.id"
                        >
                          <i style="color:green" class="el-icon-check"></i>
                        </div>
                      </div>
                    </el-col>
                  </el-row>
                  <div slot="reference">
                    <div class="display-flex text-t-none">
                      <div class="f14 pL10">{{ ` in ` }}</div>
                      <div class="display-flex btn-effect-1">
                        <i class="el-icon-folder f18 pR5 primary-font"></i>
                        <div class="primary-font f14 ellipsis">
                          {{ selectedDashoardFolder.name }}
                        </div>
                      </div>
                    </div>
                  </div>
                </el-popover>
              </div>
            </div>
          </div>
          <div class="flex-middle width100 justify-content-end">
            <div class="flex-middle create-dashboard-btn-section">
              <!-- <el-select
                v-model="selectedTabId"
                @change="changeDashboardTabId(selectedTabId)"
                :filterable="true"
                placeholder="Tab Name"
                v-if="dashboardTabsList && dashboardTabsList.length > 0"
                class="fc-input-select mR20 fc-input-full-border2"
              >
                <el-option
                  v-for="(tab, id) in dashboardTabsList"
                  :key="id"
                  :label="tab.name"
                  :value="tab.id"
                >
                </el-option>
              </el-select> -->
              <!-- <el-autocomplete
                popper-class="my-autocomplete"
                v-model="dashbaordFolderName"
                :fetch-suggestions="querySearch"
                placeholder="Enter the folder name"
                @select="handleSelect"
                class="fc-autocomplete-select"
              >
                <i class="el-icon-folder f18 pT8 pR5" slot="prefix"></i>
                <i class slot="prefix" @click="handleIconClick"></i>
                <template v-slot="{ item }">
                  <div class="dashboard-folder-name">{{ item.name }}</div>
                </template>
              </el-autocomplete> -->
              <!-- <div
                v-if="dashboard.tabEnabled"
                class="chart-delete-icon"
                style
                v-tippy
                title="Edit Dashboard Tabs"
              >
                <i
                  class="el-icon-setting f18 pL10"
                  @click="toggleDashboardTabEditor()"
                ></i>
              </div> -->
              <div class="pointer" style v-tippy title="Edit Dashboard Tabs">
                <el-popover
                  popper-class="dashboard-option-dialog"
                  placement="bottom"
                  width="300"
                  trigger="click"
                >
                  <div>
                    <div class=" dashboard-option-header p10">
                      Tab Options
                    </div>
                    <div class="p10">
                      <div class="row pT10">
                        <div class="col-6">Tabs</div>
                        <div class="col-6">
                          <el-switch
                            @change="showconfirmationDilog()"
                            class="pull-right el-input-textbox-full-border"
                            v-model="dashboardOptions.showTabs"
                          />
                        </div>
                      </div>
                      <div class="row pT10">
                        <div class="col-6 self-center">Tab Position</div>
                        <div class="col-6">
                          <el-select
                            class="pull-right el-input-textbox-full-border"
                            v-model="dashboardOptions.dashboardTabPlacement"
                            placeholder="Please select the position"
                          >
                            <el-option :label="'Top'" :value="1"></el-option>
                            <el-option :label="'Left'" :value="2"></el-option>
                          </el-select>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div slot="reference">
                    <img
                      src="~assets/tabs.svg"
                      style="height:20px;width:20px;margin-right: 3px;margin-top:3p"
                      class="delete-icon mL5"
                      @click="showDashboardOption = true"
                    />

                    <!-- <i
                      class="el-icon-s-operation f24 pL10"
                      @click="showDashboardOption = true"
                    ></i> -->
                  </div>
                </el-popover>
              </div>
            </div>
            <div
              class="flex-middle create-dashboard-btn-section dashboard-folder"
            >
              <el-dropdown @command="handleButton">
                <el-button
                  type="primary"
                  class="pT10 PR20 fc-create-btn mL10 mR10 f13"
                >
                  <i class="el-icon-plus"></i> Add<i
                    class="el-icon-arrow-down el-icon--right"
                  ></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="addText"
                    >Add Text</el-dropdown-item
                  >
                  <el-dropdown-item command="addImage"
                    >Add Image</el-dropdown-item
                  >
                  <el-dropdown-item command="addComponent"
                    >Add Component</el-dropdown-item
                  >
                  <el-dropdown-item
                    command="addCard"
                    v-if="canShowNewCardBuilder"
                    >Add Card</el-dropdown-item
                  >
                </el-dropdown-menu>
              </el-dropdown>
            </div>
            <el-button
              size="medium"
              class="plain f13"
              style="height: 35px !important;"
              @click="cancel()"
              >{{ $t('panel.dashboard.cancel') }}
            </el-button>
            <el-button
              size="medium"
              type="primary"
              style="height: 35px !important;"
              class="setup-el-btn mR10 f13"
              @click="saveMultiTab"
              :loading="dashboardSave"
              >{{
                dashboardSave ? 'Saving' : $t('panel.dashboard.save')
              }}</el-button
            >
            <!-- <div class="chart-delete-icon visibility-hide-actions" style v-tippy title="Delete Dashboard">
                <i class="el-icon-delete fc-red f18 mL10" @click="deletedashboard()"></i>
              </div>

              <el-dropdown>
              <span class="el-dropdown-link">
                <i class="el-icon-more"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click="deletedashboard()">Delete</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown> -->
          </div>
        </div>
      </div>
    </div>
    <div class="row" style="position:relative;">
      <div
        class="col-12 container-section "
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
            <dashboard-filter v-if="$route.query.filters"></dashboard-filter>
            <div
              @drop="drop"
              @dragover="allowDrop"
              v-bind:class="{ dragzone: isDraging == true }"
              v-if="isDraging"
            >
              <div class="dropArea">
                {{ $t('panel.dashboard.drop') }}
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
                    <el-button @click="cancelBuilding()" class="col-6">{{
                      $t('panel.dashboard.cancel')
                    }}</el-button>
                    <el-button
                      type="primary"
                      @click="selectBuildings()"
                      class="col-6"
                      >{{ $t('panel.dashboard.confirm') }}</el-button
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
                    <el-button @click="cancelBuilding()" class="col-6">{{
                      $t('panel.dashboard.cancel')
                    }}</el-button>
                    <el-button
                      type="primary"
                      @click="selectBuildings()"
                      class="col-6"
                      >{{ $t('panel.dashboard.confirm') }}</el-button
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
                    <el-button @click="cancelBuilding()" class="col-6">{{
                      $t('panel.dashboard.cancel')
                    }}</el-button>
                    <el-button
                      type="primary"
                      @click="selectwebcard()"
                      class="col-6"
                      >{{ $t('panel.dashboard.confirm') }}</el-button
                    >
                  </span>
                </el-dialog>
                <el-dialog
                  custom-class="select-building-dialog select-reading-dialog"
                  v-bind:class="{
                    readingmode4: readingCard.mode === 4,
                    editmode: readingCard.editmode,
                  }"
                  :visible.sync="readingpopup"
                  :modal-append-to-body="false"
                  width="60%"
                  :before-close="cancelBuilding"
                >
                  <div class="new-header-container">
                    <div class="new-header-text" v-if="readingCard.editmode">
                      <div class="fc-setup-modal-title">
                        {{ $t('panel.dashboard.edit_Read_card') }}
                      </div>
                    </div>
                    <div class="new-header-text" v-else>
                      <div class="fc-setup-modal-title">
                        {{ $t('panel.dashboard.add_Read_card') }}
                      </div>
                    </div>
                  </div>
                  <div
                    class
                    v-bind:class="{
                      'mT20 mL10 mR40': readingCard.editmode,
                      'mT20 mL40 mR40': !readingCard.editmode,
                    }"
                  >
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
                                  {{ $t('panel.dashboard.energy_usage') }}
                                </div>
                                <div class="card-static-reading">
                                  999
                                  <span class="card-reading-value">kWh</span>
                                </div>
                                <div class="f12 bold">
                                  {{ $t('panel.dashboard.ths_mnth') }}
                                </div>
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
                                  {{ $t('panel.dashboard.energy_usage') }}
                                </div>
                                <div class="card-static-reading">
                                  999
                                  <span class="card-reading-value">kWh</span>
                                </div>
                                <div class="f12 bold">
                                  {{ $t('panel.dashboard.ths_mnth') }}
                                </div>
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
                        v-bind:class="{ 'col-12': readingCard.editmode }"
                      >
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            {{ $t('panel.dashboard.add_read') }}
                          </div>
                          <el-popover
                            placement="right"
                            width="300"
                            popper-class="select-card-reading  fc-popover-p0"
                            v-model="addreadingVisible2"
                          >
                            <f-add-data-point
                              :showLastValue="true"
                              ref="addDataPointForm"
                              @save="getDatapoint"
                              @cancel="cancelDataPointAdder"
                              :source="'dashboardedit'"
                              :editdata="readingCard"
                            >
                            </f-add-data-point>
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
                            {{ $t('panel.dashboard.title') }}
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
                            {{ $t('panel.dashboard.add_tgt_read') }}
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
                                popper-class="select-card-reading  fc-popover-p0"
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
                                  :newReading="true"
                                  :editdata="readingCard.targetreading"
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
                              >
                              </el-input>
                            </div>
                          </div>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            {{ $t('panel.dashboard.max') }}
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
                            >
                            </el-input>
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
                            >
                            </el-input>
                          </div>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            {{ $t('panel.dashboard.period') }}
                          </div>
                          <el-select
                            v-model="readingCard.operatorId"
                            placeholder="Please select a building"
                            class="el-input-textbox-full-border"
                          >
                            <el-option
                              :label="dateRange.label"
                              :value="dateRange.value"
                              v-for="(dateRange,
                              index) in getdateOperators().filter(
                                rt => rt.label === 'Range'
                              )"
                              :key="index"
                            ></el-option>
                          </el-select>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            {{ $t('panel.dashboard.aggregation') }}
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
                        <div class="reading-card-container">
                          <el-checkbox
                            v-model="
                              readingCard.targetreading.enableCenterText1
                            "
                            >{{ $t('panel.dashboard.enable__center_label') }}
                          </el-checkbox>
                        </div>
                      </div>
                      <div
                        class="reading-card-data-selecter col-7"
                        v-else
                        v-bind:class="{ 'col-12': readingCard.editmode }"
                      >
                        <div class="reading-card-container">
                          <div class="reading-card-header fc-input-label-txt">
                            {{ $t('panel.dashboard.select_reading') }}
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
                              :newReading="true"
                              :editdata="readingCard"
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
                            {{ $t('panel.dashboard.title') }}
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
                            {{ $t('panel.dashboard.period') }}
                          </div>
                          <el-select
                            v-model="readingCard.operatorId"
                            placeholder="Please select a building"
                            class="el-input-textbox-full-border"
                          >
                            <el-option
                              :label="dateRange.label"
                              :value="dateRange.value"
                              v-for="(dateRange,
                              index) in getdateOperators().filter(
                                rt => rt.label === 'Range'
                              )"
                              :key="index"
                            ></el-option>
                          </el-select>
                        </div>
                        <div
                          class="reading-card-container"
                          v-if="readingCard.mode === 3"
                        >
                          <div class="reading-card-header fc-input-label-txt">
                            {{ $t('panel.dashboard.aggregation') }}
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
                            {{ $t('panel.dashboard.aggregation') }}
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
                          v-if="
                            readingCard.mode === 1 &&
                              readingCard.readingType === 2 &&
                              $helpers.isLicenseEnabled('CONTROL_ACTIONS') &&
                              readingCard.aggregationFunc === 6
                          "
                        >
                          <el-checkbox v-model="readingCard.write">{{
                            $t('panel.dashboard.enable_control')
                          }}</el-checkbox>
                        </div>
                        <div
                          class="reading-card-container"
                          v-if="
                            readingCard.mode === 1 &&
                              readingCard.aggregationFunc === 6
                          "
                        >
                          <el-checkbox
                            v-model="readingCard.colorMap"
                            @change="setColorMapProperties(readingCard)"
                          >
                            {{
                              $t('panel.dashboard.condition_format')
                            }}</el-checkbox
                          >
                        </div>
                        <div
                          class="reading-card-container"
                          v-if="
                            readingCard.mode === 2 &&
                              readingCard.readingType === 2 &&
                              $helpers.isLicenseEnabled('CONTROL_ACTIONS') &&
                              readingCard.aggregationFunc === 6
                          "
                        >
                          <el-checkbox v-model="readingCard.write">{{
                            $t('panel.dashboard.enable_control')
                          }}</el-checkbox>
                        </div>
                        <div
                          class="reading-card-container"
                          v-if="readingCard.mode === 3"
                        >
                          <div class="reading-card-header fc-input-label-txt">
                            {{ $t('panel.dashboard.graph_aggregation') }}
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
                        <spinner
                          v-if="readingCard.colorMap && !readingCard.readings"
                          :show="readingCard.colorMap && !readingCard.readings"
                          size="80"
                        ></spinner>
                        <div
                          class="reading-card-container"
                          v-if="
                            readingCard.colorMap &&
                              readingCard.readings &&
                              readingCard.readings.readingField &&
                              colorMapSupported[
                                readingCard.readings.readingField.dataTypeEnum
                              ] &&
                              Object.keys(readingCard.colorMapConfig).length
                          "
                        >
                          <div
                            class="color-map-container"
                            v-if="
                              readingCard.readings.readingField.dataTypeEnum ===
                                'BOOLEAN'
                            "
                          >
                            <div class="row pB5">
                              <div class="col-2">
                                {{ $t('panel.dashboard.value') }}
                              </div>
                              <div class="col-6">
                                {{ $t('panel.dashboard.label') }}
                              </div>
                              <div class="col-2 text-center">
                                {{ $t('panel.dashboard.text') }}
                              </div>
                              <div class="col-2 text-center">Bg</div>
                            </div>
                            <div class="row pB5">
                              <div class="col-2 self-center">0</div>
                              <div class="col-6 self-center">
                                <el-input
                                  placeholder="Please input"
                                  class="el-input-textbox-full-border bR3 fc-input-full-border-h35"
                                  v-model="
                                    readingCard.colorMapConfig.BOOLEAN.min.label
                                  "
                                ></el-input>
                              </div>
                              <div class="col-2 self-center text-center">
                                <el-color-picker
                                  v-model="
                                    readingCard.colorMapConfig.BOOLEAN.min
                                      .textColor
                                  "
                                  size="small"
                                  class="dashboard-color-picker"
                                ></el-color-picker>
                              </div>
                              <div class="col-2 self-center text-center">
                                <el-color-picker
                                  v-model="
                                    readingCard.colorMapConfig.BOOLEAN.min
                                      .bgColor
                                  "
                                  size="small"
                                  class="dashboard-color-picker"
                                ></el-color-picker>
                              </div>
                            </div>
                            <div class="row pB5">
                              <div class="col-2 self-center">1</div>
                              <div class="col-6 self-center">
                                <el-input
                                  placeholder="Please input"
                                  class="el-input-textbox-full-border bR3 fc-input-full-border-h35"
                                  v-model="
                                    readingCard.colorMapConfig.BOOLEAN.max.label
                                  "
                                ></el-input>
                              </div>
                              <div class="col-2 self-center text-center">
                                <el-color-picker
                                  v-model="
                                    readingCard.colorMapConfig.BOOLEAN.max
                                      .textColor
                                  "
                                  size="small"
                                  class="dashboard-color-picker"
                                ></el-color-picker>
                              </div>
                              <div class="col-2 self-center text-center">
                                <el-color-picker
                                  v-model="
                                    readingCard.colorMapConfig.BOOLEAN.max
                                      .bgColor
                                  "
                                  size="small"
                                  class="dashboard-color-picker"
                                ></el-color-picker>
                              </div>
                            </div>
                          </div>
                          <div
                            class="color-map-container"
                            v-if="
                              readingCard.readings.readingField.dataTypeEnum ===
                                'ENUM' &&
                                readingCard.readings.readingField.enumMap &&
                                Object.keys(
                                  readingCard.readings.readingField.enumMap
                                ).length &&
                                readingCard.colorMapConfig['ENUM']
                            "
                          >
                            <div class="row pB5">
                              <div class="col-2">
                                {{ $t('panel.dashboard.value') }}
                              </div>
                              <div class="col-6">
                                {{ $t('panel.dashboard.label') }}
                              </div>
                              <div class="col-2 text-center">
                                {{ $t('panel.dashboard.text') }}
                              </div>
                              <div class="col-2 text-center">Bg</div>
                            </div>
                            <template v-if="render">
                              <div
                                class="row pB5"
                                v-for="(value, key) in readingCard
                                  .colorMapConfig['ENUM']"
                                :key="key"
                              >
                                <div class="col-2 self-center">{{ key }}</div>
                                <div class="col-6 self-center">
                                  <el-input
                                    placeholder="Please input"
                                    class="el-input-textbox-full-border bR3 fc-input-full-border-h35"
                                    v-model="value.label"
                                    :key="key"
                                  ></el-input>
                                </div>
                                <div class="col-2 self-center text-center">
                                  <el-color-picker
                                    v-model="value.textColor"
                                    size="small"
                                    :key="key"
                                    @change="$forceUpdate()"
                                    class="dashboard-color-picker"
                                  ></el-color-picker>
                                </div>
                                <div class="col-2 self-center text-center">
                                  <el-color-picker
                                    v-model="value.bgColor"
                                    size="small"
                                    :key="key"
                                    @change="$forceUpdate()"
                                    class="dashboard-color-picker"
                                  ></el-color-picker>
                                </div>
                              </div>
                            </template>
                          </div>
                          <div
                            class="color-map-container"
                            v-if="
                              readingCard.readings.readingField.dataTypeEnum ===
                                'NUMBER' &&
                                readingCard.colorMapConfig['NUMBER'] &&
                                readingCard.colorMapConfig['NUMBER'].length
                            "
                          >
                            <div class="row pB5">
                              <div class="col-3">
                                {{ $t('panel.dashboard.mn') }}
                              </div>
                              <div class="col-3">
                                {{ $t('panel.dashboard.mx') }}
                              </div>
                              <div class="col-3">
                                {{ $t('panel.dashboard.label') }}
                              </div>
                              <div class="col-1 text-center">
                                {{ $t('panel.dashboard.text') }}
                              </div>
                              <div class="col-1 text-center">Bg</div>
                            </div>
                            <template v-if="render">
                              <div
                                class="row pB10 visibility-visible-actions"
                                v-for="(value, key) in readingCard
                                  .colorMapConfig['NUMBER']"
                                :key="key"
                              >
                                <div class="col-3 self-center">
                                  <el-input-number
                                    v-model="value.min"
                                    size="mini"
                                  ></el-input-number>
                                </div>
                                <div class="col-3 self-center">
                                  <el-input-number
                                    v-model="value.max"
                                    size="mini"
                                  ></el-input-number>
                                </div>
                                <div class="col-3 self-center">
                                  <el-input
                                    placeholder="Please input"
                                    class="el-input-textbox-full-border bR3 fc-input-full-border-h35"
                                    v-model="value.label"
                                    :key="key"
                                  ></el-input>
                                </div>
                                <div class="col-1 self-center text-center">
                                  <el-color-picker
                                    v-model="value.textColor"
                                    size="small"
                                    :key="key"
                                    @change="$forceUpdate()"
                                    class="dashboard-color-picker"
                                  ></el-color-picker>
                                </div>
                                <div class="col-1 self-center text-center">
                                  <el-color-picker
                                    v-model="value.bgColor"
                                    size="small"
                                    :key="key"
                                    @change="$forceUpdate()"
                                    class="dashboard-color-picker"
                                  ></el-color-picker>
                                </div>
                                <div
                                  class="col-1 self-center text-center nowrap visibility-hide-actions"
                                  v-bind:class="{
                                    pL0: !key,
                                    'text-left': !key,
                                  }"
                                >
                                  <span
                                    class="add-icon"
                                    @click="addColorMapElemnt()"
                                  >
                                    <i class="el-icon-circle-plus-outline"></i>
                                  </span>
                                  <span
                                    class="delete-icon"
                                    @click="removeColorMapElemnt(key)"
                                    v-if="key"
                                  >
                                    <i class="el-icon-remove-outline"></i>
                                  </span>
                                </div>
                              </div>
                            </template>
                          </div>
                          <div
                            class="color-map-container"
                            v-if="
                              readingCard.readings.readingField.dataTypeEnum ===
                                'DECIMAL' &&
                                readingCard.colorMapConfig['DECIMAL'] &&
                                readingCard.colorMapConfig['DECIMAL'].length
                            "
                          >
                            <div class="row pB5">
                              <div class="col-3">
                                {{ $t('panel.dashboard.mn') }}
                              </div>
                              <div class="col-3">
                                {{ $t('panel.dashboard.mx') }}
                              </div>
                              <div class="col-3">
                                {{ $t('panel.dashboard.label') }}
                              </div>
                              <div class="col-1 text-center">
                                {{ $t('panel.dashboard.text') }}
                              </div>
                              <div class="col-1 text-center">Bg</div>
                            </div>
                            <template v-if="render">
                              <div
                                class="row pB10 visibility-visible-actions"
                                v-for="(value, key) in readingCard
                                  .colorMapConfig['DECIMAL']"
                                :key="key"
                              >
                                <div class="col-3 self-center">
                                  <el-input-number
                                    v-model="value.min"
                                    size="mini"
                                  ></el-input-number>
                                </div>
                                <div class="col-3 self-center">
                                  <el-input-number
                                    v-model="value.max"
                                    size="mini"
                                  ></el-input-number>
                                </div>
                                <div class="col-3 self-center">
                                  <el-input
                                    placeholder="Please input"
                                    class="el-input-textbox-full-border bR3 fc-input-full-border-h35"
                                    v-model="value.label"
                                    :key="key"
                                  ></el-input>
                                </div>
                                <div class="col-1 self-center text-center">
                                  <el-color-picker
                                    v-model="value.textColor"
                                    size="small"
                                    :key="key"
                                    @change="$forceUpdate()"
                                    class="dashboard-color-picker"
                                  ></el-color-picker>
                                </div>
                                <div class="col-1 self-center text-center">
                                  <el-color-picker
                                    v-model="value.bgColor"
                                    size="small"
                                    :key="key"
                                    @change="$forceUpdate()"
                                    class="dashboard-color-picker"
                                  ></el-color-picker>
                                </div>
                                <div
                                  class="col-1 self-center text-center nowrap visibility-hide-actions"
                                  v-bind:class="{
                                    pL0: !key,
                                    'text-left': !key,
                                  }"
                                >
                                  <span
                                    class="add-icon"
                                    @click="addColorMapElemntforDecimal()"
                                  >
                                    <i class="el-icon-circle-plus-outline"></i>
                                  </span>
                                  <span
                                    class="delete-icon"
                                    @click="removeColorMapElemntDecimal(key)"
                                    v-if="key"
                                  >
                                    <i class="el-icon-remove-outline"></i>
                                  </span>
                                </div>
                              </div>
                            </template>
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
                      >{{ $t('panel.dashboard.confirm') }}</el-button
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
                        {{ $t('panel.dashboard.sel_ahu') }}
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
                        {{ $t('panel.dashboard.sel_asset') }}
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
                      >{{ $t('panel.dashboard.confirm') }}</el-button
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
                  :widget="editwidgetData"
                  :editwidget="editwidgetcalled"
                  @update="imageCardUpdate"
                ></f-photoUploader>
                <textCard
                  :visibility.sync="showtextcard"
                  @update="updatetextcard"
                  @add="addtextcard"
                  :widget="editwidgetData"
                  @close="closetextcard"
                >
                </textCard>
                <f-card-builder
                  :visibility.sync="showCardBuilder"
                  @close="cancelBuilding"
                  @save="addAssetCard"
                  :addAssetCard="assetCardData"
                  :assetcardEdit="assetcardEdit"
                  @update="updateAssetCardData"
                >
                </f-card-builder>
                <f-fcu-card
                  :visibility.sync="showFcuCardDialog"
                  @close="cancelBuilding"
                  @update="updatFacuCard"
                  @save="saveFcuCard"
                  :cardData="fcuCardData"
                ></f-fcu-card>
                <f-boolean-card-dialog
                  :visibility.sync="showBooleanCardDialog"
                  @close="cancelBuilding"
                  @update="updatFacuCard"
                  @save="saveBooleancard"
                  :cardData="booleancardData"
                ></f-boolean-card-dialog>
                <f-alarmbar-card-dialog
                  :visibility.sync="showAlarmbarCardDialog"
                  @close="cancelBuilding"
                  @update="updatFacuCard"
                  @save="saveAlarmcard"
                  :cardData="alarmcardData"
                >
                </f-alarmbar-card-dialog>
                <kpi-card-setup
                  v-if="showkpiCard"
                  :visibility.sync="showkpiCard"
                  @close="cancelBuilding"
                  @save="cardBuilder"
                  @update="updateKpiCard"
                  :kpiCard="kpiCard"
                ></kpi-card-setup>
                <kpi-target-card
                  v-if="showkpitargetCard"
                  :visibility.sync="showkpitargetCard"
                  @close="cancelBuilding"
                  @save="cardBuilder"
                  @update="updateKpiCard"
                  :cardData="kpiCard"
                  :type="newDashboardData.childKey"
                >
                </kpi-target-card>
                <dashbaord-dialog
                  v-if="buildingCard"
                  :visibility.sync="buildingCard"
                  @close="cancelBuilding"
                  @save="cardBuilder"
                  @update="updateKpiCard"
                  :buildings="buildings"
                  :cardData="kpiCard"
                  :type="newDashboardData.childKey"
                ></dashbaord-dialog>
                <target-meter-dialog
                  v-if="targetMeterDialog"
                  :visibility.sync="targetMeterDialog"
                  @close="cancelBuilding"
                  @save="cardBuilder"
                  @update="updateKpiCard"
                  :buildings="buildings"
                  :cardData="kpiCard"
                  :type="newDashboardData.childKey"
                ></target-meter-dialog>
                <gauge-card-setup
                  v-if="gaugeSetup"
                  :visibility.sync="gaugeSetup"
                  @close="cancelBuilding"
                  @save="cardBuilder"
                  @update="updateKpiCard"
                  :buildings="buildings"
                  :cardData="kpiCard"
                  :type="newDashboardData.childKey"
                ></gauge-card-setup>
                <pmreading-card-setup
                  v-if="pmreadingsetup"
                  :visibility.sync="pmreadingsetup"
                  @close="cancelBuilding"
                  @save="cardBuilder"
                  @update="updateKpiCard"
                  :buildings="buildings"
                  :cardData="kpiCard"
                  :type="newDashboardData.childKey"
                ></pmreading-card-setup>
                <smart-map-setup
                  :visibility.sync="smartmapsetup"
                  v-if="smartmapsetup"
                  @close="cancelBuilding"
                  @save="cardBuilder"
                  @update="updateKpiCard"
                  :buildings="buildings"
                  :cardData="kpiCard"
                  :type="newDashboardData.childKey"
                ></smart-map-setup>
                <smart-table-setup
                  :visibility.sync="smarttablesetup"
                  v-if="smarttablesetup"
                  @close="cancelBuilding"
                  @save="cardBuilder"
                  @update="updateKpiCard"
                  :buildings="buildings"
                  :cardData="kpiCard"
                  :type="newDashboardData.childKey"
                ></smart-table-setup>
                <smart-table-wrapper-setup
                  :visibility.sync="smarttablewrappersetup"
                  v-if="smarttablewrappersetup"
                  @close="cancelBuilding"
                  @save="cardBuilder"
                  @update="updateKpiCard"
                  :buildings="buildings"
                  :cardData="kpiCard"
                  :type="newDashboardData.childKey"
                ></smart-table-wrapper-setup>
                <smart-card-setup
                  :visibility.sync="smartcardsetup"
                  v-if="smartcardsetup"
                  @close="cancelBuilding"
                  @save="cardBuilder"
                  @update="updateKpiCard"
                  :buildings="buildings"
                  :cardData="kpiCard"
                  :type="newDashboardData.childKey"
                ></smart-card-setup>
              </div>
            </div>
            <!-- <div class="row">
              <div class="col-6">
                <div class="self-center empty-drop-box">
                  <div class="header">{{ $t('panel.dashboard.dg_dp') }}</div>
                  <div class="subheader">
                    {{ $t('panel.dashboard.ht_and_wth') }}
                  </div>
                </div>
              </div>
            </div> -->
          </div>
        </div>
      </div>
      <div
        class="col-12 container-section"
        :style="getHeight"
        v-bind:class="{ fullWidth: collapseSidebar === true }"
        v-else
      >
        <el-row class="height-100">
          <el-col
            :span="4"
            v-if="
              dashboardTabContexts &&
                dashboardTabContexts.length > 0 &&
                dashboardOptions.dashboardTabPlacement === 2
            "
          >
            <div class="dashboard-tab-sidebar view-panel">
              <div
                class="dashboard-tab-sidebar-scroll"
                v-if="dashboardTabContexts && dashboardTabContexts.length > 0"
              >
                <div
                  v-for="(folder, index) in dashboardTabContexts"
                  :key="index"
                  class="dashboard-folder-container"
                >
                  <div
                    @click="expand(folder)"
                    class="dfolder-name"
                    v-bind:class="{ active: dashboardTabId === folder.id }"
                  >
                    <div class="mL5">
                      <span>{{ folder.name }}</span>
                      <div class="dfolder-icon fR" v-if="folder.childTabs">
                        <i class="el-icon-arrow-down" v-if="folder.expand"></i>
                        <i class="el-icon-arrow-right" v-else></i>
                      </div>
                    </div>
                  </div>
                  <div v-show="folder.expand" class="dfolder-children">
                    <div
                      @click="changeDashboardTabId(childFolder.id)"
                      v-for="(childFolder, cId) in folder.childTabs"
                      :key="cId"
                      v-bind:class="{
                        active: dashboardTabId === childFolder.id,
                      }"
                    >
                      <span class="mL5">{{ childFolder.name }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div
                class="addTab-btn view-manager-btn"
                @click="toggleDashboardTabEditor()"
              >
                <!-- <el-button
                  style="height: 35px !important;"
                  type="primary"
                  size="small"
                  class="setup-el-btn mR10"
                  @click="toggleDashboardTabEditor()"
                  ><i class="el-icon-setting"></i> Manage Tab</el-button
                > -->
                <i class="el-icon-setting"></i>
                <span class="label mL10 text-uppercase">
                  {{ $t('home.dashboard.tab_manager') }}
                </span>
              </div>
            </div>
          </el-col>
          <el-col
            :span="24"
            v-if="
              dashboardOptions.dashboardTabPlacement === 1 &&
                dashboardTabContexts &&
                dashboardTabContexts.length > 0
            "
          >
            <dashboard-top-tab
              :edit="true"
              :dashboardTabContexts="dashboardTabContexts"
              :dashboardTabId="dashboardTabId"
              @toggleDashboardTabEditor="toggleDashboardTabEditor"
              @changeDashboardTabId="changeDashboardTabId"
            ></dashboard-top-tab>
          </el-col>
          <el-col :span="tabsEnabled ? 20 : 24" class="height-100">
            <div class="height100 scrollable dashboardmainlayout">
              <div v-if="loading || !dashboardLayout || dashboardTabLoading">
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
                class="dashboard-container edit-dashboard-container"
                :class="{ editmode: editMode }"
                v-else
                @dragenter="containerEntry"
              >
                <dashboard-filter
                  v-if="$route.query.filters"
                ></dashboard-filter>
                <div
                  @drop="drop"
                  @dragover="allowDrop"
                  v-bind:class="{ dragzone: isDraging == true }"
                  v-if="isDraging"
                >
                  <div class="dropArea">
                    {{ $t('panel.drop') }}
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
                        <el-button @click="cancelBuilding()" class="col-6">{{
                          $t('panel.share.cancel')
                        }}</el-button>
                        <el-button
                          type="primary"
                          @click="selectBuildings()"
                          class="col-6"
                          >{{ $t('panel.dashboard.confirm') }}</el-button
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
                        <el-button @click="cancelBuilding()" class="col-6">{{
                          $t('panel.dashboard.cancel')
                        }}</el-button>
                        <el-button
                          type="primary"
                          @click="selectwebcard()"
                          class="col-6"
                          >{{ $t('panel.dashboard.confirm') }}</el-button
                        >
                      </span>
                    </el-dialog>
                    <el-dialog
                      custom-class="select-building-dialog select-reading-dialog"
                      v-bind:class="{
                        readingmode4: readingCard.mode === 4,
                        editmode: readingCard.editmode,
                      }"
                      :modal-append-to-body="false"
                      :visible.sync="readingpopup"
                      :width="readingCard.editmode ? '90%' : '80%'"
                      :before-close="cancelBuilding"
                    >
                      <div class="new-header-container">
                        <div
                          class="new-header-text"
                          v-if="readingCard.editmode"
                        >
                          <div class="fc-setup-modal-title">
                            {{ $t('panel.dashboard.edit_read_card') }}
                          </div>
                        </div>
                        <div class="new-header-text" v-else>
                          <div class="fc-setup-modal-title">
                            {{ $t('panel.dashboard.add_read_card') }}
                          </div>
                        </div>
                      </div>
                      <div
                        class
                        v-bind:class="{
                          'mT20 mL10 mR40': readingCard.editmode,
                          'mT20 mL40 mR40': !readingCard.editmode,
                        }"
                      >
                        <div class="row">
                          <div
                            class="reading-card-layout-changer col-5"
                            v-if="!readingCard.editmode"
                          >
                            <div class="row">
                              <div
                                class="card-layout-big pointer col-6"
                                v-bind:class="{
                                  active: readingCard.mode === 1,
                                }"
                                @click="readingCard.mode = 1"
                              >
                                <div class="rd-conatiner">
                                  <div class="sample-card-content">
                                    <div class="card-small-heading">
                                      {{ $t('panel.dashboard.energy_usage') }}
                                    </div>
                                    <div class="card-static-reading">
                                      999
                                      <span class="card-reading-value"
                                        >kWh</span
                                      >
                                    </div>
                                    <div class="f12 bold">
                                      {{ $t('panel.dashboard.ths_mnth') }}
                                    </div>
                                  </div>
                                </div>
                              </div>
                              <div
                                class="card-layout-big pointer layout-3 col-6"
                                v-bind:class="{
                                  active: readingCard.mode === 3,
                                }"
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
                                v-bind:class="{
                                  active: readingCard.mode === 2,
                                }"
                                @click="readingCard.mode = 2"
                              >
                                <div class="rd-conatiner">
                                  <div class="sample-card-content">
                                    <div class="card-small-heading">
                                      {{ $t('panel.dashboard.energy_usage') }}
                                    </div>
                                    <div class="card-static-reading">
                                      999
                                      <span class="card-reading-value"
                                        >kWh</span
                                      >
                                    </div>
                                    <div class="f12 bold">
                                      {{ $t('panel.dashboard.ths_mnth') }}
                                    </div>
                                  </div>
                                </div>
                              </div>
                              <div
                                class="card-layout-big pointer layout-3 col-6"
                                v-bind:class="{
                                  active: readingCard.mode === 4,
                                }"
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
                            v-bind:class="{ 'col-12': readingCard.editmode }"
                          >
                            <div class="reading-card-container">
                              <div
                                class="reading-card-header fc-input-label-txt"
                              >
                                {{ $t('panel.dashboard.add_read') }}
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
                                  :newReading="true"
                                  :editdata="readingCard"
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
                              <div
                                class="reading-card-header fc-input-label-txt"
                              >
                                {{ $t('panel.dashboard.title') }}
                              </div>
                              <el-input
                                :autofocus="true"
                                class="addReading-title el-input-textbox-full-border"
                                v-model="readingCard.title"
                                placeholder="Enter the title"
                              ></el-input>
                            </div>
                            <div class="reading-card-container">
                              <div
                                class="reading-card-header fc-input-label-txt"
                              >
                                {{ $t('panel.dashboard.add_tgt_read') }}
                              </div>
                              <div class="row readingcard-graph-container">
                                <div class="col-6">
                                  <el-select
                                    v-model="
                                      readingCard.targetreading.targetmode
                                    "
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
                                    popper-class="select-card-reading  fc-popover-p0"
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
                                      :newReading="true"
                                      :editdata="readingCard.targetreading"
                                    ></f-add-data-point>
                                    <el-input
                                      slot="reference"
                                      :autofocus="true"
                                      class="BL0 addReading-title el-input-textbox-full-border"
                                      v-model="
                                        readingCard.targetreading.readingName
                                      "
                                      :placeholder="
                                        'Click to add target reading'
                                      "
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
                                  >
                                  </el-input>
                                </div>
                              </div>
                            </div>
                            <div class="reading-card-container">
                              <div
                                class="reading-card-header fc-input-label-txt"
                              >
                                {{ $t('panel.dashboard.max') }}
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
                                    readingCard.targetreading.maxmode ===
                                      'constant'
                                  "
                                  :autofocus="true"
                                  class="BL0 addReading-title el-input-textbox-full-border col-4"
                                  v-model="readingCard.targetreading.maxValue"
                                  :placeholder="'Enter only Max value'"
                                >
                                </el-input>
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
                                  v-model="
                                    readingCard.targetreading.maxpercentage
                                  "
                                  :placeholder="'Enter only max %'"
                                >
                                </el-input>
                              </div>
                            </div>
                            <div class="reading-card-container">
                              <div
                                class="reading-card-header fc-input-label-txt"
                              >
                                {{ $t('panel.dashboard.period') }}
                              </div>
                              <el-select
                                v-model="readingCard.operatorId"
                                placeholder="Please select a building"
                                class="el-input-textbox-full-border"
                              >
                                <template
                                  v-for="(dateRange,
                                  index) in getdateOperators().filter(
                                    rt => rt.label === 'Range'
                                  )"
                                >
                                  <el-option
                                    :label="dateRange.label"
                                    :value="dateRange.value"
                                    :key="index"
                                  ></el-option>
                                </template>
                              </el-select>
                            </div>
                            <div class="reading-card-container">
                              <div
                                class="reading-card-header fc-input-label-txt"
                              >
                                {{ $t('panel.dashboard.aggregation') }}
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
                            <div class="reading-card-container">
                              <el-checkbox
                                v-model="
                                  readingCard.targetreading.enableCenterText1
                                "
                                >{{ $t('panel.dashboard.enable_center_label') }}
                              </el-checkbox>
                            </div>
                          </div>
                          <div
                            class="reading-card-data-selecter col-7"
                            v-else
                            v-bind:class="{ 'col-12': readingCard.editmode }"
                          >
                            <div class="reading-card-container">
                              <div
                                class="reading-card-header fc-input-label-txt"
                              >
                                {{ $t('panel.dashboard.select_reading') }}
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
                                  :newReading="true"
                                  :editdata="readingCard"
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
                              <div
                                class="reading-card-header fc-input-label-txt"
                              >
                                {{ $t('panel.dashboard.title') }}
                              </div>
                              <el-input
                                :autofocus="true"
                                class="addReading-title el-input-textbox-full-border"
                                v-model="readingCard.title"
                                placeholder="Enter the title"
                              ></el-input>
                            </div>
                            <div class="reading-card-container">
                              <div
                                class="reading-card-header fc-input-label-txt"
                              >
                                {{ $t('panel.dashboard.period') }}
                              </div>
                              <el-select
                                v-model="readingCard.operatorId"
                                placeholder="Please select a building"
                                class="el-input-textbox-full-border"
                              >
                                <el-option
                                  :label="dateRange.label"
                                  :value="dateRange.value"
                                  v-for="(dateRange,
                                  index) in getdateOperators().filter(
                                    rt => rt.label === 'Range'
                                  )"
                                  :key="index"
                                ></el-option>
                              </el-select>
                            </div>
                            <div
                              class="reading-card-container"
                              v-if="readingCard.mode === 3"
                            >
                              <div
                                class="reading-card-header fc-input-label-txt"
                              >
                                {{ $t('panel.dashboard.aggregation') }}
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
                              <div
                                class="reading-card-header fc-input-label-txt"
                              >
                                {{ $t('panel.dashboard.aggregation') }}
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
                              v-if="
                                readingCard.mode === 1 &&
                                  readingCard.readingType === 2 &&
                                  $helpers.isLicenseEnabled(
                                    'CONTROL_ACTIONS'
                                  ) &&
                                  readingCard.aggregationFunc === 6
                              "
                            >
                              <el-checkbox v-model="readingCard.write">{{
                                $t('panel.dashboard.enable_control')
                              }}</el-checkbox>
                            </div>
                            <div
                              class="reading-card-container"
                              v-if="
                                readingCard.mode === 1 &&
                                  readingCard.aggregationFunc === 6 &&
                                  readingCard &&
                                  readingCard.readings &&
                                  readingCard.readings.readingField &&
                                  colorMapSupported[
                                    readingCard.readings.readingField
                                      .dataTypeEnum
                                  ]
                              "
                            >
                              <el-checkbox
                                v-model="readingCard.colorMap"
                                @change="setColorMapProperties(readingCard)"
                              >
                                {{
                                  $t('panel.dashboard.condition_format')
                                }}</el-checkbox
                              >
                            </div>
                            <div
                              class="reading-card-container"
                              v-if="
                                readingCard.mode === 2 &&
                                  readingCard.readingType === 2 &&
                                  $helpers.isLicenseEnabled(
                                    'CONTROL_ACTIONS'
                                  ) &&
                                  readingCard.aggregationFunc === 6
                              "
                            >
                              <el-checkbox v-model="readingCard.write">{{
                                $t('panel.dashboard.enable_control')
                              }}</el-checkbox>
                            </div>
                            <div
                              class="reading-card-container"
                              v-if="readingCard.mode === 3"
                            >
                              <div
                                class="reading-card-header fc-input-label-txt"
                              >
                                {{ $t('panel.dashboard.graph_aggregation') }}
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
                                    v-for="(legend,
                                    index) in aggregateFunctions2"
                                    :key="index"
                                  ></el-option>
                                </el-select>
                              </div>
                            </div>
                            <spinner
                              v-if="
                                readingCard.colorMap && !readingCard.readings
                              "
                              :show="
                                readingCard.colorMap && !readingCard.readings
                              "
                              size="80"
                            ></spinner>
                            <div
                              class="reading-card-container"
                              v-if="
                                readingCard.colorMap &&
                                  readingCard.readings &&
                                  readingCard.readings.readingField &&
                                  colorMapSupported[
                                    readingCard.readings.readingField
                                      .dataTypeEnum
                                  ] &&
                                  Object.keys(readingCard.colorMapConfig).length
                              "
                            >
                              <div
                                class="color-map-container"
                                v-if="
                                  readingCard.readings.readingField
                                    .dataTypeEnum === 'BOOLEAN'
                                "
                              >
                                <div class="row pB5">
                                  <div class="col-2">
                                    {{ $t('panel.dashboard.value') }}
                                  </div>
                                  <div class="col-6">
                                    {{ $t('panel.dashboard.label') }}
                                  </div>
                                  <div class="col-2 text-center">
                                    {{ $t('panel.dashboard.text') }}
                                  </div>
                                  <div class="col-2 text-center">Bg</div>
                                </div>
                                <div class="row pB5">
                                  <div class="col-2 self-center">0</div>
                                  <div class="col-6 self-center">
                                    <el-input
                                      placeholder="Please input"
                                      class="el-input-textbox-full-border fc-input-full-border-h35"
                                      v-model="
                                        readingCard.colorMapConfig.BOOLEAN.min
                                          .label
                                      "
                                    ></el-input>
                                  </div>
                                  <div class="col-2 self-center text-center">
                                    <el-color-picker
                                      v-model="
                                        readingCard.colorMapConfig.BOOLEAN.min
                                          .textColor
                                      "
                                      size="small"
                                      class="dashboard-color-picker"
                                    ></el-color-picker>
                                  </div>
                                  <div class="col-2 self-center text-center">
                                    <el-color-picker
                                      v-model="
                                        readingCard.colorMapConfig.BOOLEAN.min
                                          .bgColor
                                      "
                                      size="small"
                                      class="dashboard-color-picker"
                                    ></el-color-picker>
                                  </div>
                                </div>
                                <div class="row pB5">
                                  <div class="col-2 self-center">1</div>
                                  <div class="col-6 self-center">
                                    <el-input
                                      placeholder="Please input"
                                      class="el-input-textbox-full-border fc-input-full-border-h35"
                                      v-model="
                                        readingCard.colorMapConfig.BOOLEAN.max
                                          .label
                                      "
                                    ></el-input>
                                  </div>
                                  <div class="col-2 self-center text-center">
                                    <el-color-picker
                                      v-model="
                                        readingCard.colorMapConfig.BOOLEAN.max
                                          .textColor
                                      "
                                      size="small"
                                      class="dashboard-color-picker"
                                    ></el-color-picker>
                                  </div>
                                  <div class="col-2 self-center text-center">
                                    <el-color-picker
                                      v-model="
                                        readingCard.colorMapConfig.BOOLEAN.max
                                          .bgColor
                                      "
                                      size="small"
                                      class="dashboard-color-picker"
                                    ></el-color-picker>
                                  </div>
                                </div>
                              </div>
                              <div
                                class="color-map-container"
                                v-if="
                                  readingCard.readings.readingField
                                    .dataTypeEnum === 'ENUM' &&
                                    readingCard.readings.readingField.enumMap &&
                                    Object.keys(
                                      readingCard.readings.readingField.enumMap
                                    ).length &&
                                    readingCard.colorMapConfig['ENUM']
                                "
                              >
                                <div class="row pB5">
                                  <div class="col-2">
                                    {{ $t('panel.dashboard.value') }}
                                  </div>
                                  <div class="col-6">
                                    {{ $t('panel.dashboard.label') }}
                                  </div>
                                  <div class="col-2 text-center">
                                    {{ $t('panel.dashboard.text') }}
                                  </div>
                                  <div class="col-2 text-center">Bg</div>
                                </div>
                                <template v-if="render">
                                  <div
                                    class="row pB5"
                                    v-for="(value, key) in readingCard
                                      .colorMapConfig['ENUM']"
                                    :key="key"
                                  >
                                    <div class="col-2 self-center">
                                      {{ key }}
                                    </div>
                                    <div class="col-6 self-center">
                                      <el-input
                                        placeholder="Please input"
                                        class="el-input-textbox-full-border fc-input-full-border-h35"
                                        v-model="value.label"
                                        :key="key"
                                      ></el-input>
                                    </div>
                                    <div class="col-2 self-center text-center">
                                      <el-color-picker
                                        v-model="value.textColor"
                                        size="small"
                                        :key="key"
                                        @change="$forceUpdate()"
                                        class="dashboard-color-picker"
                                      ></el-color-picker>
                                    </div>
                                    <div class="col-2 self-center text-center">
                                      <el-color-picker
                                        v-model="value.bgColor"
                                        size="small"
                                        :key="key"
                                        @change="$forceUpdate()"
                                        class="dashboard-color-picker"
                                      ></el-color-picker>
                                    </div>
                                  </div>
                                </template>
                              </div>
                              <div
                                class="color-map-container"
                                v-if="
                                  readingCard.readings.readingField
                                    .dataTypeEnum === 'NUMBER' &&
                                    readingCard.colorMapConfig['NUMBER'] &&
                                    readingCard.colorMapConfig['NUMBER'].length
                                "
                              >
                                <div class="row pB5">
                                  <div class="col-3">
                                    {{ $t('panel.dashboard.mn') }}
                                  </div>
                                  <div class="col-3">
                                    {{ $t('panel.dashboard.mx') }}
                                  </div>
                                  <div class="col-3">
                                    {{ $t('panel.dashboard.label') }}
                                  </div>
                                  <div class="col-1 text-center">
                                    {{ $t('panel.dashboard.text') }}
                                  </div>
                                  <div class="col-1 text-center">Bg</div>
                                </div>
                                <template v-if="render">
                                  <div
                                    class="row pB10 visibility-visible-actions"
                                    v-for="(value, key) in readingCard
                                      .colorMapConfig['NUMBER']"
                                    :key="key"
                                  >
                                    <div class="col-3 self-center">
                                      <el-input-number
                                        v-model="value.min"
                                        size="mini"
                                      ></el-input-number>
                                    </div>
                                    <div class="col-3 self-center">
                                      <el-input-number
                                        v-model="value.max"
                                        size="mini"
                                      ></el-input-number>
                                    </div>
                                    <div class="col-3 self-center">
                                      <el-input
                                        placeholder="Please input"
                                        class="el-input-textbox-full-border fc-input-full-border-h35"
                                        v-model="value.label"
                                        :key="key"
                                      ></el-input>
                                    </div>
                                    <div class="col-1 self-center text-center">
                                      <el-color-picker
                                        v-model="value.textColor"
                                        size="small"
                                        :key="key"
                                        @change="$forceUpdate()"
                                        class="dashboard-color-picker"
                                      ></el-color-picker>
                                    </div>
                                    <div class="col-1 self-center text-center">
                                      <el-color-picker
                                        v-model="value.bgColor"
                                        size="small"
                                        :key="key"
                                        @change="$forceUpdate()"
                                        class="dashboard-color-picker"
                                      ></el-color-picker>
                                    </div>
                                    <div
                                      class="col-1 self-center text-center nowrap visibility-hide-actions"
                                      v-bind:class="{
                                        pL0: !key,
                                        'text-left': !key,
                                      }"
                                    >
                                      <span
                                        class="add-icon"
                                        @click="addColorMapElemnt()"
                                      >
                                        <i
                                          class="el-icon-circle-plus-outline"
                                        ></i>
                                      </span>
                                      <span
                                        class="delete-icon"
                                        @click="removeColorMapElemnt(key)"
                                        v-if="key"
                                      >
                                        <i class="el-icon-remove-outline"></i>
                                      </span>
                                    </div>
                                  </div>
                                </template>
                              </div>
                              <div
                                class="color-map-container"
                                v-if="
                                  readingCard.readings.readingField
                                    .dataTypeEnum === 'DECIMAL' &&
                                    readingCard.colorMapConfig['DECIMAL'] &&
                                    readingCard.colorMapConfig['DECIMAL'].length
                                "
                              >
                                <div class="row pB5">
                                  <div class="col-3">
                                    {{ $t('panel.dashboard.mn') }}
                                  </div>
                                  <div class="col-3">
                                    {{ $t('panel.dashboard.mx') }}
                                  </div>
                                  <div class="col-3">
                                    {{ $t('panel.dashboard.label') }}
                                  </div>
                                  <div class="col-1 text-center">
                                    {{ $t('panel.dashboard.text') }}
                                  </div>
                                  <div class="col-1 text-center">Bg</div>
                                </div>
                                <template v-if="render">
                                  <div
                                    class="row pB10 visibility-visible-actions"
                                    v-for="(value, key) in readingCard
                                      .colorMapConfig['DECIMAL']"
                                    :key="key"
                                  >
                                    <div class="col-3 self-center">
                                      <el-input-number
                                        v-model="value.min"
                                        size="mini"
                                      ></el-input-number>
                                    </div>
                                    <div class="col-3 self-center">
                                      <el-input-number
                                        v-model="value.max"
                                        size="mini"
                                      ></el-input-number>
                                    </div>
                                    <div class="col-3 self-center">
                                      <el-input
                                        placeholder="Please input"
                                        class="el-input-textbox-full-border fc-input-full-border-h35"
                                        v-model="value.label"
                                        :key="key"
                                      ></el-input>
                                    </div>
                                    <div class="col-1 self-center text-center">
                                      <el-color-picker
                                        v-model="value.textColor"
                                        size="small"
                                        :key="key"
                                        @change="$forceUpdate()"
                                        class="dashboard-color-picker"
                                      ></el-color-picker>
                                    </div>
                                    <div class="col-1 self-center text-center">
                                      <el-color-picker
                                        v-model="value.bgColor"
                                        size="small"
                                        :key="key"
                                        @change="$forceUpdate()"
                                        class="dashboard-color-picker"
                                      ></el-color-picker>
                                    </div>
                                    <div
                                      class="col-1 self-center text-center nowrap visibility-hide-actions"
                                      v-bind:class="{
                                        pL0: !key,
                                        'text-left': !key,
                                      }"
                                    >
                                      <span
                                        class="add-icon"
                                        @click="addColorMapElemntforDecimal()"
                                      >
                                        <i
                                          class="el-icon-circle-plus-outline"
                                        ></i>
                                      </span>
                                      <span
                                        class="delete-icon"
                                        @click="
                                          removeColorMapElemntDecimal(key)
                                        "
                                        v-if="key"
                                      >
                                        <i class="el-icon-remove-outline"></i>
                                      </span>
                                    </div>
                                  </div>
                                </template>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <span slot="footer" class="modal-dialog-footer">
                        <el-button
                          @click="cancelBuilding()"
                          class="modal-btn-cancel"
                          >{{ $t('panel.share.cancel') }}</el-button
                        >
                        <el-button
                          type="primary"
                          @click="addreadings()"
                          class="modal-btn-save"
                          >{{ $t('panel.share.confirm') }}</el-button
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
                            {{ $t('panel.dashboard.sel_ahu') }}
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
                                $route.query.showallcards &&
                                  $account.org.id !== 135
                              "
                            ></el-option>
                          </el-select>
                        </div>
                        <div class="reading-card-container">
                          <div class="reading-card-header">
                            {{ $t('panel.dashboard.sel_asset') }}
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
                          >{{ $t('panel.share.cancel') }}</el-button
                        >
                        <el-button
                          type="primary"
                          @click="addAHUData()"
                          class="modal-btn-save"
                          >{{ $t('panel.dashboard.confirm') }}</el-button
                        >
                      </span>
                    </el-dialog>
                    <f-photoUploader
                      :module="
                        currentModuleName ? currentModuleName.module : ''
                      "
                      :dialogVisible.sync="showAvatarUpload"
                      @image-updated="avatarCropSuccess"
                      @input="getInput"
                      @upload-done="avatarCropUploadSuccess"
                      @upload-failed="avatarCropUploadFail"
                      :widget="editwidgetData"
                      :editwidget="editwidgetcalled"
                      @update="imageCardUpdate"
                    ></f-photoUploader>
                    <textCard
                      :visibility.sync="showtextcard"
                      @update="updatetextcard"
                      @add="addtextcard"
                      :widget="editwidgetData"
                      @close="closetextcard"
                    >
                    </textCard>
                    <f-card-builder
                      :visibility.sync="showCardBuilder"
                      @close="cancelBuilding"
                      @save="addAssetCard"
                      :addAssetCard="assetCardData"
                      :assetcardEdit="assetcardEdit"
                      @update="updateAssetCardData"
                    >
                    </f-card-builder>
                    <f-fcu-card
                      :visibility.sync="showFcuCardDialog"
                      @close="cancelBuilding"
                      @update="updatFacuCard"
                      @save="saveFcuCard"
                      :cardData="fcuCardData"
                    ></f-fcu-card>
                    <f-boolean-card-dialog
                      :visibility.sync="showBooleanCardDialog"
                      @close="cancelBuilding"
                      @update="updatFacuCard"
                      @save="saveBooleancard"
                      :cardData="booleancardData"
                    ></f-boolean-card-dialog>
                    <f-alarmbar-card-dialog
                      :visibility.sync="showAlarmbarCardDialog"
                      @close="cancelBuilding"
                      @update="updatFacuCard"
                      @save="saveAlarmcard"
                      :cardData="alarmcardData"
                    >
                    </f-alarmbar-card-dialog>
                    <kpi-card-setup
                      v-if="showkpiCard"
                      :visibility.sync="showkpiCard"
                      @close="cancelBuilding"
                      @save="cardBuilder"
                      @update="updateKpiCard"
                      :kpiCard="kpiCard"
                    ></kpi-card-setup>
                    <kpi-target-card
                      v-if="showkpitargetCard"
                      :visibility.sync="showkpitargetCard"
                      @close="cancelBuilding"
                      @save="cardBuilder"
                      @update="updateKpiCard"
                      :cardData="kpiCard"
                    ></kpi-target-card>
                    <dashbaord-dialog
                      v-if="buildingCard"
                      :visibility.sync="buildingCard"
                      @close="cancelBuilding"
                      @save="cardBuilder"
                      @update="updateKpiCard"
                      :buildings="buildings"
                      :cardData="kpiCard"
                      :type="newDashboardData.childKey"
                    ></dashbaord-dialog>
                    <target-meter-dialog
                      v-if="targetMeterDialog"
                      :visibility.sync="targetMeterDialog"
                      @close="cancelBuilding"
                      @save="cardBuilder"
                      @update="updateKpiCard"
                      :buildings="buildings"
                      :cardData="kpiCard"
                      :type="newDashboardData.childKey"
                    ></target-meter-dialog>
                    <gauge-card-setup
                      v-if="gaugeSetup"
                      :visibility.sync="gaugeSetup"
                      @close="cancelBuilding"
                      @save="cardBuilder"
                      @update="updateKpiCard"
                      :buildings="buildings"
                      :cardData="kpiCard"
                      :type="newDashboardData.childKey"
                    ></gauge-card-setup>
                    <pmreading-card-setup
                      v-if="pmreadingsetup"
                      :visibility.sync="pmreadingsetup"
                      @close="cancelBuilding"
                      @save="cardBuilder"
                      @update="updateKpiCard"
                      :buildings="buildings"
                      :cardData="kpiCard"
                      :type="newDashboardData.childKey"
                    ></pmreading-card-setup>
                    <smart-map-setup
                      v-if="smartmapsetup"
                      :visibility.sync="smartmapsetup"
                      @close="cancelBuilding"
                      @save="cardBuilder"
                      @update="updateKpiCard"
                      :buildings="buildings"
                      :cardData="kpiCard"
                      :type="newDashboardData.childKey"
                    ></smart-map-setup>
                    <smart-table-setup
                      :visibility.sync="smarttablesetup"
                      v-if="smarttablesetup"
                      @close="cancelBuilding"
                      @save="cardBuilder"
                      @update="updateKpiCard"
                      :buildings="buildings"
                      :cardData="kpiCard"
                      :type="newDashboardData.childKey"
                    ></smart-table-setup>
                    <smart-table-wrapper-setup
                      :visibility.sync="smarttablewrappersetup"
                      v-if="smarttablewrappersetup"
                      @close="cancelBuilding"
                      @save="cardBuilder"
                      @update="updateKpiCard"
                      :buildings="buildings"
                      :cardData="kpiCard"
                      :type="newDashboardData.childKey"
                    ></smart-table-wrapper-setup>
                    <smart-card-setup
                      :visibility.sync="smartcardsetup"
                      v-if="smartcardsetup"
                      @close="cancelBuilding"
                      @save="cardBuilder"
                      @update="updateKpiCard"
                      :buildings="buildings"
                      :cardData="kpiCard"
                      :type="newDashboardData.childKey"
                    ></smart-card-setup>
                  </div>
                </div>
                <!-- <div class="row" v-if="!dashboardLayout.length">
              <div class="col-6">
                <div class="self-center empty-drop-box">
                  <div class="header">{{ $t('panel.dashboard.dg_dp') }}</div>
                  <div class="subheader">
                    {{ $t('panel.dashboard.ht_and_wth') }}
                  </div>
                </div>
              </div>
            </div> -->
                <div>
                  <!-- <div class="bg-grids">
                <grid-layout
                  :layout.sync="bglayout"
                  :col-num="24"
                  :row-height="40"
                  :is-draggable="true"
                  :is-resizable="true"
                  :is-mirrored="false"
                  :vertical-compact="true"
                  :margin="[10, 10]"
                  :isDraggable="false"
                  :isResizable="true"
                  :use-css-transforms="true"
                >
                  <grid-item
                    v-for="item in bglayout"
                    :x="item.x"
                    :y="item.y"
                    :w="item.w"
                    :h="item.h"
                    :i="item.i"
                    :key="item.i"
                  >
                    <div
                      class="grid-cells"
                      :style="{ height: '100%', width: '100%' }"
                    ></div>
                  </grid-item>
                </grid-layout>
              </div> -->
                  <div>
                    <!-- <div class="main-grids"> -->
                    <grid-layout
                      :layout.sync="dashboardLayout"
                      :col-num="96"
                      :rowHeight="height"
                      :is-draggable="editMode"
                      :is-resizable="editMode"
                      :use-css-transforms="true"
                      :margin="[margin, margin]"
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
                        <CardWrapper
                          v-if="item.widget.type === 'card'"
                          :key="`newCard-${index}`"
                          :ref="'widget[' + index + ']'"
                          :widget="item.widget"
                          :widgetConfig="item"
                          :dashboardId="dashboard ? dashboard.id : null"
                          :showRemove="true"
                          :showEdit="true"
                          @removeCard="deleteCard"
                          @editCard="editCard"
                          @duplicate="duplicate"
                          @helpTextConfig="openHelpTextConfig"
                        ></CardWrapper>
                        <f-widget
                          v-else
                          :ref="'widget[' + index + ']'"
                          :type="item.widget.type"
                          :selectedWidgetId="selectedWidgetId"
                          :editwidget="editwidgetcalled"
                          :widget="item.widget"
                          :grid="item"
                          :key="index"
                          :rowHeight="height"
                          :dashboard="dashboard ? dashboard.id : null"
                          @deletechart="deleteChart(index)"
                          @widget="updatewidget, getIndex(index)"
                          :mode="editMode"
                          @editwidget="editwidget(index)"
                          @helpTextConfig="openHelpTextConfig"
                        ></f-widget>
                      </grid-item>
                    </grid-layout>
                  </div>
                </div>
              </div>
              <div v-if="editMode">
                <new-chart-widget
                  :dashboardId="dashboard ? dashboard.id : null"
                  ref="newChartWidget"
                  :moduleName="moduleName"
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
                >
                </new-prebuilt-widget>
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
                        <el-radio v-model="shareTo" :label="1">{{
                          $t('panel.dashboard.only_me')
                        }}</el-radio>
                        <el-radio v-model="shareTo" :label="2">{{
                          $t('panel.dashboard.everyone')
                        }}</el-radio>
                        <el-radio v-model="shareTo" :label="3">{{
                          $t('panel.dashboard.specific')
                        }}</el-radio>
                      </div>
                    </el-col>
                  </el-row>
                  <el-row
                    v-if="shareTo === 3"
                    align="middle"
                    style="margin:0px;padding-top:20px;"
                    :gutter="20"
                  >
                    <el-col
                      :span="24"
                      style="padding-right: 0px;padding-left:0px;"
                    >
                      <div class="textcolor">
                        {{ $t('panel.dashboard.users') }}
                      </div>
                      <div class="add">
                        <el-select
                          v-model="sharedUsers"
                          multiple
                          style="width:100%"
                          class="form-item"
                          :placeholder="$t('common.wo_report.choose_users')"
                        >
                          <el-option
                            v-for="user in users"
                            :key="user.id"
                            :label="user.name"
                            :value="user.id"
                          >
                          </el-option>
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
                    <el-col
                      :span="24"
                      style="padding-right:0px;padding-left:0px;"
                    >
                      <div class="textcolor">
                        {{ $t('panel.dashboard.roles') }}
                      </div>
                      <div class="add">
                        <el-select
                          v-model="sharedRoxles"
                          multiple
                          style="width:100%"
                          class="form-item"
                          :placeholder="$t('common.wo_report.choose_roles')"
                        >
                          <el-option
                            v-for="role in roles"
                            :key="role.id"
                            :label="role.name"
                            :value="role.id"
                          >
                          </el-option>
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
                    <el-col
                      :span="24"
                      style="padding-right: 0px;padding-left:0px;"
                    >
                      <div class="textcolor">
                        {{ $t('panel.dashboard.teams') }}
                      </div>
                      <div class="add">
                        <el-select
                          v-model="sharedGroups"
                          multiple
                          style="width:100%"
                          class="form-item"
                          :placeholder="$t('common.wo_report.choose_teams')"
                        >
                          <el-option
                            v-for="group in groups"
                            :key="group.id"
                            :label="group.name"
                            :value="group.id"
                          >
                          </el-option>
                        </el-select>
                      </div>
                    </el-col>
                  </el-row>
                  <span slot="footer" class="dialog-footer">
                    <el-button @click="sharingDialogVisible = false">{{
                      $t('panel.dashboard.cancel')
                    }}</el-button>
                    <el-button type="primary" @click="applySharing">{{
                      $t('panel.dashboard.confirm')
                    }}</el-button>
                  </span>
                </el-dialog>
                <!-- <dashboard-tab-editor
                  v-if="dashboardTabEditorVisibility"
                  :visibility.sync="dashboardTabEditorVisibility"
                  :dashboardObject="dashboard"
                ></dashboard-tab-editor> -->
                <Dashboard-tab-dialog-editor
                  v-if="dashboardTabEditorVisibility"
                  :visibility.sync="dashboardTabEditorVisibility"
                  :dashboardObject="dashboard"
                >
                </Dashboard-tab-dialog-editor>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <NewCardBuilder
      v-if="showCardBuilderSetup"
      @close="onBuilderClosed"
      @save="onCardSave"
      @duplicate="onCardDuplicate"
      @update="onCardUpdate"
      :cardData="selectedCardData"
      :isDuplicate="isDuplicate"
      :type="newDashboardData.childKey"
    >
    </NewCardBuilder>
    <DashboardCompoents
      v-if="showCompoents"
      @close="closeCompoent()"
      @resetreportData="resetreportData()"
      @addElement="addDashboardElement"
      @expand="expand"
      @option="setoption"
      @setModuleName="setModuleName"
      :data="componentData"
      :reportLoading="reportLoading"
    >
    </DashboardCompoents>
    <el-dialog
      title="Add Dashboard Folder"
      :visible.sync="showAddDashboardfolder"
      width="30%"
      :before-close="closeAddDashboard"
    >
      <div class="height150">
        <el-input
          :autofocus="true"
          class="create-dashboard-input-title fc-input-full-border2"
          v-model="newDashboardName"
          placeholder="Enter the Folder Name"
        ></el-input>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeAddDashboard()"
          >Cancel</el-button
        >
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="addDashbopardFolder()"
          >Save</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      title="Disable Dashboard Tab"
      :visible.sync="EnableDashboardOptions"
      v-if="EnableDashboardOptions"
      :close-on-click-modal="false"
      :show-close="false"
      width="30%"
    >
      <div class="height150">
        <el-checkbox v-model="copyallWidgets">
          Copy tab widgets to main dashboard</el-checkbox
        >
        <div class="pT15" v-if="copyallWidgets">
          <el-row class="add-dashboard-popper">
            <el-col :span="24" class="mB10">{{ 'Tab Name' }}</el-col>
            <el-col :span="24">
              <el-select
                class="el-input-textbox-full-border"
                v-model="dashboardTabforWidgets"
                placeholder="Please select a building"
              >
                <el-option
                  :label="tab.name"
                  :value="tab.id"
                  v-for="(tab, ix) in dashboardTabsList"
                  :key="ix"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDashboardOption(false)"
          >Cancel</el-button
        >
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="saveDisabledDashboardOptions()"
          >Confirm</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      title="Enable Dashboard Tab"
      :visible.sync="disableDashboardOptions"
      width="30%"
      v-if="disableDashboardOptions"
      :close-on-click-modal="false"
      :show-close="false"
    >
      <div class="height150">
        <el-row class="add-dashboard-popper">
          <el-col :span="24" class="mB10">{{ 'Tab Name' }}</el-col>
          <el-col :span="24">
            <el-input
              v-model="newDashboardTabName"
              class="fc-input-full-border2"
              placeholder="Enter Tab Name"
            ></el-input>
          </el-col>
        </el-row>
        <div class="pT15">
          <el-checkbox v-model="copyallWidgets"
            >Copy all the widgets to new tab</el-checkbox
          >
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDashboardOption(false)"
          >Cancel</el-button
        >
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="saveEnabledDashboardOptions()"
          >{{ newDashboardTabName ? 'Save' : `Confirm` }}</el-button
        >
      </div>
    </el-dialog>
    <WidgetHelpTextConfig
      v-if="showWidgetHelpTextConfig"
      :visibility.sync="showWidgetHelpTextConfig"
      :widget="widgetForHelpTextConfig"
      @helpTextChanged="handleHelpTextChange"
    />
  </div>
</template>

<script>
import FWidget from './widget/FWidget'
import NewChartWidget from './forms/NewChartWidget'
import textCard from '@/TextCardPopup'
import NewListWidget from './forms/NewListWidget'
import NewMapWidget from './forms/NewMapWidget'
import NewPrebuiltWidget from './forms/NewPrebuiltWidget'
import NewWebWidget from './forms/NewWebWidget'
import FAddDataPoint from 'pages/energy/analytics/components/FAddDataPoint'
import { GridLayout, GridItem } from 'vue-grid-layout'
import DashboardFilter from './DashboardFilter'
import ReportHelper from 'pages/report/mixins/ReportHelper'
import DateUtil from '@/mixins/DateHelper'
import FPhotoUploader from '@/FPhotoUploader'
import FCardBuilder from '@/CardBuilder'
import FFcuCard from '@/FCUCardDialog'
import FBooleanCardDialog from '@/FBooleanCardDialog'
import FAlarmbarCardDialog from '@/FAlarmBarCardDialog'
import DashboardHelper from 'pages/dashboard/mixins/DashboardHelper'
import kpiCardSetup from '@/KFICardbuilderDialog'
import DashbaordDialog from '@/DashboardCardDialogs'
import TargetMeterDialog from '@/TargetMeterDialog'
// import DashboardTabEditor from './DashboardTabEditor'
import DashboardTopTab from './DashboardTopTab'
import DashboardTabDialogEditor from './DashboardTabDialogEditor'
import kpiTargetCard from '@/KPITargetMeterDialog'
import GaugeCardSetup from '@/GaugeCardSetup'
import PmreadingCardSetup from '@/PmreadingCardSetup'
import SmartMapSetup from '@/SmartMapSetup'
import SmartTableSetup from '@/SmartTableSetup'
import SmartTableWrapperSetup from '@/SmartTableWrapperSetup'
import SmartCardSetup from '@/SmartCardSetup'
import NewCardBuilder from 'pages/card-builder/CardBuilder'
import CardBuilderDashboardMixin from 'pages/card-builder/components/CardBuilderDashboardMixin'
import CardWrapper from 'pages/card-builder/components/CardWrapper'
import { isEmpty } from '@facilio/utils/validation'
import { mapState, mapGetters } from 'vuex'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'
import DashboardCompoents from 'pages/dashboard/DashboardCompoents/ComponentChooser'
import DashboardComponentMixins from 'pages/dashboard/DashboardCompoents/DashboardComponentMixins'
import { getBaseURL } from 'util/baseUrl'
import WidgetHelpTextConfig from 'pages/dashboard/widget/WidgetHelpTextConfig'
import { API } from '@facilio/api'

export default {
  mixins: [
    ReportHelper,
    DateUtil,
    DashboardHelper,
    CardBuilderDashboardMixin,
    DashboardComponentMixins,
  ],
  props: ['sites', 'currentDashboard'],
  components: {
    DashboardCompoents,
    NewChartWidget,
    NewListWidget,
    NewMapWidget,
    NewPrebuiltWidget,
    NewWebWidget,
    FWidget,
    GridLayout,
    GridItem,
    DashboardFilter,
    FAddDataPoint,
    FPhotoUploader,
    FCardBuilder,
    FFcuCard,
    FBooleanCardDialog,
    FAlarmbarCardDialog,
    kpiCardSetup,
    // DashboardTabEditor,
    DashbaordDialog,
    TargetMeterDialog,
    kpiTargetCard,
    GaugeCardSetup,
    PmreadingCardSetup,
    SmartMapSetup,
    SmartTableSetup,
    SmartTableWrapperSetup,
    SmartCardSetup,
    NewCardBuilder,
    CardWrapper,
    textCard,
    DashboardTabDialogEditor,
    DashboardTopTab,
    WidgetHelpTextConfig,
  },
  data() {
    return {
      render: true,
      selectedModule: 'Maintenance',
      colorMapSupported: {
        ENUM: 'ENUM',
        BOOLEAN: 'BOOLEAN',
        DECIMAL: 'DECIMAL',
        NUMBER: 'NUMBER',
      },
      folderLoading: true,
      moduleName: 'workorder',
      loading: true,
      dialogVisible: false,
      buildingCard: false,
      gaugeSetup: false,
      pmreadingsetup: false,
      smartmapsetup: false,
      smarttablesetup: false,
      smarttablewrappersetup: false,
      smartcardsetup: false,
      showCardBuilderSetup: false,
      targetMeterDialog: false,
      editwidgetcalled: false,
      editwidgetData: null,
      selectedWidgetId: -1,
      readingpopup: false,
      webcardVisible: false,
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
        editmode: false,
        parentName1: '',
        colorMap: false,
        colorMapConfig: {},
        write: false,
        readingType: -1,
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
          enableCenterText1: true,
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
      selectedDashoardFolder: {
        id: -1,
        name: 'default',
      },
      dashboardFolderList: [],
      convertedlayout: null,
      chartDeleted: false,
      newDashboardData: [],
      buildings: [],
      tempBuildings: [],
      allBuildings: [],
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
      customModuleList: [],
      supportedModules: [
        {
          label: 'Work Orders',
          value: 'workorder',
          list: [],
          expand: false,
          license: 'MAINTENANCE',
        },
        {
          label: 'Planned Maintenance',
          value: 'preventivemaintenance',
          list: [],
          expand: false,
        },
        {
          label: 'Alarms',
          value: this.$helpers.isLicenseEnabled('NEW_ALARMS')
            ? 'newreadingalarm'
            : 'alarm',
          list: [],
          expand: false,
          license: 'ALARMS',
        },
        {
          label: 'Assets',
          value: 'asset',
          list: [],
          expand: false,
          license: 'SPACE_ASSET',
        },
        {
          label: 'Inventory Request',
          value: 'inventoryrequest',
          list: [],
          expand: false,
          license: 'INVENTORY',
        },
        {
          label: 'Item',
          value: 'item',
          list: [],
          expand: false,
          license: 'INVENTORY',
        },
        {
          label: 'Contracts',
          value: 'contracts',
          list: [],
          expand: false,
          license: 'CONTRACT',
        },
        {
          label: 'Purchaseorder',
          value: 'purchaseorder',
          list: [],
          expand: false,
          license: 'PURCHASE',
        },
        {
          label: 'Purchaserequest',
          value: 'purchaserequest',
          list: [],
          expand: false,
          license: 'PURCHASE',
        },
        {
          label: 'Visitor',
          value: 'visitor',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Custom module',
          value: 'custom',
          list: [],
          expand: false,
          license: 'CONNECTEDAPPS',
        },
        {
          label: 'Visits',
          value: 'visitorlog',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'contact',
          value: 'contact',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Watchlist',
          value: 'watchlist',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Visitor invite',
          value: 'invitevisitor',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Tenants',
          moduleName: 'tenant',
          list: [],
          expand: false,
          license: 'TENANTS',
        },
      ],
      wocards: [
        {
          disbale: this.$account.org.id === 262 ? false : 'true',
          key: 'kpiCard',
          childKey: 'multiTrend',
          label: 'Multi Trend',
          w: 48,
          h: 24,
        },
        {
          disbale:
            this.$route.query && this.$route.query.showallcards
              ? false
              : 'true',
          key: 'kpiCard',
          childKey: 'linearCard',
          label: 'Linear Gauge Card',
          w: 29,
          h: 27,
        },
        {
          disbale:
            this.$route.query && this.$route.query.showallcards
              ? false
              : 'true',
          key: 'kpiCard',
          childKey: 'radialCard',
          label: 'Radial Gauge Card',
          w: 29,
          h: 27,
        },
        {
          key: 'kpiCard',
          label: 'KPI Card',
          w: 12,
          h: 12,
        },
        {
          // disbale:
          //   this.$route.query && this.$route.query.showallcards
          //     ? false
          //     : 'true',
          key: 'kpiCard',
          childKey: 'kpitargetCard',
          label: 'Target KPI Card',
          w: 24,
          h: 24,
        },
        {
          key: 'textcard',
          label: 'Text card',
          w: 48,
          h: 24,
        },
        {
          key: 'imagecard',
          label: 'Image card',
          w: 20,
          h: 20,
        },
        {
          key: 'web',
          label: 'Web Card',
          w: 48,
          h: 24,
        },
      ],
      cards: [
        {
          disbale: this.$account.org.id !== 146 ? false : 'true',
          key: 'kpiCard',
          childKey: 'smarttable',
          label: 'Smart Table',
          w: 24,
          h: 12,
        },
        // {
        //   disbale: this.$account.org.id !== 146 ? false : 'true',
        //   key: 'kpiCard',
        //   childKey: 'smarttablewrapper',
        //   label: 'Smart Table Wrapper',
        //   w: 24,
        //   h: 12,
        // },
        {
          disbale: this.$account.org.id !== 146 ? false : 'true',
          key: 'kpiCard',
          childKey: 'smartcard',
          label: 'Smart Card',
          w: 24,
          h: 12,
        },
        {
          disbale:
            this.$route.query && this.$route.query.showallcards
              ? false
              : 'true',
          key: 'kpiCard',
          childKey: 'smartnewmap',
          label: 'Smart New Map',
          w: 24,
          h: 12,
        },
        {
          key: 'kpiCard',
          childKey: 'pmreadingswidget',
          label: 'Pm Readings Widget',
          w: 24,
          h: 12,
        },
        {
          disbale:
            this.$route.query && this.$route.query.showallcards
              ? false
              : 'true',
          key: 'kpiCard',
          childKey: 'smartenergymap',
          label: 'Smart Energy Map',
          w: 24,
          h: 12,
        },
        {
          disbale:
            this.$route.query && this.$route.query.showallcards
              ? false
              : 'true',
          key: 'kpiCard',
          childKey: 'smartmap',
          label: 'Smart Map',
          w: 24,
          h: 12,
        },
        {
          disbale:
            this.$route.query && this.$route.query.showallcards
              ? false
              : 'true',
          key: 'kpiMultiResultCard',
          label: 'KPI Multi Result Card',
          w: 12,
          h: 12,
        },
        {
          // disbale:
          //   this.$route.query && this.$route.query.showallcards
          //     ? false
          //     : 'true',
          disbale: this.$account.org.id !== 146 ? false : 'true',

          key: 'resourceAlarmBar',
          label: 'Boolean Card',
          w: 12,
          h: 12,
        },
        {
          disbale: this.$account.org.id !== 146 ? false : 'true',
          key: 'alarmbarwidget',
          label: 'Alarm Card',
          w: 12,
          h: 12,
        },
        {
          disbale: this.$account.org.id === 169 ? false : 'true',
          key: 'emrillFcu',
          label: 'FCU Status',
          w: 20,
          h: 16,
        },
        {
          disbale: this.$account.org.id === 169 ? false : 'true',
          key: 'emrillFcuList',
          label: 'FCU List',
          w: 48,
          h: 16,
        },
        {
          // disbale: (this.$route.query && this.$route.query.showallcards) ? false : 'true',
          key: 'readingComboCard',
          label: 'Card builder',
          w: 12,
          h: 12,
        },
        {
          key: 'readingcard',
          label: 'Reading card',
          w: 16,
          h: 12,
        },
        {
          disbale:
            this.$route.query && this.$route.query.showallcards
              ? false
              : 'true',
          key: 'kpiCard',
          childKey: 'targetmeter',
          label: 'Target Meter',
          w: 24,
          h: 24,
        },
        {
          key: 'textcard',
          label: 'Text card',
          w: 48,
          h: 24,
        },
        {
          key: 'imagecard',
          label: 'Image card',
          w: 20,
          h: 20,
        },
        {
          key: 'web',
          label: 'Web',
          w: 48,
          h: 24,
        },
        {
          disbale: this.$account.org.id !== 135 ? 'true' : false,
          key: 'fahuStatusCard',
          label: 'AHU Status',
          w: 12,
          h: 12,
        },
        {
          disbale: this.$account.org.id !== 146 ? 'true' : false,
          key: 'fahuStatusCardNew',
          label: 'AHU Status',
          w: 12,
          h: 12,
        },
        {
          disbale:
            this.$route.query &&
            this.$route.query.showallcards &&
            this.$account.org.id !== 135
              ? false
              : 'true',
          key: 'fahuStatusCard3',
          label: 'AHU Status',
          w: 12,
          h: 12,
        },
        {
          disbale: this.$account.org.id !== 133 ? 'true' : false,
          key: 'fahuStatusCard1',
          label: 'AHU Status',
          w: 12,
          h: 12,
        },
        {
          key: 'weathermini',
          label: 'Weather',
          w: 24,
          h: 12,
        },
        {
          key: 'kpiCard',
          childKey: 'carbonmini',
          label: 'Carbon Emission',
          w: 24,
          h: 12,
        },
        {
          key: 'kpiCard',
          childKey: 'energycostmini',
          label: 'Energy cost',
          w: 24,
          h: 12,
        },
        {
          key: 'kpiCard',
          childKey: 'controlCommandmini',
          label: 'Control Command',
          w: 24,
          h: 12,
        },
        {
          key: 'kpiCard',
          childKey: 'profilemini',
          label: 'Energy',
          w: 24,
          h: 12,
        },
      ],
      staticWidgets: [
        {
          key: 'profilecard',
          label: 'Profile Card',
          w: 24,
          h: 24,
        },
        {
          key: 'energycard',
          label: 'Energy Card',
          w: 24,
          h: 24,
        },
        {
          key: 'energycost',
          label: 'Energy Cost',
          w: 24,
          h: 24,
        },
        {
          key: 'energycostaltayer',
          label: 'Energy Saving',
          w: 24,
          h: 24,
        },
        {
          key: 'weathercard',
          label: 'Weather Card',
          w: 24,
          h: 24,
        },
        {
          key: 'weathercardaltayer',
          label: 'Weather saving Card',
          w: 24,
          h: 24,
        },
        {
          key: 'workordersummary',
          label: 'Workorder Summary',
          w: 24,
          h: 32,
        },
        {
          key: 'categories',
          label: 'Categories',
          w: 24,
          h: 32,
        },
        {
          key: 'technicians',
          label: 'Technicians',
          w: 24,
          h: 32,
        },
        {
          key: 'closedwotrend',
          label: 'Closed WO Trend',
          w: 24,
          h: 32,
        },
        {
          key: 'mywosummary',
          label: 'My Workorder Summary',
          w: 24,
          h: 32,
        },
        {
          key: 'openalarms',
          label: 'Open Alarms',
          w: 24,
          h: 32,
        },
        {
          key: 'buildingopenalarms',
          label: 'Building Open Alarms',
          w: 24,
          h: 32,
        },
        {
          key: 'mapwidget',
          label: 'Site Map Widget',
          w: 64,
          h: 32,
        },
        {
          key: 'buildingmapwidget',
          label: 'Building Map Widget',
          w: 64,
          h: 32,
        },
        {
          key: 'sampleppm',
          label: 'PPM Low to Moderate Risk Scoresheet',
          w: 96,
          h: 32,
        },
        {
          key: 'utsdata',
          label: 'uts',
          w: 96,
          h: 40,
        },
        {
          disbale: this.$account.org.id === 169 ? false : 'true',
          key: 'emrillFcu',
          label: 'FCU Status',
          w: 20,
          h: 16,
        },
        {
          disbale: this.$account.org.id === 169 ? false : 'true',
          key: 'emrillFcuList',
          label: 'FCU List',
          w: 48,
          h: 16,
        },
      ],
      emptywidget: false,
      isDraging: false,
      shareTo: 2,
      showkpiCard: false,
      showkpitargetCard: false,
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
      modules: [
        {
          label: 'Maintenance',
          moduleName: 'workorder',
          license: 'MAINTENANCE',
        },
        {
          label: 'Fault detection and diagnostics',
          moduleName: 'alarm',
          license: 'ALARMS',
        },
        {
          label: 'Building performance',
          moduleName: 'energydata',
          license: 'ENERGY',
        },
        {
          label: 'Asset',
          moduleName: 'asset',
          license: 'SPACE_ASSET',
        },
        {
          label: 'Inventory Request',
          moduleName: 'inventoryrequest',
          list: [],
          expand: false,
          license: 'INVENTORY',
        },
        {
          label: 'Item',
          moduleName: 'item',
          list: [],
          expand: false,
          license: 'INVENTORY',
        },
        {
          label: 'Contracts',
          moduleName: 'contracts',
          list: [],
          expand: false,
          license: 'CONTRACT',
        },
        {
          label: 'Purchaseorder',
          moduleName: 'purchaseorder',
          list: [],
          expand: false,
          license: 'PURCHASE',
        },
        {
          label: 'Purchaserequest',
          moduleName: 'purchaserequest',
          list: [],
          expand: false,
          license: 'PURCHASE',
        },
        {
          label: 'Visitor',
          moduleName: 'visitor',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Custom module',
          moduleName: 'custom',
          list: [],
          expand: false,
          license: 'CONNECTEDAPPS',
        },
        {
          label: 'Visits',
          moduleName: 'visitorlog',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'contact',
          moduleName: 'contact',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Watchlist',
          moduleName: 'watchlist',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Visitor invite',
          moduleName: 'invitevisitor',
          list: [],
          expand: false,
          license: 'VISITOR',
        },
        {
          label: 'Tenants',
          moduleName: 'tenant',
          list: [],
          expand: false,
          license: 'TENANTS',
        },
      ],
      dragedElement: 'test',
      showAvatarUpload: false,
      showtextcard: false,
      showCardBuilder: false,
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
      graphicsList: null,
      connectedAppWidgetList: null,
      dashboardTabContexts: null,
      dashboardTabLoading: null,
      dashboardTabsList: null,
      selectedTabId: null,
      showTemplateReportDialog: false,
      currentTemplateReportId: null,
      dashboardTabEditorVisibility: false,
      showWidgetHelpTextConfig: false,
      widgetForHelpTextConfig: null,
    }
  },
  computed: {
    ...mapState({
      groups: state => state.groups,
      users: state => state.users,
      roles: state => state.roles,
    }),

    ...mapGetters(['getCurrentUser']),

    getLicenseEnabledModules() {
      return this.modules.filter(module => {
        if (this.$helpers.isLicenseEnabled(module.license)) return module
      })
    },
    dashboardLink() {
      let dashboardlink =
        this.$attrs.dashboardlink || this.$route.params.dashboardlink

      if (
        [
          'residentialbuildingdashboard',
          'commercialbuildingdashboard',
        ].includes(dashboardlink)
      ) {
        return 'buildingdashboard'
      } else {
        return dashboardlink
      }
    },
    buildingId() {
      return this.$attrs.buildingid || this.$route.params.buildingid
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
    margin() {
      return document.getElementsByClassName('layout-page')[0].offsetWidth / 100
    },
    height() {
      if (this.collapseSidebar) {
        // return (document.getElementsByClassName('layout-page')[0].offsetWidth / 96) - ((document.getElementsByClassName('layout-page')[0].offsetWidth / 96) / 3)
        return (
          document.getElementsByClassName('layout-page')[0].offsetWidth / 10000
        )
      } else {
        let width =
          document.getElementsByClassName('layout-page')[0].offsetWidth -
          document.getElementsByClassName('layout-page')[0].offsetWidth / 4 +
          60
        return width / 10000
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
    dashboardTabId() {
      return this.$route.query && this.$route.query.tab
        ? parseInt(this.$route.query.tab)
        : null
    },
    newLayout() {
      if (this.$helpers.isLicenseEnabled('NEW_LAYOUT')) {
        return true
      }
      return false
    },
    canShowNewCardBuilder() {
      return true
    },
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadUsers')
  },
  mounted() {
    if (!this.$helpers.isLicenseEnabled('NEW_LAYOUT')) {
      this.moduleName = this.getCurrentModule().module
    }
    this.initData()
    this.loadNewDashboardFoler()
    if (this.$helpers.isLicenseEnabled('GRAPHICS')) {
      this.loadGraphicsList()
    }
  },
  beforeDestroy() {
    this.beforeDestroyHook()
  },
  watch: {
    dashboardLink: function() {
      this.loadFolder('watch')
    },
    buildingId: function() {
      this.loadFolder('watch')
    },
    $route: function() {
      this.openFirstReport()
    },
    dashboardTabId: function() {
      if (this.dashboardLayouts[this.dashboardTabId]) {
        this.dashboardLayout = this.dashboardLayouts[this.dashboardTabId]
      } else {
        this.loadDashboardTabWidgets()
      }
    },
  },
  methods: {
    handleHelpTextChange(widgetToChange) {
      // done to avoid prop mutation
      // Widget text edit component emits ,widget obj with helptext and settings
      // find same widget obj in layouyt and mutate it here
      this.showWidgetHelpTextConfig = false
      //To do: Handle for tabbed Dbs also
      if (this.dashboardLayout) {
        let gridItem = this.dashboardLayout.find(e => {
          return e?.widget.id == widgetToChange.id
        })

        if (gridItem?.widget) {
          gridItem.widget.widgetSettings = widgetToChange.widgetSettings
          gridItem.widget.helpText = widgetToChange.helpText
        }
      }
    },
    openHelpTextConfig(widget) {
      this.widgetForHelpTextConfig = widget
      this.showWidgetHelpTextConfig = true
    },
    cardbuilderadd() {
      let templateJson = {
        key: 'kpiCard',
        childKey: 'cardbuilder',
        label: 'Card Builder',
        w: 24,
        h: 12,
      }
      this.option = 'cards'
      this.start(templateJson)
      this.$nextTick(() => {
        this.drop()
      })
    },
    setModuleName(name) {
      // let moduleName = this.modules.find(rt => rt.label === name).moduleName
      this.moduleName = name
      Promise.all([
        this.loadNewReportTree(),
        this.loadReportTree(true),
      ]).then(() => {})
    },
    getModuleSpacificCards() {
      if (this.moduleName === 'workorder') {
        return this.wocards
      } else if (this.moduleName.indexOf('energy') > -1) {
        return this.cards
      } else {
        return this.wocards
      }
    },
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
      this.currentModuleName = 'workorder' // ******** need update
      Promise.all([
        this.loadNewReportTree(),
        this.loadReportTree(),
        this.getAllbuildings(),
        this.getFullbuildings(),
      ]).then(() => {
        self.loadFolder()
        self.loading = false
      })
      // this.moduleViews()
      this.dashbaordScreen()
    },
    resizecontrol(item) {
      if (this.editMode) {
        if (item.widget.type === 'card') {
          let cardContext = this.$getProperty(item, 'widget.dataOptions', null)

          if (isEmpty(cardContext)) return false
          return this.canResizeCard(cardContext)
        } else if (
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
    },
    allowResizewidget(key) {
      if (key) {
        if (key === 'textcard') {
          return true
        } else if (key === 'imagecard') {
          return true
        } else if (key === 'web') {
          return true
        } else if (key === 'readingComboCard') {
          return true
        } else if (key === 'kpiCard') {
          return true
        } else if (key === 'emrilllevel') {
          return true
        } else if (key === 'emrilllevel1List') {
          return true
        } else if (key === 'emrilllevel2List') {
          return true
        } else if (key === 'emrilllevel3List') {
          return true
        } else if (key === 'emrillFcuList') {
          return true
        } else if (key === 'resourceAlarmBar') {
          return true
        } else if (key === 'alarmbarwidget') {
          return true
        } else if (key === 'kpiMultiResultCard') {
          return true
        } else if (key.indexOf('tempcard') > -1) {
          return true
        } else {
          return false
        }
      }
      return false
    },
    getAllbuildings() {
      let self = this
      let params = {}
      return new Promise(resolve => {
        let currentModule = {
          module: this.moduleName,
        }
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
              self.tempBuildings = response.data.reportData.buildingDetails
              resolve(response)
            } else {
              self.buildings = response.data
            }
          })
          .catch(function() {})
      })
    },
    getFullbuildings() {
      let self = this
      let params = {}
      return new Promise(resolve => {
        let currentModule = this.moduleName
        let url = '/report/alarms/getAllBuildings'
        self.$http
          .post(url, params)
          .then(function(response) {
            if (currentModule.module === 'energydata') {
              self.allBuildings = response.data
              resolve(response)
            } else {
              self.allBuildings = response.data
            }
          })
          .catch(function() {})
      })
    },
    loadReportTree(loadOnlyTree) {
      let self = this
      let moduleName = this.moduleName
      this.reportLoading = true
      if (moduleName === 'energy') {
        moduleName = 'energyData'
      }
      if (moduleName !== 'custom') {
        self.$http
          .get(
            '/report/workorder/getAllWorkOrderReports?moduleName=' + moduleName
          )
          .then(function(response) {
            let data = response.data.allWorkOrderJsonReports
            let treeData = data.map(function(d) {
              d.expand = false
              d.reports.forEach(rt => {
                rt.selected = false
                rt.disabled = false
              })
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
            self.reportLoading = false
          })
          .catch(function(error) {
            if (error) {
              self.loading = false
            }
          })
      }
      if (loadOnlyTree !== true && this.mode !== 'new') {
        this.loadFolder('watch')
      }
    },
    loadNewReportTree() {
      let self = this
      this.reportLoading = true
      let moduleName = this.moduleName
      if (moduleName === 'energy') {
        moduleName = 'energyData'
      }else if(moduleName === 'newreadingalarm' || moduleName === 'bmsalarm'){
        moduleName = 'alarm'
      }
      let url = '/v3/report/folders?moduleName=' + moduleName
      if (moduleName === 'custom') {
        url = 'v3/report/folders?moduleName=custommodule'
      }
      API.get(url).then(resp => {
        if (!resp.error) {
          let data = resp.data.reportFolders
          let treeData = data.map(function(d) {
            d.expand = false
            d.reports.forEach(rt => {
              rt.selected = false
              rt.disabled = false
            })
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
          self.openFirstReport()
          self.reportLoading = false
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
          if (
            self.supportedModules[i].license &&
            this.$helpers.isLicenseEnabled(self.supportedModules[i].license)
          ) {
            self.$http
              .get('/view?moduleName=' + self.supportedModules[i].value)
              .then(function(response) {
                self.supportedModules[i].list = response.data.views
                self.supportedModules[i].list.forEach(rt => {
                  self.$set(rt, 'selected', false)
                  self.$set(rt, 'disabled', false)
                })
              })
          } else if (!self.supportedModules[i].license) {
            self.$http
              .get('/view?moduleName=' + self.supportedModules[i].value)
              .then(function(response) {
                self.supportedModules[i].list = response.data.views
                self.supportedModules[i].list.forEach(rt => {
                  self.$set(rt, 'selected', false)
                  self.$set(rt, 'disabled', false)
                })
              })
          }
        }
      }
      this.customModuleList = []
      this.$http('/v2/module/list').then(response => {
        let moduleList = response.data.result.moduleList
        if (!isEmpty(moduleList)) {
          for (let cmodule of moduleList) {
            let custom = {
              label: cmodule.displayName,
              value: cmodule.name,
              list: [],
              expand: false,
              license: 'CONNECTEDAPPS',
            }
            self.$http
              .get('/view?moduleName=' + cmodule.name)
              .then(function(response) {
                if (response.data.views) {
                  custom.list = response.data.views

                  custom.list.forEach(rt => {
                    rt.selected = false
                    rt.disabled = false
                  })
                  self.customModuleList.push(custom)
                }
              })
          }
        }
      })
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
              .then(function() {
                self.$message({
                  message: 'Dashboard deleted successfully!',
                  type: 'success',
                })
                self.$router.go(-1)
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
    updatewidget() {},
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
        .then(function() {
          self.$message({
            message: 'Sharing applied successfully!',
            type: 'success',
          })
          self.sharingDialogVisible = false
        })
    },
    loadDashboard() {
      let { query } = this.$route
      let { appId } = query || {}
      let self = this
      self.loading = true
      self.dashboardLayout = null
      let url = '/dashboard/' + this.dashboardLink
      if (!this.$helpers.isLicenseEnabled('NEW_LAYOUT')) {
        url += '?moduleName=' + this.getCurrentModule().module
        if (self.siteId) {
          url += '&siteId=' + self.siteId
        } else if (self.buildingId) {
          url += '&buildingId=' + self.buildingId
        }
      } else if (this.dashboardLink.includes('buildingdashboard')) {
        url += '?moduleName=' + 'energydata'
        if (self.siteId) {
          url += '&siteId=' + self.siteId
        } else if (self.buildingId) {
          url += '&buildingId=' + self.buildingId
        }
      } else {
        if (self.siteId) {
          url += '?siteId=' + self.siteId
        } else if (self.buildingId) {
          url += '?buildingId=' + self.buildingId
        }
      }

      self.$http.get(url).then(function(response) {
        self.dashboard = response.data.dashboardJson[0]
        self.backupDashboard = self.$helpers.cloneObject(
          response.data.dashboardJson[0]
        )
        if (self.dashboard.tabEnabled) {
          self.dashboardOptions.showTabs = self.dashboard.tabEnabled
        }
        if (self.dashboard.dashboardTabPlacement === -1) {
          self.dashboardOptions.dashboardTabPlacement = 2
        } else {
          self.dashboardOptions.dashboardTabPlacement =
            self.dashboard.dashboardTabPlacement
        }
        if (
          response.data.dashboardJson[0].tabs &&
          response.data.dashboardJson[0].tabs.length > 0
        ) {
          self.dashboardTabContexts = response.data.dashboardJson[0].tabs
          self.dashboardTabsList = []
          let query = {
            create: self.$route.query.create,
            tab: self.dashboardTabContexts[0].id,
          }
          if (!isEmpty(appId)) {
            query.appId = appId
          }
          if (!self.dashboardTabId) {
            self.$router.replace({
              path: self.$route.path,
              query,
            })
          }
          for (let tab of self.dashboardTabContexts) {
            self.dashboardTabsList.push({
              id: tab.id,
              name: tab.name,
              expand: false,
            })
            if (tab.childTabs && tab.childTabs.length > 0) {
              for (let childTab of tab.childTabs) {
                self.dashboardTabsList.push({
                  id: childTab.id,
                  name: childTab.name,
                })
              }
            }
          }
          self.loadDashboardTabWidgets()
        } else {
          self.dashboardTabContexts = null
          let query = {
            create: self.$route.query.create,
          }
          if (!isEmpty(appId)) {
            query.appId = appId
          }
          self.$router.replace({
            path: self.$route.path,
            query,
          })
        }
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
        self.loading = false
      })
    },
    changeDashboardTabId(id) {
      if (id && id > 0 && this.dashboardTabId !== id) {
        this.$set(
          this.dashboardLayouts,
          this.dashboardTabId,
          this.dashboardLayout
        )
        this.$router.replace({
          path: this.$route.path,
          query: {
            create: this.$route.query.create,
            tab: id,
          },
        })
      }
    },
    loadDashboardTabWidgets() {
      if (
        this.copyallWidgets &&
        this.newDashboardTabName &&
        this.dashboardOptions.showTabs
      ) {
        return
      }
      if (!this.dashboardTabId) {
        return
      }
      this.selectedTabId = this.dashboardTabId
      this.dashboardTabLoading = true
      this.$http
        .get('/dashboard/getTabWidgets?dashboardTabId=' + this.dashboardTabId)
        .then(response => {
          if (response.data.dashboardTabContext) {
            let dashboard = response.data.dashboardTabContext.clientWidgetJson
            this.$set(this.dashboard, 'children', dashboard)
            this.prepareDashboardLayout()
            this.$emit('dashboardobj', this.dashboard)
          } else {
            this.$route.replace(this.$route.path)
          }
          this.dashboardTabLoading = false
        })
        .catch(() => {
          this.dashboardTabLoading = false
        })
    },
    loadNewDashboardFoler() {
      let { query } = this.$route
      let { appId } = query || {}

      let self = this
      let moduleName = this.moduleName
      if (moduleName === 'energy') {
        moduleName = 'energyData'
      }
      this.folderLoading = true
      let url = '/dashboard/getDashboardFolder'
      if (!isEmpty(appId) && appId > 0) {
        url += '?appId=' + appId
      }
      if (!this.$helpers.isLicenseEnabled('NEW_LAYOUT')) {
        url += '?moduleName=' + moduleName
      }
      self.$http.get(url).then(function(response) {
        let foldername = {
          Analytics: 'Analytics',
          'Fault diagnosis': 'Fault diagnosis',
          Maintenance: 'Maintenance',
          default: 'default',
        }
        self.dashboardFolderList = response.data.dashboardFolders
        if (self.mode === 'new') {
          self.setDefaultDashboard()
        }
        self.folderLoading = false
      })
    },
    prepareDashboardLayout() {
      let self = this
      let layout = []
      let tx = 0
      for (let i = 0; i < self.dashboard.children.length; i++) {
        // Sections / Groups are not support in old dashboard
        const {
          widget: { type },
        } = self.dashboard.children[i]
        if (type == 'section') {
          continue
        }
        if (tx + self.dashboard.children[i].widget.layout.width > 96) {
          tx = 0
        }
        let x =
          (self.dashboard.children[i].widget.layout.x
            ? self.dashboard.children[i].widget.layout.x
            : 0) * 1
        let y =
          (self.dashboard.children[i].widget.layout.y
            ? self.dashboard.children[i].widget.layout.y
            : 0) * 1
        layout.push({
          i: self.dashboard.children[i].widget.id + '',
          x: x * 1,
          y: y * 1,
          w: self.dashboard.children[i].widget.layout.width * 1,
          h: self.dashboard.children[i].widget.layout.height * 1,
          widget: self.dashboard.children[i].widget,
          minW: self.getMinW(
            self.dashboard.children[i].widget.type,
            self.dashboard.children[i].widget
          ),
          minH: self.getMinH(
            self.dashboard.children[i].widget.type,
            self.dashboard.children[i].widget
          ),
        })
        tx += self.dashboard.children[i].widget.layout.width * 1
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
        return 4
      }
      if (widget && widget.dataOptions && widget.dataOptions.staticKey) {
        if (widget.dataOptions.staticKey === 'textcard') {
          return 4
        }
        if (widget.dataOptions.staticKey === 'imagecard') {
          return 4
        }
        if (widget.dataOptions.staticKey === 'web') {
          return 4
        }
        if (widget.dataOptions.staticKey === 'readingComboCard') {
          return 4
        }
      }
      if (type) {
        let width = 24
        switch (type) {
          case 'chart':
            width = 32
            break
          case 'static':
            width = 24
            break
          case 'view':
            width = 48
            break
          case 'list':
            width = 40
            break
          case 'map':
            width = 56
            break
          case 'web':
            width = 32
            break
          case 'connectedapps':
            width = 32
            break
          case 'graphics':
            width = 96
            break
          default:
            width = 32
            break
        }
        return width
      } else {
        return 24
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
        return 4
      }
      if (widget && widget.dataOptions && widget.dataOptions.staticKey) {
        if (widget.dataOptions.staticKey === 'textcard') {
          return 4
        }
        if (widget.dataOptions.staticKey === 'imagecard') {
          return 4
        }
        if (widget.dataOptions.staticKey === 'web') {
          return 4
        }
        if (widget.dataOptions.staticKey === 'readingComboCard') {
          return 4
        }
      }
      if (type) {
        let height = 24
        switch (type) {
          case 'chart':
            height = 24
            break
          case 'static':
            height = 32
            break
          case 'view':
            height = 32
            break
          case 'list':
            height = 32
            break
          case 'map':
            height = 32
            break
          case 'buildingcard':
            height = 24
            break
          case 'web':
            height = 32
            break
          case 'connectedapps':
            height = 32
            break
          case 'graphics':
            height = 45
            break
          default:
            height = 32
            break
        }
        return height
      } else {
        return 32
      }
    },
    saveEditDashboard() {
      let self = this
      let foldername = {
        Analytics: 'Analytics',
        'Fault diagnosis': 'Fault diagnosis',
        Maintenance: 'Maintenance',
        default: 'default',
      }
      if (self.supportedModules.length !== 0) {
        let data = {
          dashboard: {
            dashboardName: this.newdashboard.label,
            moduleId: this.$helpers.isLicenseEnabled('NEW_LAYOUT')
              ? this.supportedModules.find(row => row.name === 'workorder')
                  .moduleId
              : this.moduleName !== 'custom'
              ? this.supportedModules.find(row => row.name === this.moduleName)
                  .moduleId
              : this.supportedModules.find(row => row.name === 'asset')
                  .moduleId,
            dashboardFolderId:
              this.selectedDashoardFolder && this.selectedDashoardFolder.id
                ? this.selectedDashoardFolder.id
                : this.dashboardFolderList.find(rt => foldername[rt.name])
                ? this.dashboardFolderList.find(rt => foldername[rt.name]).id
                : -1,
          },
        }
        if (this.selectedDashoardFolder && this.selectedDashoardFolder.name) {
          this.dashbaordFolderName = this.selectedDashoardFolder.name
        }
        // if (
        //   this.dashboardFolderList.length &&
        //   this.dashboardFolderList.find(
        //     rt => rt.name === this.dashbaordFolderName
        //   )
        // ) {
        //   let item = this.dashboardFolderList.find(
        //     rt => rt.name === this.dashbaordFolderName
        //   )
        //   this.dashbaordFolderName = item.name
        //   this.selectedDashoardFolder = item
        // }
        this.addDashboardFolder(data)
      }
    },
    savefolderandDashboard(data, mode, dashboardObj, dashboard) {
      let self = this
      let reportFolderObj = {
        name: this.dashbaordFolderName,
      }
      let moduleName = this.$helpers.isLicenseEnabled('NEW_LAYOUT')
        ? 'workorder'
        : self.getCurrentModule().module
      self.$http
        .post('dashboard/addDashboardFolder?moduleName=' + moduleName, {
          dashboardFolderContext: reportFolderObj,
        })
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
        .catch(function() {
          self.$message({
            message: 'Dashboard Folder not saved',
            type: 'error',
          })
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
      } else if (
        this.selectedDashoardFolder &&
        dashboardname[0].replace(/\s/g, '').length > 0 &&
        this.dashboardFolderList.length === 0
      ) {
        this.saveDashboard(data)
      }
    },
    loadSupportedModules() {
      let self = this
      if (this.validateFolder()) {
        this.dashboardSave = true
        let url = '/dashboard/supportedmodules'
        this.$http.get(url).then(function(response) {
          if (response.data.modules) {
            self.supportedModules = response.data.modules
            self.saveEditDashboard()
          }
        })
      }
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
    saveMultiTab() {
      if (
        Object.keys(this.dashboardLayouts).length &&
        this.dashboardTabContexts &&
        this.dashboardTabContexts.length
      ) {
        this.$set(
          this.dashboardLayouts,
          this.dashboardTabId,
          this.dashboardLayout
        )
        let tabs = []
        let tabNames = {}
        this.dashboardTabContexts.forEach(rt => {
          this.$set(tabNames, rt.id, rt.name)
          if (rt.childTabs && rt.childTabs.length) {
            rt.childTabs.forEach(rl => {
              this.$set(tabNames, rl.id, rl.name)
            })
          }
        })
        Object.keys(this.dashboardLayouts).forEach(key => {
          let dasthboardlayout = this.dashboardLayouts[key]
          let tabName = tabNames[key]
          dasthboardlayout = this.removeWidgets(dasthboardlayout)
          tabs.push(
            this.prepareWidgetToSave(dasthboardlayout, Number(key), tabName)
          )
        })
        let params = {
          dashboardMeta: {
            dashboardId: this.dashboard.id,
            tabs: tabs,
          },
        }
        this.updateMultiTab(params)
      } else {
        this.saveEdit()
      }
    },
    saveEdit(dashboard, mode) {
      let self = this
      this.dashboardSave = true
      let dashboardWidgets = []
      let widgetData = null

      if (self.dashboardLayout !== null) {
        for (let i = 0; i < self.dashboardLayout.length; i++) {
          let gridItem = self.dashboardLayout[i]

          let reportTemplate =
            gridItem.widget.dataOptions.newReport &&
            gridItem.widget.dataOptions.newReport.type &&
            gridItem.widget.dataOptions.newReport.type === 4
              ? gridItem.widget.dataOptions.reportTemplate
                ? gridItem.widget.dataOptions.reportTemplate
                : gridItem.widget.dataOptions.newReport.reportTemplate
              : null
          if (typeof reportTemplate === 'object') {
            reportTemplate = JSON.stringify(reportTemplate)
          }

          if (gridItem.widget.type === 'card') {
            widgetData = {
              id: gridItem.widget.id,
              type: gridItem.widget.type,
              layoutWidth: gridItem.w,
              layoutHeight: gridItem.h,
              order: i + 1,
              xPosition: gridItem.x,
              yPosition: gridItem.y,
              helpText: gridItem?.widget?.helpText,
              widgetSettings: gridItem?.widget?.widgetSettings,
              ...gridItem.widget.dataOptions,
            }
          } else {
            widgetData = {
              id: gridItem.widget.id,
              type: gridItem.widget.type,
              layoutWidth: gridItem.w,
              layoutHeight: gridItem.h,
              order: i + 1,
              xPosition: gridItem.x,
              yPosition: gridItem.y,
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
              reportTemplate: reportTemplate,
              helpText: gridItem?.widget?.helpText,
              widgetSettings: gridItem?.widget?.widgetSettings,
            }
          }

          if (widgetData.type === 'graphics') {
            widgetData.graphicsId = gridItem.widget.graphicsId
            if (
              gridItem.widget.dataOptions &&
              gridItem.widget.dataOptions.graphicsOptions
            ) {
              widgetData.graphicsOptions =
                typeof gridItem.widget.dataOptions.graphicsOptions === 'object'
                  ? JSON.stringify(gridItem.widget.dataOptions.graphicsOptions)
                  : gridItem.widget.dataOptions.graphicsOptions
            }
          }
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
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'fahuStatusCardNew' &&
            gridItem.widget.dataOptions.AHUcard
          ) {
            widgetData.paramsJson = {
              parentId: gridItem.widget.dataOptions.AHUcard.id,
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'readingComboCard' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
            let data1 = JSON.parse(gridItem.widget.dataOptions.metaJson)
            if (
              data1.expressions &&
              data1.hasOwnProperty('v2Script') &&
              data1.hasOwnProperty('workflowV2String')
            ) {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  expressions: data1.expressions,
                  workflowUIMode: 1,
                  isV2Script: data1.v2Script,
                  workflowV2String: data1.workflowV2String,
                },
              })
            } else if (data1 && data1.expressions) {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  expressions: data1.expressions,
                  workflowUIMode: 1,
                },
              })
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'kpiCard' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
            let data1 = JSON.parse(gridItem.widget.dataOptions.metaJson)
            if (
              data1.expressions &&
              data1.hasOwnProperty('v2Script') &&
              data1.hasOwnProperty('workflowV2String')
            ) {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  expressions: data1.expressions,
                  workflowUIMode: 1,
                  isV2Script: data1.v2Script,
                  workflowV2String: data1.workflowV2String,
                },
              })
            } else if (data1.workflowV2String) {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  isV2Script: true,
                  workflowV2String: data1.workflowV2String,
                },
              })
            } else if (data1 && data1.expressions) {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  expressions: data1.expressions,
                  workflowUIMode: 1,
                },
              })
            } else {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  v2Script: true,
                  workflowV2String: 'void test(){↵    ↵    ↵}',
                },
              })
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'kpiCard' &&
            gridItem.widget.dataOptions.paramsJson
          ) {
            widgetData.paramsJson = gridItem.widget.dataOptions.paramsJson || {}
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'kpiMultiResultCard' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
            let data1 = JSON.parse(gridItem.widget.dataOptions.metaJson)
            if (
              data1.expressions &&
              data1.hasOwnProperty('v2Script') &&
              data1.hasOwnProperty('workflowV2String')
            ) {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  expressions: data1.expressions,
                  workflowUIMode: 1,
                  isV2Script: data1.v2Script,
                  workflowV2String: data1.workflowV2String,
                },
              })
            } else if (data1 && data1.expressions) {
              widgetData.widgetVsWorkflowContexts = []
              widgetData.widgetVsWorkflowContexts.push({
                workflow: {
                  expressions: data1.expressions,
                  workflowUIMode: 1,
                },
              })
            }
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'fcucard' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrillFcu' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrillFcuList' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrilllevel1' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrilllevel2' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrilllevel3' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrilllevel' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrilllevel1List' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrilllevel2List' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'emrilllevel3List' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'resourceAlarmBar' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
            widgetData.paramsJson = JSON.parse(
              gridItem.widget.dataOptions.metaJson
            ).params
          }
          if (
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.staticKey &&
            gridItem.widget.dataOptions.staticKey === 'alarmbarwidget' &&
            gridItem.widget.dataOptions.metaJson
          ) {
            widgetData.metaJson = gridItem.widget.dataOptions.metaJson
            widgetData.paramsJson = JSON.parse(
              gridItem.widget.dataOptions.metaJson
            ).params
          }
          if (
            gridItem.widget.type === 'chart' &&
            gridItem.widget.dataOptions &&
            gridItem.widget.dataOptions.hasOwnProperty('chartTypeInt')
          ) {
            widgetData.chartType = gridItem.widget.dataOptions.chartTypeInt
          }
          dashboardWidgets.push(widgetData)
        }
      }
      let dashboardObj = {}
      if (self.mode === 'new') {
        dashboardObj = {
          dashboardMeta: {
            id: dashboard.id,
            tabEnabled: self.dashboardOptions.showTabs,
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
            tabEnabled: self.dashboardOptions.showTabs,
            dashboardTabPlacement: self.dashboardOptions.dashboardTabPlacement,
            dashboardName: self.dashboard.label,
            linkName: self.dashboardLink,
            dashboardWidgets: dashboardWidgets,
            dashboardFolderId: dashboard
              ? dashboard.dashboardFolderId
                ? dashboard.dashboardFolderId
                : null
              : self.dashboard.dashboardFolderId
              ? self.dashboard.dashboardFolderId
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
      if (this.buildingId && this.dashboardLink === 'sitedashboard') {
        dashboardObj.siteId = this.buildingId
      } else if (
        this.buildingId &&
        this.dashboardLink === 'buildingdashboard'
      ) {
        dashboardObj.buildingId = this.buildingId
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
      if (
        dashboardObj.dashboardMeta &&
        dashboardObj.dashboardMeta.dashboardWidgets
      ) {
        let data = []
        data = dashboardObj.dashboardMeta.dashboardWidgets.filter(
          rt => rt.layoutHeight !== 0
        )
        dashboardObj.dashboardMeta.dashboardWidgets = data
      }
      if (this.dashboardTabId && this.dashboardOptions.showTabs) {
        this.updateDashboardTab(dashboardObj, dashboard)
        return
      }
      self.$http.post('/dashboard/update', dashboardObj).then(function() {
        self.$message({
          message: 'Dashboard updated successfully!',
          type: 'success',
        })
        if (
          !self.copyallWidgets &&
          !self.newDashboardTabName &&
          !self.dashboardOptions.showTabs
        ) {
          self.$router.push(self.newconstractUrl(dashboard))
        } else {
          self.loadEmptyDashboard()
        }
        self.dashboardSave = false
      })
    },
    updateDashboardTab(dashboardObj, dashboard) {
      let selectedTab = this.dashboardTabsList.find(
        i => i.id === this.selectedTabId
      )
      let param = {
        dashboardMeta: {
          dashboardName: dashboardObj.dashboardMeta.dashboardName,
          dashboardId: dashboardObj.dashboardMeta.id,
          dashboardFolderId: dashboardObj.dashboardMeta.dashboardFolderId,
          tabId: this.dashboardTabId,
          dashboardTabPlacement: this.dashboardOptions.dashboardTabPlacement,
          dashboardWidgets: this.updateDashboardTabId(
            dashboardObj.dashboardMeta.dashboardWidgets
          ),
          dashboardTabName: selectedTab.name,
        },
      }
      let self = this
      self.$http.post('/dashboard/updateTab', param).then(function() {
        self.$message({
          message: 'Dashboard updated successfully!',
          type: 'success',
        })
        self.$router.push(self.newconstractUrl(dashboard))
      })
    },
    newconstractUrl() {
      let dashboardlink =
        this.mode === 'new' ? this.newdashboard.linkName : this.dashboardLink

      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.DASHBOARD_VIEWER) || {}

        if (name) {
          return this.$router.resolve({ name, params: { dashboardlink } }).href
        }
      } else {
        return '/app/home/dashboard/' + dashboardlink
      }
    },
    cancelEdit() {
      location.reload()
      this.$router.go(-1)
    },
    cancel() {
      this.$router.push(this.newconstractUrl())
      // this.$router.go(-1)
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
    editwidget(index) {
      this.selectedWidgetId = -1
      if (
        index > -1 &&
        this.dashboardLayout[index] &&
        this.dashboardLayout[index].widget
      ) {
        let widget = this.dashboardLayout[index].widget
        this.isDraging = true
        this.editwidgetData = this.dashboardLayout[index]
        this.editwidgetData.index = index
        let data =
          widget && widget.id === -1 ? widget.dataOptions : widget.dataOptions
        this.selectedWidgetId = widget.id
        this.prepareEditwidgetData(widget, data)
      }
    },
    prepareEditwidgetData(widget, data) {
      if (
        data.staticKey === 'readingcard' ||
        data.staticKey === 'readingGaugeCard' ||
        data.staticKey === 'readingWithGraphCard'
      ) {
        if (widget.dataOptions.reading) {
          this.readingCard = this.$helpers.cloneObject(
            widget.dataOptions.reading
          )
          this.readingCard.operatorId =
            widget.dataOptions.reading && widget.dataOptions.reading.operatorId
              ? widget.dataOptions.reading.operatorId
              : 28
          this.readingCard.editmode = true
        } else {
          this.setReadingcardData(widget.dataOptions, widget)
        }
        this.addReadingPopUp()
      } else if (data.staticKey === 'imagecard') {
        this.showImageCardPopUp()
      } else if (data.staticKey === 'textcard') {
        this.showtextCardPopUp()
      } else if (data.staticKey === 'kpiCard') {
        if (widget.dataOptions.metaJson) {
          if (
            widget.dataOptions.paramsJson &&
            widget.dataOptions.paramsJson.key
          ) {
            this.newDashboardData.childKey = widget.dataOptions.paramsJson.key
            let metaJson = JSON.parse(widget.dataOptions.metaJson)

            this.editKpiCard(metaJson)
            if (this.newDashboardData.childKey === 'targetmeter') {
              this.targetMeterDialog = true
            } else if (this.newDashboardData.childKey === 'kpitargetCard') {
              this.showkpitargetCard = true
            } else if (
              this.newDashboardData.childKey === 'linearCard' ||
              this.newDashboardData.childKey === 'radialCard'
            ) {
              this.gaugeSetup = true
            } else if (this.newDashboardData.childKey === 'pmreadingswidget') {
              this.pmreadingsetup = true
            } else if (this.newDashboardData.childKey === 'smartnewmap') {
              this.smartmapsetup = true
            } else if (this.newDashboardData.childKey === 'smarttable') {
              this.smarttablesetup = true
            } else if (this.newDashboardData.childKey === 'smarttablewrapper') {
              this.smarttablewrappersetup = true
            } else if (this.newDashboardData.childKey === 'smartcard') {
              this.smartcardsetup = true
            } else if (this.newDashboardData.childKey === 'cardbuilder') {
              this.showCardBuilderSetup = true
            } else {
              this.buildingCard = true
            }
          } else {
            this.editKpiCard(JSON.parse(widget.dataOptions.metaJson))
            this.showkpiCard = true
          }
        }
      } else if (data.staticKey === 'readingComboCard') {
        if (widget.dataOptions.metaJson) {
          let editKpiCard = JSON.parse(widget.dataOptions.metaJson)
          this.assetcardEdit = true
          this.showCardBuilder = true
          this.editAssetCard(editKpiCard)
        }
      }
    },
    editKpiCard(data) {
      if (data) {
        if (data.hasOwnProperty('edit')) {
          data.edit = true
        }
        this.kpiCard = null
        this.kpiCard = this.$helpers.cloneObject(data)
      }
    },
    setReadingcardData(data) {
      let meta = JSON.parse(data.metaJson)
      let params = data.paramsJson
      this.readingCard.title = meta.title
      this.readingCard.mode = meta.mode || this.readingCard.mode
      this.readingCard.parentId = params.parentId
      this.readingCard.aggregationFunc =
        params.aggregateFunc || this.readingCard.aggregationFunc
      this.readingCard.readingFieldId = params.fieldId
      this.readingCard.operatorId = params.dateOperator
        ? params.dateOperator
        : params.dateFilter && params.dateFilter.value
        ? params.dateFilter.value
        : 28
      this.readingCard.editmode = true
      this.readingCard.write = params.write
      this.readingCard.colorMap = meta.colorMap ? meta.colorMap : false
      this.readingCard.colorMapConfig = meta.colorMap ? meta.colorMapConfig : {}

      if (data.staticKey === 'readingGaugeCard') {
        this.readingCard.targetreading.readingFieldId = params.fieldId1
        this.readingCard.targetreading.parentId = params.parentId1
        this.readingCard.targetreading.count = params.constant
        this.readingCard.targetreading.targetmode =
          params.fieldId1 > -1 ? 'reading' : 'constant'
        this.readingCard.targetreading.maxmode =
          params.maxConstant === -1 && params.maxPercentage === -1
            ? 'usetarget'
            : params.maxConstant > -1
            ? 'constant'
            : params.maxPercentage > -1
            ? 'percentage'
            : 'usetarget'
        this.readingCard.targetreading.enableCenterText1 =
          meta && meta['enableCenterText1'] !== undefined
            ? meta.enableCenterText1
            : true
        if (params.maxConstant === -1 && params.maxPercentage === -1) {
          this.readingCard.targetreading.maxValue = null
        } else if (params.maxConstant > -1) {
          this.readingCard.targetreading.maxValue = params.maxConstant
        } else if (params.maxPercentage > -1) {
          this.readingCard.targetreading.maxpercentage = params.maxPercentage
        }
      }
    },
    deleteChart(index) {
      this.currentTemplateReportId = null
      this.deletlayout.push(this.dashboardLayout[index])
      this.dashboardLayout[index].w = 0
      this.dashboardLayout[index].h = 0
      this.dashboardLayout[index].minH = 0
      this.dashboardLayout[index].minW = 0
      this.dashboardLayout[index].x = 0
      this.dashboardLayout[index].y = 0
      this.editwidgetData = null
    },
    openFirstReport() {
      let self = this
      if (
        !self.$route.params.reportid &&
        !this.$route.path.includes('scheduled')
      ) {
        if (
          self.newReportTree &&
          self.newReportTree.length &&
          self.newReportTree[0].reports.length
        ) {
          self.newReportTree[0].expand = true
          // let reportLink = self.getReportLink(self.reportTree[0].children[0].widget.dataOptions.reportId)
          // self.$router.push({path: reportLink})
        } else if (
          self.reportTree.length &&
          self.reportTree[0].children.length
        ) {
          self.reportTree[0].expand = true
          // let reportLink = self.getReportLink(self.reportTree[0].children[0].widget.dataOptions.reportId)
          // self.$router.push({path: reportLink})
        }
      }
    },
    expand(folder) {
      this.$set(folder, 'expand', !folder.expand)
      this.changeDashboardTabId(folder.id)
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
    drag() {},
    allowDrop(e) {
      e.preventDefault()
    },
    start(data, parent) {
      this.selectedBuilding = null
      this.editwidgetData = null
      if (parent) {
        data.moduleName = parent
      }
      this.newDashboardData = data
      this.isDraging = true
    },
    resetCardData() {
      this.readingCard = {
        title: '',
        mode: 1,
        parentId: -1,
        legend: 'sum',
        parentName1: '',
        write: false,
        readingType: -1,
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
          enableCenterText1: true,
        },
        aggregationFunc: 3,
        readingName: '',
        operatorId: 28,
        layout: {
          color: '',
          fontColor: '',
        },
        colorMap: false,
        colorMapConfig: {},
      }
    },
    dragEnter() {},
    drop() {
      this.emptywidget = true
      if (
        this.newDashboardData.key === 'energycostmini' ||
        this.newDashboardData.key === 'profilemini' ||
        this.newDashboardData.key === 'carbonmini' ||
        this.newDashboardData.key === 'weathermini'
      ) {
        if (this.newDashboardData.key === 'weathermini') {
          this.addDataPopUp('all')
        } else {
          this.addDataPopUp()
        }
      } else if (this.newDashboardData.key === 'readingcard') {
        this.resetCardData()
        this.addReadingPopUp()
      } else if (this.newDashboardData.key === 'imagecard') {
        this.showImageCardPopUp()
      } else if (this.newDashboardData.key === 'textcard') {
        this.showtextCardPopUp()
      } else if (this.newDashboardData.key === 'web') {
        this.webcardVisible = true
      } else if (this.newDashboardData.key === 'fahuStatusCard') {
        this.ahupopup = true
      } else if (this.newDashboardData.key === 'fahuStatusCard1') {
        this.ahupopup = true
      } else if (this.newDashboardData.key === 'fahuStatusCard3') {
        this.ahupopup = true
      } else if (this.newDashboardData.key === 'fahuStatusCardNew') {
        this.ahupopup = true
      } else if (this.newDashboardData.key === 'readingComboCard') {
        this.showCardBuilder = true
      } else if (this.newDashboardData.key === 'kpiCard') {
        let keys = {
          profilemini: 'profilemini',
          energycostmini: 'energycostmini',
          carbonmini: 'carbonmini',
          controlCommandmini: 'controlCommandmini',
        }
        if (keys[this.newDashboardData.childKey]) {
          this.buildingCard = true
        } else if (
          this.newDashboardData.childKey === 'smartmap' ||
          this.newDashboardData.childKey === 'smartenergymap'
        ) {
          this.addNewelemnt()
        } else if (this.newDashboardData.childKey === 'targetmeter') {
          this.targetMeterDialog = true
        } else if (this.newDashboardData.childKey === 'kpitargetCard') {
          this.showkpitargetCard = true
        } else if (
          this.newDashboardData.childKey === 'linearCard' ||
          this.newDashboardData.childKey === 'radialCard'
        ) {
          this.gaugeSetup = true
        } else if (this.newDashboardData.childKey === 'pmreadingswidget') {
          this.pmreadingsetup = true
        } else if (this.newDashboardData.childKey === 'smartnewmap') {
          this.smartmapsetup = true
        } else if (this.newDashboardData.childKey === 'smarttable') {
          this.smarttablesetup = true
        } else if (this.newDashboardData.childKey === 'smarttablewrapper') {
          this.smarttablewrappersetup = true
        } else if (this.newDashboardData.childKey === 'smartcard') {
          this.smartcardsetup = true
        } else if (this.newDashboardData.childKey === 'cardbuilder') {
          this.showCardBuilderSetup = true
        } else if (this.newDashboardData.childKey === 'multiTrend') {
          this.addNewelemnt()
        } else {
          this.showkpiCard = true
        }
        this.kpiCard = null
      } else if (this.newDashboardData.key === 'fcucard') {
        this.showFcuCardDialog = true
      } else if (this.newDashboardData.key === 'emrilllevel') {
        this.showFcuCardDialog = true
      } else if (this.newDashboardData.key === 'emrillFcu') {
        this.showFcuCardDialog = true
      } else if (this.newDashboardData.key === 'emrillFcuList') {
        this.showFcuCardDialog = true
      } else if (this.newDashboardData.key === 'resourceAlarmBar') {
        this.showBooleanCardDialog = true
      } else if (this.newDashboardData.key === 'alarmbarwidget') {
        this.showAlarmbarCardDialog = true
      } else {
        this.addNewelemnt()
      }
    },
    avatarCropUploadSuccess(jsonData) {
      this.imagecardData = null
      this.imagecardData = jsonData
      this.addNewelemnt()
      this.isDraging = false
      this.showAvatarUpload = false
    },
    imageCardUpdate(data) {
      this.imagecardData = null
      this.imagecardData = data
      if (this.editwidgetData && this.editwidgetData.index) {
        this.dashboardLayout[
          this.editwidgetData.index
        ].widget.dataOptions.metaJson = JSON.stringify(data)
        this.dashboardLayout[
          this.editwidgetData.index
        ].widget.dataOptions.imagecardData = data
      }
      this.editwidgetcalled = !this.editwidgetcalled
      this.isDraging = false
      this.showAvatarUpload = false
      this.editwidgetData = null
    },
    avatarCropSuccess(avatarDataUrl) {
      this.avatarDataUrl = null
      this.avatarDataUrl = avatarDataUrl
    },
    avatarCropUploadFail() {
      this.isDraging = false
      this.showAvatarUpload = false
      this.avatarDataUrl = null
    },
    updateWidget() {
      this.dashboardLayout[this.editwidgetData.index] = this.updateWidgetLayout(
        this.editwidgetData
      )
      this.editwidgetcalled = !this.editwidgetcalled
      this.readingCard.editmode = false
      // this.dashboardLayout.slice(this.editwidgetData.index, 1)
      // this.dashboardLayout.insert(this.editwidgetData.index, this.editwidgetData)
      this.editwidgetData = null
    },
    addreadings() {
      if (this.readingPopupValidation().valid) {
        if (this.editwidgetData) {
          this.updateWidget()
        } else {
          this.addNewelemnt()
        }
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
      this.showtextcard = false
      this.ahupopup = false
      this.webcardVisible = false
      this.showCardBuilder = false
      this.assetcardEdit = false
      this.showFcuCardDialog = false
      this.showBooleanCardDialog = false
      this.showAlarmbarCardDialog = false
      this.buildingCard = false
      this.gaugeSetup = false
      this.pmreadingsetup = false
      this.smartmapsetup = false
      this.smarttablesetup = false
      this.smarttablewrappersetup = false
      this.smartcardsetup = false
      this.showCardBuilderSetup = false
      this.targetMeterDialog = false
      this.resetCardData()
    },
    addDataPopUp(data) {
      this.dialogVisible = true
      if (data === 'all') {
        this.buildings = this.allBuildings
      } else {
        this.buildings = this.tempBuildings
      }
    },
    addReadingPopUp() {
      this.readingpopup = true
    },
    showImageCardPopUp() {
      this.showAvatarUpload = true
    },
    showtextCardPopUp() {
      this.showtextcard = true
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
        if (this.readingCard.id) {
          this.readingCard.readingType =
            data.fullList && this.readingCard.id
              ? data.fullList.find(rt => rt.id === this.readingCard.id)
                  .readingType
              : -1
        } else {
          this.readingCard.readingType =
            data.fullList && data.readingField && data.readingField.id
              ? data.fullList.find(rt => rt.fieldId === data.readingField.id)
                  .readingType
              : -1
        }
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
    cancelDataPointAdder() {
      this.addreadingVisible2 = false
      this.addreadingVisible3 = false
    },
    containerover() {
      this.emptywidget = true
    },
    dropContainer() {
      this.emptywidget = true
    },
    dragend() {
      if (
        !this.dialogVisible &&
        !this.readingpopup &&
        !this.showAvatarUpload &&
        !this.showtextcard &&
        !this.ahupopup &&
        !this.webcardVisible &&
        !this.showCardBuilder &&
        !this.showFcuCardDialog &&
        !this.showBooleanCardDialog &&
        !this.showAlarmbarCardDialog &&
        !this.showkpiCard &&
        !this.showkpitargetCard &&
        !this.buildingCard &&
        !this.gaugeSetup &&
        !this.pmreadingsetup &&
        !this.smartmapsetup &&
        !this.smarttablesetup &&
        !this.smarttablewrappersetup &&
        !this.smartcardsetup &&
        !this.showCardBuilderSetup &&
        !this.targetMeterDialog
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
    containerEntry() {
      // let elemnt = document.getElementsByClassName('fc-widget-header')[0]
    },
    addNewelemnt(mode) {
      this.dashboardlength = this.dashboardlength + 1
      let data = this.newDashboardData
      let layout = {}
      if (data.optionName) {
        this.option = data.optionName
      }
      if (this.option === 'view') {
        layout = this.getReportWidget(data, layout)
      } else if (this.option === 'static') {
        layout = {
          i: this.dashboardlength + '',
          x: 0,
          y: 0,
          w: data.w,
          h: data.h,
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
        }
      } else if (this.option === 'cards') {
        layout = {
          i: this.dashboardlength + '',
          x: 0,
          y: 0,
          w: data.w,
          h: data.h,
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
        }
        if (this.readingCard.mode === 2) {
          layout.h = 8
          layout.widget.height = 8
          layout.minH = 8
        }
        if (this.readingCard.mode === 3) {
          layout.h = 16
          layout.widget.height = 16
          layout.minH = 16
        }
        if (this.readingCard.mode === 4) {
          layout.h = 16
          layout.widget.height = 16
          layout.minH = 16
          layout.widget.dataOptions.staticKey = 'readingGaugeCard'
        }
        if (layout.widget.dataOptions.staticKey === 'readingComboCard') {
          layout.h =
            this.assetCardData && this.assetCardData.cardType
              ? this.assetCardLayoutChanger(this.assetCardData)
              : 12
          layout.widget.layout.height =
            this.assetCardData && this.assetCardData.cardType
              ? this.assetCardLayoutChanger(this.assetCardData)
              : 12
          layout.widget.height =
            this.assetCardData && this.assetCardData.cardType
              ? this.assetCardLayoutChanger(this.assetCardData)
              : 12
          layout.minH =
            this.assetCardData && this.assetCardData.cardType
              ? this.assetCardLayoutChanger(this.assetCardData)
              : 12
          layout.w =
            this.assetCardData && this.assetCardData.cardType
              ? this.cardToLayoutMap[this.assetCardData.cardType].w
              : 12
          layout.widget.width =
            this.assetCardData && this.assetCardData.cardType
              ? this.cardToLayoutMap[this.assetCardData.cardType].w
              : 12
          layout.widget.layout.width =
            this.assetCardData && this.assetCardData.cardType
              ? this.cardToLayoutMap[this.assetCardData.cardType].w
              : 12
          layout.minW =
            this.assetCardData && this.assetCardData.cardType
              ? this.cardToLayoutMap[this.assetCardData.cardType].w
              : 12
        }
        if (layout.widget.dataOptions.staticKey === 'imagecard') {
          layout = this.getReportWidget(data, layout, 'imagecard')
        }
        if (layout.widget.dataOptions.staticKey === 'textcard') {
          layout = this.getReportWidget(data, layout, 'textcard')
        }
        if (layout.widget.dataOptions.staticKey === 'web') {
          layout.minH = 4
          layout.minW = 4
        }
        if (
          this.AHUcard.catogry === 'FAHU' &&
          layout.widget.dataOptions.staticKey === 'fahuStatusCard'
        ) {
          layout.widget.dataOptions.staticKey = 'fahuStatusCard2'
        }
        if (
          this.fcuCardData &&
          this.fcuCardData.level &&
          layout.widget.dataOptions.staticKey === 'fcucard'
        ) {
          layout.widget.dataOptions.staticKey =
            'emrilllevel' + this.fcuCardData.level
        }
        if (
          this.fcuCardData &&
          this.fcuCardData.level &&
          layout.widget.dataOptions.staticKey === 'emrilllevel'
        ) {
          layout.widget.dataOptions.staticKey =
            'emrilllevel' + this.fcuCardData.level + 'List'
        }
        if (
          layout.widget.dataOptions.staticKey === 'kpiCard' &&
          data.childKey
        ) {
          layout.widget.dataOptions.paramsJson = {
            key: data.childKey,
          }
        }
      } else if (this.option === 'web') {
        layout = {
          i: this.dashboardlength + '',
          x: 0,
          y: 0,
          w: this.getMinW('web'),
          h: this.getMinH('web'),
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
          label: data.label,
        }
      } else if (this.option === 'connectedapps') {
        layout = {
          i: this.dashboardlength + '',
          x: 0,
          y: 0,
          w: this.getMinW('connectedapps'),
          h: this.getMinH('connectedapps'),
          widget: {
            layout: {
              width: this.getMinW('connectedapps'),
              x: 0,
              y: Infinity,
              position: this.dashboardlength,
              height: this.getMinH('connectedapps'),
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
            type: 'connectedapps',
            id: data.id,
          },
          minW: this.getMinW('connectedapps'),
          minH: this.getMinH('connectedapps'),
          label: data.label,
        }
      } else if (this.option === 'graphics') {
        layout = this.getReportWidget(data, layout)
      } else {
        layout = this.getReportWidget(data, layout)
      }

      // if(data.type && data.type === 4 && (this.currentTemplateReportId === null || this.currentTemplateReportId !== this.newDashboardData.id)){
      //   this.showTemplateReportDialog = true
      //   return
      // }
      if (!mode) {
        this.dashboardLayout.push(this.updateWidgetLayout(layout))
      }
    },
    updateWidgetLayout(layout) {
      if (
        layout.widget &&
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey &&
        layout.widget.dataOptions.staticKey === 'readingcard'
      ) {
        if (this.readingCard.mode === 1) {
          layout.h = 12
          layout.widget.height = 12
          layout.minH = 12
        }
        if (this.readingCard.mode === 2) {
          layout.h = 8
          layout.widget.height = 8
          layout.minH = 8
        }
        if (this.readingCard.mode === 3) {
          layout.h = 16
          layout.widget.height = 16
          layout.minH = 16
          layout.widget.dataOptions.staticKey = 'readingWithGraphCard'
        }
        if (this.readingCard.mode === 4) {
          layout.h = 16
          layout.widget.height = 16
          layout.minH = 16
          layout.widget.dataOptions.staticKey = 'readingGaugeCard'
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
        layout.widget.dataOptions.staticKey === 'readingWithGraphCard'
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
        layout.widget.dataOptions.staticKey === 'fahuStatusCardNew'
      ) {
        layout.widget.dataOptions.AHUcard = this.AHUcard
        this.AHUcard = {
          catogry: '',
          id: null,
        }
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'readingComboCard'
      ) {
        layout.widget.dataOptions.readingComboCard = this.assetCardData
        this.assetCardData = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'kpiCard'
      ) {
        layout.widget.dataOptions.data = this.kpiCard
        this.kpiCard = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'fcucard'
      ) {
        layout.widget.dataOptions.cardData = this.fcuCardData
        this.fcuCardData = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'resourceAlarmBar'
      ) {
        layout.widget.dataOptions.cardData = this.booleancardData
        this.booleancardData = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'alarmbarwidget'
      ) {
        layout.widget.dataOptions.cardData = this.alarmcardData
        this.alarmcardData = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'emrillFcu'
      ) {
        layout.widget.dataOptions.cardData = this.fcuCardData
        this.fcuCardData = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'emrillFcuList'
      ) {
        layout.widget.dataOptions.cardData = this.fcuCardData
        this.fcuCardData = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'emrilllevel1'
      ) {
        layout.widget.dataOptions.cardData = this.fcuCardData
        this.fcuCardData = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'emrilllevel2'
      ) {
        layout.widget.dataOptions.cardData = this.fcuCardData
        this.fcuCardData = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'emrilllevel3'
      ) {
        layout.widget.dataOptions.cardData = this.fcuCardData
        this.fcuCardData = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'emrilllevel'
      ) {
        layout.widget.dataOptions.cardData = this.fcuCardData
        this.fcuCardData = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'emrilllevel1List'
      ) {
        layout.widget.dataOptions.cardData = this.fcuCardData
        this.fcuCardData = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'emrilllevel2List'
      ) {
        layout.widget.dataOptions.cardData = this.fcuCardData
        this.fcuCardData = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'emrilllevel3List'
      ) {
        layout.widget.dataOptions.cardData = this.fcuCardData
        this.fcuCardData = null
      }
      if (
        layout.widget.dataOptions &&
        layout.widget.dataOptions.staticKey === 'emrilllevel3List'
      ) {
        layout.widget.dataOptions.cardData = this.fcuCardData
        this.fcuCardData = null
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
      this.readingCard = {
        title: '',
        mode: 1,
        parentId: -1,
        legend: 'sum',
        parentName1: '',
        write: false,
        readingType: -1,
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
          enableCenterText1: true,
        },
        aggregationFunc: 3,
        readingName: '',
        operatorId: 28,
        layout: {
          color: '',
          fontColor: '',
        },
        colorMap: false,
        colorMapConfig: {},
      }
      return layout
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
      } else if (key === 'controlCommandmini') {
        return ''
      } else if (key === 'profilemini') {
        return ''
      } else if (key === 'readingcard') {
        return ''
      } else {
        return data.label
      }
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
    getStaticWidgetList(widgets) {
      if (widgets) {
        if (this.$account.org.id !== 108 && this.$account.org.id !== 113) {
          widgets = widgets.filter(widget => widget.key !== 'sampleppm')
        }
        return widgets
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
      let { query } = this.$route
      let { appId } = query || {}
      let self = this
      let moduleName = this.moduleName
      if (moduleName === 'energy') {
        moduleName = 'energyData'
      }
      let url = '/dashboard/getDashboardFolder'
      if (!this.$helpers.isLicenseEnabled('NEW_LAYOUT')) {
        url += '?moduleName=' + moduleName
      }
      if (!isEmpty(appId)) {
        url += '?appId=' + appId
      }
      self.$http.get(url).then(function(response) {
        let foldername = {
          Analytics: 'Analytics',
          'Fault diagnosis': 'Fault diagnosis',
          Maintenance: 'Maintenance',
          default: 'default',
        }
        self.dashboardFolderList = response.data.dashboardFolders
        if (self.mode === 'new') {
          self.dashbaordFolderName =
            self.dashboardFolderList && self.dashboardFolderList.length
              ? self.dashboardFolderList.find(rt => foldername[rt.name])
                ? self.dashboardFolderList.find(rt => foldername[rt.name]).name
                : 'Maintenance'
              : 'Maintenance'
        }
        if (self.mode === 'new') {
          self.setDefaultDashboard()
        }
        if (source === 'watch') {
          self.loadDashboard()
        }
      })
    },
    handleSelect(item) {
      this.dashbaordFolderName = item.name
      this.selectedDashoardFolder = item
    },
    handleIconClick() {},
    getSourceFile() {},
    getInput() {
      this.cancelBuilding()
    },
    loadGraphicsList() {
      this.$http
        .get('/v2/graphics/list')
        .then(response => {
          if (response.data.responseCode === 0) {
            this.graphicsList = response.data.result.graphics_list
            this.graphicsList.forEach(rt => {
              this.$set(rt, 'selected', false)
              this.$set(rt, 'disabled', false)
            })
          } else {
            throw new Error(response.data.message)
          }
        })
        .catch(error => {
          this.$message.error(error)
        })
    },
    loadConnectedAppWidgetList() {
      let connectedAppsUrl =
        '/v2/connectedApps/widgetList?filters=' +
        encodeURIComponent(
          JSON.stringify({ entityType: { operatorId: 9, value: ['3'] } })
        )
      this.$http
        .get(connectedAppsUrl)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.connectedAppWidgetList =
              response.data.result.connectedAppWidgets
          } else {
            throw new Error(response.data.message)
          }
        })
        .catch(error => {
          this.$message.error(error)
        })
    },
    toggleDashboardTabEditor() {
      this.dashboardTabEditorVisibility = !this.dashboardTabEditorVisibility
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
  /*background-color: #fd4b92;*/
  background-color: var(--fc-theme-color);
  -webkit-animation-delay: 0.3s;
  animation-delay: 0.3s;
}

.sk-cube-grid .sk-cube3 {
  background-color: #2f2e49;
  -webkit-animation-delay: 0.4s;
  animation-delay: 0.4s;
}

.sk-cube-grid .sk-cube4 {
  /*background-color: #fd4b92;*/
  background-color: var(--fc-theme-color);
  -webkit-animation-delay: 0.1s;
  animation-delay: 0.1s;
}

.sk-cube-grid .sk-cube5 {
  background-color: #2f2e49;
  -webkit-animation-delay: 0.2s;
  animation-delay: 0.2s;
}

.sk-cube-grid .sk-cube6 {
  /*background-color: #fd4b92;*/
  background-color: var(--fc-theme-color);
  -webkit-animation-delay: 0.3s;
  animation-delay: 0.3s;
}

.sk-cube-grid .sk-cube7 {
  background-color: #2f2e49;
  -webkit-animation-delay: 0s;
  animation-delay: 0s;
}

.sk-cube-grid .sk-cube8 {
  /*background-color: #fd4b92;*/
  background-color: var(--fc-theme-color);
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
  display: block;
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
  line-height: normal;
  letter-spacing: 0.8px;
  text-align: left;
  /*color: #ef4f8f;*/
  color: #8ca1ad;
  text-transform: uppercase;
  height: 76px;
  padding: 15px 17px 15px 17px;
  /* border-bottom: 1px solid #6666661a; */
  background: white;
  box-shadow: 0 2px 4px 0 rgba(223, 223, 223, 0.5);
  position: relative;
}

.create-dashboard-input-title .el-input__inner {
  color: #324056;
  /* border: none; */
  font-size: 20px;
  font-weight: 500;
  padding-left: 5px;
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
  padding-bottom: 120px;
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
  /*background: #ee518f;*/
  background: var(--fc-theme-color);
  color: #fff;
  font-weight: 500;
}

.editdashboard-sidebar-header .scheduled-viewall div.active {
  /*background: #ee518f;*/
  background: var(--fc-theme-color);
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
  /*background-color: #fd4b92;*/
  background: var(--fc-theme-color);
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
  /* padding-right: 15px; */
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
  /*border-left: 3px solid #ee518f;*/
  border-left: 3px solid var(--fc-theme-color);
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

/* .dashboard-folder {
  margin-right: 25px;
  padding-top: 10px;
} */
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

.building-dialog .el-dialog__body {
  height: 65vh;
}

.select-reading-dialog .el-dialog__body {
  height: 600px;
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

.select-card-reading fc-popover-p0.p5 {
  padding-top: 15px;
  padding-bottom: 15px;
}

.select-card-reading fc-popover-p0.p5 div {
  width: 100%;
}

.select-building-dialog .reading-card-container {
  padding-top: 10px;
  padding-bottom: 10px;
}

.select-card-reading fc-popover-p0.fc-el-btn {
  padding: 18px !important;
  background: #fff;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  font-weight: 500;
  text-align: center;
  cursor: pointer;
}

.select-card-reading fc-popover-p0.fc-el-btn.el-report-save-btn {
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

.dashboard-container.editmode .fc-widget-fullScreen {
  display: none;
}

.el-dialog__wrapper.editmode .reading-card-layout-changer.col-6:first-child {
  display: none;
}

.el-dialog__wrapper.editmode {
  width: 50%;
  margin-left: 25%;
}

.el-dialog__wrapper.editmode .reading-card-layout-changer.col-6:second-child {
  flex: 0 100%;
}

.el-dialog__wrapper.editmode .reading-card-data-selecter.col-6 {
  border-left: 0;
  padding-left: 0;
  flex: 0 100%;
  width: 100%;
  max-width: 100%;
}
.dashboard-main-section .fc-autocomplete-select .el-input__inner {
  padding-left: 30px !important;
}
.bg-grids {
  position: absolute;
  width: 100%;
  height: 100%;
}
.main-grids {
  position: absolute;
  width: 100%;
  height: 100%;
}
.grid-cells {
  background: #c7d8ff;
}
.bg-grids .vue-resizable-handle {
  display: none;
}
.dashboard-folder-height {
  max-height: 300px;
  overflow: auto;
}
.dashboard-folder.active {
  background-color: #f1f8fa;
}
.dashboard-folder:hover {
  background-color: #f1f8fa;
}
.dashboard-title-input {
  width: 300px;
  border: none !important;
}
.dashboard-title-input:hover {
  border: none !important;
}
.dashboard-folder-chooser {
  border-radius: 0px;
}
.fol-header {
  background: #39b2c2;
  color: #fff;
}
.dashboard-folder-chooser .popper__arrow::after,
.dashboard-option-dialog .popper__arrow::after,
.dashboard-folder-chooser .popper__arrow,
.dashboard-folder-chooser .el-popper[x-placement^='bottom'] .popper__arrow {
  border-bottom-color: #39b2c2 !important;
}
.dashboard-option-dialog {
  padding: 0px !important;
}
.dashboard-option-header {
  background: #39b2c2;
  color: #fff;
  font-weight: 500;
}
.addTab-btn {
  position: absolute;
  bottom: 125px;
  width: 100%;
  border-top: 1px solid #eee !important;
}
/* .edit-dashboard-container .vue-grid-layout {
  overflow: scroll;
} */
</style>
