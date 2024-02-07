<template>
  <el-table
    ref="listTable"
    :data="modulesList"
    style="width: 100%;"
    :fit="true"
    height="100%"
    @selection-change="handleSelection"
  >
    <template slot="empty">
      <img
        class="mT20"
        src="~statics/noData-light.png"
        width="100"
        height="100"
      />
      <div class="mT10 label-txt-black f14">
        No {{ moduleDisplayName ? moduleDisplayName : moduleName }} available.
      </div>
    </template>
    <el-table-column
      v-if="multiSelect"
      type="selection"
      width="60"
      fixed
    ></el-table-column>
    <el-table-column v-if="!multiSelect" fixed width="60">
      <template v-slot="item">
        <div>
          <el-radio
            :label="item.row.id"
            v-model="selectedId"
            class="fc-radio-btn"
            @change="setSelectedItem"
          ></el-radio>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="Name" width="333">
      <template v-slot="item">
        <div>{{ item.row.name }}</div>
      </template>
    </el-table-column>
    <el-table-column label="Category" width="333">
      <template v-slot="item">
        <div>
          {{
            item.row.spaceCategory &&
            spaceCategories &&
            item.row.spaceCategory.id > -1
              ? spaceCategories[item.row.spaceCategory.id]
              : '---'
          }}
        </div>
      </template>
    </el-table-column>
    <el-table-column label="Area" width="333">
      <template v-slot="item">
        <div>
          {{ item.row.area ? $util.formateSqft(item.row.area) : '---' }}
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>
<script>
import SpecialModules from '@/base/SpecialModules'
import { mapGetters } from 'vuex'
export default {
  extends: SpecialModules,
  props: ['multiSelect', 'handleSelection'],
  created() {
    this.$store.dispatch('loadSpaceCategory')
  },
  computed: {
    ...mapGetters(['getSpaceCategoryPickList']),
    spaceCategories() {
      return this.getSpaceCategoryPickList()
    },
  },
}
</script>
