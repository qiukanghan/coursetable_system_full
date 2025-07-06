const fs = require('fs');
const path = require('path');

module.exports = (req, res, next) => {
  // 拦截 POST /users 请求，处理登录逻辑
  if (req.url === '/users' && req.method === 'POST') {
    let body = '';
    req.on('data', (chunk) => {
      body += chunk;
    });
    req.on('end', () => {
      try {
        // 解析请求体（用户名、密码）
        const { username, password } = JSON.parse(body);
        
        // 读取 test02.json 数据
        const dbPath = path.join(__dirname, 'test02.json');
        const dbData = JSON.parse(fs.readFileSync(dbPath, 'utf8'));
        const users = dbData.users || [];

        // 查找匹配的用户
        const user = users.find(
          (u) => u.username === username && u.password === password
        );

        if (user) {
          // 登录成功：返回 token 和成功响应
          res.statusCode = 200;
          res.setHeader('Content-Type', 'application/json');
          res.end(JSON.stringify({
            code: 0,
            msg: '登录成功',
            token: user.token,
          }));
        } else {
          // 登录失败：返回错误响应
          res.statusCode = 401;
          res.setHeader('Content-Type', 'application/json');
          res.end(JSON.stringify({
            code: 1,
            msg: '用户名或密码错误',
          }));
        }
      } catch (error) {
        // 捕获 JSON 解析、文件读取等异常
        console.error('登录中间件错误:', error);
        res.statusCode = 500;
        res.setHeader('Content-Type', 'application/json');
        res.end(JSON.stringify({
          code: 500,
          msg: '服务器内部错误',
        }));
      }
    });
  } else {
    // 其他请求走 JSON Server 默认逻辑
    next();
  }
};