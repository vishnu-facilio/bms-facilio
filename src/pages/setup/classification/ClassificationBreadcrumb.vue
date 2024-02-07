<template>
  <div class="breadcrumb-container pointer mL20">
    <div v-for="(item, index) in breadCrumbObj" :key="index + 'item'">
      <el-dropdown
        class="dropdown-breadcrumb"
        v-if="item.child"
        @command="getBreadcrumbList"
        trigger="click"
      >
        <div class="d-flex breadcrumb-container-list">
          <el-tooltip
            effect="dark"
            :content="$t('setup.classification.show_path')"
            placement="bottom-end"
            class="d-flex vertical-middle"
          >
            <div class="mL5 breadcrumb-hover pB8">...</div>
          </el-tooltip>
          <div class="mL5 breadcrumb-hover">/</div>
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="breadcrumbItem in dropdownBreadcrumb"
            :key="breadcrumbItem.id + 'breadcrumb'"
            :command="breadcrumbItem"
          >
            {{ breadcrumbItem.name }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <div
        class="mL5 breadcrumb-last-child"
        v-else-if="index === breadCrumbObj.length - 1"
        @click="getBreadcrumbList(item)"
      >
        {{ item.name }}
      </div>
      <div v-else class="d-flex vertical-middle">
        <span class="mL5 breadcrumb-hover" @click="getBreadcrumbList(item)">{{
          item.name
        }}</span>
        <div class="mL5 breadcrumb-hover">
          /
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: ['breadCrumb'],
  computed: {
    breadcrumbSize() {
      let size = this.breadCrumb.length
      return size < 4
    },
    dropdownBreadcrumb() {
      let breadcrumb = []

      if (!this.breadcrumbSize)
        breadcrumb = this.breadCrumb.slice(1, this.breadCrumb.length - 2)
      return breadcrumb
    },
    breadCrumbObj() {
      let breadCrumbObj = []
      if (!this.breadcrumbSize) {
        breadCrumbObj.push(
          this.breadCrumb[0],
          { child: this.dropdownBreadcrumb },
          ...this.breadCrumb.slice(-2)
        )
      } else {
        breadCrumbObj = this.breadCrumb
      }
      return breadCrumbObj
    },
  },
  methods: {
    getBreadcrumbList(item) {
      this.$emit('onGetBreadcrumbList', item)
    },
  },
}
</script>
<style lang="scss" scoped>
.breadcrumb-container {
  text-transform: capitalize;
  align-items: center;
  height: 40px;
  display: flex;

  .breadcrumb-container-list {
    align-items: center;
  }
  .breadcrumb-hover:hover {
    color: #324056;
  }
  .breadcrumb-hover {
    color: rgba(50, 64, 86, 0.5);
    letter-spacing: 0.5px;
  }
  .breadcrumb-last-child {
    letter-spacing: 0.5px;
  }
}
</style>
