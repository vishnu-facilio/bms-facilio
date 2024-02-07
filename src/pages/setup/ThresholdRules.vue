<template>
  <div class=" height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Alarm Rules</div>
        <div class="heading-description">List of all Alarm Rules</div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button type="primary" @click="newThresholdRule" class="setup-el-btn"
          >New Alarm Rule</el-button
        >
        <new-threshold-rule
          v-if="thresholddialog"
          :visibility.sync="thresholddialog"
          :parentRuleName="parentRuleName"
          :isDependant="isDependant"
          :isnew="isNew"
          @saved="aftersave"
          :rule="rule"
        ></new-threshold-rule>
      </div>
    </div>
    <div class="select-all-rule">
      <el-popover
        placement="bottom"
        width="200"
        trigger="click"
        class="rule-popover"
        after-enter="true"
        v-model="closepopover"
        :popper-class="'alarmrule-list-popover'"
      >
        <div
          class="rule-list-txt"
          v-for="(category, index) in categoryTypes"
          :key="index"
          :label="category.label"
          :value="category.value"
          @click="selectedRuleTypes(category.value)"
        >
          {{ category.label }}
        </div>
        <el-button slot="reference" class="all-rule-btn"
          >{{ categoryTypes.find(type => categoryType === type.value).label
          }}<i class="el-icon-arrow-down"></i
        ></el-button>
      </el-popover>
    </div>

    <div class="container-scroll">
      <div class="row setting-Rlayout">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table mT20">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">RULE NAME</th>
                <th class="setting-table-th setting-th-text">SPACE / ASSET</th>
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
            <tbody v-else-if="!Object.keys(readingRules).length">
              <tr>
                <td colspan="100%" class="text-center">
                  No rules created yet.
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <template class="tablerow" v-for="(list, index) in readingRules">
                <tr
                  class="list-rule-head nohover"
                  :key="index"
                  v-if="categoryType === 'all'"
                >
                  <td colspan="100%">
                    {{ list.category }}
                    <span class="rules-count"
                      >{{ list.rules.length }} Rules</span
                    >
                  </td>
                </tr>
                <tr
                  class="tablerow"
                  v-for="(readingRule, ruleIndex) in list.rules"
                  :key="index + '_' + ruleIndex"
                  v-if="
                    categoryType === 'all' || categoryType === list.category
                  "
                >
                  <td style="padding-right: 0;">
                    <div style="width: 100%;line-height: 22px;">
                      {{ readingRule.name }}
                    </div>
                  </td>
                  <td style="width: 150px; ,max-width: 150px;">
                    {{ getResourceName(readingRule) }}
                  </td>
                  <td style="width: 100px; ,max-width: 100px;">
                    <el-switch
                      v-model="readingRule.status"
                      @change="changeRuleStatus(readingRule)"
                      class="Notification-toggle"
                      active-color="rgba(57, 178, 194, 0.8)"
                      inactive-color="#e5e5e5"
                    ></el-switch>
                  </td>
                  <td style="width: 15%;">
                    <div
                      class="text-left actions"
                      style="margin-top:0px;margin-right: 15px;text-align:center;"
                    >
                      <i
                        class="el-icon-edit pointer"
                        @click="editThresholdRule(readingRule)"
                      ></i>
                      &nbsp;&nbsp;
                      <i
                        class="el-icon-delete pointer"
                        @click="
                          deleteRule(
                            readingRule,
                            list.category,
                            index,
                            ruleIndex
                          )
                        "
                      ></i>
                    </div>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <el-dialog title="Confirm Delete" :visible.sync="showDialog" width="40%">
      <div>Are you sure you want to delete this?</div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showDialog = false">CANCEL</el-button>
        <el-button
          type="primary"
          @click="
            showDialog = false
            deleteRule()
          "
          >CONFIRM</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
