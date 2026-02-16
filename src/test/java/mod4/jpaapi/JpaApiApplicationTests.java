//package mod4.jpaapi;
//
//import mod4.jpaapi.controllers.UsersController;
//import mod4.jpaapi.models.Name;
//import mod4.jpaapi.models.User;
//import mod4.jpaapi.repositories.UsersRepository;
//import mod4.jpaapi.services.UsersService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import tools.jackson.databind.ObjectMapper;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//class JpaApiApplicationTests {
//
//	@Autowired
//	private MockMvc mockMvc;
//
//	@Mock
//	private UsersRepository usersRepository;
//
//	@Mock
//	private UsersService usersService;
//
//	@InjectMocks
//	private UsersController usersController;
//
//	private ObjectMapper objectMapper;
//
//	@BeforeEach
//	public void setUp() {
//		MockitoAnnotations.openMocks(this);
//		mockMvc = MockMvcBuilders.standaloneSetup(usersController).build();
//		objectMapper = new ObjectMapper();
//	}
//
//	@Test
//	public void testCreateUser_Success() throws Exception {
//		Name name = new Name("Petrov Vasiliy");
//		User user = new User();
//		user.setName(name);
//		user.setEmail("flue@go.org");
//		user.setBirthday(LocalDate.of(1995, 11, 12));
//		user.setCreated(LocalDateTime.now());
//		user.setUpdated(LocalDateTime.now());
//
//		when(usersRepository.save(any(User.class))).thenReturn(user);
//
//		mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
//				.content(objectMapper.writeValueAsString(user)))
//				.andExpect(status().isCreated())
//				.andExpect(jsonPath("$.name")
//						.value("Petrov Vasiliy"))
//				.andExpect(jsonPath("$.email")
//						.value("flue@go.org"))
//				.andExpect(jsonPath("$.birthday")
//				.value("1995-11-12"));
//
//		verify(usersRepository, times(1)).save(any(User.class));
//	}
//
//}
