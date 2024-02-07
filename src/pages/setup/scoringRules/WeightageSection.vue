<template>
  <div>
    <div id="weightage-header" class="section-header">
      {{ $t('common.products.score_weightage') }}
    </div>

    <div class="p50 pT10 pR70 pB30">
      <div
        v-for="(score, index) in scoreWeightage"
        :key="`${score.name}-${index}`"
        class="weightage-grp"
      >
        <template v-if="scoreWeightage.length > 1">
          <div class="reorder-txt" @click="showReorderPopup = true">
            {{ $t('common._common.reorder') }}
          </div>
          <div class="delete-grp pointer" @click="details.removeGrp(index)">
            <inline-svg
              :key="`delete-${index}`"
              src="svgs/delete"
              class="f-delete vertical-middle"
              iconClass="icon icon-sm icon-remove"
              :title="$t('common.wo_report.delete_group')"
              v-tippy="{ placement: 'top', arrow: true }"
            ></inline-svg>
          </div>
        </template>

        <el-form
          :ref="`weightageForm-${index}`"
          :model="score"
          :rules="rules"
          label-position="top"
        >
          <el-form-item :label="$t('common.header.group_name')" prop="name">
            <el-input
              class="width50 fc-input-full-border2"
              v-model="score.name"
              :placeholder="$t('common._common.enter_group_name')"
            />
          </el-form-item>
        </el-form>

        <ConditionAndDependencySection
          :title="$t('common._common.dependencies')"
          recordTitle="Dependancy"
          tableNameCol="name"
          :score="score"
          :scoreType="scoreTypes.DEPENDENCY"
          @add="addNewDependency(index)"
          @edit="data => editDependency(data, index)"
        ></ConditionAndDependencySection>

        <ConditionAndDependencySection
          :title="$t('common.products.conditions')"
          recordTitle="Condition"
          tableNameCol="namedCriteria.name"
          :score="score"
          :scoreType="scoreTypes.CONDITION"
          @add="addNewCondition(index)"
          @edit="data => editCondition(data, index)"
        ></ConditionAndDependencySection>

        <div class="overall-weightage">
          <div class="pR20 mL-auto">
            {{ $t('common.header.overall_weightage') }}
          </div>
          <div class="pR20 width200px text-right">
            {{ score.getGroupTotalWeightage() }}%
          </div>
        </div>

        <div class="weightage-sub-header mT20" style="margin-bottom: 20px;">
          <div>
            <div class="weightage-sub-header-text">
              {{ $t('common._common._criteria') }}
            </div>
            <div class="weightage-sub-header-descrip">
              {{
                $t(
                  'common._common.specify_which_modulename_this_commitment_applies_to',
                  { moduleName }
                )
              }}
            </div>
          </div>

          <el-switch
            v-model="score.iscriteriaEnable"
            :disabled="scoreWeightage.length > index + 1"
            @change="score.namedCriteriaId = null"
            class="mL-auto mT5"
            active-color="rgba(57, 178, 194, 0.8)"
            inactive-color="#e5e5e5"
          ></el-switch>
        </div>

        <el-select
          v-if="score.iscriteriaEnable"
          class="fc-input-full-border2 width300px fc-tag"
          v-model="score.namedCriteriaId"
          filterable
          collapse-tags
          :loading="criteriaLoading"
          placeholder="Select Criteria"
        >
          <el-option
            v-for="criteria in criteriaList"
            :key="criteria.id"
            :label="criteria.name"
            :value="criteria.id"
          ></el-option>
        </el-select>
      </div>

      <el-tooltip
        :content="
          $t('common.header.please_define_score_weightage_group_groups')
        "
        :disabled="canAddGroup"
        placement="right"
        class="mT30"
      >
        <div class="inline">
          <el-button
            :disabled="!canAddGroup"
            @click="details.addGroup()"
            class="task-add-btn mT5"
          >
            <img src="~assets/add-blue.svg" />
            <span class="btn-label mL5">
              {{ $t('common.header.add_group') }}
            </span>
          </el-button>
        </div>
      </el-tooltip>
    </div>

    <ScoringConditionManagerForm
      v-if="showCriteriaForm"
      :selectedCriteria="selectedCriteria"
      :moduleName="moduleName"
      @remove="removeCondition"
      @onSave="saveCondition"
      @onClose="showCriteriaForm = false"
    ></ScoringConditionManagerForm>

    <ScoringDependancyForm
      v-if="showDependencyForm"
      :selectedDependency="selectedDependency"
      :moduleName="moduleName"
      :currentModule="currentModule"
      :ruleId="ruleId"
      @remove="removeDependency"
      @onSave="saveDependency"
      @onClose="showDependencyForm = false"
    ></ScoringDependancyForm>

    <ReorderPopup
      v-if="showReorderPopup"
      :scoreWeightage="scoreWeightage"
      @onSave="weightage => (scoreWeightage = weightage)"
      @onClose="showReorderPopup = false"
    ></ReorderPopup>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import ScoringConditionManagerForm from './components/ConditionManagerPopup'
