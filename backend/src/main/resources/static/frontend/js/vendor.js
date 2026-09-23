// Vendor portal JavaScript
const VendorApp = {
    currentRejectTripId: null,

    async loadVendorDashboard() {
        if (!Auth.requireRole('VENDOR')) return;

        try {
            const res = await api.get('/vendor/dashboard');
            if (res && res.success) {
                const data = res.data;
                this.renderDashboard(data);
            }
        } catch (err) {
            console.error('Failed to load vendor dashboard', err);
        }
    },

    renderDashboard(d) {
        // Vendor Header
        const nameEl = document.getElementById('vendor-name-display');
        if (nameEl) nameEl.textContent = `${d.vendorName} (${d.vendorCode})`;

        // Cooldown Banner
        const cooldownBanner = document.getElementById('cooldown-banner');
        if (cooldownBanner) {
            if (d.onCooldown) {
                cooldownBanner.style.display = 'block';
                document.getElementById('cooldown-details').textContent = d.cooldownDetails || 'Vendor is currently in cooldown on one or more trips.';
            } else {
                cooldownBanner.style.display = 'none';
            }
        }

        // Capacity
        document.getElementById('val-capacity').textContent = `${d.availableCapacity} / ${d.totalCapacity}`;
        const capPct = d.totalCapacity > 0 ? ((d.availableCapacity / d.totalCapacity) * 100).toFixed(0) : 0;
        const capBar = document.getElementById('bar-capacity');
        if (capBar) capBar.style.width = `${capPct}%`;

        // Today metrics
        document.getElementById('val-today-actual').textContent = d.todayActualTrips;
        document.getElementById('val-today-expected').textContent = Number(d.todayExpectedTrips || 0).toFixed(2);
        const todayShortfallEl = document.getElementById('val-today-shortfall');
        const todaySf = Number(d.todayShortfall || 0);
        todayShortfallEl.textContent = (todaySf >= 0 ? `+${todaySf.toFixed(2)}` : todaySf.toFixed(2));
        todayShortfallEl.className = 'stat-value ' + (todaySf > 0 ? 'text-warning' : 'text-success');

        // Overall metrics
        document.getElementById('val-overall-actual').textContent = d.overallActualTrips;
        document.getElementById('val-overall-expected').textContent = Number(d.overallExpectedTrips || 0).toFixed(2);
        const overallShortfallEl = document.getElementById('val-overall-shortfall');
        const overallSf = Number(d.currentShortfall || 0);
        overallShortfallEl.textContent = (overallSf >= 0 ? `+${overallSf.toFixed(2)}` : overallSf.toFixed(2));
        overallShortfallEl.className = 'stat-value ' + (overallSf > 0 ? 'text-warning' : 'text-success');

        // Counts
        document.getElementById('count-accepted').textContent = d.acceptedTrips;
        document.getElementById('count-rejected').textContent = d.rejectedTrips;
        document.getElementById('count-completed').textContent = d.completedTrips;

        // Zone Breakdown
        const zoneContainer = document.getElementById('zone-breakdown-list');
        if (zoneContainer && d.zoneMetrics) {
            zoneContainer.innerHTML = d.zoneMetrics.map(zm => {
                const sf = Number(zm.shortfall || 0);
                const sfClass = sf > 0 ? 'color: var(--warning-text); font-weight: bold;' : 'color: var(--success-text);';
                return `
                    <div class="bar-row mb-4">
                        <div class="bar-labels">
                            <span>${zm.name} (Contractual Target: ${zm.targetPercentage}%)</span>
                            <span>Expected: ${Number(zm.expectedTrips || 0).toFixed(1)} | Actual: ${zm.actualTrips} | <span style="${sfClass}">Shortfall: ${sf > 0 ? '+' : ''}${sf.toFixed(2)}</span></span>
                        </div>
                        <div class="bar-track">
                            <div class="bar-fill bar-fill-actual" style="width: ${Math.min(100, zm.targetPercentage)}%"></div>
                        </div>
                    </div>
                `;
            }).join('');
        }

        // Category Breakdown
        const catContainer = document.getElementById('category-breakdown-list');
        if (catContainer && d.categoryMetrics) {
            catContainer.innerHTML = d.categoryMetrics.map(cm => {
                const sf = Number(cm.shortfall || 0);
                const sfClass = sf > 0 ? 'color: var(--warning-text); font-weight: bold;' : 'color: var(--success-text);';
                return `
                    <div class="bar-row mb-4">
                        <div class="bar-labels">
                            <span>${cm.name} (Contractual Target: ${cm.targetPercentage}%)</span>
                            <span>Expected: ${Number(cm.expectedTrips || 0).toFixed(1)} | Actual: ${cm.actualTrips} | <span style="${sfClass}">Shortfall: ${sf > 0 ? '+' : ''}${sf.toFixed(2)}</span></span>
                        </div>
                        <div class="bar-track">
                            <div class="bar-fill ${cm.name === 'ESCORT' ? 'bar-fill-positive' : 'bar-fill-actual'}" style="width: ${Math.min(100, cm.targetPercentage)}%"></div>
                        </div>
                    </div>
                `;
            }).join('');
        }
    },

    async loadVendorTrips() {
        if (!Auth.requireRole('VENDOR')) return;

        try {
            const res = await api.get('/vendor/trips');
            if (res && res.success) {
                this.renderTripsTable(res.data);
            }
        } catch (err) {
            console.error('Failed to load vendor trips', err);
        }
    },

    renderTripsTable(trips) {
        const tbody = document.getElementById('vendor-trips-body');
        if (!tbody) return;

        if (trips.length === 0) {
            tbody.innerHTML = `<tr><td colspan="10" class="empty-state">No trips currently offered or assigned to your vendor organization.</td></tr>`;
            return;
        }

        const now = new Date().getTime();

        tbody.innerHTML = trips.map(t => {
            const statusClass = `badge-${t.status.toLowerCase().replace(/_/g, '-')}`;
            const isOffered = (t.status === 'OFFERED_TO_VENDOR');

            let countdownHtml = '—';
            if (isOffered && t.responseDeadline) {
                const deadlineMs = new Date(t.responseDeadline).getTime();
                const diffSec = Math.max(0, Math.floor((deadlineMs - now) / 1000));
                const min = Math.floor(diffSec / 60);
                const sec = diffSec % 60;
                const timerColor = diffSec < 60 ? 'color: var(--danger); font-weight: 700;' : 'color: var(--warning-text); font-weight: 600;';
                countdownHtml = `<span style="${timerColor}">⏱ ${min}m ${sec < 10 ? '0' : ''}${sec}s</span>`;
            }

            let actionHtml = `<span class="text-muted text-xs">—</span>`;
            if (isOffered) {
                actionHtml = `
                    <div class="table-actions">
                        <button class="btn btn-success btn-sm" onclick="VendorApp.acceptTrip(${t.id})">✓ Accept</button>
                        <button class="btn btn-danger btn-sm" onclick="VendorApp.openRejectModal(${t.id})">✗ Reject</button>
                    </div>
                `;
            }

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
                    <td>${countdownHtml}</td>
                    <td>${actionHtml}</td>
                </tr>
            `;
        }).join('');
    },

    async acceptTrip(tripId) {
        if (!confirm(`Are you sure you want to ACCEPT Trip #${tripId}?`)) return;

        try {
            const res = await api.post(`/vendor/trips/${tripId}/accept`, {});
            if (res && res.success) {
                showToast(`Trip #${tripId} accepted! Status: ACCEPTED`, 'success');
                this.loadVendorTrips();
            }
        } catch (err) {
            console.error('Accept error', err);
        }
    },

    openRejectModal(tripId) {
        this.currentRejectTripId = tripId;
        document.getElementById('reject-trip-id-display').textContent = tripId;
        document.getElementById('rejection-reason').value = '';
        document.getElementById('reject-modal').classList.add('active');
    },

    closeRejectModal() {
        document.getElementById('reject-modal').classList.remove('active');
        this.currentRejectTripId = null;
    },

    async submitReject(event) {
        event.preventDefault();
        if (!this.currentRejectTripId) return;

        const reason = document.getElementById('rejection-reason').value.trim() || 'Declined by vendor operator';
        const submitBtn = document.getElementById('submit-reject-btn');
        submitBtn.disabled = true;

        try {
            const res = await api.post(`/vendor/trips/${this.currentRejectTripId}/reject`, { reason });
            if (res && res.success) {
                showToast(`Trip #${this.currentRejectTripId} rejected. Cooldown period started.`, 'warning');
                this.closeRejectModal();
                this.loadVendorTrips();
            }
        } catch (err) {
            console.error('Reject error', err);
        } finally {
            submitBtn.disabled = false;
        }
    }
};
