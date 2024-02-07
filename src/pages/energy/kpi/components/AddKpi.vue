<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div class="kpi-modal">
      <el-form
        :model="newkpi"
        :rules="rules"
        :label-position="'top'"
        ref="categoryForm"
        class="fc-form"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{
                isNew ? $t('common._common.new') : $t('common._common._edit')
              }}
              {{ $t('common.header.kpi_definition') }}
            </div>
          </div>
        </div>

        <div class="new-body-modal enpi-body-modal">
          <div class="body-scroll pR10">
            <el-row :gutter="20" class="mB10">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common._common.kpi_name') }}
                </p>
                <el-form-item prop="name">
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="newkpi.name"
                    type="text"
                    autocomplete="off"
                    :placeholder="$t('common._common.enter_kpi_name')"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20" class="mB10">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common.wo_report.report_description') }}
                </p>
                <el-form-item>
                  <el-input
                    type="textarea"
                    :autosize="{ minRows: 4, maxRows: 4 }"
                    class="width100 fc-input-full-border-textarea"
                    :placeholder="$t('common._common.enter_desc')"
                    v-model="newkpi.description"
                    resize="none"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20" class="mB10">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('common.header.kpi_category') }}
                </p>
                <el-form-item
                  class="category-autocomplete"
                  prop="kpiCategoryName"
                >
                  <el-select
                    class="fc-input-full-border2 width300px"
                    v-model="newkpi.kpiCategoryName"
                    filterable
                    :placeholder="$t('common.products.select_kpi_category')"
                    :disabled="!isNew"
                  >
                    <el-option
                      v-for="category in kpiCategory"
                      :key="category.value"
                      :label="category.label"
                      :value="category.label"
                    ></el-option>
                  </el-select>

                  <el-button
                    v-if="isNew"
                    @click="showAddCategoryDialog"
                    class="inline mL10"
                    v-tippy
                    :title="$t('common.products.add_kpi_category')"
                  >
                    <inline-svg
                      src="svgs/add"
                      iconClass="icon icon-xxs"
                    ></inline-svg>
                  </el-button>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row class="mB10">
              <el-col :span="12">
                <p class="fc-input-label-txt">
                  {{ $t('common.products.site') }}
                </p>
                <Lookup
                  v-model="newkpi.siteId"
                  :field="siteField"
                  :hideLookupIcon="true"
                  :disabled="!isNew"
                >
                </Lookup>
              </el-col>
            </el-row>

            <el-row class="mB10">
              <el-col :span="24">
                <p class="fc-input-label-txt mT10">
                  {{ $t('common._common.type') }}
                </p>
                <el-radio
                  :disabled="!isNew"
                  v-model="kpiType"
                  :value="$t('common.space_asset_chooser.space')"
                  label="space"
                  class="fc-radio-btn"
                  >{{ $t('common.space_asset_chooser.space') }}</el-radio
                >
                <el-radio
                  :disabled="!isNew"
                  v-model="kpiType"
                  label="asset"
                  :value="$t('common._common.asset')"
                  class="fc-radio-btn"
                  >{{ $t('common._common.asset') }}</el-radio
                >
              </el-col>
            </el-row>

            <el-row :gutter="20" class="mT30">
              <template v-if="kpiType === 'asset'">
                <el-col :span="12">
                  <div class="fc-input-label-txt">
                    {{ $t('common._common.category') }}
                  </div>
                  <el-form-item prop="assetCategoryId">
                    <el-select
                      :disabled="!isNew"
                      v-model="newkpi.assetCategoryId"
                      filterable
                      :placeholder="$t('common.products.select_category')"
                      class="fc-input-full-border-select2 width100"
                    >
                      <el-option
                        v-for="(category, index) in assetCategory"
                        :key="index"
                        :label="category.displayName"
                        :value="parseInt(category.id)"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <div class="fc-input-label-txt">
                    {{ $t('common.products.asset_s') }}
                  </div>
                  <FLookupField
                    class="resource-field"
                    :model.sync="newkpi.includedResources"
                    :field="spaceAssetField"
                    :siteId="newkpi.siteId"
                    :categoryId="newkpi.assetCategoryId"
                    :disabled="!isNew || !newkpi.assetCategoryId"
                    @showLookupWizard="showLookupWizard"
                  ></FLookupField>
                </el-col>
              </template>

              <el-col :span="12" v-else>
                <template class="pR0">
                  <div class="fc-input-label-txt">
                    {{ $t('common.space_asset_chooser.space') }}
                  </div>
                  <el-form-item prop="resourceId">
                    <FLookupField
                      class="resource-field"
                      :model.sync="newkpi.resourceId"
                      :field="spaceAssetField"
                      :siteId="newkpi.siteId"
                      :disabled="!isNew"
                      @showLookupWizard="showLookupWizard"
                    ></FLookupField>
                  </el-form-item>
                </template>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <div class="fc-input-label-txt mT30">
                  {{ $t('common._common.frequency') }}
                </div>
                <el-form-item prop="frequency">
                  <el-select
                    v-model="newkpi.frequency"
                    class="fc-input-full-border2 width100"
                    :placeholder="$t('common._common.choose_frequency')"
                    :disabled="!isNew"
                  >
                    <el-option
                      v-for="(label, value) in frequencyTypes"
                      :key="value"
                      :label="label"
                      :value="parseInt(value)"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row class="mB20">
              <el-col :span="24">
                <div class="fc-input-label-txt mT30 pB20">
                  {{ $t('commissioning.sheet.header_unit') }}
                </div>
                <el-radio
                  v-model="unit"
                  label="choose"
                  :value="$t('common.text.choose')"
                  class="fc-radio-btn"
                  >{{ $t('common.products.choose_unit') }}</el-radio
                >
                <el-radio
                  v-model="unit"
                  label="custom"
                  :value="$t('common.date_picker.custom')"
                  class="fc-radio-btn"
                  >{{ $t('common.header.custom_unit') }}</el-radio
                >
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="24">
                <el-col :span="12" v-if="unit === 'custom'">
                  <el-input
                    class="fc-input-full-border2"
                    v-model="formulaFieldUnit"
                    type="text"
                    :placeholder="$t('common._common.enter_unit')"
                  />
                </el-col>

                <template v-else>
                  <el-col :span="12" class="pR5">
                    <el-select
                      @change="loadUnit(metricsUnits)"
                      v-model="metricId"
                      filterable
                      :placeholder="$t('common.products.select_metric')"
                      class="fc-input-full-border2 width100"
                    >
                      <el-option
                        v-for="(metric, index) in metricsUnits.metrics"
                        :key="index"
                        :label="metric.name"
                        :value="metric.metricId"
                      ></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="12" class="pL5">
                    <el-select
                      v-model="unitId"
                      :disabled="!metricId"
                      :placeholder="$t('common.products.select_unit')"
                      class="fc-input-full-border2 width100"
                    >
                      <el-option
                        v-for="(unit, index) in hasMetricName"
                        :key="index"
                        :label="unit.displayName + ' (' + unit.symbol + ')'"
                        :value="unit.unitId"
                      ></el-option>
                    </el-select>
                  </el-col>
                </template>
              </el-col>
            </el-row>

            <el-row>
              <el-col class="flex align-center">
                <div class="fc-input-label-txt mT30 pB20 pR30">
                  {{ $t('kpi.kpi.is_v2') }}

                  <el-switch
                    active-color="rgba(57, 178, 194, 0.8)"
                    inactive-color="#e5e5e5"
                    v-model="isV2"
                  >
                  </el-switch>
                </div>
              </el-col>
            </el-row>

            <el-form-item prop="workflow" v-if="!isV2">
              <div class="mT5 line-height20 pR20">
                <f-formula-builder
                  v-model="newkpi.workflow"
                  :isNew="isNew"
                  :assetCategory="{ id: newkpi.assetCategoryId }"
                  module="enpi"
                ></f-formula-builder>
              </div>
            </el-form-item>

            <el-form-item prop="workflow" v-else>
              <div
                class="height250 overflow-y-scroll width90 pB50 script-line-height"
              >
                <CodeMirror
                  :codeeditor="true"
                  v-model="workflowV2String"
                ></CodeMirror>
              </div>
            </el-form-item>

            <!-- <el-row class="mB20">
              <el-col :span="24">
                <div class="mT40 mB20 fbTitle">
                  {{ $t('common._common.safe_limit') }}
                </div>
                <el-radio
                  v-model="safeLimitType"
                  :label="safeLimitTypes.MIN"
                  :value="safeLimitTypes.MIN"
                  class="fc-radio-btn"
                  >{{ $t('common._common.less_then') }}</el-radio
                >

                <el-radio
                  v-model="safeLimitType"
                  :label="safeLimitTypes.MAX"
                  :value="safeLimitTypes.MAX"
                  class="fc-radio-btn"
                  >{{ $t('common._common.greater_than') }}</el-radio
                >

                <el-radio
                  v-model="safeLimitType"
                  :label="safeLimitTypes.BETWEEN"
                  :value="safeLimitTypes.BETWEEN"
                  class="fc-radio-btn"
                  >{{ $t('common._common.between') }}</el-radio
                >

                <el-radio
                  v-model="safeLimitType"
                  :label="safeLimitTypes.NONE"
                  :value="safeLimitTypes.NONE"
                  class="fc-radio-btn"
                  >{{ $t('common.wo_report.none') }}</el-radio
                >
              </el-col>
            </el-row> -->

            <!-- <el-row>
              <el-col v-if="safeLimitType === safeLimitTypes.MIN" :span="12">
                <el-form-item prop="target">
                  <el-input
                    class="fc-input-full-border2"
                    v-model="newkpi.target"
                    type="number"
                    :placeholder="
                      $t('common._common.enter_maximum_target_value')
                    "
                  />
                </el-form-item>
              </el-col>

              <el-col
                v-else-if="safeLimitType === safeLimitTypes.MAX"
                :span="12"
              >
                <el-form-item prop="minTarget">
                  <el-input
                    class="fc-input-full-border2"
                    v-model="newkpi.minTarget"
                    type="number"
                    :placeholder="
                      $t('common._common.enter_minimum_target_value')
                    "
                  />
                </el-form-item>
              </el-col>

              <el-col
                v-else-if="safeLimitType === safeLimitTypes.BETWEEN"
                :span="24"
              >
                <el-col :span="12" class="pR5">
                  <el-form-item prop="minTarget">
                    <el-input
                      class="fc-input-full-border2"
                      v-model="newkpi.minTarget"
                      type="number"
                      :placeholder="
                        $t('common._common.enter_minimum_target_value')
                      "
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12" class="pL5">
                  <el-form-item prop="target">
                    <el-input
                      class="fc-input-full-border2"
                      v-model="newkpi.target"
                      type="number"
                      :placeholder="
                        $t('common._common.enter_maximum_target_value')
                      "
                    />
                  </el-form-item>
                </el-col>
              </el-col>

              <el-col v-else :span="24"></el-col>
            </el-row> -->
          </div>
        </div>

        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            v-if="isV2"
            class="modal-btn-save"
            type="primary"
            @click="submitV2Form()"
            :loading="saving"
            >{{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}</el-button
          >
          <el-button
            v-else
            class="modal-btn-save"
            type="primary"
            @click="submitForm()"
            :loading="saving"
            >{{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}</el-button
          >
        </div>
      </el-form>

      <FLookupFieldWizard
        v-if="canShowLookupWizard"
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="selectedLookupField"
        :siteId="newkpi.siteId"
        :categoryId="newkpi.assetCategoryId"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>

      <AddKpiCategory
        v-if="canShowCategoryDialog"
        :category="{}"
        @onCategoryCreate="refreshCategories"
        @onClose="canShowCategoryDialog = false"
      ></AddKpiCategory>
    </div>
  </el-dialog>
</template>
<script>
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
import AddKpiCategory from './AddKpiCategory'
import FLookupField from '@/forms/FLookupField'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { mapState } from 'vuex'
import { isEmpty, isNull } from '@facilio/utils/validation'
import find from 'lodash/find'
import { getFieldOptions } from 'util/picklist'
import CodeMirror from '@/CodeMirror'
import { API } from '@facilio/api'
import { Lookup } from '@facilio/ui/forms'

const siteField = {
  isDataLoading: true,
  options: [],
  lookupModuleName: 'site',
  field: {
    lookupModule: {
      name: 'site',
      displayName: 'Sites',
    },
  },
  placeHolderText: 'All Sites',
  multiple: false,
  additionalParams: {
    orderBy: 'spaceType',
    orderType: 'asc',
  },
}

const safeLimitTypes = {
  MIN: 0,
  MAX: 1,
  BETWEEN: 2,
  NONE: -99,
}

export default {
  components: {
    Lookup,
    FFormulaBuilder,
    AddKpiCategory,
    FLookupField,
    FLookupFieldWizard,
    CodeMirror,
  },
  props: ['isNew', 'kpi'],
  data() {
    return {
      siteField,
      isV2: false,
      kpiCategory: null,
      metricsUnits: {},
      saving: false,
      formulaFieldUnit: '',
      unit: 'choose',
      metricId: null,
      metricName: '',
      unitId: null,
      kpiType: 'space',
      safeLimitType: safeLimitTypes.MIN,
      newkpi: {
        name: '',
        description: '',
        siteId: null,
        kpiCategoryName: null,
        resourceId: null,
        frequency: null,
        triggerType: 1,
        formulaFieldType: 1,
        resourceType: 1,
        resultDataType: 3,
        workflow: null,
        assetCategoryId: null,
        includedResources: [],
        target: null,
        minTarget: null,
      },
      workflowV2String: null,
      canShowCategoryDialog: false,
      safeLimitTypes,
      canShowLookupWizard: false,
      selectedLookupField: null,
      spaceAssetField: {},

      rules: {
        kpiCategoryName: {
          validator: function(rule, value, callback) {
            if (isEmpty(find(this.kpiCategory, ['label', value]))) {
              callback(
                new Error(
                  this.$t(
                    'common.header.please_select_kpi_category_create_new_category'
                  )
                )
              )
            } else callback()
          }.bind(this),
          trigger: 'change',
        },
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
        workflow: {
          validator: function(rule, value, callback) {
            setTimeout(() => {
              if (isNull(this.newkpi.workflow))
                callback(
                  new Error(this.$t('common._common.formula_cannot_be_ready'))
                )
              else callback()
            }, 500)
          }.bind(this),
          trigger: 'blur',
        },
        target: {
          validator: function(rule, value, callback) {
            if (
              this.safeLimitType === this.safeLimitTypes.MIN ||
              this.safeLimitType === this.safeLimitTypes.BETWEEN
            ) {
              if (isEmpty(this.newkpi.target)) {
                callback(
                  new Error(this.$t('common._common.fields_cannot_be_empty'))
                )
              } else callback()
            }
          }.bind(this),
          trigger: 'change',
        },
        minTarget: {
          validator: function(rule, value, callback) {
            if (
              this.safeLimitType === this.safeLimitTypes.MAX ||
              this.safeLimitType === this.safeLimitTypes.BETWEEN
            ) {
              if (isEmpty(this.newkpi.minTarget)) {
                callback(
                  new Error(this.$t('common._common.fields_cannot_be_empty'))
                )
              } else callback()
            }
          }.bind(this),
          trigger: 'change',
        },
        frequency: {
          required: true,
          message: this.$t('common.header.please_select_frequency'),
          trigger: 'blur',
        },
        resourceId: {
          validator: function(rule, value, callback) {
            if (this.kpiType === 'space') {
              if (isEmpty(this.newkpi.resourceId)) {
                callback(
                  new Error(this.$t('common.header.please_select_a_space'))
                )
              } else callback()
            }
          }.bind(this),
          trigger: 'change',
        },
        assetCategoryId: {
          validator: function(rule, value, callback) {
            if (this.kpiType === 'asset') {
              if (isEmpty(this.newkpi.assetCategoryId)) {
                callback(
                  new Error(this.$t('common.header.please_select_an_asset'))
                )
              } else callback()
            }
          }.bind(this),
          trigger: 'blur',
        },
      },
    }
  },

  async created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
    let { error, options } = await getFieldOptions({
      field: { lookupModuleName: 'kpiCategory' },
    })

    if (error) {
      this.$message.error(error.message || 'Error Occured')
    } else {
      this.kpiCategory = options
      this.init()
    }
    this.loadDefaultMetricUnits()
  },

  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),

    kpiCategoryId: {
      get() {
        let { kpiCategory } = this
        if (this.newkpi.kpiCategoryName) {
          let selectedCategory = find(kpiCategory, [
            'label',
            this.newkpi.kpiCategoryName,
          ])

          return selectedCategory.value
        }
        return -1
      },
      set(id) {
        let { kpiCategory } = this
        let selectedCategory = find(kpiCategory, ['value', id]) || []
        this.newkpi.kpiCategoryName = selectedCategory.label
        this.newkpi.kpiCategoryId = selectedCategory.value
      },
    },

    frequencyTypes() {
      let types = {
        1: 'Daily',
        2: 'Weekly',
        3: 'Monthly',
        6: 'Annually',
        8: 'Hourly',
      }
      return types
    },

    hasMetricName() {
      if (
        !isEmpty(this.metricName) &&
        !isEmpty(this.metricsUnits.metricWithUnits)
      )
        return this.metricsUnits.metricWithUnits[this.metricName]
      else return []
    },
  },

  watch: {
    'newkpi.siteId': function() {
      this.newkpi.includedResources = []
    },

    'newkpi.assetCategoryId': {
      handler(value) {
        if (!isEmpty(value)) {
          let { name: categoryName } = this.assetCategory.find(
            d => d.id === this.newkpi.assetCategoryId
          )
          this.spaceAssetField.placeHolderText = `All ${categoryName}s selected`
          this.newkpi.includedResources = []
        }
      },
    },

    kpiType: {
      handler(value) {
        let fieldObj = {
          lookupModuleName: value,
          filters: null,
          lookupModule: { displayName: value },
        }

        if (value === 'asset') {
          fieldObj.multiple = true
          this.newkpi.resourceId = null
        } else {
          this.newkpi.assetCategoryId = null
          this.newkpi.includedResources = []
        }
        this.spaceAssetField = fieldObj
      },
      immediate: true,
    },
  },

  methods: {
    init() {
      if (!this.isNew) {
        this.newkpi = { ...this.newkpi, ...this.$helpers.cloneObject(this.kpi) }
        if (this.newkpi.kpiCategory > 0) {
          this.kpiCategoryId = this.newkpi.kpiCategory
        }

        if (
          this.kpi.matchedResources &&
          this.kpi.matchedResources.length !== 0
        ) {
          this.kpiType =
            this.kpi.matchedResources[0].resourceType === 1 ? 'space' : 'asset'
        }

        let { unit, unitId, metric, metricEnum } = this.kpi.readingField
        this.formulaFieldUnit = isEmpty(unitId) ? unit : null
        this.unitId = !isEmpty(unitId) ? unitId : null
        this.metricId = !isEmpty(metric) ? metric : null
        this.metricName = metricEnum
        !isEmpty(this.formulaFieldUnit) ? (this.unit = 'custom') : ''

        let { minTarget, target } = this.newkpi
        minTarget = !isEmpty(minTarget) ? minTarget : null
        target = !isEmpty(target) ? target : null
        if (isEmpty(minTarget) && isEmpty(target))
          this.safeLimitType = safeLimitTypes.NONE
        else {
          if (!isEmpty(minTarget)) this.safeLimitType = safeLimitTypes.MAX
          else if (!isEmpty(target)) this.safeLimitType = safeLimitTypes.MIN
          else this.safeLimitType = safeLimitTypes.BETWEEN
        }
        this.$set(this.newkpi, 'minTarget', minTarget)
        this.$set(this.newkpi, 'target', target)

        let workflowScript = this.$getProperty(
          this,
          'kpi.workflow.workflowV2String'
        )
        if (this.kpi.workflow.isV2Script) {
          this.isV2 = true
        }
        if (!isEmpty(workflowScript)) {
          this.workflowV2String = workflowScript
          this.$set(this.newkpi.workflow, 'workflowV2String', workflowScript)
        }

        if (this.kpi.assetCategoryId === -1) {
          this.newkpi.assetCategoryId = null
        }
      }
    },

    showLookupWizard(field, canShow) {
      this.selectedLookupField = field
      this.canShowLookupWizard = canShow
    },

    setLookupFieldValue(props) {
      let { field } = props
      let { options = [], selectedItems = [], lookupModuleName } = field
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
      this.selectedLookupField = {}
      this.$set(this.spaceAssetField, 'options', options)

      if (lookupModuleName === 'space') {
        this.newkpi.resourceId = selectedItemIds[0]
      } else {
        this.newkpi.includedResources = selectedItemIds
      }
    },

    showAddCategoryDialog() {
      this.canShowCategoryDialog = true
    },

    async refreshCategories(categoryId) {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'kpiCategory' },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.kpiCategory = options
        this.kpiCategoryId = categoryId
        this.canShowCategoryDialog = false
      }
    },

    submitForm() {
      this.$refs['categoryForm'].validate(valid => {
        if (!valid) return false

        let url = '/v2/kpi/add'

        let {
          name,
          description,
          id,
          workflow,
          target,
          minTarget,
          formulaFieldUnit,
        } = this.$helpers.cloneObject(this.newkpi)

        let kpiObj = {
          name,
          description: !isEmpty(description) ? description : '',
          id,
          workflow,
          target,
          minTarget,
          formulaFieldUnit,
        }

        if (this.newkpi.siteId === null) {
          this.newkpi.siteId = -1
        }

        if (this.isNew) {
          kpiObj = this.$helpers.cloneObject(this.newkpi)
          if (kpiObj.assetCategoryId > 0) {
            kpiObj.resourceType = 6
            delete kpiObj.resourceId
          } else {
            kpiObj.resourceType = 1
            delete kpiObj.assetCategoryId
          }
          kpiObj.interval = 60
        }

        if (this.safeLimitType === safeLimitTypes.MIN) delete kpiObj.minTarget
        else if (this.safeLimitType === safeLimitTypes.MAX) delete kpiObj.target
        else if (this.safeLimitType === safeLimitTypes.NONE) {
          delete kpiObj.target
          delete kpiObj.minTarget
        }

        let param = {
          formula: { ...kpiObj, kpiCategory: this.kpiCategoryId },
          formulaFieldUnit: this.unit === 'custom' ? this.formulaFieldUnit : '',
          metricId: this.unit === 'choose' ? this.metricId : null,
          unitId: this.unit === 'choose' ? this.unitId : null,
        }

        if (this.unit === 'custom') {
          delete param.metricId
          delete param.unitId
        } else {
          delete param.formulaFieldUnit
        }
        this.$set(param.formula.workflow, 'isV2Script', false)

        if (!this.isNew) {
          url = '/v2/kpi/update'

          kpiObj = this.$helpers.compareObject(kpiObj, this.kpi)
          kpiObj.id = this.kpi.id

          delete param.formula.kpiCategory

          param = {
            ...param,
            moduleId: this.kpi.moduleId,
          }
        }

        let showError = () => {
          this.$message.error(
            this.isNew
              ? this.$t('common.header.kpi_addition_failed')
              : this.$t('common.header.kpi_updation_failed')
          )
        }

        this.saving = true
        this.$http
          .post(url, param)
          .then(response => {
            this.saving = false

            if (response.data.responseCode === 0) {
              this.$message.success(
                this.isNew
                  ? this.$t('common.products.new_kpi_added_successfully')
                  : this.$t('common.header.kpi_updated_successfully')
              )
              this.$emit('onSave', response.data.result.formulaField || kpiObj)
              this.$emit('onClose')
            } else {
              showError()
            }
          })
          .catch(() => {
            this.saving = false
            showError()
          })
      })
    },

    async submitV2Form() {
      this.$refs['categoryForm'].validate(async valid => {
        if (!valid) return false

        let url = '/v2/kpi/add'

        let {
          name,
          description,
          id,
          workflow,
          target,
          minTarget,
          formulaFieldUnit,
        } = this.$helpers.cloneObject(this.newkpi)

        let kpiObj = {
          name,
          description: !isEmpty(description) ? description : '',
          id,
          workflow,
          target,
          minTarget,
          formulaFieldUnit,
        }

        if (this.isNew) {
          kpiObj = this.$helpers.cloneObject(this.newkpi)
          if (kpiObj.assetCategoryId > 0) {
            kpiObj.resourceType = 6
            delete kpiObj.resourceId
          } else {
            kpiObj.resourceType = 1
            delete kpiObj.assetCategoryId
          }
          kpiObj.interval = 60
        }

        if (this.safeLimitType === safeLimitTypes.MIN) delete kpiObj.minTarget
        else if (this.safeLimitType === safeLimitTypes.MAX) delete kpiObj.target
        else if (this.safeLimitType === safeLimitTypes.NONE) {
          delete kpiObj.target
          delete kpiObj.minTarget
        }

        let param = {
          formula: { ...kpiObj, kpiCategory: this.kpiCategoryId },
          formulaFieldUnit: this.unit === 'custom' ? this.formulaFieldUnit : '',
          metricId: this.unit === 'choose' ? this.metricId : null,
          unitId: this.unit === 'choose' ? this.unitId : null,
        }

        if (this.unit === 'custom') {
          delete param.metricId
          delete param.unitId
        } else {
          delete param.formulaFieldUnit
        }

        if (!this.isNew) {
          url = '/v2/kpi/update'

          kpiObj = this.$helpers.compareObject(kpiObj, this.kpi)
          kpiObj.id = this.kpi.id

          delete param.formula.kpiCategory

          param = {
            ...param,
            moduleId: this.kpi.moduleId,
          }
        }
        this.$set(
          param.formula.workflow,
          'workflowV2String',
          this.workflowV2String
        )
        this.$set(param.formula.workflow, 'isV2Script', true)

        this.saving = true
        let { data, error } = await API.post(url, param)
        this.saving = false
        if (error) {
          this.$message.error(error.message || 'Error Occurred')
        } else {
          this.$message.success(
            this.isNew
              ? this.$t('common.products.new_kpi_added_successfully')
              : this.$t('common.header.kpi_updated_successfully')
          )
          this.$emit('onSave', data.formulaField || kpiObj)
          this.$emit('onClose')
        }
      })
    },

    closeDialog() {
      this.$emit('onClose')
    },

    loadDefaultMetricUnits() {
      this.$http.get('/units/getDefaultMetricUnits').then(response => {
        this.metricsUnits = response.data
      })
    },
    loadUnit(metricsUnits) {
      this.metricName = metricsUnits.metrics.find(
        metric => this.metricId === metric.metricId
      )
      this.metricName = this.metricName._name

      this.unitId = metricsUnits.orgUnitsList.find(
        metric => metric.metric === this.metricId
      )
      this.unitId = !isEmpty(this.unitId) ? this.unitId.unit : null
    },
  },
}
</script>
<style lang="scss">
.kpi-modal {
  .category-autocomplete {
    .el-form-item__content {
      display: flex;
      .el-autocomplete {
        flex-grow: 1;
      }
    }
  }
  .resource-field {
    .el-input__prefix {
      left: 83%;
      z-index: 10;
    }
  }
}
</style>
<style>
.kpi-modal .textcolor {
  color: #6b7e91;
}
.kpi-modal .ruletitle {
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.8px;
  color: #ef4f8f;
}
.kpi-modal .header.text {
  font-size: 18px;
  text-align: left;
  letter-spacing: 0.6px;
}

