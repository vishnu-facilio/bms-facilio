import $helpers from 'util/helpers'
import $constants from 'util/constant'
import { isEmpty } from '@facilio/utils/validation'

function addParam(workflow, name, type) {
  if (!workflow.parameters) {
    workflow.parameters = []
  }
  let params = workflow.parameters
  if (!params.find(param => param.name === name)) {
    params.push({ name: name, typeString: type || 'String' })
  }
}

function setMailTemplateExpression(
  ids,
  mailTemplate,
  mailKey,
  mode,
  module,
  isFtl
) {
  mailTemplate[mailKey] = ''
  ids = ids.filter((id, i) => {
    if (isNaN(id)) {
      if (id.includes('notif_mod_audience')) {
        mailTemplate.toType = 'audience'
        return true
      }
      mailTemplate[mailKey] += id
      if (i + 1 !== ids.length) {
        mailTemplate[mailKey] += ','
      }
      if (!id.startsWith('${cs.')) {
        let value = id.replace(/[:\\-]/g, '')
        if (
          !mailTemplate.workflow.expressions.find(exp => exp.constant === value)
        ) {
          let name = value.replace(/[${}]/g, '')
          mailTemplate.workflow.expressions.push({
            name: name,
            constant: value,
          })
          mailTemplate.workflow.parameters.push({
            name: name,
            typeString: 'String',
          })
        }
      }
      return false
    }
    return true
  })

  let roleFunc = {
    mail: 'getRoleEmails',
    sms: 'getRolePhone',
    phonecall: 'getRolePhoneCall',
    whatsapp: 'getRoleWhatsapp',
    mobile: 'getRoleOuids',
  }

  // TODO remove expressions for mobile to
  for (let i = 0; i < ids.length; i++) {
    let lastCharIndex = mailTemplate[mailKey].length - 1

    if (
      i === 0 &&
      mailTemplate[mailKey] &&
      mailTemplate[mailKey][lastCharIndex] !== ','
    ) {
      mailTemplate[mailKey] += ','
    }

    let expName =
      (mailKey === 'cc' ? 'cc_' : '') +
      (mailKey === 'bcc' ? 'bcc_' : '') +
      'user_' +
      (mode ? ExpKeys[mode] : 'email') +
      (i + 1)

    if (mailTemplate.toType === 'audience') {
      expName = ids[i]
    }

    mailTemplate[mailKey] += '$' + '{' + expName
    if (isFtl) {
      mailTemplate[mailKey] += '!}'
    } else {
      mailTemplate[mailKey] += ':-}'
    }

    if (i + 1 !== ids.length) {
      mailTemplate[mailKey] += ','
    }
    if (mailTemplate.toType === 'role') {
      let roleId = ids[i]

      mailTemplate.workflow.expressions.push({
        name: expName,
        defaultFunctionContext: {
          nameSpace: 'system',
          functionName: roleFunc[mode],
          params: roleId + ',${' + module + '.resource.id}',
        },
      })
      addParam(mailTemplate.workflow, module + '.resource.id', 'Number')
    } else if (mailTemplate.toType === 'audience') {
      let audienceFieldName = ids[i].substring(
        0,
        ids[i].indexOf('.notif_mod_audience')
      )
      mailTemplate.workflow.expressions.push({
        name: expName,
        defaultFunctionContext: {
          nameSpace: 'system',
          functionName: 'getAudienceEmail',
          params:
            `'${audienceFieldName}','${module}','` + '${' + module + ".id}'",
        },
      })
      addParam(mailTemplate.workflow, module + '.id', 'Number')
    } else if (mailTemplate.toType === 'group') {
      mailTemplate.workflow.expressions.push({
        name: expName,
        fieldName: mode
          ? ExpKeys[mode] === 'id'
            ? 'groupId'
            : ExpKeys[mode]
          : 'email',
        moduleName: 'groups',
        aggregateString: '[0]',
        criteria: {
          pattern: '(1)',
          conditions: {
            1: {
              fieldName: 'groupId',
              operatorId: 9,
              sequence: '1',
              value: ids[i],
            },
          },
        },
      })
    } else {
      mailTemplate.workflow.expressions.push({
        name: expName,
        fieldName: mode
          ? ExpKeys[mode] === 'id'
            ? 'ouid'
            : ExpKeys[mode]
          : 'email',
        moduleName: 'users',
        aggregateString: '[0]',
        criteria: {
          pattern: '(1)',
          conditions: {
            1: {
              fieldName: 'ouid',
              operatorId: 9,
              sequence: '1',
              value: ids[i],
            },
          },
        },
      })
    }
  }
}

