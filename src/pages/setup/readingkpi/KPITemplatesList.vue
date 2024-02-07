<template>
  <div class="kpi-list">
    <portal to="kpi-setup-tab-actions">
      <div class="search-and-pagination-container flex-middle">
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
    <div v-else-if="$validation.isEmpty(records) && !isLoading">
      <SetupEmpty>
        <template #emptyImage>
          <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
        </template>
        <template #emptyHeading>
          {{ $t('kpi.kpi.no_kpi_available') }}
        </template>
        <template #emptyDescription> </template>
      </SetupEmpty>
    </div>
    <el-table
      :data="records"
      class="width100 fc-setup-table fc-setup-table-th-borderTop"
      height="calc(100vh - 225px)"
    >
      <el-table-column
        prop="name"
        :label="$t('common.products._name')"
        width="200"
        fixed
      >
        <template v-slot="readingKpi">
          <div
            class="overflow-handle"
            v-tippy="{
              arrow: true,
              arrowType: 'round',
              animation: 'fade',
              distance: 0,
              interactive: true,
            }"
            :content="getKpiName(readingKpi.row)"
          >
            {{ getKpiName(readingKpi.row) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="assetCategory"
        :label="$t('asset.assets.asset_category')"
        width="200"
        fixed
      >
        <template v-slot="readingKpi">
          <div class="overflow-handle">
            {{ getAssetCategory(readingKpi.row) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="spaceOrAsset"
        :label="$t('common._common.space/asset')"
        width="200"
      >
        <template v-slot="readingKpi">
          <div class="overflow-handle">
            {{ getResourceName(readingKpi.row) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="frequency"
        :label="$t('asset.assets.asset_freq')"
        width="150"
      >
        <template v-slot="readingKpi">
          {{ getFrequency(readingKpi.row) }}
        </template>
      </el-table-column>
      <el-table-column
        prop="createdBy"
        :label="$t('custommodules.list.created_by')"
        width="150"
      >
        <template v-slot="readingKpi">
          <div
            class="overflow-handle"
            v-tippy="{
              arrow: true,
              arrowType: 'round',
              animation: 'fade',
              distance: 0,
              interactive: true,
            }"
            :content="getCreatedUserName(readingKpi.row)"
          >
            {{ getCreatedUserName(readingKpi.row) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="modifiedBy"
        :label="$t('common.failure_class.modified_by')"
        width="150"
      >
        <template v-slot="readingKpi">
          <div
            class="overflow-handle"
            v-tippy="{
              arrow: true,
              arrowType: 'round',
              animation: 'fade',
              distance: 0,
              interactive: true,
            }"
            :content="getModifiedUserName(readingKpi.row)"
          >
            {{ getModifiedUserName(readingKpi.row) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="status"
        :label="$t('alarm.alarm.alarm_status')"
        width="100"
      >
        <template v-slot="readingKpi">
          <el-switch
            v-model="readingKpi.row.status"
            @change="toggleStatus(readingKpi.row)"
            active-color="rgba(57, 178, 194, 0.8)"
            inactive-color="#e5e5e5"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column
        prop
        label
        class="visibility-visible-actions"
        width="110px"
        fixed="right"
      >
        <template v-slot="readingKpi">
          <div class="text-center template-actions content-between inline-flex">
            <i
              class="el-icon-edit edit-icon visibility-hide-actions pR15"
              data-arrow="true"
              :title="$t('common._common.edit')"
              v-tippy
              @click="editKpi(readingKpi.row)"
            ></i>
            <i
              class="el-icon-delete fc-delete-icon visibility-hide-actions pR10"
              data-arrow="true"
              :title="$t('common._common.delete')"
              v-tippy
              @click="deleteRecords([readingKpi.row.id])"
            ></i>
            <el-dropdown
              v-if="isActiveKpi(readingKpi) && !isDynamicKpi(readingKpi)"
              @command="handleDropDown($event, readingKpi.row)"
              trigger="click"
            >
              <span class="el-dropdown-link">
                <fc-icon
                  group="default"
                  name="ellipsis-vertical"
                  class="visibility-hide-actions ellipsis-icon"
                  size="16"
                ></fc-icon>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="runHistory">{{
                  $t('kpi.historical.run_historical')
                }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </template>
      </el-table-column>
    </el-table>
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
import { isEmpty } from '@facilio/utils/validation'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData.js'

const frequencies = {
  '1 Min': 1,
  '2 Mins': 2,
  '3 Mins': 3,
  '4 Mins': 4,
  '5 Mins': 5,
  '10 Mins': 6,
  '15 Mins': 7,
  '20 Mins': 8,
  '30 Mins': 9,
  '1 Hr': 10,
  '2 Hr': 11,
  '3 Hr': 12,
  '4 Hr': 13,
  '8 Hr': 14,
  '12 Hr': 15,
  '1 Day': 16,
  'Weekly ': 17,
  'Monthly ': 18,
  'Quarterly ': 19,
  'Half Yearly': 20,
  'Annually ': 21,
}

export default {
  components: {
    SetupLoader,
    SetupEmpty,
    Pagination,
    AdvancedSearch,
    FTags,
  },
  data() {
    return {
      showDialog: false,
      isLoading: false,
      records: [],
      kpiFilterList: [],
      selectedImpact: null,
      recordCount: null,
      page: 1,
      perPage: 50,
      frequencies: frequencies,
    }
  },
  async created() {
    await this.loadRecords({ force: true })
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    ...mapState({
      assetCategoryList: state => state.assetCategory,
    }),
    ...mapGetters(['getUser']),
    moduleName() {
      return 'readingkpi'
    },
    moduleDisplayName() {
      return 'ReadingKPI'
    },
    modelDataClass() {
      return CustomModuleData
    },
  },
  methods: {
    resetPage() {
      this.page = 1
    },
    resetFilters() {
      this.kpiFilterList = []
      this.resetPage()
      this.loadRecords({ filters: this.kpiFilterList })
    },
    async toggleStatus(kpiObj) {
      let { id, status } = kpiObj || {}
      let dataObj = {
        status: status,
      }
      let payload = {
        data: dataObj,
        moduleName: this.moduleName,
        id: id,
      }
      let { error } = await API.post('v3/modules/data/patch', payload)
      if (isEmpty(error)) {
        this.$message.success(this.$t('kpi.kpi.status_update_success'))
      } else {
        this.$message.error(
          error.message || this.$t('kpi.kpi.status_update_failed')
        )
      }
    },
    applyReadingKpiFilters({ filters }) {
      this.kpiFilterList = filters
      this.resetPage()
      this.loadRecords({ filters: JSON.stringify(this.kpiFilterList) })
    },
    async loadRecords(props) {
      try {
        let { filters, force } = props || {}
        let { moduleName, page, perPage } = this
        let params = {
          moduleName,
          page,
          perPage,
          force,
          filters,
          withCount: true,
          orderBy: 'SYS_CREATED_TIME',
          orderType: 'desc',
        }
        this.isLoading = true
        const { data, meta } = await API.get('v3/modules/data/list', params)
        const { readingkpi } = data || {}
        this.records = readingkpi
        const { pagination } = meta || {}
        const { totalCount } = pagination || {}
        this.recordCount = totalCount
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
      this.isLoading = false
    },
    editKpi(record) {
      let { id } = record || {}
      this.$router.push({
        name: 'edit-kpi',
        params: { id },
        query: this.$route.query,
      })
    },
    async loadNextPage(page) {
      this.page = page
      await this.loadRecords()
    },
    getKpiName(kpi) {
      return this.$getProperty(kpi, 'name', '---')
    },
    getResourceName(kpiObj) {
      let { ns, assetCategory, firstAssetName } = kpiObj || {}
      let includedAssetIds = this.$getProperty(ns, 'includedAssetIds', [])
      let { displayName } = assetCategory

      if (!isEmpty(includedAssetIds)) {
        let assetCategoryId = this.$getProperty(assetCategory, 'id')
        if (assetCategoryId > 0) {
          let message
          let assetCount = includedAssetIds.length

          if (!isEmpty(assetCount)) {
            if (assetCount === 1) {
              return firstAssetName
            }
            message = `${assetCount} ${displayName}s`
          }
          return message
        }

        if (includedAssetIds.length === 1) {
          return firstAssetName
        }
      } else if (!isEmpty(assetCategory)) {
        return `All  ${displayName}s`
      } else return '---'
    },
    getAssetCategory(record) {
      const { assetCategory } = record || {}
      return this.$getProperty(assetCategory, 'displayName', '---')
    },
    getFrequency(record) {
      let { frequencies } = this
      let { frequency, kpiType } = record
      let freq
      if (kpiType === 1 || kpiType === 2) {
        freq = Object.keys(frequencies).find(
          key => frequencies[key] === frequency
        )
      }
      if (!isEmpty(freq)) {
        return freq
      }
      return '---'
    },
    getStatus(record) {
      return this.$getProperty(record, 'status', '---')
    },
    getCreatedUserName(readingKpi) {
      let { sysCreatedBy } = readingKpi || {}
      return this.getUserName(sysCreatedBy)
    },
    getModifiedUserName(readingKpi) {
      let { sysModifiedBy } = readingKpi || {}
      return this.getUserName(sysModifiedBy)
    },
    getUserName(userObj) {
      if (!isEmpty(userObj)) {
        let { id } = userObj
        let currentUser = this.getUser(id)
        return currentUser.name === 'Unknown' ? '---' : currentUser.name
      }
      return '---'
    },
    async deleteRecords(idList) {
      let value = await this.$dialog.confirm({
        title: this.$t(`kpi.kpi.delete_kpi`),
        message: this.$t(`kpi.kpi.delete_confirm`),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })

      if (value) {
        let { moduleName } = this
        let { error } = await API.deleteRecord(moduleName, idList)
        if (error) {
          let { message } = error
          this.$message.error(message)
        } else {
          this.$message.success(this.$t(`kpi.kpi.delete_success`))
          this.loadRecords(true)
        }
      }
    },
    handleDropDown(command, record) {
      let { id } = record || {}
      if (command === 'runHistory') {
        this.$router.push({
          name: 'readingkpi.logs',
          params: {
            kpiId: id,
          },
        })
      }
    },
    isActiveKpi(record) {
      let { row: { status } = {} } = record || {}
      return status
    },
    isDynamicKpi(record) {
      let { row: { kpiType } = {} } = record || {}
      return kpiType === 3
    },
  },
}
</script>
<style lang="scss" scoped>
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
</style>
