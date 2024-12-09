import React, {useEffect} from "react";
import Navbar from "./Navbar";
import MainContent from "./MainContent";
import Sidebar from "./Sidebar";
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import {fetchWrap} from "../fetchWrap";
import {get} from "axios";
const Home = () => {
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

export default Home;