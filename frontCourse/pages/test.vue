<template>
	<view class="pageBg" />
	
	<!-- 课程提醒框 -->
	<view class="reminder-box" v-if="showReminder" :style="{ display: showReminder ? 'flex' : 'none' }">
		<text class="reminder-text">30分钟后有课程：{{ reminderCourseName }}</text>
		<button class="reminder-close" @click="closeReminder">×</button>
	</view>
	
	<text class="title">{{titleValue}}</text>
	<button class="btn-change btn-font"  @click="changeView()">查看{{otherTitle}}</button>
	<button class="btn-add btn-font"  @click="addCourse()">添加课表</button>
	<button class="btn-exit btn-font"  @click="gotoPage('/pages/index/index')">退出登录</button>
	
	<!-- 周课表视图 -->
	<view class="container" v-if="isWeekView">
		<view class="week-container">
			<view class="week-column" v-for="week in 7" :key="week">
				<view class="week-title">
					{{ ['周一', '周二', '周三', '周四', '周五', '周六', '周日'][week - 1] }}
				</view>
				<view class="course-item" v-for="(item, index) in getCoursesByWeek(week)" :key="index"
				:style="{ backgroundColor: item.categoryId === '1' ? '#e6f3ff' : 'white' }">
					<view class="course-title">课程名称：{{ item.courseName }}</view>
					<view>时间：{{ formatTime(item.dayOfWeek)}}</view>
					<view>地点：{{ item.location}}</view>
					<view>讲师：{{ item.teacher }}</view>
					<view>课程ID：{{ item.courseId }}</view>
				</view>
			</view>
		</view>
	</view>
	<!-- 日课表视图 -->
	<view class="container day-view" v-else>
		<view class="day-header">
			<text class="day-title">今日（{{ ['周一', '周二', '周三', '周四', '周五', '周六', '周日'][todayWeek - 1] }}）课表</text>
		</view>
		<view class="day-course-container">
			<view class="course-item" v-for="(item, index) in todayCourses" :key="index"
			:style="{ backgroundColor: item.categoryId === '1' ? '#e6f3ff' : 'white' }">
					<view class="course-title">课程名称：{{ item.courseName }}</view>
					<view>时间：{{ formatTime(item.dayOfWeek)}}</view>
					<view>地点：{{ item.location}}</view>
					<view>讲师：{{ item.teacher }}</view>
					<view>课程ID：{{ item.courseId }}</view>
			</view>
			<view class="empty-state" v-if="todayCourses.length === 0">
				今日没有课程安排
			</view>
		</view>
	</view>

	<uni-popup  ref="popupAdd" type="center" v-bind:is-mask-click="true">
		<view class="popContainer">
			<label>课程名：</label>
			<input type="text"  v-model="addData.courseName" placeholder="请输入课程名"/>
			<label>时间：</label>
			<input type="text"  v-model="addData.dayOfWeek" placeholder="周一十点零分：1-10-00"/>
			<label>地点：</label>
			<input type="text"  v-model="addData.location" placeholder="一号楼c201"/>
			<label>老师：</label>
			<input type="text"  v-model="addData.teacher" placeholder="请输入老师名"/>
			<label>课程编号：</label>
			<input type="text"  v-model="addData.courseId" placeholder="请输入课程编号"/>
			<label>是否必修：</label>
			<input type="text"  v-model="addData.categoryId" placeholder="是：1，否：0"/>
			<button class="btn-font btn-comfirmAdd" @click="confirmAdd()">确认添加</button>
			<button class="btn-font btn-comfirmAdd" @click="excelUpload()">excel批量添加</button>
		</view>
	</uni-popup>	
</template>

