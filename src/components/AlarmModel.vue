<template>
  <div style="background: #fff; width: 400px; height: 420px !important">
    <div label="Create Workorder" style="padding: 20px 20px 10px 20px">
      <div class="setup-modal-title">Create workorder</div>
    </div>
    <div class="model-container" style="background: #fff">
      <p class="fc-input-label-txt">Category</p>
      <el-select
        v-model="alarmRequest.category"
        class="fc-input-full-border2 width100"
      >
        <el-option
          v-for="category in ticketcategory"
          :key="category.id"
          :label="category.displayName"
          :value="category.id"
        >
        </el-option>
      </el-select>

      <p class="fc-input-label-txt mT20">Team/Staff</p>
      <div
        style="
          border: 1px solid #d8dce5;
          display: inline-block;
          position: relative;
          width: 100%;
          height: 40px;
          line-height: 40px;
          padding-left: 10px;
          padding-right: 10px;
          border-radius: 3px;
        "
      >
        <span>{{ getTeamStaffLabel(alarmRequest) }}</span>
        <span style="float: right">
          <i
            class="el-icon-arrow-down"
            style="position: relative; top: -4px; right: 3px; color: #c0c4cc"
          ></i>
        </span>
        <f-assignment :model="alarmRequest" viewtype="form"></f-assignment>
      </div>

      <p class="fc-input-label-txt mT20">Priority</p>
      <el-select
        v-model="alarmRequest.priority"
        class="fc-input-full-border2 width100"
      >
        <el-option
          v-for="priority in ticketpriority"
          :key="priority.priority"
          :label="priority.displayName"
          :value="parseInt(priority.id)"
        >
        </el-option>
      </el-select>
      <p class="fc-input-label-txt mT20">Site</p>
      <el-select
        v-model="alarmRequest.siteId"
        filterable
        clearable
        collapse-tags
        placeholder="Sites"
        class="fc-input-full-border2 width100"
        v-tippy
      >
        <el-option
          v-for="(site, index) in sites"
          :key="index"
          :label="site.name"
          :value="site.id"
        >
        </el-option>
      </el-select>
    </div>
    <div class="modal-dialog-footer" style="background: #fff">
      <el-button @click="closeDialog" class="modal-btn-cancel"
        >CANCEL</el-button
      >
      <el-button class="modal-btn-save" @click="createWO()">CREATE</el-button>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import FAssignment from '@/FAssignment'
import TeamStaffMixin from '@/mixins/TeamStaffMixin'
export default {
  props: ['id'],
  components: {
    FAssignment,
  },
  mixins: [TeamStaffMixin],
  data() {
    return {
      alarmRequest: {
        category: null,
        assignedTo: {
          id: -1,
        },
        assignmentGroup: {
          id: -1,
        },
        siteId: null,
        priority: null,
      },
    }
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadGroups')
  },
  computed: {
    ...mapState({
      users: state => state.users,
      sites: state => state.sites,
      ticketcategory: state => state.ticketCategory,
      ticketpriority: state => state.ticketPriority,
    }),
  },
  methods: {
    createWO(event) {
      this.$emit('submit', this.alarmRequest)
    },
    closeDialog() {
      this.reset()
      this.$emit('closed')
    },
    reset() {
      this.alarmRequest = {
        category: null,
        assignedTo: {
          id: -1,
        },
        assignmentGroup: {
          id: -1,
        },
        priority: null,
      }
    },
  },
}
</script>
<style>
.model-container {
  padding-left: 25px;
  padding-right: 25px;
  height: 300px;
  overflow: scroll;
}
.f-select {
  font-size: 14px;
}
div.f-select label {
  color: #757575;
}
div.f-select span.selected-tag {
  color: #333 !important;
  font-size: 13px;
}
div.f-select .v-select .open-indicator {
  color: rgba(0, 0, 0, 0.57);
}
.model-footer {
  position: absolute;
  z-index: 1;
  bottom: 0;
  right: 0;
  left: 0;
}
.v-select .dropdown-toggle {
  border-bottom: 1px solid rgba(60, 60, 60, 0.14) !important;
}
.closeDilaogicon i.q-item-icon.q-icon.material-icons {
  font-size: 15px;
}
</style>
