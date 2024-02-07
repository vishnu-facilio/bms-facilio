<template>
  <div class="planner-summary-container">
    <div class="planner-summary-header">
      {{ $t('maintenance.pm.ad_hoc_task') }}
    </div>
    <div class="ad-hoc-container">
      <img
        class="mT20 self-center"
        src="~statics/noData-light.png"
        width="100"
        height="100"
      />
      <div class="mT10 label-txt-black f14 self-center">
        {{ $t('maintenance.pm.no_add_hoc_task') }}
      </div>
    </div>
  </div>
</template>

<script>
export default {}
</script>

<style lang="scss">
.ad-hoc-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  padding: 20px;
}
</style>
