<template>
  <div class="asset-details-widget pT0" ref="bmsAlarmDetailCard">
    <div class="container flex" v-if="occurrence">
      <div class="field">
        <el-row class="pB20 pT15 border-bottom3">
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('alarm.alarm.message') }}
              </div>
            </el-col>
            <el-col :span="16">
              <div class="fc-black-13 text-left pointer pR10">
                {{ alarm.description ? alarm.description : '---' }}
              </div>
            </el-col>
          </el-col>
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('alarm.alarm.source') }}
              </div>
            </el-col>
            <el-col :span="16">
              <div class="fc-black-13 text-left pointer pR10">
                {{ alarm.source ? alarm.source : '---' }}
              </div>
            </el-col>
          </el-col>
        </el-row>
        <el-row class="border-bottom3 pB20 pT15">
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('alarm.alarm.last_Occurred_Time') }}
              </div>
            </el-col>
            <el-col :span="16">
              <div class="fc-black-13 text-left pointer">
                {{ alarm.lastCreatedTime | formatDate() }}
              </div>
            </el-col>
          </el-col>
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('alarm.alarm.last_reported_on') }}
              </div>
            </el-col>
            <el-col :span="16">
              <div class="fc-black-13 text-left">
                {{ alarm.lastOccurredTime | formatDate() }}
              </div>
            </el-col>
          </el-col>
        </el-row>
        <el-row class="pB20 pT15 ">
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('alarm.alarm.condition') }}
              </div>
            </el-col>
            <el-col :span="16">
              <div class="fc-black-13 text-left pointer pR10">
                {{ alarm.condition ? alarm.condition : '---' }}
              </div>
            </el-col>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: [
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'primaryFields',
  ],
  data() {
    return {
      loading: false,
      lookupValue: null,
      spaceDetails: null,
      isAllVisible: false,
      readingField: null,
    }
  },
  computed: {
    resourceDetails() {
      return this.$getProperty(this.details, 'alarm.resource', null)
    },
    alarm() {
      return this.$getProperty(this, 'details.alarm')
    },
    occurrence() {
      return this.$getProperty(this, 'details.occurrence')
    },
  },

  mounted() {
    this.autoResize()
  },
  methods: {
    autoResize() {
      let height = this.$refs['bmsAlarmDetailCard'].scrollHeight
      let width = this.$refs['bmsAlarmDetailCard'].scrollWidth
      if (height === 0) height += 192
      else if (height === 160) height += 32
      if (this.resizeWidget) {
        this.resizeWidget({ height, width })
      }
    },
  },
}
</script>
<style scoped lang="scss">
.asset-details-widget {
  padding: 10px 30px 0px;
  text-align: left;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.field {
  flex: 0 100%;
  padding: 20px 0;
  border-bottom: 1px solid #edf4fa;
  transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
  -webkit-transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
  -moz-transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
}
@keyframes linkanimation {
  from {
    -webkit-transform: translateY(-2px);
    transform: translateY(-2px);
  }
  to {
    -webkit-transform: translateY(2px);
    transform: translateY(2px);
  }
}
.field:last-child:not(:nth-child(even)) {
  border-bottom: none;
}
</style>
