<template>
  <el-dialog
    :visible.sync="visibility"
    custom-class="setup-dialog-2"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div id="newenpi" style="height:400px;">
      <el-form
        :model="connection"
        :label-position="'top'"
        ref="meterForm"
        class="fc-form"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ $t('common.header.client_credentials') }}
            </div>
          </div>
        </div>
        <div class="new-body-modal enpi-body-modal">
          <div class="body-scroll">
            <el-row :gutter="20">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common.header.client_id') }}
                </p>
                <el-form-item>
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="connection.clientId"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common.header.client_id')"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common.header.client_secret') }}
                </p>
                <el-form-item>
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="connection.clientSecretId"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common.header.client_secret')"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common.header.redirect_url') }}
                </p>
                <el-form-item>
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="connection.callBackURL"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common.header.redirect_url')"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">
            {{ $t('common._common.cancel') }}</el-button
          >
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="submitForm"
            :loading="saving"
            >{{
              saving
                ? $t('common._common.updating')
                : $t('common._common.update')
            }}
          </el-button>
        </div>
      </el-form>
    </div>
  </el-dialog>
</template>
<script>
export default {
  props: ['isNew', 'connectionObj', 'visibility', 'kpiCategoryId'],
  data() {
    return {
      saving: false,
      connection: {
        id: null,
        name: '',
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
    }
  },
  mounted() {
    if (this.isNew) {
      this.resetForm()
    } else {
      this.connection = this.$helpers.cloneObject(this.connectionObj)
    }
  },
  methods: {
    getConnectionParams() {
      if (this.isNew) {
        return {
          authType: this.connection.authType,
          name: this.connection.name,
          serviceName: this.connection.serviceName,
          scope: this.connection.scope,
          authorizeUrl: this.connection.authorizeUrl,
          accessTokenUrl: this.connection.accessTokenUrl,
          refreshTokenUrl: this.connection.refreshTokenUrl,
        }
      } else {
        return {
          id: this.connection.id,
          clientId: this.connection.clientId,
          clientSecretId: this.connection.clientSecretId,
        }
      }
    },
    submitForm() {
      let self = this
      this.showCreateNewDialog = false
      let param = {
        connectionContext: this.getConnectionParams(),
      }
      let url = '/v2/connection/update'
      self.$http
        .post(url, param)
        .then(response => {
          if (response.data.responseCode === 0) {
            if (self.isNew) {
              self.$message.success(
                this.$t('common._common.connection_created_successfully')
              )
            } else {
              self.$message.success(
                this.$t('common._common.connection_updated_successfully')
              )
            }
          } else {
            self.$message.error(response.data.message)
          }
          self.resetForm()
          self.$emit('saved')
          self.cancel()
        })
        .catch(error => {
          console.log(error)
          self.$message.error(
            this.$t('common.wo_report.unable_to_create_connection')
          )
        })
    },
    resetForm() {
      this.connection = {
        name: '',
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
      }
      this.connectionBasic = {
        name: '',
        ParamType: 1,
        connectionParams: {},
      }
    },
    cancel() {
      this.resetForm()
      this.$emit('canceled')
      this.closeDialog()
    },
    save() {
      let self = this
      self.saving = true
    },
    closeDialog() {
      this.$emit('update:visibility', false)
      this.$emit('closed')
    },
  },
}
</script>
<style>
#newenpi .el-input.is-disabled .el-input__inner {
  background-color: white !important;
}
#newenpi .textcolor {
  color: #6b7e91;
}
#newenpi .ruletitle {
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.8px;
  color: #ef4f8f;
}
#newenpi .header.text {
  font-size: 18px;
  text-align: left;
  letter-spacing: 0.6px;
}

#newenpi .header .el-textarea__inner {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #666666;
}

#newenpi .header.el-input .el-input__inner,
#newenpi .header.el-textarea .el-textarea__inner {
  border-bottom: none;
  resize: none;
}
#newenpi .primarybutton.el-button {
  background-color: #39b2c2 !important;
  border-color: #39b2c2 !important;
  color: #ffffff !important;
  float: right;
}
#newenpi .fc-form .form-header,
#newenpi .fc-form-container .form-header {
  font-weight: normal;
  font-size: 16px;
  text-align: left;
}
#newenpi .fc-form .form-input {
  padding: 0px;
}
#newenpi .column-item {
  padding: 10px;
  border: 1px solid #f2f2f2;
  cursor: move;
  margin-top: 5px;
}
#newenpi.el-button:focus,
#newenpi .el-button:hover {
  background-color: #ecf5ff;
}
.fc-create-record {
  width: 50% !important;
}
.body-scroll {
  width: 100%;
  overflow-y: scroll;
  display: inline-block;
  /* height: 100vh; */
  padding-bottom: 30px;
}
.kpi-body-modal {
  height: calc(100vh - 100px) !important;
}
.kpi-icon-search {
  line-height: 0px !important;
  font-size: 16px !important;
  cursor: pointer;
  position: absolute;
  top: 0px;
  right: 11%;
}
.setup-dialog-2 .el-dialog__body,
.setup-dialog-2 .el-dialog__header {
  padding: 0px;
}
</style>
