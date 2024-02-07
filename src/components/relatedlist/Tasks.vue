<template>
  <div class="fc-tasks">
    <div class="text-center" v-if="loading">
      <spinner :show="loading"></spinner>
    </div>
    <div v-else>
      <ul class="task-list">
        <li
          v-for="(task, index) in tasks"
          class="task"
          :key="task.id"
          :class="{
            completed: task.status && task.status.type === 'CLOSED',
            editing: task == editedTask,
          }"
        >
          {{ task }}
          <div class="view">
            <input
              class="toggle"
              type="checkbox"
              @change="changeTaskStatus(task)"
              v-model="completedTasks"
              :value="task.id"
            />
            <label @dblclick="editTask(task)">{{ task.subject }}</label>
            <input
              class="edit el-input__inner"
              v-model="task.subject"
              v-task-focus="task == editedTask"
              autofocus
              @blur="doneEdit(task)"
              @keyup.enter="doneEdit(task)"
              @keyup.esc="cancelEdit(task)"
            />
            <div
              style="left:530px;color: #dedede;font-size: 17px; padding-top: 0px; padding-right: 10px;padding-left:10px;"
              class="task-actions"
            >
              |
            </div>
            <div class="task-actions">
              <div class="action">
                <q-icon style="color: #aaa;" name="location on" />
                <span style="color: #888;">{{
                  task.space ? task.space.name : '---'
                }}</span>
                <q-popover ref="spacepopover" class="fc-task-assignee-popover">
                  <q-list link class="scroll">
                    <q-item
                      v-for="space in spaces"
                      :key="space.id"
                      @click="
                        assignSpace(index, space.id, space.name),
                          $refs.spacepopover[index].close()
                      "
                    >
                      <q-item-main :label="space.name" />
                    </q-item>
                  </q-list>
                </q-popover>
              </div>
            </div>
          </div>
          <button class="destroy" @click="removeTask(task)"></button>
        </li>
      </ul>
      <div class="add-task">
        <textarea
          v-model="newTask"
          placeholder="Add a task  (Hit Enter to save)"
          class="add-task-input"
          @keydown.enter="addTask"
        ></textarea>
      </div>
    </div>
  </div>
</template>
<script>
import { QPopover, QList, QItem, QItemMain, QIcon } from 'quasar'

