import React from "react";
import { Route, Routes } from "react-router-dom";
import LoginForm from "./components/LoginForm";
import Dashboard from "./components/Dashboard";
import OAuthSuccess from "./components/OAuthSuccess";

function App() {
  return (
    <Routes>
      <Route path="/" element={<LoginForm />} />
      <Route path="/dashboard" element={<Dashboard />} />
      <Route path="/oauth_success" element={<OAuthSuccess />} />
    </Routes>
  );
}

export default App;
