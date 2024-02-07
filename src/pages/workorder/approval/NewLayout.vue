<template>
  <div class="layout container">
    <subheader
      v-if="!$route.path.includes('summary')"
      :menu="subheaderMenu"
      newbtn="true"
      type="workorder"
      :parent="`${this.parentPath}/approvals`"
      :listCount="listCount"
      :showRearrange="false"
      :showCurrentViewOnly="showQuickSearch"
      :filtersRetain="filtersRetains"
      :maxVisibleMenu="3"
      class="inventory_subheader"
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
              v-if="moduleVsViews.preparingViews"
              trigger="click"
            >
              <span class="el-dropdown-link">
                {{ moduleVsViews.mainViewsList[selectedList] }}
                <i
                  class="el-icon-arrow-down el-icon--right"
                  v-if="!showQuickSearch"
                ></i>
              </span>
              <el-dropdown-menu
                slot="dropdown"
                v-if="moduleVsViews.preparingViews && !showQuickSearch"
              >
                <el-dropdown-item
                  v-for="(view, key) in moduleVsViews.preparingViews"
                  :key="key"
                  :command="key"
                  >{{ moduleVsViews.mainViewsList[key] }}</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div
            v-if="moduleVsViews.preparingViews"
            class="fc-separator-lg mL10 mR10"
          ></div>
          <!-- Dialog for Edit View -->
        </div>
      </template>
      <div class="fR fc-subheader-right fc-subheader-right-inventory">
        <template v-if="!showQuickSearch">
          <div class="pL15 fc-black-small-txt-12">
            <pagination :total="listCount" :perPage="50"></pagination>
            <div class="block"></div>
          </div>
          <span class="separator" v-if="listCount > 0">|</span>
          <div
            class="pointer user-select-none pR0 sort-icon"
            @click.stop="$refs.sortPopover.open()"
          >
            <img
              src="~assets/new-sortby.svg"
              style="width: 19px;height: 19px;"
              class="sort-icon-hover mR10 mL10 mT10"
            />
            <page-sort
              ref="sortPopover"
              v-if="sortConfigList"
              :sortList="sortConfigList"
              :config="sortConfig"
              :excludeFields="excludedSortFields"
              @onchange="updateSort"
            ></page-sort>
          </div>
          <span class="separator">|</span>
          <div class="pR10">
            <i
              class="el-icon-search fc-black-2 f16 pointer mR10 mL10 fw-bold"
              @click.stop="toggleQuickSearch()"
            ></i>
          </div>
          <div class="pL10" v-show="!showQuickSearch">
            <router-link
              to="/app/wo/approvals/ir/new/ir?create=new"
              append
              v-if="
                $hasPermission('inventory:CREATE') &&
                  getCurrentModule === 'inventoryrequest'
              "
            >
              <el-button
                class="fc-create-btn create-btn"
                :title="'New Inventory Request'"
                v-tippy
                data-size="small"
              >
                <i class="el-icon-plus white-color f12 fw-bold"></i>
              </el-button>
            </router-link>
          </div>
        </template>
        <!-- View When SearchBox Is Acessed -->
        <div
          class="fc-black-small-txt-12 fc-subheader-right-search-inventory"
          v-show="showQuickSearch"
        >
          <new-search
            v-if="filterConfig"
            :loadViews="true"
            @loadView="loadGroupViews()"
            :moduleName="filterConfig.moduleName"
            :config="filterConfig"
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
    <div class="height100 row width100">
      <div v-if="appliedFilters !== null" class="width100">
        <div class="fL" style="width: 84%;">
          <new-tag
            v-if="filterConfig"
            :showCloseIcon="true"
            :config="filterConfig"
            :showFilterAdd="showAddFilter"
            :filters="appliedFilters"
            class="layout-new-tag"
          ></new-tag>
        </div>
        <div class="save-btn-section">
          <div v-if="appliedFilters">
            <div v-if="!viewDetail.isDefault">
              <el-dropdown @command="savingView">
                <el-button type="primary" class="subheader-saveas-btn">
                  {{ $t('common._common.save_filters') }}
                  <i class="el-icon-arrow-down el-icon--right"></i>
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
      <div class="height100 f-list-view">
        <router-view @syncCount="callbackMethod"></router-view>
      </div>
    </div>
    <div v-show="showListWorkorder">
      <views-list
        v-if="filterConfig"
        :config="filterConfig"
        @delete="deleteView"
        :preparingViews="moduleVsViews.preparingViews"
        :views.sync="views"
        :showListWorkorder.sync="showListWorkorder"
        :mainViewsList="moduleVsViews.mainViewsList"
        :parentPathList="moduleVsViews.parentPathList"
        :showEditIcon="showEditIcon"
      ></views-list>
    </div>
    <view-customization
      :visible.sync="showViewSettings"
      :reload="true"
      @onchange="loadGroupViews()"
      :menu="subheaderMenu"
      :moduleName="getCurrentModule"
    ></view-customization>
  </div>