export default {
  props: ['module', 'record'],
  data() {
    return {
      loading: false,
      tasks: [],
      newTask: '',
      editedTask: null,
      completedTasks: [],
    }
  },
  components: {
    QPopover,
    QList,
    QItem,
    QItemMain,
    QIcon,
  },
  mounted() {
    this.loadTasks()
  },
  watch: {
    record: function() {
      this.loadTasks()
    },
    tasks: function() {
      this.record.noOfTasks = this.tasks.length
      this.record.noOfClosedTasks = this.completedTasks.length
    },
    completedTasks: function() {
      this.record.noOfClosedTasks = this.completedTasks.length
    },
  },
  directives: {
    'task-focus': function(el) {
      el.focus()
    },
  },
  computed: {
    spaces() {
      return this.$store.state.spaces
    },
    openStatusId() {
      return this.$store.getters.getTicketStatusByLabel(
        'Submitted',
        'workorder'
      ).id
    },
    closedStatusId() {
      return this.$store.getters.getTicketStatusByLabel('Closed', 'workorder')
        .id
    },
  },
  created() {
    this.$store.dispatch('loadTicketStatus', 'workorder')
  },
  methods: {
    loadTasks() {
      let self = this
      self.loading = true
      return this.$http
        .get('/task?module=' + this.module + '&recordId=' + this.record.id)
        .then(function(response) {
          self.loading = false
          self.tasks = response.data.tasks ? response.data.tasks : []

          for (let idx in self.tasks) {
            if (!self.tasks[idx].status) {
              self.tasks[idx].status = {
                type: 'OPEN',
                typeCode: 1,
                id: self.openStatusId,
              }
            } else {
              if (self.tasks[idx].status.typeCode === 2) {
                self.completedTasks.push(self.tasks[idx].id)
              }
            }
          }
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.tasks = []
          }
        })
    },
    addTask() {
      let task = {
        parentTicketId:
          typeof this.record.id === 'object'
            ? this.record.id[0]
            : this.record.id,
        subject: this.newTask,
        description: this.newTask,
        assignedTo: null,
        space: null,
        status: {
          type: 'OPEN',
          id: this.openStatusId,
        },
      }
      this.doneEdit(task)
    },

    removeTask: function(task) {
      if (task.id) {
        let data = {
          id: [task.id],
        }

        let self = this
        self.$http.post('/task/delete', data).then(() => {
          self.tasks.splice(self.tasks.indexOf(task), 1)
        })
      }
    },

    editTask: function(task) {
      this.beforeEditCache = task.subject
      this.editedTask = task
    },

    doneEdit: function(task) {
      let self = this
      if (task.subject.trim() === '') {
        return
      }
      this.editedTask = null
      task.description = task.subject
      if (task.id) {
        let data = {
          id: [task.id],
          task: {
            id: task.id,
            parentTicketId: task.parentTicketId,
            subject: task.subject,
            description: task.description,
          },
        }

        self.$http.post('/task/update', data)
      } else {
        self.$http.post('/task/add', { task: task }).then(function(response) {
          task.id = response.data.taskId
          self.tasks.push(task)
          self.newTask = ''
        })
      }
    },

    cancelEdit: function(task) {
      this.editedTask = null
      task.subject = this.beforeEditCache
    },

    changeTaskStatus: function(task) {
      let completed = false
      for (let x in this.completedTasks) {
        if (this.completedTasks[x] === task.id) {
          completed = true
        }
      }

      let data = {
        id: [task.id],
        task: {
          id: task.id,
          parentTicketId: task.parentTicketId,
          status: {
            id: completed ? this.closedStatusId : this.openStatusId,
            type: completed ? 'CLOSED' : 'OPEN',
          },
        },
      }

      this.$http.post('/task/updatestatus', data).then(() => {
        if (completed) {
          task.status.type = 'CLOSED'
        } else {
          task.status.type = 'OPEN'
        }
      })
    },
    assignSpace: function(index, spaceId, name) {
      let task = this.tasks[index]

      let data = {
        id: [task.id],
        task: {
          id: task.id,
          parentTicketId: task.parentTicketId,
          space: {
            id: spaceId,
          },
        },
      }

      this.$http.post('/task/assign', data).then(() => {
        if (spaceId > 0) {
          task.space = {
            id: spaceId,
            name: name,
          }
        } else {
          task.space = null
        }
      })
    },
  },
}
</script>
<style>
.fc-tasks .add-task {
  margin-top: 20px;
  position: relative;
}

.fc-tasks .add-task-input {
  border: 1px solid rgb(237, 237, 237);
  resize: none;
  padding: 10px;
  outline: none;
  font-size: 12px;
  width: 100%;
  transition: 0.4s;
  height: 35px;
  overflow: hidden;
}

.taskstatus {
  border: 1px solid #ddd;
  border-radius: 50%;
  padding-left: 7px;
  padding-right: 6px;
  padding-top: 6px;
  padding-bottom: 3px;
}
.taskstatus svg {
  fill: #ddd;
  height: 12px;
  width: 12px;
}
.fc-tasks .fc-tasks-empty {
  margin: 0 auto;
  font-size: 13px;
  padding: 20px;
  text-align: center;
}

.fc-tasks .fc-tasks-empty .empty-msg {
  margin-bottom: 10px;
  color: #666;
}

.fc-tasks .add-button {
  color: rgb(41, 179, 167);
  background-color: white;
  padding: 5px 10px;
  font-size: 11px;
  border: none;
  outline: none;
  cursor: pointer;
  border: 1px solid rgb(41, 179, 167);
}

.fc-tasks .fc-task-row {
  margin-bottom: -1px;
  padding-bottom: 1px;
  position: relative;
}

