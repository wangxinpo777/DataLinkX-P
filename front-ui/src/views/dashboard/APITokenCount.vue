<template>
  <div class="ApiTokenCount">
    <p style="margin-bottom: 10px;font-size: 20px">deepseek-chat</p>
    <a-row :gutter="68">
      <a-col :xs="24" :sm="12" :style="{ marginBottom: ' 24px'}">
        <number-info
          :total="sumChatApiCount"
          :sub-total="Math.abs(compareChatApiCount)"
          :status="compareChatApiCount > 0 ? 'up' : 'down'">
          <span slot="subtitle">
            <span>API 请求次数</span>
          </span>
        </number-info>
        <!-- miniChart -->
        <div class="ant-pro-smooth-area">
          <div class="chart-wrapper" :style="{ height: 100 }">
            <v-chart
              :force-fit="true"
              :height="150"
              :data="deepseekChatApiCountData"
              :scale="apiScale"
              :padding="[40, 40, 40, 40]">
              <v-tooltip/>
              <v-smooth-line position="date*count" :size="2"/>
              <v-smooth-area position="date*count"/>
              <v-axis dataKey="x" :visible="true"/><!-- 显示 Y 轴 -->
              <v-axis dataKey="y" :visible="true"/>
            </v-chart>
          </div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :style="{ marginBottom: ' 24px'}">
        <number-info
          :total="sumChatTokenCount"
          :sub-total="Math.abs(compareChatTokenCount)"
          :status="compareChatTokenCount > 0 ? 'up' : 'down'">
          <span slot="subtitle">
            <span>Tokens</span>
          </span>
        </number-info>
        <!-- miniChart -->
        <div class="ant-pro-smooth-area">
          <div class="chart-wrapper" :style="{ height: 100 }">
            <v-chart
              :force-fit="true"
              :height="150"
              :data="deepseekChatTokenCountData"
              :scale="tokenScale"
              :padding="[40, 40, 40, 40]"><!-- 工具提示 -->
              <v-tooltip/><!-- 图例 -->
              <v-legend
                position="top"
                :item-width="null"
                :offset="10"
              /><!-- 堆叠柱状图 -->
              <v-bar
                position="date*value"
                color="type"
                :size="20"
                :adjust="[{ type: 'stack' }]"
              /><!-- X 轴 -->
              <v-axis
                dataKey="date"
                :visible="true"
                :label="{ style: { fill: '#666', fontSize: 12 } }"
              /><!-- Y 轴 -->
              <v-axis
                dataKey="value"
                :visible="true"
                :label="{ style: { fill: '#666', fontSize: 12 }, offset: 20}"
                :tickInterval="200"
              />
            </v-chart>
          </div>
        </div>
      </a-col>
    </a-row>
    <p style="margin-bottom: 10px;font-size: 20px">deepseek-reasoner</p>
    <a-row :gutter="68">
      <a-col :xs="24" :sm="12" :style="{ marginBottom: ' 24px'}">
        <number-info
          :total="sumReasonerApiCount"
          :sub-total="Math.abs(compareReasonerApiCount)"
          :status="compareReasonerApiCount > 0 ? 'up' : 'down'">
          <span slot="subtitle">
            <span>API 请求次数</span>
          </span>
        </number-info>
        <!-- miniChart -->
        <div class="ant-pro-smooth-area">
          <div class="chart-wrapper" :style="{ height: 100 }">
            <v-chart
              :force-fit="true"
              :height="150"
              :data="deepseekReasonerApiCountData"
              :scale="apiScale"
              :padding="[40, 40, 40, 40]">
              <v-tooltip/>
              <v-smooth-line position="date*count" :size="2"/>
              <v-smooth-area position="date*count"/>
              <v-axis dataKey="x" :visible="true"/>
              <!-- 显示 Y 轴 -->
              <v-axis dataKey="y" :visible="true"/>
            </v-chart>
          </div>
        </div>
      </a-col>
      <a-col :xs="24" :sm="12" :style="{ marginBottom: ' 24px'}">
        <number-info
          :total="sumReasonerTokenCount"
          :sub-total="Math.abs(compareReasonerTokenCount)"
          :status="compareReasonerTokenCount > 0 ? 'up' : 'down'">
          <span slot="subtitle">
            <span>Tokens</span>
          </span>
        </number-info>
        <!-- miniChart -->
        <div class="ant-pro-smooth-area">
          <div class="chart-wrapper" :style="{ height: 100 }">
            <v-chart
              :force-fit="true"
              :height="150"
              :data="deepseekReasonerTokenCountData"
              :scale="tokenScale"
              :padding="[40, 40, 40, 40]"><!-- 工具提示 -->
              <v-tooltip/><!-- 图例 -->
              <v-legend
                position="top"
                :item-width="null"
                :offset="10"
              /><!-- 堆叠柱状图 -->
              <v-bar
                position="date*value"
                color="type"
                :size="20"
                :adjust="[{ type: 'stack' }]"
              /><!-- X 轴 -->
              <v-axis
                dataKey="date"
                :visible="true"
                :label="{ style: { fill: '#666', fontSize: 12 } }"
              /><!-- Y 轴 -->
              <v-axis
                dataKey="value"
                :visible="true"
                :label="{ style: { fill: '#666', fontSize: 12 },offset: 20 }"
                :tickInterval="200"
              />
            </v-chart>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import { baseMixin } from '@/store/app-mixin'
