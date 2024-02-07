<script>
import InventoryRequestLineItemsBase from 'src/pages/Inventory/InventoryRequest/component/InventoryRequestLineItemsBase.vue'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData.js'

export default {
  extends: InventoryRequestLineItemsBase,
  data() {
    return {
      inventoryType: 1,
    }
  },
  computed: {
    viewname() {
      return 'all-items'
    },
    moduleDisplayName() {
      return this.$t('common.inventory._items')
    },
    inventoryTypeName() {
      return this.$t('common.header.item')
    },
    formConfig() {
      return {
        formType: 'POP_UP_FORM',

        modifyFieldPropsHook: field => {
          let { name } = field || {}
          if (name === 'itemType') {
            return { ...field, isDisabled: true, required: true }
          }
          if (name === 'toolType') {
            return { ...field, hideField: true }
          }
        },
      }
    },

    emptyStateBtnList() {
      if (this.canEditOrDelete() && this.hasEditPermission()) {
        return [
          {
            label: this.$t('common.inventory.select_item'),
            value: {
              lookupModuleName: 'itemTypes',
              lookupModuleDisplayName: this.$t('common.inventory._item_types'),
              getRecordDetails: async payload => {
                let { id, moduleName } = payload || {}
                let inventoryRequestLineItems = {
                  itemType: {
                    id: id,
                  },
                }
                return new CustomModuleData({
                  ...inventoryRequestLineItems,
                  moduleName,
                })
              },
            },
          },
        ]
      }
      return []
    },
  },
  methods: {
    inventoryName(val) {
      return this.$getProperty(val, 'itemType.name', '---')
    },
  },
}
</script>
