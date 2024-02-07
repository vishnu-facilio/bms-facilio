<template>
  <el-dialog
    :visible="dialogVisibility"
    :fullscreen="true"
    :append-to-body="true"
    :width="'75%'"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog55 setup-dialog fc-tabs-layout-page"
    style="z-index: 999999"
  >
    <el-form
      :model="labourFormDetails"
      ref="labourForm"
      class="fc-form"
      :rules="rules"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{
              isNew
                ? $t('setup.labour.form.add_labour')
                : $t('setup.labour.form.edit_labour')
            }}
          </div>
        </div>
      </div>

      <div v-if="loading" class="new-body-modal">
        <div v-for="index in [1, 2, 3, 4, 5]" :key="index">
          <el-row class="mB20">
            <el-col :span="24">
              <span class="lines loading-shimmer width50 mB10"></span>
              <span class="inputlines loading-shimmer"></span>
            </el-col>
          </el-row>
        </div>

        <el-row class="mB10 mT10">
          <el-col :span="24">
            <span class="lines loading-shimmer width50 mB10"></span>
            <div class="mT10 d-flex">
              <div
                v-for="index in [1, 2, 3]"
                :key="index"
                class="flex-middle flex-grow"
              >
                <span class="circle loading-shimmer"></span>
                <span class="lines loading-shimmer mL10 mR30 flex-grow"></span>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <div v-else class="new-body-modal">
        <div class="body-scroll">
          <el-row>
            <el-col :span="24">
              <el-form-item prop="name">
                <p class="labour-input-label">
                  {{ $t('setup.labour.form.name') }}
                  <span class="mandatory-field-color">*</span>
                </p>

                <el-input
                  class="width100 fc-input-full-border2 labour-input"
                  autofocus
                  v-model="labourFormDetails.name"
                  type="text"
                  :placeholder="$t('setup.labour.form.name')"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item prop="siteId">
                <p class="labour-input-label">
                  {{ $t('setup.labour.form.site') }}
                </p>
                <FLookupField
                  :model.sync="labourFormDetails.siteId"
                  :hideLookupIcon="true"
                  :field="siteLookup"
                  class="quick-filter"
                ></FLookupField>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item prop="peopleId">
                <p class="labour-input-label">
                  {{ $t('setup.labour.form.people') }}
                  <span class="mandatory-field-color">*</span>
                </p>
                <FLookupField
                  :model.sync="labourFormDetails.peopleId"
                  :disabled="fromPeople"
                  :field="peopleLookup"
                  @recordSelected="setPeopleEmailandPhone"
                  @showLookupWizard="showLookupFieldWizard = true"
                  class="quick-filter"
                ></FLookupField>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item prop="email">
                <p class="labour-input-label">
                  {{ $t('setup.labour.form.email') }}
                </p>
                <el-input
                  class="width100 fc-input-full-border2"
                  autofocus
                  :disabled="disableEmail"
                  v-model="labourFormDetails.email"
                  type="text"
                  :placeholder="$t('setup.labour.form.email')"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item prop="phone">
                <p class="labour-input-label">
                  {{ $t('setup.labour.form.phone') }}
                </p>

                <el-input
                  class="width100 fc-input-full-border2"
                  autofocus
                  :disabled="!showEmail"
                  v-model="labourFormDetails.phone"
                  type="number"
                  :placeholder="$t('setup.labour.form.phone')"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <p class="labour-label">
                {{ $t('setup.labour.form.Address') }}
              </p>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item>
                <p class="labour-input-label">
                  {{ $t('setup.setup_profile.street') }}
                </p>
                <el-input
                  v-model="labourFormDetails.location.street"
                  :placeholder="$t('setup.setup_profile.enter_street')"
                  class="fc-input-full-border-select2"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item>
                <p class="labour-input-label">
                  {{ $t('setup.setup_profile.city') }}
                </p>
                <el-input
                  v-model="labourFormDetails.location.city"
                  :placeholder="$t('setup.setup_profile.entre_city')"
                  class="fc-input-full-border-select2"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item>
                <p class="labour-input-label">
                  {{ $t('setup.setup_profile.state') }}
                </p>
                <el-input
                  v-model="labourFormDetails.location.state"
                  :placeholder="$t('setup.setup_profile.enter_state')"
                  class="fc-input-full-border-select2"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item>
                <p class="labour-input-label">
                  {{ $t('setup.setup_profile.zipcode') }}
                </p>
                <el-input
                  v-model="labourFormDetails.location.zip"
                  type="number"
                  :placeholder="$t('setup.setup_profile.enter_zipcode')"
                  class="fc-input-full-border-select2"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="profile-input-group pB20">
                <p class="labour-input-label">
                  {{ $t('setup.setup_profile.country') }}
                </p>
                <el-select
                  filterable
                  clearable
                  v-model="labourFormDetails.location.country"
                  :placeholder="$t('setup.setup_profile.enter_country')"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="country in countryList"
                    :key="country.value"
                    :label="country.label"
                    :value="String(country.value)"
                  >
                  </el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <div class="d-flex ">
                <p class="labour-input-label">
                  {{ $t('setup.labour.form.rate_per_hour') }}
                </p>
                <el-tooltip effect="dark" placement="top" class="mT15 mL5">
                  <div slot="content">
                    {{ $t('setup.labour.tooltipcontent1') }} <br />
                    {{ $t('setup.labour.tooltipcontent2') }} <br />
                    {{ $t('setup.labour.tooltipcontent3') }}
                  </div>
                  <inline-svg
                    src="svgs/info-2"
                    iconClass="icon icon-xs"
                  ></inline-svg>
                </el-tooltip>
              </div>
              <el-input
                :placeholder="$t('setup.labour.form.enter_rate')"
                type="number"
                v-model="labourFormDetails.cost"
                class="width100 fc-input-full-border2 mB20"
              ></el-input>
            </el-col>
          </el-row>
          <el-row>
            <p class="labour-label">
              {{ $t('setup.labour.form.craftandskills') }}
            </p>
          </el-row>

          <LabourCraftBuilder
            :editLabourCraftSkill.sync="labourFormDetails.labourCraftSkill"
            ref="labourCraftBuilder"
          >
          </LabourCraftBuilder>
        </div>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel mT12">
          {{ $t('common._common.cancel') }}
        </el-button>
        <el-button
          class="modal-btn-save mT12"
          type="primary"
          @click="submitForm()"
        >
          {{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}
        </el-button>
      </div>
    </el-form>
    <template v-if="showLookupFieldWizard">
      <LookupWizard
        v-if="isNewLookupWizardEnabled"
        :canShowLookupWizard.sync="showLookupFieldWizard"
        :field="peopleLookup"
        @setLookupFieldValue="setLookupFieldValue"
      ></LookupWizard>
      <FLookupFieldWizard
        v-else
        :canShowLookupWizard.sync="showLookupFieldWizard"
        :selectedLookupField="peopleLookup"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
    </template>
  </el-dialog>
</template>
<script>
import countries from 'util/data/countries'
import FLookupField from '@/forms/FLookupField'
import LabourCraftBuilder from './LabourCraftBuilder.vue'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { LookupWizard } from '@facilio/ui/forms'

const peopleLookup = {
  isDataLoading: false,
  options: [],
  lookupModuleName: 'people',
  field: {
    lookupModule: {
      name: 'people',
      displayName: 'Peoples',
    },
  },
  forceFetchAlways: true,
  filters: {
    labour: {
      operatorId: 15,
      value: ['false'],
    },
  },
  multiple: false,
  isDisabled: false,
}

const siteLookup = {
  isDataLoading: false,
  options: [],
  lookupModuleName: 'site',
  field: {
    lookupModule: {
      name: 'site',
      displayName: 'Sites',
    },
  },
  filters: {},
  multiple: false,
  forceFetchAlways: true,
  isDisabled: false,
}

export default {
  props: ['editLabourDetails', 'isNew', 'fromPeople'],
  data() {
    return {
      saving: false,
      showLookupFieldWizard: false,
      dialogVisibility: true,
      labourFormDetails: {
        name: null,
        siteId: null,
        peopleId: null,
        location: {
          street: null,
          city: null,
          state: null,
          zip: null,
          country: null,
          lat: 1.1,
          lng: 1.1,
        },
        availability: false,
        email: null,
        phone: null,
        cost: null,
        labourCraftSkill: [
          {
            craftId: null,
            skillId: null,
            rate: null,
            isDefault: false,
            skillOptions: [],
          },
        ],
      },
      peopleLookup,
      siteLookup,
      loading: false,
      countryList: countries,
      showEmail: false,
      rules: {
        name: {
          required: true,
          message: this.$t(`setup.labour.mandatory_fields.name`),
          trigger: 'blur',
        },
        peopleId: {
          required: true,
          validator: function(rule, value, callback) {
            if (isEmpty(value)) {
              callback(
                new Error(this.$t(`setup.labour.mandatory_fields.people`))
              )
            } else callback()
          }.bind(this),
          trigger: 'change',
        },
      },
    }
  },
  components: {
    FLookupField,
    FLookupFieldWizard,
    LookupWizard,
    LabourCraftBuilder,
  },
  async created() {
    await this.initCraft()
  },
  computed: {
    isNewLookupWizardEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_LOOKUP_WIZARD')
    },
    disableEmail() {
      let { showEmail, fromPeople } = this
      return !showEmail || fromPeople
    },
  },
  methods: {
    setLookupFieldValue(field) {
      let selectedItems = this.$getProperty(field, 'field.selectedItems', [])
      if (!isEmpty(selectedItems)) {
        let selectedItem = selectedItems[0]
        this.labourFormDetails.peopleId = selectedItem?.value
        this.setPeopleEmailandPhone(selectedItem)
      }
    },
    async initCraft() {
      let { fromPeople, isNew } = this
      if (isNew && !fromPeople) return

      this.loading = true
      this.labourFormDetails = await this.deserializelabour()
      this.loading = false
    },
    async submitForm() {
      let labourData = this.serializeLabour()

      if (this.isNew) {
        let { error } = await API.createRecord('labour', {
          data: labourData,
        })
        if (error) {
          this.$message.error(
            error.message || `setup.labour.add_failure_message`
          )
        } else {
          this.$message.success(this.$t(`setup.labour.add_success_message`))
          this.$emit('onSubmit')
          this.closeDialog()
        }
      } else {
        let deletingLabourCraft = this.$refs['labourCraftBuilder']
          .deleteLabourCraftList

        if (!isEmpty(deletingLabourCraft)) {
          let { error } = await API.deleteRecord(
            'labourCraftSkill',
            deletingLabourCraft
          )
          if (error) {
            let { message } = error
            this.$message.error(
              message ||
                this.$t(`setup.labour.labourCraft_delete_error_message`)
            )
          } else {
            this.$message.success(
              this.$t(`setup.labour.labourCraft_delete_success_message`)
            )
          }
        }

        let { error } = await API.updateRecord('labour', {
          data: labourData,
          id: this.editLabourDetails.id,
        })
        if (error) {
          this.$message.error(error.message || 'error')
        } else {
          this.$message.success(this.$t(`setup.labour.update_success_message`))
          this.$emit('onSubmit')
          this.closeDialog()
        }
      }
      this.closeDialog()
    },
    async setPeopleEmailandPhone(peopleData) {
      if (isEmpty(peopleData)) {
        this.labourFormDetails.email = ''
        this.labourFormDetails.phone = ''
        this.showEmail = false
      } else {
        let { people, error } = await API.fetchRecord('people', {
          id: peopleData.value,
        })
        if (error) {
          this.$message.error(error.message || 'error')
        } else {
          this.labourFormDetails.email = this.$getProperty(people, 'email', '')
          this.labourFormDetails.phone = this.$getProperty(people, 'phone', '')
          this.showEmail = true
        }
      }
    },
    serializeLabourCraftSkill() {
      let { labourCraftSkill } = this.labourFormDetails
      let filtereLabourCraftSkill = (labourCraftSkill || []).filter(
        craftSkill => !isEmpty(craftSkill.craftId)
      )
      let serializedLabourCraftSkill = filtereLabourCraftSkill.map(
        craftSkill => {
          let { craftId, skillId, rate, id, isDefault } = craftSkill || {}
          let craft = { id: craftId }
          let skill = { id: skillId || null }

          return {
            craft,
            skill,
            rate: isEmpty(rate) ? 0.0 : parseInt(rate),
            isDefault,
            id,
          }
        }
      )
      return serializedLabourCraftSkill
    },
    serializeLabour() {
      let labourData = {
        ...this.labourFormDetails,
        labourCraftSkill: this.serializeLabourCraftSkill(),
      }
      let { peopleId, cost } = labourData || {}
      let people = { id: peopleId }
      delete labourData.peopleId
      labourData.people = people
      labourData.location.zip = parseInt(labourData.location.zip)
      labourData.cost = isEmpty(cost) ? 0.0 : parseInt(cost)
      labourData.phone = parseInt(labourData.phone)
      labourData.availability = true

      return labourData
    },
    async deserializeCraftAndSkills(craftSkills) {
      return await Promise.all(
        (craftSkills || []).map(async labourDetail => {
          let { craft, skill, rate, isDefault = false, id } = labourDetail || {}
          let { id: craftId } = craft || {}
          let { crafts } = await API.fetchRecord('crafts', { id: craftId })

          return {
            id,
            craftId,
            skillId: skill?.id,
            rate,
            isDefault,
            skillOptions: crafts?.skills || [],
          }
        })
      )
    },
    async deserializelabour() {
      let {
        name,
        id,
        siteId,
        email,
        phone,
        cost,
        people,
        labourCrafts,
        location,
        locationId,
      } = this.editLabourDetails || {}
      let { id: peopleId } = people || {}
      let labourCraftSkill = await this.deserializeCraftAndSkills(labourCrafts)

      if (!isEmpty(peopleId)) {
        this.showEmail = true
      }

      let labourObj = {
        name,
        id,
        email,
        phone,
        siteId,
        cost,
        peopleId,
        locationId,
        labourCraftSkill,
      }
      let locationInfo = {}
      let labourCraftSkillList = []

      if (isEmpty(location)) {
        locationInfo = {
          street: null,
          city: null,
          state: null,
          zip: null,
          country: null,
          lat: 1.1,
          lng: 1.1,
        }
      } else {
        locationInfo = location
      }

      if (isEmpty(labourCraftSkill)) {
        labourCraftSkillList = [
          {
            craftId: null,
            skillId: null,
            rate: null,
            isDefault: false,
            skillOptions: [],
          },
        ]
      } else {
        labourCraftSkillList = labourCraftSkill
      }

      labourObj = {
        ...labourObj,
        location: locationInfo,
        labourCraftSkill: labourCraftSkillList,
      }

      return labourObj
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss" scoped>
.mandatory-field-color {
  color: #d54141;
}
.labour-input {
  margin-bottom: 0px;
}
.labour-input-label {
  font-size: 14px;
  font-weight: 500;
  color: #2f4058;
  margin-bottom: 5px;
  margin-top: 10px;
}
.labour-label {
  font-size: 16px;
  font-weight: 500;
  color: #3ab2c1;
  margin-top: 10px;
  margin-bottom: 10px;
}
.lines {
  height: 15px;
  border-radius: 5px;
}
.inputlines {
  width: 95%;
  height: 40px;
}
// .labour-icon{

// }
</style>
