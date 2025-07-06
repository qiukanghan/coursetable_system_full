<template>
	<view class="pageBg" />
	<text class="title">{{titleValue}}</text>
	<button class="btn-change btn-font"  @click="changeTime()">查看{{otherTitle}}</button>
	<button class="btn-add btn-font"  @click="addCourse()">添加课表</button>
	<button class="btn-exit btn-font"  @click="gotoPage('/pages/index/index')">退出登录</button>
	
	  <view class="container">
		<!-- 按星期分组容器（周一到周日） -->
		<view class="week-container">
		  <!-- 循环渲染周一到周日列 -->
		  <view class="week-column" v-for="week in 7" :key="week">
			<view class="week-title">
			  {{ ['周一', '周二', '周三', '周四', '周五', '周六', '周日'][week - 1] }}
			</view>
			<!-- 渲染对应星期的课程 -->
			<view class="course-item" v-for="(item, index) in getCoursesByWeek(week)" :key="index"
				  :style="{ backgroundColor: item.isMust === '1' ? '#e6f3ff' : 'white' }"
			>
			  <view class="course-title">课程名称：{{ item.name }}</view>
			  <view>发布人：{{ item.time }}</view>
			  <view>时间：{{ formatTime(item.location) }}</view>
			  <view>讲师：{{ item.teacher }}</view>
			  <view>课程ID：{{ item.id }}</view>
			</view>
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
	const isWeek=ref(true)
	function gotoPage(path){
		  uni.navigateTo({ url: path })
	}
	const titleValue=ref('周课表')
	const otherTitle=ref('日课表')
	function changeTime(){
		let temp=titleValue.value;
		titleValue.value=otherTitle.value;
		otherTitle.value=temp;
	}
	
	const popupAdd = ref(null)        // 初始弹窗引用
	function addCourse(){
		popupAdd.value.open('center')
	}
	const addData = ref({
	  addName: '',
	  addTime: '',
	  addLocation: '',
	  addTeacher: '',
	  addMust: ''
	})
		
		
		
	const courseList = ref([]);
	const loading = ref(false);
	// 获取课程列表（保持原有逻辑）
	const getCourseList = () => {
	  loading.value = true;
	  uni.request({
	    url: 'http://localhost:3013/courseList',
	    method: 'GET',
	    success: (res) => {
	      if (res.statusCode === 200) {
	        courseList.value = res.data;
	      }
	    },
	    complete: () => loading.value = false
	  });
	};
	
	// 根据星期数筛选课程（核心排序逻辑）
	const getCoursesByWeek = (week) => {
	  // 筛选出时间第一位数字等于当前星期的课程
	  return courseList.value.filter(item => {
	    // 假设时间格式为 "1-10-10"，取第一位数字（星期）
	    const timeWeek = parseInt(item.location.split('-')[0]);
	    return timeWeek === week;
	  });
	};
	
	// 格式化时间显示（去除星期部分）
	const formatTime = (timeStr) => {
	  const parts = timeStr.split('-');
	  return `${parts[1]}:${parts[2]}`; // 显示 "10:10"
	};
	
	onMounted(() => getCourseList());
	
</script>

<style lang="scss" scoped>
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
</style>