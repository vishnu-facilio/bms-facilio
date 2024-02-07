<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div id="newenergymeter">
      <el-popover
        ref="purposePopver"
        placement="right"
        :title="$t('common.products.add_purpose')"
        width="300"
        trigger="click"
        v-model="popoverVisible"
        popper-class="purpose-popover"
      >
        <div>
          <el-input
            v-model="newPurpose"
            style="margin-bottom: 40px;padding-left: 20px;padding-right: 20px;"
          ></el-input>
          <div style="text-align: right; margin: 0px 0px 0px;">
            <el-button
              size="mini"
              type="text"
              @click="popoverVisible = false"
              class="modal-btn-cancel"
              >{{ $t('setup.users_management.cancel') }}</el-button
            >
            <el-button
              type="primary"
              size="mini"
              @click="addNewPurpose"
              class="modal-btn-save"
              :loading="purposeSaving"
              >{{
                purposeSaving
                  ? $t('common._common._saving')
                  : $t('common._common._save')
              }}</el-button
            >
          </div>
        </div>
      </el-popover>
      <el-form
        :model="meter"
        :rules="rules"
        :label-position="'top'"
        ref="meterForm"
        class="fc-form"
      >
        <div
          class="form-header"
          style="margin-top:0px;padding-top:0;padding-left:45px;padding-top: 20px;padding-bottom: 20px;"
        >
          <div class="setup-modal-title">
            {{
              isNew
                ? $t('common.wo_report.new_energy_meter')
                : $t('common.header.edit_energy_meter')
            }}
          </div>
          <!-- <div style="float:right;padding-right:40px">
        <el-button class="btn-modal-fill"  type="primary"  @click="submitForm('meterForm')" :loading="saving">{{saving ? 'Saving...' : 'SAVE'}} </el-button>
        <el-button class="btn-modal-border" @click="cancel"> CANCEL</el-button>
      </div> -->
        </div>
        <div style="border-bottom: 1px solid #edeeef;"></div>
        <el-row
          align="middle"
          style="margin:0px;border-bottom:1px solid #edeeef;padding-top:10px; padding-bottom:15px; padding-left:20px; background:#fcfcfc;"
          :gutter="50"
        >
          <el-col :span="24" style="padding-right: 25px;">
            <div
              style="float:left;font-size: 13px;font-weight: 500;letter-spacing: 0.7px;padding-top:6px; "
            >
              {{ $t('setup.setupLabel.is_virtual_meter') }}
            </div>
            <div
              style="float:left;padding-left:100px;font-size: 13px;font-weight: normal;letter-spacing: 0.4px;padding-top:5px; color:#666666;"
            >
              {{ meter.isVirtual ? 'Yes' : 'No' }}
            </div>
            <div style="float: left;padding-top: 2px;padding-left: 10px;">
              <el-switch
                v-if="!isNew"
                disabled
                v-model="meter.isVirtual"
                active-color="#ef4f8f"
              ></el-switch>
              <el-switch
                v-else
                v-model="meter.isVirtual"
                active-color="#ef4f8f"
              ></el-switch>
            </div>
            <div style="clear:both;"></div>
          </el-col>
        </el-row>

        <div class="new-body-modal energy-body-modal" style="padding-top: 0;">
          <el-row :gutter="20">
            <el-col :span="24">
              <p class="fc-input-label-txt">{{ $t('common.roles.name') }}</p>
              <el-input
                class="text required header width100 fc-input-full-border2"
                v-model="meter.name"
                type="text"
                autocomplete="off"
                autofocus
                :placeholder="$t('setup.setupLabel.enter_meter_name')"
              />
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24" class="mT20">
              <p class="fc-input-label-txt">
                {{ $t('maintenance._workorder.description') }}
              </p>
              <el-input
                type="textarea"
                :placeholder="$t('common._common.enter_desc')"
                class="text required header fc-input-full-border-textarea"
                :autosize="{ minRows: 3, maxRows: 4 }"
                v-model="meter.description"
                autocomplete="off"
                resize="none"
              ></el-input>
            </el-col>
          </el-row>

          <el-row
            v-if="sites.length !== 1 && isNew"
            align="middle"
            :gutter="20"
          >
            <el-col :span="12" class="mT20">
              <div class="fc-input-label-txt">
                {{ $t('space.sites._site') }}
              </div>
              <div class="add">
                <Lookup
                  v-model="meter.siteId"
                  :field="fields.site"
                  :hideLookupIcon="true"
                  @recordSelected="setSelectedValue"
                  @showLookupWizard="showLookupWizardSite"
                >
                </Lookup>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12" class="mT30">
              <div class="fc-input-label-txt">
                {{ $t('setup.setupLabel.serving_location') }}
              </div>
              <div class="add">
                <el-select
                  v-model="meter.purposeSpace.id"
                  filterable
                  class="form-item width100 fc-input-full-border2 "
                  :placeholder="$t('common._common.choose_serving_location')"
                >
                  <el-option
                    v-for="space in spaces"
                    :key="space.id"
                    :label="space.name"
                    :value="space.id"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col
              v-if="showRootMeterOption"
              :span="12"
              style="padding-left: 0px;padding-left: 0px;padding-right: 25px;padding-top: 23px;position: relative;top: 29px;left: 30px;"
            >
              <div class="add">
                <el-switch
                  style="float:left;"
                  v-model="meter.root"
                  active-color="#ef4f8f"
                ></el-switch>
                <div style="float:left;padding-left: 10px;padding-top: 2px;">
                  {{ $t('setup.setupLabel.is_root_meter') }}
                </div>
                <div style="clear:both;"></div>
              </div>
            </el-col>
          </el-row>
          <el-row align="middle" :gutter="20">
            <el-col :span="12" class="mT30">
              <div class="fc-input-label-txt">
                {{ $t('common.header.purpose') }}
              </div>
              <div class="add">
                <el-select
                  v-model="meter.purpose.id"
                  filterable
                  class="form-item width100 fc-input-full-border2 "
                  :placeholder="$t('common._common.choose_purpose')"
                >
                  <el-option
                    v-for="(purpose, key) in picklistOptions"
                    :key="key"
                    :label="purpose"
                    :value="parseInt(key)"
                  >
                  </el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="4" style="position: relative;top: 56px;left: 30px;">
              <div class="inline">
                <el-button
                  v-popover:purposePopver
                  size="small"
                  style="text-align:center;width:44px;border-width: 1px;font-weight: normal;height:40px;font-sie: 16px;color: #333;"
                  icon="el-icon-plus"
                ></el-button>
              </div>
            </el-col>
            <el-col
              v-if="showPurposeRootMeterOption"
              :span="12"
              style="padding-top: 23px;padding-left: 0px;padding-top: 23px;padding-left: 0px;padding-left: 0px;padding-right: 25px;padding-top: 23px;position: relative;top: 0;left: 100px;"
            >
              <div class="add">
                <el-switch
                  style="float:left;"
                  v-model="meter.root"
                  active-color="#ef4f8f"
                ></el-switch>
                <div style="float:left;padding-left: 10px;padding-top: 2px;">
                  {{ $t('setup.setupLabel.is_root_meter') }}
                </div>
                <div style="clear:both;"></div>
              </div>
            </el-col>
          </el-row>
          <el-row v-if="!meter.isVirtual" align="middle" :gutter="20">
            <el-col :span="12" class="mT30">
              <div class="fc-input-label-txt">
                {{ $t('common._common.serial_number') }}
              </div>
              <div class="add">
                <el-input
                  v-model="meter.serialNumber"
                  :placeholder="$t('setup.setupLabel.enter_meter_serial')"
                  class="fc-input-full-border2 "
                ></el-input>
              </div>
            </el-col>
          </el-row>
          <el-row v-if="!meter.isVirtual" align="middle" :gutter="20">
            <el-col :span="12" class="mT30">
              <div class="fc-input-label-txt">
                {{ $t('common._common.located_at') }}
              </div>
              <div class="add">
                <el-select
                  v-model="meter.space.id"
                  filterable
                  style="width:100%"
                  class="form-item fc-input-full-border2 "
                  :placeholder="$t('common._common.choose_location')"
                >
                  <el-option
                    v-for="space in spaces"
                    :key="space.id"
                    :label="space.name"
                    :value="space.id"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
          <el-row align="middle" :gutter="20">
            <el-col :span="12" class="mT30">
              <div class="fc-input-label-txt">
                {{ $t('setup.setupLabel.multi_factor') }}
              </div>
              <div class="add">
                <el-input
                  v-model="meter.multiplicationFactor"
                  :placeholder="$t('setup.setupLabel.enter_multi_factor')"
                  class="fc-input-full-border2 "
                ></el-input>
              </div>
            </el-col>
          </el-row>
          <div v-if="meter.isVirtual" style="padding-top: 40px;">
            <f-formula-builder
              v-if="meter.isVirtual"
              v-model="meter.childMeterExpression"
              module="virtual"
              :hideModeChange="true"
            ></f-formula-builder>
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancel">
            {{ $t('setup.users_management.cancel') }}</el-button
          >
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="submitForm('meterForm')"
            :loading="saving"
            >{{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}
          </el-button>
        </div>
      </el-form>
    </div>
  </el-dialog>
</template>
<script>
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import { getFieldOptions } from 'util/picklist'
import { Lookup } from '@facilio/ui/forms'
import { mapStateWithLogging } from 'store/utils/log-map-state'
const fields = {
  site: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'site',
    field: {
      lookupModule: {
        name: 'site',
        displayName: 'Sites',
      },
    },

    multiple: false,
    additionalParams: {
      orderBy: 'spaceType',
      orderType: 'asc',
    },
  },
}

