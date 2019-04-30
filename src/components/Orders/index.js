import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Steps, Button, message, Row, Col } from 'antd';
import Destination from './Destination';
import MyInfo from './MyInfo';
import Deliver from './Deliver';
import Payment from './Payment';

const Step = Steps.Step;

const steps = [{
  title: 'Destination',
  description: 'Enter destination info',
  content: 'Destination-content',
}, {
  title: 'My Info',
  description: 'Enter my info',
  content: 'My Info-content',
}, {
  title: 'Deliver Methods',
  description: 'Enter deliver methods',
  content: 'Deliver Methods-content',
}, {
  title: 'Payment',
  description: 'Enter payment info',
  content: 'Payment-content',
}];

class Orders extends Component {
  constructor(props) {
    super(props);
    this.state = {
      current: 0,
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
          <Col span={7}>
            <div className="steps-content">
              {current === 0  && (<Destination/>)}
              {current === 1  && (<MyInfo/>)}
              {current === 2  && (<Deliver/>)}
              {current === 3  && (<Payment/>)}
            </div>
            <div className="steps-action">
              <Row type="flex" justify="center">
                <Col span={12}>
                  {
                    current > 0
                    && (
                      <Button onClick={() => this.prev()}>
                        Back
                      </Button>
                    )
                  }
                </Col>
                <Col span={12}>
                  {
                    current < steps.length - 1
                    && <Button type="primary" onClick={() => this.next()}>Next</Button>
                  }
                  {
                    current === steps.length - 1
                    &&
                    <Button
                      type="danger"
                      onClick={() => message.success('Processing complete!')}>
                      <Link to="/success">Make Order</Link>
                    </Button>
                  }
                </Col>
              </Row>



            </div>
          </Col>
        </Row>

      </div>
    );
  }
}

export default Orders;