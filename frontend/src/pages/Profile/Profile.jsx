import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import React, { useState, useEffect } from 'react';
import style from './Profile.module.css'
import Navbar from '../../components/Navbar/Navbar';


export default function Profile() {
    const roleFieldMap = {
        ADMIN: [
            { key: 'username', label: 'Username' },
            { key: 'fName', label: 'Full Name' },
            { key: 'password', label: 'Password' },
            { key: 'email', label: 'Email' },
        ],
        STUDENT: [
            { key: 'username', label: 'Username' },
            { key: 'fName', label: 'Full Name' },
            { key: 'password', label: 'Password' },
            { key: 'email', label: 'Email' },
            { key: 'college', label: 'College' },
            { key: 'faculty', label: 'Faculty' },
            { key: 'cv', label: 'CV' },
            { key: 'grade', label: 'Grade' },
            { key: 'phone', label: 'Phone' },
            { key: 'yearOfGraduation', label: 'Year of Graduation' },
        ],
        HR: [
            { key: 'username', label: 'Username' },
            { key: 'fName', label: 'Full Name' },
            { key: 'password', label: 'Password' },
            { key: 'email', label: 'Email' },
            { key: 'company', label: 'Company' },
        ],
    };

    const [user, setUser] = useState(null);
    const [editingUserId, setEditingUserId] = useState(null);
    const [editedUser, setEditedUser] = useState({});
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                setIsLoading(true);
                const token = localStorage.getItem("accessToken");
                const decoded = jwtDecode(token);
                const role = decoded.role;
                const id = JSON.parse(localStorage.getItem("userData")).userId;
    
                let url = '';
                if (role === 'ADMIN') {
                    url = `http://localhost:8765/user-service/admins/${id}`;
                } else if (role === 'STUDENT') {
                    url = `http://localhost:8765/user-service/students/${id}`;
                } else if (role === 'HR') {
                    url = `http://localhost:8765/user-service/hr/${id}`;
                }
    
                const response = await axios.get(url, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                    withCredentials: true
                });
    
                // Transform the data structure
                const userData = response.data;
                
                const transformedData = {
                    ...userData.user,  // Spread user object properties
                    ...userData        // Spread root properties (overwrites duplicates)
                };
                if(role === 'HR'){
                    transformedData.company = transformedData.company.name;
                }
                // Remove the nested user object if it exists
                if (transformedData.user) {
                    delete transformedData.user;
                }
                if (transformedData.fname) {
                    transformedData.fName = transformedData.fname;
                    delete transformedData.fname;
                }
    
                setUser(transformedData);
                
            } catch (err) {
                setError(err.response?.data?.message || err.message);
                console.error("Error fetching user:", err);
            } finally {
                setIsLoading(false);
            }
        };
    
        fetchUser();
    }, []);

    const handleEditClick = (user) => {
        setEditingUserId(user.id);
        setEditedUser({ ...user, password: null });
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setEditedUser(prev => ({
        ...prev,
        [name]: value
        }));
    };

    const handleSave = async (userId) => {
        try {
            const token = localStorage.getItem("accessToken");
            const decoded = jwtDecode(token);
            const role = decoded.role;
            const id = userId;
    
            // Make the PUT request to update the user
            let url = '';
            if (role === 'ADMIN') {
                url = `http://localhost:8765/user-service/admins/${id}`;
                if (editedUser.fName) {
                    editedUser.fname = editedUser.fName;
                    delete editedUser.fName;
                }
                if (editedUser.name) {
                    delete editedUser.name;
                }
            } else if (role === 'STUDENT'){
                url = `http://localhost:8765/user-service/students/${id}`;
                
            } else if (role === 'HR'){
                url = `http://localhost:8765/user-service/hr/${id}`;
                if (editedUser.fName) {
                    editedUser.fname = editedUser.fName;
                    delete editedUser.fName;
                }
            }
            const response = await axios.put(url, editedUser, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                }
            });
                
            if (response.status === 200) {
                // Successfully updated user, update the state
                user.fName = editedUser.fname;
                setUser({ ...user, ...editedUser });
                setEditingUserId(null);
            } else {
                throw new Error('Failed to update user');
            }
        } catch (err) {
            setError(err.message);
        }
    };

    const handleCancel = () => {
        setEditingUserId(null);
    };

    if (isLoading) return <div className={`${style["text-center"]} ${style["py-8"]}`}>Loading...</div>;
    if (error) return <div className={`${style["text-center"]} ${style["py-8"]} ${style["text-red-600"]}`}>Error: {error}</div>;

    return (
        <>
            <Navbar />
            {user && (
                <div key={user.id} className={style["user-profile-card"]}>
                    <div className={style["user-card-header"]}>
                        <h2 className={style["user-name"]}>
                            {user.fName}
                        </h2>
                        {editingUserId !== user.id ? (
                            <button
                                onClick={() => handleEditClick(user)}
                                className={`${style["edit-btn"]} ${style["transition-effect"]}`}
                            >
                                Edit
                            </button>
                        ) : (
                            <div className={style["button-group"]}>
                                <button
                                    onClick={() => handleSave(user.id)}
                                    className={`${style["save-btn"]} ${style["transition-effect"]}`}
                                >
                                    Save
                                </button>
                                <button
                                    onClick={handleCancel}
                                    className={`${style["cancel-btn"]} ${style["transition-effect"]}`}
                                >
                                    Cancel
                                </button>
                            </div>
                        )}
                    </div>
    
                    <div className={style["form-grid"]}>
                        {editingUserId === user.id ? (
                            roleFieldMap[jwtDecode(localStorage.getItem("accessToken")).role]?.map(field => (
                                <div key={field.key} className={style["form-field"]}>
                                    <label className={style["field-label"]}>
                                        {field.label}
                                    </label>
                                    {field.key === 'username' ? (
                                        <p className={style["read-only-value"]}>{user[field.key]}</p>
                                    ) : field.key === 'password' ? (
                                        <input
                                            type="password"
                                            name={field.key}
                                            value={editedUser[field.key] || ''} 
                                            onChange={handleInputChange}
                                            className={`${style["field-input"]} ${style["password-input"]}`}
                                        />
                                    ) : (
                                        <input
                                            type={field.key === 'password' ? 'password' : 'text'}
                                            name={field.key}
                                            value={editedUser[field.key] || ''}
                                            onChange={handleInputChange}
                                            className={style["field-input"]}
                                        />
                                    )}
                                </div>
                            ))
                        ) : (
                            roleFieldMap[jwtDecode(localStorage.getItem("accessToken")).role]?.map(field => (
                                field.key !== 'password' && (
                                    <div key={field.key} className={style["form-field"]}>
                                        <p className={style["field-label"]}>{field.label}</p>
                                        <p className={style["read-only-value"]}>{user[field.key]}</p>
                                    </div>
                                )
                            ))
                        )}
                    </div>
                </div>
            )}
        </>
    );    
};
