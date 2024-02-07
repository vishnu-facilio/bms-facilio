<template>
  <div>
    <div id="triggers-header" class="section-header">
      {{ $t('setup.setup.triggers') }}
    </div>

    <div class="p50 pT10 pR70 pB30 scoring-rule-trigger">
      <div v-if="$validation.isEmpty(triggers)" class="score-triggers no-hover">
        <span class="score-trigger-name">{{
          $t('setup.setup.no_triggers_associated')
        }}</span>
      </div>
      <div v-for="trigger in triggers" :key="trigger.id" class="score-triggers">
        <div class="score-trigger-details">
          <div class="score-trigger-name">
            {{ trigger.name }}
          </div>
          <div class="score-trigger-type">
            {{ trigger.internal ? 'Internal' : eventTypes[trigger.eventType] }}
          </div>
        </div>
        <div
          v-if="!trigger.internal"
          class="mL20 reset-txt"
          @click="deleteTrigger(trigger.id)"
        >
          {{ $t('common._common.remove') }}
        </div>
      </div>

      <el-button
        @click="showExistingListForm = true"
        class="task-add-btn mL-auto"
      >
        <img src="~assets/add-blue.svg" />
        <span class="btn-label mL5">{{ $t('setup.setup.add_triggers') }}</span>
      </el-button>
    </div>

    <el-dialog
      :title="$t('setup.setup.add_triggers')"
      :visible="showExistingListForm"
      width="30%"
      class="fc-dialog-center-container scorring-rule-triggers"
      :append-to-body="true"
      :before-close="closeDialog"
    >
      <div class="height150">
        <div class="flex-center-row-space">
          <el-select
            class="fc-input-full-border2 width300px fc-tag"
            v-model="selectedTriggers"
            filterable
            multiple
            collapse-tags
            allow-create
            default-first-option
            :loading="loading"
            :loading-text="loadingText"
            @change="setTrigger"
            placeholder="Select Triggers"
          >
            <el-option
              v-for="trigger in triggersList"
              :key="trigger.id"
              :label="trigger.name"
              :value="trigger.id"
            ></el-option>
          </el-select>
        </div>

        <div class="modal-dialog-footer">
          <el-button @click="closeDialog" class="modal-btn-cancel">
            {{ $t('setup.users_management.cancel') }}
          </el-button>

          <el-button
            type="primary"
            class="modal-btn-save"
            @click="saveTriggers"
          >
            {{ $t('panel.dashboard.confirm') }}
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { isEmpty, isNumber } from '@facilio/utils/validation'
import { Trigger } from 'pages/setup/trigger/TriggerModel'

const eventTypes = {
  1: 'Create',
  2: 'Edit',
  4: 'Delete',
  1073741824: 'Custom',
}

export default {
  props: ['moduleName', 'details', 'isNew'],

  data() {
    return {
      showExistingListForm: false,
      triggers: [],
      loading: false,
      loadingText: null,
      triggersList: [],
      selectedTriggers: [],
      eventTypes,
    }
  },

  async created() {
    await this.loadTriggers()
    this.triggers = this.details.triggers

    let { triggers } = this.details

    if (!isEmpty(triggers)) {
      let internalTriggers = triggers.filter(t => !isEmpty(t) && t.internal)
      this.triggersList = [...this.triggersList, ...internalTriggers]
    }
  },

  watch: {
    showExistingListForm(newVal) {
      if (newVal) {
        this.selectedTriggers = this.triggers.map(t => t.id)
      }
    },
  },

  methods: {
    async loadTriggers() {
      try {
        this.loading = true
        this.triggersList = await Trigger.fetchAll(this.moduleName)
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
      this.loading = false
    },
    setTrigger(triggers) {
      this.selectedTriggers = []

      triggers.forEach(async triggerVal => {
        if (!isNumber(triggerVal)) {
          this.loadingText = 'Adding new trigger...'
          this.loading = true

          try {
            let { moduleName } = this
            let triggerData = new Trigger({ name: triggerVal, moduleName })

            await triggerData.save()
            this.triggersList.push(triggerData)
            this.$nextTick(() => {
              this.selectedTriggers.push(triggerData.id)
            })
          } catch (errorMsg) {
            this.$message.error(errorMsg)
          }

          this.loading = false
          this.loadingText = null
        } else {
          this.selectedTriggers.push(triggerVal)
        }
      })
    },
    saveTriggers() {
      let { selectedTriggers, triggersList } = this
      let selectedTriggersList = triggersList.filter(t =>
        selectedTriggers.includes(t.id)
      )

      if (isEmpty(selectedTriggersList)) {
        this.$message.error(this.$t('common.products.triggers_not_selected'))
        return
      }
      this.triggers = selectedTriggersList.map(trigger => {
        let { name, id, internal = false, eventType } = trigger
        return { name, id, internal, eventType }
      })
      this.closeDialog()
    },
    deleteTrigger(triggerId) {
      let index = this.triggers.findIndex(t => t.id === triggerId)
      !isEmpty(index) && this.triggers.splice(index, 1)
    },
    closeDialog() {
      this.showExistingListForm = false
      this.selectedTriggers = []
    },
  },
}
</script>
<style lang="scss">
.scorring-rule-triggers {
  .add-trigger {
    color: #39b2c2;
    margin-left: 5px;
    font-size: 13px;
    font-weight: 500;
    letter-spacing: 0.46px;
  }
}
.scoring-rule-trigger {
  .score-triggers {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 35px;
    border: 1px solid #edf4fa;
    border-bottom: none;

    .score-trigger-details {
      display: flex;
      flex-direction: column;
      margin: auto 0px;

      .score-trigger-name {
        font-weight: 400;
        letter-spacing: 0.5px;
        font-size: 14px;
        line-height: 24px;
        color: #415e7b;
      }
      .score-trigger-type {
        font-size: 12px;
        letter-spacing: 0.5px;
        color: #8ca0ad;
        margin-top: 5px;
      }
    }
    .reset-txt {
      font-size: 12px;
      letter-spacing: 0.5px;
      color: #6171db;
      cursor: pointer;
    }

    &:not(.no-hover):hover {
      background-color: #f5f7fa;
    }
  }
  .score-triggers:last-of-type {
    border-bottom: 1px solid #edf4fa;
  }
}
</style>
