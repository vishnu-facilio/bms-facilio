<template>
  <div class="details_fc" ref="failureclass-desc">
    <div class="flx">
      <div class="details">{{ $t('common.failure_class.details') }}</div>
    </div>
    <div class="details_body">
      <div class="description">
        {{ $t('common.failure_class.description') }}
      </div>
      <span class="d-flex mT5">
        {{ this.getDescription() }}
      </span>
    </div>
    <div class="flx setup-dialog80 ">
      <div>
        <div class="description">
          {{ $t('common.failure_class.created_by') }}
        </div>
        <span class="d-flex mT5">
          {{ this.getCreatedBy() }}
        </span>
      </div>
      <div>
        <div class="description">
          {{ $t('common.failure_class.created_time') }}
        </div>
        <span class="d-flex mT5">
          {{ getCreatedTime() }}
        </span>
      </div>
      <div>
        <div class="description">
          {{ $t('common.failure_class.modified_by') }}
        </div>
        <span class="d-flex mT5">
          {{ this.getModifiedBy() }}
        </span>
      </div>
      <div>
        <div class="description">
          {{ $t('common.failure_class.modified_time') }}
        </div>
        <span class="d-flex mT5">
          {{ getModifiedTime() }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['details', 'resizeWidget'],
  mounted() {
    this.autoResize()
  },
  methods: {
    getDescription() {
      return this.$getProperty(this.details, 'description', '---')
    },
    getCreatedBy() {
      return this.$getProperty(this.details, 'sysCreatedBy.name', '---')
    },
    getModifiedBy() {
      return this.$getProperty(this.details, 'sysModifiedBy.name', '---')
    },
    getCreatedTime() {
      let createdTime = this.$options.filters.formatDate(
        this.details.sysCreatedTime
      )
      return createdTime ? createdTime : '---'
    },
    getModifiedTime() {
      let modifiedTime = this.$options.filters.formatDate(
        this.details.sysModifiedTime
      )
      return modifiedTime ? modifiedTime : '---'
    },
    autoResize() {
      this.$nextTick(() => {
        setTimeout(() => {
          const LENGHT = 200
          let container = this.$refs['failureclass-desc']
          let description = this.getDescription()
          if (container && description.length > LENGHT) {
            let height = container.scrollHeight + 50
            let width = container.scrollWidth
            if (this.resizeWidget) {
              this.resizeWidget({ height: height, width })
            }
          }
        })
      })
    },
  },
}
</script>

<style scoped>
.details_fc {
  margin: 5px;
  padding: 24px 24px 32px;
  background-color: #fff;
}
.flx {
  display: flex;
  justify-content: space-between;
}
.ic_delete {
  margin: 0 20px 0 0;
  object-fit: contain;
}
.Shape {
  margin: 0 24px 0 0;
  padding: 4px;
}
.details {
  margin: 1px 0px 1px 0;
  font-size: 15px;
  font-weight: 450;
  color: #324056;
}
.description {
  opacity: 0.5;
  font-size: 12px;
  letter-spacing: 0.16px;
  color: #324056;
}
.details_body {
  margin: 25px 0;
}
</style>