import ScoringDependancyForm from './components/DependancyPopup'
import ConditionAndDependencySection from './components/ConditionAndDependencyLayout'
import ReorderPopup from './components/GroupReorderPopup'

const scoreTypes = {
  CONDITION: 1,
  DEPENDENCY: 2,
}

export default {
  props: ['moduleName', 'details', 'isNew', 'currentModule'],
  components: {
    ScoringConditionManagerForm,
    ScoringDependancyForm,
    ConditionAndDependencySection,
    ReorderPopup,
  },

  data() {
    return {
      scoreWeightage: [],
      grpOrder: 1,
      criteriaList: [],
      criteriaLoading: false,
      showCriteriaForm: false,
      selectedCriteria: null,
      selectedCriteriaIdx: null,
      showDependencyForm: false,
      selectedDependency: null,
      selectedDependencyIdx: null,
      selectedGrpIdx: null,
      showReorderPopup: false,
      scoreTypes,
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'blur',
        },
      },
    }
  },

  created() {
    this.scoreWeightage = this.details.scoringContexts
    this.loadCriteriaList()
  },

  computed: {
    canAddGroup() {
      let scoreGrpLength = this.scoreWeightage.length
      let { namedCriteriaId } = this.scoreWeightage[scoreGrpLength - 1] || {}

      return !isEmpty(namedCriteriaId)
    },
    ruleId() {
      if (!this.isNew) return this.details.id
      else return null
    },
  },

  methods: {
    async loadCriteriaList() {
      this.criteriaLoading = true

      let { moduleName } = this
      let { error, data } = await API.post('v2/namedCriteria/list', {
        moduleName,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.criteriaList = data.namedCriteriaList || []
      }
      this.criteriaLoading = false
    },
    addNewCondition(index) {
      this.selectedCriteria = null
      this.selectedCriteriaIdx = null
      this.selectedGrpIdx = index
      this.showCriteriaForm = true
    },
    editCondition(condition, index) {
      let { baseScoringContexts } = this.scoreWeightage[index]

      this.selectedCriteriaIdx = baseScoringContexts.findIndex(
        c => c.namedCriteriaId === condition.namedCriteriaId
      )
      this.selectedGrpIdx = index
      this.selectedCriteria = condition
      this.showCriteriaForm = true
    },
    saveCondition(condition) {
      let scoreWeightageGrp = this.scoreWeightage[this.selectedGrpIdx]
      scoreWeightageGrp.saveCondition(condition, this.selectedCriteriaIdx)
    },
    removeCondition() {
      let scoreWeightageGrp = this.scoreWeightage[this.selectedGrpIdx]
      scoreWeightageGrp.removeCondition(this.selectedCriteriaIdx)
    },
    addNewDependency(index) {
      this.selectedDependency = null
      this.selectedDependencyIdx = null
      this.selectedGrpIdx = index
      this.showDependencyForm = true
    },
    editDependency(dependency, index) {
      let { baseScoringContexts } = this.scoreWeightage[index]

      this.selectedDependencyIdx = baseScoringContexts.findIndex(
        d => d.name === dependency.name
      )
      this.selectedGrpIdx = index
      this.selectedDependency = dependency
      this.showDependencyForm = true
    },
    saveDependency(dependency) {
      let scoreWeightageGrp = this.scoreWeightage[this.selectedGrpIdx]
      scoreWeightageGrp.saveDependency(dependency, this.selectedDependencyIdx)
    },
    removeDependency() {
      let scoreWeightageGrp = this.scoreWeightage[this.selectedGrpIdx]
      scoreWeightageGrp.removeDependency(this.selectedDependencyIdx)
    },
    validateForm() {
      let promises = this.scoreWeightage.map((score, index) => {
        let formRef = `weightageForm-${index}`
        return this.$refs[formRef][0].validate()
      })
      return promises
    },
    async validate() {
      let isGrpScoreValid = this.scoreWeightage.every(
        s => s.getGroupTotalWeightage() === 100
      )
      let grpScorePromise = new Promise(resolve => {
        if (!isGrpScoreValid) {
          this.$message.error(
            this.$t('common._common.each_group_weight_exactly')
          )
          resolve(false)
        } else {
          resolve(true)
        }
      })
      try {
        let result = await Promise.all([
          ...this.validateForm(),
          grpScorePromise,
        ])
        return result.every(r => r)
      } catch {
        return false
      }
    },
  },
}
</script>
<style lang="scss">
.weightage-grp {
  border: 1px solid #e5ebf0;
  padding: 20px 30px;
  margin-bottom: 15px;

  .delete-grp {
    position: absolute;
    right: 100px;
    z-index: 1;
    color: #ff0000;
  }
  .weightage-sub-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 30px;
  }
  .weightage-sub-header-text {
    letter-spacing: 1px;
    color: #ee508f;
    font-size: 12px;
    font-weight: 500;
    text-transform: uppercase;
  }
  .weightage-sub-header-descrip {
    margin-top: 10px;
    letter-spacing: 0.5px;
    color: #6b7e91;
    font-size: 14px;
  }
  .fill-green {
    svg {
      path {
        fill: #23b096;
      }
    }
  }
  .add-weightage-sub-score {
    font-size: 13px;
    font-weight: 500;
    letter-spacing: 0.5px;
    color: #39b2c2;
    margin-left: 5px;
  }
  .empty-state {
    display: flex;
    justify-content: center;
    flex-direction: column;
    padding: 25px 0px;

    .weightage-add-btn {
      padding: 10px 20px;
      border: 1px solid #39b2c2;
      min-height: 36px;
      margin: 20px auto;

      .weightage-add-btn-text {
        font-size: 12px;
        font-weight: 500;
        color: #39b2c2;
        letter-spacing: 0.5px;
        text-transform: uppercase;
        margin-left: 5px;
      }
      &:hover {
        background: transparent;
      }
    }
  }
  .el-table__footer-wrapper tbody td {
    font-weight: bold;
    color: #5a5e66;
    background: transparent;
    text-transform: uppercase;
    font-size: 13px;
  }
  .el-table__footer-wrapper tbody td:first-of-type {
    display: flex;
  }
  .overall-weightage {
    font-weight: bold;
    font-size: 14px;
    text-transform: uppercase;
    color: #5a5e66;
    padding: 20px 0px;
    margin-bottom: 20px;
    line-height: 23px;
    display: flex;
  }
  .reorder-txt {
    font-size: 12px;
    letter-spacing: 0.5px;
    color: #6171db;
    position: absolute;
    right: 130px;
    z-index: 1;
    cursor: pointer;
  }
}
</style>
