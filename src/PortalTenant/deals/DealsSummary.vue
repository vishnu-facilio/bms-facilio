<script>
import Summary from 'src/pages/community/deals/DealsSummary'
import { findRouteForModule, pageTypes } from '@facilio/router'

export default {
  extends: Summary,
  methods: {
    goToList() {
      let { viewname } = this
      let route = findRouteForModule('dealsandoffers', pageTypes.LIST)

      if (route) {
        this.$router.push({ name: route.name, params: { viewname } })
      } else {
        console.warn('Could not resolve route')
      }
    },
    dropdownActionHandler(command) {
      if (command === 'edit') {
        let { id } = this
        let route = findRouteForModule('dealsandoffers', pageTypes.EDIT)

        if (route) {
          this.$router.push({ name: route.name, params: { id } })
        } else {
          console.warn('Could not resolve route')
        }
      } else if (command === 'delete') {
        this.deleteRecord()
      }
    },
  },
}
</script>
