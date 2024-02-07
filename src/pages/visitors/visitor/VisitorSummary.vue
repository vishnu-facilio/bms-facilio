<template>
  <div class="d-flex flex-col flex-auto visitor-summary">
    <template v-if="summaryLoading">
      <spinner :show="summaryLoading"></spinner>
    </template>
    <template v-else>
      <div class="visitor-container">
        <div class="fc-dark-grey-txt18">
          <div class="fc-id">#{{ summaryData.id }}</div>
          <div class="fc-black-17 bold inline-flex">
            {{ summaryData.name }}
          </div>
        </div>
        <div class="fc__layout__align inventory-overview-btn-group">
          <el-dropdown
            v-if="
              $hasPermission(`visitor:UPDATE`) ||
                $hasPermission(`visitor:DELETE`)
            "
            @command="handleCommand"
          >
            <span class="el-dropdown-link">
              <img src="~assets/menu.svg" height="18" width="18" />
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                v-if="$hasPermission(`visitor:UPDATE`)"
                command="edit"
                >{{ $t('common._common.edit') }}</el-dropdown-item
              >
              <el-dropdown-item
                v-if="$hasPermission(`visitor:DELETE`)"
                command="delete"
                >{{ $t('common._common.delete') }}</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <div class="flex-auto" v-if="!$validation.isEmpty(summaryData)">
        <div class="fc__asset__main__scroll">
          <div class="fc__white__bg__asset">
            <el-row>
              <el-col :span="6">
                <div>
                  <img
                    v-if="!$validation.isEmpty(summaryData.avatarUrl)"
                    :src="summaryData.avatarUrl"
                    width="130"
                    height="130"
                    class="store-item-upload-img bR8"
                  />
                  <img
                    v-else
                    src="~statics/icons/user.png"
                    width="130"
                    height="130"
                    class="store-item-upload-img"
                  />
                </div>
              </el-col>
              <el-col :span="16" class="mB30">
                <el-col :span="12" class="mB30">
                  <div class="fc-blue-label">
                    {{ systemField['phone'] }}
                  </div>
                  <div class="label-txt-black pT5">
                    {{ summaryData.phone ? summaryData.phone : '---' }}
                  </div>
                </el-col>
                <el-col :span="12" class="mB30">
                  <div class="fc-blue-label">
                    {{ systemField['email'] }}
                  </div>
                  <div class="label-txt-black pT5">
                    {{ summaryData.email ? summaryData.email : '---' }}
                  </div>
                </el-col>
              </el-col>
              <el-col :span="16">
                <el-col :span="12" class="mB30">
                  <div class="fc-blue-label">Last Visited Space</div>
                  <div class="label-txt-black pT5">
                    <space-avatar
                      size="md"
                      v-if="summaryData.lastVisitedSpace"
                      :name="'true'"
                      :space="summaryData.lastVisitedSpace"
                    ></space-avatar>
                    <div class="label-txt-black pT5" v-else>---</div>
                  </div>
                </el-col>
                <el-col :span="12" class="mB30">
                  <div class="fc-blue-label">Last Visited Host</div>
                  <div class="label-txt-black pT5">
                    <user-avatar
                      v-if="summaryData.lastVisitedPeople"
                      size="md"
                      :user="summaryData.lastVisitedPeople"
                    ></user-avatar>
                    <div v-else>---</div>
                  </div>
                </el-col>
              </el-col>
            </el-row>
          </div>
          <div class="mT20 mB10 inventory-section-header">
            Summary
          </div>
          <div class="fc__white__bg__info mB20">
            <el-row class="border-bottom6 pB20">
              <el-col :span="12">
                <el-col :span="12">
                  <div class="fc-blue-label">Last Visited Time</div>
                </el-col>
                <el-col :span="12">{{
                  summaryData.lastVisitedTime &&
                  summaryData.lastVisitedTime !== -1
                    ? this.$options.filters.formatDate(
                        summaryData.lastVisitedTime
                      )
                    : '---'
                }}</el-col>
              </el-col>
              <el-col :span="12">
                <el-col :span="12">
                  <div class="fc-blue-label">Last Visit Duration</div>
                </el-col>
                <el-col :span="12">{{
                  convertMilliSecondsToTimeHHMM(summaryData.lastVisitDuration)
                }}</el-col>
              </el-col>
            </el-row>
            <el-row class="pT20">
              <el-col :span="12">
                <el-col :span="12">
                  <div class="fc-blue-label">Type</div>
                </el-col>
                <el-col :span="12">
                  {{ $getProperty(summaryData, 'visitorType.name', '---') }}
                </el-col>
              </el-col>
            </el-row>
          </div>
          <div class=" fc__white__bg__p0">
            <VisitorSecondaryDetails
              :visitorDetails="summaryData"
            ></VisitorSecondaryDetails>
          </div>

          <div class="fc__white__bg__p0 mT20 p0 pr-table">
            <div class="widget-header">Location</div>
            <div class="fc__white__bg__info b0">
              <el-row class="border-bottom6 pB20">
                <el-col :span="12">
                  <el-col :span="12">
                    <div class="fc-blue-label">
                      {{ $t('setup.setup_profile.city') }}
                    </div>
                  </el-col>
                  <el-col :span="12">{{
                    $getProperty(summaryData, 'location.street', '---')
                  }}</el-col>
                </el-col>
                <el-col :span="12">
                  <el-col :span="12">
                    <div class="fc-blue-label">
                      {{ $t('setup.setup_profile.street') }}
                    </div>
                  </el-col>
                  <el-col :span="12">{{
                    $getProperty(summaryData, 'location.city', '---')
                  }}</el-col>
                </el-col>
              </el-row>
              <el-row class="border-bottom6 pB20 pT20">
                <el-col :span="12">
                  <el-col :span="12">
                    <div class="fc-blue-label">
                      {{ $t('setup.setup_profile.state') }}
                    </div>
                  </el-col>
                  <el-col :span="12">{{
                    $getProperty(summaryData, 'location.state', '---')
                  }}</el-col>
                </el-col>
                <el-col :span="12">
                  <el-col :span="12">
                    <div class="fc-blue-label">
                      {{ $t('setup.setup_profile.zipcode') }}
                    </div>
                  </el-col>
                  <el-col :span="12">{{
                    $getProperty(summaryData, 'location.zip', '---')
                  }}</el-col>
                </el-col>
              </el-row>
              <el-row class="border-bottom6 pB20 pT20">
                <el-col :span="12">
                  <el-col :span="12">
                    <div class="fc-blue-label">
                      {{ $t('setup.setup_profile.country') }}
                    </div>
                  </el-col>
                  <el-col :span="12">{{
                    $getProperty(summaryData, 'location.country', '---')
                  }}</el-col>
                </el-col>
                <el-col :span="12"></el-col>
              </el-row>
            </div>
          </div>
          <div
            class="fc__white__bg__info__custom mT20"
            v-if="!$validation.isEmpty(customFields)"
          >
            <template v-for="(d, index) in customFields">
              <el-row
                class="border-bottom6 pB20 pT20"
                v-if="index % 2 === 0"
                :key="index"
              >
                <el-col :span="12">
                  <el-col :span="12">
                    <div class="fc-blue-label">
                      {{ customFields[index].displayName }}
                    </div>
                  </el-col>
                  <el-col
                    :span="12"
                    v-if="customFields[index].field.dataTypeEnum === 'DATE'"
                    >{{
                      summaryData.data &&
                      summaryData.data[customFields[index].name] > 0
                        ? $options.filters.formatDate(
                            summaryData.data[customFields[index].name],
                            true
                          )
                        : '---'
                    }}</el-col
                  >
                  <el-col
                    :span="12"
                    v-else-if="
                      customFields[index].field.dataTypeEnum === 'DATE_TIME'
                    "
                    >{{
                      (summaryData.data &&
                        summaryData.data[customFields[index].name]) > 0
                        ? $options.filters.formatDate(
                            summaryData.data[customFields[index].name]
                          )
                        : '---'
                    }}</el-col
                  >
                  <el-col
                    :span="12"
                    v-else-if="
                      [2, 3].includes(customFields[index].field.dataType)
                    "
                    >{{
                      summaryData.data &&
                      summaryData.data[customFields[index].name] &&
                      summaryData.data[customFields[index].name] !== -1
                        ? summaryData.data[customFields[index].name]
                        : '---'
                    }}</el-col
                  >
                  <el-col
                    :span="12"
                    v-else-if="
                      customFields[index].field.dataTypeEnum === 'ENUM'
                    "
                    >{{
                      summaryData.data &&
                      summaryData.data[customFields[index + 1].name]
                        ? customFields[index + 1].field.enumMap[
                            parseInt(
                              summaryData.data[customFields[index + 1].name]
                            )
                          ]
                        : '---'
                    }}</el-col
                  >
                  <el-col :span="12" v-else>{{
                    summaryData.data &&
                    summaryData.data[customFields[index].name]
                      ? summaryData.data[customFields[index].name]
                      : '---'
                  }}</el-col>
                </el-col>
                <el-col v-if="customFields.length > index + 1" :span="12">
                  <el-col :span="12">
                    <div class="fc-blue-label">
                      {{ customFields[index + 1].displayName }}
                    </div>
                  </el-col>
                  <el-col
                    :span="12"
                    v-if="customFields[index + 1].field.dataTypeEnum === 'DATE'"
                    >{{
                      summaryData.data &&
                      summaryData.data[customFields[index + 1].name] > 0
                        ? $options.filters.formatDate(
                            summaryData.data[customFields[index + 1].name],
                            true
                          )
                        : '---'
                    }}</el-col
                  >
                  <el-col
                    :span="12"
                    v-else-if="
                      customFields[index + 1].field.dataTypeEnum === 'DATE_TIME'
                    "
                    >{{
                      (summaryData.data &&
                        summaryData.data[customFields[index + 1].name]) > 0
                        ? $options.filters.formatDate(
                            summaryData.data[customFields[index + 1].name]
                          )
                        : '---'
                    }}</el-col
                  >
                  <el-col
                    :span="12"
                    v-else-if="
                      [2, 3].includes(customFields[index + 1].field.dataType)
                    "
                    >{{
                      summaryData.data &&
                      summaryData.data[customFields[index + 1].name] &&
                      summaryData.data[customFields[index + 1].name] !== -1
                        ? summaryData.data[customFields[index + 1].name]
                        : '---'
                    }}</el-col
                  >
                  <el-col
                    :span="12"
                    v-else-if="
                      customFields[index + 1].field.dataTypeEnum === 'ENUM'
                    "
                    >{{
                      summaryData.data &&
                      summaryData.data[customFields[index + 1].name]
                        ? customFields[index + 1].field.enumMap[
                            parseInt(
                              summaryData.data[customFields[index + 1].name]
                            )
                          ]
                        : '---'
                    }}</el-col
                  >
                  <el-col :span="12" v-else>{{
                    summaryData.data &&
                    summaryData.data[customFields[index + 1].name]
                      ? summaryData.data[customFields[index + 1].name]
                      : '---'
                  }}</el-col>
                </el-col>
              </el-row>
            </template>
          </div>
          <!-- notes & attachements -->
          <div class="fc__white__bg__p0 mT20" :key="summaryData.id">
            <el-tabs v-model="activeName">
              <el-tab-pane label="Notes" name="first">
                <div
                  v-if="activeName === 'first' && summaryData && summaryData.id"
                  class="inventory-comments"
                >
                  <notes
                    module="visitornotes"
                    parentModule="visitor"
                    class="flex-grow overflow-scroll visitor-comments"
                    placeholder="Add a note"
                    btnLabel="Add Note"
                    :record="summaryData"
                    :notify="false"
                  ></notes>
                </div>
              </el-tab-pane>
              <el-tab-pane label="Attachments" name="second">
                <div v-if="activeName === 'second'">
                  <attachments
                    module="visitorattachments"
                    parentModule="visitor"
                    v-if="summaryData"
                    :record="summaryData"
                  ></attachments>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
          <div class="fc__white__bg__p0 mT20 width100">
            <div class="widget-header">
              {{ $t('common.header.visitors_logs') }}
            </div>
            <el-table
              height="400"
              class="inventory-inner-table clearboth visitor-table"
              :data="visitsList"
              v-loading="visitsLoading"
              :empty-text="$t('home.visitor.visitor_log_no_data')"
              :default-sort="{
                prop: 'name',
                order: 'descending',
              }"
            >
              <template slot="empty">
                <img
                  class="mT50"
                  src="~statics/noData-light.png"
                  width="100"
                  height="100"
                />
                <div class="mT10 label-txt-black f14 op6">
                  {{ $t('home.visitor.visitor_log_no_data') }}
                </div>
              </template>
              <el-table-column prop="host.name" sortable label="HOST">
                <template v-slot="data">
                  <user-avatar size="md" :user="data.row.host"></user-avatar>
                </template>
              </el-table-column>
              <el-table-column
                prop="checkInTime"
                sortable
                :formatter="getDateTime"
                label="CHECK-IN TIME"
              ></el-table-column>
              <el-table-column
                prop="checkOutTime"
                sortable
                :formatter="getDateTime"
                label="CHECK-OUT TIME"
              ></el-table-column>
              <el-table-column
                prop="purposeOfVisitEnum"
                sortable
                label="PURPOSE OF VISIT"
              ></el-table-column>
              <el-table-column>
                <template v-slot="data">
                  <div class="text-center">
                    <TransitionButtons
                      :record="data.row"
                      moduleName="visitorlog"
                      :stateFlowList="visitsStateFlow"
                      @transitionSuccess="loadVisitorLog"
                    ></TransitionButtons>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
      <VisitorForm
        v-if="editFormVisibility"
        :visibility.sync="editFormVisibility"
        moduleName="visitor"
        :editId="summaryData.id"
        @saved="loadSummary"
      ></VisitorForm>
    </template>
  </div>
