import React, { Component, Fragment } from 'react';
import { Link } from 'react-router-dom';

const Success = () => {
  return(
    <Fragment>
      <h1>Success Page {' '}</h1>
      <p><Link to="/orders">Make another order? {' '}</Link> <span>Go Back <Link to="/">Home</Link></span></p>
    </Fragment>
  );
}

export default Success;