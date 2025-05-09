import React, { useState } from 'react';
import { FiChevronDown, FiChevronUp } from 'react-icons/fi';
import '../../pages/HRDash/HRDash.css';

const ApplicationsList = ({ applications, onStatusChange }) => {
    const [isExpanded, setIsExpanded] = useState(false);

    return (
        <div className="applications-section">
            <div 
                className="section-header" 
                onClick={() => setIsExpanded(!isExpanded)}
                style={{ cursor: 'pointer' }}
            >
                <h2>Applications</h2>
                <span className="toggle-arrow">
                    {isExpanded ? <FiChevronUp size={24} /> : <FiChevronDown size={24} />}
                </span>
            </div>
            
            {!isExpanded ? null : applications.length === 0 ? (
                <p className="no-applications">No applications found</p>
            ) : (
                <div className="applications-list">
                    {applications.map((application) => (
                        <div key={application.id} className="application-card">
                            <div className="student-info">
                                <h3>{application.studentName}</h3>
                                <div className="student-details">
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
                            
                            <div className="application-meta">
                                <div className={`status-badge ${application.status.toLowerCase()}`}>
                                    {application.status}
                                </div>
                                <p className="applied-date">
                                    Applied: {new Date(application.appliedAt).toLocaleDateString()}
                                </p>
                            </div>
                            
                            {application.status === "PENDING" && (
                                <div className="application-actions">
                                    <button 
                                        className="accept-btn"
                                        onClick={() => onStatusChange(application.id, 'ACCEPTED')}
                                        disabled={application.status === 'ACCEPTED'}
                                    >
                                        Accept
                                    </button>
                                    <button 
                                        className="reject-btn"
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