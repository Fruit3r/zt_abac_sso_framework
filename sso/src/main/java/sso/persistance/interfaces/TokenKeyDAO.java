package sso.persistance.interfaces;

import sso.utils.exceptions.PersistenceException;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface TokenKeyDAO {

    /**
     * Returns the current private key for token signing
     *
     * @return Current private key for token signing
     */
    public PrivateKey getPrivateKey();

    /**
     * Return the current public key for token signing
     *
     * @return Current public key for token signing
     */
    public PublicKey getPublicKey();

    /**
     * Overwrites the old key pair with a new one
     *
     * @throws PersistenceException if an exception occured during key persisting
     */
    public void generateNewKeyPair() throws PersistenceException;
}
