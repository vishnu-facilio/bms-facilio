<script>
import ViewHeader from 'newapp/components/ViewHeaderWithoutGroups.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: ViewHeader,

  methods: {
    loadViews() {
      return this.$store
        .dispatch('view/loadGroupViews', {
          moduleName: this.moduleName,
          groupType: 2,
          viewType: 2,
        })
        .then(() => {
          this.initializeViews()
        })
    },
    goToView(view) {
      if (!isEmpty(view)) {
        let { name: viewname } = view
        this.$router
          .push({ params: { viewname } })
          .catch(error => console.warn('Could not switch view\n', error))
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.view-header-wg {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px;
}
</style>
