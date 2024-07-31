package org.example.servlet;


import com.google.gson.Gson;
import org.example.dao.impl.CourseDAOImpl;
import org.example.dto.CourseDTO;
import org.example.service.CourseService;

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
 * Сервлет, обрабатывающий запросы, связанные с курсами.
 */
@WebServlet(name = "CourseServlet", urlPatterns = {"/courses", "/courses/*"})
public class CourseServlet extends HttpServlet {
    private final CourseService courseService;
    private final Gson gson;

    /**
     * Конструктор по умолчанию, инициализирующий сервис и GSON-объект.
     */
    public CourseServlet() {
        this.courseService = new CourseService(new CourseDAOImpl());
        this.gson = new Gson();
    }

    /**
     * Конструктор, инициализирующий сервис и GSON-объект с использованием DataSource.
     *
     * @param source источник данных
     */
    public CourseServlet(DataSource source) {
        this.courseService = new CourseService(new CourseDAOImpl(source));
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
            List<CourseDTO> courses = courseService.findAll();
            writeResponse(response, courses);
        } else {
            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                Long courseId = Long.parseLong(parts[1]);
                Optional<CourseDTO> course = courseService.findById(courseId);
                if (course.isPresent()) {
                    writeResponse(response, course.get());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else if (parts.length == 3 && parts[2].equals("courses")) {
                Long studentId = Long.parseLong(parts[1]);
                List<CourseDTO> courses = courseService.findCoursesByStudentId(studentId);
                writeResponse(response, courses);
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
        CourseDTO courseDTO = gson.fromJson(request.getReader(), CourseDTO.class);
        CourseDTO createdCourse = courseService.create(courseDTO);
        writeResponse(response, createdCourse);
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
            Long courseId = Long.parseLong(parts[1]);
            CourseDTO courseDTO = gson.fromJson(request.getReader(), CourseDTO.class);
            courseDTO.setId(courseId);
            CourseDTO updatedCourse = courseService.update(courseDTO);
            writeResponse(response, updatedCourse);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
            Long courseId = Long.parseLong(pathInfo.substring(1));
            boolean deleted = courseService.deleteById(courseId);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
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
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(object));
        out.flush();
    }
}
