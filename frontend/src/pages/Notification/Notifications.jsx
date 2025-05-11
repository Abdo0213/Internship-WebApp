import React, { useState, useEffect } from 'react';
import axios from 'axios';
import style from './Notifications.module.css';
import Navbar from '../../components/Navbar/Navbar';

const Notification = () => {
    const [notifications, setNotifications] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
    const fetchNotifications = async () => {
        try {
            setLoading(true);
            const token = localStorage.getItem('accessToken');
            
            if (!token) {
                throw new Error('No authentication token found');
            }

            const response = await axios.get('http://localhost:8765/internship-service/notifications', {
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                params: {
                    page: 0,       // Explicitly set page number
                    size: 10       // Explicitly set page size
                }
            });
            // Check response structure
            if (response.data && response.data.content) {
                setNotifications(response.data.content);
            } else if (Array.isArray(response.data)) {
                // Handle case if backend returns array directly
                setNotifications(response.data);
            } else {
                throw new Error('Unexpected response format');
            }
            
        } catch (error) {
            console.error('Error fetching notifications:', error);
            // Optional: set error state
        } finally {
            setLoading(false);
        }
    };

    fetchNotifications();
}, []);

    const markAsRead = async (id) => {
        try {
            const token = localStorage.getItem('accessToken');
            await axios.patch(`http://localhost:8765/internship-service/notifications/${id}/read`, {}, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setNotifications(notifications.map(notification => 
                notification.id === id ? { ...notification, read: true } : notification
            ));
        } catch (error) {
            console.error('Error marking notification as read:', error);
        }
    };

    const filteredNotifications = notifications.filter(notification =>
        notification.internshipTitle.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const getStatusBadgeClass = (status) => {
        switch(status) {
            case 'ACCEPTED': return 'accepted';
            case 'REJECTED': return 'rejected';
            case 'PENDING': return 'pending';
            default: return '';
        }
    };

    if (loading) {
        return (
            <>
                <Navbar />
                <div className={style["loading-spinner"]}>
                    <div className={style["spinner"]}></div>
                    <p>Loading notifications...</p>
                </div>
            </>
        );
    }

    return (
        <>
            <Navbar />
            <div className={style["notification-container"]}>
                <div className={style["notification-controls"]}>
                    <div className={style["search-container"]}>
                        <input
                            type="text"
                            placeholder="Search by internship title..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className={style["search-input"]}
                        />
                    </div>
                </div>
                
                <div className={style["notification-list"]}>
                    {filteredNotifications.length === 0 ? (
                        <div className={style["empty-state"]}>
                            <p>No notifications found</p>
                        </div>
                    ) : (
                        filteredNotifications.map(notification => (
                            <div 
                                key={notification.id}
                                className={`${style["notification-item"]} ${notification.read ? style['read'] : style['unread']}`}
                                onClick={() => !notification.read && markAsRead(notification.id)}
                            >
                                <div className={style["notification-main"]}>
                                    <div className={style["notification-header"]}>
                                        <h3 className={style["internship-title"]}>{notification.internshipTitle}</h3>
                                        <span className={`${style['status-badge']} ${style[getStatusBadgeClass(notification.application.status)]}`}>
                                            {notification.application.status}
                                        </span>
                                    </div>
                                    <p className={style["notification-meta"]}>
                                        <span className={style["company-name"]}>{notification.application.internship.companyName}</span>
                                        <span className={style["application-date"]}>
                                            Applied on: {new Date(notification.application.appliedAt).toLocaleDateString()}
                                        </span>
                                    </p>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </>
    );
};

export default Notification;