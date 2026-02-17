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
import org.junit.jupiter.api.DisplayName;
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

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc()
class JpaApiApplicationTests {

    UUID testIdValid;
    UUID testIdInvalid;
    Name testNameValid;
    Name testNameInvalid;
    User testUserExists;
    User testUserNew;
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
        testUserExists.setBirthday(testBirthday);
        testUserExists.setCreated(LocalDateTime.now());
        testUserExists.setUpdated(LocalDateTime.now());
        testUserDTO = UsersService.mapToDTO(testUserExists);
    }

	@Test
	@DisplayName("Get all users test")
	public void testGetAllUsersEndpoint() throws Exception {
		User user1 = getTestUserForList("Petrovskiy Petr Petrovich", "petrovich@list.ru",
				LocalDate.of(1996, 12, 12));
		User user2 = getTestUserForList("Ivanov Ivan Ivanivich", "ivantheterrible@bk.ru",
				LocalDate.of(1986, 2, 22));
		User user3 = getTestUserForList("Sidorov Dmitriy Vladimirovich", "sidr@gmail.com",
				LocalDate.of(1975, 8, 7));
		userTestList = Stream.of(user1, user2, user3).map(UsersService::mapToDTO).toList();

		when(usersService.getAllUsers()).thenReturn(userTestList);

		mockMvc.perform((get("/api/users"))).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(user1.getId().toString()))
				.andExpect(jsonPath("$[1].id").value(user2.getId().toString()))
				.andExpect(jsonPath("$[2].id").value(user3.getId().toString()));

		verify(usersService, atLeastOnce()).getAllUsers();
	}

    @Test
	@DisplayName("Get user by id test: case success")
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
	@DisplayName("Get user by id test: case fail")
	public void testGetUserByIdEndpoint_WrongId() throws Exception {
		when(usersService.getUser(testIdInvalid)).thenThrow(new UserNotFoundException("Not found"));

		mockMvc.perform(get("/api/users/2afae0af-0a24-47da-b1a0-219995da50")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(usersService, times(1)).getUser(testIdInvalid);
	}

	@Test
	@DisplayName("Create user test: case success")
	public void testCreateUserEndpoint_Success() throws Exception {
		testUserNew = getNewUser();

		when(usersService.createUser(testUserNew)).thenReturn(ResponseEntity
				.created(getLocation(testUserNew)).body(getCreatedUserDto(testUserNew)));

		mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testUserNew)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(testUserNew.getId()));

		verify(usersService, times(1)).createUser(testUserNew);
	}

	@Test
	@DisplayName("Create user test: case fail")
	public void testCreateUserEndpoint_WrongNameInput() throws Exception {
		testUserNew = getNewUser();
		testUserNew.setName(testNameInvalid);

		when(usersService.createUser(testUserNew)).thenThrow(new NotValidUserInputException("Not valid name provided"));

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(testUserNew)))
				.andExpect(status().isBadRequest());

		verify(usersService, times(1)).createUser(testUserNew);
	}

	@Test
	@DisplayName("Create user test: case fail")
	public void testCreateUserEndpoint_WrongEmailInput() throws Exception {
		testUserNew = getNewUser();
		testUserNew.setEmail(testEmailInvalid);

		when(usersService.createUser(testUserNew)).thenThrow(new NotValidUserInputException("Not valid email provided"));

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(testUserNew)))
				.andExpect(status().isBadRequest());

		verify(usersService, times(1)).createUser(testUserNew);
	}

	@Test
	@DisplayName("Create user test: case fail")
	public void testCreateUserEndpoint_WrongBirthdayInput() throws Exception {
		testUserNew = getNewUser();
		testUserNew.setBirthday(LocalDate.of(2028, 1, 5));

		when(usersService.createUser(testUserNew)).thenThrow(new NotValidUserInputException("Not valid birthday provided"));

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(testUserNew)))
				.andExpect(status().isBadRequest());

		verify(usersService, times(1)).createUser(testUserNew);
	}

	@Test
	@DisplayName("Update user test: case success")
	public void testUpdateUserEndpoint_Success() throws Exception {
		User updatedUser = getUpdatedUser();

		when(usersService.updateUser(eq(testIdValid), any(User.class)))
				.thenReturn(ResponseEntity.ok(getCreatedUserDto(updatedUser)));

		MvcResult result = mockMvc.perform(put("/api/users/2afae0af-0a24-47da-b1a0-219995da50e4")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updatedUser)))
				.andExpect(status().isOk()).andReturn();

		String jsonResponse = result.getResponse().getContentAsString();
		UserDTO actualUpdatedUser = objectMapper.readValue(jsonResponse, UserDTO.class);

		assertNotNull(actualUpdatedUser);
		assertEquals(updatedUser.getId(), actualUpdatedUser.id());
		assertEquals(updatedUser.getName(), actualUpdatedUser.name());
		assertEquals(updatedUser.getEmail(), actualUpdatedUser.email());
		assertEquals(updatedUser.getBirthday(), actualUpdatedUser.birthday());

		verify(usersService, times(1)).updateUser(testIdValid, updatedUser);
	}

	@Test
	@DisplayName("Update user test: case fail")
	public void testUpdateUserEndpoint_WrongNameProvided() throws Exception {
		User updatedUser = getUpdatedUser();
		updatedUser.setName(testNameInvalid);

		when(usersService.updateUser(eq(testIdValid), any(User.class)))
				.thenThrow(new NotValidUserInputException("Wrong name provided"));

		mockMvc.perform(put("/api/users/2afae0af-0a24-47da-b1a0-219995da50e4")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updatedUser)))
				.andExpect(status().isBadRequest());

		verify(usersService, times(1)).updateUser(testIdValid, updatedUser);
	}

	@Test
	@DisplayName("Update user test: case fail")
	public void testUpdateUserEndpoint_WrongEmailProvided() throws Exception {
		User updatedUser = getUpdatedUser();
		updatedUser.setEmail(testEmailInvalid);

		when(usersService.updateUser(eq(testIdValid), any(User.class)))
				.thenThrow(new NotValidUserInputException("Wrong email provided"));

		mockMvc.perform(put("/api/users/2afae0af-0a24-47da-b1a0-219995da50e4")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updatedUser)))
				.andExpect(status().isBadRequest());

		verify(usersService, times(1)).updateUser(testIdValid, updatedUser);
	}

	@Test
	@DisplayName("Update user test: case fail")
	public void testUpdateUserEndpoint_WrongBirthdayProvided() throws Exception {
		User updatedUser = getUpdatedUser();
		updatedUser.setBirthday(LocalDate.of(2028, 10, 23));

		when(usersService.updateUser(eq(testIdValid), any(User.class)))
				.thenThrow(new NotValidUserInputException("Wrong birthday provided"));

		mockMvc.perform(put("/api/users/2afae0af-0a24-47da-b1a0-219995da50e4")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updatedUser)))
				.andExpect(status().isBadRequest());

		verify(usersService, times(1)).updateUser(testIdValid, updatedUser);
	}

	@Test
	@DisplayName("Delete user test: case success")
	public void testDeleteUserById_Success() throws Exception {
		when(usersService.deleteUser(testIdValid)).thenReturn(ResponseEntity.ok().build());

		mockMvc.perform(delete("/api/users/2afae0af-0a24-47da-b1a0-219995da50e4"))
				.andExpect(status().isOk());

		verify(usersService, times(1)).deleteUser(testIdValid);
	}

	@Test
	@DisplayName("Delete user test: case fail")
	public void testDeleteUserById_WrongId() throws Exception {
		when(usersService.deleteUser(testIdInvalid)).thenReturn(ResponseEntity.notFound().build());

		mockMvc.perform(delete("/api/users/2afae0af-0a24-47da-b1a0-219995da50"))
				.andExpect(status().isNotFound());

		verify(usersService, times(1)).deleteUser(testIdInvalid);
	}

	private User getNewUser() {
		User user = new User();
		user.setName(testNameValid);
		user.setEmail(testEmailValid);
		user.setBirthday(testBirthday);
		user.setCreated(LocalDateTime.now());
		user.setUpdated(LocalDateTime.now());
		return user;
	}

	private User getUpdatedUser() {
		User updatedUser = new User();
		updatedUser.setId(testUserExists.getId());
		Name newName = Name.nameFromString("UpdatedSurname UpdatedName");
		updatedUser.setName(newName);
		updatedUser.setEmail("updated@email.su");
		updatedUser.setBirthday(LocalDate.of(1988, 10, 23));
		updatedUser.setCreated(testUserExists.getCreated());
		LocalDateTime lastUpdated = LocalDateTime.now();
		updatedUser.setUpdated(lastUpdated);
		return updatedUser;
	}

	private static UserDTO getCreatedUserDto(User createdUser) {
        return UsersService.mapToDTO(createdUser);
	}

	private static URI getLocation(User createdUser) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(createdUser.getId()).toUri();
	}

	private static User getTestUserForList (String name, String email, LocalDate birthday) {
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
