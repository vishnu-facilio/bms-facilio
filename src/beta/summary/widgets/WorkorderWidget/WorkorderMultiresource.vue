<template>
  <FContainer class="multi-resource">
    <NewLineItemList
      v-bind="$attrs"
      ref="lineItemTable"
      :config="listConfiguration"
      :moduleDisplayName="moduleDisplayName"
      :widgetDetails="widgetDetails"
      :moduleName="moduleName"
      :widget="widget"
      :additionalParams="additionalParams"
      @resize-Widget="resizeWidgetParams"
      viewname="all"
    >
    </NewLineItemList>
  </FContainer>
</template>
<script>
import { FContainer, FText } from '@facilio/design-system'
import NewLineItemList from 'src/beta/summary/widgets/WorkorderWidget/NewAssociateResource.vue'

export default {
  props: ['widget', 'details', 'resizeWidget'],
  components: {
    NewLineItemList,
    FContainer,
  },

  computed: {
    moduleName() {
      return 'multiResource'
    },
    moduleDisplayName() {
      return 'Associated Space / Asset'
    },
    filters() {
      let filterObj = {
        parentModuleId: {
          operatorId: 36,
          value: [`${this.details.moduleId}`],
        },
        parentRecordId: {
          operatorId: 36,
          value: [`${this.recordId}`],
        },
      }
      return filterObj
    },
    listConfiguration() {
      let { filters, detailsLayout } = this

      return {
        expand: true,
        detailsLayout,
        labelPosition: 'vertical',
        columns: 2,
        isSelectionEnabled: true,
        canShowActions: true,
        canShowEdit: true,
        canShowDelete: true,
        filters,
        mainfieldAction: () => {},
        canEnableMainFieldAction: false,
        canHideColumnConfig: true,
        searchType: 'ADAVANCE_SEARCH',
        formType: 'POP_UP_FORM',
        addBtnText: 'Add Space / Asset',
        formTitle: {
          defaultForm: 'Associated Space/Asset',
          addForm: 'Associate Space / Asset',
          editForm: 'Associate Space / Asset',
        },
      }
    },

    widgetDetails() {
      let { widget } = this
      let { widgetParams } = widget || {}
      let { summaryWidgetName } = widgetParams || {}
      let emptyStateText = this.$t('setup.relationship.no_module_available', {
        moduleName: 'Additional Space / Asset',
      })
      let emptyStateBtnList = [
        { label: 'Associate Space / Asset', value: 'default_add' },
      ]
      return {
        title: 'Associated Space / Asset',
        canHideTitle: true,
        perPage: 5,
        summaryWidgetName,
        emptyStateText,
        emptyStateBtnList,
      }
    },

    additionalParams() {
      return {
        parentModuleId: this.details.moduleId,
        parentRecordId: this.recordId,
      }
    },
    moduleId() {
      return this.$getProperty(
        this.widget,
        'widgetParams.module.moduleId',
        null
      )
    },
    recordId() {
      return this.$getProperty(this.details, 'id', null)
    },
  },
  methods: {
    resizeWidgetParams() {
      let height = 108
      this.resizeWidget({ height })
    },
  },
}
</script>
<style lang="scss" scoped>
.title-section-ws {
  display: none !important;
}
</style>
