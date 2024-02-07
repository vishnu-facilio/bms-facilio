<template>
  <div class="fc-bundle-summary-page">
    <div v-if="loading" class="flex-middle fc-empty-white">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else>
      <el-header height="80">
        <div>
          <div class="pointer pB10 fc-dark-blue-txt12 f14" @click="backToList">
            <i class="el-icon-back fc-dark-blue-txt12 fwBold"></i>
            Back
          </div>
          <div class="fc-black3-16 bold text-capitalize">
            {{ $t('commissioning.list.publish') }}
          </div>
        </div>
        <button
          type="button"
          class="setup-el-btn pL30 pR30"
          @click="bundleCreateVersion()"
        >
          {{ $t('setup.setupLabel.create_version') }}
        </button>
      </el-header>
      <div class="fc-bundle-summary">
        <el-table
          :data="bundleChangeSetList"
          style="width: 100%"
          class="fc-bundle-list-table"
          :empty-text="$t('setup.setup_empty_state.publish_empty')"
        >
          <template slot="empty">
            <img
              class="mT50"
              src="~statics/noData-light.png"
              width="100"
              height="100"
            />
            <div class="mT10 label-txt-black f14 op6">
              {{ $t('setup.setup_empty_state.publish_empty') }}
            </div>
          </template>

          <el-table-column label="name">
            <template v-slot="publish">
              {{ publish.row.componentDisplayName }}
            </template>
          </el-table-column>
          <el-table-column prop="enum" label="Enum">
            <template v-slot="publish">
              {{ publish.row.componentTypeEnum }}
            </template>
          </el-table-column>
          <el-table-column prop="Mode Enum" label="Enum">
            <template v-slot="publish">
              {{ publish.row.modeEnum }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <!-- create version dialog start-->
    <el-dialog
      title="Create Version"
      :visible.sync="dialogVisible"
      width="30%"
      class="fc-dialog-center-container"
      :before-close="handleClose"
      :append-to-body="true"
    >
      <div class="height180">
        <el-form ref="bundleForm" :model="bundle">
          <el-form-item :label="$t('setup.approvalprocess.name')" prop="name">
            <el-input
              class="width100 fc-input-full-border2"
              autofocus
              v-model="bundle.version"
              type="text"
              :placeholder="$t('setup.placeholder.enter_bundle_name')"
            />
          </el-form-item>
        </el-form>

        <div class="modal-dialog-footer">
          <el-button @click="closeDialog" class="modal-btn-cancel">
            {{ $t('setup.users_management.cancel') }}
          </el-button>
          <el-button
            type="primary"
            :loading="saving"
            class="modal-btn-save"
            @click="createVersion()"
          >
            {{ $t('panel.dashboard.confirm') }}
          </el-button>
        </div>
      </div>
    </el-dialog>
    <!-- create version dialog end-->
  </div>
</template>
<script>
import { API } from '@facilio/api'
export default {
  data() {
    return {
      bundle: [
        {
          version: '',
        },
      ],
      bundleChangeSetList: [],
      dialogVisible: false,
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
    this.bundlePublish()
  },
  methods: {
    async bundlePublish() {
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
        this.bundleChangeSetList = data.bundleChangeSetList || []
      }
      this.loading = false
    },
    bundleCreateVersion() {
      this.dialogVisible = true
    },
    closeDialog() {
      this.dialogVisible = false
    },
    createVersion() {
      this.$refs['bundleForm'].validate(async valid => {
        if (!valid) return false
        this.saving = true
        let url = 'v3/bundle/createVersion'
        let params = {
          bundle: {
            id: this.bundleId,
          },
          version: this.bundle.version,
        }
        let { error, data } = await API.post(url, params)

        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.$message.success(
            this.$t('common._common.trigger_saved_successfully')
          )
          this.$emit('onSave', data.bundle)
          this.closeDialog()
          this.$router.push({
            name: 'developerSummary',
            params: {
              id: this.bundleId,
            },
          })
        }
        this.saving = false
      })
    },
    backToList() {
      this.$router.go(-1)
    },
  },
}
</script>
<style lang="scss"></style>
