<template>
  <div class="db-user-filter-manager">
    <el-dialog
      :visible="dialogVisible"
      class="db-user-filter-manager-dialog"
      :show-close="false"
      :append-to-body="true"
      title="Add components"
      width="70%"
      :before-close="handleClose"
    >
      <div class="body">
        <!-- <spinner v-if="loading" :show="loading" :size="80"></spinner> -->
        <!-- <div
          v-if="selectedReportsWithFolder.length == 0"
          class="user-filter-module-empty"
        >
          <div
            class="col-12 flex-middle justify-content-center flex-direction-column height60vh pT30"
          >
            <inline-svg
              src="svgs/empty-dashboard"
              iconClass="text-center icon-xxxlg"
            ></inline-svg>
            <div class="fc-black-24 pT20">
              No data Available
            </div>
          </div>
        </div> -->

        <div class="filter-config-container">
          <div class="cp-border-right width20">
            <div class="filter-config-accordian">
              <div
                class="accordian-item"
                v-for="(value, key) in tabs"
                :key="key"
              >
                <div
                  class="accordian-item-tab pointer"
                  v-bind:class="{ active: activeName === value }"
                  @click="setOption(value)"
                >
                  {{ key }}
                </div>
              </div>
            </div>
          </div>
          <div class=" row width50" v-if="activeName === 'analytics'">
            <div
              class="header-area p20 col-12 cp-border-right cp-border-bottom"
            >
              <div class="report-search row">
                <div class="col-4">
                  <!-- <p class="fc-input-label-txt pB5">Module</p> -->
                  <el-select
                    v-model="selectAnalyticsType"
                    filterable
                    class="fc-input-full-border-select2  width100 pR10"
                  >
                    <el-option
                      v-for="(value, key) in analyticsReportTypes"
                      :key="key"
                      :value="value"
                      :label="key"
                    >
                    </el-option>
                  </el-select>
                </div>
                <div class="col-4"></div>
                <div class="col-4">
                  <!-- <p class="fc-input-label-txt pB5">Search</p> -->
                  <el-input
                    :autofocus="true"
                    v-model="search.analytics"
                    suffix-icon="el-icon-search"
                    class="width100 fc-input-full-border2"
                    placeholder="Search"
                    @input="searchReport()"
                  ></el-input>
                </div>
              </div>
            </div>
            <div class=" row width100" style="height: calc(60vh - 80px);">
              <div class="filter-config-left col-12" v-if="analyticsLoading">
                <spinner :show="true" size="80"></spinner>
              </div>
              <div class="filter-config-left col-12" v-else>
                <div
                  class="filter-config-accordian"
                  v-if="
                    filteredAnalyticsReports && filteredAnalyticsReports.length
                  "
                >
                  <div
                    class="accordian-item"
                    v-for="(folder, moduleIndex) in filteredAnalyticsReports"
                    :key="moduleIndex"
                  >
                    <div
                      class="accordian-item-header pointer bonder-top-none"
                      @click="expand(folder)"
                    >
                      {{ folder.name }}
                      <i
                        class="accordian-arrow"
                        :class="[{ expanded: folder.expand }]"
                      ></i>
                    </div>
                    <div class="accordian-item-body" v-if="folder.expand">
                      <div class="module-fields-container">
                        <div
                          class="module-field"
                          v-for="(report, fieldIndex) in folder.reports.filter(
                            rt => !rt.disabled
                          )"
                          :key="fieldIndex"
                        >
                          <el-checkbox
                            style="border: none;"
                            v-model="report.selected"
                            :label="report.name"
                            @change="getselectedData(report, fieldIndex)"
                          ></el-checkbox>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <template v-else>
                  <div style="margin-top: 10%;" class="emptyIcon text-center">
                    <inline-svg
                      src="svgs/empty-dashboard"
                      iconClass="text-center icon-xlg"
                    ></inline-svg>
                    <div class="fc-black-24  f14 text-center">
                      No Reports available
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </div>
          <!-- PIVOT START -->
          <div class=" row width50" v-if="activeName === 'pivot'">
            <div
              class="header-area p20 col-12 cp-border-right cp-border-bottom"
            >
              <div class="report-search row">
                <div class="col-8"></div>
                <div class="col-4">
                  <!-- <p class="fc-input-label-txt pB5">Search</p> -->
                  <el-input
                    :autofocus="true"
                    v-model="search.pivotReports"
                    suffix-icon="el-icon-search"
                    class="width100 fc-input-full-border2"
                    placeholder="Search"
                    @input="searchReport()"
                  ></el-input>
                </div>
              </div>
            </div>
            <div class=" row width100" style="height: calc(60vh - 80px);">
              <div class="filter-config-left col-12" v-if="pivotReportsLoading">
                <spinner :show="true" size="80"></spinner>
              </div>
              <div class="filter-config-left col-12" v-else>
                <div
                  class="filter-config-accordian"
                  v-if="pivotReportsList && pivotReportsList.length"
                >
                  <div
                    class="accordian-item"
                    v-for="(folder, moduleIndex) in pivotReportsList"
                    :key="moduleIndex"
                  >
                    <div
                      class="accordian-item-header pointer bonder-top-none"
                      @click="expand(folder)"
                    >
                      {{ folder.name }}
                      <i
                        class="accordian-arrow"
                        :class="[{ expanded: folder.expand }]"
                      ></i>
                    </div>
                    <div class="accordian-item-body" v-if="folder.expand">
                      <div class="module-fields-container">
                        <div
                          class="module-field"
                          v-for="(report, fieldIndex) in folder.reports.filter(
                            rt => !rt.disabled
                          )"
                          :key="fieldIndex"
                        >
                          <el-checkbox
                            style="border: none;"
                            v-model="report.selected"
                            :label="report.name"
                            @change="getselectedData(report, fieldIndex)"
                          ></el-checkbox>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <template v-else>
                  <div style="margin-top: 10%;" class="emptyIcon text-center">
                    <inline-svg
                      src="svgs/empty-dashboard"
                      iconClass="text-center icon-xlg"
                    ></inline-svg>
                    <div class="fc-black-24  f14 text-center">
                      No Reports available
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </div>
          <!-- PIVOT END -->
          <div class=" row width50" v-if="activeName === 'report'">
            <div
              class="header-area p20 col-12 cp-border-right cp-border-bottom"
            >
              <div class="report-search row">
                <div class="col-4">
                  <!-- <p class="fc-input-label-txt pB5">Module</p> -->
                  <el-select
                    @change="setModuleName"
                    v-model="selectedModule"
                    filterable
                    class="fc-input-full-border-select2  width100 pR10"
                  >
                    <el-option
                      v-for="(m, mIdx) in modulesList"
                      :key="mIdx"
                      :value="m.name"
                      :label="m.displayName"
                    >
                    </el-option>
                  </el-select>
                </div>
                <div class="col-4"></div>
                <div class="col-4">
                  <!-- <p class="fc-input-label-txt pB5">Search</p> -->
                  <el-input
                    :autofocus="true"
                    v-model="search.report"
                    suffix-icon="el-icon-search"
                    class="width100 fc-input-full-border2"
                    placeholder="Search"
                    @input="searchReport()"
                  ></el-input>
                </div>
              </div>
            </div>
            <div class=" row width100" style="height: calc(60vh - 80px);">
              <div class="filter-config-left col-12" v-if="reportLoading">
                <spinner :show="true" size="80"></spinner>
              </div>
              <div class="filter-config-left col-12" v-else>
                <div
                  class="filter-config-accordian"
                  v-if="
                    selectedReportsWithFolder &&
                      selectedReportsWithFolder.length
                  "
                >
                  <div
                    class="accordian-item"
                    v-for="(folder, moduleIndex) in selectedReportsWithFolder"
                    :key="moduleIndex"
                  >
                    <div
                      class="accordian-item-header pointer bonder-top-none"
                      @click="expand(folder)"
                    >
                      {{ folder.name }}
                      <i
                        class="accordian-arrow"
                        :class="[{ expanded: folder.expand }]"
                      ></i>
                    </div>
                    <div class="accordian-item-body" v-if="folder.expand">
                      <div class="module-fields-container">
                        <div
                          class="module-field"
                          v-for="(report, fieldIndex) in folder.reports.filter(
                            rt => !rt.disabled
                          )"
                          :key="fieldIndex"
                        >
                          <el-checkbox
                            style="border: none;"
                            v-model="report.selected"
                            :label="report.name"
                            @change="getselectedData(report, fieldIndex)"
                          ></el-checkbox>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <template v-else>
                  <div style="margin-top: 10%;" class="emptyIcon text-center">
                    <inline-svg
                      src="svgs/empty-dashboard"
                      iconClass="text-center icon-xlg"
                    ></inline-svg>
                    <div class="fc-black-24  f14 text-center">
                      No Reports available
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </div>
          <div class=" row width50" v-else-if="activeName === 'view'">
            <div
              class="header-area p20 col-12 cp-border-right  cp-border-bottom"
            >
              <div class="report-search row">
                <div class="col-4">
                  <!-- <p class="fc-input-label-txt pB5">Module</p> -->
                  <el-select
                    @change="setModuleName"
                    v-model="selectedModule"
                    filterable
                    class="fc-input-full-border-select2 width100 pR10"
                  >
                    <el-option
                      v-for="(m, mIdx) in modulesList"
                      :key="mIdx"
                      :value="m.name"
                      :label="m.displayName"
                    >
                    </el-option>
                  </el-select>
                </div>
                <div class="col-4"></div>
                <div class="col-4">
                  <!-- <p class="fc-input-label-txt pB5">Search</p> -->
                  <el-input
                    :autofocus="true"
                    v-model="search.view"
                    suffix-icon="el-icon-search"
                    class="width100 fc-input-full-border2"
                    placeholder="Search"
                    @input="searchReport()"
                  ></el-input>
                </div>
              </div>
            </div>
            <div class=" row width100" style="height: calc(60vh - 80px);">
              <div class="filter-config-left col-12" v-if="viewLoading">
                <spinner :show="true" size="80"></spinner>
              </div>
              <div class="filter-config-left col-12" v-else>
                <div
                  class="filter-config-accordian"
                  v-if="
                    selectediVewsWithFolder && selectediVewsWithFolder.length
                  "
                >
                  <div
                    class="accordian-item"
                    v-for="(folder, moduleIndex) in selectediVewsWithFolder"
                    :key="moduleIndex"
                  >
                    <div
                      class="accordian-item-header pointer border-top-none"
                      @click="expand(folder)"
                    >
                      {{ folder.label }}
                      <i
                        class="accordian-arrow"
                        :class="[{ expanded: folder.expand }]"
                      ></i>
                    </div>
                    <div class="accordian-item-body" v-if="folder.expand">
                      <div class="module-fields-container">
                        <div
                          class="module-field"
                          v-for="(report, fieldIndex) in folder.views.filter(
                            rt => !rt.disabled
                          )"
                          :key="fieldIndex"
                        >
                          <el-checkbox
                            style="border: none;"
                            v-model="report.selected"
                            :label="report.displayName"
                            @change="getselectedData(report, fieldIndex)"
                          ></el-checkbox>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <template v-else>
                  <div style="margin-top: 10%;" class="emptyIcon text-center">
                    <inline-svg
                      src="svgs/empty-dashboard"
                      iconClass="text-center icon-xlg"
                    ></inline-svg>
                    <div class="fc-black-24  f14 text-center">
                      No Views available
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </div>
          <div
            class=" row width50"
            v-else-if="
              activeName === 'graphics' && $helpers.isLicenseEnabled('GRAPHICS')
            "
          >
            <div
              class="header-area p20 col-12 cp-border-right cp-border-bottom"
            >
              <div class="report-search row">
                <div class="col-4">
                  <!-- <p class="fc-input-label-txt pB5">Module</p> -->
                  <el-select
                    v-model="selectedAssetCatergoryId"
                    filterable
                    class="fc-input-full-border-select2 width100 pR10"
                  >
                    <el-option
                      v-for="(m, mIdx) in filteredAssetCategory"
                      :key="mIdx"
                      :value="m.id"
                      :label="m.name"
                    >
                    </el-option>
                  </el-select>
                </div>
                <div class="col-4"></div>
                <div class="col-4">
                  <!-- <p class="fc-input-label-txt pB5">Search</p> -->
                  <el-input
                    :autofocus="true"
                    v-model="search.graphics"
                    suffix-icon="el-icon-search"
                    class="width100 fc-input-full-border2"
                    placeholder="Search"
                    @input="searchReport()"
                  ></el-input>
                </div>
              </div>
            </div>
            <div
              class="filter-config-left col-12"
              style="height: calc(60vh - 80px);overflow: auto;"
            >
              <div class="filter-config-accordian">
                <div
                  class="accordian-item"
                  v-for="(folder, moduleIndex) in getfullGraphicsList"
                  :key="moduleIndex"
                >
                  <div
                    class="accordian-item-header pointer border-top-none"
                    @click="expand(folder)"
                  >
                    {{ folder.label }}
                    <i
                      class="accordian-arrow"
                      :class="[{ expanded: folder.expand }]"
                    ></i>
                  </div>
                  <div class="accordian-item-body" v-if="folder.expand">
                    <div class="module-fields-container">
                      <div
                        class="module-field"
                        v-for="(report, fieldIndex) in folder.list.filter(
                          rt => !rt.disabled
                        )"
                        :key="fieldIndex"
                      >
                        <el-checkbox
                          style="border: none;"
                          v-model="report.selected"
                          :label="report.name"
                          @change="getselectedData(report, fieldIndex)"
                        ></el-checkbox>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class=" row width30">
            <div class=" row width100" style="height: calc(60vh - 10px);">
              <div class="filter-config-right col-12">
                <div class="selcted-filters-title">
                  SELECTED COMPONENTS
                </div>

                <template v-if="selectedData && selectedData.length">
                  <draggable
                    :list="selectedData"
                    v-bind="fieldSectionDragOptions"
                  >
                    <div
                      v-for="(report, index) in selectedData"
                      :key="index"
                      class="mB10 mT15"
                    >
                      <div class="field-row">
                        <div class="task-handle mR10 pointer mT10">
                          <img src="~assets/drag-grey.svg" />
                        </div>

                        <div class="mT-auto mB-auto">
                          <div
                            v-if="
                              report.optionName && report.optionName === 'view'
                            "
                          >
                            {{ report.displayName || '' }}
                          </div>
                          <div v-else>
                            {{ report.name || '' }}
                          </div>
                          <div
                            class="secondary-text pT5"
                            style="color: #c0c4cb;font-weight: 400;font-size: 11px;"
                          >
                            {{
                              report.typeDisplayName
                                ? `${report.typeDisplayName} ${
                                    report.moduleDisplayName
                                      ? 'in ' + report.moduleDisplayName
                                      : ''
                                  }`
                                : ''
                            }}
                          </div>
                        </div>

                        <div class="mL-auto mT-auto mB-auto">
                          <div class="mR10 inline">
                            <i
                              class="el-icon-delete pointer trash-icon"
                              @click="
                                ;(report.selected = false),
                                  deleteselectedData(index)
                              "
                            ></i>
                          </div>
                        </div>
                      </div>
                    </div>
                  </draggable>
                </template>
                <template v-else>
                  <div style="margin-top: 50%;" class="emptyIcon text-center">
                    <inline-svg
                      src="svgs/empty-dashboard"
                      iconClass="text-center icon-xlg"
                    ></inline-svg>
                    <div class="fc-black-24  f14 text-center">
                      No components selected
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="dialog-save-cancel">
        <el-button class="modal-btn-cancel" @click="handleClose">
          Cancel</el-button
        >
        <el-button type="primary" class="modal-btn-save" @click="addToDashboard"
          >Done</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import draggable from 'vuedraggable'
