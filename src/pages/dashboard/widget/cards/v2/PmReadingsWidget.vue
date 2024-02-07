<template>
  <div class="dragabale-card height100 ">
    <div class="d-flex flex-direction-column justify-center" v-if="loading">
      <spinner :show="loading"></spinner>
    </div>
    <div v-else class="linear-gauge dragabale-card fc-widget height-100">
      <div>
        <div class="pm-reading-table-overlay"></div>
        <f-pm-readingtable
          :pmObject="pmObject"
          :pm="pm"
          :workorder="workorder"
          :config="data.config"
          :settings="settings"
        ></f-pm-readingtable>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import FPmReadingtable from '@/PmReadingsable'
export default {
  props: ['widget', 'config'],
  data() {
    return {
      loading: false,
      result: null,
      settings: null,
      data: {
        style: {
          color: 'red',
          bgcolor: 'pink',
        },
        unitString: '',
        workflowV2String: '',
      },
      pm: null,
      workorder: null,
      pmObject: null,
    }
  },
  components: {
    FPmReadingtable,
  },
  mounted() {
    this.getClientProps()
    this.getParams()
    this.loadpm()
  },
  methods: {
    getClientProps() {
      this.$nextTick(() => {
        if (this.$el) {
          this.settings = {
            height: this.$el.clientHeight - 67,
          }
        } else {
          this.settings = {
            height: 200,
          }
        }
      })
    },
    loadpm() {
      this.loading = true
      this.$http
        .get(
          '/workorder/preventiveMaintenanceSummary?id=' +
            parseInt(this.data.pmId)
        )
        .then(response => {
          this.pm = response.data.preventivemaintenance
          this.workorder = response.data.workorder
          this.pmObject = response.data
          this.loading = false
        })
    },
    refresh() {
      this.updateCard()
    },
    getParams() {
      if (this.widget.dataOptions.data) {
        this.widget.dataOptions.metaJson = JSON.stringify(
          this.widget.dataOptions.data
        )
        this.data = this.widget.dataOptions.data
        this.setParams()
      } else if (this.widget.id > -1 && this.widget.dataOptions.metaJson) {
        this.data = JSON.parse(this.widget.dataOptions.metaJson)
        this.setParams()
      }
    },
    setParams() {
      if (this.data && !this.data.style) {
        this.data.style = {
          color: this.color,
          bgcolor: this.bgcolor,
        }
      } else {
        this.style = this.data.style
      }
      this.widget.dataOptions.metaJson = JSON.stringify(this.data)
    },
    updateCard() {
      let self = this
      let params = null
      self.getParams()
      params = {
        workflow: {
          isV2Script: true,
          workflowV2String: this.data.workflowV2String,
        },
        staticKey: 'kpiCard',
      }
      this.loading = true
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.getData(response.data.cardResult)
          self.loading = false
        })
        .catch(function(error) {
          console.log('******** error', error)
          self.loading = false
        })
    },
    // loadCardData() {
    //   let self = this
    //   let params = null
    //   if (this.widget && this.widget.id > -1) {
    //     params = {
    //       widgetId: self.widget.id,
    //     }
    //   } else {
    //     params = {
    //       workflow: {
    //         isV2Script: true,
    //         workflowV2String: this.data.workflowV2String,
    //       },
    //       staticKey: 'kpiCard',
    //     }
    //   }

    //   this.loading = true
    //   return self.$http
    //     .post('dashboard/getCardData', params)
    //     .then(function(response) {
    //       self.getData(response.data.cardResult)
    //       self.loading = false
    //     })
    //     .catch(function(error) {
    //       console.log('******** error', error)
    //       self.loading = false
    //     })
    // },
    getData(data) {
      if (data.hasOwnProperty('result')) {
        this.result = data.result
      }
    },
    getFormattedValue(value) {
      if (!isEmpty(value)) {
        return `${value}`
      }
      return `--`
    },
  },
}
</script>
<style>
.pm-reading-table-settings {
  padding: 10px;
  border-bottom: 1px solid #e6ebf0;
}
/* .pm-reading-table-overlay {
  position: absolute;
  top: 60px;
  width: 100%;
  height: 100%;
  z-index: 20;
} */
</style>

<style lang="scss" scoped></style>
