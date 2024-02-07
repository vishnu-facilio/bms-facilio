import { API } from '@facilio/api'
import moment from 'moment-timezone'
import Vue from 'vue'
import helpers from 'src/util/helpers'
export default {
  data() {
    return {
      employee: [],
      serviceRequestData: [],
      desks: [],
      bookingData: [],
      loading: true,
      visitorData: [],
    }
  },
  created() {
    this.loadServicePortalData()
  },
  methods: {
    loadServicePortalData() {
      this.loading = true
      API.get('/v2/servicePortalHome?count=2', {}).then(({ error, data }) => {
        if (!error) {
          this.employee = data.employee[0]
          this.serviceRequestData = data.serviceRequest
          this.desks = data.desks[0]
          this.bookingData = data.facilitybooking
          this.visitorData = data.invitevisitor
        }
        this.loading = false
      })
    },
    getFormattedDay(date) {
      let today = moment()
        .endOf('day')
        .tz(Vue.prototype.$timezone)
      let tomorrow = moment()
        .add(1, 'day')
        .endOf('day')
        .tz(Vue.prototype.$timezone)

      if (date < today) return 'Today'
      if (date < tomorrow) return 'Tomorrow'
      return helpers.formatDateFull(date)
    },
  },
}
