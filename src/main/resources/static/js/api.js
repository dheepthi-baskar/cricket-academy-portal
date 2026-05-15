const Api = {
  getToken() {
    return localStorage.getItem("token");
  },

  getRole() {
    return localStorage.getItem("role");
  },

  getUserId() {
    return localStorage.getItem("userId");
  },

  setSession(token, role, userId) {
    localStorage.setItem("token", token);
    localStorage.setItem("role", role);
    localStorage.setItem("userId", String(userId));
  },

  clearSession() {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("userId");
  },

  requireRole(allowedRoles) {
    const role = this.getRole();
    if (!this.getToken() || !role) {
      window.location.href = "/";
      return false;
    }
    if (!allowedRoles.includes(role)) {
      this.redirectForRole(role);
      return false;
    }
    return true;
  },

  redirectForRole(role) {
    const map = {
      PLAYER: "/player.html",
      COACH: "/coach.html",
      ADMIN: "/admin.html",
    };
    window.location.href = map[role] || "/";
  },

  async request(path, options = {}) {
    const headers = { "Content-Type": "application/json", ...options.headers };
    const token = this.getToken();
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    const res = await fetch(path, { ...options, headers });
    const text = await res.text();
    let body = null;
    if (text) {
      try {
        body = JSON.parse(text);
      } catch {
        body = { message: text };
      }
    }

    if (res.status === 401) {
      this.clearSession();
      window.location.href = "/";
      throw new Error("Session expired. Please sign in again.");
    }

    if (!res.ok) {
      throw new Error(body?.message || `Request failed (${res.status})`);
    }

    return body;
  },

  login(email, password) {
    return this.request("/api/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    });
  },

  logout() {
    this.clearSession();
    window.location.href = "/";
  },

  formatDate(iso) {
    if (!iso) return "—";
    return new Date(iso).toLocaleDateString(undefined, {
      year: "numeric",
      month: "short",
      day: "numeric",
    });
  },

  formatDateTime(iso) {
    if (!iso) return "—";
    return new Date(iso).toLocaleString(undefined, {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  },

  statusClass(status) {
    const s = (status || "").toLowerCase();
    if (s === "live") return "status-live";
    if (s === "completed") return "status-completed";
    if (s === "cancelled") return "status-cancelled";
    return "status-scheduled";
  },

  showAlert(el, message, type = "error") {
    if (!el) return;
    el.textContent = message;
    el.className = `alert alert-${type}`;
    el.classList.remove("hidden");
  },

  hideAlert(el) {
    if (el) el.classList.add("hidden");
  },
};
