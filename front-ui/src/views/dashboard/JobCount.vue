<template>
  <a-card :title="'任务统计'" :loading="loading" style="margin-bottom: 24px;border-radius: 8px;">
    <template v-slot:extra>
      <a-menu mode="horizontal" v-model="selectedDate" @click="handleMenuClick">
        <a-menu-item key="week">{{ $t('dashboard.analysis.all-week') }}</a-menu-item>
        <a-menu-item key="month">{{ $t('dashboard.analysis.all-month') }}</a-menu-item>
        <a-menu-item key="year">{{ $t('dashboard.analysis.all-year') }}</a-menu-item>
      </a-menu>
    </template>
    <div class="job-count">
      <a-row :gutter="16" style="border: 1px solid #e8e8e8; border-radius: 8px; padding: 24px;">
        <a-col :span="8">
          <number-info
            :total="totalJobs"
            :sub-total="Math.abs(compareJobCount)"
            :status="compareJobCount > 0 ? 'up' : 'down'">
            <span slot="subtitle">
              <span>总任务</span>
            </span>
          </number-info>
        </a-col>
        <a-col :span="8">
          <number-info
            :total="successJobs"
            :sub-total="Math.abs(computeSuccessJobCount)"
            :status="compareJobCount > 0 ? 'up' : 'down'">
            <span slot="subtitle">
              <span>成功任务</span>
            </span>
          </number-info>
        </a-col>
        <a-col :span="8">
          <number-info
            :total="failedJobs"
            :sub-total="Math.abs(computeFailedJobCount)"
            :status="computeFailedJobCount > 0 ? 'up' : 'down'">
            <span slot="subtitle">
              <span>失败任务</span>
            </span>
          </number-info>
        </a-col>
      </a-row>
      <a-row :gutter="68" style="display: flex">
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
              <!-- 堆叠面积图 -->
              <v-stack-area position="date*success" color="#1890FF" :adjust="[{ type: 'stack' }]" />
              <v-stack-area position="date*failed" color="#2FC25B" :adjust="[{ type: 'stack' }]" />

              <v-axis dataKey="date" :visible="true" />
              <v-axis dataKey="value" :visible="true" />
            </v-chart>
          </div>
          <div class="dataTable">
            <a-table
              row-key="index"
              size="small"
              :columns="[
                { title: '日期', dataIndex: 'date', key: 'date' },
                { title: '成功任务', dataIndex: 'success', key: 'success' },
                { title: '失败任务', dataIndex: 'failed', key: 'failed' }
              ]"
              :dataSource="areaChartData"
              :pagination="{ pageSize: 5 }"
              :rowKey="record => record.date" >
            </a-table>
          </div>
        </a-col>
        <a-col :xs="24" :sm="12" :style="{ marginBottom: '24px' }">
          <!-- 饼图 -->
          <div class="ant-pro-pie" style="height: 100%" ref="pieChart">
            <v-chart
              :force-fit="true"
              :height="pieHeightData"
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
    <div style="text-align: right;">
      <a-range-picker
        :allowClear="false"
        v-model="pickDate"
        :style="{width: '256px'}"
        @change="selectedDate=[];jobInit()" />
    </div>
  </a-card>
</template>
<script>
import { NumberInfo } from '@/components'
import moment from 'moment/moment'
import { jobCount } from '@/api/job/joblog'

export default {
  name: 'JobCount',
  components: { NumberInfo },
  data () {
    return {
      pieHeightData: 200,
      selectedDate: ['week'],
      // 默认本周
      pickDate: [moment().startOf('week'), moment().endOf('week')],
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
      areaChartData: [],
      areaScale: [
        {
          dataKey: 'date',
          alias: '年份'
        },
        {
          dataKey: 'success',
          alias: '成功任务'
        },
        {
          dataKey: 'failed',
          alias: '失败任务'
        }
      ],
      // 饼图数据
      pieChartData: [],
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
  methods: {
    jobInit () {
      this.loading = true
      const dateFrom = this.pickDate[0].format('YYYY-MM-DD')
      const dateTo = this.pickDate[1].format('YYYY-MM-DD')
      jobCount({ dateFrom, dateTo }).then(res => {
        this.areaChartData = res.result
        if (this.areaChartData.length === 0) {
          this.areaChartData = [
            { date: moment().subtract(1, 'days').format('YYYY-MM-DD'), success: 0, failed: 0 },
            { date: moment().format('YYYY-MM-DD'), success: 0, failed: 0 }
          ]
          this.pieChartData = []
        } else {
          this.pieChartData = [
            { type: '成功任务', value: this.successJobs },
            { type: '失败任务', value: this.failedJobs }
          ]
        }
        this.pieHeight()
        this.loading = false
      }).catch(err => {
        console.error(err)
        this.loading = false
      })
    },
    handleMenuClick (e) {
      this.selectedDate = e.key
      this.pickDate = [moment().startOf(e.key), moment().endOf(e.key)]
      this.jobInit()
    },
    pieHeight () {
      this.$nextTick(() => {
        this.pieHeightData = this.$refs.pieChart?.clientHeight || 200
      })
    }
  },
  mounted () {
    this.jobInit()
  },
  computed: {
    // 计算总任务数
    totalJobs () {
      return this.successJobs + this.failedJobs
    },
    // 计算成功任务数
    successJobs () {
      return this.areaChartData.reduce((acc, cur) => {
        return acc + (cur.success || 0) // 累加当前项的 success 值
      }, 0) // 初始值为 0
    },
    // 计算失败任务数
    failedJobs () {
      return this.areaChartData.reduce((acc, cur) => {
        return acc + (cur.failed || 0) // 累加当前项的 failed 值
      }, 0) // 初始值为 0
    },
    computeSuccessJobCount () {
      if (this.areaChartData.length > 1) {
        return this.areaChartData[0].success - this.areaChartData[1].success
      }
      if (this.areaChartData.length === 1) {
        return this.areaChartData[0].success
      }
      if (this.areaChartData.length === 0) {
        return 0
      }
      return 0
    },
    computeFailedJobCount () {
      if (this.areaChartData.length > 1) {
        return this.areaChartData[0].failed - this.areaChartData[1].failed
      }
      if (this.areaChartData.length === 1) {
        return this.areaChartData[0].failed
      }
      if (this.areaChartData.length === 0) {
        return 0
      }
      return 0
    },
    compareJobCount () {
      return this.computeSuccessJobCount + this.computeFailedJobCount
    }
  }
}
</script>
<style scoped lang="less">
ul.ant-menu.ant-menu-horizontal.ant-menu-root.ant-menu-light {
  background-color: initial;
}
::v-deep span.ant-calendar-picker-input.ant-input{
  background-color: unset;
  background-image: none;
  border: 1px solid #ff0000;
  border-radius: 8px;
}
</style>
