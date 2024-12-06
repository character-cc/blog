import React from "react";
import Navbar from "./components/Navbar";
import MainContent from "./components/MainContent";
import Sidebar from "./components/Sidebar";
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
const App = () => {
    return (
        <div>
            <Navbar />
            <div className="container d-flex mt-5">
                <MainContent />
                <Sidebar />
            </div>
        </div>
    );
};

export default App;

