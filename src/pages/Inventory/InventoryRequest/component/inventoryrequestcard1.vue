<template>
  <div>
    <div class="height100 flex-center-vH flex-wrap">
      <el-row class="p30">
        <el-col :span="6">
          <store-room-avatar
            v-if="details.storeRoom"
            :name="false"
            size="lg"
            :storeRoom="details.storeRoom"
          ></store-room-avatar>
          <img
            class="fc-avatar fc-avatar-lg"
            src="~statics/inventory/store-img.jpg"
            v-else
          />
        </el-col>
        <el-col :span="18">
          <div class="fc-black-13 fwBold text-uppercase text-left">
            {{ $t('common.products.storeroom') }}
          </div>
          <div class="fc-black-13 text-left pT8">
            {{ storeRoomName }}
          </div>
        </el-col>
      </el-row>
      <el-row class="p30">
        <el-col :span="6">
          <div class="fc-in-custom-icon" style="background-color: #4ab9c0;">
            <InlineSvg
              src="svgs/workorder-ir-box"
              iconClass="icon icon-md vertical-middle fc-white-color"
            ></InlineSvg>
          </div>
        </el-col>
        <el-col :span="18">
          <div class="fc-black-13 fwBold text-uppercase text-left">
            {{ $t('common._common.workorder_id') }}
          </div>
          <div class="fc-dark-blue5 text-left pT8 f13 pointer">
            <div v-if="parentId" @click="goToSummary(parentId)">
              {{ '#' + details.workOrderLocalId }}
              <i class="el-icon-right pL5 vertical-middle bold"></i>
            </div>
            <div v-else>---</div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import StoreRoomAvatar from '@/avatar/Storeroom'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['details'],
  components: {
    StoreRoomAvatar,
  },
  computed: {
    storeRoomName() {
      let { details } = this
      let { storeRoom } = details || {}
      let { name } = storeRoom || {}
      return name || '---'
    },
    parentId() {
      let { parentId } = this.details || {}
      return !isEmpty(parentId) ? parentId : null
    },
  },
  methods: {
    goToSummary(id) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({
            name,
            params: { viewname: 'all', id },
          })
        }
      } else {
        this.$router.push({ path: `/app/wo/orders/summary/${id}` })
      }
    },
  },
}
</script>
