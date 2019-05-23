import React, { Component, } from 'react';
import axios from 'axios';
import { Form, Icon, Input, Button, } from 'antd';
import {message} from 'antd/lib/index';
import { ENGINE_METHOD_DIGESTS } from 'constants';

// var mock_data = {
//   order_id: "1234567890987654321",
//   expect_arrive_time: "10:24",
//   current_status: "initialized"
// }

class TrackingNum extends Component{
  constructor(props) {
    super(props);
    this.state = {
      fetch: false,
      expect_arrive_time: "",
      current_status: "",
      order_id:"",
    }
  }

  handleSubmit = e => {
    e.preventDefault();

    this.props.form.validateFields((err, values) => {
      if (!err) {
        // const formData = new FormData();
        // formData.set('order_id', values.orderNum);
        axios({
          method: 'POST',
          url:'/track',
          // data: formData,
          data: JSON.stringify({ order_id: values.orderNum }),
          // config: { headers: {'Content-Type': 'multipart/form-data' }}
        }).then(response => {
          if (response.status === 200) {
            var data = JSON.parse(response.request.response);
            console.log("track data response", data);
            this.setState({ 
              fetch: true , 
              expect_arrive_time: data.expect_arrive_time,
              current_status: data.status,
              order_id: data.order_id,
              expect_arrive_time: data.expect_arrive_time,
            });
          } else {
            message.error("Wrong order number! Please check!");
            this.setState({ fetch: true });
          }
          console.log(response);
        }).catch(err => {
          console.log(err);
        });
        this.props.form.resetFields();
      }
    });
  };

  render() {

    // const { current_status } = mock_data;
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
              <span>{this.state.order_id}</span>
            </Form.Item>
          </Form.Item>

          <Form.Item>
            <Form.Item style={{ display: 'inline-block', width: 'calc(30% - 12px)' }}>
              <h3>Expect Arrive Time&nbsp;&#58;</h3>
            </Form.Item>
            <Form.Item style={{ display: 'inline-block', width: '40%' }}>
              <span>{this.state.expect_arrive_time}</span>
            </Form.Item>
          </Form.Item>

          <Form.Item>
            <Form.Item style={{ display: 'inline-block', width: 'calc(30% - 12px)' }}>
              <h3>Current Status&nbsp;&#58;</h3>
            </Form.Item>
            <Form.Item style={{ display: 'inline-block', width: '40%' }}>
              <span>{this.state.current_status}</span>
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