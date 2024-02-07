<template>
  <el-dialog
    :visible="openDialog"
    :fullscreen="true"
    :append-to-body="true"
    :before-close="closeCategoryDialog"
    custom-class="new-category-container fc-dialog-form fc-dialog-right custom-rule-dialog setup-dialog60 setup-dialog"
    style="z-index: 1999"
  >
    <div class="mL30 mT20 new-header-modal new-header-text f22 ">
      <div class="setup-modal-title">
        {{ $t('common.header.new_category') }}
      </div>
    </div>
    <div
      v-if="isLoading"
      class="flex-middle fc-empty-white m10 fc-agent-empty-state"
    >
      <spinner :show="isLoading" size="80"></spinner>
    </div>

    <div
      v-else-if="!$validation.isEmpty(selectedGroup)"
      class="new-body-modal pL30 pR30"
    >
      <div class="f16 font-bold pT5 pB5">{{ selectedGroup.typeName }}</div>
      <div class="pT8 pB20">Type: {{ selectedGroup.type.label }}</div>
      <div
        v-if="category"
        class="rule-border-blue p20 pR10 position-relative"
        style="border-left: 1px solid rgb(228, 235, 241);"
      >
        <el-form
          :model="category"
          ref="form"
          :rules="rules"
          label-width="125px"
          label-position="left"
        >
          <el-row>
            <el-col>
              <el-form-item label="Category" class="mB25" prop="type">
                <el-select
                  v-model="category.type"
                  class="fc-input-full-border2 width70"
                  placeholder="Select a type"
                >
                  <el-option
                    v-for="option in assetCategory"
                    :key="option.id"
                    :label="option.displayName"
                    :value="option.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col>
              <el-form-item
                label="Assets"
                prop="assets"
                class="flookup-field-category mB25"
              >
                <FLookupField
                  class="width70"
                  ref="assets-lookup-field"
                  :model.sync="category.assets"
                  :disabled="$validation.isEmpty(category.type)"
                  :fetchOptionsOnLoad="true"
                  :canShowLookupWizard="showLookupFieldWizard"
                  :field="fieldObj"
                  :hideDropDown="true"
                  :isClearable="true"
                  @showLookupWizard="showLookupWizard"
                  @setLookupFieldValue="setLookupFieldValue"
                  :preHookFilterConstruction="constructCategoryFilter"
                ></FLookupField>
              </el-form-item>
            </el-col>
          </el-row>

          <NewControlPoints
            :category.sync="category"
            :controlPointsList="controlPointsList"
          />
        </el-form>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <div style="margin-bottom:0px">
        <el-button
          class="modal-btn-cancel text-uppercase"
          @click="closeCategoryDialog"
          >{{ $t('common._common.cancel') }}</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="save"
          :loading="saving"
        >
          {{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}
        </el-button>
      </div>
    </div>
    <FLookupFieldWizard
      v-if="showLookupFieldWizard"
      :canShowLookupWizard.sync="showLookupFieldWizard"
      :selectedLookupField="selectedLookupField"
      :withReadings="true"
      @setLookupFieldValue="setLookupFieldValue"
      :categoryId="category.type"
    ></FLookupFieldWizard>
  </el-dialog>
</template>

<script>
import clone from 'lodash/clone'
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty, isObject } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import NewControlPoints from './NewControlPoints'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import FLookupField from '@/forms/FLookupField'
export default {
  props: [
    'openDialog',
    'closeDialog',
    'isEdit',
    'selectedGroup',
    'selectedCategory',
  ],
  components: { NewControlPoints, FLookupFieldWizard, FLookupField },
  data() {
    return {
      category: {
        type: null,
        assets: [],
        controlPoints: [],
        assetList: [],
      },
      isAssetsLoading: false,
      assetsList: [],
      saving: false,
      assetCategory: [],
      loadPoints: false,
      controlPointsList: [],
      isLoading: false,
      rules: {
        type: [
          {
            required: true,
            message: 'Please select a category',
            trigger: 'change',
          },
        ],
        assets: [
          {
            required: true,
            message: 'Please select assets',
            trigger: 'change',
          },
        ],
        controlPoints: [
          {
            required: true,
            message: 'Please select atleast one control point',
            trigger: 'change',
          },
        ],
      },
      fieldObj: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'asset',
        field: {
          lookupModule: {
            name: 'asset',
            displayName: 'Assets',
          },
        },
        forceFetchAlways: true,
        filters: {},
        isDisabled: true,
        multiple: true,
        showFilters: false,
      },
      showLookupFieldWizard: false,
      selectedLookupField: null,
    }
  },
  async created() {
    this.isLoading = true
    await this.fetchTypeVsCategory()
    if (this.isEdit) {
      let {
        type: { id },
        assets,
      } = this.selectedCategory
      let formattedAssets = assets.map(asset => {
        let { asset: assetDetails } = asset
        if (!isEmpty(assetDetails)) {
          let { name, id } = assetDetails

          return { label: name, value: id }
        } else return asset
      })

      this.category = {
        ...this.selectedCategory,
        type: id,
        assets: formattedAssets,
      }
      await this.getPoints()
    } else {
      let { controlPoints } = this.category
      if (isEmpty(controlPoints))
        controlPoints.push(clone(this.controlPointObject))
    }
    this.isLoading = false
  },
  computed: {
    controlPointObject() {
      let { controlPoints } = this.category
      return {
        id: isEmpty(controlPoints) ? 1 : controlPoints.length - 1,
        point: '',
        trueVal: null,
        falseVal: null,
      }
    },
    categoryObj() {
      return {
        type: null,
        assets: [],
        controlPoints: [clone(this.controlPointObject)],
        assetList: [],
      }
    },
  },
  methods: {
    constructCategoryFilter() {
      let { type } = this.category
      let filters = {}
      if (!isEmpty(type)) {
        filters = {
          category: {
            operator: 'is',
            value: [`${type}`],
          },
        }
      }
      return filters
    },
    showLookupWizard(field, canShow) {
      this.selectedLookupField = field
      this.showLookupFieldWizard = canShow
    },
    setLookupFieldValue(props) {
      let { field } = props
      let { selectedItems = [] } = field
      let selectedItemIds = []
      if (!isEmpty(selectedItems)) {
        selectedItemIds = selectedItems.map(item => item.value)
      }

      this.category.assets = selectedItemIds
      this.category.assetList = selectedItems
      this.$set(this, 'selectedLookupField', {})

      this.getPoints()
    },
    async fetchTypeVsCategory() {
      let { type } = this.selectedGroup
      let { data, error } = await API.get('/v3/control/getCategoryForType', {
        type: type.value,
      })
      if (!isEmpty(error)) this.$message.error(error.message || 'Error Occured')
      else {
        let { categories } = data
        this.assetCategory = categories
      }
    },
    async getPoints() {
      let {
        category: { type },
      } = this
      this.controlPointsList = []
      this.$http
        .get(
          `v2/readings/assetcategory?id=${
            isObject(type) ? type.id : type
          }&excludeEmptyFields=true&fetchControllableFields=true`
        )
        .then(response => {
          this.controlPointsList = this.$getProperty(
            response,
            'data.result.readings'
          )
        })
    },
    save() {
      this.$refs['form'].validate(async valid => {
        let { controlPoints } = this.category
        let isPointsEmpty = false
        controlPoints.forEach(point => {
          let { trueVal, falseVal } = point
          if (isEmpty(trueVal) || isEmpty(falseVal)) {
            isPointsEmpty = true
          }
        })
        if (valid && !isPointsEmpty) {
          let {
            category: { type },
            isEdit,
          } = this
          let selectedCategory = this.assetCategory.find(
            currCategory => currCategory.id === type
          )

          this.category = { ...this.category, type: selectedCategory }
          this.$emit('onAddNewCategory', {
            category: this.category,
            group: this.selectedGroup,
            isEdit: isEdit,
          })
          this.category = cloneDeep(this.categoryObj)
        } else if (isPointsEmpty) {
          this.$message.error('Control Points cannot be empty')
        }
      })
    },
    closeCategoryDialog() {
      this.category = cloneDeep(this.categoryObj)
      this.closeDialog()
    },
  },
}
</script>

<style lang="scss">
.new-category-container {
  .el-radio__input.is-checked .el-radio__inner {
    background-color: #39b2c2;
    border-color: #39b2c2;
  }
  .flookup-field-category {
    .el-input {
      .el-input__prefix {
        right: 5px;
        left: 95%;
        z-index: 10;
        .fc-lookup-icon {
          margin-top: 12px;
        }
      }
      .el-input__suffix {
        .el-icon-circle-close {
          padding-left: 30px;
        }
      }
    }
  }
}
</style>
