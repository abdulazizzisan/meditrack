package dev.zisan.meditrack;

import dev.zisan.meditrack.search.repository.DoctorSearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class MeditrackApplicationTests {

	@MockitoBean
	private DoctorSearchRepository doctorSearchRepository;

	@Test
	void contextLoads() {
	}

}
