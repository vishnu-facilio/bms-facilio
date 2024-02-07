<template>
  <div class="clear pT20" v-if="history && !$validation.isEmpty(history)">
    <div class="fc-black-text14 fw-bold ">
      {{ $t('common.tabs.history') }}
    </div>
    <table class="clear border-tlbr-none">
      <tbody>
        <tr v-for="activity in historiesWithMessage" :key="activity.id">
          <td class="border-bottom1px pT10 pB10 ">
            <div class="fc-grey-text12 pB5 pT5 fw4">
              {{ activity.ttime | formatDate }}
            </div>
            <div v-if="activity.type === 3 && activity.info.assigned">
              {{ getUserName(activity.doneBy.ouid, activity) }}
              <template v-if="activity.info.assigned.assignedTo">
                <template v-if="activity.info.assigned.assignedTo > 0">
                  <span v-html="activity.displayMessage"></span>
                  <i>{{
                    getUserName(activity.info.assigned.assignedTo, activity)
                  }}</i>
                </template>
                <template v-else>
                  <b>{{ $t('common._common.removed_the_staff') }}</b>
                </template>
              </template>
              <template v-else>
                <template v-if="activity.info.assigned.assignmentGroup > 0">
                  <span v-html="activity.displayMessage"></span>
                  {{ $t('common.wo_report.team') }}
                  <i>{{
                    getGroup(activity.info.assigned.assignmentGroup, activity)
                      .name
                  }}</i>
                </template>
                <template v-else>
                  <b>{{ $t('common._common.removed_the_team') }}</b>
                </template>
              </template>
            </div>
            <div v-else-if="activity.type === 24">
              {{ getUserName(activity.doneBy.ouid, activity) }}
              {{ activity.ttime | formatDate }}
              <span v-html="activity.displayMessage"></span>
              <i>
                <span @click="openPm(activity)" class="fc-id pointer">
                  #{{ getPmId(activity) }}
                </span>
              </i>
            </div>
            <div v-else-if="activity.type !== 3 && activity.type !== 24">
              {{ getUserName(activity.doneBy.ouid, activity) }}
              <span v-html="activity.displayMessage"></span>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import ActivitiesView from 'src/components/relatedlist/ActivitiesView.vue'

export default {
  extends: ActivitiesView,
  props: ['history'],
  methods: {
    loadActivities() {
      return history
    },
  },
  computed: {
    historiesWithMessage() {
      return this.history
        .map(item => {
          item.displayMessage = this.getActivityMessage(item).message
          return item
        })
        .filter(item => item.displayMessage !== null)
    },
  },
}
</script>
