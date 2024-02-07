<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div id="newenpi">
      <el-form
        :model="connection"
        :label-position="'top'"
        ref="meterForm"
        class="fc-form"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{
                isNew
                  ? $t('common.products.new_connector')
                  : $t('common.header.edit_connector')
              }}
            </div>
          </div>
        </div>
        <div class="new-body-modal enpi-body-modal">
          <div class="body-scroll">
            <el-row :gutter="20" class="pB5">
              <el-col :span="12">
                <p class="fc-input-label-txt">
                  {{ $t('common.wo_report.authentication_types') }}
                </p>
                <el-select
                  v-model="connection.authType"
                  filterable
                  :placeholder="$t('common.products.select_auth_type')"
                  class="width100 fc-input-full-border2"
                  @change="resetForm(connection.authType)"
                >
                  <el-option label="OAuth2" :value="parseInt(1)"></el-option>
                  <el-option
                    :label="$t('common.header.basic_authentication')"
                    :value="parseInt(2)"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
            <el-row :gutter="20" v-if="connection.authType === 1">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common.header.grand_type') }}
                </p>
                <el-select
                  v-model="connection.grantType"
                  filterable
                  :placeholder="$t('common.products.select_auth_type')"
                  class="width100 fc-input-full-border2"
                >
                  <el-option
                    :label="$t('common.header.authorization_code')"
                    :value="1"
                  ></el-option>
                  <el-option
                    :label="$t('common.header.client_credentials')"
                    :value="2"
                  ></el-option>
                  <el-option
                    :label="$t('common.header.password_credentials')"
                    :value="3"
                  ></el-option>
                  <el-option
                    :label="$t('common.header.implicit')"
                    :value="4"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
            <el-row :gutter="20" v-if="connection.authType === 1">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common.products.connectors_name') }}
                </p>
                <el-form-item>
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="connection.name"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common._common.enter_connection_name')"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20" v-if="connection.authType === 1">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common._common.display_name') }}
                </p>
                <el-form-item>
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="connection.serviceName"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common._common.enter_service_name')"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row
              :gutter="20"
              v-if="connection.authType === 1 && connection.grantType === 1"
            >
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common.header.authorize_url') }}
                </p>
                <el-form-item>
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="connection.authorizeUrl"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common._common.enter_uri')"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20" v-if="connection.authType === 1">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common.products.access_token_url') }}
                </p>
                <el-form-item>
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="connection.accessTokenUrl"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common._common.enter_uri')"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20" v-if="connection.authType === 1">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common._common.refresh_token_url') }}
                </p>
                <el-form-item>
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="connection.refreshTokenUrl"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common._common.enter_uri')"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <template v-if="connection.authType === 1">
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
            </template>

            <el-row :gutter="20" v-if="connection.authType === 1">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common.products.scope') }}
                </p>
                <el-form-item>
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="connection.scope"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common._common.enter_scope')"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20" v-if="connection.authType === 1">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common.products.audience') }}
                </p>
                <el-form-item>
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="connection.audience"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common._common.enter_audience')"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20" v-if="connection.authType === 1">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common.header.resource_') }}
                </p>
                <el-form-item>
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="connection.resource"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common._common.enter_resource')"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <template
              v-if="connection.authType === 1 && connection.grantType === 3"
            >
              <el-row :gutter="20">
                <el-col :span="24">
                  <p class="fc-input-label-txt">
                    {{ $t('common.products.user_name') }}
                  </p>
                  <el-form-item>
                    <el-input
                      class="width100 fc-input-full-border2"
                      autofocus
                      v-model="connection.userName"
                      type="text"
                      autocomplete="off"
                      :placeholder="$t('common._common.enter_user_name')"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="24">
                  <p class="fc-input-label-txt">
                    {{ $t('common._common.password') }}
                  </p>
                  <el-form-item>
                    <el-input
                      show-password
                      class="width100 fc-input-full-border2"
                      autofocus
                      v-model="connection.password"
                      type="password"
                      :placeholder="$t('common._common.enter_password')"
                      autocomplete="new-password"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </template>
            <el-row :gutter="20" v-if="connection.authType === 2">
              <el-col :span="20">
                <p class="fc-input-label-txt">
                  {{ $t('common.products.name') }}
                </p>
                <el-form-item>
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="connectionBasic.name"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common._common.enter_name')"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20" v-if="connection.authType === 2">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common.products.service_name') }}
                </p>
                <el-form-item>
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="connectionBasic.serviceName"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common._common.enter_service_name')"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <div v-if="connection.authType === 2">
              <div v-if="isNew">
                <el-row :gutter="20">
                  <el-col :span="24">
                    <p class="fc-input-label-txt">
                      {{ $t('common.products.user_name') }}
                    </p>
                    <el-form-item>
                      <el-input
                        class="width100 fc-input-full-border2"
                        autofocus
                        v-model="connectionBasic.connectionParams.userName"
                        type="text"
                        autocomplete="off"
                        :placeholder="$t('common._common.enter_user_name')"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="20">
                  <el-col :span="24">
                    <p class="fc-input-label-txt">
                      {{ $t('common._common.password') }}
                    </p>
                    <el-form-item>
                      <el-input
                        class="width100 fc-input-full-border2"
                        autofocus
                        v-model="connectionBasic.connectionParams.password"
                        type="password"
                        autocomplete="new-password"
                        :placeholder="$t('common._common.enter_password')"
                        show-password
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
              </div>
              <div v-else>
                <div
                  v-for="(connection, key) in connectionBasic.connectionParams"
                  :key="key"
                >
                  <el-row :gutter="20" v-if="key === 0">
                    <el-col :span="24">
                      <p class="fc-input-label-txt">
                        {{ $t('common.products.user_name') }}
                      </p>
                      <el-form-item>
                        <el-input
                          class="width100 fc-input-full-border2"
                          autofocus
                          v-model="connection.value"
                          type="text"
                          autocomplete="off"
                          :placeholder="$t('common._common.enter_user_name')"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row :gutter="20" v-if="key === 1">
                    <el-col :span="24">
                      <p class="fc-input-label-txt">
                        {{ $t('common._common.password') }}
                      </p>
                      <el-form-item>
                        <el-input
                          class="width100 fc-input-full-border2"
                          autofocus
                          v-model="connection.value"
                          type="password"
                          autocomplete="new-password"
                          :placeholder="$t('common._common.enter_password')"
                          show-password
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog" class="modal-btn-cancel">
            {{ $t('common._common.cancel') }}</el-button
          >
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="submitForm()"
            :loading="saving"
            v-if="connection.authType === 1"
            >{{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}
          </el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="submitBasicForm"
            :loading="saving"
            v-else
            >{{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}
          </el-button>
        </div>
      </el-form>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
export default {
  props: ['isNew', 'connectionObj', 'kpiCategoryId'],
  data() {
    return {
      saving: false,
      connection: {
        id: null,
        name: '',
        authType: 1,
        grantType: 1,
        serviceName: '',
        clientId: null,
        clientSecretId: null,
        scope: null,
        audience: null,
        resource: null,
        authorizeUrl: '',
        accessTokenUrl: '',
        refreshTokenUrl: '',
        revokeTokenUrl: '',
        accessToken: '',
        authCode: '',
        refreshToken: '',
        userName: '',
        password: '',
      },
      connectionBasic: {
        name: '',
        serviceName: '',
        ParamType: 1,
        connectionParams: [
          {
            key: 'userName',
            value: null,
          },
          {
            key: 'password',
            value: null,
          },
        ],
      },
    }
  },
  mounted() {
    if (this.isNew) {
      this.resetForm()
    } else {
      this.connectionBasic = {
        ...this.connectionObj,
      }
      this.connection = this.connectionObj
      this.connectionBasic = this.connectionObj
    }
  },
  methods: {
    getConnectionParams() {
      // if (this.isNew) {
      //   return {
      //     authType: this.connection.authType,
      //     name: this.connection.name,
      //     serviceName: this.connection.serviceName,
      //     scope: this.connection.scope,
      //     authorizeUrl: this.connection.authorizeUrl,
      //     accessTokenUrl: this.connection.accessTokenUrl,
      //     refreshTokenUrl: this.connection.refreshTokenUrl,
      //   }
      // } else {
      //   return this.connection
      return this.connection
    },
    getConnectionBasicParams() {
      return {
        authType: this.connection.authType,
        name: this.connectionBasic.name,
        id: this.connection.id,
        ParamType: this.connectionBasic.ParamType,
        serviceName: this.connectionBasic.serviceName,
        connectionParams: [
          {
            key: 'userName',
            value:
              this.connectionBasic.connectionParams &&
              this.connectionBasic.connectionParams.userName,
          },
          {
            key: 'password',
            value:
              this.connectionBasic.connectionParams &&
              this.connectionBasic.connectionParams.password,
          },
        ],
      }
    },
    editGetConnectionBasicParams() {
      return {
        authType: this.connection.authType,
        name: this.connectionBasic.name,
        id: this.connection.id,
        ParamType: this.connectionBasic.ParamType,
        serviceName: this.connectionBasic.serviceName,
        connectionParams: [
          {
            key: 'userName',
            value:
              this.connectionBasic.connectionParams[0] &&
              this.connectionBasic.connectionParams[0].value,
          },
          {
            key: 'password',
            value:
              this.connectionBasic.connectionParams[1] &&
              this.connectionBasic.connectionParams[1].value,
          },
        ],
      }
    },
    toObject(array) {
      let obj = {}
      array.forEach(rt => {
        obj[rt.key] = rt.value
      })
      return obj
    },

    removeParam(index) {
      this.connectionBasic.connectionParams.splice(index, 1)
    },
    async submitBasicForm() {
      let self = this
      self.saving = true
      let param = null
      if (this.isNew) {
        param = {
          connectionContext: this.getConnectionBasicParams(),
        }
      } else {
        param = {
          connectionContext: this.editGetConnectionBasicParams(),
        }
      }

      let url = this.isNew ? '/v2/connection/add' : '/v2/connection/update'
      let { error, data } = await API.post(url, param)

      if (data) {
        if (self.isNew) {
          self.$message.success(
            this.$t('common._common.connection_created_successfully')
          )
        } else {
          self.$message.success(
            this.$t('common._common.connection_updated_successfully')
          )
        }
      } else if (error) {
        self.$message.error(error.message)
      }
      self.saving = false
      this.$emit('reloadList')
      this.closeDialog()
    },
    async agentVersion() {
      let { error, data } = await API.get(
        `v2/agent/versions?latestVersion=true`
      )
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.versionData = data || []
      }
    },
    async submitForm() {
      let self = this
      self.saving = true
      let param = {
        connectionContext: this.getConnectionParams(),
      }
      let url = this.isNew ? '/v2/connection/add' : '/v2/connection/update'

      let { data } = await API.post(url, param)

      if (data) {
        if (this.isNew) {
          this.$message.success(
            this.$t('common._common.connection_created_successfully')
          )
        } else {
          this.$message.success(
            this.$t('common._common.connection_updated_successfully')
          )
        }
        this.toggleCredentials(data)
      } else {
        this.$message.error(
          this.$t('common.wo_report.unable_to_create_connection')
        )
      }

      this.saving = false
      this.closeDialog()
    },
    resetForm(authType) {
      this.connection = {
        name: '',
        authType: authType ? authType : 1,
        grantType: 1,
        serviceName: '',
        clientId: null,
        clientSecretId: null,
        scope: null,
        audience: null,
        resource: null,
        authorizeUrl: '',
        accessTokenUrl: '',
        refreshTokenUrl: '',
        revokeTokenUrl: '',
        accessToken: '',
        authCode: '',
        refreshToken: '',
        userName: '',
        password: '',
      }
      this.connectionBasic = {
        name: '',
        ParamType: 1,
        serviceName: '',
        connectionParams: [],
      }
    },
    // cancel() {
    //   this.resetForm()
    //   this.$emit('canceled')
    //   this.closeDialog()
    // },
    save() {
      let self = this
      self.saving = true
    },
    toggleCredentials(data) {
      let self = this
      if (data.connection && self.isNew) {
        self.$emit('saved', data.connection, true)
      } else {
        self.$emit('saved')
      }
    },
    closeDialog() {
      this.$emit('onClose')
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
</style>
