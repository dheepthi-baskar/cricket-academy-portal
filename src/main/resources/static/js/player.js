if (!Api.requireRole(["PLAYER"])) {
  // redirected
} else {
  document.getElementById("logout-btn").addEventListener("click", () => Api.logout());

  const pageAlert = document.getElementById("page-alert");
  const fixturesBody = document.getElementById("fixtures-body");
  const metricsBody = document.getElementById("metrics-body");
  const fixturesEmpty = document.getElementById("fixtures-empty");
  const metricsEmpty = document.getElementById("metrics-empty");

  function isoDate(d) {
    return d.toISOString().slice(0, 10);
  }

  async function load() {
    Api.hideAlert(pageAlert);
    const today = new Date();
    const from = new Date(today);
    from.setDate(from.getDate() - 30);

    try {
      const [metrics, analytics, fixtures] = await Promise.all([
        Api.request("/api/player/me/metrics"),
        Api.request(
          `/api/player/me/analytics?from=${isoDate(from)}&to=${isoDate(today)}`
        ),
        Api.request("/api/player/me/fixtures"),
      ]);

      document.getElementById("stat-speed").textContent =
        analytics.overallAverageBowlingSpeed?.toFixed(1) ?? "—";
      document.getElementById("stat-runs").textContent =
        analytics.totalRunsInRange ?? "—";
      document.getElementById("stat-fitness").textContent =
        analytics.averageFitnessScore?.toFixed(1) ?? "—";

      renderFixtures(fixtures);
      renderMetrics(metrics);
    } catch (err) {
      Api.showAlert(pageAlert, err.message);
    }
  }

  function renderFixtures(fixtures) {
    fixturesBody.innerHTML = "";
    if (!fixtures?.length) {
      fixturesEmpty.classList.remove("hidden");
      return;
    }
    fixturesEmpty.classList.add("hidden");
    fixtures.forEach((f) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${escapeHtml(f.title)}</td>
        <td>${Api.formatDateTime(f.scheduledAt)}</td>
        <td><span class="status ${Api.statusClass(f.status)}">${f.status}</span></td>`;
      fixturesBody.appendChild(tr);
    });
  }

  function renderMetrics(metrics) {
    metricsBody.innerHTML = "";
    if (!metrics?.length) {
      metricsEmpty.classList.remove("hidden");
      return;
    }
    metricsEmpty.classList.add("hidden");
    metrics.forEach((m) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${Api.formatDate(m.metricDate)}</td>
        <td>${m.bowlingSpeed ?? "—"}</td>
        <td>${m.runsScored ?? "—"}</td>
        <td>${m.wicketsTaken ?? "—"}</td>
        <td>${m.fitnessScore ?? "—"}</td>
        <td>${escapeHtml(m.coachNotes || "—")}</td>`;
      metricsBody.appendChild(tr);
    });
  }

  function escapeHtml(s) {
    const el = document.createElement("span");
    el.textContent = s;
    return el.innerHTML;
  }

  load();
}
