<template>
  <div>
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title capitalize">
          Transaction Rules
        </div>
        <div class="heading-description">
          List of all {{ moduleDisplayName || moduleName }} transaction Rules
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="addTranscationForm"
        >
          Add Transaction
        </el-button>
      </div>
    </div>

    <el-row class="pL30">
      <el-col :span="5">
        <el-select
          placeholder="Module"
          filterable
          v-model="transModuleName"
          class="fc-input-full-border2 width100"
          :default-first-option="true"
        >
          <el-option
            v-for="(parentModule, index) in modulesList"
            :key="index"
            :label="parentModule.displayName"
            :value="parentModule.name"
          >
          </el-option>
        </el-select>
      </el-col>

      <el-col :span="5">
        <el-select
          placeholder="Sub Module"
          filterable
          v-model="transSubModuleName"
          class="fc-input-full-border2 width100 mL20"
          clearable
          @change="fetchTransactionRules"
        >
          <el-option
            v-for="(subModule, idx) in subModulesList[transModuleName]"
            :key="`${transModuleName}_${idx}`"
            :label="subModule.displayName"
            :value="subModule.name"
          >
          </el-option>
        </el-select>
      </el-col>
    </el-row>

    <spinner v-if="loading" :show="loading" size="80"></spinner>

    <el-table
      v-else
      :data="transactionRulesList"
      :cell-style="{ padding: '12px 20px' }"
      empty-text="No transaction available."
      style="width: 100%"
      height="calc(100vh - 230px)"
      class="m30"
    >
      <el-table-column fixed prop label="ID" min-width="90" class="pR10 pL10">
        <template v-slot="rule" class="pL0">
          <div class="fc-id">{{ '#' + rule.row.id }}</div>
        </template>
      </el-table-column>
      <el-table-column
        prop="name"
        :label="$t('setup.setupLabel.rule_name')"
      ></el-table-column>

      <el-table-column label="Type">
        <template v-slot="rule">
          {{
            rule.row.transactionConfigJson.transactionType === '1'
              ? 'Credit'
              : 'Debit'
          }}
        </template>
      </el-table-column>
      <el-table-column label="Status" width="200">
        <template v-slot="rule">
          <el-switch
            v-model="rule.row.status"
            @change="changeRuleStatus(rule.row)"
            class="Notification-toggle"
            active-color="rgba(57, 178, 194, 0.8)"
            inactive-color="#e5e5e5"
          ></el-switch>
        </template>
      </el-table-column>

      <el-table-column class="visibility-visible-actions">
        <template v-slot="rule">
          <div class="text-center">
            <i
              class="el-icon-edit edit-icon visibility-hide-actions pR15"
              data-arrow="true"
              title="Edit Transaction"
              v-tippy
              @click="editTransaction(rule.row)"
            ></i>
            <!-- This commented code will be removed from April 19 2023
                <i
                class="el-icon-delete fc-delete-icon visibility-hide-actions"
                data-arrow="true"
                title="Delete Transaction"
                v-tippy
                @click="deleteRule(rule.row)"
              ></i> -->
          </div>
        </template>
      </el-table-column>
    </el-table>

    <AddTransactionRule
      v-if="showDialog"
      :isNew="isNew"
      :rule="selectedRule"
      :moduleName="currentModuleName"
      @saved="fetchTransactionRules"
      @onClose="showDialog = false"
    ></AddTransactionRule>
  </div>
</template>
<script>
import AddTransactionRule from 'pages/setup/transactionRules/TransactionAddUpdateForm'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['moduleName', 'moduleDisplayName'],
  data() {
    return {
      isNew: false,
      showDialog: false,
      loading: false,
      transactionRulesList: [],
      selectedRule: null,
      modulesList: [],
      subModulesList: [],
    }
  },
  title() {
    return 'Transaction Rules'
  },
  components: { AddTransactionRule },

  async created() {
    await this.loadResourceData()
  },

  computed: {
    transModuleName: {
      get() {
        let {
          $route: { params },
        } = this
        let { moduleName } = params || {}

        return moduleName
      },
      set(moduleName) {
        this.$router.replace({
          params: { moduleName, subModule: null },
        })
      },
    },
    transSubModuleName: {
      get() {
        let {
          $route: { params },
        } = this
        let { subModule } = params || {}

        return subModule
      },
      set(subModule) {
        let { transModuleName } = this

        this.$router.replace({
          params: {
            moduleName: transModuleName,
            subModule: isEmpty(subModule) ? null : subModule,
          },
        })
      },
    },
    currentModuleName() {
      let { transModuleName, transSubModuleName } = this

      if (transSubModuleName) return transSubModuleName
      else return transModuleName
    },
  },

  watch: {
    moduleName: {
      handler: 'fetchTransactionRules',
      immediate: true,
    },
  },

  methods: {
    async changeRuleStatus(rule) {
      let url
      let title = ''
      let message = ''
      if (rule.status) {
        url = 'turnonrule'
        title = 'Do you want to enable this Transaction rule?'
        message =
          'Once this rule is enabled, It will be active in the future transactions.'
      } else {
        url = 'turnoffrule'
        title = 'Do you want to disable this Transaction rule?'
        message =
          "Once this rule is disabled, It won't be used in the future transactions"
      }
      let dialogObj = {
        title: `${title}`,
        htmlMessage: `${message}`,
        rbLabel: 'Confirm',
        rbClass: 'fdialog-edit',
      }
      this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          let params = { workflowId: rule.id }
          let { error } = API.post('/setup/' + url, params)
          if (error) {
            this.$message.error(
              error.message || this.$t('setup.failed.rule_status_change_failed')
            )
          }
        } else {
          rule.status = rule.status ? false : true
        }
      })
    },

    async loadResourceData() {
      let url = '/v2/transactionrule/module'
      let { data, error } = await API.get(
        url,
        {},
        { cacheTimeout: 10 * 60 * 1000 }
      )

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let { modules, submodules } = data

        this.modulesList = modules || []
        this.subModulesList = submodules || []
      }
    },
    async fetchTransactionRules() {
      this.loading = true

      let { error, data } = await API.post('v2/modules/rules/list', {
        ruleType: 43,
        moduleName: this.currentModuleName,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.transactionRulesList = data.workflowRuleList || []
      }
      this.loading = false
    },
    addTranscationForm() {
      this.isNew = true
      this.showDialog = true
    },
    editTransaction(selectedRule) {
      this.isNew = false
      this.showDialog = true
      this.selectedRule = selectedRule
    },
    //    deleteRule(selectedRule) {
    //   this.$dialog
    //     .confirm({
    //       title: 'Delete Rule',
    //       message: 'Are you sure you want to delete this rule?',
    //       rbDanger: true,
    //       rbLabel: 'Delete',
    //     })
    //     .then(async value => {
    //       if (!value) return

    //       let { error } = await API.post('/v2/modules/rules/delete', {
    //         ids: [selectedRule.id],
    //       })

    //       if (error) {
    //         this.$message.error(error.message || 'Rule deletion failed')
    //       } else {
    //         this.$message.success('Rule deleted successfully')
    //         this.fetchTransactionRules()
    //       }
    //     })
    // },
  },
}
</script>