<script setup>
	import { ref, onMounted, onUnmounted, watchEffect } from 'vue'
	import { onLoad } from '@dcloudio/uni-app'
	import * as XLSX from 'xlsx'

	// 提醒功能相关变量
	const showReminder = ref(false)
	const reminderCourseName = ref('')
	let reminderTimer = null

	// 课程数据和视图控制
	const titleValue = ref('周课表')
	const otherTitle = ref('日课表')
	const popupAdd = ref(null)
	const courseList = ref([])
	const studentIdT = ref('')

	// 视图控制
	const isWeekView = ref(true)
	const todayWeek = ref(0)
	const todayCourses = ref([])

	// 页面导航
	function gotoPage(path) {
	  uni.navigateTo({ url: path })
	}

	// 切换视图
	function changeView() {
	  isWeekView.value = !isWeekView.value
	  titleValue.value = isWeekView.value ? '周课表' : '日课表'
	  otherTitle.value = isWeekView.value ? '日课表' : '周课表'
	}

	// 打开添加课程弹窗
	function addCourse() {
	  popupAdd.value.open('center')
	}

	// 关闭提醒框
	function closeReminder() {
	  showReminder.value = false
	}

	// 获取课程列表
	const getCourseList = () => {
	  uni.request({
		url: `http://localhost:3001/courseList?studentId=${studentIdT.value}`,
		method: 'GET',
		success: (res) => {
		  if (res.statusCode === 200) {
			courseList.value = res.data
			filterTodayCourses()
			console.log('课程列表加载成功，共', courseList.value.length, '门课程')
			setCourseReminders()
			checkImmediateReminders() // 立即检查是否有需要提醒的课程
		  } else {
			console.error('获取课程列表失败:', res)
		  }
		},
		fail: (err) => {
		  console.error('网络请求失败:', err)
		}
	  })
	}

	// 根据星期筛选课程
	const getCoursesByWeek = (week) => {
	  return courseList.value.filter(item => {
		const timeWeek = parseInt(item.dayOfWeek.split('-')[0])
		return timeWeek === week
	  })
	}

	// 格式化时间显示
	const formatTime = (timeStr) => {
	  const parts = timeStr.split('-')
	  return `${parts[1]}:${parts[2]}`
	}

	// 过滤今日课程
	const filterTodayCourses = () => {
		todayCourses.value = courseList.value.filter(item => {
			const timeWeek = parseInt(item.dayOfWeek.split('-')[0])
			return timeWeek === todayWeek.value
		})
		console.log('今日课程数量:', todayCourses.value.length)
	}

	// 解析课程时间
	const parseCourseTime = (timeStr) => {
		if (!timeStr || typeof timeStr !== 'string') return null
		
		const [weekDay, hour, minute] = timeStr.split('-').map(Number)
		
		if (isNaN(weekDay) || isNaN(hour) || isNaN(minute)) {
			console.warn('无效的时间格式:', timeStr)
			return null
		}
		
		// 确保时间格式有效
		if (weekDay < 1 || weekDay > 7 || hour < 0 || hour > 23 || minute < 0 || minute > 59) {
			console.warn('时间值超出范围:', timeStr)
			return null
		}
		
		// 创建课程时间对象
		const now = new Date()
		const courseTime = new Date(now)
		
		// 设置课程的星期和时间
		// 注意：JavaScript中getDay()返回0(周日)到6(周六)，而我们的weekDay是1(周一)到7(周日)
		let jsWeekDay = weekDay % 7 // 转换为0-6
		if (jsWeekDay === 0) jsWeekDay = 7 // 周日在我们的系统中是7
		
		// 如果设置的星期已经过去，则设置为下周
		if (jsWeekDay < now.getDay()) {
			courseTime.setDate(courseTime.getDate() + 7 - (now.getDay() - jsWeekDay))
		} else if (jsWeekDay === now.getDay()) {
			// 如果是今天，但时间已经过去，则设置为下周
			if (courseTime.getHours() > hour || (courseTime.getHours() === hour && courseTime.getMinutes() > minute)) {
				courseTime.setDate(courseTime.getDate() + 7)
			}
		} else {
			courseTime.setDate(courseTime.getDate() + (jsWeekDay - now.getDay()))
		}
		
		courseTime.setHours(hour, minute, 0)
		
		return courseTime > now ? courseTime : null
	}

	// 设置课程提醒
	const setCourseReminders = () => {
		// 清除已有定时器
		if (reminderTimer) {
			clearInterval(reminderTimer)
			console.log('清除已有提醒定时器')
		}
		
		// 如果没有今日课程，不设置提醒
		if (todayCourses.value.length === 0) {
			console.log('今日没有课程，不设置提醒')
			return
		}
		
		// 每分钟检查一次
		reminderTimer = setInterval(() => {
			console.log('检查课程提醒...')
			checkImmediateReminders()
		}, 60000)
		
		console.log('已设置课程提醒定时器，每分钟检查一次')
	}

	// 立即检查是否有需要提醒的课程
	const checkImmediateReminders = () => {
		console.log('立即检查课程提醒...')
		const now = new Date()
		
		// 检查每门今日课程
		for (const course of todayCourses.value) {
			const courseTime = parseCourseTime(course.dayOfWeek)
			
			if (!courseTime) {
				console.warn('无法解析课程时间:', course.dayOfWeek)
				continue
			}
			
			// 计算时间差（分钟）
			const diffMinutes = Math.floor((courseTime - now) / (1000 * 60))
			
			// 30分钟内的课程触发提醒
			if (diffMinutes >= 0 && diffMinutes <= 30) {
				console.log(`课程"${course.courseName}"将在${diffMinutes}分钟后开始，触发提醒`)
				reminderCourseName.value = course.courseName
				showReminder.value = true
				// 只提醒一次，找到第一个符合条件的课程后退出循环
				break
			}
		}
	}

	// 添加课程数据
	const addData = ref({
		courseName: '',
		dayOfWeek: '',
		location: '',
		teacher: '',
		courseId: '',
		categoryId: ''
	})

	// 确认添加课程
	const confirmAdd = () => {
		uni.request({
			url: `http://localhost:3001/courseList?studentId=${studentIdT.value}`,
			method: 'POST',
			data: addData.value,
			success: (res) => {
				if (res.statusCode === 201) {
					uni.showToast({ title: '添加成功', icon: 'success' })
					getCourseList() // 添加后刷新课程列表和提醒
				} else {
					uni.showToast({ title: '添加失败', icon: 'none' })
				}
			},
			fail: () => {
				uni.showToast({ title: '网络异常，请检查连接', icon: 'none' })
			}
		})
	}

	// Excel批量导入课程
	const excelUpload = () => {
		uni.chooseFile({
			count: 1,
			extension: ['.xlsx', '.xls'],
			success: (res) => {
				const file = res.tempFiles[0]
				if (!file) return

				const reader = new FileReader()
				reader.onload = (e) => {
					try {
						const data = e.target.result
						const workbook = XLSX.read(data, { type: 'binary' })
						const worksheet = workbook.Sheets[workbook.SheetNames[0]]
						const jsonData = XLSX.utils.sheet_to_json(worksheet)
						
						const formattedData = jsonData.map(item => ({
							courseName: item.课程名称 || '',
							dayOfWeek: item.时间 || '',
							location: item.地点 || '',
							teacher: item.老师 || '',
							courseId: item.课程编号 || '',
							categoryId: item.是否必修 === '必修' ? '1' : '0'
						}))
						
						batchAddBySingleApi(formattedData, 0)
					} catch (error) {
						console.error('Excel解析错误:', error)
						uni.showToast({ title: 'Excel解析失败', icon: 'none' })
					}
				}
				reader.onerror = () => uni.showToast({ title: '文件读取失败', icon: 'none' })
				reader.readAsBinaryString(file)
			},
			fail: () => uni.showToast({ title: '选择文件失败', icon: 'none' })
		})
	}

	
	// 批量添加课程（逐个调用API）
	const batchAddBySingleApi = (courses, index) => {
	  if (index >= courses.length) {
		// 全部完成
		uni.showToast({ title: `已完成${courses.length}条课程添加`, icon: 'success' })
		getCourseList() // 刷新列表
		return
	  }
	
	  // 调用现有单条添加接口
	  uni.request({
		url: `http://localhost:3001/courseList?studentId=${studentIdT.value}`,
		method: 'POST',
		data: courses[index],
		success: (res) =>   batchAddBySingleApi(courses, index + 1),
		fail: () => batchAddBySingleApi(courses, index + 1)
	  })
	}

	// 页面加载时获取学生ID
	onLoad((options) => {
		studentIdT.value = options.studentId
		console.log('当前学生ID:', studentIdT.value)
	})

	// 页面挂载时初始化
	onMounted(() => {
		console.log('页面挂载，初始化课程表')
		const now = new Date()
		todayWeek.value = now.getDay() === 0 ? 7 : now.getDay()
		console.log('今日星期:', todayWeek.value)
		getCourseList()
		
		// 显示当前日期时间（用于调试）
		console.log('当前时间:', now.toLocaleString())
	})

	// 页面卸载时清理资源
	onUnmounted(() => {
		if (reminderTimer) {
			clearInterval(reminderTimer)
			console.log('页面卸载，清除提醒定时器')
		}
	})

	// 监听今日课程变化，更新提醒
	watchEffect(() => {
		console.log('今日课程列表更新，重新设置提醒')
		setCourseReminders()
	})
