<template>
  <f-form
    ref="newDashboardForm"
    type="dialog50"
    title="Add Pre-built Widget"
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
          <el-select
            v-model="fscope.formModel.staticKey"
            placeholder="Select Pre-built Widget"
          >
            <el-option
              v-for="(widget, index) in staticWidgets"
              :key="index"
              :label="widget.label"
              :value="widget.key"
            >
            </el-option>
          </el-select>
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
  props: ['dashboardId', 'container'],
  components: {
    QInput,
    FForm,
  },
  data() {
    return {
      model: {
        widgetName: null,
        type: 0,
        dashboardId: 1,
        layoutWidth: 1,
        layoutHeight: 1,
        layoutPosition: 1,
        dataRefreshIntervel: 100,
        staticKey: '',
        headerText: '',
        headerSubText: '{today}',
      },
      staticWidgets: [
        {
          key: 'workordersummary',
          label: 'Workorder Summary',
          w: 3,
          h: 4,
        },
        {
          key: 'categories',
          label: 'Categories',
          w: 3,
          h: 4,
        },
        {
          key: 'technicians',
          label: 'Technicians',
          w: 3,
          h: 4,
        },
        {
          key: 'closedwotrend',
          label: 'Closed WO Trend',
          w: 3,
          h: 4,
        },
        {
          key: 'mywosummary',
          label: 'My Workorder Summary',
          w: 3,
          h: 4,
        },
        {
          key: 'profilecard',
          label: 'Profile Card',
          w: 3,
          h: 3,
        },
        {
          key: 'energycard',
          label: 'Energy Card',
          w: 3,
          h: 3,
        },
        {
          key: 'energycost',
          label: 'Energy Cost',
          w: 3,
          h: 3,
        },
        {
          key: 'weathercard',
          label: 'Weather Card',
          w: 3,
          h: 3,
        },
        {
          key: 'openalarms',
          label: 'Open Alarms',
          w: 3,
          h: 4,
        },
        {
          key: 'buildingopenalarms',
          label: 'Building Open Alarms',
          w: 3,
          h: 4,
        },
        {
          key: 'mapwidget',
          label: 'Map Widget',
          w: 8,
          h: 4,
        },
      ],
    }
  },
  methods: {
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
      formModel.layoutWidth = this.staticWidgets.find(
        w => w.key === formModel.staticKey
      ).w
      formModel.layoutHeight = this.staticWidgets.find(
        w => w.key === formModel.staticKey
      ).h
      let formdata = {
        widgetStaticContext: formModel,
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
