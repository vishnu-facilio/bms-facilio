<template>
  <div>
    <el-row :gutter="50" align="middle">
      <el-col :span="24">
        <el-input
          v-model="model.subject"
          class="form-item"
          float-label="Task Title"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  props: ['model', 'assets'],
  data() {
    return {}
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadGroups')
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      locations: state => state.locations,
      ticketstatus: state => state.ticketStatus.workorder,
      ticketpriority: state => state.ticketPriority,
      ticketcategory: state => state.ticketCategory,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
  },
}
</script>
<style>
.add-task-title {
  font-weight: 500;
  letter-spacing: 0.9px;
  color: #030303;
  text-transform: uppercase;
}
</style>
