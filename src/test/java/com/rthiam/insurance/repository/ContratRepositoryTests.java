package com.rthiam.insurance.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.rthiam.insurance.domain.Contrat;
import com.rthiam.insurance.domain.StatutContrat;
import com.rthiam.insurance.domain.TypeContrat;

/**
 * Persistence tests for {@link ContratRepository}.
 */
@DataJpaTest
class ContratRepositoryTests {

	@Autowired
	private ContratRepository repository;

	private static Contrat contrat(String numero) {
		return new Contrat(numero, TypeContrat.AUTO, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
				new BigDecimal("450.00"));
	}

	@Test
	void saveAndFindByNumero() {
		repository.save(contrat("CTR-0001"));

		Contrat found = repository.findByNumero("CTR-0001").orElseThrow();

		assertThat(found.getId()).isNotNull();
		assertThat(found.getTypeContrat()).isEqualTo(TypeContrat.AUTO);
		assertThat(found.getStatut()).isEqualTo(StatutContrat.ACTIF);
		assertThat(found.getPrimeAnnuelle()).isEqualByComparingTo("450.00");
		assertThat(repository.existsByNumero("CTR-0001")).isTrue();
	}

	@Test
	void findByStatut() {
		Contrat resilie = contrat("CTR-0002");
		resilie.setStatut(StatutContrat.RESILIE);
		repository.save(contrat("CTR-0001"));
		repository.save(resilie);

		assertThat(repository.findByStatut(StatutContrat.RESILIE)).extracting(Contrat::getNumero)
				.containsExactly("CTR-0002");
	}

	@Test
	void numeroMustBeUnique() {
		repository.saveAndFlush(contrat("CTR-0001"));

		assertThatThrownBy(() -> repository.saveAndFlush(contrat("CTR-0001")))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

}
