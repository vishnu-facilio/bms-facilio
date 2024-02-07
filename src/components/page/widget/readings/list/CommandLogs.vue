<template>
  <div class="scrollable">
    
    <template >
      <portal :to="portalName" :key="portalName + '-portalwrap'" slim>

        <div v-if="!$validation.isEmpty(list) && showRedirectButton"
          class="pR10"
          style="padding-top: 2px;"
          v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
          content="Redirect to command logs"
          >
          <router-link :to="resolvePath()" >
            <fc-icon group="navigation" name="open-window" size="16"></fc-icon>
          </router-link>
        </div>

        <span v-if="!$validation.isEmpty(list) && showRedirectButton" class="separator" >|</span>
                
        <pagination 
          :currentPage.sync="page" 
          :total="totalCount" 
          :perPage="perPage" 
        >
        </pagination> 

        <span v-if="!$validation.isEmpty(list)" class="separator" >|</span>

        <div
          v-if="!$validation.isEmpty(list)"
          style="padding-top: 2px;"
          @click="listRefresh"
          v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
          :content="$t('common._common.refresh')"
          >
          <i class="el-icon-refresh fwBold f16"></i>
        </div>

      </portal>
    </template>
    <div v-if="loading" class="hv-center height100">
      <spinner :show="loading"></spinner>
    </div>
    <div v-else-if="$validation.isEmpty(list)" class="height100 hv-center">
      <div>
        <div>
          <InlineSvg src="svgs/emptystate/readings-empty" iconClass="icon text-center icon-130 emptystate-icon-size">
          </InlineSvg>
        </div>
        <div class="fc-black-dark f18 bold">
          {{ $t('asset.readings.no_readings_available') }}
        </div>
      </div>
    </div>
    <div v-else>
      <el-table v-if="!$validation.isEmpty(list)" 
        :data="list" 
        style="width: 100%"
        height="330px"
        :header-cell-style="headerCellStyle" 
        :cell-style="{ fontSize: '14px' }">

        <el-table-column label="Name" width="300px">
          <template slot-scope="reading">
            <span style="padding-left: 30px;display: inline-block;">{{ reading.row.field.displayName }}</span>
          </template>
        </el-table-column>

        <el-table-column :label="$t('common._common.set_value')">
          <template v-slot="scope">
            <div class="pL30">
              <template v-if="scope.row.value == null">
                null
              </template>
              <template v-else-if="isDecimalField(scope.row.field)">
                {{ Number(scope.row.value).toFixed(1)
                }}{{ scope.row.field.unit }}
              </template>
              <template v-else-if="isBooleanField(scope.row.field)">
                {{
                  $fieldUtils.getDisplayValue(
                    scope.row.field,
                    scope.row.value === 'true' || scope.row.value === '1'
                  )
                }}
              </template>
              <template v-else>
                {{
                  $fieldUtils.getDisplayValue(
                    addEnumMapField(scope.row.field),
                    scope.row.value
                  )
                }}
              </template>
            </div>
          </template>
        </el-table-column>


        <el-table-column :label="$t('common._common.executed_time')">
          <template v-slot="scope">
            <div class="pL30">
              {{ scope.row.executedTime | formatDate() }}
            </div>
          </template>
        </el-table-column>

        <el-table-column :label="$t('common._common.executed_by')" prop="id">
          <template v-slot="scope">
            <div class="pL30">
              <div v-if="scope.row.executedMode === 1">
                <user-avatar size="md" class="pm-list-avatar" :user="scope.row.executedBy" :showPopover="false"
                  :showLabel="false"></user-avatar>
              </div>
              <div v-else-if="scope.row.executedMode === 2 || scope.row.executedMode === 6
                ">
                <user-avatar size="md" class="pm-list-avatar" :user="scope.row.executedBy" :showPopover="false"
                  :showLabel="false"></user-avatar>
              </div>
              <div v-else-if="scope.row.executedMode === 3">
                <div>{{ $t('common.header.automatic') }}</div>
              </div>
              <div v-else-if="scope.row.executedMode === 4">
                <div>{{ $t('common.header.automatic') }}</div>
              </div>
              <div v-else-if="scope.row.executedMode === 5">
                <div>{{ $t('common.header.automatic') }}</div>
              </div>
              <div v-else>
                <div>---</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column :label="$t('common.products.status')" prop="id">
          <template v-slot="scope">
            <div class="pL30">
              <div v-if="scope.row.status === 1">
                <div class="fc-green-status bold f13 text-left">
                  {{ $t('maintenance.pm_list.success') }}
                </div>
              </div>
              <div v-else-if="scope.row.status === 2">
                <div class="fc-orange-status text-left">
                  {{ $t('controls.data_command_status.sent') }}
                </div>
              </div>
              <div v-else-if="scope.row.status === 3">
                <div class="fc-red-status f13 text-left">
                  {{ $t('maintenance._workorder.error') }}
                </div>
              </div>
              <div v-else-if="scope.row.status === 4">
                <div class="fc-black-13 f13 text-left">
                  {{ $t('controls.data_command_status.sheduled') }}
                </div>
              </div>
              <div v-else-if="scope.row.status === 5">
                <div class="fc-black-13 f13 text-left">
                  {{ $t('maintenance._workorder.without_permission') }}
                </div>
              </div>
              <div v-else-if="scope.row.status === 6">
                <div class="fc-darkRed-status f13 text-left">
                  {{ $t('controls.data_command_status.failed') }}
                </div>
              </div>
              <div v-else-if="scope.row.status === 7">
                <div class="fc-darkOrange-status f13 text-left">
                  {{ $t('controls.data_command_status.retrying') }}
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

      </el-table>
    </div>

  </div>
