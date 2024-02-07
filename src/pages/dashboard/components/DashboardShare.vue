<template>
  <div>
    <el-popover
      ref="popover5"
      placement="bottom"
      width="350"
      height="400"
      v-model="sharingDialogVisible"
      popper-class="sharepopover"
    >
      <div class="share-container">
        <el-row align="middle " :guttr="20" class="share-header">
          <el-col :span="24" style="padding-right: 35px;padding-left:0px;">
            <div class="add">
              <el-radio v-model="shareTo" :label="1">
                {{ $t('panel.share.me') }}</el-radio
              >
              <el-radio v-model="shareTo" :label="2">{{
                $t('panel.share.everyone')
              }}</el-radio>
              <el-radio v-model="shareTo" :label="3">{{
                $t('panel.share.specific')
              }}</el-radio>
              <!-- <el-radio v-model="shareTo" :label="4">Portal users</el-radio> -->
            </div>
          </el-col>
        </el-row>
        <el-row
          v-if="shareTo === 3"
          align="middle"
          style="margin:0px;padding-top:20px;"
          :gutter="20"
        >
          <el-col :span="24" style="padding-right: 0px;padding-left:0px;">
            <div class="textcolor">{{ $t('panel.share.users') }}</div>
            <div class="add">
              <el-select
                v-model="sharedUsers"
                multiple
                style="width:100%"
                class="form-item "
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
          </el-col>
        </el-row>
        <el-row
          v-if="shareTo === 3"
          align="middle"
          style="margin:0px;padding-top:20px;"
          :gutter="20"
        >
          <el-col :span="24" style="padding-right:0px;padding-left:0px;">
            <div class="textcolor">{{ $t('panel.share.roles') }}</div>
            <div class="add">
              <el-select
                v-model="sharedRoles"
                multiple
                style="width:100%"
                class="form-item "
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
          </el-col>
        </el-row>
        <el-row
          v-if="shareTo === 3"
          align="middle"
          style="margin:0px;padding-top:20px;"
          :gutter="20"
        >
          <el-col :span="24" style="padding-right: 0px;padding-left:0px;">
            <div class="textcolor">{{ $t('panel.share.teams') }}</div>
            <div class="add">
              <el-select
                v-model="sharedGroups"
                multiple
                style="width:100%"
                class="form-item "
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
          </el-col>
        </el-row>
        <el-row
          v-if="shareTo === 4"
          align="middle"
          style="margin:0px;padding-top:20px;"
          :gutter="20"
        >
          <el-col :span="24" style="padding-right: 0px;padding-left:0px;">
            <div class="textcolor">{{ $t('panel.share.portal') }}</div>
            <div class="add">
              <el-select
                v-model="portalusers"
                multiple
                style="width:100%"
                class="form-item "
                :placeholder="$t('common.wo_report.choose_users')"
              >
                <el-option
                  v-for="user in portalusers"
                  :key="user.id"
                  :label="user.name"
                  :value="user.id"
                ></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="dialog-footer row">
        <div
          @click="sharingDialogVisible = false"
          class="col-6 shareoverbtn shrbtn1"
        >
          {{ $t('panel.share.cancel') }}
        </div>
        <div @click="applySharing" class="col-6 shareoverbtn shrbtn2">
          {{ $t('panel.share.confirm') }}
        </div>
      </div>
    </el-popover>
    <div v-popover:popover5>
      <i
        title="Share"
        v-tippy
        data-position="top"
        data-arrow="true"
        class="el-icon-share default dashboard-edit share-icon"
      ></i>
    </div>
  </div>
</template>
<script>
import ReportHelper from 'pages/report/mixins/ReportHelper'
import { mapState, mapGetters } from 'vuex'

