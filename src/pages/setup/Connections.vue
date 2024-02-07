<template>
  <div class="height100 width100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('common.products.connectors') }}
        </div>
        <div class="heading-description">
          {{ $t('common._common.list_of_all_connectors') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          @click="newConnectionOpen"
          class="setup-el-btn"
          >{{ $t('common.products.new_connector') }}</el-button
        >
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th
                  class="setting-table-th setting-th-text"
                  style="min-width: 250px;"
                >
                  {{ $t('common._common.display_name') }}
                </th>
                <th
                  class="setting-table-th setting-th-text"
                  style="min-width: 200px;"
                >
                  {{ $t('common.products.connectors_name') }}
                </th>
                <th
                  class="setting-table-th setting-th-text"
                  style="min-width: 180px;"
                >
                  {{ $t('common.products.connector_status') }}
                </th>
                <th
                  class="setting-table-th setting-th-text"
                  style="min-width: 180px;"
                ></th>
                <th
                  class="setting-table-th setting-th-text"
                  style="min-width: 160px;"
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
            <tbody v-else-if="!connections || !connections.length">
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('common.products.no_connections_available') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(connection, index) in connections"
                :key="index"
              >
                <td>
                  <div
                    class="fc-avatar-element q-item-division relative-position cursor-pointer"
                  >
                    <div>
                      <div>
                        <span class="q-item-label">{{
                          connection.serviceName
                        }}</span>
                      </div>
                    </div>
                  </div>
                </td>
                <td>{{ connection.name }}</td>
                <td>
                  <el-tag
                    class="f12"
                    size="mini"
                    :type="tagState[connection.state]"
                    >{{ state[connection.state] }}</el-tag
                  >
                </td>
                <td>
                  <div
                    v-if="
                      connection.state === 2 && connection.authenticationURL
                    "
                  >
                    <el-button
                      type="primary"
                      class="uppercase connection-auth-btn"
                      @click="openAuthenticationPanel(connection)"
                      >{{ $t('common.header.authorize') }}</el-button
                    >
                  </div>
                  <!-- <div v-if="connection.state > 2">
                  <el-button type="default" class="uppercase reconnect-auth-btn" @click="openAuthenticationPanel(connection)">Reauthorize</el-button>
                </div> -->
                </td>
                <td>
                  <div
                    class="text-left actions"
                    style="margin-top:-3px;margin-right: 15px;text-align:center;"
                  >
                    <i
                      class="el-icon-edit pointer el-icon-refresh pR10"
                      data-arrow="true"
                      :title="$t('common._common.invalid_conection')"
                      v-tippy
                      @click="invalidConnection(connection)"
                    ></i>
                    <i
                      class="el-icon-connection pointer"
                      :class="
                        connection.clientId
                          ? $t('common.header.connected')
                          : $t('common.header.notconnected')
                      "
                      data-arrow="true"
                      :title="$t('common._common.credentials')"
                      v-tippy
                      @click="updateCredentials(connection)"
                    ></i
                    >&nbsp;&nbsp;
                    <i
                      class="el-icon-edit pointer edit-icon"
                      data-arrow="true"
                      :title="$t('common.header.edit_connector')"
                      v-tippy
                      @click="editconnection(connection)"
                    ></i>
                    &nbsp;&nbsp;
                    <i
                      class="el-icon-delete pointer"
                      data-arrow="true"
                      :title="$t('common.header.delete_connector')"
                      v-tippy
                      @click="deleteconnection(connection)"
                    ></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <new-connection
      v-if="showCreateNewDialog"
      :isNew="isNew"
      :connectionObj="connection"
      @saved="saved"
      @onClose="showCreateNewDialog = false"
      @reloadList="loadconnections"
    ></new-connection>
    <new-credentials
      v-if="showCredentials"
      :isNew="false"
      :visibility.sync="showCredentials"
      :connectionObj="connection"
      @saved="loadconnections"
    ></new-credentials>
  </div>
</template>
<script>
import NewConnection from 'pages/setup/new/NewConnection'
import NewCredentials from 'pages/setup/new/NewConnectionCredentials'
export default {
  title() {
    return 'connections'
  },
  components: {
    NewConnection,
    NewCredentials,
  },
  watch: {
    $route: function(from, to) {
      console.log('********** current router', this.$route)
    },
  },
  data() {
    return {
      showCreateNewDialog: false,
      showCredentials: false,
      loading: true,
      closed: false,
      isNew: true,
      authWindow: {},
      state: {
        1: 'CREATED',
        2: 'NOT CONNECTED',
        3: 'CONNECTED',
        4: 'CONNECTED',
        5: 'DISABLED',
      },
      tagState: {
        1: '',
        2: 'warning',
        3: 'success',
        4: 'success',
        5: 'danger',
      },
      connection: {
        name: '',
        id: null,
        authType: 1,
        serviceName: '',
        clientId: null,
        clientSecretId: null,
        scope: null,
        authorizeUrl: '',
        accessTokenUrl: '',
        refreshTokenUrl: '',
        revokeTokenUrl: '',
        accessToken: '',
        authCode: '',
        refreshToken: '',
        callBackURL: '',
      },
      connectionBasic: {
        name: '',
        ParamType: 1,
        connectionParams: [
          {
            key: '',
            value: '',
          },
        ],
      },
      emitForm: false,
      resetForm: false,
    }
  },
  mounted() {
    this.loadconnections()
  },
  methods: {
    openAuthenticationPanel(connection) {
      if (connection) {
        this.authWindow = window.open(
          connection.authenticationURL,
          'targetWindow',
          'toolbar=no,location=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=700,height=700,left=250'
        )
        this.closeEventCheck(this.authWindow, connection)
      }
    },
    newConnectionOpen() {
      this.showCreateNewDialog = true
      this.isNew = true
    },
    closeEventCheck(win, connection) {
      if (this.intervalObj) {
        clearInterval(this.intervalObj)
        this.intervalObj = null
      }

      let closed = false
      let self = this
      let interval = setInterval(function() {
        if (win.closed && !closed) {
          closed = true
          self.checktheConnection(connection)
          clearInterval(interval)
        }
      }, 250)
    },
    checktheConnection(connection) {
      let self = this
      self.$http
        .get('/v2/connection/getAllConnection')
        .then(function(response) {
          self.connections = response.data.result.connections
            ? response.data.result.connections
            : []
          if (connection.state >= 3) {
            self.$message.success(
              this.$t('common.wo_report.authenticated_successfully')
            )
            self.loading = true
            self.loading = false
          } else {
            self.$message.error(
              this.$t('common.wo_report.authenticated_failed')
            )
          }
        })
        .catch(function(error) {
          self.loading = false
          console.log(error)
        })
    },
    loadconnections() {
      let that = this
      that.loading = true
      that.$http
        .get('/v2/connection/getAllConnection')
        .then(function(response) {
          that.connections = response.data.result.connections
            ? response.data.result.connections
            : []
          that.loading = false
        })
        .catch(function(error) {
          that.loading = false
          console.log(error)
        })
    },
    updateCredentials(connection) {
      this.showCreateNewDialog = false
      this.connection = connection
      this.showCredentials = true
    },
    cancelForm() {
      this.resetForm = true
      this.showCreateNewDialog = false
      this.showCredentials = false
    },
    saveForm() {
      this.emitForm = true
    },
    saved(data, type) {
      if (type) {
        this.$nextTick(() => {
          if (data.authType === 1) {
            // this.updateCredentials(data)
          }
        })
      } else {
        this.loadconnections()
      }
    },
    editconnection(connection) {
      this.connection = connection
      console.log('Connections data', connection)
      this.showCreateNewDialog = true
      this.isNew = false
    },
    invalidConnection(connection) {
      this.$http
        .post('/v2/connection/invalidateConnection', {
          connectionContext: {
            id: connection.id,
          },
        })
        .then(response => {
          if (response.data) {
            this.$message.warning(
              this.$t('common._common.connection_invalidate')
            )
            this.loadconnections()
          }
        })
    },
    deleteconnection(connection) {
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_connection'),
          message: this.$t(
            'common._common.are_you_sure_want_to_delete_this_connection'
          ),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.$http
              .post('/v2/connection/delete', {
                connectionContext: {
                  id: connection.id,
                },
              })
              .then(response => {
                if (response.data) {
                  this.$message.success(
                    this.$t('common.products.connection_deleted_successfully')
                  )
                  this.loadconnections()
                }
              })
          }
        })
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
