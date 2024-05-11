package sso.persistance.implementations;

import org.springframework.stereotype.Repository;
import sso.persistance.interfaces.TokenKeyDAO;
import sso.utils.exceptions.PersistenceException;

import java.io.*;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Repository
public class TokenKeyDAOFile implements TokenKeyDAO {

    private static final String ALGORITHM = "RSA";
    private static final String PRIVATE_KEY_PATH = "resources/keys/token-key.private";
    private static final String PUBLIC_KEY_PATH = "resources/keys/token-key.public";
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private File privateKeyFile;
    private File publicKeyFile;

    public TokenKeyDAOFile() {
        privateKeyFile = new File(Paths.get(PRIVATE_KEY_PATH).toAbsolutePath().normalize().toString());
        publicKeyFile = new File(Paths.get(PUBLIC_KEY_PATH).toAbsolutePath().normalize().toString());
        this.createTokenKeyFilesIfNotExist();

        try {
            this.loadKeys();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            this.generateNewKeyPair();
        } catch (IOException e) {
            throw new PersistenceException("TokenKeyDAOFile # MSG: IOException occured # REASON: " + e.getMessage());
        }
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public void generateNewKeyPair() throws PersistenceException {
        KeyPairGenerator keyPairGenerator;
        KeyPair keyPair;

        // Generate new keys
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
            // Write to files
            try {
                this.storeKeys();
            } catch (IOException e) {
                throw new PersistenceException("TokenKeyDAOFile # MSG: IOException occured # REASON: " + e.getMessage());
            }
        } catch (NoSuchAlgorithmException e) {}
    }
    private void storeKeys() throws IOException {
        // Store private key
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        FileOutputStream privateKeyFileOS = new FileOutputStream(this.privateKeyFile);
        privateKeyFileOS.write(pkcs8EncodedKeySpec.getEncoded());
        privateKeyFileOS.close();

        // Store public key
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        FileOutputStream publicKeyFileOS = new FileOutputStream(this.publicKeyFile);
        publicKeyFileOS.write(x509EncodedKeySpec.getEncoded());
        publicKeyFileOS.close();
    }

    private void loadKeys() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

        // Load private key
        FileInputStream privateKeyFileIS = new FileInputStream(this.privateKeyFile);
        byte[] encodedPrivateKey = new byte[(int) this.privateKeyFile.length()];
        privateKeyFileIS.read(encodedPrivateKey);
        privateKeyFileIS.close();
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        this.privateKey = keyFactory.generatePrivate(privateKeySpec);

        // Load public key
        FileInputStream publicKeyFileIS = new FileInputStream(this.publicKeyFile);
        byte[] encodedPublicKey = new byte[(int) this.publicKeyFile.length()];
        publicKeyFileIS.read(encodedPublicKey);
        publicKeyFileIS.close();
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        this.publicKey = keyFactory.generatePublic(publicKeySpec);
    }

    private void createTokenKeyFilesIfNotExist() {
        if (!privateKeyFile.exists() || !publicKeyFile.exists()) {
            try {
                privateKeyFile.getParentFile().mkdirs();
                privateKeyFile.createNewFile();
                publicKeyFile.getParentFile().mkdirs();
                publicKeyFile.createNewFile();
            } catch (IOException e) {
                throw new PersistenceException("TokenKeyDAOFile # MSG: could not create token key files # REASON: " + e.getMessage());
            }
        }
    }
}
