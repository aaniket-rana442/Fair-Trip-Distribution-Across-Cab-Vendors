// Central API Service with JWT authentication and error handling
const API_BASE = '/api';

function showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerText = message;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

async function request(endpoint, options = {}) {
    const token = localStorage.getItem('token');
    
    const headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        ...(options.headers || {})
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers
    };

    try {
        const response = await fetch(`${API_BASE}${endpoint}`, config);

        // Handle CSV or non-JSON responses
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('text/csv')) {
            return await response.blob();
        }

        const data = await response.json().catch(() => null);

        if (!response.ok) {
            const errorMsg = data && (data.message || data.error) 
                ? (data.message || data.error) 
                : `HTTP Error ${response.status}: ${response.statusText}`;

            if (response.status === 401) {
                showToast('Session expired or unauthorized. Please log in.', 'error');
                localStorage.clear();
                setTimeout(() => {
                    window.location.href = '/login.html';
                }, 1200);
                throw new Error('Unauthorized');
            }

            if (response.status === 403) {
                showToast('Access denied: You do not have permission for this action.', 'error');
                throw new Error('Forbidden');
            }

            showToast(errorMsg, 'error');
            throw new Error(errorMsg);
        }

        return data;
    } catch (err) {
        if (err.message !== 'Unauthorized' && err.message !== 'Forbidden') {
            console.error(`API Error on ${endpoint}:`, err);
        }
        throw err;
    }
}

const api = {
    get: (url) => request(url, { method: 'GET' }),
    post: (url, body) => request(url, { method: 'POST', body: JSON.stringify(body) }),
    put: (url, body) => request(url, { method: 'PUT', body: JSON.stringify(body) }),
    patch: (url, body) => request(url, { method: 'PATCH', body: body ? JSON.stringify(body) : undefined }),
    delete: (url) => request(url, { method: 'DELETE' }),
    download: async (url, filename) => {
        try {
            const blob = await request(url, { method: 'GET' });
            const downloadUrl = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = downloadUrl;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            a.remove();
            showToast('Download started', 'success');
        } catch (e) {
            showToast('Failed to download report', 'error');
        }
    }
};