import { API } from '@facilio/api'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'

export default {
  components: {
    draggable,
  },
  props: ['data', 'reportLoading'],
  data() {
    return {
      analyticsLoading: false,
      pivotReportsLoading: false,
      dialogVisible: true,
      viewLoading: true,
      selectedModule: 'workorder',
      selectAnalyticsType: 'all',
      selectedAssetCatergoryId: -1,
      activeName: 'analytics',
      fieldSectionDragOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
      tabs: {
        'Analytical Reports': 'analytics',
        'Module Reports': 'report',
        'Pivot Reports': 'pivot',
        Views: 'view',
        Graphics: 'graphics',
      },
      search: {
        report: '',
        view: '',
        graphics: '',
        analytics: '',
        pivotReports: '',
      },
      analyticsReportTypes: {
        All: 'all',
        Portfolio: 'PORTFOLIO',
        Building: 'BUILDING',
        Heatmap: 'HEAT_MAP',
        Treemap: 'TREE_MAP',
        Scatter: 'SCATTER',
      },
      selectedReports: [],
      selectedviews: [],
      selectedData: [],
      modulesList: [],
      viewsList: [],
      analyticsReports: [],
      pivotReports: [],
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.getView()
    this.loadAnalyticsReport()
    this.loadPivotReports()
    API.get(`v2/automation/module`).then(({ data }) => {
      this.modulesList = data.modules
      this.modulesList.forEach(rt => {
        this.$set(rt, 'id', rt.moduleId)
      })
      this.modulesList.push({
        displayName: 'Building performance',
        name: 'energydata',
        id: -1,
      })
      this.modulesList.push({
        displayName: 'Fault detection and diagnostics',
        name: 'alarm',
        id: -2,
      })
    })
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
    filteredAssetCategory() {
      let data = {
        name: 'All',
        id: -1,
      }
      return [{ ...data }, ...this.assetCategory]
    },
    getLicenseEnabledModules() {
      let { getLicenseEnabledModules } = this.data
      if (getLicenseEnabledModules) {
        return getLicenseEnabledModules
      }
      return []
    },
    newReportTree() {
      let { reports } = this.data
      let { search } = this
      if (reports) {
        reports.forEach(folder => {
          if (folder.reports) {
            folder.reports.forEach(report => {
              report.folderName = folder.name
              report.typeDisplayName = 'Module Report'
              report.optionName = 'report'
              if (folder.moduleId) {
                report.moduleDisplayName = this.getModuleName(folder.moduleId)
              }
              this.$set(report, 'selected', this.isSelectedData(report))
              if (this.compareString(report.name, search.report)) {
                report.disabled = false
              } else {
                report.disabled = true
              }
            })
          }
        })
        return reports.filter(rt => {
          if (rt.reports) {
            let rp = rt.reports.filter(report => !report.disabled)
            if (rp && rp.length) {
              return rp
            }
          }
          return null
        })
      }
      return []
    },
    reportTree() {
      let { reportTree } = this.data
      let { search } = this
      if (reportTree) {
        reportTree.forEach(folder => {
          if (folder.reports) {
            folder.reports.forEach(report => {
              report.folderName = folder.name
              report.typeDisplayName = 'Module Report'
              report.optionName = 'report'
              if (folder.moduleId) {
                report.moduleDisplayName = this.getModuleName(folder.moduleId)
              }
              this.$set(report, 'selected', this.isSelectedData(report))
              if (this.compareString(report.name, search.report)) {
                report.disabled = false
              } else {
                report.disabled = true
              }
            })
          }
        })
        return reportTree.filter(rt => {
          if (rt.reports) {
            let rp = rt.reports.filter(report => !report.disabled)
            if (rp && rp.length) {
              return rp
            }
          }
          return null
        })
      }
      return []
    },
    allreports() {
      let { reports, reportTree } = this.data
      if (reports && !reportTree) {
        return reports.map(rt => rt.reports)
      } else if (!reports && reportTree) {
        return this.concatArray(reportTree.map(rt => rt.reports))
      } else if (reports && reportTree) {
        return [
          ...this.concatArray(reports.map(rt => rt.reports)),
          ...this.concatArray(reportTree.map(rt => rt.reports)),
        ]
      }
      return []
    },
    selectedReportsWithFolder() {
      let { newReportTree, reportTree } = this
      let r = []
      let rt = []
      if (newReportTree && newReportTree.length) {
        r = newReportTree
      }
      if (reportTree && reportTree.length) {
        rt = reportTree
      }

      return [...r, ...rt]
    },
    selectediVewsWithFolder() {
      // let { supportedModules, customModuleList } = this
      // let r = []
      // let rt = []
      // if (supportedModules && supportedModules.length) {
      //   r = supportedModules
      // }
      // if (customModuleList && customModuleList.length) {
      //   rt = customModuleList
      // }

      // return [...r, ...rt]
      return this.supportedModules
    },
    supportedModules(module) {
      // let { supportedModules } = this.data
      let { viewsList } = this
      let { search } = this
      if (viewsList) {
        viewsList.forEach(folder => {
          if (folder.views) {
            folder.views.forEach(report => {
              report.folderName = folder.label
              report.typeDisplayName = 'View'
              report.optionName = 'view'
              if (module) {
                report.moduleName = module.selectedModule
              }
              if (folder.moduleId) {
                report.moduleDisplayName = this.getModuleName(folder.moduleId)
              }
              if (this.compareString(report.displayName, search.view)) {
                report.disabled = false
              } else {
                report.disabled = true
              }
            })
          }
        })
        return viewsList.filter(rt => {
          if (rt.views) {
            let views = rt.views.filter(report => !report.disabled)
            if (views && views.length) {
              return views
            }
          }
          return null
        })
      }
      return []
    },
    customModuleList() {
      let { customModuleList } = this.data
      let { search } = this
      if (customModuleList) {
        customModuleList.forEach(folder => {
          if (folder.list) {
            folder.list.forEach(report => {
              report.folderName = folder.label
              report.typeDisplayName = 'View'
              report.optionName = 'view'
              if (folder.moduleId) {
                report.moduleDisplayName = this.getModuleName(folder.moduleId)
              }
              if (this.compareString(report.displayName, search.view)) {
                report.disabled = false
              } else {
                report.disabled = true
              }
            })
          }
        })
        return customModuleList.filter(rt => {
          if (rt.list) {
            let list = rt.list.filter(report => !report.disabled)
            if (list && list.length) {
              return list
            }
          }
          return null
        })
      }
      return []
    },
    allviews() {
      let { customModuleList, supportedModules } = this.data
      if (customModuleList && !supportedModules) {
        return customModuleList.map(rt => rt.list)
      } else if (!customModuleList && supportedModules) {
        return this.concatArray(supportedModules.map(rt => rt.list))
      } else if (customModuleList && supportedModules) {
        return [
          ...this.concatArray(supportedModules.map(rt => rt.list)),
          ...this.concatArray(customModuleList.map(rt => rt.list)),
        ]
      }
      return []
    },
    getfullGraphicsList() {
      let graphicsData = this.graphicsList.filter(
        rt => rt.assetCategoryId === this.selectedAssetCatergoryId
      )
      let data = {
        label: 'Graphics',
        expand: true,
        list:
          this.selectedAssetCatergoryId === -1
            ? this.graphicsList
            : graphicsData,
      }
      return [{ ...data }]
    },
    analyticsList() {
      let { analyticsReports } = this
      let { search } = this
      if (analyticsReports) {
        analyticsReports.forEach(folder => {
          if (folder.reports) {
            folder.reports.forEach(report => {
              report.folderName = folder.name
              if (folder.moduleId) {
                report.moduleDisplayName = this.getModuleName(folder.moduleId)
              }
              this.$set(report, 'selected', this.isSelectedData(report))
              if (this.compareString(report.name, search.analytics)) {
                report.disabled = false
              } else {
                report.disabled = true
              }
            })
          }
        })
        return analyticsReports.filter(rt => {
          if (rt.reports) {
            let reports = rt.reports.filter(report => !report.disabled)
            if (reports && reports.length) {
              return reports
            }
          }
          return null
        })
      }
      return []
    },
    pivotReportsList() {
      //pivot reports that match search string
      let { pivotReports } = this
      let { search } = this
      if (pivotReports) {
        pivotReports.forEach(folder => {
          if (folder.reports) {
            folder.reports.forEach(report => {
              report.folderName = folder.name
              if (folder.moduleId) {
                report.moduleDisplayName = this.getModuleName(folder.moduleId)
              }
              this.$set(report, 'selected', this.isSelectedData(report))
              if (this.compareString(report.name, search.pivotReports)) {
                report.disabled = false
              } else {
                report.disabled = true
              }
            })
          }
        })
        return pivotReports.filter(rt => {
          if (rt.reports) {
            let reports = rt.reports.filter(report => !report.disabled)
            if (reports && reports.length) {
              return reports
            }
          }
          return null
        })
      }
      return []
    },
    filteredAnalyticsReports() {
      let { analyticsList, selectAnalyticsType } = this
      if (selectAnalyticsType === 'all') {
        return analyticsList
      } else {
        return analyticsList.filter(folder => {
          let reports = folder.reports.filter(
            report => report.analyticsTypeEnum === selectAnalyticsType
          )
          if (reports && reports.length) {
            return reports
          }
          return null
        })
      }
    },
    graphicsList() {
      let { graphicsList } = this.data
      let { search } = this
      if (graphicsList) {
        graphicsList.forEach(graphics => {
          graphics.typeDisplayName = 'Graphics'
          graphics.optionName = 'graphics'

          if (this.compareString(graphics.name, search.analytics)) {
            graphics.disabled = false
          } else {
            graphics.disabled = true
          }
        })
        return graphicsList
      }
      return []
    },
    selectedGraphicsList() {
      return this.graphicsList
        ? this.graphicsList.filter(report => {
            if (report && report.selected) {
              return report
            }
            return null
          })
        : []
    },
  },
  methods: {
    async loadAnalyticsReport() {
      this.analyticsLoading = true
      let url = `/v3/report/folders?moduleName=energyData`
      await API.get(url)
        .then(({ data }) => {
          this.analyticsReports = data.reportFolders
          let i = 0
          this.analyticsReports.forEach(folder => {
            if (i === 0) {
              this.$set(folder, 'expand', true)
            } else {
              this.$set(folder, 'expand', false)
            }
            this.$set(folder, 'label', folder.displayName)
            folder.reports.forEach(rt => {
              this.$set(rt, 'selected', this.isSelectedData(rt))
              this.$set(rt, 'disabled', false)
              this.$set(rt, 'disabled', false)
              this.$set(rt, 'folderName', folder.displayName)
              this.$set(rt, 'typeDisplayName', 'Analytical Report')
              this.$set(rt, 'optionName', 'report')
              this.$set(rt, 'moduleDisplayName', 'Analytical Report')
            })
            i++
          })
          this.analyticsLoading = false
        })
        .catch(() => {
          this.analyticsLoading = false
        })
    },
    async loadPivotReports() {
      this.pivotReportsLoading = true
      let url = `/v3/report/folders?moduleName=energyData&isPivot=true`

      let { data, error } = await API.get(url)
      if (error) {
        // this.$message('error fetching pivot reports',error)
        console.error(error)
      } else {
        this.pivotReports = data.reportFolders
        let i = 0
        this.pivotReports.forEach(folder => {
          if (i === 0) {
            this.$set(folder, 'expand', true)
          } else {
            this.$set(folder, 'expand', false)
          }
          this.$set(folder, 'label', folder.displayName)
          folder.reports.forEach(rt => {
            this.$set(rt, 'selected', this.isSelectedData(rt))
            this.$set(rt, 'disabled', false)
            this.$set(rt, 'disabled', false)
            this.$set(rt, 'folderName', folder.displayName)
            this.$set(rt, 'typeDisplayName', 'Pivot Report')
            this.$set(rt, 'optionName', 'report')
            this.$set(rt, 'moduleDisplayName', 'Pivot Report')
          })
          i++
        })
      }
      this.pivotReportsLoading = false
    },
    getView(moduleName) {
      let { selectedModule } = this
      this.viewLoading = true
      this.viewsList = []
      let displayname = 'Work orders'
      if (moduleName) {
        displayname = this.modulesList.find(rt => rt.name === moduleName)
          .displayName
      }
      let url = `v2/views/viewList?moduleName=${selectedModule}`
      let { query } = this.$route
      let { appId } = query || {}
      if (!isEmpty(appId)) {
        url += `&appId=${appId}`
      }
      API.get(url).then(({ data }) => {
        if (data.groupViews) {
          let groups = data.groupViews
          groups.forEach(folder => {
            this.$set(folder, 'expand', false)
            this.$set(folder, 'label', folder.displayName)
            folder.views.forEach(rt => {
              this.$set(rt, 'selected', this.isSelectedData(rt))
              this.$set(rt, 'disabled', false)
              this.$set(rt, 'disabled', false)
              this.$set(rt, 'folderName', folder.displayName)
              this.$set(rt, 'typeDisplayName', 'View')
              this.$set(rt, 'optionName', 'view')
              this.$set(rt, 'moduleDisplayName', displayname)
            })
          })

          this.viewsList = groups
        }

        this.viewLoading = false
      })
    },
    isSelectedData(component) {
      let isCompoentd = this.selectedData.findIndex(rt => {
        if (rt.id === component.id) {
          return rt
        }
        return null
      })
      return isCompoentd > -1 ? true : false
    },
    getModuleName(moduleId) {
      let m = this.modulesList.find(rt => rt.id === moduleId)
      if (m && m.displayName) {
        return m.displayName
      }
      return ''
    },
    compareString(string1, string2) {
      if (string2 === '') {
        return true
      }
      if (string1 && string2) {
        if (string1.toLowerCase().indexOf(string2.toLowerCase()) >= 0) {
          return true
        }
      }
      return false
    },
    searchReport() {
      let { activeName, search } = this
      if (activeName === 'analytics' && search.report !== '') {
        this.selectedReportsWithFolder.forEach(rt => {
          rt.expand = true
        })
      } else if (activeName === 'report' && search.report === '') {
        this.selectedReportsWithFolder.forEach(rt => {
          rt.expand = false
        })
      }
      if (activeName === 'report' && search.report !== '') {
        this.selectedReportsWithFolder.forEach(rt => {
          rt.expand = true
        })
      } else if (activeName === 'report' && search.report === '') {
        this.selectedReportsWithFolder.forEach(rt => {
          rt.expand = false
        })
      }
      if (activeName === 'view' && search.view !== '') {
        this.selectediVewsWithFolder.forEach(rt => {
          rt.expand = true
        })
      } else if (activeName === 'view' && search.view === '') {
        this.selectediVewsWithFolder.forEach(rt => {
          rt.expand = false
        })
      }
      if (activeName === 'pivot' && search.pivotReports !== '') {
        this.selectedReportsWithFolder.forEach(rt => {
          rt.expand = true
        })
      } else if (activeName === 'pivot' && search.pivotReports === '') {
        this.selectedReportsWithFolder.forEach(rt => {
          rt.expand = false
        })
      }
    },
    addToDashboard() {
      this.selectedReports.forEach(rt => {
        this.$set(rt, 'optionName', 'report')
      })
      this.selectedviews.forEach(rt => {
        this.$set(rt, 'optionName', 'view')
      })
      this.selectedGraphicsList.forEach(rt => {
        this.$set(rt, 'optionName', 'graphics')
      })
      // let data = [
      //   ...this.selectedReports,
      //   ...this.selectedviews,
      //   ...this.selectedGraphicsList,
      // ]

      this.$emit('addElement', this.selectedData)
      this.handleClose()
    },
    setOption(name) {
      this.activeName = name
      this.$emit('option', name)
    },
    close() {
      this.$emit('close')
    },
    handleClose() {
      this.close()
      this.$emit('resetreportData')
    },
    expand(folder) {
      this.$set(folder, 'expand', !folder.expand)
    },
    concatArray(array) {
      return [].concat.apply([], array)
    },
    setModuleName() {
      if (this.activeName === 'view') {
        this.getView(this.selectedModule)
      }
      this.$emit('setModuleName', this.selectedModule, this.selectedData)
    },
    deleteselectedData(index) {
      this.selectedData.splice(index, 1)
    },
    getselectedData(data) {
      // let selectedReports = this.concatArray(this.allreports).filter(report => {
      //   if (report && report.selected) {
      //     return report
      //   }
      // })
      // let selectedviews = this.concatArray(this.allviews).filter(report => {
      //   if (report && report.selected) {
      //     return report
      //   }
      // })
      // let graphicsList = this.graphicsList.filter(report => {
      //   if (report && report.selected) {
      //     return report
      //   }
      // })
      // this.selectedData = [
      //   ...selectedReports,
      //   ...selectedviews,
      //   ...graphicsList,
      // ]
      if (data.selected) {
        this.selectedData.push(data)
      } else {
        let findData = this.selectedData.findIndex(rt => rt.id === data.id)
        this.deleteselectedData(findData)
      }
    },
    getViewData() {
      this.selectedviews = this.concatArray(this.allviews).filter(report => {
        if (report && report.selected) {
          return report
        }
        return null
      })
    },
    handleClick() {},
  },
}
</script>
<style lang="scss">
.db-user-filter-manager-dialog {
  .el-dialog__header {
    border-bottom: 1px solid #eff1f4;
  }
  .el-dialog__body {
    padding: 0px;
  }
  .body {
    height: 60vh;
  }
  .filter-config-container {
    height: 100%;
    display: flex;
  }

  .filter-config-left {
    width: 40%;
    height: 100%;
    overflow-y: scroll;
    border-right: 1px solid #eff1f4;
  }
  .filter-config-right {
    width: 60%;
    height: 100%;
    overflow-y: scroll;
    padding: 15px 25px;
  }

  .accordian-item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    background-color: #f8fcff;
    font-size: 14px;

    font-weight: 500;
    padding: 15px 25px;
    letter-spacing: 0.5px;
    color: #324056;
    border-bottom: 1px solid #eff1f4;
    border-top: none;
  }
  .accordian-item-tab {
    display: flex;
    justify-content: space-between;
    align-items: center;

    font-size: 14px;

    font-weight: 500;
    padding: 15px 25px;
    letter-spacing: 0.5px;
    color: #324056;
    border-bottom: 1px solid #eff1f4;
    // border-top: 1px solid #eff1f4;
  }
  .selcted-filters-title {
    font-size: 11px;
    font-weight: bold;
    letter-spacing: 1px;
    color: #324056;
  }
  .module-fields-container {
    padding: 10px;
    margin-left: 10px;
    // max-height: 300px;
    // overflow-y: scroll;
  }
  .module-field {
    font-size: 14px;
    padding: 10px;
    letter-spacing: 0.5px;
    color: #324056;
  }
  .accordian-arrow {
    border: solid #809bae;
    border-width: 1px 1px 0px 0px;
    display: inline-block;
    padding: 3px;
    transform: rotate(135deg);

    &.expanded {
      transform: rotate(-45deg);
    }
  }
  .field-row {
    padding: 10px 10px;
    border: 1px solid #f4f5f7;
    box-shadow: 0px 3px 5px #f4f5f7;
    display: flex;
    font-size: 13px;
  }

  .field-row:hover {
    background-color: #f1f8fa;
  }

  .modal-btn-save {
    margin-left: 0px !important;
  }
  .applies-to-container {
    border: 1px solid #f4f5f7;
    box-shadow: 0px 3px 5px #f4f5f7;
    padding: 15px 15px 10px 30px;
  }
  .applies-to-field {
    font-size: 14px;
    letter-spacing: 0.5px;
    padding-bottom: 7px;
  }
  .applies-to-field-in {
    font-weight: 500;
    font-style: italic;
    margin-left: 5px;
    margin-right: 5px;
  }
  .applies-to-field-module-name {
    text-transform: uppercase;
    letter-spacing: 0.2px;
  }
  .applies-to-title {
    letter-spacing: 0.5px;
    font-weight: 500;
    font-size: 14px;
    color: #324056;
    padding-bottom: 7px;
    padding-top: 5px;
  }
}
.accordian-item-tab.active {
  background-color: #f1f8fa;
}
.cp-border-left {
  border-left: 1px solid #eff1f4;
}
.cp-border-right {
  border-right: 1px solid #eff1f4;
}
.cp-border-top {
  border-top: 1px solid #eff1f4;
}
.cp-border-bottom {
  border-bottom: 1px solid #eff1f4;
}
.emptyIcon .icon-xlg {
  width: 70px;
  // margin-left: 120%;
}
</style>
