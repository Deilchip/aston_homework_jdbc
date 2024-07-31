package org.example.servlet;

import com.google.gson.Gson;
import org.example.dao.impl.StudentDAOImpl;
import org.example.dto.StudentDTO;
import org.example.service.StudentService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

/**
 * Сервлет, обрабатывающий запросы, связанные со студентами.
 */
@WebServlet(name = "StudentServlet", urlPatterns = {"/students", "/students/*"})
public class StudentServlet extends HttpServlet {
    private final StudentService studentService;
    private final Gson gson;

    /**
     * Конструктор по умолчанию, инициализирующий сервис и GSON-объект.
     */
    public StudentServlet() {
        this.studentService = new StudentService(new StudentDAOImpl());
        this.gson = new Gson();
    }

    /**
     * Конструктор, инициализирующий сервис и GSON-объект с использованием DataSource.
     *
     * @param source источник данных
     */
    public StudentServlet(DataSource source) {
        this.studentService = new StudentService(new StudentDAOImpl(source));
        this.gson = new Gson();
    }

    /**
     * Обрабатывает GET-запросы к сервлету.
     *
     * @param request  объект HttpServletRequest
     * @param response объект HttpServletResponse
     * @throws IOException если возникает ошибка ввода-вывода
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<StudentDTO> students = studentService.findAll();
            writeResponse(response, students);
        } else {
            String[] parts = pathInfo.split("/");
            if (parts.length < 3) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            Long courseId = Long.parseLong(parts[1]);
            if (parts[2].equals("students")) {
                List<StudentDTO> studentsByCourse = studentService.getStudentsByCourseId(courseId);
                writeResponse(response, studentsByCourse);
            } else {
                Long studentId = Long.parseLong(parts[2]);
                Optional<StudentDTO> student = studentService.findById(studentId);
                if (student.isPresent()) {
                    writeResponse(response, student.get());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        }
    }

    /**
     * Обрабатывает POST-запросы к сервлету.
     *
     * @param request  объект HttpServletRequest
     * @param response объект HttpServletResponse
     * @throws IOException если возникает ошибка ввода-вывода
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StudentDTO studentDTO = gson.fromJson(request.getReader(), StudentDTO.class);
        StudentDTO createdStudent = studentService.create(studentDTO);
        writeResponse(response, createdStudent);
    }

    /**
     * Обрабатывает PUT-запросы к сервлету.
     *
     * @param request  объект HttpServletRequest
     * @param response объект HttpServletResponse
     * @throws IOException если возникает ошибка ввода-вывода
     */
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String[] parts = pathInfo.split("/");
        if (parts.length == 2) {
            Long studentId = Long.parseLong(parts[1]);
            StudentDTO studentDTO = gson.fromJson(request.getReader(), StudentDTO.class);
            studentDTO.setId(studentId);
            StudentDTO updatedStudent = studentService.update(studentDTO);
            writeResponse(response, updatedStudent);
        }
    }

    /**
     * Обрабатывает DELETE-запросы к сервлету.
     *
     * @param request  объект HttpServletRequest
     * @param response объект HttpServletResponse
     */
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.matches("/\\d+")) {
            Long studentId = Long.parseLong(pathInfo.substring(1));
            boolean deleted = studentService.deleteById(studentId);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else if (pathInfo != null && pathInfo.matches("/\\d+/courses/\\d+")) {
            String[] parts = pathInfo.split("/");
            if (parts.length == 4) {
                Long studentId = Long.parseLong(parts[1]);
                Long courseId = Long.parseLong(parts[3]);
                studentService.removeCourseFromStudent(studentId, courseId);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Записывает ответ в HttpServletResponse в формате JSON.
     *
     * @param response объект HttpServletResponse
     * @param object   объект, который нужно записать в ответ
     * @throws IOException если возникает ошибка ввода-вывода
     */
    private void writeResponse(HttpServletResponse response, Object object) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(new Gson().toJson(object));
        writer.flush();
    }
}
