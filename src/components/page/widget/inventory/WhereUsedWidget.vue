<template>
  <LineItemList
    ref="lineItemList"
    v-bind="$attrs"
    :config="listConfiguration"
    :widgetDetails="widgetDetails"
    moduleDisplayName="Where Used"
    moduleName="assetSpareParts"
    viewname="whereUsed"
  >
  </LineItemList>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemListOld.vue'

export default {
  props: ['widget', 'details', 'moduleName'],
  components: { LineItemList },
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
    }
  },
  computed: {
    filters() {
      let { itemTypeId } = this || {}
      let fieldName = 'itemType'
      let filterObj = {}

      filterObj[fieldName] = {
        operatorId: 9,
        value: [itemTypeId],
      }
      return filterObj
    },
    widgetDetails() {
      let { widget } = this
      let { relatedList } = widget || {}
      let { summaryWidgetName } = relatedList || {}
      let emptyStateText = this.$t('common._common.where_used_empty_text')

      return {
        perPage: 10,
        title: 'Where Used',
        summaryWidgetName,
        emptyStateText,
      }
    },
    itemTypeId() {
      let { moduleName } = this || {}
      if (moduleName === 'itemTypes') {
        return JSON.stringify(this.details?.id)
      } else {
        return JSON.stringify(this.details?.itemType?.id)
      }
    },

    listConfiguration() {
      let { filters } = this

      return {
        expand: false,
        isSelectionEnabled: false,
        isIndexEnabled: false,
        filters,
        canHideEdit: true,
        canHideSearch: true,
        canHideDelete: true,
        canHideAddBtn: true,
        canHideFooter: false,
        canHideColumnConfig: true,
        canEnableMainFieldAction: false,
      }
    },
  },
}
</script>
