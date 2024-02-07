import { isEmpty } from '@facilio/utils/validation'
export default {
  methods: {
    criteriaValidate(LHS, RHS, operator, type) {
      if (LHS && RHS && operator && type) {
        if (type === 'TEXT' || type === 'STRING') {
          if (operator === 'EQUAL') {
            if (LHS === RHS) {
              return true
            }
            return false
          }
          if (operator === 'DOESNT_CONTAIN') {
            if (LHS.indexOf(RHS) === -1) {
              return true
            }
            return false
          }
          if (operator === 'CONTAINS') {
            if (LHS.indexOf(RHS) > -1) {
              return true
            }
            return false
          }
          if (operator === 'EQUAL') {
            if (LHS === RHS) {
              return true
            }
            return false
          }
          if (operator === 'ENDS_WITH') {
            if (LHS.endsWith(RHS)) {
              return true
            }
            return false
          }
          if (operator === 'IS_NOT_EMPTY') {
            if (LHS !== '' || LHS !== null) {
              return true
            } else if (RHS !== '' || RHS !== null) {
              return true
            }
            return false
          }
          if (operator === 'IS_EMPTY') {
            if (LHS === '' || LHS === null) {
              return true
            } else if (RHS === '' || RHS === null) {
              return true
            }
            return false
          }
          if (operator === 'STARTS_WITH') {
            if (LHS.startsWith(RHS)) {
              return true
            }
            return false
          }
          return false
        } else if (type === 'NUMBER') {
          LHS = Number(LHS)
          RHS = Number(RHS)
          if (operator === 'LESS_THAN_EQUAL') {
            if (LHS <= RHS) {
              return true
            }
            return false
          }
          if (operator === 'GREATER_THAN') {
            if (LHS > RHS) {
              return true
            }
            return false
          }
          if (operator === 'LESS_THAN') {
            if (LHS < RHS) {
              return true
            }
            return false
          }
          if (operator === 'EQUAL') {
            if (LHS === RHS) {
              return true
            }
            return false
          }
          if (operator === 'IS_NOT_EMPTY') {
            if (LHS !== undefined) {
              return true
            }
            return false
          }
          if (operator === 'NOT_EQUAL') {
            if (LHS !== RHS) {
              return true
            } else if (RHS !== '' || RHS !== null) {
              return true
            }
            return false
          }
          if (operator === 'GREATER_THAN_EQUAL') {
            if (LHS >= RHS) {
              return true
            } else if (RHS === '' || RHS === null) {
              return true
            }
            return false
          }
          if (operator === 'LESS_THAN_EQUAL') {
            if (LHS <= RHS) {
              return true
            } else if (RHS === '' || RHS === null) {
              return true
            }
            return false
          }
          if (operator === 'IS_EMPTY') {
            if (LHS === null || LHS === undefined) {
              return true
            } else if (RHS === '' || RHS === null) {
              return true
            }
            return false
          }
          if (operator === 'NOT_EQUALS') {
            if (LHS !== RHS) {
              return true
            } else if (RHS === '' || RHS === null) {
              return true
            }
            return false
          }
          return false
        }
        return false
      }
      return false
    },
    serializeCriteria(criteria) {
      for (let condition of Object.keys(criteria.conditions)) {
        let hasValidFieldName =
          criteria.conditions[condition].hasOwnProperty('fieldName') &&
          !isEmpty(criteria.conditions[condition].fieldName)
        if (!hasValidFieldName) {
          delete criteria.conditions[condition]
        } else {
          let discardKeys = [
            'valueArray',
            'operatorsDataType',
            'operatorLabel',
            'operator',
          ]

          discardKeys.forEach(key => {
            delete criteria.conditions[condition][key]
          })
        }
      }
      if (criteria && criteria.conditions) {
        if (Object.keys(criteria.conditions).length === 0) {
          criteria = null
        }
      }
      return criteria
    },
  },
}
