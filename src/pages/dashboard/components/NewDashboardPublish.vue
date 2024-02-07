<template>
  <div>
    <template v-if="mode === 'publish'">
      <div style="padding:0px;">
        <div class="dashboard-share dialog-box">
          <el-dialog
            title="Publish to portal"
            :visible.sync="dialogVisible"
            width="30%"
            custom-class="dashboardShare-dialog"
            :before-close="handleClose"
          >
            <div>
              <div v-if="loading" class="loadigContainer">
                <spinner :show="true" size="80"></spinner>
              </div>
              <div class="fc-share-container" v-else>
                <el-row align="middle " :guttr="20">
                  <el-col
                    :span="24"
                    style="padding-right: 0px;padding-left:0px;"
                  >
                    <p class="grey-text2 kpi-text-3 pB10">
                      {{ 'Portal type' }}
                    </p>
                    <div class="add">
                      <el-select
                        v-model="shareTo"
                        class="fc-input-full-border-select2 width100"
                      >
                        <el-option
                          label="Specific portal user"
                          :value="4"
                        ></el-option>
                        <el-option
                          label="All portal users"
                          :value="5"
                        ></el-option>
                      </el-select>
                    </div>
                  </el-col>
                </el-row>
                <el-row
                  v-if="shareTo === 4"
                  align="middle"
                  style="margin:0px;padding-top:0px;"
                  :gutter="20"
                >
                  <el-col
                    :span="24"
                    style="padding-right: 0px;padding-left:0px;"
                  >
                    <div class="add">
                      <p class="grey-text2 kpi-text-3 pB10">
                        {{ $t('panel.share.sel_portal_users') }}
                      </p>
                      <el-select
                        v-model="sharedPortalUsers"
                        multiple
                        filterable
                        style="width:100%"
                        class="form-item fc-input-full-border-select2"
                        placeholder="Select portal users"
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
            </div>
            <span class="dialog-footer row">
              <div
                @click="dialogVisible = false"
                class="col-6 shareoverbtn shrbtn1"
              >
                {{ $t('panel.share.cancel') }}
              </div>
              <div @click="publishportal" class="col-6 shareoverbtn shrbtn2">
                {{ shareTo === 4 ? 'Publish' : 'Confirm' }}
              </div>
            </span>
          </el-dialog>
        </div>
      </div>
    </template>
    <template v-else>
      <div style="padding:0px;">
        <div class="dashboard-share dialog-box">
          <el-dialog
            title="Share the Dashboard"
            :visible.sync="dialogVisible"
            width="30%"
            custom-class="dashboardShare-dialog"
            :before-close="handleClose"
          >
            <div>
              <div v-if="loading" class="loadigContainer">
                <spinner :show="true" size="80"></spinner>
              </div>
              <div class="fc-share-container" v-else>
                <el-row align="middle " :guttr="20">
                  <el-col
                    :span="24"
                    style="padding-right: 0px;padding-left:0px;"
                  >
                    <div class="add">
                      <el-select
                        v-model="shareTo"
                        class="fc-input-full-border-select2 width100"
                      >
                        <el-option label="Only Me" :value="1"></el-option>
                        <el-option label="Everyone" :value="2"></el-option>
                        <el-option
                          label="Specific User / Team / Role"
                          :value="3"
                        ></el-option>
                      </el-select>
                    </div>
                  </el-col>
                </el-row>
                <el-row
                  align="middle "
                  :guttr="20"
                  style="margin:0px;padding-top:20px;"
                >
                  <span class="text-paragraph">
                    {{ $t('panel.share.option') }}
                  </span>
                </el-row>
                <el-row
                  v-if="shareTo === 3"
                  align="middle"
                  style="margin:0px;padding-top:20px;"
                  :gutter="20"
                >
                  <el-col
                    :span="24"
                    style="padding-right: 0px;padding-left:0px;"
                  >
                    <div class="add">
                      <el-select
                        v-model="sharedUsers"
                        multiple
                        collapse-tags
                        style="width:100%"
                        class="form-item fc-tag fc-input-full-border-select2"
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
                  <el-col
                    :span="24"
                    style="padding-right:0px;padding-left:0px;"
                  >
                    <div class="add">
                      <el-select
                        v-model="sharedRoles"
                        multiple
                        collapse-tags
                        style="width:100%"
                        class="form-item fc-tag fc-input-full-border-select2"
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
                  <el-col
                    :span="24"
                    style="padding-right: 0px;padding-left:0px;"
                  >
                    <div class="add">
                      <el-select
                        v-model="sharedGroups"
                        multiple
                        collapse-tags
                        style="width:100%"
                        class="form-item fc-tag fc-input-full-border-select2"
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
                  style="margin:0px;padding-top:0px;"
                  :gutter="20"
                >
                  <el-col
                    :span="24"
                    style="padding-right: 0px;padding-left:0px;"
                  >
                    <div class="add">
                      <p class="grey-text2 kpi-text-3 pB10 ">
                        {{ $t('portal.share.sel_portal_users') }}
                      </p>
                      <el-select
                        v-model="sharedPortalUsers"
                        multiple
                        collapse-tags
                        style="width:100%"
                        class="form-item fc-tag fc-input-full-border-select2"
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
            </div>
            <span class="dialog-footer row">
              <div
                @click="
                  $emit('update:dialogVisible', !dialogVisible)
                  dialogVisible = false
                "
                class="col-6 shareoverbtn shrbtn1"
              >
                {{ $t('portal.share.cancel') }}
              </div>
              <div @click="applySharing" class="col-6 shareoverbtn shrbtn2">
                {{ $t('portal.share.ok') }}
              </div>
            </span>
          </el-dialog>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import ReportHelper from 'pages/report/mixins/ReportHelper'
