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
      validOrderId: true,
      order_id: "",
      expect_arrive_time: "",
      current_address: "",
      status: "",
      from: {},
      to: {},
      loading: false,
      iconLoading: false,
    }
  }

  handleSubmit = e => {
    e.preventDefault();

    this.props.form.validateFields((err, values) => {
      if (!err) {
        // const formData = new FormData();
        // formData.set('order_id', values.orderNum);
        this.setState({ iconLoading: true });
        axios({
          method: 'POST',
          url:'/track',
          // data: formData,
          data: JSON.stringify({ order_id: values.orderNum }),
          // config: { headers: {'Content-Type': 'multipart/form-data' }}
        }).then(response => {
          if (response.data.status === "Invalid Order Number") {
            this.setState({ validOrderId: false });
            this.setState({
              loading: !this.state.loading,
              iconLoading: !this.state.iconLoading,
            });
          } else if (response.status === 200 ) {
            let {expect_arrive_time, from_address, to_address, status, order_id } = response.data;
            this.setState({
              fetch: true,
              validOrderId: true,
              expect_arrive_time,
              status,
              from: from_address,
              to: to_address,
              order_id,
            });
            this.setState({
              loading: !this.state.loading,
              iconLoading: !this.state.iconLoading,
            });
          }
          console.log(response.data);
        }).catch(err => {
          console.log(err);
          this.setState({
            loading: !this.state.loading,
            iconLoading: !this.state.iconLoading,
          });
        });
        this.props.form.resetFields();
        this.setState({
          loading: !this.state.loading,
          iconLoading: !this.state.iconLoading,
        });
      }
    });
  };

  render() {
    
    const { order_id, expect_arrive_time, status, from, to, validOrderId } = this.state;
    const { getFieldDecorator } = this.props.form;
    let formUI;
    if (this.state.fetch && validOrderId) {
      formUI = (
        <Form className="tracking-form">
          <Form.Item>
            <Form.Item style={{ display: 'inline-block', width: 'calc(20% - 12px)' }}>
              <h3>Order id&nbsp;&#58;</h3>
            </Form.Item>
            <Form.Item style={{ display: 'inline-block', width: '40%' }}>
              <span>{ order_id }</span>
            </Form.Item>
          </Form.Item>

          <Form.Item>
            <Form.Item style={{ display: 'inline-block', width: 'calc(20% - 12px)' }}>
              <h3>Expect Arrive Time&nbsp;&#58;</h3>
            </Form.Item>
            <Form.Item style={{ display: 'inline-block', width: '40%' }}>
              <span>{ expect_arrive_time }</span>
            </Form.Item>
          </Form.Item>

          <Form.Item>
            <Form.Item style={{ display: 'inline-block', width: 'calc(20% - 12px)' }}>
              <h3>Status&nbsp;&#58;</h3>
            </Form.Item>
            <Form.Item style={{ display: 'inline-block', width: '40%' }}>
              <span>{ status }</span>
            </Form.Item>
          </Form.Item>

          <Form.Item>
            <Form.Item style={{ display: 'inline-block', width: 'calc(20% - 12px)' }}>
              <h3>From&nbsp;&#58;</h3>
            </Form.Item>
            <Form.Item style={{ display: 'inline-block', width: '40%' }}>
              <span>{from.street + `, ` + from.city + `, ` + from.state + `, ` + from.zipcode }</span>
            </Form.Item>
          </Form.Item>

          <Form.Item>
            <Form.Item style={{ display: 'inline-block', width: 'calc(20% - 12px)' }}>
              <h3>To&nbsp;&#58;</h3>
            </Form.Item>
            <Form.Item style={{ display: 'inline-block', width: '40%' }}>
              <span>{to.street + `, ` + to.city + `, ` + to.state + `, ` + to.zipcode }</span>
            </Form.Item>
          </Form.Item>
        </Form>
      );
    } else if (!validOrderId){
      formUI = (
        <Form className="tracking-form">
          <h3><Icon type="warning" theme="twoTone" twoToneColor="#eb2f96"/> {' '}<span style={{ color: '#eb2f96' }}>Order Number is Not Valid, Please Check</span></h3>
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
              <Button type="primary" htmlType="submit" className="login-form-button" loading={this.state.loading}>
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