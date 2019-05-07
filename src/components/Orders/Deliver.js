import React, { Component, Fragment } from 'react';
import {Form, Table, Select} from "antd";

const mock_data = {
  order_id: "1234567890987654321",
  distance: "24",
  robots: [
    {
      robot_id: "1234",
      type: "land_robot",
      time: "77",
      price: "47.90"
    }, {
      robot_id: "4321",
      type: "UAV",
      time: "31",
      price: "120.90"
    }
  ]
}


const { Option } = Select;

class DeliverForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      order_id: "",
      distance: "",
      robots: [
        {
          robot_id: "",
          type: "",
          time: "",
          price: ""
        }
      ]
    }
  }


  render() {
    const { getFieldDecorator } = this.props.form;
    let radioUI = (<h1>Sorry, no available deliver robot right now, please try it later</h1>);
    const columns = [{
      title: 'Type',
      dataIndex: 'type',
      key: 'type',
    }, {
      title: 'Time',
      dataIndex: 'time',
      key: 'time',
    }, {
      title: 'Price',
      dataIndex: 'price',
      key: 'price',
    }];

    if (mock_data.robots.length > 0) {

      radioUI = (
        <div>
          <Form>
            <Form.Item style={{ display: 'inline-block', width: 'calc(50% - 12px)' }}>
              <h1>Choose your deliver method&nbsp;</h1>
            </Form.Item>
            <Form.Item style={{ display: 'inline-block', width: '40%' }}>
              <Select placeholder="Select one Method">
                {mock_data.robots.map( ele => {
                    return(
                      <Option key={ele.robot_id} value={ele.robot_id}>
                        {ele.type === 'land_robot' ? "land robot" : "UAV"}
                      </Option>
                    );
                  }
                )}

              </Select>
            </Form.Item>
          </Form>

          <div>
            <Table
              rowKey={record => record.robot_id}
              columns={columns}
              dataSource={mock_data.robots}
              pagination={false}
            />
          </div>
        </div>
      );
    }


    return(
      <div className="form-wrapper">
        {radioUI}
      </div>
    );
  }
}

const Deliver = Form.create()(DeliverForm);

export default Deliver;
