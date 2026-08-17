// 管理员登录页：输入账号密码调用后端登录接口，成功后保存 token 并跳回首页
import { useState } from 'react'
import { Card, Input, Button, message } from 'antd'
import request from '../api/request'

export default function Login() {
  const [form, setForm] = useState({ username: '', password: '' })

  const handleLogin = async () => {
    try {
      const { data } = await request.post('/auth/login', form)
      localStorage.setItem('token', data.token)
      message.success('登录成功')
      window.location.href = '/'
    } catch (e) {
      message.error('登录失败')
    }
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', padding: 80 }}>
      <Card title="管理员登录" style={{ width: 320 }}>
        <Input
          placeholder="用户名"
          value={form.username}
          onChange={(e) => setForm({ ...form, username: e.target.value })}
          style={{ marginBottom: 12 }}
        />
        <Input.Password
          placeholder="密码"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          style={{ marginBottom: 12 }}
        />
        <Button type="primary" block onClick={handleLogin}>登录</Button>
      </Card>
    </div>
  )
}
