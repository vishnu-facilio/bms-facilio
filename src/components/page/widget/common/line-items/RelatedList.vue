<template>
  <LineItemList
    v-bind="$attrs"
    :config="listConfiguration"
    :moduleDisplayName="moduleDisplayName"
    :moduleName="moduleName"
    :widgetDetails="widgetDetails"
    :viewname="viewname"
  >
    <template #header-additional-action-right>
      <span class="separator mL10 mR10">|</span>
      <fc-icon
        group="action"
        name="maximise"
        size="14"
        color="#91969d"
        class="pointer"
        @click="canExpandList = true"
      ></fc-icon>
    </template>
    <ListPopup
      v-if="canExpandList"
      :config="currentModuleDetails"
      @onCancel="canExpandList = false"
    ></ListPopup>
  </LineItemList>
</template>
<script>
import LineItemList from './LineItemList.vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { RelatedListData } from './RelatedListData'
import ListPopup from './RelatedListPopup.vue'

export default {
  props: ['widget', 'details'],
  components: { LineItemList, ListPopup },
  data: () => ({ canExpandList: false }),
  computed: {
    modelDataClass() {
      return RelatedListData
    },
    relatedListObj() {
      return this.$getProperty(this.widget, 'relatedList') || {}
    },
    moduleName() {
      return this.$getProperty(this.relatedListObj, 'module.name') || ''
    },
    perPage() {
      return 5
    },
    moduleDisplayName() {
      return this.$getProperty(this.relatedListObj, 'module.displayName') || ''
    },
    viewname() {
      return 'hidden-all'
    },
    relatedFieldName() {
      return this.$getProperty(this.relatedListObj, 'field.name')
    },
    recordId() {
      let { details } = this
      let { id } = details
      return id
    },
    listConfiguration() {
      let {
        redirectToOverview,
        getRecordList,
        getRecordCount,
        modelDataClass,
        modifyFieldPropsHook,
      } = this
      // Disable edit, delete and create temporarily until pageBuilder release
      // let { isEditable, isDeletable, isCreateAllowed } = this.relatedListObj || {}

      return {
        canHideEdit: true,
        canHideDelete: true,
        canShowAddBtn: false,
        canHideFooter: true,
        hideListSelect: true,
        modelDataClass,
        getRecordList,
        getRecordCount,
        modifyFieldPropsHook,
        mainfieldAction: redirectToOverview,
      }
    },
    title() {
      let { moduleDisplayName, relatedListObj } = this
      let { relatedListDisplayName } = relatedListObj?.field || {}

      return relatedListDisplayName || moduleDisplayName
    },
    widgetDetails() {
      let { relatedListObj, moduleDisplayName, perPage, details, title } = this
      let { summaryWidgetName } = relatedListObj || {}
      let emptyStateText = this.$t('setup.relationship.no_module_available', {
        moduleName: moduleDisplayName,
      })

      return {
        perPage,
        summaryWidgetName,
        emptyStateText,
        title,
        parentDetails: details,
      }
    },
    isCustomModule() {
      return this.$getProperty(this.relatedListObj, 'module.custom')
    },
    currentModuleDetails() {
      let {
        moduleName,
        title,
        getRecordList,
        getRecordCount,
        redirectToOverview,
      } = this
      return {
        lookupModuleName: moduleName,
        lookupModuleDisplayName: title,
        getRecordList,
        getRecordCount,
        mainfieldAction: redirectToOverview,
      }
    },
  },
  methods: {
    async getRecordList(extraParams) {
      let {
        moduleName,
        viewname,
        perPage,
        recordId,
        relatedFieldName,
        $attrs,
      } = this
      let { moduleName: currentModuleName } = $attrs
      let params = {
        moduleName,
        viewname,
        perPage,
        recordId,
        relatedFieldName,
        currentModuleName,
        ...extraParams,
      }

      return await this.modelDataClass.fetchAll(params)
    },
    getRecordCount() {
      return this.modelDataClass.recordListCount
    },
    modifyFieldPropsHook(field) {
      let { details, $attrs } = this
      let actualModuleName = $attrs.moduleName

      let { displayTypeEnum, lookupModuleName } = field || {}
      let isRelatedModuleField =
        displayTypeEnum === 'LOOKUP_SIMPLE' &&
        lookupModuleName === actualModuleName

      if (isRelatedModuleField) {
        let { id } = details || {}
        return { ...field, isDisabled: true, value: id }
      }
    },
    redirectToOverview(record) {
      let { moduleName, isCustomModule } = this
      let { id } = record || {}
      let route

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          route = this.$router.resolve({
            name,
            params: { viewname: 'all', id },
          }).href
        }
      } else {
        let routerMap = {
          workorder: {
            name: 'wosummarynew',
            params: { id },
          },
          workpermit: {
            name: 'workPermitSummary',
            params: { id, viewname: 'all' },
          },
          purchaserequest: {
            name: 'prSummary',
            params: { id, viewname: 'all' },
          },
          purchaseorder: {
            name: 'poSummary',
            params: { id, viewname: 'all' },
          },
          tenantcontact: {
            name: 'tenantcontact',
            params: { id, viewname: 'all' },
          },
          tenantunit: {
            path: `/app/tm/tenantunit/${id}/overview`,
          },
          vendors: {
            name: 'vendorsSummary',
            params: { id, viewname: 'all' },
          },
          asset: {
            name: 'assetsummary',
            params: { assetid: id, viewname: 'all' },
          },
          tenantspaces: {
            name: 'tenant',
            params: { id: (record.tenant || {}).id, viewname: 'all' },
          },
          serviceRequest: {
            name: 'serviceRequestSummary',
            params: { id, viewname: 'all' },
          },
          client: {
            name: 'clientSummary',
            params: { id, viewname: 'all' },
          },
          vendorcontact: {
            name: 'vendorContactsSummary',
            params: { id, viewname: 'all' },
          },
          insurance: {
            name: 'insurancesSummary',
            params: { id, viewname: 'all' },
          },
          quote: {
            path: `/app/tm/quotation/all/${id}/overview`,
          },
          item: {
            path: `/app/inventory/item/all/${id}/summary`,
          },
          tool: {
            path: `/app/inventory/tool/all/${id}/summary`,
          },
        }

        if (isCustomModule) {
          route = this.$router.resolve({
            path: `/app/ca/modules/${moduleName}/all/${id}/summary`,
          }).href
        } else {
          let moduleRouterObj = routerMap[moduleName] || null
          route =
            moduleRouterObj && this.$router.resolve(routerMap[moduleName]).href
        }
      }
      route && window.open(route, '_blank')
    },
  },
}
</script>
