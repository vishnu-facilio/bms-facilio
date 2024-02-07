<template>
  <div>
    <div
      id="commitment-header"
      class="section-header commitments-section-header commitments-section-header-text"
    >
      {{ $t('setup.setupLabel.commitments') }}
    </div>
    <div class="p50 pT0 pR70 pB30">
      <p class="fc-sub-title-desc">
        {{ $t('setup.setupLabel.sla_commitment_header_desc') }}
      </p>
      <template v-for="(commitment, index) in commitments">
        <div
          class="rule-border-blue p20 mB15 position-relative"
          style="border-left: 1px solid rgb(228, 235, 241);"
          :key="`collapse-${index}`"
          :name="index"
        >
          <div
            class="delete-commitment pointer"
            @click="removeCommitment(index)"
          >
            <inline-svg
              v-if="commitments.length > 1"
              :key="`delete-${index}`"
              src="svgs/delete"
              class="f-delete vertical-middle"
              iconClass="icon icon-sm icon-remove"
              title="Delete commitment"
              v-tippy="{ placement: 'top', arrow: true }"
            ></inline-svg>
          </div>

          <el-form
            :key="`commitments-${index}-sla-${commitment}`"
            :model="commitment"
            :rules="rules"
            label-width="220px"
            label-position="left"
          >
            <el-row class="mB10">
              <el-col :span="20">
                <el-form-item
                  prop="name"
                  :label="$t('setup.setupLabel.commitment_name')"
                  class="mB10"
                >
                  <el-input
                    v-model="commitment.name"
                    class="fc-input-full-border2"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row class="mT30">
              <el-col :span="24" style="max-width: 750px">
                <div class="d-flex flex-direction-row mB20">
                  <div class="d-flex align-center width220px">
                    <span class="commitments-section-header">{{
                      $t('setup.setupLabel.sla_commitment_sub_def_header')
                    }}</span>
                    <el-tooltip
                      placement="top-start"
                      :content="
                        $t(
                          'setup.setupLabel.sla_commitment_sub_def_header_info'
                        )
                      "
                    >
                      <i class="el-icon-info pL5 mT2"></i>
                    </el-tooltip>
                  </div>
                  <div class="flex-grow d-flex align-center">
                    <span class="commitments-section-header">{{
                      $t('setup.setupLabel.sla_commitment_sub_def_header2')
                    }}</span>
                  </div>
                </div>
                <div
                  class="mB20"
                  v-for="entity in commitment.slaEntities"
                  :key="`commitment_${index}_sla_${entity.slaEntityId}`"
                >
                  <div class="d-flex flex-direction-row">
                    <div class="d-flex align-center" style="width: 220px">
                      <span class="fc-input-label-txt">
                        {{ getEntityName(entity.slaEntityId) }}
                      </span>
                    </div>
                    <div class="flex-grow d-flex">
                      <DurationField
                        :key="`entity-${index}-sla-${entity.slaEntityId}`"
                        class="width100"
                        v-model="entity.durationPlaceHolder"
                        @change="
                          duration => (entity.durationPlaceHolder = duration)
                        "
                      ></DurationField>
                    </div>
                    <div class="mL20" style="width: 200px">
                      <el-select
                        v-model="entity.type"
                        class="fc-input-full-border-select2 width100 vertical-middle"
                      >
                        <el-option
                          v-for="(field, index) in hourTypes"
                          :key="`approvalType-${index}`"
                          :label="field.label"
                          :value="field.id"
                        ></el-option>
                      </el-select>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>

            <template v-if="index === commitments.length - 1">
              <el-row>
                <el-col :span="15" class="mT20 d-flex">
                  <div class="commitments-section-header flex-middle">
                    {{
                      $t(
                        'setup.setupLabel.commitment_satisfies_these_conditions'
                      )
                    }}
                  </div>
                  <el-switch
                    v-model="canShowCriteria"
                    class="Notification-toggle mL-auto mT5"
                    active-color="rgba(57, 178, 194, 0.8)"
                    inactive-color="#e5e5e5"
                  ></el-switch>
                </el-col>
                <el-col :span="24" v-if="canShowCriteria">
                  <CriteriaBuilder
                    v-model="commitment.criteria"
                    :moduleName="module"
                  />
                </el-col>
              </el-row>
            </template>
            <template v-else>
              <CriteriaBuilder
                v-model="commitment.criteria"
                :moduleName="module"
              />
            </template>
          </el-form>
        </div>
      </template>

      <el-row class="mT30 pB30">
        <el-col :span="24">
          <el-tooltip
            :content="
              `Please define commitment criteria to add more commitments`
            "
            :disabled="canShowCriteria"
            placement="right"
          >
            <div class="inline">
              <el-button
                :disabled="!canShowCriteria"
                @click="addCommitment()"
                class="task-add-btn mT5"
              >
                <img src="~assets/add-blue.svg" />
                <span class="btn-label mL5">
                  {{ $t('setup.add.add_commitment') }}
                </span>
              </el-button>
            </div>
          </el-tooltip>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import clone from 'lodash/clone'
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty } from '@facilio/utils/validation'
import DurationField from '../components/SLADurationField'
import { CriteriaBuilder } from '@facilio/criteria'

