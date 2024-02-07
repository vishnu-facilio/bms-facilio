<template>
  <div>
    <el-dialog
      title="Push Missing Data"
      :visible.sync="showPushingData"
      width="70%"
      :before-close="closeDialog"
      class="fc-dialog-center-container text-left fc-dialog-center-body-p0"
    >
      <div class="height400 overflow-y-scroll pB50">
        <div v-if="loading" class="flex-middle height300">
          <spinner :show="loading" size="80"></spinner>
        </div>
        <div
          v-if="$validation.isEmpty(pushDatas) && !loading"
          class="height300 fc-empty-white flex-middle justify-content-center flex-direction-column"
        >
          <inline-svg
            src="svgs/emptystate/reportlist"
            iconClass="icon text-center icon-xxxlg"
          ></inline-svg>
          <div class="nowo-label">
            No Pushing meter available
          </div>
        </div>
        <el-collapse
          v-if="!loading && !$validation.isEmpty(pushDatas)"
          class="fc-pushing-data-collapse"
          accordion
        >
          <el-collapse-item
            :name="index"
            v-for="(pushData, index) in pushDatas"
            :key="index.id"
          >
            <template slot="title">
              <el-row class="width100">
                <el-col :span="6">
                  <div
                    class="label-txt-black textoverflow-ellipsis"
                    :title="pushData.data.building.name"
                    v-tippy="{
                      arrow: true,
                      arrowType: 'round',
                      animation: 'fade',
                    }"
                    v-if="$getProperty(pushData, 'data.building.name')"
                  >
                    {{
                      pushData.data.building.name
                        ? pushData.data.building.name
                        : '---'
                    }}
                  </div>
                </el-col>
                <el-col :span="18">
                  <div class="flex-middle pL20">
                    <div class="fc-grey3-text14">Meter Name</div>
                    <div
                      class="label-txt-black pL10 textoverflow-ellipsis"
                      v-for="energyMeterContext in pushData.meterContexts"
                      :key="energyMeterContext"
                      :title="energyMeterContext.meterContext.name"
                      v-tippy="{
                        arrow: true,
                        arrowType: 'round',
                        animation: 'fade',
                      }"
                    >
                      <div
                        class="label-txt-black textoverflow-ellipsis"
                        style="max-width: 250px;"
                        v-if="
                          $getProperty(energyMeterContext, 'meterContext.name')
                        "
                      >
                        {{
                          energyMeterContext.meterContext.name
                            ? energyMeterContext.meterContext.name
                            : '---'
                        }}
                      </div>
                      <div
                        class="label-txt-black missing-data fw4"
                        v-if="
                          $getProperty(energyMeterContext, 'missingRangeString')
                        "
                      >
                        Data Missing for
                        <span class="fc-blue-txt4-12 f14 bold">{{
                          energyMeterContext.missingRangeString
                            ? energyMeterContext.missingRangeString
                            : ''
                        }}</span>
                      </div>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </template>
            <div
              v-if="
                $validation.isEmpty(
                  pushData.meterContexts[0]
                    .energyStarDataMissingRangesThatCanBeFilled
                )
              "
            >
              <div
                class="flex-middle justify-content-center flex-direction-column"
              >
                <inline-svg
                  src="svgs/emptystate/reportlist"
                  iconClass="icon text-center icon-xxxlg "
                ></inline-svg>
                <div class="nowo-label">
                  No Missing Data available
                </div>
              </div>
            </div>

            <div
              v-if="
                !$validation.isEmpty(
                  pushData.meterContexts[0]
                    .energyStarDataMissingRangesThatCanBeFilled
                )
              "
            >
              <el-row
                class="flex-middle flex-wrap"
                v-if="pushData.meterContexts[0].energyStarDataAvailableRanges"
              >
                <el-col :span="6">
                  <div class="fc-grey3-text14">Data in Energy Star</div>
                </el-col>
                <el-col :span="18" class="flex-middle flex-wrap">
                  <div
                    class="label-txt-black pL20 pB10"
                    v-for="(energyMeterContextDate, index) in pushData
                      .meterContexts[0].energyStarDataAvailableRanges"
                    :key="index"
                  >
                    <div v-if="energyMeterContextDate">
                      <el-tag class="fc-tag-bg-blue">
                        <span
                          v-if="
                            $getProperty(energyMeterContextDate, 'startTime')
                          "
                        >
                          {{
                            energyMeterContextDate.startTime | formatDate(true)
                          }}
                        </span>
                        -
                        <span
                          v-if="$getProperty(energyMeterContextDate, 'endTime')"
                        >
                          {{
                            energyMeterContextDate.endTime | formatDate(true)
                          }}
                        </span>
                      </el-tag>
                    </div>
                  </div>
                </el-col>
              </el-row>
              <el-row
                class="pT10 flex-middle flex-wrap"
                v-if="pushData.meterContexts"
              >
                <el-col :span="6">
                  <div class="fc-grey3-text14">Data in Facilio</div>
                </el-col>
                <el-col :span="18" class="flex-middle flex-wrap">
                  <div
                    class="label-txt-black pL20 pB10"
                    v-for="energyMeterContext in pushData.meterContexts"
                    :key="energyMeterContext"
                  >
                    <el-tag class="fc-tag-bg-blue">
                      <span
                        v-if="
                          $getProperty(
                            energyMeterContext,
                            'dataAvailableInFacilio.startTime'
                          )
                        "
                      >
                        {{
                          energyMeterContext.dataAvailableInFacilio.startTime
                            | formatDate(true)
                        }}
                        -
                      </span>
                      <span
                        v-if="
                          $getProperty(
                            energyMeterContext,
                            'dataAvailableInFacilio.endTime'
                          )
                        "
                      >
                        {{
                          energyMeterContext.dataAvailableInFacilio.endTime
                            | formatDate(true)
                        }}
                      </span>
                    </el-tag>
                  </div>
                </el-col>
              </el-row>
              <el-row
                class="pT10 flex-middle flex-wrap"
                v-if="
                  pushData.meterContexts[0]
                    .energyStarDataMissingRangesThatCanBeFilled
                "
              >
                <el-col :span="6">
                  <div class="fc-grey3-text14">Missing Data</div>
                </el-col>
                <el-col :span="18" class="flex-middle flex-wrap">
                  <div
                    class="label-txt-black pL20 pB10"
                    v-for="(energyMeterContextMissing, index) in pushData
                      .meterContexts[0]
                      .energyStarDataMissingRangesThatCanBeFilled"
                    :key="index"
                  >
                    <el-tag class="fc-tag-bg-blue">
                      <span
                        v-if="
                          $getProperty(energyMeterContextMissing, 'startTime')
                        "
                      >
                        {{
                          energyMeterContextMissing.startTime | formatDate(true)
                        }}
                      </span>
                      -
                      <span
                        v-if="
                          $getProperty(energyMeterContextMissing, 'endTime')
                        "
                      >
                        {{
                          energyMeterContextMissing.endTime | formatDate(true)
                        }}
                      </span>
                    </el-tag>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button type="primary" @click="confirmBtn" class="modal-btn-save"
          >Confirm</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
