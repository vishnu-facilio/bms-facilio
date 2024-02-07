<template>
  <div class="height100">
    <div
      class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
      v-if="openShiftRotationId === -1"
    >
      <!-- table start -->
      <div v-if="loading" class="flex-middle fc-empty-white">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div v-else>
        <!-- <div class="view-column-chooser" @click="showColumnSettings = true">
          <img
            src="~assets/column-setting.svg"
            style="text-align: center; position: absolute; top: 35%;right: 29%;"
          >
        </div>-->
        <el-table
          :data="shiftRotations"
          style="width: 100%;"
          height="auto"
          :fit="true"
        >
          <template slot="empty">
            <img src="~statics/noData-light.png" width="100" height="100" />
            <div class="mT10 label-txt-black f14">
              {{ $t('common.products.no_shift_rotation_available') }}
            </div>
          </template>
          <el-table-column
            fixed
            prop
            :label="$t('common._common.id')"
            min-width="90"
          >
            <template v-slot="shiftRotation">
              <div class="fc-id">{{ '#' + shiftRotation.row.id }}</div>
            </template>
          </el-table-column>
          <el-table-column
            fixed
            prop="name"
            :label="$t('common.products.name')"
            width="300"
          >
            <template v-slot="shiftRotation">
              <div
                v-tippy
                small
                :title="shiftRotation.row.schedularName"
                class="flex-middle"
              >
                <div class="mL10">
                  <div class="fw5 ellipsis textoverflow-ellipsis width200px">
                    {{ shiftRotation.row.schedularName }}
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
            <template v-slot="shiftRotation">
              <div v-if="!isFixedColumn(field.name) || field.parentField">
                <div v-if="field.name === 'movable'">
                  {{
                    shiftRotation.row.type &&
                    getShiftRotation(shiftRotation.row.type.id).movable
                      ? 'Yes'
                      : 'No'
                  }}
                </div>
                <div
                  class="table-subheading"
                  v-else-if="field.name === 'timeOfSchedule'"
                  :class="{
                    'text-right': field.field.dataTypeEnum === 'DECIMAL',
                  }"
                >
                  {{
                    convertSecondsToTimeHHMM(shiftRotation.row[field.name]) ||
                      '---'
                  }}
                </div>
                <div
                  class="table-subheading"
                  v-else
                  :class="{
                    'text-right': field.field.dataTypeEnum === 'DECIMAL',
                  }"
                >
                  {{ getColumnDisplayValue(field, shiftRotation.row) || '---' }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop
            label
            width="130"
            class="visibility-visible-actions"
          >
            <template v-slot="shiftRotation">
              <div class="text-center">
                <i
                  class="el-icon-edit edit-icon-color visibility-hide-actions"
                  :title="$t('common.products.edit_shift_rotation')"
                  data-arrow="true"
                  v-tippy
                  @click="editShiftRotation(shiftRotation.row)"
                ></i>
                <i
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                  v-if="!shiftRotation.row.defaultShiftRotation"
                  data-arrow="true"
                  :title="$t('common.header.delete_shift_rotation')"
                  v-tippy
                  @click="deleteShiftRotation(shiftRotation.row.id)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- table end -->
    </div>
    <div v-if="showCreateNewDialog">
      <new-shift-rotation
        v-if="showCreateNewDialog"
        :editData="shiftRotationObj"
        :visibility.sync="showCreateNewDialog"
        @saved="loadShiftRotations"
      ></new-shift-rotation>
    </div>
    <!-- <column-customization
      :visible.sync="showColumnSettings"
      moduleName="shiftRotation"
    ></column-customization>-->
  </div>
</template>
<script>
import Spinner from '@/Spinner'
import ViewMixinHelper from '@/mixins/ViewMixin'
import NewShiftRotation from './NewShiftRotation'
import moment from 'moment-timezone'
export default {
  mixins: [ViewMixinHelper],
  components: {
    Spinner,
    NewShiftRotation,
  },
  data() {
    return {
      shiftRotationObj: '',
      showCreateNewDialog: false,
      isVisible: false,
      loading: true,
      showPrintPreview: false,
      selectedShiftRotations: [],
      shiftRotationList: [],
      fetchingMore: false,
      saving: false,
      actions: {
        delete: {
          loading: false,
        },
      },
      showColumnSettings: false,
    }
  },
  computed: {
    shiftRotations() {
      return this.$store.state.shiftrotation.shiftRotations
    },
    openShiftRotationId() {
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
      return this.$store.state.shiftrotation.canLoadMore
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
    searchQuery() {
      return this.$store.state.shiftrotation.quickSearchQuery
    },
    page() {
      return this.$route.query.page || 1
    },
  },
  mounted() {
    this.loadShiftRotations()
    // this.loadShiftRotationsCount()
  },
  watch: {
    currentView: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadShiftRotations()
        // this.loadShiftRotationsCount()
      }
    },
    filters: function(newVal) {
      this.loadShiftRotations()
      // this.loadShiftRotationsCount()
    },
    page: function(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadShiftRotations()
        // this.loadWOCounts()
      }
    },
    searchQuery() {
      this.loadShiftRotations()
      // this.loadShiftRotationsCount()
    },
  },
  methods: {
    editShiftRotation(shiftRotation) {
      this.shiftRotationObj = shiftRotation
      this.showCreateNewDialog = true
    },
    closeNewDialog() {
      this.shiftRotationObj = ''
      this.showCreateNewDialog = false
    },
    refreshShiftRotationList() {
      this.loadShiftRotations()
      this.showCreateNewDialog = false
    },
    loadShiftRotations(loadMore) {
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
        .dispatch('shiftrotation/fetchShiftRotations', queryObj)
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
    loadShiftRotationsCount() {
      let self = this
      let queryObj = {
        viewname: this.currentView,
        filters: this.filters,
        search: this.quickSearchQuery,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        count: true,
      }
      let url = '/v2/shiftRotation/count?viewName=' + queryObj.viewname
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
      this.loadShiftRotations(true)
    },
    deleteShiftRotation(id) {
      let self = this
      self.$dialog
        .confirm({
          title: self.$t('common.header.delete_shift_rotation'),
          message: self.$t(
            'common.header.are_you_sure_you_want_to_delete_this_shift_rotation'
          ),
          rbDanger: true,
          rbLabel: self.$t('common._common.delete'),
        })
        .then(function(value) {
          if (value) {
            self.$http
              .post('/v2/shiftRotation/delete', { id: id })
              .then(function(response) {
                if (response.data.responseCode === 0) {
                  self.$message.success(
                    self.$t(
                      'common.products.shift_rotation_deleted_successfully'
                    )
                  )
                  self.loadShiftRotations()
                } else {
                  self.$message.error(response.data.message)
                }
              })
          }
        })
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
