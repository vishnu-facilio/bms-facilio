<template>
  <div class="height100 width100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('common.products.connectedapps') }}
        </div>
        <div class="heading-description">
          {{ $t('common._common.list_of_all_connected_apps') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          @click="newConnectedApp"
          class="setup-el-btn"
          >{{ $t('common.products.new_connected_app') }}</el-button
        >
      </div>
    </div>
    <div class="container-scroll border-top-grey">
      <ConnectedAppsListTemplate
        :isLoading.sync="loading"
        :connectedAppsList="connectedAppsList"
        @redirect="redirectToConnectedApp"
        @delete="deleteConnectedApp"
      >
      </ConnectedAppsListTemplate>
    </div>
    <div v-if="showCreateNewDialog">
      <el-dialog
        :visible.sync="showCreateNewDialog"
        :fullscreen="true"
        :append-to-body="true"
        custom-class="fc-dialog-form fc-dialog-right setup-dialog100 setup-dialog assetaddvaluedialog fc-web-form-dialog fc-item-type-summary-dialog"
        style="z-index: 999999;"
      >
        <ConnectedAppForm
          :isNew="isNew"
          :connectedAppData="connectedAppObj"
          @onClose="closeForm"
          @onSave="loadConnectedApps"
          class="facilio-inventory-web-form-body"
        >
        </ConnectedAppForm>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import ConnectedAppsListTemplate from 'pages/connectedapps/ConnectedAppsListTemplate'
import ConnectedAppForm from 'pages/setup/connectedapps/ConnectedAppForm'
export default {
  title() {
    return 'Connected Apps'
  },
  components: {
    ConnectedAppForm,
    ConnectedAppsListTemplate,
  },
  data() {
    return {
      showCreateNewDialog: false,
      loading: true,
      connectedAppsList: [],
      connectedAppObj: null,
      isNew: false,
      showSAMLConfig: false,
      idpLoginURL: null,
      idpLogoutURL: null,
      idpCertificateURL: null,
      configureSAMLForm: {
        id: null,
        spEntityId: null,
        spAcsUrl: null,
        subjectType: null,
        nameIdFormat: null,
      },
      samlConfigSaving: false,
    }
  },
  mounted() {
    this.loadConnectedApps()
  },
  methods: {
    loadConnectedApps() {
      this.loading = true
      this.$http
        .get('/v2/connectedApps/all')
        .then(response => {
          this.connectedAppsList = response.data.result.connectedApps
            ? response.data.result.connectedApps
            : []
          this.loading = false
        })
        .catch(function(error) {
          this.loading = false
          console.log(error)
        })
    },
    redirectToConnectedApp(connectedApp) {
      let { id: connectedAppId } = connectedApp
      this.$router.push({
        name: 'connectedapp-layout',
        params: {
          connectedAppId,
        },
      })
    },
    closeForm() {
      this.showCreateNewDialog = false
    },
    saveForm() {
      this.emitForm = true
    },
    newConnectedApp() {
      this.showCreateNewDialog = true
      this.isNew = true
    },
    deleteConnectedApp(connectedApp) {
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_connected_app'),
          message: this.$t('common._common.are_you_want_delete_connected_app'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.$http
              .post('/v2/connectedApps/delete', {
                connectedAppId: connectedApp.id,
              })
              .then(response => {
                if (response.data) {
                  this.$message.success(
                    this.$t(
                      'common.products.connected_app_deleted_successfully'
                    )
                  )
                  this.loadConnectedApps()
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
.border-top-grey {
  border-top: 1px solid #ebecec;
}
</style>
<style>
.fc-create-record {
  width: 45% !important;
}
</style>
