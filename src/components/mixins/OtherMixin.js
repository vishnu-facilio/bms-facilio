import { mapState } from 'vuex'
export default {
  computed: {
    ...mapState({ account: state => state.account }),
    isAltayerNonPrivilagedUser() {
      if (this.account?.org.id === 418) {
        let { user } = this.account || {}
        let { role } = user || {}
        if (!role.isPrevileged) {
          return true
        }
      }
      return false
    },
  },
  methods: {
    getAltayerFilteredModulesList(modulesList) {
      if (this.isAltayerNonPrivilagedUser) {
        modulesList = modulesList.filter(
          mod => mod.label != null && mod.label != 'Reports'
        )
      }
      return modulesList
    },
  },
}
