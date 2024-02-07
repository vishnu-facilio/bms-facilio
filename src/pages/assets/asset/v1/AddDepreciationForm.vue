<template>
  <div>
    <el-dialog
      :visible="!canShowAddNewForm"
      :title="'Apply Depreciation Schedule'"
      class="fc-dialog-center-container"
      width="50%"
      height="50%"
      :append-to-body="true"
      :before-close="closeDialog"
    >
      <error-banner
        :error.sync="error"
        :errorMessage.sync="errorText"
      ></error-banner>
      <div class="height400 mT15">
        <div v-if="loading" style="margin-bottom: 22px;">
          <span class="lines loading-shimmer width50 height45"></span>
        </div>

        <el-form
          v-else
          ref="depreciationForm"
          :rules="rules"
          :model="selectedDepreciation"
        >
          <el-form-item prop="id">
            <div class="d-flex">
              <el-select
                v-model="selectedDepreciation.id"
                class="fc-input-full-border-select2 width50"
                filterable
                placeholder="Select Depreciation Schedule"
              >
                <el-option
                  v-for="depreciation in depreciationList"
                  :key="depreciation.id"
                  :label="depreciation.name"
                  :value="depreciation.id"
                ></el-option>
              </el-select>

              <div
                class="text-fc-green pointer mL20"
                @click="canShowAddNewForm = true"
              >
                <inline-svg
                  src="add-icon"
                  class="vertical-middle pT5"
                  iconClass="icon icon-md mR5"
                ></inline-svg>
                Add New
              </div>
            </div>
          </el-form-item>
        </el-form>

        <div class="fc-modal-sub-title pB10" style="color: #385571;">
          Preview
        </div>

        <depreciation-schedule
          :asset="asset"
          :selectedDepreciationId="selectedDepreciation.id"
          @appliedDepreciationId="setDepreciationId"
          @depreciationRel="setDepreciationRel"
          @error="errorBanner"
        ></depreciation-schedule>

        <div class="modal-dialog-footer">
          <el-button @click="closeDialog" class="modal-btn-cancel">
            Cancel
          </el-button>

          <el-button
            type="primary"
            class="modal-btn-save"
            :loading="applyingDepreciation"
            @click="submitForm()"
          >
            Confirm
          </el-button>
        </div>
      </div>
    </el-dialog>

    <new-asset-depreciation
      v-if="canShowAddNewForm"
      @onRecordSaved="addDepreciation"
      @onClose="canShowAddNewForm = false"
    ></new-asset-depreciation>
  </div>
</template>
<script>
import NewAssetDepreciation from 'pages/setup/depreciation/NewAssetDepreciation'
import { API } from '@facilio/api'
import DepreciationSchedule from 'pages/setup/depreciation/ScheduleList'
import ErrorBanner from '@/ErrorBanner'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['asset'],

  data() {
    return {
      selectedDepreciation: {
        id: null,
      },
      loading: false,
      appliedDepreciationId: null,
      depreciationList: [],
      canShowAddNewForm: false,
      applyingDepreciation: false,
      depreciationRelId: null,
      error: false,
      errorText: '',
      rules: {
        id: {
          required: true,
          message: 'Please select a Depreciation Schedule',
          trigger: 'change',
        },
      },
    }
  },

  components: { NewAssetDepreciation, DepreciationSchedule, ErrorBanner },

  created() {
    this.loadDepreciation()
  },

  watch: {
    'selectedDepreciation.id'(value) {
      if (value) {
        this.error = false
        this.errorText = ''
      }
    },
  },

  methods: {
    async loadDepreciation() {
      this.loading = true
      let { list, error } = await API.fetchAll('assetdepreciation')
      if (error) this.$message.error(error.message || 'Error Occured')
      else this.depreciationList = list
      this.loading = false
    },
    addDepreciation(depreciation) {
      this.depreciationList.push(depreciation)
      this.selectedDepreciation.id = depreciation.id
    },

    setDepreciationId(id) {
      this.appliedDepreciationId = id
      this.selectedDepreciation.id = id
    },
    setDepreciationRel(rel) {
      let { id } = rel || {}
      this.depreciationRelId = id
    },
    errorBanner(errorMsg) {
      this.error = true
      this.errorText = errorMsg || 'Error Occured'
    },

    submitForm() {
      this.$refs['depreciationForm'].validate(async valid => {
        if (!valid) return false
        let { asset, depreciationRelId } = this
        let param = {
          data: {
            asset: {
              id: asset.id,
            },
            depreciation: {
              id: this.selectedDepreciation.id,
            },
          },
        }
        let promise = {}
        if (isEmpty(depreciationRelId))
          promise = await API.createRecord('assetdepreciationRel', param)
        else
          promise = await API.updateRecord('assetdepreciationRel', {
            ...param,
            id: depreciationRelId,
          })
        let { error } = promise || {}
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success(
            this.$t('setup.users_management.Depreciation_scheduled')
          )
          this.$emit('fetchAssetDetail')
          this.applyingDepreciation = false
          this.closeDialog()
        }
      })
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style scoped>
.lines {
  height: 16px;
  border-radius: 5px;
}
.height45 {
  height: 45px !important;
}
</style>
