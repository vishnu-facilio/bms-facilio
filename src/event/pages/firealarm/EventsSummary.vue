<template>
  <div class="layout container pB100">
    <div v-if="!event"></div>
    <div v-else>
      <div class="pB60">
        <div class="summaryHeaderContent row">
          <div class="col-8 self-center">
            <div class="color-2 f12">
              #<span class="f11">{{ $t('common._common.id') }} </span
              >{{ event.id }}
            </div>
            <div class="w4 fc-summary-subject-main" style="margin-top: 0px">
              {{ event.eventMessage ? event.eventMessage : '---' }}
            </div>
            <div>
              <div class="q-item-sublabel fas-sublabel">
                <span v-if="event.severity">
                  <span
                    class="uppercase secondary-color summaryseverityTag"
                    v-bind:style="{
                      'background-color': getAlarmSeverityByName(event.severity)
                        .color,
                    }"
                    aria-hidden="true"
                    >{{
                      event.severity
                        ? getAlarmSeverityByName(event.severity).displayName
                        : '---'
                    }}</span
                  ></span
                >
                <span v-if="event.severity" class="space-secondary-color"
                  >&nbsp;|&nbsp;</span
                >
                <span>{{ event.createdTime | fromNow }}</span>
              </div>
            </div>
          </div>

          <div class="col-4">
            <div class="row">
              <div style="position: relative; left: 100%">
                <q-icon
                  slot="right"
                  class="pull-right"
                  name="close"
                  @click="closeSummary"
                  :title="$t('maintenance._workorder.close_summary')"
                  v-tippy
                  style="font-size: 20px; cursor: pointer; float: right"
                />
              </div>
            </div>
          </div>
        </div>
        <div class="fc-border-top"></div>
        <div class="row fc-summary-row">
          <div class="col-8 scroll-content">
            <div class="row" style="padding-left: 30px; padding-top: 30px">
              <div class="col-5">
                <div class="fc-summary-content-div p15">
                  <div class="header">{{ $t('alarm.alarm.source') }}</div>
                  <div class="mtb5 content">
                    {{ event.source ? event.source : '---' }}
                  </div>
                </div>
                <div class="fc-summary-content-div p15">
                  <div class="header">
                    {{ $t('maintenance._workorder.status') }}
                  </div>
                  <div class="mtb5 content">
                    {{ event.state ? event.state : '---' }}
                  </div>
                </div>
                <div class="fc-summary-content-div p15">
                  <div class="header">{{ $t('alarm.alarm.alarm_class') }}</div>
                  <div class="mtb5 content">
                    {{ event.alarmClass ? event.alarmClass : '---' }}
                  </div>
                </div>
              </div>

              <div class="col-5">
                <div class="fc-summary-content-div p15">
                  <div class="header">{{ $t('alarm.alarm.condition') }}</div>
                  <div class="mtb5 content">
                    {{ event.condition ? event.condition : '---' }}
                  </div>
                </div>
                <div class="fc-summary-content-div p15">
                  <div class="header">
                    {{ $t('maintenance.wr_list.created_on') }}
                  </div>
                  <div class="mtb5 content" v-if="event.createdTime">
                    {{ event.createdTime | formatDate() }}
                  </div>
                  <div class="mtb5" v-else>---</div>
                </div>
                <div class="fc-summary-content-div p15">
                  <div class="header">
                    {{ $t('maintenance.wr_list.priority') }}
                  </div>
                  <div class="mtb5 content">
                    {{ event.priority ? event.priority : '--' }}
                  </div>
                </div>
              </div>
            </div>

            <div
              class="row"
              style="padding-left: 30px; padding-top: 30px"
              v-if="
                event.additionInfo &&
                  Object.keys(event.additionInfo).length !== 0
              "
            >
              <div class="col-6">
                <div class="row fc-summary-additional-div">
                  {{ $t('alarm.alarm.additional_info') }}
                </div>
                <div
                  class="row"
                  v-if="key !== 'PUBLISH_TYPE' && key !== 'eventStateEnum'"
                  v-for="(value, key) in event.additionInfo"
                  :key="key"
                >
                  <div class="col-6 key p10">{{ key }}</div>
                  <div class="col-6 value p10 justify-center">{{ value }}</div>
                </div>
              </div>
            </div>

            <div class="row p20"></div>
          </div>

          <div class="col-4 summary-border-left">
            <div
              class="row summary-content p20"
              style="border-bottom: solid 1px #f0f0f0"
              v-if="event.alarmId !== -1"
            >
              <div class="col-12 header">
                {{ $t('alarm.alarm.alarm_details') }}
              </div>
              <div
                class="col-12 header"
                style="padding-bottom: 4px; text-transform: uppercase"
              >
                {{ $t('maintenance._workorder.status') }}
              </div>
              <div class="col-12 mtb5" v-if="event.alarmsId !== -1">
                Alarm Created
              </div>
              <div class="col-12 mtb5" v-else>No Alarm Created</div>
              <div class="col-12 content" v-if="event.alarmsId !== -1"></div>
              <div class="col-12 summary-alarm-id">
                <span
                  v-if="event.alarmsId !== -1"
                  class="pointer"
                  @click="openalarm(event.alarmId)"
                >
                  <span class="f11">#{{ $t('common._common.id') }}</span
                  >{{ event.alarmId ? event.alarmId : '---' }}
                </span>
              </div>
            </div>
            <div class="summary-right-scroll">
              <div class="row p5"></div>
              <div class="row p20 summary-content2">
                <div class="col-12 header">
                  {{ $t('maintenance._workorder.asset') }}
                </div>
                <div
                  v-if="event.resource && event.resource.resourceType === 2"
                  class="col-12 content pointer"
                  @click="openAsset(event.resourceId)"
                >
                  {{ event.resource.name }}
                </div>
                <div v-else class="col-12 content">---</div>
              </div>
              <div class="row p20 summary-content2">
                <div class="col-12 header">
                  {{ $t('alarm.alarm.asset_category') }}
                </div>
                <div
                  v-if="event.resource && event.resource.resourceType === 2"
                  class="col-12 content"
                >
                  {{
                    event.resource.category
                      ? getAssetCategory(event.resource.category.id).displayName
                      : '---'
                  }}
                </div>
                <div v-else class="col-12 content">---</div>
              </div>
              <div class="row p20 summary-content2">
                <div class="col-12 header">
                  {{ $t('maintenance.wr_list.site') }}
                </div>
                <div class="col-12 content">---</div>
              </div>
              <div class="row p20 summary-content2">
                <div class="col-12 header">
                  {{ $t('alarm.alarm.building') }}
                </div>
                <div class="col-12 content">---</div>
              </div>
              <div class="row p20 summary-content2">
                <div class="col-12 header">{{ $t('alarm.alarm.floor') }}</div>
                <div class="col-12 content">---</div>
              </div>
              <div class="row p20 summary-content2">
                <div class="col-12 header">
                  {{ $t('maintenance._workorder.space') }}
                </div>
                <div class="col-12 content">---</div>
              </div>
              <div class="row p20"></div>
              <div class="row p20"></div>
              <div class="row p20"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { QIcon } from 'quasar'
