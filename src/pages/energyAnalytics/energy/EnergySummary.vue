<template>
  <div>
    <div class="fc-energy-summary">
      <el-row>
        <el-col :span="5">
          <div class="fc-energy-sum-side">
            <el-header height="60" class="fc-energy-sum-side-header">
              <div class="fc-black4-16 bold">
                ENERGY STAR <span class="">&#174;</span> Score
              </div>
              <div class="fc-dark-blue5 pT8">
                <a @click="goBack" class="fc-dark-blue5">
                  Portfolio View
                </a>
              </div>
            </el-header>
            <div class="fc-energy-sum-side-scroll">
              <div>
                <div>
                  <div
                    class="label-txt-black fc-energy-sum-side-list"
                    v-for="listData in sideListDatas"
                    :key="listData.id"
                    @click="goToSummary(listData.id)"
                    :class="{ energySideActive: openSummary === listData.id }"
                  >
                    <el-row class="d-flex items-center">
                      <el-col :span="3">
                        <img
                          v-if="
                            $getProperty(
                              listData,
                              'data.building.avatarUrl',
                              false
                            )
                          "
                          :src="listData.data.building.avatarUrl"
                          alt
                          width="30"
                          height="30"
                          class="fc-energy-sidebar-icon"
                        />
                        <img
                          src="~assets/svgs/spacemanagement/building.svg"
                          width="30"
                          height="30"
                          class="fc-energy-sidebar-icon"
                          v-else
                        />
                      </el-col>
                      <el-col :span="21" class="pL10">
                        {{
                          $getProperty(listData, 'data.building.name', '---')
                        }}
                      </el-col>
                    </el-row>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="19">
          <el-header
            v-if="loading || $validation.isEmpty(summaryData)"
            height="97px"
            class="fc-energy-sum-main-header"
          >
            <el-col :span="2">
              <div
                class="fc-animated-background fc-avatar-empty-loading fc-load-size70"
              ></div>
            </el-col>
            <el-col :span="21" class="mT20 pL10">
              <div class="fc-animated-background pT5 pB5 width50"></div>
              <div class="fc-animated-background pT5 pB5 width30 mT10"></div>
              <div class="fc-animated-background pT5 pB5 width20 mT10"></div>
            </el-col>
          </el-header>
          <el-header v-else height="97px" class="fc-energy-sum-main-header">
            <el-row>
              <el-col :span="2">
                <img
                  v-if="summaryData.data && summaryData.data.building.avatarUrl"
                  :src="summaryData.data.building.avatarUrl"
                  alt
                  class="energy-img-con"
                />
                <img
                  src="~assets/svgs/spacemanagement/building.svg"
                  class="energy-img-con"
                  v-else
                />
              </el-col>
              <el-col :span="21" class="pL10">
                <div class="fc-id">#{{ summaryData.buildingId }}</div>
                <div class="fc-black3-20">
                  {{ $getProperty(summaryData, 'data.building.name', '---') }}
                </div>
                <div class="flex-middle pT10">
                  <div class="fc-black-13 text-left pR10">
                    Year Built:
                    <span class="bold">
                      {{
                        summaryData.yearBuild ? summaryData.yearBuild : '---'
                      }}
                    </span>
                  </div>
                  <el-divider direction="vertical"></el-divider>
                  <div class="fc-black-13 text-left pL10 pR10">
                    Property Type:
                    <span class="bold">
                      {{
                        $getProperty(
                          summaryData,
                          'buildingTypeEnum.name',
                          '---'
                        )
                      }}
                    </span>
                  </div>
                  <el-divider direction="vertical"></el-divider>
                  <div class="fc-black-13 text-left pL10 pR10">
                    Gross Floor Area:
                    <span class="bold">
                      {{
                        numberWithCommas(
                          summaryData.data &&
                            summaryData.data.building.grossFloorArea
                            ? summaryData.data.building.grossFloorArea
                            : '---'
                        )
                      }}
                      Sq.Ft
                    </span>
                  </div>
                </div>
              </el-col>
            </el-row>
          </el-header>
          <el-tabs
            v-model="activeName"
            @tab-click="handleClick"
            class="fc-energy-tab-sum"
          >
            <el-tab-pane label="SUMMARY" name="first">
              <div class="fc-energy-height-scroll pT10">
                <div class="pL20 pR20 pT10">
                  <div class="p20 white-bg-block">
                    <el-row>
                      <el-col :span="12" class="fc-card-data-border">
                        <el-col :span="12">
                          <div class="fc-black-13 text-left bold">
                            Property Address
                          </div>
                        </el-col>
                        <el-col :span="12">
                          <div class="fc-black-13 text-left">
                            {{
                              $account.org.street ? $account.org.street : '---'
                            }}
                          </div>
                        </el-col>
                      </el-col>
                      <el-col :span="12" class="fc-card-data-border">
                        <el-col :span="12">
                          <div class="fc-black-13 text-left bold">
                            Weekly Operating Hours
                          </div>
                        </el-col>
                        <el-col :span="12">
                          <div class="fc-black-13 text-left">
                            {{
                              $getProperty(
                                summaryData,
                                'data.building.operatingHour',
                                '---'
                              )
                            }}
                          </div>
                        </el-col>
                      </el-col>
                    </el-row>

                    <el-row>
                      <el-col
                        :span="12"
                        class="pB20 fc-card-data-border"
                        v-for="(propertyContextData,
                        index) in propertyContextDatas"
                        :key="index"
                        v-if="index < limitBy"
                      >
                        <el-col :span="12" v-if="propertyContextData">
                          <div class="fc-black-13 text-left bold">
                            {{
                              $getProperty(
                                propertyContextData,
                                'properyUseTypeEnum.displayName',
                                ''
                              )
                            }}
                          </div>
                        </el-col>
                        <el-col :span="12" v-if="propertyContextData">
                          <div class="fc-black-13 text-left">
                            {{
                              propertyContextData.value
                                ? propertyContextData.value
                                : ''
                            }}
                            {{
                              $getProperty(
                                propertyContextData,
                                'properyUseTypeEnum.unit',
                                ''
                              )
                            }}
                          </div>
                        </el-col>
                      </el-col>
                    </el-row>
                    <div class="text-center pT15">
                      <a
                        href="javascript:void(0)"
                        class="fc-dark-blue3-12 bold"
                        @click="
                          toggle(defaultLimit, propertyContextDatas.length)
                        "
                        >{{ limitBy === 0 ? 'View More' : 'View Less' }}</a
                      >
                    </div>
                  </div>
                </div>
                <div class="fc-black-15 f18 p20 bold pB0">
                  Energy Star Metrics
                </div>
                <div class="d-flex pT10 pL20 pB10">
                  <div class="d-flex">
                    <div class="fc-black-13">Current :</div>
                    <div class="fc-black-13 bold pL10">
                      {{ formatTime(summaryData.currentTime - 31540000000) }} to
                      {{ formatTime(summaryData.currentTime) }}
                    </div>
                  </div>
                  <div class="d-flex pL20">
                    <div class="fc-black-13">
                      Baseline Comparison :
                    </div>
                    <div class="pL10 fc-black-13 bold">
                      {{ formatTime(summaryData.baselineMonth - 31100000000) }}
                      to {{ formatTime(summaryData.baselineMonth) }}
                    </div>
                  </div>
                </div>
                <el-row
                  class="pL20 pR20 pT10"
                  :gutter="20"
                  style="height:200px"
                >
                  <el-col :span="12" class="height100">
                    <div
                      v-if="loading"
                      class="flex-middle align-start flex-direction-column  white-bg-block fc-energy-bar-chart height220 p20"
                    >
                      <div
                        class="fc-animated-background pT10 pB10 width30 text-left mT10"
                      ></div>
                      <div
                        class="fc-animated-background pT10 pB10 width100 height30 mT20"
                      ></div>
                      <div
                        class="fc-animated-background pT5 pB5 mT20 height20 width60"
                      ></div>
                    </div>
                    <div
                      v-if="
                        $validation.isEmpty(
                          metricValueDatas && widgets.score
                        ) && !loading
                      "
                      class="flex-middle justify-content-center flex-direction-column  white-bg-block fc-energy-bar-chart height220"
                    >
                      <inline-svg
                        src="svgs/emptystate/reportlist"
                        iconClass="icon text-center icon-xxxlg"
                      ></inline-svg>
                      <div class="nowo-label pT10 f15">
                        No Energy star Score
                      </div>
                    </div>
                    <div
                      class="height100"
                      v-if="
                        !loading &&
                          !$validation.isEmpty(
                            metricValueDatas && widgets.score
                          )
                      "
                    >
                      <div class="p20 white-bg-block fc-energy-bar-chart pB40">
                        <div class="fc-black-15 p10 bold">
                          ENERGY STAR Score
                        </div>
                        <f-progress-bar-chart
                          v-if="activeName === 'first'"
                          v-show="widgets.score"
                          :fixedChartHeight="null"
                          :isWidget="true"
                          :data="widgets.score ? widgets.score.data : null"
                          :options="widgetOptions"
                          :colors="defaultcolors.score"
                          :unit="tabunitmap.score"
                          class="height100"
                        ></f-progress-bar-chart>
                        <div
                          class="fc-energy-dot flex-middle width100 pT20 justify-content-center"
                        >
                          <div
                            class="flex-middle flex-direction-column align-start"
                            v-if="
                              $getProperty(metricValueDatas, 'score.current')
                            "
                          >
                            <div
                              class="fc-black-com f24 text-left bold pL20"
                              :class="
                                fontsize(
                                  $getProperty(
                                    metricValueDatas,
                                    'score.current'
                                  )
                                )
                              "
                            >
                              {{
                                numberWithCommas(
                                  $getProperty(
                                    metricValueDatas,
                                    'score.current'
                                  )
                                )
                              }}
                            </div>
                            <div class="flex-middle">
                              <div class="fc-dot fc-dot-blue"></div>
                              <div class="fc-black-12 pL10 bold">
                                Current
                              </div>
                            </div>
                          </div>

                          <div class="flex-middle mL20">
                            <div
                              class="flex-middle flex-direction-column align-start"
                              v-if="
                                $getProperty(metricValueDatas, 'score.baseline')
                              "
                            >
                              <div
                                class="fc-black-com f24 text-left bold pL20"
                                :class="
                                  fontsize(
                                    $getProperty(
                                      metricValueDatas,
                                      'score.baseline'
                                    )
                                  )
                                "
                              >
                                {{
                                  numberWithCommas(
                                    $getProperty(
                                      metricValueDatas,
                                      'score.baseline'
                                    )
                                  )
                                }}
                              </div>
                              <div class="flex-middle">
                                <div class="fc-dot-dark-blue fc-dot"></div>
                                <div class="fc-black-12 pL10 bold">
                                  Baseline
                                </div>
                              </div>
                            </div>
                          </div>

                          <div class="flex-middle mL20">
                            <div
                              class="flex-middle flex-direction-column align-start"
                              v-if="
                                $getProperty(metricValueDatas, 'score.median')
                              "
                            >
                              <div
                                class="fc-black-com f24 text-left bold pL20"
                                :class="
                                  fontsize(
                                    $getProperty(
                                      metricValueDatas,
                                      'score.median'
                                    )
                                  )
                                "
                              >
                                {{
                                  numberWithCommas(
                                    $getProperty(
                                      metricValueDatas,
                                      'score.median'
                                    )
                                  )
                                }}
                              </div>
                              <div class="flex-middle">
                                <div class="fc-dot-red fc-dot"></div>
                                <div class="fc-black-12 pL10 bold">
                                  National Median
                                </div>
                              </div>
                            </div>
                          </div>
                          <div class="flex-middle mL20 align-start">
                            <div
                              class="flex-middle flex-direction-column"
                              v-if="
                                $getProperty(metricValueDatas, 'score.target')
                              "
                            >
                              <div
                                class="fc-black-com f24 text-left bold"
                                :class="
                                  fontsize(
                                    $getProperty(
                                      metricValueDatas,
                                      'score.target'
                                    )
                                  )
                                "
                              >
                                {{
                                  numberWithCommas(
                                    $getProperty(
                                      metricValueDatas,
                                      'score.target'
                                    )
                                  )
                                }}
                              </div>
                              <div class="flex-middle">
                                <div class="fc-dot-green fc-dot"></div>
                                <div class="fc-black-12 pL10 bold">
                                  Target
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </el-col>
                  <el-col :span="12" class="height100">
                    <div
                      v-if="loading"
                      class="flex-middle align-start flex-direction-column  white-bg-block fc-energy-bar-chart height220 p20"
                    >
                      <div
                        class="fc-animated-background pT10 pB10 width30 text-left mT10"
                      ></div>
                      <div
                        class="fc-animated-background pT10 pB10 width100 height30 mT20"
                      ></div>
                      <div
                        class="fc-animated-background pT5 pB5 mT20 height20 width60"
                      ></div>
                    </div>
                    <div
                      v-if="
                        $validation.isEmpty(
                          metricValueDatas && widgets.energyCost
                        ) && !loading
                      "
                      class="flex-middle justify-content-center flex-direction-column  white-bg-block fc-energy-bar-chart height220"
                    >
                      <inline-svg
                        src="svgs/emptystate/reportlist"
                        iconClass="icon text-center icon-xxxlg"
                      ></inline-svg>
                      <div class="nowo-label pT10 f15">
                        No Energy Cost
                      </div>
                    </div>
                    <div
                      class="height100"
                      v-if="
                        !loading &&
                          !$validation.isEmpty(
                            metricValueDatas && widgets.energyCost
                          )
                      "
                    >
                      <div class="p20 white-bg-block fc-energy-bar-chart pB40">
                        <div class="fc-black-15 p10 bold">Energy Cost</div>
                        <f-progress-bar-chart
                          v-if="activeName === 'first'"
                          v-show="widgets.energyCost"
                          :fixedChartHeight="null"
                          :isWidget="true"
                          :data="
                            widgets.energyCost ? widgets.energyCost.data : null
                          "
                          :options="widgetOptions"
                          :colors="defaultcolors.energyCost"
                          :unit="tabunitmap.energyCost"
                          class="height100"
                        >
                        </f-progress-bar-chart>
                        <div
                          class="fc-energy-dot flex-middle width100 pT20 justify-content-center"
                        >
                          <div
                            class="flex-middle flex-direction-column align-start"
                            v-if="
                              $getProperty(
                                metricValueDatas,
                                'energyCost.current'
                              )
                            "
                          >
                            <div
                              class="fc-black-com f24 text-center bold pL20"
                              :class="
                                fontsize(
                                  $getProperty(
                                    metricValueDatas,
                                    'energyCost.current'
                                  )
                                )
                              "
                            >
                              <currency
                                :value="
                                  $getProperty(
                                    metricValueDatas,
                                    'energyCost.current'
                                  )
                                "
                              ></currency>
                            </div>
                            <div class="flex-middle">
                              <div class="fc-dot fc-dot-yellow"></div>
                              <div class="fc-black-12 pL10 bold">
                                Current
                              </div>
                            </div>
                          </div>

                          <div class="flex-middle mL20">
                            <div
                              class="flex-middle flex-direction-column align-start"
                              v-if="
                                $getProperty(
                                  metricValueDatas,
                                  'energyCost.baseline'
                                )
                              "
                            >
                              <div
                                class="fc-black-com f24 text-center bold pL20"
                                :class="
                                  fontsize(
                                    $getProperty(
                                      metricValueDatas,
                                      'energyCost.baseline'
                                    )
                                  )
                                "
                              >
                                <currency
                                  :value="
                                    $getProperty(
                                      metricValueDatas,
                                      'energyCost.baseline'
                                    )
                                  "
                                ></currency>
                              </div>
                              <div class="flex-middle">
                                <div class="fc-dot-dark-blue fc-dot"></div>
                                <div class="fc-black-12 pL10 bold">
                                  Baseline
                                </div>
                              </div>
                            </div>
                          </div>

                          <div class="flex-middle mL20">
                            <div
                              class="flex-middle flex-direction-column align-start"
                              v-if="
                                $getProperty(
                                  metricValueDatas,
                                  'energyCost.median'
                                )
                              "
                            >
                              <div
                                class="fc-black-com f24 text-center bold pL20"
                                :class="
                                  fontsize(
                                    $getProperty(
                                      metricValueDatas,
                                      'energyCost.median'
                                    )
                                  )
                                "
                              >
                                <currency
                                  :value="
                                    $getProperty(
                                      metricValueDatas,
                                      'energyCost.median'
                                    )
                                  "
                                ></currency>
                              </div>
                              <div class="flex-middle">
                                <div class="fc-dot-red fc-dot"></div>
                                <div class="fc-black-12 pL10 bold">
                                  National Median
                                </div>
                              </div>
                            </div>
                          </div>
                          <div class="flex-middle mL20">
                            <div
                              class="flex-middle flex-direction-column align-start"
                              v-if="
                                $getProperty(
                                  metricValueDatas,
                                  'energyCost.target'
                                )
                              "
                            >
                              <div
                                class="fc-black-com f24 text-center bold"
                                :class="
                                  fontsize(
                                    $getProperty(
                                      metricValueDatas,
                                      'energyCost.target'
                                    )
                                  )
                                "
                              >
                                <currency
                                  :value="
                                    $getProperty(
                                      metricValueDatas,
                                      'energyCost.target'
                                    )
                                  "
                                ></currency>
                              </div>
                              <div class="flex-middle">
                                <div class="fc-dot-green fc-dot"></div>
                                <div class="fc-black-12 pL10 bold">
                                  Target
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </el-col>
                </el-row>
                <!-- <div class="d-flex pT20 pL20 pB10 pR20 mT10">
                  <div class="fc-border-message-yellow flex-middle">
                    <inline-svg
                      src="svgs/alert"
                      iconClass="icon text-left icon-sm fill-yellow vertical-text-top"
                    ></inline-svg>
                    <div class="lable-txt-black pL10">
                      Energy Spend could be
                      <b>lower by $35,000 to $45,000</b> compared to typical
                      buildings of the same size
                    </div>
                  </div>
                </div> -->
                <el-row
                  class="pL20 pR20 pT10 mT40"
                  :gutter="20"
                  style="height:200px"
                >
                  <el-col :span="12" class="height100">
                    <div
                      v-if="loading"
                      class="flex-middle align-start flex-direction-column  white-bg-block fc-energy-bar-chart height220 p20"
                    >
                      <div
                        class="fc-animated-background pT10 pB10 width30 text-left mT10"
                      ></div>
                      <div
                        class="fc-animated-background pT10 pB10 width100 height30 mT20"
                      ></div>
                      <div
                        class="fc-animated-background pT5 pB5 mT20 height20 width60"
                      ></div>
                    </div>
                    <div
                      v-if="
                        $validation.isEmpty(energyConsumtionWidgetData) &&
                          !loading
                      "
                      class="flex-middle justify-content-center flex-direction-column  white-bg-block fc-energy-bar-chart height220"
                    >
                      <inline-svg
                        src="svgs/emptystate/reportlist"
                        iconClass="icon text-center icon-xxxlg"
                      ></inline-svg>
                      <div class="nowo-label f15 pT10">
                        No Energy Consumption
                      </div>
                    </div>
                    <div
                      class="height100"
                      v-if="
                        !loading &&
                          !$validation.isEmpty(energyConsumtionWidgetData)
                      "
                    >
                      <div class="p20 white-bg-block fc-energy-bar-chart pB40">
                        <div class="fc-black-15 p10 bold">
                          Energy Consumption
                        </div>
                        <f-progress-bar-chart
                          v-if="activeName === 'first'"
                          v-show="energyConsumtionWidgetData"
                          :fixedChartHeight="null"
                          :isWidget="true"
                          :data="
                            energyConsumtionWidgetData !== {}
                              ? energyConsumtionWidgetData
                              : null
                          "
                          :options="widgetOptions"
                          :colors="defaultcolors.energyConsumption"
                          :unit="'kWh'"
                          class="height100"
                        >
                        </f-progress-bar-chart>
                        <div
                          class="fc-energy-dot flex-middle width100 pT20 justify-content-center"
                        >
                          <div
                            class="flex-middle flex-direction-column align-start"
                            v-if="
                              energyConsumtionWidgetData &&
                                energyConsumtionWidgetData.current &&
                                energyConsumtionWidgetData.current.length
                            "
                          >
                            <div
                              class="fc-black-com f24 text-center bold pL20"
                              :class="
                                fontsize(
                                  energyConsumtionWidgetData &&
                                    energyConsumtionWidgetData.current &&
                                    energyConsumtionWidgetData.current.length &&
                                    energyConsumtionWidgetData.current[0]
                                    ? parseInt(
                                        energyConsumtionWidgetData.current[0]
                                      ) + ' kWh'
                                    : ''
                                )
                              "
                            >
                              {{
                                numberWithCommas(
                                  energyConsumtionWidgetData &&
                                    energyConsumtionWidgetData.current &&
                                    energyConsumtionWidgetData.current.length
                                    ? parseInt(
                                        energyConsumtionWidgetData.current[0]
                                      ) + ' kWh'
                                    : ''
                                )
                              }}
                            </div>
                            <div class="flex-middle">
                              <div class="fc-dot fc-dot-yellow2"></div>
                              <div class="fc-black-12 pL10 bold">
                                Current
                              </div>
                            </div>
                          </div>

                          <div class="flex-middle mL20">
                            <div
                              class="flex-middle flex-direction-column align-start"
                              v-if="
                                energyConsumtionWidgetData &&
                                  energyConsumtionWidgetData.baseline &&
                                  energyConsumtionWidgetData.baseline.length
                              "
                            >
                              <div
                                class="fc-black-com f24 text-center bold pL20"
                                :class="
                                  fontsize(
                                    energyConsumtionWidgetData &&
                                      energyConsumtionWidgetData.baseline &&
                                      energyConsumtionWidgetData.baseline
                                        .length &&
                                      energyConsumtionWidgetData.baseline[0]
                                      ? parseInt(
                                          energyConsumtionWidgetData.baseline[0]
                                        ) + ' kWh'
                                      : ''
                                  )
                                "
                              >
                                {{
                                  numberWithCommas(
                                    energyConsumtionWidgetData &&
                                      energyConsumtionWidgetData.baseline &&
                                      energyConsumtionWidgetData.baseline.length
                                      ? parseInt(
                                          energyConsumtionWidgetData.baseline[0]
                                        ) + ' kWh'
                                      : ''
                                  )
                                }}
                              </div>
                              <div class="flex-middle">
                                <div class="fc-dot-dark-blue fc-dot"></div>
                                <div class="fc-black-12 pL10 bold">
                                  Baseline
                                </div>
                              </div>
                            </div>
                          </div>

                          <div class="flex-middle mL20">
                            <div
                              class="flex-middle flex-direction-column align-start"
                              v-if="
                                energyConsumtionWidgetData &&
                                  energyConsumtionWidgetData.median &&
                                  energyConsumtionWidgetData.median.length
                              "
                            >
                              <div
                                class="fc-black-com f24 text-center bold pL20"
                                :class="
                                  fontsize(
                                    energyConsumtionWidgetData &&
                                      energyConsumtionWidgetData.median &&
                                      energyConsumtionWidgetData.median
                                        .length &&
                                      energyConsumtionWidgetData.median[0]
                                      ? parseInt(
                                          energyConsumtionWidgetData.median[0]
                                        ) + ' kWh'
                                      : ''
                                  )
                                "
                              >
                                {{
                                  numberWithCommas(
                                    energyConsumtionWidgetData &&
                                      energyConsumtionWidgetData.median &&
                                      energyConsumtionWidgetData.median.length
                                      ? parseInt(
                                          energyConsumtionWidgetData.median[0]
                                        ) + ' kWh'
                                      : ''
                                  )
                                }}
                              </div>
                              <div class="flex-middle">
                                <div class="fc-dot-red fc-dot"></div>
                                <div class="fc-black-12 pL10 bold">
                                  National Median
                                </div>
                              </div>
                            </div>
                          </div>
                          <div class="flex-middle mL20">
                            <div
                              class="flex-middle flex-direction-column align-start"
                              v-if="
                                energyConsumtionWidgetData &&
                                  energyConsumtionWidgetData.target &&
                                  energyConsumtionWidgetData.target.length
                              "
                            >
                              <div
                                class="fc-black-com f24 text-center bold pL20"
                                :class="
                                  energyConsumtionWidgetData &&
                                  energyConsumtionWidgetData.target &&
                                  energyConsumtionWidgetData.target.length &&
                                  energyConsumtionWidgetData.target[0]
                                    ? parseInt(
                                        energyConsumtionWidgetData.target[0]
                                      ) + ' kWh'
                                    : ''
                                "
                              >
                                {{
                                  numberWithCommas(
                                    energyConsumtionWidgetData &&
                                      energyConsumtionWidgetData.target &&
                                      energyConsumtionWidgetData.target.length
                                      ? parseInt(
                                          energyConsumtionWidgetData.target[0]
                                        ) + ' kWh'
                                      : ''
                                  )
                                }}
                              </div>
                              <div class="flex-middle">
                                <div class="fc-dot-green fc-dot"></div>
                                <div class="fc-black-12 pL10 bold">
                                  Target
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </el-col>
                  <el-col :span="12" class="height100">
                    <div
                      v-if="loading"
                      class="flex-middle align-start flex-direction-column  white-bg-block fc-energy-bar-chart height220 p20"
                    >
                      <div
                        class="fc-animated-background pT10 pB10 width30 text-left mT10"
                      ></div>
                      <div
                        class="fc-animated-background pT10 pB10 width100 height30 mT20"
                      ></div>
                      <div
                        class="fc-animated-background pT5 pB5 mT20 height20 width60"
                      ></div>
                    </div>
                    <div
                      v-if="
                        $validation.isEmpty(
                          metricValueDatas && widgets.totalGHGEmissions
                        ) && !loading
                      "
                      class="flex-middle justify-content-center flex-direction-column  white-bg-block fc-energy-bar-chart height220"
                    >
                      <inline-svg
                        src="svgs/emptystate/reportlist"
                        iconClass="icon text-center icon-xxxlg"
                      ></inline-svg>
                      <div class="nowo-label f15 pT10">
                        No Green House Gas emissions
                      </div>
                    </div>
                    <div
                      class="height100"
                      v-if="
                        !loading &&
                          !$validation.isEmpty(
                            metricValueDatas && widgets.totalGHGEmissions
                          )
                      "
                    >
                      <div class="p20 white-bg-block fc-energy-bar-chart pB40">
                        <div class="fc-black-15 p10 bold">
                          Green House Gas Emissions
                        </div>
                        <f-progress-bar-chart
                          v-if="activeName === 'first'"
                          v-show="widgets.totalGHGEmissions"
                          :fixedChartHeight="null"
                          :isWidget="true"
                          :data="
                            widgets.totalGHGEmissions
                              ? widgets.totalGHGEmissions.data
                              : null
                          "
                          :options="widgetOptions"
                          :colors="defaultcolors.totalGHGEmissions"
                          :unit="tabunitmap.totalGHGEmissions"
                          class="height100"
                        >
                        </f-progress-bar-chart>
                        <div
                          class="fc-energy-dot flex-middle width100 pT20 justify-content-center"
                        >
                          <div
                            class="flex-middle flex-direction-column align-start"
                            v-if="
                              metricValueDatas &&
                                metricValueDatas.totalGHGEmissions &&
                                metricValueDatas.totalGHGEmissions.current
                            "
                          >
                            <div
                              class="fc-black-com f24 text-center bold pL20"
                              :class="
                                metricValueDatas &&
                                metricValueDatas.totalGHGEmissions &&
                                metricValueDatas.totalGHGEmissions.current
                                  ? parseInt(
                                      metricValueDatas.totalGHGEmissions.current
                                    )
                                  : ''
                              "
                            >
                              {{
                                metricValueDatas &&
                                metricValueDatas.totalGHGEmissions &&
                                metricValueDatas.totalGHGEmissions.current
                                  ? parseInt(
                                      metricValueDatas.totalGHGEmissions.current
                                    )
                                  : ''
                              }}
                            </div>
                            <div class="flex-middle">
                              <div class="fc-dot fc-dot-green2"></div>
                              <div class="fc-black-12 pL10 bold">
                                Current
                              </div>
                            </div>
                          </div>

                          <div class="flex-middle mL20">
                            <div
                              class="flex-middle flex-direction-column align-start"
                              v-if="
                                metricValueDatas &&
                                  metricValueDatas.totalGHGEmissions &&
                                  metricValueDatas.totalGHGEmissions.baseline
                              "
                            >
                              <div
                                class="fc-black-com f24 text-center bold pL20"
                                :class="
                                  fontsize(
                                    metricValueDatas &&
                                      metricValueDatas.totalGHGEmissions &&
                                      metricValueDatas.totalGHGEmissions
                                        .baseline
                                      ? parseInt(
                                          metricValueDatas.totalGHGEmissions
                                            .baseline
                                        )
                                      : ''
                                  )
                                "
                              >
                                {{
                                  numberWithCommas(
                                    metricValueDatas &&
                                      metricValueDatas.totalGHGEmissions &&
                                      metricValueDatas.totalGHGEmissions
                                        .baseline
                                      ? parseInt(
                                          metricValueDatas.totalGHGEmissions
                                            .baseline
                                        )
                                      : ''
                                  )
                                }}
                              </div>
                              <div class="flex-middle">
                                <div class="fc-dot-dark-blue fc-dot"></div>
                                <div class="fc-black-12 pL10 bold">
                                  Baseline
                                </div>
                              </div>
                            </div>
                          </div>

                          <div class="flex-middle mL20">
                            <div
                              class="flex-middle flex-direction-column align-start"
                              v-if="
                                metricValueDatas &&
                                  metricValueDatas.totalGHGEmissions &&
                                  metricValueDatas.totalGHGEmissions.median
                              "
                            >
                              <div
                                class="fc-black-com f24 text-center bold pL20"
                                :class="
                                  fontsize(
                                    metricValueDatas &&
                                      metricValueDatas.totalGHGEmissions &&
                                      metricValueDatas.totalGHGEmissions.median
                                      ? parseInt(
                                          metricValueDatas.totalGHGEmissions
                                            .median
                                        )
                                      : ''
                                  )
                                "
                              >
                                {{
                                  numberWithCommas(
                                    metricValueDatas &&
                                      metricValueDatas.totalGHGEmissions &&
                                      metricValueDatas.totalGHGEmissions.median
                                      ? parseInt(
                                          metricValueDatas.totalGHGEmissions
                                            .median
                                        )
                                      : ''
                                  )
                                }}
                              </div>
                              <div class="flex-middle">
                                <div class="fc-dot-red fc-dot"></div>
                                <div class="fc-black-12 pL10 bold">
                                  National Median
                                </div>
                              </div>
                            </div>
                          </div>
                          <div class="flex-middle mL20">
                            <div
                              class="flex-middle flex-direction-column align-start"
                              v-if="
                                metricValueDatas &&
                                  metricValueDatas.totalGHGEmissions &&
                                  metricValueDatas.totalGHGEmissions.target
                              "
                            >
                              <div
                                class="fc-black-com f24 text-center bold pL20"
                                :class="
                                  fontsize(
                                    metricValueDatas &&
                                      metricValueDatas.totalGHGEmissions &&
                                      metricValueDatas.totalGHGEmissions.target
                                      ? parseInt(
                                          metricValueDatas.totalGHGEmissions
                                            .target
                                        )
                                      : ''
                                  )
                                "
                              >
                                {{
                                  numberWithCommas(
                                    metricValueDatas &&
                                      metricValueDatas.totalGHGEmissions &&
                                      metricValueDatas.totalGHGEmissions.target
                                      ? parseInt(
                                          metricValueDatas.totalGHGEmissions
                                            .target
                                        )
                                      : ''
                                  )
                                }}
                              </div>
                              <div class="flex-middle">
                                <div class="fc-dot-green fc-dot"></div>
                                <div class="fc-black-12 pL10 bold">
                                  Target
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </el-col>
                </el-row>

                <el-row
                  class="pL20 pR20 pT10 mT40"
                  :gutter="20"
                  style="height:480px;"
                >
                  <el-col :span="24" class="height100">
                    <div class="height100">
                      <div class="white-bg-block height100">
                        <div class="width100 widget-header-2 row">
                          <el-select
                            class="fc-input-full-border3"
                            v-model="tabNo"
                            @change="switchTab(tabNo)"
                          >
                            <el-option
                              v-for="(tab, indx) of tabLists"
                              :key="indx"
                              :label="tab.displayName"
                              :value="indx"
                            >
                              {{ tab.displayName }}
                              <span v-if="tab.field.unit" class="pL5"
                                >({{
                                  tab.field.unit ? tab.field.unit : ''
                                }})</span
                              >
                            </el-option>
                          </el-select>
                          <el-select
                            class="fc-input-full-border3 pL10"
                            v-model="SummaryPeriod"
                          >
                            <el-option
                              v-for="(period, indx) of timePeriods"
                              :key="indx"
                              :label="period.name"
                              :value="period.value"
                            ></el-option>
                          </el-select>
                          <div
                            style="float:right;"
                            v-if="tabActive && summaryData.id"
                          >
                            <portal-target
                              :name="tabActive + summaryData.id + 'summary'"
                              slim
                            ></portal-target>
                          </div>
                        </div>
                        <building-summary-widget
                          v-if="
                            tabActive &&
                              summaryData.id &&
                              activeName === 'first'
                          "
                          :parentId="summaryData.id"
                          :fieldId="tabActive"
                          :period="SummaryPeriod"
                          :chartTypeTarget="
                            tabActive + summaryData.id + 'summary'
                          "
                          :baseline="
                            metricValueDatas &&
                            metricValueDatas[tabLabel] &&
                            metricValueDatas[tabLabel].baseline
                              ? metricValueDatas[tabLabel].baseline
                              : null
                          "
                          :median="
                            metricValueDatas &&
                            metricValueDatas[tabLabel] &&
                            metricValueDatas[tabLabel].median
                              ? metricValueDatas[tabLabel].median
                              : null
                          "
                          :target="
                            metricValueDatas &&
                            metricValueDatas[tabLabel] &&
                            metricValueDatas[tabLabel].target
                              ? metricValueDatas[tabLabel].target
                              : null
                          "
                          :unit="tabUnit"
                        ></building-summary-widget>
                        <div
                          class="fc-energy-dot flex-middle width100 pB20 pL20"
                          style="bottom: 0;position: absolute;"
                        >
                          <div class="flex-middle">
                            <div class="fc-dot fc-dot-blue"></div>
                            <div class="fc-black-12 pL10 bold">
                              Current
                            </div>
                          </div>
                          <div class="flex-middle mL20">
                            <div class="fc-dot-dark-blue fc-dot"></div>
                            <div class="fc-black-12 pL10 bold">
                              Baseline
                            </div>
                          </div>
                          <div class="flex-middle mL20">
                            <div class="fc-dot-red fc-dot"></div>
                            <div class="fc-black-12 pL10 bold">
                              National Median
                            </div>
                          </div>
                          <div class="flex-middle mL20">
                            <div class="fc-dot-green fc-dot"></div>
                            <div class="fc-black-12 pL10 bold">
                              Target
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </el-tab-pane>
            <el-tab-pane label="ENERGY" name="second">
              <div class="fc-energy-height-scroll">
                <div class="p20">
                  <div class="fc-blue-label">METER INFORMATION</div>
                  <div
                    v-if="loading"
                    class="flex-middle justify-content-center flex-direction-column  white-bg-block fc-energy-bar-chart height200"
                  >
                    <spinner :show="loading" size="80"></spinner>
                  </div>
                  <div
                    v-if="$validation.isEmpty(energyMeterData) && !loading"
                    class="flex-middle justify-content-center flex-direction-column  white-bg-block fc-energy-bar-chart height200"
                  >
                    <inline-svg
                      src="svgs/emptystate/reportlist"
                      iconClass="icon text-center icon-xxxlg"
                    ></inline-svg>
                    <div class="nowo-label">
                      No Meter here
                    </div>
                  </div>
                  <div
                    class="white-bg-block mT20"
                    v-if="!loading && !$validation.isEmpty(energyMeterData)"
                  >
                    <el-table
                      :data="energyMeterData"
                      style="width: 100%"
                      :cell-style="{ padding: '10px 20px' }"
                      :fit="true"
                      class="fc-table-th-minus"
                    >
                      <el-table-column prop="id" label="id" width="120">
                        <template v-slot="energyMeter">
                          <div
                            class="fc-id"
                            @click="
                              sendToEnergyMeter(energyMeter.row.meterContext.id)
                            "
                          >
                            # {{ energyMeter.row.meterContext.id }}
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column prop="name" label="name">
                        <template v-slot="energyMeter">
                          <div class="pointer">
                            <div
                              v-if="
                                $getProperty(
                                  energyMeter,
                                  'row.meterContext.name'
                                )
                              "
                            >
                              {{
                                $getProperty(
                                  energyMeter,
                                  'row.meterContext.name'
                                )
                              }}
                            </div>
                            <div v-else>
                              ---
                            </div>
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column prop="energy" label="ENERGY TYPE">
                        <template v-slot="energyMeter">
                          <div
                            v-if="
                              $getProperty(energyMeter, 'row.typeEnum.name')
                            "
                          >
                            {{ $getProperty(energyMeter, 'row.typeEnum.name') }}
                          </div>
                          <div v-else>
                            ---
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column
                        prop="bilDate"
                        label="MOST RECENT BILL DATE"
                      >
                        <template v-slot="energyMeter">
                          <div
                            v-if="
                              $getProperty(
                                energyMeter,
                                'row.data.moseRecentBillData'
                              )
                            "
                          >
                            {{
                              energyMeter.row.data.moseRecentBillData
                                | formatDate(true)
                            }}
                          </div>
                          <div v-else>
                            ---
                          </div>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                  <el-row class="pT20" :gutter="20" style="height:490px;">
                    <el-col :span="24" class="height100">
                      <div class="height100">
                        <div class="white-bg-block height100">
                          <div v-if="energyMeterData.length">
                            <div class="width100 widget-header-2 row">
                              <el-select
                                class="fc-input-full-border3"
                                v-model="activeMeter"
                              >
                                <el-option
                                  v-for="(meter, indx) of energyMeterData"
                                  :key="indx"
                                  :label="meter.meterContext.name"
                                  :value="meter.id"
                                ></el-option>
                              </el-select>
                              <el-select
                                class="fc-input-full-border3 pL10"
                                v-model="EnergyPeriod"
                              >
                                <el-option
                                  v-for="(period, indx) of timePeriods"
                                  :key="indx"
                                  :label="period.name"
                                  :value="period.value"
                                ></el-option>
                              </el-select>
                              <portal-target
                                v-if="
                                  activeMeter &&
                                    energyconsumptionFieldId &&
                                    activeName === 'second'
                                "
                                :name="
                                  'widget-chart-type-picker' +
                                    activeMeter +
                                    energyconsumptionFieldId +
                                    'energy'
                                "
                              ></portal-target>
                            </div>
                            <el-tabs
                              v-model="activeChart"
                              class="fc-energy-chart-tab"
                              ><el-tab-pane
                                label="Energy Consumption"
                                name="Energy Consumption"
                              >
                                <building-energy-widget
                                  v-if="
                                    activeMeter &&
                                      energyconsumptionFieldId &&
                                      activeName === 'second' &&
                                      activeChart === 'Energy Consumption'
                                  "
                                  :parentId="activeMeter"
                                  :fieldId="energyconsumptionFieldId"
                                  :period="EnergyPeriod"
                                  :chartTypeTarget="
                                    'widget-chart-type-picker' +
                                      activeMeter +
                                      energyconsumptionFieldId +
                                      'energy'
                                  "
                                ></building-energy-widget></el-tab-pane
                              ><el-tab-pane
                                label="Energy Cost"
                                name="Energy Cost"
                              >
                                <building-energy-widget
                                  v-if="
                                    tabActive &&
                                      energycostFieldId &&
                                      activeName === 'second' &&
                                      activeChart === 'Energy Cost'
                                  "
                                  :parentId="activeMeter"
                                  :fieldId="energycostFieldId"
                                  :period="EnergyPeriod"
                                  :chartTypeTarget="
                                    'widget-chart-type-picker' +
                                      activeMeter +
                                      energyconsumptionFieldId +
                                      'energy'
                                  "
                                ></building-energy-widget></el-tab-pane
                            ></el-tabs>
                          </div>
                        </div>
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="METRICS" name="third">
              <div class="fc-energy-height-scroll">
                <div class="p20" v-if="activeName === 'third'">
                  <div class="fc-blue-label">METER INFORMATION</div>
                  <div
                    v-if="loading"
                    class="flex-middle justify-content-center flex-direction-column  white-bg-block fc-energy-bar-chart height200 mT20"
                  >
                    <spinner :show="loading" size="80"></spinner>
                  </div>
                  <div
                    v-if="$validation.isEmpty(metricValueDatas) && !loading"
                    class="flex-middle justify-content-center flex-direction-column  white-bg-block fc-energy-bar-chart height200 mT20"
                  >
                    <inline-svg
                      src="svgs/emptystate/reportlist"
                      iconClass="icon text-center icon-xxxlg"
                    ></inline-svg>
                    <div class="nowo-label">
                      No Meterics available here
                    </div>
                  </div>
                  <div
                    class="white-bg-block mT20"
                    v-if="!loading && !$validation.isEmpty(metricValueDatas)"
                  >
                    <table class="setting-list-view-table width100">
                      <thead>
                        <tr>
                          <th
                            class="setting-table-th setting-th-text uppercase"
                          >
                            Metric
                          </th>
                          <th
                            class="setting-table-th setting-th-text uppercase"
                          >
                            current
                          </th>
                          <th
                            class="setting-table-th setting-th-text uppercase"
                          >
                            baseline
                          </th>
                          <th
                            class="setting-table-th setting-th-text uppercase"
                          >
                            median
                          </th>
                          <th
                            class="setting-table-th setting-th-text uppercase"
                          >
                            target
                          </th>
                        </tr>
                      </thead>

                      <tbody>
                        <tr
                          v-for="(metricData, key) in metricValueDatas"
                          :key="metricData"
                        >
                          <td>
                            {{ tableMetaData[key].displayName }}
                          </td>
                          <td>
                            <div
                              v-if="tableMetaData[key].name === 'energyCost'"
                            >
                              {{
                                numberWithCommas(
                                  Math.round(metricData.current)
                                    ? tableMetaData[key].unit +
                                        Math.round(metricData.current)
                                    : '---'
                                )
                              }}
                            </div>
                            <div v-else>
                              {{
                                numberWithCommas(
                                  Math.round(metricData.current)
                                    ? Math.round(metricData.current) +
                                        ' ' +
                                        tableMetaData[key].unit
                                    : '---'
                                )
                              }}
                            </div>
                          </td>
                          <td>
                            <div
                              v-if="tableMetaData[key].name === 'energyCost'"
                            >
                              {{
                                numberWithCommas(
                                  Math.round(metricData.baseline)
                                    ? tableMetaData[key].unit +
                                        Math.round(metricData.baseline)
                                    : '---'
                                )
                              }}
                            </div>
                            <div v-else>
                              {{
                                numberWithCommas(
                                  Math.round(metricData.baseline)
                                    ? Math.round(metricData.baseline) +
                                        ' ' +
                                        tableMetaData[key].unit
                                    : '---'
                                )
                              }}
                            </div>
                          </td>
                          <td>
                            <div
                              v-if="tableMetaData[key].name === 'energyCost'"
                            >
                              {{
                                numberWithCommas(
                                  Math.round(metricData.median)
                                    ? tableMetaData[key].unit +
                                        Math.round(metricData.median)
                                    : '---'
                                )
                              }}
                            </div>
                            <div v-else>
                              {{
                                numberWithCommas(
                                  Math.round(metricData.median)
                                    ? Math.round(metricData.median) +
                                        ' ' +
                                        tableMetaData[key].unit
                                    : '---'
                                )
                              }}
                            </div>
                          </td>
                          <td>
                            <div
                              v-if="tableMetaData[key].name === 'energyCost'"
                            >
                              {{
                                numberWithCommas(
                                  Math.round(metricData.target)
                                    ? tableMetaData[key].unit +
                                        '' +
                                        Math.round(metricData.target)
                                    : '---'
                                )
                              }}
                            </div>
                            <div v-else>
                              {{
                                numberWithCommas(
                                  Math.round(metricData.target)
                                    ? Math.round(metricData.target) +
                                        ' ' +
                                        tableMetaData[key].unit
                                    : '---'
                                )
                              }}
                            </div>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { isArray, isEmpty } from '@facilio/utils/validation'
