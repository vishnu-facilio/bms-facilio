<template>
  <div>
    <div class="height100 flex-center-vH flex-wrap">
      <el-row class="p30">
        <el-col :span="6">
          <user-avatar
            size="lg"
            :name="false"
            :user="getUser(requestedForId)"
            v-if="requestedForId"
          ></user-avatar>
          <user-avatar
            size="lg"
            :name="false"
            :user="null"
            v-else
          ></user-avatar>
        </el-col>
        <el-col :span="18">
          <div class="fc-black-13 fwBold text-uppercase text-left">
            {{ $t('common.products.requested_for') }}
          </div>
          <div class="fc-black-13 text-left pT8">
            {{ requestedForName }}
          </div>
        </el-col>
      </el-row>
      <el-row class="p30">
        <el-col :span="6">
          <user-avatar
            size="lg"
            :name="false"
            :user="getUser(requestedById)"
            v-if="requestedById"
          ></user-avatar>
          <user-avatar
            size="lg"
            :name="false"
            :user="null"
            v-else
          ></user-avatar>
        </el-col>
        <el-col :span="18">
          <div class="fc-black-13 fwBold text-uppercase text-left">
            {{ $t('common.products.requested_by') }}
          </div>
          <div class="fc-black-13 text-left pT8">
            {{ requestedByName }}
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import { mapState, mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['details'],
  components: {
    UserAvatar,
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
    ...mapGetters(['getUser']),
    requestedForId() {
      let { requestedFor } = this.details || {}
      let { id } = requestedFor || {}
      return !isEmpty(id) ? id : null
    },
    requestedById() {
      let { requestedBy } = this.details || {}
      let { id } = requestedBy || {}
      return !isEmpty(id) ? id : null
    },
    requestedForName() {
      let { requestedForId } = this
      let { name } = requestedForId ? this.getUser(requestedForId) : {}

      return name ? name : '---'
    },
    requestedByName() {
      let { requestedById } = this
      let { name } = requestedById ? this.getUser(requestedById) : {}

      return name ? name : '---'
    },
  },
}
</script>
