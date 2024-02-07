<template>
  <div class="dashboard-main-section new-dashboard">
    <div class="row" v-if="!loading">
      <div class="col-12">
        <div
          class="new-create-dashboard-header fc-theme-color flex-middle justify-between visibility-visible-actions"
        >
          <div class="pull-left width30">
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
            <div
              class="flex-middle create-dashboard-btn-section"
              v-if="showTabOptions"
            >
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
                            @change="toggleTabsVisibility"
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
                    />
                  </div>
                </el-popover>
              </div>
            </div>
            <div
              class="flex-middle create-dashboard-btn-section dashboard-folder"
            >
              <el-dropdown @command="handleButton" placement="bottom">
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
                  <el-dropdown-item command="addSection"
                    >Add Group</el-dropdown-item
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
              :disabled="dashboardSave"
              @click="saveDashboard"
              >{{
                dashboardSave ? 'Saving' : $t('panel.dashboard.save')
              }}</el-button
            >
          </div>
        </div>
      </div>
    </div>
    <div class="row" style="position:relative;">
      <div class="col-12 container-section" :style="getHeight">
        <el-row class="height-100">
          <div
            v-if="
              dashboardTabId &&
                dashboardTabContexts &&
                dashboardTabContexts.length > 0
            "
          >
            <el-col
              :span="4"
              v-if="dashboardOptions.dashboardTabPlacement === 2"
            >
              <vertical-tab-selector
                :tabsList="dashboardTabContexts"
                :currentTabId="dashboardTabId"
                :isEditor="editMode"
                @toggleDashboardTabEditor="toggleDashboardTabEditor"
                @changeDashboardTabId="changeDashboardTabId"
              />
            </el-col>
            <el-col
              :span="24"
              v-if="dashboardOptions.dashboardTabPlacement === 1"
            >
              <dashboard-top-tab
                :edit="true"
                :dashboardTabContexts="dashboardTabContexts"
                :dashboardTabId="dashboardTabId"
                @toggleDashboardTabEditor="toggleDashboardTabEditor"
                @changeDashboardTabId="changeDashboardTabId"
              ></dashboard-top-tab>
            </el-col>
          </div>
          <el-col :span="tabsEnabled ? 20 : 24" class="height-100">
            <div class="height100 scrollable dashboardmainlayout">
              <cube-loader
                v-if="loading || !dashboardLayout || dashboardTabLoading"
              />
              <div
                class="dashboard-container edit-dashboard-container gridstack-container"
                v-else
                :class="{ editmode: editMode }"
              >
                <!-- This component written on top of gridstack.js has known issues,
                  for now,
                  1) Unmount the gridstack component.
                  2) Assign value to this.dashboardLayout.
                  3) Mount the gridstack component.
                  If you don't do this the :layout.sync will rekt your this.dashboardLayout data after you assign a value to
                  this.dashboardLayout.
                  -->
                <GridstackLayout
                  class="gridstack-layout"
                  v-if="showGridstackComponent == true"
                  @sectionHeightChanged="adjustSectionHeight"
                  ref="gridstack"
                  margin="5px"
                  :layout.sync="dashboardLayout"
                  :float="false"
                  :column="96"
                  rowHeight="15px"
                  :static="viewOrEdit == 'view' ? true : false"
                  :minRows="12"
                  :disableOneColumnMode="true"
                >
                  <GridstackItem
                    v-for="item in dashboardLayout"
                    :item-content-style="sectionStyle[item.id]"
                    :class="{ 'gridstack-group': item.type == 'section' }"
                    :key="item.id"
                    :x="item.x"
                    :y="item.y"
                    :w="item.w"
                    :h="item.h"
                    :id="item.id"
                    :noResize="
                      item.hasOwnProperty('children') ||
                        (widgetConfigMap.hasOwnProperty(item.id)
                          ? widgetConfigMap[item.id].noResize
                          : false)
                    "
                    class="dashboard-f-widget"
                    :itemContentCustomClass="['shadow-widget']"
                  >
                    <SectionContainer
                      v-if="item.hasOwnProperty('children')"
                      :section="item"
                      :viewOrEdit="viewOrEdit"
                      @clone="cloneSection(item.id)"
                      @removeSection="deleteWidget(item.id)"
                      @editSection="editSection(item.id)"
                      @addWidgetToSection="
                        widgetType => {
                          addWidgetToSection(item.id, widgetType)
                        }
                      "
                      @resizeSection="resizeSection"
                    >
                      <GridstackSection :id="item.id" :item="item">
                        <GridstackItem
                          v-for="child in item.children"
                          :key="child.id"
                          :id="child.id"
                          :w="child.w"
                          :h="child.h"
                          :x="child.x"
                          :y="child.y"
                          :noResize="
                            child.hasOwnProperty('children') ||
                              (widgetConfigMap.hasOwnProperty(child.id)
                                ? widgetConfigMap[child.id].noResize
                                : false)
                          "
                          :itemContentCustomClass="['shadow-widget']"
                        >
                          <widget-wrapper
                            :updateWidget="updateWidget"
                            :parentId="item.id"
                            :groupList="groupList"
                            :loadImmediately="loadImmediately"
                            :isLazyDashboard="isLazyDashboard"
                            :hideTimelineFilterInsideWidget="false"
                            :item="child"
                            :widgetConfig="widgetConfigMap[child.id] || {}"
                            :viewOrEdit="viewOrEdit"
                            @removeWidget="deleteWidget(child.id)"
                            @duplicateWidget="duplicateWidget"
                          />
                        </GridstackItem>
                      </GridstackSection>
                    </SectionContainer>
                    <template v-else>
                      <widget-wrapper
                        parentId="master"
                        :updateWidget="updateWidget"
                        :groupList="groupList"
                        :loadImmediately="loadImmediately"
                        :isLazyDashboard="isLazyDashboard"
                        :item="item"
                        :hideTimelineFilterInsideWidget="false"
                        :widgetConfig="widgetConfigMap[item.id] || {}"
                        :viewOrEdit="viewOrEdit"
                        @removeWidget="deleteWidget(item.id)"
                        @duplicateWidget="duplicateWidget"
                      />
                    </template>
                  </GridstackItem>
                </GridstackLayout>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <Dashboard-tab-dialog-editor
      v-if="dashboardTabEditorVisibility"
      :visibility.sync="dashboardTabEditorVisibility"
      :dashboardObject="dashboard"
    >
    </Dashboard-tab-dialog-editor>
    <new-card-builder
      v-if="showCardBuilderSetup"
      @close="showCardBuilderSetup = false"
      @save="addCard"
    />
    <report-chooser
      v-if="showReportChooser"
      @close="showReportChooser = false"
      @resetreportData="resetreportData()"
      @addElement="addReports"
      @expand="expand"
      @option="setoption"
      @setModuleName="setModuleName"
      :data="componentData"
      :reportLoading="reportLoading"
    />
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
    <f-photoUploader
      v-if="showImageEditer"
      :dialogVisible.sync="showImageEditer"
      :editwidget="false"
      @input="showImageEditer = false"
      @upload-done="addImageCard"
      @upload-failed="imageCardUploadFailed"
    />
    <text-card-editer
      v-if="showtextcard"
      :visibility.sync="showtextcard"
      @add="addtextcard"
      @close="showtextcard = false"
    />
    <section-editer
      v-if="showSectionEditor"
      :visibility.sync="showSectionEditor"
      :section="sectionSelectedForEditing"
      @createSection="createSection"
      @updateSection="updateSection"
    />

    <enable-dashboard-tabs
      v-if="showEnableDashboardModal"
      @save="
        data => {
          showEnableDashboardModal = false
          addNewDashboardTab(data)
        }
      "
      @close="
        () => {
          dashboardOptions.showTabs = !dashboardOptions.showTabs // Since the user press cancel, reset the 'showTabs'.
          showEnableDashboardModal = false
        }
      "
    />
    <disable-dashboard-tabs
      v-if="showDisableTabsModal"
      :tabs="dashboardTabsList"
      @close="
        () => {
          dashboardOptions.showTabs = !dashboardOptions.showTabs // Since the user press cancel, reset the 'showTabs'.
          showDisableTabsModal = false
        }
      "
      @save="
        data => {
          showDisableTabsModal = false
          disableTabs(data)
        }
      "
    />
  </div>
