export const fetchWrap = async (url, options = {}) => {
    const headers = {
        "Content-Type": "application/json",
        ...options.headers,
        "Frontend-URL": "http://localhost",
    };

    try {
        const response = await fetch(url, { ...options, headers });
        if (response.status === 401) {
            const data = await response.json();
            if (data.redirectUrl) {
                setTimeout(() => {
                    window.location.replace(data.redirectUrl);
                }, 3000);
            }
            return null;
        } else if (response.ok) {
            return response.json();
        } else {
            throw new Error("Request failed");
        }
    } catch (error) {
        console.error("Error in fetch:", error);
        throw error;
    }
};
