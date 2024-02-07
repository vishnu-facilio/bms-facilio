<template>
  <div class="fc-agent-con">
    <agent-header></agent-header>
    <div class="fc-agent-main-con">
      <agent-sidebar></agent-sidebar>
      <div class="fc-agent-main-con">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>
<script>
import agentSidebar from 'agent/AgentSidebar'
import agentHeader from 'agent/AgentHeader'
import 'src/agent/styles/agent.scss'

export default {
  data() {
    return {}
  },
  components: {
    agentSidebar,
    agentHeader,
  },
}
</script>
<style lang="scss" scoped>
.fc-agent-con {
  width: 100%;
  height: 100vh;
  overflow: hidden;
  position: relative;
  box-sizing: border-box;
  -moz-box-sizing: border-box;
  -webkit-box-sizing: border-box;
  position: fixed;
  top: 0;
  right: 0;
  left: 0;
  z-index: 100;
  background: #f7f8f9;
  padding-left: 0 !important;
}
.fc-agent-main-con {
  height: calc(100vh - 60px);
  display: flex;
  flex-wrap: nowrap;
  flex-direction: row;
  flex: 0 0 100%;
  position: relative;
  overflow: hidden;
}
</style>
