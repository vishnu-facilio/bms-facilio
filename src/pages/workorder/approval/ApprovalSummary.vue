<template>
  <div>
    <div clasc="row">
      <div class="white-color" v-if="workorder">
        <div>
          <div class="summaryHeader">
            <div class="fL" style="padding-top:12px;">
              <div style="display: -webkit-inline-box;display:inline-box;">
                <div class="q-item-label primary-field summary-header-heading">
                  {{ workorder.subject }}
                </div>
                <span
                  v-if="workorder.urgency > 0"
                  class="fc-tag-urgent mL20"
                  :style="{ background: urgencyColorCode[workorder.urgency] }"
                >
                  {{ $constants.WO_URGENCY[workorder.urgency] }}</span
                >
              </div>

              <div
                style="font-size: 13px;position:relative;clear:both;top: 4px;left: 0px;"
              >
                <span class="fc-id bold">#{{ workorder.serialNumber }}</span>
                <span v-if="workorder.sourceType" class="separator">|</span>
                <template>
                  <span style="color: #8ca1ad; padding-right: 5px;">
                    <i
                      v-if="workorder.sourceType === 2"
                      class="fa fa-envelope-o"
                    ></i>
                    <i
                      v-else-if="workorder.sourceType === 10"
                      class="f14 fa fa-globe"
                    ></i>
                    <i
                      v-else-if="workorder.sourceType === 1"
                      class="f14 el-icon-service approval-el-icon-service"
                    ></i>
                    <i
                      v-else-if="workorder.sourceType === 4"
                      class="f14 el-icon-bell approval-el-icon-bell"
                    ></i>
                    <i
                      v-else-if="workorder.sourceType === 5"
                      class="f14 el-icon-date approval-el-icon-date"
                    ></i>

                    <!-- <img src="~assets/envelope.svg" style="width: 16px;height: 16px;vertical-align: middle;"> -->
                  </span>
                  <span
                    v-if="workorder.requester"
                    style="color: #2ea2b2; font-size: 13px;vertical-align: middle;padding-right: 8px;padding-left: 5px;"
                    >{{ workorder.requester.name }}</span
                  >
                </template>
                <div class="clearboth"></div>
              </div>
            </div>
            <div
              class="fR"
              style="padding: 0 25px;position: relative;top: -4;right: 35px;"
            >
              <approve-reject
                v-if="workorder.moduleState && workorder.moduleState.id > 0"
                :record="workorder"
                moduleName="workorder"
                :transformFn="transformFormData"
                updateUrl="/v2/workorders/update"
                @transitionSuccess="onFormSave"
              ></approve-reject>
            </div>
            <div class="fR close-summary-request">
              <div class="triangle-close">
                <i
                  class="close-summary-icon material-icons pull-right pointer"
                  @click="closeSummary"
                  data-theme="light"
                  data-arrow="true"
                  v-tippy
                  >close</i
                >
              </div>
            </div>
            <div class="clearboth"></div>
          </div>
        </div>

        <div class="overflow-scroll approval-request-details-block">
          <wo-field-details
            :isDisableForApproval="true"
            class="approval-wo-fields-"
            :workorder="workorder"
            :excludeFields="[
              'space',
              'type',
              'tenant',
              'sourceType',
              'createdTime',
              'vendor',
              'parentWO',
              'sourceTypeVal',
            ]"
            :refreshWoDetails="onFormSave"
          ></wo-field-details>
        </div>

        <div class="wo-actual-content">
          <el-tabs
            v-model="activeName"
            class="fc-tab-container approval-tab-container"
          >
            <el-tab-pane label="SUMMARY" name="first">
              <!-- summary tab content -->
              <div class="summaryMainContent">
                <h5 style="font-size:26px;">{{ workorder.subject }}</h5>
                <p class="description">{{ workorder.description }}</p>
              </div>
              <div style="margin-right: 30px;margin-left: 24px;padding: 10px;">
                <div
                  class="row"
                  style="padding-bottom: 10px;border-bottom: 1px solid #f4f4f4;width:100%;"
                >
                  <div class="col-12">
                    <div
                      style="color: #84828c; padding-top: 25px;
               position: relative;"
                    >
                      <div class="col-6" style="width: 21%;float: left;">
                        <span
                          style="font-size: 13px;cursor: pointer;"
                          @click="selectSubSection('comment')"
                          >{{ $t('maintenance.wr_list.comments') }} ({{
                            workorder.noOfNotes >= 0 ? workorder.noOfNotes : 0
                          }})
                          <div
                            v-bind:class="{
                              wosactive: subSection === 'comment',
                            }"
                            style="position: absolute; width: 30px; height: 2px;left: 0;right: 0;"
                          ></div>
                        </span>
                      </div>
                      <div class="col-6" style="width: 40%;float: left;">
                        <span
                          style="padding-right: 15px;font-size: 13px;margin-left:10px; cursor: pointer;"
                          @click="selectSubSection('file')"
                        >
                          {{ $t('maintenance.wr_list.attachments') }} ({{
                            workorder.noOfAttachments >= 0
                              ? workorder.noOfAttachments
                              : 0
                          }})
                          <div
                            v-bind:class="{ wosactive: subSection === 'file' }"
                            style="position: absolute; width: 30px; height: 2px;bottom:left: 0;margin-left: 10px;"
                          ></div>
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
                <attachments
                  v-if="subSection === 'file'"
                  module="ticketattachments"
                  :record="workorder"
                ></attachments>
                <tasks
                  v-if="subSection === 'task'"
                  module="ticket"
                  :record="workorder"
                ></tasks>
                <comments
                  v-if="subSection === 'comment'"
                  module="ticketnotes"
                  parentModule="workorder"
                  :record="workorder"
                ></comments>
                <div class="pT15"></div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="TASK" name="second">
              <tasks module="ticket" :record="workorder"></tasks>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
    <space-asset-chooser
      @associate="associateResource"
      :visibility.sync="chooserVisibility"
      :initialValues="{}"
    ></space-asset-chooser>
  </div>
