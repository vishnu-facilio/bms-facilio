<template>
  <div id="summarytask" class="pL30 pR30">
    <div class="pT20" v-if="newTaskList.length > 0">
      <div
        v-if="sequencedData"
        v-for="(obj, index) in newTaskList"
        :key="index"
      >
        <div
          v-if="sections[obj.sectionId]"
          class="task-subject pB10 pL8"
          :style="{
            'border-bottom': sections[obj.sectionId]
              ? 'solid 2px #d9f4f7'
              : '0',
          }"
        >
          {{ sections[obj.sectionId] ? sections[obj.sectionId].name : '' }}
        </div>
        <div
          v-bind:class="{
            selected: selectedTask === task,
            closed: task.status.type !== 'OPEN',
          }"
          v-for="(task, index1) in obj.taskList"
          :key="index1"
          class="task-list"
        >
          <div style="cursor:pointer;padding-left:5px;margin-right:8px">
            <i
              v-if="task.status.type === 'OPEN'"
              class="el-icon-circle-check-outline"
              style="color: #b8e5eb;font-size: 24px;"
            ></i>
            <i
              v-else
              class="el-icon-circle-check"
              style="color: #39b2c2;font-size: 24px;"
            ></i>
          </div>
          <div class="task-item width100 pL5 position-relative">
            <el-row>
              <el-col :span="9">
                <el-input
                  v-if="task.id === -1"
                  style="width:100%;"
                  v-model="task.subject"
                  type="text"
                  :placeholder="$t('maintenance._workorder.write_a_task_name')"
                ></el-input>
                <div
                  v-else
                  :class="[
                    { closedTask: task.status.type !== 'OPEN' },
                    'task-subject pL8 flLeft',
                  ]"
                  type="text"
                  placeholder
                  class="pL0 max-width480px width100 ellipsis"
                >
                  {{ task.subject }}
                </div>
                <div
                  v-if="task.id === -1 || (task.resource && task.resource.name)"
                  class="pT5 mL10 flLeft"
                >
                  <div
                    v-if="task.id === -1"
                    class="flLeft"
                    style="height:10px; width:10px;cursor:pointer;"
                  >
                    <img src="~statics/icons/assetspace.svg" />
                  </div>
                  <div v-else class="flLeft pR5">
                    <img
                      v-if="task.resource.resourceType === 1"
                      src="~statics/space/space-resource.svg"
                      style="height:12px; width:14px;"
                    />
                    <img
                      v-else
                      src="~statics/space/asset-resource.svg"
                      style="height:11px; width:14px;"
                    />
                  </div>
                  <div class="f12 pL6 ellipsis max-width300px blue-color">
                    {{ task.resource.name || '' }}
                  </div>
                </div>
              </el-col>
              <el-col :span="15" class="height0">
                <div class="flRight row">
                  <div>
                    <span v-if="task.inputType === 5 || task.inputType === '5'">
                      <el-radio-group
                        v-if="checkOptionsLength(task.options)"
                        v-model="task.inputValue"
                      >
                        <el-radio
                          disabled
                          v-for="(option, index) in task.options"
                          class="task-radio-group"
                          color="secondary"
                          :label="option"
                          :key="index"
                          >{{ option }}</el-radio
                        >
                      </el-radio-group>
                    </span>
                    <el-input
                      disabled
                      v-else-if="task.inputType === 3 || task.inputType === '3'"
                      type="text"
                      :placeholder="$t('maintenance._workorder.enter_text')"
                      v-model="task.inputValue"
                    ></el-input>
                    <el-input
                      disabled
                      v-else-if="task.inputType === 4 || task.inputType === '4'"
                      type="number"
                      :placeholder="
                        $t('maintenance._workorder.enter_numeric_value')
                      "
                      v-model="task.inputValue"
                    ></el-input>
                    <div
                      v-else-if="task.inputType === 2 || task.inputType === '2'"
                    >
                      <el-input
                        disabled
                        class="inline"
                        style="width:auto"
                        type="number"
                        v-model="task.inputValue"
                        :placeholder="
                          $t('maintenance._workorder.enter_reading')
                        "
                      ></el-input>
                      <div
                        class="inline"
                        style="padding-left:7px"
                        v-if="task.readingField && task.readingField.unit"
                      >
                        {{ task.readingField.unit }}
                      </div>
                      <!-- <el-date-picker disabled class="reading-time" :placeholder="$t('maintenance._workorder.time_of_reading')" v-model="task.inputTime" :type="'datetime'" :default-value="new Date(workorder.createdTime)"></el-date-picker> -->
                    </div>
                    <div v-else-if="task.inputValue" class="task-subject pL8">
                      {{ task.inputValue }}
                    </div>
                  </div>
                  <div v-if="task.remarks" class="mR15">
                    <span>
                      <img
                        @click="
                          ;(remarkDialog = true),
                            (selectedRemark = task.remarks)
                        "
                        width="20"
                        height="20"
                        src="~assets/comment-bubble.svg"
                      />
                    </span>
                  </div>
                  <div v-if="task.noOfAttachments > 0">
                    <el-badge
                      class="badge fc-approval-badge"
                      @click="showAttachment(task)"
                      :value="task.noOfAttachments"
                      :max="99"
                    >
                      <img
                        src="~assets/photo-symbol.svg"
                        width="28"
                        height="18"
                      />
                    </el-badge>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
      <el-dialog
        title="Remark"
        :visible.sync="remarkDialog"
        width="30%"
        class="fc-dialog-center-container"
      >
        <div class="height300">
          <div class="label-txt-black">{{ selectedRemark }}</div>
        </div>
        <div slot="footer" class="modal-dialog-footer">
          <el-button
            @click=";(remarkDialog = false), (selectedRemark = null)"
            class="btn-green-full"
            >Cancel</el-button
          >
          <!-- <el-button type="primary" @click="innerVisible = true">open the inner Dialog</el-button> -->
        </div>
      </el-dialog>
      <div
        v-if="!sequencedData"
        v-for="(section, index) in Object.keys(taskList).sort()"
        :key="index"
      >
        <div
          v-if="sections[section]"
          style="padding-top:35px; font-size: 13px;font-weight: bold;letter-spacing: 0.6px;color: #333333;padding-bottom: 10px;padding-left:8px;"
          :style="{
            'border-bottom': sections[section] ? 'solid 2px #d9f4f7' : '0',
          }"
        >
          {{ sections[section] ? sections[section].name : '' }}
        </div>
        <div
          v-bind:class="{
            selected: selectedTask === task,
            closed: task.status.type !== 'OPEN',
          }"
          v-for="(task, index1) in taskList[section]"
          :key="index1"
          class="task-list"
        >
          <div style="cursor:pointer;padding-left:5px;margin-right:8px">
            <i
              v-if="task.status.type === 'OPEN'"
              class="el-icon-circle-check-outline"
              style="color: #b8e5eb;font-size: 24px;"
            ></i>
            <i
              v-else
              class="el-icon-circle-check"
              style="color: #39b2c2;font-size: 24px;"
            ></i>
          </div>
        </div>
      </div>
      <!-- <div class="pT15" style="padding-bottom:30px;" v-if="canEdit">
            <el-button @click="addNewTask" class="btnsize txt" style="font-size: 10px;font-weight: bold;padding-right: 13px !important;padding-left: 13px !important;border: 1px solid rgb(57, 178, 194);color: rgb(57, 178, 194);padding-top: 6px !important;padding-bottom: 6px !important;border-radius: 3px;">{{$t("maintenance._workorder.add_task")}}</el-button>
            <el-button v-show="false" @click="addNewTask" class="btnsize txt" style="font-size: 10px;font-weight: bold;padding-right: 13px !important;padding-left: 13px !important;border: 1px solid rgb(57, 178, 194);color: rgb(57, 178, 194);padding-top: 6px !important;padding-bottom: 6px !important;border-radius: 3px;">{{$t("maintenance._workorder.add_section")}}</el-button>
      </div>-->
    </div>
    <div v-else class="text-center mT50">
      <img src="~statics/nochartData.png" width="100" height="100" />
    </div>
    <!-- PreviewFile component -->
    <preview-file
      :visibility.sync="visibility"
      v-if="selectedTask && visibility"
      :previewFile="selectedTask.attachments[0]"
      :files="selectedTask.attachments"
    ></preview-file>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import PreviewFile from '@/PreviewFile'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['record'],
  watch: {
    record: function(newVal) {
      if (newVal) {
        this.workorder = newVal
        this.loadTasks()
      }
    },
  },
  components: {
    PreviewFile,
  },
  created() {
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadGroups')
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketcategory: state => state.ticketCategory,
      ticketpriority: state => state.ticketPriority,
      ticketstatus: state => state.ticketStatus.workorder,
      sites: state => state.site,
    }),
    ...mapGetters(['getTicketTypePickList']),
    tickettype() {
      return this.getTicketTypePickList()
    },
    openStatusId() {
      let id = this.$store.getters.getTicketStatusByLabel(
        'Submitted',
        'workorder'
      ).id
      if (id) {
        return parseInt(id)
      }
      return id
    },
    closedStatusId() {
      let id = this.$store.getters.getTicketStatusByLabel('Closed', 'workorder')
        .id
      if (id) {
        return parseInt(id)
      }
      return id
    },
    ticketStatusValue() {
      if (this.workorder && this.workorder.status && this.workorder.status.id) {
        return (
          this.$store.getters.getTicketStatus(
            this.workorder.status.id,
            'workorder'
          ).displayName || ''
        )
      }
      return ''
    },
  },
  data() {
    return {
      attachmentDialog: false,
      remarkDialog: false,
      selectedRemark: null,
      showSiteEdit: true,
      loading: true,
      selectedTask: null,
      sequencedData: false,
      taskList: {},
      newTaskList: [],
      sections: {},
      showAddTask: false,
      visibility: false,
    }
  },
  mounted() {
    if (this.record) {
      this.workorder = this.record
    }
    this.loadTasks()
    let currentSiteId = Number(this.$cookie.get('fc.currentSite')) || -1
    if (currentSiteId !== -1) {
      this.showSiteEdit = false
    }
  },
  methods: {
    showAttachment(task) {
      this.visibility = true
      this.selectedTask = task
    },
    checkOptionsLength(options) {
      return options.length <= 2 && !options.some(option => option.length > 10)
    },
    loadTasks() {
      let moduleId = this.$getProperty(this.workorder, 'moduleState.id', null)
      this.canEdit =
        !isEmpty(moduleId) &&
        !this.$store.getters.isStatusLocked(
          this.workorder.moduleState.id,
          'workorder'
        )
      this.loading = true
      this.newTaskList = []
      this.taskList = {}
      return this.$http
        .get('/task?module=task&recordId=' + this.workorder.id)
        .then(response => {
          this.loading = false
          if (
            response.data.tasks[Object.keys(response.data.tasks)[0]][0]
              .sequence !== -1
          ) {
            let unsorted = []
            Object.keys(response.data.tasks).forEach(key => {
              unsorted.push({
                secId: key,
                seq: response.data.tasks[key][0].sequence,
              })
            })
            let sorted = this.sort(unsorted)
            let newTaskList = []
            sorted.forEach(ele => {
              newTaskList.push({
                sectionId: ele.secId,
                taskList: response.data.tasks[ele.secId],
              })
            })
            newTaskList = newTaskList.map((obj, objInx) => {
              let tasks = obj.taskList.map((task, index) => {
                this.loadAttachments(task)
                if (!task.status || task.status.id === this.openStatusId) {
                  task.status = {
                    type: 'OPEN',
                    typeCode: 1,
                    id: this.openStatusId,
                  }
                } else {
                  task.status = {
                    type: 'CLOSED',
                    typeCode: 2,
                    id: this.closedStatusId,
                  }
                }
                if (task.inputTime === -1) {
                  task.inputTime = null
                }
                return task
              })
              obj.taskList = tasks
              return obj
            })
            this.newTaskList = newTaskList
            this.sequencedData = true
          } else {
            this.sequencedData = false
          }
          this.taskList = response.data.tasks ? response.data.tasks : {}
          for (let idx in this.taskList) {
            for (let idx2 in this.taskList[idx]) {
              if (
                !this.taskList[idx][idx2].status ||
                this.taskList[idx][idx2].status.id === this.openStatusId
              ) {
                this.taskList[idx][idx2].status = {
                  type: 'OPEN',
                  typeCode: 1,
                  id: this.openStatusId,
                }
              } else {
                this.taskList[idx][idx2].status = {
                  type: 'CLOSED',
                  typeCode: 2,
                  id: this.closedStatusId,
                }
              }
              if (this.taskList[idx][idx2].inputTime === -1) {
                this.taskList[idx][idx2].inputTime = null
              }
            }
          }
          this.sections = response.data.sections ? response.data.sections : {}
        })
        .catch(error => {
          if (error) {
            this.loading = false
            this.tasks = []
          }
        })
    },
    loadAttachments(task) {
      return this.$http
        .get('/attachment?module=taskattachments&recordId=' + task.id)
        .then(response => {
          if (response.status === 200) {
            this.loading = false
            task.attachments = response.data.attachments
              ? response.data.attachments
              : []
          }
        })
    },
    sort(arr) {
      let len = arr.length
      for (let i = len - 1; i >= 0; i--) {
        for (let j = 1; j <= i; j++) {
          if (arr[j - 1].seq > arr[j].seq) {
            let temp = arr[j - 1]
            arr[j - 1] = arr[j]
            arr[j] = temp
          }
        }
      }
      return arr
    },
  },
}
</script>
<style>
#summarytask .closedTask.el-input .el-input__inner {
  color: #a7c4c7;
}
</style>
