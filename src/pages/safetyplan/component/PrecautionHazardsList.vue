<template>
  <div>
    <LineItemList
      v-bind="$attrs"
      :config="listConfiguration"
      :moduleDisplayName="moduleDisplayName"
      :moduleName="moduleName"
      :widgetDetails="widgetDetails"
      style="height:100%;"
    >
    </LineItemList>
  </div>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemListOld.vue'
import { API } from '@facilio/api'

export default {
  props: ['widget', 'details'],
  components: { LineItemList },
  computed: {
    moduleName() {
      return 'hazardPrecaution'
    },
    moduleDisplayName() {
      return 'Associated Hazards'
    },
    filters() {
      let { details, widget } = this
      let { id } = details
      let { relatedList } = widget || {}
      let { field } = relatedList || {}
      let fieldName = (field || {}).name || 'precaution'
      let filterObj = {}

      filterObj[fieldName] = { operatorId: 36, value: [`${id}`] }
      return filterObj
    },
    editConfig() {
      return {
        canHideEdit: true,
      }
    },
    deleteConfig() {
      return {
        canHideDelete: true,
      }
    },
    columnCustomConfig() {
      return { canShowColumnConfig: false }
    },
    listConfiguration() {
      let {
        filters,
        editConfig,
        deleteConfig,
        columnCustomConfig,
        searchAndFilterConfig,
        quickCreateConfig,
        getViewDetails,
      } = this

      return {
        expand: false,
        isSelectionEnabled: false,
        isIndexEnabled: false,
        filters,
        canHideFooter: true,
        getViewDetails,
        ...(editConfig || {}),
        ...(deleteConfig || {}),
        ...(columnCustomConfig || {}),
        ...(quickCreateConfig || {}),
        ...(searchAndFilterConfig || {}),
        canEnableMainFieldAction: true,
      }
    },
    quickCreateConfig() {
      return {
        canHideAddBtn: true,
      }
    },
    widgetDetails() {
      let { moduleDisplayName } = this
      let emptyStateText = this.$t('common.safetyPlan.no_hazard_added', {
        moduleName: moduleDisplayName,
      })

      return {
        perPage: 7,
        emptyStateText,
        emptyStateBtnList: [],
      }
    },
    searchAndFilterConfig() {
      return { canHideSearch: false, canHideFilter: false }
    },
  },
  methods: {
    async getViewDetails() {
      let { error, data } = await API.get(`v2/views/associatedhazards`, {
        moduleName: 'hazardPrecaution',
      })
      if (error) {
        return { error, data }
      } else {
        let viewDetail = data?.viewDetail || {}
        let { fields } = viewDetail
        viewDetail.fields = fields.filter(fldObj => {
          let { field } = fldObj || {}
          return !field?.mainField
        })
        return { error, data: { viewDetail } }
      }
    },
  },
}
</script>
