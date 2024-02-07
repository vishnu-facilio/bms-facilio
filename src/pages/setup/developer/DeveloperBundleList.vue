<template>
  <div class="height100 overflow-hidden fc-bundle-page">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('setup.new.developer_application') }}
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
          {{ $t('setup.add.add_bundle') }}</el-button
        >
      </div>
    </div>
    <!-- main -->
    <div class="container-scroll clearboth">
      <div class="width100 setting-Rlayout mT30">
        <div v-if="loading" class="flex-middle fc-empty-white">
          <spinner :show="loading" size="80"></spinner>
        </div>
        <el-row :gutter="20" v-else>
          <el-col span="8" v-for="bundle in bundleList" :key="bundle">
            <el-card
              class="bundle-box-card visibility-visible-actions pointer"
              @click="openSummary(bundle)"
            >
              <div class="fc-bundle-more-icon visibility-hide-actions">
                <!-- <el-dropdown>
                  <span class="el-dropdown-link">
                    <i class="el-icon-more"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item>Publish</el-dropdown-item>
                    <el-dropdown-item>Delete</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown> -->
              </div>
              <div
                class="flex-middle justify-content-center flex-direction-column"
              >
                <avatar size="lg" :user="$account.user"></avatar>
                <div
                  class="fc-black3-16 bold pT10 pointer"
                  @click="openSummary(bundle)"
                >
                  {{ bundle.bundleName }}
                </div>
                <div class="fc-black-12 pT10">
                  {{ bundle.bundleGlobalName }}
                </div>
                <el-button
                  @click="openSummary(bundle)"
                  class="mT20 fc-btn-green-medium-border"
                >
                  Edit Bundle
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
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
import Avatar from '@/Avatar'
import bundleForm from './DeveloperBundleform'
import { API } from '@facilio/api'
export default {
  data() {
    return {
      showAddBundle: false,
      isNew: false,
      selectedBundle: null,
      bundleList: [],
      loading: false,
    }
  },
  components: {
    Avatar,
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
      let { error, data } = await API.get('/v3/bundle/getAllBundles')
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.bundleList = data.bundleList || []
      }
      this.loading = false
    },
    openSummary(bundle) {
      this.$router.push({
        name: 'developerSummary',
        params: {
          id: bundle.id,
        },
      })
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
