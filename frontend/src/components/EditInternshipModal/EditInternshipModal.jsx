import React from 'react';
import '../../pages/HRDash/HRDash.css';

const EditInternshipModal = ({ 
    internship, 
    onSave, 
    onCancel, 
    onInputChange 
}) => {
    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <h2>Edit Internship</h2>
                
                <form onSubmit={(e) => {
                    e.preventDefault();
                    onSave();
                }}>
                    <div className="form-group">
                        <label>Title</label>
                        <input
                            type="text"
                            name="title"
                            value={internship.title || ''}
                            onChange={onInputChange}
                            required
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Stipend</label>
                            <input
                                type="text"
                                name="stipend"
                                value={internship.stipend || ''}
                                onChange={onInputChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Type</label>
                            <select
                                name="type"
                                value={internship.type || ''}
                                onChange={onInputChange}
                                required
                            >
                                <option value="REMOTE">Remote</option>
                                <option value="HYBRID">Hybrid</option>
                                <option value="PART_TIME">Part-Time</option>
                                <option value="FULL_TIME">Full-Time</option>
                                
                            </select>
                        </div>
                    </div>
                    <div className="form-row">
                        <div className="form-group">
                            <label>Statue</label>
                            <select
                                name="status"
                                value={internship.status || ''}
                                onChange={onInputChange}
                                required
                            >
                                <option value="ACTIVE">Active</option>
                                <option value="EXPIRED">Expired</option>
                                <option value="FILLED">Filled</option>
                            </select>
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Location</label>
                            <input
                                type="text"
                                name="location"
                                value={internship.location || ''}
                                onChange={onInputChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Duration</label>
                            <input
                                type="text"
                                name="duration"
                                value={internship.duration || ''}
                                onChange={onInputChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Description</label>
                        <textarea
                            name="description"
                            value={internship.description || ''}
                            onChange={onInputChange}
                            rows={5}
                            required
                        />
                    </div>

                    <div className="modal-actions">
                        <button 
                            type="button" 
                            className="cancel-button"
                            onClick={onCancel}
                        >
                            Cancel
                        </button>
                        <button 
                            type="submit" 
                            className="submit-button"
                        >
                            Save Changes
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EditInternshipModal;