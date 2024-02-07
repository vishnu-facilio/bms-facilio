<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog score-condition"
    style="z-index: 999999"
    :before-close="closeDialog"
  >
    <el-form
      :model="scoreCondition"
      :rules="rules"
      :label-position="'top'"
      ref="condition-manager"
      class="fc-form"
    >
      <div class="new-header-container flex-center-row-space">
        <div class="fc-setup-modal-title">
          {{
            isNew
              ? $t('common.header.add_conditions')
              : $t('common.header.edit_condition')
          }}
        </div>
        <div v-if="!isNew" class="pointer" @click="removeCondition()">
          <inline-svg
            src="svgs/delete"
            class="f-delete vertical-middle"
            iconClass="icon icon-sm icon-remove"
          ></inline-svg>
        </div>
      </div>

      <div class="new-body-modal enpi-body-modal">
        <div class="body-scroll">
          <template v-if="initialLoading">
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
            <el-form-item label="Condition" prop="namedCriteriaId">
              <el-select
                v-model="scoreCondition.namedCriteriaId"
                filterable
                :loading="loading"
                :placeholder="$t('common.products.select_condition')"
                class="fc-input-full-border2 width100 fc-tag"
                popper-class="condition-popover"
              >
                <el-option
                  v-for="criteria in criteriaList"
                  :key="criteria.id"
                  :label="criteria.name"
                  :value="criteria.id"
                ></el-option>

                <div class="search-select-filter-btn pT5">
                  <el-button
                    class="btn-green-full filter-footer-btn fw-bold pT10 pB10"
                    @click="showCreate = true"
                  >
                    {{ $t('common.header.add_new') }}
                  </el-button>
                </div>

                <div slot="empty" class="el-select-dropdown__empty">
                  <div class="empty-text">
                    {{ $t('common._common.nodata') }}
                  </div>
                  <el-button
                    class="btn-green-full filter-footer-btn fw-bold pT10 pB10"
                    @click="showCreate = true"
                  >
                    {{ $t('common.header.add_new') }}
                  </el-button>
                </div>
              </el-select>
            </el-form-item>

            <el-form-item
              :label="$t('common.products.weightage')"
              prop="weightage"
            >
              <el-input
                :placeholder="
                  $t('common.header.please_enter_condition_weightage')
                "
                v-model="scoreCondition.weightage"
                type="number"
                class="width100 fc-input-full-border2 right-border-with-suffix"
              >
                <template slot="append">%</template>
              </el-input>
            </el-form-item>
          </template>
        </div>
      </div>
    </el-form>

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
        {{ saving ? $t('common._common._saving') : $t('common._common._save') }}
      </el-button>
    </div>

    <ConditionManagerForm
      v-if="showCreate"
      :isNew="true"
      :moduleName="moduleName"
      @onSave="setConditions"
      @onClose="showCreate = false"
    ></ConditionManagerForm>
  </el-dialog>
</template>
<script>
import ConditionManagerForm from 'pages/setup/conditionmanager/ConditionManagerForm'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { ScoreConditionManager } from '../Models/ConditionManagerModel'

export default {
  props: ['selectedCriteria', 'moduleName'],
  components: { ConditionManagerForm },

  data() {
    return {
      initialLoading: false,
      scoreCondition: null,
      saving: false,
      criteriaList: [],
      loading: false,
      showCreate: false,
      rules: {
        namedCriteriaId: {
          required: true,
          message: this.$t('common.header.please_select_a_condition'),
          trigger: 'blur',
        },
        weightage: {
          required: true,
          message: this.$t('common._common.please_enter_a_weightage'),
          trigger: 'change',
        },
      },
    }
  },

  async created() {
    this.initialLoading = true
    if (!this.isNew) {
      this.scoreCondition = this.selectedCriteria
    } else {
      this.scoreCondition = new ScoreConditionManager()
    }
    await this.loadCriteriaList()
    this.initialLoading = false
  },

  computed: {
    isNew() {
      return isEmpty(this.selectedCriteria)
    },
  },

  methods: {
    async loadCriteriaList() {
      this.loading = true

      let { moduleName } = this
      let { error, data } = await API.post('v2/namedCriteria/list', {
        moduleName,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.criteriaList = data.namedCriteriaList || []
      }
      this.loading = false
    },
    setConditions(condition) {
      this.criteriaList.push(condition)
      this.loading = true

      this.$nextTick(() => {
        this.scoreCondition.namedCriteriaId = condition.id
        this.loading = false
      })
    },
    submitForm() {
      this.$refs['condition-manager'].validate(valid => {
        if (!valid) return

        let { scoreCondition, criteriaList } = this
        let { namedCriteriaId } = scoreCondition
        let namedCriteria = criteriaList.find(c => c.id === namedCriteriaId)
        let serializedData = scoreCondition.serialize()

        this.$emit('onSave', { ...serializedData, namedCriteria })
        this.closeDialog()
      })
    },
    removeCondition() {
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
.score-condition {
  .add-new-condition {
    color: #39b2c2;
    margin-left: 5px;
    font-size: 13px;
    font-weight: 500;
    letter-spacing: 0.46px;
  }
  .lines {
    height: 15px;
    border-radius: 5px;
  }
  .height40 {
    height: 40px !important;
  }
  .right-border-with-suffix.el-input .el-input__inner {
    border-top-right-radius: 0 !important;
    border-bottom-right-radius: 0 !important;
  }
}
.condition-popover {
  .el-select-dropdown__list {
    padding-bottom: 50px;
  }
  .el-select-dropdown__item {
    font-weight: 400;
  }
  .empty-text {
    padding: 10px 0;
    margin: 0;
    text-align: center;
    color: #999;
    font-size: 14px;
    line-height: 24px;
  }
}
</style>
