<template>
  <PageLayout :moduleName="moduleName">
    <template slot="title">
      {{ moduleDisplayName }}
    </template>
    <template slot="header">
      <template v-if="listCount">
        <pagination :total="listCount" :perPage="50"></pagination>
      </template>
      <el-tooltip
        effect="dark"
        :content="$t('common._common.sort')"
        placement="bottom"
      >
        <Sort
          :moduleName="moduleName"
          :key="moduleName + '-sort'"
          @sortChange="loadRecords"
        >
        </Sort>
      </el-tooltip>
      <el-tooltip
        effect="dark"
        :content="$t('common._common.search')"
        placement="bottom"
      >
        <AdvancedSearch
          :key="`${moduleName}-search`"
          :moduleName="moduleName"
          :moduleDisplayName="moduleDisplayName"
        >
        </AdvancedSearch>
      </el-tooltip>
    </template>
    <Tags :key="`ftags-list-${moduleName}`" :hideSaveView="true"></Tags>
    <div class="list-container" v-if="openModuleId === -1">
      <div v-if="showLoading" class="list-loading">
        <spinner :show="showLoading" size="80"></spinner>
      </div>
      <div
        v-if="$validation.isEmpty(moduleRecordList) && !showLoading"
        class="list-empty-state"
      >
        <inline-svg
          src="svgs/emptystate/workorder"
          iconClass="icon text-center icon-xxxxlg height-auto"
        ></inline-svg>
        <div class="line-height20 nowo-label">
          No
          {{ moduleDisplayName ? moduleDisplayName.toLowerCase() : moduleName }}
          available
        </div>
      </div>

      <template v-if="!showLoading && !$validation.isEmpty(moduleRecordList)">
        <el-table
          :data="moduleRecordList"
          ref="tableList"
          class="width100"
          height="100%"
          :fit="true"
        >
          <el-table-column fixed prop label="ID" min-width="100">
            <template v-slot="data">
              <div class="fc-id" @click="openRecordSummary(data.row.id)">
                {{ '#' + data.row.id }}
              </div>
            </template>
          </el-table-column>
          <el-table-column fixed prop label="NAME" min-width="200">
            <template v-slot="data">
              <div
                v-tippy
                @click="openRecordSummary(data.row.id)"
                small
                :title="data.row.name"
                class="flex-middle"
              >
                <div class="fw5 ellipsis textoverflow-ellipsis font-normal">
                  {{ data.row.name }}
                </div>
              </div>
            </template>
          </el-table-column>
          <template v-for="(field, index) in viewColumns">
            <el-table-column
              :align="checkDecimalType(field) ? 'right' : 'left'"
              :key="index"
              :prop="field.name"
              :label="field.displayName"
              min-width="200"
              v-if="!isFixedColumn(field.name) || field.parentField"
            >
              <template v-slot="data">
                <div v-if="!isFixedColumn(field.name) || field.parentField">
                  <div
                    class="table-subheading"
                    :class="{
                      'text-right': checkDecimalType(field),
                    }"
                  >
                    {{ getColumnDisplayValue(field, data.row) || '---' }}
                  </div>
                </div>
              </template>
            </el-table-column>
          </template>
        </el-table>
      </template>
    </div>
  </PageLayout>
</template>
<script>
import PageLayout from 'src/PortalTenant/components/PageLayout'
import Pagination from 'src/components/list/FPagination'
import ViewMixinHelper from '@/mixins/ViewMixin'
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import { findRouteForModule, pageTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import { isDecimalField } from '@facilio/utils/field'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import Tags from 'newapp/components/search/FTags'
import Sort from '../components/Sort'

export default {
  props: ['moduleName'],
  mixins: [ViewMixinHelper],
  components: { PageLayout, Pagination, AdvancedSearch, Tags, Sort },
  data() {
    return {
      moduleRecordList: [],
      listCount: null,
      loading: true,
      fetchingMore: false,
      columnConfig: {
        fixedColumns: ['id', 'name'],
        availableColumns: [],
        showLookupColumns: false,
      },
      tableLoading: false,
    }
  },
  computed: {
    ...mapState({
      viewLoading: state => state.view.isLoading,
      viewFields: state => {
        return state.view.currentViewDetail
          ? state.view.currentViewDetail.fields
          : []
      },
      metaInfo: state => state.view.metaInfo,
    }),
    moduleDisplayName() {
      return 'Control Groups'
    },
    currentViewFields() {
      return this.viewFields
    },
    showLoading() {
      return this.loading || this.viewLoading || this.tableLoading
    },
    openModuleId() {
      if (this.$attrs.id) {
        return parseInt(this.$attrs.id)
      }
      return -1
    },
    currentView() {
      return this.$attrs.viewname
    },
    page() {
      return this.$route.query.page || 1
    },
    title() {
      return this.moduleDisplayName
    },
    isV3Api() {
      return true
    },
    filters() {
      let {
        $route: { query },
      } = this
      let { search } = query || {}
      return search ? JSON.parse(search) : null
    },
  },
  watch: {
    moduleName: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal && !isEmpty(newVal)) {
          this.$store.dispatch('loadTicketStatus', this.moduleName)
          this.$store
            .dispatch('view/loadModuleMeta', this.moduleName)
            .catch(() => {})
        }
      },
      immediate: true,
    },
    currentViewFields() {
      this.tableLoading = true
      this.$nextTick(() => {
        this.$refs.tableList ? this.$refs.tableList.doLayout() : null
        this.tableLoading = false
      })
    },
    currentView: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.getViewDetail()
          this.loadRecords()
        }
      },
      immediate: true,
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadRecords()
      }
    },
    filters() {
      this.loadRecords()
    },
  },
  methods: {
    checkDecimalType(fieldObj) {
      let { field } = fieldObj || {}
      return !isEmpty(field) ? isDecimalField(field) : false
    },
    getViewDetail() {
      if (this.currentView) {
        this.$store.dispatch('view/loadViewDetail', {
          viewName: this.currentView,
          moduleName: this.moduleName,
        })
      }
    },
    async loadRecords(force = false) {
      let { filters, currentView, moduleName, page, includeParentFilter } = this
      if (isEmpty(currentView)) return

      this.loading = true
      let { list, meta: { pagination = {} } = {}, error } = await API.fetchAll(
        moduleName,
        {
          viewName: currentView,
          page,
          perPage: 50,
          withCount: true,
          filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
          includeParentFilter,
        },
        { force }
      )

      if (error) {
        this.$message.error(error.message || 'Could not fetch list')
      } else {
        this.moduleRecordList = list
        this.listCount = pagination.totalCount || null
      }

      this.loading = false
    },
    openRecordSummary(id) {
      let { moduleName } = this
      let route = findRouteForModule(moduleName, pageTypes.OVERVIEW)
      if (route) {
        this.$router.push({ name: route.name, params: { viewname: 'all', id } })
      } else {
        console.warn('Could not resolve route')
      }
    },
  },
}
</script>
