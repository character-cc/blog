import React, {useEffect, useState} from "react";
import { Editor } from "@tinymce/tinymce-react";
import Select from "react-select";
import axios from "axios";
import Navbar from "./Navbar";
import "./uploadPost.css"
import {data, useNavigate, useParams} from "react-router-dom";
import {fetchWrap} from "../fetchWrap";

const UploadPost = () => {
    const { postId } = useParams();
    const [title, setTitle] = useState("");
    const [categories, setCategories] = useState([]);
    const [content, setContent] = useState("");
    const [categoryOptions, setCategoryOptions] = useState([]);

    const navigate = useNavigate();
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
                const response = await fetchWrap(`http://localhost/api/post_detail/${postId}`);
                if (response.ok) {
                    const data = await response.json();
                    setTitle(data.title);
                    setContent(data.content);
                    setCategories(data.categories.map(cat => ({
                        value: cat.id,
                        label: cat.name
                    })));
                    console.log(data);
                    console.log(categories);
                }
            } catch (error) {
                console.log( error);
            }
        };
        fetchPostDetails();
    }, [postId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (categories.length === 0) {
            alert("Vui lòng chọn ít nhất một thể loại.");
            return; 
        }
        const postData = {
            title,
            categories: categories.map(cat => cat.label),
            content,
        };
        try {
            let response;
            if (postId) {
                response = await axios.put(`http://localhost/api/posts/edit/${postId}`, postData);
                alert(postId ? "Bài viết đã được cập nhật thành công!" : "Bài viết đã được tạo thành công!");
                navigate("/post/" + postId);
            } else {
                response = await axios.post("http://localhost/api/posts/upload", postData);
                const id = response.data.postId;
                alert(postId ? "Bài viết đã được cập nhật thành công!" : "Bài viết đã được tạo thành công!");
                navigate("/post/" + id);
            }



        } catch (error) {
            console.log( error);
            alert("Có lỗi xảy ra.");
        }
    };

    const imageUploadHandler = (blobInfo, progress) => {
        return new Promise((resolve, reject) => {
            const xhr = new XMLHttpRequest();
            xhr.withCredentials = false;
            xhr.open('POST', 'http://localhost/api/posts/images/upload');

            xhr.upload.onprogress = (e) => {
                if (e.lengthComputable) {
                    progress(e.loaded / e.total * 100);
                }
            };
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
                <h1 className="mb-4">{postId ? "Sủa bài viết " : "Tạo bài viết"}</h1>
                <form onSubmit={handleSubmit}>
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

                    <div className="mb-3">
                        <label htmlFor="content" className="form-label">Nội dung</label>
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

                    <div className="text-end">
                        <button type="submit" className="btn btn-success">{postId ? "Sửa" : "Xuất bản"}</button>
                    </div>
                </form>
            </div>
        </>
    );
};

export default UploadPost;
