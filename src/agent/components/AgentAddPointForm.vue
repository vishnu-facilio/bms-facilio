<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
      :before-close="closeDialog"
      style="z-index: 999999"
    >
      <el-form
        :model="newPoints"
        :label-position="'top'"
        ref="agentPointForm"
        label-width="120px"
        class="fc-form"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              New Point
            </div>
          </div>
        </div>
        <div class="new-body-modal">
          <div class="setup-input-block pB10">
            <el-row>
              <el-col :span="12">
                <p class="fc-input-label-txt">
                  Agent
                </p>
                <el-form-item>
                  <el-select
                    prop="agents"
                    v-model="selectedAgentId"
                    filterable
                    placeholder="Select Agent"
                    class="width100"
                    @change="updateSelectedType()"
                  >
                    <el-option
                      v-for="(agent, index) in this.agentFilter"
                      :key="index"
                      :label="agent.displayName || agent.name"
                      :value="agent.id"
                      no-data-text="No Agent available"
                      clearable
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <div class="setup-input-block pB10">
            <el-row>
              <el-col :span="12">
                <p class="fc-input-label-txt">
                  Type
                </p>
                <el-form-item>
                  <el-select
                    prop="selectedType"
                    v-model="selectedType"
                    filterable
                    :required="true"
                    placeholder="Select Controller Type"
                    class="width100"
                    @change="changeControllerType()"
                  >
                    <el-option
                      key="Modbus Ip"
                      label="Modbus Ip"
                      value="4"
                    ></el-option>
                    <el-option
                      key="Modbus Rtu"
                      label="Modbus Rtu"
                      value="5"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <div
            v-if="lookupModuleName != null && selectedType != null"
            class="setup-input-block pB10"
          >
            <el-row>
              <el-col :span="12">
                <p class="fc-input-label-txt">Controller</p>
                <FLookupFieldWrapper
                  v-model="newPoints.controllerId"
                  :key="lookupModuleName"
                  :field="{
                    lookupModule: {
                      name: lookupModuleName,
                    },
                    multiple: false,
                  }"
                  :filterConstruction="constructAgentFilter"
                  :hideLookupIcon="false"
                  :disabled="false"
                ></FLookupFieldWrapper>
              </el-col>
            </el-row>
          </div>
          <div class="setup-input-block pB10">
            <el-row>
              <el-col :span="24">
                <p class="fc-input-label-txt">Name</p>
                <el-form-item prop="name">
                  <el-input
                    v-model="newPoints.name"
                    placeholder="Enter the Name"
                    class="fc-input-txt width100"
                    req
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <div class="setup-input-block pB10">
            <el-row>
              <el-col :span="12">
                <p class="fc-input-label-txt">Register Type</p>
                <el-form-item prop="function">
                  <el-select
                    v-model="newPoints.registerType"
                    filterable
                    :required="true"
                    placeholder="Select Register type"
                    class="width100"
                  >
                    <el-option
                      v-for="(type, index) in registerType"
                      :key="index"
                      :label="type.label"
                      :value="type.value"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <div
            v-if="!$validation.isEmpty(newPoints.registerType)"
            class="setup-input-block pB10"
          >
            <el-row>
              <el-col :span="12">
                <p class="fc-input-label-txt">Modbus DataType</p>
                <el-form-item prop="datatype">
                  <el-select
                    v-model="newPoints.dataType"
                    :required="true"
                    placeholder="Select the modbus"
                    class="width100"
                  >
                    <div
                      v-if="
                        this.newPoints.registerType == 1 ||
                          this.newPoints.registerType == 2
                      "
                    >
                      <el-option
                        key="BINARY"
                        label="BINARY"
                        value="1"
                      ></el-option>
                    </div>
                    <div v-else>
                      <el-option
                        v-for="(type, index) in modbusDataTypes"
                        :key="index"
                        :label="type.label"
                        :value="type.value"
                      ></el-option>
                    </div>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <div class="setup-input-block pB10">
            <el-row>
              <el-col :span="12">
                <p class="fc-input-label-txt">Register Number</p>
                <el-form-item prop="registerNumber">
                  <el-input
                    v-model="newPoints.registerNumber"
                    placeholder="Enter register number"
                    class="fc-input-txt width100"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <div class="setup-input-block pB10">
            <el-row>
              <el-col :span="12">
                <p class="fc-input-label-txt">Interval</p>
                <el-form-item prop="function">
                  <el-select
                    v-model="newPoints.interval"
                    filterable
                    :required="true"
                    placeholder="Select point interval"
                    class="width100"
                  >
                    <el-option
                      v-for="(type, index) in intervalTiming"
                      :key="index"
                      :label="type.label"
                      :value="type.value"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <!-- <div class="setup-input-block pB10">
            <el-row>
              <el-col :span="24">
                <p class="fc-input-label-txt">Controller Id</p>
                <el-form-item prop="function">
                  <el-input
                    v-model="newPoints.controllerId"
                    placeholder="Enter Point Name"
                    class="fc-input-txt width100"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div> -->
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel"
            >CANCEL</el-button
          >
          <el-button
            type="primary"
            @click="submitForm('agentForm')"
            :loading="saving"
            class="modal-btn-save"
            >{{ saving ? 'Saving...' : 'SAVE' }}</el-button
          >
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import Constants from 'util/constant'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
export default {
  data() {
    return {
      saving: false,
      agents: [],
      selectedType: null,
      selectedAgentId: null,
      newPoints: {
        name: '',
        controllerType: '',
        registerType: '',
        registerNumber: '',
        modbusDataType: '',
        controllerId: '',
        interval: 0,
      },
      registerType: [
        {
          value: 1,
          label: 'Coils Status',
        },
        {
          value: 2,
          label: 'Inputs Status',
        },
        {
          value: 3,
          label: 'Holding Register',
        },
        {
          value: 4,
          label: 'Input Register',
        },
      ],
      modbusDataTypes: [
        {
          value: 2,
          label: 'TWO_BYTE_INT_UNSIGNED',
        },
        {
          value: 3,
          label: 'TWO_BYTE_INT_SIGNED',
        },
        {
          value: 4,
          label: 'FOUR_BYTE_INT_UNSIGNED',
        },
        {
          value: 5,
          label: 'FOUR_BYTE_INT_SIGNED',
        },
        {
          value: 6,
          label: 'FOUR_BYTE_INT_UNSIGNED_SWAPPED',
        },
        {
          value: 7,
          label: 'FOUR_BYTE_INT_SIGNED_SWAPPED',
        },
        {
          value: 10,
          label: 'EIGHT_BYTE_INT_UNSIGNED',
        },
        {
          value: 11,
          label: 'EIGHT_BYTE_INT_SIGNED',
        },
        {
          value: 12,
          label: 'EIGHT_BYTE_INT_UNSIGNED_SWAPPED',
        },
        {
          value: 13,
          label: 'EIGHT_BYTE_INT_SIGNED_SWAPPED',
        },
        {
          value: 14,
          label: 'EIGHT_BYTE_FLOAT',
        },
        {
          value: 15,
          label: 'EIGHT_BYTE_FLOAT_SWAPPED',
        },
        {
          value: 16,
          label: 'TWO_BYTE_BCD',
        },
        {
          value: 17,
          label: 'FOUR_BYTE_BCD',
        },
        {
          value: 20,
          label: 'FOUR_BYTE_BCD_SWAPPED',
        },
        {
          value: 22,
          label: 'TWO_BYTE_INT_UNSIGNED_SWAPPED',
        },
        {
          value: 23,
          label: 'TWO_BYTE_INT_SIGNED_SWAPPED',
        },
        {
          value: 24,
          label: 'FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED',
        },
        {
          value: 25,
          label: 'FOUR_BYTE_INT_SIGNED_SWAPPED_SWAPPED',
        },
      ],
      intervalTiming: [
        {
          value: 0,
          label: 'Agent interval',
        },
        {
          value: 2,
          label: '2 Mins',
        },
        {
          value: 5,
          label: '5 Mins',
        },
        {
          value: 10,
          label: '10 Mins',
        },
        {
          value: 15,
          label: '15 Mins',
        },
      ],
      lookupModuleName: null,
    }
  },
  mounted() {
    this.loadAgents()
  },
  components: {
    FLookupFieldWrapper,
  },
  props: ['visibility', 'isNew', 'model'],
  computed: {
    ...mapState({
      sites: state => state.site,
    }),
    agentFilter() {
      return this.agents.filter(agent => agent.agentType === 1)
    },
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    cancel() {
      this.$emit('canceled')
    },
    resetForm() {
      this.$refs['agentPointForm'].resetFields()
    },
    modbusData() {},
    submitForm() {
      this.saving = true
      let pointDataPayload = {
        name: this.newPoints.name,
        controllerType: parseInt(this.selectedType),
        agentId: parseInt(this.selectedAgentId),
        modbusDataType: parseInt(this.newPoints.dataType),
        registerType: parseInt(this.newPoints.registerType),
        registerNumber: parseInt(this.newPoints.registerNumber),
        controllerId: parseInt(this.newPoints.controllerId),
        interval: parseInt(this.newPoints.interval),
      }
      if (this.isNew) {
        this.$http
          .post('/v2/modbus/addPoint', pointDataPayload)
          .then(response => {
            if (response.status === 200) {
              this.$message('Modbus Point will be added in a while.')
              this.saving = false
              this.resetForm()
              this.$emit('saved')
              this.$emit('update:visibility', false)
            }
          })
      }
    },
    loadAgents() {
      this.loading = true
      this.$http
        .get('/v2/agent/getFilter')
        .then(response => {
          this.loading = false
          this.agents =
            response.data.result && response.data.result.data
              ? response.data.result.data
              : []
        })
        .catch(() => {
          this.agents = []
          this.loading = false
        })
    },
    changeControllerType() {
      let { newPoints } = this
      newPoints.controllerId = null
      this.lookupModuleName = Constants.ControllerModuleName[this.selectedType]
    },
    updateSelectedType() {
      this.selectedType = null
    },
    constructAgentFilter() {
      return { agentId: { operatorId: 9, value: [`${this.selectedAgentId}`] } }
    },
  },
}
</script>