import {
  NumberInfo,
  MiniSmoothArea
} from '@/components'

export default {
  name: 'ApiTokenCount',
  mixins: [baseMixin],
  components: {
    NumberInfo,
    MiniSmoothArea
  },
  props: {
    prefixCls: {
      type: String,
      default: 'ant-pro-smooth-area'
    },
    apiScale: {
      type: [Object, Array],
      required: true
    },
    tokenScale: {
      type: [Object, Array],
      required: true
    },
    deepseekChatApiCountData: {
      type: Array,
      required: true
    },
    deepseekChatTokenCountData: {
      type: Array,
      required: true
    },
    deepseekReasonerApiCountData: {
      type: Array,
      required: true
    },
    deepseekReasonerTokenCountData: {
      type: Array,
      required: true
    }
  },
  computed: {
    sumChatApiCount () {
      return this.deepseekChatApiCountData.reduce((acc, cur) => acc + cur.count, 0)
    },
    // 计算和昨天相比的增长
    compareChatApiCount () {
      return this.deepseekChatApiCountData[this.deepseekChatApiCountData.length - 1].count - this.deepseekChatApiCountData[this.deepseekChatApiCountData.length - 2].count
    },
    sumChatTokenCount () {
      return this.deepseekChatTokenCountData.reduce((acc, cur) => acc + cur.value, 0)
    },
    compareChatTokenCount () {
      return this.deepseekChatTokenCountData[this.deepseekChatTokenCountData.length - 1].value - this.deepseekChatTokenCountData[this.deepseekChatTokenCountData.length - 2].value
    },
    sumReasonerApiCount () {
      return this.deepseekReasonerApiCountData.reduce((acc, cur) => acc + cur.count, 0)
    },
    compareReasonerApiCount () {
      return this.deepseekReasonerApiCountData[this.deepseekReasonerApiCountData.length - 1].count - this.deepseekReasonerApiCountData[this.deepseekReasonerApiCountData.length - 2].count
    },
    sumReasonerTokenCount () {
      return this.deepseekReasonerTokenCountData.reduce((acc, cur) => acc + cur.value, 0)
    },
    compareReasonerTokenCount () {
      return this.deepseekReasonerTokenCountData[this.deepseekReasonerTokenCountData.length - 1].value - this.deepseekReasonerTokenCountData[this.deepseekReasonerTokenCountData.length - 2].value
    }
  }
}
</script>

<style scoped>
/* 针对 data-icon="caret-down" 的样式 */
::v-deep [data-icon="caret-down"] {
  color: red; /* 设置颜色为红色 */

}
/* 针对 data-icon="caret-up" 的样式 */
::v-deep [data-icon="caret-up"] {
  color: green; /* 设置颜色为绿色 */
}
</style>
