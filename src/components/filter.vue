<template>
  <q-modal
    ref="createNewModel"
    position="right"
    content-classes="fc-filter-record"
    @close="close"
  >
    <q-btn flat @click="$refs.createNewModel.close()">
      <q-icon name="close" />
    </q-btn>
    <div style="padding: 20px;">
      <div style="font-size: 22px; font-weight:600; color: #444;">Filter</div>
      <div>
        <q-field label="Requester">
          <q-select
            multiple
            v-model="filter.requester.value"
            :options="picklist.requester"
          />
        </q-field>
      </div>
      <div>
        <q-field label="Created">
          <q-select
            v-model="filter.createdDate.operator"
            :options="[
              {
                label: 'Today',
                value: 'Today',
              },
              {
                label: 'Yesterday',
                value: 'Yesterday',
              },
              {
                label: 'Till Yesterday',
                value: 'Till Yesterday',
              },
              {
                label: 'Current Week',
                value: 'Current Week',
              },
              {
                label: 'Current Month',
                value: 'Current Month',
              },
              {
                label: 'Last Week',
                value: 'Last Week',
              },
              {
                label: 'Last Month',
                value: 'Last Month',
              },
            ]"
          />
        </q-field>
      </div>
      <div>
        <q-field label="Urgency">
          <q-option-group
            color="secondary"
            type="checkbox"
            v-model="filter.priority.value"
            :options="picklist.ticketpriority"
          />
        </q-field>
      </div>
      <div>
        <q-btn color="primary" @click="applyFilters">
          Apply Filters
        </q-btn>
      </div>
    </div>
  </q-modal>
</template>
<script>
import { QModal, QBtn, QIcon, QOptionGroup, QField, QSelect } from 'quasar'
import { mapActions } from 'vuex'

export default {
  props: ['filters'],
  data() {
    this.loadPicklist('ticketpriority')
    this.loadPicklist('requester')
    return {
      filter: this.filters,
      picklist: {
        ticketpriority: [],
        requester: [],
      },
    }
  },
  mounted() {
    this.$refs.createNewModel.open()
  },
  components: {
    QModal,
    QBtn,
    QIcon,
    QOptionGroup,
    QField,
    QSelect,
  },
  methods: {
    ...mapActions(['workorder/getPicklist']),
    ...mapActions({
      getPicklist: 'workorder/getPicklist',
    }),
    applyFilters() {
      this.$emit('apply')
    },
    close() {
      this.$emit('closed')
    },
    loadPicklist(moduleName) {
      let self = this
      self.loading = true
      this.getPicklist(moduleName).then(function(response) {
        let array = []
        for (let key in response.data) {
          let val = response.data[key]
          let obj = {}
          obj.label = val
          obj.value = key + '_' + val
          array.push(obj)
        }
        self.picklist[moduleName] = array
        self.loading = false
      })
    },
  },
}
</script>
<style>
.fc-filter-record {
  width: 30% !important;
  height: 100% !important;
  max-height: 100% !important;
}
</style>
