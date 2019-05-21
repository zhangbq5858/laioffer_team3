import React from 'react';
import { Link } from 'react-router-dom';

const Success = props => {
  return(
    <div className="success-page">
      <h1>Make Order Successfully {' '}</h1>
      <div>
        <p> Your Order Number is: {' '}<span style={{fontWeight:'bold'}}>{props.orderId}</span></p>
        <span className="order-link" onClick={() => props.setPage("0")}>Make another order? {' '}</span>
        <span>Go Back <Link to="/">Home</Link></span>
      </div>
    </div>
  );
}

export default Success;