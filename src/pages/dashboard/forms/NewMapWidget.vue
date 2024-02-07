<template>
  <f-form
    ref="newDashboardForm"
    type="dialog50"
    title="New Map Widget"
    :model="model"
    @save="save"
  >
    <template v-slot:form="fscope">
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-input
            v-model="fscope.formModel.dashboardDesc"
            class="form-item"
            float-label="Description"
            type="textarea"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-select
            v-model="fscope.formModel.moduleId"
            class="form-item"
            float-label="Module"
            :options="supportedModules | options('displayName', 'moduleId')"
          />
        </el-col>
      </el-row>
    </template>
  </f-form>
</template>

<script>
import { QInput, QSelect } from 'quasar'
import FForm from '@/FForm'

export default {
  components: {
    QInput,
    QSelect,
    FForm,
  },
  data() {
    return {
      model: {
        dashboardName: null,
        dashboardDesc: null,
        moduleId: null,
      },
      supportedModules: [],
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
    this.loadSupportedModules()
  },
  methods: {
    loadSupportedModules() {
      let self = this
      let url = '/dashboard/supportedmodules'
      this.$http.get(url).then(function(response) {
        if (response.data.modules) {
          self.supportedModules = response.data.modules
        }
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
      let formdata = {
        dashboard: {
          dashboardName: formModel.dashboardName,
          moduleId: formModel.moduleId,
        },
      }
      self.$http.post('/dashboard/add', formdata).then(function(response) {
        self.$message({
          message: 'Dashboard created successfully!',
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
