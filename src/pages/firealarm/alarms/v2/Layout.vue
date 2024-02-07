+
<template>
  <div class="layout container fc-alarm-list-page fc-alarm-page">
    <subheader
      v-if="
        !$route.path.includes('summary') &&
          !$route.path.includes('overview') &&
          !$route.path.includes('newsummary')
      "
      v-show="subheaderNeeded == true"
      :menu="subheaderMenu"
      newbtn="true"
      type="workorder"
      parent="/app/fa/faults"
      :listCount="listCount"
      showRearrange="true"
      :filtersRetain="filtersRetains"
      @rearrange="showViewSettings = true"
      :showCurrentViewOnly="showQuickSearch"
      :maxVisibleMenu="3"
    >
      <template slot="prefix">
        <div class="fL fc-subheader-left">
          <!-- DropDown -->
          <div class="pR15 pointer">
            <img
              src="~assets/three-line-menu.svg"
              width="14"
              height="14"
              class="wo-three-line"
              @click.stop="showListWorkorder = true"
            />
            <el-dropdown
              class="fc-dropdown-menu pL10"
              @command="openChild"
              v-if="selectedList !== 'System Views'"
              trigger="click"
            >
              <span class="el-dropdown-link">
                {{ selectedList }}
                <i
                  class="el-icon-arrow-down el-icon--right"
                  v-if="!showQuickSearch"
                ></i>
              </span>
              <el-dropdown-menu
                slot="dropdown"
                v-if="views && !showQuickSearch"
              >
                <el-dropdown-item
                  v-for="(view, key) in views"
                  :key="key"
                  v-if="view.name !== 'sensorAlarmViews'"
                  :command="view.displayName"
                  >{{ view.displayName }}</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div
            v-if="views && selectedList !== 'System Views'"
            class="fc-separator-lg mL10 mR10"
          ></div>
          <!-- Dialog for Edit View -->
        </div>
      </template>
      <div class="fR fc-subheader-right">
        <!-- View When SearchBox Is Closed -->
        <template v-if="!showQuickSearch">
          <div class="pL15 fc-black-small-txt-12">
            <pagination :total="listCount" :perPage="50"></pagination>
            <div class="block"></div>
          </div>
          <span class="separator" v-if="listCount > 0">|</span>
          <el-tooltip
            effect="dark"
            :content="$t('common._common.sort')"
            placement="left"
          >
            <div
              class="pointer user-select-none pR0 sort-icon"
              @click.stop="$refs.sortPopover.open()"
            >
              <img
                src="~assets/new-sortby.svg"
                style="
                  width: 19px;
                  height: 19px;
                  margin-left: 5px;
                  margin-right: 5px;
                  margin-top: 7px;
                "
                class="sort-icon-hover"
              />

              <page-sort
                ref="sortPopover"
                :sortList="sortConfigLists"
                :config="sortConfig"
                :excludeFields="excludedSortFields"
                @onchange="updateSort"
                class="alarm-page-sort"
              ></page-sort>
            </div>
          </el-tooltip>
          <span class="separator">|</span>
          <el-tooltip
            effect="dark"
            :content="$t('common._common.export')"
            placement="left"
          >
            <f-export-settings
              module="newreadingalarm"
              :viewDetail="viewDetail"
              :showViewScheduler="true"
              :showMail="showMail"
            ></f-export-settings>
          </el-tooltip>
          <span class="separator">|</span>
          <el-tooltip
            effect="dark"
            :content="$t('common._common.search')"
            placement="left"
          >
            <div>
              <i
                class="el-icon-search fc-black-2 f16 pointer mR10 mL10 fw-bold"
                @click.stop="toggleQuickSearch()"
              ></i>
            </div>
          </el-tooltip>
        </template>
        <!-- View When SearchBox Is Acessed -->

        <div
          class="fc-black-small-txt-12 fc-subheader-right-search"
          v-show="showQuickSearch"
        >
          <new-search
            :config="filterConfig"
            :moduleName="filterConfig.moduleName"
            :loadViews="true"
            @loadView="loadViews()"
            @hideSearch="showQuickSearch = false"
            :quickSearchQuery="quickSearchQuery"
            :save="save"
            :saveAs="saveAs"
            :resetFilters="resetFilters"
            :sortConfig="sortConfig"
            :showSearch="showQuickSearch"
            :defaultFilter="defaultFilter"
          ></new-search>
          <div class="filter-search-close"></div>
        </div>
      </div>
    </subheader>
    <div class="height100 row">
      <div v-if="appliedFilters !== null && showTag" class="width100">
        <div class="fL" style="width: 84%">
          <new-tag
            :config="filterConfig"
            :filters="appliedFilters"
            :showFilterAdd="showAddFilter"
            :showCloseIcon="true"
            class="layout-new-tag"
          ></new-tag>
        </div>
        <div class="save-btn-section">
          <div v-if="appliedFilters">
            <div v-if="!viewDetail.isDefault">
              <el-dropdown @command="savingView">
                <el-button type="primary" class="subheader-saveas-btn">
                  {{ $t('common._common.save_filters')
                  }}<i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="Save">{{
                    $t('common._common._save')
                  }}</el-dropdown-item>
                  <el-dropdown-item command="Save As">{{
                    $t('common._common.save_as')
                  }}</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <div
                class="clear-filter"
                v-if="Object.keys(appliedFilters).length > '0'"
                @click="resetFilters = !resetFilters"
              >
                {{ $t('common._common.clear_all_filters') }}
              </div>
            </div>
            <div v-else>
              <el-button
                class="subheader-saveas-btn saveas"
                v-if="Object.keys(appliedFilters).length > '0'"
                @click="saveAs = !saveAs"
                >{{ $t('common._common.save_filters') }}</el-button
              >
              <div
                class="clear-filter"
                v-if="Object.keys(appliedFilters).length > '0'"
                @click="resetFilters = !resetFilters"
              >
                {{ $t('common._common.clear_all_filters') }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div
        class="height100 f-list-view"
        v-bind:class="{ 'filter-active': showFilter }"
      >
        <router-view
          @syncCount="callbackMethod"
          @showTag="showTagInList"
        ></router-view>
      </div>
    </div>
    <div v-show="showListWorkorder">
      <views-list
        :config="filterConfig"
        :showViewSchedulerListTab="true"
        :newGroup="true"
        :preparingViews.sync="views"
        @delete="deleteView"
        :views.sync="views"
        :showEditIcon="showEditIcon"
        :showListWorkorder.sync="showListWorkorder"
        :moduleName="viewModuleName"
        :mainViewsList="mainViewsList"
      ></views-list>
    </div>
    <view-customization
      :visible.sync="showViewSettings"
      :reload="true"
      @onchange="callMethod()"
      :menu="subheaderMenu"
      :moduleName="viewModuleName"
    ></view-customization>
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none"
    ></iframe>
  </div>
</template>
<script>
import Subheader from '@/Subheader'
import ViewCustomization from '@/ViewCustomization'
import { mapState, mapActions } from 'vuex'
import ViewsList from '@/ViewsList'
import NewSearch from '@/NewSearch'
import Pagination from '@/list/FPagination'
import FExportSettings from '@/FExportSettings'
import NewTag from '@/NewTag'
import PageSort from '@/PageSort'
export default {
  data() {
    return {
      showListWorkorder: false,
      showEditIcon: true,
      defaultFilter: 'subject',
      showMail: false,
      filtersRetains: ['search', 'includeParentFilter'],
      listCount: '',
      excludedSortFields: ['clearedBy'],
      save: false,
      saveAs: false,
      resetFilters: false,
      selectedList: 'SystemViews',
      showTag: true,
      showQuickSearch: false,
      showAddFilter: true,
      sortConfigLists: [],
      moduleMetaObject: {},
      quickSearchQuery: null,
      mainViewsList: {
        SystemViews: 'System Views',
        CustomViews: 'Custom Views',
      },
      sortConfig: {
        orderBy: {
          label: this.$t('alarm.alarm.last_Occurred_Time'),
          value: 'lastOccurredTime',
        },
        orderType: 'desc',
      },
      sortConfigList: [
        'createdTime',
        'severity',
        'resource',
        'acknowledgedBy',
        'readingalarmcategory',
        'lastCreatedTime',
        'subject',
        'noOfOccurrences',
        'lastOccurredTime',
      ],
      filterSelected: false,
      showViewSettings: false,
      exportDownloadUrl: null,
      filterConfig: {
        moduleName: 'newreadingalarm',
        includeParentCriteria: true,
        path: '/app/fa/faults/',
        data: {
          subject: {
            label: 'Message',
            displayType: 'string',
            value: [],
          },
          severity: {
            label: this.$t('alarm.alarm.severity'),
            displayType: 'select',
            options: {},
            value: [],
          },
          // rule: {
          //   label: 'Rule',
          //   displayType: 'select',
          //   options: {},
          //   value: [],
          // },
          // previousSeverity: {
          //   label: this.$t('alarm.alarm.previous_severity'),
          //   displayType: 'select',
          //   options: {},
          //   value: [],
          // },
          readingAlarmCategory: {
            label: this.$t('asset.assets.asset_category'),
            displayType: 'select',
            options: {},
            value: [],
          },
          faultType: {
            label: 'Fault Type',
            displayType: 'select',
            options: {},
            value: [],
          },
          asset: {
            label: this.$t('alarm.alarm.asset'),
            displayType: 'asset',
            options: {},
            key: 'resource',
            value: [],
          },
          acknowledged: {
            label: this.$t('alarm.alarm.is_acknowledged'),
            displayType: 'select',
            options: { true: 'Yes', false: 'No' },
            operatorId: 15,
            value: '',
          },
          acknowledgedBy: {
            label: this.$t('alarm.alarm.acknowledge_by'),
            displayType: 'select',
            options: {},
            value: [],
          },
          acknowledgedTime: {
            label: this.$t('alarm.alarm.acknowledged_time'),
            displayType: 'select',
            single: true,
            type: 'date',
            customdate: true,
            options: {
              '1_40': 'Within 1 Hour',
              '12_40': 'Within 12 Hours',
              '24_40': 'Within 24 Hours',
              22: 'Today',
              25: 'Yesterday',
              31: 'This Week',
              28: 'This Month',
              27: 'Last Month',
              '2_39': 'Last 2 Months',
              '6_39': 'Last 6 Months',
            },
            value: '',
          },
          clearedTime: {
            label: this.$t('alarm.alarm.cleared_time'),
            displayType: 'select',
            single: true,
            type: 'date',
            customdate: true,
            options: {
              '1_40': 'Within 1 Hour',
              '12_40': 'Within 12 Hours',
              '24_40': 'Within 24 Hours',
              22: 'Today',
              25: 'Yesterday',
              31: 'This Week',
              28: 'This Month',
              27: 'Last Month',
              '2_39': 'Last 2 Months',
              '6_39': 'Last 6 Months',
            },
            value: '',
          },
          lastOccurredTime: {
            label: 'Last Reported On',
            displayType: 'select',
            single: true,
            type: 'date',
            customdate: true,
            options: {
              22: 'Today',
              25: 'Yesterday',
              31: 'This Week',
              28: 'This Month',
              27: 'Last Month',
              '2_39': 'Last 2 Months',
              '6_39': 'Last 6 Months',
            },
            value: '',
          },
          lastCreatedTime: {
            label: 'Last Occurred Time',
            displayType: 'select',
            single: true,
            type: 'date',
            customdate: true,
            options: {
              22: 'Today',
              25: 'Yesterday',
              31: 'This Week',
              28: 'This Month',
              27: 'Last Month',
              '2_39': 'Last 2 Months',
              '6_39': 'Last 6 Months',
            },
            value: '',
          },
        },
        availableColumns: [
          'createdTime',
          'severity',
          'resource',
          'acknowledgedBy',
          'readingalarmcategory',
          'lastCreatedTime',
          'subject',
          'noOfOccurrences',
          'lastOccurredTime',
          'clearedTime',
          'faultType',
        ],
        fixedCols: ['subject'],
        saveView: true,
      },
    }
  },
  components: {
    Subheader,
    ViewCustomization,
    ViewsList,
    NewSearch,
    NewTag,
    FExportSettings,
    Pagination,
    PageSort,
  },
  created() {
    // console.log('loadViews')
    this.$store.dispatch('view/clearViews').then(() => {})
  },
  mounted() {
    this.loadViews()
  },
  computed: {
    ...mapActions({
      loadViewDetail: 'view/loadViewDetail',
      savesorting: 'view/savesorting',
    }),
    // views() {
    //   return this.$store.state.view.groupViews
    // },
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
      moduleMeta: state => state.view.metaInfo,
      currentViewDetail: state => state.view.currentViewDetail,
    }),
    views: {
      get: function() {
        return this.$store.state.view.groupViews
      },
      set: function(newValue) {},
    },
    showFilter() {
      return this.filterSelected && this.$route.path.indexOf('/summary/') === -1
    },
    filters() {
      return this.$route.query.search
        ? JSON.parse(this.$route.query.search)
        : null
    },
    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    appliedFilters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    includeParentFilter() {
      return (
        this.$route.query.includeParentFilter &&
        (this.$route.query.includeParentFilter === 'true' ||
          this.$route.query.includeParentFilter === true)
      )
    },
    subheaderNeeded() {
      if (
        this.$route.path.includes('summary') ||
        this.$route.path.includes('overview') ||
        this.$route.path.includes('newsummary')
      ) {
        return false
      } else {
        return true
      }
    },
    subheaderMenu() {
      return this.getSubHeaderMenu()
    },
    viewModuleName() {
      let selectedView
      if (this.currentView) {
        for (let i = 0; i < this.views.length; i++) {
          let lists = this.views[i].views
          let key1 = lists.find(rt => rt.name === this.currentView)
          if (key1) {
            selectedView = key1
          }
        }
      }
      return selectedView ? selectedView.moduleName : 'newreadingalarm'
    },
  },
  watch: {
    currentView(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.getViewDetail()
      }
    },
    viewDetail(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        if (this.viewDetail.sortFields) {
          this.sortConfig = {
            orderType: this.viewDetail.sortFields[0].isAscending
              ? 'asc'
              : 'desc',
            orderBy: this.viewDetail.sortFields[0].sortField.name,
          }
        }
      }
    },
  },
  methods: {
    callMethod() {
      console.log('callMethod')
      this.loadViews()
    },
    updateSort(sorting) {
      this.$store.dispatch('view/savesorting', {
        viewName: this.currentView,
        orderBy: sorting.orderBy,
        orderType: sorting.orderType,
        moduleName: this.viewModuleName,
      })
    },
    loadViews() {
      let param = {
        moduleName: this.viewModuleName,
      }

      this.$store.dispatch('view/loadGroupViews', param).then(() => {
        this.setDefaultView()
        this.$store.dispatch('view/loadModuleMeta', this.viewModuleName)
        this.getViewDetail()
        this.metaFieldsLists()
      })
    },
    setDefaultView() {
      if (this.$route.path.endsWith('/fa/faults')) {
        if (this.views.length && this.views[0].views.length) {
          this.$router.push({
            path: '/app/fa/faults/' + this.views[0].views[0].name,
            query: this.$route.query,
          })
        }
      }
    },
    metaFieldsLists() {
      let self = this
      self.$http
        .get('/module/metafields?moduleName=' + this.viewModuleName)
        .then(response => {
          self.moduleMetaObject = response.data.meta
          if (self.moduleMetaObject && self.moduleMetaObject.fields) {
            for (let i = 0; i < self.moduleMetaObject.fields.length; i++) {
              self.sortConfigLists.push(self.moduleMetaObject.fields[i].name)
            }
          }
        })
    },
    openChild(command) {
      let self = this
      let a2
      self.selectedList = null
      self.selectedList = command
      let key1 = self.views.find(rt => rt.displayName === command)
      if (key1.displayName === command) {
        a2 = key1.views
      }
      if (a2) {
        self.$router.push({
          path: '/app/fa/faults/' + a2[0].name,
        })
      }
    },
    savingView(command) {
      if (command === 'Save') {
        this.save = !this.save
      } else if (command === 'Save As') {
        this.saveAs = !this.saveAs
      }
    },
    deleteView(a, index) {
      console.log('key12', a, index)
      let self = this
      let key2 = []
      this.$dialog
        .confirm({
          title: 'Delete View',
          message: 'Are you sure you want to delete this View?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            self.$http
              .post('/view/delete', {
                id: a.id,
              })
              .then(response => {
                if (response.data === null) {
                  for (let i = 0; i < self.views.length; i++) {
                    let lists = self.views[i].views
                    let key1 = lists.find(rt => rt.name === a.name)
                    if (key1) {
                      key2 = lists.find(rt => rt.name !== a.name)
                      console.log('key12', key2)
                      if (key2) {
                        self.$router.push('/app/fa/faults/' + key2.name)
                        self.loadViews()
                        self.$message.success('View deleted successfully')
                      } else {
                        let a1 = 'systemviews'
                        let key3 = self.views.find(rt => rt.name === a1)
                        let a2 = key3.views
                        if (key3) {
                          self.$router.push('/app/fa/faults/' + a2[0].name)
                          self.loadViews()
                          // this.views[i].views.splice(index, 1)
                          self.$message.success('View deleted successfully')
                        }
                      }
                    }
                  }
                } else {
                  this.$message.error('View cannot be deleted')
                }
              })
          }
        })
    },
    toggleQuickSearch() {
      this.showQuickSearch = !this.showQuickSearch
    },
    callbackMethod(newVal) {
      this.listCount = newVal
    },
    showTagInList(newVal) {
      this.showTag = newVal
    },
    getViewDetail() {
      let moduleNames = this.viewModuleName
      // this.viewDetail && this.viewDetail.hasOwnProperty('moduleName')
      //   ? this.viewDetail.moduleName
      //   : 'newreadingalarm'
      console.log('moduleName' + moduleNames)
      this.$store.dispatch('view/loadViewDetail', {
        viewName: this.currentView,
        moduleName: moduleNames,
      })
    },
    getSubHeaderMenu() {
      let a1 = []
      let self = this
      if (self.currentView) {
        for (let i = 0; i < self.views.length; i++) {
          let lists = self.views[i].views
          let key1 = lists.find(rt => rt.name === self.currentView)
          if (key1) {
            self.selectedList = self.views[i].displayName
            a1 = self.views[i].views
          }
        }
      }
      if (!self.currentView && self.views.length) {
        let a = 'systemviews'
        let key1 = self.views.find(rt => rt.name === a)
        if (key1) {
          self.selectedList = key1.displayName
          a1 = key1.views
        }
      }
      if (!self.selectedList) {
        return []
      }
      if (a1.length > 0) {
        return a1.map(view => ({
          label: view.displayName,
          path: {
            path: '/app/fa/faults/' + view.name,
          },
          permission: 'workorder:READ,READ_TEAM,READ_OWN',
          id: view.id,
          name: view.name,
          isCustom: !view.isDefault,
        }))
      }
      return []
    },
    exportList(type) {
      let self = this
      this.$refs.export.close()
      let url =
        '/exportModule?type=' +
        type +
        '&moduleName=alarm&viewName=' +
        this.currentView
      self.$message({
        showClose: true,
        message: 'Downloading...',
        type: 'success',
      })
      this.$http.get(url).then(function(response) {
        self.exportDownloadUrl = response.data.fileUrl
      })
    },
  },
}
</script>
<style>
.right-actions {
  white-space: nowrap;
  margin-top: -20px;
  margin-right: 20px;
}
.asset-layout-icon .wl-icon-downarrow {
  position: relative;
  left: 7px;
  bottom: 2px;
  font-size: 13px;
}
.wl-icon-downarrow {
  position: relative;
  left: 7px;
  bottom: 2px;
  font-size: 13px;
}
.fc-subheader-container {
  width: 100%;
  height: 60px;
  background: #ffffff;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-box-sizing: border-box;
  padding: 20px 30px;
}

