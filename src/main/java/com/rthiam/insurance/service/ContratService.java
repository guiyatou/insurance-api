package com.rthiam.insurance.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rthiam.insurance.domain.Contrat;
import com.rthiam.insurance.domain.StatutContrat;
import com.rthiam.insurance.repository.ContratRepository;
import com.rthiam.insurance.web.dto.ContratRequest;
import com.rthiam.insurance.web.dto.ContratResponse;

/**
 * Business operations on contracts.
 */
@Service
@Transactional(readOnly = true)
public class ContratService {

	private final ContratRepository repository;

	public ContratService(ContratRepository repository) {
		this.repository = repository;
	}

	/** Returns all contracts, or only those with the given status when it is not null. */
	public List<ContratResponse> findAll(StatutContrat statut) {
		List<Contrat> contrats = statut == null
				? repository.findAll(Sort.by("id"))
				: repository.findByStatut(statut);
		return contrats.stream().map(ContratResponse::from).toList();
	}

	public ContratResponse findById(Long id) {
		return ContratResponse.from(getContrat(id));
	}

	@Transactional
	public ContratResponse create(ContratRequest request) {
		if (repository.existsByNumero(request.numero())) {
			throw new NumeroContratDejaUtiliseException(request.numero());
		}
		Contrat contrat = new Contrat(request.numero(), request.typeContrat(), request.dateDebut(),
				request.dateFin(), request.primeAnnuelle());
		if (request.statut() != null) {
			contrat.setStatut(request.statut());
		}
		return ContratResponse.from(repository.save(contrat));
	}

	@Transactional
	public ContratResponse update(Long id, ContratRequest request) {
		Contrat contrat = getContrat(id);
		if (repository.existsByNumeroAndIdNot(request.numero(), id)) {
			throw new NumeroContratDejaUtiliseException(request.numero());
		}
		contrat.setNumero(request.numero());
		contrat.setTypeContrat(request.typeContrat());
		contrat.setDateDebut(request.dateDebut());
		contrat.setDateFin(request.dateFin());
		contrat.setPrimeAnnuelle(request.primeAnnuelle());
		if (request.statut() != null) {
			contrat.setStatut(request.statut());
		}
		return ContratResponse.from(repository.saveAndFlush(contrat));
	}

	@Transactional
	public void delete(Long id) {
		repository.delete(getContrat(id));
	}

	private Contrat getContrat(Long id) {
		return repository.findById(id).orElseThrow(() -> new ContratNotFoundException(id));
	}

}
