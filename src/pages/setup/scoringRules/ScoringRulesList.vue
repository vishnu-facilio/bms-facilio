<template>
  <div class="scoring-rule-list">
    <div class="scoring-rule-header">
      <div>
        <div class="scoring-rule-name">
          {{ $t('setup.setup.scoring_rules') }}
        </div>
        <div class="scoring-rule-desc">
          {{
            $t('common._common.list_of_all_modulename_scoring_rule', {
              moduleName: moduleDisplayName || moduleName,
            })
          }}
        </div>
      </div>

      <el-button type="primary" class="setup-el-btn" @click="addRule">
        {{ $t('setup.setup.add_scoring_rule') }}
      </el-button>
    </div>

    <portal-target name="automation-modules" class="mB30"></portal-target>

    <spinner v-if="loading" :show="loading" size="80"></spinner>

    <el-table
      v-else
      :data="scoringRulesList"
      :cell-style="{ padding: '12px 30px' }"
      :empty-text="$t('common.header.no_scoring_rules_available')"
      style="width: 100%"
      height="calc(100vh - 230px)"
    >
      <el-table-column :label="$t('common.products.name')">
        <template v-slot="rule">
          <div @click="editRule(rule.row)">
            {{ rule.row.scoreDetails.name }}
          </div>
        </template>
      </el-table-column>

      <el-table-column :label="$t('common.wo_report.report_description')">
        <template v-slot="rule">
          {{ rule.row.scoreDetails.description || '---' }}
        </template>
      </el-table-column>

      <el-table-column :label="$t('common.wo_report.rating_scale')">
        <template v-slot="rule">
          <template v-if="rule.row.scoreDetails.scoreType === 1">%</template>
          <template v-else>{{ rule.row.scoreDetails.scoreRange }}</template>
        </template>
      </el-table-column>

      <el-table-column :label="$t('common._common.status')" width="150px">
        <template v-slot="rule">
          <el-switch
            v-model="rule.row.status"
            @change="changeStatus(rule.row)"
            active-color="rgba(57, 178, 194, 0.8)"
            inactive-color="#e5e5e5"
          ></el-switch>
        </template>
      </el-table-column>

      <el-table-column class="visibility-visible-actions" width="200px">
        <template v-slot="rule">
          <div class="text-center">
            <i
              class="el-icon-edit edit-icon visibility-hide-actions pR15"
              data-arrow="true"
              :title="$t('setup.setup.edit_scoring_rule')"
              v-tippy
              @click="editRule(rule.row)"
            ></i>
            <i
              class="el-icon-delete fc-delete-icon visibility-hide-actions"
              data-arrow="true"
              :title="$t('setup.setup.delete_scoring_rule')"
              v-tippy
              @click="deleteRule(rule.row)"
            ></i>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { ScoringRule } from './Models/ScoringRuleModel'

export default {
  props: ['moduleName', 'moduleDisplayName'],

  data() {
    return {
      loading: false,
      scoringRulesList: [],
    }
  },

  created() {
    this.loadScoringRule()
  },

  methods: {
    async loadScoringRule() {
      this.loading = true

      try {
        this.scoringRulesList = await ScoringRule.fetchAll(this.moduleName)
      } catch (error) {
        this.$message.error(error)
      }

      this.loading = false
    },
    addRule() {
      let { moduleName } = this

      this.$router.push({
        name: 'scoringrules.new',
        params: { moduleName },
      })
    },
    async changeStatus(rule) {
      try {
        await rule.patch()
        this.$message.success(
          this.$t('common.wo_report.changed_scoring_rule_status')
        )
      } catch (error) {
        this.$message.error(
          error || this.$t('common._common.failed_to_changed_rule_status')
        )
      }
    },
    editRule(rule) {
      let { moduleName } = this
      let { scoreDetails } = rule || {}
      let { id } = scoreDetails || {}

      this.$router.push({
        name: 'scoringrules.edit',
        params: { moduleName, id },
      })
    },
    async deleteRule(rule) {
      let value = await this.$dialog.confirm({
        title: this.$t('common.header.delete_scoring_rule'),
        htmlMessage: this.$t(
          'common.wo_report.are_you_sure_delete_scoring_rule'
        ),
        rbDanger: true,
        rbLabel: this.$t('common.login_expiry.rbLabel'),
      })

      if (!value) return

      let { scoreDetails } = rule || {}
      let { id } = scoreDetails || {}

      try {
        await ScoringRule.delete(id)
        let idx = this.scoringRulesList.findIndex(t => t.id === id)

        !isEmpty(idx) && this.scoringRulesList.splice(idx, 1)
        this.$message.success(
          this.$t('common.header.scoring_rule_deleted_successfully')
        )
      } catch (error) {
        this.$message.error(error)
      }
    },
  },
}
</script>
<style lang="scss">
.scoring-rule-list {
  padding: 20px 30px;
  height: calc(100vh - 50px);

  .scoring-rule-header {
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
  }
  .scoring-rule-name {
    font-size: 18px;
    color: #000000;
    letter-spacing: 0.7px;
    padding-bottom: 5px;
    text-transform: capitalize;
  }
  .scoring-rule-desc {
    font-size: 13px;
    color: #808080;
    letter-spacing: 0.3px;
  }
}
</style>
