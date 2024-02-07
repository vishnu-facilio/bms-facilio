<template>
  <div>
    <div v-if="loading" class="mT50 fc-webform-loading">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else>
      <div v-if="editObj && editObj.customTitle" class="fc-pm-main-content-H">
        {{ editObj.customTitle }}
        <div class="fc-heading-border-width43 mT15"></div>
      </div>
      <div v-else-if="title" class="fc-pm-main-content-H">
        {{ title }}
        <div class="fc-heading-border-width43 mT15"></div>
      </div>
      <el-form
        ref="newRecordForm"
        @submit.prevent="save"
        :disabled="disableForm"
        :label-position="labelPosition"
        :model="model"
        :rules="rules"
      >
        <div
          v-for="(section, sidx) in sections"
          :key="sidx"
          :id="name ? `section-${sidx}` : null"
        >
          <el-row
            v-for="(r, fidx) in section.fields"
            :key="fidx"
            class="fc-web-form-row"
            :gutter="20"
          >
            <el-col
              :span="span(labelPosition, element.span)"
              v-for="(element, index) in section.fields[fidx]"
              :key="index"
              class="fc-web-form-input-col"
            >
              <el-form-item
                v-if="element.displayTypeEnum"
                :label="element.displayName"
                :required="element.required"
                label-width="150px"
                :prop="
                  element.parent
                    ? `${element.parent}.${element.name}`
                    : element.name
                "
                :class="element.displayTypeEnum"
              >
                <el-input
                  v-if="element.displayTypeEnum === 'TEXTBOX' && element.parent"
                  :placeholder="'Enter ' + element.displayName"
                  v-model="model[element.parent][element.name]"
                  class="fc-input-full-border2 width575px"
                ></el-input>
                <el-input
                  v-else-if="
                    element.displayTypeEnum === 'TEXTBOX' &&
                      ((element.name === 'workOrder' &&
                        name === 'inventoryRequestWOForm') ||
                        (element.name === 'email' &&
                          editObj &&
                          name === 'contactForm'))
                  "
                  :disabled="true"
                  :placeholder="'Enter ' + element.displayName"
                  v-model="model[element.name]"
                  class="fc-input-full-border2 width575px"
                ></el-input>
                <el-input
                  v-else-if="element.displayTypeEnum === 'TEXTBOX'"
                  :placeholder="'Enter ' + element.displayName"
                  v-model="model[element.name]"
                  class="fc-input-full-border2 width575px"
                ></el-input>
                <i
                  class="el-icon-s-order pointer edit-icon-color"
                  v-else-if="element.displayTypeEnum === 'LONG_DESC'"
                  data-arrow="true"
                  :title="$t('common.header.add_long_description')"
                  v-tippy
                  @click="addLongDescriptionVisibility = true"
                ></i>
                <el-input
                  v-else-if="
                    element.displayTypeEnum === 'TEXTAREA' && element.parent
                  "
                  type="textarea"
                  v-model="model[element.parent][element.name]"
                  :autosize="{ minRows: 6, maxRows: 4 }"
                  :placeholder="'Type your ' + element.displayName"
                  class="fc-input-full-border-textarea width575px"
                  resize="none"
                ></el-input>
                <el-input
                  v-else-if="element.displayTypeEnum === 'TEXTAREA'"
                  type="textarea"
                  v-model="model[element.name]"
                  :autosize="{ minRows: 6, maxRows: 4 }"
                  :placeholder="'Type your ' + element.displayName"
                  class="fc-input-full-border-textarea width575px"
                  resize="none"
                ></el-input>
                <el-input
                  type="number"
                  :min="0"
                  v-else-if="
                    (element.displayTypeEnum === 'NUMBER' ||
                      element.displayTypeEnum === 'DECIMAL') &&
                      element.parent
                  "
                  v-model="model[element.parent][element.name]"
                  class="fc-input-full-border2 width575px"
                ></el-input>
                <el-input
                  type="number"
                  :min="0"
                  v-else-if="
                    element.displayTypeEnum === 'NUMBER' ||
                      element.displayTypeEnum === 'DECIMAL'
                  "
                  v-model="model[element.name]"
                  class="fc-input-full-border2 width575px"
                ></el-input>
                <el-checkbox
                  disabled
                  v-else-if="
                    element.displayTypeEnum === 'DECISION_BOX' &&
                      editObj &&
                      (editObj.customTitle === 'Edit Tool Type' ||
                        editObj.customTitle === 'Edit Item Type') &&
                      element.name === 'isRotating'
                  "
                  v-model="model[element.name]"
                ></el-checkbox>
                <el-checkbox
                  :disabled="!model['isRotating']"
                  v-else-if="
                    element.displayTypeEnum === 'DECISION_BOX' &&
                      name === 'item_types_form' &&
                      element.name === 'isConsumable'
                  "
                  v-model="model[element.name]"
                ></el-checkbox>
                <el-checkbox
                  @change="isRotatingChangeActions()"
                  v-else-if="
                    element.displayTypeEnum === 'DECISION_BOX' &&
                      name === 'item_types_form' &&
                      element.name === 'isRotating'
                  "
                  v-model="model[element.name]"
                ></el-checkbox>
                <el-checkbox
                  v-else-if="
                    element.displayTypeEnum === 'DECISION_BOX' && element.parent
                  "
                  v-model="model[element.parent][element.name]"
                ></el-checkbox>
                <el-checkbox
                  v-else-if="element.displayTypeEnum === 'DECISION_BOX'"
                  v-model="model[element.name]"
                ></el-checkbox>
                <el-date-picker
                  v-else-if="
                    element.displayTypeEnum === 'DATE' && element.parent
                  "
                  v-model="model[element.parent][element.name]"
                  :type="'date'"
                  class="fc-input-full-border2 width575px"
                ></el-date-picker>
                <el-date-picker
                  v-else-if="element.displayTypeEnum === 'DATE'"
                  v-model="model[element.name]"
                  :type="'date'"
                  class="fc-input-full-border2 width575px"
                ></el-date-picker>
                <el-date-picker
                  v-else-if="
                    element.displayTypeEnum === 'DATETIME' && element.parent
                  "
                  v-model="model[element.parent][element.name]"
                  :type="'datetime'"
                  class="fc-input-full-border2 width575px"
                ></el-date-picker>
                <f-date-picker
                  v-else-if="
                    element.displayTypeEnum === 'DATETIME' &&
                      name === 'reservationForm' &&
                      (element.name === 'scheduledEndTime' ||
                        element.name === 'scheduledStartTime')
                  "
                  :disabled="
                    element.name === 'scheduledEndTime' &&
                      parseInt(model['durationType']) !== 6
                  "
                  value-format="timestamp"
                  v-model="model[element.name]"
                  :type="'datetime'"
                  class="fc-input-full-border2 width575px"
                ></f-date-picker>
                <el-date-picker
                  v-else-if="element.displayTypeEnum === 'DATETIME'"
                  v-model="model[element.name]"
                  :type="'datetime'"
                  class="fc-input-full-border2 width575px"
                ></el-date-picker>
                <el-time-picker
                  v-else-if="
                    element.displayTypeEnum === 'TIME' && element.parent
                  "
                  format="HH:mm"
                  value-format="timestamp"
                  v-model="model[element.parent][element.name]"
                  :placeholder="element.displayName"
                  class="fc-input-full-border-select2 width575px"
                ></el-time-picker>
                <el-time-picker
                  v-else-if="element.displayTypeEnum === 'TIME'"
                  format="HH:mm"
                  value-format="timestamp"
                  v-model="model[element.name]"
                  :placeholder="element.displayName"
                  class="fc-input-full-border-select2 width575px"
                ></el-time-picker>
                <el-select
                  v-else-if="element.displayTypeEnum === 'URGENCY'"
                  filterable
                  v-model="model[element.name]"
                  class="fc-input-full-border-select2 width575px"
                >
                  <el-option
                    v-for="(option, index) in element.options"
                    :key="index"
                    :label="option.label"
                    :value="option.value"
                  ></el-option>
                </el-select>
                <div
                  v-else-if="
                    element.options &&
                      element.parent &&
                      model[element.parent][element.name] &&
                      editObj &&
                      editObj[element.parent] &&
                      editObj[element.parent].disabled
                  "
                >
                  <el-select
                    v-if="element.options && element.parent"
                    disabled
                    v-model="model[element.parent][element.name]"
                    class="fc-input-full-border-select2 width575px"
                  >
                    <el-option
                      v-for="(option, index) in element.options"
                      :key="index"
                      :label="option.label"
                      :value="String(option.value)"
                    ></el-option>
                  </el-select>
                </div>
                <el-select
                  v-else-if="
                    element.options &&
                      element.parent === 'vendor' &&
                      model[element.parent][element.name] &&
                      (name === 'purchaseRequestForm' ||
                        name === 'purchaseOrderForm' ||
                        name === 'purchaseContractForm')
                  "
                  filterable
                  :clearable="!element.required"
                  @change="
                    vendorChangeActions(model[element.parent][element.name])
                  "
                  v-model="model[element.parent][element.name]"
                  class="fc-input-full-border-select2 width575px"
                >
                  <el-option
                    v-for="(option, index) in element.options"
                    :key="index"
                    :label="option.label"
                    :value="option.value"
                  ></el-option>
                </el-select>
                <el-select
                  v-else-if="
                    element.options &&
                      element.parent === 'storeRoom' &&
                      model[element.parent][element.name] &&
                      (name === 'purchaseRequestForm' ||
                        name === 'purchaseOrderForm')
                  "
                  filterable
                  :clearable="!element.required"
                  @change="
                    storeRoomChangeActions(model[element.parent][element.name])
                  "
                  v-model="model[element.parent][element.name]"
                  class="fc-input-full-border-select2 width575px"
                >
                  <el-option
                    v-for="(option, index) in element.options"
                    :key="index"
                    :label="option.label"
                    :value="option.value"
                  ></el-option>
                </el-select>
                <el-select
                  v-else-if="
                    ['portalWorkpermitForm', 'workpermitForm'].includes(name) &&
                      element.name === 'permitType'
                  "
                  filterable
                  :clearable="!element.required"
                  @change="permitTypeChangeActions(model[element.name])"
                  v-model="model[element.name]"
                  class="fc-input-full-border-select2 width575px"
                >
                  <el-option
                    v-for="(option, index) in element.options"
                    :key="index"
                    :label="option.label"
                    :value="String(option.value)"
                  ></el-option>
                </el-select>
                <el-select
                  v-else-if="
                    ['portalWorkpermitForm', 'workpermitForm'].includes(name) &&
                      element.parent === 'vendorContact'
                  "
                  filterable
                  :disabled="disableWorkPermitVendorFields"
                  :clearable="!element.required"
                  v-model="model[element.parent][element.name]"
                  class="fc-input-full-border-select2 width575px"
                >
                  <el-option
                    v-for="(option, index) in vendorContacts"
                    :key="index"
                    :label="option.name"
                    :value="String(option.id)"
                  ></el-option>
                </el-select>
                <el-select
                  v-else-if="
                    ['portalWorkpermitForm', 'workpermitForm'].includes(name) &&
                      element.parent === 'vendor'
                  "
                  filterable
                  :disabled="disableWorkPermitVendorFields"
                  :clearable="!element.required"
                  @change="
                    permitVendorChangeActions(
                      model[element.parent][element.name]
                    )
                  "
                  v-model="model[element.parent][element.name]"
                  class="fc-input-full-border-select2 width575px"
                >
                  <el-option
                    v-for="(option, index) in element.options"
                    :key="index"
                    :label="option.label"
                    :value="option.value"
                  ></el-option>
                </el-select>
                <template v-else-if="element.parent === 'vendor'">
                  <FLookupField
                    :model.sync="model[element.parent][element.name]"
                    :field="getVendorField"
                    @showLookupWizard="() => (canShowLookupWizard = true)"
                    class="width575px"
                  />
                  <FLookupFieldWizard
                    v-if="canShowLookupWizard"
                    :canShowLookupWizard.sync="canShowLookupWizard"
                    :selectedLookupField="getVendorField"
                    @setLookupFieldValue="setLookupFieldValue"
                  />
                </template>
                <el-select
                  filterable
                  v-else-if="element.parent === 'rotatingItem'"
                  v-model="model[element.parent][element.name]"
                  :clearable="!element.required"
                  @change="rotatingItemToolChangeActions"
                  class="fc-input-full-border-select2 width575px"
                >
                  <template>
                    <el-option
                      v-for="(item, index) in rotatingItemList"
                      :key="index"
                      :label="item.itemType.name"
                      :value="String(item.id)"
                    >
                      <span class="fL">{{ item.itemType.name }}</span>
                      <span class="select-float-right-text13">{{
                        item.storeRoom.name
                      }}</span>
                    </el-option>
                  </template>
                </el-select>
                <el-select
                  filterable
                  v-else-if="element.parent === 'rotatingTool'"
                  v-model="model[element.parent][element.name]"
                  @change="rotatingItemToolChangeActions"
                  :clearable="!element.required"
                  class="fc-input-full-border-select2 width575px"
                >
                  <el-option
                    v-for="(option, index) in rotatingToolList"
                    :key="index"
                    :label="option.toolType.name"
                    :value="String(option.id)"
                  >
                    <span class="fL">{{ option.toolType.name }}</span>
                    <span class="select-float-right-text13">{{
                      option.storeRoom.name
                    }}</span>
                  </el-option>
                </el-select>
                <div
                  v-else-if="element.displayTypeEnum === 'SPACEMULTICHOOSER'"
                  class="fc-input-full-border-select2 width575px"
                  style="float:left"
                >
                  <el-input
                    v-model="selectedResourceLabel"
                    type="text"
                    :placeholder="$t('common._common.to_search_type')"
                    class="fc-input-full-border-select2"
                  >
                    <i
                      @click="chooserVisibility = true"
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                    ></i>
                  </el-input>
                  <space-asset-multi-chooser
                    v-if="chooserVisibility"
                    @associate="associateResource"
                    :resourceType="'2, 3, 4'"
                    :disable="true"
                    :visibility.sync="chooserVisibility"
                    :initialValues="resourceData"
                    :filter="filter"
                    :showAsset="false"
                    :hideBanner="true"
                  ></space-asset-multi-chooser>
                </div>
                <div
                  v-else-if="element.displayTypeEnum === 'SITEMULTICHOOSER'"
                  class="fc-input-full-border-select2 width575px"
                  style="float:left"
                >
                  <el-input
                    v-model="selectedSiteLabel"
                    type="text"
                    :placeholder="$t('common._common.to_search_type')"
                    class="fc-input-full-border-select2"
                  >
                    <i
                      @click="chooserVisibility = true"
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                    ></i>
                  </el-input>
                  <space-asset-multi-chooser
                    v-if="chooserVisibility"
                    @associate="associateSite"
                    :resourceType="[1]"
                    :disable="true"
                    :visibility.sync="chooserVisibility"
                    :initialValues="siteresourceData"
                    :showAsset="false"
                    :hideBanner="true"
                  ></space-asset-multi-chooser>
                </div>
                <el-select
                  v-else-if="element.options && element.parent"
                  :clearable="!element.required"
                  filterable
                  v-model="model[element.parent][element.name]"
                  class="fc-input-full-border-select2 width575px"
                >
                  <el-option
                    v-if="filterBySite(option)"
                    v-for="(option, index) in element.options"
                    :key="index"
                    :label="option.label"
                    :value="option.value"
                  ></el-option>
                </el-select>
                <el-select
                  v-else-if="element.options"
                  filterable
                  :clearable="!element.required"
                  v-model="model[element.name]"
                  class="fc-input-full-border-select2 width575px"
                >
                  <el-option
                    v-if="filterBySite(option)"
                    v-for="(option, index) in element.options"
                    :key="index"
                    :label="option.label"
                    :value="option.value"
                  ></el-option>
                </el-select>
                <div v-else-if="element.displayTypeEnum === 'ATTACHMENT'">
                  <div class="fc-attachments fc-wo-form-attachement-block">
                    <div
                      v-show="model['attachedFiles'].attachments.length >= 1"
                      class="pB25 border-bottom3 pT10"
                    >
                      <span
                        ><img
                          src="~statics/icons/clip-black.svg"
                          class="pull-right pointer"
                          onclick="document.getElementById('facilio-web-form-file-attachment').click()"
                          style="height:15px;width:13px"
                      /></span>
                    </div>
                    <div
                      class="fc-attachment-row visibility-visible-actions"
                      v-for="(attachment, index) in model['attachedFiles']
                        .attachments"
                      :key="attachment.name"
                    >
                      <div class="attachment-label label-txt-black">
                        {{ attachment.fileName }}
                      </div>
                      <div class="attachment-sublabel">
                        <span class="fc-grey-text12-light">{{
                          attachment.fileSize | prettyBytes
                        }}</span>
                        <span
                          class="attachment-sublabel"
                          v-if="attachment.status === 1"
                          >, {{ $t('common._common.uploading') }}</span
                        >
                        <span
                          class="attachment-sublabel"
                          v-else-if="attachment.status === 2"
                          >, {{ $t('common._common.success') }}</span
                        >
                        <span class="attachment-sublabel" v-else>
                          {{ attachment.error }}</span
                        >
                        <div
                          class="innerblock pull-right visibility-hide-actions"
                        >
                          <i
                            class="el-icon-delete fc-delete-icon pointer attach-delete-icon"
                            @click="deleteAttachment('attachedFiles', index)"
                          ></i>
                        </div>
                      </div>
                    </div>
                    <div
                      v-show="!model['attachedFiles'].attachments.length"
                      class="pointer"
                      @change="
                        filesChange('attachedFiles', $event.target.files)
                      "
                    >
                      <form
                        enctype="multipart/form-data"
                        novalidate
                        v-on:submit.prevent="addAttachment"
                      >
                        <div class="dropbox text-center">
                          <img
                            src="~assets/upload-icon.svg"
                            width="20"
                            height="20"
                            class="mT10 opacity-05"
                          />
                          <input
                            ref="attachment"
                            id="facilio-web-form-file-attachment"
                            type="file"
                            class="input-file"
                          />
                          <p>
                            {{ $t('common.attachment_form.drag_and_drop_file')
                            }}<br />
                            {{ $t('common.attachment_form.click_to_browse') }}
                          </p>
                        </div>
                      </form>
                    </div>
                  </div>
                </div>

                <div v-else-if="element.displayTypeEnum === 'LOGO'">
                  <div class="fc-attachments fc-wo-form-attachement-block">
                    <div
                      class="fc-attachment-row visibility-visible-actions tenant-upload-con"
                      v-if="model['logo'].logoImageUrl"
                    >
                      <div class="logo-upload">
                        <span class="input-upload tenant-upload">
                          <input
                            ref="logo"
                            type="file"
                            class="upload"
                            style="width: 200px;height:100px;"
                            @change="logoChange('logo', $event.target.files)"
                          />
                          <span class="upload-icon-block">
                            <img
                              src="~assets/image-upload.svg"
                              class="upload-img"
                            />
                            <img
                              v-if="model['logo'].logoImageUrl"
                              :src="model['logo'].logoImageUrl"
                              class="upload-img2"
                            />
                          </span>
                        </span>
                      </div>
                    </div>
                    <div
                      v-if="!model['logo'].logoImageUrl"
                      class="pointer"
                      @change="logoChange('logo', $event.target.files)"
                    >
                      <form
                        enctype="multipart/form-data"
                        novalidate
                        v-on:submit.prevent="addAttachment"
                      >
                        <div class="dropbox text-center">
                          <img
                            src="~assets/upload-icon.svg"
                            width="20"
                            height="20"
                            class="mT10 opacity-05"
                          />
                          <input
                            ref="attachment"
                            id="facilio-web-form-file-attachment"
                            type="file"
                            class="input-file"
                          />
                          <p>
                            {{ $t('common.attachment_form.drag_and_drop_file')
                            }}<br />
                            {{ $t('common.attachment_form.click_to_browse') }}
                          </p>
                        </div>
                      </form>
                    </div>
                  </div>
                </div>
                <div
                  v-else-if="element.displayTypeEnum === 'WOASSETSPACECHOOSER'"
                  class="fc-input-full-border-select2 width575px"
                  style="float:left"
                >
                  <el-input
                    @change="
                      quickSearchQuery = spaceAssetDisplayName
                      showSpaceAssetChooser()
                    "
                    v-model="spaceAssetDisplayName"
                    type="text"
                    :placeholder="$t('common._common.to_search_type')"
                    class="fc-input-full-border-select2"
                  >
                    <i
                      @click="visibility = true"
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                    ></i>
                  </el-input>
                  <wo-space-asset-chooser
                    @associate="associate($event, element.name)"
                    :visibility.sync="visibility"
                    :query="quickSearchQuery"
                    :appendToBody="false"
                    :filter="filter"
                  ></wo-space-asset-chooser>
                </div>
                <div
                  v-else-if="element.displayTypeEnum === 'QR'"
                  class="fc-input-full-border-select2 width575px"
                  style="float:left"
                >
                  <el-input
                    @change="
                      quickSearchQuery = spaceAssetDisplayName
                      showSpaceAssetChooser()
                      hitServer
                    "
                    v-model="spaceAssetDisplayName"
                    type="text"
                    :placeholder="$t('common._common.to_search_type')"
                    class="fc-input-full-border-select2"
                  >
                    <i
                      @click="visibility = true"
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                    ></i>
                  </el-input>
                  <wo-space-asset-chooser
                    @associate="associateQr($event, element.name)"
                    :visibility.sync="visibility"
                    :query="quickSearchQuery"
                    :appendToBody="false"
                    :filter="filter"
                  ></wo-space-asset-chooser>
                </div>
                <div
                  v-else-if="element.displayTypeEnum === 'SPACECHOOSER'"
                  class="fc-input-full-border-select2 width575px"
                  style="float:left"
                >
                  <el-input
                    @change="
                      quickSearchQuery = spaceAssetDisplayName
                      showSpaceAssetChooser()
                    "
                    v-model="spaceAssetDisplayName"
                    type="text"
                    :placeholder="$t('common._common.to_search_type')"
                    class="fc-input-full-border-select2"
                  >
                    <i
                      @click="visibility = true"
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                    ></i>
                  </el-input>
                  <wo-space-asset-chooser
                    @associate="associate($event, element.name)"
                    :visibility.sync="visibility"
                    :query="quickSearchQuery"
                    :showAsset="false"
                    picktype="space"
                    :appendToBody="false"
                    :filter="filter"
                  ></wo-space-asset-chooser>
                </div>
                <div
                  v-else-if="element.displayTypeEnum === 'RESERVABLE_SPACES'"
                  class="fc-input-full-border-select2 width575px"
                  style="float:left"
                >
                  <el-input
                    @change="showSpaceAssetChooser()"
                    readonly
                    v-model="spaceAssetDisplayName"
                    type="text"
                    :placeholder="$t('common._common.select_space')"
                    class="fc-input-full-border-select2"
                  >
                    <i
                      @click="visibility = true"
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                    ></i>
                  </el-input>
                  <wo-space-asset-chooser
                    @associate="associate($event, element.name)"
                    :visibility.sync="visibility"
                    :query="quickSearchQuery"
                    :showAsset="false"
                    picktype="space"
                    :appendToBody="false"
                    :hideSidebar="'false'"
                    :queryFilter="reservableFilter"
                    :resourceType="[4]"
                  ></wo-space-asset-chooser>
                </div>
                <div
                  v-else-if="element.displayTypeEnum === 'BUILDINGCHOOSER'"
                  class="fc-input-full-border-select2 width575px"
                  style="float:left"
                >
                  <el-input
                    @change="
                      quickSearchQuery = spaceAssetDisplayName
                      showSpaceAssetChooser()
                    "
                    v-model="spaceAssetDisplayName"
                    type="text"
                    :placeholder="$t('common._common.to_search_type')"
                    class="fc-input-full-border-select2"
                  >
                    <i
                      @click="visibility = true"
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                    ></i>
                  </el-input>
                  <wo-space-asset-chooser
                    @associate="associate($event, element.name)"
                    :showAsset="false"
                    :visibility.sync="visibility"
                    :query="quickSearchQuery"
                    :resourceType="[2]"
                    :appendToBody="false"
                    :filter="filter"
                  ></wo-space-asset-chooser>
                </div>
                <div
                  v-else-if="element.displayTypeEnum === 'TEAMSTAFFASSIGNMENT'"
                  class="fc-input-full-border-select2 width575px"
                >
                  <div class="fc-border-input-div">
                    <span>{{ getTeamStaffLabel(model) }}</span>
                    <span style="float: right;padding-right: 5px;">
                      <i
                        class="el-icon-arrow-down team-down-icon"
                        style="color: #5a7591 !important; font-size: 14px !important; font-weight: 600 !important;"
                      ></i>
                    </span>
                  </div>
                  <f-assignment
                    :model="model"
                    :siteId="model.siteId"
                    viewtype="form"
                  ></f-assignment>
                  <div></div>
                </div>
                <div
                  v-else-if="element.displayTypeEnum === 'USER'"
                  class="fc-input-full-border-select2 width575px"
                >
                  <div class="form-input">
                    <el-select
                      v-model="model[element.name].id"
                      filterable
                      clearable
                      :placeholder="element.displayName"
                      style="width: 100%;"
                    >
                      <el-option
                        v-for="user in users"
                        :key="user.id"
                        :label="user.name"
                        :value="String(user.id)"
                        v-if="
                          !(
                            showUsersWithNoScope &&
                            user.accessibleSpace &&
                            user.accessibleSpace.length > 0
                          )
                        "
                      ></el-option>
                    </el-select>
                  </div>
                </div>
                <div
                  v-else-if="element.displayTypeEnum === 'MULTI_USER_LIST'"
                  class="fc-input-full-border-select2 el-select-block fc-tag width575px"
                >
                  <div class="form-input">
                    <el-select
                      v-model="model[element.name]"
                      collapse-tags
                      :multiple="true"
                      filterable
                      clearable
                      :placeholder="element.displayName"
                      class="width100"
                    >
                      <el-option
                        v-for="user in users"
                        :key="user.id"
                        :label="user.name"
                        :value="String(user.id)"
                        v-if="
                          !(
                            showUsersWithNoScope &&
                            user.accessibleSpace &&
                            user.accessibleSpace.length > 0
                          )
                        "
                      ></el-option>
                    </el-select>
                  </div>
                </div>
                <el-input
                  v-else-if="element.displayTypeEnum === 'TICKETNOTES'"
                  type="textarea"
                  v-model="model.comment"
                  :autosize="{ minRows: 6, maxRows: 4 }"
                  :placeholder="'Type your ' + element.displayName"
                  class="fc-input-full-border-textarea width575px"
                  resize="none"
                ></el-input>
                <template v-else-if="element.displayTypeEnum === 'DURATION'">
                  <div class="position-relative">
                    <el-input
                      type="number"
                      v-model="model[element.name].days"
                      class="fc-input-full-border-select2 duration-input"
                      ><span slot="suffix" class="pR10 fc-grey6 f12">{{
                        $t('common._common.days')
                      }}</span></el-input
                    >
                    <el-select
                      v-model="model[element.name].hours"
                      class="fc-input-full-border-select2 duration-input mL15 position-relative"
                    >
                      <el-option
                        v-for="(item, idx) in model[element.name].options"
                        :key="idx"
                        :label="item.label"
                        :value="item.value"
                      >
                      </el-option>
                    </el-select>
                    <span
                      class="fc-grey6 f12 position-relative fc-input-select-hour"
                      >{{ $t('common._common.hours') }}</span
                    >
                  </div>
                </template>
                <div
                  v-else-if="element.displayTypeEnum === 'ADDRESS'"
                  class="position-relative"
                >
                  <div class="profile-input-container">
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <div class="profile-input-group pB20">
                          <p class="input-label-text">
                            {{ $t('setup.setup_profile.street') }}
                          </p>
                          <el-input
                            v-model="model[element.name].street"
                            :placeholder="
                              $t('setup.setup_profile.enter_street')
                            "
                            class="fc-input-full-border-select2"
                          />
                        </div>
                      </el-col>
                      <el-col :span="12">
                        <div class="profile-input-group pB20">
                          <p class="input-label-text">
                            {{ $t('setup.setup_profile.city') }}
                          </p>
                          <el-input
                            v-model="model[element.name].city"
                            :placeholder="$t('setup.setup_profile.entre_city')"
                            class="fc-input-full-border-select2"
                          />
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                  <div class="profile-input-container">
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <div class="profile-input-group pB20">
                          <p class="input-label-text">
                            {{ $t('setup.setup_profile.state') }}
                          </p>
                          <el-input
                            v-model="model[element.name].state"
                            :placeholder="$t('setup.setup_profile.enter_state')"
                            class="fc-input-full-border-select2"
                          />
                        </div>
                      </el-col>
                      <el-col :span="12">
                        <div class="profile-input-group pB20">
                          <p class="input-label-text">
                            {{ $t('setup.setup_profile.zipcode') }}
                          </p>
                          <el-input
                            v-model="model[element.name].zip"
                            type="number"
                            :placeholder="
                              $t('setup.setup_profile.enter_zipcode')
                            "
                            class="fc-input-full-border-select2"
                          />
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                  <div class="profile-input-container">
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <div class="profile-input-group pB20">
                          <p class="input-label-text">
                            {{ $t('setup.setup_profile.country') }}
                          </p>
                          <el-select
                            filterable
                            clearable
                            v-model="model[element.name].country"
                            :placeholder="
                              $t('setup.setup_profile.enter_country')
                            "
                            class="fc-input-full-border-select2 width100"
                          >
                            <el-option
                              v-for="country in countryList"
                              :key="country.value"
                              :label="country.label"
                              :value="String(country.value)"
                            >
                            </el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </div>
                <div
                  v-else-if="element.displayTypeEnum === 'SADDRESS'"
                  class="position-relative"
                >
                  <div
                    v-if="
                      element.name === 'billToAddress' ||
                        element.name === 'shipToAddress'
                    "
                    class="fc-heading-border-width43 mT15"
                    style="margin-top: -10px;margin-bottom: 30px;"
                  ></div>
                  <div class="profile-input-container">
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <div class="profile-input-group pB20">
                          <p class="input-label-text">
                            {{ $t('setup.setup_profile.street') }}
                          </p>
                          <el-input
                            v-model="model[element.name].street"
                            :placeholder="
                              $t('setup.setup_profile.enter_street')
                            "
                            class="fc-input-full-border-select2"
                          />
                        </div>
                      </el-col>
                      <el-col :span="12">
                        <div class="profile-input-group pB20">
                          <p class="input-label-text">
                            {{ $t('setup.setup_profile.city') }}
                          </p>
                          <el-input
                            v-model="model[element.name].city"
                            :placeholder="$t('setup.setup_profile.entre_city')"
                            class="fc-input-full-border-select2"
                          />
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                  <div class="profile-input-container">
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <div class="profile-input-group pB20">
                          <p class="input-label-text">
                            {{ $t('setup.setup_profile.state') }}
                          </p>
                          <el-input
                            v-model="model[element.name].state"
                            :placeholder="$t('setup.setup_profile.enter_state')"
                            class="fc-input-full-border-select2"
                          />
                        </div>
                      </el-col>
                      <el-col :span="12">
                        <div class="profile-input-group pB20">
                          <p class="input-label-text">
                            {{ $t('setup.setup_profile.zipcode') }}
                          </p>
                          <el-input
                            v-model="model[element.name].zip"
                            type="number"
                            :placeholder="
                              $t('setup.setup_profile.enter_zipcode')
                            "
                            class="fc-input-full-border-select2"
                          />
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                  <div class="profile-input-container">
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <div class="profile-input-group pB20">
                          <p class="input-label-text">
                            {{ $t('setup.setup_profile.country') }}
                          </p>
                          <el-select
                            filterable
                            clearable
                            v-model="model[element.name].country"
                            :placeholder="
                              $t('setup.setup_profile.enter_country')
                            "
                            class="fc-input-full-border-select2 width100"
                          >
                            <el-option
                              v-for="country in countryList"
                              :key="country.value"
                              :label="country.label"
                              :value="String(country.value)"
                            >
                            </el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </div>
                <div
                  v-else-if="element.displayTypeEnum === 'IMAGE'"
                  class="position-relative"
                >
                  <div class="web-form-image-body" v-if="!model[element.name]">
                    <input
                      type="file"
                      drag
                      class="input-file-image"
                      accept="image/*"
                      style="height: 160px; width: 100%; position: absolute; top: 0; left: 0; cursor: pointer;"
                      @change="imageChange($event.target.files, element.name)"
                    />
                    <div>
                      {{
                        $t('common.attachment_form.click_to_choose_file_drag')
                      }}
                    </div>
                  </div>
                  <div class="web-form-image-uploaded" v-else>
                    <img
                      :src="
                        getBaseURL() +
                          '/v2/files/preview/' +
                          model[element.name]
                      "
                      class="upload-img"
                      :title="$t('common.attachment_form.click_to_change')"
                    />
                    <input
                      type="file"
                      drag
                      class="input-file-image"
                      accept="image/*"
                      style="height: 160px; width: 100%; position: absolute; top: 0; left: 0; cursor: pointer;"
                      @change="imageChange($event.target.files, element.name)"
                    />
                  </div>
                </div>
                <div
                  v-else-if="element.displayTypeEnum === 'PURCHASEDITEM'"
                  class="position-relative"
                >
                  <table
                    class="setting-list-view-table fc-inventory-item-table store-table"
                  >
                    <thead>
                      <tr>
                        <th
                          class="setting-table-th setting-th-text uppercase"
                          style="width: 240px;"
                        >
                          {{ $t('common._common.quantity') }}
                        </th>
                        <th
                          class="setting-table-th setting-th-text uppercase"
                          style="width: 240px;"
                        >
                          {{ $t('common.header.unit_price') }}
                        </th>
                        <th></th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr
                        v-for="(field, index) in model[element.name]"
                        :key="index"
                        class="visibility-visible-actions"
                      >
                        <td class="module-builder-td">
                          <el-input
                            :placeholder="$t('common._common.quantity')"
                            type="number"
                            :min="0"
                            v-model="model[element.name][index].quantity"
                            class="fc-input-full-border-select2 duration-input"
                          ></el-input>
                        </td>
                        <td class="module-builder-td">
                          <el-input
                            :placeholder="$t('common.header.unit_price')"
                            type="number"
                            :min="0"
                            v-model="model[element.name][index].unitcost"
                            class="fc-input-full-border-select2 duration-input"
                          ></el-input>
                        </td>
                        <td>
                          <div
                            class="visibility-hide-actions export-dropdown-menu"
                          >
                            <span
                              @click="
                                addPurchasedItemEntry(
                                  model[element.name],
                                  index,
                                  element.displayTypeEnum
                                )
                              "
                              :title="$t('common._common.add')"
                              v-tippy
                            >
                              <img
                                src="~assets/add-icon.svg"
                                class="mR10 mT10"
                              />
                            </span>
                            <span
                              v-if="model[element.name].length > 1"
                              @click="
                                removeEntryFromList(model[element.name], index)
                              "
                              :title="$t('common._common.remove')"
                              v-tippy
                            >
                              <img src="~assets/remove-icon.svg" class="mT10" />
                            </span>
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div
                  v-else-if="element.displayTypeEnum === 'SERVICEVENDORS'"
                  class="position-relative"
                >
                  <table class="setting-list-view-table store-table">
                    <thead class="setup-dialog-thead">
                      <tr>
                        <th
                          class="setting-table-th setting-th-text"
                          style="width: 240px;"
                        >
                          {{ $t('common.products.vendor') }}
                        </th>
                        <th
                          class="setting-table-th setting-th-text"
                          style="width: 240px;"
                        >
                          {{ $t('common.header.last_price') }}
                        </th>
                        <th></th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr
                        v-for="(field, index) in model[element.name]"
                        :key="index"
                        class="visibility-visible-actions"
                      >
                        <td class="module-builder-td">
                          <el-select
                            filterable
                            clearable
                            v-model="model[element.name][index].vendor.id"
                            class="fc-input-full-border-select2 width100"
                          >
                            <el-option
                              v-for="(vendor, index) in vendors"
                              :key="index"
                              :label="vendor.label"
                              :value="vendor.value"
                            ></el-option>
                          </el-select>
                        </td>
                        <td class="pL20">
                          <el-input
                            :placeholder="$t('common.header.price')"
                            type="number"
                            :min="0"
                            v-model="model[element.name][index].lastPrice"
                            class="fc-input-full-border-select2 duration-input"
                          ></el-input>
                        </td>
                        <td>
                          <div
                            class="visibility-hide-actions export-dropdown-menu"
                          >
                            <span
                              @click="
                                addServiceVendorEntry(
                                  model[element.name],
                                  index,
                                  element.displayTypeEnum
                                )
                              "
                              title="Add"
                              v-tippy
                            >
                              <img
                                src="~assets/add-icon.svg"
                                class="mR10 mT10"
                              />
                            </span>
                            <span
                              v-if="model[element.name].length > 1"
                              @click="
                                removeEntryFromList(model[element.name], index)
                              "
                              title="Remove"
                              v-tippy
                            >
                              <img src="~assets/remove-icon.svg" class="mT10" />
                            </span>
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div
                  v-else-if="element.displayTypeEnum === 'UNIT'"
                  class="position-relative"
                >
                  <el-row class="position-relative" :gutter="20">
                    <el-col :span="12" class="position-relative">
                      <el-select
                        @change="loadUnit(element.name, element.MetricMeta)"
                        v-model="element.MetricMeta.metric"
                        class="fc-input-full-border-select2 width100"
                      >
                        <el-option
                          v-for="(dtype, index) in metricsUnits.metrics"
                          :key="index"
                          :label="dtype.name"
                          :value="dtype.metricId"
                        ></el-option>
                      </el-select>
                      <span class="position-absolute right60">{{
                        $t('common.tabs.metric')
                      }}</span>
                    </el-col>
                    <el-col :span="12" class="position-relative">
                      <el-select
                        clearable
                        @change="
                          setUnit(element.name, element.MetricMeta.unitId)
                        "
                        v-model="element.MetricMeta.unitId"
                        class="fc-input-full-border-select2 width100"
                      >
                        <el-option
                          v-for="(dtype, index) in metricsUnits.metricWithUnits[
                            element.MetricMeta.metricName
                          ]"
                          :key="index"
                          :label="dtype.displayName"
                          :value="dtype.unitId"
                        ></el-option>
                      </el-select>
                      <span class="position-absolute right35">{{
                        $t('common.header.unit')
                      }}</span>
                    </el-col>
                  </el-row>
                </div>

                <template
                  v-else-if="element.displayTypeEnum === 'VENDOR_CONTACTS'"
                >
                  <div class="row full-layout-white fc-form-wo-task-con">
                    <div class="width100 pB50">
                      <div
                        v-if="element.displayTypeEnum === 'VENDOR_CONTACTS'"
                        class="position-relative"
                      >
                        <table
                          v-if="model[element.name].length === 0"
                          class="setting-list-view-table store-table width100"
                        >
                          <tbody>
                            <div v-if="model[element.name].length === 0">
                              <div style="text-align:center">
                                <div>
                                  <img
                                    src="~statics/noData-light.png"
                                    width="100"
                                    height="100"
                                  />
                                </div>
                                <el-button
                                  @click="
                                    addVendorContactEntry(model[element.name])
                                  "
                                  class="btnsize txt pointer fc-btn-green-medium-fill"
                                  >{{
                                    $t('common.header.add_contact')
                                  }}</el-button
                                >
                              </div>
                            </div>
                          </tbody>
                        </table>
                        <table
                          v-else
                          class="setting-list-view-table store-table"
                        >
                          <thead class="setup-dialog-thead">
                            <tr>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.products.name') }}
                              </th>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.header.phone') }}
                              </th>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.header.email') }}
                              </th>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.header.portal_access') }}
                              </th>
                              <th></th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr
                              v-for="(field, index) in model[element.name]"
                              :key="index"
                              class="visibility-visible-actions"
                            >
                              <td class="module-builder-td">
                                <el-input
                                  :placeholder="$t('common.products.name')"
                                  v-model="model[element.name][index].name"
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td class="pL20">
                                <el-input
                                  :placeholder="$t('common.header.phone')"
                                  v-model="model[element.name][index].phone"
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td class="pL20">
                                <el-input
                                  :placeholder="$t('common.header.email')"
                                  v-model="model[element.name][index].email"
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td class="pL20">
                                <el-switch
                                  v-model="
                                    model[element.name][index]
                                      .isPortalAccessNeeded
                                  "
                                  class="pL20"
                                ></el-switch>
                              </td>
                              <td>
                                <div
                                  class="visibility-hide-actions export-dropdown-menu"
                                >
                                  <span
                                    @click="
                                      addVendorContactEntry(model[element.name])
                                    "
                                    :title="$t('common._common.add')"
                                    v-tippy
                                  >
                                    <img
                                      src="~assets/add-icon.svg"
                                      class="mR10 mT10"
                                    />
                                  </span>
                                  <span
                                    @click="
                                      removeEntryFromList(
                                        model[element.name],
                                        index
                                      )
                                    "
                                    :title="$t('common._common.remove')"
                                    v-tippy
                                  >
                                    <img
                                      src="~assets/remove-icon.svg"
                                      class="mT10"
                                    />
                                  </span>
                                </div>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                </template>

                <template
                  v-else-if="element.displayTypeEnum === 'EXTERNAL_ATTENDEES'"
                >
                  <div
                    class="fc-heading-border-width43 mT15"
                    style="margin-top: -10px;margin-bottom: 30px;"
                  ></div>
                  <div class="row full-layout-white fc-form-wo-task-con">
                    <div class="width100 pB50">
                      <div
                        v-if="element.displayTypeEnum === 'EXTERNAL_ATTENDEES'"
                        class="position-relative"
                      >
                        <table
                          v-if="model[element.name].length === 0"
                          class="setting-list-view-table store-table width100"
                        >
                          <tbody>
                            <div v-if="model[element.name].length === 0">
                              <div style="text-align:center">
                                <div>
                                  <img
                                    src="~statics/noData-light.png"
                                    width="100"
                                    height="100"
                                  />
                                </div>
                                <el-button
                                  @click="
                                    addExternalAttendeeEntry(
                                      model[element.name],
                                      index
                                    )
                                  "
                                  class="btnsize pointer txt fc-btn-green-medium-fill"
                                  >{{
                                    $t('common.header.add_external_attendee')
                                  }}</el-button
                                >
                              </div>
                            </div>
                          </tbody>
                        </table>
                        <table
                          v-else
                          class="setting-list-view-table store-table"
                        >
                          <thead class="setup-dialog-thead">
                            <tr>
                              <th class="setting-table-th setting-th-text">
                                {{ $t('common.products.name') }}
                              </th>
                              <th class="setting-table-th setting-th-text">
                                {{ $t('setup.setup_profile.email') }}
                              </th>
                              <th></th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr
                              v-for="(field, index) in model[element.name]"
                              :key="index"
                              class="visibility-visible-actions"
                            >
                              <td class="module-builder-td">
                                <el-input
                                  :placeholder="$t('common.products.name')"
                                  v-model="model[element.name][index].name"
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td class="module-builder-td">
                                <el-input
                                  :placeholder="$t('setup.setup_profile.email')"
                                  v-model="model[element.name][index].email"
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td>
                                <div
                                  class="visibility-hide-actions export-dropdown-menu"
                                >
                                  <span
                                    @click="
                                      addExternalAttendeeEntry(
                                        model[element.name],
                                        index
                                      )
                                    "
                                    :title="$t('common._common.add')"
                                    v-tippy
                                  >
                                    <img
                                      src="~assets/add-icon.svg"
                                      class="mR10 mT10"
                                    />
                                  </span>
                                  <span
                                    @click="
                                      removeEntryFromList(
                                        model[element.name],
                                        index
                                      )
                                    "
                                    :title="$t('common._common.remove')"
                                    v-tippy
                                  >
                                    <img
                                      src="~assets/remove-icon.svg"
                                      class="mT10"
                                    />
                                  </span>
                                </div>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                </template>
                <template v-else-if="element.displayTypeEnum === 'LINEITEMS'">
                  <div
                    class="fc-heading-border-width43 mT15"
                    style="margin-top: -10px;margin-bottom: 30px;"
                  ></div>
                  <div class="row full-layout-white fc-form-wo-task-con">
                    <div class="width100 pB50">
                      <div
                        v-if="element.displayTypeEnum === 'LINEITEMS'"
                        class="position-relative"
                      >
                        <table class="setting-list-view-table store-table">
                          <thead class="setup-dialog-thead">
                            <tr>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common._common.type') }}
                              </th>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.products.name') }}
                              </th>
                              <th
                                class="setting-table-th setting-th-text"
                                v-if="name !== 'rentalLeaseContractForm'"
                                style="width: 240px;"
                              >
                                {{ $t('common._common.quantity') }}
                              </th>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.header.unit_price') }}
                              </th>
                              <th></th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr
                              v-for="(field, index) in model[element.name]"
                              :key="index"
                              class="visibility-visible-actions"
                            >
                              <td class="module-builder-td">
                                <el-select
                                  v-model="
                                    model[element.name][index].inventoryType
                                  "
                                  class="fc-input-full-border-select2 width100"
                                  @change="
                                    loadTypes(
                                      model[element.name][index].inventoryType
                                    )
                                  "
                                >
                                  <el-option
                                    :label="$t('common.header.item')"
                                    key="1"
                                    :value="1"
                                  ></el-option>
                                  <el-option
                                    :label="$t('common.header.tool')"
                                    key="2"
                                    :value="2"
                                  ></el-option>
                                  <el-option
                                    :label="$t('common._common.others')"
                                    v-if="
                                      name === 'purchaseOrderForm' ||
                                        name === 'purchaseRequestForm'
                                    "
                                    key="4"
                                    :value="4"
                                  ></el-option>
                                </el-select>
                              </td>
                              <td class="module-builder-td">
                                <el-select
                                  filterable
                                  clearable
                                  @change="
                                    lineItemChangeActionItem(
                                      model[element.name][index]
                                    )
                                  "
                                  v-model="
                                    model[element.name][index].itemType.id
                                  "
                                  class="fc-input-full-border-select2 width100"
                                  v-if="
                                    parseInt(
                                      model[element.name][index].inventoryType
                                    ) === 1
                                  "
                                >
                                  <el-option-group key="1" label="Vendor Items">
                                    <el-option
                                      v-for="(vendorItem, index) in vendorItems"
                                      :key="index"
                                      :label="vendorItem.itemType.name"
                                      :value="vendorItem.itemType.id"
                                    ></el-option>
                                  </el-option-group>
                                  <el-option-group
                                    key="2"
                                    :label="
                                      vendorItems.length > 0
                                        ? 'Other Items'
                                        : ''
                                    "
                                  >
                                    <el-option
                                      v-for="(item, index) in nonVendorItems"
                                      :key="index"
                                      :label="item.name"
                                      :value="item.id"
                                    ></el-option>
                                  </el-option-group>
                                </el-select>
                                <el-select
                                  filterable
                                  clearable
                                  @change="
                                    lineItemChangeActionTool(
                                      model[element.name][index]
                                    )
                                  "
                                  v-model="
                                    model[element.name][index].toolType.id
                                  "
                                  class="fc-input-full-border-select2 width100"
                                  v-if="
                                    parseInt(
                                      model[element.name][index].inventoryType
                                    ) === 2
                                  "
                                >
                                  <el-option
                                    v-for="(tool, index) in toolTypes"
                                    :key="index"
                                    :label="tool.name"
                                    :value="tool.id"
                                  ></el-option>
                                </el-select>
                                <el-input
                                  :placeholder="$t('common.wo_report.remarks')"
                                  v-model="model[element.name][index].remarks"
                                  v-if="
                                    parseInt(
                                      model[element.name][index].inventoryType
                                    ) === 4
                                  "
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td
                                v-if="name !== 'rentalLeaseContractForm'"
                                class="module-builder-td"
                              >
                                <el-input
                                  :placeholder="$t('common._common.quantity')"
                                  :min="0"
                                  type="number"
                                  v-model="model[element.name][index].quantity"
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td class="module-builder-td">
                                <el-input
                                  :placeholder="$t('common.tabs.cost')"
                                  type="number"
                                  :min="0"
                                  v-model="model[element.name][index].unitPrice"
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td>
                                <div
                                  class="visibility-hide-actions export-dropdown-menu"
                                >
                                  <span
                                    @click="
                                      addItemEntry(
                                        model[element.name],
                                        index,
                                        element.displayTypeEnum
                                      )
                                    "
                                    :title="$t('common._common.add')"
                                    v-tippy
                                  >
                                    <img
                                      src="~assets/add-icon.svg"
                                      class="mR10 mT10"
                                    />
                                  </span>
                                  <span
                                    v-if="model[element.name].length > 1"
                                    @click="
                                      removeEntryFromList(
                                        model[element.name],
                                        index
                                      )
                                    "
                                    :title="$t('common._common.remove')"
                                    v-tippy
                                  >
                                    <img
                                      src="~assets/remove-icon.svg"
                                      class="mT10"
                                    />
                                  </span>
                                </div>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                </template>
                <template
                  v-else-if="
                    element.displayTypeEnum === 'INVREQUEST_LINE_ITEMS'
                  "
                >
                  <div
                    class="fc-heading-border-width43 mT15"
                    style="margin-top: -10px;margin-bottom: 30px;"
                  ></div>
                  <div class="row full-layout-white fc-form-wo-task-con">
                    <div
                      class="width100 pB50"
                      :class="
                        name === 'inventoryRequestWOForm'
                          ? 'margin-top-negative-50'
                          : ''
                      "
                    >
                      <div
                        v-if="
                          element.displayTypeEnum === 'INVREQUEST_LINE_ITEMS'
                        "
                        class="position-relative"
                      >
                        <table class="setting-list-view-table store-table">
                          <thead class="setup-dialog-thead">
                            <tr>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common._common.type') }}
                              </th>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.products.name') }}
                              </th>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common._common.quantity') }}
                              </th>
                              <th></th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr
                              v-for="(field, index) in model[element.name]"
                              :key="index"
                              class="visibility-visible-actions"
                            >
                              <td class="module-builder-td">
                                <el-select
                                  v-model="
                                    model[element.name][index].inventoryType
                                  "
                                  class="fc-input-full-border-select2 width100"
                                  @change="
                                    loadTypes(
                                      model[element.name][index].inventoryType
                                    )
                                  "
                                >
                                  <el-option
                                    :label="$t('common.header.item')"
                                    key="1"
                                    :value="1"
                                  ></el-option>
                                  <el-option
                                    :label="$t('common.header.tool')"
                                    key="2"
                                    :value="2"
                                  ></el-option>
                                </el-select>
                              </td>
                              <td class="module-builder-td">
                                <div
                                  v-if="
                                    parseInt(
                                      model[element.name][index].inventoryType
                                    ) === 1
                                  "
                                >
                                  <el-select
                                    filterable
                                    clearable
                                    v-model="
                                      model[element.name][index].itemType.id
                                    "
                                    class="fc-input-full-border-select2 width100"
                                  >
                                    <el-option
                                      v-for="(itemType, index) in itemTypes"
                                      :key="index"
                                      :label="itemType.name"
                                      :value="itemType.id"
                                    >
                                    </el-option>
                                  </el-select>
                                </div>
                                <div
                                  v-if="
                                    parseInt(
                                      model[element.name][index].inventoryType
                                    ) === 2
                                  "
                                >
                                  <el-select
                                    filterable
                                    clearable
                                    v-model="
                                      model[element.name][index].toolType.id
                                    "
                                    class="fc-input-full-border-select2 width100"
                                  >
                                    <el-option
                                      v-for="(toolType, index) in toolTypes"
                                      :key="index"
                                      :label="toolType.name"
                                      :value="toolType.id"
                                    >
                                    </el-option>
                                  </el-select>
                                </div>
                              </td>
                              <td class="module-builder-td">
                                <el-input
                                  :placeholder="$t('common._common.quantity')"
                                  :min="0"
                                  type="number"
                                  v-model="model[element.name][index].quantity"
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td>
                                <div
                                  class="visibility-hide-actions export-dropdown-menu"
                                >
                                  <span
                                    @click="
                                      addIRItemEntry(
                                        model[element.name],
                                        index,
                                        element.displayTypeEnum
                                      )
                                    "
                                    :title="$t('common._common.add')"
                                    v-tippy
                                  >
                                    <img
                                      src="~assets/add-icon.svg"
                                      class="mR10 mT10"
                                    />
                                  </span>
                                  <span
                                    v-if="model[element.name].length > 1"
                                    @click="
                                      removeEntryFromList(
                                        model[element.name],
                                        index
                                      )
                                    "
                                    :title="$t('common._common.remove')"
                                    v-tippy
                                  >
                                    <img
                                      src="~assets/remove-icon.svg"
                                      class="mT10"
                                    />
                                  </span>
                                </div>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                </template>
                <template
                  v-else-if="element.displayTypeEnum === 'LABOUR_LINE_ITEMS'"
                >
                  <div
                    class="fc-heading-border-width43 mT15"
                    style="margin-top: -10px;margin-bottom: 30px;"
                  ></div>
                  <div class="row full-layout-white fc-form-wo-task-con">
                    <div class="width100 pB50">
                      <div
                        v-if="element.displayTypeEnum === 'LABOUR_LINE_ITEMS'"
                        class="position-relative"
                      >
                        <table class="setting-list-view-table store-table">
                          <thead class="setup-dialog-thead">
                            <tr>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.header.labour') }}
                              </th>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.header.rate_hr') }}
                              </th>
                              <th></th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr
                              v-for="(field, index) in model[element.name]"
                              :key="index"
                              class="visibility-visible-actions"
                            >
                              <td class="module-builder-td">
                                <el-select
                                  filterable
                                  clearable
                                  v-model="model[element.name][index].labour.id"
                                  @change="
                                    loadRateForLabour(
                                      model[element.name][index]
                                    )
                                  "
                                  class="fc-input-full-border-select2 width100"
                                >
                                  <el-option
                                    v-for="(labour, index) in labours"
                                    :key="index"
                                    :label="labour.name"
                                    :value="labour.id"
                                  ></el-option>
                                </el-select>
                              </td>
                              <td class="module-builder-td">
                                <el-input
                                  :placeholder="$t('common.header.rate_hr')"
                                  type="number"
                                  v-model="model[element.name][index].cost"
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td>
                                <div
                                  class="visibility-hide-actions export-dropdown-menu"
                                >
                                  <span
                                    @click="
                                      addLabourEntry(
                                        model[element.name],
                                        index,
                                        element.displayTypeEnum
                                      )
                                    "
                                    :title="$t('common._common.add')"
                                    v-tippy
                                  >
                                    <img
                                      src="~assets/add-icon.svg"
                                      class="mR10 mT10"
                                    />
                                  </span>
                                  <span
                                    v-if="model[element.name].length > 1"
                                    @click="
                                      removeEntryFromList(
                                        model[element.name],
                                        index
                                      )
                                    "
                                    :title="$t('common._common.remove')"
                                    v-tippy
                                  >
                                    <img
                                      src="~assets/remove-icon.svg"
                                      class="mT10"
                                    />
                                  </span>
                                </div>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                </template>
                <template
                  v-else-if="element.displayTypeEnum === 'WARRANTY_LINE_ITEMS'"
                >
                  <div
                    class="fc-heading-border-width43 mT15"
                    style="margin-top: -10px;margin-bottom: 30px;"
                  ></div>
                  <div class="row full-layout-white fc-form-wo-task-con">
                    <div class="width100 pB50">
                      <div
                        v-if="element.displayTypeEnum === 'WARRANTY_LINE_ITEMS'"
                        class="position-relative"
                      >
                        <table class="setting-list-view-table store-table">
                          <thead class="setup-dialog-thead">
                            <tr>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common._common.service') }}
                              </th>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.header.item_coverage') }}
                              </th>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.header.tool_coverage') }}
                              </th>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.header.labor_coverage') }}
                              </th>
                              <th></th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr
                              v-for="(field, index) in model[element.name]"
                              :key="index"
                              class="visibility-visible-actions"
                            >
                              <td class="module-builder-td">
                                <el-select
                                  filterable
                                  clearable
                                  v-model="
                                    model[element.name][index].service.id
                                  "
                                  class="fc-input-full-border-select2 width100"
                                >
                                  <el-option
                                    v-for="(service, index) in services"
                                    :key="index"
                                    :label="service.name"
                                    :value="service.id"
                                  ></el-option>
                                </el-select>
                              </td>
                              <td class="module-builder-td">
                                <el-input
                                  :placeholder="
                                    $t('common.header.item_coverage')
                                  "
                                  type="number"
                                  :max="100"
                                  :min="0"
                                  v-model="
                                    model[element.name][index].itemCoverage
                                  "
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td class="module-builder-td">
                                <el-input
                                  :placeholder="
                                    $t('common.header.tool_coverage')
                                  "
                                  type="number"
                                  :max="100"
                                  :min="0"
                                  v-model="
                                    model[element.name][index].toolCoverage
                                  "
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td class="module-builder-td">
                                <el-input
                                  :placeholder="
                                    $t('common.header.labor_coverage')
                                  "
                                  type="number"
                                  :max="100"
                                  :min="0"
                                  v-model="
                                    model[element.name][index].labourCoverage
                                  "
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td>
                                <div
                                  class="visibility-hide-actions export-dropdown-menu"
                                >
                                  <span
                                    @click="
                                      addWarrantyLineEntry(
                                        model[element.name],
                                        index,
                                        element.displayTypeEnum
                                      )
                                    "
                                    :title="$t('common._common.add')"
                                    v-tippy
                                  >
                                    <img
                                      src="~assets/add-icon.svg"
                                      class="mR10 mT10"
                                    />
                                  </span>
                                  <span
                                    v-if="model[element.name].length > 1"
                                    @click="
                                      removeEntryFromList(
                                        model[element.name],
                                        index
                                      )
                                    "
                                    :title="$t('common._common.remove')"
                                    v-tippy
                                  >
                                    <img
                                      src="~assets/remove-icon.svg"
                                      class="mT10"
                                    />
                                  </span>
                                </div>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                </template>
                <template
                  v-else-if="element.displayTypeEnum === 'VISITOR_INVITEES'"
                >
                  <div
                    class="fc-heading-border-width43 mT15"
                    style="margin-top: -10px;margin-bottom: 30px;"
                  ></div>
                  <div class="row full-layout-white fc-form-wo-task-con">
                    <div class="width100 pB50">
                      <div
                        v-if="element.displayTypeEnum === 'VISITOR_INVITEES'"
                        class="position-relative"
                      >
                        <table class="setting-list-view-table store-table">
                          <thead class="setup-dialog-thead">
                            <tr>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.products.visitor') }}
                              </th>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.header.email') }}
                              </th>
                              <th
                                class="setting-table-th setting-th-text"
                                style="width: 240px;"
                              >
                                {{ $t('common.header.phone') }}
                              </th>
                              <th></th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr
                              v-for="(field, index) in model[element.name]"
                              :key="index"
                              class="visibility-visible-actions"
                            >
                              <td class="module-builder-td">
                                <el-select
                                  @change="
                                    fillVisitorObject(
                                      model[element.name][index]
                                    )
                                  "
                                  filterable
                                  clearable
                                  allow-create
                                  v-model="model[element.name][index].id"
                                  class="fc-input-full-border-select2 width100"
                                >
                                  <el-option
                                    v-for="(visitor, index) in visitors"
                                    :key="index"
                                    :label="visitor.name"
                                    :value="visitor.id"
                                  ></el-option>
                                </el-select>
                              </td>
                              <td class="module-builder-td">
                                <el-input
                                  placeholder="Email"
                                  :readonly="
                                    !isSelectCreateNew(
                                      model[element.name][index].id
                                    )
                                  "
                                  v-model="model[element.name][index].email"
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td class="module-builder-td">
                                <el-input
                                  placeholder="Phone"
                                  :readonly="
                                    !isSelectCreateNew(
                                      model[element.name][index].id
                                    )
                                  "
                                  v-model="model[element.name][index].phone"
                                  class="fc-input-full-border-select2 duration-input"
                                ></el-input>
                              </td>
                              <td>
                                <div
                                  class="visibility-hide-actions export-dropdown-menu"
                                >
                                  <span
                                    @click="
                                      addVisitorInviteesEntry(
                                        model[element.name],
                                        index,
                                        element.displayTypeEnum
                                      )
                                    "
                                    :title="$t('common._common.add')"
                                    v-tippy
                                  >
                                    <img
                                      src="~assets/add-icon.svg"
                                      class="mR10 mT10"
                                    />
                                  </span>
                                  <span
                                    v-if="model[element.name].length > 1"
                                    @click="
                                      removeEntryFromList(
                                        model[element.name],
                                        index
                                      )
                                    "
                                    :title="$t('common._common.remove')"
                                    v-tippy
                                  >
                                    <img
                                      src="~assets/remove-icon.svg"
                                      class="mT10"
                                    />
                                  </span>
                                </div>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                </template>
                <template
                  v-else-if="element.displayTypeEnum === 'SCHEDULER_INFO'"
                >
                  <div
                    class="fc-heading-border-width43 mT15"
                    style="margin-top: -10px;margin-bottom: 30px;"
                  ></div>
                  <div class="row full-layout-white fc-form-wo-task-con">
                    <div class="width100 pB50">
                      <el-row :span="24">
                        <el-col :span="12">
                          <el-checkbox
                            v-model="model[element.name].isCheckedPayment"
                            >{{
                              $t('common.header.schedule_payment')
                            }}</el-checkbox
                          >
                          <el-radio-group
                            v-if="model[element.name].isCheckedPayment"
                            @change="
                              paymentRadioGroupChangeActions(
                                model[element.name]
                              )
                            "
                            v-model="model[element.name].frequencyType"
                          >
                            <el-radio :label="1" class="width100 mT20">
                              {{ $t('common._common.daily') }}
                            </el-radio>
                            <el-radio :label="2" class="width100 mT20">
                              {{ $t('common._common.weekly') }}
                            </el-radio>
                            <el-radio :label="3" class="width100 mT20">
                              {{ $t('common._common.monthly') }}
                            </el-radio>
                            <el-radio :label="4" class="width100 mT20">
                              {{ $t('common._common.yearly') }}
                            </el-radio>
                          </el-radio-group>
                        </el-col>
                        <el-col
                          class="label-txt2"
                          v-if="model[element.name].isCheckedPayment"
                          :span="12"
                        >
                          <div v-if="model[element.name].frequencyType === 1">
                            {{ $t('common.wo_report.every') }}
                            <el-input
                              type="number"
                              :min="0"
                              v-model="model[element.name].paymentInterval"
                              class="width100px mT10 fc-input-label-txt"
                            ></el-input>
                            {{ $t('common.wo_report.days') }}
                            <div>
                              {{ $t('common.header.at_time') }}
                              <el-time-picker
                                format="HH:mm"
                                value-format="timestamp"
                                class="width100px mT10 fc-input-full-border2"
                                v-model="model[element.name].scheduleTime"
                              ></el-time-picker>
                            </div>
                          </div>
                          <div v-if="model[element.name].frequencyType === 2">
                            {{ $t('common.wo_report.every') }}
                            <el-input
                              type="number"
                              :min="0"
                              v-model="model[element.name].paymentInterval"
                              class="width100px mT10 fc-input-label-txt"
                            ></el-input>
                            {{ $t('common.wo_report.weeks') }}
                            <div>
                              {{ $t('common.header.at_time') }}
                              <el-time-picker
                                format="HH:mm"
                                class="width100px mT10 fc-input-full-border2"
                                value-format="timestamp"
                                v-model="model[element.name].scheduleTime"
                              ></el-time-picker>
                            </div>
                            <div>
                              {{ $t('common._common.on_day') }}
                              <el-select
                                v-model="model[element.name].scheduleDay"
                                class="width100px mT10 fc-input-full-border-select2"
                              >
                                <el-option
                                  v-for="(options, index) in model[element.name]
                                    .weekdays"
                                  :key="index"
                                  :label="options.label"
                                  :value="options.value"
                                ></el-option>
                              </el-select>
                            </div>
                          </div>
                          <div v-if="model[element.name].frequencyType === 3">
                            {{ $t('common.wo_report.every') }}
                            <el-input
                              type="number"
                              :min="0"
                              v-model="model[element.name].paymentInterval"
                              class="width100px mT10 fc-input-label-txt"
                            ></el-input>
                            {{ $t('common._common.months') }}
                            <div>
                              {{ $t('common.header.on') }}
                              <el-select
                                v-model="model[element.name].scheduleDay"
                                class="width100px mL35 mT10 fc-input-full-border-select2"
                              >
                                <el-option
                                  v-for="(options, index) in model[element.name]
                                    .days"
                                  :key="index"
                                  :label="options.label"
                                  :value="options.value"
                                ></el-option>
                              </el-select>
                            </div>
                            <div>
                              {{ $t('common.header.at_time') }}
                              <el-time-picker
                                format="HH:mm"
                                class="width100px mL5 mT10 fc-input-full-border2"
                                value-format="timestamp"
                                v-model="model[element.name].scheduleTime"
                              ></el-time-picker>
                            </div>
                          </div>
                          <div v-if="model[element.name].frequencyType === 4">
                            {{ $t('common.wo_report.every') }}
                            <el-input
                              type="number"
                              :min="0"
                              v-model="model[element.name].paymentInterval"
                              class="width100px mT10 fc-input-label-txt"
                            ></el-input>
                            {{ $t('common._common.years') }}
                            <div>
                              {{ $t('common.header.on') }}
                              <el-select
                                v-model="model[element.name].scheduleMonth"
                                class="width100px mL30 mT10 fc-input-full-border-select2"
                              >
                                <el-option
                                  v-for="(options, index) in model[element.name]
                                    .months"
                                  :key="index"
                                  :label="options.label"
                                  :value="options.value"
                                ></el-option>
                              </el-select>
                            </div>
                            <div>
                              {{ $t('common._common.on_day')
                              }}<el-select
                                v-model="model[element.name].scheduleDay"
                                class="width100px mL5 mT10 fc-input-full-border-select2"
                              >
                                <el-option
                                  v-for="(options, index) in model[element.name]
                                    .days"
                                  :key="index"
                                  :label="options.label"
                                  :value="options.value"
                                ></el-option>
                              </el-select>
                            </div>
                            <div>
                              {{ $t('common.header.at_time') }}
                              <el-time-picker
                                format="HH:mm"
                                class="width100px mT10 fc-input-full-border2"
                                value-format="timestamp"
                                v-model="model[element.name].scheduleTime"
                              ></el-time-picker>
                            </div>
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                  </div>
                </template>

                <template
                  v-else-if="element.displayTypeEnum === 'RECURRING_VISITOR'"
                >
                  <div class="row full-layout-white fc-form-wo-task-con mT10">
                    <div class="width100 pB50">
                      <el-row>
                        <el-col :span="24">
                          <p class="fc-input-label-txt">
                            {{ $t('common._common.type') }}
                          </p>
                          <el-radio-group
                            v-model="model[element.name].isRecurring"
                            @change="
                              resetIsRecurringValues(
                                model[element.name].isRecurring,
                                element.name
                              )
                            "
                            class="width100"
                          >
                            <el-radio :label="1" class="fc-radio-btn"
                              >{{ $t('common.wo_report.non_recurring') }}
                            </el-radio>
                            <el-radio :label="2" class="fc-radio-btn"
                              >{{ $t('common.wo_report.recurring') }}
                            </el-radio>
                          </el-radio-group>
                        </el-col>
                        <el-col
                          :span="24"
                          v-if="model[element.name].isRecurring === 1"
                          class="mT20"
                        >
                          <el-col :span="12">
                            <p class="fc-input-label-txt">
                              {{ $t('common.wo_report.expected_start_time') }}
                            </p>
                            <f-date-picker
                              v-model="model[element.name].expectedStartTime"
                              value-format="timestamp"
                              :type="'datetime'"
                              class="fc-input-full-border2 width575px"
                            ></f-date-picker>
                          </el-col>
                          <el-col :span="12">
                            <p class="fc-input-label-txt">
                              {{ $t('common.wo_report.expected_end_time') }}
                            </p>
                            <f-date-picker
                              v-model="model[element.name].expectedEndTime"
                              value-format="timestamp"
                              :type="'datetime'"
                              class="fc-input-full-border2 width575px"
                            ></f-date-picker>
                          </el-col>
                        </el-col>
                        <el-col
                          :span="24"
                          v-if="model[element.name].isRecurring === 2"
                          class="mT20"
                        >
                          <el-col :span="12">
                            <p class="fc-input-label-txt">
                              {{ $t('common.wo_report.start_date') }}
                            </p>
                            <f-date-picker
                              v-model="model[element.name].startDate"
                              value-format="timestamp"
                              :type="'date'"
                              class="fc-input-full-border2 width575px"
                            ></f-date-picker>
                          </el-col>
                          <el-col :span="12">
                            <p class="fc-input-label-txt">
                              {{ $t('common.wo_report.end_date') }}
                            </p>
                            <f-date-picker
                              v-model="model[element.name].endDate"
                              value-format="timestamp"
                              :type="'date'"
                              class="fc-input-full-border2 width575px"
                            ></f-date-picker>
                          </el-col>
                          <el-col :span="24" class="mT20">
                            <el-col :span="12">
                              <p class="fc-input-label-txt">
                                {{ $t('common.wo_report.start_time') }}
                              </p>
                              <el-time-picker
                                format="HH:mm"
                                v-model="model[element.name].startTime"
                                value-format="HH:mm"
                                :placeholder="$t('common.wo_report.start_time')"
                                class="width100 fc-input-full-border-select2"
                              ></el-time-picker>
                            </el-col>
                            <el-col :span="12">
                              <p class="fc-input-label-txt">
                                {{ $t('common.wo_report.end_time') }}
                              </p>
                              <el-time-picker
                                format="HH:mm"
                                v-model="model[element.name].endTime"
                                value-format="HH:mm"
                                :placeholder="$t('common.wo_report.end_time')"
                                class="width100 fc-input-full-border-select2"
                              ></el-time-picker>
                            </el-col>
                          </el-col>
                          <el-col :span="24" class="mT20">
                            <p class="fc-input-label-txt">
                              {{ $t('common.wo_report.allowed_days') }}
                            </p>
                            <el-checkbox
                              v-for="(week, wIndex) in model[element.name]
                                .weeks"
                              v-model="week.value"
                              :key="wIndex"
                              >{{ week.displayName }}</el-checkbox
                            >
                          </el-col>
                        </el-col>
                      </el-row>
                    </div>
                  </div>
                </template>

                <template
                  v-else-if="element.displayTypeEnum === 'ASSETMULTICHOOSER'"
                >
                  <div
                    class="fc-heading-border-width43 mT15"
                    style="margin-top: -10px;margin-bottom: 30px;"
                  ></div>
                  <div class="row full-layout-white fc-form-wo-task-con">
                    <div class="width100 pB50">
                      <div
                        v-if="model.assets.length > 0"
                        class="taskbar fc-pm-task-form-table-con"
                      >
                        <table class="fc-pm-form-table fc-pm-form-task-table">
                          <tbody>
                            <tr
                              class="visibility-visible-actions pointer table-hover"
                              v-for="(asset, index) in model.assets"
                              :key="index"
                            >
                              <td><div class="dot-green fL mL5"></div></td>
                              <td style="width: 500px;">#{{ asset.id }}</td>
                              <td style="width: 500px;">
                                {{ asset.name }}
                              </td>
                              <td style="width: 500px;">
                                {{
                                  getAssetCategory(asset.category.id)
                                    .displayName
                                }}
                              </td>
                              <td>
                                <div
                                  class="taskremoveicon visibility-hide-actions pR10"
                                  @click.stop="removeAsset(index)"
                                >
                                  <i
                                    class="el-icon-delete fc-delete f14 pointer visibility-hide-actions"
                                  ></i>
                                </div>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>

                      <div>
                        <div
                          v-if="model.assets.length <= 0"
                          style="text-align: center;"
                        >
                          <inline-svg
                            src="svgs/emptystate/task"
                            iconClass="icon text-center icon-xxxlg"
                          ></inline-svg>
                        </div>
                        <div
                          v-if="model.assets.length <= 0"
                          class="boldtext line-height0"
                        >
                          {{
                            $t('common._common.select_asset_associated_tenant')
                          }}
                        </div>
                        <div
                          v-if="model.assets.length <= 0"
                          style="text-align:center;margin-top: 10px;"
                        >
                          <el-button
                            style="text-align:center"
                            @click="utilityChooserVisibility = true"
                            class="fc-btn-green-medium-fill"
                            >{{
                              $t('common.header.associate_assets')
                            }}</el-button
                          >
                        </div>
                        <div v-else style="margin-top: 10px;">
                          <el-button
                            style="text-align:center"
                            @click="utilityChooserVisibility = true"
                            class="fc-btn-green-medium-fill"
                            >{{
                              $t('common.header.associate_assets')
                            }}</el-button
                          >
                        </div>
                      </div>
                    </div>
                  </div>
                </template>
                <space-asset-multi-chooser
                  v-if="utilityChooserVisibility"
                  @associate="associateAssets"
                  :resourceType="[2, 3, 4]"
                  :disable="true"
                  :visibility.sync="utilityChooserVisibility"
                  :initialValues="energyUtilityData"
                  :filter="filter"
                  :showAsset="true"
                  :hideBanner="true"
                ></space-asset-multi-chooser>
                <template v-else-if="element.displayTypeEnum === 'TASKS'">
                  <div
                    class="fc-heading-border-width43 mT15"
                    style="margin-top: -10px;margin-bottom: 30px;"
                  ></div>
                  <div
                    id="task"
                    class="row full-layout-white fc-form-wo-task-con"
                  >
                    <div class="width100 pB50">
                      <div
                        v-if="
                          model[element.name].length === 1 &&
                            model[element.name][0].section === 'default' &&
                            model[element.name][0].tasks.length === 0
                        "
                      >
                        <div style="text-align: center;">
                          <inline-svg
                            src="svgs/emptystate/task"
                            iconClass="icon text-center icon-xxllg vertical-middle"
                          ></inline-svg>
                        </div>
                        <div class="nowo-label">
                          <!-- {{ $t('maintenance._workorder.task_body_text') }} -->
                        </div>
                        <div style="text-align:center">
                          <el-button
                            @click="addNewTask()"
                            class="btnsize txt fc-btn-green-medium-fill"
                            >{{
                              $t('maintenance._workorder.add_task')
                            }}</el-button
                          >
                          <el-button
                            @click="addNewSection()"
                            class="btnsize txt taskbutton fc-btn-green-medium-border"
                            >{{
                              $t('maintenance._workorder.add_section')
                            }}</el-button
                          >
                        </div>
                      </div>
                      <draggable
                        :options="{ draggable: moveIt ? '.asf' : '' }"
                        :element="'div'"
                        :list="model[element.name]"
                      >
                        <div
                          v-for="(section, sectionIdx) in model[element.name]"
                          class="wo-task-section asf"
                          :key="sectionIdx"
                        >
                          <div
                            v-if="section.section !== 'default'"
                            class="tasksection visibility-visible-actions pT10"
                            @click="selectedTaskSectionIdx = sectionIdx"
                            style="height: 43px;"
                          >
                            <el-input
                              class="flLeft task-input-bold task-input-untitled"
                              style="width:100%;font-weight: bold;"
                              v-model="section.section"
                              type="text"
                              placeholder=""
                            ></el-input>
                            <div
                              class="flLeft sectionremoveicon visibility-hide-actions"
                              style="padding-top: 6px;"
                              @click.stop="removeTaskSection(sectionIdx)"
                            >
                              <i
                                class="el-icon-delete fc-delete f14 pointer"
                                style="position: absolute;right: 0;"
                              ></i>
                            </div>
                            <div class="clearboth"></div>
                          </div>
                          <draggable
                            :options="{ draggable: moveIt ? '.asd' : '' }"
                            :element="'div'"
                            :list="section.tasks"
                          >
                            <div class="taskbar fc-pm-task-form-table-con">
                              <table
                                class="fc-pm-form-table fc-pm-form-task-table"
                              >
                                <tbody>
                                  <tr
                                    class="visibility-visible-actions pointer table-hover"
                                    v-bind:class="{
                                      selectedTask: selectedTask === task,
                                      notselectedTask: selectedTask !== task,
                                      selectedErrorTask:
                                        errorIndex === index &&
                                        sectionIdx === selectedTaskSectionIdx,
                                    }"
                                    @click="
                                      selectedTaskSectionIdx = sectionIdx
                                      selectedTask = task
                                      selectedTaskIdx = index
                                      showTaskDetails(task)
                                    "
                                    v-for="(task, index) in section.tasks"
                                    :key="index"
                                  >
                                    <td>
                                      <div class="dot-green fL mL5"></div>
                                    </td>
                                    <td style="width: 500px;">
                                      <el-input
                                        class="pL10 fc-input-full-border-select2"
                                        style="width:100%;"
                                        :ref="
                                          'task-' +
                                            section.section +
                                            '-' +
                                            index
                                        "
                                        v-model="task.subject"
                                        type="text"
                                        placeholder="Enter a task"
                                        @keyup.native.enter="
                                          onTaskEnter(
                                            $event,
                                            index,
                                            task.subject
                                          )
                                        "
                                        @paste.native="
                                          onTaskPaste(
                                            section.element,
                                            index,
                                            $event
                                          )
                                        "
                                      ></el-input>
                                    </td>
                                    <td>
                                      <div class="position-relative">
                                        <el-input
                                          class="fc-input-full-border-select-hover fc-input-full-border-select2 btn-border-hide fc-form-space-input pR0"
                                        >
                                          <el-button
                                            slot="append"
                                            icon="el-icon-search"
                                            @click="
                                              selectedTaskSectionIdx = sectionIdx
                                              selectedTaskIdx = index
                                              showTaskSpaceAssetChooser()
                                            "
                                            class="btn-border-hide visibility-hide-actions"
                                          ></el-button>
                                        </el-input>
                                        <div class="taskdesc wo-task-in">
                                          {{
                                            task.resource
                                              ? task.resource.name
                                              : ''
                                          }}
                                        </div>
                                      </div>
                                    </td>
                                    <td>
                                      <el-button
                                        @click="showTaskSettings = true"
                                        class="mT0 btn-border-hide visibility-hide-actions"
                                        style="width: 20px;"
                                      >
                                        <img
                                          src="~assets/settings-grey.svg"
                                          height="16px"
                                          width="16px"
                                        />
                                      </el-button>
                                    </td>
                                    <td>
                                      <div
                                        class="taskremoveicon visibility-hide-actions pR10"
                                        @click.stop="
                                          removeTask(section.section, index)
                                        "
                                      >
                                        <i
                                          class="el-icon-delete fc-delete f14 pointer visibility-hide-actions"
                                        ></i>
                                      </div>
                                    </td>
                                  </tr>
                                </tbody>
                              </table>
                            </div>
                          </draggable>
                          <task-space-asset-chooser
                            @associate="associateTaskSpace"
                            :visibility.sync="taskSpaceChooserVisibility"
                            :quickSearchQuery="taskQuickSearchQuery"
                            :filter="filter"
                          ></task-space-asset-chooser>
                        </div>
                      </draggable>
                      <div
                        class="pT20"
                        v-if="
                          !(
                            model[element.name].length === 1 &&
                            model[element.name][0].section === 'default' &&
                            model[element.name][0].tasks.length === 0
                          )
                        "
                      >
                        <el-button
                          @click="addNewTask()"
                          class="fc-btn-green-medium-fill"
                          >{{
                            $t('maintenance._workorder.add_task')
                          }}</el-button
                        >
                        <el-button
                          @click="addNewSection()"
                          class="fc-btn-green-medium-border"
                          >{{
                            $t('maintenance._workorder.add_section')
                          }}</el-button
                        >
                      </div>
                    </div>
                  </div>
                  <!-- reading field start -->
                  <el-dialog
                    :title="$t('common._common.task_details')"
                    :visible.sync="showTaskSettings"
                    width="40%"
                    class="fc-dialog-center-container taskdialog2"
                  >
                    <div class="task-container-scroll2">
                      <el-row align="middle">
                        <el-col :span="24">
                          <div class="fc-dark-grey-txt14">
                            {{ $t('maintenance._workorder.description')
                            }}<el-input
                              v-model="selectedTask.description"
                              class="fc-input-full-border-textarea"
                              type="textarea"
                              autoComplete="off"
                              :rows="1"
                              resize="none"
                              placeholder="Enter Description"
                              :autosize="{ minRows: 5, maxRows: 5 }"
                            />
                          </div>
                        </el-col>
                      </el-row>
                      <div class="mB10">
                        <el-checkbox
                          v-model="selectedTask.attachmentRequired"
                          >{{ $t('maintenance._workorder.photo') }}</el-checkbox
                        >
                        <el-checkbox
                          v-model="selectedTask.enableInput"
                          @change="
                            selectedTask.inputType = !selectedTask.enableInput
                              ? 1
                              : selectedTask.inputType
                          "
                          >{{
                            $t('maintenance._workorder.enable_input')
                          }}</el-checkbox
                        >
                      </div>

                      <div v-if="selectedTask.enableInput">
                        <div class="pT10 fc-dark-grey-txt14">
                          {{ $t('maintenance._workorder.task_type') }}
                        </div>
                        <el-radio
                          @change="onSelectInput"
                          :disabled="
                            (!selectedTask.resource ||
                              selectedTask.resource.id <= 0) &&
                              (!model.resource ||
                                !model.resource.id ||
                                model.resource.id <= 0)
                          "
                          v-model="selectedTask.inputType"
                          class="fc-radio-btn pT15"
                          color="secondary"
                          label="2"
                          >{{ $t('maintenance._workorder.reading') }}
                          {{
                            (!selectedTask.resource ||
                              selectedTask.resource.id <= 0) &&
                            (!model.resource ||
                              !model.resource.id ||
                              model.resource.id <= 0)
                              ? $t('maintenance._workorder.select_asset_space')
                              : ''
                          }}</el-radio
                        >
                        <el-radio
                          @change="onSelectInput"
                          v-model="selectedTask.inputType"
                          class="fc-radio-btn pT15 "
                          color="secondary"
                          label="3"
                          >{{ $t('maintenance._workorder.text') }}</el-radio
                        >
                        <el-radio
                          @change="onSelectInput"
                          v-model="selectedTask.inputType"
                          class="fc-radio-btn pT15"
                          color="secondary"
                          label="4"
                          >{{ $t('maintenance._workorder.number') }}</el-radio
                        >
                        <el-radio
                          @change="onSelectInput"
                          v-model="selectedTask.inputType"
                          class="fc-radio-btn pT15"
                          color="secondary"
                          label="5"
                          >{{ $t('maintenance._workorder.option') }}</el-radio
                        >
                        <el-radio
                          v-show="false"
                          @change="onSelectInput"
                          v-model="selectedTask.inputType"
                          class="fc-radio-btn pT15"
                          color="secondary"
                          label="6"
                          >{{ $t('maintenance._workorder.checkbox') }}</el-radio
                        >
                      </div>
                      <div v-if="selectedTask.inputType === '2'">
                        <div class="fc-dark-grey-txt14">
                          {{ $t('maintenance._workorder.reading_field') }}
                        </div>
                        <div>
                          <el-select
                            v-model="selectedTask.readingFieldId"
                            filterable
                            style="width:100%"
                            class="form-item"
                            placeholder=" "
                          >
                            <el-option
                              v-for="readingField in taskReadingFields"
                              :key="readingField.id"
                              :label="readingField.displayName"
                              :value="readingField.id"
                            ></el-option>
                          </el-select>
                        </div>
                      </div>
                      <div v-if="selectedTask.inputType === '4'">
                        <div class="fc-dark-grey-txt14">
                          {{ $t('maintenance.pm_list.validation') }}
                        </div>
                        <el-select
                          v-model="selectedTask.validation"
                          style="width:100%"
                          class="form-item fc-input-full-border-select2"
                          placeholder=""
                        >
                          <el-option
                            :label="$t('common.wo_report.none')"
                            key="none"
                            value="none"
                          ></el-option>
                          <el-option
                            :label="$t('common._common.incremental')"
                            key="incremental"
                            value="incremental"
                          ></el-option>
                          <el-option
                            :label="$t('common._common.decremental')"
                            key="decremental"
                            value="decremental"
                          ></el-option>
                          <el-option
                            :label="$t('common._common.safe_limit')"
                            key="safeLimit"
                            value="safeLimit"
                          ></el-option>
                        </el-select>
                        <div
                          class="inline-space-between"
                          v-if="selectedTask.validation === 'safeLimit'"
                        >
                          <div>
                            <div class="fc-dark-grey-txt14 mT10">
                              {{ $t('maintenance.pm_list.minimum_value') }}
                            </div>
                            <el-input
                              type="number"
                              v-model="selectedTask.minSafeLimit"
                              class="fc-input-full-border2"
                            ></el-input>
                          </div>
                          <div>
                            <div class="fc-dark-grey-txt14">
                              {{ $t('maintenance.pm_list.max_value') }}
                            </div>
                            <el-input
                              type="number"
                              v-model="selectedTask.maxSafeLimit"
                              class="fc-input-full-border2"
                            ></el-input>
                          </div>
                        </div>
                      </div>
                      <div
                        v-if="
                          selectedTask.inputType === '5' ||
                            selectedTask.inputType === '6'
                        "
                        class="wo-options"
                      >
                        <div class="pT20 fc-dark-grey-txt14">
                          {{ $t('maintenance._workorder.options') }}
                        </div>
                        <div
                          :key="option"
                          v-for="(option, index) in selectedTask.options"
                          style="padding-bottom:10px;"
                          class="taskoption visibility-visible-actions"
                        >
                          <div v-if="selectedTask.inputType === '5'"></div>
                          <div
                            v-if="selectedTask.inputType === '6'"
                            style="width: 14px;height: 14px;margin-top: 9px;margin-right: 7px; border-radius: 3px; border: 1px solid #c4d1dd;"
                            class="flLeft"
                          ></div>
                          <div
                            class="flLeft"
                            style="padding-left:3px; width:200px;"
                          >
                            <el-input
                              type="text"
                              v-model="option.name"
                              placeholder=""
                              class="fc-input-full-border2 width200px task-option-input"
                            >
                              <template slot="prepend">{{
                                index + 1
                              }}</template>
                            </el-input>
                          </div>
                          <div
                            class="flLeft optionremoveicon"
                            @click="remove(selectedTask.options, index)"
                            v-show="selectedTask.options.length > 2"
                          >
                            <i
                              class="el-icon-delete visibility-hide-actions pointer mL10"
                            ></i>
                          </div>
                          <div class="clearboth"></div>
                        </div>
                        <div
                          class="add-option pointer"
                          @click="addTaskOption(selectedTask)"
                        >
                          {{ $t('maintenance._workorder.add_option') }}
                        </div>
                      </div>
                    </div>
                    <div class="modal-dialog-footer">
                      <el-button
                        class="modal-btn-cancel"
                        @click="showTaskSettings = false"
                        >{{ $t('common._common.cancel') }}</el-button
                      >
                      <el-button
                        type="primary"
                        class="modal-btn-save"
                        @click="saveTaskSettings"
                        >{{ $t('common._common._save') }}</el-button
                      >
                    </div>
                  </el-dialog>
                  <!-- reading field end -->
                </template>
                <el-button
                  @click="showCreateDialog(element)"
                  type="text"
                  v-if="element.allowCreate"
                  title="Add New"
                  v-tippy
                  :class="
                    labelPosition === 'left'
                      ? span(labelPosition, element.span) === 24
                        ? 'purchase-item-type-plus-btn-left-span'
                        : 'purchase-item-type-plus-btn'
                      : 'purchase-item-type-plus-btn-top'
                  "
                  ><i class="el-icon-plus"></i
                ></el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <long-description-editor
          :content="model.longDesc"
          :visibility.sync="addLongDescriptionVisibility"
          @saved="data => saveLongDescriptionData(data)"
          v-if="addLongDescriptionVisibility"
        ></long-description-editor>
      </el-form>
    </div>
  </div>
