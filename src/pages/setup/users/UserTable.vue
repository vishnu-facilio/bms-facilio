<template>
  <div>
    <portal to="#usertable-search-and-pagination">
      <div class="fc-search-and-pagination">
        <pagination
          :total="userCount"
          :currentPage.sync="page"
          :perPage="perPage"
          class="nowrap pT5"
          ref="page"
        ></pagination>
        <div
          class="fc-portal-filter-border"
          @click="showExtraFilter"
          :class="{ filterActive: showFilter }"
          v-tippy="{
            arrow: true,
            arrowType: 'round',
            animation: 'fade',
          }"
          :content="$t('common._common.advanced_filters')"
        >
          <inline-svg
            class="pointer"
            src="svgs/dashboard/filter"
            iconClass="icon icon-sm-md fc-fill-path-grey"
          ></inline-svg>
          <div
            :class="{
              'dot-active-pink': isFilterActive,
            }"
          ></div>
        </div>
      </div>
    </portal>

    <portal to="#user-table-extra-filter">
      <div class="fc-search-type" v-if="showFilter">
        <div class="flex-middle">
          <div class="relative mR20">
            <div class="fc-black-13 text-left bold pB5">
              {{ $t('common._common.search') }}
            </div>
            <el-input
              placeholder="Search"
              v-model="activeUserSearchData"
              @keyup.native.enter="refreshList"
              class="fc-input-full-border2 width250px"
              @clear="refreshList"
              clearable
            ></el-input>
          </div>

          <div v-if="inviteAcceptStatus">
            <div class="fc-black-13 text-left bold pB5">
              {{ $t('common._common.type') }}
            </div>
            <el-select
              placeholder="Select"
              @change="refreshList"
              v-model="activeSubTab"
              class="fc-input-full-border-select2"
              filterable
            >
              <el-option
                v-for="(tab, inex) in tabList"
                :key="inex"
                :label="tab.label"
                :value="tab.value"
              >
              </el-option>
            </el-select>
          </div>
        </div>
      </div>
    </portal>

    <setup-loader
      v-if="loading"
      :class="['shadow-none', this.tableHeightClass]"
    >
      <template #setupLoading>
        <spinner :show="loading" size="80"></spinner>
      </template>
    </setup-loader>
    <setup-empty
      v-else-if="$validation.isEmpty(userlist) && !loading"
      :class="['shadow-none', this.tableHeightClass]"
    >
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('setup.empty.empty_user') }}
      </template>
      <template #emptyDescription> </template>
    </setup-empty>
    <el-table
      v-else
      :data="userlist"
      class="width100 fc-setup-table-th-borderTop fc-setup-table fc-setup-table-p0"
      :height="tableHeight"
      :fit="true"
      :header-cell-style="{ background: '#f3f1fc' }"
    >
      <el-table-column prop="name" :label="$t('common.roles.name')">
        <template v-slot="userData">
          <div @click="openSummary(userData.row)" class="fc-user-list-hover">
            <user-avatar size="md" :user="userData.row"></user-avatar>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="email"
        :label="$t('common.header._email')"
      ></el-table-column>
      <el-table-column
        prop="role.name"
        :label="$t('common.wo_report.role')"
        width="200"
      ></el-table-column>
      <el-table-column :label="$t('maintenance._workorder.status')" width="150">
        <template v-if="inviteAcceptStatus" v-slot="userData">
          <el-switch
            v-model="userData.row.userStatus"
            :disabled="userData.row.role.name === 'Super Administrator'"
            data-arrow="true"
            :title="$t('setup.users_management.user_status')"
            v-tippy
            @change="changeStatus(userData.row)"
            active-color="rgba(57, 178, 194, 0.8)"
            inactive-color="#e5e5e5"
          ></el-switch>
        </template>
        <template v-else v-slot="userData">
          <el-button
            @click="reinvite(userData.row)"
            type="text"
            class="pT0 pB0"
          >
            {{ pendingUserStatus(userData) }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column v-if="inviteAcceptStatus" width="150">
        <template v-slot="userData">
          <div
            v-if="userData.row.role.name !== 'Super Administrator'"
            class="visibility-visible-actions justify-content-even flex-middle"
          >
            <i
              v-if="userData.row.userStatus"
              class="fa fa-unlock reset-icon visibility-hide-actions"
              data-arrow="true"
              :title="$t('setup.users_management.send_reset_pw')"
              v-tippy
              @click="reset(userData.row)"
            ></i>
            <i
              class="el-icon-delete visibility-hide-actions delete-icon"
              data-arrow="true"
              :title="$t('setup.users_management.delete_user')"
              v-tippy
              @click="deleteUser(userData.row)"
            ></i>
            <i
              class="el-icon-edit visibility-hide-actions"
              data-arrow="true"
              :title="$t('setup.users_management.edit_user')"
              v-tippy
              @click="editUser(userData.row)"
            ></i>
          </div>
        </template>
      </el-table-column>
      <el-table-column v-else width="150">
        <template v-slot="userData">
          <div class="visibility-visible-actions">
            &nbsp;&nbsp;
            <i
              class="el-icon-delete visibility-hide-actions delete-icon"
              data-arrow="true"
              :title="$t('common.header.delete_user')"
              v-tippy
              @click="deleteUser(userData.row)"
            ></i>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <edit-user
      v-if="showDialog"
      :user="editedUser"
      :isNew="false"
      :appId="appId"
      :applications="selectedApp"
      @save="loadUserData"
      @close="showDialog = false"
    ></edit-user>
  </div>
</template>

<script>
import EditUser from 'pages/setup/users/EditUser'
import { API } from '@facilio/api'
import Pagination from 'pages/setup/components/SetupPagination'
import UserAvatar from '@/avatar/User'
import { isEmpty } from '@facilio/utils/validation'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'

export default {
  props: ['appId', 'selectedApp'],
  data() {
    return {
      userlist: [],
      showDialog: false,
      loading: true,
      editedUser: null,
      sendingUserId: -1,
      perPage: 50,
      userCount: null,
      activeUserSearchData: '',
      showFilter: false,
      activeSubTab: 'all',
      tabList: [
        {
          label: 'All',
          value: 'all',
        },
        {
          label: 'Active Users',
          value: 'activeUsers',
        },
        {
          label: 'Inactive Users',
          value: 'inActiveUsers',
        },
      ],
    }
  },
  components: {
    EditUser,
    Pagination,
    UserAvatar,
    SetupLoader,
    SetupEmpty,
  },
  computed: {
    activeTab() {
      return this.$route.name === 'users' ? 'user' : 'pending'
    },
    tableHeight() {
      return this.showFilter ? 'calc(100vh - 310px)' : 'calc(100vh - 210px)'
    },
    tableHeightClass() {
      return this.showFilter ? 'height-with-filter' : 'height-without-filter'
    },
    isFilterActive() {
      return this.activeUserSearchData || this.activeSubTab != 'all'
    },
    page() {
      return this.$route.query.page || 1
    },
    inviteAcceptStatus() {
      return this.activeTab === 'user'
    },

    userStatus() {
      let { activeTab, activeSubTab } = this
      return (activeTab === 'user' && activeSubTab === 'all') ||
        activeTab === 'pending'
        ? null
        : activeSubTab === 'activeUsers'
    },
  },
  watch: {
    activeTab: {
      handler() {
        this.changeTab()
      },
    },
    page() {
      this.loadUserData()
    },
    appId: {
      handler(newVal) {
        if (!isEmpty(newVal)) this.loadUserData()
      },
      immediate: true,
    },
  },
  methods: {
    changeTab() {
      this.userSearchIcon = true
      this.activeSubTab = 'all'
      this.refreshList()
    },
    pendingUserStatus(userData) {
      let { sendingUserId } = this
      return sendingUserId === userData?.row?.id
        ? this.$t('setup.users_management.sending')
        : this.$t('setup.users_management.reinvite')
    },
    showExtraFilter() {
      this.showFilter = !this.showFilter
    },
    reinvite(user) {
      this.sendingUserId = user.id
      API.post('setup/resendinvite', { userId: user.id, isPortal: false }).then(
        ({ error }) => {
          if (error) {
            this.sendingUserId = -1
            this.$message.error(this.$t('setup.users_management.error_occured'))
          } else {
            this.$message.success(
              this.$t('setup.users_management.invitation_send_success')
            )
            this.sendingUserId = -1
            this.$store.dispatch('loadUsers', true)
          }
        }
      )
    },
    async loadUsersCount() {
      let {
        appId,
        page,
        perPage,
        inviteAcceptStatus,
        userStatus,
        activeUserSearchData: search,
      } = this
      let { data, error } = await API.get('/v2/application/users/list', {
        page,
        perPage,
        inviteAcceptStatus,
        userStatus,
        appId: appId,
        fetchCount: true,
        search: !isEmpty(search) ? search : null,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.userCount = data.count
      }
    },
    editUser(user) {
      this.editedUser = user
      this.showDialog = true
    },
    async changeStatus(userinfo) {
      let { uid, ouid, userStatus } = userinfo || {}
      let params = { user: { uid, ouid, userStatus } }
      let { error } = await API.post('/setup/changestatus', params)

      if (error) {
        this.$message.error(this.$t('setup.users_management.error_occured'))
      } else {
        if (userStatus) {
          this.$message.success(
            this.$t('setup.users_management.user_activated')
          )
        } else {
          this.$message.success(
            this.$t('setup.users_management.user_deactivated')
          )
        }
        this.$store.dispatch('loadUsers', true)
        this.loadUserData()
      }
    },
    async loadusers() {
      this.loading = true
      let {
        appId,
        page,
        perPage,
        inviteAcceptStatus,
        userStatus,
        activeUserSearchData: search,
      } = this
      let { data, error } = await API.get('/v2/application/users/list', {
        page,
        perPage,
        inviteAcceptStatus,
        userStatus,
        appId,
        search: !isEmpty(search) ? search : null,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.userlist = data.users || []
      }
      this.loading = false
    },
    refreshList() {
      this.$router.push({
        query: {},
      })
      this.loadUserData()
    },
    loadUserData() {
      this.loadUsersCount()
      this.loadusers()
    },

    reset(user) {
      API.post('/fresetPassword', { emailaddress: user.email }).then(
        ({ data, error }) => {
          if (error) {
            this.$dialog.notify(
              this.$t('setup.users_management.pw_reset_not_send')
            )
          } else {
            this.resetDone = true
            if (data?.invitation?.status === 'success') {
              this.$dialog.notify(
                this.$t('setup.users_management.pw_reset_link')
              )
              this.$store.dispatch('loadUsers', true)
            } else {
              this.$dialog.notify(
                this.$t('setup.users_management.pw_reset_not_send')
              )
            }
          }
        }
      )
    },

    async deleteUser(user) {
      let { uid, ouid, name, id } = user || {}
      let params = { user: { uid, ouid } }
      let value = await this.$dialog.confirm({
        title: this.$t('setup.users_management.delete_user_msg'),
        message: `${this.$t(
          'setup.users_management.delete_user_msg2'
        )} User ${name} ?`,
        rbDanger: true,
        rbLabel: this.$t('setup.users_management.delete'),
      })

      if (!value) return

      let { error } = await API.post('/setup/deleteUser', params)

      if (error) {
        this.$message.error(this.$t('setup.users_management.unable_to_delete'))
      } else {
        this.$message.success(
          this.$t('setup.users_management.user_delete_success')
        )

        let deletingIndex = this.userlist.findIndex(user => user.id === id)

        if (deletingIndex !== -1) {
          this.userlist.splice(deletingIndex, 1)
          this.userCount--
        }
        this.$store.dispatch('loadUsers', true)
      }
    },

    openSummary(user) {
      this.$router.push({
        name: 'userSummary',
        params: {
          id: user.id,
          appId: this.appId,
        },
      })
    },
  },
}
</script>
<style lang="scss" scoped>
.fc-user-list-hover {
  .q-item-label {
    &:hover {
      color: #46a2bf;
      text-decoration: underline;
    }
  }
}
.fc-search-and-pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.height-with-filter {
  height: calc(100vh - 350px);
}
.height-without-filter {
  height: calc(100vh - 250px);
}
.fc-search-type {
  border-top: 1px solid rgba(230, 233, 242, 0.6);
  padding: 20px;
  width: 100%;
}
</style>
