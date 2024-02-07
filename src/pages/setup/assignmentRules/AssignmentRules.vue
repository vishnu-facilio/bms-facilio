<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('common.products.assignment_rules') }}
        </div>
        <div class="heading-description">
          {{ $t('common._common.list_of_all_assingment_rules') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button class="plain" @click="canMove">{{
          $t('common.header.rearrange')
        }}</el-button>
        <el-button
          type="primary"
          @click="addrule"
          class="setup-el-btn"
          style="margin-top: -4px;"
          >{{ $t('common.products.add_assignment_rule') }}</el-button
        >
        <new-assignment-rule
          v-if="showDialog"
          :rule="selectedRule"
          :visibility.sync="showDialog"
          :assignmentRule="selectedAssignmentRule"
          :isNew="isNew"
          @onsave="loadAssignmentRules"
        ></new-assignment-rule>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text"></th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('common.products._name') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('common.wo_report._description') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('common.products.status') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td></td>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="!assignmentRules.length">
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('common.products.no_rules_created_yet') }}
                </td>
              </tr>
            </tbody>
            <draggable
              v-model="assignmentRules"
              :options="{ draggable: moveIt ? '.asd' : '' }"
              :element="'tbody'"
              v-else
            >
              <tr
                class="asd"
                v-bind:class="{ tablerow: !moveIt, activedrag: moveIt }"
                v-for="(assignmentRule, index) in assignmentRules"
                :key="index"
              >
                <td style="width:1px;">
                  <i class="fa fa-exchange exchange" v-if="moveIt == true"></i>
                </td>
                <td>{{ assignmentRule.name ? assignmentRule.name : '---' }}</td>
                <td>
                  {{
                    assignmentRule.description
                      ? assignmentRule.description
                      : '---'
                  }}
                </td>
                <td>
                  <el-switch
                    v-model="assignmentRule.status"
                    @change="toggle(assignmentRule)"
                    active-color="rgba(57, 178, 194, 0.8)"
                    inactive-color="#e5e5e5"
                  ></el-switch>
                </td>
                <td>
                  <div
                    class="text-left actions"
                    style="margin-top:-3px;margin-right: 15px;text-align:center;border-right: none;"
                  >
                    <i
                      class="el-icon-edit pointer"
                      @click="editRule(assignmentRule)"
                    ></i>
                    &nbsp;&nbsp;
                    <i
                      class="el-icon-delete pointer"
                      @click="deleteRule(assignmentRule)"
                    ></i>
                  </div>
                </td>
              </tr>
            </draggable>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import NewAssignmentRule from './NewAssignmentRule'
import draggable from 'vuedraggable'
import { isEmpty } from '@facilio/utils/validation'
export default {
  components: {
    draggable,
    NewAssignmentRule,
  },
  data() {
    return {
      loading: true,
      moveIt: false,
      isNew: false,
      assignmentRules: [],
      showDialog: false,
      selectedRule: null,
    }
  },
  title() {
    return 'Assignment Rules'
  },
  created() {
    this.loadAssignmentRules()
  },
  methods: {
    loadAssignmentRules() {
      this.loading = true
      this.$util.loadRules('workorder', 6).then(rules => {
        this.assignmentRules = rules
        this.loading = false
      })
    },
    canMove() {
      this.moveIt = !this.moveIt
    },
    toggle(assignmentRule) {
      this.$util.changeRuleStatus(
        'workorder',
        assignmentRule.id,
        assignmentRule.status
      )
    },
    addrule() {
      this.isNew = true
      this.showDialog = true
      this.selectedAssignmentRule = null
      this.selectedRule = null
    },
    editRule(assignmentRule) {
      this.selectedRule = assignmentRule
      this.selectedAssignmentRule = assignmentRule
      this.isNew = false
      this.showDialog = true
    },
    deleteRule(rule) {
      let promptObj = {
        title: this.$t('common.header.delete_assignment_rules'),
        message: this.$t(
          'common.wo_report.are_you_want_delete_assignment_rule'
        ),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      }
      this.$dialog.confirm(promptObj).then(value => {
        if (value) {
          this.$util.deleteRule('workorder', rule.id).then(() => {
            let idx = this.assignmentRules.findIndex(r => r.id === rule.id)

            if (!isEmpty(idx)) {
              this.assignmentRules.splice(idx, 1)
            }
          })
        }
      })
    },
  },
}
</script>
<style>
.more-actions:hover {
  background-color: #fafbfc;
  cursor: pointer;
}
.exchange:hover {
  cursor: all-scroll;
}
.sortable-ghost {
  opacity: 0;
}
.activedrag {
  border: 1px bold;
}
.fa-exchange {
  color: rgba(0, 0, 0, 0.6);
}
.prompt-input {
  display: none !important;
}
</style>
