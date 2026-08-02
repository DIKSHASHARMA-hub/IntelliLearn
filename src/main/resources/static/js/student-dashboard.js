
let currentQuiz = null; 
let lastDashboardData = null; 

async function loadDashboardSummary() {
  const user = ilGetUser();
  const res = await ilAuthFetch('/dashboard/student/' + user.id, { method: 'GET' });

  const box = document.getElementById('dashboardHeadline');
  if (!res || !res.ok) {
    box.innerHTML = '<p class="empty-hint">No attempts yet — generate a quiz from a subject to get started.</p>';
    return;
  }

  lastDashboardData = await res.json();

  box.innerHTML = `
    <div class="stat-card">
      <p class="stat-label">Average score so far</p>
      <p class="stat-value">${formatPercent(lastDashboardData.averageScore)}</p>
    </div>
  `;
}



async function loadProgressStats() {
  if (!lastDashboardData) {
    const user = ilGetUser();
    const res = await ilAuthFetch('/dashboard/student/' + user.id, { method: 'GET' });
    if (res && res.ok) lastDashboardData = await res.json();
  }

  if (!lastDashboardData) {
    document.getElementById('statsRow').innerHTML =
      '<p class="empty-hint">No attempts yet — take a quiz to see your progress here.</p>';
    return;
  }

  renderStats(lastDashboardData);
}

function renderStats(data) {
  document.getElementById('statsRow').innerHTML = `
    <div class="stat-card">
      <p class="stat-label">Quizzes attempted</p>
      <p class="stat-value">${data.totalQuizzesAttempted ?? 0}</p>
    </div>
    <div class="stat-card">
      <p class="stat-label">Average score</p>
      <p class="stat-value">${formatPercent(data.averageScore)}</p>
    </div>
    <div class="stat-card">
      <p class="stat-label">Highest score</p>
      <p class="stat-value">${formatPercent(data.highestScore)}</p>
    </div>
  `;
}



async function loadQuizHistory() {
  if (!lastDashboardData) {
    const user = ilGetUser();
    const res = await ilAuthFetch('/dashboard/student/' + user.id, { method: 'GET' });
    if (res && res.ok) lastDashboardData = await res.json();
  }

  renderRecentAttempts((lastDashboardData && lastDashboardData.recentAttempts) || []);
}

function renderRecentAttempts(attempts) {
  const container = document.getElementById('recentAttempts');

  if (!attempts.length) {
    container.innerHTML = '<p class="empty-hint">No attempts yet — generate a quiz from the Subjects page.</p>';
    return;
  }

  container.innerHTML = attempts.map(a => `
    <div class="attempt-row">
      <span>${escapeHtml(a.quizTitle)}</span>
      <span class="score-pill">${a.score}/${a.totalQuestions} · ${formatPercent(a.percentage)}</span>
    </div>
  `).join('');
}

function formatPercent(value) {
  if (value === null || value === undefined) return '—';
  return Math.round(value) + '%';
}



async function loadSubjects() {
  const list = document.getElementById('subjectPickList');
  list.innerHTML = '<p class="empty-hint">Loading subjects…</p>';

  const res = await ilAuthFetch('/subjects', { method: 'GET' });
  if (!res) return;

  if (!res.ok) {
    list.innerHTML = '<p class="empty-hint">Could not load subjects.</p>';
    return;
  }

  const subjects = await res.json();

  if (!subjects.length) {
    list.innerHTML = '<p class="empty-hint">No subjects available yet.</p>';
    return;
  }

  list.innerHTML = subjects.map(s => `
    <div class="subject-pick-row">
      <div style="width:100%;">
        <h3>${escapeHtml(s.name)}</h3>
        <p>${s.description ? escapeHtml(s.description) : 'No description'}</p>
        <div id="notesInfo-${s.id}" style="margin-top:8px;"><p class="empty-hint">Checking notes…</p></div>
      </div>
    </div>
  `).join('');

  subjects.forEach(s => loadNotesPreview(s.id));
}

