<template>
  <div>
    <!-- resource -->
    <div
      v-if="field.name === 'resource'"
      class="q-item-division relative-position"
      style="min-width: 170px;"
    >
      <div
        v-if="
          !$validation.isEmpty(moduleData.resource) &&
            !$validation.isEmpty(moduleData.resource.space) &&
            !$validation.isEmpty(moduleData.resource.space.id)
        "
      >
        <div class="flLeft mR7">
          <img
            src="~statics/space/space-resource.svg"
            style="height:12px; width:14px;"
          />
        </div>
        <span
          class="flLeft q-item-label ellipsis max-width140px"
          v-tippy
          small
          data-position="bottom"
          :title="moduleData.resource.space.name"
          >{{ moduleData.resource.space.name }}</span
        >
      </div>
      <div v-else>
        <span class="q-item-label secondary-color color-d"
          >--- {{ $t('maintenance.wr_list.space_asset') }} ---</span
        >
      </div>
    </div>

    <!-- acknowledgedBy -->

    <div
      v-else-if="field.name === 'acknowledgedBy'"
      class="q-item-division relative-position wo_assignedto_avatarblock"
    >
      <div
        v-if="moduleData.acknowledgedBy && moduleData.acknowledgedBy.id"
        class="wo-assigned-avatar fL"
      >
        <user-avatar
          size="sm"
          :user="$store.getters.getUser(moduleData.acknowledgedBy.id)"
          :showLabel="true"
          moduleName="lookupModuleName"
        ></user-avatar>
      </div>
      <div v-else>Unacknowledged</div>
    </div>

    <!-- noOfEvents -->

    <div v-else-if="field.name === 'noOfEvents'">
      <span class="flLeft">
        <img src="~statics/icons/event.svg" style="width:15px;" />
      </span>
      <span class="flLeft pL5">{{ eventsLabel }}</span>
    </div>

    <!-- modifiedTime -->

    <div v-else-if="field.name === 'modifiedTime'" style="min-width:150px">
      <timer
        class="alarm-timer p10"
        :time="moduleData.modifiedTime"
        :title="moduleData.modifiedTime | formatDate()"
        v-tippy="{
          html: '#timer_popover_' + moduleData.id,
          distance: 0,
          interactive: true,
          theme: 'light',
          animation: 'scale',
          arrow: true,
        }"
      ></timer>
      <div :id="'timer_popover_' + moduleData.id" class="hide">
        <div style="font-size: 12px;letter-spacing: 0.5px;color: #666666;">
          {{ moduleData.modifiedTime | formatDate() }}
        </div>
      </div>
    </div>

    <!-- previousSeverity -->

    <div
      v-else-if="
        field.name === 'previousSeverity' && moduleData.previousSeverity
      "
      class="q-item-label self-center f10 secondary-color"
    >
      <div
        class="q-item-label uppercase"
        v-bind:class="moduleData.previousSeverity ? { severityTag: true } : ''"
        v-bind:style="{
          'background-color': getAlarmSeverity(moduleData.previousSeverity.id)
            .color,
        }"
      >
        {{
          moduleData.previousSeverity
            ? getAlarmSeverity(moduleData.previousSeverity.id).displayName
            : '---'
        }}
      </div>
    </div>

    <!-- severity -->

    <div v-else-if="field.name === 'severity'">
      <div
        class="q-item-label self-center f10 secondary-color"
        v-if="moduleData.severity"
        style="min-width: 90px;margin-left: 13%;"
      >
        <div
          class="q-item-label uppercase severityTag"
          v-bind:style="{
            'background-color': getAlarmSeverity(moduleData.severity.id).color,
          }"
        >
          {{
            moduleData.severity.id
              ? getAlarmSeverity(moduleData.severity.id).displayName
              : '---'
          }}
        </div>
      </div>
    </div>

    <!-- alarmType -->

    <div v-else-if="field.name === 'alarmType'">
      <span
        class="q-item-label"
        style="font-size:13px;letter-spacing: 0.4px;"
        >{{ moduleData.alarmTypeVal ? moduleData.alarmTypeVal : '---' }}</span
      >
    </div>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import { isEmpty } from '@facilio/utils/validation'
import Timer from '@/Timer'
import { mapGetters } from 'vuex'

export default {
  components: { UserAvatar, Timer },
  props: ['field', 'moduleData'],
  computed: {
    ...mapGetters(['getAlarmSeverity']),
    eventsLabel() {
      let { moduleData } = this
      let { noOfEvents } = moduleData
      return isEmpty(noOfEvents) || noOfEvents === 0
        ? '0'
        : moduleData.noOfEvents
    },
  },
  methods: {
    isActiveAlarm(alarm) {
      if (this.getAlarmSeverity(alarm.severity.id).severity !== 'Clear') {
        return true
      }
      return false
    },
  },
}
</script>
<style>
.severityTag {
  border-radius: 38px;
  font-size: 10px !important;
  font-weight: bold !important;
  letter-spacing: 0.8px;
  text-align: center;
  color: #ffffff !important;
  padding: 4px 10px;
  width: 72px;
  margin-bottom: 6px;
}
</style>
