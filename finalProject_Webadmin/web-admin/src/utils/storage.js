export const storage = {
  setToken: (token) => localStorage.setItem('access_token', token),
  getToken: () => localStorage.getItem('access_token'),
  removeToken: () => localStorage.removeItem('access_token'),
  
  setUserInfo: (user) => localStorage.setItem('user_info', JSON.stringify(user)),
  getUserInfo: () => JSON.parse(localStorage.getItem('user_info') || 'null'),
  removeUserInfo: () => localStorage.removeItem('user_info'),
  
  clear: () => localStorage.clear(),
};