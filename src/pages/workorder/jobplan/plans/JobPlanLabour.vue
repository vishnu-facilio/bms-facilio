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
    class="height-100"
  ></LineItemList>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemList.vue'
import { API } from '@facilio/api'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData'
import JobPlanPlansCommon from 'src/pages/workorder/jobplan/plans/JobPlanPlansCommon'
import { PUBLISHED_STATUS } from 'pages/workorder/pm/create/utils/pm-utils.js'
export default {
  extends: JobPlanPlansCommon,
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'groupKey',
    'activeTab',
    'widget',
    'resizeWidget',
    'module',
  ],
  components: {
    LineItemList,
  },
  computed: {
    moduleName() {
      return 'jobPlanLabours'
    },
    moduleDisplayName() {
      return 'Job Plan Labors'
    },
    jobPlan() {
      let { details } = this || {}
      let { jobplan } = details || {}
      return jobplan
    },
    filters() {
      let { details } = this || {}
      let { id } = details || {}
      let filter = {
        parent: {
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

    additionalParams() {
      let { details } = this || {}
      let { id } = details || {}
      return {
        parent: { id },
      }
    },
    formConfig() {
      return {
        formType: 'POP_UP_FORM',
        modifyFieldPropsHook: field => {
          let { name } = field || {}
          if (name === 'craft') {
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
        editConfig,
        deleteConfig,
        hideFooter,
      } = this

      return {
        canHideHeader: false,
        canHideDelete: false,
        canHideFooter: false,
        canHideFilter: false,
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
        ...(editConfig || {}),
        ...(deleteConfig || {}),
        ...(hideFooter || {}),
        filters,
        searchType: 'ADAVANCE_SEARCH',
        formType: 'POP_UP_FORM',
        formTitle: {
          addForm: 'Labor Details',
          editForm: 'Labor Details',
        },
      }
    },
    widgetDetails() {
      let { widget, emptyStateBtnList } = this
      let { relatedList } = widget || {}
      let { summaryWidgetName } = relatedList || {}
      let emptyStateText = this.$t('setup.relationship.no_module_available', {
        moduleName: 'Labors',
      })

      return {
        title: this.$t('common._common.list_of_labours'),
        canHideTitle: true,
        perPage: 5,
        summaryWidgetName,
        emptyStateText,
        actionButtonList: emptyStateBtnList,
      }
    },
    emptyStateBtnList() {
      let { details } = this || {}
      let { jpStatus } = details || {}

      let emptyStateBtnList = []

      if (PUBLISHED_STATUS[jpStatus] === 'Unpublished') {
        emptyStateBtnList = [
          {
            label: this.$t('common._common.select_craft'),
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
                  'v3/jobPlanLabours/plans',
                  params
                )
                if (error) {
                  this.$message.error(
                    error.message || this.$t('common._common.error_occured')
                  )
                } else {
                  let { jobPlanLabours } = data || {}

                  return new CustomModuleData({
                    ...(jobPlanLabours || {}),
                    moduleName,
                  })
                }
              },
            },
          },
        ]
      }
      return emptyStateBtnList
    },
  },
}
</script>
