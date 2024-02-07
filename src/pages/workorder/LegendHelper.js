import { mapState } from 'vuex'

export default {
  computed: {
    ...mapState({
      ticketCategory: state => state.ticketCategory,
      ticketPriority: state => state.ticketPriority,
      ticketType: state => state.ticketType,
    }),
  },

  methods: {
    async colorCodeWorkOrders(legendAttribute, workorders, timeMetricSettings) {
      console.log('color coding workorder', arguments)
      //this.$store.dispatch('loadTicketStatus', 'workorder')

      let legendMap = {}

      switch (legendAttribute) {
        case 'none':
          workorders.forEach(wo => {
            wo.colorIndex = -1
          })
          return legendMap

        case 'category':
          await this.$store.dispatch('loadTicketCategory')
          this.ticketCategory.forEach((e, index) => {
            legendMap[e.id] = {
              colorIndex: index,
              name: e.name,
            }
          })

          workorders.forEach(wo => {
            if (wo.category) {
              wo.colorIndex = legendMap[wo.category.id].colorIndex
            } else {
              wo.colorIndex = -1
            }
          })
          return legendMap

        case 'priority':
          await this.$store.dispatch('loadTicketPriority')
          this.ticketPriority.forEach((e, index) => {
            legendMap[e.id] = {
              name: e.displayName,
              colorIndex: index,
            }
          })

          workorders.forEach(wo => {
            if (wo.priority) {
              wo.colorIndex = legendMap[wo.priority.id].colorIndex
            } else {
              wo.colorIndex = -1
            }
          })
          return legendMap

        case 'type':
          await this.$store.dispatch('loadTicketType')
          this.ticketType.forEach((e, index) => {
            legendMap[e.id] = {
              colorIndex: index,
              name: e.name,
            }
          })

          workorders.forEach(wo => {
            if (wo.type) {
              wo.colorIndex = legendMap[wo.type.id].colorIndex
            } else {
              wo.colorIndex = -1
            }
          })
          return legendMap

        case 'frequency':
          Object.keys(this.$constants.FACILIO_FREQUENCY).forEach((e, index) => {
            legendMap[e] = {
              colorIndex: index,
              name: this.$constants.FACILIO_FREQUENCY[e],
            }
          })

          workorders.forEach(wo => {
            if (wo.frequencyID) {
              wo.colorIndex = legendMap[wo.frequencyID].colorIndex
            } else {
              wo.colorIndex = -1
            }
          })
          return legendMap

        case 'timeMetric':
          timeMetricSettings
            .filter(metric => metric.enabled)
            .forEach((e, index) => {
              //no id feild
              legendMap[e.name] = {
                colorIndex: index,
                name: e.displayName,
              }
            })

          workorders.forEach(wo => {
            if (wo.time) {
              wo.colorIndex = legendMap[wo.time].colorIndex
            } else {
              wo.colorIndex = -1
            }
          })
          return legendMap

        default: {
          //if none of the above attributes then color coding is based on custom field
          let resp = await this.$http.get('/module/meta?moduleName=workorder')

          let customField = resp.data.meta.fields.find(
            e => e.name == legendAttribute
          )
          let customFieldName = customField.name
          let customFieldEnum = customField.enumMap

          console.log(
            'custom field selected for legend enum map is,',
            customFieldName
          )

          Object.keys(customFieldEnum).forEach((e, index) => {
            legendMap[e] = {
              colorIndex: index,
              name: customFieldEnum[e],
            }
          })
          workorders.forEach(wo => {
            if (wo[customFieldName]) {
              //pmplanner jobs custom field is a property of wo object
              wo.colorIndex = legendMap[wo[customFieldName]].colorIndex
            } else if (wo.data && wo.data[customFieldName]) {
              //in calendar jobs format is   wo{ data:{customFieldName:vale}}
              wo.colorIndex = legendMap[wo.data[customFieldName]].colorIndex
            } else {
              wo.colorIndex = -1
            }
          })
          return legendMap
        }
      }
    },
  },
}
