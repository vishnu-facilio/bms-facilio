<template>
  <el-button
    v-if="hasPermission"
    class="fc-create-btn create-btn uppercase"
    style="background:#ef508f;"
    @click="handleClick"
  >
    <template v-if="!$validation.isEmpty(buttonDisplayName)">
      {{ buttonDisplayName }}
    </template>
    <slot v-else></slot>
  </el-button>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['to'],
  computed: {
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),

    hasPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('CREATE', currentTab)
    },
    buttonDisplayName() {
      let { currentTab } = this
      let { configJSON } = currentTab
      let { actionName } = configJSON || {}

      return actionName
    },
  },
  methods: {
    handleClick() {
      if (!isEmpty(this.to)) {
        this.$router.push(this.to)
      }
      this.$emit('click')
    },
  },
}
</script>
