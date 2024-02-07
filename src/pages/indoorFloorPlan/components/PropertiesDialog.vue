<template>
  <div class="properties-section fc-properties-section-editor">
    <div class="new-header-container">
      <div class="new-header-text relative">
        <div class="fc-setup-modal-title">Properties</div>
        <div class="fc-setup-modal-close f18 pointer" @click="handleClose">
          <i class="el-icon-close fwBold"></i>
        </div>
      </div>
    </div>
    <div class="new-body-modal infp-left-section height100 pL20 pR20 mT10">
      <el-form ref="form" :model="properties" label-width="120px">
        <!-- <el-row class="mB10">
          <el-col :span="24">
            <el-form-item prop="name" class="mB10 m0">
              <p class="fc-input-label-txt pB5">Name</p>
              <el-input
                @change="update()"
                v-model="properties.label"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row> -->
        <template
          v-if="
            properties.markerId &&
              String(properties.markerId).indexOf('desk') > -1
          "
        >
          <el-row class="mB10 mT20">
            <el-col :span="24">
              <el-form-item prop="siteId" class="mB10 m0">
                <p class="fc-input-label-txt pB5">Desk Type</p>
                <el-select
                  v-model="properties.deskType"
                  filterable
                  @change="update()"
                  placeholder="Select Desk type"
                  class="width100 el-input-textbox-full-border"
                >
                  <template v-for="(type, index) in deskTypes">
                    <el-option
                      :key="index"
                      :label="type.name"
                      :value="type.value"
                    ></el-option>
                  </template>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <!--
          <el-row class="mB10 mT20">
            <el-col :span="24">
              <el-form-item prop="floorId" class="mB10 m0">
                <p class="fc-input-label-txt pB5">Department</p>
                <el-select
                  filterable
                  clearable
                  @change="update()"
                  v-model="properties.departmentId"
                  placeholder="Select Department"
                  class="width100 el-input-textbox-full-border"
                >
                  <el-option
                    v-for="(department, index) in formatedDepartmentList"
                    :key="index"
                    :label="department.name"
                    :value="department.id"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row> -->
        </template>

        <el-row
          class="mB10 mT10"
          v-if="
            properties.markerId &&
              String(properties.markerId).indexOf('desk') === -1 &&
              markerModule
          "
        >
          <el-col :span="24">
            <el-form-item prop="floorId" class="mB10 m0">
              <p class="fc-input-label-txt pB5">
                {{ `${markerModule.displayName}` }}
              </p>
              <!-- <el-select
                filterable
                clearable
                @change="update()"
                v-model="properties.recordId"
                placeholder="Select Record"
                class="width100 el-input-textbox-full-border"
              >
                <el-option
                  v-for="(record, index) in records"
                  :key="index"
                  :label="record.name"
                  :value="record.id"
                >
                </el-option>
              </el-select> -->
              <FLookupFieldWrapper
                v-model="properties.recordId"
                :label="markerModule.displayName"
                @recordSelected="value => setName(value)"
                :field="{
                  lookupModule: { name: markerModule.name },
                  multiple: false,
                }"
              ></FLookupFieldWrapper>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row
          class="mB10 mT10"
          v-else-if="properties.markerModuleId && markerModuleObj && !loading"
        >
          <el-col :span="24">
            <el-form-item prop="floorId" class="mB10 m0">
              <p class="fc-input-label-txt pB5">
                {{ `${markerModuleObj.displayName}` }}
              </p>
              <!-- <el-select
                filterable
                clearable
                @change="update()"
                v-model="properties.recordId"
                placeholder="Select Record"
                class="width100 el-input-textbox-full-border"
              >
                <el-option
                  v-for="(record, index) in records"
                  :key="index"
                  :label="record.name"
                  :value="record.id"
                >
                </el-option>
              </el-select> -->
              <FLookupFieldWrapper
                v-model="properties.recordId"
                :label="markerModuleObj.displayName"
                @recordSelected="value => setName(value)"
                :field="{
                  lookupModule: { name: markerModuleObj.name },
                  multiple: false,
                }"
              ></FLookupFieldWrapper>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row class="mT30">
          <el-col :span="24">
            <el-button @click="deleteMarker()" class="fc-floorplan-delete"
              >DELETE</el-button
            >
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import { isEqual } from 'lodash'

export default {
  props: ['properties', 'visible', 'departments'],
  components: { FLookupFieldWrapper },
  data() {
    return {
      element: {
        id: null,
      },
      deskTypes: [
        { name: 'Assigned', value: 1 },
        { name: 'Hotel', value: 2 },
        { name: 'Hot', value: 3 },
      ],
      records: [],
      modules: [],
      loading: false,
    }
  },
  mounted() {
    this.loadModules()
  },
  watch: {
    properties: {
      handler: function(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          this.rerender()
        }
      },
      deep: true,
    },
  },
  computed: {
    formatedDepartmentList() {
      if (this.departments) {
        return [...this.departments, { name: 'None', id: -1 }]
      }
      return [{ name: 'none', id: -1 }]
    },
    markerModule() {
      let { modules } = this
      if (modules.length) {
        let module = this.modules.find(
          rt => rt.moduleId === Number(this.properties.recordModuleId)
        )
        return module
      }
      return null
    },
    markerModuleObj() {
      let { modules } = this
      if (modules.length) {
        let module = this.modules.find(
          rt => rt.moduleId === Number(this.properties.markerModuleId)
        )
        return module
      }
      return null
    },
    markerTypModule() {
      let { modules } = this
      if (modules.length) {
        let module = this.modules.find(
          rt => rt.moduleId === Number(this.properties.recordModuleId)
        )
        return module
      }
      return null
    },
  },
  methods: {
    rerender() {
      this.loading = true
      this.$nextTick(() => {
        this.loading = false
      })
    },
    setName(value) {
      if (value.label) {
        this.$set(this.properties, 'label', value.label)
        this.update()
      }
    },
    async loadModules() {
      let url = '/v3/modules/list/all'
      let { error, data } = await API.get(url)

      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        Object.values(data).forEach(value => {
          this.modules.push(...value)
        })
        this.loadRecords()
      }
    },
    async loadRecords() {
      if (this.markerModule && this.markerModule.name) {
        let params = {
          moduleName: this.markerModule.name,
          id: Number(this.properties.recordModuleId),
        }
        let { data, error } = await API.post(`v3/modules/data/list`, params)

        if (data) {
          this.records = data[this.markerModule.name]
        } else if (error) {
          this.$message.error(error.message)
        }
      }
    },
    deleteMarker() {
      this.$emit('delete', this.properties)
    },
    handleClose() {
      this.$emit('update:visible', false)
      this.$emit('close')
    },
    update() {
      this.$emit('update', this.properties)
    },
  },
}
</script>
