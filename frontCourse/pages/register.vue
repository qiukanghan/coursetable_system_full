<template>
	
	<uni-popup  ref="popup" type="center" v-bind:is-mask-click="false">
		  <view class="container">
			<view class="logo">注册账号</view>
			
			<form class="login-form">
			  <view class="form-group">
				<label>用户名：</label>
				<input type="text" v-model="formData.studentId" placeholder="请输入用户名"/>

				<label>密码：</label>
				<input type="password" v-model="formData.password" placeholder="请输入密码"/>

				<label>确认密码：</label>
				<input type="password" v-model="formData.checkpwd" placeholder="再次输入密码"/>			
			  </view>
			  
			  <button class="btn-register" @click="register()">注册</button>
			  <button class="btn-register" @click="gotoPage('/pages/index/index')">取消注册</button>
			</form>
		  </view>
	</uni-popup>

</template>

<script setup>
	import { ref, onMounted } from 'vue'
	const popup = ref(null)        // 初始弹窗引用
	function gotoPage(path){
		  uni.navigateTo({ url: path })
	}
	onMounted(() => {
	  setTimeout(() => {
		popup.value.open('center') // 页面加载后自动打开初始弹窗
	  }, 300)
	})
	
const formData = ref({
  studentId: '',
  password: '',
  checkpwd: ''
})

const register = () => {
  if (!formData.value.studentId) {
	uni.showToast({ title: '请输入用户名', icon: 'none' })
    return
  }
  if (!formData.value.password) {
	uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }
 uni.request({
     // url: 'http://10.32.61.192:8080/api/register', // 接口地址
	 url: `http://10.27.195.175:8080/api/register`,
     method: 'POST',
     data: {
       studentId: formData.value.studentId.trim(),
       password: formData.value.password,
     },
     success: (res) => {
       if (res.statusCode === 201) {
		uni.showToast({ title: '注册成功', icon: 'success' })
         setTimeout(() => {
           popup.value.close()
           uni.navigateTo({ url: '/pages/index/index' }) // 假设登录页路径
         }, 1500)
       } else {
		uni.showToast({ title: '注册失败，请重试', icon: 'none' })
       }
     },
     fail: () => {
		uni.showToast({ title: '网络异常，请检查连接', icon: 'none' })
     },
     complete: () => {
       
     }
   })
}
</script>

<style scoped>
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

.btn-register {
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