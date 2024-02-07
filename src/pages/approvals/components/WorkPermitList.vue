<template>
  <div>
    <div
      v-if="$validation.isEmpty(list)"
      class="full-layout-white height100 m10"
    >
      <div class="row container nowor">
        <div class="justify-center nowo fc-h100vh pT50">
          <div>
            <inline-svg
              src="svgs/emptystate/workorder"
              class="mB10"
              iconClass="icon text-center icon-xxxxlg height-auto"
            ></inline-svg>
            <div class="nowo-label">
              No Work Permits Found
            </div>
            <div class="nowo-sublabel" style="width: 24%;">
              There are no work permits that require approval.
            </div>
          </div>
        </div>
      </div>
    </div>

    <div
      v-else
      class="fc-list-view fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser p10 pB100"
    >
      <div
        class="view-column-chooser"
        style="top: 10px;"
        @click="showColumnSettings = true"
      >
        <img
          src="~assets/column-setting.svg"
          style="text-align: center; position: absolute; top: 35%;right: 29%;"
        />
      </div>

      <el-table
        :data="list"
        ref="approvalList"
        class="width100"
        height="auto"
        :fit="true"
        @row-click="goToSummary"
      >
        <el-table-column fixed prop label="ID" min-width="90">
          <template slot-scope="data">
            <div class="fc-id">{{ '#' + data.row.localId }}</div>
          </template>
        </el-table-column>

        <el-table-column fixed prop label="PERMIT NAME" width="300">
          <template slot-scope="data">
            <div
              v-tippy
              small
              :title="$getProperty(data, 'row.name', '---')"
              class="flex-middle"
            >
              <div class="mL10">
                <div class="fw5 ellipsis textoverflow-ellipsis width200px">
                  {{ $getProperty(data, 'row.name', '---') }}
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          v-for="(field, index) in viewColumns.filter(
            field => !isFixedColumn(field.name) || field.parentField
          )"
          :key="index"
          :prop="field.name"
          :label="field.displayName"
          :align="
            $getProperty(field, 'field.dataTypeEnum') === 'DECIMAL'
              ? 'right'
              : 'left'
          "
          min-width="200"
        >
          <template v-slot="data">
            <div>
              <div
                class="table-subheading"
                :class="{
                  'text-right':
                    $getProperty(field, 'field.dataTypeEnum') === 'DECIMAL',
                }"
              >
                {{ getColumnDisplayValue(field, data.row) || '---' }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          fixed="right"
          prop
          label
          column-key="actions"
          width="250"
        >
          <template v-slot="data">
            <div class="approval-list-action-container" @click.stop="() => {}">
              <ApprovalButtons
                size="small"
                moduleName="workpermit"
                :record="data.row"
                :availableTransitions="getApprovalStates(data.row)"
                @onSuccess="onTransitionSuccess"
                @onFailure="onTransitionError"
                class="approval-list-actions"
                @click.stop="() => {}"
              ></ApprovalButtons>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <column-customization
        :visible.sync="showColumnSettings"
        moduleName="workpermit"
        :columnConfig="columnConfig"
        :viewName="currentView"
      ></column-customization>
    </div>
  </div>
</template>
<script>
import ApprovalButtons from '@/approval/ApprovalButtons'
import ViewMixinHelper from '@/mixins/ViewMixin'
import ColumnCustomization from '@/ColumnCustomization'

export default {
  props: [
    'list',
    'refreshAction',
    'setSortConfig',
    'setTransitionConfig',
    'getApprovalStates',
    'openSummary',
    'currentView',
  ],
  mixins: [ViewMixinHelper],
  components: { ApprovalButtons, ColumnCustomization },
  created() {
    this.init()
  },
  data() {
    return {
      showColumnSettings: false,
      columnConfig: {
        fixedColumns: ['name', 'localId'],
        availableColumns: [],
        showLookupColumns: false,
      },
      isActionInProgress: false,
    }
  },
  computed: {
    tableRef() {
      return this.$refs['approvalList']
    },
  },
  watch: {
    list() {
      this.init()
    },
  },
  methods: {
    init() {
      this.updateSortConfig()
    },
    updateSortConfig() {
      let sortConfig = {
        orderBy: {
          label: this.$t('maintenance.wr_list.datecreated'),
          value: 'createdTime',
        },
        orderType: 'desc',
      }

      this.setSortConfig(sortConfig, null, null)
    },
    onTransitionSuccess() {
      this.refreshAction()
    },
    onTransitionError() {
      this.$message.error('Could not update Work Permit')
    },
    goToSummary(row, col) {
      if (['selection', 'actions'].includes(col.columnKey)) {
        return
      }
      this.openSummary(row)
    },
  },
}
</script>
