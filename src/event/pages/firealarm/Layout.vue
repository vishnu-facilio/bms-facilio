<template>
  <div class="layout container">
    <subheader
      :menu="subheaderMenu"
      newbtn="false"
      type="workorder"
      :listCount="listCount"
      parent="/app/fa/events"
    >
      <div class="row" style="margin-right: 30px; margin-top: -12px;">
        <div class="col-12">
          <router-link
            :to="{ query: { create: 'new' } }"
            append
            v-if="$hasPermission('alarm:CREATE')"
          >
            <button class="sh-button sh-button-add button-add  shadow-12">
              <q-icon name="add" style="ffont-size: 20px;font-weight: 700;" />
            </button>
          </router-link>
        </div>
      </div>
    </subheader>
    <router-view @syncCount="callbackMethod" class=""></router-view>
  </div>
</template>
<script>
import Subheader from '@/Subheader'
import { QIcon } from 'quasar'

export default {
  data() {
    return {
      subheaderMenu: [
        {
          label: this.$t('common.events.today'),
          path: { path: '/app/fa/events/today' },
        },
        {
          label: this.$t('common.events.yesterday'),
          path: { path: '/app/fa/events/yesterday' },
        },
        {
          label: this.$t('common.events.this_week'),
          path: { path: '/app/fa/events/thisweek' },
        },
        {
          label: this.$t('common.events.last_week'),
          path: { path: '/app/fa/events/lastweek' },
        },
        {
          label: this.$t('common.events.all_events'),
          path: { path: '/app/fa/events/all' },
        },
      ],
    }
  },
  watch: {
    callbackMethod(newVal) {
      this.listCount = newVal
    },
  },
  components: {
    Subheader,
    QIcon,
  },
}
</script>
