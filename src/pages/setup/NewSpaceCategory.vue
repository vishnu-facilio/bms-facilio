<script>
import CustomModulesCreation from 'src/pages/custom-module/CustomModulesCreation'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: CustomModulesCreation,
  props: ['viewname'],
  computed: {
    moduleName() {
      return 'spacecategory'
    },
    moduleDisplayName() {
      return 'Space Category'
    },
  },
  methods: {
    afterSaveHook(response) {
      let { error } = response || {}
      if (isEmpty(error)) this.redirectToList()
    },
    async redirectToList() {
      this.$router.push({
        name: 'space-categories',
      })
    },
  },
}
</script>
