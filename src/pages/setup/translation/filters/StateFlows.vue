<template>
  <div class="filter flex-middle">
    <FilterDropdown
      v-if="!loadingStateFlows"
      v-model="activeStateFlow"
      :options="options"
    >
    </FilterDropdown>
    <div v-else class="loading-shimmer width120px height40 bR5"></div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import FilterDropdown from './FilterDropdown.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['moduleName', 'setFilter'],
  components: {
    FilterDropdown,
  },
  data() {
    return {
      stateFlowList: [],
      activeStateFlow: null,
      loadingStateFlows: true,
      transitionForm: [],
    }
  },
  created() {
    this.loadStateFlows()
  },
  watch: {
    activeStateFlow: function() {
      this.setFilter({
        loading: false,
        query: {
          stateFlowId: this.activeStateFlow.toString(),
        },
      })
    },
  },
  computed: {
    options() {
      return this.stateFlowList.map(stateFlow => {
        return {
          id: stateFlow.id,
          label: stateFlow.name,
        }
      })
    },
  },
  methods: {
    async loadStateFlows() {
      this.loadingStateFlows = true
      const { error, data } = await API.post('v2/translationsetup/list', {
        moduleName: this.moduleName,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        const { stateFlows } = data || {}
        if (!isEmpty(stateFlows)) {
          this.stateFlowList = stateFlows
          this.activeStateFlow = this.stateFlowList[0].id
        } else {
          this.setFilter({
            loading: false,
            query: null,
          })
        }
      }
      this.loadingStateFlows = false
    },
  },
}
</script>

<style scoped>
.filter {
  margin: 0 40px;
  padding: 10px 0;
}
</style>