.fc-subheader-left {
  display: flex;
  flex-direction: row;
  position: relative;
}

.fc-subheader-right {
  display: flex;
  flex-direction: row;
  height: 0;
  margin-right: 12px;
}

.fc-subheader-right-search {
  position: relative;
  bottom: 11px;
  right: -2px;
}
.create-btn {
  margin-top: -10px;
}

.subheader-tab {
  background: #fff;
  height: 2px;
}

.subheader-tab.active {
  background: #ef508f;
  margin-top: 8px;
  width: 20px;
}

.wo-three-line {
  position: relative;
  top: 2px;
}
.individual-view {
  position: relative;
  text-transform: capitalize;
  padding-left: 10px;
  padding-right: 10px;
  font-size: 14px;
  color: #333333;
  height: 40px;
}

.individual-view:hover {
  background-color: #f1f8fa;
  border-radius: 3px;
}

.individual-view .el-icon-edit {
  position: absolute;
  right: 8px;
  top: 10px;
}

.individual-view .customEdit {
  position: absolute;
  right: 30px;
  top: 10px;
}

.wo-list-block {
  margin-bottom: 20px;
}

.view-dialog-body .el-dialog__body {
  padding-top: 10px;
}

.view-dialog-body .el-dialog__title {
  padding-left: 10px;
}

