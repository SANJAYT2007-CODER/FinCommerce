/* FinCommerce Centralized API Client */

const API_BASE_URL = 'http://localhost:8085';

class ApiClient {

  static getToken() {
    return localStorage.getItem('token');
  }

  static getHeaders(hasBody = true) {
    const headers = {};
    if (hasBody) {
      headers['Content-Type'] = 'application/json';
    }
    const token = this.getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
  }

  static async request(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const config = {
      ...options,
      headers: {
        ...this.getHeaders(options.body !== undefined),
        ...options.headers
      }
    };

    if (options.body && typeof options.body === 'object') {
      config.body = JSON.stringify(options.body);
    }

    try {
      const response = await fetch(url, config);
      const responseText = await response.text();

      let data;
      try {
        data = responseText ? JSON.parse(responseText) : {};
      } catch (parseErr) {
        throw new Error(`Backend server error (${response.status}). Please ensure Spring Boot backend is running on http://localhost:8085.`);
      }

      if (!response.ok) {
        if ((response.status === 401 || response.status === 403) && !endpoint.includes('/api/auth/')) {
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          window.location.href = 'login.html';
        }
        throw new Error(data.message || `API request failed with status ${response.status}`);
      }

      return data;
    } catch (error) {
      if (error.message.includes('Failed to fetch')) {
        throw new Error('Cannot connect to backend server at http://localhost:8085. Please start the Spring Boot application using .\\mvnw.cmd spring-boot:run');
      }
      console.error(`[API Error] ${endpoint}:`, error);
      throw error;
    }
  }

  static get(endpoint) {
    return this.request(endpoint, { method: 'GET' });
  }

  static post(endpoint, body) {
    return this.request(endpoint, { method: 'POST', body });
  }

  static put(endpoint, body) {
    return this.request(endpoint, { method: 'PUT', body });
  }

  static delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  }
}
