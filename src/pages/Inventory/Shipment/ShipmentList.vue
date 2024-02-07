<template>
  <div class="height100 schedule-list-container" v-if="isSummaryOpen">
    <div v-if="showLoading" class="flex-middle fc-empty-white">
      <spinner :show="showLoading" size="80"></spinner>
    </div>
    <div v-else class="flex flex-row flex-no-wrap">
      <div
        class="height100vh full-layout-white fc-border-left fc-border-right approval-sidebar-list"
        style="flex: 0 0 320px;max-width:320px"
      >
        <SummarySidebar
          :list="shipments"
          :isLoading.sync="loading"
          :activeRecordId="selectedRecordId"
          :total="listCount"
          :currentCount="(shipments || []).length"
          class="purchaseorder-summary-list"
        >
          <template #title>
            <el-row class="p15 pT20 pB20 fc-border-bottom">
              <el-col :span="2">
                <span @click="openList()">
                  <inline-svg
                    @click="openList()"
                    src="svgs/arrow"
                    class="rotate-90 pointer"
                    iconClass="icon icon-sm"
                  >
                  </inline-svg>
                </span>
              </el-col>

              <el-col :span="22">
                <div class="bold">{{ currentViewDetail.displayName }}</div>
              </el-col>
            </el-row>
          </template>
          <template v-slot="{ record }">
            <div
              class="sp-li ellipsis f12 pointer asset-item p20 active"
              @click="openShipmentOverview(record.id)"
            >
              <el-row>
                <el-col :span="24">
                  <div
                    class="f14 truncate-text"
                    :title="record.fromStore.name + ' + ' + record.toStore.name"
                    v-tippy="{
                      placement: 'top',
                      animation: 'shift-away',
                      arrow: true,
                    }"
                  >
                    {{
                      $getProperty(record.fromStore, 'name') +
                        ' -> ' +
                        $getProperty(record.toStore, 'name')
                    }}
                  </div>
                  <div class="flex-middle justify-content-space width100 pT10">
                    <div class="d-flex">
                      <div class="fc-id pR10">#{{ record.id }}</div>
                    </div>
                    <div class="fc-grey2-text12">
                      <i class="el-icon-time pR5"></i>
                      {{ record.sysCreatedTime | fromNow }}
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </template>
        </SummarySidebar>
      </div>
      <div class="flex-grow flex-shrink flex-no-wrap inventory-summary-width">
        <router-view class="flex-grow"></router-view>
      </div>
    </div>
  </div>
  <!-- eslint-disable-next-line vue/valid-template-root -->
  <CommonListLayout
    v-else
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => moduleDisplayName"
    :hideSubHeader="isEmpty(shipments)"
    pathPrefix="/app/inventory/shipment"
    :key="moduleName + '-list-layout'"
    :recordCount="listCount"
    :recordLoading="showLoading"
  >
    <template #header>
      <AdvancedSearchWrapper
        :key="`${moduleName}-search`"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>

      <button class="fc-create-btn" @click="newShipment()">
        NEW SHIPMENT
      </button>
    </template>
    <template #sub-header-actions>
      <template v-if="!isEmpty(shipments)">
        <Pagination
          :total="listCount"
          :perPage="50"
          :skipTotalCount="true"
        ></Pagination>
        <span class="separator pL0" v-if="listCount"> |</span>
        <Sort
          :key="moduleName + '-sort'"
          :config="sortConfig"
          :sortList="sortConfigLists"
          @onchange="updateSort"
        ></Sort>
        <span class="separator pL0"> |</span>
        <FExportSettings
          :module="moduleName"
          :viewDetail="viewDetail"
          :showMail="false"
          :filters="filters"
        ></FExportSettings>
      </template>
    </template>
    <template #content>
      <div class="fc-card-popup-list-view">
        <div
          class="fc-list-view p10 pT0 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
          v-if="selectedRecordId === -1"
        >
          <Spinner
            v-if="showLoading"
            class="mT40"
            :show="showLoading"
          ></Spinner>
          <div
            v-if="$validation.isEmpty(shipments) && !showLoading"
            class="fc-list-empty-state-container"
          >
            <inline-svg
              src="svgs/emptystate/inventory"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="nowo-label">
              No Shipments Available
            </div>
          </div>
          <div v-if="!showLoading && !$validation.isEmpty(shipments)">
            <div class="view-column-chooser" @click="showColumnSettings = true">
              <img
                src="~assets/column-setting.svg"
                style="text-align: center; position: absolute; top: 35%;right: 29%;"
              />
            </div>
            <el-table
              :data="shipments"
              style="width: 100%;"
              height="100%"
              :row-class-name="'no-hover'"
            >
              <el-table-column fixed prop="" label="ID" min-width="30">
                <template v-slot="shipment">
                  <div
                    @click="openShipmentOverview(shipment.row.id)"
                    class="fc-id main-field-column"
                  >
                    {{ '#' + shipment.row.id }}
                  </div>
                </template>
              </el-table-column>
              <template v-for="(field, index) in viewColumns">
                <el-table-column
                  :fixed="field.name === 'name'"
                  :align="
                    field.field.dataTypeEnum === 'DECIMAL' ? 'right' : 'left'
                  "
                  :prop="field.name"
                  v-if="!isFixedColumn(field.name) || field.parentField"
                  :key="index"
                  :label="field.displayName"
                >
                  <template v-slot="shipment">
                    <div v-if="!isFixedColumn(field.name) || field.parentField">
                      <div v-if="field.name === 'movable'">
                        {{
                          shipment.row.type &&
                          getShipmentType(shipment.type.row.id).movable
                            ? 'Yes'
                            : 'No'
                        }}
                      </div>
                      <div
                        class="table-subheading"
                        v-else
                        :class="{
                          'text-right': field.field.dataTypeEnum === 'DECIMAL',
                        }"
                      >
                        {{
                          getColumnDisplayValue(field, shipment.row) || '---'
                        }}
                      </div>
                    </div>
                  </template>
                </el-table-column>
              </template>
              <el-table-column
                prop=""
                label=""
                width="130"
                class="visibility-visible-actions"
              >
                <template v-slot="shipment">
                  <div class="text-center">
                    <i
                      class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                      data-arrow="true"
                      v-if="shipment.row.status === 1"
                      title="Delete Shipment"
                      v-tippy
                      @click="deleteShipment([shipment.row.id])"
                    ></i>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
        <column-customization
          :visible.sync="showColumnSettings"
          moduleName="shipment"
          :viewName="currentView"
        ></column-customization>
        <portal to="view-manager-link">
          <router-link
            tag="div"
            :to="`/app/inventory/${moduleName}/viewmanager`"
            class="view-manager-btn"
          >
            <inline-svg
              src="svgs/hamburger-menu"
              class="d-flex"
              iconClass="icon icon-sm"
            ></inline-svg>
            <span class="label mL10 text-uppercase">
              {{ $t('viewsmanager.list.views_manager') }}
            </span>
          </router-link>
        </portal>
      </div>
      <new-shipment
        v-if="newShipmentFormVisibility"
        :visibility.sync="newShipmentFormVisibility"
        @saved="loadShipments"
      ></new-shipment>
    </template>
  </CommonListLayout>
