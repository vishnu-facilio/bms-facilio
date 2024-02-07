<template>
  <div class="container-scroll">
    <div class="row setting-Rlayout">
      <div class="col-lg-12 col-md-12">
        <table
          :class="[
            'setting-list-view-table',
            module === 'ticketPriority' ? 'mT70' : 'mT30',
          ]"
        >
          <thead>
            <tr>
              <th class="setting-table-th setting-th-text width100px"></th>
              <th class="setting-table-th setting-th-text pL0 width200px">
                {{ $t('common.header.priority') }}
              </th>
              <th class="setting-table-th setting-th-text width250px">
                {{ $t('common._common.description') }}
              </th>
              <th class="setting-table-th setting-th-text width200px">
                {{ $t('common.header.priority_color') }}
              </th>
              <th class="setting-table-th setting-th-text width150px"></th>
              <th class="setting-table-th setting-th-text width100px"></th>
            </tr>
          </thead>
          <tbody v-if="loading">
            <tr>
              <td colspan="100%" class="text-center">
                <spinner :show="loading" size="80"></spinner>
              </td>
            </tr>
          </tbody>
          <tbody v-else-if="$validation.isEmpty(moduleTypeList)">
            <tr>
              <td colspan="100%" class="text-center">
                {{ $t('common.header.no_priorities_yet') }}
              </td>
            </tr>
          </tbody>
          <draggable
            v-else
            v-model="moduleTypeList"
            @change="updateTypesOrder"
            :options="{
              draggable: moveIt ? '.asd' : '',
              handle: '.dragable',
              ghostClass: 'drag-ghost',
              dragClass: 'custom-drag',
              animation: 150,
            }"
            :element="'tbody'"
          >
            <tr
              class="asd visibility-visible-actions"
              v-bind:class="{ tablerow: !moveIt, activedrag: moveIt }"
              v-for="(moduleType, index) in moduleTypeList"
              :key="index"
            >
              <td class="width100px"></td>
              <td class="pL0 pR0 width200px">
                <div v-if="moduleType.isNew" class="display-flex-between-space">
                  <el-input
                    v-model="moduleType[moduleFields.displayName]"
                    class="fc-border-select width250px"
                  ></el-input>
                </div>
                <div v-else>
                  <span>{{ moduleType[moduleFields.displayName] }}</span>
                </div>
              </td>
              <td class="width250px">
                <div v-if="moduleType.isNew" class="display-flex-between-space">
                  <el-input
                    v-model="moduleType[moduleFields.description]"
                    class="fc-border-select width250px"
                  ></el-input>
                  <i
                    class="el-icon-check pointer fR fc-icon-grey priority-check-icon visibility-hide-actions"
                    @click="addEditType(moduleType)"
                  ></i>
                </div>
                <div v-else>
                  <span>{{ moduleType[moduleFields.description] }}</span>
                  <i
                    class="el-icon-edit pointer fR fc-icon-grey visibility-hide-actions"
                    @click="enableEdit(moduleType, index)"
                  ></i>
                </div>
              </td>
              <td class="width200px">
                <div v-if="moduleType.isNew" class="fc-color-picker">
                  <el-color-picker
                    v-if="moduleType.isNew"
                    v-model="moduleType[moduleFields.color]"
                    class="mR10"
                    popper-class="fc-color-pallete"
                  ></el-color-picker>
                  <el-input
                    v-model="moduleType[moduleFields.color]"
                    class="width130px fc-border-select"
                    placeholder="Pick the priority color"
                  ></el-input>
                </div>
                <span v-else>
                  <i
                    class="fa fa-circle priorityMediumtag"
                    v-bind:style="{ color: moduleType[moduleFields.color] }"
                    aria-hidden="true"
                  ></i>
                  {{
                    moduleType[moduleFields.color]
                      ? moduleType[moduleFields.color]
                      : ''
                  }}
                </span>
              </td>
              <td class="width150px">
                <div
                  class="text-left visibility-hide-actions"
                  style="margin-top:-3px;margin-right: 15px;text-align:center;"
                >
                  <img
                    src="~assets/add-icon.svg"
                    style="height:18px;width:18px;"
                    class="mR10"
                    @click="addType(moduleType, index)"
                  />
                  <img
                    v-if="!moduleType.isDefault"
                    src="~assets/remove-icon.svg"
                    style="height:18px;width:18px;margin-right: 3px;"
                    @click="deleteType(moduleType, index)"
                  />
                </div>
              </td>
              <td class="width100px"></td>
            </tr>
          </draggable>
        </table>
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import draggable from 'vuedraggable'
import { mapState } from 'vuex'
import cloneDeep from 'lodash/cloneDeep'

