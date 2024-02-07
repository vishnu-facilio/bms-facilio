import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'

export default {
  computed: {
    ...mapGetters(['getCurrentSiteId']),
  },
  methods: {
    transformFormData(data, formData) {
      data.id = [data.id]
      isEmpty(formData) ? (formData = {}) : null
      formData.siteId = formData.siteId || this.getCurrentSiteId()

      if (!isEmpty(formData.ticketattachments)) {
        data.ticketattachments = formData.ticketattachments
        delete formData.ticketattachments
      }

      if (formData.comment) {
        data.comment = formData.comment
      }
      if (formData.notifyRequester) {
        data.notifyRequester = formData.notifyRequester
        delete formData.notifyRequester
      }

      data['workorder'] = Object.keys(formData).reduce((res, key) => {
        if (formData[key]) {
          res[key] = formData[key]
        }
        return res
      }, {})

      return data
    },
  },
}
