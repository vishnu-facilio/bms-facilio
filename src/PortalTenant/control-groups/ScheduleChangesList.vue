<template>
  <div class="height100">
    <PageLayout :moduleName="moduleName">
      <template slot="title">
        {{ moduleDisplayName }}
      </template>
      <template slot="header">
        <AdvancedSearchWrapper
          :key="`ftags-list-${moduleName}`"
          :filters="filters"
          :moduleName="moduleName"
          :moduleDisplayName="moduleDisplayName"
          :hideSaveView="true"
        ></AdvancedSearchWrapper>
        <CreateButton @click="openChangeSchedule()">
          Change Schedule
        </CreateButton>
      </template>
      <template slot="header-2">
        <template v-if="listCount">
          <pagination :total="listCount" :perPage="50"></pagination>
        </template>
      </template>

      <div
        class="fc-list-view p10 pT0 mT10 height100vh fc-table-td-height fc-table-viewchooser pB100 fc-v1-portal-table fc-workpermit-table fc-list-table-container"
        v-if="openModuleId === -1"
      >
        <div v-if="showLoading" class="flex-middle fc-empty-white">
          <spinner :show="showLoading" size="80"></spinner>
        </div>
        <div
          v-if="$validation.isEmpty(moduleRecordList) && !showLoading"
          class="height100vh flex-middle justify-content-center flex-direction-column fc-empty-white"
        >
          <inline-svg
            src="svgs/emptystate/workorder"
            iconClass="icon text-center icon-xxxxlg height-auto"
          ></inline-svg>
          <div class="line-height20 nowo-label">
            No
            {{
              moduleDisplayName ? moduleDisplayName.toLowerCase() : moduleName
            }}
            available
          </div>
        </div>

        <div v-if="!showLoading && !$validation.isEmpty(moduleRecordList)">
          <div
            class="view-column-chooser"
            @click="showColumnSettings = true"
            v-show="false"
          >
            <img
              src="~assets/column-setting.svg"
              style="text-align: center; position: absolute; top: 35%;right: 29%;"
            />
          </div>
          <el-table
            :data="exceptionList"
            ref="tableList"
            class="width100"
            height="auto"
            :fit="true"
          >
            <el-table-column fixed prop label="ID" min-width="120">
              <template v-slot="data">
                <div class="fc-id">
                  {{ '#' + data.row[idField] }}
                </div>
              </template>
            </el-table-column>
            <el-table-column
              fixed
              prop
              :label="nameColumn.displayName || 'NAME'"
              min-width="200"
            >
              <template v-slot="data">
                <div
                  v-tippy
                  small
                  :title="
                    nameColumn
                      ? getColumnDisplayValue(nameColumn, data.row) || ''
                      : data.row.name
                  "
                  class="flex-middle"
                >
                  <div class="fw5 ellipsis textoverflow-ellipsis width200px">
                    {{
                      nameColumn
                        ? getColumnDisplayValue(nameColumn, data.row) || ''
                        : data.row.name
                    }}
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="Extend/Reduce" min-width="200">
              <template v-slot="exception">
                {{
                  !exception.row.offSchedule
                    ? 'Extended Hours'
                    : 'Reduced Hours'
                }}
              </template>
            </el-table-column>

            <el-table-column label="Frequency" min-width="200">
              <template v-slot="exception">
                {{ exception.row.isDay ? 'Weekly' : `Specific Date` }}
              </template>
            </el-table-column>
            <el-table-column label="From" min-width="200">
              <template v-slot="exception">
                {{ getHour(exception.row.startTime, exception.row.isDay) }}
              </template>
            </el-table-column>
            <el-table-column label="To" min-width="200">
              <template v-slot="exception">
                {{ getHour(exception.row.endTime, exception.row.isDay) }}
              </template>
            </el-table-column>
            <template v-for="(field, index) in viewColumns">
              <el-table-column
                :align="checkDecimalType(field) ? 'right' : 'left'"
                :key="index"
                :prop="field.name"
                :label="field.displayName"
                min-width="200"
                v-if="!isFixedColumn(field.name) || field.parentField"
              >
                <template v-slot="data">
                  <div v-if="!isFixedColumn(field.name) || field.parentField">
                    <div
                      class="table-subheading"
                      :class="{
                        'text-right': checkDecimalType(field),
                      }"
                    >
                      {{ getColumnDisplayValue(field, data.row) || '---' }}
                    </div>
                  </div>
                </template>
              </el-table-column>
            </template>
            <el-table-column
              v-if="canShowDelete"
              width="130"
              class="visibility-visible-actions"
              fixed="right"
            >
              <template v-slot="data">
                <div v-if="canShowActions(data.row)" class="text-center">
                  <i
                    class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                    data-arrow="true"
                    :title="$t('common._common.delete')"
                    v-tippy
                    @click="deleteRecord(data.row.id)"
                  ></i>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </PageLayout>

    <column-customization
      :visible.sync="showColumnSettings"
      :moduleName="moduleName"
      :columnConfig="columnConfig"
      :viewName="currentView"
    ></column-customization>
    <ChangeSchedule
      v-if="showChangeSchedule"
      :closeDialog="closeDialog"
      @onSave="scheduleChange"
      :isTenantGroup="true"
      :tenantId="tenantId"
      moduleName="controlScheduleExceptionTenant"
    />
    <DeleteSchedule
      v-if="showDeleteDialog"
      :closeDialog="closeDialog"
      :recordId="exceptionId"
      moduleName="controlScheduleExceptionTenant"
    />
  </div>
