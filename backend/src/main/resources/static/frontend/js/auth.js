// Authentication helper module
const Auth = {
    getToken() {
        return localStorage.getItem('token');
    },

    getUser() {
        const raw = localStorage.getItem('user');
        return raw ? JSON.parse(raw) : null;
    },

    isAuthenticated() {
        return !!this.getToken();
    },

    getRole() {
        const user = this.getUser();
        return user ? user.role : null;
    },

    hasRole(role) {
        return this.getRole() === role;
    },

    setSession(loginData) {
        localStorage.setItem('token', loginData.token);
        localStorage.setItem('user', JSON.stringify({
            userId: loginData.userId,
            name: loginData.name,
            email: loginData.email,
            role: loginData.role,
            vendorId: loginData.vendorId,
            vendorCode: loginData.vendorCode
        }));
    },

    logout() {
        localStorage.clear();
        window.location.href = '/login.html';
    },

    requireRole(expectedRole) {
        if (!this.isAuthenticated()) {
            window.location.href = '/login.html';
            return false;
        }

        const currentRole = this.getRole();
        if (expectedRole && currentRole !== expectedRole) {
            // Redirect to their own dashboard
            if (currentRole === 'ADMIN') window.location.href = '/admin/dashboard.html';
            else if (currentRole === 'VENDOR') window.location.href = '/vendor/dashboard.html';
            else window.location.href = '/user/dashboard.html';
            return false;
        }

        // Populate user details in sidebar
        const user = this.getUser();
        const nameEl = document.querySelector('.user-profile .user-name');
        const roleEl = document.querySelector('.user-profile .user-role');
        if (nameEl && user) nameEl.textContent = user.name || user.email;
        if (roleEl && user) roleEl.textContent = user.role;

        return true;
    }
};
