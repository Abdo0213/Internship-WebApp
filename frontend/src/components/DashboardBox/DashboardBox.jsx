import React from 'react';
import { Link } from 'react-router-dom';
import './DashboardBox.css';

const DashboardBox = ({ title, count, addLink, viewLink }) => {
    return (
        <div className="dashboard-box">
        <h3>{title}</h3>
        <div className="count">{count}</div>
        <div className="box-actions">
            <Link to={addLink} className="btn btn-add">Add New</Link>
            <Link to={viewLink} className="btn btn-view">View All</Link>
        </div>
        </div>
    );
};

export default DashboardBox;