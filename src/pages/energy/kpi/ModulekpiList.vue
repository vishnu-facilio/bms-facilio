<template>
  <CommonListLayout
    moduleName="formulaField"
    :getPageTitle="() => 'Module KPI List'"
  >
    <template #views v-if="tabCheck()">
      <KpiViews></KpiViews>
    </template>
    <template #views v-else>
      <ReadingKpiViews></ReadingKpiViews>
    </template>
    <template #header>
      <template v-if="!showSearch && totalCount > 0">
        <pagination
          :total="totalCount"
          :perPage="perPage"
          class="pL15 fc-black-small-txt-12"
        ></pagination>

        <span class="separator">|</span>
      </template>

      <search
        :config="filterConfigData"
        :moduleName="filterConfigData.moduleName"
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

      <div v-if="moduleLoading" class="flex-middle fc-empty-white">
        <spinner :show="moduleLoading" size="80"></spinner>
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
        style="width: 100%"
        height="auto"
        row-class-name="tablerow-edit-delete-icon"
      >
        <el-table-column :label="$t('common._common.kpi_name')">
          <template v-slot="kpi" class="pL0">
            <div
              class="q-item-label"
              style="margin-top: 1px; letter-spacing: 0.3px;"
            >
              {{ kpi.row.name }}
            </div>
          </template>
        </el-table-column>

        <el-table-column :label="$t('setup.setupLabel.modules')">
          <template v-slot="scope">
            <span>{{ scope.row.moduleObj }}</span>
          </template>
        </el-table-column>

        <el-table-column :label="$t('common.tabs.metric')">
          <template v-slot="scope">
            <span>{{ scope.row.metricDisplayName }}</span>
          </template>
        </el-table-column>

        <el-table-column :label="$t('common._common.safe_limit')">
          <template v-slot="scope">
            <span>{{ scope.row.safeLimit }}</span>
          </template>
        </el-table-column>

        <el-table-column :label="$t('common.header.current_value')">
          <template v-slot="scope">
            <span>{{ scope.row.currentValue }}</span>
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
          @click="removeKpi('v2/kpi/module/delete')"
          >{{ $t('common._common.delete') }}</el-button
        >
      </div>
    </el-dialog>
  </CommonListLayout>
</template>

<script>
import ReadingKpiViews from 'pages/energy/readingkpi/ReadingKpiViews.vue'
import KpiList from './ReadingKpiTemplates'
import Pagination from '@/list/FPagination'
import { isEmpty, isArray, isNullOrUndefined } from '@facilio/utils/validation'
import { getModules } from './helpers/kpiConstants'
import CommonListLayout from 'newapp/list/DeprecatedCommonLayout'
import Search from 'newapp/components/Search'
import Tags from 'newapp/components/Tags'
import NewKpi from './components/AddModuleKpi'
import KpiViews from './components/kpiViews'
import { isWebTabsEnabled } from '@facilio/router'
import { API } from '@facilio/api'
import { mapState } from 'vuex'

