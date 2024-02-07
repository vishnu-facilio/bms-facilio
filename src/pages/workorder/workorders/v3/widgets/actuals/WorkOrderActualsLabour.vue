<template>
  <div>
    <LineItemList
      v-bind="$attrs"
      ref="lineItemList"
      :config="listConfiguration"
      :moduleName="moduleName"
      :moduleDisplayName="moduleDisplayName"
      :additionalParams="additionalParams"
      :widgetDetails="widgetDetails"
      viewname="all"
      @onDelete="refreshCost()"
      @onCreateOrUpdate="refreshCost()"
      @clickedActionBtn="btnValue"
      class="height-100"
    >
    </LineItemList>
  </div>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemList'
import { eventBus } from '@/page/widget/utils/eventBus'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData'
import { API } from '@facilio/api'

export default {
  props: ['widget', 'details'],
  components: {
    LineItemList,
  },
  data() {
    return {
      actionButtonValue: null,
    }
  },
  computed: {
    moduleDisplayName() {
      return 'Work Order Labor Actual(s)'
    },
    moduleName() {
      return 'workorderLabour'
    },
    workorder() {
      let { details } = this || {}
      let { workorder } = details || {}
      return workorder
    },
    filters() {
      let { workorder } = this || {}
      let { id } = workorder || {}
      let filter = {
        parentId: {
          operatorId: 9,
          value: [`${id}`],
        },
      }
      return filter
    },
    editConfig() {
      return {
        canHideEdit: false,
      }
    },
    deleteConfig() {
      return {
        canHideDelete: false,
      }
    },
    searchAndFilterConfig() {
      return { canHideSearch: false, canHideFilter: false }
    },
    formConfig() {
      return {
        formType: 'POP_UP_FORM',
        modifyFieldPropsHook: field => {
          let { actionButtonValue } = this || {}
          let { lookupModuleName } = actionButtonValue || {}
          let { name } = field || {}
          if (['craft', 'cost'].includes(name)) {
            return { ...field, isDisabled: true }
          }
          if (name === 'labour' && lookupModuleName === 'labourCraftSkill') {
            return { ...field, isDisabled: true }
          }
        },
      }
    },
    listConfiguration() {
      let {
        filters,
        searchAndFilterConfig,
        formConfig,
        disableActionableUIElements,
      } = this

      return {
        expand: true,
        isSelectionEnabled: false,
        isIndexEnabled: false,
        filters,
        canHideColumnConfig: true,
        canShowEdit: true,
        canShowDelete: true,
        canHideFooter: false,
        ...(searchAndFilterConfig || {}),
        ...(formConfig || {}),
        formTitle: {
          addForm: 'Labor Details',
          editForm: 'Labor Details',
        },
        disableActionableUIElements: disableActionableUIElements,
      }
    },

    widgetDetails() {
      let { widget } = this
      let { relatedList } = widget || {}
      let { summaryWidgetName } = relatedList || {}
      let emptyStateText = this.$t('setup.relationship.no_module_available', {
        moduleName: 'Labors',
      })

      let actionButtonList = [
        {
          label: this.$t('common._common.select_labor'),
          value: {
            lookupModuleName: 'labourCraftSkill',
            lookupModuleDisplayName: 'Labor',
            getRecordDetails: async payload => {
              let { id, moduleName, lookupModuleName } = payload || {}
              let params = {
                recordId: id,
                moduleName: lookupModuleName,
              }
              let { data, error } = await API.get(
                '/v3/workOrderLabour/actuals',
                params
              )
              if (error) {
                this.$message.error(
                  error.message || this.$t('common._common.error_occured')
                )
              } else {
                let { workorderLabour } = data || {}
                return new CustomModuleData({
                  ...(workorderLabour || {}),
                  moduleName,
                })
              }
            },
          },
        },
        {
          label: this.$t('common._common.select_planned_labor'),
          value: {
            lookupModuleName: 'workorderLabourPlan',
            lookupModuleDisplayName: 'Planned Labor',
            getRecordDetails: async payload => {
              let { id, moduleName, lookupModuleName } = payload || {}
              let params = {
                recordId: id,
                moduleName: lookupModuleName,
              }
              let { data, error } = await API.get(
                '/v3/workOrderLabour/actuals',
                params
              )
              if (error) {
                this.$message.error(
                  error.message || this.$t('common._common.error_occured')
                )
              } else {
                let { workorderLabour } = data || {}
                return new CustomModuleData({
                  ...(workorderLabour || {}),
                  moduleName,
                })
              }
            },
            getRecordList: async () => {
              let { workorder } = this || {}
              let { id } = workorder || {}
              let filters = {
                parent: {
                  operatorId: 9,
                  value: [`${id}`],
                },
              }
              let params = {
                filters: JSON.stringify(filters),
              }
              let { list, error } = await API.fetchAll(
                'workorderLabourPlan',
                params
              )
              if (!error) {
                return list
              }
            },
            getRecordCount: async () => {
              let { workorder } = this || {}
              let { id } = workorder || {}
              let filters = {
                parent: {
                  operatorId: 9,
                  value: [`${id}`],
                },
              }
              let params = {
                filters: JSON.stringify(filters),
              }
              let { list, error } = await API.fetchAll(
                'workorderLabourPlan',
                params
              )
              if (!error) {
                return list.length
              }
            },
          },
        },
      ]

      return {
        title: this.$t('common._common.list_of_labours'),
        canHideTitle: true,
        emptyStateText,
        perPage: 5,
        summaryWidgetName,
        actionButtonList,
      }
    },
    workOrderSettings() {
      let { details } = this
      let { workOrderSettings = {} } = details || {}
      return workOrderSettings
    },
    disableActionableUIElements() {
      let { workOrderSettings } = this
      let { inventoryActuals } = workOrderSettings || {}
      let { allowed: canDoActionsOnActuals = true } = inventoryActuals || {}
      return !canDoActionsOnActuals
    },
  },
  methods: {
    btnValue(value) {
      this.actionButtonValue = value
    },
    additionalParams() {
      let { workorder } = this || {}
      let { id } = workorder || {}
      return {
        parentId: id,
      }
    },
    refreshCost() {
      eventBus.$emit('reloadOverallCost')
    },

    getWorkOrderId() {
      let { workorder } = this || {}
      let { id } = workorder || {}
      return id
    },
  },
}
</script>
