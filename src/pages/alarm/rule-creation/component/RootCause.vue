<template>
  <div>
    <div id="rootcauses-header" class="section-header">
      {{ $t('alarm.rules.root_cause') }}
    </div>
    <div class="p50 pT10 pB30" style="min-height: 500px">
      <div
        v-if="$validation.isEmpty(rcaSelected)"
        class="rule-basic-info-content"
      >
        <div class="d-flex flex-direction-column text-center">
          <inline-svg
            src="svgs/emptystate/data-empty"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="mT10 empty-text f15 bold">
            {{ $t('alarm.rules.no_root_cause') }}
          </div>
          <div class="mT5 empty-text-desc f13">
            {{ $t('alarm.rules.no_root_cause_created') }}
          </div>
          <div class="inline-block mT20">
            <el-button
              :class="{
                'v2-rules-btn': isV2,
              }"
              class="pT10 pB10 small-border-btn text-uppercase pL15 pR15"
              @click="handleClickEvent()"
              >{{ $t('alarm.rules.add_root_cause') }}</el-button
            >
          </div>
        </div>
      </div>
      <div class="root-cause-container" v-else>
        <div class="flex-center-row-space mB10">
          <div class="desc mT0">
            {{ $t('alarm.rules.possible_desc') }}
          </div>

          <div class="flex flex-direction-row pointer items-center">
            <el-button
              :class="{
                'v2-rules-btn': isV2,
              }"
              class="pT10 pB10 small-border-btn text-uppercase pL15 pR15"
              @click="handleClickEvent()"
              >{{ $t('alarm.rules.add_root_cause') }}</el-button
            >
          </div>
        </div>
        <el-table
          :data="rcaSelected"
          :header-cell-style="{
            background: '#f3f1fc',
          }"
          class="root-cause-table"
          :fit="true"
          style="width: 100%;"
          border
        >
          <el-table-column type="index" label="S.NO." width="100">
          </el-table-column>
          <el-table-column prop="name" label="NAME">
            <template v-slot="rule">
              <div
                class="label-txt-black textoverflow-ellipsis"
                :title="rule.row.name || '---'"
                v-tippy="tippyOptions"
              >
                {{ rule.row.name }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="DESCRIPTION">
            <template v-slot="rule">
              <div class="label-txt-black">
                {{ $getProperty(rule, 'row.description') || '---' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="CATEGORY">
            <template v-slot="rule">
              <div
                class="label-txt-black textoverflow-ellipsis"
                :title="getCategory(rule) || '---'"
                v-tippy="tippyOptions"
              >
                {{ getCategory(rule) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop
            label
            width="130"
            class="visibility-visible-actions"
          >
            <template v-slot="rule">
              <div class="text-center">
                <i
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  @click="deleteItem(rule)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="!$validation.isEmpty(rca.groups)" class="mT30 width100 f14">
          <div class="header">{{ $t('alarm.rules.ranking') }}</div>
          <div class="desc mBT20 required">
            {{ $t('alarm.rules.data_set_interval') }}
          </div>
          <el-select
            class="fc-input-full-border2 ranking-select"
            v-model="rca.dataSetInterval"
            placeholder="Data set interval"
          >
            <el-option
              v-for="(key, label) in dataSetInterval"
              :key="key"
              :label="label"
              :value="key"
            ></el-option>
          </el-select>
          <div class="desc mBT20 required">
            {{ $t('alarm.rules.rule_interval') }}
          </div>
          <el-select
            class="fc-input-full-border2 ranking-select"
            v-model="rca.ruleInterval"
            placeholder="Rule Interval"
          >
            <el-option
              v-for="(key, label) in ruleInterval"
              :key="key"
              :label="label"
              :value="key"
            ></el-option>
          </el-select>
        </div>
        <div v-if="isGroupLoading">
          <Spinner :show="isGroupLoading" size="80"></Spinner>
        </div>
        <template v-else>
          <div v-for="(group, index) in rca.groups" :key="`groups-${index}`">
            <Group
              :group="group"
              :moduleName="moduleName"
              :isEditForm="isEditForm"
              @groupChanged="groupChanged"
              @deleteGroup="deleteGroup"
              :isV2="isV2"
            />
          </div>
        </template>
        <div
          class="d-flex pointer mT20 mB20 pL20 align-center"
          :class="{ 'fc-v2-color': isV2, 'add-group-container': !isV2 }"
          @click="addGroup"
        >
          <div class="align-center">
            <fc-icon
              group="default"
              name="circle-plus"
              size="30"
              color="#ffffff"
            />
          </div>
          <div class="add-group-text bold">
            {{ $t('alarm.rules.add_new_group') }}
          </div>
        </div>
      </div>
    </div>
    <NewRootCause
      v-if="showAddRootCause"
      :closeDialog="() => (showAddRootCause = false)"
      :rulesList="rulesPickList"
      @addRootCause="associateRule"
      :rcaSelected="rcaSelectedValues"
      :isV2="isV2"
    />
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import { isEmpty } from 'util/validation'
import cloneDeep from 'lodash/cloneDeep'
import { v4 as uuid } from 'uuid'
import NewRootCause from './NewRootCause'
import Group from './Group'
import Spinner from '@/Spinner'

const dataSetInterval = {
  '1 Day': 86400000,
  '2 Days': 172800000,
  '3 Days': 259200000,
  '4 Days': 345600000,
  '5 Days': 432000000,
  '6 Days': 518400000,
  '7 Days': 604800000,
  '10 Days': 864000000,
  '15 Days': 1296000000,
  '30 Days': 2592000000,
}

const ruleInterval = {
  '1 Min': 60000,
  '2 Mins': 120000,
  '3 Mins': 180000,
  '4 Mins': 240000,
  '5 Mins': 300000,
  '10 Mins': 600000,
  '15 Mins': 900000,
  '20 Mins': 1200000,
  '30 Mins': 1800000,
  '1 Hr': 3600000,
  '2 Hr': 7200000,
  '3 Hr': 10800000,
  '4 Hr': 14400000,
  '8 Hr': 28800000,
  '12 Hr': 43200000,
  '1 Day': 86400000,
}

const groupModel = {
  id: 0,
  name: '',
  criteria: {},
  conditions: {},
  status: true,
}

export default {
  props: ['alarmRulesObj', 'isEditForm', 'isV2'],
  components: { NewRootCause, Group: Group, Spinner },
  data() {
    return {
      moduleName: 'newreadingalarm',
      categoryId: null,
      showAddRootCause: false,
      rulesPickList: [],
      rcaSelectedValues: [],
      rulesList: [],
      rcaSelected: [],
      dataSetInterval: dataSetInterval,
      ruleInterval: ruleInterval,
      rcaGroups: [],
      rca: {
        dataSetInterval: null,
        ruleInterval: null,
        rcaRuleIds: [],
        groups: [],
      },
      isGroupLoading: false,
      tippyOptions: {
        placement: 'top',
        animation: 'shift-away',
        arrow: true,
      },
    }
  },
  created() {
    this.prefillRCARules()
  },
  computed: {
    ...mapState({
      assetCategoryList: state => state.assetCategory,
    }),
  },
  watch: {
    rca: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.$emit('ruleDetailsChange', { rca: newVal })
        }
      },
      deep: true,
    },
  },

  methods: {
    async prefillRCARules() {
      let { isEditForm } = this
      if (isEditForm) {
        this.initRCARules()
      }
    },
    async handleClickEvent() {
      this.showAddRootCause = true
      await this.initRCARules()
    },
    async initRCARules() {
      await this.loadRules()
      let { alarmRulesObj } = this
      let { rca } = alarmRulesObj || {}
      if (!isEmpty(rca)) {
        this.rca = cloneDeep(rca)
        let { rcaRuleIds } = this.rca || {}
        if (!isEmpty(rcaRuleIds)) this.associateRule(rcaRuleIds)
      }
    },
    async loadRules() {
      let { alarmRulesObj } = this
      let { assetCategory, id: ruleId } = alarmRulesObj || {}
      let { id: assetCategoryId } = assetCategory || {}
      let moduleName = this.getCategoryName(assetCategoryId)

      let { data, error } = await API.get('v3/readingrule/fetchRules', {
        moduleName,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { result } = data
        if (this.isEditForm) {
          this.rulesList = result.filter(rule => rule.id !== ruleId)
        } else {
          this.rulesList = result
        }
        let { rulesList } = this
        if (!isEmpty(result)) {
          this.rulesPickList = rulesList.map(rule => {
            return { label: rule.name, value: rule.id }
          })
        }
      }
    },
    associateRule(selectedValues) {
      this.rcaSelectedValues = selectedValues
      let { rulesList } = this
      this.rcaSelected = []
      selectedValues.forEach(id => {
        let currRuleList = rulesList.find(rule => rule.id === id)
        this.rcaSelected.push(currRuleList)
      })

      this.rca.rcaRuleIds = selectedValues
    },
    deleteItem(rule) {
      let { rcaSelected } = this
      let { row: { id } = {} } = rule
      let index = rcaSelected.findIndex(deleteRule => deleteRule.id === id)
      this.rcaSelected.splice(index, 1)
      this.rcaSelectedValues.splice(index, 1)
      this.rca.rcaRuleIds = this.rcaSelectedValues
    },
    getCategoryName(assetCategoryId) {
      let { assetCategoryList } = this || {}
      if (assetCategoryId > 0 && assetCategoryList) {
        let category = assetCategoryList.find(
          category => category.id === assetCategoryId
        )
        if (!isEmpty(category)) {
          let { moduleName } = category || {}
          return moduleName
        }
      }
    },
    initGroups() {
      let groupObjectModel = cloneDeep(groupModel)
      let id = uuid().substring(0, 4)
      groupObjectModel.id = id
      let { rca } = this
      let { groups } = rca || {}
      if (isEmpty(groups) && Array.isArray(groups))
        this.rca.groups.push(groupObjectModel)
    },

    groupChanged(group) {
      let { rca } = this
      let { groups } = rca || []
      let index = groups.findIndex(rcaGroup => rcaGroup.id === group.id)
      groups[index] = group
      this.$set(this.rca, 'groups', cloneDeep(groups))
    },

    addGroup() {
      let { rca: { groups } = {} } = this
      if (isEmpty(groups)) {
        this.rca.dataSetInterval = 604800000
        this.rca.ruleInterval = 3600000
      }
      let groupObjectModel = cloneDeep(groupModel)
      let id = uuid().substring(0, 4)
      groupObjectModel.id = id
      this.rca.groups.push(groupObjectModel)
    },

    deleteGroup(group) {
      this.isGroupLoading = true
      let { rca } = this
      let { groups } = rca || []
      let index = groups.findIndex(rcaGroup => rcaGroup.id === group.id)
      groups.splice(index, 1)
      this.$set(this.rca, 'groups', cloneDeep(groups))
      this.$nextTick(() => {
        this.isGroupLoading = false
      })
    },
    getCategoryDisplayName(assetCategoryId) {
      let { assetCategoryList } = this
      if (assetCategoryId > 0 && Array.isArray(assetCategoryList)) {
        let category = assetCategoryList.find(
          category => category.id === assetCategoryId
        )
        if (!isEmpty(category)) {
          let { displayName } = category || {}
          return displayName
        }
      }
    },
    getCategory(rule) {
      let { row } = rule || {}
      let { assetCategory } = row || {}
      let { id } = assetCategory || {}
      return this.getCategoryDisplayName(id)
    },
  },
}
</script>

<style lang="scss" scoped>
.required {
  color: #e6333d !important;
}
.rule-index {
  color: #91b3b6;
  margin-right: 10px;
}
.root-cause-container {
  padding-right: 10%;
}

.header {
  font-size: 12px;
  font-weight: 500;
  line-height: normal;
  letter-spacing: 1px;
  color: #ee508f;
}

.mBT20 {
  margin: 20px 0px 20px;
}
.desc {
  font-size: 14px;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #6b7e91;
}

.ranking-select {
  min-width: 763px;
  min-height: 40px;
}

.add-group-container {
  color: #fff;
  border-radius: 4px;
  background-color: #38b2c2;
}

.add-group-text {
  margin: 20px 5px 20px;
  font-size: 14px;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #fff;
}

.configured-green {
  color: #5bc293;
}
.trash-icon {
  color: #de7272;
}
</style>
<style lang="scss">
.root-cause-table {
  .el-table__row td {
    padding-left: 20px;
  }
}
</style>
