<template>
  <div class="width100">
    <div class="fc-table-td-height contract-table">
      <el-table
        :data="assetContractsList"
        style="width: 100%"
        height="auto"
        :fit="true"
      >
        <template slot="empty">
          <div class="text-center mT20">
            <InlineSvg
              src="svgs/emptystate/readings-empty"
              iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
            ></InlineSvg>

            <div class="fc-black-dark f18 bold pL50 line-height10">
              No Contracts available
            </div>
          </div>
        </template>
        <el-table-column
          prop="parentId"
          label="ID"
          width="80"
          fixed
          class="pR0"
        >
          <template v-slot="contract">
            <div class="fw5 fc-id">{{ '#' + contract.row.parentId }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="name" label="NAME" width="180" class="pL0" fixed>
          <template v-slot="contract">
            <div
              @click="openContract(contract.row.id)"
              v-tippy
              small
              :title="contract.row.name"
              class="flex-middle"
            >
              <div class="text-left db-bold fw5 sp-td pL0">
                {{ contract.row.name }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          prop="contractTypeEnum"
          label="CONTRACT TYPE"
          width="180"
        >
        </el-table-column>
        <el-table-column prop="vendor.name" label="VENDOR" width="180">
          <template v-slot="contract">
            <div class="fw5 ellipsis width200px">
              {{ contract.row.vendor.name }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="endDate" label="END DATE" width="180">
          <template v-slot="contract">
            <div class="fw5 ellipsis width200px">
              {{ (contract.row.endDate = getLocalDate(contract.row.endDate)) }}
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="renewalDate" label="RENEWAL DATE">
          <template v-slot="contract">
            <div
              v-if="contract.row.renewalDate && contract.row.renewalDate > 0"
              class="fw5 ellipsis width200px"
            >
              {{ getLocalDate(contract.row.renewalDate) }}
            </div>
            <div v-else class="fw5 ellipsis width200px">---</div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
export default {
  props: ['details'],

  data() {
    return {
      loading: true,
      assetContractsList: [],
    }
  },
  mounted: function() {
    this.getContracts()
  },
  methods: {
    getContracts: function() {
      let that = this
      that.$http
        .get(
          '/v2/assets/assetAssociatedActiveContracts?assetId=' + that.details.id
        )
        .then(function(response) {
          that.assetContractsList = response.data.result.contracts
          that.loading = false
        })
        .catch(() => {})
    },
    openContract(id) {
      if (id) {
        let url = '/app/ct/rentalleasecontracts/all/summary/' + id
        this.$router.replace({
          path: url,
        })
      }
    },
    getLocalDate(date) {
      let d
      if (date) {
        d = this.$options.filters.formatDate(date.valueOf(), true, false)
      }
      return d
    },
  },
}
</script>
<style scoped lang="scss"></style>
