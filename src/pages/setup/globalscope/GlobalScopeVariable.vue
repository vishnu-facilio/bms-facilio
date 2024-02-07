<template>
  <div class="fc-email-template-page">
    <SetupHeader>
      <template #heading>
        {{ $t('setup.globalscoping.globalscoping') }}
      </template>
      <template #description>
        {{ $t('setup.globalscoping.description') }}
      </template>
      <template #tabs>
        <div class="flex-middle p20 justify-content-space">
          <div>
            <el-select
              v-if="canShowApps"
              v-model="applicationId"
              :placeholder="$t('setup.globalscoping.selectapp')"
              filterable
              @change="setApplication"
              class="fc-input-full-border2"
            >
              <el-option-group
                v-for="(applicationList, key) in applicationGroups"
                :key="key"
                :label="key"
              >
                <el-option
                  v-for="app in applicationList"
                  :key="app.linkName"
                  :label="app.name"
                  :value="app.id"
                >
                </el-option>
              </el-option-group>
            </el-select>
          </div>
          <div class="flex-middle">
            <template v-if="showSearchIcon">
              <div
                @click="searchShowHide"
                v-if="searchHide"
                class="pointer mR15 fc-portal-filter-border"
                v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
                :content="$t('setup.globalscoping.search')"
              >
                <i class="el-icon-search fc-black-14 text-right fwBold"></i>
              </div>
              <div class="relative mR15" v-if="searchShow">
                <input
                  :placeholder="$t('setup.globalscoping.searchsubject')"
                  v-model="querySearch"
                  ref="querySearch"
                  class="fc-input-full-border2 fc-log-search width250px"
                  @keyup.enter="searchLoadData(true)"
                />
                <i
                  class="el-icon-close fc-search-close fc-black-14 text-right fwBold"
                  @click="searchClose"
                ></i>
              </div>
              <span class="separator">|</span>
            </template>
            <div>
              <pagination
                :pageNo="page"
                :total="totalCount"
                :perPage="20"
                class="nowrap"
                @onPageChanged="setPage"
              >
              </pagination>
            </div>
            <el-button
              type="primary"
              class="setup-el-btn mL20"
              @click="addGlobalScope"
            >
              {{ $t('setup.globalscoping.add_global_scope') }}
            </el-button>
          </div>
        </div>
      </template>
    </SetupHeader>

    <setup-loader v-if="loading">
      <template #setupLoading>
        <spinner :show="loading" size="80"></spinner>
      </template>
    </setup-loader>
    <setup-empty
      v-else-if="$validation.isEmpty(globalScopeVariableList) && !loading"
    >
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('setup.globalscoping.no_global_scope') }}
      </template>
    </setup-empty>
    <el-table
      v-else
      :data="globalScopeVariableList"
      class="width100 fc-setup-table fc-setup-table-th-borderTop"
      height="calc(100vh - 225px)"
      :header-cell-style="{ background: '#f7faff' }"
      :fit="true"
    >
      <el-table-column :label="$t('setup.globalscoping.name')">
        <template v-slot="scope">
          <div
            class="textoverflow-ellipsis"
            :title="getValue(scope.row, 'displayName')"
            v-tippy
          >
            {{ getValue(scope.row, 'displayName') }}
          </div>
        </template>
      </el-table-column>
      <el-table-column :label="$t('setup.globalscoping.scopemodule')">
        <template v-slot="scope">
          {{ getValue(scope.row, 'applicableModuleDisplayName') }}
        </template>
      </el-table-column>
      <el-table-column :label="$t('setup.globalscoping.valuegenerator')">
        <template v-slot="scope">
          <div
            v-if="scope.row.typeEnum === 'SCOPED'"
            class="textoverflow-ellipsis"
            :title="getValue(scope.row, 'valueGeneratorDisplayName')"
          >
            {{ getValue(scope.row, 'valueGeneratorDisplayName') }}
          </div>
          <div v-else>
            {{ $t('setup.globalscoping.all') }}
          </div>
        </template>
      </el-table-column>
      <el-table-column :label="$t('setup.globalscoping.isswitch')">
        <template v-slot="scope">
          <div class="textoverflow-ellipsis">
            {{ scope.row.showSwitch ? 'Yes' : 'No' }}
          </div>
        </template>
      </el-table-column>
      <el-table-column>
        <template v-slot="scope">
          <el-switch
            v-model="scope.row.status"
            @change="changeScopeStatus(scope.row)"
            class="Notification-toggle"
            active-color="#3ab2c2"
            inactive-color="#e5e5e5"
          >
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column>
        <template v-slot="scope">
          <div class="width50px flex-middle justify-content-space">
            <i
              @click="editGlobalScope(scope.row)"
              class="el-icon-edit fc-black2-18 pointer visibility-hide-actions mL10"
              data-arrow="true"
              :title="$t('common._common.edit')"
              v-tippy
            ></i>
            <i
              @click="deleteGlobalScopeVariable(scope.row)"
              class="el-icon-delete fc-black2-18 pointer visibility-hide-actions mL10"
              data-arrow="true"
              :title="$t('common._common.delete')"
              v-tippy
            ></i>
            <Popover
              :key="'popover-' + scope.row.id"
              :data="scope.row"
              @refresh="data => fetchGlobalScopeVariables(true)"
            ></Popover>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import SetupHeader from 'pages/setup/components/SetupHeaderTabs'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'
