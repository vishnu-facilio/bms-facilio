<template>
  <div>
    <div :class="{ 'fc-black-small-txt-12': !hideToggle }" v-if="total > 0">
      <span v-if="from !== to">{{ from }} -</span>
      <span>{{ to }}</span>
      <template v-if="!hideToggle">
        <span
          class="el-icon-arrow-left fc-black-small-txt-12 fw-bold f16 pointer mL10 mR5"
          @click="from > 1 ? prev() : null"
          v-bind:class="{ disable: from <= 1 }"
        ></span>
        <span
          class="el-icon-arrow-right fc-black-small-txt-12 f16 pointer mR10 fw-bold"
          @click="to !== total ? next() : null"
          v-bind:class="{ disable: to === total }"
        ></span>
      </template>
    </div>
  </div>
</template>
<script>
import Pagination from '@/list/FPagination'
export default {
  extends: Pagination,
}
</script>
