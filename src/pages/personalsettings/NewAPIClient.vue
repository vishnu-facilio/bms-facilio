<template>
  <div>
    <el-dialog
      :title="$t('common._common.add_api_client')"
      :visible.sync="canShowAPIClientType"
      width="30%"
      class="fc-dialog-center-container"
      :append-to-body="true"
      :before-close="closeDialog"
    >
      <div class="height300">
        <el-form>
          <el-form-item :required="true" :label="$t('common.products.name')">
            <el-input
              class="width100 fc-input-full-border2"
              autofocus
              v-model="apiClient.name"
              type="text"
              :placeholder="$t('common._common.enter_name')"
            />
          </el-form-item>
          <el-form-item
            :label="$t('common._common.apiclienttype')"
            :required="true"
          >
            <el-select
              v-model="apiType"
              :placeholder="$t('common.products.select_tabtype')"
              filterable
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="type in apiClientTypes"
                :key="type.value"
                :label="type.name"
                :value="type.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>

        <div class="modal-dialog-footer">
          <el-button @click="closeDialog" class="modal-btn-cancel">
            {{ $t('setup.users_management.cancel') }}
          </el-button>
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="submitForm()"
            :loading="saving"
          >
            {{ $t('panel.dashboard.confirm') }}
          </el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog
      :title="$t('common._common.api_secret')"
      :visible.sync="canShowSecret"
      width="30%"
      class="fc-dialog-center-container"
      :append-to-body="true"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :before-close="closeSecret"
    >
      <div class="max-height300 pB40">
        <div>
          <div>
            <div v-if="apiType === 'oauth2'">
              <div class="fc-black2-14 text-left bold">
                <i class="el-icon-lock pR10 fc-secret-lock"></i>
                Oauth Client ID:
              </div>
              <div
                class="flex-middle fc-code-block-oauthKey visibility-visible-actions mB20"
              >
                {{ clientId }}
                <div
                  @click="copyLinkName(clientId)"
                  class="pointer pL5 visibility-hide-actions"
                >
                  <inline-svg
                    src="svgs/link-copy"
                    iconClass="icon icon-sm-md vertical-bottom op5"
                  ></inline-svg>
                </div>
              </div>
            </div>
          </div>
          <div>
            <div class="fc-black2-14 text-left bold">
              <i class="el-icon-key pR10 fc-secret-key"></i>
              Secret Key:
            </div>
            <div
              class="flex-middle fc-code-block-apiKey visibility-visible-actions"
            >
              <div class="fc-black2-14 text-left">
                {{ token }}
              </div>
              <div
                @click="copyLinkName(token)"
                class="pointer pL5 visibility-hide-actions"
              >
                <inline-svg
                  src="svgs/link-copy"
                  iconClass="icon icon-sm-md vertical-bottom op5"
                ></inline-svg>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
export default {
  props: ['isNew'],
  data() {
    return {
      clientId: null,
      token: null,
      apiType: 'api-key',
      canShowSecret: false,
      canShowAPIClientType: false,
      canShowAPIKeyClientForm: false,
      canShowOauth2ClientForm: false,
      saving: false,
      apiClient: {
        name: null,
      },
      apiClientTypes: [
        {
          name: 'API Key',
          value: 'api-key',
        },
        {
          name: 'Oauth2',
          value: 'oauth2',
        },
      ],
    }
  },
  created() {
    this.canShowAPIClientType = this.isNew
  },
  methods: {
    openApiClientForm() {
      this.canShowAPIClientType = false
      if (this.apiType === 'api-key') {
        this.canShowAPIKeyClientForm = true
      } else {
        this.canShowOauth2ClientForm = true
      }
    },
    closeDialog() {
      this.$emit('onClose')
    },
    async submitForm() {
      this.saving = true
      if (this.apiType === 'api-key') {
        this.apiClient.authType = 2
      } else {
        this.apiClient.authType = 1
      }
      let { error, data } = await API.post('/v2/dev/createAPIClient', {
        apiClient: this.apiClient,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.clientId = data.clientId
        this.token = data.token
        this.canShowAPIKeyClientForm = false
        this.canShowOauth2ClientForm = false
        this.canShowAPIClientType = false
        this.$message.success(this.$t('common._common.api_client_added'))
        this.canShowSecret = true
      }
      this.saving = false
    },
    closeSecret() {
      this.$emit('onSave')
      this.closeDialog()
    },
    async copyLinkName(copy) {
      await navigator.clipboard.writeText(copy)
      this.$message({
        message: 'Copied !',
        type: 'success',
      })
    },
  },
}
</script>
<style lang="scss">
.fc-secret-key {
  transform: rotate(45deg);
  font-size: 18px;
  vertical-align: inherit;
}
.fc-secret-lock {
  font-size: 16px;
}
</style>
