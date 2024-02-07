<template>
  <CommonListLayout
    moduleName="formulaField"
    :getPageTitle="() => 'Reading KPI List'"
  >
    <template #views>
      <KpiViews></KpiViews>
    </template>

    <template #header>
      <template v-if="!showSearch && totalCount > 0">
        <pagination
          :total="totalCount"
          :perPage="50"
          class="pL15 fc-black-small-txt-12"
        ></pagination>

        <span class="separator">|</span>
      </template>

      <search
        v-if="!isFilterOptionsLoading"
        :config="filterConfig"
        :moduleName="filterConfig.moduleName"
        defaultFilter="name"
        :includeAllMetaFields="false"
      ></search>

      <template v-if="!showSearch">
        <span class="separator">|</span>

        <div class="pL10">
          <button
            class="fc-create-btn outline-none"
            style="margin-top: -10px;"
            @click="createKpi()"
          >
            {{ $t('common.products.new_kpi') }}
          </button>
        </div>
      </template>
    </template>
    <div
      class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
    >
      <div>
        <tags :disableSaveFilters="true"></tags>
      </div>
      <div v-if="loading" class="flex-middle fc-empty-white">
        <spinner :show="loading" size="80"></spinner>
      </div>

      <div
        v-else-if="$validation.isEmpty(kpiList)"
        class="width100 text-center empty-container pB30 pT30"
      >
        <inline-svg
          src="svgs/emptystate/kpi"
          class="vertical-middle"
          iconClass="icon icon-256"
        ></inline-svg>
        <div class="f18 bold" style="margin-bottom: 7px;">
          {{ $t('common.products.no_kpis_found') }}
        </div>
        <div class="f12 grey-text2">
          {{ $t('common.header.you_dont_seem_to_have_kpis_created_view') }}
        </div>
      </div>

      <el-table
        v-else
        key="KPIlist"
        :data="kpiList"
        class="kpi-list fc-list-eltable"
        height="auto"
        style="width: 100%"
        row-class-name="tablerow-edit-delete-icon"
      >
        <el-table-column :label="$t('common.products._name')">
          <template v-slot="kpi" class="pL0">
            <div
              @click="opensummary(kpi.row.id)"
              class="q-item-label"
              style="margin-top: 1px; letter-spacing: 0.3px;"
            >
              {{ kpi.row.name }}
            </div>
          </template>
        </el-table-column>

        <el-table-column :label="$t('common._common.category')">
          <template v-slot="kpi">
            <span>{{
              getKpiCategoryById(kpi.row.kpiCategory)
                ? getKpiCategoryById(kpi.row.kpiCategory).name
                : '---'
            }}</span>
          </template>
        </el-table-column>

        <el-table-column :label="$t('common._common._space_asset')">
          <template v-slot="scope">
            <span>{{
              getResourceName(scope.row) ? getResourceName(scope.row) : ''
            }}</span>
          </template>
        </el-table-column>

        <el-table-column :label="$t('common._common.frequency')">
          <template v-slot="scope">
            <span>{{
              scope.row.frequency ? frequencyTypes[scope.row.frequency] : '---'
            }}</span>
          </template>
        </el-table-column>

        <el-table-column :label="$t('common.products.status')">
          <template v-slot="kpi">
            <el-switch
              v-model="kpi.row.active"
              @change="toggleStatus(kpi.row)"
              active-color="rgba(57, 178, 194, 0.8)"
              inactive-color="#e5e5e5"
            ></el-switch>
          </template>
        </el-table-column>

        <el-table-column width="150">
          <template v-slot="kpi">
            <div class="d-flex justify-content-center">
              <span
                v-tippy
                :title="$t('common._common.edit')"
                @click="editKpi(kpi.row)"
                class="hover-actions-view outline-none"
              >
                <inline-svg
                  src="svgs/edit"
                  class="vertical-middle"
                  iconClass="icon icon-sm mR25 icon-edit"
                ></inline-svg>
              </span>
              <span
                v-tippy
                :title="$t('common._common.delete')"
                @click="openDeleteDialog(kpi.row.id)"
                class="hover-actions-view"
              >
                <inline-svg
                  src="svgs/delete"
                  class="vertical-middle"
                  iconClass="icon icon-sm icon-remove"
                ></inline-svg>
              </span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <new-kpi
      v-if="showCreationForm"
      :isNew="isNew"
      :kpi="selectedKPI"
      @onSave="loadData()"
      @onClose="showCreationForm = false"
    ></new-kpi>

    <el-dialog
      :visible="canShowDialog"
      width="30%"
      title="Warning"
      class="fieldchange-Dialog pB15 fc-dialog-center-container kpi-list"
      custom-class="dialog-header-padding"
      :append-to-body="true"
      :show-close="false"
      :before-close="() => (canShowDialog = false)"
    >
      <div
        class="pB40 pT10 break-word height180"
        v-html="$t('kpi.kpi.warning_message')"
      ></div>
      <div class="modal-dialog-footer">
        <el-button @click="canShowDialog = false" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          type="primary"
          class="modal-btn-save delete-dialog"
          @click="removeKpi('/v2/kpi/delete')"
          >{{ $t('common._common.delete') }}</el-button
        >
      </div>
    </el-dialog>
  </CommonListLayout>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapState, mapGetters } from 'vuex'
