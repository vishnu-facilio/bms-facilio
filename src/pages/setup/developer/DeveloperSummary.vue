<template>
  <div class="fc-bundle-summary-page">
    <div v-if="loading" class="flex-middle fc-empty-white">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else>
      <el-header height="80">
        <div>
          <div class="pointer pB10" @click="backToList">
            <i class="el-icon-back fc-dark-blue-txt12 f14 bold">Back</i>
          </div>
          <div class="fc-black3-16 bold" v-if="bundleVersionList.length > 0">
            {{ bundleVersionList[0].bundleName }}
          </div>
        </div>
        <div>
          <button
            type="button"
            class="setup-el-btn pL30 pR30"
            @click="bundlePublish()"
          >
            {{ $t('commissioning.list.publish') }}
          </button>
        </div>
      </el-header>
      <div class="fc-bundle-summary">
        <el-table
          :data="bundleVersionList"
          style="width: 100%"
          class="fc-bundle-list-table"
          :empty-text="$t('setup.setup_empty_state.bundle_empty_summary')"
        >
          <template slot="empty">
            <img
              class="mT50"
              src="~statics/noData-light.png"
              width="100"
              height="100"
            />
            <div class="mT10 label-txt-black f14 op6">
              {{ $t('setup.setup_empty_state.bundle_empty_summary') }}
            </div>
          </template>
          <el-table-column prop="version" label="version">
            <template v-slot="bundle">
              {{ bundle.row.version ? bundle.row.version : '---' }}
            </template>
          </el-table-column>
          <el-table-column prop="time" label="Created Time">
            <template v-slot="bundle">
              {{ bundle.row.createdTime | formatDate() }}
            </template>
          </el-table-column>
          <el-table-column prop="url" label="url">
            <template v-slot="bundle">
              <a
                :href="downloadBundle"
                @click="downloadBundleVersion(bundle.row)"
                target="_blank"
                >Download</a
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
export default {
  data() {
    return {
      bundleChangeSet: [],
      bundleVersionList: [],
      bundleChangeSetList: [],
      downloadBundle: null,
      loading: false,
    }
  },
  computed: {
    bundleId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
  },
  created() {
    this.loadBundleVersion()
    this.loadBundleGetChangeSet()
  },
  methods: {
    async loadBundleVersion() {
      this.loading = true
      let params = {
        bundle: {
          id: this.bundleId,
        },
      }
      let { error, data } = await API.post('v3/bundle/getAllVersions', params)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.bundleVersionList = data.bundleVersionList || []
      }
      this.loading = false
    },
    async loadBundleGetChangeSet() {
      this.loading = true
      let params = {
        bundle: {
          id: this.bundleId,
        },
      }
      let { error, data } = await API.post('v3/bundle/getChangeSet', params)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.bundleChangeSet = data.bundleVersion || []
      }
      this.loading = false
    },
    bundlePublish() {
      this.$router.push({
        name: 'developerPublish',
        params: {
          id: this.bundleId,
        },
      })
    },
    downloadBundleVersion(bundle) {
      this.downloadBundle = bundle.downloadUrl
    },
    backToList() {
      this.$router.go(-1)
    },
  },
}
</script>
