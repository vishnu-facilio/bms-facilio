<script>
import { isEmpty, isUndefined } from '@facilio/utils/validation'
import debounce from 'lodash/debounce'
import cloneDeep from 'lodash/cloneDeep'
import { v4 as uuid } from 'uuid'
import { decimalPlaces } from 'pages/card-builder/card-constants'
import isString from 'lodash/isString'
import { API } from '@facilio/api'

export default {
  props: [
    'isNew',
    'savedCardData',
    'cardType',
    'onClose',
    'onGoBack',
    'onCardSave',
    'onCardUpdate',
    'closePopup',
    'cardMeta',
    'isDuplicate',
    'onCardDuplicate',
  ],
  data() {
    return {
      conditionalFormatting: {},
      decimalPlaces,
      scriptModeInt: null,
      customScriptId: null,
      nameSpaces: [],
      enableBoxchecked: false,
    }
  },
  created() {
    this.initializeValidators()

    let { isNew, savedCardData } = this
    if (!isNew && !isEmpty(savedCardData)) {
      this.deserialize(this.savedCardData.cardParams)
      this.deserializeState(this.savedCardData.cardState)
      this.deserializeActions(this.savedCardData.cardDrilldown)
      this.deserializeConditions(this.savedCardData.conditionalFormatting)
      this.deserializeCustomScriptData(this.savedCardData)

      // TODO Revaluate if necessary and model this as computed
      this.setConditionalData(this.cardDataObj)
    }
  },
  watch: {
    cardDataObj: {
      deep: true,
      handler() {
        this.validate() && this.debouncedGetData()
      },
    },
    cardStateObj: {
      deep: true,
      handler() {
        this.validate() && this.debouncedGetData()
      },
    },
    conditionalFormatting: {
      deep: true,
      handler() {
        this.validate() && this.debouncedGetData()
      },
    },
    customScriptId: {
      deep: true,
      handler(newval, oldVal) {
        if (newval !== oldVal) {
          this.debouncedGetData()
        }
      },
    },
  },
  computed: {
    previewState() {
      let { result, cardStateObj } = this
      let { cardContext } = result || {}

      return !isEmpty(result && cardContext)
        ? result.state
          ? result.state
          : this.serializeState(cardStateObj)
        : this.serializeState(cardStateObj)
    },
    componentKey() {
      return uuid()
    },
    variables() {
      let { result } = this
      if (result && result.data) {
        let previewData = result.data
        let { variables } = previewData
        if (variables) {
          return variables
        }
      }
      return []
    },
  },
  methods: {
    setProps() {
      if (this.scriptModeInt === 3 || this.scriptModeInt === null) {
        this.enableBoxchecked = false
      } else if (this.scriptModeInt !== null) {
        this.enableBoxchecked = true
      }
    },
    async loadNameSpaceList() {
      await API.get('/v2/workflow/getNameSpaceListWithFunctions')
        .then(({ data }) => {
          this.nameSpaces = data.workflowNameSpaceList
            ? data.workflowNameSpaceList
            : []
        })
        .catch(() => {
          this.nameSpaces = []
        })
    },
    deserializeCustomScriptData({ customScriptId, scriptModeInt }) {
      this.customScriptId = customScriptId
      this.scriptModeInt = scriptModeInt
    },
    serializeCustomScriptData({ customScriptId, scriptModeInt }) {
      this.customScriptId = customScriptId
      this.scriptModeInt = scriptModeInt
    },
    validate() {
      let hasEmptyField = false
      let customRules = this.validateProperty() || {}
      let cardDataObj = this.serialize()

      this.resourceProps.forEach(prop => {
        let isMap = typeof prop === 'object'
        let propName = isMap ? prop.prop : prop

        if (customRules[propName]) {
          if (customRules[propName](cardDataObj)) {
            hasEmptyField = true
          }
        } else if (isMap && prop.resourceProps) {
          prop.resourceProps.forEach(p => {
            if (
              isEmpty(cardDataObj[propName]) ||
              isEmpty(cardDataObj[propName][p])
            ) {
              hasEmptyField = true
            }
          })
        } else {
          if (isEmpty(cardDataObj[propName])) {
            hasEmptyField = true
          }
        }
      })

      return !hasEmptyField
    },
    changescriptModeInt() {
      let id = []
      if (this.nameSpaces) {
        for (let nameSpace of this.nameSpaces) {
          if (nameSpace.functions !== null) {
            for (let func of nameSpace.functions) {
              id.push(func.id)
            }
          }
        }
      }

      if (this.enableBoxchecked == true) {
        if (id.length) {
          this.customScriptId = id[0]
        }
        this.scriptModeInt = 2
      } else {
        this.customScriptId = -99
        this.scriptModeInt = 3
      }
    },

    validateProperty() {
      // To be overriden in card component if data validation rule for a property
      // needs to be more complex that just an isEmpty() check
      // {
      //   prop1: validatorFn1(data) {
      //      return true when field is invalid or has error
      //   },
      //   prop2: validatorFn2(data) {
      //      return false when data is correct
      //   }
      // }
    },

    validateForm() {
      let form = this.$refs[`${this.cardLayout}_form`]

      return new Promise((resolve, reject) => {
        form.validate(isValid => {
          if (isValid) resolve()
          else reject()
        })
      })
    },

    initializeValidators() {
      let { cardDataObj } = this
      let customValidationRules = this.validateField() || {}
      let validationRules = {}

      Object.keys(cardDataObj).forEach(prop => {
        let propName = typeof prop === 'object' ? prop.prop : prop

        let validator = function(rule, value, callback) {
          if (isEmpty(this.cardDataObj[propName])) {
            callback(new Error('Field can not be empty'))
          } else {
            callback()
          }
        }.bind(this)
        const type = this.$getProperty(this, 'cardMeta.type') || null
        if (!(type === 'KPICARD2' && propName === 'dateRange')) {
          validationRules[propName] = {
            trigger: 'blur',
            validator,
          }
        }
      })

      this.$set(this, 'validationRules', {
        ...validationRules,
        ...customValidationRules,
      })
    },

    validateField() {
      // To be overriden in card component if form validation rule for a property
      // needs to be more complex that just an isEmpty() check
      // Return an object with prop vs {trigger, validator} (ElementUI form rule format)
      // as below:
      // {
      //   prop1: {trigger: 'change', validator: (rule, value, callback)=>{}}
      //   prop2: {trigger: 'blur', validator: (rule, value, callback)=>{}}
      // }
    },

    deserialize(cardParams) {
      let params = cloneDeep(cardParams)
      Object.keys(params).forEach(prop =>
        this.deserializeProperty(prop, params)
      )
    },

    deserializeProperty(prop, data) {
      // To be overriden in card components if required.
      // By default only sets the props in resourceProps to cardDataObj
      let validProps = this.resourceProps.map(p =>
        typeof p === 'object' ? p.prop : p
      )

      if (validProps.includes(prop)) {
        this.$set(this.cardDataObj, prop, data[prop])
      }
    },

    serialize() {
      let { resourceProps, cardDataObj } = this
      let data = {}

      resourceProps.forEach(prop => {
        let propName = typeof prop === 'object' ? prop.prop : prop
        data[propName] = cardDataObj[propName]

        let value = this.serializeProperty(propName, cardDataObj)
        if (!isUndefined(value)) data[propName] = value
      })
      let kpiType = data?.kpiType ?? null
      if (kpiType === 'reading') {
        let newData = this.$helpers.cloneObject(data)
        const { kpis, maxSafeLimitKpi, centerTextKpi, kpi } = newData ?? {}

        if (!isEmpty(kpis)) {
          newData.kpis = kpis.map(kpi => {
            let { kpiId } = kpi
            if (!isEmpty(kpiId) && isString(kpiId)) {
              kpi.kpiId = parseInt(kpiId.split('_')[0])
            }
            return kpi
          })
        }
        ;[maxSafeLimitKpi, centerTextKpi, kpi].forEach((key, index) => {
          if (!isEmpty(key)) {
            let { kpiId } = key
            key.kpiId = isString(kpiId) ? parseInt(kpiId.split('_')[0]) : kpiId
            if (index === 0) {
              newData['maxSafeLimitKpi'] = key
            } else if (index === 1) {
              newData['centerTextkpi'] = key
            } else {
              newData['kpi'] = key
            }
          }
        })
        return newData
      }
      return data
    },

    serializeProperty(/* prop, data */) {
      // To be implemented in card components if any special handling is required
      // before sending to server, for any of the props in resourceProps
    },

    serializeState() {
      // To be overriden in the card components as required.
      // By default, sends the cardStateObj as it is.
      let { cardStateObj } = this
      if (!isEmpty(cardStateObj)) return cardStateObj
    },

    serializeConditions() {
      // to serialize the conditional formatting
      let { conditionalFormatting } = this
      if (!isEmpty(conditionalFormatting)) return conditionalFormatting
    },

    deserializeConditions(conditionalFormatting) {
      // TO deserialize the state
      if (!isEmpty(conditionalFormatting))
        this.$set(this, 'conditionalFormatting', conditionalFormatting)
    },

    deserializeState(cardState) {
      // To be overriden in the card components if required.
      // Receives cardState from server as the only param
      if (!isEmpty(cardState)) this.$set(this, 'cardStateObj', cardState)
    },

    serializeActions() {
      // To be overriden in the card components as required.
      // By default, sends the cardActions as it is.
      let { cardActions } = this
      if (!isEmpty(cardActions)) return cardActions
    },

    deserializeActions(cardDrilldown) {
      // To be overriden in the card components if required.
      // Receives cardDrillDdown from server as the only param
      if (!isEmpty(cardDrilldown)) this.$set(this, 'cardActions', cardDrilldown)
    },

    configWatchHook() {
      // use this method insted of watch => config
    },

    customValidation() {
      // this methods should be removed
      // if any custom validation requirement needed then use this method ..also this is the last validation
      return true
    },

    getData() {
      this.configWatchHook()
      let data = {
        cardContext: {
          cardLayout: this.cardLayout,
          cardParams: this.serialize(),
          cardState: this.serializeState(),
          cardDrilldown: this.serializeActions(),
          conditionalFormatting: this.serializeConditions(),
          customScriptId: this.customScriptId,
          scriptModeInt: this.scriptModeInt,
        },
      }
      this.isPreviewLoading = true
      return this.$http
        .post(`/v2/dashboard/cards/getCardData`, data)
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.$set(this, 'result', data.result)
          } else {
            this.$set(this, 'result', null)
          }
          this.isPreviewLoading = false
        })
        .catch(() => {
          this.isPreviewLoading = false
        })
    },

    debouncedGetData: debounce(function() {
      return this.getData()
    }, 1 * 1000),

    save() {
      this.validateForm()
        .then(() => {
          if (this.customValidation()) {
            let data = {
              cardContext: {
                cardLayout: this.cardLayout,
                cardParams: this.serialize(),
                cardState: this.serializeState(),
                cardDrilldown: this.serializeActions(),
                conditionalFormatting: this.serializeConditions(),
                customScriptId: this.customScriptId,
                scriptModeInt: this.scriptModeInt,
              },
            }

            if (this.isNew) {
              this.onCardSave(data, this.layout)
            } else if (this.isDuplicate) {
              this.onCardDuplicate(data, this.layout)
            } else {
              this.onCardUpdate(data, this.layout)
            }
          }
        })
        .catch(() => {})
    },

    close() {
      this.onClose()
    },

    setConditionalData(cardDataObj) {
      let { reading } = cardDataObj
      let { readings } = cardDataObj
      let readingArray = !isEmpty(reading)
        ? [reading]
        : !isEmpty(readings)
        ? readings
        : []

      this.setConditionalVariables(readingArray, true)
    },

    setConditionalVariables(readings = [], skipRerender) {
      if (!Array.isArray(readings)) {
        readings = [readings]
      }
      let { cardType } = this
      readings.forEach(reading => {
        let { dataType } = reading
        let { conditionalVariables } = this
        let variable = (conditionalVariables || []).find(
          v => v.name === 'value'
        )
        const assignOperator = () => {
          if (dataType === 2) {
            variable.dataType = 'NUMBER'
          } else if (dataType === 3) {
            variable.dataType = 'DECIMAL'
          } else if (dataType === 4) {
            variable.dataType = 'BOOLEAN'
          } else {
            variable.dataType = 'STRING'
          }
        }
        if (cardType === 'kpi' && dataType) {
          assignOperator()
        } else if (
          !isEmpty(conditionalVariables) &&
          variable &&
          cardType != 'kpi'
        ) {
          assignOperator()
        }
        if (!skipRerender) {
          this.rerenderConditionalFormatting()
        }
      })
    },

    rerenderConditionalFormatting() {
      let conditionalComponent = this.$refs['conditional-formatting']
      if (!isEmpty(conditionalComponent)) {
        conditionalComponent.rerender()
      }
    },
  },
}
</script>
<style>
.graphics-insert-variable-title {
  padding: 12px;
  border-bottom: 1px solid #fafafa;
  text-transform: uppercase;
  font-weight: 500;
  font-size: 13px;
}
.graphics-insert-variable-filter {
  padding: 12px;
}
.graphics-insert-variable-list {
  max-height: 300px;
  overflow: scroll;
}
.graphics-insert-variable-list ul {
  padding: 0;
  margin: 0;
}
.graphics-insert-variable-list ul li {
  list-style: none;
  padding: 12px;
  cursor: pointer;
}
.graphics-insert-variable-list ul li:hover {
  background: #fafafa;
}
</style>