export default {
  props: ['module'],
  components: {
    draggable,
  },
  title() {
    return this.title
  },
  data() {
    return {
      loading: false,
      moduleFields: {},
      moduleTypeList: null,
      moveIt: true,
      priorities: this.ticketPriority,
      title: null,
    }
  },
  created() {
    this.fetchData()
    this.$store.dispatch('loadAlarmSeverity')
  },

  computed: {
    ...mapState({
      alarmSeverity: state => state.alarmSeverity,
      ticketPriority: state => state.ticketPriority,
    }),
  },

  methods: {
    fetchData(forceFetch = false) {
      this.loading = true
      Promise.all([
        this.$store.dispatch('loadTicketPriority', forceFetch),
        this.$store.dispatch('loadAlarmSeverity', forceFetch),
      ]).then(() => {
        this.initData()
        this.loading = false
      })
    },
    initData() {
      let fields = {
        ticketPriority: {
          displayName: 'displayName',
          description: 'description',
          typeName: 'priority',
          color: 'colour',
          sequenceNumber: 'sequenceNumber',
          title: 'priority',
        },
        alarmSeverity: {
          displayName: 'displayName',
          typeName: 'severity',
          color: 'color',
          sequenceNumber: 'cardinality',
          title: 'severity',
        },
      }

      this.moduleFields.displayName = 'displayName'
      this.moduleTypeList = cloneDeep(this[this.module])
      this.title = fields[this.module].title

      Object.entries(fields[this.module]).forEach(([key, value]) => {
        key !== 'title' ? (this.moduleFields[key] = value) : null
      })
    },
    updateTypesOrder() {
      // draggable action handled
      let order = 0
      this.moduleTypeList.forEach(element => {
        element[this.moduleFields.sequenceNumber] = ++order
      })
      this.updateTypes()
    },
    updateTypes() {
      // update multiple types
      let url,
        params = {}
      let updateModule = {
        ticketPriority: {
          url: 'updateTicketPriorities',
          paramObj: 'ticketPriorties',
        },
        alarmSeverity: {
          url: 'updateAlarmSeverities',
          paramObj: 'alarmSeverities',
        },
      }

      url = `/v2/picklist/${updateModule[this.module].url}`
      params[updateModule[this.module].paramObj] = this.moduleTypeList

      API.post(url, params).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.fetchData(true)
          this.$dialog.notify('Successfully updated the ' + this.title)
        }
      })
    },
    addEditType(moduleType) {
      let url,
        params = {}
      let addOrUpdateType = {
        ticketPriority: {
          paramObj: 'ticketPriority',
          addUrl: 'addTicketPriority',
          updateUrl: 'updateTicketPriority',
        },
        alarmSeverity: {
          paramObj: 'assetSeverity',
          addUrl: 'addAlarmSeverity',
          updateUrl: 'updateAlarmSeverity',
        },
      }

      params[addOrUpdateType[this.module].paramObj] = moduleType
      url = `/v2/picklist/${
        moduleType.id
          ? addOrUpdateType[this.module].updateUrl
          : addOrUpdateType[this.module].addUrl
      }`

      API.post(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Something went wrong')
        } else {
          moduleType.id = data.record.id
          moduleType.isNew = false
          this.$store.commit('GENERIC_ADD_OR_UPDATE', {
            type: this.module,
            data: data.record,
          })
          this.$dialog.notify('Successfully updated the ' + this.title)
          this.fetchData(true)
        }
      })
    },
    addType(prior, index) {
      // Detele type dummy object
      let data = {
        displayName: null,
        description: null,
        [this.moduleFields.sequenceNumber]:
          prior[this.moduleFields.sequenceNumber] + 1,
        isNew: true,
        [this.moduleFields.color]: '#E6E6EA',
      }
      this.moduleTypeList.splice(index + 1, 0, data)
    },
    enableEdit(moduleType, index) {
      let { moduleTypeList } = this

      moduleType.isNew = true
      moduleTypeList.splice(index, 1, moduleType)
      this.$set(this, 'moduleTypeList', moduleTypeList)
    },
    deleteType(moduleType, index) {
      // Detele  type api call
      if (!moduleType.id) {
        this.moduleTypeList.splice(index, 1)
        return
      }

      let deleteModule = {
        ticketPriority: {
          url: 'deleteTicketPriority',
          paramObj: 'ticketPriority',
        },
        alarmSeverity: {
          url: 'deleteAlarmSeverity',
          paramObj: 'assetSeverity',
        },
      }
      let url,
        params = {}

      url = `/v2/picklist/${deleteModule[this.module].url}`
      params[deleteModule[this.module].paramObj] = { id: moduleType.id }

      this.$dialog
        .confirm({
          title: 'Delete' + this.module,
          message: 'Are you sure you want to delete this ' + this.title + ' ?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            API.post(url, params).then(({ error }) => {
              if (error) {
                this.$message.error(error.message || 'Error Occured')
              } else {
                this.$dialog.notify('Successfully deleted the' + this.title)
                this.moduleTypeList.splice(index, 1)
                this.$store.commit('GENERIC_DELETE', {
                  type: this.module,
                  matches: index,
                })
              }
            })
          }
        })
    },

    sortType(array, key) {
      // ordering according to sequence or cordinality
      return array.sort(function(a, b) {
        let x = a[key]
        let y = b[key]
        return x < y ? -1 : x > y ? 1 : 0
      })
    },
  },
}
</script>
<style>
.priorityMediumtag {
  font-size: 16px;
  padding-right: 10px;
  position: relative;
  top: 1px;
}
.priority-check-icon {
  color: #939299;
  font-weight: 600;
  padding-top: 10px;
  align-items: center;
  margin-left: 47px;
  font-size: 16px;
}
.priority-more-popover {
  padding: 0;
}
.priority-more-popover ul {
  list-style-type: none;
  padding-left: 0;
  margin-top: 4px;
  margin-bottom: 0px;
}
.priority-more-popover li {
  padding-top: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(151, 151, 151, 0.1);
  padding-left: 24px;
  padding-right: 20px;
  letter-spacing: 0.5px;
  font-size: 13px;
  color: #67666a;
  font-weight: 500;
}
.priority-more-popover li:hover {
  background: #f1f8fa;
  color: #39b2c2;
  cursor: pointer;
}
</style>
