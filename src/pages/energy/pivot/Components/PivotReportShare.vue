<template>
  <div v-if="show">
    <f-dialog
      v-if="show"
      :visible.sync="show"
      width="30%"
      @save="share(reportDetails)"
      @close="$emit('cancel')"
      :confirmTitle="$t('common._common._save')"
      :stayOnSave="true"
      customClass="qr-dialog"
    >
      <div class="fc-setup-modal-title">
        {{ $t('common.wo_report.sharing_permission') }}
      </div>
      <div class="mT10">
        <div>
          <el-radio
            v-model="shareTo"
            :label="1"
            class="fc-radio-btn pB10 pT10"
            >{{ $t('common.wo_report.only_me') }}</el-radio
          >
        </div>
        <div>
          <el-radio v-model="shareTo" :label="2" class="fc-radio-btn pB10">{{
            $t('common.wo_report.everyone')
          }}</el-radio>
        </div>
        <div>
          <el-radio v-model="shareTo" :label="3" class="fc-radio-btn pB10">{{
            $t('common.wo_report.specific')
          }}</el-radio>
        </div>
        <el-row v-if="shareTo === 3" class="mT20 el-select-block">
          <el-col :span="24">
            <div class="label-txt-black pB5">
              {{ $t('common.wo_report.team') }}
            </div>
            <el-select
              filterable
              v-model="sharedGroups"
              multiple
              collapse-tags
              class="width100 fc-full-border-select-multiple2"
              :placeholder="$t('common.wo_report.choose_teams')"
            >
              <el-option
                v-for="group in groups"
                :key="group.id"
                :label="group.name"
                :value="group.id"
              >
              </el-option>
            </el-select>
          </el-col>
        </el-row>

        <el-row v-if="shareTo === 3" class="mT20 el-select-block">
          <el-col :span="24">
            <div class="label-txt-black pB5">
              {{ $t('common.wo_report.role') }}
            </div>
            <el-select
              filterable
              v-model="sharedRoles"
              multiple
              collapse-tags
              class="width100 fc-full-border-select-multiple2"
              :placeholder="$t('common.wo_report.choose_roles')"
            >
              <el-option
                v-for="role in roles"
                :key="role.id"
                :label="role.name"
                :value="role.id"
              >
              </el-option>
            </el-select>
          </el-col>
        </el-row>

        <el-row v-if="shareTo === 3" class="mT20 el-select-block">
          <el-col :span="24">
            <div class="label-txt-black pB5">
              {{ $t('common.wo_report.staff') }}
            </div>
            <el-select
              filterable
              v-model="sharedUsers"
              multiple
              collapse-tags
              class="width100 fc-full-border-select-multiple2"
              :placeholder="$t('common.wo_report.choose_users')"
            >
              <el-option
                v-for="user in users"
                :key="user.id"
                :label="user.name"
                :value="user.id"
              >
              </el-option>
            </el-select>
          </el-col>
        </el-row>
      </div>
      <div style="padding: 25px 0px;"></div>
    </f-dialog>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import FDialog from '@/FDialogNew'
import { mapState, mapGetters } from 'vuex'
import { API } from '@facilio/api'
import { getFieldOptions } from 'util/picklist'
import { getApp } from '@facilio/router'

export default {
  components: {
    FDialog,
  },
  props: ['showPopUp', 'reportDetails'],
  emits: ['cancel'],

  data() {
    return {
      reportShareData: [],
      shareTo: 2,
      sharedGroups: [],
      sharedUsers: [],
      sharedRoles: [],
      reportSharing: {},
      roles: [],
      users: [],
      show: false,
    }
  },
  created() {
    this.$store.dispatch('loadGroups'), this.getdetails()
  },

  computed: {
    ...mapState({
      groups: state => state.groups,
    }),

    ...mapGetters(['getCurrentUser']),

    showDialogPopup() {
      return this.showPopUp
    },
  },
  watch: {
    showDialogPopup(newValue) {
      if (newValue == true) {
        this.setDefaultShare()
      } else if (newValue == false) {
        this.show = false
      }
    },
  },
  methods: {
    empty() {
      this.sharedGroups = []
      this.sharedRoles = []
      this.sharedUsers = []
    },
    async loadUsersList() {
      let appId = getApp().id || null

      let { error, options } = await getFieldOptions({
        field: {
          lookupModuleName: 'users',
          filters: { applicationId: { value: [appId] } },
        },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.users = options.map(user => ({
          name: user.label,
          id: user.value,
        }))
      }
    },
    async loadRoleList() {
      let appId = getApp().id || null
      if (isEmpty(appId)) return
      let params = {
        appId,
      }
      let { data, error } = await API.get('/setup/roles', params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.roles = data.roles || []
      }
    },
    getdetails() {
      this.loadUsersList()
      this.loadRoleList()
    },
    async setDefaultShare() {
      this.empty()
      let response = await API.get('v3/report/share/get', {
        reportId: this.reportDetails.id || -1,
      })
      response = response?.data?.reportShareDetails
      if (
        response.length == 1 &&
        response[0].userId == this.getCurrentUser().ouid
      ) {
        this.shareTo = 1
      } else if (response.length > 0) {
        this.shareTo = 3
        for (let i = 0; i < response.length; i++) {
          if (response[i].type === 1) {
            this.sharedUsers.push(response[i].userId)
          } else if (response[i].type === 2) {
            this.sharedRoles.push(response[i].roleId)
          } else if (response[i].type === 3) {
            this.sharedGroups.push(response[i].groupId)
          }
        }
      } else {
        this.shareTo = 2
      }
      this.show = true
    },
    share(reportDetails) {
      this.reportSharing.reportId = reportDetails.id || null
      if (this.shareTo === 1) {
        this.reportShareData.push({
          type: 1,
          userId: this.getCurrentUser().ouid,
        })
      } else if (this.shareTo === 3) {
        if (this.sharedUsers.length > 0) {
          for (let i = 0; i < this.sharedUsers.length; i++) {
            this.reportShareData.push({
              type: 1,
              userId: this.sharedUsers[i],
            })
          }
        }

        if (this.sharedRoles.length > 0) {
          for (let i = 0; i < this.sharedRoles.length; i++) {
            this.reportShareData.push({
              type: 2,
              roleId: this.sharedRoles[i],
            })
          }
        }

        if (this.sharedGroups.length > 0) {
          for (let i = 0; i < this.sharedGroups.length; i++) {
            this.reportShareData.push({
              type: 3,
              groupId: this.sharedGroups[i],
            })
          }
        }
      }
      this.reportSharing.reportShareInfo = this.reportShareData || {}
      API.post('v3/report/share/create', this.reportSharing).catch(error =>
        this.$error.message(error)
      )
      ;(this.reportShareData = []), (this.shareTo = 2)
      this.empty()
      this.$emit('cancel')
    },
  },
}
</script>

<style></style>
