<template>
  <div class="p30">
    <div class="header cards-config-header ">
      <span class="pointer" @click="onGoBack">
        <inline-svg
          src="svgs/arrow"
          class="vertical-top rotate-90 mR20"
          iconClass="icon"
        ></inline-svg>
      </span>
      {{ (cardMeta && cardMeta.name) || 'PM Reading Card Layout' }}
      <span class="pointer" @click="onClose">
        <inline-svg
          src="svgs/close"
          class="vertical-middle fR"
          iconClass="icon icon-sm"
        ></inline-svg>
      </span>
    </div>
    <div class="container mT20">
      <div class="section config-panel panel-scroll">
        <el-form
          :model="cardDataObj"
          :ref="`${this.cardLayout}_form`"
          :rules="validationRules"
          :label-position="'top'"
        >
          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item prop="title" class="mB10">
                <p class="fc-input-label-txt pB5">Title</p>
                <el-input
                  :autofocus="isNew"
                  v-model="cardDataObj.title"
                  class="width100 fc-input-full-border2"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item prop="dateRange" class="mB10">
                <p class="fc-input-label-txt pB5">Preventive Maintenance</p>
                <el-select
                  v-model="cardDataObj.pmId"
                  placeholder="Please select a period"
                  class="width100 el-input-textbox-full-border"
                  @change="loadResources"
                >
                  <template v-for="(pm, index) in pmlist">
                    <el-option
                      :label="pm.name"
                      :value="pm.id"
                      :key="index"
                    ></el-option>
                  </template>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row
            class="mB10"
            v-if="currentPM && currentPM.pmCreationType === 2"
          >
            <el-col :span="24">
              <el-form-item prop="dateRange" class="mB10">
                <p class="fc-input-label-txt pB5">
                  {{ currentPM.assignmentType === 4 ? 'Asset' : 'Space' }}
                </p>
                <el-select
                  v-model="cardDataObj.resourceId"
                  placeholder="Please select a period"
                  class="width100 el-input-textbox-full-border"
                >
                  <template v-for="(resource, index) in pmResourceList">
                    <el-option
                      :label="resource.name"
                      :value="resource.id"
                      :key="index"
                    ></el-option>
                  </template>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item prop="dateRange" class="mB10">
                <p class="fc-input-label-txt pB5">Default Period</p>
                <el-select
                  v-model="cardDataObj.dateRange"
                  placeholder="Please select a period"
                  class="width100 el-input-textbox-full-border"
                >
                  <template v-for="(dateRange, index) in dateOperators">
                    <el-option
                      :label="dateRange.label"
                      :value="dateRange.value"
                      :key="index"
                    ></el-option>
                  </template>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="preview-panel section">
        <div class="card-wrapper">
          <Card
            :cardDataObj="cardDataObj"
            :cardData="previewData"
            :cardState="previewState"
            :isLoading="isPreviewLoading"
          ></Card>
        </div>
        <!-- card tools -->
      </div>
    </div>
    <div class="d-flex mT-auto form-action-btn">
      <el-button
        class="form-btn f13 bold secondary text-center text-uppercase"
        @click="onGoBack()"
        >Cancel</el-button
      >
      <el-button
        type="primary"
        class="form-btn f13 bold primary m0 text-center text-uppercase"
        @click="save()"
        >Save</el-button
      >
    </div>
  </div>
</template>
<script>
import Config from '../base/Config'
import Card from './Card'
export default {
  name: 'PmReadingcCard1',
  extends: Config,
  components: { Card },
  data() {
    return {
      cardLayout: `pmreadings_layout_1`,
      currentPM: null,
      pmlist: [],
      pmResourceList: [],
      resourceProps: ['title', 'pmId', 'dateRange'],
      cardDataObj: {
        title: '',
        pmId: null,
        resourceId: null,
        dateRange: 'Today',
      },
    }
  },
  mounted() {
    this.loadPmList()
  },
  methods: {
    loadResources() {
      let { cardDataObj } = this
      this.pmResourceList = []
      cardDataObj.resourceId = null
      let pm = this.pmlist.find(rt => rt.id === cardDataObj.pmId)
      this.currentPM = pm
      this.$http
        .get(`/workorder/getMultiplePMResources?pmId=${cardDataObj.pmId}`)
        .then(response => {
          this.pmResourceList = response.data.multiPmResources
        })
    },
    loadPmList() {
      let self = this
      this.$http
        .get('/planned/all')
        .then(function(response) {
          self.pmlist = response.data
        })
        .catch(() => {})
    },
  },
}
</script>
<style scoped lang="scss">
.card-wrapper {
  width: 400px;
  height: 350px;
}
</style>
