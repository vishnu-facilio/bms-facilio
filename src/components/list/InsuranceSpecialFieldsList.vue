<template>
  <div>
    <div v-if="field.name === 'insurance'">
      <div v-if="moduleData.insuranceFileName">
        <span
          class="insurance-link"
          @click="downloadAttachment(moduleData.insuranceUrl)"
          >{{ moduleData.insuranceFileName }}</span
        >
      </div>
      <div v-else>---</div>
    </div>
  </div>
</template>
<script>
export default {
  props: ['field', 'moduleData'],
  methods: {
    downloadAttachment(url) {
      window.open(url)
    },
  },
}
</script>
