import React, { Component, } from 'react';
import {Form, Table, Select, Button, Modal, Icon } from 'antd';
import axios from 'axios';
import {message} from 'antd/lib/index';

const { Option } = Select;

class DeliverForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      order_id: "",
      distance: "",
      robots: [],
      visible1: false,
      visible2: false,
      loading: false,
      iconLoading: false,
    }
  }

  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (this.props.getRobotInfo()[2] === "-1") {
        this.showModal2();
      } else {
        if (!err) {
          const selectRobot = this.props.getRobotInfo()[1].filter(ele => ele["type"] === values.robot);
          axios({
            method: 'POST',
            url:'/confirmOrder',
            data: JSON.stringify({
              order_id: this.props.getRobotInfo()[0],
              robot: selectRobot,
            }),
          }).then(response => {
            if (response.status === 200) {
              message.success("Make order successfully! Thanks");
              this.props.setPage("2");
            } else {
              message.error("oop! something wrong");
              this.props.history.push(`/`)
            }
            console.log(response);
          }).catch(err => {
            console.log(err);
          });
          this.setState({
            loading: !this.state.loading,
            iconLoading: !this.state.iconLoading,
          });
        }
      }

    });
  }

  showModal1 = () => {
    this.setState({
      visible1: true,
    });
  };

  showModal2 = () => {
    this.setState({
      visible2: true,
    });
  };

  handleOk = e => {
    console.log(this.props.getRobotInfo()[0]);
    axios({
      method: 'DELETE',
      url:'/confirmOrder',
      data: JSON.stringify({
        order_id: this.props.getRobotInfo()[0],
      }),
    }).then(response => {
      if (response.status === 200) {
        message.success("Cancel order successfully! ");
        this.props.history.push(`/`)
      } else {
        message.error("oop! something wrong");
        this.props.history.push(`/`)
      }
    }).catch(err => {
      console.log(err);
    });
    this.setState({
      visible1: false,
    });
  };

  handleCancel1 = e => {
    this.setState({
      visible1: false,
    });
  };

  handleCancel2 = e => {
    this.setState({
      visible2: false,
    });
    this.props.history.push(`/`);
  };

  render() {
    // console.log(this.props.getRobotInfo()[2]);
    const { getFieldDecorator } = this.props.form;
    let radioUI = (<h1>Sorry, no available deliver robot right now, please try it later</h1>);
    const columns = [{
      title: 'Type',
      dataIndex: 'type',
      key: 'type',
      render: (record) => {
        return record === "land_robot" ? "Land Robot" : "UAV"
      }
    }, {
      title: 'Time(mins)',
      dataIndex: 'time',
      key: 'time',
    }, {
      title: 'Price($)',
      dataIndex: 'price',
      key: 'price',
    }, {
      title: 'Available Time',
      dataIndex: 'appointment_time',
      key: 'appointment_time',
    }];

    if (this.props.getRobotInfo()[1].length > 0) {
      radioUI = (
        <div>
          <Form>
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
                  {this.props.getRobotInfo()[1].map( (ele, idx) => {
                      return(
                        <Option key={idx} value={ele.type}>
                          {ele.type === "land_robot" ? "Land Robot" : "UAV"}
                        </Option>
                      );
                    }
                  )}
                </Select>
              )}

            </Form.Item>
            <div>
              <Table
                rowKey={record => record.type}
                columns={columns}
                dataSource={this.props.getRobotInfo()[1]}
                pagination={false}
              />
            </div>

            <Form.Item style={{paddingTop: '15px'}}>
              <Button type="primary" htmlType="submit" className="login-form-button" loading={this.state.loading} onClick={this.handleSubmit}>
                Submit
              </Button>

              <Button onClick={this.showModal1} type="danger" htmlType="submit" className="login-form-button" style={{marginLeft: '40px'}}>
                Cancel
              </Button>
            </Form.Item>
          </Form>

          <Modal
            title="Cancel Order"
            visible={this.state.visible1}
            onOk={this.handleOk}
            onCancel={this.handleCancel1}
          >
            <p>Do you want to cancel these order? <Icon type="frown" /></p>
          </Modal>

          <Modal
            title="Cannot Finish The Order"
            visible={this.state.visible2}
            footer={[
              <Button key="back" onClick={this.handleCancel2}>
                ok
              </Button>
            ]}
          >
            <p>Oops... The distance is too far, we cannot finish the order.<Icon type="frown" /></p>
            <p>Click <b>ok</b> to go back Homepage</p>
          </Modal>
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
