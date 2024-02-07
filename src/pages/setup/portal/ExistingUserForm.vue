<template>
  <el-dialog
    v-if="canShowUsersList"
    :visible="true"
    width="50%"
    :before-close="close"
    class="fc-dialog-center-container fc-dialog-center-body-p0 scale-up-center fc-dialog-header-hide"
    :append-to-body="true"
  >
    <div class="new-header-container pL20 pR0 height60px line-height30">
      <div class="new-header-modal">
        <div class="new-header-text flex-middle justify-content-space height30">
          <div class="setup-modal-title">
            {{ $t('common._common.add_existing_user') }}
          </div>
          <div class="fR fc-subheader-right flex-middle">
            <!-- <f-search
              class="fc-app-user-search"
              ref="search"
              :remote="true"
              @search="searchUsers"
            ></f-search> -->
            <div class="flex-middle position-relative mR10">
              <i
                class="el-icon-search pointer fw6 fc-black3-16"
                @click="showUserSearch"
                v-if="userSearchIcon"
              ></i>
              <div v-if="showUserSearchInput" class="flex-middle">
                <el-input
                  placeholder="Search"
                  v-model="activeUserSearchData"
                  @change="activeUserSearch"
                  class="fc-input-full-border2"
                ></el-input>
                <div>
                  <i
                    class="el-icon-close fc-close-icon-search pointer"
                    @click="clearUser"
                  ></i>
                </div>
              </div>
            </div>
            <pagination
              ref="pagination"
              :total="userListCount"
              :perPage="50"
              :currentPage.sync="page"
            >
            </pagination>
          </div>
        </div>
      </div>
    </div>
    <div v-if="loading" class="flex-middle height450">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div
      v-if="$validation.isEmpty(userList) && !loading"
      class="height450 flex-middle justify-content-center flex-direction-column"
    >
      <inline-svg
        src="svgs/emptystate/readings-empty"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="nowo-label">
        {{ $t('common._common.no_users_available') }}
      </div>
    </div>
    <div
      v-if="!loading && !$validation.isEmpty(userList)"
      style="padding-bottom: 52px;"
    >
      <el-table
        :data="userList"
        style="width: 100%;"
        height="450"
        ref="elTable"
        class="fc-table-th-pLalign-reduce"
      >
        <el-table-column
          prop="name"
          :label="$t('common.products.name')"
          width="270"
        >
          <template v-slot="data">
            <el-radio
              :label="data.row"
              v-model="selectedUser"
              class="fc-radio-btn"
            >
              {{ data.row.name }}
            </el-radio>
          </template>
        </el-table-column>
        <el-table-column
          prop="email"
          :label="$t('common.header.email')"
          width="250"
        >
          <template v-slot="data">
            {{ data.row.email }}
          </template>
        </el-table-column>
        <el-table-column
          prop="phone"
          :label="$t('common.header.phone')"
          width="200"
        >
          <template v-slot="data">
            {{ data.row.phone ? data.row.phone : '---' }}
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="dialog-footer">
      <div class="modal-dialog-footer">
        <el-button @click="close()" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button @click="next()" type="primary" class="modal-btn-save">
          {{ $t('common._common.next') }}
        </el-button>
      </div>
    </div>
  </el-dialog>
  <el-dialog
    v-else-if="canShowRolesList"
    :visible="true"
    width="35%"
    :before-close="close"
    class="fc-dialog-center-container fc-dialog-center-body-p0 scale-up-center fc-dialog-header-hide"
    :append-to-body="true"
  >
    <div class="p30 pB100">
      <p class="fc-input-label-txt pB10">
        {{ $t('common.products.user') }}
      </p>
      <el-select
        v-model="selectedUser.id"
        class="fc-input-full-border-select2 width100"
        disabled
      >
        <el-option
          v-for="user in userList"
          :key="user.id"
          :label="user.name"
          :value="user.id"
        >
        </el-option>
      </el-select>
      <p class="fc-input-label-txt pT20 pB10">
        {{ $t('common.wo_report.role') }}
      </p>
      <el-select
        v-model="selectedRoleId"
        class="fc-input-full-border-select2 width100"
        filterable
        clearable
      >
        <el-option
          v-for="role in rolesList"
          :key="role.roleId"
          :label="role.name"
          :value="role.roleId"
        >
        </el-option>
      </el-select>
    </div>
    <div class="dialog-footer">
      <div class="modal-dialog-footer">
        <el-button @click="back()" class="modal-btn-cancel">{{
          $t('common.header.back')
        }}</el-button>
        <el-button
          :loading="saving"
          :disabled="$validation.isEmpty(selectedRoleId)"
          type="primary"
          class="modal-btn-save"
          @click="saveRecord()"
        >
          {{ saving ? $t('common.products.adding') : $t('common._common.add') }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import FSearch from '@/FSearch'
import { API } from '@facilio/api'
import Pagination from 'pageWidgets/utils/WidgetPagination'
export default {
  props: ['app'],
  data() {
    return {
      saving: false,
      userListCount: '',
      loading: true,
      userList: [],
      selectedUser: null,
      selectedRoleId: null,
      canShowUsersList: true,
      canShowRolesList: false,
      page: 1,
      showUserSearchInput: false,
      activeUserSearchData: '',
      userSearchIcon: true,
    }
  },
  async created() {
    this.loading = true
    await this.loadApplicationUserCount()
    await this.loadApplicationUser()
    await this.loadRolesForApp()
    this.loading = false
  },
  components: {
    FSearch,
    Pagination,
  },
  computed: {
    appId() {
      return this.app.id
    },
  },
  watch: {
    page(newVal, oldVal) {
      if (oldVal != newVal) {
        this.loadApplicationUser()
      }
    },
  },
  methods: {
    async loadApplicationUserCount() {
      // let userFilters = {}
      // if (searchQuery) {
      //   userFilters.name = { operatorId: 5, value: [searchQuery] }
      // }
      let search
      if (this.activeUserSearchData) {
        search = this.activeUserSearchData
      }
      API.get(`/v2/application/users/list?fetchCount=true`, {
        appId: this.appId || this.$route.params.id,
        fetchNonAppUsers: true,
        search,
      }).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.userListCount = data.count
        }
      })
    },
    async loadApplicationUser() {
      // let userFilters = {}
      // if (searchQuery) {
      //   userFilters.name = { operatorId: 5, value: [searchQuery] }
      // }
      let search
      if (this.activeUserSearchData) {
        search = this.activeUserSearchData
      }
      this.loading = true
      let { error, data } = await API.get(
        `/v2/application/users/list?page=${this.page}&perPage=50`,
        {
          appId: this.appId || this.$route.params.id,
          fetchNonAppUsers: true,
          search,
        }
      )

      if (error) {
        this.userList = []
      } else {
        this.userList = data.users || []
        this.userList = [
          ...new Map(this.userList.map(user => [user['uid'], user])).values(),
        ]
      }
      this.loading = false
    },
    activeUserSearch() {
      this.loadApplicationUserCount()
      this.loadApplicationUser()
      this.page = 1
    },
    searchUsers(searchQuery) {
      this.page = 1
      this.loadApplicationUserCount(searchQuery)
      this.loadApplicationUser(searchQuery)
    },
    async loadRolesForApp() {
      let { appId } = this

      let { error, data } = await API.get('/setup/roles', { appId })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.rolesList = (data.roles || []).filter(
          role => role.name !== 'Super Administrator'
        )
      }
    },
    next() {
      this.canShowUsersList = false
      this.canShowRolesList = true
    },
    back() {
      this.canShowRolesList = false
      this.canShowUsersList = true
    },
    close() {
      this.$emit('close')
    },
    saveRecord() {
      this.saving = true
      let { selectedRoleId, selectedUser } = this

      let userData = {
        appId: this.appId || this.$route.params.id,
        user: {
          id: selectedUser.id,
          email: selectedUser.email,
          roleId: selectedRoleId,
        },
      }
      API.post('/v2/application/users/add', userData)
        .then(({ error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occurred')
          } else {
            this.$message(this.$t('common.header.existing_user_added'))
            this.$emit('saved')
            this.$store.dispatch('loadUsers', true)
          }
        })
        .finally(() => {
          this.saving = false
          this.close()
        })
    },
    showUserSearch() {
      this.showUserSearchInput = true
      this.userSearchIcon = false
    },
    clearUser() {
      this.showUserSearchInput = false
      this.userSearchIcon = true
      this.activeUserSearchData = null
      this.activeUserSearch()
    },
  },
}
</script>
<style lang="scss">
.fc-app-user-search .el-input .el-input__inner {
  font-size: 14px;
}
.fc-table-th-pLalign-reduce {
  .th.is-leaf {
    padding-left: 10px;
    padding-right: 10px;
  }
  .el-table__body td {
    padding-left: 15px;
    padding-right: 15px;
  }
  th .cell {
    padding-left: 0 !important;
    padding-right: 0 !important;
  }
}
</style>
