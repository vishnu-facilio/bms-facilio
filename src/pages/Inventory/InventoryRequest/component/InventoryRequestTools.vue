<script>
import InventoryRequestLineItemsBase from 'src/pages/Inventory/InventoryRequest/component/InventoryRequestLineItemsBase.vue'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData.js'

export default {
  extends: InventoryRequestLineItemsBase,
  data() {
    return {
      inventoryType: 2,
    }
  },
  computed: {
    moduleDisplayName() {
      return this.$t('common.inventory._tools')
    },

    inventoryTypeName() {
      return this.$t('common.header.tool')
    },
    viewname() {
      return 'all-tools'
    },
    formConfig() {
      return {
        formType: 'POP_UP_FORM',

        modifyFieldPropsHook: field => {
          let { name } = field || {}
          if (name === 'toolType') {
            return { ...field, isDisabled: true, required: true }
          }
          if (name === 'itemType') {
            return { ...field, hideField: true }
          }
        },
      }
    },

    emptyStateBtnList() {
      if (this.canEditOrDelete() && this.hasEditPermission()) {
        return [
          {
            label: this.$t('common.inventory.select_tool'),
            value: {
              lookupModuleName: 'toolTypes',
              lookupModuleDisplayName: this.$t('common.inventory._tool_types'),
              getRecordDetails: async payload => {
                let { id, moduleName } = payload || {}
                let inventoryRequestLineItems = {
                  toolType: {
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
      return this.$getProperty(val, 'toolType.name', '---')
    },
  },
}
</script>
