<template>
  <a-card :loading="loading" :bordered="false" :title="'DeepSeek用量信息'" style="margin-bottom: 24px;border-radius: 8px;">
    <template v-slot:extra>
      <a-menu mode="horizontal" v-model="selectedDate" @click="handleMenuClick">
        <a-menu-item key="week">{{ $t('dashboard.analysis.all-week') }}</a-menu-item>
        <a-menu-item key="month">{{ $t('dashboard.analysis.all-month') }}</a-menu-item>
        <a-menu-item key="year">{{ $t('dashboard.analysis.all-year') }}</a-menu-item>
      </a-menu>
    </template>
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
                <v-axis dataKey="date" :visible="true"/>
                <v-axis dataKey="count" :visible="true"/>
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
                <v-axis dataKey="date" :visible="true"/>
                <v-axis dataKey="count" :visible="true"/>
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
    <div style="text-align: right;">
      <a-range-picker :allowClear="false" v-model="pickDate" :style="{width: '256px'}" @change="selectedDate=[];deepseekinit()"/>
    </div>
  </a-card>
</template>

<script>
import { baseMixin } from '@/store/app-mixin'
import { MiniSmoothArea, NumberInfo } from '@/components'
import moment from 'moment'
import { deepseekApiCount, deepseekTokenCount } from '@/api/deepseek/api'

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
    }
  },
  data () {
    return {
      loading: true,
      selectedDate: ['week'],
      // 默认本周
      pickDate: [moment().startOf('week'), moment().endOf('week')],
      deepseekChat: 'deepseek-chat',
      deepseekReasoner: 'deepseek-reasoner',
      deepseekChatApiCountData: [],
      deepseekChatTokenCountData: [],
      deepseekReasonerApiCountData: [],
      deepseekReasonerTokenCountData: [],
      apiScale: [{ dataKey: 'date', alias: '时间' }, { dataKey: 'count', alias: '次数' }],
      tokenScale: [
        { dataKey: 'date', alias: '日期' },
        { dataKey: 'value', alias: '数值' }
      ]
    }
  },
  mounted () {
    this.deepseekinit()
  },
  methods: {
    handleMenuClick (e) {
      this.selectedDate = e.key
      this.pickDate = [moment().startOf(e.key), moment().endOf(e.key)]
      this.deepseekinit()
    },
    deepseekinit () {
      this.loading = true
      const dateFrom = this.pickDate[0].format('YYYY-MM-DD')
      const dateTo = this.pickDate[1].format('YYYY-MM-DD')
      // 发送多个 API 请求并等待它们全部完成
      Promise.all([
        this.deepseekApiCount(this.deepseekChat, dateFrom, dateTo),
        this.deepseekApiCount(this.deepseekReasoner, dateFrom, dateTo),
        this.deepseekTokenCount(this.deepseekChat, dateFrom, dateTo),
        this.deepseekTokenCount(this.deepseekReasoner, dateFrom, dateTo)
      ]).then(() => {
        this.loading = false // 所有请求完成后关闭 loading
      }).catch(error => {
        console.error('API 请求失败：', error)
        this.loading = false // 即使失败，也要关闭 loading
      })
    },
    deepseekApiCount (model, dateFrom, dateTo) {
      return deepseekApiCount({ model: model, dateFrom: dateFrom, dateTo: dateTo }).then((res) => {
        const list = res.result
        if (list.length === 0) {
          list.push({
            date: moment().subtract(1, 'days').format('YYYY-MM-DD'),
            count: 0,
            model: model
          })
          list.push({
            date: moment().format('YYYY-MM-DD'),
            count: 0,
            model: model
          })
        }
        if (model === 'deepseek-chat') {
          this.deepseekChatApiCountData = list
        } else {
          this.deepseekReasonerApiCountData = list
        }
      })
    },
    deepseekTokenCount (model, dateFrom, dateTo) {
      return deepseekTokenCount({ model: model, dateFrom: dateFrom, dateTo: dateTo }).then((res) => {
        let list = res.result
        if (list.length === 0) {
          list.push({
            date: moment().subtract(1, 'days').format('YYYY-MM-DD'),
            promptCacheHitTokens: 0,
            promptCacheMissTokens: 0,
            completionTokens: 0,
            model: model
          })
          list.push({
            date: moment().format('YYYY-MM-DD'),
            promptCacheHitTokens: 0,
            promptCacheMissTokens: 0,
            completionTokens: 0,
            model: model
          })
        }
        // 数据源：长表格式，每个日期对应多个类型和值
        list = list.reduce((acc, cur) => {
          acc.push({
            date: cur.date,
            type: '输入（命中缓存）',
            value: cur.promptCacheHitTokens
          })
          acc.push({
            date: cur.date,
            type: '输入（未命中缓存）',
            value: cur.promptCacheMissTokens
          })
          acc.push({
            date: cur.date,
            type: '输出',
            value: cur.completionTokens
          })
          return acc
        }, [])
        if (model === 'deepseek-chat') {
          this.deepseekChatTokenCountData = list
        } else {
          this.deepseekReasonerTokenCountData = list
        }
      })
    }
  },
  computed: {
    sumChatApiCount () {
      return this.deepseekChatApiCountData.reduce((acc, cur) => acc + cur.count, 0)
    },
    // 计算和昨天相比的增长
    compareChatApiCount () {
      return this.deepseekChatApiCountData.length > 1 && this.deepseekChatApiCountData[0].count - this.deepseekChatApiCountData[1].count
    },
    sumChatTokenCount () {
      return this.deepseekChatTokenCountData.reduce((acc, cur) => acc + cur.value, 0)
    },
    compareChatTokenCount () {
      return this.deepseekChatTokenCountData.reduce(
        (acc, cur) => cur.date === moment().subtract().format('YYYY-MM-DD') ? acc + cur.value : acc, 0) -
        this.deepseekChatTokenCountData.reduce((acc, cur) => cur.date === moment().subtract(1, 'days').format('YYYY-MM-DD') ? acc + cur.value : acc,
        0
      )
    },
    sumReasonerApiCount () {
      return this.deepseekReasonerApiCountData.reduce((acc, cur) => acc + cur.count, 0)
    },
    compareReasonerApiCount () {
      return this.deepseekReasonerApiCountData.length > 1 && this.deepseekReasonerApiCountData[0].count - this.deepseekReasonerApiCountData[1].count
    },
    sumReasonerTokenCount () {
      return this.deepseekReasonerTokenCountData.reduce((acc, cur) => acc + cur.value, 0)
    },
    compareReasonerTokenCount () {
      return this.deepseekReasonerTokenCountData.reduce(
          (acc, cur) => cur.date === moment().subtract().format('YYYY-MM-DD') ? acc + cur.value : acc, 0) -
        this.deepseekReasonerTokenCountData.reduce((acc, cur) => cur.date === moment().subtract(1, 'days').format('YYYY-MM-DD') ? acc + cur.value : acc, 0
        )
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
