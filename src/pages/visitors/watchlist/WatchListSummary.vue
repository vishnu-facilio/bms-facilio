<template>
  <el-dialog
    :visible="true"
    :append-to-body="true"
    :before-close="goToList"
    width="50%"
    custom-class="slideInRight fc-animated fc-v1-dialog-right slideInRight fc-dialog-header-hide watchlist-summary"
  >
    <div v-if="loading" class="height100 flex-middle">
      <spinner :show="true" size="80"></spinner>
    </div>
    <template v-else>
      <el-header height="150" class="fc-v1-overview-header position-relative">
        <div class="flex-middle justify-content-space">
          <div class="">
            <div class="fc-id">#{{ recordData.id }}</div>
            <div class="flex-middle">
              <VisitorAvatar
                module="visitor"
                :name="false"
                size="lg"
                v-if="recordData"
                :recordData="recordData"
              ></VisitorAvatar>
              <div class="mL10">
                <div class="fw5 ellipsis textoverflow-ellipsis width200px">
                  {{ $getProperty(recordData, 'name', '---') }}
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="fc-v1-overview-header-close" @click="goToList()">
          <i class="el-icon-close"></i>
        </div>
      </el-header>
      <el-main class="p0">
        <div class="widget-header">
          <div class="pT13">Details</div>
          <div class="header-highlighter"></div>
        </div>
        <div class="fc-tab-inner-scroll pT10 pB10 pR30 pL30">
          <div class="border-bottom1px pB20">
            <div class="label-txt-black fw-bold">
              Basic Info
            </div>
            <el-row class="pT20">
              <el-col :span="12">
                <div class="fc-grey3-text14">Phone</div>
                <div class="label-txt-black bold pT5">
                  {{ recordData.phone ? recordData.phone : '---' }}
                </div>
              </el-col>
              <el-col :span="12">
                <div class="fc-grey3-text14">Email</div>
                <div class="label-txt-black bold pT5">
                  {{ recordData.email ? recordData.email : '---' }}
                </div>
              </el-col>
            </el-row>

            <el-row class="pT20">
              <el-col :span="12">
                <div class="fc-grey3-text14">Vip</div>
                <div class="label-txt-black bold pT5">
                  {{ recordData.isVip ? 'Yes' : 'No' }}
                </div>
              </el-col>
              <el-col :span="12">
                <div class="fc-grey3-text14">Blocked</div>
                <div class="label-txt-black bold pT5">
                  {{ recordData.isBlocked ? 'Yes' : 'No' }}
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-main>
    </template>
  </el-dialog>
</template>
<script>
import VisitorAvatar from '@/avatar/VisitorAvatar'
import { API } from '@facilio/api'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['viewname', 'id', 'moduleName'],
  components: {
    VisitorAvatar,
  },
  data() {
    return {
      recordData: null,
      loading: false,
    }
  },
  computed: {
    currentId() {
      let { id } = this
      return id ? parseInt(id) : null
    },
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      let { currentId } = this
      this.loading = true
      let { watchlist, error } = await API.fetchRecord('watchlist', {
        id: currentId,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.recordData = watchlist || []
      }
      this.loading = false
    },
    goToList() {
      let { viewname, moduleName } = this
      let query = this.$route?.query || {}

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

        if (name) {
          this.$router.replace({ name, params: { viewname }, query })
        }
      } else {
        this.$router.replace({
          name: 'watchlist-list',
          params: { viewname },
          query,
        })
      }
    },
  },
}
</script>
<style lang="scss">
.watchlist-summary {
  .widget-header {
    padding: 10px 30px 0px;
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 1px;
    color: #385571;
    text-transform: uppercase;
    background: #f6f8f9;
    margin-bottom: 15px;
  }
  .header-highlighter {
    border-bottom: 2px solid #ee518f;
    width: 30px;
    margin-top: 10px;
  }
}
</style>