export default {
  name: 'NewEnergyMeter',
  props: ['meter', 'isNew', 'visibility'],
  data() {
    return {
      fields,
      workflow: null,
      saving: false,
      purposeSaving: false,
      picklistOptions: {},
      popoverVisible: false,
      initialValue: null,
      newPurpose: '',
      rules: {
        'meter.name': [
          {
            required: true,
            message: this.$t('common._common.please_enter_a_name'),
            trigger: 'blur',
          },
        ],
        'meter.purpose': [
          {
            required: true,
            message: this.$t('common.header.please_select_a_purpose'),
            trigger: 'blur',
          },
        ],
        'meter.siteId': [
          {
            required: true,
            message: this.$t('common.header.please_select_a_site'),
            trigger: 'blur',
          },
        ],
      },
      spaces: [],
    }
  },
  watch: {
    'meter.siteId': {
      handler: function(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.$util
            .loadSpace(null, null, [{ key: 'site', value: newVal }], null)
            .then(data => {
              this.spaces = data.basespaces
            })
        }
      },
      deep: true,
    },
  },
  components: {
    Lookup,
    FFormulaBuilder,
  },
  created() {
    this.$store.dispatch('loadEnergyMeters')
    this.$store.dispatch('loadServiceList')
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadSpaces')
  },
  mounted() {
    this.initEnergyMeter()
    this.loadPickList('energymeterpurpose')
  },
  computed: {
    ...mapState({
      sites: state => state.site,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
    showRootMeterOption() {
      return (
        this.meter.purposeSpace &&
        this.meter.purposeSpace.id &&
        !this.meter.purpose.id
      )
    },
    showPurposeRootMeterOption() {
      return (
        this.meter.purposeSpace &&
        this.meter.purposeSpace.id &&
        this.meter.purpose.id
      )
    },
  },
  methods: {
    initEnergyMeter: function() {
      this.workflow = this.meter.childMeterExpression
      if (!this.isNew) {
        this.initialValue = this.$helpers.cloneObject(this.meter)
      }
    },
    showLookupWizardSite(field, canShow) {
      canShow = false
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizardSite', canShow)
    },
    setSelectedValue(selectedValues, field) {
      selectedValues
      field
    },
    setExpressionString(expression) {
      this.meter.childMeterExpression = expression
    },
    async loadPickList(moduleName) {
      this.picklistOptions = {}
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: moduleName, skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.picklistOptions = options
      }
    },
    submitForm(meterForm) {
      let self = this
      this.$refs[meterForm].validate(valid => {
        if (valid) {
          let url

          let meterObj = {}
          if (this.isNew) {
            url = '/energymeter/add'
            meterObj.energyMeter = this.$helpers.cloneObject(this.meter)
            if (
              meterObj.energyMeter.purposeSpace &&
              !meterObj.energyMeter.purposeSpace.id
            ) {
              meterObj.energyMeter.purposeSpace.id = -1
            }
            if (
              meterObj.energyMeter.purpose &&
              !meterObj.energyMeter.purpose.id
            ) {
              meterObj.energyMeter.purpose.id = -1
            }
            if (meterObj.energyMeter.space && !meterObj.energyMeter.space.id) {
              meterObj.energyMeter.space.id = -1
            }
          } else {
            url = '/energymeter/update'
            meterObj.energyMeter = this.$helpers.compareObject(
              this.meter,
              this.initialValue
            )
            meterObj.energyMeter.id = this.meter.id
          }

          if (!this.meter.isVirtual) {
            delete meterObj.energyMeter.childMeterExpression
          } else if (
            !isEmpty(
              this.$getProperty(meterObj, 'energyMeter.purposeSpace.id', null)
            )
          ) {
            if (!meterObj.energyMeter.space) {
              meterObj.energyMeter.space = {}
            }
            meterObj.energyMeter.space.id = meterObj.energyMeter.purposeSpace.id
          }

          self.saving = true
          this.$http
            .post(url, meterObj)
            .then(function(response) {
              JSON.stringify(response)
              if (response.status === 200) {
                self.$message(
                  self.isNew
                    ? self.$t('common.wo_report.new_energy_meter_created')
                    : self.$t('common.wo_report.energy_meter_updated_sucess')
                )
                self.saving = false
                self.$emit('saved')
                self.$emit('update:visibility', false)
              } else {
                self.saving = false
              }
            })
            .catch(function(error) {
              console.log(error)
            })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    cancel() {
      this.$emit('canceled')
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    save() {
      let self = this
      self.saving = true
    },
    addNewPurpose() {
      let self = this
      this.purposeSaving = true
      this.$http
        .post('/energymeter/addPurpose', {
          energyMeterPurpose: { name: this.newPurpose },
        })
        .then(function(response) {
          self.purposeSaving = false
          if (response.data) {
            self.popoverVisible = false
            self.picklistOptions[response.data.id] = self.newPurpose
            self.meter.purpose.id = response.data.id
            self.$store.dispatch('updateServiceList', {
              id: response.data.id,
              name: self.newPurpose,
            })
            self.newPurpose = ''
          }
        })
    },
  },
}
</script>
<style>
#newenergymeter .textcolor {
  color: #6b7e91;
}
#newenergymeter .el-input .el-input__inner,
.el-textarea .el-textarea__inner {
  color: #333333;
}
#newenergymeter .ruletitle {
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.8px;
  color: #ef4f8f;
}
#newenergymeter .primarybutton.el-button {
  background-color: #39b2c2 !important;
  border-color: #39b2c2 !important;
  color: #ffffff !important;
  float: right;
}
#newenergymeter .fc-form .form-header,
.fc-form-container .form-header {
  font-weight: normal;
  font-size: 16px;
  text-align: left;
}
#newenergymeter .fc-form .form-input {
  padding: 0px;
}
#newenergymeter .column-item {
  padding: 10px;
  border: 1px solid #f2f2f2;
  cursor: move;
  margin-top: 5px;
}
#newenergymeter.el-button:focus,
#newenergymeter .el-button:hover {
  background-color: #ecf5ff;
}
.purpose-popover {
  padding-top: 20px;
}
.fc-create-record {
  width: 45% !important;
}
.energy-body-modal {
  height: calc(100vh - 200px) !important;
  padding-bottom: 60px !important;
}
.purpose-popover .el-popover__title {
  padding-left: 20px;
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #333333;
  margin: 0;
}
.purpose-popover .el-button + .el-button {
  margin-left: 0;
}
</style>