export default {
  mixins: [ReportHelper],
  props: ['id', 'dashboard'],
  data() {
    return {
      shareTo: 2,
      sharedUsers: [],
      sharedRoles: [],
      portalusers: [],
      sharedGroups: [],
      sharingDialogVisible: false,
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
  computed: {
    ...mapState({
      groups: state => state.groups,
      users: state => state.users,
      roles: state => state.roles,
    }),

    ...mapGetters(['getCurrentUser']),

    dashboardLink() {
      return this.$route.params.dashboardlink
    },
  },
  created() {
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadUsers')
  },
  mounted() {
    this.loadSharing()
    this.loadPortalUsers()
  },
  methods: {
    loadPortalUsers() {
      let self = this
      self.$http.get('/setup/portalusers').then(function(response) {
        self.portalusers = response.data.users
      })
    },
    loadSharing: function() {
      let self = this
      if (self.dashboard === null) {
        return
      }
      self.$http
        .get('/dashboardsharing/' + self.dashboard.id)
        .then(function(response) {
          if (response.data.dashboardSharing) {
            if (response.data.dashboardSharing.length === 0) {
              self.shareTo = 2
            } else {
              self.sharedUsers = []
              self.sharedRoles = []
              self.sharedGroups = []
              for (let i = 0; i < response.data.dashboardSharing.length; i++) {
                let dashboardSharing = response.data.dashboardSharing[i]
                if (dashboardSharing.sharingType === 1) {
                  self.sharedUsers.push(dashboardSharing.orgUserId)
                } else if (dashboardSharing.sharingType === 2) {
                  self.sharedRoles.push(dashboardSharing.roleId)
                } else if (dashboardSharing.sharingType === 3) {
                  self.sharedGroups.push(dashboardSharing.groupId)
                } else if (dashboardSharing.sharingType === 4) {
                  self.sharedUsers.push(dashboardSharing.orgUserId)
                }
              }
              if (
                response.data.dashboardSharing.length === 1 &&
                self.sharedUsers.length === 1 &&
                self.sharedUsers[0] === self.getCurrentUser().ouid
              ) {
                self.shareTo = 1
              } else {
                self.shareTo = 3
              }
            }
          }
        })
    },
    applySharing() {
      let self = this
      self.sharingDialogVisible = false
      let dashboardSharing = []
      if (self.shareTo === 1) {
        dashboardSharing.push({
          dashboardId: self.dashboard.id,
          sharingType: 1,
          orgUserId: self.getCurrentUser().ouid,
        })
      } else if (self.shareTo === 3) {
        if (self.sharedUsers.length > 0) {
          dashboardSharing.push({
            dashboardId: self.dashboard.id,
            sharingType: 1,
            orgUserId: self.getCurrentUser().ouid,
          })
          for (let i = 0; i < self.sharedUsers.length; i++) {
            if (self.sharedUsers[i] !== self.getCurrentUser().ouid) {
              dashboardSharing.push({
                dashboardId: self.dashboard.id,
                sharingType: 1,
                orgUserId: self.sharedUsers[i],
              })
            }
          }
        }
        if (self.sharedRoles.length > 0) {
          for (let i = 0; i < self.sharedRoles.length; i++) {
            dashboardSharing.push({
              dashboardId: self.dashboard.id,
              sharingType: 2,
              roleId: self.sharedRoles[i],
            })
          }
        }
        if (self.sharedGroups.length > 0) {
          for (let i = 0; i < self.sharedGroups.length; i++) {
            dashboardSharing.push({
              dashboardId: self.dashboard.id,
              sharingType: 3,
              groupId: self.sharedGroups[i],
            })
          }
        }
      } else if (self.shareTo === 4) {
        if (self.sharedUsers.length > 0) {
          for (let i = 0; i < self.sharedUsers.length; i++) {
            if (self.sharedUsers[i] !== self.getCurrentUser().ouid) {
              dashboardSharing.push({
                dashboardId: self.dashboard.id,
                sharingType: 1,
                orgUserId: self.sharedUsers[i],
              })
            }
          }
        }
      }
      self.$http
        .post('/dashboardsharing/apply', {
          dashboardId: self.dashboard.id,
          dashboardSharing: dashboardSharing,
        })
        .then(function(response) {
          self.$message({
            message: 'Sharing applied successfully!',
            type: 'success',
          })
        })
    },
  },
}
</script>
<style>
.sharepopover {
  height: 400px;
  position: relative;
  padding: 0;
  border: solid 1px #e1e1e1;
}

.share-container {
  padding: 20px;
  overflow: auto;
  height: 380px;
}

.shareoverbtn {
  padding: 10px;
  text-align: center;
  background: #f8768c;
  padding-top: 15px;
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.1px;
  text-align: center;
  text-transform: uppercase;
  padding-bottom: 15px;
  cursor: pointer;
}
.dialog-footer.row {
  position: absolute;
  width: 100%;
  bottom: 0;
}
.shrbtn1 {
  color: #8f8f8f;
  background-color: #f4f4f4;
}
.shrbtn2 {
  background-color: #39b2c2;
  color: #ffffff;
}
.share-header {
  margin: 0px;
  position: absolute;
  top: 0px;
  z-index: 6;
  background: #fff;
  padding: 10px;
  width: 100%;
  left: 0;
  border-radius: 4px;
  text-align: center;
  white-space: nowrap;
  padding-top: 15px;
  padding-bottom: 15px;
}
.share-container .el-radio__input.is-checked .el-radio__inner {
  border-color: #ee518f;
  background: #ee518f;
}
.share-container .el-radio__input.is-checked + .el-radio__label {
  color: #ee518f;
}
</style>