</template>
<script>
import ModuleList from 'PortalTenant/custom-module/ModuleList'
import ChangeSchedule from 'pages/controls/controlgroups/components/ChangeSchedule'
import DeleteSchedule from 'pages/controls/controlgroups/components/DeleteSchedule'
import { isEmpty } from '@facilio/utils/validation'
import helpers from 'src/util/helpers'
import { mapState, mapGetters } from 'vuex'
import { isRecordLocked, isRequestedState } from 'PortalTenant/util'

const { getOrgMoment: moment } = helpers

export default {
  extends: ModuleList,
  components: { ChangeSchedule, DeleteSchedule },
  data() {
    return {
      currTenantId: null,
      showChangeSchedule: false,
      exceptionId: null,
      showDeleteDialog: false,
    }
  },
  computed: {
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    tenantId() {
      let { moduleRecordList } = this
      if (!isEmpty(moduleRecordList[0])) {
        let { tenant } = moduleRecordList[0] || {}
        let { id } = tenant || {}
        return id
      } else {
        return null
      }
    },
    exceptionList() {
      let { moduleRecordList } = this
      return moduleRecordList.map(exception => {
        let { startSchedule, endSchedule } = exception
        if (!isEmpty(startSchedule) && !isEmpty(endSchedule)) {
          startSchedule = JSON.parse(startSchedule)
          endSchedule = JSON.parse(endSchedule)

          let { times: startTimes } = startSchedule
          let { times: endTimes } = endSchedule
          return {
            ...exception,
            startTime: startTimes[0],
            endTime: endTimes[0],
            isDay: true,
          }
        } else {
          let { startTime, endTime } = exception
          return {
            ...exception,
            startTime,
            endTime,
            isDay: false,
          }
        }
      })
    },
    canShowDelete() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('DELETE', currentTab)
    },
  },
  methods: {
    openChangeSchedule() {
      this.showChangeSchedule = true
    },
    closeDialog(isSaved) {
      this.showChangeSchedule = false
      this.showDeleteDialog = false
      if (isSaved) this.loadRecords()
    },
    scheduleChange() {
      this.showChangeSchedule = false
      this.loadRecords()
    },
    getHour(time, isDay) {
      if (isDay) return new moment(time, 'HH:mm').format('hh:mm A')
      else return new moment(time, 'x').format('hh:mm A')
    },
    deleteRecord(id) {
      this.exceptionId = id
      this.showDeleteDialog = true
    },
    canShowActions(record) {
      let { moduleName } = this
      return !isRecordLocked(record, moduleName) && !isRequestedState(record)
    },
  },
}
</script>

<style scoped lang="scss">
.group-field {
  &:hover {
    color: #489edc;
  }
}
</style>
