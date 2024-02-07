<template>
  <div>
    <q-modal
      ref="createNewModel"
      position="right"
      content-classes="fc-create-record"
      @close="close"
    >
      <q-btn class="fc-model-close" flat @click="closeDialog">
        <q-icon name="close" />
      </q-btn>
      <form-layout
        @onsave="saveWorkOrder"
        @cancel="closeDialog"
        :layout="formlayout"
      >
        <el-col :md="24" :lg="24">
          <div class="bold pT15 pB15">Scheduling</div>
          <el-form-item label="Start from"
            >{{ preventiveForm }}
            <el-date-picker
              v-model="preventiveForm.startTime"
              :type="'datetime'"
            ></el-date-picker>
            } }
          </el-form-item>

          <f-schedule
            :from="preventiveForm.startTime"
            v-model="preventiveForm.schedule"
          ></f-schedule>

          <el-form-item
            label="Stop repeating after"
            v-if="
              preventiveForm.schedule &&
                preventiveForm.schedule.frequencyType > 0
            "
          >
            <el-select v-model="preventiveForm.stopAfter">
              <el-option label="Never" value="never"></el-option>
              <el-option label="Time" value="time"></el-option>
              <el-option label="Count" value="count"></el-option>
            </el-select>
            <el-date-picker
              v-if="preventiveForm.stopAfter === 'time'"
              v-model="preventiveForm.stopAfterTime"
              :type="'datetime'"
            ></el-date-picker>
            <el-input-number
              v-if="preventiveForm.stopAfter === 'count'"
              v-model="preventiveForm.stopAfterCount"
            ></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :md="24" :lg="24">
          <div class="bold pT15 pB15">Tasks</div>
          <el-form-item v-for="(task, index) in tasks" :key="index">
            <el-input
              v-model="task.subject"
              style="width: 80% !important;"
            ></el-input>
            <el-button
              @click="removeTask(index)"
              v-if="index !== 0"
              type="text"
              class="p15"
              icon="el-icon-minus"
            ></el-button>
            <el-button
              @click="addTask"
              type="text"
              class="p15"
              icon="el-icon-plus"
            ></el-button>
          </el-form-item>
        </el-col>
      </form-layout>
    </q-modal>
  </div>
</template>
<script>
import FormLayout from '@/Formlayout'
import FSchedule from '@/FSchedule'
import { QModal, QBtn, QIcon } from 'quasar'

export default {
  props: ['preventive'],
  data() {
    return {
      formlayout: {
        title: 'Edit Preventive Maintenance',
        metaurl: '/workorder/layout',
      },
      isPreventive: false,
      savePreventiveVisible: false,
      preventiveForm: {
        title: this.preventive,
        startTime: this.preventive.startTime
          ? new Date(this.preventive.startTime)
          : new Date(),
        schedule: this.preventive.schedule,
        stopAfter: this.preventive.stopAfter,
        stopAfterTime: this.preventive.stopAfterTime,
        stopAfterCount: 1,
      },
      preventiveSaving: false,
      workorder: null,
      tasks: [
        {
          subject: null,
        },
      ],
    }
  },
  mounted() {
    this.$refs.createNewModel.open()
  },
  components: {
    QModal,
    QBtn,
    QIcon,
    FormLayout,
    FSchedule,
  },
  methods: {
    removeTask(index) {
      this.tasks.splice(index, 1)
    },
    addTask() {
      this.tasks.push({
        subject: null,
      })
    },
    close() {
      let newpath = this.$route.path.replace('/new', '')
      this.$router.replace({ path: newpath })
    },
    closeDialog() {
      this.$refs.createNewModel.close()
    },
    saveWorkOrder(formdata) {
      let preventiveFormData = {}
      preventiveFormData.title = formdata.workorder.subject
      preventiveFormData.startTime = new Date(
        this.preventiveForm.startTime
      ).getTime()
      preventiveFormData.schedule = this.preventiveForm.schedule
      if (this.preventiveForm.stopAfter === 'time') {
        preventiveFormData.endExecutionTime = new Date(
          this.preventiveForm.stopAfterTime
        ).getTime()
      } else if (this.preventiveForm.stopAfter === 'count') {
        preventiveFormData.maxExecution = this.preventiveForm.stopAfterCount
      }

      let data = {}
      data.workorder = formdata.workorder
      data.workorder.requester = formdata.requester
      data.preventivemaintenance = preventiveFormData
      data.tasks = this.tasks

      let saveurl = '/workorder/editPreventiveMaintenance'

      console.log(this.model)
      let self = this
      self.$http
        .post(saveurl, data)
        .then(function(response) {
          console.log(response)
          if (typeof response.data === 'object') {
            const alert1 = Alert.create({
              html: 'Preventive maintenance created successfully!',
              color: 'positive',
              position: 'top-center',
            })
            setTimeout(function() {
              alert1.dismiss()
            }, 1500)

            self.close()
            self.resetPreventive()
          } else {
            const alert3 = Alert.create({
              html:
                'Preventive maintenance creation failed, please check requester email address!',
              color: 'negative',
              position: 'top-center',
            })
            setTimeout(function() {
              alert3.dismiss()
            }, 1500)
            self.formlayout.submit = false
          }
        })
        .catch(function(error) {
          console.log(error)
          const alert2 = Alert.create({
            html: ' creation failed! [ ' + error + ']',
            color: 'negative',
            position: 'top-center',
          })
          setTimeout(function() {
            alert2.dismiss()
          }, 1500)
          self.formlayout.submit = false
        })
    },
    resetPreventive() {
      this.preventiveForm.title = ''
      this.preventiveForm.startTime = new Date()
      this.preventiveForm.schedule = null
      this.preventiveForm.stopAfter = 'never'
      this.preventiveForm.stopAfterTime = ''
      this.preventiveForm.stopAfterCount = 1
      this.tasks = [
        {
          subject: null,
        },
      ]
    },
  },
}
</script>
<style>
.fc-create-record {
  width: 60% !important;
  height: 100% !important;
  max-height: 100% !important;
}
</style>
