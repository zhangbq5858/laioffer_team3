import React, { Component, } from 'react';
import axios from 'axios';
import { Form, Icon, Input, Button, } from 'antd';
import {message} from 'antd/lib/index';

const mock_data = {
  order_id: "1234567890987654321",
  expect_arrive_time: "10:24",
  current_address: {
    street: "1234 World Ave",
    city: "New York",
    state: "NY",
    zipcode: "01234"
  }
}

class TrackingNum extends Component{
  constructor(props) {
    super(props);
    this.state = {
      fetch: false,
      expect_arrive_time: "",
      current_address: "",
    }
  }

  handleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        const formData = new FormData();
        formData.set('order_id', values.orderNum);
        axios({
          method: 'POST',
          url:'/track',
          data: formData,
          config: { headers: {'Content-Type': 'multipart/form-data' }}
        }).then(response => {
          if (response.status === 200) {
            this.setState({ fetch: true });
          } else {
            message.error("Wrong order number! Please check!");
            this.setState({ fetch: false });
          }
          console.log(response);
        }).catch(err => {
          console.log(err);
        });
      }
    });
  };

  render() {

    const {current_address} = mock_data;
    const { getFieldDecorator } = this.props.form;
    let formUI;
    if (this.state.fetch) {
      formUI = (
        <Form className="tracking-form">
          <Form.Item>
            <Form.Item style={{ display: 'inline-block', width: 'calc(30% - 12px)' }}>
              <h3>Order id&nbsp;&#58;</h3>
            </Form.Item>
            <Form.Item style={{ display: 'inline-block', width: '40%' }}>
              <span>{mock_data.order_id}</span>
            </Form.Item>
          </Form.Item>

          <Form.Item>
            <Form.Item style={{ display: 'inline-block', width: 'calc(30% - 12px)' }}>
              <h3>Expect Arrive Time&nbsp;&#58;</h3>
            </Form.Item>
            <Form.Item style={{ display: 'inline-block', width: '40%' }}>
              <span>{mock_data.expect_arrive_time}</span>
            </Form.Item>
          </Form.Item>

          <Form.Item>
            <Form.Item style={{ display: 'inline-block', width: 'calc(30% - 12px)' }}>
              <h3>Current Location&nbsp;&#58;</h3>
            </Form.Item>
            <Form.Item style={{ display: 'inline-block', width: '40%' }}>
              <span>{current_address.street + `, ` + current_address.city + `, ` + current_address.state + `, ` + current_address.zipcode }</span>
            </Form.Item>
          </Form.Item>
        </Form>
      );
    }

    return(
      <div>
        <h1>Enter Your Order Id: </h1>
        <Form onSubmit={this.handleSubmit} >
          <Form.Item>
            <Form.Item style={{ display: 'inline-block', width: 'calc(40% - 12px)' }}>
              {getFieldDecorator('orderNum', {
                rules: [{ required: true, message: 'Please input your order number!' }],
              })(
                <Input
                  prefix={<Icon type="barcode" style={{ color: 'rgba(0,0,0,.25)' }} />}
                  placeholder="Order#"
                />,
              )}
            </Form.Item>
            <Form.Item style={{ display: 'inline-block', width: '40%', paddingLeft:'20px' }}>
              <Button type="primary" htmlType="submit" className="login-form-button">
                Search
              </Button>
            </Form.Item>
          </Form.Item>
        </Form>
        
        <div>{formUI}</div>
      </div>
    );
  }
}

const Tracking = Form.create()(TrackingNum);
export default Tracking;