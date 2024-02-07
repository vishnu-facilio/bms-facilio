<template>
  <div>
    <SetupLoader v-if="loading">
      <template #setupLoading>
        <spinner :show="loading" size="80"></spinner>
      </template>
    </SetupLoader>
    <div v-else>
      <div class="flex-space-between">
        <div class="fc-black-15 fwBold pL20 pT20">API Clients</div>
        <el-button type="primary" class="setup-el-btn" @click="addApiClient()">
          {{ $t('common._common.add_api_client') }}
        </el-button>
      </div>
      <div class="fc-setup-actions-con pT0 pL0"></div>
      <el-table
        :data="apiClientList"
        style="width: 100%"
        height="calc(100vh - 320px)"
        class="fc-setup-table fc-setup-table-p0 fc-setup-table-th-borderTop"
        :fit="true"
      >
        <template slot="empty">
          <div class="height57vh fc-align-center-column">
            <inline-svg
              src="svgs/list-empty"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="label-txt-black f18 bold">
              {{ $t('setup.empty.empty_api_clients') }}
            </div>
          </div>
        </template>
        <el-table-column prop="name" label="Name">
          <template v-slot="apiClient">
            {{ apiClient.row.name }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="Type">
          <template v-slot="apiClient">
            {{ apiClient.row.authType === 2 ? 'API Key' : 'Oauth2' }}
          </template>
        </el-table-column>
        <el-table-column prop="oauth2ClientId" label="Client ID">
          <template v-slot="apiClient">
            {{ apiClient.row.oauth2ClientId || '---' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="Created Time">
          <template v-slot="apiClient">
            {{ apiClient.row.createdTime | formatDate(true) }}
          </template>
        </el-table-column>
        <el-table-column label="">
          <template v-slot="apiClient">
            <i
              class="visibility-hide-actions el-icon-delete pL10"
              @click="removeClient(apiClient.row.id)"
            ></i>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <new-api-client
      v-if="showApiClientForm"
      :isNew="isNew"
      @onClose="onClose"
      @onSave="listApiClients"
    ></new-api-client>
  </div>
</template>
<script>
import SetupLoader from 'pages/setup/components/SetupLoader'
import NewApiClient from './NewAPIClient'
import { API } from '@facilio/api'
export default {
  data() {
    return {
      loading: false,
      apiClientList: [],
      isNew: false,
      showApiClientForm: false,
    }
  },
  components: {
    NewApiClient,
  },
  async created() {
    await this.listApiClients()
  },
  methods: {
    onSave() {
      this.showApiClientForm = false
      this.listApiClients()
    },
    addApiClient() {
      ;(this.isNew = true), (this.showApiClientForm = true)
    },
    onClose() {
      this.showApiClientForm = false
    },
    async listApiClients() {
      this.loading = true
      let { error, data } = await API.get(`/v2/dev/listApiClients`)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.apiClientList = data.apiClients
      }

      this.loading = false
    },
    async removeClient(id) {
      this.loading = true
      let { error } = await API.post(`/v2/dev/deleteClient`, { id })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
        this.loading = false
      } else {
        this.$message.success(
          this.$t('common._common.api_deleted_successfully')
        )
        this.listApiClients()
      }
    },
  },
}
</script>
<style scoped>
.flex-space-between {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}
</style>
