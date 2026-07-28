// Student dashboard logic. Relies on auth.js and common.js (ilAuthFetch,
// ilToast, escapeHtml) having already been loaded on the page.

let currentQuiz = null; // { quizId, title, questions: [...] }

// ---------- Progress ----------

async function loadProgress() {
  const user = ilGetUser();
  const res = await ilAuthFetch('/dashboard/student/' + user.id, { method: 'GET' });
  if (!res) return;

  if (!res.ok) {
    document.getElementById('statsRow').innerHTML =
      '<p class="empty-hint">No attempts yet — take a quiz to see your progress here.</p>';
    return;
  }

  const data = await res.json();
  renderStats(data);
  renderRecentAttempts(data.recentAttempts || []);
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

function renderRecentAttempts(attempts) {
  const container = document.getElementById('recentAttempts');

  if (!attempts.length) {
    container.innerHTML = '<p class="empty-hint">No attempts yet.</p>';
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

// ---------- Browse subjects ----------

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

// ---------- Taking a quiz ----------

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

// ---------- View switching ----------

function showView(viewName) {
  document.querySelectorAll('.sd-view').forEach(v => v.style.display = 'none');
  document.getElementById('view-' + viewName).style.display = 'block';
}

function backToBrowse() {
  currentQuiz = null;
  showView('browse');
  loadProgress();
}

// ---------- Init ----------

document.addEventListener('DOMContentLoaded', () => {
  const user = ilGetUser();
  if (!user || !ilGetToken() || user.role !== 'STUDENT') {
    window.location.href = '/login';
    return;
  }

  document.getElementById('userName').textContent = user.firstName + ' ' + user.lastName;
  loadProgress();
  loadSubjects();
});