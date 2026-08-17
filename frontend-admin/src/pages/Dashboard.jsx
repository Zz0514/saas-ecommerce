// 后台首页（仪表盘）：当前为欢迎占位页，后续可放销售/订单数据概览
import { Layout, Typography } from 'antd'

const { Header, Content } = Layout

export default function Dashboard() {
  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ color: '#fff', fontSize: 18 }}>电商管理后台</Header>
      <Content style={{ padding: 24 }}>
        <Typography.Title level={3}>欢迎使用管理后台</Typography.Title>
        <p>左侧可进入「商品管理」等模块。</p>
      </Content>
    </Layout>
  )
}
