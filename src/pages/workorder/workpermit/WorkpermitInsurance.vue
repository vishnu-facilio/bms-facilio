<template>
  <div class="height100">
    <router-link
      :to="{ path: '/tenant/insurance/add', query: { vendorId: vendorId } }"
      append
    >
      <el-button
        class="fc-create-btn create-btn uppercase"
        style="background:#ef508f;float:right"
        ><i class="el-icon-plus"></i
      ></el-button>
    </router-link>
    <div
      class="fc-list-view fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100 fc-v1-portal-table clearboth"
    >
      <div v-if="showLoading" class="flex-middle fc-empty-white">
        <spinner :show="showLoading" size="80"></spinner>
      </div>
      <div v-else>
        <div></div>
        <el-table
          :data="moduleRecordList"
          ref="tableList"
          class="width100"
          height="auto"
          :fit="true"
        >
          <template slot="empty">
            <img src="~statics/noData-light.png" width="100" height="100" />
            <div class="mT10 label-txt-black f14">
              {{ $t('home.insurance.insurance_no_data') }}
            </div>
          </template>
          <el-table-column fixed prop label="ID" min-width="90">
            <template v-slot="data">
              <div class="fc-id">{{ '#' + data.row.id }}</div>
            </template>
          </el-table-column>
          <el-table-column fixed prop label="COMPANY NAME" width="300">
            <template v-slot="data">
              <div
                v-tippy
                small
                :title="$getProperty(data, 'row.companyName', '---')"
                class="flex-middle"
              >
                <div class="mL10">
                  <div class="fw5 ellipsis textoverflow-ellipsis width200px">
                    {{ $getProperty(data, 'row.companyName', '---') }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            :align="field.field.dataTypeEnum === 'DECIMAL' ? 'right' : 'left'"
            v-for="(field, index) in viewColumns"
            :key="index"
            :prop="field.name"
            :label="field.displayName"
            min-width="200"
            v-if="!isFixedColumn(field.name) || field.parentField"
          >
            <template v-slot="data">
              <div
                class="text-align-center"
                v-if="field.name === 'attachmentPreview'"
              >
                <f-list-attachment-preview
                  module="insuranceattachments"
                  :record="data.row"
                ></f-list-attachment-preview>
              </div>
              <div v-else-if="field.name === 'host' && data.row.host">
                <user-avatar size="md" :user="data.row.host"></user-avatar>
              </div>
              <div v-else-if="!isFixedColumn(field.name) || field.parentField">
                <div
                  class="table-subheading"
                  :class="{
                    'text-right': field.field.dataTypeEnum === 'DECIMAL',
                  }"
                >
                  {{ getColumnDisplayValue(field, data.row) || '---' }}
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <column-customization
      :visible.sync="showColumnSettings"
      :moduleName="getModuleName"
      :columnConfig="columnConfig"
      :viewName="currentView"
    ></column-customization>
  </div>
</template>
<script>
import Spinner from '@/Spinner'
import ColumnCustomization from '@/ColumnCustomization'
import ViewMixinHelper from '@/mixins/ViewMixin'
import FListAttachmentPreview from '@/relatedlist/ListAttachmentPreview'
import UserAvatar from '@/avatar/User'
export default {
  props: ['details'],
  mixins: [ViewMixinHelper],
  components: {
    Spinner,
    ColumnCustomization,
    FListAttachmentPreview,
    UserAvatar,
  },
  data() {
    return {
      loading: true,
      fetchingMore: false,
      showColumnSettings: false,
      columnConfig: {
        fixedColumns: ['id', 'companyName'],
        availableColumns: [],
        showLookupColumns: true,
        lookupToShow: ['vendor'],
      },
    }
  },
  computed: {
    moduleStoreName() {
      return 'insurance'
    },
    stateflows() {
      return this.$store.state[this.moduleStoreName].stateFlows
    },
    currentViewFields() {
      return this.$store.state.view.currentViewDetail['fields']
    },
    showLoading() {
      return this.loading || this.$store.state.view.detailLoading
    },
    getModuleName() {
      return this.$route.meta.module
    },
    moduleRecordList() {
      return this.$store.state[this.moduleStoreName].insurance
    },
    openModuleId() {
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
      return this.$store.state[this.moduleStoreName].canLoadMore
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
    searchQuery() {
      return this.$store.state[this.moduleStoreName].quickSearchQuery
    },
    page() {
      return this.$route.query.page || 1
    },
    vendorId() {
      return this.details && this.details.vendor && this.details.vendor.id
        ? this.details.vendor.id
        : -1
    },
  },
  mounted() {
    this.getViewDetail()
    this.loadRecords()
    // this.loadRecordsCount()
  },
  watch: {
    currentViewFields() {
      this.loading = true
      this.$nextTick(() => {
        this.$refs.tableList ? this.$refs.tableList.doLayout() : null
        this.loading = false
      })
    },
    currentView(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadRecords()
        // this.loadRecordsCount()
      }
    },
    filters(newVal) {
      this.loadRecords()
      // this.loadRecordsCount()
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadRecords()
        // this.loadRecordsCount()
      }
    },
    searchQuery() {
      this.loadRecords()
      // this.loadRecordsCount()
    },
  },
  methods: {
    getViewDetail() {
      this.$store.dispatch('view/loadViewDetail', {
        viewName: 'all',
        moduleName: 'insurance',
      })
    },
    refreshList() {
      this.loadRecords()
    },
    loadRecords(loadMore) {
      let queryObj = {
        viewname: this.currentView,
        page: this.page,
        filters: {
          vendor: {
            operatorId: 5,
            value: [this.vendorId + ''],
          },
        },
        search: this.searchQuery,
        isNew: true,
        includeParentFilter: this.includeParentFilter,
      }
      this.loading = true
      this.$store
        .dispatch(this.moduleStoreName + '/fetchInsurance', queryObj)
        .then(response => {
          console.log('insurance------>', response)
          this.loading = false
          this.fetchingMore = false
        })
        .catch(error => {
          if (error) {
            this.loading = false
            this.fetchingMore = false
          }
        })
    },
    loadRecordsCount() {
      let queryObj = {
        filters: this.filters,
        search: this.quickSearchQuery,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        count: true,
      }
      let url = `/v2/insurance/${this.currentView}`
      let params
      params = 'fetchCount=' + queryObj.count
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
      this.$http
        .get(url)
        .then(response => {
          this.listcount = response.data.result.count
          this.$emit('syncCount', response.data.result.count)
        })
        .catch(error => {
          console.log(error)
        })
    },
    loadMore() {
      this.fetchingMore = true
      this.loadRecords(true)
    },
    updateState(transition, record) {
      this.$http
        .post('/v2/insurance/update', {
          insurance: [record],
          stateTransitionId: transition.id,
        })
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success('State updated successfully.')
            this.refreshList()
          } else {
            this.$message.error(response.data.message)
          }
        })
    },
  },
}
</script>
