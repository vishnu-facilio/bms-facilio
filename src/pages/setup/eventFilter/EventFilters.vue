<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Event Filtering</div>
        <div class="heading-description">List of all Event Filters</div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button type="primary" @click="newEventRule" class="setup-el-btn"
          >Add Filter</el-button
        >
        <new-event-filter
          v-if="showDialog"
          :selectedEventRule="selectedEventRule"
          :visibility.sync="showDialog"
          :isNew="isNew"
          @saved="loadEventRules"
        ></new-event-filter>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">NAME</th>
                <th class="setting-table-th setting-th-text">CONDITION</th>
                <th class="setting-table-th setting-th-text">ACTION</th>
                <th class="setting-table-th setting-th-text">STATUS</th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="!eventRules || !eventRules.length">
              <tr>
                <td colspan="100%" class="text-center">
                  No EventFilters available.
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(eventRule, index) in eventRules"
                :key="index"
              >
                <td>{{ eventRule.name }}</td>
                <td>{{ eventRule.criteriaId != -1 ? 'Simple' : 'Complex' }}</td>
                <td>{{ eventRule.successActionEnum }}</td>
                <td>
                  <el-switch
                    v-model="eventRule.active"
                    @change="changeRuleStatus(eventRule)"
                    class="Notification-toggle"
                    active-color="rgba(57, 178, 194, 0.8)"
                    inactive-color="#e5e5e5"
                  ></el-switch>
                </td>
                <td>
                  <div
                    class="text-left actions"
                    style="margin-top:-3px;margin-right: 15px;text-align:center;"
                  >
                    <i
                      class="el-icon-edit pointer"
                      @click="editEventRule(eventRule)"
                    ></i>
                    &nbsp;&nbsp;
                    <i
                      class="el-icon-delete pointer"
                      @click="deleteEventRule(eventRule.id)"
                    ></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import NewEventFilter from './NewEventFilter'
export default {
  components: {
    NewEventFilter,
  },
  data() {
    return {
      loading: true,
      eventRules: [],
      isNew: true,
      selectedEventRule: {},
      showDialog: false,
    }
  },
  title() {
    return 'Event Filtering'
  },
  created() {
    this.loadEventRules()
  },
  methods: {
    changeRuleStatus(rule) {
      rule.active = !rule.active
      let self = this
      let parseRule = {
        id: rule.id,
        active: !rule.active,
      }
      this.$http
        .post('/event/updateEventRule', { eventRule: parseRule })
        .then(() => {
          self.$message.success('Rule updated sucessfully')
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    newEventRule() {
      this.isNew = true
      this.showDialog = true
    },
    editEventRule(eventRule) {
      this.isNew = false
      this.selectedEventRule = this.$helpers.cloneObject(eventRule)
      this.showDialog = true
    },
    loadEventRules() {
      let self = this
      self.loading = true
      self.$http.get('/event/allRules').then(function(response) {
        self.eventRules = response.data
        self.loading = false
      })
    },
    deleteEventRule(id) {
      let self = this
      self.$dialog
        .confirm({
          title: 'Delete Event Rule ',
          message: 'Are you sure you want to delete this Rule?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(function(value) {
          if (value) {
            self.$http
              .post('/event/deleteEventRule', { id: id })
              .then(function(response) {
                if (
                  response.status === 200 &&
                  typeof response.data === 'object'
                ) {
                  self.$message.success('Rule deleted successfully')
                  let idx = self.eventRules.findIndex(rule => rule.id === id)
                  self.eventRules.splice(idx, 1)
                } else {
                  self.$message.error('Rule deletion failed')
                }
              })
              .catch(() => {
                console.log('Error in deleting')
              })
          }
        })
    },
  },
}
</script>
<style>
.fc-create-record {
  width: 60% !important;
  height: 100% !important;
  max-height: 100% !important;
}
</style>
