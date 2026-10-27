package com.rthiam.insurance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rthiam.insurance.domain.Contrat;
import com.rthiam.insurance.domain.StatutContrat;

/**
 * Data access for {@link Contrat}.
 */
public interface ContratRepository extends JpaRepository<Contrat, Long> {

	Optional<Contrat> findByNumero(String numero);

	boolean existsByNumero(String numero);

	boolean existsByNumeroAndIdNot(String numero, Long id);

	List<Contrat> findByStatut(StatutContrat statut);

}
