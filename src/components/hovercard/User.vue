<template>
  <div class="fc-hovercard">
    <div class="hovercard-container">
      <div class="fh-postions">
        <div class="row">
          <q-item-main>
            <span>
              <avatar size="xxlg" :user="user"></avatar>
            </span>
          </q-item-main>
        </div>
        <div class="row">
          <div class="col-12">
            <q-item-main>
              <div class="q-item-label text-center fc-hovercard-name">
                {{ user.name }}
              </div>
              <div class="q-item-sublabel p5">
                <span class="fh-span ellipsis"
                  ><i aria-hidden="true" class="q-icon material-icons">phone</i
                  >{{ user.phone ? user.phone : '---' }}</span
                >
                <span class="fh-span ellipsis"
                  ><i aria-hidden="true" class="q-icon material-icons">mail</i
                  ><span class="ellipsis">{{
                    user.email ? user.email : '---'
                  }}</span></span
                >
              </div>
            </q-item-main>
          </div>
        </div>
        <div class="row">
          <div class="col-6 text-center fc-border-right">
            <q-item-main class="pointer">
              <div class="fch-avatar fch-avatar-lg leed-color-1">
                <!--  {{duetoday()}} -->
                {{ workorders.due_today }}
              </div>
              <div class="q-item-sublabel fh-up-label uppercase p5">
                due today
              </div>
            </q-item-main>
          </div>
          <div class="col-6 text-center">
            <q-item-main>
              <div class="fch-avatar fch-avatar-lg color-2">
                {{ workorders.open }}
              </div>
              <div class="q-item-sublabel fh-up-label uppercase p5">open</div>
            </q-item-main>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Avatar from '@/Avatar'
import { QItemMain } from 'quasar'
export default {
  props: ['user', 'load'],
  components: {
    Avatar,
    QItemMain,
  },
  data() {
    return {
      workorders: {
        due_today: 4,
        open: 13,
      },
    }
  },
  watch: {
    load: function() {
      if (this.load) {
        this.loadUserInfo()
      } else {
        this.clearUserInfo()
      }
    },
  },
  methods: {
    clearUserInfo() {
      this.workorders = {
        due_today: 0,
        open: 0,
      }
    },

    loadUserInfo() {},
  },
}
</script>
