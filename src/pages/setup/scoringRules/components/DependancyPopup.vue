<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog condition-manager-form"
    style="z-index: 999999"
    :before-close="closeDialog"
  >
    <el-form
      :model="dependency"
      :rules="rules"
      :label-position="'top'"
      ref="score-dependency"
      class="fc-form weightage-section"
    >
      <div class="new-header-container flex-center-row-space">
        <div class="fc-setup-modal-title">
          {{
            isNew
              ? $t('common._common.add_dependency')
              : $t('common.header.edit_dependancy')
          }}
        </div>
        <div v-if="!isNew" class="pointer" @click="removeDependency()">
          <inline-svg
            src="svgs/delete"
            class="f-delete vertical-middle"
            iconClass="icon icon-sm icon-remove"
          ></inline-svg>
        </div>
      </div>

      <div class="new-body-modal enpi-body-modal">
        <div class="body-scroll">
          <template v-if="loading">
            <template v-for="index in [1, 2]">
              <el-row class="mB10" :key="index">
                <el-col :span="24">
                  <span class="lines loading-shimmer width50 mB10"></span>

                  <span
                    class="lines loading-shimmer width100 mB10 height40"
                  ></span>
                </el-col>
              </el-row>
            </template>
          </template>

          <template v-else>
            <el-form-item :label="$t('common.products.name')" prop="name">
              <el-input
                class="width100 fc-input-full-border2"
                autofocus
                v-model="dependency.name"
                type="text"
                :placeholder="$t('common._common.enter_dependency_name')"
              />
            </el-form-item>

            <el-form-item
              :label="$t('common.products.weightage')"
              prop="weightage"
            >
              <el-input
                :placeholder="
                  $t('common.header.please_enter_dependency_weightage')
                "
                v-model="dependency.weightage"
                type="number"
                class="width100 fc-input-full-border2"
              >
                <template slot="append">%</template>
              </el-input>
            </el-form-item>

            <el-form-item :label="$t('common.header.source')" prop="nodeType">
              <el-radio-group
                v-model="dependency.nodeType"
                @change="setNodeType"
                class="mB15"
              >
                <el-radio class="fc-radio-btn" :label="nodeTypes.MODULE">
                  {{ moduleName }}
                </el-radio>
                <el-radio class="fc-radio-btn" :label="nodeTypes.SUB_MODULE">
                  {{ $t('common._common.sub_module') }}
                </el-radio>
              </el-radio-group>
            </el-form-item>

            <el-row v-if="dependency.nodeType === nodeTypes.SUB_MODULE">
              <el-col :span="12">
                <el-form-item
                  :label="$t('common._common.sub_module')"
                  prop="fieldModuleId"
                >
                  <el-select
                    v-model="dependency.fieldModuleId"
                    filterable
                    :loading="subModuleLoading"
                    @change="resetForSubModule"
                    :placeholder="$t('common.products.select_sub_module')"
                    class="fc-input-full-border-select2 width100"
                  >
                    <el-option
                      v-for="(subModule, index) in subModulesList"
                      :key="`${subModule.moduleId}-${index}`"
                      :label="subModule.displayName"
                      :value="subModule.moduleId"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>

              <el-col :span="12" class="pL10">
                <el-form-item :label="$t('common.header.field')" prop="fieldId">
                  <el-select
                    v-model="dependency.fieldId"
                    filterable
                    :disabled="$validation.isEmpty(dependency.fieldModuleId)"
                    :loading="subModuleFieldsLoading"
                    :placeholder="
                      $t('common.products.select_sub_module_lookup_field')
                    "
                    class="fc-input-full-border-select2 width100"
                  >
                    <el-option
                      v-for="(subModuleField, index) in subModulesLookupFields"
                      :key="`${subModuleField.id}-${index}`"
                      :label="subModuleField.displayName"
                      :value="subModuleField.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item
              :label="$t('common.products.score_rule')"
              prop="scoreRuleId"
            >
              <el-select
                v-model="dependency.scoreRuleId"
                filterable
                :disabled="isScoreRuleDisable"
                :loading="scoringRuleLoading"
                :placeholder="$t('common.products.select_score_rule')"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="(score, index) in scoringRulesList"
                  :key="`${score.id}-${index}`"
                  :label="score.name"
                  :value="score.id"
                ></el-option>
              </el-select>
            </el-form-item>

            <template v-if="dependency.nodeType === nodeTypes.SUB_MODULE">
              <el-form-item class="mT30">
                <el-checkbox v-model="dependency.shouldBePropagated">
                  {{ $t('common.dialog.should_be_propagated') }}
                </el-checkbox>
              </el-form-item>

              <el-form-item prop="criteria">
                <div class="fc-pink f12 text-uppercase bold line-height20 mT20">
                  {{ $t('common._common._criteria') }}
                </div>
                <div class="label-txt-black line-height20">
                  {{ $t('common._common.specify_criteria_for_sub_module') }}
                </div>
                <NewCriteriaBuilder
                  class="stateflow-criteria mT10"
                  ref="criteriaBuilder"
                  v-model="dependency.criteria"
                  :exrule="dependency.criteria"
                  @condition="newValue => (dependency.criteria = newValue)"
                  :module="subModuleName || moduleName"
                  :isRendering.sync="criteriaRendered"
                  :hideTitleSection="true"
                  :disable="$validation.isEmpty(dependency.fieldModuleId)"
                ></NewCriteriaBuilder>
              </el-form-item>
            </template>
          </template>
        </div>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">
          {{ $t('common._common.cancel') }}
        </el-button>
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="submitForm()"
          :loading="saving"
        >
          {{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}
        </el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import { isEmpty } from '@facilio/utils/validation'
import { ScoreDependency } from '../Models/DependencyModel'
import { ScoringRule } from '../Models/ScoringRuleModel'

const nodeTypes = {
  MODULE: 2,
  SUB_MODULE: 1,
}

export default {
  props: ['moduleName', 'currentModule', 'ruleId', 'selectedDependency'],
  components: { NewCriteriaBuilder },

  data() {
    return {
      dependency: null,
      subModuleName: null,
      loading: false,
      criteriaRendered: false,
      scoringRuleLoading: false,
      scoringRulesList: [],
      subModuleLoading: false,
      subModulesList: [],
      subModuleFieldsLoading: false,
      subModulesLookupFields: [],
      saving: false,
      nodeTypes,
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
        weightage: {
          required: true,
          message: this.$t('common._common.please_enter_a_weightage'),
          trigger: 'change',
        },
        scoreRuleId: {
          required: true,
          message: this.$t('common.header.please_select_a_score_rule'),
          trigger: 'blur',
        },
        fieldModuleId: {
          required: true,
          message: this.$t('common.header.please_select_a_sub_module'),
          trigger: 'blur',
        },
        fieldId: {
          required: true,
          message: this.$t(
            'common.header.please_select_sub_module_lookup_field'
          ),
          trigger: 'blur',
        },
      },
    }
  },

  async created() {
    this.init()
  },

  computed: {
    isNew() {
      return isEmpty(this.selectedDependency)
    },
    isScoreRuleDisable() {
      let { dependency } = this
      let { fieldModuleId, nodeType } = dependency

      return nodeType === nodeTypes.SUB_MODULE && isEmpty(fieldModuleId)
    },
  },

  methods: {
    async init() {
      this.loading = true
      if (!this.isNew) {
        let { fieldModuleId, nodeType } = this.selectedDependency

        if (nodeType === nodeTypes.SUB_MODULE) {
          await this.loadSubModulesList()

          let { name } =
            this.subModulesList.find(s => s.moduleId === fieldModuleId) || {}

          this.subModuleName = name
          await this.getSubModuleLookupFields()
        }
        this.dependency = this.selectedDependency
      } else {
        this.dependency = new ScoreDependency()
      }
      await this.loadScoringRulesList()
      this.loading = false
    },
    async loadScoringRulesList() {
      this.scoringRuleLoading = true

      try {
        let { subModuleName, moduleName, ruleId } = this
        let scoringRuleModuleName = subModuleName || moduleName

        let rulesList = await ScoringRule.fetchAll(scoringRuleModuleName)
        rulesList = rulesList.map(rule => rule.scoreDetails)

        if (ruleId && scoringRuleModuleName === moduleName) {
          rulesList = rulesList.filter(rule => rule.id !== ruleId)
        }
        this.scoringRulesList = rulesList || []
      } catch (error) {
        this.$message.error(error)
      }

      this.scoringRuleLoading = false
    },
    setNodeType() {
      let { nodeType } = this.dependency
      if (nodeType === nodeTypes.SUB_MODULE) {
        this.$set(this.dependency, 'shouldBePropagated', false)
        this.loadSubModulesList()
      } else {
        this.loadScoringRulesList()
        delete this.dependency.fieldModuleId
        delete this.dependency.fieldId
        delete this.dependency.criteria
        delete this.dependency.shouldBePropagated
      }
      this.$set(this.dependency, 'scoreRuleId', null)
    },
    async loadSubModulesList() {
      this.subModuleLoading = true

      try {
        let { moduleName } = this
        this.subModulesList = await ScoreDependency.getSubModulesList(
          moduleName
        )
      } catch (error) {
        this.$message.error(error)
      }

      this.subModuleLoading = false
    },
    resetForSubModule() {
      let { fieldModuleId } = this.dependency

      this.$set(this.dependency, 'fieldId', null)
      this.$set(this.dependency, 'scoreRuleId', null)

      if (isEmpty(fieldModuleId)) return

      let { name } =
        this.subModulesList.find(s => s.moduleId === fieldModuleId) || {}

      this.subModuleName = name
      this.getSubModuleLookupFields()
      this.loadScoringRulesList()
    },
    async getSubModuleLookupFields() {
      this.subModuleFieldsLoading = true

      try {
        let { subModuleName, currentModule } = this
        let { extendedModuleIds } = currentModule || {}

        this.subModulesLookupFields = await ScoreDependency.loadSubModuleFieldOptions(
          subModuleName,
          extendedModuleIds
        )
      } catch (error) {
        this.$message.error(error)
      }
      this.subModuleFieldsLoading = false
    },
    submitForm() {
      this.$refs['score-dependency'].validate(valid => {
        if (!valid) return

        let serializedData = this.dependency.serialize()
        this.$emit('onSave', serializedData)
        this.closeDialog()
      })
    },
    removeDependency() {
      this.$emit('remove')
      this.closeDialog()
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss">
.weightage-section {
  .lines {
    height: 15px;
    border-radius: 5px;
  }
  .height40 {
    height: 40px !important;
  }
}
</style>
