package com.rthiam.insurance.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Bean Validation rules of {@link Contrat}.
 */
class ContratValidationTests {

	private static ValidatorFactory factory;

	private static Validator validator;

	@BeforeAll
	static void setUp() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@AfterAll
	static void tearDown() {
		factory.close();
	}

	@Test
	void validContratHasNoViolation() {
		Contrat contrat = new Contrat("CTR-0001", TypeContrat.HABITATION, LocalDate.of(2026, 1, 1), null,
				new BigDecimal("300.00"));

		assertThat(validator.validate(contrat)).isEmpty();
	}

	@Test
	void dateFinBeforeDateDebutIsRejected() {
		Contrat contrat = new Contrat("CTR-0001", TypeContrat.AUTO, LocalDate.of(2026, 6, 1),
				LocalDate.of(2026, 1, 1), new BigDecimal("300.00"));

		Set<ConstraintViolation<Contrat>> violations = validator.validate(contrat);

		assertThat(violations).extracting(v -> v.getPropertyPath().toString()).containsExactly("periodeValide");
	}

	@Test
	void missingFieldsAreRejected() {
		Contrat contrat = new Contrat(" ", null, null, null, BigDecimal.ZERO);

		Set<ConstraintViolation<Contrat>> violations = validator.validate(contrat);

		assertThat(violations).extracting(v -> v.getPropertyPath().toString())
				.containsExactlyInAnyOrder("numero", "typeContrat", "dateDebut", "primeAnnuelle");
	}

}
