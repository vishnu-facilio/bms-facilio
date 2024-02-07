<template>
  <div v-if="!$validation.isEmpty(tagsArr)" class="filter-tags pointer">
    <el-popover
      placement="bottom-end"
      trigger="click"
      popper-class="filter-tags-popover"
    >
      <div slot="reference" class="d-flex align-center">
        {{ tagLength }} <i class="el-icon-arrow-down"></i>
      </div>
      <div class="filter-tag-container">
        <div class="d-flex justify-content-space f14 mB8">
          <span class="flt-txt">{{
            $t('common.filters.filtered_results')
          }}</span>
          <span @click="resetFilters" class="fl-blue-txt pointer">{{
            $t('common.filters.clear_all')
          }}</span>
        </div>
        <div class="d-flex filter-tags-holder">
          <el-tag
            v-for="(tag, index) in filterTag"
            :key="index"
            class="round-tag d-flex"
          >
            <div class="pR5 truncate-text ftag-text">
              {{ tag.tagString }}
            </div>
            <fc-icon
              v-if="!hideClose"
              group="action"
              name="cross"
              size="16"
              color="#0074d1"
              class="pointer"
              @click="clearFilter(tag)"
            ></fc-icon>
          </el-tag>
        </div>
      </div>
      <div v-if="canSaveView" class="tags-save-filter">
        <el-button
          v-if="isSystemView"
          @click="savingView('new')"
          class="save-filter"
          >{{ $t('filters.tags.save_view') }}</el-button
        >
        <el-dropdown v-else @command="savingView">
          <el-button class="save-filter">
            {{ $t('filters.tags.save_view_as')
            }}<i class="el-icon-arrow-down el-icon--right"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item class="text-capitalize" command="new">{{
              $t('filters.tags.save_as_new')
            }}</el-dropdown-item>
            <el-dropdown-item class="text-capitalize" command="edit">{{
              $t('filters.tags.save_to_existing')
            }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-popover>
  </div>
</template>
<script>
import FTags from 'src/newapp/components/search/FTags.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: FTags,

  computed: {
    tagLength() {
      let { tagsArr = [] } = this
      return tagsArr.length
    },
    filterTag() {
      let { tagsArr = [] } = this
      return tagsArr.map(tag => {
        let {
          fieldDisplayName,
          operatorDisplayName,
          valueStr,
          parentDisplayName,
        } = tag || {}
        let tagString = ''
        if (!isEmpty(parentDisplayName)) {
          tagString = `${parentDisplayName}/${fieldDisplayName} ${operatorDisplayName} ${valueStr}`
        } else {
          tagString = `${fieldDisplayName} ${operatorDisplayName} ${valueStr}`
        }

        return { ...tag, tagString }
      })
    },
  },
}
</script>
<style lang="scss" scoped>
.ftag-text {
  font-size: 12px;
  font-weight: 400;
}
.round-tag {
  height: 20px;
  max-width: 90%;
  display: flex;
  border-radius: 10px;
  color: #0074d1;
  border: solid 1px #007adb;
  background-color: #f7feff;
  letter-spacing: 0.5px;
  font-size: 14px;
  width: fit-content;
  align-items: center;
}
.fl-blue-txt {
  color: #0074d1;
}
.tags-save-filter {
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid #d5d9dd;
}
.save-filter {
  padding: 6px 12px 6px 16px;
  height: 32px;
  border: solid 1px #a8d9ff;
  color: #0074d1;
  text-transform: capitalize;
}
.filter-tag-container {
  margin: 8px 12px;

  .mB8 {
    margin-bottom: 8px;
  }
  .flt-txt {
    color: #1d384e;
  }
  .filter-tags-holder {
    flex-direction: column;
    row-gap: 8px;
    max-height: 400px;
    overflow: scroll;
    padding: 8px 0px;
  }
}
</style>
<style lang="scss">
.filter-tags-popover {
  padding: 0px !important;
  width: 280px;
}
.filter-tags-popover .i.el-icon-arrow-down.el-icon--right {
  margin-left: 0px !important;
}
</style>
