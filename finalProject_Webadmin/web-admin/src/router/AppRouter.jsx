import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from '../components/layout/MainLayout';
import LoginPage from '../pages/Auth/LoginPage';
import DashboardPage from '../pages/Dashboard/DashboardPage';
import UserList from '../pages/User/UserList';
import CoursePage from '../pages/Course/CoursePage';
import PrivateRoute from './PrivateRoute';

// --- IMPORT MỚI ---
import ExercisePage from '../pages/Exercise/ExercisePage'; 

const AppRouter = () => {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      <Route path="/" element={<PrivateRoute><MainLayout /></PrivateRoute>}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="users" element={<UserList />} />
        <Route path="courses" element={<CoursePage />} />
        
        {/* --- ROUTE MỚI --- */}
        <Route path="exercises" element={<ExercisePage />} />
      </Route>

      <Route path="*" element={<div>404 Not Found</div>} />
    </Routes>
  );
};

export default AppRouter;