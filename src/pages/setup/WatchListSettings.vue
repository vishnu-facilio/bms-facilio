<template>
  <div>
    <div class="visitor-setting-con-width mT20">
      <spinner v-if="loading" :show="loading" size="80"></spinner>
      <div
        v-else
        class="visitor-hor-card scale-up-left"
        v-for="(preference, index) in prefList"
        :key="index"
      >
        <el-row class="flex-middle">
          <el-col :span="20">
            <div class="fc-black-15 fwBold">{{ preference.displayName }}</div>
            <div class="fc-grey4-13 pT5">
              {{ preference.description }}
            </div>
          </el-col>
          <el-col :span="3" class="text-right">
            <div class="flex-middle">
              <div class="">
                <el-switch
                  v-model="preference.enabled"
                  @change="toggleAll($event, preference)"
                ></el-switch>
              </div>
              <div class="label-txt-black pL10 fw6">
                {{ preference.enabled ? 'Enable' : 'Disabled' }}
              </div>
            </div>
          </el-col>
          <!-- <el-col :span="1">
            <div class="flex-middle">
              <el-dropdown class="pR15 pL15" trigger="click" v-bind:hide-on-click="false">
                <span class="el-dropdown-link">
                  <i class="el-icon-more visitor-type-card-more"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item
                    v-for="(notificationType, name, index) in preference.types"
                    :key="index"
                  >
                    <el-row>
                      <el-col :span="16">
                        {{ name }}
                      </el-col>

                      <el-col :span="8">
                        <el-switch
                          @click.native.stop
                          v-model="notificationType.enabled"
                          @change="
                            handleSingleToggle(
                              $event,
                              notificationType,
                              preference
                            )
                          "
                        ></el-switch>
                      </el-col>
                    </el-row>

                  </el-dropdown-item>
                  <el-dropdown-item >
                      <el-row>
                      <el-col :v-if="16" @click.native.stop.prevent @click.stop.prevent>
                        <el-select v-model="preference.notifyUsers"  multiple  filterable collapse-tags class="width100 fc-tag fc-input-full-border-select2" popper-append-to-body="false"	 @change="dialogSave(preference)">
                      <el-option  v-for="user in users"  :key="user.id" :label="user.name" :value="user.id"></el-option>
                        </el-select >
                      </el-col>
                      </el-row>
                      </el-dropdown-item>

                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </el-col> -->
        </el-row>
      </div>
    </div>
    <f-dialog
      v-if="showSettingsDialog"
      :append-to-body="true"
      :visible.sync="showSettingsDialog"
      :width="'350px'"
      maxHeight="300px"
      title="Notification preference"
      @save="dialogSave(selectedpref)"
      @close="dialogClose"
      confirmTitle="SAVE"
    >
      <div class="overflow-y-scroll pB50">
        <div class="">
          <el-row
            v-for="(notificationType, name, index) in selectedpref.types"
            :key="index"
            class="pB20"
          >
            <el-col :span="20">
              {{ name }}
            </el-col>

            <el-col :span="4">
              <el-switch
                @click.native.stop
                v-model="notificationType.enabled"
              ></el-switch>
            </el-col>
          </el-row>

          <el-row>
            <el-col :v-if="16">
              <el-select
                v-model="selectedpref.notifyUsers"
                multiple
                filterable
                collapse-tags
                class="width100 fc-tag fc-input-full-border-select2"
              >
                <el-option
                  v-for="user in users"
                  :key="user.id"
                  :label="user.name"
                  :value="user.id"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </div>
      </div>
    </f-dialog>
  </div>
