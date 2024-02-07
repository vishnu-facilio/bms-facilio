import { displayTypeProp } from '@facilio/data'
import setProperty from 'dset'
import getProperty from 'dlv'
import { isEmpty, isArray } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
import Constants from 'util/constant'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData'
import helpers from 'src/util/helpers'

export class BudgetModuleData extends CustomModuleData {
  @displayTypeProp({
    deserialize: (fieldObj, instance) => {
      let { budgetAmountList } = instance
      let incomes = (budgetAmountList || []).filter(
        amtObj => amtObj.amountType === 1
      )
      let expenses = (budgetAmountList || []).filter(
        amtObj => amtObj.amountType === 2
      )
      let { budgetIncomeDefaults, budgetExpenseDefaults } = Constants
      let fieldValue = {
        incomes: !isEmpty(incomes)
          ? incomes
          : [cloneDeep(budgetIncomeDefaults)],
        expenses: !isEmpty(expenses)
          ? expenses
          : [cloneDeep(budgetExpenseDefaults)],
      }

      setProperty(fieldObj, 'value', fieldValue)
      return fieldObj
    },
    serialize: (finalObj, field, formModel) => {
      let { name } = field || {}
      let { currencyCode, exchangeRate } = formModel || {}
      finalObj[name] = formModel[name]
      finalObj['currencyCode'] = currencyCode
      finalObj['exchangeRate'] = exchangeRate

      return finalObj
    },
  })
  BUDGET_AMOUNT

  afterSerialize({ data }) {
    let { budgetamount } = data
    if (!isEmpty(budgetamount)) {
      let { incomes, expenses } = budgetamount

      if (isArray(incomes)) {
        incomes.forEach(element => {
          let momentObj = this.getStartValue(data)
          element.monthlyAmountSplitUp.forEach(split => {
            setProperty(split, 'startDate', momentObj.valueOf())
            momentObj.add(1, 'month')
          })
        })
        if (
          incomes.length === 1 &&
          isEmpty(getProperty(incomes, '0.account.id'))
        ) {
          incomes = []
        }
      }
      if (isArray(expenses)) {
        expenses.forEach(element => {
          let momentObj = this.getStartValue(data)
          element.monthlyAmountSplitUp.forEach(split => {
            setProperty(split, 'startDate', momentObj.valueOf())
            momentObj.add(1, 'month')
          })
        })
        if (
          expenses.length === 1 &&
          isEmpty(getProperty(expenses, '0.account.id'))
        ) {
          expenses = []
        }
      }
      setProperty(data, 'budgetamount', [...incomes, ...expenses])
    }
    return data
  }

  getStartValue(data) {
    return helpers
      .getOrgTimeMoment()
      .year(2018 + Number(data.fiscalYearStart) || 2020)
      .month(!isEmpty(data.fiscalYear) ? Number(data.fiscalYear) - 1 : 0)
      .startOf('month')
  }
}
