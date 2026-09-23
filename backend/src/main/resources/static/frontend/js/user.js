// User portal JavaScript
const UserApp = {
    async loadUserDashboard() {
        if (!Auth.requireRole('NORMAL_USER')) return;

        try {
            const res = await api.get('/trips/my');
            if (res && res.success) {
                const trips = res.data;
                document.getElementById('total-my-trips').textContent = trips.length;
                document.getElementById('pending-my-trips').textContent = trips.filter(t => t.status === 'PENDING' || t.status === 'REALLOCATING').length;
                document.getElementById('accepted-my-trips').textContent = trips.filter(t => t.status === 'ACCEPTED' || t.status === 'OFFERED_TO_VENDOR').length;
                document.getElementById('completed-my-trips').textContent = trips.filter(t => t.status === 'COMPLETED').length;

                this.renderTripsTable(trips.slice(0, 5), 'recent-trips-body');
            }
        } catch (err) {
            console.error('Error loading user dashboard', err);
        }
    },

    async loadMyTrips() {
        if (!Auth.requireRole('NORMAL_USER')) return;

        try {
            const res = await api.get('/trips/my');
            if (res && res.success) {
                this.renderTripsTable(res.data, 'my-trips-body');
            }
        } catch (err) {
            console.error('Error loading trips', err);
        }
    },

    renderTripsTable(trips, tbodyId) {
        const tbody = document.getElementById(tbodyId);
        if (!tbody) return;

        if (trips.length === 0) {
            tbody.innerHTML = `<tr><td colspan="9" class="empty-state">No trips booked yet. Click "Book a Trip" to start!</td></tr>`;
            return;
        }

        tbody.innerHTML = trips.map(t => {
            const statusClass = `badge-${t.status.toLowerCase().replace(/_/g, '-')}`;
            const isCancellable = (t.status === 'PENDING' || t.status === 'OFFERED_TO_VENDOR' || t.status === 'REALLOCATING');
            const cancelBtn = isCancellable 
                ? `<button class="btn btn-danger btn-sm" onclick="UserApp.cancelTrip(${t.id})">Cancel</button>` 
                : `<span class="text-muted text-xs">—</span>`;

            return `
                <tr>
                    <td><strong>#${t.id}</strong></td>
                    <td>${t.pickupLocation}</td>
                    <td>${t.destination}</td>
                    <td>${t.passengerCount}</td>
                    <td>${t.distanceKm} km</td>
                    <td><span class="badge badge-normal">${t.distanceZoneDescription || t.distanceZone}</span></td>
                    <td><span class="badge ${t.tripCategory === 'ESCORT' ? 'badge-escort' : 'badge-normal'}">${t.tripCategory}</span></td>
                    <td><span class="badge ${statusClass}">${t.status}</span></td>
                    <td>${t.allocatedVendorName ? `<strong>${t.allocatedVendorName}</strong>` : '<span class="text-muted">Awaiting Allocation</span>'}</td>
                    <td>${cancelBtn}</td>
                </tr>
            `;
        }).join('');
    },

    async bookTrip(event) {
        event.preventDefault();
        const submitBtn = document.getElementById('book-btn');
        submitBtn.disabled = true;
        submitBtn.textContent = 'Booking Trip...';

        const payload = {
            passengerName: document.getElementById('passengerName').value.trim(),
            pickupLocation: document.getElementById('pickupLocation').value.trim(),
            destination: document.getElementById('destination').value.trim(),
            passengerCount: parseInt(document.getElementById('passengerCount').value, 10),
            distanceKm: parseFloat(document.getElementById('distanceKm').value),
            tripCategory: document.getElementById('tripCategory').value,
            notes: document.getElementById('notes').value.trim() || undefined
        };

        try {
            const res = await api.post('/trips', payload);
            if (res && res.success) {
                showToast(`Trip #${res.data.id} successfully booked! Status: PENDING`, 'success');
                setTimeout(() => {
                    window.location.href = '/user/my-trips.html';
                }, 1200);
            }
        } catch (err) {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Confirm Booking';
        }
    },

    async cancelTrip(tripId) {
        if (!confirm(`Are you sure you want to cancel Trip #${tripId}?`)) return;

        try {
            const res = await api.post(`/trips/${tripId}/cancel`, {});
            if (res && res.success) {
                showToast(`Trip #${tripId} cancelled`, 'success');
                this.loadMyTrips();
            }
        } catch (err) {
            console.error('Error cancelling trip', err);
        }
    },

    updateZoneHint(distance) {
        const hintEl = document.getElementById('zone-hint');
        if (!hintEl) return;
        const d = parseFloat(distance);
        if (isNaN(d) || d < 0) {
            hintEl.textContent = 'Please enter a valid distance';
            hintEl.style.color = 'var(--text-muted)';
            return;
        }

        if (d < 15) {
            hintEl.textContent = 'Calculated Zone: ZONE_0_15 (0 to < 15 km)';
            hintEl.style.color = '#0284c7';
        } else if (d < 25) {
            hintEl.textContent = 'Calculated Zone: ZONE_15_25 (15 to < 25 km)';
            hintEl.style.color = '#2563eb';
        } else {
            hintEl.textContent = 'Calculated Zone: ZONE_25_PLUS (25 km and above)';
            hintEl.style.color = '#7c3aed';
        }
    }
};
