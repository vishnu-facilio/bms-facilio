<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    :before-close="closeDialog"
    custom-class="fc-dialog-form fc-dialog-right classification-dialog-right"
    style="z-index: 999999"
  >
    <template slot="title">
      <div class="el-dialog__title self-center f16">
        {{ title }}
      </div>
    </template>
    <div v-if="isLoading" class="classification-shimmer-lines mT10">
      <span class="span-name loading-shimmer"></span>
      <span class="lines loading-shimmer"></span>
      <span class="span-name loading-shimmer"></span>
      <span class="rectangle loading-shimmer"></span>
      <span class="span-name loading-shimmer"></span>
      <span class="lines loading-shimmer"></span>
    </div>
    <div v-else class="attributes-dialog">
      <el-form :rules="rules" ref="classification-form" :model="attributeData">
        <el-form-item
          :label="$t('setup.classification.attributes.attr_name')"
          prop="name"
        >
          <el-input
            v-model="attributeData.name"
            :placeholder="$t('setup.classification.attributes.enter_attr_name')"
            class="fc-input-full-border-select2 width100"
          >
          </el-input
        ></el-form-item>
        <el-form-item
          :label="$t('setup.classification.description')"
          prop="description"
        >
          <el-input
            v-model="attributeData.description"
            type="textarea"
            :min-rows="1"
            :autosize="{ minRows: 2, maxRows: 4 }"
            :placeholder="$t('setup.classification.enter_description')"
            class="fc-input-full-border-select2 width100"
          >
          </el-input>
        </el-form-item>
        <el-form-item
          :label="$t('setup.classification.attributes.field_type')"
          prop="fieldType"
        >
          <el-select
            v-model="attributeData.fieldType"
            class="fc-input-full-border-select2 width100 "
            :placeholder="$t('setup.classification.attributes.data_type')"
            :disabled="!isNew"
          >
            <el-option
              v-for="(option, index) in typeDatas"
              :key="index + 'typeDatas'"
              :label="option"
              :value="parseInt(index)"
            ></el-option>
          </el-select>
        </el-form-item>
        <div v-if="hasMetricFieldType" class="d-flex">
          <el-form-item class="width100">
            <el-select
              v-model="attributeData.metric"
              filterable
              :placeholder="$t('setup.classification.attributes.choose_metric')"
              class="fc-input-full-border-select2 width100 pR15"
              @change="resetUnitField"
              :loading="isMetricLoading"
            >
              <el-option
                v-for="option in metricsList"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item class="width100">
            <el-select
              v-model="attributeData.unitId"
              filterable
              :placeholder="$t('setup.classification.attributes.choose_unit')"
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="option in unitOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
      </el-form>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialog" class="modal-btn-cancel">{{
        $t('setup.classification.attributes.cancel')
      }}</el-button>
      <el-button
        type="primary"
        class="modal-btn-save"
        @click="onSave"
        :loading="saving"
        >{{ $t('setup.classification.attributes.save') }}</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import { mapGetters, mapActions, mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { AttributeListModel } from 'pages/setup/classification/AttributeModal'

const specificUserValue = {
  STRING: 1,
  NUMBER: 2,
  DECIMAL: 3,
  BOOLEAN: 4,
  DATETIME: 6,
}
export default {
  props: ['selectedId'],
  data() {
    return {
      saving: false,
      isLoading: false,
      specificUserValue,
      attributeData: {},
      rules: {
        name: [
          {
            required: true,
            message: this.$t(
              'setup.classification.attributes.please_input_attr_name'
            ),
            trigger: 'blur',
          },
        ],
        fieldType: [
          {
            required: true,
            message: this.$t(
              'setup.classification.attributes.please_select_field'
            ),
            trigger: 'change',
          },
        ],
      },
      typeDatas: {
        1: 'String',
        2: 'Number',
        3: 'Decimal',
        4: 'Boolean',
        6: 'DateTime',
      },
    }
  },
  created() {
    this.getCurrentAttributeData()
    this.loadMetricUnits()
  },
  computed: {
    ...mapGetters({
      getMetrics: 'metricUnits/getMetrics',
      getMetricsUnit: 'metricUnits/getMetricsUnit',
    }),
    ...mapState({
      isMetricLoading: state => state.metricUnits.isMetricLoading,
    }),
    metricsList() {
      let metrics = this.getMetrics
      return metrics || []
    },
    unitOptions() {
      let {
        attributeData: { metric },
      } = this
      let options = this.getMetricsUnit({ metricId: metric })
      return options || []
    },
    isNew() {
      return isEmpty(this.selectedId)
    },
    title() {
      return this.isNew
        ? this.$t('setup.classification.attributes.new_attribute')
        : this.$t('setup.classification.attributes.edit_attribute')
    },
    hasMetricFieldType() {
      let { attributeData } = this
      return (
        attributeData.fieldType == specificUserValue.DECIMAL ||
        attributeData.fieldType == specificUserValue.NUMBER
      )
    },
  },
  methods: {
    ...mapActions({
      loadMetricUnits: 'metricUnits/loadMetricUnits',
    }),
    async getCurrentAttributeData() {
      this.isLoading = true
      if (!this.isNew) {
        this.attributeData = await AttributeListModel.fetch({
          id: this.selectedId,
        })
      } else {
        this.attributeData = new AttributeListModel()
      }
      this.isLoading = false
    },
    resetUnitField() {
      this.attributeData.unitId = null
    },
    closeDialog() {
      this.$emit('onClose')
    },
    async validate() {
      try {
        return await this.$refs['classification-form'].validate()
      } catch {
        return false
      }
    },
    async onSave() {
      let valid = await this.validate()
      if (!valid) {
        return
      }
      this.saving = true

      try {
        await this.attributeData.save()

        if (isEmpty(this.selectedId))
          this.$message.success(
            this.$t('setup.classification.attributes.created_success')
          )
        else
          this.$message.success(
            this.$t('setup.classification.attributes.update_success')
          )
      } catch (error) {
        this.$message.error(error.message)
      }

      this.saving = false
      this.$emit('onSave')
      this.closeDialog()
    },
  },
}
</script>
<style lang="scss" scoped>
.dialog-header {
  color: #4f4f4f;
  font-weight: 500;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  border-bottom: solid 1px #d0d9e2;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 28px;
  justify-content: space-between;
  .close-icon-dialog {
    font-size: 24px;
    font-weight: 500;
  }
}
.attributes-dialog {
  padding: 40px 32px 50px;
  .el-form-item {
    margin-bottom: 40px;

    .el-form-item__content {
      line-height: normal;
    }
    .el-form-item__label {
      font-weight: 500;
      color: #2f4058;
      font-size: 14px;
      letter-spacing: 0.5px;
      line-height: normal;
      padding-bottom: 8px;
    }
  }
}
.classification-shimmer-lines .lines {
  height: 30px;
  width: 90%;
  margin: 0px 20px 20px;
  border-radius: 5px;
}
.classification-shimmer-lines .span-name {
  height: 10px;
  width: 50%;
  margin: 20px 20px 5px;
  border-radius: 5px;
}
.classification-shimmer-lines .rectangle {
  height: 90px;
  width: 90%;
  margin: 0px 20px 20px;
  border-radius: 5px;
}
</style>
