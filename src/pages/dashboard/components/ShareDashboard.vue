<template>
  <el-dialog
    title="Share the Dashboard"
    :visible.sync="dialogVisible"
    width="51%"
    custom-class="dashboardShare-dialog"
    :before-close="handleClose"
  >
    <div
      v-if="isSelectedUserLoading"
      class="dashboard-share-form-with-container pB51"
    >
      <div
        class="lines loading-shimmer"
        v-for="i in 2"
        :key="`option_available_list-${i}`"
      ></div>
    </div>
    <div v-else class="dashboard-share-form-with-container pB51">
      <el-radio-group v-model="shareTo" class="sharing-radio-icon">
        <el-radio
          v-for="(displayLabel, value) in shareWithTypes"
          :key="value"
          :label="parseInt(value)"
          @change="onChangeSharingTo"
          class="fc-radio-btn"
        >
          {{ displayLabel }}
        </el-radio>
      </el-radio-group>
      <div v-if="isSpecific" class="specific-pb">
        <div class="specific_user-header">
          {{ $t('common.sharing_permission.select_user') }}
        </div>
        <div class="specific_user-descrp">
          {{ $t('common.dashboard.dashboard_sharing_desc') }}
        </div>
        <div class="user-selection-container">
          <div class="user-selection-container-block avaiable-user-container">
            <div class="header-section">
              {{ $t('common.sharing_permission.available_user') }}
            </div>
            <div class="user-selection-filter-container">
              <el-select
                v-model="shareWithSelected"
                class="width100 border-right2"
                @change="searchText = null"
              >
                <el-option
                  v-for="(option, index) in specificUser"
                  :key="index"
                  :label="option"
                  :value="parseInt(index)"
                ></el-option>
              </el-select>
              <el-input
                prefix-icon="el-icon-search"
                placeholder="Search"
                v-model="searchText"
                @input="searchContent"
                @clear="searchContent"
                clearable
              ></el-input>
            </div>
            <div class="list-container">
              <div v-if="isLoading" class="pT20">
                <div
                  class="lines loading-shimmer"
                  v-for="i in 6"
                  :key="`available_list-${i}`"
                ></div>
              </div>
              <div
                v-else
                class="block-card"
                v-for="(element, index) in filteredList"
                :key="`filteredList-${index}`"
              >
                <span class="block-text">{{ element.name }}</span>
                <i
                  class="el-icon-plus addition-plus-icon"
                  @click="addcolumn(element)"
                ></i>
              </div>
            </div>
          </div>

          <div class="user-selection-container-block selected-user-container">
            <div class="header-section">
              {{ $t('common.sharing_permission.selected_user') }}
            </div>

            <div class="list-container">
              <div v-if="isLoading" class="pT20">
                <div
                  class="lines loading-shimmer"
                  v-for="i in 3"
                  :key="`selected_list-${i}`"
                ></div>
              </div>
              <div
                class="empty-state"
                v-else-if="$validation.isEmpty(selectedUsers)"
              >
                {{ $t('common.sharing_permission.add_here') }}
              </div>
              <draggable
                v-else
                v-model="selectedUsers"
                class="dragArea"
                :group="'people'"
                handle=".rearrange-icon"
              >
                <div
                  class="block-card"
                  v-for="(element, index) in selectedUsers"
                  :key="`selected-user-${index}`"
                >
                  <div class="flex-middle">
                    <el-tooltip
                      effect="dark"
                      :content="$t('common.sharing_permission.reorder')"
                      placement="top"
                      style="height:16px"
                    >
                      <inline-svg
                        src="svgs/ic-drag-handle"
                        iconClass="icon icon-sm-md"
                        class="rearrange-icon cursor-move"
                      ></inline-svg>
                    </el-tooltip>

                    <div class="block-text2 mL5">{{ element.name }}</div>
                  </div>
                  <i
                    class="el-icon-close addition-plus-icon"
                    v-on:click="removeColumn(index)"
                  ></i>
                </div>
              </draggable>
            </div>
          </div>
        </div>
      </div>
      <div v-if="!isOnlyMe" class="mT30">
        <el-checkbox v-model="hasEditPermission">
          {{ $t('common.sharing_permission.permission_desc') }}
        </el-checkbox>
      </div>
    </div>
    <span class="dialog-footer row">
      <div @click="handleClose" class="col-6 shareoverbtn shrbtn1">
        {{ $t('panel.share.cancel') }}
      </div>
      <div @click="applySharing" class="col-6 shareoverbtn shrbtn2">
        {{ $t('panel.share.save') }}
      </div>
    </span>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters, mapState } from 'vuex'
