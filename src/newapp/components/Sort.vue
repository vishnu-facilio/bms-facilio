<template>
  <div class="page-sort-popover p0">
    <el-popover
      v-model="visible"
      placement="bottom"
      trigger="click"
      popper-class="sort-popover page-sort-popover"
      :visible-arrow="true"
      :tabindex="-1"
    >
      <el-input
        type="text"
        v-model="search"
        placeholder="Search..."
        class="tag-list-search"
      />
      <div
        class="pointer user-select-none sort-icon-container"
        slot="reference"
      >
        <img src="~assets/new-sortby.svg" class="sort-icon" />
      </div>
      <div link class="sort-popovernew">
        <div
          v-for="(field, index) in filteredSortFieldList"
          :key="index"
          @click.stop="updateOrderBy(field.name), close()"
          class="position-relative sort-active-check"
        >
          <div
            :label="field.name"
            :class="{
              'selected flex-middle': orderBy === field.name,
            }"
            class="fc-div-hover page-sort-list"
          >
            {{ field.displayName }}
          </div>
          <i :class="{ 'el-icon-check': orderBy === field.name }"></i>
        </div>
      </div>
      <div class="scr-sep"></div>
      <div class="sort-popovernew">
        <div
          class="position-relative sort-active-check"
          @click.stop="updateOrderType('asc'), close()"
        >
          <div class="q-item-bottom-p page-sort-list fc-div-hover flex-middle">
            <div v-if="config.orderType" separator>
              <div :class="{ selected: orderType === 'asc' }">
                {{ $t('common._common.ascending') }}
              </div>
            </div>
          </div>
          <i :class="{ 'el-icon-check': orderType === 'asc' }"></i>
        </div>
        <div
          class="position-relative sort-active-check"
          @click.stop="updateOrderType('desc'), close()"
        >
          <div
            class="q-item-bottom-p page-sort-list fc-div-hover flex-middle active-sort-type"
          >
            <div v-if="config.orderType">
              <div :class="{ selected: orderType === 'desc' }">
                {{ $t('common._common.descending') }}
              </div>
            </div>
          </div>
          <i :class="{ 'el-icon-check': orderType === 'desc' }"></i>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script>
import { isEmpty, isObject } from '@facilio/utils/validation'
import { mapState } from 'vuex'
export default {
  props: ['config', 'sortList', 'excludeFields'],
  data() {
    return {
      visible: false,
      search: '',
      skipDisplayTypeHash: [
        'MULTI_LOOKUP',
        'MULTI_ENUM',
        'MULTI_LOOKUP_SIMPLE',
        'LOOKUP_SIMPLE',
        'LOOKUP_POPUP',
      ],
    }
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
    orderBy() {
      let { orderBy } = this.config
      return isObject(orderBy)
        ? orderBy.value
        : !isEmpty(orderBy)
        ? orderBy
        : null
    },
    orderType() {
      return this.config.orderType ? this.config.orderType : null
    },
    filteredSortFieldList() {
      let { search, sortListDataType } = this
      if (!isEmpty(search)) {
        return (sortListDataType || []).filter(fld => {
          let { displayName } = fld
          let lcDisplayName = displayName.toLowerCase()
          let lcSearch = search.toLowerCase()
          return lcDisplayName.includes(lcSearch)
        })
      } else return sortListDataType || []
    },
    sortListDataType() {
      let { moduleMeta, excludeFields = [], skipDisplayTypeHash } = this
      if (!isEmpty(moduleMeta)) {
        let { fields } = moduleMeta || {}
        let currentSortFields = (fields || []).filter(fld => {
          return (this.sortList || []).includes(fld?.name)
        })
        let filteredFields = (currentSortFields || []).filter(fld => {
          let { displayType, name: fieldName } = fld || {}
          let { _name } = displayType || {}

          return (
            !skipDisplayTypeHash.includes(_name) &&
            !(excludeFields || []).includes(fieldName)
          )
        })
        return (filteredFields || []).map(fld => ({
          name: fld.name,
          displayName: fld.displayName,
        }))
      }
      return []
    },
  },
  methods: {
    close() {
      this.visible = false
    },
    updateOrderBy(orderBy) {
      this.config.orderBy = orderBy
      this.$emit('onchange', { orderBy: orderBy, orderType: this.orderType })
    },
    updateOrderType(orderType) {
      this.config.orderType = orderType
      this.$emit('onchange', { orderBy: this.orderBy, orderType: orderType })
    },
  },
}
</script>

<style scoped lang="scss">
.sort-icon-container {
  outline: none;
}
.sort-icon {
  width: 19px;
  height: 19px;
  margin-left: 5px;
  margin-right: 7px;
  margin-top: 0;
  outline: none;

  &:hover {
    fill: #e7328a;
    transition: fill 0.5s ease;
  }
}

.fc-div-hover:hover {
  cursor: pointer;
}

.sort-popover {
  margin-top: 8px;
}

.sort-popover .q-item-main.q-item-section {
  font-size: 13px;
}

.sort-popover .q-item-main.q-item-section.selected {
  font-weight: bold;
  color: #39b2c2;
}
.q-item-bottom-p {
  border-top: none !important;
}
.scr-sep {
  border-top: 1px solid #f1f1f1;
  margin-top: 10px;
  margin-bottom: 7px;
}
.sort-popover .el-icon-check {
  font-size: 14px;
  color: #39b2c2;
  font-weight: bold;
}
.sort-popover .el-icon-check:before {
  padding-right: 6px;
}
.page-sort-list {
  padding-top: 5px;
  padding-bottom: 5px;
  padding-left: 10px;
  height: 45px;
}
.selected {
  font-weight: bold;
  color: #39b2c2;
}
.sort-active-check .el-icon-check {
  position: absolute;
  right: 0;
  top: 14px;
}
.tag-list-search {
  width: 100%;
  border-top: none !important;
  border-left: none !important;
  border-right: none !important;
  border-radius: 0 !important;
  line-height: 25px !important;
}
.tag-list-search .el-input__inner {
  height: 40px;
  padding-left: 20px;
  padding-right: 20px;
  border-bottom: 1px solid #d0d9e2;
  border-top: none;
  border-left: none;
  border-right: none;
  margin-bottom: 0px;
}
.sort-popovernew {
  padding: 0;
  max-height: 210px;
  overflow-y: scroll;
  overflow-x: hidden;
  min-width: 180px;
  max-width: 300px;
}
.sort-popovernew .page-sort-list {
  padding: 10px 20px !important;
}
</style>
<style>
.page-sort-popover {
  padding: 0;
}
.tag-list-search .el-input__inner {
  padding: 20px 20px;
}

.el-popover {
  border: 1px solid #f4f4f4;
}
</style>
