import React from 'react';
import '../../pages/HRDash/HRDash.css';

const InternshipDetails = ({ internship, onEditClick, onCreateNewClick }) => {
    return (
        <div className="details-section">
            <div className="content-header">
                <h2>{internship.title}</h2>
                <div className="button-group">
                    <button className="edit-btn" onClick={onEditClick}>
                        Edit Internship
                    </button>
                </div>
            </div>

            <div className="internship-details">
                <div className="application-meta">
                    <div className={`status-badge ${internship.status.toLowerCase()}`}>
                        {internship.status}
                    </div>
                </div>
                <div className='details-grid'>
                    <div className="detail-item">
                        <span className="detail-label">Company:</span>
                        <span className="detail-value">{internship.companyName}</span>
                    </div>
                    <div className="detail-item">
                        <span className="detail-label">Type:</span>
                        <span className="detail-value">{internship.type}</span>
                    </div>
                    <div className="detail-item">
                        <span className="detail-label">Stipend:</span>
                        <span className="detail-value">{internship.stipend}</span>
                    </div>
                    <div className="detail-item">
                        <span className="detail-label">Location:</span>
                        <span className="detail-value">{internship.location}</span>
                    </div>
                    <div className="detail-item">
                        <span className="detail-label">Duration:</span>
                        <span className="detail-value">{internship.duration}</span>
                    </div>
                    <div className="detail-item">
                        <span className="detail-label">Posted:</span>
                        <span className="detail-value">
                            {new Date(internship.createdAt).toLocaleDateString()}
                        </span>
                    </div>
                </div>
            </div>

            <div className="description-section">
                <h3>Description</h3>
                <p>{internship.description}</p>
            </div>
        </div>
    );
};

export default InternshipDetails;