<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div id="newenpi">
      <el-form
        :model="newenpi"
        :label-position="'top'"
        ref="meterForm"
        class="fc-form"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{
                isNew ? $t('setup.list.new') : $t('maintenance.pm_list.edit')
              }}
              ENPI DEFINITION
            </div>
          </div>
        </div>

        <div class="new-body-modal enpi-body-modal">
          <div class="body-scroll">
            <el-row :gutter="20">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  ENPI {{ $t('common.roles.name') }}
                </p>
                <el-form-item>
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="newenpi.name"
                    type="text"
                    autocomplete="off"
                    placeholder="Enter EnPI Name"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="24">
                <p class="fc-input-label-txt">
                  {{ $t('space.sites.site_description') }}
                </p>
                <el-form-item>
                  <el-input
                    type="textarea"
                    :autosize="{ minRows: 4, maxRows: 4 }"
                    class="width100 fc-input-full-border-textarea"
                    :placeholder="$t('common._common.enter_desc')"
                    v-model="newenpi.description"
                    resize="none"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <p class="fc-input-label-txt mT10">
                  {{ $t('maintenance.wr_list.type') }}
                </p>
                <el-radio
                  :disabled="!isNew"
                  v-model="enpiType"
                  label="space"
                  value="space"
                  class="fc-radio-btn"
                  >{{ $t('alarm.alarm.space') }}</el-radio
                >
                <el-radio
                  :disabled="!isNew"
                  v-model="enpiType"
                  label="asset"
                  value="asset"
                  class="fc-radio-btn"
                  >{{ $t('alarm.alarm.asset') }}</el-radio
                >
              </el-col>
            </el-row>
            <el-row :gutter="20" class="mT30">
              <template v-if="enpiType === 'asset'">
                <el-col :span="12">
                  <div class="fc-input-label-txt">
                    {{ $t('space.sites.site_category') }}
                  </div>
                  <el-select
                    :disabled="!isNew"
                    v-model="newenpi.assetCategoryId"
                    filterable
                    placeholder="Select Category"
                    class="fc-input-full-border-select2 width100"
                  >
                    <el-option
                      v-for="(category, index) in assetCategory"
                      :key="index"
                      :label="category.displayName"
                      :value="parseInt(category.id)"
                    ></el-option>
                  </el-select>
                </el-col>
                <el-col :span="12">
                  <div class="fc-input-label-txt">Asset(s)</div>
                  <el-input
                    v-model="resourceLabel"
                    disabled
                    class="fc-border-select fc-input-full-border-select2 width100"
                  >
                    <i
                      @click="isNew ? (chooserVisibility = true) : null"
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                    ></i>
                  </el-input>
                  <!-- <div class="add f-element resource-list pB20">
                  <div>
                    <el-select class="multi mT10 width100 fc-input-full-border-select2" multiple v-model="dummyAssetValue" disabled style="width: 80%;">
                      <el-option :label="resourceLabel" :value="1"></el-option>
                     <i @click="isNew ? chooserVisibility = true : null" slot="suffix" style="line-height:0px !important; font-size:16px !important; cursor:pointer;" class="el-input__icon el-icon-search"></i>
                    </el-select>
                  </div>
                </div> -->
                </el-col>
              </template>

              <el-col :span="12" v-else>
                <template>
                  <div class="fc-input-label-txt">
                    {{ $t('alarm.alarm.space') }}
                  </div>
                  <el-input
                    @change="
                      resourceQuery = selectedResourceName
                      chooserVisibility = true
                    "
                    :disabled="!isNew"
                    v-model="selectedResourceName"
                    class="mT10 fc-input-full-border2"
                    type="text"
                    :placeholder="$t('common._common.to_search_type')"
                  >
                    <i
                      @click="isNew ? (chooserVisibility = true) : null"
                      :disabled="!isNew"
                      slot="suffix"
                      class="el-input__icon el-icon-search enpi-icon-search"
                    ></i>
                  </el-input>
                </template>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="12">
                <div class="fc-input-label-txt mT30">
                  {{ $t('space.sites.site_freq') }}
                </div>
                <el-select
                  v-model="newenpi.frequency"
                  class="fc-input-full-border2 width100"
                  placeholder="Choose Frequency"
                  :disabled="!isNew"
                >
                  <el-option
                    v-for="(label, value) in frequencyTypes"
                    :key="value"
                    :label="label"
                    :value="parseInt(value)"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <div class="fc-input-label-txt mT30">
                  {{ $t('commissioning.sheet.header_unit') }}
                </div>
                <el-input
                  class="fc-input-full-border2"
                  v-model="formulaFieldUnit"
                  type="text"
                  placeholder="Enter Unit"
                />
              </el-col>
            </el-row>
            <div class="mT40">
              <f-formula-builder
                v-model="newenpi.workflow"
                :isNew="isNew"
                module="enpi"
                :assetCategory="{ id: newenpi.assetCategoryId }"
              ></f-formula-builder>
            </div>
            <f-safe-limit
              v-model="newenpi.readingField"
              :edit="!this.isNew"
              ref="safelimit"
            />
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">
            {{ $t('setup.users_management.cancel') }}</el-button
          >
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="submitForm('meterForm')"
            :loading="saving"
            >{{
              saving
                ? $t('maintenance._workorder.saving')
                : $t('maintenance._workorder.save')
            }}
          </el-button>
        </div>
      </el-form>
      <space-asset-multi-chooser
        v-if="newenpi.assetCategoryId > 0"
        @associate="associateResource"
        :visibility.sync="chooserVisibility"
        :initialValues="resourceData"
        :query="resourceQuery"
        :showAsset="true"
        :disable="true"
        :hideBanner="true"
      ></space-asset-multi-chooser>
      <space-asset-chooser
        v-else
        @associate="associateResource"
        :visibility.sync="chooserVisibility"
        :initialValues="{}"
        :query="resourceQuery"
        :showAsset="enpiType === 'asset'"
        :picktype="enpiType"
      ></space-asset-chooser>
    </div>
  </el-dialog>
