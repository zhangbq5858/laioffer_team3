import React, { Component, Fragment } from 'react';
import {  Route, Switch, Link } from 'react-router-dom';
import { Row, Col } from 'antd';
import "../styles/Home.css";

const Home = () => {
	return (
		<Fragment>
			<Row type="flex" justify="space-around" align="middle">
				<Col span={8}>
					<Link to="/orders">
					  <div className="home-select-btn">
							<span>Make Orders</span>
						</div>
					</Link>
				</Col>

				<Col span={8}>
					<Link to="/tracking">
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