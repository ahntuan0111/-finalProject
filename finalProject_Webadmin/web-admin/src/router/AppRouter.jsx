import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from '../components/layout/MainLayout';
import LoginPage from '../pages/Auth/LoginPage';
import DashboardPage from '../pages/Dashboard/DashboardPage';
import UserList from '../pages/User/UserList';
import CoursePage from '../pages/Course/CoursePage';
import PrivateRoute from './PrivateRoute';

import ExercisePage from '../pages/Exercise/ExercisePage'; 
import DocumentManagement from '../pages/Document/DocumentManagement';

// SỬA 1: Đổi './pages/...' thành '../pages/...' cho đồng nhất với cấu trúc
import QuizManagement from '../pages/QuizManagement/QuizManagement'; 

const AppRouter = () => {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      <Route path="/" element={<PrivateRoute><MainLayout /></PrivateRoute>}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="users" element={<UserList />} />
        <Route path="courses" element={<CoursePage />} />
        <Route path="exercises" element={<ExercisePage />} />
        <Route path="documents" element={<DocumentManagement />} />
        
        {/* SỬA 2: Đổi path "/admin/quizzes" thành "quizzes" để khớp với Sidebar */}
        <Route path="quizzes" element={<QuizManagement />} />
      </Route>

      {/* Đây chính là dòng in ra chữ 404 Not Found mà bạn vừa thấy */}
      <Route path="*" element={<div>404 Not Found</div>} />
    </Routes>
  );
};

export default AppRouter;