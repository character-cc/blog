import React from "react";
import Home from "./components/Home";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min';
import UploadPost from "./components/UploadPost";
import CategoryModal from "./components/CategoryModal";
import PostDetail from "./components/PostDetail";
import Test from "./components/Test";
import Search from "./components/Search";
import YourStory from "./components/YourStory";

import LoginForm from "./components/Login";
const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="test" element={<Test />} />
                <Route path="/posts/upload" element={<UploadPost />} />
                <Route path="/posts/:id" element={<PostDetail />} />
                <Route path="/categories" element={<CategoryModal />} />
                <Route path="/search" element={<Search />} />
                <Route path="/me/story" element={<YourStory />}></Route>
                <Route path="/posts/edit/:postId" element={<UploadPost />} />
                <Route path="/login" element={<LoginForm/>} />
            </Routes>
        </Router>
    );
};

export default App;

