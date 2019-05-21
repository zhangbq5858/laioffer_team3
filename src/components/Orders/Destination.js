import React, { Component, Fragment} from 'react';
import { Form, Input, Select, Button, Modal, } from 'antd';
import axios from 'axios';
import {message} from 'antd/lib/index'

const { Option } = Select;

const statesAbbr = ['AL', 'AK', 'AS', 'AZ', 'AR', 'CA', 'CO', 'CT', 'DE', 'DC', 'FM', 'FL', 'GA', 'GU', 'HI', 'ID', 'IL', 'IN', 'IA', 'KS', 'KY', 'LA', 'ME', 'MH', 'MD', 'MA', 'MI', 'MN', 'MS', 'MO', 'MT', 'NE', 'NV', 'NH', 'NJ', 'NM', 'NY', 'NC', 'ND', 'MP', 'OH', 'OK', 'OR', 'PW', 'PA', 'PR', 'RI', 'SC', 'SD', 'TN', 'TX', 'UT', 'VT', 'VI', 'VA', 'WA', 'WV', 'WI', 'WY', 'AE', 'AA', 'AP'];

class DestinationForm extends Component{
  constructor(props) {
    super(props);
    this.state = {
      confirmDirty: false,
      to_address:{
        street:"",
        city:"",
        state:"",
        zipcode:"",
      },
      from_address:{
        street:"",
        city:"",
        state:"",
        zipcode:"",
      },
      sender_email:"",
      receiver_email:"",
      visible: false, 
      loading: false,
      iconLoading: false,
    };
  }

  handleChange = input => event => {
    if (input === "to_address.state" || input === "from_address.state") {
      this.setState({ [input]: event });
    } else {
      this.setState({ [input]: event.target.value });
    }
  };

  showModal = () => {
    this.setState({
      visible: true,
    });
  };

  handleOk = e => {
    this.setState({
      visible: false,
    });
    this.props.form.resetFields();
    this.props.history.push(`/`);
  };

