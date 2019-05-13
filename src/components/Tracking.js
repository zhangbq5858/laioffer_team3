import React, { Component, Fragment } from 'react';
import axios from 'axios';
import { Form, } from "antd";

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

class Tracking extends Component{
  constructor(props) {
    super(props);
  }
  componentDidMount() {
    // axios
    //   .get('/track')
    //   .then(response => {
    //     console.log(response)
    //   })
    //   .catch(err => {
    //     console.log(err);
    //   });
  }

  render() {

    const {current_address} = mock_data;

    return(
      <div>
        <h1>Tracking Page</h1>
        <Form>
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
      </div>
    );
  }
}

export default Tracking;