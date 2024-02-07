<template>
  <el-dialog
    :title="'Categories'"
    v-if="canShowDialog"
    :visible.sync="canShowDialog"
    width="30%"
    class="fc-dialog-center-container"
    :append-to-body="true"
  >
    <div class="height250">
      <div class="label">
        {{ $t('common._common.category') }} <span class="label-span">*</span>
      </div>
      <FLookupField
        class="width400px mT10"
        :model.sync="categoryFormObj.categoryId"
        :field="selectedLookupField"
        @showLookupWizard="showLookupWizard"
      />
      <FLookupFieldWizard
        v-if="canShowLookupWizard"
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="selectedLookupField"
        @setLookupFieldValue="setWizardValue"
      />
      <div v-if="error" class="category-empty-msg">{{ message }}</div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="openAssetCreation()"
          :loading="isSaving"
          >{{ $t('common._common.confirm') }}</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import FLookupField from 'src/components/forms/FLookupField.vue'
import FLookupFieldWizard from 'src/components/FLookupFieldWizard.vue'
import cloneDeep from 'lodash/cloneDeep'
const selectedLookupField = {
  isDataLoading: false,
  options: [],
  config: {},
  lookupModuleName: 'assetcategory',
  field: {
    lookupModule: {
      name: 'assetcategory',
      displayName: 'Asset Category',
    },
  },
  multiple: false,
  selectedItems: [],
}
export default {
  props: ['canShowCategoryDialog', 'selectedCategory'],
  components: { FLookupField, FLookupFieldWizard },
  data() {
    return {
      error: false,
      isSaving: false,
      message: this.$t('common._common.please_select_category'),
      canShowLookupWizard: false,
      selectedLookupField,
      categoryFormObj: {
        categoryId: null,
      },
    }
  },
  computed: {
    canShowDialog: {
      get() {
        return this.canShowCategoryDialog
      },
      set(value) {
        this.$emit('update:canShowCategoryDialog', value)
      },
    },
  },
  watch: {
    selectedLookupField: {
      async handler() {
        this.error = false
        this.canShowLookup = false
      },
      deep: true,
    },
  },
  methods: {
    closeDialog() {
      this.$set(this, 'canShowDialog', false)
    },
    showLookupWizard(_, canShow) {
      this.canShowLookupWizard = canShow
    },
    setWizardValue(props) {
      let { field } = props || {}
      let { selectedItems = [] } = field || {}
      let selectedItemIds = []

      if (!isEmpty(selectedItems)) {
        selectedItemIds = selectedItems.find(item => item.value).value || {}
      }
      this.$set(this.categoryFormObj, 'categoryId', selectedItemIds)
    },
    async openAssetCreation() {
      let { categoryFormObj } = this
      let { categoryId } = categoryFormObj || {}

      this.isSaving = true
      if (!isEmpty(categoryId)) {
        let param = { moduleName: 'assetcategory', id: categoryId }
        let { data, error } = await API.get('v3/modules/data/summary', param)
        if (!error) {
          let { assetcategory } = data || {}
          let selectedCategory = cloneDeep(assetcategory)
          this.$emit('update:selectedCategory', selectedCategory)
          this.$emit('openAssetCreation')
        }
      } else {
        this.error = true
      }
      this.isSaving = false
    },
  },
}
</script>
<style lang="scss" scoped>
.category-empty-msg {
  color: #f56c6c;
  font-size: 12px;
  line-height: 1;
  margin-top: 5px;
  padding-top: 4px;
}
.label {
  color: #385571;
  letter-spacing: 0.8px;
  position: relative;
  font-weight: normal;
}
.label-span {
  color: red;
  margin-left: 2px;
  font-size: 15px;
}
</style>
