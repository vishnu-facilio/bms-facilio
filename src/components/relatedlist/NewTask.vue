<template>
  <div>
    <q-modal
      ref="createNewEventModel"
      position="right"
      content-classes="fc-create-record"
      @close="close"
    >
      <q-btn
        class="fc-model-close"
        flat
        @click="$refs.createNewEventModel.close()"
      >
        <q-icon name="close" />
      </q-btn>
      <div class="fc-formlayout">
        <div>
          <q-toolbar inverted>
            <q-toolbar-title>New Event</q-toolbar-title>
          </q-toolbar>
          <form v-on:submit.prevent="submit" style="padding:0 25px;">
            <div class="row md-gutter">
              <div class="col-lg-12 col-md-12">
                <div>
                  <label><h6>Payload</h6></label>
                  <textarea
                    required
                    class="full-width payload"
                    v-model="eventPayload"
                  ></textarea>
                </div>
              </div>
              <div
                class="col-xs-12 col-sm-12 col-md-12 col-lg-12"
                align="right"
              >
                <q-btn
                  flat
                  type="button"
                  cancel
                  @click="$refs.createNewEventModel.close()"
                  >Cancel</q-btn
                >
                <q-btn loader v-model="submitBtn" color="primary">
                  Save
                  <span slot="loading">Saving...</span>
                </q-btn>
              </div>
            </div>
          </form>
        </div>
      </div>
    </q-modal>
  </div>
</template>
<script>
import { QModal, QBtn, QIcon, QToolbar, QToolbarTitle } from 'quasar'

export default {
  data() {
    return {
      eventPayload:
        '{\n    "source" : "Tridium",\n    "node" : "ln01",\n    "message" : "Smoke sss Detected",\n    "priority" : 255,\n    "alarmClass" : "BMTS_LNW",\n    "state" : "Alarm",\n    "timestamp" : 1511354432000,\n    "test1" : "value1",\n    "test2" : "value2"\n}',
      submitBtn: false,
      severity: [
        {
          label: 'High',
          value: 'High',
        },
        {
          label: 'Medium',
          value: 'Medium',
        },
        {
          label: 'Low',
          value: 'Low',
        },
        {
          label: 'Clear',
          value: 'Clear',
        },
        {
          label: 'Idle',
          value: 'Idle',
        },
      ],
    }
  },
  components: {
    QModal,
    QBtn,
    QIcon,
    QToolbar,
    QToolbarTitle,
  },
  mounted() {
    this.$refs.createNewEventModel.open()
  },
  methods: {
    close() {
      let newpath = this.$route.path.replace('/new', '')
      this.$router.replace({ path: newpath })
    },
    cancel() {
      this.$emit('canceled')
      this.submitBtn = false
    },
    submit: function() {
      this.submitBtn = true
      this.$http
        .post('/event/addevent', {
          payload: JSON.parse(this.eventPayload),
        })
        .catch(() => {})
      this.close()
    },
  },
}
</script>
<style>
.fc-create-record {
  width: 40% !important;
  height: 100% !important;
  max-height: 100% !important;
}
.payload {
  height: 300px;
}
</style>