import { mapState, mapGetters } from 'vuex'

export default {
  mixins: [ReportHelper],
  props: ['id', 'mode', 'dashboard', 'dialogVisible', 'fullDashboardContect'],
  data() {
    return {
      shareTo: 3,
      sharedUsers: [],
      sharedPortalUsers: [],
      sharedRoles: [],
      portalusers: [],
      sharedGroups: [],
      sharingDialogVisible: false,
      loading: false,
      dashboardSharingcontext: [],
      dashboardSharing: null,
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
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadUsers')
  },
  mounted() {
    this.shareTo = this.mode === 'publish' ? 4 : 3
    if (this.shareTo === 4) {
      this.loadPortalUsers()
    } else {
      this.loadSharing()
    }
  },
  methods: {
    publishportal() {
      if (this.shareTo === 4) {
        this.applySharing()
      } else if (this.shareTo === 5) {
        this.publishToAll()
      }
    },
    loadPortalUsers() {
      let self = this
      self.loading = true
      self.$http.get('/setup/allPortalUsers').then(function(response) {
        self.portalusers = response.data.users
        self.loadSharing()
      })
    },
    handleClose() {
      this.$emit('update:dialogVisible', !this.dialogVisible)
      this.dialogVisible = false
    },
    loadSharing: function() {
      let self = this
      if (self.dashboard === null) {
        return
      }
      self.loading = true
      self.$http
        .get('/dashboardsharing/' + self.dashboard.id)
        .then(function(response) {
          if (response.data.dashboardSharing) {
            self.dashboardSharingcontext = response.data.dashboardSharing
            if (response.data.dashboardSharing.length === 0) {
              self.shareTo = 2
            } else {
              self.sharedUsers = []
              self.sharedRoles = []
              self.sharedGroups = []
              self.sharedPortalUsers = []
              if (response.data.dashboardSharing.length) {
                self.dashboardSharing = response.data.dashboardSharing[0]
              }
              for (let i = 0; i < response.data.dashboardSharing.length; i++) {
                let dashboardSharing = response.data.dashboardSharing[i]
                if (dashboardSharing.sharingType === 1) {
                  self.sharedUsers.push(dashboardSharing.orgUserId)
                } else if (dashboardSharing.sharingType === 2) {
                  self.sharedRoles.push(dashboardSharing.roleId)
                } else if (dashboardSharing.sharingType === 3) {
                  self.sharedGroups.push(dashboardSharing.groupId)
                } else if (dashboardSharing.sharingType === 4) {
                  self.sharedPortalUsers.push(dashboardSharing.orgUserId)
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
          if (self.mode === 'publish') {
            if (
              self.dashboardSharing &&
              self.dashboardSharing.sharingType === 5
            ) {
              self.shareTo = 5
            } else {
              self.shareTo = 4
            }
          }
          self.loading = false
        })
    },
    getdashboardFormId(dashboard) {
      if (
        dashboard &&
        dashboard.id &&
        this.fullDashboardContect &&
        this.fullDashboardContect.length
      ) {
        let data = null
        this.fullDashboardContect.find(function(dashboardFolder) {
          if (
            dashboardFolder.id === dashboard.dashboardFolderId &&
            dashboardFolder.dashboards &&
            dashboardFolder.dashboards.length
          ) {
            dashboardFolder.dashboards.find(d => {
              if (d.id === dashboard.id) {
                data = d
              }
            })
          }
        })
        return data
      } else {
        return null
      }
    },
    publishToAll() {
      let self = this
      self.sharingDialogVisible = false
      let dashboardSharing = []
      dashboardSharing.push({
        dashboardId: self.dashboard.id,
        sharingType: 5,
        orgUserId: self.getCurrentUser().ouid,
      })
      let dashboardShre = []
      if (self.dashboardSharingcontext.length) {
        dashboardShre = self.dashboardSharingcontext.filter(
          rt => rt.sharingType !== 4
        )
        dashboardShre = dashboardShre.filter(rt => rt.sharingType !== 5)
      }
      let params = {
        dashboard: {
          id: self.dashboard.id,
          dashboardSharingContext: [...dashboardSharing, ...dashboardShre],
        },
      }
      self.$http
        .post('/dashboard/updateDashboard', params)
        .then(function(response) {
          if (self.shareTo === 4 || self.shareTo === 5) {
            self.$message({
              message: 'Published successfully!',
              type: 'success',
            })
          } else {
            self.$message({
              message: 'Sharing applied successfully!',
              type: 'success',
            })
          }
        })
      self.$emit('update:dialogVisible', false)
    },
    applySharing() {
      let self = this
      self.sharingDialogVisible = false
      let dashboardSharing = []
      if (self.sharedPortalUsers.length > 0) {
        for (let i = 0; i < self.sharedPortalUsers.length; i++) {
          if (self.sharedPortalUsers[i] !== self.getCurrentUser().ouid) {
            dashboardSharing.push({
              dashboardId: self.dashboard.id,
              sharingType: 4,
              orgUserId: self.sharedPortalUsers[i],
            })
          }
        }
      }
      let dashboardShre = []
      if (self.dashboardSharingcontext.length) {
        dashboardShre = self.dashboardSharingcontext.filter(
          rt => rt.sharingType !== 4
        )
        dashboardShre = dashboardShre.filter(rt => rt.sharingType !== 5)
      }
      let params = {
        dashboard: {
          id: self.dashboard.id,
          dashboardSharingContext: [...dashboardSharing, ...dashboardShre],
        },
      }
      self.$http
        .post('/dashboard/updateDashboard', params)
        .then(function(response) {
          if (self.shareTo === 4 || self.shareTo === 5) {
            self.$message({
              message: 'Published successfully!',
              type: 'success',
            })
          } else {
            self.$message({
              message: 'Sharing applied successfully!',
              type: 'success',
            })
          }
        })
      self.dialogVisible = false
      self.$emit('update:dialogVisible', self.dialogVisible)
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

.fc-share-container {
  overflow: auto;
  margin-bottom: 50px;
  max-height: 350px;
  margin-bottom: 50px;
}
.fc-share-container {
  padding: 0 !important;
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
  left: 0px;
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
.fc-share-container .el-radio__input.is-checked .el-radio__inner {
  border-color: #ee518f;
  background: #ee518f;
}
.fc-share-container .el-radio__input.is-checked + .el-radio__label {
  color: #ee518f;
}
.el-dialog.dashboardShare-dialog {
  display: block;
  padding: 0px;
}
.dashboard-share.dialog-box {
  position: relative;
  padding: 0px;
}
.el-dialog.dashboardShare-dialog .el-dialog__header {
  width: 100%;
  border-bottom: solid 1px #eaecf1;
}
.text-paragraph {
  font-size: 12px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.5;
  letter-spacing: 0.3px;
  color: #8ca1ad;
  white-space: initial;
}
.loadigContainer {
  height: 380px;
}
</style>
