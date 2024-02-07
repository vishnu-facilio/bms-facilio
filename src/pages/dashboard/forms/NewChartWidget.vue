<template>
  <f-form
    ref="newDashboardForm"
    type="dialog50"
    title="New Chart Widget"
    :model="model"
    @save="save"
  >
    <template v-slot:form="fscope">
      <el-row :gutter="50" align="middle">
        <el-col :span="12">
          <q-input
            v-model="fscope.formModel.widgetName"
            class="form-item"
            float-label="Widget name"
          />
        </el-col>
        <el-col :span="12" style="padding-top:30px;">
          <el-select v-model="fscope.formModel.reportId" placeholder="Select">
            <el-option-group
              v-for="group in reportList"
              :key="group.label"
              :label="group.label"
            >
              <el-option
                v-for="child in group.children"
                :key="child.widget.dataOptions.reportId"
                :label="child.label"
                :value="child.widget.dataOptions.reportId"
              >
              </el-option>
            </el-option-group>
          </el-select>
        </el-col>
      </el-row>
      <el-row
        :gutter="50"
        align="middle"
        style="padding-top: 30px;padding-bottom: 20px;"
      >
        <el-col :span="12">
          <div style="color: #39b2c2;">{{ $t('panel.grids.width') }}</div>
          <el-select v-model="fscope.formModel.layoutWidth" placeholder="width">
            <el-option
              v-for="child in gridList"
              :key="child"
              :label="child"
              :value="child"
            >
            </el-option>
          </el-select>
        </el-col>
        <el-col :span="12">
          <div style="color: #39b2c2;">{{ $t('panel.grids.height') }}</div>
          <el-select
            v-model="fscope.formModel.layoutHeight"
            placeholder="width"
          >
            <el-option
              v-for="child in gridHeight"
              :key="child"
              :label="child"
              :value="child"
            >
            </el-option>
          </el-select>
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-input
            v-model="fscope.formModel.dataRefreshIntervel"
            class="form-item"
            float-label="Refresh Interval (in seconds)"
          />
        </el-col>
      </el-row>
    </template>
  </f-form>
</template>

<script>
import { QInput } from 'quasar'
import FForm from '@/FForm'
import ReportHelper from 'pages/report/mixins/ReportHelper'

export default {
  mixins: [ReportHelper],
  props: ['dashboardId', 'container', 'moduleName'],
  components: {
    QInput,
    FForm,
  },
  data() {
    return {
      model: {
        widgetName: null,
        type: 1,
        dashboardId: 1,
        layoutWidth: 1,
        layoutHeight: 1,
        layoutPosition: 1,
        dataRefreshIntervel: 100,
        reportId: '',
        headerText: '',
        headerSubText: '{today}',
      },
      reportList: [],
      gridList: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
      gridHeight: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
    }
  },
  // watch: {
  //   visibility: function (newVal) {
  //     this.visibilityState = newVal
  //     if (this.visibilityState) {
  //       this.$refs.newDashboardForm.open()
  //     }
  //     else {
  //       this.$refs.newDashboardForm.close()
  //     }
  //   }
  // },
  mounted() {
    this.loadReportList()
  },
  methods: {
    loadReportList() {
      let self = this
      self.$http
        .get(
          '/report/workorder/getAllWorkOrderReports?moduleName=' +
            (this.moduleName || 'workorder')
        )
        .then(function(response) {
          self.reportList = response.data.allWorkOrderJsonReports
        })
    },
    open() {
      this.$refs.newDashboardForm.open()
    },
    close() {
      this.$refs.newDashboardForm.close()
    },
    save() {
      let self = this
      let formModel = self.$refs.newDashboardForm.getFormModel()
      formModel.dashboardId = this.dashboardId
      formModel.headerText = formModel.widgetName
      let formdata = {
        widgetChartContext: formModel,
      }
      self.$http
        .post('/dashboard/addWidget', formdata)
        .then(function(response) {
          self.$message({
            message: 'Chart widget created successfully!',
            type: 'success',
          })
          self.close()
        })
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
