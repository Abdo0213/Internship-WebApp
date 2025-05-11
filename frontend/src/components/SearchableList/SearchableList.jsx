import React, { useState } from 'react';
import style from './SearchableList.module.css';

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
    <>
      <div className={style["left-sidebar"]}>
        <div className={style["search-container"]}>
          <input
            type="text"
            placeholder="Search..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className={style["search-input"]}
          />
        </div>
        <div className={style["internship-list"]}>
          {filteredItems.map(item => {
            return (
              <div
                key={item.id}
                onClick={() => onItemSelect(item)}
                className={`${style["internship-item"]} ${style[selectedItem?.id === item.id ? 'selected' : '']}`}
              >
                <h3 className={style["item-title"]}>{getItemDisplay(item)}</h3>
              </div>
            );
          })}
          {filteredItems.length === 0 && (
            <div className="no-results">No matching items found</div>
          )}
        </div>
      </div>
    </>
  );
};

export default SearchableList;