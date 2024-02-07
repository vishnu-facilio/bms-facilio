/* eslint-disable no-undef */ /* eslint-disable no-console */
<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    title="SELECT THE FLOOR"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog25 setup-dialog show-right-dialog"
    :before-close="closeDialog"
    :append-to-body="true"
    style="z-index: 999999"
  >
    <div v-if="employee && data">
      <div class="emp-header">
        <img class="fc-custom-logo" src="~assets/office-chair.svg" />
        <div
          style="padding-top: 10px;
    font-size: 15px;
    padding-left: 5px;"
        >
          {{ data.singleline_1 }}
        </div>
        <!-- <div class="emp-building">
      <i class="el-icon-office-building"></i>
      <div class="f14 pT10 pL5">
      {{data.space && data.space.name ? data.space.name : '---'}}
      </div>
    </div> -->
        <div class="emp-close pull-right f16 pointer" @click="closeDialog">
          <i class="el-icon-close f20" style="font-weight: 600;"></i>
        </div>
      </div>
    </div>
    <div class="emp-content">
      <div class="fc-input-label-txt header">Details</div>
      <div class="mb5">{{ employee.name }}</div>
      <div>{{ data.singleline }}</div>
    </div>
    <div class="emp-content">
      <div class="fc-input-label-txt header">Department</div>
      <div class="mb5">{{ getFieldData('picklist') }}</div>
    </div>
    <div class="emp-content">
      <div class="fc-input-label-txt header">Space</div>
      <div class="mb5">
        {{ data.space && data.space.name ? data.space.name : '---' }}
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapActions } from 'vuex'
import { API } from '@facilio/api'

export default {
  props: ['visibility', 'element', 'floorPlan', 'mappedSpaceId'],
  data() {
    return {
      employee: null,
      fields: [],
    }
  },
  computed: {
    employeeId() {
      let { element } = this
      let { target } = element
      if (target && target.floorplan && target.floorplan.employeeId) {
        return target.floorplan.employeeId
      }
      return null
    },
    data() {
      let { data } = this.employee
      if (data) {
        return data
      }
      return null
    },
  },
  created() {
    this.getFields()
  },
  mounted() {
    this.getEmployeeDetails()
  },
  methods: {
    getFields() {
      let moduleName = 'custom_deskmanager'
      let url = `/v2/filter/advanced/fields/${moduleName}`
      this.$http.get(url).then(response => {
        if (
          response.data &&
          response.data.result &&
          response.data.result.fields
        ) {
          this.fields = response.data.result.fields
        }
      })
    },
    getFieldData(fieldName) {
      if (fieldName) {
        let fieldobj = this.fields.find(rt => rt.name === fieldName)
        if (fieldName === 'picklist') {
          let { options } = fieldobj
          let object = options.find(
            rt => rt.value === this.data['picklist'].toString()
          )
          return object.label
        }
      }
    },
    getEmployeeDetails() {
      let url = `v2/module/data/${this.employeeId}?moduleName=custom_deskmanager`
      this.$http.get(url).then(response => {
        if (
          response.data &&
          response.data.result &&
          response.data.result.moduleData
        ) {
          this.employee = response.data.result.moduleData
        }
      })
    },
    closeDialog() {
      this.$emit('update:visibility', false)
      this.$emit('close', false)
    },
  },
}
</script>

<style>
.fp-sm-container {
  height: calc(100vh - 60px);
  overflow: auto;
}
.emp-header {
  display: inline-flex;
  background: #a9aaca;
  padding: 10px;
  width: 100%;
  color: #fff;
}
.emp-building {
  color: #fff;
  font-size: 35px;
  padding-left: 30px;
  display: inline-flex;
}
.emp-close {
  right: 10px;
  position: absolute;
  font-weight: 800;
  cursor: pointer;
}
.emp-content .header {
  font-weight: 500;
}
.emp-content {
  padding: 20px;
}
</style>
