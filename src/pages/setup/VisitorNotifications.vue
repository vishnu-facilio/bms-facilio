<template>
  <div class="visitor-setting-con-width mT20">
    <spinner v-if="loading" :show="loading" size="80"></spinner>

    <template v-else v-for="(preference, index) in preferenceList">
      <el-row :key="index" class="visitor-hor-card scale-up-left flex-middle">
        <el-col :span="20">
          <div class="fc-black-15 fwBold">{{ preference.displayName }}</div>
          <div class="fc-grey4-13 pT5">{{ preference.description }}</div>
        </el-col>

        <el-col :span="3" class="text-right">
          <div class="flex-middle">
            <el-switch
              v-model="preference.enabled"
              @change="toggleGroup(preference)"
            ></el-switch>
            <div class="label-txt-black pL10 fw6">
              {{ preference.enabled ? 'Enable' : 'Disabled' }}
            </div>
          </div>
        </el-col>

        <el-col :span="1">
          <div class="flex-middle">
            <el-dropdown
              class="pR15 pL15"
              trigger="click"
              v-bind:hide-on-click="false"
            >
              <span class="el-dropdown-link">
                <i class="el-icon-more visitor-type-card-more"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  v-for="(notification, name) in preference.types"
                  :key="name"
                >
                  <el-row>
                    <el-col :span="16">{{ name }}</el-col>
                    <el-col :span="8">
                      <el-switch
                        v-model="notification.enabled"
                        @change="toggleSingleNotification(notification)"
                      ></el-switch>
                    </el-col>
                  </el-row>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-col>
      </el-row>
    </template>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import groupBy from 'lodash/groupBy'

export default {
  props: [],
  data() {
    return {
      preferenceList: [],
      loading: false,
      moduleName: 'visitorlog',
    }
  },

  created() {
    this.loadNotificationList()
  },

  methods: {
    async loadNotificationList() {
      this.loading = true

      let allNotifications = await this.loadAllNotifications()
      let enabledNotifications = await this.loadEnabledNotifications()

      this.constructNotifications(allNotifications, enabledNotifications)
      this.loading = false
    },
    async loadAllNotifications() {
      let { moduleName } = this
      let url = 'v2/preference/getAllPrefslist'
      let params = { moduleName, fetchModuleSpecific: true }
      let { error, data } = await API.post(url, params)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        return data.preferenceList || []
      }
    },
    async loadEnabledNotifications() {
      let { moduleName } = this
      let url = 'v2/preference/getEnabledPrefsList'
      let { error, data } = await API.post(url, { moduleName })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        return data.preferenceList || []
      }
    },
    constructNotifications(allPref, enabledPref) {
      let notifications = allPref.map(pref => {
        let { description, displayName, enabled, name } = pref
        return { description, displayName, enabled, name }
      })

      enabledPref.forEach(enbPref => {
        let { preferenceName, id } = enbPref
        let index = notifications.findIndex(p => p.name === preferenceName)

        if (index !== -1) {
          let pref = notifications[index]

          pref = { ...pref, id, enabled: true }
          notifications.splice(index, 1, pref)
        }
      })

      let prefByDesp = groupBy(notifications, n => n.name.split('_')[0])

      this.preferenceList = Object.values(prefByDesp).map(notif => {
        let { displayName, description } = notif[0]
        let prefDisplayName = displayName.split('_')[0]
        let types = notif.reduce((types, v) => {
          let { displayName, enabled, name, id } = v
          let notiDisplayName = displayName.split('_')[1]
          let type = { displayName, enabled, name }

          if (id) type = { ...type, id }
          types[notiDisplayName] = type
          return types
        }, {})
        let enabled = Object.values(types).some(t => t.enabled === true)

        return {
          description,
          displayName: prefDisplayName,
          enabled,
          types,
        }
      })
    },
    toggleGroup(pref) {
      let { enabled, types } = pref

      Object.values(types).forEach(
        async type => await this.toggleNotification(enabled, type)
      )
      this.loadNotificationList()
    },
    async toggleSingleNotification(notif) {
      let { enabled } = notif || {}

      await this.toggleNotification(enabled, notif)
      this.loadNotificationList()
    },
    async toggleNotification(isActive, notif) {
      let { moduleName } = this
      let { name, id } = notif || {}

      if (isActive) {
        let url = 'v2/preference/enable'
        let params = { moduleName, name }
        let { error } = await API.post(url, params)

        if (error) {
          this.$message.error(error.message || 'Error Occurred')
          return
        }
      } else if (id) {
        let url = 'v2/preference/disable'
        let params = { preferenceId: id }
        let { error } = await API.post(url, params)

        if (error) {
          this.$message.error(error.message || 'Error Occurred')
          return
        }
      }
    },
  },
}
</script>
