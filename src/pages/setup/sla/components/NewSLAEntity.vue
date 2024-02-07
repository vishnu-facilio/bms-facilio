<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    :before-close="cancel"
    custom-class="fc-dialog-form fc-dialog-right custom-rule-dialog setup-dialog40 setup-dialog"
    style="z-index: 1999"
  >
    <el-form
      ref="new-stateflow-form"
      :model="slaEntity"
      :rules="rules"
      :label-position="'top'"
    >
      <div class="new-header-container mR30 pL30">
        <div class="new-header-modal">
          <div class="new-header-text">
            <div class="setup-modal-title">
              {{ title }}
            </div>
          </div>
        </div>
        <div class="sla-field-desc truncate-text mT3">
          {{ $t('setup.setupLabel.sla_new_type_header_desc') }}
        </div>
      </div>
      <div class="new-body-modal new-escalation-level">
        <el-row class="mB10">
          <el-col :span="24">
            <el-form-item prop="name" class="mB10">
              <p class="fc-input-label-txt newsla-header-color">
                Name
              </p>
              <el-input
                :autofocus="isNew"
                v-model="slaEntity.name"
                class="width100 pR20"
                :placeholder="$t('setup.failureclass.enter_description')"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item prop="description">
              <p class="fc-input-label-txt newsla-header-color">
                {{ $t('setup.approvalprocess.description') }}
              </p>
              <el-input
                v-model="slaEntity.description"
                :min-rows="1"
                type="textarea"
                :autosize="{ minRows: 2, maxRows: 4 }"
                class="fc-input-full-border-select2 width100"
                :placeholder="$t('setup.failureclass.enter_description')"
                resize="none"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="mB10">
          <el-form-item prop="baseFieldId" class="mB10">
            <p class="fc-input-label-txt m0 newsla-header-color">
              {{ $t('setup.setupLabel.sla_field') }}
            </p>
            <p class="sla-field-desc mB20">
              {{ $t('setup.setupLabel.sla_field_desc') }}
            </p>
            <el-select
              v-model="slaEntity.dueFieldId"
              :placeholder="$t('setup.globalscoping.selectfield')"
              class="fc-input-full-border-select2 width100 pR20 sla-entity"
              filterable
            >
              <template slot="prefix">
                <InlineSvg
                  src="svgs/calendar"
                  iconClass="icon mT13 mL5"
                ></InlineSvg>
              </template>
              <el-option
                v-for="item in fields.filter(f => [5, 6].includes(f.dataType))"
                :key="item.id"
                :label="item.displayName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-row>
        <el-row class="mB10">
          <el-form-item prop="compareFieldId" class="mB10">
            <p class="fc-input-label-txt m0 newsla-header-color">
              {{ $t('setup.setupLabel.sla_referance_field') }}
            </p>
            <p class="sla-field-desc mB20">
              {{ $t('setup.setupLabel.sla_referance_field_desc') }}
            </p>
            <el-select
              v-model="slaEntity.baseFieldId"
              :placeholder="$t('setup.globalscoping.selectfield')"
              class="fc-input-full-border-select2 width100 pR20 sla-entity"
              filterable
            >
              <template slot="prefix">
                <InlineSvg
                  src="svgs/calendar"
                  iconClass="icon mT13 mL5"
                ></InlineSvg>
              </template>
              <el-option
                v-for="item in fields.filter(f => [5, 6].includes(f.dataType))"
                :key="item.id"
                :label="item.displayName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-row>
        <div class="mB10">
          <span class="fc-modal-sub-title fc-input-label-color">
            {{ $t('setup.setupLabel.sla_criteria') }}
          </span>
          <el-tooltip
            placement="top-start"
            :content="$t('setup.setupLabel.sla_criteria_info')"
          >
            <i class="el-icon-info mL5"></i>
          </el-tooltip>
        </div>
        <div>
          <p class="m0 truncate-text sla-field-desc">
            {{ $t('setup.setupLabel.sla_criteria_desc') }}
          </p>
        </div>
        <CriteriaBuilder
          v-model="slaEntity.criteria"
          :moduleName="moduleName"
          ref="criteriaBuilder"
        ></CriteriaBuilder>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="cancel" class="modal-btn-cancel text-uppercase">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="save"
          :loading="isSaving"
          >{{
            isSaving ? $t('common._common._saving') : $t('common._common._save')
          }}</el-button
        >
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import { CriteriaBuilder } from '@facilio/criteria'

export default {
  name: 'NewSLAEntity',
  props: ['entity', 'moduleName', 'isNew'],
  components: { CriteriaBuilder },
  created() {
    this.$store.dispatch('loadUsers')
    this.loadFields().then(() => this.init())
  },
  data() {
    return {
      fields: [],
      slaEntity: {
        name: '',
        description: '',
        baseFieldId: null,
        dueFieldId: null,
        criteria: null,
      },
      isSaving: false,
      rules: {},
    }
  },
  computed: {
    title() {
      return this.isNew
        ? this.$t('setup.add.new_breach_type')
        : this.$t('setup.add.edit_breach_type')
    },
  },
  methods: {
    loadFields() {
      return this.$http
        .get('/module/metafields?moduleName=' + this.moduleName)
        .then(({ data }) => {
          this.fields = this.$getProperty(data, 'meta.fields', null) || []
        })
    },
    init() {
      if (!this.isNew) {
        this.slaEntity = {
          ...this.slaEntity,
          ...this.entity,
        }
      }
    },
    validate() {
      let isValidCriteria = this.$refs['criteriaBuilder']?.validate()
      return isValidCriteria
    },
    cancel() {
      this.$emit('onClose')
    },
    save() {
      let isValid = this.validate()

      if (!isValid) return

      let { slaEntity, moduleName } = this

      this.isSaving = true
      this.$http
        .post('v2/slaEntity/addOrUpdate', { moduleName, slaEntity })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.$message.success('SLA entity saved')
            this.$emit('onSave', data.result.slaEntity)
            this.cancel()
          }
          this.isSaving = false
        })
        .catch(() => {
          this.isSaving = false
          this.$message.error('Could not create SLA entity')
        })
    },
  },
}
</script>
<style scoped>
.new-subheader-model {
  line-height: 24px;
  letter-spacing: 0.5px;
}
.fc-input-label-color {
  color: #3ab2c1 !important;
  text-transform: capitalize !important;
  font-size: 14px;
  font-weight: 400;
  letter-spacing: 0.5px;
  line-height: normal;
}
.sla-field-desc {
  font-size: 12px;
  word-break: break-word;
  line-height: normal;
  letter-spacing: 0.6px;
  color: #999;
}
.newsla-header-color {
  color: #3ab2c1 !important;
  text-transform: capitalize;
}
</style>
<style lang="scss">
.sla-entity {
  .el-input__inner {
    padding-left: 35px !important;
  }
}
</style>
