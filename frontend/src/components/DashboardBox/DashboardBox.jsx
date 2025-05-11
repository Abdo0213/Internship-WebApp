import React from 'react';
import { Link } from 'react-router-dom';
import style from './DashboardBox.module.css';

const DashboardBox = ({ title, count, addLink, viewLink }) => {
    return (
        <div className={style["dashboard-box"]}>
        <h3>{title}</h3>
        <div className={style["count"]}>{count}</div>
        <div className={style["box-actions"]}>
            <Link to={viewLink} className={`${style["btn"]} ${style["btn-view"]}`}>View All</Link>
        </div>
        </div>
    );
};

export default DashboardBox;