

import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import Select from "react-select";
import { Editor } from "@tinymce/tinymce-react";
import axios from "axios";
import {fetchWrap} from "../fetchWrap";

const UploadPost = () => {
    const { postId } = useParams();

    const [title, setTitle] = useState("");
    const [categories, setCategories] = useState([]);
    const [content, setContent] = useState("");
    const [categoryOptions, setCategoryOptions] = useState([]);

    useEffect(() => {
        const getCategories = async () => {
            try {
                const response = await fetch("http://localhost/api/categories");
                if (response.ok) {
                    const data = await response.json();
                    const transformedOptions = data.map(category => ({
                        value: category.id,
                        label: category.name
                    }));
                    setCategoryOptions(transformedOptions);
                }
            } catch (error) {
                console.error( error);
            }
        };
        getCategories();
    }, []);

    useEffect(() => {
        const fetchPostDetails = async () => {
            if (!postId) return;

            try {
                const response = await fetchWrap(`http://localhost/api/posts/${postId}`);
                if (response.ok) {
                    const data = await response.json();
                    setTitle(data.title);
                    setContent(data.content);
                    setCategories(data.categories.map(cat => ({
                        value: cat.id,
                        label: cat.name
                    })));
                }
            } catch (error) {
                console.log( error);
            }
        };
        fetchPostDetails();
    }, [postId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const postData = {
            title,
            categories: categories.map(cat => cat.value),
            content,
        };
        try {
            let response;
            if (postId) {
                response = await axios.put(`http://localhost/api/posts/${postId}`, postData);
            } else {
                response = await axios.post("http://localhost/api/posts/upload", postData);
            }
            alert(postId ? "Bài viết đã được cập nhật thành công!" : "Bài viết đã được tạo thành công!");
        } catch (error) {
            console.log( error);
            alert("Có lỗi xảy ra.");
        }
    };

    return (
        <div className="container mt-4">
            <h1>{postId ? "Chỉnh sửa bài viết" : "Tạo bài viết mới"}</h1>
            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label>Tiêu đề</label>
                    <input
                        type="text"
                        className="form-control"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                    />
                </div>
                <div className="mb-3">
                    <label>Thể loại</label>
                    <Select
                        isMulti
                        options={categoryOptions}
                        value={categories}
                        onChange={setCategories}
                        placeholder="Chọn thể loại..."
                    />
                </div>
                <div className="mb-3">
                    <label>Nội dung</label>
                    <Editor
                        apiKey="e165yfho0uvtfkbdv6swajcvqjdweaixeq9swobtwphycw1f"
                        init={{
                            plugins: [
                                // Core editing features
                                'anchor', 'autolink', 'charmap', 'codesample', 'emoticons', 'image', 'link', 'lists', 'media', 'searchreplace', 'table', 'visualblocks', 'wordcount',
                                // Your account includes a free trial of TinyMCE premium features
                                // Try the most popular premium features until Dec 20, 2024:
                                'checklist', 'mediaembed', 'casechange', 'export', 'formatpainter', 'pageembed', 'a11ychecker', 'tinymcespellchecker', 'permanentpen', 'powerpaste', 'advtable', 'advcode', 'editimage', 'advtemplate', 'mentions', 'tableofcontents', 'footnotes', 'mergetags', 'autocorrect', 'typography', 'inlinecss', 'markdown','importword', 'exportword', 'exportpdf'
                            ],
                            toolbar: 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table mergetags | addcomment showcomments | spellcheckdialog a11ycheck typography | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat',
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
                <button type="submit" className="btn btn-success">
                    {postId ? "Cập nhật" : "Xuất bản"}
                </button>
            </form>
        </div>
    );
};

export default UploadPost;