import Pagination from '@/list/FPagination'
import CommonListLayout from 'newapp/list/DeprecatedCommonLayout'
import Search from 'newapp/components/Search'
import Tags from 'newapp/components/Tags'
import NewKpi from 'pages/energy/kpi/components/AddKpi'
import KpiViews from './components/kpiViews'
import { getFieldOptions } from 'util/picklist'
import { API } from '@facilio/api'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'

export default {
  name: 'KPIList',
  components: { Pagination, CommonListLayout, Search, Tags, NewKpi, KpiViews },
  data() {
    return {
      kpiList: [],
      perPage: 50,
      loading: true,
      isNew: false,
      selectedKPI: null,
      totalCount: null,
      showCreationForm: false,
      isFilterOptionsLoading: true,
      kpiIdToDelete: null,
      canShowDialog: false,
      filterConfig: {
        moduleName: 'formulaField',
        path: `/app/em/kpi/`,
        includeParentCriteria: true,
        excludeModuleStateField: true,
        data: {
          name: {
            label: this.$t('common.products.name'),
            displayType: 'string',
            value: '',
          },
          kpiCategory: {
            label: this.$t('common._common.category'),
            displayType: 'select',
            options: {},
            value: null,
          },
          siteId: {
            label: this.$t('common.products.site'),
            options: {},
            value: null,
            displayType: 'select',
            key: 'siteId',
          },
          assetCategoryId: {
            label: this.$t('common._common.asset_category'),
            displayType: 'select',
            options: {},
            value: null,
          },
        },
        availableColumns: ['name', 'kpiCategory', 'siteId', 'assetCategoryId'],
        fixedCols: ['name'],
        saveView: false,
      },
    }
  },

  async created() {
    await this.$store.dispatch('loadKpiCategories')
    await this.init()
    await this.loadFilterOptions()
  },

  computed: {
    ...mapState({
      showSearch: state => state.search.active,
    }),
    ...mapGetters({
      getKpiCategoryById: 'getKpiCategoryById',
    }),
    assetCategoryList() {
      return this.$store.state.assetCategory
    },
    frequencyTypes() {
      let types = this.$helpers.cloneObject(this.$constants.FACILIO_FREQUENCY)
      types['8'] = 'Hourly'
      return types
    },
    page() {
      return this.$route.query.page || 1
    },
    filters() {
      let { $route } = this
      let {
        query: { search },
      } = $route || {}

      if (!isEmpty(search)) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
  },
  watch: {
    filters: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.init()
        }
      },
    },
    page() {
      this.init()
    },
  },
  methods: {
    async init() {
      this.loading = true
      await this.loadData()
      await this.loadKPICount()
      this.loading = false
    },
    loadData() {
      let url = `/v2/kpi/list?type=1&page=${this.page}&perPage=50&`
      let { filters } = this
      let filterObj = { ...filters }
      let { resource } = filterObj
      let params = {}

      let parsedFilters
      if (!isEmpty(filterObj)) {
        if (filterObj.resource) delete filterObj.resource
        parsedFilters = encodeURIComponent(JSON.stringify(filterObj))
      }

      if (!isEmpty(parsedFilters)) url = `${url}filters=${parsedFilters}&`

      if (!isEmpty(resource)) params.resource = resource[0].value

      url = Object.keys(params).reduce((res, key) => {
        res += key + '=' + params[key] + '&'
        return res
      }, url)

      return this.$http.get(url).then(({ data }) => {
        if (data.responseCode === 0) {
          this.kpiList = data.result.formulaList
        }
      })
    },
    loadKPICount() {
      let url = '/v2/kpi/count?type=1&fetchCount=true'

      if (!isEmpty(this.page)) {
        url += `&page=${this.page}&perPage=${this.perPage}`
      }

      return this.$http
        .get(url)
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.totalCount = data.result.count
          }
        })
        .catch(() => {
          this.totalCount = null
        })
    },
    async loadFilterOptions() {
      this.isFilterOptionsLoading = true

      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'kpiCategory', skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$set(this.filterConfig.data, 'kpiCategory', {
          ...this.filterConfig.data.kpiCategory,
          options,
        })
        this.isFilterOptionsLoading = false
      }
    },
    onClick(row, col) {
      if (col.label) {
        this.selectedKPI = row
        this.opensummary(row.id)
      }
    },
    opensummary(id) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.READING_KPI_SUMMARY) || {}
        this.$router.push({
          name,
          params: {
            id,
          },
          query: this.$route.query,
        })
      } else {
        this.$router.push({
          path: '/app/em/kpi/reading/templates/' + id + '/overview',
          query: this.$route.query,
        })
      }
    },
    getResourceName(KPI) {
      if (!isEmpty(KPI.matchedResourcesIds)) {
        if (KPI.assetCategoryId > 0) {
          let message
          let selectedCount
          let categoryName = this.getCategoryName(KPI.assetCategoryId)

          if (KPI.includedResources && KPI.includedResources.length) {
            selectedCount = KPI.includedResources.length
          }

          if (selectedCount) {
            if (selectedCount === 1) {
              return KPI.matchedResources[0].name
            }
            message = selectedCount + ' ' + categoryName + 's'
          } else {
            message = 'All ' + categoryName + 's'
          }
          return message
        }

        let firstResource =
          KPI.matchedResources[Object.keys(KPI.matchedResources)[0]]

        if (KPI.matchedResources.length === 1) {
          return firstResource.name
        }
        return firstResource.resourceType === 1 ? 'Some Spaces' : 'Some Assets'
      }
      return '---'
    },

    getCategoryName(categoryId) {
      if (categoryId > 0 && this.assetCategoryList) {
        let category = this.assetCategoryList.find(
          category => category.id === categoryId
        )
        if (category) {
          return category.name
        }
      }
    },

    createKpi() {
      this.isNew = true
      this.selectedKPI = null
      this.showCreationForm = true
    },

    editKpi(kpi) {
      if (kpi.siteId === -1) {
        kpi.siteId = null
      }
      this.isNew = false
      this.selectedKPI = kpi
      this.showCreationForm = true
    },

    openDeleteDialog(kpiId) {
      this.kpiIdToDelete = kpiId
      this.canShowDialog = true
    },

    removeKpi(url) {
      this.$http
        .post(url, { id: this.kpiIdToDelete })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.$message.success(
              this.$t('common.header.kpi_deleted_successfully')
            )
            this.loadData()
          } else {
            this.$message.error(
              this.$t('common.header.kpi_could_not_be_deleted')
            )
          }
        })
        .finally(() => {
          this.kpiIdToDelete = null
          this.canShowDialog = false
        })
    },

    async toggleStatus(kpiObj) {
      let { active, id } = kpiObj || {}
      let { error } = await API.post('v2/kpi/status', {
        status: active,
        ruleId: id,
      })

      if (error) {
        this.$message.error(error.message || 'Status Update Failed')
        let index = this.kpiList.findIndex(kpi => id === kpi.id)
        if (index !== -1) {
          this.$set(this.kpiList, index, { ...kpiObj, active: !active })
        }
      } else {
        this.$message.success('Status Updated Successfully')
      }
    },
  },
}
</script>
<style lang="scss">
.kpi-list {
  .el-table__body-wrapper {
    height: 100vh;
    overflow-y: scroll;
  }
  .el-dialog__header {
    border-bottom: none;
  }
}
</style>

<style scoped>
li {
  float: left;
  padding: 0px 15px;
  list-style-type: none;
  border-right: 1px solid #e5e4e4;
}
li.active a {
  font-weight: 500;
}
li.active .sh-selection-bar {
  border-right: 0px solid #e0e0e0;
  border-left: 0px solid #e0e0e0;
  border-color: var(--fc-theme-color);
}
li .sh-selection-bar {
  border: 1px solid transparent;
  width: 25px;
  margin-top: 7px;
  position: absolute;
}
li:last-of-type {
  border-right: 0px;
}
a {
  color: #50516c;
}
a:hover {
  color: #50516c;
}
.modal-btn-save.delete-dialog {
  background-color: #ec7c7c;
  font-weight: 600;
}
.modal-btn-save:hover.delete-dialog {
  background-color: #ec7c7c !important;
}
</style>
