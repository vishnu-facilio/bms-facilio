<template>
  <div class="classification-list-container height100">
    <div class="cls-header">
      <div>
        <div class="header-title fw5">
          {{ $t('setup.classification.classification') }}
        </div>
        <div class="header-content">
          {{ $t('setup.classification.list_classifications') }}
        </div>
      </div>
      <div class="add-button">
        <portal-target name="classification-btn-portal"></portal-target>
      </div>
    </div>
    <div class="d-flex navigation-list-tab">
      <div
        class="navigation-list pointer"
        v-for="(value, key) in tabs"
        :key="`${key}-tab`"
        @click="navigateTabs(key)"
      >
        <div
          class="mR40 f16 navigation-list-header"
          :class="[isSelected(key) && 'fw5']"
          style="height: 16px"
        >
          {{ value }}
        </div>
        <div :class="[isSelected(key) && 'sh-selection-bar']"></div>
      </div>
    </div>
    <ClassificationList
      v-if="activeTab === 'classificationList'"
    ></ClassificationList>
    <AttributeList v-if="activeTab === 'attributesList'"></AttributeList>
  </div>
</template>

<script>
import AttributeList from 'pages/setup/classification/AttributeList.vue'
import ClassificationList from 'pages/setup/classification/ClassificationList'

export default {
  components: {
    AttributeList,
    ClassificationList,
  },
  data() {
    return {
      activeTab: 'classificationList',
      tabs: {
        classificationList: this.$t('setup.classification.classification'),
        attributesList: this.$t('setup.classification.attributes.attributes'),
      },
    }
  },
  methods: {
    navigateTabs(item) {
      this.activeTab = item
    },
    isSelected(item) {
      return this.activeTab === item
    },
  },
}
</script>
<style lang="scss">
.classification-list-container {
  .setup-el-btn:hover,
  .setup-el-btn:focus {
    color: #fff !important;
  }
  .el-tabs__header.is-top {
    padding-top: 10px;
  }
  .el-tabs__item {
    font-size: 16px;
    text-transform: capitalize;
  }
  .cls-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 27px 24px;
    background-color: white;
  }
  .header-title {
    font-size: 18px;
    letter-spacing: 0.2px;
    color: #4f4f4f;
    padding-bottom: 8px;
  }
  .header-content {
    font-size: 12px;
    letter-spacing: 0.43px;
    opacity: 0.5;
    color: #324056;
  }
  .navigation-list-tab {
    border-top: 1px solid #f0f0f0;
    background: #fff;
    padding: 18px 24px 0px;
  }
}
.navigation-list {
  align-items: center;
  &:hover {
    color: #ee518f;
  }
  .sh-selection-bar {
    width: 32px;
    margin-top: 14px;
    border-bottom: 2px solid #ee518f;
  }
  .navigation-list-header {
    letter-spacing: 0.57px;
  }
}
</style>