</template>
<script>
import FDialog from '@/FDialogNew'
export default {
  created() {
    this.loadNotificationList()
  },
  components: {
    FDialog,
  },
  computed: {
    users() {
      let userList = this.$store.state.users
      if (userList) {
        return userList
      }
      return []
    },
  },
  methods: {
    dialogClose() {
      showSettingsDialog = false
      this.selectedpref.enabled = false
    },
    async loadNotificationList() {
      let resp = await this.$http.post('v2/preference/getAllPrefslist', {
        moduleName: 'watchlist',
        fetchModuleSpecific: true,
      })

      let allPref = resp.data.result.preferenceList
      if (!allPref) {
        allPref = []
      }

      let enabledResp = await this.$http.post(
        'v2/preference/getEnabledPrefsList',
        {
          moduleName: 'watchlist',
        }
      )
      // group by pref display names

      let enabledPrefMap = {}
      if (enabledResp.data.result.preferenceList) {
        enabledResp.data.result.preferenceList.forEach(ele => {
          enabledPrefMap[ele.preferenceName] = ele
        })
      }

      console.log('enabled prefs are', enabledPrefMap)

      let prefTypes = {}

      allPref.forEach(ele => {
        //group based on description,
        if (!prefTypes[ele.description]) {
          prefTypes[ele.description] = {}
        }

        let notificationType = ele.displayName.split('_')[1]
        let notificationDisplayName = ele.displayName.split('_')[0]
        // ele.displayName=ele.displayName.replace("_"," ")

        prefTypes[ele.description].description = ele.description
        prefTypes[ele.description].displayName = notificationDisplayName

        if (!prefTypes[ele.description].types) {
          prefTypes[ele.description].types = {}
          prefTypes[ele.description].enabled = false
          prefTypes[ele.description].notifyUsers = null
        }
        prefTypes[ele.description].types[notificationType] = {}
        prefTypes[ele.description].types[notificationType].enabled = false

        if (enabledPrefMap[ele.name]) {
          prefTypes[ele.description].types[notificationType].enabled = true
          prefTypes[ele.description].types[notificationType].id =
            enabledPrefMap[ele.name].id
        }

        prefTypes[ele.description].types[notificationType].name = ele.name

        prefTypes[ele.description].types[notificationType].displayName =
          ele.displayName
      })

      this.prefList = Object.values(prefTypes)
      console.log('', prefTypes)
      this.prefList.forEach(pref => {
        this.setMainSwitch(pref)
      })
      this.loading = false
    },

    handleSingleToggle(val, notificationType, preference) {
      this.handleNotificationToggle(
        val,
        notificationType,
        preference.notifyUsers
      )
      this.setMainSwitch(preference)
    },
    toggleAll(val, pref) {
      // console.log('toggling all for', val, pref)

      // Object.keys(pref.types).forEach(typeKey => {
      //   pref.types[typeKey].enabled = val
      //   this.handleNotificationToggle(val, pref.types[typeKey])
      // })

      // this.setMainSwitch(pref)
      if (val) {
        pref.enabled = false
        this.showSettingsDialog = true
        this.selectedpref = pref
      } else {
        Object.keys(pref.types).forEach(typeKey => {
          pref.types[typeKey].enabled = val
          this.handleNotificationToggle(val, pref.types[typeKey])
        })
      }
    },

    dialogSave(pref) {
      Object.keys(pref.types).forEach(typeKey => {
        //send request to update all enabled pref user list
        if (pref.types[typeKey].enabled) {
          this.handleNotificationToggle(
            true,
            pref.types[typeKey],
            pref.notifyUsers.map(String)
          )
        }
      })
      pref.enabled = true
      this.showSettingsDialog = false
    },

    async handleNotificationToggle(val, notificationType, notifyUsers) {
      console.log('toggling', val, notificationType)

      let url = null
      let params = null
      if (val) {
        url = 'v2/preference/enable'
        params = {
          moduleName: 'watchlist',
          name: notificationType.name,
        }

        params.value = {
          to: notifyUsers,
        }

        //once notification is set , get id from resp and set it

        try {
          let resp = await this.$http.post(url, params)
          notificationType.id = resp.data.result.preferences.id
        } catch (e) {
          console.log(e)
        }
      } else {
        params = {
          preferenceId: notificationType.id,
        }
        url = 'v2/preference/disable'
        this.$http.post(url, params)
      }
    },

    //set main switch if atleast on sub switch is enabled
    setMainSwitch(pref) {
      pref.enabled = false

      console.log('setting main flag for ', Object.keys(pref.types))
      Object.keys(pref.types).forEach(typeKey => {
        if (pref.types[typeKey].enabled) {
          pref.enabled = true
        }
      })
    },
  },
  props: [],
  data() {
    return {
      value1: true,
      prefList: [],
      showSettingsDialog: false,
      selectedpref: null,
      loading: true,
    }
  },
}
</script>
