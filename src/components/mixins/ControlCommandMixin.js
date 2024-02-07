export default {
  computed: {},
  methods: {
    getControlableAssets() {
      return this.$http
        .get('/v2/controlAction/getControllableAssets')
        .then(response => {})
    },
    getControlableReadings(resourceId) {
      return this.$http
        .get('/v2/controlAction/getControllableFields?resourceId=' + resourceId)
        .then(response => {})
    },
    controlActionChangeStatus(readingField, asset, status) {
      let jsonParams = {
        rdm: {
          resourceId: asset,
          fieldId: readingField.fieldId,
          isControllable: status,
          controlActionMode: 1,
        },
      }
      let self = this
      self.$http
        .post(`/v2/controlAction/updateRDM`, jsonParams)
        .then(response => {
          let res = response.data.result
          if (res && res.rdm) {
            self.$message.success(
              (status ? 'Enabled' : 'Disabled') +
                ' control action successfully.'
            )
          }
        })
        .catch(function() {})
    },
  },
}