</template>
<script>
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import FSafeLimit from '@/FSafeLimit'
import { mapState } from 'vuex'
export default {
  props: ['isNew', 'enpi', 'visibility'],
  data() {
    return {
      saving: false,
      formulaFieldUnit: '',
      enpiType: 'space',
      newenpi: {
        name: '',
        description: '',
        resourceId: null,
        frequency: null,
        triggerType: 1,
        formulaFieldType: 1,
        resourceType: 1,
        resultDataType: 3,
        workflow: {},
        assetCategoryId: null,
        includedResources: [],
        readingField: {
          safeLimitId: -1,
          raiseSafeLimitAlarm: false,
          displayName: '',
          dataType: 1,
          lesserThan: null,
          greaterThan: null,
          betweenTo: null,
          betweenFrom: null,
          safeLimitPattern: 'none',
          safeLimitSeverity: 'Minor',
        },
      },
      selectedResourceName: null,
      selectedResourceList: null,
      resourceQuery: null,
      chooserVisibility: false,
    }
  },
  components: {
    FFormulaBuilder,
    SpaceAssetChooser,
    SpaceAssetMultiChooser,
    FSafeLimit,
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),

    frequencyTypes() {
      let types = this.$helpers.cloneObject(this.$constants.FACILIO_FREQUENCY)
      types['8'] = 'Hourly'
      return types
    },

    resourceData() {
      let resourceData = {}
      if (this.newenpi.assetCategoryId > 0) {
        resourceData.assetCategory = this.newenpi.assetCategoryId
      }
      if (
        this.newenpi.includedResources &&
        this.newenpi.includedResources.length
      ) {
        resourceData.isIncludeResource = true
        resourceData.selectedResources = this.newenpi.includedResources.map(
          resource => ({ id: resource })
        )
      }
      return resourceData
    },

    dummyAssetValue() {
      if (this.resourceLabel) {
        return [1]
      }
      return []
    },

    resourceLabel() {
      if (this.newenpi.assetCategoryId > 0) {
        let category = this.assetCategory.filter(
          d => d.id === this.newenpi.assetCategoryId
        )
        let message
        let selectedCount = 0
        if (this.newenpi.includedResources) {
          selectedCount = this.newenpi.includedResources.length
        }
        if (selectedCount) {
          if (selectedCount === 1) {
            return this.newenpi.includedResources[0].name
          }
          message =
            selectedCount +
            ' ' +
            category[0].name +
            (selectedCount > 1 ? 's' : '')
        } else {
          message = 'All ' + category[0].name + 's selected'
        }
        return message
      } else {
        return this.selectedResourceName
      }
    },
  },
  mounted() {
    if (!this.isNew) {
      this.newenpi = this.$helpers.cloneObject(this.enpi)
      this.selectedResourceName = this.enpi.matchedResources[0].name
      this.formulaFieldUnit = this.enpi.readingField.unit
      if (this.enpi.assetCategoryId === -1) {
        this.newenpi.assetCategoryId = null
      }
      this.enpiType =
        this.enpi.matchedResources[0].resourceType === 1 ? 'space' : 'asset'
    } else if (
      this.isNew &&
      typeof this.enpi !== 'undefined' &&
      Object.keys(this.enpi).length !== 0
    ) {
      // saving regression expressions
      this.newenpi = this.$helpers.cloneObject(this.enpi)
      this.selectedResourceName = this.enpi.matchedResources[0].name
      delete this.newenpi.matchedResources
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
  },
  methods: {
    submitForm(meterForm) {
      let self = this
      this.$refs[meterForm].validate(valid => {
        if (valid) {
          let fieldReadingRules = []
          fieldReadingRules.push(
            this.$common.getSafeLimitRules(
              this.newenpi.readingField,
              this.newenpi.assetCategoryId
            )
          )
          let url = '/reading/addformula'
          // this.newenpi.interval = 60
          let objToSave = this.$helpers.cloneObject(this.newenpi)
          if (objToSave.assetCategoryId > 0) {
            objToSave.resourceType = 6
            delete objToSave.resourceId
          } else {
            objToSave.resourceType = 1
            delete objToSave.assetCategoryId
          }

          let param = {
            formula: objToSave,
            fieldReadingRules,
            formulaFieldUnit: this.formulaFieldUnit,
          }
          if (this.isNew) {
            param.formula.interval = 60
          }
          if (!this.isNew) {
            let delReadingRulesIds = []
            if (
              this.newenpi.readingField &&
              this.newenpi.readingField.safeLimitPattern === 'none' &&
              this.newenpi.readingField.safeLimitId &&
              this.newenpi.readingField.safeLimitId !== -1
            ) {
              delReadingRulesIds.push(this.newenpi.readingField.safeLimitId)
            }
            url = '/reading/updateformula'
            objToSave = this.$helpers.compareObject(objToSave, this.enpi)
            objToSave.id = this.enpi.id
            // param=  {formula: self.newenpi}
            param = {
              formula: objToSave,
              moduleId: this.enpi.moduleId,
              formulaFieldUnit: this.formulaFieldUnit,
              delReadingRulesIds,
              fieldReadingRules,
            }
          }
          /* let newenpi = {}
          newenpi.enpi = {
            name: enpi.name,
            description: enpi.description,
            frequency: enpi.frequency
          }
          // if (this.newenpi.frequency === 1) {
          //   newenpi.enpi.schedule = {'frequencyType': 1, 'frequency': 1, 'tim
          resultDataType: 3,es': ['00:01']}
          // }
          // else if (this.newenpi.frequency === 2) {
          //   newenpi.enpi.schedule = {'frequencyType': 3, 'frequency': 1, 'times': ['00:01']}
          // }
          // else if (this.newenpi.frequency === 3) {
          //   newenpi.enpi.schedule = {'frequencyType': 3, 'frequency': 3, 'times': ['00:01']}
          // }
          if (enpi.resourceId) {
            newenpi.enpi.resourceId = enpi.resourceId
          }
          newenpi.enpi.workflow = this.newenpi.workflow */
          self.saving = true
          this.$http
            .post(url, param)
            .then(function(response) {
              self.saving = false
              if (typeof response.data === 'object') {
                self.$message.success(
                  self.isNew
                    ? self.$t('setup.new.new_enpi_success')
                    : self.$t('setup.new.update_enpi_success')
                )
                self.resetForm()
                self.$emit('saved')
                self.$emit('update:visibility', false)
              } else {
                self.$message.error(
                  self.isNew ? 'EnPI addition failed' : 'EnPI updation failed'
                )
              }
            })
            .catch(function(error) {
              self.saving = false
              self.$message.error(
                self.isNew ? 'EnPI addition failed' : 'EnPI updation failed'
              )
            })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    resetForm() {
      this.newenpi = {
        name: '',
        description: '',
        space: {
          id: null,
        },
        frequency: null,
        workflow: {},
      }
    },
    cancel() {
      this.resetForm()
      this.$emit('canceled')
    },
    save() {
      let self = this
      self.saving = true
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    associateResource(selectedObj) {
      this.chooserVisibility = false
      this.resourceQuery = null

      if (this.newenpi.assetCategoryId > 0) {
        if (selectedObj.resourceList && selectedObj.resourceList.length) {
          this.newenpi.includedResources = selectedObj.resourceList.map(
            resource => resource.id
          )
        }
      } else {
        this.newenpi.resourceId = selectedObj.id
        this.selectedResourceName = selectedObj.name
      }
    },
  },
}
</script>
<style>
#newenpi .el-input.is-disabled .el-input__inner {
  background-color: white !important;
}
#newenpi .textcolor {
  color: #6b7e91;
}
#newenpi .ruletitle {
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.8px;
  color: #ef4f8f;
}
#newenpi .header.text {
  font-size: 18px;
  text-align: left;
  letter-spacing: 0.6px;
}

#newenpi .header .el-textarea__inner {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #666666;
}

