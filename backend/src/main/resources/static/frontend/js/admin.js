const AdminApp = {
    async loadDashboard() {
        if (!Auth.requireRole('ADMIN')) return;

        try {
            const response = await api.get('/admin/dashboard');
            if (response && response.success) {
                this.renderDashboard(response.data);
            }
        } catch (error) {
            console.error('Failed to load admin dashboard', error);
        }
    },

    renderDashboard(data) {
        const values = {
            'total-trips-today': data.totalTripsToday,
            'pending-trips': data.pendingTrips,
            'accepted-trips': data.acceptedTrips,
            'completed-trips': data.completedTrips,
            'active-vendors': data.activeVendors,
            'total-vendors': data.totalVendors,
            'vendor-cooldowns': data.vendorsOnCooldown
        };

        Object.entries(values).forEach(([id, value]) => {
            const element = document.getElementById(id);
            if (element) element.textContent = value ?? 0;
        });

        this.renderPendingTrips(data.pendingTripsList || []);
        this.renderVendors(data.vendors || []);
    },

    renderPendingTrips(trips) {
        const body = document.getElementById('pending-trips-body');
        if (!body) return;
        if (trips.length === 0) {
            body.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No trips are waiting for allocation.</td></tr>';
            return;
        }

        body.innerHTML = trips.map(trip => `
            <tr>
                <td><strong>#${trip.id}</strong></td>
                <td>${this.escape(trip.pickupLocation)}</td>
                <td>${this.escape(trip.destination)}</td>
                <td>${trip.passengerCount}</td>
                <td>${this.escape(trip.distanceZoneDescription || trip.distanceZone || '-')}</td>
                <td>${this.escape(trip.tripCategory || '-')}</td>
                <td><button class="btn btn-primary btn-sm" onclick="AdminApp.allocateTrip(${trip.id})">Allocate</button></td>
            </tr>
        `).join('');
    },

    renderVendors(vendors) {
        const body = document.getElementById('vendors-body');
        if (!body) return;
        if (vendors.length === 0) {
            body.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No vendors registered.</td></tr>';
            return;
        }

        body.innerHTML = vendors.map(vendor => `
            <tr>
                <td>${this.escape(vendor.vendorName)}</td>
                <td>${this.escape(vendor.vendorCode)}</td>
                <td><span class="badge ${vendor.status === 'ACTIVE' ? 'badge-accepted' : 'badge-rejected'}">${this.escape(vendor.status)}</span></td>
                <td>${vendor.availableCapacity ?? 0} / ${vendor.totalCapacity ?? 0}</td>
                <td>${vendor.totalAllocatedTrips ?? 0}</td>
                <td>${vendor.totalAcceptedTrips ?? 0}</td>
            </tr>
        `).join('');
    },

    async allocateTrip(tripId) {
        try {
            const response = await api.post(`/admin/trips/${tripId}/allocate`, {});
            if (response && response.success) {
                showToast(`Trip #${tripId} allocated successfully`, 'success');
                await this.loadDashboard();
            }
        } catch (error) {
            console.error(`Failed to allocate trip ${tripId}`, error);
        }
    },

    escape(value) {
        return String(value ?? '').replace(/[&<>'"]/g, character => ({
            '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;'
        })[character]);
    }
};
