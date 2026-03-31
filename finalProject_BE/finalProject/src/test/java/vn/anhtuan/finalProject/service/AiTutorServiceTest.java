package vn.anhtuan.finalProject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import vn.anhtuan.finalProject.dto.response.AiChatMessageResponse;
import vn.anhtuan.finalProject.entity.AiChatSession;
import vn.anhtuan.finalProject.entity.User;
import vn.anhtuan.finalProject.repository.AiChatMessageRepository;
import vn.anhtuan.finalProject.repository.AiChatSessionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiTutorServiceTest {

    @Mock
    private AiChatSessionRepository sessionRepository;

    @Mock
    private AiChatMessageRepository messageRepository;

    @Mock
    private IUserService userService;

    @Mock
    private RestClient geminiRestClient;

    @InjectMocks
    private AiTutorService aiTutorService;

    private User user;
    private AiChatSession session;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        session = new AiChatSession();
        session.setId(1L);
        session.setUser(user);
    }

    @Test
    void testGetHistory_Empty() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(sessionRepository.findTopByUserAndExerciseIdOrderByCreatedAtDesc(user, 0L)).thenReturn(Optional.empty());

        List<AiChatMessageResponse> result = aiTutorService.getHistory();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetHistory_WithMessages() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(sessionRepository.findTopByUserAndExerciseIdOrderByCreatedAtDesc(user, 0L)).thenReturn(Optional.of(session));
        when(messageRepository.findBySessionOrderByCreatedAtAsc(session)).thenReturn(new ArrayList<>());

        List<AiChatMessageResponse> result = aiTutorService.getHistory();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
