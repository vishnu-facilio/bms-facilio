<template>
  <FModal
    v-if="canShowDialog"
    title="UTILITY TYPES"
    :visible="canShowDialog"
    :confirmLoading="false"
    size="S"
    type="default"
    padding="containerNone"
    @ok="openMeterCreation()"
    @cancel="closeDialog()"
  >
    <FContainer padding="containerXLarge sectionSmall" height="200px">
      <FText>Utility Type</FText>
      <Lookup
        v-model="categoryFormObj.categoryId"
        :field="selectedLookupField"
        @showLookupWizard="showLookupWizard"
      />
      <LookupWizard
        v-if="canShowLookupWizard"
        :canShowLookupWizard.sync="canShowLookupWizard"
        @setLookupFieldValue="setWizardValue"
        :field.sync="selectedLookupField"
      />
    </FContainer>
  </FModal>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
import { FModal, FContainer, FText } from '@facilio/design-system'
import { Lookup, LookupWizard } from '@facilio/ui/new-forms'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
const selectedLookupField = {
  isDataLoading: false,
  options: [],
  config: {},
  lookupModuleName: 'utilitytype',
  field: {
    lookupModule: {
      name: 'utilitytype',
      displayName: 'Meter Category',
    },
  },
  multiple: false,
  selectedItems: [],
}
export default {
  props: ['canShowUtilityTypeDialog', 'selectedCategory'],
  components: { FModal, FContainer, FText, Lookup, LookupWizard },
  data() {
    return {
      error: false,
      isSaving: false,
      message: this.$t('asset._meters.please_select_utilityType'),
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
        return this.canShowUtilityTypeDialog
      },
      set(value) {
        this.$emit('update:canShowUtilityTypeDialog', value)
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
    async openMeterCreation() {
      let { categoryFormObj } = this
      let { categoryId } = categoryFormObj || {}

      this.isSaving = true
      if (!isEmpty(categoryId)) {
        let param = { moduleName: 'utilitytype', id: categoryId }
        let { data, error } = await API.get('v3/modules/data/summary', param)
        if (!error) {
          let { utilitytype } = data || {}
          let selectedCategory = cloneDeep(utilitytype)
          let categoryModuleName = (selectedCategory || {}).moduleName
          let utilityTypeId = (selectedCategory || {}).id
          if (isWebTabsEnabled()) {
            let { name } = findRouteForModule('meter', pageTypes.CREATE) || {}
            name &&
              this.$router.push({
                name,
                query: { categoryModuleName, utilityTypeId },
              })
          } else {
            this.$router.push({
              name: 'meter',
              query: { categoryModuleName, utilityTypeId },
            })
          }
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
