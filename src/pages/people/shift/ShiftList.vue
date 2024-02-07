<template>
  <div class="height100">
    <div
      class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
      v-if="openShiftId === -1"
    >
      <!-- table start -->
      <div v-if="loading" class="flex-middle fc-empty-white">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div
        v-if="$validation.isEmpty(shifts) && !loading"
        class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column"
      >
        <inline-svg
          src="svgs/list-empty"
          iconClass="icon text-center icon-xxxxlg"
        ></inline-svg>
        <div class="mT10 fc-black-dark f16 fw6">
          <div class="mT10 label-txt-black f14">
            {{ $t('common.products.no_shift_available') }}
          </div>
        </div>
      </div>
      <div v-if="!loading && !$validation.isEmpty(shifts)">
        <div class="view-column-chooser" @click="showColumnSettings = true">
          <img
            src="~assets/column-setting.svg"
            style="text-align: center; position: absolute; top: 35%;right: 29%;"
          />
        </div>
        <el-table :data="shifts" style="width: 100%;" height="auto" :fit="true">
          <!-- <template slot="empty">
            <img src="~statics/noData-light.png" width="100" height="100" />
            <div class="mT10 label-txt-black f14">No Shift available</div>
          </template> -->
          <el-table-column
            fixed
            prop=""
            :label="$t('common._common.id')"
            min-width="90"
          >
            <template v-slot="shift">
              <div class="fc-id">{{ '#' + shift.row.id }}</div>
            </template>
          </el-table-column>
          <el-table-column
            fixed
            prop="name"
            :label="$t('common.products.name')"
            width="300"
          >
            <template v-slot="shift">
              <div v-tippy small :title="shift.row.name" class="flex-middle">
                <div>
                  <div class="fw5 ellipsis textoverflow-ellipsis width200px">
                    {{ shift.row.name }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            :fixed="field.name === 'name'"
            :align="field.field.dataTypeEnum === 'DECIMAL' ? 'right' : 'left'"
            v-for="(field, index) in viewColumns"
            :key="index"
            :prop="field.name"
            :label="field.displayName"
            min-width="200"
            v-if="!isFixedColumn(field.name) || field.parentField"
          >
            <template v-slot="shift">
              <div v-if="!isFixedColumn(field.name) || field.parentField">
                <div v-if="field.name === 'movable'">
                  {{
                    shift.row.type && getShift(shift.row.type.id).movable
                      ? 'Yes'
                      : 'No'
                  }}
                </div>
                <div
                  class="table-subheading"
                  v-else-if="
                    field.name === 'startTime' || field.name === 'endTime'
                  "
                  :class="{
                    'text-right': field.field.dataTypeEnum === 'DECIMAL',
                  }"
                >
                  {{ convertSecondsToTimeHHMM(shift.row[field.name]) || '---' }}
                </div>
                <div
                  class="table-subheading"
                  v-else
                  :class="{
                    'text-right': field.field.dataTypeEnum === 'DECIMAL',
                  }"
                >
                  {{ getColumnDisplayValue(field, shift.row) || '---' }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop=""
            label=""
            width="130"
            class="visibility-visible-actions"
            fixed="right"
          >
            <template v-slot="shift">
              <div class="text-center">
                <i
                  class="el-icon-edit edit-icon-color visibility-hide-actions"
                  :title="$t('common.products.edit_shift')"
                  data-arrow="true"
                  v-tippy
                  @click="editShift(shift.row)"
                ></i>
                <i
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                  v-if="!shift.row.defaultShift"
                  data-arrow="true"
                  :title="$t('common.header.delete_shift')"
                  v-tippy
                  @click="deleteShift(shift.row.id)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- table end -->
    </div>
    <div v-if="showCreateNewDialog">
      <new-shift
        v-if="showCreateNewDialog"
        :shiftEditData="shiftObj"
        :visibility.sync="showCreateNewDialog"
        @saved="loadShifts"
      ></new-shift>
    </div>
    <column-customization
      :visible.sync="showColumnSettings"
      moduleName="shift"
      :viewName="currentView"
    ></column-customization>
  </div>
</template>
<script>
import Spinner from '@/Spinner'
import ColumnCustomization from '@/ColumnCustomization'
import ViewMixinHelper from '@/mixins/ViewMixin'
import NewShift from './NewShift'
import moment from 'moment-timezone'
export default {
  mixins: [ViewMixinHelper],
  components: {
    Spinner,
    ColumnCustomization,
    NewShift,
  },
  data() {
    return {
      shiftObj: '',
      showCreateNewDialog: false,
      isVisible: false,
      loading: true,
      showPrintPreview: false,
      selectedShifts: [],
      shiftList: [],
      fetchingMore: false,
      saving: false,
      actions: {
        delete: {
          loading: false,
        },
      },
      showColumnSettings: false,
      resetForm: false,
      emitForm: false,
    }
  },
  computed: {
    shifts() {
      return this.$store.state.shift.shifts
    },
    openShiftId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    canLoadMore() {
      return this.$store.state.shift.canLoadMore
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
    searchQuery() {
      return this.$store.state.shift.quickSearchQuery
    },
    page() {
      return this.$route.query.page || 1
    },
  },
  created() {
    this.$store.dispatch('loadShifts')
  },
  mounted() {
    this.loadShifts()
    // this.loadShiftsCount()
  },
  watch: {
    currentView: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadShifts()
        // this.loadShiftsCount()
      }
    },
    shifts: function() {},
    filters: function(newVal) {
      this.loadShifts()
      // this.loadShiftsCount()
    },
    page: function(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadShifts()
        // this.loadWOCounts()
      }
    },
    searchQuery() {
      this.loadShifts()
      // this.loadShiftsCount()
    },
  },
  methods: {
    editShift(shift) {
      this.shiftObj = shift
      this.showCreateNewDialog = true
    },
    closeNewDialog() {
      this.shiftObj = ''
      this.showCreateNewDialog = false
    },
    refreshShiftList() {
      this.loadShifts()
      this.showCreateNewDialog = false
    },
    loadShifts(loadMore) {
      let self = this
      let queryObj = {
        viewname: this.currentView,
        page: this.page,
        filters: this.filters,
        search: this.searchQuery,
        isNew: true,
        includeParentFilter: this.includeParentFilter,
      }
      self.loading = true
      self.$store
        .dispatch('shift/fetchShifts', queryObj)
        .then(function(response) {
          self.loading = false
          self.fetchingMore = false
          // self.page++
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.fetchingMore = false
          }
        })
    },
    loadShiftsCount() {
      let self = this
      let queryObj = {
        viewname: this.currentView,
        filters: this.filters,
        search: this.quickSearchQuery,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        count: true,
      }
      let url = '/v2/shift/count?viewName=' + queryObj.viewname
      let params
      params = 'count=' + queryObj.count
      if (queryObj.filters) {
        params =
          params +
          '&filters=' +
          encodeURIComponent(JSON.stringify(queryObj.filters))
      }
      if (queryObj.search) {
        params = params + '&search=' + queryObj.search
      }
      if (queryObj.criteriaIds) {
        params = params + '&criteriaIds=' + queryObj.criteriaIds
      }
      if (queryObj.includeParentFilter) {
        params = params + '&includeParentFilter=' + queryObj.includeParentFilter
      }
      url = url + '&' + params
      self.$http
        .get(url)
        .then(function(response) {
          self.listcount = response.data.result.count
          self.$emit('syncCount', response.data.result.count)
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    loadMore() {
      this.fetchingMore = true
      this.loadShifts(true)
    },
    deleteShift(id) {
      let self = this
      self.$dialog
        .confirm({
          title: self.$t('common.header.delete_shift'),
          message: self.$t(
            'common.header.are_you_sure_you_want_to_delete_this_shift'
          ),
          rbDanger: true,
          rbLabel: self.$t('common._common.delete'),
        })
        .then(function(value) {
          if (value) {
            self.$http
              .post('/v2/shift/delete', { id: id })
              .then(function(response) {
                if (response.data.responseCode === 0) {
                  self.$message.success(
                    self.$t('common.products.shift_deleted_successfully')
                  )
                  self.loadShifts()
                } else {
                  self.$message.error(response.data.message)
                }
              })
          }
        })
    },
    cancelForm() {
      this.resetForm = true
      this.showCreateNewDialog = false
    },
    saveForm() {
      this.emitForm = true
    },
    submitForm(data, shift) {
      let self = this
      this.showCreateNewDialog = false
      let url = '/v2/shift/update'
      if (data.unit === '') {
        delete data.unit
      }
      let param = {
        shifts: data,
      }
      param.shifts['id'] = shift.id
      self.$http
        .post(url, param)
        .then(response => {
          if (response.data.responseCode === 0) {
            self.$message.success(
              this.$t('common._common.updated_successfully')
            )
          } else {
            self.$message.error(response.data.message)
          }
          self.loadShifts()
        })
        .catch(error => {
          console.log(error)
          self.$message.error(this.$t('common.wo_report.unable_to_update'))
        })
      self.emitForm = false
    },
    convertSecondsToTimeHHMM(val) {
      return moment()
        .startOf('day')
        .seconds(val)
        .format('HH:mm')
    },
  },
}
</script>
