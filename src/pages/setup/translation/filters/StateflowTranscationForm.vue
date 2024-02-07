<template>
  <div class="filter flex-middle">
    <FilterDropdown
      v-if="!loadingStateFlows"
      v-model="activeStateFlow"
      :options="options"
      @input="formReload"
    >
    </FilterDropdown>
    <div v-else class="loading-shimmer width120px height40 bR5"></div>

    <!-- filter 2 -->
    <FilterDropdown
      v-if="!loadingStateForm"
      v-model="activeForm"
      :options="activeStateFormFlow"
      class="mL20"
    >
    </FilterDropdown>
    <div v-else class="loading-shimmer width120px height40 bR5 mL20"></div>
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
      activeForm: null,
      loadingStateForm: true,
      stateFromOptions: [],
      stateFlowFormList: [],
      showEmptyData: false,
    }
  },
  created() {
    this.loadStateFlows()
  },
  watch: {
    activeForm: function() {
      this.setFilter({
        loading: false,
        query: {
          formId: this.activeForm.toString(),
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
    activeStateFormFlow() {
      return this.stateFlowFormList.map(form => {
        return {
          id: form.formId,
          label: form.name,
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
          this.loadStateFlowForms()
        } else {
          this.setFilter({
            loading: false,
            query: null,
          })
        }
      }
      this.loadingStateFlows = false
    },
    async loadStateFlowForms() {
      this.loadingStateForm = true
      this.activeForm = null
      let params = {
        stateFlowId: this.activeStateFlow,
      }
      const { error, data } = await API.post(
        'v2/translationsetup/getStateTransitionForms',
        params
      )
      if (error) {
        this.$message.error(error.message || 'Error Occured')
        this.setFilter({
          loading: false,
          query: null,
        })
      } else {
        const { stateTransitionList } = data || []
        if (!isEmpty(stateTransitionList)) {
          this.stateFlowFormList = stateTransitionList
          this.activeForm = this.stateFlowFormList[0].formId
          this.showEmptyData = false
        } else {
          this.setFilter({
            loading: false,
            query: null,
          })
        }
      }
      this.loadingStateForm = false
    },
    formReload() {
      this.loadStateFlowForms()
    },
  },
}
</script>

<style scoped>
.filter {
  margin: 0 40px;
  padding: 10px 0;
}
.emptyFormButton {
  font-size: 14px;
  cursor: pointer;
  padding: 10px;
  background-color: #f3f4f7;
  border-radius: 4px;
  font-weight: 500;
}
</style>