export default {
  name: 'ModulekpiList',
  extends: KpiList,
  props: ['isOldKpi'],
  components: {
    Pagination,
    CommonListLayout,
    Search,
    Tags,
    NewKpi,
    KpiViews,
    ReadingKpiViews,
  },
  async created() {
    await this.loadModules()
    this.loadData()
  },

  data() {
    return {
      kpiList: [],
      moduleLoading: true,
      totalCount: null,
      perPage: 50,
      metrics: {},
      modulesObjList: [],
      filterConfigData: {
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
          siteId: {
            label: this.$t('common.products.site'),
            options: {},
            value: null,
            displayType: 'select',
            key: 'siteId',
          },
        },
        availableColumns: ['name', 'site'],
        fixedCols: ['name'],
        saveView: false,
      },
    }
  },
  watch: {
    page() {
      this.loadData()
    },
  },
  computed: {
    page() {
      return this.$route.query.page || 1
    },
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
    }),
  },

  methods: {
    tabCheck() {
      if (isWebTabsEnabled()) {
        const tabType = this.currentTab?.typeEnum ?? null
        if (!isEmpty(tabType) && tabType === 'KPI') {
          return true
        }
        return false
      }
      return this.isOldKpi
    },
    loadData() {
      if (isEmpty(this.modulesObjList)) return

      this.getCount()
      let url = this.getUrl()

      this.moduleLoading = true
      this.$http.get(url).then(response => {
        if (response.data.responseCode === 0) {
          let { kpis = [] } = response.data.result || {}
          this.kpiList = kpis || []

          if (!isEmpty(kpis)) {
            let promises = []
            this.kpiList.forEach(kpi => {
              let {
                target,
                minTarget,
                moduleName,
                metricId,
                metricName,
                currentValue,
              } = kpi

              if (!isEmpty(target) && !isEmpty(minTarget))
                kpi.safeLimit = `${minTarget} - ${target}`
              else if (!isEmpty(target)) kpi.safeLimit = `< ${target}`
              else if (!isEmpty(minTarget)) kpi.safeLimit = `> ${minTarget}`
              else kpi.safeLimit = '--'

              if (isNullOrUndefined(currentValue)) kpi.currentValue = '---'

              let moduleObj = this.modulesObjList[0].list.find(
                moduleList => moduleList.moduleName === moduleName
              )
              isEmpty(moduleObj)
                ? (moduleObj = this.modulesObjList[1].list.find(
                    moduleList => moduleList.moduleName === moduleName
                  ))
                : null
              kpi.moduleObj = moduleObj ? moduleObj.label : ''

              let promise = this.loadMetric(
                moduleName,
                metricId,
                metricName
              ).then(metric => this.$set(kpi, 'metricDisplayName', metric))
              promises.push(promise)
            })
            Promise.all(promises).then(() => (this.moduleLoading = false))
          } else this.moduleLoading = false
        } else {
          this.$message.error(response.data.message)
          this.moduleLoading = false
        }
      })
    },

    async loadModules() {
      let { error, data } = await API.get('/v3/modules/list/all')

      if (error) {
        this.$message.error(error.message)
      } else {
        let modulesListKey = {
          systemModules: 'System Modules',
          customModules: 'Custom Modules',
        }

        Object.entries(modulesListKey).forEach(([key, value]) => {
          let list = data[key].map(list => {
            return { moduleName: list.name, label: list.displayName }
          })

          if (key === 'systemModules') {
            list = [
              ...list,
              ...getModules().filter(
                module =>
                  this.$helpers.isLicenseEnabled(module.license) === true
              ),
            ]
          }

          this.modulesObjList.push({
            list,
            label: value,
          })
        })
      }
    },

    loadMetric(moduleName, metricId, metricName) {
      return new Promise(resolve => {
        this.getMetricList(this.metrics, moduleName).then(() => {
          let metric
          if (!isEmpty(metricName))
            metric = this.metrics[moduleName].find(
              metric => metric.name === metricName
            )
          else
            metric = this.metrics[moduleName].find(
              metric => metric.id === metricId
            )
          resolve(this.$getProperty(metric, 'displayName', '---'))
        })
      })
    },

    getMetricList(list, moduleName) {
      if (list[moduleName]) {
        if (isArray(list[moduleName])) {
          return new Promise(resolve => resolve(list[moduleName]))
        }
        return list[moduleName]
      } else {
        let url = `v2/kpi/module/metrics?moduleName=${moduleName}`
        let promise = this.$http.get(url).then(({ data }) => {
          if (data.responseCode === 0) {
            let { metrics } = data.result
            this.metrics[moduleName] = metrics
          }
        })
        this.metrics[moduleName] = promise
        return promise
      }
    },

    getUrl() {
      let url = `/v2/kpi/module/list?fetchCurrentValue=true&type=1&page=${this.page}&perPage=${this.perPage}&`
      let { filters } = this
      let filterObj = { ...filters }

      let parsedFilters
      if (!isEmpty(filterObj)) {
        if (filterObj.resource) delete filterObj.resource
        parsedFilters = encodeURIComponent(JSON.stringify(filterObj))
      }

      if (!isEmpty(parsedFilters)) url = `${url}filters=${parsedFilters}&`

      return url
    },

    getCount() {
      let url = 'v2/kpi/module/count'

      this.$http.get(url).then(({ data }) => {
        if (data.responseCode === 0) {
          let { count } = data.result
          this.totalCount = count
        }
      })
    },

    loadKPICount() {},
  },
}
</script>
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
