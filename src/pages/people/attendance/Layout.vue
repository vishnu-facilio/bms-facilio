<template>
  <div class="layout container">
    <subheader
      :menu="subheaderMenu"
      parent="/app/pl/attendance"
      :listCount="listCount"
    >
      <template slot="prefix">
        <div class="fL fc-subheader-left"></div>
      </template>
      <div class="fR fc-subheader-right fc-subheader-right-inventory">
        <template v-if="!showQuickSearch">
          <div class="pL15 fc-black-small-txt-12">
            <pagination :total="listCount" :perPage="50"></pagination>
            <div class="block"></div>
          </div>
          <span class="separator" v-if="listCount > 0">|</span>
          <new-date-picker
            v-if="dateObj"
            :zone="$timezone"
            class="filter-field date-filter-comp mT6"
            :dateObj="dateObj"
            :tabs="getRestrictedPickerTabs()"
            @date="changeDateFilter"
          ></new-date-picker>
        </template>
      </div>
    </subheader>
    <div class="height100 row width100">
      <div class="height100 f-list-view">
        <router-view @syncCount="callbackMethod"></router-view>
      </div>
    </div>
    <view-customization
      :visible.sync="showViewSettings"
      :reload="true"
      @onchange="loadGroupViews()"
      :menu="subheaderMenu"
      moduleName="attendance"
    ></view-customization>
  </div>
</template>
<script>
import Subheader from '@/Subheader'
import ViewCustomization from '@/ViewCustomization'
import { mapState, mapActions } from 'vuex'
import Pagination from '@/list/FPagination'
import NewDatePicker from '@/NewDatePicker'
import NewDateHelper from '@/mixins/NewDateHelper'
export default {
  data() {
    return {
      showListWorkorder: false,
      showEditIcon: true,
      defaultFilter: 'lastCheckInTime',
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
          label: 'checkInTime',
          value: 'checkInTime',
        },
        orderType: 'asc',
      },
      filterSelected: false,
      showViewSettings: false,
      exportDownloadUrl: null,
      moduleVsViews: {
        mainViewsList: {},
        preparingViews: {},
        parentPathList: {},
      },
      dateObj: null,
      filterConfig: null,
    }
  },
  mixins: [NewDateHelper],
  components: {
    Subheader,
    ViewCustomization,
    Pagination,
    NewDatePicker,
  },
  created() {
    this.$store.dispatch('view/clearViews')
  },
  mounted() {
    this.loadAllModuleViews()
    this.loadGroupViews()
    this.$store.dispatch('view/loadModuleMeta', 'attendance')
    this.getViewDetail()
    if (!this.filterSelected && this.filters) {
      this.toggleViewFilter()
    }
    this.setTitle('Attendance')
    this.loadDateObj()
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
    getCurrentList() {
      this.selectedList = this.$route.meta.listType || 'table'
      return this.$route.meta.listType || 'table'
    },
  },
  filters: {
    viewName: function(name) {
      let prefix = ''
      let suffix = ''
      if (name === 'all') {
        prefix = 'All '
      }
      suffix = 'Attendance'
      return prefix + suffix
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
    dateObj(newVal, oldVal) {
      if (newVal && newVal.value && newVal.value[0]) {
        this.$router.push(
          this.$route.path +
            '?startTime=' +
            newVal.value[0] +
            '&endTime=' +
            newVal.value[1]
        )
      }
    },
    getCurrentList(newVal, oldVal) {
      if (this.dateObj && this.dateObj.value) {
        this.$router.push(
          this.$route.path +
            '?startTime=' +
            this.dateObj.value[0] +
            '&endTime=' +
            this.dateObj.value[1]
        )
      } else {
        this.loadDateObj()
      }
    },
  },
  methods: {
    loadGroupViews() {
      let param = {
        moduleName: 'attendance',
        status: false,
      }
      this.$store.dispatch('view/loadGroupViews', param)
    },
    loadAllModuleViews() {
      // let modules = [
      //   {
      //     moduleName: 'table',
      //     displayName: 'Table View',
      //     parentPath: '/app/pl/attendance/table/'
      //   },
      //   {
      //     moduleName: 'calendar',
      //     displayName: 'Calendar View',
      //     parentPath: '/app/pl/attendance/calendar/'
      //   }
      // ]
      let self = this
      self.moduleVsViews.mainViewsList['table'] = 'Table View'
      self.moduleVsViews.parentPathList['table'] = '/app/pl/attendance/table/'
      self.moduleVsViews.mainViewsList['calendar'] = 'Calendar View'
      self.moduleVsViews.parentPathList['calendar'] =
        '/app/pl/attendance/calendar/'

      self.$http
        .get(
          '/v2/views/viewList?moduleName=' +
            'attendance' +
            '&groupStatus=' +
            'false'
        )
        .then(function(response) {
          if (response.data.result) {
            self.moduleVsViews.preparingViews['table'] =
              response.data.result.views
            self.moduleVsViews.preparingViews['calendar'] =
              response.data.result.views
          }
        })
        .catch(function(error) {
          console.log('######### error: ', error)
        })
    },
    updateSort(sorting) {
      this.$store.dispatch('view/savesorting', {
        viewName: this.currentView,
        orderBy: sorting.orderBy,
        orderType: sorting.orderType,
        moduleName: 'attendance',
      })
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
                  this.$message.success('View deleted successfully')
                  this.$router.push(this.filterConfig.path + this.views[0].name)
                } else {
                  this.$message.error('View cannot be deleted')
                }
              })
          }
        })
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
        moduleName: 'attendance',
      })
    },
    getSubHeaderMenu() {
      let list = []
      let self = this
      list.push({
        label: 'Tabular View',
        path: { path: '/app/pl/attendance/table/all' },
      })
      list.push({
        label: 'Calendar View',
        path: { path: '/app/pl/attendance/calendar/all' },
      })
      return list
    },
    loadAttendance() {
      let self = this
      let queryObj = {
        viewname: this.currentView,
      }
      self.loading = true
      self.$store
        .dispatch('attendance/fetchAttendance', queryObj)
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
    changeDateFilter(dateFilter) {
      this.$set(this, 'dateObj', dateFilter)
    },
    loadDateObj() {
      this.$set(this, 'dateObj', NewDateHelper.getDatePickerObject(64))
    },
    getRestrictedPickerTabs() {
      let temp = {}
      temp['enableByOperationOnId'] = true
      temp['disableDefaultLabels'] = true
      let enabledTabs = []

      enabledTabs.push('M')

      temp['enabledTabs'] = enabledTabs
      temp['loadAdditional'] = {
        year: {
          period: 'start',
          label: 'year',
          operation: 'add',
          value: 4,
        },
      }
      return temp
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
.pagination {
  padding-top: 13px;
}
.fc-subheader-right-inventory {
  align-items: center;
  margin-top: 8px;
  margin-bottom: 8px;
}
</style>
