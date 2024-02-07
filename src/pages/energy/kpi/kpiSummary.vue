<template>
  <div style="height: auto; overflow-y: scroll;">
    <div
      class="fc__layout__align fc__asset__main__header pT20 pB20 pL20 pR20"
      style="width: auto; align-items: center !important; border-bottom: none;"
    >
      <div v-if="kpi" class="asset-details">
        <div class="asset-id mb5">#{{ kpi.id }}</div>
        <div class="asset-name mb5 max-width550px">{{ kpi.name }}</div>
      </div>
      <div class="d-flex flex-direction-row" style="margin-left: auto;">
        <el-button
          class="fc-wo-border-btn pL15 pR15 self-center"
          @click="editKpi()"
        >
          <i class="el-icon-edit"></i>
        </el-button>
      </div>
    </div>
    <page
      :key="kpi.id"
      v-if="kpi && kpi.id"
      :module="moduleName"
      :id="kpi.id"
      :details="kpi"
    ></page>
    <new-kpi
      v-if="showEditPopup"
      :isNew="false"
      :kpi="selectedKPI"
      @onSave="onKpiUpdate"
      @onClose=";(showEditPopup = false), (selectedKPI = null)"
    ></new-kpi>
  </div>
</template>
<script>
import Page from '@/page/PageBuilder'
import NewKpi from 'pages/energy/kpi/components/AddKpi'
import { isEmpty } from '@facilio/utils/validation'

export default {
  name: 'kpiSummary',
  props: ['viewname', 'id'],
  components: { Page, NewKpi },

  data() {
    return {
      moduleName: 'formulaField',
      kpi: null,
      showEditPopup: false,
      selectedKPI: null,
    }
  },

  created() {
    this.loadKpi(this.id)
  },

  computed: {},
  watch: {
    id: {
      immediate: true,
      handler() {
        this.loadKpi(this.id)
      },
    },
  },
  methods: {
    loadKpi(id) {
      if (isEmpty(id)) return

      this.$http.get(`/v2/kpi/${id}`).then(response => {
        if (response.data.responseCode === 0)
          this.kpi = this.$getProperty(
            response,
            'data.result.formulaField',
            null
          )
      })
    },
    editKpi() {
      this.selectedKPI = this.$helpers.cloneObject(this.kpi)
      this.showEditPopup = true
    },
    onKpiUpdate({ id }) {
      this.loadKpi(id)
    },
  },
}
</script>

<style scoped>
.asset-details {
  flex-grow: 1;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.asset-details .asset-id {
  font-size: 12px;
  color: #39b2c2;
}
.asset-details .asset-name {
  font-size: 22px;
  color: #324056;
}
.asset-details .asset-space {
  font-size: 13px;
  color: #8ca1ad;
}
.fc-badge {
  color: #fff;
  background-color: #23b096;
  padding: 5px 18px;
}
</style>