const hourTypes = [
  { id: 1, label: 'Calendar Hours' },
  { id: 2, label: 'Business Hours' },
]

export default {
  name: 'NewCommitments',
  props: {
    policy: {
      type: Object,
    },
    entities: {
      type: Array,
    },
  },
  components: { DurationField, CriteriaBuilder },
  data() {
    return {
      commitments: [],
      rules: {},
      hourTypes,
      canShowCriteria: false,
      canWatchForChanges: false,
    }
  },
  computed: {
    policyId() {
      return this.$route.params.id
    },
    isNew() {
      return isEmpty(this.$route.params.id)
    },
    module() {
      return this.$route.params.moduleName
    },
    slaPolicy() {
      return this.sharedData.slaPolicy
    },
    commitmentObj() {
      return {
        name: '',
        criteria: null,
        isCriteriaRendering: false,
        slaEntities: this.entities.map(({ id }) => ({
          slaEntityId: id,
          durationPlaceHolder: null,
          type: 1,
        })),
      }
    },
    haveCriteriasRendered() {
      return this.commitments.every(c => !c.isCriteriaRendering)
    },
  },
  watch: {
    policy: {
      handler: function() {
        this.init()
      },
      immediate: true,
    },
    commitments: {
      handler() {
        if (this.canWatchForChanges && this.haveCriteriasRendered)
          this.$emit('modified')
      },
      deep: true,
    },
  },
  methods: {
    init() {
      if (this.isNew) {
        this.addCommitment()
      } else {
        this.deserialize(cloneDeep(this.policy.commitments || []))
        isEmpty(this.commitments) && this.addCommitment()
      }
      this.$nextTick(() => (this.canWatchForChanges = true))
    },
    updateCriteria(commitment, value) {
      this.$set(commitment, 'criteria', value)
    },
    getEntityName(id) {
      let entity = this.entities.find(e => e.id === id)
      return entity.name || ''
    },
    addCommitment() {
      let { commitments, commitmentObj } = this
      let cloneObj = cloneDeep(commitmentObj)
      commitments.push(cloneObj)
      this.toggleCriteria()
    },
    removeCommitment(index) {
      let { commitments } = this
      commitments.splice(index, 1)
    },
    toggleCriteria() {
      this.canShowCriteria = false
    },
    validate() {
      return Promise.resolve(true)
    },
    deserialize(commitments) {
      this.commitments = commitments.map(commitment => {
        let entityList = commitment.slaEntities || []

        this.entities.forEach(({ id }) => {
          let hasEntity = !isEmpty(entityList.find(e => e.slaEntityId === id))

          if (!hasEntity) {
            entityList.push({
              slaEntityId: id,
              durationPlaceHolder: null,
              type: 1,
            })
          }
        })

        entityList.forEach(entity => {
          if (entity.type === -1) entity.type = 1
        })

        return {
          ...commitment,
          isCriteriaRendering: false,
          slaEntities: entityList,
        }
      })
      if (!isEmpty(commitments)) {
        let lastCommitment = commitments[commitments.length - 1] || {}
        let criteria = this.$getProperty(lastCommitment, 'criteria', null)

        if (!isEmpty(criteria)) this.canShowCriteria = true
      }
    },
    serialize() {
      let commitments = clone(this.commitments)

      return commitments.map((commitment, index) => {
        // Filter entities where duration is empty
        let slaEntities = commitment.slaEntities
          .filter(
            entity =>
              !isEmpty(entity.durationPlaceHolder) ||
              entity.durationPlaceHolder > 0
          )
          .map(entity => {
            delete entity.addDuration
            return entity
          })
        let criteria = !isEmpty(commitment.criteria)
          ? commitment.criteria
          : null

        let criteriaId = commitment.criteriaId || null
        if (!isEmpty(criteriaId) && isEmpty(criteria)) {
          criteriaId = -99
        }

        if (index === commitments.length - 1 && !this.canShowCriteria) {
          criteria = null
          criteriaId = -99
        }

        let data = {
          name: commitment.name,
          slaEntities: slaEntities,
          criteria,
        }
        if (commitment.id) data.id = commitment.id
        // TODO remove isNew check here, check with server if id still required
        if (!this.isNew && criteriaId === -99) data.criteriaId = criteriaId

        return data
      })
    },
  },
}
</script>
<style lang="scss" scoped>
.delete-commitment {
  position: absolute;
  right: 20px;
  top: 20px;
  z-index: 1;
  color: #ff0000;
}
.commitments-section-header {
  color: #3ab2c1 !important;
  font-size: 14px !important;
  font-weight: 500;
  letter-spacing: 1.6px;
}
.commitments-section-header-text {
  padding: 20px 50px 15px !important;
  text-transform: capitalize !important;
}
</style>
