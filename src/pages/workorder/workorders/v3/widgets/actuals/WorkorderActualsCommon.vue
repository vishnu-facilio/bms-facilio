<script>
import { eventBus } from '@/page/widget/utils/eventBus'
import Vue from 'vue'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { CustomModuleData } from 'pages/custom-module/CustomModuleData'
import InventoryMixin from 'pages/Inventory/InventoryMixin'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  mixins: [InventoryMixin],
  props: ['widget', 'details', 'resizeWidget'],
  data() {
    return {
      inventoryReservationId: null,
      requestedLineItem: null,
      remainingQuantity: null,
      parentTransactionId: null,
      actionButtonValue: null,
      isServiceDurationBased: false,
      isItemTypeRotating: null,
    }
  },
  computed: {
    workorder() {
      let { details } = this || {}
      let { workorder } = details || {}
      return workorder
    },
    siteId() {
      let { workorder } = this
      let { siteId } = workorder || {}
      return siteId
    },
    invReqModuleName() {
      return 'inventoryrequest'
    },
    additionalParams() {
      let {
        workorder,
        inventoryReservationId,
        parentTransactionId,
        remainingQuantity,
        requestedLineItem,
        actionButtonValue,
      } = this
      let { id, currencyCode, exchangeRate } = workorder || {}
      let { lookupModuleName } = actionButtonValue || {}
      let value = null
      if (
        !isEmpty(inventoryReservationId) &&
        lookupModuleName === 'inventoryReservation'
      ) {
        value = {
          parentId: id,
          inventoryReservation: { id: inventoryReservationId },
        }
      }
      if (!isEmpty(parentTransactionId)) {
        return {
          parentId: id,
          parentTransactionId,
          remainingQuantity: remainingQuantity,
          requestedLineItem: requestedLineItem || null,
        }
      }

      value = isEmpty(value)
        ? {
            parentId: id,
          }
        : value

      return !isEmpty(currencyCode)
        ? {
            ...value,
            currencyCode,
            exchangeRate,
          }
        : value
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
      let { moduleName } = this
      return {
        canHideEdit: moduleName === 'workorderItem' || 'workorderTool',
      }
    },
    deleteConfig() {
      return {
        canHideDelete: true,
      }
    },
    searchAndFilterConfig() {
      return { canHideSearch: false, canHideFilter: false }
    },
    formConfig() {
      return {
        formType: 'POP_UP_FORM',
        modifyFieldPropsHook: field => {
          let { name } = field || {}

          if (
            ['item', 'tool', 'service', 'storeRoom', 'workorder'].includes(name)
          ) {
            return { ...field, isDisabled: true }
          }
          if (name === 'asset') {
            return !this.isItemTypeRotating
              ? { ...field, hideField: true }
              : { ...field, required: true }
          }
          if (name === 'quantity' && this.isItemTypeRotating) {
            return { ...field, isDisabled: true }
          }

          if (name === 'issuedTo' && this.isIssuedToolsAddition()) {
            return { ...field, isDisabled: true }
          }
        },
      }
    },
    quickCreateConfig() {
      return {
        canHideAddBtn: false,
      }
    },
    columnCustomConfig() {
      return { canHideColumnConfig: true }
    },
    listConfiguration() {
      let {
        filters,
        editConfig,
        deleteConfig,
        searchAndFilterConfig,
        columnCustomConfig,
        formConfig,
        quickCreateConfig,
        tableSlotList,
        disableActionableUIElements,
      } = this
      return {
        hideListSelect: true,
        filters,
        canHideFooter: false,
        ...(editConfig || {}),
        ...(deleteConfig || {}),
        ...(columnCustomConfig || {}),
        ...(quickCreateConfig || {}),
        ...(searchAndFilterConfig || {}),
        ...(formConfig || {}),
        tableSlotList: tableSlotList || [],
        mainfieldAction: () => {},
        timezone: Vue.prototype.$timezone,
        timeformat: Vue.prototype.$timeformat || 'HH:mm a',
        dateformat: Vue.prototype.$dateformat,
        disableActionableUIElements: disableActionableUIElements,
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
      let { moduleName, siteId, workorder } = this
      let workOrderId = this.$getProperty(workorder, 'id')

      let emptyStateBtnList = []
      // if (this.checkWoManageInventoryPermission())
      if (moduleName === 'workorderItem') {
        if (this.checkWoManageInventoryPermission()) {
          emptyStateBtnList = [
            {
              label: this.$t('common.inventory.select_item'),
              value: {
                lookupModuleName: 'item',
                lookupModuleDisplayName: this.$t('common.inventory.item'),
                additionalParams: {
                  siteId,
                  includeServingSite: true,
                },
                additionalFilters: {
                  quantity: {
                    operatorId: 13,
                    value: ['0'],
                  },
                },
                getRecordDetails: async payload => {
                  let { id, moduleName } = payload || {}

                  let queryParam = {
                    itemId: id,
                    workOrderId,
                  }
                  let { data, error } = await API.get(
                    '/v3/workOrderItem/getWorkorderItem',
                    queryParam
                  )
                  if (error) {
                    this.$message.error(
                      error.message || this.$t('common._common.error_occured')
                    )
                  } else {
                    let { workorderItem } = data || {}
                    let { itemType } = workorderItem || {}
                    let { isRotating } = itemType || {}
                    this.isItemTypeRotating = isRotating
                    return new CustomModuleData({
                      ...workorderItem,
                      moduleName,
                    })
                  }
                },
              },
            },
            {
              label: this.$t('common.inventory.select_reserved_items'),
              value: {
                lookupModuleName: 'inventoryReservation',
                lookupModuleDisplayName: this.$t(
                  'common.inventory.reserved_items'
                ),
                additionalFilters: {
                  workOrder: {
                    operatorId: 9,
                    value: [`${workOrderId}`],
                  },
                  reservationStatus: {
                    operatorId: 10,
                    value: [`${3}`],
                  },
                  itemType: {
                    operatorId: 2,
                  },
                },
                getRecordDetails: async payload => {
                  let { id, moduleName } = payload || {}
                  this.inventoryReservationId = !isEmpty(id) ? id : null
                  let queryParam = {
                    reservationId: id,
                  }
                  let { data, error } = await API.get(
                    '/v3/workOrderItem/getWorkorderItemFromReservation',
                    queryParam
                  )
                  if (error) {
                    this.$message.error(
                      error.message || this.$t('common._common.error_occured')
                    )
                  } else {
                    let { workorderItem } = data || {}
                    let { itemType } = workorderItem || {}
                    let { isRotating } = itemType || {}
                    this.isItemTypeRotating = isRotating
                    return new CustomModuleData({
                      ...workorderItem,
                      moduleName,
                    })
                  }
                },
              },
            },
          ]
        }
        emptyStateBtnList.push({
          label: this.$t('common.inventory.select_issued_item'),
          value: {
            lookupModuleName: 'itemTransactions',
            lookupModuleDisplayName: this.$t('common.inventory.issued_items'),
            viewname: 'issued-items',
            additionalParams: {
              siteId,
              showItemsForIssue: true,
            },
            getRecordDetails: async payload => {
              let { id, moduleName } = payload || {}
              this.parentTransactionId = !isEmpty(id) ? id : null
              let { workorder } = this
              let queryParam = {
                itemTransactionId: id,
                workOrderId: workorder?.id,
              }
              let { data, error } = await API.get(
                '/v3/workOrderItem/getWorkOrderItemFromIssuedItem',
                queryParam
              )
              if (error) {
                this.$message.error(
                  error.message || this.$t('common._common.error_occured')
                )
              } else {
                let { workorderItem } = data || {}
                let { requestedLineItem, remainingQuantity } =
                  workorderItem || {}
                this.requestedLineItem = requestedLineItem
                this.remainingQuantity = remainingQuantity
                return new CustomModuleData({
                  ...workorderItem,
                  moduleName,
                })
              }
            },
          },
        })
      } else if (moduleName === 'workorderTools') {
        if (this.checkWoManageInventoryPermission()) {
          emptyStateBtnList = [
            {
              label: this.$t('common.inventory.select_tool'),
              value: {
                lookupModuleName: 'tool',
                lookupModuleDisplayName: this.$t('common.inventory._tools'),
                additionalParams: {
                  siteId,
                  includeServingSite: true,
                },
                additionalFilters: {
                  quantity: {
                    operatorId: 13,
                    value: ['0'],
                  },
                },
                getRecordDetails: async payload => {
                  let { id, moduleName } = payload || {}
                  let queryParam = {
                    toolId: id,
                    workOrderId,
                  }
                  let { data, error } = await API.get(
                    '/v3/workorderTools/getWorkorderTool',
                    queryParam
                  )
                  if (error) {
                    this.$message.error(
                      error.message || this.$t('common._common.error_occured')
                    )
                  } else {
                    let { workorderTools } = data || {}
                    return new CustomModuleData({
                      ...workorderTools,
                      moduleName,
                    })
                  }
                },
              },
            },
            {
              label: this.$t('common.inventory.select_reserved_tools'),
              value: {
                lookupModuleName: 'inventoryReservation',
                viewname: 'all-tools',
                lookupModuleDisplayName: this.$t(
                  'common.inventory.reserved_tools'
                ),
                additionalFilters: {
                  workOrder: {
                    operatorId: 9,
                    value: [`${workOrderId}`],
                  },
                  reservationStatus: {
                    operatorId: 10,
                    value: [`${3}`],
                  },
                  toolType: {
                    operatorId: 2,
                  },
                },
                getRecordDetails: async payload => {
                  let { id, moduleName } = payload || {}
                  this.inventoryReservationId = !isEmpty(id) ? id : null
                  let queryParam = {
                    reservationId: id,
                  }
                  let { data, error } = await API.get(
                    '/v3/workorderTools/getWorkorderToolFromReservation',
                    queryParam
                  )
                  if (error) {
                    this.$message.error(
                      error.message || this.$t('common._common.error_occured')
                    )
                  } else {
                    let { workorderTools } = data || {}
                    let { toolType } = workorderTools || {}
                    let { isRotating } = toolType || {}
                    this.isItemTypeRotating = isRotating
                    return new CustomModuleData({
                      ...workorderTools,
                      moduleName,
                    })
                  }
                },
              },
            },
          ]
        }
        emptyStateBtnList.push({
          label: this.$t('common.inventory.select_issued_tool'),
          value: {
            lookupModuleName: 'toolTransactions',
            lookupModuleDisplayName: this.$t('common.inventory.issued_tools'),
            viewname: 'issued-tools',
            additionalParams: {
              siteId,
              showToolsForIssue: true,
            },
            getRecordDetails: async payload => {
              let { id, moduleName } = payload || {}
              this.parentTransactionId = !isEmpty(id) ? id : null
              let { workorder } = this
              let queryParam = {
                toolTransactionId: id,
                workOrderId: workorder?.id,
              }
              let { data, error } = await API.get(
                '/v3/workorderTools/getWorkOrderToolFromIssuedTool',
                queryParam
              )
              if (error) {
                this.$message.error(
                  error.message || this.$t('common._common.error_occured')
                )
              } else {
                let { workorderTools } = data || {}
                let { requestedLineItem, remainingQuantity } =
                  workorderTools || {}
                this.requestedLineItem = requestedLineItem
                this.remainingQuantity = remainingQuantity
                return new CustomModuleData({
                  ...workorderTools,
                  moduleName,
                })
              }
            },
          },
        })
      } else if (moduleName === 'workorderService') {
        if (this.checkWoManageInventoryPermission()) {
          emptyStateBtnList = [
            {
              label: this.$t('common.inventory.select_service'),
              value: {
                lookupModuleName: 'service',
                lookupModuleDisplayName: this.$t('common.inventory._services'),
                getRecordDetails: async payload => {
                  let { id, moduleName } = payload || {}
                  let { workorder } = this
                  let workOrderId = this.$getProperty(workorder, 'id')
                  let queryParam = {
                    serviceId: id,
                    workOrderId,
                  }
                  let { data, error } = await API.get(
                    '/v3/workorderService/getWorkorderService',
                    queryParam
                  )
                  if (error) {
                    this.$message.error(
                      error.message || this.$t('common._common.error_occured')
                    )
                  } else {
                    let { workorderService } = data || {}
                    let { service } = workorderService || {}
                    let { paymentTypeEnum } = service || {}
                    if (paymentTypeEnum === 'DURATION_BASED') {
                      this.isServiceDurationBased = true
                    }
                    return new CustomModuleData({
                      ...workorderService,
                      moduleName,
                    })
                  }
                },
              },
            },
            {
              label: this.$t('common.inventory.select_planned_services'),
              value: {
                lookupModuleName: 'workOrderPlannedServices',
                lookupModuleDisplayName: this.$t(
                  'common.header.planned_services'
                ),
                additionalFilters: {
                  workOrder: {
                    operatorId: 9,
                    value: [`${workOrderId}`],
                  },
                },
                getRecordDetails: async payload => {
                  let { id, moduleName } = payload || {}
                  let queryParam = {
                    plannedServiceId: id,
                  }
                  let { data, error } = await API.get(
                    '/v3/workOrderPlannedServices/forActuals',
                    queryParam
                  )
                  if (error) {
                    this.$message.error(
                      error.message || this.$t('common._common.error_occured')
                    )
                  } else {
                    let { workorderService } = data || {}
                    return new CustomModuleData({
                      ...workorderService,
                      moduleName,
                    })
                  }
                },
              },
            },
          ]
        }
      }

      return emptyStateBtnList
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
      this.resetData()
      this.actionButtonValue = value
    },
    resetData() {
      this.inventoryReservationId = null
      this.requestedLineItem = null
      this.remainingQuantity = null
      this.parentTransactionId = null
      this.actionButtonValue = null
      this.isServiceDurationBased = false
    },
    refreshData() {
      eventBus.$emit('reloadWorkorderActualsCost')
    },
    isIssuedToolsAddition() {
      let { actionButtonValue: { lookupModuleName } = {} } = this
      return !isEmpty(lookupModuleName)
        ? lookupModuleName === 'toolTransactions'
        : false
    },
    getNonRotatingItems(items) {
      let nonRotatingItems = (items || {}).filter(item => {
        let { itemType } = item || {}
        let { rotating } = itemType || {}
        return !rotating
      })
      return nonRotatingItems
    },
    inventoryRequestDropdownAction(action) {
      if (action === 'create_inventory_request') {
        this.redirectToFormCreation()
      } else if (action === 'view_inventory_request') {
        this.openList()
      }
    },
    redirectToFormCreation() {
      let { invReqModuleName, $router, workorder } = this
      let { id, localId } = workorder || {}
      let route
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(invReqModuleName, pageTypes.CREATE) || {}
        if (name) {
          route = $router.resolve({
            name,
            query: {
              requestedFromWorkOrder: true,
              woId: JSON.stringify(id),
              woLocalId: JSON.stringify(localId),
            },
          })
        }
      } else {
        route = $router.resolve({
          name: 'inventoryrequest-create',
          params: {
            invReqModuleName,
          },
          query: {
            requestedFromWorkOrder: true,
            woId: JSON.stringify(id),
            woLocalId: JSON.stringify(localId),
          },
        })
      }
      route = route.href || {}
      route && window.open(route, '_blank')
    },
    openList() {
      let { viewname, invReqModuleName, $router } = this
      let { workorder } = this
      let { id } = workorder || {}
      let filters = {
        workorder: { operatorId: 36, value: [`${id}`] },
      }
      let query = {
        includeParentFilter: 'true',
        search: JSON.stringify(filters),
      }
      let route
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(invReqModuleName, pageTypes.LIST) || {}
        if (name) {
          route = $router.resolve({
            name,
            params: {
              viewname,
            },
            query,
          })
        }
      } else {
        route = $router.resolve({
          name: 'inventoryrequest',
          params: { viewname },
          query,
        })
      }
      route = route.href || {}
      route && window.open(route, '_blank')
    },
  },
}
</script>
