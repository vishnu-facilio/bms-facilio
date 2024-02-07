<template>
  <div
    class="white-bg-block mb fc__wo__sum__timer pB20 mT10 pT20 remaining_time_cont"
  >
    <div class="fc-id11 text-uppercase pL15">
      {{ $t('maintenance._workorder.sla_remaining_time') }}
    </div>
    <div>
      <Timer
        class="alarm-timer p10 static"
        :time="dateValue"
        :staticTime="true"
        twoDigits="true"
      ></Timer>
    </div>
  </div>
</template>
<script>
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import moment from 'moment-timezone'
import Timer from '@/Timer'
import { isEmpty } from '@facilio/utils/validation'
export default {
  name: 'WorkDuration',
  mixins: [workorderMixin],
  props: ['widget', 'details', 'layoutParams', 'moduleName'],
  components: {
    Timer,
  },
  mounted() {
    this.getTime()
  },
  data() {
    return {
      dateValue: null,
      timer: null,
    }
  },
  computed: {
    remainingTime() {
      let dateField = this.$getProperty(
        this,
        'widget.widgetParams.dateField',
        'dueDate'
      )
      let remainingTime = this.details.workorder[dateField]
      if (isEmpty(remainingTime)) {
        remainingTime = this.details?.workorder?.data[dateField]
      }

      return remainingTime
    },
  },
  watch: {
    remainingTime() {
      this.getTime()
    },
  },

  methods: {
    getTime() {
      let { remainingTime } = this
      if (!isEmpty(remainingTime) && remainingTime > 0) {
        this.dateValue = remainingTime - moment().valueOf()
        if (this.dateValue < 0) {
          this.dateValue = 0
        } else {
          if (!this.timer) {
            this.timer = setInterval(() => {
              this.dateValue = remainingTime - moment().valueOf()
              if (this.dateValue < 0) {
                this.dateValue = 0
                clearInterval(this.timer)
                this.timer = null
              }
            }, 1000)
          }
        }
      }
    },
  },
}
</script>
<style scoped lang="scss">
.alarm-timer {
  padding: 5px 10px 10px 15px !important;
}
</style>
<style lang="scss">
.remaining_time_cont {
  .t-separate {
    margin-top: 5px !important;
  }
  .t-label {
    font-size: 20px;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    line-height: 30px;
    letter-spacing: 0.2px;
    color: #ff3184 !important;
  }
  .t-sublabel {
    text-align: center;
    font-size: 10px !important;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 0.4px;
    color: #324056;
    text-transform: uppercase !important;
  }
}
</style>
