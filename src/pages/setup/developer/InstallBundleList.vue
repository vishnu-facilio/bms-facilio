<template>
  <div class="height100 overflow-hidden fc-bundle-page">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('setup.new.install_bundle') }}
        </div>
        <div class="heading-description">
          {{ $t('setup.list.list_developer') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="openBundleCreation()"
        >
          {{ $t('setup.add.add_install_bundle') }}</el-button
        >
      </div>
    </div>
    <!-- main -->
    <div class="container-scroll clearboth">
      <div class="width100 setting-Rlayout mT30">
        <div>
          <table class="setting-list-view-table overflow-scroll">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">
                  {{ $t('setup.approvalprocess.name') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('setup.users_management.created_time') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('setup.setupLabel.version') }}
                </th>
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="!bundleInstallList.length">
              <tr>
                <td>{{ $t('setup.empty.bundle_empty') }}</td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                v-for="bundle in bundleInstallList"
                :key="bundle"
                class="tablerow"
              >
                <td>
                  {{ bundle.bundleGlobalName }}
                </td>
                <td>
                  {{ bundle.installedTime | formatDate() }}
                </td>
                <td>
                  {{ bundle.installedVersion }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <bundleForm
      v-if="showAddBundle"
      :bundleData="selectedBundle"
      :isNew="isNew"
      @onSave="loadBundle"
      @onClose="showAddBundle = false"
    >
    </bundleForm>
  </div>
</template>
<script>
import bundleForm from 'pages/setup/developer/InstallBundleForm.vue'
import { API } from '@facilio/api'
export default {
  data() {
    return {
      showAddBundle: false,
      isNew: false,
      selectedBundle: null,
      bundleInstallList: [],
      loading: false,
      bundle: null,
    }
  },
  components: {
    bundleForm,
  },
  mounted() {
    this.loadBundle()
  },
  methods: {
    async openBundleCreation() {
      this.showAddBundle = true
      this.isNew = true
      this.selectedBundle = null
      this.loadBundle()
    },
    async loadBundle() {
      this.loading = true
      let { error, data } = await API.get('v3/bundle/getAllInstalledBundles')
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.bundleInstallList = data.installedBundles || []
      }
      this.loading = false
    },
  },
}
</script>
<style lang="scss">
.fc-bundle-page {
  .bundle-box-card {
    position: relative;
    border-radius: 5px;
    border: none;
    -webkit-box-shadow: 0 1.5px 3.5px 0 rgb(226 229 233 / 50%);
    box-shadow: 0 1.5px 3.5px 0 rgb(226 229 233 / 50%);
    height: 200px;
    margin-bottom: 20px;
    &:hover {
      cursor: pointer;
      box-shadow: 0 2px 20px -3px rgb(0 0 0 / 9%);
    }
    .fc-bundle-more-icon {
      position: absolute;
      right: 20px;
      i {
        font-size: 16px;
        color: #5a5e66;
        transform: rotate(90deg);
      }
    }
  }
}
</style>
