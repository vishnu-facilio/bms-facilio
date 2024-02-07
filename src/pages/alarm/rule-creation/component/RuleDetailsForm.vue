<template>
  <div>
    <div id="ruledetails-header" class="section-header">
      {{ $t('alarm.rules.rule_details') }}
    </div>
    <el-form
      ref="alarmRulesForm"
      :model="preRequsite"
      :rules="validationRules"
      label-width="150px"
      label-position="left"
      class="p50 pT10 pB30"
    >
      <div class="section-container flex-container">
        <div class="form-one-column fc-label-required">
          <el-form-item
            :label="`${this.$t('alarm.rules.rule_name')}`"
            prop="name"
          >
            <el-input
              v-model="preRequsite.name"
              class="fc-input-full-border2"
              :placeholder="`${this.$t('alarm.rules.rule_name')}`"
              :autofocus="true"
            ></el-input>
          </el-form-item>
        </div>
        <div class="form-one-column">
          <el-form-item
            :label="`${this.$t('common.roles.description')}`"
            prop="description"
          >
            <el-input
              v-model="preRequsite.description"
              type="textarea"
              class="mT3 fc-input-full-border-textarea"
              :autofocus="true"
              :min-rows="2"
              :autosize="{ minRows: 3, maxRows: 4 }"
              :placeholder="`${this.$t('common.roles.description')}`"
            ></el-input>
          </el-form-item>
        </div>
        <div v-if="isAsset" class="form-one-column">
          <div class="form-one-column  fc-label-required">
            <el-form-item
              :label="`${this.$t('alarm.alarm.asset_category')}`"
              prop="assetCategory"
            >
              <FLookupField
                :key="`rules-assetcategory`"
                :model.sync="preRequsite.assetCategory.id"
                :field="assetCategoryField"
                :isRemoteField="false"
                :disabled="!isNew"
                @recordSelected="onAssetCategorySelected"
              ></FLookupField>
            </el-form-item>
          </div>
          <div v-if="!isAssetCategoryEmpty" class="form-one-column">
            <el-form-item>
              <el-radio-group v-model="bulkSelectOption">
                <el-radio label="all" class="fc-radio-btn">{{
                  $t('alarm.rules.all_assets')
                }}</el-radio>
                <el-radio label="include" class="fc-radio-btn">{{
                  $t('alarm.rules.specific_assets')
                }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
          <div class="form-one-column">
            <el-form-item :label="`${this.$t('common.products.assets')}`">
              <div v-if="isAllAssetsSelected">
                <el-input
                  class="fc-input-full-border2"
                  :placeholder="allAssetsPlaceHolderText"
                  :disabled="true"
                ></el-input>
              </div>
              <FLookupField
                v-else
                :key="`assets-lookup-field`"
                ref="assets-lookup-field"
                :model.sync="preRequsite.assets"
                :field="assetField"
                :preHookFilterConstruction="constructCategoryFilter"
                @showLookupWizard="showLookupWizard"
              ></FLookupField>
            </el-form-item>
          </div>
        </div>
        <div class="form-one-column  fc-label-required">
          <el-form-item
            :label="`${this.$t('rule.create.rule_interval')}`"
            prop="ruleInterval"
          >
            <el-select
              v-model="preRequsite.ruleInterval"
              class="fc-input-full-border2 width100"
              filterable
              clearable
              :placeholder="`${this.$t('common._common.select')}`"
            >
              <el-option
                v-for="(key, label) in ruleIntervalOptions"
                :key="key"
                :label="label"
                :value="key"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
      </div>
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
        :categoryId="preRequsite.assetCategory.id"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
    </div>
  </div>
</template>
<script>
import FLookupField from '@/forms/FLookupField'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { isEmpty } from 'util/validation'
import isEqual from 'lodash/isEqual'
import { LookupWizard } from '@facilio/ui/forms'

export default {
  components: {
    FLookupField,
    FLookupFieldWizard,
    LookupWizard,
  },
  props: {
    isNew: {
      type: Boolean,
    },
    ruleInfo: {
      type: Object,
    },
  },
  data() {
    return {
      preRequsite: {
        name: '',
        description: '',
        assetCategory: {
          id: null,
        },
        assets: null,
        ruleInterval: 900000,
        assetCategoryId: null,
      },
      validationRules: {
        name: [
          {
            required: true,
            message: 'Please enter rule name',
            trigger: 'blur',
          },
        ],
        assetCategory: [
          {
            required: true,
            message: 'Please select asset category',
            trigger: 'blur',
          },
        ],
        ruleInterval: [
          {
            required: true,
            message: 'Please select rule interval',
            trigger: 'blur',
          },
        ],
      },
      resourceType: 'asset',
      assetCategoryField: {
        lookupModuleName: 'assetcategory',
        lookupModule: {
          type: -1,
        },
        options: [],
        isDataLoading: false,
      },
      assetField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'asset',
        placeHolderText: `${this.$t('commissioning.sheet.select_asset')}`,
        field: {
          lookupModule: {
            name: 'asset',
            displayName: 'Asset',
          },
        },
        multiple: true,
      },
      canShowLookupWizard: false,
      selectedLookupField: null,
      bulkSelectOption: 'all',
      selectedCategoryLabel: '',
      isMatchedAssetSelection: false,
      ruleIntervalOptions: {
        '1 Min': 60000,
        '2 Mins': 120000,
        '3 Mins': 180000,
        '4 Mins': 240000,
        '5 Mins': 300000,
        '10 Mins': 600000,
        '15 Mins': 900000,
        '20 Mins': 1200000,
        '30 Mins': 1800000,
        '1 Hr': 3600000,
        '2 Hr': 7200000,
        '3 Hr': 10800000,
        '4 Hr': 14400000,
        '8 Hr': 28800000,
        '12 Hr': 43200000,
        '1 Day': 86400000,
      },
    }
  },
  created() {
    this.prefillRuleDetails()
  },
  computed: {
    isAsset() {
      let { resourceType } = this
      return resourceType === 'asset'
    },
    isAssetCategoryEmpty() {
      let { preRequsite } = this
      let { assetCategory } = preRequsite || {}
      let { id } = assetCategory || {}
      return isEmpty(id)
    },
    isAllAssetsSelected() {
      let { bulkSelectOption } = this
      return bulkSelectOption === 'all'
    },
    allAssetsPlaceHolderText() {
      let { selectedCategoryLabel } = this
      return `All ${selectedCategoryLabel} selected`
    },
    isNewLookupWizardEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_LOOKUP_WIZARD')
    },
  },
  watch: {
    isAsset() {
      let { preRequsite } = this
      let { assetCategory } = preRequsite || {}
      let { id } = assetCategory || {}
      this.$set(id, 'id', null)
    },
    preRequsite: {
      handler(value) {
        this.$emit('ruleDetailsChange', value)
        this.$emit('sendRuleDetail', value)
      },
      deep: true,
    },
    'preRequsite.assetCategory.id': {
      async handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          let { $refs, assetField, preRequsite, isMatchedAssetSelection } = this
          let elem = $refs['assets-lookup-field']
          if (!isEmpty(elem) && !isMatchedAssetSelection) {
            let options = (await elem.getOptions({ initialFetch: true })) || []
            this.$set(preRequsite, 'assets', null)
            this.$set(assetField, 'options', options)
          }
          this.isMatchedAssetSelection = false
        }
      },
      immediate: true,
    },
  },
  methods: {
    prefillRuleDetails() {
      let { preRequsite, ruleInfo } = this
      if (!isEmpty(ruleInfo)) {
        this.preRequsite = ruleInfo
        let { assets, assetCategory } = ruleInfo
        if (!isEmpty(assets)) {
          this.bulkSelectOption = 'include'
          this.isMatchedAssetSelection = true
          this.$set(preRequsite, 'assets', assets)
        }
        let { ns } = ruleInfo
        let { execInterval } = ns || {}

        let { id } = assetCategory
        this.preRequsite.assetCategoryId = id
        this.$set(this.preRequsite, 'ruleInterval', execInterval)
      }
      this.$emit('sendRuleDetail', preRequsite)
    },
    showLookupWizard(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizard', canShow)
    },
    setLookupFieldValue(props) {
      let { preRequsite } = this
      let { field } = props
      let { options = [], selectedItems = [] } = field
      let selectedItemIds = []
      if (!isEmpty(selectedItems)) {
        selectedItemIds = selectedItems.map(item => item.value)
        if (!isEmpty(options)) {
          let ids = options.map(item => item.value)
          // Have to push only new options that doesnt exists in field options
          let newOptions = selectedItems.filter(
            item => !ids.includes(item.value)
          )
          options.unshift(...newOptions)
        } else {
          options = [...selectedItems]
        }
      }
      this.$set(preRequsite, `assets`, selectedItemIds)
      this.$set(this.selectedLookupField, 'options', options)
    },
    constructCategoryFilter() {
      let { preRequsite } = this
      let { assetCategory } = preRequsite || {}
      let { id } = assetCategory || {}
      let filters = {}
      if (!isEmpty(assetCategory)) {
        filters = {
          category: {
            operator: 'is',
            value: [`${id}`],
          },
        }
      }
      return filters
    },
    onAssetCategorySelected(selectedValue) {
      let { label } = selectedValue || {}
      let { assetField, preRequsite } = this
      let { selectedItems } = assetField || {}
      let { assetCategory } = preRequsite || {}
      let { id } = assetCategory || {}
      if (!isEmpty(id)) {
        this.$set(preRequsite, 'assetCategoryId', id)
      }
      if (!isEmpty(label)) {
        this.selectedCategoryLabel = label
      }
      if (!isEmpty(selectedItems)) {
        assetField.selectedItems = []
        this.$set(preRequsite, 'assets', null)
      }
    },
  },
}
</script>
