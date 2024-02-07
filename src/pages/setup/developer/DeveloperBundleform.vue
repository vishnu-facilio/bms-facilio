<template>
  <el-dialog
    :title="
      isNew ? $t('setup.create.create_bundle') : $t('setup.create.edit_bundle')
    "
    :visible="true"
    width="30%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="height350">
      <el-form ref="bundleForm" :model="bundle">
        <el-form-item :label="$t('setup.approvalprocess.name')" prop="name">
          <el-input
            class="width100 fc-input-full-border2"
            autofocus
            v-model="bundle.bundleName"
            type="text"
            :placeholder="$t('setup.placeholder.enter_bundle_name')"
          />
        </el-form-item>
        <el-form-item label="Bundle Global name" prop="name">
          <el-input
            class="width100 fc-input-full-border2"
            autofocus
            v-model="bundle.bundleGlobalName"
            type="text"
            :placeholder="$t('setup.placeholder.enter_bundle_name')"
          />
        </el-form-item>
        <el-form-item prop="managed" label="Type">
          <el-select
            v-model="bundle.type"
            placeholder="Select"
            class="width100 fc-input-full-border2 pB20"
          >
            <el-option
              v-for="item in bundleType"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
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
          @click="saveBundle()"
        >
          {{ $t('panel.dashboard.confirm') }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
export default {
  props: ['isNew', 'bundleData'],
  data() {
    return {
      bundle: {
        bundleName: '',
        bundleGlobalName: '',
        type: null,
      },
      saving: false,
      bundleType: [
        {
          value: 2,
          label: 'Un-Managed',
        },
        {
          value: 3,
          label: 'Managed',
        },
      ],
    }
  },
  created() {
    if (!this.isNew) {
      this.bundle = this.bundleData
    }
  },
  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    saveBundle() {
      this.$refs['bundleForm'].validate(async valid => {
        if (!valid) return false
        this.saving = true
        let url = 'v3/bundle/addBundle'
        let params = {
          bundle: {
            bundleName: this.bundle.bundleName,
            bundleGlobalName: this.bundle.bundleGlobalName,
            type: this.bundle.type,
          },
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
          this.$emit('onSave', data.bundleContext)
          this.closeDialog()
        }
        this.saving = false
      })
    },
  },
}
</script>
