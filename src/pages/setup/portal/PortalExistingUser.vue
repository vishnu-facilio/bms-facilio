<template>
  <el-dialog
    v-if="canShowUserList"
    :visible="true"
    width="60%"
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
          <div class="flex-middle">
            <div>
              <div
                @click="searchShowHide"
                v-if="searchHide"
                class="pointer mR20"
                v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
                content="Search"
              >
                <i class="el-icon-search fc-black-14 text-right fwBold"></i>
              </div>
              <div class="relative mR20" v-if="searchShow">
                <el-input
                  placeholder="Search"
                  v-model="querySearch"
                  ref="querySearch"
                  class="fc-input-full-border2 fc-existing-user-search width250px mR20"
                  @change="searchLoadData"
                  clearable
                >
                  <el-select
                    v-model="selectFiledSearch"
                    slot="prepend"
                    placeholder="Select"
                  >
                    <el-option label="Name" value="name"></el-option>
                    <el-option label="Email" value="email"></el-option>
                  </el-select>
                </el-input>
                <i
                  class="el-icon-close fc-search-close fc-black-14 text-right fwBold f18 pointer"
                  @click="searchClose"
                ></i>
              </div>
            </div>
            <pagination
              :total="userListCount"
              :current-page="page"
              @pagechanged="setPage"
              class="mR20"
            ></pagination>
          </div>
        </div>
      </div>
    </div>
    <div v-if="loading" class="flex-middle height450">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div
      v-else-if="$validation.isEmpty(userList)"
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
    <div v-else>
      <el-table
        :data="userTableList"
        style="width: 100%;padding-bottom: 50px;"
        height="450"
        ref="elTable"
        class="portal-existing-user-table fc-table-th-pLalign-reduce fc-table-td-user-pad pB50"
      >
        <el-table-column prop="name" :label="$t('common.products.name')">
          <template v-slot="data">
            <el-radio
              :label="data.row.id"
              v-model="selectedUserId"
              class="fc-radio-btn"
              >{{ data.row.name }}
            </el-radio>
          </template>
        </el-table-column>
        <el-table-column prop="email" :label="$t('common.header.email')">
        </el-table-column>
        <el-table-column :prop="parentModule" :label="parentModule">
        </el-table-column>
        <el-table-column prop="role" :label="$t('common.wo_report.role')">
        </el-table-column>
      </el-table>
    </div>
    <div class="dialog-footer">
      <div class="modal-dialog-footer">
        <el-button @click="close()" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button type="primary" class="modal-btn-save" @click="next()">
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
        v-model="selectedUserId"
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
import { API } from '@facilio/api'
import { mapGetters } from 'vuex'
import Pagination from 'pages/setup/AuditLog/AuditLogPagination'

export default {
  props: ['app', 'save', 'fetchParams', 'parentModule', 'fetchParamsCount'],
  data() {
    return {
      saving: false,
      loading: true,
      userList: [],
      selectedUserId: null,
      canShowUserList: true,
      canShowRolesList: false,
      rolesList: [],
      selectedRoleId: null,
      searchHide: true,
      searchShow: false,
      querySearch: '',
      selectFiledSearch: 'name',
      userListCount: 0,
      page: 1,
      perPage: 50,
    }
  },
  async created() {
    this.$store.dispatch('loadRoles')
    await this.loadApplicationUser()
    await this.loadRolesForApp()
  },
  components: {
    Pagination,
  },
  computed: {
    ...mapGetters(['getRoleNameById']),

    userTableList() {
      let {
        userList,
        parentModule,
        app: { linkName },
      } = this

      return userList.map(user => {
        let { name, id, email, rolesMap } = user || {}
        let currentUser = { name, id, email }
        let roleId = (rolesMap || {})[linkName]

        currentUser[parentModule] = user[parentModule]?.name || '---'
        currentUser.role = roleId ? this.getRoleNameById(roleId) : '---'

        return currentUser
      })
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
    async loadApplicationUser() {
      let { params, url, dataKey } = this.fetchParams || {}
      let payload = {
        ...params,
      }
      if (this.querySearch) {
        let { filters } = payload
        filters = JSON.parse(filters)

        if (this.selectFiledSearch === 'email') {
          filters.email = {
            operatorId: 5,
            value: [this.querySearch],
          }
        } else {
          filters.name = {
            operatorId: 5,
            value: [this.querySearch],
          }
        }
        payload.filters = JSON.stringify(filters)
      }
      payload.page = this.page
      payload.perPage = this.perPage
      this.loading = true

      let { error, data } = await API.get(url, payload)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.userList = data[dataKey] || []
        this.loadApplicationUserCount()
      }
      this.loading = false
    },
    async loadApplicationUserCount() {
      this.loading = true
      let { params, url } = this.fetchParamsCount || {}
      params.page = this.page
      params.perPage = this.perPage
      let { error, data } = await API.get(url, params)
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.userListCount = data.recordCount || []
      }
      this.loading = false
    },
    async loadRolesForApp() {
      let { app } = this
      let { error, data } = await API.get('/setup/roles', { appId: app.id })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.rolesList = (data.roles || []).filter(
          role => role.name !== 'Super Administrator'
        )
      }
    },
    saveRecord() {
      this.saving = true
      let { selectedUserId, app, selectedRoleId, userList } = this
      let selectedUser = userList.find(user => user.id === selectedUserId)
      let user = {
        ...selectedUser,
        rolesMap: { [app.linkName]: selectedRoleId },
      }

      this.save(user)
        .then(() => {
          this.close()
        })
        .catch(() => {})
        .finally(() => {
          this.saving = false
        })
    },
    close() {
      this.$emit('onClose')
    },
    next() {
      this.canShowUserList = false
      this.canShowRolesList = true
    },
    back() {
      this.canShowRolesList = false
      this.canShowUserList = true
    },
    searchShowHide() {
      this.searchHide = false
      this.searchShow = true
    },
    searchClose() {
      this.searchHide = true
      this.searchShow = false
      this.querySearch = null
    },
    searchLoadData() {
      this.loadApplicationUser()
      this.page = 1
    },
    setPage(page) {
      this.page = page
      this.loadApplicationUser()
    },
  },
}
</script>
<style lang="scss">
.fc-table-td-user-pad {
  .el-table__body td {
    padding-left: 20px !important;
    padding-right: 20px !important;
  }
  .el-table__header {
    th {
      padding-left: 12px !important;
      padding-right: 12px !important;
    }
  }
}
.fc-search-close {
  position: absolute;
  right: 30px;
  top: 10px;
  background: #fff;
}
</style>
