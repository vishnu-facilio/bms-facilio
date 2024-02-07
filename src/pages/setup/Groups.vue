<template>
  <div class="height100 width100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">{{ $t('setup.setup.teams') }}</div>
        <div class="heading-description">
          {{ $t('setup.users_management.list_all_teams') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          @click="showNewGroupTemp()"
          class="setup-el-btn"
          >{{ $t('setup.users_management.new_team') }}</el-button
        >
        <new-group
          v-if="showDialog"
          :visibility.sync="showDialog"
          :isNew="isNew"
          :group="selectedGroup"
          @saved="loadgroups"
        ></new-group>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th
                  class="setting-table-th setting-th-text uppercase"
                  style="min-width: 200px;"
                >
                  {{ $t('setup.approvalprocess.name') }}
                </th>
                <th
                  class="setting-table-th setting-th-text"
                  style="min-width: 200px;"
                >
                  {{ $t('setup.users_management.site') }}
                </th>
                <th
                  class="setting-table-th setting-th-text uppercase"
                  style="min-width: 180px;"
                >
                  {{ $t('setup.approvalprocess.description') }}
                </th>
                <th
                  class="setting-table-th setting-th-text"
                  style="min-width: 180px;"
                >
                  {{ $t('setup.users_management.created') }}
                </th>
                <th
                  class="setting-table-th setting-th-text"
                  style="min-width: 80px;"
                >
                  {{ $t('setup.users_management.status') }}
                </th>
                <th
                  class="setting-table-th setting-th-text"
                  style="min-width: 130px;"
                ></th>
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="groupList.length === 0">
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('setup.users_management.no_teams_available') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(group, index) in groupList"
                :key="group.id"
              >
                <td style="min-width: 200px;">
                  <span class="group-name">{{ group.name || '' }}</span>
                </td>
                <td style="min-width: 200px">
                  {{
                    group.siteId > 0
                      ? $store.getters.getSite(group.siteId).name
                      : 'All Sites'
                  }}
                </td>
                <td v-if="group.description" style="min-width: 180px;">
                  {{ group.description }}
                </td>
                <td v-else style="min-width: 180px;">
                  ---
                </td>
                <td style="min-width: 180px;">
                  <div v-if="group.createdTime > 0">
                    {{ group.createdTime | fromNow }}
                  </div>
                  <div v-else>---</div>
                </td>
                <td style="text-align: left;min-width: 80px;">
                  <el-switch
                    v-model="group.isActive"
                    @change="changeStatus(group)"
                    class="Notification-toggle"
                    active-color="rgba(57, 178, 194, 0.8)"
                    inactive-color="#e5e5e5"
                  ></el-switch>
                </td>
                <td style="min-width: 130px;">
                  <div
                    class="text-left actions"
                    style="margin-top:-3px;margin-right: 15px;text-align:center;"
                  >
                    <i
                      class="el-icon-edit pointer"
                      @click="editGroup(group)"
                    ></i>
                    &nbsp;&nbsp;
                    <i
                      class="el-icon-delete pointer"
                      @click="deleteGroup(group.groupId, index)"
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
import NewGroup from 'pages/setup/new/NewGroup'
import { mapState } from 'vuex'
import { API } from '@facilio/api'

export default {
  title() {
    return 'Teams'
  },
  components: {
    NewGroup,
  },
  computed: {
    ...mapState({
      groupList: state => state.groups || [],
    }),
  },
  data() {
    return {
      activateuser: true,
      showCreateNewGroupDialog: false,
      loading: false,
      selectedGroup: {
        name: null,
        isActive: null,
        id: null,
        description: null,
        groupId: null,
        siteId: null,
      },
      // selectedGroup: null,
      isNew: false,
      showNewGroup: false,
      showDialog: false,
    }
  },
  created() {
    this.loadgroups()
  },
  methods: {
    editGroup(group) {
      this.selectedGroup = group
      this.isNew = false
      this.showNewGroup = true
      this.showDialog = true
    },
    showNewGroupTemp() {
      this.selectedGroup = null
      this.showNewGroup = true
      this.isNew = true
      this.showDialog = true
    },
    changeStatus(groupinfo) {
      const formData = new FormData()
      formData.append('group.GroupId', groupinfo.groupId)
      formData.append('group.isActive', groupinfo.isActive)
      let { error } = API.post('/setup/changeTeamStatus', formData)
      if (!error) {
        this.$message(this.$t('setup.users_management.group_status_success'))
      }
    },
    async deleteGroup(groupId) {
      let value = await this.$dialog.confirm({
        title: this.$t('setup.users_management.delete_team'),
        message: this.$t('setup.users_management.team_delete_permanently'),
        rbDanger: true,
        rbLabel: this.$t('setup.users_management.delete'),
      })
      if (value) {
        let { error } = await API.post('/setup/deletegroup', { groupId })

        if (!error) {
          this.loadgroups()
        }
      }
    },
    async loadgroups() {
      this.loading = true
      await this.$store.dispatch('loadGroups', true)
      this.loading = false
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
.pointer {
  cursor: pointer;
}
</style>
<style>
.fc-create-record {
  width: 45% !important;
}
</style>
