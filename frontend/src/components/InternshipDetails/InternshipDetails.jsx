import React from 'react';
import style from './InternshipDetails.module.css';

const InternshipDetails = ({ internship, onEditClick, onCreateNewClick }) => {
    return (
        <div className={style["details-section"]}>
            <div className={style["content-header"]}>
                <h2>{internship.title}</h2>
                <div className={style["button-group"]}>
                    <button className={style["edit-btn"]} onClick={onEditClick}>
                        Edit Internship
                    </button>
                </div>
            </div>

            <div className={style["internship-details"]}>
                <div className={style["application-meta"]}>
                    <div className={`${style["status-badge"]} ${style[internship.status.toLowerCase()]}`}>
                        {internship.status}
                    </div>
                </div>
                <div className={style['details-grid']}>
                    <div className={style["detail-item"]}>
                        <span className={style["detail-label"]}>Company:</span>
                        <span className={style["detail-value"]}>{internship.companyName}</span>
                    </div>
                    <div className={style["detail-item"]}>
                        <span className={style["detail-label"]}>Type:</span>
                        <span className={style["detail-value"]}>{internship.type}</span>
                    </div>
                    <div className={style["detail-item"]}>
                        <span className={style["detail-label"]}>Stipend:</span>
                        <span className={style["detail-value"]}>{internship.stipend}</span>
                    </div>
                    <div className={style["detail-item"]}>
                        <span className={style["detail-label"]}>Location:</span>
                        <span className={style["detail-value"]}>{internship.location}</span>
                    </div>
                    <div className={style["detail-item"]}>
                        <span className={style["detail-label"]}>Duration:</span>
                        <span className={style["detail-value"]}>{internship.duration}</span>
                    </div>
                    <div className={style["detail-item"]}>
                        <span className={style["detail-label"]}>Posted:</span>
                        <span className={style["detail-value"]}>
                            {new Date(internship.createdAt).toLocaleDateString()}
                        </span>
                    </div>
                </div>
            </div>

            <div className={style["description-section"]}>
                <h3>Description</h3>
                <p>{internship.description}</p>
            </div>
        </div>
    );
};

export default InternshipDetails;