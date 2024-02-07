<template>
  <div class="pm-tigger-dialog-form">
    <div
      class="pm-tigger-dialog-form-left"
      :class="{
        'triger-right-side-width': !(
          trigger.type === 1 && trigger.schedule.frequencyType !== 0
        ),
      }"
    >
      <div class="label-txt-black text-uppercase fwBold trigger-header-left">
        Trigger
      </div>
      <div class="pL30 pR30">
        <el-row class="mT30">
          <div class="fc-input-label-txt pB0">Name</div>
          <el-col :span="24">
            <div class="pT10">
              <el-input
                v-model="trigger.name"
                class="fc-input-full-border2 width100"
              ></el-input>
            </div>
          </el-col>
        </el-row>
        <el-row class="mT20">
          <div class="fc-input-label-txt pB0">Trigger Type</div>
          <el-col :span="24" class="pT10">
            <div>
              <el-select
                v-model="trigger.type"
                @change="onTriggerTypeChange"
                style="width:100%"
                class="form-item fc-input-full-border-select2 width100"
                placeholder=" "
              >
                <el-option
                  v-for="triggerType in triggerTypes"
                  :key="triggerType.value"
                  :label="triggerType.label"
                  :value="triggerType.value"
                ></el-option>
              </el-select>
            </div>
          </el-col>
        </el-row>
        <schedule-trigger
          v-if="trigger.type === 1"
          :trigger="trigger"
          :fromPage="'pmtrigger'"
        ></schedule-trigger>
        <reading-trigger
          v-if="trigger.type === 2"
          :trigger="trigger"
          :resource="resource"
        ></reading-trigger>
        <alarm-condition-trigger
          v-if="trigger.type === 3"
          :trigger="trigger"
        ></alarm-condition-trigger>
        <user-trigger
          v-if="trigger.type === 4"
          :trigger="trigger"
        ></user-trigger>
        <custom-trigger v-if="trigger.type === 5" :trigger="trigger">
        </custom-trigger>
      </div>
    </div>
    <template v-if="trigger.type === 1 && trigger.schedule.frequencyType !== 0">
      <div
        class="pm-tigger-dialog-form-right"
        :class="{
          'triger-right-side-hide': !(
            trigger.type === 1 && trigger.schedule.frequencyType !== 0
          ),
        }"
      >
        <div>
          <div class="flex-middle pm-trigger-left-header">
            <InlineSvg
              src="svgs/schedule"
              iconClass="icon icon-md new-icon"
            ></InlineSvg>
            <div class="label-txt-black fwBold mL10">Next 10 Schedules</div>
          </div>
          <div class="pR30 pL30 pT20">
            <scheduled-dates :trigger="trigger"></scheduled-dates>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import ScheduledDates from '@/ScheduledDates'
import ScheduleTrigger from '@/TriggerTypeSchedule'
import ReadingTrigger from '@/TriggerTypeReading'
import AlarmConditionTrigger from '@/TriggerTypeAlarmCondition'
import UserTrigger from '@/TriggerTypeUser'
import CustomTrigger from '@/TriggerTypeCustom'
import PMMixin from '@/mixins/PMMixin'
import { deepCloneObject } from 'util/utility-methods'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['trigger', 'isSingleWo', 'resource', 'customTriggerTypes'],
  components: {
    ScheduleTrigger,
    ScheduledDates,
    ReadingTrigger,
    AlarmConditionTrigger,
    UserTrigger,
    CustomTrigger,
  },
  mounted() {
    for (let key in this.$constants.FACILIO_FREQUENCY) {
      if (this.$constants.FACILIO_FREQUENCY.hasOwnProperty(key)) {
        this.frequencyTypes.push({
          label: this.$constants.FACILIO_FREQUENCY[key],
          value: key,
        })
      }
    }
  },
  data() {
    return {
      frequencyTypes: [],
    }
  },
  watch: {
    trigger: function() {},
  },
  computed: {
    triggerTypes() {
      let { customTriggerTypes } = this
      let types = [
        {
          label: 'Schedule',
          value: 1,
        },
        {
          label: 'Reading',
          value: 2,
        },
        {
          label: 'Alarm Condition',
          value: 3,
        },
        {
          label: 'Manual',
          value: 4,
        },
        {
          label: 'Custom',
          value: 5,
        },
      ]
      if (this.isSingleWo) {
        return types
      } else if (!isEmpty(customTriggerTypes)) {
        types = types.filter(type => customTriggerTypes.includes(type.value))
      } else {
        types = [
          {
            label: 'Schedule',
            value: 1,
          },
          {
            label: 'Manual',
            value: 4,
          },
          {
            label: 'Custom',
            value: 5,
          },
        ]
      }
      return types
    },
  },
  methods: {
    onTriggerTypeChange(val) {
      if (val != 1) {
        this.trigger.schedule = null
      } else {
        this.trigger.schedule = deepCloneObject(PMMixin.INITIAL_SCHEDULE)
        this.trigger.schedule.values = []
      }
    },
  },
}
</script>
<style lang="scss">
.pm-tigger-dialog-form {
  .pm-tigger-dialog-form-left {
    width: 70%;
    float: left;
    height: 100%;
    overflow-y: scroll;
    padding-bottom: 100px;
    border-right: 1px solid #ebedf4;
    max-height: 600px;
  }

  .pm-tigger-dialog-form-right {
    width: 30%;
    float: left;
    overflow-y: scroll;
    max-height: 600px;
    padding-bottom: 100px;
  }

  .triger-right-side-hide {
    display: none;
  }

  .triger-right-side-width {
    width: 100% !important;
    border-right: none;
    padding-right: 0;
  }
  .criteria-container {
    max-width: 760px;
  }
  .trigger-header-left {
    border-bottom: 1px solid #ebedf4;
    padding: 15px 30px;
    background: #ffffff;
    position: sticky;
    top: 0;
    z-index: 10000;
  }
  .pm-trigger-left-header {
    border-bottom: 1px solid #ebedf4;
    padding: 13px 30px;
    background: #ffffff;
    position: sticky;
    top: 0;
    z-index: 1;
  }
}
</style>
