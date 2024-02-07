<template>
  <el-popover
    v-model="visible"
    placement="bottom"
    trigger="click"
    popper-class="portal-sort-container"
    :visible-arrow="true"
  >
    <div class="pointer user-select-none pR0" slot="reference">
      <InlineSvg
        src="new-sortby"
        class="d-flex"
        iconClass="icon self-center mR5"
      ></InlineSvg>
    </div>

    <el-input type="text" v-model="search" placeholder="Search..." />
    <div class="sort-list-container">
      <div
        v-for="(sortField, index) in filteredSortList"
        :key="index"
        @click.stop="updateOrder(sortField.name, 'orderBy')"
        :class="['list-item', orderBy === sortField.name && 'active']"
      >
        <div>
          {{ sortField.displayName }}
        </div>

        <i v-if="orderBy === sortField.name" class="el-icon-check"></i>
      </div>

      <div class="seperator"></div>

      <template v-if="sortConfig.orderType">
        <div
          v-for="(type, name) in orderTypeList"
          :key="name"
          class="list-item"
          :class="['list-item', orderType === name && 'active']"
          @click.stop="updateOrder(name, 'orderType')"
        >
          {{ type }}
        </div>
      </template>
    </div>
  </el-popover>
</template>

<script>
import { isEmpty, isObject } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import { API } from '@facilio/api'

export default {
  props: ['moduleName'],
  data() {
    return {
      visible: false,
      search: '',
      sortConfig: {
        orderBy: '',
        orderType: '',
      },
      sortConfigLists: [],
      orderTypeList: { asc: 'Ascending', desc: 'Descending' },
    }
  },
  created() {
    this.loadModuleMeta()
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
    }),
    orderBy() {
      let { orderBy } = this.sortConfig
      return isObject(orderBy) ? orderBy.value : orderBy || null
    },
    orderType() {
      let { orderType } = this.sortConfig
      return orderType || null
    },
    filteredSortList() {
      let { sortConfigLists, search } = this

      return sortConfigLists.filter(field => {
        let { displayName } = field || {}

        if (!isEmpty(search)) {
          return displayName.toLowerCase().includes(search.toLowerCase())
        }
        return true
      })
    },
    currentView() {
      return this.$route.params.viewname
    },
  },
  watch: {
    viewDetail() {
      let { sortFields = [] } = this.viewDetail || {}

      if (!isEmpty(sortFields)) {
        let { name } = this.$getProperty(sortFields[0], 'sortField', {})
        let { isAscending } = this.$getProperty(sortFields, '0', {})

        this.sortConfig = {
          orderType: isAscending ? 'asc' : 'desc',
          orderBy: name ? name : '',
        }
      }
    },
  },
  methods: {
    async updateOrder(orderName, type) {
      this.$set(this.sortConfig, type, orderName)
      this.$emit('sortChange', true)
      this.visible = false
    },

    async loadModuleMeta() {
      let { moduleName } = this
      let { data, error } = await API.get(`/v2/fields/sortable/${moduleName}`)

      if (!error) {
        let { fields } = data || {}
        if (!isEmpty(fields)) {
          this.sortConfigLists = fields
        }
      }
    },
  },
}
</script>
<style lang="scss">
.portal-sort-container {
  padding: 0px;
  cursor: pointer;

  .el-input__inner {
    padding: 20px 20px;
  }
  .sort-list-container {
    max-height: 300px;
    overflow: scroll;

    .list-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 10px 20px;

      &.active {
        font-weight: bold;
        color: #39b2c2;
      }
      .el-icon-check {
        font-weight: 700;
      }
    }
    .seperator {
      margin: 10px 0;
      border-top: 1px solid #f1f1f1;
    }
  }
}
</style>
