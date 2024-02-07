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
          class="mL10 fc-btn-ico-lg pointer"
          style="padding-top: 5px; padding-bottom: 5px;"
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
                v-if="$hasPermission(`visitor:UPDATE`) && summaryData.totalInvites > 0"
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
          <div class="widget-header">Summary</div>
          <div class="fc__white__bg__info p0">
            <el-row class="border-bottom6 p10">
              <el-col :span="12">
                <el-col :span="12" class="p10">
                  <div class="fc-blue-label">Description</div>
                </el-col>
                <el-col :span="12" class="p10">
                  {{ $getProperty(summaryData, 'description', '---') }}
                </el-col>
              </el-col>
            </el-row>
            <el-row class="border-bottom6 p10 ">
              <el-col :span="12">
                <el-col :span="12" class="p10">
                  <div class="fc-blue-label">Total Invites</div>
                </el-col>
                <el-col :span="12" class="p10">
                  {{ $getProperty(summaryData, 'totalInvites', '---') }}
                </el-col>
              </el-col>
              <el-col :span="12">
                <el-col :span="12" class="p10">
                  <div class="fc-blue-label">No. of Checked-In Invites</div>
                </el-col>
                <el-col :span="12" class="p10">{{
                  $getProperty(summaryData, 'checkedInCount', '---')
                }}</el-col>
              </el-col>
            </el-row>
            <el-row class="border-bottom6 p10">
              <el-col :span="12">
                <el-col :span="12" class="p10">
                  <div class="fc-blue-label">Created By</div>
                </el-col>
                <el-col :span="12">
                  <div class="label-txt-black pT5">
                    <user-avatar
                      v-if="summaryData.sysCreatedBy"
                      size="md"
                      :user="summaryData.sysCreatedBy"
                    ></user-avatar>
                    <div v-else>---</div>
                  </div>
                </el-col>
              </el-col>
              <el-col :span="12">
                <el-col :span="12" class="p10">
                  <div class="fc-blue-label">Created Time</div>
                </el-col>
                <el-col :span="12" class="p10">
                  {{
                    summaryData.sysCreatedTime &&
                    summaryData.sysCreatedTime !== -1
                      ? this.$options.filters.formatDate(
                          summaryData.sysCreatedTime
                        )
                      : '---'
                  }}
                </el-col>
              </el-col>
            </el-row>
            <el-row class="p10">
              <el-col :span="12">
                <el-col :span="12" class="p10">
                  <div class="fc-blue-label">Modified By</div>
                </el-col>
                <el-col :span="12">
                  <div class="label-txt-black pT5">
                    <user-avatar
                      v-if="summaryData.sysModifiedBy"
                      size="md"
                      :user="summaryData.sysModifiedBy"
                    ></user-avatar>
                    <div v-else>---</div>
                  </div>
                </el-col>
              </el-col>
              <el-col :span="12">
                <el-col :span="12" class="p10">
                  <div class="fc-blue-label">Modified Time</div>
                </el-col>
                <el-col :span="12" class="p10">
                  {{
                    summaryData.sysModifiedTime &&
                    summaryData.sysModifiedTime !== -1
                      ? this.$options.filters.formatDate(
                          summaryData.sysModifiedTime
                        )
                      : '---'
                  }}
                </el-col>
              </el-col>
            </el-row>
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
          <div class="widget-header">All Invites</div>
          <div class="fc__white__bg__p0">
            <el-table
              height="400"
              class="m0 width100 inventory-inner-table clearboth visitor-table"
              :data="groupInvitesList"
              v-loading="visitsLoading"
              :empty-text="$t('home.visitor.all_invites_deleted')"
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
                  {{ $t('home.visitor.all_invites_deleted') }}
                </div>
              </template>
              <el-table-column
                class="padding-left:10px;"
                prop="id"
                sortable
                label="ID"
              >
              </el-table-column>
              <el-table-column
                prop="visitorName"
                sortable
                label="NAME"
              >
              <template v-slot="data">
                <div
                class="fw5 ellipsis width200px mL10 main-field-column"
                 @click="goToOverview(data.row.id)"
                 >
                 {{ data.row.visitorName }}

                </div>
              </template></el-table-column>
              <el-table-column
                prop="visitorPhone"
                sortable
                label="PHONE"
              ></el-table-column>
              <el-table-column
                prop="visitorEmail"
                sortable
                label="EMAIL"
              ></el-table-column>
              <el-table-column prop="host.name" label="HOST">
                <template v-slot="data">
                  <user-avatar size="md" :user="data.row.host"></user-avatar>
                </template>
              </el-table-column>
              <el-table-column
                prop="expectedCheckInTime"
                :formatter="getDateTime"
                label="EXPECTED CHECK-IN TIME"
              ></el-table-column>
              <el-table-column
                prop="expectedCheckOutTime"
                :formatter="getDateTime"
                label="EXPECTED CHECK-OUT TIME"
              ></el-table-column>

              <el-table-column width="100">
                <template v-slot="data">
                  <div class="text-center flex-middle">
                    <i
                      v-if="$hasPermission(`${moduleName}:UPDATE`)"
                      class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                      data-arrow="true"
                      ::title="$t('common._common.edit')"
                      v-tippy
                      @click="editSingleRecord([data.row], 'invitevisitor')"
                    ></i>
                    <i
                      v-if="$hasPermission(`${moduleName}:DELETE`) && summaryData.totalInvites > 1"
                      class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                      data-arrow="true"
                      :title="$t('common._common.delete')"
                      v-tippy
                      @click="deleteRecord([data.row.id], 'invitevisitor')"
                    ></i>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </template>
    <VisitsSummary v-if="visitorinvite"
    :id="inviteid"
    :groupinviteInviteSummary="true"
    :moduleName="'invitevisitor'"
    @closeDialog="closeDialog()"
    />

  </div>


