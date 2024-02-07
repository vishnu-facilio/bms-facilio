<template>
  <div class="fc-setup-page">
    <SetupLayout>
      <template #heading>
        {{ $t('setup.users_management.users') }}
      </template>
      <template #description>
        {{ $t('setup.users_management.list_all_user') }}
      </template>
      <template #actions>
        <div class="flex-middle">
          <el-select
            v-if="canShowApps"
            v-model="appId"
            placeholder="Select App"
            filterable
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

          <div class="action-btn setting-page-btn mL20">
            <el-dropdown v-if="canShowApps" @command="addNewForm">
              <el-button type="primary" class="setup-el-btn">
                {{ $t('common.products.add_user')
                }}<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="new">{{
                  $t('common.products.new_user')
                }}</el-dropdown-item>
                <el-dropdown-item command="existing">
                  {{ $t('common.header.existing_user') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <el-button
              v-else
              type="primary"
              class="setup-el-btn"
              @click="addUser"
            >
              {{ $t('setup.users_management.add_user') }}
            </el-button>
          </div>
        </div>
      </template>
      <template #searchAndPagination>
        <portal-target name="#usertable-search-and-pagination"></portal-target>
      </template>
      <template #tabs>
        <div class="d-flex">
          <router-link :to="{ name: 'users' }" class="fc-user-list-header mR40">
            <div>{{ $t('setup.users_management.users') }}</div>
            <div class="sh-selection-bar"></div>
          </router-link>
          <router-link
            :to="{ name: 'pendingusers' }"
            class="fc-user-list-header mR40 "
          >
            <div>{{ $t('setup.setupLabel.pending_users') }}</div>
            <div class="sh-selection-bar"></div>
          </router-link>
        </div>
      </template>
      <template #extraFilter>
        <portal-target
          class="width100"
          name="#user-table-extra-filter"
        ></portal-target>
      </template>

      <template #body>
        <div class="fc-setup-list-tab">
          <UserTable
            ref="userTable"
            :selectedApp="selectedApp"
            :appId="appId"
          ></UserTable>
        </div>

        <edit-user
          v-if="showDialog"
          :isNew="true"
          :appId="appId"
          :applications="selectedApp"
          @save="reloadUserList"
          @close="showDialog = false"
        ></edit-user>

        <AddExistingUser
          v-if="showExistingDialog"
          :app="selectedApp"
          @saved="reloadUserList"
          @close="showExistingDialog = false"
        ></AddExistingUser>
      </template>
    </SetupLayout>
  </div>
</template>
<script>
import SetupLayout from 'pages/setup/components/SetupLayout'
import EditUser from 'pages/setup/users/EditUser'
import { loadApps } from 'util/appUtil'
import UserTable from 'pages/setup/users/UserTable'
import AddExistingUser from 'pages/setup/portal/ExistingUserForm'
import { getApp } from '@facilio/router'
export default {
  name: 'NewUsersList',
  data() {
    return {
      showDialog: false,
      applications: [],
      appId: null,
      showExistingDialog: false,
      appLoading: false,
    }
  },
  title() {
    return this.title
  },
  components: {
    SetupLayout,
    EditUser,
    UserTable,
    AddExistingUser,
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
  created() {
    this.$store.dispatch('loadShifts')
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
    this.loadAppList()
  },

  methods: {
    loadAppList() {
      this.appLoading = true

      return loadApps()
        .then(({ data, error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.applications = data.filter(app => app.appCategory === 3)
            const { linkName: currentApp } = getApp()
            let selectedApp =
              this.applications.find(app => app.linkName === currentApp) ||
              this.applications.find(app => app.isDefault) ||
              this.applications[0]

            this.appId = (selectedApp || {}).id
          }
        })
        .finally(() => (this.appLoading = false))
    },
    reloadUserList() {
      this.$refs['userTable']?.loadUserData()
    },
    addUser() {
      this.showDialog = true
    },
    addNewForm(cmd) {
      if (cmd === 'new') {
        this.addUser()
      } else if (cmd === 'existing') {
        this.showExistingDialog = true
      }
    },
  },
}
</script>
<style scoped lang="scss">
.fc-user-list-hover {
  .q-item-label {
    &:hover {
      color: #46a2bf;
      text-decoration: underline;
    }
  }
}

.fc-user-list-header {
  font-size: 12px;
  line-height: 20px;
  letter-spacing: 1px;
  text-transform: uppercase;
  color: #324056;
  font-weight: 500;

  &:hover {
    color: #ee518f;
  }

  &.active {
    font-weight: 700;
  }

  &.active .sh-selection-bar {
    border-right: 0px solid #e0e0e0;
    border-left: 0px solid #e0e0e0;
    border: 1px solid var(--fc-theme-color);
    width: 32px !important;
    position: absolute;
    height: 2px;
    background-color: #ee518f;
  }

  .sh-selection-bar {
    border: 1px solid transparent;
    width: 25px;
    margin-top: 14px;
    position: absolute;
  }
}

.fc-user-pagination {
  position: absolute;
  top: 180px;
  z-index: 10;
  right: 70px;
}
</style>
