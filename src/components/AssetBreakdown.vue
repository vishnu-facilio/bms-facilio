<template>
  <el-dialog
    :visible.sync="visibility"
    :append-to-body="true"
    custom-class="setup-dialog40 setup-dialog asset-breakdown-popup"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <error-banner
      :error.sync="error"
      :errorMessage.sync="errorText"
    ></error-banner>
    <div class="content-section">
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">{{ 'Report Asset Downtime' }}</div>
        </div>
      </div>
      <div class="new-body-modal">
        <el-row :gutter="20">
          <el-col :span="24">
            <p class="fc-input-label-txt pB10">
              {{ $t('common._common.message') }}
            </p>
            <el-input
              v-model="breakdown.condition"
              placeholder="Enter the Condition"
              class="fc-input-full-border-select2"
            ></el-input>
          </el-col>
        </el-row>
        <el-row align="middle" class="mT20">
          <el-col :span="24">
            <div class="fc-input-label-txt">
              {{ $t('common.wo_report.time') }}
            </div>
            <div class="form-input">
              <f-date-picker
                v-model="time"
                type="datetimerange"
                start-placeholder="Start Date"
                end-placeholder="End Date"
                @change="pickerChange"
                class="fc-input-full-border-select2 form-date-picker"
              ></f-date-picker>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          type="primary"
          @click="saveAssetBreakdown"
          :loading="isSaving"
          class="modal-btn-save"
          >{{ isSaving ? 'Saving...' : $t('common._common._save') }}</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'
import { isEmpty, isArray } from '@facilio/utils/validation'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import moment from 'moment-timezone'

export default {
  props: ['assetBDSourceDetails', 'visibility'],
  components: {
    ErrorBanner,
    FDatePicker,
  },
  data() {
    return {
      breakdown: {
        sourceId: null,
        condition: null,
        assetid: null,
        sourceType: null,
      },
      time: [],
      fromtime: null,
      totime: null,
      error: false,
      errorText: '',
      isSaving: false,
    }
  },
  mounted() {
    this.initAssetBreakDown()
  },
  methods: {
    initAssetBreakDown() {
      this.breakdown = { ...this.assetBDSourceDetails }
    },
    pickerChange(value) {
      if (!isEmpty(value) && isArray(value)) {
        this.fromtime = value[0]
        this.totime = value[1]
        this.time = value.map(val => this.getTimeInSystemZone(val))
      } else this.time = []
    },
    validation() {
      if (isEmpty(this.breakdown.condition)) {
        this.errorText = this.$t('common.placeholders.please_enter_condition')
        this.error = true
      } else if (this.time.length != 2) {
        this.errorText = this.$t('common.placeholders.please_choose_time')
        this.error = true
      } else {
        this.errorText = ''
        this.error = false
      }
    },

    saveAssetBreakdown() {
      this.validation()

      if (this.error) return

      let assetBDSourceDetails = { ...this.breakdown }
      let { fromtime, totime } = this
      assetBDSourceDetails.fromtime = fromtime
      assetBDSourceDetails.totime = totime

      this.isSaving = true

      this.$http
        .post('/v2/assetbreakdown/addNew', { assetBDSourceDetails })
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success(
              this.$t('common._common.Asset_breakdown_saved_successfully')
            )
            this.$emit('onSave')
            this.closeDialog()
          } else {
            this.$message.error(response.data.message)
          }
          this.isSaving = false
        })
        .catch(() => {
          this.$message.error(
            this.$t('common._common."Failed_to_record_breakdown')
          )
          this.closeDialog()
          this.isSaving = false
        })
    },
    getTimeInSystemZone(value) {
      return moment.tz(value, this.$timezone).format('YYYY-MM-DD HH:mm:ss')
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>

<style scoped>
.new-body-modal {
  height: auto;
}
</style>
<style lang="scss">
.asset-breakdown-popup {
  .el-dialog__body {
    padding: 0 0 54px;
  }
  .el-textarea .el-textarea__inner {
    min-height: 50px !important;
    width: 350px;
    resize: none;
  }

  .el-date-editor.el-range-editor.el-input__inner.el-date-editor--datetimerange {
    width: 100% !important;
  }
}
</style>
