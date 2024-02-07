<template>
  <div
    class="d-flex flex-direction-column asset-sum-empty-block"
    :class="[newSiteSummary ? 'site-readings-list' : '']"
    style="height: 100%; overflow-y: scroll;"
  >
    <!-- Widget TopBar Section -->
    <template v-if="isActive">
      <portal :to="portalName" :key="portalName + '-portalwrap'" slim>
        <f-search
          v-if="isActive"
          v-model="list"
          class="mR6"
          searchKey="name"
          :key="portalName + type + '-search'"
        ></f-search>
        <span class="separator">|</span>
        <div
          @click="listRefresh"
          v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
          content="Refresh"
          >
          <i class="el-icon-refresh fwBold f16"></i>
        </div>
      </portal>
    </template>
 
    <div v-if="loading" class="height100 hv-center " >
        <spinner :show="loading"></spinner>
    </div>
    <div v-else-if="$validation.isEmpty(list)"  class="height100 hv-center " >
        <div >
          <InlineSvg
            src="svgs/emptystate/readings-empty"
            iconClass="icon text-center icon-130 emptystate-icon-size"
          ></InlineSvg>
          <div class="fc-black-dark f18 bold">
            {{ $t('asset.readings.no_readings_available') }}
          </div>
              
        </div>
    </div>
    <div v-else >
        <el-table
        :data="list" 
        :fit="true" 
        :header-cell-style="headerCellStyle"
        style="width:100%"
        height="330px" 
        :cell-style="{ fontSize: '14px' }">
          >
          <el-table-column :label="$t('asset.readings.name')" width="300">
            <template slot-scope="reading">
              <div class="align-name-field">
                {{reading.row.field.displayName}}
              </div>
            </template>
          </el-table-column>

          <el-table-column :label="$t('asset.readings.value')">
            <template slot-scope="reading">
              <div class="pL30"
                  v-html="
                    displayReadings(
                      reading.row.field,
                      reading.row.value,
                      reading.row.actualValue
                    )
                  "
                ></div>
            </template>
          </el-table-column>

          <el-table-column :label="$t('asset.readings.last_recorded')">
            <template slot-scope="reading">
              <div class="pL30"
                v-if="
                  reading.row.ttime > 0 &&
                    !isValueEmpty(
                      reading.row.field,
                      reading.row.value,
                      reading.row.actualValue
                    )
                "
              >
                {{ reading.row.ttime | fromNow }}
            </div>
            <div v-else class="pL30">{{ '---' }}</div>
            </template>
          </el-table-column>

          <el-table-column  v-if="$helpers.isLicenseEnabled('ENERGY') && $hasPermission('energy:CREATE_EDIT_REPORTS,VIEW_REPORTS,CREATE_EDIT_DASHBOARD,EXPORT_REPORTS')"
            :label="$t('asset.readings.analytics')" 
          >
          <template slot-scope="reading">
            <div class="pL30">
                <a
                  class="f13 flex align-center"
                  @click="
                    goToAnalytics(
                      assetId,
                      reading.row.field.id,
                      {
                        operatorId: 63,
                        value: reading.row.ttime ? reading.row.ttime + '' : null,
                      },
                      reading.row
                    )
                  "
                  >{{ $t('asset.readings.go_to_analytics') }}
                  <InlineSvg
                    src="svgs/black-arrow-right"  
                    class="pointer flex mL5"
                    iconClass="icon icon-xs"
                  ></InlineSvg>
                </a>
            </div>    
          </template>
          </el-table-column>

          <el-table-column align="center">
            <template slot-scope="reading">
            <div v-if="canShowAddReading(reading.row)">
                <a
                  class="f13 pL30"
                  @click="
                    showAddReadingDialog(
                      reading.row.field.module.name,
                      reading.row.field
                    )
                  "
                  >+ {{ $t('asset.readings.add') }}</a
                >
              </div>
              <div v-if="reading.row.controllable" class="visibility-hide-actions">
                <el-dropdown
                  class="mL50"
                  style="padding-top: 5px; padding-bottom: 5px;position: relative; top: 5px;"
                  trigger="click"
                >
                  <img
                    src="~assets/svgs/menu.svg"
                    width="14"
                    height="14"
                    class="vertical-text-super"
                  />
                  <el-dropdown-menu slot="dropdown" trigger="click" class="p10">
                    <div
                      class="pT5 pB5 fc-label-hover label-txt-black f14 pL10 pR10 pointer"
                      @click="updateAction(reading.row)"
                    >
                      {{
                        reading.row.isControllable
                          ? 'Disable Control Command'
                          : 'Enable Control Command'
                      }}
                    </div>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </template>  
          </el-table-column>

        </el-table>
      </div>
   
    <AddReadingPopup
      v-if="showReadingDialog"
      :reading="editObj"
      :saveAction="updateReadings"
      :closeAction="closePopup"
      :recordId="assetId"
      :moduleName="moduleName"
    ></AddReadingPopup>
  </div>
