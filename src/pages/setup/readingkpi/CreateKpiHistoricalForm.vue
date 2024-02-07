<template>
  <el-dialog
    :title="$t('kpi.historical.kpi_history')"
    :visible="true"
    :append-to-body="true"
    custom-class="setup-dialog40 kpi-historical-popup-dialog bR8"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <el-form
      label-position="top"
      ref="historyForm"
      :model="historyForm"
      :rules="formRules"
    >
      <el-form-item label="KPI Template" prop="kpiId">
        <FLookupField
          :key="`kpi-lookup-field`"
          ref="kpi-lookup-field"
          :model.sync="historyForm.kpiId"
          :field="kpiField"
          @showLookupWizard="showKpiLookupWizard"
          :preHookFilterConstruction="constructKpiTypeFilter"
        ></FLookupField>
      </el-form-item>
      <el-form-item label="Asset" prop="assets">
        <FLookupField
          :key="`assets-lookup-field`"
          ref="assets-lookup-field"
          :model.sync="historyForm.assets"
          :field="assetField"
          :categoryId="assetCategoryId"
          :disabled="isKpiSelected"
          @showLookupWizard="showAssetsLookupWizard"
        ></FLookupField>
      </el-form-item>
      <el-form-item
        label="Interval"
        prop="dateRange"
        class="reading-kpi-date-picker"
      >
        <FddHistoricalDatePicker @rangeChange="setDateRange">
        </FddHistoricalDatePicker>
      </el-form-item>

      <el-form-item class="mB10">
        <div class="kpi-history-log-create-button">
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
        :categoryId="assetCategoryIdForWizard"
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
  props: ['kpiId'],
  components: {
    FLookupField,
    FLookupFieldWizard,
    FddHistoricalDatePicker,
    LookupWizard,
  },
  created() {
    let { kpiId } = this
    if (!isEmpty(kpiId)) {
      this.historyForm.kpiId = kpiId
    }
  },
  data() {
    return {
      kpiList: [],
      currentKpi: null,
      assetCategoryId: null,
      assetCategoryIdForWizard: null,
      historyForm: {
        kpiId: null,
        assets: null,
        dateRange: null,
      },
      assetField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'asset',
        placeHolderText: `${this.$t('kpi.historical.assets')}`,
        field: {
          lookupModule: {
            name: 'asset',
            displayName: 'Asset',
          },
        },
        filters: {},
        multiple: true,
      },
      isKpiWizard: false,
      kpiField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'readingkpi',
        placeHolderText: `${this.$t('kpi.historical.select_kpi')}`,
        field: {
          lookupModule: {
            name: 'readingkpi',
            displayName: 'Reading KPI',
          },
        },
        filters: { status: { operatorId: 15, value: ['true'] } },
        multiple: false,
      },
      canShowLookupWizard: false,
      selectedLookupField: null,
      formRules: {
        kpiId: [
          { required: true, message: 'Please select a KPI', trigger: 'blur' },
        ],
        dateRange: [
          {
            required: true,
            message: 'Please choose a Date Range',
            trigger: 'blur',
          },
        ],
      },
    }
  },
  computed: {
    isKpiSelected() {
      let { historyForm: { kpiId } = {} } = this
      return isEmpty(kpiId)
    },
    isNewLookupWizardEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_LOOKUP_WIZARD')
    },
  },
  watch: {
    'historyForm.kpiId': function(newVal) {
      if (!isEmpty(newVal)) this.loadKpi(newVal)
    },
  },
  methods: {
    reset() {
      this.historyForm = {
        kpiId: null,
        assets: null,
        dateRange: null,
      }
    },
    closeDialog() {
      this.reset()
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
      let { historyForm } = this
      let { kpiId, dateRange, assets } = historyForm || {}

      let url = 'v3/readingKpi/historical/run'
      let params = {
        recordId: kpiId,
        assets,
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
    async loadKpi(force = true) {
      let { historyForm: { kpiId } = {} } = this
      try {
        let { readingkpi } = await API.fetchRecord(
          'readingkpi',
          {
            id: kpiId,
          },
          { force }
        )
        this.currentKpi = readingkpi
        let { assetCategory: { id } = {} } = readingkpi || {}
        this.assetCategoryId = id
      } catch (errorMsg) {
        this.$message.error(errorMsg.message)
      }
    },
    showKpiLookupWizard(field, canShow) {
      this.isKpiWizard = true
      this.assetCategoryIdForWizard = null
      this.showLookupWizard(field, canShow)
    },
    showAssetsLookupWizard(field, canShow) {
      this.isKpiWizard = false
      this.assetCategoryIdForWizard = this.assetCategoryId
      this.showLookupWizard(field, canShow)
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
      if (this.isKpiWizard) {
        this.$set(historyForm, `kpiId`, selectedItemIds[0])
      } else {
        this.$set(historyForm, `assets`, selectedItemIds)
        this.isKpiWizard = false
      }

      this.$set(this.selectedLookupField, 'options', options)
    },
    setDateRange(dateObj) {
      this.historyForm.dateRange = [...dateObj]
    },
    constructKpiTypeFilter() {
      return {
        kpiType: {
          operator: 'is',
          value: ['1', '2'],
        },
      }
    },
  },
}
</script>

<style lang="scss">
.kpi-historical-popup-dialog {
  .el-dialog__header {
    border-bottom: solid 1px #d0d9e2;
  }
  .el-dialog__body {
    padding: 10px 20px;
  }
}
.kpi-history-log-create-button {
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
