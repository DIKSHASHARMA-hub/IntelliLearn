package com.intellilearn.service.impl;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import com.intellilearn.dto.response.QuestionDTO;
import com.intellilearn.dto.response.QuizResponseDTO;
import com.intellilearn.entity.Notes;
import com.intellilearn.entity.Question;
import com.intellilearn.entity.Quiz;
import com.intellilearn.entity.Subject;
import com.intellilearn.exception.NoteNotFoundException;
import com.intellilearn.exception.QuizGenerationException;
import com.intellilearn.exception.SubjectNotFoundException;
import com.intellilearn.repository.NotesRepository;
import com.intellilearn.repository.QuestionRepository;
import com.intellilearn.repository.QuizRepository;
import com.intellilearn.repository.SubjectRepository;
import com.intellilearn.service.interfaces.QuizService;
import com.intellilearn.util.AiService;
import com.intellilearn.util.PdfReaderUtil;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class QuizServiceImpl implements QuizService {

    private final SubjectRepository subjectRepository;

    private final NotesRepository notesRepository;

    private final QuizRepository quizRepository;

    private final QuestionRepository questionRepository;

    private final PdfReaderUtil pdfReaderUtil;

    private final AiService aiService;

    private final ObjectMapper objectMapper;

    public QuizServiceImpl(
            SubjectRepository subjectRepository,
            NotesRepository notesRepository,
            QuizRepository quizRepository,
            QuestionRepository questionRepository,
            PdfReaderUtil pdfReaderUtil,
            AiService aiService) {

        this.subjectRepository = subjectRepository;
        this.notesRepository = notesRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.pdfReaderUtil = pdfReaderUtil;
        this.aiService = aiService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public QuizResponseDTO generateQuizForNote(Long noteId) {

        Notes notes = notesRepository.findById(noteId)
                .orElseThrow(() ->
                        new NoteNotFoundException("Notes not found."));

        String pdfText = pdfReaderUtil.extractText(notes.getFilePath());

        String aiResponse = aiService.generateQuiz(pdfText);

        Quiz quiz = new Quiz();

        quiz.setTitle(notes.getTitle() + " Quiz");
        quiz.setSubject(notes.getSubject());
        quiz.setNotes(notes);

        quiz = quizRepository.save(quiz);

        List<QuestionDTO> questionDTOList = parseQuestionsIntoQuiz(aiResponse, quiz);

        return new QuizResponseDTO(
                quiz.getId(),
                quiz.getTitle(),
                questionDTOList
        );
    }

    private List<QuestionDTO> parseQuestionsIntoQuiz(String aiResponse, Quiz quiz) {

        List<QuestionDTO> questionDTOList = new ArrayList<>();

        try {

            JsonNode rootNode = objectMapper.readTree(aiResponse);

            String generatedJson = rootNode
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            generatedJson = generatedJson
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            JsonNode questionsNode = objectMapper.readTree(generatedJson);

            for (JsonNode node : questionsNode) {

                Question question = new Question();

                question.setQuestion(node.get("question").asText());
                question.setOptionA(node.get("optionA").asText());
                question.setOptionB(node.get("optionB").asText());
                question.setOptionC(node.get("optionC").asText());
                question.setOptionD(node.get("optionD").asText());
                question.setCorrectAnswer(node.get("correctAnswer").asText());
                question.setQuiz(quiz);

                question = questionRepository.save(question);

                QuestionDTO dto = new QuestionDTO();

                dto.setQuestionId(question.getId());
                dto.setQuestion(question.getQuestion());
                dto.setOptionA(question.getOptionA());
                dto.setOptionB(question.getOptionB());
                dto.setOptionC(question.getOptionC());
                dto.setOptionD(question.getOptionD());

                questionDTOList.add(dto);
            }

        } catch (Exception e) {
            throw new QuizGenerationException("Error while parsing the AI-generated quiz response.", e);
        }

        return questionDTOList;
    }

    @Override
    public QuizResponseDTO getQuiz(Long quizId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() ->
                        new jakarta.persistence.EntityNotFoundException("Quiz not found."));

        List<Question> questions = questionRepository.findByQuiz(quiz);

        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {

            QuestionDTO dto = new QuestionDTO();

            dto.setQuestionId(question.getId());
            dto.setQuestion(question.getQuestion());
            dto.setOptionA(question.getOptionA());
            dto.setOptionB(question.getOptionB());
            dto.setOptionC(question.getOptionC());
            dto.setOptionD(question.getOptionD());

            questionDTOList.add(dto);
        }

        return new QuizResponseDTO(
                quiz.getId(),
                quiz.getTitle(),
                questionDTOList
        );
    }

    @Override
    public QuizResponseDTO getQuizBySubject(Long subjectId) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() ->
                        new SubjectNotFoundException("Subject not found."));

        List<Quiz> quizzes = quizRepository.findBySubject(subject);

        if (quizzes.isEmpty()) {
            throw new jakarta.persistence.EntityNotFoundException(
                    "No quiz has been generated for this subject yet.");
        }

        Quiz latestQuiz = quizzes.stream()
                .max((a, b) -> Long.compare(a.getId(), b.getId()))
                .orElseThrow();

        return getQuiz(latestQuiz.getId());
    }

    @Override
    public QuizResponseDTO getQuizByNote(Long noteId) {

        if (!notesRepository.existsById(noteId)) {
            throw new NoteNotFoundException("Notes not found.");
        }

        List<Quiz> quizzes = quizRepository.findByNotesId(noteId);

        if (quizzes.isEmpty()) {
            throw new jakarta.persistence.EntityNotFoundException(
                    "No quiz has been generated for these notes yet.");
        }

        Quiz latestQuiz = quizzes.stream()
                .max((a, b) -> Long.compare(a.getId(), b.getId()))
                .orElseThrow();

        return getQuiz(latestQuiz.getId());
    }
}
