<script>
import FormCreation from '@/base/FormCreation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import sortBy from 'lodash/sortBy'
import pick from 'lodash/pick'
export default {
  extends: FormCreation,
  title() {
    return 'Facility'
  },
  computed: {
    moduleName() {
      return 'facility'
    },
    moduleDisplayName() {
      return 'Facility'
    },
    isV3Api() {
      return true
    },
    isWidgetsSupported() {
      return true
    },
  },
  methods: {
    afterSaveHook({ error, facility = {} }) {
      if (!error) this.redirectToSummary(facility)
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
          name: 'facilityList',
        })
      }
    },
    redirectToSummary(data) {
      if (data && data?.bookingCanceledCount > 0) {
        let canceledCount = data?.bookingCanceledCount
        let message =
          canceledCount > 1
            ? `${canceledCount} Bookings has been canceled.`
            : `${canceledCount} Booking has been canceled.`
        setTimeout(() => {
          this.$message.warning(message)
        }, 1000)
      }
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
          name: 'facilitySummary',
          params: { id: data.id, viewname: 'all' },
        })
      }
    },
    afterSerializeHook({ data }) {
      let { facilityWeekdayAvailability = [] } = data
      let selectedList = []
      facilityWeekdayAvailability.forEach(record => {
        if (record.isSelected) {
          delete record.isSelected
          selectedList.push(record)
        }
      })
      this.$set(data, 'facilityWeekdayAvailability', selectedList)
      return data
    },
    formatForFacilityAvailability(moduleData = {}) {
      let { isEdit } = this
      let { weekDayAvailabilities = [] } = moduleData
      let availabilityData = []
      let dayOfWeekSelected = []

      let requiredFields = ['dayOfWeek', 'startTime', 'endTime', 'cost']
      if (isEdit) requiredFields.push('id')

      if (!isEmpty(weekDayAvailabilities)) {
        weekDayAvailabilities.forEach(record => {
          availabilityData.push({
            isSelected: true,
            ...pick(record, requiredFields),
          })
          dayOfWeekSelected.push(record.dayOfWeek)
        })
      }
      this.$constants.facilityAvailabilityDefaults.forEach(record => {
        if (!dayOfWeekSelected.includes(record.dayOfWeek)) {
          record.isSelected = false
          availabilityData.push(record)
        }
      })
      return sortBy(availabilityData, ['dayOfWeek'])
    },
  },
}
</script>