.kpi-modal .header .el-textarea__inner {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #666666;
}
.kpi-modal .el-input.is-disabled .el-input__inner {
  color: black;
  background-color: white !important;
}
.kpi-modal .header.el-input .el-input__inner,
.kpi-modal .header.el-textarea .el-textarea__inner {
  border-bottom: none;
  resize: none;
}
.kpi-modal .primarybutton.el-button {
  background-color: #39b2c2 !important;
  border-color: #39b2c2 !important;
  color: #ffffff !important;
  float: right;
}
.kpi-modal .fc-form .form-header,
.kpi-modal .fc-form-container .form-header {
  font-weight: normal;
  font-size: 16px;
  text-align: left;
}
.kpi-modal .fc-form .form-input {
  padding: 0px;
}
.kpi-modal .column-item {
  padding: 10px;
  border: 1px solid #f2f2f2;
  cursor: move;
  margin-top: 5px;
}
.kpi-modal.el-button:focus,
.kpi-modal .el-button:hover {
  background-color: #ecf5ff;
}
.fc-create-record {
  width: 50% !important;
}
.body-scroll {
  width: 100%;
  overflow-y: scroll;
  display: inline-block;
  padding-bottom: 30px;
}
.kpi-body-modal {
  height: calc(100vh - 100px) !important;
}
.kpi-icon-search {
  line-height: 0px !important;
  font-size: 16px !important;
  cursor: pointer;
  position: absolute;
  top: 0px;
  right: 11%;
}
</style>

<style lang="scss" scoped>
.script-line-height {
  line-height: 20px !important;
  border: solid 1px #d0d9e2;
}
</style>