</script>

<style lang="scss" scoped>
// 原有样式保持不变
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
		border: 2px solid #C9CDD4;
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
			
		 &:active {
			color: #FFFFFF;
			background-image: linear-gradient( #0085FF, #212AFF); 
			border: 2px solid #0E42D2;
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
	}
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}
	
	.popContainer{
		width: 680px;
		height: 260px;
		background-color: #FFFFFF;
		border-radius:12px 12px 12px 12px;
		display: flex;
			flex-direction: column;
			align-items: left;
			justify-content: center;
			padding: 40px;
			gap: 20px;
			flex-wrap: wrap; 
		
			
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
.week-container {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding: 10px;
}
.week-column {
  min-width: 200px;
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 10px;
  flex-shrink: 0;
  height: 480px;
  overflow-y: auto;
}
.week-title {
  font-weight: bold;
  text-align: center;
  margin-bottom: 10px;
  padding: 5px;
  background-color: #e0e0e0;
  border-radius: 4px;
  position: sticky;
  top: 0;
  z-index: 10;
}
.course-item {
  background-color: white;
  border-radius: 6px;
  padding: 10px;
  margin-bottom: 12px;
  font-size: 14px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}
.course-title {
  font-weight: bold;
  margin-bottom: 5px;
}
.day-view {
  .day-header {
    padding: 16px 24px;
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 16px;
  }
  
  .day-course-container {
    display: flex;
    flex-direction: column;
    padding: 0 24px;
    max-height: calc(100% - 80px);
    overflow-y: auto;
    border-radius: 0 0 12px 12px;
    margin-top: -16px;
    
    .empty-state {
      text-align: center;
      padding: 40px;
      color: #999;
    }
  }
}

/* 课程提醒样式 */
.reminder-box {
  position: fixed;
  top: 100px;
  right: 24px;
  width: 300px;
  background-color: #ff9800;
  color: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
  z-index: 9999; /* 确保在最上层 */
  display: flex;
  align-items: center;
  justify-content: space-between;
  animation: fadeIn 0.5s ease-in-out;
}

.reminder-text {
  font-size: 16px;
  font-weight: 500;
}

.reminder-close {
  background: none;
  border: none;
  color: white;
  font-size: 20px;
  cursor: pointer;
  outline: none;
}

/* 添加淡入动画 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>