<template>
  <el-dialog
    :title="
      isNew
        ? $t('common.wo_report.new_asset_department')
        : $t('common.wo_report.edit_asset_department')
    "
    :visible="true"
    width="30%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="height200">
      <el-form ref="newAssetDepartment" :rules="rules" :model="departmentData">
        <el-form-item label="Name" prop="name" :required="true">
          <el-input
            class="width100 fc-input-full-border2"
            autofocus
            v-model="departmentData.name"
            type="text"
            :placeholder="$t('common._common.enter_department')"
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
          @click="saveDepartment()"
        >
          {{ $t('panel.dashboard.confirm') }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { AssetDepartment } from './AssetDepartmentModel'
import { isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'

export default {
  props: ['department'],

  data() {
    return {
      saving: false,
      departmentData: {},
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          department: 'change',
        },
      },
    }
  },

  created() {
    this.departmentData = this.isNew
      ? new AssetDepartment()
      : cloneDeep(this.department)
  },
  computed: {
    isNew() {
      return isEmpty(this.department)
    },
  },

  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    saveDepartment() {
      this.$refs['newAssetDepartment'].validate(async valid => {
        if (!valid) return false

        this.saving = true
        try {
          if (this.isNew) {
            await this.departmentData.save()
            this.$message.success(
              this.$t('common._common.asset_department_created_success')
            )
          } else {
            let { name } = this.departmentData || {}

            await this.departmentData.patch({ name })
            this.$message.success(
              this.$t('common._common.asset_department_updated_success')
            )
          }

          this.$emit('onSave')
          this.closeDialog()
        } catch (errorMsg) {
          this.$message.error(errorMsg)
        }
        this.saving = false
      })
    },
  },
}
</script>
