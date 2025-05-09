import React, { useEffect, useState } from 'react';
import '../../pages/HRDash/HRDash.css'; // You'll need to create this CSS file
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';

const CreateInternshipModal = ({ show, onClose, onCreate, modalType }) => {
    const token = localStorage.getItem("accessToken");
    const role = jwtDecode(token).role;
    const [hrs, setHrs] = useState([]);
    const [companies, setCompanies] = useState([]);
    const [newInternship, setNewInternship] = useState({
        title: '',
        companyName: '',
        description: '',
        type: 'REMOTE',
        stipend: '',
        duration: '',
        location: '',
        hrId: null
    });
    const [newHr, setNewRHr] = useState({
        username: '',
        company: '',
        email: '',
        password: '',
        fname: ''
    });
    const [newCompany, setNewCompany] = useState({
        name: "",
        industry: "",
        location: ""
    });

    
    useEffect(() => {
        const fetchHrs = async () => {
            try {
                const headers = {
                    Authorization: `Bearer ${token}`,
                };
                const response = await axios.get('http://localhost:8765/user-service/hr', { headers });
                const data = response.data;
                
                // Make sure data is an array
                if (Array.isArray(data)) {
                    setHrs(data);
                    if (response.data.length > 0 && !newInternship.hrId) {
                        setNewInternship(prev => ({
                            ...prev,
                            hrId: response.data[0].id
                        }));
                    }
                } else {
                    console.error('Expected an array but got:', data);
                }
            } catch (error) {
                console.error('Error fetching HRs:', error);
            }
        };
        
        const fetchCompanies = async () => {
            try {
                const headers = {
                    Authorization: `Bearer ${token}`,
                };
                const response = await axios.get('http://localhost:8765/user-service/companies', { headers });
                const data = response.data;
                console.log(data);
                // Make sure data is an array
                if (Array.isArray(data)) {
                    setCompanies(data);
                    if (response.data.length > 0 && !newInternship.hrId) {
                        setNewInternship(prev => ({
                            ...prev,
                            companyName: response.data[0].name
                        }));
                    }
                } else {
                    console.error('Expected an array but got:', data);
                }
            } catch (error) {
                console.error('Error fetching HRs:', error);
            }
        };
        if(role === "ADMIN"){
            fetchHrs();
            fetchCompanies();
        } else {
            return
        }
    }, [token, role]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        if(modalType === "internship"){
            setNewInternship(prev => ({
                ...prev,
                [name]: value
            }));
        }
        else if(modalType === "hr"){
            setNewRHr(prev => ({
                ...prev,
                [name]: value
            }));
        }
        else if(modalType === "company"){
            setNewCompany(prev => ({
                ...prev,
                [name]: value
            }));
        }
    };

    const handleSubmit = (e) => {
        if(modalType === "internship"){
            const submissionData = {
                ...newInternship,
                hrId: parseInt(newInternship.hrId, 10)
            };
            e.preventDefault();
            onCreate(submissionData);
        }
        else if(modalType === "hr"){
            e.preventDefault();
            onCreate(newHr);
        }
        else if(modalType === "company"){
            e.preventDefault();
            onCreate(newCompany);
        }
        
    };

    if (!show) return null;

    if(modalType === "internship"){
        return (
            <div className="modal-overlay">
                <div className="modal-content">
                    <h2>Create New Internship</h2>
                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label>Title</label>
                            <input
                                type="text"
                                name="title"
                                value={newInternship.title}
                                onChange={handleInputChange}
                                placeholder='Enter Title for internship'
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Stipend</label>
                            <input
                                type="text"
                                name="stipend"
                                value={newInternship.stipend || ''}
                                onChange={handleInputChange}
                                placeholder='Example $300/month or not Paid'
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Type</label>
                            <select
                                name="type"
                                value={newInternship.type || ''}
                                onChange={handleInputChange}
                                required
                            >
                                <option value="REMOTE">Remote</option>
                                <option value="HYBRID">Hybrid</option>
                                <option value="PART_TIME">Part-Time</option>
                                <option value="FULL_TIME">Full-Time</option>
                                
                            </select>
                        </div>
                        {role === "ADMIN" ? (
                            <>
                                <div className="form-group">
                                    <label>HR</label>
                                    <select
                                        name="hrId"
                                        value={newInternship.hrId || ''}
                                        onChange={handleInputChange}
                                        required
                                    >
                                        {hrs.map(hr=>(
                                            <option key={hr.id} value={hr.id}>{hr.user.fname || "Un named HR"}</option>
                                        ))}
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label>Company</label>
                                    <select
                                        name="companyName"
                                        value={newInternship.companyName || ''}
                                        onChange={handleInputChange}
                                        required
                                    >
                                        {companies.map(company=>(
                                            <option key={company.id} value={company.name}>{company.name}</option>
                                        ))}
                                        
                                    </select>
                                </div>
                            </>
                            ): null}

                        <div className="form-group">
                            <label>Location</label>
                            <input
                                type="text"
                                name="location"
                                value={newInternship.location || ''}
                                onChange={handleInputChange}
                                placeholder='Enter the location for internship'
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Duration</label>
                            <input
                                type="text"
                                name="duration"
                                value={newInternship.duration || ''}
                                onChange={handleInputChange}
                                placeholder='Example 8 months'
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Description</label>
                            <textarea
                                name="description"
                                value={newInternship.description}
                                onChange={handleInputChange}
                                placeholder='Enter Description of internship'
                                required
                            />
                        </div>
                        
                        {/* Add more fields as needed */}
                        
                        <div className="modal-actions">
                            <button type="button" className='cancel-button' onClick={onClose}>Cancel</button>
                            <button type="submit" className='submit-button' >Create</button>
                        </div>
                    </form>
                </div>
            </div>
        );
    } else if (modalType === "company"){
        return (
            <div className="modal-overlay">
                <div className="modal-content">
                    <h2>Add New Company</h2>
                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label>Name</label>
                            <input
                                type="text"
                                name="name"
                                value={newCompany.name}
                                onChange={handleInputChange}
                                placeholder='Enter Name of Company'
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Industry</label>
                            <input
                                type="text"
                                name="industry"
                                value={newCompany.industry || ''}
                                onChange={handleInputChange}
                                placeholder='Enter Industry of Company'
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Location</label>
                            <input
                                type="text"
                                name="location"
                                value={newCompany.location || ''}
                                onChange={handleInputChange}
                                placeholder='Enter Location for Company'
                                required
                            />
                        </div>
                        <div className="modal-actions">
                            <button type="button" className='cancel-button' onClick={onClose}>Cancel</button>
                            <button type="submit" className='submit-button' >Create</button>
                        </div>
                    </form>
                </div>
            </div>
        );
    } else if (modalType === "hr"){
        return (
            <div className="modal-overlay">
                <div className="modal-content">
                    <h2>Add New HR</h2>
                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label>Full Name</label>
                            <input
                                type="text"
                                name="fname"
                                value={newHr.fname}
                                onChange={handleInputChange}
                                placeholder='Enter Full Name for HR'
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Username</label>
                            <input
                                type="text"
                                name="username"
                                value={newHr.username || ''}
                                onChange={handleInputChange}
                                placeholder='Enter Username for HR'
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Email</label>
                            <input
                                type="email"
                                name="email"
                                value={newHr.email || ''}
                                onChange={handleInputChange}
                                placeholder='Enter Email for HR'
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Password</label>
                            <input
                                type="password"
                                name="password"
                                value={newHr.password || ''}
                                onChange={handleInputChange}
                                placeholder='Enter Password for HR'
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Company</label>
                            <select
                                name="companyName"
                                value={newInternship.companyName || ''}
                                onChange={handleInputChange}
                                required
                            >
                                {companies.map(company=>(
                                    <option key={company.id} value={company.name}>{company.name}</option>
                                ))}
                                
                            </select>
                        </div>
                        <div className="modal-actions">
                            <button type="button" className='cancel-button' onClick={onClose}>Cancel</button>
                            <button type="submit" className='submit-button' >Create</button>
                        </div>
                    </form>
                </div>
            </div>
        );
    }
};

export default CreateInternshipModal;