async function loadNotesPreview(subjectId) {
  const container = document.getElementById('notesInfo-' + subjectId);
  const res = await ilAuthFetch('/notes/subject/' + subjectId, { method: 'GET' });
  if (!res) return;

  if (!res.ok) {
    container.innerHTML = '<p class="empty-hint">No notes uploaded for this subject yet.</p>';
    return;
  }

  const notes = await res.json().catch(() => []);
  if (!notes || !notes.length) {
    container.innerHTML = '<p class="empty-hint">No notes uploaded for this subject yet.</p>';
    return;
  }

  container.innerHTML = notes.map(note => `
    <div class="note-line" style="display:flex; align-items:center; justify-content:space-between; gap:10px; margin-bottom:6px;">
      <span class="score-pill" style="cursor:pointer;" onclick="downloadNotes(${note.id}, '${escapeHtml(note.fileName)}')">
        &#8595; ${escapeHtml(note.title)}
      </span>
      <button class="primary-btn" style="padding:7px 12px; font-size:12px;" onclick="generateAndTakeQuiz(${note.id})" id="genBtn-${note.id}">
        Generate quiz
      </button>
    </div>
  `).join('');
}

async function downloadNotes(noteId, fileName) {
  const res = await ilAuthFetch('/notes/' + noteId + '/download', { method: 'GET' });
  if (!res) return;

  if (!res.ok) {
    ilToast('Could not download notes', true);
    return;
  }

  const blob = await res.blob();
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = fileName || 'notes.pdf';
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
}



async function loadMyNotes() {
  const container = document.getElementById('myNotesList');
  container.innerHTML = '<p class="empty-hint">Loading your notes…</p>';

  const subjectsRes = await ilAuthFetch('/subjects', { method: 'GET' });
  if (!subjectsRes || !subjectsRes.ok) {
    container.innerHTML = '<p class="empty-hint">Could not load notes.</p>';
    return;
  }

  const subjects = await subjectsRes.json();

  if (!subjects.length) {
    container.innerHTML = '<p class="empty-hint">No subjects yet.</p>';
    return;
  }

  const perSubjectNotes = await Promise.all(subjects.map(async s => {
    const res = await ilAuthFetch('/notes/subject/' + s.id, { method: 'GET' });
    if (!res || !res.ok) return { subject: s, notes: [] };
    const notes = await res.json().catch(() => []);
    return { subject: s, notes: notes || [] };
  }));

  const withNotes = perSubjectNotes.filter(group => group.notes.length);

  if (!withNotes.length) {
    container.innerHTML = '<p class="empty-hint">No notes have been uploaded yet.</p>';
    return;
  }

  container.innerHTML = withNotes.map(group => `
    <div style="margin-bottom:18px;">
      <h4 style="font-family: var(--font-mono); font-size: 11px; letter-spacing: 0.08em; text-transform: uppercase; color: var(--ink-muted); margin: 0 0 8px;">
        ${escapeHtml(group.subject.name)}
      </h4>
      ${group.notes.map(note => `
        <div class="note-row">
          <span>${escapeHtml(note.title)} <span class="empty-hint">(${escapeHtml(note.fileName)})</span></span>
          <button class="ghost-btn" onclick="downloadNotes(${note.id}, '${escapeHtml(note.fileName)}')">Download</button>
        </div>
      `).join('')}
    </div>
  `).join('');
}



async function generateAndTakeQuiz(noteId) {
  const btn = document.getElementById('genBtn-' + noteId);
  btn.disabled = true;
  btn.textContent = 'Generating…';

  const res = await ilAuthFetch('/quiz/generate/note/' + noteId, { method: 'POST' });

  btn.disabled = false;
  btn.textContent = 'Generate quiz';

  if (!res) return;

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    ilToast(err.message || 'Quiz generation failed', true);
    return;
  }

  currentQuiz = await res.json();
  renderQuizRunner();
  showView('quiz');
}

