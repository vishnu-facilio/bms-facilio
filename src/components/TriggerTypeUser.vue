<template>
  <div>
    <div class="fc-setup-modal-title pT20">Sharing Permission</div>
    <div class="mT10">
      <el-radio
        v-model="trigger.sharingContext.shareTo"
        :label="1"
        class="fc-radio-btn pB10 pT10"
        >Only Me</el-radio
      >
      <el-radio
        v-model="trigger.sharingContext.shareTo"
        :label="2"
        class="fc-radio-btn pB10"
        >Everyone</el-radio
      >
      <el-radio
        v-model="trigger.sharingContext.shareTo"
        :label="3"
        class="fc-radio-btn pB10"
        >Specific</el-radio
      >
    </div>
    <el-row
      v-if="trigger.sharingContext.shareTo === 3"
      class="mT20 el-select-block"
    >
      <el-col :span="24">
        <div class="fc-input-label-txt pB10">Team</div>
        <el-select
          filterable
          v-model="trigger.sharingContext.sharedGroups"
          multiple
          collapse-tags
          class="width100 fc-full-border-select-multiple2"
          :placeholder="$t('common.wo_report.choose_users')"
        >
          <el-option
            v-for="group in groups"
            :key="group.id"
            :label="group.name"
            :value="group.id"
          >
          </el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row
      v-if="trigger.sharingContext.shareTo === 3"
      class="mT20 el-select-block"
    >
      <el-col :span="24">
        <div class="fc-input-label-txt pB10">Role</div>
        <el-select
          filterable
          v-model="trigger.sharingContext.sharedRoles"
          multiple
          collapse-tags
          class="width100 fc-full-border-select-multiple2"
          :placeholder="$t('common.wo_report.choose_roles')"
        >
          <el-option
            v-for="role in roles"
            :key="role.id"
            :label="role.name"
            :value="role.id"
          >
          </el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row
      v-if="trigger.sharingContext.shareTo === 3"
      class="mT20 el-select-block"
    >
      <el-col :span="24">
        <div class="fc-input-label-txt pB10">Staff</div>
        <el-select
          filterable
          v-model="trigger.sharingContext.sharedUsers"
          multiple
          collapse-tags
          class="width100 fc-full-border-select-multiple2"
          :placeholder="$t('common.wo_report.choose_teams')"
        >
          <el-option
            v-for="user in users"
            :key="user.id"
            :label="user.name"
            :value="user.id"
          >
          </el-option>
        </el-select>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import { mapState } from 'vuex'

export default {
  props: ['trigger'],
  data() {
    return {}
  },
  created() {
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadUsers')
  },
  computed: {
    ...mapState({
      groups: state => state.groups,
      users: state => state.users,
      roles: state => state.roles,
    }),
  },
}
</script>
