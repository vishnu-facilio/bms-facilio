<template>
  <div class="fc-formlayout">
    <div>
      <q-toolbar inverted>
        <q-toolbar-title>New Alarm</q-toolbar-title>
      </q-toolbar>
      <form v-on:submit.prevent="submit" style="padding:0 25px;">
        <div class="row md-gutter">
          <div class="col-lg-12 col-md-12">
            <div>
              <el-input v-model="alarm.subject" float-label="Subject" />

              <el-select
                v-model="alarm.type"
                float-label="Type"
                :options="alarmtype"
              />

              <el-select
                v-model="alarm.asset.id"
                float-label="Sensor"
                :options="devices | options"
              />
            </div>
          </div>
          <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" align="right">
            <q-btn flat type="button" cancel @click="close">Cancel</q-btn>
            <q-btn loader v-model="submitBtn" color="primary">
              Save
              <span slot="loading">Saving...</span>
            </q-btn>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>
<script>
import { mapState, mapActions } from 'vuex'
import { QBtn, QToolbar, QToolbarTitle } from 'quasar'

export default {
  data() {
    return {
      alarm: {
        subject: null,
        description: 'alarms',
        type: 1,
        asset: {
          id: null,
        },
        alarmStatus: 1,
        statusVal: 'Active',
      },
      submitBtn: false,
      alarmtype: [
        {
          label: 'Maintenance',
          value: 1,
        },
        {
          label: 'Critical',
          value: 2,
        },
        {
          label: 'Life Safety',
          value: 3,
        },
        {
          label: 'Energy',
          value: 5,
        },
      ],
    }
  },
  mounted() {},
  components: {
    QBtn,
    QToolbar,
    QToolbarTitle,
  },
  methods: {
    ...mapActions({
      addAlarmApi: 'alarm/addAlarm',
    }),
    close() {
      this.alarm = {
        subject: null,
        description: 'alarms',
        type: 1,
        asset: {
          id: null,
        },
        alarmStatus: 1,
        statusVal: 'Active',
      }
      this.submitBtn = false
      this.$emit('closed')
    },
    submit: function() {
      let newAlarm = this.alarm
      this.addAlarmApi(newAlarm)
      this.close()
    },
  },
  computed: {
    ...mapState({
      devices: state => state.assets,
    }),
  },
}
</script>
<style>
.fc-create-record {
  width: 40% !important;
  height: 100% !important;
  max-height: 100% !important;
}
</style>