import { getFilteredApps, loadApps } from 'util/appUtil'
import Popover from './Popover'
import { isEmpty } from '@facilio/utils/validation'
import Pagination from 'src/components/list/FPagination'

const domainEnumVsName = {
  FACILIO: 'Facilio',
  SERVICE_PORTAL: 'Occupant Portal',
  TENANT_PORTAL: 'Tenant Portal',
  VENDOR_PORTAL: 'Vendor Portal',
  CLIENT_PORTAL: 'Client Portal',
  DEVELOPER: 'Developer',
  EMPLOYEE_PORTAL: 'Employee Portal',
}

export default {
  data() {
    return {
      globalScopeVariableList: [],
      loading: true,
      applications: [],
      applicationGroups: [],
      applicationId: null,
      totalCount: 0,
      searchHide: true,
      searchShow: false,
      querySearch: null,
      page: 1,
    }
  },
  title() {
    return this.$t('setup.globalscoping.scoping')
  },
  components: {
    SetupLoader,
    SetupEmpty,
    SetupHeader,
    Popover,
    Pagination,
  },
  created() {
    this.loadApps().then(() => {
      this.fetchGlobalScopeVariables()
    })
  },
  computed: {
    appId() {
      if (this.$route.params.appId) {
        return parseInt(this.$route.params.appId)
      }
      return -1
    },
    canShowApps() {
      let { applicationGroups } = this
      return !isEmpty(applicationGroups)
    },
    selectedApp() {
      return this.applications.find(app => app.id === this.applicationId)
    },
    showSearchIcon() {
      let { globalScopeVariableList, querySearch } = this
      if (!isEmpty(querySearch)) {
        return true
      }
      return !isEmpty(globalScopeVariableList)
    },
  },
  watch: {
    page(newVal, oldVal) {
      if (oldVal != newVal) {
        this.fetchGlobalScopeVariables()
      }
    },
  },
  methods: {
    setApplication() {
      this.fetchGlobalScopeVariables()
      let { applicationId } = this
      this.$router.replace({
        name: 'globalscopelist',
        params: {
          appId: applicationId,
        },
      })
    },
    getValue(data, key) {
      return data[key] ? data[key] : '---'
    },
    setPage(page) {
      this.page = page
      this.fetchGlobalScopeVariables()
    },
    searchLoadData() {
      this.page = 1
      this.totalCount = 0
      this.fetchGlobalScopeVariables()
    },
    searchShowHide() {
      this.searchHide = false
      this.searchShow = true
    },
    searchClose() {
      this.searchHide = true
      this.searchShow = false
      this.querySearch = null
      this.totalCount = 0
      this.page = 1
      this.fetchGlobalScopeVariables()
    },
    async loadApps() {
      let { error, data } = await loadApps()
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.applications = getFilteredApps(data)
        this.constructAppGroups(this.applications)
      }
    },
    async changeScopeStatus(data) {
      let { id, status } = data || {}
      let { error } = await API.post(`/v3/scopeVariable/setStatus`, {
        id,
        status,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.$message.success('Status updated successfully')
      }
    },
    async deleteGlobalScopeVariable(data) {
      let value = await this.$dialog.confirm({
        title: this.$t('setup.globalscoping.dialogtitle'),
        message: this.$t('setup.globalscoping.dialogcontent'),
        rbDanger: true,
        rbLabel: this.$t('setup.delete.delete'),
      })
      if (value) {
        let { id } = data || {}
        this.loading = true
        let { error } = await API.post(`/v3/scopeVariable/delete`, {
          scopeVariable: { id },
        })
        if (error) {
          this.$message.error(error.message || 'Error Occurred')
        } else {
          this.$message.success('Deleted successfully')
        }
        await this.fetchGlobalScopeVariables()
        this.loading = false
      }
    },
    constructAppGroups(applications) {
      const { linkName: currentApp } = getApp()
      let appGroups = Object.values(applications).reduce((groups, app) => {
        if (app.linkName === currentApp) {
          let { appId } = this
          if (isEmpty(appId)) {
            this.applicationId = (app || {}).id
            this.replaceRouter()
          } else {
            this.applicationId = appId
          }
        }
        let { appDomain } = app || {}
        let { appDomainTypeEnum } = appDomain || {}
        let key = domainEnumVsName[appDomainTypeEnum]
        if (isEmpty(groups[key])) {
          groups[key] = []
        }
        groups[key].push(app)
        return groups
      }, {})
      if (isEmpty(this.applicationId)) {
        this.applicationId = (applications[0] || {}).id
      }
      this.applicationGroups = appGroups
    },
    replaceRouter() {
      let { appId } = this
      if (isEmpty(appId)) {
        let { applicationId } = this
        this.$router.replace({
          name: 'globalscopelist',
          params: {
            appId: applicationId,
          },
        })
      }
    },
    async fetchGlobalScopeVariables(force = true) {
      this.loading = true
      let { applicationId, page, querySearch } = this
      let { error, data, meta } = await API.get(
        `/v3/scopeVariable/list`,
        { appId: applicationId, perPage: 20, page, search: querySearch },
        { force }
      )
      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let { scopeVariable } = data || {}
        let { totalCount } = meta || {}
        this.globalScopeVariableList = scopeVariable
        this.totalCount = totalCount
      }
      this.loading = false
    },
    addGlobalScope() {
      let { applicationId } = this
      this.$router.push({
        name: 'globalscopenew',
        params: { applicationId },
      })
    },
    editGlobalScope(data) {
      let { applicationId } = this
      let { id } = data || {}
      this.$router.push({
        name: 'globalscopeedit',
        params: { applicationId, id },
      })
    },
  },
}
</script>
<style lang="scss" scoped>
.fc-log-search {
  height: 40px;
  line-height: 40px;
  padding-right: 30px;
  border-radius: 3px;
  background-color: #ffffff;
  border: solid 1px #d0d9e2;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.4px;
  color: #333333;
  font-size: 16px;
  padding-left: 15px !important;
  font-weight: 500;
  transition: 0.4s all;
}
.fc-search-close {
  position: absolute;
  right: 10px;
  top: 10px;
  background: #fff;
  cursor: pointer;
}
</style>
