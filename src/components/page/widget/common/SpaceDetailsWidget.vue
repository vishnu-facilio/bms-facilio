<template>
  <div>
    <div class="asset-details-widget">
      <div v-if="loading" class="container">
        <div v-for="index in [1, 2, 3]" :key="index" class="field">
          <el-row>
            <el-col :span="12">
              <span class="lines loading-shimmer"></span>
            </el-col>

            <el-col :span="12">
              <span class="lines loading-shimmer"></span>
            </el-col>
          </el-row>
        </div>

        <div class="field">
          <el-row>
            <el-col :span="12"></el-col>
            <el-col :span="12"></el-col>
          </el-row>
        </div>
      </div>
      <div v-else class="container" ref="content-container">
        <div
          v-for="(space, index) in spaceList"
          :key="space.fieldName + index"
          class="field"
        >
          <el-row>
            <el-col :span="12" class="field-label">{{
              space.fieldName
            }}</el-col>
            <el-col :span="12" class="field-value line-height22">{{
              space.displayValue
            }}</el-col>
          </el-row>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  name: 'FieldDetails',
  props: [
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'calculateDimensions',
  ],
  data() {
    return {
      moduleVsSpaceKey: {
        visitorlogging: 'visitedSpace',
        tenantunit: 'space',
      },
      spaceType: null,
      loading: true,
      spaceObj: null,
      spaceList: [],
      defaultWidgetHeight: this.layoutParams.h,
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    formateSpaceObj() {
      let { spaceObj, spaceType } = this
      if (!isEmpty(spaceObj)) {
        if (!isEmpty(spaceObj.site)) {
          this.spaceList.push({
            fieldName: 'Site',
            displayValue:
              spaceType === 1
                ? spaceObj.name
                : this.$getProperty(this, 'spaceObj.site.name', '---') || '---',
          })
        }
        if (!isEmpty(spaceObj.building)) {
          this.spaceList.push({
            fieldName: 'Building',
            displayValue:
              spaceType === 2
                ? spaceObj.name
                : this.$getProperty(this, 'spaceObj.building.name', '---') ||
                  '---',
          })
        }
        if (!isEmpty(spaceObj.floor)) {
          this.spaceList.push({
            fieldName: 'Floor',
            displayValue:
              spaceType === 3
                ? spaceObj.name
                : this.$getProperty(this, 'spaceObj.floor.name', '---') ||
                  '---',
          })
        }
        if (!isEmpty(spaceObj.space)) {
          this.spaceList.push({
            fieldName: 'Space',
            displayValue:
              spaceType === 4
                ? spaceObj.name
                : this.$getProperty(this, 'spaceObj.space.name', '---') ||
                  '---',
          })
        }
        if (!isEmpty(spaceObj.space1)) {
          this.spaceList.push({
            fieldName: 'Space 1',
            displayValue:
              this.$getProperty(this, 'spaceObj.space1.name', '---') || '---',
          })
        }
        if (!isEmpty(spaceObj.space2)) {
          this.spaceList.push({
            fieldName: 'Space 2',
            displayValue:
              this.$getProperty(this, 'spaceObj.space2.name', '---') || '---',
          })
        }
        if (!isEmpty(spaceObj.space3)) {
          this.spaceList.push({
            fieldName: 'Space 3',
            displayValue:
              this.$getProperty(this, 'spaceObj.space3.name', '---') || '---',
          })
        }
        if (!isEmpty(spaceObj.space4)) {
          this.spaceList.push({
            fieldName: 'Space 4',
            displayValue:
              this.$getProperty(this, 'spaceObj.space4.name', '---') || '---',
          })
        }
        this.autoResize()
      }
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['content-container']
        if (!container) return

        let height = this.$refs['content-container'].scrollHeight + 60
        let width = this.$refs['content-container'].scrollWidth

        let { h } = this.calculateDimensions({ height, width })
        let params = {}
        if (h <= this.defaultWidgetHeight) {
          params = { height, width }
        } else {
          params = { h: h }
        }
        this.resizeWidget(params)
      })
    },
    init() {
      let { moduleVsSpaceKey, details, moduleName } = this
      let spaceObj = details[`${moduleVsSpaceKey[moduleName]}`]
      this.loading = true
      if (!isEmpty(spaceObj) && !isEmpty(spaceObj.id)) {
        this.$http
          .get(`v2/basespaces/${spaceObj.id}`)
          .then(({ data: { result, message, responseCode } }) => {
            if (responseCode === 0) {
              this.loading = false
              this.spaceObj = result.basespace || {}
              this.spaceType = (this.spaceObj || {}).spaceType
              this.formateSpaceObj()
            } else {
              throw new Error(message)
            }
          })
          .catch(({ message }) => {
            this.loading = false
            this.$message.error(message)
          })
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.asset-details-widget {
  padding: 10px 30px 0px;
  text-align: left;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.container {
  padding-left: 10px;
  font-size: 13px;
  display: flex;
  flex-wrap: wrap;
  overflow: hidden;
}
.field {
  flex: 0 50%;
  padding: 20px 0;
  border-bottom: 1px solid #edf4fa;
  transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
}

.lines {
  height: 15px;
  width: 200px;
  margin: 0px 20px 0px 20px;
  border-radius: 5px;
}

.field-label,
.field-value {
  word-break: break-word;
  padding-right: 10px;
}
.field-label {
  color: #324056;
  font-weight: 500;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.5px;
}
.field-value {
  padding-left: 10px;
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.5px;
  color: #324056;
}
</style>
