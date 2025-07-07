<template>
	<view class="pageBg" />
	<text class="title">{{titleValue}}</text>
	<button class="btn-change btn-font"  @click="changeView()">查看{{otherTitle}}</button>
	<button class="btn-add btn-font"  @click="addCourse()">添加课表</button>
	<button class="btn-exit btn-font"  @click="gotoPage('/pages/index/index')">退出登录</button>
	
	<!-- 周课表视图 - 使用v-if控制显示 -->
	<view class="container" v-if="isWeekView">
		<view class="week-container">
			<view class="week-column" v-for="week in 7" :key="week">
				<view class="week-title">
					{{ ['周一', '周二', '周三', '周四', '周五', '周六', '周日'][week - 1] }}
				</view>
				<view class="course-item" v-for="(item, index) in getCoursesByWeek(week)" :key="index"
				:style="{ backgroundColor: item.isMust === '1' ? '#e6f3ff' : 'white' }">
					<view class="course-title">课程名称：{{ item.name }}</view>
					<view>发布人：{{ item.time }}</view>
					<view>时间：{{ formatTime(item.location) }}</view>
					<view>讲师：{{ item.teacher }}</view>
					<view>课程ID：{{ item.id }}</view>
				</view>
			</view>
		</view>
	</view>

	<!-- 日课表视图 - 使用v-else控制显示 -->
	<view class="container day-view" v-else>
		<view class="day-header">
			<text class="day-title">今日（{{ ['周一', '周二', '周三', '周四', '周五', '周六', '周日'][todayWeek - 1] }}）课表</text>
		</view>
		<view class="day-course-container">
			<view class="course-item" v-for="(item, index) in todayCourses" :key="index"
			:style="{ backgroundColor: item.isMust === '1' ? '#e6f3ff' : 'white' }">
				<view class="course-title">课程名称：{{ item.name }}</view>
				<view>发布人：{{ item.time }}</view>
				<view>时间：{{ formatTime(item.location) }}</view>
				<view>讲师：{{ item.teacher }}</view>
				<view>课程ID：{{ item.id }}</view>
			</view>
			<view class="empty-state" v-if="todayCourses.length === 0">
				今日没有课程安排
			</view>
		</view>
	</view>

	<uni-popup  ref="popupAdd" type="center" v-bind:is-mask-click="true">
		<view class="popContainer">
			<view class="form-group">
				<label>课程名：</label>
				<input type="text"  v-model="addData.addName" placeholder="请输入用户名"/>
			</view>
			
			<view class="form-group">
				<label>时间：</label>
				<input type="text"  v-model="addData.addTime" placeholder="周一十点零分：1-10-00"/>
			</view>
			
			<view class="form-group">
				<label>地点：</label>
				<input type="text"  v-model="addData.addLocation" placeholder="一号楼c201"/>
			</view>
			
			<view class="form-group">
				<label>老师：</label>
				<input type="text"  v-model="addData.addTeacher" placeholder="请输入老师名"/>
			</view>
			
		   <view class="form-group">
		      <label>是否必修：</label>
		      <radio-group v-model="addData.addMust">
		        <label class="radio-item">
		          <radio :value="true" /> 是
		        </label>
		        <label class="radio-item">
		          <radio :value="false" /> 否
		        </label>
		      </radio-group>
		    </view>
			<button class="btn-font btn-comfirmAdd" @click="confirmAdd()">确认添加</button>
		</view>

	</uni-popup>	
</template>

<script setup>
import { ref, onMounted } from 'vue'

const titleValue = ref('周课表')
const otherTitle = ref('日课表')
const popupAdd = ref(null)
const courseList = ref([])
const loading = ref(false)
const addData = ref({
  addName: '',
  addTime: '',
  addLocation: '',
  addTeacher: '',
  addMust: ''
})

// 视图控制
const isWeekView = ref(true)
// 存储今日星期数（1-7）
const todayWeek = ref(0)
// 存储今日课程
const todayCourses = ref([])

function gotoPage(path) {
  uni.navigateTo({ url: path })
}

// 切换视图
function changeView() {
  isWeekView.value = !isWeekView.value
  titleValue.value = isWeekView.value ? '周课表' : '日课表'
  otherTitle.value = isWeekView.value ? '日课表' : '周课表'
}

function addCourse() {
  popupAdd.value.open('center')
}

// 获取课程列表（保持原有逻辑）
const getCourseList = () => {
  loading.value = true
  uni.request({
    url: 'http://localhost:3000/courseList',
    method: 'GET',
    success: (res) => {
      if (res.statusCode === 200) {
        courseList.value = res.data
        // 获取课程数据后，立即过滤出今日课程
        filterTodayCourses()
      }
    },
    complete: () => loading.value = false
  })
}