</template>
<script>
import TransitionButtons from '@/stateflow/TransitionButtonsForList'
import UserAvatar from '@/avatar/User'
import SpaceAvatar from '@/avatar/Space'
import moment from 'moment-timezone'
import Attachments from '@/relatedlist/Attachments'
import VisitorForm from './VisitorForm'
import { API } from '@facilio/api'
import Notes from '@/widget/Notes'
import { isEmpty } from '@facilio/utils/validation'
import VisitorSecondaryDetails from 'src/pages/visitors/visitor/VisitorSecondaryDetails.vue'

export default {
  props: ['id'],
  components: {
    TransitionButtons,
    SpaceAvatar,
    UserAvatar,
    Attachments,
    Notes,
    VisitorForm,
    VisitorSecondaryDetails,
  },
  data() {
    return {
      summaryLoading: false,
      activeName: 'first',
      visitsLoading: false,
      visitsList: null,
      visitsStateFlow: null,
      summaryData: null,
      editFormVisibility: false,
      systemField: {},
      customFields: [],
    }
  },
  created() {
    this.loadForm()
  },
  watch: {
    id: {
      handler(newValue) {
        if (newValue) {
          this.loadSummary()
          this.loadVisitorLog()
        }
      },
      immediate: true,
    },
  },
  methods: {
    async loadSummary() {
      let { id } = this
      this.summaryLoading = true
      let { visitor, error } = await API.fetchRecord('visitor', {
        id: id,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.summaryData = visitor || []
      }
      this.summaryLoading = false
    },
    loadForm() {
      API.get('/v2/forms', { moduleName: 'visitor' }).then(
        ({ error, data }) => {
          if (!error) {
            this.formFields(data.forms[0])
          }
        }
      )
    },
    formFields(data) {
      let { fields } = data || {}
      if (fields) {
        fields.forEach(f => {
          let { field } = f
          let { default: defaultField, name, displayName } = field || {}

          if (defaultField !== true) {
            this.customFields.push(f)
          } else {
            this.systemField[name] = displayName ? displayName : name
          }
        })
      }
    },
    handleCommand(command) {
      if (command === 'delete') {
        this.delete()
      } else if (command === 'edit') {
        this.editFormVisibility = true
      }
    },
    delete() {
      this.$dialog
        .confirm({
          title: this.$t('home.visitor.delete_visitor'),
          message: this.$t('home.visitor.delete_visitor_confirmation'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            API.deleteRecord('visitor', this.summaryData.id).then(
              ({ error }) => {
                if (error) {
                  this.$message.error(error.messsage || 'Error Occured')
                } else {
                  this.$message.success(
                    this.$t('home.visitor.visitor_delete_success')
                  )
                  this.$emit('redirectToList')
                }
              }
            )
          }
        })
        .catch(() => {})
    },
    convertMilliSecondsToTimeHHMM(val) {
      val = val * 1000
      if (val && val > 0) {
        return moment()
          .startOf('day')
          .milliseconds(val)
          .format('HH:mm')
      } else {
        return '00:00'
      }
    },
    async loadVisitorLog() {
      this.visitsLoading = true

      let { id } = this
      let filters = {
        visitor: {
          operatorId: 36,
          value: [`${id}`],
        },
      }
      let params = {
        viewname: 'all',
        page: 1,
        perPage: 50,
        filters: JSON.stringify(filters),
      }
      let { list, meta, error } = await API.fetchAll('visitorlog', params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.visitsList = list || []
        this.visitsStateFlow = this.$getProperty(meta, 'stateflows', {})
      }
      this.visitsLoading = false
    },
    getDateTime(val, obj) {
      let value = val[obj.property]
      return !value || value === -1
        ? '---'
        : this.$options.filters.formatDate(value)
    },
    lastVisitDuration(summaryData) {
      let hour = this.$getProperty(summaryData, 'lastVisitDuration', null)

      if (isEmpty(hour)) {
        return '---'
      } else {
        return `${hour} h`
      }
    },
  },
}
</script>
<style lang="scss">
.visitor-summary {
  .widget-header {
    padding: 18px 30px;
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 1px;
    color: #385571;
    text-transform: uppercase;
    border-bottom: 1px solid #e3e3e3;
  }
  .visitor-container {
    height: 90px;
    background-color: #ffffff;
    align-items: center !important;
    border-bottom: 1px solid #ebf0f5;
    display: flex;
    flex-direction: row;
    align-content: center;
    justify-content: space-between;
    padding: 24px 20px 20px;
    width: auto;
    align-items: center !important;
  }
  .visitor-table {
    padding: 0 18px;
    .el-table__cell {
      padding: 12px;
    }
  }
  .fc__white__bg__p0 {
    .asset-details-widget {
      .container {
        .field {
          .field-label {
            font-size: 11px;
            font-weight: 500;
            letter-spacing: 0.7px;
            color: #8ca1ad;
            text-transform: uppercase;
          }
        }
      }
    }
  }
  .inventory-comments {
    .visitor-comments {
      .fc-comments {
        padding: 0;
        #commentBoxPar {
          width: 100% !important;
        }
      }
    }
  }
}
</style>
