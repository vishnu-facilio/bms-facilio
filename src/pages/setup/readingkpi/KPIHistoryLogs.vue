<template>
  <div class="kpi-history-log-page">
    <portal to="kpi-setup-tab-actions">
      <div class="search-and-pagination-container flex-middle">
        <div>
          <div
            class="fc-portal-filter-border"
            v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
            :content="$t('common._common.refresh')"
            @click="reloadLogs"
          >
            <i class="el-icon-refresh fwBold f16"></i>
          </div>
        </div>
        <span class="separator">|</span>
        <AdvancedSearch
          :key="`${moduleName}-search`"
          :moduleName="moduleName"
          :moduleDisplayName="moduleDisplayName"
          :hideQuery="true"
          :onSave="applyReadingKpiFilters"
          :filterList="kpiFilterList"
        >
        </AdvancedSearch>
        <span class="separator">|</span>
        <div class="pagination-container">
          <Pagination
            :currentPage.sync="page"
            :total="recordCount"
            :perPage="perPage"
            @update:currentPage="loadNextPage"
          ></Pagination>
        </div>
      </div>
    </portal>

    <FTags
      :key="`ftags-list-${moduleName}`"
      :filterList="kpiFilterList"
      :hideQuery="true"
      :hideSaveView="true"
      @updateFilters="applyReadingKpiFilters"
      @resetFilters="resetFilters"
      class="border-tlbr-none mT0 mR0 mL0 mb5"
    ></FTags>
    <SetupLoader v-if="isLoading">
      <template #setupLoading>
        <spinner :show="isLoading" size="80"></spinner>
      </template>
    </SetupLoader>
    <SetupEmpty v-else-if="$validation.isEmpty(records) && !isLoading">
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('kpi.historical.no_logs_available') }}
      </template>
      <template #emptyDescription> </template>
    </SetupEmpty>
    <div class="mB10 position-relative pB100 overflow-y-scroll">
      <el-row class="kpi-history-log-header">
        <el-col :span="5">
          <div class="history-log-header-txt">
            {{ $t('kpi.kpi.kpi_name') }}
          </div>
        </el-col>
        <el-col :span="3">
          <div class="history-log-header-txt">
            {{ $t('common.header.assets') }}
          </div>
        </el-col>
        <el-col :span="5">
          <div class="history-log-header-txt">
            {{ $t('rule.create.start_end_date') }}
          </div>
        </el-col>

        <el-col :span="4">
          <div class="history-log-header-txt">
            {{ $t('maintenance._workorder.status') }}
          </div>
        </el-col>
        <el-col :span="4">
          <div class="history-log-header-txt">
            {{ $t('setup.setupLabel.executed_by') }}
          </div>
        </el-col>
        <el-col :span="3">
          <div class="history-log-header-txt">
            {{ $t('setup.setupLabel.executed_time') }}
          </div>
        </el-col>
      </el-row>
      <!-- collapse Parent Log  -->
      <el-collapse
        v-model="activeNames"
        :accordion="true"
        @change="handleChange"
        class="kpi-history-logs-collapse"
      >
        <div class="collapse-scroll">
          <el-collapse-item
            v-for="(record, index) in records"
            :key="index"
            :name="index"
            :data="record"
          >
            <template slot="title">
              <el-row class="width100 pL30">
                <el-col :span="5">
                  <div class="flex-middle pT10">
                    <i class="el-icon-caret-right"></i>
                    <i class="el-icon-caret-bottom"></i>
                    <div
                      v-tippy="{
                        arrow: true,
                        arrowType: 'round',
                        animation: 'fade',
                        distance: 0,
                        interactive: true,
                      }"
                      :content="getRecordData(record, 'kpi.name')"
                      class="fc-black-com f13 line-height20 overflow-handle setup-dialog60 "
                    >
                      {{ getRecordData(record, 'kpi.name') }}
                    </div>
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="flex-middle pT10">
                    <div class="fc-black-com f13 line-height20">
                      {{ getResourceCount(record) }}
                      {{ $t('common.header.assets') }}
                    </div>
                  </div>
                </el-col>
                <el-col :span="5">
                  <div class="flex-middle">
                    <div class="fc-grey2 f13 line-height20 fw4">
                      {{ $t('rule.create.start') }}
                    </div>
                    <div class="fc-black-com f13 pL5 line-height20 fw4">
                      {{ getTimeDateFormat(record, 'startTime') }}
                      {{ getTimeFormat(record, 'startTime') }}
                    </div>
                  </div>
                  <div class="flex-middle">
                    <div class="fc-grey2 f13 line-height20 fw4">
                      {{ $t('rule.create.end') }}
                    </div>
                    <div class="fc-black-com f13 pL12 line-height20 fw4">
                      {{ getTimeDateFormat(record, 'endTime') }}
                      {{ getTimeFormat(record, 'endTime') }}
                    </div>
                  </div>
                </el-col>
                <el-col :span="4">
                  <div
                    class="fc-black-com f13 bold line-height20 flex-middle fw4 pT10"
                  >
                    <div>
                      {{ getSuccessCount(record) }} /
                      {{ record.resourceCount }}
                    </div>
                    <div
                      v-bind:style="{
                        color:
                          $constants.KPI_LOGGER_STATUS[record.status].color,
                      }"
                      :class="'f14 line-height20 fw4'"
                      class="pL10"
                    >
                      {{ $constants.KPI_LOGGER_STATUS[record.status].label }}
                    </div>
                  </div>
                </el-col>
                <el-col :span="4">
                  <div
                    class="fc-black-com f13 line-height20 fw4 overflow-handle pT10"
                    v-tippy="{
                      arrow: true,
                      arrowType: 'round',
                      animation: 'fade',
                      distance: 0,
                      interactive: true,
                    }"
                    :content="getCreatedUserName(record)"
                  >
                    {{ getCreatedUserName(record) }}
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="fc-black-com f13 line-height20 fw4 pT10">
                    {{ getTimeDateFormat(record, 'sysCreatedTime') }}
                    {{ getTimeFormat(record, 'sysCreatedTime') }}
                  </div>
                </el-col>
              </el-row>
            </template>
            <div class="resource-status-list">
              <el-row
                v-if="record.isLoading"
                class="kpi-history-collapse-child"
              >
                <spinner :show="record.isLoading" size="80"></spinner>
              </el-row>
              <!--First Level log resource List -->
              <el-row
                v-for="(child, childIndex) in childrenMap[record.id]"
                :key="childIndex"
                class="kpi-history-collapse-child"
              >
                <el-col :span="5">
                  <div class="hide-v">
                    {{ $t('rule.create.dont_remove') }}
                  </div>
                </el-col>
                <el-col :span="8" class>
                  <div class="flex-middle">
                    <div class="label-txt-black pL10">
                      {{ child.name }}
                    </div>
                  </div>
                </el-col>
                <el-col :span="4" class="text-left">
                  <div
                    :style="{
                      color: $constants.KPI_LOGGER_STATUS[child.status].color,
                    }"
                    class="f14 pL10"
                    v-tippy="{
                      arrow: true,
                      arrowType: 'round',
                      animation: 'fade',
                      maxWidth: '500',
                    }"
                    :content="getErrMsg(child)"
                  >
                    {{ $constants.KPI_LOGGER_STATUS[child.status].label }}
                  </div>
                </el-col>
                <el-col :span="4">
                  <div class="hide-v">
                    {{ $t('rule.create.dont_remove') }}
                  </div>
                </el-col>
                <el-col :span="3">
                  <div class="hide-v">
                    {{ $t('rule.create.dont_remove') }}
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-collapse-item>
        </div>
      </el-collapse>
    </div>
    <CreateKpiHistoricalDialog
      v-if="showHistoricalDialog"
      :kpiId="activeKpiId"
      @close="closeCreateDialog"
    ></CreateKpiHistoricalDialog>
  </div>
