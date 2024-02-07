<template>
  <div class="">
    <el-header class="fc-v1-setup-header" height="150">
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
      <div>
        <div class="fc-setup-tab-content">
          <slot name="tabs">
            <slot name="tabFilter">
              <div class="flex-middle justify-content-space p20">
                <div class="">
                  <slot name="filter"></slot>
                </div>
                <div class="">
                  <slot name="searchAndPagination"></slot>
                </div>
              </div>
            </slot>
            <slot name="tabContent"></slot>
          </slot>
        </div>
      </div>
    </el-header>
  </div>
</template>