  handleSubmit = (e) => {
    e.preventDefault();

    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          to_address:{
            street:values.addTo,
            city:values.cityTo,
            state:values.stateTo,
            zipcode:values.zipTo,
          },
          from_address:{
            street:values.add,
            city:values.city,
            state:values.state,
            zipcode:values.zip,
          },
          sender_email:values.email,
          receiver_email:values.emailTo,
        });

        this.setState({ iconLoading: true });
        axios({
          method: 'POST',
          url:'/createOrder',
          data: JSON.stringify({
            to_address: {
              street: values.addTo,
              city: values.cityTo,
              state: values.stateTo,
              zipcode: values.zipTo,
            }, 
            from_address: {
              street: values.add,
              city: values.city,
              state: values.state,
              zipcode: values.zip,
            },
            sender_email: values.email,
            receiver_email: values.emailTo
          }),
        }).then(response => {
          if (response.status === 200) {
            message.success("Save information successfully");
            this.props.handleRobotInfo(response.data.order_id, response.data.robots);
            this.props.setPage("1");
          } else {
            this.showModal();
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
    });
  }

  render() {
    const { getFieldDecorator } = this.props.form;

    return (
      <Fragment>
        <Form onSubmit={this.handleSubmit} className="form-wrapper">

          <Form.Item
            className="form-title"
          >
            <h1>Enter your Destination Info</h1>
          </Form.Item>

          <Form.Item
            style={{ marginBottom: 0 }}
          >
            <Form.Item
              className="form-lable"
              label={(
                <span>
              First Name&nbsp;
            </span>
              )}
              style={{ display: 'inline-block', width: 'calc(50% - 12px)' }}
            >
              {getFieldDecorator('fNameTo', {
                rules: [{ required: false, message: 'Please input your first name!' }],
              })(
                <Input
                  style={{ display: 'inline-block', width: '100%' }}
                />
              )}

            </Form.Item>
            <span style={{ display: 'inline-block', width: '24px', textAlign: 'center' }}></span>
            <Form.Item
              className="form-lable"
              label={(
                <span>
              Last Name&nbsp;
            </span>
              )}
              style={{ display: 'inline-block', width: 'calc(50% - 12px)' }}
            >
              {getFieldDecorator('lNameTo', {
                rules: [{ required: false, message: 'Please input your last name!' }],
              })(
                <Input
                  style={{ display: 'inline-block', width: '100%' }}
                />
              )}
            </Form.Item>
          </Form.Item>

          <Form.Item
            label={(
              <span>
              Address&nbsp;
            </span>
            )}
          >
            {getFieldDecorator('addTo', {
              rules: [{ required: true, message: 'Please input your address!' }],
            })(
              <Input onChange={this.handleChange("to_address.street")}/>
            )}
          </Form.Item>


          <Form.Item
            style={{ marginBottom: 0 }}
          >
            <Form.Item
              label={(
                <span>
              City&nbsp;
            </span>
              )}
              style={{ display: 'inline-block', width: 'calc(40% - 12px)' }}
            >
              {getFieldDecorator('cityTo', {
                rules: [{ required: true, message: 'Please input your city!' }],
              })(
                <Input
                  style={{ display: 'inline-block', width: '100%' }}
                  onChange={this.handleChange("to_address.city")}
                />
              )}

            </Form.Item>
            <span style={{ display: 'inline-block', width: '18px', textAlign: 'center' }}></span>
            <Form.Item
              label={(
                <span>
              State&nbsp;
            </span>
              )}
              style={{ display: 'inline-block', width: 'calc(30% - 12px)' }}
            >
              {getFieldDecorator('stateTo', {
                rules: [{ required: true, message: 'Please input your state!' }],
              })(
                <Select
                  placeholder="Select one State"
                  style={{ display: 'inline-block', width: '100%' }}
                  onChange={this.handleChange("to_address.state")}
                >
                  {
                    statesAbbr.map(stAbr=> {
                      return(
                        <Option key={stAbr} value={stAbr}>
                          {stAbr}
                        </Option>
                      );
                    })
                  }
                </Select>
              )}
            </Form.Item>
            <span style={{ display: 'inline-block', width: '18px', textAlign: 'center' }}></span>
            <Form.Item
              label={(
                <span>
              Zip&nbsp;
            </span>
              )}
              style={{ display: 'inline-block', width: 'calc(30% - 12px)' }}
            >
              {getFieldDecorator('zipTo', {
                rules: [{
                  pattern: /(^\d{5}$)|(^\d{5}-\d{4}$)/, message: 'Please input valid zip code!'
                }, {
                  required: true, message: 'Please input your zip code!'
                }],
              })(
                <Input
                  style={{ display: 'inline-block', width: '100%' }}
                  onChange={this.handleChange("to_address.zipcode")}
                />
              )}
            </Form.Item>
          </Form.Item>


          <Form.Item
            style={{ marginBottom: 0 }}
          >
            <Form.Item
              label={(
                <span>
              E-mail&nbsp;
            </span>
              )}
              style={{ display: 'inline-block', width: 'calc(50% - 12px)' }}
            >
              {getFieldDecorator('emailTo', {
                rules: [{
                  type: 'email', message: 'The input is not valid E-mail!',
                }, {
                  required: true, message: 'Please input your email!'
                }],
              })(
                <Input
                  style={{ display: 'inline-block', width: '100%' }}
                  onChange={this.handleChange("receiver_email")}
                />
              )}

            </Form.Item>
            <span style={{ display: 'inline-block', width: '24px', textAlign: 'center' }}></span>
            <Form.Item
              label={(
                <span>
              Phone&nbsp;
            </span>
              )}
              style={{ display: 'inline-block', width: 'calc(50% - 12px)' }}
            >
              {getFieldDecorator('phoneTo', {
                rules: [{ required: false, message: 'Please input your phone number!' }],
              })(
                <Input
                  style={{ display: 'inline-block', width: '100%' }}
                />
              )}
            </Form.Item>
          </Form.Item>

          <hr />      
          <Form.Item
            className="form-title"
          >
            <h1>Enter Yourself Info</h1>
          </Form.Item>

          <Form.Item
            style={{ marginBottom: 0 }}
          >
            <Form.Item
              label={(
                <span>
              First Name&nbsp;
            </span>
              )}
              style={{ display: 'inline-block', width: 'calc(50% - 12px)' }}
            >
              {getFieldDecorator('fName', {
                rules: [{ required: false, message: 'Please input your first name!' }],
              })(
                <Input
                  style={{ display: 'inline-block', width: '100%' }}
                />
              )}

            </Form.Item>
            <span style={{ display: 'inline-block', width: '24px', textAlign: 'center' }}></span>
            <Form.Item
              label={(
                <span>
              Last Name&nbsp;
            </span>
              )}
              style={{ display: 'inline-block', width: 'calc(50% - 12px)' }}
            >
              {getFieldDecorator('lName', {
                rules: [{ required: false, message: 'Please input your last name!' }],
              })(
                <Input
                  style={{ display: 'inline-block', width: '100%' }}
                />
              )}
            </Form.Item>
          </Form.Item>

          <Form.Item
            label={(
              <span>
              Address&nbsp;
            </span>
            )}
          >
            {getFieldDecorator('add', {
              rules: [{ required: true, message: 'Please input your address!' }],
            })(
              <Input onChange={this.handleChange("from_address.street")}/>
            )}
          </Form.Item>


          <Form.Item
            style={{ marginBottom: 0 }}
          >
            <Form.Item
              label={(
                <span>
              City&nbsp;
            </span>
              )}
              style={{ display: 'inline-block', width: 'calc(40% - 12px)' }}
            >
              {getFieldDecorator('city', {
                rules: [{ required: true, message: 'Please input your city!' }],
              })(
                <Input
                  style={{ display: 'inline-block', width: '100%' }}
                  onChange={this.handleChange("from_address.city")}
                />
              )}

            </Form.Item>
            <span style={{ display: 'inline-block', width: '18px', textAlign: 'center' }}></span>
            <Form.Item
              label={(
                <span>
              State&nbsp;
            </span>
              )}
              style={{ display: 'inline-block', width: 'calc(30% - 12px)' }}
            >
              {getFieldDecorator('state', {
                rules: [{ required: true, message: 'Please input your state!' }],
              })(
                <Select
                  placeholder="Select one State"
                  style={{ display: 'inline-block', width: '100%' }}
                  onChange={this.handleChange("from_address.state")}
                >
                  {
                    statesAbbr.map(stAbr=> {
                      return(
                        <Option key={stAbr} value={stAbr}>
                          {stAbr}
                        </Option>
                      );
                    })
                  }
                </Select>
              )}
            </Form.Item>
            <span style={{ display: 'inline-block', width: '18px', textAlign: 'center' }}></span>
            <Form.Item
              label={(
                <span>
              Zip&nbsp;
            </span>
              )}
              style={{ display: 'inline-block', width: 'calc(30% - 12px)' }}
            >
              {getFieldDecorator('zip', {
                rules: [{
                  pattern: /(^\d{5}$)|(^\d{5}-\d{4}$)/, message: 'Please input valid zip code!'
                }, {
                  required: true, message: 'Please input your zip code!'
                }],
              })(
                <Input
                  style={{ display: 'inline-block', width: '100%' }}
                  onChange={this.handleChange("from_address.zipcode")}
                />
              )}
            </Form.Item>
          </Form.Item>


          <Form.Item
            style={{ marginBottom: 0 }}
          >
            <Form.Item
              label={(
                <span>
              E-mail&nbsp;
            </span>
              )}
              style={{ display: 'inline-block', width: 'calc(50% - 12px)' }}
            >
              {getFieldDecorator('email', {
                rules: [{
                  type: 'email', message: 'The input is not valid E-mail!',
                }, {
                  required: true, message: 'Please input your email!'
                }],
              })(
                <Input
                  style={{ display: 'inline-block', width: '100%' }}
                  onChange={this.handleChange("from_address.email")}
                />
              )}

            </Form.Item>
            <span style={{ display: 'inline-block', width: '24px', textAlign: 'center' }}></span>
            <Form.Item
              label={(
                <span>
              Phone&nbsp;
            </span>
              )}
              style={{ display: 'inline-block', width: 'calc(50% - 12px)' }}
            >
              {getFieldDecorator('phone', {
                rules: [{
                  required: false, message: 'Please input your phone number!'
                }],
              })(
                <Input
                  style={{ display: 'inline-block', width: '100%' }}
                />
              )}
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit" className="login-form-button" loading={this.state.loading}>
                Submit
              </Button>
            </Form.Item>
          </Form.Item>

        </Form>

        <div>
        <Modal
          title="oops..." 
          visible={this.state.visible}
          footer={[
            <Button key="back" onClick={this.handleOk}>
              ok
            </Button>
          ]}
        >
          <p>We do not have robots right now. Click ok to back the home page.</p>
          <p>Sorry for inconvenience</p>
        </Modal>
      </div>

      </Fragment>
    );
  }
}

const Destination = Form.create()(DestinationForm);
export default Destination;