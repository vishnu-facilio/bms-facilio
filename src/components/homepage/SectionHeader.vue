<template>
  <div>
    <div class="section-header flex-middle" v-if="name">
      <span>{{ name }}</span>
      <span class="mL5">
        <InfoIcon v-if="infoMsg" :content="infoMsg"></InfoIcon>
      </span>
    </div>
    <slot></slot>
  </div>
</template>

<script>
import InfoIcon from 'src/PortalTenant/components/InfoIcon.vue'
export default {
  props: ['name', 'infoMsg'],
  components: { InfoIcon },
}
</script>

<style lang="scss" scoped>
.section-header {
  font-size: 16px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #464646;
  padding-left: 12px;
}
</style>
