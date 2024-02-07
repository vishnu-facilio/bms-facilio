<template>
  <div>
    <div id="kpidetails-header" class="section-header">
      {{ $t('kpi.kpi.kpi_details') }}
    </div>
    <el-form
      ref="kpiDetailsForm"
      :model="kpiDetailsObj"
      :rules="validationRules"
      label-width="150px"
      label-position="left"
      class="p50 pT10 pB30"
    >
      <div class="section-container flex-container">
        <div
          class="reading-kpi-form-item reading-kpi-form-item fc-label-required"
        >
          <el-form-item :label="`${this.$t('common.roles.name')}`" prop="name">
            <el-input
              v-model="kpiDetailsObj.name"
              class="fc-input-full-border2"
              :placeholder="`${this.$t('common.roles.name')}`"
              :autofocus="true"
            ></el-input>
          </el-form-item>
        </div>
        <div class="reading-kpi-form-item">
          <el-form-item
            :label="`${this.$t('common.roles.description')}`"
            prop="description"
          >
            <el-input
              v-model="kpiDetailsObj.description"
              type="textarea"
              class="mT3 fc-input-full-border-textarea"
              :autofocus="true"
              :min-rows="2"
              :autosize="{ minRows: 3, maxRows: 4 }"
              :placeholder="`${this.$t('common.roles.description')}`"
            ></el-input>
          </el-form-item>
        </div>
        <div class="reading-kpi-form-item  fc-label-required">
          <el-form-item
            :label="`${this.$t('kpi.kpi.kpi_category')}`"
            prop="kpiCategory"
          >
            <el-select
              data-test-selector="kpiCategoryId"
              ref="kpiCategoryId"
              key="kpiCategoryId"
              v-model="kpiDetailsObj.kpiCategory"
              filterable
              collapse-tags
              :placeholder="$t('common.products.select_category')"
              class="fc-input-full-border-select2 width100 fc-tag"
              remote
              :remote-method="remoteMethodKpiCatRequest"
            >
              <div slot="empty" class="min-width730">
                <div class="el-select-dropdown__empty">
                  {{ $t('common._common.empty_option_text') }}
                </div>
                <div class="kpi-form-item">
                  <el-button
                    @click="addKpiCategory()"
                    class="add-kpi-category-container"
                  >
                    <i class="el-icon-plus plus-icon"></i>
                    {{ $t('common.header.new_category') }}</el-button
                  >
                </div>
              </div>
              <el-option
                v-for="(option, index) in kpiCategoryOptions"
                :key="index"
                :label="option.label"
                :value="option.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
        <div class="reading-kpi-form-item">
          <el-form-item :label="`${this.$t('common.products.site')}`">
            <FLookupField
              :key="`kpi-site`"
              :model.sync="kpiDetailsObj.siteId"
              :field="siteField"
              :isRemoteField="false"
            ></FLookupField>
          </el-form-item>
        </div>
        <div class="reading-kpi-form-item">
          <el-form-item>
            <el-radio-group v-model="kpiDetailsObj.resourceType">
              <el-radio :label="4" class="fc-radio-btn">{{
                $t('common.space_asset_chooser.asset')
              }}</el-radio>
              <el-radio :label="3" class="fc-radio-btn">{{
                $t('common.space_asset_chooser.space')
              }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>
        <div
          v-if="kpiDetailsObj.resourceType === 3"
          class="reading-kpi-form-item"
        >
          <div class="reading-kpi-form-item">
            <el-form-item
              :label="`${this.$t('maintenance.pm_list.space_category')}`"
              prop="spaceCategory"
            >
              <FLookupField
                :key="`kpi-spacecategory`"
                :model.sync="kpiDetailsObj.spaceCategory"
                :field="spaceCategoryField"
                :isRemoteField="false"
                :disabled="!isNew"
                @recordSelected="onSpaceCategorySelected"
              ></FLookupField>
            </el-form-item>
            <div v-if="!isSpaceCategoryEmpty" class="reading-kpi-form-item ">
              <el-form-item>
                <el-radio-group v-model="bulkSelectOption">
                  <el-radio label="all" class="fc-radio-btn">{{
                    $t('space.sites.all_spaces')
                  }}</el-radio>
                  <el-radio label="include" class="fc-radio-btn">{{
                    $t('space.sites.specific_spaces')
                  }}</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
            <div v-else class="reading-kpi-form-item fc-label-required">
              <el-form-item
                :label="`${this.$t('common.wo_report.spaces')}`"
                prop="spaces"
              >
                <FLookupField
                  :key="`kpi-site`"
                  :model.sync="kpiDetailsObj.spaces"
                  :field="spaceField"
                  :isRemoteField="false"
                ></FLookupField>
              </el-form-item>
            </div>

            <div v-if="!isSpaceCategoryEmpty" class="reading-kpi-form-item">
              <el-form-item :label="`${this.$t('common.wo_report.spaces')}`">
                <div v-if="isAllAssetsSelected">
                  <el-input
                    class="fc-input-full-border2"
                    :placeholder="allSpacesPlaceHolderText"
                    :disabled="true"
                  ></el-input>
                </div>
                <FLookupField
                  v-else
                  :key="`spaces-lookup-field`"
                  ref="spaces-lookup-field"
                  :model.sync="kpiDetailsObj.spaces"
                  :field="spaceField"
                  :preHookFilterConstruction="constructSpaceCategoryFilter"
                  @showLookupWizard="showLookupWizard"
                ></FLookupField>
              </el-form-item>
            </div>
          </div>
        </div>
        <div v-else class="reading-kpi-form-item">
          <div class="reading-kpi-form-item  fc-label-required">
            <el-form-item
              :label="`${this.$t('alarm.alarm.asset_category')}`"
              prop="assetCategoryId"
            >
              <FLookupField
                :key="`kpi-assetcategory`"
                :model.sync="kpiDetailsObj.assetCategoryId"
                :field="assetCategoryField"
                :isRemoteField="false"
                :disabled="!isNew"
                @recordSelected="onAssetCategorySelected"
              ></FLookupField>
            </el-form-item>
          </div>
          <div v-if="!isAssetCategoryEmpty" class="reading-kpi-form-item">
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
          <div class="reading-kpi-form-item">
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
                :model.sync="kpiDetailsObj.assets"
                :field="assetField"
                :preHookFilterConstruction="constructAssetCategoryFilter"
                @showLookupWizard="showLookupWizard"
              ></FLookupField>
            </el-form-item>
          </div>
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
        :categoryId="kpiDetailsObj.assetCategoryId"
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
import { getFieldOptions } from 'util/picklist'
import { API } from '@facilio/api'
import debounce from 'lodash/debounce'
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
    kpiInfo: {
      type: Object,
    },
  },
  data() {
    return {
      kpiDetailsObj: {
        name: '',
        description: '',
        kpiCategory: null,
        siteId: null,
        resourceType: 4, //enum index
        assetCategoryId: null,
        spaceCategory: null,
        asset: null,
        assets: null,
        space: null,
        spaces: null,
      },
      showAddOptionKpiCat: false,
      kpiCatSearchText: null,
      kpiCategoryOptions: null,
      validationRules: {
        name: [
          {
            required: true,
            message: 'Please enter KPI name',
            trigger: 'blur',
          },
        ],

        kpiCategory: [
          {
            required: true,
            message: 'Please select KPI category',
            trigger: 'blur',
          },
        ],

        assetCategoryId: [
          {
            required: true,
            message: 'Please select asset category',
            trigger: 'blur',
          },
        ],
      },

      siteField: {
        lookupModuleName: 'site',
        lookupModule: {
          type: -1,
        },
        options: [],
        placeHolderText: `${this.$t('setup.setup.select_site')}`,
        isDataLoading: false,
      },
      assetCategoryField: {
        lookupModuleName: 'assetCategory',
        lookupModule: {
          type: -1,
        },
        options: [],
        isDataLoading: false,
        placeHolderText: `${this.$t('kpi.kpi.select_asset_category')}`,
      },
      spaceCategoryField: {
        lookupModuleName: 'spacecategory',
        lookupModule: {
          type: -1,
        },
        options: [],
        isDataLoading: false,
        placeHolderText: `${this.$t('kpi.kpi.select_space_category')}`,
      },
      spaceField: {
        lookupModuleName: 'space',
        field: {
          lookupModule: {
            name: 'space',
            displayName: 'Space',
          },
        },
        options: [],
        isDataLoading: false,
        multiple: true,
        placeHolderText: `${this.$t('rule.create.select_space')}`,
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
      canShowKpiCategoryDialog: false,
      selectedLookupField: null,
      bulkSelectOption: 'all',
      selectedAssetCategoryLabel: '',
      selectedSpaceCategoryLabel: '',
      isMatchedSpaceSelection: false,
      isMatchedAssetSelection: false,
    }
  },
  created() {
    this.getKpiCategoryOptions()
    if (!this.isNew) this.prefillKpiDetails()
    this.remoteMethodKpiCatRequest = debounce(this.getKpiCatRemote, 500)
  },
  computed: {
    isAssetCategoryEmpty() {
      let { kpiDetailsObj } = this
      let { assetCategoryId } = kpiDetailsObj || {}
      return isEmpty(assetCategoryId)
    },
    isSpaceCategoryEmpty() {
      let { kpiDetailsObj } = this
      let { spaceCategory } = kpiDetailsObj || {}
      return isEmpty(spaceCategory)
    },
    isAllAssetsSelected() {
      let { bulkSelectOption } = this
      return bulkSelectOption === 'all'
    },
    allSpacesPlaceHolderText() {
      let { selectedSpaceCategoryLabel } = this
      return `All ${selectedSpaceCategoryLabel} selected`
    },
    allAssetsPlaceHolderText() {
      let { selectedAssetCategoryLabel } = this
      return `All ${selectedAssetCategoryLabel} selected`
    },
    isNewLookupWizardEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_LOOKUP_WIZARD')
    },
  },
  watch: {
    kpiDetailsObj: {
      async handler(newVal, oldVal) {
        if (!isEmpty(newVal)) {
          this.$emit('kpiDetailsChange', newVal)
        }
        let {
          assetCategoryId: assetCategoryIdNew,
          spaceCategoryId: spaceCategoryIdNew,
        } = newVal || {}
        let {
          assetCategoryId: assetCategoryIdOld,
          spaceCategoryId: spaceCategoryIdOld,
        } = oldVal || {}
        if (!isEqual(assetCategoryIdNew, assetCategoryIdOld)) {
          let {
            $refs,
            assetField,
            kpiDetailsObj,
            isMatchedAssetSelection,
          } = this
          let elem = $refs['assets-lookup-field']
          if (!isEmpty(elem) && !isMatchedAssetSelection) {
            let options = (await elem.getOptions({ initialFetch: true })) || []
            this.$set(kpiDetailsObj, 'assets', null)
            this.$set(assetField, 'options', options)
          }
          this.isMatchedAssetSelection = false
        }

        if (!isEqual(spaceCategoryIdNew, spaceCategoryIdOld)) {
          let {
            $refs,
            spaceField,
            kpiDetailsObj,
            isMatchedSpaceSelection,
          } = this
          let elem = $refs['spaces-lookup-field']
          if (!isEmpty(elem) && !isMatchedSpaceSelection) {
            let options = (await elem.getOptions({ initialFetch: true })) || []
            this.$set(kpiDetailsObj, 'spaces', null)
            this.$set(spaceField, 'options', options)
          }
          this.isMatchedSpaceSelection = false
        }
      },
      deep: true,
    },
    isAllAssetsSelected: {
      handler(value) {
        if (value) this.$set(this.kpiDetailsObj, 'assets', null)
      },
    },
  },
  methods: {
    async getKpiCatRemote(searchText) {
      let query = {
        field: { lookupModuleName: 'kpiCategory' },
      }
      if (!isEmpty(searchText)) {
        query.searchText = searchText
      } else if (this.showAddOptionKpiCat) {
        this.showAddOptionKpiCat = false
      }
      let { options, error } = await getFieldOptions(query)
      if (isEmpty(error)) {
        this.kpiCategoryOptions = options
        if (isEmpty(options)) {
          this.kpiCatSearchText = searchText
          this.showAddOptionKpiCat = true
        }
      } else {
        this.$message.error(error.message || 'Error occured')
      }
    },
    async getKpiCategoryOptions() {
      let { options } = await getFieldOptions({
        field: { lookupModuleName: 'kpiCategory' },
      })
      this.kpiCategoryOptions = options
    },
    async prefillKpiDetails() {
      this.getKpiCategoryOptions()
      let { kpiDetailsObj, kpiInfo } = this
      if (!isEmpty(kpiInfo)) {
        let { name, description, kpiCategory, siteId, assets } = kpiInfo || {}
        this.kpiDetailsObj = {
          name: name,
          description: description,
          kpiCategory: kpiCategory,
          siteId: siteId,
          assets: assets,
        }
        let { resourceType, assetCategory } = kpiInfo || {}
        let { displayName } = assetCategory || {}
        this.selectedAssetCategoryLabel = displayName
        this.$set(this.kpiDetailsObj, 'resourceType', resourceType)

        let { id } = assetCategory
        this.kpiDetailsObj.assetCategoryId = id

        if (!isEmpty(assets)) {
          this.bulkSelectOption = 'include'
          this.isMatchedAssetSelection = true
          this.$set(kpiDetailsObj, 'assets', assets)
        }
      }
    },
    showLookupWizard(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizard', canShow)
    },
    setLookupFieldValue(props) {
      let { kpiDetailsObj } = this
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
      this.$set(kpiDetailsObj, `assets`, selectedItemIds)
      this.$set(this.selectedLookupField, 'options', options)
    },
    constructAssetCategoryFilter() {
      let { kpiDetailsObj } = this
      let { assetCategoryId } = kpiDetailsObj || {}
      let filters = {}
      if (!isEmpty(assetCategoryId)) {
        filters = {
          category: {
            operator: 'is',
            value: [`${assetCategoryId}`],
          },
        }
      }
      return filters
    },
    constructSpaceCategoryFilter() {
      let { kpiDetailsObj } = this
      let { spaceCategory } = kpiDetailsObj || {}
      let filters = {}
      if (!isEmpty(spaceCategory)) {
        filters = {
          spaceCategory: {
            operator: 'is',
            value: [`${spaceCategory}`],
          },
        }
      }
      return filters
    },
    onSpaceCategorySelected(selectedValue) {
      let { label } = selectedValue || {}
      let { spaceField, kpiDetailsObj } = this
      let { selectedItems } = spaceField || {}
      if (!isEmpty(label)) {
        this.selectedSpaceCategoryLabel = label
      }
      if (!isEmpty(selectedItems)) {
        spaceField.selectedItems = []
        this.$set(kpiDetailsObj, 'spaces', null)
      }
    },
    onAssetCategorySelected(selectedValue) {
      let { label } = selectedValue || {}
      let { assetField, kpiDetailsObj } = this
      let { selectedItems } = assetField || {}
      if (!isEmpty(label)) {
        this.selectedAssetCategoryLabel = label
      }
      if (!isEmpty(selectedItems)) {
        assetField.selectedItems = []
        this.$set(kpiDetailsObj, 'assets', null)
      }
    },
    async addKpiCategory() {
      let params = {
        kpiCategory: {
          name: this.kpiCatSearchText,
        },
      }

      let { error, data } = await API.post('v2/kpi/category/add', params)
      if (isEmpty(error)) {
        await this.refreshCategories(data.kpiCategoryContext.id)
        this.showAddOptionKpiCat = false
      } else {
        this.$message.error(
          error.message ||
            this.$t('common._common.error_while_creatingkpi_category')
        )
      }
    },
    async refreshCategories(categoryId) {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'kpiCategory' },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$set(this, 'kpiCategoryOptions', options)
        this.$set(
          this.kpiDetailsObj,
          'kpiCategory',
          categoryId !== 0 ? categoryId : null
        )
        this.canShowKpiCategoryDialog = false
      }
    },
  },
}
</script>

<style scoped lang="scss">
.kpi-form-item {
  flex: 1 1 100%;
  width: 100%;
  border-top: 1px solid #c7c1c14f;
}
.add-kpi-category-container {
  border: none;
  width: 100%;
  text-align: left;
  color: #3ab2c2;
  font-weight: 400;
}
.plus-icon {
  font-weight: bold;
}
</style>
<style lang="scss">
.reading-kpi-form-item {
  flex: 1 1 100%;
  width: 100%;
  .el-form-item {
    display: flex;
    flex-direction: column;
  }
  .el-form-item__content {
    margin-left: 0px !important;
  }
}
.min-width730 {
  min-width: 730px;
}
</style>
