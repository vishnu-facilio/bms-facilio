<template>
  <div :style="containerHeight">
    <div
      id="escalation-header"
      class="section-header new-escalation-section-header"
    >
      {{ $t('setup.setupLabel.escalations') }}
    </div>
    <div class="p50 pT0 pR70 pB50">
      <div>
        <p class="fc-sub-title-desc">
          {{ $t('setup.setupLabel.sla_new_escalation_header_desc') }}
        </p>
      </div>
      <template v-for="(escalation, index) in escalations">
        <el-form
          class="mB40"
          :model="escalation"
          :rules="rules"
          label-width="180px"
          label-position="left"
          :key="`escalation-form-${index}`"
        >
          <el-row class="mB15">
            <el-col :span="12">
              <div class="entity-type-header">
                {{ getEntityName(escalation.slaEntityId) }}
              </div>
            </el-col>
            <el-col :span="12">
              <div
                @click="addLevel(escalation)"
                style="color: #3ab2c1"
                class="f13 fR pointer"
              >
                <inline-svg
                  src="svgs/plus-button"
                  style="height:18px;width:18px;margin-right: 4px;"
                  class="vertical-middle fill-greeny-blue"
                />
                {{ $t('setup.add.add_escalation') }}
              </div>
            </el-col>
          </el-row>
          <el-table
            :data="escalation.levels"
            :empty-text="$t('setup.empty.empty_escalations')"
            border
            class="mB10"
            style="width: 100%"
          >
            <el-table-column label="Escalation Level" width="220">
              <template v-slot="{ $index }">
                <div class="pL20" @click="editLevel(escalation, $index)">
                  Level {{ $index + 1 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column
              :label="$t('setup.setupLabel.escalations_point')"
              width="210"
            >
              <template v-slot="{ row }" class="mL10">
                <div class="pL20">
                  {{
                    getEscalationPoint(
                      row,
                      getEntityName(escalation.slaEntityId)
                    )
                  }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="Actions">
              <template v-slot="{ row, $index }">
                <div class="d-flex pL20">
                  <template
                    v-if="!$validation.isEmpty(getEscalationActions(row))"
                  >
                    <div
                      v-for="(action, index) in getEscalationActions(row)"
                      :key="action"
                      class="action-badge"
                      :class="{ mL5: index > 0 }"
                    >
                      {{ action }}
                    </div>
                  </template>
                  <div v-else>No actions configured.</div>
                  <div class="flex-middle mL-auto mR20 actions">
                    <div
                      @click="editLevel(escalation, $index)"
                      style="height: 14px;"
                    >
                      <inline-svg
                        src="svgs/edit"
                        class="edit-icon-color mR20"
                        iconClass="icon icon-sm icon-edit"
                        title="Edit Escalation"
                        v-tippy="{ placement: 'bottom', arrow: true }"
                      ></inline-svg>
                    </div>
                    <div
                      @click="removeLevel(escalation, $index)"
                      style="height: 14px;"
                    >
                      <inline-svg
                        :key="`delete-level-${$index}`"
                        src="svgs/delete"
                        class="f-delete"
                        iconClass="icon icon-sm icon-remove"
                        title="Remove Escalation"
                        v-tippy="{ placement: 'bottom', arrow: true }"
                      ></inline-svg>
                    </div>
                  </div>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-form>
        <NewEscalationLevel
          v-for="(level, index) in escalation.levels"
          :key="escalation.slaEntityId + '_' + index"
          :ref="escalation.slaEntityId + '_' + index"
          :visibility="level.showPopup"
          :entityId="escalation.slaEntityId"
          :entityName="getEntityName(escalation.slaEntityId)"
          :level="level"
          :position="index"
          :onSave="
            levelObj =>
              handleLevelUpdate(escalation.slaEntityId, index, levelObj)
          "
          :onClose="() => (level.showPopup = false)"
        ></NewEscalationLevel>
      </template>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
import NewEscalationLevel from '../components/NewEscalationLevel'

const intervalTypes = [
  {
    id: 1,
    name: 'Before',
  },
  {
    id: 2,
    name: 'On',
  },
  {
    id: 3,
    name: 'After',
  },
]

export default {
  name: 'NewEscalations',
  props: {
    policy: {
      type: Object,
    },
    entities: {
      type: Array,
    },
  },
  components: { NewEscalationLevel },
  data() {
    return {
      escalations: [],
      rules: {},
      intervalTypes,
      showLevelPopup: false,
      canWatchForChanges: false,
    }
  },
  computed: {
    isNew() {
      return isEmpty(this.$route.params.id)
    },
    policyId() {
      return this.$route.params.id
    },
    module() {
      return this.$route.params.moduleName
    },
    slaPolicy() {
      return this.sharedData.slaPolicy
    },
    levelObj() {
      return {
        type: null,
        interval: null,
        actions: [],
        showPopup: false,
      }
    },
    containerHeight() {
      let root = document.querySelector('.scroll-container')
      let height = root.offsetHeight || 370

      return {
        'min-height': height - 60 + 'px',
      }
    },
  },
  watch: {
    policy: {
      handler: function() {
        this.init()
      },
      immediate: true,
    },
    escalations: {
      handler() {
        if (this.canWatchForChanges) this.$emit('modified')
      },
      deep: true,
    },
  },
  methods: {
    init() {
      if (!this.isNew) {
        this.deserialize(cloneDeep(this.policy.escalations))

        if (isEmpty(this.escalations)) this.createEscalations()
      } else {
        this.createEscalations()
      }
      this.$nextTick(() => (this.canWatchForChanges = true))
    },
    serialize() {
      let escalations = cloneDeep(this.escalations)

      return escalations
        .map(escalation => {
          let levels = (escalation.levels || []).map((level, index) => {
            let EscalationPopup = this.$refs[
              escalation.slaEntityId + '_' + index
            ][0]

            // EscalationPopup.initData(level)
            return EscalationPopup.serialize()
          })

          levels
            .filter(level => !isEmpty(level.type))
            .forEach((level, index) => {
              // Serialize level intervals to be successive ie. current value plus sum of all previous vaues
              if (index !== 0) {
                level.interval = level.interval + levels[index - 1].interval
              }
            })

          return {
            slaEntityId: escalation.slaEntityId,
            levels,
          }
        })
        .filter(escalation => !isEmpty(escalation.levels))
    },
    deserialize(escalationsList) {
      escalationsList = escalationsList || []

      this.entities.forEach(({ id }) => {
        let hasEntity = !isEmpty(
          (escalationsList || []).find(e => e.slaEntityId === id)
        )
        if (!hasEntity) {
          escalationsList.push({
            slaEntityId: id,
            levels: [],
          })
        }
      })

      escalationsList.forEach(({ levels }) => {
        let _levels = cloneDeep(levels)
        levels.forEach((level, index) => {
          if (index !== 0) {
            // Deserialize level intervals to be successive ie. reduce value from sum of all previous values.
            level.interval = level.interval - _levels[index - 1].interval
          }
          level.showPopup = false
        })
      })
      this.escalations = escalationsList
    },
    createEscalations() {
      this.escalations = this.entities.map(entity => ({
        slaEntityId: entity.id,
        levels: [],
      }))
    },
    handleLevelUpdate(entityId, position, level) {
      let escalation = this.escalations.find(
        ({ slaEntityId }) => slaEntityId === entityId
      )

      this.$set(escalation.levels, position, level)
    },
    addLevel(escalation) {
      let { levelObj } = this
      let level = { ...cloneDeep(levelObj), showPopup: true }

      this.handleLevelUpdate(
        escalation.slaEntityId,
        escalation.levels.length,
        level
      )
    },
    editLevel(escalation, position) {
      this.$set(escalation.levels[position], 'showPopup', true)
    },
    removeLevel(escalation, index) {
      let { levels } = escalation
      levels.splice(index, 1)
      this.$set(escalation, 'levels', levels)
    },
    getEntityName(id) {
      let entity = this.entities.find(e => e.id === id) || {}
      return entity.name || ''
    },
    getEscalationPoint(row, entityName) {
      if (isEmpty(row.type)) {
        return 'Not Configured'
      } else if (row.type === 2) {
        return `On ${entityName}`
      } else {
        let { intervalTypes } = this

        let intervalType = intervalTypes.find(({ id }) => row.type === id) || {}

        let { Days = 0, Hrs = 0, Mins = 0 } = this.$helpers.getDuration(
          row.interval,
          'seconds',
          1
        )
        let interval = Days === 0 ? (Mins === 0 ? Hrs : Mins) : Days
        let unit =
          Days === 0 ? (Mins === 0 ? 'hour(s)' : 'minute(s)') : 'day(s)'

        return `${intervalType.name} ${interval} ${unit}`
      }
    },
    getEscalationActions(row) {
      let actions = row.actions || []
      let configuredActions = []

      actions.forEach(action => {
        if (parseInt(action.actionType) === 3) {
          configuredActions.push('Email')
        } else if (parseInt(action.actionType) === 4) {
          configuredActions.push('SMS')
        } else if (parseInt(action.actionType) === 7) {
          configuredActions.push('Notify')
        } else if (parseInt(action.actionType) === 13) {
          configuredActions.push('Field')
        } else if (parseInt(action.actionType) === 21) {
          configuredActions.push('Script')
        } else if (parseInt(action.actionType) === 19) {
          configuredActions.push('Change Status')
        }
      })

      return configuredActions
    },
  },
}
</script>
<style lang="scss" scoped>
.action-badge {
  border-radius: 12px;
  border: solid 1px #39b2c2;
  color: #39b2c2;
  font-size: 11px;
  padding: 0 10px;
}
.entity-type-header {
  font-size: 14px;
  font-weight: 500;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #324056;
}
.new-escalation-section-header {
  font-size: 14px !important;
  padding: 20px 50px 15px !important;
  text-transform: capitalize !important;
  color: #3ab2c1 !important;
}
</style>
<style lang="scss">
.fill-greeny-blue {
  svg {
    path {
      fill: #3ab2c1 !important;
    }
  }
}
</style>
