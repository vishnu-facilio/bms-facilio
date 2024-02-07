<template>
  <div>
    <div
      class="fc-portal-inner-summary-header flex-middle justify-content-space"
    >
      <div class="fc-black2-18 text-left bold">
        {{ $t('setup.users_management.users') }}
      </div>
      <div class="flex-middle">
        <pagination
          ref="pagination"
          :total="usersCount"
          :perPage="perPage"
          :currentPage.sync="page"
          class="flex-middle justify-content-end p0 pL10"
        >
        </pagination>
        <div
          class="fc-portal-filter-border"
          @click="showExtraFilter"
          :class="{ filterActive: showFilter }"
          v-tippy="{
            arrow: true,
            arrowType: 'round',
            animation: 'fade',
          }"
          content="Advanced filters"
        >
          <inline-svg
            class="pointer"
            src="svgs/dashboard/filter"
            iconClass="icon icon-sm-md fc-fill-path-grey"
          ></inline-svg>
          <div
            :class="{
              'dot-active-pink': querySearch,
            }"
          ></div>
        </div>
      </div>
    </div>
    <div v-if="showFilter" class="fc-show-filter-con flex-middle">
      <div class="relative">
        <div class="fc-black-13 text-left bold pB5">Search</div>
        <el-input
          placeholder="Search name or email"
          v-model="querySearch"
          ref="querySearch"
          class="fc-input-full-border2 fc-existing-user-search width250px"
          @change="searchLoadData"
          clearable
        >
        </el-input>
      </div>
    </div>

    <div class="occupantPortal-tab">
      <SetupLoader class="m10 width98" v-if="loading">
        <template #setupLoading>
          <spinner :show="loading" size="80"></spinner>
        </template>
      </SetupLoader>
      <setup-empty
        v-else-if="$validation.isEmpty(userlist) && !loading"
        class="m10 width98"
      >
        <template #emptyImage>
          <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
        </template>
        <template #emptyHeading>
          {{ $t('setup.empty.empty_user') }}
        </template>
      </setup-empty>
      <div class="occupantPortal-tabrow" v-else>
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table" width="100%">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('setup.approvalprocess.name') }}
                </th>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('setup.setup_profile.email') }}
                </th>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('common.roles.role') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody>
              <tr
                class="tablerow visibility-visible-actions"
                v-for="(user, index) in userlist"
                :key="index"
              >
                <td>
                  <user-avatar
                    size="md"
                    :user="user"
                    class="width200px"
                  ></user-avatar>
                </td>
                <td>
                  {{ user.email }}
                </td>
                <td>
                  {{ getRoleName(user) }}
                </td>
                <td class="pL0 pR0 nowrap">
                  <i
                    class="visibility-hide-actions el-icon-edit fc-setup-list-edit"
                    @click="editUser(user)"
                  ></i>
                  <i
                    class="visibility-hide-actions el-icon-delete fc-setup-list-delete"
                    @click="removeUser(user)"
                  ></i>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <portal to="header-buttons">
        <div class="position-relative">
          <el-button type="primary" @click="addUser" class="setup-el-btn">
            {{ $t('common.header.add_existing_user') }}
          </el-button>
        </div>
      </portal>
    </div>
    <AddExistingUser
      v-if="showDialog"
      :app="app"
      @close="showDialog = false"
      @saved="onSave"
    ></AddExistingUser>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import { mapGetters } from 'vuex'
import { API } from '@facilio/api'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import AddExistingUser from 'pages/setup/portal/ExistingUserForm'

export default {
  props: ['app'],
  components: {
    Pagination,
    UserAvatar,
    SetupLoader,
    SetupEmpty,
    AddExistingUser,
  },
  computed: {
    ...mapGetters(['getRoleNameById']),
  },
  data() {
    return {
      isNew: true,
      showDialog: false,
      showFilter: false,
      loading: true,
      userlist: [],
      usersCount: '',
      selectFiledSearch: 'name',
      currentUser: null,
      page: 1,
      perPage: 50,
      querySearch: null,
    }
  },
  async created() {
    await this.$store.dispatch('loadRoles')
    await this.loadUsersCount()
    await this.loadusers()
  },
  methods: {
    addUser() {
      this.isNew = true
      this.showDialog = true
    },
    async onSave() {
      await this.loadUsersCount()
      await this.loadusers()
    },
    searchLoadData() {
      this.page = 1
      this.perPage = 50
      this.loadusers(this.querySearch)
      this.loadUsersCount(this.querySearch)
    },
    async editUser(user) {
      this.isNew = false
      this.currentUser = user
      this.showDialog = true
    },
    async removeUser(user) {
      let { name } = user
      let value = await this.$dialog.confirm({
        title: this.$t('common.header.delete_user'),
        htmlMessage: this.$t('common._common.do_you_want_delete_user_name', {
          name,
        }),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })

      if (!value) return

      const formData = new FormData()
      formData.append('user.uid', user.uid)
      formData.append('user.ouid', user.ouid)

      let { error } = await API.post('/setup/deleteUser', formData)

      if (error) {
        this.$message.error(error.message)
      } else {
        this.$message.success(this.$t('common._common.delete_success'))
        await this.loadusers()
        await this.loadUsersCount()
      }
    },
    getRoleName(user) {
      let roleId = user.roleId
      return roleId ? this.getRoleNameById(roleId) : '---'
    },
    showExtraFilter() {
      this.showFilter = !this.showFilter
    },
    loadUsersCount(search) {
      let { app } = this
      API.get(
        `/v2/application/users/list?appId=${app.id}&page=${this.page}&perPage=${this.perPage}&fetchCount=true&inviteAcceptStatus=true`,
        {
          search,
        }
      ).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.usersCount = data.count
        }
      })
    },
    loadusers(search) {
      let { app } = this
      let appId = app.id
      this.loading = true
      API.get(
        `/v2/application/users/list?page=${this.page}&perPage=${this.perPage}`,
        { appId, search }
      ).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.userlist = data.users || []
        }
        this.loading = false
      })
    },
  },
}
</script>
