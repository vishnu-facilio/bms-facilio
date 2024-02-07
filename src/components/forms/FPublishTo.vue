<template>
  <div class="d-flex flex-col">
    <el-form-item label="Publish To" class="section-items publish-to">
      <el-radio-group
        style="text-decoration:none"
        v-model="sharingType"
        @change="clearFieldValues"
      >
        <div class="pB20 pT10" style="text-decoration:none">
          <el-radio
            style="text-decoration:none"
            :label="sharingTypes.BUILDING"
            class="fc-radio-btn"
          >
            <span class="normal-case">
              {{ $t('common._common.building') }}
            </span>
          </el-radio>
          <div v-if="sharingType === sharingTypes.BUILDING" class="mL30 mT20">
            <el-radio-group v-model="sharingSubType" @change="clearFieldValues">
              <el-radio :label="sharingSubTypes.ALL" class="fc-radio-btn">
                <span class="normal-case">
                  {{ $t('common._common.all_building') }}
                </span>
              </el-radio>
              <el-radio
                :label="sharingSubTypes.SELECTED"
                class="fc-radio-btn mL20"
              >
                <span class="normal-case">
                  {{ $t('common._common.selected_building') }}
                </span>
              </el-radio>
            </el-radio-group>
          </div>
        </div>
        <div class="pB20">
          <el-radio :label="sharingTypes.TENANT_UNIT" class="fc-radio-btn">
            <span class="normal-case">
              {{ $t('common._common.selected_tenant_units') }}
            </span>
          </el-radio>
        </div>
        <div class="pB20">
          <el-radio :label="sharingTypes.ROLE" class="fc-radio-btn">
            <span class="normal-case">
              {{ $t('common.products.selected_roles') }}
            </span>
          </el-radio>
          <div v-if="sharingType === sharingTypes.ROLE" class="mL30 mT20">
            <el-radio-group v-model="sharingSubType" @change="clearFieldValues">
              <el-radio :label="sharingSubTypes.ALL" class="fc-radio-btn">
                <span class="normal-case">
                  {{ $t('common._common.all_role') }}
                </span>
              </el-radio>
              <el-radio
                :label="sharingSubTypes.SELECTED"
                class="fc-radio-btn mL20 fc-input-label-txt"
              >
                <span class="normal-case">
                  {{ $t('common._common.selected_roles') }}
                </span>
              </el-radio>
            </el-radio-group>
          </div>
        </div>
        <div class="pB20">
          <el-radio :label="sharingTypes.PEOPLE" class="fc-radio-btn">
            <span class="normal-case">
              {{ $t('common.products.selected_people') }}
            </span>
          </el-radio>
          <div v-if="sharingType === sharingTypes.PEOPLE" class="mL30 mT20">
            <el-radio-group v-model="sharingSubType" @change="clearFieldValues">
              <el-radio :label="sharingSubTypes.ALL" class="fc-radio-btn">
                <span class="normal-case">
                  {{ $t('common._common.all_people') }}
                </span>
              </el-radio>
              <el-radio
                :label="sharingSubTypes.SELECTED"
                class="fc-radio-btn mL20"
              >
                <span class="normal-case">
                  {{ $t('common._common.selected_people') }}
                </span>
              </el-radio>
            </el-radio-group>
          </div>
        </div>
      </el-radio-group>
    </el-form-item>
    <el-form-item
      v-if="sharingType === sharingTypes.TENANT_UNIT"
      label="Select Tenant Units"
      class="section-items"
    >
      <FLookupField
        :key="`${field.name}-tenantunit`"
        :model.sync="sharedToSpace"
        :field="fields.tenantunit"
        @recordSelected="serialize"
        @showLookupWizard="showLookupWizard"
      ></FLookupField>
    </el-form-item>
    <el-form-item
      v-if="
        sharingType === sharingTypes.ROLE &&
          sharingSubType === sharingSubTypes.SELECTED
      "
      label="Select Roles"
      class="section-items"
    >
      <el-select
        filterable
        v-model="sharedToRole"
        multiple
        collapse-tags
        class="fc-input-full-border-select2 width100 fc-tag"
        @change="serialize"
      >
        <el-option
          v-for="role in roles"
          :key="role.value"
          :label="role.label"
          :value="role.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item
      v-if="
        sharingType === sharingTypes.PEOPLE &&
          sharingSubType === sharingSubTypes.SELECTED
      "
      label="Select People"
      class="section-items"
    >
      <FLookupField
        :key="`${field.name}-people`"
        :model.sync="sharedToPeople"
        :fetchOptionsOnLoad="true"
        :field="fields.people"
        @recordSelected="serialize"
        @showLookupWizard="showLookupWizard"
      ></FLookupField>
    </el-form-item>
    <el-form-item
      v-if="
        sharingType === sharingTypes.BUILDING &&
          sharingSubType === sharingSubTypes.SELECTED
      "
      label="Select Building"
      class="section-items"
    >
      <FLookupField
        :key="`${field.name}-building`"
        :model.sync="sharedToBuilding"
        :fetchOptionsOnLoad="true"
        :field="fields.building"
        @recordSelected="serialize"
        @showLookupWizard="showLookupWizard"
      ></FLookupField>
    </el-form-item>
    <el-form-item>
      <el-row>
        <el-col :span="9">
          <el-checkbox
            v-if="showRoleFilter"
            v-model="showRoleFilterField"
            @change="serialize()"
            style="padding-bottom: 15px;padding-left: 135px;padding-top: 10px;width:100%;float:left;"
            >{{ $t('common._common.add_role_filter') }}</el-checkbox
          >
        </el-col>
        <el-col :span="15">
          <div
            style="float:left;width:100%"
            v-if="showRoleFilterField"
            label="Select Roles"
            class="section-items"
          >
            <el-select
              filterable
              v-model="sharedToRoleFilter"
              multiple
              collapse-tags
              class="fc-input-full-border-select2 width100 fc-tag"
              @change="serialize"
            >
              <el-option
                v-for="role in roles"
                :key="role.value"
                :label="role.label"
                :value="role.value"
              ></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
    </el-form-item>

    <FLookupFieldWizard
      v-if="canShowLookupWizard"
      :canShowLookupWizard.sync="canShowLookupWizard"
      :selectedField="selectedLookupField"
      :key="selectedLookupField.lookupModuleName"
      :withReadings="true"
      @setLookupFieldValue="setLookupFieldValue"
      :specialFilterValue="specialFilterValue"
      :showFilters="true"
    ></FLookupFieldWizard>
  </div>
