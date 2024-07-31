package org.example.servlet;

import com.google.gson.Gson;
import org.example.dao.impl.CoordinatorDAOImpl;
import org.example.dto.CoordinatorDTO;
import org.example.dto.StudentDTO;
import org.example.service.CoordinatorService;

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
 * Сервлет, обрабатывающий запросы, связанные с координаторами.
 */
@WebServlet(name = "CoordinatorServlet", urlPatterns = {"/coordinators", "/coordinators/*"})
public class CoordinatorServlet extends HttpServlet {
    private final CoordinatorService coordinatorService;
    private final Gson gson;

    /**
     * Конструктор по умолчанию, инициализирующий сервис и GSON-объект.
     */
    public CoordinatorServlet() {
        this.coordinatorService = new CoordinatorService(new CoordinatorDAOImpl());
        this.gson = new Gson();
    }

    /**
     * Конструктор, инициализирующий сервис и GSON-объект с использованием DataSource.
     *
     * @param source источник данных
     */
    public CoordinatorServlet(DataSource source) {
        this.coordinatorService = new CoordinatorService(new CoordinatorDAOImpl(source));
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
            List<CoordinatorDTO> coordinators = coordinatorService.findAll();
            writeResponse(response, coordinators);
        } else {
            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                Long coordinatorId = Long.parseLong(parts[1]);
                Optional<CoordinatorDTO> coordinator = coordinatorService.findById(coordinatorId);
                if (coordinator.isPresent()) {
                    writeResponse(response, coordinator.get());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else if (parts.length == 3 && parts[2].equals("students")) {
                Long coordinatorId = Long.parseLong(parts[1]);
                List<StudentDTO> students = coordinatorService.getStudentsForCoordinator(coordinatorId);
                writeResponse(response, students);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
        CoordinatorDTO coordinatorDTO = gson.fromJson(request.getReader(), CoordinatorDTO.class);
        CoordinatorDTO createdCoordinator = coordinatorService.create(coordinatorDTO);
        writeResponse(response, createdCoordinator);
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
            Long coordinatorId = Long.parseLong(parts[1]);
            CoordinatorDTO coordinatorDTO = gson.fromJson(request.getReader(), CoordinatorDTO.class);
            coordinatorDTO.setId(coordinatorId);
            CoordinatorDTO updatedCoordinator = coordinatorService.update(coordinatorDTO);
            writeResponse(response, updatedCoordinator);
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
            Long coordinatorId = Long.parseLong(pathInfo.substring(1));
            boolean deleted = coordinatorService.deleteById(coordinatorId);
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