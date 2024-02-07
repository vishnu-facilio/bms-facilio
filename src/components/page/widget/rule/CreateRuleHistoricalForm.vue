<template>
  <el-dialog
    :title="$t('rule.historical.run_historical')"
    :visible="true"
    :append-to-body="true"
    custom-class="setup-dialog40 rule-historical-popup-dialog bR8"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <el-form
      label-position="top"
      ref="historyForm"
      :model="historyForm"
      :rules="formRules"
    >
      <el-form-item label="Asset" prop="assets">
        <FLookupField
          :key="`assets-lookup-field`"
          ref="assets-lookup-field"
          :model.sync="historyForm.assets"
          :field="assetField"
          :categoryId="assetCategoryId"
          @showLookupWizard="showLookupWizard"
        ></FLookupField>
      </el-form-item>
      <el-form-item label="Actions">
        <el-checkbox
          :label="$t('rule.historical.create_alarm')"
          v-model="historyForm.alarm"
          disabled
        ></el-checkbox>
        <el-checkbox
          :label="$t('rule.historical.calculate_fault_impact')"
          v-model="historyForm.impact"
          disabled
        ></el-checkbox>

        <el-checkbox
          :label="$t('rule.historical.calculate_rca')"
          v-model="historyForm.rca"
          disabled
        ></el-checkbox>
      </el-form-item>

      <el-form-item label="Interval" prop="dateRange">
        <FddHistoricalDatePicker @rangeChange="setDateRange">
        </FddHistoricalDatePicker>
      </el-form-item>

      <el-form-item class="mB10">
        <div class="rule-history-log-create-button">
          <el-button @click="closeDialog">
            {{ $t('common._common.cancel') }}
          </el-button>
          <el-button type="primary" @click="onSubmit">
            {{ $t('common.products.create') }}
          </el-button>
        </div>
      </el-form-item>
    </el-form>
    <div v-if="canShowLookupWizard">
      <LookupWizard
        v-if="isNewLookupWizardEnabled"
        :canShowLookupWizard.sync="canShowLookupWizard"
        :field="selectedLookupField"
        @setLookupFieldValue="setLookupFieldValue"
      ></LookupWizard>
      <FLookupFieldWizard
        v-else
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="selectedLookupField"
        :categoryId="assetCategoryId"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
    </div>
  </el-dialog>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import FLookupField from '@/forms/FLookupField'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { LookupWizard } from '@facilio/ui/forms'
import { API } from '@facilio/api'
import FddHistoricalDatePicker from 'src/pages/setup/readingkpi/FddHistoricalDatePicker.vue'

export default {
  components: {
    FLookupField,
    FLookupFieldWizard,
    FddHistoricalDatePicker,
    LookupWizard,
  },
  created() {
    this.loadRule()
  },
  data() {
    return {
      ruleList: [],
      assetCategoryId: null,
      historyForm: {
        assets: null,
        dateRange: null,
        alarm: true,
        rca: true,
        impact: true,
      },
      assetField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'asset',
        placeHolderText: `${this.$t('rule.historical.assets')}`,
        field: {
          lookupModule: {
            name: 'asset',
            displayName: 'Asset',
          },
        },
        filters: {},
        multiple: true,
      },
      canShowLookupWizard: false,
      selectedLookupField: null,
      formRules: {
        dateRange: [
          {
            required: true,
            message: `${this.$t('rule.historical.validate_daterange')}`,
            trigger: 'blur',
          },
        ],
      },
    }
  },
  computed: {
    ruleId() {
      let { $route: { params } = {} } = this
      return parseInt(this.$getProperty(params, 'id'))
    },
    isNewLookupWizardEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_LOOKUP_WIZARD')
    },
  },
  methods: {
    closeDialog() {
      this.$emit('close')
    },
    async onSubmit() {
      this.$refs['historyForm'].validate(valid => {
        if (valid) {
          this.createHistory()
        }
      })
    },
    async createHistory() {
      let { historyForm, ruleId } = this
      let { dateRange, assets } = historyForm || {}

      let url = 'v3/readingrule/historical/run'
      let params = {
        recordId: ruleId,
        assetIds: assets,
        startTime: dateRange[0],
        endTime: dateRange[1],
      }
      let { data, error } = await API.post(url, params)
      if (!isEmpty(error)) {
        this.$message.error(error.message)
      } else {
        let { success } = data || {}
        this.$message.success(success)
        this.closeDialog()
      }
    },
    async loadRule(force = true) {
      let { ruleId } = this
      try {
        let { newreadingrules } = await API.fetchRecord(
          'newreadingrules',
          {
            id: ruleId,
          },
          { force }
        )
        let { assetCategory: { id } = {} } = newreadingrules || {}
        this.assetCategoryId = id
      } catch (errorMsg) {
        this.$message.error(errorMsg.message)
      }
    },
    showLookupWizard(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizard', canShow)
    },
    setLookupFieldValue(props) {
      let { historyForm } = this
      let { field } = props
      let { options = [], selectedItems = [] } = field || {}
      let selectedItemIds = []
      if (!isEmpty(selectedItems)) {
        selectedItemIds = selectedItems.map(item => item.value)
        if (!isEmpty(options)) {
          let ids = options.map(item => item.value)
          let newOptions = selectedItems.filter(
            item => !ids.includes(item.value)
          )
          options.unshift(...newOptions)
        } else {
          options = [...selectedItems]
        }
      }
      this.$set(historyForm, `assets`, selectedItemIds)
      this.$set(this.selectedLookupField, 'options', options)
    },
    setDateRange(dateObj) {
      this.$set(this.historyForm, 'dateRange', dateObj)
    },
  },
}
</script>

<style lang="scss">
.rule-historical-popup-dialog {
  .el-dialog__header {
    border-bottom: solid 1px #d0d9e2;
  }
  .el-dialog__body {
    padding: 10px 20px;
  }
}
.rule-history-log-create-button {
  display: flex !important;
  flex-direction: row-reverse;
  gap: 10px;
  .el-button--primary {
    background-color: #3ab2c2;
    border-color: #3ab2c2;
  }
  .el-button--default {
    border: solid 1px #38b2c2;
  }
  .el-button--primary:hover {
    background-color: #3ab2c2;
    border-color: #3ab2c2;
  }
}
</style>
