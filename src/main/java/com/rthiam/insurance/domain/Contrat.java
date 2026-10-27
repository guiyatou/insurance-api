package com.rthiam.insurance.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Insurance contract.
 */
@Entity
@Table(name = "contrat")
public class Contrat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 30)
	@Column(name = "numero", nullable = false, unique = true, length = 30)
	private String numero;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "type_contrat", nullable = false, length = 20)
	private TypeContrat typeContrat;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "statut", nullable = false, length = 20)
	private StatutContrat statut = StatutContrat.ACTIF;

	@NotNull
	@Column(name = "date_debut", nullable = false)
	private LocalDate dateDebut;

	/** Null means the contract has no planned end date. */
	@Column(name = "date_fin")
	private LocalDate dateFin;

	@NotNull
	@Positive
	@Digits(integer = 10, fraction = 2)
	@Column(name = "prime_annuelle", nullable = false, precision = 12, scale = 2)
	private BigDecimal primeAnnuelle;

	/** Required by JPA. */
	protected Contrat() {
	}

	public Contrat(String numero, TypeContrat typeContrat, LocalDate dateDebut, LocalDate dateFin,
			BigDecimal primeAnnuelle) {
		this.numero = numero;
		this.typeContrat = typeContrat;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
		this.primeAnnuelle = primeAnnuelle;
	}

	/** The end date, when present, must not be before the start date. */
	@AssertTrue(message = "dateFin must not be before dateDebut")
	public boolean isPeriodeValide() {
		return dateDebut == null || dateFin == null || !dateFin.isBefore(dateDebut);
	}

	public Long getId() {
		return id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public TypeContrat getTypeContrat() {
		return typeContrat;
	}

	public void setTypeContrat(TypeContrat typeContrat) {
		this.typeContrat = typeContrat;
	}

	public StatutContrat getStatut() {
		return statut;
	}

	public void setStatut(StatutContrat statut) {
		this.statut = statut;
	}

	public LocalDate getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(LocalDate dateDebut) {
		this.dateDebut = dateDebut;
	}

	public LocalDate getDateFin() {
		return dateFin;
	}

	public void setDateFin(LocalDate dateFin) {
		this.dateFin = dateFin;
	}

	public BigDecimal getPrimeAnnuelle() {
		return primeAnnuelle;
	}

	public void setPrimeAnnuelle(BigDecimal primeAnnuelle) {
		this.primeAnnuelle = primeAnnuelle;
	}

	/** Identity is based on the database id only (null before persist). */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Contrat)) {
			return false;
		}
		Contrat other = (Contrat) o;
		return id != null && id.equals(other.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public String toString() {
		return "Contrat{id=" + id + ", numero='" + numero + "', typeContrat=" + typeContrat + ", statut=" + statut
				+ "}";
	}

}
