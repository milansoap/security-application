import React from "react";
import { Route, Navigate } from "react-router-dom";

function ProtectedRoute({ element, ...rest }) {
  const authToken = localStorage.getItem("authToken");

  if (!authToken) {
    return <Navigate to="/" />;
  }

  return <Route element={element} {...rest} />;
}

export default ProtectedRoute;