</template>

<script>
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import FTags from 'newapp/components/search/FTags'
import { API } from '@facilio/api'
import { mapState, mapGetters } from 'vuex'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData.js'
import CreateKpiHistoricalDialog from 'src/pages/setup/readingkpi/CreateKpiHistoricalForm.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['showHistoricalDialog'],

  components: {
    SetupLoader,
    SetupEmpty,
    Pagination,
    AdvancedSearch,
    FTags,
    CreateKpiHistoricalDialog,
  },
  data() {
    return {
      showDialog: false,
      isLoading: false,
      records: [],
      kpiFilterList: [],
      recordCount: null,
      page: 1,
      perPage: 20,
      childrenMap: {},
      activeNames: [],
      activeKpiId: null,
    }
  },
  async created() {
    this.setActiveKpi()
    await this.loadRecords({ force: true })
    this.$store.dispatch('view/loadModuleMeta', 'readingrule')
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    ...mapState({
      assetCategoryList: state => state.assetCategory,
    }),
    ...mapGetters(['getUser']),
    moduleName() {
      return 'kpiLogger'
    },
    moduleDisplayName() {
      return 'KPI Logger'
    },
    modelDataClass() {
      return CustomModuleData
    },
  },
  methods: {
    getSuccessCount(record) {
      let { successCount } = record || {}
      return isEmpty(successCount) ? 0 : successCount
    },
    getTimeDateFormat(record, timeProp) {
      return this.$helpers.formatDateFull(this.getRecordData(record, timeProp))
    },
    getTimeFormat(record, timeProp) {
      return this.$helpers.formatTimeFull(this.getRecordData(record, timeProp))
    },
    resetPage() {
      this.page = 1
    },
    resetFilters() {
      this.kpiFilterList = []
      this.resetPage()
      this.loadRecords({ filters: this.kpiFilterList })
    },
    applyReadingKpiFilters({ filters }) {
      this.kpiFilterList = filters
      this.resetPage()
      this.loadRecords()
    },
    async loadRecords(props) {
      try {
        let { force } = props || {}
        let { moduleName, page, perPage, kpiFilterList } = this
        let params = {
          moduleName,
          page,
          perPage,
          force,
          filters: JSON.stringify({
            isSysCreated: { operatorId: 15, value: ['false'] },
            ...kpiFilterList,
          }),
          withCount: true,
          orderBy: 'SYS_CREATED_TIME',
          orderType: 'desc',
        }
        this.isLoading = true
        const { data, meta } = await API.get('v3/modules/data/list', params)
        const { kpiLogger } = data || {}
        this.records = kpiLogger
        const { pagination } = meta || {}
        const { totalCount } = pagination || {}
        this.recordCount = totalCount
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
      this.isLoading = false
    },
    reloadLogs() {
      this.loadRecords()
    },
    async loadNextPage(page) {
      this.page = page
      await this.loadRecords()
    },
    getRecordData(record, property) {
      return this.$getProperty(record, `${property}`, '---')
    },
    getResourceCount(record) {
      let { resourceCount } = record || {}
      return resourceCount
    },
    getErrMsg(child) {
      let { errMsg } = child || {}
      return isEmpty(errMsg) ? 'Success' : `(${errMsg})`
    },
    handleChange(index) {
      let { records } = this
      let currentParent = records[index]
      let { id: parentId } = currentParent || {}
      if (!isEmpty(parentId)) {
        if (isEmpty(this.childrenMap[parentId])) {
          this.handleNodeClick(currentParent)
        }
      }
    },
    async handleNodeClick(parentLogger) {
      let { id } = parentLogger || {}
      let url = '/v3/readingKpi/historical/fetchAssetDetails'
      this.$set(parentLogger, 'isLoading', true)
      let { data, error } = await API.get(url, { parentLoggerId: id })
      if (error) {
        let { message } = error
        this.$message.error(message || 'Error Occured')
      } else {
        this.$set(parentLogger, 'isLoading', false)
        this.childrenMap[id] = Object.assign(
          this.$getProperty(data, 'assetList', [])
        )
      }
    },
    closeCreateDialog() {
      this.activeKpiId = null
      this.$emit('closeCreateDialog')
    },
    setActiveKpi() {
      let { $route } = this
      let { params } = $route || {}
      let { kpiId } = params || {}
      if (!isEmpty(kpiId)) {
        this.activeKpiId = kpiId
        this.$emit('openCreateDialog')
      }
    },
    getCreatedUserName(readingKpi) {
      let { sysCreatedBy } = readingKpi || {}
      return this.getUserName(sysCreatedBy)
    },
    getUserName(userObj) {
      if (!isEmpty(userObj)) {
        let { id } = userObj ?? {}
        let currentUser = this.getUser(id)
        let { name } = currentUser ?? {}
        return name === 'Unknown' ? '---' : name
      }
      return '---'
    },
  },
}
</script>
<style lang="scss">
.kpi-history-logs-collapse {
  width: 100%;
  .el-collapse-item {
    .el-collapse-item__header {
      height: 70px;
      border-bottom: 1px solid #f2f5f6;
    }
    .el-collapse-item__content {
      padding-bottom: 0px !important;
    }
    .el-collapse-item__arrow {
      visibility: hidden;
    }
  }
}
</style>
<style lang="scss" scoped>
.kpi-history-log-header {
  padding: 20px 30px;
  border-bottom: solid 1px #d0d9e2;
  background: white;
}
.kpi-history-collapse-child {
  width: 100%;
  padding: 0px 30px;
  border-bottom: 1px solid #f3f3f3;
  display: flex;
  align-items: center;
  flex-direction: row;
  height: 60px;
  background: #fafafa;
  .label-txt-black {
    line-height: 20px;
  }
}
.kpi-history-log-page .el-collapse-item .el-icon-caret-right {
  color: #324056;
  font-size: 14px;
  font-weight: bold;
  visibility: visible;
  transition: all 0.3s ease;
  background: #fff;
}
.kpi-history-log-page .el-collapse-item .is-active .el-icon-caret-right {
  visibility: hidden;
}
.kpi-history-log-page .el-collapse-item .is-active .el-icon-caret-bottom {
  visibility: visible;
  color: #324056;
  font-size: 14px;
  font-weight: bold;
  transition: all 0.3s ease;
  position: relative;
  right: 13px;
  background: #fff;
  bottom: 1px;
}
.kpi-history-log-page .el-collapse-item .el-icon-caret-bottom {
  visibility: hidden;
}
.collapse-scroll {
  overflow-y: scroll;
  height: calc(100vh - 200px);
}
.kpi-user-column {
  display: flex;
  align-items: center;
}
.overflow-handle {
  word-break: normal;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}
.resource-status-list {
  overflow-y: scroll;
  max-height: 500px;
}
</style>