#newenpi .header.el-input .el-input__inner,
#newenpi .header.el-textarea .el-textarea__inner {
  border-bottom: none;
  resize: none;
}
#newenpi .primarybutton.el-button {
  background-color: #39b2c2 !important;
  border-color: #39b2c2 !important;
  color: #ffffff !important;
  float: right;
}
#newenpi .fc-form .form-header,
#newenpi .fc-form-container .form-header {
  font-weight: normal;
  font-size: 16px;
  text-align: left;
}
#newenpi .fc-form .form-input {
  padding: 0px;
}
#newenpi .column-item {
  padding: 10px;
  border: 1px solid #f2f2f2;
  cursor: move;
  margin-top: 5px;
}
#newenpi.el-button:focus,
#newenpi .el-button:hover {
  background-color: #ecf5ff;
}
.fc-create-record {
  width: 50% !important;
}
.body-scroll {
  width: 100%;
  overflow-y: scroll;
  display: inline-block;
  /* height: 100vh; */
  padding-bottom: 30px;
}
.enpi-body-modal {
  height: calc(100vh - 100px) !important;
}
.enpi-icon-search {
  line-height: 0px !important;
  font-size: 16px !important;
  cursor: pointer;
  position: absolute;
  top: 0px;
  right: 11%;
}
</style>
