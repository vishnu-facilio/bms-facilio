<template>
  <div v-if="isActionConfigured">
    <el-row class="pT10 pB10">
      <el-col>
        <div class=" display-flex-between-space">
          <div>{{ helperText }}</div>
          <div class="fc__layout__has__columns">
            <div class="pR10 configured-green f12 pT2 border-right2">
              Configured
            </div>
            <div class="pointer pL10" @click="edit">
              <i class="el-icon-edit"></i>
            </div>
            <div class="pL12 pointer" @click="reset">
              Reset
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    <hr class="separator-line" />
  </div>
</template>

<script>
export default {
  props: ['isActionConfigured', 'helperText'],
  methods: {
    edit() {
      this.$emit('edit')
    },
    reset() {
      this.$emit('reset')
    },
  },
}
</script>

<style>
.configure-blue {
  color: #6171db;
}
.configured-green {
  color: #5bc293;
}
</style>
