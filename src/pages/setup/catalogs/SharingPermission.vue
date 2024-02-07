<template>
  <div>
    <f-dialog
      v-if="canShowDialog"
      :title="$t('viewsmanager.sharing_permission.title')"
      :visible.sync="canShowDialog"
      width="37%"
      @save="saveSharingPermission"
      @close="canShowDialog = false"
      :confirmTitle="$t('common._common.share')"
      :stayOnSave="true"
      customClass="qr-dialog"
    >
      <div class="sharing-permission-container height350">
        <div class="header-label pB15">
          {{ $t('viewsmanager.sharing_permission.share_with') }}
        </div>
        <div class="pB15">
          <el-select
            v-model="shareTo"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="(option, index) in shareWith"
              :key="index"
              :label="option.label"
              :value="option.value"
            ></el-option>
          </el-select>
        </div>
        <div v-if="isSpecific" class="sharing-desc">
          {{ $t('viewsmanager.sharing_permission.desc') }}
        </div>
        <div class="mT20" v-if="isSpecific">
          <el-select
            v-model="sharedUsers"
            multiple
            collapse-tags
            class="fc-input-full-border2 width100"
            :placeholder="$t('common.wo_report.choose_users')"
          >
            <el-option
              v-for="user in users"
              :key="user.id"
              :label="user.name"
              :value="user.id"
            ></el-option>
          </el-select>
        </div>
        <div class="mT20" v-if="isSpecific">
          <el-select
            v-model="sharedRoles"
            multiple
            collapse-tags
            class="fc-input-full-border2 width100"
            :placeholder="$t('common.wo_report.choose_roles')"
          >
            <el-option
              v-for="role in roles"
              :key="role.id"
              :label="role.name"
              :value="role.id"
            ></el-option>
          </el-select>
        </div>
        <div
          class="mT20"
          v-if="isSpecific && selectedApp.linkName === 'newapp'"
        >
          <el-select
            v-model="sharedGroups"
            multiple
            collapse-tags
            class="fc-input-full-border2 width100"
            :placeholder="$t('common.wo_report.choose_teams')"
          >
            <el-option
              v-for="group in groups"
              :key="group.id"
              :label="group.name"
              :value="group.id"
            ></el-option>
          </el-select>
        </div>
      </div>
    </f-dialog>
  </div>
</template>

<script>
import FDialog from '@/FDialogNew'
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'

const sharingTypeHash = {
  1: {
    valueString: 'userId',
    valueArrString: 'sharedUsers',
  },
  2: {
    valueString: 'roleId',
    valueArrString: 'sharedRoles',
  },
  3: {
    valueString: 'groupId',
    valueArrString: 'sharedGroups',
  },
}

export default {
  props: [
    'showDialog',
    'sharingDetails',
    'currentUserId',
    'selectedAppId',
    'selectedApp',
    'roles',
  ],
  components: {
    FDialog,
  },
  created() {
    this.$store.dispatch('loadGroups')
    this.init()
  },
  mounted() {
    this.loadUsersList()
  },
  data() {
    return {
      shareTo: 3,
      sharedGroups: [],
      sharedUsers: [],
      sharedRoles: [],
      users: [],
      shareWith: [
        {
          label: this.$t('viewsmanager.sharing_permission.only_me'),
          value: 1,
        },
        {
          label: this.$t('viewsmanager.sharing_permission.everyone'),
          value: 2,
        },
      ],
    }
  },
  computed: {
    ...mapState({
      groups: state => state.groups,
    }),
    canShowDialog: {
      get() {
        return this.showDialog
      },
      set(value) {
        this.$emit('update:showDialog', value)
      },
    },
    isSpecific() {
      let { shareTo } = this
      return shareTo === 3
    },
  },
  watch: {
    shareTo(val) {
      let { currentUserId, sharedUsers, selectedAppId } = this
      let isAlreadyUserExists = sharedUsers.includes(currentUserId)
      if (
        val === 3 &&
        !isEmpty(currentUserId) &&
        !isAlreadyUserExists &&
        (getApp() || {}).id === selectedAppId
      ) {
        this.sharedUsers.push(currentUserId)
      }
    },
  },
  methods: {
    loadUsersList() {
      let { selectedAppId } = this

      API.get(`/v2/application/users/list?appId=${selectedAppId}`).then(
        ({ data, error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.users = data.users
          }
        }
      )
    },
    init() {
      let { sharingDetails, currentUserId, selectedApp } = this
      let sharedList = (sharingDetails || []).filter(
        viewSharingContext => viewSharingContext.typeEnum != 'APP'
      )
      if (isEmpty(sharedList) || sharedList.length === 0) {
        this.shareTo = 2
      } else {
        sharedList.forEach(sharingInfo => {
          let { type } = sharingInfo
          let sharingTypeObj = sharingTypeHash[type] || {}
          let { valueString, valueArrString } = sharingTypeObj
          if (!isEmpty(valueString) && !isEmpty(valueArrString)) {
            let value = sharingInfo[valueString]

            let typeArr = this.$getProperty(this, `${valueArrString}`, [])
            typeArr.push(value)
            this.$set(this, valueArrString, typeArr)
          }
        })
        if (
          sharedList.length === 1 &&
          this.sharedUsers.length === 1 &&
          this.sharedUsers[0] === currentUserId
        ) {
          this.shareTo = 1
        } else {
          this.shareTo = 3
        }
      }
      if (selectedApp.linkName !== 'newapp') {
        let index = this.sharedUsers.indexOf(currentUserId)
        if (index > -1) {
          this.sharedUsers.splice(index, 1)
        }
        let labelObj = {
          label: this.$t('viewsmanager.sharing_permission.specific_user'),
          value: 3,
        }
        this.shareWith.push(labelObj)
      } else {
        let labelObj = {
          label: this.$t('viewsmanager.sharing_permission.specific'),
          value: 3,
        }
        this.shareWith.push(labelObj)
      }
    },
    saveSharingPermission() {
      let { sharedUsers, sharedRoles, sharedGroups, currentUserId } = this
      let sharingDetails = []
      if (this.shareTo === 1) {
        sharingDetails.push({
          type: 1,
          userId: currentUserId,
        })
      } else if (this.shareTo === 3) {
        if (!isEmpty(sharedUsers)) {
          sharingDetails.push({
            type: 1,
            userId: currentUserId,
          })
          sharedUsers.forEach(user => {
            if (user !== currentUserId) {
              sharingDetails.push({
                type: 1,
                userId: user,
              })
            }
          })
        }
        if (!isEmpty(sharedRoles)) {
          sharedRoles.forEach(role => {
            sharingDetails.push({
              type: 2,
              roleId: role,
            })
          })
        }
        if (!isEmpty(sharedGroups)) {
          sharedGroups.forEach(group => {
            sharingDetails.push({
              type: 3,
              groupId: group,
            })
          })
        }
      }
      this.$emit('sharing', sharingDetails)
      this.canShowDialog = false
    },
  },
}
</script>
<style lang="scss">
.sharing-permission-container {
  .header-label {
    font-size: 14px;
    letter-spacing: 0.5px;
    color: #6b7e91;
  }
  .sharing-desc {
    font-size: 13px;
    letter-spacing: 0.5px;
    color: #6b7e91;
  }
}
</style>
