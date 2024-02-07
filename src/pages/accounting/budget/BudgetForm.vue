<script>
import CustomModulesCreation from 'src/pages/custom-module/CustomModulesCreation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { BudgetModuleData } from './BudgetModuleData'

export default {
  extends: CustomModulesCreation,
  title() {
    return 'Budget'
  },
  computed: {
    moduleName() {
      return 'budget'
    },
    moduleDisplayName() {
      return 'Budget'
    },
    customClassForContainer() {
      return 'budget-container'
    },
    modelDataClass() {
      return BudgetModuleData
    },
  },
  methods: {
    afterSerializeHook(attrs) {
      return this.moduleData.afterSerialize(attrs)
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
          name: 'budgetList',
        })
      }
    },
    redirectToSummary(id) {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
          })
      } else {
        this.$router.push({
          name: 'budgetSummary',
          params: { id },
        })
      }
    },
  },
}
</script>