// 根据星期数筛选课程（保持原有逻辑）
const getCoursesByWeek = (week) => {
  return courseList.value.filter(item => {
    const timeWeek = parseInt(item.location.split('-')[0])
    return timeWeek === week
  })
}

// 格式化时间显示（保持原有逻辑）
const formatTime = (timeStr) => {
  const parts = timeStr.split('-')
  return `${parts[1]}:${parts[2]}`
}

// 过滤今日课程
const filterTodayCourses = () => {
  todayCourses.value = courseList.value.filter(item => {
    const timeWeek = parseInt(item.location.split('-')[0])
    return timeWeek === todayWeek.value
  })
}

onMounted(() => {
  // 获取今日星期数
  const now = new Date()
  let day = now.getDay()
  todayWeek.value = day === 0 ? 7 : day
  
  getCourseList()
})
</script>

<style lang="scss" scoped>
// 完全保持原有样式不变
page{
			background-image: url('/static/image/bk.png');
			background-size: cover;
	}
	.title{
		position: absolute;
			top: 16px;
			left: 24px;
		width: 112px;
		height: 48px;
		
		font-family: "PingFang SC", sans-serif;
		font-weight: 600;
		font-size: 28px;
		line-height: 48px;
		letter-spacing: 0;
		color: #1D2129;
	}
	.btn-font{
		background-color: #F2F3F5;
		border-radius: 1000px;
		border: 2px solid #C9CDD4; /* 修正 border 写法 */
		box-shadow: 0 2px 4px 0 rgba(88, 127, 255, 0.5); 
		
		font-family: "PingFang SC", sans-serif;
		font-weight: 500;
		font-size: 20px;
		line-height: 28px;
		letter-spacing: 0;
		color: #1D2129;
		
		display: flex;
			justify-content: center;
			align-items: center;
			
		 &:active { /* ✅ 正确的伪类选择器 */
			color: #FFFFFF;
			background-image: linear-gradient( #0085FF, #212AFF); 
			border: 2px solid #0E42D2; /* 修正 border 写法 */
			box-shadow: 0 0 12px 0 #1FE4FF; 
		  }
	}
	.btn-change{
		width: 188px;
		height: 48px;
		position: absolute;
			top: 16px;
			right: 24px;
	}
	.btn-add{
		width: 188px;
		height: 48px;
		position: absolute;
			top: 636px;
			right: 24px;
	}
	.btn-exit{
		width: 188px;
		height: 48px;
		position: absolute;
			top: 636px;
			left: 24px;
	}
	.btn-comfirmAdd{
		width: 188px;
		height: 48px;
		position: absolute;
			top: 300px;
			right: 24px;
	}
	

	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}
	
	.popContainer{
		width: 680px;
		height: 332px;
		background-color: #FFFFFF;
		border-radius:12px 12px 12px 12px;
		display: flex;
			flex-direction: column;
			align-items: left;
			justify-content: center;
			padding: 40px;
			gap: 20px;
	}
	.form-group {
	  margin-bottom: 30rpx;
	  font-family: "PingFang SC", sans-serif;
	  font-weight: 500;
	  font-size: 20px;
	  line-height: 28px;
	  letter-spacing: 0;
	  color: #1D2129;
	}

	.container{
		position: absolute;
			top: 80px;
			left: 24px;
			border-radius: 12px;
		width: 1232px;
		height: 540px;
		background-color: #FFFFFF;
	}
// .container {
//   padding: 24px;
// }

/* 横向排列周一到周日 */
.week-container {
  display: flex;
  gap: 10px; /* 列之间的间距 */
  overflow-x: auto; /* 超出宽度可滚动 */
  padding-bottom: 10px;
}

/* 每列样式 */
.week-column {
  min-width: 200px; /* 每列最小宽度 */
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 10px;
}

.week-title {
  font-weight: bold;
  text-align: center;
  margin-bottom: 10px;
  padding: 5px;
  background-color: #e0e0e0;
  border-radius: 4px;
}

/* 课程项样式 */
.course-item {
  background-color: white;
  border-radius: 6px;
  padding: 10px;
  margin-bottom: 10px;
  font-size: 14px;
}

.course-title {
  font-weight: bold;
  margin-bottom: 5px;
}

// 新增日课表样式 - 尽量保持原有样式结构
.day-view {
  .day-header {
    padding: 16px 24px;
    font-size: 20px;
    font-weight: bold;
  }
  
  .day-course-container {
    display: flex;
    flex-direction: column;
    padding: 0 24px 24px;
    
    .empty-state {
      text-align: center;
      padding: 40px;
      color: #999;
    }
  }
}
</style>