import React, { Component, } from 'react';
import { Form, Input, Select, } from 'antd';

const { Option } = Select;


const statesAbbr = ['AL', 'AK', 'AS', 'AZ', 'AR', 'CA', 'CO', 'CT', 'DE', 'DC', 'FM', 'FL', 'GA', 'GU', 'HI', 'ID', 'IL', 'IN', 'IA', 'KS', 'KY', 'LA', 'ME', 'MH', 'MD', 'MA', 'MI', 'MN', 'MS', 'MO', 'MT', 'NE', 'NV', 'NH', 'NJ', 'NM', 'NY', 'NC', 'ND', 'MP', 'OH', 'OK', 'OR', 'PW', 'PA', 'PR', 'RI', 'SC', 'SD', 'TN', 'TX', 'UT', 'VT', 'VI', 'VA', 'WA', 'WV', 'WI', 'WY', 'AE', 'AA', 'AP'];

class MyInfoForm extends Component{
  constructor(props) {
    super(props);
    this.state = {
      confirmDirty: false,
    };
  }

  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        console.log('Received values of form: ', values);
      }
    });
  }

  render() {
    const { getFieldDecorator } = this.props.form;

    return (
      <Form onSubmit={this.handleSubmit} className="form-wrapper">

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
              rules: [{ required: true, message: 'Please input your first name!' }],
            })(
              <Input
                style={{ display: 'inline-block', width: '100%' }}
                onChange={this.props.handleChange("from_address.fName")}
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
              rules: [{ required: true, message: 'Please input your last name!' }],
            })(
              <Input
                style={{ display: 'inline-block', width: '100%' }}
                onChange={this.props.handleChange("from_address.lName")}
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
            <Input onChange={this.props.handleChange("from_address.street")}/>
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
                onChange={this.props.handleChange("from_address.city")}
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
              <Select placeholder="Select one State" style={{ display: 'inline-block', width: '100%' }} onChange={this.props.handleChange("from_address.state")}>
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
                onChange={this.props.handleChange("from_address.zipcode")}
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
                onChange={this.props.handleChange("from_address.email")}
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
              rules: [{ required: true, message: 'Please input your phone number!' }],
            })(
              <Input
                style={{ display: 'inline-block', width: '100%' }}
                onChange={this.props.handleChange("from_address.phone")}
              />
            )}
          </Form.Item>
        </Form.Item>

      </Form>
    );
  }
}

const MyInfo = Form.create()(MyInfoForm);

export default MyInfo;