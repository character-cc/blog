import React, { useState, useEffect } from "react";
import "./categoryModal.css";
import {fetchWrap} from "../fetchWrap";
import {useNavigate} from "react-router-dom";

const CategoryModal = () => {
    const [selectedCategories, setSelectedCategories] = useState([]);
    const [categories, setCategories] = useState([]);
   const toggleCategory = (category) => {
        setSelectedCategories((prev) =>
            prev.includes(category)
                ? prev.filter((c) => c !== category)
                : [...prev, category]
        );
    };
   const navigate = useNavigate();
    const params = new URLSearchParams(window.location.search);
    const frontEndUrl = params.get('frontEndUrl');
     console.log(frontEndUrl);
    useEffect(  () => {
        const fetchCategories = async () => {
            try {
                const response = await fetchWrap("http://localhost/api/categories");
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
            console.log(JSON.stringify({ categories}));
            const response = await fetch("http://localhost/api/upload-categories", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({categories}),
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
                console.log(frontEndUrl);
                setTimeout(() => {
                    window.location.replace(frontEndUrl);
                }, 3000);
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
