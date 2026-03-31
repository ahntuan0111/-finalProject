package vn.anhtuan.finalProject.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import vn.anhtuan.finalProject.entity.ExerciseSubmission;
import vn.anhtuan.finalProject.repository.ExerciseSubmissionRepository;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminSubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExerciseSubmissionRepository submissionRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllSubmissions() throws Exception {
        ExerciseSubmission submission = new ExerciseSubmission();
        submission.setId(1L);
        submission.setImageUrl("http://image.com");

        when(submissionRepository.findAll()).thenReturn(Collections.singletonList(submission));

        mockMvc.perform(get("/api/admin/submissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].imageUrl").value("http://image.com"));
    }
}
