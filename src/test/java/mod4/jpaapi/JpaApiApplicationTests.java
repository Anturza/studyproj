package mod4.jpaapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import mod4.jpaapi.dto.UserDTO;
import mod4.jpaapi.exceptionhandling.exceptions.NotValidUserInputException;
import mod4.jpaapi.exceptionhandling.exceptions.UserNotFoundException;
import mod4.jpaapi.models.Name;
import mod4.jpaapi.models.User;
import mod4.jpaapi.repositories.UsersRepository;
import mod4.jpaapi.services.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc()
class JpaApiApplicationTests {

    UUID testIdValid;
    UUID testIdInvalid;
    Name testNameValid;
    Name testNameInvalid;
    User testUserExists;
    User testUserCreating;
	String testEmailValid;
	String testEmailInvalid;
	LocalDate testBirthday;
	UserDTO testUserDTO;
	List<UserDTO> userTestList;

    @Autowired
    private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private UsersService usersService;

    @BeforeEach
    public void setUp() {
        testIdValid = UUID.fromString("2afae0af-0a24-47da-b1a0-219995da50e4");
		testIdInvalid = UUID.fromString("2afae0af-0a24-47da-b1a0-219995da50");
        testNameValid = new Name("Petrov Vasiliy");
        testNameInvalid = new Name("");
		testEmailValid = "flue@go.org";
		testEmailInvalid = "flue@goorg";
		testBirthday = LocalDate.of(1977, 3, 17);
        testUserExists = new User();
        testUserExists.setId(testIdValid);
        testUserExists.setName(testNameValid);
        testUserExists.setEmail(testEmailValid);
        testUserExists.setBirthday(LocalDate.of(1995, 11, 12));
        testUserExists.setCreated(LocalDateTime.now());
        testUserExists.setUpdated(LocalDateTime.now());
        testUserDTO = UsersService.mapToDTO(testUserExists);
    }

	@Test
	public void testGetAllUsersEndpoint_Success() throws Exception {
		User user1 = getTestUser("Petrovskiy Petr Petrovich", "petrovich@list.ru", LocalDate.of(1996, 12, 12));
		User user2 = getTestUser("Ivanov Ivan Ivanivich", "ivantheterrible@bk.ru", LocalDate.of(1986, 2, 22));
		User user3 = getTestUser("Sidorov Dmitriy Vladimirovich", "sidr@gmail.com", LocalDate.of(1975, 8, 7));
		userTestList = Stream.of(user1, user2, user3).map(UsersService::mapToDTO).toList();

		when(usersService.getAllUsers()).thenReturn(userTestList);

		mockMvc.perform((get("/api/users"))).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(user1.getId().toString()))
				.andExpect(jsonPath("$[1].id").value(user2.getId().toString()))
				.andExpect(jsonPath("$[2].id").value(user3.getId().toString()));

		verify(usersService, atLeastOnce()).getAllUsers();
	}

    @Test
    public void testGetUserByIdEndpoint_Success() throws Exception {

        when(usersService.getUser(testIdValid)).thenReturn(testUserDTO);

        MvcResult result = mockMvc.perform(get("/api/users/2afae0af-0a24-47da-b1a0-219995da50e4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		String jsonResponse = result.getResponse().getContentAsString();
		UserDTO actualUser = objectMapper.readValue(jsonResponse, UserDTO.class);

		assertNotNull(actualUser);
		assertEquals(testIdValid, actualUser.id());
		assertEquals(testNameValid, actualUser.name());
		assertEquals(testEmailValid, actualUser.email());

        verify(usersService, times(1)).getUser(testIdValid);
    }

	@Test
	public void testGetUserByIdEndpoint_Fail() throws Exception {

		when(usersService.getUser(testIdInvalid)).thenThrow(new UserNotFoundException("Not found"));

		mockMvc.perform(get("/api/users/2afae0af-0a24-47da-b1a0-219995da50")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(usersService, times(1)).getUser(testIdInvalid);
	}

	@Test
	public void testCreateUserEndpoint_Success() throws Exception {
		testUserCreating = new User();
		testUserCreating.setName(testNameValid);
		testUserCreating.setEmail(testEmailValid);
		testUserCreating.setBirthday(testBirthday);
		testUserCreating.setCreated(LocalDateTime.now());
		testUserCreating.setUpdated(LocalDateTime.now());

		when(usersService.createUser(testUserCreating)).thenReturn(ResponseEntity
				.created(getLocation(testUserCreating)).body(getCreatedUserDto(testUserCreating)) );

		mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testUserCreating)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(testUserCreating.getId()));

		verify(usersService, times(1)).createUser(testUserCreating);
	}

	@Test
	public void testCreateUserEndpoint_FailWrongNameInput() throws Exception {
		testUserCreating = new User();
		testUserCreating.setName(testNameInvalid);
		testUserCreating.setEmail(testEmailValid);
		testUserCreating.setBirthday(testBirthday);
		testUserCreating.setCreated(LocalDateTime.now());
		testUserCreating.setUpdated(LocalDateTime.now());

		when(usersService.createUser(testUserCreating)).thenThrow(new NotValidUserInputException("Not valid name provided"));

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(testUserCreating)))
				.andExpect(status().isBadRequest());

		verify(usersService, times(1)).createUser(testUserCreating);
	}

	@Test
	public void testCreateUserEndpoint_FailWrongEmailInput() throws Exception {
		testUserCreating = new User();
		testUserCreating.setName(testNameValid);
		testUserCreating.setEmail(testEmailInvalid);
		testUserCreating.setBirthday(testBirthday);
		testUserCreating.setCreated(LocalDateTime.now());
		testUserCreating.setUpdated(LocalDateTime.now());

		when(usersService.createUser(testUserCreating)).thenThrow(new NotValidUserInputException("Not valid email provided"));

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(testUserCreating)))
				.andExpect(status().isBadRequest());

		verify(usersService, times(1)).createUser(testUserCreating);
	}

	@Test
	public void testCreateUserEndpoint_FailWrongBirthdayInput() throws Exception {
		testUserCreating = new User();
		testUserCreating.setName(testNameValid);
		testUserCreating.setEmail(testEmailValid);
		testUserCreating.setBirthday(LocalDate.of(2028, 1, 5));
		testUserCreating.setCreated(LocalDateTime.now());
		testUserCreating.setUpdated(LocalDateTime.now());

		when(usersService.createUser(testUserCreating)).thenThrow(new NotValidUserInputException("Not valid birthday provided"));

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(testUserCreating)))
				.andExpect(status().isBadRequest());

		verify(usersService, times(1)).createUser(testUserCreating);
	}

	private static UserDTO getCreatedUserDto(User createdUser) {
        return UsersService.mapToDTO(createdUser);
	}

	private static URI getLocation(User createdUser) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(createdUser.getId()).toUri();
	}

	private static User getTestUser(String name, String email, LocalDate birthday) {
		User testUser = new User();
		testUser.setId(UUID.randomUUID());
		Name parsedName = Name.nameFromString(name);
		testUser.setName(parsedName);
		testUser.setEmail(email);
		testUser.setBirthday(birthday);
		testUser.setCreated(LocalDateTime.now());
		testUser.setUpdated(LocalDateTime.now());
		return testUser;
	}


}
