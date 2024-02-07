<template>
  <el-popover
    popper-class="fp-seting-popover p0"
    placement="left"
    width="350"
    trigger="click"
    v-if="!loading && !isPortal"
    v-model="visibility"
  >
    <div class="fp-s-title pL10 p10">{{ i18_text.customization }}</div>
    <el-collapse v-model="activeName" accordion>
      <el-collapse-item :title="i18_text.tab1" name="common" class="p0">
        <div class="p10">
          <el-row>
            <el-col :span="18" class="pB10"
              ><div class="fp-stng-label pL5">
                {{ i18_text.compactview }}
              </div></el-col
            >
            <el-col :span="4"
              ><div class="fp-setting-switch">
                <el-switch v-model="settings.compactView"> </el-switch></div
            ></el-col>
          </el-row>
          <el-row>
            <el-col :span="18" class="pB10"
              ><div class="fp-stng-label pL5">
                {{ i18_text.texthalo }}
              </div></el-col
            >
            <el-col :span="4"
              ><div class="fp-setting-switch">
                <el-switch v-model="settings.textHalo"> </el-switch></div
            ></el-col>
          </el-row>
          <el-row v-if="!settings.compactView">
            <el-col :span="18" class="pB10"
              ><div class="fp-stng-label pL5">
                {{ i18_text.overlaptext }}
              </div></el-col
            >
            <el-col :span="4"
              ><div class="fp-setting-switch">
                <el-switch v-model="settings.allowTextOverlap">
                </el-switch></div
            ></el-col>
          </el-row>
          <el-row v-if="isImageOpacity">
            <el-col :span="10" class="pB10">
              <div class="fp-stng-label pL5">
                {{ i18_text.opacity }}
              </div>
            </el-col>
            <el-col :span="12">
              <el-slider
                v-model="settings.imageOpacity"
                :min="0.1"
                :max="1"
                :step="0.1"
                class="fp-slider-settings"
              ></el-slider>
            </el-col>
          </el-row>
        </div>
        <!-- <div class="" v-if="!settings.compactView">
            <div class="fp-stng-label p10">{{ i18_text.fontSize }}</div>
            <div class="">
               <el-button-group class="fp-s-text-btn">
            <el-button type="primary" @click="setFontSize(8)"
              ><span class="f10">{{ i18_text.fontA }}</span></el-button
            >
            <el-button type="primary" @click="setFontSize(10)"
              ><span class="f12">{{ i18_text.fontA }}</span></el-button
            >
            <el-button type="primary" @click="setFontSize(12)"
              ><span class="f14">{{ i18_text.fontA }}</span></el-button
            >
            <el-button type="primary" @click="setFontSize(14)"
              ><span class="f16">{{ i18_text.fontA }}</span></el-button
            >
          </el-button-group>

              <el-slider
                v-model="settings.fontSize"
                :step="1"
                :min="10"
                :max="20"
                show-stops
              >
              </el-slider>
            </div>
          </div>
        </div> -->
      </el-collapse-item>
      <el-collapse-item :title="i18_text.tab2" name="desk">
        <div class="p10">
          <div class="flex p5 flex-col">
            <div class="fp-stng-label">{{ i18_text.primaryText }}</div>
            <div class="flex">
              <el-select
                v-model="settings.deskPrimaryLabel.labelType"
                filterable
                default-first-option
                placeholder="Select Agent"
                width="250px"
                class="fc-input-full-border-select2 mL0 mT5 pL0 pR5 width60"
              >
                <el-option
                  v-for="(type, index) in labelTypes"
                  :key="index"
                  :label="type.name"
                  :value="type.value"
                  no-data-text="No data available"
                  clearable
                ></el-option>
              </el-select>
              <el-select
                v-model="settings.deskPrimaryLabel.fontSize"
                filterable
                default-first-option
                placeholder="Font Size"
                width="250px"
                class="fc-input-full-border-select2 mL5 mT5 pR5 width20"
              >
                <el-option
                  v-for="size in fontSizeSet"
                  :key="size"
                  :label="size"
                  :value="size"
                  no-data-text="No data available"
                  clearable
                ></el-option>
              </el-select>
              <div class="p5">
                <el-color-picker
                  show-alpha
                  :predefine="getPredefinedColors()"
                  v-model="settings.deskPrimaryLabel.color"
                ></el-color-picker>
              </div>
            </div>
            <div
              class="pT10"
              v-if="settings.deskPrimaryLabel.labelType === 'CUSTOM'"
            >
              <div class="fp-stng-label">
                {{ i18_text.deskPrimaryCustomLabel }}
              </div>

              <el-input
                width="250px"
                v-model="settings.deskPrimaryLabel.customText"
                class="el-input-textbox-full-border width70"
              >
              </el-input>

              <el-popover placement="left" width="300" trigger="click">
                <el-row :gutter="20">
                  <el-col :span="24">
                    <el-input
                      v-model="deskSearchVariables"
                      class="input-box-1 mt-1 el-input-textbox-full-border "
                      size="medium"
                      placeholder="Search variables"
                      suffix-icon="el-icon-search"
                    ></el-input>
                  </el-col>
                </el-row>
                <div class="checkbox-popover">
                  <div
                    v-for="(field, index) in filteredDeskFieldList"
                    :key="index"
                    class="pT10"
                  >
                    <el-checkbox
                      @change="
                        addtext(
                          field,
                          'deskPrimaryLabel',
                          'customText',
                          'desks'
                        )
                      "
                      v-model="field.checked"
                      >{{ field.displayName }}</el-checkbox
                    >
                  </div>
                </div>
                <el-button
                  icon="el-icon-s-operation"
                  slot="reference"
                  class="mL20"
                ></el-button>
              </el-popover>
            </div>
          </div>

          <div>
            <div class="flex p5 flex-col">
              <div class="fp-stng-label">{{ i18_text.secondaryText }}</div>
              <div class="flex">
                <el-select
                  v-model="settings.deskSecondaryLabel.labelType"
                  filterable
                  default-first-option
                  placeholder="Select Agent"
                  width="250px"
                  class="fc-input-full-border-select2 mL0 mT5 pL0 pR5 width60"
                >
                  <el-option
                    v-for="(type, index) in labelTypes"
                    :key="index"
                    :label="type.name"
                    :value="type.value"
                    no-data-text="No data available"
                    clearable
                  ></el-option>
                </el-select>
                <el-select
                  v-model="settings.deskSecondaryLabel.fontSize"
                  filterable
                  default-first-option
                  placeholder="Font Size"
                  width="250px"
                  class="fc-input-full-border-select2 mL5 mT5 pR5 width20"
                >
                  <el-option
                    v-for="size in fontSizeSet"
                    :key="size"
                    :label="size"
                    :value="size"
                    no-data-text="No data available"
                    clearable
                  ></el-option>
                </el-select>
                <div class="p5">
                  <el-color-picker
                    show-alpha
                    :predefine="getPredefinedColors()"
                    v-model="settings.deskSecondaryLabel.color"
                  ></el-color-picker>
                </div>
              </div>
              <div
                class="pT10"
                v-if="settings.deskSecondaryLabel.labelType === 'CUSTOM'"
              >
                <div class="fp-stng-label">
                  {{ i18_text.deskSecondaryCustomLabel }}
                </div>

                <el-input
                  width="250px"
                  v-model="settings.deskSecondaryLabel.customText"
                  class="el-input-textbox-full-border width70"
                >
                </el-input>

                <el-popover placement="left" width="300" trigger="click">
                  <el-row :gutter="20">
                    <el-col :span="24">
                      <el-input
                        v-model="deskSearchVariables"
                        class="input-box-1 mt-1 el-input-textbox-full-border "
                        size="medium"
                        placeholder="Search variables"
                        suffix-icon="el-icon-search"
                      ></el-input>
                    </el-col>
                  </el-row>
                  <div class="checkbox-popover">
                    <div
                      v-for="(field, index) in filteredDeskFieldList"
                      :key="index"
                      class="pT10"
                    >
                      <el-checkbox
                        @change="
                          addtext(
                            field,
                            'deskSecondaryLabel',
                            'customText',
                            'desks'
                          )
                        "
                        v-model="field.checked"
                        >{{ field.displayName }}</el-checkbox
                      >
                    </div>
                  </div>
                  <el-button
                    icon="el-icon-s-operation"
                    slot="reference"
                    class="mL20"
                  ></el-button>
                </el-popover>
              </div>
            </div>
          </div>
        </div>
      </el-collapse-item>
      <el-collapse-item :title="i18_text.tab3" name="space">
        <div class="p10">
          <div class="flex p5 flex-col">
            <div class="fp-stng-label">{{ i18_text.primaryText }}</div>
            <div class="flex">
              <el-select
                v-model="settings.spacePrimaryLabel.labelType"
                filterable
                default-first-option
                placeholder="Select Primary Text"
                width="250px"
                class="fc-input-full-border-select2 mL0 mT5 pL0 pR5 width60"
              >
                <el-option
                  v-for="(type, index) in spaceLabelTypes"
                  :key="index"
                  :label="type.name"
                  :value="type.value"
                  no-data-text="No data available"
                  clearable
                ></el-option>
              </el-select>
              <el-select
                v-model="settings.spacePrimaryLabel.fontSize"
                filterable
                default-first-option
                placeholder="Font Size"
                width="250px"
                class="fc-input-full-border-select2 mL5 mT5 pR5 width20"
              >
                <el-option
                  v-for="size in fontSizeSet"
                  :key="size"
                  :label="size"
                  :value="size"
                  no-data-text="No data available"
                  clearable
                ></el-option>
              </el-select>
              <div class="p5">
                <el-color-picker
                  show-alpha
                  :predefine="getPredefinedColors()"
                  v-model="settings.spacePrimaryLabel.color"
                ></el-color-picker>
              </div>
            </div>
            <div
              class="pT10"
              v-if="settings.spacePrimaryLabel.labelType === 'CUSTOM'"
            >
              <div class="fp-stng-label">
                {{ i18_text.spacePrimaryCustomLabel }}
              </div>

              <el-input
                width="250px"
                v-model="settings.spacePrimaryLabel.customText"
                class="el-input-textbox-full-border width70"
              >
              </el-input>

              <el-popover placement="left" width="300" trigger="click">
                <el-row :gutter="20">
                  <el-col :span="24">
                    <el-input
                      v-model="spaceSearchVariables"
                      class="input-box-1 mt-1 el-input-textbox-full-border "
                      size="medium"
                      placeholder="Search variables"
                      suffix-icon="el-icon-search"
                    ></el-input>
                  </el-col>
                </el-row>
                <div class="checkbox-popover">
                  <div
                    v-for="(field, index) in filteredSpaceFieldList"
                    :key="index"
                    class="pT10"
                  >
                    <el-checkbox
                      @change="
                        addtext(
                          field,
                          'spacePrimaryLabel',
                          'customText',
                          'space'
                        )
                      "
                      v-model="field.checked"
                      >{{ field.displayName }}</el-checkbox
                    >
                  </div>
                </div>
                <el-button
                  icon="el-icon-s-operation"
                  slot="reference"
                  class="mL20"
                ></el-button>
              </el-popover>
            </div>
          </div>
          <div>
            <div class="flex p5 flex-col">
              <div class="fp-stng-label">{{ i18_text.secondaryText }}</div>
              <div class="flex">
                <el-select
                  v-model="settings.spaceSecondaryLabel.labelType"
                  filterable
                  default-first-option
                  placeholder="Select Primary Text"
                  width="250px"
                  class="fc-input-full-border-select2 mL0 mT5 pL0 pR5 width60"
                >
                  <el-option
                    v-for="(type, index) in spaceLabelTypes"
                    :key="index"
                    :label="type.name"
                    :value="type.value"
                    no-data-text="No data available"
                    clearable
                  ></el-option>
                </el-select>
                <el-select
                  v-model="settings.spaceSecondaryLabel.fontSize"
                  filterable
                  default-first-option
                  placeholder="Font Size"
                  width="250px"
                  class="fc-input-full-border-select2 mL5 mT5 pR5 width20"
                >
                  <el-option
                    v-for="size in fontSizeSet"
                    :key="size"
                    :label="size"
                    :value="size"
                    no-data-text="No data available"
                    clearable
                  ></el-option>
                </el-select>
                <div class="p5">
                  <el-color-picker
                    show-alpha
                    :predefine="getPredefinedColors()"
                    v-model="settings.spaceSecondaryLabel.color"
                  ></el-color-picker>
                </div>
              </div>
              <div
                class="pT10"
                v-if="settings.spaceSecondaryLabel.labelType === 'CUSTOM'"
              >
                <div class="fp-stng-label">
                  {{ i18_text.spaceSecondaryCustomLabel }}
                </div>

                <el-input
                  width="250px"
                  v-model="settings.spaceSecondaryLabel.customText"
                  class="el-input-textbox-full-border width70"
                >
                </el-input>

                <el-popover placement="left" width="300" trigger="click">
                  <el-row :gutter="20">
                    <el-col :span="24">
                      <el-input
                        v-model="spaceSearchVariables"
                        class="input-box-1 mt-1 el-input-textbox-full-border "
                        size="medium"
                        placeholder="Search variables"
                        suffix-icon="el-icon-search"
                      ></el-input>
                    </el-col>
                  </el-row>
                  <div class="checkbox-popover">
                    <div
                      v-for="(field, index) in filteredSpaceFieldList"
                      :key="index"
                      class="pT10"
                    >
                      <el-checkbox
                        @change="
                          addtext(
                            field,
                            'spaceSecondaryLabel',
                            'customText',
                            'space'
                          )
                        "
                        v-model="field.checked"
                        >{{ field.displayName }}</el-checkbox
                      >
                    </div>
                  </div>
                  <el-button
                    icon="el-icon-s-operation"
                    slot="reference"
                    class="mL20"
                  ></el-button>
                </el-popover>
              </div>
            </div>
          </div>

          <div v-if="viewerMode === 'ASSIGNMENT'">
            <el-row>
              <el-col :span="19"
                ><div class="fp-stng-label pL5 pT10">
                  {{ i18_text.nonreservable }}
                </div></el-col
              >
              <el-col :span="4">
                <div class="pL10">
                  <el-color-picker
                    show-alpha
                    :predefine="getPredefinedColors()"
                    v-model="settings.spaceBookingState.nonReservableColor"
                  ></el-color-picker></div
              ></el-col>
            </el-row>
          </div>
        </div>
      </el-collapse-item>
      <el-collapse-item
        :title="i18_text.tab5"
        name="assignemntState"
        v-if="viewerMode === 'ASSIGNMENT' && parkingList.length"
      >
        <div class="p10">
          <div v-if="viewerMode === 'ASSIGNMENT'">
            <el-row>
              <el-col :span="19"
                ><div class="fp-stng-label pL5 pT10">
                  {{ i18_text.unassigned }}
                </div></el-col
              >
              <el-col :span="4">
                <div class="pL10">
                  <el-color-picker
                    show-alpha
                    :predefine="getPredefinedColors()"
                    v-model="settings.assignmentState.unAssignedColor"
                    @change="
                      assignmentSpaceColorChange(
                        settings.assignmentState,
                        'unAssignedOpacity',
                        settings.assignmentState.unAssignedColor
                      )
                    "
                  ></el-color-picker></div
              ></el-col>
            </el-row>

            <el-row>
              <el-col :span="19"
                ><div class="fp-stng-label pL5 pT10">
                  {{ i18_text.assigned }}
                </div></el-col
              >
              <el-col :span="4">
                <div class="pL10">
                  <el-color-picker
                    show-alpha
                    :predefine="getPredefinedColors()"
                    v-model="settings.assignmentState.assignedColor"
                    @change="
                      assignmentSpaceColorChange(
                        settings.assignmentState,
                        'assignedOpacity',
                        settings.assignmentState.assignedColor
                      )
                    "
                  ></el-color-picker></div
              ></el-col>
            </el-row>
          </div>
        </div>
      </el-collapse-item>
      <el-collapse-item
        :title="i18_text.tab4"
        name="bookingstate"
        v-if="viewerMode === 'BOOKING'"
      >
        <div class="p10">
          <div v-if="viewerMode === 'BOOKING'">
            <el-row>
              <el-col :span="19"
                ><div class="fp-stng-label pL5 pT10">
                  {{ i18_text.available }}
                </div></el-col
              >
              <el-col :span="4">
                <div class="pL10">
                  <el-color-picker
                    show-alpha
                    :predefine="getPredefinedColors()"
                    v-model="settings.bookingState.availableColor"
                  ></el-color-picker></div
              ></el-col>
            </el-row>

            <el-row>
              <el-col :span="19"
                ><div class="fp-stng-label pL5 pT10">
                  {{ i18_text.booked }}
                </div></el-col
              >
              <el-col :span="4">
                <div class="pL10">
                  <el-color-picker
                    show-alpha
                    :predefine="getPredefinedColors()"
                    v-model="settings.bookingState.notAvailableColor"
                  ></el-color-picker></div
              ></el-col>
            </el-row>

            <el-row>
              <el-col :span="19"
                ><div class="fp-stng-label pL5 pT10">
                  {{ i18_text.notreservable }}
                </div></el-col
              >
              <el-col :span="4">
                <div class="pL10">
                  <el-color-picker
                    show-alpha
                    :predefine="getPredefinedColors()"
                    v-model="settings.bookingState.nonReservableColor"
                  ></el-color-picker></div
              ></el-col>
            </el-row>
          </div>
        </div>
      </el-collapse-item>
    </el-collapse>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="reset">
        <span>{{ $t('common._common.reset') }}</span>
      </el-button>
      <el-button
        type="primary"
        class="modal-btn-save"
        @click="save"
        :loading="applying"
        >{{
          applying ? $t('common._common.applying') : $t('common._common.apply')
        }}
      </el-button>
    </div>
    <div class="fp-settings" slot="reference" v-if="!loading">
      <el-button icon="el-icon-s-tools" class="fp-stng-btn"></el-button>
    </div>
  </el-popover>
