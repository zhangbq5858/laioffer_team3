import React, { Fragment } from 'react';
import { Link } from 'react-router-dom';
import { Row, Col } from 'antd';
import history_icon from '../assets/images/history.png';
import order_icon from '../assets/images/order.png';
import "../styles/Home.css";

const Home = props => {
	return (
		<Fragment>
			<Row type="flex" justify="space-around" align="middle">
				<Col span={8}>
					<Link to="/orders">
					<img className="img-btn" src={order_icon} alt="order" />
					  <div className="home-select-btn">
					  		
							<span>Make Orders</span>
						</div>
					</Link>
				</Col>

				<Col span={8}>
					<Link to="/tracking">
					<img className="img-btn" src={history_icon} alt="history" />
						<div className="home-select-btn">
							
							<span>Tracking</span>
						</div>
					</Link>
				</Col>
			</Row>
		</Fragment>
	);
}

export default Home;