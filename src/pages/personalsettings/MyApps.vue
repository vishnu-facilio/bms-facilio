<template>
  <div class="user-myapp-list">
    <div>
      <el-table
        :data="myAppsList"
        style="width: 100%"
        height="calc(100vh - 213px)"
        class="fc-setup-table fc-setup-table-p0 fc-setup-table-th-borderTop fc-setup-table-padding10"
        :fit="true"
      >
        <el-table-column width="200" prop="appName" label="App Name">
          <template v-slot="myApps">
            {{ $getProperty(myApps, 'row.application.name') }}
          </template>
        </el-table-column>
        <el-table-column width="480" prop="description" label="Description">
          <template v-slot="myApps">
            {{ $getProperty(myApps, 'row.application.description', '---') }}
          </template>
        </el-table-column>
        <el-table-column width="240" label="Role" prop="roleName">
          <template v-slot="myApps">
            {{ $getProperty(myApps, 'row.role.name', '---') }}
          </template>
        </el-table-column>
        <el-table-column width="340" prop="isDefaultApp">
          <template v-slot="myApps">
            <div class="default-apps-icons">
              <div v-if="$getProperty(myApps, 'row.isDefaultMobileApp')" class="default-app-containter">
                <fc-icon color="#4aaab7" size="15" group="platform" name="mobile"></fc-icon>
                <span class="default-div">
                  {{ $t('setup.workordersettings.default') }}
                </span>
              </div>
              <div v-if="$getProperty(myApps, 'row.isDefaultApp')" class="default-app-containter">
                <fc-icon size="15" color="#4aaab7" group="platform" name="laptop"></fc-icon>
                <span class="default-div">
                  {{ $t('setup.workordersettings.default') }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="id">
          <template v-slot="myApps">
            <MoreOptionPopover
              v-if="myApps.row.showDefaultAppOption"
              :key="'popover-' + myApps.id + myApps.isDefaultApp + myApps.isDefaultMobileApp"
              :data="myApps"
              @refresh="(data) => listMyAppsForUser()"
            ></MoreOptionPopover>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import MoreOptionPopover from 'src/pages/setup/users/MoreOptionPopover.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  data() {
    return {
      loading: false,
      myAppsList: [],
      defaultWebApp : null,
      defaultMobileApp : null
    }
  },
  created() {
    this.listMyAppsForUser()
  },
  components: {
    MoreOptionPopover,
  },
  computed: {
    summaryId() {
      let id = this.$getProperty(this, '$route.params.id', -1)
      return parseInt(id)
    },
    applicationId() {
      let appId = this.$getProperty(this, '$route.params.appId', -1)
      return parseInt(appId)
    },
  },
  methods: {
    refreshList() {
      this.listMyAppsForUser()
    },
    async listMyAppsForUser() {
      this.loading = true
      let { error, data } = await API.get(`v3/myApps/listMyApps`, {
      },{ cacheTimeout : 0 })
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        if(!isEmpty(data.myApps)) {
          let filteredMyApps = []
          data.myApps.forEach(myApp => {
            if(!isEmpty(myApp.application)) {
              let { application } = myApp || {}
              if(application.appCategoryEnum === "FEATURE_GROUPING" || application.appCategoryEnum === "WORK_CENTERS") {
                  myApp.showDefaultAppOption = true
              } else {
                  myApp.showDefaultAppOption = false
              }
              filteredMyApps.push(myApp)
            }
          })
          this.myAppsList = filteredMyApps
        }
      }
      this.loading = false
    },
  },
}
</script>
<style lang="scss">
  .user-myapp-list{
    .fc-setup-summary .fc-setup-summary-card .el-card__body {
      padding: 0px;
    }
  }
</style>
<style scoped>
.flex-space-between {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}
.font12 {
  font-size: 13px;
}
.default-app-containter {
  display : flex;
  align-items: center;
  justify-content: space-between;
  width: 80px;
  height: 25px;
  background-color: rgba(74, 170, 183, 0.1);
  color: #4aaab7;
  border-radius: 5px;
  padding : 4px 8px;
  font-weight: 500;
  font-size: 13px;
}
.default-apps-icons {
  display : flex;
  align-items: center;
  justify-content: space-between;
  width: 180px;
}
</style>
