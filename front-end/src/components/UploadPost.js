import React, { useState } from "react";
import { Editor } from "@tinymce/tinymce-react";
import Select from "react-select";
import axios from "axios";
import Navbar from "./Navbar";
import "./uploadPost.css"

const UploadPost = () => {
    const [title, setTitle] = useState("");
    const [categories, setCategories] = useState([]);
    const [content, setContent] = useState("");

    const categoryOptions = [
        { value: "category1", label: "Thể loại 1" },
        { value: "category2", label: "Thể loại 2" },
        { value: "category3", label: "Thể loại 3" },
        { value: "category4", label: "Thể loại 4" },
    ];

    const handleSubmit = async (e) => {
        e.preventDefault();

        const postData = {
            title,
            categories: categories.map((cat) => cat.value),
            content,
        };

        try {
            const response = await axios.post("http://localhost/api/posts/upload", postData);
            console.log("Post created:", response.data);
            alert("Bài viết đã được tạo thành công!");
        } catch (error) {
            console.error("Error creating post:", error);
            alert("Có lỗi xảy ra khi tạo bài viết.");
        }
    };

    const imageUploadHandler = (blobInfo, progress) => {
        return new Promise((resolve, reject) => {
            const xhr = new XMLHttpRequest();
            xhr.withCredentials = false;
            xhr.open('POST', 'http://localhost/api/posts/images/upload'); // Đảm bảo URL đúng

            // Xử lý tiến trình upload
            xhr.upload.onprogress = (e) => {
                if (e.lengthComputable) {
                    progress(e.loaded / e.total * 100); // Cập nhật tiến trình
                }
            };

            // Khi upload xong
            xhr.onload = () => {
                if (xhr.status === 403) {
                    reject({ message: 'HTTP Error: ' + xhr.status, remove: true });
                    return;
                }

                if (xhr.status < 200 || xhr.status >= 300) {
                    reject('HTTP Error: ' + xhr.status);
                    return;
                }

                const json = JSON.parse(xhr.responseText);

                if (!json || typeof json.location !== 'string') {
                    reject('Invalid JSON: ' + xhr.responseText);
                    return;
                }

                // Trả về URL ảnh
                resolve(json.location);
            };

            xhr.onerror = () => {
                reject('Image upload failed due to an XHR Transport error. Code: ' + xhr.status);
            };

            const formData = new FormData();
            formData.append('file', blobInfo.blob(), blobInfo.filename());
            xhr.send(formData);
        });
    };
    return (
        <>
            <Navbar />
            <div className="container mt-4">
                <h1 className="mb-4">Tạo bài viết mới</h1>
                <form onSubmit={handleSubmit}>
                    {/* Tiêu đề */}
                    <div className="mb-3">
                        <label htmlFor="title" className="form-label">Tiêu đề</label>
                        <input
                            type="text"
                            className="form-control"
                            id="title"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            placeholder="Nhập tiêu đề..."
                        />
                    </div>

                    {/* Thể loại */}
                    <div className="mb-3">
                        <label htmlFor="categories" className="form-label">Thể loại</label>
                        <Select className = "react-select__control"
                            isMulti
                            options={categoryOptions}
                            value={categories}
                            onChange={setCategories}
                            placeholder="Chọn thể loại..."
                        />
                    </div>

                    {/* Nội dung */}
                    <div className="mb-3">
                        <label htmlFor="content" className="form-label">Nội dung</label>
                        <Editor
                            apiKey="e165yfho0uvtfkbdv6swajcvqjdweaixeq9swobtwphycw1f"
                            init={{
                                plugins: "image link media code table ",
                                toolbar: "undo redo | bold italic | link image | alignleft aligncenter alignright | code",
                                images_upload_url: "http://localhost/api/posts/images/upload",
                                images_upload_handler: async (blobInfo, success, failure) => {
                                    const formData = new FormData();
                                    formData.append("file", blobInfo.blob(), blobInfo.filename());
                                    try {
                                        const response = await fetch("http://localhost/api/posts/images/upload", {
                                            method: "POST",
                                            body: formData,
                                        });

                                        if (!response.ok) {
                                            throw new Error("Failed to upload image");
                                        }

                                        const result = await response.json();
                                        console.log(result);
                                        if (result && result.location) {
                                             return result.location
                                        } else {
                                            console.error("Missing location in response:", result);
                                            failure("Invalid response: location is missing");
                                        }
                                    } catch (error) {
                                        console.error("Upload failed:", error);
                                        failure("Error: " + error.message);
                                    }
                                }
                            }}
                            value={content}
                            onEditorChange={(newContent) => setContent(newContent)}
                            className="tinymce-container"
                        />
                    </div>

                    <div className="text-end">
                        <button type="submit" className="btn btn-success">Xuất bản</button>
                    </div>
                </form>
            </div>
        </>
    );
};

export default UploadPost;
