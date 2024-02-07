<template>
  <div class="height100 controlpointlist-page">
    <div class="position-relative" style="z-index: 600;">
      <portal to="controlpagenation" key="controllistpage" slim>
        <el-tooltip
          effect="dark"
          :content="$t('common._common.search')"
          placement="left"
        >
          <div>
            <i
              class="el-icon-search control-point-search fc-black-2 contr f16 pointer fw-bold mR10"
              @click.stop="toggleQuickSearch()"
            ></i>
          </div>
        </el-tooltip>
        <pagination
          :perPage="perPage"
          :total="listCount"
          style="margin-top: 14px;"
        ></pagination>
      </portal>
      <div
        class="fc-black-small-txt-12 fc-subheader-right-search controlpoints-search fR"
        v-show="showQuickSearch"
      >
        <new-search
          :config="filterConfig"
          @hideSearch="showQuickSearch = false"
          :showSearch="showQuickSearch"
          :defaultFilter="defaultFilter"
        >
        </new-search>
        <div class="filter-search-close"></div>
      </div>
      <div
        v-if="appliedFilters !== null && showtag"
        class="fL"
        style="width: 84%;"
      >
        <new-tag
          :config="filterConfig"
          :filters="appliedFilters"
          specialOperatorHandling="true"
          :showFilterAdd="showAddFilter"
          :showCloseIcon="true"
          class="layout-new-tag control-points-layout-new-tag"
        ></new-tag>
        <div
          style="top: 13px !important;"
          class="clear-filter pointer control-clear-btn"
          v-if="Object.keys(appliedFilters).length > '0'"
          @click="resetFilters()"
        >
          {{ $t('common._common.clear_all_filters') }}
        </div>
      </div>
    </div>
    <div
      class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100 block"
      v-if="openControlId === -1"
      :class="$route.query.search ? 'fc-list-table-search-scroll' : ''"
    >
      <div v-if="loading" class="flex-middle fc-empty-white">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div v-else>
        <el-table
          :data="controlpointsload"
          width="100%"
          height="auto"
          type="index"
          :index="indexMethod"
          :fit="true"
        >
          <template slot="empty">
            <img
              class="mT50"
              src="~statics/noData-light.png"
              width="100"
              height="100"
            />
            <div class="mT10 label-txt-black f14 op6">
              {{ $t('common.products.no_control_points_available') }}
            </div>
          </template>
          <el-table-column
            fixed
            :label="$t('common._common._asset')"
            min-width="260"
            prop=""
          >
            <template v-slot="scope">
              <div
                class="max-width300px textoverflow-ellipsis"
                :title="scope.row.resourceContext.name"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
              >
                {{ scope.row.resourceContext.name }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            fixed
            :label="$t('common._common._asset_category')"
            min-width="200"
            prop=""
          >
            <template v-slot="scope">
              {{ scope.row.resourceContext.category.displayName }}
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('common.tabs._reading')"
            width="200"
            prop=""
          >
            <template v-slot="scope">
              <div
                class="textoverflow-ellipsis"
                style="max-width: 200px;"
                :title="scope.row.field.displayName"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
              >
                {{ scope.row.field.displayName }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('common.header.last_recorded_value')"
            width="210"
            prop=""
          >
            <template v-slot="scope">
              {{ Math.round(scope.row.value).toFixed(1)
              }}{{ scope.row.field.unit }}
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('common.header.last_recorded_time')"
            width="200"
            prop=""
          >
            <template v-slot="scope">
              {{ scope.row.ttime | formatDate() }}
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('common._common._is_sandbox')"
            width="150"
            prop=""
          >
            <template v-slot="scope">
              <el-switch
                v-model="scope.row.controlActionMode"
                @change="changestatus(scope.row, scope.row.controlActionMode)"
                :active-value="1"
                :inactive-value="2"
                class="Notification-toggle"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#e5e5e5"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column
            label=""
            width="180"
            prop=""
            class="visibility-visible-actions"
          >
            <template v-slot="scope">
              <button
                class="fc__border__btn visibility-hide-actions"
                style="padding: 5px 16px;"
                v-if="canShowCommandButton"
                @click="setdialogShow(scope.row)"
              >
                {{ $t('common.header._command') }}
              </button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <el-dialog
      :title="$t('common.header.command_value')"
      :visible.sync="controlPopup.visible"
      width="30%"
      :before-close="handleClose"
      class="setpoint-dialog fc-dialog-center-container fc-dialog-center-container"
    >
      <div>
        <div class="fc-input-label-txt">{{ $t('common.header.command') }}</div>
        <el-input
          :placeholder="$t('common._common.please_enter_value')"
          v-model="controlPopup.setVal"
          type="number"
          class="fc-input-full-border2 width100"
        ></el-input>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          @click="controlPopup.visible = false"
          class="modal-btn-cancel"
          >{{ $t('common._common.cancel') }}</el-button
        >
        <el-button
          type="primary"
          @click="saveReading"
          :loading="controlPopup.saving"
          class="modal-btn-save"
          >{{
            controlPopup.saving
              ? $t('common.products.setting')
              : $t('common.header.command')
          }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { isWebTabsEnabled, findTab, tabTypes } from '@facilio/router'
import Pagination from '@/list/FPagination'
import NewSearch from '@/NewSearch'
import { mapState, mapGetters } from 'vuex'
import NewTag from '@/NewTag'
export default {
  data() {
    return {
      showAddFilter: false,
      showtag: false,
      defaultFilter: 'fieldId',
      listCount: '',
      filterConfig: {
        includeParentCriteria: true,
        path: '/app/co/cp/',
        data: {
          fieldId: {
            label: this.$t('common.tabs.reading'),
            displayType: 'select',
            options: {},
            value: [],
          },
          asset: {
            label: this.$t('common._common.asset'),
            displayType: 'select',
            options: {},
            value: [],
            key: 'resourceId',
          },
          controlActionMode: {
            label: this.$t('common._common._is_sandbox'),
            displayType: 'select',
            options: { 1: 'Yes', 2: 'No' },
            operatorId: 9,
            value: '',
          },
          ttime: {
            label: this.$t('common.header._last_recorded_time'),
            displayType: 'select',
            single: true,
            type: 'date',
            customdate: true,
            options: {
              '1_40': 'Within 1 Hour',
              '12_40': 'Within 12 Hours',
              '24_40': 'Within 24 Hours',
              22: 'Today',
              25: 'Yesterday',
              31: 'This Week',
              28: 'This Month',
              27: 'Last Month',
              '2_39': 'Last 2 Months',
              '6_39': 'Last 6 Months',
            },
            value: '',
          },
        },
      },
      controlpointsload: [],
      showQuickSearch: false,
      loading: true,
      totalCount: null,
      tableData: [],
      dialogVisible: false,
      newReadingValue: null,
      selectedScope: null,
      perPage: 50,
      controlPopup: {
        visible: false,
        controlConfig: {
          resourceId: null,
          fieldId: null,
          value: null,
        },
        setVal: null,
        saving: false,
      },
    }
  },
  computed: {
    ...mapState({
      controls: state => state.control.controls,
    }),
    ...mapGetters({
      getTabByTabId: 'webtabs/getTabByTabId',
      tabHasPermission: 'webtabs/tabHasPermission',
    }),
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
    openControlId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    appliedFilters() {
      let {
        $route: { query },
      } = this
      let { search } = query || {}
      return search ? JSON.parse(search) : null
    },
    canShowCommandButton() {
      let canShowCommand = false
      let moduleName = 'asset'

      if (isWebTabsEnabled()) {
        let { tabId } = findTab(tabTypes.MODULE, {
          moduleName,
        })
        if (!isEmpty(tabId)) {
          let assetWebTab = this.getTabByTabId(tabId)
          canShowCommand = this.tabHasPermission('CONTROL', assetWebTab)
        }
      } else {
        canShowCommand = this.$hasPermission('asset:CONTROL')
      }
      return canShowCommand
    },
  },
  mounted: function() {
    this.loadControlPoints()
    this.loadCount()
    this.loadControlPointsCount()
    this.setOptionsForAssets()
  },
  watch: {
    page() {
      this.loadControlPoints()
    },
    filters: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.loadControlPoints()
          this.loadControlPointsCount()
        }
      },
    },
    appliedFilters: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          let {
            $route: { query },
          } = this
          let { search } = query || {}
          this.$router.push({
            query: {
              search: search,
              includeParentFilter: 'true',
            },
          })
        }
      },
    },
  },
  components: {
    Pagination,
    NewSearch,
    NewTag,
  },
  methods: {
    setOptionsForAssets() {
      this.showtag = false
      let url = `v2/controlAction/getControllableAssets`
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          if (
            response.data &&
            response.data.result &&
            response.data.result.controllableResources
          ) {
            console.log('********', response.data.result.controllableResources)
            response.data.result.controllableResources.forEach(asset => {
              this.filterConfig.data.resourceId.options[asset.id] = asset.name
            })
          }
          this.setOptionsForResources()
        }
      })
    },
    setOptionsForResources() {
      let url = `v2/controlAction/getControllableFields`
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          if (
            response.data &&
            response.data.result &&
            response.data.result.controllableFields
          ) {
            console.log('********', response.data.result.controllableFields)
            response.data.result.controllableFields.forEach(field => {
              this.filterConfig.data.fieldId.options[field.id] =
                field.displayName
            })
            this.showtag = true
          }
        }
      })
    },
    toggleQuickSearch() {
      this.showQuickSearch = !this.showQuickSearch
    },
    resetFilters() {
      this.$router.push({
        path: '/app/co/cp/controlpoints',
      })
    },
    loadControlPointsCount() {
      let queryObj = {
        filters: this.filters,
        includeParentFilter: true,
        fetchCount: true,
      }
      let self = this
      let url = 'v2/controlAction/getControllablePoints'
      let params
      params = 'fetchCount=' + queryObj.fetchCount
      if (queryObj.filters) {
        params =
          params +
          '&filters=' +
          encodeURIComponent(JSON.stringify(queryObj.filters))
      }
      if (queryObj.includeParentFilter) {
        params = params + '&includeParentFilter=' + queryObj.includeParentFilter
      }
      url = url + '?' + params
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          this.listCount = response.data.result.readingDataMetaCount
            ? response.data.result.readingDataMetaCount
            : ''
        }
      })
    },
    loadControlPoints() {
      let { filters } = this
      let self = this
      self.loading = true
      let url = `v2/controlAction/getControllablePoints?page=${this.page}&perPage=${this.perPage}`
      if (filters) {
        url += `&filters=${encodeURIComponent(
          JSON.stringify(filters)
        )}&includeParentFilter=true`
      }
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          this.controlpointsload = response.data.result.controllablePoints
            ? response.data.result.controllablePoints
            : []
          this.loading = false
        }
      })
    },
    handleClose() {
      this.controlPopup.visible = false
    },
    indexMethod(index) {
      return index * 2
    },
    setdialogShow(scopeRow) {
      this.selectedScope = scopeRow
      this.controlPopup.visible = true
    },
    loadCount() {
      this.$http
        .get(
          `/v2/controlAction/getControllablePoints?viewName=all&fetchCount=true`
        )
        .then(response => {
          if (response.data.responseCode === 0) {
            this.totalCount = response.data.result.count
          } else {
            this.totalCount = null
          }
        })
        .catch(() => (this.totalCount = null))
    },
    changestatus(scopeRow, controlActionMode) {
      let jsonParams = {
        rdm: {
          resourceId: scopeRow.resourceId,
          fieldId: scopeRow.fieldId,
          controlActionMode: controlActionMode,
        },
      }
      let self = this
      self.$http
        .post(`/v2/controlAction/updateRDM`, jsonParams)
        .then(response => {
          let res = response.data.result
          if (res && res.rdm) {
            self.$message.success(
              'Switched to ' +
                (res.rdm.controlActionMode === 1 ? 'Sand Box' : 'Live') +
                ' status successfully.'
            )
          }
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    saveReading() {
      this.controlPopup.saving = true
      this.$util.setReadingValue(
        this.selectedScope.resourceId,
        this.selectedScope.fieldId,
        this.controlPopup.setVal,
        null,
        null
      )
      this.controlPopup.saving = false
      this.controlPopup.visible = false
    },
  },
}
</script>
<style lang="scss">
.controlpointlist-page {
}
.control-point-search {
  position: absolute !important;
  right: 10px !important;
  top: 21px;
}
.controlpoints-search {
  position: absolute !important;
  top: -58px !important;
  right: 15px !important;
  z-index: 500 !important;
  background: #fff !important;
}

.setpoint-dialog .el-dialog__body {
  height: 200px;
}
.control-logic-clear-filter {
  width: 17%;
  position: absolute;
  top: 14px;
  right: 22px;
  text-align: right;
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #748893;
  float: right;
  margin-left: 10px;
  margin-top: 5px;
  cursor: pointer;
  z-index: 123;
}
.control-points-layout-new-tag {
  margin-top: 10px !important;
  margin-bottom: 10px !important;
}
.control-clear-btn {
  top: 13px !important;
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #748893;
  float: right;
  margin-left: 10px;
  margin-top: 5px;
  cursor: pointer;
  width: 17%;
  position: absolute;
  right: 22px;
  text-align: right;
}
</style>