import FProgressBarChart from 'newcharts/components/FProgressBarChart'
import BuildingSummaryWidget from 'pages/energyAnalytics/energy/BuildingSummaryWidget'
import BuildingEnergyWidget from 'pages/energyAnalytics/energy/BuildingEnergyWidget'
import chartModel from 'newcharts/model/chart-model'
import deepmerge from 'util/deepmerge'
import colorHelper from 'newcharts/helpers/color-helper'
import { API } from '@facilio/api'
import moment from 'moment-timezone'
import { isWebTabsEnabled, findRouteForTab, tabTypes } from '@facilio/router'
import { mapState } from 'vuex'

export default {
  data() {
    return {
      activeName: 'first',
      activeChart: 'Energy Consumption',
      summaryData: [],
      energyMeterData: [],
      propertyContextDatas: [],
      propertyContextData: null,
      loading: true,
      energyMeter: null,
      metricDatas: [],
      metricData: null,
      metricValueDatas: [],
      widgets: {},
      widgetOptions: {},
      listData: null,
      energyConsumtionWidgetData: null,
      defaultcolors: {
        score: {
          current: '#a6e1ff',
          baseline: '#1f8bdd',
          median: '#f98484',
          target: '#55d685',
        },
        energyCost: {
          current: '#ffdd91',
          baseline: '#1f8bdd',
          median: '#f98484',
          target: '#55d685',
        },
        energyConsumption: {
          current: '#ffd2bf',
          baseline: '#1f8bdd',
          median: '#f98484',
          target: '#55d685',
        },
        totalGHGEmissions: {
          current: '#c4edd7',
          baseline: '#1f8bdd',
          median: '#f98484',
          target: '#55d685',
        },
      },
      defaultLimit: 0,
      limitBy: 0,
      sideListDatas: [],
      tabActive: null,
      tabunitmap: {},
      tabNo: null,
      tabLabel: null,
      tabUnit: null,
      tabLists: [],
      activeMeter: null,
      energycostFieldId: null,
      energyconsumptionFieldId: null,
      SummaryPeriod: 10,
      EnergyPeriod: 10,
      metaMeterName: {},
      tableMetaData: {},
      timePeriods: [
        {
          name: 'Monthly',
          value: 10,
        },
        {
          name: 'Yearly',
          value: 8,
        },
      ],
      isAllVisible: false,
      needsShowMore: false,
    }
  },
  components: {
    FProgressBarChart,
    BuildingSummaryWidget,
    BuildingEnergyWidget,
  },
  computed: {
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    openSummary() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    showMoreLinkText() {
      return this.isAllVisible ? 'View Less' : 'View More'
    },
  },
  watch: {
    openSummary: function() {
      this.summaryLoadData()
      this.metricsLoadData()
    },
  },
  mounted() {
    // this.summaryLoadData()
    this.metricsLoadData()
    this.sideListData()
    this.prepareWidgetOptions()
    // this.tabListData()
    this.getFields()
    this.loading = true
    this.tabListData()
  },
  methods: {
    prepareTableData() {
      this.tableMetaData = {}
      let self = this
      Object.keys(self.metricValueDatas).forEach(et => {
        let metric = self.tabLists.find(rt => rt.name === et)
        let tableMetric = {
          displayName: metric.displayName,
          unit: '',
          name: metric.name,
        }
        if (metric && metric.field && metric.field.unit) {
          tableMetric.unit = metric.field.unit
        }
        self.$set(self.tableMetaData, et, tableMetric)
      })
      this.loading = false
    },
    toggle(defaultLimit, meters_length) {
      this.limitBy =
        this.limitBy === defaultLimit ? meters_length : defaultLimit
    },
    handleClick() {},
    switchTab(indx) {
      this.tabActive = this.tabLists[indx].field.fieldId
      this.tabLabel = this.tabLists[indx].name
      this.tabUnit = this.tabLists[indx].field.unit
    },
    numberWithCommas(value) {
      return value.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ',')
    },
    tabListData() {
      this.$http
        .get('/v2/energystar/getMetricsList')
        .then(response => {
          this.tabLists = response.data.result.metrics
          if (this.tabLists.length) {
            this.tabActive = this.tabLists[0].field.fieldId
            this.tabLabel = this.tabLists[0].name
            this.tabUnit = this.tabLists[0].field.unit
            this.tabNo = 0
            this.tabunitmap = {}
            for (let tab of this.tabLists) {
              this.tabunitmap[tab.name] = tab.field.unit
            }
          }
          this.summaryLoadData()
          // return this.tabLists
        })
        .catch(() => {
          this.loading = false
        })
    },
    async sideListData() {
      let { error, data } = await API.get(`/v2/energystar/fetchMainSummaryData`)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.sideListDatas = data.energyStarPropertiesContext || []
      }
    },
    summaryLoadData() {
      // this.loading = true
      this.$http
        .get('/v2/energystar/fetchProperty?propertyId=' + this.openSummary)
        .then(response => {
          this.summaryData = response.data.result.energyStarPropertyContext
            ? response.data.result.energyStarPropertyContext
            : []

          this.energyMeterData = response.data.result.energyStarPropertyContext
            .meterContexts
            ? response.data.result.energyStarPropertyContext.meterContexts
            : []
          this.propertyContextDatas = response.data.result
            .energyStarPropertyContext.propertyUseContexts
            ? response.data.result.energyStarPropertyContext.propertyUseContexts
            : []
          if (this.energyMeterData.length) {
            this.activeMeter = this.energyMeterData[0].id
          }

          let parameters = [
            this.energyMeterData[0].meterId,
            this.summaryData.lastMetricsRetrivedDate,
            this.summaryData.baselineMonth,
          ]

          let params = {
            defaultWorkflowId: 55,
            paramList: parameters,
          }

          this.$http
            .post('/v2/workflow/getDefaultWorkflowResult', params)
            .then(response => {
              if (response.data.result.workflow) {
                this.prepareEnergyConsumptionWidgetData(
                  response.data.result.workflow.returnValue
                )
              }
              this.prepareTableData()
            })
        })
        .catch(() => {
          this.loading = false
        })
      return this.summaryData
    },
    metricsLoadData() {
      this.loading = true
      this.widgets = {}
      this.$http
        .get(
          'v2/energystar/fetchPropertyMetricsData?propertyId=' +
            this.openSummary
        )
        .then(response => {
          this.loading = false
          this.metricDatas = response.data.result.energyStarPropertyContext
            ? response.data.result.energyStarPropertyContext
            : []
          this.metricValueDatas = response.data.result.energyStarPropertyContext
            .data.values
            ? response.data.result.energyStarPropertyContext.data.values
            : []
          this.prepareWidgetData()
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    prepareWidgetData() {
      let widgetObj = {}
      if (this.metricValueDatas) {
        for (let [idx, value] of Object.entries(this.metricValueDatas)) {
          let details = {}
          details['name'] = idx
          details['data'] = {}
          if (!isEmpty(value)) {
            Object.keys(value).forEach(key => {
              if (!['max', 'maxValue'].includes(key) && value[key]) {
                details.data[key] = [parseFloat(value[key])]
              } else if (key === 'maxValue' && value[key]) {
                details.data['x'] = this.findNiceDelta(
                  parseFloat(value[key]),
                  5
                )
              }
            })
          }
          if (Object.keys(details).length > 1) {
            widgetObj[idx] = details
          }
        }
      }
      this.widgets = widgetObj
    },
    prepareEnergyConsumptionWidgetData(EnConData) {
      if (EnConData) {
        let details = {}
        Object.keys(EnConData).forEach(key => {
          if (!['max', 'maxValue'].includes(key) && EnConData[key]) {
            details[key] = EnConData[key]
              ? [parseFloat(EnConData[key])]
              : [null]
          } else if (key === 'maxValue' && EnConData[key]) {
            details['x'] = this.findNiceDelta(parseFloat(EnConData[key]), 5)
          }
        })
        this.energyConsumtionWidgetData =
          Object.keys(details).length > 1 ? details : null
      }
    },
    getMeterTypeObj(unit) {
      return this.tabLists.find(field => field.unit === unit) || {}
    },
    prepareWidgetOptions() {
      let defaultOptions = {}
      let customOptions = {
        axis: {
          x: {
            datatype: 'number',
          },
        },
        dataPoints: [],
        regionMultiplier: 0.003,
      }
      let mergedOptions = deepmerge.objectAssignDeep(
        defaultOptions,
        chartModel.options,
        {
          style: chartModel.style,
        },
        customOptions
      )
      this.widgetOptions = mergedOptions
    },
    findNiceDelta(maxVal, count) {
      if (maxVal) {
        let step = Math.ceil(maxVal) / (count - 1)
        let order = Math.pow(10, Math.floor(Math.log10(step)))
        let delta = step / order

        let stepVal = null

        if (!stepVal) {
          stepVal = delta * order
        }

        let stepList = []
        stepList.push(0)
        for (let j = 1; j < count; j++) {
          stepList.push(Math.ceil(j * stepVal))
        }
        return stepList
      } else {
        return [null]
      }
    },
    fontsize(value) {
      if (!value) {
        return 'f24'
      }
      value = value + ''
      if (value.length < 3) {
        return 'f24'
      } else if (value.length < 6) {
        return 'f18'
      } else if (value.length < 8) {
        return 'f16'
      } else if (value.length < 10) {
        return 'f12'
      } else if (value.length < 10) {
        return 'f11'
      } else {
        return 'f10'
      }
    },
    getFields() {
      this.$http
        .get('/module/meta?moduleName=energyStarMeterData')
        .then(response => {
          let energycostField = response.data.meta.fields.find(
            field => field.name === 'cost'
          )
          this.energycostFieldId = energycostField
            ? energycostField.fieldId
            : null
          let energyconsumptionField = response.data.meta.fields.find(
            field => field.name === 'usage'
          )
          this.energyconsumptionFieldId = energyconsumptionField
            ? energyconsumptionField.fieldId
            : null
        })
    },
    formatTime(date) {
      return moment(date)
        .tz(this.$timezone)
        .format('MMM-YYYY')
    },
    findRoute() {
      let { currentTab = {}, $router } = this
      let tabType = tabTypes.CUSTOM
      let config = { type: 'energyStar' }
      let route =
        findRouteForTab(currentTab.id, {
          tabType,
          config,
        }) || {}

      return $router.resolve({ name: route.name }).href
    },
    goToSummary(id) {
      if (isWebTabsEnabled()) {
        let path = this.findRoute()
        this.$router.push({
          path: path + '/energy/summary/' + id,
        })
      } else {
        this.$router.push({
          path: '/app/en/energy/summary/' + id,
        })
      }
    },
    sendToEnergyMeter(id) {
      if (isWebTabsEnabled()) {
        // TODO
      } else {
        this.$router.push({
          path: '/app/en/energymeter/all/' + id + '/overview',
        })
      }
    },
    goBack() {
      if (isWebTabsEnabled()) {
        let path = this.findRoute()
        this.$router.push({
          path: path + '/energy',
        })
      } else {
        this.$router.push('/app/en/energy')
      }
    },
  },
}
</script>

<style lang="scss"></style>
