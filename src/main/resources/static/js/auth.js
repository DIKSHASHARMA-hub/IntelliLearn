// Talks to the existing JSON REST API (/api/users/register, /api/users/login)
// and stores the returned JWT for subsequent requests.

const IL_TOKEN_KEY = 'il_token';
const IL_USER_KEY = 'il_user';

function ilStoreSession(userResponse) {
  localStorage.setItem(IL_TOKEN_KEY, userResponse.token);
  localStorage.setItem(IL_USER_KEY, JSON.stringify({
    id: userResponse.id,
    firstName: userResponse.firstName,
    lastName: userResponse.lastName,
    email: userResponse.email,
    role: userResponse.role
  }));
}

function ilGetUser() {
  const raw = localStorage.getItem(IL_USER_KEY);
  return raw ? JSON.parse(raw) : null;
}

function ilGetToken() {
  return localStorage.getItem(IL_TOKEN_KEY);
}

function ilLogout() {
  localStorage.removeItem(IL_TOKEN_KEY);
  localStorage.removeItem(IL_USER_KEY);
  window.location.href = '/login';
}

function ilShowMessage(el, text, type) {
  el.textContent = text;
  el.className = 'form-message ' + type;
}

async function ilSubmitForm(url, payload, messageEl, button) {
  button.disabled = true;
  const originalLabel = button.textContent;
  button.textContent = 'Please wait…';

  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    const data = await res.json().catch(() => ({}));

    if (!res.ok) {
      ilShowMessage(messageEl, data.message || 'Something went wrong. Please try again.', 'error');
      button.disabled = false;
      button.textContent = originalLabel;
      return;
    }

    ilStoreSession(data);
    ilShowMessage(messageEl, 'Success — redirecting…', 'success');
    window.location.href = '/home';

  } catch (err) {
    ilShowMessage(messageEl, 'Could not reach the server. Is it running?', 'error');
    button.disabled = false;
    button.textContent = originalLabel;
  }
}