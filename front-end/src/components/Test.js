import React, { useState } from "react";
import { Editor } from "@tinymce/tinymce-react";
import Select from "react-select"; // Thêm import React-Select
import "./test.css"
import Navbar from "./Navbar";
const Test = () => {
    // State lưu trữ dữ liệu
    const [title, setTitle] = useState("");
    const [categories, setCategories] = useState([]); // Đa lựa chọn
    const [content, setContent] = useState("");

    // Danh sách thể loại giả lập (dành cho React-Select)
    const categoryOptions = [
        { value: "category1", label: "Thể loại 1" },
        { value: "category2", label: "Thể loại 2" },
        { value: "category3", label: "Thể loại 3" },
        { value: "category4", label: "Thể loại 4" },
    ];

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log("Title:", title);
        console.log("Selected Categories:", categories);
        console.log("Content:", content);
    };

    return (
        <>
            <Navbar />
        <div className="container mt-4">
            <h1 className="mb-4">Tạo bài viết mới</h1>
            <form onSubmit={handleSubmit}>
                {/* Tiêu đề */}
                <div className="mb-3">
                    <label htmlFor="title" className="form-label">
                        Tiêu đề
                    </label>
                    <input
                        type="text"
                        className="form-control"
                        id="title"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        placeholder="Nhập tiêu đề..."
                    />
                </div>

                <div className="mb-3">
                    <label htmlFor="categories" className="form-label">
                        Thể loại
                    </label>
                    <Select
                        className="react-select__control"
                        isMulti
                        options={categoryOptions}
                        value={categories}
                        onChange={setCategories} // Gán trực tiếp danh sách đã chọn
                        placeholder="Chọn thể loại..."
                    />
                </div>

                {/* Nội dung */}
                <div className="mb-3">
                    <label htmlFor="content" className="form-label">
                        Nội dung
                    </label>
                    <Editor
                        apiKey="e165yfho0uvtfkbdv6swajcvqjdweaixeq9swobtwphycw1f"
                        init={{
                            plugins: ["image", "link", "media", "code", "table", "lists"],
                            toolbar:
                                "undo redo | bold italic | link image | alignleft aligncenter alignright | code",
                            images_upload_url: "/upload", // Endpoint để xử lý upload
                            images_upload_handler: function (blobInfo, success, failure) {
                                const formData = new FormData();
                                formData.append("file", blobInfo.blob(), blobInfo.filename());

                                // Gửi request lên server
                                fetch("/upload", {
                                    method: "POST",
                                    body: formData,
                                })
                                    .then((response) => response.json())
                                    .then((data) => {
                                        success(data.location); // Trả URL của ảnh từ server
                                    })
                                    .catch((error) => {
                                        failure("Upload failed: " + error.message);
                                    });
                            },
                        }}
                        onEditorChange={(newContent) => setContent(newContent)} // Lấy nội dung từ editorc
                    className="tinymce-container" />
                </div>

                {/* Nút Xuất bản */}
                <div className="text-end">
                    <button type="submit" className="btn btn-success">
                        Xuất bản
                    </button>
                </div>
            </form>
        </div>
</>
    );
};

export default Test;
