<template>
  <div class="layout container">
    <subheader
      :menu="subheaderMenu"
      parent="/app/pl/sft"
      maxVisibleMenu="4"
      class="inventory_subheader"
    >
      <div
        v-if="
          getCurrentModule === 'shift' ||
            getCurrentModule === 'break' ||
            getCurrentModule === 'shiftRotation'
        "
        class="fR fc-subheader-right fc-subheader-right-inventory"
      >
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
          <div>
            <div v-if="getCurrentModule === 'shift'">
              <el-button
                class="fc-create-btn create-btn"
                @click="newShiftForm"
                v-show="!showQuickSearch"
              >
                <div>{{ $t('common.products.add_shift') }}</div>
              </el-button>
            </div>
            <div v-else-if="getCurrentModule === 'break'">
              <el-button
                class="fc-create-btn create-btn"
                @click="newBreakForm"
                v-show="!showQuickSearch"
              >
                <div>{{ $t('common.products.add_break') }}</div>
              </el-button>
            </div>
            <div v-else-if="getCurrentModule === 'shiftRotation'">
              <el-button
                class="fc-create-btn create-btn"
                @click="newShiftRotationForm"
                v-show="!showQuickSearch"
              >
                <div>{{ $t('common.products.new_shift_rotation') }}</div>
              </el-button>
            </div>
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
          >
          </new-search>
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
            :filters="appliedFilters"
            class="layout-new-tag"
          ></new-tag>
        </div>
        <div class="save-btn-section">
          <div v-if="appliedFilters">
            <div v-if="!viewDetail.isDefault">
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
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>
    <new-break
      v-if="newBreakFormVisibility"
      :visibility.sync="newBreakFormVisibility"
      @saved="loadBreak"
    ></new-break>
    <new-shift
      v-if="newShiftFormVisibility"
      :visibility.sync="newShiftFormVisibility"
      @saved="loadShift"
    ></new-shift>
    <new-shift-rotation
      v-if="newShiftRotationFormVisibility"
      :visibility.sync="newShiftRotationFormVisibility"
      @saved="loadShiftRotation"
    ></new-shift-rotation>
  </div>
