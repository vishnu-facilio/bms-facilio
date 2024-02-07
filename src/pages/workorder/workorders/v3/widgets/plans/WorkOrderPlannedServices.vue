<template>
  <div>
    <LineItemList
      v-bind="$attrs"
      ref="lineItemTable"
      :config="listConfiguration"
      :moduleName="moduleName"
      :widgetDetails="widgetDetails"
      :additionalParams="additionalParams"
      :moduleDisplayName="moduleDisplayName"
      @onDelete="reloadCost()"
      @onCreateOrUpdate="reloadCost()"
      @handleSelection="selectedRecords"
      viewname="all"
      class="height-100"
    >
      <template #bulk-actions>
        <span
          :class="{
            'cursor-not-allowed disabled': disableActionableUIElements,
          }"
        >
          <div
            class="delete-btn-container margin-auto"
            @click="deleteLineItems()"
            :class="{
              'pointer-events-none': disableActionableUIElements,
            }"
          >
            <div class="text-center">
              <Inline-svg
                src="svgs/plans/ic-delete"
                iconClass="icon icon-sm-md"
              ></Inline-svg>
            </div>

            <div class="reserve text-center">
              {{ $t('common._common.delete') }}
            </div>
          </div>
        </span>
      </template>
    </LineItemList>
  </div>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemList.vue'
import WorkOrderPlans from './WorkOrderPlans.vue'
import { CustomModuleData } from 'pages/custom-module/CustomModuleData'
export default {
  extends: WorkOrderPlans,
  props: ['workOrderId', 'widget', 'disableActionableUIElements', 'workorder'],
  components: {
    LineItemList,
  },
  data() {
    return {
      selectedLineItems: [],
    }
  },
  computed: {
    moduleName() {
      return 'workOrderPlannedServices'
    },
    moduleDisplayName() {
      return this.$t('common.inventory._services')
    },
    listConfiguration() {
      let { formConfig, filters, disableActionableUIElements } = this
      return {
        filters,
        skipModulePermission: {
          editPermission: true,
          deletePermission: true,
        },
        ...(formConfig || {}),
        disableActionableUIElements: disableActionableUIElements,
      }
    },
    formConfig() {
      return {
        formType: 'POP_UP_FORM',
        modifyFieldPropsHook: field => {
          let { name } = field || {}
          if (name === 'service') {
            return { ...field, isDisabled: true }
          }
        },
      }
    },
    widgetDetails() {
      let { widget, moduleDisplayName, emptyStateBtnList } = this
      let { widgetParams } = widget || {}
      let { summaryWidgetName } = widgetParams || {}
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
      return [
        {
          label: this.$t('common.inventory.select_service'),
          value: {
            lookupModuleName: 'service',
            lookupModuleDisplayName: this.$t('common.inventory._services'),
            getRecordDetails: async payload => {
              let { id, moduleName } = payload || {}
              let workOrderPlannedService = {
                service: {
                  id: id,
                },
              }

              return new CustomModuleData({
                ...workOrderPlannedService,
                moduleName,
              })
            },
          },
        },
      ]
    },
  },
}
</script>
<style lang="scss" scoped>
@import 'src/pages/Inventory/styles/inventory-styles.scss';
</style>
