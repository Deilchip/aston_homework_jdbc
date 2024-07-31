package creator;

import lombok.experimental.UtilityClass;
import org.example.dto.CoordinatorDTO;
import org.example.dto.CourseDTO;
import org.example.dto.StudentDTO;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CreatorObject {
    public CoordinatorDTO createCoordinatorForTest(String name) {
        return CoordinatorDTO.builder()
                .name(name)
                .build();
    }

    public StudentDTO createStudentForTest(String name, long id) {
        return StudentDTO.builder()
                .name(name)
                .coordinator(CoordinatorDTO.builder().id(id).build())
                .build();
    }

    public StudentDTO createStudentWithCourseIdForTest(String name, long id) {
        List<CourseDTO> courseDTOS = new ArrayList<>();
        courseDTOS.add(CourseDTO.builder().id(id + 1).build());
        return StudentDTO.builder()
                .name(name)
                .coordinator(CoordinatorDTO.builder().build())
                .courses(courseDTOS)
                .build();
    }

    public static CourseDTO createCourseForTest(String name) {
        return CourseDTO.builder()
                .name(name)
                .build();
    }
}