</template>
<script>
import Subheader from '@/Subheader'
import { mapState, mapActions } from 'vuex'
import NewSearch from '@/NewSearch'
import Pagination from '@/list/FPagination'
import NewTag from '@/NewTag'
import PageSort from '@/PageSort'
import NewBreak from './NewBreak'
import NewShift from './NewShift'
import NewShiftRotation from './NewShiftRotation'
export default {
  data() {
    return {
      showListWorkorder: false,
      showEditIcon: true,
      defaultFilter: 'name',
      showMail: false,
      listCount: '',
      save: false,
      saveAs: false,
      resetFilters: false,
      selectedList: null,
      showQuickSearch: false,
      quickSearchQuery: null,
      saving: false,
      formObj: null,
      sortConfig: {
        orderBy: {
          label: 'Name',
          value: 'name',
        },
        orderType: 'asc',
      },
      sortConfigList: null,
      filterSelected: false,
      showViewSettings: false,
      exportDownloadUrl: null,
      moduleVsViews: {
        mainViewsList: {},
        preparingViews: {},
        parentPathList: {},
      },
      filterConfig: null,
      newBreakFormVisibility: false,
      newShiftFormVisibility: false,
      newShiftRotationFormVisibility: false,
    }
  },
  components: {
    Subheader,
    NewSearch,
    NewTag,
    Pagination,
    PageSort,
    NewBreak,
    NewShift,
    NewShiftRotation,
  },
  created() {
    this.$store.dispatch('loadShifts')
    this.$store.dispatch('view/clearViews')
  },
  mounted() {
    // this.loadAllModuleViews()
    if (this.getCurrentModule !== 'shiftplanner') {
      this.loadGroupViews()
      this.$store.dispatch('view/loadModuleMeta', this.getCurrentModule)
      this.getViewDetail()
      if (!this.filterSelected && this.filters) {
        this.toggleViewFilter()
      }
      this.loadSortConfigListForModule()
      this.setFilterConfig()
    }
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
    subheaderNeeded() {
      return this.$route.params.viewname !== undefined
    },
    getCurrentModule() {
      this.selectedList = this.$route.meta.module || 'shift'
      return this.$route.meta.module || 'shift'
    },
  },
  filters: {
    viewName: function(name, modulename) {
      let prefix = ''
      let suffix = ''
      if (name === 'all') {
        prefix = 'All '
      }
      if (modulename === 'shift') {
        suffix = 'Shift(s)'
      } else if (modulename === 'break') {
        suffix = 'Break'
      }
      return prefix + suffix
    },
  },
  watch: {
    filters: function(val) {
      if (val != null && !this.filterSelected) {
        this.toggleViewFilter()
      }
    },
    $route: function(newVal, oldVal) {
      if (
        newVal.params.viewname !== oldVal.params.viewname ||
        newVal.meta.module !== oldVal.meta.module
      ) {
        this.setTitle(
          this.$options.filters.viewName(
            newVal.params.viewname,
            newVal.meta.module
          )
        )
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
      if (newVal !== oldVal && newVal !== 'shiftplanner') {
        this.loadGroupViews()
        this.$store.dispatch('view/loadModuleMeta', this.getCurrentModule)
        this.getViewDetail()
        this.setFilterConfig()
        this.loadSortConfigListForModule()
      }
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
      let modules = [
        {
          moduleName: 'shift',
          displayName: 'Shift(s)',
          parentPath: '/app/pl/sft/shift/',
        },
        {
          moduleName: 'break',
          displayName: 'Break',
          parentPath: '/app/pl/sft/break/',
        },
      ]
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
              self.moduleVsViews.preparingViews[moduleKey.moduleName] =
                response.data.result.views
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
      if (this.getCurrentModule === 'shift') {
        this.defaultFilter = 'name'
        this.filterConfig = {
          moduleName: 'shift',
          includeParentCriteria: true,
          path: '/app/pl/sft/shift/',
          data: {
            id: {
              label: 'ID',
              displayType: 'string',
              value: [],
            },
            name: {
              label: 'Name',
              displayType: 'string',
              value: [],
            },
          },
          availableColumns: ['name'],
          fixedCols: ['name'],
          saveView: true,
        }
      } else if (this.getCurrentModule === 'break') {
        this.defaultFilter = 'name'
        this.filterConfig = {
          moduleName: 'break',
          includeParentCriteria: true,
          path: '/app/pl/sft/break/',
          data: {
            id: {
              label: 'ID',
              displayType: 'string',
              value: [],
            },
            name: {
              label: 'Name',
              displayType: 'string',
              value: [],
            },
          },
          availableColumns: ['name'],
          fixedCols: ['name'],
          saveView: true,
        }
      } else if (this.getCurrentModule === 'shiftRotaion') {
        this.defaultFilter = 'schedularName'
        this.filterConfig = {
          moduleName: 'shiftRotaion',
          includeParentCriteria: true,
          path: '/app/pl/sft/shiftrt/',
          data: {
            id: {
              label: 'ID',
              displayType: 'string',
              value: [],
            },
            schedularName: {
              label: 'Scheduler Name',
              displayType: 'string',
              value: [],
            },
          },
          availableColumns: ['schedularName'],
          fixedCols: ['schedularName'],
          saveView: true,
        }
      }
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
      list.push({
        label: 'Shift(s)',
        path: { path: '/app/pl/sft/shift/all' },
      })
      list.push({
        label: 'Break',
        path: { path: '/app/pl/sft/break/all' },
      })
      list.push({
        label: 'Shift Planner',
        path: { path: '/app/pl/sft/shiftplanner/all' },
      })
      list.push({
        label: 'Shift Rotation',
        path: { path: '/app/pl/sft/shiftrt/all' },
      })
      return list
    },
    loadSortConfigListForModule() {
      if (this.getCurrentModule === 'shift') {
        this.sortConfigList = ['name', 'startTime', 'endTime']
      } else if (this.getCurrentModule === 'break') {
        this.sortConfigList = ['name', 'breakTime', 'breakTime']
      } else if (this.getCurrentModule === 'shiftRotation') {
        this.sortConfigList = ['schedularName']
      }
    },
    loadBreak() {
      let self = this
      let queryObj = {
        viewname: this.currentView,
      }
      self.loading = true
      self.$store
        .dispatch('shiftbreak/fetchBreak', queryObj)
        .then(function(response) {
          self.loading = false
          self.fetchingMore = false
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.fetchingMore = false
          }
        })
    },
    loadShift() {
      let self = this
      let queryObj = {
        viewname: this.currentView,
      }
      self.loading = true
      self.$store
        .dispatch('shift/fetchShifts', queryObj)
        .then(function(response) {
          self.loading = false
          self.fetchingMore = false
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.fetchingMore = false
          }
        })
    },
    loadShiftRotation() {
      let self = this
      let queryObj = {
        viewname: this.currentView,
      }
      self.loading = true
      self.$store
        .dispatch('shiftrotation/fetchShiftRotations', queryObj)
        .then(function(response) {
          self.loading = false
          self.fetchingMore = false
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.fetchingMore = false
          }
        })
    },
    newBreakForm() {
      this.newBreakFormVisibility = true
    },
    newShiftForm() {
      this.newShiftFormVisibility = true
    },
    newShiftRotationForm() {
      this.newShiftRotationFormVisibility = true
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
