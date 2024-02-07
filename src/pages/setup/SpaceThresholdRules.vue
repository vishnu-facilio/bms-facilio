<template>
  <div class="fc-form-container full-layout-white height100">
    <div class="row">
      <div class="col-lg-12 col-md-12">
        <div class="pull-left form-header">Space Threshold Rules</div>
        <div class="action-btn pull-right">
          <!-- <button class="btn btn--primary" @click="showCreateDialog = true">Add Rule</button> -->
          <el-button
            type="primary"
            style="background-color:#39b2c2;height:35px;border:1px solid #39b2c2;padding-top: 10px; "
            @click="newRule"
            >New Threshold Rule</el-button
          >
        </div>
      </div>
    </div>
    <div class="row pT30">
      <div class="col-lg-12 col-md-12">
        <table
          class="fc-list-view-table fc-border-1"
          style="padding-bottom: 100px;"
        >
          <thead>
            <tr>
              <th class="text-left" style="width:250px;">NAME</th>
              <th class="text-left" style="width:200px;">DESCRIPTION</th>
              <th class="text-left" style="width:200px;">RULE ORDER</th>
              <th class="text-left">STATUS</th>
              <th class="text-left"></th>
            </tr>
          </thead>
          <tbody v-if="loading">
            <tr>
              <td colspan="100%" class="text-center">
                <spinner :show="loading" size="80"></spinner>
              </td>
            </tr>
          </tbody>
          <tbody v-else-if="!readingRules.length">
            <tr>
              <td colspan="100%" class="text-center">
                No rules created yet.
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr
              class="tablerow"
              v-for="(readingRule, index) in readingRules"
              :key="index"
            >
              <td>{{ readingRule.name }}</td>
              <td>{{ readingRule.description }}</td>
              <td>{{ readingRule.executionOrder }}</td>
              <td>{{ readingRule.status ? 'Yes' : 'No' }}</td>
              <td>
                <div class="btn-block actions">
                  <button
                    class="btn btn--tertiary"
                    title="Edit"
                    data-arrow="true"
                    v-tippy
                  >
                    <i aria-hidden="true" class="fa fa-pencil"></i>
                  </button>
                  <button
                    class="btn btn--tertiary"
                    title="Delete"
                    data-arrow="true"
                    v-tippy
                  >
                    <i aria-hidden="true" class="fa fa-trash"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <!-- <new-reading-rule :visible.sync="showCreateDialog"></new-reading-rule> -->
  </div>
</template>
<script>
// import NewReadingRule from 'pages/setup/new/NewThresholdRule'
export default {
  components: {
    // NewReadingRule
  },
  data() {
    return {
      loading: true,
      readingRules: [],
      showCreateDialog: false,
    }
  },
  mounted() {
    this.loadReadingRules()
  },
  methods: {
    loadReadingRules() {
      let self = this
      self.loading = true
      self.$http
        .get('/setup/getWorkflowRules?ruleType=1')
        .then(function(response) {
          if (response.data === null) {
            self.readingRules = []
          } else {
            self.readingRules = response.data
          }
          self.loading = false
        })
    },
    newRule() {
      this.$router.push({ path: 'thresholdrules/new' })
    },
    closeCreateDialog() {
      console.log('close dialog called.... ')
      this.$refs.createRuleModel.close()
    },
  },
}
</script>
