<template>
  <div>
    <el-form>
      <div v-if="!$validation.isEmpty(urls)">
        <el-row
          v-for="(url, index) in urls"
          :key="index"
          class="visibility-visible-actions"
        >
          <div class="mT20">
            <el-col :span="19" class="el-select-block">
              <el-input
                v-model="urls[index]"
                @change="update(url)"
                class="fc-input-full-border2"
              ></el-input>
            </el-col>
            <el-col class="visibility-hide-actions" :span="5">
              <img
                src="~assets/add-icon.svg"
                style="height:18px;width:18px;margin-left: 32px;margin-top: 10px;"
                class="delete-icon pointer"
                @click="addRow()"
              />
              <img
                v-if="!$validation.isEmpty(urls) && urls.length > 1"
                src="~assets/remove-icon.svg"
                style="height:18px;width:18px;margin-left: 10px;"
                class="delete-icon pointer"
                @click="deleteRow(index)"
              />
            </el-col>
          </div>
        </el-row>
      </div>
      <div v-else>
        <el-row class="visibility-visible-actions">
          <div class="mT20">
            <el-col :span="19" class="el-select-block">
              <el-input
                v-model="urls[0]"
                @change="update(urls[0])"
                class="fc-input-full-border2"
              ></el-input>
            </el-col>
            <el-col class="visibility-hide-actions" :span="3">
              <img
                src="~assets/add-icon.svg"
                style="height:18px;width:18px;margin-left: 32px;margin-top: 10px;"
                class="delete-icon pointer"
                @click="addRow()"
              />
            </el-col>
          </div>
        </el-row>
      </div>
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {}
  },
  props: ['urlArray'],
  computed: {
    urls: {
      get() {
        return this.urlArray
      },
      set(value) {
        this.$emit('update:urlArray', value)
      },
    },
  },
  methods: {
    update() {},
    addRow() {
      this.urls.push('')
    },
    deleteRow(index) {
      this.urls.splice(index, 1)
    },
  },
}
</script>
