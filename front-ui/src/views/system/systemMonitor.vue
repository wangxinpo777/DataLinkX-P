<template>
  <div class="main">
    <a-card title="系统状态" :loading="loading">
      <template slot="extra">
        <a-button type="primary" @click="getSystemMonitorAnalysis({refresh:true})">
          <span>刷新缓存</span>
        </a-button>
      </template>
      <a-descriptions>
        <a-descriptions-item label="磁盘总量">{{ systemStatus.totalDisk }}</a-descriptions-item>
        <a-descriptions-item label="磁盘空闲量">{{ systemStatus.freeDisk }}</a-descriptions-item>
        <a-descriptions-item label="操作系统">{{ systemStatus.systemOs }}</a-descriptions-item>
        <a-descriptions-item label="进程启动时间">{{ systemStatus.programRunTime }}</a-descriptions-item>
        <a-descriptions-item label="进程ID">{{ systemStatus.pid }}</a-descriptions-item>
        <a-descriptions-item label="CPU总数">{{ systemStatus.cpus }}</a-descriptions-item>
        <a-descriptions-item label="JDK version">{{ systemStatus.javaVersion }}</a-descriptions-item>
        <a-descriptions-item label="JVM内存大小">{{ systemStatus.jvmMemorySize }}</a-descriptions-item>
        <a-descriptions-item label="JVM内存已使用">{{ systemStatus.jvmMemoryUsed }}</a-descriptions-item>
        <a-descriptions-item label="机器总内存">{{ systemStatus.machineMemorySize }}</a-descriptions-item>
        <a-descriptions-item label="CPU使用率">{{ systemStatus.cpuUsage }}</a-descriptions-item>
        <a-descriptions-item label="内存使用率">{{ systemStatus.memoryRate }}</a-descriptions-item>
      </a-descriptions>
      <span class="ant-descriptions-item-label ant-descriptions-item-colon">系统运行状态分析</span>
      <a-tooltip placement="right">
        <template v-slot:title>
          数据默认缓存 30分钟，如需获取最新数据，请点击右上角手动刷新。
        </template>
        <a-icon style="margin-left: 8px" type="info-circle" />
      </a-tooltip>
      <a-skeleton active v-if="analysisVisible" />
      <div class="analysis" v-else v-html="markdownRender.render(systemAnalysis)"></div>
    </a-card>
  </div>
</template>
<script>
import { Card } from 'ant-design-vue'
import { getSystemMonitor, getSystemMonitorAnalysis } from '@/api/system/monitor'
import MarkdownIt from 'markdown-it'

export default {
  components: {
    'a-card': Card
  },
  data () {
    return {
      loading: false,
      analysisVisible: false,
      markdownRender: new MarkdownIt({
        html: true,
        linkify: true,
        typographer: true
      }),
      systemAnalysis: '',
      systemStatus: {
        totalDisk: '',
        freeDisk: '',
        systemOs: '',
        programRunTime: '',
        pid: '',
        cpus: '',
        cpuUsage: '',
        javaVersion: '',
        jvmMemorySize: '',
        jvmMemoryUsed: '',
        memoryRate: '',
        machineMemorySize: ''
      }
    }
  },
  methods: {
    getSystemMonitorAnalysis (params) {
      this.analysisVisible = true
      getSystemMonitorAnalysis(params).then(res => {
        this.systemAnalysis = res.result
        this.analysisVisible = false
      }).catch(() => {
        this.analysisVisible = false
      })
    }
  },
  created () {
    this.loading = true
    this.analysisVisible = true
    getSystemMonitor().then(res => {
      this.systemStatus = res.result
      this.loading = false
      getSystemMonitorAnalysis().then(res => {
        this.analysisVisible = false
        this.systemAnalysis = res.result
      }).catch(() => {
        this.analysisVisible = false
      })
    }).catch(() => {
      this.loading = false
    })
  }
}
</script>
<style scoped>
.analysis {
  margin-top: 8px;
  border: 1px solid #e8e8e8;
  padding: 8px 16px;
  border-radius: 8px;
  background-color: #f9f9f9;
  max-width: 45vw;
  min-width: 45vw;
  overflow: auto;
}
</style>
