import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
export default {
  computed: { ...mapGetters(['getUser', 'getGroup']) },
  created() {
    this.$store.dispatch('loadGroups')
  },
  methods: {
    getAssignmentGroupName(model) {
      let {
        assignmentGroup: { id, name },
      } = model || {}
      if (!isEmpty(name)) return name
      else if (!isEmpty(id) && !isEmpty(this.getGroup(id)))
        return this.$getProperty(this.getGroup(id), 'name', '---')
      else return '---'
    },
    getAssignedToName(model) {
      let {
        assignedTo: { id, name },
      } = model || {}
      if (!isEmpty(name)) return name
      else if (!isEmpty(id) && !isEmpty(this.getUser(id)))
        return this.$getProperty(this.getUser(id), 'name', '---')
      else return '---'
    },
    getTeamStaffLabel(model) {
      return `${this.getAssignmentGroupName(model)} / ${this.getAssignedToName(
        model
      )}`
    },
  },
}
