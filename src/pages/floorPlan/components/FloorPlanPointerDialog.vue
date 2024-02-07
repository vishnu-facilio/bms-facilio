<template>
  <el-dialog
    title="Properties"
    :visible.sync="visibility"
    v-if="visibility"
    :append-to-body="true"
    :before-close="closeDialog"
    custom-class="floorplan-property-dialog graphic-object-dialog fc-dialog-center-container setup-dialog60 graphic-object-build-dialog"
  >
    <div v-if="currentObject">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="fc-input-label-txt mb5">Select Employee</div>
          <div>
            <el-select
              v-model="currentObject.floorplan.employeeId"
              class="fc-input-full-border-select2"
              :filterable="true"
            >
              <el-option
                v-for="(employee, index) in employeeList"
                :key="index"
                :label="employee.name"
                :value="employee.id"
              ></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeDialog()"
        >CANCEL</el-button
      >
      <el-button
        class="modal-btn-save"
        type="primary"
        @click="updateObjectProps"
        >SAVE</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import colors from 'charts/helpers/colors'
import { mapState } from 'vuex'
import { API } from '@facilio/api'
export default {
  props: ['visibility', 'currentObject', 'variables', 'spaceList'],
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
  },
  created() {
    this.getEmployeeList()
  },
  data() {
    return {
      employeeList: [],
    }
  },
  mounted() {},
  methods: {
    getEmployeeList() {
      let url = `v2/module/data/list?moduleName=custom_deskmanager&page=1&perPage=500&viewName=all`
      this.$http.get(url).then(response => {
        if (
          response.data &&
          response.data.result &&
          response.data.result.moduleDatas
        ) {
          this.employeeList = response.data.result.moduleDatas
        }
      })
    },
    getPredefinedColors() {
      return colors.readingcardColors
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    updateObjectProps() {
      this.$emit('updateObject', this.currentObject)
    },
  },
}
</script>