import NewThresholdRule from 'pages/setup/new/NewThresholdRule'
export default {
  components: {
    NewThresholdRule,
  },
  title() {
    return 'Alarm Rules'
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  data() {
    return {
      loading: true,
      readingRules: [],
      isNew: true,
      isDependant: false,
      rule: {},
      loadThresholdRuleForm: false,
      selectedRule: null,
      showDialog: false,
      thresholddialog: false,
      parentRuleName: null,
      closepopover: false,
      subheaderMenu: [
        {
          label: 'Asset',
          path: { path: '/app/setup/alarmsettings/alarmrules/asset' },
        },
        {
          label: 'Space',
          path: { path: '/app/setup/alarmsettings/alarmrules/space' },
        },
      ],
      categoryTypes: [{ label: 'All Alarm Rules', value: 'all' }],
      categoryType: 'all',
    }
  },
  mounted() {
    this.loadReadingRules()
  },
  computed: {
    assetCategoryList() {
      return this.$store.state.assetCategory
    },
  },
  methods: {
    getRuleName: function(ruleId) {
      let rules = this.readingRules.map(list => list.rules)
      return rules.find(rule => rule.id === ruleId).name
    },
    getResourceName(rule) {
      if (rule.assetCategoryId > 0) {
        let message
        let isIncluded = rule.includedResources && rule.includedResources.length
        let selectedCount
        if (isIncluded) {
          selectedCount = rule.includedResources.length
        } else if (rule.excludedResources && rule.excludedResources.length) {
          selectedCount = rule.excludedResources.length
        }
        let categoryName = this.getCategoryName(rule.assetCategoryId)
        if (selectedCount) {
          message =
            (isIncluded ? selectedCount : 'Some') +
            ' ' +
            categoryName +
            (!isIncluded || selectedCount > 1 ? 's' : '')
        } else {
          message = 'All ' + categoryName + 's'
        }
        return message
      } else if (rule.resourceId > 0) {
        return rule.matchedResources[Object.keys(rule.matchedResources)[0]].name
      }
      return '---'
    },
    getCategoryName(categoryId) {
      if (categoryId > 0 && this.assetCategoryList) {
        let category = this.assetCategoryList.find(
          category => category.id === categoryId
        )
        if (category) {
          return category.name
        }
      }
    },
    aftersave() {
      this.closeCreateDialog()
    },
    newThresholdRule() {
      this.loadThresholdRuleForm = true
      this.isNew = true
      this.rule = {}
      this.isDependant = false
      this.thresholddialog = true
    },
    newThresholdDependantRule(rule) {
      this.loadThresholdRuleForm = true
      this.isNew = true
      this.isDependant = true
      this.rule = this.$helpers.cloneObject(rule)
      this.parentRuleName = this.rule.name
      this.thresholddialog = true
    },
    editThresholdRule(rule) {
      let self = this
      self.$util.fetchRule('alarm', rule.id).then(function(rule) {
        self.loadThresholdRuleForm = true
        self.isNew = false
        self.isDependant = false
        self.rule = rule
        if (self.rule.parentRuleId !== -1) {
          self.parentRuleName = self.getRuleName(self.rule.parentRuleId)
        }
        self.thresholddialog = true
      })
    },
    changeRuleStatus(rule) {
      this.$util
        .changeRuleStatus('alarm', rule.id, rule.status)
        .then(function(response) {})
        .catch(function(error) {
          console.log(error)
        })
    },
    loadReadingRules() {
      this.loading = true
      this.$util.loadRules('alarm').then(rules => {
        this.readingRules = []
        if (rules) {
          let spaceRules = []
          let energyRules = []
          let chillerRules = []
          let fcuRules = []
          let otherRules = {}
          rules.forEach(rule => {
            let category
            if (rule.assetCategoryId > 0) {
              category = this.getCategoryName(rule.assetCategoryId)
            } else if (rule.resourceId > 0) {
              let resource =
                rule.matchedResources[Object.keys(rule.matchedResources)[0]]
              if (resource.resourceType === 2) {
                category = this.getCategoryName(resource.category.id)
              } else {
                spaceRules.push(rule)
              }
            }
            if (category) {
              if (category === 'Energy Meter') {
                energyRules.push(rule)
              } else if (category === 'Chiller') {
                chillerRules.push(rule)
              } else if (category === 'FCU') {
                fcuRules.push(rule)
              } else {
                if (!otherRules[category]) {
                  otherRules[category] = []
                }
                otherRules[category].push(rule)
              }
            }
          })
          if (energyRules.length) {
            this.readingRules.push({
              category: 'Energy Meter',
              rules: energyRules,
            })
            this.categoryTypes.push({
              label: 'Energy Meter Rules',
              value: 'Energy Meter',
            })
          }
          if (chillerRules.length) {
            this.readingRules.push({ category: 'Chiller', rules: chillerRules })
            this.categoryTypes.push({
              label: 'Chiller Rules',
              value: 'Chiller',
            })
          }
          if (fcuRules.length) {
            this.readingRules.push({ category: 'FCU', rules: fcuRules })
            this.categoryTypes.push({ label: 'FCU Rules', value: 'FCU' })
          }
          for (let category in otherRules) {
            let rules = otherRules[category]
            this.readingRules.push({ category: category, rules: rules })
            this.categoryTypes.push({
              label: category + ' Rules',
              value: category,
            })
          }
          if (spaceRules.length) {
            this.readingRules.push({
              category: 'All Spaces',
              rules: spaceRules,
            })
            this.categoryTypes.push({
              label: 'All Space Rules',
              value: 'All Spaces',
            })
          }
        }
        this.loading = false
      })
    },
    deleteRule(rule, category, idx, ruleIndex) {
      this.$dialog
        .confirm({
          title: 'Delete Rule',
          message: 'Are you sure you want to delete this rule?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            this.$util.deleteRule('alarm', rule.id).then(response => {
              this.readingRules[idx].rules.splice(ruleIndex, 1)
              if (!this.readingRules[idx].rules.length) {
                this.readingRules.splice(idx, 1)
              }
            })
          }
        })
    },
    closeCreateDialog() {
      this.loadReadingRules()
    },
    selectedRuleTypes(category) {
      this.categoryType = category
      this.closepopover = false
    },
  },
}
</script>
<style>
.select-all-rule {
  margin-left: 30px;
  margin-top: 30px;
}
.select-all-rule .el-input__suffix-inner .el-select__caret {
  color: #333333;
  font-weight: 900;
  font-size: 16px;
}
.list-rule-head {
  background-color: #f3f5f7 !important;
}
.list-rule-head td {
  border-top: none;
  border-top: none;
  border-left: none;
  border-right: none;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.3;
  letter-spacing: 0.6px;
  color: #333333;
  color: #333333;
  font-size: 14px;
  border-collapse: separate;
  padding: 14px 30px;
}
.rules-count {
  color: #96a9bc;
  font-size: 12px;
  letter-spacing: 0.6px;
}
.alarmrule-list-popover {
  padding-top: 10px;
  padding-bottom: 10px;
}
</style>
