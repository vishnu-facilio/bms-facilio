import { SetupData, prop } from '@facilio/data'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export class CurrencyListModel extends SetupData {
  @prop({ primary: true })
  id = null

  @prop({ serialize: null })
  displayName = ''

  @prop({ serialize: null })
  displaySymbol = ''

  @prop({
    deserialize: currency => ({
      exchangeRate: parseFloat(currency.exchangeRate),
    }),

    serialize: exchangeRate => ({
      exchangeRate: `${exchangeRate}`,
    }),
  })
  exchangeRate = 1

  @prop({ serialize: null })
  sysModifiedBy = null

  @prop({ serialize: null })
  status = false

  @prop()
  currencyCode = ''

  @prop({ serialize: null })
  baseCurrency = ''

  @prop()
  decimalPlaces = 1

  static totalCount = null
  static moduleName = 'multicurrency'

  static async fetchAllRecords(payload) {
    let url = `v2/${this.moduleName}/list`
    let { error, data } = await API.get(url, payload)
    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      let result = (data?.currencies || []).map(currency => {
        let { sysModifiedBy } = currency
        if (!isEmpty(sysModifiedBy)) {
          let { sysModifiedBy: modifiedBy } = data?.supplements || {}
          if (!isEmpty(modifiedBy[sysModifiedBy])) {
            currency = {
              ...currency,
              sysModifiedBy: modifiedBy[sysModifiedBy]?.name,
            }
          }
        }
        return currency
      })
      return { data: result || {} }
    }
  }
  static async fetchCount(params) {
    let url = `v2/multicurrency/list/count`
    let { error, data } = await API.get(url, params)

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      return data?.count || null
    }
  }

  async saveRecord(serializedData) {
    let url = 'v2/multicurrency/addOrUpdate'
    return await API.post(url, { currency: serializedData })
  }

  async patchUpdate(payload) {
    let url = 'v2/multicurrency/updateStatus'
    return await API.post(url, payload)
  }

  static async loadMetrics() {
    let { error, data } = await API.get('v2/multicurrency/list/filter')

    if (error) {
      throw new Error(error.message || 'Error Occurred')
    } else {
      return data?.currencies || []
    }
  }
}
