<script>
import ModuleDetails from 'pages/setup/modules/ModuleDetails.vue'

export default {
  extends: ModuleDetails,

  computed: {
    activeModuleTabs() {
      let activeTabs = [...this.tabs]

      activeTabs.splice(0, 1) // delete template tab
      activeTabs.push({
        label: 'Severity',
        path: 'severity',
      })

      return activeTabs
    },
  },
}
</script>
