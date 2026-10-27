package com.rthiam.insurance.service;

/**
 * Thrown when a contract number is already used by another contract.
 */
public class NumeroContratDejaUtiliseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NumeroContratDejaUtiliseException(String numero) {
		super("Numero " + numero + " is already used by another contrat");
	}

}
