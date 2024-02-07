<template>
  <div class="fc-setup-page">
    <SetupHeader>
      <template #heading>
        {{ $t('setup.users_management.people') }}
      </template>
      <template #description>
        {{ $t('setup.users_management.people_desc') }}
      </template>
      <template #actions>
        <div class="flex-middle" :class="disableAction">
          <div class="action-btn setting-page-btn mL20">
            <el-button type="primary" class="setup-el-btn" @click="addPerson">
              {{ $t('setup.users_management.add_people') }}
            </el-button>
          </div>
        </div>
      </template>
      <template #searchAndPagination class="p10">
        <div class="flex-middle">
          <f-search
            class="mR10"
            :class="disableQuickSearch"
            v-model="peopleList"
            key="name"
            :remote="true"
            @search="quickSearch"
          ></f-search>
          <AdvancedSearch
            :key="`${moduleName}-search`"
            :moduleName="moduleName"
            :moduleDisplayName="moduleDisplayName"
            :hideQuery="true"
            :onSave="applyPeopleFilters"
            :filterList="peopleFilterList"
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
            :total="peopleCount"
            :perPage="perPage"
            class="nowrap pT5"
            ref="f-page"
            :pageNo="peoplePage"
            @onPageChanged="setPage"
          ></pagination>
        </div>
      </template>
    </SetupHeader>
    <div class="mB15" v-if="!$validation.isEmpty(peopleFilterList)">
      <FTags
        :key="`ftags-list-${moduleName}`"
        class="setup-filter-tags"
        :hideQuery="true"
        :hideSaveView="true"
        :filterList="peopleFilterList"
        @updateFilters="applyPeopleFilters"
        @resetFilters="resetFilters"
      ></FTags>
    </div>
    <SetupLoader class="m10" :class="resourceEmpty" v-if="isLoading">
      <template #setupLoading>
        <spinner :show="isLoading" size="80"></spinner>
      </template>
    </SetupLoader>
    <SetupEmpty
      class="m10"
      :class="resourceEmpty"
      v-else-if="$validation.isEmpty(peopleList) && !isLoading"
    >
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('setup.empty.empty_people') }}
      </template>
    </SetupEmpty>
    <div v-else class="mL10 mT10 mR10">
      <el-table
        :data="peopleList"
        class="width100 fc-setup-table fc-setup-table-th-borderTop"
        :height="tableHeight"
        :fit="true"
        :header-cell-style="{ background: '#f3f1fc' }"
      >
        <el-table-column :label="$t('common.roles.name')" fixed :width="300">
          <template v-slot="person">
            <div @click="openSummary(person.row.id)" class="fc-user-list-hover">
              <div
                class="truncate-text"
                v-tippy
                :title="getPersonDetail(person, 'name')"
              >
                <user-avatar size="sm" :user="person.row"></user-avatar>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="email"
          :label="$t('common.header._email')"
          :width="330"
        >
          <template v-slot="person">
            <div
              class="truncate-text"
              v-tippy
              :title="getPersonDetail(person, 'email')"
            >
              {{ getPersonDetail(person, 'email') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="phone"
          :label="$t('setup.users_management.phone')"
          :width="130"
        >
          <template v-slot="person">
            <div
              class="truncate-text"
              v-tippy
              :title="getPersonDetail(person, 'phone')"
            >
              {{ getPersonDetail(person, 'phone') }}
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
          <template v-slot="person">
            <div class="text-center template-actions">
              <i
                class="el-icon-edit edit-icon visibility-hide-actions pR15"
                data-arrow="true"
                :title="$t('common._common.edit')"
                v-tippy
                @click="editPerson(person.row)"
              ></i>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <NewPersonForm
      v-if="showPersonDialog"
      :showPersonDialog="showPersonDialog"
      :selectedPerson="selectedPerson"
      :isNew="isNew"
      @closeDialog="closeDialog"
      @savePerson="savePerson"
    />
  </div>
</template>
<script>
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import SetupHeader from 'pages/setup/components/SetupHeader'
import { API } from '@facilio/api'
import Pagination from 'pages/setup/components/SetupPagination'
import NewPersonForm from './NewPersonForm'
import UserAvatar from '@/avatar/User'
import { isEmpty } from '@facilio/utils/validation'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import FTags from 'newapp/components/search/FTags'
import FSearch from '@/FSearch'

export default {
  name: 'Peoplelist',
  data() {
    return {
      showPersonDialog: false,
      selectedPerson: null,
      isLoading: true,
      peopleList: [],
      perPage: 50,
      peopleCount: null,
      peoplePage: 1,
      isNew: false,
      peopleFilterList: [],
      peopleQuickSearch: null,
    }
  },
  components: {
    SetupEmpty,
    SetupHeader,
    SetupLoader,
    Pagination,
    NewPersonForm,
    UserAvatar,
    AdvancedSearch,
    FTags,
    FSearch,
  },
  computed: {
    disableAction() {
      let { peopleList } = this
      return isEmpty(peopleList) ? 'disable-actions' : ''
    },
    moduleName() {
      return 'people'
    },
    moduleDisplayName() {
      return 'People'
    },
    tableHeight() {
      let { peopleFilterList } = this
      return !isEmpty(peopleFilterList)
        ? 'calc(100vh - 280px)'
        : 'calc(100vh - 200px)'
    },
    resourceEmpty() {
      let { peopleFilterList } = this
      return !isEmpty(peopleFilterList)
        ? 'resource-filter-applied-empty'
        : 'resource-empty'
    },
    disableQuickSearch() {
      let { peopleFilterList } = this
      return !isEmpty(peopleFilterList) ? 'disable-search-actions' : ''
    },
    disableAdvancedSearch() {
      let { peopleQuickSearch } = this
      return !isEmpty(peopleQuickSearch) ? 'disable-search-actions' : ''
    },
  },
  created() {
    this.loadPeople()
  },
  watch: {
    peoplePage: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          let { peopleFilterList, peopleQuickSearch } = this
          let filters = !isEmpty(peopleFilterList)
            ? peopleFilterList
            : peopleQuickSearch
          if (!isEmpty(filters)) this.loadPeople({ filters })
          else this.loadPeople()
        }
      },
      immediate: true,
    },
  },
  methods: {
    async loadPeople(props) {
      this.isLoading = true
      let { filters } = props || {}
      let { peoplePage, perPage, moduleName } = this
      let params = { page: peoplePage, perPage, withCount: true }
      if (!isEmpty(filters)) {
        params = { ...params, filters }
        params['filters'] = JSON.stringify(filters)
      }
      let { list, error, meta } = await API.fetchAll(moduleName, params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.peopleList = list || []
        let { pagination } = meta || {}
        this.peopleCount = this.$getProperty(pagination, 'totalCount', null)
      }
      this.isLoading = false
    },
    getPersonDetail(person, property) {
      let { row } = person || {}
      return this.$getProperty(row, property, '---')
    },
    openSummary(id) {
      this.$router.push({ name: 'peopleSummary', params: { id } })
    },
    addPerson() {
      this.isNew = true
      this.showPersonDialog = true
    },
    editPerson(person) {
      this.showPersonDialog = true
      this.selectedPerson = person
    },
    setPage(page) {
      this.peoplePage = page
    },
    closeDialog(canClose) {
      if (canClose) {
        this.isNew = false
        this.selectedPerson = null
        this.showPersonDialog = false
      }
    },
    resetPage() {
      this.peoplePage = 1
    },
    applyPeopleFilters({ filters }) {
      this.peopleFilterList = filters
      this.resetPage()
      this.loadPeople({ filters })
    },
    quickSearch(searchText) {
      this.resetPage()
      if (!isEmpty(searchText)) {
        let filters = { name: { operatorId: 5, value: [`${searchText}`] } }
        this.peopleQuickSearch = filters
        this.loadPeople({ filters })
      } else {
        this.peopleQuickSearch = null
        this.loadPeople()
      }
    },
    savePerson() {
      this.loadPeople()
      this.isNew = false
      this.showPersonDialog = false
      this.selectedPerson = null
    },
    resetFilters() {
      this.peopleFilterList = []
      this.resetPage()
      this.loadPeople()
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

  .q-item-label {
    &:hover {
      color: #46a2bf !important;
      text-decoration: underline !important;
    }
  }
}
</style>
