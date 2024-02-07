<template>
  <div class="fc-hovercard">
    <div class="hovercard-container">
      <div class="fh-postions">
        <div class="row">
          <q-item-main>
            <span>
              <img
                class="fc-avatar fc-avatar-xxlg"
                :src="space.avatarUrl"
                v-if="space.avatarUrl"
              />
            </span>
          </q-item-main>
        </div>
        <div class="row">
          <div class="col-12">
            <q-item-main>
              <div class="q-item-label text-center">{{ space.name }}</div>
              <div class="q-item-sublabel">
                <span class="fh-span"
                  ><i aria-hidden="true" class="fa fa-map-marker"></i
                  >&nbsp;&nbsp;PO Box 1247, Dubai, United Arab Emirates</span
                >
              </div>
            </q-item-main>
          </div>
        </div>
        <div class="row">
          <div class="col-6 text-center">
            <q-item-main>
              <div class="fch-avatar fch-avatar-lg">122</div>
              <div class="q-item-sublabel fh-up-label uppercase">due today</div>
            </q-item-main>
          </div>
          <div class="col-6 text-center">
            <q-item-main>
              <div class="fch-avatar fch-avatar-lg">122</div>
              <div class="q-item-sublabel fh-up-label uppercase">open</div>
            </q-item-main>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { QItemMain } from 'quasar'
export default {
  props: ['space', 'load'],
  components: {
    QItemMain,
  },
  data() {
    return {
      workorders: {
        due_today: 0,
        open: 0,
      },
    }
  },
  watch: {
    load: function() {
      if (this.load) {
        this.loadSpaceInfo()
      } else {
        this.clearSpaceInfo()
      }
    },
    'space.id': function() {},
  },
  methods: {
    clearSpaceInfo() {
      this.workorders = {}
    },
    loadSpaceInfo() {
      this.workorders = {
        due_today: 10,
        open: 13,
      }
    },
  },
}
</script>
