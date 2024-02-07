<template>
  <div class="fc-setup-page">
    <SetupHeader>
      <template #heading>
        {{ $t('setup.users_management.teams') }}
      </template>
      <template #description>
        {{ $t('setup.users_management.team_desc') }}
      </template>
      <template #actions>
        <div class="flex-middle">
          <div class="action-btn setting-page-btn mL20">
            <el-button type="primary" class="setup-el-btn" @click="addTeam">
              {{ $t('setup.users_management.add_team') }}
            </el-button>
          </div>
        </div>
      </template>
      <template #searchAndPagination class="p10">
        <div class="flex-middle" :class="disableAction">
          <f-search
            class="mR10"
            v-model="teamsList"
            key="name"
            :class="disableQuickSearch"
            :remote="true"
            @search="quickSearch"
          ></f-search>
          <AdvancedSearch
            :key="`${moduleName}-search`"
            :moduleName="moduleName"
            :moduleDisplayName="moduleDisplayName"
            :hideQuery="true"
            :onSave="applyTeamFilters"
            :filterList="teamFilterList"
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
            :total="teamsCount"
            :perPage="perPage"
            class="nowrap"
            ref="f-page"
            :pageNo="teamPage"
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
        :filterList="teamFilterList"
        @updateFilters="applyTeamFilters"
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
      v-else-if="$validation.isEmpty(teamsList) && !isLoading"
    >
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('setup.empty.empty_team') }}
      </template>
    </SetupEmpty>
    <div v-else class="mL10 mT10 mR10">
      <el-table
        :data="teamsList"
        class="width100 fc-setup-table fc-setup-table-th-borderTop"
        :height="tableHeight"
        :fit="true"
        :header-cell-style="{ background: '#f3f1fc' }"
      >
        <el-table-column fixed :label="$t('common.roles.name')" :width="300">
          <template v-slot="team">
            <div
              class="truncate-text"
              v-tippy
              :title="getTeamDetail(team, 'name')"
            >
              {{ getTeamDetail(team, 'name') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('setup.users_management.description')"
          :width="250"
        >
          <template v-slot="team">
            <div
              class="truncate-text"
              v-tippy
              :title="getTeamDetail(team, 'description')"
            >
              {{ getTeamDetail(team, 'description') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('setup.users_management.site')"
          :width="200"
        >
          <template v-slot="team">
            <div
              class="truncate-text"
              v-tippy
              :title="getSiteName(team.row.siteId)"
            >
              {{ getSiteName(team.row.siteId) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="email"
          :label="$t('common.header._email')"
          :width="300"
        >
          <template v-slot="team">
            <div
              class="truncate-text"
              v-tippy
              :title="getTeamDetail(team, 'email')"
            >
              {{ getTeamDetail(team, 'email') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          :label="$t('setup.users_management.status')"
          :width="150"
        >
          <template v-slot="team">
            <el-switch
              v-model="team.row.isActive"
              @change="updateTeamStatus(team.row)"
            >
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column
          prop
          label
          :width="180"
          class="visibility-visible-actions"
          fixed="right"
        >
          <template v-slot="team">
            <div class="text-center template-actions">
              <i
                class="el-icon-edit edit-icon visibility-hide-actions pR15"
                data-arrow="true"
                :title="$t('common._common.edit')"
                v-tippy
                @click="editTeam(team.row)"
              ></i>
              <i
                class="el-icon-delete fc-delete-icon visibility-hide-actions"
                data-arrow="true"
                :title="$t('common._common.delete')"
                v-tippy
                @click="deleteTeam(team.row.id)"
              ></i>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <NewTeamForm
      v-if="showTeamDialog"
      :visibility.sync="showTeamDialog"
      :isNew="isNew"
      :selectedTeam="selectedTeam"
      @closeDialog="closeDialog"
      @teamSaved="loadTeams"
    />
  </div>
</template>
<script>
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import SetupHeader from 'pages/setup/components/SetupHeader'
import { API } from '@facilio/api'
import Pagination from 'pages/setup/components/SetupPagination'
import NewTeamForm from './NewTeam'
import { isEmpty } from '@facilio/utils/validation'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import FTags from 'newapp/components/search/FTags'
import FSearch from '@/FSearch'
import { getFieldOptions } from 'util/picklist'

export default {
  name: 'TeamList',
  data() {
    return {
      showTeamDialog: false,
      selectedTeam: null,
      isLoading: false,
      teamsList: [],
      perPage: 50,
      teamsCount: null,
      teamPage: 1,
      isNew: true,
      teamFilterList: [],
      teamQuickSearch: null,
      sites: [],
    }
  },
  components: {
    SetupEmpty,
    SetupHeader,
    SetupLoader,
    Pagination,
    NewTeamForm,
    AdvancedSearch,
    FTags,
    FSearch,
  },
  computed: {
    disableAction() {
      let { teamsList } = this
      return isEmpty(teamsList) ? 'disable-actions' : ''
    },
    moduleName() {
      return 'peopleGroup'
    },
    moduleDisplayName() {
      return 'Teams'
    },
    tableHeight() {
      let { teamFilterList } = this
      return !isEmpty(teamFilterList)
        ? 'calc(100vh - 280px)'
        : 'calc(100vh - 210px)'
    },
    resourceEmpty() {
      let { teamFilterList } = this
      return !isEmpty(teamFilterList)
        ? 'resource-filter-applied-empty'
        : 'resource-empty'
    },
    disableQuickSearch() {
      let { teamFilterList } = this
      return !isEmpty(teamFilterList) ? 'disable-search-actions' : ''
    },
    disableAdvancedSearch() {
      let { teamQuickSearch } = this
      return !isEmpty(teamQuickSearch) ? 'disable-search-actions' : ''
    },
  },
  created() {
    this.loadTeams()
  },
  watch: {
    teamPage: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          let { teamFilterList, teamQuickSearch } = this
          let filters = !isEmpty(teamFilterList)
            ? teamFilterList
            : teamQuickSearch
          if (!isEmpty(filters)) this.loadTeams({ filters })
          else this.loadTeams()
        }
      },
      immediate: true,
    },
  },
  methods: {
    async loadTeams(props) {
      this.isLoading = true
      let { filters } = props || {}
      let { teamPage, perPage, moduleName } = this
      let params = { page: teamPage, perPage, withCount: true }
      if (!isEmpty(filters)) {
        params = { ...params, filters }
        params['filters'] = JSON.stringify(filters)
      }
      let { list, error, meta } = await API.fetchAll(moduleName, params)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.teamsList = list || []
        let { pagination } = meta || {}
        this.teamsCount = this.$getProperty(pagination, 'totalCount', null)
        await this.getSiteList()
      }
      this.isLoading = false
    },
    getTeamDetail(team, property) {
      let { row } = team || {}
      return this.$getProperty(row, property, '---')
    },
    addTeam() {
      this.isNew = true
      this.showTeamDialog = true
    },
    editTeam(team) {
      this.isNew = false
      this.showTeamDialog = true
      this.selectedTeam = team
    },
    setPage(page) {
      this.teamPage = page
    },
    closeDialog(canClose) {
      if (canClose) {
        this.showTeamDialog = false
        this.selectedTeam = null
      }
    },

    async getSiteList() {
      let { teamsList } = this
      let defaultIds = []
      teamsList.forEach(team => {
        if (team.siteId) {
          defaultIds.push(team.siteId)
        }
      })

      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'site' },
        defaultIds,
      })
      if (!error) {
        this.sites = options
      }
    },
    getSiteName(siteId) {
      let siteName = 'All Sites'
      let { sites = [] } = this
      if (siteId > 0) {
        let siteObj = sites.find(site => site.value === siteId) || {}
        let { label = 'All Sites' } = siteObj || {}
        siteName = label
      }
      return siteName
    },
    applyTeamFilters({ filters }) {
      this.teamFilterList = filters
      this.resetPage()
      this.loadTeams({ filters })
    },
    resetPage() {
      this.teamPage = 1
    },
    quickSearch(searchText) {
      this.resetPage()
      if (!isEmpty(searchText)) {
        let filters = { name: { operatorId: 5, value: [`${searchText}`] } }
        this.teamQuickSearch = filters
        this.loadTeams({ filters })
      } else {
        this.teamQuickSearch = null
        this.loadTeams()
      }
    },
    resetFilters() {
      this.teamFilterList = []
      this.resetPage()
      this.loadTeams()
    },
    saveTeam() {
      this.loadTeam()
      this.showTeamDialog = false
      this.selectedTeam = null
    },
    async updateTeamStatus(team) {
      let { moduleName } = this
      let { id } = team || {}
      let { error } = await API.updateRecord(moduleName, { id, data: team })

      if (!error) {
        this.loadTeams()
        this.$message.success(this.$t('setup.users_management.team_updated'))
      }
    },
    async deleteTeam(teamId) {
      let value = await this.$dialog.confirm({
        title: this.$t('setup.users_management._delete_team'),
        message: this.$t('setup.users_management.are_you_sure_delete_team'),
        rbDanger: true,
        rbLabel: 'Delete',
      })
      if (value) {
        let { moduleName } = this
        let { error } = await API.deleteRecord(moduleName, teamId)
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.resetPage()
          await this.loadTeams()
          this.$message.success(
            this.$t('setup.users_management.delete_team_successfully')
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
  .disable-actions {
    pointer-events: none !important;
  }
  .disable-search-actions {
    pointer-events: none !important;
    opacity: 0.5;
  }
}
</style>
