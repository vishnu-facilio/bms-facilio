export default {
  methods: {
    isWorkOrderEditable() {
      const ORGID = this.$account.org.orgId

      // WO cannot be edited for ATRE
      const ATRE = 406
      if (ORGID === ATRE) {
        return false
      }

      // WO cannot be edited for BFM Technician
      const BRIGHT_FM = 274
      const TECHNICIAN = 274
      const ROLEID = this.$account.user.roleId
      if (ORGID === BRIGHT_FM && ROLEID && ROLEID == TECHNICIAN) {
        return false
      }

      // Locked & Requested WO cannot be edited
      let hasState = this.$getProperty(this.workorder, 'moduleState.id', null)
      let isLocked = hasState
        ? this.isStatusLocked(this.workorder.moduleState.id, 'workorder')
        : false
      let { isRequestedState } = this

      if (!hasState || isLocked || isRequestedState) {
        return false
      }

      return this.$hasPermission('workorder:UPDATE')
    },
  },
}
