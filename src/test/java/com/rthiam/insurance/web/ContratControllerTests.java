package com.rthiam.insurance.web;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.rthiam.insurance.domain.Contrat;
import com.rthiam.insurance.domain.StatutContrat;
import com.rthiam.insurance.domain.TypeContrat;
import com.rthiam.insurance.repository.ContratRepository;

/**
 * End-to-end tests of the /api/contrats endpoints, with the real service and H2 database.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ContratControllerTests {

	private static final String VALID_BODY = """
			{
			  "numero": "CTR-0001",
			  "typeContrat": "AUTO",
			  "dateDebut": "2026-01-01",
			  "dateFin": "2026-12-31",
			  "primeAnnuelle": 450.00
			}
			""";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ContratRepository repository;

	@BeforeEach
	void cleanDatabase() {
		repository.deleteAll();
	}

	private Contrat saveContrat(String numero, StatutContrat statut) {
		Contrat contrat = new Contrat(numero, TypeContrat.HABITATION, LocalDate.of(2026, 1, 1), null,
				new BigDecimal("300.00"));
		contrat.setStatut(statut);
		return repository.save(contrat);
	}

	@Test
	void createReturns201WithLocation() throws Exception {
		mockMvc.perform(post("/api/contrats").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", endsWith("/api/contrats/" + idOf("CTR-0001"))))
				.andExpect(jsonPath("$.numero").value("CTR-0001"))
				.andExpect(jsonPath("$.statut").value("ACTIF"))
				.andExpect(jsonPath("$.dateDebut").value("2026-01-01"))
				.andExpect(jsonPath("$.primeAnnuelle").value(450.0));
	}

	@Test
	void createWithInvalidBodyReturns400WithFieldErrors() throws Exception {
		String body = """
				{
				  "numero": "",
				  "dateDebut": "2026-06-01",
				  "dateFin": "2026-01-01",
				  "primeAnnuelle": -10
				}
				""";

		mockMvc.perform(post("/api/contrats").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.numero").exists())
				.andExpect(jsonPath("$.errors.typeContrat").exists())
				.andExpect(jsonPath("$.errors.primeAnnuelle").exists())
				.andExpect(jsonPath("$.errors.periodeValide").exists());
	}

	@Test
	void createWithDuplicateNumeroReturns409() throws Exception {
		saveContrat("CTR-0001", StatutContrat.ACTIF);

		mockMvc.perform(post("/api/contrats").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
				.andExpect(status().isConflict());
	}

	@Test
	void findAllCanFilterByStatut() throws Exception {
		saveContrat("CTR-0001", StatutContrat.ACTIF);
		saveContrat("CTR-0002", StatutContrat.RESILIE);

		mockMvc.perform(get("/api/contrats"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));

		mockMvc.perform(get("/api/contrats").param("statut", "RESILIE"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].numero").value("CTR-0002"));
	}

	@Test
	void findByIdReturns404WhenMissing() throws Exception {
		mockMvc.perform(get("/api/contrats/{id}", 999_999))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	void updateReplacesFieldsAndKeepsStatutWhenAbsent() throws Exception {
		Contrat existing = saveContrat("CTR-0001", StatutContrat.SUSPENDU);
		String body = """
				{
				  "numero": "CTR-0001-B",
				  "typeContrat": "VIE",
				  "dateDebut": "2026-02-01",
				  "primeAnnuelle": 99.90
				}
				""";

		mockMvc.perform(put("/api/contrats/{id}", existing.getId()).contentType(MediaType.APPLICATION_JSON)
				.content(body))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.numero").value("CTR-0001-B"))
				.andExpect(jsonPath("$.typeContrat").value("VIE"))
				.andExpect(jsonPath("$.statut").value("SUSPENDU"));
	}

	@Test
	void updateWithNumeroOfAnotherContratReturns409() throws Exception {
		saveContrat("CTR-0001", StatutContrat.ACTIF);
		Contrat other = saveContrat("CTR-0002", StatutContrat.ACTIF);

		mockMvc.perform(put("/api/contrats/{id}", other.getId()).contentType(MediaType.APPLICATION_JSON)
				.content(VALID_BODY))
				.andExpect(status().isConflict());
	}

	@Test
	void deleteReturns204ThenContratIsGone() throws Exception {
		Contrat existing = saveContrat("CTR-0001", StatutContrat.ACTIF);

		mockMvc.perform(delete("/api/contrats/{id}", existing.getId()))
				.andExpect(status().isNoContent());
		mockMvc.perform(get("/api/contrats/{id}", existing.getId()))
				.andExpect(status().isNotFound());
	}

	private Long idOf(String numero) {
		return repository.findByNumero(numero).map(Contrat::getId).orElse(-1L);
	}

}