import { mapGetters } from 'vuex'
export default {
  data() {
    return {
      summoryview: true,
      subSection: 'comment',
    }
  },
  computed: {
    ...mapGetters(['getAssetCategory', 'getAlarmSeverityByName']),
    event() {
      return this.$store.getters['event/getEventById'](this.eventId)
    },
    eventId() {
      return parseInt(this.$route.params.id)
    },
  },
  mounted() {
    if (this.event) {
      let title = '[#' + this.event.id + '] ' + this.event.eventMessage
      this.setTitle(title)
    } else {
      this.fetchEvent()
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
  },
  methods: {
    closeSummary() {
      this.summoryview = false
      let newpath = this.$route.path.substring(
        0,
        this.$route.path.indexOf('/summary/')
      )
      this.$router.replace({ path: newpath })
    },
    selectSubSection(section) {
      this.subSection = section
    },
    openalarm(id) {
      if (id) {
        this.$router.push({ path: '/app/fa/faults/summary/' + id })
      } else {
        return false
      }
    },
    fetchEvent() {},
    openAsset(id) {
      if (id) {
        let url = '/app/at/assets/all/' + id + '/overview'
        this.$router.replace({ path: url })
      }
    },
  },
  watch: {
    event: function() {
      let title = '[#' + this.event.id + '] ' + this.event.eventMessage
      this.setTitle(title)
    },
    eventId() {
      this.fetchEvent()
    },
  },
  components: {
    QIcon,
  },
}
</script>
<style>
.fas-container {
  padding: 1rem;
}
.fas-row {
  padding: 10px;
  background: white;
}
.fas-sublabel {
  font-size: 12px;
}
div.fas-sublabel > span {
  font-weight: 400;
  padding: 7px;
  padding-left: 0px;
  font-size: 13px;
}
.fas-timer {
  font-size: 13px;
  padding-left: 7px;
}
.fas-timer div.fc-timer div.t-label {
  font-size: 17px;
}
.fas-label {
  margin: 5px;
  margin-left: 0px;
  font-size: 18px;
  font-weight: 500;
}
.fas-logo {
  text-align: center;
  background: #37ca5d;
  width: 10px !important;
  height: 45px;
  max-width: 45px !important;
  min-width: 10px !important;
  padding: 10px;
  border-radius: 24px;
  margin-right: 15px;
  margin-left: 5px;
  color: white;
}
.fas-row-2 {
  border: 1px solid #eee;
  margin: -1px;
  padding-left: 15px;
}
.fas-row-2 div.q-item-label {
  font-size: 13px;
  font-weight: 500;
}
.fas-row-2 div.q-item-sublabel {
  font-size: 12px;
}
.fia-uppercase {
  font-size: 11px !important;
  text-align: center;
  padding: 15px;
  color: #3ae6b7;
}
.fas-col-name {
  padding-top: 10px;
  margin-left: -5px;
}
.fas-col-sublabel {
  font-size: 12px;
  margin: 0px;
}
.fas-col-sublabel-2 {
  font-size: 11px;
  margin: 0px;
}
.fas-col-avatar {
  margin-top: 6px;
}
.fas-l {
  padding-bottom: 5px;
}
.wosactive {
  border-bottom: 2px solid #fd4b92;
  color: #25243e;
  padding-bottom: 10px;
}
.fia-td-logo1 {
  background-color: #f5e02d !important;
}
.fia-td-logo2 {
  background-color: #ee8e39 !important;
}
.fia-td-logo3 {
  background-color: #ff0000 !important;
}
.fia-td-logo4 {
  background-color: #f5e02d !important;
}
.fia-td-logo5 {
  background-color: #f39c12 !important;
}
#t01 > tr > td,
#t01 > tr > th {
  text-align: left;
  padding: 10px;
}
table#t01 tr:nth-child(even) {
  background-color: #eee;
}
table#t01 tr:nth-child(odd) {
  background-color: #fff;
}
table#t01 th {
  font-weight: 400;
}
.summary-right-scroll {
  overflow-y: auto;
  height: 100%;
}
</style>
