<template>
  <div>
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
      <el-table-column v-else fixed width="60">
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
      <el-table-column label="Name" width="240">
        <template v-slot="item">
          <div>{{ item.row.name }}</div>
        </template>
      </el-table-column>
      <el-table-column label="Managed by" width="240">
        <template v-slot="item">
          <div v-if="$org.id === 116">
            {{
              item.row.data && item.row.data.ownedBy
                ? item.row.data.ownedBy
                : item.row.data && item.row.data.maintainedBy
                ? item.row.data.maintainedBy
                : '---'
            }}
          </div>
          <div v-else>
            <div v-if="item.row.managedBy && item.row.managedBy.id">
              <user-avatar
                size="md"
                :user="$store.getters.getUser(item.row.managedBy.id)"
              ></user-avatar>
            </div>
            <div v-else>---</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="Floor area / Total area" width="240">
        <template v-slot="item">
          <div>
            {{
              $util.formateSqft(item.row.grossFloorArea) +
                ' / ' +
                $util.formateSqft(item.row.area)
            }}
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import SpecialModules from '@/base/SpecialModules'

export default {
  extends: SpecialModules,
  props: ['multiSelect', 'handleSelection'],
}
</script>
