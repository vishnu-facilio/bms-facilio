<template>
  <div class="dashboard-tab-sidebar view-panel">
    <div class="dashboard-tab-sidebar-scroll">
      <div
        v-for="(folder, index) in tabsList"
        :key="index"
        class="dashboard-folder-container"
      >
        <div
          @click="changeDashboardTabId(folder.id), expand(folder)"
          class="dfolder-name"
          v-bind:class="{ active: currentTabId === folder.id }"
        >
          <div class="mL5">
            <span>{{ folder.name }}</span>
            <div class="dfolder-icon fR" v-if="folder.childTabs">
              <i class="el-icon-arrow-down" v-if="folder.expand"></i>
              <i class="el-icon-arrow-right" v-else></i>
            </div>
          </div>
        </div>
        <div v-show="folder.expand" class="dfolder-children">
          <div
            @click="changeDashboardTabId(childFolder.id)"
            v-for="(childFolder, cId) in folder.childTabs"
            :key="cId"
            v-bind:class="{
              active: currentTabId === childFolder.id,
            }"
          >
            <span class="mL5">{{ childFolder.name }}</span>
          </div>
        </div>
      </div>
      <div
        v-if="isEditor"
        class="addTab-btn view-manager-btn"
        @click="$emit('toggleDashboardTabEditor')"
      >
        <i class="el-icon-setting"></i>
        <span class="label mL10 text-uppercase">
          {{ $t('home.dashboard.tab_manager') }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    tabsList: {
      type: Array,
      required: true,
    },
    currentTabId: {
      type: [Number, Object],
      required: true,
    },
    isEditor: {},
  },
  methods: {
    changeDashboardTabId(id) {
      this.$emit('changeDashboardTabId', id)
    },
    expand(folder) {
      this.$emit('expand', folder)
    },
  },
}
</script>

<style lang="scss" scoped>
.dashboard-tab-sidebar {
  background: white;
  height: 100vh;
  overflow-y: hidden;
  border-right: 1px solid #6666662f;
  position: relative;
  border-top: 1px solid #6666662f;
}
.dashboard-tab-sidebar-scroll {
  height: 100%;
  overflow-y: scroll;
  padding-bottom: 200px;
}
.dashboard-folder-container {
  position: relative;
}
.dfolder-name {
  padding: 14px 10px 14px 20px;
  cursor: pointer;
  font-size: 13px;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
}

.dfolder-name.active {
  background: #f0f7f8;
  font-weight: 500;
}

.dfolder-name i {
  margin-right: 6px;
  font-size: 16px;
  color: #66666696;
}
.dfolder-name.active,
.dfolder-children div.active {
  background: #f0f7f8;
}
.dfolder-name:hover,
.dfolder-children div:not(.rempty):hover {
  background: #f3f6f9;
}
.dfolder-children div {
  padding: 12px 10px 12px 20px;
  display: flex;
  justify-content: space-between;
}

.dfolder-children div:not(.rempty) {
  cursor: pointer;
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  letter-spacing: 0.4px;
  text-align: left;
  color: #333333;
}
</style>