</template>

<script>
import FSearch from '@/FSearch'

import JumpToHelper from '@/mixins/JumpToHelper'
import ControlCommandMixin from '@/mixins/ControlCommandMixin'
import AddReadingPopup from '@/readings/AddReadingValue'
import {
  isBooleanField,
  isDecimalField,
  isEnumField,
  isDateField,
  isDateTimeField,
} from '@facilio/utils/field'
import { isEmpty, isNullOrUndefined } from '@facilio/utils/validation'
import SpaceMixin from 'pages/spacemanagement/overview/helpers/SpaceHelper'

export default {
  mixins: [JumpToHelper, ControlCommandMixin, SpaceMixin],
  components: { FSearch, AddReadingPopup },
  props: [
    'type',
    'url',
    'isActive',
    'portalName',
    'assetId',
    'resize',
    'reset',
    'details',
    'moduleName',
  ],

  mounted() {
    this.loadData()
    this.loadCount()
  },

  data() {
    return {
      loading: false,
      list: null,
      totalCount: null,
      canShowAllReadings: true,
      defaultListSize: 6,
      editObj: {},
      showReadingDialog: false,
    }
  },

  computed: {
    filteredList() {
      let sortedList = [...this.list].sort((a, b) => {
        if (
          a.field.displayName.toLowerCase() > b.field.displayName.toLowerCase()
        ) {
          return 1
        }
        if (
          b.field.displayName.toLowerCase() > a.field.displayName.toLowerCase()
        ) {
          return -1
        }
        return 0
      })

      if (this.canShowAllReadings) {
        return sortedList
      } else {
        return sortedList.slice(0, this.defaultListSize)
      }
    },
    canShowViewMore() {
      return (
        this.list &&
        this.list.length > 0 &&
        this.list.length > this.defaultListSize
      )
    },
    showMoreLinkText() {
      return this.canShowAllReadings ? 'View Less' : 'View More'
    },
    setValue() {
      return this.$store.state.publishdata.setValue[this.assetId]
    },
    hasModuleAddReadingPermission() {
      if (['site', 'building', 'space'].includes(this.moduleName)) {
        if (this.$hasPermission('space:ADD_READING')) return true
        return false
      }
      return true
    },
  },

  methods: {
    toggleVisibility() {
      this.canShowAllReadings = !this.canShowAllReadings

      this.$nextTick(() => {
        if (this.canShowAllReadings) {
          this.resize()
        } else {
          this.reset()
        }
      })
    },

    updateAction(field) {
      field.isControllable = !field.isControllable
      this.controlActionChangeStatus(field, this.assetId, field.isControllable)
    },

    loadData() {
      this.loading = true

      let promise = this.$http
        .post(`/v2/reading/latestdata/${this.assetId}`, {
          readingType: this.type,
        })
        .then(response => {
          if (response.data.responseCode === 0) {
            let list = (response.data.result.readingValues || []).map(
              reading => ({
                name: reading.field.displayName,
                ...reading,
              })
            )
            this.list = list
          } else {
            this.list = []
          }
        })
        .catch(() => (this.list = null))

      Promise.all([promise]).finally(() => (this.loading = false))
    },

    loadCount() {
      this.loading = true

      this.$http
        .get(
          `v2/reading/latestdata/${this.assetId}?readingType=${this.type}&fetchCount=true`
        )
        .then(response => {
          if (response.data.responseCode === 0) {
            this.totalCount = response.data.result.count
          } else {
            this.totalCount = null
          }
        })
        .catch(() => (this.totalCount = null))
    },

    isValueEmpty(field, value, actualValue) {
      if (isBooleanField(field))
        return (
          value === null ||
          value === undefined ||
          isNullOrUndefined(actualValue)
        )
      else if (Number(value) === -1) return true
      else return isEmpty(value)
    },

    displayReadings(field, value, actualValue) {
      if (Number(value) === -1) {
        return '--'
      } else if (isDecimalField(field)) {
        let unit = field.unit ? field.unit : ''
        return value.toFixed(2) + ' ' + unit
      } else if (isDateTimeField(field)) {
        return this.$options.filters.formatDate(value)
      } else if (isDateField(field)) {
        return this.$options.filters.formatDate(value, true)
      } else if (isBooleanField(field)) {
        if (
          isNullOrUndefined(value) ||
          value === -1 ||
          isNullOrUndefined(actualValue)
        )
          return '--'
        return value ? field.trueVal || 'True' : field.falseVal || 'False'
      } else if (isEnumField(field)) {
        return field.enumMap[parseInt(value)]
      } else {
        return value
      }
    },

    showAddReadingDialog(readingName, field) {
      this.editObj.readingName = readingName
      this.editObj.field = field
      this.showReadingDialog = true
    },
    closePopup() {
      this.showReadingDialog = false
      this.resetReadingObj()
    },
    resetReadingObj() {
      this.editObj = {
        ttime: null,
        value: null,
      }
    },

    updateReadings() {
      this.loadData()
      this.loadCount()
      this.closePopup()
    },

    goToAnalytics(assetId, fieldId, dateFilter, reading) {
      let buildingId = this.details.space ? this.details.space.buildingId : null
      let aggr =
        reading.field &&
        reading.field.unit &&
        ['currency', 'kwh', 'co2', 'kg', 'mwh'].includes(
          reading.field.unit.trim().toLowerCase()
        )
          ? 3
          : 2
      this.jumpReadingToAnalytics(
        assetId,
        fieldId,
        dateFilter,
        null,
        aggr,
        buildingId
      )
    },
    canShowAddReading(reading) {
      return (
        Number(reading.inputType) === 1 && this.hasModuleAddReadingPermission
      )
    },
    listRefresh(){
      this.loadData()
      this.loadCount()
    },
    headerCellStyle() {
      return {
        'padding-left': '30px', 'background-color': '#f7faff'
      };
    },
  },
  watch: {
    setValue: {
      handler: 'updateReadings',
      deep: true,
    },
  },
}
</script>
<style lang="scss" scoped>
.view-more-pane {
  margin-top: auto;
  position: fixed;
  width: 100%;
  bottom: 0;
  padding: 15px 0 15px;
  background-color: #fff;
  box-shadow: 0px -5px 18px 0 rgba(233, 233, 226, 0.5);
}
.site-readings-list {
  thead {
    tr {
      position: sticky;
      top: 0;
      z-index: 1;
    }
    th {
      background: #f3f1fc;
    }
  }
}
</style>
<style lang="scss">
.site-readings-widget {
  .el-icon-search {
    &:not(.el-input__icon) {
      margin-top: 15px;
    }
  }
  .el-tabs__nav-wrap {
    padding-left: 20px;
  }
}
</style>
<style lang="scss" scoped>
  .mR6{
    margin-right: 6px !important;
  }
  .align-name-field{
  padding-left: 30px;
  display: inline-block;
}

.hv-center {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
