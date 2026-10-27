package com.rthiam.insurance.service;

/**
 * Thrown when no contract exists for the given id.
 */
public class ContratNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ContratNotFoundException(Long id) {
		super("Contrat " + id + " not found");
	}

}
