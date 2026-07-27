// Shared across teacher-dashboard.js and student-dashboard.js.
// Relies on auth.js (ilGetToken, ilGetUser, ilLogout) being loaded first.

function ilAuthHeaders(extra) {
  const headers = Object.assign({}, extra || {});
  const token = ilGetToken();
  if (token) headers['Authorization'] = 'Bearer ' + token;
  return headers;
}

async function ilAuthFetch(url, options) {
  options = options || {};
  options.headers = ilAuthHeaders(options.headers);

  const res = await fetch(url, options);

  if (res.status === 401) {
    // Token missing/expired — session is no longer valid.
    ilLogout();
    return null;
  }

  return res;
}

function ilToast(message, isError) {
  const toast = document.getElementById('toast');
  toast.textContent = message;
  toast.className = 'toast show' + (isError ? ' error' : '');
  setTimeout(() => { toast.className = 'toast'; }, 3200);
}

function escapeHtml(str) {
  if (str === null || str === undefined) return '';
  const div = document.createElement('div');
  div.textContent = str;
  return div.innerHTML;
}