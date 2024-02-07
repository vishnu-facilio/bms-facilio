<template>
  <div class="fc-setup-page">
    <SetupHeader>
      <template #heading>
        {{ $t('setup.labour.heading') }}
      </template>
      <template #description>
        {{ $t('setup.labour.description') }}
      </template>
      <template #actions>
        <el-button type="primary" class="setup-el-btn" @click="addLabour">
          {{ $t('setup.labour.button.add_labour') }}
        </el-button>
      </template>
      <template #searchAndPagination>
        <div class="flex-middle" :class="disableAction">
          <f-search
            class="mR10 pT3"
            v-model="labourlist"
            key="name"
            :remote="true"
            :class="disableQuickSearch"
            @search="quickSearch"
          ></f-search>
          <AdvancedSearch
            :key="`${moduleName}-search`"
            :moduleName="moduleName"
            :moduleDisplayName="moduleDisplayName"
            :hideQuery="true"
            :onSave="applyAdvancedSearchFilters"
            :filterList="labourFilterList"
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
            :currentPage.sync="page"
            :total="labourCount"
            :perPage="perPage"
            :pageNo="page"
            @onPageChanged="setPage"
          ></pagination>
        </div>
      </template>
    </SetupHeader>
    <div class="mB15">
      <FTags
        :key="`ftags-list-${moduleName}`"
        :hideQuery="true"
        :hideSaveView="true"
        :filterList="labourFilterList"
        @updateFilters="applyAdvancedSearchFilters"
        @resetFilters="resetFilters"
      ></FTags>
    </div>
    <SetupLoader class="m10" :class="resourceEmpty" v-if="loading">
      <template #setupLoading>
        <spinner :show="loading" size="80"></spinner>
      </template>
    </SetupLoader>
    <SetupEmpty
      class="m10"
      :class="resourceEmpty"
      v-else-if="$validation.isEmpty(labourlist) && !loading"
    >
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('setup.empty.empty_labor') }}
      </template>
    </SetupEmpty>
    <div v-else class="m15">
      <el-table
        :data="labourlist"
        empty-text="No Labor Available"
        class="width100 fc-setup-table fc-setup-table-th-borderTop"
        :height="tableHeight"
        :header-cell-style="{ background: '#f3f1fc' }"
      >
        <el-table-column
          prop="name"
          :label="$t('common.roles.name')"
          fixed
          :width="250"
        >
          <template v-slot="labor">
            <div
              class="truncate-text"
              v-tippy
              :title="getLaborDetail(labor, 'name')"
            >
              {{ getLaborDetail(labor, 'name') }}
            </div>
          </template>
        </el-table-column>

        <el-table-column
          prop="people.name"
          :label="$t('common.products.people')"
          :width="200"
        >
          <template v-slot="labor">
            <div
              class="truncate-text"
              v-tippy
              :title="getLaborDetail(labor, 'peopleName')"
            >
              {{ getLaborDetail(labor, 'peopleName') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="email"
          :label="$t('common.header._email')"
          :width="250"
        >
          <template v-slot="labor">
            <div
              class="truncate-text"
              v-tippy
              :title="getLaborDetail(labor, 'email')"
            >
              {{ getLaborDetail(labor, 'email') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="cost"
          :label="`${$t('setup.labour.form.rateperhour')}(${$currency})`"
          :width="200"
        >
          <template v-slot="labor">
            {{ getLaborDetail(labor, 'cost') }}
          </template>
        </el-table-column>
        <el-table-column
          prop
          label
          :width="180"
          fixed="right"
          class="visibility-visible-actions"
        >
          <template v-slot="labour">
            <div class="text-center template-actions">
              <i
                class="el-icon-edit edit-icon visibility-hide-actions"
                data-arrow="true"
                :title="$t('common._common.edit')"
                v-tippy
                @click="editLabour(labour.row)"
              ></i>
              <i
                class="el-icon-delete fc-delete-icon visibility-hide-actions mL15"
                data-arrow="true"
                :title="$t('common._common.delete')"
                v-tippy
                @click="deleteLabour(labour.row.id)"
              ></i>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <LabourForm
      v-if="showAddLabour"
      :isNew="isNew"
      :editLabourDetails="selectedLabourRowDetails"
      @onSubmit="fetchLabourList"
      @onClose="showAddLabour = false"
    ></LabourForm>
  </div>
</template>
<script>
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import SetupHeader from 'pages/setup/components/SetupHeader'
import LabourForm from './LabourForm.vue'
import { API } from '@facilio/api'
import Pagination from 'pages/setup/components/SetupPagination'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import { isEmpty } from '@facilio/utils/validation'
import FSearch from '@/FSearch'
import FTags from 'newapp/components/search/FTags'

export default {
  components: {
    SetupLoader,
    SetupEmpty,
    SetupHeader,
    LabourForm,
    Pagination,
    AdvancedSearch,
    FSearch,
    FTags,
  },
  data() {
    return {
      showAddLabour: false,
      isNew: false,
      loading: true,
      labourlist: [],
      selectedLabourRowDetails: null,
      page: 1,
      perPage: 50,
      labourFilterList: null,
      labourQuickSearch: null,
      moduleName: 'labour',
      moduleDisplayName: 'Labour',
      labourCount: null,
    }
  },
  async created() {
    await this.fetchLabourList()
  },
  computed: {
    disableAction() {
      let { labourlist } = this
      return isEmpty(labourlist) ? 'disable-actions' : ''
    },
    tableHeight() {
      let { labourFilterList } = this
      return !isEmpty(labourFilterList)
        ? 'calc(100vh - 280px)'
        : 'calc(100vh - 210px)'
    },
    resourceEmpty() {
      let { labourFilterList } = this
      return !isEmpty(labourFilterList)
        ? 'resource-filter-applied-empty'
        : 'resource-empty'
    },
    disableQuickSearch() {
      let { labourFilterList } = this
      return !isEmpty(labourFilterList) ? 'disable-search-actions' : ''
    },
    disableAdvancedSearch() {
      let { labourQuickSearch } = this
      return !isEmpty(labourQuickSearch) ? 'disable-search-actions' : ''
    },
  },
  watch: {
    page: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          let { labourFilterList, labourQuickSearch } = this
          let filters = !isEmpty(labourFilterList)
            ? labourFilterList
            : labourQuickSearch
          if (!isEmpty(filters)) this.fetchLabourList({ filters })
          else this.fetchLabourList()
        }
      },
      immediate: true,
    },
  },
  methods: {
    async fetchLabourList(props) {
      this.loading = true
      let { filters } = props || {}
      let params = {
        page: this.page,
        perPage: this.perPage,
        withCount: true,
        moduleName: this.moduleName,
      }
      if (!isEmpty(filters)) {
        params = { ...params, filters: JSON.stringify(filters) }
      }
      let { list, error, meta = {} } = await API.fetchAll('labour', params)
      if (error) {
        let { message } = error
        this.$message.error(
          message || this.$t(`setup.labour.fetch_labour_error`)
        )
      } else {
        this.labourCount = this.$getProperty(
          meta,
          'pagination.totalCount',
          null
        )
        this.labourlist = list || []
      }
      this.loading = false
    },
    quickSearch(searchText) {
      this.resetPage()
      if (!isEmpty(searchText)) {
        let filters = { name: { operatorId: 5, value: [`${searchText}`] } }
        this.labourQuickSearch = filters
        this.fetchLabourList({ filters })
      } else {
        this.labourQuickSearch = null
        this.fetchLabourList()
      }
    },
    getLaborDetail(labor, property) {
      let { row } = labor || {}
      let value =
        property === 'peopleName'
          ? this.$getProperty(row.people, 'name', '---')
          : this.$getProperty(row, property, '---')
      return value
    },
    resetFilters() {
      this.labourFilterList = []
      this.resetPage()
      this.fetchLabourList()
    },
    addLabour() {
      this.selectedLabourRowDetails = null
      this.showAddLabour = true
      this.isNew = true
    },
    editLabour(selectedLabourRowDetails) {
      this.selectedLabourRowDetails = selectedLabourRowDetails
      this.isNew = false
      this.showAddLabour = true
    },
    applyAdvancedSearchFilters({ filters }) {
      this.labourFilterList = filters
      this.resetPage()
      this.fetchLabourList({ filters })
    },
    setPage(page) {
      this.page = page
    },
    resetPage() {
      this.page = 1
    },
    async deleteLabour(laborId) {
      let value = await this.$dialog.confirm({
        title: this.$t('setup.users_management.delete_labor'),
        message: this.$t('setup.users_management.are_you_sure_delete_labor'),
        rbDanger: true,
        rbLabel: 'Delete',
      })
      if (value) {
        let { moduleName } = this
        let { error } = await API.deleteRecord(moduleName, laborId)
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.resetPage()
          await this.fetchLabourList()
          this.$message.success(
            this.$t('setup.users_management.delete_labor_successfully')
          )
        }
      }
    },
  },
}
</script>
<style scoped lang="scss">
.fc-setup-page {
  .resource-empty {
    height: 75vh !important;
  }
  .resource-filter-applied-empty {
    height: 65vh !important;
  }
  .truncate-text {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .disable-actions {
    pointer-events: none !important;
  }
  .disable-search-actions {
    pointer-events: none !important;
    opacity: 0.5;
  }
}
</style>
