export default {
  data() {
    return {
      alarmFields: [],
      selectedMetric: null,
      numberOperators: [
        {
          operator: 9,
          label: 'Equals',
          key: '==',
        },
        {
          operator: 10,
          label: 'Not Equals',
          key: '!=',
        },
        {
          operator: 13,
          label: 'Greater Than',
          key: '>',
        },
        {
          operator: 14,
          label: 'Greater Than Equals',
          key: '>=',
        },
        {
          operator: 11,
          label: 'Less Than',
          key: '<',
        },
        {
          operator: 12,
          label: 'Less Than Equals',
          key: '<=',
        },
      ],
      booleanOperators: [
        {
          operator: 15,
          label: 'is',
          key: '==',
        },
      ],
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  methods: {
    loadThresholdFields(isMultiResource, assetCategoryId, resource) {
      if (isMultiResource) {
        if (assetCategoryId > 0) {
          this.thresholdFields = []
          this.$store
            .dispatch('formulabuilder/loadAssetReadings', {
              assetCategoryId: assetCategoryId,
            })
            .then(() => {
              this.thresholdFields = this.$store.getters[
                'formulabuilder/getAssetReadings'
              ](assetCategoryId, true)
              this.ruleCondition()
            })
        }
      } else {
        this.thresholdFields = []
        this.$util.loadReadingFields(resource).then(fields => {
          this.thresholdFields = fields
        })
      }
    },
    setMetricModule(field, setObject) {
      this.thresholdFields.filter(d => {
        if (d.id === field) {
          setObject.selectedMetric = d
          setObject.metricFieldName = d.name
          setObject.event.moduleId = d.moduleId
        }
      })
    },
    loadAlarmFields() {
      this.$util.loadFields('alarm', true).then(fields => {
        this.alarmFields = fields
      })
    },
    parseAction(action) {
      let impactsMessage = {
        actionType: 6,
        templateJson: {
          fieldMatcher: [],
        },
      }
      let matcher = []
      Object.keys(action).filter(key => {
        matcher.push({
          field: key,
          value: action[key] ? action[key] : null,
        })
      })
      impactsMessage.templateJson.fieldMatcher = matcher
      return impactsMessage
    },
    getOperatorSymbol: function(operatorId) {
      let operator = [...this.numberOperators, ...this.booleanOperators].find(
        operator => operatorId === operator.operator
      )
      return operator ? operator.key : ''
    },
    simpleConditionParse(readingRule, metricFieldObject) {
      let isExpressionValue = readingRule.percentage === '$' + '{previousValue}'
      if (
        !readingRule.hasOwnProperty('occurences') ||
        readingRule.occurences <= 0
      ) {
        readingRule.occurences =
          readingRule.occurences > 0 ? readingRule.occurences : null
        readingRule.workflow = {
          expressions: [],
          resultEvaluator: '',
          parameters: [
            {
              name: 'resourceId',
              typeString: 'Number',
            },
          ],
        }
        readingRule.workflow.expressions.push({
          name: 'a',
          aggregateString: 'lastValue',
          fieldName: metricFieldObject.name,
          moduleName: metricFieldObject.module.name,
          aggregateCondition: [],
          criteria: {
            pattern: '(1)',
            conditions: {
              1: {
                fieldName: 'parentId',
                operatorId: 9,
                sequence: '1',
                value: this.isMultiResource
                  ? '${resourceId' + '}'
                  : this.selectedResourceList.id,
              },
            },
          },
        })
        delete readingRule.criteria
        readingRule.workflow.resultEvaluator =
          'a' +
          this.getOperatorSymbol(readingRule.operatorId) +
          (isExpressionValue ? 'previousValue' : readingRule.percentage)
      } else {
        delete readingRule.criteria
        readingRule.workflow = {
          expressions: [],
          resultEvaluator: '',
          parameters: [
            {
              name: 'resourceId',
              typeString: 'Number',
            },
          ],
        }
        readingRule.workflow.expressions.push({
          name: 'a',
          aggregateString: 'count',
          fieldName: metricFieldObject.name,
          moduleName: metricFieldObject.module.name,
          aggregateCondition: [],
          criteria: {
            pattern: '(1)',
            conditions: {
              1: {
                fieldName: 'parentId',
                operatorId: 9,
                sequence: '1',
                value: this.isMultiResource
                  ? '${resourceId' + '}'
                  : this.selectedResourceList.id,
              },
            },
          },
        })
        readingRule.workflow.expressions[0].aggregateCondition.push({
          fieldName: metricFieldObject.name,
          operatorId: readingRule.operatorId,
          sequence: '1',
          value: readingRule.percentage,
        })
        if (readingRule.consecutiveoroverperiod === 2) {
          readingRule.workflow.expressions[0].aggregateString = 'count'
          readingRule.workflow.expressions[0].orderByFieldName = 'ttime'
          readingRule.workflow.expressions[0].sortBy = 'desc'
          readingRule.workflow.expressions[0].limit = readingRule.occurences
        } else {
          readingRule.workflow.expressions[0].criteria.pattern = '(1 and 2)'
          readingRule.workflow.expressions[0].criteria.conditions['2'] = {
            fieldName: 'ttime',
            operatorId: 42,
            sequence: '2',
            value: readingRule.overperiod + ',${ttime}', // eslint-disable-line no-template-curly-in-string
          }
          this.addParam('ttime', 'Number', readingRule)
        }
        readingRule.percentage = readingRule.occurences
        readingRule.workflow.resultEvaluator = 'a==' + readingRule.occurences
      }
    },
    aggregationParse(readingRule, metricFieldObject) {
      readingRule.workflow = {
        expressions: [],
        resultEvaluator: '',
        parameters: [
          {
            name: 'resourceId',
            typeString: 'Number',
          },
        ],
      }
      readingRule.workflow.expressions.push({
        name: 'a',
        aggregateString: readingRule.aggregation,
        fieldName: metricFieldObject.name,
        moduleName: metricFieldObject.module.name,
        criteria: {
          pattern: '(1 and 2)',
          conditions: {
            1: {
              fieldName: 'parentId',
              operatorId: 9,
              sequence: '1',
              value: this.isMultiResource
                ? '${resourceId' + '}'
                : this.selectedResourceList.id,
            },
            2: {
              fieldName: 'ttime',
              operatorId: 42,
              sequence: '2',
              value: readingRule.dateRange + ',${ttime}', // eslint-disable-line no-template-curly-in-string
            },
          },
        },
        aggregateCondition: [],
      })
      this.addParam('ttime', 'Number', readingRule)
      if (readingRule.aggregation === 'ccount') {
        readingRule.workflow.expressions[0].aggregateString = 'count'
        readingRule.workflow.expressions[0].orderByFieldName = 'ttime'
        readingRule.workflow.expressions[0].sortBy = 'desc'
        readingRule.workflow.expressions[0].limit = readingRule.percentage
      }
      readingRule.workflow.resultEvaluator =
        'a' +
        this.getOperatorSymbol(readingRule.operatorId) +
        readingRule.percentage
    },
    baseLineParse(readingRule, metricFieldObject) {
      readingRule.workflow = {
        expressions: [],
        resultEvaluator: '',
        parameters: [
          {
            name: 'resourceId',
            typeString: 'Number',
          },
        ],
      }
      let baselineExp = {
        name: 'a',
        aggregateString: readingRule.aggregation,
        fieldName: metricFieldObject.name,
        moduleName: metricFieldObject.module.name,
        criteria: {
          pattern: '(1 and 2)',
          conditions: {
            1: {
              fieldName: 'parentId',
              operatorId: 9,
              sequence: '1',
              value: this.isMultiResource
                ? '${resourceId' + '}'
                : this.selectedResourceList.id,
            },
            2: {
              fieldName: 'ttime',
              operatorId: 42,
              sequence: '2',
              value: readingRule.dateRange + ',${ttime}', // eslint-disable-line no-template-curly-in-string
            },
          },
        },
        conditionSeqVsBaselineId: {
          2: readingRule.baselineId,
        },
      }
      this.addParam('ttime', 'Number', readingRule)
      readingRule.workflow.expressions.push(baselineExp)

      let baselineExpB = this.$helpers.cloneObject(baselineExp)
      baselineExpB.name = 'b'
      baselineExpB.conditionSeqVsBaselineId = null
      readingRule.workflow.expressions.push(baselineExpB)

      readingRule.workflow.resultEvaluator =
        '((b-a)/a)*100' +
        this.getOperatorSymbol(readingRule.operatorId) +
        readingRule.percentage
    },
    addParam(name, type, data) {
      if (!data.workflow.parameters) {
        data.workflow.parameters = []
      }
      let params = data.workflow.parameters
      if (!params.find(param => param.name === name)) {
        params.push({
          name: name,
          typeString: type || 'String',
        })
      }
    },
    parseCondition(rule) {
      if (rule) {
        rule.readingFieldId =
          rule.readingFieldId < 0 ? null : rule.readingFieldId
        if (rule.readingFieldId > 0) {
          this.setMetricModule(rule.readingFieldId, rule)
        }
        if (rule.thresholdType === 4) {
          rule.minFlapValue = rule.minFlapValue > 0 ? rule.minFlapValue : null
          rule.maxFlapValue = rule.maxFlapValue > 0 ? rule.maxFlapValue : null
          rule.flapInterval = rule.flapInterval / (60 * 1000)
          rule.flapFrequency =
            rule.flapFrequency > 0 ? rule.flapFrequency : null
        } else if (rule.thresholdType === 1) {
          rule.occurences = rule.occurences <= 0 ? null : rule.occurences
          rule.operatorId = rule.operatorId <= 0 ? null : rule.operatorId
          if (rule.workflow) {
            if (rule.occurences) {
              rule.occurences = rule.percentage < 0 ? null : rule.percentage
              rule.percentage =
                rule.workflow.expressions[0].aggregateCondition &&
                rule.workflow.expressions[0].aggregateCondition[0]
                  ? rule.workflow.expressions[0].aggregateCondition[0].value
                  : null
              if (rule.workflow.expressions[0].criteria.conditions['2']) {
                rule.consecutiveoroverperiod = 1
                rule.overperiod = parseInt(
                  rule.workflow.expressions[0].criteria.conditions['2'].value
                )
              } else {
                rule.consecutiveoroverperiod = 2
              }
            } else {
              rule.occurences = null
              rule.overPeriod = null
              rule.consecutiveoroverperiod = null
            }
          }
        } else if (rule.thresholdType === 5 || rule.thresholdType === 6) {
          rule.dateRange =
            rule.dateRange && rule.dateRange !== -1 ? rule.dateRange : -1
          if (!rule.workflow) {
            rule.workflow = {
              occurences: {},
            }
          }
          rule.workflow.occurences = {
            occurences: rule.occurences,
            overPeriod: rule.overPeriod,
            consecutive: rule.consecutive,
          }
        } else if (rule.thresholdType === -1) {
          rule.thresholdType = 1
          rule.occurences = null
          rule.operatorId = null
        }
        delete rule.matchedResources
        delete rule.operator
      }
    },
  },
}