export default {
  validateEmail(email) {
    let emailRegx = /^(([^<>()\\[\]\\.,;:\s@"]+(\.[^<>()\\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
    if (emailRegx.test(email) === false) {
      return false
    }
    return true
  },
  validateNumberValue(value) {
    let mobileRegx = /^[0-9]+$/
    if (mobileRegx.test(value) === false) {
      return false
    }
    return true
  },
  setUserMailWorkflow(
    ids,
    mailTemplate,
    mode,
    module,
    isFtl,
    ccDetails,
    bccDetails
  ) {
    if (!mailTemplate) {
      mailTemplate = {}
    }

    if (!mailTemplate.workflow) {
      mailTemplate.workflow = {}
    }
    if (!mailTemplate.workflow.parameters) {
      mailTemplate.workflow.parameters = []
    }
    if (!mailTemplate.workflow.expressions) {
      mailTemplate.workflow.expressions = []
    }

    if (isFtl) {
      if (
        !mailTemplate.workflow.parameters.find(param => param.name === 'org')
      ) {
        mailTemplate.workflow.parameters.push({
          name: 'org',
          typeString: 'String',
        })
      }
      if (
        !mailTemplate.workflow.parameters.find(param => param.name === 'user')
      ) {
        mailTemplate.workflow.parameters.push({
          name: 'user',
          typeString: 'String',
        })
      }
    } else if (
      !mailTemplate.workflow.parameters.find(
        param => param.name === 'org.domain'
      )
    ) {
      mailTemplate.workflow.parameters.push({
        name: 'org.domain',
        typeString: 'String',
      })
    }
    mailTemplate.workflow.expressions = mailTemplate.workflow.expressions.filter(
      exp =>
        exp.fieldName !== 'email' &&
        exp.fieldName !== 'ouid' &&
        exp.fieldName !== 'mobile' &&
        exp.moduleName !== 'users' &&
        exp.moduleName !== 'groups' &&
        exp.aggregateString !== '[0]' &&
        exp.name !== 'org.domain'
    )
    if (!isFtl) {
      mailTemplate.workflow.expressions.push({
        name: 'org.domain',
        constant: '$' + '{org.domain}',
      })
    }
    mailTemplate.workflow.workflowString = null

    let mailKey = mode === 'mobile' ? 'id' : 'to'
    setMailTemplateExpression(ids, mailTemplate, mailKey, mode, module, isFtl)
    if (ccDetails) {
      let ccIds = ccDetails.ids
      let ccKey = ccDetails.key
      setMailTemplateExpression(ccIds, mailTemplate, ccKey, mode, module, isFtl)
    }
    if (bccDetails) {
      let bccIds = bccDetails.ids
      let bccKey = bccDetails.key
      setMailTemplateExpression(
        bccIds,
        mailTemplate,
        bccKey,
        mode,
        module,
        isFtl
      )
    }

    return mailTemplate
  },

  getUsersFromTemplate(template, property) {
    let userIds = []
    let key = template.originalTemplate.hasOwnProperty('id') ? 'id' : 'to'
    if (property) {
      key = property
    }
    if (!template.originalTemplate[key]) {
      return userIds
    }
    let userMails = Array.isArray(template.originalTemplate[key])
      ? template.originalTemplate[key]
      : template.originalTemplate[key].split(',')
    if (template.workflow.expressions.length) {
      for (let i = 0; i < template.workflow.expressions.length; i++) {
        let exp = template.workflow.expressions[i]
        if (
          (exp.fieldName === 'email' ||
            exp.fieldName === 'ouid' ||
            exp.fieldName === 'phone') &&
          (exp.moduleName === 'users' || exp.moduleName === 'groups') &&
          exp.aggregateString === '[0]'
        ) {
          if (exp.criteria.conditions && exp.criteria.conditions[1].value) {
            let userId = parseInt(exp.criteria.conditions[1].value)
            if (userIds.indexOf(userId) === -1) {
              if (exp.name.startsWith('bcc_')) {
                if (key === 'bcc') {
                  userIds.push(userId)
                }
              } else if (exp.name.startsWith('cc_')) {
                if (key === 'cc') {
                  userIds.push(userId)
                }
              } else {
                if (['to', 'id'].includes(key)) {
                  userIds.push(userId)
                }
              }
            }
          }
        } else if (
          exp.defaultFunctionContext &&
          [
            'getRoleEmails',
            'getRolePhone',
            'getRoleOuids',
            'getRolePhoneCall',
            'getRoleWhatsapp',
          ].includes(exp.defaultFunctionContext.functionName)
        ) {
          userIds.push(parseInt(exp.defaultFunctionContext.params.split(',')))
          template.toType = 'role'
        } else if (
          !isEmpty(exp) &&
          !isEmpty(exp.defaultFunctionContext) &&
          ['getAudienceEmail'].includes(exp.defaultFunctionContext.functionName)
        ) {
          template.toType = 'audience'
          userIds.push(exp.name)
        } else if (exp.constant) {
          let mail = userMails.find(
            mail => mail.replace(':-', '').replace('!""', '') === exp.constant
          )
          if (mail) {
            userIds.push(mail)
          }
        }
      }
    } else if (
      template.workflow.parameters &&
      template.workflow.parameters.length
    ) {
      template.workflow.parameters.forEach(param => {
        let mail = userMails.find(
          mail =>
            mail.replace(':-', '').replace('!""', '') ===
            '${' + param.name + '}'
        )
        if (mail) {
          userIds.push(mail)
        }
      })
    } else {
      userMails.forEach(mail => {
        let user = this.users.find(user => user.email === mail)
        if (user) {
          userIds.push(parseInt(user.id))
        }
      })
    }
    userMails.forEach(mail => {
      if (isNaN(mail) && mail.startsWith('${cs.')) {
        let placeHolder = mail
        userIds.push(placeHolder)
      }
    })
    if (!userIds.length) {
      userIds = userMails
    }
    return userIds
  },

  getFormulaFieldFromWorkflow(workflow, formula) {
    let formulaField = {
      formulaFieldType: $constants.FormulaFieldType.LIVE_FORMULA,
      triggerType: $constants.FormulaTriggerType.POST_LIVE_READING,
      workflow: workflow,
    }
    if (formula) {
      Object.assign(formulaField, formula)
      if (formulaField.assetCategoryId > 0) {
        formulaField.resourceType =
          $constants.FormulaResourceType.ASSET_CATEGORY
      }
    }
    return formulaField
  },

  getSafeLimitRules(f, categoryId) {
    let min = null
    let max = null
    if (f.safeLimitPattern === 'lesser') {
      max = f.lesserThan
    } else if (f.safeLimitPattern === 'greater') {
      min = f.greaterThan
    } else if (f.safeLimitPattern === 'between') {
      max = f.betweenTo
      min = f.betweenFrom
    }
    let readingRules = []
    if (min !== null || max !== null) {
      readingRules.push({
        id: f.safeLimitId,
        name: 'Safe Limit',
        ruleType: 9,
        event: {
          activityType: 1,
        },
        assetCategoryId: categoryId ? categoryId : -1,
        thresholdType: 5,
        workflow: {
          expressions: [
            {
              name: 'a',
              expr: 'inputValue', // eslint-disable-line no-template-curly-in-string
            },
            {
              name: 'b',
              constant: min === null ? -1 : min,
            },
            {
              name: 'c',
              constant: max === null ? -1 : max,
            },
          ],
          parameters: [
            {
              name: 'inputValue',
              typeString: f.dataType === 2 ? 'Number' : 'Decimal',
            },
          ],
          resultEvaluator: '(b!=-1&&a<b)||(c!=-1&&a>c)',
        },
        actions: !f.raiseSafeLimitAlarm
          ? null
          : [
              {
                actionType: 6,
                templateJson: {
                  fieldMatcher: [
                    {
                      field: 'message',
                      value: `${f.displayName} should be within safe limit`,
                    },
                    {
                      field: 'severity',
                      value: f.safeLimitSeverity,
                    },
                  ],
                },
              },
            ],
      })
    }
    return readingRules
  },

  // Temp
  setExpressionFromPlaceHolder(workflow, str, module) {
    let placeHolders = $helpers.getPlaceholders(str)
    if (!placeHolders.length) {
      return
    }

    if (!workflow.parameters) {
      workflow.parameters = []
    }
    if (!workflow.expressions) {
      workflow.expressions = []
    }
    placeHolders.forEach(placeHolder => {
      if (!workflow.parameters.some(param => param.name === placeHolder)) {
        workflow.parameters.push({ name: placeHolder, typeString: 'String' })
      }
      if (!workflow.expressions.some(param => param.name === placeHolder)) {
        if (module && placeHolder === 'getTransitionPermaLink') {
          this.addTransitionPermaLinkExpr(workflow, placeHolder, module)
        } else {
          workflow.expressions.push({
            name: placeHolder,
            constant: '${' + placeHolder + '}',
          })
        }
      }
    })
  },

  addTransitionPermaLinkExpr(workflow, placeHolder, module) {
    workflow.expressions.push(
      {
        name: placeHolder + 's',
        defaultFunctionContext: {
          nameSpace: 'default',
          functionName: 'getTransitionPermaLink',
          params:
            "'" +
            window.location.protocol +
            '//' +
            window.location.host +
            "'," +
            module +
            ".id,'/link/1','" +
            module +
            "'",
        },
      },
      {
        name: placeHolder,
        defaultFunctionContext: {
          nameSpace: 'list',
          functionName: 'get',
          params: placeHolder + "s, '0'",
        },
      }
    )
    addParam(workflow, module + '.id', 'Number')
  },

  isBacnetController(controller) {
    return (
      [1, 2].includes(controller.controllerType) ||
      controller.instanceNumber > 0
    )
  },
}

const ExpKeys = {
  mail: 'email',
  sms: 'phone',
  whatsapp: 'phone',
  phonecall: 'phone',
  web: 'id',
  mobile: 'id',
}