</template>
<script>

import { API } from '@facilio/api'
import { isBooleanField, isDecimalField } from '@facilio/utils/field'
import UserAvatar from '@/avatar/User'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import { isEmpty } from '@facilio/utils/validation'
import isEqual from 'lodash/isEqual'
import FSearch from '@/FSearch'
import {
  isWebTabsEnabled,
  findRouteForTab,
  tabTypes,
  getApp,
} from '@facilio/router'
export default {
  components: { UserAvatar, Pagination, FSearch },
  props: [
   
    'isActive',
    'portalName',
    'assetId',
    'resize',
    'reset',
    'details',
    'moduleName',
  ],

  data() {
    return {
      loading: false,
      list: null,
      totalCount: null,    
      page: 1,
      perPage: 50,
      router:null
    }
  },
  watch: {
    page(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.loadControlPoints()
      }
    },
  },
  mounted() {
    this.loadControlPoints()
  },
  created() {
    this.isBooleanField = isBooleanField
    this.isDecimalField = isDecimalField
  },
  computed:{
    showRedirectButton(){
      if (isWebTabsEnabled()) {
        let router = findRouteForTab(tabTypes.CUSTOM, {
          config: { type: 'commands' },
        })
        if(!isEmpty(router)){
          this.router = router
          return true
        }
        return false
      }
      return true
    }
  },
  methods: {
    async loadControlPoints(force) {
      this.loading = true
      let params = {
        moduleName: 'controlActionCommand',
        page: this.page,
        perPage: this.perPage,
        withCount: true
      }
      params.filters = JSON.stringify({
        resource: {
          operatorId: 36,
          value: [this.assetId.toString()],
        },
      })
      let url = '/v3/modules/data/list'

      let { error, data, meta } = await API.get(url, params,{ force})
    
      if (error) {
        error.message || this.$t('common._common.error_occured')
      
      } else {
        this.list = data['controlActionCommand']
        let { pagination } = meta
        let { totalCount } = pagination
        this.totalCount = totalCount
      }
      this.loading = false
    },
    addEnumMapField(fields) {
      let enumMap = fields.values.reduce((acc, object) => {
        acc[object.index.toString()] = object.value
        return acc
      }, {})
      fields.enumMap = enumMap
      return fields
    },
    headerCellStyle() {
      return {
        'padding-left': '30px', 'background-color': '#f7faff'
      };
    },
    listRefresh(){
      this.page = 1
      this.totalCount = 0
      this.list = null
      this.loadControlPoints(true)
    }, 
    resolvePath(){
      let filters = {"resource":{"operatorId":36,"value":[`${this.assetId}`]}}
      let actualPath = '/app/co/cc/commands'
      if (!isEmpty(this.router)) {
        let{path} = this.router
        actualPath = `/${getApp()?.linkName}/${path}/commands/all`
      }
      return {path:`${actualPath}?search=${encodeURIComponent(JSON.stringify(filters))}`};

    }
  }
}
</script>
<style lang="scss" scoped>
.hv-center {
  display: flex;
  align-items: center;
  justify-content: center;
}
.separator{
  padding-left: 1px;
  padding-bottom:2px ;
}
.mR6{
  margin-right: 6px !important;
}
.scrollable{
  height: 100%;
  overflow-y: scroll;
}
</style>