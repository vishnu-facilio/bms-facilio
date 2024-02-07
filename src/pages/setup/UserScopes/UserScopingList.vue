<template>
  <div class="fc-email-template-page">
    <SetupHeader>
      <template #heading>
        {{ $t('setup.userScoping.user_scoping') }}
      </template>
      <template #description>
        {{ $t('setup.userScoping.user_scoping_desc') }}
      </template>
      <template #actions>
        <el-button
          type="primary"
          class="setup-el-btn mL20"
          @click="addUserCreation"
        >
          {{ $t('setup.setup.user_scoping') }}
        </el-button>
      </template>
      <template #filter>
        <div class="flex-middle">
          <div>
            <div class="fc-black-14 text-left bold pB10">
              Applications
            </div>
            <el-select
              v-if="canShowApps"
              v-model="appId"
              placeholder="Select App"
              filterable
              @change="userScopesListData"
              class="fc-input-full-border2"
            >
              <el-option
                v-for="app in applications"
                :key="app.linkName"
                :label="app.name"
                :value="app.id"
              >
              </el-option>
            </el-select>
          </div>
        </div>
      </template>
    </SetupHeader>

    <setup-loader v-if="loading">
      <template #setupLoading>
        <spinner :show="loading" size="80"></spinner>
      </template>
    </setup-loader>
    <setup-empty v-else-if="$validation.isEmpty(userScopeList) && !loading">
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('setup.userScoping.no_user_scopes') }}
      </template>
    </setup-empty>
    <el-table
      v-else
      :data="userScopeList"
      class="fc-setup-table-th-borderTop fc-setup-table fc-setup-table-p0 m10 fc-email-template-table"
      height="calc(100vh - 300px)"
      :fit="true"
    >
      <el-table-column label="name">
        <template v-slot="scope">
          <div @click="editUserScopes(scope.row)" class="fc-role-name">
            {{ scope.row.scopeName }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="Description">
        <template v-slot="scope">
          {{ scope.row.description ? scope.row.description : '---' }}
        </template>
      </el-table-column>
      <el-table-column label="Created time">
        <template v-slot="scope">
          <div v-if="scope.row.createdTime > -1">
            {{ scope.row.createdTime | formatDate(true) }}
          </div>
          <div v-else>
            ---
          </div>
        </template>
      </el-table-column>
      <el-table-column label="Is default">
        <template v-slot="scope">
          {{ scope.row.default ? scope.row.default : '---' }}
        </template>
      </el-table-column>
      <el-table-column class="visibility-visible-actions">
        <template v-slot="scope">
          <div class="flex-middle">
            <div
              class="mR10"
              v-tippy
              content="Clone"
              @click="addCloneUserScope(scope.row)"
            >
              <i
                class="el-icon-document-copy visibility-hide-actions f16 fc-portal-filter-border fc-icon-common-hover"
              ></i>
            </div>
            <i
              class="el-icon-delete visibility-hide-actions f16 fc-portal-filter-border mR10 fc-edit-setup-delete-hover"
              v-tippy
              content="Delete scope"
              @click="deleteScopes(scope.row)"
              v-if="scope.row.default === false"
            ></i>
            <i
              v-tippy
              content="Edit user scope"
              class="el-icon-edit visibility-hide-actions f16 fc-portal-filter-border fc-setup-edit-hover"
              @click="editCreationForm(scope.row)"
              v-if="scope.row.default === false"
            ></i>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <UserScopeAddForm
      :isNew="isNew"
      v-if="showUserScopeForm"
      @onClose="reloadUserScope"
      :userScopeData="selectedUserScopeData"
      :applicationId="appId"
    ></UserScopeAddForm>
    <userScopeCreation
      :isNew="isNew"
      v-if="showUserCreationForm"
      @onClose="showUserCreationForm = false"
      :applicationId="appId"
      @onSave="userScopesListData"
      :userScopeData="selectedUserScopeData"
    >
    </userScopeCreation>
  </div>
</template>
<script>
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import SetupHeader from 'pages/setup/components/SetupHeaderTabs'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'
import UserScopeAddForm from 'pages/setup/UserScopes/UserScopingAddOrEdit'
import userScopeCreation from 'pages/setup/UserScopes/UserScopeCreation'
import { getFilteredApps, loadApps } from 'util/appUtil'
export default {
  data() {
    return {
      loading: true,
      applications: [],
      appId: null,
      userScopeList: [],
      isNew: true,
      showUserScopeForm: false,
      selectedUserScopeData: null,
      showUserCreationForm: false,
    }
  },
  title() {
    return 'User Scoping'
  },
  components: {
    SetupLoader,
    SetupEmpty,
    SetupHeader,
    UserScopeAddForm,
    userScopeCreation,
  },
  created() {
    this.loadApps().then(() => {
      this.userScopesListData()
    })
  },
  computed: {
    canShowApps() {
      let { applications } = this
      return applications.length > 1
    },
    selectedApp() {
      return this.applications.find(app => app.id === this.appId)
    },
  },
  methods: {
    async loadApps() {
      let { error, data } = await loadApps()
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.applications = getFilteredApps(data)

        const { linkName: currentApp } = getApp()
        const selectedApp =
          this.applications.find(app => app.linkName === currentApp) ||
          this.applications[0]
        this.appId = (selectedApp || {}).id
      }
    },
    async userScopesListData(force = true) {
      this.loading = true
      let {
        error,
        data,
      } = await API.get(`v2/scoping/scopingList?appId=${this.appId}`, { force })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.userScopeList = data.scopingContextList
      }
      this.loading = false
    },
    deleteScopes(scope, force = false) {
      this.$dialog
        .confirm({
          title: this.$t('setup.userScoping.delete_user_scopes'),
          message: this.$t(
            'setup.userScoping.are_you_sure_you_want_to_delete_this_user'
          ),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(async value => {
          if (!value) return

          let { error } = await API.post(
            'v2/scoping/delete',
            {
              scopingId: scope.id,
            },
            {
              force,
            }
          )

          if (error) {
            this.$message.error(
              error.message || this.$t('setup.userScoping.delete_user_failed')
            )
          } else {
            this.$message.success(this.$t('setup.userScoping.delete_user'))
            this.$nextTick(() => {
              this.userScopesListData()
            })
          }
        })
    },
    addUserScopes() {
      this.showUserScopeForm = true
      this.isNew = true
      this.selectedUserScopeData = null
    },
    editUserScopes(user) {
      this.showUserScopeForm = true
      this.isNew = false
      this.selectedUserScopeData = user
    },
    editCreationForm(user) {
      this.showUserCreationForm = true
      this.isNew = false
      this.selectedUserScopeData = user
    },
    addUserCreation() {
      this.isNew = true
      this.selectedUserScopeData = null
      this.showUserCreationForm = true
    },
    async addCloneUserScope(user) {
      this.loading = true
      let url = 'v2/scoping/clone'
      let params = {
        scopingId: user.id,
      }
      let { error } = await API.post(url, params)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.$message.success(this.$t('setup.userScoping.clone_successfully'))
        this.$nextTick(() => {
          this.userScopesListData()
        })
      }
      this.loading = false
    },
    reloadUserScope() {
      this.showUserScopeForm = false
      this.userScopesListData()
    },
  },
}
</script>
<style lang="scss">
.fc-email-template-page {
  .setup-header-component {
    position: sticky;
    top: 0;
  }
  .fc-setup-empty,
  .fc-setup-loader {
    margin: 10px;
  }
}
// .fc-email-template-table {
//   height: calc(100vh - 300px);
//   padding-bottom: 100px;
// }
</style>
