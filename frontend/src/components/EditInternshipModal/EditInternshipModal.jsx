import React from 'react';
import style from '../../pages/HRDash/HRDash.module.css';

const EditInternshipModal = ({ 
    item, 
    onSave, 
    onCancel, 
    onInputChange,
    modalType

}) => {
    if (modalType === "internship"){
        return (
            <div className={style["modal-overlay"]}>
                <div className={style["modal-content"]}>
                    <h2>Edit Internship</h2>
                    
                    <form onSubmit={(e) => {
                        e.preventDefault();
                        onSave();
                    }}>
                        <div className={style["form-group"]}>
                            <label>Title</label>
                            <input
                                type="text"
                                name="title"
                                value={item.title || ''}
                                onChange={onInputChange}
                                required
                            />
                        </div>
                        <div className={style["form-group"]}>
                            <label>Stipend</label>
                            <input
                                type="text"
                                name="stipend"
                                value={item.stipend || ''}
                                onChange={onInputChange}
                                required
                            />
                        </div>
                        <div className={style["form-group"]}>
                            <label>Type</label>
                            <select
                                name="type"
                                value={item.type || ''}
                                onChange={onInputChange}
                                required
                            >
                                <option value="REMOTE">Remote</option>
                                <option value="HYBRID">Hybrid</option>
                                <option value="PART_TIME">Part-Time</option>
                                <option value="FULL_TIME">Full-Time</option>
                                
                            </select>
                        </div>
                        <div className={style["form-group"]}>
                            <label>Statue</label>
                            <select
                                name="status"
                                value={item.status || ''}
                                onChange={onInputChange}
                                required
                            >
                                <option value="ACTIVE">Active</option>
                                <option value="EXPIRED">Expired</option>
                                <option value="FILLED">Filled</option>
                            </select>
                        </div>
                        <div className={style["form-group"]}>
                            <label>Location</label>
                            <input
                                type="text"
                                name="location"
                                value={item.location || ''}
                                onChange={onInputChange}
                                required
                            />
                        </div>
                        <div className={style["form-group"]}>
                            <label>Duration</label>
                            <input
                                type="text"
                                name="duration"
                                value={item.duration || ''}
                                onChange={onInputChange}
                                required
                            />
                        </div>
                        <div className={style["form-group"]}>
                            <label>Description</label>
                            <textarea
                                name="description"
                                value={item.description || ''}
                                onChange={onInputChange}
                                rows={5}
                                required
                            />
                        </div>
                        <div className={style["modal-actions"]}>
                            <button 
                                type="button" 
                                className={style["cancel-button"]}
                                onClick={onCancel}
                            >
                                Cancel
                            </button>
                            <button 
                                type="submit" 
                                className={style["submit-button"]}
                            >
                                Save Changes
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        );
    }
    else if (modalType === "company"){
        return (
            <div className={style["modal-overlay"]}>
                <div className={style["modal-content"]}>
                    <h2>Edit Company</h2>
                    
                    <form onSubmit={(e) => {
                        e.preventDefault();
                        onSave();
                    }}>
                        <div className={style["form-group"]}>
                            <label>Name</label>
                            <input
                                type="text"
                                name="name"
                                value={item.name || ''}
                                onChange={onInputChange}
                                required
                            />
                        </div>
                        <div className={style["form-group"]}>
                            <label>Industry</label>
                            <input
                                type="text"
                                name="industry"
                                value={item.industry || ''}
                                onChange={onInputChange}
                                required
                            />
                        </div>
                        <div className={style["form-group"]}>
                            <label>Location</label>
                            <input
                                type="text"
                                name="location"
                                value={item.location || ''}
                                onChange={onInputChange}
                                required
                            />
                        </div>
                        <div className={style["modal-actions"]}>
                            <button 
                                type="button" 
                                className={style["cancel-button"]}
                                onClick={onCancel}
                            >
                                Cancel
                            </button>
                            <button 
                                type="submit" 
                                className={style["submit-button"]}
                            >
                                Save Changes
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        );
    }
};

export default EditInternshipModal;