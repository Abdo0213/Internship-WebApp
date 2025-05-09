import React, { useState } from 'react';
import './SearchableList.css';

const SearchableList = ({ items, onItemSelect, selectedItem, displayProperty }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const getDisplayText = (item) => {
    // Try different property paths in order of priority
    return item.user?.fname || 
      item.title || 
      item.name || 
      item.username || 
      item.email || 
      '';
  };

  const filteredItems = items.filter(item => {
    const text = getDisplayText(item).toString().toLowerCase();
    return text.includes(searchTerm.toLowerCase());
  });

  const getItemDisplay = (item) => {
    if (displayProperty) {
      // Handle nested properties like 'user.fname'
      return displayProperty.split('.').reduce((obj, key) => obj?.[key], item) || '';
    }
    return getDisplayText(item);
  };

  return (
    <div className="searchable-list">
      <input
        type="text"
        placeholder="Search..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        className="search-input"
      />
      <ul className="items-list">
        {filteredItems.map(item => (
          <li
            key={item.id}
            onClick={() => onItemSelect(item)}
            className={selectedItem?.id === item.id ? 'selected' : ''}
          >
            {getItemDisplay(item)}
          </li>
        ))}
      </ul>
      {filteredItems.length === 0 && (
        <div className="no-results">No matching items found</div>
      )}
    </div>
  );
};

export default SearchableList;