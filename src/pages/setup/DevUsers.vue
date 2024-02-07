<template>
  <div class="mT20">
    <template>
      <div>
        <el-radio-group v-model="activeTab" size="small" class="user-tabs">
          <el-radio-button
            :label="$t('common.products.developers')"
          ></el-radio-button>
        </el-radio-group>
        <div class="fR fc-subheader-right">
          <f-search
            v-if="userlist != ''"
            ref="search"
            style="float: left;margin-top:12px"
            class="portal-user-search"
            :remote="true"
            @search="searchUsers"
          ></f-search>
          <pagination
            ref="pagination"
            style="float: right;margin-top:10px"
            :total="usersCount"
            :perPage="perPage"
            :currentPage.sync="page"
          >
          </pagination>
        </div>
      </div>
      <div v-if="loading">
        <spinner
          :show="loading"
          size="80"
          class="flex-middle height450"
        ></spinner>
      </div>
      <div
        v-else
        class="occupantPortal-tabrow setting-Rlayout pL0 pR0 mT20 pB40"
      >
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
                  {{ $t('setup.setup_profile.phone') }}
                </th>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('common.roles.role') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="$validation.isEmpty(userlist)">
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('setup.setup.no_developers') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow visibility-visible-actions"
                v-for="(user, index) in userlist"
                :key="index"
              >
                <td>
                  <user-avatar size="md" :user="user"></user-avatar>
                </td>
                <td>{{ user.email }}</td>
                <td>
                  {{ user.phone ? user.phone : '---' }}
                </td>
                <td>
                  {{ getRoleName(user) }}
                </td>
                <td class="d-flex pT20">
                  <i
                    class="visibility-hide-actions el-icon-edit"
                    @click="editUser(user)"
                  ></i>
                  <i
                    class="visibility-hide-actions el-icon-delete pL10"
                    @click="removeUser(user)"
                  ></i>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import { API } from '@facilio/api'
import { mapGetters } from 'vuex'
import FSearch from '@/FSearch'
import Pagination from 'pageWidgets/utils/WidgetPagination'
export default {
  props: ['app'],
  components: {
    FSearch,
    Pagination,
    UserAvatar,
  },
  data() {
    return {
      activeTab: this.$t('common.products.developers'),
      loading: true,
      userlist: [],
      usersCount: '',
      currentUser: null,
      page: 1,
      perPage: 50,
    }
  },
  async created() {
    await this.$store.dispatch('loadRoles')
    await this.loadUsersCount()
    await this.loadusers()
  },
  computed: {
    ...mapGetters(['getRoleNameById']),
  },
  watch: {
    page(newVal, oldVal) {
      if (oldVal != newVal) {
        this.loadusers()
        this.loadUsersCount()
      }
    },
  },
  methods: {
    getRoleName(user) {
      let roleId = user.roleId
      return roleId ? this.getRoleNameById(roleId) : '---'
    },
    searchUsers(search) {
      this.page = 1
      this.loadUsersCount(search)
      this.loadusers(search)
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
          this.userCount = data.count
        }
      })
    },
    loadusers(search) {
      let { app } = this
      let appId = app.id
      this.loading = true
      API.get(
        `/v2/application/users/list?page=${this.page}&perPage=${this.perPage}&inviteAcceptStatus=true`,
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
    async editUser(user) {
      this.$emit('onEditUser', user)
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
  },
}
</script>
<style lang="scss">
.user-tabs {
  .el-radio-button {
    .el-radio-button__inner {
      background: none;
      border: none;
      box-shadow: none;
      padding: 0 20px;
      height: 36px;
      line-height: 38px;
      display: inline-block;
      position: relative;
      font-weight: 500;
      font-size: 11px;
      color: #324056;
      text-transform: uppercase;
    }
    &.is-active .el-radio-button__inner {
      font-weight: bold;
      color: #385571;
    }
    &.is-active::after {
      content: '';
      position: absolute;
      width: 40%;
      bottom: 0;
      left: 20px;
      border: 1px solid #ee518f;
    }
  }
}
.portal-user-search .f-quick-search-input {
  margin-top: -5px;
}
</style>
