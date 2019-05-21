import React, { Component } from 'react';
import { BrowserRouter, Route, Switch, Link } from 'react-router-dom';
import { Layout, Breadcrumb, } from 'antd';
import Home from './Home';
import Orders from './Orders';
import Tracking from './Tracking';
import Login from './Login';
import Register from './Register';
import logo from '../assets/images/logo.svg';

import '../styles/App.css';

const { Content, Footer, Header } = Layout;

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      path: "",
    }
  }
  handlePath = path => {
    this.setState({
      path,
    })
  }

  render() {
    return (
      <BrowserRouter>
        <div className="App">
          <Layout className="layout">
            <Header style={{ position: 'fixed', zIndex: 1, width: '100%' }}>
              <img src={logo} className="App-logo" alt="logo" />
              <div className="logo">
                <Link to="/" >
                  <p className="App-name" onClick={() => this.handlePath("")}>Smart Shipping</p>
                </Link>
              </div>
                
              <div className="user-status">
                <Link to="/login" ><p className="user-status-text">Sign Up/Login</p></Link>
              </div>
            </Header>

            <Content style={{ padding: '90px 60px 0', }}>
              <Breadcrumb style={{ margin: '16px 0' }}>
                <Breadcrumb.Item></Breadcrumb.Item>
              </Breadcrumb>
              <div style={{ background: '#fff', padding: 24, minHeight: 760 }}>
                <Switch>
                  <Route exact path="/" component={Home} />
                  <Route exact path="/orders" component={Orders} />
                  <Route exact path="/tracking" component={Tracking} />
                  <Route exact path="/login" component={Login} />
                  <Route exact path="/register" component={Register} />
                </Switch>
              </div>
            </Content>

            <Footer style={{ textAlign: 'center' }}>
              Smart Shipping (Team3)©2019 Created by Ant UED
            </Footer>
          </Layout>
        </div>
      </BrowserRouter>
    );
  }
}

export default (App);