</template>
<script>
import countries from 'util/data/countries'
import FAssignment from '@/FAssignment'
import WoSpaceAssetChooser from '@/SpaceAssetChooser'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import { mapState, mapGetters } from 'vuex'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import draggable from 'vuedraggable'
import { isEmpty } from '@facilio/utils/validation'
import LongDescriptionEditor from '@/LongDescriptionEditor'
import { getFieldOptions } from 'util/picklist'
import {
  getFilteredItemList,
  getFilteredToolList,
} from 'pages/Inventory/InventoryUtil'
import { getBaseURL } from 'util/baseUrl'
import { prettyBytes } from '@facilio/utils/filters'
import FLookupField from '@/forms/FLookupField'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import TeamStaffMixin from '@/mixins/TeamStaffMixin'
import { mapStateWithLogging } from 'store/utils/log-map-state'

const enumFieldsList = [
  'costType',
  'unitType',
  'status',
  'rentalLeaseContractType',
  'durationType',
  'deviceType',
  'contactType',
  'workType',
  'tenantType',
  'permitType',
]
export default {
  props: {
    name: {
      type: String,
    },
    categoryId: [],
    reset: {
      type: Boolean,
    },
    emitForm: {
      type: Boolean,
      required: true,
    },
    editObj: {
      type: Object,
    },
    isServicePortal: {
      type: Boolean,
    },
    formObj: null,
    disableForm: {
      type: Boolean,
    },
    formFields: null,
    siteScope: Array,
  },
  mixins: [TeamStaffMixin],
  components: {
    WoSpaceAssetChooser,
    FAssignment,
    draggable,
    'space-asset-multi-chooser': SpaceAssetMultiChooser,
    'task-space-asset-chooser': WoSpaceAssetChooser,
    LongDescriptionEditor,
    FDatePicker,
    FLookupField,
    FLookupFieldWizard,
  },
  data() {
    return {
      canShowLookupWizard: false,
      confirmRequest: {
        assignedTo: {
          id: '',
        },
        assignmentGroup: {
          id: '',
        },
        resource: null,
        assets: [],
        spaces: [],
      },
      loading: true,
      mode: false,
      fields: [],
      title: '',
      labelPosition: 'left',
      model: null,
      rules: {},
      dataTypeMapping: {},
      pickListData: {},
      woSpaceAssetvisibility: {},
      mandatoryFields: {},
      visibility: false,
      quickSearchQuery: '',
      taskQuickSearchQuery: '',
      spaceAssetDisplayName: '',
      sections: [],
      lastAddedSectionCount: 1,
      selectedTaskSectionIdx: 0,
      selectedTaskIdx: 0,
      showAddTask: false,
      showAddTrigger: false,
      taskSpaceChooserVisibility: false,
      chooserVisibility: false,
      utilityChooserVisibility: false,
      selectedTask: {
        validation: 'none',
        options: [],
        inputType: 1,
        enableInput: false,
        attachmentRequired: false,
        readingFieldId: null,
      },
      newTask: {
        subject: '',
        description: '',
        resource: {
          id: -1,
        },
        readingFieldId: null,
        options: [],
        inputType: 1,
        sectionId: -1,
        uniqueId: -1,
        enableInput: false,
        attachmentRequired: false,
        inputValidation: null,
        inputValidationRuleId: -1,
        safeLimitRuleId: -1,
        minSafeLimit: null,
        maxSafeLimit: null,
        validation: 'none',
      },
      moveIt: true,
      dialogVisible: false,
      showTaskSettings: false,
      taskReadingFields: [],
      errorIndex: null,
      metricsUnits: {},
      countryList: countries,
      imageUploadUrl: null,
      utilityAssets: [],
      selectedResourceLabel: null,
      selectedSiteLabel: null,
      selectedAssetLabel: null,
      selectedResources: [],
      selectedAssets: [],
      items: [],
      itemTypes: [],
      tools: [],
      toolTypes: [],
      vendors: [],
      assets: [],
      storeRooms: [],
      vendorItems: [],
      nonVendorItems: [],
      labours: [],
      services: [],
      addLongDescriptionVisibility: false,
      visitors: [],
      formId: -1,
      disableWorkPermitVendorFields: true,
      vendorContacts: [],
      vendorOpts: {},
      actualForms: null,
    }
  },
  async created() {
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadGroups')
    this.loadAssetPickListData()
    this.loadPickList('vendors', this.vendorOpts).then(() => {
      let options = this.vendorOpts.options
      for (let i = 0; i < options.length; i++) {
        options[i].value = '' + options[i].value
      }
    })
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      locations: state => state.locations,
      ticketstatus: state => state.ticketStatus.workorder,
      ticketpriority: state => state.ticketPriority,
      ticketcategory: state => state.ticketCategory,
      sites: state => state.site,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
    ...mapGetters([
      'getTicketCategoryPickList',
      'getTicketStatusPickList',
      'getTicketTypePickList',
      'getTicketPriorityPickList',
      'getSpaceCategoryPickList',
      'getAssetCategory',
    ]),
    getVendorField() {
      let { fields } = this
      let vendorField
      fields.forEach(field => {
        let currentField = field[0]
        if (currentField.parent === 'vendor') vendorField = field[0]
      })
      return vendorField
    },
    showUsersWithNoScope() {
      return this.name === 'multi_web_pm'
    },
    reservableFilter() {
      let temp = {}
      temp['reservable'] = {
        value: ['true'],
        operator: 'is',
      }
      return temp
    },
    spaceMap() {
      return this.spaces.reduce((arr, space) => {
        arr[space.id] = space.name
        return arr
      }, {})
    },
    ticketStatusMap() {
      return this.getTicketStatusPickList('workorder')
    },
    tickettype() {
      return this.getTicketTypePickList()
    },
    spacecategory() {
      return this.getSpaceCategoryPickList()
    },
    filter() {
      let filter = {}
      if (this.model.siteId && this.model.siteId) {
        filter.site = Number(this.model.siteId)
      } else if (this.model.site && this.model.site.id) {
        filter.site = Number(this.model.site.id)
      }
      return filter
    },
    resourceData() {
      return {
        isIncludeResource: true,
        selectedResources: this.model.spaces.map(resource => ({
          id: resource && typeof resource === 'object' ? resource.id : resource,
        })),
      }
    },
    siteresourceData() {
      return {
        selectedResources:
          this.model.sites && this.model.sites.map(space => ({ id: space })),
      }
    },
    energyUtilityData() {
      return {
        assetCategory: this.categoryId,
        isIncludeResource: true,
        selectedResources: this.model.assets.map(resource => ({
          id: resource && typeof resource === 'object' ? resource.id : resource,
        })),
      }
    },
    currentSiteId() {
      return Number(this.$cookie.get('fc.currentSite'))
    },
    rotatingItemList() {
      return this.items.filter(item =>
        this.$getProperty(item, 'itemType.isRotating')
      )
    },
    rotatingToolList() {
      return this.tools.filter(tool =>
        this.$getProperty(tool, 'toolType.isRotating')
      )
    },
    defaultFields() {
      let { actualForms } = this
      let { fields } = actualForms || {}
      let defaultFields = []
      if (!isEmpty(fields)) {
        fields.forEach(field => {
          let { field: fieldObj, name } = field || {}
          if (!isEmpty(fieldObj)) {
            if (fieldObj.default) defaultFields.push(name)
          } else {
            defaultFields.push(name)
          }
        })
      }
      return defaultFields
    },
  },
  mounted() {
    this.initForm(this.formObj)
  },
  watch: {
    siteScope(newVal) {
      if (!newVal || !newVal.length) {
        return
      }

      if (!this.groups) {
        return
      }

      if (!this.fields) {
        return
      }

      let filtered = []
      for (let k = 0; k < this.groups.length; k++) {
        let group = this.groups[k]
        if (!group.siteId || group.siteId < 0) {
          let option = {}
          option.label = group.name
          option.value = String(group.groupId)
          option.siteId = group.siteId
          filtered.push(option)
        } else {
          if (this.siteScope.includes(group.siteId)) {
            let option = {}
            option.label = group.name
            option.value = String(group.groupId)
            option.siteId = group.siteId
            filtered.push(option)
          }
        }
      }

      for (let i = 0; i < this.fields.length; i++) {
        let field = this.fields[i]
        if (!field) {
          continue
        }

        for (let j = 0; j < field.length; j++) {
          let element = field[j]
          if (!element) {
            continue
          }

          if (
            element.lookupModuleName &&
            element.lookupModuleName === 'groups'
          ) {
            element.options = filtered
          }
        }
      }
    },
    loading(newVal, oldVal) {
      if (newVal !== oldVal) {
        if (!this.loading) {
          this.$emit('loaded')
        }
      }
    },
    editObj(newVal, oldVal) {
      let self = this
      if (newVal !== oldVal) {
        let obj = this.$helpers.cloneFromSchema(this.model, newVal)
        Object.keys(obj)
          .filter(key => key in this.model)
          .forEach(key => {
            if (obj[key] && key === 'spaces') {
              self.selectedResourceLabel = self.resourceLabel(obj[key])
              this.model[key] = obj[key]
            } else if (obj[key] && key === 'space') {
              self.spaceAssetDisplayName = self.model[key].name
              self.model[key] = obj[key]
            } else if (obj[key] && key === 'assets') {
              this.model[key] = obj[key]
            } else if (obj[key] && key === 'sites') {
              self.selectedSiteLabel = self.siteLabel(obj[key])
              this.model[key] = obj[key]
            } else if (
              obj[key] &&
              key !== 'assignmentGroup' &&
              key !== 'assignedTo' &&
              key !== 'owner'
            ) {
              this.model[key] = obj[key]
              let { defaultFields } = this
              if (
                this.model[key] instanceof Object &&
                this.model[key].hasOwnProperty('id') &&
                obj[key] instanceof Object &&
                obj[key].hasOwnProperty('id') &&
                obj[key].id &&
                key !== 'vendor' &&
                defaultFields.includes(key)
              ) {
                this.model[key].id = String(obj[key].id)
              }
            }
          })
      }
    },
    emitForm() {
      if (this.emitForm) {
        this.save()
      }
    },
    reset() {
      if (this.reset) {
        this.resetForm()
        this.$emit('update:reset', false)
      }
    },
    model: {
      handler: function() {
        if (this.model) {
          this.$emit('update:model', this.model)
        }
        if (this.model.resource && this.model.resource.name) {
          this.spaceAssetDisplayName = this.model.resource.name
        }
        if (this.model.spaces) {
          this.selectedResourceLabel = this.resourceLabel(this.model.spaces)
        }
        if (this.model.sites) {
          this.selectedSiteLabel = this.siteLabel(this.model.sites)
        }
        if (
          this.spaceAssetDisplayName === '' &&
          this.model.space &&
          this.name === 'reservationForm'
        ) {
          this.spaceAssetDisplayName = this.model.space.name
        }
      },
      deep: true,
    },
    formObj: {
      handler: function(newVal, oldVal) {
        this.initForm(newVal)
      },
    },
  },
  methods: {
    setLookupFieldValue(props) {
      let { field } = props || {}
      let { parent, name } = field || {}
      let id = this.$getProperty(field, 'selectedItems.0.value')
      this.model[parent][name] = id
    },
    getBaseURL() {
      return getBaseURL()
    },
    getSpaceName(id) {
      return id && this.spaceMap[id] ? this.spaceMap[id] : ''
    },
    span(labelPosition, currentSpan) {
      if (labelPosition === 'left') {
        return currentSpan === 1 ? 24 : 8
      }
      return currentSpan === 1 ? 24 : 12
    },
    resourceFilter() {
      return [3]
    },
    saveTaskSettings() {
      this.model.tasks[this.selectedTaskSectionIdx].tasks[
        this.selectedTaskIdx
      ] = this.$helpers.cloneObject(this.selectedTask)
      this.showTaskSettings = false
    },
    filterBySite(option) {
      let hasSite = this.model.hasOwnProperty('site')
      let hasSiteId = this.model.hasOwnProperty('siteId')
      if (
        option.hasOwnProperty('siteId') &&
        option.siteId &&
        option.siteId > -1 &&
        (hasSite || hasSiteId)
      ) {
        let siteId = hasSite ? this.model.site.id : this.model.siteId
        if (!(siteId && siteId > -1)) {
          return true
        }
        return Number(option.siteId) === Number(siteId)
      }
      return true
    },
    addAttachment() {},
    attachnewfile() {
      this.$refs.uploadData.open()
    },
    filesChange(elementName, selectedFiles) {
      for (let num in selectedFiles) {
        let fileEntry = {
          fileName: selectedFiles[num].name,
          fileSize: selectedFiles[num].size,
          prettyFileSize: prettyBytes(selectedFiles[num].size),
          contentType: selectedFiles[num].type,
          status: null,
          error: null,
          selectedFiles: selectedFiles[num],
        }
        this.model[elementName].attachments.push(fileEntry)
        this.model[elementName].uploadFiles.push(
          new File([selectedFiles[num]], selectedFiles[num].name)
        )
      }
    },
    logoChange(elementName, selectedFile) {
      let self = this
      let picReader = new FileReader()
      this.model[elementName].uploadLogo.push(
        new File([selectedFile[0]], selectedFile[0].name)
      )
      picReader.addEventListener('load', function(event) {
        self.model[elementName].logoImageUrl = event.target.result
      })
      picReader.readAsDataURL(selectedFile[0])
    },

    deleteAttachment(name, index) {
      if (this.model[name].attachments && this.model[name].uploadFiles) {
        this.model[name].attachments.splice(index, 1)
        this.model[name].uploadFiles.splice(index, 1)
      }
    },
    deleteLogo(name) {
      if (this.model[name].logo && this.model[name].uploadLogo) {
        this.model[name].uploadLogo = []
        this.model[name].logoImageUrl = ''
      }
    },
    initForm(formObj) {
      if (!this.name) {
        this.bindFieldsType(this.formFields)
      } else if (formObj) {
        this.bindFieldsType(formObj)
      } else {
        let url = null
        if (this.isServicePortal) {
          url = `/getFormMeta?formNames=${this.name}`
        } else {
          url = `/getFormMeta?formNames=${this.name}`
        }
        // eslint-disable-next-line @facilio/no-http
        this.$http
          .get(url)
          .then(response => {
            this.formId = response.data.forms[0].id
            this.bindFieldsType(response.data.forms[0])
            this.actualForms = response.data.forms[0]
          })
          .catch(() => {})
      }
    },
    showSpaceAssetChooser() {
      this.visibility = true
    },
    showTaskSpaceAssetChooser() {
      this.taskSpaceChooserVisibility = true
    },
    associateResource(selectedObj) {
      this.model.spaces = []
      if (selectedObj.resourceList && selectedObj.resourceList.length) {
        this.model.spaces = selectedObj.resourceList
      }
      this.selectedResourceLabel = this.resourceLabel(this.model.spaces)
      this.chooserVisibility = false
      this.$forceUpdate()
    },
    associateSite(selectedObj) {
      this.model.sites = []
      if (selectedObj.resourceList && selectedObj.resourceList.length) {
        this.model.sites = selectedObj.resourceList.map(resource => resource.id)
      }
      this.selectedSiteLabel = this.siteLabel(this.model.sites)
      this.chooserVisibility = false
      this.$forceUpdate()
    },
    associateAssets(selectedObj) {
      this.model.assets = []
      if (selectedObj.resourceList && selectedObj.resourceList.length) {
        this.model.assets = selectedObj.resourceList
        this.selectedAssetLabel = this.assetLabel(this.model.assets)
      }
      this.utilityChooserVisibility = false
      this.$forceUpdate()
    },
    removeAsset(index) {
      this.model.assets.splice(index, 1)
      this.$forceUpdate()
    },
    resourceLabel(selectedResourceObject) {
      if (selectedResourceObject) {
        let message
        let selectedCount = selectedResourceObject.length
        let categoryName = 'Space'
        if (selectedCount > 0) {
          message =
            selectedCount +
            ' ' +
            (selectedCount > 1 ? categoryName + 's' : categoryName)
        } else {
          message = ''
        }
        return message
      }
    },
    siteLabel(selectedResourceObject) {
      if (selectedResourceObject) {
        let message
        let selectedCount = selectedResourceObject.length
        let categoryName = 'Site'
        if (selectedCount > 0) {
          message =
            selectedCount +
            ' ' +
            (selectedCount > 1 ? categoryName + 's' : categoryName)
        } else {
          message = ''
        }
        return message
      }
    },
    assetLabel(selectedResourceObject) {
      if (selectedResourceObject) {
        let message
        let selectedCount = selectedResourceObject.length
        let categoryName = 'Asset'
        if (selectedCount > 0) {
          message =
            selectedCount +
            ' ' +
            (selectedCount > 1 ? categoryName + 's' : categoryName)
        } else {
          message = ''
        }
        return message
      }
    },
    associateTaskSpace(selectedObj) {
      this.taskSpaceChooserVisibility = false
      this.model.tasks[this.selectedTaskSectionIdx].tasks[
        this.selectedTaskIdx
      ].resource = selectedObj
      this.onSelectInput()
    },
    associateQr(selectedObj, fieldName) {
      this.model[fieldName || 'resource'] = { id: selectedObj.id }
      this.spaceAssetDisplayName = selectedObj.name
      this.visibility = false
      this.$emit('workflowresponse', selectedObj.id)
      this.$forceUpdate()
    },
    associate(selectedObj, fieldName) {
      this.model[fieldName || 'resource'] = { id: selectedObj.id }
      this.spaceAssetDisplayName = selectedObj.name
      this.visibility = false
      this.$forceUpdate()
    },
    resetForm() {
      // Temp handing for reset should work on props to reset all fields
      Object.keys(this.model).forEach(v => {
        if (
          this.model[v] instanceof Object &&
          this.model[v].hasOwnProperty('id')
        ) {
          this.model[v].id = null
        } else {
          this.model[v] = null
        }
        if (this.spaceAssetDisplayName) {
          this.spaceAssetDisplayName = ''
        }
      })
      this.$refs['newRecordForm'].resetFields()
    },
    async bindFieldsType(data) {
      this.title = data.displayName
      this.labelPosition = data.labelPositionVal
      this.fields = []
      let modelObj = {}
      let modifier = {}
      let promises = []
      this.loading = true
      if (this.isServicePortal) {
        let requesterPostion
        data.fields.forEach((d, i) => {
          if (d.name === 'requester') {
            requesterPostion = i
          }
        })
        if (requesterPostion > -1) {
          if (this.$portaluser) {
            data.fields.splice(requesterPostion, 1)
          } else {
            data.fields.splice(requesterPostion, 1, {
              displayTypeEnum: 'TEXTBOX',
              name: 'name',
              parent: 'requester',
              sequenceNumber: 1,
              displayName: 'Requester Name',
              span: 1,
              required: true,
              isRequester: true,
            })
            data.fields.splice(requesterPostion + 1, 0, {
              displayTypeEnum: 'TEXTBOX',
              name: 'email',
              parent: 'requester',
              sequenceNumber: 2,
              displayName: 'Requester Email',
              span: 1,
              required: true,
              isRequester: true,
            })
            this.reorderSequenceNumber(data.fields)
          }
        }
      }
      for (let i in data.fields) {
        let field = data.fields[i]
        if (!this.fields[field.sequenceNumber - 1]) {
          this.fields[field.sequenceNumber - 1] = []
        }
        if (field.span === 1) {
          this.fields[field.sequenceNumber - 1].push(field)
        } else if (field.span === 2) {
          if (this.fields[field.sequenceNumber - 1][0]) {
            this.fields[field.sequenceNumber - 1][0] = field
          } else {
            this.fields[field.sequenceNumber - 1].push(field)
          }
        } else if (field.span === 3) {
          if (this.fields[field.sequenceNumber - 1][1]) {
            this.fields[field.sequenceNumber - 1][1] = field
          } else {
            this.fields[field.sequenceNumber - 1].push(field)
          }
        }
      }
      for (let y in this.fields) {
        modifier[y] = []
        for (let k in this.fields[y]) {
          let field = this.fields[y][k]
          if (field.specialType === 'users') {
            field.options = []
            for (let uidx in this.users) {
              let user = this.users[uidx]
              let userOption = {}
              userOption.label = user.name + ' (' + user.email + ')'
              userOption.value = user.id
              field.options.push(userOption)
            }
            modifier[y].push([
              {
                displayTypeEnum: field.displayTypeEnum,
                name: 'id',
                parent: field.name,
                displayName: field.displayName,
                required: field.required,
                options: field.options,
                span: field.span,
                allowCreate: field.allowCreate,
                lookupModuleName: field.lookupModuleName,
                createFormName: field.createFormName,
              },
            ])
          } else if (field.specialType === 'groups') {
            field.options = []
            for (let gidx in this.groups) {
              let group = this.groups[gidx]
              let option = {}
              option.label = group.name
              option.value = group.groupId
              option.siteId = group.siteId
              if (
                !this.showUsersWithNoScope ||
                !group.siteId ||
                group.siteId < 0
              ) {
                field.options.push(option)
              }
              field.options.push(option)
            }
            modifier[y].push([
              {
                displayTypeEnum: field.displayTypeEnum,
                name: 'id',
                parent: field.name,
                displayName: field.displayName,
                required: field.required,
                options: field.options,
                span: field.span,
                allowCreate: field.allowCreate,
                lookupModuleName: field.lookupModuleName,
                createFormName: field.createFormName,
              },
            ])
          } else if (
            field.displayTypeEnum === 'LOOKUP_POPUP' &&
            field.lookupModuleName === 'location'
          ) {
            let isPromise = this.parseFieldObject(
              field,
              this.locations,
              modifier,
              y
            )
            if (isPromise) {
              promises.push(isPromise)
            }
          } else if (
            field.displayTypeEnum === 'LOOKUP_POPUP' &&
            field.lookupModuleName === 'basespace'
          ) {
            field.options = []
            if (this.spaces && this.spaces.length > 0) {
              for (let sidx in this.spaces) {
                let space = this.spaces[sidx]
                let option = {}
                option.label = space.name + ' (' + space.spaceTypeVal + ')'
                option.value = space.id
                field.options.push(option)
              }
              modifier[y].push([
                {
                  displayTypeEnum: field.displayTypeEnum,
                  name: 'id',
                  parent: field.name,
                  displayName: field.displayName,
                  required: field.required,
                  options: field.options,
                  span: field.span,
                  allowCreate: field.allowCreate,
                  lookupModuleName: field.lookupModuleName,
                  createFormName: field.createFormName,
                },
              ])
            } else {
              this.pickListData[field.lookupModuleName] = true
              this.loadPickList(field.lookupModuleName, field).then(_ => {
                modifier[y].push([
                  {
                    displayTypeEnum: field.displayTypeEnum,
                    name: 'id',
                    parent: field.name,
                    displayName: field.displayName,
                    required: field.required,
                    options: field.options,
                    span: field.span,
                    allowCreate: field.allowCreate,
                    lookupModuleName: field.lookupModuleName,
                    createFormName: field.createFormName,
                  },
                ])
              })
            }
          } else if (
            field.displayTypeEnum === 'LOOKUP_POPUP' &&
            field.lookupModuleName === 'site'
          ) {
            field.options = []
            if (this.sites && this.sites.length > 0) {
              this.sites.forEach(site => {
                let option = {}
                option.label = site.name
                option.value = String(site.id)
                field.options.push(option)
              })
            } else {
              this.pickListData[field.lookupModuleName] = true
              this.loadPickList(field.lookupModuleName, field).then(_ => {
                modifier[y].push([
                  {
                    displayTypeEnum: field.displayTypeEnum,
                    name: 'id',
                    parent: field.name,
                    displayName: field.displayName,
                    required: field.required,
                    options: field.options,
                    span: field.span,
                    allowCreate: field.allowCreate,
                    lookupModuleName: field.lookupModuleName,
                    createFormName: field.createFormName,
                  },
                ])
              })
            }
          } else if (
            field.displayTypeEnum === 'LOOKUP_POPUP' &&
            field.lookupModuleName &&
            field.lookupModuleName === 'building'
          ) {
            let isPromise = this.parseSpaceFieldObject(
              field,
              this.spaces,
              'Building',
              modifier,
              y
            )
            if (isPromise) {
              promises.push(isPromise)
            }
          } else if (
            field.displayTypeEnum === 'LOOKUP_POPUP' &&
            field.lookupModuleName === 'floor'
          ) {
            let isPromise = this.parseSpaceFieldObject(
              field,
              this.spaces,
              'Floor',
              modifier,
              y
            )
            if (isPromise) {
              promises.push(isPromise)
            }
          } else if (
            field.displayTypeEnum === 'LOOKUP_POPUP' &&
            field.lookupModuleName === 'asset'
          ) {
            let isPromise = this.parseFieldObject(
              field,
              this.assets,
              modifier,
              y
            )
            if (isPromise) {
              promises.push(isPromise)
            }
          } else if (
            field.displayTypeEnum === 'LOOKUP_SIMPLE' &&
            field.lookupModuleName === 'vendors' &&
            this.vendorOpts &&
            this.vendorOpts.options
          ) {
            field.options = this.vendorOpts.options
            modifier[y].push([
              {
                displayTypeEnum: field.displayTypeEnum,
                name: 'id',
                parent: field.name,
                displayName: field.displayName,
                required: field.required,
                options: field.options,
                span: field.span,
                allowCreate: field.allowCreate,
                lookupModuleName: field.lookupModuleName,
                createFormName: field.createFormName,
              },
            ])
          } else if (
            field.displayTypeEnum === 'LOOKUP_SIMPLE' &&
            field.lookupModuleName === 'tickettype' &&
            (this.name === 'web_pm' || this.name == 'multi_web_pm')
          ) {
            let options = []
            Object.keys(this.tickettype).forEach(i => {
              if (this.tickettype[i]) {
                options.push({ label: this.tickettype[i], value: String(i) })
              }
            })
            field.options = options
            modifier[y].push([
              {
                displayTypeEnum: field.displayTypeEnum,
                name: 'id',
                parent: field.name,
                displayName: field.displayName,
                required: field.required,
                options: field.options,
                span: field.span,
                allowCreate: field.allowCreate,
                lookupModuleName: field.lookupModuleName,
                createFormName: field.createFormName,
              },
            ])
          } else if (
            field.displayTypeEnum === 'LOOKUP_SIMPLE' &&
            field.lookupModuleName === 'groups'
          ) {
            field.options = []
            for (let gidx in this.groups) {
              let group = this.groups[gidx]
              let option = {}
              option.label = group.name
              option.value = String(group.groupId)
              option.siteId = group.siteId
              field.options.push(option)
            }
            modifier[y].push([
              {
                displayTypeEnum: field.displayTypeEnum,
                name: 'id',
                parent: field.name,
                displayName: field.displayName,
                required: field.required,
                options: field.options,
                span: field.span,
                allowCreate: field.allowCreate,
                lookupModuleName: field.lookupModuleName,
                createFormName: field.createFormName,
              },
            ])
          } else if (
            field.displayTypeEnum === 'LOOKUP_SIMPLE' &&
            field.lookupModuleName === 'ticketstatus'
          ) {
            let isPromise = this.parseFieldObject(
              field,
              this.ticketStatusMap,
              modifier,
              y
            )
            if (isPromise) {
              promises.push(isPromise)
            }
          } else if (
            field.displayTypeEnum === 'LOOKUP_SIMPLE' &&
            field.lookupModuleName === 'ticketpriority'
          ) {
            let isPromise = this.parseFieldObject(
              field,
              this.getTicketPriorityPickList(),
              modifier,
              y
            )
            if (isPromise) {
              promises.push(isPromise)
            }
          } else if (
            field.displayTypeEnum === 'LOOKUP_SIMPLE' &&
            field.lookupModuleName === 'ticketcategory'
          ) {
            let isPromise = this.parseFieldObject(
              field,
              this.getTicketCategoryPickList(),
              modifier,
              y
            )
            if (isPromise) {
              promises.push(isPromise)
            }
          } else if (
            field.displayTypeEnum === 'LOOKUP_SIMPLE' &&
            field.lookupModuleName === 'spaceCategory'
          ) {
            let isPromise = this.parseFieldObject(
              field,
              this.spacecategory,
              modifier,
              y
            )
            if (isPromise) {
              promises.push(isPromise)
            }
          } else if (
            field.displayTypeEnum === 'LOOKUP_SIMPLE' &&
            (field.lookupModuleName === 'site' || field.name === 'siteId')
          ) {
            field.options = []
            if (this.sites && this.sites.length > 0) {
              // handling for gobal site switch
              if (this.currentSiteId && this.currentSiteId > 0) {
                this.sites.forEach(site => {
                  let option = {}
                  option.label = site.name
                  option.value = String(site.id)
                  if (site.id === this.currentSiteId) {
                    field.options.push(option)
                  }
                })
                if (field.name === 'site') {
                  modifier[y].push([
                    {
                      displayTypeEnum: field.displayTypeEnum,
                      name: 'id',
                      parent: field.name,
                      displayName: field.displayName,
                      required: field.required,
                      options: field.options,
                      span: field.span,
                      allowCreate: field.allowCreate,
                      lookupModuleName: field.lookupModuleName,
                      createFormName: field.createFormName,
                    },
                  ])
                }
              } else {
                this.sites.forEach(site => {
                  let option = {}
                  option.label = site.name
                  option.value = String(site.id)
                  field.options.push(option)
                })
                if (field.name === 'site') {
                  modifier[y].push([
                    {
                      displayTypeEnum: field.displayTypeEnum,
                      name: 'id',
                      parent: field.name,
                      displayName: field.displayName,
                      required: field.required,
                      options: field.options,
                      span: field.span,
                      allowCreate: field.allowCreate,
                      lookupModuleName: field.lookupModuleName,
                      createFormName: field.createFormName,
                    },
                  ])
                }
              }
            } else {
              this.pickListData[field.lookupModuleName] = true
              this.loadPickList(field.lookupModuleName, field).then(_ => {
                if (field.name === 'site') {
                  modifier[y].push([
                    {
                      displayTypeEnum: field.displayTypeEnum,
                      name: 'id',
                      parent: field.name,
                      displayName: field.displayName,
                      required: field.required,
                      options: field.options,
                      span: field.span,
                      allowCreate: field.allowCreate,
                      lookupModuleName: field.lookupModuleName,
                      createFormName: field.createFormName,
                    },
                  ])
                }
              })
            }
          } else if (
            field.displayTypeEnum === 'LOOKUP_SIMPLE' &&
            field.name === 'rotatingItem'
          ) {
            this.items = await getFilteredItemList()
            field.options = []
            this.pickListData[field.lookupModuleName] = true
            let isPromise = this.loadPickList(
              field.lookupModuleName,
              field
            ).then(_ => {
              modifier[y].push([
                {
                  displayTypeEnum: field.displayTypeEnum,
                  name: 'id',
                  parent: field.name,
                  displayName: field.displayName,
                  required: field.required,
                  options: field.options,
                  span: field.span,
                  allowCreate: field.allowCreate,
                  lookupModuleName: field.lookupModuleName,
                  createFormName: field.createFormName,
                },
              ])
            })
            if (isPromise) {
              promises.push(isPromise)
            }
          } else if (
            field.displayTypeEnum === 'LOOKUP_SIMPLE' &&
            field.name === 'rotatingTool'
          ) {
            this.tools = await getFilteredToolList()
            this.fillLookUpMeta(field, modifier, promises, y)
          } else if (
            field.displayTypeEnum === 'LOOKUP_SIMPLE' &&
            field.name === 'vendorContact' &&
            ['portalWorkpermitForm', 'workpermitForm'].includes(this.name)
          ) {
            // temp hack
            let vendorId = this.$getProperty(this, 'editObj.vendor.id', null)
            let permitType = this.$getProperty(this, 'editObj.permitType', null)
            if (vendorId > 0) {
              this.permitVendorChangeActions(vendorId, true)
            }
            if (
              Number(permitType) === 1 ||
              this.name === 'portalWorkpermitForm'
            ) {
              this.disableWorkPermitVendorFields = false
            }
            this.fillLookUpMeta(field, modifier, promises, y)
          } else if (
            field.displayTypeEnum === 'LOOKUP_SIMPLE' ||
            field.displayTypeEnum === 'LOOKUP_POPUP'
          ) {
            field.options = []
            this.pickListData[field.lookupModuleName] = true
            let isPromise = this.loadPickList(
              field.lookupModuleName,
              field
            ).then(_ => {
              modifier[y].push([
                {
                  displayTypeEnum: field.displayTypeEnum,
                  name: 'id',
                  parent: field.name,
                  displayName: field.displayName,
                  required: field.required,
                  options: field.options,
                  span: field.span,
                  allowCreate: field.allowCreate,
                  lookupModuleName: field.lookupModuleName,
                  createFormName: field.createFormName,
                },
              ])
            })
            if (isPromise) {
              promises.push(isPromise)
            }
          } else if (field.displayTypeEnum === 'URGENCY') {
            field.options = []
            let urgency = ['Not Urgent', 'Urgent', 'Emergency']
            for (let i = 0; i < urgency.length; i++) {
              field.options.push({
                label: urgency[i],
                value: i + 1,
              })
            }
          } else if (field.displayTypeEnum === 'DURATION') {
            modelObj[field.name] = { days: 0, hours: 0, options: [] }
            for (let i = 0; i < 24; i += 0.5) {
              modelObj[field.name].options.push({ label: i, value: i })
            }
          } else if (field.displayTypeEnum === 'ADDRESS') {
            if (this.editObj && this.editObj[field.name]) {
              modelObj[field.name] = {
                id: this.editObj[field.name].id,
                street: null,
                city: null,
                state: null,
                zip: null,
                lat: 1.1,
                lng: 1.1,
                country: null,
              }
            } else {
              modelObj[field.name] = {
                street: null,
                city: null,
                state: null,
                zip: null,
                lat: 1.1,
                lng: 1.1,
                country: null,
              }
            }
          } else if (field.displayTypeEnum === 'SADDRESS') {
            if (this.editObj && this.editObj.hasOwnProperty(field.name)) {
              modelObj[field.name] = {
                id: this.editObj[field.name].id,
                street: null,
                city: null,
                state: null,
                zip: null,
                lat: 1.1,
                lng: 1.1,
                country: null,
              }
            } else if (
              field.name === 'billToAddress' &&
              this.editObj === null
            ) {
              this.$http.get('/settings/company').then(response => {
                modelObj[field.name] = {
                  street: null,
                  city: null,
                  state: null,
                  zip: null,
                  lat: 1.1,
                  lng: 1.1,
                  country: null,
                }
                let address = response.data.org
                modelObj.billToAddress.city = address.city
                modelObj.billToAddress.state = address.state
                modelObj.billToAddress.street = address.street
                modelObj.billToAddress.country = address.country
                modelObj.billToAddress.zip = address.zip
              })
            } else {
              modelObj[field.name] = {
                street: null,
                city: null,
                state: null,
                zip: null,
                lat: 1.1,
                lng: 1.1,
                country: null,
              }
            }
          } else if (field.displayTypeEnum === 'PURCHASEDITEM') {
            this.$set(modelObj, field.name)
            modelObj[field.name] = [
              {
                unitcost: null,
                quantity: null,
              },
            ]
          } else if (field.displayTypeEnum === 'WARRANTY_LINE_ITEMS') {
            this.$set(modelObj, field.name)
            modelObj[field.name] = [
              {
                service: { id: null },
                toolCoverage: null,
                itemCoverage: null,
                labourCoverage: null,
              },
            ]
            this.loadTypes(5)
          } else if (field.displayTypeEnum === 'VENDOR_CONTACTS') {
            this.$set(modelObj, field.name)
            modelObj[field.name] = []
          } else if (field.displayTypeEnum === 'SERVICEVENDORS') {
            this.$set(modelObj, field.name)
            modelObj[field.name] = [
              {
                vendor: { id: null },
                lastPrice: null,
              },
            ]
            this.loadVendorPickListData()
          } else if (field.displayTypeEnum === 'EXTERNAL_ATTENDEES') {
            this.$set(modelObj, field.name)
            modelObj[field.name] = []
          } else if (field.displayTypeEnum === 'RECURRING_VISITOR') {
            modelObj[field.name] = {
              isRecurring: 1,
              expectedStartTime: null,
              expectedEndTime: null,
              startDate: null,
              endDate: null,
              startTime: null,
              endTime: null,
              weeks: [
                { dayOfWeek: 1, displayName: 'Monday', value: true },
                { dayOfWeek: 2, displayName: 'Tuesday', value: true },
                { dayOfWeek: 3, displayName: 'Wednesday', value: true },
                { dayOfWeek: 4, displayName: 'Thrusday', value: true },
                { dayOfWeek: 5, displayName: 'Friday', value: true },
                { dayOfWeek: 6, displayName: 'Saturday', value: true },
                { dayOfWeek: 7, displayName: 'Sunday', value: true },
              ],
            }
          } else if (field.displayTypeEnum === 'LINEITEMS') {
            this.loadTypes(1)
            this.loadTypes(2)
            this.loadTypes(4)
            this.$set(modelObj, field.name)
            modelObj[field.name] = [
              {
                inventoryType: 1,
                itemType: {
                  id: null,
                },
                toolType: {
                  id: null,
                },
                remarks: null,
                quantity: null,
                unitPrice: null,
              },
            ]
          } else if (field.displayTypeEnum === 'INVREQUEST_LINE_ITEMS') {
            this.loadTypes(1)
            this.loadTypes(2)
            this.$set(modelObj, field.name)
            modelObj[field.name] = [
              {
                inventoryType: 1,
                itemType: {
                  id: null,
                },
                toolType: {
                  id: null,
                },
                quantity: null,
              },
            ]
          } else if (field.displayTypeEnum === 'VISITOR_INVITEES') {
            this.loadTypes(6)
            this.$set(modelObj, field.name)
            modelObj[field.name] = [
              {
                id: null,
                name: null,
                phone: null,
                email: null,
              },
            ]
          } else if (field.displayTypeEnum === 'LABOUR_LINE_ITEMS') {
            this.loadLabours()
            this.$set(modelObj, field.name)
            modelObj[field.name] = [
              {
                labour: {
                  id: null,
                },
                cost: null,
              },
            ]
          } else if (field.displayTypeEnum === 'SCHEDULER_INFO') {
            modelObj[field.name] = {
              isCheckedPayment: false,
              paymentInterval: null,
              scheduleDay: null,
              scheduleMonth: null,
              scheduleTime: null,
              frequencyType: 1,
              weekdays: [
                {
                  label: 'Monday',
                  value: 1,
                },
                {
                  label: 'Tuesday',
                  value: 2,
                },
                {
                  label: 'Wednesday',
                  value: 3,
                },
                {
                  label: 'Thursday',
                  value: 4,
                },
                {
                  label: 'Friday',
                  value: 5,
                },
                {
                  label: 'Saturday',
                  value: 6,
                },
                {
                  label: 'Sunday',
                  value: 7,
                },
              ],
              months: [
                { label: 'January', value: 1 },
                { label: 'February', value: 2 },
                { label: 'March', value: 3 },
                { label: 'April', value: 4 },
                { label: 'May', value: 5 },
                { label: 'June', value: 6 },
                { label: 'July', value: 7 },
                { label: 'August', value: 8 },
                { label: 'September', value: 9 },
                { label: 'October', value: 10 },
                { label: 'November', value: 11 },
                { label: 'December', value: 12 },
              ],
              days: [],
            }
            for (let i = 1; i <= 28; i++) {
              modelObj[field.name].days.push({ label: i, value: i })
            }
          } else if (field.displayTypeEnum === 'UNIT') {
            field.MetricMeta = {
              unitId: null,
              metric: null,
              metricName: null,
            }
            this.loadDefaultMetricUnits()
          } else if (field.displayTypeEnum === 'MULTI_USER_LIST') {
            modelObj[field.name] = []
          } else if (field.displayTypeEnum === 'USER') {
            modelObj[field.name] = {
              id: null,
            }
            if (
              this.name === 'reservationForm' &&
              field.name === 'reservedFor'
            ) {
              modelObj[field.name].id = String(this.$account.user.id)
            }
          } else if (
            field.displayTypeEnum === 'DATE' &&
            (field.name === 'requestedTime' || field.name === 'orderedTime')
          ) {
            modelObj[field.name] = Date.now()
          } else if (
            field.displayTypeEnum === 'SELECTBOX' &&
            field.name === 'country'
          ) {
            field.options = countries
          } else if (
            field.displayTypeEnum === 'WOASSETSPACECHOOSER' ||
            field.displayTypeEnum === 'QR'
          ) {
            modelObj['resource'] = null
          } else if (
            field.displayTypeEnum === 'DECISION_BOX' &&
            field.name === 'isConsumable'
          ) {
            modelObj[field.name] = true
            if (
              this.editObj &&
              this.editObj.hasOwnProperty('isConsumable') &&
              !this.editObj[field.name]
            ) {
              modelObj[field.name] = false
            }
          } else if (field.displayTypeEnum === 'DECISION_BOX') {
            if (field.field && !field.field.default) {
              if (!modelObj.hasOwnProperty('data')) {
                modelObj['data'] = {}
              }
              modelObj['data'][field.name] = false
              modifier[y].push([
                {
                  displayTypeEnum: field.displayTypeEnum,
                  name: field.name,
                  parent: 'data',
                  displayName: field.displayName,
                  required: field.required,
                  span: field.span,
                },
              ])
            } else {
              modelObj[field.name] = false
            }
          } else if (field.displayTypeEnum === 'SPACEMULTICHOOSER') {
            modelObj['spaces'] = null
          } else if (field.displayTypeEnum === 'SITEMULTICHOOSER') {
            modelObj['sites'] = []
          } else if (field.displayTypeEnum === 'TASKS') {
            modelObj['tasks'] = [
              {
                section: 'default',
                tasks: [],
              },
            ]
          } else if (field.displayTypeEnum === 'ASSETMULTICHOOSER') {
            modelObj['assets'] = [
              {
                section: 'default',
                assets: [],
              },
            ]
          } else if (field.field && enumFieldsList.includes(field.field.name)) {
            if (field.displayTypeEnum === 'SELECTBOX') {
              field.options = []
              if (field.field.enumMap) {
                Object.keys(field.field.enumMap).forEach(key => {
                  let option = {}
                  option.label = field.field.enumMap[key]
                  option.value = key
                  field.options.push(option)
                })
                modifier[y].push([
                  {
                    displayTypeEnum: field.displayTypeEnum,
                    name: field.name,
                    displayName: field.displayName,
                    required: field.required,
                    options: field.options,
                    span: field.span,
                    allowCreate: field.allowCreate,
                    lookupModuleName: field.lookupModuleName,
                    createFormName: field.createFormName,
                  },
                ])
              }
            }
          } else if (field.field && !field.field.default) {
            if (field.displayTypeEnum === 'SELECTBOX') {
              field.options = []
              if (field.field.enumMap) {
                Object.keys(field.field.enumMap).forEach(key => {
                  let option = {}
                  option.label = field.field.enumMap[key]
                  option.value = key
                  field.options.push(option)
                })
                modifier[y].push([
                  {
                    displayTypeEnum: field.displayTypeEnum,
                    name: field.name,
                    parent: 'data',
                    displayName: field.displayName,
                    required: field.required,
                    options: field.options,
                    span: field.span,
                    allowCreate: field.allowCreate,
                    lookupModuleName: field.lookupModuleName,
                    createFormName: field.createFormName,
                  },
                ])
              }
            } else {
              modifier[y].push([
                {
                  displayTypeEnum: field.displayTypeEnum,
                  name: field.name,
                  parent: 'data',
                  displayName: field.displayName,
                  required: field.required,
                  span: field.span,
                  allowCreate: field.allowCreate,
                  lookupModuleName: field.lookupModuleName,
                  createFormName: field.createFormName,
                },
              ])
            }
          }

          if (typeof modelObj[field.name] === 'undefined') {
            if (field.displayTypeEnum === 'REQUESTER') {
              modelObj[field.name] = {}
            } else if (field.displayTypeEnum === 'ATTACHMENT') {
              modelObj['attachedFiles'] = {
                attachments: [],
                uploadFiles: [],
              }
            } else if (field.displayTypeEnum === 'LOGO') {
              modelObj['logo'] = {
                uploadLogo: [],
                logoImageUrl: '',
              }
            } else if (field.displayTypeEnum === 'WOASSETSPACECHOOSER') {
              modelObj['resource'] = null
            } else if (field.displayTypeEnum === 'TEAMSTAFFASSIGNMENT') {
              modelObj['assignedTo'] = { id: '' }
              modelObj['assignmentGroup'] = { id: '' }
            } else if (field.displayTypeEnum === 'TICKETNOTES') {
              modelObj['comment'] = null
            } else if (field.name === 'siteId') {
              modelObj['siteId'] = ''
            } else if (
              field.displayTypeEnum === 'LOOKUP_SIMPLE' ||
              field.displayTypeEnum === 'LOOKUP_POPUP'
            ) {
              modelObj[field.name] = { id: '' }
            } else if (field.isRequester) {
              if (!modelObj.hasOwnProperty(field.parent)) {
                modelObj[field.parent] = {}
              }
              modelObj[field.parent][field.name] = ''
            } else if (field.field && !field.field.default) {
              if (!modelObj.hasOwnProperty('data')) {
                modelObj['data'] = {}
              }
              modelObj['data'][field.name] = null
            } else {
              modelObj[field.name] = null
            }
          }

          if (field.required && field.displayTypeEnum !== 'REQUESTER') {
            if (field.parent) {
              this.rules[field.parent] = []
              this.rules[field.parent].push({
                required: true,
                message: `Please input ${field.displayName}`,
                trigger: 'blur',
              })
            } else {
              this.rules[field.name] = []
              this.rules[field.name].push({
                required: true,
                message: `Please input ${field.displayName}`,
                trigger: 'blur',
              })
            }
          }
          if (field.displayTypeEnum === 'ATTACHMENT') {
            this.dataTypeMapping['attachedFiles'] = field.displayTypeEnum
          } else {
            this.dataTypeMapping[field.name] = field.displayTypeEnum
          }
        }
      }
      if (!this.editObj) {
        this.model = modelObj
      } else {
        if (!modelObj.hasOwnProperty('siteId')) {
          modelObj['siteId'] = null
        }
        this.model = this.$helpers.cloneFromSchema(modelObj, this.editObj)
        Object.keys(this.model)
          .filter(key => key in this.model)
          .forEach(key => {
            if (
              this.model[key] &&
              key !== 'assignmentGroup' &&
              key !== 'assignedTo'
            ) {
              if (
                this.model[key] instanceof Object &&
                this.model[key].hasOwnProperty('id') &&
                this.model[key] instanceof Object &&
                this.model[key].hasOwnProperty('id') &&
                this.model[key].id &&
                key !== 'vendor'
              ) {
                this.model[key].id = String(this.model[key].id)
              }
            }
          })
      }
      Promise.all(promises).then(_ => {
        Object.keys(modifier).forEach(e => {
          let m = modifier[e]
          if (m.length > 0) {
            m.forEach(n => {
              if (n.required) {
                this.rules[n.parent ? `${n.parent}.${n.name}` : n.name].push({
                  required: true,
                  message: `Please input Requester Name`,
                  trigger: 'blur',
                })
              }
              if (n[0].span === 1) {
                this.fields[e].splice(0, 1, n[0])
              } else if (n[0].span === 2) {
                if (this.fields[e][0]) {
                  this.fields[e][0] = n[0]
                } else {
                  this.fields[e].push(n[0])
                }
              } else if (n[0].span === 3) {
                if (this.fields[e][1]) {
                  this.fields[e][1] = n[0]
                } else {
                  this.fields[e].push(n[0])
                }
              }
            })
          }
        })
        // Temp hack to introduce sections
        let containsTask = false
        this.sections.push({ fields: [] }, { fields: [] })
        for (let i = 0; i < this.fields.length; i++) {
          if (!this.fields[i]) {
            continue
          }
          if (
            this.fields[i][0] &&
            this.fields[i][0].displayTypeEnum === 'TASKS'
          ) {
            this.fields[i][0].name = 'tasks'
            containsTask = true
            this.fields[i].span = 1
            this.$set(this.sections[1], 'fields', [this.fields[i]])
          } else if (
            this.fields[i][0] &&
            this.fields[i][0].displayTypeEnum === 'ASSETMULTICHOOSER'
          ) {
            this.fields[i][0].name = 'utilityMeters'
            containsTask = true
            this.fields[i].span = 1
            this.$set(this.sections[1], 'fields', [this.fields[i]])
          } else if (
            this.fields[i][0] &&
            this.fields[i][0].displayTypeEnum === 'LINEITEMS' &&
            (this.name === 'purchaseContractForm' ||
              this.name === 'rentalLeaseContractForm')
          ) {
            this.fields[i][0].name = 'lineItems'
            containsTask = true
            this.fields[i].span = 1
            this.$set(this.sections[1], 'fields', [this.fields[i]])
          } else if (
            this.fields[i][0] &&
            this.fields[i][0].displayTypeEnum === 'WARRANTY_LINE_ITEMS' &&
            this.name === 'warrantyContractForm'
          ) {
            this.fields[i][0].name = 'lineItems'
            containsTask = true
            this.fields[i].span = 1
            this.$set(this.sections[1], 'fields', [this.fields[i]])
          } else if (
            this.fields[i][0] &&
            this.fields[i][0].displayTypeEnum === 'INVREQUEST_LINE_ITEMS' &&
            this.name === 'inventoryRequestForm'
          ) {
            this.fields[i][0].name = 'lineItems'
            containsTask = true
            this.fields[i].span = 1
            this.$set(this.sections[1], 'fields', [this.fields[i]])
          } else if (
            this.fields[i][0] &&
            this.fields[i][0].displayTypeEnum === 'VISITOR_INVITEES'
          ) {
            this.fields[i][0].name = 'invitees'
            containsTask = true
            this.sections.push({ fields: [] })
            this.$set(this.sections[2], 'fields', [this.fields[i]])
          } else if (
            this.fields[i][0] &&
            this.fields[i][0].displayTypeEnum === 'RECURRING_VISITOR'
          ) {
            containsTask = true
            this.fields[i].span = 1
            this.$set(this.sections[1], 'fields', [this.fields[i]])
          } else if (
            this.fields[i][0] &&
            this.fields[i][0].displayTypeEnum === 'SCHEDULER_INFO'
          ) {
            this.fields[i][0].name = 'payment'
            containsTask = true
            this.sections.push({ fields: [] })
            this.sections[2].fields.push(this.fields[i])
          } else if (
            this.fields[i][0] &&
            this.fields[i][0].displayTypeEnum === 'LABOUR_LINE_ITEMS' &&
            this.name === 'labourContractForm'
          ) {
            containsTask = true
            this.fields[i].span = 1
            this.$set(this.sections[1], 'fields', [this.fields[i]])
          } else {
            this.sections[0].fields.push(this.fields[i])
          }
        }

        if (!containsTask) {
          this.sections.splice(1, 1)
        }
        this.loading = false
      })
    },
    cloneFromSchema(schema, obj) {
      let value = {}
      Object.keys(schema).forEach(key => {
        if (schema[key] instanceof Object && obj[key] instanceof Object) {
          value[key] = this.$helpers.cloneFromSchema(schema[key], obj[key])
        } else {
          value[key] = obj[key]
        }
      })
      return value
    },
    reorderSequenceNumber(fields) {
      fields.forEach((d, i) => {
        d.sequenceNumber = i + 1
      })
    },
    parseSpaceFieldObject(field, typeName, spaceType, modifier, index) {
      field.options = []
      if (this.spaces && this.spaces.length > 0) {
        Object.keys(this.spaces).forEach(key => {
          let option = {}
          if (this.spaces[key].spaceTypeVal === spaceType) {
            option.label = this.spaces[key].name
            option.value = this.spaces[key].id
            field.options.push(option)
          }
        })
        modifier[index].push([
          {
            displayTypeEnum: field.displayTypeEnum,
            name: 'id',
            parent: field.name,
            displayName: field.displayName,
            required: field.required,
            options: field.options,
            span: field.span,
            allowCreate: field.allowCreate,
            lookupModuleName: field.lookupModuleName,
            createFormName: field.createFormName,
          },
        ])
        return null
      } else {
        this.pickListData[field.lookupModuleName] = true
        return this.loadPickList(field.lookupModuleName, field).then(_ => {
          modifier[index].push([
            {
              displayTypeEnum: field.displayTypeEnum,
              name: 'id',
              parent: field.name,
              displayName: field.displayName,
              required: field.required,
              options: field.options,
              span: field.span,
              allowCreate: field.allowCreate,
              lookupModuleName: field.lookupModuleName,
              createFormName: field.createFormName,
            },
          ])
        })
      }
    },
    parseFieldObject(field, typeName, modifier, index) {
      field.options = []
      if (!isEmpty(typeName)) {
        Object.keys(typeName).forEach(key => {
          let option = {}
          option.label = typeName[key]
          option.value = key
          field.options.push(option)
        })
        modifier[index].push([
          {
            displayTypeEnum: field.displayTypeEnum,
            name: 'id',
            parent: field.name,
            displayName: field.displayName,
            required: field.required,
            options: field.options,
            span: field.span,
            allowCreate: field.allowCreate,
            lookupModuleName: field.lookupModuleName,
            createFormName: field.createFormName,
          },
        ])
        return null
      } else {
        this.pickListData[field.lookupModuleName] = true
        return this.loadPickList(field.lookupModuleName, field).then(_ => {
          modifier[index].push([
            {
              displayTypeEnum: field.displayTypeEnum,
              name: 'id',
              parent: field.name,
              displayName: field.displayName,
              required: field.required,
              options: field.options,
              span: field.span,
              allowCreate: field.allowCreate,
              lookupModuleName: field.lookupModuleName,
              createFormName: field.createFormName,
            },
          ])
        })
      }
    },
    loadPickList(moduleName, field) {
      return getFieldOptions({
        field: { lookupModuleName: moduleName },
        perPage: 500, //temp
      }).then(({ error, options }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.pickListData[moduleName] = false
          field.options = options
        }
      })
    },
    sendClientErrorLog(error) {
      if (isEmpty(error)) {
        return
      }
      let { $account } = this
      let { user } = $account || {}
      let data = {
        userId: user.ouid,
        orgId: user.orgId,
        route: this.$router.name,
        message: error.message,
        stacktrace: error.stack || '',
        browser: 'browser',
        os: 'os',
        ceType: 'client: FacilioWebForm.vue',
        ceInfo: null,
        ceUa: null,
      }
      this.$http.post('/v2/errors/web', data).catch(() => {
        setTimeout(function cb() {}, 1 * 60 * 1000) //delays logging by 1min if api call fails
      })
    },
    save() {
      try {
        this.saving = true
        let { formId, model } = this
        this.$refs['newRecordForm'].validate(valid => {
          if (valid) {
            this.$emit('formID', formId)
            this.$emit('validated', this.transformAsContextObj(model))
          } else {
            this.$emit('failed', this.transformAsContextObj(model))
          }
        })
      } catch (error) {
        this.$message.error(
          this.$t('maintenance.old_pm.something_went_wrong_in_form')
        )
        this.sendClientErrorLog(error)
      }
    },
    cancel() {
      this.$emit('cancel', true)
    },
    transformAsContextObj(model) {
      let self = this
      Object.keys(model).forEach(key => {
        if (model[key] && key === 'data') {
          Object.keys(model[key]).forEach(a => {
            let dataType = self.dataTypeMapping[a]
            if (dataType === 'DATE' || dataType === 'DATETIME') {
              if (isNaN(model[key].a)) {
                model[key][a] = Date.parse(model[key][a])
              }
            } else if (dataType === 'DURATION') {
              model[key][a].duration = this.$util.daysHoursToUnixTime(
                model[key][a]
              )
            } else if (model[key][a] && model[key][a].id === '') {
              model[key][a].id = -1
            }
          })
        } else {
          let dataType = self.dataTypeMapping[key]
          if (dataType === 'DATE' || dataType === 'DATETIME') {
            if (isNaN(model[key])) {
              model[key] = Date.parse(model[key])
            } else if (
              !isNaN(Date.parse(model[key])) &&
              (key === 'requestedTime' ||
                key === 'requiredTime' ||
                key === 'orderedTime' ||
                key === 'endDate' ||
                key === 'fromDate' ||
                key === 'renewalDate' ||
                key === 'expectedStartTime' ||
                key === 'expectedEndTime')
            ) {
              model[key] = Date.parse(model[key])
            }
          } else if (dataType === 'DURATION') {
            model[key].duration = this.$util.daysHoursToUnixTime(model[key])
          } else if (model[key] && model[key].id === '' && key !== 'owner') {
            model[key].id = -1
          } else if (model[key] && model[key].id === '' && key === 'owner') {
            model[key].id = -99
          }
        }
      })
      return model
    },
    addNewSection() {
      let error = this.validatePreTasks()
      if (error) {
        this.$message.error(error)
        return
      }
      this.selectedTaskSectionIdx = this.model['tasks'].length
      this.model['tasks'][this.selectedTaskSectionIdx] = {
        section: this.checkAndAddSection('Untitled Section'),
        tasks: [],
      }
      this.addNewTask()
      this.$forceUpdate()
    },
    addNewTask(event, index) {
      let error = this.validatePreTasks()
      if (error) {
        this.$message.error(error)
        return
      }
      let newTask = this.$helpers.cloneObject(this.newTask)
      newTask.resource = this.$helpers.cloneObject(this.model.resource)
      if (this.model.resource && newTask.resource) {
        newTask.resource.name = this.spaceAssetDisplayName
      }
      let taskSection = this.model['tasks'][this.selectedTaskSectionIdx]
      index = index >= 0 ? index : taskSection.tasks.length
      taskSection.tasks.splice(index, 0, newTask)
      this.$nextTick(() => {
        let ref = this.$refs['task-' + taskSection.section + '-' + index]
        if (ref) {
          ref[0].focus()
        }
      })
      this.showTaskDetails(newTask)
    },
    fillReadings() {
      this.readingFields = []
      if (
        this.newWorkOrder.resource &&
        this.newWorkOrder.resource.resourceType === 2
      ) {
        this.$util
          .loadReadingFields(this.newWorkOrder.resource, false)
          .then(fields => {
            this.readingFields = fields
          })
      }
    },
    fillTaskReadings() {
      this.$util
        .loadReadingFields(this.selectedTask.resource, false)
        .then(fields => {
          this.taskReadingFields = fields
        })
    },
    onSelectInput() {
      if (this.selectedTask.inputType === '2') {
        if (
          !this.selectedTask.resource ||
          this.selectedTask.resource.id === -1
        ) {
          this.selectedTask.resource = this.newWorkOrder.resource
        }
        this.fillTaskReadings()
      } else if (
        this.selectedTask.inputType === '5' ||
        this.selectedTask.inputType === '6'
      ) {
        if (this.selectedTask.options.length < 2) {
          this.addTaskOption(this.selectedTask)
          this.addTaskOption(this.selectedTask)
        }
      }
    },
    addTaskOption(selectedTask) {
      selectedTask.options.push({ name: '' })
    },
    onTaskSelectInput() {
      if (this.taskSettingsEdit.inputType === '2') {
        this.fillTaskReadingsForTasks()
      } else if (this.taskSettingsEdit.inputType === '5') {
        this.taskSettingsEdit.options = []
        this.addTaskOption(this.taskSettingsEdit)
        this.addTaskOption(this.taskSettingsEdit)
      } else if (this.taskSettingsEdit.inputType === '6') {
        this.taskSettingsEdit.options = [{ name: '' }, { name: '' }]
        this.taskSettingsEdit.options[0].name = 'YES'
        this.taskSettingsEdit.options[1].name = 'NO'
      }
    },
    showTaskDetails(task) {
      this.showAddTask = true
      this.showAddTrigger = false
      this.selectedTask = this.$helpers.cloneObject(task)
      this.selectedTask.inputType = String(this.selectedTask.inputType)
      if (this.selectedTask.inputType) {
        this.selectedTask.enableInput = true
      }
      this.onSelectInput()
    },
    removeTask(sectionName, index) {
      let currentSection = this.model['tasks'].filter(
        section => section.section === sectionName
      )[0]
      currentSection.tasks.splice(index, 1)
      this.closedisplay()
    },
    removeTaskSection(index) {
      this.model['tasks'][index - 1].tasks = this.model['tasks'][
        index - 1
      ].tasks.concat(this.model['tasks'][index].tasks)
      this.model['tasks'].splice(index, 1)
      this.closedisplay()
      this.selectedTaskSectionIdx = index - 1
    },
    remove(list, index) {
      list.splice(index, 1)
    },
    closedisplay() {
      this.showAddTask = false
      this.showAddTrigger = false
    },
    onTaskEnter($event, index, subject) {
      if (!$event.target || $event.target.selectionStart !== 0 || !subject) {
        index++
      }
      this.addNewTask($event, index)
    },
    onTaskPaste(tasks, index, event) {
      let data = event.clipboardData.getData('text/plain')
      if (data) {
        let list = data.split(/[\n\r]/g)
        if (list.length > 1) {
          if (!tasks[index].subject) {
            tasks[index].subject = list.splice(0, 1)[0]
          }
          list.forEach(subject => {
            if (subject) {
              this.addNewTask(event, ++index)
              tasks[index].subject = subject
            }
          })
          event.preventDefault()
        }
      }
    },
    checkAndAddSection(section) {
      let newsection = section
      for (let key in this.model['tasks']) {
        if (newsection === this.model['tasks'][key].section) {
          newsection = this.checkAndAddSection(
            newsection + this.lastAddedSectionCount
          )
          this.lastAddedSectionCount++
        }
      }
      return newsection
    },
    validatePreTasks(data) {
      this.isTaskError = false
      this.errorIndex = null
      let error
      let taskLength = this.model['tasks'][this.selectedTaskSectionIdx].tasks
        .length
      let prevTask = {}
      if (taskLength >= 1) {
        prevTask = this.model['tasks'][this.selectedTaskSectionIdx].tasks[
          taskLength - 1
        ]
        if (prevTask) {
          error = this.validateTask(prevTask)
          if (error) {
            this.errorIndex = taskLength - 1
          }
        }
      } else {
        if (this.selectedTaskSectionIdx >= 1) {
          let preSectionLastTask = this.model['tasks'][
            this.selectedTaskSectionIdx - 1
          ].tasks.length
          prevTask = this.model['tasks'][this.selectedTaskSectionIdx - 1].tasks[
            preSectionLastTask - 1
          ]
          if (prevTask) {
            error = this.validateTask(prevTask)
          }
          if (error) {
            this.errorIndex = preSectionLastTask - 1
          }
        }
      }
      if (error) {
        this.isTaskError = true
        return error
      }
    },
    validateTask(task) {
      let errorMsg = ''
      if (!task.subject) {
        errorMsg += 'Please specify the task subject \n'
      }
      if (task.enableInput) {
        if (parseInt(task.inputType) === 2) {
          if (parseInt(task.readingFieldId) < 1 || !task.readingFieldId) {
            errorMsg += 'Please select a specific reading type \n'
          }
        } else if (parseInt(task.inputType) === 4) {
          if (task.validation === 'safeLimit') {
            if (!task.minSafeLimit && !task.maxSafeLimit) {
              errorMsg += 'Please enter either minimum or maximum value \n'
            }
          }
        } else if (parseInt(task.inputType) === 5) {
          if (task.options.length < 2) {
            errorMsg += 'Please enter atleast two options\n'
          } else if (!task.options[0].name && !task.options[1].name) {
            errorMsg += 'Please enter atleast two options \n'
          }
        }
      }
      return errorMsg
    },
    cancelTaskSettings() {
      this.showTaskSettings = false
    },
    forceUpdate() {
      this.$forceUpdate()
    },
    addPurchasedItemEntry(field, index, type) {
      if (type === 'PURCHASEDITEM') {
        field.push({ unitcost: null, quantity: null })
      }
    },
    addLabourEntry(field, index, type) {
      field.push({ labour: { id: null }, cost: null })
    },
    addWarrantyLineEntry(field) {
      field.push({
        service: { id: null },
        toolCoverage: null,
        itemCoverage: null,
        labourCoverage: null,
      })
    },
    addServiceVendorEntry(field) {
      field.push({ vendor: { id: null }, lastPrice: null })
    },
    addVendorContactEntry(field) {
      field.push({
        name: null,
        email: null,
        phone: null,
        isPortalAccessNeeded: false,
      })
    },
    addVisitorInviteesEntry(field) {
      field.push({
        id: null,
        email: null,
        phone: null,
      })
    },
    addItemEntry(field) {
      let emptyData = {
        inventoryType: 1,
        itemType: {
          id: null,
        },
        toolType: {
          id: null,
        },
        remarks: null,
        quantity: null,
        unitPrice: null,
      }
      field.push(emptyData)
    },
    addIRItemEntry(field) {
      let emptyData = {
        inventoryType: 1,
        itemType: {
          id: null,
        },
        toolType: {
          id: null,
        },
        quantity: null,
      }
      field.push(emptyData)
    },
    removeEntryFromList(list, index) {
      list.splice(index, 1)
    },
    addExternalAttendeeEntry(field) {
      field.push({ name: null, email: null })
    },
    imageChange(file, name) {
      if (!file) return

      const formData = new FormData()
      formData.append('fileContent', file[0])

      let self = this
      self.$http.post('/v2/files/add', formData).then(function(response) {
        self.model[name] = response.data.result.fileInfo.fileId
      })
    },
    loadDefaultMetricUnits() {
      let self = this
      self.$http.get('/units/getDefaultMetricUnits').then(response => {
        self.metricsUnits = response.data
      })
    },
    loadUnit(name, field) {
      if (field.metric > 0) {
        let metric = this.metricsUnits.metrics.filter(d => {
          if (d.metricId === field.metric) {
            return d
          }
        })
        field.metricName = metric[0]._name
        if (field.metricName === 'PERCENTAGE') {
          this.metricsUnits.metricWithUnits[metric[0]._name].splice(1, 1)
        }
        field.unitId = this.setOrgUnit(field.metric)
        this.model[name] = this.setOrgUnit(field.metric)
      }
    },
    setOrgUnit(metricId) {
      if (this.metricsUnits.orgUnitsList) {
        let unitId = null
        this.metricsUnits.orgUnitsList.forEach(d => {
          if (d.metric === metricId) {
            unitId = d.unit
          }
        })
        return unitId > 0 ? unitId : null
      }
    },
    setUnit(name, unitid) {
      this.model[name] = unitid
    },
    showCreateDialog(element) {
      let self = this
      this.$quickAdd.open({
        formName: element.createFormName,
        moduleName: element.lookupModuleName,
        onAdd: function(data) {
          if (data.moduleData) {
            let option = {
              label: data.moduleData.displayName
                ? data.moduleData.displayName
                : data.moduleData.name,
              value: data.moduleData.id + '',
            }
            element.options.push(option)

            if (element.parent) {
              self.model[element.parent][element.name] = option.value
            } else {
              self.model[element.name] = option.value
            }
          }
        },
        onCancel: function() {},
      })
    },
    loadTypes(type) {
      let rotatingFilter = {
        isRotating: {
          operatorId: 15,
          value: ['true'],
        },
      }
      if (type === 1) {
        if (this.itemTypes.length === 0) {
          let self = this

          let url = 'v2/itemTypes/view/all'
          if (this.name === 'rentalLeaseContractForm') {
            url =
              url +
              '?&filters=' +
              encodeURIComponent(JSON.stringify(rotatingFilter))
          }
          this.$http.get(url).then(function(response) {
            self.itemTypes =
              response.data.result && response.data.result.itemTypes
                ? response.data.result.itemTypes
                : []
            self.nonVendorItems = self.itemTypes
          })
        }
      } else if (type === 2) {
        if (this.toolTypes.length === 0) {
          let self = this
          let url = 'v2/toolTypes/view/all'
          if (this.name === 'rentalLeaseContractForm') {
            url =
              url +
              '?&filters=' +
              encodeURIComponent(JSON.stringify(rotatingFilter))
          }
          this.$http.get(url).then(function(response) {
            self.toolTypes =
              response.data.result && response.data.result.toolTypes
                ? response.data.result.toolTypes
                : []
          })
        }
      } else if (type === 3) {
        if (this.vendors.length === 0) {
          let self = this
          self.$http
            .get('/v2/vendors/view/all')
            .then(function(response) {
              self.vendors = response.data.result.vendors
            })
            .catch(() => {})
        }
      } else if (type === 4) {
        if (this.storeRooms.length === 0) {
          let self = this
          self.$http
            .get('/v2/storeRoom/view/all')
            .then(function(response) {
              self.storeRooms = response.data.result.storeRooms
            })
            .catch(() => {})
        }
      } else if (type === 5) {
        if (this.services.length === 0) {
          let self = this
          self.$http
            .get('/v2/service/all')
            .then(response => {
              self.services = response.data.result.services
            })
            .catch(() => {})
        }
      } else if (type === 6) {
        if (this.visitors.length === 0) {
          this.$http
            .get('/v2/visitor/all')
            .then(response => {
              this.visitors = response.data.result.visitors || []
            })
            .catch(() => {})
        }
      }
    },
    loadLabours() {
      let self = this
      self.$http
        .get('/v2/labour/labourList')
        .then(function(response) {
          self.labours = response.data.result.labours
        })
        .catch(() => {})
    },
    loadRateForLabour(labourData) {
      let selectedLabour = this.labours.filter(labour => {
        return labourData.labour.id === labour.id
      })
      if (selectedLabour.length === 1) {
        labourData.cost = selectedLabour[0].cost
      } else {
        labourData.cost = null
      }
    },
    vendorChangeActions(id) {
      let self = this
      this.$http
        .get('/v2/itemTypesForVendors/vendor/' + id)
        .then(function(response) {
          self.vendorItems = response.data.result.itemVendors
          self.nonVendorItems = self.itemTypes.filter(item => {
            return !self.vendorItems.some(vItem => {
              return item.id === vItem.itemType.id
            })
          })
        })
        .catch(() => {})
    },
    storeRoomChangeActions(id) {
      let selectedStoreRoom = this.storeRooms.filter(store => {
        return store.id === parseInt(id)
      })
      if (this.model.shipToAddress && selectedStoreRoom[0].location) {
        this.model.shipToAddress.city = selectedStoreRoom[0].location.city
        this.model.shipToAddress.state = selectedStoreRoom[0].location.state
        this.model.shipToAddress.street = selectedStoreRoom[0].location.street
        this.model.shipToAddress.country = selectedStoreRoom[0].location.country
        this.model.shipToAddress.zip = selectedStoreRoom[0].location.zip
      }
    },
    isApprovalNeededChangeActions() {
      if (!this.model['isApprovalNeeded']) {
        this.model['isGatePassRequired'] = false
      }
    },
    isRotatingChangeActions() {
      if (!this.model['isRotating']) {
        this.model['isConsumable'] = true
      }
    },
    rotatingItemToolChangeActions() {
      if (this.model.rotatingItem.id > 0) {
        this.model.rotatingTool.id = null
      }
      if (this.model.rotatingTool.id > 0) {
        this.model.rotatingItem.id = null
      }
    },
    lineItemChangeActionItem(val) {
      if (val.itemType && val.itemType.id && this.model.vendor.id) {
        let param = {
          vendorId: this.model.vendor.id,
          inventoryType: 1,
          itemTypeId: val.itemType.id,
        }
        this.$http
          .post('/v2/purchasecontract/getActiveContractPrice', param)
          .then(response => {
            if (response.data.responseCode === 0) {
              val.unitPrice = response.data.result.unitPrice
            }
          })
          .catch(() => {})
      }
    },
    lineItemChangeActionTool(val) {
      if (val.toolType && val.toolType.id && this.model.vendor.id) {
        let param = {
          vendorId: this.model.vendor.id,
          inventoryType: 2,
          toolTypeId: val.toolType.id,
        }
        this.$http
          .post('/v2/purchasecontract/getActiveContractPrice', param)
          .then(response => {
            if (response.data.responseCode === 0) {
              val.unitPrice = response.data.result.unitPrice
            }
          })
          .catch(() => {})
      }
    },
    async loadAssetPickListData() {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'asset', skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.assets = options
      }
    },
    async loadVendorPickListData() {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'vendors' },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.vendors = options
      }
    },
    async loadServicePickListData() {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'services' },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.services = options
      }
    },
    saveLongDescriptionData(data) {
      this.model['longDesc'] = data
      this.addLongDescriptionVisibility = false
    },
    paymentRadioGroupChangeActions(model) {
      model.paymentInterval = null
      model.scheduleTime = null
      model.scheduleDay = null
      model.scheduleMonth = null
    },
    isSelectCreateNew(id) {
      return !isEmpty(id) && isNaN(id)
    },
    fillVisitorObject(field) {
      if (!this.isSelectCreateNew(field.id)) {
        let visitor = this.visitors.find(i => i.id === field.id)
        field.phone = visitor.phone
        field.email = visitor.email
      } else {
        field.phone = null
        field.email = null
      }
    },
    resetIsRecurringValues(isRecurring, fieldName) {
      this.model[fieldName] = {
        isRecurring: isRecurring,
        expectedStartTime: null,
        expectedEndTime: null,
        startDate: null,
        endDate: null,
        startTime: null,
        endTime: null,
        weeks: [
          { dayOfWeek: 1, displayName: 'Monday', value: true },
          { dayOfWeek: 2, displayName: 'Tuesday', value: true },
          { dayOfWeek: 3, displayName: 'Wednesday', value: true },
          { dayOfWeek: 4, displayName: 'Thrusday', value: true },
          { dayOfWeek: 5, displayName: 'Friday', value: true },
          { dayOfWeek: 6, displayName: 'Saturday', value: true },
          { dayOfWeek: 7, displayName: 'Sunday', value: true },
        ],
      }
    },
    permitTypeChangeActions(val) {
      if (Number(val) === 1) {
        this.disableWorkPermitVendorFields = false
      } else {
        this.disableWorkPermitVendorFields = true
        this.$setProperty(this, 'model.vendor.id', '')
        this.$setProperty(this, 'model.vendorContact.id', '')
      }
    },
    permitVendorChangeActions(val, init) {
      if (!isEmpty(val)) {
        let filter = {
          vendor: {
            operatorId: 36,
            value: [val + ''],
          },
        }
        this.$http
          .get(
            '/v2/contacts/all' +
              '?filters=' +
              encodeURIComponent(JSON.stringify(filter))
          )
          .then(response => {
            if (response.data.responseCode === 0) {
              this.$set(
                this,
                'vendorContacts',
                response.data.result.contacts || []
              )
            }
          })
      }
      if (!init) {
        this.$setProperty(this, 'model.vendorContact.id', '')
      }
    },
    fillLookUpMeta(field, modifier, promises, y) {
      field.options = []
      this.pickListData[field.lookupModuleName] = true
      let isPromise = this.loadPickList(field.lookupModuleName, field).then(
        _ => {
          modifier[y].push([
            {
              displayTypeEnum: field.displayTypeEnum,
              name: 'id',
              parent: field.name,
              displayName: field.displayName,
              required: field.required,
              options: field.options,
              span: field.span,
              allowCreate: field.allowCreate,
              lookupModuleName: field.lookupModuleName,
              createFormName: field.createFormName,
            },
          ])
        }
      )
      if (isPromise) {
        promises.push(isPromise)
      }
    },
  },
}
</script>
<style>
.task-inner {
  border-top: 1px solid transparent;
  border-bottom: 1px solid #e3eaf1;
  padding-top: 7px;
  padding-bottom: 10px;
}
.task-inner:hover {
  background-color: #f9feff;
  border: 0;
  border-top: 1px solid #9ed7de;
  border-bottom: 1px solid #9ed7de;
}
.fc-form-space-input .el-input__inner {
  border-right: none !important;
  border-radius: 0;
  padding-right: 5px;
  border-top-left-radius: 3px;
  border-bottom-left-radius: 3px;
  padding-left: 8px;
}
.fc-form-space-input .el-input-group__append {
  background: none;
  padding-left: 0;
  border: none;
  padding-right: 10px;
}
.fc-form-space-input .el-button--default {
  background: none;
}
.wo-task-in {
  text-align: right;
  padding-right: 10px;
  text-overflow: ellipsis;
  letter-spacing: 0.5px;
  white-space: nowrap;
  overflow: hidden;
  position: absolute;
  left: 10px;
  top: 3px;
}
.task-section .el-form-item.is-success .el-input__inner:focus,
.task-input-untitled .el-input__inner {
  border-color: none;
  border-bottom: none;
}
.duration-input .el-input__inner {
  -moz-appearance: textfield;
}
.input-upload {
  background-color: #f6feff;
  border: none;
  border-radius: 0%;
  margin-top: 10px;
  color: #fff;
  cursor: pointer;
  overflow: hidden;
  white-space: nowrap;
  position: relative;
  display: inline-block;
  margin-top: 0;
  width: 130px;
  height: 130px;
  border-style: dotted;
  border: 2px dashed #8cd9e3;
}
.input-upload .upload {
  position: absolute;
  top: 0;
  right: 0;
  margin: 0;
  padding: 0;
  font-size: 20px;
  cursor: pointer;
  opacity: 0;
  filter: alpha(opacity=0);
  cursor: pointer;
  font-size: 10px;
  left: 0;
  z-index: 1;
}
.logo-upload .upload-img {
  width: 24px;
  height: 24px;
  position: absolute;
  top: 40px;
  left: 40%;
  vertical-align: middle;
}
.upload-img2 {
  width: 130px;
  height: 130px;
  vertical-align: middle;
  position: absolute;
  top: 0;
  left: 0;
  overflow: hidden;
}
.tenant-upload-con .input-upload {
  border-radius: 0;
}
.input-upload .upload-icon-block {
  color: #fff;
}
.web-form-image-body {
  width: 50%;
  position: relative;
  background-color: #fff;
  padding: 0;
  border: solid 1px #d0d9e2;
  font-size: 0.8rem;
  text-align: left;
  transition: outline-offset 0.15s ease-in-out 0s,
    background-color 0.15s linear 0s;
  color: #333333;
  cursor: pointer;
  border-radius: 3px;
  padding: 0 10px;
}
.web-form-image-body .upload-img {
  width: 30px;
  height: 30px;
  top: 12px;
  position: relative;
}
.web-form-image-body .input-file-image,
.web-form-image-uploaded .input-file-image {
  opacity: 0;
}
.web-form-image-uploaded {
  height: 160px;
  width: 250px;
  position: relative;
  text-align: left;
}

.web-form-image-uploaded img {
  max-height: 140px;
  max-width: 240px;
}
.margin-top-negative-50 {
  margin-top: -50px;
}
</style>
