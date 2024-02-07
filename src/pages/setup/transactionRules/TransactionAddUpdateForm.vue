<template>
  <el-dialog
    :visible="true"
    width="50%"
    :before-close="closeDialog"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog transaction-form"
  >
    <div class="form-header">
      {{
        isNew
          ? $t('setup.setup.new_transaction_rules')
          : $t('setup.setup.edit_transaction_rules')
      }}
    </div>
    <div class="fc-warning-tag-1 flex-middle justify-content-center tag-margin">
      <inline-svg
        src="svgs/alert"
        iconClass="icon text-left icon-sm fill-yellow vertical-middle"
      ></inline-svg>
      <div class="pL10 break-word">
        {{
          isNew
            ? `Newly created rules will be applicable to future transactions.`
            : `Recently edited rules will take effect in future transactions.`
        }}
      </div>
    </div>

    <div class="new-body-modal">
      <el-form
        :model="transactionRule"
        :label-position="'top'"
        ref="transactionForm"
        :rules="rules"
      >
        <template v-if="loading">
          <el-row class="mB10" v-for="index in [1, 2]" :key="index">
            <el-col :span="24">
              <span class="lines-header loading-shimmer width50"></span>
              <span class="lines-content loading-shimmer width100"></span>
            </el-col>
          </el-row>

          <el-row class="mB10">
            <el-col :span="24">
              <span class="lines-header loading-shimmer width50"></span>
            </el-col>
            <el-col :span="24">
              <span class="lines-content loading-shimmer width50"></span>
            </el-col>
          </el-row>
        </template>

        <template v-else>
          <el-form-item :label="$t('setup.setup.transaction_name')" prop="name">
            <el-input
              v-model="transactionRule.name"
              class="fc-input-full-border2"
              :placeholder="$t('setup.setup.transaction_name')"
            >
            </el-input>
          </el-form-item>

          <el-form-item
            :label="$t('setup.setup.transaction_date')"
            prop="transactionConfigJson.transactionDate"
          >
            <el-select
              :placeholder="$t('setup.setup.transaction_date')"
              v-model="transactionRule.transactionConfigJson.transactionDate"
              filterable
              class="fc-input-full-border2 width100"
            >
              <el-option
                v-for="(date, index) in dateFields"
                :key="index"
                :label="date.displayName"
                :value="date.name"
              >
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item
            :label="$t('setup.setup.resource_field')"
            prop="transactionConfigJson.transactionResource"
          >
            <el-select
              v-model="
                transactionRule.transactionConfigJson.transactionResource
              "
              filterable
              :placeholder="$t('setup.setup.transaction_resource_field')"
              :clearable="true"
              :loading="resourceFieldLoading"
              class="fc-input-full-border2 width100"
            >
              <el-option
                v-for="(field, fieldIndex) in resourceFields"
                :key="fieldIndex"
                :label="field.displayName"
                :value="field.id"
              >
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item
            :label="$t('setup.setup.chart_of_accounts')"
            prop="transactionConfigJson.account"
          >
            <FLookupField
              class="resource-field"
              :model.sync="transactionChartOfAccount"
              :field="chartOfAccountLookup"
              :fetchOptionsOnLoad="true"
              :hideLookupIcon="true"
            ></FLookupField>
          </el-form-item>

          <el-form-item
            :label="$t('setup.setup.transaction_amount')"
            prop="transactionConfigJson.transactionAmount"
          >
            <el-select
              :placeholder="$t('setup.setup.transaction_amount')"
              filterable
              v-model="transactionRule.transactionConfigJson.transactionAmount"
              class="fc-input-full-border2 width100"
            >
              <el-option
                v-for="(date, index) in costFields"
                :key="index"
                :label="date.displayName"
                :value="date.name"
              >
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item
            :label="$t('setup.setup.transaction_type')"
            prop="transactionConfigJson.transactionType"
          >
            <el-select
              v-model="transactionRule.transactionConfigJson.transactionType"
              filterable
              :placeholder="$t('setup.setup.transaction_type')"
              class="fc-input-full-border2 width100"
            >
              <el-option
                v-for="(value, label) in transactionTypes"
                :key="value"
                :label="label"
                :value="value"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item class="mB0">
            <div class="transaction-form-criteria-builder-label">
              {{ $t('setup.users_management.criteria') }}
            </div>
            <div class="fc-sub-title-desc">
              {{ $t('setup.setup.specify_rules') }}
            </div>
            <CriteriaBuilder
              v-model="transactionRule.criteria"
              :moduleName="moduleName"
              ref="criteria-builder"
            />
          </el-form-item>
        </template>
      </el-form>
    </div>

    <div class="modal-dialog-footer">
      <el-button @click="closeDialog()" class="modal-btn-cancel">
        {{ $t('maintenance._workorder.cancel') }}
      </el-button>

      <el-button
        type="primary"
        @click="submitForm()"
        :loading="saving"
        class="modal-btn-save"
      >
        {{
          saving
            ? $t('maintenance._workorder.saving')
            : $t('maintenance._workorder.save')
        }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import FLookupField from '@/forms/FLookupField'
import { isEmpty } from '@facilio/utils/validation'
import { CriteriaBuilder } from '@facilio/criteria'

const chartOfAccountLookup = {
  isDataLoading: false,
  options: [],
  lookupModuleName: 'chartofaccount',
  field: {
    lookupModule: {
      name: 'chartofaccount',
      displayName: 'Chart Of Account',
    },
  },
  forceFetchAlways: true,
  filters: {},
  multiple: false,
  isDisabled: false,
}

export default {
  props: ['isNew', 'moduleName', 'rule'],
  components: { CriteriaBuilder, FLookupField },

  data() {
    return {
      saving: false,
      loading: false,
      metaFields: [],
      resourceModuleInfo: {},
      resourceFieldLoading: false,
      transactionTypes: {
        Credit: '1',
        Debit: '2',
      },
      transactionRule: {
        name: '',
        transactionConfigJson: {
          transactionDate: null,
          account: null,
          transactionAmount: null,
          transactionType: null,
          transactionResource: null,
          criteria: null,
        },
      },
      chartOfAccountLookup,
      rules: {
        name: {
          required: true,
          message: 'Please enter the transaction name',
          trigger: 'blur',
        },
        'transactionConfigJson.transactionDate': {
          required: true,
          message: 'Please enter transaction date',
          trigger: 'change',
        },
        'transactionConfigJson.account': {
          required: true,
          message: 'Please choose an account',
          trigger: 'change',
        },
        'transactionConfigJson.transactionAmount': {
          required: true,
          message: 'Please enter transaction amount',
          trigger: 'change',
        },
        'transactionConfigJson.transactionType': {
          required: true,
          message: 'Please enter transaction type',
          trigger: 'change',
        },
      },
    }
  },

  async created() {
    this.loading = true
    await this.getModuleFields()

    if (!this.isNew) {
      this.transactionRule = { ...this.rule }
    }
    this.loading = false
    await this.loadResourceModuleInfo()
  },

  computed: {
    dateFields() {
      return this.metaFields.filter(
        field =>
          this.$getProperty(field, 'dataTypeEnum._name', '') === 'DATE_TIME'
      )
    },
    costFields() {
      return this.metaFields.filter(
        field =>
          field.metric === 15 || field.displayType?._name === 'MULTI_CURRENCY'
      )
    },
    resourceFields() {
      let { metaFields, resourceModuleInfo, $getProperty } = this

      return (metaFields || []).filter(field => {
        let isSiteFld = ['siteId', 'site'].includes(field?.name)
        let resourceModuleId = (resourceModuleInfo || {}).moduleId
        let isMultiLookupFld =
          $getProperty(field, 'dataTypeEnum._name', '') === 'MULTI_LOOKUP'
        let extendedLookupFldIdList =
          $getProperty(field, 'lookupModule.extendedModuleIds') || []
        let isResourceFld =
          !isEmpty(resourceModuleId) &&
          extendedLookupFldIdList.includes(resourceModuleId)

        return (isSiteFld || isResourceFld) && !isMultiLookupFld
      })
    },
    transactionChartOfAccount: {
      get() {
        let { transactionConfigJson } = this.transactionRule || {}
        let { account } = transactionConfigJson || null
        return !isEmpty(account) ? parseInt(account) : null
      },
      set(value) {
        this.$set(this.transactionRule.transactionConfigJson, 'account', value)
      },
    },
  },

  methods: {
    async getModuleFields() {
      let { moduleName } = this
      let { error, data } = await API.get(
        `/module/metafields?moduleName=${moduleName}`
      )

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.metaFields = data?.meta?.fields || []
      }
    },
    submitForm() {
      this.$refs['transactionForm'].validate(async valid => {
        let criteriaValidation = this.$refs['criteria-builder']?.validate()
        if (!valid || !criteriaValidation) return

        this.saving = true

        let { moduleName, transactionRule, isNew } = this
        let { transactionConfigJson, name, criteria } = transactionRule
        let rule = {
          ...transactionRule,
          ruleType: 43,
          event: {
            moduleName,
            activityType: 268435456,
          },
          transactionConfigJson: {
            ...transactionConfigJson,
            transactionName: name,
            transactionSourceModuleName: moduleName,
            transactionRollUpModuleName: 'budgetmonthlyamount',
            transactionRollUpFieldName: 'actualMonthlyAmount',
            transactionSourceRecordId: 'id',
            creationModuleName: 'transaction',
          },
          criteria,
        }
        let url = isNew
          ? '/v2/transactionrule/add'
          : '/v2/transactionrule/update'
        let params = { moduleName, transactionRule: rule }
        let { error } = await API.post(url, params)

        if (error) {
          this.$message.error(
            error.message || 'Could not save transaction rule'
          )
        } else {
          this.$message.success('Transaction rule saved.')
          this.$emit('saved')
          this.closeDialog()
        }
        this.saving = false
      })
    },
    async loadResourceModuleInfo() {
      this.resourceFieldLoading = true

      let url = `/module/meta?moduleName=resource`
      let { error, data } = await API.get(url)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let { module } = data?.meta || {}
        this.resourceModuleInfo = module || {}
      }
      this.resourceFieldLoading = false
    },
    closeDialog() {
      this.$emit('onClose')
      this.$emit('saved')
    },
  },
}
</script>
<style lang="scss">
.fc-remove-required-field {
  .el-form-item__label {
    display: none;
  }
}
</style>
<style lang="scss" scoped>
.transaction-form {
  .form-header {
    font-weight: bold;
    letter-spacing: 0.9px;
    color: #333333;
    text-transform: uppercase;
    padding: 16px 40px 16px;
    border-bottom: 1px solid #edeeef;
  }
  .lines-header,
  .lines-content {
    border-radius: 5px;
    margin-bottom: 10px;
  }
  .lines-header {
    height: 15px;
  }
  .lines-content {
    height: 40px;
  }
  .transaction-form-criteria-builder-label {
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 1.6px;
    color: #385571;
    text-transform: uppercase;
  }
}
</style>
