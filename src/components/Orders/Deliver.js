import React, { Component, Fragment } from 'react';
import {Form, Table, Select, Button} from "antd";
import $ from 'jquery';
import {message} from 'antd/lib/index'

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
      robots: [],
    }
  }
  componentDidMount() {
    // $.ajax({
    //   url:'/xxxxxxxxx',
    //   method: 'GET',
    // }).then(res => {
    //   console.log(res);
    //
    //   this.setState({
    //     order_id: res.order_id,
    //     distance: res.distance,
    //     robots: res.robots,
    //   })
    // }, error => {
    //   console.log(error)
    //   message.error(error.responseText);
    // }).catch(err => {
    //   console.log(err)
    // });
  }

  handleChange = input => event => {
    if (input === "to_address.state" || input === "from_address.state") {
      this.setState({ [input]: event });
    } else {
      this.setState({ [input]: event.target.value });
    }
  };

  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const formData = new FormData();
        formData.set('order_id', this.state.order_id);
        formData.set('robot', values.robot);

        console.log(formData.get('order_id'));
        console.log(formData.get('robot'));

        // $.ajax({
        //   url:'/confirmOrder',
        //   method: 'POST',
        //   data: formData,
        //   dataType: 'text',
        // }).then(res => {
        //   this.props.setPage("2");
        // }, error => {
        //   console.log(error)
        //   message.error(error.responseText);
        // }).catch(err => {
        //   console.log(err)
        // });

        this.props.setPage("2");
      }
    });
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
                  {mock_data.robots.map( ele => {
                      return(
                        <Option key={ele.robot_id} value={ele.robot_id}>
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
                dataSource={mock_data.robots}
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
