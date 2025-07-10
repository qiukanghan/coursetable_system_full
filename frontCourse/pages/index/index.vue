<template>
  <!-- 登录弹窗 -->
	<uni-popup ref="popup" type="center" :is-mask-click="false">
		<view class="container">
			<view class="logo">登录系统</view>
			<!-- 登录表单 -->
				<form class="login-form" >
					<view class="form-group">
						  <label>用户名：</label>
						  <input type="text" v-model="formData.studentId" placeholder="请输入用户名"/>
						  <label>密码：</label>
						  <input type="password" v-model="formData.password" placeholder="请输入密码"/>
					</view>
					<button class="submit-btn" @click="login()">登录</button>
					<button class="submit-btn" @click="gotoPage('/pages/register')">注册</button>
				</form>
		</view>
	</uni-popup>
</template>

<script setup>
	import { ref, onMounted } from 'vue'

	// 响应式变量
	const popup = ref(null) // 弹窗引用
	const formData = ref({ // 表单数据（用户名和密码）
	  studentId: '',
	  password: ''
	})
	function gotoPage(path) {
		uni.navigateTo({ url: path })
	}
	// 核心登录逻辑
	const login = () => {
	  // 1. 表单验证（检查用户名和密码是否为空）
	  if (!formData.value.studentId) {
		uni.showToast({ title: '请输入用户名', icon: 'none' })
		console.log("账号")
		return
	  }
	  if (!formData.value.password) {
		uni.showToast({ title: '请输入密码', icon: 'none' })
		console.log("密码")
		return
	  }
	 uni.request({
		// 查询用户数据
		url: `http://10.27.195.175:8080/api/login`,
		// url: `http://localhost:3000/users?studentId=${formData.value.studentId.trim()}&password=${formData.value.password}`,
		method: 'POST', // 使用 GET 请求查询用户
		// method:'GET',
		data:formData.value,
		success: (res) => {
		  // 4. 登录成功处理
		  // if (res.statusCode === 200 && res.data.length > 0) {
		  if (res.statusCode === 200 ) {
			// 保存后端返回的令牌（用于后续接口身份验证）
			const user = res.data[0]
			uni.navigateTo({ url: `/pages/home?studentId=${formData.value.studentId.trim()}`})
			uni.showToast({ title: '登录成功', icon: 'success' })
			popup.value.close()
		  } else {
			// 登录失败（如用户名密码错误）
			uni.showToast({ title: '用户名或密码错误', icon: 'none' })
		  }
		},
		fail: () => {
		  uni.showToast({ title: '网络异常，请重试', icon: 'none' })
		  console.log("失败")
		},
		complete: () => {
		  console.log("完成")
		}
	  })
	}
onMounted(() => {
  setTimeout(() => {
	popup.value.open('center')
  }, 300)
})
</script>

<style lang="scss" scoped>
	page{
			background-image: url('/static/image/bk.png');
			background-size: cover;
	}
/* 样式保持不变 */
.container {
	width: 500px;
	height: 400px;
	background-color: #FFFFFF;
  display: flex;
	  flex-direction: column;
	  justify-content: center;
	  align-items: center;

  padding: 0 30rpx;
}

.logo {
  font-size: 48rpx;
  font-weight: bold;
  margin-bottom: 80rpx;
  color: #007AFF;
}

.login-form {
  width: 100%;
  max-width: 600rpx;
}

.form-group {
  margin-bottom: 30rpx;
}

.form-group label {
  display: block;
  font-size: 32rpx;
  margin-bottom: 10rpx;
  color: #333;
}

.form-group input {
  width: 100%;
  height: 80rpx;
  padding: 0 20rpx;
  border: 1rpx solid #ccc;
  border-radius: 8rpx;
  font-size: 32rpx;
}

.submit-btn {
  width: 100%;
  height: 90rpx;
  background-color: #007AFF;
  color: white;
  border-radius: 8rpx;
  font-size: 36rpx;
  margin-top: 20rpx;
  
	
  &:active {
  			color: #FFFFFF;
  			background-image: linear-gradient( #0085FF, #212AFF); 
  			box-shadow: 0 0 12px 0 #1FE4FF; 
   }
}
	.btn-next{
		width: 108px;
		height: 48px;
		position: absolute;
			top: 16px;
			right: 24px;
		background-color: #007AFF;	
				
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
.submit-btn:disabled {
  background-color: #CCCCCC;
  cursor: not-allowed;
}

.tips {
  display: flex;
  justify-content: space-between;
  width: 100%;
  max-width: 600rpx;
  margin-top: 40rpx;
  font-size: 28rpx;
  color: #666;
}

.tips text {
  cursor: pointer;
}
</style>