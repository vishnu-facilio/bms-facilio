import { isWebTabsEnabled, findTab, tabTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'
export default {
  computed: {
    ...mapGetters({
      getTabByTabId: 'webtabs/getTabByTabId',
      tabHasPermission: 'webtabs/tabHasPermission',
    }),
  },
  methods: {
    checkStoreRoomPermission() {
      let targetModuleName = 'storeRoom'
      let canCreate = false
      let canUpdate = false
      if (isWebTabsEnabled()) {
        let { tabId } = findTab(tabTypes.MODULE, {
          moduleName: targetModuleName,
        })
        if (!isEmpty(tabId)) {
          let webTabObj = this.getTabByTabId(tabId)
          canCreate = this.tabHasPermission('CREATE', webTabObj)
          canUpdate = this.tabHasPermission('UPDATE', webTabObj)
        }
      } else {
        canCreate = this.$hasPermission('inventory:CREATE')
        canUpdate = this.$hasPermission('inventory:UPDATE,UPDATE_OWN')
      }
      return canCreate && canUpdate
    },
    checkWoManageInventoryPermission() {
      let targetModuleName = 'workorder'
      if (isWebTabsEnabled()) {
        let { tabId } = findTab(tabTypes.MODULE, {
          moduleName: targetModuleName,
        })
        if (!isEmpty(tabId)) {
          let webTabObj = this.getTabByTabId(tabId)
          return (
            this.tabHasPermission('MANAGE_INVENTORY', webTabObj) ||
            this.checkStoreRoomPermission()
          )
        }
      } else {
        return (
          this.$hasPermission('workorder:MANAGE_INVENTORY') ||
          this.checkStoreRoomPermission()
        )
      }
      return false
    },
    checkReserveInventoryPermission() {
      let targetModuleName = 'inventoryrequest'
      if (isWebTabsEnabled()) {
        let { tabId } = findTab(tabTypes.MODULE, {
          moduleName: targetModuleName,
        })
        if (!isEmpty(tabId)) {
          let webTabObj = this.getTabByTabId(tabId)
          return (
            this.tabHasPermission('RESERVE_INVENTORY', webTabObj) ||
            this.checkStoreRoomPermission()
          )
        }
      } else {
        return (
          this.$hasPermission('inventoryrequest:RESERVE_INVENTORY') ||
          this.checkStoreRoomPermission()
        )
      }
      return false
    },
  },
}
