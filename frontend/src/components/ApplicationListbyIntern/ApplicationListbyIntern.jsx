import React, { useState } from 'react';
import { FiChevronDown, FiChevronUp } from 'react-icons/fi';
import style from '../../pages/HRDash/HRDash.module.css';

const ApplicationsList = ({ applications, onStatusChange }) => {
    const [isExpanded, setIsExpanded] = useState(false);

    return (
        <div className={style["applications-section"]}>
            <div 
                className={style["section-header"]} 
                onClick={() => setIsExpanded(!isExpanded)}
                style={{ cursor: 'pointer' }}
            >
                <h2>Applications</h2>
                <span className={style["toggle-arrow"]}>
                    {isExpanded ? <FiChevronUp size={24} /> : <FiChevronDown size={24} />}
                </span>
            </div>
            
            {!isExpanded ? null : applications.length === 0 ? (
                <p className={style["no-applications"]}>No applications found</p>
            ) : (
                <div className={style["applications-list"]}>
                    {applications.map((application) => (
                        <div key={application.id} className={style["application-card"]}>
                            <div className={style["student-info"]}>
                                <h3>{application.studentName}</h3>
                                <div className={style["student-details"]}>
                                    <p><strong>College:</strong> {application.studentCollege}</p>
                                    <p><strong>Faculty:</strong> {application.studentFaculty}</p>
                                    <p><strong>Grade:</strong> {application.studentGrade}</p>
                                    <p><strong>CV:</strong> 
                                        <a href={`/path/to/cvs/${application.studentCV}`} target="_blank" rel="noopener noreferrer">
                                            {application.studentCV}
                                        </a>
                                    </p>
                                </div>
                            </div>
                            
                            <div className={style["application-meta"]}>
                                <div className={`${style["status-badge"]} ${style[application.status.toLowerCase()]}`}>
                                    {application.status}
                                </div>
                                <p className={style["applied-date"]}>
                                    Applied: {new Date(application.appliedAt).toLocaleDateString()}
                                </p>
                            </div>
                            
                            {application.status === "PENDING" && (
                                <div className={style["application-actions"]}>
                                    <button 
                                        className={style["accept-btn"]}
                                        onClick={() => onStatusChange(application.id, 'ACCEPTED')}
                                        disabled={application.status === 'ACCEPTED'}
                                    >
                                        Accept
                                    </button>
                                    <button 
                                        className={style["reject-btn"]}
                                        onClick={() => onStatusChange(application.id, 'REJECTED')}
                                        disabled={application.status === 'REJECTED'}
                                    >
                                        Reject
                                    </button>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default ApplicationsList;