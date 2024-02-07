<template>
  <div>
    <div
      v-if="$validation.isEmpty(details)"
      class="d-flex justify-content-center flex-direction-column align-center height100"
    >
      <div>
        <inline-svg
          src="svgs/emptystate/data-empty"
          class="vertical-middle'"
          iconClass="icon icon-60"
        ></inline-svg>
      </div>
      <div class="fc-black3-16 self-center bold">
        {{ $t('common._common.no_lineitems_available') }}
      </div>
    </div>
    <div v-else class="item-summary-table width100 pT20 pL30 pB20">
      <div class="fc-black-13 fw-bold text-left">
        {{ $t('common._common.serving_sites') }}
      </div>
      <el-table
        :data="storeRoomSites"
        style="width: 100%"
        :fit="true"
        height="370"
        class="fc-table-widget-scroll unit-table"
      >
        <el-table-column
          prop="name"
          label="Name"
          min-width="100"
          class="fc-black-13 fw-bold text-left"
          sortable
        ></el-table-column>
        <el-table-column
          prop="area"
          label="Area"
          min-width="60"
          :formatter="areaFormatter"
          sortable
        ></el-table-column>
        <el-table-column
          prop="siteTypeVal"
          label="Type"
          min-width="80"
          sortable
        >
          <template v-slot="site">
            <div>
              <div class="flex-middle z-10" v-if="site.row.siteTypeVal">
                <div>
                  <div class="black-color f14">
                    {{ site.row.siteTypeVal }}
                  </div>
                </div>
              </div>
              <div class="flex-middle z-10" v-else>
                <div>
                  <div class="black-color f14">
                    {{ '---' }}
                  </div>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="MANAGED BY">
          <template v-slot="site">
            <div>
              <div class="flex-middle z-10" v-if="site.row.managedBy">
                <div>
                  <div class="black-color f14">
                    <span>
                      <user-avatar
                        size="sm"
                        :user="$store.getters.getUser(site.row.managedBy.id)"
                      ></user-avatar>
                    </span>
                  </div>
                </div>
              </div>
              <div class="flex-middle z-10" v-else>
                <div>
                  <div class="black-color f14">
                    {{ '---' }}
                  </div>
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
import { isEmpty } from '@facilio/utils/validation'
import UserAvatar from '@/avatar/User'
export default {
  props: ['details'],
  data() {
    return {
      storeRoomSites: [],
    }
  },
  components: {
    UserAvatar,
  },
  watch: {
    currentStoreRoomId: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.loadStoreRoomSites()
        }
      },
      immediate: true,
    },
  },
  computed: {
    currentStoreRoomId() {
      return parseInt(this.$route.params.id)
    },
  },
  methods: {
    loadStoreRoomSites() {
      this.siteLoading = true
      this.storeRoomSites = []
      let servingsites = this.$getProperty(this.details, 'servingsites')
      if (servingsites && servingsites.length > 0) {
        this.storeRoomSites = servingsites
      }
      this.siteLoading = false
    },
    areaFormatter(row) {
      return this.$getProperty(row, 'area', '---')
    },
  },
}
</script>
<style lang="scss">
.item-summary-table {
  .el-table th.is-leaf {
    padding-left: 0;
    padding-right: 0;
  }
  .el-table th.el-table__cell > .cell {
    padding-left: 0;
    padding-right: 0;
  }
}
</style>
