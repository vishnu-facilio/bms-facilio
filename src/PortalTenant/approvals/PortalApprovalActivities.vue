<template>
  <PageLayout>
    <template #views>
      <ApprovalViews
        :moduleName="moduleName"
        :isActivityView="true"
        :showEditIcon="false"
        :pathPrefix="pathPrefix"
        :canShowViewsSidePanel.sync="canShowViewsSidePanel"
      ></ApprovalViews>
    </template>
    <template #header>
      <pagination
        v-if="totalCount"
        :total="totalCount"
        :perPage="perPage"
        class="pL15 fc-black-small-txt-12"
      ></pagination>
    </template>
    <div class="width100">
      <FTags :key="`ftags-list-${moduleName}`" :hideSaveView="true"></FTags>
    </div>

    <div :class="['f-list-view height100vh', { m10: loading }]">
      <div v-if="loading" class="full-layout-white text-center height100vh">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div
        v-if="$validation.isEmpty(activities)"
        class="flex-middle width100 m10 justify-center shadow-none white-bg-block flex-direction-column approval-activity-container"
      >
        <InlineSvg
          src="svgs/emptystate/history"
          iconClass="icon icon-xxxxlg mR10"
        ></InlineSvg>
        <div class="nowo-label text-center pT10">
          {{ $t('asset.history.no_history_available') }}
        </div>
      </div>
      <div
        class="fc-list-view fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser p10 pB100 approval-activities"
        v-else
      >
        <el-table
          :data="activities"
          ref="tableList"
          class="width100"
          height="auto"
          :fit="true"
          row-class-name="activity-row no-hover"
        >
          <el-table-column fixed prop label="Time" width="200px">
            <template slot-scope="data">
              <div>{{ data.row.ttime | formatDate }}</div>
            </template>
          </el-table-column>
          <el-table-column fixed prop label="User" width="220px">
            <template slot-scope="data">
              <avatar
                size="sm"
                :user="{ name: getUserName(data.row.doneBy.ouid) }"
              ></avatar>
              {{ getUserName(data.row.doneBy.ouid) }}
            </template>
          </el-table-column>
          <el-table-column prop label="Action" min-width="320px">
            <template slot-scope="data">
              <div>
                <span v-html="getActivityMessage(data.row).message"></span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop label="Approval Process" width="230px">
            <template slot-scope="data">
              {{ getRuleName(data.row) }}
            </template>
          </el-table-column>
          <el-table-column
            prop
            :label="getPrimaryFieldName(moduleName)"
            min-width="350px"
          >
            <template slot-scope="data">
              <div class="position-relative">
                <div class="ellipsis textoverflow-ellipsis">
                  {{ getModulePrimaryField(data.row) }}
                </div>
                <a
                  @click="goToModuleSummary({ id: data.row.parentId })"
                  class="summary-link pointer"
                >
                  {{ $t('common.header.open') }}
                  {{ $t('common._common.summary') }}
                  <inline-svg
                    src="svgs/new-tab"
                    iconClass="icon vertical-middle icon-sm mL3"
                  ></inline-svg>
                </a>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </PageLayout>
</template>
<script>
import ApprovalActivities from 'pages/approvals/ApprovalActivities'
import PageLayout from '../components/PageLayout'
import ApprovalViews from './PortalApprovalViews'
export default {
  extends: ApprovalActivities,
  components: { PageLayout, ApprovalViews },
}
</script>
