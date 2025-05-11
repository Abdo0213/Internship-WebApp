import { useState, useEffect, useCallback, useRef } from 'react';
import axios from 'axios';
import style from'./Internship.module.css';
import Navbar from '../../components/Navbar/Navbar';
import { useNavigate } from 'react-router-dom'; 


const STUDENT_FIELDS = [
    { key: 'fName', label: 'Full Name', editable: true },
    { key: 'email', label: 'Email', editable: true, type: 'email' },
    { key: 'college', label: 'College', editable: true },
    { key: 'faculty', label: 'Faculty', editable: true },
    { key: 'cv', label: 'Upload CV (PDF only)', editable: true, type: 'file' },
    { key: 'grade', label: 'Grade', editable: true, type: 'number' },
    { key: 'phone', label: 'Phone Number', editable: true, type: 'tel' },
    { key: 'yearOfGraduation', label: 'Year of Graduation', editable: true, type: 'number' }
];

const ApplicationForm = ({ onClose, studentId, internshipId }) => {
    const [formData, setFormData] = useState({});
    const [cvFile, setCvFile] = useState(null);
    const formRef = useRef(null);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    
    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        if (!token) {
            navigate('/login');
            return;
        } else{
            const fetchUserData = async () => {
                const token = localStorage.getItem("accessToken");
                try {
                    const response = await axios.get(`http://localhost:8765/user-service/students/${studentId}`, 
                        {headers: {
                            'Authorization': `Bearer ${token}`
                        },
                        withCredentials: true 
                    });
                    const userData = response.data;
                    const transformedData = {
                        ...userData.user,  // Spread user object properties
                        ...userData        // Spread root properties (overwrites duplicates)
                    };
                    if (transformedData.user) {
                        delete transformedData.user;
                    }
                    if (transformedData.fname) {
                        transformedData.fName = transformedData.fname;
                        delete transformedData.fname;
                    }
                    setFormData(transformedData); // should match STUDENT_FIELDS keys
    
                } catch (error) {
                    console.error('Error fetching user data:', error);
                }
                };
            
                fetchUserData();
        }
        
        }, [studentId, navigate]);

    // Close when clicking outside the form
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (formRef.current && !formRef.current.contains(event.target)) {
                onClose();
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [onClose]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleFileChange = (e) => {
        setCvFile(e.target.files[0]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
    
        const token = localStorage.getItem("accessToken");
        if (!token) {
            navigate('/login');
        }
    
        const finalData = {
            ...formData,
            cv: cvFile ? cvFile.name : null,
            password: null, // ensure password is not sent
        };
    
        const applicationRequest = {
            studentId: studentId,
            internshipId: internshipId
        };
    
        try {
            // 1. Update student profile
            const userResponse = await axios.put(
                `http://localhost:8765/user-service/students/${studentId}`,
                finalData,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    }
                }
            );
    
            if (userResponse.status !== 200) {
                throw new Error('Failed to update user');
            }
    
            // 2. Submit application
            console.log(applicationRequest);
            const appResponse = await axios.post(
                `http://localhost:8765/internship-service/applications`,
                applicationRequest,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    }
                }
            );
    
            if (appResponse.status === 200 || appResponse.status === 201) {
                alert('Application submitted successfully!');
            } else {
                throw new Error('Failed to submit application');
            }
        } catch (err) {
            console.error(err);
    
            // ✅ Proper error handling using err not error
            if (err.response) {
                const backendMessage = err.response.data?.message || JSON.stringify(err.response.data);
                alert(`Error: ${backendMessage}`);
                setError(backendMessage);
            } else if (err.request) {
                alert('No response from server.');
                setError('No response from server.');
            } else {
                alert(`Error: ${err.message}`);
                setError(err.message || 'An unexpected error occurred');
            }
        }
    };
    
    return (
        <div className={style["overlay"]}>
            <div className={style["application-form"]} ref={formRef}>
            <div className={style["form-header"]}>
                <h2>Internship Application</h2>
                <button className={style["close-btn"]} onClick={onClose}>
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M18 6L6 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        <path d="M6 6L18 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                </button>
            </div>
            
            <form onSubmit={handleSubmit}>
                <div className={style["form-grid"]}>
                {STUDENT_FIELDS.map(field => {
                    return (
                    <div className={`${style["form-group"]} ${style[field.type === 'file' ? 'full-width' : '']}`} key={field.key}>
                        <label htmlFor={field.key}>
                            {field.label}
                            {field.required && <span className={style["required"]}>*</span>}
                        </label>
                        {field.type === 'file' ? (
                            <div className={style["file-upload"]}>
                                <input
                                type="file"
                                id={field.key}
                                name={field.key}
                                accept=".pdf"
                                onChange={handleFileChange}
                                required
                                />
                                <div className={style["file-upload-label"]}>
                                {cvFile ? cvFile.name : 'Choose file...'}
                                </div>
                            </div>
                        ) : (
                        <input
                            type={field.type || 'text'}
                            id={field.key}
                            name={field.key}
                            value={formData[field.key] || ''}
                            onChange={handleChange}
                            readOnly={!field.editable}
                            required
                            className={style[!field.editable ? 'disabled' : '']}
                        />
                        )}
                    </div>
                    );
                })}
                </div>
                
                <div className={style["form-actions"]}>
                <button type="button" className={style["cancel-btn"]} onClick={onClose}>
                    Cancel
                </button>
                <button type="submit" className={style["submit-btn"]}>
                    Submit Application
                </button>
                </div>
            </form>
            </div>
        </div>
    );
};