</template>
<script>
import UserAvatar from '@/avatar/User'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import VisitsSummary from 'src/pages/visitors/visits/VisitsSummary.vue'

export default {
  props: ['id'],
  components: {
    UserAvatar,
    VisitsSummary
  },
  data() {
    return {
      summaryLoading: false,
      activeName: 'first',
      visitsLoading: false,
      groupInvitesList: null,
      visitsStateFlow: null,
      summaryData: null,
      editFormVisibility: false,
      systemField: {},
      customFields: [],
      moduleName: 'groupinvite',
      inviteid:null,
      visitorinvite:false,
    }
  },
  watch: {
    id: {
      handler(newValue) {
        if (newValue) {
          this.loadSummary()
        }
      },
      immediate: true,
    },
  },
  methods: {
    async loadSummary() {
      let { id } = this
      this.summaryLoading = true
      let { groupinvite, error } = await API.fetchRecord('groupinvite', {
        id: id,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.summaryData = groupinvite || []
        this.groupInvitesList = groupinvite.groupChildInvites
      }
      this.summaryLoading = false
    },
    formFields(data) {
      let { fields } = data || {}

      fields.forEach(f => {
        let { field } = f
        let { default: defaultField, name, displayName } = field || {}

        if (defaultField !== true) {
          this.customFields.push(f)
        } else {
          this.systemField[name] = displayName ? displayName : name
        }
      })
    },
    handleCommand(command) {
      if (command === 'delete') {
        this.deleteRecord(this.summaryData.id, this.moduleName)
      } else if (command === 'edit') {
        this.editRecord(this.summaryData)
      }
    },
    editRecord(data) {
      let { id, formId, groupChildInvites } = data
      let visitorTypeId = groupChildInvites[0].visitorType.id || null
      let query = {
        formId: formId,
        formMode: 'bulk',
        visitorTypeId: visitorTypeId,
      }

      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        if (name) {
          this.$router.push({
            name,
            params: { id },
            query,
          })
        }
      } else {
        this.$router.push({
          name: 'group-invites-edit',
          params: { id },
          query,
        })
      }
    },
    deleteRecord(id, moduleName) {
      this.$dialog
        .confirm({
          title:
          moduleName === 'invitevisitor'
              ? this.$t('home.visitor.delete_visitor_invite')
              : this.$t('home.visitor.delete_group_invite'),
          message:
            moduleName === 'invitevisitor'
              ? this.$t('home.visitor.delete_visitor_invite_confirmation')
              : this.$t('home.visitor.delete_group_invite_confirmation'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            API.deleteRecord(moduleName, id).then(({ error }) => {
              if (error) {
                this.$message.error(error.messsage || 'Error Occured')
              } else {
                this.$message.success(
                  moduleName === 'invitevisitor'
                    ? this.$t('home.visitor.visitor_invite_delete_success')
                    : this.$t('home.visitor.group_invite_delete_success')                )
                    this.loadSummary()
              }
            })
          }
        })
    },
    editSingleRecord(data) {
      let { id, visitorType, formId } = data[0]
      let query = {
        visitorTypeId: visitorType.id,
        formId: formId,
      }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('invitevisitor', pageTypes.EDIT) || {}

        if (name) {
          this.$router.push({
            name,
            params: { id },
            query,
          })
        }
      } else {
        this.$router.push({
          name: 'invites-edit',
          params: { id },
          query,
        })
      }
    },
    backToList() {
      let { viewname, moduleName, $route } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

        if (name) {
          this.$router.push({ name, params: { viewname }, query: $route.query })
        }
      } else {
        this.$router.push({
          path: `/app/vi/groupinvite/${viewname}`,
          query: $route.query,
        })
      }
    },
    getDateTime(val, obj) {
      let value = val[obj.property]
      return !value || value === -1
        ? '---'
        : this.$options.filters.formatDate(value)
    },

    goToOverview(inviteid) {
      this.inviteid=inviteid
      this.visitorinvite=true


    },
    closeDialog(){
      this.inviteid=null
      this.visitorinvite=false
    }
  },
}
</script>
<style lang="scss">
.visitor-summary {
  .widget-header {
    padding: 18px 5px;
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 1px;
    color: #385571;
    text-transform: uppercase;
  }
  .widget-header:first-child {
    padding: 0px 5px 18px;
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 1px;
    color: #385571;
    text-transform: uppercase;
  }
  .visitor-container {
    height: 90px;
    box-shadow: 2px 5px 4px 0 rgba(19, 15, 39, 0.02);
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
    border-bottom: none;
  }
  .visitor-table {
    .el-table__cell:first-child {
      padding-left: 30px;
    }
    .el-table__cell {
      padding: 10px 9px;
    }
  }
}
</style>
