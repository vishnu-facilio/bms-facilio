<template>
  <div class="custom-module-overview">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header p20">
        <div class="custom-module-details">
          <div class="d-flex flex-middle align-center">
            <div @click="showAvatarPreview()">
              <div v-if="record[photoFieldName] > 0">
                <img
                  :src="record.getImage(photoFieldName)"
                  class="item-img-container pointer"
                />
              </div>
              <div v-else>
                <item-avatar
                  :name="false"
                  size="xlg"
                  module="item"
                  :recordData="record"
                ></item-avatar>
              </div>
            </div>
            <div class="mL10">
              <div class="custom-module-id pB10">
                <i
                  v-if="$account.portalInfo"
                  class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                  content="back"
                  arrow
                  v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                  @click="back"
                ></i>
                #{{ record && record.id }}
              </div>
              <div class="custom-module-name d-flex max-width300px">
                <el-tooltip
                  placement="bottom"
                  effect="dark"
                  :content="record[mainFieldKey]"
                >
                  <span class="whitespace-pre-wrap custom-header">{{
                    record[mainFieldKey]
                  }}</span>
                </el-tooltip>
              </div>
            </div>
          </div>
        </div>
        <div class="marginL-auto flex-middle">
          <CustomButton
            class="pR10"
            :record="record"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="refreshData()"
            @onError="() => {}"
          />
          <el-button
            class="fc__add__btn mR15"
            v-if="$hasPermission('inventory:CREATE')"
            @click="openStockItem()"
            >{{ $t('common.header.stock_item') }}</el-button
          >
          <el-button
            v-if="$hasPermission('inventory:UPDATE,UPDATE_OWN')"
            type="button"
            class="fc-wo-border-btn pL15 pR15 self-center"
            @click="editRecord"
          >
            <i class="el-icon-edit"></i>
          </el-button>
        </div>
      </div>
      <Page
        :key="record.id"
        :module="moduleName"
        :id="record.id"
        :details="record"
        :primaryFields="primaryFields"
        :notesModuleName="notesModuleName"
        :isV3Api="true"
        :attachmentsModuleName="attachmentsModuleName"
        :showDescriptionTitle="true"
        :showCategory="true"
      ></Page>
    </template>
    <el-dialog
      v-if="photoId"
      :visible.sync="showPreviewImage"
      width="60%"
      :append-to-body="true"
      style="z-index: 9999999999;"
    >
      <img style="width:100%" :src="record.getImage(photoFieldName)" />
    </el-dialog>
    <stock-item
      :visibility.sync="stockItemVisibility"
      :record="record"
      :moduleName="moduleName"
      @saved="loadRecord(true)"
    ></stock-item>
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import ItemAvatar from '@/avatar/ItemTool'
import StockItem from '../Items/components/StockItem'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: CustomModuleSummary,
  props: ['viewname'],
  components: {
    ItemAvatar,
    StockItem,
  },
  data() {
    return {
      notesModuleName: 'itemTypesNotes',
      attachmentsModuleName: 'itemTypesAttachments',
      primaryFields: [
        'localId',
        'category',
        'photoId',
        'lastPurchasedDate',
        'lastIssuedDate',
        'lastPurchasedPrice',
        'minimumQuantity',
        'description',
      ],
      showPreviewImage: false,
      stockItemVisibility: false,
    }
  },
  computed: {
    moduleName() {
      return 'itemTypes'
    },
    mainFieldKey() {
      return 'name'
    },
    photoId() {
      return this.$getProperty(this, 'record.photoId', null)
    },
    photoFieldName() {
      return 'photoId'
    },
  },
  title() {
    'Item Types'
  },
  methods: {
    openStockItem() {
      this.stockItemVisibility = true
    },
    showAvatarPreview() {
      if (this.photoId) {
        this.showPreviewImage = true
      }
    },
    editRecord() {
      let { id } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
          })
      } else {
        id &&
          this.$router.push({
            name: 'edit-itemType',
            params: { id },
          })
      }
    },
  },
}
</script>
<style lang="scss">
.item-img-container {
  width: 50px;
  height: 50px;
  border: 1px solid #f9f9f9;
  border-radius: 50%;
}
</style>
