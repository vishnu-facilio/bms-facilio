<template>
  <div>
    <div v-if="!hideLabel" class="fc-pink f12 pB5 bold">Agent</div>
    <el-select
      :disabled="!disableSelect && $validation.isEmpty(agentSearch)"
      v-model="selectedAgentId"
      remote
      filterable
      default-first-option
      @change="getControllers()"
      placeholder="Select Agent"
      class="fc-input-full-border-select2"
      :loading="isAgentLoading"
      loading-text="Searching"
      :remote-method="query => searchAgent({ searchText: query })"
    >
      <el-option
        v-for="agent in agents"
        :key="agent.id"
        :label="agent.displayName || agent.name"
        :value="agent.id"
        no-data-text="No Agent available"
        clearable
      ></el-option>
    </el-select>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import debounce from 'lodash/debounce'

export default {
  data() {
    return {
      isAgentLoading: false,
      agents: null,
      selectedAgentId: null,
      defaultIds: [],
      selectedAgent: null,
      agentSearch: '',
      disableSelect: false,
    }
  },

  props: ['discoverableAgents', 'hideLabel', 'filterActiveAgents','defaultAgent'],

  mounted() {
    this.getAgents()
  },
  methods: {
    getAgents() {
      this.defaultIds = this.$route.query.agentId
      let url = '/v2/agent/list?page=1&perPage=50'
      if (!isEmpty(this.defaultIds)) {
        url = `${url}&defaultIds=${this.defaultIds}`
      }
      if (!isEmpty(this.agentSearch)) {
        url = `${url}&querySearch=${this.agentSearch}`
      }

      this.$set(this, 'isAgentLoading', true)
      this.$http
        .get(url)
        .then(response => {
          this.$set(this, 'isAgentLoading', false)
          this.agents =
            response.data.result && response.data.result.data
              ? response.data.result.data
              : []

          this.disableSelect = true
          if (this.discoverableAgents) {
            this.agents = this.agents.filter(
              agent =>
                agent.agentType === 1 ||
                agent.agentType === 2 ||
                agent.agentType === 9 ||
                agent.agentType === 6
            )
          }
          if (this.filterActiveAgents) {
            this.agents = this.agents.filter(agent => agent.connected == true)
          }
          if (this.agents.length && isEmpty(this.agentSearch)) {

            if(this.defaultAgent != null){
              this.selectedAgentId = this.defaultAgent
            }
            else{
              this.selectedAgentId = parseInt(this.$route.query.agentId) || this.agents[0].id
            }
            
            this.getSelectedAgent()
            if (this.selectedAgent != null) {
              this.$emit('onAgentFilter', this.selectedAgent)
            }
          }
          this.disableSelect = !isEmpty(this.agents) ? true : false
        })
        .catch(() => {
          this.agents = []
          this.$set(this, 'isAgentLoading', false)
        })
    },
    getControllers() {
      this.getSelectedAgent()
      if (this.selectedAgent != null) {
        this.$emit('onAgentFilter', this.selectedAgent)
      }
    },
    getSelectedAgent() {
      this.selectedAgent = this.agents.find(
        agent => agent.id === this.selectedAgentId
      )
    },
    searchAgent: debounce(function(props = {}) {
      let { searchText } = props
      this.agentSearch = searchText
      this.getAgents()
    }, 1000),
  },
}
</script>
<style lang="scss"></style>
