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
      <el-table-column label="Email" width="240">
        <template v-slot="item">
          <div>{{ item.row.email }}</div>
        </template>
      </el-table-column>
      <el-table-column label="Role" width="240">
        <template v-slot="item">
          <div>{{ item.row.role.name }}</div>
        </template>
      </el-table-column>
      <el-table-column label="Status" width="240">
        <template v-slot="item">
          <div
            v-if="item.row.role && item.row.role.name === 'Super Administrator'"
          >
            <el-switch
              disabled
              v-model="item.row.userStatus"
              class="Notification-toggle"
              active-color="rgba(57, 178, 194, 0.8)"
              inactive-color="#e5e5e5"
            ></el-switch>
          </div>
          <div v-else>
            <el-switch
              v-if="!item.row.userStatus"
              v-model="item.row.userStatus"
              data-arrow="true"
              :title="$t('setup.users_management.exceedeed_users')"
              v-tippy
              class="Notification-"
              active-color="rgba(57, 178, 194, 0.8)"
              inactive-color="#e5e5e5"
              disabled
            ></el-switch>
            <el-switch
              v-else
              v-model="item.row.userStatus"
              data-arrow="true"
              :title="$t('setup.users_management.user_status')"
              v-tippy
              class="Notification-"
              active-color="rgba(57, 178, 194, 0.8)"
              inactive-color="#e5e5e5"
              disabled
            ></el-switch>
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