</template>
<script>
import Spinner from '@/Spinner'
import ColumnCustomization from '@/ColumnCustomization'
import ViewMixinHelper from '@/mixins/ViewMixin'
import SummarySidebar from 'newapp/components/SummarySideBar'
import { isEmpty } from '@facilio/utils/validation'
import Pagination from 'src/newapp/components/ListPagination'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import CommonListLayout from 'newapp/list/CommonLayout'
import { API } from '@facilio/api'
import NewShipment from 'pages/Inventory/Shipment/NewShipmentform'
import { mapState } from 'vuex'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper'

export default {
  mixins: [ViewMixinHelper],
  props: ['moduleName', 'viewname'],
  components: {
    Spinner,
    ColumnCustomization,
    SummarySidebar,
    CommonListLayout,
    Pagination,
    FExportSettings,
    Sort,
    NewShipment,
    AdvancedSearchWrapper,
  },
  data() {
    return {
      isEmpty,
      shipmentObj: '',
      showCreateNewDialog: false,
      isVisible: false,
      loading: true,
      showPrintPreview: false,
      listCount: null,
      selectAll: false,
      selectedShipments: [],
      newShipmentFormVisibility: false,
      shipments: [],
      fetchingMore: false,
      sortConfigLists: [],
      sortConfig: {
        orderBy: {
          label: this.$t('common._common.id'),
          value: 'localId',
        },
        orderType: 'desc',
      },
      actions: {
        delete: {
          loading: false,
        },
      },
      showColumnSettings: false,
      saving: false,
    }
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
      metaInfo: state => state.view.metaInfo,
      groupViews: state => state.view.groupViews,
      viewLoading: state => state.view.isLoading,
      detailLoading: state => state.view.detailLoading,
    }),
    currentView() {
      return this.$route.params.viewname
    },
    currentViewDetail() {
      if (this.$route.query.search) {
        return { displayName: 'Filtered Item', name: 'filtereditems' }
      }
      return this.viewDetail || {}
    },
    isSummaryOpen() {
      return !isEmpty(this.selectedRecordId)
    },
    moduleDisplayName() {
      return 'shipment'
    },
    selectedRecordId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    page() {
      return this.$route.query.page || 1
    },
    showLoading() {
      return this.loading || this.detailLoading
    },
  },
  watch: {
    currentView: {
      handler(newVal, oldVal) {
        if (oldVal !== newVal) {
          this.loadShipments()
          this.loadShipmentsCount()
        }
      },
      immediate: true,
    },
    filters() {
      this.loadShipments()
      this.loadShipmentsCount()
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadShipments()
      }
    },
  },
  methods: {
    async loadShipments() {
      let { currentView, page, filters, includeParentFilter } = this
      let params = {
        page,
        perPage: 50,
        includeParentFilter,
        filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
      }

      this.loading = true

      let { error, data } = await API.get(`/v2/shipment/${currentView}`, params)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.shipments = data.shipments
      }
      this.loading = false
      this.fetchingMore = false
    },
    updateSort(sorting) {
      let { moduleName, currentView } = this
      let sortObj = {
        moduleName,
        viewName: currentView,
        orderBy: sorting.orderBy,
        orderType: sorting.orderType,
        skipDispatch: true,
      }
      this.$store
        .dispatch('view/savesorting', sortObj)
        .then(() => this.loadShipments())
    },
    newShipment() {
      this.newShipmentFormVisibility = true
    },
    async loadShipmentsCount() {
      let queryObj = {
        viewname: this.currentView,
        filters: this.filters,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        count: true,
      }
      let url = `/v2/shipment/shipmentCount?viewName=${queryObj.viewname}`
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

      let { data, error } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.listCount = this.$getProperty(data, 'recordCount')
        this.$emit('syncCount', this.$getProperty(data, 'recordCount'))
      }
    },
    loadMore() {
      this.fetchingMore = true
      this.loadShipments(true)
    },
    deleteShipment(idList) {
      let self = this
      self.$dialog
        .confirm({
          title: 'Delete Shipment(s)',
          message: 'Are you sure you want to delete this Shipment(s)?',
          rbDanger: true,
          rbLabel: self.$t('common._common.delete'),
        })
        .then(function(value) {
          if (value) {
            self.$http
              .post('/v2/shipment/delete', { recordIds: idList })
              .then(function(response) {
                if (response.data.responseCode === 0) {
                  response.data.result.recordIds.forEach(element => {
                    let doAsset = self.shipments.find(r => r.id === element)
                    if (doAsset) {
                      self.shipments.splice(self.shipments.indexOf(doAsset), 1)
                      self.$message.success('Shipment(s) Deleted Successfully')
                    }
                  })
                } else {
                  self.$message.error('Unable to delete this Shipment')
                }
              })
          }
        })
    },
    openList() {
      if (isWebTabsEnabled()) {
        let { currentView } = this
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'shipment',

          params: {
            viewname: this.currentView,
          },
          query: this.$route.query,
        })
      }
    },
    openShipmentOverview(id) {
      if (isWebTabsEnabled()) {
        let { currentView } = this
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
              id,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'shipmentSummary',
          params: {
            id,
            viewname: this.currentView,
          },
          query: this.$route.query,
        })
      }
    },
  },
}
</script>
<style scoped></style>
