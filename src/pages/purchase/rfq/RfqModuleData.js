import { displayTypeProp } from '@facilio/data'
import { isEmpty, isArray } from '@facilio/utils/validation'
import setProperty from 'dset'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData'
import { API } from '@facilio/api'

export class RfqModuleData extends CustomModuleData {
  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { requestForQuotationLineItems } = instance || {}

      if (!isEmpty(requestForQuotationLineItems)) {
        requestForQuotationLineItems.forEach(lineItem => {
          if (isEmpty(lineItem.toolType)) {
            lineItem.toolType = { id: null }
          }
          if (isEmpty(lineItem.itemType)) {
            lineItem.itemType = { id: null }
          }
          if (isEmpty(lineItem.service)) {
            lineItem.service = { id: null }
          }
          if (isEmpty(lineItem.unitOfMeasure)) {
            lineItem.unitOfMeasure = { id: null }
          }
          if (isEmpty(lineItem.description)) {
            lineItem.description = ''
          }
        })
      }
      setProperty(fieldObj, 'value', requestForQuotationLineItems)
      return fieldObj
    },

    serialize: (finalObj, field, formModel) => {
      let { name } = field || {}

      finalObj[name] = (formModel[name] || [])
        .filter(lineItem => {
          let { inventoryType, itemType, toolType, quantity, service } =
            lineItem || {}
          let { id: itemId } = itemType || {}
          let { id: toolId } = toolType || {}
          let { id: serviceId } = service || {}
          let hasItemType =
            inventoryType === 1 && !isEmpty(itemId) && !isEmpty(quantity)
          let hasToolType =
            inventoryType === 2 && !isEmpty(toolId) && !isEmpty(quantity)
          let hasService =
            inventoryType === 3 && !isEmpty(serviceId) && !isEmpty(quantity)
          let hasOthers = inventoryType === 4 && !isEmpty(quantity)
          return hasItemType || hasToolType || hasService || hasOthers
        })
        .map(lineItem => {
          let {
            id,
            inventoryType,
            itemType,
            toolType,
            service,
            description,
            unitPrice,
            unitOfMeasure,
            quantity,
          } = lineItem || {}
          let { id: itemId } = itemType || {}
          let { id: toolId } = toolType || {}
          let { id: serviceId } = service || {}
          let lineItemObj = {
            id,
            inventoryType,
            itemType: inventoryType === 1 ? { id: itemId } : null,
            toolType: inventoryType === 2 ? { id: toolId } : null,
            service: inventoryType === 3 ? { id: serviceId } : null,
            quantity,
            description,
            unitPrice,
            unitOfMeasure,
          }
          if (id < 0) {
            delete lineItemObj.id
          }
          return lineItemObj
        })
      finalObj[name] = !isEmpty(finalObj[name]) ? finalObj[name] : null
      return finalObj
    },
  })
  LINEITEMS

  static async fetchRecord({ moduleName, id, force = false, prIds }) {
    if (!isEmpty(prIds)) {
      if (!isEmpty(prIds) && isArray(JSON.parse(prIds))) {
        let queryParam = {
          recordIds: JSON.parse(prIds),
        }
        let { data, error } = await API.get(
          '/v3/requestForQuotation/convertPrToRfq',
          queryParam
        )
        if (error) {
          let { message } = error
          this.$message.error(message)
          return {}
        } else {
          let { requestForQuotation } = data || {}
          return { data: requestForQuotation, moduleName }
        }
      }
      return {}
    } else {
      let { [moduleName]: data, error } = await API.fetchRecord(
        moduleName,
        { id },
        { force }
      )

      if (error) {
        throw new Error(error.message || 'Error Occurred')
      } else {
        return { data: data || {}, moduleName }
      }
    }
  }
}
