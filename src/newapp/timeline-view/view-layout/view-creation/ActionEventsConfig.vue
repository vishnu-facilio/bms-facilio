<template>
  <el-form label-position="left" class="timeline-view-action-events">
    <el-form-item
      v-for="item in groupingOptions"
      :key="item.prop"
      :prop="item.prop"
      :label="item.label"
      class="mB10 mT10 flex-middle"
      :disabled="item.disable"
    >
      <el-radio
        v-model="details[item.prop]"
        :label="item.allowVal"
        :disabled="item.disable"
        class="fc-radio-btn"
      >
        {{ radioLabel.allow }}
      </el-radio>
      <el-radio
        v-model="details[item.prop]"
        :label="item.dontAllowVal"
        :disabled="item.disable"
        class="fc-radio-btn"
      >
        {{ radioLabel.dontAllow }}
      </el-radio>
    </el-form-item>
  </el-form>
</template>
<script>
export default {
  props: ['viewDetails', 'isNew', 'saveAsNew'],
  data() {
    return {
      details: {
        allowRescheduling: false,
        allowReAssignment: false,
        allowGroupAssignment: true,
        allowPastAssignment: false,
        disablePastEvents: true,
      },
    }
  },
  created() {
    if (!this.isNew || this.saveAsNew) {
      let {
        allowRescheduling,
        allowReAssignment,
        allowGroupAssignment,
        allowPastAssignment,
        disablePastEvents,
      } = this.viewDetails || {}
      this.details = {
        allowRescheduling,
        allowReAssignment,
        allowGroupAssignment,
        allowPastAssignment,
        disablePastEvents,
      }
    }
  },
  computed: {
    isPastEventdisabled() {
      let {
        allowReAssignment,
        allowRescheduling,
        allowGroupAssignment,
      } = this.details
      return !(allowReAssignment || allowRescheduling || allowGroupAssignment)
    },
    groupingOptions() {
      return [
        {
          label: 'Assign events to the selected group',
          prop: 'allowGroupAssignment',
          allowVal: true,
          dontAllowVal: false,
          disable: false,
        },
        {
          label: 'Reschedule Event',
          prop: 'allowRescheduling',
          allowVal: true,
          dontAllowVal: false,
          disable: false,
        },
        {
          label: 'Reassign Event',
          prop: 'allowReAssignment',
          allowVal: true,
          dontAllowVal: false,
          disable: false,
        },
        {
          label: 'Move events to earlier dates',
          prop: 'allowPastAssignment',
          allowVal: true,
          dontAllowVal: false,
          disable: this.isPastEventdisabled,
        },
        {
          label: 'Actions on Past Events',
          prop: 'disablePastEvents',
          allowVal: false,
          dontAllowVal: true,
          disable: this.isPastEventdisabled,
        },
      ]
    },
    radioLabel() {
      return {
        allow: 'Allow',
        dontAllow: "Don't Allow",
      }
    },
  },
  watch: {
    isPastEventdisabled(newVal) {
      if (newVal) {
        this.details.disablePastEvents = true
        this.details.allowPastAssignment = false
      }
    },
  },
  methods: {
    serialize() {
      return this.details
    },
  },
}
</script>
<style lang="scss">
.timeline-view-action-events {
  .el-form-item__label {
    width: 300px;
  }
  .el-form-item__content {
    margin: auto;
  }
}
</style>