.fc-tasks .new-task,
.edit {
  position: relative;
  margin: 0;
  width: 100%;
  font-family: inherit;
  font-weight: inherit;
  line-height: 13px;
  border: 0;
  color: inherit;
  padding: 3px;
  box-sizing: border-box;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.fc-tasks .new-task {
  padding: 16px 16px 16px 60px;
  border: none;
  background: rgba(0, 0, 0, 0.003);
  box-shadow: inset 0 -2px 1px rgba(0, 0, 0, 0.03);
}

.fc-tasks .task-list {
  margin: 0;
  padding: 0;
  list-style: none;
  margin: 12px 0;
  font-size: 13px;
}

.fc-tasks .task-list li {
  position: relative;
  border-bottom: 1px solid #f7f7f7;
  padding: 5px 0;
}

.fc-tasks .task-list li:hover {
  background: #fafafa;
}

.fc-tasks .task-list .view {
  position: relative;
}

.fc-tasks .task-list li:last-child {
  border-bottom: none;
}

.fc-tasks .task-list li.editing {
  border-bottom: 1px solid #30c9f7;
}

.fc-tasks .task-list li.editing .edit {
  display: block !important;
  width: 400px;
  padding: 7px 14px;
  margin: 0 0 0 35px;
  border-top: 0;
  border-left: 0;
  border-right: 0;
  outline: none;
  background: transparent;
  font-size: 14px;
}

.fc-tasks .task-list li.editing .view label {
  display: none;
}

.fc-tasks .task-list li .toggle {
  text-align: center;
  width: 30px;
  height: 30px;
  position: absolute;
  top: 0px;
  bottom: 0;
  margin: auto 0;
  border: none; /* Mobile Safari */
  -webkit-appearance: none;
  appearance: none;
  outline: none;
  cursor: pointer;
}

.fc-tasks .task-list li .toggle:after {
  content: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="-10 -18 100 135"><circle cx="50" cy="50" r="50" fill="none" stroke="#bbb" stroke-width="3"/><path fill="#bbb" d="M72 25L42 71 27 56l-4 4 20 20 34-52z"/></svg>');
}

.fc-tasks .task-list li .toggle:checked:after {
  content: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="-10 -18 100 135"><circle cx="50" cy="50" r="50" fill="none" stroke="#5dc2af" stroke-width="3"/><path fill="#5dc2af" d="M72 25L42 71 27 56l-4 4 20 20 34-52z"/></svg>');
  opacity: 0.5;
}

.fc-tasks .task-list li label {
  word-break: break-all;
  padding: 15px 60px 15px 18px;
  margin-left: 30px;
  display: block;
  line-height: 1.2;
  transition: color 0.4s;
  width: 500px;
  cursor: pointer;
}

.fc-tasks .task-list li.completed label {
  color: #d9d9d9;
  text-decoration: line-through;
}

.fc-tasks .task-list li button {
  margin: 0;
  padding: 0;
  border: 0;
  background: none;
  font-size: 100%;
  vertical-align: baseline;
  font-family: inherit;
  font-weight: inherit;
  color: inherit;
  -webkit-appearance: none;
  appearance: none;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.fc-tasks .task-list li .task-actions {
  position: absolute;
  left: 550px;
  top: 14px;
}

.fc-tasks .task-list li .task-actions .action {
  cursor: pointer;
  float: left;
  padding: 0 15px;
}

.fc-tasks .task-list li .task-actions .action .q-item-main {
  display: inline-flex;
}

.fc-tasks .task-list li .destroy {
  display: none;
  position: absolute;
  top: 0;
  right: 10px;
  bottom: 0;
  width: 30px;
  height: 30px;
  margin: auto 0;
  font-size: 30px;
  color: #cc9a9a;
  transition: color 0.2s ease-out;
  outline: none;
  cursor: pointer;
}

.fc-tasks .task-list li .destroy:hover {
  color: #af5b5e;
}

.fc-tasks .task-list li .destroy:after {
  content: '×';
}

.fc-tasks .task-list li:hover .destroy {
  display: block;
}

.fc-tasks .task-list li .edit {
  display: none !important;
}

.fc-tasks .task-list li.editing:last-child {
  margin-bottom: -1px;
}

.fc-task-assignee-popover .q-list {
  min-width: 150px;
}

.fc-task-assignee-popover .q-item {
  font-size: 13px;
}
</style>
