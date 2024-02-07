export default {
  data() {
    return {
      alarmFields: [],
      selectedMetric: null,
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  methods: {
    loadThresholdFields(isMultiResource) {
      if (isMultiResource) {
        if (this.rule.preRequsite.assetCategoryId > 0) {
          this.thresholdFields = []
          this.$store
            .dispatch('formulabuilder/loadAssetReadings', {
              assetCategoryId: this.rule.preRequsite.assetCategoryId,
            })
            .then(() => {
              this.thresholdFields = this.$store.getters[
                'formulabuilder/getAssetReadings'
              ](this.rule.preRequsite.assetCategoryId, true)
            })
        }
      } else if (
        this.selectedResourceList &&
        this.selectedResourceList.id > 0
      ) {
        this.thresholdFields = []
        this.$util.loadReadingFields(this.selectedResourceList).then(fields => {
          this.thresholdFields = fields
        })
      }
    },
    setMetricModule(field, setObject) {
      this.thresholdFields.filter(d => {
        if (d.name === field) {
          this.selectedMetric = d
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
      let impactsMessage = { actionType: 6, templateJson: { fieldMatcher: [] } }
      Object.keys(action.templateJson.fieldMatcher).filter(key => {
        impactsMessage.templateJson.fieldMatcher.push({
          field: key,
          value: action.templateJson.fieldMatcher[key]
            ? action.templateJson.fieldMatcher[key]
            : null,
        })
      })
      return impactsMessage
      // action.actions.push(impactsMessage)
    },
    getOperatorSymbol: function(operatorId) {
      if (operatorId === 9) {
        return '=='
      } else if (operatorId === 10) {
        return '!='
      } else if (operatorId === 13) {
        return '>'
      } else if (operatorId === 14) {
        return '>='
      } else if (operatorId === 11) {
        return '<'
      } else if (operatorId === 12) {
        return '<='
      }
      return ''
    },
  },
}
