<script>
import FormCreation from '@/base/FormCreation'
import { findRouteForModule, pageTypes } from '@facilio/router'
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
      return this.$getProperty(this.formObj, 'module.displayName') || 'Booking'
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    afterSaveHook({ error, facilitybooking = {} }) {
      if (!error) this.redirectToSummary(facilitybooking)
    },
    redirectToList() {
      let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
      name &&
        this.$router.push({
          name,
        })
    },
    redirectToSummary(data) {
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
    },
    afterSerializeHook({ data }) {
      let { bookingslot = [] } = data
      let selectedList = []
      bookingslot.forEach(record => {
        let { id } = record
        selectedList.push({ slot: { id } })
      })
      this.$set(data, 'bookingslot', selectedList)
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