</template>
<script>
import Attachments from '@/relatedlist/Attachments'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import Tasks from '@/relatedlist/NewTasks'
import Comments from '@/relatedlist/Comments'
import { mapState } from 'vuex'
import ApproveReject from '@/stateflow/ApproveReject'
import WoFieldDetails from '../workorders/v1/WorkOrderFieldDetails'
import transformMixin from 'pages/workorder/workorders/v1/mixins/workorderTransform'
import { isEmpty } from '@facilio/utils/validation'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  mixins: [transformMixin],
  props: ['model'],
  data() {
    return {
      duedate: '',
      confirmRequest: {
        category: null,
        assignedTo: {
          id: '',
        },
        assignmentGroup: {
          id: '',
        },
        space: null,
        priority: null,
        dialogVisible: false,
      },
      assignment: {
        assignedTo: {
          id: '',
        },
        assignmentGroup: {
          id: '',
        },
      },
      summaryCheckbox: true,
      loading: true,
      subSection: 'comment',
      visibility: false,
      spaceAssetDisplayName: '',
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 3600 * 1000 * 24
        },
      },
      chooserVisibility: false,
      selectedResource: {
        name: '',
      },
      actions: {
        approve: {
          loading: false,
        },
        reject: {
          loading: false,
        },
        delete: {
          loading: false,
        },
      },
      activeName: 'first',
      urgencyColorCode: {
        1: '#7fa5ff',
        2: '#f56837',
        3: '#e65244',
      },
      WOUrgency: {
        1: 'Not Urgent',
        2: 'Urgent',
        3: 'Emergency',
      },
    }
  },
  created() {
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadGroups')
  },
  computed: {
    ...mapState({
      users: state => state.users,
      ticketcategory: state => state.ticketCategory,
      ticketpriority: state => state.ticketPriority,
      sites: state => state.site,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
    workorder() {
      return this.$store.getters['workorder/getWorkOrderById'](
        parseInt(this.$route.params.id)
      )
    },
    isLocked() {
      let moduleId = this.$getProperty(this.workorder, 'moduleState.id', null)
      return (
        !isEmpty(moduleId) &&
        !this.$store.getters.isStatusLocked(
          this.workorder.moduleState.id,
          'workorder'
        )
      )
    },
  },
  filters: {
    getUser: function(id) {
      let userObj = this.users.find(user => user.id === id)
      if (userObj) {
        return userObj
      } else {
        return {
          name: 'System',
        }
      }
    },
  },
  watch: {
    workorder: function(newVal) {
      console.log('work order changes')
      let title =
        '[#' + this.workorder.serialNumber + '] ' + this.workorder.subject
      this.setTitle(title)
      this.spaceAssetDisplayName = ''
      if (newVal.resource) {
        this.spaceAssetDisplayName = newVal.resource.name
      }
      this.setDueDate(this.workorder.dueDate)
    },
    'model.resource'(val) {
      console.log('ttttgggg' + JSON.stringify(val))
      if (val) {
        this.spaceAssetDisplayName = val.name
      }
    },
    'assignment.assignedTo.id'() {
      this.updateWorkOrder({
        assignedTo: { id: this.assignment.assignedTo.id },
      })
    },
    'assignment.assignmentGroup.id'() {
      this.updateWorkOrder({
        assignmentGroup: { id: this.assignment.assignmentGroup.id },
      })
    },
  },
  mounted() {
    if (this.workorder) {
      let title =
        '[#' + this.workorder.serialNumber + '] ' + this.workorder.subject
      this.setTitle(title)
      this.spaceAssetDisplayName = ''
      if (this.workorder.resource) {
        this.spaceAssetDisplayName = this.workorder.resource.name
      }
    }
  },
  methods: {
    getSiteName(siteId) {
      if (this.sites) {
        let s = this.sites.find(i => i.id === siteId)
        if (!s) {
          return '---'
        }
        return s.name
      }
      return '---'
    },
    assignWorkOrder(idList, assignedTo, assignmentGroup) {
      console.log('assignment group--->', assignmentGroup)
      let self = this
      self.actions.assign.loading = true
      let assignedStatus = {
        id: this.$store.getters.getTicketStatusByLabel('Assigned', 'workorder')
          .id,
        status: 'Assigned',
      }

      let data = {}
      data.id = idList
      if (assignedTo) {
        console.log('Assignment status', assignedTo)
        data.assignedTo = assignedTo
      }
      if (assignmentGroup) {
        console.log('Assignment status', assignmentGroup)
        data.assignmentGroup = assignmentGroup
      }
      data.status = assignedStatus

      self.$store.dispatch('workorder/updateWorkOrder', data).then(function() {
        self.actions.assign.loading = false
        self.$dialog.notify('Workorder assigned successfully!')
        self.resetSelectAll()
        self.onFormSave()
      })
    },
    setDueDate(duedate) {
      if (duedate === -1) {
        this.duedate = Date.now() - 3600 * 1000 * 24
      } else {
        this.duedate = new Date(duedate)
      }
    },
    updateWorkOrder(field) {
      let self = this
      let updateObj = {
        id: [this.workorder.id],
        fields: field,
      }
      self.$store
        .dispatch('workorder/updateWorkOrder', updateObj)
        .then(function() {
          self.$dialog.notify('Workorder request updated successfully!')
          if (field.assignedTo) {
            self.workorder.assignedTo.id = field.assignedTo.id
          } else if (field.assignmentGroup) {
            self.workorder.assignmentGroup.id = field.assignmentGroup.id
          }
          self.onFormSave()
        })
    },
    updateSite(siteId) {
      this.$dialog
        .confirm({
          title: this.$t('maintenance._workorder.change_site'),
          message:
            'Changing Site will remove Assignees and Space/Assets from this request. Are you sure you want to change the site ?',
          rbDanger: true,
          rbLabel: 'Change',
        })
        .then(value => {
          if (value) {
            this.updateWorkOrder({ siteId })
          }
        })
    },
    closeSummary() {
      let newpath = this.$route.path.substring(
        0,
        this.$route.path.indexOf('/summary/')
      )
      this.$router.replace({ path: newpath })
    },
    selectSubSection(section) {
      this.subSection = section
    },
    showSpaceAssetChooser() {
      this.visibility = true
    },
    associateResource(selectedObj) {
      // console.log('associateResource' + JSON.stringify(selectedObj))
      this.selectedResource = selectedObj
      this.chooserVisibility = false
      let self = this
      let updateObj = {
        id: [this.workorder.id],
        fields: { resource: { id: selectedObj.id } },
      }
      self.$store
        .dispatch('workorder/updateWorkOrder', updateObj)
        .then(function() {
          self.$dialog.notify('Work Request updated successfully!')
          self.spaceAssetDisplayName = selectedObj.name
          self.onFormSave()
        })
    },
    reset() {
      this.confirmRequest = {
        category: null,
        assignedTo: {
          id: '',
        },
        assignmentGroup: {
          id: '',
        },
        space: null,
        priority: null,
        resource: null,
        spaceAssetDisplayName: '',
      }
    },
    onChangeDueDate(pickVal) {
      this.updateWorkOrderDueDate(
        [this.workorder.id],
        this.$helpers.getTimeInOrg(pickVal)
      )
    },
    updateWorkOrderDueDate(idList, date) {
      let updateObj = {
        id: idList,
        fields: {
          dueDate: date,
        },
      }
      this.$store
        .dispatch('workorder/updateWorkOrder', updateObj)
        .then(response => {
          this.$message.success('work request Due Date updated successfully!')
          this.onFormSave()
        })
        .catch(() => {
          this.$message.error('work request Due Date updation failed')
        })
    },
    openAsset(id) {
      if (id) {
        let url = '/app/at/assets/all/' + id + '/overview'
        this.$router.replace({ path: url })
      }
    },
    onFormSave() {
      this.$store.dispatch('workorder/reloadApproval', null, { root: true })
    },
  },
  components: {
    Attachments,
    Tasks,
    Comments,
    SpaceAssetChooser,
    ApproveReject,
    WoFieldDetails,
  },
}
</script>
<style type="text/css">
.wosactive {
  border-bottom: 2px solid #fd4b92;
  color: #25243e;
  padding-bottom: 10px;
  font-weight: 500;
}
.q-if.row.no-wrap.items-center.relative-position.q-select.q-if-focusable.text-primary::before,
.q-if.row.no-wrap.items-center.relative-position.q-select.q-if-focusable.text-primary::after {
  display: none;
}
.summaryHeader {
  height: 80px;
  padding-left: 40px;
  padding-right: 10px;
  padding-bottom: 20px;
  padding-top: 15px;
  background: #fff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}
.summaryMainContent {
  margin-left: 24px;
  margin-right: 35px;
  padding: 0 10px 15px;
  float: left;
}

.summaryMainContent .description {
  font-size: 14px;
  letter-spacing: 0.02em;
  line-height: 20px;
  white-space: pre-line;
  letter-spacing: 0.3px;
  text-align: left;
  color: #666666;
}
.close-summary-request {
  position: absolute;
  right: -2px;
  top: -1px;
}

.wo-actual-content {
  position: relative;
  height: 100vh;
  overflow-y: hidden;
  padding-bottom: 200px;
}
.approval-request-details-block {
  width: 30%;
  max-width: 30%;
  height: 100vh;
  float: right;
  margin-right: 10px;
  margin-top: 10px;
}
.summaryMainContent h5 {
  font-size: 15px !important;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
  font-weight: normal;
  line-height: 20px;
}
.summary-header-heading {
  max-width: 500px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  float: left;
  display: inline-block !important;
  font-size: 20px;
  letter-spacing: 0.6px;
  color: #324056;
  font-size: 17px;
}
.request-details-block .picklist .picklist-downarrow {
  display: none;
}
.request-details-block .picklist:hover .picklist-downarrow {
  display: inline-block;
}
.urgent {
  font-size: 13px;
}
.building-resulttxt .el-input .el-input__inner {
  border-bottom: none;
}
#showdatepicker .el-date-editor {
  width: 0;
}
.approval-msg {
  background-color: rgba(77, 229, 2494, 0.2);
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.4px;
  text-align: center;
  color: #39b2c2;
  text-align: center;
  padding: 12px 10px;
}
.approval-msg img {
  position: relative;
  top: 2px;
}
.selected {
  background-color: #f4fafb !important;
}
.approval-tab-container {
  width: 97%;
  height: 100vh;
  padding-bottom: 230px;
  background: #fff;
  margin-top: 10px;
  margin-left: 10px;
  margin-right: 10px;
  overflow-y: scroll;
  overflow-x: hidden;
  border: 1px solid rgba(3, 3, 3, 0.05);
}
.approval-wo-fields {
  padding-top: 20px;
  background: #fff;
  padding-left: 20px;
  margin-top: 10px;
  padding-right: 20px;
}
.approval-request-details-block .approval-wo-fields-con {
  padding-top: 20px;
  background: #fff;
  padding-left: 20px;
  margin-top: 10px;
  padding-right: 20px;
}
.approval-request-details-block .fc-scrollbar-wrap,
.approval-request-details-block .wo-site-card,
.approval-request-details-block .wo-space-card {
  margin-top: 0 !important;
}
</style>