export default function Internship() {
    const [internships, setInternships] = useState([]);
    const [selectedInternship, setSelectedInternship] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [debouncedSearchTerm, setDebouncedSearchTerm] = useState('');
    const [page, setPage] = useState(1);
    const [hasMore, setHasMore] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const [showApplication, setShowApplication] = useState(false);

    const studentId = JSON.parse(localStorage.getItem("userData"))?.userId;
    const token = localStorage.getItem('authToken');

    const fetchInternships = useCallback(async (pageNum, search = '') => {
        setIsLoading(true);
        setError(null);
    
        try {
            const config = {
                params: {
                    page: pageNum - 1,
                    size: 10,
                    search: search
                }
            };
    
            if (token) {
                config.headers = {
                    Authorization: `Bearer ${token}`
                };
            }
    
            const response = await axios.get('http://localhost:8765/internship-service/internships', config);
            const newInternships = response.data?.content || [];
    
            if (pageNum === 1) {
                setInternships(newInternships);
            } else {
                setInternships(prev => [...prev, ...newInternships]);
            }
    
            setHasMore(newInternships.length > 0);
    
            if (newInternships.length > 0 && !selectedInternship) {
                setSelectedInternship(newInternships[0]);
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to fetch internships');
        } finally {
            setIsLoading(false);
        }
    }, [token, selectedInternship]);
    

    // Debounce search input
    useEffect(() => {
        const handler = setTimeout(() => {
            setDebouncedSearchTerm(searchTerm);
        }, 500);
        return () => clearTimeout(handler);
    }, [searchTerm]);

    // Fetch internships on debounced search term
    useEffect(() => {
        setPage(1);
        fetchInternships(1, debouncedSearchTerm);
    }, [debouncedSearchTerm, fetchInternships]);

    // Infinite scroll handler
    const handleScroll = useCallback(() => {
        if (isLoading || !hasMore) return;
        const { scrollTop, scrollHeight, clientHeight } = document.documentElement;
        if (scrollTop + clientHeight >= scrollHeight - 100) {
            setPage(prev => prev + 1);
        }
    }, [isLoading, hasMore]);

    useEffect(() => {
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, [handleScroll]);

    // Fetch more internships on page increment (but not for page 1)
    useEffect(() => {
        if (page > 1) {
            fetchInternships(page, debouncedSearchTerm);
        }
    }, [page, debouncedSearchTerm, fetchInternships]);

    
    return (
        <>
            <Navbar />
            <div className={style["page-container"]}>
                <div className={style["internship-container"]}>
                    <div className={style["left-sidebar"]}>
                        <div className={style["search-container"]}>
                            <input
                            type="text"
                            placeholder="Search internships..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className={style["search-input"]}
                            />
                        </div>
                        
                        <div className={style["internship-list"]}>
                            {internships.map(internship => {
                            return (
                                <div 
                                    key={`${internship.id}-${internship.created_at}`}
                                    className={`${style["internship-item"]} ${style[selectedInternship?.id === internship.id ? 'active' : '']}`}
                                    onClick={() => setSelectedInternship(internship)}
                                    >
                                    <h3 className={style["item-title"]}>{internship.title}</h3>
                                    <p className={style["item-company"]}>Company: {internship.hrCompanyName}</p>
                                    <p className="item-hr">HR: {internship.hrName}</p>
                                </div>
                            );
                            })}
                            {isLoading && <div className={style["loading-message"]}>Loading more internships...</div>}
                            {error && <div className={style["error-message"]}>{error}</div>}
                            {!hasMore && !isLoading && internships.length > 0 && (
                            <div className={style["end-message"]}>No more internships to load</div>
                            )}
                        </div>
                    </div>

                    <div className={style["right-sidebar"]}>
                    {selectedInternship ? (
                            <div className={style["internship-details"]}>
                                <div className={style["detail-header"]}>
                                    <h1 className={style["detail-title"]}>{selectedInternship.title}</h1>
                                    <span className={`${style["status-badge"]} ${style[selectedInternship.status.toLowerCase()]}`}>
                                    {selectedInternship.status}
                                    </span>
                                </div>
                            
                                <div className={style["detail-meta"]}>
                                    <div className={style["meta-row"]}>
                                        <div className={style["meta-item"]}>
                                            <span className={style["meta-label"]}>Company:</span>
                                            <span className={style["meta-value"]}>{selectedInternship.hrCompanyName}</span>
                                        </div>
                                        <div className={style["meta-item"]}>
                                            <span className={style["meta-label"]}>Type:</span>
                                            <span className={style["meta-value"]}>{selectedInternship.type}</span>
                                        </div>
                                    </div>
                                    
                                    <div className={style["meta-row"]}>
                                        <div className={style["meta-item"]}>
                                            <span className={style["meta-label"]}>HR Contact:</span>
                                            <span className={style["meta-value"]}>{selectedInternship.hrName}</span>
                                        </div>
                                        <div className={style["meta-item"]}>
                                            <span className={style["meta-label"]}>Stipend:</span>
                                            <span className={style["meta-value"]}>{selectedInternship.stipend}</span>
                                        </div>
                                    </div>
                                    
                                    <div className={style["meta-row"]}>
                                        <div className={style["meta-item"]}>
                                            <span className={style["meta-label"]}>Location:</span>
                                            <span className={style["meta-value"]}>{selectedInternship.location}</span>
                                        </div>
                                        <div className={style["meta-item"]}>
                                            <span className={style["meta-label"]}>Duration:</span>
                                            <span className={style["meta-value"]}>{selectedInternship.duration}</span>
                                        </div>
                                    </div>
                                    
                                    <div className={style["meta-row"]}>
                                        <div className={style["meta-item"]}>
                                            <span className={style["meta-label"]}>Posted:</span>
                                            <span className={style["meta-value"]}>
                                                {new Date(selectedInternship.createdAt).toLocaleDateString(undefined, {
                                                    year: 'numeric',
                                                    month: 'long',
                                                    day: 'numeric'
                                                })}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                
                                <div className={style["detail-section"]}>
                                    <h2 className={style["section-title"]}>Description</h2>
                                    <p className={style["section-content"]}>{selectedInternship.description}</p>
                                </div>
                                
                                <div className={style["detail-section"]}>
                                    <h2 className={style["section-title"]}>Requirements</h2>
                                    <p className={style["section-content"]}>{selectedInternship.requirements || "Not specified"}</p>
                                </div>
                                
                                <button className={style["apply-button"]} onClick={() => setShowApplication(true)}>Apply Now</button>
                            </div>
                        ) : (
                            <div className={style["no-selection"]}>
                                <p>Select an internship to view details</p>
                            </div>
                        )}
                    </div>
                </div>
                {showApplication && (
                    <ApplicationForm
                        onClose={() => setShowApplication(false)}
                        studentId={studentId}
                        internshipId={selectedInternship.id}
                    />
                )}
            </div>
            
        </>
    );
};