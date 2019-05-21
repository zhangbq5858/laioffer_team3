import React, { Component, Fragment } from 'react';
import { Link } from 'react-router-dom';

const Login = () => {
  return(
    <Fragment>
      <h1>Login Page</h1>
      <p>No account? {' '}
        <span><Link to="/register">Register Now!</Link></span>
      </p>
    </Fragment>
  );
}

export default Login;