</template>
<script>
import FLookupField from '@/forms/FLookupField'
import FLookupFieldWizard from 'src/pages/community/components/LookUpSpecialWizardAudience.vue'
import { isEmpty } from '@facilio/utils/validation'
import isEqual from 'lodash/isEqual'
import { API } from '@facilio/api'

const sharingTypes = {
  TENANT_UNIT: 1,
  ROLE: 2,
  PEOPLE: 3,
  BUILDING: 4,
}

const sharingSubTypes = {
  ALL: 1,
  SELECTED: 2,
}

const fields = {
  building: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'building',
    field: {
      lookupModule: {
        name: 'building',
        displayName: 'Buildings',
      },
    },
    forceFetchAlways: true,
    filters: {},
    multiple: true,
    isDisabled: false,
  },
  tenantunit: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'tenantunit',
    field: {
      lookupModule: {
        name: 'tenantunit',
        displayName: 'Tenant Unit',
      },
    },
    forceFetchAlways: true,
    filters: {},
    multiple: true,
    isDisabled: false,
  },
  role: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'role',
    field: {
      lookupModule: {
        name: 'role',
        displayName: 'Roles',
      },
    },
    forceFetchAlways: true,
    filters: {},
    multiple: true,
    isDisabled: false,
  },
  people: {
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
    filters: {},
    multiple: true,
    isDisabled: false,
  },
}

