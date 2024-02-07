<template>
  <LineItemList
    v-bind="$attrs"
    ref="lineItemTable"
    :config="listConfiguration"
    :moduleName="moduleName"
    :widgetDetails="widgetDetails"
    :moduleDisplayName="moduleDisplayName"
    :additionalParams="additionalParams"
    viewname="all"
    @onDelete="reloadCost()"
    @onCreateOrUpdate="reloadCost()"
    class="height-100"
  ></LineItemList>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemList.vue'
import WorkOrderPlans from './WorkOrderPlans.vue'
import { API } from '@facilio/api'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData'
export default {
  extends: WorkOrderPlans,
  props: [
    'workOrderId',
    'resizeWidget',
    'widget',
    'disableActionableUIElements',
    'workorder',
  ],
  components: {
    LineItemList,
  },
  data() {
    return {
      details: null,
      selectedLookupId: null,
    }
  },
  computed: {
    moduleName() {
      return 'workorderLabourPlan'
    },
    moduleDisplayName() {
      return 'Work Order Labor Plan(s)'
    },

    filters() {
      let filterObj = {
        parent: {
          operatorId: 9,
          value: [`${this.workOrderId}`],
        },
      }
      return filterObj
    },
    formConfig() {
      return {
        formType: 'POP_UP_FORM',
        modifyFieldPropsHook: field => {
          let { name } = field || {}
          if (['craft', 'totalPrice'].includes(name)) {
            return { ...field, isDisabled: true }
          }
        },
      }
    },

    listConfiguration() {
      let {
        filters,
        detailsLayout,
        formConfig,
        disableActionableUIElements,
      } = this

      return {
        canHideHeader: false,
        canHideEdit: false,
        canHideDelete: false,
        canHideFooter: false,
        canHideFilter: false,
        canHideSummaryWidget: false,
        isSelectionEnabled: true,
        accordion: true, //expand one at a time
        detailsLayout, //details construction for quick summary
        labelPosition: 'vertical', // vertical or horizontal
        columns: 2, //colunms split in quick summary; max cols allowed is 4
        canShowActions: true,
        canShowEdit: true,
        canShowDelete: true,
        canHideColumnConfig: true,
        ...(formConfig || {}),
        filters,
        searchType: 'ADAVANCE_SEARCH',

        formTitle: {
          defaultForm: 'Labour',
          addForm: 'Labour Details',
          editForm: 'Labour Details',
        },
        disableActionableUIElements: disableActionableUIElements,
      }
    },
    widgetDetails() {
      let { widget } = this
      let { widgetParams } = widget || {}
      let { summaryWidgetName } = widgetParams || {}
      let emptyStateText = this.$t('setup.relationship.no_module_available', {
        moduleName: 'Labors',
      })

      return {
        title: this.$t('common._common.list_of_labours'),
        canHideTitle: true,
        perPage: 5,
        summaryWidgetName,
        emptyStateText,
        actionButtonList: [
          {
            label: this.$t('common._common.add_craft'),
            value: {
              lookupModuleName: 'crafts',
              lookupModuleDisplayName: 'Craft',
              getRecordDetails: async payload => {
                let { id, moduleName, lookupModuleName } = payload || {}
                let params = {
                  recordId: id,
                  moduleName: lookupModuleName,
                }
                let { data, error } = await API.get(
                  '/v3/workOrderLabour/plans',
                  params
                )
                if (error) {
                  this.$message.error(
                    error.message || this.$t('common._common.error_occured')
                  )
                } else {
                  let { workorderLabourPlan } = data || {}

                  return new CustomModuleData({
                    ...(workorderLabourPlan || {}),
                    moduleName,
                  })
                }
              },
            },
          },
        ],
      }
    },
    additionalParams() {
      let params = {
        parent: { id: this.workOrderId },
      }
      return params
    },
  },
}
</script>
