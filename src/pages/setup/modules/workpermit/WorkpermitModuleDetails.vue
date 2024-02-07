<script>
import ModuleDetails from 'pages/setup/modules/ModuleDetails.vue'

export default {
  extends: ModuleDetails,

  computed: {
    activeModuleTabs() {
      let activeTabs = [...this.tabs]

      activeTabs.push({
        label: 'CheckList',
        path: 'checklist',
      })

      return activeTabs
    },
  },
}
</script>
