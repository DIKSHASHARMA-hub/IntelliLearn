async function loadSubjects() {
  const list = document.getElementById('subjectList');
  list.innerHTML = '<p class="empty-hint">Loading subjects…</p>';

  const res = await ilAuthFetch('/subjects', { method: 'GET' });
  if (!res) return;

  if (!res.ok) {
    list.innerHTML = '<p class="empty-hint">Could not load subjects.</p>';
    return;
  }

  const subjects = await res.json();

  if (!subjects.length) {
    list.innerHTML = '<p class="empty-hint">No subjects yet. Create one to get started.</p>';
    return;
  }

  list.innerHTML = '';
  subjects.forEach(subject => list.appendChild(renderSubjectCard(subject)));
}

function renderSubjectCard(subject) {
  const card = document.createElement('div');
  card.className = 'subject-card';
  card.id = 'subject-' + subject.id;

  const user = ilGetUser();
  const isOwner = subject.createdByUserId === null || subject.createdByUserId === user.id;

  const ownerActions = isOwner
    ? `<button class="ghost-btn" onclick="event.stopPropagation(); toggleEditSubject(${subject.id})">Edit</button>
       <button class="ghost-btn" onclick="event.stopPropagation(); deleteSubject(${subject.id}, '${escapeHtml(subject.name)}')">Delete</button>`
    : '';

  card.innerHTML = `
    <div class="subject-card-head">
      <div class="subject-title-group" onclick="toggleSubject(${subject.id})" style="cursor:pointer; flex:1;">
        <h3 id="subjectName-${subject.id}">${escapeHtml(subject.name)}</h3>
        <p id="subjectDesc-${subject.id}">${subject.description ? escapeHtml(subject.description) : 'No description'}</p>
      </div>
      <div style="display:flex; align-items:center; gap:10px;">
        ${ownerActions}
        <span class="chevron" onclick="toggleSubject(${subject.id})" style="cursor:pointer;">&#9656;</span>
      </div>
    </div>
    <div class="inline-form" id="editForm-${subject.id}">
      <div class="field-row">
        <input type="text" id="editName-${subject.id}" value="${escapeHtml(subject.name)}" placeholder="Subject name">
        <input type="text" id="editDesc-${subject.id}" value="${subject.description ? escapeHtml(subject.description) : ''}" placeholder="Description (optional)">
      </div>
      <button class="primary-btn" onclick="saveSubjectEdit(${subject.id})">Save changes</button>
      <button class="ghost-btn" onclick="toggleEditSubject(${subject.id})">Cancel</button>
    </div>
    <div class="subject-body">
      <div class="subject-section">
        <h4>Notes</h4>
        <div id="notes-${subject.id}"><p class="empty-hint">Not loaded yet.</p></div>
        <div class="upload-row" style="margin-top:10px;">
          <input type="text" id="noteTitle-${subject.id}" placeholder="Note title (e.g. Java Basics)">
          <input type="file" id="noteFile-${subject.id}" accept="application/pdf">
          <button class="ghost-btn" onclick="uploadNotes(${subject.id})">Upload PDF</button>
        </div>
      </div>
    </div>
  `;

  return card;
}

function toggleSubject(subjectId) {
  const card = document.getElementById('subject-' + subjectId);
  const wasExpanded = card.classList.contains('expanded');

  document.querySelectorAll('.subject-card.expanded').forEach(c => c.classList.remove('expanded'));

  if (!wasExpanded) {
    card.classList.add('expanded');
    loadNotesForSubject(subjectId);
  }
}

async function createSubject() {
  const nameInput = document.getElementById('newSubjectName');
  const descInput = document.getElementById('newSubjectDesc');
  const btn = document.getElementById('createSubjectBtn');

  const name = nameInput.value.trim();
  if (!name) { ilToast('Subject name is required', true); return; }

  btn.disabled = true;
  const res = await ilAuthFetch('/subjects', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name: name, description: descInput.value.trim() || null })
  });
  btn.disabled = false;

  if (!res) return;

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    ilToast(err.message || 'Could not create subject', true);
    return;
  }

  nameInput.value = '';
  descInput.value = '';
  document.getElementById('newSubjectForm').classList.remove('open');
  ilToast('Subject created');
  loadSubjects();
}

function toggleEditSubject(subjectId) {
  document.getElementById('editForm-' + subjectId).classList.toggle('open');
}

async function saveSubjectEdit(subjectId) {
  const nameInput = document.getElementById('editName-' + subjectId);
  const descInput = document.getElementById('editDesc-' + subjectId);

  const name = nameInput.value.trim();
  if (!name) { ilToast('Subject name is required', true); return; }

  const res = await ilAuthFetch('/subjects/' + subjectId, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name: name, description: descInput.value.trim() || null })
  });
  if (!res) return;

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    ilToast(err.message || 'Could not update subject', true);
    return;
  }

  const updated = await res.json();
  document.getElementById('subjectName-' + subjectId).textContent = updated.name;
  document.getElementById('subjectDesc-' + subjectId).textContent = updated.description || 'No description';
  toggleEditSubject(subjectId);
  ilToast('Subject updated');
}

async function deleteSubject(subjectId, subjectName) {
  const confirmed = confirm(
    'Delete "' + subjectName + '"? This also removes all of its notes, ' +
    'generated quizzes, and any student attempts on those quizzes. This cannot be undone.'
  );
  if (!confirmed) return;

  const res = await ilAuthFetch('/subjects/' + subjectId, { method: 'DELETE' });
  if (!res) return;

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    ilToast(err.message || 'Could not delete subject', true);
    return;
  }

  ilToast('Subject deleted');
  loadSubjects();
}



