<template>
  <div>
    <div
      :class="{ 'fc-black-small-txt-12': !hideToggle }"
      v-if="total > 0"
      class="mT-5 d-flex"
    >
      <span
        v-if="!hideToggle"
        class="el-icon-arrow-left pagination-arrow fc-black-small-txt-12 f16 fw-bold pointer pR5"
        @click="from > 1 ? prev() : null"
        :class="{ disable: from <= 1 }"
      ></span>
      <el-popover placement="bottom" trigger="click" :disabled="disablePopup">
        <span
          slot="reference"
          class="pointer"
          :class="[!disablePopup && 'button-hover']"
        >
          <span v-if="from !== to">{{ from }} - </span>
          <span>{{ to }}</span> {{ $t('maintenance._workorder.of') }}
          {{ total }}
        </span>
        <el-pagination
          layout="prev, pager, next"
          @current-change="handleCurrentChange"
          :total="total"
          :pager-count="5"
          :page-size.sync="perPage"
          :current-page.sync="page"
        >
        </el-pagination>
      </el-popover>
      <span
        v-if="!hideToggle"
        class="el-icon-arrow-right pagination-arrow fc-black-small-txt-12 f16 fw-bold pointer pL5"
        @click="to !== total ? next() : null"
        :class="{ disable: to === total }"
      ></span>
    </div>
  </div>
</template>
<script>
import Pagination from 'src/components/list/FPagination'
export default {
  extends: Pagination,
}
</script>
