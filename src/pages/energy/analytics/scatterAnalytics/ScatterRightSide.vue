<template>
  <div>
    <div class="overflow-scroll-flex min-width0 min-height0">
      <div class="width100 fc-analytics-right-sidebar fc-overflow-y pB200">
        <div class="min-width100">
          <el-tabs
            v-if="scatterType === 1"
            v-model="childTab"
            class="fc-scatter-analytics-tab-child fc-tab-heading-left20"
          >
            <el-tab-pane label="Axis" name="axis">
              <div class="p20 pT0">
                <div class="">
                  <div class="fc-pink bold f13 line-height25">
                    {{ $t('analytics.analytics.xaxis') }}
                  </div>
                  <div>
                    <el-input
                      class="fc-input-full-border-select2 width100"
                      v-model="report.options.axis.x.label.text"
                      placeholder="X Label"
                    ></el-input>
                  </div>
                </div>
                <div class="pT20">
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <div class="fc-grey7-12 f13 text-left line-height25">
                        {{ $t('analytics.analytics.xmin') }}
                      </div>
                      <el-input
                        placeholder="auto"
                        class="fc-input-full-border-select2 width100"
                        v-model="report.options.axis.x.range.min"
                      ></el-input>
                    </el-col>
                    <el-col :span="12">
                      <div class="fc-grey7-12 f13 text-left line-height25">
                        {{ $t('analytics.analytics.xmax') }}
                      </div>
                      <el-input
                        placeholder="auto"
                        class="fc-input-full-border-select2 width100"
                        v-model="report.options.axis.x.range.max"
                      ></el-input>
                    </el-col>
                  </el-row>
                </div>
                <div class="pT20">
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <div class="fc-grey7-12 f13 text-left line-height25">
                        {{ $t('analytics.analytics.ticks_count') }}
                      </div>
                      <el-input
                        placeholder="auto"
                        class="fc-input-full-border-select2 width100"
                        v-model.number="report.options.axis.x.ticks.count"
                      ></el-input>
                    </el-col>
                    <el-col :span="12">
                      <div class="fc-grey7-12 f13 text-left line-height25">
                        {{ $t('analytics.analytics.decimals') }}
                      </div>
                      <el-input
                        class="fc-input-full-border-select2 width100"
                        v-model.number="report.options.axis.x.format.decimals"
                      ></el-input>
                    </el-col>
                  </el-row>
                </div>
              </div>
              <div class="y-axis-container fc-y-axis-container mT0">
                <div class="yaxis-tab-block clearboth pT20 fc-yaxis-tab-block ">
                  <el-tabs type="card">
                    <el-tab-pane label="Y1 Axis">
                      <div class="p20 pT0">
                        <div
                          class="customize-input-block pR20"
                          style="margin-top: 20px;"
                        >
                          <p class="customize-label">Show</p>
                          <el-row>
                            <el-col :span="18">
                              <el-checkbox
                                v-model="report.options.axis.y.show"
                              />
                            </el-col>
                          </el-row>
                        </div>
                        <div class="pT30">
                          <div class="fc-pink bold f13 line-height25">
                            {{ $t('analytics.analytics.y1axis_label') }}
                          </div>
                          <div>
                            <el-input
                              type="text"
                              placeholder="Y1 Label"
                              class="fc-input-full-border-select2 width100"
                              v-model="report.options.axis.y.label.text"
                            ></el-input>
                          </div>
                        </div>
                        <div class="pT20">
                          <el-row :gutter="20">
                            <el-col :span="12">
                              <div
                                class="fc-grey7-12 f13 text-left line-height25"
                              >
                                {{ $t('analytics.analytics.ymin') }}
                              </div>
                              <el-input
                                class="fc-input-full-border-select2 width100"
                                v-model.number="report.options.axis.y.range.min"
                                placeholder="auto"
                              ></el-input>
                            </el-col>
                            <el-col :span="12">
                              <div
                                class="fc-grey7-12 f13 text-left line-height25"
                              >
                                {{ $t('analytics.analytics.ymax') }}
                              </div>
                              <el-input
                                class="fc-input-full-border-select2 width100"
                                v-model.number="report.options.axis.y.range.max"
                                placeholder="auto"
                              ></el-input>
                            </el-col>
                          </el-row>
                        </div>
                        <div class="pT20">
                          <el-row :gutter="20">
                            <el-col :span="12">
                              <div
                                class="fc-grey7-12 f13 text-left line-height25"
                              >
                                {{ $t('analytics.analytics.ticks_count') }}
                              </div>
                              <el-input
                                class="fc-input-full-border-select2 width100"
                                v-model.number="
                                  report.options.axis.y.ticks.count
                                "
                              ></el-input>
                            </el-col>
                            <el-col :span="12">
                              <div
                                class="fc-grey7-12 f13 text-left line-height25"
                              >
                                {{ $t('analytics.analytics.decimals') }}
                              </div>
                              <el-input
                                class="fc-input-full-border-select2 width100"
                                v-model.number="
                                  report.options.axis.y.format.decimals
                                "
                              ></el-input>
                            </el-col>
                          </el-row>
                        </div>
                      </div>
                    </el-tab-pane>
                    <el-tab-pane label="Y2 Axis">
                      <div class="p20 pT0">
                        <div
                          class="customize-input-block pR20"
                          style="margin-top: 20px;"
                        >
                          <p class="customize-label">Show</p>
                          <el-row>
                            <el-col :span="18">
                              <el-checkbox
                                v-model="report.options.axis.y2.show"
                              />
                            </el-col>
                          </el-row>
                        </div>
                        <div class="pT30">
                          <div class="fc-pink bold f13 line-height25">
                            {{ $t('analytics.analytics.y2axis_label') }}
                          </div>
                          <div>
                            <el-input
                              type="text"
                              placeholder="Y2 Label"
                              class="fc-input-full-border-select2 width100"
                              v-model="report.options.axis.y2.label.text"
                            ></el-input>
                          </div>
                        </div>
                        <div class="pT20">
                          <el-row :gutter="20">
                            <el-col :span="12">
                              <div
                                class="fc-grey7-12 f13 text-left line-height25"
                              >
                                {{ $t('analytics.analytics.ymin') }}
                              </div>
                              <el-input
                                class="fc-input-full-border-select2 width100"
                                v-model.number="
                                  report.options.axis.y2.range.min
                                "
                                placeholder="auto"
                              ></el-input>
                            </el-col>
                            <el-col :span="12">
                              <div
                                class="fc-grey7-12 f13 text-left line-height25"
                              >
                                {{ $t('analytics.analytics.ymax') }}
                              </div>
                              <el-input
                                class="fc-input-full-border-select2 width100"
                                v-model.number="
                                  report.options.axis.y2.range.max
                                "
                                placeholder="auto"
                              ></el-input>
                            </el-col>
                          </el-row>
                        </div>
                        <div class="pT20">
                          <el-row :gutter="20">
                            <el-col :span="12">
                              <div
                                class="fc-grey7-12 f13 text-left line-height25"
                              >
                                {{ $t('analytics.analytics.ticks_count') }}
                              </div>
                              <el-input
                                class="fc-input-full-border-select2 width100"
                                v-model.number="
                                  report.options.axis.y2.ticks.count
                                "
                              ></el-input>
                            </el-col>
                            <el-col :span="12">
                              <div
                                class="fc-grey7-12 f13 text-left line-height25"
                              >
                                {{ $t('analytics.analytics.decimals') }}
                              </div>
                              <el-input
                                class="fc-input-full-border-select2 width100"
                                v-model.number="
                                  report.options.axis.y2.format.decimals
                                "
                              ></el-input>
                            </el-col>
                          </el-row>
                        </div>
                      </div>
                    </el-tab-pane>
                  </el-tabs>
                </div>
              </div>
            </el-tab-pane>
            <!-- <el-tab-pane label="Legend" name="legend"
              ><div class="customize-tab-body-axis">
                <div class="customize-scroll-hidden">
                  <div class="customize-scroll pR5">
                    <div class="customize-input-block pR20 pL20">
                      <el-row>
                        <el-col :span="18">
                          <div class="fc-text-pink uppercase line-height24">
                            Chart Legend
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <span class="customize-label pR10">Show</span>
                          <el-checkbox v-model="report.options.legend.show" />
                        </el-col>
                      </el-row>
                    </div>

                    <div
                      v-if="report.options.settings.chartMode !== 'multi'"
                      class="customize-input-block customize-radio-block scatter-customize-radio-block pT10"
                    >
                      <p class="customize-label pR20 pL20">Position</p>
                      <div class="pR20 pL20">
                        <el-radio-group
                          v-model="report.options.legend.position"
                        >
                          <el-radio-button label="top">Top</el-radio-button>
                          <el-radio-button label="bottom"
                            >Bottom</el-radio-button
                          >
                        </el-radio-group>
                      </div>
                    </div>
                  </div>
                </div>
              </div></el-tab-pane
            > -->
            <el-tab-pane label="Style" name="style"
              ><div class="customize-tab-body-axis pT0">
                <div class="customize-scroll-hidden">
                  <div class="customize-scroll pR5">
                    <div class="label-txt-black pR20 pL20">General</div>

                    <div class="customize-input-block pR20 pL20 pT20">
                      <el-row>
                        <!-- <el-col :span="24">
                          <el-checkbox
                            v-model="report.options.line.connectNull"
                            class="mR10"
                          />
                          <span class="customize-label"
                            >Connect Missing Data</span
                          >
                        </el-col> -->
                        <el-col :span="24" class="pT10">
                          <el-checkbox
                            v-model="report.options.general.labels"
                            class="mR10"
                          />
                          <span class="customize-label">Data Labels</span>
                        </el-col>
                      </el-row>
                    </div>

                    <div class="customize-input-block pR20 pL20 pT20">
                      <p class="label-txt-black">Grid Line</p>
                      <el-row class="mT10">
                        <el-col :span="12">
                          <el-checkbox
                            v-model="report.options.general.grid.x"
                            class="customize-label"
                          />
                          <span class="customize-label pL10">X Grid</span>
                        </el-col>
                        <el-col :span="12">
                          <el-checkbox
                            v-model="report.options.general.grid.y"
                            class="customize-label"
                          />
                          <span class="customize-label pL10">Y Grid</span>
                        </el-col>
                      </el-row>
                    </div>
                  </div>
                </div>
              </div></el-tab-pane
            >
          </el-tabs>
          <!--   -->
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: ['report', 'scatterType'],
  data() {
    return {
      parentTab: 'first',
      childTab: 'axis',
    }
  },
  methods: {},
}
</script>
<style scoped>
.el-tabs--top .el-tabs__item.is-top:nth-child(2) {
  padding-left: 20px;
}
</style>
