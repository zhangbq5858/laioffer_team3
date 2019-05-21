import React, { Component } from 'react';
import { Steps, Row, Col } from 'antd';
import Destination from './Destination';
import Deliver from './Deliver';
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
      loading: true,
    };
  }

  setPage = page => {
    // const current = this.state.current + 1;
    this.setState({ current: Number(page) });
  }

  handleRobotInfo = (order_id, robots, loading) => {
    this.setState({
      order_id,
      robots,
      loading,
    })
  }

  getRobotInfo = () => {
    return [this.state.order_id, this.state.robots];
  }

  render() {
    const { current } = this.state;
    return (
      <div>
        <Row type="flex" >
          <Col span={5}>
            <Steps direction="vertical" size="small" current={current}>
              {steps.map(item => <Step key={item.title} title={item.title} description={item.description}/>)}
            </Steps>
          </Col>
          <Col span={19}>
            <div className="steps-content">

              {current === 0  && (<Destination setPage={this.setPage} handleRobotInfo={this.handleRobotInfo} history={this.props.history}/>)}
              {current === 1  && (<Deliver setPage={this.setPage} getRobotInfo={this.getRobotInfo} history={this.props.history}/>)}
              {current === 2  && (<Success setPage={this.setPage} orderId={this.state.order_id}/>)}

            </div>
          </Col>
        </Row>
      </div>
    );
  }
}
export default Orders;