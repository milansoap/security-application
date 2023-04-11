import React, { useEffect } from "react";

const OAuthSuccess = () => {
  const storeJwtToken = () => {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get("token");

    if (token) {
      localStorage.setItem("authToken", token);
      // Redirect to dashboard
      window.location.href = "/dashboard";
    } else {
      // Handle error case
    }
  };

  useEffect(() => {
    storeJwtToken();
  }, []);

  return <div>Loading...</div>;
};

export default OAuthSuccess;
