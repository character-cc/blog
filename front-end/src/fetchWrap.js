



export const fetchWrap = async (url, frontEndUrl = "http://localhost", options = {}) => {
    const headers = {
        "Content-Type": "application/json",
        ...options.headers,
        "Frontend-URL": frontEndUrl,
    };

    try {
        const response = await fetch(url, { ...options, headers });
        console.log(response.status);
        if (response.status === 401) {
            const data = await response.json();
            if (data.redirectUrl) {
                setTimeout(() => {
                    window.location.replace(data.redirectUrl);
                }, 3000);
            }
            return null;
        } else if (response.ok) {
            // console.log(data);
            return response;
        } else if (response.status === 307) {
            const data = await response.json();
                console.log(data.redirectFrontEndUrl);
                setTimeout(() => {
                    window.location.replace("http://localhost/category_modal?frontEndUrl=" + data.redirectFrontEndUrl);
                }, 3000);
            return null;
        }
        else {
            throw new Error("Request failed");

        }
    } catch (error) {
        console.error("Error in fetch:", error);
        throw error;
    }
};
