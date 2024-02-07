<template>
  <div class="height100">
    <div
      class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
      v-if="openBreakId === -1"
    >
      <!-- table start -->
      <div v-if="loading" class="flex-middle fc-empty-white">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div v-else>
        <div class="view-column-chooser" @click="showColumnSettings = true">
          <img
            src="~assets/column-setting.svg"
            style="text-align: center; position: absolute; top: 35%;right: 29%;"
          />
        </div>
        <el-table
          :data="shiftbreaks"
          style="width: 100%;"
          height="auto"
          :fit="true"
        >
          <template slot="empty">
            <img src="~statics/noData-light.png" width="100" height="100" />
            <div class="mT10 label-txt-black f14">
              {{ $t('common.products.no_break_available') }}
            </div>
          </template>
          <el-table-column
            fixed
            prop=""
            :label="$t('common._common.id')"
            min-width="90"
          >
            <template v-slot="shiftbreak">
              <div class="fc-id">{{ '#' + shiftbreak.row.id }}</div>
            </template>
          </el-table-column>
          <el-table-column
            fixed
            prop="name"
            :label="$t('common.products.name')"
            width="300"
          >
            <template v-slot="shiftbreak">
              <div
                v-tippy
                small
                :title="shiftbreak.row.name"
                class="flex-middle"
              >
                <div>
                  <div class="fw5 ellipsis textoverflow-ellipsis width200px">
                    {{ shiftbreak.row.name }}
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
            <template v-slot="shiftbreak">
              <div v-if="!isFixedColumn(field.name) || field.parentField">
                <div v-if="field.name === 'movable'">
                  {{
                    shiftbreak.row.type &&
                    getBreak(shiftbreak.row.type.id).movable
                      ? 'Yes'
                      : 'No'
                  }}
                </div>
                <div
                  class="table-subheading"
                  v-else-if="field.name === 'breakTime'"
                  :class="{
                    'text-right': field.field.dataTypeEnum === 'DECIMAL',
                  }"
                >
                  {{
                    convertSecondsToTimeHHMM(shiftbreak.row[field.name]) ||
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
                  {{ getColumnDisplayValue(field, shiftbreak.row) || '---' }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop=""
            label=""
            width="130"
            class="visibility-visible-actions"
          >
            <template v-slot="shiftbreak">
              <div class="text-center">
                <i
                  class="el-icon-edit edit-icon-color visibility-hide-actions"
                  :title="$t('common.products.edit_break')"
                  data-arrow="true"
                  v-tippy
                  @click="editBreak(shiftbreak.row)"
                ></i>
                <i
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common.header.delete_break')"
                  v-tippy
                  @click="deleteBreak(shiftbreak.row.id)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- table end -->
    </div>
    <div v-if="showEditBreakDialog">
      <new-break
        v-if="showEditBreakDialog"
        :breakEditData="shiftbreakObj"
        :visibility.sync="showEditBreakDialog"
        @saved="refreshBreakList"
      ></new-break>
    </div>
    <column-customization
      :visible.sync="showColumnSettings"
      moduleName="break"
      :viewName="currentView"
    ></column-customization>
  </div>
</template>
<script>
import Spinner from '@/Spinner'
import ColumnCustomization from '@/ColumnCustomization'
import ViewMixinHelper from '@/mixins/ViewMixin'
import moment from 'moment-timezone'
import NewBreak from './NewBreak'
export default {
  mixins: [ViewMixinHelper],
  components: {
    Spinner,
    ColumnCustomization,
    NewBreak,
  },
  data() {
    return {
      shiftbreakObj: '',
      showEditBreakDialog: false,
      isVisible: false,
      loading: true,
      showPrintPreview: false,
      selectedBreak: [],
      shiftbreakList: [],
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
    shiftbreaks() {
      return this.$store.state.shiftbreak.break_list
    },
    openBreakId() {
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
      return this.$store.state.shiftbreak.canLoadMore
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
    searchQuery() {
      return this.$store.state.shiftbreak.quickSearchQuery
    },
    page() {
      return this.$route.query.page || 1
    },
  },
  mounted() {
    this.loadBreak()
    // this.loadBreakCount()
  },
  watch: {
    currentView: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadBreak()
        // this.loadBreakCount()
      }
    },
    filters: function(newVal) {
      this.loadBreak()
      // this.loadBreakCount()
    },
    page: function(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadBreak()
        // this.loadWOCounts()
      }
    },
    searchQuery() {
      this.loadBreak()
      // this.loadBreakCount()
    },
  },
  methods: {
    editBreak(shiftbreak) {
      this.shiftbreakObj = shiftbreak
      this.showEditBreakDialog = true
    },
    closeNewDialog() {
      this.shiftbreakObj = ''
      this.showEditBreakDialog = false
    },
    refreshBreakList() {
      this.loadBreak()
      this.showEditBreakDialog = false
    },
    loadBreak(loadMore) {
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
        .dispatch('shiftbreak/fetchBreak', queryObj)
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
    loadBreakCount() {
      let self = this
      let queryObj = {
        viewname: this.currentView,
        filters: this.filters,
        search: this.quickSearchQuery,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        count: true,
      }
      let url = '/v2/break/count?viewName=' + queryObj.viewname
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
      this.loadBreak(true)
    },
    deleteBreak(id) {
      let self = this
      self.$dialog
        .confirm({
          title: this.$t('common.header.delete_break'),
          message: this.$t(
            'common.header.are_you_sure_you_want_to_delete_this_break'
          ),
          rbDanger: true,
          rbLabel: self.$t('common._common.delete'),
        })
        .then(function(value) {
          if (value) {
            self.$http
              .post('/v2/break/delete', { id: id })
              .then(function(response) {
                if (response.data.responseCode === 0) {
                  self.$message.success(
                    this.$t('common.products.break_deleted_successfully')
                  )
                  self.loadBreak()
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
