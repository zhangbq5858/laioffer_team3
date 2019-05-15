import React, { Component, } from 'react';
import {Form, Table, Select, Button} from 'antd';
import axios from 'axios';
import $ from 'jquery';
import {message} from 'antd/lib/index';

const { Option } = Select;

class DeliverForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      order_id: "",
      distance: "",
      robots: [],
    }
  }

  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const selectRobot = this.props.getRobotInfo()[1].filter(ele => ele["type"] === values.robot);
        console.log(selectRobot);
        const formData = new FormData();
        formData.set('order_id', this.props.getRobotInfo()[0]);
        formData.set('robot', selectRobot);

        // this.props.setPage("2");

        axios({
          method: 'POST',
          url:'/confirmOrder',
          data: formData,
          config: { headers: {'Content-Type': 'multipart/form-data' }}
        }).then(response => {
          if (response.status === 200) {
            this.props.setPage("2");
          } else {
            message.error("oop! something wrong");
            this.props.history.push(`/`)
          }
          console.log(response);
        }).catch(err => {
          console.log(err);
        });
      }
    });
  }


  render() {
    console.log(this.props.getRobotInfo()[1]);
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

    if (this.props.getRobotInfo()[1].length > 0) {

      radioUI = (
        <div>
          <Form onSubmit={this.handleSubmit}>
            <Form.Item style={{ display: 'inline-block', width: 'calc(50% - 12px)' }}>
              <h1>Choose your deliver method&nbsp;</h1>
            </Form.Item>
            <Form.Item style={{ display: 'inline-block', width: '40%' }}>

              {getFieldDecorator('robot', {
                rules: [{ required: true, message: 'Please choose your deliver method!' }],
              })(
                <Select
                  placeholder="Select one Method"
                >
                  {this.props.getRobotInfo()[1].map( ele => {
                      return(
                        <Option key={ele.type} value={ele.type}>
                          {ele.type === 'land_robot' ? "land robot" : "UAV"}
                        </Option>
                      );
                    }
                  )}
                </Select>
              )}

            </Form.Item>
            <div>
              <Table
                rowKey={record => record.robot_id}
                columns={columns}
                dataSource={this.props.getRobotInfo()[1]}
                pagination={false}
              />
            </div>

            <Form.Item>
              <Button type="primary" htmlType="submit" className="login-form-button">
                Submit
              </Button>
            </Form.Item>
          </Form>
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
