/* FinCommerce Auth & User Session Handler */

class Auth {
  static getCurrentUser() {
    const userJson = localStorage.getItem('user');
    return userJson ? JSON.parse(userJson) : null;
  }

  static isAuthenticated() {
    return !!localStorage.getItem('token');
  }

  static requireAuth() {
    if (!this.isAuthenticated()) {
      window.location.href = 'login.html';
    }
  }

  static requireAdmin() {
    this.requireAuth();
    const user = this.getCurrentUser();
    if (!user || user.role !== 'ROLE_ADMIN') {
      alert('Access Denied: Admin Privileges Required');
      window.location.href = 'dashboard.html';
    }
  }

  static login(email, password) {
    return ApiClient.post('/api/auth/login', { email, password })
      .then(res => {
        if (res.success && res.data) {
          localStorage.setItem('token', res.data.accessToken);
          localStorage.setItem('user', JSON.stringify(res.data));
          return res.data;
        }
        throw new Error(res.message || 'Login failed');
      });
  }

  static register(fullName, email, mobileNumber, password, confirmPassword) {
    return ApiClient.post('/api/auth/register', {
      fullName,
      email,
      mobileNumber,
      password,
      confirmPassword
    }).then(res => {
      if (res.success && res.data) {
        localStorage.setItem('token', res.data.accessToken);
        localStorage.setItem('user', JSON.stringify(res.data));
        return res.data;
      }
      throw new Error(res.message || 'Registration failed');
    });
  }

  static sendOtp(identifier) {
    return ApiClient.post('/api/auth/send-otp', { identifier })
      .then(res => {
        if (res.success && res.data) {
          return res.data;
        }
        throw new Error(res.message || 'Failed to send OTP');
      });
  }

  static verifyOtp(identifier, otp, deviceInfo = 'Chrome / Windows') {
    return ApiClient.post('/api/auth/verify-otp', { identifier, otp, deviceInfo })
      .then(res => {
        if (res.success && res.data) {
          localStorage.setItem('token', res.data.accessToken);
          localStorage.setItem('user', JSON.stringify(res.data));
          return res.data;
        }
        throw new Error(res.message || 'OTP Verification failed');
      });
  }

  static logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'login.html';
  }
}

