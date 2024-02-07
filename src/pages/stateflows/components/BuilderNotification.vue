<template>
  <div v-if="!$validation.isEmpty(message)">
    <div class="pB10 pT10 pR15 pL5 message d-flex">
      <span v-if="type !== types.SAVING" class="pL12 pR12 pT7">
        <inline-svg
          :src="icon[type]"
          iconClass="icon icon-sm-md fill-white"
        ></inline-svg>
      </span>
      <span v-else class="spinner pL5 pR5">
        <spinner show="true" size="30" colour="#ffffff"></spinner>
      </span>
      <span class="mR-auto lh30">{{ message }}</span>
      <span @click="hide()" class="lh30 pL15">
        <inline-svg
          src="svgs/close"
          iconClass="icon fill-white icon-xxxs"
          class="pointer pL10"
        ></inline-svg>
      </span>
    </div>
  </div>
</template>

<script>
const types = {
  SUCCESS: 0,
  ERROR: 1,
  SAVING: 2,
}

export default {
  data() {
    return {
      message: '',
      type: null,
      icon: ['done', 'svgs/cancel'],
      types,
    }
  },
  methods: {
    showSaving(data) {
      this.message = data || 'Saving'
      this.type = this.types.SAVING
    },

    showSuccess(data) {
      this.message = data || 'Saved'
      this.type = this.types.SUCCESS
      setTimeout(() => {
        this.hide()
      }, 3500)
    },

    showError(data) {
      this.message = data || 'Error Occured'
      this.type = this.types.ERROR
      setTimeout(() => {
        this.hide()
      }, 3500)
    },

    hide() {
      this.message = ''
      this.type = null
    },
  },
}
</script>
<style scoped>
.success {
  color: #67c23a;
  background-color: #f0f9eb;
  border-color: #e1f3d8;
}
.error {
  color: #f56c6c;
  background-color: #fef0f0;
  border-color: #fde2e2;
}
.message {
  color: #ffffff;
  background-color: #000000;
  border-color: #ffffff;
  border-radius: 3px;
}
.spinner {
  margin: 0;
}
.lh30 {
  line-height: 30px;
}
</style>
