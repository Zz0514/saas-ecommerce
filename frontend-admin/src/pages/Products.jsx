import { useEffect, useState } from 'react'
import { Table, Button, Modal, Form, InputNumber, Input, message } from 'antd'
import { getProducts, createProduct, deleteProduct } from '../api/product'

export default function Products() {
  const [data, setData] = useState([])
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm()

  const load = async () => {
    const res = await getProducts()
    setData(res.data)
  }

  useEffect(() => { load() }, [])

  const handleCreate = async () => {
    const values = await form.validateFields()
    await createProduct(values)
    message.success('创建成功')
    setOpen(false)
    form.resetFields()
    load()
  }

  const handleDelete = async (id) => {
    await deleteProduct(id)
    message.success('删除成功')
    load()
  }

  const columns = [
    { title: 'ID', dataIndex: 'id' },
    { title: '名称', dataIndex: 'name' },
    { title: '价格', dataIndex: 'price' },
    { title: '库存', dataIndex: 'stock' },
    {
      title: '操作',
      render: (_, record) => (
        <Button danger onClick={() => handleDelete(record.id)}>删除</Button>
      )
    }
  ]

  return (
    <div style={{ padding: 24 }}>
      <Button type="primary" onClick={() => setOpen(true)} style={{ marginBottom: 16 }}>
        新增商品
      </Button>
      <Table rowKey="id" columns={columns} dataSource={data} />
      <Modal title="新增商品" open={open} onOk={handleCreate} onCancel={() => setOpen(false)}>
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="price" label="价格" rules={[{ required: true }]}>
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="stock" label="库存">
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