</template>
<script>
import colors from 'charts/helpers/colors'

export default {
  props: ['settingsJson', 'viewerMode', 'isPortal', 'parkingList'],
  data() {
    return {
      loading: true,
      activeName: 'common',
      settings: null,
      settingsClone: null,
      visibility: true,
      applying: false,
      i18_text: {
        compactview: 'Compact View',
        texthalo: 'Text Outline',
        overlaptext: 'Overlap Text',
        fontSize: 'Size',
        fontColor: 'Color',
        fontA: 'A',
        floorplansettings: 'SETTINGS',
        customization: 'CUSTOMIZATION',
        tab1: 'General',
        tab2: 'Desk',
        tab3: 'Space',
        tab4: 'Booking State',
        tab5: 'Space Assignment State',
        primaryText: 'Primary Label',
        deskPrimaryCustomLabel: 'Desk Primary Custom Label',
        deskSecondaryCustomLabel: 'Desk Secondary Custom Label',
        spacePrimaryCustomLabel: 'Space Primary Custom Label',
        spaceSecondaryCustomLabel: 'Space Secondary Custom Label',
        available: 'Available',
        notreservable: 'Non Reservable',
        booked: 'Booked',
        nonreservable: 'Space Background',
        opacity: 'Image Opacity',
        secondaryText: 'Secondary Label',
        unassigned: 'Unassigned',
        assigned: 'Assigned',
      },
      labelTypes: [
        { name: 'Full Name', value: 'FULL_NAME' },
        { name: 'First Name', value: 'FIRST_NAME' },
        { name: 'Last Name', value: 'LAST_NAME' },
        { name: 'Initial With First Name', value: 'INITIAL_WITH_FIRST_NAME' },
        { name: 'Initial With Last Name', value: 'INITIAL_WITH_LAST_NAME' },
        { name: 'Desk Name', value: 'DESK_NAME' },
        { name: 'None', value: 'RETURN_NULL' },
        { name: 'Custom', value: 'CUSTOM' },
      ],
      fontSizeSet: [10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20],
      spaceLabelTypes: [
        { name: 'Space Name', value: 'DEFAULT' },
        { name: 'Category', value: 'CATEGORY' },
        { name: 'None', value: 'RETURN_NULL' },
        { name: 'Custom', value: 'CUSTOM' },
      ],
      deskFields: [],
      spaceFields: [],
      deskSearchVariables: null,
      spaceSearchVariables: null,
    }
  },
  mounted() {
    this.loading = true
    this.getSettings()
    this.$nextTick(() => {
      this.loading = false
    })
  },
  created() {
    this.loadFields()
  },
  computed: {
    filteredDeskFieldList() {
      let fields = []
      this.deskFields.forEach(rt => {
        rt['checked'] = false
        if (this.deskSearchVariables === null) {
          fields.push(rt)
        } else if (
          rt.displayName
            .toLowerCase()
            .indexOf(this.deskSearchVariables.toLowerCase()) >= 0
        ) {
          fields.push(rt)
        }
      })
      return fields
    },
    filteredSpaceFieldList() {
      let fields = []
      this.spaceFields.forEach(rt => {
        rt['checked'] = false
        if (this.spaceSearchVariables === null) {
          fields.push(rt)
        } else if (
          rt.displayName
            .toLowerCase()
            .indexOf(this.spaceSearchVariables.toLowerCase()) >= 0
        ) {
          fields.push(rt)
        }
      })

      return fields
    },
    isImageOpacity() {
      if (this.$route?.query?.opacity) {
        return true
      }
      return false
    },
  },
  watch: {
    settings: {
      handler: function(newval, oldval) {
        if (oldval !== null) {
          this.applyChanges()
        }
      },
      deep: true,
    },
    settingsJson: {
      handler: function() {
        this.applying = false
        this.getSettings()
      },
      deep: true,
    },
  },
  methods: {
    assignmentSpaceColorChange(data, key, color) {
      if (color) {
        let opacity = color.replace(/^.*,(.+)\)/, '$1')
        if (opacity && Number(opacity) > 0) {
          this.$set(data, key, opacity)
        } else {
          this.$set(data, key, 0.3)
        }
      }
    },
    reset() {
      this.getSettings()
      this.visibility = false
    },
    save() {
      this.applying = true
      this.saveChanges()
    },
    addtext(field, module, label, moduleName) {
      if (field.checked) {
        if (this.settings[module][label] === null) {
          this.settings[module][label] = ''
        }
        this.settings[module][label] += '${'
        if (field?.lookupModule?.name) {
          this.settings[module][label] += `${moduleName}.${field.name}.name`
        } else {
          this.settings[module][label] += `${moduleName}.${field.name}`
        }
        this.settings[module][label] += '}'
      } else {
        this.removetext(field)
      }
      this.applyChanges()
    },
    removetext(field, module, label) {
      let text = '${' + field.name + '}'
      this.settings[module][label] = this.settings[module][label].replace(
        text,
        ''
      )
    },
    loadFields() {
      this.$util.loadModuleMeta('desks').then(meta => {
        meta.fields.forEach(rt => {
          this.$set(rt, 'checked', false)
        })
        this.deskFields = meta.fields
      })
      this.$util.loadModuleMeta('space').then(meta => {
        meta.fields.forEach(rt => {
          this.$set(rt, 'checked', false)
        })
        this.spaceFields = meta.fields
      })
    },
    getPredefinedColors() {
      return colors.readingcardColors
    },
    setFontSize(size) {
      this.$set(this.settings.commonSettings, 'fontSize', size)
    },
    getSettings() {
      this.checkmissingData(this.settingsJson) // need to remove this methods
      this.settings = this.$helpers.cloneObject(this.settingsJson)
      this.settingsClone = this.$helpers.cloneObject(this.settingsJson)
    },
    checkmissingData(settingJson) {
      if (!settingJson?.assignmentState?.unAssignedColor) {
        this.$set(settingJson, 'assignmentState', {
          assignedColor: 'rgba(255, 0, 0,0.3)',
          unAssignedColor: 'rgba(0, 0, 0,0.3)',
          unAssignedOpacity: 0.3,
          assignedOpacity: 0.3,
        })
      }
    },
    setSettings() {
      localStorage.setItem('fpSettings', JSON.stringify(this.settings))
    },
    saveChanges() {
      this.$emit('handleSettings', this.settings)
      // this.setSettings()
    },
    applyChanges() {
      this.$emit('applySettings', this.settings)
      // this.setSettings()
    },
  },
}
</script>
<style>
.fp-s-title {
  font-size: 13px;
  padding: 12px;
  font-weight: 500;
  color: #2d2d52;
}
.fp-settings {
  position: absolute;
  top: 110px;
  right: 10px;
}
.fp-stng-btn {
  border-radius: 1;
  padding: 6px;
  box-shadow: 0px 0px 0 2px rgb(0 0 0 / 10%);
  border-color: transparent !important;
}
.fp-stng-label {
  font-weight: 400;
}
.fp-s-text-btn .el-button-group > .el-button {
  height: 45px;
}
.checkbox-popover {
  max-height: 200px;
  background-color: #fff;
  overflow: auto;
}
.el-color-picker__trigger {
  height: 42px;
  width: 42px;
}
.reset-save-btn {
  right: 0;
  left: 0;
  z-index: 1;
}
.fp-seting-popover .el-collapse-item__header {
  padding: 10px;
}
.fp-seting-popover {
  border-radius: 4px;
}
.fp-seting-popover .el-collapse-item__header.is-active {
  color: #39b2c2;
}
.fp-slider-settings .el-slider__button {
  border: 2px solid #39b2c2;
}
.fp-slider-settings .el-slider__bar {
  background-color: #39b2c2;
}
</style>
<style scoped>
.modal-dialog-footer {
  position: relative;
}
</style>
