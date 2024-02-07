<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">{{ $t('setup.setup.requesters') }}</div>
        <div class="heading-description">
          {{ $t('setup.users_management.list_of_requesters') }}
        </div>
      </div>

      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="showDialog = true"
          >{{ $t('setup.users_management.add_requester') }}</el-button
        >
        <new-requester
          v-if="showDialog"
          :visibility.sync="showDialog"
          @saved="loadusers"
        ></new-requester>
      </div>
    </div>
    <div class="container-scroll">
      <subheader :menu="subheaderMenu" parent="app/setup/resource/"></subheader>
      <div class="row setting-Rlayout">
        <div class="col-lg-12 col-md-12 mT60">
          <table class="setting-list-view-table" width="100%">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('setup.approvalprocess.name') }}
                </th>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('setup.setup_profile.email') }}
                </th>
                <!-- <th class="setting-table-th setting-th-text">LAST LOGIN</th> -->
                <th class="setting-table-th setting-th-text">
                  {{ $t('setup.users_management.reinvite') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody
              v-else-if="
                !userlist ||
                  userlist.filter(user => user.inviteAcceptStatus === false)
                    .length == 0
              "
            >
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('setup.users_management.no_req_available') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(user, index) in userlist"
                :key="index"
                v-if="user.inviteAcceptStatus === false"
              >
                <td>
                  <user-avatar size="md" :user="user"></user-avatar>
                </td>
                <td>{{ user.email }}</td>
                <td>
                  <el-button
                    @click="reinvite(user)"
                    type="text"
                    :send="sending"
                  >
                    {{
                      sending === user.id
                        ? $t('common._common._saving')
                        : $t('setup.users_management.reinvite')
                    }}
                  </el-button>
                </td>
                <!-- <button class="btn btn--tertiary" @click="deleteUser(user)">Delete</button> -->
                <td style="width: 10%;">
                  <div
                    class="text-left actions"
                    style="margin-top:-3px;margin-right: 15px;text-align:center;"
                  >
                    <i
                      class="el-icon-delete pointer"
                      data-arrow="true"
                      title="Delete requester"
                      v-tippy
                      @click="deleteUser(user)"
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
import UserAvatar from '@/avatar/User'
import Subheader from '@/Subheader'
import NewRequester from 'pages/setup/new/NewRequester'
// import http from 'util/http'
export default {
  title() {
    return 'Requesters'
  },
  components: {
    NewRequester,
    UserAvatar,
    Subheader,
  },
  data() {
    return {
      subheaderMenu: [
        {
          label: this.$t('setup.users_management.portal_users'),
          path: { path: '/app/setup/resource/requesters' },
        },
        {
          label: this.$t('setup.setup.requesters'),
          path: { path: '/app/setup/resource/pendingRequesters' },
        },
      ],
      activateuser: true,
      showDialog: false,
      loading: true,
      userlist: [],
      sending: null,
    }
  },
  mounted: function() {
    this.loadusers()
  },
  methods: {
    deleteUser(user) {
      let self = this
      const formData = new FormData()
      formData.append('user.email', user.email)
      self.$dialog
        .confirm({
          title: self.$t('setup.users_management.delete_req_msg'),
          message:
            self.$t('setup.users_management.delete_user_msg2 ') +
            'requester ' +
            user.name +
            ' ' +
            '?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(function(value) {
          if (value) {
            self.$http
              .post('/setup/deleteportaluser', formData)
              .then(function(response) {
                if (
                  response.data &&
                  response.data.userId &&
                  response.data.userId > 0
                ) {
                  self.userlist.splice(self.userlist.indexOf(user), 1)
                  self.$message.success(
                    self.$t('setup.users_management.user_delete_success')
                  )
                } else {
                  if (response.data.error) {
                    self.$message.error(response.data.error)
                  }
                }
              })
          }
        })
        .catch(function(error) {
          console.log(error)
          self.$message.error(self.$t('setup.users_management.error_occured'))
        })
    },
    loadusers: function() {
      let that = this
      that.$http
        .get('/setup/portalusers')
        .then(function(response) {
          that.userlist = response['data'].users
          that.loading = false
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    // pendingusers () {
    //   if (activateuser = false) {
    //     this.userlist = response['data'].users
    // },
    aftersave() {
      // this.$refs.showNewGroup.close()
      console.log('Requester added')
      this.loadusers()
    },
    reinvite(user) {
      let self = this
      self.sending = user.id
      self.$http
        .post('setup/resendinvite', { userId: user.id, isPortal: true })
        .then(function(response) {
          JSON.stringify(response)
          if (response.status === 200) {
            console.log('---->>respose', response)
            self.$message.success(
              self.$t('setup.users_management.invitation_send_success')
            )
            self.sending = -1
          }
        })
        .catch(function(error) {
          self.sending = -1
          console.log('------->>>', error)
          self.$message.error(self.$t('setup.users_management.error_occured'))
        })
    },
    closeDialog: function() {
      this.$refs.createNewModel.close()
      this.$router.push({ path: '/app/setup/resource/pendingRequesters' })
    },
  },
}
</script>
<style scoped>
table.dataTable > tbody > tr > td {
  padding: 10px;
  vertical-align: middle;
  border-spacing: 0;
  border-collapse: collapse;
}
table.dataTable thead > tr > th {
  padding: 5px 10px 2px 10px;
  color: #6f7175;
  vertical-align: top;
  font-weight: 400;
  font-size: 13px;
  border-bottom: 0px;
}
table.dataTable tr.odd {
  background-color: '#fafafa';
}

table.dataTable tr.even {
  background-color: blue;
}

div.dataTables_info {
  padding-top: 8px;
  white-space: nowrap;
  padding-left: 10px;
}

div.dataTables_info,
div.dataTables_paginate {
  padding: 18px;
  white-space: nowrap;
}

div.row-title {
  font-weight: 400;
}

div.row-subtitle {
  font-weight: 400;
  color: #6f7175;
}

.dataTable tbody tr:hover {
  background: #fafafa;
  cursor: pointer;
}

.dataTable tr th .checkbox {
  padding-left: 17px !important;
}

.dataTable tbody tr:last-child td {
  border-bottom: 1px solid #e7e7e7 !important;
}

.dataTable > tbody > tr:first-child > td {
  border-top: 0px;
}

div.row.content-center {
  padding-top: 100px;
  padding-bottom: 144px;
}

table.dataTable.dtr-inline.collapsed > tbody > tr > td:first-child:before,
table.dataTable.dtr-inline.collapsed > tbody > tr > th:first-child:before {
  background-color: #50ca7c;
  font-size: 16px;
  line-height: 16px;
  display: none;
}
.no-screen-msg .row-title {
  font-size: 17px;
  color: #212121;
  padding: 10px 0;
}
.no-screen-msg .row-subtitle {
  font-size: 13px;
  padding: 1px 0px;
}

.dataTable tbody tr.selected {
  background: rgba(14, 153, 227, 0.1);
}
.record-list,
.record-summary {
  padding: 0;
  transition: all 0.3s;
}
.more-actions .dropdown-toggle {
  color: #d8d8d8;
  font-size: 18px;
}

.more-actions .dropdown-toggle:hover {
  color: #000000;
}

.more-actions .dropdown-menu {
  right: 0;
  left: initial;
}
.toggle-switch label {
  position: relative;
  display: block;
  height: 12px;
  width: 30px;
  background: #999;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.08s linear;
}

.toggle-switch label:after {
  position: absolute;
  left: 0;
  top: -2px;
  display: block;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #fafafa;
  box-shadow: rgba(0, 0, 0, 0.4) 0px 1px 3px 0px;
  content: '';
  transition: 0.08s linear;
}

.toggle-switch label:active:after {
  transform: scale(1.15, 0.85);
}

.toggle-switch .checkbox:checked ~ label {
  background: rgba(80, 202, 124, 0.5);
}

.toggle-switch .checkbox:checked ~ label:after {
  left: 14px;
  background: #50ca7c;
}

.toggle-switch .checkbox:disabled ~ label {
  background: #d5d5d5;
  cursor: not-allowed;
  pointer-events: none;
}

.toggle-switch .checkbox:disabled ~ label:after {
  background: #bcbdbc;
}
.fc-white-theme ul.subheader-tabs li.active a,
.fc-white-theme ul.subheader-tabs li:hover a {
  color: #25243e;
}
.setting-Rlayout {
  padding: 1rem 1.7rem !important;
}
</style>
