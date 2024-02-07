<script>
import { isEmpty, isArray } from '@facilio/utils/validation'
import { isMultiLookupField } from '/src/util/field-utils.js'
import isEqual from 'lodash/isEqual'

export default {
  methods: {
    getUserName(id) {
      if (id) {
        let user = null
        user = (this.users || []).find(us => us.ouid === id)

        if (user) {
          return user.name
        } else if (!isEmpty(this.portalUserList)) {
          user = (this.portalUserList || []).find(u => u.ouid === id)
          if (user) {
            return user.name
          } else {
            return '---1'
          }
        } else {
          return '---2'
        }
      } else {
        return '---3'
      }
    },
    getAttachmentsString(obj) {
      let str = ''
      for (let item = 0; item < (obj || []).length; item++) {
        if (item !== 0) {
          if (item === obj.length - 1) {
            str += ' and '
          } else {
            str += ', '
          }
        }
        str += obj[item]
      }
      return str
    },
    getAssignedBy(updatedFields) {
      let user = (updatedFields || []).find(el => el.fieldName === 'assignedTo')
      if (user.newValue) {
        return this.getUser(user.newValue.id).name
      } else if (user.oldValue) {
        return this.getUser(user.oldValue.id).name
      }
    },
    getLocation(value) {
      return this.$helpers.parseLocation(value)
    },
    getMessage(activity) {
      let { info } = activity || {}
      let { module, fieldsMap } = this
      if (module === 'newreadingalarm') {
        return this.processAlarmActivity(activity)
      } else if ([63, 64, 70, 71].includes(activity.type)) {
        return this.formatQuotationActivity(activity)
      } else if (activity.type === 75 && !isEmpty(info.changeSet)) {
        let filteredArr = info.changeSet.filter(a => {
          let { field: fieldName, newValue, oldValue } = a || {}
          let field = fieldsMap[fieldName] || {}
          let isMultiLookupChanged = true
          if (isMultiLookupField(field)) {
            isMultiLookupChanged =
              (newValue || []).length !== (oldValue || []).length
          }

          return (
            isMultiLookupChanged &&
            a.field !== 'modifiedTime' &&
            a.newValue !== -99 &&
            !(isEmpty(a.oldValue) && isEmpty(a.newValue)) &&
            !isEqual(a.oldValue, a.newValue) &&
            a.field !== 'moduleState' &&
            a.field !== 'sysModifiedTime'
          )
        })
        if (!isEmpty(filteredArr)) {
          return {
            message:
              '<b>updated </b>' +
              filteredArr.reduce((accStr, activity) => {
                return isEmpty(accStr)
                  ? this.getValue(activity)
                  : `${accStr}, ${this.getValue(activity)}`
              }, ''),
          }
        } else {
          return { message: null }
        }
      } else {
        // 64,66,69,71,72 Common Activities for Comment, Attachments, Status Update, Approval, Approval Entry respectively
        // use same for other modules
        if (activity.type === 65) {
          return {
            message:
              '<b> added the Comment </b>' +
              '<i>' +
              '(' +
              this.$getProperty(activity, 'info.Comment', '') +
              ')' +
              '</i>',
          }
        } else if ((activity || {}).type === 66) {
          return {
            message:
              '<b>' +
              'attached ' +
              '</b>' +
              '<i>' +
              this.$getProperty(
                activity,
                ['info', 'attachment', 0, 'Filename'],
                ''
              ) +
              '</i>',
          }
        } else if ((activity || {}).type === 69) {
          return {
            message:
              '<b>' +
              'updated status ' +
              '</b>' +
              'from ' +
              '<i>' +
              this.$getProperty(activity, 'info.oldValue', '') +
              ' to ' +
              this.$getProperty(activity, 'info.newValue', '') +
              '</i>',
          }
        } else if ((activity || {}).type === 72) {
          // Approval Activity Message
          let { metaInfo } = this
          let { module } = metaInfo || {}
          let { displayName } = module || {}
          if (isEmpty(displayName)) {
            displayName = module
          }
          let approvalMessage = ``
          let approvalStatus = this.$getProperty(activity, 'info.status', '')
          let moduleSingularDisplayName = this.$constants
            .moduleSingularDisplayNameMap[module]
            ? this.$constants.moduleSingularDisplayNameMap[module]
            : displayName
          if (approvalStatus === 'Approved') {
            approvalMessage = `<b> approved the ${moduleSingularDisplayName}</b>`
          } else if (approvalStatus === 'Rejected') {
            approvalMessage = `<b> rejected the ${moduleSingularDisplayName}</b>`
          } else if (approvalStatus === 'Requested') {
            approvalMessage = `<b> resent for Approval.</b>`
          }
          return {
            message: approvalMessage,
          }
        } else if ((activity || {}).type === 73) {
          return {
            message: `<b> initiated Approval process</b>`,
          }
        } else if ((activity || {}).type === 109) {
          let employee = (info?.changeSet || []).find(
            a =>
              a.field === 'employee' &&
              a.newValue > 0 &&
              a.oldValue !== a.newValue
          )
          if (employee) {
            return {
              message: `<b> assigned <i>${info.empName}</i> to desk <i>${info.deskName}</i> </b>`,
            }
          } else {
            return { message: null }
          }
        } else if ((activity || {}).type === 110) {
          let employee = (info?.changeSet || []).find(
            a =>
              a.field === 'employee' &&
              a.oldValue > 0 &&
              a.oldValue !== a.newValue
          )
          if (employee) {
            return {
              message: `<b> unassigned <i>${info.empName}</i> from desk <i>${info.deskName}</i> </b>`,
            }
          } else {
            return { message: null }
          }
        }
      }
      return { message: '<b>' + activity.message + '</b>' }
    },
    getValue(info) {
      let { fieldsMap } = this
      let { field: fieldName, newValue, oldValue } = info || {}
      let field = fieldsMap[fieldName] || {}
      let displayValue = newValue
      let displayOldValue = oldValue
      if (!isEmpty(field)) {
        if (!isArray(displayValue)) {
          if (Number(displayValue)) {
            displayValue = Number(displayValue)
          }
        }
        displayValue = this.$fieldUtils.getDisplayValue(field, displayValue)

        if (!isArray(displayOldValue)) {
          if (Number(displayOldValue)) {
            displayOldValue = Number(displayOldValue)
          }
        }
        if (!isEmpty(displayOldValue)) {
          if (isMultiLookupField(field)) {
            let lookupRecordNames = (displayOldValue || []).map(
              currRecord =>
                currRecord.displayName || currRecord.name || currRecord.subject
            )
            if (lookupRecordNames.length > 5) {
              displayOldValue = `${lookupRecordNames
                .slice(0, 5)
                .join(', ')} +${Math.abs(lookupRecordNames.length - 5)}`
            } else {
              displayOldValue = !isEmpty(lookupRecordNames)
                ? `${lookupRecordNames.join(', ')}`
                : '---'
            }
          } else {
            displayOldValue = this.$fieldUtils.getDisplayValue(
              field,
              displayOldValue
            )
          }
        }
      }
      if (!isEmpty(displayOldValue) && displayOldValue !== '---') {
        return (
          '<b>' +
          info.displayName +
          '</b>' +
          ' from ' +
          '<i>' +
          displayOldValue +
          '</i>' +
          ' to ' +
          '<i>' +
          displayValue +
          '</i>'
        )
      }
      return (
        '<b>' +
        info.displayName +
        '</b>' +
        ' as ' +
        '<i>' +
        displayValue +
        '</i>'
      )
    },
    processAlarmActivity(activity) {
      if (activity.type === 50) {
        return { message: '<b>created alarm occurrence </b>' }
      } else if (activity.type === 51) {
        return { message: '<b> created workorder </b>' }
      } else if (activity.type === 53) {
        return {
          message: '<b> Acknowledged Alarm </b>',
        }
      } else if (activity.type === 54) {
        return {
          message:
            '<b> added the Comment </b>' +
            '<i>' +
            '(' +
            activity.infoJsonStr.Comment +
            ')' +
            '</i>',
        }
      } else if (activity.type === 56) {
        return {
          message: '<b> alarm occurrence cleared </b>',
        }
      } else if (activity.type === 57) {
        return {
          message: '<b> Auto Cleared </b>',
        }
      }
      return 'Activity'
    },
    formatQuotationActivity(activity) {
      let message = ''
      let val = this.$getProperty(activity, 'info.totalCost', null)
      let amount
      if (val) {
        val = this.$d3.format(',.2f')(val)
        amount =
          this.$currency === '$' || this.$currency === '₹'
            ? this.$currency + val
            : val + ' ' + this.$currency
      }
      if (activity.type === 63) {
        message =
          `<b> Created the Quote for </b>` + (amount ? `<i>${amount}</i>` : ``)
      } else if (activity.type === 64) {
        message =
          `<b> Updated the Quote.</b>` +
          (amount ? `<b> Amount changed to</b> <i>${amount}</i>` : ``)
      } else if (activity.type === 70) {
        message = `<b> Associated Term(s)</b> <i>${this.$getProperty(
          activity,
          'info.termsName'
        )}</i>`
      } else if (activity.type === 71) {
        message = `<b> Disassociated Term(s) </b> <i>${this.$getProperty(
          activity,
          'info.termsName'
        )}</i>`
      }
      return {
        message,
      }
    },
  },
}
</script>
