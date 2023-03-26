import React from "react";
import { Route, Routes } from "react-router-dom";
import LoginForm from "./components/LoginForm";
import Dashboard from "./components/Dashboard";
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  return (
    <Routes>
      <Route path="/" element={<LoginForm />} />
      <ProtectedRoute path="/dashboard" element={<Dashboard />} />
    </Routes>
  );
}

export default App;