export default {
  props: ['showPushingData'],
  data() {
    return {
      pushDatas: [],
      meterContextData: [],
      energyStarDataAvailableRanges: [],
      energyMeterContext: null,
      energyMeterContextMissing: null,
      energyMeterContextDate: null,
      loading: true,
      pushData: null,
    }
  },
  mounted() {
    this.energyPushingData()
  },
  methods: {
    closeDialog() {
      this.$emit('update:showPushingData', false)
    },
    async energyPushingData() {
      this.loading = true
      let { data, error } = await API.get('/v2/energystar/fetchMissingData')
      if (error) {
        this.loading = false
        let { message } = error
        this.$message.error(message)
      } else {
        this.loading = false
        this.pushDatas = data.energyStarPropertiesContext
          ? data.energyStarPropertiesContext
          : []
        this.meterContextData = data.energyStarPropertiesContext.meterContexts
          ? data.energyStarPropertiesContext.meterContexts
          : []
      }
    },
    confirmBtn() {
      this.loading = true
      let pushMeterData = []
      this.pushDatas.forEach(pushData => {
        if (
          pushData.meterContexts[0]
            .energyStarDataMissingRangesThatCanBeFilled[0]
        )
          pushMeterData.push({
            id: pushData.id,
            dateRanges: [
              {
                startTime:
                  pushData.meterContexts[0]
                    .energyStarDataMissingRangesThatCanBeFilled[0].startTime,
                endTime:
                  pushData.meterContexts[0]
                    .energyStarDataMissingRangesThatCanBeFilled[0].endTime,
              },
            ],
          })
      })
      API.post('/v2/energystar/bulkPushHistoricalData', {
        pushMeterData: pushMeterData,
      }).then(({ error }) => {
        if (error) {
          this.$message.error(error)
          this.loading = false
          this.showPushingData = false
        } else {
          this.$message.success('Push data updated Successfully')
          this.loading = false
          this.showPushingData = false
        }
      })
    },
  },
}
</script>
