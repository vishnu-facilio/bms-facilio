<script>
import { CustomModuleData } from 'pages/custom-module/CustomModuleData'
import { PUBLISHED_STATUS } from 'pages/workorder/pm/create/utils/pm-utils.js'

export default {
  props: ['widget', 'details'],
  computed: {
    workorder() {
      let { details } = this || {}
      let { workorder } = details || {}
      return workorder
    },
    additionalParams() {
      let { details } = this || {}
      let { id } = details || {}
      return {
        jobPlan: { id },
      }
    },
    filters() {
      let { details } = this || {}
      let { id } = details || {}
      let filter = {
        jobPlan: {
          operatorId: 9,
          value: [`${id}`],
        },
      }
      return filter
    },
    hideFooter() {
      let { details } = this || {}
      let { jpStatus } = details || {}
      let canHideFooter = PUBLISHED_STATUS[jpStatus] === 'Published'
      return { canHideFooter }
    },
    editConfig() {
      let { hideFooter } = this || {}
      let { canHideFooter } = hideFooter || {}
      return { canHideEdit: canHideFooter }
    },
    deleteConfig() {
      let { hideFooter } = this || {}
      let { canHideFooter } = hideFooter || {}
      return { canHideDelete: canHideFooter }
    },

    listConfiguration() {
      let { filters, formConfig, editConfig, deleteConfig, hideFooter } = this
      return {
        filters,
        ...(editConfig || {}),
        ...(deleteConfig || {}),
        ...(hideFooter || {}),
        ...(formConfig || {}),
        canHideColumnConfig: true,
        hideListSelect: true,
      }
    },
    formConfig() {
      return {
        formType: 'POP_UP_FORM',
        modifyFieldPropsHook: field => {
          let { name } = field || {}
          if (
            (name === 'itemType' && this.moduleName === 'jobPlanItems') ||
            (name === 'toolType' && this.moduleName === 'jobPlanTools') ||
            (name === 'service' && this.moduleName === 'jobPlanServices')
          ) {
            return { ...field, isDisabled: true }
          }
        },
      }
    },
    widgetDetails() {
      let { widget, moduleDisplayName, emptyStateBtnList } = this
      let { relatedList } = widget || {}
      let { summaryWidgetName } = relatedList || {}
      let emptyStateText = this.$t('setup.relationship.no_module_available', {
        moduleName: moduleDisplayName,
      })
      return {
        canHideTitle: true,
        perPage: 5,
        summaryWidgetName,
        emptyStateText,
        actionButtonList: emptyStateBtnList,
      }
    },
    emptyStateBtnList() {
      let { moduleName, details } = this
      let { jpStatus } = details || {}
      let emptyStateBtnList = []
      if (PUBLISHED_STATUS[jpStatus] === 'Unpublished') {
        if (moduleName === 'jobPlanItems') {
          emptyStateBtnList = [
            {
              label: this.$t('common.inventory.select_item'),
              value: {
                lookupModuleName: 'itemTypes',
                lookupModuleDisplayName: this.$t(
                  'common.inventory._item_types'
                ),
                getRecordDetails: async payload => {
                  let { id, moduleName } = payload || {}
                  let jobPlanItems = {
                    itemType: {
                      id: id,
                    },
                  }

                  return new CustomModuleData({
                    ...jobPlanItems,
                    moduleName,
                  })
                },
              },
            },
          ]
        } else if (moduleName === 'jobPlanTools') {
          emptyStateBtnList = [
            {
              label: this.$t('common.inventory.select_tool'),
              value: {
                lookupModuleName: 'toolTypes',
                lookupModuleDisplayName: this.$t(
                  'common.inventory._tool_types'
                ),
                getRecordDetails: async payload => {
                  let { id, moduleName } = payload || {}
                  let jobPlanTools = {
                    toolType: {
                      id: id,
                    },
                  }

                  return new CustomModuleData({
                    ...jobPlanTools,
                    moduleName,
                  })
                },
              },
            },
          ]
        } else if (moduleName === 'jobPlanServices') {
          emptyStateBtnList = [
            {
              label: this.$t('common.inventory.select_service'),
              value: {
                lookupModuleName: 'service',
                lookupModuleDisplayName: this.$t('common.inventory._services'),
                getRecordDetails: async payload => {
                  let { id, moduleName } = payload || {}
                  let jobPlanServices = {
                    service: {
                      id: id,
                    },
                  }

                  return new CustomModuleData({
                    ...jobPlanServices,
                    moduleName,
                  })
                },
              },
            },
          ]
        }
      }
      return emptyStateBtnList
    },
  },
}
</script>
