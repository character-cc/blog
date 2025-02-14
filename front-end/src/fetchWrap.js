import {useNavigate} from "react-router-dom";


export const fetchWrap = async (url, options = {}) => {
    const headers = {
        "Content-Type": "application/json",
        ...options.headers,
    };


    const identification = window.localStorage.getItem("identification");
    if (identification) {
        headers.identification = identification;
    }

        const response = await fetch(url, { ...options, headers });
        console.log(response.status);
        if (response.status === 401) {
            setTimeout(() => {
                window.location.replace("http://localhost/login");
            }, 0);
            return null;
        } else if (response.status === 307) {
            setTimeout(() => {
                window.location.replace("http://localhost/categories");
            }, 0);
            // navigate("/categories");
            return null;
        } else if (response.ok) {
            const newIdentification = response.headers.get("identification");
            if (newIdentification) {
                window.localStorage.setItem("identification", newIdentification);
            }

        }
        return response;

};
