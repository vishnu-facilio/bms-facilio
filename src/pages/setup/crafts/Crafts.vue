<template>
  <div class="fc-setup-page">
    <SetupHeader>
      <template #heading>
        {{ $t('setup.crafts.heading') }}
      </template>
      <template #description>
        {{ $t('setup.crafts.description') }}
      </template>
      <template #actions>
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="addCraftsAndSkills"
        >
          {{ $t('setup.crafts.button.add_crafts') }}
        </el-button>
      </template>
      <template #searchAndPagination>
        <div class="flex-middle" :class="disableAction">
          <f-search
            class="mR10 pT3"
            v-model="craftlist"
            key="name"
            :remote="true"
            @search="quickSearch"
            :class="disableQuickSearch"
          ></f-search>
          <AdvancedSearch
            :key="`${moduleName}-search`"
            :moduleName="moduleName"
            :moduleDisplayName="moduleDisplayName"
            :hideQuery="true"
            :onSave="applyAdvancedSearchFilters"
            :filterList="craftsFilterList"
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
            :total="craftCount"
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
        :filterList="craftsFilterList"
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
      v-else-if="$validation.isEmpty(craftlist) && !loading"
    >
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('setup.empty.empty_crafts') }}
      </template>
    </SetupEmpty>
    <div v-else class="mL10 mT10 mR10">
      <el-table
        :data="craftlist"
        class="width100 fc-setup-table fc-setup-table-th-borderTop"
        :height="tableHeight"
        :header-cell-style="{ background: '#f3f1fc' }"
      >
        <el-table-column
          prop="name"
          :label="$t('setup.users_management.name')"
          :width="200"
        >
          <template v-slot="craft">
            <div
              class="truncate-text"
              v-tippy
              :title="getCraftDetail(craft, 'name')"
            >
              {{ getCraftDetail(craft, 'name') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="description"
          :label="$t('setup.relationship.description')"
          :width="400"
        >
          <template v-slot="craft">
            <div
              class="truncate-text"
              v-tippy
              :title="getCraftDetail(craft, 'description')"
            >
              {{ getCraftDetail(craft, 'description') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="standardRate"
          :label="`Rate Per Hour(${$currency})`"
          :width="200"
        >
          <template v-slot="craft">
            {{ $getProperty(craft.row, 'standardRate', '---') }}
          </template>
        </el-table-column>
        <el-table-column
          prop
          label
          :width="180"
          class="visibility-visible-actions"
          fixed="right"
        >
          <template v-slot="craft">
            <div class="d-flex text-center template-actions flex-middle">
              <i
                class="el-icon-edit edit-icon visibility-hide-actions"
                data-arrow="true"
                :title="$t('common._common.edit')"
                v-tippy
                @click="editCraft(craft.row.id)"
              ></i>
              <i
                class="el-icon-delete fc-delete-icon visibility-hide-actions mL15"
                data-arrow="true"
                :title="$t('common._common.delete')"
                v-tippy
                @click="deleteCraft(craft.row.id)"
              ></i>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <CraftForm
      v-if="showAddCraft"
      :isNew="isNew"
      :selectedCraftId="selectedCraftId"
      @onSubmit="fetchCraftList"
      @onClose="showAddCraft = false"
    ></CraftForm>
  </div>
</template>
<script>
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import SetupHeader from 'pages/setup/components/SetupHeader'
import CraftForm from './CraftForm.vue'
import { API } from '@facilio/api'
import Pagination from 'pages/setup/components/SetupPagination'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import { isEmpty } from '@facilio/utils/validation'
import FSearch from '@/FSearch'
import FTags from 'newapp/components/search/FTags'

export default {
  components: {
    SetupHeader,
    SetupLoader,
    SetupEmpty,
    CraftForm,
    Pagination,
    AdvancedSearch,
    FSearch,
    FTags,
  },
  data() {
    return {
      craftCount: null,
      page: 1,
      perPage: 50,
      showAddCraft: false,
      isNew: false,
      loading: true,
      craftlist: [],
      selectedCraftId: '',
      craftsFilterList: null,
      craftsQuickSearch: null,
      moduleName: 'crafts',
      moduleDisplayName: 'Crafts',
    }
  },
  async created() {
    await this.fetchCraftList()
  },
  computed: {
    disableAction() {
      let { craftlist } = this
      return isEmpty(craftlist) ? 'disable-actions' : ''
    },
    tableHeight() {
      let { craftsFilterList } = this
      return !isEmpty(craftsFilterList)
        ? 'calc(100vh - 250px)'
        : 'calc(100vh - 210px)'
    },
    resourceEmpty() {
      let { craftsFilterList } = this
      return !isEmpty(craftsFilterList)
        ? 'resource-filter-applied-empty'
        : 'resource-empty'
    },
    disableQuickSearch() {
      let { craftsFilterList } = this
      return !isEmpty(craftsFilterList) ? 'disable-search-actions' : ''
    },
    disableAdvancedSearch() {
      let { craftsQuickSearch } = this
      return !isEmpty(craftsQuickSearch) ? 'disable-search-actions' : ''
    },
  },
  watch: {
    page: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          let { craftsFilterList, craftsQuickSearch } = this
          let filters = !isEmpty(craftsFilterList)
            ? craftsFilterList
            : craftsQuickSearch
          if (!isEmpty(filters)) this.fetchCraftList({ filters })
          else this.fetchCraftList()
        }
      },
      immediate: true,
    },
  },
  methods: {
    async fetchCraftList(props) {
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

      let { list, error, meta = {} } = await API.fetchAll(`crafts`, params)

      if (error) {
        let { message } = error
        this.$message.error(
          message || this.$t(`setup.crafts.fetch_craft_list_error`)
        )
      } else {
        this.craftCount = this.$getProperty(meta, 'pagination.totalCount', null)
        this.craftlist = list || []
      }
      this.loading = false
    },
    getCraftDetail(craft, property) {
      let { row } = craft || {}
      return this.$getProperty(row, property, '---')
    },
    setPage(page) {
      this.page = page
    },
    resetPage() {
      this.page = 1
    },
    resetFilters() {
      this.craftsFilterList = []
      this.resetPage()
      this.fetchCraftList()
    },
    addCraftsAndSkills() {
      this.showAddCraft = true
      this.isNew = true
    },
    editCraft(craftId) {
      this.selectedCraftId = craftId
      this.isNew = false
      this.showAddCraft = true
    },
    async deleteCraft(craftId) {
      let value = await this.$dialog.confirm({
        title: this.$t('setup.users_management.delete_craft'),
        message: this.$t('setup.users_management.are_you_sure_delete_craft'),
        rbDanger: true,
        rbLabel: 'Delete',
      })
      if (value) {
        let { moduleName } = this
        let { error } = await API.deleteRecord(moduleName, craftId)
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.resetPage()
          await this.fetchCraftList()
          this.$message.success(this.$t('setup.crafts.delete_crafts_success'))
        }
      }
    },
    applyAdvancedSearchFilters({ filters }) {
      this.craftsFilterList = filters
      this.resetPage()
      this.fetchCraftList({ filters })
    },
    quickSearch(searchText) {
      this.resetPage()
      if (!isEmpty(searchText)) {
        let filters = { name: { operatorId: 5, value: [`${searchText}`] } }
        this.craftsQuickSearch = filters
        this.fetchCraftList({ filters })
      } else {
        this.craftsQuickSearch = null
        this.fetchCraftList()
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
  .disable-actions {
    pointer-events: none !important;
  }
  .disable-search-actions {
    pointer-events: none !important;
    opacity: 0.5;
  }
  .truncate-text {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>
