<template>
  <div style="height: auto; overflow-y: scroll;">
    <div v-if="ruleOverviewLoading" class="flex-middle fc-empty-white">
      <spinner :show="ruleOverviewLoading" size="80"></spinner>
    </div>
    <div
      v-else-if="rule && rule.alarmRule"
      class="fc__layout__align fc__asset__main__header pT20 pB20 pL20 pR20"
      style="width: auto; align-items: center !important; border-bottom: none;height: 70px;"
    >
      <div v-if="rule && rule.alarmRule" class="flex-middle">
        <div class="wos-id" @click="goBack()" :title="$t('common.header.back')">
          <span class="fc-id">#{{ rule.alarmRule.preRequsite.id }}</span>
        </div>
        <div class="fc-black-22 fw3 flex-middle">
          <div class="fc-separator-lg mL20 mR20"></div>
          <div class="max-width500px textoverflow-ellipsis">
            {{ rule.alarmRule.preRequsite.name }}
          </div>
          <div class="fc-newsummary-chip">
            {{
              rule.alarmRule.preRequsite.status
                ? $t('common._common.active')
                : $t('common._common.inactive')
            }}
          </div>
        </div>
      </div>
      <div class="display-flex mR10">
        <div class="flRight fc-el-button mL10">
          <el-button
            v-if="$hasPermission('alarmrules:UPDATE,UPDATE_TEAM,UPDATE_OWN')"
            @click=";(isEditLoading = true), editThresholdRule(ruleId)"
            :loading="isEditLoading"
            >{{ $t('common._common.edit') }}</el-button
          >
          <el-button
            v-if="$hasPermission('alarmrules:UPDATE,UPDATE_TEAM,UPDATE_OWN')"
            @click=";(isLoading = true), changeRuleStatus(rule.alarmRule)"
            :loading="isLoading"
            type="primary"
          >
            {{
              (rule.alarmRule.preRequsite.status
                ? $t('common._common.deactivate')
                : $t('common._common.activate')) + (isLoading ? 'ing' : '')
            }}
          </el-button>
          <el-dropdown
            class="mL10 fc-btn-ico-lg pointer"
            style="padding-top: 5px; padding-bottom: 5px;position: relative; top: 5px;"
            trigger="click"
          >
            <span class="el-dropdown-link">
              <img src="~assets/menu.svg" width="16" height="16" />
            </span>
            <el-dropdown-menu slot="dropdown" trigger="click" class="p10">
              <div
                class="pT5 pB5 fc-label-hover label-txt-black f14 pL10 pR10 pointer"
                @click="showHistoryDialog = true"
              >
                {{ $t('common.tabs.historical_run') }}
              </div>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>
    <historical-run
      v-if="showHistoryDialog"
      :visibility.sync="showHistoryDialog"
      :details="details"
    ></historical-run>
    <page
      v-if="rule && rule.alarmRule"
      :key="rule.alarmRule.preRequsite.id"
      :module="currentModuleName"
      :id="rule.alarmRule.preRequsite.id"
      :details="rule"
      :primaryFields="primaryFields"
    ></page>
  </div>
</template>
<script>
import Page from '@/page/PageBuilder'
import HistoricalRun from '@/page/widget/rule/HistoricalRunDialog'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'

export default {
  components: { Page, HistoricalRun },
  mounted() {
    this.loadRuleDetails()
  },
  data() {
    return {
      showHistoryDialog: false,
      rule: null,
      thresholddialog: false,
      ruleOverviewLoading: false,
      isEditLoading: false,
      selectedRules: null,
      selectedRule: null,
      isLoading: false,
      currentModuleName: 'readingrule',
      primaryFields: [
        'name',
        'description',
        'category',
        'type',
        'siteId',
        'space',
        'qrVal',
      ],
    }
  },
  computed: {
    ruleId() {
      return parseInt(this.$route.params.id)
    },
    moduleName() {
      return 'readingrule'
    },
  },
  watch: {
    ruleId: function() {
      this.loadRuleDetails()
    },
  },
  methods: {
    loadReadingRules() {},
    goBack() {
      window.history.go(-1)
    },
    editThresholdRule(rule) {
      let self = this
      self.selectedRules = self.rule
      self.selectedRule = this.ruleId
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.RULES_EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: { id: rule },
            query: this.$route.query,
          })
      } else {
        let url = '/app/fa/rule/edit/' + rule
        self.$router.replace({ path: url })
        self.$router.push()
      }
    },
    loadRuleDetails() {
      let self = this
      this.ruleOverviewLoading = true
      let params = { ruleId: this.ruleId }
      self.$http
        .post('/v2/alarm/rules/fetchRuleSummary', params)
        .then(function(response) {
          self.rule = response.data.result
          self.ruleOverviewLoading = false
        })
    },
    changeRuleStatus(rule) {
      this.rule.alarmRule.preRequsite.status = !this.rule.alarmRule.preRequsite
        .status
      this.$util
        .changeRuleStatus('alarm', rule.preRequsite.id, rule.preRequsite.status)
        .then(response => {
          this.isLoading = false
          this.$message.success(
            (rule.preRequsite.status
              ? this.$t('common._common.activate')
              : this.$t('common._common.deactivate')) +
              this.$t('common._common.rule_successfully')
          )
        })
        .catch(function(error) {
          console.log(error)
        })
    },
  },
}
</script>
<style scoped>
.asset-details {
  flex-grow: 1;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.asset-details .asset-id {
  font-size: 12px;
  color: #39b2c2;
}
.asset-details .asset-name {
  font-size: 22px;
  color: #324056;
}
.asset-details .asset-space {
  font-size: 13px;
  color: #8ca1ad;
}
</style>
