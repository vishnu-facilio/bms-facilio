<script>
import FormCreation from '@/base/FormCreation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: FormCreation,
  title() {
    return 'Booking'
  },
  computed: {
    moduleName() {
      return 'facilitybooking'
    },
    moduleDisplayName() {
      return 'Booking'
    },
    isV3Api() {
      return true
    },
    isWidgetsSupported() {
      return true
    },
  },
  methods: {
    afterSaveHook({ error, facilitybooking = {} }) {
      if (!error) this.redirectToSummary(facilitybooking)
    },
    redirectToList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
          })
      } else {
        this.$router.push({
          name: 'bookingList',
        })
      }
    },
    redirectToSummary(data) {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id: data.id,
              viewname: 'all',
            },
          })
      } else {
        this.$router.push({
          name: 'bookingSummary',
          params: { id: data.id, viewname: 'all' },
        })
      }
    },
    afterSerializeHook({ data }) {
      let { bookingslot = [] } = data
      let selectedList = []

      bookingslot.forEach(record => {
        let { id } = record
        selectedList.push({ slot: { id } })
      })
      data['bookingslot'] = selectedList

      return data
    },
    getFormattedSlotData(moduleData) {
      let { slotList = [] } = moduleData
      let slotData = []
      if (!isEmpty(slotList)) {
        slotList.forEach(record => {
          slotData.push(record.slot)
        })
      }
      return slotData
    },
  },
}
</script>
