<template>
  <el-dialog
    :visible="true"
    :before-close="closeForm"
    :append-to-body="true"
    :title="title"
    class="currency-dialog  agents-dialog fc-dialog-center-container"
  >
    <div class="height400">
      <div class="note">
        {{ note }}
      </div>
      <el-form
        ref="currency_form"
        :model="currencyData"
        :rules="validationRules"
        :label-position="'left'"
        label-width="200px"
        class="currency-form"
      >
        <el-form-item
          prop="currencyCode"
          :label="$t('setup.setup_profile.currency')"
          :required="true"
        >
          <el-select
            v-model="currencyData.currencyCode"
            placeholder="Select"
            class="fc-input-full-border-select2 width100"
            :loading="isLoading"
            :disabled="!$validation.isEmpty(record)"
            @change="currentSymbol()"
          >
            <el-option
              v-for="option in metricsList"
              :key="option.currencyCode"
              :label="`${option.currencyCode} - ${option.displaySymbol}`"
              :value="option.currencyCode"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="!isDefault"
          prop="exchangeRate"
          :label="$t('setup.currency.conversion_rate', { baseCode })"
        >
          <template #label>
            {{ $t('common._common.conversion_rate') }}
            <span class="description">{{ `1 ${baseCode}` }}</span> =
          </template>
          <el-input
            type="number"
            v-model.number="currencyData.exchangeRate"
            class="fc-input-full-border-select2 width100 currency-input"
            :placeholder="$t('setup.currency.enter_conversion_rate')"
          >
            <template slot="append">{{ currencySymbol || '' }}</template>
          </el-input>
        </el-form-item>
        <el-form-item prop="decimalPlaces" label="Decimal">
          <Select
            v-model="currencyData.decimalPlaces"
            :options="options"
            valueName="value"
            labelName="label"
          ></Select>
        </el-form-item>
      </el-form>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeForm()">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button
        :loading="isLoading"
        class="modal-btn-save"
        type="primary"
        @click="saveData()"
        >{{ $t('common._common._save') }}</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import { Select } from '@facilio/ui/forms'
import { CurrencyListModel } from 'src/pages/setup/organizationSetting/currency/CurrencyModel'

export default {
  props: ['visiblity', 'record', 'isDefault', 'baseCode'],
  components: { Select },
  async created() {
    this.setCurrencyData()
    await this.loadMetricUnits()
  },
  data() {
    return {
      isLoading: false,
      isSaving: false,
      metricsList: [],
      currencySymbol: null,
      currencyData: {},
      validationRules: {
        currencyCode: [
          {
            required: true,
            message: this.$t('common._common.please_select_currency'),
            trigger: 'blur',
          },
        ],
        exchangeRate: {
          validator: function(rule, value, callback) {
            if (!value || parseInt(value) < 0) {
              callback(new Error(this.$t('common._common.exchange_rate_error')))
            } else {
              callback()
            }
          }.bind(this),
          trigger: 'change',
        },
      },
    }
  },
  computed: {
    options() {
      return [
        {
          label: '1',
          value: 1,
        },
        {
          label: '2',
          value: 2,
        },
        {
          label: '3',
          value: 3,
        },
      ]
    },
    title() {
      return this.isDefault
        ? this.$t('setup.currency.choose_default')
        : this.$t('setup.currency.new_currency')
    },
    note() {
      return this.isDefault
        ? this.$t('setup.currency.default_note')
        : this.$t('setup.currency.note')
    },
  },
  methods: {
    async loadMetricUnits() {
      this.isLoading = true
      this.metricsList = await CurrencyListModel.loadMetrics()
      this.isLoading = false
    },
    closeForm() {
      this.$emit('close')
    },
    async saveData() {
      this.$refs['currency_form'].validate(async valid => {
        if (valid) {
          this.isSaving = true
          let { error } = await this.currencyData.save()

          if (error) {
            this.$message.error(
              error.message || this.$t('setup.currency.unable_to_save_currency')
            )
          } else {
            this.$message.success(
              this.record?.id > 0
                ? this.$t('common.products.currency_updated_successfully')
                : this.$t('common.products.currency_added_successfully')
            )
            this.$emit('saved')
            this.closeForm()
            this.isDefault && (await this.$store.dispatch('getCurrentAccount'))
          }
          this.isSaving = false
        }
      })
    },
    currentSymbol() {
      if (!this.isDefault) {
        let { currencyCode } = this.currencyData
        let metric = this.metricsList.find(
          metric => metric.currencyCode === currencyCode
        )
        this.currencySymbol = metric?.displaySymbol
      }
    },
    async setCurrencyData() {
      let { record } = this
      this.currencyData = new CurrencyListModel(record)
      this.currencySymbol = record?.symbol
    },
  },
}
</script>
<style lang="scss">
.note {
  text-align: center;
  background-color: #f2f7ff;
  padding: 10px 0;
  font-size: 14px;
  color: #2e2e49;
  letter-spacing: 0.32px;
}

.description {
  color: #808080;
  font-size: 12px;
  letter-spacing: 0.43px;
}

.currency-dialog {
  .el-dialog__body {
    padding: 0 !important;
  }
}

.currency-form {
  .el-form-item {
    width: 60%;
    margin: 40px auto;
    .el-form-item__content {
      line-height: 0px !important;
    }
  }
}

.currency-input {
  .el-input__inner {
    border-top-right-radius: 0px !important;
    border-bottom-right-radius: 0px !important;
  }
  .el-input-group__append {
    background: #fff;
  }
}
</style>