import { API } from '@facilio/api'
import draggable from 'vuedraggable'
import debounce from 'lodash/debounce'
import { getFieldOptions } from 'util/picklist'

const shareWithTypeValue = { ONLY_ME: 1, EVERYONE: 2, SELECT_USER: 3 }
const specificUserValue = { USER: 1, ROLE: 2, GROUP: 3 }

export default {
  props: ['dashboard', 'dialogVisible', 'appId'],
  components: { draggable },
  data() {
    return {
      shareWithTypeValue,
      specificUserValue,
      viewSharingDetails: {},
      hasEditPermission: false,
      sharingDetails: null,
      isLoading: false,
      shareTo: shareWithTypeValue.EVERYONE,
      roleList: [],
      selectedUsers: [],
      userList: [],
      searchText: null,
      shareWithSelected: specificUserValue.USER,
      shareWithTypes: { 1: 'Only Me', 2: 'Everyone', 3: 'Select User' },
      specificUser: { 1: 'User', 2: 'Role', 3: 'Team' },
      isSelectedUserLoading: false,
    }
  },
  watch: {
    dashboard: {
      handler(newData, oldData) {
        if (newData !== oldData) {
          this.loadSharing()
        }
      },
      deep: true,
    },
  },
  async created() {
    await this.loadSharing()
  },
  computed: {
    ...mapState({
      groupList: state => state.groups,
    }),
    ...mapGetters(['getCurrentUser']),
    currentUserId() {
      let { id } = this.getCurrentUser() || {}
      return id || null
    },
    isSpecific() {
      let { shareTo } = this
      return shareTo === shareWithTypeValue.SELECT_USER
    },
    isOnlyMe() {
      let { shareTo } = this
      return shareTo === this.shareWithTypeValue.ONLY_ME
    },

    filteredList() {
      let { shareWithSelected } = this
      let { USER, ROLE, GROUP } = specificUserValue
      let currentList =
        shareWithSelected === USER
          ? this.userList
          : shareWithSelected === ROLE
          ? this.roleList
          : shareWithSelected === GROUP
          ? this.groupList
          : []
      let currentUserList = currentList.filter(filt => {
        return !this.selectedUsers.find(element => {
          return element.name === filt.name
        })
      })

      if (this.searchText && shareWithSelected != USER) {
        return currentUserList.filter(user => {
          let { name } = user || {}
          return name.toLowerCase().includes(this.searchText.toLowerCase())
        })
      }
      return currentUserList
    },
  },
  methods: {
    async deserializedDashboardSharingDetails(dashboardSharing) {
      if (!isEmpty(dashboardSharing)) {
        let { USER, ROLE, GROUP } = specificUserValue
        if (!isEmpty(dashboardSharing)) {
          this.shareTo = shareWithTypeValue.SELECT_USER
          if (dashboardSharing.length === 1) {
            let [sharedData] = dashboardSharing
            let { orgUserId, sharingType } = sharedData

            if (sharingType === 1 && this.currentUserId === orgUserId) {
              this.shareTo = shareWithTypeValue.ONLY_ME
            }
          }
        } else {
          this.shareTo = shareWithTypeValue.EVERYONE
        }
        let userId = dashboardSharing
          .filter(user => user.sharingType === USER)
          .map(user => user.orgUserId)

        let roleId = dashboardSharing
          .filter(user => user.sharingType === ROLE)
          .map(user => user.roleId)

        let groupId = dashboardSharing
          .filter(user => user.sharingType === GROUP)
          .map(user => user.groupId)
        await this.getUserManagementList(userId)
        this.loadUserIds(userId)
        this.loadRoleIds(roleId)
        this.loadGroupIds(groupId)
        this.isSelectedUserLoading = false
      } else {
        this.shareTo = shareWithTypeValue.EVERYONE
        this.isSelectedUserLoading = false
      }
      let linkName = this.$getProperty(this, 'dashboard.linkName', null)
      if (!isEmpty(linkName)) {
        let { data, error } = await API.get(`v3/dashboard/${linkName}`)
        if (!error) {
          this.hasEditPermission = this.$getProperty(
            data,
            'dashboardJson.0.locked',
            this.dashboard?.locked
          )
        }
      }
    },
    async loadSharing() {
      let self = this
      if (this.dashboard === null) {
        return
      }
      this.isSelectedUserLoading = true
      this.shareTo = null
      self.isLoading = true
      let { data, error } = await API.get(
        '/dashboardsharing/' + self.dashboard.id
      )
      if (!error) {
        if (data.dashboardSharing) {
          this.deserializedDashboardSharingDetails(data.dashboardSharing)
        } else {
          self.isSelectedUserLoading = false
        }
      } else {
        self.isSelectedUserLoading = false
      }

      self.isLoading = false
    },
    async applySharing() {
      let self = this
      let sharingData = this.serializeData()
      let { dashboardSharing } = sharingData
      let params = {
        dashboard: {
          locked: self.hasEditPermission,
          id: self.dashboard.id,
          dashboardSharingContext: [...dashboardSharing],
        },
      }
      let { data, error } = API.post('/dashboard/updateDashboard', params)
      if (!error) {
        self.$message({
          message: 'Sharing applied successfully!',
          type: 'success',
        })
      }
      this.handleClose()
    },
    handleClose() {
      this.$emit('update:dialogVisible', false)
    },
    async onChangeSharingTo() {
      if (this.shareTo === shareWithTypeValue.ONLY_ME) {
        this.hasEditPermission = false
      }
      if (this.shareTo === shareWithTypeValue.SELECT_USER) {
        this.getUserManagementList()
      }
      this.shareWithSelected = specificUserValue.USER
      this.selectedUsers = []
    },
    async getUserManagementList(userIds = null) {
      this.isLoading = true
      await this.loadUsersList(userIds)
      await this.loadRoleList()
      await this.$store.dispatch('loadGroups')
      this.isLoading = false
    },
    async loadUsersList(userIds = null) {
      let { appId, searchText } = this

      let { error, options } = await getFieldOptions({
        field: {
          lookupModuleName: 'users',
          filters: { applicationId: { value: [appId] } },
        },
        searchText,
        defaultIds: userIds,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.userList = options.map(user => ({
          name: user.label,
          id: user.value,
        }))
      }
    },
    async loadRoleList() {
      let { appId } = this
      if (isEmpty(appId)) return
      let params = {
        search: this.searchText,
        appId,
      }
      let { data, error } = await API.get('/setup/roles', params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.roleList = data.roles || []
      }
    },
    searchContent: debounce(function() {
      let { shareWithSelected } = this
      let { USER } = specificUserValue
      if (shareWithSelected === USER) this.loadUsersList()
    }, 500),
    loadUserIds(usersId) {
      let { userList } = this
      let userDetails = (userList || [])
        .filter(user => (usersId || []).includes(user.id))
        .map(user => ({ ...user, userId: user.id }))

      this.selectedUsers = [...this.selectedUsers, ...userDetails]
    },
    loadRoleIds(roleId) {
      let { roleList } = this
      let roleDetails = (roleList || [])
        .filter(user => (roleId || []).includes(user.roleId))
        .map(user => ({ ...user, roleIdKey: user.id }))

      this.selectedUsers = [...this.selectedUsers, ...roleDetails]
    },
    loadGroupIds(groupId) {
      let { groupList } = this
      let groupDetails = (groupList || [])
        .filter(user => (groupId || []).includes(user.groupId))
        .map(user => ({ ...user, groupId: user.id }))

      this.selectedUsers = [...this.selectedUsers, ...groupDetails]
    },
    addcolumn(element) {
      let { shareWithSelected } = this
      let { USER, ROLE, GROUP } = specificUserValue

      if (shareWithSelected === USER) {
        this.selectedUsers.push({ ...element, userId: element.id })
      } else if (shareWithSelected === ROLE) {
        this.selectedUsers.push({ ...element, roleIdKey: element.id })
      } else if (shareWithSelected === GROUP) {
        this.selectedUsers.push({ ...element, groupId: element.id })
      }
    },
    removeColumn(index) {
      this.selectedUsers.splice(index, 1)
    },
    serializeData() {
      let { currentUserId, selectedUsers } = this
      let sharedUserIds = selectedUsers
        .filter(user => user.userId)
        .map(user => user.userId)
      let sharedRoleIds = selectedUsers
        .filter(user => user.roleIdKey)
        .map(user => user.roleIdKey)
      let sharedGroupIds = selectedUsers
        .filter(user => user.groupId)
        .map(user => user.groupId)
      let sharedUserDetails = [],
        sharedRoleDetails = [],
        sharedGroupDetails = []

      if (this.shareTo === shareWithTypeValue.ONLY_ME) {
        sharedUserDetails.push({
          sharingType: 1,
          orgUserId: currentUserId,
          dashboardId: this.dashboard.id,
        })
      } else if (this.shareTo === shareWithTypeValue.SELECT_USER) {
        if (!isEmpty(sharedUserIds)) {
          sharedUserDetails = sharedUserIds.map(userId => {
            if (userId !== currentUserId) {
              return {
                sharingType: 1,
                orgUserId: userId,
                dashboardId: this.dashboard.id,
              }
            } else {
              return {
                sharingType: 1,
                orgUserId: currentUserId,
                dashboardId: this.dashboard.id,
              }
            }
          })
        }
        if (!isEmpty(sharedRoleIds)) {
          sharedRoleDetails = sharedRoleIds.map(roleId => ({
            sharingType: 2,
            roleId: roleId,
            dashboardId: this.dashboard.id,
          }))
        }
        if (!isEmpty(sharedGroupIds)) {
          sharedGroupDetails = sharedGroupIds.map(groupId => ({
            sharingType: 3,
            groupId: groupId,
            dashboardId: this.dashboard.id,
          }))
        }
      }
      let sharingDetails = [
        ...sharedUserDetails,
        ...sharedRoleDetails,
        ...sharedGroupDetails,
      ]

      return { dashboardSharing: sharingDetails }
    },
  },
}
</script>
<style lang="scss">
.dashboard-share-form-with-container {
  .specific-pb {
    padding-bottom: 5px;
  }
  .lines {
    margin: 5px 10px;
    width: 93%;
    height: 24px;
    border-radius: 5px;
  }
  .sharing-radio-icon .el-radio__input.is-checked .el-radio__inner {
    background-color: #3ab2c1;
    text-transform: capitalize;
  }
  .specific_user-header {
    color: #2f4058;
    font-weight: 500;
    font-size: 16px;
    margin-top: 20px;
  }
  .specific_user-descrp {
    color: #c0c4cc;
    font-size: 12px;
    margin-top: 10px;
  }
  .user-selection-container {
    display: flex;
    margin-top: 20px;
    height: 350px;

    .user-selection-container-block {
      display: flex;
      flex-direction: column;
      width: 330px;
      border-radius: 2px;

      .header-section {
        background-color: #f5f9fa;
        font-size: 14px;
        font-weight: 500;
        letter-spacing: 0.5px;
        color: #2f4058;
        padding: 15px 20px;
        flex-shrink: 1;
      }
      .user-selection-filter-container {
        border-bottom: 1px solid #d0d9e2;
        display: flex;
        width: 100%;
        flex-shrink: 1;

        .el-input__inner,
        .el-textarea__inner,
        .el-input {
          line-height: 40px !important;
          padding-left: 15px !important;
          padding-right: 15px !important;
          background-color: #ffffff;
          border: none !important;
          font-size: 14px;
          font-weight: normal;
          letter-spacing: 0.4px;
          color: #324056;
          text-overflow: ellipsis;
          font-weight: 400;
          padding-right: 30px;
          white-space: nowrap;
        }
      }
      .list-container {
        flex-grow: 1;
        overflow-y: scroll;

        .block-card {
          justify-content: space-between;
          min-height: 40px;
          width: 300px;
          border-radius: 2px;
          background-color: #ffffff;
          border: solid 1px #e3e1e1;
          padding: 10px 15px;
          margin: 10px;
          display: flex;
          align-items: center;

          .block-text {
            letter-spacing: 0.5px;
            color: #2f4058;
            font-size: 14px;
            word-break: break-word;
          }
          .block-text2 {
            letter-spacing: 0.5px;
            color: #2c9baa;
            font-size: 14px;
            word-break: break-word;
          }
          .addition-plus-icon {
            font-size: 16px;
            color: #aeaeae;
            font-weight: 700;
            cursor: pointer;
          }
          .rearrange-block {
            display: flex;
            align-items: center;

            .rearrange-icon {
              margin-right: 4px;
            }
          }
        }
        .empty-state {
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 16px;
          font-weight: 500;
          letter-spacing: 0.57px;
          color: #2f4058;
          align-items: center;
          height: 100%;
          opacity: 0.5;
        }
      }
    }
    .avaiable-user-container {
      margin-right: 40px;
      border: solid 1px #c5e7eb;
    }
    .selected-user-container {
      border: 1px dashed #c7d0d9;
      background-color: #fcfeff;
    }
  }
}
.el-dialog.dashboardShare-dialog {
  display: block;
  padding: 0px;
  .el-dialog__headerbtn {
    display: None !important;
  }
}
.dashboard-share.dialog-box {
  position: relative;
  padding: 0px;
}
.el-dialog.dashboardShare-dialog .el-dialog__header {
  width: 100%;
  border-bottom: solid 1px #eaecf1;
}
.pB51 {
  padding-bottom: 51px;
}
.dialog-footer.row {
  position: absolute;
  width: 100%;
  bottom: 0;
  left: 0px;
}
</style>
