package com.rthiam.insurance.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.rthiam.insurance.domain.StatutContrat;
import com.rthiam.insurance.domain.TypeContrat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Payload used to create or update a contract.
 * When statut is null: ACTIF on creation, unchanged on update.
 */
public record ContratRequest(
		@NotBlank @Size(max = 30) String numero,
		@NotNull TypeContrat typeContrat,
		StatutContrat statut,
		@NotNull LocalDate dateDebut,
		LocalDate dateFin,
		@NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal primeAnnuelle) {

	/** The end date, when present, must not be before the start date. */
	@AssertTrue(message = "dateFin must not be before dateDebut")
	public boolean isPeriodeValide() {
		return dateDebut == null || dateFin == null || !dateFin.isBefore(dateDebut);
	}

}
