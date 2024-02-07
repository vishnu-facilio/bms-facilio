import { displayTypeProp } from '@facilio/data'
import { isEmpty } from '@facilio/utils/validation'
import setProperty from 'dset'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData'

export class TrModuleData extends CustomModuleData {
  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { transferrequestlineitems } = instance || {}

      if (!isEmpty(transferrequestlineitems)) {
        transferrequestlineitems.forEach(lineItem => {
          if (isEmpty(lineItem.toolType)) {
            lineItem.toolType = { id: null }
          }
          if (isEmpty(lineItem.itemType)) {
            lineItem.itemType = { id: null }
          }
        })
      }
      setProperty(fieldObj, 'value', transferrequestlineitems)
      return fieldObj
    },
    serialize: (finalObj, field, formModel) => {
      let { name } = field || {}

      finalObj[name] = (formModel[name] || [])
        .filter(lineItem => {
          let { inventoryType, itemType, toolType, quantity } = lineItem || {}
          let { id: itemId } = itemType || {}
          let { id: toolId } = toolType || {}
          let hasItemType =
            inventoryType === 1 && !isEmpty(itemId) && !isEmpty(quantity)
          let hasToolType =
            inventoryType === 2 && !isEmpty(toolId) && !isEmpty(quantity)

          return hasItemType || hasToolType
        })
        .map(lineItem => {
          let {
            id,
            inventoryType,
            itemType,
            toolType,
            quantity,
            quantityReceived,
          } = lineItem || {}
          let { id: itemId } = itemType || {}
          let { id: toolId } = toolType || {}

          return {
            id,
            inventoryType,
            itemType: inventoryType === 2 ? null : { id: itemId },
            toolType: inventoryType === 1 ? null : { id: toolId },
            quantity,
            quantityReceived: quantityReceived || 0,
          }
        })
      finalObj[name] = !isEmpty(finalObj[name]) ? finalObj[name] : null
      return finalObj
    },
  })
  INVREQUEST_LINE_ITEMS
}
