<script>
import Summary from 'src/pages/community/neighbourhood/NeighbourhoodSummary'
import { findRouteForModule, pageTypes } from '@facilio/router'

export default {
  extends: Summary,
  methods: {
    goToList() {
      let { viewname } = this
      let route = findRouteForModule('neighbourhood', pageTypes.LIST)

      if (route) {
        this.$router.push({ name: route.name, params: { viewname } })
      } else {
        console.warn('Could not resolve route')
      }
    },
    dropdownActionHandler(command) {
      if (command === 'edit') {
        let { id } = this
        let route = findRouteForModule('neighbourhood', pageTypes.EDIT)

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
