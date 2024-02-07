<template>
  <el-dialog
    :visible.sync="visibility"
    :append-to-body="true"
    title="Add Root Cause"
    custom-class="setup-dialog40 fc-dialog-center-container fc-dialog-center-body-p0"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div class="rca-rule-dialog-tab">
      <el-tabs v-model="activeTab">
        <el-tab-pane
          v-for="(tab, index) in tabs"
          :key="tab.name + index"
          :name="tab.name"
          :label="tab.displayName"
          lazy
        >
          <!-- {{ruleList}} -->
          <div class="height350 overflow-y-scroll pB50">
            <el-checkbox-group v-model="ruleObjs">
              <el-checkbox
                v-for="(rule, label) in ruleList"
                :label="label"
                :key="label"
                class="label-txt-black flex-middle p10 bold visibility-visible-actions"
                >{{ rule }}</el-checkbox
              >
            </el-checkbox-group>
          </div>
        </el-tab-pane>
      </el-tabs>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialog()"
          >Cancel</el-button
        >
        <el-button class="modal-btn-save" @click="addRCARule()">Save</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
export default {
  props: ['visibility', 'ruleObj', 'categoryId', 'rcaRuleList', 'sharedData'],
  data() {
    return {
      ruleObjs: this.ruleObj ? this.ruleObj : [],
      activeTab: 'configuredAlarm',
      ruleList: null,
    }
  },
  computed: {
    tabs() {
      let activeTab = this.activeTab
      return [
        {
          type: 'configuredAlarm',
          name: 'configuredAlarm',
          displayName: 'Configured Alarms',
          isActive: activeTab === 'configuredAlarm',
        },
      ]
    },
  },
  mounted() {
    this.loadCategoryRules()
  },
  methods: {
    loadCategoryRules() {
      let params = {
        categoryId: this.categoryId,
        ruleId: this.sharedData.preRequsite
          ? this.sharedData.preRequsite.id
          : -1,
      }
      this.$http.post('/v2/alarm/rules/rcaRules', params).then(response => {
        this.ruleList = response.data.result.rules
      })
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    addRCARule() {
      this.$emit('addRcaRule', this.ruleObjs)
      this.$emit('update:visibility', false)
    },
  },
}
</script>

<style lang="scss">
.rca-rule-dialog-tab {
  .el-tabs__nav {
    padding-left: 30px;
  }
  .height350 {
    padding-left: 20px;
  }
  .el-tabs__active-bar {
    left: 30px;
  }
}
</style>
