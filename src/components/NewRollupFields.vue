<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form setup-dialog50 setup-dialog"
      :before-close="closeDialog"
      style="z-index: 999999"
    >
      <el-form :model="rollupData" :label-position="'top'" ref="roolupForm">
        <el-header class="fc-dialog-right-header" height="50">
          <div class="fc-setup-modal-title">
            {{ isNew ? 'New' : 'Edit' }} Rollup Fields
          </div>
        </el-header>
        <div class="new-body-modal">
          <el-form-item label="Name" prop="name">
            <el-input
              type="text"
              v-model="rollupData.name"
              class="fc-input-full-border2"
              placeholder="Enter user name"
            ></el-input>
          </el-form-item>
          <el-form-item label="Description" prop="description">
            <el-input
              v-model="rollupData.description"
              type="textarea"
              :autosize="{ minRows: 2, maxRows: 2 }"
              resize="none"
              class="fc-input-full-border-select2"
              :placeholder="$t('common._common.enter_desc')"
            ></el-input>
          </el-form-item>
          <div class="fc-text-pink mT20">CONFIGURATION</div>
          <el-form-item label="Module" prop="module">
            <el-select
              v-model="rollupData.formModule"
              placeholder="Select"
              filterable
              clearable
              class="width70"
              @change="moduleChange(rollupData)"
            >
              <el-option
                v-for="(moduleValue, index) in moduleValueDatas"
                :key="index"
                :label="moduleValue.displayName"
                :value="moduleValue.name"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="Rollup Type" prop="rollupType">
            <el-radio-group v-model="rollupData.rollupType">
              <el-radio :label="1" class="fc-radio-btn">Count</el-radio>
              <el-radio :label="3" class="fc-radio-btn">Sum</el-radio>
              <el-radio :label="4" class="fc-radio-btn">Min</el-radio>
              <el-radio :label="5" class="fc-radio-btn">Max</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="Field to Aggregate" prop="aggregate">
            <el-select
              v-model="rollupData.aggregate"
              filterable
              clearable
              placeholder="Select"
              class="width70"
            >
              <el-option
                v-for="(category, index) in categoryFields"
                :key="index"
                :label="category.displayName"
                :value="category.name"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="criteria" class="pT10">
            <el-radio-group v-model="rollupData.criteriaSelect">
              <el-radio :label="4" class="fc-radio-btn">All records</el-radio>
              <el-radio :label="5" class="fc-radio-btn"
                >Specific records</el-radio
              >
            </el-radio-group>
          </el-form-item>
          <!-- creteria builder -->
          <new-criteria-builder
            v-if="rollupData.criteriaSelect === 5"
            v-model="rollupData.criteria"
            :exrule="rollupData.criteria"
            @condition="somefnt"
            :title="'Specify rules for event filtering'"
            :module="rollupData.formModule"
          ></new-criteria-builder>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel"
            >CANCEL</el-button
          >
          <el-button
            type="primary"
            @click="submitForm('roolupForm')"
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
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['visibility', 'isNew', 'module', 'model'],
  data() {
    return {
      saving: false,
      loading: true,
      rollupData: {
        name: '',
        description: '',
        formModule: null,
        rollupType: 1,
        aggregate: null,
        criteriaSelect: 4,
        criteria: null,
        id: '',
      },
      moduleValueDatas: [],
      asset: null,
      categoryFields: [],
      category: null,
    }
  },
  components: {
    NewCriteriaBuilder,
  },
  computed: {},
  mounted() {
    this.loadAssetFields()
    if (!this.isNew) {
      Object.assign(this.rollupData, {
        name: this.model.parentRollUpField.displayName,
        description: this.model.description,
        formModule: this.model.childModule.name,
        rollupType: this.model.aggregateFunctionId,
        aggregate: this.model.aggregateField.displayName,
        criteriaSelect: !isEmpty(this.model.childCriteria) ? 5 : 4,
        criteria: this.model.childCriteria,
        id: this.model.parentRollUpField.id,
        // criteria,
        // id
      })
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    async loadAssetFields() {
      this.loading = true
      let { error, data } = await API.get(
        `v2/rollUpField/fetchRollUpSubModules?moduleName=${this.module}`
      )
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.moduleValueDatas = data.submodules
        this.loading = false
      }
    },
    somefnt(newVal) {
      this.rollupData.criteria = newVal
    },
    loadMetaFields() {
      this.loading = true
      let moduleData = this.rollupData.formModule
      let spaceRemoved = moduleData.split(' ').join('')
      // if(spaceRemoved) {
      //   return
      // }
      this.categoryFields = []
      this.$http
        .get(`module/metafields?moduleName=${spaceRemoved}`)
        .then(response => {
          this.categoryFields = response.data.meta.fields || []
          this.loading = false
        })
    },
    moduleChange(rollupData) {
      if (rollupData.formModule) {
        this.aggregate =
          this.moduleValueDatas.length > 0 ? this.moduleValueDatas[0].id : null
        this.loadMetaFields()
        rollupData.criteria = null
        rollupData.criteriaSelect = 4
        rollupData.aggregate = null
        this.categoryFields = null
      } else {
        rollupData.criteria = null
        rollupData.criteriaSelect = 4
        rollupData.aggregate = null
        this.categoryFields = null
      }
    },
    submitForm(roolupForm) {
      this.$refs[roolupForm].validate(valid => {
        if (valid) {
          this.saving = true
          if (!this.isNew) {
            let rollupFieldUpdate = {
              aggregateFunctionId: this.rollupData.rollupType,
              description: this.rollupData.description,
              field: {
                displayName: this.rollupData.name,
                id: this.rollupData.id,
              },
            }
            if (this.rollupData.criteriaSelect === 5) {
              rollupFieldUpdate.criteria = this.rollupData.criteria
            } else {
              rollupFieldUpdate.criteria = {
                criteriaId: -99,
              }
            }

            this.$http
              .post('/v2/rollUpField/update', rollupFieldUpdate)
              .then(response => {
                let { responseCode, message } = response.data
                if (responseCode === 0) {
                  this.$message('Edit Rollup fields updated.')
                  this.saving = false
                  this.$emit('update:visibility', false)
                  this.$emit('saved')
                  this.resetForm()
                } else {
                  this.$message.error(message)
                  this.saving = false
                }
              })
          } else {
            let rollupFieldData = {
              moduleName: this.module,
              fields: [
                {
                  displayName: this.rollupData.name,
                },
              ],
              childModuleId: this.moduleValueDatas.find(
                module => module.name === this.rollupData.formModule
              ).moduleId,
              aggregateFunctionId: this.rollupData.rollupType,
              description: this.rollupData.description,
              aggregateFieldId: this.categoryFields.find(
                module => module.name === this.rollupData.aggregate
              ).id,
            }

            if (this.rollupData.criteriaSelect === 5) {
              rollupFieldData.criteria = this.rollupData.criteria
            } else {
              rollupFieldData.criteria = {
                criteriaId: -99,
              }
            }
            this.$http
              .post('/v2/rollUpField/add', rollupFieldData)
              .then(response => {
                let { responseCode, message } = response.data
                if (responseCode === 0) {
                  this.$message('New rollup field added.')
                  this.saving = false
                  this.$emit('update:visibility', false)
                  this.$emit('saved')
                  this.resetForm()
                } else {
                  this.$message.error(message)
                  this.saving = false
                }
              })
          }
        }
      })
    },
    resetForm() {
      this.$refs['roolupForm'].resetFields()
    },
  },
}
</script>

<style lang="scss"></style>
