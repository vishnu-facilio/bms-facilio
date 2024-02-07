<template>
  <div class="height100 width100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">{{ $t('common.header.labour') }}</div>
        <div class="heading-description">
          {{ $t('common._common.list_of_labour') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          @click="showNewLabour()"
          class="setup-el-btn"
          >{{ $t('common.header.add_labour') }}</el-button
        >
        <new-labour
          ref="createNewModel"
          :isNew="isNew"
          v-if="showCreateNewLabourDialog"
          @saved="labourSaved"
          @close="closeAddDialog"
          :visibility.sync="showCreateNewLabourDialog"
          :labour="this.labour"
        ></new-labour>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('setup.users_management.name') }}
                </th>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('common._common.phone_number') }}
                </th>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('common.products.user') }}
                </th>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('common.header.rate_per_hour') }}
                </th>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('common.header.status') }}
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
            <tbody v-else-if="labourlist.length === 0">
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('common._common.no_labour_added') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="labour in labourlist"
                :key="labour.id"
              >
                <td>
                  <span class="role-name">{{ labour.name }}</span>
                </td>
                <td v-if="labour.phone">
                  {{ labour.phone }}
                </td>
                <td v-else>
                  ---
                </td>
                <td v-if="labour.user">
                  {{ labour.user.name }}
                </td>
                <td v-else>
                  ---
                </td>
                <td v-if="labour.cost">
                  <currency
                    class="text-right pR40"
                    :value="labour.cost"
                  ></currency>
                </td>
                <td v-else>
                  ---
                </td>
                <td v-if="labour.availability">
                  {{ $t('common._common.active') }}
                </td>
                <td v-else>
                  {{ $t('common._common.inactive') }}
                </td>
                <td>
                  <div
                    class="text-left actions"
                    style="margin-top:-3px;margin-right: 15px;text-align:center;"
                  >
                    <i
                      class="el-icon-edit pointer"
                      @click="showEditLabour(labour)"
                    ></i>
                    <!-- &nbsp;&nbsp;
                <i class="el-icon-delete pointer" @click="deleteLabour(labour)"></i> -->
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
import NewLabour from 'pages/setup/new/NewLabour'
import { API } from '@facilio/api'

export default {
  title() {
    return 'Labour'
  },
  components: {
    NewLabour,
  },
  data() {
    return {
      activateuser: true,
      showCreateNewLabourDialog: false,
      loading: true,
      labourlist: [],
      isNew: true,
      labour: {},
      user: {},
    }
  },
  mounted: function() {
    this.loadLabour()
  },
  methods: {
    menuClick: function(event) {
      alert(event)
      this.$refs.popover1.close()
    },
    showNewLabour: function() {
      this.labour = null
      this.isNew = true
      this.showCreateNewLabourDialog = true
    },
    showEditLabour(labour) {
      this.labour = this.$helpers.cloneObject(labour)
      if (labour.siteId) {
        this.labour.siteId = labour.siteId.toString()
      }
      if (this.labour.user) {
        this.labour.user.id = parseInt(labour.user.id)
      }
      this.isNew = false
      this.showCreateNewLabourDialog = true
    },
    close: function() {
      this.showCreateNewLabourDialog = false
    },
    async deleteLabour(labour) {
      let { error } = await API.deleteRecord('labour', labour.id)
      if (!error) {
        this.loadLabour()
      } else {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      }
    },
    loadLabour: async function() {
      this.loading = true
      let { list, error } = await API.fetchAll('labour', {
        page: 1,
        perPage: 5000,
        withCount: true,
      })
      if (!error) {
        this.labourlist = list
        this.loading = false
      } else {
        this.$message.error(this.$t('common._common.could_not_fetch_module'))
      }
    },
    labourSaved() {
      this.showCreateNewLabourDialog = false
      this.loadLabour()
    },
    closeAddDialog() {
      this.showCreateNewLabourDialog = false
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
</style>
