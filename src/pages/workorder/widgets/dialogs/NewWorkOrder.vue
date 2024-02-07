<template>
  <el-dialog
    :visible.sync="visibilityState"
    :fullscreen="true"
    custom-class="fc-dialog-form fc-dialog-right"
    @close="close"
    :show-close="false"
    :close-on-click-modal="false"
    :before-close="handleClose"
  >
    <span slot="title">
      <el-row class="form-header">
        <el-col :span="12">
          <el-dropdown trigger="click" @command="switchForm">
            <span class="el-dropdown-link form-title">
              {{ activeForm.label }}
              <i class="el-icon-arrow-down down-arrow"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                v-for="(form, index) in formList"
                :key="index"
                v-if="form.value !== activeForm.value"
                :command="form.value"
                >{{ form.label }}</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
        </el-col>
        <el-col :span="12" class="text-right form-label pointer">
          {{ $t('maintenance._workorder.choose_from_temp') }}
          <i class="el-icon-arrow-down down-arrow"></i>
        </el-col>
      </el-row>
    </span>
    <el-form ref="newWOForm" @submit.prevent="save" :label-position="'top'">
      <div
        class="fc-form-content"
        style="padding-bottom: 100px; max-height: 85vh;"
      >
        <new-workorder-form
          :assets="allAssets"
          :model="newWorkOrder"
        ></new-workorder-form>
        <div
          class="pT30"
          v-if="activeForm.value === 'newPreventiveMaintenance'"
        >
          <div class="bold pT15 pB15">{{ $t('common.header.scheduling') }}</div>

          <q-datetime
            v-model="preventiveForm.startTime"
            class="form-item"
            float-label="Start Time"
            type="datetime"
          />

          <f-schedule
            :from="preventiveForm.startTime"
            v-model="preventiveForm.schedule"
          ></f-schedule>

          <el-row :gutter="80" align="middle">
            <el-col :span="12">
              <q-select
                v-model="preventiveForm.stopAfter"
                class="form-item"
                float-label="Stop repeating after"
                :options="stopAfterMenu | options('label', 'value')"
                :clearable="true"
              />
            </el-col>
            <el-col :span="12">
              <q-datetime
                v-if="preventiveForm.stopAfter === 'time'"
                v-model="preventiveForm.stopAfterTime"
                class="form-item"
                float-label="Stop at"
                type="datetime"
              />
              <q-input
                v-if="preventiveForm.stopAfter === 'count'"
                type="number"
                v-model="preventiveForm.stopAfterCount"
                class="form-item"
                float-label="Stop at"
              />
            </el-col>
          </el-row>

          <div class="bold pT30 pB10">{{ $t('common._common.reminders') }}</div>
          <el-row
            :gutter="50"
            align="middle"
            v-for="(reminder, index) in reminders"
            :key="index"
          >
            <el-col :span="3">
              <div class="q-if-focused text-primary remind-label">
                <span>{{ $t('common._common.remind') }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <q-select
                v-model="reminder.type"
                class="form-item"
                :options="reminderType | options('label', 'value')"
              />
            </el-col>
            <el-col :span="12">
              <q-select
                v-model="reminder.duration"
                class="form-item"
                :options="
                  generateReminderOptions(index == reminders.length - 1)
                    | options('label', 'value')
                "
                :clearable="true"
              />
            </el-col>
            <el-col :span="3">
              <div
                v-if="index != reminders.length - 1"
                class="add-entity-btn add-reminder"
                @click="removeReminder(index)"
              >
                <i class="el-icon-circle-close-outline"></i>
              </div>
              <div
                v-else-if="reminder.duration != -1"
                class="add-entity-btn add-reminder"
                @click="addReminder()"
              >
                <i class="el-icon-circle-plus-outline"></i>
              </div>
            </el-col>
          </el-row>
        </div>
        <div class="pT30">
          <div class="bold pB15">{{ $t('common._common.tasks') }}</div>
          <div class="col-12 summary-task-content pT15" v-if="taskList.length">
            <el-collapse accordion>
              <el-collapse-item
                :name="index"
                v-for="(task, index) in taskList"
                :key="index"
                class="newTaskCollapse"
              >
                <template slot="title">
                  <div class="row task-list">
                    <div
                      class="col-1 self-center taskstatusdot"
                      style="max-width: 30px;"
                    >
                      <div class="task-dot open"></div>
                    </div>
                    <div class="col-9 ellpsis fc-summary-content-row">
                      {{ task.subject ? task.subject : '' }}
                    </div>
                    <div class="col-1"></div>
                    <div class="col-1 self-center" @click="removeTask(index)">
                      <i
                        aria-hidden="true"
                        class="q-icon material-icons destroy"
                        style="font-size: 16px;font-weight: 500;"
                        >{{ $t('common._common.close') }}</i
                      >
                    </div>
                  </div>
                </template>
              </el-collapse-item>
            </el-collapse>
          </div>
          <div v-show="showAddTask" class="pT15">
            <new-task-form
              :assets="allAssets"
              :model="newTask"
              :isTask="true"
            ></new-task-form>
            <div class="pT30">
              <div class="flLeft primarybtn mR10">
                <el-button
                  type="primary"
                  plain
                  size="mini"
                  style="margin-top: -5px; background: rgb(57, 178, 194); color: white; border-color: rgb(57, 178, 194);"
                  @click="addTask"
                  >{{ $t('common._common.add') }}</el-button
                >
              </div>
              <div class="flLeft primarybtn">
                <el-button
                  style="margin-top: -5px;"
                  type="text"
                  @click="cancelNewTask"
                  >{{ $t('common._common.cancel') }}</el-button
                >
              </div>
            </div>
          </div>
          <div class="pT15" v-if="!showAddTask">
            <div @click="addNewTask" class="add-entity-btn">
              <i class="el-icon-circle-plus-outline"></i>
              <span>{{ $t('common._common.add_task') }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-form>
    <div class="modal-dialog-footer">
      <button type="button" class="modal-btn-cancel" @click="closeDialog">
        <!----><!----><span>{{ $t('common._common.cancel') }}</span>
      </button>
      <button type="button" class="modal-btn-save" @click="save">
        <!----><!----><span>{{ $t('common._common._save') }}</span>
      </button>
    </div>
  </el-dialog>
</template>

<script>
import { mapState } from 'vuex'
import NewWorkOrderForm from 'pages/workorder/widgets/forms/NewWorkOrderForm'
import NewTaskForm from 'pages/workorder/widgets/forms/NewTaskForm'
import FSchedule from '@/FSchedule'
import { QInput, QSelect, QDatetime } from 'quasar'
import { getFieldOptions } from 'util/picklist'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  props: ['visibility', 'defaultForm'],
  components: {
    QInput,
    QSelect,
    QDatetime,
    FSchedule,
    'new-workorder-form': NewWorkOrderForm,
    'new-task-form': NewTaskForm,
  },
  data() {
    return {
      assets: [],
      visibilityState: this.visibility,
      activeForm: {
        label: this.$t('maintenance.wr_list.new_wo'),
        value: 'newWorkOrder',
      },
      formList: [
        {
          label: this.$t('maintenance.wr_list.new_wo'),
          value: 'newWorkOrder',
        },
        {
          label: this.$t('maintenance.wr_list.new_wo_template'),
          value: 'newWorkOrderTemplate',
        },
        {
          label: this.$t('maintenance.wr_list.new_pm'),
          value: 'newPreventiveMaintenance',
        },
      ],
      newWorkOrder: {
        requester: {
          email: '',
        },
        subject: '',
        description: '',
        space: {
          id: '',
        },
        asset: {
          id: '',
        },
        category: {
          id: '',
        },
        type: {
          id: '',
        },
        priority: {
          id: '',
        },
        assignedTo: {
          id: '',
        },
        assignmentGroup: {
          id: '',
        },
        dueDate: null,
      },
      preventiveForm: {
        startTime: new Date(),
        schedule: null,
        stopAfter: 'never',
        stopAfterTime: '',
        stopAfterCount: 1,
      },
      reminders: [],
      stopAfterMenu: [
        {
          label: 'Never',
          value: 'never',
        },
        {
          label: 'Time',
          value: 'time',
        },
        {
          label: 'Count',
          value: 'count',
        },
      ],
      reminderType: [
        {
          label: 'Before',
          value: 1,
        },
        {
          label: 'After',
          value: 2,
        },
      ],
      taskList: [],
      showAddTask: false,
      newTask: {
        subject: '',
        space: {
          id: -1,
        },
        asset: {
          id: -1,
        },
        isReadingTask: false,
        readingFieldId: -1,
      },
      allAssets: [],
    }
  },
  created() {
    this.loadAssetPickListData()
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadGroups')
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketstatus: state => state.ticketStatus.workorder,
      ticketpriority: state => state.ticketPriority,
      ticketcategory: state => state.ticketCategory,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
  },
  watch: {
    visibility: function(newVal) {
      this.visibilityState = newVal
    },
    defaultForm: function() {
      if (this.defaultForm) {
        this.switchForm(this.defaultForm)
      }
    },
  },
  mounted() {
    if (this.defaultForm) {
      this.switchForm(this.defaultForm)
    }
    this.addReminder()
  },
  methods: {
    async loadAssetPickListData() {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'asset', skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.assets = options
      }
    },
    switchForm(formName) {
      this.activeForm = this.formList.find(form => form.value === formName)
    },
    close() {},
    closeDialog() {
      this.visibilityState = false
      this.handleClose()
    },
    handleClose(done) {
      let newpath = this.$route.path.replace('/new', '')
      this.$router.replace({ path: newpath })

      this.newWorkOrder = {
        requester: {
          email: '',
        },
        subject: '',
        description: '',
        space: {
          id: '',
        },
        asset: {
          id: '',
        },
        category: {
          id: '',
        },
        type: {
          id: '',
        },
        priority: {
          id: '',
        },
        assignedTo: {
          id: '',
        },
        assignmentGroup: {
          id: '',
        },
        dueDate: null,
      }
      this.preventiveForm = {
        startTime: new Date(),
        schedule: null,
        stopAfter: 'never',
        stopAfterTime: '',
        stopAfterCount: 1,
      }
      this.taskList = []
      this.showAddTask = false
    },
    addNewTask() {
      this.newTask = {
        subject: '',
        space: {
          id: -1,
        },
        asset: {
          id: -1,
        },
        isReadingTask: false,
        readingFieldId: -1,
      }
      this.showAddTask = true
    },
    addTask() {
      if (!this.newTask.subject) {
        this.$message({
          message: this.$t('common._common.task_title_required'),
          type: 'error',
        })
      } else {
        this.taskList.push(this.newTask)
        this.newTask = {
          subject: '',
          space: {
            id: -1,
          },
          asset: {
            id: -1,
          },
          isReadingTask: false,
          readingFieldId: -1,
        }
        this.showAddTask = false
      }
    },
    cancelNewTask() {
      this.showAddTask = false
    },
    removeTask(index) {
      this.taskList.splice(index, 1)
    },
    expand(expandTask) {
      for (let taskEntry of this.taskList) {
        taskEntry.expand = false
      }
      expandTask.expand = true
    },
    save() {
      let workorder = this.newWorkOrder
      if (workorder.dueDate) {
        workorder.dueDate = new Date(workorder.dueDate).getTime()
      }
      if (!workorder.space.id) {
        delete workorder.space
      }
      if (!workorder.asset.id) {
        delete workorder.asset
      }
      if (!workorder.category.id) {
        delete workorder.category
      }
      if (!workorder.priority.id) {
        delete workorder.priority
      }
      if (!workorder.assignmentGroup.id) {
        delete workorder.assignmentGroup
      }
      if (!workorder.assignedTo.id) {
        delete workorder.assignedTo
      }
      if (!workorder.dueDate) {
        delete workorder.dueDate
      }

      let formdata = {}
      formdata.workorder = workorder
      formdata.requester = workorder.requester
      formdata.tasks = this.taskList

      if (this.activeForm.value === 'newWorkOrder') {
        this.saveWorkOrder(formdata)
      } else if (this.activeForm.value === 'newWorkOrderTemplate') {
        this.saveWorkOrderTemplate(formdata)
      } else if (this.activeForm.value === 'newPreventiveMaintenance') {
        let preventiveFormData = {}
        let trigger = {}
        preventiveFormData.title = formdata.workorder.subject
        trigger.startTime = new Date(this.preventiveForm.startTime).getTime()
        trigger.schedule = this.preventiveForm.schedule
        preventiveFormData.triggers = []
        preventiveFormData.triggers.push(trigger)
        let reminderList = []
        for (let idx = 0; idx < this.reminders.length; idx++) {
          if (this.reminders[idx].duration !== -1) {
            reminderList.push(this.reminders[idx])
          }
        }
        formdata.reminders = reminderList

        if (this.preventiveForm.stopAfter === 'time') {
          preventiveFormData.endTime = new Date(
            this.preventiveForm.stopAfterTime
          ).getTime()
        } else if (this.preventiveForm.stopAfter === 'count') {
          preventiveFormData.maxCount = this.preventiveForm.stopAfterCount
        }
        formdata.preventivemaintenance = preventiveFormData
        this.savePreventiveMaintenance(formdata)
      }
    },
    saveWorkOrder(formdata) {
      let saveurl = '/workorder/add'
      let self = this
      self.$http
        .post(saveurl, formdata)
        .then(function(response) {
          if (typeof response.data === 'object') {
            if (response.data.workorder) {
              self.$store.state.workorder.workorders.splice(
                0,
                0,
                response.data.workorder
              )
            }
            self.$message({
              message: this.$t('common.wo_report.workorder_created_success'),
              type: 'success',
            })
            self.closeDialog()
          } else {
            self.$message({
              message: this.$t('common.wo_report.workorder_creation_failed'),
              type: 'error',
            })
          }
        })
        .catch(function(error) {
          console.log(error)
          self.$message({
            message: this.$t('common.wo_report.workorder_creation_failed'),
            type: 'error',
          })
        })
    },
    saveWorkOrderTemplate(formdata) {
      let saveurl = '/workorder/saveTemplate'
      let self = this
      self.$http
        .post(saveurl, formdata)
        .then(function(response) {
          self.$message({
            message: this.$t(
              'common._common.workorder_template_created_successfully'
            ),
            type: 'success',
          })
          self.closeDialog()
        })
        .catch(function(error) {
          if (error) {
            self.$message({
              message: this.$t(
                'common._common.workorder_template_creation_failed'
              ),
              type: 'error',
            })
          }
        })
    },
    savePreventiveMaintenance(formdata) {
      let saveurl = '/workorder/addPreventiveMaintenance'
      let self = this
      self.$http
        .post(saveurl, formdata)
        .then(function(response) {
          self.$message({
            message: this.$t(
              'common.wo_report.preventive_maintenance_creation_failed'
            ),
            type: 'success',
          })
          self.closeDialog()
        })
        .catch(function(error) {
          console.log(error)
          self.$message({
            message: this.$t(
              'common.wo_report.preventive_maintenance_creation_failed'
            ),
            type: 'error',
          })
        })
    },
    generateReminderOptions(isNever) {
      let options = []
      if (isNever) {
        options.push({
          label: 'Never',
          value: -1,
        })
      }

      for (let i = 1; i < 24; i++) {
        options.push({
          label: i + (i === 1 ? ' Hour' : ' Hours'),
          value: i * 3600,
        })
      }

      for (let i = 1; i < 32; i++) {
        options.push({
          label: i + (i === 1 ? ' Day' : ' Days'),
          value: i * 24 * 3600,
        })
      }

      return options
    },
    addReminder() {
      this.reminders.push({
        duration: -1,
        type: 1,
      })
    },
    removeReminder(index) {
      this.reminders.splice(index, 1)
    },
  },
}
</script>
<style>
.collapsible-entity {
  padding: 12px 0;
  border-bottom: solid 1px rgba(57, 177, 193, 0.3);
}
.collapsible-entity.collapsed {
  cursor: pointer;
}
.collapsible-entity.first {
  border-top: solid 1px rgba(57, 177, 193, 0.3);
}
.collapsible-entity .title {
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.6px;
  color: #333;
}
.collapsible-entity .email {
  font-size: 13px;
  letter-spacing: 0.4px;
  color: #259aa9;
}
.collapsible-entity .subtitle {
  font-size: 14px;
  letter-spacing: 0.4px;
  color: #333333;
}
.firstentry {
  border-top: none;
  border-bottom: none;
}
.firstentry.collapsed {
  margin-top: 20px;
  border-top: solid 1px rgba(57, 177, 193, 0.3);
  border-bottom: solid 1px rgba(57, 177, 193, 0.3);
}
.fc-form-content .el-select,
.fc-form-content .el-cascader {
  width: 100%;
}
.add-reminder {
  margin-top: 12px;
  text-align: center;
}
.remind-label {
  margin-top: 16px;
  text-align: center;
}
</style>