export default {
  props: ['value', 'field', 'isEdit', 'formModel'],
  components: { FLookupField, FLookupFieldWizard },
  data() {
    return {
      publishTo: [
        {
          sharingType: sharingTypes.BUILDING,
        },
      ],
      chooserVisibility: true,
      originalData: [],
      sharingTypes,
      fields,
      roles: [],
      selectedLookupField: null,
      canShowLookupWizard: false,
      sharingTypeSelection: 1,
      sharingSubTypes,
      sharingSubType: sharingSubTypes.ALL,
      showRoleFilterField: false,
      sharingType: sharingTypes.BUILDING,
      roleFilterSharingInfo: [],
    }
  },
  async created() {
    await this.loadRoles()
  },
  mounted() {
    if (this.isEdit) {
      this.originalData = this.value
      this.initValues()
    } // for finding deleted Ids
  },
  watch: {
    value: {
      handler: function(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) this.deserialize()
      },
      immediate: true,
    },
  },
  computed: {
    showRoleFilter() {
      let { sharingType } = this
      let { sharingSubType } = this
      if (
        sharingType === sharingTypes.TENANT_UNIT ||
        (sharingType == sharingTypes.BUILDING &&
          sharingSubType === sharingSubTypes.SELECTED)
      ) {
        return true
      }
      return false
    },
    specialFilterValue() {
      let { selectedLookupField } = this
      let { lookupModuleName } = selectedLookupField
      return lookupModuleName === 'people'
        ? {
            peopleType: { operatorId: 54, value: ['1', '2'] },
          }
        : {}
    },
    sharedToBuilding: {
      get() {
        return this.publishTo
          .filter(({ sharedToSpace }) => !isEmpty(sharedToSpace))
          .map(({ sharedToSpace }) => sharedToSpace?.id)
      },
      set(values) {
        let publishTo = isEmpty(values)
          ? null
          : values
              .filter(id => !isEmpty(id))
              .map(id => {
                let sharingType = sharingTypes.BUILDING
                return {
                  sharingType,
                  sharedToSpace: {
                    id,
                  },
                }
              })
        this.publishTo = !isEmpty(publishTo)
          ? publishTo
          : [
              {
                sharingType: sharingTypes.BUILDING,
              },
            ]
      },
    },
    sharedToSpace: {
      get() {
        return this.publishTo
          .filter(({ sharedToSpace }) => !isEmpty(sharedToSpace))
          .map(({ sharedToSpace }) => sharedToSpace?.id)
      },
      set(values) {
        let publishTo = isEmpty(values)
          ? null
          : values
              .filter(id => !isEmpty(id))
              .map(id => {
                let sharingType = sharingTypes.TENANT_UNIT

                return {
                  sharingType,
                  sharedToSpace: {
                    id,
                  },
                }
              })
        this.publishTo = !isEmpty(publishTo)
          ? publishTo
          : [
              {
                sharingType: sharingTypes.TENANT_UNIT,
              },
            ]
      },
    },
    sharedToRole: {
      get() {
        return this.publishTo
          .filter(({ sharedToRole }) => !isEmpty(sharedToRole))
          .map(({ sharedToRole }) => sharedToRole?.id)
      },
      set(values) {
        let publishTo = values
          .filter(id => !isEmpty(id))
          .map(id => {
            let sharingType = sharingTypes.ROLE

            return {
              sharingType,
              sharedToRole: {
                id: id,
                roleId: id,
              },
            }
          })

        this.publishTo = !isEmpty(publishTo)
          ? publishTo
          : [
              {
                sharingType: sharingTypes.ROLE,
              },
            ]
      },
    },
    sharedToPeople: {
      get() {
        return this.publishTo
          .filter(({ sharedToPeople }) => !isEmpty(sharedToPeople))
          .map(({ sharedToPeople }) => sharedToPeople?.id)
      },
      set(values) {
        let publishTo = values
          .filter(id => !isEmpty(id))
          .map(id => {
            let sharingType = sharingTypes.PEOPLE

            return {
              sharingType,
              sharedToPeople: {
                id,
              },
            }
          })
        this.publishTo = !isEmpty(publishTo)
          ? publishTo
          : [
              {
                sharingType: sharingTypes.PEOPLE,
              },
            ]
      },
    },
    sharedToRoleFilter: {
      get() {
        return this.roleFilterSharingInfo
          .filter(({ sharedToRole }) => !isEmpty(sharedToRole))
          .map(({ sharedToRole }) => sharedToRole?.id)
      },
      set(values) {
        let publishTo = values
          .filter(id => !isEmpty(id))
          .map(id => {
            let sharingType = sharingTypes.ROLE
            return {
              sharingType,
              sharedToRole: {
                id: id,
                roleId: id,
              },
            }
          })
        if (!isEmpty(publishTo)) {
          this.roleFilterSharingInfo = publishTo
        } else {
          this.roleFilterSharingInfo = []
        }
      },
    },
  },
  methods: {
    setLookupFieldValue(value) {
      let { sharingType } = this
      let { field } = value
      let { selectedItems } = field
      if (sharingType === sharingTypes.PEOPLE) {
        this.sharedToPeople = selectedItems.map(item => item.value)
      } else {
        this.sharedToSpace = selectedItems.map(item => item.value)
      }
      this.serialize()
      this.canShowLookupWizard = false
    },
    showLookupWizard(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizard', canShow)
    },
    async loadRoles() {
      this.loading = true
      let filters = JSON.stringify({
        appLinkNames: ['tenant', 'vendor', 'service'],
      })

      let { error, data } = await API.get('/v3/picklist/role', {
        filters,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { pickList } = data || {}
        this.roles = pickList || []
      }
      this.loading = false
    },
    clearFieldValues() {
      this.showRoleFilterField = false
      let { sharingType } = this
      let sharingSubType = sharingSubTypes.ALL
      if (sharingType == sharingTypes.TENANT_UNIT) {
        sharingSubType = sharingSubTypes.SELECTED
      }
      this.publishTo = [{ sharingType, sharingSubType }]
      this.serialize()
    },
    getDeletedIds() {
      let { originalData, publishTo, $getProperty, sharingType } = this

      if (isEmpty(originalData)) return []

      if (sharingType === sharingTypes.TENANT_UNIT) return []

      let removedItems = originalData.filter(shareObj => {
        let { sharingType, sharedToSpace } = shareObj

        return isEmpty(
          publishTo.find(p => {
            return (
              p.sharingType === sharingType &&
              $getProperty(p, 'sharedToSpace.id') ===
                $getProperty(sharedToSpace, 'id')
            )
          })
        )
      })

      return removedItems.map(({ id }) => id)
    },
    getSharingTypeWithAppliedFilter(filterSharingType) {
      if (
        !isEmpty(filterSharingType) &&
        filterSharingType == sharingTypes.ROLE
      ) {
        let publishTo = this.publishTo.filter(function(publishInfo) {
          return publishInfo.sharingType != sharingTypes.ROLE
        })
        return this.$getProperty(publishTo, '0.sharingType')
      }
      return this.$getProperty(this.publishTo, '0.sharingType')
    },
    initValues() {
      let { formModel } = this
      let { filterSharingType } = formModel || {}
      this.sharingType = this.getSharingTypeWithAppliedFilter(filterSharingType)
      if (filterSharingType && filterSharingType == sharingTypes.ROLE) {
        this.showRoleFilterField = true
      }
      let { value: fieldValue } = this

      if (isEmpty(fieldValue)) {
        this.serialize()
      } else if (!isEmpty(fieldValue)) {
        fieldValue.forEach(fieldVal => {
          if (
            isEmpty(fieldVal.sharedToSpace) &&
            isEmpty(fieldVal.sharedToRole) &&
            isEmpty(fieldVal.sharedToPeople)
          ) {
            this.sharingSubType = sharingSubTypes.ALL
          } else {
            this.sharingSubType = sharingSubTypes.SELECTED
          }
        })
        if (filterSharingType && filterSharingType == sharingTypes.ROLE) {
          this.publishTo = fieldValue.filter(function(publishInfo) {
            return publishInfo.sharingType != sharingTypes.ROLE
          })
          this.roleFilterSharingInfo = fieldValue.filter(function(publishInfo) {
            return publishInfo.sharingType == sharingTypes.ROLE
          })
        } else {
          this.publishTo = fieldValue
        }
      }
    },
    deserialize() {
      let { value: fieldValue } = this

      if (isEmpty(fieldValue)) {
        this.serialize()
      } else if (!isEmpty(fieldValue)) {
        this.publishTo = fieldValue
      }
    },

    serialize() {
      let { roleFilterSharingInfo } = this
      publishTo = (this.publishTo || []).filter(function(publishInfo) {
        return publishInfo.sharingType != sharingTypes.ROLE
      })
      if (this.showRoleFilterField) {
        this.publishTo = (publishTo || []).concat(roleFilterSharingInfo)
      }
      let {
        publishTo,
        getDeletedIds,
        sharingSubType,
        showRoleFilterField,
      } = this
      if (!showRoleFilterField) {
        this.roleFilterSharingInfo = []
      }
      let sharingType = publishTo[0].sharingType
      let fieldValue
      if (
        sharingSubType === sharingSubTypes.ALL &&
        sharingType != sharingTypes.TENANT_UNIT
      ) {
        fieldValue = [
          {
            sharingType,
          },
        ]
      } else {
        fieldValue = publishTo.map(shareObj => ({
          ...shareObj,
        }))
      }
      this.$emit('input', fieldValue)
      if (showRoleFilterField) {
        this.formModel['filterSharingType'] = sharingTypes.ROLE
      } else {
        this.formModel['filterSharingType'] = null
      }
      if (!isEmpty(getDeletedIds())) {
        this.formModel[`${this.field.name}_delete`] = getDeletedIds()
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.f-webform-container .el-form.el-form--label-top {
  .section-container .section-items.publish-to {
    margin-bottom: 10px;
  }
}
</style>
g
