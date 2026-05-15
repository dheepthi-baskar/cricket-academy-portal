if (!Api.requireRole(["COACH", "ADMIN"])) {
  // redirected
} else {
  document.getElementById("logout-btn").addEventListener("click", () => Api.logout());

  const pageAlert = document.getElementById("page-alert");
  const metricAlert = document.getElementById("metric-alert");
  const bookingAlert = document.getElementById("booking-alert");
  const playerSelect = document.getElementById("player-select");
  const metricDate = document.getElementById("metric-date");

  metricDate.value = new Date().toISOString().slice(0, 10);

  document.querySelectorAll("#tabs .tab").forEach((tab) => {
    tab.addEventListener("click", () => {
      document.querySelectorAll("#tabs .tab").forEach((t) => t.classList.remove("active"));
      document.querySelectorAll(".panel").forEach((p) => p.classList.remove("active"));
      tab.classList.add("active");
      document.getElementById(tab.dataset.panel).classList.add("active");
    });
  });

  playerSelect.addEventListener("change", () => loadPlayerMetrics(playerSelect.value));

  document.getElementById("metric-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    Api.hideAlert(metricAlert);
    const playerId = playerSelect.value;
    if (!playerId) return;

    const body = {
      metricDate: metricDate.value,
      bowlingSpeed: numOrNull(document.getElementById("bowling-speed").value),
      runsScored: intOrNull(document.getElementById("runs-scored").value),
      wicketsTaken: intOrNull(document.getElementById("wickets").value),
      fitnessScore: intOrNull(document.getElementById("fitness").value),
      coachNotes: document.getElementById("notes").value || null,
    };

    try {
      await Api.request(`/api/coach/players/${playerId}/metrics`, {
        method: "POST",
        body: JSON.stringify(body),
      });
      Api.showAlert(metricAlert, "Metric saved.", "success");
      e.target.reset();
      metricDate.value = new Date().toISOString().slice(0, 10);
      loadPlayerMetrics(playerId);
    } catch (err) {
      Api.showAlert(metricAlert, err.message);
    }
  });

  document.getElementById("booking-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    Api.hideAlert(bookingAlert);
    const start = document.getElementById("start-time").value;
    const end = document.getElementById("end-time").value;
    if (!start || !end) return;

    try {
      await Api.request("/api/coach/bookings", {
        method: "POST",
        body: JSON.stringify({
          facilityId: Number(document.getElementById("facility-select").value),
          startTime: new Date(start).toISOString(),
          endTime: new Date(end).toISOString(),
          notes: document.getElementById("booking-notes").value || null,
        }),
      });
      Api.showAlert(bookingAlert, "Booking created.", "success");
      e.target.reset();
      loadBookings();
    } catch (err) {
      Api.showAlert(bookingAlert, err.message);
    }
  });

  async function init() {
    Api.hideAlert(pageAlert);
    try {
      const [players, facilities] = await Promise.all([
        Api.request("/api/coach/players"),
        Api.request("/api/coach/facilities"),
      ]);
      fillSelect(playerSelect, players, (p) => p.id, (p) => `${p.displayName} (${p.batchName})`);
      fillSelect(
        document.getElementById("facility-select"),
        facilities,
        (f) => f.id,
        (f) => f.name
      );
      if (players.length) {
        playerSelect.value = players[0].id;
        loadPlayerMetrics(players[0].id);
      }
      await Promise.all([loadFixtures(), loadBilling(), loadBookings()]);
    } catch (err) {
      Api.showAlert(pageAlert, err.message);
    }
  }

  function fillSelect(select, items, valueFn, labelFn) {
    select.innerHTML = "";
    items.forEach((item) => {
      const opt = document.createElement("option");
      opt.value = valueFn(item);
      opt.textContent = labelFn(item);
      select.appendChild(opt);
    });
  }

  async function loadPlayerMetrics(playerId) {
    const body = document.getElementById("player-metrics-body");
    const empty = document.getElementById("player-metrics-empty");
    body.innerHTML = "";
    if (!playerId) {
      empty.classList.remove("hidden");
      return;
    }
    try {
      const metrics = await Api.request(`/api/coach/players/${playerId}/metrics`);
      if (!metrics.length) {
        empty.classList.remove("hidden");
        return;
      }
      empty.classList.add("hidden");
      metrics.forEach((m) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${Api.formatDate(m.metricDate)}</td>
          <td>${m.bowlingSpeed ?? "—"}</td>
          <td>${m.runsScored ?? "—"}</td>
          <td>${m.fitnessScore ?? "—"}</td>`;
        body.appendChild(tr);
      });
    } catch (err) {
      empty.textContent = err.message;
      empty.classList.remove("hidden");
    }
  }

  async function loadFixtures() {
    const fixtures = await Api.request("/api/coach/fixtures");
    const tbody = document.getElementById("fixtures-body");
    const empty = document.getElementById("fixtures-empty");
    tbody.innerHTML = "";
    if (!fixtures.length) {
      empty.classList.remove("hidden");
      return;
    }
    empty.classList.add("hidden");
    fixtures.forEach((f) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${esc(f.title)}</td>
        <td>${Api.formatDateTime(f.scheduledAt)}</td>
        <td><span class="status ${Api.statusClass(f.status)}">${f.status}</span></td>
        <td class="actions">${fixtureActions(f)}</td>`;
      tbody.appendChild(tr);
      tr.querySelectorAll("[data-action]").forEach((btn) => {
        btn.addEventListener("click", () => fixtureAction(f.id, btn.dataset.action));
      });
    });
  }

  function fixtureActions(f) {
    const s = f.status;
    const parts = [];
    if (s === "SCHEDULED") {
      parts.push(`<button type="button" class="btn btn-gold btn-sm" data-action="go-live">Go live</button>`);
      parts.push(`<button type="button" class="btn btn-danger btn-sm" data-action="cancel">Cancel</button>`);
    }
    if (s === "LIVE") {
      parts.push(`<button type="button" class="btn btn-primary btn-sm" data-action="complete">Complete</button>`);
    }
    return parts.join("") || "—";
  }

  async function fixtureAction(id, action) {
    Api.hideAlert(pageAlert);
    const path = `/api/coach/fixtures/${id}/${action}`;
    try {
      await Api.request(path, { method: "POST" });
      await loadFixtures();
    } catch (err) {
      Api.showAlert(pageAlert, err.message);
    }
  }

  async function loadBilling() {
    const rows = await Api.request("/api/coach/billing/summaries");
    const tbody = document.getElementById("billing-body");
    const empty = document.getElementById("billing-empty");
    tbody.innerHTML = "";
    if (!rows.length) {
      empty.classList.remove("hidden");
      return;
    }
    empty.classList.add("hidden");
    rows.forEach((b) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${esc(b.playerName)}</td>
        <td>${esc(b.periodLabel)}</td>
        <td>${b.amountDue}</td>`;
      tbody.appendChild(tr);
    });
  }

  async function loadBookings() {
    const rows = await Api.request("/api/coach/bookings/mine");
    const tbody = document.getElementById("bookings-body");
    const empty = document.getElementById("bookings-empty");
    tbody.innerHTML = "";
    if (!rows.length) {
      empty.classList.remove("hidden");
      return;
    }
    empty.classList.add("hidden");
    rows.forEach((b) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${esc(b.facilityName)}</td>
        <td>${Api.formatDateTime(b.startTime)}</td>
        <td>${Api.formatDateTime(b.endTime)}</td>`;
      tbody.appendChild(tr);
    });
  }

  function numOrNull(v) {
    return v === "" ? null : Number(v);
  }

  function intOrNull(v) {
    return v === "" ? null : parseInt(v, 10);
  }

  function esc(s) {
    const el = document.createElement("span");
    el.textContent = s ?? "";
    return el.innerHTML;
  }

  init();
}
