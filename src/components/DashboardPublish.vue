<template>
  <div>
    <el-dialog
      title="Publish to Portal"
      :visible.sync="visible"
      width="30%"
      custom-class="dashboard-publish-dialog"
      :before-close="close"
    >
      <div
        class=""
        :style="{ height: '400px', overflow: 'auto', 'padding-bottom': '20px' }"
      >
        <div v-if="loading">
          <spinner :show="loading" size="80"></spinner>
        </div>
        <template v-else>
          <div
            class="ptp-container border-11"
            v-for="(portalApp, index) in portalApps"
            :key="index"
          >
            <div class="ptp-header">
              {{ portalApp.name }}
              <div class="pull-right">
                <el-switch v-model="publish[portalApp.id].enable"></el-switch>
              </div>
            </div>
            <div class="ptp-dis">
              <div>
                {{ portalApp.description }}
              </div>
            </div>
            <div class="ptp-user" v-if="publish[portalApp.id].enable">
              <el-row :gutter="20">
                <el-col :span="10">
                  <el-select
                    v-model="publish[portalApp.id].publishingType"
                    class="fc-input-full-border-select2 width100"
                  >
                    <el-option label="All users" :value="1"></el-option>
                    <el-option label="Specific users" :value="2"></el-option>
                  </el-select>
                </el-col>
                <el-col
                  :span="10"
                  v-if="publish[portalApp.id].publishingType === 2"
                >
                  <el-select
                    v-model="publish[portalApp.id]['users']"
                    multiple
                    collapse-tags
                    style="width:100%"
                    class="form-item fc-tag fc-input-full-border-select2 pull-right"
                    :placeholder="$t('common.wo_report.choose_users')"
                  >
                    <el-option
                      v-for="user in getAppBasedUsersList(portalApp.id)"
                      :key="user.id"
                      :label="user.name"
                      :value="user.id"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
            </div>
          </div>
        </template>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="close()">Cancel</el-button>
        <el-button class="modal-btn-save" type="primary" @click="update()"
          >Publish</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
export default {
  props: ['dashboardId', 'visible'],
  data() {
    return {
      loading: false,
      allUsers: [],
      systemApps: [],
      publish: {},
      dashboardPublishingContext: [],
    }
  },
  async created() {
    this.loadPortalUsers() // only portal users
  },
  computed: {
    portalApps() {
      let {
        appCategory: { PORTALS },
      } = this.$constants

      return this.systemApps.filter(app => app.appCategory === PORTALS)
    },
  },
  async mounted() {
    if (!isEmpty(this.dashboardId)) {
      this.loading = true
      this.loadSystemApps()
    }
  },
  methods: {
    decodePublishData() {
      let dashboardPublishingContext = []
      let { publish, dashboardId } = this
      Object.keys(publish).forEach(appId => {
        let { publishingType, users, enable } = publish[appId]
        if (enable) {
          if (publishingType === 2 && users.length) {
            publish[appId].users.forEach(userId => {
              let data = {
                appId: appId,
                dashboardId: dashboardId,
                publishingType: 2,
                orgUserId: userId,
              }
              dashboardPublishingContext.push(data)
            })
          } else if (publish[appId].publishingType === 1) {
            let data = {
              appId: appId,
              dashboardId: dashboardId,
              publishingType: 1,
            }
            dashboardPublishingContext.push(data)
          }
        }
      })
      return {
        dashboard: {
          id: this.dashboardId,
          dashboardPublishingContext: dashboardPublishingContext,
        },
      }
    },
    async publishDashboard(publishData) {
      if (!isEmpty(publishData)) {
        let { error } = await API.post(`dashboardpublish/update`, publishData)
        if (error) {
          this.$message.success(error)
        } else {
          this.$message.success('Published Successfully')
        }
      }
    },
    async loadPortalUsers() {
      let { data, error } = await API.get(`setup/allPortalUsers`)
      if (!error) {
        this.allUsers = data.users || []
      }
    },
    getAppBasedUsersList(appId) {
      return this.allUsers.filter(user => user.applicationId === appId)
    },
    defaultPublishData() {
      let { portalApps, publish } = this
      portalApps.forEach(app => {
        this.$set(publish, app.id, {
          enable: false,
          users: [],
          publishingType: 1,
        })
      })
    },
    preparePublishData() {
      let { dashboardPublishingContext, publish } = this

      // to decoding publish context to local context
      dashboardPublishingContext.forEach(context => {
        let users =
          publish[context.appId] && publish[context.appId][users]
            ? publish[context.appId][users]
            : []
        let data = {
          enable: true,
          publishingType: context.publishingType,
          users: [...users, context.orgUserId],
        }
        this.$set(publish, context.appId, data)
      })
    },
    async getDashboardPublishData() {
      let { dashboardId } = this
      let { data, error } = await API.get(`dashboardpublish/${dashboardId}`)
      if (!error) {
        this.dashboardPublishingContext = data.dashboardPublishing || []
        this.preparePublishData()
        this.loading = false
      }
    },
    async loadSystemApps() {
      let { data, error } = await API.get(`v2/application/list`)
      if (!error) {
        this.systemApps = data.application || []

        // temp till newapp is not required
        this.systemApps.forEach(app => {
          if ('newapp' === app.linkName) {
            app.linkName = 'app'
          }
        })
        this.defaultPublishData()
        this.getDashboardPublishData()
      }
    },
    close() {
      this.$emit('update:visible', false)
    },
    updateVisiblity() {
      this.close()
    },
    update() {
      this.publishDashboard(this.decodePublishData())
      this.close()
    },
  },
}
</script>
<style>
.ptp-container {
  padding: 15px;
  border-radius: 2px;
  margin-bottom: 10px;
}
.ptp-header {
  padding-bottom: 10px;
  font-weight: 500;
  font-size: 13px;
}
.ptp-dis {
  padding-bottom: 20px;
  padding-top: 0px;
  font-size: 13px;
  color: #8ca1ad;
}
.ptp-user {
  padding-bottom: 10px;
}
.dashboard-publish-dialog .el-dialog__header {
  border-bottom: 1px solid #ededed;
  padding-bottom: 15px;
}
</style>