</template>
<script>
import VerticalTabSelector from './VerticalTabSelector'
import MoveToMixin from 'src/pages/new-dashboard/components/dashboard/MoveToMixin.js'
import WidgetWrapper from 'src/pages/new-dashboard/components/widgets/WidgetWrapper.vue'
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'
import CubeLoader from 'src/pages/new-dashboard/components/CubeLoader.vue'
import textCardEditer from '@/TextCardPopup'
import SectionContainer from 'src/pages/new-dashboard/components/groups/SectionContainer.vue'
import SectionEditer from 'src/pages/new-dashboard/components/groups/SectionEditer.vue'
import {
  GridstackItem,
  GridstackLayout,
  GridstackSection,
} from '@facilio/ui/dashboard'
import ReportHelper from 'pages/report/mixins/ReportHelper'
import DateUtil from '@/mixins/DateHelper'
import FPhotoUploader from 'src/pages/new-dashboard/components/reusable-components/FPhotoUploader.vue'
import DashboardHelper from 'src/pages/new-dashboard/components/dashboard/DashboardHelper.js'
import DashboardTopTab from './DashboardTopTab'
import DashboardTabDialogEditor from './DashboardTabDialogEditor'
import NewCardBuilder from 'src/pages/new-dashboard/components/card-builder/CardBuilder.vue'
import CardBuilderDashboardMixin from 'src/pages/new-dashboard/components/card-builder/CardBuilderDashboardMixin.vue'
import { isEmpty } from '@facilio/utils/validation'
import { mapState, mapGetters } from 'vuex'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'
import reportChooser from 'pages/dashboard/DashboardCompoents/ComponentChooser'
import DashboardComponentMixins from 'src/pages/new-dashboard/components/dashboard/DashboardComponentMixins.vue'
import DashboardMixin from './DashboardMixin.js'
import DisableDashboardTabs from './DisableDashboardTabs.vue'
import EnableDashboardTabs from './EnableDashboardTabs.vue'
export default {
  mixins: [
    MoveToMixin,
    ReportHelper,
    DateUtil,
    DashboardHelper,
    CardBuilderDashboardMixin,
    DashboardMixin,
    DashboardComponentMixins,
  ],
  components: {
    DisableDashboardTabs,
    EnableDashboardTabs,
    VerticalTabSelector,
    WidgetWrapper,
    SectionEditer,
    CubeLoader,
    reportChooser,
    GridstackLayout,
    GridstackItem,
    GridstackSection,
    FPhotoUploader,
    NewCardBuilder,
    textCardEditer,
    DashboardTabDialogEditor,
    DashboardTopTab,
    SectionContainer,
  },
  data() {
    return {
      showDisableTabsModal: false,
      showEnableDashboardModal: false,
      loadImmediately: true,
      uid: -1,
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
      showTabOptions: true,
      sectionIndex: -1,
      sectionSelectedForEditing: null,
      showSectionEditor: false,
      render: true,
      selectedModule: 'Maintenance',
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
      dashboard: { label: '' },
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
      dashboardLayout: [],
      deletlayout: [],
      webView: [],
      sharingDialogVisible: false,
      removeChartList: [],
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
      readingcardlegend: ['Sum', 'Avg', 'Min', 'Max'],
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
      dragedElement: 'test',
      showImageEditer: false,
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
      graphicsList: null,
      connectedAppWidgetList: null,
      dashboardTabContexts: null,
      dashboardTabLoading: null,
      showTemplateReportDialog: false,
      currentTemplateReportId: null,
      dashboardTabEditorVisibility: false,
      showWidgetHelpTextConfig: false,
      widgetForHelpTextConfig: null,
      widgetConfigMap: {},
    }
  },
  computed: {
    dashboardTabsList() {
      return (this.dashboardTabContexts ?? []).reduce((list, tab) => {
        list.push({
          id: tab.id,
          name: tab.name,
          expand: false,
        })
        if (!isEmpty(tab?.childTabs)) {
          for (let childTab of tab.childTabs) {
            list.push({
              id: childTab.id,
              name: childTab.name,
            })
          }
        }
        return list
      }, [])
    },
    isLazyDashboard() {
      return true
    },
    ...mapState({
      groups: state => state.groups,
      users: state => state.users,
      roles: state => state.roles,
    }),
    ...mapGetters(['getCurrentUser']),
    getLicenseEnabledModules() {
      return this.modules.filter(module => {
        if (this.$helpers.isLicenseEnabled(module.license)) {
          return module
        }
      })
    },
    dashboardLink() {
      return this.$route.params.dashboardlink
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
    viewOrEdit() {
      return 'edit'
    },
    dashboardTabId() {
      return this.$route?.query?.tab ? parseInt(this.$route?.query?.tab) : null
    },
    canShowNewCardBuilder() {
      return true
    },
  },
  mounted() {
    this.init()
    this.subscribeEventBus()
    this.loadReportsData()
  },
  beforeDestroy() {
    this.unsubscribeEventBus()
  },
  watch: {
    dashboardLink() {
      // When the dashboard changes (dashboardLink, is the name of the dashboard!), reload the component again.
      this.loadDashboard()
    },
    $route() {
      this.openFirstReport()
    },
    dashboardTabId: {
      handler: async function(newVal) {
        if (newVal) {
          if (this.dashboardLayouts[this.dashboardTabId]) {
            this.changeDashboardLayout({
              layout: cloneDeep(this.dashboardLayouts[this.dashboardTabId]),
            })
          } else {
            await this.loadDashboardTabs()
          }
        }
      },
    },
  },
  methods: {
    adjustSectionHeight(sectionId) {
      // Got rekt! In some cases Gridstack section is not emitting change event.
      // Assume a Gridstack section having two widgets A and B, widget B is vertically below widget A.
      // If we move the widget A to the master grid from the section grid, the change event for widget B is not
      // get emitted, this bug is present in the package itself, can't do anything about it right now.
      const { dashboardLayout } = this
      const section = dashboardLayout.find(section => section.id == sectionId)
      const { id, collapsed, children: widgets } = section
      if (!collapsed && !isEmpty(widgets)) {
        const SECTION_HEAD_HEIGHT = 7
        const PADDING_BOTTOM = 1
        const SECTION_BODY_HEIGHT = Math.max(...widgets.map(o => o.y + o.h))
        const TOTAL_HEIGHT =
          SECTION_HEAD_HEIGHT + SECTION_BODY_HEIGHT + PADDING_BOTTOM
        this.$refs['gridstack'].updateItem(id, { h: TOTAL_HEIGHT })
      }
    },
    get_uid() {
      return String(this.uid--) // Number is a primitive, no need to cloneDeep.
    },
    loadReportsData() {
      this.loadNewReportTree()
      if (this.$helpers.isLicenseEnabled('GRAPHICS')) {
        this.loadGraphicsList()
      }
    },
    loadNewReportTree() {
      let self = this
      this.reportLoading = true

      let url = '/v3/report/folders?moduleName='
      // Lame, hard code edgecase...
      if (this.moduleName === 'energy') {
        url += 'energyData'
      } else if (this.moduleName === 'custom') {
        url += 'custommodule'
      } else if (
        this.moduleName === 'newreadingalarm' ||
        this.moduleName === 'bmsalarm'
      ) {
        url += 'alarm'
      } else {
        url += this.moduleName
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
    loadGraphicsList() {
      API.get('/v2/graphics/list').then(response => {
        this.graphicsList = response.data.graphics_list
        this.graphicsList.forEach(rt => {
          this.$set(rt, 'selected', false)
          this.$set(rt, 'disabled', false)
        })
      })
    },
    handleHelpTextChange({ id, helpText, widgetSettings }) {
      if (this.dashboardLayout) {
        const { index, childIndex } = this.findIndexUsingId(id)
        if (!isEmpty(childIndex)) {
          this.$set(
            this.dashboardLayout[index].children[childIndex].widget,
            'widgetSettings',
            widgetSettings
          )
          this.$set(
            this.dashboardLayout[index].children[childIndex].widget,
            'helpText',
            helpText
          )
        } else {
          this.$set(
            this.dashboardLayout[index].widget,
            'widgetSettings',
            widgetSettings
          )
          this.$set(this.dashboardLayout[index].widget, 'helpText', helpText)
        }
        this.showWidgetHelpTextConfig = false
      }
    },
    setModuleName(name) {
      this.moduleName = name
      this.loadNewReportTree()
      this.loadReportTree()
    },
    loadReportTree() {
      let self = this
      this.reportLoading = true

      let url = '/report/workorder/getAllWorkOrderReports?moduleName='
      if (this.moduleName === 'energy') {
        url += 'energyData'
      } else {
        url += this.moduleName
      }
      if (this.moduleName !== 'custom') {
        API.get(url)
          .then(response => {
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
    },
    setDefaultDashboard() {
      const FIRST_FOLDER_INDEX = 0
      this.selectedDashoardFolder =
        this.dashboardFolderList[FIRST_FOLDER_INDEX] ?? {}
    },
    getIndex(index) {
      return index
    },
    fetchDashboardUsingLinkName() {
      const url = '/v3/dashboard/' + this.dashboardLink
      return API.get(url, {}, { force: true })
    },
    async loadDashboard() {
      this.loading = true
      const { appId } = this.$route?.query ?? {}
      let self = this
      const { data, error } = await this.fetchDashboardUsingLinkName()
      if (isEmpty(error)) {
        const dashboard = data.dashboardJson[0]
        self.dashboard = dashboard
        const { tabs, tabEnabled, dashboardTabPlacement } = dashboard ?? {}
        self.dashboardTabContexts = tabs
        if (!isEmpty(tabs) && tabEnabled) {
          // For dashboard in which 'tabs' are enabled...
          self.dashboardOptions.showTabs = tabEnabled
          if (dashboardTabPlacement === -1) {
            self.dashboardOptions.dashboardTabPlacement = 2 // Default position for tabs is 'left'...
          } else {
            self.dashboardOptions.dashboardTabPlacement = dashboardTabPlacement
          }
          // Sometimes the user comes to a dashboard in which tabs are enabled using just the dashboard's linkName without a tabId.
          // Set the first tab in route url if this.dashboardTabId (Computed comes from route url) is not set.
          if (!self.dashboardTabId) {
            const [firstTab] = tabs ?? []
            const query = {
              create: self.$route.query.create,
              tab: firstTab?.id,
            }
            if (!isEmpty(appId)) {
              query.appId = appId
            }
            self.$router.replace({
              path: self.$route.path,
              query,
            })
          }
          // TODO: Make the tabViewer reflect the tab name when the tab is inside a master tab on load.
          // Tabs will be loaded via a watcher called 'dashboardTabId'...
        } else {
          // Dashboards in which 'tabs' are not enabled...
          self.dashboardTabContexts = null
          const query = {
            create: self.$route.query.create,
          }
          if (!isEmpty(appId)) {
            query.appId = appId
          }
          self.$router.replace({
            path: self.$route.path,
            query,
          })
          self.prepareDashboardLayout(cloneDeep(dashboard.children))
        }
        self.setFolder()
      } else {
        self.$message.error(error.message ?? 'Error Occurred')
      }
      this.loading = false
    },
    setFolder() {
      if (this.mode === 'edit' && !isEmpty(this.dashboardFolderList)) {
        const { dashboardFolderId } = this.dashboard
        this.selectedDashoardFolder = this.dashboardFolderList.find(
          folder => folder.id === dashboardFolderId
        )
      }
    },
    changeDashboardTabId(id) {
      if (id && id > 0 && this.dashboardTabId !== id) {
        this.dashboardLayouts[this.dashboardTabId] = cloneDeep(
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
    fetchDashboardTab(tabId) {
      return API.get('/v3/dashboard/tab/' + tabId)
    },
    async loadDashboardTabs() {
      const self = this
      this.dashboardTabLoading = true
      const { data, error } = await this.fetchDashboardTab(self.dashboardTabId)
      if (!error) {
        const {
          dashboardTabContext: { clientWidgetJson },
        } = data ?? {}
        self.dashboard['children'] = clientWidgetJson
        this.prepareDashboardLayout(cloneDeep(clientWidgetJson))
      } else {
        self.$message.error(error?.message ?? 'Error Occurred')
      }
      this.dashboardTabLoading = false
    },
    async saveDashboard() {
      if (!isEmpty(this.dashboard.label)) {
        if (!isEmpty(this.selectedDashoardFolder)) {
          this.dashboardSave = true
          if (this.mode == 'new') {
            await this.saveNewDashboard()
          } else if (this.mode == 'edit') {
            await this.saveOldDashboard()
          }
        } else {
          this.$message.error('Please select a folder.')
        }
      } else {
        this.$message.error('Please name the dashboard')
      }
    },
    async saveNewDashboard() {
      // While creating a new dashboard the user can't create and switch tabs because we hide features related to tabs in new dashboard creation mode.
      const self = this
      const { data, error } = await API.get('/dashboard/supportedmodules')
      if (!error) {
        const selectedModule = data.modules.find(i => i.name == 'workorder')
        await self.createNewDashboard(selectedModule.moduleId)
      } else {
        self.$message.error(error?.message ?? 'Error Occurred')
      }
    },
    async createNewDashboard(moduleId) {
      const self = this
      const req = {
        dashboard: {
          dashboardName: this.dashboard.label,
          dashboardFolderId: this.selectedDashoardFolder.id,
          moduleId, // TODO: This is hardcode, need to remove in next release.
        },
      }
      const { error, data } = await API.post('/dashboard/add', req)
      if (!error) {
        const { dashboard } = data ?? {}
        const dashboardObj = {
          dashboard_meta: {
            id: dashboard.id,
            dashboardFolderId: dashboard.dashboardFolderId,
            dashboardName: dashboard.dashboardName,
            tabEnabled: dashboard.tabEnabled,
            dashboardWidgets: self.getDashboardWidgets(
              cloneDeep(self.dashboardLayout)
            ),
            linkName: dashboard.linkName,
          },
        }
        await self.submitDashboard(dashboardObj)
      } else {
        self.$message.error(error?.message ?? 'Error Occurred')
      }
    },
    async saveTab({ redirect = true, widgets = [] } = {}) {
      const self = this
      const dashboardObj = {
        dashboard_meta: {
          id: self.dashboard.id, // For now, if linkName is used use id, for tabs use dashboardId.
          dashboardId: self.dashboard.id,
          tabId: self.dashboardTabId,
          tabEnabled: self.dashboardOptions.showTabs,
          dashboardTabPlacement: self.dashboardOptions.dashboardTabPlacement,
          dashboardName: self.dashboard.label,
          linkName: self.dashboardLink,
          dashboardWidgets: !isEmpty(widgets)
            ? this.getDashboardWidgets(cloneDeep(widgets))
            : this.getDashboardWidgets(cloneDeep(this.dashboardLayout)),
          dashboardFolderId: self.selectedDashoardFolder?.id,
        },
      }
      await self.submitDashboard(dashboardObj, redirect)
    },
    async saveMultiTab() {
      const self = this
      // Save all the recent changes done to the dashboardLayouts hashMap so that we can update all the tabs in one shot.
      this.dashboardLayouts[this.dashboardTabId] = cloneDeep(
        this.dashboardLayout
      )
      const tabNames = (this.dashboardTabContexts ?? []).reduce(
        (tabNames, rt) => {
          tabNames[rt.id] = rt.name
          if (!isEmpty(rt?.childTabs)) {
            rt.childTabs.forEach(rl => {
              tabNames[rl.id] = rl.name
            })
          }
          return tabNames
        },
        {}
      )
      const tabs = Object.keys(this.dashboardLayouts).reduce((tabs, key) => {
        const dashboardLayout = cloneDeep(self.dashboardLayouts[key])
        const tabName = tabNames[key]
        const layout = self.prepareDashboardTab(
          dashboardLayout,
          Number(key),
          tabName
        )
        tabs.push(layout)
        return tabs
      }, [])
      const dashboardObj = {
        dashboard_meta: {
          dashboardName: self.dashboard.label,
          dashboardId: this.dashboard.id,
          tabs: tabs,
          multi_tab_update: true,
        },
      }
      await this.submitDashboard(dashboardObj)
    },
    async submitDashboard(dashboardObj, redirect = true) {
      const self = this
      const { tabEnabled, multi_tab_update, linkName: newDashboardLink } =
        dashboardObj?.dashboard_meta ?? {}
      let url = ''
      if ((tabEnabled || multi_tab_update) && redirect) {
        url = '/v3/dashboard/tab/update'
      } else {
        url = '/v3/dashboard/update'
      }
      const { data, error } = await API.post(url, dashboardObj)
      if (isEmpty(error)) {
        self.$message({
          message: 'Dashboard updated successfully!',
          type: 'success',
        })
        if (redirect) {
          const link = self.newconstractUrl(newDashboardLink)
          self.$router.push(link)
        }
      } else {
        self.$message.error(error?.message ?? 'Error Occurred')
      }
      self.dashboardSave = false
    },
    async saveOldDashboard() {
      if (Object.keys(this.dashboardLayouts).length) {
        await this.saveMultiTab() // Save multiple tabs of a dashboard in one shot!
      } else {
        await this.saveTab() // Save a 'single tab' or a dashboard with tab disabled...
      }
    },
    formatWidgetData(item, i) {
      // This method needs to be refactored by someone experience...
      // Don't touch this...
      const gridItem = cloneDeep(item)
      let widgetData = null
      let reportTemplate = gridItem?.widget?.dataOptions?.reportTemplate ? gridItem.widget.dataOptions.reportTemplate : null
      if (typeof reportTemplate === 'object') {
        reportTemplate = JSON.stringify(reportTemplate)
      }

      if (gridItem.widget.type === 'card') {
        widgetData = cloneDeep({
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
        })
      } else {
        widgetData = cloneDeep({
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
        })
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
        widgetData.paramsJson = gridItem.widget.dataOptions.params.paramsJson
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
        widgetData.paramsJson = gridItem.widget.dataOptions.params.paramsJson
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
        widgetData.paramsJson = gridItem.widget.dataOptions.params.paramsJson
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
      widgetData.id = gridItem.id < 0 ? -1 : gridItem.id // Convert random negative number to -1 for db to know that it is a new widget.
      return cloneDeep(widgetData)
    },
    getDashboardWidgets(layout) {
      const self = this
      let dashboardWidgets = []
      if (layout !== null) {
        layout.forEach((widget, index) => {
          if (widget.children) {
            let section = []
            widget.banner_meta = JSON.stringify(widget.banner_meta) // The backend people wants this property value as string.
            widget.children.forEach((childWidget, childIndex) => {
              section.push(self.formatWidgetData(childWidget, childIndex))
            })
            delete widget.children
            widget['section'] = section
            widget.id = widget.id < 0 ? -1 : widget.id
            dashboardWidgets.push(widget)
          } else {
            dashboardWidgets.push(self.formatWidgetData(widget, index))
          }
        })
      }
      return dashboardWidgets
    },

    newconstractUrl(dashboardLink = null) {
      const dashboardlink = !isEmpty(dashboardLink)
        ? dashboardLink
        : this.dashboardLink
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.DASHBOARD_VIEWER) || {}
        if (name) {
          return this.$router.resolve({ name, params: { dashboardlink } }).href
        }
      } else {
        return '/app/home/dashboard/' + dashboardlink
      }
    },
    cancel() {
      this.$router.push(this.newconstractUrl())
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
        } else if (
          self.reportTree.length &&
          self.reportTree[0].children.length
        ) {
          self.reportTree[0].expand = true
        }
      }
    },
    expand(folder) {
      this.$set(folder, 'expand', !folder.expand)
      this.changeDashboardTabId(folder.id)
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
    showtextCardPopUp() {
      this.showtextcard = true
    },
    showEmptyDashboard() {
      this.showTabOptions = false
      this.dashboardLayout = []
      this.dashboardTabLoading = false
      this.loading = false
      this.showGridstackComponent = true
    },
    async init() {
      const self = this
      await this.loadFolder()
      if (self.mode === 'edit') {
        self.loadDashboard()
      } else if (self.mode === 'new') {
        self.setDefaultDashboard() // Set the first folder in the list as current folder.
        self.showEmptyDashboard()
      }
    },
    async loadFolder() {
      const self = this
      const { appId } = this.$route?.query ?? {}
      let url = '/dashboard/getDashboardFolder'
      if (!isEmpty(appId)) {
        url += '?appId=' + appId
      }
      const { data, error } = await API.get(url)
      if (isEmpty(error)) {
        self.dashboardFolderList = data.dashboardFolders
      } else {
        this.$message.error(error.message ?? 'Error Occurred')
      }
    },
    handleSelect(item) {
      this.dashbaordFolderName = item.name
      this.selectedDashoardFolder = item
    },
    toggleDashboardTabEditor() {
      this.dashboardTabEditorVisibility = !this.dashboardTabEditorVisibility
    },
  },
}
</script>
<style lang="scss">
@import './styles/dashboard-styles.scss';
</style>
<style lang="scss">
// Used for override element-ui styles.
.create-dashboard-input-title .el-input__inner {
  color: #324056;
  font-size: 20px;
  font-weight: 500;
  padding-left: 5px;
  letter-spacing: 0.6px;
}
</style>
<style lang="scss">
.dashboard-container {
  height: 100%;
  overflow-y: scroll;
  width: 100%;
  padding-bottom: 70px;
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
</style>
