package com.rthiam.insurance.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.rthiam.insurance.domain.Contrat;
import com.rthiam.insurance.domain.StatutContrat;
import com.rthiam.insurance.domain.TypeContrat;

/**
 * Contract representation returned by the API.
 */
public record ContratResponse(
		Long id,
		String numero,
		TypeContrat typeContrat,
		StatutContrat statut,
		LocalDate dateDebut,
		LocalDate dateFin,
		BigDecimal primeAnnuelle) {

	public static ContratResponse from(Contrat contrat) {
		return new ContratResponse(contrat.getId(), contrat.getNumero(), contrat.getTypeContrat(),
				contrat.getStatut(), contrat.getDateDebut(), contrat.getDateFin(), contrat.getPrimeAnnuelle());
	}

}
