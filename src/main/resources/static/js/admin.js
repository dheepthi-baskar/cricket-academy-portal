if (!Api.requireRole(["ADMIN"])) {
  // redirected
} else {
  document.getElementById("logout-btn").addEventListener("click", () => Api.logout());

  const pageAlert = document.getElementById("page-alert");
  let batchesCache = [];

  document.querySelectorAll("#tabs .tab").forEach((tab) => {
    tab.addEventListener("click", () => {
      document.querySelectorAll("#tabs .tab").forEach((t) => t.classList.remove("active"));
      document.querySelectorAll(".panel").forEach((p) => p.classList.remove("active"));
      tab.classList.add("active");
      document.getElementById(tab.dataset.panel).classList.add("active");
    });
  });

  document.getElementById("batch-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const alertEl = document.getElementById("batch-alert");
    Api.hideAlert(alertEl);
    try {
      await Api.request("/api/admin/batches", {
        method: "POST",
        body: JSON.stringify({
          name: document.getElementById("batch-name").value.trim(),
          description: document.getElementById("batch-desc").value.trim() || null,
          coachUserId: Number(document.getElementById("coach-user-id").value),
        }),
      });
      Api.showAlert(alertEl, "Batch created.", "success");
      e.target.reset();
      document.getElementById("coach-user-id").value = "2";
      await loadBatches();
    } catch (err) {
      Api.showAlert(alertEl, err.message);
    }
  });

  document.getElementById("facility-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const alertEl = document.getElementById("facility-alert");
    Api.hideAlert(alertEl);
    try {
      await Api.request("/api/admin/facilities", {
        method: "POST",
        body: JSON.stringify({
          name: document.getElementById("facility-name").value.trim(),
          description: document.getElementById("facility-desc").value.trim() || null,
        }),
      });
      Api.showAlert(alertEl, "Facility added.", "success");
      e.target.reset();
      await loadFacilities();
    } catch (err) {
      Api.showAlert(alertEl, err.message);
    }
  });

  document.getElementById("fixture-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const alertEl = document.getElementById("fixture-alert");
    Api.hideAlert(alertEl);
    const when = document.getElementById("fixture-when").value;
    try {
      await Api.request("/api/admin/fixtures", {
        method: "POST",
        body: JSON.stringify({
          title: document.getElementById("fixture-title").value.trim(),
          batchId: Number(document.getElementById("fixture-batch").value),
          scheduledAt: new Date(when).toISOString(),
        }),
      });
      Api.showAlert(alertEl, "Fixture scheduled.", "success");
      e.target.reset();
      await loadFixtures();
    } catch (err) {
      Api.showAlert(alertEl, err.message);
    }
  });

  async function init() {
    Api.hideAlert(pageAlert);
    document.getElementById("coach-user-id").value = "2";
    try {
      await loadBatches();
      await loadFacilities();
      await loadFixtures();
    } catch (err) {
      Api.showAlert(pageAlert, err.message);
    }
  }

  async function loadBatches() {
    batchesCache = await Api.request("/api/admin/batches");
    const tbody = document.getElementById("batches-body");
    tbody.innerHTML = "";
    batchesCache.forEach((b) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${esc(b.name)}</td>
        <td>${esc(b.coachName)}</td>
        <td>${esc(b.description || "—")}</td>`;
      tbody.appendChild(tr);
    });

    const batchSelect = document.getElementById("fixture-batch");
    batchSelect.innerHTML = "";
    batchesCache.forEach((b) => {
      const opt = document.createElement("option");
      opt.value = b.id;
      opt.textContent = b.name;
      batchSelect.appendChild(opt);
    });
  }

  async function loadFacilities() {
    const rows = await Api.request("/api/admin/facilities");
    const tbody = document.getElementById("facilities-body");
    tbody.innerHTML = "";
    rows.forEach((f) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `<td>${esc(f.name)}</td><td>${esc(f.description || "—")}</td>`;
      tbody.appendChild(tr);
    });
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
    const batchName = (id) => batchesCache.find((b) => b.id === id)?.name ?? id;
    fixtures.forEach((f) => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${esc(f.title)}</td>
        <td>${esc(batchName(f.batchId))}</td>
        <td>${Api.formatDateTime(f.scheduledAt)}</td>
        <td><span class="status ${Api.statusClass(f.status)}">${f.status}</span></td>`;
      tbody.appendChild(tr);
    });
  }

  function esc(s) {
    const el = document.createElement("span");
    el.textContent = s ?? "";
    return el.innerHTML;
  }

  init();
}
