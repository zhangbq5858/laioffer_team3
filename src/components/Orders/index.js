import React, { Component } from 'react';
import { Steps, Row, Col } from 'antd';
import Destination from './Destination';
import MyInfo from './MyInfo';
import Deliver from './Deliver';
import Payment from './Payment';
import Success from './Success';

const Step = Steps.Step;

const steps = [{
  title: 'Destination & My Info',
  description: 'Enter destination & My info',
  content: 'Destination-content',
}, {
  title: 'Deliver Methods',
  description: 'Enter deliver methods',
  content: 'Deliver Methods-content',
},
//   {
//   title: 'Payment',
//   description: 'Enter payment info',
//   content: 'Payment-content',
// }
];

class Orders extends Component {
  constructor(props) {
    super(props);
    this.state = {
      current: 0,
      order_id: "",
      robots: [],
      //   to_fName:"",
      //   to_lName:"",
      //   to_street:"",
      //   to_city:"",
      //   to_state:"",
      //   to_zipcode:"",
      //   to_email:"",
      //   to_phone:"",
      //
      //   from_fName:"",
      //   from_lName:"",
      //   from_street:"",
      //   from_city:"",
      //   from_state:"",
      //   from_zipcode:"",
      //   from_email:"",
      //   from_phone:"",
    };
  }

  next() {
    const current = this.state.current + 1;
    this.setState({ current });
  }

  prev() {
    const current = this.state.current - 1;
    this.setState({ current });
  }

  setPage = page => {
    // const current = this.state.current + 1;
    this.setState({ current: Number(page) });
  }

  handleRobotInfo = (order_id, robots) => {
    this.setState({
      order_id,
      robots,
    })
  }

  getRobotInfo = () => {
    return [this.state.order_id, this.state.robots];
  }

  // handleChange = input => event => {
  //   if (input === "to_address.state" || input === "from_address.state") {
  //     this.setState({ [input]: event });
  //   } else {
  //     this.setState({ [input]: event.target.value });
  //   }
  // };

  render() {
    // const { current, to_fName, to_lName, to_street, to_city, to_state, to_zipcode, to_email, to_phone, from_fName, from_lName, from_street, from_city, from_state, from_zipcode, from_email, from_phone} = this.state;
    // const values = { to_fName, to_lName, to_street, to_city, to_state, to_zipcode, to_email, to_phone, from_fName, from_lName, from_street, from_city, from_state, from_zipcode, from_email, from_phone};
    const { current } = this.state;

    return (
      <div>
        <div>

        </div>
        <Row type="flex" >
          <Col span={5}>
            <Steps direction="vertical" size="small" current={current}>
              {steps.map(item => <Step key={item.title} title={item.title} description={item.description}/>)}
            </Steps>
          </Col>
          <Col span={19}>
            <div className="steps-content">

              {current === 0  && (<Destination setPage={this.setPage} handleRobotInfo={this.handleRobotInfo}/>)}
              {current === 1  && (<Deliver setPage={this.setPage} getRobotInfo={this.getRobotInfo} history={this.props.history}/>)}
              {current === 2  && (<Success />)}

            </div>
          </Col>
        </Row>

      </div>
    );
  }
}

export default Orders;