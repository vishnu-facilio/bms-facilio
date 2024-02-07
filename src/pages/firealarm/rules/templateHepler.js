export default {
  methods: {
    thresholdValue(type) {
      return this.$constants.CONDITION_TYPE[type]
    },
    groupByField(list, field) {
      let groups = {}
      for (let i = 0; i < list.length; i++) {
        let group = list[i][field]
        if (group in groups) {
          groups[group].push(list[i])
        } else {
          groups[group] = [list[i]]
        }
      }
      return groups
    },
    loadThresholdFields(id) {
      this.thresholdFields = []
      this.$store
        .dispatch('formulabuilder/loadAssetReadings', {
          assetCategoryId: id,
        })
        .then(() => {
          this.thresholdFields = this.$store.getters[
            'formulabuilder/getAssetReadings'
          ](id, true)
          // setMetricModule()
        })
    },
    getFieldDisplayName(name) {
      let fieldObj
      this.thresholdFields.filter(d => {
        if (d.name === name) {
          fieldObj = d
        }
      })
      return fieldObj
    },
  },
}