async function loadNotesForSubject(subjectId) {
  const container = document.getElementById('notes-' + subjectId);
  const res = await ilAuthFetch('/notes/subject/' + subjectId, { method: 'GET' });
  if (!res) return;

  if (!res.ok) {
    container.innerHTML = '<p class="empty-hint">No notes uploaded yet.</p>';
    return;
  }

  const notes = await res.json().catch(() => []);
  if (!notes || !notes.length) {
    container.innerHTML = '<p class="empty-hint">No notes uploaded yet.</p>';
    return;
  }

  const user = ilGetUser();

  container.innerHTML = notes.map(note => {
    const isOwner = note.uploadedByUserId === null || note.uploadedByUserId === user.id;
    const removeBtn = isOwner
      ? `<button class="ghost-btn" onclick="deleteNotes(${subjectId}, ${note.id})">Remove</button>`
      : '';
    return `
    <div class="note-row">
      <span>${escapeHtml(note.title)} <span class="empty-hint">(${escapeHtml(note.fileName)})</span></span>
      ${removeBtn}
    </div>
  `;
  }).join('');
}

async function deleteNotes(subjectId, noteId) {
  if (!confirm('Remove this PDF?')) return;

  const res = await ilAuthFetch('/notes/' + noteId, { method: 'DELETE' });
  if (!res) return;

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    ilToast(err.message || 'Could not remove notes', true);
    return;
  }

  ilToast('Notes removed');
  loadNotesForSubject(subjectId);
}

async function uploadNotes(subjectId) {
  const titleInput = document.getElementById('noteTitle-' + subjectId);
  const fileInput = document.getElementById('noteFile-' + subjectId);

  const title = titleInput.value.trim();
  const file = fileInput.files[0];

  if (!title || !file) {
    ilToast('Add a title and choose a PDF first', true);
    return;
  }

  const formData = new FormData();
  formData.append('title', title);
  formData.append('file', file);

  ilToast('Uploading…');

  
  const res = await ilAuthFetch('/notes/upload/' + subjectId, {
    method: 'POST',
    body: formData
  });
  if (!res) return;

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    ilToast(err.message || 'Upload failed', true);
    return;
  }

  titleInput.value = '';
  fileInput.value = '';
  ilToast('Notes uploaded');
  loadNotesForSubject(subjectId);
}



function showView(viewName) {
  document.querySelectorAll('.sd-view').forEach(v => v.style.display = 'none');
  document.getElementById('view-' + viewName).style.display = 'block';

  document.querySelectorAll('.sidebar-link[data-view]').forEach(link => {
    link.classList.toggle('active', link.dataset.view === viewName);
  });

  if (viewName === 'dashboard') refreshSubjectCount();
}

async function refreshSubjectCount() {
  const res = await ilAuthFetch('/subjects', { method: 'GET' });
  if (!res || !res.ok) return;
  const subjects = await res.json();
  document.getElementById('subjectCount').textContent = subjects.length;
}

// ---------- Profile ----------

function renderProfile() {
  const user = ilGetUser();
  document.getElementById('profileName').textContent = user.firstName + ' ' + user.lastName;
  document.getElementById('profileEmail').textContent = user.email;
  document.getElementById('profilePhone').textContent = user.phone || '—';
  document.getElementById('profileRole').textContent = user.role;
}

function toggleEditProfile() {
  const user = ilGetUser();
  document.getElementById('editFirstName').value = user.firstName;
  document.getElementById('editLastName').value = user.lastName;
  document.getElementById('editPhone').value = user.phone || '';
  document.getElementById('editProfileForm').classList.toggle('open');
}

async function saveProfileEdit() {
  const firstNameInput = document.getElementById('editFirstName');
  const lastNameInput = document.getElementById('editLastName');
  const phoneInput = document.getElementById('editPhone');

  const firstName = firstNameInput.value.trim();
  const lastName = lastNameInput.value.trim();
  const phone = phoneInput.value.trim();

  if (!firstName || !lastName) { ilToast('First and last name are required', true); return; }
  if (!/^[0-9]{10}$/.test(phone)) { ilToast('Phone number must be 10 digits', true); return; }

  const res = await ilAuthFetch('/api/users/profile', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ firstName: firstName, lastName: lastName, phone: phone })
  });
  if (!res) return;

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    ilToast(err.message || 'Could not update profile', true);
    return;
  }

  const updated = await res.json();
  ilUpdateStoredUser(updated);
  document.getElementById('userNameInline').textContent = updated.firstName + ' ' + updated.lastName;
  renderProfile();
  document.getElementById('editProfileForm').classList.remove('open');
  ilToast('Profile updated');
}

// ---------- Init ----------

document.addEventListener('DOMContentLoaded', () => {
  const user = ilGetUser();
  if (!user || !ilGetToken() || user.role !== 'TEACHER') {
    window.location.href = '/login';
    return;
  }

  document.getElementById('userNameInline').textContent = user.firstName + ' ' + user.lastName;
  renderProfile();

  loadSubjects();
  refreshSubjectCount();

  document.getElementById('showCreateFormBtn').addEventListener('click', () => {
    document.getElementById('newSubjectForm').classList.toggle('open');
  });

  document.getElementById('createSubjectBtn').addEventListener('click', createSubject);
});