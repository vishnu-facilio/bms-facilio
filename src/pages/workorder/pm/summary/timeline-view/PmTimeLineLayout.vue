<template>
  <div class="d-flex height100 flex-direction-column">
    <div class="d-flex pm-timeline-header-container">
      <div class="pm-planner-section d-flex">
        <slot name="planner-selector"></slot>
        <slot name="pm-assignment"></slot>
      </div>
      <div class="schedular-calendar-actions">
        <slot name="calendar-actions"></slot>
      </div>
      <div class="search-actions">
        <slot name="search-customization"></slot>
      </div>
    </div>
    <div class="scheduler-content">
      <slot name="schedular"></slot>
    </div>
  </div>
</template>
<script>
export default {
  name: 'pmTimeLineLayout',
}
</script>
<style scoped>
.pm-timeline-header-container {
  height: 65px;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  background: #fff;
  align-items: center;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.11);
  margin-bottom: 20px;
}
.scheduler-content {
  background: #fff;
  height: 100%;
}
.schedular-calendar-actions {
  display: flex;
  align-items: center;
  margin-left: -30%;
}
.search-actions {
  justify-content: flex-end;
}
</style>