</template>
<script>
import Subheader from '@/Subheader'
import ViewCustomization from '@/ViewCustomization'
import { mapState, mapActions } from 'vuex'
import ViewsList from '@/ViewsList'
import NewSearch from '@/NewSearch'
import Pagination from '@/list/FPagination'
import NewTag from '@/NewTag'
import PageSort from '@/PageSort'
export default {
  data() {
    return {
      parentPath: '/app/wo',
      showListWorkorder: false,
      showEditIcon: false,
      showAddFilter: true,
      defaultFilter: 'subject',
      listCount: '',
      save: false,
      saveAs: false,
      moduleMetaObject: {},
      filtersRetains: ['search', 'includeParentFilter'],
      resetFilters: false,
      selectedList: null,
      showQuickSearch: false,
      quickSearchQuery: null,
      excludedSortFields: null,
      sortConfig: {
        orderBy: {
          label: this.$t('maintenance.wr_list.datecreated'),
          value: 'createdTime',
        },
        orderType: 'desc',
      },
      sortConfigList: null,
      filterSelected: false,
      showViewSettings: false,
      moduleVsViews: {
        mainViewsList: {},
        preparingViews: {},
        parentPathList: {},
      },
      filterConfig: null,
    }
  },
  components: {
    Subheader,
    ViewCustomization,
    ViewsList,
    NewSearch,
    NewTag,
    Pagination,
    PageSort,
  },
  created() {
    this.$store.dispatch('view/clearViews')
  },
  mounted() {
    if (this.$helpers.isEtisalat()) {
      this.parentPath = '/app/home'
    }
    this.loadAllModuleViews()
    this.loadGroupViews()
    if (this.getCurrentModule === 'approval') {
      this.$store.dispatch('view/loadModuleMeta', 'workorder')
    } else {
      this.$store.dispatch('view/loadModuleMeta', this.getCurrentModule)
    }
    this.getViewDetail()
    if (!this.filterSelected && this.filters) {
      this.toggleViewFilter()
    }
    this.loadSortConfigListForModule()
    this.setFilterConfig()
  },
  computed: {
    ...mapActions({
      loadViewDetail: 'view/loadViewDetail',
      savesorting: 'view/savesorting',
    }),
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
    }),
    views() {
      return this.$store.state.view.groupViews
    },
    subheaderMenu() {
      return this.getSubHeaderMenu()
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
    getCurrentModule() {
      this.selectedList = this.$route.meta.module || 'approval'
      return this.$route.meta.module || 'approval'
    },
    isWorkPermitLicenseEnabled() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers || {}

      return isLicenseEnabled('WORK_PERMIT')
    },
  },
  watch: {
    filters: function(val) {
      if (val != null && !this.filterSelected) {
        this.toggleViewFilter()
      }
    },
    currentView(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.getViewDetail()
      }
    },
    viewDetail(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        if (this.viewDetail.sortFields) {
          let orderBy = this.sortConfigList.find(name => {
            return name === this.viewDetail.sortFields[0].sortField.name
          })
          this.sortConfig = {
            orderType: this.viewDetail.sortFields[0].isAscending
              ? 'asc'
              : 'desc',
            orderBy,
          }
        }
      }
    },
    getCurrentModule(newVal, oldVal) {
      this.loadGroupViews()
      if (this.getCurrentModule === 'approval') {
        this.$store.dispatch('view/loadModuleMeta', 'workorder')
      } else {
        this.$store.dispatch('view/loadModuleMeta', this.getCurrentModule)
      }
      this.getViewDetail()
      this.setFilterConfig()
      this.loadSortConfigListForModule()
    },
    showListWorkorder() {
      this.loadSortConfigListForModule()
    },
  },
  methods: {
    loadGroupViews() {
      let param = {
        moduleName: this.getCurrentModule,
        status: false,
      }
      this.$store.dispatch('view/loadGroupViews', param)
    },
    loadAllModuleViews() {
      let { isWorkPermitLicenseEnabled } = this
      let modules = [
        {
          moduleName: 'approval',
          displayName: this.$t('common.header.workrequests'),
          parentPath: `${this.parentPath}/approvals/wr/`,
        },
      ]
      let workPermit = {
        moduleName: 'workpermit',
        displayName: this.$t('Work Permits'),
        parentPath: `${this.parentPath}/approvals/wp/`,
      }
      if (isWorkPermitLicenseEnabled) {
        modules.push(workPermit)
      }
      let self = this
      for (let moduleKey of modules) {
        self.moduleVsViews.mainViewsList[moduleKey.moduleName] =
          moduleKey.displayName
        self.moduleVsViews.parentPathList[moduleKey.moduleName] =
          moduleKey.parentPath
        self.moduleVsViews.preparingViews[moduleKey.moduleName] = []

        self.$http
          .get(
            '/v2/views/viewList?moduleName=' +
              moduleKey.moduleName +
              '&groupStatus=' +
              'false'
          )
          .then(function(response) {
            if (response.data.result) {
              let views = response.data.result.views
              if (moduleKey.moduleName === 'workpermit') {
                views = (views || []).filter(view => view.name === 'requested')
              }
              self.moduleVsViews.preparingViews[moduleKey.moduleName] = views
            }
          })
          .catch(function(error) {
            console.log('######### error: ', error)
          })
      }
    },
    updateSort(sorting) {
      this.$store.dispatch('view/savesorting', {
        viewName: this.currentView,
        orderBy: sorting.orderBy,
        orderType: sorting.orderType,
        moduleName: this.getCurrentModule,
      })
    },
    setFilterConfig() {
      if (this.getCurrentModule === 'approval') {
        this.defaultFilter = 'subject'
        this.filterConfig = {
          moduleName: 'workorder',
          includeParentCriteria: true,
          fetchModuleAction: 'workorder/fetchWorkOrders',
          path: `${this.parentPath}/approvals/wr`,
          data: {
            subject: {
              label: 'Subject',
              displayType: 'string',
              value: [],
            },
            category: {
              label: this.$t('maintenance.wr_list.category'),
              displayType: 'select',
              options: {},
              value: [],
            },
            space: {
              label: this.$t('maintenance._workorder.space'),
              displayType: 'space',
              options: {},
              value: [],
            },
            asset: {
              label: this.$t('maintenance._workorder.asset'),
              displayType: 'asset',
              options: {},
              value: [],
            },
            assignedTo: {
              label: this.$t('maintenance.wr_list.staff/team'),
              displayType: 'select',
              options: {},
              value: [],
            },
            ticketType: {
              label: this.$t('maintenance.wr_list.type'),
              displayType: 'select',
              options: {},
              value: [],
              key: 'type',
            },
            status: {
              label: this.$t('maintenance._workorder.status'),
              displayType: 'select',
              options: {},
              value: [],
            },
            priority: {
              label: this.$t('maintenance.wr_list.priority'),
              displayType: 'select',
              options: {},
              value: [],
            },
            sourceType: {
              label: this.$t('maintenance.wr_list.source_type'),
              displayType: 'select',
              options: this.$constants.SourceType,
              value: [],
            },
            dueDate: {
              label: this.$t('maintenance.wr_list.duedate'),
              displayType: 'select',
              type: 'date',
              customdate: true,
              options: {
                26: 'Overdue',
                22: 'Today',
                23: 'Tomorrow',
                '8_41': 'Next 8 Hours',
              },
              value: [],
            },
            createdTime: {
              label: this.$t('maintenance.wr_list.created_date'),
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
          },
          availableColumns: [
            'subject',
            'category',
            'resource',
            'dueDate',
            'status',
            'assignedTo',
            'createdTime',
            'comments',
            'tasks',
            'priority',
            'noOfNotes',
            'noOfTasks',
            'modifiedTime',
            'actualWorkStart',
            'actualWorkEnd',
          ],
          fixedCols: ['subject'],
          saveView: true,
        }
      } else if (this.getCurrentModule === 'inventoryrequest') {
        this.defaultFilter = 'name'
        this.filterConfig = {
          moduleName: 'inventoryrequest',
          includeParentCriteria: true,
          path: `${this.parentPath}approvals/ir`,
          data: {
            name: {
              label: 'Name',
              displayType: 'string',
              value: [],
            },
            parentId: {
              label: 'Work Order',
              displayType: 'string',
              value: [],
            },
            localId: {
              label: 'Local Id',
              displayType: 'string',
              value: [],
            },
            requestedBy: {
              label: 'Requested By',
              displayType: 'select',
              options: {},
              value: [],
            },
            storeRoom: {
              label: 'Storeroom',
              displayType: 'select',
              options: {},
              value: [],
            },
            requestedFor: {
              label: 'Requested For',
              displayType: 'select',
              options: {},
              value: [],
            },
            requestedTime: {
              label: 'Requested Time',
              displayType: 'select',
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
            requiredTime: {
              label: 'Required Time',
              displayType: 'select',
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
          },
          availableColumns: ['name'],
          fixedCols: ['name'],
          saveView: true,
        }
      } else if (this.getCurrentModule === 'workpermit') {
        this.defaultFilter = 'name'
        this.filterConfig = {
          moduleName: 'workpermit',
          includeParentCriteria: true,
          path: `${this.parentPath}/approvals/wp`,
          data: {
            name: {
              label: 'Name',
              displayType: 'string',
              value: [],
            },
            requestedBy: {
              label: 'Requested By',
              displayType: 'select',
              options: {},
              value: [],
            },
            vendor: {
              label: 'Vendor',
              displayType: 'select',
              options: {},
              value: [],
            },
          },
          availableColumns: ['name'],
          fixedCols: ['name'],
          saveView: true,
        }
      }
    },
    openChild(command) {
      let self = this
      self.selectedList = null
      self.selectedList = command
      let a2 = self.moduleVsViews.preparingViews[command]
      if (a2) {
        self.$router.push(
          this.moduleVsViews.parentPathList[command] + a2[0].name
        )
      }
      this.loadSortConfigListForModule()
    },
    savingView(command) {
      if (command === 'Save') {
        this.save = !this.save
      } else if (command === 'Save As') {
        this.saveAs = !this.saveAs
      }
    },
    deleteView(a, index) {
      this.$dialog
        .confirm({
          title: 'Delete View',
          message: 'Are you sure you want to delete this View?',
          rbDanger: true,
          rbLabel: 'Delete',
        })

        .then(value => {
          if (value) {
            this.$http
              .post('/view/delete', {
                id: a.id,
              })
              .then(response => {
                if (typeof response.data === 'object') {
                  this.loadGroupViews()
                  // this.preparingViews['CustomViews'].splice(index, 1)
                  this.$message.success('View deleted successfully')
                  this.$router.push(this.filterConfig.path + this.views[0].name)
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
    toggleViewFilter() {
      this.filterSelected = !this.filterSelected
    },
    callbackMethod(newVal) {
      this.listCount = newVal
    },
    getViewDetail() {
      this.$store.dispatch('view/loadViewDetail', {
        viewName: this.currentView,
        moduleName: this.getCurrentModule,
      })
    },
    getSubHeaderMenu() {
      let list = []
      let self = this
      Object.keys(self.views).forEach(function(key) {
        let view = self.views[key]
        if (
          self.getCurrentModule !== 'workpermit' ||
          view.name === 'requested'
        ) {
          list.push({
            label: view.displayName,
            path: {
              path:
                self.moduleVsViews.parentPathList[self.getCurrentModule] +
                view.name,
            },
            permission: 'workorder:READ,READ_TEAM,READ_OWN',
            id: view.id,
            name: view.name,
          })
        }
      })
      return list
    },
    metaFieldsLists() {
      let self = this
      self.sortConfigList = []
      self.$http
        .get('/module/metafields?moduleName=' + 'workorder')
        .then(response => {
          self.moduleMetaObject = response.data.meta
          if (self.moduleMetaObject && self.moduleMetaObject.fields) {
            for (let i = 0; i < self.moduleMetaObject.fields.length; i++) {
              self.sortConfigList.push(self.moduleMetaObject.fields[i].name)
            }
          }
        })
    },
    loadSortConfigListForModule() {
      if (this.getCurrentModule === 'approval') {
        this.excludedSortFields = [
          'assignedTo',
          'assignedBy',
          'assignmentGroup',
          'createdBy',
          'requestedBy',
          'requester',
        ]
        this.filtersRetains = ['search', 'includeParentFilter']
        this.metaFieldsLists()
      } else if (this.getCurrentModule === 'inventoryrequest') {
        this.excludedSortFields = null
        this.filtersRetains = null
        this.sortConfigList = [
          'localId',
          'name',
          'requestedTime',
          'requiredTime',
        ]
      }
    },
  },
}
</script>
<style>
.fc-subheader-left {
  display: flex;
  flex-direction: row;
  position: relative;
}

.fc-subheader-right-search-inventory {
  position: relative;
  bottom: 0px;
  right: -2px;
}

.create-btn {
  margin-top: -10px;
}

.wo-three-line {
  position: relative;
  top: 2px;
}

.fc-dropdown-menu {
  font-weight: 500;
  color: #2d2d52;
}

.el-icon-edit.pointer.edit-icon.visibility-hide-actions.fR.editview {
  position: absolute;
  right: 38px;
  top: 10px;
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

.filter-search-close {
  font-size: 18px;
  position: absolute;
  right: 85px;
  top: 10px;
  color: #8ca1ad;
}

.layout-new-tag {
  width: 100%;
  padding-left: 20px;
  padding-right: 10px;
  margin-top: 24px;
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
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
#newworkordercategory .el-textarea .el-textarea__inner {
  min-height: 50px !important;
  width: 350px;
  resize: none;
}
.assetaddvaluedialog .inventory_subheader .subheader-section {
  background: #f8f9fa;
}
.fc-subheader-right-inventory {
  align-items: center;
  margin-top: 8px;
  margin-bottom: 8px;
}
</style>