function renderQuizRunner() {
  document.getElementById('quizRunnerTitle').textContent = currentQuiz.title;

  const container = document.getElementById('quizQuestions');
  container.innerHTML = (currentQuiz.questions || []).map((q, index) => `
    <div class="quiz-question-block">
      <p class="q-num">Question ${index + 1} of ${currentQuiz.questions.length}</p>
      <p class="q-text">${escapeHtml(q.question)}</p>
      ${['A', 'B', 'C', 'D'].map(letter => `
        <label class="quiz-option">
          <input type="radio" name="q-${q.questionId}" value="${letter}">
          <div class="opt-card">${letter}. ${escapeHtml(q['option' + letter])}</div>
        </label>
      `).join('')}
    </div>
  `).join('');
}

async function submitQuiz() {
  const answers = [];

  for (const q of currentQuiz.questions) {
    const selected = document.querySelector('input[name="q-' + q.questionId + '"]:checked');
    if (!selected) {
      ilToast('Please answer every question before submitting', true);
      return;
    }
    answers.push({ questionId: q.questionId, selectedAnswer: selected.value });
  }

  const user = ilGetUser();
  const submitBtn = document.getElementById('submitQuizBtn');
  submitBtn.disabled = true;
  submitBtn.textContent = 'Submitting…';

  const res = await ilAuthFetch('/quiz-attempt/submit', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      studentId: user.id, // the server ignores this and uses the authenticated user, but the field is required by validation
      quizId: currentQuiz.quizId,
      answers: answers
    })
  });

  submitBtn.disabled = false;
  submitBtn.textContent = 'Submit quiz';

  if (!res) return;

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    ilToast(err.message || 'Could not submit quiz', true);
    return;
  }

  const result = await res.json();
  lastDashboardData = null; // stale now that a new attempt exists — force a refresh next time it's viewed
  renderResult(result);
  showView('result');
}

function renderResult(result) {
  document.getElementById('resultScore').textContent = result.score + ' / ' + result.totalQuestions;
  document.getElementById('resultSub').textContent =
    formatPercent(result.percentage) + ' · ' + (result.message || '');

  const container = document.getElementById('resultReview');
  container.innerHTML = (result.answers || []).map(a => {
    const question = currentQuiz.questions.find(q => q.questionId === a.questionId);
    const label = question ? question.question : ('Question ' + a.questionId);
    return `
      <div class="review-row ${a.isCorrect ? 'correct' : 'incorrect'}">
        <p class="review-q">${escapeHtml(label)}</p>
        <p class="review-meta">
          Your answer: ${escapeHtml(a.selectedAnswer)}
          ${a.isCorrect ? '— correct' : '— correct answer: ' + escapeHtml(a.correctAnswer)}
        </p>
      </div>
    `;
  }).join('');
}



function showView(viewName) {
  document.querySelectorAll('.sd-view').forEach(v => v.style.display = 'none');
  document.getElementById('view-' + viewName).style.display = 'block';

  document.querySelectorAll('.sidebar-link[data-view]').forEach(link => {
    link.classList.toggle('active', link.dataset.view === viewName);
  });

  if (viewName === 'dashboard') loadDashboardSummary();
  else if (viewName === 'subjects') loadSubjects();
  else if (viewName === 'mynotes') loadMyNotes();
  else if (viewName === 'quizzes') loadQuizHistory();
  else if (viewName === 'progress') loadProgressStats();
}

function backToSubjects() {
  currentQuiz = null;
  showView('subjects');
}



document.addEventListener('DOMContentLoaded', () => {
  const user = ilGetUser();
  if (!user || !ilGetToken() || user.role !== 'STUDENT') {
    window.location.href = '/login';
    return;
  }

  document.getElementById('userNameInline').textContent = user.firstName + ' ' + user.lastName;
  document.getElementById('profileName').textContent = user.firstName + ' ' + user.lastName;
  document.getElementById('profileEmail').textContent = user.email;
  document.getElementById('profileRole').textContent = user.role;

  loadDashboardSummary();
});
