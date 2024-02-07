<template>
  <div class="policy-container">
    <portal to="booking-policy-settings-header-booking-policies">
      <div class="display-flex-between-space m20">
        <div class="setting-title-block">
          <div class="setting-form-title">
            {{ $t('workplace.general.bookingpolicies') }}
          </div>
          <div class="heading-description">
            {{ $t('workplace.policies.description') }}
          </div>
        </div>
        <div class="action-btn setting-page-btn">
          <el-button
            type="primary"
            class="el-button setup-el-btn el-button--primary"
            @click="addPolicy"
            >{{ $t('workplace.general.add_policy') }}</el-button
          >
        </div>
      </div>
    </portal>

    <div class="pull-right mB15">
      <template class="p10">
        <div class="flex-middle filter-container">
          <f-search
            class="mR10"
            :class="disableQuickSearch"
            v-model="policyList"
            key="name"
            :remote="true"
            @search="quickSearch"
          ></f-search>
          <span class="separator">|</span>
          <AdvancedSearch
            :key="`${moduleName}-search`"
            :moduleName="moduleName"
            :moduleDisplayName="moduleDisplayName"
            :hideQuery="true"
            :onSave="applyPolicyFilters"
            :filterList="policyFilterList"
            :class="disableAdvancedSearch"
          >
            <template #icon>
              <div class="resource-icons pT5">
                <InlineSvg
                  src="svgs/dashboard/filter"
                  class="pointer"
                  iconClass="icon icon-sm fc-fill-path-grey"
                ></InlineSvg>
              </div>
            </template>
          </AdvancedSearch>
          <span class="separator">|</span>
          <pagination
            :total="policyCount"
            :perPage="perPage"
            class="nowrap pT5"
            ref="f-page"
            :pageNo="policyPage"
            @onPageChanged="setPage"
          ></pagination>
        </div>
      </template>
    </div>

    <div class="mB15" v-if="!$validation.isEmpty(policyFilterList)">
      <FTags
        :key="`ftags-list-${moduleName}`"
        class="setup-filter-tags"
        :hideQuery="true"
        :hideSaveView="true"
        :filterList="policyFilterList"
        @updateFilters="applyPolicyFilters"
        @resetFilters="resetFilters"
      ></FTags>
    </div>

    <SetupLoader class="m10" :class="resourceEmpty" v-if="isLoading">
      <template #setupLoading>
        <spinner :show="isLoading" size="80"></spinner>
      </template>
    </SetupLoader>
    <SetupEmpty
      class="empty-container"
      :class="resourceEmpty"
      v-else-if="$validation.isEmpty(policyList) && !isLoading"
    >
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('workplace.policy_status.no_policy') }}
      </template>
    </SetupEmpty>

    <div v-else>
      <el-table
        :data="policyList"
        class="width100 fc-setup-table fc-setup-table-th-borderTop list-table"
        :height="tableHeight"
        :fit="true"
        :header-cell-style="{ background: '#f3f1fc' }"
      >
        <el-table-column
          :label="$t('workplace.general.name')"
          fixed
          :width="200"
        >
          <template v-slot="policySlot">
            <div
              class="truncate-text"
              v-tippy
              :title="getPolicyDetail(policySlot, 'name')"
            >
              {{ getPolicyDetail(policySlot, 'name') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="criteriaId"
          :label="$t('workplace.general.criteriaId')"
          :width="130"
        >
          <template v-slot="policySlot">
            <div
              class="truncate-text"
              v-tippy
              :title="getPolicyDetail(policySlot, 'criteriaId')"
            >
              {{ getPolicyDetail(policySlot, 'criteriaId') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="description"
          :label="$t('workplace.general.policydescription')"
          :width="330"
        >
          <template v-slot="policySlot">
            <div
              class="truncate-text"
              v-tippy
              :title="getPolicyDetail(policySlot, 'description')"
            >
              {{ getPolicyDetail(policySlot, 'description') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="moduleName"
          :label="$t('workplace.general.moduleName')"
          :width="130"
        >
          <template v-slot="policySlot">
            <div
              class="truncate-text"
              v-tippy
              :title="getPolicyDetail(policySlot, 'moduleName')"
            >
              {{ getPolicyDetail(policySlot, 'moduleName') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop
          label
          :width="180"
          class="visibility-visible-actions"
          fixed="right"
        >
          <template v-slot="policySlot">
            <div class="text-center template-actions">
              <i
                class="el-icon-edit edit-icon visibility-hide-actions pR15"
                data-arrow="true"
                :title="$t('common._common.edit')"
                v-tippy
                @click="editPolicy(policySlot.row)"
              ></i>
              <i
                class="el-icon-delete fc-delete-icon visibility-hide-actions"
                data-arrow="true"
                :title="$t('common._common.delete')"
                v-tippy
                @click="deletePolicy(policySlot.row.id)"
              ></i>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <AddNewPolicy
      v-if="showDialog"
      :selectedPolicy="selectedPolicy"
      :id="id"
      :isNew="isNew"
      @closeDialog="closeDialog"
      @savePolicy="savePolicy"
      :visibility.sync="showDialog"
    />
  </div>
</template>

<script>
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import { API } from '@facilio/api'
import Pagination from 'pages/setup/components/SetupPagination'
import { isEmpty } from '@facilio/utils/validation'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import FTags from 'newapp/components/search/FTags'
import FSearch from '@/FSearch'
import AddNewPolicy from 'src/pages/setup/WorkplaceSettings/NewBookingPolicies.vue'

export default {
  name: 'Policylist',
  title() {
    return this.$t('workplace.general.bookingpolicies')
  },
  data() {
    return {
      showDialog: false,
      selectedPolicy: null,
      isLoading: true,
      policyList: [],
      perPage: 50,
      policyCount: null,
      policyPage: 1,
      isNew: false,
      policyFilterList: [],
      policyQuickSearch: null,
      id: null,
    }
  },
  components: {
    SetupEmpty,
    SetupLoader,
    Pagination,
    AddNewPolicy,
    AdvancedSearch,
    FTags,
    FSearch,
  },
  computed: {
    disableAction() {
      let { policyList } = this
      return isEmpty(policyList) ? 'disable-actions' : ''
    },
    moduleName() {
      return 'spaceBookingPolicy'
    },
    moduleDisplayName() {
      return 'Space Booking Policy'
    },
    tableHeight() {
      let { policyFilterList } = this
      return !isEmpty(policyFilterList)
        ? 'calc(100vh - 260px)'
        : 'calc(100vh - 200px)'
    },
    resourceEmpty() {
      let { policyFilterList } = this
      return !isEmpty(policyFilterList)
        ? 'resource-filter-applied-empty'
        : 'resource-empty'
    },
    disableQuickSearch() {
      let { policyFilterList } = this
      return !isEmpty(policyFilterList) ? 'disable-search-actions' : ''
    },
    disableAdvancedSearch() {
      let { policyQuickSearch } = this
      return !isEmpty(policyQuickSearch) ? 'disable-search-actions' : ''
    },
  },
  created() {
    this.loadPolicy()
  },
  watch: {
    policyPage: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          let { policyFilterList, policyQuickSearch } = this
          let filters = !isEmpty(policyFilterList)
            ? policyFilterList
            : policyQuickSearch
          if (!isEmpty(filters)) this.loadPolicy({ filters })
          else this.loadPolicy()
        }
      },
      immediate: true,
    },
  },
  methods: {
    async editPolicy(policySlot) {
      this.id = policySlot.id

      let { error, data } = await API.get(
        '/v3/modules/data/summary?id=' +
          this.id +
          '&moduleName=spaceBookingPolicy'
      )

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let { spaceBookingPolicy } = data || {}
        if (spaceBookingPolicy != null) {
          this.selectedPolicy = spaceBookingPolicy
          this.isLoading = false
          this.isNew = false
          this.showDialog = true
        }
      }
    },

    async loadPolicy(props) {
      this.isLoading = true
      let { filters } = props || {}
      let { policyPage, perPage, moduleName } = this
      let params = {
        page: policyPage,
        perPage,
        withCount: true,
        orderType: 'desc',
        orderBy: 'SYS_CREATED_TIME',
      }
      if (!isEmpty(filters)) {
        params = { ...params, filters }
        params['filters'] = JSON.stringify(filters)
      }
      let { list, error, meta } = await API.fetchAll(moduleName, params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.policyList = list || []
        let { pagination } = meta || {}
        this.policyCount = this.$getProperty(pagination, 'totalCount', null)
      }
      this.isLoading = false
    },
    getPolicyDetail(policySlot, property) {
      let { row } = policySlot || {}
      return this.$getProperty(row, property, '---')
    },

    addPolicy() {
      this.isNew = true
      this.showDialog = true
    },

    showEdit(policySlot) {
      this.$router.push({
        name: 'bookingpolicy.edit',
        params: {
          id: policySlot.id,
          moduleName: this.moduleName,
        },
      })
    },

    setPage(page) {
      this.policyPage = page
    },
    closeDialog(canClose) {
      if (canClose) {
        this.showDialog = false
      }
    },
    resetPage() {
      this.policyPage = 1
    },
    applyPolicyFilters({ filters }) {
      this.policyFilterList = filters
      this.resetPage()
      this.loadPolicy({ filters })
    },
    quickSearch(searchText) {
      this.resetPage()
      if (!isEmpty(searchText)) {
        let filters = { name: { operatorId: 5, value: [`${searchText}`] } }
        this.policyQuickSearch = filters
        this.loadPolicy({ filters })
      } else {
        this.policyQuickSearch = null
        this.loadPolicy()
      }
    },
    savePolicy() {
      this.loadPolicy()
      this.isNew = false
      this.showDialog = false
      this.selectedPolicy = null
    },
    resetFilters() {
      this.policyFilterList = []
      this.resetPage()
      this.loadPolicy()
    },
    async deletePolicy(policyId) {
      let value = await this.$dialog.confirm({
        title: this.$t('workplace.policy_status.delete_title'),
        message: this.$t('workplace.policy_status.delete_policy'),
        rbDanger: true,
        rbLabel: 'Delete',
      })
      if (value) {
        let { moduleName } = this
        let { error } = await API.deleteRecord(moduleName, policyId)
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.resetPage()
          await this.loadPolicy()
          this.$message.success(this.$t('workplace.policy_status.deleted'))
        }
      }
    },
  },
}
</script>
<style scoped lang="scss">
.policy-container {
  height: calc(100vh - 206px);
  max-height: 700px;
  margin: 0px 20px 20px 30px;
  overflow: hidden;
  padding-bottom: 50px;
  .resource-empty {
    height: 75vh !important;
  }
  .resource-filter-applied-empty {
    height: 65vh !important;
  }

  .disable-search-actions {
    pointer-events: none !important;
    opacity: 0.5;
  }
  .list-table {
    max-height: 550px;
  }
  .filter-container {
    height: 40px;
    display: flex;
    align-items: center;
  }
  .empty-container {
    margin: 60px !important;
  }
}
</style>
