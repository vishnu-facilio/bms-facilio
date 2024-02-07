<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Email Settings</div>
        <div class="heading-description">List of all Support Email</div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button type="primary" @click="showSupportMail" class="setup-el-btn"
          >Add Email
          <new-supportemail
            v-if="showDialog"
            :visibility.sync="showDialog"
            :supportEmailContext="editingSupportEmail"
            @onsave="appendSupportEmail"
          ></new-supportemail>
        </el-button>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">NAME</th>
                <th class="setting-table-th setting-th-text">Forwarded to</th>
                <th class="setting-table-th setting-th-text">Team</th>
                <th class="setting-table-th setting-th-text">Site</th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="!supportEmails || !supportEmails.length">
              <tr class="tablerow">
                <td
                  colspan="100%"
                  class="text-center"
                  style="padding-top: 18px;padding-bottom: 18px;"
                >
                  No supportEmail added yet
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                v-for="(supportEmail, idx) in supportEmails"
                :key="supportEmail.id"
                class="tablerow"
              >
                <td tyle="width: 150px;">
                  <div class=" mailfont">{{ supportEmail.replyName }}</div>
                  <div class=" content">{{ supportEmail.actualEmail }}</div>
                </td>
                <td style="width: 150px;">
                  <div class=" content">{{ supportEmail.fwdEmail }}</div>
                </td>
                <td tyle="width: 150px;">
                  <div class=" content">
                    {{
                      supportEmail.autoAssignGroupId > 0
                        ? $store.getters.getGroup(
                            supportEmail.autoAssignGroupId
                          ).name
                        : '---'
                    }}
                  </div>
                </td>
                <td tyle="width: 200px;">
                  <div class=" content">
                    {{
                      supportEmail.siteId > 0
                        ? getSiteName(supportEmail.siteId)
                        : '---'
                    }}
                  </div>
                </td>
                <td>
                  <div
                    class="text-left actions"
                    style="margin-top:-3px;margin-right: 15px;text-align:center;width: 100px;"
                  >
                    <i
                      class="el-icon-edit pointer"
                      @click="editEmail(supportEmail)"
                    ></i>
                    &nbsp;&nbsp;
                    <i
                      class="el-icon-delete pointer"
                      @click="deleteEmail(supportEmail.id, idx)"
                    ></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import NewSupportemail from 'pages/setup/new/newSupportemail'
export default {
  title() {
    return 'Email Settings'
  },
  components: {
    NewSupportemail,
  },
  data() {
    return {
      loading: true,
      isEdit: true,
      editingSupportEmail: null,
      showCreateNewUserDialog: false,
      newsupportmail: false,
      visibility: false,
      emailNotifications: [],
      supportEmails: [],
      showDialog: false,
      sites: [],
    }
  },
  created() {
    this.$store.dispatch('loadGroups')
  },
  mounted: function() {
    this.loadnotifications()
    this.$util.loadSpace(1).then(response => {
      this.sites = response.basespaces
    })
  },
  watch: {
    supportEmail: function(newVal) {
      this.loadnotifications()
    },
  },
  methods: {
    menuClick: function(event) {
      alert(event)
    },
    showNewUser: function() {
      this.showCreateNewUserDialog = true
    },
    showSupportMail() {
      this.editingSupportEmail = null
      this.showDialog = true
    },
    close: function() {
      this.showCreateNewUserDialog = false
    },
    closeDialog() {
      // this.visibility = false
      this.$emit('update:visibility', false)
      this.reset()
      // this.visibilityState = false
    },
    getSiteName(siteId) {
      let site = this.sites.find(site => site.id === siteId)
      return site ? site.name : ''
    },
    editEmail(supportEmail) {
      this.editingSupportEmail = this.$helpers.cloneObject(supportEmail)
      // this.$refs.createFieldModel.open()
      this.showCreateNewUserDialog = true
      this.showDialog = true
    },
    deleteEmail(id, idx) {
      this.$dialog
        .confirm({
          title: 'Delete the email setting?',
          message: 'Are you sure you want to delete this Email setting?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            this.$http
              .post('/setup/deleteemailsettings', { supportEmailId: id })
              .then(response => {
                if (typeof response.data === 'object') {
                  this.$message.success('Deleted successfully')
                  this.supportEmails.splice(idx, 0)
                } else {
                  this.$message.error('Deletion failed')
                }
              })
          }
        })
    },
    loadnotifications: function() {
      let that = this
      that.$http
        .get('/setup/emailsettings')
        .then(function(response) {
          that.emailNotifications = response['data'].emailSetting
          that.supportEmails = response['data'].supportEmails
          that.loading = false
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    appendSupportEmail: function() {
      this.loadnotifications()
    },
  },
}
</script>
<style>
.borderTop,
.borderBottom {
  width: 100%;
  height: 1px;
  background-color: #eeeeee;
  margin-bottom: 3px;
}
.btn-sm {
  padding: 5px 10px;
  font-size: 12px;
  line-height: 1.5;
  border-radius: 3px;
}
.btn-default {
  color: #333;
  background-color: #fff;
  border-color: #ccc;
}
.divider {
  margin-top: 6px;
  margin-bottom: 6px;
}
.title {
  font-size: 18px;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
}
.mailfont {
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.4px;
  margin-top: -10px;
  text-align: left;
  color: #000000;
}
.subhead {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #666666;
  margin-top: -20px;
}
.content {
  font-size: 14px;
  letter-spacing: 0.3px;
  text-align: left;
  color: #333333;
}
.fwdtitle {
  font-size: 13px;
  letter-spacing: 0.4px;
  margin-top: -10px;
  text-align: left;
  color: #666666;
}
/*.btn {
    padding: 6px 12px;
    margin-bottom: 0;
    font-size: 14px;
    font-weight: 400;
    line-height: 1.42857143;
    text-align: center;
    white-space: nowrap;
    vertical-align: middle;
    -ms-touch-action: manipulation;
    /* touch-action: manipulation; */
/* cursor: pointer; */
/*-webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    background-image: none;
    border: 1px solid transparent;
    border-radius: 4px;
}*/
</style>
