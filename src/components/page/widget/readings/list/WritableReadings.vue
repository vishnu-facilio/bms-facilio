<template>
  <div class="scrollable">
    <template v-if="isActive">
      <portal :to="portalName" :key="portalName + '-portalwrap'" slim>

        <f-search 
          v-if="isActive" 
          v-model="list" 
          searchKey="name" 
          class="mR6"
          :key="portalName + '-search'">
        </f-search>
        <span v-if="isActive" 
          class="separator">|
        </span>
        <div
          @click="listRefresh"
          v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
          :content="$t('common._common.refresh')"
          >
          <i class="el-icon-refresh fwBold f16"></i>
        </div>

      </portal>
    </template>

    <div v-if="loading" class="height100 hv-center ">
      <spinner :show="loading"></spinner>
    </div>
    <div v-else-if="$validation.isEmpty(list)" class="height100 hv-center">
      <div>
        <div >
          <InlineSvg src="svgs/emptystate/readings-empty" iconClass="icon text-center icon-130 emptystate-icon-size">
          </InlineSvg>
        </div>
        <div class="fc-black-dark f18 bold">
          {{ $t('asset.readings.no_readings_available') }}
        </div>
      </div>
    </div>
    <div v-else >
      <el-table v-if="!$validation.isEmpty(list)" 
        :data="list" 
        :fit="true" 
        :header-cell-style="headerCellStyle"
        style="width:100%"
        height="330px" 
        :cell-style="{ fontSize: '14px' }">

        <el-table-column label="Name" width="300">
          <template slot-scope="reading">
            <span class="align-name-field">
              {{ $getProperty(reading, 'row.field.displayName','---') }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="Value" width="200">
          <template slot-scope="reading">
            <div class="pL30">
              <template v-if="reading.row.value == null">
                {{reading.row.value}}
              </template>
              <template v-else-if="isDecimalField(reading.row.field)">
                {{ Number(reading.row.value).toFixed(1)}}{{ reading.row.field.unit }}                
              </template>
              <template v-else-if="isBooleanField(reading.row.field)">
                {{
                  $fieldUtils.getDisplayValue(
                    reading.row.field,
                    reading.row.value === 'true' || reading.row.value === '1'
                  )
                }}
              </template>              
              <template v-else>
                {{
                  $fieldUtils.getDisplayValue(
                    addEnumMapField(reading.row.field),
                    reading.row.value
                  )
                }}
              </template>
            </div>
          </template></el-table-column>

        <el-table-column label="Last Recorded" width="200">
          <template slot-scope="reading">
            <span class="pL30">{{ getTimeFromNow(reading) }}</span>
          </template>
        </el-table-column>

        <el-table-column>
          <template slot-scope="reading" >
            <a @click="showSetReadingDialog(reading)" class="pL30">
              {{$t('asset.readings.set')}}
            </a>
          </template>
        </el-table-column>

      </el-table>

      <SetReadingPopup v-if="showSetDialog" 
        :key="newReading.field.id" 
        :reading="newReading" 
        :saveAction="closePopup"
        :closeAction="closePopup" 
        :recordId="details.id" 
        :recordName="details.name">
      </SetReadingPopup>

    </div>
  </div>
</template>

<script>
import FSearch from '@/FSearch'
import { isEmpty } from '@facilio/utils/validation'
import { fromNow } from 'src/util/filters'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import isEqual from 'lodash/isEqual'
import SetReadingPopup from '@/readings/SetReadingValue'
import { API } from '@facilio/api'
import { isBooleanField, isDecimalField  } from '@facilio/utils/field'
export default {
  components: { FSearch, Pagination, SetReadingPopup },
  props: [
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
  },
  created() {
    this.isBooleanField = isBooleanField
    this.isDecimalField = isDecimalField
  },
  data() {
    return {
      loading: false,
      list: null,
      showSetDialog: false,
      newReading: {
        field: null,
        value: null
      },
      readingType:'writable'
    }
  },

  computed: {
    setValue() {
      let data = this.$store.state.publishdata.setValue[this.details.id]
      return data ? data[this.reading.field.id] : null
    },
  },
  watch: {
    widget: {
      immediate: true,
      handler: 'loadData',
    },
    setValue: {
      handler: 'setFieldValue',
    },
  },

  methods: {
    async loadData(force){
      this.loading = true
      let url = `/v2/reading/latestdata/${this.assetId}`
      let params = {readingType:this.readingType}
      let{error,data} = await API.get(url,params,{force})
      if(error){
        this.$message.error(error.message || 'Error Occured')
        this.loading = false
      }
      else{
        let list = (data.readingValues || []).map(reading => ({
          name : reading.field.displayName,
          ...reading,
        }))
        this.list = list
        this.loading = false
      }
    },
    addEnumMapField(fields) {
      let enumMap = fields.values.reduce((acc, object) => {
        acc[object.index.toString()] = object.value
        return acc
      }, {})
      fields.enumMap = enumMap
      return fields
    },
    getTimeFromNow(readingRow) {
      let reading = this.$getProperty(readingRow, 'row')
      if (!isEmpty(reading)) {
        let { actualValue, ttime } = reading
        return !isEmpty(actualValue) ? fromNow(ttime) : '---'
      }
      return '---'
    },
    showSetReadingDialog(readingRow) {
      let reading = this.$getProperty(readingRow, 'row')
      let { newReading } = this
      newReading.field = reading.field
      newReading.value = reading.value
      this.showSetDialog = true
    },
    closePopup() {
      this.showSetDialog = false
      this.resetEditObj()
    },
    resetEditObj() {
      this.newReading = {
        field: null,
        value: null,
      }
    },
    headerCellStyle() {
      return {
        'padding-left': '30px', 'background-color': '#f7faff'
      };
    },
    listRefresh(){
      this.loadData(true)
    },
    setFieldValue() {
      if (this.setValue) {
        this.reading.value = this.setValue
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.hv-center {
  display: flex;
  align-items: center;
  justify-content: center;
}
.align-name-field{
  padding-left: 30px;
  display: inline-block;
}
.mR6{
  margin-right: 6px !important;
}
.scrollable{
  height: 100%;
  overflow-y: scroll;
}
</style>