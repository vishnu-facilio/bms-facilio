<template>
  <div class="">
    <el-header class="fc-v1-setup-header" height="120">
      <div
        class="flex-middle justify-content-space fc-header-border-bottom p20"
      >
        <div>
          <div class="fc-v1-setup-heading">
            <slot name="heading"></slot>
          </div>
          <div class="fc-v1-setup-description">
            <slot name="description"></slot>
          </div>
        </div>
        <div class="flex-middle">
          <slot name="actions"></slot>
        </div>
      </div>
      <div
        class="flex-middle justify-content-space p10 fc-setup-actions-header"
      >
        <div class="">
          <slot name="filter"></slot>
        </div>
        <div class="">
          <slot name="searchAndPagination"></slot>
        </div>
      </div>

      <div class="flex-middle fc-filter-animation">
        <slot name="extraFilter"></slot>
      </div>
    </el-header>
  </div>
</template>
<style scoped lang="scss">
.fc-header-border-bottom {
  height: 80px;
}
.fc-setup-actions-header {
  height: 45px;
}
</style>
