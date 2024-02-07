<template>
  <CommonListLayout
    moduleName="readingkpi"
    :getPageTitle="() => 'Reading KPI List'"
  >
    <template #views>
      <ReadingKpiViews></ReadingKpiViews>
    </template>
    <el-row class="main-wrapper-reading-kpi">
      <el-col class="left-pane column-height" :span="5">
        <ReadingKpiOrAssetList
          :nameList="nameList"
          :kpiType="kpiType"
          @changeInNameList="setProperties"
        />
      </el-col>
      <el-col class="column-height" :span="19">
        <ReadingKpiOrAssetDetails
          :records="records"
          :kpiType="kpiType"
          :groupBy="wrapperData.groupBy"
          :currentRecordId="wrapperData.currentRecordId"
          :currentRecordName="wrapperData.currentRecordName"
          class="p10"
        />
      </el-col>
    </el-row>
  </CommonListLayout>
</template>

<script>
import ReadingKpiViews from 'pages/energy/readingkpi/ReadingKpiViews.vue'
import CommonListLayout from 'newapp/list/DeprecatedCommonLayout'
import ReadingKpiOrAssetList from 'src/pages/energy/readingkpi/ReadingKpiOrAssetList.vue'
import ReadingKpiOrAssetDetails from 'src/pages/energy/readingkpi/ReadingKpiOrAssetDetails.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  components: {
    ReadingKpiViews,
    CommonListLayout,
    ReadingKpiOrAssetList,
    ReadingKpiOrAssetDetails,
  },
  created() {
    let { $route } = this
    let { params, path } = $route || {}
    let { kpiType } = params
    if (isEmpty(kpiType)) {
      this.$router.push({
        path: path + '/live',
      })
    }
  },
  data() {
    return {
      wrapperData: {},
      records: [],
      currentRecordId: -1,
      currentRecordName: null,
      nameList: null,
    }
  },
  computed: {
    kpiType() {
      let { $route: { params: { kpiType } = {} } = {} } = this
      return isEmpty(kpiType) ? 'live' : kpiType
    },
  },
  methods: {
    async setProperties(value) {
      this.wrapperData = { ...this.wrapperData, ...value }
    },
  },
}
</script>

<style scoped lang="scss">
.main-wrapper-reading-kpi {
  height: 100%;
}
.left-pane {
  display: flex;
  flex-direction: column;
  background: white;
}
.column-height {
  height: calc(100vh - 105px);
}
</style>
