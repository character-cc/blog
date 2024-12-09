import React, { useState, useEffect } from "react";
import "./categoryModal.css";
import {fetchWrap} from "../fetchWrap"; // Import file CSS tùy chỉnh

const CategoryModal = ({ onClose }) => {
    const [selectedCategories, setSelectedCategories] = useState([]);
    const [categories, setCategories] = useState([]);
   const toggleCategory = (category) => {
        setSelectedCategories((prev) =>
            prev.includes(category)
                ? prev.filter((c) => c !== category)
                : [...prev, category]
        );
    };
    useEffect(  () => {
        const fetchCategories = async () => {
            try {
                const response = await fetchWrap("./api/categories");

                 const data = await response.json();
                console.log("Categories:", data);
                if(data.length > 0){
                    setCategories(data);
                }
            }
            catch (error) {
                console.error(error);
            }
        }
        fetchCategories();


        }
    ,[]);

    const uploadCategories = async (categories) => {
        try {
            const response = await fetchWrap("/api/upload-categories", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ categories }),
            });
            if (response.ok) {
                console.log("Upload thành công!");
            } else {
                console.error("Server báo lỗi nhưng vẫn trả về thành công:");
            }
        } catch (error) {
            console.error("Lỗi khi upload categories:", error);
        }
    };


    const handleSubmit = async () => {
        if (selectedCategories.length < 3) {
            alert("Bạn chưa chọn đủ số lượng");
        } else {
            try {
                await uploadCategories(selectedCategories);
                onClose();
            } catch (error) {
                console.error("Lỗi khi submit categories:", error);
            }
        }
    };
    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <h2>Chọn ít nhất 3 danh mục</h2>
                <div className="button-grid">
                    {categories.map((category) => (
                        <button
                            key={category.name}
                            className={`category-button ${
                                selectedCategories.includes(category.name) ? "selected" : ""
                            }`}
                            onClick={() => toggleCategory(category.name)}
                        >
                            {category.name}
                        </button>
                    ))}
                </div>
                <button className="confirm-button" onClick={handleSubmit}>
                    Xác nhận
                </button>
            </div>
        </div>
    );
};
export default CategoryModal;
