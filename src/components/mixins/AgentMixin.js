export default {
  data() {
    return {
      agentType: null,
      isConnected: false,
    }
  },
  methods: {
    loadAgent() {
      return this.$http.get('/v2/agent/getFilter').then(response => {
        return response.data
      })
    },
    isAgentOffline(selectedAgentId, arrayCopy) {
      if (selectedAgentId && arrayCopy.length) {
        let currentAgent = arrayCopy.find(agent => agent.id === selectedAgentId)
        this.agentType = currentAgent.agentType
        this.isConnected = currentAgent.connected
      }
    },
    canDiscover(agentType, controllerType) {
      return (
        agentType === 2 ||
        agentType === 6 ||
        agentType === 9 ||
        (agentType === 1 && controllerType != 4 && controllerType != 5)
      )
    },
    supportAdvancedDiscover(agentType, controllerType) {
      if(controllerType==null){
        return agentType == 1
      }
      return agentType == 1 && controllerType == 1
    },
    supportsAdvanceDiscoverPoints(agentType, selectedType) {
      return agentType == 1 && (selectedType == 1 || selectedType == 7);
    },
    supportsConfigureAllPoints(agentType, selectedType) {
      if(agentType == 2){
        return true
      }
      return (agentType == 1  && selectedType == 1);
    },
    canConfigurePoints(agentType, controllerType) {
      return (agentType == 2 || agentType == 6 || agentType == 9) ||
      (agentType == 1 && [1, 2, 4, 7, 8, 12].includes(controllerType))
    },
  },
}
