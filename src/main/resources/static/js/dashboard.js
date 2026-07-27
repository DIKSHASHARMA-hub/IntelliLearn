// Teacher dashboard logic. Relies on auth.js and common.js (ilAuthFetch,
// ilToast, escapeHtml) having already been loaded on the page.

// ---------- Subjects ----------

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

  card.innerHTML = `
    <div class="subject-card-head" onclick="toggleSubject(${subject.id})">
      <div class="subject-title-group">
        <h3>${escapeHtml(subject.name)}</h3>
        <p>${subject.description ? escapeHtml(subject.description) : 'No description'}</p>
      </div>
      <span class="chevron">&#9656;</span>
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

// ---------- Notes ----------

async function loadNotesForSubject(subjectId) {
  const container = document.getElementById('notes-' + subjectId);
  const res = await ilAuthFetch('/notes/subject/' + subjectId, { method: 'GET' });
  if (!res) return;

  if (!res.ok) {
    container.innerHTML = '<p class="empty-hint">No notes uploaded yet.</p>';
    setUploadFormEnabled(subjectId, true);
    return;
  }

  const note = await res.json().catch(() => null);
  if (!note || !note.id) {
    container.innerHTML = '<p class="empty-hint">No notes uploaded yet.</p>';
    setUploadFormEnabled(subjectId, true);
    return;
  }

  container.innerHTML = `
    <div class="note-row">
      <span>${escapeHtml(note.title)} <span class="empty-hint">(${escapeHtml(note.fileName)})</span></span>
      <button class="ghost-btn" onclick="deleteNotes(${subjectId})">Remove</button>
    </div>
    <p class="empty-hint" style="margin-top:8px;">Only one PDF per subject — remove this one to upload a replacement.</p>
  `;
  setUploadFormEnabled(subjectId, false);
}

function setUploadFormEnabled(subjectId, enabled) {
  const titleInput = document.getElementById('noteTitle-' + subjectId);
  const fileInput = document.getElementById('noteFile-' + subjectId);
  if (!titleInput || !fileInput) return;
  titleInput.style.display = enabled ? '' : 'none';
  fileInput.style.display = enabled ? '' : 'none';
  const uploadBtn = fileInput.parentElement.querySelector('.ghost-btn');
  if (uploadBtn) uploadBtn.style.display = enabled ? '' : 'none';
}

async function deleteNotes(subjectId) {
  if (!confirm('Remove the current PDF for this subject?')) return;

  const res = await ilAuthFetch('/notes/subject/' + subjectId, { method: 'DELETE' });
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

  // Note: no Content-Type header here on purpose — the browser sets the
  // correct multipart boundary automatically for FormData.
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

// ---------- Init ----------

document.addEventListener('DOMContentLoaded', () => {
  const user = ilGetUser();
  if (!user || !ilGetToken() || user.role !== 'TEACHER') {
    window.location.href = '/login';
    return;
  }

  document.getElementById('userName').textContent = user.firstName + ' ' + user.lastName;
  loadSubjects();

  document.getElementById('showCreateFormBtn').addEventListener('click', () => {
    document.getElementById('newSubjectForm').classList.toggle('open');
  });

  document.getElementById('createSubjectBtn').addEventListener('click', createSubject);
});