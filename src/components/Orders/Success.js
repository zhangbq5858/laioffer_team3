import React, { Component, Fragment } from 'react';
import { Link } from 'react-router-dom';

const Success = props => {
  return(
    <div className="success-page">
      <h1>Make Order Successfully {' '}</h1>
      <p>
        <span className="order-link" onClick={() => props.setPage("0")}>Make another order? {' '}</span>
        <span>Go Back <Link to="/"><span onClick={() => props.setPath("")}>Home</span></Link></span>
      </p>
    </div>
  );
}

export default Success;