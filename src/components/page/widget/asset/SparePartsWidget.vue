<template>
  <LineItemList
    ref="lineItemList"
    v-bind="$attrs"
    :config="listConfiguration"
    :widgetDetails="widgetDetails"
    moduleDisplayName="Spare Parts"
    moduleName="assetSpareParts"
    :additionalParams="additionalParams"
    :unSavedRecords.sync="unSavedRecords"
    canHideEdit="false"
    viewname="hidden-all"
    @emptyStateBtn="showLookupWizard"
  >
    <template #footer-btns>
      <el-button
        type="primary"
        class="line-item-list-footer-add-btn"
        @click="showLookupWizard"
      >
        {{ 'Select Spareparts' }}
      </el-button>
    </template>
    <FLookupFieldWizard
      v-if="canShowLookupWizard"
      :canShowLookupWizard.sync="canShowLookupWizard"
      :selectedLookupField="fieldObj"
      :withReadings="true"
      @setLookupFieldValue="setLookupFieldValue"
    />
  </LineItemList>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemListOld.vue'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  props: ['widget', 'details', 'moduleName'],
  components: { LineItemList, FLookupFieldWizard },
  data() {
    return {
      recordList: [],
      unSavedRecords: [],
      canShowLookupWizard: false,
      canHideSearch: true,
      fieldObj: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'assetSpareParts',
        placeHolderText: `${this.$t('commissioning.sheet.select_asset')}`,
        field: {
          lookupModule: {
            name: 'assetSpareParts',
            displayName: 'Spare Parts',
          },
        },
        multiple: true,
      },
      additionalParams: {
        asset: {
          id: this.details.id,
        },
      },
    }
  },
  computed: {
    filters() {
      let fieldName = this.moduleName
      let filterObj = {}

      filterObj[fieldName] = {
        operatorId: 9,
        value: [JSON.stringify(this.details?.id)],
      }
      return filterObj
    },
    widgetDetails() {
      let { widget } = this
      let { relatedList } = widget || {}
      let { summaryWidgetName } = relatedList || {}
      let emptyStateText = this.$t('common._common.spare_parts_empty_text')

      return {
        perPage: 10,
        summaryWidgetName,
        emptyStateText,
        emptyStateBtnList: [
          { value: 'default_add' },
          { label: 'Select Spare Parts', value: 'select_spare_parts' },
        ],
      }
    },
    listConfiguration() {
      let { filters } = this

      return {
        expand: false,
        isSelectionEnabled: false,
        isIndexEnabled: false,
        filters,
        canHideColumnConfig: true,
        canHideSearch: true,
        canHideFooter: false,
        canEnableMainFieldAction: false,
      }
    },
  },
  methods: {
    showLookupWizard() {
      this.canShowLookupWizard = true
    },

    async setLookupFieldValue(selectedData) {
      this.$refs['lineItemList'].isLoading = true
      let assetId = this.$getProperty(this, 'details.id', null)

      let { field } = selectedData || {}
      let { selectedItems } = field || {}
      if (isEmpty(selectedItems)) {
        return
      }
      let selectedIds = selectedItems.map(element => {
        return element?.value
      })
      let queryParam = {
        selectedIds: selectedIds,
        assetId,
      }
      let { data, error } = await API.get(
        '/v3/assetSpareParts/select',
        queryParam
      )
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        let { assetSpareParts } = data || {}
        this.unSavedRecords = assetSpareParts || []
        this.$refs['lineItemList'].loadRecords(true)
      }
    },
  },
}
</script>
