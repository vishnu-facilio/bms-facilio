<template>
  <div
    class="d-flex align-center filter-cont"
    :class="filters && 'filter-list'"
  >
    <el-tooltip
      effect="dark"
      :content="$t('common._common.search')"
      :open-delay="500"
      placement="top"
      class="filter-icons"
      :class="[filters ? 'icon-left' : 'fl-icon-hover']"
      :tabindex="-1"
    >
      <AdvancedSearch
        :key="`${moduleName}-search`"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      >
        <template #icon>
          <fc-icon
            group="filter"
            name="filter"
            size="16"
            class="pointer"
          ></fc-icon></template
      ></AdvancedSearch>
    </el-tooltip>
    <filter-tags
      class="filter-icons icon-right"
      :moduleName="moduleName"
      :hideSaveView="hideSaveView"
    ></filter-tags>
  </div>
</template>
<script>
import AdvancedSearch from 'src/newapp/components/search/AdvancedSearch.vue'
import FilterTags from 'src/newapp/components/FilterTags.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['moduleName', 'moduleDisplayName', 'hideSaveView'],
  components: { AdvancedSearch, FilterTags },
  computed: {
    filters() {
      let { $route } = this
      let {
        query: { search },
      } = $route || {}

      return !isEmpty(search) ? JSON.parse(search) : null
    },
  },
}
</script>
<style lang="scss" scoped>
.filter-cont {
  height: 32px;

  .filter-seperator {
    color: #d5d9dd;
    font-size: 16px;
  }
}
.filter-list {
  border: 1px solid #d5d9dd;
  border-radius: 4px;
}
.filter-icons {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 38px;
  padding: 8px;

  &:hover {
    background-color: #f1f2f4;
  }
}
.fl-icon-hover {
  &:hover {
    border-radius: 4px;
  }
}
.icon-left {
  &:hover {
    border-radius: 4px 0px 0px 4px !important;
  }
}
.icon-right {
  border-left: 1px solid #d5d9dd;
  &:hover {
    border-radius: 0px 4px 4px 0px;
  }
}
</style>
