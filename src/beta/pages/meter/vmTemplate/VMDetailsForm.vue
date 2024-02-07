<template>
  <div>
    <div class="vm-section-header">
      {{ $t('asset.virtual_meters.meters_info') }}
    </div>
    <el-form
      ref="vm-details-form"
      :model="vmDetailsObj"
      :rules="validationRules"
      label-width="150px"
      label-position="left"
      class="p50 pT10 pB30"
    >
      <div class="section-container flex-container">
        <div class="vm-form-item">
          <el-form-item
            :label="`${this.$t('asset.virtual_meters.meter_name')}`"
            prop="meterName"
          >
            <el-input
              v-model="vmDetailsObj.meterName"
              class="fc-input-full-border2 width60"
              :disabled="!isNew"
              :placeholder="
                `${this.$t('asset.virtual_meters.enter_meter_name')}`
              "
              :autofocus="true"
            ></el-input>
          </el-form-item>
        </div>
        <div class="vm-form-item">
          <el-form-item
            :label="`${this.$t('asset.virtual_meters.meter_description')}`"
            prop="meterDescription"
          >
            <el-input
              v-model="vmDetailsObj.meterDescription"
              type="textarea"
              class="mT3 fc-input-full-border-textarea width60"
              :disabled="!isNew"
              :autofocus="true"
              :min-rows="2"
              :autosize="{ minRows: 3, maxRows: 4 }"
              :placeholder="`${this.$t('asset.virtual_meters.description')}`"
            ></el-input>
          </el-form-item>
        </div>
      </div>
    </el-form>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  components: {},
  props: ['isNew', 'vmInfo'],
  data() {
    return {
      vmDetailsObj: {
        meterName: '',
        meterDescription: '',
      },
      validationRules: {
        meterName: [
          {
            required: true,
            message: 'Please enter meter name',
            trigger: 'blur',
          },
        ],
      },
    }
  },
  created() {
    this.vmDetailsObj = this.vmInfo
  },
  watch: {
    vmDetailsObj: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          this.$emit('updateVmDetails', newVal)
        }
      },
      deep: true,
    },
  },
  methods: {
    validate() {
      return new Promise(resolve => {
        this.$refs['vm-details-form'].validate(valid => {
          if (valid) {
            resolve(true)
          } else {
            resolve(false)
          }
        })
      })
    },
  },
}
</script>
<style lang="scss">
.vm-form-item {
  flex: 1 1 100%;
  width: 100%;
  .el-form-item {
    display: flex;
    flex-direction: column;
  }
  .el-form-item__content {
    margin-left: 0px !important;
  }
  .el-form-item__label {
    width: 160px !important;
  }
}
</style>
