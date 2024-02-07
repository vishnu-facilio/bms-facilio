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
          :list="gatePass"
          :isLoading.sync="loading"
          :activeRecordId="selectedRecordId"
          :total="listCount"
          :currentCount="(gatePass || []).length"
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
              @click="openGatePassSummary(record.id)"
            >
              <el-row>
                <el-col :span="24">
                  <div
                    class="f14 truncate-text"
                    :title="record.subject || record.name || record.issuedTo"
                    v-tippy="{
                      placement: 'top',
                      animation: 'shift-away',
                      arrow: true,
                    }"
                  >
                    {{ record.subject || record.name || record.issuedTo }}
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
    :hideSubHeader="isEmpty(gatePass)"
    pathPrefix="/app/inventory/gatepass"
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
      <button class="fc-create-btn" @click="openGatePassForm()">
        NEW GATE PASS
      </button>
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(gatePass)">
        <Pagination
          :total="listCount"
          :perPage="50"
          :skipTotalCount="true"
          class="mT2"
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
          class="fc-list-view p10 pT0  fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
          v-if="selectedRecordId === -1"
        >
          <Spinner
            v-if="showLoading"
            class="mT40"
            :show="showLoading"
          ></Spinner>
          <div
            v-if="$validation.isEmpty(gatePass) && !showLoading"
            class="fc-list-empty-state-container"
          >
            <inline-svg
              src="svgs/emptystate/inventory"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="nowo-label">
              No Gate Pass available
            </div>
          </div>
          <div v-if="!showLoading && !$validation.isEmpty(gatePass)">
            <div class="view-column-chooser" @click="showColumnSettings = true">
              <img
                src="~assets/column-setting.svg"
                style="text-align: center; position: absolute; top: 35%;right: 29%;"
              />
            </div>
            <el-table
              :data="gatePass"
              style="width: 100%;"
              height="auto"
              :fit="true"
              :row-class-name="'no-hover'"
            >
              <el-table-column fixed prop="" label="ID" min-width="90">
                <template v-slot="gatePass">
                  <div
                    @click="openGatePassSummary(gatePass.row.id)"
                    class="fc-id main-field-column"
                  >
                    {{ '#' + gatePass.row.id }}
                  </div>
                </template>
              </el-table-column>
              <template v-for="(field, index) in viewColumns">
                <el-table-column
                  :fixed="field.name === 'name'"
                  :align="
                    field.field.dataTypeEnum === 'DECIMAL' ? 'right' : 'left'
                  "
                  :key="index"
                  :prop="field.name"
                  :label="field.displayName"
                  min-width="200"
                  v-if="!isFixedColumn(field.name) || field.parentField"
                >
                  <template v-slot="gatePass">
                    <div v-if="!isFixedColumn(field.name) || field.parentField">
                      <div v-if="field.name === 'movable'">
                        {{
                          gatePass.row.type &&
                          getItem(gatePass.row.type.id).movable
                            ? 'Yes'
                            : 'No'
                        }}
                      </div>
                      <div
                        v-else-if="field && field.name === 'issuedBy'"
                        class="q-item-division relative-position wo_assignedto_avatarblock"
                      >
                        <div class="wo-assigned-avatar fL">
                          <div
                            v-if="
                              gatePass.row.issuedBy && gatePass.row.issuedBy.id
                            "
                          >
                            <user-avatar
                              size="md"
                              :user="
                                $store.getters.getUser(gatePass.row.issuedBy.id)
                              "
                            ></user-avatar>
                          </div>
                        </div>
                      </div>
                      <div
                        class="table-subheading"
                        v-else
                        :class="{
                          'text-right': field.field.dataTypeEnum === 'DECIMAL',
                        }"
                      >
                        {{
                          getColumnDisplayValue(field, gatePass.row) || '---'
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
                fixed="right"
              >
                <template v-slot="gatePass">
                  <div class="text-center">
                    <i
                      class="el-icon-printer edit-icon-color visibility-hide-actions"
                      title="Print Gate Pass"
                      data-arrow="true"
                      v-tippy
                      @click="printGatePass(gatePass.row)"
                    ></i>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>

      <column-customization
        :visible.sync="showColumnSettings"
        moduleName="gatePass"
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
      <gate-pass-form
        v-if="gatePassFormVisibility"
        :visibility.sync="gatePassFormVisibility"
        @saved="refreshGatePassList"
      ></gate-pass-form>
    </template>
  </CommonListLayout>
</template>
<script>
import Spinner from '@/Spinner'
import ColumnCustomization from '@/ColumnCustomization'
import ViewMixinHelper from '@/mixins/ViewMixin'
import UserAvatar from '@/avatar/User'
import CommonListLayout from 'newapp/list/CommonLayout'
import SummarySidebar from 'newapp/components/SummarySideBar'
import GatePassForm from 'pages/Inventory/Gatepass/NewGatePass'
import { isEmpty } from '@facilio/utils/validation'
import Pagination from 'src/newapp/components/ListPagination'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import { API } from '@facilio/api'
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
    UserAvatar,
    SummarySidebar,
    CommonListLayout,
    Pagination,
    FExportSettings,
    Sort,
    GatePassForm,
    AdvancedSearchWrapper,
  },
  data() {
    return {
      gatePassObj: '',
      showCreateNewDialog: false,
      loading: true,
      listCount: null,
      gatePassFormVisibility: false,
      sortConfigLists: [],
      gatePass: [],
      isNewItem: true,
      fetchingMore: false,
      saving: false,
      sortConfig: {
        orderBy: {
          label: this.$t('common._common.id'),
          value: 'localId',
        },
        orderType: 'desc',
      },
      showColumnSettings: false,
      isEmpty,
    }
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
      metaInfo: state => state.view.metaInfo,
      groupViews: state => state.view.groupViews,
      viewLoading: state => state.view.isLoading,
    }),
    currentViewDetail() {
      if (this.$route.query.search) {
        return { displayName: 'Filtered Item', name: 'filtereditems' }
      }
      return this.viewDetail || {}
    },
    isSummaryOpen() {
      return !isEmpty(this.selectedRecordId)
    },
    selectedRecordId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    moduleDisplayName() {
      return 'Gate Pass'
    },
    currentView() {
      return this.$route.params.viewname
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
      return this.loading || this.viewLoading
    },
  },
  watch: {
    currentView: {
      handler(newVal, oldVal) {
        if (oldVal !== newVal && !isEmpty(newVal)) {
          this.loadGatePass()
          this.loadGatePassCount()
        }
      },
      immediate: true,
    },
    filters() {
      this.loadGatePass()
      this.loadGatePassCount()
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadGatePass()
      }
    },
  },
  methods: {
    editItem(gatePass) {
      let url = '/v2/gatePass/' + gatePass.id
      API.get(url).then(({ data }) => {
        this.isNewItem = false
        this.gatePassObj = data.gatePass
        this.showCreateNewDialog = true
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
          name: 'gatepass',

          params: {
            viewname: this.currentView,
          },
          query: this.$route.query,
        })
      }
    },
    refreshGatePassList() {
      this.loadGatePass()
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
        .then(() => this.loadItemTypes())
    },
    openGatePassSummary(id) {
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
          name: 'gatepassSummary',
          params: {
            id,
            viewname: this.currentView,
          },
          query: this.$route.query,
        })
      }
    },

    printGatePass(gatePass) {
      window.open(
        window.location.protocol +
          '//' +
          window.location.host +
          '/app/at/gatepass/pdf/' +
          gatePass.id
      )
    },
    closeNewDialog() {
      this.gatePassObj = ''
      this.isNewItem = true
      this.showCreateNewDialog = false
    },
    refreshItemList() {
      this.loadGatePass()
      this.showCreateNewDialog = false
    },
    async loadGatePass() {
      let { currentView, page, filters, includeParentFilter } = this
      let params = {
        page,
        perPage: 50,
        includeParentFilter,
        filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
      }

      this.loading = true

      let { error, data } = await API.get(
        `/v2/gatePass/view/${currentView}`,
        params
      )

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.gatePass = data.gatePass
      }
      this.loading = false
      this.fetchingMore = false
    },
    openGatePassForm() {
      this.gatePassFormVisibility = true
    },
    async loadGatePassCount() {
      let queryObj = {
        viewname: this.currentView,
        filters: this.filters,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        count: true,
      }
      let url = `/v2/gatePass/count?viewName=${queryObj.viewname}`

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
      let { data, error } = await API.get(url)

      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.listCount = this.$getProperty(data, 'count')
        this.$emit('syncCount', this.$getProperty(data, 'count'))
      }
    },
    loadMore() {
      this.fetchingMore = true
      this.loadGatePass(true)
    },
  },
}
</script>
<style scoped></style>
