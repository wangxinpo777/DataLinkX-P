<template>
  <a-card :title="'任务统计'" :loading="loading" style="margin-bottom: 24px;border-radius: 8px;">
    <div class="job-count">
      <a-row :gutter="16">
        <a-col :span="8">
          <number-info
            :total="0"
            :sub-total="Math.abs(0)"
            :status="0 > 0 ? 'up' : 'down'">
            <span slot="subtitle">
              <span>总任务</span>
            </span>
          </number-info>
        </a-col>
        <a-col :span="8">
          <number-info
            :total="0"
            :sub-total="Math.abs(0)"
            :status="0 > 0 ? 'up' : 'down'">
            <span slot="subtitle">
              <span>成功任务</span>
            </span>
          </number-info>
        </a-col>
        <a-col :span="8">
          <number-info
            :total="0"
            :sub-total="Math.abs(0)"
            :status="0 > 0 ? 'up' : 'down'">
            <span slot="subtitle">
              <span>失败任务</span>
            </span>
          </number-info>
        </a-col>
      </a-row>
      <a-row :gutter="68">
        <a-col :xs="24" :sm="12" :style="{ marginBottom: '24px' }">
          <!-- 线性面积图 -->
          <div class="ant-pro-smooth-area">
            <v-chart
              :force-fit="true"
              :height="200"
              :data="areaChartData"
              :scale="areaScale"
              :padding="[40, 40, 40, 40]">
              <v-tooltip />
              <v-smooth-line position="date*value" :size="2" />
              <v-smooth-area position="date*value" />
              <v-axis dataKey="year" :visible="true" />
              <v-axis dataKey="value" :visible="true" />
            </v-chart>
          </div>
        </a-col>
        <a-col :xs="24" :sm="12" :style="{ marginBottom: '24px' }">
          <!-- 饼图 -->
          <div class="ant-pro-pie">
            <v-chart
              :force-fit="true"
              :height="200"
              :data="pieChartData"
              :scale="pieScale"
              :padding="[0, -200, 20, 0]">
              <v-legend data-key="type" :offsetX="100" :offsetY="-20" />
              <v-tooltip data-key="type*value" :showTitle="false" />
              <v-axis />
              <v-pie position="value" color="type" :vStyle="pieStyle" :label="labelConfig"></v-pie>
              <v-coord type="theta" :radius="0.7" :innerRadius="0.6" />
            </v-chart>
          </div>
        </a-col>
      </a-row>
    </div>
  </a-card>
</template>
<script>
import { NumberInfo } from '@/components'

export default {
  name: 'JobCount',
  components: { NumberInfo },
  data () {
    return {
      pieStyle: {
        stroke: '#fff',
        lineWidth: 1
      },
      labelConfig: ['value', {
        formatter: (val, item) => {
          return item.point.type + ': ' + val
        }
      }],
      loading: false,
      // 线性面积图数据
      areaChartData: [
        { date: '2023-10-01', value: 3 },
        { date: '2023-10-02', value: 4 },
        { date: '2023-10-03', value: 3.5 },
        { date: '2023-10-04', value: 5 },
        { date: '2023-10-05', value: 4.9 },
        { date: '2023-10-06', value: 6 },
        { date: '2023-10-07', value: 7 },
        { date: '2023-10-08', value: 9 },
        { date: '2023-10-09', value: 13 }
      ],
      areaScale: [
        {
          dataKey: 'year',
          alias: '年份'
        },
        {
          dataKey: 'value',
          alias: '任务数量',
          min: 0
        }
      ],
      // 饼图数据
      pieChartData: [
        { type: '成功任务', value: 60 },
        { type: '失败任务', value: 40 }
      ],
      pieScale: [
        {
          dataKey: 'value',
          alias: '任务数量'
        },
        {
          dataKey: 'type',
          alias: '任务类型'
        }
      ]
    }
  },
  mounted () {
    this.loading = true
    setTimeout(() => {
      this.loading = false
    }, 500)
  }
}
</script>
<style scoped lang="less">
</style>