.border-right-remove:last-child {
  border-right: none;
}

.fc-dropdown-menu {
  font-weight: 500;
  color: #2d2d52;
}

.comment-close {
  position: relative;
  top: 0;
  right: 10px;
  opacity: 0.3;
  color: #000000;
}

.el-icon-edit.pointer.edit-icon.visibility-hide-actions.fR.editview {
  position: absolute;
  right: 38px;
  top: 10px;
}

.save-btn-separator {
  font-size: 0;
  color: TRANSPARENT;
  padding-right: 0;
  padding-left: 0;
  border-left: 1px SOLID #efefef;
  margin-left: 10px;
  margin-right: 10px;
}

.subheader-saveas-btn:hover {
  background-color: rgba(125, 103, 184, 0.1);
  color: #372668;
}
.sort-icon {
  margin-top: -7px;
}

.pagination {
  padding-top: 13px;
}

.delete-icon {
  position: absolute;
  right: 6px;
  top: 10px;
}

.sh-selection-bar {
  border: 1px solid #ee518f;
  width: 25px;
  margin-top: 5px;
  position: absolute;
}

.filter-search-close {
  font-size: 18px;
  position: absolute;
  right: 85px;
  top: 10px;
  color: #8ca1ad;
}

.sort-icon-hover:hover {
  fill: #e7328a;
  transition: fill 0.5s ease;
}
.save-btn-section {
  width: 17%;
  position: absolute;
  top: 29px;
  right: 22px;
  text-align: right;
}
.subheader-saveas-btn {
  background: #39b2c2;
  border-radius: 3px;
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 0.3px;
  color: #ffffff;
  padding: 5px;
}
.subheader-saveas-btn:hover {
  background: #33a6b5;
  color: #fff;
}

.clear-filter {
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #748893;
  float: right;
  margin-left: 10px;
  margin-top: 5px;
  cursor: pointer;
}
.dp-saveas-btn {
  background-color: #39b2c2;
  border-color: #39b2c2;
  padding: 6px 4px;
  font-size: 10px;
  font-weight: 500;
  border-radius: 4px;
  letter-spacing: 0.3px;
  color: #ffffff;
}
.dp-saveas-btn:hover {
  background: #33a6b5;
  color: #fff;
}
.pagination {
  padding-top: 13px;
}
.create-btn {
  margin-top: -10px;
}
.fc-alarm-page .page-sort-popover {
  right: 40px !important;
}
</style>
