import { API } from '@facilio/api'

export default {
  methods: {
    async getReadingValues(assetId, type) {
      let { data } = await API.post('/v2/workflow/runWorkflow', {
        nameSpace: 'controls',
        functionName: 'getReadingValues',
        paramList: [assetId, type],
      })
      if (data && data.workflow) {
        let { workflow } = data
        let { returnValue = [] } = workflow
        return returnValue
      }
      return null
    },
    setReadingValue(assetId, fieldId, readingValue) {
      this.$util.setReadingValue(assetId, fieldId, readingValue)
    },
  },
}
