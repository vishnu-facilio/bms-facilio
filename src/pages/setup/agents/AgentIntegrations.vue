<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Agent Integrations</div>
        <div class="heading-description">
          List of all available agent integrations
        </div>
      </div>
      <div class="action-btn setting-page-btn controller-right-actions"></div>
    </div>
    <div class="container-scroll">
      <div class="setting-Rlayout mT30 d-flex flex-direction-column">
        <div
          v-for="(integration, index) in availableIntegrations"
          :key="'integration' + index"
          class="integrations-card"
        >
          <component v-if="integration" :is="integration"></component>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Wattsense from 'pages/setup/integrations/Wattsense.vue'
export default {
  title() {
    return 'Integrations'
  },
  components: {
    Wattsense,
  },
  computed: {
    availableIntegrations() {
      return [Wattsense]
    },
  },
}
</script>
<style scoped>
.integrations-card {
  box-shadow: 0 3px 7px 0 rgba(233, 233, 226, 0.5);
  background-color: #fff;
  margin: 0 20px 20px 0;
  min-height: 135px;
}
</style>
